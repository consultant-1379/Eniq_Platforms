package com.ericsson.navigator.esm.manager.snmp;

import com.ericsson.navigator.esm.util.jmx.MBean;

public interface TrapReceiverMBean extends MBean {

	String getInterface();
	int getPort();
	long getTotalSNMPTrapsReceived();
	void resetSNMPTrapsReceived();
	float getIncomingTrapRate();
}
