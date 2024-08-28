package com.ericsson.navigator.esm.model.pm;

import java.math.BigDecimal;

public class DefaultCounter extends AbstractCounter {

	private static final long serialVersionUID = 1L;

	public DefaultCounter(final String name, final String id, final BigDecimal value, final CounterType type) {
		super(name, id, value, type);
	}
	
	public DefaultCounter(final String name, final String id, final String value, final CounterType type) {
		super(name, id, value, type);
	}
}
