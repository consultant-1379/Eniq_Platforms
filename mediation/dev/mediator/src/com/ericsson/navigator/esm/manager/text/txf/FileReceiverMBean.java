package com.ericsson.navigator.esm.manager.text.txf;

import java.io.File;
import java.util.List;

import com.ericsson.navigator.esm.util.jmx.MBean;

public interface FileReceiverMBean extends MBean {

	List<File> getMonitoredAlarmFiles();
	long getTotalTXFEventsReceived();
	void resetTXFEventsReceived();
	float getIncomingEventRate();
}
