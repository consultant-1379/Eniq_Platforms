package com.ericsson.eniq.techpacksdk.datamodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Vector;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.distocraft.dc5000.repository.dwhrep.Datainterface;
import com.distocraft.dc5000.repository.dwhrep.Tpactivation;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.common.testutilities.UnitDatabaseTestCase.Schema;
import com.ericsson.eniq.techpacksdk.java.test.hsql.common.CreateHSQLDatabase;
import com.ericsson.eniq.techpacksdk.java.test.sybase.common.CreateSYBASEDatabase;

public class InterfaceTreeDataModelTest extends CreateSYBASEDatabase{

	
	static InterfaceTreeDataModel _testInstance ;
	static ExpectedDBValues expInstance;
	static String serverName = "atrcx892zone3.athtem.eei.ericsson.se";
	static Datainterface obj; 
	
	@BeforeClass
	public static void setUp() throws SQLException{
		CreateSYBASEDatabase.createStatsVer(false);
		_testInstance = new InterfaceTreeDataModel(getRockFactory(Schema.dwhrep),serverName);
		expInstance = new ExpectedDBValues();
	}
	
	private static Datainterface getDataInterfaceobj() throws Exception{
		Class testClass = _testInstance.getClass();
		Method getAllVersions = testClass.getDeclaredMethod("getAllVersions", String.class);
		getAllVersions.setAccessible(true);
		
		Vector <Datainterface> actualVec = (Vector<Datainterface>) getAllVersions.invoke(_testInstance, "INTF_DC_E_MGW");
		obj = actualVec.firstElement();
		return obj;
	}
	
	@Test
	public void testgetAllUniqueInterfaces() throws Exception{
		
		Class testClass = _testInstance.getClass();
		Method getAllUniqueInterfaces = testClass.getDeclaredMethod("getAllUniqueInterfaces", new Class[] {});
		getAllUniqueInterfaces.setAccessible(true);
		
		Vector <String> actual = (Vector<String>) getAllUniqueInterfaces.invoke(_testInstance, new Object[] {});
		Vector <String> expected = expInstance.getInterfaceNameFromDataInterface();
		assertEquals(expected,actual);
		
	}
	
	/*@Test
	public void testgetTypes() throws Exception{
		Class testClass = _testInstance.getClass();
		Method getTypes = testClass.getDeclaredMethod("getTypes", String.class);
		getTypes.setAccessible(true);
		
		Vector <String> actual = (Vector<String>) getTypes.invoke(_testInstance, "DC_E_TEST");
		Vector <String> expected = expInstance.getInterfaceNameFromDataInterface();
		//System.out.println("a="+actual);
		//System.out.println("exp= "+expected);
		assertEquals(actual,expected);
		
	}*/
	
	@Test
	public void testgetAllVersions() throws Exception{
		/*Class testClass = _testInstance.getClass();
		Method getAllVersions = testClass.getDeclaredMethod("getAllVersions", String.class);
		getAllVersions.setAccessible(true);
		
		Vector <Datainterface> actualVec = (Vector<Datainterface>) getAllVersions.invoke(_testInstance, "INTF_DC_E_MGW");
		obj = actualVec.firstElement();*/
		obj = getDataInterfaceobj();
		String actualTpName = obj.getInterfacename();
		Vector <String> expectedVec = expInstance.getInterfaceNameFromDataInterface();
		String expectedValue = expectedVec.firstElement();
		//System.out.println("actual="+actualVec);
		//System.out.println("exp= "+expectedValue);
		assertEquals(expectedValue,actualTpName);
			
	}
	
	/*@Test
	public void testActivateInterface() throws Exception{
		 obj = getDataInterfaceobj();		
		_testInstance.activateInterface(obj);
		//String actual = tp.getVersionid();
		
		/*Vector <String> expected = expInstance.getVerIdFromTPActivation();
		String expectedStr = expected.firstElement();
		//System.out.println("expectedStr="+expectedStr);
		//System.out.println("exp= "+expected);
		assertEquals(actual,expectedStr);
		
	}*/
	

}
