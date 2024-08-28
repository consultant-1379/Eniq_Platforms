package com.ericsson.navigator.esm.model.pm;

import java.util.Date;
import java.util.Map;

import com.ericsson.navigator.esm.model.ManagedData;

public interface CounterSet extends ManagedData<CounterSet.State, CounterSet>{

	enum State {Active, NodeRestarted, RopDataMissing, Deleted}

	String getFdn();
	String getType();
	String getCounterSetId();
	Map<String, Counter> getCounters();
	String getMoid();
	Date getEndTime();
	int getGranularityPeriod();
	Date getLastModifiedTime();
}
