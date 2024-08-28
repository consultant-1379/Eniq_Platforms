package com.ericsson.navigator.esm.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class AbstractManagedData<S, D extends ManagedData<S,D>> 
	implements ManagedData<S,D>, Serializable {

	private static final long serialVersionUID = 1L;
	protected long uniqueId = -1;
	protected S state = null;
	protected String stateUser = "";
	protected Date stateTime = null;
	protected String managedObjectInstance = "";
	public static final SimpleDateFormat DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public AbstractManagedData() {
	}
	
	public AbstractManagedData(final D data) {
		uniqueId = data.getUniqueId();
		state = data.getState();
		stateTime = data.getStateTime();
		stateUser = data.getStateUser();
		managedObjectInstance = data.getManagedObjectInstance();
	}
	
	@Override
	public void reset(){}
	
	@Override
	public String getManagedObjectInstance() {
		return managedObjectInstance;
	}

	// Stole implementation from Long.java
	public int compareTo(final D data) {
		return (uniqueId < data.getUniqueId() ? -1 : (uniqueId == data
				.getUniqueId() ? 0 : 1));
	}

	@SuppressWarnings("unchecked")
	public boolean equals(final Object o) {
		return compareTo((D)o) == 0;
	}
	
	public boolean equals(final D data) {
		return compareTo(data) == 0;
	}

	// Stole implementation from Long.java
	public int hashCode() {
		return (int) (uniqueId ^ (uniqueId >>> 32));
	}

	public long getUniqueId() {
		return uniqueId;
	}

	public S getState() {
		return state;
	}
	
	public Date getStateTime() {
		return stateTime;
	}

	public String getStateUser() {
		return stateUser;
	}

	public boolean update(final D data) {
		if (data.isStateChange()) {
			return updateState(data);
		} else {
			return updateData(data);
		}
	}

	protected boolean updateState(final D data) {
		if (data.getState().equals(getState())) {
			return false;
		}
		state = data.getState();
		return true;
	}

	public String toString() {
		final StringBuffer b = new StringBuffer();
		b.append("Unique ID : ");
		b.append(uniqueId);
		b.append("\nManaged object instance : ");
		b.append(managedObjectInstance);
		b.append("\nState : ");
		b.append(state);
		b.append("\nState user : ");
		b.append(stateUser);
		b.append("\nState time : ");
		b.append(stateTime == null ? "" : DATEFORMAT.format(stateTime));
		return b.toString();
	}

	/**
	 * Update the alarm attributes.
	 * 
	 * @param alarm
	 *            The alarm with to check against.
	 * @return True if an attribute has changed. False otherwise.
	 */
	protected boolean updateData(final D data){
		return false;
	}

	@Override
	public boolean isStateChange() {
		return false;
	}
}
