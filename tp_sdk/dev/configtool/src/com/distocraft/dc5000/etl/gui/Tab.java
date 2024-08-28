package com.distocraft.dc5000.etl.gui;



import ssc.rockfactory.RockFactory;

/**
 * Copyright Distocraft 2005<br>
 * <br>
 * $id$
 * 
 * @author lemminkainen
 */
public interface Tab {

  public void connected(RockFactory rock, RockFactory dwhrepRock, String connectionID);

  public void disconnected();

}
