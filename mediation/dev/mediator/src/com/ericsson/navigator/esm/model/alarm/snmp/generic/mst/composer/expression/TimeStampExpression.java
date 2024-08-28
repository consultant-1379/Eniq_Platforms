package com.ericsson.navigator.esm.model.alarm.snmp.generic.mst.composer.expression;

import com.adventnet.snmp.snmp2.SnmpPDU;

/**
 * This expression will return the up-time for the agent that sent the trap.
 * 
 * If it is unable to return the up-time then a empty string will be returned.
 * @author qbacfre
 *
 */
public class TimeStampExpression extends VariableExpression {
	
	/**
	 * Fetches the upTime from the PDU and returns it as a 
	 * string.
	 * 
	 * A empty string is returned if unable to retrieve the expression.
	 * @param pdu The PDU to extract the value from.
	 */
	@Override
	public String getPartExpression(final SnmpPDU pdu) {
		return "" + pdu.getUpTime();
	}

}
