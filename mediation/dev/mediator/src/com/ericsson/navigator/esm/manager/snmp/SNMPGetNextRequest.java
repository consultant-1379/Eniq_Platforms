package com.ericsson.navigator.esm.manager.snmp;

import java.io.IOException;
import java.util.Vector;

import com.adventnet.snmp.snmp2.SnmpAPI;
import com.adventnet.snmp.snmp2.SnmpPDU;
import com.adventnet.snmp.snmp2.SnmpVarBind;
import com.ericsson.navigator.esm.MVM;
import com.ericsson.navigator.esm.model.alarm.snmp.SNMPAddressInformation;

public abstract class SNMPGetNextRequest<P extends SNMPProtocol<?, ?, ? extends SNMPAddressInformation>> extends SNMPRequest<P> {

	protected final String[] m_OIDs;
	protected final long m_StartTime = System.currentTimeMillis();
	protected long m_PDUStartTime = System.currentTimeMillis();
	
	private final String classname = getClass().getName();
	
	public SNMPGetNextRequest(final String[] OIDs, final P protocol) {
		super(protocol);
		m_OIDs = OIDs;
	}
	
	@Override
	protected void error(final int code, final String error) {
		if (code == SnmpAPI.SNMP_ERR_NOSUCHNAME) {
			finished();
			return;
		}
		error(error);
	}

	@Override
	public void responseReceived(final SnmpPDU pdu) {
		m_PDUStartTime = System.currentTimeMillis();
		if (MVM.logger.isDebugEnabled()) {
			MVM.logger.debug(classname+".responseReceived(); --> " 
				+ m_Protocol.getFDN() + " : "
				+ (System.currentTimeMillis() - m_PDUStartTime));
		}
		if (inSubTree(pdu)) {
			createResult(pdu);
			m_PDU = createRequest(getOIDs(pdu), SnmpAPI.GETNEXT_REQ_MSG);
			try {
				AsynchronousSnmpRequestHandler.getInstance().addSNMPRequest(this);
			} catch (final IOException e) {
				error(e.getMessage());
			}
		} else {
			finished();
		}
	}
	
	protected abstract void createResult(SnmpPDU pdu);
	protected abstract void finished();

	@SuppressWarnings("unchecked")
	protected String[] getOIDs(final SnmpPDU pdu) {
		final Vector<SnmpVarBind> varBinds = pdu.getVariableBindings();
		final String[] oids = new String[varBinds.size()];
		int i = 0;
		for (final SnmpVarBind varBind : varBinds) {
			oids[i++] = varBind.getObjectID().toString();
		}
		return oids;
	}

	protected boolean inSubTree(final SnmpPDU pdu) {
		int i = 0;
		for(final String oid : m_OIDs){
			if (!pdu.getObjectID(i++).toString().startsWith(oid)) {
				return false;
			}
		}
		return pdu.getVariableBindings().size() != 0;
	}

}
