package com.ericsson.eniq.afj;

import static com.ericsson.eniq.afj.common.PropertyConstants.ENGINE_NORMAL;
import static com.ericsson.eniq.afj.common.PropertyConstants.ENGINE_NO_LOADS;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_AFJTECHPACKS;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.engine.main.EngineAdmin;
import com.ericsson.eniq.afj.common.AFJDelta;
import com.ericsson.eniq.afj.common.AFJMeasurementType;
import com.ericsson.eniq.afj.common.AFJTechPack;
import com.ericsson.eniq.afj.common.DatabaseAdmin;
import com.ericsson.eniq.afj.common.DatabaseLockAction;
import com.ericsson.eniq.afj.common.DatabaseState;
import com.ericsson.eniq.afj.common.EngineAdminFactory;
import com.ericsson.eniq.afj.common.FileArchiver;
import com.ericsson.eniq.afj.common.FileArchiverFactory;
import com.ericsson.eniq.afj.common.PropertiesUtility;
import com.ericsson.eniq.afj.common.PropertyConstants;
import com.ericsson.eniq.afj.common.RockDatabaseAdminFactory;
import com.ericsson.eniq.afj.common.RockDatabaseLockActionFactory;
import com.ericsson.eniq.afj.database.AFJDatabaseCommonUtils;
import com.ericsson.eniq.afj.database.AFJDatabaseCommonUtilsFactory;
import com.ericsson.eniq.afj.database.AFJDatabaseHandler;
import com.ericsson.eniq.afj.upgrade.TechPackRestorer;
import com.ericsson.eniq.afj.upgrade.TechPackRestorerFactory;
import com.ericsson.eniq.afj.upgrade.TechPackUpgrader;
import com.ericsson.eniq.afj.upgrade.TechPackUpgraderFactory;
import com.ericsson.eniq.afj.xml.AFJComparatorFactory;
import com.ericsson.eniq.afj.xml.AFJParser;
import com.ericsson.eniq.afj.xml.AFJParserFactory;
import com.ericsson.eniq.afj.xml.CompareInterface;
import com.ericsson.eniq.exception.AFJConfiguationException;
import com.ericsson.eniq.exception.AFJException;

/**
 * 
 * Implementation class. Performs the operations triggered from the AdminUI for AFJ.
 * 
 * @author esunbal
 * 
 * 
 */
public class AFJManagerImpl implements AFJManager {

  private static final String NO_LOADS = "NoLoads";

  private final Logger log = Logger.getLogger(this.getClass().getName());
  
  private final EngineAdmin engineAdmin;

  private final FileArchiver fileArchiver;

  private DatabaseLockAction lockAction;

  private DatabaseAdmin databaseAdmin;

  public AFJManagerImpl() throws AFJException {
    engineAdmin = EngineAdminFactory.getInstance();
    fileArchiver = FileArchiverFactory.getInstance();
  }

  /*
   * Method for junit to override default databaseAdmin
   */
  public void setDatabaseAdmin(final DatabaseAdmin databaseAdmin) {
    this.databaseAdmin = databaseAdmin;
  }

  /*
   * 
   * Populates the AFJTechPack object for displaying on the AdminUI when the user clicks on the AFJ Upgrade link.
   * 
   * @see com.ericsson.eniq.afj.AFJManager#getAFJTechPacks()
   * 
   * @return List<AFJTechPack>
   */
  public List<AFJTechPack> getAFJTechPacks() throws AFJException, AFJConfiguationException {
    final List<AFJTechPack> afjTechPacksResult = new ArrayList<AFJTechPack>();
    /*
     * 
     * Logic
     * 
     * 1.) Get the property afjBasePath
     * 
     * 2.) Browse through each of the sub directories defined for afj upgrade.
     * 
     * 3.) File filter on the xml files
     * 
     * 4.) Check the afj_status file under each sub-dir to get the afj history
     * 
     * 5.) Populate the AFJTechPack objects and return the list.
     */
    final String afjBaseDir = PropertiesUtility.getProperty(PropertyConstants.PROP_AFJ_BASE_DIR);
    if (afjBaseDir == null) {
      log.severe("Invalid " + PropertyConstants.PROP_AFJ_BASE_DIR + " property");
      throw new AFJException("Unable to fetch afjBaseDir property. Please check the config properties file.");
    }
    log.finest(PropertyConstants.PROP_AFJ_BASE_DIR + ": " + afjBaseDir);
    final File directories = new File(afjBaseDir);
    // Filter out dirs which are defined for afj upgrade.
    final String afjEnabledTechpacks = PropertiesUtility.getProperty(PROP_AFJTECHPACKS);
    final String[] afjEnabledTechpacksTokens = afjEnabledTechpacks.split(",");
    final List<String> afjEnabledTechpacksList = Arrays.asList(afjEnabledTechpacksTokens);
    final FileFilter dirFilter = new FileFilter() {

      @Override
      public boolean accept(final File file) {
        boolean returnValue = false;
        if (file.isDirectory() && afjEnabledTechpacksList.contains(file.getName())) {
          returnValue = true;
        }
        return returnValue;
      }
    };
    final File[] subDirs = directories.listFiles(dirFilter);
    if (subDirs != null && subDirs.length > 0) {
      String techPackName = null;
      String fileName = null;
      String schemaName = null;
      String contextPath = null;
      // String status = null;
      boolean checkMOMFile = false;
      AFJTechPack afjTechPack = null;
      for (int i = 0; i < subDirs.length; i++) {
        afjTechPack = new AFJTechPack();
        fileName = null;
        techPackName = null;
        checkMOMFile = false;
        techPackName = subDirs[i].getName();
        log.finest("Dir " + techPackName + " found.");
        afjTechPack.setTechPackName(techPackName);
        try {
          fileName = checkForMOMFile(techPackName);
          if (fileName == null) {
            fileName = "No valid xml file found.";
          } else {
            checkMOMFile = true;
          }
          log.finest("Dir " + techPackName + " has MOM file: " + fileName);
        } catch (AFJException ae) {
          log.finest("Dir " + techPackName + " check result: " + ae.getMessage());
          // Set the exception message to be passed to the AdminUI.
          afjTechPack.setMessage(ae.getMessage());
        }
        afjTechPack.setMomFilePresent(checkMOMFile);
        afjTechPack.setFileName(fileName);
        afjTechPack.setMaxCounters(new Integer(PropertiesUtility.getProperty(PropertyConstants.PROP_MAXCOUNTERS, "100")));
        afjTechPack.setMaxMeasTypes(new Integer(PropertiesUtility.getProperty(PropertyConstants.PROP_MAXMEASTYPES, "20")));
        schemaName = getSchemaName(techPackName);
        afjTechPack.setSchemaName(schemaName);
        contextPath = getContextPath(techPackName);
        afjTechPack.setContextPath(contextPath);
        afjTechPack.setNamespaceAware(isNamespaceAware(techPackName));
        afjTechPacksResult.add(afjTechPack);
      }
    } else {
      log.info("No valid directories for NVU files found on the server. Please check if the NVU techpack is installed.");
      throw new AFJConfiguationException(
          "No valid directories for NVU files found on the server. Please check if the NVU techpack is installed.");
    }
    return afjTechPacksResult;
  }

  
	  /**
	 * @param techpackName
	 * @return
	 */
  private String getContextPath(final String techpackName)throws AFJException{
	  if(techpackName == null){
		  return null;
	  }
	  else if(techpackName.equalsIgnoreCase(PropertiesUtility.getProperty(PropertyConstants.PROP_BSS_NAME)) || 
			  techpackName.equalsIgnoreCase(PropertiesUtility.getProperty(PropertyConstants.PROP_STN_NAME))){
		  return PropertiesUtility.getProperty(PropertyConstants.PROP_AFJ_SCHEMA_PACKAGE);
	  }
	  else{
		  return PropertiesUtility.getProperty(PropertyConstants.PROP_AFJ_SCHEMA_PACKAGE_IMS);
	  }
  }

	  /**
		 * @param techpackName
		 * @return
		 */
  private String getSchemaName(final String techpackName)throws AFJException{
	  if(techpackName == null){
		  return null;
	  }
	  else if(techpackName.equalsIgnoreCase(PropertiesUtility.getProperty(PropertyConstants.PROP_BSS_NAME)) || 
			  techpackName.equalsIgnoreCase(PropertiesUtility.getProperty(PropertyConstants.PROP_STN_NAME))){
		  return PropertiesUtility.getProperty(PropertyConstants.PROP_AFJ_SCHEMA_FILE);
	  }
	  else{
		  return PropertiesUtility.getProperty(PropertyConstants.PROP_AFJ_SCHEMA_FILE_IMS);
	  }
  }

	/**
	 * @param techpackName
	 * @return
	 * @throws AFJException
	 */
  private boolean isNamespaceAware(final String techpackName)throws AFJException{
	  if(techpackName == null){
		  return false;
	  }
	  else if(techpackName.equalsIgnoreCase(PropertiesUtility.getProperty(PropertyConstants.PROP_BSS_NAME)) || 
			  techpackName.equalsIgnoreCase(PropertiesUtility.getProperty(PropertyConstants.PROP_STN_NAME))){
		  return Boolean.parseBoolean(PropertiesUtility.getProperty(PropertyConstants.PROP_DEFAULT_NAMESPACEAWARE));
	  }
	  else	{	  
		  return Boolean.parseBoolean(PropertiesUtility.getProperty(PropertyConstants.PROP_IMS_NAMESPACEWARE));
	  }
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.afj.AFJManager#getAFJTechPack(java.lang.String)
   */
  @Override
  public AFJTechPack getAFJTechPack(final String techPackName) throws AFJException, AFJConfiguationException {
    final List<AFJTechPack> afjTechPackList = getAFJTechPacks();
    for (AFJTechPack afjTechPack : afjTechPackList) {
      if (afjTechPack.getTechPackName().equals(techPackName)) {
        return afjTechPack;
      }
    }
    return null;
  }

  /**
   * 
   * Checks if there are any xml files under the /eniq/data/pmdata/AFJ/<TP> path on the server and returns the name of
   * the file.
   * 
   * @param techPackName
   * 
   * @return
   */
  private String checkForMOMFile(final String techPackName) throws AFJException {
    String fileName = null;
    final FileFilter xmlFilter = new FileFilter() {

      @Override
      public boolean accept(final File file) {
        if (file.isFile() && file.canRead() && file.getName().endsWith(".xml")) {
          return true;
        } else {
          return false;
        }
      }
    };
    final String techPackDir = PropertiesUtility.getProperty(PropertyConstants.PROP_AFJ_BASE_DIR) + File.separator
        + techPackName;
    final File directory = new File(techPackDir);
    final File[] filesInDir = directory.listFiles(xmlFilter);
    log.finest("techPackDir:" + techPackDir);
    if (filesInDir == null || filesInDir.length == 0) {
      log.info("No xml file found. Please check the dir:" + techPackDir);
      fileName = null;
    } else if (filesInDir.length > 1) {
      log.severe("More than one xml file found. There should be only one xml file in the dir:" + techPackDir);
      throw new AFJException("ERROR:More than one xml file found. There should be only one xml file in the dir:"
          + techPackDir);
    } else {
      fileName = filesInDir[0].getName();
    }
    return fileName;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.afj.AFJManager#getAFJDeltaList(com.ericsson.eniq.common.AFJTechPack)
   */
  @Override
  public AFJDelta getAFJDelta(final AFJTechPack techPack) throws AFJException {
    /*
     * 
     * Get the techPack object. Should have the name of techpack and the file.
     * 
     * Location will always be /eniq/data/pmdata/AFJ/<tpname>
     * 
     * Check if the file exists.
     * 
     * Call the AFJParser to parse the file. Get the return values in PM object.
     */
    final AFJDelta deltaObject = new AFJDelta();
    try {        
      final AFJDatabaseCommonUtils commUtils = AFJDatabaseCommonUtilsFactory.getInstance();      
      final RockFactory dwhrep = AFJDatabaseHandler.getInstance().getDwhrep();
      final CompareInterface comparator = AFJComparatorFactory.getComparator(techPack.getTechPackName());
      final String techPackVersion = commUtils.getActiveTechPackVersion(techPack.getTechPackName(), dwhrep);
      
      deltaObject.setTechPackVersion(techPackVersion);
      log.info("NVU delta for " + techPack.getTechPackName() + " requested.");
      final AFJParser parser = AFJParserFactory.getParser(techPack);
      deltaObject.setTechPackName(techPack.getTechPackName());
      deltaObject.setMomFileName(techPack.getFileName());
      final Object pmObject = parser.parse(techPack);
      if (pmObject == null) {
        log.info("PM Object is null");
        return null; // Depicts there is no data.
      }
      final List<AFJMeasurementType> comparedObjects = comparator.getMeasTypeDelta(pmObject);
      deltaObject.setMeasurementTypes(comparedObjects);
      log.info("Returning " + comparedObjects.size() + " measurement types for " + techPack.getTechPackName());
    } catch (AFJException ae) {
      log.severe("Error in performing NVU delta.");
      throw new AFJException(ae.getMessage());
    } finally {
      AFJDatabaseHandler.getInstance().closeConnections();
      AFJDatabaseHandler.setInstance(null);
      AFJParserFactory.setParser(null);
    }
    return deltaObject;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.afj.AFJManager#upgradeAFJTechPack(com.ericsson.eniq.common.AFJDelta)
   */
  @Override
  public String upgradeAFJTechPack(final AFJDelta delta) throws AFJException {
    if (delta == null) {
      throw new AFJException("Invalid delta.");
    }
    log.info("Inside the upgradeAFJTechPack method. Calling the upgrade for:" + delta.getTechPackName());
    databaseAdmin = RockDatabaseAdminFactory.getDWHinstance();
    lockAction = RockDatabaseLockActionFactory.getDWHInstance();
    String returnValue = null;
    try {
      log.info("NVU Upgrade for " + delta.getTechPackName() + " requested.");
      if (upgradeIsAllowed()) {
        log.info("Putting the engine to NoLoads");
        boolean engineProfileChange = changeProfile(ENGINE_NO_LOADS);
        if (engineProfileChange) {
          log.info("engine profile changed to NoLoads successfully.");
          try {
            if (lockDWH()) {
              log.info("DWH users DCBO & DCPUBLIC locked.");
            } else {
              throw new AFJException("Failed to lock DWH users DCBO and DCPUBLIC.");
            }
            try {
              log.info("Calling the getUpgrader");
              final TechPackUpgrader upgrader = TechPackUpgraderFactory.getUpgrader(delta.getTechPackName());
              log.info("Calling the Upgrade");
              returnValue = upgrader.upgrade(delta);
              log.info("Calling the backup");
              backupMIMFile(delta.getTechPackName(), delta.getMomFileName());
              log.info("backupcompleted");
            } finally {
              if (unlockDWH()) {
                log.info("DWH users DCBO & DCPUBLIC unlocked.");
              } else {
                log.severe("Failed to unlock DWH users DCBO and DCPUBLIC.");
              }
            }
          } finally {
            engineProfileChange = changeProfile(ENGINE_NORMAL);
            log.info("engine profile changed to Normal successfully:" + engineProfileChange);
          }
        } else {
          throw new AFJException("Failed to change Engine profile to NoLoads.");
        }
      } else {
        throw new AFJException("Engine profile is NoLoads, operation can not continue.");
      }
    } catch (Exception e) {
      log.info("Error in performing NVU upgrade.");
      e.printStackTrace();
      throw new AFJException(e.getMessage());
    } finally {
      AFJDatabaseHandler.getInstance().closeConnections();
      AFJDatabaseHandler.setInstance(null);
    }
    return returnValue;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.afj.AFJManager#restoreAFJTechPack()
   */
  @Override
  public Boolean restoreAFJTechPack(final AFJTechPack techPack) throws AFJException {
    if (techPack == null) {
      throw new AFJException("Invalid TechPack - cannot proceed with the restore.");
    }
    log.info("NV Restore for " + techPack.getTechPackName() + " requested.");
    databaseAdmin = RockDatabaseAdminFactory.getDWHinstance();
    lockAction = RockDatabaseLockActionFactory.getDWHInstance();
    Boolean returnValue = false;
    try {
      if (upgradeIsAllowed()) {
        log.info("Putting the engine to NoLoads");
        boolean engineProfileChange = changeProfile(ENGINE_NO_LOADS);
        if (engineProfileChange) {
          log.info("engine profile changed to NoLoads successfully.");
          try {
            if (lockDWH()) {
              log.info("DWH users DCBO & DCPUBLIC locked.");
            } else {
              throw new AFJException("Failed to lock DWH users DCBO and DCPUBLIC.");
            }
            try {
              final TechPackRestorer restorer = TechPackRestorerFactory.getInstance(techPack);
              returnValue = restorer.restore();
            } finally {
              if (unlockDWH()) {
                log.info("DWH users DCBO & DCPUBLIC unlocked.");
              } else {
                log.severe("Failed to unlock DWH users DCBO and DCPUBLIC.");
              }
            }
          } finally {
            engineProfileChange = changeProfile(ENGINE_NORMAL);
            log.info("engine profile changed to Normal successfully:" + engineProfileChange);
          }
        } else {
          throw new AFJException("Failed to change Engine profile to NoLoads.");
        }
      } else {
        throw new AFJException("Engine profile is NoLoads, operation can not continue.");
      }
    } catch (Exception e) {
      log.info("Error in performing NVU restore.");
      throw new AFJException(e.getMessage());
    } finally {
      AFJDatabaseHandler.getInstance().closeConnections();
      AFJDatabaseHandler.setInstance(null);
    }
    return returnValue;
  }

  /**
   * 
   * Checks if prerequisites are OK
   * 
   * @return true if upgrade is possible
   */
  private boolean upgradeIsAllowed() {
    try {
      if (!engineAdmin.getCurrentProfile().equals(NO_LOADS)) {
        if (databaseAdmin.isState(DatabaseState.NORMAL)) {
          return true;
        }
      }
      return false;
    } catch (Exception e) {
      log.severe("Upgrade can not be started.");
      e.printStackTrace();
      return false;
    }
  }

  /**
   * 
   * Internal method for changing the engine profile.
   * 
   * @author edeamai
   * 
   * @param profile
   *          The profile to change to, e.g. Normal or No Loads
   */
  private boolean changeProfile(final String profile) {
    log.info("Going to change engine execution profile to: " + profile);
    try {
      if (!engineAdmin.changeProfile(profile)) {
        throw new AFJException(profile + " execution profile could not be set");
      }
      log.info("Engine execution profile has been changed to: " + profile);
      return true;
    } catch (Exception e) {
      log.severe("Could not put engine to " + profile + " execution profile.");
      e.printStackTrace();
      return false;
    }
  }

  /**
   * 
   * Internal method for locking DCBO and DCPUBLIC users out DWH
   * 
   * @author eheijun
   * 
   * @return true if lock was successfull
   */
  private boolean lockDWH() {
    log.info("Going to lock out DCBO and DCPUBLIC users from DWH.");
    try {
      if (lockAction.performLock()) {
        log.info("DCBO and DCPUBLIC users locked.");
        return true;
      }
    } catch (Exception e) {
      log.severe("Could not lock users from DWH.");
      e.printStackTrace();
    }
    return false;
  }

  /**
   * 
   * Internal method for unlocking DCBO and DCPUBLIC users out DWH
   * 
   * @author eheijun
   * 
   * @return true if unlock was successfull
   */
  private boolean unlockDWH() {
    log.info("Going to unlock locked out DCBO and DCPUBLIC.");
    try {
      if (lockAction.performUnlock()) {
        log.info("DCBO and DCPUBLIC users unlocked.");
        return true;
      }
    } catch (Exception e) {
      log.severe("Could not unlock users from DWH.");
      e.printStackTrace();
    }
    return false;
  }

  /**
   * Moves MIM file into archive folder
   * 
   * @param techPackName
   *          name of the Tech Pack (used for folder name)
   * @param momFileName
   *          name of the MIM file (used for file name)
   * @throws AFJException
   *           thrown if there is problems with AFJ property file
   */
  private void backupMIMFile(final String techPackName, final String momFileName) throws AFJException {
    final File momFile = new File(PropertiesUtility.getProperty(PropertyConstants.PROP_AFJ_BASE_DIR) + File.separator
        + techPackName + File.separator + momFileName);
    final File archiveDir = new File(PropertiesUtility.getProperty(PropertyConstants.PROP_AFJ_ARCHIVE_DIR)
        + File.separator + techPackName + File.separator + "archive" + File.separator);
    try {
      final File backupFile = fileArchiver.backupFile(momFile, archiveDir);
      if (backupFile != null) {
        log.info("File " + momFile.getName() + " successfully archived into " + archiveDir + " as "
            + backupFile.getName());
      }
    } catch (IOException e) {
      log.warning("File " + momFile.getName() + " failed to be archieved into " + archiveDir);
      e.printStackTrace();
    }
  }

   /**
   * For internal use.
   * @param args
   * @throws AFJException
   */
//   public static void main(String[] args) throws Exception{
//  
//   AFJManagerImpl afjManagerObject = null;
//   afjManagerObject = new AFJManagerImpl();
//  
//  
//   System.setProperty("CONF_DIR","C:/eniq/sw/conf");
////   List<AFJTechPack> result = new ArrayList<AFJTechPack>();
//   AFJDelta delta = null;
//   AFJTechPack techPack = new AFJTechPack();
//   techPack.setTechPackName("DC_E_IMS");
//   techPack.setFileName("cscf.xml");
//   techPack.setContextPath("com.ericsson.ims.mim");
//   techPack.setSchemaName("pm_mim_xsd.xsd");
//   techPack.setNamespaceAware(true);
//   try {
//   afjManagerObject.getAFJTechPacks();
//   delta = afjManagerObject.getAFJDelta(techPack);
//   // result = afjManagerObject.getAFJTechPacks();
//   // List<AFJMeasurementType> measTypeList = delta.getMeasurementTypes();
//   //
//   // for(AFJMeasurementType measType: measTypeList){
//   // System.out.println("MeasurementType:"+measType.getTypeName());
//   // List<AFJMeasurementCounter> counterList = measType.getNewCounters();
//   // for(AFJMeasurementCounter counter:counterList){
//   // System.out.println("  MeasurementCounter:"+counter.getCounterName());
//   // }
//   // }
//  
//   final long start = System.currentTimeMillis();
//  
//   afjManagerObject.upgradeAFJTechPack(delta);
//  
//   final long end = System.currentTimeMillis();
//  
//   System.out.println("Upgrade completed in " + (end - start) + " ms");
//  
//   } catch (AFJException e) {
//   // TODO Auto-generated catch block
//   System.out.println(e.getMessage());
//   e.printStackTrace();
//   }
//   catch (AFJConfiguationException e) {
//   // TODO Auto-generated catch block
//   System.out.println(e.getMessage());
//   e.printStackTrace();
//   }
//   // System.out.println("Size of list:"+result.size());
//   // AFJTechPack techpackObject = null;
//   // for(int i = 0; i < result.size(); i++){
//   // techpackObject = result.get(i);
//   // System.out.println("TPName="+techpackObject.getTechPackName());
//   //// System.out.println("Status="+techpackObject.getStatus());
//   // System.out.println("MOM Exists="+techpackObject.isMomFilePresent());
//   // System.out.println("Message="+techpackObject.getMessage());
//   // }
//   }

}
