package com.ericsson.navigator.esm.model.alarm.snmp.generic;

import java.util.List;

import com.ericsson.navigator.esm.util.jmx.MBean;

public interface MibControllerMBean extends MBean {

	List<String> getLoadedMibFiles();
}
