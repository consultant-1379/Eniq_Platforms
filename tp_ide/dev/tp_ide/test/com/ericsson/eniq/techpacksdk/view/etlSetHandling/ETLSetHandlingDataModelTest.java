package com.ericsson.eniq.techpacksdk.view.etlSetHandling;

import static org.junit.Assert.*;

import com.ericsson.eniq.common.testutilities.DatabaseTestUtils;
import java.lang.reflect.Field;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Vector;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.etl.rock.Meta_collection_setsFactory;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_collectionsFactory;
import com.distocraft.dc5000.etl.rock.Meta_schedulings;
import com.distocraft.dc5000.etl.rock.Meta_schedulingsFactory;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actionsFactory;

public class ETLSetHandlingDataModelTest {
	private static final String TESTDB_DRIVER = "org.hsqldb.jdbcDriver";
	private static final String DWHREP_URL = "jdbc:hsqldb:mem:dwhrep";
	private RockFactory rock = null;
	private ETLSetHandlingDataModel testInstance;

	@Ignore  //Ignored because it has failing due to a check in it that is specific to Gouping - grouping was removed
	@SuppressWarnings("unchecked")
	@Test
	public void testCopySets(){
		// eeoidiv, 20100906, HM70360:ASA Error -131 error for Aggregator_DC_E_RBS_DLBASEBANDPOOL_DAYBH
		final String oldVersion = "((117))";
		final String newVersion = "((119))";
		final String oldName = "DC_E_RBS";
		final String newName = "DC_E_RBS";
		final String setType = "Techpack";
		
		try{
		// Copy new sets
			testInstance.copySets(oldName, oldVersion, newName, newVersion, setType, this.rock);
		// Query dB and ensure New sets do not have oldVersion
	    Meta_transfer_actions action = new Meta_transfer_actions(rock);
	    action.setVersion_number(newVersion);
	    Meta_transfer_actionsFactory aF = new Meta_transfer_actionsFactory(rock, action, true);
	    Vector<Meta_transfer_actions> targetActions = aF.get();
	    if(targetActions.size()<=0) {
	    	fail("No Meta_transfer_actions found in test dB");
	    }
	    for (Iterator<Meta_transfer_actions> iter = targetActions.iterator(); iter.hasNext();) {
	      Meta_transfer_actions a = iter.next();
	      assertFalse("Meta_transfer_actions.Action_contents should not contain old version number", a.getAction_contents().contains(oldVersion));
	      assertTrue("Meta_transfer_actions.Action_contents should contain new version number", a.getAction_contents().contains(newVersion));
	    } // for
		}catch(Exception e){
			fail("testCopySets - "+e.getMessage());
	    }
	} //testCopySets
	
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
			  //overRideBusyHourConversionNeeded = false;
			  Field oldEniqLevel = ETLSetHandlingDataModel.class.getDeclaredField("oldEniqLevel");
			  oldEniqLevel.setAccessible(true);
			  oldEniqLevel.set(testInstance, "2.0");

			  //execution...
			  boolean actual = testInstance.isBusyHourConversionNeeded();
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
	  
	  @Test
		public void testCopySetsDuringMigrationDisablesOldAggregatorSets(){
			final String oldVersion = "((118))";
			final String newVersion = "((119))";
			final String oldName = "CUSTOM_DC_E_CPP";
			final String newName = "CUSTOM_DC_E_CPP";
			final String setType = "Custompack";
			
			try{
			// Copy new sets
			testInstance.copySets(oldName, oldVersion, newName, newVersion, setType, this.rock);
			//com.distocraft.dc5000.etl.rock.Meta_collection_sets
			//Query META_COLLECTION_SETS for new SET
			Meta_collection_sets collectionSets= new Meta_collection_sets(rock);
			collectionSets.setVersion_number(newVersion);
			Meta_collection_setsFactory collectionSetsFactory = new Meta_collection_setsFactory(rock, collectionSets, true);
			Meta_collection_sets set = collectionSetsFactory.getElementAt(0);
		
			//Check that for all META_COLLECTIONS, which have type = Aggregator, have HOLD_Flag = Y.
			//and that all other SETs are unchanged.
			Meta_collections collections = new Meta_collections(rock);
			collections.setCollection_set_id(set.getCollection_set_id());
			Meta_collectionsFactory collectionsFactory = new Meta_collectionsFactory(rock, collections, true);
			
			Vector<Meta_collections> collectionsVector = collectionsFactory.get();
			Iterator<Meta_collections> collectionsItr = collectionsVector.iterator();
			Meta_collections metaCollectionsReuse = null;
			
			while(collectionsItr.hasNext()){
				metaCollectionsReuse = collectionsItr.next();
				
				//only the Aggregation Sets must be changed from HOLD_FLAG = N -> Y. Others must remain unchanged.
				if(metaCollectionsReuse.getSettype().equals("Aggregator")){
					assertEquals(metaCollectionsReuse.getCollection_name()+" should have HOLD_flag = Y.","Y",metaCollectionsReuse.getHold_flag());
				}else{
					assertEquals(metaCollectionsReuse.getCollection_name()+" should have HOLD_flag unchanged.","N",metaCollectionsReuse.getHold_flag());
				}
			}

			
			//Check that for all META_TRANSFER_ACTIONS, which have type = Aggregation, have Enabled_Flag = N.
			//and that all other Actions are unchanged.
			Meta_transfer_actions actions = new Meta_transfer_actions(rock);
			actions.setCollection_set_id(set.getCollection_set_id());
			Meta_transfer_actionsFactory actionsFactory = new Meta_transfer_actionsFactory(rock, actions, true);
			
			Vector<Meta_transfer_actions> actionsVector = actionsFactory.get();
			Iterator<Meta_transfer_actions> itr = actionsVector.iterator();
			Meta_transfer_actions metaActionsReuse = null;
			
			while(itr.hasNext()){
				metaActionsReuse = itr.next();
				
				//only the Aggregation Sets must be changed from ENABLED_FLAG = Y -> N. Others must remain unchanged.
				if(metaActionsReuse.getAction_type().equals("Aggregation") || metaActionsReuse.getAction_type().equals("GateKeeper")){
					assertEquals(metaActionsReuse.getTransfer_action_name()+" should have enabled_flag = N.","N",metaActionsReuse.getEnabled_flag());
				}else{
					assertEquals(metaActionsReuse.getTransfer_action_name()+" should have enabled_flag unchanged.","Y",metaActionsReuse.getEnabled_flag());
				}
			}
			
			//Check that for all META_SCHEDULINGS, which have their name start with "Aggregator", have Hold_Flag = Y.
			//and that all other Schedulings are unchanged.
			Meta_schedulings scheduleSearch = new Meta_schedulings(rock);
			scheduleSearch.setCollection_set_id(set.getCollection_set_id());
			Meta_schedulingsFactory scheduleFactory = new Meta_schedulingsFactory(rock, scheduleSearch, true);
			
			Vector<Meta_schedulings> scheduleVector = scheduleFactory.get();
			Iterator<Meta_schedulings> schedulingItr = scheduleVector.iterator();
			Meta_schedulings metaSchedulingReuse = null;
			while(schedulingItr.hasNext()){
				metaSchedulingReuse = schedulingItr.next();
				//only the Aggregation Sets' Schedulings must be changed from HOLD = N -> Y. Others must remain unchanged.
				if(metaSchedulingReuse.getName().startsWith("Aggregator")){
					assertEquals(metaSchedulingReuse.getName()+" should have HOLD_flag = Y.","Y",metaSchedulingReuse.getHold_flag());
				}else{
					assertEquals(metaSchedulingReuse.getName()+" should have HOLD_flag unchanged.","N",metaSchedulingReuse.getHold_flag());
				}
			}

			
			}catch(Exception e){
				fail("testCopySets - "+e.getMessage());
		    }
		}
	
	@Before
	public void setUp() throws Exception {
	    rock = new RockFactory(DWHREP_URL, "SA", "", TESTDB_DRIVER, "test", true);
    DatabaseTestUtils.loadSetup(rock, "etlSetHandling");

	    testInstance = new ETLSetHandlingDataModel(rock);
	}

	  @After
	  public void tearDown() throws Exception {
		  testInstance = null;
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
}
