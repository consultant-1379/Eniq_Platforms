package com.ericsson.navigator.esm.manager.pm.file.remote;

import com.ericsson.navigator.esm.util.jmx.MDynamicBean;

public interface ParserPluginControllerMBean  extends MDynamicBean {

	int getTotalConfigFiles();
	String getConfigFiles();
}
	
