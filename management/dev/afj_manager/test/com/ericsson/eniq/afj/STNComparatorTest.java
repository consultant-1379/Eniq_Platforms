/**
 * 
 */
package com.ericsson.eniq.afj;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ericsson.eniq.afj.common.AFJMeasurementCounter;
import com.ericsson.eniq.afj.common.AFJMeasurementType;
import com.ericsson.eniq.afj.schema.CounterType;
import com.ericsson.eniq.afj.schema.DataType;
import com.ericsson.eniq.afj.schema.GroupType;
import com.ericsson.eniq.afj.schema.MocType;
import com.ericsson.eniq.afj.schema.PM;
import com.ericsson.eniq.afj.schema.CounterType.CollectionMethod;
import com.ericsson.eniq.afj.schema.CounterType.MeasurementResult;
import com.ericsson.eniq.afj.xml.STNComparator;

/**
 * @author esunbal
 *
 */
public class STNComparatorTest {
	
	private static STNComparator objectUnderTest;
	
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
		} catch (Exception e) {
			e.printStackTrace();
		}
		statement = connection.createStatement();
		
		statement.execute("CREATE TABLE META_DATABASES (USERNAME VARCHAR(31), VERSION_NUMBER VARCHAR(31), "
				+ "TYPE_NAME VARCHAR(31), CONNECTION_ID BIGINT, CONNECTION_NAME VARCHAR(31), "
				+ "CONNECTION_STRING VARCHAR(31), PASSWORD VARCHAR(31), DESCRIPTION VARCHAR(31), "
				+ "DRIVER_NAME VARCHAR(31), DB_LINK_NAME VARCHAR(31))");
		
		statement.executeUpdate("INSERT INTO META_DATABASES VALUES"
				+ "('sa', 'v1.2', 'USER', 1, 'dwhrep', 'jdbc:hsqldb:mem:testdb', '', 'desc', "
				+ "'org.hsqldb.jdbcDriver', 'db')");
		
		statement.execute("create table TPActivation (TECHPACK_NAME varchar(30) not null, STATUS varchar(10) not null,VERSIONID varchar(128) null,TYPE varchar(10) not null,MODIFIED INTEGER)");
		statement.execute("insert into TPActivation (TECHPACK_NAME,STATUS,VERSIONID,TYPE,MODIFIED) values ('DC_E_STN','ACTIVE','DC_E_STN:((2))','PM',1)");
		
		statement.execute("create table DefaultTags (TAGID varchar(50) not null, " +
				"DATAFORMATID varchar(100) not null, DESCRIPTION varchar(200) null)");

		statement.execute("alter table DefaultTags add primary key (TAGID, DATAFORMATID)");

		statement.execute("create table MeasurementCounter (TYPEID varchar(255) not null, " +
				"DATANAME varchar(128) not null, DESCRIPTION varchar(32000) null, " +
				"TIMEAGGREGATION varchar(50) null, GROUPAGGREGATION varchar(50) null, " +
				"COUNTAGGREGATION varchar(50) null, COLNUMBER numeric(9) null, DATATYPE varchar(50) null, " +
				"DATASIZE integer null, DATASCALE integer null, INCLUDESQL integer null, " +
				"UNIVOBJECT varchar(128) null, UNIVCLASS varchar(35) null, COUNTERTYPE varchar(16) null, COUNTERPROCESS varchar(16) null, DATAID varchar(255), FOLLOWJOHN integer null)");

		statement.execute("alter table MeasurementCounter add primary key (TYPEID, DATANAME)");

		/* Insert values into the tables*/
		statement.execute("insert into DefaultTags (TAGID,DATAFORMATID,DESCRIPTION) values ('E1Interface','DC_E_STN:((2)):DC_E_STN_E1INTERFACE:mdc','Default tags for DC_E_STN_E1INTERFACE in DC_E_STN:((2)) with format mdc.')");
		statement.execute("insert into MeasurementCounter (TYPEID,DATANAME) values ('DC_E_STN:((2)):DC_E_STN_E1INTERFACE','ifInErrors')");
		statement.execute("insert into MeasurementCounter (TYPEID,DATANAME) values ('DC_E_STN:((2)):DC_E_STN_E1INTERFACE','ifInOctets')");
		statement.execute("insert into MeasurementCounter (TYPEID,DATANAME) values ('DC_E_STN:((2)):DC_E_STN_E1INTERFACE','ifOutErrors')");
		statement.execute("insert into MeasurementCounter (TYPEID,DATANAME) values ('DC_E_STN:((2)):DC_E_STN_E1INTERFACE','ifOutOctets')");
		
		/* Create property file for database connection details */
		AFJManagerProperties = new File(System.getProperty("user.dir"), "AFJManager.properties");
		System.setProperty("CONF_DIR",System.getProperty("user.dir"));
		AFJManagerProperties.deleteOnExit();
		try {
			final PrintWriter pw = new PrintWriter(new FileWriter(AFJManagerProperties));
			pw.write("stn.name=DC_E_STN\n");
			pw.write("countertype.identity=IDENTITY\n");
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
          stmt.executeUpdate("SHUTDOWN");
        } finally {
          stmt.close();
        }
      } finally {
        connection.close();
      }
		  /* Cleaning up after test */		
		  objectUnderTest = null;
	  }
	
	  @Before
	  public void setUpBeforeTest() throws Exception {
		  /* Create STNComparator instance before every test and set up a ant project object for it. */
		  objectUnderTest = new STNComparator();
	  } 
	
	/**
	 * Test method for {@link com.ericsson.eniq.afj.xml.STNComparator#getMeasTypeDelta(java.lang.Object)}.
	 */
	@Test
	public void testGetMeasTypeDeltaForNewMeasType() throws Exception{	
		
		/* Setup the PM object*/
		
		final PM pmObject = new PM();
		final DataType dt = new DataType();
		
		final CollectionMethod cm = new CollectionMethod();
		cm.setType("CC");
		cm.setSubtype("SUM");
		
		final MeasurementResult mr = new MeasurementResult();
		mr.setResultType("decimal");
		
		final MocType mt1 = new MocType();
		mt1.setName("E1Interface");		
		final GroupType gt = new GroupType();
		final CounterType ct1 = new CounterType();
		ct1.setMeasurementName("ifInErrors");
		ct1.setCollectionMethod(cm);
		ct1.setMeasurementResult(mr);
		final CounterType ct2 = new CounterType();
		ct2.setMeasurementName("ifInOctets");
		ct2.setCollectionMethod(cm);
		ct2.setMeasurementResult(mr);
		final CounterType ct3 = new CounterType();
		ct3.setMeasurementName("ifOutErrors");		
		ct3.setCollectionMethod(cm);
		ct3.setMeasurementResult(mr);
		gt.getCounter().add(ct1);
		gt.getCounter().add(ct2);
		gt.getCounter().add(ct3);
		mt1.getGroup().add(gt);
		
		final MocType mt2 = new MocType();
		mt2.setName("NewMocName");		
		final GroupType gt1 = new GroupType();
		final CounterType ct4 = new CounterType();
		ct4.setMeasurementName("ifInErrors");
				
		ct4.setCollectionMethod(cm);
		ct4.setMeasurementResult(mr);
		gt1.getCounter().add(ct4);
		mt2.getGroup().add(gt1);			
		
		dt.getMoc().add(mt1);
		dt.getMoc().add(mt2);
		pmObject.setData(dt);
		
		final List<AFJMeasurementType> deltaList = objectUnderTest.getMeasTypeDelta(pmObject);
		if(deltaList.size() > 1){
			fail("Expecting only 1 MOC in delta.");
		}
		final String expectedMeasurementType = "DC_E_STN_NEWMOCNAME";
		final String actualMeasurementType = deltaList.get(0).getTypeName();		
		assertEquals(expectedMeasurementType.toUpperCase(), actualMeasurementType.trim());
	}
	
	
	/**
	 * Test method for {@link com.ericsson.eniq.afj.xml.STNComparator#getMeasTypeDelta(java.lang.Object)}.
	 */
	@Test
	public void testGetMeasTypeDeltaForNewMeasTypeWithIdentityCounters() throws Exception{	
		
		/* Setup the PM object*/
		
		final PM pmObject = new PM();
		final DataType dt = new DataType();
		
		final CollectionMethod cm = new CollectionMethod();
		cm.setType("IDENTITY");
		cm.setSubtype("SUM");
		
		final MeasurementResult mr = new MeasurementResult();
		mr.setResultType("decimal");
		
		final MocType mt1 = new MocType();
		mt1.setName("E1Interface");		
		final GroupType gt = new GroupType();
		final CounterType ct1 = new CounterType();
		ct1.setMeasurementName("ifInErrors");
		ct1.setCollectionMethod(cm);
		ct1.setMeasurementResult(mr);
		final CounterType ct2 = new CounterType();
		ct2.setMeasurementName("ifInOctets");
		ct2.setCollectionMethod(cm);
		ct2.setMeasurementResult(mr);
		final CounterType ct3 = new CounterType();
		ct3.setMeasurementName("ifOutErrors");		
		ct3.setCollectionMethod(cm);
		ct3.setMeasurementResult(mr);
		gt.getCounter().add(ct1);
		gt.getCounter().add(ct2);
		gt.getCounter().add(ct3);
		mt1.getGroup().add(gt);
		
		final MocType mt2 = new MocType();
		mt2.setName("NewMocName");		
		final GroupType gt1 = new GroupType();
		final CounterType ct4 = new CounterType();
		ct4.setMeasurementName("ifInErrors");
				
		ct4.setCollectionMethod(cm);
		ct4.setMeasurementResult(mr);
		gt1.getCounter().add(ct4);
		mt2.getGroup().add(gt1);			
		
		dt.getMoc().add(mt1);
		dt.getMoc().add(mt2);
		pmObject.setData(dt);
		
		final List<AFJMeasurementType> deltaList = objectUnderTest.getMeasTypeDelta(pmObject);
		final int expected = 0;
		final int actual = deltaList.size();		
		assertEquals(expected, actual);
	}
	
	/**
	 * Test method for {@link com.ericsson.eniq.afj.xml.STNComparator#getMeasTypeDelta(java.lang.Object)}.
	 */
	@Test
	public void testGetMeasTypeDeltaForNewMeasTypeWithMixedCounters() throws Exception{	
		
		/* Setup the PM object*/
		
		final PM pmObject = new PM();
		final DataType dt = new DataType();
		
		final CollectionMethod cm = new CollectionMethod();
		cm.setType("IDENTITY");
		cm.setSubtype("SUM");
		
		final CollectionMethod cm1 = new CollectionMethod();
		cm1.setType("CC");
		cm1.setSubtype("SUM");
		
		final MeasurementResult mr = new MeasurementResult();
		mr.setResultType("decimal");
		
		final MocType mt1 = new MocType();
		mt1.setName("E1Interface");		
		final GroupType gt = new GroupType();
		final CounterType ct1 = new CounterType();
		ct1.setMeasurementName("ifInErrors");
		ct1.setCollectionMethod(cm);
		ct1.setMeasurementResult(mr);
		final CounterType ct2 = new CounterType();
		ct2.setMeasurementName("ifInOctets");
		ct2.setCollectionMethod(cm);
		ct2.setMeasurementResult(mr);
		final CounterType ct3 = new CounterType();
		ct3.setMeasurementName("ifOutErrors");		
		ct3.setCollectionMethod(cm);
		ct3.setMeasurementResult(mr);
		gt.getCounter().add(ct1);
		gt.getCounter().add(ct2);
		gt.getCounter().add(ct3);
		mt1.getGroup().add(gt);
		
		final MocType mt2 = new MocType();
		mt2.setName("NewMocName");		
		final GroupType gt1 = new GroupType();
		final CounterType ct4 = new CounterType();
		ct4.setMeasurementName("ifInErrors");
				
		ct4.setCollectionMethod(cm1);
		ct4.setMeasurementResult(mr);
		gt1.getCounter().add(ct4);
		mt2.getGroup().add(gt1);			
		
		dt.getMoc().add(mt1);
		dt.getMoc().add(mt2);
		pmObject.setData(dt);
		
		final List<AFJMeasurementType> deltaList = objectUnderTest.getMeasTypeDelta(pmObject);
		
		final int expected = 1;
		// Expecting only 1 meas type mapped to one tag with only 1 counter (ifInErrors) in it.
		final int actual = deltaList.get(0).getTags().get(0).getNewCounters().size();		
		assertEquals(expected, actual);
	}
	
	/**
	 * Test method for {@link com.ericsson.eniq.afj.xml.STNComparator#getMeasTypeDelta(java.lang.Object)}.
	 */
	@Test
	public void testGetMeasTypeDeltaForNewCounters() throws Exception{	
		
		/* Setup the PM object*/
		
		final PM pmObject = new PM();
		final DataType dt = new DataType();
		final CollectionMethod cm = new CollectionMethod();
		cm.setType("CC");
		cm.setSubtype("SUM");
		
		final MeasurementResult mr = new MeasurementResult();
		mr.setResultType("decimal");
		
		final MocType mt1 = new MocType();
		mt1.setName("E1Interface");		
		final GroupType gt = new GroupType();
		final CounterType ct1 = new CounterType();
		ct1.setMeasurementName("ifInErrors");
		ct1.setCollectionMethod(cm);
		ct1.setMeasurementResult(mr);
		final CounterType ct2 = new CounterType();
		ct2.setMeasurementName("ifInOctets");
		ct2.setCollectionMethod(cm);
		ct2.setMeasurementResult(mr);
		final CounterType ct3 = new CounterType();
		ct3.setMeasurementName("NewCounter");
		ct3.setCollectionMethod(cm);
		ct3.setMeasurementResult(mr);
		gt.getCounter().add(ct1);
		gt.getCounter().add(ct2);
		gt.getCounter().add(ct3);
		mt1.getGroup().add(gt);			
		
		dt.getMoc().add(mt1);	
		pmObject.setData(dt);
		
		final List<AFJMeasurementType> deltaList = objectUnderTest.getMeasTypeDelta(pmObject);
		if(deltaList.size() > 1){
			fail("Expecting only 1 MOC in delta.");
		}
		final String expectedMeasurementType = "NewCounter";
		final String actualMeasurementType = deltaList.get(0).getTags().get(0).getNewCounters().get(0).getCounterName();		
		assertEquals(expectedMeasurementType, actualMeasurementType.trim());
	}

	/**
	 * Test method for {@link com.ericsson.eniq.afj.xml.STNComparator#getMeasTypeDelta(java.lang.Object)}.
	 */
	@Test
	public void testGetMeasTypeDeltaForNewCountersWithIdentityCounters() throws Exception{	
		
		/* Setup the PM object*/
		
		final PM pmObject = new PM();
		final DataType dt = new DataType();
		final CollectionMethod cm = new CollectionMethod();
		cm.setType("IDENTITY");
		cm.setSubtype("SUM");
		
		final MeasurementResult mr = new MeasurementResult();
		mr.setResultType("decimal");
		
		final MocType mt1 = new MocType();
		mt1.setName("E1Interface");		
		final GroupType gt = new GroupType();
		final CounterType ct1 = new CounterType();
		ct1.setMeasurementName("ifInErrors");
		ct1.setCollectionMethod(cm);
		ct1.setMeasurementResult(mr);
		final CounterType ct2 = new CounterType();
		ct2.setMeasurementName("ifInOctets");
		ct2.setCollectionMethod(cm);
		ct2.setMeasurementResult(mr);
		final CounterType ct3 = new CounterType();
		ct3.setMeasurementName("NewCounter");
		ct3.setCollectionMethod(cm);
		ct3.setMeasurementResult(mr);
		gt.getCounter().add(ct1);
		gt.getCounter().add(ct2);
		gt.getCounter().add(ct3);
		mt1.getGroup().add(gt);			
		
		dt.getMoc().add(mt1);	
		pmObject.setData(dt);
		
		final List<AFJMeasurementType> deltaList = objectUnderTest.getMeasTypeDelta(pmObject);
		final int expected = 0;
		final int actual = deltaList.size();
		assertEquals(expected, actual);
	}
	
	/**
	 * Test method for {@link com.ericsson.eniq.afj.xml.STNComparator#getMeasTypeDelta(java.lang.Object)}.
	 */
	@Test
	public void testGetMeasTypeDeltaForNewCountersWithMixedCounters() throws Exception{	
		
		/* Setup the PM object*/
		
		final PM pmObject = new PM();
		final DataType dt = new DataType();
		final CollectionMethod cm = new CollectionMethod();
		cm.setType("IDENTITY");
		cm.setSubtype("SUM");
		
		final CollectionMethod cm1 = new CollectionMethod();
		cm1.setType("CC");
		cm1.setSubtype("SUM");
		
		final MeasurementResult mr = new MeasurementResult();
		mr.setResultType("decimal");
		
		final MocType mt1 = new MocType();
		mt1.setName("E1Interface");		
		final GroupType gt = new GroupType();
		final CounterType ct1 = new CounterType();
		ct1.setMeasurementName("ifInErrors");
		ct1.setCollectionMethod(cm);
		ct1.setMeasurementResult(mr);
		final CounterType ct2 = new CounterType();
		ct2.setMeasurementName("ifInOctets");
		ct2.setCollectionMethod(cm);
		ct2.setMeasurementResult(mr);
		final CounterType ct3 = new CounterType();
		ct3.setMeasurementName("NewCounter");
		ct3.setCollectionMethod(cm1);
		ct3.setMeasurementResult(mr);
		gt.getCounter().add(ct1);
		gt.getCounter().add(ct2);
		gt.getCounter().add(ct3);
		mt1.getGroup().add(gt);			
		
		dt.getMoc().add(mt1);	
		pmObject.setData(dt);
		
		final List<AFJMeasurementType> deltaList = objectUnderTest.getMeasTypeDelta(pmObject);
		final int expected = 1;
		final int actual = deltaList.get(0).getTags().get(0).getNewCounters().size();
		assertEquals(expected, actual);
	}
	
	
	/**
	 * Test method for {@link com.ericsson.eniq.afj.xml.STNComparator#createCounterObject(com.ericsson.eniq.afj.schema.CounterType)}.
	 */
	@Test
	public void testCreateCounterObject() {
		final CounterType ct = new CounterType();
		ct.setMeasurementName("ifInErrors");
		ct.setMeasurementType("ifInErrors");
		ct.setDescription("Number of inbound packets that contained errors");
		ct.setStatus("current");
		final MeasurementResult mr = new MeasurementResult();
		mr.setResultType("decimal");
		final CollectionMethod cm = new CollectionMethod();
		cm.setType("CC");
		cm.setSubtype("SUM");
		ct.setCollectionMethod(cm);
		ct.setMeasurementResult(mr);
		
		final AFJMeasurementCounter actualOutput = objectUnderTest.createCounterObject(ct);
		final String actual = actualOutput.getCounterName().toUpperCase();
		final String expected = ct.getMeasurementName().toUpperCase();
		assertEquals(expected, actual);
	}
	
	
	/**
	 * Test method for {@link com.ericsson.eniq.afj.xml.STNComparator#preProcessInput(PM object)}.
	 */
	@Test
	public void testPreProcessInputDuplicateMocs() throws Exception{
		
		/* Make the PM object */
		
		final PM pmObject = new PM();
		final DataType dt = new DataType();
		final CollectionMethod cm = new CollectionMethod();
		cm.setType("CC");
		cm.setSubtype("SUM");
		
		final MeasurementResult mr = new MeasurementResult();
		mr.setResultType("decimal");
		
		
		final MocType mt1 = new MocType();
		mt1.setName("E1Interface");		
		final GroupType gt = new GroupType();
		final CounterType ct1 = new CounterType();
		ct1.setMeasurementName("ifInErrors");
		ct1.setCollectionMethod(cm);
		ct1.setMeasurementResult(mr);
		final CounterType ct2 = new CounterType();
		ct2.setMeasurementName("ifInOctets");
		ct2.setCollectionMethod(cm);
		ct2.setMeasurementResult(mr);
		final CounterType ct3 = new CounterType();
		ct3.setMeasurementName("Sundar");
		ct3.setCollectionMethod(cm);
		ct3.setMeasurementResult(mr);
		gt.getCounter().add(ct1);
		gt.getCounter().add(ct2);
		gt.getCounter().add(ct3);
		mt1.getGroup().add(gt);
		
		final MocType mt2 = new MocType();
		mt2.setName("E1Interface");		
		final GroupType gt1 = new GroupType();
		final CounterType ct4 = new CounterType();
		ct4.setMeasurementName("ifInErrors");
		ct4.setCollectionMethod(cm);
		ct4.setMeasurementResult(mr);
		gt1.getCounter().add(ct4);
		mt2.getGroup().add(gt1);			
		
		dt.getMoc().add(mt1);
		dt.getMoc().add(mt2);
		pmObject.setData(dt);
		
		/* Reflecting the tested method */
		final Class pcClass = objectUnderTest.getClass();
		final Method preProcessInput = pcClass.getDeclaredMethod("preProcessInput", new Class[] {PM.class});
		preProcessInput.setAccessible(true);	
		final String actualOutput = (String)preProcessInput.invoke(objectUnderTest, new Object[] {pmObject});
		final String expectedOuput = "\nDuplicate moc name specification:E1Interface";		
		assertEquals(expectedOuput, actualOutput);
	}

	/**
	 * Test method for {@link com.ericsson.eniq.afj.xml.STNComparator#preProcessInput(PM object)}.
	 */
	@Test
	public void testPreProcessInputDuplicateCounters() throws Exception{
		
		/* Make the PM object */
		
		final PM pmObject = new PM();
		final DataType dt = new DataType();
		final CollectionMethod cm = new CollectionMethod();
		cm.setType("CC");
		cm.setSubtype("SUM");
		
		final MeasurementResult mr = new MeasurementResult();
		mr.setResultType("decimal");
		
		
		final MocType mt1 = new MocType();
		mt1.setName("E1Interface");		
		final GroupType gt = new GroupType();
		final CounterType ct1 = new CounterType();
		ct1.setMeasurementName("ifInErrors");
		final CounterType ct2 = new CounterType();
		ct1.setCollectionMethod(cm);
		ct1.setMeasurementResult(mr);
		ct2.setMeasurementName("ifInOctets");
		ct2.setCollectionMethod(cm);
		ct2.setMeasurementResult(mr);
		final CounterType ct3 = new CounterType();
		ct3.setMeasurementName("ifInErrors");
		ct3.setCollectionMethod(cm);
		ct3.setMeasurementResult(mr);
		gt.getCounter().add(ct1);
		gt.getCounter().add(ct2);
		gt.getCounter().add(ct3);
		mt1.getGroup().add(gt);
		
		final MocType mt2 = new MocType();
		mt2.setName("E1Interface");		
		final GroupType gt1 = new GroupType();
		final CounterType ct4 = new CounterType();
		ct4.setMeasurementName("ifInErrors");
		ct4.setCollectionMethod(cm);
		ct4.setMeasurementResult(mr);
		gt1.getCounter().add(ct4);
		mt2.getGroup().add(gt1);	
		
		dt.getMoc().add(mt1);
		dt.getMoc().add(mt2);
		pmObject.setData(dt);
		
		/* Reflecting the tested method */
		final Class pcClass = objectUnderTest.getClass();
		final Method preProcessInput = pcClass.getDeclaredMethod("preProcessInput", new Class[] {PM.class});
		preProcessInput.setAccessible(true);	
		final String actualOutput = (String)preProcessInput.invoke(objectUnderTest, new Object[] {pmObject});
		final String expectedOuput = "\nDuplicate counter name specification:ifInErrors in the moc:E1Interface\nDuplicate moc name specification:E1Interface";	
		System.out.println("Actual Output:"+actualOutput);
		assertEquals(expectedOuput, actualOutput);
	}
	
	/**
	 * Test method for {@link com.ericsson.eniq.afj.xml.STNComparator#preProcessInput(PM object)}.
	 */
	@Test
	public void testPreProcessInputDuplicateMocsAndCounters() throws Exception{
		
		/* Make the PM object */
		
		final PM pmObject = new PM();
		final DataType dt = new DataType();
		
		final CollectionMethod cm = new CollectionMethod();
		cm.setType("CC");
		cm.setSubtype("SUM");
		
		final MeasurementResult mr = new MeasurementResult();
		mr.setResultType("decimal");
		
		final MocType mt1 = new MocType();
		mt1.setName("E1Interface");		
		final GroupType gt = new GroupType();
		final CounterType ct1 = new CounterType();
		ct1.setMeasurementName("ifInErrors");
		ct1.setCollectionMethod(cm);
		ct1.setMeasurementResult(mr);
		final CounterType ct2 = new CounterType();
		ct2.setMeasurementName("ifInOctets");
		ct2.setCollectionMethod(cm);
		ct2.setMeasurementResult(mr);
		final CounterType ct3 = new CounterType();
		ct3.setMeasurementName("ifInErrors");
		ct3.setCollectionMethod(cm);
		ct3.setMeasurementResult(mr);
		gt.getCounter().add(ct1);
		gt.getCounter().add(ct2);
		gt.getCounter().add(ct3);
		mt1.getGroup().add(gt);
		
		dt.getMoc().add(mt1);		
		pmObject.setData(dt);
		
		/* Reflecting the tested method */
		final Class pcClass = objectUnderTest.getClass();
		final Method preProcessInput = pcClass.getDeclaredMethod("preProcessInput", new Class[] {PM.class});
		preProcessInput.setAccessible(true);	
		final String actualOutput = (String)preProcessInput.invoke(objectUnderTest, new Object[] {pmObject});
		final String expectedOuput = "\nDuplicate counter name specification:ifInErrors in the moc:E1Interface";	
		System.out.println("Actual Output:"+actualOutput);
		assertEquals(expectedOuput, actualOutput);
	}
	
	/**
	 * Test method for {@link com.ericsson.eniq.afj.xml.STNComparator#sybaseNameCheck(String name)}.
	 */
	@Test
	public void testSybaseNameCheck() throws Exception{
		/* Reflecting the tested method */
		final Class pcClass = objectUnderTest.getClass();
		final Method sybaseNameCheck = pcClass.getDeclaredMethod("sybaseNameCheck", new Class[] {String.class});
		sybaseNameCheck.setAccessible(true);	
		final boolean actualOutput = (Boolean)sybaseNameCheck.invoke(objectUnderTest, new Object[] {"$Sundar"});
		final boolean expectedOuput = true;		
		assertEquals(expectedOuput, actualOutput);
	}
	
	/**
	 * Test method for {@link com.ericsson.eniq.afj.xml.STNComparator#sybaseNameCheck(String name)}.
	 */
	@Test
	public void testSybaseNameCheckMLPPP() throws Exception{
		/* Reflecting the tested method */
		final Class pcClass = objectUnderTest.getClass();
		final Method sybaseNameCheck = pcClass.getDeclaredMethod("sybaseNameCheck", new Class[] {String.class});
		sybaseNameCheck.setAccessible(true);	
		final boolean actualOutput = (Boolean)sybaseNameCheck.invoke(objectUnderTest, new Object[] {"ML-PPP"});
		final boolean expectedOuput = true;		
		assertEquals(expectedOuput, actualOutput);
	}
}
