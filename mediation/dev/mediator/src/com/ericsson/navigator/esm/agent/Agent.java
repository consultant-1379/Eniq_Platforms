package com.ericsson.navigator.esm.agent;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.ericsson.navigator.esm.agent.rmi.AgentInterface;
import com.ericsson.navigator.esm.agent.rmi.AgentSession;
import com.ericsson.navigator.esm.agent.rmi.AlarmActionInterface;
import com.ericsson.navigator.esm.model.ManagedDataType;

public abstract class Agent {

	protected AgentInterface m_AgentInterface = null;
	protected AgentSession m_Session = null;
	protected boolean m_Exiting = false;
	protected final String m_Name;
	private final String m_AgentInterfaceName;
	private final int m_Port;
	private final String m_Host;
	protected AlarmActionInterface m_AlarmActionInterface;
	protected String troubleDesc = "";
	
	public enum ExitCode {
		NORMAL, CONNECTION_BROKEN, REGISTRY_NOT_FOUND, AGENTINTERFACE_NOT_FOUND, AGENT_ERROR
	};

	private final static SimpleDateFormat DATEFORMATTER = new SimpleDateFormat(
			"yyyyMMddHHmmss");

	public Agent(final String name, final String agentInterfaceName,
			final String host, final int port) {
		m_Name = name;
		m_Host = host;
		m_AgentInterfaceName = agentInterfaceName;
		m_Port = port;
	}

	public void disconnect(final ExitCode code) {
		m_Exiting = true;
	}

	/**
	 * Set the error description
	 * @param description
	 */
	public void setTroubleDescription(final String description) {
		troubleDesc = description;
	}
	
	protected void connect(final ManagedDataType[] types, final boolean includeInitial) {
		m_Exiting = false;
		try {
			final Registry registry = LocateRegistry.getRegistry(m_Host, m_Port);
			m_AgentInterface = (AgentInterface) registry
					.lookup(m_AgentInterfaceName);
			m_Session = m_AgentInterface.connect(m_Name + "."
					+ DATEFORMATTER.format(new Date()), types, includeInitial);
			m_AlarmActionInterface = m_Session.getAlarmActionInterface();
		} catch (final RemoteException e) {
			setTroubleDescription("Connection lost to Service Network Manager.");
			error("Could not connect to registry", e);
			disconnect(ExitCode.REGISTRY_NOT_FOUND);
		} catch (final NotBoundException e) {
			error("Could not locate the AgentInterface in registry", e);
			disconnect(ExitCode.AGENTINTERFACE_NOT_FOUND);
		}
	}

	protected abstract void error(String error, Exception e);
}
