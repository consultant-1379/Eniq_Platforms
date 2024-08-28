package com.ericsson.navigator.esm.model.alarm.snmp.generic.mst.composer.expression;

import org.apache.log4j.Logger;

import com.adventnet.snmp.snmp2.SnmpPDU;

/**
 * This VariableExpression will return the enterprise oid of the 
 * SnmpPDU.
 * @author qbacfre
 *
 */
public class EnterpriseExpression extends OidExpression {
	
	private static Logger logger = Logger.getLogger(EnterpriseExpression.class.getName());
	
	/**
	 * Retrieve the enterprise variable from the pdu.
	 * @param pdu The pdu to grab the information from.
	 */
	@Override
	public String getPartExpression(final SnmpPDU pdu) {
		try {
			if (pdu.getVersion() == 0) {
				return pdu.getEnterprise().toString();
			} else {
				final String enterprise = super.getPartExpression(pdu);
				return enterprise.substring(0, enterprise.length() - 4);
			}
		} catch (final Exception e) {
			logger.error("EnterpriseExpression unable to get enterprise for trap", e);
			return "";
		}
	}
}
