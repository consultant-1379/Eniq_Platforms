package com.ericsson.navigator.esm.manager.snmp;

import java.util.EventListener;

import com.adventnet.snmp.snmp2.SnmpPDU;
import com.ericsson.navigator.esm.model.AddressInformation;

public interface ManagedSystemTrapListener extends EventListener {

	void receivedTrap(SnmpPDU pdu) throws TranslationException;
	AddressInformation getAddressInformation();

}
 