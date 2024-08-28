package com.ericsson.sim.monitor.eniq;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


import com.ericsson.sim.constants.SIMEnvironment;

public class EniqSystemStatusHandler {
	
	Logger log = LogManager.getLogger(this.getClass().getName());
	
	public void start(){
		File statusFile = new File(SIMEnvironment.CONFIGPATH, "enginestatus.txt");
		if(statusFile.exists()){
			try{
				BufferedReader input =  new BufferedReader(new FileReader(statusFile));
				
				String engineStatus = input.readLine();
				engineStatus = engineStatus.substring(engineStatus.indexOf("=")+1, engineStatus.length());
				System.setProperty("ENIQEngineStatus",engineStatus);
				
				String memoryStatus = input.readLine();
				memoryStatus = memoryStatus.substring(memoryStatus.indexOf("=")+1, memoryStatus.length());
				System.setProperty("ENIQMemoryStatus",memoryStatus);
				input.close();

			}catch(Exception e){
				log.error("Error reading ENIQ status file");
				System.setProperty("ENIQStatus","Disabled");
			}
		}
		
	}

}
