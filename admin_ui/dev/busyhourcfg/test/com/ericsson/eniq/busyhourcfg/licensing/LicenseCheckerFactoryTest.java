/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.licensing;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ericsson.eniq.repository.ETLCServerProperties;


/**
 * @author eheijun
 *
 */
public class LicenseCheckerFactoryTest {

  private final Mockery context = new JUnit4Mockery() {

    {
      setImposteriser(ClassImposteriser.INSTANCE);
    }
  };
  
  private ETLCServerProperties etlcServerProperties;

  private LicenseChecker mockLicenseChecker;
  
  /**
   * @throws java.lang.Exception
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    System.setProperty("CONF_DIR", System.getProperty("user.dir"));
    final File etlcProperties = new File(System.getProperty("CONF_DIR"), "ETLCServer.properties");
    try {
      final PrintWriter pw = new PrintWriter(new FileWriter(etlcProperties));
      pw.write("ENGINE_DB_URL = jdbc:hsqldb:mem:testdb\n");
      pw.write("ENGINE_DB_USERNAME = sa\n");
      pw.write("ENGINE_DB_PASSWORD = \n");
      pw.write("ENGINE_DB_DRIVERNAME = org.hsqldb.jdbcDriver\n");
      pw.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  
  /**
   * @throws java.lang.Exception
   */
  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    final File etlcProperties = new File(System.getProperty("user.dir"), "ETLCServer.properties");
    etlcProperties.delete();
    System.clearProperty("CONF_DIR");
  }
  
  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    etlcServerProperties = new ETLCServerProperties("ETLCServer.properties");
    mockLicenseChecker = context.mock(LicenseChecker.class);
  }

  /**
   * Test method for {@link com.ericsson.eniq.busyhourcfg.licensing.LicenseCheckerFactory#getLicenseChecker(com.ericsson.eniq.busyhourcfg.config.ETLCServerProperties)}.
   */
  @Test
  public void testGetDefaultLicenseChecker() {
    LicenseCheckerFactory.setLicenseChecker(null);
    final LicenseChecker licenseChecker = LicenseCheckerFactory.getLicenseChecker(etlcServerProperties);
    assertTrue(licenseChecker instanceof DefaultLicenseChecker);
    assertTrue(licenseChecker != null);
  }

  /**
   * Test method for {@link com.ericsson.eniq.busyhourcfg.licensing.LicenseCheckerFactory#getLicenseChecker(com.ericsson.eniq.busyhourcfg.config.ETLCServerProperties)}.
   */
  @Test
  public void testGetNonDefaultLicenseChecker() {
    LicenseCheckerFactory.setLicenseChecker(mockLicenseChecker);
    final LicenseChecker licenseChecker = LicenseCheckerFactory.getLicenseChecker(etlcServerProperties);
    assertFalse(licenseChecker instanceof DefaultLicenseChecker);
    assertTrue(licenseChecker != null);
  }

}
