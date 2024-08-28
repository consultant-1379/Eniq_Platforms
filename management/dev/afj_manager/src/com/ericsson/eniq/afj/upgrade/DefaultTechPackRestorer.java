/**
 * 
 */
package com.ericsson.eniq.afj.upgrade;

import static com.ericsson.eniq.afj.common.PropertyConstants.DATAFORMAT_DELIMITER;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROPERTY_UNDERSCORE;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_DEF_PARTITIONTYPE;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_ETLDATA_DIR;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_ETLDATA_JOINED_DIR;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_TEMPLATE_DIR;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_IQLOADER_DIR;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_REJECTED_DIR;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.dwhm.VersionUpdateAction;
import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.repository.dwhrep.Dwhtype;
import com.ericsson.eniq.afj.common.AFJDelta;
import com.ericsson.eniq.afj.common.AFJMeasurementType;
import com.ericsson.eniq.afj.common.AFJTechPack;
import com.ericsson.eniq.afj.common.CommonSetGenerator;
import com.ericsson.eniq.afj.common.CommonSetGeneratorFactory;
import com.ericsson.eniq.afj.common.PropertiesUtility;
import com.ericsson.eniq.afj.database.AFJDatabaseCommonUtils;
import com.ericsson.eniq.afj.database.AFJDatabaseCommonUtilsFactory;
import com.ericsson.eniq.afj.database.AFJDatabaseHandler;
import com.ericsson.eniq.dwhm.CreateViewsActionFactory;
import com.ericsson.eniq.dwhm.PartitionActionFactory;
import com.ericsson.eniq.dwhm.StorageTimeActionFactory;
import com.ericsson.eniq.dwhm.VersionUpdateActionFactory;
import com.ericsson.eniq.engine.EngineRestarterWrapper;
import com.ericsson.eniq.exception.AFJException;
import com.ericsson.eniq.repository.ActivationCacheWrapper;
//import com.ericsson.eniq.techpacksdk.CreateLoaderSetFactory;
import com.ericsson.eniq.techpacksdk.CreateTPDirCheckerSetFactory;
//import com.ericsson.eniq.techpacksdk.view.etlSetHandling.setWizards.CreateLoaderSet;
import com.ericsson.eniq.common.setWizards.CreateTPDirCheckerSet;

/**
 * @author eheijun
 * 
 */
public class DefaultTechPackRestorer implements TechPackRestorer {

  private final Logger log = Logger.getLogger(this.getClass().getName());

  private final AFJTechPack techPack;
  
  private final boolean isCalledByNVU = true;

  public DefaultTechPackRestorer(final AFJTechPack techPack) {
    this.techPack = techPack;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.afj.upgrade.TechPackRestorer#restore()
   */
  @Override
  public Boolean restore() throws AFJException {
    log.info("Restore for " + techPack.getTechPackName() + " starts.");
    final AFJDatabaseHandler databaseHandler = AFJDatabaseHandler.getInstance();
    try {
      final RockFactory dwhrep = databaseHandler.getDwhrep();
      final RockFactory etlrep = databaseHandler.getEtlrep();
      final RockFactory dwh = databaseHandler.getDwh();
      final RockFactory dbadwh = databaseHandler.getDbaDwh();
      final AFJDatabaseCommonUtils commonUtils = AFJDatabaseCommonUtilsFactory.getInstance();
      final AFJDelta reverseAFJDelta = commonUtils.getAFJDelta(techPack.getTechPackName(), dwhrep);

      if (reverseAFJDelta.getMeasurementTypes().isEmpty()) {
        log.info("There is no modified measurement types for Tech Pack " + reverseAFJDelta.getTechPackVersion());
        return true;
      }

      log.info("AFJDelta reversed from the database successfully for Tech Pack " + reverseAFJDelta.getTechPackVersion());
      final List<AFJMeasurementType> newMeasurementTypes = new ArrayList<AFJMeasurementType>();
      final List<AFJMeasurementType> oldMeasurementTypes = new ArrayList<AFJMeasurementType>();
      for (AFJMeasurementType measurementType : reverseAFJDelta.getMeasurementTypes()) {
        if (measurementType.isTypeNew()) {
          newMeasurementTypes.add(measurementType);
        } else {
          oldMeasurementTypes.add(measurementType);
        }
      }

      if (!oldMeasurementTypes.isEmpty()) {
        log.info("Tech Pack has " + oldMeasurementTypes.size() + " modified measurement types.");
        removeDwhrepEntries(oldMeasurementTypes, false, dwhrep);
        removeEtlrepEntries(oldMeasurementTypes, false, dwhrep, etlrep);
        removeDwhEntries(oldMeasurementTypes, false, dwhrep, etlrep, dwh, dbadwh);
        log.info("Restore for " + techPack.getTechPackName() + " counters finished.");
      }
      if (!newMeasurementTypes.isEmpty()) {
        log.info("Tech Pack has " + newMeasurementTypes.size() + " added measurement types.");
        removeEtlrepEntries(newMeasurementTypes, true, dwhrep, etlrep);
        removeDwhrepEntries(newMeasurementTypes, true, dwhrep);
        removeDwhEntries(newMeasurementTypes, true, dwhrep, etlrep, dwh, dbadwh);
        log.info("Restore for " + techPack.getTechPackName() + " measurementtypes finished.");
      }
      databaseHandler.commitTransaction(true);

      log.info("Restarting Engine");
      try {
        EngineRestarterWrapper.execute();
      } catch (Exception e) {
        throw new AFJException("Exception in restarting engine:" + e.getMessage());
      }
      log.info("Engine restart completed successfully");

      return true;
    } catch (Exception e) {
      databaseHandler.commitTransaction(false);
      log.severe("Restore for " + techPack.getTechPackName() + " failed.");
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Removes AFJ data from Etlrep
   * 
   * @param measurementTypes
   * @param removeAll
   * @param dwhrep
   * @param etlrep
   * @throws Exception
   */
  private void removeEtlrepEntries(final List<AFJMeasurementType> measurementTypes, final Boolean removeAll,
      final RockFactory dwhrep, final RockFactory etlrep) throws Exception {
    for (AFJMeasurementType measurementType : measurementTypes) {
      removeDirectoryCheckerSets(measurementType, removeAll, dwhrep, etlrep);
      removeDirectoryCheckerDirectories(measurementType, removeAll);
    }
    for (AFJMeasurementType measurementType : measurementTypes) {
      updateLoaderSet(measurementType, removeAll, dwhrep, etlrep);
    }
  }

  /**
   * Method to remove the dwhrep entries for removing the counter. The method deletes rows in the following tables:
   * <b>MeasurementColumn, MeasurementCounter, DataItem</b> If whole measurement type should be removed then clears mt
   * data from these tables <b>MeasurementColumn, MeasurementCounter, DataItem, Transformer, MeasurementTable,
   * MeasurementKeys MeasurementTypeClass, MeasurementType, DefaultTags, InterfaceMeasurement, DataFormat</b>
   */
  private void removeDwhrepEntries(final List<AFJMeasurementType> measurementTypes, final Boolean removeAll,
      final RockFactory dwhrep) throws AFJException {

    final AFJDatabaseCommonUtils commonUtils = AFJDatabaseCommonUtilsFactory.getInstance();

    commonUtils.removeInterfaceMeasurement(measurementTypes, removeAll, dwhrep);

    //commonUtils.removeDwhcolumn(measurementTypes, removeAll, dwhrep);
    //commonUtils.removeDwhpartition(measurementTypes, removeAll, dwhrep);

    commonUtils.removeDataItem(measurementTypes, removeAll, dwhrep);
    commonUtils.removeDefaultTags(measurementTypes, removeAll, dwhrep);
    commonUtils.removeDataFormat(measurementTypes, removeAll, dwhrep);

    commonUtils.removeTransformation(measurementTypes, removeAll, dwhrep);
    commonUtils.removeTransformer(measurementTypes, removeAll, dwhrep);

    commonUtils.removeMeasurementColumn(measurementTypes, removeAll, dwhrep);
    commonUtils.removeMeasurementTable(measurementTypes, removeAll, dwhrep);

    commonUtils.removeMeasurementCounter(measurementTypes, removeAll, dwhrep);
    commonUtils.removeMeasurementKey(measurementTypes, removeAll, dwhrep);
    commonUtils.removeMeasurementType(measurementTypes, removeAll, dwhrep);
    commonUtils.removeMeasurementTypeClass(measurementTypes, removeAll, dwhrep);

  }

  private void removeDwhEntries(final List<AFJMeasurementType> measurementTypes, final Boolean removeAll,
      final RockFactory dwhrep, final RockFactory etlrep, final RockFactory dwh, final RockFactory dbadwh)
      throws Exception {
    for (AFJMeasurementType measurementType : measurementTypes) {
      callVersionUpdateAction(measurementType.getTpName(), measurementType.getTypeName(), removeAll, dwhrep, etlrep,
          dwh, dbadwh);
    }
  }

  /**
   * Removes the directory checker sets from a given measurement type.
   * 
   * @param measurementType
   * @param etlrep
   * @param dwhrep
   * @throws Exception
   */
  private void removeDirectoryCheckerSets(final AFJMeasurementType measurementType, final Boolean removeAll,
      final RockFactory dwhrep, final RockFactory etlrep) throws Exception {

    if (!removeAll) {
      return;
    }

    final AFJDatabaseCommonUtils commonUtils = AFJDatabaseCommonUtilsFactory.getInstance();

    final Meta_collection_sets mcs = commonUtils.getTechpack(measurementType.getTpName(),
        measurementType.getTpVersion(), etlrep);
    final String setName = mcs.getCollection_set_name();
    final String setVersion = mcs.getVersion_number();
    final Long techPackId = mcs.getCollection_set_id();
    final String versionId = measurementType.getTpVersion();
    final String directoryCheckerString = "Directory_Checker_" + measurementType.getTpName();

    final Vector<Meta_collections> mcList = commonUtils.getMeta_collections(techPackId, setVersion,
        directoryCheckerString, etlrep);

    final long collectionId = mcList.firstElement().getCollection_id();

    final CreateTPDirCheckerSet cts = CreateTPDirCheckerSetFactory.getInstance(setName, setVersion, versionId, dwhrep,
        etlrep, techPackId, null);

    cts.removeForAFJ(measurementType.getTypeName().toLowerCase(), collectionId);

  }

  /**
   * Removes the directory checker directories from a given measurement type.
   * 
   * @param removeAll
   * @param tpName
   * @param typeName
   * @throws AFJException
   */
  private void removeDirectoryCheckerDirectories(final AFJMeasurementType measurementType, final Boolean removeAll)
      throws AFJException {

    if (!removeAll) {
      return;
    }

    final String tpName = measurementType.getTpName();
    final String typeName = measurementType.getTypeName();

    final String IQ_LOADER_DIR = PropertiesUtility.getProperty(PROP_IQLOADER_DIR) + File.separator
        + tpName.toUpperCase() + File.separator + typeName.toUpperCase() + PROPERTY_UNDERSCORE
        + PropertiesUtility.getProperty(PROP_DEF_PARTITIONTYPE);

    final String REJECTED_DIR = PropertiesUtility.getProperty(PROP_REJECTED_DIR) + File.separator
        + tpName.toUpperCase() + File.separator + typeName.toUpperCase() + PROPERTY_UNDERSCORE
        + PropertiesUtility.getProperty(PROP_DEF_PARTITIONTYPE);

    final String ETLDATA_DIR_BASE = PropertiesUtility.getProperty(PROP_ETLDATA_DIR);
    final String ETLDATA_DIR = ETLDATA_DIR_BASE + File.separator + typeName.toLowerCase();

    final String ETLDATA_DIR_RAW = ETLDATA_DIR_BASE + File.separator + typeName.toLowerCase() + File.separator
        + PropertiesUtility.getProperty(PROP_DEF_PARTITIONTYPE).toLowerCase();

    final String ETLDATA_DIR_JOINED = ETLDATA_DIR_BASE + File.separator + typeName.toLowerCase() + File.separator
        + PropertiesUtility.getProperty(PROP_ETLDATA_JOINED_DIR).toLowerCase();

    final List<String> directoriesList = new ArrayList<String>();
    directoriesList.add(IQ_LOADER_DIR);
    directoriesList.add(REJECTED_DIR);
    directoriesList.add(ETLDATA_DIR);
    directoriesList.add(ETLDATA_DIR_RAW);
    directoriesList.add(ETLDATA_DIR_JOINED);

    for (String directoryName : directoriesList) {
      final File rmDirectory = new File(directoryName);
      if (rmDirectory.exists()) {
        final File[] files = rmDirectory.listFiles();
        for(int i = 0; i < files.length; i++) {
          final File tmpFile = files[i];
          if (tmpFile.delete()) {
            log.info("File:" + tmpFile + " removed in directory " + rmDirectory + ".");
          } else {
            log.warning("Unable to remove the file:" + tmpFile);
          }
        }
        if (rmDirectory.delete()) {
          log.info("Directory:" + rmDirectory + " removed.");
        } else {
          log.warning("Unable to remove the directory:" + directoryName);
        }
      } else {
        log.info("Directory:" + rmDirectory + " not exists. Not removing it.");
      }
    }
  }

  private void updateLoaderSet(final AFJMeasurementType measurementType, final Boolean removeAll,
      final RockFactory dwhrep, final RockFactory etlrep) throws Exception {

    final String templateDir = PropertiesUtility.getProperty(PROP_TEMPLATE_DIR);

    final String typeId = measurementType.getTpVersion() + DATAFORMAT_DELIMITER + measurementType.getTypeName();

    final AFJDatabaseCommonUtils commonUtils = AFJDatabaseCommonUtilsFactory.getInstance();

    final Meta_collection_sets mcs = commonUtils.getTechpack(measurementType.getTpName(),
        measurementType.getTpVersion(), etlrep);
    final String setName = mcs.getCollection_set_name();
    final String setVersion = mcs.getVersion_number();
    final Long techPackId = mcs.getCollection_set_id();
    // final String techPackName = mcs.getCollection_set_name();
    // final String versionId = measurementType.getTpVersion();
    // final CreateLoaderSet cls = CreateLoaderSetFactory.getInstance(templateDir, setName, setVersion, versionId,
    // dwhrep,
    // etlrep, techPackId, techPackName, true);

    final CommonSetGenerator csg = CommonSetGeneratorFactory.getInstance(templateDir, setName, setVersion, dwhrep,
        etlrep, techPackId.intValue(), setName, true);

    if (removeAll) {
      csg.removeLoaderAction(typeId);
    } else {
      csg.updateLoaderAction(typeId);
    }

  }

  /**
   * Method to call the VersionUpdateAction and StorageTimeAction logic of the dwhmanager.
   * 
   * @param tpName
   * @param measName
   * @param dwhrep
   * @param etlrep
   * @param dwh
   * @param dbadwh
   * @throws AFJException
   */
  private void callVersionUpdateAction(final String tpName, final String measName, final boolean removeAll,
      final RockFactory dwhrep, final RockFactory etlrep, final RockFactory dwh, final RockFactory dbadwh)
      throws AFJException {

    final AFJDatabaseCommonUtils commonUtils = AFJDatabaseCommonUtilsFactory.getInstance();

    try {

      // Revalidate the ActivationCache to get the uncommitted insert values of new meas types into the cache.
      if (removeAll) {
        log.info("Revalidate ActivationCache");
        ActivationCacheWrapper.initialize(dwhrep, true);
        log.info("Revalidated ActivationCache");
      }

//      final Logger log = Logger.getLogger(this.getClass().toString());
//      log.info("log value:" + log.getName());

      log.info("Calling version update action");
      final VersionUpdateAction vua = VersionUpdateActionFactory.getInstance(dwhrep, dwh, tpName, log);
      vua.execute(measName);
      log.info("Calling version update action finished.");

      // Call the PartitionAction for new counters in existing meas types.
      if (!removeAll) {
        log.info("Calling CreateViewsAction");
        final Dwhtype dwhType = commonUtils.getDwhType(measName, dwhrep);
        CreateViewsActionFactory.getInstance(dbadwh, dwh, dwhrep, dwhType, log);
        log.info("Calling CreateViewsAction finished.");
      }

      // Creates the actual raw tables and partitions.
      // Note: There is an exception User 'dwhrep' has the row in 'DWHType' locked
      // in com.distocraft.dc5000.dwhm.SanityChecker.sanityCheck(SanityChecker.java:280)
      // The exception occurs due to a new connection created in dwhmanager and we have the table locked here.
      // Valid scenario,doesn't affect the upgrade - can't refactor dwhmanager code.
      // The exception doesn't stop the flow of code. Also we are anyways refreshing the PhysicalTable cache at the end
      // of upgrade.

      // Call the StorageTimeAction for new meas types.
      if (removeAll) {
        log.info("Calling storage time action");
        StorageTimeActionFactory.getInstance(dwhrep, etlrep, dwh, dbadwh, tpName, log, measName,isCalledByNVU);
        log.info("Calling storage time action finished.");
        log.info("Calling partition action");
        PartitionActionFactory.getInstance(dwhrep, dwh, tpName, log,isCalledByNVU);
        log.info("Calling partition action finished. Warnings it gave can be ignored.");
      }

    } catch (Exception e) {
      e.printStackTrace();
      throw new AFJException("Error in versionupdateaction. Message:" + e.getMessage() + " Exception:" + e.toString());
    }
  }
}
