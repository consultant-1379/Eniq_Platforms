package com.ericsson.navigator.esm.model.pm;

import java.util.List;

import com.ericsson.navigator.esm.util.jmx.MDynamicBean;

public interface AbstractProtocolCounterSetListBean extends MDynamicBean {

	int getCounterSetCount();
	String getAddress();
	List<String> getCounterSetSchedulingInformation();
}
