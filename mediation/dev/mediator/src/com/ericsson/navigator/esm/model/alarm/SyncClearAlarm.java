package com.ericsson.navigator.esm.model.alarm;

import java.util.Date;

public class SyncClearAlarm extends AbstractAlarm {

	private static final long serialVersionUID = 1L;

	public SyncClearAlarm(final Alarm alarm) {
		super(alarm);
		m_EventTime = new Date();
		m_PerceivedSeverity = PerceivedSeverity.CLEARED;
		m_RecordType = RecordType.SYNC_ALARM;
	}
}
