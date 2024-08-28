package com.ericsson.navigator.esm.model.alarm.snmp.generic;

import com.adventnet.snmp.snmp2.SnmpPDU;
import com.ericsson.navigator.esm.manager.snmp.TranslationException;
import com.ericsson.navigator.esm.manager.snmp.TrapReceiver;
import com.ericsson.navigator.esm.model.alarm.Alarm;
import com.ericsson.navigator.esm.model.alarm.snmp.AbstractSNMPAlarmList;
import com.ericsson.navigator.esm.model.alarm.snmp.SNMPAddressInformation;
import com.ericsson.navigator.esm.model.alarm.snmp.SNMPSynchronizationRequest;
import com.ericsson.navigator.esm.model.conversion.ConversionController;
import com.ericsson.navigator.esm.util.SupervisionException;
import com.ericsson.navigator.esm.util.log.Log;

public class GenericSNMPAlarmList extends AbstractSNMPAlarmList<SNMPAddressInformation> {

	private static final long serialVersionUID = 1L;
	protected final MibController m_MibController;

	public GenericSNMPAlarmList(final String fdn,
			final SNMPAddressInformation addressInformation, 
			final Log<Alarm> log, 
			final ConversionController conversionController, 
			final TrapReceiver trapReceiver, 
			final MibController mibController) {
		super(fdn, addressInformation, log, conversionController, trapReceiver);
		m_MibController = mibController;
	}

	public boolean isSynchable() {
		return false;
	}

	public void receivedTrap(final SnmpPDU pdu) throws TranslationException {
		if (pdu != null) {
			try {
				correlate(new SnmpEvent(this, pdu, m_MibController.getMibOperations()));
			} catch (final Exception e) {
				throw new TranslationException("Error translating Generic SNMP event "+pdu.getTrapOID(), e);
			}
		}
	}

	@Override
	public void startSNMPSupervision() throws SupervisionException {
	}

	@Override
	public void stopSNMPSupervision() throws SupervisionException {
	}

	@Override
	public SNMPSynchronizationRequest<GenericSNMPAlarmList> getSynchronizationRequest() {
		return null;
	}
}