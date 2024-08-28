package com.ericsson.eniq.backuprestore.backup;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.distocraft.dc5000.repository.cache.*;

import com.distocraft.dc5000.etl.engine.structure.TransferActionBase;
import com.ericsson.eniq.common.RemoteExecutor;
import com.ericsson.eniq.common.lwp.LwProcess;
import com.ericsson.eniq.common.lwp.LwpOutput;
import com.jcraft.jsch.JSchException;

import ssc.rockfactory.RockFactory;
/**
 * This class takes a backup of previous day aggregated data and create a zipped file.
 * 
 * @author xsarave
 * 
 */
public class BackupAggregatedData extends TransferActionBase implements Runnable {

	private DBConnect utils;
	
	private RockFactory dwhdb;
	private RockFactory repdb;
	private RockFactory etlrep;
	private Logger log;
	private String password;
	private Map<String, ArrayList<String>> aggregationListFromStatus = new HashMap<String, ArrayList<String>>();
	private String currentDate;

	private String partitonForStatusTable;
	private Map<String, ArrayList<String>> aggregationSetFromStatusWithPartition = new HashMap<String, ArrayList<String>>();
	private String sessionTables = "LOG_AGGREGATIONSTATUS";
	private static final String dbisqlPath = "bash /eniq/sw/bin/backupaggregation.bsh";
	public static final String fileName = "/eniq/sw/bin/back.sql";
	private PrintWriter printWriter;
	private List<String> filelist = new ArrayList<String>();

	
	/**
	* This function is the constructor of this BackupAggregatedData class.
	* 
	* @param parentlog
	* @param etlrep
	* 
	* 
	 */
	public BackupAggregatedData(Logger parentlog, RockFactory etlrep) {
		this.etlrep = etlrep;
		this.log = Logger.getLogger(parentlog.getName() + ".BackupAggregatedData");
	}

	
	/**
	 * This function Connect to Log_AggregationStatus to find the list of Aggreagtions that
	 *  are successfully aggregatted today...
	 * @param partitionName
	 * 
	 */
	
	public void retriveFromLogAggrStatusTable(String partitionName) {

		String query = "select AGGREGATION,TYPENAME,DATADATE from " + partitionName
				+ " where status ='aggregated' and TIMELEVEL IN('day','count','daybh','rankbh') and convert(date,INITIAL_AGGREGATION)=(CURRENT DATE-1) or convert(date,LAST_AGGREGATION)=(CURRENT DATE-1)";
		log.finest("sql query to get the details from log_aggregationstatus table:" + query);
		ResultSet result = DBConnect.executeQuery(dwhdb, query);
		try {

			while (result.next()) {

				String tempAggregation = result.getString("AGGREGATION");
				String tempTypename = result.getString("TYPENAME");
				String tempDate = result.getString("DATADATE");

				if (BackupConfigurationCache.getCache().isBackupActive(tempTypename)) {
					
					directoryCreation(tempTypename);
					String subdirectory = tempAggregation.substring(tempTypename.length() + 1).trim();
					
					if (subdirectory.contains("_")) {

						subdirectory = tempAggregation.substring(tempTypename.length() + 1,
								tempAggregation.indexOf('_', tempTypename.length() + 1));
						subdirectory=subdirectory.trim();
					}
					if(subdirectory.equals("DAY")||subdirectory.equals("RANKBH")||subdirectory.equals("COUNT")||subdirectory.equals("DAYBH")){
						directoryCreation(tempTypename + "/" + subdirectory);
						ArrayList<String> list = new ArrayList<String>();
						list.add(tempTypename);
						list.add(tempDate);
						try {
							aggregationListFromStatus.put(tempAggregation, list);
						} catch (Exception e) {
							log.info("exception:" + e);
						}

						log.fine("aggregation set name:" + tempAggregation + " tempTypename:" + tempTypename + " datadate:"
							+ tempDate);
					}
					
					
				}
			}
		} catch (Exception e) {
			log.info("Exception occured.." + e);
		}

	}
	/**
	 * This function Create a list of Queries that needs to be executed to get the backup of
	 *  DAY tables..
	 * 
	 */
	
	public void listOfAggregationQueries() {
		try {

			for (String key : aggregationSetFromStatusWithPartition.keySet()) {
				String aggregationName = key;
				ArrayList list = aggregationSetFromStatusWithPartition.get(aggregationName);
				String filename = exportDataFromTable(aggregationName, list, dwhdb, log, utils);
				filelist.add(filename);

			}

		} catch (Exception e) {
			log.finest(e.getMessage());
		}

	}
	
	
	/**
	 * Create a directory structure for the aggregator files...
	 * @param subdirectory
	 */
	 
	public void directoryCreation(String subdirectory) {
		try {
			String filename = "/eniq/flex_data_bkup/" + subdirectory + "/";
			log.fine("File creation path:" + filename);
			File theDir = new File(filename);

			// if the directory does not exist, create it
			if (!theDir.exists()) {
				log.info("creating directory: " + filename);
				boolean result = false;

				try {
					theDir.mkdir();
					result = true;
				} catch (Exception se) {
					log.log(Level.SEVERE, "Error in creating Directory: " + filename);
				}
				if (result) {
					log.finest("Directory " + filename + " created successfully");
				}
			}
		} catch (Exception e) {
			log.info("Exception occured during directory creation..");
		}

	}

	
	/**
	 * This function cretaes the export query
	 * @param tableNameWithPartition
	 * @param list
	 * @param dwhdb
	 * @param log
	 * @param utils
	 * @return String
	 * 
	 */
	public String exportDataFromTable(String tableNameWithPartition, ArrayList<String> list, RockFactory dwhdb,
			Logger log, DBConnect utils) {
		// It has to be Export Query
		String tableName = null;
		String dataDate = null;
		tableName = list.get(0);
		dataDate = list.get(1);
		String subdirectory = tableNameWithPartition.substring(tableName.length() + 1).trim();
		if (subdirectory.contains("_")) {
			subdirectory = tableNameWithPartition.substring(tableName.length() + 1,
					tableNameWithPartition.indexOf('_', tableName.length() + 1));
			subdirectory=subdirectory.trim();
		}

		String filename = "/eniq/flex_data_bkup/" + tableName + "/" + subdirectory + "/" + tableNameWithPartition + "_"+ dataDate + ".txt";
		String query = "Select * from " + tableNameWithPartition + " where date_id ='" + dataDate + "'; OUTPUT TO  '"+ filename + "' FORMAT TEXT WITH COLUMN NAMES DELIMITED BY '\\x09';";
		log.finest("sql query to backup aggregation data and store it in a file:" + query);
		PrintWriter printWriter = null;
		File file = new File(fileName);
		try {
			if (!file.exists())
				file.createNewFile();
			printWriter = new PrintWriter(new FileOutputStream(fileName, true));
			printWriter.write(query + "\n");
		} catch (IOException ioex) {
			ioex.printStackTrace();
			log.log(Level.SEVERE, "Exception creating " + filename + " file", ioex);
		} finally {
			if (printWriter != null) {
				printWriter.flush();
				printWriter.close();
			}
		}

		return filename;

	}

	
	/**
	 * This function runs the backupaggregation.bsh script using LWPOutput
	 * 
	 */
	public void executeSql() {
		
		try {
			final LwpOutput dbisqlresult = LwProcess.execute(dbisqlPath, true, log);
			log.log(Level.FINEST, "Executing the script " + dbisqlPath + " to perform dbisql operation.");
			if (dbisqlresult.getExitCode() != 0) {
				log.log(Level.WARNING, "Error executing the dbisql command " + dbisqlresult);
			}
		} catch (Exception e) {
			log.severe("Exception while running dbisql command " + e.getMessage());
		}

	}
	
	
	/**
	 * This function get the database connection from dbconnect and take the backup.
	 * 
	 */
	public void aggregate() {

		utils = new DBConnect(log);
		try {
			log.finest("Etlrep connection details::"+etlrep);
			utils.loadProperties(etlrep);
			dwhdb = utils.getDBConn("dwhdb");
			repdb = utils.getDBConn("repdb");
			// Find the partition of Log_Session_Aggregation and
			// log_AggreagtionStatus table
			partitonForStatusTable = (utils.getParitionTableNames(repdb, sessionTables));
			log.info("Partition name of log_AggreagtionStatus is:"+partitonForStatusTable);

			// find the aggregator names that are successfully aggregated for
			// various dates
			retriveFromLogAggrStatusTable(partitonForStatusTable);

			// Find the partitions for the Aggregated tables
			aggregationSetFromStatusWithPartition = utils.getParitionTableNames(repdb, aggregationListFromStatus);

			// Get the list of Export Queries from the Map
			listOfAggregationQueries();
			log.info("Executing the query...");
			executeSql();
			log.info("Trying to zip the aggegated backup files..");
			utils.zippedFile(filelist);
			log.info("Execution of Backup aggregated data completed..");
			// Execute the Queries and load the resultset to corresponding dir

			// Take the back up log_session_aggregator and
			// log_aggregationstatus--- get to know the directory

		} catch (SQLException e) {
			log.info("SQLException Occured :" + e.getMessage());
		} catch (Exception e) {
			log.info("Exception Occured: " + e.getMessage());
		} 
		finally {
			try {
				
				dwhdb.getConnection().close();
				repdb.getConnection().close();
			} catch (SQLException e) {
				log.info("Exception while closing the connection of RockFactory object:"+e);
				e.printStackTrace();
			}
		}

	}


	@Override
	public void run() {
		// TODO Auto-generated method stub
		log.info("Started Aggregation Back up");
		if (BackupConfigurationCache.getCache().isBackupStatus()
				&& (BackupConfigurationCache.getCache().getBackupLevel().equals("AGGREGATED")
						|| BackupConfigurationCache.getCache().getBackupLevel().equals("BOTH"))) {
			
			aggregate();
		}

		
	}

}
