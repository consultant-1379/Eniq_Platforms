/**
 * 
 */
package com.ericsson.eniq.repository;

import org.junit.Test;


/**
 * @author eheijun
 *
 */
public class PhysicalTableCacheWrapperTest {

  /**
   * Test method for {@link com.ericsson.eniq.repository.PhysicalTableCacheWrapper#initialize(ssc.rockfactory.RockFactory)}.
   */
  @Test
  public void testInitialize() {
    if (PhysicalTableCacheWrapper.isInitializeAllowed()) {
      PhysicalTableCacheWrapper.setInitializeAllowed(false);
    }
    PhysicalTableCacheWrapper.initialize(null);
  }

}
