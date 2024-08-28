package com.ericsson.navigator.esm.manager.snmp;

import java.io.IOException;

import com.adventnet.snmp.snmp2.SnmpPDU;
import com.ericsson.navigator.esm.model.alarm.snmp.SNMPAddressInformation;

public interface AsynchronousSnmpRequest {
	void executeRequest() throws IOException;
	boolean matchesResponse(SnmpPDU pdu);
	void handleResponse(SnmpPDU pdu);
	SNMPProtocol<?, ?, ? extends SNMPAddressInformation> getManagedSystem();
}
