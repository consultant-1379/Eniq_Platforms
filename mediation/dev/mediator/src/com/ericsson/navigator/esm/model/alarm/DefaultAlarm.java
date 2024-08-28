package com.ericsson.navigator.esm.model.alarm;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public abstract class DefaultAlarm extends AbstractAlarm { //NOPMD
	
	private static final long serialVersionUID = 1L;
	
	public DefaultAlarm() {
		super();
	}

	public DefaultAlarm(final Alarm alarm) {
		super(alarm);
	}
	
	Map<String, Serializable> getProperties() {
		final Map<String, Serializable> properties = new HashMap<String, Serializable>();

		// expose only changeable fields (all but uniqueId)
		properties.put("managedObjectInstance", managedObjectInstance);
		properties.put("m_AdditionalText", m_AdditionalText);
		properties.put("m_AlarmId", m_AlarmId);
		properties.put("m_EventTime", m_EventTime);
		properties.put("m_EventType", m_EventType);
		properties.put("m_ProbableCause", m_ProbableCause);
		properties.put("m_SpecificProblem", m_SpecificProblem);
		properties.put("m_RecordType", m_RecordType.toString());
		properties.put("m_PerceivedSeverity", m_PerceivedSeverity.toString());
		properties.put("stateUser", stateUser);
		properties.put("stateTime", stateTime);
		properties.put("state", this.getState().toString());
		
		return properties;
	}
	
	void update(final Map<String, Serializable> updatedProperties) { //NOPMD
		for (Entry<String, Serializable> property: updatedProperties.entrySet()) {
			final String name = property.getKey();
			final Serializable value = property.getValue();
			
			if ("managedObjectInstance".equals(name)) {
				managedObjectInstance = (String) value;
			} else if ("m_AlarmId".equals(name)) {
				m_AlarmId = (String) value;				
			} else if ("m_EventTime".equals(name)) {
				m_EventTime = (Date) value;				
			} else if ("m_EventType".equals(name)) {
				m_EventType = (String) value;				
			} else if ("m_ProbableCause".equals(name)) {
				m_ProbableCause = (String) value;				
			} else if ("m_SpecificProblem".equals(name)) {
				m_SpecificProblem = (String) value;				
			} else if ("m_RecordType".equals(name)) {
				m_RecordType = RecordType.valueOf((String) value);
			} else if ("m_PerceivedSeverity".equals(name)) {
				m_PerceivedSeverity = PerceivedSeverity.valueOf((String) value);
			} else if ("stateUser".equals(name)) {
				stateUser = (String) value;				
			} else if ("stateTime".equals(name)) {
				stateTime = (Date) value;				
			} else if ("state".equals(name)) {
				state = State.valueOf((String) value);				
			} else if ("m_AdditionalText".equals(name)) {
				m_AdditionalText += (String) value;				
			} else {
				m_AdditionalText += "\n" + name + "=" + value;
			}			
		}	
	}

}
