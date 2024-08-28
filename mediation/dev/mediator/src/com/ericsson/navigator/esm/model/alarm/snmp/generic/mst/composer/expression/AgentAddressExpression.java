package com.ericsson.navigator.esm.model.alarm.snmp.generic.mst.composer.expression;

import java.net.InetAddress;

import com.adventnet.snmp.snmp2.SnmpPDU;

/**
 * The AgentAddressExpression fetches the AgentAddress from the PDU and
 * returns it as a string.
 * @author qbacfre
 *
 */
public class AgentAddressExpression extends VariableExpression {
	
	/**
	 * Fetches the AgentAddress from the PDU and returns it as a 
	 * string. The Agent address is set to the remote UDP packet 
	 * address by the TrapReceiver.
	 */
	@Override
	public String getPartExpression(final SnmpPDU pdu) {
		final InetAddress addr = pdu.getAgentAddress();
		return addr.getHostAddress();
	}
}

