package com.ericsson.navigator.esm.manager.pm.file.remote.plugin;

import java.util.Date;
import java.util.Map;

public interface CounterSetCallback {

	void pushCounterSet(String moid, Date endTime, Map<String, String> counters);
	void info(String message, Exception e);
	void debug(String message, Exception e);
	void warn(String message, Exception e);
	void error(String message, Exception e);
	void info(String message);
	void debug(String message);
	void warn(String message);
	void error(String message);
}
