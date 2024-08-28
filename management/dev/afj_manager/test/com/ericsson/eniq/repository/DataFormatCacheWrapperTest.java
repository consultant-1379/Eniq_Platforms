/**
 * 
 */
package com.ericsson.eniq.repository;

import org.junit.Test;


/**
 * @author eheijun
 *
 */
public class DataFormatCacheWrapperTest {

  /**
   * Test method for {@link com.ericsson.eniq.repository.DataFormatCacheWrapper#initialize(ssc.rockfactory.RockFactory)}.
   */
  @Test
  public void testInitialize() {
    if (DataFormatCacheWrapper.isInitializeAllowed()) {
      DataFormatCacheWrapper.setInitializeAllowed(false);
    }
    DataFormatCacheWrapper.initialize(null);
  }

}
