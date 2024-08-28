package com.ericsson.navigator.esm.model;

import com.ericsson.navigator.esm.util.SupervisionException;

public interface Protocol<S, D extends ManagedData<S,D>, AI extends AddressInformation> extends ManagedDataList<D, S>{

	AI getAddressInformation();
	void startSupervision() throws SupervisionException;
	void stopSupervision(boolean isRemoved) throws SupervisionException;
	boolean isUpdated(final Protocol<?, ?, ?> protocol);
}
