package com.ericsson.navigator.esm.model.pm;

import com.ericsson.navigator.esm.model.AbstractManagedDataList;
import com.ericsson.navigator.esm.model.ManagedDataEvent;
import com.ericsson.navigator.esm.model.ManagedDataType;
import com.ericsson.navigator.esm.model.ManagedDataEvent.Action;
import com.ericsson.navigator.esm.model.pm.CounterSet.State;
import com.ericsson.navigator.esm.util.log.Log;

public class ManagedCounterSetList extends AbstractManagedDataList<CounterSet, CounterSet.State> {

	public ManagedCounterSetList(final String fdn, final Log<CounterSet> log) {
		super(fdn, log);
	}

	@Override
	protected ManagedDataEvent<CounterSet> createEvent(final CounterSet data,
			final Action action, final boolean isStateChange) {
		return new ManagedDataEvent<CounterSet>(data, action, isStateChange);
	}
	
	@Override
	protected void dataUpdated(final CounterSet data, final CounterSet oldData) {
		if (oldData.getState().equals(State.Deleted)) {
			items.remove(oldData.getUniqueId());
			firePushEvent(createEvent(oldData, ManagedDataEvent.Action.DELETE, true));
		} else {
			super.dataUpdated(data, oldData);
		}
	}

	@Override
	protected CounterSet createStateChange(final CounterSet data, final State state,
			final String userId) {
		return new StateCounterSet(data, state, userId);
	}

	@Override
	public ManagedDataType getType() {
		return ManagedDataType.CounterSet;
	}

}
