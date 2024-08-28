package com.ericsson.navigator.esm.agent.rmi;

import java.io.Serializable;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.ericsson.navigator.esm.model.ManagedDataType;
import com.ericsson.navigator.esm.model.ManagedModel;

public class DefaultAgentInterface extends UnicastRemoteObject implements
		AgentInterface, Serializable {

	private static final long serialVersionUID = 1L;
	protected final transient List<DefaultAgentSession> m_Sessions;
	private final transient ManagedModel<?> managedProtocolModel;
	private static final String classname = DefaultAgentInterface.class.getName();
	private static Logger m_Logger = Logger.getLogger(classname);
	
	public DefaultAgentInterface(final ManagedModel<?> managedProtocolModel)
			throws RemoteException {
		super();
		m_Sessions = Collections.synchronizedList(new Vector<DefaultAgentSession>());
		this.managedProtocolModel = managedProtocolModel;
	}

	public AgentSession connect(final String id, final ManagedDataType[] types, final boolean includeInitial) throws RemoteException {
		final DefaultAgentSession session = new DefaultAgentSession(id, this, types, includeInitial);
		m_Sessions.add(session);
		final Thread initializeThread = new Thread("Initalize Thread: " + id){
			@Override
			public void run() {
				session.initialize();
			}
		};
		initializeThread.start();
		return session;
	}
	
	public void remove(final DefaultAgentSession session) {
		if (m_Sessions.contains(session)) {
			m_Logger.info(classname+
					".remove(); Removing session for agent with ID: "
					+ session.getId());
			try {
				// Removing all references to object
				UnicastRemoteObject.unexportObject(session, true);
				if(!m_Sessions.remove(session)){
					m_Logger.error(classname+
							".remove(); Could not remove session for agent with ID: "
							+ session.getId());
				}
			} catch (final NoSuchObjectException e) {
				m_Logger.error(classname+
						".remove(); Could not unexport session for agent with ID: "
						+ session.getId(), e);
			}
		}
	}

	public void removeAll() {
		m_Logger.info(classname+".removeAll(); -->" );
		try {
			UnicastRemoteObject.unexportObject(this, true);		
			 // Copy sessions, list is updated during iteration 
			final List<DefaultAgentSession> copySessions = 
				new Vector<DefaultAgentSession>(m_Sessions);
			for (DefaultAgentSession session: copySessions) {
				session.unreferenced();
			}
		} catch (final NoSuchObjectException e) {
			m_Logger.error(classname+
				".removeAll(); Could not unexport this instance", e);
			
		}
		m_Logger.info(classname+".removeAll(); <--" );
	}

	public ManagedModel<?> getManagedModel() {
		return managedProtocolModel;
	}
}
