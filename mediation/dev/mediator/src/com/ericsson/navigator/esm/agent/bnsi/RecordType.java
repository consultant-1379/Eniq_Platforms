package com.ericsson.navigator.esm.agent.bnsi;

/**
 * Alarm (1) An alarm or an alarm clear record.
 * Event (2) An event record. There will never be a clear message
 * for this alarm. An event is a notification with no clear.
 * Internal Alarm (3) An alarm that will not be a part of a future alarm
 * synchronization. This is typically an internal error
 * message. Use Alarm (1) or Event (2) records if
 * possible.
 * Repeated (4) A repeated alarm, which means that the alarm is
 * already sent to the management system.A repeated
 * alarm is for example used with the attribute
 * TrendIndication if an alarm changes severity,
 * see chapter 6.5.17.
 * Sync. Alarm (5) An alarm record within an alarm synchronization
 * phase.
 * Heartbeat (6) An alarm (or alarm clear) record due to a loss of a
 * connection discovered by for example the absence of
 * heartbeat messages. Use Alarm (1) record if possible.
 * Sync. Started (7) A record indicating the start of the alarm
 * synchronization phase.
 * Sync. Ended (8) A record indicating the end of the alarm
 * synchronization phase.
 * Sync. Event (9) An event record (unacknowledged) within an alarm
 * synchronization phase.
 */
enum RecordType {
	ALARM(1), EVENT(2), REPEATED(4), SYNC_ALARM(5), HEARTBEAT(6), START_SYNC(7), END_SYNC(8), SYNC_EVENT(9);
	
	int m_Nr = 1;
	
	RecordType(final int nr){
		m_Nr = nr;
	}
	
	int getValue(){
		return m_Nr;
	}
};
