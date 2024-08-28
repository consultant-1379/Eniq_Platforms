package com.ericsson.navigator.esm.model.alarm.snmp.snf;

import java.util.Map;

import com.adventnet.snmp.snmp2.SnmpPDU;
import com.ericsson.navigator.esm.manager.snmp.TranslationException;
import com.ericsson.navigator.esm.manager.snmp.TrapReceiver;
import com.ericsson.navigator.esm.model.Protocol;
import com.ericsson.navigator.esm.model.alarm.Alarm;
import com.ericsson.navigator.esm.model.alarm.snmp.AbstractSNMPAlarmList;
import com.ericsson.navigator.esm.model.alarm.snmp.SNMPAddressInformation;
import com.ericsson.navigator.esm.model.alarm.snmp.SNMPSynchronizationRequest;
import com.ericsson.navigator.esm.model.conversion.ConversionController;
import com.ericsson.navigator.esm.util.SupervisionException;
import com.ericsson.navigator.esm.util.log.Log;

public class SNFAlarmList extends AbstractSNMPAlarmList<SNMPAddressInformation> {

	private static final long serialVersionUID = 1L;
	private final Map<String, String> m_ResourceIdToName;

	public SNFAlarmList(final String fdn, final SNMPAddressInformation ai, 
			final Map<String, String> resourceIdToName, final Log<Alarm> log, 
			final ConversionController conversionController, final TrapReceiver trapReceiver) {
		super(fdn, ai, log, conversionController, trapReceiver);
		m_ResourceIdToName = resourceIdToName;
	}

	public void receivedTrap(final SnmpPDU pdu) throws TranslationException{
		if (pdu.getVariable(1).toString().equals(SNFEvent.EVENTOID)) {
			correlate(new SNFEvent(this, pdu));
		} else if (pdu.getVariable(1).toString().equals(SNFAlarm.RAISEOID)) {
			correlate(new SNFAlarm(this, pdu, false));
		} else if (pdu.getVariable(1).toString().equals(SNFAlarm.CEASEOID)) {
			correlate(new SNFAlarm(this, pdu, true));
		} else {
			throw new TranslationException("Trap OID did not match any SNF OIDs: "
					+ pdu.getVariable(1).toString());
		}
	}
	
	/**
	 * Get the FDN of matching the resourceID if any.
	 * 
	 * First this method tries to find a one to one match (1.1 = 1.1)
	 * If this is not possible it will try and find a parent wildcard
	 * to match with (1.* = 1.1). If this is not either available it will
	 * try and match the parent resourceId (1 = 1). IF none of the above match 
	 * the system FDN will be returned.
	 * 
	 * @param resourceId The resource ID to search for.
	 * @return A FDN matching the resourceId on one of above described ways.
	 */
	public String getFDN(String resourceId){
		if(resourceId.length() > 0 && resourceId.charAt(0) == '.'){
			resourceId = resourceId.substring(1);
		}
		String name = m_ResourceIdToName.get(resourceId);
		if(name == null){
			final int index = resourceId.lastIndexOf('.');
			if(index == -1){
				return getFDN();
			}
			final String parentResourceId = resourceId.substring(0, index);
			name = m_ResourceIdToName.get(parentResourceId+".*");
			if(name == null){
				return getFDN(parentResourceId);
			}
		}
		return name;
	}

	public boolean isSynchable() {
		return true;
	}

	@Override
	public void startSNMPSupervision() throws SupervisionException {
	}

	@Override
	public void stopSNMPSupervision() throws SupervisionException {
	}

	@Override
	public SNMPSynchronizationRequest<SNFAlarmList> getSynchronizationRequest() {
		return new SNFSynchronizationRequest(this);
	}
	
	@Override
	public boolean isUpdated(final Protocol<?, ?, ?> protocol) {
		//final SNFAlarmList snfAlarmList = (SNFAlarmList)protocol;
		//return super.isUpdated(protocol) || !m_ResourceIdToName.equals(snfAlarmList.m_ResourceIdToName);
		return true;
	}
}
