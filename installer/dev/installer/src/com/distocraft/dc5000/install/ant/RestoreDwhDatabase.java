/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.distocraft.dc5000.install.ant;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import org.apache.tools.ant.BuildException;

import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.engine.main.ITransferEngineRMI;
import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.etl.rock.Meta_collection_setsFactory;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_collectionsFactory;
import com.distocraft.dc5000.etl.rock.Meta_databases;
import com.distocraft.dc5000.etl.rock.Meta_databasesFactory;
import com.distocraft.dc5000.repository.dwhrep.Techpackdependency;
import com.distocraft.dc5000.repository.dwhrep.TechpackdependencyFactory;
import com.distocraft.dc5000.repository.dwhrep.Tpactivation;
import com.distocraft.dc5000.repository.dwhrep.TpactivationFactory;

/**
 * This is custom made ANT task that restores the DWHDB database using the
 * information from repdb database
 * 
 * @author epaujor
 * 
 */
public class RestoreDwhDatabase extends CommonTask {

  private static final String AND_COLLECTION_NAME_LIKE_DWHM_INSTALL = "AND COLLECTION_NAME like 'DWHM_Install_%'";

  private static final String AND_COLLECTION_NAME_LIKE_DIRECTORY_CHECKER = "AND COLLECTION_NAME like 'Directory_Checker_%'";
 
  private static final String ENABLED_FLAG = "Y";

  private static final String USER = "USER";

  private static final String DWHREP = "dwhrep";

  private static final String DWH = "dwh";

  private transient RockFactory etlrepRockFactory = null;

  private transient RockFactory dwhrepRockFactory = null;

  private transient RockFactory dwhdbRockFactory = null;

  private String binDirectory = new String();

  private String installDirectory = new String();

  private List<String> activeTechpackNames;

  /**
   * This function restores the DWHDB database.
   */
  @Override
  public void execute() throws BuildException {

    final ITransferEngineRMI engineRMI = connectEngine();
    if (!binDirectory.endsWith(File.separator)) {
      // Add the missing separator char "/" from the end of the directory
      // string.
      binDirectory = this.binDirectory + File.separator;
    }

    if (!installDirectory.endsWith(File.separator)) {
      // Add the missing separator char "/" from the end of the directory
      // string.
      installDirectory = this.installDirectory + File.separator;
    }

    createDatabaseConnections();

    activeTechpackNames = getActiveTechpacks();

    System.out.println("\nRunning Directory checker sets for techpacks...");
    runDirCheckerSetsForTechpacks(engineRMI);
    System.out
        .println("Directory checker sets for techpacks are currently running. Please check AdminUI to see if they ran successfully.");

    System.out.println("\nChecking if tables already exist in dwhdb database for user 'dc'...");
    checkIfTablesAlreadyExist();
    System.out.println("No tables exist in dwhdb database for user 'dc'.");

    System.out.println("\nClearing DWHPartition, ExternalStatementStatus and CountingManagement tables in dwhrep...");
    truncateDwhrepTable("truncate table DWHPartition");
    truncateDwhrepTable("truncate table ExternalStatementStatus");
    truncateDwhrepTable("truncate table CountingManagement");
    System.out
        .println("Finished clearing DWHPartition, ExternalStatementStatus and CountingManagement tables in dwhrep.");

    System.out.println("\nRunning the DWHM_Install sets...");
    runDWHMInstallSets(engineRMI);
    System.out.println("Finished running the DWHM_Install sets.");

    System.out.println("\n Activating interfaces...");
    reactivateInterfaces();

    System.out.println("\nRunning the restore sets...");
    runRestoreSets(engineRMI);
    System.out.println("Restore sets are currently running. Please check AdminUI to see if they ran successfully.");
  }

  /**
   *
   */
  private void reactivateInterfaces() {

    int indexHypen;
    String interfaceAbsName = null;
    String ossName = null;
    final StringBuilder activateInterfaceCommand = new StringBuilder();

    System.out.println("ActiveInterfaceNames are as below");

    try {
      final Vector<Meta_collection_sets> activeInterfaceNames = getActiveInterfaces();

      if (activeInterfaceNames.size() > 0) {
        System.out.println("ActiveInterfaceNames are as below\n");

        for (Meta_collection_sets interfaceName : activeInterfaceNames) {
          System.out.println(interfaceName.getCollection_set_name());
        }

        for (Meta_collection_sets interfaceName : activeInterfaceNames) {

          final String interfaceNameWithOssName = interfaceName.getCollection_set_name();
          if(interfaceNameWithOssName.indexOf("-")>=0 && interfaceNameWithOssName.toString().contains("INTF_DC_E_IMS_PGM_AP-WUIGM"))
        	{
        	  interfaceAbsName="INTF_DC_E_IMS_PGM_AP-WUIGM";
        	  ossName = interfaceNameWithOssName.substring(interfaceAbsName.length()+1);
        	}else{
        		indexHypen = interfaceNameWithOssName.indexOf("-");
        		if (indexHypen > 0) {
        			interfaceAbsName = interfaceNameWithOssName.substring(0, indexHypen);
        			ossName = interfaceNameWithOssName.substring(indexHypen + 1);
        		}
        	}
            System.out.println("\nAttempting to activate interface " + interfaceAbsName + " for OSS " + ossName);

            activateInterfaceCommand.append(installDirectory);
            activateInterfaceCommand.append("activate_interface -o ");
            activateInterfaceCommand.append(ossName);
            activateInterfaceCommand.append(" -i ");
            activateInterfaceCommand.append(interfaceAbsName);

            System.out.println(runCommand(activateInterfaceCommand.toString()));
            System.out.println(activateInterfaceCommand.toString() + "ran successfully.");

            activateInterfaceCommand.delete(0, activateInterfaceCommand.length());

          }
        }else {
            System.out.println("No ActiveInterfaceNames found. Exiting");
        }

      } catch (SQLException e) {
      e.printStackTrace();
    } catch (RockException e) {
      e.printStackTrace();
    }

  }

  /**
   * @return
   * @throws RockException
   * @throws SQLException
   */
  private Vector<Meta_collection_sets> getActiveInterfaces() throws SQLException, RockException {

    final Meta_collection_sets whereMetaCollSet = new Meta_collection_sets(this.etlrepRockFactory);
    whereMetaCollSet.setType("Interface");
    whereMetaCollSet.setEnabled_flag(ENABLED_FLAG);

    final Meta_collection_setsFactory metaCollSetsFactory = new Meta_collection_setsFactory(this.etlrepRockFactory,
        whereMetaCollSet);

    return metaCollSetsFactory.get();
  }

  /**
   * Runs the Directory_Checker sets for all the active techpacks
   * 
   * @throws BuildException
   */
  private void runDirCheckerSetsForTechpacks(final ITransferEngineRMI engineRMI) {
    for (String techpackName : activeTechpackNames) {
      if (techpackName.contains("DWH_BASE")) {
        continue; // DWH_BASE does not have a Directory Checker set.
      }

      try {
        final List<Meta_collection_sets> metaCollSetForTps = getActiveMetaCollectionSetsForTp(techpackName);

        if (metaCollSetForTps.size() > 0) {
          final List<Meta_collections> dirCheckerCollections = getActiveCollectionForWhereClause(metaCollSetForTps,
              AND_COLLECTION_NAME_LIKE_DIRECTORY_CHECKER);
          if (dirCheckerCollections.size() > 0) {
            for (Meta_collections dirCheckerCol : dirCheckerCollections) {
              final String directoryCheckerName = dirCheckerCol.getCollection_name();
              System.out.println("\nAttempting to run " + directoryCheckerName + " ...");
              startAndWaitSet(engineRMI, techpackName, directoryCheckerName);
              System.out.println(directoryCheckerName + " ran successfully.");
            }
          }
        }
      } catch (Exception e) {
        System.out.println("Exception seen while running directory checker set(s) for techpack: '" + techpackName
            + "'.");
      }
    }
  }
  
  /**
   * Runs the DWHM_Install sets for all the active techpacks
   * 
   * @throws BuildException
   */
  private void runDWHMInstallSets(final ITransferEngineRMI engineRMI) throws BuildException {
    for (String techpackName : activeTechpackNames) {
      try {
        final List<Meta_collection_sets> metaCollSetForTps = getActiveMetaCollectionSetsForTp(techpackName);

        if (metaCollSetForTps.size() > 0) {
          final List<Meta_collections> dwhmInstallCollections = getActiveCollectionForWhereClause(metaCollSetForTps,
              AND_COLLECTION_NAME_LIKE_DWHM_INSTALL);
          if (dwhmInstallCollections.size() > 0) {
            for (Meta_collections dwhmInstallCol : dwhmInstallCollections) {
              final String dwhmInstallName = dwhmInstallCol.getCollection_name();
              System.out.println("\nAttempting to run " + dwhmInstallName + " ...");
              startAndWaitSet(engineRMI, techpackName, dwhmInstallName);
              System.out.println(dwhmInstallName + " ran successfully.");
            }
          }
        }
      } catch (Exception e) {
        System.out.println("Exception seen while running DWHM_Install set(s) for techpack: '" + techpackName + "'.");
      }
    }
  }


  /**
   * @param metaCollSetForTps
   * @return
   * @throws SQLException
   * @throws RockException
   */
  protected List<Meta_collections> getActiveCollectionForWhereClause(final List<Meta_collection_sets> metaCollSetForTps,
      final String whereClause) throws SQLException, RockException {
    final Meta_collection_sets metaCollSetForTp = metaCollSetForTps.get(0);

    final Meta_collections whereColl = new Meta_collections(etlrepRockFactory);
    whereColl.setCollection_set_id(metaCollSetForTp.getCollection_set_id());
    whereColl.setVersion_number(metaCollSetForTp.getVersion_number());
    whereColl.setEnabled_flag(ENABLED_FLAG);

    final Meta_collectionsFactory metCollFactory = new Meta_collectionsFactory(etlrepRockFactory, whereColl,
        whereClause);
    return metCollFactory.get();
  }

  /**
   * @param techpackName
   * @return
   * @throws SQLException
   * @throws RockException
   */
  private List<Meta_collection_sets> getActiveMetaCollectionSetsForTp(final String techpackName) throws SQLException,
      RockException {
    final Meta_collection_sets whereMetaCollSet = new Meta_collection_sets(etlrepRockFactory);
    whereMetaCollSet.setCollection_set_name(techpackName);
    whereMetaCollSet.setEnabled_flag(ENABLED_FLAG);
    final Meta_collection_setsFactory metaCollSetFactory = new Meta_collection_setsFactory(etlrepRockFactory,
        whereMetaCollSet);
    return metaCollSetFactory.get();
  }

  /**
   * Create database connections to ETLREP, DWHREP and DWHDB
   */
  private void createDatabaseConnections() {
    System.out.println("Checking connection to etlrep database...");
    final Map<String, String> databaseConnectionDetails = getDatabaseConnectionDetails();
    // Create the connection to the etlrep.
    etlrepRockFactory = createEtlrepRockFactory(databaseConnectionDetails);
    System.out.println("Connection to etlrep database created.");

    System.out.println("\nChecking connection to dwhrep database...");
    // Create also the connection to dwhrep.
    dwhrepRockFactory = createDwhrepRockFactory(DWHREP);
    System.out.println("Connection to dwhrep database created.");

    System.out.println("\nChecking connection to dwhdb database...");
    // Create also the connection to dwhrep.
    dwhdbRockFactory = createDwhrepRockFactory(DWH);
    System.out.println("Connection to dwhdb database created.");
  }

  /**
   * This function creates the rockfactory object to etlrep from the database
   * connection details read from ETLCServer.properties file.
   * 
   * @param databaseConnectionDetails
   * @return Returns the created RockFactory.
   */
  private RockFactory createEtlrepRockFactory(final Map<String, String> databaseConnectionDetails)
      throws BuildException {

    RockFactory rockFactory = null;
    final String databaseUsername = databaseConnectionDetails.get("etlrepDatabaseUsername").toString();
    final String databasePassword = databaseConnectionDetails.get("etlrepDatabasePassword").toString();
    final String databaseUrl = databaseConnectionDetails.get("etlrepDatabaseUrl").toString();
    final String databaseDriver = databaseConnectionDetails.get("etlrepDatabaseDriver").toString();

    try {
      rockFactory = new RockFactory(databaseUrl, databaseUsername, databasePassword, databaseDriver, "PreinstallCheck",
          true);
    } catch (Exception e) {
      e.printStackTrace();
      throw new BuildException("Unable to initialize database connection.", e);
    } finally {
      if (rockFactory == null) {
        throw new BuildException(
            "Unable to initialize database connection. Please check the settings in the ETLCServer.properties file.");
      }
    }
    return rockFactory;
  }

  /**
   * This function creates the RockFactory to dwhrep. The created RockFactory is
   * inserted in class variable dwhrepRockFactory.
   */
  private RockFactory createDwhrepRockFactory(final String connectionName) throws BuildException {
    RockFactory rockFactory = null;
    try {
      final Meta_databases whereMetaDatabases = new Meta_databases(this.etlrepRockFactory);
      whereMetaDatabases.setConnection_name(connectionName);
      whereMetaDatabases.setType_name(USER);
      final Meta_databasesFactory metaDatabasesFactory = new Meta_databasesFactory(this.etlrepRockFactory,
          whereMetaDatabases);
      final Vector<Meta_databases> metaDatabases = metaDatabasesFactory.get();

      if (metaDatabases != null && metaDatabases.size() == 1) {
        final Meta_databases targetMetaDatabase = metaDatabases.get(0);
        rockFactory = new RockFactory(targetMetaDatabase.getConnection_string(), targetMetaDatabase.getUsername(),
            targetMetaDatabase.getPassword(), etlrepRockFactory.getDriverName(), "PreinstallCheck", true);
      } else {
        throw new BuildException("Unable to connect metadata (No dwhrep or multiple dwhreps defined in Meta_databases)");
      }
    } catch (Exception e) {
      throw new BuildException("Creating database connection to dwhrep failed.", e);
    } finally {
      if (rockFactory == null) {
        throw new BuildException("Unable to initialize database connection.");
      }
    }

    return rockFactory;
  }

  /**
   * Checks if any tables already exist for the user dc. If they do, an
   * exception is thrown.
   * 
   * @throws BuildException
   */
  private void checkIfTablesAlreadyExist() throws BuildException {
    ResultSet resultSet = null;
    Statement statement = null;
    try {
      statement = dwhdbRockFactory.getConnection().createStatement();
      final String sqlQuery = "select tab.table_name from systable tab, sysuser suser where "
          + "table_type='BASE' and tab.creator = suser.user_id and suser.user_name = 'dc'";

      resultSet = statement.executeQuery(sqlQuery);

      if (resultSet.next()) {
        if (resultSet.getRow() != 0) {
          throw new BuildException("Tables already exist in the dwhdb database for user 'dc'.");
        }
      }
    } catch (SQLException e) {
      throw new BuildException("Failed to check if tables already exist in dwhdb database for user 'dc'.", e);
    } finally {
      try {
        if (resultSet != null) {
          resultSet.close();
        }
        if (statement != null) {
          statement.close();
        }
      } catch (SQLException e) {
        System.out.println("Issue closing statement/resultSet.");
      }
    }
  }

  private void truncateDwhrepTable(final String sql) throws BuildException {
    Statement statement = null;
    try {
      statement = dwhrepRockFactory.getConnection().createStatement();
      statement.executeUpdate(sql);
    } catch (SQLException e) {
      throw new BuildException("Failed to truncate the table", e);
    } finally {
      try {
        if (statement != null) {
          statement.close();
        }
      } catch (SQLException e) {
        System.out.println("Issue closing statement.");
      }
    }
  }

  /**
   * Get all the active techpacks
   * 
   * @return
   */
  private List<String> getActiveTechpacks() {
    final List<String> orderedActiveTpNames = new ArrayList<String>();
    try {
      final Tpactivation whereTpactivation = new Tpactivation(dwhrepRockFactory);
      whereTpactivation.setStatus("ACTIVE");
      final TpactivationFactory tpactivationFactory = new TpactivationFactory(dwhrepRockFactory, whereTpactivation);
      final Vector<Tpactivation> activeTps = tpactivationFactory.get();

      for (Tpactivation tp : activeTps) {
        final List<Techpackdependency> tpDependencies = getDependentTpsBasedOnVersion(tp.getVersionid());
        addTechpacksInCorrectOrder(tp, orderedActiveTpNames, tpDependencies.listIterator());
      }
    } catch (SQLException e) {
      throw new BuildException("Failed to retreive active techpacks", e);
    } catch (RockException e) {
      throw new BuildException("Failed to retreive active techpacks", e);
    }
    return orderedActiveTpNames;
  }

  private List<Techpackdependency> getDependentTpsBasedOnVersion(final String versionId) throws SQLException,
      RockException {
    final Techpackdependency whereTpDependency = new Techpackdependency(dwhrepRockFactory);
    whereTpDependency.setVersionid(versionId);
    final TechpackdependencyFactory tpDependencyFactory = new TechpackdependencyFactory(this.dwhrepRockFactory,
        whereTpDependency);

    return tpDependencyFactory.get();
  }

  /**
   * Get the list of techpacks in the correct order so that the dependant
   * techpacks are listed first
   * 
   * @param techpack
   * @param orderedActiveTpNames
   * @throws SQLException
   * @throws RockException
   */
  private void addTechpacksInCorrectOrder(final Tpactivation techpack, final List<String> orderedActiveTpNames,
      final Iterator<Techpackdependency> tpDependencies) throws SQLException, RockException {

    if (!orderedActiveTpNames.contains(techpack.getTechpack_name())) {
      while (tpDependencies.hasNext()) {
        final Techpackdependency tp = tpDependencies.next();
        final Tpactivation whereTpactivation = new Tpactivation(dwhrepRockFactory);
        whereTpactivation.setTechpack_name(tp.getTechpackname());

        final TpactivationFactory tpactivationFactory = new TpactivationFactory(dwhrepRockFactory, whereTpactivation);
        final Vector<Tpactivation> activeTps = tpactivationFactory.get();

        for (Tpactivation activeTp : activeTps) {
          tpDependencies.remove();
          addTechpacksInCorrectOrder(activeTp, orderedActiveTpNames, tpDependencies);
        }

      }
      System.out.println("Adding " + techpack.getTechpack_name() + " to list of techpacks...");
      orderedActiveTpNames.add(techpack.getTechpack_name());
    }
  }

  private void runRestoreSets(final ITransferEngineRMI engineRMI) throws BuildException {
    for (String techpackName : activeTechpackNames) {
      System.out.println("\nAttempting to run restore sets for " + techpackName + "...");
      restore(engineRMI, techpackName, "ALL", "2000:01:01", getCurrentDate());
      System.out.println("Restore sets running for " + techpackName
          + ". Please check AdminUI to see if they ran successfully.");
    }
  }

  private String getCurrentDate() {
    final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy:MM:dd", Locale.getDefault());
    return dateTimeFormat.format(new Date());
  }

  public void setBinDirectory(final String dir) {
    binDirectory = dir;
  }

  public String getBinDirectory() {
    return binDirectory;
  }

  public void setInstallDirectory(final String dir) {
    installDirectory = dir;
  }

  public String getInstallDirectory() {
    return installDirectory;
  }

}
