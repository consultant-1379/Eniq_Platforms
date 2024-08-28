package com.ericsson.navigator.esm.model.alarm.snmp.generic.mst;


import com.adventnet.snmp.mibs.MibOperations;
import com.adventnet.snmp.snmp2.SnmpPDU;
import com.ericsson.navigator.esm.MVM;
import com.ericsson.navigator.esm.manager.snmp.TranslationException;
import com.ericsson.navigator.esm.manager.snmp.TrapReceiver;
import com.ericsson.navigator.esm.model.alarm.Alarm;
import com.ericsson.navigator.esm.model.alarm.snmp.SNMPAddressInformation;
import com.ericsson.navigator.esm.model.alarm.snmp.generic.GenericSNMPAlarmList;
import com.ericsson.navigator.esm.model.alarm.snmp.generic.MibController;
import com.ericsson.navigator.esm.model.conversion.ConversionController;
import com.ericsson.navigator.esm.util.log.Log;

public class MSTAlarmList extends GenericSNMPAlarmList {

	private static final long serialVersionUID = 1L;
	private final TrapTranslator m_Translator;

	public MSTAlarmList(final String fdn,
			final SNMPAddressInformation addressInformation, 
			final Log<Alarm> log, 
			final ConversionController conversionController, 
			final TrapReceiver trapReceiver,
			final MibController mibController,
			final TrapTranslator translator) {
		super(fdn, addressInformation, log, conversionController, trapReceiver, mibController);
		this.m_Translator = translator;
	}

	public MibOperations getMibOperations(){
		return m_MibController.getMibOperations();
	}

	@Override
	public void receivedTrap(final SnmpPDU pdu) throws TranslationException {
		if (MVM.logger.isDebugEnabled()) {
			MVM.logger.debug(this.getClass().getName()+".receivedTrap(); -->" + getFDN());
		}
		
		if(pdu.getVariable(1).toString().startsWith(".1.3.6.1.6")){ //HN53033
			MVM.logger.warn(this.getClass().getName() + "Generic Trap received, Generic traps not supported " + pdu.getVariable(1).toString() );
		}
		
		else{
		final MSTAlarm alarm = m_Translator.translateAlarm(pdu, this);
		
		if (alarm == null) {
			if (MVM.logger.isDebugEnabled()) {
				MVM.logger.debug(this.getClass().getName()+".receivedTrap(); " + getFDN() + " no trap translation");
			}
			correlate(new NonTranslatedEvent(this, pdu, m_MibController.getMibOperations()));
		} else if (alarm.isSuppressed()) {
			if (MVM.logger.isDebugEnabled()) {
				MVM.logger.debug(this.getClass().getName()+".receivedTrap(); " + getFDN() + " suppressed trap");
			}
		} else {	
			if (MVM.logger.isDebugEnabled()) {
				MVM.logger.debug(this.getClass().getName()+".receivedTrap(); " + getFDN() + " translated trap");
			}
			correlate(alarm);
		}
		}
		if (MVM.logger.isDebugEnabled()) {
			MVM.logger.debug(this.getClass().getName()+".receivedTrap(); <--");
		}
	}
}
