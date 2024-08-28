package com.distocraft.dc5000.etl.gui.setwizard;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.ResourceNotFoundException;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.gui.ConfigTool;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_collectionsFactory;
import com.distocraft.dc5000.etl.rock.Meta_schedulings;
import com.distocraft.dc5000.etl.rock.Meta_schedulingsFactory;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actionsFactory;
import com.distocraft.dc5000.repository.dwhrep.Dataformat;
import com.distocraft.dc5000.repository.dwhrep.DataformatFactory;
import com.distocraft.dc5000.repository.dwhrep.Datainterface;
import com.distocraft.dc5000.repository.dwhrep.Defaulttags;
import com.distocraft.dc5000.repository.dwhrep.DefaulttagsFactory;
import com.distocraft.dc5000.repository.dwhrep.Interfacemeasurement;
import com.distocraft.dc5000.repository.dwhrep.Interfacetechpacks;
import com.distocraft.dc5000.repository.dwhrep.Transformer;
import com.distocraft.dc5000.repository.dwhrep.TransformerFactory;

/**
 * 
 * @author savinen Copyright Distocraft 2005
 * 
 * $id$
 */
public class CreateInterfaceSet {

  protected String loaderTemplateName;

  protected String setTemplateName;

  protected RockFactory dwhrepRock;

  protected RockFactory rock;

  protected long techPackID;

  protected String defaultTemplateName = "defaultAdapter";

  protected long maxSetID = 0;

  protected long maxActionID = 0;

  protected String interfaceName = "";

  protected String tpVersion;

  protected String versionID;

  protected String adapterType = "";

  protected String connectionID = "";

  protected String adapterName = "";

  protected String dirName = "";

  protected String itype = "";

  protected String objType = "";

  protected String templateDir = "";

  /**
   * 
   * constructor
   * 
   * @param version
   * @param rock
   * @param techPackID
   * @param name
   * @param adapterType
   * @param adapterName
   * @param connectionID
   * @throws Exception
   */
  public CreateInterfaceSet(String objType, String templateDir, String tpVersion, String versionID,
      RockFactory dwhrepRock, RockFactory rock, long techPackID, String interfaceName, String adapterType,
      String adapterName, String dirName, String connectionID) throws Exception {

    if (objType.equalsIgnoreCase("measurement")) {
      itype = "pmdata";
    } else if (objType.equalsIgnoreCase("reference")) {
      itype = "referencedata";
    } else {
      itype = "pmdata";
    }

    this.objType = objType;
    this.templateDir = templateDir;
    this.rock = rock;
    this.dwhrepRock = dwhrepRock;
    this.techPackID = techPackID;

    this.maxSetID = getSetMaxID(rock) + 1;
    this.maxActionID = getActionMaxID(rock) + 1;

    this.interfaceName = interfaceName;
    this.tpVersion = tpVersion;
    this.versionID = versionID;
    this.adapterType = adapterType;
    this.connectionID = connectionID;
    this.adapterName = adapterName;
    this.dirName = dirName;

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
    boolean isMergeOk = false;
    try {
      isMergeOk = Velocity.mergeTemplate(templateDir + "/" + templateName, Velocity.ENCODING_DEFAULT, context,
          strWriter);
    } catch (ResourceNotFoundException e) {
      isMergeOk = Velocity.mergeTemplate(templateDir + "/" + "defaultAdapter.vm", Velocity.ENCODING_DEFAULT, context,
          strWriter);
    }

    Logger.getLogger("Wizard.Generate.Merge").log(Level.FINEST, " Velocity Merge OK: " + isMergeOk);

    return strWriter.toString();

  }

  protected Properties createProperty() throws Exception {

    VelocityContext context = new VelocityContext();
    context.put("parserType", this.adapterType);
    context.put("interfaceName", this.interfaceName);
    context.put("transformerName", this.interfaceName + ".xml");
    context.put("directoryName", this.dirName);
    context.put("interfaceType", this.itype);

    return createProperty(merge(getTemplateName(this.adapterName), context));
  }

  protected void createInterface() throws Exception {

    List result = new ArrayList();

    Datainterface di = new Datainterface(dwhrepRock);
    di.setInterfacename(interfaceName);
    di.setInterfacetype(this.objType);
    di.setStatus(new Long(1));
    di.setDescription("");
    di.setDataformattype(this.adapterType);

    di.saveDB();

    // Query q = session.createQuery("from DataFormat where VERSIONID=?");
    // q.setString(0,version.getVersionid());
    // Iterator vIter = q.list().iterator();

    Dataformat adf = new Dataformat(dwhrepRock);
    adf.setVersionid(versionID);
    DataformatFactory adff = new DataformatFactory(dwhrepRock, adf);

    Iterator vIter = adff.get().iterator();
    while (vIter.hasNext()) {

      Dataformat df = (Dataformat) vIter.next();

      if (df.getDataformattype().equalsIgnoreCase(this.adapterType)
          && df.getObjecttype().equalsIgnoreCase(this.objType)) {

        // Query qd = session.createQuery("from DefaultTag where
        // DATAFORMATID=?");
        // qd.setString(0,df.getDataformatid());

        Defaulttags adt = new Defaulttags(dwhrepRock);
        adt.setDataformatid(df.getDataformatid());
        DefaulttagsFactory dtf = new DefaulttagsFactory(dwhrepRock, adt);

        Iterator dIter = dtf.get().iterator();
        while (dIter.hasNext()) {
          Defaulttags dt = (Defaulttags) dIter.next();

          Interfacemeasurement im = new Interfacemeasurement(dwhrepRock);
          im.setDataformatid(dt.getDataformatid());

          Transformer t_cond = new Transformer(dwhrepRock);
          t_cond.setTransformerid(dt.getDataformatid());
          TransformerFactory tFact = new TransformerFactory(dwhrepRock, t_cond);

          Vector v = tFact.get();

          if (v != null && v.size() == 1)
            im.setTransformerid(dt.getDataformatid());

          im.setDescription(dt.getDescription());
          im.setModiftime(new Timestamp(new Date().getTime()));
          im.setStatus(new Long(1));
          im.setTagid(dt.getTagid());
          im.setInterfacename(interfaceName);

          im.saveDB();
        }
      }
    }

    ConfigTool.reloadConfig();

  }

  protected void createInterfaceTechpacks(String techpacks, String dataformattype) throws Exception {

    Datainterface di = new Datainterface(dwhrepRock);
    di.setInterfacename(interfaceName);
    di.setInterfacetype(this.adapterType);
    di.setStatus(new Long(1));
    di.setDescription("");
    di.setDataformattype(dataformattype);
    di.saveDB();

    StringTokenizer token = new StringTokenizer(techpacks);

    while (token.hasMoreElements()) {

      String tp = (String) token.nextToken();
      Interfacetechpacks it = new Interfacetechpacks(dwhrepRock);
      it.setInterfacename(interfaceName);
      it.setTechpackname(tp);
      it.saveDB();
    }

    ConfigTool.reloadConfig();
  }

  public void create() throws Exception {

    long iSet = 0;
    long iAction = 0;

    createInterface();

    Logger.getLogger("Wizard.CreateInterfaceSet.create").log(Level.INFO, "Creating Adapter Set: " + interfaceName);
    createSet("Adapter", new Long(this.maxSetID + iSet).intValue(),
        "Adapter_" + this.interfaceName + "_" + this.adapterName).insertToDB(rock);

    Logger.getLogger("Wizard.CreateInterfaceSet.create").log(Level.INFO, "Creating Parse Action: " + interfaceName);
    createAction(1, "Parse", new Long(this.maxSetID + iSet).intValue(),
        new Long(this.maxActionID + iAction).intValue(), "", propertyToString(createProperty()), this.adapterName)
        .insertToDB(rock);
    iAction++;
    Logger.getLogger("Wizard.CreateInterfaceSet.create").log(Level.INFO,
        "Creating trigger loader Action: " + interfaceName);
    createAction(2, "TriggerScheduledSet", new Long(this.maxSetID + iSet).intValue(),
        new Long(this.maxActionID + iAction).intValue(), "", "", "Trigger_loaders_" + this.adapterName)
        .insertToDB(rock);

  }

  public void createTechpacks(String techpacks, String dataformattype) throws Exception {

    long iSet = 0;
    long iAction = 0;

    createInterfaceTechpacks(techpacks, dataformattype);

    Logger.getLogger("Wizard.CreateInterfaceSet.create").log(Level.INFO, "Creating Adapter Set: " + interfaceName);
    createSet("Adapter", new Long(this.maxSetID + iSet).intValue(),
        "Adapter_" + this.interfaceName + "_" + this.adapterName).insertToDB(rock);

    Logger.getLogger("Wizard.CreateInterfaceSet.create").log(Level.INFO,
        "Creating scheduling for Adapter Set: " + interfaceName);
    // Create the default scheduling for the adapter.
    createSchedule(new Long(this.maxSetID + iSet).intValue(),
        "TriggerAdapter_" + this.interfaceName + "_" + this.adapterName).insertToDB(this.rock);

    Logger.getLogger("Wizard.CreateInterfaceSet.create").log(Level.INFO, "Creating Parse Action: " + interfaceName);
    createAction(1, "Parse", new Long(this.maxSetID + iSet).intValue(),
        new Long(this.maxActionID + iAction).intValue(), "", propertyToString(createProperty()), this.adapterName)
        .insertToDB(rock);
    iAction++;
    Logger.getLogger("Wizard.CreateInterfaceSet.create").log(Level.INFO,
        "Creating trigger loader Action: " + interfaceName);
    createAction(2, "TriggerScheduledSet", new Long(this.maxSetID + iSet).intValue(),
        new Long(this.maxActionID + iAction).intValue(), "", "", "Trigger_loaders_" + this.adapterName)
        .insertToDB(rock);

  }

  private String getTemplateName(String adapterName) {

    return adapterName.toLowerCase() + ".vm";
  }

  protected SetGenerator createSet(String type, long iSet, String name) throws Exception {

    SetGenerator set = new SetGenerator();
    set.setCOLLECTION_NAME(name);
    set.setCOLLECTION_ID(Long.toString(iSet));
    set.setMAX_ERRORS("0");
    set.setMAX_FK_ERRORS("0");
    set.setMAX_COL_LIMIT_ERRORS("0");
    set.setCHECK_FK_ERROR_FLAG("N");
    set.setCHECK_COL_LIMITS_FLAG("N");
    set.setVERSION_NUMBER(tpVersion);
    set.setCOLLECTION_SET_ID(Long.toString(techPackID));
    set.setPRIORITY("0");
    set.setQUEUE_TIME_LIMIT("30");
    set.setENABLED_FLAG("Y");
    set.setSETTYPE(type);
    set.setFOLDABLE_FLAG("Y");
    set.setHOLD_FLAG("N");

    return set;

  }

  protected ActionGenerator createAction(int order, String type, long iSet, long iAct, String whereClause,
      String actionContents, String name) throws Exception {

    ActionGenerator loadAction = new ActionGenerator();
    loadAction.setVERSION_NUMBER(tpVersion);
    loadAction.setTRANSFER_ACTION_ID(Long.toString(iAct));
    loadAction.setCOLLECTION_ID(Long.toString(iSet));
    loadAction.setCOLLECTION_SET_ID(Long.toString(techPackID));
    loadAction.setACTION_TYPE(type);
    loadAction.setTRANSFER_ACTION_NAME(name);
    loadAction.setORDER_BY_NO(Integer.toString(order));
    loadAction.setWHERE_CLAUSE(whereClause);
    loadAction.setACTION_CONTENTS(actionContents);
    loadAction.setENABLED_FLAG("Y");
    loadAction.setCONNECTION_ID(connectionID);

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

    StringTokenizer st = new StringTokenizer(str, "\n");
    while (st.hasMoreTokens()) {
      String tmp = st.nextToken();
      String[] split = tmp.split("=");
      if (split[0] != null) {
        String key = split[0];
        String value = "";
        if (split.length > 1)
          value = split[1];
        prop.setProperty(key.trim(), value.trim());
      }

    }

    return prop;

  }

  private ScheduleGenerator createSchedule(long iSet, String name) throws Exception {

    ScheduleGenerator schedule = new ScheduleGenerator();
    schedule.setVERSION_NUMBER(this.tpVersion);
    schedule.setID(new Long(getScheduleMaxID(this.rock) + 1));
    schedule.setEXECUTION_TYPE("interval");
    schedule.setSCHEDULING_MONTH(new Long(1));
    schedule.setSCHEDULING_DAY(new Long(1));
    schedule.setSCHEDULING_HOUR(new Long(0));
    schedule.setSCHEDULING_MIN(new Long(0));
    schedule.setCOLLECTION_SET_ID(new Long(techPackID));
    schedule.setCOLLECTION_ID(new Long(iSet));
    schedule.setINTERVAL_HOUR(new Long(0));
    schedule.setINTERVAL_MIN(new Long(15));
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
