package com.distocraft.dc5000.etl.gui.setwizard;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
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
import com.distocraft.dc5000.repository.dwhrep.Referencecolumn;
import com.distocraft.dc5000.repository.dwhrep.ReferencecolumnFactory;
import com.distocraft.dc5000.repository.dwhrep.Referencetable;
import com.distocraft.dc5000.repository.dwhrep.ReferencetableFactory;
import com.distocraft.dc5000.repository.dwhrep.Versioning;


/**
 * 
 * @author savinen Copyright Distocraft 2005
 * 
 * $id$
 */
public class CreateTopologyLoaderSet {

  private Logger log = Logger.getLogger("Wizard.CreateTopologyLoaderSet");
  
  protected String loaderTemplateName;

  protected String setTemplateName;

  protected Versioning version;
  
  protected RockFactory dwhrepRock;

  protected RockFactory rock;

  protected int techPackID;

  protected String temporaryOptionTemplate = "temporaryOption.vm";

  private String pathSeparator = "/";

  protected long maxSetID = 0;

  protected long maxActionID = 0;
  
  protected String templateDir = "";
  
  protected boolean doSchedulings = false;

  protected long maxSchedulingID = 0;
  
  protected String techPackName = "";
  
  /**
   * constructor
   * 
   * 
   */
  public CreateTopologyLoaderSet(String templateDir, Versioning version, RockFactory dwhrepRock, RockFactory rock, int techPackID, String techPackName, boolean schedulings) throws Exception {

    this.templateDir = templateDir;
    this.version = version;
    this.dwhrepRock = dwhrepRock;
    this.rock = rock;
    this.techPackID = techPackID;

    this.maxSetID = getSetMaxID(rock) + 1;
    this.maxActionID = getActionMaxID(rock) + 1;
    
    this.doSchedulings = schedulings;
    this.techPackName = techPackName;
    this.maxSchedulingID = getScheduleMaxID(rock) + 1;

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
    boolean isMergeOk = Velocity.mergeTemplate(templateDir +"/"+ templateName, Velocity.ENCODING_DEFAULT, context, strWriter);
    log.fine("   Velocity Merge OK: " + isMergeOk);

    return strWriter.toString();

  }

  protected String CreateSql(String template, Referencetable rtable) throws Exception {

    Vector vec = new Vector();
    VelocityContext context = new VelocityContext();

    //Iterator iter = rtable.getReferenceColumns().iterator();
    
    Referencecolumn rfc = new Referencecolumn(dwhrepRock);
    rfc.setTypeid(rtable.getTypeid());
    ReferencecolumnFactory rfcf = new ReferencecolumnFactory(dwhrepRock,rfc);
    Iterator iter = rfcf.get().iterator();
    
    while (iter.hasNext()) {

      Referencecolumn rCol = (Referencecolumn) iter.next();
      vec.add(rCol);
    }

    Collections.sort(vec, new comp());

    context.put("referencecolumn", vec);

    return merge(template, context);
  }

  protected String CreateSqlWTable(String template, Referencetable rtable) throws Exception {

    Vector vec = new Vector();
    VelocityContext context = new VelocityContext();

    //Iterator iter = rtable.getReferenceColumns().iterator();
    Referencecolumn rfc = new Referencecolumn(dwhrepRock);
    rfc.setTypeid(rtable.getTypeid());
    ReferencecolumnFactory rfcf = new ReferencecolumnFactory(dwhrepRock,rfc);
    Iterator iter = rfcf.get().iterator();
 
    
    while (iter.hasNext()) {

      Referencecolumn rCol = (Referencecolumn) iter.next();
      vec.add(rCol);
    }

    Collections.sort(vec, new comp());

    context.put("referencecolumn", vec);
    context.put("TABLE", rtable.getTypename());

    return merge(template, context);
  }

  class comp implements Comparator {

    public comp() {
    }

    public int compare(Object d1, Object d2) {

      Referencecolumn l1 = (Referencecolumn) d1;
      Referencecolumn l2 = (Referencecolumn) d2;

      Long i1 = (Long) l1.getColnumber();
      Long i2 = (Long) l2.getColnumber();
      return i1.compareTo(i2);
    }
  }

  public void create() throws Exception {

    long iAction = 0;
    long iSet = 0;
    long iOrder = 0;
    int uPolicy = 0;
    long iScheduling = 0;
    String dir = "";
    
    Referencetable rt = new Referencetable(dwhrepRock);
    rt.setVersionid(version.getVersionid());
    ReferencetableFactory rtf = new ReferencetableFactory(dwhrepRock,rt);
    
    
    Iterator iTable = rtf.get().iterator();

    while (iTable.hasNext()) {

      iOrder = 0;
      uPolicy = 0;

      Referencetable rtable = (Referencetable) iTable.next();

      dir = "${ETLDATA_DIR}/"+ rtable.getTypename().toLowerCase() + this.pathSeparator + "raw" + this.pathSeparator;

      
      if (rtable.getUpdate_policy() != null) {
        uPolicy = rtable.getUpdate_policy().intValue();
      }

      if (uPolicy == 1) {

        // load directly
        log.info("Creating Set and Actions for direct topology loaders");

        // create set
        log.info("Creating set TopologyLoader_" + rtable.getTypename());
        createSet("TopologyLoader_" + rtable.getTypename(),"Topology", new Long(this.maxSetID + iSet).intValue()).insertToDB(rock);

        // temporary option
        log.info("  Creating action SQL_Execute_" + rtable.getTypename());
        createAction("SQL_Execute_" + rtable.getTypename(),iOrder, "SQL Execute", new Long(this.maxSetID + iSet).intValue(),
            new Long(this.maxActionID + iAction).intValue(), "", getTemporaryOptions()).insertToDB(rock);
        iAction++;
        iOrder++;

        if (canFind("DELETE.TOPOLOGY.vm")){
          // delete topology
          log.info("  Creating action SQL_Execute_" + rtable.getTypename());
          createAction("SQL_Execute_" + rtable.getTypename(),iOrder, "SQL Execute", new Long(this.maxSetID + iSet).intValue(),
              new Long(this.maxActionID + iAction).intValue(), "", CreateSqlWTable("DELETE.TOPOLOGY.vm", rtable))
              .insertToDB(rock);
          iAction++;
          iOrder++;          
        }
        
        // create topology loader
        log.info("  Creating action UnPartitioned_Loader_" + rtable.getTypename());
        createAction("UnPartitioned_Loader_" + rtable.getTypename(),iOrder, "UnPartitioned Loader", new Long(this.maxSetID + iSet).intValue(),
            new Long(this.maxActionID + iAction).intValue(), createPropertyString(rtable.getTypename(), dir),
            CreateSql("LOADER.TOPOLOGY.vm", rtable)).insertToDB(rock);
        iAction++;
        iOrder++;

        if (doSchedulings) {

          // create scheduling
          String holdFlag = "N";
          String name = "Loader_" + rtable.getTypename();
          
          log.info("  Creating schedule " + name);

          createWaitSchedule(new Long(this.maxSchedulingID + iScheduling).intValue(), this.techPackID,
              new Long(this.maxSetID + iSet).intValue(), name, holdFlag).insertToDB(this.rock);
          iScheduling++;
        }     

        iSet++;

      } else if (uPolicy == 2) {

        // use _CURRENT view
        log.info("Creating Set and Actions for indirect (use _CURRENT view) topology loaders");

        // create set
        log.info("Creating set TopologyLoader_" + rtable.getTypename());
        createSet("TopologyLoader_" + rtable.getTypename(),"Topology", new Long(this.maxSetID + iSet).intValue()).insertToDB(rock);

        // temporary option
        log.info("  Creating action SQL_Execute_" + rtable.getTypename());
        createAction("SQL_Execute_" + rtable.getTypename(),iOrder, "SQL Execute", new Long(this.maxSetID + iSet).intValue(),
            new Long(this.maxActionID + iAction).intValue(), "", getTemporaryOptions()).insertToDB(rock);
        iAction++;
        iOrder++;
        
        // create topology loader
        log.info("  Creating action UnPartitioned_Loader_" + rtable.getTypename());
        createAction("UnPartitioned_Loader_" + rtable.getTypename(),iOrder, "UnPartitioned Loader", new Long(this.maxSetID + iSet).intValue(),
            new Long(this.maxActionID + iAction).intValue(), createPropertyString(rtable.getTypename()+"_CURRENT_DC", dir),
            CreateSql("LOADER.TOPOLOGY.vm", rtable)).insertToDB(rock);
        iAction++;
        iOrder++;
        
        // Trigger Scheduled Set
        log.info("  Creating action Trigger_Updater_" + rtable.getTypename());
        createAction("Trigger_Updater_" + rtable.getTypename(),iOrder, "TriggerScheduledSet", new Long(this.maxSetID + iSet).intValue(),
            new Long(this.maxActionID + iAction).intValue(), "TopologyUpdater_" + rtable.getTypename(),"TopologyUpdater_" + rtable.getTypename()).insertToDB(rock);
        iAction++;
        iOrder++;      
        
        if (doSchedulings) {

          // create scheduling
          String holdFlag = "N";
          String name = "Loader_" + rtable.getTypename();
          
          log.info("  Creating schedule " + name);

          createWaitSchedule(new Long(this.maxSchedulingID + iScheduling).intValue(), this.techPackID,
              new Long(this.maxSetID + iSet).intValue(), name, holdFlag).insertToDB(this.rock);
          iScheduling++;
        }     

        
        if (canFind("TopologyUpdater.vm")){
          
          iSet++;
          
          if (doSchedulings) {

            // create scheduling
            String holdFlag = "N";
            String name = "TopologyUpdater_"+rtable.getTypename();
            
            log.info("  Creating schedule " + name);

            createWaitSchedule(new Long(this.maxSchedulingID + iScheduling).intValue(), this.techPackID,
                new Long(this.maxSetID + iSet).intValue(), name, holdFlag).insertToDB(this.rock);
            iScheduling++;
          }     

          
          // create set
          // new update set
          log.info("  Creating set TopologyUpdater_" + rtable.getTypename());
          createSet("TopologyUpdater_" + rtable.getTypename(),"Topology", new Long(this.maxSetID + iSet).intValue()).insertToDB(rock);      
          iOrder=0;

        }
        
        // insert
        log.info("  Creating insert action SQL_Execute_" + rtable.getTypename());
        createAction("SQL_Execute_" + rtable.getTypename(),iOrder, "SQL Execute", new Long(this.maxSetID + iSet).intValue(),
            new Long(this.maxActionID + iAction).intValue(), "", CreateSqlWTable("INSERT.TOPOLOGY.vm", rtable))
            .insertToDB(rock);
        iAction++;
        iOrder++;
        
        // update
        Logger.getLogger("Wizard.CreateTopologyLoaderSet.create").log(Level.INFO,
            "Creating update Action for  " + rtable.getTypename());
        createAction("SQL_Execute_" + rtable.getTypename(),iOrder, "SQL Execute", new Long(this.maxSetID + iSet).intValue(),
            new Long(this.maxActionID + iAction).intValue(), "", CreateSqlWTable("UPDATE.TOPOLOGY.vm", rtable))
            .insertToDB(rock);
        iAction++;
        iOrder++;
      
        if (canFind("REPLACE.TOPOLOGY.vm")){
          // replace
          Logger.getLogger("Wizard.CreateTopologyLoaderSet.create").log(Level.INFO,
              "Creating replace Action for " + rtable.getTypename());
          createAction("SQL_Execute_" + rtable.getTypename(),iOrder, "SQL Execute", new Long(this.maxSetID + iSet).intValue(),
              new Long(this.maxActionID + iAction).intValue(), "", CreateSqlWTable("REPLACE.TOPOLOGY.vm", rtable))
              .insertToDB(rock);
          iAction++;
          iOrder++;               
        }
       
        // remove
        Logger.getLogger("Wizard.CreateTopologyLoaderSet.create").log(Level.INFO,
            "Creating remove Action for " + rtable.getTypename());
        createAction("SQL_Execute_" + rtable.getTypename(),iOrder, "SQL Execute", new Long(this.maxSetID + iSet).intValue(),
            new Long(this.maxActionID + iAction).intValue(), "", CreateSqlWTable("REMOVE.TOPOLOGY.vm", rtable))
            .insertToDB(rock);
        iAction++;
        iOrder++;
        
        // ReloadDBlookupsCache
        Logger.getLogger("Wizard.CreateTopologyLoaderSet.create").log(Level.INFO,
            "Creating ReloadDBLookups Action for " + rtable.getTypename());
        createAction("ReloadDBLookups_" + rtable.getTypename(),iOrder, "ReloadDBLookups", new Long(this.maxSetID + iSet).intValue(),
            new Long(this.maxActionID + iAction).intValue(), "", propertyToString(createProperty("tableName="+rtable.getTypename())))
            .insertToDB(rock);
        iAction++;
        iOrder++;
        
        iSet++;

      } else {

        // do nothing
        Logger.getLogger("Wizard.CreateTopologyLoaderSet.create").log(Level.INFO, "Skipping " + rtable.getTypename());

      }
    
  }
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
  
  protected String getTemporaryOptions() {

    try {

      VelocityContext context = new VelocityContext();
      return merge(temporaryOptionTemplate, context);

    } catch (Exception e) {
      log.log(Level.WARNING,"Template "+temporaryOptionTemplate+" not found.",e);
    }

    return "";
  }
  
  protected boolean canFind(String filename){
    
    File file = new File(filename);
    
    if (file.exists()) {

      return true;

    } else {

      try {

        ClassLoader cl = this.getClass().getClassLoader();
        InputStreamReader isr = new InputStreamReader(cl.getResourceAsStream(templateDir +"/"+filename));
        if (isr !=null) return true;

      } catch (Exception e) {

      }

      return false;

    }
  }
  
  protected SetGenerator createSet(String name,String type, long iSet) throws Exception {

    SetGenerator set = new SetGenerator();
    set.setCOLLECTION_NAME(name);
    set.setCOLLECTION_ID(Long.toString(iSet));
    set.setMAX_ERRORS("0");
    set.setMAX_FK_ERRORS("0");
    set.setMAX_COL_LIMIT_ERRORS("0");
    set.setCHECK_FK_ERROR_FLAG("N");
    set.setCHECK_COL_LIMITS_FLAG("N");
    set.setVERSION_NUMBER(version.getTechpack_version());
    set.setCOLLECTION_SET_ID(Integer.toString(techPackID));
    set.setPRIORITY("1");
    set.setQUEUE_TIME_LIMIT("30");
    set.setENABLED_FLAG("Y");
    set.setSETTYPE(type);
    set.setFOLDABLE_FLAG("Y");
    set.setHOLD_FLAG("N");

    return set;

  }

  protected ActionGenerator createAction(String name,long order, String type, long iSet, long iAct,
      String whereClause, String actionContents) throws Exception {

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

  protected String createPropertyString(String tablename,String dir) throws Exception {
    Properties prop = new Properties();

    prop.setProperty("tablename", tablename);
    prop.setProperty("dir", dir);
    prop.setProperty("techpack", techPackName);
    prop.setProperty("pattern", ".+");

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

}
