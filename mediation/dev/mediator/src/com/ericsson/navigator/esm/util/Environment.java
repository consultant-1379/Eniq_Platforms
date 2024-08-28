package com.ericsson.navigator.esm.util;

import java.io.File;

public class Environment {

	public static final int DEFAULTREGISTRYPORT = 10101;
	public static final int REGISTRYPORT = Integer.getInteger("REGISTRYPORT",
			DEFAULTREGISTRYPORT);
	public static final String AGENTINTERFACE = System.getProperty(
			"AGENTINTERFACE", "agentinterface");
	public static final String BNSI_USAGE = System.getProperty("USAGE",
			"SendAlarms <agent name> [-sync|-xsync] -hbint <time sec> -ver <version> [-32bit]");
	public static final String TOPOLOGYFILE = System.getProperty(
			"TOPOLOGYFILE", "/eniq/mediator/conf/topology/SystemTopology.xml");
	public static final String SRCONFIGFILE = System.getProperty(
			"SRCONFIGFILE", "/eniq/mediator/conf/topology/SystemTopology.cfg");
	public static final File DATALISTFILE = new File(System.getProperty(
			"DATALISTFILE", "/eniq/mediator/runtime/datalist.ser"));
	public static final File ALARMLISTFILEBACKUP = new File(DATALISTFILE
			.getAbsoluteFile()
			+ ".backup");
	public static final long DATALISTSAVEDELAY = Long.getLong(
			"DATALISTSAVEDELAY", 300000);
	public static final long POLL_DELAY = 10 * 1000;
	public static final String REGISTRYHOST = System.getProperty("REGISTRYHOST", "");	
}
