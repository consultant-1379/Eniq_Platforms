package com.ericsson.navigator.esm.model.alarm.snmp.snf;

import com.adventnet.snmp.snmp2.SnmpPDU;

public class SNFEvent extends SNFAlarm {

	private static final long serialVersionUID = 1L;
	public static final String EVENTOID = ".1.3.6.1.4.1.193.110.2.15.2.0.1";

	public SNFEvent(final SNFAlarmList managedSystem, final SnmpPDU pdu) {
		super(managedSystem, pdu, false);
		m_RecordType = RecordType.EVENT;
	}

}
