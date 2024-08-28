package com.ericsson.navigator.esm.manager.pm.file.local.irp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ericsson.navigator.esm.MVMEnvironment;
import com.ericsson.navigator.esm.util.component.Component;
import com.ericsson.navigator.esm.util.component.ComponentInitializationException;
import com.ericsson.navigator.esm.util.component.ComponentShutdownException;
import com.ericsson.navigator.esm.util.file.DirectoryListener;
import com.ericsson.navigator.esm.util.file.DirectoryMonitor;
import com.ericsson.navigator.esm.util.jmx.BeanServerHelper;
import com.ericsson.navigator.esm.util.rate.Measures;

public class CounterSetFileLoader implements Component, DirectoryListener, CounterSetFileLoaderMBean {

	//private final String northBoundDirectory = MVMEnvironment.NB_DIRECTORY;
	private String northBoundDirectory;
	
	private static final String classname = CounterSetFileLoader.class.getName();
	public static Logger logger = Logger.getLogger(classname);

	private long totalReceivedCounterSets = 0;
	private long totalParsedFiles = 0;
	private final File inputDir;
	private final File archiveDir;
	private final File corruptedDir;
	private final long pollDelay;
	private final Map<String, CounterSetFileListener> counterSetFileListeners;
	private final Map<CounterSetFileParser.TYPE, CounterSetFileParser> counterSetFileParsers;
	private DirectoryMonitor counterSetDirMonitor;

	private final Measures measures;
	private final static int SECONDS = 10;
	private static boolean successfullParsing = true;

	public CounterSetFileLoader(final long pollDelay, final String input, final String archive, final String corrupted) {
		this.inputDir = new File(input);
		this.archiveDir = new File(archive);
		this.corruptedDir = new File(corrupted);
		this.pollDelay = pollDelay;
		this.counterSetFileListeners = Collections.synchronizedMap(new HashMap<String, CounterSetFileListener>());
		this.measures = new Measures(SECONDS);
		this.counterSetFileParsers = new HashMap<CounterSetFileParser.TYPE, CounterSetFileParser>();
	}

	@Override
	public String getComponentName() {
		return CounterSetFileLoader.class.getSimpleName();
	}

	public void setSuccessfullparsing(final boolean parse) {
		CounterSetFileLoader.successfullParsing = parse;
	}

	public boolean getSuccessfullparsing() {
				return CounterSetFileLoader.successfullParsing;
	}

	@Override
	public void initialize() throws ComponentInitializationException {
		totalReceivedCounterSets = 0;
		totalParsedFiles = 0;
		loadCounterSetFiles(inputDir);
		counterSetDirMonitor = new DirectoryMonitor(pollDelay);
		counterSetDirMonitor.addDirectory(inputDir);
		counterSetDirMonitor.addListener(this);
		BeanServerHelper.registerMDynamicBean(this, CounterSetFileLoaderMBean.class);
	}

	@Override
	public void shutdown() throws ComponentShutdownException {
		counterSetDirMonitor.removeListener(this);
		counterSetDirMonitor.removeDirectory(inputDir);
		counterSetDirMonitor.stop();
		BeanServerHelper.unRegisterMBean(this);
	}

	@Override
	public void directoryChanged(final File directory) {
		loadCounterSetFiles(inputDir);
	}

	private void copyFileToNorthBoundDirectory(final File file){
	    try {
	    	logger.debug(classname + ".copyFileToNorthBoundDirectory(); copying file "
					+ file.getAbsolutePath() + " to north bound directory: "
					+ northBoundDirectory + "  : " + System.currentTimeMillis());
	    	
	    	final FileChannel srcChannel = new FileInputStream(file).getChannel();
	    	final FileChannel destChannel = new FileOutputStream(northBoundDirectory + File.separator + file.getName()).getChannel();
	    	
	    	destChannel.transferFrom(srcChannel, 0, srcChannel.size());
	    	srcChannel.close();
	    	destChannel.close();
	    	
	    	logger.debug(classname + ".copyFileToNorthBoundDirectory(); completed copying file "
					+ file.getAbsolutePath() + " to north bound directory: "
					+ northBoundDirectory + "  : " + System.currentTimeMillis());
	    }catch(IOException ioe) {
	    	logger.error(classname + ".copyFileToNorthBoundDirectory(); Failed to copy file: "
					+ file.getAbsolutePath()
					+ " to north bound directory: "
					+ northBoundDirectory);
	    }
	}
	
	private void loadCounterSetFiles(final File directory) {
		logger.debug(classname + ".loadCounterSetFiles() -->");
		if (directory.isDirectory()) {
			for (final CounterSetFileParser parser : counterSetFileParsers.values()) {
				for (final File file : directory.listFiles(parser.getFilenameFilter())) {

					logger.debug(classname + ".loadCounterSetFiles(); Parsing " + parser.getType() + " file "
					        + file.getPath());
					try {
						parser.parse(file.getPath());
						totalParsedFiles++;
						measures.addValue(System.currentTimeMillis(), 1);
					} catch (final Exception e) {
						successfullParsing = false;
						logger.error(classname + ".loadCounterSetFiles(); Parsing " + parser.getType() + " file "
						        + file.getPath() + " failed " + e);
					} finally {
						if (getSuccessfullparsing()) {
							copyFileToNorthBoundDirectory(file);
							moveFile(archiveDir, file);
						} else {
							logger.warn(classname + "Invalid input file moving " + file + " to " + corruptedDir);
							moveFile(corruptedDir, file);
						}
					}
				}
			}
		} else {
			logger.error(classname + ".loadCounterSetFiles(); " + directory.getPath() + " is not a valid directory");
		}
	}

	private void moveFile(final File destinationDir, final File file) {
		final File destination = new File(destinationDir.getPath() + File.separator + file.getName());
		if (!file.renameTo(destination)) {
			logger.warn(classname + ".moveFile(); Unable to move file " + file.getPath() + " to "
			        + destination.getPath());
		}
		if (file.exists() && !file.delete()) {
			logger.warn(classname + ".moveFile(); Unable to delete file " + file.getPath() + " after parsing");
		}
	}

	public void addFileParser(final CounterSetFileParser parser) {
		logger.debug(classname + ".addFileParser(); Adding parser " + parser.getType());
		counterSetFileParsers.put(parser.getType(), parser);
	}

	public void removeFileParser(final CounterSetFileParser parser) {
		logger.debug(classname + ".removeFileParser(); Removing parser " + parser.getType());
		counterSetFileParsers.remove(parser.getType());
	}

	public void addFileListener(final CounterSetFileListener listener) {
		if (!listener.getFDN().isEmpty()) {
			logger.debug(classname + ".addFileListener(); Adding listener " + listener.getFDN());
			counterSetFileListeners.put(listener.getFDN(), listener);
		}
	}

	public void removeFileListener(final CounterSetFileListener listener) {
		logger.debug(classname + ".removeFileListener(); Removing listener " + listener.getFDN());
		counterSetFileListeners.remove(listener.getFDN());
	}

	public boolean validListener(final String nedn) {
		if (getListener(nedn) != null) {
			return true;
		}
		return false;
	}

	private CounterSetFileListener getListener(final String nedn) {
		for (final String keyFdn : counterSetFileListeners.keySet()) {
			final String[] splitFdn = keyFdn.split("=");
			final String cimName = splitFdn[splitFdn.length - 1];
			if (!nedn.isEmpty() && (nedn.equals(keyFdn) || nedn.endsWith(cimName))) {
				return counterSetFileListeners.get(keyFdn);
			} else if (nedn.isEmpty()) {
				return counterSetFileListeners.get("PM=EMPTY_NEDN");
			}
		}
		return null;
	}

	public void addCounterSet(final String nedn, final String moid, final int gp, final Date endDate,
	        final Map<String, String> observedCounters) {
		final CounterSetFileListener listener = getListener(nedn);
		if (listener != null) {
			logger.debug(classname + ".addCounterSet(); Sending counters to " + listener.getFDN());
			listener.receivedCounterSet(nedn, moid, gp, endDate, observedCounters);
			totalReceivedCounterSets++;
		}
	}

	@Override
	public int getTotalParsers() {
		return counterSetFileParsers.size();
	}

	@Override
	public int getTotalListeners() {
		return counterSetFileListeners.size();
	}

	@Override
	public String getListeners() {
		final StringBuffer listeners = new StringBuffer();
		int commaCorrection = 0;
		for (final String fdn : counterSetFileListeners.keySet()) {
			listeners.append(fdn);
			commaCorrection++;
			if (commaCorrection < counterSetFileListeners.keySet().size()) {
				listeners.append(", ");
			}
		}
		return listeners.toString();
	}

	@Override
	public long getTotalReceivedCounterSets() {
		return totalReceivedCounterSets;
	}

	@Override
	public void resetTotalReceivedCounterSets() {
		totalReceivedCounterSets = 0;
	}

	@Override
	public long getTotalParsedFiles() {
		return totalParsedFiles;
	}

	@Override
	public void resetTotalParsedFiles() {
		totalParsedFiles = 0;
	}

	@Override
	public float getParsingFileRate() {
		measures.getLastSecond(System.currentTimeMillis());
		final float sum = measures.getSumOfAll();
		if (sum == 0) {
			return 0;
		}
		return (sum / SECONDS);
	}

	@Override
	public String getInstanceName() {
		return classname;
	}
}
