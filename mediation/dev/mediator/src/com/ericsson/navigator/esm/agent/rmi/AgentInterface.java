package com.ericsson.navigator.esm.agent.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

import com.ericsson.navigator.esm.model.ManagedDataType;

public interface AgentInterface extends Remote{

	AgentSession connect(String id, ManagedDataType[] types, boolean includeInitial) throws RemoteException;
}
