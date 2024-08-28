package com.ericsson.sim.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.ArrayList;
import com.ericsson.sim.constants.SIMEnvironment;

public class SIMStatus extends UnicastRemoteObject implements StatusIntf {

	private static final long serialVersionUID = 1L;

	public SIMStatus() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	public List<String> status() throws RemoteException {

		final List<String> al = new ArrayList<String>();
		al.add("--- SIM ---");
		long up = (System.currentTimeMillis() - SIMEnvironment.STARTTIME) / 1000;
		final long days = up / (60 * 60 * 24);
		final long hours = (up - (days * (60 * 60 * 24))) / (60 * 60);
		final long minutes = (up - (hours * (60 * 60))) / 60;
		al.add("Uptime: " + days + " days " + hours + " hours " + minutes + " minutes");
		al.add("");
		
		String licenseState = System.getProperty("SIMLicense");
		if(licenseState.equals("valid")){
			al.add("Valid SIM license found");
		}else if(licenseState.equals("invalid")){
			al.add("No valid SIM license found. SIM is not operational");
		}
		
		al.add("");
		al.add("Java VM");
		final Runtime rt = Runtime.getRuntime();
		al.add("  Available processors: " + rt.availableProcessors());
		al.add("  Free Memory: " + rt.freeMemory() / 1000000.0 + " MB");
		al.add("  Total Memory: " + rt.totalMemory() / 1000000.0 + " MB");
		al.add("  Max Memory: " + rt.maxMemory() / 1000000.0 + " MB");
		al.add("");

		return al;

	}

}
