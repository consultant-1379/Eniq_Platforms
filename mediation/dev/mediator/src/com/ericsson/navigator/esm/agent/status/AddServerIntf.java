package com.ericsson.navigator.esm.agent.status;

import java.rmi.*;
import java.util.List;

public interface AddServerIntf extends Remote{
	public List<String> status() throws RemoteException, Exception;
	}