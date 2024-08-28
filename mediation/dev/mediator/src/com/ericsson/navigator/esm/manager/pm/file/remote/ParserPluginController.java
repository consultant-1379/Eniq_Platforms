package com.ericsson.navigator.esm.manager.pm.file.remote;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.ericsson.navigator.esm.manager.pm.file.remote.plugin.CounterSetFileParser;
import com.ericsson.navigator.esm.util.component.Component;
import com.ericsson.navigator.esm.util.component.ComponentInitializationException;
import com.ericsson.navigator.esm.util.component.ComponentShutdownException;
import com.ericsson.navigator.esm.util.file.DirectoryListener;
import com.ericsson.navigator.esm.util.file.DirectoryMonitor;
import com.ericsson.navigator.esm.util.jmx.BeanServerHelper;


public class ParserPluginController implements Component, DirectoryListener, ParserPluginControllerMBean {

	private static final String classname = ParserPluginController.class.getName(); 
	public static Logger logger = Logger.getLogger(classname);

	private final long pollDelay;
	private final File parserPluginBaseDir;
	private final Map<String,Properties> pluginProperties;
	private final String pluginPropertiesFile;
	private DirectoryMonitor parserPluginDirMonitor;
	private HashSet<String> changedList = new HashSet<String>();

	public ParserPluginController(final String parserPluginBaseDir, final String pluginPropertiesFile, final long pollDelay) {
		this.pollDelay = pollDelay;
		this.parserPluginBaseDir = new File(parserPluginBaseDir);
		pluginProperties = new HashMap<String,Properties>();
		this.pluginPropertiesFile = pluginPropertiesFile;
	}

	@Override
	public String getComponentName() {
		return ParserPluginController.class.getSimpleName();
	}

	@Override
	public void initialize() throws ComponentInitializationException {
		parserPluginDirMonitor = new DirectoryMonitor(pollDelay);
		directoryChanged(parserPluginBaseDir);
		parserPluginDirMonitor.addListener(this);
		parserPluginDirMonitor.addDirectory(parserPluginBaseDir);
		BeanServerHelper.registerMDynamicBean(this, ParserPluginControllerMBean.class);
	}

	@Override
	public void shutdown() throws ComponentShutdownException {
		parserPluginDirMonitor.removeListener(this);
		parserPluginDirMonitor.stop();
		pluginProperties.clear();
		BeanServerHelper.unRegisterMBean(this);
	}

	@Override
	public void directoryChanged(final File directory) {
		logger.debug(classname + ".directoryChanged() --> " + directory.getAbsolutePath());
		
		String oldRemoteDir = "";
		if(directory.getParentFile().equals(parserPluginBaseDir)) {			
			oldRemoteDir = pluginProperties.get(directory.getName()).getProperty("remoteDirectory", "");
		}
		
		if(directory.equals(parserPluginBaseDir)) {
			reloadAllProperties(directory);
		} else if(directory.getParentFile().equals(parserPluginBaseDir)) {
			reloadAllProperties(directory.getParentFile());
		}
		
		if(directory.getParentFile().equals(parserPluginBaseDir)) {
			String newRemoteDir = getPluginProperties(directory.getName()).getProperty("remoteDirectory", "");
			
			if(!oldRemoteDir.equals("") && !newRemoteDir.equals("")){
				if(!oldRemoteDir.equals(newRemoteDir)){
					logger.debug(classname + ".reloadAllProperties() remoteDirectory has been changed!");
					changedList.add(directory.getName());
				}
			}
		}
		
		
		logger.debug(classname + ".directoryChanged() <--");
	}

	private void reloadAllProperties(final File baseDirectory) {
		logger.debug(classname + ".reloadAllProperties() -->");		
		if(baseDirectory.equals(parserPluginBaseDir)) {
			
			pluginProperties.clear();
			for(final File subDirectory : baseDirectory.listFiles(new DirectoryFilenameFilter())) { //NOPMD
				for(final File config : subDirectory.listFiles(new ConfigFilenameFilter())) { //NOPMD
					loadProperties(config);
				}
				if(!parserPluginDirMonitor.containsDirectory(subDirectory)){
					parserPluginDirMonitor.addDirectory(subDirectory);
				}
			}
		}
		logger.debug(classname + ".reloadAllProperties() <--");
	}

	private void loadProperties(final File file) {
		FileInputStream fileInputStream;
		try {
			fileInputStream = new FileInputStream(file);
			final Properties prop = new Properties();
			prop.loadFromXML(fileInputStream);			
			pluginProperties.put(file.getParentFile().getName(), prop);
		} catch (final FileNotFoundException e) {
			logger.error(classname + ".loadProperies(); Properties File not found: " 
					+ file.getPath(), e);
		} catch (final InvalidPropertiesFormatException e) {
			logger.error(classname + ".loadProperies(); Invalid Property format: " 
					+ file.getPath(), e);
		} catch (final IOException e) {
			logger.error(classname + ".loadProperies(); Error reading properties file: " 
					+ file.getPath(), e);
		}
	}

	public Properties getPluginProperties(final String dirName) {		
		return pluginProperties.get(dirName);
	}

	public boolean verify(final File file) {

		boolean check = false;

		check = parserPluginDirMonitor.containsDirectory(file);
		return check;
	}

	public void removedir(final File dir) {
		if (parserPluginDirMonitor.containsDirectory(dir)) {

			parserPluginDirMonitor.removeDirectory(dir);
		}

	}

	public CounterSetFileParser getParserPluginInstance(final String pluginDirectory, final String className){
		logger.debug(classname + ".getParserPluginInstance() -->");

		final File parserPluginDir = new File(parserPluginBaseDir.getPath() 
				+ File.separator + pluginDirectory);
		CounterSetFileParser counterSetFileParser = null;
		try {
			final List<URL> urlClassPathList = new ArrayList<URL>();
			
			final File libDir = new File(parserPluginDir.getPath() + File.separator + "lib" + File.separator);
			if(libDir.isDirectory()) {
				for(final File jar : libDir.listFiles(new JarFilenameFilter())) { //NOPMD
					urlClassPathList.add(jar.toURI().toURL());
				}
			}
			
			final URL[] urlClassPath = urlClassPathList.toArray(new URL[0]);
			final URLClassLoader pluginClassLoader = 
				new URLClassLoader(urlClassPath, getClass().getClassLoader());
			final Class<? extends CounterSetFileParser> parserClass = 
				Class.forName(className, true, pluginClassLoader).asSubclass(CounterSetFileParser.class);
			counterSetFileParser = parserClass.newInstance();
			logger.debug(classname + ".getParserPluginInstance(); Loaded Plugin Parser " 
					+ counterSetFileParser.getClass().getName());
		} catch (final ClassNotFoundException e) {
			logger.error(classname + ".getParserPluginInstance(); Class " 
					+ className +" not found in specified class path: " 
					+ parserPluginDir.getPath(), e);
		} catch (final InstantiationException e) {
			logger.error(classname + ".getParserPluginInstance(); Cannot instantiate class " 
					+ className , e);
		} catch (final IllegalAccessException e) {
			logger.error(classname + ".getParserPluginInstance(); " 
					+ "Accessing method does not have access to specified method of class " 
					+ className , e);
		} catch (final MalformedURLException e) {
			logger.error(classname + ".getParserPluginInstance(); " 
					+ "Could not create URL from specified directory URI " 
					+ parserPluginDir.getPath() + File.separator , e);
		}
		logger.debug(classname + ".getParserPluginInstance() <--");
		return counterSetFileParser;
	}

	class DirectoryFilenameFilter implements FilenameFilter {
		 public boolean accept(final File dir, final String name) {
			 final File file = new File(dir.getPath() + File.separator + name);
			 if(file.isDirectory()) {
				 return true;
			 }
			 return false;
		 }
	}

	class ConfigFilenameFilter implements FilenameFilter {
		 public boolean accept(final File dir, final String name) {
			 if(name.equals(pluginPropertiesFile)) {
				 return true;
			 }
			 return false;
		 }
	}

	class JarFilenameFilter implements FilenameFilter {
		 public boolean accept(final File dir, final String name) {
			 if(name.endsWith(".jar")) {
				 return true;
			 }
			 return false;
		 }
	}

	@Override
	public String getInstanceName() {
		return classname;
	}

	@Override
	public int getTotalConfigFiles() {
		return pluginProperties.size();
	}

	@Override
	public String getConfigFiles() {
		final StringBuffer configFiles = new StringBuffer();
		int commaCorrection = 0;
		for(final String plugInDir : pluginProperties.keySet()) {
			configFiles.append("Properties for " + plugInDir);
			final Properties props = pluginProperties.get(plugInDir);
			configFiles.append(", Port: " + props.getProperty("port", "22"));
			configFiles.append(", User: " + props.getProperty("user", "ossuser"));
			configFiles.append(", MainClass: " + props.getProperty("mainClass", 
					"com.ericsson.navigator.esm.manager.pm.file.remote.plugin.irpxml.IrpXMLCounterSetParser"));
			configFiles.append(", RemoteDirectory: " + props.getProperty("remoteDirectory", ""));
			configFiles.append(", NorthBoundDirectory: " + props.getProperty("northBoundDirectory", ""));
			configFiles.append(", RemoteSubDirectory: " + props.getProperty("remoteSubDirectory", ""));
			configFiles.append(", FileNamePattern: " + props.getProperty("fileNamePattern", ""));
			configFiles.append(", FilePerDay: " + props.getProperty("filePerDay", "false"));
			configFiles.append(", MaxFileTransfersPerRop: " + props.getProperty("MaxFileTransfersPerRop","10" ));
			commaCorrection++;
			if(commaCorrection < pluginProperties.keySet().size()) {
				configFiles.append("; ");
			}
			
		}
		return configFiles.toString();
	}

	public boolean checkList(String pluginName){
		boolean contains = changedList.contains(pluginName);
		if(contains){
			changedList.remove(pluginName);
		}
		return contains;
	}

	
}
