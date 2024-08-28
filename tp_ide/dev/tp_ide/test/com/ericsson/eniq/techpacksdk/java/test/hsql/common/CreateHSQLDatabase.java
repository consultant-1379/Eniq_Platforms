package com.ericsson.eniq.techpacksdk.java.test.hsql.common;


import java.net.Proxy.Type;

import com.ericsson.eniq.common.testutilities.UnitDatabaseTestCase;

public class CreateHSQLDatabase extends UnitDatabaseTestCase {

//  static {
//    System.setProperty("integration_host", "atrcx892zone3");
//  }

  public static void createStatsVer() {
	  createStatsVer(false);
  }
  
  public static void createStatsVer(boolean isHSQLGuiNeeded) {
	  if(isHSQLGuiNeeded){
		  System.setProperty("dms", "");
	  }
	  setup(TestType.unit);
	  //loadDefaultTechpack(TechPack.stats, "v1");
	  loadAllSQLFiles(TechPack.stats, "ide_test");
  }
  
  public static void createEventsVer() {
	  createEventsVer(false);
  }
  
  public static void createEventsVer(boolean isHSQLGuiNeeded) {
	  if(isHSQLGuiNeeded){
		  System.setProperty("dms", "");
	  }
	  setup(TestType.unit);
	  loadDefaultTechpack(TechPack.events, "v1");
  }
}
