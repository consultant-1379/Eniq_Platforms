package com.ericsson.navigator.esm.model.pm;

import com.ericsson.navigator.esm.model.pm.Counter.CounterType;

public class CounterDefinition {
	
	private final String id;
	private final String name;
	private final CounterType type;
	
	public CounterDefinition(final String id, final String name, final CounterType type) {
		this.id = id;
		this.name = name;
		this.type = type;
	}
	
	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public CounterType getType() {
		return type;
	}
}
