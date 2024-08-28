/***
 * 
 */
package com.ericsson.eniq.licensing.cache;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author ecarbjo
 *
 */
public class DefaultLicensingResponseTest {
	private static String msg = "The message of my choosing";
	private DefaultLicensingResponse lr = null;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		lr = new DefaultLicensingResponse(LicensingResponse.LICENSE_VALID, msg);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link com.ericsson.eniq.licensing.cache.DefaultLicensingResponse#getMessage()}.
	 */
	@Test
	public void testGetMessage() {
		assertTrue(lr.getMessage().equals(msg));
	}

	/**
	 * Test method for {@link com.ericsson.eniq.licensing.cache.DefaultLicensingResponse#getResponseType()}.
	 */
	@Test
	public void testGetResponseType() {
		assertTrue(lr.getResponseType() == LicensingResponse.LICENSE_VALID);
	}

	/**
	 * Test method for {@link com.ericsson.eniq.licensing.cache.DefaultLicensingResponse#isValid()}.
	 */
	@Test
	public void testIsValid() {
		assertTrue(lr.isValid());
		
		lr = new DefaultLicensingResponse(LicensingResponse.LICENSE_INVALID, msg);
		assertTrue(!lr.isValid());
	}

}
