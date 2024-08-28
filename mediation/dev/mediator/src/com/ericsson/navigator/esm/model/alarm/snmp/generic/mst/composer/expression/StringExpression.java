package com.ericsson.navigator.esm.model.alarm.snmp.generic.mst.composer.expression;

import com.adventnet.snmp.mibs.MibOperations;
import com.adventnet.snmp.snmp2.SnmpPDU;

/**
 * This is a static string in a expression.
 * @author qbacfre
 *
 */
public class StringExpression extends MapExpression {

	private final String expression;
	
	/**
	 * @param constant
	 */
	public StringExpression(final String constant) {
		this.expression = constant;
	}
	
	/**
	 * Evaluates the constant. In this case it only returns the string.
	 */
	public void evaluate(final SnmpPDU pdu, final MibOperations mibOperations, final StringBuffer buffer) {
		buffer.append(expression);
	}
	
	/**
	 * Evaluates the constant. In this case it only returns the string.
	 */
	public void evaluate(final SnmpPDU pdu, final StringBuffer buffer) {
		buffer.append(expression);
	}

	/**
	 * Checks if this is a static expression or not
	 * returns true;
	 */
	public boolean isStatic() {
		return true;
	}
}
