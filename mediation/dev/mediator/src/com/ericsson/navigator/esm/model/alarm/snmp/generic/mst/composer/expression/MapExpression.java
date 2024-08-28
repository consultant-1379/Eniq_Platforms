package com.ericsson.navigator.esm.model.alarm.snmp.generic.mst.composer.expression;

import com.ericsson.navigator.esm.model.alarm.snmp.generic.mst.TrapMap;
import com.adventnet.snmp.mibs.MibOperations;
import com.adventnet.snmp.snmp2.SnmpPDU;

/**
 * A MapExpression handles information on howto map.
 * 
 * @author qhapers, qagnkuc
 * 
 */
public abstract class MapExpression implements IExpression {

	/**
	 * Runs the evaluate.
	 */
	public abstract void evaluate(SnmpPDU pdu, MibOperations mibOperations, StringBuffer buffer);
	
	/**
	 * Evaluate the expression and return a string.
	 * @param pdu The PDU for which the expression should be evaluated around.
	 * @param map The TrapMap for which the resulting expression should be mapped to.
	 * @param buffer The StringBuffer containing the partly finished expression.
	 * @return String representation of the expression. 
	 */
	public void evaluate(final SnmpPDU pdu, final TrapMap map, final StringBuffer buffer) {
		evaluate(pdu, buffer); // Covers the 'raw' case
		
		if (map != null) {
			final String value = map.getEntry(buffer.toString());
			buffer.delete(0, buffer.length());		
			buffer.append(value);
		}
	}
	
	/**
	 * Runs the evaluate.
	 */
	public abstract void evaluate(SnmpPDU pdu, StringBuffer buffer);
}
