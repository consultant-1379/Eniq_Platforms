package com.ericsson.navigator.esm.model.alarm;

import com.ericsson.navigator.esm.util.jmx.MDynamicBean;

public interface AbstractProtocolAlarmListBean extends MDynamicBean {

	int getAlarmCount();
	String getAddress();
}
