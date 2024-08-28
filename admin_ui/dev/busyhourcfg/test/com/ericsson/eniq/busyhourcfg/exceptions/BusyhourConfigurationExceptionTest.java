/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.exceptions;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author eheijun
 *
 */
public class BusyhourConfigurationExceptionTest {

  private static final String SOME_ERROR_MESSAGE = "some error message";

	/**
	 * Test method for {@link com.ericsson.eniq.busyhourcfg.exceptions.BusyhourConfigurationException#BusyhourConfigurationException(java.lang.String)}.
	 */
	@Test
	public void testBusyhourConfigurationException() {
    try {
      throw new BusyhourConfigurationException(SOME_ERROR_MESSAGE);      
    } catch (BusyhourConfigurationException e) {
      assertTrue(SOME_ERROR_MESSAGE.equals(e.getMessage()));
    } catch (Exception e) {
      fail("wrong exception");
    }
	}

}
