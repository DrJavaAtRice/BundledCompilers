/*BEGIN_COPYRIGHT_BLOCK
 *
 * This file is a part of DrJava. Current versions of this project are available
 * at http://sourceforge.net/projects/drjava
 *
 * Copyright (C) 2001-2002 JavaPLT group at Rice University (javaplt@rice.edu)
 *
 * DrJava is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * DrJava is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * or see http://www.gnu.org/licenses/gpl.html
 *
 * In addition, as a special exception, the JavaPLT group at Rice University
 * (javaplt@rice.edu) gives permission to link the code of DrJava with
 * the classes in the gj.util package, even if they are provided in binary-only
 * form, and distribute linked combinations including the DrJava and the
 * gj.util package. You must obey the GNU General Public License in all
 * respects for all of the code used other than these classes in the gj.util
 * package: Dictionary, HashtableEntry, ValueEnumerator, Enumeration,
 * KeyEnumerator, Vector, Hashtable, Stack, VectorEnumerator.
 *
 * If you modify this file, you may extend this exception to your version of the
 * file, but you are not obligated to do so. If you do not wish to
 * do so, delete this exception statement from your version. (However, the
 * present version of DrJava depends on these classes, so you'd want to
 * remove the dependency first!)
 *
END_COPYRIGHT_BLOCK*/

package edu.rice.cs.drjava.ui.config;

import javax.swing.*;
import edu.rice.cs.drjava.ui.KeyBindingManager;
import edu.rice.cs.drjava.config.*;
import edu.rice.cs.drjava.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Hashtable;

/**
 * Graphical form of a KeyStrokeOption
 * @version $Id$
 */
public class KeyStrokeOptionComponent  extends OptionComponent<KeyStrokeOption> implements Comparable{
  private static final int DIALOG_HEIGHT = 185;
  public static Hashtable _keyToKSOC = new Hashtable();
  private JButton _jb;
  private static GetKeyDialog _getKeyDialog =  null;    
  
  private KeyStroke _ks;
  private KeyStroke _configKs;

  public KeyStrokeOptionComponent (KeyStrokeOption opt, String text, final Frame parent) {
    super(opt, text, parent);
  
    _ks = DrJava.CONFIG.getSetting(opt);
    _configKs = _ks;
    
    _jb = new JButton();
    _jb.setBackground(Color.white);
    _jb.setText(_option.format(_ks));
    _jb.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {

        if (_getKeyDialog == null) {
          _getKeyDialog =    
            new GetKeyDialog(parent, 
                             "Specify Shortcut", 
                             true);
        }

        _getKeyDialog.promptKey(KeyStrokeOptionComponent.this);
      }
    });
   
    GridLayout gl = new GridLayout(1,0);
    gl.setHgap(15);
    _keyToKSOC.put(_ks, this);
  }
  
  public String toString() { 
    return "<KSOC>label:" + getLabelText() + "ks: " +
      
      _getKeyStroke() + "jb: " + _jb.getText() + "</KSOC>\n";
  }
  
  public boolean update() {
    if (!_ks.equals(DrJava.CONFIG.getSetting(_option))) {
      DrJava.CONFIG.setSetting(_option, _ks);
      _setKeyStroke(DrJava.CONFIG.getSetting(_option));
    }
    return true;
  }
  
  public int compareTo(Object o) {
    KeyStrokeOptionComponent other = (KeyStrokeOptionComponent)o;
    return this.getLabelText().compareTo(other.getLabelText());
  }
  
  public KeyStroke _getKeyStroke() {
    return _ks;
  }
  
  public KeyStroke _getConfigKeyStroke() {
    return _configKs;
  }
  
  public JComponent getComponent() { return _jb; }
  
  private void _setKeyStroke(KeyStroke ks) {
    System.out.println("setKS:" + getLabelText() + ks);
    _ks = ks;
    _jb.setText(_option.format(ks));
    System.out.println("<--setKS:" + getLabelText() + _jb.getText());   
  }
  /*
  private JButton _getButton() {
    return _jb;
  }*/
  /*
  public boolean equals (Object o) {
    if (! (o instanceof KeyStrokeOptionComponent)) return false;
    return (_option  == ((KeyStrokeOptionComponent)o)._option);
  }*/
  
  /**
   * A dialog that allows the user to type in a keystroke to be bound
   * to the action that was clicked. If the user types a keystroke that
   * is bound to another action, the dialog will display that information.
   */
  private class GetKeyDialog extends JDialog {
    private InputField _inputField;
    private JButton _clearButton;
    private JButton _cancelButton;
    private JButton _okButton;  
    private JLabel _instructionLabel;
    private JLabel _currentLabel;
    private JLabel _actionLabel;
    private JPanel _inputAndClearPanel;
    private JPanel _labelsPanel;
    private JPanel _cancelAndOKPanel;
    private KeyStroke _currentKeyStroke;
    private KeyStrokeOptionComponent _ksoc;
    private Frame frame;
         
    public GetKeyDialog(Frame f, String title, boolean modal) {
      super(f, title, modal);
      frame = f;
    
      _inputField = new InputField();
      _clearButton = new JButton("Clear");
      _clearButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ae) {
          _inputField.setText("");
          _actionLabel.setText("<none>");
          _currentKeyStroke = KeyStrokeOption.NULL_KEYSTROKE;
        }
      });
      _cancelButton = new JButton("Cancel");
      _cancelButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ae) {
          GetKeyDialog.this.dispose();
        }
      });
      _okButton = new JButton("OK");
      _okButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ae) {
          if (!_ksoc._getKeyStroke().equals(_currentKeyStroke)) {
            _keyToKSOC.remove(_ksoc._getKeyStroke());            
            
            KeyStrokeOptionComponent conflict = 
              (KeyStrokeOptionComponent)_keyToKSOC.get(_currentKeyStroke);
            
            if (conflict != null) {
              _keyToKSOC.remove(_currentKeyStroke);
              conflict._setKeyStroke(KeyStrokeOption.NULL_KEYSTROKE);
            }
            _keyToKSOC.put(_currentKeyStroke, _ksoc);
            _ksoc._setKeyStroke(_currentKeyStroke);
          }
          GetKeyDialog.this.dispose();
          
        }
      });
      _instructionLabel = new JLabel("Type in the keystroke you want to use " +
                                     "and click \"OK\"");
      _currentLabel = new JLabel("Current action bound to the keystroke:");
      _actionLabel = new JLabel("<none>");
      
      _inputAndClearPanel = new JPanel(new BorderLayout());
      _inputAndClearPanel.add(_inputField, BorderLayout.CENTER);
      _inputAndClearPanel.add(_clearButton, BorderLayout.EAST);
      
      //_labelsPanel = new JPanel();
      //_labelsPanel.add(_currentLabel);
      //_labelsPanel.add(_actionLabel);
      
      _cancelAndOKPanel = new JPanel(new GridLayout(1,0));
      _cancelAndOKPanel.add(_okButton);
      _cancelAndOKPanel.add(_cancelButton);
      
      JPanel panel = (JPanel)this.getContentPane();
      
      panel.setLayout(new GridLayout(0, 1));
      panel.add(_instructionLabel);
      panel.add(_inputAndClearPanel);
      //panel.add(_labelsPanel);
      panel.add(_currentLabel);
      panel.add(_actionLabel);
      panel.add(_cancelAndOKPanel);
      this.setSize((int)_instructionLabel.getPreferredSize().getWidth() + 30, DIALOG_HEIGHT);
      centerOnScreen();
      this.pack();
    }
   
    public void promptKey(KeyStrokeOptionComponent k) {
      _ksoc = k;
      _instructionLabel.setText("Type in the keystroke you want to use for " +
                                k.getLabelText() + 
                                " and click \"OK\"");
      _currentKeyStroke = k._getKeyStroke();
      _actionLabel.setText("<none>");
      _inputField.setText("");
      //this.setLocation(frame.getLocation());
      this.setSize((int)_instructionLabel.getPreferredSize().getWidth() + 30, DIALOG_HEIGHT);
      centerOnScreen();
      super.setVisible(true);
      
    }
    
    private void centerOnScreen() {
      
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      Dimension dialogSize = this.getSize();
      // Set to the new correct size and location
      this.setLocation((screenSize.width - dialogSize.width) / 2,
                       (screenSize.height - dialogSize.height) / 2);
    }
    
    /**
     * A textfield that takes in one keystroke at a time and displays
     * its formatted String version. It updates the label that displays
     * what action the currently displayed keystroke is bound to.
     */
    private class InputField extends JTextField {
      /*public boolean getFocusTraversalKeysEnabled() {
        return false;
      }*/
      
      public void processKeyEvent(KeyEvent e) {    
        KeyStroke ks = KeyStroke.getKeyStrokeForEvent(e);
        System.out.println(ks);     
        if (e.getID() == KeyEvent.KEY_PRESSED) {         
          this.setText(_option.format(ks));
          KeyStrokeOptionComponent configKs = (KeyStrokeOptionComponent)_keyToKSOC.get(ks);
          if (configKs == null)
            _actionLabel.setText("<none>");
          else {
            String name = KeyBindingManager.Singleton.getName(configKs._getConfigKeyStroke());
            _actionLabel.setText(name);
          }
          _currentKeyStroke = ks;
        }
      }
    }
  }
  
  public void reset() {
    _jb.setText(_option.format(DrJava.CONFIG.getSetting(_option)));
  }  

}