package com.distocraft.dc5000.etl.gui;


/**
 * Copyright Distocraft 2005<br>
 * <br>
 * $id$
 *
 * @author lemminkainen
 */
public interface UI {
  
  /**
   * Enables/disables UI.
   * @param enabled
   */
  public void startOperation(String msg);
  public void endOperation();
}
