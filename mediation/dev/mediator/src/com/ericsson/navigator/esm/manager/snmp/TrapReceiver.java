package com.ericsson.navigator.esm.manager.snmp;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

import org.apache.log4j.Logger;

import com.adventnet.snmp.snmp2.SnmpException;
import com.adventnet.snmp.snmp2.SnmpMessage;
import com.adventnet.snmp.snmp2.SnmpPDU;
import com.ericsson.navigator.esm.model.alarm.snmp.generic.MibController;
import com.ericsson.navigator.esm.util.component.Component;
import com.ericsson.navigator.esm.util.component.ComponentInitializationException;
import com.ericsson.navigator.esm.util.component.ComponentShutdownException;
import com.ericsson.navigator.esm.util.jmx.BeanServerHelper;
import com.ericsson.navigator.esm.util.rate.Measures;
import com.ericsson.navigator.esm.util.reference.WeakListenerList;

public final class TrapReceiver extends Thread implements TrapReceiverMBean, Component {

	private final WeakListenerList<ManagedSystemTrapListener> m_Listeners = new WeakListenerList<ManagedSystemTrapListener>();
	private DatagramSocket m_Socket = null;
	private static final int PDUBUFFERSIZE = Integer.getInteger("PDUSIZE",
			1024 * 65); // 65KB
	private final static int SECONDS = 5;
	final private Logger m_Logger;
	final private MibController m_MibController;
	final private InetSocketAddress m_BindAddress;
	private long m_TotalTrapsReceived = 0;
	private final Measures m_Measures;

	private final String classname = getClass().getName();

	public TrapReceiver(final InetSocketAddress bindAddress, final Logger logger, final MibController mibController) {
		m_Logger = logger;
		m_MibController = mibController;
		m_BindAddress = bindAddress;
		m_Measures = new Measures(SECONDS);
		BeanServerHelper.registerMBean(this);
	}

	public synchronized void initialize() throws ComponentInitializationException {
		try {
			m_Socket = new DatagramSocket(m_BindAddress);
			m_Socket.setReceiveBufferSize(100000000);
		} catch (final BindException e){
			throw new ComponentInitializationException("SNMP Trap Receiver could not " +
					"bind to port 162. Port 162 is occupied by another application. ", e, true);
		} catch (SocketException e) {
			throw new ComponentInitializationException("Socket error occured", e, true);
		}
		start();
	}
	
	@Override
	public String getComponentName() {
		return TrapReceiver.class.getSimpleName();
	}
	
	@Override
	public void shutdown() throws ComponentShutdownException {
		interrupt();
	}

	@Override
	public void interrupt() {
		super.interrupt();
		m_Socket.close();
	}

	public int getPort() {
		return m_Socket.getLocalPort();
	}

	@Override
	public void run() {

		while (!isInterrupted()) {
			try {
				final DatagramPacket packet = new DatagramPacket(// NOPMD
						new byte[PDUBUFFERSIZE], PDUBUFFERSIZE);
				m_Socket.receive(packet);
				++m_TotalTrapsReceived;
				m_Measures.addValue(System.currentTimeMillis(), 1);
				SnmpMessage message = null;
				final byte[] b = new byte[packet.getLength()];// NOPMD
				System.arraycopy(packet.getData(), 0, b, 0, packet.getLength());
				message = new SnmpMessage(b);// NOPMD
				final SnmpPDU pdu = message.getPDU();
				pdu.decode();
				pdu.setAgentAddress(packet.getAddress());
				receivedTrap(pdu);
			} catch (final SocketException e) {
				if (e.getMessage().indexOf("Socket closed") != -1) {
					return;
				} else {
					m_Logger.error(classname
							+ ".run(); Socket error when receiving trap.", e);
				}
			} catch (final InterruptedIOException e) {
				m_Logger.debug(classname
						+ ".run(); Interrupted IO error when receiving trap.", e);
				return;
			} catch (final IOException e) {
				m_Logger.error(classname
						+ ".run(); IO error when receiving trap.", e);
			} catch (final SnmpException e) {
				m_Logger.error(classname
						+ ".run(); SNMP error when parsing received trap.", e);
			}
		}
	}

	public void addTrapListener(final ManagedSystemTrapListener l) {
		synchronized (m_Listeners) {
			if (!m_Listeners.contains(l)) {
				m_Listeners.addListener(l);
			}
		}
	}

	public void removeTrapListener(final ManagedSystemTrapListener l) {
		synchronized (m_Listeners) {
			m_Listeners.removeListener(l);
		}
	}

	public void receivedTrap(final SnmpPDU pdu) {
		if (m_Logger.isDebugEnabled()) {
			m_Logger.debug(classname + ".receivedTrap(); Trap received: "
					+ pdu.getAgentAddress().getHostAddress());
		}
		boolean foundManagedSystem = false;
		synchronized (m_Listeners) {
			for (final ManagedSystemTrapListener l : m_Listeners) {
				if (l.getAddressInformation().getAddress().equals(
						pdu.getAgentAddress().getHostAddress())) {
					try {
						foundManagedSystem = true;
						l.receivedTrap(pdu);
					} catch (final Exception e) {
						m_Logger.error(classname + ".receivedTrap();", e);
					}
				}
			}
		}
		if (!foundManagedSystem) {
			m_Logger.warn("Dropping SNMP trap since no managed system is registered with address: "
							+ pdu.getAgentAddress().getHostAddress());
			m_Logger.warn("Dropped SNMP trap:\n" + m_MibController.getMibOperations().toString(pdu));
		}
	}

	public String getInterface() {
		return m_BindAddress.getAddress().getHostAddress();
	}

	public long getTotalSNMPTrapsReceived() {
		return m_TotalTrapsReceived;
	}

	public String getInstanceName() {
		return "";
	}

	public void resetSNMPTrapsReceived() {
		m_TotalTrapsReceived = 0;
	}

	public float getIncomingTrapRate() {
		m_Measures.getLastSecond(System.currentTimeMillis());
		final float sum = m_Measures.getSumOfAll();
		if (sum == 0) {
			return 0;
		}
		return (sum / SECONDS);
	}
}
