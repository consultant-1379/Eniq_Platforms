/**
 * 
 */
package com.ericsson.eniq.techpacksdk;

import ssc.rockfactory.RockFactory;

import com.ericsson.eniq.common.setWizards.StatsCreateTPDirCheckerSet;

/**
 * @author eheijun
 * 
 */
public class CreateTPDirCheckerSetFactory {

  private static StatsCreateTPDirCheckerSet _instance;

  private CreateTPDirCheckerSetFactory() {
  }

  /**
   * Get and create CreateTPDirCheckerSet instance
   * 
   * @param setName
   * @param setVersion
   * @param versionId
   * @param dwhrep
   * @param etlrep
   * @param techPackId
   * @return the _instance
   * @throws Exception
   */
  public static StatsCreateTPDirCheckerSet getInstance(final String setName, final String setVersion,
      final String versionId, final RockFactory dwhrep, final RockFactory etlrep, final Long techPackId,
      final String topologyName) throws Exception {
    if (_instance != null) {
      return _instance;
    }
    return new StatsCreateTPDirCheckerSet(setName, setVersion, versionId, dwhrep, etlrep, techPackId, topologyName);
  }

  /**
   * Set the instance of the CreateTPDirCheckerSet
   * 
   * @param instance
   *          the _instance to set
   */
  public static void setInstance(final StatsCreateTPDirCheckerSet instance) {
    _instance = instance;
  }

}
