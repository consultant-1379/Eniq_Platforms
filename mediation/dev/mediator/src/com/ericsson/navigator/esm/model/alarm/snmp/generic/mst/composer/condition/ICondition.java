package com.ericsson.navigator.esm.model.alarm.snmp.generic.mst.composer.condition;

import com.adventnet.snmp.snmp2.SnmpPDU;

public interface ICondition {

	/**
	 * This will compile the expression and check if the condition is valid.
	 * 
	 * @return true if valid condition and false otherwise.
	 */
	boolean compile();

	/**
	 * The validate method will grab the constant or variable
	 * and match it against the condition.
	 * @param pdu The PDU that should be validated against.
	 * @return true if the PDU meets the criteria, false otherwise.
	 */
	boolean matches(SnmpPDU pdu);

}