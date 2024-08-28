package com.ericsson.navigator.esm.model;

import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.ericsson.navigator.esm.model.ManagedDataEvent.Action;
import com.ericsson.navigator.esm.util.log.Log;
import com.ericsson.navigator.esm.util.log.LogException;
import com.ericsson.navigator.esm.util.reference.WeakListenerList;

public abstract class AbstractManagedDataList<D extends ManagedData<S,D>, S> 
	implements ManagedDataList<D, S> {

	private static final long serialVersionUID = 1L;	
	protected String fdn = null;
	protected SortedMap<Long, D> items = null;
	protected WeakListenerList<ManagedDataListener<D>> m_Listeners = new WeakListenerList<ManagedDataListener<D>>();
	private final Log<D> log;
	
	private static final String classname = AbstractManagedDataList.class.getName();
	private static Logger logger = Logger.getLogger(classname);

	public AbstractManagedDataList(final String fdn, final Log<D> log) {
		items = new TreeMap<Long, D>();
		this.fdn = fdn;
		this.log = log;
	}
	
	public int getDataCount() {
		return items.size();
	}

	public boolean hasData(final Long uniqueId) {
		return items.containsKey(uniqueId);
	}

	public synchronized void addManagedDataListener(final ManagedDataListener<D> l, final boolean includeInitial) {
		m_Listeners.addListener(l);
		if(!includeInitial){
			return;
		}
		for (final D data : items.values()) {
			l.pushDataEvent(createEvent(data, ManagedDataEvent.Action.INITIAL, false));//NOPMD
		}
	}

	protected abstract ManagedDataEvent<D> createEvent(D oldAlarm, Action delete, boolean isStateChange);
	protected abstract D createStateChange(D oldData, S state, String userId);

	public synchronized void removeManagedDataListener(final ManagedDataListener<D> l) {
		m_Listeners.removeListener(l);
	}

	public String getFDN() {
		return fdn;
	}

	protected synchronized void firePushEvent(final ManagedDataEvent<D> event) {
		for (final ManagedDataListener<D> l : m_Listeners) {
			l.pushDataEvent(event);
		}
	}

	public void correlate(final D data) {
		correlate(data, true);
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
	 * @param data
	 *            The alarm to correlate onto the managed system.
	 */
	public synchronized void correlate(final D data, final boolean shouldLog) {
		if (shouldLog) {
			logData(data);
		}
		final D oldData = items.get(data.getUniqueId());
		if (oldData == null) {
			addData(data);
		} else {
			if(oldData.update(data)){
				dataUpdated(data, oldData);
			}
		}

	}

	protected void dataUpdated(final D data, final D oldData) {
		firePushEvent(createEvent(oldData, ManagedDataEvent.Action.UPDATE, data.isStateChange()));
	}

	protected void addData(final D data) {
		items.put(data.getUniqueId(), data);
		firePushEvent(createEvent(data, ManagedDataEvent.Action.ADD, false));
	}
	
	protected void logData(final D data) {
		if(log == null){
			return;
		}
		try {
			log.log(data);
		} catch (final LogException e) {
			logger.error(classname
					+ "logAlarm(); Error detected while logging alarm.", e);
		}
	}

	public synchronized void changeDataState(final Long uniqueId,
			final S state, final String userId) {
		final D oldData = items.get(uniqueId);
		if (oldData != null) {
			correlate(createStateChange(oldData, state, userId));
		}
	}

	@Override
	public boolean equals(final Object o) {
		return o.getClass().equals(getClass()) && getFDN().equals(((ManagedDataList<?,?>) o).getFDN());
	}
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}

}
