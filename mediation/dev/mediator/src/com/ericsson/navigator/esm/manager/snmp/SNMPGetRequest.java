package com.ericsson.navigator.esm.manager.snmp;

import com.adventnet.snmp.snmp2.SnmpAPI;
import com.ericsson.navigator.esm.model.alarm.snmp.SNMPAddressInformation;

public abstract class SNMPGetRequest<MS extends SNMPProtocol<?, ?, ? extends SNMPAddressInformation>> extends SNMPRequest<MS> {
	
	protected final String[] m_OIDs;
	
	public SNMPGetRequest(final String[] oids, final MS managedSystem){
		super(managedSystem);
		m_OIDs = oids;
		m_PDU = createRequest(m_OIDs, SnmpAPI.GET_REQ_MSG);
	}
	
	@Override
	protected void error(final int code, final String error) {
		error(error);
	}
}
