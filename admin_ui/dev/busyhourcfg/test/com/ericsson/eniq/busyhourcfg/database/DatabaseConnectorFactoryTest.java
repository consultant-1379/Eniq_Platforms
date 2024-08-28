/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.database;

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

import com.ericsson.eniq.busyhourcfg.config.BusyhourProperties;
import com.ericsson.eniq.repository.ETLCServerProperties;


/**
 * @author eheijun
 *
 */
public class DatabaseConnectorFactoryTest {

  private final Mockery context = new JUnit4Mockery() {

    {
      setImposteriser(ClassImposteriser.INSTANCE);
    }
  };
  
  private static final String TEST_APPLICATION = "RockDatabaseConnectorTest";

  private static final String TESTDB_DRIVER = "org.hsqldb.jdbcDriver";

  private static final String TESTDB_URL = "jdbc:hsqldb:mem:dummydb";

  private static final String TESTDB_USERNAME = "sa";

  private static final String TESTDB_PASSWORD = "";

  private ETLCServerProperties etlcServerProperties;

  private BusyhourProperties busyhourProperties;

  private DatabaseConnector mockDatabaseConnector;

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
    etlcServerProperties.addProperty(ETLCServerProperties.DBURL, TESTDB_URL);
    etlcServerProperties.addProperty(ETLCServerProperties.DBUSERNAME, TESTDB_USERNAME);
    etlcServerProperties.addProperty(ETLCServerProperties.DBPASSWORD, TESTDB_PASSWORD);
    etlcServerProperties.addProperty(ETLCServerProperties.DBDRIVERNAME, TESTDB_DRIVER);
    busyhourProperties = new BusyhourProperties();
    busyhourProperties.addProperty(BusyhourProperties.APPLICATIONNAME, TEST_APPLICATION);
    mockDatabaseConnector = context.mock(DatabaseConnector.class);
  }
  
  /**
   * Test method for {@link com.ericsson.eniq.busyhourcfg.database.DatabaseConnectorFactory#createDatabaseConnector()}.
   */
  @Test
  public void testGetDefaultDatabaseConnector() {
    DatabaseConnectorFactory.setDatabaseConnector(null);
    final DatabaseConnector databaseConnector = DatabaseConnectorFactory.getDatabaseConnector(etlcServerProperties, busyhourProperties);
    assertTrue(databaseConnector instanceof RockDatabaseConnector);
    assertTrue(databaseConnector != null);
    final RockDatabaseConnector rockDatabaseConnector = (RockDatabaseConnector) databaseConnector;
    assertTrue(rockDatabaseConnector.getEtlrepRock() != null);
  }

  /**
   * Test method for {@link com.ericsson.eniq.busyhourcfg.database.DatabaseConnectorFactory#createDatabaseConnector()}.
   */
  @Test
  public void testGetNonDefaultDatabaseConnector() {
    DatabaseConnectorFactory.setDatabaseConnector(mockDatabaseConnector);
    final DatabaseConnector databaseConnector = DatabaseConnectorFactory.getDatabaseConnector(etlcServerProperties, busyhourProperties);
    assertFalse(databaseConnector instanceof RockDatabaseConnector);
    assertTrue(databaseConnector != null);
  }

  /**
   * Test method for {@link com.ericsson.eniq.busyhourcfg.database.DatabaseConnectorFactory#createDatabaseConnector()}.
   */
  @Test
  public void testGetDatabaseConnectorWithNullParams() {
    DatabaseConnectorFactory.setDatabaseConnector(null);
    final DatabaseConnector databaseConnector1 = DatabaseConnectorFactory.getDatabaseConnector(null, busyhourProperties);
    assertTrue(databaseConnector1 == null);
    final DatabaseConnector databaseConnector2 = DatabaseConnectorFactory.getDatabaseConnector(etlcServerProperties, null);
    assertTrue(databaseConnector2 == null);
    final DatabaseConnector databaseConnector3 = DatabaseConnectorFactory.getDatabaseConnector(null, null);
    assertTrue(databaseConnector3 == null);
  }

}
