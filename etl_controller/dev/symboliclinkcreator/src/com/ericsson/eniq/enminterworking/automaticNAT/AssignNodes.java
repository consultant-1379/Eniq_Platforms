package com.ericsson.eniq.enminterworking.automaticNAT;

import java.rmi.Naming;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.distocraft.dc5000.common.RmiUrlFactory;
import com.ericsson.eniq.enminterworking.EnmInterCommonUtils;
import com.ericsson.eniq.enminterworking.IEnmInterworkingRMI;
import com.ericsson.eniq.enminterworking.utilities.DbUtilities;

import ssc.rockfactory.RockFactory;

public class AssignNodes {

	
	private String node_type;
	private String node_fdn;
	private RockFactory rf_etlrep;
	private RockFactory rf_dwhrep;
	private Connection con;
	private Logger log;
	private List<Eniq_Role> roleTableList = null;
	final String roleSql = "Select IP_ADDRESS from RoleTable where ROLE='SLAVE'";

	public AssignNodes (String node_type, String node_fdn) {
		this.node_fdn = node_fdn;
		this.node_type = node_type;
		log = Logger.getLogger("symboliclinkcreator.nat");
		try{
			roleTableList = NodeAssignmentCache.getRoleTableContents();
		rf_etlrep = DbUtilities.connectToEtlrep();
		rf_dwhrep = DbUtilities.connectTodb(rf_etlrep, "dwhrep");
		con = rf_dwhrep.getConnection();
			
		boolean isAssigned = checkNAT();
		if (!isAssigned) {
			log.info("Checking the policy criteria to assign the node " + node_fdn);
			assignNode();
			} 
			else {
			log.info("FDN " + node_fdn + " is already assigned or unassigned. ");
			}
		} catch (Exception e){
			log.warning("Exception in the AssignNodes Constructor." + e.getMessage());
		}
		finally{
			closeConnections();
		}
	}

	private void assignNode() {
		String whichEniq = null;
		boolean isRmi = false;
		boolean insertIntoSlave = false;
		Statement stmt = null;
		String serverIP = "" ;
		ResultSet rs = null;
		log.finest("roleTableList contents :" + roleTableList.toString());
			if (roleTableList != null && (roleTableList.isEmpty())) {
			
				log.info("Single ENIQ-S configuration. Assigning the node FDN "+ node_fdn +" to ENIQ-S server : "
						+ EnmInterCommonUtils.getEngineHostname());
				whichEniq = EnmInterCommonUtils.getEngineHostname();
			
			try{
				stmt = con.createStatement();
				stmt.execute("insert into ENIQS_Node_Assignment (ENIQ_IDENTIFIER , FDN , NeType ) values ( '"
						+ whichEniq + "' , '" + node_fdn + "' , '" + node_type + "' )");
				
			} catch ( Exception e ){
				log.finest("Exception occured while inserting node FDN " + node_fdn + " into ENIQS_Node_Assignment table. " + e.getMessage());
				
				try{
			        log.finest("Connection was already closed. Trying again...to insert the node FDN "+ node_fdn + " into ENIQS_Node_Assignment table");
			    		
			        closeConnections();
			        rf_etlrep = DbUtilities.connectToEtlrep();
			    	rf_dwhrep = DbUtilities.connectTodb(rf_etlrep, "dwhrep");
			        con = rf_dwhrep.getConnection();
			            
			        stmt = con.createStatement();
			        stmt.execute("insert into ENIQS_Node_Assignment (ENIQ_IDENTIFIER , FDN , NeType ) values ( '"
								+ whichEniq + "' , '" + node_fdn + "' , '" + node_type + "' )");
				
				} catch ( Exception e1 ){
					log.warning("Exception while re-trying. FDN " + node_fdn + " is not inserted into ENIQS_Node_Assignment table. " + e1.getMessage());
				}
			}
		}
		else {
			log.info("Multi ENIQ-S configuration. Validating the FDN " + node_fdn + " against Policy and Criteria");
				// As first step, check if the node_type matches with any of
				// the P&C
//			boolean matches = false;
				Map<String, ArrayList<String>> TechnoMap = NodeAssignmentCache.getTechnologyNodeMap();
				for (Eniq_Role eniq : roleTableList) {
					boolean matches = false;
					log.finest("Checking with the P&C of Eniq " + eniq);
					if (!eniq.getPolicyCriteriaMap().isEmpty()) {
						log.finest(" Server = " + eniq.toString() + " PolicyCriteriaMap = " + eniq.getPolicyCriteriaMap().toString());
						boolean technologymatched = false;
						for (String technology : eniq.getPolicyCriteriaMap().keySet()) {
							if (technology.equals("*")) {
								technologymatched = true;
							} else if (TechnoMap.containsKey(technology)) {
								if (TechnoMap.get(technology).contains(node_type)) {
									log.info(eniq + " supports Technology : " + technology + " and Node type : "
											+ node_type);
									technologymatched = true;
								}
							} else {
								log.info(eniq + " doesnt support Technology :" + technology);
							}

							if (technologymatched) {
							matches = checkNodeAgainstPolicies(eniq.getPolicyCriteriaMap().get(technology), node_fdn);
								break;
							}
						}
						log.finest(" Value of boolean matches is " + matches);
					
						if (matches) {
							log.info("Node FDN " + node_fdn + " matches with the Policy Criteria defined for ENIQ-S Server " + eniq);
							whichEniq = eniq.toString();
						
						try{
							stmt = con.createStatement();
							stmt.execute("insert into ENIQS_Node_Assignment (ENIQ_IDENTIFIER , FDN , NeType ) values ( '"
											+ whichEniq + "' , '" + node_fdn + "' , '" + node_type + "' )");
							
						} catch ( Exception e) {
							log.finest("Exception occured while inserting node FDN " + node_fdn + " into ENIQS_Node_Assignment table " + e.getMessage());
							try{
								log.finest("Connection was already closed. Trying again...to insert the node FDN "+ node_fdn + " into ENIQS_Node_Assignment table");
								
								closeConnections();
								rf_etlrep = DbUtilities.connectToEtlrep();
						    	rf_dwhrep = DbUtilities.connectTodb(rf_etlrep, "dwhrep");
						        con = rf_dwhrep.getConnection();
						        
						        stmt = con.createStatement();
						        stmt.execute("insert into ENIQS_Node_Assignment (ENIQ_IDENTIFIER , FDN , NeType ) values ( '"
													+ whichEniq + "' , '" + node_fdn + "' , '" + node_type + "' )");
						          
//						        log.info("The Node FDN " + node_fdn + " is Un-Assigned. Successfully added into ENIQS_Node_Assignment table");
//						        insertIntoSlave = true;
						          
							} catch ( Exception e1 ){
								log.warning("Exception while re-trying. FDN " + node_fdn + " is not inserted into ENIQS_Node_Assignment table. " + e1.getMessage());
								break;
							}
					    }
						log.info("The Node FDN " + node_fdn + " is assigned to " + whichEniq + " server. Successfully added into ENIQS_Node_Assignment table");
						insertIntoSlave = true;
						break;
						
						} else {
							log.info("Node FDN " + node_fdn + " doesnt match with any of the Policy Criteria defined");
						}
					} else {
						log.info("Policy and Criteria not defined for the ENIQ-S server  " + eniq + ". Hence the node FDN " + node_fdn + " will be Un-Assigned! ");

					try{
						stmt = con.createStatement();
						stmt.execute("insert into ENIQS_Node_Assignment (ENIQ_IDENTIFIER , NeType , FDN ) values ('','"
								+ node_type + "' , '" + node_fdn + "')");
						log.info("The Node FDN " + node_fdn + " is Un-Assigned. Successfully added into ENIQS_Node_Assignment table");
						insertIntoSlave = true;
						
					} catch ( Exception e) {
						log.finest("Exception occured while inserting the node FDN "+ node_fdn + " into ENIQS_Node_Assignment table" + e.getMessage());
						try{
							log.finest("Connection was already closed. Trying again...to insert the node FDN "+ node_fdn + " into ENIQS_Node_Assignment table");
							
							closeConnections();							
					        rf_etlrep = DbUtilities.connectToEtlrep();
					    	rf_dwhrep = DbUtilities.connectTodb(rf_etlrep, "dwhrep");
					    	con = rf_dwhrep.getConnection();
					    	
					        stmt = con.createStatement();
					        stmt.execute("insert into ENIQS_Node_Assignment (ENIQ_IDENTIFIER , NeType , FDN ) values ('','"
										+ node_type + "' , '" + node_fdn + "')");
					        
					        insertIntoSlave = true;
					        log.info("The Node FDN " + node_fdn + " is Un-Assigned. Successfully added into ENIQS_Node_Assignment table");
					          
						} catch ( Exception e1 ){
							log.warning("Exception while re-trying. FDN " + node_fdn + " is not added into ENIQS_Node_Assignment table. " + e1.getMessage());
						}
					}
				}
			}
			if( !insertIntoSlave ){
				try{
					stmt = con.createStatement();
					stmt.execute("insert into ENIQS_Node_Assignment (ENIQ_IDENTIFIER , NeType , FDN ) values ('','"
							+ node_type + "' , '" + node_fdn + "')");
//					log.info("The Node FDN " + node_fdn + " is Un-Assigned. Successfully added into ENIQS_Node_Assignment table");
					insertIntoSlave = true;
					
				} catch ( Exception e) {
					log.finest("Exception occured while inserting the node FDN "+ node_fdn + " into ENIQS_Node_Assignment table" + e.getMessage());
					try{
						log.finest("Connection was already closed. Trying again...to insert the node FDN "+ node_fdn + " into ENIQS_Node_Assignment table");
						
						closeConnections();							
				        rf_etlrep = DbUtilities.connectToEtlrep();
				    	rf_dwhrep = DbUtilities.connectTodb(rf_etlrep, "dwhrep");
				    	con = rf_dwhrep.getConnection();
				    	
				        stmt = con.createStatement();
				        stmt.execute("insert into ENIQS_Node_Assignment (ENIQ_IDENTIFIER , NeType , FDN ) values ('','"
									+ node_type + "' , '" + node_fdn + "')");
				        
				        insertIntoSlave = true;
//				        log.info("The Node FDN " + node_fdn + " is Un-Assigned. Successfully added into ENIQS_Node_Assignment table");
				          
					} catch ( Exception e1 ){
						log.warning("Exception while re-trying. FDN " + node_fdn + " is not added into ENIQS_Node_Assignment table. " + e1.getMessage());
					}
				}
			}
			
			// insertion to be done in all the slave servers
			if ( insertIntoSlave ){
				try{
					rs = stmt.executeQuery(roleSql);
				} catch (Exception e) {
					log.finest("Exception occured while fetching all the SLAVE servers IP from ROLE Table." + e.getMessage());
					try{
						log.finest("Connection was already closed. Trying again...to fetch all the SLAVE servers IP from ROLE Table.");
						
						closeConnections();							
				        rf_etlrep = DbUtilities.connectToEtlrep();
				    	rf_dwhrep = DbUtilities.connectTodb(rf_etlrep, "dwhrep");
				    	con = rf_dwhrep.getConnection();
				    	
				        stmt = con.createStatement();
				        rs = stmt.executeQuery(roleSql);
				        log.finest("Successfully fetched all the SLAVE servers IP from ROLE Table");
				          
					} catch ( Exception e1 ){
						log.warning("Exception while re-trying. Failed to fetch all the SLAVE servers IP from ROLE Table. " + e1.getMessage());
						return;
					}
				}
				try{
					while (rs.next()) {
						serverIP = rs.getString("IP_ADDRESS");
					final IEnmInterworkingRMI RMI = (IEnmInterworkingRMI) Naming
								.lookup((RmiUrlFactory.getInstance()).getMultiESRmiUrl(serverIP));
						log.info("RMI : Server IP : " + serverIP + "\t Role : " + RMI.getCurrentRole() + " \t"
							+ RMI.toString());
	
					if (whichEniq == null) {
							try{
						isRmi = RMI.natInsert("", node_fdn, node_type);
							} catch ( Exception e ){
								log.warning("RMI: Error while writing FDN " + node_fdn + " to the NAT table in Server IP  "+ serverIP + "\t" + e.getMessage());
							}
						} else {
							try{
						isRmi = RMI.natInsert(whichEniq, node_fdn, node_type);
							} catch ( Exception e ){
								log.warning("RMI: Error while writing FDN " + node_fdn + " to the NAT table in Server IP  "+ serverIP + "\t" + e.getMessage());
							}
					}
					if (isRmi) {
						log.info("Inserted the FDN " + node_fdn + " into Slave ENIQ-S server " + whichEniq + "  ENIQS_Node_Assignment successfully. "
								+ rs.getString("IP_ADDRESS"));
					}
				}
				} catch (Exception e) {
					log.warning("RMI: Error while writing FDN " + node_fdn + " to the NAT table in Server IP  "+ serverIP + "\t" + e.getMessage());
				}
			}
		}
	}

	private boolean checkNodeAgainstPolicies(ArrayList<String> arrayList, String nodeType) {

		boolean matchFlag = false;
		String nodeName = nodeType.substring(nodeType.lastIndexOf("=") + 1, nodeType.length());
		for (String s : arrayList) {
			log.info("Matching node " + nodeName + " with the policy : " + s);
			if (s.equals("*")) {
				log.finest("Node " + nodeName + " matches with policy '*'");
				matchFlag = true;
				break;
			} else {
				try{
					Pattern pattern = Pattern.compile(s);
					Matcher matcher = pattern.matcher(nodeName);
					if (matcher.matches()) {
						log.info("Pattern " + s + " matches with Node : " + nodeName);
						matchFlag = true;
						break;
					} else {
						matchFlag = false;
					}
				}
				catch (PatternSyntaxException pe){
					log.warning("Exception occured. Please check the Naming Convention Policy: "+ s +" is invalid. " + pe.getMessage());
				}
				catch (Exception e){
					log.warning("Exception occured while matching node FDN "+ nodeType +" against the Naming Convention Policy!!" + e.getMessage());
				}
			}
		}
		return matchFlag;
	}

	private boolean checkNAT() {
		// Check if this fdn is already assigned/present in the NAT table
		boolean flag = false;
		Statement stmt = null;
		try{
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select FDN from ENIQS_Node_Assignment where FDN='" + node_fdn +"' ");
			if(rs.next()){
				flag = true;
			}
		} catch ( Exception e ){
			log.finest("Exception occured while checking the FDN " + node_fdn + " in the ENIQS_Node_Assignment table!" + e.getMessage());
			try{
				log.finest("Connection was already closed. Trying again...to check the FDN " + node_fdn + " in the ENIQS_Node_Assignment table!");
				
				closeConnections();
	            rf_etlrep = DbUtilities.connectToEtlrep();
	    		rf_dwhrep = DbUtilities.connectTodb(rf_etlrep, "dwhrep");
	            con = rf_dwhrep.getConnection();
	            
				stmt = con.createStatement();
	            ResultSet rs = stmt.executeQuery("select FDN from ENIQS_Node_Assignment where FDN='" + node_fdn +"' ");
	            if(rs.next()){
	            	flag = true;
	            }
			} catch (Exception e1) {
				log.warning("Exception while re-trying. Failed to check the FDN " + node_fdn + " in the ENIQS_Node_Assignment table!" + e1.getMessage());
			}
		}
		return flag;
	}
	
	/**
	 * 
	 */
	private void closeConnections() {
		try {
			if(con != null){
				con.close();
			}
			if(rf_dwhrep.getConnection() != null){
				rf_dwhrep.getConnection().close();
			}
			if(rf_etlrep.getConnection() != null){
				rf_etlrep.getConnection().close();
			}
		} catch (SQLException e) {
			log.warning("Error while closing the connections " + e.getMessage());
		}
	}
}
