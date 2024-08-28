package com.ericsson.navigator.esm.model;



public interface ManagedDataProducer<D extends ManagedData<S,D>, S> {

	void addManagedDataListener(ManagedDataListener<D> l, boolean incudeInitial);
	void removeManagedDataListener(ManagedDataListener<D> l);
}
