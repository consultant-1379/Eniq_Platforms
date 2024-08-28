/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.database;

import com.ericsson.eniq.busyhourcfg.dwhmanager.DWHStorageTimeAction;


/**
 * @author eheijun
 *
 */
public class BusyhourUpdaterFactory {
  
  private static BusyhourUpdater updater;
  
  /**
   * Hide constructor
   */
  private BusyhourUpdaterFactory() {}
  
  public static BusyhourUpdater getBusyhourUpdater(final DatabaseSession databaseSession, final DWHStorageTimeAction dwhStorageTimeAction) {
    if (updater == null) {
      return new RockBusyhourUpdater(databaseSession, dwhStorageTimeAction);
    }
    return updater;
  }

  
  /**
   * @return the updater
   */
  public static BusyhourUpdater getUpdater() {
    return updater;
  }

  
  /**
   * @param updater the updater to set
   */
  public static void setUpdater(final BusyhourUpdater updater) {
    BusyhourUpdaterFactory.updater = updater;
  }
}
