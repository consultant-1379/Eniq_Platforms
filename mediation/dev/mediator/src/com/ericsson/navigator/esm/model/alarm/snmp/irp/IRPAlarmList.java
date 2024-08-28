package com.ericsson.navigator.esm.model.alarm.snmp.irp;

import java.util.Map;

import org.apache.log4j.Logger;

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

public class IRPAlarmList extends AbstractSNMPAlarmList<SNMPAddressInformation> {
	private static final String classname = IRPAlarm.class.getName();
	private static final Logger logger = Logger.getLogger(classname);
	
	private static final long serialVersionUID = 1L;
	private static final String PROBABLECAUSEOID = ".1.3.6.1.4.1.3881.2.1.8.1.6";
	private static final String EVENTTYPEOID = ".1.3.6.1.4.1.3881.2.1.8.1.5";
	private static final String ALARMIDOID = ".1.3.6.1.4.1.3881.2.1.8.1.1";
	private static final String SPECIFICPROBLEMOID = ".1.3.6.1.4.1.3881.2.1.8.1.8";
	private static final String EVENTTIMEOID = ".1.3.6.1.4.1.3881.2.1.8.1.4";
	private static final String MANAGEDOBJECTINSTANCEOID = ".1.3.6.1.4.1.3881.2.1.8.1.3";
	private static final String PERCEIVEDSEVERITYOID = ".1.3.6.1.4.1.3881.2.1.8.1.7";
	private static final String ADDITIONALTEXTOID = ".1.3.6.1.4.1.3881.2.1.8.1.13";
	public static final String IRPALARMMOC = ".1.3.6.1.4.1.3881.2.1.8.1.2";
	
	private final Map<String, String> m_ManagedObjectInstanceMap;

	private static final String[] SYNCHOIDS = { PROBABLECAUSEOID, EVENTTYPEOID,
			ALARMIDOID, SPECIFICPROBLEMOID, EVENTTIMEOID,
			MANAGEDOBJECTINSTANCEOID, PERCEIVEDSEVERITYOID, ADDITIONALTEXTOID, IRPALARMMOC };

	public IRPAlarmList(final String fdn,
			final SNMPAddressInformation addressInformation, 
			final Map<String, String> managedObjectInstanceMap, 
			final Log<Alarm> log, final ConversionController conversionController, 
			final TrapReceiver trapReceiver) {
		super(fdn, addressInformation, log, conversionController, trapReceiver);
		m_ManagedObjectInstanceMap = managedObjectInstanceMap;
	}
	
	public String getFDN(final String managedObjectInstance){
        final String fdnFromMap = m_ManagedObjectInstanceMap.get(managedObjectInstance);
		if(fdnFromMap == null){
			if(managedObjectInstance == null || managedObjectInstance.length() == 0){
				return getFDN();
			}else if(managedObjectInstance.startsWith(getFDN())){
				  return managedObjectInstance;
			}else{
			  return getFDN()+","+managedObjectInstance;
			}
		}
		return fdnFromMap;		
	}

	public boolean isSynchable() {
		return true;
	}

	public void receivedTrap(final SnmpPDU pdu) throws TranslationException {
		if (pdu.getVariable(1).toString().startsWith(IRPAlarm.IRPALARMOID)) {
			correlate(new IRPAlarm(this, pdu));
		} else {
			
			if(pdu.getVariable(1).toString().startsWith(".1.3.6.1.6")){
				logger.warn(classname + "Generic Trap received, Generic traps not supported" + pdu.getVariable(1).toString() );
			}
			
			else{
				throw new TranslationException("Trap OID did not match any IRP OIDs: "
					+ pdu.getVariable(1).toString());
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
	public SNMPSynchronizationRequest<IRPAlarmList> getSynchronizationRequest() {
		return new IRPSynchronizationRequest(SYNCHOIDS, this);
	}
	
	@Override
	public boolean isUpdated(final Protocol<?, ?, ?> protocol) {
		//final IRPAlarmList irpAlarmList = (IRPAlarmList)protocol;
		//return super.isUpdated(protocol) || 
		//	!m_ManagedObjectInstanceMap.equals(irpAlarmList.m_ManagedObjectInstanceMap);
		return true;
	}
}
