package com.ericsson.navigator.esm.model.alarm.text.txf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.ericsson.navigator.esm.util.component.Component;
import com.ericsson.navigator.esm.util.component.ComponentInitializationException;
import com.ericsson.navigator.esm.util.component.ComponentShutdownException;
import com.ericsson.navigator.esm.util.jmx.BeanServerHelper;

public class TXFTranslator implements TXFTranslatorMBean, Component {
	
	private static final String classname = TXFTranslator.class.getName();
	private static Logger logger = Logger.getLogger(classname);
	
	private final String txfTranslateMapPath;
	private final Map<String, TranslateMap> translateMaps;
	private final long pollDelay;

	public TXFTranslator(final String path, final long pollDelay){
		translateMaps = new TreeMap<String, TranslateMap>();
		txfTranslateMapPath = path;
		this.pollDelay = pollDelay;
		BeanServerHelper.registerMBean(this);
	}

	public synchronized TranslateMap load(final String type) {
		TranslateMap map = translateMaps.get(type);
		if(map == null){
			final File file = new File(txfTranslateMapPath, type);
			map = new TranslateMap(file, pollDelay);
			translateMaps.put(type, map);
			try {
				map.load();
			} catch (FileNotFoundException e) {
				logger.info(classname + 
						".load(); TXF Translation file not found: " + 
						file.getAbsolutePath() + ", if TXF integration for type " + type + " use integers values" +
								" that require mapping no mapping will be performed.");
			} catch (IOException e) {
				logger.error(classname + 
						".load(); IO Error when loading TXF translate map: " + 
						file.getAbsolutePath(), e);
			}
			map.initialize();
		}
		return map;
	}

	public List<String> getLoadedTranslationMaps() {
		final List<String> files = new ArrayList<String>();
		for(TranslateMap map : translateMaps.values()){
			files.add(map.toString());
		}
		return files;
	}

	public String getInstanceName() {
		return "";
	}

	@Override
	public String getComponentName() {
		return TXFTranslator.class.getSimpleName();
	}

	@Override
	public void initialize() throws ComponentInitializationException {}

	@Override
	public void shutdown() throws ComponentShutdownException {
		for(TranslateMap map : translateMaps.values()){
			map.cancel();
		}
	}
}
