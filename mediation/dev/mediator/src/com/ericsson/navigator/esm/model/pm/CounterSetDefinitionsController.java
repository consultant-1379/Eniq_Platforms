package com.ericsson.navigator.esm.model.pm;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.ericsson.navigator.esm.util.component.Component;
import com.ericsson.navigator.esm.util.component.ComponentInitializationException;
import com.ericsson.navigator.esm.util.component.ComponentShutdownException;
import com.ericsson.navigator.esm.util.file.DirectoryListener;
import com.ericsson.navigator.esm.util.file.DirectoryMonitor;


public class CounterSetDefinitionsController implements DirectoryListener, Component {

	private final long pollDelay;
	
	public CounterSetDefinitionsController(final String dir, final long pollDelay) {
		handler = new CounterSetDefinitionHandler();
		this.pollDelay = pollDelay;
		definitionsDir = new File(dir);
		counterSetDefinitions = new HashMap<String, CounterSetDefinition>();
	}

	@Override
	public String getComponentName() {
		return CounterSetDefinitionsController.class.getSimpleName();
	}

	@Override
	public void shutdown() throws ComponentShutdownException {
		definitionDirMonitor.removeDirectory(definitionsDir);
		definitionDirMonitor.removeListener(this);
		definitionDirMonitor.stop();
	}
	
	public void initialize() throws ComponentInitializationException {
		loadDefinitionsDir(definitionsDir);
		definitionDirMonitor = new DirectoryMonitor(pollDelay);
		definitionDirMonitor.addDirectory(definitionsDir);
		definitionDirMonitor.addListener(this);
	}
	
	@Override
	public void directoryChanged(final File directory) {
		loadDefinitionsDir(definitionsDir);
	}
	
	public synchronized CounterSetDefinition getCounterSetDefinitionByFileName(final String fileStr) {
		return counterSetDefinitions.get(fileStr);
	}
	
	public synchronized CounterSetDefinition getCounterSetDefinitionByName(final String fileStr) {
		return counterSetDefinitions.get(fileStr+".xml");
	}
	
	public synchronized Collection<CounterSetDefinition> getCounterSetDefinitions(){
		return Collections.unmodifiableCollection(counterSetDefinitions.values());
	}
	
	
	/*PRIVATE*/
	private static final String classname = CounterSetDefinition.class.getName();
	private static final Logger logger = Logger.getLogger(classname);
	private final File definitionsDir;
	private final Map<String, CounterSetDefinition> counterSetDefinitions;
	final CounterSetDefinitionHandler handler;
	private DirectoryMonitor definitionDirMonitor;
	
	private synchronized void loadDefinitionsDir(final File definitionsDir) {
		if(definitionsDir.isDirectory()) {
			counterSetDefinitions.clear();
			final String[] fileList = definitionsDir.list();
			
			File definitionFile;
			for(String f: fileList) {
				definitionFile = new File(definitionsDir, f); //NOPMD
				if(definitionFile.isFile() && definitionFile.canRead()
						&& definitionFile.getName().endsWith(".xml")) {
					try {
						final CounterSetDefinition cSet = handler.getCounterSetDefinitionFromFile(definitionFile);
						if(cSet != null) {
							counterSetDefinitions.put(f, cSet);
						} else {
							logger.error("failed to retrieve complete counterSet from file " + f.toString());
						}
						
					} catch (IOException e) {
						logger.error("error while parsing counterset file" + 
								definitionFile.getAbsolutePath() + ": " + e.getMessage());
					} catch (SAXException e) {
						logger.error("SAX error while parsing counterset file " + 
								definitionFile.getAbsolutePath() + ": " + e.getMessage());
					}
				} else {
					logger.warn(f.toString() + " is not an xml file; skipping.");
				}
			}
		} else {
			logger.error("Not a valid definitions directory: " + definitionsDir.toString());
		}
	}
}
