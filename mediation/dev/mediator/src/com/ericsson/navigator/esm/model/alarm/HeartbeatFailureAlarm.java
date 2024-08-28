package com.ericsson.navigator.esm.model.alarm;

import java.util.Date;

public class HeartbeatFailureAlarm extends AbstractAlarm {

	private static final long serialVersionUID = 1L;

	public HeartbeatFailureAlarm(final ProtocolAlarmList<?> protocolAlarmList, final String reason, final PerceivedSeverity severity) {
		managedObjectInstance = protocolAlarmList.getFDN();
		m_SpecificProblem = "Heartbeat failure";
		m_ProbableCause = "Failed to ping managed system";
		m_EventType = "Communications alarm";
		m_RecordType = RecordType.HEARTBEAT_ALARM;
		m_PerceivedSeverity = severity;
		m_EventTime = new Date();
		final StringBuffer b = new StringBuffer();
		b.append("Failed to ping managed system named ");
		b.append(protocolAlarmList.getFDN());
		b.append("\nIP address: ");
		b.append(protocolAlarmList.getAddressInformation().getAddress());
		b.append('\n');
		b.append(reason);
		m_AdditionalText = b.toString();
		generateUniqueId();
	}
}
