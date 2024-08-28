package com.ericsson.navigator.esm.manager.snmp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Timer;
import java.util.TimerTask;

import com.adventnet.snmp.snmp2.SnmpAPI;
import com.adventnet.snmp.snmp2.SnmpException;
import com.adventnet.snmp.snmp2.SnmpOID;
import com.adventnet.snmp.snmp2.SnmpPDU;
import com.ericsson.navigator.esm.MVM;
import com.ericsson.navigator.esm.model.alarm.snmp.SNMPAddressInformation;
import com.ericsson.navigator.esm.model.alarm.snmp.SNMPAddressInformation.VERSION;

public abstract class SNMPRequest<P extends SNMPProtocol<?, ?, ? extends SNMPAddressInformation>>
		implements AsynchronousSnmpRequest {


	public static final String SYSTEMUPTIME = ".1.3.6.1.2.1.1.3.0";
	public static final String SYSTEMUPTIME_EX_INDEX = ".1.3.6.1.2.1.1.3";
	
	protected P m_Protocol = null;
	private static final Timer REQUESTTIMER = new Timer();
	private static long m_LastRequestTime = -1;
	private TimerTask m_RequestTimerTask = null;
	protected SnmpPDU m_PDU = null;
	protected static int m_CurrentRequestID = 0;
	private final static int TIMEOUT = Integer.getInteger("RTIMEOUT", 5000);
	private final static int RETRIES = Integer.getInteger("SRETRIES", 3);
	private final static int SPREADINGTIME = Integer.getInteger("SPREADINGTIME", 1);
	private int m_RetryCount = 0;
	
	private final String classname = getClass().getName();

	public SNMPRequest(final P protocol) {
		m_Protocol = protocol;
	}

	protected abstract void timedOut();

	protected abstract void responseReceived(SnmpPDU pdu);

	protected abstract void error(String error);
	protected abstract void error(int code, String error);

	public void executeRequest() throws IOException {
		if (MVM.logger.isDebugEnabled()) {
			MVM.logger.debug(classname+".executeRequest(); --> " + m_Protocol.getFDN());
		}
		setRequestId();
		m_PDU.getEncodedLength(null);
		final byte[] data = m_PDU.getData();
		final ByteBuffer buffer = ByteBuffer.wrap(data, 0, data.length);
		synchronized (REQUESTTIMER) {
			spread();
			scheduleTimeout();
			final int sentBytes = m_Protocol.getRequestChannel()
					.getChannel().send(
							buffer,
							new InetSocketAddress(m_Protocol
									.getAddressInformation().getAddress(),
									m_Protocol.getAddressInformation()
											.getPort()));
			if (sentBytes == 0 || sentBytes != buffer.position()) {
				throw new IOException("Error sending pdu, sent bytes were "
						+ sentBytes + " out of " + buffer.position());
			}
		}
		if (MVM.logger.isDebugEnabled()) {
			MVM.logger.debug(classname+".executeRequest(); <-- ");
		}
	}
	
	private void spread(){
		if(System.currentTimeMillis() - m_LastRequestTime < SPREADINGTIME){
			try {
				Thread.sleep(SPREADINGTIME);
			} catch (final InterruptedException e) {
				return;
			}
			m_LastRequestTime = System.currentTimeMillis();
		}
	}

	private void removeFromChannel() {
		m_Protocol.getRequestChannel().removeRequest(this);
	}

	private void setRequestId() {
		synchronized (REQUESTTIMER) {
			m_PDU.setReqid(++m_CurrentRequestID);
			if (m_CurrentRequestID == Integer.MAX_VALUE) {
				m_CurrentRequestID = 0;
			}
		}
	}
	
	private boolean decode(final SnmpPDU pdu) {
		try {
			if (!pdu.decode()) {
				error("Failed to decode PDU.");
				return false;
			}
			return true;
		} catch (final SnmpException e) {
			error("Failed to decode PDU: " + e.getMessage());
			return false;
		}
	}

	public void handleResponse(final SnmpPDU pdu) {
		removeFromChannel();
		if (m_RequestTimerTask.cancel()) {
			// Response to late, request already timed out.
			return;
		}
		if (!decode(pdu)) {
			return;
		}
		if (pdu.getErrstat() != SnmpAPI.SNMP_ERR_NOERROR) {
			error(pdu.getErrstat(), pdu.getError());
			return;
		}
		responseReceived(pdu);
	}

	private void scheduleTimeout() {
		m_RequestTimerTask = new TimedOutTask();
		REQUESTTIMER.schedule(m_RequestTimerTask, TIMEOUT);
	}

	public SNMPProtocol<?, ?, ? extends SNMPAddressInformation> getManagedSystem() {
		return m_Protocol;
	}

	public boolean matchesResponse(final SnmpPDU pdu){
		return m_PDU.getReqid() == pdu.getReqid();
	}

	@Override
	public String toString() {
		return m_PDU.getReqid() + "";
	}

	private void retry() {
		try {
			AsynchronousSnmpRequestHandler.getInstance().addSNMPRequest(this);
		} catch (final IOException e) {
			error(e.getMessage());
			MVM.logger.error(classname+".retry(); Failed to add SNMP Request for "
					+ m_Protocol.getFDN(), e);
		}
	}

	private class TimedOutTask extends TimerTask {
		private boolean finished = false;

		@Override
		public synchronized boolean cancel() {
			super.cancel();
			return finished;
		}

		@Override
		public synchronized void run() {
			try{
				removeFromChannel();
				if (m_RetryCount < RETRIES) {
					m_RetryCount++;
					retry();
				} else {
					timedOut();
				}
				finished = true;
			} catch (Exception e){
				MVM.logger.error(classname+".TimedOutTask.run(); Failed to timeout request "
						+ m_Protocol.getFDN(), e);
			}
		}
	}
	
	protected SnmpPDU createRequest(final String[] oids, final byte reqType) {
		final SnmpPDU pdu = new SnmpPDU();
		pdu.setCommand(reqType);
		for (final String oid : oids) {
			pdu.addNull(new SnmpOID(oid));//NOPMD
		}
		final SNMPAddressInformation information = m_Protocol
				.getAddressInformation();
		pdu.setCommunity(information.getCommunity());
		if (information.getVersion().equals(VERSION.V1)) {
			pdu.setVersion(SnmpAPI.SNMP_VERSION_1);
		} else {
			pdu.setVersion(SnmpAPI.SNMP_VERSION_2C);
		}
		return pdu;
	}
}
