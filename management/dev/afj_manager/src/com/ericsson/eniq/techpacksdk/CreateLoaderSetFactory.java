/**
 * 
 */
package com.ericsson.eniq.techpacksdk;

import com.ericsson.eniq.common.setWizards.StatsCreateLoaderSet;

import ssc.rockfactory.RockFactory;

/**
 * @author eheijun
 * 
 */
public class CreateLoaderSetFactory {

  private static StatsCreateLoaderSet _instance;

  private CreateLoaderSetFactory() {
  }

  /**
   * Get and create the instance of the CreateLoaderSet
   * 
   * @param templateDir
   * @param setName
   * @param setVersion
   * @param versionId
   * @param dwhrep
   * @param etlrep
   * @param techPackId
   * @param techPackName
   * @param schedulings
   * @return the _instance
   * @throws Exception
   */
  public static StatsCreateLoaderSet getInstance(final String templateDir, final String setName, final String setVersion,
      final String versionId, final RockFactory dwhrep, final RockFactory etlrep, final Long techPackId,
      final String techPackName, final Boolean schedulings) throws Exception {
    if (_instance != null) {
      return _instance;
    }
    return new StatsCreateLoaderSet(templateDir, setName, setVersion, versionId, dwhrep, etlrep, techPackId.intValue(),
        techPackName, schedulings);
  }

  /**
   * Set the instance of the CreateLoaderSet
   * 
   * @param instance
   *          the _instance to set
   */
  public static void setInstance(final StatsCreateLoaderSet instance) {
    _instance = instance;
  }

}
