/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.config;

/**
 * @author eheijun
 * 
 */
public class BusyhourConfigurationFactory {
  
  private static BusyhourConfiguration _instance;

  
  /**
   * Hide constructor
   */
  private BusyhourConfigurationFactory() {
    super();
  }

  /**
   * Gets configuration settings 
   * @return default configuration settings for busyhourcfg
   */
  public static BusyhourConfiguration getBusyhourConfiguration() {
    if (_instance == null) {
      _instance = new DefaultBusyhourConfiguration(); 
    }
    return _instance;
  }
  
  /**
   * Set alternative BusyhourConfiguration (for junit tests)
   * @param instance
   */
  public static void setBusyhourConfiguration(final BusyhourConfiguration instance) {
    _instance = instance;
  }

}
