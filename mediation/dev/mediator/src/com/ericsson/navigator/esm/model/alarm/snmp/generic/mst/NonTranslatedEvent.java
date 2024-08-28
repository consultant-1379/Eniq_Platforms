package com.ericsson.navigator.esm.model.alarm.snmp.generic.mst;

import com.adventnet.snmp.mibs.MibOperations;
import com.adventnet.snmp.snmp2.SnmpPDU;
import com.ericsson.navigator.esm.model.alarm.snmp.SNMPAddressInformation;
import com.ericsson.navigator.esm.model.alarm.snmp.SNMPAlarmList;
import com.ericsson.navigator.esm.model.alarm.snmp.generic.SnmpEvent;

public class NonTranslatedEvent extends SnmpEvent {

	private static final long serialVersionUID = 1L;

	public NonTranslatedEvent(final SNMPAlarmList<SNMPAddressInformation> system, final SnmpPDU pdu, final MibOperations mibOperations) {
		super(system, pdu, mibOperations);
		m_EventType = "Translation error";
		m_SpecificProblem = "Missing manager side translation";
		m_ProbableCause = "No matching translation";
	}
}
