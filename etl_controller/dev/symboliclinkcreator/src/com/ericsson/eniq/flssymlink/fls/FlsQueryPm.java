package com.ericsson.eniq.flssymlink.fls;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.ListIterator;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.ws.rs.client.Client;

import javax.ws.rs.core.Response;

import org.glassfish.jersey.message.internal.MessageBodyProviderNotFoundException;

import com.ericsson.eniq.common.DatabaseConnections;
import com.ericsson.eniq.flssymlink.StaticProperties;

import ssc.rockfactory.RockFactory;
import ssc.rockfactory.RockResultSet;

public class FlsQueryPm {
	
	public static FlsQueryPm instance=null;
				
	static String HOST;
	ENMServerDetails cache;
	static Client client;
	RestClientInstance restClientInstance;
	Logger log;
	final String PATH = "file/v1/files";
	
	//queue for storing JSON Objects
	public LinkedBlockingQueue<Runnable> pmQueue;
	
	public FlsQueryPm() {
		
	}
	
	public FlsQueryPm(ENMServerDetails cache, Logger log, LinkedBlockingQueue<Runnable> pmQueue) {
		this.cache=cache;
		this.log=log;
		this.pmQueue=pmQueue;
	}
	
	
	public static FlsQueryPm getInstance(ENMServerDetails cache, Logger log, LinkedBlockingQueue<Runnable> pmQueue){
		if(instance == null){
			instance=new FlsQueryPm(cache,log,pmQueue);
		}
		return instance;
	}
	

	 /** Send FLS Query REQUEST to ENM in REST GET request and get the response in the form of JSON
	 * object.
	 * @param nodeFdnList 
	 * @param nodeName,nodeList, isNewNode,id
	 */
	
	//To collect Nodes assigned to particular Eniq-S
	public ArrayList<String> getNodeFdnList(String neType,String eniqName) throws SQLException
	{
		RockFactory dwhrep=null;
		String selectString=null;
		final RockResultSet rockResultSet;
		try{
			dwhrep=DatabaseConnections.getDwhRepConnection();
			selectString="select FDN from ENIQS_Node_Assignment where  NETYPE='"+neType+"' and ENIQ_IDENTIFIER='"+eniqName+"'";
			rockResultSet = dwhrep.setSelectSQL(selectString);
			ResultSet rs= rockResultSet.getResultSet();
			ArrayList<String> returnNodeFdnList=new ArrayList<String>();
			
			while(rs.next()){
				returnNodeFdnList.add(rs.getString(1));
				log.finest("FDN value "+rs.getString(1));
			}
			
			dwhrep.getConnection().close();
			return returnNodeFdnList;
			
		}catch(Exception e){
			log.warning("Exception in getNodeList method "+e.getMessage());
			return null;
		}		
		
	}
	
	//To collect Nodes assigned to any Eniq-S
	public ArrayList<String> getAssignedNodeFdnList(String neType) throws SQLException
	{
		RockFactory dwhrep=null;
		String selectString=null;
		final RockResultSet rockResultSet;
		try{
			dwhrep=DatabaseConnections.getDwhRepConnection();
			selectString="select FDN from ENIQS_Node_Assignment where  NETYPE='"+neType+"' and ENIQ_IDENTIFIER != ''";
			rockResultSet = dwhrep.setSelectSQL(selectString);
			ResultSet rs= rockResultSet.getResultSet();
			ArrayList<String> returnNodeFdnList=new ArrayList<String>();
			
			while(rs.next()){
				returnNodeFdnList.add(rs.getString(1));
				log.finest("FDN value "+rs.getString(1));
			}
			
			dwhrep.getConnection().close();
			return returnNodeFdnList;
			
		}catch(Exception e){
			log.warning("Exception in getAssignedNodeFdnList method"+e.getMessage());
			return null;
		}		
		
	}
//To collect Nodes in NAT table

		public ArrayList<String> getNodeAllFdnList(String neType) throws SQLException
		{
			RockFactory dwhrep=null;
			String selectString=null;
			final RockResultSet rockResultSet;
			try{
				dwhrep=DatabaseConnections.getDwhRepConnection();
				selectString="select FDN from ENIQS_Node_Assignment where  NETYPE='"+neType+"'";
				rockResultSet = dwhrep.setSelectSQL(selectString);
				ResultSet rs= rockResultSet.getResultSet();
				ArrayList<String> returnNodeFdnList=new ArrayList<String>();
				
				while(rs.next()){
					returnNodeFdnList.add(rs.getString(1));
					log.finest("FDN value "+rs.getString(1));
				}
				
				dwhrep.getConnection().close();
				return returnNodeFdnList;
				
			}catch(Exception e){
				log.warning("exception in getNodeList method"+e);
				return null;
			}		
			
		}
	public long queryPM(String nodeType,long id,String dateTime,int choice) {
		String pmDataType=null;
		if(nodeType.equals("ERBS")||nodeType.equals("RadioNode")){
			pmDataType="PM_STATISTICAL,dataType==PM_EBSL_ERBS";
		}else if(nodeType.equals("SGSN-MME")){
			pmDataType="PM_STATISTICAL,dataType==PM_EBSM_3GPP";
		}else{
			pmDataType="PM_STATISTICAL";
		}
		Date startTime = new Date(System.currentTimeMillis());
		long resultId=id;
		int eniq_Stats_count = 0;
		PmJsonArray pmJsonArray=null;
		try{
			HOST = "https://" + cache.getHost();
			restClientInstance = RestClientInstance.getInstance();
			client = restClientInstance.getClient(cache, log);
			
			if(RestClientInstance.getSessionCheck()){
				
				PmJson pmJson;
		    
			    RockFactory dwhrep=null;
				String selectString=null;
				final RockResultSet rockResultSet;
				
				try{
					dwhrep=DatabaseConnections.getDwhRepConnection();
					selectString="select count(*) from RoleTable";
					rockResultSet = dwhrep.setSelectSQL(selectString);
					ResultSet rs= rockResultSet.getResultSet();
					
					if(rs.next()){
						eniq_Stats_count=rs.getInt(1);
					}
					
				}catch(Exception e){
					log.warning("Exception occured while checking the Roletable "+e.getMessage());
				}
				
			ArrayList<String> nodeFdnList=null;
			ArrayList<String> fullNodeFdnList=null;
			ArrayList<String> assignedNodeFdnList=null;
			if(eniq_Stats_count>0){
				try {
					fullNodeFdnList=getNodeAllFdnList(nodeType);
					assignedNodeFdnList=getAssignedNodeFdnList(nodeType);
					nodeFdnList=getNodeFdnList(nodeType,Main.eniqName);
				} catch (SQLException e) {
					log.warning("Exception occured while getting the Nodelist "+e.getMessage());
				}
			}
		    Response flsResponse=null ;
		    try{
		    		
		    switch(choice){
		    
		    case 1:
		    	flsResponse =client.target(HOST).path(PATH).queryParam("filter","(dataType=="+pmDataType+");nodeType=="+nodeType+";id=gt="+id)
				.queryParam("select", "fileLocation,id,nodeName,nodeType,fileCreationTimeInOss,dataType")
				.request("application/hal+json").get();
		 
		    	log.fine("PM query with token id URL : \n  " + 
				 client.target(HOST).path(PATH).queryParam("filter","(dataType=="+pmDataType+");nodeType=="+nodeType+";id=gt="+id)
					.queryParam("select", "fileLocation,id,nodeName,nodeType,fileCreationTimeInOss,dataType"));
		
		    	break;
		    	
		    case 2:
		    	 flsResponse =client.target(HOST).path(PATH).queryParam("filter","(dataType=="+pmDataType+");nodeType=="+nodeType+";fileCreationTimeInOss=ge="+dateTime)
					.queryParam("select", "fileLocation,id,nodeName,nodeType,fileCreationTimeInOss,dataType")
					.request("application/hal+json").get();
			 
		    	 log.fine("PM query with time stamp URL :  \n  " + 
					 client.target(HOST).path(PATH).queryParam("filter","(dataType=="+pmDataType+");nodeType=="+nodeType+";fileCreationTimeInOss=ge="+dateTime)
						.queryParam("select", "fileLocation,id,nodeName,nodeType,fileCreationTimeInOss,dataType"));

		    	break;
		    	
		    case 3:
		    	flsResponse =client.target(HOST).path(PATH).queryParam("filter","(dataType=="+pmDataType+");nodeType=="+nodeType+";fileCreationTimeInOss=ge="+dateTime+";fileCreationTimeInOss=lt="+Main.flsStartDateTimeAdminUi)
				.queryParam("select", "fileLocation,id,nodeName,nodeType,fileCreationTimeInOss,dataType")
				.request("application/hal+json").get();
		 
	    	 log.fine("PM query with time stamp URL :  \n  " + 
				 client.target(HOST).path(PATH).queryParam("filter","(dataType=="+pmDataType+");nodeType=="+nodeType+";fileCreationTimeInOss=ge="+dateTime+";fileCreationTimeInOss=lt="+Main.flsStartDateTimeAdminUi)
					.queryParam("select", "fileLocation,id,nodeName,nodeType,fileCreationTimeInOss,dataType"));

	    	break;
	    	
	    case 4:
	    	 flsResponse =client.target(HOST).path(PATH).queryParam("filter","(dataType=="+pmDataType+");nodeType=="+nodeType+";id=gt="+id+";fileCreationTimeInOss=lt="+Main.flsStartDateTimeAdminUi)
				.queryParam("select", "fileLocation,id,nodeName,nodeType,fileCreationTimeInOss,dataType")
				.request("application/hal+json").get();
		 
	    	 log.fine("PM query with time stamp URL :  \n  " + 
	    			 client.target(HOST).path(PATH).queryParam("filter","(dataType=="+pmDataType+");nodeType=="+nodeType+";id=gt="+id+";fileCreationTimeInOss=lt="+Main.flsStartDateTimeAdminUi)
						.queryParam("select", "fileLocation,id,nodeName,nodeType,fileCreationTimeInOss,dataType"));

	    	break;

		    	
	    default:
		    	flsResponse =client.target(HOST).path(PATH).queryParam("filter","(dataType=="+pmDataType+");nodeType=="+nodeType)
				.queryParam("select", "fileLocation,id,nodeName,nodeType,fileCreationTimeInOss,dataType")
				.request("application/hal+json").get();
		 
		    	log.fine("PM query with time stamp URL :  \n  " + 
				 client.target(HOST).path(PATH).queryParam("filter","(dataType=="+pmDataType+");nodeType=="+nodeType)
					.queryParam("select", "fileLocation,id,nodeName,nodeType,fileCreationTimeInOss,dataType"));

		    	break;
		    }
		    }catch(Exception e){
		    	log.warning("Exception atPM Querying for NodeType: "+nodeType+"Excetion is :"+e);
		    }
			
		    Main.lastDate=new Date();
			
			if(flsResponse !=null){
				
				pmJsonArray =flsResponse.readEntity(PmJsonArray.class);
				
				log.info("PM response: "+flsResponse + "\nresponse status: "+ flsResponse.getStatus() );
				
				ArrayList<PmJson> pmJsonArrayList=pmJsonArray.getFiles();
				
				if(pmJsonArrayList!=null){
					for(ListIterator<PmJson> iterator=pmJsonArrayList.listIterator();iterator.hasNext();){
						pmJson=iterator.next();
						
						if(eniq_Stats_count ==0){
						log.finest("Adding to the PM Queue of node type: "+nodeType+" file name: "+pmJson.getFileLocation());
						pmQueue.add(new PmQueueHandler(pmJson,log));
						}
						else{
							boolean nodeFlag=Boolean.parseBoolean(StaticProperties.getProperty("NODE_FLAG","false"));
							
							if(!nodeFlag){
								
								if(nodeFdnList.contains(pmJson.getNodeName())){
								  log.finest("Adding to the pmqueue of node type: "+nodeType+" file name: "+pmJson.getFileLocation());
									pmQueue.add(new PmQueueHandler(pmJson,log));
								}
								else if(assignedNodeFdnList.contains(pmJson.getNodeName())){
									log.finest("NodeFdn: "+pmJson.getNodeName()+" is not belongs to this Eniq-S server");
								}
								else if(fullNodeFdnList.contains(pmJson.getNodeName())){
									log.warning("NodeFdn: "+pmJson.getNodeName()+" is not assigned to any server ");
								}else{
									log.warning("NodeFdn: "+pmJson.getNodeName()+" is new FDN and not present in NAT Table");
								}
								}
							else{
								String nodeName=pmJson.getNodeName();
								for(ListIterator<String> i=nodeFdnList.listIterator();i.hasNext();){
									String nodeFdn=(String)i.next();
									if(Pattern.compile(".*"+nodeName+".*").matcher(nodeFdn).matches()){
									  log.finest("Adding to the PM Queue of node type: "+nodeType+" file name: "+pmJson.getFileLocation());
									  pmQueue.add(new PmQueueHandler(pmJson,log));
									   }
															  
								 }
							}
						}
				     }
				}
					//storing last id value
				if(pmJsonArrayList.size()>0){
					resultId=pmJsonArray.files.get(pmJsonArrayList.size()-1).getId();
				}

			}else{
				log.warning("PM Response is null for NodeType: "+nodeType);
				return resultId;
			}
				
			}	
		}
		catch(MessageBodyProviderNotFoundException e){
			log.warning("MessageBodyProviderNotFoundException in PM Subscription  "+e.getMessage());
			RestClientInstance.refreshInstance();
		}
		catch(Exception e){
			log.warning("Exception in PM Subscription  "+e.getMessage());
			RestClientInstance.refreshInstance();
		}
		Date endTime = new Date(System.currentTimeMillis());
		Long diff=endTime.getTime()-startTime.getTime();
		log.info("Time taken(in ms) to query fls for NodeType: "+nodeType+" is: "+diff+" . Total number of files received in the response is:" + pmJsonArray.files.size());
		return resultId;
	}
}