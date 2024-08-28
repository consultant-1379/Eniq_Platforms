/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.database;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ssc.rockfactory.RockFactory;

import com.ericsson.eniq.busyhourcfg.config.BusyhourProperties;
import com.ericsson.eniq.busyhourcfg.data.vo.DefaultTechPack;
import com.ericsson.eniq.busyhourcfg.data.vo.TechPack;
import com.ericsson.eniq.busyhourcfg.dwhmanager.DWHStorageTimeAction;
import com.ericsson.eniq.busyhourcfg.dwhmanager.DWHStorageTimeActionFactory;
import com.ericsson.eniq.busyhourcfg.exceptions.DatabaseException;
import com.ericsson.eniq.repository.ETLCServerProperties;


/**
 * @author eheijun
 *
 */
public class ActivationActionTest {
  

  private final Mockery context = new JUnit4Mockery() {

    {
      setImposteriser(ClassImposteriser.INSTANCE);
    }
  };
  
  private static final String TESTDB_DRIVER = "org.hsqldb.jdbcDriver";

  private static final String TESTDB_URL = "jdbc:hsqldb:mem:dummydb";

  private static final String TESTDB_USERNAME = "sa";

  private static final String TESTDB_PASSWORD = "";

  //private ETLCServerProperties mockETLCServerProperties;
  private ETLCServerProperties etlcServerProperties;
  
  private BusyhourProperties mockBusyhourProperties;
  
  private TechPack mockTechPack;

  private DatabaseConnector mockDatabaseConnector;

  private RockDatabaseSession mockDatabaseSession;

  /**
   * @throws java.lang.Exception
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    System.setProperty("CONF_DIR", System.getProperty("user.dir"));
    System.setProperty("dc5000.config.directory", System.getProperty("CONF_DIR"));
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
    System.clearProperty("dc5000.config.directory");
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
    mockBusyhourProperties = context.mock(BusyhourProperties.class);
    mockTechPack = context.mock(TechPack.class);
    mockDatabaseConnector = context.mock(DatabaseConnector.class);
    mockDatabaseSession = context.mock(RockDatabaseSession.class);
    final DWHStorageTimeAction mockDWHStorageTimeAction = context.mock(DWHStorageTimeAction.class);
    final RockFactory mockDwhrepRockFactory = context.mock(RockFactory.class, "DWHREP");
    final RockFactory mockDwhRockFactory = context.mock(RockFactory.class, "DWH");
    
    context.checking(new Expectations() {

      {
        allowing(mockDatabaseConnector).createDatabaseSession(with(any(String.class)));
        will(returnValue(mockDatabaseSession));
        allowing(mockDatabaseSession).getDwhRockFactory();
        will(returnValue(mockDwhRockFactory));
        allowing(mockDatabaseSession).getDwhrepRockFactory();
        will(returnValue(mockDwhrepRockFactory));
        allowing(mockDatabaseSession).clear();
      }
    });
    
    
    DatabaseConnectorFactory.setDatabaseConnector(mockDatabaseConnector);
    DWHStorageTimeActionFactory.setDWHStorageTimeAction(mockDWHStorageTimeAction);
    
  }

  /**
   * Test method for {@link com.ericsson.eniq.busyhourcfg.database.ActivationAction#call()}.
   */
  @Test
  public void testCall() {
    ActivationAction activationAction = new ActivationAction(etlcServerProperties, mockBusyhourProperties, mockTechPack);
    try {
      UpdateResult updateResult = activationAction.call();
      assertTrue(updateResult.isError());
    } catch (Exception e) {
      fail(e.getMessage());
    }
    
  }

  /**
   * Test method for {@link com.ericsson.eniq.busyhourcfg.database.ActivationAction#call()}.
   */
  @Test
  public void testInvalidCall() {
    try {
      context.checking(new Expectations() {
        {
          allowing(mockDatabaseSession).performBusyhourActivation(with(any(DefaultTechPack.class)), with(any(DWHStorageTimeAction.class)));
          will(throwException(new DatabaseException("EXPECTED TEST EXCEPTION")));
        }
      });
    } catch (DatabaseException e) {
      fail(e.getMessage());
    }
    ActivationAction activationAction = new ActivationAction(etlcServerProperties, mockBusyhourProperties, new DefaultTechPack("TEST:((1))", "TEST", 0));
    try {
      UpdateResult updateResult = activationAction.call();
      assertTrue(updateResult.isError());
    } catch (Exception e) {
      fail(e.getMessage());
    }
    
  }

}
