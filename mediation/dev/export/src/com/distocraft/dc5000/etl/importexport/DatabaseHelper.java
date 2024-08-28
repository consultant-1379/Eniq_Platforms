package com.distocraft.dc5000.etl.importexport;
/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.VelocityException;

import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.dwhm.StorageTimeAction;
import com.distocraft.dc5000.etl.importexport.gpmgt.exception.GroupMgtException;
import com.distocraft.dc5000.etl.importexport.gpmgt.exception.GroupMgtImportException;
import com.distocraft.dc5000.etl.importexport.gpmgt.exception.GroupMgtVelocityException;
import com.distocraft.dc5000.etl.rock.Meta_databases;
import com.distocraft.dc5000.etl.rock.Meta_databasesFactory;
import com.distocraft.dc5000.repository.cache.GroupTypeDef;
import com.distocraft.dc5000.repository.cache.GroupTypeKeyDef;
import com.distocraft.dc5000.repository.dwhrep.Dwhcolumn;
import com.distocraft.dc5000.repository.dwhrep.DwhcolumnFactory;
import com.distocraft.dc5000.repository.dwhrep.Dwhpartition;
import com.distocraft.dc5000.repository.dwhrep.DwhpartitionFactory;
import com.distocraft.dc5000.repository.dwhrep.Dwhtype;
import com.distocraft.dc5000.repository.dwhrep.DwhtypeFactory;
import com.distocraft.dc5000.repository.dwhrep.Tpactivation;
import com.distocraft.dc5000.repository.dwhrep.TpactivationFactory;

/**
 * Db Helper class for accessing dwhdb and dwhrep, handles opening and closing of connection.
 */
@SuppressWarnings({"PMD.TooManyMethods", "PMD.ExcessiveImports"})	
public class DatabaseHelper {
	/**
	 * dwhrep connection
	 */
	private static RockFactory dwhrep = null;
	/**
	 * dwhdb connection
	 */
	private static RockFactory dwhdb = null;
	/**
	 * dwhrep connection type constant.
	 */
	private static final String DWHREP = "dwhrep";
	/**
	 * dwhdb connection type constant
	 */
	private static final String DWH = "dwh";
	/**
	 * Key in ETLC server properties for the default ETLREP url
	 */
	private static final String ENGINE_DB_URL = "ENGINE_DB_URL";
	/**
	 * Key in ETLC server properties for the default etlrep jdbc driver
	 */
	private static final String ENGINE_DRIVER = "ENGINE_DB_DRIVERNAME";
	/**
	 * Key in ETLC server properties for default etlrep username
	 */
	private static final String ENGINE_USERNAME = "ENGINE_DB_USERNAME";
	/**
	 * Key in ETLC server properties for the default etlrep password
	 */
	private static final String ENGINE_PASSWORD = "ENGINE_DB_PASSWORD";
		
	/**
	 * User connection type constant for Meta_databases
	 */
	private static final String USER = "USER";
	/**
	 * Session name for database connections.
	 */
	private static final String GRP_MGT_IMPORT = "GrpMgtImport";
	/**
	 * Key to get the the EVENTS techpack name from the group management properties
	 */
	private static final String TPNAME_PROP = "events.tpname";

    /**
     * delete table template
     */
    private final String deleteTableTemplate;

	private static final String DROP_TABLE = "drop table ";
	private static final String TRUNCATE_TABLE = "truncate table ";
	private static final List<String> SQL_NOT_FOUND_STATES = Arrays.asList("S0002", "42W33", "42501"); // NOPMD : Descriptive
	private static final String SQL_TABLE_ALREADY_DEFINED = "52010"; // NOPMD : Descriptive
	private static final String DC_SCHEMA = "dc.";
	private static final String SELECT_COUNT_1 = "select count(1) as cc from ";

  private static final String SELECT_COUNT_GROUP_NAME = "select count(distinct(GROUP_NAME)) as cnt from ";
	private static final Logger LOGGER = Logger.getLogger("gpmgt.DatabaseHelper");
    private final Properties gpMgtProperties;
	/**
	 * Mutex object used while creating db connections.
	 */
	private static final Object MUTEX = new Object();

	/**
	 * Constructor for Export.
	 */
	public DatabaseHelper(){
        gpMgtProperties = new Properties();
        deleteTableTemplate = "undefined";
	}

	/**
	 * Constructor for Import, with load options
	 * @param gpMgtProperties BCP Load options
	 */
	public DatabaseHelper(final Properties gpMgtProperties){
		this.gpMgtProperties = gpMgtProperties;
        deleteTableTemplate = gpMgtProperties.getProperty("table.delete.template", "templates/delete_table.vm");
	}

	/**
	 * Get the DC user schema
	 * @return DC Schema
	 */
	public String getDcSchema() {
		return DC_SCHEMA;
	}

	/**
	 * Logging....
	 * @param msg What to log
	 * @param lvl The level to log it
	 */
	private void log(final String msg, final Level lvl){
		LOGGER.log(lvl, msg);
	}

	/**
	 * Get a connection to DWHDB
	 * This is a shared instance and only one will exist.
	 * @return DWHDB connection
	 * @throws RockException Connection Errors
	 * @throws SQLException Select errors
	 * @throws IOException Failed to read properties
	 */
	public RockFactory getDwhdb() throws RockException, SQLException, IOException {
		synchronized (MUTEX) {
			if (dwhdb == null || dwhdb.getConnection().isClosed()) {
				final CProps connDetails = getConnectionInfo(DWH);
				dwhdb = new RockFactory(connDetails.getUrl(), connDetails.getUser(), connDetails.getPasswd(),
					 connDetails.getDriver(), GRP_MGT_IMPORT, false);
			}
			return dwhdb;
		}
	}

	/**
	 * Get a connection to DWHREP
	 * This is a shared instance and only one will exist.
	 * @return DWHREP connection
	 * @throws RockException Connection Errors
	 * @throws SQLException Select errors
	 * @throws IOException Failed to read properties
	 */
	public RockFactory getDwhrep() throws RockException, SQLException, IOException {
		synchronized (MUTEX) {
			if (dwhrep == null || dwhrep.getConnection().isClosed()) {
				final CProps connDetails = getConnectionInfo(DWHREP);
				dwhrep = new RockFactory(connDetails.getUrl(), connDetails.getUser(), connDetails.getPasswd(),
					 connDetails.getDriver(), GRP_MGT_IMPORT, false);
			}
			return dwhrep;
		}
	}

	/**
	 * Get the versionid of the current active tech pack
	 * @return VersionId of the current active tech pack
	 * @throws RockException Db connection errors
	 * @throws IOException Errors getting properties
	 * @throws SQLException Db select errors
	 * @throws GroupMgtImportException If no active techpack is found.
	 */
	public String getActiveTechpackVersion() throws RockException, IOException, SQLException, GroupMgtImportException {
		final Properties gpMgtProps = GroupMgtHelper.getGpMgtProperties();
		final String tpName = gpMgtProps.getProperty(TPNAME_PROP);
		if(tpName == null || tpName.length() == 0){
			throw new GroupMgtException("No Default TechPack name found", null);
		}
		final Tpactivation where = new Tpactivation(getDwhrep());
		where.setTechpack_name(tpName);
		final TpactivationFactory fac = new TpactivationFactory(getDwhrep(), where);
		final List<Tpactivation> vList = fac.get();
		if(vList.isEmpty()){
			throw new GroupMgtImportException("No Active Version of "+tpName+" found.");
		}
		return vList.get(0).getVersionid();
	}


	/**
	 * Get the connection details needed to open a RockFactory object.
	 * @param cType The Connection type e.g. DWHREP
	 * @return Struct with the login details needed to open a RockFactory object
	 * @throws SQLException Select errors
	 * @throws RockException Connection errors
	 * @throws IOException Failed to read etlc properties file
	 */
	private CProps getConnectionInfo(final String cType) throws SQLException, RockException, IOException {
		final Properties etlcProps = GroupMgtHelper.getEtlcProperties();
		final String dbUrl = etlcProps.getProperty(ENGINE_DB_URL);
		final String driver = etlcProps.getProperty(ENGINE_DRIVER);
		final String user = etlcProps.getProperty(ENGINE_USERNAME);
		final String pass = etlcProps.getProperty(ENGINE_PASSWORD);
		final RockFactory etlrep = new RockFactory(dbUrl, user, pass, driver, GRP_MGT_IMPORT, false);
		try{
			final Meta_databases where  = new Meta_databases(etlrep);
			where.setType_name(USER);
			where.setConnection_name(cType);
			final Meta_databasesFactory fac = new Meta_databasesFactory(etlrep, where);
			@SuppressWarnings({"unchecked"}) final List<Meta_databases> dbs = fac.get();
			if(dbs.isEmpty()){
				throw new RockException("Cant find "+cType+" login details.");
			}
			final Meta_databases metaDwhrep = dbs.get(0);
			final CProps etlDetails = new CProps();
			etlDetails.setUrl(metaDwhrep.getConnection_string());
			etlDetails.setDriver(metaDwhrep.getDriver_name());
			etlDetails.setUser(metaDwhrep.getUsername());
			etlDetails.setPasswd(metaDwhrep.getPassword());
			return etlDetails;
		} finally {
			close(etlrep);
		}
	}
	/**
	 * Cleanup
	 * Closes all database connections
	 */
	public void destroy() {
			closeDwhdb();
			closeDwhrep();
	}

	/**
	 * Close the dwhdb connection
	 */
	public void closeDwhdb(){
		close(dwhdb);
	}
	/**
	 * Close the dwhrep connection
	 */
	public void closeDwhrep(){
		close(dwhrep);
	}
	/**
	 * Utility method to close a Database connection
	 * @param rock Connection to close, can be null
	 */
	private void close(final RockFactory rock) {
		try {
			if (rock != null && !rock.getConnection().isClosed()) {
				rock.getConnection().close();
			}
		} catch (Throwable t) {/*Ignore, there's not much we can do*/} //NOPMD
	}
	// fix for DEFTFTMIT-568
	// To get the HIERARCHY_1 values for a particular CELL_ID
	public String getHierarchy_1For3GCell(final String sqlStmt) throws RockException, IOException, SQLException {
		getDwhdb();
		final Statement stmt = getDwhdb().getConnection().createStatement();
		try{
			ResultSet rs= stmt.executeQuery(sqlStmt);
			String temp=null;
			while(rs.next()) {
				temp=rs.getString(1);
			}
			return temp;
			} finally {
				stmt.close();
			}	
		}

	/**
	 * Exec an Update SQL in DWHDB
	 * @param sqlStmt SWL Statement to execute
	 * @return Rows updated
	 * @throws RockException Connections errors
	 * @throws IOException Failed to reas properties
	 * @throws SQLException SQL errors
	 */
	public int executeUpdateDwhdb(final String sqlStmt) throws RockException, IOException, SQLException {
		getDwhdb();
		final Statement stmt = getDwhdb().getConnection().createStatement();
		try{
      final int rows = stmt.executeUpdate(sqlStmt);
      stmt.getConnection().commit();
      return rows;
		} finally {
			stmt.close();
		}
	}

	/**
	 * Check is a table is empty or not
	 * @param tableName The table to check
	 * @param dbConnection The connection to use
	 * @return TRUE if the table is empty, FALSE otherwise
	 * @throws SQLException For any errors
	 */
	public boolean isTableEmpty(final String tableName, final RockFactory dbConnection) throws SQLException {
		final Statement stmt = dbConnection.getConnection().createStatement();
		ResultSet countResult;
		try {
			countResult = stmt.executeQuery(SELECT_COUNT_1 + getDcSchema() + tableName);
			try {
				countResult.next();
				final int rowCount = Integer.parseInt(countResult.getString("cc"));
				return rowCount == 0;
			} finally {
				countResult.close();
			}
		} finally {
			stmt.close();
		}
	}

  /**
   * Gets the total number of groups that are already in the table and the groups that will be imported as a sum of:
   * 1) old non-FeaturePack-specific number of distinct group names for a particular group type in the actual group table,
   * 2) new non-FeaturePack-specific number of distinct group names for a particular group type in the temporary table, and
   * 3) new FeaturePack-specific number of distinct group names for a particular group type in the temporary table.
   * The FeaturePack-specific group names (pattern) can be specified in gpmgt.events.fpgroupsname.
   *
   * @param tableName
   *          The table to which the groups will be imported
   * @param tempTableName
   *          The table of groups being imported
   * @param defaultFpGroupName
   *          The default FeaturePack-specific group name pattern
   * @param dbConnection
   *          The connection to use
   * @throws SQLException
   *           For any errors
   * @return the number of distinct group names for a particular group type between
   * temp group table and the actual group table
   */
  public long getNumberOfGroupsForGroupType(final String tableName, final String tempTableName,
	  final String defaultFpGroupName, final RockFactory dbConnection)
      throws SQLException
  {
    // FeaturePack-specific group name pattern or a default one if the property is not set
    final String fpGroupName = gpMgtProperties.getProperty("events.fpgroupsname", defaultFpGroupName);
    final Statement stmt = dbConnection.getConnection().createStatement();
    ResultSet countResult1 = null;
    ResultSet countResult2 = null;
    ResultSet countResult3 = null;
    try {
      countResult1 = stmt.executeQuery(SELECT_COUNT_GROUP_NAME + getDcSchema() + tableName
		  + " where GROUP_NAME not like '" + fpGroupName + "'");
      countResult1.next();
      // old non-FeaturePack-specific number of distinct group names for a particular group type in the actual group table
      final long numberOfOldNonFpGroups = Long.parseLong(countResult1.getString("cnt"));
      log("Number of non-FeaturePack-specific groups groups currently in " + tableName + " is " + numberOfOldNonFpGroups, Level.FINE);
      countResult2 = stmt.executeQuery(SELECT_COUNT_GROUP_NAME + getDcSchema() + tempTableName
	          + " where GROUP_NAME not like '" + fpGroupName + "' and GROUP_NAME not in (select distinct(GROUP_NAME) from "
	          + getDcSchema() + tableName + " where GROUP_NAME not like '" + fpGroupName + "')");
      countResult2.next();
      // new non-FeaturePack-specific number of distinct group names for a particular group type in the temporary table
      final long numberOfNewNonFpGroups = Long.parseLong(countResult2.getString("cnt"));
      log("Number of new non-FeaturePack-specific groups to import into " + tableName + " is " + numberOfNewNonFpGroups, Level.FINE);
      countResult3 = stmt.executeQuery(SELECT_COUNT_GROUP_NAME + getDcSchema() + tempTableName
	          + " where GROUP_NAME like '" + fpGroupName + "' and GROUP_NAME not in (select distinct(GROUP_NAME) from "
	          + getDcSchema() + tableName + " where GROUP_NAME like '" + fpGroupName + "')");
      countResult3.next();
      // new FeaturePack-specific number of distinct group names for a particular group type in the temporary table
      final long numberOfNewFpGroups = Long.parseLong(countResult3.getString("cnt"));
      log("Number of new FeaturePack-specific groups to import into " + tableName + " is " + numberOfNewFpGroups, Level.FINE);
      countResult1.close();
      countResult2.close();
      countResult3.close();
      return numberOfOldNonFpGroups + numberOfNewNonFpGroups + numberOfNewFpGroups;
    } finally {
      stmt.close();
      if (countResult1 != null) {
        countResult1.close();
      }
      if (countResult2 != null) {
      countResult2.close();
      }
      if (countResult3 != null) {
        countResult3.close();
      }
    }
  }

  /**
   * Gets the number of distinct group names for a particular grouptype and RAT
   * value from the temporary group table [tempTableName] and the existing group
   * table [tableName].
   * 
   * @param tableName
   *          The table with existing group names
   * @param tempTableName
   *          The temporary table containing new groups.
   * @param dbConnection
   *          The connection to use
   * @param rat
   *          value. To distinguish between 2G and 3G groups.
   * @throws SQLException
   *           For any errors
   * @return number of distinct group names for a particular grouptype & RAT type
   */
  public int getNumberOfGroupsForRatValue(final String tableName, final String tempTableName,
      final RockFactory dbConnection, final int rat)
      throws SQLException
  {
    final Statement stmt = dbConnection.getConnection().createStatement();
    ResultSet countResult1 = null;
    ResultSet countResult2 = null;
    try {
      countResult1 = stmt.executeQuery(SELECT_COUNT_GROUP_NAME + getDcSchema() + tableName+" where RAT = "+rat);
      countResult1.next();
      final int numberOfGroups = Integer.parseInt(countResult1.getString("cnt"));
      log("Number of groups currently in " + tableName + " for RAT = " + rat + " is " + numberOfGroups, Level.FINE);
      countResult2 = stmt.executeQuery(SELECT_COUNT_GROUP_NAME + getDcSchema() + tempTableName
          + " where GROUP_NAME not in (select distinct(GROUP_NAME) from " + getDcSchema() + tableName + " where RAT = "
          + rat + ") and RAT = " + rat);
      countResult2.next();
      final int numberOfNewGroups = Integer.parseInt(countResult2.getString("cnt"));
      log("Number of new groups to import into " + tableName + " for RAT = " + rat + " is " + numberOfNewGroups,
          Level.FINE);
      return numberOfGroups + numberOfNewGroups;
    } finally {
      stmt.close();
      countResult1.close();
      countResult2.close();
    }
  }

  public List<Integer> getRatValuesFromTempTable(final String tempTableName, final RockFactory dbConnection)
      throws SQLException {
    final List<Integer> listOfRatValues = new ArrayList<Integer>();
    final Statement stmt = dbConnection.getConnection().createStatement();
    ResultSet ratResult = null;
    try {
      ratResult = stmt.executeQuery("select distinct(RAT) from " + getDcSchema() + tempTableName);
      while (ratResult.next()) {
        listOfRatValues.add(ratResult.getInt("RAT"));
      }
      return listOfRatValues;
    } finally {
      stmt.close();
      ratResult.close();
    }
  }
	/**
	 * Load a table
	 * @param tmpTableDDL List of temp table and the DDL required to create them if needed
	 * @param fileColumnMap Map of the colums that are loaded for eac file being loaded
	 * @param afterLoadAction Delete or leave the generated load files
	 * @throws RockException connection errors
	 * @throws SQLException SQL Errors
	 * @throws IOException If failed to read properties
	 */
	@SuppressWarnings({"PMD.DataflowAnomalyAnalysis"})
	public void loadTempTables(final Map<String, String> tmpTableDDL, final Map<File, String> fileColumnMap,
														 final String afterLoadAction) throws RockException, SQLException, IOException {
		final Statement executeStmt = getDwhdb().getConnection().createStatement();
        final String loaderOptions = gpMgtProperties.getProperty("loader.options", "");
		try {
			for (File fileToLoad : fileColumnMap.keySet()) {
				// the load file is the table name (minus the .sql bit)
				final String fName = fileToLoad.getName();
				final String tmpTableToLoad = fName.substring(0, fName.lastIndexOf('.'));
				if (tmpTableDDL.containsKey(tmpTableToLoad)) {
					final String cSql = tmpTableDDL.remove(tmpTableToLoad);
					log("Creating temporary table " + tmpTableToLoad, Level.FINE);
					try {
						log(cSql, Level.FINEST);
						executeStmt.executeUpdate(cSql);
					} catch (SQLException e) {
						if (SQL_TABLE_ALREADY_DEFINED.equals(e.getSQLState())) {
							log("Table '" + tmpTableToLoad + "' already exists, using that one.", Level.FINE);
							log("Truncating temp table '" + tmpTableToLoad + "'", Level.FINE);
							executeStmt.executeUpdate(TRUNCATE_TABLE + tmpTableToLoad + ";");
						} else {
							throw e;
						}
					}
				}
				final String loadFilePath = fileToLoad.getAbsolutePath();
				final StringBuilder loadSqlBfr = new StringBuilder(); //NOPMD : Where else does it go?
				loadSqlBfr.append("LOAD TABLE ").append(getDcSchema()).append(tmpTableToLoad).append(" (");
				final String columnPartStmt = fileColumnMap.get(fileToLoad);
				loadSqlBfr.append(columnPartStmt);
				loadSqlBfr.append(") from '"). append(loadFilePath).append("' ").append(loaderOptions).append(";");
				log(loadSqlBfr.toString(), Level.FINE);
				final int rowsLoaded = doLoad(executeStmt, loadSqlBfr.toString(), fileToLoad, afterLoadAction);
				log("\tLoaded " + rowsLoaded + " rows into " + tmpTableToLoad, Level.INFO);
			}
		} catch (SQLException e){
			throw new GroupMgtImportException(e.getMessage(), e);
		} finally {
			executeStmt.close();
		}
	}

	/**
	 * Execute the load
	 * Like this for the tests to get access
	 * @param stmt Statement to use
	 * @param loadStmt Load SQL statement
	 * @param fileToLoad File to load
	 * @param afterAction After on the load file
	 * @return Number of rows loaded
	 * @throws SQLException SQL Errors
	 * @throws IOException Failed to get properties
	 */
	protected int doLoad(final Statement stmt, final String loadStmt, final File fileToLoad,
												final String afterAction) throws SQLException, IOException {
		try {
			return stmt.executeUpdate(loadStmt);
		} finally {
			afterLoadAction(fileToLoad, afterAction);
		}
	}

	/**
	 * Delete or leave the generated load file.
	 * @param fileToLoad Load file
	 * @param action Action to take, delete or none.
	 */
	protected void afterLoadAction(final File fileToLoad, final String action){
		if(fileToLoad == null || !fileToLoad.exists()){
			return;
		}
		AfterLoadAction ala; //NOPMD : its the point of the method....
		try{
			ala = AfterLoadAction.valueOf(action); //NOPMD : its the point of the method....
		} catch (IllegalArgumentException e){
			ala = AfterLoadAction.delete;
			log("After Load Action '"+action+"' is not valid, defaulting to " + ala, Level.WARNING);
		}
		if(ala == AfterLoadAction.delete && fileToLoad.delete()) {
			log("Couldn't delete " + fileToLoad.getAbsoluteFile() + "; delete manually", Level.FINE);
		}
	}

	/**
	 * Drop a temp table
	 * @param tableToDelete The table name
	 * @throws RockException connection errors
	 * @throws SQLException SQL Errors
	 * @throws IOException If failed to read properties
	 */
	public void dropTmpTables(final String tableToDelete) throws SQLException, RockException, IOException {
		final Statement dropTmp = getDwhdb().getConnection().createStatement();
		try {
			dropTmp.executeUpdate(DROP_TABLE + getDcSchema() + tableToDelete);
		} catch (SQLException e) {
			if (!SQL_NOT_FOUND_STATES.contains(e.getSQLState())) { // table not found....
				throw e;
			}
		} finally {
			dropTmp.close();
		}
	}

	/**
	 * Get teh DDL statement to create the temp table
	 * @param tmpTableName The temp table name
	 * @param gpType the group tyoe the table is for
	 * @return The table DDL sql
	 * @throws RockException connection errors
	 * @throws SQLException SQL Errors
	 * @throws IOException If failed to read properties
	 */
	public String getTempTableDDL(final String tmpTableName, final GroupTypeDef gpType ) throws RockException, SQLException, IOException {
		final Dwhtype dwhType = getDwqhType(gpType);
		final Dwhpartition partition = getDwhPartitions(gpType);
		partition.setTablename(tmpTableName);

		final Vector<Dwhcolumn> cols = getDwhColumns(dwhType);	// NOPMD : Not changing repository
		// tmp table key order should match the real table...
		final Vector<Dwhcolumn> orderList = new Vector<Dwhcolumn>(); // NOPMD : Not changing repository
		for (GroupTypeKeyDef key : gpType.getKeys()) {
			for (Dwhcolumn c : cols) {
				if (c.getDataname().equalsIgnoreCase(key.getKeyName())) {
					orderList.add(c);
					break;
				}
			}
		}
		try {
			GroupMgtHelper.initVelocity();
			final StringBuilder createSql = new StringBuilder();
            final String deleteIfExists = getDeleteTableSql(tmpTableName);
			createSql.append(deleteIfExists).append("\n");
			final StorageTimeAction sta = getStorageTimeAction();
			final String staStmt = sta.getPartitionCreateStatement(dwhType, partition, orderList);
			createSql.append(staStmt).append("\n");
      return createSql.toString();
		} catch (Exception e) {
			throw new GroupMgtImportException("Failed to generate temp table creation sql", e);
		}
	}

    protected String getDeleteTableSql(final String tableName){
        final StringWriter sqlWriter = new StringWriter();
		final VelocityContext vctx = new VelocityContext();
		vctx.put("tableToDelete", tableName);
		boolean mergedOk;
		try {
			mergedOk = Velocity.mergeTemplate(deleteTableTemplate, Velocity.ENCODING_DEFAULT, vctx, sqlWriter);
			if(mergedOk){
                return sqlWriter.toString();
			}
                throw new GroupMgtImportException("Velocity Merge failed for an unknown reason..");
		} catch (VelocityException e) {
			throw new GroupMgtVelocityException(deleteTableTemplate, e);
		} catch (Exception e) {
			throw new GroupMgtVelocityException(deleteTableTemplate, e);
		}
    }

	/**
	 * Get an instance of StorageTimeAction
	 * @return StorageTimeAction
   * @throws Exception Creation errors
	 */
	protected StorageTimeAction getStorageTimeAction() throws Exception {
		return new StorageTimeAction(null, null, LOGGER);
	}
	
	private Dwhtype getDwqhType(final GroupTypeDef gpType) throws RockException, SQLException, IOException {
		getDwhrep();
		final Dwhtype dwhere = new Dwhtype(dwhrep);
		dwhere.setTypename(gpType.getTypename());
		final DwhtypeFactory dfac = new DwhtypeFactory(dwhrep, dwhere);
		final List<Dwhtype> dwhtypes = dfac.get();
		if (dwhtypes.isEmpty()) {
			throw new GroupMgtImportException("No DWH Type found!");
		}
		return dwhtypes.get(0);
	}

	private Dwhpartition getDwhPartitions(final GroupTypeDef gpType) throws SQLException, RockException, IOException {
		getDwhrep();
		final Dwhpartition pwhere = new Dwhpartition(dwhrep);
		pwhere.setTablename(gpType.getTableName());
		final DwhpartitionFactory pfac = new DwhpartitionFactory(dwhrep, pwhere);
		final List<Dwhpartition> parts = pfac.get();
		if (parts.isEmpty()) {
			throw new GroupMgtImportException("No DWH Partition found!");
		}
		return parts.get(0);
	}

	private Vector<Dwhcolumn> getDwhColumns(final Dwhtype dwhType // NOPMD : Not changing StorageTimeAction StorageTimeAction & repository
																					) throws SQLException, RockException, IOException {
		getDwhrep();
		final Dwhcolumn cwhere = new Dwhcolumn(dwhrep);
		cwhere.setStorageid(dwhType.getStorageid());
		final DwhcolumnFactory cfac = new DwhcolumnFactory(dwhrep, cwhere);
		final Vector<Dwhcolumn> cols = cfac.get(); // NOPMD : Not changing repository
		if (cols.isEmpty()) {
			throw new GroupMgtImportException("No DWH Columns found!");
		}
		return cols;
	}


	/**
	 * Struct to hold login details, username, password, db_url & driver to use.
	 */
	private static class CProps {
		private String url = null;
		private String user = null;
		private String passwd = null;
		private String driver = null;
		public String getUrl() {
			return url;
		}

		public void setUrl(final String aUrl) {
			this.url = aUrl;
		}

		public String getUser() {
			return user;
		}

		public void setUser(final String aUser) {
			this.user = aUser;
		}

		public String getPasswd() {
			return passwd;
		}

		public void setPasswd(final String aPasswd) {
			this.passwd = aPasswd;
		}

		public String getDriver() {
			return driver;
		}

		public void setDriver(final String aDriver) {
			this.driver = aDriver;
		}
	}
}
