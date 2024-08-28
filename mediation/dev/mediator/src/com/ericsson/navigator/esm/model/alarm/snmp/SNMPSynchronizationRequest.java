package com.ericsson.navigator.esm.model.alarm.snmp;

import java.util.List;
import java.util.Vector;

import com.adventnet.snmp.snmp2.SnmpAPI;
import com.adventnet.snmp.snmp2.SnmpPDU;
import com.ericsson.navigator.esm.MVM;
import com.ericsson.navigator.esm.manager.snmp.SNMPGetNextRequest;
import com.ericsson.navigator.esm.model.alarm.Alarm;

public abstract class SNMPSynchronizationRequest<MS extends SNMPAlarmList<? extends SNMPAddressInformation>>
		extends SNMPGetNextRequest<MS> {

	private final List<Alarm> m_Alarms;
	protected static final String TIMEDOUT_ERROR = "Synchronization request timed out";
	
	private final String classname = getClass().getName();

	public SNMPSynchronizationRequest(final String[] OIDs, final MS managedSystem) {
		super(OIDs, managedSystem);
		m_Alarms = new Vector<Alarm>();
		m_PDU = createRequest(m_OIDs, SnmpAPI.GETNEXT_REQ_MSG);
	}

	protected abstract Alarm createSyncAlarm(SnmpPDU pdu);

	@Override
	protected void timedOut() {
		m_Protocol.abortSync(TIMEDOUT_ERROR);
	}

	@Override
	protected void error(final String error) {
		m_Protocol.abortSync(error);
	}

	protected void createResult(final SnmpPDU pdu) {
		final Alarm alarm = createSyncAlarm(pdu);
		m_Alarms.add(alarm);
	}

	protected void finished() {
		m_Protocol.correlateSync(m_Alarms);
		if (MVM.logger.isDebugEnabled()) {
			MVM.logger.debug(classname+".finished(); --> Synched " + m_Alarms.size() + 
					" alarms in " + (System.currentTimeMillis() - m_StartTime) + 
					" ms, for managed system: " + m_Protocol.getFDN());
		}
	}
}
