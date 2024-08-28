/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.data.vo;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author eheijun
 * 
 */
public class DefaultMappedTypeTest {

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.data.vo.DefaultMappedType#DefaultMappedType()}
   * .
   */
  @Test
  public void testDefaultMappedType() {
    try {
      new DefaultMappedType(null, null, null, null);
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

}
