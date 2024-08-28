package com.distocraft.dc5000.etl.fls;

import java.util.ArrayList;
import java.util.ListIterator;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.GenericType;

public class FlsQueryPm {
	
	public static FlsQueryPm instance=null;
				
	static String HOST;
	ENMServerDetails cache;
	static Client client;
	RestClientInstance restClientInstance;
	Logger log;
	
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
	 * @param nodeName,nodeList, isNewNode,id
	 */
	
	public int queryPM(String nodeType, boolean isNewNode,int id) {
		int resultId=id;
		try{
			HOST = "https://" + cache.getHost();
			restClientInstance = RestClientInstance.getInstance();
			client = restClientInstance.getClient(cache, log);
			log.info("client connection successful");
			String path_name;
			PmJson pmJson;
		
				if(!isNewNode){
					path_name= "file/v1/files?filter=dataType==\"PM_*\";nodeType==\""+nodeType+"\"&select=fileLocation,id,nodeName,nodeType,fileCreationTimeInOss&orderBy=id asc";
					}else{
					path_name= "file/v1/files?filter=dataType==\"PM_*\";nodeType==\""+nodeType+"\";id=gt="+id+"&select=fileLocation,id,nodeName,nodeType,fileCreationTimeInOss&orderBy=id asc";
					}
				log.finest("URL" + path_name + "  thread name:" + Thread.currentThread().getName());
				
				//get request for nodeType
				ArrayList<PmJson> pmArrayList=client.target(HOST).path(path_name).request("application/json").get(new GenericType<ArrayList<PmJson>>(){});
				
				//adding json object to queue
				if(pmArrayList!=null){
					for(ListIterator<PmJson> iterator=pmArrayList.listIterator();iterator.hasNext();){
						pmJson=iterator.next();
						
						pmQueue.add(new PmQueueHandler(pmJson));
				     }
					//storing last id value
					resultId=pmArrayList.get(pmArrayList.size()-1).getId();
					
					while(pmArrayList.size()>9999){
						path_name= "file/v1/files?filter=dataType==\"PM_*\";nodeType==\""+nodeType+"\";id=gt="+resultId+"&select=fileLocation,id,nodeName,nodeType,fileCreationTimeInOss&orderBy=id asc";
						log.finest("URL" + path_name + "  thread name:" + Thread.currentThread().getName());
						pmArrayList=client.target(HOST).path(path_name).request("application/json").get(new GenericType<ArrayList<PmJson>>(){});
					
						//adding json object to queue
						if(pmArrayList!=null){
							for(ListIterator<PmJson> iterator=pmArrayList.listIterator();iterator.hasNext();){
								pmJson=iterator.next();
								
								pmQueue.add(new PmQueueHandler(pmJson));
						     }
							resultId=pmArrayList.get(pmArrayList.size()-1).getId();
						
					}
					
				}
								
				}
				
				log.info("NodeType:\t"+nodeType+"\tlast id value\t"+resultId);
				
		}
		catch(Exception e){
			System.out.println("Exception "+e);
		}
		return resultId;
	}
}