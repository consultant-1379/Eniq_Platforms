package com.ericsson.sim.snmp;

import java.util.ArrayList;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.ericsson.sim.executor.ExecutorThread;
import com.ericsson.sim.model.node.Node;
import com.ericsson.sim.model.pools.DisabledPool;
import com.ericsson.sim.model.protocol.Protocol;
import com.ericsson.sim.model.protocol.sftp.SFTPproperties;
import com.ericsson.sim.model.protocol.snmp.CounterSet;
import com.ericsson.sim.model.protocol.snmp.SNMPproperties;
import com.ericsson.sim.model.protocol.snmp.disable.DisabledCollection;
import com.ericsson.sim.snmp.execution.GetNextThread;
import com.ericsson.sim.snmp.execution.GetThread;
import com.ericsson.sim.snmp.packet.RequestPackets;

public class SNMPExecutionThread implements ExecutorThread{

	private Logger log = LogManager.getLogger(this.getClass().getName());
	private Node node;
	private SNMPproperties properties;
	
	@Override
	public void init(Node node, Protocol protocol) {
		this.node = node;
		this.properties = (SNMPproperties) protocol;  
		
	}
	
	@Override
	public void run() {
		log.info("SNMP execution started, collecting " + node.getName() +  " - " + properties.getName());
		
		RequestPackets.getInstance().getPackets(node);
		
		//get disabled counters NodeID
		DisabledCollection disabled = DisabledPool.getInstance().getDisabledCollection(node.getID());
		
		for(CounterSet cs : properties.getCounterSets()){
			ArrayList<String> disabledCounters = new ArrayList<String>();

			if(disabled.containsCounterset(cs.getName())){
				disabledCounters.addAll(disabled.getCounterNames(cs.getName()));
			}
			
			if(cs.getProperty("type").equals("Tabular")){
				GetNextThread gne = new GetNextThread();
				gne.init(node, properties, cs, disabledCounters);
				log.debug("Get Next scheduled for " + cs.getName());
				gne.run();
			}else{
				GetThread gne = new GetThread();
				gne.init(node, properties, cs, disabledCounters);
				log.debug("Get scheduled for " + cs.getName());
				gne.run();
			}
		}
		
	}

	

	@Override
	public void init(Node node, Protocol protocol, CounterSet cs,
			ArrayList<String> disabledCounters) {
		// TODO Auto-generated method stub
		
	}

}
