package com.ericsson.navigator.esm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.ericsson.navigator.esm.util.Environment;
import com.ericsson.navigator.esm.util.component.ComponentInitializationException;
import com.ericsson.navigator.esm.util.component.ComponentShutdownException;
import com.ericsson.navigator.esm.util.file.FileListener;
import com.ericsson.navigator.esm.util.file.FileMonitor;


public class MemUsageListener implements FileListener{
	
	private static final String fileLocation = "/eniq/mediator/runtime/memUsage.txt";
	private final FileMonitor fileMonitor;	
	private static MemUsageListener mul;
	private static int instance;
	
	private MemUsageListener(){
		fileMonitor = new FileMonitor(Environment.POLL_DELAY);
		this.initialize();
		mul = this;
	}
	
	public void checkEngineStatus(boolean setup){
		final File f = new File(fileLocation);
		if(f.exists() && setup){
			try{
				BufferedReader input =  new BufferedReader(new FileReader(f));
				String status = input.readLine();
				status = status.substring(status.indexOf("=")+1, status.length());
				
				if(!status.endsWith("G")){
					System.setProperty("MemUsageListener","true");
				}else{
					float usedMem = Float.valueOf(status.substring(0, status.length() - 1));
					float maxMem = ((MVMEnvironment.STORAGESPACE*85)/100);
					if(usedMem > maxMem){
						System.setProperty("MemUsageListener","false");
					}else{
						System.setProperty("MemUsageListener","true");
					}
				}
				
			}catch(IOException ex){
				MVM.logger.error("Error reading file");
				System.setProperty("MemUsageListener","false");
			}
		}
		else
			System.setProperty("MemUsageListener","false");
		
	}
	
	public void fileChanged(final File file) {
		String oldState = System.getProperty("MemUsageListener");
		checkEngineStatus(true);
		if(System.getProperty("MemUsageListener").equalsIgnoreCase("false")){
			MVM.logger.info("MemUsageListener.fileChanged(): Memory usage limit reached, collection stopped! ");
		}else if(oldState.equalsIgnoreCase("false") && System.getProperty("MemUsageListener").equalsIgnoreCase("true")){
			MVM.logger.info("MemUsageListener.fileChanged(): Memory usage cleared, collection allowed");
		}
		
	}
	
	public void initialize(){
		boolean setup = true;
		try{
			fileMonitor.addFile(new File(fileLocation));
			fileMonitor.addListener(this);
		}catch(Exception e){
			MVM.logger.error("Error reading file");
			setup = false;
		}
		checkEngineStatus(setup);
	}
	
	public void shutdown(){
		try {
			MVM.logger.info("MemUsageListener.shutdown(); Shutdown component.");
			fileMonitor.removeListener(this);
			fileMonitor.stop();
		} catch (Exception e) {
			MVM.logger.error("MemUsageListener.shutdown(); Failed to shutdown component. ", e);
		}
	}
	
	public static MemUsageListener getInstance(){
		if(instance ==0){
			instance = 1;
			return new MemUsageListener();
		}
		if(instance == 1){
			return mul;
		}
		
		return null;
	}
	
}
