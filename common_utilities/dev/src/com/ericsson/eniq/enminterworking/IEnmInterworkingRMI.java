package com.ericsson.eniq.enminterworking;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * @author xarjsin
 *
 */
public interface IEnmInterworkingRMI extends Remote {

	/**
	 * Gets current role assigned to the server
	 * 
	 * @return Current role of server
	 * @
	 */
	String getCurrentRole() throws RemoteException;
	
	/**
	 * Updates the server as a Slave server
	 * Also updates master details
	 * 
	 * @param masterHost
	 * @param masterIP
	 * @return Slave engine hostname if successful, FAIL if failed
	 * @
	 */
	String updateSlave(String masterHost, String masterIP) throws RemoteException;
	
	/**
	 * Gets the master details if assigned as a slave server
	 * 
	 * @return Master details
	 * @
	 */
	ResultSet getMaster() throws RemoteException;
	
	/**
	 * Inserts the ENIQS_Node_Assignment table when the master server gets inserted.
	 * 
	 * @param eniqIdentifier
	 * @param fdn
	 * @param neType
	 * @return True if successful, false if failed
	 * @
	 */
	public boolean natInsert(String eniqIdentifier,String fdn ,String neType) throws RemoteException;
	
	/**
	 * Inserts the ENIQS_Policy_Criteria table when the user inputs the policy.
	 * 
	 * @param technology
	 * @param namingConvention
	 * @param eniqIdentifier
	 * @return True if successful, false if failed
	 * @
	 */
	public boolean policyCriteriaInsert(String technology,String namingConvention ,String eniqIdentifier) throws RemoteException;
	
	/**
	 * Updates the ENIQS_Policy_Criteria when the user edits the policies.
	 * 
	 * @param oldTechnology
	 * @param technology
	 * @param oldNaming
	 * @param regexString
	 * @param oldIdentifier
	 * @param identifier
	 * @return True if successful, false if failed
	 * @
	 */
	
	public boolean policyCriteriaUpdate(String oldTechnology, String technology, String oldNaming, String regexString,
			String oldIdentifier, String identifier) throws RemoteException;
	/**
	 * Updates the ENIQS_Node_Assignment when the user tries manually to assign the nodes.
	 * 
	 * @param eniqIdentifier
	 * @param fdn
	 * @return True if successful, false if failed
	 * @
	 */
	public boolean natUpdate(String eniq_identifier, String nodeFDN) throws RemoteException ;
	
	/**
	 * Returns true of FlsConf file is present (integrated with ENM)
	 * 
	 * @return True if file exists
	 * @
	 */
	public boolean flsConfExists() throws RemoteException;

	public boolean IsflsServiceEnabled() throws RemoteException;
	
	public void addingToBlockingQueue(String node_type, String node_fdn) throws RemoteException;
	
	/**
	 * Returns true of FlsConf file is present (integrated with ENM)
	 * 	 * 
	 * @return True if file exists and fls_admin_flag is set
	 * @
	 */
	public boolean isFlsMonitorOn() throws RemoteException;
	
	/*
	 * 
	 * 
	 * */
	
	public void adminuiFlsQuery(String flsUserStartDateTime) throws RemoteException;
	
	public void refreshNodeAssignmentCache() throws RemoteException;
	
	public void shutDownMain() throws RemoteException;
	
	/**
	 * Clears RoleTable to unassign server
	 * 
	 * @return Current role of server
	 * @
	 */
	String unAssignSelfSlave() throws RemoteException;

	/**
	 * Deletes row in master RoleTable with Slave entry
	 *  to unassign server
	 * 
	 * @return Current role of server
	 * @
	 */
	String unAssignSpecSlave(String engineHostname, String engineIP) throws RemoteException;
		
	public ArrayList<String> MasterNATDetail()throws RemoteException;
	
}
