package com.ericsson.sim.sftp;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.ericsson.sim.executor.ExecutionPool;
import com.ericsson.sim.executor.ProtocolExecution;
import com.ericsson.sim.model.node.Node;
import com.ericsson.sim.model.protocol.Protocol;

public class SFTPExecution implements ProtocolExecution{

	Logger log = LogManager.getLogger(this.getClass().getName());
	private String status="";
	
	@Override
	public void execute(Node node, Protocol protocol, ExecutionPool executor) {
		if(checkSystemStatus()){
			SFTPExecutionThread execThread = new SFTPExecutionThread();
			execThread.init(node, protocol);		
			executor.addToExecutor(execThread);	
		}else{
			log.warn(status + node.getName() + " - " + protocol.getName() + " collection skipped.");
		}
	}
	
	public boolean checkSystemStatus(){
		//Get the ENIQ Engine status
		String engStatus = System.getProperty("ENIQEngineStatus", "Failed");
		String memStatus = System.getProperty("ENIQMemoryStatus", "Failed");
		if(!engStatus.equalsIgnoreCase("online")){
			status = "ENIQ Engine not online. ";
			return false;
		}
		else if(memStatus.equalsIgnoreCase("MEMORYFULL")){
			status = "Memory usage on disk is >= 80%. ";
			return false;
		}
		
		return true;
	}

}
