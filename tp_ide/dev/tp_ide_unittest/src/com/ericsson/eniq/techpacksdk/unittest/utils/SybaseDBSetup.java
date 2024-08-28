package com.ericsson.eniq.techpacksdk.unittest.utils;

import java.io.File;
import java.io.FileInputStream;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import ssc.rockfactory.RockFactory;

import com.ericsson.eniq.common.testutilities.UnitDatabaseTestCase;

/**
 * This class is used to setup Sybase Database 
 * and Load Techpack SQLs which reside in ETLC_TestHelper Package  
 * 
 */

public class SybaseDBSetup extends UnitDatabaseTestCase {   
	
	 static {
		    System.setProperty("integration_host", TestSetupConstants.TEST_SERVER);
		  }
	 
	 
	 // To setup Sybase Database without display
	 public static void createStatsVer() throws Exception{
		  createStatsVer(false);
	  }
	// This will setup sybase Database and display it upon user request.
	public static void createStatsVer(boolean showDatabase) throws Exception {
		  if(showDatabase){
			  System.setProperty("dms", "");			  
		  }		
		  setup(TestType.integration);
		  createLogBHTable();
		  loadAllSQLFiles(TechPack.stats, "ide");			 
		  loadAllSQLFiles(TechPack.stats, "ide_baseTP");
	  }
	

	public static String getTestServer() {
		return TestSetupConstants.TEST_SERVER;
	}
	
	private static boolean setLoginProcedure() throws SQLException{
	  
		  RockFactory dwhrep = getRockFactory(Schema.dwhrep);
		  Statement stm = dwhrep.getConnection().createStatement();
		  final String createLockTable = "SET OPTION PUBLIC.login_procedure = NULL;";
		  stm.executeUpdate(createLockTable);
		  stm.close();
		  return false;
	}
	
	public static void createLogBHTable() throws SQLException {
		RockFactory dwh = getRockFactory(Schema.dc);
		Statement stm = dwh.getConnection().createStatement();
		final String createLogBHTable = "create table IF NOT EXISTS LOG_BusyhourHistory (ID unsigned int,DATETIME timestamp,SQL varchar(3200));";
		stm.executeUpdate(createLogBHTable);
		
		final String createTimeTable = "create table IF NOT EXISTS DIM_Time (HOUR_ID tinyint,MIN_ID tinyint,OFFICE_TIME tinyint,DAY_TIME tinyint,NIGHT_TIME tinyint);";
		stm.executeUpdate(createTimeTable);
	}
	public static void reinitializeSybaseDB() {
		System.out.println("reinitializeSybaseDB(): Reinitializing Sybase temporary Database on "+TestSetupConstants.TEST_SERVER+"....");
		System.out.println("reinitializeSybaseDB(): Please Wait....");
		loadAllSQLFiles(TechPack.stats, "ide_drop_table_SQL");		
		//loadAllSQLFiles(TechPack.stats, "ide");
		loadDefaultTechpack(TechPack.stats, "ide", "Tech_Pack_DC_E_TEST.sql");	
		loadAllSQLFiles(TechPack.stats, "ide_baseTP");
	}	
	
}
