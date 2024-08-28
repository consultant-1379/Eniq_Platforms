package com.ericsson.navigator.esm.model.pm.snmp;

import java.util.ArrayList;
import java.util.List;

import com.adventnet.snmp.snmp2.SnmpAPI;
import com.adventnet.snmp.snmp2.SnmpPDU;
import com.ericsson.navigator.esm.MVM;
import com.ericsson.navigator.esm.manager.snmp.SNMPGetRequest;
import com.ericsson.navigator.esm.manager.snmp.SNMPRequest;
import com.ericsson.navigator.esm.model.pm.CounterDefinition;
import com.ericsson.navigator.esm.model.pm.CounterSet;
import com.ericsson.navigator.esm.model.pm.CounterSetDefinition;
import com.ericsson.navigator.esm.model.pm.DefaultCounterSetIdentification;
import com.ericsson.navigator.esm.model.pm.Counter.CounterType;
import com.ericsson.navigator.esm.model.pm.DefaultCounterSetScheduling;

public class SNMPGetCounterSetRequest extends SNMPGetRequest<SNMPCounterSetList> {
	
	private final CounterSetDefinition definition;
	private final int granularityPeriod;
	private final SNMPCounterSetList snmpCounterSetList;
	private static final String classname = SNMPGetCounterSetRequest.class.getName();
	private final DefaultCounterSetScheduling<DefaultCounterSetIdentification> countersetSchedule ;
	public SNMPGetCounterSetRequest(final CounterSetDefinition definition, 
			final SNMPCounterSetList counterSetList, final int rop, final DefaultCounterSetScheduling<DefaultCounterSetIdentification> counterSetSchedule) {
		super(getOIDs(definition, SNMPRequest.SYSTEMUPTIME), counterSetList);
		this.definition = definition;
		granularityPeriod = rop*60;
		snmpCounterSetList = counterSetList;
		countersetSchedule= counterSetSchedule;
	}

	static String[] getOIDs(final CounterSetDefinition definition, final String upTime) {
		final List<String> oids = new ArrayList<String>();
		oids.add(upTime);
		for(CounterDefinition counter : definition.getDefinitions().values()){
			if(counter.getType() != CounterType.KPI && !upTime.equals(counter.getId())) {
				oids.add(counter.getId());
			}
		}
		return oids.toArray(new String[oids.size()]);
	}

	@Override
	protected void error(final String error) {
		MVM.logger.error(classname+".error(); CounterSet request " +
				definition.getFileName() + " , with fdn " + 
				m_Protocol.getFDN() + ", error " + error);
		
		snmpCounterSetList.pmStatus(countersetSchedule);
	}

	@Override
	protected void responseReceived(final SnmpPDU pdu) {
		if (pdu.getErrstat() == SnmpAPI.SNMP_ERR_NOSUCHNAME) {
			//snmpCounterSetList.pmStatus();
			return;
		}
		
		final CounterSet counterSet = new SNMPCounterSet(
				m_Protocol.getFDN(),granularityPeriod, definition, m_OIDs, pdu);
		
		
		m_Protocol.correlate(counterSet);
		m_Protocol.correlateSnapShot(definition.getCounterSetId());
		
		MVM.logger.info(classname + ".responseReceived(); Received response for counterset "+definition.getFileName()+" from "+ m_Protocol.getFDN() +", "+counterSet.getCounters().size()+" counters.");
		if(counterSet.getCounters().size() >0){
			SNMPCounterSetStore.getInstance().sortNewCounterset(counterSet, true);
		}
		//snmpCounterSetList.pmStatus();
	}

	@Override
	protected void timedOut() {
		MVM.logger.error(classname+".error(); CounterSet request " + 
				definition.getFileName() + " timed out, with fdn " + m_Protocol.getFDN());
		
		snmpCounterSetList.pmStatus(countersetSchedule);
	}
}
