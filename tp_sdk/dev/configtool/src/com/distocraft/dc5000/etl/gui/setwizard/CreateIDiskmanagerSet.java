package com.distocraft.dc5000.etl.gui.setwizard;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Logger;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_collectionsFactory;
import com.distocraft.dc5000.etl.rock.Meta_schedulings;
import com.distocraft.dc5000.etl.rock.Meta_schedulingsFactory;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actionsFactory;

/**
 * 
 * @author savinen Copyright Distocraft 2005
 * 
 * $id$
 */
public class CreateIDiskmanagerSet {

  private Logger log = Logger.getLogger("Wizard.CreateIDiskmanagerSet");

  protected String loaderTemplateName;

  protected String setTemplateName;

  protected String version;

  protected RockFactory rock;

  protected long techPackID;

  protected long maxSetID = 0;

  protected long maxActionID = 0;

  protected long day = 0;

  protected long hour = 0;

  private String pathSeparator = "/";

  private String name = "";

  private String name1 = "";

  private String itype = "";

  private String timeMask = "";

  private String fileMask = "";

  private String dateFormatInput = "";

  private String directoryDepth = "2";

  /**
   * constructor
   * 
   * 
   */
  public CreateIDiskmanagerSet(String objType, String version, RockFactory rock, long techPackID, String name,
      String name1) throws Exception {

    if (objType.equalsIgnoreCase("measurement")) {
      itype = "pmdata";
    } else if (objType.equalsIgnoreCase("reference")) {
      itype = "referencedata";
    } else {
      itype = "pmdata";
    }

    this.version = version;
    this.rock = rock;
    this.techPackID = techPackID;

    this.maxSetID = getSetMaxID(rock) + 1;
    this.maxActionID = getActionMaxID(rock) + 1;
    this.name = name;
    this.name1 = name1;
    this.day = 15;
    this.hour = 0;
    this.fileMask = ".*";

  }

  public void create() throws Exception {

    long iSet = 0;
    long iAction = 0;

    // set
    log.info("Creating set: " + name);
    createSet("Service", new Long(this.maxSetID + iSet).intValue(), "Diskmanager_" + name).insertToDB(rock);

    // actions
    iAction = createDiskmanager(iSet, iAction, "archive");
    iAction = createDiskmanager(iSet, iAction, "processed");
    iAction = createDiskmanager(iSet, iAction, "double");
    iAction = createDiskmanager(iSet, iAction, "failed");

    log.info("  Creating scheduling for Diskmanager_" + "Diskmanager_" + name);
    createSchedule(new Long(this.maxSetID + iSet).intValue(), "Diskmanager_" + name).insertToDB(this.rock);

  }

  private long createDiskmanager(long iSet, long iAction, String foldername) throws Exception {

    Properties prop = new Properties();

    prop.setProperty("diskManager.dir.inDir", "${ARCHIVE_DIR}/${OSS}" + pathSeparator + name1 + pathSeparator
        + foldername + pathSeparator);
    prop.setProperty("diskManager.dir.outDir", "${ARCHIVE_DIR}/${OSS}" + pathSeparator + name1 + pathSeparator
        + foldername + pathSeparator);
    prop.setProperty("diskManager.dir.archiveMode", "4");
    prop.setProperty("diskManager.dir.archivePrefix", "archive");
    prop.setProperty("diskManager.dir.fileAgeHour", Long.toString(this.hour));
    prop.setProperty("diskManager.dir.fileAgeDay", Long.toString(this.day));
    prop.setProperty("diskManager.dir.fileAgeMode", "0");
    prop.setProperty("diskManager.dir.timeMask", timeMask);
    prop.setProperty("diskManager.dir.fileMask", fileMask);
    prop.setProperty("diskManager.dir.dateFormatInput", dateFormatInput);
    prop.setProperty("diskManager.dir.dateFormatOutput", "yyyy-MM-dd");
    prop.setProperty("diskManager.dir.directoryDepth", directoryDepth);

    log.info("  Creating action Diskmanager_" + foldername);
    String actionName = "Diskmanager_" + foldername;
    int setId = new Long(this.maxSetID + iSet).intValue();
    createAction(iAction, "Diskmanager", setId,
        new Long(this.maxActionID + iAction).intValue(), actionName, propertyToString(prop))
        .insertToDB(rock);
    iAction++;

    return iAction;

  }

  private SetGenerator createSet(String type, long iSet, String Name) throws Exception {

    SetGenerator set = new SetGenerator();
    set.setCOLLECTION_NAME(Name);
    set.setCOLLECTION_ID(Long.toString(iSet));
    set.setMAX_ERRORS("0");
    set.setMAX_FK_ERRORS("0");
    set.setMAX_COL_LIMIT_ERRORS("0");
    set.setCHECK_FK_ERROR_FLAG("N");
    set.setCHECK_COL_LIMITS_FLAG("N");
    set.setVERSION_NUMBER(version);
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
    loadAction.setVERSION_NUMBER(version);
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

  protected Properties createProperty(String str) throws Exception {

    String result = "";

    Properties prop = new Properties();
    StringTokenizer st = new StringTokenizer(str, "=");
    String key = st.nextToken();
    String value = st.nextToken();
    prop.setProperty(key.trim(), value.trim());

    return prop;

  }

  private ScheduleGenerator createSchedule(long iSet, String name) throws Exception {

    ScheduleGenerator schedule = new ScheduleGenerator();
    schedule.setVERSION_NUMBER(this.version);
    schedule.setID(new Long(getScheduleMaxID(this.rock)+1));
    schedule.setEXECUTION_TYPE("weekly");
    schedule.setSCHEDULING_MONTH(new Long(1));
    schedule.setSCHEDULING_DAY(new Long(1));
    schedule.setSCHEDULING_HOUR(new Long(5));
    schedule.setSCHEDULING_MIN(new Long(0));
    schedule.setCOLLECTION_SET_ID(new Long(techPackID));
    schedule.setCOLLECTION_ID(new Long(iSet));
    schedule.setMON_FLAG("Y");
    schedule.setTUE_FLAG("Y");
    schedule.setWED_FLAG("Y");
    schedule.setTHU_FLAG("Y");
    schedule.setFRI_FLAG("Y");
    schedule.setSAT_FLAG("Y");
    schedule.setSUN_FLAG("Y");
    schedule.setHOLD_FLAG("Y");
    schedule.setSCHEDULING_YEAR(new Long(2006));
    schedule.setNAME(name);
           
    return schedule;
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

  
}
