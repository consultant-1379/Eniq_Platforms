package com.distocraft.dc5000.etl.gui.setwizard;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
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
import com.distocraft.dc5000.repository.dwhrep.Measurementcounter;
import com.distocraft.dc5000.repository.dwhrep.MeasurementcounterFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementkey;
import com.distocraft.dc5000.repository.dwhrep.MeasurementkeyFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementtable;
import com.distocraft.dc5000.repository.dwhrep.MeasurementtableFactory;
import com.distocraft.dc5000.repository.dwhrep.Versioning;

/**
 * 
 * @author savinen Copyright Distocraft 2005
 * 
 * $id$
 */
public class CreateAggregatorSet {
  
  private Logger log = Logger.getLogger("Wizard.CreateAggregatorSet");

  protected String loaderTemplateName;

  protected String setTemplateName;

  protected Versioning version;

  protected RockFactory dwhrepRock;

  protected RockFactory rock;

  protected int techPackID;

  protected long maxSetID = 0;

  protected long maxActionID = 0;

  protected long maxSchedulingID = 0;

  protected boolean doSchedulings = false;

  protected String templateDir = "";

  /**
   * constructor
   */
  public CreateAggregatorSet(String templateDir, Versioning version, RockFactory dwhrepRock, RockFactory rock,
      int techPackID, boolean schedulings) throws Exception {

    this.templateDir = templateDir;
    this.version = version;
    this.dwhrepRock = dwhrepRock;
    this.rock = rock;
    this.techPackID = techPackID;

    this.maxSetID = getSetMaxID(rock) + 1;
    this.maxActionID = getActionMaxID(rock) + 1;
    this.maxSchedulingID = getScheduleMaxID(rock) + 1;
    this.doSchedulings = schedulings;
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
    boolean isMergeOk = Velocity.mergeTemplate(templateDir + "/" + templateName, Velocity.ENCODING_DEFAULT, context,
        strWriter);
    log.fine("   Velocity Merge OK: " + isMergeOk);

    return strWriter.toString();

  }

  /**
   * 
   * Creates SQL command(s)
   * 
   * 
   * @see com.distocraft.dc5000.etl.generator.Generate#CreateSql(java.util.List)
   */
  protected String CreateAggregationSql(String aggStr) throws Exception {

    VelocityContext context = new VelocityContext();

    Aggregation agga = new Aggregation(dwhrepRock);
    agga.setAggregation(aggStr);
    agga.setVersionid(version.getVersionid());
    AggregationFactory aggf = new AggregationFactory(dwhrepRock, agga);
    Aggregation agg = aggf.getElementAt(0);

    log.finest("   Aggregation clause: " + aggStr);

    Map sourcemeaskeyMap = new HashMap();
    Map targetmeaskeyMap = new HashMap();
    Map sourcemeascountMap = new HashMap();
    Map targetmeascountMap = new HashMap();
    Map targetTableMap = new HashMap();
    Map sourceTableMap = new HashMap();
    Map bhtypeMap = new HashMap();

    // Iterator iter = agg.getAggregationRules().iterator();
    Aggregationrule aggrule = new Aggregationrule(dwhrepRock);
    aggrule.setAggregation(aggStr);
    aggrule.setVersionid(version.getVersionid());
    AggregationruleFactory aggrulef = new AggregationruleFactory(dwhrepRock, aggrule);
    Iterator iter = aggrulef.get().iterator();

    while (iter.hasNext()) {

      Aggregationrule aggRule = (Aggregationrule) iter.next();

      // source table

      sourceTableMap.put(aggRule.getRuletype(), aggRule.getSource_table());

      Measurementtable mts = new Measurementtable(dwhrepRock);
      mts.setMtableid(aggRule.getSource_mtableid());
      MeasurementtableFactory mtsf = new MeasurementtableFactory(dwhrepRock, mts);
      List qLists = mtsf.get();

      if (qLists != null && qLists.size() > 0) {
        Iterator it = qLists.iterator();
        while (it.hasNext()) {
          Measurementtable mTbl = (Measurementtable) it.next();

          Measurementkey mkey = new Measurementkey(dwhrepRock);
          mkey.setTypeid(mTbl.getTypeid());
          MeasurementkeyFactory mtypef = new MeasurementkeyFactory(dwhrepRock, mkey);
          sourcemeaskeyMap.put(aggRule.getRuletype(), mtypef.get());
        }
      }

      if (qLists != null && qLists.size() > 0) {
        Iterator it = qLists.iterator();
        while (it.hasNext()) {
          Measurementtable mTbl = (Measurementtable) it.next();

          Measurementcounter mcount = new Measurementcounter(dwhrepRock);
          mcount.setTypeid(mTbl.getTypeid());
          MeasurementcounterFactory mcountf = new MeasurementcounterFactory(dwhrepRock, mcount);
          sourcemeascountMap.put(aggRule.getRuletype(), mcountf.get());
        }

      }

      // target table

      targetTableMap.put(aggRule.getRuletype(), aggRule.getTarget_table());

      Measurementtable mtt = new Measurementtable(dwhrepRock);
      mtt.setMtableid(aggRule.getTarget_mtableid());
      MeasurementtableFactory mttf = new MeasurementtableFactory(dwhrepRock, mtt);
      List qListt = mttf.get();

      if (qListt != null && qListt.size() > 0) {
        Iterator it = qListt.iterator();
        while (it.hasNext()) {
          Measurementtable mTbl = (Measurementtable) it.next();

          Measurementkey mkey = new Measurementkey(dwhrepRock);
          mkey.setTypeid(mTbl.getTypeid());
          MeasurementkeyFactory mtypef = new MeasurementkeyFactory(dwhrepRock, mkey);
          targetmeaskeyMap.put(aggRule.getRuletype(), mtypef.get());
        }

      }

      if (qListt != null && qListt.size() > 0) {
        Iterator it = qListt.iterator();
        while (it.hasNext()) {
          Measurementtable mTbl = (Measurementtable) it.next();

          Measurementcounter mcount = new Measurementcounter(dwhrepRock);
          mcount.setTypeid(mTbl.getTypeid());
          MeasurementcounterFactory mcountf = new MeasurementcounterFactory(dwhrepRock, mcount);
          targetmeascountMap.put(aggRule.getRuletype(), mcountf.get());
        }

      }

      String bhtype = "";
      if (aggRule.getBhtype() == null) {
        bhtype = "";
      } else {
        bhtype = aggRule.getBhtype();
      }

      bhtypeMap.put(aggRule.getRuletype(), bhtype);

    }

    context.put("Bhtype", bhtypeMap);
    context.put("TargetTable", targetTableMap);
    context.put("SourceTable", sourceTableMap);
    context.put("TargetMeasurementKeyMap", targetmeaskeyMap);
    context.put("SourceMeasurementKeyMap", sourcemeaskeyMap);
    context.put("TargetMeasurementCounterMap", targetmeascountMap);
    context.put("SourceMeasurementCounterMap", sourcemeascountMap);

    String result = merge(getTemplate(agg.getAggregationtype(), agg.getAggregationscope()), context);
    log.finer("   SQL: " + result);

    return result;

  }

  /**
   * 
   * 
   * 
   * @param agg
   * @return
   */
  private String getGateKeeperSQL(String aggStr) throws Exception {

    String sqlClause = "";

    Aggregationrule aggr = new Aggregationrule(dwhrepRock);
    aggr.setAggregation(aggStr);
    aggr.setVersionid(version.getVersionid());
    AggregationruleFactory aggrf = new AggregationruleFactory(dwhrepRock, aggr);
    Iterator iter = aggrf.get().iterator();

    boolean first = true;

    while (iter.hasNext()) {

      Aggregationrule aggRule = (Aggregationrule) iter.next();

      // checks that aggregation itself is not aggregated
      if (first) {

        String type = aggRule.getTarget_type();
        String level = aggRule.getTarget_level();
        String scope = aggRule.getAggregationscope();

        // check if this aggregation is allready done
        sqlClause += "SELECT count(*) result FROM LOG_AGGREGATIONSTATUS WHERE TYPENAME = '" + type
            + "' AND TIMELEVEL = '" + level + "' AND DATADATE = $date AND AGGREGATIONSCOPE = '" + scope
            + "' AND STATUS NOT IN ('AGGREGATED')";

        first = false;

      }

      // filters out week and month aggregations because we cannot check every aggregation in week/month.
      if (!aggRule.getSource_level().equalsIgnoreCase("RAW") && !aggRule.getAggregationscope().equalsIgnoreCase("WEEK") && !aggRule.getAggregationscope().equalsIgnoreCase("MONTH")) {

        // check dependensies on other aggregations (one step backwards)
        String type = aggRule.getSource_type();
        String level = aggRule.getSource_level();
        String scope = aggRule.getAggregationscope();
        
        // create clause that checks that aggregation_rules and aggregation_status have the same amount of aggregations
        // zero count in LOG_AggregationRules part of the sql closes the gate also.
        String tmpSqlClause = "select count(*) from (SELECT count(distinct aggregation) c  FROM LOG_AGGREGATIONSTATUS   WHERE typename = '" + type + "' AND timelevel = '" + level + "' AND DATADATE = $date and AGGREGATIONSCOPE = '" + scope + "' AND STATUS IN ('AGGREGATED') ) as a ,(select  count(distinct aggregation) c  from LOG_AggregationRules where target_type = '" + type + "' AND target_level = '" + level + "'  and aggregationscope = '" + scope + "') as b where a.c>=b.c and b.c <> 0 ";
               
        // checks for dublicate clauses
        if (sqlClause.indexOf(tmpSqlClause) == -1)
          sqlClause += "\nUNION ALL\n" + tmpSqlClause;

      } else if (aggRule.getSource_level().equalsIgnoreCase("RAW")) {

        // check is there raw data in active partition for aggregation day

        String tmpSqlClause = "SELECT count(*) result FROM " + aggRule.getSource_table()
            + " WHERE DATE_ID = $date ";

        if (sqlClause.indexOf(tmpSqlClause) == -1)
          sqlClause += "\nUNION ALL\n" + tmpSqlClause;

      }

    }

    return sqlClause;
  }

  /**
   * 
   * parses template name
   * 
   * 
   * @param AGGREGATIONTYPE
   * @param AGGREGATIONSCOPE
   * @return template name
   */
  private String getTemplate(String aggregationtype, String aggregationscope) {
    return aggregationtype + "." + aggregationscope + ".vm";
  }

  public void create() throws Exception {

    Aggregation aagg = new Aggregation(dwhrepRock);
    aagg.setVersionid(version.getVersionid());
    AggregationFactory aggf = new AggregationFactory(dwhrepRock, aagg);
    Iterator iAgg = aggf.get().iterator();

    long iSet = 0;
    long iAction = 0;
    long order = 0;
    long iScheduling = 0;
    String aggstr = "";

    try {
      VelocityContext context = new VelocityContext();
      StringWriter strWriter = new StringWriter();
      Velocity.mergeTemplate(templateDir + "/aggregator.vm", Velocity.ENCODING_DEFAULT, context, strWriter);
      aggstr = strWriter.toString();

    } catch (Exception e) {
      log.log(Level.WARNING,"Velocity error",e);
    }

    while (iAgg.hasNext()) {

      order = 0;
      Aggregation agg = (Aggregation) iAgg.next();

      String tablename = "";
      Properties aggregationProp = new Properties();

      // Iterator iter = agg.getAggregationRules().iterator();
      Aggregationrule aggr = new Aggregationrule(dwhrepRock);
      aggr.setAggregation(agg.getAggregation());
      AggregationruleFactory aggrf = new AggregationruleFactory(dwhrepRock, aggr);
      Iterator iter = aggrf.get().iterator();

      if (iter != null && iter.hasNext()) {

        Aggregationrule aggRule = (Aggregationrule) iter.next();
        if (aggRule != null)
          tablename = aggRule.getTarget_table();
      }

      aggregationProp.setProperty("tablename", tablename);
      String name = aggstr + agg.getAggregation();

      log.info("Creating set " + name);
      createSet(name, "Aggregator", new Long(this.maxSetID + iSet).intValue(), agg).insertToDB(rock);

      Properties gk_where = new Properties();
      gk_where.setProperty("aggregation", agg.getAggregation());
      log.info("  Creating action: " + name);
      createAction("GateKeeper", order, "GateKeeper", new Long(this.maxSetID + iSet).intValue(),
          new Long(this.maxActionID + iAction).intValue(), agg, getGateKeeperSQL(agg.getAggregation()),
          propertyToString(gk_where)).insertToDB(rock);
      iAction++;
      order++;

      log.info("  Creating action: " + name);
      createAction(name, order, "Aggregation", new Long(this.maxSetID + iSet).intValue(),
          new Long(this.maxActionID + iAction).intValue(), agg, CreateAggregationSql(agg.getAggregation()),
          propertyToString(aggregationProp)).insertToDB(rock);
      iAction++;

      if (doSchedulings) {

        // create scheduling
        String holdFlag = "";
        holdFlag = "N";
        iScheduling++;

        log.info("  Creating schedule " + name);

        createWaitSchedule(new Long(this.maxSchedulingID + iScheduling).intValue(), this.techPackID,
            new Long(this.maxSetID + iSet).intValue(), name, holdFlag).insertToDB(this.rock);
      }

      iSet++;

    }

  }

  private SetGenerator createSet(String name, String type, long iSet, Aggregation agg) throws Exception {

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
    set.setPRIORITY("0");
    set.setQUEUE_TIME_LIMIT("30");
    set.setENABLED_FLAG("Y");
    set.setSETTYPE(type);
    set.setFOLDABLE_FLAG("Y");
    set.setHOLD_FLAG("N");

    return set;

  }

  private ActionGenerator createAction(String name, long order, String type, long iSet, int iAct, Aggregation agg,
      String actionContents, String where) throws Exception {

    ActionGenerator loadAction = new ActionGenerator();
    loadAction.setVERSION_NUMBER(version.getTechpack_version());
    loadAction.setTRANSFER_ACTION_ID(Long.toString(iAct));
    loadAction.setCOLLECTION_ID(Long.toString(iSet));
    loadAction.setCOLLECTION_SET_ID(Integer.toString(techPackID));
    loadAction.setACTION_TYPE(type);
    // loadAction.setTRANSFER_ACTION_NAME(type+"_"+agg.getComp_id().getAggregation());
    loadAction.setTRANSFER_ACTION_NAME(name);
    loadAction.setORDER_BY_NO(Long.toString(order));
    loadAction.setWHERE_CLAUSE(where);
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

    Properties prop = new Properties();

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
