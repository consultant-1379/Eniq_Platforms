package com.ericsson.navigator.esm.agent.status;

import java.io.BufferedReader;
import java.io.FileReader;
import java.rmi.*;
import java.text.SimpleDateFormat;
import java.util.*;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.ericsson.navigator.esm.MVM;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class AddClient{
	
	public static void main (String args[]){
		
		//Fix for TR HR88496
		/*
		try{
			String addServerURL = "rmi://localhost/AddServer";
			
			AddServerIntf addServerIntf = (AddServerIntf)Naming.lookup(addServerURL);
			final List al = addServerIntf.status();
			
			//AddServerImpl addServerImpl = new AddServerImpl();
						
			final Iterator i = al.iterator();
			while (i.hasNext()) {
			final String t = (String) i.next();
			System.out.println(t);
			}
			
		}catch(ConnectException ce){
			System.out.println("RMI server disabled");
			//e.printStackTrace();
		}catch(NotBoundException ne){
			System.out.println("NotBoundException as Mediator is not running");
		}catch(RemoteException re){
			System.out.println("RemoteException as Mediator is not running");
		}catch(Exception e){
			System.out.println("Unable to start RMI as Mediator is not running");
		}
		*/
		// Updated code for HR88496
		try {
			BufferedReader reader = new BufferedReader(new FileReader("/eniq/log/sw_log/mediator/mediator_started.txt"));
			String line = null;
			String line1 = null;
							
			while ((line = reader.readLine()) != null) {
				line1 = line.substring(line.indexOf("=")+1, line.length());
				//System.out.println("Test : " + line1);
			}
			
			final long now = System.currentTimeMillis();
			
			SimpleDateFormat sdf = new SimpleDateFormat("MM.dd.yyyy HH.mm.ss");
			Date date = sdf.parse(line1);
			long up = date.getTime();
							
			up = now - up;
			final long days = up / (1000 * 60 * 60 * 24);
			up -= (days * (1000 * 60 * 60 * 24));
			final long hours = up / (1000 * 60 * 60);
			up -= (hours * ((1000 * 60 * 60)));
			final long minutes = up / (1000 * 60);
			
			System.out.println("Uptime: " + days + " days " + hours + " hours " + minutes + " minutes");
		}catch(Exception e){
			System.out.println("Exception Occured : " + e);
		}
	}
}