package com.ericsson.eniq.techpacksdk.unittest.utils;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * This class contains the constants for the TechPack IDE unit test setup.
 * Reads properties file from 
 *   C:\eniq\sw\conf\ide_unittest\TechPackIDE_unittest.properties 
 *   Sets the values to constants if found in the file
 *   If not set a default value.
 * @author efaigha
 * 
 */
public class TestSetupConstants {	
	
	
	static Properties  prop = new Properties();
	/**
	 * 
	 * 	
	 */
	static{
		try {			 	
		 	File f = new File("/eniq/sw/conf/TechPackIDE_unittest.properties");
		 	if (f.exists()) {
		 		FileInputStream in = new FileInputStream(f);
			 	//InputStream in = SybaseDatabaseSetup.class.getClass().getResourceAsStream("/eniq/sw/conf/TechPackIDE_unittest.properties");
			 	prop.load(in);
			 	in.close();
		 	} else {
		 		System.out.println("Property File "+f+ "does not exist on your system!!! Please create property file");
		 	}				   
		   
	   }catch (Exception e) {
		   System.out.println("Failed to read from properties file" + e.getMessage());
	   }		
	}
	
	public static String TEST_SERVER = prop.getProperty("TestServer.name", "atrcx892zone3.athtem.eei.ericsson.se");	
	
	// The userId for etlrep used in the login window
	public static String TEST_USERID = prop.getProperty("TestUser.id", "tester");

	// The password for etlrep used in the login window
	public static String TEST_USER_PASSWD = prop.getProperty("TestUser.password", "tester");
	
	// The base techpack version used in testing
	public static String BASE_TP_VERSIONID = prop.getProperty("BaseTechPack.version", "TP_BASE:BASE_TP_20110201");;
	
	
	// A flag for enabling or disabling logging of changed database tables.
	public static final boolean TABLE_CHANGE_LOGGING_FLAG = true;
	
    // Timeout in milliseconds for the TechPackIDE login to succeed.
    public static int LOGIN_TIMEOUT = 180000;
	
	// Test TechPack VersionID
	public static String TEST_TP_VERSION = prop.getProperty("TestTechPack.version","1");

	// Test TechPack name
	public static String TEST_TP_NAME = prop.getProperty("TestTechPack.name", "DC_E_TEST").toUpperCase();
	
	//Dummy Test TechPack name to simulate crate new TP.
	public static String NEW_TEST_TP_NAME = "TEST_" + TEST_USERID.toUpperCase();
	
	//Dummy TP VesrionID
	public static String NEW_TEST_TP_VERSION = "NEW";
	
	public static final String TEST_TP_PATH = TEST_SERVER +"/"+TEST_TP_NAME+"/"+TEST_TP_NAME+":"+TEST_TP_VERSION;
	
	// Name for the dwhrep database
	public static final String DB_DWHREP = prop.getProperty("DWHRepDB.name", "dwhrep");

   // Name for the etlrep database
   public static final String DB_ETLREP = prop.getProperty("ETLRepDB.name", "etlrep");

   // Name for the dwh database
   public static final String DB_DWH = prop.getProperty("DWHDB.name", "dwh");
   
   public static final String DB_PORT = prop.getProperty("DB.Port","2641");
   
   public static final String DB_USER = prop.getProperty("DB.User","etlrep");
   
   public static final String UNIVERSE_REPOSITORY = prop.getProperty("Universe.Rep","atrcx886vm2");
   
   public static final String ODBC_CONNECTION = prop.getProperty("Universe.ODBC","atrcx892zone3_repdb");

   public static final String TEST_SERVER_USER = prop.getProperty("server.user.name","dcuser"); 
   public static final String TEST_SERVER_PASS = prop.getProperty("server.user.pass","dcuser");
   
   public static String TP_NEW_VERSION = prop.getProperty("TestTP.newVersion","PA1");
   
   public static String TEST_TP_VERSIONID = TEST_TP_NAME+":(("+TEST_TP_VERSION+"))";
   public static String COPY_TP_VERSIONID = TEST_TP_NAME+":(("+TP_NEW_VERSION+"))";

   public static  String MEASUREMENT_TYPE_NAME = prop.getProperty("measurement.name", "MT1");
   
   public static String TEST_R_STATE = prop.getProperty("test.RState", "R1A");
   
   public static String INTERFACE_NAME = prop.getProperty("interface.name","INTF_DC_E_TEST");

   public static String INTERFACE_VERSION = prop.getProperty("interface.version","1");
   
   public static String statement = prop.getProperty("ES.query","Delete from DIM_E_CN_TEST_DEVICETYPE"); 
   
   public static String NEW_TRANSFORMER = prop.getProperty("transformer.name","TestTransformer").toUpperCase();
   
   public static String TRANSFORMATION_SOURCE = prop.getProperty("transformation.source","TEST_SOURCE").toUpperCase();
   
   public static String TEST_TP_DEPENDENCY = prop.getProperty("dependentTp.name ","DIM_E_CN").toUpperCase();
	
   // Busy Hour Table Colum index
   
  
}
	

