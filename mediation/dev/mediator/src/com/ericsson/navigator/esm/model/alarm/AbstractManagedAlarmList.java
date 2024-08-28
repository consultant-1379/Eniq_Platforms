package com.ericsson.navigator.esm.model.alarm;

import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.ericsson.navigator.esm.model.AbstractManagedDataList;
import com.ericsson.navigator.esm.model.ManagedDataEvent;
import com.ericsson.navigator.esm.model.ManagedDataType;
import com.ericsson.navigator.esm.model.ManagedDataEvent.Action;
import com.ericsson.navigator.esm.model.alarm.Alarm.RecordType;
import com.ericsson.navigator.esm.model.alarm.Alarm.State;
import com.ericsson.navigator.esm.util.SupervisionException;
import com.ericsson.navigator.esm.util.log.Log;

public abstract class AbstractManagedAlarmList extends 
	AbstractManagedDataList<Alarm, Alarm.State> implements ManagedAlarmList {
 
	private static final RecordType[] DEFAULT_TYPES_TO_CLEAR = new RecordType[]{
					RecordType.ALARM, RecordType.SYNC_ALARM};
	private static final long serialVersionUID = 1L;
	protected boolean m_IsSynchronizing = false;
	protected List<Alarm> m_listBeforeSync = null;

	private static final String classname = AbstractManagedAlarmList.class.getName();
	private static Logger m_Logger = Logger.getLogger(classname);

	public AbstractManagedAlarmList(final String fdn, final Log<Alarm> log) {
		super(fdn, log);
	}
	
	public int getAlarmCount() {
		return getDataCount();
	}

	public boolean hasAlarm(final Long uniqueId) {
		return hasData(uniqueId);
	}

	/**
	 * This method will correlate an alarm onto an managed system alarm list. If
	 * an alarm is new it will be added to the list if it is not an cleared
	 * alarm. If the an existing alarm is to be updated it will be removed if
	 * the alarm has the perceived severity cleared or a delete state after the
	 * update.
	 * 
	 * All acknowledge and deletion of alarms are handled this method.
	 * 
	 * This method will also log the alarm to the CyclicZIPlog.
	 * 
	 * @param alarm
	 *            The alarm to correlate onto the managed system.
	 * @see AbstractAlarm#update(Alarm) for the complete correlation logic.
	 */
	@Override
	protected void addData(final Alarm alarm) {
		if (alarm.getPerceivedSeverity().equals(
				Alarm.PerceivedSeverity.CLEARED)) {
			return;
		}
		super.addData(alarm);
	}
	
	@Override
	protected void dataUpdated(final Alarm alarm, final Alarm oldAlarm) {
		if (oldAlarm.getState().equals(Alarm.State.DELETED)) {
			items.remove(oldAlarm.getUniqueId());
			firePushEvent(createEvent(oldAlarm, ManagedDataEvent.Action.DELETE, true));
		} else if (oldAlarm.getPerceivedSeverity().equals(
				Alarm.PerceivedSeverity.CLEARED)) {
			items.remove(alarm.getUniqueId());
			firePushEvent(createEvent(oldAlarm, ManagedDataEvent.Action.DELETE, false));
		} else {
			super.dataUpdated(alarm, oldAlarm);
		}
	}

	public abstract void getActiveAlarmList() throws SupervisionException;

	public void synchronize() throws SupervisionException {
		if (!isSynchable()) {
			return;
		}
		synchronized (this) {
			if (m_IsSynchronizing) {
				return;
			}
			m_IsSynchronizing = true;
		}
		m_listBeforeSync = new Vector<Alarm>();
		m_listBeforeSync.addAll(items.values());
		getActiveAlarmList();
	}

	public void correlateSync(final List<Alarm> syncAlarmList) {
		correlateSync(syncAlarmList, DEFAULT_TYPES_TO_CLEAR);
	}
	
	public void correlateSync(final List<Alarm> syncAlarmList, final RecordType[] typesToClear){
		final List<Alarm> syncAlarmListCopy = new Vector<Alarm>();
		syncAlarmListCopy.addAll(syncAlarmList);
		// Check for missed new alarms
		for (final Alarm alarm : syncAlarmList) {
			correlate(alarm);
		}
		// Check for alarms to be cleared
		m_listBeforeSync.removeAll(syncAlarmListCopy);
		if (m_Logger.isDebugEnabled()) {
			m_Logger.debug(classname+".correlateSync() Nof alarms left from before sync "+m_listBeforeSync.size());
		}
		for (final Alarm alarm : m_listBeforeSync) {
			if (shouldClearType(alarm.getRecordType(), typesToClear)
					&& !alarm.getPerceivedSeverity().equals(
							Alarm.PerceivedSeverity.CLEARED)) {
				if (m_Logger.isDebugEnabled()) {
					m_Logger.debug(classname+".correlateSync() SyncClearAlarm created for alarm "+alarm.getUniqueId());
				}
				correlate(new SyncClearAlarm(alarm));//NOPMD
			}
		}
		m_listBeforeSync = null;
		m_IsSynchronizing = false;
	}
	
	private static boolean shouldClearType(final RecordType type, final RecordType[] typesToClear){
		for(RecordType clearType : typesToClear){
			if(clearType.equals(type)){
				return true;
			}
		}
		return false;
	}

	@Override
	protected ManagedDataEvent<Alarm> createEvent(final Alarm alarm, final Action action,
			final boolean isStateChange) {
		return new ManagedDataEvent<Alarm>(alarm, action, isStateChange);
	} 

	@Override
	protected Alarm createStateChange(final Alarm alarm, final State state, final String userId) {
		return new StateAlarm(alarm, state, userId);
	}
	
	@Override
	public ManagedDataType getType() {
		return ManagedDataType.Alarm;
	}

}
