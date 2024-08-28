package com.distocraft.dc5000.etl.gui.setwizard;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.velocity.app.Velocity;


import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_collectionsFactory;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actionsFactory;
import com.distocraft.dc5000.repository.dwhrep.Versioning;


/**
 * 
 * @author savinen Copyright Distocraft 2005
 * 
 * $id$
 */
public class CreateTPDiskmanagerSet {

  protected String loaderTemplateName;

  protected String setTemplateName;

  protected Versioning version;

  protected RockFactory rock;

  protected long techPackID;

  protected long maxSetID = 0;

  protected long maxActionID = 0;

  protected long day = 0;

  protected long hour = 0;

  private String loaderlogDir = "${LOG_DIR}/iqloader";
  
  private String rejectedDir = "${REJECTED_DIR}";
  
  private String TPLogDir = "${LOG_DIR}/engine/";

  private String pathSeparator = "/";

  private String timeMask = "";

  private String dateFormatInput = "";

  private String directoryDepth = "5";
  private String techPack = "";

  /**
   * constructor
   * 
   * 
   */
  public CreateTPDiskmanagerSet(Versioning version, RockFactory rock, long techPackID, String techPackName) throws Exception {

    this.version = version;
    this.rock = rock;
    this.techPackID = techPackID;

    this.maxSetID = getSetMaxID(rock) + 1;
    this.maxActionID = getActionMaxID(rock) + 1;

    this.day = 2;
    this.hour = 0;
    this.techPack = techPackName;

  }

  public void create() throws Exception {

    long iSet = 0;
    long iAction = 0;

    // set
    Logger.getLogger("Wizard.CreateTPDiskmanagerSet.create").log(Level.INFO,
        "Creating Diskmanager Set: " + version.getTechpack_name());
    createSet("Service", new Long(this.maxSetID + iSet).intValue(), this.version.getTechpack_name()).insertToDB(rock);

    // actions    
    iAction = createDiskmanager(iSet, iAction, "loaderLog"+pathSeparator+techPack, loaderlogDir+pathSeparator+techPack);
    iAction = createDiskmanager(iSet, iAction, "rejected"+pathSeparator+techPack, rejectedDir+pathSeparator+techPack);
    iAction = createDiskmanager(iSet, iAction, "techpack"+pathSeparator+techPack, TPLogDir+pathSeparator+techPack);

  }

  private long createDiskmanager(long iSet, long iAction, String name, String dir) throws Exception {

    Properties prop = new Properties();

    prop.setProperty("diskManager.dir.inDir", dir + pathSeparator);
    prop.setProperty("diskManager.dir.outDir", dir + pathSeparator);
    prop.setProperty("diskManager.dir.archiveMode", "3");
    prop.setProperty("diskManager.dir.archivePrefix", "archive");
    prop.setProperty("diskManager.dir.fileAgeHour", Long.toString(this.hour));
    prop.setProperty("diskManager.dir.fileAgeDay", Long.toString(this.day));
    prop.setProperty("diskManager.dir.fileAgeMode", "0");
    prop.setProperty("diskManager.dir.timeMask", timeMask);
    prop.setProperty("diskManager.dir.fileMask", "(?!.*\\.zip$).*");
    prop.setProperty("diskManager.dir.dateFormatInput", dateFormatInput);
    prop.setProperty("diskManager.dir.dateFormatOutput", "yyyy-MM-dd");
    prop.setProperty("diskManager.dir.directoryDepth", directoryDepth);

    Logger.getLogger("Wizard.CreateTPDiskmanagerSet.create").log(Level.INFO, "Creating diskmanager Action: " + name);
    createAction(iAction, "Diskmanager", new Long(this.maxSetID + iSet).intValue(),
        new Long(this.maxActionID + iAction).intValue(), name, propertyToString(prop)).insertToDB(rock);
    iAction++;

    return iAction;

  }

  private SetGenerator createSet(String type, long iSet, String techPackName) throws Exception {

    SetGenerator set = new SetGenerator();
    set.setCOLLECTION_NAME("Diskmanager_" + techPackName);
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
