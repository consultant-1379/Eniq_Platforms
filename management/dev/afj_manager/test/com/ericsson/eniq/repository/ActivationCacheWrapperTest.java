/**
 * 
 */
package com.ericsson.eniq.repository;

import org.junit.Test;


/**
 * @author eheijun
 *
 */
public class ActivationCacheWrapperTest {

  /**
   * Test method for {@link com.ericsson.eniq.repository.ActivationCacheWrapper#initialize(ssc.rockfactory.RockFactory, boolean)}.
   */
  @Test
  public void testInitialize() {
    if (ActivationCacheWrapper.isInitializeAllowed()) {
      ActivationCacheWrapper.setInitializeAllowed(false);
    }
    ActivationCacheWrapper.initialize(null, null);
  }

}
