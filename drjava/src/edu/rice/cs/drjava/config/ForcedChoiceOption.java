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

package edu.rice.cs.drjava.config;
import java.util.Collection;
import java.util.Iterator;


/**
 * Class defining a configuration option that requires a choice between
 * mutually-exclusive possible values.  Values are stored as Strings, though
 * this could be extended to any type with a fairly simple refactoring.
 * @version $Id$
 */
public class ForcedChoiceOption extends Option<String>
{
  private Collection<String> _choices;

  /**
   * @param key The name of this option.
   * @param def The default value of the option.
   * @param choices A collection of all possible values of this Option, as Strings.
   */
  public ForcedChoiceOption(String key, String def, Collection<String> choices) {
    super(key,def);
    _choices = choices;
  }

  /**
   * Checks whether the parameter String is a legal value for this option.
   * The input String must be formatted exactly like the original, as defined
   * by String.equals(String).
   * @param s the value to check
   * @return true if s is legal, false otherwise
   */
  public boolean isLegal(String s) {
    return _choices.contains(s);
  }

  /**
   * Gets all legal values of this option.
   * @return an Iterator containing the set of all Strings for which isLegal returns true.
   */
  public Iterator<String> getLegalValues() {
    return _choices.iterator();
  }

  /**
   * Gets the number of legal values for this option.
   * @return an int indicating the number of legal values.
   */
  public int getNumValues() {
    return _choices.size();
  }

  /**
   * Parses an arbitrary String into an acceptable value for this option.
   * @param s The String to be parsed.
   * @return s, if s is a legal value of this option.
   * @exception IllegalArgumentException if "s" is not one of the allowed values.
   */
  public String parse(String s)
  {
    if (isLegal(s)) {
      return s;
    }
    else {
      throw new OptionParseException(name, s, "Value is not an acceptable choice for this option.");
    }
  }

  /**
   * @param s The String to be formatted.
   * @return "s", no actual formatting is performed.
   */
  public String format(String s) {
    return s;
  }
}
