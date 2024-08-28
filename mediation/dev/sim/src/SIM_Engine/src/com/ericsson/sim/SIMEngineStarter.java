package com.ericsson.sim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.ericsson.sim.constants.SIMEnvironment;
import com.ericsson.sim.engine.EngineComponent;
import com.ericsson.sim.engine.model.TopologyHandler;
import com.ericsson.sim.engine.scheduler.RopIntervalScheduler;
import com.ericsson.sim.model.pools.DisabledPool;
import com.ericsson.sim.model.pools.NodePool;
import com.ericsson.sim.model.pools.ProtocolPool;
import com.ericsson.sim.model.pools.RopIntervalPool;
import com.ericsson.sim.monitor.ConfigMonitor;
import com.ericsson.sim.rmi.RMIcomponent;
import com.ericsson.sim.sftp.serialization.appended.FilePropertyManager;

public class SIMEngineStarter {

	Logger log = LogManager.getLogger(this.getClass().getName());
	public List<EngineComponent> components = null;
	
	public void init(RMIcomponent rmi) {
		try{
			
			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					log.info("SIM shutdown called");				
					shutdown();
				}
			});
		
			//Load configuration objects
			log.debug("Loading configuration");
			RopIntervalPool.getInstance().loadPersistedFile(SIMEnvironment.ROPINTERVALCONFIG);
			ProtocolPool.getInstance().loadPersistedFile(SIMEnvironment.PROTOCOLCONFIG);
			ProtocolPool.getInstance().loadPersistedFile(SIMEnvironment.PROCUSPROTOCOLCONFIG);
			NodePool.getInstance().loadPersistedFile(SIMEnvironment.TOPOLOGYEXTRACONFIG);
			DisabledPool.getInstance().parseInput(SIMEnvironment.DISABLEDCOUNTERSCONFIG);
			
			
			//Initialize each engine components
			log.debug("Initializing engine components");
			components = new ArrayList<EngineComponent>();
			
			log.debug("Initializing ROP Interval Scheduler");
			RopIntervalScheduler scheduler = RopIntervalScheduler.getInstance();
			components.add(scheduler);
			
			log.debug("Initializing Config Monitor");
			ConfigMonitor opt = new ConfigMonitor();
			components.add(opt);	
		
			//Start each engine component
			for(EngineComponent ec : components){
				ec.startup();
			}
			
			components.add(rmi);
			
			log.debug("Initializing Topology Handler");
			TopologyHandler th = new TopologyHandler(scheduler);

			
        	th.startup();
		
		}catch(Exception e){
			log.error("Unable to start SIM. Exception caught: " , e);
		}
	}

	public void shutdown() {
		Collections.reverse(components);
		for (EngineComponent ec : components) {
			log.debug("Shutting down " + ec.getName());
			ec.exit();
		}
		try {
			NodePool.getInstance().writePersistedFile(SIMEnvironment.CONFIGPATH);
			FilePropertyManager.getInstance().writePerFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
