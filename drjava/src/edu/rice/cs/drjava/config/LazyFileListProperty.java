/*BEGIN_COPYRIGHT_BLOCK
 *
 * Copyright (c) 2001-2008, JavaPLT group at Rice University (drjava@rice.edu)
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

package edu.rice.cs.drjava.config;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.util.FileOps;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.io.File;
import java.io.IOException;
import edu.rice.cs.util.StringOps;

/** Class representing file lists that are not evaluated until necessary.
  * @version $Id$
  */
public abstract class LazyFileListProperty extends DrJavaProperty {
  /** Separating string. */
  protected String _sep;
  /** Relative directory. */
  protected String _dir;
  /** Create an lazy file list property. */
  public LazyFileListProperty(String name, String sep, String dir, String help) {
    super(name, help);
    _sep = sep;
    _dir = dir;
    resetAttributes();
  }
  
  /** Mark the value as stale. */
  public void invalidate() {
    // nothing to do, but tell those who are listening
    invalidateOthers(new HashSet<DrJavaProperty>());
  }
  
  /** Return true if the value is current. */
  public boolean isCurrent() { return false; }

  /** Abstract factory method specifying the list. */
  protected abstract List<File> getList();
  
  /** Update the value by concatenating the list of documents. */
  public void update() {
    List<File> l = getList();
    if (l.size()==0) { _value = ""; return; }
    StringBuilder sb = new StringBuilder();
    for(File fil: l) {
      sb.append(StringOps.replaceVariables(_attributes.get("sep"), PropertyMaps.ONLY, PropertyMaps.GET_CURRENT));
      try {
        String f = fil.toString();
        if (_attributes.get("rel").equals("/")) f = fil.getAbsolutePath();
        else {
          File rf = new File(StringOps.
                               unescapeSpacesWith1bHex(StringOps.replaceVariables(_attributes.get("rel"), 
                                                                                  PropertyMaps.ONLY, 
                                                                                  PropertyMaps.GET_CURRENT)));
          f = FileOps.stringMakeRelativeTo(fil, rf);
        }
        String s = edu.rice.cs.util.StringOps.escapeSpacesWith1bHex(f);
        sb.append(s);
      }
      catch(IOException e) { /* ignore */ }
      catch(SecurityException e) { /* ignore */ }
    }
    _value = sb.toString();
    if (_value.startsWith(_attributes.get("sep"))) {
      _value= _value.substring(_attributes.get("sep").length());
    }
  }
  
  /** Reset the attributes. */
  public void resetAttributes() {
    _attributes.clear();
    _attributes.put("sep", _sep);
    _attributes.put("rel", _dir);
  }
}
