package com.ericsson.navigator.esm.agent.rmi;

import com.ericsson.navigator.esm.util.jmx.MDynamicBean;

public interface DefaultAgentSessionMBean extends MDynamicBean{

	String getID();
	int getEventQueueSize();
	int getEventsDelivered();
	float getOutgoingEventRate();
}
