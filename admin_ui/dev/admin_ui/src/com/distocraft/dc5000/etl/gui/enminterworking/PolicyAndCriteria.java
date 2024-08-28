package com.distocraft.dc5000.etl.gui.enminterworking;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

//import com.distocraft.dc5000.etl.engine.main.ITransferEngineRMI;
import com.distocraft.dc5000.common.RmiUrlFactory;
import com.ericsson.eniq.enminterworking.IEnmInterworkingRMI;

public class PolicyAndCriteria {

	private final Log log = LogFactory.getLog(this.getClass());

	public boolean insertPolicy(String technology, String namingConvention, String es, Connection dwhrep) {
		Statement stmt = null;
		ResultSet rs = null;
		boolean isInserted = false;
		boolean isRmi = false;
		final String roleSql="Select IP_ADDRESS from RoleTable where ROLE='SLAVE'";
		final String sql = "Insert into ENIQS_Policy_Criteria values ('" + technology + "','" + namingConvention + "','"
				+ es + "')";
		try {
			stmt = dwhrep.createStatement();
			stmt.execute(sql);
			log.info("Inserted policy successfully");
			isInserted = true;
				//insert the same into ENIQ-S slaves (if present)
				try {
					rs = stmt.executeQuery(roleSql);
					while (rs.next()) {
					String IPAddress = rs.getString("IP_ADDRESS");
					log.debug("The ipaddress is " + IPAddress);
					IEnmInterworkingRMI multiEs =  (IEnmInterworkingRMI) Naming.lookup(RmiUrlFactory.getInstance().getMultiESRmiUrl(IPAddress));
					isRmi=multiEs.policyCriteriaInsert(technology,namingConvention ,es);
					if(isRmi){
						log.info("Inserted the row successfully in " + rs.getString("IP_ADDRESS"));
					}
					}
				} catch (SQLException e) {
					log.warn("SQLException at insertPolicy: "+e);
				} catch (MalformedURLException e) {
					log.warn("MalformedURLException at insertPolicy: "+e);
				} catch (RemoteException e) {
					log.warn("RemoteException at insertPolicy: "+e);
				} catch (NotBoundException e) {
					log.warn("NotBoundException at insertPolicy: "+e);
				}
		} catch (SQLException e) {
			log.error("SQLException: "+ e);
		} finally {
			
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					log.warn("SQLException at insertPolicy finally clause: "+e);
				}
			}
		}
		return isInserted;
	}

	public boolean validateRegex(String tech, String name, String es, Connection dwhrep) {
		Statement stmt = null;
		boolean isValid = true;
		ResultSet rs;

		// if naming convention policy is provided
		if (!name.equalsIgnoreCase("*")) {
			// check whether the name is matching the pattern
			if (isPatternMatching(name)) {
				log.debug("Matching the pattern " + name);

				String sqlCheck = "select NAMINGCONVENTION from ENIQS_Policy_Criteria where TECHNOLOGY='" + tech
						+ "' and NAMINGCONVENTION='" + name + "' and ENIQ_IDENTIFIER='" + es
						+ "'";
				log.debug("sql  :  " + sqlCheck);

				try {
					stmt = dwhrep.createStatement();
					rs = stmt.executeQuery(sqlCheck);
					while(rs.next()){
						if (rs.getString("NAMINGCONVENTION")==name) {
							// policy already exists
							log.info("Policy Already exists!!");
							isValid = false;
							break;
						} 
					}
				} catch (Exception e) {
					log.warn("Exception caught while checking the policy table " + e);
					isValid = false;
				}
			} else {
				log.info("Please enter the valid NamingConvention Policy : " + name);
				isValid = false;
			}
		}
		// if naming convention policy is *
		else {
			String sqlCheck = "select * from ENIQS_Policy_Criteria where TECHNOLOGY='" + tech + "'and NAMINGCONVENTION='" + name + "' and ENIQ_IDENTIFIER='" + es + "'";
			log.debug("sql  :  " + sqlCheck);

			try {
				stmt = dwhrep.createStatement();
				rs = stmt.executeQuery(sqlCheck);
				if (rs.next()) {
					// policy already exists
					log.info("Policy Already exists!!");
					isValid = false;
				} else {
					// new policy
					log.info("Policy is Valid!!");
					isValid = true;
				}
			} catch (Exception e) {
				log.warn("Exception caught while checking the policy table " + e);
				isValid = false;
			}
		}
		return isValid;
	}

	/**
	 * @param name
	 * @return
	 */
	protected boolean isPatternMatching(String name) {
		try{
			@SuppressWarnings("unused")
			Pattern pattern = Pattern.compile(name);
			return true;
		}
		catch(Exception e){
			log.debug("pattern :"+name+ " is not valid");
			return false;
		}


	}

	protected ArrayList<ArrayList<String>> getAllPolicies(Connection dwhrep) {
		ArrayList<ArrayList<String>>  pList = new ArrayList<ArrayList<String>>();
		Statement stmt = null;
		ResultSet rSet = null;
		try {
			stmt = dwhrep.createStatement();
			rSet = stmt.executeQuery("Select * from ENIQS_Policy_Criteria");
			while (rSet.next()) {
				ArrayList<String> list = new ArrayList<String>();
				list.add(rSet.getString("TECHNOLOGY"));
				list.add(rSet.getString("NAMINGCONVENTION"));
				list.add(rSet.getString("ENIQ_IDENTIFIER"));
				pList.add(list);
			}
		} catch (Exception e) {
			log.warn("Exception caught in getAllPolicies method"+e);
		} finally {
			// finally clean up
			try {
				if (rSet != null) {
					rSet.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e) {
				log.warn("Exception caught while fetching the policy  " + e);
			}
		}
		return pList;
	}

	// refines the policy
	public String[] refinePolicy(String index, Connection dwhrep) {
		String[] out1 = null;
		HashMap<Integer, String[]> tablesContents = new HashMap<Integer, String[]>();
		Statement stmt = null;
		ResultSet tableContentRS;
		//ArrayList<String[]> output = new ArrayList<String[]>();
		try {

			String sqltableContent = "select * from ENIQS_Policy_Criteria ";
			log.debug("sql  :  " + sqltableContent);

			stmt = dwhrep.createStatement();
			tableContentRS = stmt.executeQuery(sqltableContent);
			ResultSetMetaData meta = tableContentRS.getMetaData();
			int col = meta.getColumnCount();
			out1 = new String[col];
			while (tableContentRS.next()) {
				log.debug("row :: " + tableContentRS.getRow());
				String[] values = new String[col];
				for (int column = 1; column <= col; column++) {
					Object value = tableContentRS.getObject(column);
					values[column - 1] = String.valueOf(value);
				}
				tablesContents.put(tableContentRS.getRow(), values);
			}
			int index1 = Integer.parseInt(index) + 1;
			out1 = tablesContents.get(index1);
			for (int i = 1; i <= 4; i++) {
				log.debug(tablesContents.get(i));
			}
			log.debug(out1);

		} catch (SQLException e) {
			log.warn("Exception caught at refinePolicy method"+e);
		}
		return out1;
	}

	public boolean isCombinationExists(String technology, String regexString, String identifier, Connection dwhrep) {

		boolean isCombinationExists = false;
		Statement stmt = null;
		ResultSet rs;

		try {
			String sqlCheck = " select * from  ENIQS_Policy_Criteria where TECHNOLOGY='" + technology
					+ "' and NAMINGCONVENTION='" + regexString + "'";

			log.debug("sql  :  " + sqlCheck);

			stmt = dwhrep.createStatement();
			rs = stmt.executeQuery(sqlCheck);
			while(rs.next()){
				if (rs.getString("NAMINGCONVENTION").equals(regexString)) {
				// policy already exists
				log.info("Policy Already exists!!");
				isCombinationExists = true;
					break;
				} 
			}
		} catch (SQLException e) {
			log.error("Exception occured while updating the Policy and Criteria!!"+e);
			
		}

		return isCombinationExists;
	}

	public void updatePolicy(String oldTechnology, String technology, String oldNaming, String regexString,
			String oldIdentifier, String identifier, Connection dwhrep) {
		
		//boolean isUpdated = false;
		Statement stmt = null;
		ResultSet rs = null;
		boolean isRmi = false;
		final String roleSql="Select IP_ADDRESS from RoleTable where ROLE='SLAVE'";
		final String sqlUpdate = " update ENIQS_Policy_criteria set " + " TECHNOLOGY=\'" + technology + "' , "
				+ " NAMINGCONVENTION=\'" + regexString + "' , " + " ENIQ_IDENTIFIER=\'" + identifier + "' where "
				+ " TECHNOLOGY=\'" + oldTechnology + "' and " + " NAMINGCONVENTION=\'" + oldNaming + "' and "
				+ " ENIQ_IDENTIFIER=\'" + oldIdentifier + "'";
		try {
			stmt = dwhrep.createStatement();
			stmt.execute(sqlUpdate);
				log.info("Succesfully Updated the Policy and Criteria!!");
				//update the same into ENIQ-S slaves (if present)
				try {
					stmt = dwhrep.createStatement();
					rs = stmt.executeQuery(roleSql);
					while (rs.next()) {
					String IPAddress = rs.getString("IP_ADDRESS");	
					log.debug("The ipaddress is " + IPAddress);
					IEnmInterworkingRMI multiEs =  (IEnmInterworkingRMI) Naming.lookup(RmiUrlFactory.getInstance().getMultiESRmiUrl(IPAddress));
					isRmi=multiEs.policyCriteriaUpdate(oldTechnology,technology,oldNaming,regexString,oldIdentifier,identifier);
					if(isRmi){
						log.info("Updated the row successfully in " + rs.getString("IP_ADDRESS"));
					}
					}
				} catch (SQLException e) {
					log.warn("SQLException at updatePolicy: "+e);
				} catch (MalformedURLException e) {
					log.warn("MalformedURLException at updatePolicy: "+e);
				} catch (RemoteException e) {
					log.warn("RemoteException at updatePolicy: "+e);
				} catch (NotBoundException e) {
					log.warn("NotBoundException at updatePolicy: "+e);
				}
		} catch (SQLException e) {
			log.error("Exception occured while updating the Policy and Criteria!!");
		}
	}

}
