package com.ericsson.navigator.esm.model.conversion;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.ericsson.navigator.esm.model.ManagedDataType;
import com.ericsson.navigator.esm.util.component.Component;
import com.ericsson.navigator.esm.util.component.ComponentInitializationException;
import com.ericsson.navigator.esm.util.component.ComponentShutdownException;
import com.ericsson.navigator.esm.util.file.DirectoryListener;
import com.ericsson.navigator.esm.util.file.DirectoryMonitor;
import com.ericsson.navigator.esm.util.jmx.BeanServerHelper;

//import bsh.EvalError;
//import bsh.Interpreter;

public class ConversionController implements Component, DirectoryListener, ConversionControllerMBean {

//	private static final String classname = ConversionController.class.getName(); 
//	public static Logger logger = Logger.getLogger(classname);
//
//	private static final String BSH_PREFIX = ".bsh";
//	private final long pollDelay;
//	private final File pluginsDir;
//	private DirectoryMonitor pluginsDirMonitor;
//	private final Map<String, Conversion> plugins = new HashMap<String, Conversion>();

	public ConversionController(final String pluginsDir, final long pollDelay) {
//		this.pollDelay = pollDelay;
//		this.pluginsDir = new File(pluginsDir);
	}

	@Override
	public String getComponentName() {
//		return ConversionController.class.getSimpleName();
		return "";
	}

	@Override
	public void initialize() throws ComponentInitializationException {
//		pluginsDirMonitor = new DirectoryMonitor(pollDelay);
//		directoryChanged(pluginsDir);
//		pluginsDirMonitor.addListener(this);
//		pluginsDirMonitor.addDirectory(pluginsDir);
//		BeanServerHelper.registerMDynamicBean(this, ConversionControllerMBean.class);
	}

	@Override
	public void shutdown() throws ComponentShutdownException {
//		pluginsDirMonitor.removeListener(this);
//		pluginsDirMonitor.stop();
//		plugins.clear();
//		BeanServerHelper.unRegisterMBean(this);
	}

	@Override
	public void directoryChanged(final File directory) {
//		logger.debug(classname + ".directoryChanged() --> " + directory.getAbsolutePath());
//		if(directory.equals(pluginsDir)) {
//			reloadConversionPlugins(directory);
//		} else if(directory.getParentFile().equals(pluginsDir)) {
//			reloadConversionPlugins(directory.getParentFile());
//		}
//		logger.debug(classname + ".directoryChanged() <--");
	}

	@Override
	public int getConversionPluginsSize() {
//		return plugins.size();
		return -1;
	}

	@Override
	public Set<String> getConversionPlugins() {
//		return plugins.keySet();
		return null;
	}

	@Override
	public String getInstanceName() {
//		return classname;
		return null;
	}

	public Conversion getPlugin(final String id, final ManagedDataType type) {
//		return plugins.get(pluginsDir.getPath() + File.separator 
//				+ type.toString().toLowerCase() + File.separator + id);
		return null;
	}

	private void reloadConversionPlugins(final File baseDirectory) {
//		logger.debug(classname + ".reloadConversionPlugins() --> " + baseDirectory.getPath());
//		if(baseDirectory.equals(pluginsDir)) {
//			final long startTime = System.currentTimeMillis();
//			plugins.clear();
//			for(final File subDirectory : baseDirectory.listFiles(new DirectoryFilenameFilter())) { //NOPMD
//				for(final File pluginBean : subDirectory.listFiles(new BeanShellFilenameFilter())) { //NOPMD
//					final Interpreter interpreter = new Interpreter(); //NOPMD
//					try {
//						interpreter.source(pluginBean.getPath());
//						final Conversion conversionPlugin = (Conversion) interpreter.getInterface(Conversion.class);
//						final String key = pluginBean.getPath().substring(0, 
//								pluginBean.getPath().length() - BSH_PREFIX.length());
//						plugins.put(key, conversionPlugin);
//					} catch (FileNotFoundException e) {
//						logger.error(classname + ".reloadConversionPlugins() File "
//								+ pluginBean.getPath() + " not found.", e);
//					} catch (IOException e) {
//						logger.error(classname + ".reloadConversionPlugins() IOException when loading "
//								+ pluginBean.getPath(), e);
//					} catch (EvalError e) {
//						logger.error(classname + ".reloadConversionPlugins() Error evaluating file "
//								+ pluginBean.getPath(), e);
//					} catch(Exception e) {
//						logger.error(classname + ".reloadConversionPlugins() Exception loading file "
//								+ pluginBean.getPath(), e);
//					}
//				}
//				if(!pluginsDirMonitor.containsDirectory(subDirectory)) {
//					pluginsDirMonitor.addDirectory(subDirectory);
//				}
//			}
//			logger.debug(classname + ".reloadConversionPlugins() Loaded "
//					+ plugins.size() + " conversion plugin(s) from "
//					+ baseDirectory.getPath() + " in "
//					+ (System.currentTimeMillis() - startTime) + " ms");
//		}
//		logger.debug(classname + ".reloadConversionPlugins() <--");
	}

//	class DirectoryFilenameFilter implements FilenameFilter {
//		public boolean accept(final File dir, final String name) {
//			final File file = new File(dir.getPath() + File.separator + name);
//			if(file.isDirectory()) {
//				return true;
//			}
//			return false;
//		}
//	}

//	class BeanShellFilenameFilter implements FilenameFilter {
//		public boolean accept(final File dir, final String name) {
//			if(name.endsWith(BSH_PREFIX)) {
//				return true;
//			}
//			return false;
//		}
//	}
}
