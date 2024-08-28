package com.ericsson.navigator.esm.agent.status;

import java.rmi.*;
import java.rmi.server.*;
import java.util.List;
import java.util.ArrayList;
import com.ericsson.navigator.esm.MVM;

public class AddServerImpl extends UnicastRemoteObject implements AddServerIntf{
	
	//private long startedAt = 0L;
	
	public AddServerImpl() throws RemoteException{
		//System.out.println("in the constructor");
		}
		
		
		
	public List<String> status() throws RemoteException{
		
		
		
		
		final List<String> al = new ArrayList<String>();
		//al.add("--- Mediator ---");
		final long now = System.currentTimeMillis();
		long up = now - MVM.getTime();
		final long days = up / (1000 * 60 * 60 * 24);
		up -= (days * (1000 * 60 * 60 * 24));
		final long hours = up / (1000 * 60 * 60);
		up -= (hours * ((1000 * 60 * 60)));
		final long minutes = up / (1000 * 60);
		al.add("Uptime: " + days + " days " + hours + " hours " + minutes + " minutes");
		al.add("");
		
//		al.add("Java VM");
//		final Runtime rt = Runtime.getRuntime();
//		al.add("  Available processors: " + rt.availableProcessors());
//		al.add("  Free Memory: " + rt.freeMemory());
//		al.add("  Total Memory: " + rt.totalMemory());
//		al.add("  Max Memory: " + rt.maxMemory());
//		al.add("");
		
		return al;
		
		
		
		}	
	}