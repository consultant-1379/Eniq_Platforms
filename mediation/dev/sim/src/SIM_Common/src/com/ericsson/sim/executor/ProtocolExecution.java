package com.ericsson.sim.executor;

import com.ericsson.sim.model.node.Node;
import com.ericsson.sim.model.protocol.Protocol;

public interface ProtocolExecution {
	
	public void execute(Node node, Protocol protocol, ExecutionPool executor);

}
