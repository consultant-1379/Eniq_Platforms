package com.ericsson.navigator.esm.model.alarm.pm;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.ericsson.navigator.esm.model.alarm.DefaultAlarm;
 
public class PMCollectionFailureAlarm extends DefaultAlarm {

	private static final long serialVersionUID = 1L;
	private static final SimpleDateFormat TIMEFORMATTER = new SimpleDateFormat("yyyyMMddHHmmss");

	public PMCollectionFailureAlarm(final String fdn, final String definitionId, 
			final String moid, final int gp, final Date endTime, final PerceivedSeverity severity){
		managedObjectInstance = fdn;
		m_RecordType = RecordType.PM_COLLECTION_FAILURE;
		m_PerceivedSeverity = severity;
		m_SpecificProblem = "PM collection failed for counter set " + definitionId;
		m_EventType = "Processing error alarm";
		m_ProbableCause = "Information missing";
		final StringBuffer b = new StringBuffer("PM collection failed for counter set: ");
		b.append(definitionId);
		b.append(", expected granularity period: ");
		b.append(gp);
		b.append(" minute(s) ");
		if(moid != null){
			b.append(", moid: ");
			b.append(moid);
		}
		if(endTime != null){
			b.append(", last successful collection at: ");
			b.append(TIMEFORMATTER.format(endTime));
		} else {
			b.append(", no data has been successfully collected for this counter set.");
		}
		m_AdditionalText = b.toString();
		generateUniqueId();
	}

	public PMCollectionFailureAlarm(final PMCollectionFailureAlarm alarm) {
		super(alarm);
	}

	public void setPerceivedSeverity(final PerceivedSeverity severity) {
		m_PerceivedSeverity = severity;
	}

	@Override
	public boolean shallPersist() {
		return true;
	}
}
