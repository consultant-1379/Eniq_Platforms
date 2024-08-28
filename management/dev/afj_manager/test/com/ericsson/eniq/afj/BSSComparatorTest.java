/**
 * 
 */
package com.ericsson.eniq.afj;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ericsson.eniq.afj.common.AFJMeasurementCounter;
import com.ericsson.eniq.afj.common.AFJMeasurementType;
import com.ericsson.eniq.afj.common.PropertiesUtility;
import com.ericsson.eniq.afj.schema.CounterType;
import com.ericsson.eniq.afj.schema.DataType;
import com.ericsson.eniq.afj.schema.GroupType;
import com.ericsson.eniq.afj.schema.MocType;
import com.ericsson.eniq.afj.schema.PM;
import com.ericsson.eniq.afj.schema.CounterType.CollectionMethod;
import com.ericsson.eniq.afj.schema.CounterType.MeasurementResult;
import com.ericsson.eniq.afj.xml.BSSComparator;
import com.ericsson.eniq.exception.AFJException;

/**
 * @author esunbal
 *
 */
public class BSSComparatorTest {
	
	private static BSSComparator objectUnderTest;
	
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
		
		statement.execute("create table TPActivation (TECHPACK_NAME varchar(30) not null, STATUS varchar(10) not null,VERSIONID varchar(128) null,TYPE varchar(30) not null,MODIFIED INTEGER)");
		statement.execute("insert into TPActivation (TECHPACK_NAME,STATUS,VERSIONID,TYPE,MODIFIED) values ('DC_E_BSS','ACTIVE','DC_E_BSS:((16))','PM',1)");
		statement.execute("insert into TPActivation (TECHPACK_NAME,STATUS,VERSIONID,TYPE,MODIFIED) values ('DC_E_CMN_STS','ACTIVE','DC_E_CMN_STS:((2))','PM',1)");
		
		statement.execute("create table DefaultTags (TAGID varchar(80) not null, " +
				"DATAFORMATID varchar(200) not null, DESCRIPTION varchar(200) null)");

		statement.execute("alter table DefaultTags add primary key (TAGID, DATAFORMATID)");

		statement.execute("create table MeasurementCounter (TYPEID varchar(255) not null, " +
				"DATANAME varchar(128) not null, DESCRIPTION varchar(32000) null, " +
				"TIMEAGGREGATION varchar(80) null, GROUPAGGREGATION varchar(80) null, " +
				"COUNTAGGREGATION varchar(80) null, COLNUMBER numeric(20) null, DATATYPE varchar(80) null, " +
				"DATASIZE integer null, DATASCALE integer null, INCLUDESQL integer null, " +
				"UNIVOBJECT varchar(128) null, UNIVCLASS varchar(50) null, COUNTERTYPE varchar(30) null, COUNTERPROCESS varchar(30) null, DATAID varchar(255), FOLLOWJOHN integer null)");

		statement.execute("alter table MeasurementCounter add primary key (TYPEID, DATANAME)");

		/* Insert values into the tables*/
		statement.execute("insert into DefaultTags (TAGID,DATAFORMATID,DESCRIPTION) values ('ATERTRANS','DC_E_BSS:((16)):DC_E_BSS_ATERTRANS:eniqasn1','Default tags for DC_E_BSS_ATERTRANS in DC_E_BSS:((16)) with format eniqasn1.')");
		statement.execute("insert into DefaultTags (TAGID,DATAFORMATID,DESCRIPTION) values ('PGWLDIST','DC_E_BSS:((16)):DC_E_BSS_BSC:eniqasn1','Default tags for DC_E_BSS_BSC in DC_E_BSS:((16)) with format eniqasn1.')");
		statement.execute("insert into DefaultTags (TAGID,DATAFORMATID,DESCRIPTION) values ('NUCRELGPRS','DC_E_BSS:((16)):DC_E_BSS_CELL_UTRAN_ADJ:eniqasn1','Default tags for DC_E_BSS_CELL_UTRAN_ADJ in DC_E_BSS:((16)) with format eniqasn1.')");
		
		statement.execute("insert into MeasurementCounter (TYPEID,DATANAME) values ('DC_E_BSS:((16)):DC_E_BSS_ATERTRANS','ALLOCATERATP')");
		statement.execute("insert into MeasurementCounter (TYPEID,DATANAME) values ('DC_E_BSS:((16)):DC_E_BSS_ATERTRANS','ATERCONG')");
		statement.execute("insert into MeasurementCounter (TYPEID,DATANAME) values ('DC_E_BSS:((16)):DC_E_BSS_ATERTRANS','AVATERTRCDEV')");
		
		statement.execute("insert into MeasurementCounter (TYPEID,DATANAME) values ('DC_E_BSS:((16)):DC_E_BSS_BSC','BSC_BSCCUMMS')");
		statement.execute("insert into MeasurementCounter (TYPEID,DATANAME) values ('DC_E_BSS:((16)):DC_E_BSS_BSC','BSC_BSCMAXMS')");
		statement.execute("insert into MeasurementCounter (TYPEID,DATANAME) values ('DC_E_BSS:((16)):DC_E_BSS_BSC','BSC_GSM1800CUMMS')");
		
		statement.execute("insert into MeasurementCounter (TYPEID,DATANAME) values ('DC_E_BSS:((16)):DC_E_BSS_CELL_UTRAN_ADJ','HOATTSHOULDUTRAN')");
		statement.execute("insert into MeasurementCounter (TYPEID,DATANAME) values ('DC_E_BSS:((16)):DC_E_BSS_CELL_UTRAN_ADJ','HOREQCNTUTRAN')");
		statement.execute("insert into MeasurementCounter (TYPEID,DATANAME) values ('DC_E_BSS:((16)):DC_E_BSS_CELL_UTRAN_ADJ','HORTTOCHUTRAN')");
		
		
		/* Create the etlrep tables*/
		
		statement.execute("CREATE TABLE META_COLLECTION_SETS (COLLECTION_SET_ID VARCHAR(50),COLLECTION_SET_NAME VARCHAR(50), "
                + "DESCRIPTION VARCHAR(50), VERSION_NUMBER VARCHAR(50), ENABLED_FLAG VARCHAR(50), TYPE VARCHAR(50))");
		
		statement.execute("insert into META_COLLECTION_SETS (collection_set_id,collection_set_name,version_number) values (10,'INTF_DC_E_BSS_APG','((110))')");		
		
		statement.execute("CREATE TABLE META_TRANSFER_ACTIONS (VERSION_NUMBER VARCHAR(50), TRANSFER_ACTION_ID BIGINT, "
                + "COLLECTION_ID BIGINT, COLLECTION_SET_ID BIGINT, ACTION_TYPE VARCHAR(50), TRANSFER_ACTION_NAME VARCHAR(50), "
                + "ORDER_BY_NO BIGINT, DESCRIPTION VARCHAR(50), ENABLED_FLAG VARCHAR(50), CONNECTION_ID BIGINT, "
                + "WHERE_CLAUSE_02 VARCHAR(50), WHERE_CLAUSE_03 VARCHAR(50), ACTION_CONTENTS_03 VARCHAR(50), "
                + "ACTION_CONTENTS_02 VARCHAR(50), ACTION_CONTENTS_01 VARCHAR(50), WHERE_CLAUSE_01 VARCHAR(50))");	
		
		statement.execute("insert into META_TRANSFER_ACTIONS (COLLECTION_ID,COLLECTION_SET_ID,TRANSFER_ACTION_ID,VERSION_NUMBER,ACTION_TYPE,ACTION_CONTENTS_01) values (404,10,2655,'((110))','parse','addVendorIDTo=BSC,BSCAMSG,BSCGEN')");
		
		/* Create property file for database connection details */
		AFJManagerProperties = new File(System.getProperty("user.dir"), "AFJManager.properties");
		System.setProperty("CONF_DIR",System.getProperty("user.dir"));
		AFJManagerProperties.deleteOnExit();
		try {
			final PrintWriter pw = new PrintWriter(new FileWriter(AFJManagerProperties));
			pw.write("bss.name=DC_E_BSS\n");
			pw.write("sts.name=DC_E_CMN_STS\n");
			pw.write("bss.interface=INTF_DC_E_BSS_APG\n");
			pw.write("bss.actiontype=parse\n");
			pw.write("bss.addvendoridto=addVendorIDTo\n");
			pw.write("bss.moclevel=1\n");
			pw.write("countertype.identity=IDENTITY\n");
			pw.write("bss.specialmultiplemocs=SCABISDEL\n");
			pw.write("sts.moclevel=0");

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
			/* Create BSSComparator instance before every test and set up a ant project object for it. */
			objectUnderTest = new BSSComparator();			
	    PropertiesUtility.setPropertiesUtility(null);

		} 
	/**
	 * Test method for {@link com.ericsson.eniq.afj.xml.BSSComparator#getMeasTypeDelta(java.lang.Object)}.
	 */
	@Test
	public void testGetMeasTypeDelta() throws AFJException{
		final PM pmObject = new PM();
		final DataType dt = new DataType();
		
		final MocType mt1 = new MocType();
		mt1.setName("NewMocName");
		mt1.setMocLevel(new BigInteger("1"));
		final GroupType gt = new GroupType();
		final CounterType ct1 = new CounterType();
		ct1.setMeasurementName("ALLOCATERATP");
		final MeasurementResult mr = new MeasurementResult();
		mr.setResultType("decimal");
		final CollectionMethod cm = new CollectionMethod();
		cm.setType("CC");
		cm.setSubtype("SUM");
		ct1.setCollectionMethod(cm);
		ct1.setMeasurementResult(mr);
		gt.getCounter().add(ct1);
		mt1.getGroup().add(gt);		
		
		dt.getMoc().add(mt1);		
		pmObject.setData(dt);
		
		final List<AFJMeasurementType> deltaList = objectUnderTest.getMeasTypeDelta(pmObject);
		if(deltaList.size() > 1){
			fail("Was expecting only 1 moc for BSS.");
		}
		
		final String actualMeasurementType = deltaList.get(0).getTypeName();
		System.out.println("Actual meastype:"+actualMeasurementType);
		final String expectedMeasurementType = "DC_E_BSS_NEWMOCNAME";
		assertEquals(expectedMeasurementType,actualMeasurementType);
		
	}
	
	/**
	 * Test method for {@link com.ericsson.eniq.afj.xml.BSSComparator#getMeasTypeDelta(java.lang.Object)}.
	 */
	@Test
	public void testGetMeasTypeDeltaForNewCounters() throws AFJException{
		final PM pmObject = new PM();
		final DataType dt = new DataType();
		
		final MocType mt1 = new MocType();
		mt1.setName("ATERTRANS");
		mt1.setMocLevel(new BigInteger("1"));
		final GroupType gt = new GroupType();
		final CounterType ct1 = new CounterType();
		ct1.setMeasurementName("ABC");
		final MeasurementResult mr = new MeasurementResult();
		mr.setResultType("decimal");
		final CollectionMethod cm = new CollectionMethod();
		cm.setType("CC");
		cm.setSubtype("SUM");
		ct1.setCollectionMethod(cm);
		ct1.setMeasurementResult(mr);
		gt.getCounter().add(ct1);
		mt1.getGroup().add(gt);		
		
		dt.getMoc().add(mt1);		
		pmObject.setData(dt);
		
		final List<AFJMeasurementType> deltaList = objectUnderTest.getMeasTypeDelta(pmObject);
		if(deltaList.size() > 1){
			fail("Was expecting only 1 moc for BSS.");
		}
		
		final String actualCounterName = deltaList.get(0).getTags().get(0).getNewCounters().get(0).getCounterName();
		final String expectedCounterName = "ABC";
		assertEquals(expectedCounterName,actualCounterName);
		
	}
	
	/**
	 * Test method for {@link com.ericsson.eniq.afj.xml.BSSComparator#getMeasTypeDelta(java.lang.Object)}.
	 */
	@Test
	public void testGetMeasTypeDeltaForNewCountersWithIdentityCounters() throws AFJException{
		final PM pmObject = new PM();
		final DataType dt = new DataType();
		
		final MocType mt1 = new MocType();
		mt1.setName("ATERTRANS");
		mt1.setMocLevel(new BigInteger("1"));
		final GroupType gt = new GroupType();
		final CounterType ct1 = new CounterType();
		ct1.setMeasurementName("ABC");
		final MeasurementResult mr = new MeasurementResult();
		mr.setResultType("decimal");
		final CollectionMethod cm = new CollectionMethod();
		cm.setType("IDENTITY");
		cm.setSubtype("SUM");
		ct1.setCollectionMethod(cm);
		ct1.setMeasurementResult(mr);
		gt.getCounter().add(ct1);
		mt1.getGroup().add(gt);		
		
		dt.getMoc().add(mt1);		
		pmObject.setData(dt);
		
		final List<AFJMeasurementType> deltaList = objectUnderTest.getMeasTypeDelta(pmObject);
		if(deltaList.size() > 1){
			fail("Was expecting only 1 moc for BSS.");
		}
		
		final int actual = deltaList.size();
		final int expected = 0;
		assertEquals(expected,actual);
		
	}
	
	/**
	 * Test method for {@link com.ericsson.eniq.afj.xml.BSSComparator#getMeasTypeDelta(java.lang.Object)}.
	 */
	@Test
	public void testGetMeasTypeDeltaIdentityCounter() throws AFJException{
		final PM pmObject = new PM();
		final DataType dt = new DataType();
		
		final MocType mt1 = new MocType();
		mt1.setName("NewMocName");
		mt1.setMocLevel(new BigInteger("1"));
		final GroupType gt = new GroupType();
		final CounterType ct1 = new CounterType();
		ct1.setMeasurementName("ALLOCATERATP");
		final MeasurementResult mr = new MeasurementResult();
		mr.setResultType("decimal");
		final CollectionMethod cm = new CollectionMethod();
		cm.setType("IDENTITY");
		cm.setSubtype("SUM");
		ct1.setCollectionMethod(cm);
		ct1.setMeasurementResult(mr);
		gt.getCounter().add(ct1);
		mt1.getGroup().add(gt);		
		
		dt.getMoc().add(mt1);		
		pmObject.setData(dt);
		
		final List<AFJMeasurementType> deltaList = objectUnderTest.getMeasTypeDelta(pmObject);
		final int expected = 0;
		final int actual = deltaList.size();
		assertEquals(expected,actual);
		
	}

	/**
	 * Test method for {@link com.ericsson.eniq.afj.xml.BSSComparator#createCounterObject(com.ericsson.eniq.afj.schema.CounterType)}.
	 */
	@Test
	public void testCreateCounterObjectCounterType() {
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
	 * Test method for {@link com.ericsson.eniq.afj.xml.BSSComparator#createCounterObject(com.ericsson.eniq.afj.schema.CounterType, java.lang.String)}.
	 */
	@Test
	public void testCreateCounterObjectCounterTypeString() throws Exception{
		
		final Field addVendorIdList = objectUnderTest.getClass().getDeclaredField("addVendorIdList");
		addVendorIdList.setAccessible(true);
		final List<String> mockedValues = new ArrayList<String>();
		mockedValues.add("PGWLDIST");
		addVendorIdList.set(objectUnderTest, mockedValues);
		
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
		
		final AFJMeasurementCounter actualOutput = objectUnderTest.createCounterObject(ct,"PGWLDIST");
		System.out.println("Actual Output:"+actualOutput.getCounterName());
		if(actualOutput.getCounterName().equalsIgnoreCase("PGWLDIST_ifInErrors") && actualOutput.isCounterNew()){
			assert(true);
		}
		else{
			fail("Sundar");
		}
	}

	/**
	 * Test method for {@link com.ericsson.eniq.afj.xml.BSSComparator#isMeasTypeSingle(java.lang.String)}.
	 */
	@Test
	public void testIsMeasTypeSingle() throws Exception{	
		
		final Field addVendorIdList = objectUnderTest.getClass().getDeclaredField("addVendorIdList");
		addVendorIdList.setAccessible(true);
		final List<String> mockedValues = new ArrayList<String>();
		mockedValues.add("PGWLDIST");
		addVendorIdList.set(objectUnderTest, mockedValues);
		
		final boolean actualValue = objectUnderTest.isMeasTypeSingle("PGWLDIST");
		final boolean expectedValue = false;
		
		assertEquals(expectedValue, actualValue);
	}

	/**
	 * Test method for {@link com.ericsson.eniq.afj.xml.BSSComparator#isCounterPresent(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testIsCounterPresent() throws Exception{
		
		/* Set the instance variables*/
		final Field tagToCountersMap = objectUnderTest.getClass().getDeclaredField("tagToCountersMap");
		tagToCountersMap.setAccessible(true);
		final Map<String, List<String>> mockedTagMap = new HashMap<String, List<String>>();
		final List<String> counterList = new ArrayList<String>();
		counterList.add("ALLOCATERATP");
		counterList.add("ATERCONG");
		counterList.add("AVATERTRCDEV");		
		mockedTagMap.put("DC_E_BSS:ATERTRANS", counterList);		
		tagToCountersMap.set(objectUnderTest, mockedTagMap);
		final boolean actualValue = objectUnderTest.isCounterPresent("DC_E_BSS:", "ATERTRANS", "ATERCONG");
		assertEquals(true, actualValue);
	}
	
	/**
	 * Test method for {@link com.ericsson.eniq.afj.xml.BSSComparator#getSpecialMultipleMocs()}.
	 */
	@Test
	public void testGetSpecialMultipleMocs() throws Exception{
		
		/* Reflecting the tested method */
		final Class pcClass = objectUnderTest.getClass();
		final Method getSpecialMultipleMocs = pcClass.getDeclaredMethod("getSpecialMultipleMocs", new Class[] {});
		getSpecialMultipleMocs.setAccessible(true);	
		final List<String> actualOutput = (List<String>)getSpecialMultipleMocs.invoke(objectUnderTest, new Object[] {});
		final int expectedOuput = 1;		
		assertEquals(expectedOuput, actualOutput.size());
	}

}
