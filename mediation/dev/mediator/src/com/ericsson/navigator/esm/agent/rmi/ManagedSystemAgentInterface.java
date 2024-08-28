package com.ericsson.navigator.esm.agent.rmi;

import java.rmi.RemoteException;

import com.ericsson.navigator.esm.model.ManagedModel;
import com.ericsson.navigator.esm.model.Protocol;
import com.ericsson.navigator.esm.util.jmx.BeanServerHelper;

public class ManagedSystemAgentInterface extends DefaultAgentInterface 
	implements ManagedSystemAgentInterfaceMBean{

	private static final long serialVersionUID = 1L;

	public ManagedSystemAgentInterface(
			final ManagedModel<Protocol<?,?,?>> managedProtocolModel)
			throws RemoteException {
		super(managedProtocolModel);
		BeanServerHelper.registerMBean(this);
	}

	public int getSessionCount() {
		return m_Sessions.size();
	}

	public String getInstanceName() {
		return "";
	}

}
