package com.ericsson.navigator.esm.model.alarm;

import com.ericsson.navigator.esm.model.AddressInformation;
import com.ericsson.navigator.esm.model.Protocol;

public interface ProtocolAlarmList<AI extends AddressInformation> extends Protocol<Alarm.State, Alarm, AI>, ManagedAlarmList {
 
	void setSystemAvailable();
	void setSystemUnavailable(String reason);
	boolean isAvailable();
}
