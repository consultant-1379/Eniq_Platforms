package com.ericsson.navigator.esm.model.alarm.snmp.generic.mst.composer.expression;

import java.util.LinkedList;
import java.util.List;

import com.adventnet.snmp.mibs.MibOperations;
import com.adventnet.snmp.snmp2.SnmpPDU;

/**
 * The CompositeExpression class contains a list of expressions.
 * This can be used as a root expression.
 * @author qbacfre
 *
 */
public class CompositeExpression extends MapExpression {

	private final List<IExpression> expressions;
	private boolean isStatic = true;
	
	/**
	 * Constructor.
	 */
	public CompositeExpression() {
		expressions = new LinkedList<IExpression>();
	}
	
	/**
	 * Add an expression to the list.
	 * @param expression The expression to add.
	 * @return True if added, false if null.
	 */
	public boolean addExpression(final IExpression expression) {
		if (expression == null) {
			return false;
		}
		
		if (!expression.isStatic()) {
			isStatic = false;
		}
		
		expressions.add(expression);
		
		return true;
	}	
	
	/**
	 * This method will iterate over all expressions and evaluate
	 * each of them.
	 * @param pdu The pdu
	 * @param mibOperations The mib operations
	 * @param buffer The buffer to fill
	 */
	public void evaluate(final SnmpPDU pdu, final MibOperations mibOperations, final StringBuffer buffer) {
		
		for (final IExpression expression : expressions) {
			expression.evaluate(pdu, mibOperations, buffer);
		}
	}
	
	/**
	 * This method will iterate over all expressions and evaluate
	 * each of them.
	 * @param pdu The pdu
	 * @param buffer The buffer to fill
	 */
	public void evaluate(final SnmpPDU pdu, final StringBuffer buffer) {
		
		for (final IExpression expression : expressions) {
			expression.evaluate(pdu, buffer);
		}
	}
		
	/**
	 * Checks if the expression is a static expression or not.
	 */
	public boolean isStatic() {
		return isStatic;
	}
}
