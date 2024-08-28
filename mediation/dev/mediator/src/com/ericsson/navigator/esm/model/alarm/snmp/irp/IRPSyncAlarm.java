package com.ericsson.navigator.esm.model.alarm.snmp.irp;

import com.adventnet.snmp.snmp2.SnmpPDU;

public class IRPSyncAlarm extends IRPAlarm {

	private static final long serialVersionUID = 1L;
	
	public IRPSyncAlarm(final IRPAlarmList system, final SnmpPDU pdu) {
		super(system, pdu);
		m_RecordType = RecordType.SYNC_ALARM;
	}

}