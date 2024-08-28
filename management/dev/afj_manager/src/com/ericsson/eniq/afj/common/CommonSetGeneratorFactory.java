/**
 * 
 */
package com.ericsson.eniq.afj.common;

import ssc.rockfactory.RockFactory;


/**
 * @author eheijun
 *
 */
public class CommonSetGeneratorFactory {
  
  private static CommonSetGenerator _instance;
  
  private CommonSetGeneratorFactory() {
  }

  /**
   * Get or create instance of the CommonSetGenerator 
   * @param templateDir 
   * @param setName 
   * @param setVersion 
   * @param dwhrep 
   * @param etlrep 
   * @param techPackId 
   * @param techPackName 
   * @return the _instance
   * @throws Exception 
   */
  public static CommonSetGenerator getInstance(final String templateDir, final String setName, final String setVersion, final RockFactory dwhrep, final RockFactory etlrep, final Integer techPackId, final String techPackName, final Boolean schedulings) throws Exception {
    if (_instance != null) {
      return _instance;
    }
    return new CommonSetGenerator(templateDir, setName, setVersion, dwhrep, etlrep, techPackId, techPackName, schedulings);
  }

  
  /**
   * Set the instance of the CommonSetGenerator
   * @param instance the _instance to set
   */
  public static void setInstance(final CommonSetGenerator instance) {
    _instance = instance;
  }

}
