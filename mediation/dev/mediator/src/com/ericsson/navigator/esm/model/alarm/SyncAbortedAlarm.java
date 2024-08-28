package com.ericsson.navigator.esm.model.alarm;

import java.util.Date;

public class SyncAbortedAlarm extends AbstractAlarm {

	private static final long serialVersionUID = 1L;
	
	public SyncAbortedAlarm(final ProtocolAlarmList<?> protocolAlarmList, final String reason, final PerceivedSeverity severity){
		managedObjectInstance = protocolAlarmList.getFDN();
		m_SpecificProblem = "Synchronization aborted";
		m_ProbableCause = "Failed to synchronize managed system";
		m_EventType = "Communications alarm";
		m_RecordType = RecordType.SYNC_ABORTED;
		m_PerceivedSeverity = severity;
		m_EventTime = new Date();
		final StringBuffer b = new StringBuffer();
		b.append("Failed to synchronize managed system named ");
		b.append(protocolAlarmList.getFDN());
		b.append("\nIP address: ");
		b.append(protocolAlarmList.getAddressInformation().getAddress());
		b.append('\n');
		b.append(reason);
		m_AdditionalText = b.toString();
		generateUniqueId();
	}
}
