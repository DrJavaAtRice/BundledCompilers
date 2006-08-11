/*BEGIN_COPYRIGHT_BLOCK
 *
 * This file is part of DrJava.  Download the current version of this project from http://www.drjava.org/
 * or http://sourceforge.net/projects/drjava/
 *
 * DrJava Open Source License
 * 
 * Copyright (C) 2001-2005 JavaPLT group at Rice University (javaplt@rice.edu).  All rights reserved.
 *
 * Developed by:   Java Programming Languages Team, Rice University, http://www.cs.rice.edu/~javaplt/
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated 
 * documentation files (the "Software"), to deal with the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and 
 * to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 *     - Redistributions of source code must retain the above copyright notice, this list of conditions and the 
 *       following disclaimers.
 *     - Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the 
 *       following disclaimers in the documentation and/or other materials provided with the distribution.
 *     - Neither the names of DrJava, the JavaPLT, Rice University, nor the names of its contributors may be used to 
 *       endorse or promote products derived from this Software without specific prior written permission.
 *     - Products derived from this software may not be called "DrJava" nor use the term "DrJava" as part of their 
 *       names without prior written permission from the JavaPLT group.  For permission, write to javaplt@rice.edu.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO 
 * THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * CONTRIBUTORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF 
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS 
 * WITH THE SOFTWARE.
 * 
END_COPYRIGHT_BLOCK*/

package edu.rice.cs.drjava.model;

import java.awt.print.PageFormat;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;

import edu.rice.cs.drjava.model.compiler.CompilerModel;
import edu.rice.cs.drjava.model.debug.Debugger;
import edu.rice.cs.drjava.model.debug.Breakpoint;
import edu.rice.cs.drjava.model.definitions.DefinitionsEditorKit;
import edu.rice.cs.drjava.model.junit.JUnitModel;
import edu.rice.cs.drjava.model.repl.DefaultInteractionsModel;
import edu.rice.cs.drjava.model.repl.InteractionsDJDocument;
import edu.rice.cs.drjava.model.repl.InteractionsDocument;
import edu.rice.cs.drjava.model.repl.InteractionsScriptModel;
import edu.rice.cs.drjava.project.DocumentInfoGetter;
import edu.rice.cs.drjava.project.MalformedProjectFileException;
import edu.rice.cs.util.ClassPathVector;
import edu.rice.cs.util.FileOpenSelector;
import edu.rice.cs.util.OperationCanceledException;
import edu.rice.cs.util.docnavigation.IDocumentNavigator;
import edu.rice.cs.util.swing.DocumentIterator;
import edu.rice.cs.util.text.AbstractDocumentInterface;
import edu.rice.cs.util.text.ConsoleDocument;

/** Handles the bulk of DrJava's program logic.  The UI components interface with the GlobalModel through its 
 *  public methods, and GlobalModel responds via the GlobalModelListener interface.  This removes the dependency 
 *  on the UI for the logical flow of the program's features.  With the current implementation, we can finally test 
 *  the compile an unit testing functionality of DrJava, along with many other things.  An ongoing refactoring effort 
 *  will be moving many GlobalModel functions into more specific sub-interfaces for particular behaviors:
 * 
 *  @see DefaultGlobalModel
 *  @see ILoadDocuments
 *  @see CompilerModel
 *  @see JUnitModel
 *  @see JavadocModel
 *
 *  @version $Id$
 */
public interface GlobalModel extends ILoadDocuments {

  //-------------------------- Listener Management --------------------------//

  /** Add a listener to this global model.
   *  @param listener a listener that reacts on events generated by the GlobalModel
   */
  public void addListener(GlobalModelListener listener);

  /** Remove a listener from this global model.
   *  @param listener a listener that reacts on events generated by the GlobalModel
   */
  public void removeListener(GlobalModelListener listener);

  //------------------------ Feature Model Accessors ------------------------//

  /** Returns the interactions model. */
  public DefaultInteractionsModel getInteractionsModel();

  /** Gets the CompilerModel, which provides all methods relating to compilers. */
  public CompilerModel getCompilerModel();

  /** Gets the JUnitModel, which provides all methods relating to JUnit testing. */
  public JUnitModel getJUnitModel();

  /** Gets the JavadocModel, which provides all methods relating to Javadoc. */
  public JavadocModel getJavadocModel();

  /** Gets the Debugger, which interfaces with the integrated debugger. */
  public Debugger getDebugger();
  
  /** Gets the DocumentNavigator, which controls the document view. */
  public IDocumentNavigator<OpenDefinitionsDocument> getDocumentNavigator();
   
  public void setDocumentNavigator(IDocumentNavigator<OpenDefinitionsDocument> newnav);
  
  /** @return manager for breakpoint regions. */
  public RegionManager<Breakpoint> getBreakpointManager();
  
  /** @return manager for bookmark regions. */
  public RegionManager<DocumentRegion> getBookmarkManager();
  
  /** @return managers for find result regions. */
  public List<RegionManager<MovingDocumentRegion>> getFindResultsManagers();
  
  /** @return new manager for find result regions. */
  public RegionManager<MovingDocumentRegion> createFindResultsManager();
  
  /** Dispose a manager for find result regions. */
  public void disposeFindResultsManager(RegionManager<MovingDocumentRegion> rm);
  
  /** @return manager for browser history regions. */
  public RegionManager<DocumentRegion> getBrowserHistoryManager();

  /** Add the current location to the browser history. */
  public void addToBrowserHistory();
  
//  //---------------------------- Interpreter --------------------------------//
//  /** Updates the security manager in DrJava. */
//  public void enableSecurityManager();
//  
//  /** Updates the security manager in DrJava. */
//  public void disableSecurityManager();
//  
  //---------------------------- File Management ----------------------------//

  /** Creates a new document in the definitions pane and adds it to the list of open documents.
   *  @return The new open document
   */
  public OpenDefinitionsDocument newFile();

  /**
   * Creates a new junit test case.
   * TODO: Move to JUnitModel?
   * @param name the name of the new test case
   * @param makeSetUp true iff an empty setUp() method should be included
   * @param makeTearDown true iff an empty tearDown() method should be included
   * @return the new open test case
   */
  public OpenDefinitionsDocument newTestCase(String name, boolean makeSetUp, boolean makeTearDown);

  /**
   * Closes an open definitions document, prompting to save if
   * the document has been changed.  Returns whether the file
   * was successfully closed.
   * @return true if the document was closed
   */
  public boolean closeFile(OpenDefinitionsDocument doc);
  
  /**
   * Closes an open definitions document, without prompting to save if
   * the document has been changed.  Returns whether the file
   * was successfully closed.
   * @return true if the document was closed
   */
  public boolean closeFileWithoutPrompt(OpenDefinitionsDocument doc);

  /**
   * Attempts to close all open documents.
   * @return true if all documents were closed
   */
  public boolean closeAllFiles();

  /* Opens all files in specified folder.  If rec is true, open all files in the tree rooted at dir. */
  public void openFolder(File dir, boolean rec) throws IOException, OperationCanceledException, AlreadyOpenException;

  /** Saves all open documents, prompting when necessary. */
  public void saveAllFiles(FileSaveSelector com) throws IOException;
  
  /**Creates a new project with specified project file and default values for other properties.
   * @param projFile the new project file (which does not yet exist in the file system).
   */
  public void createNewProject(File projFile);
  
  /**Configures a new project (created by createNewProject) and saves it to disk. */
  public void configNewProject() throws IOException;
  
  /**Writes the project file to disk
   * @param f where to save the project
   * @param info Extra view-related information that should be included in the project file
   */
  public void saveProject(File f, Hashtable<OpenDefinitionsDocument,DocumentInfoGetter> info) throws IOException;
  
  /**Reloads a project without writing to disk.
   * @param f project file; does not actually get touched
   */
  public void reloadProject(File f, Hashtable<OpenDefinitionsDocument,DocumentInfoGetter> info) throws IOException;
  
  /** Formats a string pathname for use in the document navigator. */
  public String fixPathForNavigator(String path) throws IOException;

  /** Gives the title of the source bin for the navigator
   *  @return The text used for the source bin in the tree navigator
   */
  public String getSourceBinTitle();
  
  /** Gives the title of the external files bin for the navigator
   *  @return The text used for the external files bin in the tree navigator
   */
  public String getExternalBinTitle();
  
  /** Gives the title of the aux files bin for the navigator
   *  @return The text used for the aux files bin in the tree navigator
   */
  public String getAuxiliaryBinTitle();
  
  /** Adds a document to the list of auxiliary files. */
  public void addAuxiliaryFile(OpenDefinitionsDocument doc);
  
  /** Removes a document from the list of auxiliary files. */
  public void removeAuxiliaryFile(OpenDefinitionsDocument doc);
  
  /** Parses out the given project file, sets up the state and other configurations
   *  such as the Navigator and the classpath, and returns an array of files to open.
   *  @param file The project file to parse
   *  @return an array of document's files to open
   */
  public void openProject(File file) throws IOException, MalformedProjectFileException;

  /** Performs any needed operations on the model before closing the project and its files.  This is not responsible
   *  for actually closing the files since that is handled in MainFrame._closeProject()
   */
  public void closeProject(boolean qutting);
  
  /** Searches for a file with the given name on the current source roots and the augmented classpath.
   *  @param fileName Name of the source file to look for
   *  @return the file corresponding to the given name, or null if it cannot be found
   */
  public File getSourceFile(String fileName);

  /** Searches for a file with the given name on the provided paths. Returns null if the file is not found.
   *  @param fileName Name of the source file to look for
   *  @param paths An array of directories to search
   */
  public File getSourceFileFromPaths(String fileName, List<File> paths);

  /** Gets an array of all sourceRoots for the open definitions documents, without duplicates. */
  public File[] getSourceRootSet();

//  /** Return the absolute path of the file with the given index, or "(untitled)" if no file exists. */
//  public String getDisplayFullPath(int index);

  /*------------------------------ Definitions ------------------------------*/

  /** Fetches the {@link javax.swing.text.EditorKit} implementation for use in the definitions pane. */
  public DefinitionsEditorKit getEditorKit();

  /** Gets a DocumentIterator to allow navigating through open swing Documents.
   *  TODO: remove ugly swing dependency.
   */
  public DocumentIterator getDocumentIterator();

  /*---------------------------------- I/O ----------------------------------*/

  /** Gets the console document. */
  public ConsoleDocument getConsoleDocument();

  /** TODO: remove this swing dependency.
   *  @return InteractionsDJDocument in use by the ConsoleDocument.
   */
  public InteractionsDJDocument getSwingConsoleDocument();

  /** Resets the console. Fires consoleReset() event. */
  public void resetConsole();

  /** Prints System.out to the DrJava console. */
  public void systemOutPrint(String s);

  /** Prints System.err to the DrJava console. */
  public void systemErrPrint(String s);
  
  /** Prints the given string to the DrJava console as an echo of System.in */
  public void systemInEcho(String s);

  //----------------------------- Interactions -----------------------------//

  /** Gets the (toolkit-independent) interactions document. */
  public InteractionsDocument getInteractionsDocument();

  /** TODO: remove this swing dependency.
   *  @return InteractionsDJDocument in use by the InteractionsDocument.
   */
  public InteractionsDJDocument getSwingInteractionsDocument();

  /** Clears and resets the interactions pane in the specified working directory, provided that the operation has some effect. */
  public void resetInteractions(File wd);
  
  /** Clears and resets the interactions pane in the specified working directory. */
  public void resetInteractions(File wd, boolean forceReset);

  /** Blocks until the interpreter has registered. */
  public void waitForInterpreter();

  /** Interprets the current given text at the prompt in the interactions pane. */
  public void interpretCurrentInteraction();

  /** Returns the current classpath in use by the Interpreter JVM. This includes the original jvm classpath, the global
   *  drjava extra classpaths, and the project extra classpaths.
   */
  public ClassPathVector getClassPath();

  /** Returns only the project's extra classpaths.
   *  @return The classpath entries loaded along with the project
   */
  public ClassPathVector getExtraClassPath();
  
  /** Sets the set of classpath entries to use as the projects set of classpath entries.  This is normally used by the
   *  project preferences.
   */
  public void setExtraClassPath(ClassPathVector cp);
  
  // TODO: Move history methods to a more appropriate home.

  /** Interprets file selected in the FileOpenSelector. Assumes all strings have no trailing whitespace. Interprets 
   *  the list of interactions as a single transaction so the first error aborts all processing.
   */
  public void loadHistory(FileOpenSelector selector) throws IOException;

  /** Loads the history/histories from the given selector. */
  public InteractionsScriptModel loadHistoryAsScript(FileOpenSelector selector)
    throws IOException, OperationCanceledException;

  /** Clears the interactions history. */
  public void clearHistory();

  /** Saves the unedited version of the current history to a file
   *  @param selector File to save to
   */
  public void saveHistory(FileSaveSelector selector) throws IOException;

  /** Saves the edited version of the current history to a file
   *  @param selector File to save to
   *  @param editedVersion Edited verison of the history which will be saved to file instead of the lines saved in the 
   *  history. The saved file will still include any tags needed to recognize it as a saved interactions file.
   */
  public void saveHistory(FileSaveSelector selector, String editedVersion) throws IOException;

  /** Returns the entire history as a String with semicolons as needed. */
  public String getHistoryAsStringWithSemicolons();

  /** Returns the entire history as a String. */
  public String getHistoryAsString();

  //------------------------------- Debugger -------------------------------//

  /** Called when the debugger wants to print a message. */
  public void printDebugMessage(String s);

  /** Returns an available port number to use for debugging the interactions JVM.
   *  @throws IOException if unable to get a valid port number.
   */
  public int getDebugPort() throws IOException;

  //--------------------------------- Misc ---------------------------------//

  // TODO: comment
  public PageFormat getPageFormat();

  // TODO: comment
  public void setPageFormat(PageFormat format);

  /** Exits the program.  Only quits if all documents are successfully closed. */
  public void quit();
  
  /** Returns the document count */
  public int getDocumentCount();
  
  /** Returnt an OOD given an AbstractDocumentInterface */
  /**CHECK IF NEEDED! */
  public OpenDefinitionsDocument getODDForDocument(AbstractDocumentInterface doc);
  
  /** Returns a list of OpenDefinitionsDocuments that do not belong to the currently active project.<br>
   *  If no project is active, all documents are returned.
   */
  public List<OpenDefinitionsDocument> getNonProjectDocuments();

  /** Teturns a list of OpenDefinitionsDocuments that do belong to the currently active project.<br>
   *  If no project is active, no documents are returned.
   */
  public List<OpenDefinitionsDocument> getProjectDocuments();
  
//  /** Compiles all open files (all files in project (??) in project mode) */
//  public void compileAll() throws IOException;
  
  /** @return true if the model has a project open, false otherwise. */
  public boolean isProjectActive();
  
//  /** junits all the appropriate files */
//  public void junitAll();
  
  /** @return the file that points to the current project file. Null if not currently in project view */
  public File getProjectFile();
  
  /** @return the directory that the class files should be stored after compilation. */
  public File[] getProjectFiles();

  /** @return the source root for the project. */
  public File getProjectRoot();
  
  /** Sets project file to specifed value; used in "Save Project As ..." command in MainFrame. */
  public void setProjectFile(File f);
  
  /** Sets the source root for the project. */
  public void setProjectRoot(File f);

  /** @return the directory that the class files should be stored after compilation. */
  public File getBuildDirectory();
  
   /** Sets the current build directory. */
  public void setBuildDirectory(File f);
  
  /** @return the working directory for the Master JVM. */
  public File getMasterWorkingDirectory();
  
  /** @return the working directory for the Slave JVM (only applied to project mode). */
  public File getWorkingDirectory();
  
   /** Sets the working directory for the Slave JVM (only applies to project mode). */
  public void setWorkingDirectory(File f);

  /** Sets the main file of the project. */
  public void setMainClass(File f);
  
  /** Return the main file for the project If not in project mode, returns null. */
  public File getMainClass();

  /** Sets the create jar file of the project. */
  public void setCreateJarFile(File f);
  
  /** Return the create jar file for the project. If not in project mode, returns null. */
  public File getCreateJarFile();

  /** Sets the create jar flags of the project. */
  public void setCreateJarFlags(int f);
  
  /** Return the create jar file for the project. If not in project mode, returns 0. */
  public int getCreateJarFlags();
  
  /** Returns true the given file is in the current project file. */
  public boolean inProject(File f);
  
  /** A file is in the project if the source root is the same as the project root. This means that project files must
   *  be saved in the source root. (we query the model through the model's state)
   */
  public boolean inProjectPath(OpenDefinitionsDocument doc);
  
  /** Notifies the project state that the project has been changed. */
  public void setProjectChanged(boolean changed);
  
  /** Returns true if the project state has been changed */
  public boolean isProjectChanged();
  
  /** @return true iff no open document is out of sync with its primary class file. */
  public boolean hasOutOfSyncDocuments();
  
  /** Cleans the build directory. */
  public void cleanBuildDirectory();
  
  /** @return a list of class files. */
  public List<File> getClassFiles();
  
  /** Returns a collection of all documents currently open for editing.  This is equivalent to the results of 
   *  getDocumentForFile for the set of all files for which isAlreadyOpen returns true.  The order of documents 
   *  is the same as in the display of documents in the view.
   *  @return a List of the open definitions documents.
   */
  public List<OpenDefinitionsDocument> getOpenDefinitionsDocuments();
  
  /** Checks if any open definitions documents have been modified since last being saved.
   *  @return whether any documents have been modified
   */
   public boolean hasModifiedDocuments();
   
   /** Checks if any open definitions documents are untitled.
   *  @return whether any documents are untitled
   */
  public boolean hasUntitledDocuments();
  
   /** Returns the OpenDefinitionsDocument for the specified File, opening a new copy if one is not already open.
   *  @param file File contained by the document to be returned
   *  @return OpenDefinitionsDocument containing file
   */
   public OpenDefinitionsDocument getDocumentForFile(File file) throws IOException;
}
