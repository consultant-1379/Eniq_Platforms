package com.ericsson.sim.sftp;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


import com.ericsson.sim.constants.SIMEnvironment;
import com.ericsson.sim.exception.SIMException;
import com.ericsson.sim.executor.ExecutorThread;
import com.ericsson.sim.model.interval.RopInterval;
import com.ericsson.sim.model.node.Node;
import com.ericsson.sim.model.pools.RopIntervalPool;
import com.ericsson.sim.model.protocol.Protocol;
import com.ericsson.sim.model.protocol.sftp.SFTPproperties;
import com.ericsson.sim.model.protocol.snmp.CounterSet;
import com.ericsson.sim.plugin.PluginLoader;
import com.ericsson.sim.sftp.serialization.SFTPserFile;
import com.ericsson.sim.watchdog.Threadhelper;

public class SFTPExecutionThread implements ExecutorThread{
	
	private Logger log = LogManager.getLogger(this.getClass().getName());
	private Node node;
	private SFTPproperties properties;
	private PluginLoader pluginloader = PluginLoader.getInstance();
	
	@Override
	public void init(Node node, Protocol protocol){
		this.node = node;
		this.properties = (SFTPproperties) protocol;  
		
	}
	
	@Override
	public void run() {
		/*
		 * 1. timer
		 * 2. discovery & collection
		 * 3. pre processing
		 * 4. processing
		 * 5. post processing
		 */
		
		//--------------------------------
		SFTPserFile serfile = new SFTPserFile();
		String serPath = SIMEnvironment.PERSPATH+"/" + node.getID()+"_"+properties.getID()+".ser";
		boolean firstRop = serfile.containsSERFile(serPath);
		
		RopInterval rop = RopIntervalPool.getInstance().getInterval(properties.getInterval());
		long ropValue = TimeUnit.MINUTES.toMillis(Integer.valueOf(rop.getProperty("ROP")));
		
		//String firstRop = (String) node.getProperty("firstRop");
		
		final Thread currThread = Thread.currentThread();
		Threadhelper threadhelper = new Threadhelper(currThread, (ropValue*2)/3);
		try{
			threadhelper.startTimer();		 
			
			if(properties.containsProperty("operationThread")){
				pluginloader.getPlugin(properties.getProperty("operationThread")).execute(node, properties);
			}else{
				throw new SIMException("No operation thread property has been configured");
			}
			
			threadhelper.cancelTimerTask();
			
			//if(firstRop != null && firstRop.contains(String.valueOf(properties.getID()))){
			if(firstRop){//  && firstRop.contains(String.valueOf(properties.getID()))){
				if(properties.containsProperty("PreProcessing")){
					pluginloader.getPlugin(properties.getProperty("PreProcessing")).execute(node, properties);
				}
				if(properties.containsProperty("Processing")){
					pluginloader.getPlugin(properties.getProperty("Processing")).execute(node, properties);
				}
				if(properties.containsProperty("PostProcessing")){
					pluginloader.getPlugin(properties.getProperty("PostProcessing")).execute(node, properties);
				}
			}
		}catch(Exception e){
			log.error("Exception: ", e);
		}finally{
			threadhelper.cancelTimerTask();
			threadhelper = null;
		}
		
	}

	@Override
	public void init(Node node, Protocol protocol, CounterSet cs, ArrayList<String> disabledCounters) {}

}
