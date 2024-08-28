/**
 * 
 */
package com.ericsson.eniq.afj;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ericsson.eniq.exception.AFJConfiguationException;

/**
 * @author esunbal
 *
 */
public class AFJConfigurationExceptionTest {

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
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link com.ericsson.eniq.exception.AFJConfiguationException#AFJConfiguationException(java.lang.String, java.lang.Exception)}.
	 */
	@Test
	public void testAFJConfiguationExceptionStringException() {
		final AFJConfiguationException exception = new AFJConfiguationException("Exception with cause", new FileNotFoundException());
		final Throwable actual = exception.getCause();
		final Throwable expected = new FileNotFoundException();
		assertEquals(expected.getMessage(), actual.getMessage());
	}

	/**
	 * Test method for {@link com.ericsson.eniq.exception.AFJConfiguationException#AFJConfiguationException(java.lang.String)}.
	 */
	@Test
	public void testAFJConfiguationExceptionString() {
		final AFJConfiguationException exception = new AFJConfiguationException("Exception");
		final String expected = "Exception";
		final String actual = exception.getMessage();
		assertEquals(expected, actual);
	}

}
