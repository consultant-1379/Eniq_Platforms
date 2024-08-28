package com.ericsson.navigator.esm.model.pm;

import java.math.BigDecimal;

public  abstract class AbstractCounter implements Counter {

	private static final long serialVersionUID = 1L;
	protected final String name;
	protected final String id;
	protected String valueStr = "";
	protected BigDecimal value;
	protected boolean isValid = true;
	protected final CounterType type;

	protected AbstractCounter(final String name, final String id, final BigDecimal value,
			final CounterType type) {
		this.name = name;
		this.id = id;
		this.value = value;
		this.type = type;
		this.valueStr = value.toString();
	}
	
	protected AbstractCounter(final String name, final String id, final String value,
			final CounterType type) {
		this.name = name;
		this.id = id;
		this.valueStr = value;
		this.type = type;
		try{
			this.value = new BigDecimal(value);
		}catch(NumberFormatException exception){
			this.value = new BigDecimal("-1");
		}
	}
	
	protected AbstractCounter(final Counter c) {
		this.name = c.getName();
		this.id = c.getProtocolIdentifier();
		this.value = c.getValue();
		this.type = c.getType();
		this.isValid = c.isValid();
		this.valueStr = c.getValue().toString();
	}

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public Counter getSerializable() {
		return new SerializedCounter(this);
	}

	@Override
	public boolean isValid() {
		return isValid;
	}

	@Override
	public void reset() {}

	@Override
	public CounterType getType() {
		return type;
	}

	@Override
	public BigDecimal getValue() {
		return value;
	}
	
	public String getValueStr() {
		return valueStr;
	}

	@Override
	public void update(final Counter c) {
		value = c.getValue();
	}

	@Override
	public String getProtocolIdentifier() {
		return id;
	}

	@Override
	public String getDataType() {
		// TODO Auto-generated method stub
		return null;
	}
}
