package com.ericsson.sim.rmi;

import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


public class RMIConnection {
	
	Logger log = LogManager.getLogger(this.getClass().getName());
	
	private void getStatus(){
		String addServerURL = "rmi://localhost/SIMAddServer";
		
		/*try{
			StatusIntf addServerIntf = (StatusIntf)Naming.lookup(addServerURL);	
		}catch(Exception ce){
			try{
				final SIMStatus addServerImpl = new SIMStatus();
				log.debug("RMI server being re-established");
				LocateRegistry.createRegistry(1099);
				Naming.bind("SIMAddServer", addServerImpl);
				log.debug("Server registered to started RMI naming");
			}catch(Exception exception){
				log.error("Unable to initialize LocateRegistry");
			}
		}*/
		
		try{
			StatusIntf statusIntf = (StatusIntf)Naming.lookup(addServerURL);
			final List<String> al = (List<String>) statusIntf.status();
			//AddServerImpl addServerImpl = new AddServerImpl();
						
			@SuppressWarnings({ "unchecked", "rawtypes" })
			final Iterator i = ((java.util.List<String>) al).iterator();
			while (i.hasNext()) {
				final String t = (String) i.next();
				System.out.println(t);
			}
			
		}catch(ConnectException ce){
			System.err.println("RMI server not running" + ce);
		}catch(NotBoundException ne){
			System.err.println("NotBoundException as SIM is not running"+ ne);
		}catch(RemoteException re){
			System.err.println("RemoteException as SIM is not running"+ re);
		}catch(Exception e){
			System.err.println("Unable to start RMI as SIM is not running"+ e);
		}
	}
	
	public static void main(String[] args){
		RMIConnection rmi = new RMIConnection();
		rmi.getStatus();
	}
}