package com.ericsson.eniq.techpacksdk.view.etlSetHandling;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Logger;

import ssc.rockfactory.RockDBObject;
import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.etl.rock.Meta_collection_setsFactory;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_collectionsFactory;
import com.distocraft.dc5000.etl.rock.Meta_schedulings;
import com.distocraft.dc5000.etl.rock.Meta_schedulingsFactory;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actionsFactory;
import com.ericsson.eniq.component.ExceptionHandler;
import com.ericsson.eniq.techpacksdk.common.Constants;
import com.ericsson.eniq.techpacksdk.common.Utils;
import com.ericsson.eniq.techpacksdk.datamodel.DataModel;
import com.ericsson.eniq.techpacksdk.view.busyhourtree.BusyhourHandlingDataModel;
import com.ericsson.eniq.techpacksdk.view.dataFormat.DataformatFactory;
import com.ericsson.eniq.techpacksdk.view.newTechPack.NewTechPackDataModel;

public class ETLSetHandlingDataModel implements DataModel {

  private static final Logger logger = Logger.getLogger(DataformatFactory.class.getName());
  
  private static final String MANAGE_NEWINTERFACE = "ManageNewInterfaceView";

  private RockFactory rockFactory = null;

  private Map theSchedulings;

  private Vector<Meta_collections> theSets;

  private Map theKeys;

  private String setName;

  private String setVersion;

  /**
   * The ENIQ Level of the old techpack
   */
  private String oldEniqLevel = null;

  // Will be set to true when the data has been refreshed due to changes in
  // another data model.
  public boolean dataUpdated = false;

  public ETLSetHandlingDataModel(RockFactory rockFactory) {
    this.rockFactory = rockFactory;

  }

  @Override
  public boolean delObj(RockDBObject rObj) {
    return false;
  }

  @Override
  public RockFactory getRockFactory() {
    return rockFactory;
  }

  @Override
  public boolean modObj(RockDBObject rObj) {
    return false;
  }

  @Override
  public boolean modObj(RockDBObject[] rObj) {
    return false;
  }

  @Override
  public boolean newObj(RockDBObject rObj) {
    return false;
  }

  public void setTechpack(String name, String version) {
    setName = name;
    setVersion = version;
    refresh();
  }

  @Override
  public void refresh() {

    try {

      Meta_collection_sets mcs = new Meta_collection_sets(rockFactory);
      mcs.setVersion_number(setVersion);
      mcs.setCollection_set_name(setName);
      Meta_collection_setsFactory mcsF = new Meta_collection_setsFactory(rockFactory, mcs, true);

      Meta_collection_sets mColSet = mcsF.getElementAt(0);

      this.getAllSchedulingsDB(mColSet);
      this.getAllActionsDB(mColSet);
      this.getAllSetsDB(mColSet);

    } catch (Exception e) {
      ExceptionHandler.instance().handle(e);
      logger.warning(e.getMessage());
    }
  }

  @Override
  public void save() {
  }

  @Override
  public boolean updated(DataModel dataModel) throws Exception {
    if (dataModel instanceof NewTechPackDataModel) {

      return true;
    } else if (dataModel instanceof BusyhourHandlingDataModel) {
      // In case the busy hours have changed, the model needs to be refreshed.
      // This is because if some busy hours were deleted, the aggregator sets
      // were also removed from DB.
      
      // Refresh the data from the DB.
      refresh();
      
      // Mark the data as updated, so that the view can be refreshed when opened the next time.
      dataUpdated = true;
    }

    return false;
  }

  @Override
  public boolean validateDel(RockDBObject rObj) {
    return false;
  }

  @Override
  public boolean validateMod(RockDBObject rObj) {
    return false;
  }

  @Override
  public boolean validateNew(RockDBObject rObj) {
    return false;
  }

  public Vector<Meta_collections> getAllSets() {
    return theSets;
  }

  /**
   * Returns a list of measurement types by versionId
   * 
   * @return theTypes a list of Measurementtypes
   */
  private void getAllSetsDB(Meta_collection_sets mColSet) {

    theSets = new Vector<Meta_collections>();

    try {

      if (mColSet == null) {
        return;
      }

      Meta_collections mc = new Meta_collections(rockFactory);
      mc.setCollection_set_id(mColSet.getCollection_set_id());
      mc.setVersion_number(mColSet.getVersion_number());
      Meta_collectionsFactory mcF = new Meta_collectionsFactory(rockFactory, mc, true);
      Vector<Meta_collections> targetSets2 = mcF.get();

      for (Iterator<Meta_collections> iter2 = targetSets2.iterator(); iter2.hasNext();) {

        Meta_collections mSet = iter2.next();
        theSets.add(mSet);

      }

    } catch (SQLException e) {
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    } catch (RockException e) {
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    }
  }

  public Vector<Object> getAllActions(Meta_collections mc) {
    String key = mc.getVersion_number() + ":" + mc.getCollection_id();

    if (theKeys.containsKey(key)) {
      return (Vector<Object>) theKeys.get(key);
    } else {
      return new Vector<Object>();
    }

  }

  /**
   * 
   * 
   * @param parentType
   *          the parent of the keys
   * @return theKeys a list of Measurementkeys
   */
  private void getAllActionsDB(Meta_collection_sets mColSet) {

    theKeys = new HashMap();

    try {

      if (mColSet == null) {
        return;
      }

      Meta_transfer_actions action = new Meta_transfer_actions(rockFactory);
      action.setCollection_set_id(mColSet.getCollection_set_id());
      action.setVersion_number(mColSet.getVersion_number());
      Meta_transfer_actionsFactory aF = new Meta_transfer_actionsFactory(rockFactory, action, true);
      Vector<Meta_transfer_actions> targetActions = aF.get();

      Collections.sort(targetActions, new cmpMeta_transfer_actions());

      for (Iterator<Meta_transfer_actions> iter = targetActions.iterator(); iter.hasNext();) {
        Meta_transfer_actions a = iter.next();

        String key = a.getVersion_number() + ":" + a.getCollection_id();
        if (theKeys.keySet().contains(key)) {
          ((Vector<Object>) theKeys.get(key)).add(a);
        } else {
          Vector<Object> v = new Vector<Object>();
          v.add(a);
          theKeys.put(key, v);
        }
      }

    } catch (SQLException e) {
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    } catch (RockException e) {
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    }
  }

  private class cmpMeta_transfer_actions implements Comparator {

	@Override
    public int compare(Object o1, Object o2) {
      Meta_transfer_actions d1 = (Meta_transfer_actions) o1;
      Meta_transfer_actions d2 = (Meta_transfer_actions) o2;
      return d1.getOrder_by_no().compareTo(d2.getOrder_by_no());
    }
  }

  public Vector<Object> getAllSchedulings(Meta_collections mc) {
    String key = mc.getVersion_number() + ":" + mc.getCollection_id();
    if (theSchedulings.containsKey(key)) {
      return (Vector<Object>) theSchedulings.get(key);
    } else {
      return new Vector<Object>();
    }
  }

  /**
   * Returns a list of measurement counters for measurementtype
   * 
   * @param measurementtype
   *          the parent of the counters
   * @return theCounters a list of Measurementcounters
   */
  private void getAllSchedulingsDB(Meta_collection_sets mColSet) {

    theSchedulings = new HashMap();

    try {

      if (mColSet == null) {
        return;
      }

      Meta_schedulings ms = new Meta_schedulings(rockFactory);
      ms.setCollection_set_id(mColSet.getCollection_set_id());
      ms.setVersion_number(mColSet.getVersion_number());
      Meta_schedulingsFactory msF = new Meta_schedulingsFactory(rockFactory, ms, true);
      Vector<Meta_schedulings> targetSchedulings = msF.get();

      for (Iterator<Meta_schedulings> iter = targetSchedulings.iterator(); iter.hasNext();) {
        Meta_schedulings s = iter.next();

        String key = s.getVersion_number() + ":" + s.getCollection_id();
        if (theSchedulings.keySet().contains(key)) {
          ((Vector<Object>) theSchedulings.get(key)).add(s);
        } else {
          Vector<Object> v = new Vector<Object>();
          v.add(s);
          theSchedulings.put(key, v);
        }

      }
    } catch (SQLException e) {
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    } catch (RockException e) {
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    }
  }

  
  /**
   * Returns true if data conversion is needed for busy hour improvements. The
   * conversion is not needed if the old techpack level is equal to the latest
   * ENIQ level or the level where busy hour improvements were introduced.
   * 
   * @return true if data conversion is needed for busy hour improvements.
   */
  protected boolean isBusyHourConversionNeeded() {
    if (oldEniqLevel != null
        && (oldEniqLevel.equals(Constants.BH_IMPROVEMENT_ENIQ_LEVEL) || oldEniqLevel
            .equals(Constants.CURRENT_TECHPACK_ENIQ_LEVEL))) {
      return false;
    } else {
      return true;
    }
  }
  
  
  /**
   * 
   * @param oldName
   * @param oldVersion
   * @param newName
   * @param newVersion
   */
  public void copySets(String oldName, String oldVersion, String newName, String newVersion, String setType, RockFactory dwhrep)
  throws Exception {
   
	  boolean disableAllActionsAndSchedulingsFromSet = false;
	  
    if (oldName == null || oldVersion == null) {
      return;
    }
    // Get the ENIQ_LEVEL for the old techpack.
    this.oldEniqLevel = Utils.getTechpackEniqLevel(dwhrep, oldName+":"+oldVersion);

    // techpack
    Meta_collection_sets mcs = new Meta_collection_sets(rockFactory);
    mcs.setVersion_number(oldVersion);
    mcs.setCollection_set_name(oldName);
    Meta_collection_setsFactory mcsF = new Meta_collection_setsFactory(rockFactory, mcs, true);

    Meta_collection_sets mColSet = mcsF.getElementAt(0);

    if (mColSet == null) {
      return;
    }
    
    long tpindex = getTPMaxID();
    long setindex = getSetMaxID();
    long actionindex = getActionMaxID();
    long scheduleindex = getSchedulingMaxID();

    // copy Techpack
    Meta_collection_sets newMcs = new Meta_collection_sets(rockFactory);
    newMcs.setCollection_set_id(++tpindex);
    newMcs.setCollection_set_name(newName);
    newMcs.setDescription(mColSet.getDescription());
    newMcs.setVersion_number(newVersion);
    newMcs.setEnabled_flag("N");
    
    //TR: HL25629
    //If techpack type PM,CM,Toplogy and EVENT the type column in 
    //META_COLLECTION_SETS table should be "Techpack" and for Custom type the type 
    //should be "Custompack", for SYSTEM the type should be "Maintence".

    if(!setType.equals(MANAGE_NEWINTERFACE)){
      //For TechPacks
    	if (setType.equalsIgnoreCase("custom")) {
    		newMcs.setType("Custompack");
    	}else if(setType.equalsIgnoreCase("system")){
    	  newMcs.setType("Maintenance");
    	}else {
    		newMcs.setType("Techpack");

    	}
    }
    else
    { //For Interfaces
    	newMcs.setType(mColSet.getType());
    }
    
    newMcs.setNewItem(true);
    newMcs.saveDB();

    // sets
    Meta_collections mc = new Meta_collections(rockFactory);
    mc.setCollection_set_id(mColSet.getCollection_set_id());
    mc.setVersion_number(mColSet.getVersion_number());
    Meta_collectionsFactory mcF = new Meta_collectionsFactory(rockFactory, mc, true);
    Vector<Meta_collections> targetSets2 = mcF.get();

    for (Iterator<Meta_collections> iter2 = targetSets2.iterator(); iter2.hasNext();) {
      Meta_collections mSet = iter2.next();

      // copy Sets
      Meta_collections nmc = new Meta_collections(rockFactory);

      nmc = (Meta_collections) mSet.clone();
      nmc.setVersion_number(newVersion);
      nmc.setCollection_id(++setindex);
      nmc.setCollection_set_id(newMcs.getCollection_set_id());
      nmc.setEnabled_flag("N");

      //If Busyhour conversion is needed then all the actions and schedulings associated with this Aggregation set must be disabled/hold.
      if(nmc.getSettype().equalsIgnoreCase("Aggregator") && isBusyHourConversionNeeded()){
    	  disableAllActionsAndSchedulingsFromSet = true;
    	  nmc.setHold_flag("Y");
      }else{
    	  disableAllActionsAndSchedulingsFromSet = false;
      }

      
      nmc.setNewItem(true);
      nmc.saveDB();
      logger.finest("    " + nmc.getCollection_name() + " " + nmc.getVersion_number() + " "
          + nmc.getCollection_set_id() + " " + nmc.getCollection_id());

      // actions
      Meta_transfer_actions action = new Meta_transfer_actions(rockFactory);
      action.setCollection_set_id(mSet.getCollection_set_id());
      action.setVersion_number(mSet.getVersion_number());
      action.setCollection_id(mSet.getCollection_id());

      Meta_transfer_actionsFactory aF = new Meta_transfer_actionsFactory(rockFactory, action, true);
      Vector<Meta_transfer_actions> targetActions = aF.get();

      for (Iterator<Meta_transfer_actions> iter = targetActions.iterator(); iter.hasNext();) {
        Meta_transfer_actions a = iter.next();

        // copy actions
        Meta_transfer_actions newOne = new Meta_transfer_actions(rockFactory);
        newOne = (Meta_transfer_actions) a.clone();
        newOne.setVersion_number(newVersion);
        newOne.setTransfer_action_id(++actionindex);
        newOne.setCollection_set_id(newMcs.getCollection_set_id());
        newOne.setCollection_id(nmc.getCollection_id());
        newOne.setNewItem(true);

        //If Busyhour conversion is needed then the old Actions/Schedulings must be disabled/put on hold.
        if(disableAllActionsAndSchedulingsFromSet){
        	newOne.setEnabled_flag("N");
        }
        
        // Special handling for SQLJoiner and Unpartitioned loader actions: also
        // update the version number in the where clause.
        if (newOne.getAction_type().equalsIgnoreCase("SQLJoiner")
            || newOne.getAction_type().equalsIgnoreCase("UnPartitioned Loader")) {
          newOne.setWhere_clause(a.getWhere_clause().replace(":" + oldVersion, ":" + newVersion));
          logger.finest("      Action: " + newOne.getTransfer_action_name()
              + " fixed version number in the where clause.");
        }
        
        // eeoidiv, 20100906, HM70360:ASA Error -131 error for Aggregator_DC_E_RBS_DLBASEBANDPOOL_DAYBH
        // Special handling for Grouping actions:
        // update the version number in the Action_contents.
        // 20110727 eanguan :: For Remove grouping CR disabling this condition
//        if (newOne.getAction_type().equalsIgnoreCase("Grouping")) {
//          newOne.setAction_contents(a.getAction_contents().replace(oldVersion, newVersion)); // Replace ((802)) with ((803))
//          logger.finest("      Action: " + newOne.getTransfer_action_name()
//              + " fixed version number in the action contents to:"+newVersion+" (was="+oldVersion+")");
//        }

        newOne.saveDB();
        logger.finest("      Action: " + newOne.getTransfer_action_name() + " " + newOne.getVersion_number() + " "
            + newOne.getCollection_set_id() + " " + newOne.getCollection_id() + " " + newOne.getTransfer_action_id());
      }

      // schedulings
      Meta_schedulings ms = new Meta_schedulings(rockFactory);
      ms.setCollection_set_id(mSet.getCollection_set_id());
      ms.setVersion_number(mSet.getVersion_number());
      ms.setCollection_id(mSet.getCollection_id());

      Meta_schedulingsFactory msF = new Meta_schedulingsFactory(rockFactory, ms, true);
      Vector<Meta_schedulings> targetSchedulings = msF.get();

      for (Iterator<Meta_schedulings> iter = targetSchedulings.iterator(); iter.hasNext();) {
        Meta_schedulings s = iter.next();

        // copy schedulings
        Meta_schedulings newOne = new Meta_schedulings(rockFactory);
        newOne = (Meta_schedulings) s.clone();
        newOne.setVersion_number(newVersion);
        newOne.setId(++scheduleindex);
        newOne.setCollection_set_id(newMcs.getCollection_set_id());
        newOne.setCollection_id(nmc.getCollection_id());
        newOne.setNewItem(true);
        
        //If Busyhour conversion is needed then the old Actions must be disabled.
        if(disableAllActionsAndSchedulingsFromSet){
        	newOne.setHold_flag("Y");
        }

        newOne.saveDB();
        logger.finest("      Scheduling: " + newOne.getName() + " " + newOne.getVersion_number() + " "
            + newOne.getCollection_set_id() + " " + newOne.getCollection_id() + " " + newOne.getId());

      }

    }
  }

  /**
   * 
   * @param oldName
   * @param oldVersion
   * @param newName
   * @param newVersion
   */
  public void copyRenameSets(String oldName, String oldVersion, String newName, String newVersion) throws Exception {

    if (oldName == null || oldVersion == null) {
      return;
    }

    // techpack
    Meta_collection_sets mcs = new Meta_collection_sets(rockFactory);
    mcs.setVersion_number(oldVersion);
    mcs.setCollection_set_name(oldName);
    Meta_collection_setsFactory mcsF = new Meta_collection_setsFactory(rockFactory, mcs, true);

    Meta_collection_sets mColSet = mcsF.getElementAt(0);

    if (mColSet == null) {
      return;
    }

    long tpindex = getTPMaxID();
    long setindex = getSetMaxID();
    long actionindex = getActionMaxID();
    long scheduleindex = getSchedulingMaxID();

    // copy Techpack
    Meta_collection_sets newMcs = new Meta_collection_sets(rockFactory);
    newMcs.setCollection_set_id(++tpindex);
    newMcs.setCollection_set_name(newName);
    newMcs.setDescription(mColSet.getDescription());
    newMcs.setVersion_number(newVersion);
    newMcs.setEnabled_flag("Y");
    newMcs.setType(mColSet.getType());
    newMcs.setNewItem(true);
    newMcs.saveDB();
    logger.finest(newMcs.getCollection_set_name() + " " + newMcs.getVersion_number() + " "
        + newMcs.getCollection_set_id());

    // sets
    Meta_collections mc = new Meta_collections(rockFactory);
    mc.setCollection_set_id(mColSet.getCollection_set_id());
    mc.setVersion_number(mColSet.getVersion_number());
    Meta_collectionsFactory mcF = new Meta_collectionsFactory(rockFactory, mc, true);
    Vector<Meta_collections> targetSets2 = mcF.get();

    for (Iterator<Meta_collections> iter2 = targetSets2.iterator(); iter2.hasNext();) {
      Meta_collections mSet = iter2.next();

      // copy Sets
      Meta_collections nmc = new Meta_collections(rockFactory);

      nmc = (Meta_collections) mSet.clone();
      nmc = (Meta_collections) mSet.clone();
      nmc.setVersion_number(newVersion);
      nmc.setEnabled_flag("Y");
      nmc.setCollection_id(++setindex);
      nmc.setCollection_set_id(newMcs.getCollection_set_id());

      nmc.setNewItem(true);
      nmc.saveDB();
      logger.finest("    " + nmc.getCollection_name() + " " + nmc.getVersion_number() + " "
          + nmc.getCollection_set_id() + " " + nmc.getCollection_id());

      // actions
      Meta_transfer_actions action = new Meta_transfer_actions(rockFactory);
      action.setCollection_set_id(mSet.getCollection_set_id());
      action.setVersion_number(mSet.getVersion_number());
      action.setCollection_id(mSet.getCollection_id());

      Meta_transfer_actionsFactory aF = new Meta_transfer_actionsFactory(rockFactory, action, true);
      Vector<Meta_transfer_actions> targetActions = aF.get();

      for (Iterator<Meta_transfer_actions> iter = targetActions.iterator(); iter.hasNext();) {
        Meta_transfer_actions a = iter.next();

        // copy actions
        Meta_transfer_actions newOne = new Meta_transfer_actions(rockFactory);
        newOne = (Meta_transfer_actions) a.clone();
        newOne.setVersion_number(newVersion);
        newOne.setTransfer_action_id(++actionindex);
        newOne.setCollection_set_id(newMcs.getCollection_set_id());
        newOne.setCollection_id(nmc.getCollection_id());
        newOne.setEnabled_flag("Y");
        newOne.setNewItem(true);
        newOne.saveDB();
        logger.finest("      Action: " + newOne.getTransfer_action_name() + " " + newOne.getVersion_number() + " "
            + newOne.getCollection_set_id() + " " + newOne.getCollection_id() + " " + newOne.getTransfer_action_id());
        a.deleteDB();
      }

      // schedulings
      Meta_schedulings ms = new Meta_schedulings(rockFactory);
      ms.setCollection_set_id(mSet.getCollection_set_id());
      ms.setVersion_number(mSet.getVersion_number());
      ms.setCollection_id(mSet.getCollection_id());

      Meta_schedulingsFactory msF = new Meta_schedulingsFactory(rockFactory, ms, true);
      Vector<Meta_schedulings> targetSchedulings = msF.get();

      for (Iterator<Meta_schedulings> iter = targetSchedulings.iterator(); iter.hasNext();) {
        Meta_schedulings s = iter.next();

        // copy schedulings
        Meta_schedulings newOne = new Meta_schedulings(rockFactory);
        newOne = (Meta_schedulings) s.clone();
        newOne.setVersion_number(newVersion);
        newOne.setId(++scheduleindex);
        newOne.setCollection_set_id(newMcs.getCollection_set_id());
        newOne.setCollection_id(nmc.getCollection_id());
        newOne.setHold_flag("N");
        newOne.setNewItem(true);
        newOne.saveDB();
        logger.finest("      Scheduling: " + newOne.getName() + " " + newOne.getVersion_number() + " "
            + newOne.getCollection_set_id() + " " + newOne.getCollection_id() + " " + newOne.getId());
        s.deleteDB();
      }
      mSet.deleteDB();
    }

    mColSet.deleteDB();
  }

  private long getSchedulingMaxID() {

    try {

      Statement s = rockFactory.getConnection().createStatement();
      ResultSet r = s.executeQuery("select max(id) maxvalue from META_SCHEDULINGS");
      r.next();
      Long m = r.getLong("maxvalue");
      r.close();
      s.close();
      return m;

    } catch (Exception e) {
      logger.warning("Error while retrieving max META_SCHEDULINGS " + e.getMessage());
    }

    return 0;
  }

  private long getActionMaxID() {

    try {

      Statement s = rockFactory.getConnection().createStatement();
      ResultSet r = s.executeQuery("select max(TRANSFER_ACTION_ID) maxvalue from META_TRANSFER_ACTIONS");
      r.next();
      Long m = r.getLong("maxvalue");
      r.close();
      s.close();
      return m;

    } catch (Exception e) {
      logger.warning("Error while retrieving max TRANSFER_ACTION_ID " + e.getMessage());
    }

    return 0;
  }

  private long getSetMaxID() {

    try {

      Statement s = rockFactory.getConnection().createStatement();
      ResultSet r = s.executeQuery("select max(COLLECTION_ID) maxvalue from META_COLLECTIONS");
      r.next();
      Long m = r.getLong("maxvalue");
      r.close();
      s.close();
      return m;

    } catch (Exception e) {
      logger.warning("Error while retrieving max COLLECTION_ID " + e.getMessage());
    }

    return 0;
  }

  private long getTPMaxID() {

    try {

      Statement s = rockFactory.getConnection().createStatement();
      ResultSet r = s.executeQuery("select max(COLLECTION_SET_ID) maxvalue from META_COLLECTION_SETS");
      r.next();
      Long m = r.getLong("maxvalue");
      r.close();
      s.close();
      return m;

    } catch (Exception e) {
      logger.warning("Error while retrieving max COLLECTION_SET_ID " + e.getMessage());
    }

    return 0;
  }

  public void deleteTPSets(String name, String version) throws Exception {

    // fetch all sets

    // fetch techpack
    Meta_collection_sets rmcs = new Meta_collection_sets(rockFactory);
    rmcs.setCollection_set_name(name);
    rmcs.setVersion_number(version);
    Meta_collection_setsFactory rmcsF = new Meta_collection_setsFactory(rockFactory, rmcs);

    Meta_collection_sets tp = rmcsF.getElementAt(0);

    if (tp == null) {
      return;
    }

    Long collection_set_id = tp.getCollection_set_id();

    // fetch sets
    Meta_collections rmc = new Meta_collections(rockFactory);
    rmc.setCollection_set_id(collection_set_id);
    rmc.setVersion_number(version);
    Meta_collectionsFactory rmcF = new Meta_collectionsFactory(rockFactory, rmc);

    // iterate sets
    Iterator rriter = rmcF.get().iterator();
    while (rriter.hasNext()) {

      Meta_collections m = (Meta_collections) rriter.next();

      // fetch actions
      Meta_transfer_actions mta = new Meta_transfer_actions(rockFactory);
      mta.setCollection_id(m.getCollection_id());
      mta.setCollection_set_id(collection_set_id);
      Meta_transfer_actionsFactory mtaF = new Meta_transfer_actionsFactory(rockFactory, mta);
      Iterator iter2 = mtaF.get().iterator();
      while (iter2.hasNext()) {

        Meta_transfer_actions a = (Meta_transfer_actions) iter2.next();

        // remove action
        a.deleteDB();
      }

      // fetch schedulings
      Meta_schedulings ms = new Meta_schedulings(rockFactory);
      ms.setCollection_id(m.getCollection_id());
      ms.setCollection_set_id(collection_set_id);
      Meta_schedulingsFactory msF = new Meta_schedulingsFactory(rockFactory, ms);
      Iterator iter3 = msF.get().iterator();
      while (iter3.hasNext()) {

        Meta_schedulings a = (Meta_schedulings) iter3.next();

        // remove schedulings
        a.deleteDB();
      }

      // remove set
      m.deleteDB();

    }

    // remove techpack
    tp.deleteDB();

  }

  public void deleteINTFSets(String name, String version) throws Exception {

    // fetch all sets
    ArrayList removeList = new ArrayList();
    Meta_collections mc = new Meta_collections(rockFactory);
    mc.setVersion_number(version);
    Meta_collectionsFactory mcF = new Meta_collectionsFactory(rockFactory, mc);
    Iterator iter = mcF.get().iterator();
    while (iter.hasNext()) {

      Meta_collections m = (Meta_collections) iter.next();

      // fetch actions
      Meta_transfer_actions mta = new Meta_transfer_actions(rockFactory);
      mta.setCollection_id(m.getCollection_id());
      mta.setCollection_set_id(m.getCollection_set_id());
      Meta_transfer_actionsFactory mtaF = new Meta_transfer_actionsFactory(rockFactory, mta);
      Iterator iter2 = mtaF.get().iterator();
      while (iter2.hasNext()) {

        Meta_transfer_actions a = (Meta_transfer_actions) iter2.next();

        // collect all techpacks that have parser set that have
        // correct interfaceName.
        // this removes the long name interfaces

        String s = null;
        Properties p = Utils.stringToProperty(a.getAction_contents());
        if (p != null) {
          s = (String) p.get("interfaceName");
        }

        if (s != null)
          if (s.equalsIgnoreCase(name)) {
        	  long collectionSetId = a.getCollection_set_id();
              if (!removeList.contains(collectionSetId)) {
                // If this list does not contain element, then add it..
                removeList.add(a.getCollection_set_id());
              }
          }
      }
    }

    Iterator riter = removeList.iterator();
    while (riter.hasNext()) {

      Long collection_set_id = (Long) riter.next();

      // fetch sets
      Meta_collections rmc = new Meta_collections(rockFactory);
      rmc.setCollection_set_id(collection_set_id);
      rmc.setVersion_number(version);
      Meta_collectionsFactory rmcF = new Meta_collectionsFactory(rockFactory, rmc);

      // iterate sets
      Iterator rriter = rmcF.get().iterator();
      while (rriter.hasNext()) {

        Meta_collections m = (Meta_collections) rriter.next();

        // fetch actions
        Meta_transfer_actions mta = new Meta_transfer_actions(rockFactory);
        mta.setCollection_id(m.getCollection_id());
        mta.setCollection_set_id(collection_set_id);
        Meta_transfer_actionsFactory mtaF = new Meta_transfer_actionsFactory(rockFactory, mta);
        Iterator iter2 = mtaF.get().iterator();
        while (iter2.hasNext()) {

          Meta_transfer_actions a = (Meta_transfer_actions) iter2.next();

          // remove action
          a.deleteDB();
        }

        // fetch schedulings
        Meta_schedulings ms = new Meta_schedulings(rockFactory);
        ms.setCollection_id(m.getCollection_id());
        ms.setCollection_set_id(collection_set_id);
        Meta_schedulingsFactory msF = new Meta_schedulingsFactory(rockFactory, ms);
        Iterator iter3 = msF.get().iterator();
        while (iter3.hasNext()) {

          Meta_schedulings a = (Meta_schedulings) iter3.next();

          // remove schedulings
          a.deleteDB();
        }

        // remove set
        m.deleteDB();

      }

      // fetch techpack
      Meta_collection_sets rmcs = new Meta_collection_sets(rockFactory);
      rmcs.setCollection_set_id(collection_set_id);
      Meta_collection_setsFactory rmcsF = new Meta_collection_setsFactory(rockFactory, rmcs);

      // remove techpack
      (rmcsF.getElementAt(0)).deleteDB();
    }
  }

}
