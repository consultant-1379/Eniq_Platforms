package com.distocraft.dc5000.etl.gui.setwizard;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_collectionsFactory;
import com.distocraft.dc5000.etl.rock.Meta_schedulings;
import com.distocraft.dc5000.etl.rock.Meta_schedulingsFactory;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actionsFactory;
import com.distocraft.dc5000.repository.dwhrep.Aggregation;
import com.distocraft.dc5000.repository.dwhrep.AggregationFactory;
import com.distocraft.dc5000.repository.dwhrep.Aggregationrule;
import com.distocraft.dc5000.repository.dwhrep.AggregationruleFactory;
import com.distocraft.dc5000.repository.dwhrep.Dwhpartition;
import com.distocraft.dc5000.repository.dwhrep.DwhpartitionFactory;
import com.distocraft.dc5000.repository.dwhrep.Dwhtype;
import com.distocraft.dc5000.repository.dwhrep.DwhtypeFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementcolumn;
import com.distocraft.dc5000.repository.dwhrep.MeasurementcolumnFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementcounter;
import com.distocraft.dc5000.repository.dwhrep.MeasurementcounterFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementkey;
import com.distocraft.dc5000.repository.dwhrep.MeasurementkeyFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementtable;
import com.distocraft.dc5000.repository.dwhrep.MeasurementtableFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementtype;
import com.distocraft.dc5000.repository.dwhrep.MeasurementtypeFactory;
import com.distocraft.dc5000.repository.dwhrep.Tpactivation;
import com.distocraft.dc5000.repository.dwhrep.TpactivationFactory;
import com.distocraft.dc5000.repository.dwhrep.Versioning;


/**
 * 
 * @author savinen Copyright Distocraft 2005
 * 
 * $id$
 */
public class CreateLoaderSet {
  
  private Logger log = Logger.getLogger("Wizard.CreateLoaderSet");

  protected String loaderTemplateName;

  protected String setTemplateName;

  protected Versioning version;

  protected RockFactory dwhrepRock;

  protected RockFactory rock;

  protected int techPackID;

  protected String templateName = "LoaderSQLXML.vm";
  protected String postLoadSQLTemplate = "postLoadSQL.vm";
  protected String updateDimSessionTemplate = "updateDimSession.vm";
  protected String temporaryOptionTemplate = "temporaryOption.vm";
  private String pathSeparator = "/";
  private String ignoredKeys= "";
  
  protected long maxSetID = 0;

  protected long maxActionID = 0;

  protected long maxSchedulingID = 0;

  protected boolean doSchedulings = false;

  protected String techPackName = "";

  protected String templateDir = "";
  
  

  /**
   * constructor
   * 
   * 
   */
  public CreateLoaderSet(String templateDir, Versioning version, RockFactory dwhrepRock, RockFactory rock, int techPackID,
      String techPackName, boolean schedulings) throws Exception {

    this.templateDir = templateDir;
    this.version = version;
    this.dwhrepRock = dwhrepRock;
    this.rock = rock;
    this.techPackID = techPackID;

    this.maxSetID = getSetMaxID(rock) + 1;
    this.maxActionID = getActionMaxID(rock) + 1;
    this.maxSchedulingID = getScheduleMaxID(rock) + 1;
    this.doSchedulings = schedulings;
    this.techPackName = techPackName;
  }

  /**
   * 
   * Merges template and context
   * 
   * @param templateName
   * @param context
   * @return string contains the output of the merge
   * @throws Exception
   */
  public String merge(String templateName, VelocityContext context) throws Exception {

    StringWriter strWriter = new StringWriter();
    boolean isMergeOk = Velocity.mergeTemplate(templateDir +"/"+ templateName, Velocity.ENCODING_DEFAULT, context,
        strWriter);
    log.finest("   Velocity Merge OK: " + isMergeOk);

    return strWriter.toString();

  }

  protected String CreateSql(String mTableID) throws Exception {

    List measurementColumnList = new ArrayList();

    //Query q = session.createQuery("from MeasurementType where VERSIONID=?");
    //q.setString(0, version.getVersionid());
    
    Measurementtype mt = new Measurementtype(dwhrepRock);
    mt.setVersionid(version.getVersionid());
    MeasurementtypeFactory mtf = new MeasurementtypeFactory(dwhrepRock,mt);

    Iterator iVer = mtf.get().iterator();

    while (iVer.hasNext()) {

      Measurementtype types = (Measurementtype) iVer.next();
      
      Measurementtable mta = new Measurementtable(dwhrepRock);
      mta.setTypeid(types.getTypeid());
      MeasurementtableFactory mtaf = new MeasurementtableFactory(dwhrepRock,mta);
      mtaf.get().iterator();
           
      Iterator iType = mtaf.get().iterator();

      while (iType.hasNext()) {

        Measurementtable tables = (Measurementtable) iType.next();
        //Iterator iTable = tables.getMeasurementColumns().iterator();
        
        Measurementcolumn mc = new Measurementcolumn(dwhrepRock);
        mc.setMtableid(tables.getMtableid());
        MeasurementcolumnFactory mcf = new MeasurementcolumnFactory(dwhrepRock,mc);
        Iterator iTable = mcf.get().iterator();

        while (iTable.hasNext()) {

          Measurementcolumn columns = (Measurementcolumn) iTable.next();
          if (tables.getMtableid().equals(mTableID)) {

            List sortList = new ArrayList();
            sortList.add(0, columns.getDataname());
            sortList.add(1, columns.getColnumber());
            measurementColumnList.add(sortList);

          }
        }
      }
    }

    class comp implements Comparator {

      public comp() {
      }

      public int compare(Object d1, Object d2) {

        List l1 = (List) d1;
        List l2 = (List) d2;

        Long i1 = (Long) l1.get(1);
        Long i2 = (Long) l2.get(1);
        return i1.compareTo(i2);
      }
    }

    Collections.sort(measurementColumnList, new comp());

    Vector vec = new Vector();
    Iterator iColl = measurementColumnList.iterator();
    while (iColl.hasNext()) {
      vec.add(((List) iColl.next()).get(0));
    }

    VelocityContext context = new VelocityContext();
    context.put("measurementColumn", vec);

    return merge(this.templateName, context);
  }

  private String getSQLJoinQuery(String typename, String mTableID) throws Exception {
    
    VelocityContext context = new VelocityContext();
    String result = merge("JOIN.vm", context);
    log.finer("   SQL: " + result);

    return result;

  }
  
  
  public void create() throws Exception {
    
    Measurementtype mt = new Measurementtype(dwhrepRock);
    mt.setVersionid(version.getVersionid());
    MeasurementtypeFactory mtf = new MeasurementtypeFactory(dwhrepRock,mt);
 
    Iterator iType = mtf.get().iterator();

    long iSet = 0;
    long iAction = 0;
    long iScheduling = 0;
    String element = "";

    
    while (iType.hasNext()) {
      
      boolean join = false;
      
      Measurementtype types = (Measurementtype) iType.next();
      
      if (types.getJoinable()!=null && types.getJoinable().length()>0){
        join = true;
        ignoredKeys = types.getJoinable();
      }
      
      Measurementkey mk = new Measurementkey(dwhrepRock);
      mk.setTypeid(types.getTypeid());
      MeasurementkeyFactory mkf = new MeasurementkeyFactory(dwhrepRock,mk);
      Iterator keys = mkf.get().iterator();
      
      while (keys.hasNext()) {
        Measurementkey key = (Measurementkey) keys.next();
        if (key.getIselement().intValue() == 1)
          element = key.getDataname();
      }

      //Iterator iTable = types.getMeasurementTables().iterator();
      Measurementtable mta = new Measurementtable(dwhrepRock);
      mta.setTypeid(types.getTypeid());
      MeasurementtableFactory mtaf = new MeasurementtableFactory(dwhrepRock,mta);
      Iterator iTable = mtaf.get().iterator();

      while (iTable.hasNext()) {

        Measurementtable mtable = (Measurementtable) iTable.next();

        String dir = "${ETLDATA_DIR}/"+ types.getTypename().toLowerCase() + this.pathSeparator + "raw" + this.pathSeparator;
     
        if (mtable.getTablelevel().equalsIgnoreCase("raw") || mtable.getTablelevel().equalsIgnoreCase("plain")) {

          int order = 0; 
          
          // create set
          log.info("Creating set Loader_" + types.getTypename());
          createSet("Loader", new Long(this.maxSetID + iSet).intValue(), types, mtable).insertToDB(rock);

          // temporary option
          log.info("  Creating action SQL_Execute_" + types.getTypename());
          createAction("SQL_Execute_" + types.getTypename(), order, "SQL Execute",
              new Long(this.maxSetID + iSet).intValue(), new Long(this.maxActionID + iAction).intValue(), types,
              mtable, "", getTemporaryOptions()).insertToDB(rock);
          iAction++;
          order++;

          if(join) {
            
            // create unpartitioned loader
            log.info("  Creating action UnPartitioned_Loader_" + types.getTypename());
            createAction("UnPartitioned_Loader_" + types.getTypename(), order, "UnPartitioned Loader", new Long(this.maxSetID + iSet).intValue(),
                new Long(this.maxActionID + iAction).intValue(), types, mtable, createPropertyStringUnpartitioned(types, dir),
                CreateSql(mtable.getMtableid())).insertToDB(rock);
            iAction++;
            order++;

          } else {

          // create loader
          log.info("  Creating action Loader_" + types.getTypename());
          createAction("Loader_" + types.getTypename(), order, "Loader", new Long(this.maxSetID + iSet).intValue(),
              new Long(this.maxActionID + iAction).intValue(), types, mtable, createPropertyString(types, "raw"),
              CreateSql(mtable.getMtableid())).insertToDB(rock);
          iAction++;
          order++;
          }
          
          
          
          // create joiner action        
          if(join) {
            log.info("  Creating action SQLJoiner_" + types.getTypename());
            String sqlJoinQuery = getSQLJoinQuery(types.getTypename(),mtable.getMtableid());
            createAction("SQLJoiner_" + types.getTypename(),order,"SQLJoiner",new Long(this.maxSetID + iSet).intValue(),
                new Long(this.maxActionID + iAction).intValue(),types, mtable,createPropertyStringJoiner(types),sqlJoinQuery).insertToDB(rock);
            iAction++;
            order++;
          }

          
          // Create duplicate check action. Only for version 5.2 and newer!
          if(templateDir.equalsIgnoreCase("5.0") || templateDir.equalsIgnoreCase("5.1") ) {
            log.info("Action DuplicateCheck not created because of selected 5.0 or 5.1 versions.");
          } else {
            log.info("  Creating action DuplicateCheck_" + types.getTypename());
            String duplicateCheckActionContext = getDuplicateCheckQuery();
            createAction("DuplicateCheck_" + types.getTypename(),order,"DuplicateCheck",new Long(this.maxSetID + iSet).intValue(),
                new Long(this.maxActionID + iAction).intValue(),types, mtable,"",duplicateCheckActionContext).insertToDB(rock);
            iAction++;
            order++;
          }
                    
          // UpdateDimSession
          Properties uds = getUpdateDimSession(element);
          log.info("  Creating action UpdateDimSession_" + types.getTypename());
          createAction("UpdateDimSession_" + types.getTypename(), order, "UpdateDimSession",
              new Long(this.maxSetID + iSet).intValue(), new Long(this.maxActionID + iAction).intValue(), types,
              mtable,propertyToString(uds), "").insertToDB(rock);
          iAction++;

          if (doSchedulings) {

            // create scheduling
            String holdFlag = "N";
            String name = "Loader_" + types.getTypename();
            
            log.info("  Creating Scheduling " + name);

            createWaitSchedule(new Long(this.maxSchedulingID + iScheduling).intValue(), this.techPackID,
                new Long(this.maxSetID + iSet).intValue(), name, holdFlag).insertToDB(this.rock);
            iScheduling++;
          }

          iSet++;

        }

      }
    }

  }

  protected String getPostLoadSQL(String baseTableName) {

    try {

      VelocityContext context = new VelocityContext();
      context.put("basetablename", baseTableName);
      
      context.toString();
      
      return merge(postLoadSQLTemplate, context);

    } catch (Exception e) {
      log.log(Level.WARNING,"Template "+postLoadSQLTemplate+" not found.",e);
    }

    return "";
  }

  
  protected String getTemporaryOptions() {

    try {

      VelocityContext context = new VelocityContext();
      return merge(temporaryOptionTemplate, context);

    } catch (Exception e) {
      log.log(Level.WARNING,"Template "+temporaryOptionTemplate+" not found.",e);
    }

    return "";
  }
  
  protected Properties getUpdateDimSession(String element) {

    try {

      VelocityContext context = new VelocityContext();
      context.put("element", element);     
      return createProperty(merge(updateDimSessionTemplate, context));
    
    } catch (Exception e) {
      log.log(Level.WARNING,"Template "+updateDimSessionTemplate+" not found.",e);
    }

    return null;
  } 
  
  
  
  protected SetGenerator createSet(String type, long iSet, Measurementtype types, Measurementtable mTable)
      throws Exception {

    SetGenerator set = new SetGenerator();
    set.setCOLLECTION_NAME(type + "_" + types.getTypename());
    set.setCOLLECTION_ID(Long.toString(iSet));
    set.setMAX_ERRORS("0");
    set.setMAX_FK_ERRORS("0");
    set.setMAX_COL_LIMIT_ERRORS("0");
    set.setCHECK_FK_ERROR_FLAG("N");
    set.setCHECK_COL_LIMITS_FLAG("N");
    set.setVERSION_NUMBER(version.getTechpack_version());
    set.setCOLLECTION_SET_ID(Integer.toString(techPackID));
    set.setPRIORITY("0");
    set.setQUEUE_TIME_LIMIT("30");
    set.setENABLED_FLAG("Y");
    set.setSETTYPE(type);
    set.setFOLDABLE_FLAG("Y");
    set.setHOLD_FLAG("N");

    return set;

  }

  protected ActionGenerator createAction(String name, long order, String type, long iSet, long iAct,
      Measurementtype types, Measurementtable mTable, String whereClause, String actionContents) throws Exception {

    ActionGenerator loadAction = new ActionGenerator();
    loadAction.setVERSION_NUMBER(version.getTechpack_version());
    loadAction.setTRANSFER_ACTION_ID(Long.toString(iAct));
    loadAction.setCOLLECTION_ID(Long.toString(iSet));
    loadAction.setCOLLECTION_SET_ID(Integer.toString(techPackID));
    loadAction.setACTION_TYPE(type);
    loadAction.setTRANSFER_ACTION_NAME(name);
    loadAction.setORDER_BY_NO(Long.toString(order));
    loadAction.setWHERE_CLAUSE(whereClause);
    loadAction.setACTION_CONTENTS(actionContents);
    loadAction.setENABLED_FLAG("Y");
    loadAction.setCONNECTION_ID("2");

    return loadAction;

  }

  protected ActionGenerator createAction(String name, long order, String type, long iSet, long iAct,
      Measurementtype types, Measurementtable mTable, String whereClause, String actionContents,String conID) throws Exception {

    ActionGenerator loadAction = new ActionGenerator();
    loadAction.setVERSION_NUMBER(version.getTechpack_version());
    loadAction.setTRANSFER_ACTION_ID(Long.toString(iAct));
    loadAction.setCOLLECTION_ID(Long.toString(iSet));
    loadAction.setCOLLECTION_SET_ID(Integer.toString(techPackID));
    loadAction.setACTION_TYPE(type);
    loadAction.setTRANSFER_ACTION_NAME(name);
    loadAction.setORDER_BY_NO(Long.toString(order));
    loadAction.setWHERE_CLAUSE(whereClause);
    loadAction.setACTION_CONTENTS(actionContents);
    loadAction.setENABLED_FLAG("Y");
    loadAction.setCONNECTION_ID(conID);

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

  protected String createPropertyStringJoiner(Measurementtype types) throws Exception {
    Properties prop = new Properties();

    prop.setProperty("objName", types.getTypename());
    prop.setProperty("typeName", types.getTypename()+"_RAW");
    prop.setProperty("versionid", version.getVersionid());
    prop.setProperty("ignoredKeys", ignoredKeys);
    prop.setProperty("prevTableName", types.getTypename()+"_PREV");

    return propertyToString(prop);
  }
  
  protected String createPropertyStringUnpartitioned(Measurementtype types, String dir) throws Exception {
    Properties prop = new Properties();

    prop.setProperty("tablename", types.getTypename()+"_PREV");
    prop.setProperty("versionid", version.getVersionid());
    prop.setProperty("dir", dir);
    prop.setProperty("techpack", techPackName);
    prop.setProperty("pattern", ".+");

    return propertyToString(prop);
  }
  
  protected String createPropertyString(Measurementtype types, String tailDir) throws Exception {
    Properties prop = new Properties();

    prop.setProperty("tablename", types.getTypename());
    prop.setProperty("techpack", this.techPackName);
    prop.setProperty("taildir", tailDir);
    prop.setProperty("dateformat", "yyyy-MM-dd");
    

    return propertyToString(prop);
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

    if (prop != null){
      
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      prop.store(baos, "");

      return baos.toString();

    }
    
    return "";
  }

  protected Properties createProperty(String str) throws Exception {

    Properties prop = new Properties();

    StringTokenizer st = new StringTokenizer(str, "\n");
    while (st.hasMoreTokens()) {
      StringTokenizer sti = new StringTokenizer(st.nextToken(), "=");
      String key = sti.nextToken();
      String value = sti.nextToken();
      prop.setProperty(key.trim(), value.trim());

    }

    return prop;

  }
  
  /**
   * This function returns the SQL query template (in string format) used in duplicate checking.
   * @return String containing velocity context template.
   */
  protected String getDuplicateCheckQuery() {
    /* Duplicate Check SQL now comes from static.properties
    try {
      VelocityContext context = new VelocityContext();
      return merge("duplicateCheck.vm", context);
    } catch (Exception e) {
      this.log.log(Level.SEVERE,"Error occurred trying to use duplicateCheck.vm.",e);
      return "";
    }
    */
    return "";
  }
  

}
