package com.distocraft.dc5000.etl.fls;

//import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
//import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import com.distocraft.dc5000.common.StaticProperties;
import com.ericsson.eniq.common.DatabaseConnections;

import ssc.rockfactory.RockFactory;
import ssc.rockfactory.RockResultSet;

public class Main {
	
	//to store id values
	public Map<String,Integer> tokenMap;
	
	//To store fls query responses
	public LinkedBlockingQueue<Runnable> pmQueue;
	public LinkedBlockingQueue<TopologyJson> topologyQueue;
	
	//threadPoolExecutor reference
	private static ThreadPoolExecutor threadPoolExecutor;

	
	public static Date lastDate=new Date();

	//to store ENM Server Details
	ENMServerDetails cache;
	
	Logger log;
	
	//To collect Nodes assigned to each Eniq-S
	public ArrayList<String> getNodeList() throws SQLException
	{
		RockFactory dwhdb=null;
		String query=null;
		final RockResultSet results;
		try{
			dwhdb = DatabaseConnections.getDwhDBConnection();
			
			query="select distinct nodeType from dc.Assigned_Nodes";
			log.finest("NodeTypeQuery\t"+query);
			results = dwhdb.setSelectSQL(query);
			
			ArrayList<String> returnNodeList=(ArrayList<String>)results.getCollection();
			log.finest("NodeTypeQuery result size\t"+returnNodeList.size());
			
			return returnNodeList;
					
		}catch(Exception e){
			System.out.println(e+"exception");
		}finally{
			dwhdb.getConnection().close();
		}
		return null;
		
	}
	
	

	
	//main method
	public static void main(String[] args) throws SecurityException, IOException  {
		
		
		final Main main=new Main();
		
		//log file creation for Fls querying process
		
		main.log=Logger.getLogger("FLSLog");
		FileHandler fileHandler=new FileHandler("FlsSymbolicLink.log");
		main.log.addHandler(fileHandler);
		
		
		//cache initialization(enm server details)
		HashMap<String, ENMServerDetails> enmServerDetails=CacheENMServerDetails.getInstance(main.log);
		
		main.cache=enmServerDetails.get(args[0]);
		
	/*	
		//time for querying topology
		Timer topologyTimer=new Timer();
		final int topologyQueryInterval=15*60*1000;
		final int topologyStartDelay=15*30*1000;
		topologyTimer.schedule(new TimerTask(){
		
			FlsQueryTopology flsQueryTopology=FlsQueryTopology.getInstance(main.cache,main.log,main.topologyQueue);
	
			int preId,postId;
			
			@Override
			public void run() {
				
					if(main.tokenMap.containsKey("TOPOLOGY")){
						//topology fls request when id is available in map
						
						preId=main.tokenMap.get("TOPOLOGY").intValue();
						main.log.fine("id value of Topology before querying:\t"+preId);
						postId=flsQueryTopology.queryTopology(true,preId);
						
						//storing last id in map
						main.log.fine("id value of topology after querying"+postId);
						if(postId>preId){
							main.tokenMap.put("TOPOLOGY",new Integer(postId));
						}
					}
					else{
						//topology fls request when id is available in map
						
						main.log.fine("topology request for first time");
						postId=flsQueryTopology.queryTopology(false,-1);
						
						//storing last id in map
						main.log.fine("id value of topology after querying"+postId);
						if(postId>preId){
							main.tokenMap.put("TOPOLOGY",new Integer(postId));
						}
					}
					
					//to create symbolic link for topology files
					Thread t=new Thread(new TopologyQueueHandler(Main.topologyQueue),"toplogyhandler");
					t.start();
				}
			},topologyStartDelay,topologyQueryInterval);
				
		//timer for scheduling to query 
	*/	
		Timer pmTimer=new Timer();
		final int pmQueryInterval=3*60*1000;
		final int pmStartDelay=30*30*1000;
		
		pmTimer.schedule(new TimerTask(){
			
			
			FlsQueryPm flsQueryPM=FlsQueryPm.getInstance(main.cache,main.log,main.pmQueue);
			int preId,postId;
			
			@Override
			public void run() {
				// to storelist node types for subscription
				ArrayList<String> nodeTypeList=null;
				try {
					nodeTypeList=main.getNodeList();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
				//to do subscription for every nodetype
				for(String nodeType:nodeTypeList){
										
					if(main.tokenMap.containsKey(nodeType)){
						//pm fls request when id is available in map
						preId=main.tokenMap.get(nodeType).intValue();
						main.log.fine("pm request for node :\t"+nodeType+"\t id before get request:"+preId);
						postId=flsQueryPM.queryPM(nodeType,true,preId);
						
						//storing last id in map
						main.log.fine("pm request for node :\t"+nodeType+"\t id after get request:"+postId);
						if(postId>preId){
							main.tokenMap.put(nodeType,new Integer(postId));
						}
					}
					else{
						//pm fls request when id is not available in map
						main.log.fine("first time pm request for node :\t"+nodeType);
						postId=flsQueryPM.queryPM(nodeType,false,-1);	
						
						//storing last id in map
						main.log.fine("pm request for node :\t"+nodeType+"\t id after get request:"+postId);
						if(postId>preId){
							main.tokenMap.put(nodeType,new Integer(postId));
						}
					}
				}
				
			}
			
		}, pmStartDelay, pmQueryInterval);
		
		//to run blocking queue forscreating symbolic link for pm files
		final int coreSize = Integer.parseInt(StaticProperties.getProperty("jmsConsumerThreadPoolCoreSize", "15"));
		final int maxSize = Integer.parseInt(StaticProperties.getProperty("jmsConsumerThreadPoolMaxSize", "30"));
		
		threadPoolExecutor = new ThreadPoolExecutor(coreSize,maxSize,100001, TimeUnit.MILLISECONDS, main.pmQueue);
		threadPoolExecutor.prestartAllCoreThreads();
		
		//Timer for scheduling id to serialized for every one hour
		Timer idTimer=new Timer();
		
		final int persistedIdInterval=60*60*1000;
		final int persistedstartDelay=60*60*1000;
		
		final FileOutputStream fout=new FileOutputStream("Persistedid.txt");  
		final ObjectOutputStream out=new ObjectOutputStream(fout);
		
		idTimer.schedule(new TimerTask(){
		
			PersistedToken persistedToken=new PersistedToken(main.tokenMap);
			
			@Override
			public void run() {
					try{
					persistedToken.setTokenMap(main.tokenMap);	
					main.log.fine("token map value "+main.tokenMap.toString()+"\t at \t"+new Date());
					out.writeObject(persistedToken);
					out.flush();
					out.close();
					}
					catch(Exception e){
						main.log.warning("exception at persisisted id time"+e);
					}
				
				}
			},persistedstartDelay,persistedIdInterval);
		
		fout.close();
		
	}
	
}
