package com.ericsson.navigator.esm.model;

import java.util.Date;

public interface ManagedData<S, D extends ManagedData<S,D>> extends Comparable<D> {
	
	ManagedDataType getDataType();
	String getManagedObjectInstance(); 
	S getState();
	Date getStateTime();
	String getStateUser();
	long getUniqueId();
	boolean update(D data);
	boolean isStateChange();
	boolean shallPersist();
	void reset();
	D getSerializable();
}
