/**
 * 
 */
package com.ericsson.eniq.dwhm;

import java.util.logging.Logger;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.dwhm.VersionUpdateAction;

/**
 * @author eheijun
 * 
 */
public class VersionUpdateActionFactory {

  private static VersionUpdateAction _instance;

  private VersionUpdateActionFactory() {}

  /**
   * Get or create instance of the VersionUpdateAction
   * 
   * @return the _instance
   * @throws Exception
   */
  public static VersionUpdateAction getInstance(final RockFactory dwhrep, final RockFactory dwh, final String tpName,
      final Logger log) throws Exception {
    if (_instance != null) {
      return _instance;
    }
    return new VersionUpdateAction(dwhrep, dwh, tpName, log);
  }

  /**
   * Set the _instance of the VersionUpdateAction
   * 
   * @param instance
   *          the _instance to set
   */
  public static void setInstance(final VersionUpdateAction instance) {
    _instance = instance;
  }

}
