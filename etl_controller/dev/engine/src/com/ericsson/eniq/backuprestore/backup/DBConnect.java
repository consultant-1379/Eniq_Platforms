package com.ericsson.eniq.backuprestore.backup;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;

import com.ericsson.eniq.repository.ETLCServerProperties;

import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;
/**
 * This class connects to dwhrep and dwhdb tables to take the backup 
 * 
 * @author xsarave
 * 
 */
public class DBConnect {

	public ETLCServerProperties etlcserverprops;

	private boolean multiBlade;
	private static Logger log;

	public DBConnect() {

	}
	/**
	* This function is the constructor of this DBConnect class.
	* 
	* @param log
	* 
	* 
	*/
	public DBConnect(Logger log) {
		DBConnect.log = log;
	}

	
	/**
	* This function get the ETLCServerProperties instance and get the dwhdb and dwhrep credentials.
	* 
	* @param etlrep
	* 
	* 
	*/
	public void loadProperties(RockFactory etlrep) throws IOException {

		etlcserverprops = new ETLCServerProperties(
				System.getProperty(ETLCServerProperties.CONFIG_DIR_PROPERTY_NAME) + "/ETLCServer.properties");

		ResultSet result = null;

		try {
			result=etlrep.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY).executeQuery("select username, connection_string, password, driver_name, connection_name from META_DATABASES where (connection_name = 'dwhrep' or connection_name = 'dwh') and type_name = 'USER'");

			loadDbProps(result);

		} catch (Exception e) {
			log.warning("Could not get database login details.");
		} 
		finally {
			
				try {
					if (result != null)
					result.close();
				} catch (SQLException e) {
					log.warning("Exception while trying to close the connection::"+e);
					e.printStackTrace();
				}
		}
	}

	
	/**
	* This function get the dwhdb and dwhrep credentials and store it in etlcserverprops.
	* 
	* @param result
	*
	*/
	private void loadDbProps(ResultSet result) {
		try {
			while (result.next()) {
				if (result.getString("connection_name").contentEquals("dwhrep")) {
					etlcserverprops.put("repdb_username", result.getString("username"));

					etlcserverprops.put("repdb_password", result.getString("password"));

					etlcserverprops.put("repdb_driver", result.getString("driver_name"));
					etlcserverprops.put("dbUrl_repdb", result.getString("connection_string"));
				} else if(result.getString("connection_name").contentEquals("dwh")){
					etlcserverprops.put("dwhdb_username", result.getString("username"));
					etlcserverprops.put("dwhdb_password", result.getString("password"));
					etlcserverprops.put("dwhdb_driver", result.getString("driver_name"));
					etlcserverprops.put("dbUrl_dwhdb", result.getString("connection_string"));
				}
			}
		} catch (Exception e) {
			log.warning("Unable to set DB properties.");
		}

		log.config("repdb username::"+etlcserverprops.getProperty("repdb_username") 
				 +",repdb driver::" + etlcserverprops.getProperty("repdb_driver")
				+ " ,repdb url:: " + etlcserverprops.getProperty("dbUrl_repdb"));
		log.config("dwhdb username::"+etlcserverprops.getProperty("dwhdb_username") + ",dwhdb drivername:: "
				 + etlcserverprops.getProperty("dwhdb_driver")
				+ ",dwhdb url::" + etlcserverprops.getProperty("dbUrl_dwhdb"));
	}

	
	/**
	* This function get RockFactory instance for dwhdb and repdb.
	* 
	* @param dbType
	*
	*/
	public RockFactory getDBConn(String dbType) throws SQLException, RockException {

		if (dbType.contentEquals("dwhdb")) {
			return new RockFactory(etlcserverprops.getProperty("dbUrl_dwhdb"),
					etlcserverprops.getProperty("dwhdb_username"), etlcserverprops.getProperty("dwhdb_password"),
					etlcserverprops.getProperty("dwhdb_driver"), "VolteParser", false);
		} else if (dbType.contentEquals("repdb")) {
			return new RockFactory(etlcserverprops.getProperty("dbUrl_repdb"),
					etlcserverprops.getProperty("repdb_username"), etlcserverprops.getProperty("repdb_password"),
					etlcserverprops.getProperty("repdb_driver"), "VolteParser", false);
		}

		return null;
	}
	
	
	/**
	* This function connects to the databae and executes the query 
	* 
	*@param dbconn
	*@param sql
	*
	*/
	public static ResultSet executeQuery(RockFactory dbconn, String sql) {
		ResultSet result = null;
		try {
			

			result = dbconn.getConnection()
					.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY).executeQuery(sql);

		} catch (SQLException e) {
			log.log(Level.WARNING, "Could not retrieve data. " + sql, e);
		}
		
		return result;
	}

	
	/**
	* This function find the partition for the list of tables that are found from
	* log_session_Aggregator
	* 
	*@param repdb
	*@param aggFromSessionTable
	*@return Map<String, ArrayList<String>>
	*
	*/
	
	public Map<String, ArrayList<String>> getParitionTableNames(RockFactory repdb,
			Map<String, ArrayList<String>> aggFromSessionTable) throws SQLException {
		ResultSet result = null;

		Map<String, ArrayList<String>> aggrListAfterPartition = new HashMap<String, ArrayList<String>>();
		try {

			ArrayList<String> list = new ArrayList<String>();

			for (Map.Entry<String, ArrayList<String>> entry : aggFromSessionTable.entrySet()) {
				log.fine("enter inside for each" + entry.getKey());
				String dataDate = null;
				String tablename = entry.getKey();

				list = entry.getValue();
				dataDate = list.get(1);
				String sql = "select tablename from dwhpartition where '" + dataDate
						+ "' between starttime and endtime AND STORAGEID like '%" + tablename + "'";
				log.finest("sql query to find the partition for aggregated table:" + sql);
				result = executeQuery(repdb, sql);
				try {

					while (result.next()) {

						aggrListAfterPartition.put(result.getString("tablename"), entry.getValue());
					}
				} finally {
					if (result != null)
						result.close();
				}
			}
			log.finest("Aggregated table list:" + aggrListAfterPartition.toString());

		} catch (Exception e) {
			log.info("exception :" + e);
		}
		return aggrListAfterPartition;
	}
	
	
	/**
	* This function find the partition for the status tables
	* 
	*@param repdb
	*@param tableName
	*@return String
	*
	*/
	
	public String getParitionTableNames(RockFactory repdb, String tableName) throws SQLException {
		ResultSet result = null;
		String parttionTableName = "";
		
		String sql = "select tablename from dwhpartition where (CURRENT DATE-1) between starttime and endtime AND STORAGEID in ('"
				+ tableName + ":PLAIN')";
		log.finest("sql query to find the partition for LOG_AGGREGATIONSTATUS table:" + sql);
		result = executeQuery(repdb, sql);
		try {
			while (result.next()) {
				parttionTableName = result.getString("tablename");

			}
		} finally {
			if (result != null)
				result.close();
		}
		log.finest("ParttionTableName for LOG_AGGREGATIONSTATUS table:" + parttionTableName);
		return parttionTableName;
	}

	
	/**
	* This function iterate through the list and zipped the file.
	* 
	*@param filelist
	*
	*/
	public void zippedFile(List<String> filelist) {
		
		for (String filename : filelist) {
			String OUTPUT_GZIP_FILE = filename + ".gz";
			log.finest("destination zip folder:" + OUTPUT_GZIP_FILE);

			byte[] buffer = new byte[1024];

			try {

				GZIPOutputStream gzos = new GZIPOutputStream(new FileOutputStream(OUTPUT_GZIP_FILE));

				FileInputStream in = new FileInputStream(filename);

				int len;
				while ((len = in.read(buffer)) > 0) {
					gzos.write(buffer, 0, len);
				}

				in.close();

				gzos.finish();
				gzos.close();
				File file=new File(filename);
				file.delete();

			} catch (IOException ex) {
				log.info("Exception while zipping the files:" + ex);
			}
		}

	}

}
