package com.ericsson.navigator.esm.model.alarm.snmp.generic.mst.composer.expression;

import com.adventnet.snmp.mibs.MibOperations;
import com.adventnet.snmp.snmp2.SnmpPDU;

/**
 * This represents a expression that contains a IExpressionEvaluator and must thus
 * be evaluated runtime.
 * @author qbacfre
 *
 */
public abstract class VariableExpression extends MapExpression {

	/**
	 * This method should be overridden by other expressions.
	 * This method is called to get the value runtime
	 * @return String or empty string if unsuccessful.
	 */
	public abstract String getPartExpression(SnmpPDU pdu);
	
	/**
	 * Runs the evaluate on the evaluator and appends this to the expression buffer.
	 */
	public void evaluate(final SnmpPDU pdu, final MibOperations mibOperations, final StringBuffer buffer) {
		buffer.append(getPartExpression(pdu));
	}
	
	/**
	 * Runs the evaluate on the evaluator and appends this to the expression buffer.
	 */
	public void evaluate(final SnmpPDU pdu, final StringBuffer buffer) {
		buffer.append(getPartExpression(pdu));
	}
	
	/**
	 * Checks if this is a static expression or not. 
	 * returns false;
	 */
	public boolean isStatic() {
		return false;
	}
}
