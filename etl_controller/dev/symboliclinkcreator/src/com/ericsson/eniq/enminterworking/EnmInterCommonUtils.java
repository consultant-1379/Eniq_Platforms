/**
 * 
 */
package com.ericsson.eniq.enminterworking;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ericsson.eniq.common.DatabaseConnections;

import ssc.rockfactory.RockFactory;

/**
 * @author xarjsin
 *
 */
public class EnmInterCommonUtils {
	private static Logger log;
	
	public static String getEngineHostname() {
		log = Logger.getLogger("symboliclinkcreator.nat");
		String hostname = null;
		String engineHostName = null;
		try {
			Scanner scan = new Scanner(new FileReader("/eniq/sw/conf/service_names"));
			while(scan.hasNext()){
				engineHostName = scan.next();
				if( engineHostName.contains("engine")){
					hostname = parsePattern( engineHostName,".+::(.+)::.+");
					log.finest("Engine host name:"+hostname);
				}
			}
			scan.close();
		} catch (FileNotFoundException e1) {
		log.warning("Cannot read file, treating as standalone");
			return System.getenv("hostname");
		}
		return hostname;
	}
	
	public static String getEngineIP() {
		log = Logger.getLogger("symboliclinkcreator.nat");
		String ip = null;
		String engineIP = null;
		try {
			Scanner scan = new Scanner(new FileReader("/eniq/sw/conf/service_names"));
			while(scan.hasNext()){
				engineIP = scan.next();
				if( engineIP.contains("engine")){
					ip = parsePattern( engineIP,"(.+)::.+::.+");
					log.finest("Engine IP Address = " + ip );
				}
			}
			scan.close();
		} catch (FileNotFoundException e1) {
			log.warning("Cannot read file, treating as standalone");
		}
		return ip;
	}
	
	private static String parsePattern(String str, String regExp) {
		log = Logger.getLogger("symboliclinkcreator.nat");
		final Pattern pattern = Pattern.compile(regExp);
		final Matcher matcher = pattern.matcher(str);
		String result = "";

		// Find a match between regExp and return group 1 as result.
		if (matcher.find()) {
			result = matcher.group(1);
			log.finest(" regExp (" + regExp + ") found from subset of " + str + "  :" + result);
		} else {
			log.warning("No subset of String " + str + " matchs defined regExp " + regExp);
		}
		return result;
	}
	
	public static String getSelfRole(){
		log = Logger.getLogger("symboliclinkcreator.nat");
		String role = null;
		RockFactory rockCon = null;
		ResultSet result = null;
		ResultSet count = null;
		String countSql = "Select count(*) from RoleTable";
		String roleSql = "Select ROLE from RoleTable where ENIQ_ID = '" + getEngineHostname() 
		+ "' and IP_ADDRESS = '" + getEngineIP() + "'";
		try
		{
			rockCon = DatabaseConnections.getDwhRepConnection();
			count = rockCon.getConnection().createStatement().executeQuery(countSql);
			if(count.next()){
				log.finest("Number of rows - " + count.getString(1));			
				if(count.getString(1).equals("0"))
				{
					role = "UNASSIGNED";
				}
				else
				{
					result = rockCon.getConnection().createStatement().executeQuery(roleSql);
					if(result.next()){
						role = result.getString("ROLE");
					}
					else {
						role = "UNDEFINED OR COULD NOT RETRIEVE ROLE";
					}
				}
			}
			else {
				role = "UNDEFINED OR COULD NOT RETRIEVE ROLE";
			}
		}
		catch (Exception e)
		{
			log.warning( "Exception at getSelfRole method: " + e);
		}
		finally {
		      // finally clean up
		      try {
		        if (result != null) {
		        	result.close();
		        }
		        if (rockCon.getConnection() != null) {
		        	rockCon.getConnection().close();
			        }
		      } catch (SQLException e) {
		    	  log.warning( "Exception at getSelfRole method finally clause: " + e);
		      }
		    }
	return role;
	}
	
	public static List<String> getSlaveIPList() {
		List<String> slaveList = new ArrayList<String>();
		RockFactory rockCon = null;
		ResultSet slaveRS = null;
		String getSlaveSql = "SELECT IP_ADDRESS FROM ROLETABLE WHERE ROLE='SLAVE'";
		try{
			rockCon = DatabaseConnections.getDwhRepConnection();
			slaveRS = rockCon.getConnection().createStatement().executeQuery(getSlaveSql);
			while(slaveRS.next()){
				slaveList.add(slaveRS.getString("IP_ADDRESS"));
			}
		}
		catch(Exception e){
			log.warning( "Exception at getSlaveIPList method: " + e);
		}
		finally {
		      // finally clean up
		      try {
		        if (slaveRS != null) {
		        	slaveRS.close();
		        }
		        if (rockCon.getConnection() != null) {
		        	rockCon.getConnection().close();
			        }
		      } catch (SQLException e) {
		    	  log.warning( "Exception at getSlaveIPList method finally clause: " + e);
		      }
		    }
		return slaveList;
	}

	public static String getMasterIP() {
		String masterIP = null;
		RockFactory rockCon = null;
		ResultSet masterRS = null;
		String getSlaveSql = "SELECT IP_ADDRESS FROM ROLETABLE WHERE ROLE='MASTER'";
		try{
			rockCon = DatabaseConnections.getDwhRepConnection();
			masterRS = rockCon.getConnection().createStatement().executeQuery(getSlaveSql);
			if(masterRS.next()){
				masterIP = masterRS.getString("IP_ADDRESS");
			}
		}
		catch(Exception e){
			log.warning( "Exception at getSlaveIPList method: " + e);
		}
		finally {
		      // finally clean up
		      try {
		        if (masterRS != null) {
		        	masterRS.close();
		        }
		        if (rockCon.getConnection() != null) {
		        	rockCon.getConnection().close();
			        }
		      } catch (SQLException e) {
		    	  log.warning( "Exception at getSlaveIPList method finally clause: " + e);
		      }
		    }
		return masterIP;
	}

}
