package com.ericsson.eniq.techpacksdk.java.test.sybase.common;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import ssc.rockfactory.RockFactory;

import com.ericsson.eniq.common.testutilities.UnitDatabaseTestCase;

public class CreateSYBASEDatabase extends UnitDatabaseTestCase {

  public static void createStatsVer() throws SQLException {
	  createStatsVer(false);
  }
  
  public static void createStatsVer(boolean isHSQLGuiNeeded) throws SQLException {
	  if(isHSQLGuiNeeded){
		  System.setProperty("dms", "");
	  }
	  if(!System.getProperty("user.dir").contains("ant_common")){
		  System.setProperty("integration_host", "atrcx892zone3.athtem.eei.ericsson.se");
	  }
	  setup(TestType.integration);
	  final boolean isBusy = cehckLock();
	  if(!isBusy){
		  loadAllSQLFiles(TechPack.stats, "ide_test");
	  }else{
		  System.out.println(" Can not load files as other user is using the integration database. " );
	  }
  }
  
  private static boolean cehckLock() throws SQLException{
	  RockFactory etlrep = getRockFactory(Schema.etlrep);
	  Statement stm = etlrep.getConnection().createStatement();
	  final String createLockTable = "create table IF NOT EXISTS integrationlock (lockby varchar(100));";
	  stm.executeUpdate(createLockTable);
	  final String whoIsUsing = "select * from integrationlock;";
	  ResultSet rs = stm.executeQuery(whoIsUsing);
	  if(rs.next()){
			 final String who = rs.getString(0);
			 System.out.println(" Currently " + who + " is using the Intergration database. Try later!!!!");
			 return true ;
	  }
	  final String user = System.getProperty("user.name");
	  final String createInsertStm = "insert into integrationlock(lockby) values('" + user + "');";
	  stm.executeUpdate(createInsertStm);
	  return false; 
}

public static void createEventsVer() {
	  createEventsVer(false);
  }
  
  public static void createEventsVer(boolean isHSQLGuiNeeded) {
	  if(isHSQLGuiNeeded){
		  System.setProperty("dms", "");
	  }
	  setup(TestType.integration);
	  loadDefaultTechpack(TechPack.events, "v1");
  }
}
