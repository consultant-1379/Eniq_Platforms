package com.distocraft.dc5000.etl.gui.setwizard;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_collectionsFactory;
import com.distocraft.dc5000.etl.rock.Meta_schedulings;
import com.distocraft.dc5000.etl.rock.Meta_schedulingsFactory;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actionsFactory;
import com.distocraft.dc5000.repository.dwhrep.Versioning;


/**
 * 
 * @author savinen Copyright Distocraft 2005
 * 
 * $id$
 */
public class CreateDWHMSet {

  private Logger log = Logger.getLogger("Wizard.CreateDWHMSet");
  
  protected String loaderTemplateName;

  protected String setTemplateName;

  protected Versioning version;

  protected RockFactory rock;

  protected long techPackID;

  protected long maxSetID = 0;

  protected long maxActionID = 0;

  protected long maxSchedulingID = 0;
  
  private String techPack = "";
  
  private boolean doSchedulings = false;

  /**
   * constructor
   * 
   * 
   */
  public CreateDWHMSet(Versioning version, RockFactory rock, long techPackID, String techPackName, boolean schedulings) throws Exception {

    this.version = version;
    this.rock = rock;
    this.techPackID = techPackID;
    this.maxSetID = getSetMaxID(rock) + 1;
    this.maxActionID = getActionMaxID(rock) + 1;
    this.maxSchedulingID = getScheduleMaxID(rock) + 1;
    this.techPack = techPackName;
    this.doSchedulings = schedulings;

  }

  public void create() throws Exception {
    
    long iSet = 0;
    long iAction = 0;
    long iOrder = 0;
    
    long iScheduling = 0;
    
    Properties prop = new Properties();
    prop.setProperty("tablename","DWHM");
    
    // set
    
    log.info("Creating set DWHM_Partition_"+techPack);    
    createSet("DWHM_Partition_"+techPack,"Partition", new Long(this.maxSetID + iSet).intValue(), this.version.getTechpack_name()).insertToDB(rock);    

    // Partition action 
    log.info("  Creating action PartitionAction_"+techPack);   
    createAction(iOrder, "PartitionAction", new Long(this.maxSetID + iSet).intValue(),
        new Long(this.maxActionID + iAction).intValue(), "PartitionAction_"+techPack, propertyToString(prop)).insertToDB(rock);
    
    iAction++;
    
    if (doSchedulings) {

      // create scheduling
      String holdFlag = "N";
      iScheduling++;
      
      String name = "DWHM_Partition_" + techPack;
      
      log.info("  Creating Scheduling " + name);

      ScheduleGenerator sg = createWaitSchedule(new Long(this.maxSchedulingID + iScheduling).intValue(), this.techPackID,
          new Long(this.maxSetID + iSet).intValue(), name, holdFlag);
      sg.insertToDB(this.rock);
    }
    
    iSet++;

    // set
    log.info("Creating set DWHM_StorageTimeUpdate_"+techPack);    
    createSet("DWHM_StorageTimeUpdate_"+techPack,"Support", new Long(this.maxSetID + iSet).intValue(), this.version.getTechpack_name()).insertToDB(rock);  

    // Storagetime action  
    log.info("  Creating action StorageTimeAction_"+techPack);   
    createAction(iOrder,"StorageTimeAction" , new Long(this.maxSetID + iSet).intValue(),
        new Long(this.maxActionID + iAction).intValue(), "StorageTimeAction_"+techPack,propertyToString(prop)).insertToDB(rock);

    iAction++;
    iOrder++;
    
    // Partition action 
    log.info("  Creating action PartitionAction_"+techPack);   
    createAction(iOrder, "PartitionAction", new Long(this.maxSetID + iSet).intValue(),
        new Long(this.maxActionID + iAction).intValue(), "PartitionAction_"+techPack, propertyToString(prop)).insertToDB(rock);
  
    iAction++;
    iSet++;
    iOrder=0;

    // set
    log.info("Creating set DWHM_Install_"+techPack);      
    createSet("DWHM_Install_"+techPack,"Install", new Long(this.maxSetID + iSet).intValue(), this.version.getTechpack_name()).insertToDB(rock);  
    
    // VersionUpdate action
    log.info("  Creating action VersionUpdate_"+techPack);   
    createAction(iOrder,"VersionUpdate" , new Long(this.maxSetID + iSet).intValue(),
        new Long(this.maxActionID + iAction).intValue(), "VersionUpdate_"+techPack,propertyToString(prop)).insertToDB(rock);

    iAction++;
    iOrder++;

    // Storagetime action    
    log.info("  Creating action StorageTimeAction_"+techPack);    
    createAction(iOrder,"StorageTimeAction" , new Long(this.maxSetID + iSet).intValue(),
        new Long(this.maxActionID + iAction).intValue(), "StorageTimeAction_"+techPack,propertyToString(prop)).insertToDB(rock);

    iAction++;
    iOrder++;
    
    // Partition action   
    log.info("  Creating action PartitionAction_"+techPack);   
    createAction(iOrder, "PartitionAction", new Long(this.maxSetID + iSet).intValue(),
        new Long(this.maxActionID + iAction).intValue(), "PartitionAction_"+techPack, propertyToString(prop)).insertToDB(rock);
    
  }


  private SetGenerator createSet(String name,String type, long iSet, String techPackName) throws Exception {

    SetGenerator set = new SetGenerator();
    set.setCOLLECTION_NAME(name);
    set.setCOLLECTION_ID(Long.toString(iSet));
    set.setMAX_ERRORS("0");
    set.setMAX_FK_ERRORS("0");
    set.setMAX_COL_LIMIT_ERRORS("0");
    set.setCHECK_FK_ERROR_FLAG("N");
    set.setCHECK_COL_LIMITS_FLAG("N");
    set.setVERSION_NUMBER(version.getTechpack_version());
    set.setCOLLECTION_SET_ID(Long.toString(techPackID));
    set.setPRIORITY("3");
    set.setQUEUE_TIME_LIMIT("30");
    set.setENABLED_FLAG("Y");
    set.setSETTYPE(type);
    set.setFOLDABLE_FLAG("Y");
    set.setHOLD_FLAG("N");

    return set;

  }

  private ActionGenerator createAction(long order, String type, long iSet, int iAct, String foldername,
      String actionContents) throws Exception {

    ActionGenerator loadAction = new ActionGenerator();
    loadAction.setVERSION_NUMBER(version.getTechpack_version());
    loadAction.setTRANSFER_ACTION_ID(Long.toString(iAct));
    loadAction.setCOLLECTION_ID(Long.toString(iSet));
    loadAction.setCOLLECTION_SET_ID(Long.toString(techPackID));
    loadAction.setACTION_TYPE(type);
    loadAction.setTRANSFER_ACTION_NAME(foldername);
    loadAction.setORDER_BY_NO(Long.toString(order));
    loadAction.setWHERE_CLAUSE("");
    loadAction.setACTION_CONTENTS(actionContents);
    loadAction.setENABLED_FLAG("Y");
    loadAction.setCONNECTION_ID("2");

    return loadAction;

  }
  
  protected ScheduleGenerator createWaitSchedule(long scheduleID, long techpackID, long setID, String name,
      String holdFlag) throws Exception {

    ScheduleGenerator schedule = new ScheduleGenerator();

    schedule.setVERSION_NUMBER(this.version.getTechpack_version());
    schedule.setID(new Long(scheduleID));
    schedule.setEXECUTION_TYPE("wait");
    schedule.setCOLLECTION_SET_ID(new Long(techpackID));
    schedule.setCOLLECTION_ID(new Long(setID));
    schedule.setNAME(name);
    schedule.setHOLD_FLAG(holdFlag);

    return schedule;
  }

  private long getSetMaxID(RockFactory rockFact) throws Exception {

    Meta_collections coll = new Meta_collections(rockFact);

    Meta_collectionsFactory dbCollections = new Meta_collectionsFactory(rockFact, coll);

    long largest = 0;
    Vector dbVec = dbCollections.get();
    for (int i = 0; i < dbVec.size(); i++) {
      Meta_collections collection = (Meta_collections) dbVec.elementAt(i);
      if (largest < collection.getCollection_id().longValue())
        largest = collection.getCollection_id().longValue();
    }

    return largest;

  }

  private long getActionMaxID(RockFactory rockFact) throws Exception {

    Meta_transfer_actions act = new Meta_transfer_actions(rockFact);

    Meta_transfer_actionsFactory dbCollections = new Meta_transfer_actionsFactory(rockFact, act);

    long largest = 0;
    Vector dbVec = dbCollections.get();
    for (int i = 0; i < dbVec.size(); i++) {
      Meta_transfer_actions action = (Meta_transfer_actions) dbVec.elementAt(i);
      if (largest < action.getTransfer_action_id().longValue())
        largest = action.getTransfer_action_id().longValue();
    }

    return largest;

  }
  
  private long getScheduleMaxID(RockFactory rockFact) throws Exception {

    Meta_schedulings sche = new Meta_schedulings(rockFact);

    Meta_schedulingsFactory dbScheduling = new Meta_schedulingsFactory(rockFact, sche);

    long largest = 0;
    Vector dbVec = dbScheduling.get();
    for (int i = 0; i < dbVec.size(); i++) {
      Meta_schedulings scheduling = (Meta_schedulings) dbVec.elementAt(i);
      if (largest < scheduling.getId().longValue())
        largest = scheduling.getId().longValue();
    }

    return largest;

  }

  protected Properties stringToProperty(String str) throws Exception {

    Properties prop = null;

    if (str != null && str.length() > 0) {
      ByteArrayInputStream bais = new ByteArrayInputStream(str.getBytes());
      prop.load(bais);
      bais.close();
    }

    return prop;

  }
  
  protected String propertyToString(Properties prop) throws Exception {

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    prop.store(baos, "");

    return baos.toString();
  }
}
