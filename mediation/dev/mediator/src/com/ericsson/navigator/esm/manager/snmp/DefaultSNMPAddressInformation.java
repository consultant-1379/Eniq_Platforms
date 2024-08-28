package com.ericsson.navigator.esm.manager.snmp;

import java.io.Serializable;

import com.ericsson.navigator.esm.model.DefaultAddressInformation;
import com.ericsson.navigator.esm.model.alarm.snmp.SNMPAddressInformation;

public class DefaultSNMPAddressInformation extends DefaultAddressInformation implements SNMPAddressInformation,
		Serializable {

	private static final long serialVersionUID = 1L;
	private final String m_Community;
	private final VERSION m_Version;
	private final int m_Port;
	private final PROTOCOL_TYPE m_ProtocolType;

	public DefaultSNMPAddressInformation(final String address, final String backupIP, final int port,
			final PROTOCOL_TYPE protocolType, final String type, final String community, final VERSION version) {
		super(address, type, backupIP);
		m_Community = community;
		m_Port = port;
		m_Version = version;
		m_ProtocolType = protocolType;
 	}

	public int getPort() {
		return m_Port;
	}

	public VERSION getVersion() {
		return m_Version;
	}

	public String getCommunity() {
		return m_Community;
	}

	@Override
	public boolean equals(final Object o) {
		final DefaultSNMPAddressInformation addressInfo = (DefaultSNMPAddressInformation) o;
		return super.equals(o)
				&& m_Port == addressInfo.m_Port
				&& m_Community.equals(addressInfo.m_Community)
				&& m_Version.equals(addressInfo.m_Version)
				&& m_ProtocolType.equals(addressInfo.m_ProtocolType);
	}
	
	public String toString(){
		final StringBuffer b = new StringBuffer();
		b.append(super.toString());
		b.append(", Protocol: ");
		b.append(m_ProtocolType.toString());
		b.append(", Port: ");
		b.append(m_Port);
		b.append(", Version: ");
		b.append(m_Version);
		b.append(", Community: ");
		b.append(m_Community);
		return b.toString();
	}
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}

}
