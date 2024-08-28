/**
 * 
 */
package com.ericsson.eniq.repository;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.repository.cache.DataFormatCache;

/**
 * @author eheijun
 * 
 */
public class DataFormatCacheWrapper {

  private static Boolean _initializeAllowed = true;

  private DataFormatCacheWrapper() {
  }

  /**
   * If required initializes DataFormatCache
   * @param rock
   * @return the _instance
   */
  public static void initialize(final RockFactory rock) {
    if (_initializeAllowed) {
      DataFormatCache.initialize(rock);
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
