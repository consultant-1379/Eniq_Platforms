package com.ericsson.navigator.esm.model.alarm;

import java.io.Serializable;
import java.util.Date;

import com.ericsson.navigator.esm.model.ManagedData;

public interface Alarm extends Serializable, ManagedData<Alarm.State, Alarm> {

	enum RecordType {
		ALARM, SYNC_ALARM, SYNC_EVENT, NON_SYNC_ALARM, EVENT, 
		HEARTBEAT_ALARM, SYNC_ABORTED, SYNC_START, SYNC_END, STATE, CLEAR_ALL, PM_COLLECTION_FAILURE;

		@Override
		public String toString() {
			switch(ordinal()){
			case 1:
				return "Synchronization alarm";
			case 2:
				return "Synchronization event";
			case 3:
				return "Non synchable alarm";
			case 4:
				return "Event";
			case 5:
				return "Heartbeat alarm";
			case 6:
				return "Synchronization aborted alarm";
			case 7:
				return "Synchronization start event";
			case 8:
				return "Synchronization end event";
			case 9:
				return "Alarm state event";
			case 10:
				return "Clear all alarm";
			case 11:
				return "PM collection alarm";
			default:
				return "Alarm";
			}
		}
	};

	enum PerceivedSeverity {
		INDETERMINATE, CRITICAL, MAJOR, MINOR, WARNING, CLEARED;
		
		@Override
		public String toString() {
			switch(ordinal()){
			case 1:
				return "Critical";
			case 2:
				return "Major";
			case 3:
				return "Minor";
			case 4:
				return "Warning";
			case 5:
				return "Cleared";
			default:
				return "Indeterminate";
			}
		}
	};

	enum State {
		ACKNOWLEDGED, UNACKNOWLEDGED, DELETED;
		
		@Override
		public String toString() {
			switch(ordinal()){
			case 0:
				return "Acknowledged";
			case 1:
				return "Unacknowledged";
			case 2:
				return "Deleted";
			default:
				return "Unacknowledged";
			}
		}
	};

	String getManagedObjectInstance();

	String getSpecificProblem();

	String getEventType();

	String getProbableCause();

	RecordType getRecordType();

	Date getEventTime();

	String getAdditionalText();

	PerceivedSeverity getPerceivedSeverity();

	String getStateUser();

	Date getStateTime();

	int getCorrelationCount();

	String getAlarmId();

	boolean affectCorrelationCount();
}
