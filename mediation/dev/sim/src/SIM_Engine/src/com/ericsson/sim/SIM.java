package com.ericsson.sim;

import java.io.File;
import java.lang.Thread.UncaughtExceptionHandler;
import java.rmi.Naming;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.ericsson.eniq.licensing.cache.DefaultLicenseDescriptor;
import com.ericsson.eniq.licensing.cache.LicenseDescriptor;
import com.ericsson.eniq.licensing.cache.LicensingCache;
import com.ericsson.eniq.licensing.cache.LicensingResponse;
import com.ericsson.sim.constants.SIMEnvironment;
import com.ericsson.sim.model.properties.RuntimeProperties;
import com.ericsson.sim.monitor.eniq.EniqSystemStatusHandler;
import com.ericsson.sim.rmi.RMIcomponent;

public class SIM {

	private final static String SIM_STARTER_LICENSE = "CXC4012008";
	Logger log = LogManager.getLogger(this.getClass().getName());
	private SIMEngineStarter manager = null;
	
	protected void startup(){
		try{
			//Initialise the logging
			initLogging();
			
			log.info("SIM startup initiated");
			SIMEnvironment.STARTTIME = System.currentTimeMillis();
			
			//Load runtime properties
			RuntimeProperties.getInstance().loadPersistedFile(SIMEnvironment.RUNPROPSCONFIG);
			
			log.debug("Initializing RMI Connection");
			final RMIcomponent rmi = new RMIcomponent();
			rmi.startup();
			
			if(validateLicense() && checkSystemStatus()){ //Validate license before starting
				log.info("SIM license validated. SIM Engine starting");
				
				//Initialize and start the SIM Engine
				manager = new SIMEngineStarter();
				final Thread mainThread = new Thread(new Runnable() {
					public void run() {	
						manager.init(rmi);
					}
				}, "Main Thread");
				mainThread.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
					public void uncaughtException(final Thread t, final Throwable e) {
						log.error("Uncaught error detected in main thread!", e);
					}
				});
				mainThread.start();
				
			}else{
				log.error("Starup checks did not pass. SIM will not start");
			}
		
		}catch(Exception e){
			log.error("Unable to start SIM. Exception caught: " + e.getMessage());
		}catch(ExceptionInInitializerError e){
			log.error("Unable to start SIM. Exception caught: " + e.getMessage());
		}
	}
	
	protected boolean validateLicense(){
		boolean validLicense = false;
		try{
			String serverHostName = RuntimeProperties.getInstance().getProperty("LicenseSentinel");
			if(serverHostName == null){
				serverHostName = "127.0.0.1";
			}

			do{
				log.debug("Validating SIM license towards " + serverHostName);
				
				LicensingCache cache = (LicensingCache) Naming.lookup("rmi://" + serverHostName + ":1200/LicensingCache");
				
				LicenseDescriptor license = new DefaultLicenseDescriptor(SIM_STARTER_LICENSE);
				LicensingResponse response = cache.checkLicense(license);
				if (response.isValid()) {
					validLicense = true;
					System.setProperty("SIMLicense", "valid");
			    }else{
			    	validLicense = false;
			    	System.setProperty("SIMLicense", "invalid");
			    	log.error("No valid SIM license found. SIM is not operational");
			    	Thread.sleep(86400000);
			    	//Thread.sleep(60000);
			    }
			
			}while(!validLicense);
			
			
		}catch(Exception e){
			log.error("Unable to validate SIM license, Exception caught: " + e.getMessage());
		}
		
		return validLicense;
	}
	
	protected boolean checkSystemStatus(){
		//Get the ENIQ Engine status
		EniqSystemStatusHandler essh = new EniqSystemStatusHandler();
		essh.start();
		String engStatus = System.getProperty("ENIQEngineStatus", "Failed");
		String memStatus = System.getProperty("ENIQMemoryStatus", "Failed");
		if(!engStatus.equalsIgnoreCase("online")){
			log.warn("ENIQ Engine not online. SIM will not start!");
			return false;
		}
		else if(memStatus.equalsIgnoreCase("MEMORYFULL")){
			log.warn("Memory usage on disk is >= 80%. SIM will not start!");
			return false;
		}
		
		return true;
	}
	
	protected void initLogging(){
		File propertyFile = new File(SIMEnvironment.LOGGERCONFIG);
		if(propertyFile.exists()){
			PropertyConfigurator.configureAndWatch(propertyFile.getAbsolutePath(), 10000);
		}else{
			log.setLevel(Level.OFF);
		}
	}
	
	public Logger getLog() {
		return log;
	}

	public void setLog(Logger log) {
		this.log = log;
	}
	
	
	public static void main(String[] args){
		SIM sim = new SIM();
		sim.startup();		
	}
	
}
