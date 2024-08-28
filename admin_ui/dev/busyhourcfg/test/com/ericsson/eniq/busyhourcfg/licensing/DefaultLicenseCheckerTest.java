/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.licensing;

import static org.junit.Assert.*;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ericsson.eniq.repository.ETLCServerProperties;


/**
 * @author eheijun
 *
 */
@RunWith(JMock.class)
public class DefaultLicenseCheckerTest {

  private final Mockery context = new JUnit4Mockery() {

    {
      setImposteriser(ClassImposteriser.INSTANCE);
    }
  };
  
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
   * Test method for {@link com.ericsson.eniq.busyhourcfg.licensing.DefaultLicenseChecker#DefaultLicenseChecker(com.ericsson.eniq.busyhourcfg.config.ETLCServerProperties)}.
   */
  @Test
  public void testDefaultLicenseChecker() {
    final ETLCServerProperties mockProperties = context.mock(ETLCServerProperties.class);
    context.checking(new Expectations() {
      {
        allowing(mockProperties).getProperty(with("ENGINE_HOSTNAME"));
        will(returnValue("localhost"));
        allowing(mockProperties).getProperty(with("ENGINE_PORT"));
        will(returnValue("1234"));
      }
    });
    
    try {
      final LicenseChecker licenseChecker = new DefaultLicenseChecker(mockProperties);
      assertTrue(licenseChecker != null);
    } catch (Exception e) {
      fail("Creating licenseChecker failed: " + e.getMessage());
    }
  }

  /**
   * Test method for {@link com.ericsson.eniq.busyhourcfg.licensing.DefaultLicenseChecker#DefaultLicenseChecker(com.ericsson.eniq.busyhourcfg.config.ETLCServerProperties)}.
   */
  @Test
  public void testDefaultLicenseCheckerWithNull() {
    try {
      final LicenseChecker licenseChecker = new DefaultLicenseChecker(null);
      assertTrue(licenseChecker != null);
    } catch (Exception e) {
      fail("Creating licenseChecker with null failed: " + e.getMessage());
    }
  }

//  /**
//   * Test method for {@link com.ericsson.eniq.busyhourcfg.licensing.DefaultLicenseChecker#isLicenseValid(java.lang.String)}.
//   */
//  @Test
//  public void testIsLicenseValid() {
//    // 
//  }
}
