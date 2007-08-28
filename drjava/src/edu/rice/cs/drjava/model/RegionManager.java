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

import edu.rice.cs.util.Lambda;
import java.util.Vector;

/** Interface for a region manager.
  * @version $Id$
  */
public interface RegionManager<R extends DocumentRegion> {    
  /** Returns the region in this manager at the given offset, or null if one does not exist.
   *  @param odd the document
   *  @param offset the offset in the document
   *  @return the DocumentRegion at the given line number, or null if it does not exist.
   */
  public R getRegionAt(OpenDefinitionsDocument odd, int offset);

  /** Get the DocumentRegion that is stored in this RegionsTreePanel overlapping the area for the given document,
   *  or null if it doesn't exist.
   *  @param odd the document
   *  @param startOffset the start offset
   *  @param endOffset the end offset
   *  @return the DocumentRegion or null
   */
  public R getRegionOverlapping(OpenDefinitionsDocument odd, int startOffset, int endOffset);
  
  /** Add the supplied DocumentRegion to the manager.
   *  @param region the DocumentRegion to be inserted into the manager
   */
  public void addRegion(R region);

  /** Remove the given DocumentRegion from the manager.
   *  @param region the DocumentRegion to be removed.
   */
  public void removeRegion(R region);

  /** Apply the given command to the specified region to change it.
   *  @param region the region to find and change
   *  @param cmd command that mutates the region. */
  public void changeRegion(R region, Lambda<Object,R> cmd);

  /** @return a Vector<R> containing the DocumentRegion objects in this mangager. */
  public Vector<R> getRegions();

  /** Tells the manager to remove all regions. */
  public void clearRegions();
  
  /** @return the current region or null if none selected */
  public R getCurrentRegion();
  
  /** @return the index of the current region or -1 if none selected */
  public int getCurrentRegionIndex();
  
  /** @return true if the current region is the first in the list, i.e. prevCurrentRegion is without effect */
  public boolean isCurrentRegionFirst();
  
  /** @return true if the current region is the last in the list, i.e. nextCurrentRegion is without effect */
  public boolean isCurrentRegionLast();
  
  /** Set the current region. 
   *  @param region new current region */
  public void setCurrentRegion(R region);
  
  /** Make the region that is more recent the current region.
   *  @return new current region */
  public R nextCurrentRegion();
  
  /** Make the region that is less recent the current region.
   *  @return new current region */
  public R prevCurrentRegion();
  
  /**
   * Set the maximum number of regions that can be stored in this manager.
   * If the maximum capacity has been reached and another region is added, the region at the end farther
   * away from the insertion location will be discarded.
   * @param size maximum number of regions, or 0 if no maximum
   */
  public void setMaximumSize(int size);
  
  /** @return the maximum number of regions that can be stored in this manager. */
  public int getMaximumSize();

  /** Adds a listener to the notifier.
   *  @param listener a listener that reacts on events
   */
  public void addListener(RegionManagerListener<R> listener);
  
  /** Removes a listener from the notifier.
   *  @param listener a listener that reacts on events
   */
  public void removeListener(RegionManagerListener<R> listener);

  /** Removes all listeners from this notifier.  */
  public void removeAllListeners();
}
