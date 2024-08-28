/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.config;


/**
 * @author eheijun
 *
 */
public class BusyhourEnvironmentFactory {

  private static BusyhourEnvironment _instance;
  
  /**
   * Hide constructor
   */
  private BusyhourEnvironmentFactory() {
    super();
  }

  /**
   * Gets environment settings 
   * @return default environment settings for busyhourcfg
   */
  public static BusyhourEnvironment getBusyhourEnvironment() {
    if (_instance == null) {
      _instance = new DefaultBusyhourEnvironment();
    }
    return _instance;
  }
  
  /**
   * Sets environment settings 
   * @return default environment settings for busyhourcfg
   */
  public static void setBusyhourEnvironment(final BusyhourEnvironment instance) {
    _instance = instance;
  }
  
}
