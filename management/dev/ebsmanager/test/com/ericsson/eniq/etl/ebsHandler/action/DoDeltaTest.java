package com.ericsson.eniq.etl.ebsHandler.action;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import ssc.rockfactory.RockFactory;

public class DoDeltaTest {

	private static DoDelta objectUnderTest;	
	private static List<String> handledTables;
	
	private static Connection connection;	
	private static Statement statement;
	Logger log = Logger.getLogger("DoDeltaTest");
	
	@BeforeClass
	public static void init() {
		try {
			objectUnderTest = new DoDelta(Logger.getLogger("DoDelta"), null, null);
			Class.forName("org.hsqldb.jdbcDriver").newInstance();
			connection = DriverManager.getConnection("jdbc:hsqldb:mem:testdb", "sa", "");
			statement = connection.createStatement();
			statement.execute("CREATE TABLE Universeparameters ( VERSIONID VARCHAR(31)  ,CLASSNAME VARCHAR(31) ,OBJECTNAME VARCHAR(62) ,UNIVERSEEXTENSION VARCHAR(31) ,ORDERNRO INTEGER  ,NAME VARCHAR(31) ,TYPENAME VARCHAR(31))");
			statement.execute("CREATE TABLE Universecomputedobject ( VERSIONID VARCHAR(31)  ,CLASSNAME VARCHAR(31) ,UNIVERSEEXTENSION VARCHAR(31) ,OBJECTNAME VARCHAR(62) ,DESCRIPTION VARCHAR(31) ,OBJECTTYPE VARCHAR(31) ,QUALIFICATION VARCHAR(31) ,AGGREGATION VARCHAR(31) ,OBJSELECT VARCHAR(31) ,OBJWHERE VARCHAR(31) ,PROMPTHIERARCHY VARCHAR(31) ,OBJ_BH_REL INTEGER  ,ELEM_BH_REL INTEGER  ,INHERITANCE INTEGER  ,ORDERNRO BIGINT )");
			statement.execute("CREATE TABLE Universeclass ( VERSIONID VARCHAR(31)  ,CLASSNAME VARCHAR(31) ,UNIVERSEEXTENSION VARCHAR(31) ,DESCRIPTION VARCHAR(31) ,PARENT VARCHAR(31) ,OBJ_BH_REL INTEGER  ,ELEM_BH_REL INTEGER  ,INHERITANCE INTEGER  ,ORDERNRO BIGINT )");
			statement.execute("CREATE TABLE Measurementtypeclass ( TYPECLASSID VARCHAR(31)  ,VERSIONID VARCHAR(31) ,DESCRIPTION VARCHAR(31))");
			statement.execute("CREATE TABLE Measurementtype ( TYPEID VARCHAR(62)  ,TYPECLASSID VARCHAR(62) ,TYPENAME VARCHAR(62) ,VENDORID VARCHAR(62) ,FOLDERNAME VARCHAR(62) ,DESCRIPTION VARCHAR(62) ,STATUS BIGINT  ,VERSIONID VARCHAR(62) ,OBJECTID VARCHAR(62) ,OBJECTNAME VARCHAR(31) ,OBJECTVERSION INTEGER  ,OBJECTTYPE VARCHAR(31) ,JOINABLE VARCHAR(31) ,SIZING VARCHAR(31) ,TOTALAGG INTEGER  ,ELEMENTBHSUPPORT INTEGER  ,RANKINGTABLE INTEGER  ,DELTACALCSUPPORT INTEGER  ,PLAINTABLE INTEGER  ,UNIVERSEEXTENSION VARCHAR(62) ,VECTORSUPPORT INTEGER  ,DATAFORMATSUPPORT INTEGER  ,FOLLOWJOHN INTEGER ,ONEMINAGG INTEGER, FIFTEENMINAGG INTEGER, EVENTSCALCTABLE INTEGER, MIXEDPARTITIONSTABLE INTEGER, LOADFILE_DUP_CHECK INTEGER, SONAGG int, SONFIFTEENMINAGG int, ROPGRPCELL varchar(255))");
			statement.execute("CREATE TABLE Measurementcounter ( TYPEID VARCHAR(62)  ,DATANAME VARCHAR(62) ,DESCRIPTION VARCHAR(62) ,TIMEAGGREGATION VARCHAR(62) ,GROUPAGGREGATION VARCHAR(62) ,COUNTAGGREGATION VARCHAR(62) ,COLNUMBER BIGINT  ,DATATYPE VARCHAR(62) ,DATASIZE INTEGER  ,DATASCALE INTEGER  ,INCLUDESQL INTEGER  ,UNIVOBJECT VARCHAR(62) ,UNIVCLASS VARCHAR(62) ,COUNTERTYPE VARCHAR(62) ,COUNTERPROCESS VARCHAR(62) ,DATAID VARCHAR(62) ,FOLLOWJOHN INTEGER )");
			
			statement.execute("insert into Universeparameters values('PM_E_EBSW:((2))','cell.CounterGroup1','Call Drop, CN Initiated, Abnormal Cause','ALL',0,'c15','PM_E_EBSW_CELL')");
			statement.execute("insert into Universecomputedobject values('PM_E_EBSW:((2))','cell.CounterGroup1','ALL','Call Drop, CN Initiated, Abnormal Cause','','number','dimension','','IDENTITY','','',0,0,0,0)");
			statement.execute("insert into Universeclass values('PM_E_EBSW:((2))','cell.CounterGroup1','ALL','','Computed Counters',0,0,0,0)");
			statement.execute("insert into MeasurementTypeClass values('PM_E_EBSW:((2)):PM_E_EBSW_CELL','PM_E_EBSW:((2))','CELL')");			
			statement.execute("insert into MeasurementType values( 'PM_E_EBSW:((2)):PM_E_EBSW_CELL', 'PM_E_EBSW:((2)):PM_E_EBSW_CELL', 'DC_E_STN_E1INTERFACE', 'DC_E_STN', 'PM_E_EBSW_CELL', 'This MO is created by the junit test.', null, 'PM_E_EBSW:((2))', 'PM_E_EBSW:((2)):PM_E_EBSW_CELL', 'PM_E_EBSW_CELL', null, null, '', 'small', 1, 1, 0, 0, 0, 'b', 0, 1, 0, 1, 1, 1, 1, 1, 0, 0, 'test' )");
			statement.execute("insert into MeasurementCounter values('PM_E_EBSW:((2)):PM_E_EBSW_CELL','ifInErrors','Number of inbound packets that ...','SUM','SUM','PEG',102,'numeric',20,0,1,'ifInErrors',null,'PEG','PEG','ifInErrors',null)");
			
			
			
			handledTables = new Vector<String> (); 
			handledTables.add("Measurementcounter");
			handledTables.add("Universeformulas");
			handledTables.add("Universeclass");
			handledTables.add("Universecomputedobject");
			handledTables.add("Universeparameters");
			
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception " + e);
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
	
	@Test
	public void toString_nochange_Test() {

		String sourcexml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<techpack>\n\t<Measurementtype DATAFORMATSUPPORT=\"\" DELTACALCSUPPORT=\"\" DESCRIPTION=\"\" DiffStatus=\"\" ELEMENTBHSUPPORT=\"\" FOLDERNAME=\"\" JOINABLE=\"\" OBJECTID=\"\" OBJECTNAME=\"\" OBJECTTYPE=\"\" OBJECTVERSION=\"\" PLAINTABLE=\"\" RANKINGTABLE=\"\" SIZING=\"\" STATUS=\"\" TOTALAGG=\"\" TYPECLASSID=\"\" TYPEID=\"\" TYPENAME=\"&apos;cell&apos;\" UNIVERSEEXTENSION=\"\" VECTORSUPPORT=\"\" VENDORID=\"\" VERSIONID=\"\">\n\t\t<Measurementcounter COLNUMBER=\"\" COUNTAGGREGATION=\"&apos;PEG&apos;\" COUNTERPROCESS=\"\" COUNTERTYPE=\"\" DATANAME=\"&apos;c3&apos;\" DATASCALE=\"0\" DATASIZE=\"18\" DATATYPE=\"&apos;numeric&apos;\" DESCRIPTION=\"&apos;&apos;\" DiffStatus=\"\" GROUPAGGREGATION=\"\" INCLUDESQL=\"\" TIMEAGGREGATION=\"\" TYPEID=\"\" UNIVCLASS=\"\" UNIVOBJECT=\"\" />\n\t</Measurementtype>\n</techpack>\n";
		String targetxml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<techpack>\n\t<Measurementtype DATAFORMATSUPPORT=\"\" DELTACALCSUPPORT=\"\" DESCRIPTION=\"\" DiffStatus=\"\" ELEMENTBHSUPPORT=\"\" FOLDERNAME=\"\" JOINABLE=\"\" OBJECTID=\"\" OBJECTNAME=\"\" OBJECTTYPE=\"\" OBJECTVERSION=\"\" PLAINTABLE=\"\" RANKINGTABLE=\"\" SIZING=\"\" STATUS=\"\" TOTALAGG=\"\" TYPECLASSID=\"\" TYPEID=\"\" TYPENAME=\"&apos;cell&apos;\" UNIVERSEEXTENSION=\"\" VECTORSUPPORT=\"\" VENDORID=\"\" VERSIONID=\"\">\n\t\t<Measurementcounter COLNUMBER=\"\" COUNTAGGREGATION=\"&apos;PEG&apos;\" COUNTERPROCESS=\"\" COUNTERTYPE=\"\" DATANAME=\"&apos;c3&apos;\" DATASCALE=\"0\" DATASIZE=\"18\" DATATYPE=\"&apos;numeric&apos;\" DESCRIPTION=\"&apos;&apos;\" DiffStatus=\"\" GROUPAGGREGATION=\"\" INCLUDESQL=\"\" TIMEAGGREGATION=\"\" TYPEID=\"\" UNIVCLASS=\"\" UNIVOBJECT=\"\" />\n\t</Measurementtype>\n</techpack>\n";
		String result = "-------------Remove-------------\n-------------Add-------------\n";

        System.out.println(sourcexml);

        System.out.println(targetxml);
	
		try {
			
			DoDelta dd = new DoDelta(log,handledTables, new ArrayList<String>());
			dd.addSourceXML(sourcexml);
			dd.addTargetXML(targetxml);
			dd.doDelta();
			
			assertEquals(result,dd.toString());

		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception " + e);
		}
	}

	@Test
	public void toString_change1_Test() {

		String sourcexml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<techpack>\n\t<Measurementtype DATAFORMATSUPPORT=\"\" DELTACALCSUPPORT=\"\" DESCRIPTION=\"\" DiffStatus=\"\" ELEMENTBHSUPPORT=\"\" FOLDERNAME=\"\" JOINABLE=\"\" OBJECTID=\"\" OBJECTNAME=\"\" OBJECTTYPE=\"\" OBJECTVERSION=\"\" PLAINTABLE=\"\" RANKINGTABLE=\"\" SIZING=\"\" STATUS=\"\" TOTALAGG=\"\" TYPECLASSID=\"\" TYPEID=\"\" TYPENAME=\"&apos;cell&apos;\" UNIVERSEEXTENSION=\"\" VECTORSUPPORT=\"\" VENDORID=\"\" VERSIONID=\"\">\n\t\t<Measurementcounter COLNUMBER=\"\" COUNTAGGREGATION=\"&apos;PEG&apos;\" COUNTERPROCESS=\"\" COUNTERTYPE=\"\" DATANAME=\"&apos;c3&apos;\" DATASCALE=\"0\" DATASIZE=\"18\" DATATYPE=\"&apos;numeric&apos;\" DESCRIPTION=\"&apos;&apos;\" DiffStatus=\"\" GROUPAGGREGATION=\"\" INCLUDESQL=\"\" TIMEAGGREGATION=\"\" TYPEID=\"\" UNIVCLASS=\"\" UNIVOBJECT=\"\" />\n\t</Measurementtype>\n</techpack>\n";
		String targetxml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<techpack>\n\t<Measurementtype DATAFORMATSUPPORT=\"\" DELTACALCSUPPORT=\"\" DESCRIPTION=\"\" DiffStatus=\"\" ELEMENTBHSUPPORT=\"\" FOLDERNAME=\"\" JOINABLE=\"\" OBJECTID=\"\" OBJECTNAME=\"\" OBJECTTYPE=\"\" OBJECTVERSION=\"\" PLAINTABLE=\"\" RANKINGTABLE=\"\" SIZING=\"\" STATUS=\"\" TOTALAGG=\"\" TYPECLASSID=\"\" TYPEID=\"\" TYPENAME=\"&apos;cell1&apos;\" UNIVERSEEXTENSION=\"\" VECTORSUPPORT=\"\" VENDORID=\"\" VERSIONID=\"\">\n\t\t<Measurementcounter COLNUMBER=\"\" COUNTAGGREGATION=\"&apos;PEG&apos;\" COUNTERPROCESS=\"\" COUNTERTYPE=\"\" DATANAME=\"&apos;c13&apos;\" DATASCALE=\"0\" DATASIZE=\"18\" DATATYPE=\"&apos;numeric&apos;\" DESCRIPTION=\"&apos;&apos;\" DiffStatus=\"\" GROUPAGGREGATION=\"\" INCLUDESQL=\"\" TIMEAGGREGATION=\"\" TYPEID=\"\" UNIVCLASS=\"\" UNIVOBJECT=\"\" />\n\t</Measurementtype>\n</techpack>\n";
		String result = "-------------Remove-------------\nMeasurementcounter  COLNUMBER = \"\" COUNTAGGREGATION = \"'PEG'\" COUNTERPROCESS = \"\" COUNTERTYPE = \"\" DATANAME = \"'c13'\" DATASCALE = \"0\" DATASIZE = \"18\" DATATYPE = \"'numeric'\" DESCRIPTION = \"''\" DiffStatus = \"\" GROUPAGGREGATION = \"\" INCLUDESQL = \"\" TIMEAGGREGATION = \"\" TYPEID = \"\" UNIVCLASS = \"\" UNIVOBJECT = \"\"\n-------------Add-------------\nMeasurementcounter  COLNUMBER = \"\" COUNTAGGREGATION = \"'PEG'\" COUNTERPROCESS = \"\" COUNTERTYPE = \"\" DATANAME = \"'c3'\" DATASCALE = \"0\" DATASIZE = \"18\" DATATYPE = \"'numeric'\" DESCRIPTION = \"''\" DiffStatus = \"\" GROUPAGGREGATION = \"\" INCLUDESQL = \"\" TIMEAGGREGATION = \"\" TYPEID = \"\" UNIVCLASS = \"\" UNIVOBJECT = \"\"\n";

		
		try {
			
			DoDelta dd = new DoDelta(log,handledTables, new ArrayList<String>());
			dd.addSourceXML(sourcexml);
			dd.addTargetXML(targetxml);	
			dd.doDelta();
			
			assertEquals(result,dd.toString());

		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception " + e);
		}
	}

	@Test
	public void toString_change2_Test() {

		String sourcexml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<techpack>\n\t<Measurementtype DATAFORMATSUPPORT=\"\" DELTACALCSUPPORT=\"\" DESCRIPTION=\"\" DiffStatus=\"\" ELEMENTBHSUPPORT=\"\" FOLDERNAME=\"\" JOINABLE=\"\" OBJECTID=\"\" OBJECTNAME=\"\" OBJECTTYPE=\"\" OBJECTVERSION=\"\" PLAINTABLE=\"\" RANKINGTABLE=\"\" SIZING=\"\" STATUS=\"\" TOTALAGG=\"\" TYPECLASSID=\"\" TYPEID=\"\" TYPENAME=\"&apos;cell&apos;\" UNIVERSEEXTENSION=\"\" VECTORSUPPORT=\"\" VENDORID=\"\" VERSIONID=\"\">\n\t\t<Measurementcounter COLNUMBER=\"\" COUNTAGGREGATION=\"&apos;PEG&apos;\" COUNTERPROCESS=\"\" COUNTERTYPE=\"\" DATANAME=\"&apos;c3&apos;\" DATASCALE=\"0\" DATASIZE=\"18\" DATATYPE=\"&apos;numeric&apos;\" DESCRIPTION=\"&apos;&apos;\" DiffStatus=\"\" GROUPAGGREGATION=\"\" INCLUDESQL=\"\" TIMEAGGREGATION=\"\" TYPEID=\"\" UNIVCLASS=\"\" UNIVOBJECT=\"\" />\n\t</Measurementtype>\n</techpack>\n";
		String targetxml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<techpack>\n\t<Measurementtype DATAFORMATSUPPORT=\"\" DELTACALCSUPPORT=\"\" DESCRIPTION=\"\" DiffStatus=\"\" ELEMENTBHSUPPORT=\"\" FOLDERNAME=\"\" JOINABLE=\"\" OBJECTID=\"\" OBJECTNAME=\"\" OBJECTTYPE=\"\" OBJECTVERSION=\"\" PLAINTABLE=\"\" RANKINGTABLE=\"\" SIZING=\"\" STATUS=\"\" TOTALAGG=\"\" TYPECLASSID=\"\" TYPEID=\"\" TYPENAME=\"&apos;cell&apos;\" UNIVERSEEXTENSION=\"\" VECTORSUPPORT=\"\" VENDORID=\"\" VERSIONID=\"\">\n\t\t<Measurementcounter COLNUMBER=\"\" COUNTAGGREGATION=\"&apos;PEG&apos;\" COUNTERPROCESS=\"\" COUNTERTYPE=\"\" DATANAME=\"&apos;c13&apos;\" DATASCALE=\"0\" DATASIZE=\"18\" DATATYPE=\"&apos;numeric&apos;\" DESCRIPTION=\"&apos;&apos;\" DiffStatus=\"\" GROUPAGGREGATION=\"\" INCLUDESQL=\"\" TIMEAGGREGATION=\"\" TYPEID=\"\" UNIVCLASS=\"\" UNIVOBJECT=\"\" />\n\t</Measurementtype>\n</techpack>\n";
		String result = "-------------Remove-------------\nMeasurementcounter  COLNUMBER = \"\" COUNTAGGREGATION = \"'PEG'\" COUNTERPROCESS = \"\" COUNTERTYPE = \"\" DATANAME = \"'c13'\" DATASCALE = \"0\" DATASIZE = \"18\" DATATYPE = \"'numeric'\" DESCRIPTION = \"''\" DiffStatus = \"\" GROUPAGGREGATION = \"\" INCLUDESQL = \"\" TIMEAGGREGATION = \"\" TYPEID = \"\" UNIVCLASS = \"\" UNIVOBJECT = \"\"\n-------------Add-------------\nMeasurementcounter  COLNUMBER = \"\" COUNTAGGREGATION = \"'PEG'\" COUNTERPROCESS = \"\" COUNTERTYPE = \"\" DATANAME = \"'c3'\" DATASCALE = \"0\" DATASIZE = \"18\" DATATYPE = \"'numeric'\" DESCRIPTION = \"''\" DiffStatus = \"\" GROUPAGGREGATION = \"\" INCLUDESQL = \"\" TIMEAGGREGATION = \"\" TYPEID = \"\" UNIVCLASS = \"\" UNIVOBJECT = \"\"\n";
	
		try {
			
			DoDelta dd = new DoDelta(log,handledTables, new ArrayList<String>());
			dd.addSourceXML(sourcexml);
			dd.addTargetXML(targetxml);
			dd.doDelta();
			
			assertEquals(result,dd.toString());

		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception " + e);
		}
	}

	
	@Test
	public void toString_change3_Test() {

		String sourcexml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<techpack>\n\t<Measurementtype DATAFORMATSUPPORT=\"\" DELTACALCSUPPORT=\"\" DESCRIPTION=\"\" DiffStatus=\"\" ELEMENTBHSUPPORT=\"\" FOLDERNAME=\"\" JOINABLE=\"\" OBJECTID=\"\" OBJECTNAME=\"\" OBJECTTYPE=\"\" OBJECTVERSION=\"\" PLAINTABLE=\"\" RANKINGTABLE=\"\" SIZING=\"\" STATUS=\"\" TOTALAGG=\"\" TYPECLASSID=\"\" TYPEID=\"\" TYPENAME=\"&apos;cell&apos;\" UNIVERSEEXTENSION=\"\" VECTORSUPPORT=\"\" VENDORID=\"\" VERSIONID=\"\">\n\t\t<Measurementcounter COLNUMBER=\"\" COUNTAGGREGATION=\"&apos;PEG&apos;\" COUNTERPROCESS=\"\" COUNTERTYPE=\"\" DATANAME=\"&apos;c3&apos;\" DATASCALE=\"0\" DATASIZE=\"18\" DATATYPE=\"&apos;numeric&apos;\" DESCRIPTION=\"&apos;&apos;\" DiffStatus=\"\" GROUPAGGREGATION=\"\" INCLUDESQL=\"\" TIMEAGGREGATION=\"\" TYPEID=\"\" UNIVCLASS=\"\" UNIVOBJECT=\"\" />\n\t</Measurementtype>\n</techpack>\n";
		String targetxml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<techpack>\n\t<Measurementtype DATAFORMATSUPPORT=\"\" DELTACALCSUPPORT=\"\" DESCRIPTION=\"\" DiffStatus=\"\" ELEMENTBHSUPPORT=\"\" FOLDERNAME=\"\" JOINABLE=\"\" OBJECTID=\"\" OBJECTNAME=\"\" OBJECTTYPE=\"\" OBJECTVERSION=\"\" PLAINTABLE=\"\" RANKINGTABLE=\"\" SIZING=\"\" STATUS=\"\" TOTALAGG=\"\" TYPECLASSID=\"\" TYPEID=\"\" TYPENAME=\"&apos;cell1&apos;\" UNIVERSEEXTENSION=\"\" VECTORSUPPORT=\"\" VENDORID=\"\" VERSIONID=\"\">\n\t\t<Measurementcounter COLNUMBER=\"\" COUNTAGGREGATION=\"&apos;PEG&apos;\" COUNTERPROCESS=\"\" COUNTERTYPE=\"\" DATANAME=\"&apos;c3&apos;\" DATASCALE=\"0\" DATASIZE=\"18\" DATATYPE=\"&apos;numeric&apos;\" DESCRIPTION=\"&apos;&apos;\" DiffStatus=\"\" GROUPAGGREGATION=\"\" INCLUDESQL=\"\" TIMEAGGREGATION=\"\" TYPEID=\"\" UNIVCLASS=\"\" UNIVOBJECT=\"\" />\n\t</Measurementtype>\n</techpack>\n";
		String result = "-------------Remove-------------\n-------------Add-------------\n";
	
		try {
			
			DoDelta dd = new DoDelta(log,handledTables, new ArrayList<String>());
			dd.addSourceXML(sourcexml);
			dd.addTargetXML(targetxml);
			dd.doDelta();
			
			assertEquals(result,dd.toString());

		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception " + e);
		}
	}
	
	/**
	 * Test method for {@link com.ericsson.eniq.etl.ebsHandler.action.DoDelta#removeFromDB(final RockFactory rock, final String versionId) throws Exception}.
	 */	
	@Test
	public void testRemoveFromDB() throws Exception{
		/* Reflecting the tested method */
		final Class pcClass = objectUnderTest.getClass();
		final Method removeFromDB = pcClass.getDeclaredMethod("removeFromDB", new Class[] {RockFactory.class,String.class});
		removeFromDB.setAccessible(true);	
		RockFactory rockFactory = new RockFactory("jdbc:hsqldb:mem:testdb", "sa", "",
                "org.hsqldb.jdbcDriver", "con", true);
		removeFromDB.invoke(objectUnderTest, new Object[] {rockFactory,"PM_E_EBSW:((2))"});
		final int expectedOuput = 0;
		// Check whether we inserted 6 common keys for DC_E_STN tp.		
		int actualOutput = -1;
		ResultSet rs = null;
		try{
			rs = statement.executeQuery("select count(*) from Universeparameters");
			if(rs.next()){
				actualOutput = rs.getInt(1);
			}
		}		
		finally{
			rs.close();
		}
		assertEquals(expectedOuput, actualOutput);
		
	}
	
}
