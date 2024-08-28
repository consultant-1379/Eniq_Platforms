package com.ericsson.navigator.esm.model.alarm.snmp.generic.mst;

import java.util.List;

import com.ericsson.navigator.esm.util.jmx.MBean;

public interface TrapTranslationLoaderMBean extends MBean{
	
	List<String> getLoadedTranslationFiles();
}
