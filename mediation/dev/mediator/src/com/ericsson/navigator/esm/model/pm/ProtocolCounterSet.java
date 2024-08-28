package com.ericsson.navigator.esm.model.pm;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ProtocolCounterSet extends AbstractCounterSet {

	private static final long serialVersionUID = 1L;
	
	public ProtocolCounterSet(final String fdn, final int gp, final String counterSetId) {
		this.granularityPeriod = gp;
		this.managedObjectInstance = fdn;
		this.counterSetId = counterSetId;
	}
		
	Map<String, Serializable> getProperties() {
		final Map<String, Serializable> properties = new HashMap<String, Serializable>();

		// expose only changeable fields
		properties.put("managedObjectInstance", managedObjectInstance);
		properties.put("moid", moid);
		properties.put("granularityPeriod", granularityPeriod);
		properties.put("lastModifiedTime", lastModifiedTime);
		properties.put("endTime", endTime);
		
		for (Counter counter: counters.values()) {
			properties.put(counter.getName().trim(), counter.getValue());
		}
		
		return properties;
	}
	
	void update(final Map<String, Serializable> updatedProperties, final CounterSetDefinitionsController csController) {
		final CounterSetDefinition csDefinition = csController.getCounterSetDefinitionByName(counterSetId);
		final Map<String, CounterDefinition> counterDefinitions = csDefinition.getDefinitions();

		for (Entry<String, Serializable> property: updatedProperties.entrySet()) {
			final String name = property.getKey();
			final Serializable value = property.getValue();
			
			if ("managedObjectInstance".equals(name)) {
				managedObjectInstance = (String) value;
			} else if ("moid".equals(name)) {
				moid = (String) value;
			} else if ("endTime".equals(name)) {
				endTime = (Date) value;
			} else if ("lastModifiedTime".equals(name)) {
				lastModifiedTime = (Date) value;
			} else if ("granularityPeriod".equals(name)) {
				granularityPeriod = (Integer) value;
			} else {
				final CounterDefinition counterDefinition = counterDefinitions.get(name);
				if (counterDefinition != null) {
					counters.put(name, new DefaultCounter(name, counterDefinition.getId(), (BigDecimal) value, counterDefinition.getType())); //NOPMD
				}
			}
		}	
	}
}
