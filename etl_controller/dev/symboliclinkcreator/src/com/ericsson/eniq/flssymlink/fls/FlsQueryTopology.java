package com.ericsson.eniq.flssymlink.fls;

import java.util.ArrayList;
import java.util.Date;
import java.util.ListIterator;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;




public class FlsQueryTopology {
	
	public static FlsQueryTopology instance=null;
	
	static String HOST;
	ENMServerDetails cache;
	static Client client;
	RestClientInstance restClientInstance;
	Logger log;
	LinkedBlockingQueue<TopologyJson> topologyQueue;
	final String PATH = "file/v1/files";
	


	
	public FlsQueryTopology( ENMServerDetails cache,Logger log,LinkedBlockingQueue<TopologyJson> topologyQueue) {
		this.log=log;
		this.cache=cache;
		this.topologyQueue=topologyQueue;
	}
	
	public static FlsQueryTopology getInstance(ENMServerDetails cache, Logger log, LinkedBlockingQueue<TopologyJson> topologyQueue){
		if(instance == null){
			instance=new FlsQueryTopology(cache,log,topologyQueue);
		}
		return instance;
	}
	public Long queryTopology(String dataType,Long id,String dateTime,int choice) {
		Date startTime = new Date(System.currentTimeMillis());
		Long resultId=id;
		TopologyJsonArray topologyJsonArray=null;
		
		try{
			HOST = "https://" + cache.getHost();
			restClientInstance = RestClientInstance.getInstance();
			client = restClientInstance.getClient(cache, log);
			
			if(RestClientInstance.getSessionCheck()){

			Response flsResponse=null ;
			
			try{

				
			    switch(choice){
			    
			    case 1:
			    	log.info("PM query with token id URL : \n  " + 
							 client.target(HOST).path(PATH).queryParam("filter","dataType==TOPOLOGY*;id=gt="+id)
								.queryParam("select", "fileLocation,id,fileCreationTimeInOss,dataType,nodeType,nodeName"));
			    	
			    	flsResponse =client.target(HOST).path(PATH).queryParam("filter","dataType==TOPOLOGY*;id=gt="+id)
					.queryParam("select", "fileLocation,id,fileCreationTimeInOss,dataType,nodeType,nodeName")
					.request("application/hal+json").get();
			 
			    	break;
			    	
			    case 2:
			    	 log.info("PM query with time stamp URL :  \n  " + 
							 client.target(HOST).path(PATH).queryParam("filter","dataType==TOPOLOGY*")
								.queryParam("select", "fileLocation,id,fileCreationTimeInOss,dataType,nodeType,nodeName"));
			    	 
			    	flsResponse =client.target(HOST).path(PATH).queryParam("filter","dataType==TOPOLOGY*")
						.queryParam("select", "fileLocation,id,fileCreationTimeInOss,dataType,nodeType,nodeName")
						.request("application/hal+json").get();
				 
			    		break;
			    	
			    case 3:

			    	 log.info("PM query with time stamp URL :  \n  " + 
						 client.target(HOST).path(PATH).queryParam("filter","dataType==TOPOLOGY*;fileCreationTimeInOss=ge="+dateTime+";fileCreationTimeInOss=lt="+Main.flsStartDateTimeAdminUi)
							.queryParam("select", "fileLocation,id,fileCreationTimeInOss,dataType,nodeType,nodeName"));

			    	flsResponse =client.target(HOST).path(PATH).queryParam("filter","dataType==TOPOLOGY*;fileCreationTimeInOss=ge="+dateTime+";fileCreationTimeInOss=lt="+Main.flsStartDateTimeAdminUi)
					.queryParam("select", "fileLocation,id,fileCreationTimeInOss,dataType,nodeType,nodeName")
					.request("application/hal+json").get();
			 
		    	break;
		    	
		    case 4:
		    	 log.fine("PM query with time stamp URL :  \n  " + 
		    			 client.target(HOST).path(PATH).queryParam("filter","dataType==TOPOLOGY*;id=gt="+id+";fileCreationTimeInOss=lt="+Main.flsStartDateTimeAdminUi)
							.queryParam("select", "fileLocation,id,fileCreationTimeInOss,dataType,nodeType,nodeName"));

		    	 
		    	 flsResponse =client.target(HOST).path(PATH).queryParam("filter","dataType==TOPOLOGY*;id=gt="+id+";fileCreationTimeInOss=lt="+Main.flsStartDateTimeAdminUi)
					.queryParam("select", "fileLocation,id,fileCreationTimeInOss,dataType,nodeType,nodeName")
					.request("application/hal+json").get();
			 
		    	
		    	break;

			    	
		    default:
		    	log.fine("PM query with time stamp URL :  \n  " + 
						 client.target(HOST).path(PATH).queryParam("filter","dataType==TOPOLOGY*")
							.queryParam("select", "fileLocation,id,fileCreationTimeInOss,dataType,nodeType,nodeName"));
		    	
			    	flsResponse =client.target(HOST).path(PATH).queryParam("filter","dataType==TOPOLOGY*")
					.queryParam("select", "fileLocation,id,fileCreationTimeInOss,dataType,nodeType,nodeName")
					.request("application/hal+json").get();
				break;
			    }
			    
			}
			catch(Exception e){
				
			}
			
			Main.lastDate=new Date();
			if(flsResponse !=null){

			topologyJsonArray =flsResponse.readEntity(TopologyJsonArray.class);
				
			log.info("TOPOLOGY response "+flsResponse + " response status: "  + flsResponse.getStatus() );
			
			//get request to enm server for topology files			
			ArrayList<TopologyJson> topologyArrayList=topologyJsonArray.getFiles();
			
			TopologyJson topologyJson=null;
			//adding topology json objects to queue
			if(topologyArrayList!=null){
				for(ListIterator<TopologyJson> iterator=topologyArrayList.listIterator();iterator.hasNext();){
					topologyJson=(TopologyJson)iterator.next();
					topologyQueue.add(topologyJson);
				 }
				//storing last id
				if(topologyArrayList.size()>0){
					resultId=topologyArrayList.get(topologyArrayList.size()-1).getId();
				}
			}
			else{
				log.warning("Topology Response is null");
				return resultId;
			}

			}
			}						
		}
		catch(Exception e){
			log.warning("Exception at Fls TOPOLOGY Query : "+e.getMessage());
			
		}
		Date endTime = new Date(System.currentTimeMillis());
		Long diff=endTime.getTime()-startTime.getTime();
		log.fine("Time taken(in ms) to query fls for Topology is: "+diff);
		return resultId;
	}

}
