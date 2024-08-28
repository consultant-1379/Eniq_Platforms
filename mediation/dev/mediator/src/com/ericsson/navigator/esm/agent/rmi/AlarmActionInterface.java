package com.ericsson.navigator.esm.agent.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import com.ericsson.navigator.esm.util.SupervisionException;

public interface AlarmActionInterface extends Remote {

	/**
	 * Order a synchronize of the alarm list against the supervised managed
	 * systems.
	 * 
	 * @param fdns A list of managed systems to synchronize.
	 * @throws RemoteException If a communication error occurred.
	 * @throws SupervisionException If an internal error occurred in the ESM server.
	 */
	void synchronize(List<String> fdns) throws RemoteException, SupervisionException;
	
	/**
	 * Order a synchronize of the alarm list against all the supervised managed
	 * systems.
	 * 
	 * @throws RemoteException If a communication error occurred.
	 * @throws SupervisionException If an internal error occurred in the ESM server.
	 */
	void synchronizeAll() throws RemoteException, SupervisionException;
	
	/**
	 * Delete the alarms from the ESM alarm cache.
	 * 
	 * @param uniqueIds The IDs of the alarms to perform the operation on.
	 * @param userId The user performing the operation.
	 * @throws RemoteException If a communication error occurred.
	 * @throws SupervisionException If an internal error occurred in the ESM server.
	 */
	void deleteAlarms(List<Long> uniqueIds, String userId) throws RemoteException, SupervisionException;
	
	/**
	 * Acknowledge the alarms from the ESM alarm cache.
	 * 
	 * @param uniqueIds The IDs of the alarms to perform the operation on.
	 * @param userId The user performing the operation.
	 * @throws RemoteException If a communication error occurred.
	 * @throws SupervisionException If an internal error occurred in the ESM server.
	 */
	void acknowledgeAlarms(List<Long> uniqueIds, String userId) throws RemoteException, SupervisionException;
	
	/**
	 * Regret the acknowledge the alarms from the ESM alarm cache.
	 * 
	 * @param uniqueIds The IDs of the alarms to perform the operation on.
	 * @param userId The user performing the operation.
	 * @throws RemoteException If a communication error occurred.
	 * @throws SupervisionException If an internal error occurred in the ESM server.
	 */
	void unAcknowledgeAlarms(List<Long> uniqueIds, String userId) throws RemoteException, SupervisionException;
}
