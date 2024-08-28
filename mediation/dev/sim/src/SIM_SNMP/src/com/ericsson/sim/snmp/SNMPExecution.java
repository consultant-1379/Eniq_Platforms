package com.ericsson.sim.snmp;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.ericsson.sim.executor.ExecutionPool;
import com.ericsson.sim.executor.ProtocolExecution;
import com.ericsson.sim.model.node.Node;
import com.ericsson.sim.model.protocol.Protocol;
import com.ericsson.sim.model.protocol.snmp.SNMPproperties;

public class SNMPExecution implements ProtocolExecution{

	private Logger log = LogManager.getLogger(this.getClass().getName());
	private String status = "";
	
	@Override
	public void execute(Node node, Protocol protocol, ExecutionPool executor) {
		if(checkSystemStatus()){
			SNMPExecutionThread execThread = new SNMPExecutionThread();
			execThread.init(node, protocol);		
			executor.addToExecutor(execThread);	
		}else{
			log.warn(status + node.getName() + " - " + protocol.getName() + " collection skipped.");
		}
		
	}
	
	private boolean checkSystemStatus(){
		//Get the ENIQ Engine status
		String memStatus = System.getProperty("ENIQMemoryStatus", "Failed");
		if(memStatus.equalsIgnoreCase("MEMORYFULL")){
			status = "Memory usage on disk is >= 80%. ";
			return false;
		}
		
		return true;
	}
	
}
