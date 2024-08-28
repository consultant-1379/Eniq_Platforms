package com.ericsson.navigator.esm.util.log.zip;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.ericsson.navigator.esm.model.alarm.Alarm;
import com.ericsson.navigator.esm.util.component.Component;
import com.ericsson.navigator.esm.util.component.ComponentInitializationException;
import com.ericsson.navigator.esm.util.component.ComponentShutdownException;
import com.ericsson.navigator.esm.util.csv.CSVPrinter;
import com.ericsson.navigator.esm.util.log.Log;
import com.ericsson.navigator.esm.util.log.LogException;

public class CyclicAlarmZipLog implements Log<Alarm>, Component {

	private Logger logger = null;
	private final String loggerConfigFile;
	
	public CyclicAlarmZipLog(final String configFile){
		loggerConfigFile = configFile;
	}

	public void initialize() throws ComponentInitializationException {
		logger = LogManager.getLogger("com.ericsson.navigator.esm.alarmlog");
		final File f = new File(loggerConfigFile);
	    if (f.exists()) {
			PropertyConfigurator.configure(loggerConfigFile); //no need for timer!?
	    } else {
	    	logger.setLevel(Level.OFF);
	    }
	}
	
	public synchronized void log(final Alarm alarm) throws LogException {
		final StringBuffer b = new StringBuffer();
		CSVPrinter.print(alarm, b);
		logger.info(b.toString());
	}

	public void shutdown() throws ComponentShutdownException {
		LogManager.shutdown();
	}

	@Override
	public String getComponentName() {
		return CyclicAlarmZipLog.class.getSimpleName();
	}
	
}
