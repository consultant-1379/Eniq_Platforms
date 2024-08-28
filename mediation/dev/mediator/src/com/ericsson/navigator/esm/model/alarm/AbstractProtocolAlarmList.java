package com.ericsson.navigator.esm.model.alarm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;

import com.ericsson.navigator.esm.MVM;
import com.ericsson.navigator.esm.model.AddressInformation;
import com.ericsson.navigator.esm.model.Protocol;
import com.ericsson.navigator.esm.model.conversion.Conversion;
import com.ericsson.navigator.esm.model.conversion.ConversionController;
import com.ericsson.navigator.esm.util.SupervisionException;
import com.ericsson.navigator.esm.util.jmx.BeanServerHelper;
import com.ericsson.navigator.esm.util.log.Log;

public abstract class AbstractProtocolAlarmList<AI extends AddressInformation> 
	extends AbstractManagedAlarmList implements ProtocolAlarmList<AI>, AbstractProtocolAlarmListBean{

	enum  ServiceState { AVAILABLE, SYNC_ABORT, HEARTBEAT }; //HEARTBEAT always last

	private static final long serialVersionUID = 1L;
	protected AI m_AddressInformation = null;
	protected ServiceState m_Available = ServiceState.AVAILABLE;
	protected static final Timer HBTIMER = new Timer();
	protected TimerTask m_HBTask = null;
	protected static final int HBINTERVAL = 180000; /* Every three minutes */

	private final String classname = getClass().getName();
	private final ConversionController conversionController;

	public AbstractProtocolAlarmList(final String fdn, final AI addressInformation, 
			final Log<Alarm> log, final ConversionController conversionController) {
		super(fdn, log);
		m_AddressInformation = addressInformation;
		this.conversionController = conversionController;
	}

	public abstract void startSupervisionNoneHB() throws SupervisionException;

	public void startSupervision() throws SupervisionException {
		startSupervisionNoneHB();
		m_HBTask = new TimerTask() {
			@Override
			public void run() {
				try {
					checkHeartbeat();
				} catch (final SupervisionException e) {
					MVM.logger.error(classname
						+ "startSupervision(); Error on sending SNMP request for managed system named "
						+ getFDN(), e);
					setSystemUnavailable(e.getMessage());
				}
			}
		};
		HBTIMER.scheduleAtFixedRate(m_HBTask, 1, HBINTERVAL);
		BeanServerHelper.registerMDynamicBean(this, AbstractProtocolAlarmListBean.class);
		synchronize();
	}

	public abstract void checkHeartbeat() throws SupervisionException;

	public synchronized void setSystemAvailable() {
		if (isAvailable()) {
			return;
		}
		
		//clear previous state
		switch (m_Available) {
		case HEARTBEAT:
			correlate(new HeartbeatFailureAlarm(this, "",
					Alarm.PerceivedSeverity.CLEARED));			
			break;
		case SYNC_ABORT:
			correlate(new SyncAbortedAlarm(this, "",
					Alarm.PerceivedSeverity.CLEARED));			
			break;
		case AVAILABLE:
			break;
		default:
			break;
		}
		m_Available = ServiceState.AVAILABLE;

		try {
			synchronize();
		} catch (final SupervisionException e) {
			abortSync(e.getMessage());
		}
	}

	public synchronized void setSystemUnavailable(final String reason) {
		if(m_Available.equals(ServiceState.SYNC_ABORT)){
			correlate(new SyncAbortedAlarm(this, "",
					Alarm.PerceivedSeverity.CLEARED));	
		}
		m_Available = ServiceState.HEARTBEAT;
		correlate(new HeartbeatFailureAlarm(this, reason,
				Alarm.PerceivedSeverity.CRITICAL));
	}

	public abstract void stopSupervisionNoneHB() throws SupervisionException;

	public void stopSupervision(final boolean isRemoved) throws SupervisionException {
		m_HBTask.cancel();
		stopSupervisionNoneHB();
		if(isRemoved){
			deleteAllAlarms();
		}
		BeanServerHelper.unRegisterMBean(this);
	}
	
	@Override
	public void correlate(final Alarm data) {

		final Conversion plugin = conversionController.getPlugin(m_AddressInformation.getType(), data.getDataType());
		if (plugin != null && data instanceof DefaultAlarm) {
			final Map<String, Serializable> oldProperties = ((DefaultAlarm)data).getProperties();
			Map<String, Serializable> updatedProperties = new HashMap<String, Serializable>(oldProperties);
			
			if (MVM.logger.isDebugEnabled()) {
				MVM.logger.debug(classname+".correlate() Runs conversion plugin " + m_AddressInformation.getType() 
						+ " with properties " + oldProperties);
			}
			try {
				updatedProperties = plugin.convert(updatedProperties);
				if (MVM.logger.isDebugEnabled()) {
					MVM.logger.debug(classname+".correlate() Conversion plugin " + m_AddressInformation.getType() 
							+ " returns " + updatedProperties);
				}
			} catch(final Exception e) {
				updatedProperties = oldProperties;
				MVM.logger.error(classname+".correlate() Error converting properties for "
						+ m_AddressInformation.getType() + " (" + data.getDataType() + "). "
						+ "Resetting properties to original values: " + updatedProperties, e);
			}
			
			if (updatedProperties == null) {
				MVM.logger.debug(classname+".correlate() Conversion plugin " + m_AddressInformation.getType() + 
						" suppressed the alarm "+data.getUniqueId());
				return;
			} else if (! updatedProperties.equals(oldProperties)) {
				final Set<Entry<String, Serializable>> updatedEntries = updatedProperties.entrySet();
				updatedEntries.removeAll(oldProperties.entrySet());		
				((DefaultAlarm)data).update(updatedProperties);
			}
		}else {
			MVM.logger.debug(classname+ ".correlate()- Bean Shell Scripts are not loaded");
		}

		super.correlate(data);
	}

	private void deleteAllAlarms() {
		MVM.logger.debug(classname+".deleteAllAlarms() for deleted system " + getFDN());
		final List<Alarm> alarms = new ArrayList<Alarm>(items.values());
		for(Alarm alarm : alarms){
			changeDataState(alarm.getUniqueId(), Alarm.State.DELETED, "MVM");
		}
	}

	public AI getAddressInformation() {
		return m_AddressInformation;
	}

	public boolean isAvailable() {
		return m_Available.equals(ServiceState.AVAILABLE);
	}

	public void abortSync(final String reason) {
		m_IsSynchronizing = false;
		m_listBeforeSync = null;
		
		if (m_Available.equals(ServiceState.AVAILABLE)) {
			correlate(new SyncAbortedAlarm(this, reason, 
					Alarm.PerceivedSeverity.WARNING));
			m_Available = ServiceState.SYNC_ABORT;
		}
	}
	
	public String getAddress(){
		return m_AddressInformation.toString();
	}
	
	public String getInstanceName(){
		return fdn;
	}
	
	@Override
	public boolean isUpdated(final Protocol<?, ?, ?> protocol) {
		return !getAddressInformation().equals(protocol.getAddressInformation());
	}
}
