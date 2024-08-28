package com.ericsson.navigator.esm.model.alarm.snmp.generic.mst;

import java.io.File;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.adventnet.snmp.snmp2.SnmpPDU;
import com.ericsson.navigator.esm.util.Environment;
import com.ericsson.navigator.esm.util.component.Component;
import com.ericsson.navigator.esm.util.component.ComponentInitializationException;
import com.ericsson.navigator.esm.util.component.ComponentShutdownException;
import com.ericsson.navigator.esm.util.file.DirectoryListener;
import com.ericsson.navigator.esm.util.file.DirectoryMonitor;

/**
 * TrapTranslator that converts PDU:s into a internal alarm format.
 *  
 * The TrapTranslator contains a collection of translations. Translations
 * are stored for each type of system. 
 * 
 * The lookup for a translation of a PDU is made first by getting all translations
 * for a system type. Then each of those translations are iterated through and the
 * when a matching translation is found, that translation is then use to get an alarm.
 * 
 * @author qbacfre
 *
 */
public class TrapTranslator implements DirectoryListener, Component {

	private static Logger logger = Logger.getLogger(TrapTranslator.class.getName());
	
	private Hashtable<String, LinkedList<TrapTranslation>> translations;
	private Hashtable<String, LinkedList<TrapMap>> maps;
	private final DirectoryMonitor monitor;
	private final List<File> directories;
	
	/**
	 * Constructor for the translator.
	 */
	public TrapTranslator() {
		directories = new LinkedList<File>();
		monitor = new DirectoryMonitor(Environment.POLL_DELAY);
		monitor.addListener(this);
	}
	
	/**
	 * If a file is changed everything is reloaded.
	 * @param file
	 */
	public void directoryChanged(final File directory) {
		loadTranslations();
	}

	public synchronized void loadTranslations() {
		logger.debug(TrapTranslator.class.getName()+".loadTranslations(); -->");
		
		final Hashtable<String, LinkedList<TrapTranslation>> tmpTranslations = new Hashtable<String, LinkedList<TrapTranslation>>();
		final Hashtable<String, LinkedList<TrapMap>> tmpMaps = new Hashtable<String, LinkedList<TrapMap>>();
		
		TrapTranslationLoader.clearJMXLoadedFiles();
		for (final File dir : directories) {
			if (dir.isDirectory()) {
				TrapTranslationLoader.fromXML(dir, tmpTranslations, tmpMaps);
			} else {
				logger.error(TrapTranslator.class.getName()+".loadTranslations(); unable to load, one of the files is not a directory " + dir.getName());
			}
		}
		
		synchronized(this) {
			//swap translations and maps
			translations = tmpTranslations;
			maps = tmpMaps;
		}
		
		logger.debug(TrapTranslator.class.getName()+".loadTranslations(); <--");
	}
	
	/**
	 * Adds a file to the monitored directories.
	 */
	public void addDirectory(final File file) {
		if (file == null) {
			logger.error(TrapTranslator.class.getName()+".addDirectory(); trying to add a file that is null");
		} else {
			directories.add(file);
			monitor.addDirectory(file);
		}
	}
	
	/**
	 * This will translate the SNMP PDU into a internal alarm. 
	 * If a translation is found that matches the PDU this will
	 * translate the alarm into a MSTAlarm. IF no translation is
	 * found that matches the MSTAlarm null will be returned
	 * 
	 * @param pdu The PDU to translate.
	 * @return A MSTAlarm
	 */
	public synchronized MSTAlarm translateAlarm(final SnmpPDU pdu, final MSTAlarmList system) {
		final String systemType = system.getAddressInformation().getType();
		
		if (logger.isDebugEnabled()) {
			logger.debug(TrapTranslator.class.getName()+".translateAlarm(); system type "+systemType);
		}

		if (translations.containsKey(systemType)) {
			
			for (final TrapTranslation translation : translations.get(systemType)) {
				if (translation.match(pdu)) {
					return translation.translate(pdu, system, maps.get(systemType));
				}
			}
		} 
		
		if (logger.isDebugEnabled()) {
			logger.debug(TrapTranslator.class.getName()+".translateAlarm(); No translation found for pdu "+pdu.getTrapOID());
		}
		return null;
	}

	@Override
	public String getComponentName() {
		return TrapTranslator.class.getSimpleName();
	}

	@Override
	public void initialize() throws ComponentInitializationException {
		loadTranslations();
	}

	@Override
	public void shutdown() throws ComponentShutdownException {
		monitor.removeListener(this);
		monitor.stop();
	}
}
