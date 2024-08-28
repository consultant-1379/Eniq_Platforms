/**
 * 
 */
package com.ericsson.eniq.afj.database;


/**
 * @author eheijun
 *
 */
public class AFJDatabaseCommonUtilsFactory {
  
  private static AFJDatabaseCommonUtils _instance;
  
  private AFJDatabaseCommonUtilsFactory() {
  }
  
  /**
   * Get or create the instance of the AFJDatabaseCommonUtils 
   * @return the _instance
   */
  public static AFJDatabaseCommonUtils getInstance() {
    if (_instance != null) {
      return _instance;
    }
    return new AFJDatabaseCommonUtils();
  }

  
  /**
   * @param instance the _instance to set
   */
  public static void setInstance(final AFJDatabaseCommonUtils instance) {
    _instance = instance;
  }

}
