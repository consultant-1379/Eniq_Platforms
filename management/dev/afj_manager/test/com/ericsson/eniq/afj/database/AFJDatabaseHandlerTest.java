/**
 * 
 */
package com.ericsson.eniq.afj.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.rock.Meta_databases;
import com.ericsson.eniq.afj.database.AFJDatabaseHandler;
import com.ericsson.eniq.exception.AFJException;

/**
 * @author esunbal
 *
 */
public class AFJDatabaseHandlerTest {

	private static AFJDatabaseHandler objectUnderTest;
	
	private static File ETLCServerProperties;	
	
	private static Connection connection;
	
	private static Statement statement;

	private static File AFJManagerProperties;
	
	private static File StaticProperties;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		/* Create the needed tables. */
		try {
			Class.forName("org.hsqldb.jdbcDriver").newInstance();
			connection = DriverManager.getConnection("jdbc:hsqldb:mem:testdb", "sa", "");
			connection.setAutoCommit(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		statement = connection.createStatement();
		
		statement.execute("CREATE TABLE META_DATABASES (USERNAME VARCHAR(31), VERSION_NUMBER VARCHAR(31), "
				+ "TYPE_NAME VARCHAR(31), CONNECTION_ID BIGINT, CONNECTION_NAME VARCHAR(31), "
				+ "CONNECTION_STRING VARCHAR(31), PASSWORD VARCHAR(31), DESCRIPTION VARCHAR(31), "
				+ "DRIVER_NAME VARCHAR(31), DB_LINK_NAME VARCHAR(31))");
		
		statement.execute("create table DefaultTags (TAGID varchar(50) not null, " +
				"DATAFORMATID varchar(100) not null, DESCRIPTION varchar(200) null)");

		statement.execute("alter table DefaultTags add primary key (TAGID, DATAFORMATID)");

		statement.execute("create table MeasurementCounter (TYPEID varchar(255) not null, " +
				"DATANAME varchar(128) not null, DESCRIPTION varchar(32000) null, " +
				"TIMEAGGREGATION varchar(50) null, GROUPAGGREGATION varchar(50) null, " +
				"COUNTAGGREGATION varchar(50) null, COLNUMBER numeric(9) null, DATATYPE varchar(50) null, " +
				"DATASIZE integer null, DATASCALE integer null, INCLUDESQL integer null, " +
				"UNIVOBJECT varchar(128) null, UNIVCLASS varchar(35) null, COUNTERTYPE varchar(16) null, COUNTERPROCESS varchar(16) null, DATAID varchar(255))");

		statement.execute("alter table MeasurementCounter add primary key (TYPEID, DATANAME)");


		/* Create property file for database connection details */
		AFJManagerProperties = new File(System.getProperty("user.dir"), "AFJManager.properties");
		System.setProperty("CONF_DIR",System.getProperty("user.dir"));
		AFJManagerProperties.deleteOnExit();
		try {
			final PrintWriter pw = new PrintWriter(new FileWriter(AFJManagerProperties));
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}    
		
		/* Create property file for database connection details */
		ETLCServerProperties = new File(System.getProperty("user.dir"), "ETLCServer.properties");
		System.setProperty("CONF_DIR",System.getProperty("user.dir"));
		ETLCServerProperties.deleteOnExit();
		try {
			final PrintWriter pw = new PrintWriter(new FileWriter(ETLCServerProperties));
			pw.write("ENGINE_DB_URL = jdbc:hsqldb:mem:testdb\n");
			pw.write("ENGINE_DB_USERNAME = sa\n");
			pw.write("ENGINE_DB_PASSWORD = \n");
			pw.write("ENGINE_DB_DRIVERNAME = org.hsqldb.jdbcDriver\n");
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/* Create property file for database connection details */
		StaticProperties = new File(System.getProperty("user.dir"), "static.properties");
		System.setProperty("CONF_DIR",System.getProperty("user.dir"));
		StaticProperties.deleteOnExit();
		try {
			final PrintWriter pw = new PrintWriter(new FileWriter(StaticProperties));
			pw.write("sybaseiq.option.public.DML_Options5=0");
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	  @AfterClass
	  public static void tearDownAfterClass() throws Exception {
	    try {
	      final Statement stmt = connection.createStatement();
	      try {
	        stmt.execute("DROP TABLE Measurementcounter");
	        stmt.execute("DROP TABLE DefaultTags");
	        stmt.execute("DROP TABLE META_DATABASES");
	        stmt.executeUpdate("SHUTDOWN");
	      } finally {
	        stmt.close();
	      }
	    } finally {
	      connection.close();
	    }
      /* Cleaning up after test */
      statement = null;
      connection = null;
      objectUnderTest = null;
	  }
	
	  @Before
		public void setUpBeforeTest() throws Exception {			
		  objectUnderTest = AFJDatabaseHandler.getInstance();		
		}  

	  @After
		public void tearDown() throws Exception {			
		  objectUnderTest = null;
		  AFJDatabaseHandler.setInstance(null);
		}
	  
	/**
	 * Test method for {@link com.ericsson.eniq.afj.database.AFJDatabaseHandler#getInstance()}.
	 */
	@Test
	public void testGetInstance() {
		final AFJDatabaseHandler result = AFJDatabaseHandler.getInstance();
		if ( !(result instanceof AFJDatabaseHandler)){
			fail("Should not reach this point. getInstance() not returning AFJDatabaseHandler.");
		}
		else{
			assert(true);
		}
	}

	/**
	 * Test method for {@link com.ericsson.eniq.afj.database.AFJDatabaseHandler#getDwhrep()}.
	 */
	@Test
	public void testGetDwhrepExceptionCheck1(){
		try{			
			/* Inserting META_DATABASE data for dwhRockFactory  */
			statement.executeUpdate("INSERT INTO META_DATABASES VALUES"
					+ "('sa', 'v1.2', 'USER', 1, 'dwhrepa', 'jdbc:hsqldb:mem:testdb', '', 'desc', "
					+ "'org.hsqldb.jdbcDriver', 'db')");

			objectUnderTest.getDwhrep();			
			fail("Should not reach this point.");
		}
		catch(Exception e){
			assert(true);
		}
	}
	
	/**
	 * Test method for {@link com.ericsson.eniq.afj.database.AFJDatabaseHandler#getDwh()}.
	 */
	@Test
	public void testGetDwhExceptionCheck1(){
		try{			
			/* Inserting META_DATABASE data for dwhRockFactory  */
			statement.executeUpdate("INSERT INTO META_DATABASES VALUES"
					+ "('sa', 'v1.2', 'USER', 1, 'dweh', 'jdbc:hsqldb:mem:testdb', '', 'desc', "
					+ "'org.hsqldb.jdbcDriver', 'db')");

			objectUnderTest.getDwh();			
			fail("Should not reach this point.");
		}
		catch(Exception e){
			assert(true);
		}
	}
	
	/**
	 * Test method for {@link com.ericsson.eniq.afj.database.AFJDatabaseHandler#getDbaDwh()}.
	 */
	@Test
	public void testGetDbaDwhException1(){
		try{			
		/* Inserting META_DATABASE data for dwhRockFactory */
		statement.executeUpdate("INSERT INTO META_DATABASES VALUES"
				+ "('sa', 'v1.2', 'DBA', 1, 'dwha', 'jdbc:hsqldb:mem:testdb', '', 'desc', "
				+ "'org.hsqldb.jdbcDriver', 'db')");
		
		objectUnderTest.getDbaDwh();		
		fail("Should not have reached this point.");
		}
		catch(Exception e){
			assert(true);
		}
	}

	
	/**
	 * Test method for {@link com.ericsson.eniq.afj.database.AFJDatabaseHandler#getEtlrep()}.
	 */
	@Test
	public void testGetEtlrep() throws AFJException{		
		final RockFactory rockFactory = (RockFactory) objectUnderTest.getEtlrep();
		
		final String actual = rockFactory.getDbURL() + ", " + rockFactory.getUserName() + ", " + rockFactory.getPassword() + ", "
		+ rockFactory.getDriverName();
		final String expected = "jdbc:hsqldb:mem:testdb, sa, , org.hsqldb.jdbcDriver";
		assertEquals(expected, actual);
	}

	/**
	 * Test method for {@link com.ericsson.eniq.afj.database.AFJDatabaseHandler#getDwhrep()}.
	 */
	@Test
	public void testGetDwhrep() throws Exception{		
		/* Inserting META_DATABASE data for dwhRockFactory  */
		statement.executeUpdate("INSERT INTO META_DATABASES VALUES"
				+ "('sa', 'v1.2', 'USER', 1, 'dwhrep', 'jdbc:hsqldb:mem:testdb', '', 'desc', "
				+ "'org.hsqldb.jdbcDriver', 'db')");
		
		final RockFactory rockFactory = (RockFactory) objectUnderTest.getDwhrep();
		/* Asserting that the object is created and it has correct properties */
		final String actual = rockFactory.getDbURL() + ", " + rockFactory.getUserName() + ", " + rockFactory.getPassword() + ", "
		+ rockFactory.getDriverName();
		final String expected = "jdbc:hsqldb:mem:testdb, sa, , org.hsqldb.jdbcDriver";
		assertEquals(expected, actual);
	}
	

	
	/**
	 * Test method for {@link com.ericsson.eniq.afj.database.AFJDatabaseHandler#getDwh()}.
	 */
	@Test
	public void testGetDwh() throws Exception{		
		/* Inserting META_DATABASE data for dwhRockFactory  */
		statement.executeUpdate("INSERT INTO META_DATABASES VALUES"
				+ "('sa', 'v1.2', 'USER', 1, 'dwh', 'jdbc:hsqldb:mem:testdb', '', 'desc', "
				+ "'org.hsqldb.jdbcDriver', 'db')");
		
		final RockFactory rockFactory = (RockFactory) objectUnderTest.getDwh();
		/* Asserting that the object is created and it has correct properties */
		final String actual = rockFactory.getDbURL() + ", " + rockFactory.getUserName() + ", " + rockFactory.getPassword() + ", "
		+ rockFactory.getDriverName();
		final String expected = "jdbc:hsqldb:mem:testdb, sa, , org.hsqldb.jdbcDriver";
		assertEquals(expected, actual);
	}

	
	
	
	/**
	 * Test method for {@link com.ericsson.eniq.afj.database.AFJDatabaseHandler#getDbaDwh()}.
	 */
	@Test
	public void testGetDbaDwh() throws Exception{		
		/* Inserting META_DATABASE data for dwhRockFactory */
		statement.executeUpdate("INSERT INTO META_DATABASES VALUES"
				+ "('sa', 'v1.2', 'DBA', 1, 'dwh', 'jdbc:hsqldb:mem:testdb', '', 'desc', "
				+ "'org.hsqldb.jdbcDriver', 'db')");
		
		final RockFactory rockFactory = (RockFactory) objectUnderTest.getDbaDwh();
		/* Asserting that the object is created and it has correct properties */
		final String actual = rockFactory.getDbURL() + ", " + rockFactory.getUserName() + ", " + rockFactory.getPassword() + ", "
		+ rockFactory.getDriverName();
		final String expected = "jdbc:hsqldb:mem:testdb, sa, , org.hsqldb.jdbcDriver";
		assertEquals(expected, actual);
	}
	
	

	
	/**
	 * Test method for {@link com.ericsson.eniq.afj.database.AFJDatabaseHandler#commitTransaction(boolean)}.
	 * May need to revisit this for logic check.
	 */
	@Test
	public void testCommitTransactionTrue() throws Exception{		
		statement.executeUpdate("INSERT INTO META_DATABASES VALUES"
				+ "('sa', 'v1.2', 'USER', 1, 'dwhrep', 'jdbc:hsqldb:mem:testdb', '', 'desc', "
				+ "'org.hsqldb.jdbcDriver', 'db')");			
		
		final RockFactory rockFactory = (RockFactory) objectUnderTest.getDwhrep();		
		final Meta_databases md = new Meta_databases(rockFactory);
		md.setUsername("sa");
		md.setVersion_number("v1.2");
		md.setType_name("TESTUSER");
		md.setDescription("Test db");
		md.setDriver_name("jdbc:hsqldb:mem:testdb");		
		md.insertDB();		
		try{
		objectUnderTest.commitTransaction(true);		
		}
		catch(AFJException ae){
			fail("Should not reach this point.");
		}
		assert(true);		
	}
	
	/**
	 * Test method for {@link com.ericsson.eniq.afj.database.AFJDatabaseHandler#commitTransaction(boolean)}.
	 * May need to revisit this for logic check.
	 */
	@Test
	public void testCommitTransactionFalse() throws Exception{		
		statement.executeUpdate("INSERT INTO META_DATABASES VALUES"
				+ "('sa', 'v1.2', 'USER', 1, 'dwhrep', 'jdbc:hsqldb:mem:testdb', '', 'desc', "
				+ "'org.hsqldb.jdbcDriver', 'db')");			
		
		final RockFactory rockFactory = (RockFactory) objectUnderTest.getDwhrep();		
		final Meta_databases md = new Meta_databases(rockFactory);
		md.setUsername("sa");
		md.setVersion_number("v1.2");
		md.setType_name("TESTUSER");
		md.setDescription("Test db");
		md.setDriver_name("jdbc:hsqldb:mem:testdb");		
		md.insertDB();		
		try{
		objectUnderTest.commitTransaction(false);		
		}
		catch(AFJException ae){
			fail("Should not reach this point.");
		}
		assert(true);		
	}
	
	/**
	 * Test method for {@link com.ericsson.eniq.afj.database.AFJDatabaseHandler#closeConnection(final Connection con)}.
	 */	
	@Test
	@Ignore
	public void testCloseConnection() throws Exception{
		/* Inserting META_DATABASE data for dwhRockFactory */
		statement.executeUpdate("INSERT INTO META_DATABASES VALUES"
				+ "('sa', 'v1.2', 'DBA', 1, 'dwh', 'jdbc:hsqldb:mem:testdb', '', 'desc', "
				+ "'org.hsqldb.jdbcDriver', 'db')");

		/* Reflecting the tested method */
		final Class pcClass = objectUnderTest.getClass();
		final Method closeConnection = pcClass.getDeclaredMethod("closeConnection", new Class[] {Connection.class});
		closeConnection.setAccessible(true);	
		closeConnection.invoke(objectUnderTest, new Object[] {connection});
		assert(true);
	}
	
}
