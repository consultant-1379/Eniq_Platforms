package com.distocraft.dc5000.etl.fls;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.GenericType;



public class FlsQueryTopology {
	
	public static FlsQueryTopology instance=null;
	
	static String HOST;
	ENMServerDetails cache;
	static Client client;
	RestClientInstance restClientInstance;
	Logger log;
	LinkedBlockingQueue<TopologyJson> topologyQueue;
	


	
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
	public int queryTopology(boolean isIdSet, int id) {
		int resultId=id;
		try{
			HOST = "https://" + cache.getHost();
			restClientInstance = RestClientInstance.getInstance();
			client = restClientInstance.getClient(cache, log);
			String path_name;
			
			if(!isIdSet){
				path_name = "file/v1/files?filter=dataType==\"TOPOLOGY\"&select=id,fileLocation,fileName,fileOrginatorType,fileCreationTimeInOss&orderBy=id asc";
			}else{
				path_name = "file/v1/files?filter=dataType==\"TOPOLOGY\";id=gt="+id+"&select=id,fileLocation,fileName,fileOrginatorType,fileCreationTimeInOss&orderBy=id asc";
			}
			log.finest("URL" + path_name + "  thread name:" + Thread.currentThread().getName());
			
			//get request to enm server for topology files			
			ArrayList<TopologyJson> topologyArrayList=client.target(HOST).path(path_name).request("application/json").get(new GenericType<ArrayList<TopologyJson>>(){});
			
			//adding topology json objects to queue
			if(topologyArrayList!=null){
				for(ListIterator<TopologyJson> iterator=topologyArrayList.listIterator();iterator.hasNext();){
					topologyQueue.add(iterator.next());
			     }
				//storing last id
			resultId=topologyArrayList.get(topologyArrayList.size()-1).getId();
			
				while(topologyArrayList.size()>9999){
					path_name = "file/v1/files?filter=dataType==\"TOPOLOGY\";id=gt="+resultId+"&select=id,fileLocation,fileName,fileOrginatorType,fileCreationTimeInOss&orderBy=id asc";
					log.finest("URL" + path_name + "  thread name:" + Thread.currentThread().getName());
					
					//get request to enm server for topology files			
					topologyArrayList=client.target(HOST).path(path_name).request("application/json").get(new GenericType<ArrayList<TopologyJson>>(){});
					
					//adding topology json objects to queue
					if(topologyArrayList!=null){
						for(ListIterator<TopologyJson> iterator=topologyArrayList.listIterator();iterator.hasNext();){
							topologyQueue.add(iterator.next());
					     }
						//storing last id
					resultId=topologyArrayList.get(topologyArrayList.size()-1).getId();
				}
				
			}
			
			}
						
		}
		catch(Exception e){
			System.out.println("Exception"+e);
			
		}
		return resultId;
	}

}
