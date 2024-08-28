package com.ericsson.sim.monitor;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.ericsson.sim.constants.SIMEnvironment;
import com.ericsson.sim.engine.model.TopologyHandler;
import com.ericsson.sim.engine.scheduler.RopIntervalScheduler;
import com.ericsson.sim.model.pools.DisabledPool;
import com.ericsson.sim.monitor.eniq.EniqSystemStatusHandler;

public class MonitoringThread extends Thread{

	Logger log = LogManager.getLogger(this.getClass().getName());
	private WatchService watcher;
	protected boolean keepRunning = true;
	
	
	@Override
	public void run() {
		try{
			watcher = FileSystems.getDefault().newWatchService();
	        Path dir = Paths.get(SIMEnvironment.CONFIGPATH);
	        dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
	             	             
	        while (keepRunning){
	        	WatchKey key;
                try{
                	key = watcher.take();
                }catch (InterruptedException|ClosedWatchServiceException e  ) {
                	return;
                }
                
                
            	WatchEvent<?> event = key.pollEvents().get(0);
            	WatchEvent.Kind<?> kind = event.kind();
                
            	
                @SuppressWarnings("unchecked")
                WatchEvent<Path> ev = (WatchEvent<Path>) event;
                Path fileName = ev.context();
                try{
	                if (kind == ENTRY_MODIFY || kind == ENTRY_CREATE){
	                
		                if (fileName.toString().equals("topology.xml")) {
		                	log.info("Change in topology file, calling topology handler");
		                	TopologyHandler th = new TopologyHandler(RopIntervalScheduler.getInstance());
		                	th.startup();
		                }else if(fileName.toString().equals("enginestatus.txt")){
		                	EniqSystemStatusHandler essh = new EniqSystemStatusHandler();
		        			essh.start();
		                }else if(fileName.toString().equals("DisabledSNMPCounters.xml")){
		                	DisabledPool.getInstance().parseInput(SIMEnvironment.DISABLEDCOUNTERSCONFIG);
		                }
	                }
                }catch(Exception e){
                	log.error("Error occured while handling file change. ", e);
                }
	            boolean valid = key.reset();
	            if (!valid) {
	            	break;
	            }
	        }
		}catch (Exception ex) {
			log.error("Error reading file: ", ex);
		}
		
	}
	
	public void exit(){
		try {
			keepRunning = false;
			watcher.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
