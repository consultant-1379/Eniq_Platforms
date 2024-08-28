package com.ericsson.navigator.esm.model.alarm;

import static com.ericsson.navigator.esm.util.Hash.fnv_64_1a;

import java.util.Date;

import com.ericsson.navigator.esm.model.AbstractManagedData;
import com.ericsson.navigator.esm.model.ManagedDataType;

@SuppressWarnings( "PMD.CyclomaticComplexity" )
public abstract class AbstractAlarm extends AbstractManagedData<Alarm.State, Alarm> implements Alarm {// NOPMD

	private static final long serialVersionUID = 1L;
	protected String m_AdditionalText = "";
	protected String m_AlarmId = "";
	protected Date m_EventTime = new Date();
	protected String m_EventType = "";
	protected String m_ProbableCause = "";
	protected String m_SpecificProblem = "";
	protected RecordType m_RecordType = RecordType.ALARM;
	protected PerceivedSeverity m_PerceivedSeverity = PerceivedSeverity.INDETERMINATE;
	protected int m_CorrelationCount = 0;
	protected static final char UNIQUESEPERATOR = '<';
	protected boolean affectCorrelationCount = true;

	public AbstractAlarm() {
		state = Alarm.State.UNACKNOWLEDGED;
	}

	public AbstractAlarm(final Alarm alarm) {
		super(alarm);
		m_EventTime = alarm.getEventTime();
		m_EventType = alarm.getEventType();
		m_ProbableCause = alarm.getProbableCause();
		m_AlarmId = alarm.getAlarmId();
		m_SpecificProblem = alarm.getSpecificProblem();
		m_PerceivedSeverity = alarm.getPerceivedSeverity();
		m_AdditionalText = alarm.getAdditionalText();
		uniqueId = alarm.getUniqueId();
		state = alarm.getState();
		stateUser = alarm.getStateUser();
		stateTime = alarm.getStateTime();
		m_CorrelationCount = alarm.getCorrelationCount();
		m_RecordType = alarm.getRecordType();
	}
	
	@Override
	public Alarm getSerializable() {
		return new SerializedAlarm(this);
	}
	
	@Override
	public void reset() {
		m_CorrelationCount = 0;
		affectCorrelationCount = false;
	}
	
	public String getAdditionalText() {
		return m_AdditionalText;
	}

	public int getCorrelationCount() {
		return m_CorrelationCount;
	}

	public String getAlarmId() {
		return m_AlarmId;
	}

	public Date getEventTime() {
		return m_EventTime;
	}

	public String getEventType() {
		return m_EventType;
	}

	public String getProbableCause() {
		return m_ProbableCause;
	}

	public RecordType getRecordType() {
		return m_RecordType;
	}

	public String getSpecificProblem() {
		return m_SpecificProblem;
	}

	/**
	 * Hash algorithm used by ESM to generate the unique ID. String.HashCode()
	 * is not good enough since it provides only a 32 bit integer with a bad
	 * spread. So, be aware not to use Collections that use the hashCode function.
	 *
	 * @see AbstractAlarm#fnv_64_1a(String)
	 */
	protected void generateUniqueId() {
		if (m_AlarmId != null && m_AlarmId.length() > 0) {
			uniqueId = fnv_64_1a(managedObjectInstance + UNIQUESEPERATOR + m_AlarmId);
		} else {
			uniqueId = fnv_64_1a(managedObjectInstance + UNIQUESEPERATOR
							+ m_SpecificProblem + UNIQUESEPERATOR + m_EventType
							+ UNIQUESEPERATOR + m_ProbableCause);
		}
	}

	public PerceivedSeverity getPerceivedSeverity() {
		return m_PerceivedSeverity;
	}

	public String toString() {
		final StringBuffer b = new StringBuffer();
		b.append(super.toString());
		if(m_AlarmId.length() > 0){
			b.append("\nAlarm ID : ");
			b.append(m_AlarmId);
		}
		b.append("\nRecord type :");
		b.append(m_RecordType);
		b.append("\nEvent time : ");
		b.append(DATEFORMAT.format(m_EventTime));
		b.append("\nPerceived severity : ");
		b.append(m_PerceivedSeverity);
		b.append("\nSpecific problem :");
		b.append(m_SpecificProblem);
		b.append("\nProbable cause : ");
		b.append(m_ProbableCause);
		b.append("\nEvent type : ");
		b.append(m_EventType);
		b.append("\nCorrelation count : ");
		b.append(m_CorrelationCount);
		b.append("\nAdditional text : ");
		b.append(m_AdditionalText);
		return b.toString();
	}
	
	@Override
	public boolean affectCorrelationCount() {
		return !getRecordType().equals(Alarm.RecordType.STATE)
				&& !getRecordType().equals(Alarm.RecordType.HEARTBEAT_ALARM)
				&& !getRecordType().equals(Alarm.RecordType.SYNC_ABORTED)
				&& !getRecordType().equals(Alarm.RecordType.SYNC_ALARM)
				&& !getRecordType().equals(Alarm.RecordType.PM_COLLECTION_FAILURE)
				&& affectCorrelationCount;
	}

	/**
	 * Updates the correlation count for an alarm.
	 * 
	 * @param alarm
	 *            The alarm causing the update.
	 * @return True if the correlation count was updated, False otherwise.
	 */
	private boolean updateCorrelationCount(final Alarm alarm) {
		if (alarm.affectCorrelationCount()) {
			++m_CorrelationCount;
			return true;
		}
		return false;
	}
	
	@Override
	public ManagedDataType getDataType() {
		return ManagedDataType.Alarm;
	}
	
	@Override
	protected boolean updateState(final Alarm data) {
		if(!super.updateState(data)){
			return false;
		}
		if (!state.equals(Alarm.State.UNACKNOWLEDGED)) {
			stateUser = data.getStateUser();
			stateTime = data.getStateTime();
		} else {
			stateUser = "";
			stateTime = null;
		}
		return true;
	}

	/**
	 * Update the alarm attributes.
	 * 
	 * @param alarm
	 *            The alarm with to check against.
	 * @return True if an attribute has changed. False otherwise.
	 */
	@Override
	protected boolean updateData(final Alarm data) {
		boolean updated = false;
		if (!m_PerceivedSeverity.equals(data.getPerceivedSeverity())) {
			m_PerceivedSeverity = data.getPerceivedSeverity();
			updated = true;
		}
		if (!m_AdditionalText.equals(data.getAdditionalText())) {
			m_AdditionalText = data.getAdditionalText();
			updated = true;
		}
		if ((m_EventType == null && data.getEventType() != null)
				|| !m_EventType.equals(data.getEventType())) {
			m_EventType = data.getEventType();
			updated = true;
		}
		if (!m_ProbableCause.equals(data.getProbableCause())) {
			m_ProbableCause = data.getProbableCause();
			updated = true;
		}
		if (!data.getSpecificProblem().equals(m_SpecificProblem)) {
			m_SpecificProblem = data.getSpecificProblem();
			updated = true;
		}
		if (updateCorrelationCount(data)) {
			updated = true;
		}
		if (data.getState().equals(Alarm.State.ACKNOWLEDGED)) {
			if (updateState(data)) {
				updated = true;
			}
		}
		if(updated){
			m_EventTime = data.getEventTime();
		}
		return updated;
	}
	
	@Override
	public boolean shallPersist() {
		return getRecordType().equals(RecordType.NON_SYNC_ALARM)
				|| getRecordType().equals(RecordType.EVENT)
						|| getState().equals(State.ACKNOWLEDGED);
	}
}
