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
public class DefaultSourceTest {

  private static final String SOME_TYPENAME = "SOME_TYPENAME";

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.data.vo.DefaultSource#DefaultSource(java.lang.String)}
   * .
   */
  @Test
  public void testDefaultSource() {
    try {
      final DefaultSource defaultSource = new DefaultSource(SOME_TYPENAME);
      assertEquals(SOME_TYPENAME, defaultSource.getTypename());
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

}
