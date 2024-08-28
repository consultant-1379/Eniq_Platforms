package com.ericsson.navigator.esm.model.conversion;

import java.util.Set;

import com.ericsson.navigator.esm.util.jmx.MDynamicBean;

public interface ConversionControllerMBean  extends MDynamicBean {
	Set<String> getConversionPlugins();
	int getConversionPluginsSize();
}
