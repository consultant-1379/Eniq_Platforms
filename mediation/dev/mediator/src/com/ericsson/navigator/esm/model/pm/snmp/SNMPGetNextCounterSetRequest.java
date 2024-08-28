package com.ericsson.navigator.esm.model.pm.snmp;

import com.adventnet.snmp.snmp2.SnmpAPI;
import com.adventnet.snmp.snmp2.SnmpPDU;
import com.ericsson.navigator.esm.MVM;
import com.ericsson.navigator.esm.manager.snmp.SNMPGetNextRequest;
import com.ericsson.navigator.esm.manager.snmp.SNMPRequest;
import com.ericsson.navigator.esm.model.pm.CounterSet;
import com.ericsson.navigator.esm.model.pm.CounterSetDefinition;
import com.ericsson.navigator.esm.model.pm.DefaultCounterSetIdentification;
import com.ericsson.navigator.esm.model.pm.DefaultCounterSetScheduling;

public class SNMPGetNextCounterSetRequest extends SNMPGetNextRequest<SNMPCounterSetList> {

    protected static final String TIMEDOUT_ERROR = "Synchronization request timed out";
	
	private final String classname = getClass().getName();
	private int fetchedCounterSetInstances = 0;
	private final int granularityPeriod;
	private final CounterSetDefinition definition;
    private final SNMPCounterSetList snmpCounterSetList;
    private final DefaultCounterSetScheduling<DefaultCounterSetIdentification> countersetSchedule ;
	public SNMPGetNextCounterSetRequest(final CounterSetDefinition definition, 
			final SNMPCounterSetList counterSetList, final int rop,final DefaultCounterSetScheduling<DefaultCounterSetIdentification> counterSetSchedule) {
		super(SNMPGetCounterSetRequest.getOIDs(
				definition, SNMPRequest.SYSTEMUPTIME_EX_INDEX), counterSetList);
		m_PDU = createRequest(m_OIDs, SnmpAPI.GETNEXT_REQ_MSG);
		granularityPeriod = rop*60;
		this.definition = definition;
		snmpCounterSetList = counterSetList ;
		countersetSchedule= counterSetSchedule;
	}

	@Override
	protected void timedOut() {
		
		MVM.logger.error(classname+".error(); CounterSet GetNext request " + 
				definition.getFileName() + " timed out, with fdn " + m_Protocol.getFDN());
		snmpCounterSetList.pmStatus(countersetSchedule);
	}

	@Override
	protected void error(final String error) {
		MVM.logger.error(classname+".error(); CounterSet GetNext request " +
				definition.getFileName() + " , with fdn " + 
				m_Protocol.getFDN() + ", error " + error);
		snmpCounterSetList.pmStatus(countersetSchedule);
	}

	protected void createResult(final SnmpPDU pdu) {
		++fetchedCounterSetInstances;
		final CounterSet counterSet = new SNMPCounterSet(
				m_Protocol.getFDN(),granularityPeriod, definition, m_OIDs, pdu, getInstance(m_OIDs, pdu));
		
		MVM.logger.info(classname + ".createResult(); Received response for counterset "+definition.getFileName()+" from "+ m_Protocol.getFDN() +", "+counterSet.getCounters().size()+" counters.");
		if(counterSet.getCounters().size() > 0){
			SNMPCounterSetStore.getInstance().sortNewCounterset(counterSet, false);
		}
		
		m_Protocol.correlate(counterSet);
		
	}

	static String getInstance(final String[] oids, final SnmpPDU pdu) {
		if(oids.length > 0){
			if(pdu.getVariableBindings().size() > 1){ //Avoid system up time at varbind index 0
				final String requestOID = oids[1];
				final String responseOID = pdu.getVariableBinding(1).getObjectID().toString();
				final String instance = responseOID.substring(requestOID.length());
				return instance;
			}
		}
		return "";
	}
	
	@Override
	protected String[] getOIDs(final SnmpPDU pdu) {
		final String[] oids = super.getOIDs(pdu);
		oids[0] = SNMPRequest.SYSTEMUPTIME_EX_INDEX;
		return oids;
	}

	protected void finished() {
		m_Protocol.correlateSnapShot(definition.getCounterSetId());
		
		if (MVM.logger.isDebugEnabled()) {
			MVM.logger.debug(classname+".finished(); --> Fetched " + fetchedCounterSetInstances  + 
					" counter set instances in " + (System.currentTimeMillis() - m_StartTime) + 
					" ms, for managed system: " + m_Protocol.getFDN());
		}
		//snmpCounterSetList.pmStatus();
		SNMPCounterSetStore.getInstance().tabularFinished(m_Protocol.getFDN(), definition.getFileName());
	}

}
