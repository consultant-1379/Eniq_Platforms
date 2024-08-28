package com.ericsson.sim.sftp;

import com.ericsson.sim.model.node.Node;
import com.ericsson.sim.model.protocol.Protocol;
import com.ericsson.sim.plugin.PluginParent;

public class PluginParentMockTest implements PluginParent {

	@Override
	public void execute(Node node, Protocol protocol) {
		System.setProperty("SFTPExecutionThreadTestValue", "passed");
		
	}

	
}
