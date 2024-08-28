package com.ericsson.sim.engine.scheduler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.ericsson.sim.engine.EngineComponent;
import com.ericsson.sim.engine.execution.ExecutionManager;
import com.ericsson.sim.exception.SIMException;
import com.ericsson.sim.model.interval.RopInterval;
import com.ericsson.sim.model.node.Node;
import com.ericsson.sim.model.pools.NodePool;
import com.ericsson.sim.model.pools.ProtocolPool;
import com.ericsson.sim.model.pools.RopIntervalPool;
import com.ericsson.sim.model.protocol.Protocol;

public class RopIntervalScheduler implements EngineComponent{

	Logger log = LogManager.getLogger(this.getClass().getName());
	private RopIntervalPool intervals = RopIntervalPool.getInstance();
	private Thread scheduler = null;
	private static RopIntervalScheduler obj;
	private static int instance = 0;
	public boolean running;
	public boolean firstRop;
	private NodePool nodePool = NodePool.getInstance();
	private ProtocolPool protoPool = ProtocolPool.getInstance();
	
	private RopIntervalScheduler(){
		obj = this;
	}
	
	public void scheduleRopIntervals(){
		try{
			
			final ExecutionManager execManager = ExecutionManager.getInstance();
			
			if(scheduler != null){
				running = false;
				scheduler.interrupt();
				scheduler=null;
				log.info("Reintialising the scheduler");
			}
			
			log.debug("Recalculating the next ROP");
			running = true;
			firstRop = true;
			scheduler = new Thread(new Runnable(){
				public void run(){
					try{
						while(running){
							long timeToNextRop = -1;
							TreeMap<Integer, HashMap<Integer, ArrayList<Integer>>> ropsToExectue = new TreeMap<Integer, HashMap<Integer, ArrayList<Integer>>>();
							
							Set<Integer> rops = intervals.getRopIntervals().keySet();
							for(Integer ropID : rops){
								RopInterval ropInterval = intervals.getInterval(ropID);
								
								if(ropInterval.getNodeIDs().size()>0){
									ArrayList<Integer> nodeIds = ropInterval.getNodeIDs();
									for(Integer id : nodeIds){
										Node node = nodePool.getNode(id);
										String offset = node.getProperty("Offset");

										int ropKey = getRopKey(ropInterval, node);
										long sleep = calculateSleepTime(ropInterval, offset, firstRop);									
										if(timeToNextRop == -1){
											timeToNextRop = sleep;
											HashMap<Integer, ArrayList<Integer>> nodelist = new HashMap<Integer, ArrayList<Integer>>();
											nodelist.putAll(addRopToExecute(ropsToExectue.get(ropKey), id, ropID));
											ropsToExectue.put(ropKey, nodelist);
											
										}else if(sleep < timeToNextRop){
											timeToNextRop = sleep;
											ropsToExectue.clear();
											HashMap<Integer, ArrayList<Integer>> nodelist = new HashMap<Integer, ArrayList<Integer>>();
											nodelist.putAll(addRopToExecute(ropsToExectue.get(ropKey), id, ropID));
											ropsToExectue.put(ropKey, nodelist);
											
										}else if(sleep == timeToNextRop){
											timeToNextRop = sleep;
											HashMap<Integer, ArrayList<Integer>> nodelist = new HashMap<Integer, ArrayList<Integer>>();
											nodelist.putAll(addRopToExecute(ropsToExectue.get(ropKey), id, ropID));
											ropsToExectue.put(ropKey, nodelist);
										}
										
									}
								}
								
							}
							log.debug("Next ROP in: " + timeToNextRop/1000+" seconds");
							Thread.sleep(timeToNextRop);
							
							DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HHmm");
							String time = dateFormat.format(Calendar.getInstance().getTime());
							
							
							for(Integer rop : ropsToExectue.keySet()){
								HashMap<Integer, ArrayList<Integer>> nodelist = ropsToExectue.get(rop);
								
								for(Integer nodeID : nodelist.keySet()){
									Node node = nodePool.getNode(nodeID);
									ArrayList<Integer> ropIds = nodelist.get(nodeID);
									
									ArrayList<Integer> protoIds = node.getProtocols();
									
									for(Integer protoID : protoIds){
										Protocol proto = protoPool.getProtocol(protoID);
										if(ropIds.contains(proto.getInterval())){
											String timeString = node.getName() + ":" + proto.getName() + ":" + "ROPStartTime";
											System.setProperty(timeString, dateFormat.format(Calendar.getInstance().getTime()));
											
											log.info("Scheduling "+node.getName() + " : " + proto.getName() + " for execution");
											execManager.scheduleExecution(node, proto);
										}
									}

								}
								
								
							}
							
							
						firstRop = false;		
						}
					}catch(InterruptedException e){
						//supressing interrupt exception.
					}
	
				}
			});	
			scheduler.start();
		
		
		}catch(Exception e){
        	log.error("Scheduler exception : ", e);

		}
	}
	
	public int getRopKey(RopInterval ropInterval, Node node){		
		int rop = Integer.valueOf(ropInterval.getProperty("ROP"));
		String nodeOffset = node.getProperty("Offset");
		int offset = 0;
		if(nodeOffset != null){
			offset = Integer.valueOf(nodeOffset);
		}else{
			offset = Integer.valueOf(ropInterval.getProperty("Offset"));
		}
		
		return rop+offset;
	}
	
	public long calculateSleepTime(RopInterval ropInterval, String nodeOffset, boolean firstRop){
		long intialDelay=0;
		int rop = Integer.valueOf(ropInterval.getProperty("ROP"));
		int offset = 0;
		if(nodeOffset != null){
			offset = Integer.valueOf(nodeOffset);
		}else{
			offset = Integer.valueOf(ropInterval.getProperty("Offset"));
		}
		
		int minutes = Calendar.getInstance().get(Calendar.MINUTE);
		minutes = (minutes / rop)*rop+rop;
		
		intialDelay = minutes - Calendar.getInstance().get(Calendar.MINUTE);
		
		if(intialDelay>=60){
			int hoursValue = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
			double fraction = (hoursValue*60.0)/rop;
			double difference = (hoursValue*60)/rop;
			fraction = fraction - difference;
			intialDelay = intialDelay-Math.round(rop*fraction);
		}
		
		//handle offset per node
		int offsetDelta = rop - offset;
		if( (intialDelay-offsetDelta) > 0){
			intialDelay = intialDelay - offsetDelta;
		}else{
			intialDelay = intialDelay + offset;
		}
		
		if(firstRop){
			if(intialDelay<=1){
				intialDelay = intialDelay+rop;
			}
		}
		
		intialDelay = intialDelay*60; //convert the time to seconds for better accuracy
		intialDelay = intialDelay - Calendar.getInstance().get(Calendar.SECOND);
		   
		return TimeUnit.SECONDS.toMillis(intialDelay);
	}
	
	public HashMap<Integer, ArrayList<Integer>> addRopToExecute(HashMap<Integer, ArrayList<Integer>> nodelist, Integer nodeid, Integer ropID){
		if(nodelist == null){
			nodelist = new HashMap<Integer, ArrayList<Integer>>();
		}
		
		ArrayList<Integer> rops = new ArrayList<Integer>();
		if(nodelist.containsKey(nodeid)){
			rops.addAll(nodelist.get(nodeid));
		}
		
		rops.add(ropID);
		nodelist.put(nodeid, rops);
		
		return nodelist;
	}
	
	
	@Override
	public void startup(){
		running = true;
	}

	@Override
	public void exit(){
		log.info("Scheduler shutdown called");
		running = false;
		if(scheduler.isAlive()){
			scheduler.interrupt();
		}
	}
	
	@Override
	public String getName() {
		return this.getClass().getName();
	}
	
	public static RopIntervalScheduler getInstance(){
		if(instance ==0){
			instance = 1;
			return new RopIntervalScheduler();
		}
		if(instance == 1){
			return obj;
		}
		
		return null;
	}

}