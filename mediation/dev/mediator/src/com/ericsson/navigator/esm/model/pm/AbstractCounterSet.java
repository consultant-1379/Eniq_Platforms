package com.ericsson.navigator.esm.model.pm;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.ericsson.navigator.esm.model.AbstractManagedData;
import com.ericsson.navigator.esm.model.ManagedDataType;

public abstract class AbstractCounterSet extends AbstractManagedData<CounterSet.State, CounterSet> 
	implements CounterSet{
 
	private static final long serialVersionUID = 1L;
	protected final Map<String, Counter> counters;
	protected String counterSetId = "";
	protected String moid = "";
	protected Date endTime = new Date();
	protected int granularityPeriod = 0;
	protected Date lastModifiedTime = new Date();
	protected String fdn = "";
	protected String type = "";
	
	protected AbstractCounterSet(){
		counters = new HashMap<String, Counter>();
		state = State.Active;
	}

	protected AbstractCounterSet(final CounterSet c){
		super(c);
		counterSetId = c.getCounterSetId();
		counters = new HashMap<String, Counter>(c.getCounters());
		moid = c.getMoid();
		endTime = c.getEndTime();
		lastModifiedTime = c.getLastModifiedTime();
		granularityPeriod = c.getGranularityPeriod();
		type=c.getType();
		fdn=c.getFdn();
	}
	
	/**
     * @param fdn the fdn to set
     */
    public void setFdn(final String fdn) {
	    this.fdn = fdn;
    }    
    
   /**
	 * @param type the type to set
	 */
	public void setType(final String type) {
		this.type = type;
	}	
	
	@Override
	public Date getLastModifiedTime() {
		return lastModifiedTime;
	}
	
	@Override
	public String getCounterSetId() {
		return counterSetId;
	}
	
	@Override
	public String getMoid() {
		return moid;
	}
	
	@Override
	public Map<String, Counter> getCounters() {
		return counters;
	}
	
	@Override
	public Date getEndTime() {
		return endTime;
	}
	
	@Override
	public int getGranularityPeriod() {
		return granularityPeriod;
	}
	
	/* (non-Javadoc)
	 * @see com.ericsson.navigator.esm.model.pm.CounterSet#getFdn()
	 */
	@Override
	public String getFdn() {
	    return fdn;
	}
	
	/* (non-Javadoc)
	 * @see com.ericsson.navigator.esm.model.pm.CounterSet#getType()
	 */
	@Override
	public String getType() {
	    return type;
	}	
	
	@Override
	public ManagedDataType getDataType() {
		return ManagedDataType.CounterSet;
	}
	
	@Override
	public CounterSet getSerializable() {
		return new SerializedCounterSet(this);
	}
	
	@Override
	protected boolean updateData(final CounterSet data) {
		if(missedRopDetected(data)){
			resetCounters();
		}
		final Set<String> countersNotUsed = new HashSet<String>(counters.keySet());
		for(String counterName : data.getCounters().keySet()){
			final Counter oldCounter = counters.get(counterName);
			final Counter newCounter = data.getCounters().get(counterName);
			if(oldCounter == null || !oldCounter.getType().equals(newCounter.getType()) ){
				counters.put(counterName, newCounter);
			} else {
				oldCounter.update(data.getCounters().get(counterName));
			}
			countersNotUsed.remove(counterName);
		}
		for(String counterNameNotUsed : countersNotUsed){
			counters.remove(counterNameNotUsed);
		}
		endTime = data.getEndTime();
		lastModifiedTime = new Date();
		return true;
	}

	/**
	 * Detect if one or more ROPs has been missed
	 */
	protected boolean missedRopDetected(final CounterSet data) {
		final long periodSinceLast = data.getEndTime().getTime() - getEndTime().getTime();
		return periodSinceLast/1000 - data.getGranularityPeriod() > data.getGranularityPeriod()/2;
	}

	public void resetCounters() {
		for(Counter c : counters.values()){
			c.reset();
		}
	}

	@Override
	public boolean shallPersist() {
		return true;
	}
	
	@Override
	public String toString() {
		final StringBuffer b = new StringBuffer(super.toString());
		b.append("\nCounterSetID : ");
		b.append(counterSetId);
		b.append("\nMOID : ");
		b.append(moid);
		b.append("\nEndTime : ");
		b.append(endTime);
		b.append("\nGranularity period : ");
		b.append(granularityPeriod);
		b.append("\nLast modified time : ");
		b.append(lastModifiedTime);
		b.append("\nFdn : ");
		b.append(fdn);
		b.append("\nType : ");
		b.append(type);
		b.append("\nNof counters : ");
		b.append(counters == null ? "" : counters.size());
		return b.toString();
	}
}
