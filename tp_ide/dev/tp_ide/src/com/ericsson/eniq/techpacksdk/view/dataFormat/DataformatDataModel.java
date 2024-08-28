package com.ericsson.eniq.techpacksdk.view.dataFormat;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import ssc.rockfactory.RockDBObject;
import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.repository.dwhrep.Dataformat;
import com.distocraft.dc5000.repository.dwhrep.Dataitem;
import com.distocraft.dc5000.repository.dwhrep.DataitemFactory;
import com.distocraft.dc5000.repository.dwhrep.Defaulttags;
import com.distocraft.dc5000.repository.dwhrep.DefaulttagsFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementcolumn;
import com.distocraft.dc5000.repository.dwhrep.MeasurementcolumnFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementcounter;
import com.distocraft.dc5000.repository.dwhrep.MeasurementcounterFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementkey;
import com.distocraft.dc5000.repository.dwhrep.MeasurementkeyFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementtype;
import com.distocraft.dc5000.repository.dwhrep.MeasurementtypeFactory;
import com.distocraft.dc5000.repository.dwhrep.Referencecolumn;
import com.distocraft.dc5000.repository.dwhrep.ReferencecolumnFactory;
import com.distocraft.dc5000.repository.dwhrep.Referencetable;
import com.distocraft.dc5000.repository.dwhrep.ReferencetableFactory;
import com.ericsson.eniq.component.ExceptionHandler;
import com.ericsson.eniq.techpacksdk.datamodel.DataModel;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;
import com.ericsson.eniq.techpacksdk.view.measurement.MeasurementTypeDataModel;
import com.ericsson.eniq.techpacksdk.view.newInterface.NewInterfaceDataModel;
import com.ericsson.eniq.techpacksdk.view.reference.ReferenceTypeDataModel;

/**
 * The model for storing Data Formats, Data Items and Default Tags.
 * 
 * @author ejarsav
 * @author eheitur
 * 
 */
public class DataformatDataModel implements DataModel {

  private static final String FIFTEEN_MIN = ":15MIN";

  private static final String RAW = ":RAW";
  
  private static final Logger logger = Logger.getLogger(DataformatFactory.class.getName());

  private RockFactory rockFactory = null;

  private String versionid;

  private Map<String, Vector<Defaulttags>> defaultTags;

  private Map<String, Vector<Dataformat>> dataFormats;

  private Map<String, Vector<Dataitem>> dataItems;

  public boolean newDataCreated = false;

  private Vector<String> dataformatToDelete;

  private final DataModelController dataModelController;

  private boolean dataformatsRenamed = false;

  private String renamedFrom = "";

  private String renamedTo = "";

  public DataformatDataModel(RockFactory rockFactory, DataModelController dataModelController) {
    this.rockFactory = rockFactory;
    this.dataModelController = dataModelController;
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

  @Override
  public void refresh() {

    dataFormats = getAllDataFormatsDB();
    dataItems = getAllDataItemsDB(dataFormats);
    defaultTags = getAllDefaultTagsDB(dataFormats);
    renamedFrom = "";
    renamedTo = "";
    dataformatsRenamed = false;
  }

  /**
   * Sets the versionId.
   * 
   * @param versionid
   */
  public void setVersionid(String versionid) {
    this.versionid = versionid;
  }

  @Override
  public void save() {
  }

  @Override
  public boolean updated(DataModel dataModel) throws Exception {
    if (dataModel instanceof NewInterfaceDataModel) {
      refresh();
      return true;
    } else if (dataModel instanceof MeasurementTypeDataModel || dataModel instanceof ReferenceTypeDataModel) {

      updateDataformats();
      refresh();

      dataModelController.rockObjectsModified(this);
      newDataCreated = true;
      return true;
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

  /**
   * @param dataformatid
   * @return
   */
  public Vector<Dataitem> getDataItems(String dataformatid) {
    return dataItems.get(dataformatid);
  }

  /**
   * Returns a map of all data items matching the given data formats.
   * 
   * @param dataformats
   *          a map of data formats.
   * @return a map of data items.
   */
  private Map<String, Vector<Dataitem>> getAllDataItemsDB(Map<String, Vector<Dataformat>> dataformats) {
    Map<String, Vector<Dataitem>> dataformatMap = new HashMap<String, Vector<Dataitem>>();

    Iterator<String> mapIter = dataformats.keySet().iterator();
    while (mapIter.hasNext()) {

      String key = mapIter.next();
      Vector<Dataformat> dfv = dataformats.get(key);

      Iterator<Dataformat> dfIter = dfv.iterator();
      while (dfIter.hasNext()) {
        Dataformat df = dfIter.next();

        Dataitem di = new Dataitem(rockFactory);
        di.setDataformatid(df.getDataformatid());

        try {

          DataitemFactory diF = new DataitemFactory(rockFactory, di, true);
          Vector<Dataitem> targetActions = diF.get();

          dataformatMap.put(df.getDataformatid(), targetActions);

        } catch (SQLException e) {
          ExceptionHandler.instance().handle(e);
          logger.warning(e.getMessage());
        } catch (RockException e) {
          ExceptionHandler.instance().handle(e);
          logger.warning(e.getMessage());
        }
      }
    }

    return dataformatMap;
  }

  /**
   * Returns a map of all data formats.
   * 
   * @return a map of data formats.
   */
  public Map<String, Vector<Dataformat>> getAllDataFormats() {
    return dataFormats;
  }

  /**
   * Returns all Data Formats matching the given data format type.
   * 
   * @param dataformattype
   *          The data format type, for example: 'mdc'.
   * @return a vector of data formats. Null in case no match for type.
   */
  public Vector<Dataformat> getDataFormats(String dataformattype) {
    return dataFormats.get(dataformattype);
  }

  /**
   * Gets a map of all Data Formats from the DB.
   * 
   * @return
   */
  private Map<String, Vector<Dataformat>> getAllDataFormatsDB() {

    Map<String, Vector<Dataformat>> theSets = new HashMap<String, Vector<Dataformat>>();

    try {

      Dataformat df = new Dataformat(rockFactory);
      df.setVersionid(versionid);
      com.distocraft.dc5000.repository.dwhrep.DataformatFactory mcF = new com.distocraft.dc5000.repository.dwhrep.DataformatFactory(
          rockFactory, df);

      Vector<Dataformat> dataformats = mcF.get();

      for (Iterator<Dataformat> iter = dataformats.iterator(); iter.hasNext();) {

        Dataformat mSet = iter.next();

        if (theSets.containsKey(mSet.getDataformattype())) {

          theSets.get(mSet.getDataformattype()).add(mSet);

        } else {

          Vector<Dataformat> v = new Vector<Dataformat>();
          v.add(mSet);

          theSets.put(mSet.getDataformattype(), v);
        }
      }

    } catch (SQLException e) {
      ExceptionHandler.instance().handle(e);
      logger.warning(e.getMessage());
    } catch (RockException e) {
      ExceptionHandler.instance().handle(e);
      logger.warning(e.getMessage());
    }

    return theSets;
  }

  public Vector<Defaulttags> getDefaultTags(String dataformatid) {
    return defaultTags.get(dataformatid);
  }

  public Map<String, Vector<Defaulttags>> getAllDefaultTagsMap() {
    return defaultTags;
  }

  public Map<String, Vector<Defaulttags>> getAllDefaultTagsDB(Map<String, Vector<Dataformat>> dataformats) {

    Map<String, Vector<Defaulttags>> tagMap = new HashMap<String, Vector<Defaulttags>>();

    Iterator<String> mapIter = dataformats.keySet().iterator();
    while (mapIter.hasNext()) {

      String key = mapIter.next();
      Vector<Dataformat> dfv = dataformats.get(key);

      Iterator<Dataformat> dfIter = dfv.iterator();
      while (dfIter.hasNext()) {
        Dataformat df = dfIter.next();

        try {

          Defaulttags dTags = new Defaulttags(rockFactory);
          dTags.setDataformatid(df.getDataformatid());
          DefaulttagsFactory intfDepF = new DefaulttagsFactory(rockFactory, dTags, true);
          Vector<Defaulttags> targetActions = intfDepF.get();
          tagMap.put(df.getDataformatid(), targetActions);

        } catch (Exception e) {
          ExceptionHandler.instance().handle(e);
          logger.warning(e.getMessage());
        }
      }
    }
    return tagMap;
  }

  public void removeDataType(String dataformattype) {

    Iterator<Dataformat> iter = dataFormats.get(dataformattype).iterator();

    while (iter.hasNext()) {

      Dataformat dataformat = iter.next();

      dataItems.remove(dataformat.getDataformatid());
      defaultTags.remove(dataformat.getDataformatid());
      iter.remove();
    }

    dataFormats.remove(dataformattype);

    if (dataformatToDelete == null) {
      dataformatToDelete = new Vector<String>();
    }

    dataformatToDelete.add(dataformattype);

  }

  public void renameDataType(String dataformattype, String newname) {

    Iterator<Dataformat> iter = dataFormats.get(dataformattype).iterator();

    Vector<Dataformat> mtVec = new Vector<Dataformat>();

    while (iter.hasNext()) {

      Dataformat dataformat = iter.next();

      Vector<Dataitem> diVec = new Vector<Dataitem>();
      Vector<Defaulttags> dTagVec = new Vector<Defaulttags>();

      Dataformat df = new Dataformat(rockFactory);
      String dataformatid = dataformat.getTypeid() + ":" + newname;
      df.setDataformatid(dataformatid);
      df.setTypeid(dataformat.getTypeid());
      df.setVersionid(dataformat.getVersionid());
      df.setObjecttype(dataformat.getObjecttype());
      df.setFoldername(dataformat.getFoldername());
      df.setDataformattype(newname);

      mtVec.add(df);

      try {

        Iterator<Dataitem> diVecIter = dataItems.get(dataformat.getDataformatid()).iterator();
        while (diVecIter.hasNext()) {

          Dataitem di = diVecIter.next();

          Dataitem newdi = new Dataitem(rockFactory);
          newdi.setDataformatid(dataformatid);
          newdi.setDataname(di.getDataname());
          newdi.setColnumber(di.getColnumber());
          newdi.setDataid(di.getDataid());
          newdi.setProcess_instruction(di.getProcess_instruction());
          newdi.setDatatype(di.getDatatype());
          newdi.setDatasize(di.getDatasize());
          newdi.setDatascale(di.getDatascale());

          diVec.add(newdi);

        }
      } catch (Exception e) {
        ExceptionHandler.instance().handle(e);
        logger.warning(e.getMessage());
      }

      try {

        Iterator<Defaulttags> dTagVecIter = defaultTags.get(dataformat.getDataformatid())
            .iterator();
        while (dTagVecIter.hasNext()) {

          Defaulttags dtag = dTagVecIter.next();

          Defaulttags newddtag = new Defaulttags(rockFactory);
          newddtag.setDataformatid(dataformatid);
          newddtag.setTagid(dtag.getTagid());
          newddtag.setDescription(dtag.getDescription());

          dTagVec.add(newddtag);
        }
      } catch (Exception e) {
        ExceptionHandler.instance().handle(e);
        logger.warning(e.getMessage());
      }

      dataItems.put(dataformatid, diVec);
      defaultTags.put(dataformatid, dTagVec);

      dataItems.remove(dataformat.getDataformatid());
      defaultTags.remove(dataformat.getDataformatid());

      if (dataformatToDelete == null) {
        dataformatToDelete = new Vector<String>();
      }

      dataformatToDelete.add(dataformattype);
    }

    // add new (renamed one)
    dataFormats.put(newname, mtVec);

    // remove old
    dataFormats.remove(dataformattype);

    dataformatsRenamed = true;
    renamedFrom = dataformattype;
    renamedTo = newname;

  }

  public void addDataType(String dataformattype) {

    try {

      Measurementtype mt = new Measurementtype(rockFactory);
      mt.setVersionid(versionid);
      mt.setDataformatsupport(new Integer(1));
      MeasurementtypeFactory mtF = new MeasurementtypeFactory(rockFactory, mt);
      Iterator<Measurementtype> iter = mtF.get().iterator();

      Vector<Dataformat> mtVec = new Vector<Dataformat>();

      while (iter.hasNext()) {

        Measurementtype mtype = iter.next();

        String dataformatid = mtype.getTypeid() + ":" + dataformattype;

        Dataformat df = new Dataformat(rockFactory);
        df.setDataformatid(dataformatid);
        df.setTypeid(mtype.getTypeid());
        df.setVersionid(mtype.getVersionid());
        df.setObjecttype("Measurement");
        df.setFoldername(mtype.getTypename());
        df.setDataformattype(dataformattype);

        mtVec.add(df);

        List<Measurementcolumn> listOfCols = getMeasurementColumns(mtype, RAW);
        if(listOfCols.isEmpty()){
          //Add 15MIN for data tiering also
          listOfCols = getMeasurementColumns(mtype, FIFTEEN_MIN);
        }
        Iterator<Measurementcolumn> mcFIter = listOfCols.iterator();
        
        Vector<Dataitem> diVec = new Vector<Dataitem>();
        while (mcFIter.hasNext()) {

          Measurementcolumn mcol = mcFIter.next();

          Dataitem di = new Dataitem(rockFactory);
          di.setDataformatid(dataformatid);
          di.setDataname(mcol.getDataname());
          di.setColnumber(mcol.getColnumber());
          di.setDataid(mcol.getDataid());
          String typeid = mcol.getMtableid().substring(0, mcol.getMtableid().lastIndexOf(":"));
          di.setProcess_instruction(getProcessInstructions(mcol.getDataname(), typeid));
          di.setDatatype(mcol.getDatatype());
          di.setDatasize(mcol.getDatasize());
          di.setDatascale(mcol.getDatascale());

          diVec.add(di);
        }

        dataItems.put(dataformatid, diVec);

        defaultTags.put(dataformatid, new Vector<Defaulttags>());
      }

      // Reference

      Referencetable rt = new Referencetable(rockFactory);
      rt.setVersionid(versionid);
      rt.setDataformatsupport(new Integer(1));
      ReferencetableFactory rtF = new ReferencetableFactory(rockFactory, rt);
      Iterator<Referencetable> rtFIter = rtF.get().iterator();

      // Vector<Dataformat> rtVec = new Vector<Dataformat>();

      while (rtFIter.hasNext()) {

        Referencetable rtype = rtFIter.next();

        String dataformatid = rtype.getTypeid() + ":" + dataformattype;

        Dataformat df = new Dataformat(rockFactory);
        df.setDataformatid(dataformatid);
        df.setTypeid(rtype.getTypeid());
        df.setVersionid(rtype.getVersionid());
        df.setObjecttype("Reference");
        df.setFoldername(rtype.getObjectname());
        df.setDataformattype(dataformattype);

        mtVec.add(df);

        Referencecolumn rc = new Referencecolumn(rockFactory);
        rc.setTypeid(rtype.getTypeid());
        ReferencecolumnFactory rcF = new ReferencecolumnFactory(rockFactory, rc);
        Iterator<Referencecolumn> rcFIter = rcF.get().iterator();
        Vector<Dataitem> diVec = new Vector<Dataitem>();
        while (rcFIter.hasNext()) {

          Referencecolumn rcol = rcFIter.next();

          Dataitem di = new Dataitem(rockFactory);
          di.setDataformatid(dataformatid);
          di.setDataname(rcol.getDataname());
          di.setColnumber(rcol.getColnumber());
          di.setDataid(rcol.getDataname());
          di.setProcess_instruction(getProcessInstructions(rcol.getDataname(), rcol.getTypeid()));
          di.setDatatype(rcol.getDatatype());
          di.setDatasize(rcol.getDatasize());
          di.setDatascale(rcol.getDatascale());

          diVec.add(di);
        }

        dataItems.put(dataformatid, diVec);

        defaultTags.put(dataformatid, new Vector<Defaulttags>());
      }

      dataFormats.put(dataformattype, mtVec);

    } catch (Exception e) {
      ExceptionHandler.instance().handle(e);
      logger.warning(e.getMessage());
    }
  }

  public void removeMarkedDataformats() {

    if (dataformatToDelete != null) {

      try {

        // remove all marked dataformats, dataitems and defaultags from DB
        Iterator<String> deIter = dataformatToDelete.iterator();
        while (deIter.hasNext()) {
          String type = deIter.next();

          Dataformat d = new Dataformat(rockFactory);
          d.setVersionid(versionid);
          d.setDataformattype(type);
          com.distocraft.dc5000.repository.dwhrep.DataformatFactory dF = new com.distocraft.dc5000.repository.dwhrep.DataformatFactory(
              rockFactory, d);
          Iterator<Dataformat> dvIter = dF.get().iterator();
          while (dvIter.hasNext()) {
            Dataformat dForm = dvIter.next();

            Dataitem di = new Dataitem(rockFactory);
            di.setDataformatid(dForm.getDataformatid());
            DataitemFactory diF = new DataitemFactory(rockFactory, di);

            Iterator<Dataitem> diFI = diF.get().iterator();
            while (diFI.hasNext()) {
              Dataitem dItem = diFI.next();
              dItem.deleteDB();
            }

            Defaulttags dt = new Defaulttags(rockFactory);
            dt.setDataformatid(dForm.getDataformatid());
            DefaulttagsFactory dtF = new DefaulttagsFactory(rockFactory, dt);

            Iterator<Defaulttags> dtFI = dtF.get().iterator();
            while (dtFI.hasNext()) {
              Defaulttags dTag = dtFI.next();
              dTag.deleteDB();
            }

            dForm.deleteDB();

          }
        }

      } catch (Exception e) {
        ExceptionHandler.instance().handle(e);
        logger.warning(e.getMessage());
      }

      dataformatToDelete.clear();
    }
  }

  public void saveDataformats() {

    // save all new dataformats to DB

    Iterator<String> dIter = dataFormats.keySet().iterator();
    while (dIter.hasNext()) {
      String key = dIter.next();
      Vector<Dataformat> dVec = dataFormats.get(key);
      Iterator<Dataformat> dvIter = dVec.iterator();
      while (dvIter.hasNext()) {
        Dataformat dForm = dvIter.next();
        if (dForm.isNewItem()) {
          try {
            dForm.saveToDB();
            // remove saved types so we dont remove them later on...
            dataformatToDelete.remove(dForm.getDataformattype());
          } catch (Exception e) {
            logger.warning(e.getMessage());
          }
        }
      }
    }
  }

  /**
   * Gets the process instruction value for a measurement columns. The value
   * will be "key" for the measurement key columns, and the type of the counter
   * for measurement counter columns (for example: "PEG"). A special case is the
   * "DCVECTOR_INDEX" measurement key, for which an empty value will be returned
   * to make MDC parser work properly.
   * 
   * @param dataname
   * @param typeid
   * @return
   * @throws Exception
   */
  private String getProcessInstructions(String dataname, String typeid) throws Exception {

    // Check for special case "DCVECTOR_INDEX" measurement key. Return an empty
    // string.
    if (dataname.equalsIgnoreCase("DCVECTOR_INDEX")) {
      return "";
    }

    // Return "key" for a measurement keys defined in this techpack.
    Measurementkey mk = new Measurementkey(rockFactory);
    mk.setDataname(dataname);
    mk.setTypeid(typeid);
    MeasurementkeyFactory mkF = new MeasurementkeyFactory(rockFactory, mk);
    if (mkF != null && mkF.get().size() > 0) {
      return "key";
    }

    // Return the counter type for counters defined in this techpack.
    Measurementcounter mc = new Measurementcounter(rockFactory);
    mc.setDataname(dataname);
    mc.setTypeid(typeid);
    MeasurementcounterFactory mcF = new MeasurementcounterFactory(rockFactory, mc);
    if (mcF != null && mcF.get().size() > 0) {
      return mcF.getElementAt(0).getCountertype();
    }

    // Return an empty string for any other column, for example: columns from
    // the base techpack.
    return "";
  }

  /**
   * Updates the data format information based on the information in the
   * measurement type, measurement column, reference table and reference column
   * tables.
   * 
   * The new data format and data item information is collected from the tables
   * above. The existing data formats and items are compared to the new ones.
   * The new ones will be added to the DB, and old ones not existing in the new
   * collected data, will be removed from the DB.
   * 
   * @throws Exception
   */
  public void updateDataformats() throws Exception {

    // Iterate through all the data format types in the model.
    Iterator<String> dIter = dataFormats.keySet().iterator();
    while (dIter.hasNext()) {

      // Get the next data format type
      String dataformattype = dIter.next();

      // Create maps for storing data formats and data items.
      Map<String, Vector<Dataformat>> newDf = new HashMap<String, Vector<Dataformat>>();
      Map<String, Vector<Dataitem>> newDi = new HashMap<String, Vector<Dataitem>>();

      // Populate the maps by getting all the data formats and data items for
      // measurement types and reference types (which have data format support
      // enabled).

      // Measurement types:
      //
      // Iterate through all measurement types with data format support.
      Measurementtype mt = new Measurementtype(rockFactory);
      mt.setVersionid(versionid);
      mt.setDataformatsupport(new Integer(1));
      MeasurementtypeFactory mtF = new MeasurementtypeFactory(rockFactory, mt);
      Iterator<Measurementtype> iter = mtF.get().iterator();

      while (iter.hasNext()) {

        // Get next measurement type.
        Measurementtype mtype = iter.next();

        // Get DataFormatId from the measurement and data format types.
        String dataformatid = mtype.getTypeid() + ":" + dataformattype;

        // Get the data format matching the id from the DB.
        Dataformat df = new Dataformat(rockFactory);
        df.setDataformatid(dataformatid);
        df.setTypeid(mtype.getTypeid());
        df.setVersionid(mtype.getVersionid());
        df.setObjecttype("Measurement");
        df.setFoldername(mtype.getTypename());
        df.setDataformattype(dataformattype);

        // If the new data format type is already in the data format map, then
        // add the data format to the vector for that type. Otherwise, the
        // vector is created first before adding.
        if (newDf.containsKey(dataformattype)) {
          newDf.get(dataformattype).add(df);
        } else {
          Vector<Dataformat> v = new Vector<Dataformat>();
          v.add(df);
          newDf.put(dataformattype, v);
        }

        // Iterate through all measurement columns for this measurement type.
        // Only 'RAW' table columns are included.
        List<Measurementcolumn> listOfCols = getMeasurementColumns(mtype, RAW);
        if(listOfCols.isEmpty()){
          //Add 15MIN for data tiering also
          listOfCols = getMeasurementColumns(mtype, FIFTEEN_MIN);
        }
        Iterator<Measurementcolumn> mcFIter = listOfCols.iterator();

        while (mcFIter.hasNext()) {

          // Get the next measurement column.
          Measurementcolumn mcol = mcFIter.next();

          // Get the data item matching the data format and the column from the
          // DB.
          Dataitem di = new Dataitem(rockFactory);
          di.setDataformatid(dataformatid);
          di.setDataname(mcol.getDataname());
          di.setColnumber(mcol.getColnumber());
          di.setDataid(mcol.getDataid());
          String typeid = mcol.getMtableid().substring(0, mcol.getMtableid().lastIndexOf(":"));
          di.setProcess_instruction(getProcessInstructions(mcol.getDataname(), typeid));
          di.setDatatype(mcol.getDatatype());
          di.setDatasize(mcol.getDatasize());
          di.setDatascale(mcol.getDatascale());

          // If the new data format id is already in the data item map, then
          // add the data item to the vector for that id. Otherwise, the
          // vector is created first before adding.
          if (newDi.containsKey(dataformatid)) {
            newDi.get(dataformatid).add(di);
          } else {
            Vector<Dataitem> v = new Vector<Dataitem>();
            v.add(di);
            newDi.put(dataformatid, v);
          }
        }
      }

      // Reference types:
      //
      // Iterate through all reference types with data format support.
      Referencetable rt = new Referencetable(rockFactory);
      rt.setVersionid(versionid);
      rt.setDataformatsupport(new Integer(1));
      ReferencetableFactory rtF = new ReferencetableFactory(rockFactory, rt);
      Iterator<Referencetable> rtFIter = rtF.get().iterator();

      while (rtFIter.hasNext()) {

        // Get next reference table.
        Referencetable rtype = rtFIter.next();

        // Get DataFormatId from the reference table and data format types.
        String dataformatid = rtype.getTypeid() + ":" + dataformattype;

        // Get the data format matching the id from the DB.
        Dataformat df = new Dataformat(rockFactory);
        df.setDataformatid(dataformatid);
        df.setTypeid(rtype.getTypeid());
        df.setVersionid(rtype.getVersionid());
        df.setObjecttype("Reference");
        df.setFoldername(rtype.getObjectname());
        df.setDataformattype(dataformattype);

        // If the new data format type is already in the data format map, then
        // add the data format to the vector for that type. Otherwise, the
        // vector is created first before adding.
        if (newDf.containsKey(dataformattype)) {
          newDf.get(dataformattype).add(df);
        } else {
          Vector<Dataformat> v = new Vector<Dataformat>();
          v.add(df);
          newDf.put(dataformattype, v);
        }

        // Iterate through all reference columns for this reference table.
        Referencecolumn rc = new Referencecolumn(rockFactory);
        rc.setTypeid(rtype.getTypeid());
        ReferencecolumnFactory rcF = new ReferencecolumnFactory(rockFactory, rc);
        Iterator<Referencecolumn> rcFIter = rcF.get().iterator();

        while (rcFIter.hasNext()) {

          // Get the next reference column.
          Referencecolumn rcol = rcFIter.next();

          // Get the data item matching the data format and the column from the
          // DB.
          Dataitem di = new Dataitem(rockFactory);
          di.setDataformatid(dataformatid);
          di.setDataname(rcol.getDataname());
          di.setColnumber(rcol.getColnumber());
          // If the dataId is not set for the reference column, then use the
          // data name value.
          if (rcol.getDataid() == null) {
            di.setDataid(rcol.getDataname());
          } else {
            di.setDataid(rcol.getDataid());
          }
          di.setProcess_instruction(getProcessInstructions(rcol.getDataname(), rcol.getTypeid()));
          di.setDatatype(rcol.getDatatype());
          di.setDatasize(rcol.getDatasize());
          di.setDatascale(rcol.getDatascale());

          // If the new data format id is already in the data item map, then
          // add the data item to the vector for that id. Otherwise, the
          // vector is created first before adding.
          if (newDi.containsKey(dataformatid)) {
            newDi.get(dataformatid).add(di);
          } else {
            Vector<Dataitem> v = new Vector<Dataitem>();
            v.add(di);
            newDi.put(dataformatid, v);
          }
        }
      }

      // Now the maps contain new data format and data item information
      // collected from measurement types and reference tables.

      // Get old data format information.
      Vector<Dataformat> oldDfs = dataFormats.get(dataformattype);

      // Iterate through the old data formats. The old data formats and data
      // items, which are not found in the new list, are removed from the DB.
      // The old ones which are also in the new list are not touched. The new
      // ones are added to the DB.
      Iterator<Dataformat> oldDfsi = oldDfs.iterator();
      while (oldDfsi.hasNext()) {

        // Get next old data format and its data items.
        Dataformat oldDataformat = oldDfsi.next();
        Vector<Dataitem> oldDis = dataItems.get(oldDataformat.getDataformatid());

        logger.finest("Investigating Dataformat: " + oldDataformat.getDataformatid());

        // Iterate through all data items for this old data format and check if
        // the new list contain any of the old ones. The match is based on the
        // dataformatid (key), dataname (key), and colnumber.
        //
        // If the match is found, then remove the data item from the new data
        // item map and the old list. If the match is not found, then leave the
        // data item to the new map, but remove it from the old list and from
        // DB.
        Iterator<Dataitem> oldDisi = oldDis.iterator();
        while (oldDisi.hasNext()) {
          Dataitem oldDataitem = oldDisi.next();
          Dataitem matchDi = null;
          if (newDi.containsKey(oldDataformat.getDataformatid())) {
            Iterator<Dataitem> newDii = newDi.get(oldDataformat.getDataformatid()).iterator();
            while (newDii.hasNext()) {

              Dataitem newDataitem = newDii.next();
              //Fix for TR HP88992
              if(versionid != null && versionid.length() > 0){
            	  if(com.ericsson.eniq.common.Utils.getTechPackType(getRockFactory(), versionid).equals(com.ericsson.eniq.common.TechPackType.EVENTS)){
            		  if(newDataitem != null && oldDataitem != null && oldDataitem.getDataid() != null ){
            			  if (newDataitem.getDataformatid().equals(oldDataitem.getDataformatid())
            					  && newDataitem.getColnumber().equals(oldDataitem.getColnumber())){
            				  newDataitem.setDataid(oldDataitem.getDataid());
            			  }
            		  }
            	  }
              }
              
              
              if (newDataitem.getDataformatid().equals(oldDataitem.getDataformatid())
                  && newDataitem.getColnumber().equals(oldDataitem.getColnumber())
                  && newDataitem.getDataname().equals(oldDataitem.getDataname())
                  && newDataitem.getDatatype().equals(oldDataitem.getDatatype())
                  && newDataitem.getDatasize() == oldDataitem.getDatasize()
            	  && newDataitem.getDatascale() == oldDataitem.getDatascale()
            	  && (newDataitem.getProcess_instruction() != null && newDataitem.getProcess_instruction().equals(oldDataitem.getProcess_instruction()))) {
                matchDi = newDataitem;
                break;
              }
            }
          }
          // Check for match.
          if (matchDi != null) {
            // Match found. Remove the data item from the new data item map and
            // from the old list.
            newDi.get(oldDataformat.getDataformatid()).remove(matchDi);
            oldDisi.remove();

          } else {
            // Match not found. Remove the data item from the old list and from
            // the DB.
            oldDisi.remove();
            oldDataitem.deleteDB();
          }
        }

        // All data items left in new map will be added to DB.
        if (newDi.containsKey(oldDataformat.getDataformatid())) {
          Iterator<Dataitem> newDii = newDi.get(oldDataformat.getDataformatid()).iterator();
          while (newDii.hasNext()) {

            Dataitem addMe = newDii.next();
            if (addMe != null && addMe.getDataid() != null) {
              addMe.insertDB();
            }
          }
        }

        // Check if the new data format map contains the current old data
        // format.
        //
        // If the match is found, then remove the data format from the new data
        // format map and the old list. If the match is not found, then leave
        // the
        // data item to the new map, but remove it from the old list and from
        // DB.
        // does the new list contain oldone (dataformat)
        Dataformat matchDf = null;
        if (newDf.get(dataformattype) != null) {
          Iterator<Dataformat> newDfi = newDf.get(dataformattype).iterator();
          while (newDfi.hasNext()) {

            Dataformat newDataformat = newDfi.next();

            if (newDataformat.getDataformatid().equals(oldDataformat.getDataformatid())) {
              matchDf = newDataformat;
              break;
            }
          }
        }

        // Check for match.
        if (matchDf != null) {
          // Match found. Remove the old data format from the new map and the
          // old list.
          newDf.get(dataformattype).remove(matchDf);
          oldDfsi.remove();

        } else {
          // Match not found. Remove the data tags (if any), the data format
          // from the old list and from DB.

          Defaulttags dft = new Defaulttags(rockFactory);
          dft.setDataformatid(oldDataformat.getDataformatid());
          DefaulttagsFactory dftF = new DefaulttagsFactory(rockFactory, dft);
          Iterator<Defaulttags> dftFIter = dftF.get().iterator();
          while (dftFIter.hasNext()) {
            Defaulttags dtag = dftFIter.next();
            dtag.deleteDB();
          }

          oldDfsi.remove();
          oldDataformat.deleteDB();
        }

      }

      // The data formats left in new map will be added to DB.
      if (newDf.get(dataformattype) != null) {
        Iterator<Dataformat> newDfi = newDf.get(dataformattype).iterator();
        while (newDfi.hasNext()) {

          Dataformat addMe = newDfi.next();
          addMe.insertDB();

          // Dataitems left in new list will be added to DB
          if (newDi.containsKey(addMe.getDataformatid())) {
            Iterator<Dataitem> newDii = newDi.get(addMe.getDataformatid()).iterator();
            while (newDii.hasNext()) {

              Dataitem addMei = newDii.next();
              if (addMei != null && addMei.getDataid() != null) {
                addMei.insertDB();
              }
            }
          }
        }
      }
    }
  }

  /**
   * @param mtype
   * @return
   * @throws SQLException
   * @throws RockException
   */
  protected List<Measurementcolumn> getMeasurementColumns(final Measurementtype mtype, final String tablelevel) throws SQLException, RockException {
    Measurementcolumn mc = new Measurementcolumn(rockFactory);
    mc.setMtableid(mtype.getTypeid() + tablelevel);
    MeasurementcolumnFactory mcF = new MeasurementcolumnFactory(rockFactory, mc);
    List<Measurementcolumn> listOfCols = mcF.get();
    return listOfCols;
  }

  /**
   * Migrates the data formation information in the database. The measurement
   * types and reference types are marked with dataformasupport = 1 or 0 based
   * on if they are found from the dataformat table or not, respectively.
   * 
   * @param versionid
   * @param fromEniqLevel
   * @throws Exception
   */
  public void migrate(String versionid, String fromEniqLevel) throws Exception {

    // Check the from version.
    if (!fromEniqLevel.equals("1.0")) {
      // No need for migrate.
      logger.log(Level.FINEST, "No need to migrate data formats.");
      return;
    } else {
      // remove all marked dataformats, dataitems and defaultags from DB

      Measurementtype mt = new Measurementtype(rockFactory);
      mt.setVersionid(versionid);
      MeasurementtypeFactory mtF = new MeasurementtypeFactory(rockFactory, mt);
      Iterator<Measurementtype> mtFIter = mtF.get().iterator();
      while (mtFIter.hasNext()) {
        Measurementtype mtype = mtFIter.next();

        // look for meastype in dataformat
        Dataformat d = new Dataformat(rockFactory);
        d.setVersionid(versionid);
        d.setTypeid(mtype.getTypeid());
        com.distocraft.dc5000.repository.dwhrep.DataformatFactory dF = new com.distocraft.dc5000.repository.dwhrep.DataformatFactory(
            rockFactory, d);

        if (dF.size() > 0) {
          // dataformat is in measurement so mark it as dataformatsupported
          mtype.setDataformatsupport(1);
          mtype.saveDB();
        } else {
          mtype.setDataformatsupport(0);
          mtype.saveDB();
        }
      }

      Referencetable rt = new Referencetable(rockFactory);
      rt.setVersionid(versionid);
      ReferencetableFactory rtF = new ReferencetableFactory(rockFactory, rt);
      Iterator<Referencetable> rtFIter = rtF.get().iterator();
      while (rtFIter.hasNext()) {
        Referencetable rtype = rtFIter.next();

        // look for Reference in dataformat
        Dataformat d = new Dataformat(rockFactory);
        d.setVersionid(versionid);
        d.setTypeid(rtype.getTypeid());
        com.distocraft.dc5000.repository.dwhrep.DataformatFactory dF = new com.distocraft.dc5000.repository.dwhrep.DataformatFactory(
            rockFactory, d);

        if (dF.size() > 0) {
          // dataformat is in referense so mark it as dataformatsupported
          rtype.setDataformatsupport(1);
          rtype.saveDB();
        } else {
          rtype.setDataformatsupport(0);
          rtype.saveDB();
        }
      }
    }
  }

  /**
   * @return
   */
  public boolean isDataformatsRenamed() {
    return dataformatsRenamed;
  }

  public void setDataformatsRenamed(boolean dataformatsRenamed) {
    this.dataformatsRenamed = dataformatsRenamed;
  }

  public String getRenamedFrom() {
    return renamedFrom;
  }

  public String getRenamedTo() {
    return renamedTo;
  }

}
