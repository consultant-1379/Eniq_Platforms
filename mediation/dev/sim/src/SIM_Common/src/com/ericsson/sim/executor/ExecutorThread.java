package com.ericsson.sim.executor;

import java.util.ArrayList;

import com.ericsson.sim.model.node.Node;
import com.ericsson.sim.model.protocol.Protocol;
import com.ericsson.sim.model.protocol.snmp.CounterSet;

public interface ExecutorThread extends Runnable{	
	@Override
	public void run();
	public void init(Node node, Protocol protocol);
	public void init(Node node, Protocol protocol, CounterSet cs, ArrayList<String> disabledCounters);
}
