package com.ericsson.navigator.esm.model;

import java.util.List;

public interface ManagedModel<M extends ManagedDataList<?, ?>>{

	void addManagedModelListener(ManagedModelListener<M> l);
	void removeManagedModelListener(ManagedModelListener<M> l);
	void addManaged(M managed);
	void removeManaged(M managed);
	List<M> getManagedDataLists(ManagedDataType type);
	List<M> getAllManagedDataLists();
	M getManaged(Long uniqueId, ManagedDataType type);
	List<M> getManaged(String fdn);
}
