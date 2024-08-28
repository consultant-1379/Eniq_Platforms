package com.ericsson.navigator.esm.manager.pm.file.local.irp;

import java.util.Date;
import java.util.EventListener;
import java.util.Map;

public interface CounterSetFileListener extends EventListener {

	String getFDN();
	void receivedCounterSet(final String nedn, final String moid, final int gp, 
			final Date endDate, final Map<String, String> observedCounters);
}