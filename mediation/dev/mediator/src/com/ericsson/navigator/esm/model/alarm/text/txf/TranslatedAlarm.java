package com.ericsson.navigator.esm.model.alarm.text.txf;

import com.ericsson.navigator.esm.model.alarm.DefaultAlarm;
import com.ericsson.navigator.esm.model.alarm.Alarm;

class TranslatedAlarm extends DefaultAlarm { //NOPMD
	private static final long serialVersionUID = 1L;

	TranslatedAlarm(final Alarm alarm) {
		super(alarm);
	}
	
	public void setProposedRepairAction(final String value) {
		m_AdditionalText += "ProposedRepairAction: " + value + "\n";
	}
	
	public void addTranslatedIntToAdditionalText(final String name, final String value) {
		m_AdditionalText += "-"+ name +"=" + value + "\n";
	}

	public void setProbableCause(final String value) {
		m_ProbableCause = value;
	}

	public void renewUniqueId() {
		generateUniqueId();
	}

	public void setSpecificProblem(final String value){
		m_SpecificProblem = value;
	}
	
	public void setEventType(final String value){
		m_EventType = value;
	}
	
	boolean isValid() { //NOPMD		
		if (getManagedObjectInstance() != null 
				&& ! getManagedObjectInstance().isEmpty()) {
			if (getAlarmId() != null 
				&& ! getAlarmId().isEmpty()) {
				return true;
			}
			
			if (getEventType() != null 
				&& ! getEventType().isEmpty() 
				&& getProbableCause() != null 
				&& ! getProbableCause().isEmpty()
				&& getSpecificProblem() != null 
				&& ! getSpecificProblem().isEmpty()) {
				return true;
			}
		}
		return false;
	}		

}
