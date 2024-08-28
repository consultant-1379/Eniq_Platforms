package com.ericsson.navigator.esm.model.pm;

import java.util.Date;

public class StateCounterSet extends AbstractCounterSet {

	private static final long serialVersionUID = 1L;

	public StateCounterSet(final CounterSet data, final State state, final String userId) {
		super(data);
		this.state = state;
		stateUser = userId;
		stateTime = new Date();
	}
	
	@Override
	public boolean isStateChange() {
		return true;
	}
}
