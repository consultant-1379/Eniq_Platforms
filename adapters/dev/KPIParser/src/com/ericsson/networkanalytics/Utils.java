package com.ericsson.networkanalytics;

import java.io.BufferedWriter;
import java.io.File;
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
				etlrep = new RockFactory(etlcserverprops.getProperty(ETLCServerProperties.DBURL), etlcserverprops.getProperty(ETLCServerProperties.DBUSERNAME),etlcserverprops.getProperty(ETLCServerProperties.DBPASSWORD), etlcserverprops.getProperty(ETLCServerProperties.DBDRIVERNAME),"NetAnKPIParser",false);
				result = executeQuery(etlrep, "select username, connection_string, password, driver_name, connection_name from META_DATABASES where (connection_name = 'dwhrep' or connection_name = 'dwh_reader_2') and type_name = 'USER'");
				loadDbProps(result);
			}else{
				etlrep = new RockFactory(etlcserverprops.getProperty(ETLCServerProperties.DBURL), etlcserverprops.getProperty(ETLCServerProperties.DBUSERNAME),etlcserverprops.getProperty(ETLCServerProperties.DBPASSWORD), etlcserverprops.getProperty(ETLCServerProperties.DBDRIVERNAME),"NetAnKPIParser",false);
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
			return new RockFactory(etlcserverprops.getProperty("dbUrl_dwhdb"), etlcserverprops.getProperty("dwhdb_username"), etlcserverprops.getProperty("dwhdb_password"), etlcserverprops.getProperty("dwhdb_driver"), "NetAnKPIParser", false);
		}else if(dbType.contentEquals("repdb")){
			return new RockFactory(etlcserverprops.getProperty("dbUrl_repdb"), etlcserverprops.getProperty("repdb_username"), etlcserverprops.getProperty("repdb_password"), etlcserverprops.getProperty("repdb_driver"), "NetAnKPIParser", false);
		}
		return null;
	}
	
	public static ResultSet executeQuery(RockFactory dbconn, String sql){
		ResultSet result = null;
		try {
			result = dbconn.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY).executeQuery(sql);
		} catch (SQLException e) {
			log.log(Level.WARNING, "Could not retrieve data. "+sql, e);
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
	
	public int getNodeId(RockFactory dbconn, String topologyTable, String column){
		int nodeidcount = 0;
		ResultSet result = null;		
		String sql = "select max("+column+") as max_id from "+topologyTable;		
		
		result = executeQuery(dbconn, sql);
		
		try {
			while(result.next()){
				nodeidcount = Integer.parseInt(result.getString("max_id"));
			}
		} catch (SQLException e) {
			log.log(Level.WARNING, "Could not get node count.", e);
		}finally{
			if(result != null)
				try {
					result.close();
				} catch (SQLException e) {
					log.warning("Could not close resultset for node count.");
				}
		}
		return nodeidcount;
	}
	
	
	public HashMap<String, String> getActiveThresholds(RockFactory dwhdb, String kpiTable) throws SQLException{
		ResultSet result = null;
		String sql = "select KPI_ID, THRESHOLD, THRESHOLD_TYPE from "+kpiTable+" where STATUS = 'ACTIVE'";
		
		HashMap<String, String> thresholds = new HashMap<>();
		result = executeQuery(dwhdb, sql);
		try{
			while(result.next()){
				thresholds.put(result.getString("KPI_ID"), result.getString("THRESHOLD")+":"+result.getString("THRESHOLD_TYPE"));
			}
		}finally{
			if(result != null)
				result.close();
		}
		
		return thresholds;
	}
	
	public ArrayList<String> getActiveKPIs(RockFactory dwhdb, String kpiTable) throws SQLException{
		ResultSet result = null;
		String sql = null;
		ArrayList<String> activeKPIs = new ArrayList<String>();
		if(kpiTable.toLowerCase().contains("energy")){
			sql = "select MEASURE_ID from "+kpiTable+" where STATUS = 'ACTIVE'";
			result = executeQuery(dwhdb, sql);
			try{
				while(result.next()){
					activeKPIs.add(result.getString("MEASURE_ID"));
				}
			}finally{
				if(result != null)
					result.close();
			}
		}else{
			sql = "select KPI_ID from "+kpiTable+" where STATUS = 'ACTIVE'";
			result = executeQuery(dwhdb, sql);
			try{
				while(result.next()){
					activeKPIs.add(result.getString("KPI_ID"));
				}
			}finally{
				if(result != null)
					result.close();
			}
		}		
		return activeKPIs;
	}
	
	public String modifyQueryWithPartitions(String query, HashMap<String, Set<String>> partitions){
		
		for(Map.Entry<String, Set<String>> entry : partitions.entrySet()){
			String tableView = entry.getKey();
			Object[] tablePartitions = entry.getValue().toArray();
			String partitionQuery = "";
			
			for(int i = 0; i < tablePartitions.length; i++){
				partitionQuery += "SELECT * FROM "+tablePartitions[i];
				for(int j = i; j < tablePartitions.length-1; j++){
					partitionQuery += " UNION ALL ";
				}
			}
			
			query = query.replace(tableView, "("+partitionQuery+")");
		}
		
		return query;
		
	}
	
	public HashMap<String, Set<String>> getPartitions(RockFactory repdb, List<String> queries) throws SQLException{
		
		HashMap<String,Set<String>> tablePartitions = new HashMap<>();
		
		Pattern p = Pattern.compile("DC_E_.*?_RAW");
		Matcher m = p.matcher(queries.toString());
		
		while(m.find()){
			
			tablePartitions.put(m.group(), new HashSet<String>());
			
		}
		
		String tableNames = tablePartitions.keySet().toString();
		tableNames = tableNames
				.substring(tableNames.indexOf("[")+1, tableNames.indexOf("]"))
				.replace(", ", "', '")
				.replace("_RAW", ":RAW");
		String partitionQuery = "SELECT * FROM DWHPARTITION WHERE CURRENT DATE BETWEEN STARTTIME AND ENDTIME AND STORAGEID in ('"+tableNames+"')";
		ResultSet result = Utils.executeQuery(repdb, partitionQuery);
		
		while(result.next()){
			
			String storageID = result.getString("STORAGEID").replace(":RAW", "_RAW");
			String partition = result.getString("TABLENAME");
			Set<String> partitions;
			if(tablePartitions.containsKey(storageID)){
				partitions = tablePartitions.get(storageID);
				partitions.add(partition);
				
				tablePartitions.put(storageID, partitions);
			}
		}
		
		result.close();
		return tablePartitions;
		
	}
	
	public HashMap<String, String> getParitionTableNames(RockFactory repdb, String datetime, HashMap<String, String> queryProperties) throws SQLException{
		ResultSet result = null;
		String sql = "select tablename from dwhpartition where CURRENT DATE between starttime and endtime AND STORAGEID in ( $TABLENAMES )";
		
		HashMap<String, String> partitionTableNames = new HashMap<>();
		
		for (Map.Entry<String, String> entry : queryProperties.entrySet()) {
		    String tablenames = entry.getValue();
		    String sqlQuery = sql.replace("$TABLENAMES", tablenames);
		    
		    result = executeQuery(repdb, sqlQuery);
		    String partitionNames = "";
		    try{
				while(result.next()){
					partitionNames = partitionNames + "," + result.getString("tablename");
				}
			}finally{
				if(result != null)
					result.close();
			}
		    
		    partitionTableNames.put(entry.getKey(), partitionNames);
		}
		
		return partitionTableNames;

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
	
	public static String calculatePreviousDay(String datetime_id){
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		Calendar cal = Calendar.getInstance();
		
		int hours = cal.get(Calendar.HOUR_OF_DAY);
		hours = hours - 24;
		cal.set(Calendar.HOUR_OF_DAY, hours);
		
		int minutes = cal.get(Calendar.MINUTE);
		minutes = minutes - (minutes%5) - 45;
		cal.set(Calendar.MINUTE, minutes);
		cal.clear(Calendar.SECOND);
		
		return dateFormat.format(cal.getTime());	
	}
	
	public String calculatePreviousHour(String datetime_id) throws ParseException{
		DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(dateTimeFormat.parse(datetime_id));
		
		int hours = cal.get(Calendar.HOUR_OF_DAY);
		hours = hours - 1;
		cal.set(Calendar.HOUR_OF_DAY, hours);
		return dateTimeFormat.format(cal.getTime());	
	}

	public HashMap<String, String> checkRequiredThresholds(String time, HashMap<String, String> activeKpiThresholds ) throws ParseException{
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    		Date date = dateFormat.parse(time);
    		Calendar c = Calendar.getInstance();
   	 	c.setTime(date);
    		int minutes = c.get(Calendar.MINUTE);
    		if (minutes%15!=0){
    			activeKpiThresholds.remove("1");
    			activeKpiThresholds.remove("2");
    			activeKpiThresholds.remove("3");
    			activeKpiThresholds.remove("4");
    			activeKpiThresholds.remove("5");
    			activeKpiThresholds.remove("9");
    			activeKpiThresholds.remove("10");
    		}
		return activeKpiThresholds;
		
	}
	
	public static int isBreach(int thresholdType, double threshold, double kpiValue){
		
		switch(thresholdType){
		case 1 :
			if (kpiValue < threshold){
				return 1;
			}else{
				return 0;
			}
			
		case 0:
			if (kpiValue > threshold){
				return 1;
			}else{
				return 0;
			}
			
		default: return 0;
		}
	}
	
	public HashMap<String, String> getNodeTopologyInfo(
			RockFactory dwhdb, String topologyTable) throws SQLException {
		
		ResultSet result = null;
		HashMap<String, String> topologyDetails = new HashMap<>(); 
		String sql = "select NODE_ID, NODE_NAME from "+topologyTable;		
		
		result = executeQuery(dwhdb, sql);
		try{
			while(result.next()){
				topologyDetails.put(result.getString("NODE_NAME"), result.getString("NODE_ID"));
			}
		}finally{
			if(result != null)
				result.close();
		}
		return topologyDetails;
	}

	public HashMap<String, String> getTopologyInfo(
			RockFactory dwhdb, String KeyColumn, String ValueColumn1, String KeyColumn2, String topologyTable) throws SQLException {

			ResultSet result = null;
            String sql=null;
            String key = null;
            HashMap<String, String > topologyDetails = new HashMap<>(); 
	

        	sql = "select "+KeyColumn+", " +ValueColumn1+", " +KeyColumn2+" from "+topologyTable+" where STATUS = 'ACTIVE'";    
        	result = executeQuery(dwhdb, sql);
        	try{
               while(result.next()){         
            	    key = result.getString(KeyColumn)+result.getString(KeyColumn2);          	    
                    topologyDetails.put(key, result.getString(ValueColumn1)) ;
               }
        	}finally{
               if(result != null)
                     result.close();
        	}
        	return topologyDetails;
	}
	
	public HashMap<String, String> getRelationsTopologyInfo(
			RockFactory dwhdb, String KeyColumn, String ValueColumn1, String KeyColumn2, String KeyColumn3,
			String topologyTable) throws SQLException {

		ResultSet result = null;
        String sql=null;
        String key = null;
        HashMap<String, String > topologyDetails = new HashMap<>(); 
	
        sql = "select "+KeyColumn+", " +ValueColumn1+", " +KeyColumn2+" , " +KeyColumn3+" from "+topologyTable+" where STATUS = 'ACTIVE'";
        
        result = executeQuery(dwhdb, sql);
        try{
               while(result.next()){          	     
            	    key = result.getString(KeyColumn)+result.getString(KeyColumn2)+result.getString(KeyColumn3);           	    
                    topologyDetails.put(key, result.getString(ValueColumn1)) ;
               }
        }finally{
               if(result != null)
                     result.close();
        }
        return topologyDetails;
	}
	
	public String calculateAdjCell(String adjCell, String cellRef) {
		if (adjCell==null || adjCell==""){
			if(cellRef!=null){
				String[] data=cellRef.split("=");	
				return data[5];
			}
		}
		return adjCell;
	}
}
