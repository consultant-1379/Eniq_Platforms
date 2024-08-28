package com.ericsson.navigator.esm.model.pm.file.remote;

import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ericsson.navigator.esm.manager.pm.file.remote.plugin.CounterSetCallback;
import com.ericsson.navigator.esm.model.pm.CounterSetDefinition;
import com.ericsson.navigator.esm.model.pm.CounterSetDefinitionsController;
import com.ericsson.navigator.esm.model.pm.ManagedCounterSetList;
import com.ericsson.navigator.esm.model.pm.RegexpCounterSetIdentification;
import com.ericsson.navigator.esm.model.pm.file.FileCounterSet;

public class DefaultCounterSetCallback implements CounterSetCallback {

	private final String classname; 
	private final Logger logger;
	private final ManagedCounterSetList counterSetList;
	private final CounterSetDefinitionsController counterSetDefinitionsController;
	private final RemoteFileCounterSetScheduling counterSetSchedule;
	public static boolean parse=false;

	public DefaultCounterSetCallback(final ManagedCounterSetList counterSetList, 
			final RemoteFileCounterSetScheduling counterSetSchedule, final CounterSetDefinitionsController counterSetDefinitionsController, final String loggerName){
		this.counterSetList = counterSetList;
		this.counterSetDefinitionsController = counterSetDefinitionsController;
		this.counterSetSchedule = counterSetSchedule;
		this.logger = Logger.getLogger(loggerName);
		this.classname = loggerName;
	}
	
	@Override
	public void debug(final String message, final Exception e) {
		logger.debug(classname + message, e);
	}

	@Override
	public void debug(final String message) {
		logger.debug(classname + message);
	}

	@Override
	public void error(final String message, final Exception e) {
		logger.error(classname + message, e);
	}

	@Override
	public void error(final String message) {
		logger.error(classname + message);
	}

	@Override
	public void info(final String message, final Exception e) {
		logger.info(classname + message, e);
	}

	@Override
	public void info(final String message) {
		logger.info(classname + message);
	}

	@Override
	public void pushCounterSet(final String moid, final Date endTime, final Map<String, String> counters) {
		boolean moidMatch = false;
		String regExSysTopFile = "";
		for(final RegexpCounterSetIdentification identification : counterSetSchedule.getIdentifications()){
			regExSysTopFile = regExSysTopFile + identification.getRegularExpression() + ", ";
			if (moid.matches(identification.getRegularExpression())) {
				moidMatch = true;
				final CounterSetDefinition definition = counterSetDefinitionsController
						.getCounterSetDefinitionByFileName(identification.getFileName());
				if(definition != null) {
					final FileCounterSet counterSet = new FileCounterSet( //NOPMD
							moid, counterSetSchedule.getRop()*60, endTime, counters, definition);
					logger.debug(classname
							+ ".pushCounterSet(); Created CounterSet\n"
							+ counterSet);
					/*
					 * Below parse variable has been get and set in AbstractProtocolCounterSetList file 
					 */
					if(counterSet.isValid()) {
						counterSetList.correlate(counterSet);
						parse=true;
					} else {		//TR HM59414 updated debug statement
						debug(".pushCounterSet();" +
								" CounterSet for counterset id : " + counterSet.getCounterSetId() + 
								" with moid "+ counterSet.getMoid() +" not valid");

					}
				} else {
					//TR HM59414 updated debug statement
					logger.debug(classname
							+ ".pushCounterSet(); CounterSet definition not found for fileName " + identification.getFileName());
				}
			}
		}
	    if(!moidMatch)
		{
			logger.info(classname
					+ ".pushCounterSet(); Moid does not match with RegularExpression defined in SystemTopology.xml file . Expected regularexp= [ "+ regExSysTopFile+"] Received regularexp= [ "+moid +" ]");
		}
	}

	@Override
	public void warn(final String message, final Exception e) {
		logger.warn(classname + message, e);
	}

	@Override
	public void warn(final String message) {
		logger.warn(classname + message);
	}

}
