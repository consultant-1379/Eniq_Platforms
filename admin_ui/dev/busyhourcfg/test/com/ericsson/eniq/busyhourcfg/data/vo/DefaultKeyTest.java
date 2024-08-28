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
public class DefaultKeyTest {

  private static final String TESTVALUE = "TESTVALUE";
  private static final String TESTSOURCE = "TESTSOURCE";
  private static final String TESTNAME = "TESTNAME";

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.data.vo.DefaultKey#DefaultKey()}.
   */
  @Test
  public void testDefaultKey() {
    try {
      new DefaultKey(TESTNAME, TESTSOURCE, TESTVALUE);
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

}
