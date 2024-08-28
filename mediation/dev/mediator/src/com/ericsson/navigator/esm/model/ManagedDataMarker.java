package com.ericsson.navigator.esm.model;

public class ManagedDataMarker extends AbstractManagedData<ManagedDataMarker, ManagedDataMarker> {

	private static final long serialVersionUID = 1L;

	public ManagedDataMarker(final String fdn) {
		managedObjectInstance = fdn;
	}

	@Override
	public ManagedDataType getDataType() {
		return ManagedDataType.Marker;
	}

	@Override
	public boolean shallPersist() {
		return false;
	}
	
	@Override
	public ManagedDataMarker getSerializable() {
		return this;
	}
}
