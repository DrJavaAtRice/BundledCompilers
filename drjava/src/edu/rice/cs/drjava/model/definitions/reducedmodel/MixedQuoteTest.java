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
 *END_COPYRIGHT_BLOCK*/

package edu.rice.cs.drjava.model.definitions.reducedmodel;

import  junit.framework.*;

/**
 * Tests the interaction between double and single quotes and comments
 * @version $Id$
 */
public final class MixedQuoteTest extends BraceReductionTestCase
  implements ReducedModelStates
{
  protected ReducedModelControl _model;

  /**
   * Initializes the reduced models used in the tests.
   */
  protected void setUp() throws Exception {
    super.setUp();
    _model = new ReducedModelControl();
  }

  /**
   * Creates a test suite for JUnit to use.
   * @return a test suite for JUnit
   */
  public static Test suite() {
    return  new TestSuite(MixedQuoteTest.class);
  }

  /**
   * Convenience function to insert a number of non-special characters into a reduced model.
   * @param model the model being modified
   * @param size the number of characters being inserted
   */
  protected void insertGap(BraceReduction model, int size) {
    for (int i = 0; i < size; i++) {
      model.insertChar(' ');
    }
  }

  /**
   * Tests how a single quote can eclipse the effects of a double quote by inserting
   * the single quote before the double quote.  This test caught an error with
   * getStateAtCurrent(): the check for double quote status checks if there is a double
   * quote immediately preceding, but it didn't make sure the double quote was FREE.
   * I fixed that, so now the test passes.
   */
  public void testSingleEclipsesDouble() {
    _model.insertChar('\"');
    assertEquals("#0.0", INSIDE_DOUBLE_QUOTE, _model.getStateAtCurrent());
    _model.move(-1);
    assertEquals("#0.1", FREE, stateOfCurrentToken(_model));
    _model.move(1);
    _model.insertChar('A');
    _model.move(-1);
    assertEquals("#1.0", INSIDE_DOUBLE_QUOTE, _model.getStateAtCurrent());
    assertEquals("#1.1", INSIDE_DOUBLE_QUOTE, stateOfCurrentToken(_model));
    assertTrue("#1.2", _model.currentToken().isGap());
    _model.move(-1);
    _model.insertChar('\'');
    assertEquals("#2.0", INSIDE_SINGLE_QUOTE, _model.getStateAtCurrent());
    assertEquals("#2.1", INSIDE_SINGLE_QUOTE, stateOfCurrentToken(_model));
    assertEquals("#2.2", "\"", _model.currentToken().getType());
    _model.move(1);
    assertEquals("#3.0", INSIDE_SINGLE_QUOTE, _model.getStateAtCurrent());
    assertEquals("#3.1", INSIDE_SINGLE_QUOTE, stateOfCurrentToken(_model));
    assertTrue("#3.2", _model.currentToken().isGap());
  }

  /**
   * Tests how a double quote can eclipse the effects of a single quote by inserting
   * the double quote before the single quote.
   */
  public void testDoubleEclipsesSingle() {
    _model.insertChar('\'');
    assertEquals("#0.0", INSIDE_SINGLE_QUOTE, _model.getStateAtCurrent());
    _model.move(-1);
    assertEquals("#0.1", FREE, stateOfCurrentToken(_model));
    _model.move(1);
    _model.insertChar('A');
    _model.move(-1);
    assertEquals("#1.0", INSIDE_SINGLE_QUOTE, _model.getStateAtCurrent());
    assertEquals("#1.1", INSIDE_SINGLE_QUOTE, stateOfCurrentToken(_model));
    assertTrue("#1.2", _model.currentToken().isGap());
    _model.move(-1);
    _model.insertChar('\"');
    assertEquals("#2.0", INSIDE_DOUBLE_QUOTE, _model.getStateAtCurrent());
    assertEquals("#2.1", INSIDE_DOUBLE_QUOTE, stateOfCurrentToken(_model));
    assertEquals("#2.2", "\'", _model.currentToken().getType());
    _model.move(1);
    assertEquals("#3.0", INSIDE_DOUBLE_QUOTE, _model.getStateAtCurrent());
    assertEquals("#3.1", INSIDE_DOUBLE_QUOTE, stateOfCurrentToken(_model));
    assertTrue("#3.2", _model.currentToken().isGap());
  }
}
