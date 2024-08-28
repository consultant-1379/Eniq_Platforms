/**
 * 
 */
package com.ericsson.eniq.repository;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.repository.cache.ActivationCache;


/**
 * @author eheijun
 *
 */
public class ActivationCacheWrapper {

  private static Boolean _initializeAllowed = true;
  
  private ActivationCacheWrapper() {
  }

  /**
   * Initialize ActivationCache
   * @param dwhrep
   * @param isAfj
   */
  public static void initialize(final RockFactory dwhrep, final Boolean isAfj) {
    if (_initializeAllowed) {
      ActivationCache.initialize(dwhrep, isAfj);
    }
  }
  
  /**
   * @return the initializeAllowed
   */
  public static Boolean isInitializeAllowed() {
    return _initializeAllowed;
  }

  /**
   * @param initializeAllowed
   *          the initializeAllowed to set
   */
  public static void setInitializeAllowed(final Boolean initializeAllowed) {
    _initializeAllowed = initializeAllowed;
  }

}
