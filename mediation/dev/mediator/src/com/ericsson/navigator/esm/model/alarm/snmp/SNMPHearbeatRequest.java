package com.ericsson.navigator.esm.model.alarm.snmp;

import com.adventnet.snmp.snmp2.SnmpPDU;
import com.ericsson.navigator.esm.MVM;
import com.ericsson.navigator.esm.manager.snmp.SNMPGetRequest;

public class SNMPHearbeatRequest<MS extends SNMPAlarmList<? extends SNMPAddressInformation>> extends SNMPGetRequest<MS> {
	
	private final String classname = getClass().getName();
	private String ipAddress;
	
	public SNMPHearbeatRequest(final String OID, final MS managedSystem){
		super(new String[]{OID}, managedSystem);
		ipAddress = managedSystem.getAddressInformation().getAddress();
	}
	
	@Override
	protected void timedOut() {
		System.setProperty("HeartbeatStatus:"+ ipAddress, "false");
		
		m_Protocol.setSystemUnavailable("Heartbeat request timed out!");
	}
	
	@Override
	public void responseReceived(final SnmpPDU pdu) {
		final long systemUptime = (Long) pdu.getVariable(0).getVarObject(); //pdu.getUpTime(); does not work
		
		System.setProperty("HeartbeatStatus:"+ipAddress, "true");
		
		if (MVM.logger.isDebugEnabled()) {
			MVM.logger.debug(classname+".responseReceived(); Heartbeat response " + m_Protocol.getFDN() + " " + systemUptime);
		}
		
		if (systemUptime < m_Protocol.getSystemUptime()) {
			if (MVM.logger.isDebugEnabled()) {
				MVM.logger.debug(classname+".responseReceived(); Node restarted during heartbeat interval, fdn=" + m_Protocol.getFDN());
			}
			m_Protocol.setSystemUnavailable("Node restarted during heartbeat interval");
		}

		m_Protocol.setSystemUptime(systemUptime);
		m_Protocol.setSystemAvailable();
	}

	@Override
	protected void error(final String error) {
		System.setProperty("HeartbeatStatus:"+ ipAddress, "false");
		m_Protocol.setSystemUnavailable(error);
	}
}
