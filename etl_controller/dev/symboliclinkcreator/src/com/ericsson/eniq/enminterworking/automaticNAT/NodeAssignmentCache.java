package com.ericsson.eniq.enminterworking.automaticNAT;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.ericsson.eniq.enminterworking.utilities.DbUtilities;

import ssc.rockfactory.RockFactory;


public class NodeAssignmentCache {

	public Logger log;
	public static int noOfServers = 0;
	public static RockFactory etlrep = null;
	public static List<Eniq_Role> roleTableContents = null;
	public static Map<String, ArrayList<String>> technologyNodeMap = null;
	public static Map<String, ArrayList<String>> policyCriteriaMap = null;

	public NodeAssignmentCache() throws SQLException {

//		log = Logger.getLogger("symboliclinkcreator.nat");
		// Check for the number of servers configured as part of Multi ES
		// functionality -- CACHE the contents of RoleTable
		noOfServers = checkNoOfServers();

		populateTechnologyNodeMap();

//		log.finest("Node Assignment Cache :"+technologyNodeMap.toString());
		
		//TODO To be removed.. Added only for testing purposes
		//new AssignNodes(nodeType, nodeFDN);

	}


	/*public static void main(String[] args) throws SQLException {

		//System.out.println("Input is " + [0] + "  -" + args[1]);
		new NodeAssignmentCache();
	}*/

	public static int checkNoOfServers() {
//		log = Logger.getLogger("symboliclinkcreator.nat");
		Eniq_Role roleTable = null;
		roleTableContents = new ArrayList<Eniq_Role>();

		// Connect to DB
		etlrep = DbUtilities.connectToEtlrep();
		RockFactory repdb = DbUtilities.connectTodb(etlrep, "dwhrep");
		Connection con = null;
		try {
			con = repdb.getConnection();
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select * from  RoleTable");

			while (rs.next()) {
				policyCriteriaMap = readPolicyCriteriaData(con, repdb, rs.getString(1));

				roleTable = new Eniq_Role(rs.getString(1), rs.getString(2), rs.getString(3), policyCriteriaMap);
				roleTableContents.add(roleTable);
			}

		} catch (Exception e) {
			// Error while connecting to DB
			// TODO throw a UserException
//			log.warning("Exception at checkNoOfServers "+e.getMessage());
		} finally {
			try {
				con.close();
				etlrep = null;
			} catch (SQLException e) {
//				log.warning("Exception at checkNoOfServers finally clause: "+e.getMessage());
			}
		}
		return roleTableContents.size();
	}

	public static Map<String, ArrayList<String>> readPolicyCriteriaData(Connection con, RockFactory repdb, String role) {
		try {
//			log = Logger.getLogger("symboliclinkcreator.nat");
			policyCriteriaMap = new HashMap<String, ArrayList<String>>();

			con = repdb.getConnection();
			Statement stmt = con.createStatement();

			ArrayList<String> alist = null;
			ResultSet rs1 = stmt.executeQuery(
					"select TECHNOLOGY,NAMINGCONVENTION from ENIQS_Policy_Criteria where TECHNOLOGY IN (Select distinct TECHNOLOGY from ENIQS_Policy_Criteria where ENIQ_IDENTIFIER='"
							+ role + "') and ENIQ_IDENTIFIER='" + role + "'");

			while (rs1.next()) {

				if (policyCriteriaMap == null) {
					alist = new ArrayList<String>();
					alist.add(rs1.getString(2));
				} else {
					if (policyCriteriaMap.containsKey(rs1.getString(1))) {
						alist = policyCriteriaMap.get(rs1.getString(1));
					} else {
						alist = new ArrayList<String>();
					}
					alist.add(rs1.getString(2));
				}
				policyCriteriaMap.put(rs1.getString(1), alist);
			}

		} catch (Exception e) {
//			log.warning("Exception at readPolicyCriteriaData Method"+e.getMessage());
		} finally {
//			log.finest("Policy criteria details :"+policyCriteriaMap.toString());
		}
		return policyCriteriaMap;
	}

	public void populateTechnologyNodeMap() {
//		log = Logger.getLogger("symboliclinkcreator.nat");
		BufferedReader br = null;
		
		try {
			String line;
			technologyNodeMap = new HashMap<>();
			br = new BufferedReader(new FileReader(new File ("/eniq/sw/conf/NodeTechnologyMapping.properties")));
			
			while ( (line = br.readLine()) != null) {
				String key = line.substring(0, line.indexOf("-"));
				String values = line.substring(line.indexOf("-")+1, line.length());
				String[] values_array = values.split(",");
				ArrayList<String> list = new ArrayList<String>();
				for(String s : values_array){
					list.add(s);
				}
				
				technologyNodeMap.put(key, list);
			}
		} catch (IOException e) {
//			log.warning("Exception at populateTechnologyNodeMap Method"+e.getMessage());
		} finally {
			try {
				br.close();
			} catch (IOException e) {
//				log.warning("Exception at populateTechnologyNodeMap Method finally clause"+e.getMessage());
			}
		}
	}

	public static List<Eniq_Role> getRoleTableContents() {
		return roleTableContents;
	}
	
	public static int getNoOfServers() {
		return noOfServers;
	}

	public static Map<String, ArrayList<String>> getTechnologyNodeMap() {
		return technologyNodeMap;
	}
	
	
}
