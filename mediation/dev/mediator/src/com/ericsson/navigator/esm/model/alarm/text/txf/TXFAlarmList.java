package com.ericsson.navigator.esm.model.alarm.text.txf;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.ericsson.navigator.esm.manager.text.txf.AlarmFileListener;
import com.ericsson.navigator.esm.manager.text.txf.FileReceiver;
import com.ericsson.navigator.esm.model.DefaultAddressInformation;
import com.ericsson.navigator.esm.model.alarm.AbstractProtocolAlarmList;
import com.ericsson.navigator.esm.model.alarm.Alarm;
import com.ericsson.navigator.esm.model.alarm.Alarm.RecordType;
import com.ericsson.navigator.esm.model.conversion.ConversionController;
import com.ericsson.navigator.esm.util.SupervisionException;
import com.ericsson.navigator.esm.util.log.Log;

public class TXFAlarmList extends AbstractProtocolAlarmList<DefaultAddressInformation> implements AlarmFileListener { //NOPMD

	private static final String classname = TXFAlarmList.class.getName();
	private static Logger logger = Logger.getLogger(classname);
	
	private static final long serialVersionUID = 1L; 
	private final FileReceiver fileReceiver;
	private final TranslateMap translateMap;
	private long lastHeartbeatPush = System.currentTimeMillis();
	private List<Alarm> syncAlarmList = null;
	private final long syncTimeout;
	private TimerTask syncTimeoutTask = null;

	public TXFAlarmList(final String fdn, final DefaultAddressInformation addressInformation,
			final Log<Alarm> log, final ConversionController conversionController,
			final FileReceiver fileReceiver, final TranslateMap translateMap, final long syncTimeout) {
		super(fdn, addressInformation, log, conversionController);
		this.fileReceiver = fileReceiver;
		this.translateMap = translateMap;
		this.syncTimeout = syncTimeout;
	}

	@Override
	public void checkHeartbeat() throws SupervisionException {
		if(lastHeartbeatPush  < System.currentTimeMillis() - HBINTERVAL){
			setSystemUnavailable("Heartbeat request timed out!");
		} else {
			setSystemAvailable();
		}
	}

	@Override
	public void startSupervisionNoneHB() throws SupervisionException {
		fileReceiver.addAlarmFileListener(this, getAddressInformation().getType());
	}

	@Override
	public void stopSupervisionNoneHB() throws SupervisionException {
		fileReceiver.removeAlarmFileListener(this, getAddressInformation().getType());
	}

	public boolean isSynchable() {
		return false;
	}

	@Override
	public void getActiveAlarmList() throws SupervisionException {}

	public void receivedAlarm(final Alarm alarm) {
		if(alarm.getRecordType().equals(Alarm.RecordType.SYNC_START)){
			startSync();
		} else if (alarm.getRecordType().equals(Alarm.RecordType.SYNC_END)){
			endSync(syncAlarmList, alarm);
		} else if (alarm.getRecordType().equals(Alarm.RecordType.SYNC_ABORTED)){
			abortSync(alarm.getSpecificProblem() + " " + alarm.getAdditionalText());
		} else if (alarm.getRecordType().equals(Alarm.RecordType.CLEAR_ALL)){
			startSync();
			endSync(new ArrayList<Alarm>(), alarm);
		} else if(m_IsSynchronizing){
			final TranslatedAlarm translatedAlarm = translateMap.translate(alarm);
			if (translatedAlarm.isValid()) {
				syncAlarmList.add(translatedAlarm);
			} else {
				logger.warn(classname+".receivedAlarm(); Dropping alarm as it is invalid\n"+ translatedAlarm);
			}
		} else{
			final TranslatedAlarm translatedAlarm = translateMap.translate(alarm);
			if (translatedAlarm.isValid()) {
				correlate(translatedAlarm);
			} else {
				logger.warn(classname+".receivedAlarm(); Dropping alarm as it is invalid\n"+ translatedAlarm);
			}
		}
	}
	
	@Override
	public void abortSync(final String reason) {
		if(syncTimeoutTask != null){
			syncTimeoutTask.cancel();
		}
		super.abortSync(reason);
	}
	
	private void endSync(final List<Alarm> syncAlarmList, final Alarm alarm){
		if(!m_IsSynchronizing) {
			logger.warn(classname+".endSync(); Received Alarm is not consistent with internal state machine:\n" + alarm +
					"\nSkipping current alarm \"" + alarm.getRecordType() + "\" request");
			return;
		}
		if(syncTimeoutTask != null){
			syncTimeoutTask.cancel();
		}
		correlateSync(syncAlarmList, RecordType.values());
	}

	private void startSync() {
		synchronized (this) {
			if (m_IsSynchronizing) {
				return;
			}
			m_IsSynchronizing = true;
			syncAlarmList = new ArrayList<Alarm>();
			syncTimeoutTask = new TimerTask(){
				@Override
				public void run() {
					abortSync("Synchronization request timed out after "
							+ syncTimeout/1000 + " seconds.");
				}
			};
			HBTIMER.schedule(syncTimeoutTask, syncTimeout);
		}
		m_listBeforeSync = new ArrayList<Alarm>();
		m_listBeforeSync.addAll(items.values());
	}

	public void receivedHeartbeat() {
		lastHeartbeatPush = System.currentTimeMillis();
	}
	
	
}
