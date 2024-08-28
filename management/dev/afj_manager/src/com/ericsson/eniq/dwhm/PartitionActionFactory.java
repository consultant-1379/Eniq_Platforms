/**
 * 
 */
package com.ericsson.eniq.dwhm;

import java.util.logging.Logger;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.dwhm.PartitionAction;

/**
 * @author eheijun
 * 
 */
public class PartitionActionFactory {

  private static PartitionAction _instance;

  private PartitionActionFactory() {}

  /**
   * Get or create instance of the PartitionAction
   * 
   * @param dwhrep
   * @param dwh
   * @param tpName
   * @param log
   * @return
   * @throws Exception
   */
  public static PartitionAction getInstance(final RockFactory dwhrep, final RockFactory dwh, final String tpName,
      final Logger log, final boolean isCalledByNVU) throws Exception {
    if (_instance != null) {
      return _instance;
    }
    return new PartitionAction(dwhrep, dwh, tpName, log, isCalledByNVU);
  }

  /**
   * Set the instance of the PartitionAction
   * 
   * @param instance
   *          the _instance to set
   */
  public static void setInstance(final PartitionAction instance) {
    _instance = instance;
  }

}
