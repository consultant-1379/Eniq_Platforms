package com.distocraft.dc5000.common;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * Class to create and get rmireistry on provided hostname and port
 * @author eanguan
 *
 */
public class ENIQRMIRegistryManager {
	private final static Logger log = Logger.getLogger("com.distocraft.dc5000.common.ENIQRMIRegistryManager");
	
	private String _hostName ;
	private int _portNumber ;
	
	/**
	 * Constructor
	 * @param host - host name on which rmi operation needs to be carried out
	 * @param port - port number on which rmi operation needs to be carried out
	 */
	public ENIQRMIRegistryManager(final String host, final int port){
		_hostName = host ;
		_portNumber = port ;
		System.setProperty("sun.rmi.transport.tcp.responseTimeout", "30000");
	}
	
	/**
	 * 
	 * @return - returns registry object running on the given hostname and port number. If no registry was running then returns null
	 * @throws RemoteException
	 */
	public Registry getRegistry() throws RemoteException{
		log.info("Starts getting RMI Regsitry on host: " + _hostName + " on Port: " + _portNumber);
		Registry rmi = null ;
		try{
			rmi = LocateRegistry.getRegistry(_hostName, _portNumber);
			log.info("RMI Regsitry got successful on host: " + _hostName + " on Port: " + _portNumber);
		}catch(final RemoteException e){
			log.log(Level.WARNING, "failed to get RMI Regsitry on host: " + _hostName + " on Port: " + _portNumber, e);
			throw e ;
		}
		log.info("Stops getting RMI Regsitry on host: " + _hostName + " on Port: " + _portNumber);
		return rmi;
	}
	
	/**
	 * 
	 * @return - returns newly created registry object on the given hostname and port number. Returns null if creation of registry fails
	 * @throws RemoteException
	 */
	public Registry createNewRegistry() throws RemoteException{
		log.info("Starts creating RMI Regsitry on host: " + _hostName + " on Port: " + _portNumber);
		Registry rmi = null ;
		try{
			rmi = LocateRegistry.createRegistry(_portNumber);
			log.info("RMI Regsitry created successful on host: " + _hostName + " on Port: " + _portNumber);
		}catch(final RemoteException e){
			log.log(Level.WARNING, "RMI Regsitry creation failed on host: " + _hostName + " on Port: " + _portNumber, e);
			throw e ;
		}
		log.info("Stops creating RMI Regsitry on host: " + _hostName + " on Port: " + _portNumber);
		return rmi ;
	}	
}
