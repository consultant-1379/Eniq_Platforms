package com.distocraft.dc5000.etl.gui.setwizard;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import java.util.List;
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
import com.distocraft.dc5000.repository.dwhrep.Measurementtable;
import com.distocraft.dc5000.repository.dwhrep.MeasurementtableFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementtype;
import com.distocraft.dc5000.repository.dwhrep.MeasurementtypeFactory;
import com.distocraft.dc5000.repository.dwhrep.Referencetable;
import com.distocraft.dc5000.repository.dwhrep.ReferencetableFactory;
import com.distocraft.dc5000.repository.dwhrep.Versioning;


/**
 * 
 * @author savinen Copyright Distocraft 2005
 * 
 * $id$
 */
public class CreateTPDirCheckerSet {

  protected String loaderTemplateName;

  protected String setTemplateName;

  protected Versioning version;
  
  protected RockFactory dwhrepRock;

  protected RockFactory rock;

  protected long techPackID;

  protected long maxSetID = 0;

  protected long maxActionID = 0;

  private String etldataDir = "${ETLDATA_DIR}";

  protected String topologyBaseDir = "${REFERENCE_DIR}";

  private String loaderlogDir = "${LOG_DIR}/iqloader";

  private String rejectedDir = "${REJECTED_DIR}";

  private String baseLoaderlogDir = "${LOG_DIR}/iqloader";

  private String baseRejectedDir = "${REJECTED_DIR}";
  
 
  
  private String pathSeparator = "/";

  private String name = "";

  /**
   * 
   * constructor
   * 
   * @param version
   * @param rock
   * @param techPackID
   * @throws Exception
   */
  public CreateTPDirCheckerSet(Versioning version, RockFactory dwhrepRock, RockFactory rock, long techPackID, String topologyName)
      throws Exception {

    this.version = version;
    this.dwhrepRock = dwhrepRock;
    this.rock = rock;
    this.techPackID = techPackID;

    this.maxSetID = getSetMaxID(rock) + 1;
    this.maxActionID = getActionMaxID(rock) + 1;
    this.name = topologyName;
    
    loaderlogDir+=pathSeparator+topologyName;
    rejectedDir+=pathSeparator+topologyName;
    
  }

  public void create(boolean topology) throws Exception {

    long iSet = 0;
    long iAction = 0;

    Properties prop = new Properties();
    prop.put("permission", "750");

    Properties loaderBaseProp = new Properties();
    loaderBaseProp.put("permission", "770");
    loaderBaseProp.put("owner", "dcuser");

    Properties rejectedBaseProp = new Properties();
    rejectedBaseProp.put("permission", "770");
    rejectedBaseProp.put("owner", "dcuser");

    Properties loaderProp = new Properties();
    loaderProp.put("permission", "750");
    loaderProp.put("owner", "dcuser");

    Properties rejectedProp = new Properties();
    rejectedProp.put("permission", "750");
    rejectedProp.put("owner", "dcuser");

    Logger.getLogger("Wizard.CreateTPDirCheckerSet.create").log(Level.INFO,
        "Creating directory checker Set: " + version.getTechpack_name());
    createSet("Install", new Long(this.maxSetID + iSet).intValue(), version.getTechpack_name()).insertToDB(rock);

    // basedir
    Logger.getLogger("Wizard.CreateTPDirCheckerSet.create").log(Level.INFO,
        "Creating directory checker Action: base_" + etldataDir);
    createAction(iAction, "CreateDir", new Long(this.maxSetID + iSet).intValue(),
        new Long(this.maxActionID + iAction).intValue(), "CreateDir_base_" + etldataDir, etldataDir + pathSeparator,
        propertyToString(prop)).insertToDB(rock);
    iAction++;

    // iqloaderBase
    Logger.getLogger("Wizard.CreateTPDirCheckerSet.create").log(Level.INFO,
        "Creating directory checker Action: CreateDir_iqloaderBase_" + etldataDir);
    createAction(iAction, "CreateDir", new Long(this.maxSetID + iSet).intValue(),
        new Long(this.maxActionID + iAction).intValue(), "CreateDir_iqloaderBase_" + baseLoaderlogDir,
        baseLoaderlogDir + pathSeparator, propertyToString(loaderBaseProp)).insertToDB(rock);
    iAction++;

    // rejectedBAse
    Logger.getLogger("Wizard.CreateTPDirCheckerSet.create").log(Level.INFO,
        "Creating directory checker Action: CreateDir_rejectedBase_" + etldataDir);
    createAction(iAction, "CreateDir", new Long(this.maxSetID + iSet).intValue(),
        new Long(this.maxActionID + iAction).intValue(), "CreateDir_rejectedBase_" + baseRejectedDir,
        baseRejectedDir + pathSeparator, propertyToString(rejectedBaseProp)).insertToDB(rock);
    iAction++;
       
    // iqloader+techpack
    Logger.getLogger("Wizard.CreateTPDirCheckerSet.create").log(Level.INFO,
        "Creating directory checker Action: CreateDir_iqloaderBase_Techpack_" + etldataDir);
    createAction(iAction, "CreateDir", new Long(this.maxSetID + iSet).intValue(),
        new Long(this.maxActionID + iAction).intValue(), "CreateDir_iqloaderBase_Techpack_" + loaderlogDir,
        loaderlogDir + pathSeparator, propertyToString(loaderBaseProp)).insertToDB(rock);
    iAction++;

    // rejected+techpack
    Logger.getLogger("Wizard.CreateTPDirCheckerSet.create").log(Level.INFO,
        "Creating directory checker Action: CreateDir_rejectedBase_Techpack_" + etldataDir);
    createAction(iAction, "CreateDir", new Long(this.maxSetID + iSet).intValue(),
        new Long(this.maxActionID + iAction).intValue(), "CreateDir_rejectedBase_Techpack_" + rejectedDir,
        rejectedDir + pathSeparator, propertyToString(rejectedBaseProp)).insertToDB(rock);
    iAction++;

    if (topology) {

      // basedir
      Logger.getLogger("Wizard.CreateTPDirCheckerSet.create").log(Level.INFO,
          "Creating directory checker Action: base_" + topologyBaseDir);
      createAction(iAction, "CreateDir", new Long(this.maxSetID + iSet).intValue(),
          new Long(this.maxActionID + iAction).intValue(), "CreateDir_base_" + topologyBaseDir,
          topologyBaseDir + pathSeparator, propertyToString(prop)).insertToDB(rock);
      iAction++;

      // meas
      Logger.getLogger("Wizard.CreateTPDirCheckerSet.create").log(Level.INFO,
          "Creating directory checker Action: base_" + topologyBaseDir + pathSeparator + name + pathSeparator);
      createAction(iAction, "CreateDir", new Long(this.maxSetID + iSet).intValue(),
          new Long(this.maxActionID + iAction).intValue(), "CreateDir_base_" + name,
          topologyBaseDir + pathSeparator + name + pathSeparator, propertyToString(prop)).insertToDB(rock);
      iAction++;
      
      //Query q = session.createQuery("from ReferenceTable where VERSIONID=?");
      //q.setString(0,version.getVersionid());
      
      Referencetable rt = new Referencetable(dwhrepRock);
      rt.setVersionid(version.getVersionid());
      ReferencetableFactory rtf = new ReferencetableFactory(dwhrepRock,rt);

      
      Iterator iTable = rtf.get().iterator();

      while (iTable.hasNext()) {

        int uPolicy = 0;
        Referencetable rtable = (Referencetable) iTable.next();

        if (rtable.getUpdate_policy() != null) {
          uPolicy = rtable.getUpdate_policy().intValue();
        }

        if (uPolicy == 1) {
        	
        	iAction = createLogCheckers(iSet, iAction, rtable.getTypename(), rejectedProp, loaderProp);
        	        	
        } else if (uPolicy == 2) {
    
        	iAction = createLogCheckers(iSet, iAction, rtable.getTypename(), rejectedProp, loaderProp);
        	
        } else {
        	
        }
      }
    }
    
    //Query qm = session.createQuery("from MeasurementType where VERSIONID=?");
    //qm.setString(0,version.getVersionid());
    
    Measurementtype mt = new Measurementtype(dwhrepRock);
    mt.setVersionid(version.getVersionid());
    MeasurementtypeFactory mtf = new MeasurementtypeFactory(dwhrepRock,mt);
    
    Iterator iVer = mtf.get().iterator();

    // measurementtypes
    while (iVer.hasNext()) {

      Measurementtype types = (Measurementtype) iVer.next();

      //Iterator iTable = types.getMeasurementTables().iterator();
      
      Measurementtable mta = new Measurementtable(dwhrepRock);
      mta.setTypeid(types.getTypeid());
      MeasurementtableFactory mtaf = new MeasurementtableFactory(dwhrepRock,mta);
      Iterator iTable = mtaf.get().iterator();

      // measurementtypes
      while (iTable.hasNext()) {

        Measurementtable table = (Measurementtable) iTable.next();

        if (table.getTablelevel().equalsIgnoreCase("raw")) {
          iAction = createETLCheckers(iSet, iAction, types.getFoldername().toLowerCase(), prop);
          iAction = createLogCheckers(iSet, iAction, types.getFoldername() + "_RAW", rejectedProp, loaderProp);
        }

        if (table.getTablelevel().equalsIgnoreCase("day"))
          iAction = createLogCheckers(iSet, iAction, types.getFoldername() + "_DAY", rejectedProp, loaderProp);
        if (table.getTablelevel().equalsIgnoreCase("daybh"))
          iAction = createLogCheckers(iSet, iAction, types.getFoldername() + "_DAYBH", rejectedProp, loaderProp);
        if (table.getTablelevel().equalsIgnoreCase("rankbh"))
          iAction = createLogCheckers(iSet, iAction, types.getFoldername() + "_RANKBH", rejectedProp, loaderProp);
       
        if (types.getJoinable()!=null && types.getJoinable().length() > 0){
          iAction = createLogCheckers(iSet, iAction, types.getFoldername() + "_PREV_RAW", rejectedProp, loaderProp);         
        }
        
      }
    }
    
    
    //qm = session.createQuery("from ReferenceTable where VERSIONID=?");
    //qm.setString(0,version.getVersionid());
    
    Referencetable rt = new Referencetable(dwhrepRock);
    rt.setVersionid(version.getVersionid());
    ReferencetableFactory rtf = new ReferencetableFactory(dwhrepRock,rt);


    iVer = rtf.get().iterator();

    // ReferenceTables
    while (iVer.hasNext()) {

      Referencetable rTable = (Referencetable) iVer.next();
      iAction = createETLCheckers(iSet, iAction, rTable.getObjectname().toLowerCase(), prop);
      iAction = createLogCheckers(iSet, iAction, rTable.getObjectname() + "_RAW", rejectedProp, loaderProp);      
    }    
  }

  private long createETLCheckers(long iSet, long iAction, String foldername, Properties prop) throws Exception {

    // meas
    Logger.getLogger("Wizard.CreateTPDirCheckerSet.create").log(Level.INFO,
        "Creating directory checker Action: raw_" + foldername);
    createAction(iAction, "CreateDir", new Long(this.maxSetID + iSet).intValue(),
        new Long(this.maxActionID + iAction).intValue(), "CreateDir_" + foldername,
        etldataDir + pathSeparator + foldername + pathSeparator, propertyToString(prop)).insertToDB(rock);
    iAction++;

    // raw
    Logger.getLogger("Wizard.CreateTPDirCheckerSet.create").log(Level.INFO,
        "Creating directory checker Action: raw_" + foldername);
    createAction(iAction, "CreateDir", new Long(this.maxSetID + iSet).intValue(),
        new Long(this.maxActionID + iAction).intValue(), "CreateDir_" + foldername + "_raw",
        etldataDir + pathSeparator + foldername + pathSeparator + "raw" + pathSeparator, propertyToString(prop))
        .insertToDB(rock);
    iAction++;

    // joined
    Logger.getLogger("Wizard.CreateTPDirCheckerSet.create").log(Level.INFO,
        "Creating directory checker Action: joined_" + foldername);
    createAction(iAction, "CreateDir", new Long(this.maxSetID + iSet).intValue(),
        new Long(this.maxActionID + iAction).intValue(), "CreateDir_" + foldername + "_joined",
        etldataDir + pathSeparator + foldername + pathSeparator + "joined" + pathSeparator, propertyToString(prop))
        .insertToDB(rock);
    iAction++;

    return iAction;

  }

  private long createLogCheckers(long iSet, long iAction, String foldername, Properties rejProp, Properties loadProp)
      throws Exception {

    // loaderLog
    Logger.getLogger("Wizard.CreateTPDirCheckerSet.create").log(Level.INFO,
        "Creating directory checker Action: loaderLog_" + foldername);
    createAction(iAction, "CreateDir", new Long(this.maxSetID + iSet).intValue(),
        new Long(this.maxActionID + iAction).intValue(), "CreateDir_" + foldername + "_loader",
        loaderlogDir + pathSeparator + foldername + pathSeparator, propertyToString(rejProp)).insertToDB(rock);
    iAction++;

    // rejected
    Logger.getLogger("Wizard.CreateTPDirCheckerSet.create").log(Level.INFO,
        "Creating directory checker Action: rejected_" + foldername);
    createAction(iAction, "CreateDir", new Long(this.maxSetID + iSet).intValue(),
        new Long(this.maxActionID + iAction).intValue(), "CreateDir_" + foldername + "_rejected",
        rejectedDir + pathSeparator + foldername + pathSeparator, propertyToString(loadProp)).insertToDB(rock);
    iAction++;

    return iAction;

  }

  private SetGenerator createSet(String type, long iSet, String techPackName) throws Exception {

    SetGenerator set = new SetGenerator();
    set.setCOLLECTION_NAME("Directory_Checker_" + techPackName);
    set.setCOLLECTION_ID(Long.toString(iSet));
    set.setMAX_ERRORS("0");
    set.setMAX_FK_ERRORS("0");
    set.setMAX_COL_LIMIT_ERRORS("0");
    set.setCHECK_FK_ERROR_FLAG("N");
    set.setCHECK_COL_LIMITS_FLAG("N");
    set.setVERSION_NUMBER(version.getTechpack_version());
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
    loadAction.setVERSION_NUMBER(version.getTechpack_version());
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
