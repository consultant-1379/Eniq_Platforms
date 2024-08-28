/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.config;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author eheijun
 *
 */
public class BusyhourPropertiesTest {

  private static final String BUSYHOURCFG_PROPERTIES = "busyhourcfg.properties";

  private static final String SOME_VALUE = "busyhourcfg";
  
  /**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	  final FileWriter fwriter = new FileWriter(BUSYHOURCFG_PROPERTIES);
	  final PrintWriter out = new PrintWriter(fwriter);
	  out.println(BusyhourProperties.APPLICATIONNAME + "=" + SOME_VALUE);
	  out.close();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	  final File f = new File(BUSYHOURCFG_PROPERTIES);
    if (!f.delete()) {
      fail("failed to remove test property file");
    }
	}

	/**
	 * Test method for {@link com.ericsson.eniq.busyhourcfg.config.BusyhourProperties#BusyhourProperties()}.
	 */
	@Test
	public void testBusyhourProperties() {
	  final BusyhourProperties busyhourProperties = new BusyhourProperties();
	  assertFalse(busyhourProperties.getProperty(BusyhourProperties.APPLICATIONNAME).equals(SOME_VALUE));
	}

	/**
	 * Test method for {@link com.ericsson.eniq.busyhourcfg.config.BusyhourProperties#BusyhourProperties(java.lang.String)}.
	 */
	@Test
	public void testBusyhourPropertiesString() {
	  final BusyhourProperties busyhourProperties = new BusyhourProperties(BUSYHOURCFG_PROPERTIES);
    assertTrue(busyhourProperties.getProperty(BusyhourProperties.APPLICATIONNAME).equals(SOME_VALUE));
	}

	/**
	 * Test method for {@link com.ericsson.eniq.busyhourcfg.config.BusyhourProperties#getProperty(java.lang.String)}.
	 */
	@Test
	public void testGetProperty() {
	  final BusyhourProperties busyhourProperties = new BusyhourProperties();
    busyhourProperties.addProperty(BusyhourProperties.APPLICATIONNAME, SOME_VALUE);
    assertTrue(busyhourProperties.getProperty(BusyhourProperties.APPLICATIONNAME).equals(SOME_VALUE));
	}

}
