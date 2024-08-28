package com.ericsson.navigator.esm.manager.snmp;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.adventnet.snmp.snmp2.SnmpException;
import com.adventnet.snmp.snmp2.SnmpMessage;
import com.adventnet.snmp.snmp2.SnmpPDU;
import com.ericsson.navigator.esm.MVM;
import com.ericsson.navigator.esm.model.alarm.snmp.SNMPAddressInformation;
import com.ericsson.navigator.esm.util.component.Component;
import com.ericsson.navigator.esm.util.component.ComponentInitializationException;
import com.ericsson.navigator.esm.util.component.ComponentShutdownException;

public class AsynchronousSnmpRequestHandler extends Thread implements Component {

	private final List<RequestChannel> m_Channels;
	private Selector m_Selector = null;
	private final static int NETWORKELEMENTSPERPORT = 5000;
	private final static int MAXNROFPORTS = Integer.getInteger("PORTS", 10);
	private final static int SENDBUFFERSIZE = 1024 * Integer.getInteger(
			"SENDSIZE", 10000);
	private final static int RECEIVEBUFFERSIZE = 1024 * Integer.getInteger(
			"RECEIVESIZE", 10000);
	private final ThreadPoolExecutor m_ThreadPoolExecutor;
	private static final int PDUBUFFERSIZE = Integer.getInteger(
			"PDUSIZE", 1024*65); //65KB
	private static final AsynchronousSnmpRequestHandler INSTANCE = new AsynchronousSnmpRequestHandler();
	
	private final String classname = getClass().getName();

	public static AsynchronousSnmpRequestHandler getInstance() {
		return INSTANCE;
	}

	private AsynchronousSnmpRequestHandler() {
		m_Channels = new Vector<RequestChannel>();
		m_ThreadPoolExecutor = new ThreadPoolExecutor(MAXNROFPORTS,
				MAXNROFPORTS * 2, 180, TimeUnit.SECONDS,
				new ArrayBlockingQueue<Runnable>(100000));
	}

	@Override
	public void interrupt() {
		super.interrupt();
		m_Selector.wakeup();
		try {
			m_Selector.close();
		} catch (final IOException e) {
			MVM.logger.error(classname+".interrupt();", e);
		}
	}

	@Override
	public void run() {
		while (!isInterrupted()) {
			try {
				if (m_Selector.select(100) > 0) {
					final Iterator<SelectionKey> keysIterator = m_Selector
							.selectedKeys().iterator();
					while (keysIterator.hasNext()) {
						final SelectionKey key = keysIterator.next();
						keysIterator.remove();
						if (!key.isReadable()) {
							continue;
						}
						handleRequest(key);
					}
				}
			} catch (final IOException e) {
				MVM.logger.error(classname+".run(); Error handling incoming SNMP response", e);
			} catch (final SnmpException e) {
				MVM.logger.error(classname+".run(); Error parsing incoming SNMP response", e);
			}
		}
	}

	private void handleRequest(final SelectionKey key) throws IOException,
			SnmpException {
		final DatagramChannel channel = (DatagramChannel) key.channel();
		final ByteBuffer buffer = ByteBuffer.allocate(PDUBUFFERSIZE);
		final InetSocketAddress address = (InetSocketAddress) channel
				.receive(buffer);
		if (address == null) {
			return;
		}
		SnmpMessage message = null;
		final byte[] b = new byte[buffer.position()];
		System.arraycopy(buffer.array(), 0, b, 0, buffer.position());
		message = new SnmpMessage(b);
		final SnmpPDU pdu = message.getPDU();
		final RequestChannel requestChannel = (RequestChannel)key.attachment();
		final AsynchronousSnmpRequest request = requestChannel.getRequest(pdu);
		if(request != null){
			handleRequest(request, pdu);
		}else{
			MVM.logger.error(classname+".handleRequest(); Response received after request timed out from "
					+ address.getAddress().getHostAddress()
					+ ", pdu reqID:" + pdu.getReqid());
		}
	}
	
	private void handleRequest(final AsynchronousSnmpRequest request, final SnmpPDU pdu){
		m_ThreadPoolExecutor.execute(new Runnable() {
			public void run() {
				request.handleResponse(pdu);
			}
		});
	}

	private RequestChannel getLeastLoadedChannel() throws IOException {
		RequestChannel leastLoadedChannel = null;
		int lowestAllocateCount = NETWORKELEMENTSPERPORT;
		synchronized (m_Channels) {
			for (final RequestChannel channel : m_Channels) {
				if (channel.getAllocateCount() < lowestAllocateCount) {
					leastLoadedChannel = channel;
					lowestAllocateCount = channel.getAllocateCount();
				}
			}
		}
		if (leastLoadedChannel != null) {
			return leastLoadedChannel;
		} else {
			throw new IOException("No available channel!");
		}
	}

	private void createChannel() throws IOException {
		final RequestChannel channel = new RequestChannel();
		final DatagramSocket socket = channel.getChannel().socket();
		socket.bind(null);
		socket.setReceiveBufferSize(RECEIVEBUFFERSIZE);
		socket.setSendBufferSize(SENDBUFFERSIZE);
		channel.getChannel().configureBlocking(false);
		channel.getChannel().register(m_Selector, SelectionKey.OP_READ,
				channel);
		m_Selector.wakeup();
		synchronized (m_Channels) {
			m_Channels.add(channel);
		}
	}
	
	@Override
	public void shutdown() throws ComponentShutdownException {
		interrupt();
	}
	
	@Override
	public String getComponentName() {
		return AsynchronousSnmpRequestHandler.class.getSimpleName();
	}

	public void initialize() throws ComponentInitializationException {
		if (m_Selector == null) {
			try {
				m_Selector = Selector.open();
			} catch (IOException e) {
				throw new ComponentInitializationException(
						"Failed to initialize SNMP request handler." , e, true);
			}
		}
		for (int i = 0; i < MAXNROFPORTS; ++i) {
			try {
				createChannel();
			} catch (final IOException e) {
				MVM.logger.error(classname+".initialize(); Failed to create channel.", e);
			}
		}
	}

	private RequestChannel allocateChannel(
			final SNMPProtocol<?, ?, ? extends SNMPAddressInformation> ms)
			throws IOException {
		final RequestChannel channel = getLeastLoadedChannel();
		ms.setRequestChannel(channel);
		channel.allocate();
		return channel;
	}

	public void addSNMPRequest(final AsynchronousSnmpRequest request)
			throws IOException {
		RequestChannel channel = request.getManagedSystem().getRequestChannel();
		if (channel == null) {
			channel = allocateChannel(request.getManagedSystem());
		}
		channel.addRequest(request);
		synchronized (this) {
			if (!isAlive()) {
				start();
			}
		}
	}
}
