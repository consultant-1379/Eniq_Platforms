package com.distocraft.dc5000.etl.gui.setwizard;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_collectionsFactory;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actionsFactory;


/**
 * 
 * @author savinen Copyright Distocraft 2005
 * 
 * $id$
 */
public class CreateIDirCheckerSet {
  
  private Logger log = Logger.getLogger("Wizard.CreateIDirCheckerSet");

  protected String loaderTemplateName;

  protected String setTemplateName;

  protected String version;

  protected RockFactory rock;

  protected long techPackID;

  protected long maxSetID = 0;

  protected long maxActionID = 0;

  private String name = "";
  private String itype = "";
  private String name1 = "";
  private String pathSeparator = "/";

  /**
   * 
   * constructor
   * 
   * @param version
   * @param rock
   * @param techPackID
   * @param name
   * @throws Exception
   */
  public CreateIDirCheckerSet(String objType,String version, RockFactory rock, long techPackID, String name,String name1) throws Exception {

     if (objType.equalsIgnoreCase("measurement")) {
      itype = "pmdata";
    } else if (objType.equalsIgnoreCase("reference")) {
      itype = "referencedata";
    } else {
      itype = "pmdata";
    }
    this.rock = rock;
    this.techPackID = techPackID;
    this.version = version;
    this.maxSetID = getSetMaxID(rock) + 1;
    this.maxActionID = getActionMaxID(rock) + 1;

    this.name = name;
    this.name1 = name1;
    
  }

  public void create() throws Exception {

    long iSet = 0;
    long iAction = 0;

    log.info("Creating set Directory_Checker" + this.name);
    createSet("Install", new Long(this.maxSetID + iSet).intValue()).insertToDB(rock);

    Properties propIn = createProperty("permission=770");
    propIn.setProperty("group","dcdata");
    Properties prop = createProperty("permission=770");

    // ossDir
    log.info("  Creating action CreateDir" + name);
    createAction(iAction + 1, "CreateDir", new Long(this.maxSetID + iSet).intValue(),
        new Long(this.maxActionID + iAction).intValue(),
        "CreateDir_ossDir",
        "${ARCHIVE_DIR}/${OSS}/", propertyToString(prop)).insertToDB(rock);
    iAction++;
   
    // baseDir
    log.info("  Creating action CreateDir" + name);
    createAction(iAction + 1, "CreateDir", new Long(this.maxSetID + iSet).intValue(),
        new Long(this.maxActionID + iAction).intValue(),
        "CreateDir_base",
        "${ARCHIVE_DIR}/${OSS}/"+name1 + pathSeparator, propertyToString(prop)).insertToDB(rock);
    iAction++;
    
    log.info("  Creating action CreateDir_in");
    createAction(iAction + 1, "CreateDir", new Long(this.maxSetID + iSet).intValue(),
        new Long(this.maxActionID + iAction).intValue(), "CreateDir_in",
        "${PMDATA_DIR}/${OSS}" + pathSeparator, propertyToString(prop))
        .insertToDB(rock);
    iAction++;
    
    iAction = createCheckers(iSet, iAction, "archive", prop);
    iAction = createCheckers(iSet, iAction, "processed", prop);
    iAction = createCheckers(iSet, iAction, "double", prop);
    iAction = createCheckers(iSet, iAction, "failed", prop);

    // adapter_tmp
    log.info("  Creating action adapter_tmp");
    createAction(iAction + 1, "CreateDir", new Long(this.maxSetID + iSet).intValue(),
        new Long(this.maxActionID + iAction).intValue(),
        "CreateDir_output",
        "${ETLDATA_DIR}/adapter_tmp/", propertyToString(prop)).insertToDB(rock);
    iAction++;
    
    // adapter_tmp
    log.info("  Creating action adapter_tmp");
    createAction(iAction + 1, "CreateDir", new Long(this.maxSetID + iSet).intValue(),
        new Long(this.maxActionID + iAction).intValue(),
        "CreateDir_output",
        "${ETLDATA_DIR}/adapter_tmp/"+name1 + pathSeparator, propertyToString(prop)).insertToDB(rock);
    iAction++;
    
  }

  private long createCheckers(long iSet, long iAction, String foldername, Properties prop) throws Exception {

    // raw
    log.info("  Creating action CreateDir_" + name1 + "/" + foldername);
    createAction(iAction + 1, "CreateDir", new Long(this.maxSetID + iSet).intValue(),
        new Long(this.maxActionID + iAction).intValue(), "CreateDir_" + foldername,
        "${ARCHIVE_DIR}/${OSS}" + pathSeparator + name1 + pathSeparator + foldername + pathSeparator, propertyToString(prop))
        .insertToDB(rock);
    iAction++;

    return iAction;

  }

  private SetGenerator createSet(String type, long iSet) throws Exception {

    SetGenerator set = new SetGenerator();
    set.setCOLLECTION_NAME("Directory_Checker_" + this.name);
    set.setCOLLECTION_ID(Long.toString(iSet));
    set.setMAX_ERRORS("0");
    set.setMAX_FK_ERRORS("0");
    set.setMAX_COL_LIMIT_ERRORS("0");
    set.setCHECK_FK_ERROR_FLAG("N");
    set.setCHECK_COL_LIMITS_FLAG("N");
    set.setVERSION_NUMBER(version);
    set.setCOLLECTION_SET_ID(Long.toString(techPackID));
    set.setPRIORITY("0");
    set.setQUEUE_TIME_LIMIT("30");
    set.setENABLED_FLAG("Y");
    set.setSETTYPE(type);
    set.setFOLDABLE_FLAG("Y");
    set.setHOLD_FLAG("N");

    return set;

  }

  private ActionGenerator createAction(long order, String type, long iSet, int iAct, String foldername, String where,
      String actionContents) throws Exception {

    ActionGenerator loadAction = new ActionGenerator();
    loadAction.setVERSION_NUMBER(version);
    loadAction.setTRANSFER_ACTION_ID(Long.toString(iAct));
    loadAction.setCOLLECTION_ID(Long.toString(iSet));
    loadAction.setCOLLECTION_SET_ID(Long.toString(techPackID));
    loadAction.setACTION_TYPE(type);
    loadAction.setTRANSFER_ACTION_NAME(foldername);
    loadAction.setORDER_BY_NO(Long.toString(order));
    loadAction.setWHERE_CLAUSE(where);
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

    Properties prop = new Properties();
    StringTokenizer st = new StringTokenizer(str, "=");
    String key = st.nextToken();
    String value = st.nextToken();
    prop.setProperty(key.trim(), value.trim());

    return prop;

  }

}
