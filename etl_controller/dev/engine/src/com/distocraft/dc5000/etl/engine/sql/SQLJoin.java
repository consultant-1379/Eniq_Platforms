package com.distocraft.dc5000.etl.engine.sql;

import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.common.SessionHandler;
import com.distocraft.dc5000.etl.engine.common.EngineConstants;
import com.distocraft.dc5000.etl.engine.common.EngineException;
import com.distocraft.dc5000.etl.engine.common.EngineMetaDataException;
import com.distocraft.dc5000.etl.engine.common.SetContext;
import com.distocraft.dc5000.etl.engine.connect.ConnectionPool;
import com.distocraft.dc5000.etl.engine.structure.TransferActionBase;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_databases;
import com.distocraft.dc5000.etl.rock.Meta_databasesFactory;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_versions;
import com.distocraft.dc5000.repository.cache.PhysicalTableCache;
import com.distocraft.dc5000.repository.dwhrep.Dwhtype;
import com.distocraft.dc5000.repository.dwhrep.DwhtypeFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementcounter;
import com.distocraft.dc5000.repository.dwhrep.MeasurementcounterFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementkey;
import com.distocraft.dc5000.repository.dwhrep.MeasurementkeyFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementtype;
import com.distocraft.dc5000.repository.dwhrep.MeasurementtypeFactory;
import com.ericsson.eniq.common.VelocityPool;

/**
 * @author savinen
 */
public class SQLJoin extends SQLOperation {

	private final Logger log;

	private final String storageid;
	private final String prevtablename;
	private final String versionid;
	private final String objName;
	private final String emptyKey;
	private final String clause;

	private final List<String> ignoredKeysList;

	private RockFactory etlreprock = null;

	private final RockFactory dwhreprock;

	private Exception exp = null;

	SetContext sctx;

	private long startTime = 0l;

	private long endTime = 0l;

	private final boolean useROWSTATUS;

	protected SQLJoin() {
		this.log = Logger.getLogger("SQLJoin");
		this.storageid = null;
		this.prevtablename = null;
		this.versionid = null;
		this.objName = null;
		this.emptyKey = "''";
		this.clause = null;
		this.ignoredKeysList = null;
		this.dwhreprock = null;
		this.useROWSTATUS = false;
	}

	/**
	 * Constructor
	 * 
	 * @param versionNumber
	 *          metadata version
	 * @param collectionSetId
	 *          primary key for collection set
	 * @param collectionId
	 *          primary key for collection
	 * @param transferActionId
	 *          primary key for transfer action
	 * @param transferBatchId
	 *          primary key for transfer batch
	 * @param connectId
	 *          primary key for database connections
	 * @param rockFact
	 *          metadata repository connection object
	 * @param connectionPool
	 *          a pool for database connections in this collection
	 * @param trActions
	 *          object that holds transfer action information (db contents)
	 * 
	 */
	public SQLJoin(final Meta_versions version, final Long collectionSetId, final Meta_collections collection,
			final Long transferActionId, final Long transferBatchId, final Long connectId, final RockFactory rockFact,
			final ConnectionPool connectionPool, final Meta_transfer_actions trActions, final SetContext sctx,
			final Logger clog) throws EngineMetaDataException {

		super(version, collectionSetId, collection, transferActionId, transferBatchId, connectId, rockFact, connectionPool,
				trActions);

		final Map<String,String> sessionLogEntry = new HashMap<String,String>();

		long loadersetID = -1;
		try {
			loadersetID = SessionHandler.getSessionID("loader");
		} catch (Exception e) {
			throw new EngineMetaDataException("Error getting loaderSetID", e, "init");
		}

		sessionLogEntry.put("LOADERSET_ID", String.valueOf(loadersetID));
		sessionLogEntry.put("SESSION_ID", "");
		sessionLogEntry.put("BATCH_ID", "");
		sessionLogEntry.put("TIMELEVEL", "");
		sessionLogEntry.put("DATATIME", "");
		sessionLogEntry.put("DATADATE", "");
		sessionLogEntry.put("ROWCOUNT", "");
		sessionLogEntry.put("SESSIONSTARTTIME", String.valueOf(System.currentTimeMillis()));
		sessionLogEntry.put("SESSIONENDTIME", "");
		sessionLogEntry.put("STATUS", "");
		sessionLogEntry.put("TYPENAME", "");

		sctx.put("sessionLogEntry", sessionLogEntry);

		this.sctx = sctx;

		this.log = Logger.getLogger(clog.getName() + ".SQLJoin");

		final String where = trActions.getWhere_clause();

		this.etlreprock = rockFact;

		try {

			final Meta_databases md_cond = new Meta_databases(etlreprock);
			md_cond.setType_name("USER");
			final Meta_databasesFactory md_fact = new Meta_databasesFactory(etlreprock, md_cond);

			RockFactory dwhreprock = null;
			
			for (Meta_databases db : md_fact.get()) {

				if (db.getConnection_name().equalsIgnoreCase("dwhrep")) {
					dwhreprock = new RockFactory(db.getConnection_string(), db.getUsername(), db.getPassword(),
							db.getDriver_name(), "SQLJoin", true);
				}

			} // for each Meta_databases

			if (dwhreprock == null)  {
				throw new Exception("Database dwhrep is not defined in Meta_databases");
			} else {
				this.dwhreprock = dwhreprock;
			}
				
			final Properties prop = TransferActionBase.stringToProperties(where);
			
			final String basetablename = prop.getProperty("typeName");
			this.prevtablename = prop.getProperty("prevTableName");
			this.versionid = prop.getProperty("versionid");
			this.objName = prop.getProperty("objName");
			final String ignoredKeys = prop.getProperty("ignoredKeys", "");
			this.emptyKey = prop.getProperty("emptyKey", "''");

			sctx.put("MeasType", objName);

			this.ignoredKeysList = new ArrayList<String>();
			final String[] iKeys = ignoredKeys.split(",");

			for (int i = 0; i < iKeys.length; i++) {
				ignoredKeysList.add(iKeys[i]);
				log.finer("Added to ignored keys: " + iKeys[i]);
			}

			log.finer("objName: " + objName);
			log.finer("basetablename: " + basetablename);
			log.finer("versionid: " + versionid);

			if (basetablename == null) {
				throw new Exception("Parameter basetablename must be defined");
			}
				
			final Dwhtype dt = new Dwhtype(dwhreprock);
			dt.setBasetablename(basetablename);
			final DwhtypeFactory dtf = new DwhtypeFactory(dwhreprock, dt);
			final Dwhtype dtr = dtf.getElementAt(0);

			if (dtr == null) {
				throw new Exception("Basetablename " + basetablename + " Not found from DWHType");
			}
				
			this.storageid = dtr.getStorageid();

			log.finer("storageid: " + storageid);

			this.clause = trActions.getAction_contents();

			log.finer("SQLclause: " + clause);

			if (clause == null) {
				throw new Exception("Parameter clause must be defined");
			}
				
		} catch (Exception e) {

			// Failure cleanup connections
			try {
				this.dwhreprock.getConnection().close();
			} catch (Exception se) {
			}

			throw new EngineMetaDataException("Error while initializing joiner ", e, "constructor");
		}

		boolean useROWSTATUS = false;
		
		try {

			final Meta_transfer_actions whereDim = new Meta_transfer_actions(rockFact);
			whereDim.setCollection_id(collection.getCollection_id());
			whereDim.setCollection_set_id(collection.getCollection_set_id());
			whereDim.setVersion_number(collection.getVersion_number());
			whereDim.setAction_type("UpdateDimSession");

			try {

				final Meta_transfer_actions updateDIMSession = new Meta_transfer_actions(rockFact, whereDim);
				final String DIMSessionWhere = updateDIMSession.getWhere_clause();

				final Properties DIMSessionProperties = TransferActionBase.stringToProperties(DIMSessionWhere);

				useROWSTATUS = "true".equalsIgnoreCase(DIMSessionProperties.getProperty("useRAWSTATUS", "false"));

			} catch (Exception e) {
				log.finer("No UpdateDIMsession action was found from this set.");
			}
		} catch (Exception e) {
			log.log(Level.WARNING, "Error while trying to fetch UpdateDimSessions ROWSTATUS property\n", e);
		}
		
		this.useROWSTATUS = useROWSTATUS;
	}

	@Override
	public void execute() throws Exception {

		VelocityEngine ve = null;

		try {

			final String sClause = "select min(date_id) min, max(date_id) max from " + prevtablename;

			log.finest("sql :" + sClause);

			ResultSet rSet = null;
			Statement stmtc = null;
			try {

				final RockFactory c = this.getConnection();

				try {
					// get max value from DB
					stmtc = c.getConnection().createStatement();
					stmtc.getConnection().commit();
					rSet = stmtc.executeQuery(sClause);
					stmtc.getConnection().commit();

				} catch (Exception e) {
					log.severe(e.getStackTrace() + "\r\n" + new String[] { this.getTrActions().getAction_contents() });
					throw e;
				}

				if (rSet != null) {
					while (rSet.next()) {

						if (rSet != null && rSet.getDate("min") != null) {
							startTime = rSet.getDate("min").getTime();
						}

						if (rSet != null && rSet.getDate("max") != null) {
							endTime = rSet.getDate("max").getTime();
						}

					}
				}
			} finally {
				try {
					rSet.close();
				} catch(Exception e) {}
				try {
					stmtc.close();
				} catch(Exception e) {}
			}

			log.finer("StartTime: " + startTime);
			log.finer("EndTime: " + endTime);

			ve = VelocityPool.reserveEngine();

			final VelocityContext ctx = getContext(versionid, prevtablename, objName);

			final StringWriter writer = new StringWriter();

			ve.evaluate(ctx, writer, "", clause);

			final String sqlClause = writer.toString();

			log.finer("Trying to execute: " + sqlClause);

			this.getConnection().executeSql(sqlClause);

			if (exp != null) {
				log.severe("Exception occured during execution. Failing set.");
				throw new EngineException(EngineConstants.CANNOT_EXECUTE, new String[] { this.getTrActions()
						.getAction_contents() }, exp, this, this.getClass().getName(), EngineConstants.ERR_TYPE_EXECUTION);

			}

			if (useROWSTATUS) {

				final List<String> tableListCreated = createTableList();

				if (tableListCreated.isEmpty()) {
					// use the tableList that is already in set context
					log.fine("Created table list is empty. Using the loaded table list.");
				} else {
					// use the created tableList which has the tables where rowstatus is
					// null or empty
					sctx.put("tableList", tableListCreated);
				}
			}

		} finally {

			VelocityPool.releaseEngine(ve);

			try {
				dwhreprock.getConnection().close();
			} catch (Exception se) {}
						
		}
	}

	private VelocityContext getContext(final String versionid, final String prevtablename, final String objName)
			throws SQLException, RockException {

		final List<String> tableNameList = new ArrayList<String>();

		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		final VelocityContext context = new VelocityContext();

		final List<Measurementkey> measkeyList = new ArrayList<Measurementkey>();
		final List<Measurementcounter> meascounterList = new ArrayList<Measurementcounter>();

		final Measurementtype mt = new Measurementtype(dwhreprock);
		mt.setVersionid(versionid);
		mt.setObjectname(objName);
		final MeasurementtypeFactory mtf = new MeasurementtypeFactory(dwhreprock, mt);

		for (Measurementtype types : mtf.get()) {

			// counters
			final Measurementcounter mc = new Measurementcounter(dwhreprock);
			mc.setTypeid(types.getTypeid());
			final MeasurementcounterFactory mcf = new MeasurementcounterFactory(dwhreprock, mc);

			meascounterList.addAll(mcf.get());

			// keys
			final Measurementkey mk = new Measurementkey(dwhreprock);
			mk.setTypeid(types.getTypeid());
			final MeasurementkeyFactory mkf = new MeasurementkeyFactory(dwhreprock, mk);
			
			measkeyList.addAll(mkf.get());
			
		}

		final PhysicalTableCache ptc = PhysicalTableCache.getCache();

		final List<Map<String,String>> tableList = new ArrayList<Map<String,String>>();

		for (String table : ptc.getActiveTables(storageid)) {

			try {

				final long sTime = ptc.getStartTime(table);
				final long eTime = ptc.getEndTime(table);

				if ((sTime <= endTime && eTime > endTime) || (sTime <= startTime && eTime > startTime)
						|| (sTime >= startTime && eTime <= endTime)) {

					final Map<String,String> m = new HashMap<String,String>();
					m.put("table", table);
					m.put("startDate", "'" + sdf.format(new Date(sTime)) + "'");
					m.put("endDate", "'" + sdf.format(new Date(eTime)) + "'");
					tableList.add(m);
					tableNameList.add(table);

				}

			} catch (Exception e) {
				log.log(Level.WARNING, "Error figuring out partitions for " + storageid, e);
				exp = e;
			}

		} // foreach table

		context.put("SourceTable", prevtablename);
		context.put("MeasurementKeyList", measkeyList);
		context.put("MeasurementCounterList", meascounterList);
		context.put("TargetTableList", tableList);
		context.put("IgnoredKeysList", ignoredKeysList);
		context.put("EmptyKey", emptyKey);

		sctx.put("tableList", tableNameList);

		return context;

	}

	/**
	 * Creating a list of tables which include null values in their rowstatus
	 * column.
	 * 
	 * @return List of tables to be loaded.
	 * @throws Exception
	 */
	private List<String> createTableList() throws SQLException {

		final List<String> tableList = new ArrayList<String>();

		final PhysicalTableCache ptc = PhysicalTableCache.getCache();

		final List<String> activeTables = ptc.getActiveTables(storageid);

		String sqlClause = "";
		final String selectPart = "\n SELECT DISTINCT date_id ";
		final String fromPart = "\n FROM ";
		final String wherePartForNulls = "\n WHERE rowstatus IS NULL ";
		final String wherePartForEmpties = "\n WHERE rowstatus = '' ";
		final String unionPart = "\n UNION ";
		final int activeTablesSize = activeTables.size();

		if (activeTablesSize > 0) {

			for (int i = 0; i < activeTablesSize; i++) {
				final String partitionTable = (String) activeTables.get(i);

				// Example:
				// SELECT DISTINCT date_id FROM DC_E_RAN_UCELL_RAW_01 WHERE rowstatus IS
				// NULL
				// UNION
				// SELECT DISTINCT date_id FROM DC_E_RAN_UCELL_RAW_01 WHERE rowstatus =
				// ''
				// ...

				sqlClause += selectPart + fromPart + partitionTable + wherePartForNulls;
				sqlClause += unionPart;
				sqlClause += selectPart + fromPart + partitionTable + wherePartForEmpties;

				if (i < activeTablesSize - 1) {
					sqlClause += unionPart;
				}
			}
			sqlClause += ";";

		} else {
			log.fine("No active tables found for storageID: " + storageid);
		}

		final RockFactory r = this.getConnection();
		final Statement s = r.getConnection().createStatement();
		ResultSet resultSet = null;
		try {
			if (sqlClause.length() > 0) {
				resultSet = s.executeQuery(sqlClause);
			}

			if (activeTablesSize > 0 && null != resultSet) {

				while (resultSet.next()) {

					for (int i = 0; i < activeTablesSize; i++) {

						final Long startTime = ptc.getStartTime((String) activeTables.get(i));
						final Long endTime = ptc.getEndTime((String) activeTables.get(i));
						final Date tableDate = resultSet.getDate(1);

						if (tableDate.getTime() >= startTime && tableDate.getTime() < endTime) {

							if (!tableList.contains(activeTables.get(i))) {
								tableList.add(activeTables.get(i));
							}
						}
					}

				}
			}
		} finally {

			try {
				if (resultSet != null) {
					resultSet.close();
				}
			} catch (Exception e) {

			}

			try {
				if (s != null) {
					s.close();
				}
			} catch (Exception e) {

			}
		}

		return tableList;
	}

}
