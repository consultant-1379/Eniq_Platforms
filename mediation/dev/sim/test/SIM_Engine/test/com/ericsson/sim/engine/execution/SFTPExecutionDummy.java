package com.ericsson.sim.engine.execution;

import com.ericsson.sim.executor.ExecutionPool;
import com.ericsson.sim.executor.ProtocolExecution;
import com.ericsson.sim.model.node.Node;
import com.ericsson.sim.model.protocol.Protocol;

public class SFTPExecutionDummy implements ProtocolExecution {

	@Override
	public void execute(Node node, Protocol protocol, ExecutionPool executor) {
		System.setProperty("sftpScheduleExecutionTest", "passed".toUpperCase());
		
	}

}
