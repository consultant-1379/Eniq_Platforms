package com.ericsson.navigator.esm.model.alarm.snmp;

import java.io.IOException;

import com.ericsson.navigator.esm.manager.snmp.AsynchronousSnmpRequestHandler;
import com.ericsson.navigator.esm.manager.snmp.RequestChannel;
import com.ericsson.navigator.esm.manager.snmp.SNMPRequest;
import com.ericsson.navigator.esm.manager.snmp.TrapReceiver;
import com.ericsson.navigator.esm.model.alarm.AbstractProtocolAlarmList;
import com.ericsson.navigator.esm.model.alarm.Alarm;
import com.ericsson.navigator.esm.model.conversion.ConversionController;
import com.ericsson.navigator.esm.util.SupervisionException;
import com.ericsson.navigator.esm.util.log.Log;

public abstract class AbstractSNMPAlarmList<AI extends SNMPAddressInformation>
		extends AbstractProtocolAlarmList<AI> implements SNMPAlarmList<AI> {

	private static final long serialVersionUID = 1L;
	
	private RequestChannel m_Channel = null;
	private final TrapReceiver m_TrapReceiver;
	private String ipAddress;
	
	/** Last known system up time in hundredths of an second since system (re-)initialized */
	private long m_SystemUptime = 0;

	public AbstractSNMPAlarmList(final String fdn, final AI addressInformation, 
			final Log<Alarm> log, final ConversionController conversionController,
			final TrapReceiver trapReceiver) {
		super(fdn, addressInformation, log, conversionController);
		m_TrapReceiver = trapReceiver;
		ipAddress = addressInformation.getAddress();
	}
	
	@Override
	protected void finalize() throws Throwable{
		m_Channel.free();
		super.finalize();
	}
	
	public void setRequestChannel(final RequestChannel channel){
		m_Channel = channel;
	}
	
	public RequestChannel getRequestChannel(){
		return m_Channel;
	}

	@Override
	public void checkHeartbeat() throws SupervisionException {
		try {
			AsynchronousSnmpRequestHandler.getInstance().addSNMPRequest(
					new SNMPHearbeatRequest<SNMPAlarmList<AI>>(SNMPRequest.SYSTEMUPTIME, this));
		} catch (final IOException e) {
			throw new SupervisionException(
					"Error on sending SNMP request for managed system named "
							+ getFDN(), e);
		}
	}
	
	public abstract SNMPSynchronizationRequest<? extends SNMPAlarmList<SNMPAddressInformation>> getSynchronizationRequest();
	
	@Override
	public void getActiveAlarmList() throws SupervisionException {
		try {
			AsynchronousSnmpRequestHandler.getInstance().addSNMPRequest(getSynchronizationRequest());
		} catch (final IOException e) {
			setSystemUnavailable("Error occured during synchronization: " + e.getMessage());
			throw new SupervisionException(
					"Error occured during synchronization: " + e.getMessage(), e);
		}
	}

	public abstract void startSNMPSupervision() throws SupervisionException;

	public abstract void stopSNMPSupervision() throws SupervisionException;

	@Override
	public void startSupervisionNoneHB() throws SupervisionException {
		System.setProperty("HeartbeatStatus:"+ ipAddress, "false");
		startSNMPSupervision();
		m_TrapReceiver.addTrapListener(this);
	}

	@Override
	public void stopSupervisionNoneHB() throws SupervisionException {
		m_TrapReceiver.removeTrapListener(this);
		stopSNMPSupervision();
	}

	public void setSystemUptime(final long systemUptime) {
		m_SystemUptime = systemUptime;
	}

	public long getSystemUptime() {
		return m_SystemUptime;
	}

}

