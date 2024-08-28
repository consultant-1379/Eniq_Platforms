package com.ericsson.navigator.esm.model.alarm.snmp.generic.mst.composer.expression;

import org.apache.log4j.Logger;

import com.adventnet.snmp.snmp2.SnmpPDU;

/**
 * This expression will retrieve the generic trap information from 
 * the pdu.
 * @author qbacfre
 *
 */
public class GenericTrapExpression extends VariableExpression {

	private static Logger logger = Logger.getLogger(GenericTrapExpression.class.getName());
	
	/**
	 * Fetches the generic trap information from the pdu
	 */
	@Override
	public String getPartExpression(final SnmpPDU pdu) {
		try {
			final int version = pdu.getTrapType();
			return String.valueOf(version);
		} catch (final Exception e) {
			logger.error("Unable to retrive generic trap information from pdu", e);
			return "";
		}
	}
}
