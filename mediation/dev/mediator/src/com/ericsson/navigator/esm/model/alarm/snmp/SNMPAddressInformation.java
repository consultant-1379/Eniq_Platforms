package com.ericsson.navigator.esm.model.alarm.snmp;

import com.ericsson.navigator.esm.model.AddressInformation;

public interface SNMPAddressInformation extends AddressInformation {

	enum VERSION { V1, V2C, V3 };
	enum PROTOCOL_TYPE {SNF, IRP, MST, SNMP};
	
	int getPort();
	String getCommunity();
	VERSION getVersion();
}
