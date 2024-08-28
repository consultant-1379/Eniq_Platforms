package com.ericsson.navigator.esm.model.alarm.snmp.snf;

import java.io.IOException;

import com.adventnet.snmp.snmp2.SnmpAPI;
import com.adventnet.snmp.snmp2.SnmpPDU;
import com.adventnet.snmp.snmp2.SnmpVar;
import com.ericsson.navigator.esm.manager.snmp.AsynchronousSnmpRequestHandler;
import com.ericsson.navigator.esm.manager.snmp.SNMPGetRequest;
import com.ericsson.navigator.esm.model.alarm.Alarm;
import com.ericsson.navigator.esm.model.alarm.snmp.SNMPSynchronizationRequest;

public class SNFSynchronizationRequest extends
		SNMPSynchronizationRequest<SNFAlarmList> {

	// Columns to fetch from alarmActiveTable
	private static final String ALARMACTIVERESOURCEIDOID = ".1.3.6.1.4.1.193.110.2.666.1.2.2.1.10";
	private static final String ALARMACTIVEDESCRIPTIONOID = ".1.3.6.1.4.1.193.110.2.666.1.2.2.1.11";
	// Pointer to alarmModelTable
	private static final String ALARMACTIVEMODELPOINTER = ".1.3.6.1.4.1.193.110.2.666.1.2.2.1.13";

	// Columns to fetch in alarmModelTable using modelTablePointer
	private static final String ALARMMODELDESCRIPTIONOID = ".1.3.6.1.4.1.193.110.2.666.1.1.2.1.6";

	// Columns to fetch in ituAlarmTable using specificModelPointer
	private static final String ITUALARMEVENTTYPEOID = ".1.3.6.1.4.1.193.110.2.667.1.1.1.1.2";
	private static final String ITUALARMPROBABLECAUSEOID = ".1.3.6.1.4.1.193.110.2.667.1.1.1.1.3";

	private static final String[] SYNCHOIDS = { ALARMACTIVERESOURCEIDOID,
			ALARMACTIVEDESCRIPTIONOID, ALARMACTIVEMODELPOINTER};

	public SNFSynchronizationRequest(final SNFAlarmList managedSystem) {
		super(SYNCHOIDS, managedSystem);
	}

	@Override
	public void responseReceived(final SnmpPDU originalPDU) {
		if (originalPDU.getErrstat() == SnmpAPI.SNMP_ERR_NOSUCHNAME || 
				!inSubTree(originalPDU)){
			finished();
			return;
		}
		try {
			final String alarmModelTableIndex = getAlarmModelTableIndices(originalPDU.getVariable(2));
			final String[] oids = new String[]{
					ALARMMODELDESCRIPTIONOID+alarmModelTableIndex,
					ITUALARMEVENTTYPEOID+getITUIndices(alarmModelTableIndex),
					ITUALARMPROBABLECAUSEOID+getITUIndices(alarmModelTableIndex)};
			AsynchronousSnmpRequestHandler.getInstance().addSNMPRequest(
					new SNFGetRequest(m_Protocol, oids, originalPDU));
		} catch (final IOException e) {
			m_Protocol.abortSync(e.getMessage());
		}
	}
	
	private String getITUIndices(final String indices){
		final int lastDotIndex = indices.lastIndexOf('.');
		final int state = Integer.parseInt(indices.substring(lastDotIndex+1));
		final String alarmListAndIndex = indices.substring(0, lastDotIndex+1);
		int severity = -1;
		switch(state){
		case 1: 
			severity = 1;
			break;
		case 2: 
			severity = 2;
			break;
		case 3: 
			severity = 6;
			break;
		case 4: 
			severity = 5;
			break;
		case 5: 
			severity = 4;
			break;
		case 6: 
			severity = 3;
			break;
		}
		return alarmListAndIndex+severity;
	}
	
	private class SNFGetRequest extends SNMPGetRequest<SNFAlarmList>{
		
		private final SnmpPDU m_OriginalPDU;
		
		public SNFGetRequest(final SNFAlarmList system, final String[] oids, final SnmpPDU originalPDU){
			super(oids, system);
			m_OriginalPDU = originalPDU;
		}
		
		@Override
		protected void error(final String error) {
			m_Protocol.abortSync(error);
		}

		@Override
		protected void responseReceived(final SnmpPDU pdu) {
			m_OriginalPDU.addVariableBinding(pdu.getVariableBinding(0));
			m_OriginalPDU.addVariableBinding(pdu.getVariableBinding(1));
			m_OriginalPDU.addVariableBinding(pdu.getVariableBinding(2));
			allInformationReceived(m_OriginalPDU);
		}

		@Override
		protected void timedOut() {
			m_Protocol.abortSync(TIMEDOUT_ERROR);
		}
	}
	
	private static String getAlarmModelTableIndices(final SnmpVar var) {
		final String oid = var.toString();
		return oid.substring(oid.indexOf('.', ".1.3.6.1.4.1.193.110.2.666.1.1.2.1.".length()));
	}

	private void allInformationReceived(final SnmpPDU pdu) {
		super.responseReceived(pdu);
	}

	@Override
	protected Alarm createSyncAlarm(final SnmpPDU pdu) {
		return new SNFSyncAlarm(m_Protocol, pdu);
	}
}
