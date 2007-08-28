/*BEGIN_COPYRIGHT_BLOCK
 *
 * Copyright (c) 2001-2007, JavaPLT group at Rice University (javaplt@rice.edu)
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *    * Redistributions of source code must retain the above copyright
 *      notice, this list of conditions and the following disclaimer.
 *    * Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 *    * Neither the names of DrJava, the JavaPLT group, Rice University, nor the
 *      names of its contributors may be used to endorse or promote products
 *      derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * This software is Open Source Initiative approved Open Source Software.
 * Open Source Initative Approved is a trademark of the Open Source Initiative.
 * 
 * This file is part of DrJava.  Download the current version of this project
 * from http://www.drjava.org/ or http://sourceforge.net/projects/drjava/
 * 
 * END_COPYRIGHT_BLOCK*/

package edu.rice.cs.drjava.model;

import java.io.File;
import java.lang.ref.WeakReference;
import javax.swing.text.Position;

/**
 * Class for a document region that remains static all the time and does not respond to changes in the document.
 * @version $Id$Region
 */
public class StaticDocumentRegion implements DocumentRegion {
  protected final OpenDefinitionsDocument _doc;
  protected final File _file;
  protected final int _startOffset;
  protected final int _endOffset;
  protected final String _string;
  
  /** Create a new static document region. Precondition: s != null. */
  public StaticDocumentRegion(OpenDefinitionsDocument doc, File file, int so, int eo, String s) {
    assert s != null;
    _doc = doc;
    _file = file;
    _startOffset = so;
    _endOffset = eo;
    _string = s;
  }
  
  /** @return the document, or null if it hasn't been established yet */
  public OpenDefinitionsDocument getDocument() { return _doc; }

  /** @return the file */
  public File getFile() { return _file; }

  /** @return the start offset */
  public int getStartOffset() {
    return (_doc == null || _doc.getLength() >= _startOffset) ? _startOffset : _doc.getLength();
  }

  /** @return the end offset */
  public int getEndOffset() {
    return (_doc == null || _doc.getLength()>=_endOffset) ? _endOffset : _doc.getLength();
  }
  
  /** @return the string it was assigned */
  public String getString() {
    return _string;
  }
  
  private static boolean equals(OpenDefinitionsDocument doc1, OpenDefinitionsDocument doc2) {
    if (doc1 == null) return (doc2 == null);
    if (doc2 == null) return false;
    return doc1.equals(doc2);
  }
  
  private static boolean equals(File f1, File f2) {
    if (f1 == null) return (f2 == null);
    if (f2 == null) return false;
    return f1.equals(f2);
  }
  
  /** @return true if the specified region is equal to this one. */
  public boolean equals(Object other) {
    if (other == null || !(other instanceof StaticDocumentRegion)) return false;
    StaticDocumentRegion o = (StaticDocumentRegion) other;
    return (equals(_doc, o._doc) && equals(_file, o._file) && _startOffset == o._startOffset &&
            _endOffset == o._endOffset && _string.equals(o._string));
  }
  
  public String toString() {
    return (_doc != null ? _doc.toString() : "null") + " " + _startOffset + " .. " + _endOffset;
  }
}
