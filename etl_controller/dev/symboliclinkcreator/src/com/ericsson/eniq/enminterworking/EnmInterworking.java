package com.ericsson.eniq.enminterworking;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

import com.distocraft.dc5000.common.ENIQRMIRegistryManager;
import com.distocraft.dc5000.common.RmiUrlFactory;
import com.ericsson.eniq.common.DatabaseConnections;
import com.ericsson.eniq.enminterworking.automaticNAT.AssignNodesQueueHandler;
import com.ericsson.eniq.enminterworking.automaticNAT.NodeAssignmentCache;
import com.ericsson.eniq.flssymlink.fls.Main;
import com.ericsson.eniq.flssymlink.fls.RestClientInstance;

import ssc.rockfactory.RockFactory;
import ssc.rockfactory.RockResultSet;

/**
 * @author xarjsin
 *
 */

public class EnmInterworking extends UnicastRemoteObject implements IEnmInterworkingRMI {
	
	private static final long serialVersionUID = -2564842512593322084L;
	
	private Logger log;
	
	private String serverIP = EnmInterCommonUtils.getEngineIP();
	private int serverPort = 1200;
	private String serverRefName = "MultiESController";
	
	public EnmInterworking() throws RemoteException {
		super();
		
	}

	@Override
	public String getCurrentRole() throws RemoteException {
		return EnmInterCommonUtils.getSelfRole();
	}

	@Override
	public String updateSlave(String masterHost, String masterIP) throws RemoteException {
		log = Logger.getLogger("symboliclinkcreator.nat");
		RockFactory rockCon = null;
		//ResultSet masterNatResultSet=null;
		ArrayList<String> masterNatList=null;
		String slaveInsert = "Insert into RoleTable (ENIQ_ID, IP_ADDRESS, ROLE) Values ('" 
		+ EnmInterCommonUtils.getEngineHostname() + "', '" 
		+ EnmInterCommonUtils.getEngineIP() + "', 'SLAVE')";
		String masterInsert = "Insert into RoleTable (ENIQ_ID, IP_ADDRESS, ROLE) Values ('" 
				+ masterHost + "', '" 
				+ masterIP + "', 'MASTER')";
		log.info("slave insert"+slaveInsert);
		log.info("master insert"+masterInsert);
		if (EnmInterCommonUtils.getSelfRole().equals("UNASSIGNED")) {
			try
			{
				rockCon = DatabaseConnections.getDwhRepConnection();
				rockCon.getConnection().createStatement().executeUpdate(masterInsert);
				rockCon.getConnection().createStatement().executeUpdate(slaveInsert);
				try{
					IEnmInterworkingRMI multiEs =  (IEnmInterworkingRMI) Naming.lookup(RmiUrlFactory.getInstance().getMultiESRmiUrl(masterIP));
					masterNatList=multiEs.MasterNATDetail();
				}catch(Exception e){
					log.warning("Exception occured while receiveing Master NAT Details :"+e.getMessage());
				}
				try{
					String slaveSelectSql="select count(*) from ENIQS_Node_Assignment";
					RockResultSet rockResultSet = rockCon.setSelectSQL(slaveSelectSql);
					ResultSet rs= rockResultSet.getResultSet();
					
					if(rs.next()){
						int eniq_nat_count=rs.getInt(1);
					
					if(eniq_nat_count>0){
					log.info("Slave ENIQS_Node_Assignment is having entries.so deleting all the entries before copying Master NAT details");
					String slaveDeleteSql="DELETE FROM ENIQS_Node_Assignment";
					rockCon.executeSql(slaveDeleteSql);
					}
					}
					String preparedSqlStr="Insert into ENIQS_Node_Assignment(ENIQ_IDENTIFIER,FDN,NETYPE) Values(?,?,?)";
					PreparedStatement ps=rockCon.createPreparedSqlQuery(preparedSqlStr);
					
					for(Iterator i=masterNatList.iterator();i.hasNext();){
						String s=(String)i.next();
						String[] arr=s.split("::");
						log.finest("id,fdn,type "+arr[0]+","+arr[1]+","+arr[2]);
						ps.setString(1, arr[0]);
						ps.setString(2, arr[1]);
						ps.setString(3, arr[2]);
						ps.execute();
					}
				}catch(Exception e){
					log.warning("Exception occured while adding  Master NAT Details to slave NAT:"+e.getMessage());
				}
				return EnmInterCommonUtils.getEngineHostname();
			}
			catch (Exception e)
			{
				log.warning( "Exception occured while updating slave ENIQS_Node_Assignment table : " + e.getMessage());
				return "FAIL";
			}
			finally
			{
				try {
			        if (rockCon.getConnection() != null) {
			        	rockCon.getConnection().close();
				        }
			      } catch (SQLException e) {
			    	  log.warning( "Exception while closing the connection: " + e);
			      }
			}
		}
		
		return "FAIL";
	}

	@Override
	public ResultSet getMaster() throws RemoteException  {
		log = Logger.getLogger("symboliclinkcreator.nat");
		RockFactory rockCon = null;
		ResultSet masterDetails = null;
		String masterSql = "Select ENIQ_ID, IP_ADDRESS from RoleTable where Role = 'MASTER'";
		if(getCurrentRole().equals("SLAVE"))
		{
			try
			{
				rockCon = DatabaseConnections.getDwhRepConnection();
				masterDetails = rockCon.getConnection().createStatement().executeQuery(masterSql);
			}
			catch (Exception e)
			{
				log.warning( "Exception occured while getting the MASTER server data from RoleTable: " + e.getMessage());
			}
			finally
			{
				try {
					if (masterDetails != null) {
						masterDetails.close();
			        }
			        if (rockCon.getConnection() != null) {
			        	rockCon.getConnection().close();
				        }
			      } catch (SQLException e) {
			    	  log.warning( "Exception while closing the connection: " + e.getMessage());
			      }
			}
		}
		return masterDetails;
	}
	
	public static void main(final String args[]){
		System.setSecurityManager(new com.distocraft.dc5000.etl.engine.ETLCSecurityManager());
		
		try {
			final EnmInterworking enmInter = new EnmInterworking();
			if(!enmInter.initRMI()){
				//System.out.println("Initialisation failed... exiting");
			    System.exit(0);
			}
			else{
				//System.out.println("RMI initialisation done");
			}
		}
		catch(Exception e) {
//			System.out.println( "Initialization failed exceptionally", e);
			//e.printStackTrace();
		}
	}

	/**
	 * Method to bind MultiES RMI
	 * @return
	 */
	public boolean initRMI() {
		log = Logger.getLogger("symboliclinkcreator.nat");
		log.info("Initializing.....MultiES RMI");
	    final String rmiRef = "//" + serverIP + ":" + serverPort + "/" + serverRefName;
	    log.info("RMI url is - " + rmiRef);
	    ENIQRMIRegistryManager rmiRgty = new ENIQRMIRegistryManager(serverIP, serverPort);
		try {
			Registry rmi = rmiRgty.getRegistry();
			log.info("MultiES server is rebinding to rmiregistry on host: " + serverIP + " on port: " + serverPort + 
	    			" with name: " + serverRefName);
			rmi.rebind(serverRefName, this);
			log.info("MultiES Server binds successfully to already running rmiregistry");
		} catch (final Exception e) {
			log.warning("Unable to bind to the rmiregistry using refrence: " + rmiRef + "\n" + e.getMessage());
			return false;
		}
		log.info("MultiES has been initialized.");
	    return true;
	}
	
	/**
	   * Method to shutdown MultiES RMI.
	   */
	  /**
	 * @
	 */
	public void shutdownRMI()  {
		  try{
			  	log = Logger.getLogger("symboliclinkcreator.nat");
			  	log.info("Shuting Down Multi ES RMI...");
			    ENIQRMIRegistryManager rmi = new ENIQRMIRegistryManager(serverIP, serverPort);
			    try {
			     	Registry registry = rmi.getRegistry();
			        registry.unbind(serverRefName);
			        UnicastRemoteObject.unexportObject(this, false);
			    } catch (final Exception e) {
			    	log.warning("Could not unregister Multi ES RMI service, quiting anyway."+e.getMessage());
			    }
			    new Thread() {
			    	@Override
			    	public void run() {
			    		try {
			    			sleep(2000);
			    		} catch (InterruptedException e) {
			    			// No action to take
			    		}
			    		System.exit(0);
			    	}//run
			    }.start();
	    } catch (Exception e) {
	    		log.warning( "Shutdown failed exceptionally: "+ e.getMessage());
	     
	    }
	}
	
	@Override
	public boolean natInsert(String eniqIdentifier,String fdn ,String neType) throws RemoteException{
		log = Logger.getLogger("symboliclinkcreator.nat");
		RockFactory rockCon = null;
		String slaveInsert = "Insert into ENIQS_Node_Assignment (ENIQ_IDENTIFIER, FDN, NETYPE) Values ('" 
		+ eniqIdentifier + "','" 
		+ fdn + "','"+neType+"')";
		
		
			try
			{
				rockCon = DatabaseConnections.getDwhRepConnection();
				rockCon.getConnection().createStatement().executeUpdate(slaveInsert);
				return true;
			}
			catch (Exception e)
			{
				log.warning( "Exception occured while updating the slave ENIQS_Node_Assignment table: " + e.getMessage());
				return false;
			}
			finally
			{
				try {
			        if (rockCon.getConnection() != null) {
			        	rockCon.getConnection().close();
				        }
			      } catch (SQLException e) {
			    	  log.warning( "Exception occured while closing the connection: " + e.getMessage());
			      }
			}
		}
	@Override
	public boolean policyCriteriaInsert(String technology,String namingConvention ,String eniqIdentifier) throws RemoteException {
		log = Logger.getLogger("symboliclinkcreator.nat");
		RockFactory rockCon = null;
		String slaveInsert = "Insert into ENIQS_Policy_Criteria (TECHNOLOGY, NAMINGCONVENTION, ENIQ_IDENTIFIER) Values ('" 
		+ technology + "', '" 
		+ namingConvention + "', '"+eniqIdentifier+"')";
		
		
			try
			{
				rockCon = DatabaseConnections.getDwhRepConnection();
				rockCon.getConnection().createStatement().executeUpdate(slaveInsert);
				return true;
			}
			catch (Exception e)
			{
				log.warning("Exception occured while updating the slave ENIQS_Policy_Criteria table: " + e.getMessage());
				return false;
			}
			finally
			{
				try {
			        if (rockCon.getConnection() != null) {
			        	rockCon.getConnection().close();
				        }
			      } catch (SQLException e) {
			    	  log.warning( "Exception occured while closing the connection: " + e.getMessage());
			      }
			}
		}
	
	
	@Override
	public boolean policyCriteriaUpdate(String oldTechnology, String technology, String oldNaming, String regexString,
			String oldIdentifier, String identifier) throws RemoteException {
		log = Logger.getLogger("symboliclinkcreator.nat");
		RockFactory rockCon = null;
		 String sqlUpdate = " update ENIQS_Policy_criteria set " + " TECHNOLOGY=\'" + technology + "' , "
				+ " NAMINGCONVENTION=\'" + regexString + "' , " + " ENIQ_IDENTIFIER=\'" + identifier + "' where "
				+ " TECHNOLOGY=\'" + oldTechnology + "' and " + " NAMINGCONVENTION=\'" + oldNaming + "' and "
				+ " ENIQ_IDENTIFIER=\'" + oldIdentifier + "'";
		try
			{
				rockCon = DatabaseConnections.getDwhRepConnection();
				rockCon.getConnection().createStatement().executeUpdate(sqlUpdate);
				return true;
			}
			catch (Exception e)
			{
				log.warning("Exception occured while updating the slave ENIQS_Policy_Criteria table: " + e.getMessage());
				return false;
			}
			finally
			{
				try {
			        if (rockCon.getConnection() != null) {
			        	rockCon.getConnection().close();
				        }
			      } catch (SQLException e) {
			    	  log.warning( "Exception occured while closing the connection: " + e.getMessage());
			      }
			}
		}
	@Override
	public boolean natUpdate(String eniq_identifier, String nodeFDN) throws RemoteException {
		log = Logger.getLogger("symboliclinkcreator.nat");
		RockFactory rockCon = null;
		String sqlUpdate = " update ENIQS_Node_Assignment set ENIQ_IDENTIFIER='" + eniq_identifier + "' where FDN = '" + nodeFDN + "'";
		try
			{
				rockCon = DatabaseConnections.getDwhRepConnection();
				rockCon.getConnection().createStatement().executeUpdate(sqlUpdate);
				log.info("Successfully Re-Assigned the node FDN "+ nodeFDN +" to " + eniq_identifier + " ENIQ-S server ");
				return true;
			}
			catch (Exception e)
			{
				log.warning( "Exception while updating the ENIQS_Node_Assignment table: " + e.getMessage());
				return false;
			}
			finally
			{
				try {
			        if (rockCon.getConnection() != null) {
			        	rockCon.getConnection().close();
				        }
			      } catch (SQLException e) {
			    	  log.warning( "Exception at natUpdate Method finally clause: " + e.getMessage());
			      }
			}
	}

	@Override
	public boolean flsConfExists() throws RemoteException {
		  try { 
		    	final File flsConf = new File("/eniq/installation/config/fls_conf");
				if(!flsConf.exists()){
					return false;
				}else{
					return true;
				}
			}
		  catch(Exception e){
			  return false;
		  }
	  }
	
	@Override
	public boolean IsflsServiceEnabled() throws RemoteException {
		log = Logger.getLogger("symboliclinkcreator.nat");
		boolean enableFLService = false;
		try{
			Process pFLS = Runtime.getRuntime().exec("cat /eniq/installation/config/fls_conf");
			BufferedReader inputFLS = new BufferedReader(new InputStreamReader(pFLS.getInputStream()));
			
			String line;
			String[]  fls_elabled_enm=null;
			while ((line = inputFLS.readLine()) != null) {
				 fls_elabled_enm= line.split("\\s+");
			}
			 
			if(fls_elabled_enm.length>0){
				enableFLService = true;
			
			}else{
				log.info("FLS mode is not configured in the server");
			}
		} catch (Exception e) {
			log.warning("Exception occured while reading the file :: /eniq/installation/config/fls_conf  \n "
					+ e.getMessage());
		}		
		return enableFLService;
	
	}

	@Override
	public void addingToBlockingQueue(String node_type, String node_fdn) throws RemoteException {
		log = Logger.getLogger("symboliclinkcreator.nat");
		try{
		log.finest("Calling addingToBlockingQueue.....");
		Main mainFls = Main.getInstance();
		mainFls.assignNodesQueue.add(new AssignNodesQueueHandler( node_type, node_fdn ) );
		log.finest("Added the FDN: " + node_fdn + " to the BlockingQueue");
		}
		catch(Exception e){
			log.warning("Exception at addingToBlockingQueue method: "+e.getMessage());
		  }
	}
	@Override
	public boolean isFlsMonitorOn() throws RemoteException{
		log = Logger.getLogger("symboliclinkcreator.fls");
		try{
			log.finest("fls_admin_flag is "+Main.fls_admin_flag);
			return Main.fls_admin_flag;
		}
		catch(Exception e){
			log.warning("Exception at isFlsMonitorOn method: "+e.getMessage());
			return false;
		}
		
	}
	
	@Override
	public void adminuiFlsQuery(String flsUserStartDateTime) throws RemoteException{
		log = Logger.getLogger("symboliclinkcreator.fls");
		try{
			Main main=Main.getInstance();
			log.finest("Before Fls querying with User inputed date and time");
			main.pmCallbyAdminUi(flsUserStartDateTime);
			log.info("Fls started querying with User inputed date and time");
		}
		catch(Exception e){
		log.warning("Exception at adminuiFlsQuery method"+e.getMessage());	
		}
		
	}

	@Override
	public void refreshNodeAssignmentCache() throws RemoteException{
		log = Logger.getLogger("symboliclinkcreator.nat");
		try{
		log.info("Policy and Criteria Cache Refreshed!");
		NodeAssignmentCache.checkNoOfServers();
		}
		catch(Exception e){
			log.warning("Exception at refreshNodeAssignmentCache method: "+e.getMessage());
		}
	}
	
	@Override
	public void shutDownMain() throws RemoteException{
		log = Logger.getLogger("symboliclinkcreator.nat");
		try{
		log.info("calling shutDown FLS ");
		Main main=Main.getInstance();
	
		log.info("Main object"+main.toString()+"is shutDown done");
		shutdownRMI();
		RestClientInstance restClientInstance=RestClientInstance.getInstance();
		if( RestClientInstance.getSessionCheck()){
	        restClientInstance.closeSession();
	        }
		main.endProcess();
		}
		catch(Exception e){
		//	log.warning("Exception at EnmInterworking shutDownMain method: "+e);
		}	
	}
	
	@Override
	public String unAssignSelfSlave() throws RemoteException {
		log = Logger.getLogger("symboliclinkcreator.nat");
		
		String deleteRole = "DELETE FROM ROLETABLE";
		RockFactory rockCon = DatabaseConnections.getDwhRepConnection();
		log.finest("Delete from roletable SQL - " + deleteRole);
		try {
			rockCon.getConnection().createStatement().executeUpdate(deleteRole);
			log.info("No role assigned to server now");
		} catch (SQLException e) {
			log.warning("Failed to unassign server" + e);
			return "Failed to unassign server";
		}
		finally {
			try {
				if (rockCon.getConnection() != null) {
		        	rockCon.getConnection().close();
			    }
		    } 
			catch (SQLException e) {
		    	  log.warning("Exception occured while closing the connection: " + e);
			}
		}
		return "No role assigned to server now";
	}

	@Override
	public String unAssignSpecSlave(String engineHostname, String engineIP) throws RemoteException{
		log = Logger.getLogger("symboliclinkcreator.nat");
		
		String deleteSpecRole = "DELETE FROM RoleTable WHERE ROLE='SLAVE' AND "
				+ "ENIQ_ID='" + engineHostname + "' AND"
				+ "IP_ADDRESS='" + engineIP + "'";
		RockFactory rockCon = DatabaseConnections.getDwhRepConnection();
		log.finest("Delete from roletable SQL - " + deleteSpecRole);
		try {
			rockCon.getConnection().createStatement().executeUpdate(deleteSpecRole);
			log.info("Slave server with Hostname - " + engineHostname + " and IP - " + engineIP 
					+ "has been unassigned from master server");
		} catch (SQLException e) {
			log.warning("Failed to unassign server with Hostname - " 
					+ engineHostname + " and IP - " + engineIP + e);
			return "Failed to unassign server with Hostname - " 
					+ engineHostname + " and IP - " + engineIP;
		}
		finally {
			try {
				if (rockCon.getConnection() != null) {
		        	rockCon.getConnection().close();
			    }
		    } 
			catch (SQLException e) {
		    	  log.warning("Exception occured while closing the connection: " + e);
			}
		}
		return "Slave server with Hostname - " + engineHostname + " and IP - " + engineIP 
				+ "has been unassigned from master server";
	}

	@Override
	public ArrayList<String> MasterNATDetail()throws RemoteException{
		ArrayList<String> masterNatList=null;
		log = Logger.getLogger("symboliclinkcreator.nat");
		RockFactory dwhrep=null;
		ResultSet masterNat=null;
		final RockResultSet rockResultSet;
		try{
		dwhrep = DatabaseConnections.getDwhRepConnection();
		String natSql="select * from ENIQS_Node_Assignment";
		rockResultSet = dwhrep.setSelectSQL(natSql);
		masterNat = rockResultSet.getResultSet();
		masterNatList=new ArrayList<String>();
		}
		catch(Exception e){
			log.warning( "Exception while querying the master nat table: " + e.getMessage());
		}
		finally{
			try{
			 if (dwhrep.getConnection() != null) {
		        	dwhrep.getConnection().close();
			        }
		      } catch (SQLException e) {
		    	  log.warning( "Exception while closing the connection: " + e.getMessage());
		      }
		}
		try {
			while(masterNat.next()){
					String s=masterNat.getNString("ENIQ_IDENTIFIER")+"::"+masterNat.getNString("FDN")+"::"+masterNat.getNString("NETYPE");
					masterNatList.add(s);
			}
		} catch (Exception e) {
			log.warning("Exception while adding Resultset to ArrayList"+e.getMessage());
		}
		return masterNatList;
		
	}

}
