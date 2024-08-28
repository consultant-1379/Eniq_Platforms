package com.ericsson.navigator.esm.model.alarm.snmp.generic.mst;

import java.util.Date;

import com.ericsson.navigator.esm.model.alarm.DefaultAlarm;

public class MSTAlarm extends DefaultAlarm {

	private static final long serialVersionUID = 1L;
	private final boolean suppress;

	/**
	 * Constructor.
	 */
	public MSTAlarm(
			final String alarmId,
			final String specificProblem, 
			final String fdn,
			final String additionalText,
			final String eventType,
			final String probableCause,
			final String perceivedSeverity,
			final RecordType recordType,
			final boolean suppress) { 
		
		m_AlarmId = alarmId;
		m_SpecificProblem = specificProblem;
		managedObjectInstance = fdn;
		m_EventTime = new Date();
		m_AdditionalText = additionalText;
		m_EventType = eventType;
		m_ProbableCause = probableCause;
		m_RecordType = recordType;
		this.suppress = suppress;
		m_PerceivedSeverity = getPerceivedSeverity(perceivedSeverity); //NOPMD
		generateUniqueId();
	}
	
	PerceivedSeverity getPerceivedSeverity(final String perceivedSeverity) {
		final PerceivedSeverity[] values = PerceivedSeverity.values();
		try { 
			final int severity = Integer.parseInt(perceivedSeverity);
			if(severity < 0 || values.length-1 < severity){
				return PerceivedSeverity.INDETERMINATE;
			}
			return values[severity];
		} catch (final Exception e) {
			return getPerceivedSeverityFromString(perceivedSeverity);
		}
	}

	public static PerceivedSeverity getPerceivedSeverityFromString(final String severity) {
		if (severity.equalsIgnoreCase("CLEARED")) {
			return PerceivedSeverity.CLEARED;
		} else if (severity.equalsIgnoreCase("CRITICAL")) {
			return PerceivedSeverity.CRITICAL;
		} else if (severity.equalsIgnoreCase("MAJOR")) {
			return PerceivedSeverity.MAJOR;
		} else if (severity.equalsIgnoreCase("MINOR")) {
			return PerceivedSeverity.MINOR;
		} else if (severity.equalsIgnoreCase("WARNING")) {
			return PerceivedSeverity.WARNING;
		} else {
			return PerceivedSeverity.INDETERMINATE;
		}
	}

	/**
	 * Check if the alarm should be suppressed.
	 * @return
	 */
	public boolean isSuppressed() {
		return suppress;
	}
}
