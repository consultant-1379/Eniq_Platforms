package com.ericsson.sim.engine.execution;

import java.util.ArrayList;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.ericsson.sim.executor.ExecutionPool;
import com.ericsson.sim.executor.ProtocolExecution;
import com.ericsson.sim.model.node.Node;
import com.ericsson.sim.model.properties.RuntimeProperties;
import com.ericsson.sim.model.protocol.Protocol;
import com.ericsson.sim.model.protocol.sftp.SFTPproperties;
import com.ericsson.sim.model.protocol.snmp.SNMPproperties;

public class ExecutionManager {

	Logger log = LogManager.getLogger(this.getClass().getName());
	private static ExecutionManager obj;
	private static int instance = 0;
	private ArrayList<ExecutionPool> sftpExecutors;
	private ArrayList<ExecutionPool> snmpExecutors;
	private ClassLoader classLoader = ExecutionManager.class.getClassLoader();
	
	private ExecutionManager(){
		obj = this;
		sftpExecutors = new ArrayList<ExecutionPool>();
		snmpExecutors = new ArrayList<ExecutionPool>();
		
		int noOfSFTPThreads = Integer.parseInt(RuntimeProperties.getInstance().getProperty("NoOfSFTPExecutorThreads"));
		int noOfSFTPexec = Integer.parseInt(RuntimeProperties.getInstance().getProperty("NoOfSFTPExecutors"));
		for(int count=0; count<noOfSFTPexec; count++){
			sftpExecutors.add(new ExecutionPool(noOfSFTPThreads));
		}
		
		int noOfSNMPThreads = Integer.parseInt(RuntimeProperties.getInstance().getProperty("NoOfSNMPExecutorThreads"));
		int noOfSNMPexec = Integer.parseInt(RuntimeProperties.getInstance().getProperty("NoOfSNMPExecutors"));
		for(int count=0; count<noOfSNMPexec; count++){
			snmpExecutors.add(new ExecutionPool(noOfSNMPThreads));
		}
		
		
	}
	
	public void scheduleExecution(Node node, Protocol protocol){
		try{
			ExecutionPool executor = null;
			if(protocol instanceof SNMPproperties){
				for(ExecutionPool es : snmpExecutors){
					if(executor == null){
						executor = es;
					}else if(es.getQueuedCount() < executor.getQueuedCount()){
						executor = es;
					}
				}

			}else if(protocol instanceof SFTPproperties){
				for(ExecutionPool es : sftpExecutors){
					if(executor == null){
						executor = es;
					}else if(es.getQueuedCount() < executor.getQueuedCount()){
						executor = es;
					}
				}
			}
			
			Class<?> aClass = classLoader.loadClass(protocol.getExecutionThreadName());
			ProtocolExecution execution = (ProtocolExecution) aClass.newInstance();
			execution.execute(node, protocol, executor);
		
		}catch(Exception e){
			log.error("Execution Error: " , e);
		}
		
		
	}
	
	public static ExecutionManager getInstance(){
		if(instance ==0){
			instance = 1;
			return new ExecutionManager();
		}
		if(instance == 1){
			return obj;
		}
		return null;
	}
}
