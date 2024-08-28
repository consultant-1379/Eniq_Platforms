package com.ericsson.navigator.esm.model.alarm.snmp.irp;

import com.adventnet.snmp.snmp2.SnmpPDU;
import com.ericsson.navigator.esm.model.alarm.Alarm;
import com.ericsson.navigator.esm.model.alarm.snmp.SNMPSynchronizationRequest;

public class IRPSynchronizationRequest extends SNMPSynchronizationRequest<IRPAlarmList> {

	public IRPSynchronizationRequest(final String[] OIDs,
			final IRPAlarmList managedSystem) {
		super(OIDs, managedSystem);
	}

	@Override
	protected Alarm createSyncAlarm(final SnmpPDU pdu) {
		return new IRPSyncAlarm(m_Protocol, pdu);
	}
}
