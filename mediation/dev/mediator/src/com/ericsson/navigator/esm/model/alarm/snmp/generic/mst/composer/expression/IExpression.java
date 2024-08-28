package com.ericsson.navigator.esm.model.alarm.snmp.generic.mst.composer.expression;

import com.adventnet.snmp.mibs.MibOperations;
import com.adventnet.snmp.snmp2.SnmpPDU;
import com.ericsson.navigator.esm.model.alarm.snmp.generic.mst.TrapMap;

/**
 * The expression is a string that is compiled. The expression might contain 
 * keywords that should be evaluated. After compiling a expression three things might
 * happen. 
 * 
 * Example. 
 * expression = The trap was sent from agent @AGENTADDRESS
 * the @AGENTADDRESS will be converted into a IExpression and during
 * runtime that object will return a string representation of the @AGENTADDRESS 
 * 
 * If the expression does not contain any keywords the expression will be a string.
 * 
 * If the expression contains a reference to a variable such as $3 then this will
 * be evaluated during runtime to the value of the traps variable number three.
 * 
 * If the expression did contain keywords then the expression will not be evaluated
 * until a PDU is sent to it which contain information. The expression will then contain
 * constant strings and one or many IExpressionEvaluators that will create a string runtime.
 * @author qbacfre
 *
 */
public interface IExpression {
		
	/**
	 * Based on the trap value, retrieves/lookups the corresponding value from the mib.
	 * Translate the value as specified by the mib.
	 * Evaluate the expression and return a string.
	 * @param pdu The PDU for which the expression should be evaluated around.
	 * @param mibOperations The MIB operations for the given system.
	 * @param buffer The StringBuffer containing the partly finished expression.
	 * @return String representation of the expression. 
	 */
	void evaluate(SnmpPDU pdu, MibOperations mibOperations, StringBuffer buffer);
	
	/**
	 * First makes a raw translation and then maps this raw value to the map.
	 * Translate the value as specified by map.
	 * Evaluate the expression and return a string.
	 * @param pdu The PDU for which the expression should be evaluated around.
	 * @param map The TrapMap for which the resulting expression should be mapped to.
	 * @param buffer The StringBuffer containing the partly finished expression.
	 * @return String representation of the expression. 
	 */
	void evaluate(SnmpPDU pdu, TrapMap map, StringBuffer buffer);
	
	/**
	 * Makes a raw translation - copies the value from the trap.
	 * Evaluate the expression and return a string.
	 * @param pdu The PDU for which the expression should be evaluated around.
	 * @param buffer The StringBuffer containing the partly finished expression.
	 * @return String representation of the expression. 
	 */
	void evaluate(SnmpPDU pdu, StringBuffer buffer);
	
	/**
	 * This method checks if the expression is static, that is if 
	 * it needs runtime evaluation or not. 
	 * @return True if it does not need runtime evaluation, false otherwise
	 */
	boolean isStatic();
}
