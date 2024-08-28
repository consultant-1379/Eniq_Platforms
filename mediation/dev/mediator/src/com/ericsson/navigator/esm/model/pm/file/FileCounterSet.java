package com.ericsson.navigator.esm.model.pm.file;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ericsson.navigator.esm.MVM;
import com.ericsson.navigator.esm.model.pm.Counter;
import com.ericsson.navigator.esm.model.pm.CounterDefinition;
import com.ericsson.navigator.esm.model.pm.CounterSetDefinition;
import com.ericsson.navigator.esm.model.pm.DefaultCounter;
import com.ericsson.navigator.esm.model.pm.ProtocolCounterSet;
import com.ericsson.navigator.esm.model.pm.Counter.CounterType;
import com.ericsson.navigator.esm.util.Hash;

public class FileCounterSet extends ProtocolCounterSet {
	
	private static final String classname = FileCounterSet.class.getName();
	public static Logger logger = Logger.getLogger(classname);
	
	private static final long serialVersionUID = 1L;
	private static final String SEPERATOR = ">";

	public FileCounterSet(final String fdn, final int gp, final Date endTime, 
			final Map<String, String> observedCounters, final CounterSetDefinition setDefinition) {
		super(fdn, gp, setDefinition.getCounterSetId());
		moid = setDefinition.getMoidString(fdn);
		this.endTime = endTime;
		for(final String counterName : observedCounters.keySet()){
			for(final CounterDefinition definition : setDefinition.getDefinitions().values()) {
				if(counterName.equals(definition.getId())) {
					try{
						final BigDecimal value = new BigDecimal(observedCounters.get(counterName)); //NOPMD
						Counter counter = null;
						if(definition.getType().equals(CounterType.PEG)){
							counter = new DefaultCounter(definition.getName(), definition.getId(), value, CounterType.PEG); //NOPMD
						} else if(definition.getType().equals(CounterType.KPI)){
							counter = new DefaultCounter(definition.getName(), definition.getId(), value, CounterType.KPI); //NOPMD
						} else if(definition.getType().equals(CounterType.INDEX)){
							counter = new DefaultCounter(definition.getName(), definition.getId(), value, CounterType.INDEX); //NOPMD
						}
						else {
							counter = new DefaultCounter(definition.getName(), definition.getId(), value, CounterType.GAUGE); //NOPMD
						}
						counters.put(definition.getName(), counter);
					} catch(NumberFormatException e){
						MVM.logger.error(classname + 
								".XMLCounterSet(); Counter " + definition.getName() + 
								" could not be parsed into a number for counter set definition " + 
								setDefinition.getFileName());
					}
				}
			}
		}
		uniqueId = Hash.fnv_64_1a(fdn + SEPERATOR + setDefinition.getFileName() + SEPERATOR + moid);
	}
	
	public boolean isValid() {
		if(counters.size() == 0) {
			return false;
		}
		return true;
	}
}
