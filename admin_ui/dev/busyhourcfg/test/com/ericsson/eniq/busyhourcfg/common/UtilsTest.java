/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.common;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * @author eheijun
 *
 */
public class UtilsTest {

	/**
	 * Test method for {@link com.ericsson.eniq.busyhourcfg.common.Utils#replaceNull(java.lang.Integer)}.
	 */
	@Test
	public void testReplaceNullInteger() {
		Integer result = null;
		Integer tmp = null;
		result = Utils.replaceNull(tmp);
		assertEquals(result.intValue(), 0);
		tmp = Integer.MAX_VALUE;
		result = Utils.replaceNull(tmp);
		assertEquals(result.intValue(), Integer.MAX_VALUE);
		tmp = Integer.valueOf(0);
		result = Utils.replaceNull(tmp);
		assertEquals(result.intValue(), 0);
		tmp = Integer.MIN_VALUE;
		result = Utils.replaceNull(tmp);
		assertEquals(result.intValue(), Integer.MIN_VALUE);
	}

	/**
	 * Test method for {@link com.ericsson.eniq.busyhourcfg.common.Utils#replaceNull(java.lang.String)}.
	 */
	@Test
	public void testReplaceNullString() {
		String result = null;
		String tmp = null;
		result = Utils.replaceNull(tmp);
		assertEquals(result, "");
		tmp = "qwerty";
		result = Utils.replaceNull(tmp);
		assertEquals(result, tmp);
	}

	/**
	 * Test method for {@link com.ericsson.eniq.busyhourcfg.common.Utils#replaceNull(java.lang.Double)}.
	 */
	@Test
	public void testReplaceNullDouble() {
		Double result = null;
		Double tmp = null;
		result = Utils.replaceNull(tmp);
		assertTrue(result.equals(new Double(0L)));
		tmp = Double.MAX_VALUE;
		result = Utils.replaceNull(tmp);
		assertTrue(result.equals(new Double(Double.MAX_VALUE)));
		tmp = new Double(0);
		result = Utils.replaceNull(tmp);
		assertEquals(result.intValue(), 0);
		tmp = Double.MIN_VALUE;
		result = Utils.replaceNull(tmp);
		assertTrue(result.equals(new Double(Double.MIN_VALUE)));
	}

	/**
	 * Test method for {@link com.ericsson.eniq.busyhourcfg.common.Utils#replaceNull(java.lang.Long)}.
	 */
	@Test
	public void testReplaceNullLong() {
		Long result = null;
		Long tmp = null;
		result = Utils.replaceNull(tmp);
		assertTrue(result.equals(Long.valueOf(0)));
		tmp = Long.MAX_VALUE;
		result = Utils.replaceNull(tmp);
		assertTrue(result.equals(new Long(Long.MAX_VALUE)));
		tmp = Long.valueOf(0);
		result = Utils.replaceNull(tmp);
		assertEquals(result.intValue(), 0);
		tmp = Long.MIN_VALUE;
		result = Utils.replaceNull(tmp);
		assertTrue(result.equals(new Long(Long.MIN_VALUE)));
	}

	/**
	 * Test method for {@link com.ericsson.eniq.busyhourcfg.common.Utils#booleanToInteger(java.lang.Boolean)}.
	 */
	@Test
	public void testBooleanToInteger() {
		Integer result = null;
		Boolean tmp = Boolean.valueOf(true);
		result = Utils.booleanToInteger(tmp);
		assertTrue(result.equals(Integer.valueOf(1)));
		tmp = Boolean.valueOf(false);
		result = Utils.booleanToInteger(tmp);
		assertTrue(result.equals(Integer.valueOf(0)));
	}

  /**
   * Test method for {@link com.ericsson.eniq.busyhourcfg.common.Utils#booleanToString(java.lang.Boolean)}.
   */
  @Test
  public void testBooleanToString() {
    String result = null;
    Boolean tmp = Boolean.valueOf(true);
    result = Utils.booleanToString(tmp);
    assertTrue(result.equals(String.valueOf("True")));
    tmp = Boolean.valueOf(false);
    result = Utils.booleanToString(tmp);
    assertTrue(result.equals(String.valueOf("False")));
  }

  /**
   * Test method for {@link com.ericsson.eniq.busyhourcfg.common.Utils#stringToBoolean(java.lang.String)}.
   */
  @Test
  public void testStringToBoolean() {
    Boolean result = null;
    String tmp = Boolean.valueOf(true).toString();
    result = Utils.stringToBoolean(tmp);
    assertTrue(result.equals(Boolean.valueOf(true)));
    tmp = Boolean.valueOf(false).toString();
    result = Utils.stringToBoolean(tmp);
    assertTrue(result.equals(Boolean.valueOf(false)));
  }

	/**
	 * Test method for {@link com.ericsson.eniq.busyhourcfg.common.Utils#integerToBoolean(java.lang.Integer)}.
	 */
	@Test
	public void testIntegerToBoolean() {
		Boolean result = null;
		Integer tmp = Integer.valueOf(1);
		result = Utils.integerToBoolean(tmp);
		assertTrue(result.booleanValue());
		tmp = Integer.valueOf(0);
		result = Utils.integerToBoolean(tmp);
		assertFalse(result.booleanValue());
	}

  /**
   * Test method for {@link com.ericsson.eniq.busyhourcfg.common.Utils#stringToInteger(java.lang.String)}.
   */
  @Test
  public void testStringToInteger() {
    Integer result = null;
    String tmp = "0";
    result = Utils.stringToInteger(tmp);
    assertTrue(result.equals(Integer.valueOf(tmp)));
    tmp = "";
    result = Utils.stringToInteger(tmp);
    assertTrue(result.equals(Integer.valueOf("0")));
  }
  
	/**
	 * Test method for {@link com.ericsson.eniq.busyhourcfg.common.Utils#stringListToString(java.util.List)}.
	 */
	@Test
	public void testStringListToString() {
		final List<String> list = new ArrayList<String>();
		String result = Utils.stringListToString(list);
		assertTrue(result.equals(""));
		list.add("qwerty");
		result = Utils.stringListToString(list);
		assertTrue(result.equals("qwerty"));
		list.add("asdfgh");
		result = Utils.stringListToString(list);
		assertTrue(result.equals("qwerty\nasdfgh"));
		result = Utils.stringListToString(list);
	}

}
