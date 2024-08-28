package com.ericsson.navigator.esm.model;

import java.util.EventListener;

 

public interface ManagedDataListener<D extends ManagedData<?,D>> extends EventListener{

	void pushDataEvent(ManagedDataEvent<D> event);
}
