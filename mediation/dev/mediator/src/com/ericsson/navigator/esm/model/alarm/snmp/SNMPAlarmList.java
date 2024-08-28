package com.ericsson.navigator.esm.model.alarm.snmp;

import com.ericsson.navigator.esm.manager.snmp.ManagedSystemTrapListener;
import com.ericsson.navigator.esm.manager.snmp.SNMPProtocol;
import com.ericsson.navigator.esm.model.alarm.Alarm;
import com.ericsson.navigator.esm.model.alarm.ProtocolAlarmList;

public interface SNMPAlarmList<AI extends SNMPAddressInformation>
		extends ProtocolAlarmList<AI>, SNMPProtocol<Alarm.State, Alarm, AI>, ManagedSystemTrapListener {

	void setSystemUptime(final long systemUptime);
	long getSystemUptime();
}
