package com.ericsson.navigator.esm.model.alarm.snmp.snf;

import com.adventnet.snmp.snmp2.SnmpPDU;

public class SNFSyncAlarm extends SNFAlarm {

	private static final long serialVersionUID = 1L;

	public SNFSyncAlarm(final SNFAlarmList managedSystem, final SnmpPDU pdu) {
		super(managedSystem, pdu, false);
		m_RecordType = RecordType.SYNC_ALARM;
	}

} 