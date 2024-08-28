/**
 * 
 */
package com.ericsson.eniq.afj.database;

import static com.ericsson.eniq.afj.common.PropertyConstants.DATAFORMAT_DELIMITER;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_BSS_ACTIONTYPE;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_BSS_ADDVENDORIDTO;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_DEF_PARTITIONTYPE;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_DEF_TABLELEVEL;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_FOLLOWJOHN;

import java.io.ByteArrayInputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Logger;

import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.etl.rock.Meta_collection_setsFactory;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_collectionsFactory;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actionsFactory;
import com.distocraft.dc5000.repository.dwhrep.Dataformat;
import com.distocraft.dc5000.repository.dwhrep.DataformatFactory;
import com.distocraft.dc5000.repository.dwhrep.Dataitem;
import com.distocraft.dc5000.repository.dwhrep.DataitemFactory;
import com.distocraft.dc5000.repository.dwhrep.Defaulttags;
import com.distocraft.dc5000.repository.dwhrep.DefaulttagsFactory;
import com.distocraft.dc5000.repository.dwhrep.Dwhtype;
import com.distocraft.dc5000.repository.dwhrep.DwhtypeFactory;
import com.distocraft.dc5000.repository.dwhrep.Interfacemeasurement;
import com.distocraft.dc5000.repository.dwhrep.Interfacetechpacks;
import com.distocraft.dc5000.repository.dwhrep.InterfacetechpacksFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementcolumn;
import com.distocraft.dc5000.repository.dwhrep.MeasurementcolumnFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementcounter;
import com.distocraft.dc5000.repository.dwhrep.MeasurementcounterFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementkey;
import com.distocraft.dc5000.repository.dwhrep.Measurementtable;
import com.distocraft.dc5000.repository.dwhrep.Measurementtype;
import com.distocraft.dc5000.repository.dwhrep.MeasurementtypeFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementtypeclass;
import com.distocraft.dc5000.repository.dwhrep.Tpactivation;
import com.distocraft.dc5000.repository.dwhrep.TpactivationFactory;
import com.distocraft.dc5000.repository.dwhrep.Transformation;
import com.distocraft.dc5000.repository.dwhrep.TransformationFactory;
import com.distocraft.dc5000.repository.dwhrep.Transformer;
import com.distocraft.dc5000.repository.dwhrep.TransformerFactory;
import com.distocraft.dc5000.repository.dwhrep.Typeactivation;
import com.distocraft.dc5000.repository.dwhrep.TypeactivationFactory;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.distocraft.dc5000.repository.dwhrep.VersioningFactory;
import com.ericsson.eniq.afj.common.AFJDelta;
import com.ericsson.eniq.afj.common.AFJMeasurementCounter;
import com.ericsson.eniq.afj.common.AFJMeasurementTag;
import com.ericsson.eniq.afj.common.AFJMeasurementType;
import com.ericsson.eniq.afj.common.PropertiesUtility;
import com.ericsson.eniq.exception.AFJException;

/**
 * Utility class used by afj manager to get required values from the database.
 * 
 * @author esunbal
 * 
 */
public class AFJDatabaseCommonUtils {

  private final Logger log = Logger.getLogger(this.getClass().getName());

  /**
   * Lists out the counters associated with a given measurement type tag name.
   * 
   * @param measTypeTagName
   * @return
   * @throws AFJException
   */
  public List<String> populateMeasCountersMap(final String measTypeTagName, final RockFactory dwhrep)
      throws AFJException {
    final List<String> measCounterList = new ArrayList<String>();
    final Measurementcounter measCounter = new Measurementcounter(dwhrep);
    measCounter.setTypeid(measTypeTagName);

    try {
      final MeasurementcounterFactory factory = new MeasurementcounterFactory(dwhrep, measCounter);
      final List<Measurementcounter> dbMeasCounterList = factory.get();
      if (dbMeasCounterList.isEmpty()) {
        throw new AFJException("No matching record found in MeasurementCounter table for typeId:" + measTypeTagName);
      }
      for (Measurementcounter mc : dbMeasCounterList) {
        measCounterList.add(mc.getDataname().toUpperCase());
      }
    } catch (SQLException e) {
      throw new AFJException("SQL Error in fetching rows from Measurementcounter:" + e.getMessage());
    } catch (RockException e) {
      throw new AFJException("Unable to use the MeasurementcounterFactory implementation:" + e.getMessage());
    }
    return measCounterList;
  }

  /**
   * Gets the active techpack version of a given techpack.
   * 
   * @param techpackName
   * @return
   * @throws AFJException
   */
  public String getActiveTechPackVersion(final String techpackName, final RockFactory dwhrep) throws AFJException {
    String tpVersion = null;
    final Tpactivation activation = new Tpactivation(dwhrep);
    activation.setTechpack_name(techpackName);

    try {
      final TpactivationFactory factory = new TpactivationFactory(dwhrep, activation);
      final List<Tpactivation> activationList = factory.get();
      if (activationList.isEmpty()) {
        throw new AFJException("There is no active techpack found for " + techpackName);
      }
      tpVersion = activationList.get(0).getVersionid();
    } catch (SQLException e) {
      throw new AFJException("SQL Error in fetching rows from Tpactivation:" + e.getMessage());
    } catch (RockException e) {
      throw new AFJException("Unable to use the TpactivationFactory implementation:" + e.getMessage());
    }
    return tpVersion;
  }

  /**
   * Gets the active techpack version of a given techpack.
   * 
   * @param techpackName
   * @return
   * @throws AFJException
   */
  public String getInterfaceVersion(final String interfaceName, final RockFactory dwhrep) throws AFJException {
    String interfaceVersion = null;
    final Interfacetechpacks activation = new Interfacetechpacks(dwhrep);
    activation.setInterfacename(interfaceName);

    try {
      final InterfacetechpacksFactory factory = new InterfacetechpacksFactory(dwhrep, activation);
      final Vector<Interfacetechpacks> activationList = factory.get();
      if (activationList.isEmpty()) {
        throw new AFJException("There is no interface version found for " + interfaceName);
      }
      interfaceVersion = activationList.get(0).getInterfaceversion();
    } catch (SQLException e) {
      throw new AFJException("SQL Error in fetching rows from Interfacetechpacks:" + e.getMessage());
    } catch (RockException e) {
      throw new AFJException("Unable to use the InterfacetechpacksFactory implementation:" + e.getMessage());
    }
    return interfaceVersion;
  }

  /**
   * Method to populate the list which contains the meastype tags which are handled as "many to one" mapping to a meas
   * type. Most applicable for BSS meas types: e.g. BSC
   * 
   * @param techpackVersion
   * @param bssInterfaceName
   * @param etlrep
   * @return
   * @throws AFJException
   */
  public ArrayList<String> populateAddVendorIdList(final String techpackVersion, final String bssInterfaceName,
      final RockFactory etlrep) throws AFJException {

    ArrayList<String> addVendorIdList = null;
    try {
      final Meta_collection_sets mcs = new Meta_collection_sets(etlrep);
      mcs.setCollection_set_name(bssInterfaceName);
      final Meta_collection_setsFactory mcsFactory = new Meta_collection_setsFactory(etlrep, mcs);
      final Vector<Meta_collection_sets> mcsList = mcsFactory.get();
      if (mcsList.isEmpty()) {
        throw new AFJException("There is no bss interface defined on the server.");
      }
      final Meta_collection_sets mcsValue = mcsList.get(0);// We should get only one match.
      final long collection_set_id = mcsValue.getCollection_set_id();

      final Meta_transfer_actions mta = new Meta_transfer_actions(etlrep);
      mta.setCollection_set_id(collection_set_id);
      mta.setAction_type(PropertiesUtility.getProperty(PROP_BSS_ACTIONTYPE));
      final Meta_transfer_actionsFactory mtaFactory = new Meta_transfer_actionsFactory(etlrep, mta);
      @SuppressWarnings({ "unchecked" })
      final Vector<Meta_transfer_actions> mtaList = mtaFactory.get();
      final Meta_transfer_actions mtaValue = mtaList.get(0); // We should get only one match.
      final String actionContents = mtaValue.getAction_contents_01();

      final Properties properties = new Properties();
      final ByteArrayInputStream bais = new ByteArrayInputStream(actionContents.getBytes());
      properties.load(bais);
      final String addVendorIdTo = properties.getProperty(PropertiesUtility.getProperty(PROP_BSS_ADDVENDORIDTO));
      final String[] stringArray = addVendorIdTo.split(",");
      addVendorIdList = new ArrayList<String>(Arrays.asList(stringArray));
    } catch (SQLException e) {
      throw new AFJException("SQL Error in populating the addVendorId List:" + e.getMessage());
    } catch (RockException e) {
      throw new AFJException("Unable to use the DefaulttagsFactory implementation:" + e.getMessage());
    } catch (Exception e) {
      throw new AFJException("Error in processing the actionContents:" + e.getMessage());
    }
    return addVendorIdList;
  }

  /**
   * Populates the hashmap used for delta processing: returnMap: This stores the mapping between tag name in xml and the
   * corresponding meas type in the eniq db for BSS e.g. EthernetInterface -> DC_E_STN:((2)):DC_E_STN_ETHNETINTF
   * 
   * @param techpackName
   * @throws AFJException
   */
  public Map<String, String> populateDefaultTagsCache(final String bssTechpackName, final String commonTechpackName,
      final RockFactory dwhrep) throws AFJException {
    final Map<String, String> returnMap = new HashMap<String, String>();
    try {
      final Defaulttags where = new Defaulttags(dwhrep);
      final DefaulttagsFactory factory = new DefaulttagsFactory(dwhrep, where);
      final Vector<Defaulttags> defaultTagsList = factory.get();
      String techpackName = null;
      String measName = null;
      if (defaultTagsList.isEmpty()) {
        throw new AFJException("There is no default tags found for BSS and Common");
      }
      for (Defaulttags dt : defaultTagsList) {
        if (dt.getDataformatid().startsWith(bssTechpackName) || dt.getDataformatid().startsWith(commonTechpackName)) {
          techpackName = dt.getDataformatid().substring(0, dt.getDataformatid().indexOf(DATAFORMAT_DELIMITER) + 1);
          measName = dt.getDataformatid().substring(0, dt.getDataformatid().lastIndexOf(DATAFORMAT_DELIMITER));
          returnMap.put(techpackName + dt.getTagid().toUpperCase(), measName);
        }
      }
    } catch (SQLException e) {
      throw new AFJException("SQL Error in populating the DefaultTags cache:" + e.getMessage());
    } catch (RockException e) {
      throw new AFJException("Unable to use the DefaulttagsFactory implementation:" + e.getMessage());
    }
    return returnMap;
  }

  /**
   * Populates the hashmap used for delta processing: returnMap: This stores the mapping between tag name in xml and the
   * corresponding meas type in the eniq db for STN. Generally a one to one mapping.
   * 
   * @param techpackName
   * @throws AFJException
   */
  public Map<String, String> populateDefaultTagsCache(final String techpackName, final RockFactory dwhrep)
      throws AFJException {
    final Map<String, String> returnMap = new HashMap<String, String>();
    try {
      final Defaulttags where = new Defaulttags(dwhrep);
      final DefaulttagsFactory factory = new DefaulttagsFactory(dwhrep, where);
      final Vector<Defaulttags> defaultTagsList = factory.get();
      if (defaultTagsList.isEmpty()) {
        throw new AFJException("There is no default tags found for " + techpackName);
      }

      for (Defaulttags dt : defaultTagsList) {
        if (dt.getDataformatid().startsWith(techpackName)) {
          returnMap.put(dt.getTagid().toUpperCase(),
              dt.getDataformatid().substring(0, dt.getDataformatid().lastIndexOf(DATAFORMAT_DELIMITER)));
        }
      }
    } catch (SQLException e) {
      throw new AFJException("SQL Error in populating the DefaultTags cache:" + e.getMessage());
    } catch (RockException e) {
      throw new AFJException("Unable to use the DefaulttagsFactory implementation:" + e.getMessage());
    }

    return returnMap;
  }

  /**
   * Method to populate the hashmap returnMap. Maps Measurement tag to number of counters associated with the tag.
   * 
   * @return
   * @throws AFJException
   */
  public Map<String, List<String>> populateMeasurementCountersMap(final Map<String, String> tagToMeasTypeMap,
      final RockFactory dwhrep) throws AFJException {
    final Map<String, List<String>> returnMap = new HashMap<String, List<String>>();
    List<String> counterList = null;
    String measName = null;

    for (String key : tagToMeasTypeMap.keySet()) {
      measName = tagToMeasTypeMap.get(key);
      counterList = populateMeasCountersMap(measName.toUpperCase(), dwhrep);
      returnMap.put(key, counterList);
    }

    return returnMap;
  }

  /**
   * Method returns the techpack defined in the meta_collection_sets table.
   * 
   * @param techpackName
   * @param versionid
   * @param etlrep
   * @return
   * @throws AFJException
   */
  public Meta_collection_sets getTechpack(final String techpackName, final String versionid, final RockFactory etlrep)
      throws AFJException {
    final String version = versionid.substring(versionid.lastIndexOf(":") + 1);
    final Meta_collection_sets mts = new Meta_collection_sets(etlrep);
    mts.setCollection_set_name(techpackName);
    mts.setVersion_number(version);
    try {
      final Meta_collection_setsFactory mtsF = new Meta_collection_setsFactory(etlrep, mts);
      final Vector<Meta_collection_sets> mcsList = mtsF.get();
      if (mcsList.isEmpty()) {
        throw new AFJException("The tp name:" + techpackName + " not found in the meta_collection_sets table");
      } else {
        return mcsList.get(0);
      }
    } catch (SQLException e) {
      throw new AFJException("SQL Exception:" + e.getMessage());
    } catch (RockException e) {
      throw new AFJException("RockException:" + e.getMessage());
    }
  }

  /**
   * Method returns the number of counters associated with a meas type.
   * 
   * @param typeId
   * @param dwhrep
   * @return
   * @throws AFJException
   */
  public int getNumberOfCounters(final String typeId, final RockFactory dwhrep) throws AFJException {
    int numberOfCounters = 0;
    final Measurementcounter measCounter = new Measurementcounter(dwhrep);
    measCounter.setTypeid(typeId);

    try {
      final MeasurementcounterFactory factory = new MeasurementcounterFactory(dwhrep, measCounter);
      final List<Measurementcounter> dbMeasCounterList = factory.get();
      numberOfCounters = dbMeasCounterList.size();
    } catch (SQLException e) {
      throw new AFJException("SQL Error in fetching rows from Measurementcounter:" + e.getMessage());
    } catch (RockException e) {
      throw new AFJException("Unable to use the MeasurementcounterFactory implementation:" + e.getMessage());
    }
    return numberOfCounters;
  }

  /**
   * Method returns the next available measurementcounter column number.
   * 
   * @param typeid
   * @param tableLevel
   * @param dwhrep
   * @return
   */
  public long getNextMeasurementCounterColumnNumber(final String typeid, final RockFactory dwhrep) throws AFJException {
    final Measurementcounter mc = new Measurementcounter(dwhrep);
    mc.setTypeid(typeid);
    try {
      final MeasurementcounterFactory fac = new MeasurementcounterFactory(dwhrep, mc, "ORDER BY COLNUMBER");
      final List<Measurementcounter> mcList = fac.get();
      if (mcList.isEmpty()) {
        return 101; // Assumption: keys take values till 100
      } else {
        long next = mcList.get(mcList.size() - 1).getColnumber();
        return ++next;
      }
    } catch (SQLException e) {
      throw new AFJException("SQL Error in fetching rows from Measurementcounter:" + e.getMessage());
    } catch (RockException e) {
      throw new AFJException("Unable to use the MeasurementcounterFactory implementation:" + e.getMessage());
    }
  }

  /**
   * Method to return the next available column number for counter addition.
   * 
   * @param dataFormatId
   * @return
   * @throws AFJException
   */
  public long getMeasurementColumnNumber(final String dataFormatId, final RockFactory dwhrep) throws AFJException {
    long returnValue = 0;
    final String mTableId = dataFormatId + DATAFORMAT_DELIMITER + "RAW";
    final Measurementcolumn mcWhere = new Measurementcolumn(dwhrep);
    mcWhere.setMtableid(mTableId);

    try {
      final MeasurementcolumnFactory mcFactory = new MeasurementcolumnFactory(dwhrep, mcWhere,
          "order by colnumber desc");
      final Measurementcolumn mcObject = mcFactory.getElementAt(0);
      if (mcObject == null) {
        throw new AFJException("No counters defined for the dataformatid:" + dataFormatId);
      }
      returnValue = mcObject.getColnumber() + 1;
    } catch (SQLException e) {
      throw new AFJException("SQLException in accessing MeasurmentCounter table:" + e.getMessage());
    } catch (RockException e) {
      throw new AFJException("RockException in accessing MeasurmentCounter table:" + e.getMessage());
    }

    return returnValue;
  }

  /**
   * New method to return the dwhtype object for a given meas name.
   * 
   * @param dwhrep
   * @param tpName
   * @param measName
   * @return
   * @throws AFJException
   */
  public Dwhtype getDwhType(final String measName, final RockFactory dwhrep) throws AFJException {
    final Dwhtype whereObject = new Dwhtype(dwhrep);
    final String storageId = measName + DATAFORMAT_DELIMITER + PropertiesUtility.getProperty(PROP_DEF_TABLELEVEL);
    whereObject.setStorageid(storageId);
    DwhtypeFactory dwhTypeFactory;
    try {
      dwhTypeFactory = new DwhtypeFactory(dwhrep, whereObject);

      final List<Dwhtype> dwhTypeList = dwhTypeFactory.get();
      if (dwhTypeList.isEmpty()) {
        throw new AFJException("No matching record in dwhtype for storageId:" + storageId);
      } else {
        return dwhTypeList.get(0);
      }
    } catch (SQLException e) {
      throw new AFJException("SQLException in getting values from dwhtype table for storageId:" + storageId);
    } catch (RockException e) {
      throw new AFJException("RockException in getting values from dwhtype table for storageId:" + storageId);
    }
  }

  /**
   * Get typeactivation for measurement type
   * 
   * @param mTypeName
   *          measurement type name
   * @param mTpName
   *          measurement type tp
   * @param dwhrep
   * @return typeactivation rock object
   * @throws AFJException
   */
  public List<Typeactivation> getTypeActivations(final String mTypeName, final String mTpName, final RockFactory dwhrep)
      throws AFJException {
    final Typeactivation whereObject = new Typeactivation(dwhrep);
    whereObject.setTypename(mTypeName);
    whereObject.setTechpack_name(mTpName);
    whereObject.setTablelevel(PropertiesUtility.getProperty(PROP_DEF_PARTITIONTYPE));

    try {
      final TypeactivationFactory typeActivationFactory = new TypeactivationFactory(dwhrep, whereObject);
      final List<Typeactivation> typeActivationList = typeActivationFactory.get();
      return typeActivationList;
    } catch (SQLException e) {
      throw new AFJException("SQLException in getting values from typeactivation table for measure:" + mTypeName);
    } catch (RockException e) {
      throw new AFJException("RockException in getting values from typeactivationtable for measure:" + mTypeName);
    }

  }

  /**
   * Get versioning by version id
   * 
   * @param versionId
   * @param dwhrep
   * @return versioning
   * @throws AFJException
   */
  public Versioning getVersioning(final String versionId, final RockFactory dwhrep) throws AFJException {
    final Versioning whereObject = new Versioning(dwhrep);
    whereObject.setVersionid(versionId);
    VersioningFactory versioningFactory;
    try {
      versioningFactory = new VersioningFactory(dwhrep, whereObject);
      final Vector<Versioning> versionings = versioningFactory.get();
      if (versionings.isEmpty()) {
        throw new AFJException(versionId + " not found in the versioning table.");
      }
      return versionings.firstElement();
    } catch (SQLException e) {
      throw new AFJException("SQLException in getting values from versioning table for id:" + versionId);
    } catch (RockException e) {
      throw new AFJException("RockException in getting values from versioning table for id:" + versionId);
    }
  }

  /**
   * Get measurement columns
   * 
   * @param mTableId
   * @param dwhrep
   * @return
   * @throws AFJException
   */
  // public List<Measurementcolumn> getMeasurementColumns(String mTableId, RockFactory dwhrep) throws AFJException {
  // final Measurementcolumn measColumnWhereObject = new Measurementcolumn(dwhrep);
  // measColumnWhereObject.setMtableid(mTableId);
  // MeasurementcolumnFactory measColumnFactory;
  // try {
  // measColumnFactory = new MeasurementcolumnFactory(dwhrep, measColumnWhereObject);
  // final List<Measurementcolumn> measColumnList = measColumnFactory.get();
  // return measColumnList;
  // } catch (SQLException e) {
  // throw new AFJException("SQLException in getting values from mcolumn table for mtableid:" + mTableId);
  // } catch (RockException e) {
  // throw new AFJException("RockException in getting values from mcolumn table for mtableid:" + mTableId);
  // }
  // }

  /**
   * Get measurement columns for STN or BSS. Checks the PUBLICKEY as well.
   * 
   * @param mTableId
   * @param dwhrep
   * @return
   * @throws AFJException
   */
  public List<Measurementcolumn> getMeasurementColumns(final String mTableId, final RockFactory dwhrep)
      throws AFJException {
    final Measurementcolumn measColumnWhereObject = new Measurementcolumn(dwhrep);
    measColumnWhereObject.setMtableid(mTableId);
    measColumnWhereObject.setColtype("PUBLICKEY");
    MeasurementcolumnFactory measColumnFactory;
    try {
      measColumnFactory = new MeasurementcolumnFactory(dwhrep, measColumnWhereObject);
      final List<Measurementcolumn> measColumnList = measColumnFactory.get();
      return measColumnList;
    } catch (SQLException e) {
      throw new AFJException("SQLException in getting values from mcolumn table for mtableid:" + mTableId);
    } catch (RockException e) {
      throw new AFJException("RockException in getting values from mcolumn table for mtableid:" + mTableId);
    }
  }

  /**
   * Get meta collections for TP
   * 
   * @param techPackId
   * @param setVersion
   * @param directoryCheckerString
   * @param etlrep
   * @return
   * @throws AFJException
   */
  public Vector<Meta_collections> getMeta_collections(final Long techPackId, final String setVersion,
      final String directoryCheckerString, final RockFactory etlrep) throws AFJException {
    final Meta_collections whereObject = new Meta_collections(etlrep);
    whereObject.setCollection_set_id(techPackId);
    whereObject.setVersion_number(setVersion);
    whereObject.setCollection_name(directoryCheckerString);

    Meta_collectionsFactory mcFactory;
    try {
      mcFactory = new Meta_collectionsFactory(etlrep, whereObject);
      @SuppressWarnings({ "unchecked" })
      final Vector<Meta_collections> mcList = mcFactory.get();
      if (mcList.isEmpty()) {
        throw new AFJException("No match found in meta_collection for directorycheckerstring:" + directoryCheckerString);
      }
      return mcList;
    } catch (SQLException e) {
      throw new AFJException("SQLException in getting values from meta_collections table for techPackId:" + techPackId);
    } catch (RockException e) {
      throw new AFJException("RockException in getting values from meta_collections table for techPackId:" + techPackId);
    }

  }

  /**
   * Generates reverse delta from FollowJohn data stored in database
   * 
   * @param techPackName
   * @param dwhrep
   * @return
   * @throws AFJException
   */
  public AFJDelta getAFJDelta(final String techPackName, final RockFactory dwhrep) throws AFJException {

    final Integer followJohn = Integer.valueOf(PropertiesUtility.getProperty(PROP_FOLLOWJOHN));

    final List<AFJMeasurementType> afjMeasurementTypes = new ArrayList<AFJMeasurementType>();

    final String techPackVersion = getActiveTechPackVersion(techPackName, dwhrep);

    try {
      final Measurementtype whereMeasurementtype = new Measurementtype(dwhrep);
      whereMeasurementtype.setVersionid(techPackVersion);
      final MeasurementtypeFactory measurementtypeFactory = new MeasurementtypeFactory(dwhrep, whereMeasurementtype,
          false);
      final Vector<Measurementtype> measurementtypes = measurementtypeFactory.get();
      for (Measurementtype measurementtype : measurementtypes) {

        final Measurementcounter whereMeasurementcounter = new Measurementcounter(dwhrep);
        whereMeasurementcounter.setTypeid(measurementtype.getTypeid());
        whereMeasurementcounter.setFollowjohn(followJohn);
        final MeasurementcounterFactory measurementcounterFactory = new MeasurementcounterFactory(dwhrep,
            whereMeasurementcounter, false);
        final Vector<Measurementcounter> measurementCounters = measurementcounterFactory.get();
        if (!measurementCounters.isEmpty()) {

          // inserted/modified MT has been found!!!
          final AFJMeasurementType afjMeasurementType = new AFJMeasurementType();
          afjMeasurementType.setTpName(techPackName);
          afjMeasurementType.setTpVersion(techPackVersion);
          afjMeasurementType.setTypeNew(followJohn.equals(measurementtype.getFollowjohn()));
          afjMeasurementType.setTypeName(measurementtype.getTypename());
          afjMeasurementType.setDescription(measurementtype.getDescription());
          afjMeasurementType.setRatio("");
          afjMeasurementType.setTags(new ArrayList<AFJMeasurementTag>());
          afjMeasurementTypes.add(afjMeasurementType);

          final Dataformat whereDataformat = new Dataformat(dwhrep);
          whereDataformat.setTypeid(measurementtype.getTypeid());
          final DataformatFactory dataformatFactory = new DataformatFactory(dwhrep, whereDataformat, false);

          final Vector<Dataformat> dataformats = dataformatFactory.get();

          for (Dataformat dataformat : dataformats) {
            final Defaulttags whereDefaulttags = new Defaulttags(dwhrep);
            whereDefaulttags.setDataformatid(dataformat.getDataformatid());
            final DefaulttagsFactory defaulttagsFactory = new DefaulttagsFactory(dwhrep, whereDefaulttags, false);

            final Vector<Defaulttags> defaulttags = defaulttagsFactory.get();
            for (Defaulttags defaulttag : defaulttags) {
              final AFJMeasurementTag afjMeasurementTag = new AFJMeasurementTag();
              afjMeasurementTag.setTagName(defaulttag.getTagid());
              afjMeasurementTag.setNewCounters(new ArrayList<AFJMeasurementCounter>());
              afjMeasurementTag.setDataformattype(dataformat.getDataformattype());
              afjMeasurementType.addTag(afjMeasurementTag);

              final Dataitem whereDataitem = new Dataitem(dwhrep);
              whereDataitem.setDataformatid(defaulttag.getDataformatid());
              final DataitemFactory dataitemFactory = new DataitemFactory(dwhrep, whereDataitem, false);
              final Vector<Dataitem> dataitems = dataitemFactory.get();

              for (Dataitem dataitem : dataitems) {
                final String counterName = dataitem.getDataname();
                for (Measurementcounter measurementCounter : measurementCounters) {
                  if (counterName.equals(measurementCounter.getDataname())) {
                    final AFJMeasurementCounter afjMeasurementCounter = new AFJMeasurementCounter();
                    afjMeasurementCounter.setCounterName(counterName);
                    // all AFJ counters are new
                    afjMeasurementCounter.setCounterNew(true);
                    afjMeasurementCounter.setSubType(measurementCounter.getTimeaggregation());
                    if (measurementCounter.getCounterprocess().equals("PEG")) {
                      afjMeasurementCounter.setType("CC");
                    } else {
                      afjMeasurementCounter.setType("GAUGE");
                    }
                    // there is no way to know original result type because they all are defined as numeric in database
                    afjMeasurementCounter.setResultType("numeric");
                    afjMeasurementCounter.setDescription(measurementCounter.getDescription());
                    afjMeasurementTag.addNewCounter(afjMeasurementCounter);
                  }
                }
              }
            }
          }
        }
      }
      final AFJDelta afjDelta = new AFJDelta();
      afjDelta.setMeasurementTypes(afjMeasurementTypes);
      afjDelta.setMomFileName(null);
      afjDelta.setTechPackName(techPackName);
      afjDelta.setTechPackVersion(techPackVersion);
      return afjDelta;
    } catch (Exception e) {
      throw new AFJException("Reverse delta generation failed. " + e.getMessage());
    }
  }

//  /**
//   * Remove measurement counters from Dwhcolumn table
//   * @param measurementTypes
//   * @param removeAll
//   * @param dwhrep
//   * @throws AFJException
//   */
//  public void removeDwhcolumn(final List<AFJMeasurementType> measurementTypes, final Boolean removeAll,
//      final RockFactory dwhrep) throws AFJException {
//    try {
//      for (AFJMeasurementType measurementType : measurementTypes) {
//        for (AFJMeasurementTag measurementTag : measurementType.getTags()) {
//          final String storageid = measurementType.getTypeName() + DATAFORMAT_DELIMITER
//              + PropertiesUtility.getProperty(PROP_DEF_PARTITIONTYPE);
//          final Dwhcolumn dc = new Dwhcolumn(dwhrep);
//          dc.setStorageid(storageid);
//          if (removeAll) {
//            dc.deleteDB(dc);
//            log.info("removed Dwhcolumn by storageid: " + dc.getStorageid());
//          } else {
//            for (AFJMeasurementCounter measurementCounter : measurementTag.getNewCounters()) {
//              dc.setDataname(measurementCounter.getCounterName());
//              dc.deleteDB(dc);
//              log.info("removed Dwhcolumn for counter " + dc.getDataname() + " by storageid: " + dc.getStorageid());
//            }
//          }
//        }
//      }
//    } catch (Exception e) {
//      throw new AFJException("Dwhcolumn remove failed. " + e.getMessage());
//    }
//  }
//
//  /**
//   * Remove measurement type from Dwhpartition table
//   * @param measurementTypes
//   * @param removeAll
//   * @param dwhrep
//   * @throws AFJException
//   */
//  public void removeDwhpartition(final List<AFJMeasurementType> measurementTypes, final Boolean removeAll, final RockFactory dwhrep)
//      throws AFJException {
//    try {
//      if (!removeAll) {
//        return;
//      }
//      for (AFJMeasurementType measurementType : measurementTypes) {
//        final String storageid = measurementType.getTypeName() + DATAFORMAT_DELIMITER
//            + PropertiesUtility.getProperty(PROP_DEF_PARTITIONTYPE);
//        final Dwhpartition dp = new Dwhpartition(dwhrep);
//        dp.setStorageid(storageid);
//        dp.deleteDB(dp);
//        log.info("removed Dwhpartition by storageid: " + dp.getStorageid());
//      }
//    } catch (Exception e) {
//      throw new AFJException("Dwhpartition remove failed. " + e.getMessage());
//    }
//  }

  /**
   * 
   * @param measurementTypes
   * @param removeAll
   * @param dwhrep
   * @throws AFJException
   */
  public void removeInterfaceMeasurement(final List<AFJMeasurementType> measurementTypes, final Boolean removeAll,
      final RockFactory dwhrep) throws AFJException {
    if (!removeAll) {
      return;
    }
    try {
      for (AFJMeasurementType measurementType : measurementTypes) {
        for (AFJMeasurementTag measurementTag : measurementType.getTags()) {
          final String measurementName = measurementType.getTpVersion() + DATAFORMAT_DELIMITER
              + measurementType.getTypeName();
          final String dataformatid = measurementName + DATAFORMAT_DELIMITER + measurementTag.getDataformattype();
          final Interfacemeasurement im = new Interfacemeasurement(dwhrep);
          im.setDataformatid(dataformatid);
          im.deleteDB(im);
          log.info("removed Interfacemeasurement by dataformatid: " + im.getDataformatid());
        }
      }
    } catch (Exception e) {
      throw new AFJException("Interfacemeasurement remove failed. " + e.getMessage());
    }
  }

  /**
   * 
   * @param measurementTypes
   * @param removeAll
   * @param dwhrep
   * @throws AFJException
   */
  public void removeDataItem(final List<AFJMeasurementType> measurementTypes, final Boolean removeAll,
      final RockFactory dwhrep) throws AFJException {
    try {
      for (AFJMeasurementType measurementType : measurementTypes) {
        for (AFJMeasurementTag measurementTag : measurementType.getTags()) {
          final String measurementName = measurementType.getTpVersion() + DATAFORMAT_DELIMITER
              + measurementType.getTypeName();
          final String dataformatid = measurementName + DATAFORMAT_DELIMITER + measurementTag.getDataformattype();
          final Dataitem di = new Dataitem(dwhrep);
          di.setDataformatid(dataformatid);
          if (removeAll) {
            di.deleteDB(di);
            log.info("removed Dataitem by dataformatid: " + di.getDataformatid());
          } else {
            for (AFJMeasurementCounter measurementCounter : measurementTag.getNewCounters()) {
              di.setDataname(measurementCounter.getCounterName());
              di.deleteDB(di);
              log.info("removed Dataitem for counter " + di.getDataname() + " by dataformatid: " + di.getDataformatid());
            }
          }
        }
      }
    } catch (Exception e) {
      throw new AFJException("Dataitem remove failed. " + e.getMessage());
    }
  }

  /**
   * 
   * @param measurementTypes
   * @param removeAll
   * @param dwhrep
   * @throws AFJException
   */
  public void removeDefaultTags(final List<AFJMeasurementType> measurementTypes, final Boolean removeAll,
      final RockFactory dwhrep) throws AFJException {
    if (!removeAll) {
      return;
    }
    try {
      for (AFJMeasurementType measurementType : measurementTypes) {
        for (AFJMeasurementTag measurementTag : measurementType.getTags()) {
          final String measurementName = measurementType.getTpVersion() + DATAFORMAT_DELIMITER
              + measurementType.getTypeName();
          final String dataformatid = measurementName + DATAFORMAT_DELIMITER + measurementTag.getDataformattype();
          final Defaulttags dt = new Defaulttags(dwhrep);
          dt.setDataformatid(dataformatid);
          dt.deleteDB(dt);
          log.info("removed Defaulttags by dataformatid: " + dt.getDataformatid());
        }
      }
    } catch (Exception e) {
      throw new AFJException("DefaultTags remove failed. " + e.getMessage());
    }
  }

  /**
   * 
   * @param measurementTypes
   * @param removeAll
   * @param dwhrep
   * @throws AFJException
   */
  public void removeDataFormat(final List<AFJMeasurementType> measurementTypes, final Boolean removeAll,
      final RockFactory dwhrep) throws AFJException {
    if (!removeAll) {
      return;
    }
    try {
      for (AFJMeasurementType measurementType : measurementTypes) {
        for (AFJMeasurementTag measurementTag : measurementType.getTags()) {
          final String measurementName = measurementType.getTpVersion() + DATAFORMAT_DELIMITER
              + measurementType.getTypeName();
          final String dataformatid = measurementName + DATAFORMAT_DELIMITER + measurementTag.getDataformattype();
          final Dataformat df = new Dataformat(dwhrep);
          df.setDataformatid(dataformatid);
          df.deleteDB(df);
          log.info("removed Dataformat by dataformatid: " + df.getDataformatid());
        }
      }
    } catch (Exception e) {
      throw new AFJException("DataFormat remove failed. " + e.getMessage());
    }
  }

  public void removeTransformation(final List<AFJMeasurementType> measurementTypes, final Boolean removeAll, 
      final RockFactory dwhrep) throws AFJException {
    if (!removeAll) {
      return;
    }
    try {
      for (AFJMeasurementType measurementType : measurementTypes) {
        for (AFJMeasurementTag measurementTag : measurementType.getTags()) {
          final String measurementName = measurementType.getTpVersion() + DATAFORMAT_DELIMITER
              + measurementType.getTypeName();
          final String transformerid = measurementName + DATAFORMAT_DELIMITER + measurementTag.getDataformattype();
          final Transformation t = new Transformation(dwhrep);
          t.setTransformerid(transformerid);
          t.deleteDB(t);
          log.info("removed Transformation by transformerid: " + t.getTransformerid());
        }
      }
    } catch (Exception e) {
      throw new AFJException("Transformation remove failed. " + e.getMessage());
    }
  }

  /**
   * 
   * @param measurementTypes
   * @param removeAll
   * @param dwhrep
   * @throws AFJException
   */
  public void removeTransformer(final List<AFJMeasurementType> measurementTypes, final Boolean removeAll,
      final RockFactory dwhrep) throws AFJException {
    if (!removeAll) {
      return;
    }
    try {
      for (AFJMeasurementType measurementType : measurementTypes) {
        for (AFJMeasurementTag measurementTag : measurementType.getTags()) {
          final String measurementName = measurementType.getTpVersion() + DATAFORMAT_DELIMITER
              + measurementType.getTypeName();
          final String transformerid = measurementName + DATAFORMAT_DELIMITER + measurementTag.getDataformattype();
          final Transformer t = new Transformer(dwhrep);
          t.setTransformerid(transformerid);
          t.deleteDB(t);
          log.info("removed Transformer by transformerid: " + t.getTransformerid());
        }
      }
    } catch (Exception e) {
      throw new AFJException("Transformer remove failed. " + e.getMessage());
    }
  }

  /**
   * 
   * @param measurementTypes
   * @param removeAll
   * @param dwhrep
   * @throws AFJException
   */
  public void removeMeasurementColumn(final List<AFJMeasurementType> measurementTypes, final Boolean removeAll,
      final RockFactory dwhrep) throws AFJException {
    try {
      for (AFJMeasurementType measurementType : measurementTypes) {
        for (AFJMeasurementTag measurementTag : measurementType.getTags()) {
          final String measurementName = measurementType.getTpVersion() + DATAFORMAT_DELIMITER
              + measurementType.getTypeName();
          final String measurementTableId = measurementName + DATAFORMAT_DELIMITER
              + PropertiesUtility.getProperty(PROP_DEF_PARTITIONTYPE);
          final Measurementcolumn mc = new Measurementcolumn(dwhrep);
          mc.setMtableid(measurementTableId);
          if (removeAll) {
            mc.deleteDB(mc);
            log.info("removed Measurementcolumn by measurementTableId: " + mc.getMtableid());
          } else {
            for (AFJMeasurementCounter measurementCounter : measurementTag.getNewCounters()) {
              mc.setDataname(measurementCounter.getCounterName());
              mc.deleteDB(mc);
              log.info("removed Measurementcolumn for counter " + mc.getDataname());
            }
          }
        }
      }
    } catch (Exception e) {
      throw new AFJException("MeasurementColumn remove failed. " + e.getMessage());
    }
  }

  /**
   * 
   * @param measurementTypes
   * @param removeAll
   * @param dwhrep
   * @throws AFJException
   */
  public void removeMeasurementTable(final List<AFJMeasurementType> measurementTypes, final Boolean removeAll,
      final RockFactory dwhrep) throws AFJException {
    if (!removeAll) {
      return;
    }
    try {
      for (AFJMeasurementType measurementType : measurementTypes) {
        final String measurementName = measurementType.getTpVersion() + DATAFORMAT_DELIMITER
            + measurementType.getTypeName();
        final String measurementTableId = measurementName + DATAFORMAT_DELIMITER
            + PropertiesUtility.getProperty(PROP_DEF_PARTITIONTYPE);
        final Measurementtable mt = new Measurementtable(dwhrep);
        mt.setMtableid(measurementTableId);
        mt.deleteDB(mt);
        log.info("removed Measurementtable by measurementTableId: " + mt.getMtableid());
      }
    } catch (Exception e) {
      throw new AFJException("MeasurementTable remove failed. " + e.getMessage());
    }
  }

  /**
   * 
   * @param measurementTypes
   * @param removeAll
   * @param dwhrep
   * @throws AFJException
   */
  public void removeMeasurementCounter(final List<AFJMeasurementType> measurementTypes, final Boolean removeAll,
      final RockFactory dwhrep) throws AFJException {
    try {
      for (AFJMeasurementType measurementType : measurementTypes) {
        for (AFJMeasurementTag measurementTag : measurementType.getTags()) {
          final String typeId = measurementType.getTpVersion() + DATAFORMAT_DELIMITER + measurementType.getTypeName();
          final Measurementcounter mc = new Measurementcounter(dwhrep);
          mc.setTypeid(typeId);
          if (removeAll) {
            mc.deleteDB(mc);
            log.info("removed Measurementcounter by typeId: " + mc.getTypeid());
          } else {
            for (AFJMeasurementCounter measurementCounter : measurementTag.getNewCounters()) {
              mc.setDataname(measurementCounter.getCounterName());
              mc.deleteDB(mc);
              log.info("removed Measurementcounter " + mc.getDataname());
            }
          }
        }
      }
    } catch (Exception e) {
      throw new AFJException("MeasurementCounter remove failed. " + e.getMessage());
    }
  }

  /**
   * 
   * @param measurementTypes
   * @param removeAll
   * @param dwhrep
   * @throws AFJException
   */
  public void removeMeasurementKey(final List<AFJMeasurementType> measurementTypes, final Boolean removeAll,
      final RockFactory dwhrep) throws AFJException {
    if (!removeAll) {
      return;
    }
    try {
      for (AFJMeasurementType measurementType : measurementTypes) {
        final String typeId = measurementType.getTpVersion() + DATAFORMAT_DELIMITER + measurementType.getTypeName();
        final Measurementkey mk = new Measurementkey(dwhrep);
        mk.setTypeid(typeId);
        mk.deleteDB(mk);
        log.info("removed Measurementkey by typeId: " + mk.getTypeid());
      }
    } catch (Exception e) {
      throw new AFJException("Measurementkey remove failed. " + e.getMessage());
    }
  }

  /**
   * 
   * @param measurementTypes
   * @param removeAll
   * @param dwhrep
   * @throws AFJException
   */
  public void removeMeasurementType(final List<AFJMeasurementType> measurementTypes, final Boolean removeAll,
      final RockFactory dwhrep) throws AFJException {
    if (!removeAll) {
      return;
    }
    try {
      for (AFJMeasurementType measurementType : measurementTypes) {
        final String typeId = measurementType.getTpVersion() + DATAFORMAT_DELIMITER + measurementType.getTypeName();
        final Measurementtype mt = new Measurementtype(dwhrep);
        mt.setTypeid(typeId);
        mt.deleteDB(mt);
        log.info("removed Measurementtype by typeId: " + mt.getTypeid());
      }
    } catch (Exception e) {
      throw new AFJException("Measurementtype remove failed. " + e.getMessage());
    }
  }

  /**
   * 
   * @param measurementTypes
   * @param removeAll
   * @param dwhrep
   * @throws AFJException
   */
  public void removeMeasurementTypeClass(final List<AFJMeasurementType> measurementTypes, final Boolean removeAll,
      final RockFactory dwhrep) throws AFJException {
    if (!removeAll) {
      return;
    }
    try {
      for (AFJMeasurementType measurementType : measurementTypes) {
        final String typeClassId = measurementType.getTpVersion() + DATAFORMAT_DELIMITER
            + measurementType.getTypeName();
        final Measurementtypeclass mtc = new Measurementtypeclass(dwhrep);
        mtc.setTypeclassid(typeClassId);
        mtc.deleteDB(mtc);
        log.info("removed Measurementtypeclass by typeClassId: " + mtc.getTypeclassid());
      }
    } catch (Exception e) {
      throw new AFJException("Measurementtypeclass remove failed. " + e.getMessage());
    }
  }
  
  /**
   * Method to return the maximum order number available for use in transformation table
 * @param tpVersion
 * @param dwhrep
 * @return
 * @throws AFJException
 */
public long getMaxOrderNumber(final String tpVersion, final RockFactory dwhrep) throws AFJException{
	  long maxOrderNo = -1l;
	  try{
		  final Transformer whereTransformer = new Transformer(dwhrep);
		  whereTransformer.setVersionid(tpVersion);
		  final TransformerFactory transformerFactory = new TransformerFactory(dwhrep, whereTransformer);
		  final List<Transformer> transformerList = transformerFactory.get();
		  if(transformerList.isEmpty()){
			  throw new AFJException("No records found in Transformer table for:"+tpVersion);
		  }
		  final Transformation whereTransformation = new Transformation(dwhrep);
		  TransformationFactory transformationFactory = null;
		  List<Transformation> transformationList = null; 
		  for(Transformer transformer:transformerList){
			  whereTransformation.setTransformerid(transformer.getTransformerid());
			  transformationFactory = new TransformationFactory(dwhrep, whereTransformation);
			  transformationList = transformationFactory.get();
			  if(!transformationList.isEmpty()){
				  for(Transformation transformation:transformationList){
					  if(transformation.getOrderno() > maxOrderNo){
						  maxOrderNo = transformation.getOrderno();
					  }
				  }
			  }
		  }
	  }catch (SQLException e) {
		  throw new AFJException("SQLException in getting values from Transformer-Transforamtion");
	  } catch (RockException e) {
		  throw new AFJException("RockException in getting values from Transformer-Transforamtion");
	  }

	  return maxOrderNo;
  }

}
