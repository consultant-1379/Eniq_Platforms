package com.ericsson.navigator.esm.util.file;

import java.io.File;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import com.ericsson.navigator.esm.MVM;
import com.ericsson.navigator.esm.MVMEnvironment;

public class DirectoryHouseKeeping extends Thread{
	
	private File directory;
	private static final String classname = DirectoryHouseKeeping.class.getName();
	
	public DirectoryHouseKeeping(File directory){
		this.directory = directory;
	}
	
	public void run(){
		MVM.logger.debug(classname+".run() -->");
		ArrayList<String> removedFiles = new ArrayList<String>();
		Calendar fileCal = Calendar.getInstance();
		Calendar currentTime = Calendar.getInstance();
		int oldDate = currentTime.get(Calendar.HOUR_OF_DAY)- MVMEnvironment.HOUSEKEEPINGDIRLIMIT;
		currentTime.set(Calendar.HOUR_OF_DAY, oldDate);
	
		if(directory.exists() && directory.list().length > 0){
			for(File file : directory.listFiles()){
				long lastmodified = file.lastModified();
				fileCal.setTimeInMillis(lastmodified);
				MVM.logger.debug(classname+".run() "+file.getAbsolutePath() +" corrupt date: " + fileCal.getTime() + ", deletion date: "+currentTime.getTime());
				if(fileCal.before(currentTime)){
					removedFiles.add(file.getAbsolutePath());
					file.delete();
				}
			}
		}
		if(removedFiles.size()>0){
			MVM.logger.info(classname+".run() Successfully deleted files: " + removedFiles);
		}
		
		MVM.logger.debug(classname+".run() <--");
	}


}
