package com.ericsson.navigator.esm.manager.text.txf;

import java.util.EventListener;

import com.ericsson.navigator.esm.model.alarm.Alarm;

public interface AlarmFileListener extends EventListener {

	void receivedAlarm(final Alarm alarm);
	void receivedHeartbeat();
	String getFDN();
}
