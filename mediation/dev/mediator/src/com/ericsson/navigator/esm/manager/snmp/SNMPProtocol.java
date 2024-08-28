package com.ericsson.navigator.esm.manager.snmp;

import com.ericsson.navigator.esm.model.AddressInformation;
import com.ericsson.navigator.esm.model.ManagedData;
import com.ericsson.navigator.esm.model.Protocol;

public interface SNMPProtocol<S, D extends ManagedData<S,D>, AI extends AddressInformation> extends Protocol<S, D, AI>{
 
	void setRequestChannel(final RequestChannel channel);
	RequestChannel getRequestChannel();
}
