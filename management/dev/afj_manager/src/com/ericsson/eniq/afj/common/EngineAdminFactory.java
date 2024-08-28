/**
 * 
 */
package com.ericsson.eniq.afj.common;

import com.distocraft.dc5000.etl.engine.main.EngineAdmin;


/**
 * @author eheijun
 *
 */
public class EngineAdminFactory {
  
  private static EngineAdmin _instance;
  
  private EngineAdminFactory() {}
  
  /**
   * Get instance of the EngineAdmin. 
   * @return
   */
  public static EngineAdmin getInstance()  {
    if (_instance == null) {
      _instance = new EngineAdmin(); 
    }
    return _instance;
  }

  /**
   * Set alternative instance of the EngineAdmin
   * @param instance
   */
  public static void setInstance(final EngineAdmin instance)  {
    _instance = instance;
  }

}
