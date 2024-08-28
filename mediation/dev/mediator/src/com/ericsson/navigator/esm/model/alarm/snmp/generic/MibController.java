package com.ericsson.navigator.esm.model.alarm.snmp.generic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.adventnet.snmp.mibs.MibOperations;
import com.ericsson.navigator.esm.util.SupervisionException;
import com.ericsson.navigator.esm.util.component.Component;
import com.ericsson.navigator.esm.util.component.ComponentInitializationException;
import com.ericsson.navigator.esm.util.component.ComponentShutdownException;
import com.ericsson.navigator.esm.util.file.DirectoryListener;
import com.ericsson.navigator.esm.util.file.DirectoryMonitor;
import com.ericsson.navigator.esm.util.jmx.BeanServerHelper;

public class MibController implements DirectoryListener, MibControllerMBean, Component {
	
	private final String mibFilesDir;
	private final String userMibFilesDir;
	private MibOperations mibOperations;
	private DirectoryMonitor fileMonitor;
	private final Integer mibLoadingMutex;
	private static final Pattern mibFilePattern = Pattern.compile(".*/(.*[.]mib).*");
	private static final String classname = MibController.class.getName();
	private static Logger logger = Logger.getLogger(classname);
	private final List<String> m_LoadedFiles;
	private final long pollDelay;
	
	public MibController(final String mibFilesDir, final String userMibFilesDir, final long pollDelay) {
		this.mibFilesDir = mibFilesDir;
		this.userMibFilesDir = userMibFilesDir;
		mibLoadingMutex = new Integer(0);
		m_LoadedFiles = new Vector<String>();
		BeanServerHelper.registerMBean(this);
		this.pollDelay = pollDelay;
	}
	
	public void initialize() throws ComponentInitializationException {
		fileMonitor = new DirectoryMonitor(pollDelay);
		fileMonitor.addDirectory(new File(mibFilesDir));
		fileMonitor.addDirectory(new File(userMibFilesDir));
		fileMonitor.addListener(this);
		try {
			loadMibs();
		} catch (final SupervisionException e) {
			logger.fatal(classname+".initialize(); Failed to load ALL mib file(s)", e);
		}
	}

	public void shutdown() throws ComponentShutdownException {
		if(fileMonitor != null){
			fileMonitor.removeDirectory(new File(mibFilesDir));
			fileMonitor.removeDirectory(new File(userMibFilesDir));
			fileMonitor.removeListener(this);
			fileMonitor.stop();
		}
		if(mibOperations != null){
			mibOperations.unloadAllMibModules();
		}
	}
	
	public synchronized MibOperations getMibOperations() {
		return mibOperations;
	}
	
	public void directoryChanged(final File file) {
		try {
			loadMibs(); //reloads all mibs
		} catch (final Exception e) {
			logger.error(getClass().getName()+".fileChanged(); Failed to load changed mib file(s)", e);
		}
	}
	
	private void loadMibs(final String files) throws SupervisionException {
		long startTime=0;
		if (logger.isDebugEnabled()) {
			startTime=System.currentTimeMillis();
			logger.debug(classname+ ".loadMibs(); -->");
		}
		final MibOperations mibOps = new MibOperations();
		if(files.trim().length() == 0){
			return;
		}
		synchronized(mibLoadingMutex){
			try {
				mibOps.loadMibModules(files); // Not thread safe
				convert(files);
			} catch (final FileNotFoundException e){
				logger.error(classname+ ".loadMibs(); Failed to load ALL MIB files since " +
						"the following file is missing: ", e);
			} catch (final Exception e) {
				e.printStackTrace();
				logger.error(classname+ ".loadMibs();", e);
				loadMibs(removeFaultyFile(files, e));
			}
			synchronized(this) {
				//swap MIB operations
				mibOperations = mibOps;
			}
			if (logger.isDebugEnabled()) {
				logger.debug(classname+ ".loadMibs(); <-- "+
						(System.currentTimeMillis()-startTime));
			}
		}
	}

	private void convert(final String files) {
		final StringTokenizer t = new StringTokenizer(files, " ");
		m_LoadedFiles.clear();
		while(t.hasMoreTokens()){
			m_LoadedFiles.add(t.nextToken().replace("\"", ""));
		}
	}

	static String removeFaultyFile(final String files, final Exception e) throws SupervisionException {
		final Matcher matcher = mibFilePattern.matcher(e.getMessage());
		if(matcher.find() && matcher.groupCount() > 0){
			if (logger.isDebugEnabled()) {
				logger.debug(classname+ ".loadMibs(); remove faulty file from loading: " + matcher.group(1));
			}
			final String fileName = matcher.group(1);
			final int endIndex = files.indexOf(fileName)+fileName.length();
			if(endIndex != -1){
				final int startIndex = files.lastIndexOf('"', endIndex-1);
				if(startIndex != -1){
					final String beforeFiles = files.substring(0, startIndex);
					final String afterFiles = (endIndex < files.length() ? files.substring(endIndex+1) : "");
					return beforeFiles + afterFiles;
				}
			}
		}
		throw new SupervisionException("Could not remove faulty MIB file for parse error: ", e);
	}

	synchronized void loadMibs() throws SupervisionException {
		String files = getMibFiles(mibFilesDir);
		files = files + " " + getMibFiles(userMibFilesDir);
		loadMibs(files);
	}

	private String getMibFiles(final String dir) throws SupervisionException {
		final File mibFilesDir = new File(dir);
		if (!mibFilesDir.exists()) {
			throw new SupervisionException("The MIB file dir " + mibFilesDir
					+ " is missing.");
		}
		final String[] mibList = mibFilesDir.list(new FilenameFilter() {
			public boolean accept(final File dir, final String name) {
				return name.substring(name.indexOf('.') + 1).equals("mib")
						|| new File(dir, name).isDirectory();
			}
		});
		final StringBuffer b = new StringBuffer();
		for (int i = 0; i < mibList.length; i++) {
			final File file = new File(mibFilesDir.getPath(), mibList[i]);//NOPMD
			if (file.isFile()) {
				b.append("\"");
				b.append(mibFilesDir.getPath());
				b.append(File.separatorChar);
				b.append(mibList[i]);
				b.append("\"");
				if (i < mibList.length) {
					b.append(" ");
				}
			} else {
				b.append(getMibFiles(file.getAbsolutePath()));
			}
		}
		return b.toString();
	}

	public List<String> getLoadedMibFiles() {
		return m_LoadedFiles;
	}

	public String getInstanceName() {
		return "";
	}

	@Override
	public String getComponentName() {
		return MibController.class.getSimpleName();
	}
}
