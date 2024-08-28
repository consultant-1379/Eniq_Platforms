package com.ericsson.navigator.esm.agent.rmi;

import java.util.List;

public interface ConnectivityActionInterface {
	
	enum SnmpProtocolType {SNMP, MST, IRP, SNF};
	enum SnmpVersion {v1, v2c};

	void addXMLTopology(final String dir, final String nameAttribute, 
			final List<String> validTags);
	void addSnmpAlarmList(final SnmpProtocolType protocolType, final String type, 
			final String ip, final SnmpVersion version, final int port, final String community);
	void addTxfAlarmList(final String fdn);
	void addSnmpCounterSet(final String filePath, final String ip, final SnmpVersion version, 
			final int port, final String community);
}
