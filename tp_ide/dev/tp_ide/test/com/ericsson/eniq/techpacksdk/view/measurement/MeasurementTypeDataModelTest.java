package com.ericsson.eniq.techpacksdk.view.measurement;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Vector;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.dwhm.VersionUpdateAction;
import com.distocraft.dc5000.repository.dwhrep.Busyhourplaceholders;
import com.distocraft.dc5000.repository.dwhrep.Datainterface;
import com.distocraft.dc5000.repository.dwhrep.Measurementcolumn;
import com.distocraft.dc5000.repository.dwhrep.Measurementcounter;
import com.distocraft.dc5000.repository.dwhrep.Measurementkey;
import com.distocraft.dc5000.repository.dwhrep.Measurementtable;
import com.distocraft.dc5000.repository.dwhrep.Measurementtype;
import com.distocraft.dc5000.repository.dwhrep.Measurementtypeclass;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.common.testutilities.UnitDatabaseTestCase.Schema;
import com.ericsson.eniq.techpacksdk.datamodel.ExpectedDBValues;
import com.ericsson.eniq.techpacksdk.datamodel.InterfaceTreeDataModel;
import com.ericsson.eniq.techpacksdk.java.test.hsql.common.CreateHSQLDatabase;
import com.ericsson.eniq.techpacksdk.java.test.sybase.common.CreateSYBASEDatabase;

/**evivrao**/

public class MeasurementTypeDataModelTest extends CreateSYBASEDatabase {
	
	static MeasurementTypeDataModel _testInstance ;
	static ExpectedDBValues expInstance;
	static String serverName = "atrcx892zone3.athtem.eei.ericsson.se";
	static Datainterface obj; 
	static String tableLevel;
	private final static String TESTDB_DRIVER = "org.hsqldb.jdbcDriver";
	private final static String DWHREP_URL = "jdbc:hsqldb:mem:dwhrep";
	private static RockFactory dwhrep = null;
	static Connection dwhrepConnection = null;
	private Integer value = 1;
	private Integer value0 = 0;
	private Vector<Object> objBHSupport =new  Vector(1);
    protected Mockery context = new JUnit4Mockery();
    
    
    private final String versionId = "DC_E_TEST:((1))";
   // private final String typeId= "DC_E_CPP:((123)):DC_E_CPP_AAL0TPVCCTP";
    private final String typeId= "DC_E_TEST:((1)):DC_E_TEST_ATMPORT";
    private final String typeId1= "DC_E_TEST:((1)):DC_E_TEST_ATMPORT1";
    //private final String typeName ="DC_E_CPP_VCLTP";
    private final String typeName ="DC_E_TEST_ATMPORT";
    private final String typeName1 ="DC_E_TEST_ATMPORT1";
    private final String dataName ="userLabel";
    private final String measExt = "Keys";
    private final String universeExtension = "a,c";
    private final String typeIdE1TP= "DC_E_TEST:((1)):DC_E_TEST_ATMPORT";
    //private String arr[]= {"3","CPP51","CPP60", "CPP61", "L10A", "L10B", "L11A", "L11B", "P5", "P6", "P6FP", "P7", "P7FP", "W10A", "W10B", "W11A", "W11B"};
    private String arr[]= {"R4.0", "R4.1", "R4.2", "R5.0", "R5.1", "R6.0", "R6.1"};
    private String arr1[]= {"3","CPP60", "CPP61", "L10A", "L10B", "L11A", "P6", "P6FP", "P7", "P7FP", "W10A", "W10B", "W11A", "W11B"};
    
    {
        // we need to mock classes, not just interfaces.
        context.setImposteriser(ClassImposteriser.INSTANCE);
    }
    
    private Versioning versioningMock = context.mock(Versioning.class);
    private Measurementtype measTypeMock = context.mock(Measurementtype.class);
    private Measurementtable measTableMock = context.mock(Measurementtable.class);
    private Measurementcounter measCounterMock= context.mock(Measurementcounter.class);
    private MeasurementtypeExt measTypeExtMock= context.mock(MeasurementtypeExt.class);
    //private Vector<Object> deltaCalcVec = getDeltaCalcSupportsForMeasurementtypeVector();
 	
    @BeforeClass
	public static void  setUp() throws SQLException{
    	CreateSYBASEDatabase.createStatsVer(false);
		_testInstance = new MeasurementTypeDataModel(getRockFactory(Schema.dwhrep));
		expInstance = new ExpectedDBValues();
		
	}
	
	
    @Before
    public void setUpMock() throws Exception {
    	
        context.checking(new Expectations() {
            {

              
                allowing(versioningMock).getVersionid();
                will(returnValue(versionId));
                
                allowing(versioningMock).getTechpack_name();
                will(returnValue("DC_E_TEST"));
                
                allowing(measTypeMock).getTypeid();
                will(returnValue(typeId));
                
                allowing(measTypeMock).getVersionid();
                will(returnValue(versionId));
                
                allowing(measTypeMock).getTypename();
                will(returnValue(typeName));
                
                allowing(measTypeMock).getEventscalctable();
                will(returnValue(value0));
                
                allowing(measTypeMock).getRankingtable();
                will(returnValue(value0));
                
                allowing(measTypeMock).getTotalagg();
                will(returnValue(value));
                
                allowing(measTypeMock).getUniverseextension();
                will(returnValue(universeExtension));
                
            
            
                allowing(measTypeExtMock).getTypename();
                will(returnValue(typeName1));
                
                allowing(measTypeExtMock).getTypeid();
                will(returnValue(typeId1));
                
                allowing(measTypeExtMock).getRankingtable();
                will(returnValue(value0));
                
                allowing(measTypeExtMock).getEventscalctable();
                will(returnValue(value0));
                
                allowing(measTypeExtMock).getMeasurementtype();
                will(returnValue(measTypeMock));
                
                allowing(measTypeExtMock).getSizing();
                will(returnValue("large"));
                
               
                allowing(measTypeExtMock).getTotalagg();
                will(returnValue(value));
                
                allowing(measTypeExtMock).getObjBHSupport();
                will(returnValue(objBHSupport));
                
                allowing(measTypeExtMock).getDeltacalcsupport();
                will(returnValue(value0));
                
                allowing(measTypeExtMock).setJoinable(with(any(String.class)));      
                
                allowing(measTypeExtMock).saveToDB();  
                
                
                allowing(measTableMock).getMtableid();
                will(returnValue("DC_E_TEST:((1)):DC_E_TEST_ATMPORT:RAW"));
                
                allowing(measCounterMock).getTypeid();
                will(returnValue(typeId));
                
                allowing(measCounterMock).getDataname();
                will(returnValue("pmReceivedAtmCells"));
                
                allowing(measTypeExtMock).getVersionid();
                will (returnValue(typeId));
                
                allowing(measTypeExtMock).getTypename();
                will (returnValue(typeName));                               
                
                allowing(measTypeExtMock).getSonAgg();                
                will (returnValue(value0));                                
                
                allowing(measTypeExtMock).getSonFifteenMinAgg();                
                will (returnValue(value0));
            }
        });

    }

    @AfterClass
    public static void tearDown() throws Exception {
      Statement stmt = dwhrepConnection.createStatement();
      stmt.execute("DROP TABLE MEASUREMENTDELTACALCSUPPORT");


    }
    
	private static void setupDwhRep() throws SQLException, RockException {
		dwhrep = getRockFactory(Schema.dwhrep);

		dwhrepConnection = dwhrep.getConnection();

		Statement stmt = dwhrepConnection.createStatement();
		stmt.execute("INSERT INTO MEASUREMENTDELTACALCSUPPORT VALUES "+
		 "('DC_E_TEST_ATMPORT', 'R4.0', '0', 'DC_E_TEST:((1))')");
		stmt.execute("INSERT INTO MEASUREMENTDELTACALCSUPPORT VALUES "+
		 "('DC_E_TEST_ATMPORT', 'R4.1', '0', 'DC_E_TEST:((1))')");
		stmt.execute("INSERT INTO MEASUREMENTDELTACALCSUPPORT VALUES "+
		 "('DC_E_TEST_ATMPORT', 'R4.2', '0', 'DC_E_TEST:((1))')");
		stmt.execute("INSERT INTO MEASUREMENTDELTACALCSUPPORT VALUES "+
		 "('DC_E_TEST_ATMPORT', 'R5.1', '0', 'DC_E_TEST:((1))')");
		stmt.execute("INSERT INTO MEASUREMENTDELTACALCSUPPORT VALUES "+
		 "('DC_E_TEST_ATMPORT', 'R6.0', '0', 'DC_E_TEST:((1))')");
		stmt.execute("INSERT INTO MEASUREMENTDELTACALCSUPPORT VALUES "+
		 "('DC_E_TEST_ATMPORT', 'R6.1', '0', 'DC_E_TEST:((1))')");
		stmt.close();

		//createDwhTypeTable(dwhrepConnection);

	}
	
	private static void createDwhTypeTable(Connection connection)
	throws SQLException {
		Statement stmt = connection.createStatement();
		stmt.executeUpdate("INSERT INTO DWHREP.MEASUREMENTOBJBHSUPPORT VALUES('DC_E_CPP:((123)):DC_E_CPP_AAL2SP','AAL2SP'");
		stmt.close();
}
	
	
	@Test
	public void testGetPublicKeysForMeasurementtype() throws Exception{
		
		tableLevel = "DC_E_TEST_ATMPORT:DAY";
		
		_testInstance.setBaseVersioning(versioningMock);
		
		Vector<Measurementkey> result = _testInstance.getPublicKeysForMeasurementtype(tableLevel);
		int sizeOfVec= result.size();
		int numOfColumnsInDBForTableLevel=29;//number of rows for DC_E_CPP_VCLTP:DAY in Measurementcolumn table
		//System.out.println("result ="+result);
		assertEquals(sizeOfVec,numOfColumnsInDBForTableLevel);


	}
	
	@Test
	public void testGetKeysForMeasurementtype() throws Exception{
		
		Vector <Measurementkey> actual = getMeasurementKeyFunc();
		int expectedSize = 10;
		int actualSize = actual.size();//Number of entries of DC_E_TEST_ATMPORT in MeasurementKey table
		assertEquals(expectedSize,actualSize);
		//System.out.println("result = "+actualString);

	}
	
	public Vector <Measurementkey> getMeasurementKeyFunc() throws Exception{
		_testInstance.setBaseVersioning(versioningMock);
		
		Class testClass = _testInstance.getClass();
		Method getKeysForMeasurementtype = testClass.getDeclaredMethod("getKeysForMeasurementtype", Measurementtype.class);
		getKeysForMeasurementtype.setAccessible(true);
		
		Vector <Measurementkey> actual = (Vector<Measurementkey>) getKeysForMeasurementtype.invoke(_testInstance, measTypeMock);
		//System.out.println("actual = "+actual);
		return actual;
	}
	
	@Test
	public void testGetPublicCountersForMeasurementtype() throws Exception{
		
		tableLevel = "ATMPORT";
		_testInstance.setBaseVersioning(versioningMock);
		Vector<MeasurementcounterExt> result = _testInstance.getPublicCountersForMeasurementtype(tableLevel);
		int actualSize = result.size();
		int expSize = 3;
		assertEquals(expSize,actualSize);
		//System.out.println("result = "+result.size());

	}
	
	@Test
	public void testGetPublicColumns() throws Exception{
		
		Vector<Measurementcolumn> result = getColumnsVec();
		int actualSizeOfVec= result.size();
		int expectedSize =30;
		assertEquals(expectedSize,actualSizeOfVec);
		//System.out.println("result = "+result.size());

	}
	
	private Vector<Measurementcolumn> getColumnsVec(){
		tableLevel = "DC_E_TEST_ATMPORT:RAW";
		_testInstance.setBaseVersioning(versioningMock);
		
		Vector<Measurementcolumn> result = _testInstance.getPublicColumns(tableLevel);
		return result;
		
	}
	
	@Test
	public void testGetVendorReleases() throws Exception{

		_testInstance.setBaseVersioning(versioningMock);
		
		Class testClass = _testInstance.getClass();
		Method getVendorReleases = testClass.getDeclaredMethod("getVendorReleases", String.class);
		getVendorReleases.setAccessible(true);
		
		Vector <String> actual = (Vector<String>) getVendorReleases.invoke(_testInstance,versionId );
		Vector <String> expected = new Vector();
	    
	    for(String exp:arr){
	    	expected.add(exp);
	    	//System.out.println("exp="+exp);
	    	
	    }
		assertEquals(expected,actual);
	    //System.out.println("result2 = "+actual);

	}
	

	@Test
	public void testCheckProcessInstructions() throws Exception{

		_testInstance.setBaseVersioning(versioningMock);
		
		Class testClass = _testInstance.getClass();
		Method checkProcessInstructions = testClass.getDeclaredMethod("checkProcessInstructions", String.class,String.class);
		checkProcessInstructions.setAccessible(true);
		
		Vector <String> actual = (Vector<String>) checkProcessInstructions.invoke(_testInstance,dataName,typeIdE1TP);
		//System.out.println("actual="+actual);
		//Vector<Measurementkey> result = _testInstance.getKeysForMeasurementtype(measTypeMock);
		String expected = new String("key");
		String actualString = actual.firstElement();
		assertEquals(expected,actualString);
		//System.out.println("result2 = "+actual);

	}
	
	
	@Test
	public void testGetDataIdFromDataFormat() throws Exception{

		_testInstance.setBaseVersioning(versioningMock);
		
		Class testClass = _testInstance.getClass();
		Method getDataIdFromDataFormat = testClass.getDeclaredMethod("getDataIdFromDataFormat", String.class,String.class,String.class);
		getDataIdFromDataFormat.setAccessible(true);
		
		String actual =  (String) getDataIdFromDataFormat.invoke(_testInstance,dataName,typeIdE1TP,versionId);
		String expected = new String("userLabel");
		assertEquals(expected,actual);
		//System.out.println("result2 = "+actual);

	}	
	
	@Test
	public void testGettargetMeasurement() throws Exception{

		_testInstance.setBaseVersioning(versioningMock);
		Integer actual = _testInstance.gettargetMeasurement(versionId, typeIdE1TP);
		Integer expected = 1;
		assertEquals(expected,actual);
		
	}
	
	@Test
	public void testGetMeasurementTypes() throws Exception{

		_testInstance.setBaseVersioning(versioningMock);
		
		Class testClass = _testInstance.getClass();
		Method getMeasurementTypes = testClass.getDeclaredMethod("getMeasurementTypes", String.class);
		getMeasurementTypes.setAccessible(true);
		
		Vector<Measurementtype> actual =  (Vector<Measurementtype>) getMeasurementTypes.invoke(_testInstance,versionId);
	   Measurementtype mt = actual.firstElement();
		String actualString = mt.getTypeid();
		//System.out.println("actual= "+actualString);
		String expected = "DC_E_TEST:((1)):DC_E_TEST_AAL1TPVCCTP";
		assertEquals(expected,actualString);
		//System.out.println("result2 = "+actual);

	}
	
	@Test
	public void testGetTablesForMeasurementtype() throws Exception{

		_testInstance.setBaseVersioning(versioningMock);
		
		Class testClass = _testInstance.getClass();
		Method getTablesForMeasurementtype = testClass.getDeclaredMethod("getTablesForMeasurementtype", Measurementtype.class);
		getTablesForMeasurementtype.setAccessible(true);
		
		Vector<Measurementtable> actual =  (Vector<Measurementtable>) getTablesForMeasurementtype.invoke(_testInstance,measTypeMock);
		String actualVal=actual.firstElement().getTypeid();
		//System.out.println("actual="+actualVal);
		String expectedVal = typeId;
		assertEquals(expectedVal,actualVal);
	}
	
	
	
	
	
	@Test
	public void testGetColumnsForMeasurementtable() throws Exception{

		_testInstance.setBaseVersioning(versioningMock);
		
		Class testClass = _testInstance.getClass();
		Method getColumnsForMeasurementtable = testClass.getDeclaredMethod("getColumnsForMeasurementtable", Measurementtable.class);
		getColumnsForMeasurementtable.setAccessible(true);
		Vector <Measurementcolumn> actual = (Vector<Measurementcolumn>) getColumnsForMeasurementtable.invoke(_testInstance,measTableMock );
		
		int expectedSize = 30;/***Number of rows of DC_E_TEST_ATMPORT in MeasurementColumn table**/
		assertEquals(expectedSize,actual.size());
		//System.out.println("result2 = "+actual.size());

	}
	
	@Test
	public void testGetCountersForMeasurementtype() throws Exception{


		Vector <MeasurementcounterExt> actual = getMeasurementcounterExt();
		int expectedSize = 3;//Number of rows of DC_E_CPP_AAL2SP in MeasurementCounter table
		assertEquals(expectedSize,actual.size());
	}
	
	public Vector <MeasurementcounterExt> getMeasurementcounterExt() throws Exception{
		_testInstance.setBaseVersioning(versioningMock);
		
		Class testClass = _testInstance.getClass();
		Method getCountersForMeasurementtype = testClass.getDeclaredMethod("getCountersForMeasurementtype", Measurementtype.class);
		getCountersForMeasurementtype.setAccessible(true);
		
		Vector <MeasurementcounterExt> actual = (Vector<MeasurementcounterExt>) getCountersForMeasurementtype.invoke(_testInstance,measTypeMock);
		//System.out.println("actial ="+actual);
		return actual;
	}
	/**need to add entries for DC_E_TEST in MeasurementDeltaCalcSupport Table**/
	@Test
	public void testGetDeltaCalcSupportsForMeasurementtype() throws Exception{
		
		setupDwhRep();
		Vector<Object> result = getDeltaCalcSupportsForMeasurementtypeVector();
		//System.out.println("result"+result);
		int actualSize = result.size();
		int expSize = 7;//Number of rows of DC_E_CPP_VCLTP in MEASUREMENTDELTACALCSUPPORT table
		//System.out.println("actual ="+actualSize);
		assertEquals(expSize,actualSize);
		
	}
	
	private Vector<Object> getDeltaCalcSupportsForMeasurementtypeVector() throws Exception{
		_testInstance.setCurrentVersioning(versioningMock);
		Class testClass = _testInstance.getClass();
	    Field vendorReleases = testClass.getDeclaredField("vendorReleases");
	    vendorReleases.setAccessible(true);
	
	    vendorReleases.set(_testInstance, arr);
	    
		Vector<Object> result = _testInstance.getDeltaCalcSupportsForMeasurementtype(measTypeMock);
		return result;
		
	}
	
	@Test
	public void testGetObjBHSupportsForMeasurementtype() throws Exception{

		//setupDwhRep();
		Vector <Object> actual = getObjBHSupportsForMeasurementtypeVec();
		//System.out.println("actual - "+actual);
		//Integer expected = 1;
		//assertEquals(actual,expected);
		
	}	
	
	private Vector <Object> getObjBHSupportsForMeasurementtypeVec() throws Exception{
		_testInstance.setBaseVersioning(versioningMock);
		Class testClass = _testInstance.getClass();
		Method getObjBHSupportsForMeasurementtype = testClass.getDeclaredMethod
		("getObjBHSupportsForMeasurementtype", Measurementtype.class);
		getObjBHSupportsForMeasurementtype.setAccessible(true);
		
		Vector <Object> actual = (Vector<Object>) getObjBHSupportsForMeasurementtype.invoke(_testInstance,measTypeMock);
		
		return actual;
	}
	
	/**not working yet for DC_E_TEST**/

	@Test
	public void testGetVectorsForMeasurementcounter() throws Exception{

		_testInstance.setBaseVersioning(versioningMock);
		Class testClass = _testInstance.getClass();
		Method getVectorsForMeasurementcounter = testClass.getDeclaredMethod("getVectorsForMeasurementcounter", Measurementcounter.class);
		getVectorsForMeasurementcounter.setAccessible(true);
		
		Vector <MeasurementcounterExt> actual = (Vector<MeasurementcounterExt>) getVectorsForMeasurementcounter.invoke(_testInstance,measCounterMock);
		
		//System.out.println("actual - "+actual);
		//Integer expected = 1;
		//assertEquals(actual,expected);
		
	}	
	
	@Test
	public void testGetClassesForVersioning() throws Exception{

		_testInstance.setBaseVersioning(versioningMock);
		Class testClass = _testInstance.getClass();
		Method getClassesForVersioning = testClass.getDeclaredMethod("getClassesForVersioning", Versioning.class);
		getClassesForVersioning.setAccessible(true);
		List <Measurementtypeclass> actual = (List<Measurementtypeclass>) getClassesForVersioning.invoke(_testInstance,versioningMock);
		int expectedValue = 5; /**Number of rows of DC_E_CPP in MeasurementTypeClass table**/
		assertEquals(expectedValue,actual.size());
		
	}	
	
	@Test
	public void testGetBusyhourPlaceholders() throws Exception{

		Busyhourplaceholders actual = getBusyhourPlaceholdersObj();
		//System.out.println("a="+actual.getBhlevel());
		final String expected = "DC_E_TEST_ATMPORT";
		
		assertEquals(expected,actual.getBhlevel());
		
	}	
	
	private Busyhourplaceholders getBusyhourPlaceholdersObj() throws Exception{
		
		_testInstance.setBaseVersioning(versioningMock);
		Class testClass = _testInstance.getClass();
		Method getBusyhourPlaceholders = testClass.getDeclaredMethod("getBusyhourPlaceholders", Measurementtype.class);
		getBusyhourPlaceholders.setAccessible(true);
		Busyhourplaceholders actual = (Busyhourplaceholders) getBusyhourPlaceholders.invoke(_testInstance,measTypeMock);
		return actual;
	}
	
	@Test
	public void testGetUniverseExtensions() throws Exception{

		_testInstance.setBaseVersioning(versioningMock);
		Class testClass = _testInstance.getClass();
		Method getUniverseExtensions = testClass.getDeclaredMethod("getUniverseExtensions", String.class);
		getUniverseExtensions.setAccessible(true);
		Vector<String> actual = (Vector<String>) getUniverseExtensions.invoke(_testInstance,versionId);
		//System.out.println("a="+actual);
		Vector<String> expected = new Vector();
		String arr[]= {"ALL","a"};
		for(String exp:arr){
			expected.add(exp);
		}
		assertEquals(expected,actual);
		
	}

	@Test
	public void testRemoveRawKeysInUnivClass() throws Exception{

		_testInstance.setCurrentVersioning(versioningMock);
		Class testClass = _testInstance.getClass();
		Method removeRawKeysInUnivClass = testClass.getDeclaredMethod("removeRawKeysInUnivClass", String.class);
		removeRawKeysInUnivClass.setAccessible(true);
		removeRawKeysInUnivClass.invoke(_testInstance,typeName);
	}	
	
	
	@Test
	public void testUpdateUnivExtInUnivClass() throws Exception{

		_testInstance.setCurrentVersioning(versioningMock);
		Class testClass = _testInstance.getClass();
		Method updateUnivExtInUnivClass = testClass.getDeclaredMethod("updateUnivExtInUnivClass",MeasurementtypeExt.class,
				String.class, String.class, String.class);
		updateUnivExtInUnivClass.setAccessible(true);
		updateUnivExtInUnivClass.invoke(_testInstance,measTypeExtMock,typeName,measExt,typeName);
	}	
	
	@Test
	public void testCreateMeasurementInformation() throws Exception{

		_testInstance.setCurrentVersioning(versioningMock);
		Class testClass = _testInstance.getClass();
		Method createMeasurementInformation = testClass.getDeclaredMethod("createMeasurementInformation",MeasurementtypeExt.class,
				Vector.class, Vector.class,Vector.class, String.class);
		createMeasurementInformation.setAccessible(true);
		Vector<Measurementkey> measurementkeys = getMeasurementKeyFunc();
		Vector<MeasurementcounterExt> measurementcounters = getMeasurementcounterExt();
		//Vector<Measurementcolumn> publicColumns = getColumnsVec();
		Vector<Measurementcolumn> publicColumns = null;
		createMeasurementInformation.invoke(_testInstance,measTypeExtMock,measurementkeys,measurementcounters,
				publicColumns,"PLAIN");
	}	
	
	
	
	
	@Test
	public void testCreateAggregationInformation() throws Exception{

		_testInstance.setBaseVersioning(versioningMock);
		_testInstance.setCurrentVersioning(versioningMock);
		Class testClass = _testInstance.getClass();
		Method createAggregationInformation = testClass.getDeclaredMethod("createAggregationInformation",
				MeasurementtypeExt.class,Vector.class,Vector.class,Vector.class);
		createAggregationInformation.setAccessible(true);
		Vector<Measurementkey> measurementkeys = getMeasurementKeyFunc();
		Vector<MeasurementcounterExt> measurementcounters = getMeasurementcounterExt();
		//Vector<MeasurementtypeExt> allmeasurementtypes= createMeasurementtypeExtObj();
		Vector<MeasurementtypeExt> allmeasurementtypes1 = new Vector<MeasurementtypeExt>();
		allmeasurementtypes1.add(measTypeExtMock);
		
		createAggregationInformation.invoke(_testInstance,measTypeExtMock,measurementkeys,measurementcounters,
				allmeasurementtypes1 );

		
	}	
	
	/*private Vector<MeasurementtypeExt>  createMeasurementtypeExtObj() throws Exception{
		
		//Measurementtype measType = new Measurementtype(dwhrep);
		MeasurementtypeExt measTypeExt =  new MeasurementtypeExt(measTypeMock, getDeltaCalcSupportsForMeasurementtypeVector(), 
				getObjBHSupportsForMeasurementtypeVec(), getBusyhourPlaceholdersObj());
		Vector<MeasurementtypeExt> allmeasurementtypes = new Vector<MeasurementtypeExt>();
		System.out.println("result"+allmeasurementtypes);
		allmeasurementtypes.add(measTypeExt);
		return allmeasurementtypes;
		
	}
	
	
	/*@Test /*List measurements is empty hence null pointer exception..
	public void testGetMeasurement() throws Exception{

		_testInstance.setBaseVersioning(versioningMock);
		
		/*Integer actual = _testInstance.gettargetMeasurement(versionId, typeIdE1TP);
		Integer expected = 1;
		assertEquals(actual,expected);
		
		MeasurementTypeData actual = _testInstance.getMeasurement(typeId);
		System.out.println("actual -"+actual);
	}*/

	
}
