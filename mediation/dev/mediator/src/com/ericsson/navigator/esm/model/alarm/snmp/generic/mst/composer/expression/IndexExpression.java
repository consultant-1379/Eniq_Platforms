package com.ericsson.navigator.esm.model.alarm.snmp.generic.mst.composer.expression;

import org.apache.log4j.Logger;

import com.adventnet.snmp.mibs.MibOperations;
import com.adventnet.snmp.snmp2.SnmpPDU;
import com.adventnet.snmp.snmp2.SnmpVar;
import com.adventnet.snmp.snmp2.SnmpVarBind;

/**
 * This class will handle expressions that have $1 or $<numeric_value>
 * The VariableExpression will fetch the variable on index 2 if it gets
 * a $2. 
 * 
 * If a number that does not exist in the pdu is passed a empty string will
 * be returned
 * 
 * @author qbacfre
 *
 */
public class IndexExpression extends MapExpression {

	private static Logger logger = Logger.getLogger(IndexExpression.class.getName());
	
	private final int index;
	
	/**
	 * Constructor. 
	 * @param index This is the index to where in the trap the 
	 * value should be retrieved.
	 */
	public IndexExpression(final int index) {
		this.index = index;
	}
	
	/**
	 * This will grab the value from the index and convert it
	 * to a string. 
	 * If the index does not exist, a empty string will be returned.
	 * 
	 * @param pdu The pdu to grab the value from.
	 * @param buffer buffer to fill with value.
	 */
	public void evaluate(final SnmpPDU pdu, final MibOperations mibOperations, final StringBuffer buffer) {
		
		final SnmpVarBind varBind = pdu.getVariableBinding(index);
		
		String value = "";
		
		if (varBind != null) {
			value = mibOperations.toString(varBind.getVariable(), varBind.getObjectID());
		} else {
			logger.error("The pdu does not contain any variable at index " + index);
		}
		
		buffer.append(value);		
	}
	
	/**
	 * This will grab the value from the index and convert it
	 * to a string. 
	 * If the index does not exist, a empty string will be returned.
	 * 
	 * @param pdu The pdu to grab the value from.
	 * @param buffer buffer to fill with value.
	 */
	public void evaluate(final SnmpPDU pdu, final StringBuffer buffer) {
				
		final SnmpVar var = pdu.getVariable(index);
		
		String value = ""; 
		
		if (var != null) {
			value = var.toString();
		} else {
			logger.error("The pdu does not contain any variable at index " + index);
		}
		
		buffer.append(value);		
	}

	/**
	 * Check if this is a static expression or not.
	 * returns false;
	 */
	public boolean isStatic() {
		return false;
	}
}
