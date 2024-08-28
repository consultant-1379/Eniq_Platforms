package com.ericsson.navigator.esm.model;

import java.util.EventListener;
import java.util.List;

public interface ManagedModelListener<M extends ManagedDataList<?,?>> extends EventListener {

	void systemAdded(ManagedModelEvent<M> event);
	void systemRemoved(ManagedModelEvent<M> event);
	List<ManagedDataType> getTypes();
	
}
