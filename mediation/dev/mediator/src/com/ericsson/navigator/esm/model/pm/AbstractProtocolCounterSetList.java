package com.ericsson.navigator.esm.model.pm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;

import com.ericsson.navigator.esm.MVM;
import com.ericsson.navigator.esm.model.AddressInformation;
import com.ericsson.navigator.esm.model.ManagedDataEvent;
import com.ericsson.navigator.esm.model.ManagedDataListener;
import com.ericsson.navigator.esm.model.Protocol;
import com.ericsson.navigator.esm.model.alarm.Alarm;
import com.ericsson.navigator.esm.model.alarm.ProtocolAlarmList;
import com.ericsson.navigator.esm.model.alarm.pm.PMCollectionFailureAlarm;
import com.ericsson.navigator.esm.model.conversion.Conversion;
import com.ericsson.navigator.esm.model.conversion.ConversionController;
import com.ericsson.navigator.esm.model.pm.CounterSet.State;
import com.ericsson.navigator.esm.model.pm.snmp.SNMPCounterSet;
import com.ericsson.navigator.esm.util.SupervisionException;
import com.ericsson.navigator.esm.util.jmx.BeanServerHelper;
import com.ericsson.navigator.esm.util.log.Log;
import com.ericsson.navigator.esm.model.pm.file.remote.DefaultCounterSetCallback;

@SuppressWarnings( "PMD.CyclomaticComplexity" )
public abstract class AbstractProtocolCounterSetList<AI extends AddressInformation, CSS extends CounterSetScheduling<?>>
		extends ManagedCounterSetList implements
		Protocol<CounterSet.State, CounterSet, AI>,
		AbstractProtocolCounterSetListBean {

	private static final int PM_DATA_STORAGE_TIMEOUT = Integer.getInteger(
			"PM_DATA_STORAGE_TIMEOUT", 24 * 7); // 1 week
	private final AI addressInformation;
	protected final List<CSS> counterSetSchedulings;
	private final List<TimerTask> scheduledTasks;
	private static final Timer PMTIMER = new Timer("PM Poller Timer");
	private final String classname = getClass().getName();
	private final Map<String, Set<Long>> remainingCounterSets = Collections
			.synchronizedMap(new HashMap<String, Set<Long>>());
	private final ProtocolAlarmList<?> alarmList;
	private final ConversionController conversionController;
	private final Map<Long, PMCollectionFailureAlarm> localPMAlarmList = new HashMap<Long, PMCollectionFailureAlarm>();
	protected final CounterSetDefinitionsController counterSetDefinitionsController;
    private boolean sftpParse =false;
    private boolean snmpParse =false;
	public AbstractProtocolCounterSetList(final String fdn,
			final List<CSS> counterSets, final AI addressInformation,
			final Log<CounterSet> log, final ProtocolAlarmList<?> alarmList,
			final CounterSetDefinitionsController counterSetDefinitionsController,
			final ConversionController conversionController) {
		super(fdn, log);
		this.counterSetSchedulings = counterSets;
		this.addressInformation = addressInformation;
		this.alarmList = alarmList;
		this.counterSetDefinitionsController = counterSetDefinitionsController;
		this.conversionController = conversionController;
		scheduledTasks = new ArrayList<TimerTask>();
	}

	/**
	 * Method to tell list to snapshot the data for a definition Id.
	 * 
	 * This method allows for protocol implementations that know when it has
	 * fetched all data to correlate away old no longer used data.
	 * 
	 * @param definitionId
	 *            The definition Id
	 */
	public void snapshot(final String definitionId) {
		final Set<Long> counterSets = new HashSet<Long>();
		remainingCounterSets.put(definitionId, counterSets);
		for ( CounterSet counterSet : getCounterSets()) {
			final SNMPCounterSet snmpCounterSet = (SNMPCounterSet) counterSet;
			if (snmpCounterSet.getCounterSetId().equals(definitionId)) {
				counterSets.add(counterSet.getUniqueId());
			}
		}
	}

	@SuppressWarnings( "PMD.CyclomaticComplexity" )
	@Override
	public void correlate(final CounterSet data) {
		final Conversion plugin = conversionController.getPlugin(data.getCounterSetId(), data.getDataType());
		if (plugin != null && data instanceof ProtocolCounterSet) {
			final Map<String, Serializable> oldProperties = ((ProtocolCounterSet)data).getProperties();
			Map<String, Serializable> updatedProperties = new HashMap<String, Serializable>(oldProperties);
			
			if (MVM.logger.isDebugEnabled()) {
				MVM.logger.debug(classname+".correlate() Runs conversion plugin " + data.getCounterSetId() 
						+ " with properties " + oldProperties);
			}
			try {
				updatedProperties = plugin.convert(updatedProperties);
				if (MVM.logger.isDebugEnabled()) {
					MVM.logger.debug(classname+".correlate() Conversion plugin " + data.getCounterSetId() 
							+ " returns " + updatedProperties);
				}
			} catch(final Exception e) {
				updatedProperties = oldProperties;
				MVM.logger.error(classname+".correlate() Error converting properties for "
						+ data.getCounterSetId() + " (" + data.getDataType() + "). "
						+ "Resetting properties to original values: " + updatedProperties, e);
			}

			if (updatedProperties == null) {
				MVM.logger.debug(classname+".correlate() Conversion plugin suppresses the counterset "+data.getUniqueId());
				return;
			} else if (! updatedProperties.equals(oldProperties)) {
				final Set<Entry<String, Serializable>> updatedEntries = updatedProperties.entrySet();
				updatedEntries.removeAll(oldProperties.entrySet());		
				((ProtocolCounterSet)data).update(updatedProperties, counterSetDefinitionsController);
			}
		}

		if (data instanceof SNMPCounterSet) {
			final SNMPCounterSet snmpCounterSet = (SNMPCounterSet) data;
			final Set<Long> counterSets = remainingCounterSets
					.get(snmpCounterSet.getCounterSetId());
			if (counterSets != null) {
				counterSets.remove(data.getUniqueId());
			}
		}
		
		if (data instanceof AbstractCounterSet){
			MVM.logger.debug(classname+".correlate() Updating counter set with type " + addressInformation.getType() 
					+ " and fdn " + fdn);
			((AbstractCounterSet)data).setType(addressInformation.getType());
			((AbstractCounterSet)data).setFdn(fdn);
		}
		
		super.correlate(data);
	}

	/**
	 * Remove not used data from list.
	 * 
	 * This method allows for protocol implementations that know when it has
	 * fetched all data to correlate away old no longer used data.
	 * 
	 * @param definitionId
	 *            The definition Id
	 */
	public void correlateSnapShot(final String definitionId) {
		final Set<Long> notUsedIds = remainingCounterSets.get(definitionId);
		for (long uniqueId : notUsedIds) {
			changeDataState(uniqueId, State.Deleted, "MVM");
		}
//		for (CounterSetScheduling<?> scheduling : counterSetSchedulings) {
//			for(CounterSetIdentification identification : scheduling.getIdentifications()){
//				if (identification.getCounterSetId().equals(definitionId)) {
//					checkPMStatus(scheduling);
//				}
//			}
//		}
	}

	@Override
	public boolean isUpdated(final Protocol<?, ?, ?> protocol) {
		return !getAddressInformation()
				.equals(protocol.getAddressInformation());
//				|| !counterSetSchedulings
//						.equals(((AbstractProtocolCounterSetList<AI, CSS>) protocol).counterSetSchedulings);
	}

	@Override
	public AI getAddressInformation() {
		return addressInformation;
	}

	// Synchronized access...
	private List<CounterSet> getCounterSets() {
		final List<CounterSet> counterSetCopy = new ArrayList<CounterSet>();
		final ManagedDataListener listener = new ManagedDataListener<CounterSet>() {
			@Override
			public void pushDataEvent(final ManagedDataEvent<CounterSet> event) {
				counterSetCopy.add(event.getData());
			}
		};
		addManagedDataListener(listener, true);
		removeManagedDataListener(listener);
		return counterSetCopy;
	}

	/**
	 * Remove old PM counter sets that are older than the
	 * PM_DATA_STORAGE_TIMEOUT and cease active PM failure alarm.
	 */
	void removeOldCounterSets(final CounterSetScheduling<?> counterSetSchedule,
			final int numberOfHours) {
		for (CounterSet counterSet : getCounterSets()) {
			for(CounterSetIdentification identification : counterSetSchedule.getIdentifications()){
				if (counterSet.getCounterSetId().equals(
						identification.getCounterSetId())) {
					final long periodSinceLast = System.currentTimeMillis()
							- counterSet.getLastModifiedTime().getTime();
					if (periodSinceLast / 1000 / 60 / 60 > numberOfHours) {
						//TR HM51902 and HM31152  - PMCollectionFailureAlarms should raise on FDN and clear on FDN.
								final PMCollectionFailureAlarm pmAlarm = new PMCollectionFailureAlarm( //NOPMD
								counterSetSchedule.getFdn(), identification.getCounterSetId(), null,
								counterSetSchedule.getRop(), null, Alarm.PerceivedSeverity.CLEARED);
						alarmList.correlate(pmAlarm);
						localPMAlarmList.remove(pmAlarm.getUniqueId());
						changeDataState(counterSet.getUniqueId(), State.Deleted,
								"MVM");
					}
				}
			}
		}
	}

	/**
	 * Check that we are collecting all countersets for a counter set id
	 * according to schedule.
	 * 
	 * If a counterset has not been collected for an entire ROP interval or all
	 * is missing then raise an alarm. Clear the alarm if all counters sets are
	 * okey.
	 * 
	 * @param counterSetSchedule
	 *            The scheduling to check
	 */
	void  checkPMStatus(final CounterSetScheduling<?> counterSetSchedule) {
		MVM.logger.debug(classname + " .checkPMStatus(); -->");
		boolean allOkey = true;
		boolean allMissing = true;
	    long periodSinceLast=0 ;
	    	  
	   	for (CounterSet counterSet : getCounterSets()) {
			for(CounterSetIdentification identification : counterSetSchedule.getIdentifications()){

				 
			if (counterSet.getCounterSetId().equals(
						identification.getCounterSetId())) {
				
		        	allMissing = false;
					periodSinceLast = System.currentTimeMillis()
							- counterSet.getLastModifiedTime().getTime();
				  
				  if(sftpParse)//True when it will be called after remote file parsing.
				    {
				    	if(!DefaultCounterSetCallback.parse) //true when No files has parsed.
				    	{
				    	 allOkey = false ;
				    	//TR HM51902 and HM31152  - PMCollectionFailureAlarms should raise on FDN and clear on FDN.
				    	 final PMCollectionFailureAlarm pmAlarm = new PMCollectionFailureAlarm( //NOPMD
									counterSetSchedule.getFdn(), identification.getCounterSetId(), null,
									counterSetSchedule.getRop(), null, Alarm.PerceivedSeverity.MINOR);
				    		 alarmList.correlate(pmAlarm);
								localPMAlarmList.put(pmAlarm.getUniqueId(), pmAlarm);				    	  	
				    	}
				    }
				  
				  else if(snmpParse)//True when it will be called after snmp pkt data colletion failed.
				    {
				    	 allOkey = false ;
				    	 final PMCollectionFailureAlarm pmAlarm = new PMCollectionFailureAlarm( //NOPMD
									counterSetSchedule.getFdn(), identification.getCounterSetId(), null,
									counterSetSchedule.getRop(), null, Alarm.PerceivedSeverity.MINOR);
				    		 alarmList.correlate(pmAlarm);
								localPMAlarmList.put(pmAlarm.getUniqueId(), pmAlarm);				    					    	
		             }				  
				    
				  			    
					if ( (periodSinceLast / 1000) > counterSet.getGranularityPeriod()) {
						allOkey = false;
						final PMCollectionFailureAlarm pmAlarm = new PMCollectionFailureAlarm( //NOPMD
								counterSetSchedule.getFdn(), identification.getCounterSetId(), null,
								counterSetSchedule.getRop(), null, Alarm.PerceivedSeverity.MINOR);
				    		 alarmList.correlate(pmAlarm);
								localPMAlarmList.put(pmAlarm.getUniqueId(), pmAlarm);				    	    	
					}
				}
			}
		}
	
		
		if (allMissing) {
			
			for(CounterSetIdentification identification : counterSetSchedule.getIdentifications()){
				final PMCollectionFailureAlarm pmAlarm = new PMCollectionFailureAlarm( //NOPMD
						counterSetSchedule.getFdn(), identification.getCounterSetId(), null,
						counterSetSchedule.getRop(), null, Alarm.PerceivedSeverity.MINOR);
				alarmList.correlate(pmAlarm);
				localPMAlarmList.put(pmAlarm.getUniqueId(), pmAlarm);
			}
		} else if (allOkey) {
			//TR HL80649 - clear alarms for only PM collected countersets. 
			for (CounterSet counterSet : getCounterSets()) { 
				for(CounterSetIdentification identification : counterSetSchedule.getIdentifications()){
					if (counterSet.getCounterSetId().equals(identification.getCounterSetId())) {
						final PMCollectionFailureAlarm pmAlarm = new PMCollectionFailureAlarm( //NOPMD
						counterSetSchedule.getFdn(), identification.getCounterSetId(), null,
						counterSetSchedule.getRop(), null, Alarm.PerceivedSeverity.CLEARED);
						
						if(localPMAlarmList.containsKey(pmAlarm.getUniqueId()))
						{
							alarmList.correlate(pmAlarm);
							localPMAlarmList.remove(pmAlarm.getUniqueId());
						}							
					}
				}	
			}
		}
	  MVM.logger.debug(classname + " .checkPMStatus(); <--");
	}
	
	

/*
 * parsefielPM() method is introduced due to TR HL85708
 * This method will be called from CounterSetFileFetcher and SNMPcounterSetList
 */
	public void parsefilePM(final CounterSetScheduling<?> counterSetSchedule )
	{
		MVM.logger.debug(classname + ".parsefilePM(); -->");
		
		
		if(classname.contains("SNMPCounterSetList"))
		{
			sftpParse =false;//Used only for remote PM file fetched 
			snmpParse=true;
		//checkPMStatus(counterSetSchedule);
		}
		else{ //Called from SFTPCountersetList
			sftpParse =true;
			snmpParse= false;
			//checkPMStatus(counterSetSchedule);
		}
		DefaultCounterSetCallback.parse = false;
		sftpParse = false;
		snmpParse=false;
		MVM.logger.debug(classname +" .parsefilePM(); <-- ");
	}
	
	private  void  fetchCounterSets(final CSS counterSetSchedule) {
		MVM.logger.debug(classname +" .fetchCounterSets();-->");
		try {
			removeOldCounterSets(counterSetSchedule,
					PM_DATA_STORAGE_TIMEOUT);
			//checkPMStatus(counterSetSchedule);
			fetchCounterSet(counterSetSchedule);
					
		} catch (final SupervisionException e) {
			MVM.logger.error(classname
					+ ".fetchCounterSets(); Error on sending SNMP request for managed system named "
					+ getFDN(), e);
		} catch (final Exception e) {
			MVM.logger.error(classname
					+ ".fetchCounterSets(); Error fetching counter sets "
					+ getFDN(), e);
		}
		MVM.logger.debug(classname +" .fetchCounterSets(); <-- ");
	}
	
	@Override
	public void startSupervision() throws SupervisionException {
		for (final CSS counterSetSchedule : counterSetSchedulings) {
			final TimerTask task = new TimerTask() { // NOPMD
				@Override
				public void run() {
					fetchCounterSets(counterSetSchedule);
				}
			};
			scheduledTasks.add(task);
			
			PMTIMER.scheduleAtFixedRate(task, getTimeOffset(Calendar
					.getInstance(), counterSetSchedule.getRop(),counterSetSchedule.getOffset()),
					counterSetSchedule.getRop() * 60000);
		}
		BeanServerHelper.registerMDynamicBean(this,
				AbstractProtocolCounterSetListBean.class);
	}

	static Date getTimeOffset(final Calendar calendar, final int rop, final int offset) {
		final int minutes = calendar.get(Calendar.MINUTE);
		final int ropsPassed = (int) minutes / rop;
		final int nextRopMinute = ropsPassed * rop + rop;
		final int nextRopPlusOffest = nextRopMinute + offset;
		final Calendar result = Calendar.getInstance();
		result.setTime(calendar.getTime());
		result.set(Calendar.MINUTE, nextRopPlusOffest);
		result.set(Calendar.SECOND, 0);
		result.set(Calendar.MILLISECOND, 0);
		return result.getTime();
	}

	protected abstract void fetchCounterSet(final CSS counterSetSchedule)
			throws SupervisionException;

	@Override
	public void stopSupervision(final boolean isRemoved)
			throws SupervisionException {
		for (TimerTask task : scheduledTasks) {
			task.cancel();
		}
		if(isRemoved){
			cleanupPMFailureAlarms();
		}
		BeanServerHelper.unRegisterMBean(this);
	}

	// Send cease on all existing PM failure alarms...
	void cleanupPMFailureAlarms() {
		for(PMCollectionFailureAlarm pmAlarm : localPMAlarmList.values()) {
			final PMCollectionFailureAlarm clearAlarm = new PMCollectionFailureAlarm(pmAlarm); //NOPMD
			clearAlarm.setPerceivedSeverity(Alarm.PerceivedSeverity.CLEARED);
			alarmList.correlate(clearAlarm);
		}
		localPMAlarmList.clear();
	}

	@Override
	public String getAddress() {
		return addressInformation.toString();
	}

	@Override
	public int getCounterSetCount() {
		return counterSetSchedulings.size();
	}

	@Override
	public List<String> getCounterSetSchedulingInformation() {
		final List<String> texts = new ArrayList<String>();
		for (CounterSetScheduling schedule : counterSetSchedulings) {
			texts.add(schedule.toString());
		}
		return texts;
	}

	@Override
	public String getInstanceName() {
		return fdn;
	}
	
	
	
}
