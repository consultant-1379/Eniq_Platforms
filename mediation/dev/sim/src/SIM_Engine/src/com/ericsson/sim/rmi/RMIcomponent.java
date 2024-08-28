package com.ericsson.sim.rmi;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


import com.ericsson.sim.engine.EngineComponent;

public class RMIcomponent implements EngineComponent{

	Logger log = LogManager.getLogger(this.getClass().getName());
	
	@Override
	public String getName() {
		return this.getClass().getName();
	}

	@Override
	public void startup() {
		try{
			final SIMStatus simStatus = new SIMStatus();
			try{				
				Naming.rebind("SIMAddServer", simStatus);
				log.debug("Server registered to already running RMI naming");
			}catch(Exception e){
				try{
					LocateRegistry.createRegistry(1099);
					Naming.bind("SIMAddServer", simStatus);
					log.debug("Server registered to started RMI naming");
				}catch(Exception exception){
					log.error("Unable to initialize LocateRegistry", exception);
				}
			}
	
		}catch(Exception e){
			log.error(e.getMessage());
		}
		
	}

	@Override
	public void exit() {
		try {
			Naming.unbind("SIMAddServer");
		} catch (RemoteException e) {
			log.error("RemoteException as SIM is not running");
		} catch (MalformedURLException e) {
			log.error("MalformedURL Exception: " + e.getMessage());
		} catch (NotBoundException e) {
			log.error("NotBoundException as SIM is not running");
		}
	}

}
