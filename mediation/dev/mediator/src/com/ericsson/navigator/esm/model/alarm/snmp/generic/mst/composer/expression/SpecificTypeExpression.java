package com.ericsson.navigator.esm.model.alarm.snmp.generic.mst.composer.expression;

import org.apache.log4j.Logger;

import com.adventnet.snmp.snmp2.SnmpPDU;

/**
 * This will fetch the specific type of the trap. 
 * @author qbacfre
 *
 */
public class SpecificTypeExpression extends VariableExpression {

	private static Logger logger = Logger.getLogger(SpecificTypeExpression.class.getName());
	
	/**
	 * Extracts the specifict type of the pdu.
	 */
	@Override
	public String getPartExpression(final SnmpPDU pdu) {
		try {
			return String.valueOf(pdu.getSpecificType());
		} catch (final Exception e)  {
			logger.error("Unable to get specific type from pdu", e);
			return "";
		}
	}
}
