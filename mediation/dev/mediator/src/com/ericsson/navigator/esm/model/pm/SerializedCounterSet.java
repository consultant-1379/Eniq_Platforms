package com.ericsson.navigator.esm.model.pm;

class SerializedCounterSet extends AbstractCounterSet {

	private static final long serialVersionUID = 1L;
	
	SerializedCounterSet(final CounterSet cs) {
		super(cs);
		counters.clear();
		for(Counter c : cs.getCounters().values()){
			counters.put(c.getName(), c.getSerializable());
		}
	}
}
