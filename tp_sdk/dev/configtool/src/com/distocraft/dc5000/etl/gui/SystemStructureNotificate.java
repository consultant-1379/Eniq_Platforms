package com.distocraft.dc5000.etl.gui;


/**
 * Copyright Distocraft 2005<br>
 * <br>
 * $id$
 *
 * @author lemminkainen
 */
public interface SystemStructureNotificate {
  
  public void metaDataChange();
  public void techPackChange();
  public void setChange();

  public void addSystemStructureListener(SystemStructureListener ssl);
  public void removeSystemStructureListener(SystemStructureListener ssl);
  
}
