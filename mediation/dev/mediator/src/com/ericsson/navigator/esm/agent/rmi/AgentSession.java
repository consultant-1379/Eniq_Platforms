package com.ericsson.navigator.esm.agent.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import com.ericsson.navigator.esm.model.ManagedDataEvent;
import com.ericsson.navigator.esm.util.SupervisionException;

public interface AgentSession extends Remote {
	
	/**
	 * Poll the alarm queue in the session for the next batch of alarms.
	 * 
	 * @return The next batch of alarms or an empty list if the retrieval 
	 * timed out.
	 * @throws RemoteException If an RMI communication error occurred.
	 */
	List<ManagedDataEvent<?>> getEvents() throws RemoteException;
	
	/**
	 * Reinitialize the session for all managed systems in the alarm cache. 
	 * Their alarm will be resent by the ESM server process just as when a 
	 * new session is launched.
	 *  
	 * @throws RemoteException If a communication error occurred.
	 */
	void reinitialize() throws RemoteException;
	
	/**
	 * Reinitialize the session for some managed systems in the alarm cache. 
	 * Their alarm will be resent by the ESM server process just as when a 
	 * new session is launched.
	 *  
	 * @throws RemoteException If a communication error occurred.
	 * @throws SupervisionException If no managed system with the given fdn could be found.
	 */
	void reinitialize(List<String> fdns) throws RemoteException, SupervisionException;
	
	/**
	 * Retrieves the alarm action interface.
	 *  
	 * @throws RemoteException If a communication error occurred.
	 */
	AlarmActionInterface getAlarmActionInterface() throws RemoteException;
	
}
