package com.ericsson.navigator.esm.model.alarm;

import java.util.Date;

public class StateAlarm extends AbstractAlarm {

	private static final long serialVersionUID = 1L;
	
	public StateAlarm(final Alarm alarm, final State state, final String userId) {
		super(alarm);
		this.state = state;
		this.stateUser = userId;
		this.stateTime = new Date();
	}
	
	@Override
	public boolean isStateChange() {
		return true;
	}
}
