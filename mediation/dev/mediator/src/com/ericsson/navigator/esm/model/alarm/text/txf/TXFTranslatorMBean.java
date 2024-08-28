package com.ericsson.navigator.esm.model.alarm.text.txf;

import java.util.List;

import com.ericsson.navigator.esm.util.jmx.MBean;

public interface TXFTranslatorMBean extends MBean {

	List<String> getLoadedTranslationMaps();
}
