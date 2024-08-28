package com.ericsson.eniq.techpacksdk.view.dataFormat;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Map;
import java.util.Vector;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.distocraft.dc5000.repository.dwhrep.Dataformat;
import com.distocraft.dc5000.repository.dwhrep.Datainterface;
import com.distocraft.dc5000.repository.dwhrep.Dataitem;
import com.distocraft.dc5000.repository.dwhrep.Defaulttags;
import com.distocraft.dc5000.repository.dwhrep.Measurementcounter;
import com.distocraft.dc5000.repository.dwhrep.Measurementkey;
import com.distocraft.dc5000.repository.dwhrep.Measurementtable;
import com.distocraft.dc5000.repository.dwhrep.Measurementtype;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.common.testutilities.UnitDatabaseTestCase.Schema;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;
import com.ericsson.eniq.techpacksdk.datamodel.ExpectedDBValues;
import com.ericsson.eniq.techpacksdk.datamodel.InterfaceTreeDataModel;
import com.ericsson.eniq.techpacksdk.java.test.hsql.common.CreateHSQLDatabase;
import com.ericsson.eniq.techpacksdk.java.test.sybase.common.CreateSYBASEDatabase;
import com.ericsson.eniq.techpacksdk.view.measurement.MeasurementtypeExt;

public class DataformatDataModelTest extends CreateSYBASEDatabase {
	
	static DataformatDataModel _testInstance ;
	static ExpectedDBValues expInstance;
	static String serverName = "atrcx892zone3.athtem.eei.ericsson.se";
    protected Mockery context = new JUnit4Mockery();
    private final String typeName ="DC_E_TEST_ATMPORT";
    private final String dataName ="userLabel";
    private final String typeId= "DC_E_TEST:((1)):DC_E_TEST_ATMPORT";
    private final String dataformattype = "mdc";
    private final String newdataformattype= "newTest";
    private final String versionId = "DC_E_TEST:((1))";
	private String newName = "newString";
    
    //private Versioning versioningMock = context.mock(Versioning.class);
   /* private Measurementtype measTypeMock = context.mock(Measurementtype.class);
    private Measurementtable measTableMock = context.mock(Measurementtable.class);
    private Measurementcounter measCounterMock= context.mock(Measurementcounter.class);
    private MeasurementtypeExt measTypeExtMock= context.mock(MeasurementtypeExt.class);
   */ 
    

	
	
	@BeforeClass
	public static void setUp() throws SQLException{
		CreateSYBASEDatabase.createStatsVer(false);
		DataModelController dataModelController = null;
		_testInstance = new DataformatDataModel(getRockFactory(Schema.dwhrep), dataModelController);
		
	}
	
	
	@Test
	public void testgetAllDataFormatsDB() throws Exception{
		
		Map<String, Vector<Dataformat>> actual = getMap();
		int expected =1;
		assertEquals(expected, actual.size());

	}

	private Map<String, Vector<Dataformat>> getMap() throws Exception{
		_testInstance.setVersionid(versionId);
		Class testClass = _testInstance.getClass();
		Method getAllDataFormatsDB = testClass.getDeclaredMethod("getAllDataFormatsDB");
		getAllDataFormatsDB.setAccessible(true);
		Map<String, Vector<Dataformat>> actual = (Map<String, Vector<Dataformat>>) getAllDataFormatsDB.invoke(_testInstance);
		return actual;
	}
	
	@Test
	public void testGetAllDataItemsDB() throws Exception{
		
		
		Map<String, Vector<Dataitem>> actual = getDataItem();
		//System.out.println("actual size="+actual.size());
		int expected =7;//Number of DataItems in DataFormat table
		assertEquals(expected, actual.size());

	}
	
	private Map<String, Vector<Dataitem>> getDataItem() throws Exception{
		Map<String, Vector<Dataformat>> dataFormatMap = getMap();
		Class testClass = _testInstance.getClass();
		Method getAllDataItemsDB = testClass.getDeclaredMethod("getAllDataItemsDB", Map.class);
		getAllDataItemsDB.setAccessible(true);
		Map<String, Vector<Dataitem>> actual = (Map<String, Vector<Dataitem>>) getAllDataItemsDB.invoke(_testInstance, dataFormatMap);
		return actual;
		
	}

	@Test
	public void testGetAllDefaultTagsDB() throws Exception{

		Map<String, Vector<Defaulttags>> actual = getTags();
		//System.out.println("actual size="+actual.size());
		int expected =7;//Number of DataItems in DataFormat table
		assertEquals(expected, actual.size());

	}
	
	private Map<String, Vector<Defaulttags>> getTags() throws Exception{
		Map<String, Vector<Dataformat>> dataFormatMap = getMap();
		Class testClass = _testInstance.getClass();
		Method getAllDefaultTagsDB = testClass.getDeclaredMethod("getAllDefaultTagsDB", Map.class);
		getAllDefaultTagsDB.setAccessible(true);
		Map<String, Vector<Defaulttags>> actual = (Map<String, Vector<Defaulttags>>) getAllDefaultTagsDB.invoke(_testInstance, dataFormatMap);
		return actual;
		
		
	}

	@Test
	public void testCheckProcessInstructions() throws Exception{

		Class testClass = _testInstance.getClass();
		Method getProcessInstructions = testClass.getDeclaredMethod("getProcessInstructions", String.class,String.class);
		getProcessInstructions.setAccessible(true);
		
		String actual = (String) getProcessInstructions.invoke(_testInstance,dataName,typeId);
		String expected = new String("key");

		assertEquals(expected,actual);


	}
	
	private void getData() throws Exception{
		
		Class testClass = _testInstance.getClass();
	    Field dataFormats = testClass.getDeclaredField("dataFormats");
	    dataFormats.setAccessible(true);
	    Map<String, Vector<Dataformat>> dataFormatMap = getMap();
	    dataFormats.set(_testInstance, dataFormatMap);
	    
	    Field dataItems = testClass.getDeclaredField("dataItems");
	    dataItems.setAccessible(true);
	    Map<String, Vector<Dataitem>> dataItemMap = getDataItem();
	    dataItems.set(_testInstance, dataItemMap);
	    
	    Field defaultTags = testClass.getDeclaredField("defaultTags");
	    defaultTags.setAccessible(true);
	    Map<String, Vector<Defaulttags>> defaultTagMap = getTags();
	    defaultTags.set(_testInstance, defaultTagMap);
		
		
	}
	
	@Test
	public void testRenameDataType() throws Exception{

		getData();
		_testInstance.renameDataType(dataformattype,newName );
	}
	
	
	
	@Test
	public void testaddDataType() throws Exception{
		getData();
		_testInstance.addDataType(newdataformattype);
	}
	
	
	@Test
	public void testupdateDataformats() throws Exception{
		getData();
		//_testInstance.removeDataType(dataformattype);
		_testInstance.updateDataformats();
	}
	
	@Test
	public void testsaveDataformats() throws Exception{
		getData();
		//_testInstance.removeDataType(dataformattype);
		_testInstance.saveDataformats();
	}
	
	
	@Test
	public void testRemoveDataType() throws Exception{
		getData();
		_testInstance.removeDataType(dataformattype);
	}
	
	@Test
	public void testRemoveMarkedDataformats() throws Exception{
		getData();
		_testInstance.removeDataType(dataformattype);
		_testInstance.removeMarkedDataformats();
	}
	

}
