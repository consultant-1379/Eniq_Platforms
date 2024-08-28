/**
 * 
 */
package com.ericsson.eniq.afj;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ericsson.eniq.afj.common.AFJMeasurementCounter;
import com.ericsson.eniq.afj.common.AFJMeasurementType;
import com.ericsson.eniq.afj.xml.HSSComparator;
import com.ericsson.eniq.afj.xml.MTASComparator;
import com.ericsson.ims.mim.CounterType;
import com.ericsson.ims.mim.GroupType;
import com.ericsson.ims.mim.MocType;
import com.ericsson.ims.mim.PM;
import com.ericsson.ims.mim.CounterType.CollectionMethod;
import com.ericsson.ims.mim.CounterType.MeasurementResult;
import com.ericsson.ims.mim.PM.Data;

/**
 * @author esunbal
 *
 */
public class HSSCompartorTest {
	
	private static HSSComparator objectUnderTest;
	
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
		statement.execute("insert into TPActivation (TECHPACK_NAME,STATUS,VERSIONID,TYPE,MODIFIED) values ('DC_E_HSS','ACTIVE','DC_E_HSS:((2))','PM',1)");
		
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
		statement.execute("insert into DefaultTags (TAGID,DATAFORMATID,DESCRIPTION) values ('HSS','DC_E_HSS:((2)):DC_E_HSS_HSS:mdc','Default tags for DC_E_HSS in DC_E_HSS:((2)) with format mdc.')");
		statement.execute("insert into MeasurementCounter (TYPEID,DATANAME) values ('DC_E_HSS:((2)):DC_E_HSS_HSS','CxPullAnswersDiaMissingAvp')");
		statement.execute("insert into MeasurementCounter (TYPEID,DATANAME) values ('DC_E_HSS:((2)):DC_E_HSS_HSS','CxPullAnswersDiaSuccess')");
		statement.execute("insert into MeasurementCounter (TYPEID,DATANAME) values ('DC_E_HSS:((2)):DC_E_HSS_HSS','CxPullAnswersDiaUnableToComply')");
		statement.execute("insert into MeasurementCounter (TYPEID,DATANAME) values ('DC_E_HSS:((2)):DC_E_HSS_HSS','CxPutAnswersDiaErrIdentitiesDontMatch')");
		
		/* Create property file for database connection details */
		AFJManagerProperties = new File(System.getProperty("user.dir"), "AFJManager.properties");
		System.setProperty("CONF_DIR",System.getProperty("user.dir"));
		AFJManagerProperties.deleteOnExit();
		try {
			final PrintWriter pw = new PrintWriter(new FileWriter(AFJManagerProperties));
			pw.write("hss.name=DC_E_HSS\n");
			pw.write("DC_E_HSS.parser=mdc\n");
			pw.write("hss.SDA\\ Counters=HssSh\n");
			pw.write("hss.ISM\\ Counters=HSS\n");			
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

	/**
	 * @throws java.lang.Exception
	 */
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

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		  /* Create IMSComparator instance before every test and set up a ant project object for it. */
		  objectUnderTest = new HSSComparator();
	  
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		objectUnderTest = null;
	}

	/**
	 * Test method for {@link com.ericsson.eniq.afj.xml.IMSComparator#getMeasTypeDelta(java.lang.Object)}.
	 */
	@Test
	public void testGetMeasTypeDelta() throws Exception{	
		
		/* Setup the PM object*/
		
		final PM pmObject = new PM();
		final Data dt = new Data();
		final CollectionMethod cm = new CollectionMethod();
		cm.setType("IDENTITY");
		cm.setSubtype("SUM");
		
		final CollectionMethod cm1 = new CollectionMethod();
		cm1.setType("CC");
		cm1.setSubtype("SUM");
		
		final MeasurementResult mr = new MeasurementResult();
		mr.setResultType("decimal");
		
		final MocType mt1 = new MocType();
		mt1.setName("ISM Counters");		
		final GroupType gt = new GroupType();
		final CounterType ct1 = new CounterType();
		ct1.setMeasurementName("CxPullAnswersDiaMissingAvp");
		ct1.setCollectionMethod(cm);
		ct1.setMeasurementResult(mr);
		final CounterType ct2 = new CounterType();
		ct2.setMeasurementName("CxPullAnswersDiaSuccess");
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
	 * Test method for {@link com.ericsson.eniq.afj.xml.IMSComparator#getMeasTypeDelta(java.lang.Object)}.
	 */
	@Test
	public void testGetMeasTypeDeltaMocUnmapped() throws Exception{	
		
		/* Setup the PM object*/
		
		final PM pmObject = new PM();
		final Data dt = new Data();
		final CollectionMethod cm = new CollectionMethod();
		cm.setType("IDENTITY");
		cm.setSubtype("SUM");
		
		final CollectionMethod cm1 = new CollectionMethod();
		cm1.setType("CC");
		cm1.setSubtype("SUM");
		
		final MeasurementResult mr = new MeasurementResult();
		mr.setResultType("decimal");
		
		final MocType mt1 = new MocType();
		mt1.setName("ABC Counters");		
		final GroupType gt = new GroupType();
		final CounterType ct1 = new CounterType();
		ct1.setMeasurementName("CxPullAnswersDiaMissingAvp");
		ct1.setCollectionMethod(cm);
		ct1.setMeasurementResult(mr);
		final CounterType ct2 = new CounterType();
		ct2.setMeasurementName("CxPullAnswersDiaSuccess");
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
		final int expected = 0;
		final int actual = deltaList.size();
		assertEquals(expected, actual);
	}


	/**
	 * Test method for {@link com.ericsson.eniq.afj.xml.IMSComparator#createCounterObject(com.ericsson.ims.mim.CounterType)}.
	 */
	@Test
	public void testCreateCounterObject() {
		final CounterType ct = new CounterType();
		ct.setMeasurementName("CxPullAnswersDiaSuccess");
		ct.setMeasurementType("CxPullAnswersDiaSuccess");
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
	 * Test method for {@link com.ericsson.eniq.afj.xml.IMSComparator#preProcessInput(PM object)}.
	 */
	@Test
	public void testPreProcessInput() throws Exception{
		
		/* Make the PM object */
		
		final PM pmObject = new PM();
		final Data dt = new Data();
		final CollectionMethod cm = new CollectionMethod();
		cm.setType("CC");
		cm.setSubtype("SUM");
		
		final MeasurementResult mr = new MeasurementResult();
		mr.setResultType("decimal");
		
		final CollectionMethod wrongCM = new CollectionMethod();
		wrongCM.setType("abc");
		wrongCM.setSubtype("def");
		
		final MeasurementResult wrongMR = new MeasurementResult();
		wrongMR.setResultType("xyz");
		
		
		final MocType mt1 = new MocType();
		mt1.setName("ISM Counters");		
		final GroupType gt = new GroupType();
		final CounterType ct1 = new CounterType();
		ct1.setMeasurementName("CxPullAnswersDiaSuccess");
		final CounterType ct2 = new CounterType();
		ct1.setCollectionMethod(cm);
		ct1.setMeasurementResult(mr);
		ct2.setMeasurementName("cscfACABackupabcdefgcscfACABackupabcdefgcscfACABackupabcdefgcscfACABackupabcdefgcscfACABackupabcdefgcscfACABackupabcdefgcscfACABackupabcdefg");
		ct2.setCollectionMethod(cm);
		ct2.setMeasurementResult(mr);
		final CounterType ct3 = new CounterType();
		ct3.setMeasurementName("CxPullAnswersDiaSuccess");
		ct3.setCollectionMethod(cm);
		ct3.setMeasurementResult(mr);
		final CounterType ct4 = new CounterType();
		ct4.setMeasurementName("\nCxPullAnswersDiaSuccess");
		ct4.setCollectionMethod(cm);
		ct4.setMeasurementResult(mr);
		gt.getCounter().add(ct1);
		gt.getCounter().add(ct2);
		gt.getCounter().add(ct3);
		gt.getCounter().add(ct4);
		mt1.getGroup().add(gt);
		
		final MocType mt2 = new MocType();
		mt2.setName("ISM Counters");		
		final GroupType gt1 = new GroupType();
		final CounterType ct5 = new CounterType();
		ct5.setMeasurementName("CxPutAnswersDiaErrIdentitiesDontMatch");
		ct5.setCollectionMethod(wrongCM);
		ct5.setMeasurementResult(wrongMR);
		gt1.getCounter().add(ct5);
		mt2.getGroup().add(gt1);	
		
		dt.getMoc().add(mt1);
		dt.getMoc().add(mt2);
		pmObject.setData(dt);
		
		/* Reflecting the tested method */
		final Class pcClass = objectUnderTest.getClass();
		final Method preProcessInput = pcClass.getDeclaredMethod("preProcessInput", new Class[] {PM.class});
		preProcessInput.setAccessible(true);	
		final String actualOutput = (String)preProcessInput.invoke(objectUnderTest, new Object[] {pmObject});
		final String expectedOuput = "\nThe character length exceeds Sybase IQ limitation of 128:cscfACABackupabcdefgcscfACABackupabcdefgcscfACABackupabcdefgcscfACABackupabcdefgcscfACABackupabcdefgcscfACABackupabcdefgcscfACABackupabcdefg" +
				"\nDuplicate counter name specification:CxPullAnswersDiaSuccess in the moc:ISM Counters" +
				"\nInvalid counter name:" +
				"\nCxPullAnswersDiaSuccess. Name must begin with a letter (A through Z), underscore (_), at sign (@), dollar sign ($), or pound sign (#)" +
				"\nDuplicate moc name specification:ISM Counters\nInvalid type:abc in counter:CxPutAnswersDiaErrIdentitiesDontMatch of moc:ISM Counters" +
				"\nInvalid subType:def in counter:CxPutAnswersDiaErrIdentitiesDontMatch of moc:ISM Counters" +
				"\nInvalid resultType:xyz in counter:CxPutAnswersDiaErrIdentitiesDontMatch of moc:ISM Counters";
		assertEquals(expectedOuput, actualOutput);
	}

}
