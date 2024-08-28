/**
 * 
 */
package com.ericsson.eniq.dwhm;

import java.util.logging.Logger;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.dwhm.StorageTimeAction;

/**
 * @author eheijun
 * 
 */
public class StorageTimeActionFactory {

  private static StorageTimeAction _instance;

  private StorageTimeActionFactory() {}

  /**
   * Get or create instance of the StorageTimeAction
   * 
   * @return the _instance
   * @throws Exception
   */
  public static StorageTimeAction getInstance(final RockFactory dwhrep, final RockFactory etlrep,
      final RockFactory dwh, final RockFactory dbadwh, final String tpName, final Logger log, final String measName, final boolean isCalledByNVU)
      throws Exception {
    if (_instance != null) {
      return _instance;
    }
    return new StorageTimeAction(dwhrep, etlrep, dwh, dbadwh, tpName, log, measName,isCalledByNVU);
  }

  /**
   * Set the _instance of the StorageTimeAction
   * 
   * @param instance
   *          the _instance to set
   */
  public static void setInstance(final StorageTimeAction instance) {
    _instance = instance;
  }

}
