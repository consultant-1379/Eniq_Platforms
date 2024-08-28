package com.ericsson.navigator.esm.model;

public interface ManagedDataList<D extends ManagedData<S,D>, S> extends ManagedDataProducer<D, S> {

	String getFDN();
	ManagedDataType getType();
	void correlate(D data);
	void correlate(D data, boolean log);
	boolean equals(Object o);
	void changeDataState(Long uniqueId, S state, String userId);
	boolean hasData(Long uniqueId);
	int getDataCount();
}
