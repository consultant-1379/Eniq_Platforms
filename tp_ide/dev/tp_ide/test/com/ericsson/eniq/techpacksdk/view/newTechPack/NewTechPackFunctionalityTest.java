package com.ericsson.eniq.techpacksdk.view.newTechPack;


import static org.junit.Assert.*;

import com.ericsson.eniq.common.testutilities.DatabaseTestUtils;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.repository.dwhrep.Aggregationrule;
import com.distocraft.dc5000.repository.dwhrep.AggregationruleFactory;
import com.distocraft.dc5000.repository.dwhrep.Busyhour;
import com.distocraft.dc5000.repository.dwhrep.BusyhourFactory;
import com.distocraft.dc5000.repository.dwhrep.Busyhourmapping;
import com.distocraft.dc5000.repository.dwhrep.BusyhourmappingFactory;
import com.distocraft.dc5000.repository.dwhrep.Busyhourrankkeys;
import com.distocraft.dc5000.repository.dwhrep.BusyhourrankkeysFactory;
import com.distocraft.dc5000.repository.dwhrep.Busyhoursource;
import com.distocraft.dc5000.repository.dwhrep.BusyhoursourceFactory;
import com.ericsson.eniq.techpacksdk.common.Constants;


public class NewTechPackFunctionalityTest {
  private static final String TESTDB_DRIVER = "org.hsqldb.jdbcDriver";
  private static final String DWHREP_URL = "jdbc:hsqldb:mem:dwhrep";    
  private static final String USERNAME = "SA";
  private static final String PASSWORD = "";
  private Statement stm;
  RockFactory rock = null;
  
  private NewTechPackFunctionality newTechPackFunctionality = null;
  
  private static Field rockField;
  private static Field oldVersionIdField;
  private static Field newVersionIdField;
  private static Field techpackField;
  private static Field latestActiveTPCacheField;
  
  private static Method createAggregationruleMethod;
  
  private boolean overRideBusyHourConversionNeeded = true; 

  @Test
  public void testConstructor(){
    assertNotNull(newTechPackFunctionality);
  }
  
  @Test
  public void testCreateAggregationRuleForProductTP(){
	  
	  final String oldVersionId = "DC_E_MGW:((802))";
	  final String newVersionId = "DC_E_MGW:((803))";
	  final String targetVersionId = "DC_E_MGW:((803))";
	  
	  setupPrivateFields(oldVersionId, newVersionId, Constants.PM_TECHPACK);
	  
	  try {
		  overRideBusyHourConversionNeeded = true;
		  createAggregationruleMethod = NewTechPackFunctionality.class.getDeclaredMethod("createAggregationrule", null);
		  createAggregationruleMethod.setAccessible(true);
		  createAggregationruleMethod.invoke(newTechPackFunctionality, null);
		  
		  //Once the AggregationRules are created, it's time to verify that the sourceMTableId and targetMTableId are correct.
	      Aggregationrule aggregationRuleWhere = new Aggregationrule(rock);
	      aggregationRuleWhere.setVersionid(newVersionId);
	      try {
			AggregationruleFactory aggregationruleFactoryWhere = new AggregationruleFactory(rock, aggregationRuleWhere);
			Iterator<Aggregationrule> itr = aggregationruleFactoryWhere.get().iterator();
			
			while(itr.hasNext()){
				Aggregationrule aggRule = itr.next();
				assertTrue("Target mTableId should start with "+targetVersionId+" but it was " + aggRule.getTarget_mtableid(), aggRule.getTarget_mtableid().startsWith(targetVersionId));
				assertTrue("Source mTableId should start with "+targetVersionId+" but it was " + aggRule.getSource_mtableid(), aggRule.getSource_mtableid().startsWith(targetVersionId));
			}
		} catch (SQLException e) {
			  fail(e.getMessage());
		} catch (RockException e) {
			  fail(e.getMessage());
		}


	  } catch (SecurityException e) {
		  fail(e.getMessage());
	  } catch (NoSuchMethodException e) {
		  fail(e.getMessage());
	  } catch (IllegalArgumentException e) {
		  fail(e.getMessage());
	  } catch (IllegalAccessException e) {
		  fail(e.getMessage());
	  } catch (InvocationTargetException e) {
		  fail(e.getMessage());
	  }
  } //testCreateAggregationRuleForProductTP
  
  @Test
  public void testCreateAggregationRuleForCustomTP(){
	  
	  final String oldVersionId = "CUSTOM_DC_E_CPP:((118))";
	  final String newVersionId = "CUSTOM_DC_E_CPP:((999))";
	  final String targetVersionId = "DC_E_CPP:((123))";
	  
	  setupPrivateFields(oldVersionId, newVersionId, Constants.CUSTOM_TECHPACK);
	  
	  try {
		  overRideBusyHourConversionNeeded = true;
		  createAggregationruleMethod = NewTechPackFunctionality.class.getDeclaredMethod("createAggregationrule", null);
		  createAggregationruleMethod.setAccessible(true);
		  createAggregationruleMethod.invoke(newTechPackFunctionality, null);
		  
		  //Once the AggregationRules are created, it's time to verify that the sourceMTableId and targetMTableId are correct.
	      Aggregationrule aggregationRuleWhere = new Aggregationrule(rock);
	      aggregationRuleWhere.setVersionid(newVersionId);
	      try {
			AggregationruleFactory aggregationruleFactoryWhere = new AggregationruleFactory(rock, aggregationRuleWhere);
			Iterator<Aggregationrule> itr = aggregationruleFactoryWhere.get().iterator();
			
			while(itr.hasNext()){
				Aggregationrule aggRule = itr.next();
				assertTrue("Target mTableId should start with "+targetVersionId+" but it was " + aggRule.getTarget_mtableid(), aggRule.getTarget_mtableid().startsWith(targetVersionId));
				assertTrue("Source mTableId should start with "+targetVersionId+" but it was " + aggRule.getSource_mtableid(), aggRule.getSource_mtableid().startsWith(targetVersionId));
			}
		} catch (SQLException e) {
			  fail(e.getMessage());
		} catch (RockException e) {
			  fail(e.getMessage());
		}


	  } catch (SecurityException e) {
		  fail(e.getMessage());
	  } catch (NoSuchMethodException e) {
		  fail(e.getMessage());
	  } catch (IllegalArgumentException e) {
		  fail(e.getMessage());
	  } catch (IllegalAccessException e) {
		  fail(e.getMessage());
	  } catch (InvocationTargetException e) {
		  fail(e.getMessage());
	  }
  } //testCreateAggregationRuleForCustomTP
  
  
  @Test
  public void testCreateBusyhourProductTP(){
    final String oldVersionId = "DC_E_MGW:((802))";
    final String newVersionId = "DC_E_MGW:((803))";
    setupPrivateFields(oldVersionId, newVersionId, Constants.PM_TECHPACK);
    try{
      newTechPackFunctionality.createBusyhour();
      
      final Busyhourmapping bhmCond = new Busyhourmapping(rock);
      bhmCond.setVersionid(newVersionId);
      bhmCond.setBhlevel("DC_E_MGW_AAL2APBH");
      bhmCond.setBhtype("PP0");
      final BusyhourmappingFactory bhmF = new BusyhourmappingFactory(rock, bhmCond, true);
      assertEquals(1, bhmF.size());
      Busyhourmapping result = bhmF.getElementAt(0);
      assertEquals(newVersionId + ":" + "DC_E_MGW_AAL2AP", result.getTypeid());

    }catch(Exception e){
      fail("testCreateBusyhourProductTP - "+e.getMessage());
    }
  } //testCreateBusyhourProductTP
  
  @Test
  public void testCreateBusyhourCustomTP_MigrateFromOldThenCreateNew(){
      overRideBusyHourConversionNeeded = false;
    final String targetVersionId = "DC_E_CPP:((123))";
    String oldVersionId = "CUSTOM_DC_E_CPP:((118))"; //118=E2.5
    String newVersionId = "CUSTOM_DC_E_CPP:((888))";
    setupPrivateFields(oldVersionId, newVersionId, Constants.CUSTOM_TECHPACK);
    try{
      newTechPackFunctionality.createBusyhour();

      oldVersionId = "CUSTOM_DC_E_CPP:((888))"; //118=E2.5
      newVersionId = "CUSTOM_DC_E_CPP:((999))";
      setupPrivateFields(oldVersionId, newVersionId, Constants.CUSTOM_TECHPACK);
      overRideBusyHourConversionNeeded = true;

      newTechPackFunctionality.createBusyhour();

      final Busyhourmapping bhmCond = new Busyhourmapping(rock);
      bhmCond.setVersionid(newVersionId);
      bhmCond.setBhlevel("DC_E_CPP_AAL2APBH");
      bhmCond.setBhtype("CTP_PP0");
      final BusyhourmappingFactory bhmF = new BusyhourmappingFactory(rock, bhmCond, true);
      assertEquals(1, bhmF.size());
      Busyhourmapping result = bhmF.getElementAt(0);
      assertEquals(targetVersionId + ":" + "DC_E_CPP_AAL2AP", result.getTypeid());

    }catch(Exception e){
      fail("testCreateBusyhourCustomTP - "+e.getMessage());
    }
  } //testCreateBusyhourCustomTP
  
  /**
   * This tests that the migrated Custom Busyhour have the 
   * TargetVersionId from the currently active TP.
   */
  @Test
  public void testCreateBusyhourCustomTP_ModifiedTargetVersionId(){
    overRideBusyHourConversionNeeded = false;
    //This is the currently active TP.
    final String targetVersionId = "DC_E_CPP:((123))";
    
    //This TP was built with DC_E_CPP:((118))...
    final String oldVersionId = "CUSTOM_DC_E_CPP:((118))"; //118=E2.5
    
    //This TP must be migrated to TargetVersionId - DC_E_CPP:((123)), which is the currently active (DC_E_CPP) TP.
    final String newVersionId = "CUSTOM_DC_E_CPP:((888))";
    setupPrivateFields(oldVersionId, newVersionId, Constants.CUSTOM_TECHPACK);
    
    try{
      newTechPackFunctionality.createBusyhour();
      
      final Busyhourmapping bhmCond = new Busyhourmapping(rock);
      bhmCond.setVersionid(newVersionId);
      bhmCond.setBhlevel("DC_E_CPP_AAL2APBH");
      bhmCond.setBhtype("CTP_PP0");
      final BusyhourmappingFactory bhmF = new BusyhourmappingFactory(rock, bhmCond, true);
      assertEquals(1, bhmF.size());
      Busyhourmapping result = bhmF.getElementAt(0);
      assertEquals(targetVersionId + ":" + "DC_E_CPP_AAL2AP", result.getTypeid());

    }catch(Exception e){
      fail("testCreateBusyhourCustomTP - "+e.getMessage());
    }
  } //testCreateBusyhourCustomTP
  
  //This is case is tested in testCreateBusyhourCustomTP_MigrateFromOldThenCreateNew.
  @Ignore
  public void testCreateBushhourNewCustomTPNoMigration(){
	// Target TP should be updated when New E11 Custom TP created.
    //This is the currently active TP.
    final String targetVersionId = "DC_E_CPP:((123))";
    //This TP was built with DC_E_CPP:((118))...
    final String oldVersionId = "CUSTOM_DC_E_CPP:((118))"; //118=E2.5
    //This TP must be migrated to TargetVersionId - DC_E_CPP:((123)), which is the currently active (DC_E_CPP) TP.
    final String newVersionId = "CUSTOM_DC_E_CPP:((888))";
    
    // In this test want to create new E11 version CUSTOM_DC_E_CPP:((888))
    setupPrivateFields(oldVersionId, newVersionId, Constants.CUSTOM_TECHPACK);
    overRideBusyHourConversionNeeded = true; //Testing case where conversion in not done.
    try{
      newTechPackFunctionality.createBusyhour();
      //TargetVersionID should be latest active product TP.
      final Busyhour whereBusyhour = new Busyhour(rock);
      whereBusyhour.setVersionid(newVersionId);
      final BusyhourFactory busyhourFactory = new BusyhourFactory(rock, whereBusyhour, true);
      assertTrue("For "+newVersionId+" Expected a list of Busyhours but got "+busyhourFactory.get(), busyhourFactory.get().size()>0);
      for (Busyhour bh : busyhourFactory.get()) {
    	  assertEquals("For "+newVersionId+" Expected TargetVserionID="+targetVersionId, targetVersionId, bh.getTargetversionid());
      }
      
      final Busyhoursource bhSCond = new Busyhoursource(rock);
      bhSCond.setVersionid(newVersionId);
      final BusyhoursourceFactory busyhoursourceFactory = new BusyhoursourceFactory(rock, bhSCond, true);
      assertTrue("For "+newVersionId+" Expected a list of Busyhoursources but got "+busyhoursourceFactory.get().size(), busyhoursourceFactory.get().size()>0);
      for (Busyhoursource bhs : busyhoursourceFactory.get()) {
    	  assertEquals("For "+newVersionId+" Expected Busyhoursource TargetVserionID="+targetVersionId, targetVersionId, bhs.getTargetversionid());
      }
      
      final Busyhourrankkeys bhRKCond = new Busyhourrankkeys(rock);
      bhRKCond.setVersionid(newVersionId);
      final BusyhourrankkeysFactory busyhourrankkeysFactory = new BusyhourrankkeysFactory(rock, bhRKCond, true);
      assertTrue("For "+newVersionId+" Expected a list of Busyhourrankkeys but got "+busyhourrankkeysFactory.get().size(), busyhourrankkeysFactory.get().size()>0);
      for (Busyhourrankkeys bhrk : busyhourrankkeysFactory.get()) {
    	  assertEquals("For "+newVersionId+" Expected Busyhourrankkeys TargetVserionID="+targetVersionId, targetVersionId, bhrk.getTargetversionid());
      }
      
      Busyhourmapping bhmCond = new Busyhourmapping(rock);
      bhmCond.setVersionid(newVersionId);
      BusyhourmappingFactory bhmF = new BusyhourmappingFactory(rock, bhmCond, true);
      assertTrue("For "+newVersionId+" Expected a list of Busyhourmappings but got "+bhmF.get().size(), bhmF.get().size()>0);
      for (Busyhourmapping bhm : bhmF.get()) {
    	  assertEquals("For "+newVersionId+" Expected Busyhourmapping TargetVserionID="+targetVersionId, targetVersionId, bhm.getTargetversionid());
    	  assertEquals("For "+newVersionId+" Expected Busyhourmapping typeId="+targetVersionId +":"+ bhm.getBhobject() , targetVersionId, bhm.getTargetversionid());
      }
    }catch(Exception e){
      fail("testCreateBushhourNewCustomTPNoMigration - "+e.getMessage());
    }
  } //testCreateBushhourNewCustomTPNoMigration

  @Test
  public void testNewCustomTPAggregationRules_ModifiedTargetVersionId(){
    overRideBusyHourConversionNeeded = false;
    final String expected1 = "DC_E_CPP_AAL2APBH_MONTHRANKBH_AAL2AP_CTP_PP0";
    final String expected2 = "DC_E_CPP_VCLTPBH_MONTHRANKBH_VCLTP_CTP_PP0";

    //This is the currently active TP.
    final String targetVersionId = "DC_E_CPP:((123))";
    
    //This TP was built with DC_E_CPP:((118))...
    final String oldVersionId = "CUSTOM_DC_E_CPP:((118))"; //118=E2.5
    
    //This TP must be migrated to TargetVersionId - DC_E_CPP:((123)), which is the currently active (DC_E_CPP) TP.
    final String newVersionId = "CUSTOM_DC_E_CPP:((888))";
    setupPrivateFields(oldVersionId, newVersionId, Constants.CUSTOM_TECHPACK);
    
    try{
      newTechPackFunctionality.createBusyhour();
      
      final Aggregationrule aggregationRuleSearch = new Aggregationrule(rock);
      aggregationRuleSearch.setVersionid(newVersionId);
      final AggregationruleFactory aggregationRuleFactory = new AggregationruleFactory(rock, aggregationRuleSearch, true);
      Vector<Aggregationrule> aggregationRules = aggregationRuleFactory.get();
      
      assertEquals("There should be 12, Rules created but got: "+aggregationRules.size(), 12, aggregationRules.size());
      
      String message = null;
      
      //Take a random sample of the data and test...
      message = isTargetVersionIdContinedInAggregationRules(targetVersionId, expected1, aggregationRules);
      assertNull(message, message);
      message = isTargetVersionIdContinedInAggregationRules(targetVersionId, expected2, aggregationRules);
      assertNull(message, message);
    }catch(Exception e){
      fail("testCreateBusyhourCustomTP - "+e.getMessage());
    }
  } //testNewCustomTPAggregationRules_ModifiedTargetVersionId
  
  @Test
  public void testNewCustomTPBusyHourMappings_ModifiedTargetVersionId(){
	overRideBusyHourConversionNeeded = false;
    //This is the currently active TP.
    final String targetVersionId = "DC_E_CPP:((123))";
    
    //This TP was built with DC_E_CPP:((118))...
    final String oldVersionId = "CUSTOM_DC_E_CPP:((118))"; //118=E2.5
    
    //This TP must be migrated to TargetVersionId - DC_E_CPP:((123)), which is the currently active (DC_E_CPP) TP.
    final String newVersionId = "CUSTOM_DC_E_CPP:((888))";

    setupPrivateFields(oldVersionId, newVersionId, Constants.CUSTOM_TECHPACK);
    try{
      newTechPackFunctionality.busyHourImprovementConversion();
      
      //DC_E_CPP_AAL2APBH=DC_E_CPP_AAL2AP
      Busyhourmapping bhmCond = new Busyhourmapping(rock);
      bhmCond.setVersionid(newVersionId);
      bhmCond.setBhlevel("DC_E_CPP_AAL2APBH");
      bhmCond.setBhtype("CTP_PP0");// Choose first Placeholder, BusyHourMappings should be the same for all.
      BusyhourmappingFactory bhmF = new BusyhourmappingFactory(rock, bhmCond, true);
      // Check number of BusyHourMappings is as expected, should be no more or less.
      assertEquals("For "+bhmCond.getVersionid()+" Expected only 1 in list for "+bhmCond.getBhlevel()+" was "+bhmF.size(), 1, bhmF.size());
      //Check it is the correct one.
      Busyhourmapping result = bhmF.getElementAt(0);
      assertEquals(targetVersionId+ ":"+"DC_E_CPP_AAL2AP", result.getTypeid());
      
      //DC_E_CPP_VCLTPBH=DC_E_CPP_VCLTP,DC_E_CPP_VCLTP_V
      bhmCond = new Busyhourmapping(rock);
      bhmCond.setVersionid(newVersionId);
      bhmCond.setBhlevel("DC_E_CPP_VCLTPBH");
      bhmCond.setBhtype("CTP_PP0");// Choose first Placeholder, BusyHourMappings should be the same for all.
      bhmF = new BusyhourmappingFactory(rock, bhmCond, true);
      // Expect list, so check contents are correct.
      // Creating Hashtable of results, then check contents of list.
      Hashtable<String, String> mappings = new Hashtable<String, String>();
      for (Busyhourmapping bhm : bhmF.get()) {
    	  mappings.put(bhm.getBhtargettype(), bhm.getTypeid());
      } // for
      // Expect {DC_E_CPP_VCLTP_V=DC_E_CPP:((118)):DC_E_CPP_VCLTP_V, DC_E_CPP_VCLTP=DC_E_CPP:((118)):DC_E_CPP_VCLTP}
      assertEquals(targetVersionId+ ":"+"DC_E_CPP_VCLTP", mappings.get("DC_E_CPP_VCLTP"));
      assertEquals(targetVersionId+ ":"+"DC_E_CPP_VCLTP_V", mappings.get("DC_E_CPP_VCLTP_V"));
      // Check number of BusyHourMappings is as expected, should be no more or less.
      assertEquals("For "+bhmCond.getVersionid()+" Expected only 2 in list for "+bhmCond.getBhlevel()+" was "+bhmF.size(), 2, bhmF.size());
      
    }catch(Exception e){
      fail("testNewCustomTPBusyHourMappings - "+e.getMessage());
    }
  } //testNewCustomTPBusyHourMappings_ModifiedTargetVersionId
  
  @Ignore
  public void testNewCustomTPBusyHourMappings(){
	overRideBusyHourConversionNeeded = false;
	final String targetVersionId = "DC_E_CPP:((118))";
    final String oldVersionId = "CUSTOM_DC_E_CPP:((118))"; //118=E2.5
    final String newVersionId = "CUSTOM_DC_E_CPP:((888))";
    setupPrivateFields(oldVersionId, newVersionId, Constants.CUSTOM_TECHPACK);
    try{
      newTechPackFunctionality.busyHourImprovementConversion();
      
      //DC_E_CPP_AAL2APBH=DC_E_CPP_AAL2AP
      Busyhourmapping bhmCond = new Busyhourmapping(rock);
      bhmCond.setVersionid(newVersionId);
      bhmCond.setBhlevel("DC_E_CPP_AAL2APBH");
      bhmCond.setBhtype("CTP_PP0");// Choose first Placeholder, BusyHourMappings should be the same for all.
      BusyhourmappingFactory bhmF = new BusyhourmappingFactory(rock, bhmCond, true);
      // Check number of BusyHourMappings is as expected, should be no more or less.
      assertEquals("For "+bhmCond.getVersionid()+" Expected only 1 in list for "+bhmCond.getBhlevel()+" was "+bhmF.size(), 1, bhmF.size());
      //Check it is the correct one.
      Busyhourmapping result = bhmF.getElementAt(0);
      assertEquals(targetVersionId+ ":"+"DC_E_CPP_AAL2AP", result.getTypeid());
      
      //DC_E_CPP_VCLTPBH=DC_E_CPP_VCLTP,DC_E_CPP_VCLTP_V
      bhmCond = new Busyhourmapping(rock);
      bhmCond.setVersionid(newVersionId);
      bhmCond.setBhlevel("DC_E_CPP_VCLTPBH");
      bhmCond.setBhtype("CTP_PP0");// Choose first Placeholder, BusyHourMappings should be the same for all.
      bhmF = new BusyhourmappingFactory(rock, bhmCond, true);
      // Expect list, so check contents are correct.
      // Creating Hashtable of results, then check contents of list.
      Hashtable<String, String> mappings = new Hashtable<String, String>();
      for (Busyhourmapping bhm : bhmF.get()) {
    	  mappings.put(bhm.getBhtargettype(), bhm.getTypeid());
      } // for
      // Expect {DC_E_CPP_VCLTP_V=DC_E_CPP:((118)):DC_E_CPP_VCLTP_V, DC_E_CPP_VCLTP=DC_E_CPP:((118)):DC_E_CPP_VCLTP}
      assertEquals(targetVersionId+ ":"+"DC_E_CPP_VCLTP", mappings.get("DC_E_CPP_VCLTP"));
      assertEquals(targetVersionId+ ":"+"DC_E_CPP_VCLTP_V", mappings.get("DC_E_CPP_VCLTP_V"));
      // Check number of BusyHourMappings is as expected, should be no more or less.
      assertEquals("For "+bhmCond.getVersionid()+" Expected only 2 in list for "+bhmCond.getBhlevel()+" was "+bhmF.size(), 2, bhmF.size());
      
    }catch(Exception e){
      fail("testNewCustomTPBusyHourMappings - "+e.getMessage());
    }
  } //testNewCustomTPBusyHourMappings
  
  /**
   * This a test for isBusyhourConversionNeeded
   * It should return true because the value for oldEniqLevel = null
   */
  @Test
  public void testIsBusyhourConversionNeeded_oldEniqLevelEquals_NULL(){
	  //setup...
	  overRideBusyHourConversionNeeded = false;
	  
	  //execution...
	  boolean actual = newTechPackFunctionality.isBusyHourConversionNeeded();
	  assertTrue("Busyhour Conversion should be required", actual);
  }
  
  /**
   * This a test for isBusyhourConversionNeeded
   * It should return true because the value for oldEniqLevel = 2.0
   * The Busyhours need migrating to new BH implementation as the 
   * version is pre ENIQ11. 
   */
  @Test
  public void testIsBusyhourConversionNeeded_oldEniqLevelEquals_2_0(){
	  try {
		  //setup...
		  overRideBusyHourConversionNeeded = false;
		  Field oldEniqLevel = NewTechPackFunctionality.class.getDeclaredField("oldEniqLevel");
		  oldEniqLevel.setAccessible(true);
		  oldEniqLevel.set(newTechPackFunctionality, "2.0");

		  //execution...
		  boolean actual = newTechPackFunctionality.isBusyHourConversionNeeded();
		  assertTrue("Busyhour Conversion should be required", actual);
	  } catch (SecurityException e) {
		  fail(e.getMessage());
	  } catch (NoSuchFieldException e) {
		  fail(e.getMessage());
	  } catch (IllegalArgumentException e) {
		  fail(e.getMessage());
	  } catch (IllegalAccessException e) {
		  fail(e.getMessage());
	  }
  }

  /**
   * This a test for isBusyhourConversionNeeded
   * It should return false because the value for oldEniqLevel = 11
   * This is the point (VERSION = 11.0.4) where New BH was introduced.
   */
  @Test
  public void testIsBusyhourConversionNeeded_oldEniqLevelEquals_11(){
	  try {
		  //setup...
		  overRideBusyHourConversionNeeded = false;
		  Field oldEniqLevel = NewTechPackFunctionality.class.getDeclaredField("oldEniqLevel");
		  oldEniqLevel.setAccessible(true);
		  oldEniqLevel.set(newTechPackFunctionality, "11");

		  //execution...
		  boolean actual = newTechPackFunctionality.isBusyHourConversionNeeded();
		  assertFalse("Busyhour Conversion should not be required", actual);
	  } catch (SecurityException e) {
		  fail(e.getMessage());
	  } catch (NoSuchFieldException e) {
		  fail(e.getMessage());
	  } catch (IllegalArgumentException e) {
		  fail(e.getMessage());
	  } catch (IllegalAccessException e) {
		  fail(e.getMessage());
	  }
  }

  
  @Test
  public void testGetLatestActiveTP(){
	  try {
		  final String expected = "DC_E_CPP:((123))";
		  final String targetVersionId = "DC_E_CPP:((118))";
		  String actual = newTechPackFunctionality.getLatestActiveTP(targetVersionId);
		  assertEquals(expected, actual);
	  } catch (IllegalArgumentException e) {
		  fail(e.getMessage());
	  } catch (SQLException e) {
		  fail(e.getMessage());
	  } catch (RockException e) {
		  fail(e.getMessage());
	  }
  }

  @Test
  public void testGetLatestActiveTPCalledTwiceWithSameBusyhour(){
	  try {
		  final String expected = "DC_E_CPP:((123))";
		  final String targetVersionId = "DC_E_CPP:((118))";
		  String actual1 = newTechPackFunctionality.getLatestActiveTP(targetVersionId);
		  
		  //After this method is called the first time, a hash table should be generated and 
		  //the first lookup should be placed in the hashtable. Need to check that this is the case.
		  
		  String actual2 = newTechPackFunctionality.getLatestActiveTP(targetVersionId);
		  assertEquals(expected, actual1);
		  assertEquals(expected, actual2);
	  } catch (IllegalArgumentException e) {
		  fail(e.getMessage());
	  } catch (SQLException e) {
		  fail(e.getMessage());
	  } catch (RockException e) {
		  fail(e.getMessage());
	  }
  }

  @Test
  public void testGetLatestActiveTPWhenNoneActive(){
	  try {
		  final String targetVersionId = "DC_E_TEST:((-1))";
		  String actual = newTechPackFunctionality.getLatestActiveTP(targetVersionId);
		  assertEquals(targetVersionId, actual);
	  } catch (IllegalArgumentException e) {
		  fail(e.getMessage());
	  } catch (SQLException e) {
		  fail(e.getMessage());
	  } catch (RockException e) {
		  fail(e.getMessage());
	  }
  } //testGetLatestActiveTPWhenNoneActive

  @Test
  public void testUpdateLatestActiveTPCache(){
	  try {
		  String name  = "Name";
		  String value = "Value";
		  newTechPackFunctionality.updateLatestActiveTPCache(name, value); 

		  HashMap<String, String> actual = (HashMap<String, String>)latestActiveTPCacheField.get(newTechPackFunctionality);
		  assertNotNull(actual);
		  String actual2 = actual.get("Name");
		  assertEquals(value, actual2);
	  } catch (SecurityException e) {
		  fail(e.getMessage());
	  }catch (IllegalArgumentException e) {
		  fail(e.getMessage());
	  } catch (IllegalAccessException e) {
		  fail(e.getMessage());
	  }
  }

  @Test
  public void testUpdateLatestActiveTPCacheSameNameTwice(){
	  try {
		  String name1  = "Name";
		  String value1 = "Value";
		  newTechPackFunctionality.updateLatestActiveTPCache(name1, value1); 

		  String name2  = "Name";
		  String value2 = "Value";
		  newTechPackFunctionality.updateLatestActiveTPCache(name2, value2); 

		  HashMap<String, String> actual = (HashMap<String, String>)latestActiveTPCacheField.get(newTechPackFunctionality);
		  assertNotNull(actual);
		  
		  //The same name should only go in once.
		  assertEquals(1, actual.size());
	  } catch (SecurityException e) {
		  fail(e.getMessage());
	  }catch (IllegalArgumentException e) {
		  fail(e.getMessage());
	  } catch (IllegalAccessException e) {
		  fail(e.getMessage());
	  }
  }

  
  @Test
  public void testReadLatestActiveTPCache(){
	  String name = "Name1";
	  String exepectedValue = "Value1";
	  newTechPackFunctionality.updateLatestActiveTPCache(name, exepectedValue); 
	  
	  //If the name is in the cache, return the value...
	  String actualValue1 = newTechPackFunctionality.isInLatestActiveTPCache(name);
	  assertEquals(exepectedValue, actualValue1);
	  
	  //If the name is not in the cache, return null...
	  String actualValue2 = newTechPackFunctionality.isInLatestActiveTPCache("ThisIsntInCache");
	  assertNull(actualValue2);
  }

  @Test
  public void testReadLatestActiveTPCacheWhichIsSetToNull(){
	  //If the name is not in the cache, return null...
	  String actualValue2 = newTechPackFunctionality.isInLatestActiveTPCache("ThisIsntInCache");
	  assertNull(actualValue2);
  }

  @Test
  public void testClearTheLatestActiveTP(){
	  try {
		  String name = "Name1";
		  String exepectedValue = "Value1";
		  newTechPackFunctionality.updateLatestActiveTPCache(name, exepectedValue); 
		  HashMap<String, String> actual;
		  actual = (HashMap<String, String>)latestActiveTPCacheField.get(newTechPackFunctionality);
		  assertNotNull(actual);

		  newTechPackFunctionality.clearTheLatestActiveTP();
		  HashMap<String, String> actual2 = (HashMap<String, String>)latestActiveTPCacheField.get(newTechPackFunctionality);
		  assertNull(actual2);
	  } catch (IllegalArgumentException e) {
		  fail(e.getMessage());
	  } catch (IllegalAccessException e) {
		  fail(e.getMessage());
	  }
  }
  
  @Test
  public void testCreateNewTechPack(){
	  try{
		  Class.forName(TESTDB_DRIVER);
		  Connection c;
		  c = DriverManager.getConnection(DWHREP_URL, USERNAME, PASSWORD);
		  stm = c.createStatement();
		  stm.executeUpdate("insert into MeasurementTypeClass(TYPECLASSID, VERSIONID, DESCRIPTION) VALUES('DC_E_SMPC:((6)):DC_E_SMPC_ABLD', 'DC_E_SMPC:((6))', 'ABLD')");
		  stm.executeUpdate("insert into MeasurementTypeClass(TYPECLASSID, VERSIONID, DESCRIPTION) VALUES('DC_E_SMPC:((6)):DC_E_SMPC_CELL', 'DC_E_SMPC:((6))', 'CELL')");
		  stm.executeUpdate("insert into MEASUREMENTTYPE (TYPEID, TYPECLASSID, TYPENAME, VENDORID, FOLDERNAME, DESCRIPTION, VERSIONID, OBJECTID, OBJECTNAME, JOINABLE, SIZING, TOTALAGG, ELEMENTBHSUPPORT, RANKINGTABLE, DELTACALCSUPPORT, PLAINTABLE, UNIVERSEEXTENSION, VECTORSUPPORT, DATAFORMATSUPPORT) values ('DC_E_SMPC:((6)):DC_E_SMPC_ABLD', 'DC_E_SMPC:((6)):DC_E_SMPC_ABLD', 'DC_E_SMPC_ABLD', 'DC_E_SMPC', 'DC_E_SMPC_ABLD', 'The SMPC node generates statistics on several performance related SMPC procedures. The counters here are presented for a specific BSC/BLC, which makes it possible to trace the load of a specific BSC/BLC.', 'DC_E_SMPC:((6))', 'DC_E_SMPC:((6)):DC_E_SMPC_ABLD', 'DC_E_SMPC_ABLD', '', 'medium', '1', '0', '0', '0', '0', 'ALL', '0', '1')");
		  stm.executeUpdate("insert into MEASUREMENTTYPE (TYPEID, TYPECLASSID, TYPENAME, VENDORID, FOLDERNAME, DESCRIPTION, VERSIONID, OBJECTID, OBJECTNAME, JOINABLE, SIZING, TOTALAGG, ELEMENTBHSUPPORT, RANKINGTABLE, DELTACALCSUPPORT, PLAINTABLE, UNIVERSEEXTENSION, VECTORSUPPORT, DATAFORMATSUPPORT) values ('DC_E_SMPC:((6)):DC_E_SMPC_CELL', 'DC_E_SMPC:((6)):DC_E_SMPC_CELL', 'DC_E_SMPC_CELL', 'DC_E_SMPC', 'DC_E_SMPC_CELL', 'Measurement of an SMPC CELL', 'DC_E_SMPC:((6))', 'DC_E_SMPC:((6)):DC_E_SMPC_CELL', 'DC_E_SMPC_CELL', '', 'medium', '1', '0', '0', '0', '0', 'ALL', '0', '1')");
		  stm.executeUpdate("insert into MEASUREMENTKEY (TYPEID, DATANAME, DESCRIPTION, ISELEMENT, UNIQUEKEY, COLNUMBER, DATATYPE, DATASIZE, DATASCALE, UNIQUEVALUE, NULLABLE, INDEXES, INCLUDESQL, UNIVOBJECT, DATAID) values ('DC_E_SMPC:((6)):DC_E_SMPC_ABLD', 'MOID', 'Managed Object Id', '0', '1', '5', 'varchar', '255', '0', '255', '1', 'HG', '1', 'MOID', 'MOID')");
		  stm.executeUpdate("insert into MEASUREMENTKEY (TYPEID, DATANAME, DESCRIPTION, ISELEMENT, UNIQUEKEY, COLNUMBER, DATATYPE, DATASIZE, DATASCALE, UNIQUEVALUE, NULLABLE, INDEXES, INCLUDESQL, UNIVOBJECT, DATAID) values ('DC_E_SMPC:((6)):DC_E_SMPC_ABLD', 'NE_ID', 'Network Element ID', '0', '0', '6', 'varchar', '64', '0', '255', '1', 'HG', '1', 'NE_ID', 'neID')");
		  stm.executeUpdate("insert into MEASUREMENTKEY (TYPEID, DATANAME, DESCRIPTION, ISELEMENT, UNIQUEKEY, COLNUMBER, DATATYPE, DATASIZE, DATASCALE, UNIQUEVALUE, NULLABLE, INDEXES, INCLUDESQL, UNIVOBJECT, DATAID) values ('DC_E_SMPC:((6)):DC_E_SMPC_CELL', 'CELL', 'Cell', '0', '1', '6', 'varchar', '35', '0', '255', '1', 'HG', '1', 'CELL', 'MOID.cell')");
		  stm.executeUpdate("insert into MEASUREMENTKEY (TYPEID, DATANAME, DESCRIPTION, ISELEMENT, UNIQUEKEY, COLNUMBER, DATATYPE, DATASIZE, DATASCALE, UNIQUEVALUE, NULLABLE, INDEXES, INCLUDESQL, UNIVOBJECT, DATAID) values ('DC_E_SMPC:((6)):DC_E_SMPC_CELL', 'MOID', 'Managed Object Id', '0', '0', '4', 'varchar', '255', '0', '255', '1', 'HG', '1', 'MOID', 'MOID')");
		  stm.executeUpdate("insert into MEASUREMENTCOUNTER (TYPEID, DATANAME, DESCRIPTION, TIMEAGGREGATION, GROUPAGGREGATION, COUNTAGGREGATION, COLNUMBER, DATATYPE, DATASIZE, DATASCALE, INCLUDESQL, UNIVOBJECT, UNIVCLASS, COUNTERTYPE, COUNTERPROCESS, DATAID) values ('DC_E_SMPC:((6)):DC_E_SMPC_ABLD', 'BSCUpTime', 'Uptime of BSC while ABLD is running. The minimal time unit is second.', 'SUM', 'SUM', 'PEG', '106', 'numeric', '18', '0', '1', 'UpTime', 'A_BSC', 'PEG', 'PEG', 'NULL')");
		  stm.executeUpdate("insert into MEASUREMENTCOUNTER (TYPEID, DATANAME, DESCRIPTION, TIMEAGGREGATION, GROUPAGGREGATION, COUNTAGGREGATION, COLNUMBER, DATATYPE, DATASIZE, DATASCALE, INCLUDESQL, UNIVOBJECT, UNIVCLASS, COUNTERTYPE, COUNTERPROCESS, DATAID) values ('DC_E_SMPC:((6)):DC_E_SMPC_ABLD', 'CipherModeCompleteEventsReceived', 'Number of received cipher mode complete events from BSC for ABLD', 'SUM', 'SUM', 'PEG', '107', 'numeric', '18', '0', '1', 'EventsReceived', 'A_CipherModeComplete', 'PEG', 'PEG', 'NULL')");
		  stm.executeUpdate("insert into MEASUREMENTCOUNTER (TYPEID, DATANAME, DESCRIPTION, TIMEAGGREGATION, GROUPAGGREGATION, COUNTAGGREGATION, COLNUMBER, DATATYPE, DATASIZE, DATASCALE, INCLUDESQL, UNIVOBJECT, UNIVCLASS, COUNTERTYPE, COUNTERPROCESS, DATAID) values ('DC_E_SMPC:((6)):DC_E_SMPC_CELL', 'AGPSMSBasedAverageMSTerminalResponseTime', 'Average time between when the MS Position Command is sent and the MS Position Response is received, while an MS Based positioning attempt is ongoing. The unit is millisecond.', 'AVG', 'AVG', 'GAUGE', '164', 'numeric', '18', '8', '1', 'MSTerminalResponseTime', 'C_AGPSMSBasedAverage', 'GAUGE', 'GAUGE', 'AGPSMSBasedAverageMSTerminalResponseTime')");
		  stm.executeUpdate("insert into MEASUREMENTCOUNTER (TYPEID, DATANAME, DESCRIPTION, TIMEAGGREGATION, GROUPAGGREGATION, COUNTAGGREGATION, COLNUMBER, DATATYPE, DATASIZE, DATASCALE, INCLUDESQL, UNIVOBJECT, UNIVCLASS, COUNTERTYPE, COUNTERPROCESS, DATAID) values ('DC_E_SMPC:((6)):DC_E_SMPC_CELL', 'AGPSMSBasedAverageSuccessfulResponseTime', 'Average time between when the MS Based A-GPS method is chosen and a successful response is returned. The unit is millisecond.', 'AVG', 'AVG', 'GAUGE', '159', 'numeric', '18', '8', '1', 'SuccessfulResponseTime', 'C_AGPSMSBasedAverage', 'GAUGE', 'GAUGE', 'AGPSMSBasedAverageSuccessfulResponseTime')");
		  stm.executeUpdate("insert into MeasurementVector (TYPEID, DATANAME, VENDORRELEASE, VINDEX, VFROM, VTO, MEASURE) VALUES('DC_E_SMPC:((6)):DC_E_SMPC_ABLD', 'pmBwUtilizationRx', '3', '0.000000', 'Peak', 'Cell Rate', '')");
		  stm.executeUpdate("insert into MeasurementVector (TYPEID, DATANAME, VENDORRELEASE, VINDEX, VFROM, VTO, MEASURE) VALUES('DC_E_SMPC:((6)):DC_E_SMPC_ABLD', 'pmBwUtilizationRx', '3', '1.000000', '0', '5', '% of VC bandwidth')");
		  stm.executeUpdate("insert into MeasurementVector (TYPEID, DATANAME, VENDORRELEASE, VINDEX, VFROM, VTO, MEASURE) VALUES('DC_E_SMPC:((6)):DC_E_SMPC_CELL', 'pmBwUtilizationRx', 'CPP60', '0.000000', 'Peak', 'Cell Rate', '')");
		  stm.executeUpdate("insert into MeasurementVector (TYPEID, DATANAME, VENDORRELEASE, VINDEX, VFROM, VTO, MEASURE) VALUES('DC_E_SMPC:((6)):DC_E_SMPC_CELL', 'pmBwUtilizationRx', 'CPP60', '1.000000', '0', '5', '% of VC bandwidth')");
		  stm.executeUpdate("insert into MEASUREMENTOBJBHSUPPORT (TYPEID, OBJBHSUPPORT) values ('DC_E_SMPC:((6)):DC_E_SMPC_ABLD', 'ATMPORT')");
		  stm.executeUpdate("insert into MEASUREMENTOBJBHSUPPORT (TYPEID, OBJBHSUPPORT) values ('DC_E_SMPC:((6)):DC_E_SMPC_ABLD', 'VCLTP')");
		  stm.executeUpdate("insert into MEASUREMENTOBJBHSUPPORT (TYPEID, OBJBHSUPPORT) values ('DC_E_SMPC:((6)):DC_E_SMPC_CELL', 'NE')");
		  stm.executeUpdate("insert into MEASUREMENTOBJBHSUPPORT (TYPEID, OBJBHSUPPORT) values ('DC_E_SMPC:((6)):DC_E_SMPC_CELL', 'HS7SL1')");
		  stm.executeUpdate("insert into MeasurementDeltaCalcSupport (TYPEID, VENDORRELEASE, DELTACALCSUPPORT, VERSIONID) VALUES('DC_E_SMPC:((6)):DC_E_SMPC_ABLD', '3', '0', 'DC_E_SMPC:((6))')");
		  stm.executeUpdate("insert into MeasurementDeltaCalcSupport (TYPEID, VENDORRELEASE, DELTACALCSUPPORT, VERSIONID) VALUES('DC_E_SMPC:((6)):DC_E_SMPC_ABLD', '31', '0', 'DC_E_SMPC:((6))')");
		  stm.executeUpdate("insert into MeasurementDeltaCalcSupport (TYPEID, VENDORRELEASE, DELTACALCSUPPORT, VERSIONID) VALUES('DC_E_SMPC:((6)):DC_E_SMPC_CELL', 'CPP60', '0', 'DC_E_SMPC:((6))')");
		  stm.executeUpdate("insert into MeasurementDeltaCalcSupport (TYPEID, VENDORRELEASE, DELTACALCSUPPORT, VERSIONID) VALUES('DC_E_SMPC:((6)):DC_E_SMPC_CELL', 'CPP61', '0', 'DC_E_SMPC:((6))')");
		  stm.executeUpdate("insert into MeasurementTable (MTABLEID, TABLELEVEL, TYPEID, BASETABLENAME, PARTITIONPLAN) VALUES('DC_E_SMPC:((6)):DC_E_SMPC_ABLD:DAY', 'DAY', 'DC_E_SMPC:((6)):DC_E_SMPC_ABLD', 'DC_E_SMPC_ABLD_DAY', 'medium_day')");
		  stm.executeUpdate("insert into MeasurementTable (MTABLEID, TABLELEVEL, TYPEID, BASETABLENAME, PARTITIONPLAN) VALUES('DC_E_SMPC:((6)):DC_E_SMPC_ABLD:RAW', 'RAW', 'DC_E_SMPC:((6)):DC_E_SMPC_ABLD', 'DC_E_SMPC_ABLD_RAW', 'medium_raw')");
		  stm.executeUpdate("insert into MeasurementTable (MTABLEID, TABLELEVEL, TYPEID, BASETABLENAME, PARTITIONPLAN) VALUES('DC_E_SMPC:((6)):DC_E_SMPC_CELL:RAW', 'RAW', 'DC_E_SMPC:((6)):DC_E_SMPC_CELL', 'DC_E_SMPC_CELL_RAW', 'medium_raw')");
		  stm.executeUpdate("insert into MeasurementTable (MTABLEID, TABLELEVEL, TYPEID, BASETABLENAME, PARTITIONPLAN) VALUES('DC_E_SMPC:((6)):DC_E_SMPC_CELL:DAY', 'DAY', 'DC_E_SMPC:((6)):DC_E_SMPC_CELL', 'DC_E_SMPC_CELL_DAY', 'medium_day')");
		  stm.executeUpdate("insert into MeasurementColumn(MTABLEID, DATANAME, COLNUMBER, DATATYPE, DATASIZE, DATASCALE, UNIQUEVALUE, NULLABLE, INDEXES, DESCRIPTION, DATAID, RELEASEID, UNIQUEKEY, INCLUDESQL, COLTYPE) VALUES('DC_E_SMPC:((6)):DC_E_SMPC_CELL:RAW','GeneralAverageSuccessfulNonEmergencyResponseTime', '29', 'numeric', '18', '8', '255', '1','','Average time between a non-emergency Perform Location Request is received and a successful Perform Location Response is returned.The unit is millisecond.', 'GeneralAverageSuccessfulNonEmergencyResponseTime', 'DC_E_SMPC:((6))'," +
		  		"				'0','1','COUNTER')");  
		  stm.executeUpdate("insert into MeasurementColumn(MTABLEID, DATANAME, COLNUMBER, DATATYPE, DATASIZE, DATASCALE, UNIQUEVALUE, NULLABLE, INDEXES, DESCRIPTION, DATAID, RELEASEID, UNIQUEKEY, INCLUDESQL, COLTYPE) VALUES('DC_E_SMPC:((6)):DC_E_SMPC_CELL:DAY','AGPSUEBasedUnsuccessfulLocationProcedureNotCompleted', '226', 'numeric', '18', '0', '255', '1','','Counts the number of failed UE Assisted A-GPS positioning due to non-completed. Location Procedure . For the failure codes defined for the PositionMethodFailure Diagnostic, refer to 3GPP TS 29.002.', 'AGPSUEBasedUnsuccessfulLocationProcedureNotCompleted', 'DC_E_SMPC:((6))'," +
	  		"				'0','1','COUNTER')");
		  stm.executeUpdate("insert into MeasurementColumn(MTABLEID, DATANAME, COLNUMBER, DATATYPE, DATASIZE, DATASCALE, UNIQUEVALUE, NULLABLE, INDEXES, DESCRIPTION, DATAID, RELEASEID, UNIQUEKEY, INCLUDESQL, COLTYPE) VALUES('DC_E_SMPC:((6)):DC_E_SMPC_ABLD:RAW','BATCH_ID', '17', 'smallint', '0', '0', '255', '1','','', 'BATCH_ID', 'BASE_TP_090421'," +
	  		"				'0','0','KEY')");
		  
		  stm.executeUpdate("insert into MeasurementColumn(MTABLEID, DATANAME, COLNUMBER, DATATYPE, DATASIZE, DATASCALE, UNIQUEVALUE, NULLABLE, INDEXES, DESCRIPTION, DATAID, RELEASEID, UNIQUEKEY, INCLUDESQL, COLTYPE) VALUES('DC_E_SMPC:((6)):DC_E_SMPC_ABLD:DAY','AGG_COUNT', '14', 'smallint', '0', '0', '255', '1','','', 'AGG_COUNT', 'BASE_TP_090421'," +
	  		"				'0','0','KEY')");
		  newTechPackFunctionality = new NewTechPackFunctionality();
		  //Creating a NewTechpackInfo
		  List<String> versionList = new ArrayList<String>();
		  versionList.add("12.0");
		  versionList.add("11.0");
		  List<List<String>> universeList = new ArrayList<List<String>>();
		  List<String> universeList1 = new ArrayList<String>();
		  universeList1.add("UniverseList11");
		  universeList1.add("UniverseList12");
		  List<String> universeList2 = new ArrayList<String>();
		  universeList2.add("UniverseList21");
		  universeList2.add("UniverseList22");
		  universeList.add(universeList1);
		  universeList.add(universeList2);
		  List<String> tpDepend = new ArrayList<String>();
		  tpDepend.add("DWH_MONITOR");
		  tpDepend.add("DWH_BASE");
		  
		  NewTechPackInfo newTPInfo = new NewTechPackInfo("DC_E_SMPC", "((8))", "COA 252 141/1", "R4E", "CXC4010597", "PM", 
				"DCE18", universeList, "Ericsson SMPC",  versionList, "TP_BASE:BASE_TP_20101117", tpDepend, "Install Description", "DC_E_SMPC:((6))", "username");
		  newTechPackFunctionality.createTechPack(newTPInfo, rock);
		  
		  //Now check that data went in tables correctly or not
		  //Versioning Table
		  ResultSet value = stm.executeQuery("select * from Versioning where VERSIONID='DC_E_SMPC:((8))'");
		  value.next();
		  final String versionId = value.getString("VERSIONID");
		  final String description = value.getString("DESCRIPTION");
		  final int status = value.getInt("STATUS");
		  final String techpackName = value.getString("TECHPACK_NAME");
		  final String techpackVer = value.getString("TECHPACK_VERSION");
		  final String techpackType = value.getString("TECHPACK_TYPE");
		  final String productNumber = value.getString("PRODUCT_NUMBER");
		  final String lockedBy = value.getString("LOCKEDBY");
		  final String baseDef = value.getString("BASEDEFINITION");
		  final String installDes = value.getString("INSTALLDESCRIPTION");
		  final String eniqLevel = value.getString("ENIQ_LEVEL");
		  final String licName = value.getString("LICENSENAME");
		  
		  final String actual = versionId + description + status + techpackName + techpackVer
		  					+ techpackType + productNumber + lockedBy + baseDef + installDes
		  					+ eniqLevel + licName ;
		  String exp = "DC_E_SMPC:((8))" + "Ericsson SMPC" + "1" + "DC_E_SMPC" + "R4E" + "PM" + 
		  				"COA 252 141/1" + "username" + "TP_BASE:BASE_TP_20101117" + "Install Description"
		  				+ "11" + "CXC4010597";
		  assertEquals(" Actual result does not match with expected. Execpted = " + exp, exp, actual);
		  
		  //Universe Name
		  ResultSet valueU = stm.executeQuery("select * from Universename where VERSIONID='DC_E_SMPC:((8))'");
		  valueU.next();
		  final String versionIdU = valueU.getString("VERSIONID");
		  final String universeName = valueU.getString("UNIVERSENAME");
		  final String universeExt = valueU.getString("UNIVERSEEXTENSION");
		  final String universeExtName = valueU.getString("UNIVERSEEXTENSIONNAME");
		  
		  final String actualU = versionIdU + universeName + universeExt + universeExtName ;
		  String expU = "DC_E_SMPC:((8))" + "DCE18" + "UniverseList11" + "UniverseList12" ;
		  assertEquals(" Actual result does not match with expected. Execpted = " + expU, expU, actualU);
		  
		  valueU.next();
		  final String versionIdU2 = valueU.getString("VERSIONID");
		  final String universeName2 = valueU.getString("UNIVERSENAME");
		  final String universeExt2 = valueU.getString("UNIVERSEEXTENSION");
		  final String universeExtName2 = valueU.getString("UNIVERSEEXTENSIONNAME");
		  
		  final String actualU2 = versionIdU2 + universeName2 + universeExt2 + universeExtName2 ;
		  String expU2 = "DC_E_SMPC:((8))" + "DCE18" + "UniverseList21" + "UniverseList22" ;
		  assertEquals(" Actual result does not match with expected. Execpted = " + expU2, expU2, actualU2);
		  
		  //Supportedvendorrelease
		  ResultSet valueS = stm.executeQuery("select * from Supportedvendorrelease where VERSIONID='DC_E_SMPC:((8))'");
		  valueS.next();
		  final String versionIdS = valueS.getString("VERSIONID");
		  final String vendorRelease = valueS.getString("VENDORRELEASE");
		  final String actualS = versionIdS + vendorRelease ;
		  String expS = "DC_E_SMPC:((8))" + "11.0";
		  assertEquals(" Actual result does not match with expected. Execpted = " + expS, expS, actualS);
		  
		  valueS.next();
		  final String versionIdS2 = valueS.getString("VERSIONID");
		  final String vendorRelease2 = valueS.getString("VENDORRELEASE");
		  final String actualS2 = versionIdS2 + vendorRelease2 ;
		  String expS2 = "DC_E_SMPC:((8))" + "12.0";
		  assertEquals(" Actual result does not match with expected. Execpted = " + expS2, expS2, actualS2);
		  
		  //Techpackdependency
		  ResultSet valueT = stm.executeQuery("select * from Techpackdependency where VERSIONID='DC_E_SMPC:((8))'");
		  valueT.next();
		  final String versionIdT = valueT.getString("VERSIONID");
		  final String techpackNameT = valueT.getString("TECHPACKNAME");
		  final String techpackVerT = valueT.getString("VERSION");
		  final String actualT = versionIdT + techpackNameT + techpackVerT ;
		  String expT = "DC_E_SMPC:((8))" + "DWH_MONITOR" + "DWH_BASE";
		  assertEquals(" Actual result does not match with expected. Execpted = " + expT, expT, actualT);
		  	  
		  //MeasurementType, MeasurementKey, MeasurementVector and other
		  ResultSet valueMT1 = stm.executeQuery("select * from MeasurementType where TYPEID='DC_E_SMPC:((8)):DC_E_SMPC_ABLD'");
		  ResultSet valueMT2 = stm.executeQuery("select * from MeasurementType where TYPEID='DC_E_SMPC:((8)):DC_E_SMPC_CELL'");
		  ResultSet valueMK1 = stm.executeQuery("select * from MeasurementKey where TYPEID='DC_E_SMPC:((8)):DC_E_SMPC_ABLD'");
		  ResultSet valueMK2 = stm.executeQuery("select * from MeasurementKey where TYPEID='DC_E_SMPC:((8)):DC_E_SMPC_CELL'");
		  ResultSet valueMV1 = stm.executeQuery("select * from MeasurementVector where TYPEID='DC_E_SMPC:((8)):DC_E_SMPC_ABLD'");
		  ResultSet valueMV2 = stm.executeQuery("select * from MeasurementVector where TYPEID='DC_E_SMPC:((8)):DC_E_SMPC_CELL'");
		  ResultSet valueMObjBH1 = stm.executeQuery("select * from MeasurementObjBHSupport where TYPEID='DC_E_SMPC:((8)):DC_E_SMPC_ABLD'");
		  ResultSet valueMObjBH2 = stm.executeQuery("select * from MeasurementObjBHSupport where TYPEID='DC_E_SMPC:((8)):DC_E_SMPC_CELL'");
		  ResultSet valueDeltacalc1 = stm.executeQuery("select * from MeasurementDeltaCalcSupport where TYPEID='DC_E_SMPC:((8)):DC_E_SMPC_ABLD'");
		  ResultSet valueDeltaCalc2 = stm.executeQuery("select * from MeasurementDeltaCalcSupport where TYPEID='DC_E_SMPC:((8)):DC_E_SMPC_CELL'");
		  ResultSet valueMTable1 = stm.executeQuery("select * from MeasurementTable where TYPEID='DC_E_SMPC:((8)):DC_E_SMPC_ABLD'");
		  ResultSet valueMTable2 = stm.executeQuery("select * from MeasurementTable where TYPEID='DC_E_SMPC:((8)):DC_E_SMPC_CELL'");
		  ResultSet valueMColumn1 = stm.executeQuery("select * from MeasurementColumn where MTABLEID like 'DC_E_SMPC:((8)):%'");
		  //ResultSet valueMColumn2 = stm.executeQuery("select * from MeasurementColumn where MTABLEID like 'DC_E_SMPC:((8)):%'");
		  //ResultSet valueGroupTypes = stm.executeQuery("select * from GroupTypes where VERSIONID='DC_E_SMPC:((8))'");
		  //Checking that all tables have new TP data
		  if((!valueMT1.next()) || (!valueMT2.next()) 
				  || (!valueMK1.next()) || (!valueMK2.next())
				  || (!valueMV1.next()) || (!valueMV2.next())
				  || (!valueMObjBH1.next()) || (!valueMObjBH2.next())
				  || (!valueDeltacalc1.next()) || (!valueDeltaCalc2.next())
				  || (!valueMTable1.next()) || (!valueMTable2.next())
				  || (!valueMColumn1.next())){
			  fail("Database Table does not populated for New TP.");
		  }
		  c.close();
		  stm.close();
	  }catch(final Exception e){
		  fail("Exception comes: " + e.getMessage());
	  }
  }
  
  @Test
  public void testRemoveTechPack(){
	  try{
		  Connection c;
		  c = DriverManager.getConnection(DWHREP_URL, USERNAME, PASSWORD);
		  stm = c.createStatement();
		  newTechPackFunctionality.removeTechPack("DC_E_SMPC:((8))", rock);
		  //Now check that data got deleted from tables correctly or not. We can check more tables
		  ResultSet valueV = stm.executeQuery("select * from Versioning where VERSIONID='DC_E_SMPC:((8))'");
		  ResultSet valueUN = stm.executeQuery("select * from Universename where VERSIONID='DC_E_SMPC:((8))'");
		  ResultSet valueSVR = stm.executeQuery("select * from Supportedvendorrelease where VERSIONID='DC_E_SMPC:((8))'");
		  ResultSet valueTPD = stm.executeQuery("select * from Techpackdependency  WHERE  VERSIONID='DC_E_SMPC:((8))'");
		  ResultSet valueAR = stm.executeQuery("select * from Aggregationrule  WHERE  VERSIONID='DC_E_SMPC:((8))'");
		  ResultSet valueAG = stm.executeQuery("select * from Aggregation  WHERE  VERSIONID='DC_E_SMPC:((8))'");
		  ResultSet valueP = stm.executeQuery("select * from Prompt  WHERE  VERSIONID='DC_E_SMPC:((8))'");
		  ResultSet valuePO = stm.executeQuery("select * from Promptoption  WHERE  VERSIONID='DC_E_SMPC:((8))'");
		  ResultSet valuePI = stm.executeQuery("select * from Promptimplementor  WHERE  VERSIONID='DC_E_SMPC:((8))'");
		  ResultSet valueMTC = stm.executeQuery("select * from Measurementtypeclass  WHERE  VERSIONID='DC_E_SMPC:((8))'");
		  if((valueV.next()) || (valueUN.next()) || (valueSVR.next())
				  || (valueTPD.next()) || (valueAR.next()) || (valueAG.next()) || (valueP.next())
				  || (valuePO.next()) || (valuePI.next()) || (valuePI.next())){
			  fail("Deletion from Database Table does not occur correctly.");
		  }
	  }catch(final Exception e){
		  fail("Exception comes: " + e.getMessage());
	  }		  
  }
  
  @Before
  public void setUp() throws Exception {
    newTechPackFunctionality = new NewTechPackFunctionality(){
      @Override
      protected boolean isBusyHourConversionNeeded(){
    	  if(overRideBusyHourConversionNeeded) {
    		  return false;
    	  } else {
    		  return super.isBusyHourConversionNeeded();
    	  }
      }
    };
    Thread.sleep(2000);
    rock = new RockFactory(DWHREP_URL, "SA", "", TESTDB_DRIVER, "test", true);
    DatabaseTestUtils.loadSetup(rock, "NewTechPackFunctionalityTest"); // Path to
                                                                   // sql dB

    rockField = NewTechPackFunctionality.class.getDeclaredField("rock");
    rockField.setAccessible(true);
    rockField.set(newTechPackFunctionality, rock);

    oldVersionIdField = NewTechPackFunctionality.class.getDeclaredField("oldVersionId");
    oldVersionIdField.setAccessible(true);
    
    newVersionIdField = NewTechPackFunctionality.class.getDeclaredField("newVersionId");
    newVersionIdField.setAccessible(true);
    
    techpackField = NewTechPackFunctionality.class.getDeclaredField("techpack");
    techpackField.setAccessible(true);

    latestActiveTPCacheField = NewTechPackFunctionality.class.getDeclaredField("latestActiveTPCache");
	latestActiveTPCacheField.setAccessible(true);

  }

  @After
  public void tearDown() throws Exception {
    newTechPackFunctionality = null;
		shutdown(rock);
		rock = null;
	}

	private void shutdown(final RockFactory db) {
		try {
			if (db != null && !db.getConnection().isClosed()) {
				final Statement stmt = db.getConnection().createStatement();
				stmt.executeUpdate("SHUTDOWN");
				stmt.close();
				db.getConnection().close();
			}
		} catch (Throwable t) {
			// ignore
		}
	}

    /**
     * This is a utility method. It is used to check if the Generated 
     * Aggregationrules contain the targetVersionId we expect.
     * @param targetVersionId
     * @param theContainer
     * @return
     */
    private String isTargetVersionIdContinedInAggregationRules(final String targetVersionId, final String aggregation, final Vector<Aggregationrule> theContainer){
    	Aggregationrule aggregationRule = null;
		Iterator<Aggregationrule> itr = theContainer.iterator();
		while(itr.hasNext()){
			aggregationRule = itr.next();
			//is the aggregation rule correct?
			if(aggregationRule.getAggregation().equalsIgnoreCase(aggregation)){
				//is the target_mtableId correct?
				if(aggregationRule.getTarget_mtableid().startsWith(targetVersionId)){
					//is the source_mtableid correct?
					if(aggregationRule.getSource_mtableid().startsWith(targetVersionId)){
						return null; //everything ok!
					}
					return "Aggregationrule: The source_mtableid is not correct for ("+aggregation+"). Expected it to start with - ("+targetVersionId+") but got - ("+aggregationRule.getSource_mtableid()+")"; //source_mtableid not correct!
				}
				return "Aggregationrule: The target_mtableid is not correct for ("+aggregation+"). Expected it to start with - ("+targetVersionId+") but got - ("+aggregationRule.getTarget_mtableid()+")"; //target_mtableId not correct!
			}
		}
    	return "Aggregationrule: The aggregation ("+aggregation+") couldn't be found."; //Couldn't find the aggregation in the Aggregationrules!
    }

	private void setupPrivateFields(final String oldVersionId, final String newVersionId, final String techpackType){
    try {
      oldVersionIdField.set(newTechPackFunctionality, oldVersionId);
      newVersionIdField.set(newTechPackFunctionality, newVersionId);
      final NewTechPackInfo techpack = new NewTechPackInfo();
      techpack.setType(techpackType);
      techpackField.set(newTechPackFunctionality, techpack);
    } catch (IllegalArgumentException e) {
      fail("setupPrivateFields: IllegalArgumentException - "+ e.getMessage());
    } catch (IllegalAccessException e) {
      fail("setupPrivateFields: IllegalAccessException - "+ e.getMessage());
    }
  }
}