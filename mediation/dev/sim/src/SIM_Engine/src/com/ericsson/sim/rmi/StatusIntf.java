package com.ericsson.sim.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface StatusIntf extends Remote{
	public List<String> status() throws RemoteException, Exception;
	}
