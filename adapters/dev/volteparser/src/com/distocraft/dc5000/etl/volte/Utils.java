package com.distocraft.dc5000.etl.volte;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ericsson.eniq.repository.ETLCServerProperties;

import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;

public class Utils {
	
	public ETLCServerProperties etlcserverprops;
	
	private boolean multiBlade;
	private static Logger log;
	
	public Utils(){
		
	}
	
	public Utils(Logger log) {
		Utils.log = log;
	}
	
	public void loadProperties() throws IOException{
		
		etlcserverprops =  new ETLCServerProperties(System.getProperty(ETLCServerProperties.CONFIG_DIR_PROPERTY_NAME)+"/ETLCServer.properties");
				
		try(Scanner scan = new Scanner(new FileReader(System.getProperty(ETLCServerProperties.CONFIG_DIR_PROPERTY_NAME)+"/service_names"))){	
			while(scan.hasNext()){
				if(scan.next().contains("dwh_reader_2")){
					multiBlade = true;
					break;
				}
			}
		}catch (IOException e) {
			log.warning("Could not find the server type. Server will be considered as standalone.");
		}
		
		RockFactory etlrep = null;
		ResultSet result = null;
		
		try{
			log.finest("Multiblade status --- "+multiBlade);
			
			if(multiBlade){
				etlrep = new RockFactory(etlcserverprops.getProperty(ETLCServerProperties.DBURL), etlcserverprops.getProperty(ETLCServerProperties.DBUSERNAME),etlcserverprops.getProperty(ETLCServerProperties.DBPASSWORD), etlcserverprops.getProperty(ETLCServerProperties.DBDRIVERNAME),"NetAnVolteParser",false);
				result = executeQuery(etlrep, "select username, connection_string, password, driver_name, connection_name from META_DATABASES where (connection_name = 'dwhrep' or connection_name = 'dwh_reader_2') and type_name = 'USER'");
				loadDbProps(result);
			}else{
				etlrep = new RockFactory(etlcserverprops.getProperty(ETLCServerProperties.DBURL), etlcserverprops.getProperty(ETLCServerProperties.DBUSERNAME),etlcserverprops.getProperty(ETLCServerProperties.DBPASSWORD), etlcserverprops.getProperty(ETLCServerProperties.DBDRIVERNAME),"NetAnVolteParser",false);
				result = executeQuery(etlrep, "select username, connection_string, password, driver_name, connection_name from META_DATABASES where (connection_name = 'dwhrep' or connection_name = 'dwh') and type_name = 'USER'");
				loadDbProps(result);
			}
		} catch (SQLException | RockException e) {
			log.warning("Could not get database login details.");
		}finally{
			try {
				if (result != null)
					result.close();
				if (etlrep != null)
					etlrep.getConnection().close();
			} catch (SQLException e) {
				e.printStackTrace();
			}	
		}
	}
	
	private void loadDbProps(ResultSet result){
		try{
			while(result.next()){
				if(result.getString("connection_name").contentEquals("dwhrep")){
					etlcserverprops.put("repdb_username", result.getString("username"));
					etlcserverprops.put("repdb_password", result.getString("password"));
					etlcserverprops.put("repdb_driver", result.getString("driver_name"));
					etlcserverprops.put("dbUrl_repdb", result.getString("connection_string"));
				}else{
					etlcserverprops.put("dwhdb_username", result.getString("username"));
					etlcserverprops.put("dwhdb_password", result.getString("password"));
					etlcserverprops.put("dwhdb_driver", result.getString("driver_name"));
					etlcserverprops.put("dbUrl_dwhdb", result.getString("connection_string"));
				}
			}
		}catch(Exception e){
			log.warning("Unable to set DB properties.");
		}
		
		log.config(etlcserverprops.getProperty("repdb_username")+" --- "+etlcserverprops.getProperty("repdb_password")+" --- "+etlcserverprops.getProperty("repdb_driver")+" --- "+etlcserverprops.getProperty("dbUrl_repdb"));
		log.config(etlcserverprops.getProperty("dwhdb_username")+" --- "+etlcserverprops.getProperty("dwhdb_password")+" --- "+etlcserverprops.getProperty("dwhdb_driver")+" --- "+etlcserverprops.getProperty("dbUrl_dwhdb"));
	}
	
	public void loadParameters(RockFactory dwhdb, String configTable){
		
		ResultSet result = null;
		
		String sql = "select * from "+configTable;
		
		try{
			result = executeQuery(dwhdb, sql);
			while(result.next()){
				etlcserverprops.put(result.getString("PARAMETER"), result.getString("VALUE"));
			}
			log.config(etlcserverprops.toString());
		}catch(Exception e){
			log.log(Level.WARNING, "Could not get parameters");
		}finally{
			try {
				result.close();
			} catch (SQLException e) {
				log.warning("Could not close resultset for parameters. "+ e);
			}
		}
	}
	
	public RockFactory getDBConn(String dbType) throws SQLException, RockException{
		
		if(dbType.contentEquals("dwhdb")){
			return new RockFactory(etlcserverprops.getProperty("dbUrl_dwhdb"), etlcserverprops.getProperty("dwhdb_username"), etlcserverprops.getProperty("dwhdb_password"), etlcserverprops.getProperty("dwhdb_driver"), "NetAnVolteParser", false);
		}else if(dbType.contentEquals("repdb")){
			return new RockFactory(etlcserverprops.getProperty("dbUrl_repdb"), etlcserverprops.getProperty("repdb_username"), etlcserverprops.getProperty("repdb_password"), etlcserverprops.getProperty("repdb_driver"), "NetAnVolteParser", false);
		}
		return null;
	}
	
	public static ResultSet executeQuery(RockFactory dbconn, String sql){
		ResultSet result = null;
		try {
			result = dbconn.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY).executeQuery(sql);
		} catch (SQLException e) {	
			log.warning("Could not retrieve data. "+sql);
		}
		return result;
	}
	
	public void createDataFile(String path){
		BufferedWriter bw = null;
		try {
			DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmm");
			Calendar cal = Calendar.getInstance();
			
			String date = dateFormat.format(cal.getTime());

			File file = new File(path, date+".txt");

			if (!file.exists()) {
				file.createNewFile();
			}

			bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
			bw.write(date);
			
		} catch (IOException e) {
			log.log(Level.WARNING, "Unable to write data file", e);
		} finally{
			try {
				if(bw != null)
					bw.close();
			} catch (IOException e) {
				log.warning("Could not close buffered writer.");
			}
		}
	}
	
	public String calculatePreviousRopTime(){
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		Calendar cal = Calendar.getInstance();
		
		int minutes = cal.get(Calendar.MINUTE);
		minutes = minutes - (minutes%5) - 45;
		cal.set(Calendar.MINUTE, minutes);
		cal.clear(Calendar.SECOND);
		return dateFormat.format(cal.getTime());	
	}
	
	public static String getPartitions(RockFactory repdb, String query) throws SQLException{
		
		String tableName = "";
		String queryWithPartition = "";
		String partitionQuery = "";
		ResultSet result = null;
		int count = 0;
		String storageID = "";
		String partition = "";
		
		Pattern p = Pattern.compile("DC_E_.*?_RAW");
		Matcher m = p.matcher(query.toString());
		
		while(m.find()){
			tableName = m.group();
		}
		
		tableName = tableName.replace("_RAW", ":RAW");
		
		partitionQuery = "select * from dwhpartition where CURRENT DATE between starttime and endtime AND STORAGEID in ('"+tableName+"')";
		
		result = Utils.executeQuery(repdb, partitionQuery);
		
		count = 1;
		
		while(result.next()){
			
			storageID = result.getString("STORAGEID").replace(":RAW", "_RAW");
			partition = result.getString("TABLENAME");
			
			if (count > 1){
				queryWithPartition = queryWithPartition.concat(" UNION ");
			}
			
			queryWithPartition += query.replace(storageID, partition);
			
			count++;
			
		}
		
		result.close();
		
		return queryWithPartition;
		
	}

	
}