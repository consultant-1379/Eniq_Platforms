package com.ericsson.navigator.esm.manager.pm.file.local.irp;

import com.ericsson.navigator.esm.util.jmx.MDynamicBean;

public interface CounterSetFileLoaderMBean extends MDynamicBean {
	
	int getTotalListeners();
	int getTotalParsers();
	long getTotalReceivedCounterSets();
	void resetTotalReceivedCounterSets();
	long getTotalParsedFiles();
	void resetTotalParsedFiles();
	float getParsingFileRate();
	String getListeners();
}