package com.ericsson.navigator.esm.model.pm;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

public class DeltaPegCounter extends AbstractCounter {

	private static final long serialVersionUID = 1L;
	
	private BigDecimal referenceValue;
	private final BigDecimal maxValue;
	private static final String classname = DeltaPegCounter.class.getName();
	private static final Logger logger = Logger.getLogger(classname);

	public DeltaPegCounter(final String name, final String id, 
			final BigDecimal value, final BigDecimal maxValue) {
		super(name, id, "", CounterType.PEG);
		referenceValue = value;
		this.maxValue = maxValue;
		isValid = false;
	}
	
	@Override
	public void reset() {
		isValid = false;
		referenceValue = null;
		value = null;
	}
	
	@Override
	public boolean isValid() {
		return isValid;
	}
	
	@Override
	public void update(final Counter c) {
		final DeltaPegCounter peg = (DeltaPegCounter)c;
		if(hasReferenceValue()){
			if(rolloverDetected(peg)){
				logger.debug(classname + 
						".update(); Peg Counter " + name + " rollover detected.");
				value = maxValue.subtract(referenceValue).add(peg.referenceValue);
			} else {
				value = peg.referenceValue.subtract(referenceValue);
			}
			isValid = true;
		}
		referenceValue = peg.referenceValue;
	}

	private boolean rolloverDetected(final DeltaPegCounter c) {
		return referenceValue.compareTo(c.referenceValue) > 0;
	}

	private boolean hasReferenceValue() {
		return referenceValue != null;
	}
}
