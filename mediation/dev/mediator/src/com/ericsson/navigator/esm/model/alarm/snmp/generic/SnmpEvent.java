package com.ericsson.navigator.esm.model.alarm.snmp.generic;

import java.util.Date;
import java.util.Iterator;

import com.adventnet.snmp.mibs.MibOperations;
import com.adventnet.snmp.snmp2.SnmpAPI;
import com.adventnet.snmp.snmp2.SnmpPDU;
import com.adventnet.snmp.snmp2.SnmpVarBind;
import com.ericsson.navigator.esm.model.alarm.DefaultAlarm;
import com.ericsson.navigator.esm.model.alarm.snmp.SNMPAddressInformation;
import com.ericsson.navigator.esm.model.alarm.snmp.SNMPAlarmList;

import static com.ericsson.navigator.esm.util.Hash.fnv_64_1a;

public class SnmpEvent extends DefaultAlarm {

	private static final long serialVersionUID = 1L;
	private String m_VarBindText = null;
	
	enum TrapType {COLDSTART, WARMSTART, LINKDOWN, LINKUP, 
		AUTHENTICATIONFAILURE, EPGNEIGHBORLOSS, ENTERPRISE;
		@Override
		public String toString() {
			switch(ordinal()){
			case 0:
				return "ColdStart";
			case 1:
				return "WarmStart";
			case 2:
				return "LinkDown";
			case 3:
				return "LinkUp";
			case 4:
				return "AuthenticationFailure";
			case 5:
				return "EgpNeighborLoss";
			default:
				return "Enterprise";
			}
		}
	};
	
	public SnmpEvent(final SNMPAlarmList<SNMPAddressInformation> system, final SnmpPDU pdu, final MibOperations mibOperations) {
		managedObjectInstance = system.getFDN();
		m_EventType = "other";
		m_ProbableCause = "other";
		if(pdu.getCommand() ==  SnmpAPI.TRP_REQ_MSG){
			m_SpecificProblem = getV1SpecificProblem(pdu, mibOperations);
		} else {
			m_SpecificProblem = getV2CSpecificProblem(pdu, mibOperations);
		}
		m_EventTime = new Date();
		m_PerceivedSeverity = PerceivedSeverity.WARNING;
		m_RecordType = RecordType.EVENT;
		m_AdditionalText =  mibOperations.toString(pdu);		
		createUniqueText(mibOperations, pdu); //NOPMD
		generateUniqueId();//NOPMD
		m_VarBindText = null;
	}
	
	private String getV1SpecificProblem(final SnmpPDU pdu, final MibOperations mibOperations) {
		final StringBuffer specificProblem = new StringBuffer();
		if(pdu.getTrapType() == 6){
			specificProblem.append(mibOperations.toString(pdu.getEnterprise()));
			specificProblem.append(".0.");
			specificProblem.append(pdu.getSpecificType());
		} else {
			specificProblem.append(TrapType.values()[pdu.getTrapType()].toString());
		}
		return specificProblem.toString();
	}

	private String getV2CSpecificProblem(final SnmpPDU pdu, final MibOperations mibOperations){
		return mibOperations.toString(pdu.getVariable(1), pdu.getObjectID(1));
	}
	
	@Override
	protected void generateUniqueId() {
		uniqueId = fnv_64_1a(managedObjectInstance + UNIQUESEPERATOR + m_VarBindText);
	}
	
	/**
	 * Creates a readable text representation of the trap. Since this
	 * text will be used to generate the uniqueId for the alarm this text
	 * must not include data (up time/date..) that will change when an equal 
	 * trap is generated later at a later time.
	 */
	@SuppressWarnings("unchecked")
	protected void createUniqueText(final MibOperations mibOperations, final SnmpPDU pdu){
		final StringBuffer text = new StringBuffer();
		final Iterator<SnmpVarBind> variableBindings = pdu.getVariableBindings().iterator();
		if(pdu.getCommand() == SnmpAPI.TRP_REQ_MSG){
			text.append(pdu.getEnterprise());
			text.append(pdu.getTrapType());
			text.append(pdu.getSpecificType());
		} else {
			//Remove UpTime as this will change for each trap
			variableBindings.next();
		}
		while(variableBindings.hasNext()){
			text.append(mibOperations.toString(variableBindings.next()));
		}
		m_VarBindText = text.toString();
	}
}
