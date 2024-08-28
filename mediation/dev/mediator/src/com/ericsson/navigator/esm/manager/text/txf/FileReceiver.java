package com.ericsson.navigator.esm.manager.text.txf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.ericsson.navigator.esm.model.alarm.Alarm;
import com.ericsson.navigator.esm.model.alarm.text.txf.TXFAlarm;
import com.ericsson.navigator.esm.util.component.Component;
import com.ericsson.navigator.esm.util.component.ComponentInitializationException;
import com.ericsson.navigator.esm.util.component.ComponentShutdownException;
import com.ericsson.navigator.esm.util.jmx.BeanServerHelper;
import com.ericsson.navigator.esm.util.rate.Measures;
import com.ericsson.navigator.esm.util.reference.WeakListenerList;

public class FileReceiver implements Component, FileReceiverMBean {
	
	private static final String classname = FileReceiver.class.getName();
	private static Logger logger = Logger.getLogger(classname);
	private static final Pattern alarmStartPattern = Pattern.compile("^%a");
	private static final Pattern alarmEndPattern = Pattern.compile("^%A");
	private static final Pattern heartbeatPattern = Pattern.compile("^#HB.*");
	
	private final Map<File, WeakListenerList<AlarmFileListener>> fileListeners;
	private final Map<File, FileTailer> fileTailers;
	private final String txfAlarmFilePath; 
	private final long allowedFileSize;
	private int totalEvents = 0;
	private final Measures measures;
	private final static int SECONDS = 5;
	
	
	public FileReceiver(final String txfAlarmFilePath, final long txfAllowedFileSize){
		fileListeners = Collections.synchronizedSortedMap(new TreeMap<File, WeakListenerList<AlarmFileListener>>());
		fileTailers = new TreeMap<File, FileTailer>();
		this.txfAlarmFilePath = txfAlarmFilePath;
		allowedFileSize = txfAllowedFileSize;
		measures = new Measures(SECONDS);
		BeanServerHelper.registerMBean(this);
	}
	
	private void fireReceivedAlarm(final File file, final Alarm alarm){
		if(logger.isDebugEnabled()){
			logger.debug(classname + ".fireReceivedAlarm(); --> ");
		}
		final WeakListenerList<AlarmFileListener> listeners = fileListeners.get(file);
		if(listeners == null){
			return;
		}
		synchronized(listeners){
			for(final AlarmFileListener listener : listeners){
				if(listener.getFDN().equals(alarm.getManagedObjectInstance()) || 
						alarm.getManagedObjectInstance().contains(listener.getFDN()+',')){
					listener.receivedAlarm(alarm);
				}
			}
		}
		if(logger.isDebugEnabled()){
			logger.debug(classname + ".fireReceivedAlarm(); <--");
		}
	}
	
	private void fireReceivedHearbeat(final File file, final String fdn){
		if(logger.isDebugEnabled()){
			logger.debug(classname + ".fireReceivedHearbeat(); --> ");
		}
		final WeakListenerList<AlarmFileListener> listeners = fileListeners.get(file);
		if(listeners == null){
			return;
		}
		synchronized(listeners){
			for(final AlarmFileListener listener : listeners){
				if(listener.getFDN().equals(fdn) || fdn.contains(listener.getFDN()+',')){
					listener.receivedHeartbeat();
				}
			}
		}
		if(logger.isDebugEnabled()){
			logger.debug(classname + ".fireReceivedHearbeat(); <-- ");
		}
	}

	public void addAlarmFileListener(final AlarmFileListener l, final String type) {
		logger.debug(classname + ".addAlarmFileListener(); -->");
		final File alarmFile = new File(txfAlarmFilePath, type);
		WeakListenerList<AlarmFileListener> listeners = fileListeners.get(alarmFile);
		if(listeners == null){
			try {
				createNewFile(alarmFile);
			} catch (final IOException e) {
				logger.error(classname + 
						".addAlarmFileListener(); IO error occured when creating file: " + 
						alarmFile.getAbsolutePath(), e);
				return;
			}
			listeners = new WeakListenerList<AlarmFileListener>();
			fileListeners.put(alarmFile, listeners);
			final FileTailer fileTailer = new FileTailer(alarmFile);
			fileTailers.put(alarmFile, fileTailer);
			fileTailer.start();
		}
		synchronized(listeners){
			listeners.addListener(l);
		}
		logger.debug(classname + ".addAlarmFileListener(); <--");
	}
	private boolean createNewFile(final File alarmFile) throws IOException {
		final boolean result = alarmFile.createNewFile();
		final String osName = System.getProperty("os.name");
		if(!osName.toLowerCase().contains("windows")){
			final Process process = Runtime.getRuntime().exec("chmod 666 " + alarmFile.getAbsolutePath());
			try {
				return result && process.waitFor() == 0;
			} catch (final InterruptedException e) {
				return false;
			}
		}
		return result;
	}

	public void removeAlarmFileListener(final AlarmFileListener l, final String type) {
		logger.debug(classname + ".removeAlarmFileListener(); -->");
		final File alarmFile = new File(txfAlarmFilePath, type);
		final WeakListenerList<AlarmFileListener> listeners = fileListeners.get(alarmFile);
		if(listeners != null){
			synchronized(listeners){
				listeners.removeListener(l);
			}
			if(listeners.isEmpty()){
				final FileTailer fileTailer = fileTailers.remove(alarmFile);
				fileTailer.interrupt();
				fileListeners.remove(alarmFile);
			}
		}
		logger.debug(classname + ".removeAlarmFileListener(); <--");
	}
	
	private class FileTailer extends Thread {
		
		private final File file;
		private StringBuffer alarmBuffer = null;
		
		public FileTailer(final File file){
			this.file = file;
		}
		
		@Override
		public void run() {
			tailFile();
		}
		
		private void tailFile(){
			logger.debug(classname + ".tailFile(); -->");
			RandomAccessFile reader = null;
			try {
				reader = new RandomAccessFile(file, "rw");
				String line = null;
				while(!Thread.currentThread().isInterrupted()){
					line = reader.readLine();
					if(line != null){
						parseLine(line, reader);
					} else if (file.length() >= allowedFileSize){
						truncateFile(reader);
					}else if (file.length()==0){
						reader.seek(0);
					}else {
							Thread.sleep(1000);
							tailFileHelper(reader); //HM79822 - ESM missing new alarms if the alarm reception file is edited/cleaned manually
					}
				}
			} catch (final FileNotFoundException e) {
				logger.error(classname + 
						".tailFile(); Could not find alarm file at path: " + file.getAbsolutePath());
			} catch (final IOException e) {
				logger.error(classname + 
						".tailFile(); IO Error occured when reading file at path: " + 
						file.getAbsolutePath(), e);
			} catch (final InterruptedException e) {
				logger.debug(classname + 
						".tailFile(); Tail of file interrupted for shutdown, file path: " + 
						file.getAbsolutePath(), e);
			} catch (final Exception e) {
				logger.debug(classname + 
						".tailFile(); Exception occured, file path: " + 
						file.getAbsolutePath(), e);
			} finally {
				cleanup(reader);
				logger.debug(classname + ".tailFile(); <--");
			}
		}
		
		
		private void tailFileHelper(final RandomAccessFile reader)throws IOException{
			final long currFilePointer = reader.getFilePointer(); 
			final long currLength = reader.length();
			if(currFilePointer > currLength){
				reader.seek(currFilePointer-currLength);
			}else{
				reader.seek(currFilePointer);
			}

		}

		private void cleanup(final RandomAccessFile reader) {
			try {
				truncateFile(reader);
				closeReader(file, reader);
			} catch (final IOException e) {
				logger.error(classname + 
						".cleanup(); IO Error occured when truncating file at path: " + 
						file.getAbsolutePath(), e);
			}
		}

		void truncateFile(final RandomAccessFile reader) throws IOException {
			logger.info(classname + 
					".truncateFile(); file : " + file.getAbsolutePath() + " size before: "+file.length()+" (Bytes)");
			reader.setLength(0);
			reader.seek(0);			
			final long length = file.length();
			logger.debug(classname + 
					".truncateFile(); file : " + file.getAbsolutePath() + " size after: "+length+" (Bytes)");
		}

		private void closeReader(final File file, final RandomAccessFile reader) {
			if(reader != null){
				try {
					reader.close();
				} catch (final IOException e) {
					logger.warn(classname + 
							".closeReader(); IO Error occured when closing file reader to file at path: " + 
							file.getAbsolutePath(), e);
				}
			}
		}
		
		private void parseLine(final String line, final RandomAccessFile reader) {
			final Matcher startMatcher = alarmStartPattern.matcher(line);
			final Matcher endMatcher = alarmEndPattern.matcher(line);
			final Matcher heartbeatMatcher = heartbeatPattern.matcher(line);
			try{
				if (heartbeatMatcher.matches()) {
					handleHearbeat(file, line);
				} else if (startMatcher.matches()) {
					alarmBuffer = new StringBuffer();
				} else if (endMatcher.matches() && alarmBuffer != null) {
					fireReceivedAlarm(file, new TXFAlarm(alarmBuffer.toString()));
					++totalEvents;
					measures.addValue(System.currentTimeMillis(), 1);
					alarmBuffer = null;
					 if (file.length() >= allowedFileSize) {
					 truncateFile(reader);
					 }
				} else if (alarmBuffer != null) {
					alarmBuffer.append(line);
					alarmBuffer.append('\n');
				}

			} catch (final FileNotFoundException e) {
				logger.error(classname + ".tailFile(); Could not find alarm file at path: " + file.getAbsolutePath());
			} catch (final Exception e) {
				logger.debug(classname + ".tailFile(); Exception occured, file path: " + file.getAbsolutePath(), e);
			}
		}
	}
	
	private void handleHearbeat(final File file, final String line){
		if(logger.isDebugEnabled()){
			logger.debug(classname + ".handleHearbeat(); --> ");
		}
		final int fdnStartIndex = line.indexOf(" ");
		if(fdnStartIndex == -1){
			logger.error(classname + 
					".handleHearbeat(); Could not resolve FDN from heartbeat message : " + line);
			return;
		}
		final String fdn = line.substring(fdnStartIndex).trim();
		fireReceivedHearbeat(file, fdn);
		if(logger.isDebugEnabled()){
			logger.debug(classname + ".handleHearbeat(); <-- ");
		}
	}

	public float getIncomingEventRate() {
		measures.getLastSecond(System.currentTimeMillis());
		final float sum = measures.getSumOfAll();
		if (sum == 0) {
			return 0;
		}
		return (sum / SECONDS);
	}

	public List<File> getMonitoredAlarmFiles() {
		return new ArrayList<File>(fileTailers.keySet());
	}

	public long getTotalTXFEventsReceived() {
		return totalEvents;
	}

	public void resetTXFEventsReceived() {
		totalEvents = 0;
	}

	public String getInstanceName() {
		return "";
	}

	@Override
	public String getComponentName() {
		return "TXF File Receiver";
	}

	@Override
	public void initialize() throws ComponentInitializationException {
	}

	@Override
	public void shutdown() throws ComponentShutdownException {
		for(final FileTailer tailer : fileTailers.values()){
			tailer.interrupt();
		}
		for(final FileTailer tailer : fileTailers.values()){
			try {
				tailer.join();
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
