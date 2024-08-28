/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.database;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.URL;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ssc.rockfactory.RockFactory;
import utils.MemoryDatabaseUtility;

import com.ericsson.eniq.busyhourcfg.config.BusyhourProperties;
import com.ericsson.eniq.busyhourcfg.exceptions.DatabaseException;
import com.ericsson.eniq.repository.ETLCServerProperties;

/**
 * @author eheijun
 * 
 */
public class RockDatabaseConnectorTest {

  public static final String TEST_APPLICATION = RockDatabaseConnectorTest.class.getName();

  private static RockFactory etlrepRock;

  private static RockFactory dwhrepRock;

  private static RockFactory dwhRock;

  private ETLCServerProperties etlcServerProperties;

  private BusyhourProperties busyhourProperties;

  /**
   * Creates database schemas for memdb
   * 
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
    etlrepRock = new RockFactory(MemoryDatabaseUtility.TEST_ETLREP_URL, MemoryDatabaseUtility.TESTDB_USERNAME,
        MemoryDatabaseUtility.TESTDB_PASSWORD, MemoryDatabaseUtility.TESTDB_DRIVER, TEST_APPLICATION, true);
    final URL etlrepsqlurl = ClassLoader.getSystemResource(MemoryDatabaseUtility.TEST_ETLREP_BASIC);
    if (etlrepsqlurl == null) {
      System.out.println("etlrep script can not be loaded!");
    } else {
      MemoryDatabaseUtility.loadSetup(etlrepRock, etlrepsqlurl);
    }
    dwhrepRock = new RockFactory(MemoryDatabaseUtility.TEST_DWHREP_URL, MemoryDatabaseUtility.TESTDB_USERNAME,
        MemoryDatabaseUtility.TESTDB_PASSWORD, MemoryDatabaseUtility.TESTDB_DRIVER, TEST_APPLICATION, true);
    final URL dwhrepsqlurl = ClassLoader.getSystemResource(MemoryDatabaseUtility.TEST_DWHREP_BASIC);
    if (etlrepsqlurl == null) {
      System.out.println("dwhrep script can not be loaded!");
    } else {
      MemoryDatabaseUtility.loadSetup(dwhrepRock, dwhrepsqlurl);
    }
    dwhRock = new RockFactory(MemoryDatabaseUtility.TEST_DWH_URL, MemoryDatabaseUtility.TESTDB_USERNAME,
        MemoryDatabaseUtility.TESTDB_PASSWORD, MemoryDatabaseUtility.TESTDB_DRIVER, TEST_APPLICATION, true);
    final URL dwhsqlurl = ClassLoader.getSystemResource(MemoryDatabaseUtility.TEST_DWH_BASIC);
    if (etlrepsqlurl == null) {
      System.out.println("dwh script can not be loaded!");
    } else {
      MemoryDatabaseUtility.loadSetup(dwhRock, dwhsqlurl);
    }
  }

  /**
   * Clean memory databases
   * 
   * @throws java.lang.Exception
   */
  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    MemoryDatabaseUtility.shutdown(dwhRock);
    MemoryDatabaseUtility.shutdown(dwhrepRock);
    MemoryDatabaseUtility.shutdown(etlrepRock);
    final File etlcProperties = new File(System.getProperty("user.dir"), "ETLCServer.properties");
    etlcProperties.delete();
    System.clearProperty("CONF_DIR");
  }

  /**
   * Creates properties needed for database connector
   * 
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    // making sure that we test default RockDatabaseConnector 
    DatabaseConnectorFactory.setDatabaseConnector(null);
    etlcServerProperties = new ETLCServerProperties("ETLCServer.properties");
    etlcServerProperties.addProperty(ETLCServerProperties.DBURL, MemoryDatabaseUtility.TEST_ETLREP_URL);
    etlcServerProperties.addProperty(ETLCServerProperties.DBUSERNAME, MemoryDatabaseUtility.TESTDB_USERNAME);
    etlcServerProperties.addProperty(ETLCServerProperties.DBPASSWORD, MemoryDatabaseUtility.TESTDB_PASSWORD);
    etlcServerProperties.addProperty(ETLCServerProperties.DBDRIVERNAME, MemoryDatabaseUtility.TESTDB_DRIVER);
    busyhourProperties = new BusyhourProperties();
    busyhourProperties.addProperty(BusyhourProperties.APPLICATIONNAME, TEST_APPLICATION);
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.database.RockDatabaseConnector#createDatabaseSession(com.ericsson.eniq.busyhourcfg.config.ETLCServerProperties, com.ericsson.eniq.busyhourcfg.config.BusyhourProperties)}
   * .
   */
  @Test
  public void testCreateDatabaseSession() {
    try {
      final RockDatabaseConnector databaseConnector = (RockDatabaseConnector) DatabaseConnectorFactory.getDatabaseConnector(etlcServerProperties,
          busyhourProperties);
      final DatabaseSession databaseSession = databaseConnector.createDatabaseSession(DatabaseConnectorFactory
          .getApplicationName());
      assertTrue(databaseSession instanceof RockDatabaseSession);
      final RockDatabaseSession rockDatabaseSession = (RockDatabaseSession) databaseSession;
      assertTrue(rockDatabaseSession != null);
      rockDatabaseSession.clear();
    } catch (DatabaseException e) {
      fail(e.getMessage());
    }
  }
  
  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.database.RockDatabaseConnector#createDatabaseSession(com.ericsson.eniq.busyhourcfg.config.ETLCServerProperties, com.ericsson.eniq.busyhourcfg.config.BusyhourProperties)}
   * .
   */
  @Test
  public void testCreateDatabaseSessionFail() {
    try {
      final RockDatabaseConnector databaseConnector = new RockDatabaseConnector(null);
      databaseConnector.createDatabaseSession(DatabaseConnectorFactory.getApplicationName());
      fail("DatabaseSession created with invalid connector.");
    } catch (DatabaseException e) {
      // should happen
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }
  
}
