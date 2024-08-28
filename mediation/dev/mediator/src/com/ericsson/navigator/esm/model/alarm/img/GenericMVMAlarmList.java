package com.ericsson.navigator.esm.model.alarm.img;

//import com.ericsson.navigator.esm.manager.text.txf.Alarm;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.ericsson.navigator.esm.manager.text.txf.AlarmFileListener;
import com.ericsson.navigator.esm.manager.text.txf.FileReceiver;
import com.ericsson.navigator.esm.model.DefaultAddressInformation;
import com.ericsson.navigator.esm.model.Protocol;
import com.ericsson.navigator.esm.model.alarm.AbstractProtocolAlarmList;
import com.ericsson.navigator.esm.model.alarm.*;
//import com.ericsson.navigator.esm.model.alarm.text.txf.Alarm;
//import com.ericsson.navigator.esm.model.alarm.text.txf.Log;
import com.ericsson.navigator.esm.model.alarm.text.txf.TXFAlarmList;
import com.ericsson.navigator.esm.model.alarm.text.txf.TranslateMap;
//import com.ericsson.navigator.esm.model.alarm.text.txf.TranslatedAlarm;
import com.ericsson.navigator.esm.model.conversion.ConversionController;
import com.ericsson.navigator.esm.util.SupervisionException;
import com.ericsson.navigator.esm.model.alarm.Alarm;
import com.ericsson.navigator.esm.util.log.Log;

public class GenericMVMAlarmList extends AbstractProtocolAlarmList<DefaultAddressInformation> implements AlarmFileListener  {

	private long lastHeartbeatPush = System.currentTimeMillis();
	private final FileReceiver fileReceiver=null;
	private static final String classname = TXFAlarmList.class.getName();
	private static Logger logger = Logger.getLogger(classname);
	//private List<Alarm> syncAlarmList = null;
	//private final long syncTimeout;
	private TimerTask syncTimeoutTask = null;
	//private final TranslateMap translateMap;
	
	public GenericMVMAlarmList(final String fdn, final DefaultAddressInformation addressInformation, final Log<Alarm> log) {
		super(fdn, addressInformation, log, null);
		}

	@Override
	public int getAlarmCount() {
		return 0;
	}

	@Override
	public void startSupervisionNoneHB() throws SupervisionException {
		
		
	}

	@Override
	public void checkHeartbeat() throws SupervisionException {
//		if(lastHeartbeatPush  < System.currentTimeMillis() - HBINTERVAL){
//			setSystemUnavailable("Heartbeat request timed out!");
//		} else {
//			setSystemAvailable();
//		}
		
	}

	@Override
	public void stopSupervisionNoneHB() throws SupervisionException {
		//fileReceiver.removeAlarmFileListener(this, getAddressInformation().getType());
		
	}

//	@Override
//	public void receivedAlarm(Alarm alarm) {
//		// TODO Auto-generated method stub
//		
//	}

	@Override
	public void receivedHeartbeat() {
		lastHeartbeatPush = System.currentTimeMillis();
		
	}

	@Override
	public void receivedAlarm(final Alarm alarm) {
		// TODO Auto-generated method stub
		
	}

/*	@Override
	public String getFDN() {
		// TODO Auto-generated method stub
		return null;
	}*/

//	@Override
//	public void receivedAlarm(
//			com.ericsson.navigator.esm.manager.text.txf.Alarm alarm) {
//		// TODO Auto-generated method stub
//		
//	}
	
	
	public void getActiveAlarmList() throws SupervisionException {}
	
	
	public boolean isSynchable(){
		return false;
	}

//	@Override
//	public String getFDN() {
//		// TODO Auto-generated method stub
//		return null;
//	}
	


}
