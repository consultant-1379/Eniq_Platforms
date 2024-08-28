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
public class DefaultLicenseDescriptorTest {

	private static String name = "CXC666";
	private DefaultLicenseDescriptor ld = null;
	
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

	@Test
	public void testMultipleNames() {
	  String many = "CXC1, CXC2, CXC3";
	  ld = new DefaultLicenseDescriptor(many);
	  assertTrue(ld.getNumFeatures() == 3);
	  assertTrue(ld.getFeatureNames()[1].equals("CXC2"));
	  
	  String[] test = ld.getFeatureNames();
	  for (int i = 0; i < test.length; i++) {
	    System.out.println(test[i]);
	  }
	}
	
	/**
	 * Test method for {@link com.ericsson.eniq.licensing.cache.DefaultLicenseDescriptor#getName()}.
	 */
	@Test
	public void testGetName() {
    ld = new DefaultLicenseDescriptor(name);
		assertTrue(ld.getName().equals(name));
	}

	/**
	 * Test method for {@link com.ericsson.eniq.licensing.cache.DefaultLicenseDescriptor#getCapacity()}.
	 */
	@Test
	public void testGetCapacity() {
    ld = new DefaultLicenseDescriptor(name);
		assertTrue(ld.getCapacity() == -1);
		ld.setCapacity(8);
		assertTrue(ld.getCapacity() == 8);
	}
}
