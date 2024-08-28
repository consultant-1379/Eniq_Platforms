package com.ericsson.eniq.techpacksdk.datamodel;



import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Vector;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.distocraft.dc5000.repository.dwhrep.Tpactivation;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.techpacksdk.java.test.hsql.common.CreateHSQLDatabase;
import com.ericsson.eniq.techpacksdk.java.test.sybase.common.CreateSYBASEDatabase;


public class DWHTreeDataModelTest extends CreateSYBASEDatabase{
	
	static DWHTreeDataModel _testInstance ;
	static ExpectedDBValues expInstance;
	
	@BeforeClass
	public static void setUp() throws SQLException{
		CreateSYBASEDatabase.createStatsVer(false);
		_testInstance = new DWHTreeDataModel(getRockFactory(Schema.dwhrep));
		expInstance = new ExpectedDBValues();
	}
	
	@Test
	public void testgetAllUniqueTechpackNames() throws Exception{
		
		Class testClass = _testInstance.getClass();
		Method getAllUniqueTechpackNames = testClass.getDeclaredMethod("getAllUniqueTechpackNames", new Class[] {});
		getAllUniqueTechpackNames.setAccessible(true);
		
		Vector <String> actual = (Vector<String>) getAllUniqueTechpackNames.invoke(_testInstance, new Object[] {});
		Vector <String> expected = expInstance.getExpTPNameFromVersioning();
		//System.out.println("actualUNique="+actual);
		for ( String tp : actual){
			if(!expected.contains(tp)){
				fail(" Actual result does not meet the expectation. Actual : " + actual.toString() + " Expected : " + expected.toString());
			}
		}
		
	}
	
	@Test
	public void testgetTypes() throws Exception{
		Class testClass = _testInstance.getClass();
		Method getTypes = testClass.getDeclaredMethod("getTypes", String.class);
		getTypes.setAccessible(true);
		
		Vector <String> actual = (Vector<String>) getTypes.invoke(_testInstance, "DC_E_TEST");
		Vector <String> expected = expInstance.getVerIdFromTPActivation();
		
		assertEquals(actual,expected);
		
	}
	
	@Test
	public void testgetAllVersions() throws Exception{
		Class testClass = _testInstance.getClass();
		Method getAllVersions = testClass.getDeclaredMethod("getAllVersions", String.class);
		getAllVersions.setAccessible(true);
		
		Vector <Versioning> actualVec = (Vector<Versioning>) getAllVersions.invoke(_testInstance, "DC_E_TEST");
		Versioning obj = actualVec.firstElement();
		String actualTpName = obj.getTechpack_name();
		String expectedVal = expInstance.getExpTPNameFromVersioning().get(2);
		
		
		assertEquals(actualTpName,expectedVal);
			
	}
	
	@Test
	public void testgetActiveVersion() throws Exception{
				
		Tpactivation tp = _testInstance.getActiveVersion("DC_E_TEST");
		String actual = tp.getVersionid();
		
		Vector <String> expected = expInstance.getVerIdFromTPActivation();
		String expectedStr = expected.firstElement();
		
		assertEquals(actual,expectedStr);
		
	}
	
	
}
