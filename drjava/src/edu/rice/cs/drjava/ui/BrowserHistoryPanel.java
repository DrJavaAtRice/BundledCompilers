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

package edu.rice.cs.drjava.ui;

import java.util.Vector;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import javax.swing.table.*;
import javax.swing.text.BadLocationException;
import java.awt.event.*;
import java.awt.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Position;

import edu.rice.cs.drjava.model.RegionManager;
import edu.rice.cs.drjava.model.RegionManagerListener;
import edu.rice.cs.drjava.model.DocumentRegion;
import edu.rice.cs.drjava.model.OpenDefinitionsDocument;
import edu.rice.cs.drjava.config.*;
import edu.rice.cs.util.swing.Utilities;
import edu.rice.cs.util.UnexpectedException;

/**
 * Panel for displaying browser history.
 * Currently not used because of synchronization problems.
 * This class is a swing view class and hence should only be accessed from the event-handling thread.
 * @version $Id$
 */
public class BrowserHistoryPanel extends RegionsListPanel<DocumentRegion> {
  protected JButton _backButton;
  protected JButton _forwardButton;
  protected JButton _goToButton;
  protected JButton _removeButton;
  protected JButton _removeAllButton;
  protected AbstractAction _backAction;
  protected AbstractAction _forwardAction;
  
  /** Constructs a new browser history panel.
   *  This is swing view class and hence should only be accessed from the event-handling thread.
   *  @param frame the MainFrame
   */
  public BrowserHistoryPanel(MainFrame frame) {
    super(frame, "Browser History");
    _model.getBrowserHistoryManager().addListener(new RegionManagerListener<DocumentRegion>() {      
      public void regionAdded(DocumentRegion r, int index) {
        addRegion(r, index);
        _list.ensureIndexIsVisible(index);
      }
      public void regionChanged(DocumentRegion r, int index) { 
        regionRemoved(r);
        regionAdded(r, index);
      }
      public void regionRemoved(DocumentRegion r) {
        removeRegion(r);
      }
    });
  }
  
  /** Action performed when the Enter key is pressed. Should be overridden. */
  protected void performDefaultAction() {
    goToRegion();
  }
  
  /** Go to region. */
  protected void goToRegion() {
    ArrayList<DocumentRegion> r = getSelectedRegions();
    if (r.size() == 1) {
      _model.getBrowserHistoryManager().setCurrentRegion(r.get(0));
      updateButtons();
      RegionListUserObj<DocumentRegion> userObj = getUserObjForRegion(r.get(0));
      if (userObj != null) { _list.ensureIndexIsVisible(_listModel.indexOf(userObj)); }
      _frame.scrollToDocumentAndOffset(r.get(0).getDocument(), r.get(0).getStartOffset(), false, false);
    }
  }
  
  /** Go to the previous region. */
  protected void backRegion() {
    RegionManager rm = _model.getBrowserHistoryManager();
    
    // add current location to history
    _frame.addToBrowserHistory();
    
    // then move back    
    DocumentRegion r = rm.prevCurrentRegion();
    updateButtons();
    RegionListUserObj<DocumentRegion> userObj = getUserObjForRegion(r);
    if (userObj != null) { _list.ensureIndexIsVisible(_listModel.indexOf(userObj)); }
    _frame.scrollToDocumentAndOffset(r.getDocument(), r.getStartOffset(), false, false);
  }
  
  /** Go to the next region. */
  protected void forwardRegion() {
    RegionManager rm = _model.getBrowserHistoryManager();
    
    // add current location to history
    _frame.addToBrowserHistory();
    
    // then move forward
    DocumentRegion r = rm.nextCurrentRegion();
    updateButtons();
    RegionListUserObj<DocumentRegion> userObj = getUserObjForRegion(r);
    if (userObj != null) { _list.ensureIndexIsVisible(_listModel.indexOf(userObj)); }
    _frame.scrollToDocumentAndOffset(r.getDocument(), r.getStartOffset(), false, false);
  }
  
  /** @return the action to go back in the browser history. */
  AbstractAction getBackAction() {
    return _backAction;
  }
  
  /** @return the action to go forwardin the browser history. */
  AbstractAction getForwardAction() {
    return _forwardAction;
  }
  
  /** Creates the buttons for controlling the regions. Should be overridden. */
  protected JComponent[] makeButtons() {    
    _backAction = new AbstractAction("Back") {
      public void actionPerformed(ActionEvent ae) {
        backRegion();
      }
    };
    _backButton = new JButton(_backAction);

    _forwardAction = new AbstractAction("Forward") {
      public void actionPerformed(ActionEvent ae) {
        forwardRegion();
      }
    };
    _forwardButton = new JButton(_forwardAction);

    Action goToAction = new AbstractAction("Go to") {
      public void actionPerformed(ActionEvent ae) {
        goToRegion();
      }
    };
    _goToButton = new JButton(goToAction);

    Action removeAction = new AbstractAction("Remove") {
      public void actionPerformed(ActionEvent ae) {
        for (DocumentRegion r: getSelectedRegions()) {
          _model.getBrowserHistoryManager().removeRegion(r);
        }
      }
    };
    _removeButton = new JButton(removeAction);
    
    Action removeAllAction = new AbstractAction("Remove All") {
      public void actionPerformed(ActionEvent ae) {
        _model.getBrowserHistoryManager().clearRegions();
      }
    };
    _removeAllButton = new JButton(removeAllAction);
    
    JComponent[] buts = new JComponent[] { 
      _backButton,
        _forwardButton,
        _goToButton, 
        _removeButton,
        _removeAllButton
    };
    
    return buts;
  }

  /** Update button state and text. */
  protected void updateButtons() {
    ArrayList<DocumentRegion> regs = getSelectedRegions();
    _goToButton.setEnabled(regs.size()==1);
    _removeButton.setEnabled(regs.size()>0);
    _removeAllButton.setEnabled(_listModel.size()>0);
    _backAction.setEnabled((_listModel.size()>0) && (!_model.getBrowserHistoryManager().isCurrentRegionFirst()));
    _forwardAction.setEnabled((_listModel.size()>0) && (!_model.getBrowserHistoryManager().isCurrentRegionLast()));
  }
  
  /** Makes the popup menu actions. Should be overridden if additional actions besides "Go to" and "Remove" are added. */
  protected AbstractAction[] makePopupMenuActions() {
    AbstractAction[] acts = new AbstractAction[] {
      new AbstractAction("Go to") {
        public void actionPerformed(ActionEvent e) {
          goToRegion();
        }
      },
        
        new AbstractAction("Remove") {
          public void actionPerformed(ActionEvent e) {
            for (DocumentRegion r: getSelectedRegions()) {
              _model.getBrowserHistoryManager().removeRegion(r);
            }
          }
        }
    };
    return acts;
  }
  
  /** @return the usser object in the list associated with the region, or null if not found */
  protected RegionListUserObj<DocumentRegion> getUserObjForRegion(DocumentRegion r) {
    for(int i=0; i<_listModel.size(); ++i) {
      @SuppressWarnings("unchecked") RegionListUserObj<DocumentRegion> userObj = (RegionListUserObj<DocumentRegion>)_listModel.get(i);
      if (userObj.region()==r) {
        return userObj;
      }
    }
    return null;
  }
  
  /** Factory method to create user objects put in the tree.
   *  If subclasses extend RegionListUserObj, they need to override this method. */
  protected RegionListUserObj<DocumentRegion> makeRegionListUserObj(DocumentRegion r) {
    return new BrowserHistoryListUserObj(r);
  }

  /** Class that gets put into the tree. The toString() method determines what's displayed in the three. */
  protected class BrowserHistoryListUserObj extends RegionListUserObj<DocumentRegion> {
    public BrowserHistoryListUserObj(DocumentRegion r) { super(r); }
    public String toString() {
      final StringBuilder sb = new StringBuilder();
      _region.getDocument().acquireReadLock();
      try {
        sb.append("<html>");
        if (_region==_model.getBrowserHistoryManager().getCurrentRegion()) {
          sb.append("<font color=\"red\">");
        }
        sb.append(_region.getDocument().toString());
        sb.append(':');
        sb.append(lineNumber());
        try {
          sb.append(": ");
          int length = Math.min(120, _region.getEndOffset()-_region.getStartOffset());
          sb.append(_region.getDocument().getText(_region.getStartOffset(), length).trim());
        } catch(BadLocationException bpe) { /* ignore, just don't display line */ }        
        if (_region.equals(_model.getBrowserHistoryManager().getCurrentRegion())) {
          sb.append("</font>");
        }
        sb.append("</html>");
      } finally { _region.getDocument().releaseReadLock(); }
      return sb.toString();
    }
    public boolean equals(Object other) {
      @SuppressWarnings("unchecked") BrowserHistoryListUserObj o = (BrowserHistoryListUserObj)other;
      return (o.region().getDocument().equals(region().getDocument())) &&
        (o.region().getStartOffset()==region().getStartOffset()) &&
        (o.region().getEndOffset()==region().getEndOffset());
    }
  }
}
