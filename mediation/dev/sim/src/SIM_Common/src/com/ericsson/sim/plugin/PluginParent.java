package com.ericsson.sim.plugin;

import com.ericsson.sim.model.node.Node;
import com.ericsson.sim.model.protocol.Protocol;

public interface PluginParent {

	public void execute(Node node, Protocol protocol);
	
}
