package com.distocraft.dc5000.etl.gui.set.actionview;


/**
 * Copyright Distocraft 2005<br>
 * <br>
 * $id$
 *
 * @author lemminkainen
 */
public interface ActionView {
  
  public String getType();
  public String validate();
  public String getContent() throws Exception;
  public String getWhere() throws Exception;
  public boolean isChanged();

}
