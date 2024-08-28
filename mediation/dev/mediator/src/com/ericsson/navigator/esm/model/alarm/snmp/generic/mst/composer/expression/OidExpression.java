package com.ericsson.navigator.esm.model.alarm.snmp.generic.mst.composer.expression;

import com.adventnet.snmp.snmp2.SnmpAPI;
import com.adventnet.snmp.snmp2.SnmpPDU;

public class OidExpression extends VariableExpression {

	/**
	 * This will return the OID for the PDU
	 */
	@Override
	public String getPartExpression(final SnmpPDU pdu) {
		if (pdu.getCommand() == SnmpAPI.TRP2_REQ_MSG) {
			return pdu.getVariable(1).toString();
		} else {
			final String enterprise = pdu.getEnterprise().toString();
			final int specificType = pdu.getSpecificType();
			return enterprise + ".0." + specificType;
		}
	}

}
