package com.ericsson.navigator.esm.agent.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.server.Unreferenced;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.ericsson.navigator.esm.model.ManagedDataEvent;
import com.ericsson.navigator.esm.model.ManagedDataList;
import com.ericsson.navigator.esm.model.ManagedDataListener;
import com.ericsson.navigator.esm.model.ManagedDataMarker;
import com.ericsson.navigator.esm.model.ManagedDataType;
import com.ericsson.navigator.esm.model.ManagedModelEvent;
import com.ericsson.navigator.esm.model.ManagedModelListener;
import com.ericsson.navigator.esm.model.ManagedDataEvent.Action;
import com.ericsson.navigator.esm.util.SupervisionException;
import com.ericsson.navigator.esm.util.jmx.BeanServerHelper;
import com.ericsson.navigator.esm.util.rate.Measures;


/**
 * This class implements an default agent session.
 * 
 * It initially reads all the alarms from the managed systems in the managed
 * system model into an infinite sized queue. It then swaps this queue into an
 * fixed size queue to be able to detect slow consumers.
 */
@SuppressWarnings("unchecked")
public class DefaultAgentSession extends UnicastRemoteObject implements 
		Unreferenced, AgentSession, ManagedDataListener, ManagedModelListener, DefaultAgentSessionMBean {

	private static final long serialVersionUID = 1L;
	private final DefaultAgentInterface agentInterface;
	private final String id;
	private final LinkedBlockingQueue<ManagedDataEvent<?>> eventQueue;
	private LinkedBlockingQueue<ManagedDataEvent<?>> m_NonInitialBuffer;
	private boolean m_IsUnreferenced = false;
	private boolean m_BufferNonInitial = false;
	private boolean incudeInitial;
	private int m_AlarmsDelivered = 0;
	private final Measures m_Measures;
	private final List<ManagedDataType> types;
	private final static int SECONDS = 5;
	private final AlarmActionInterface alarmActionInterface;


	private static final String classname = DefaultAgentSession.class.getName();
	private static Logger m_Logger = Logger.getLogger(classname);

	public DefaultAgentSession(final String id, final DefaultAgentInterface ai,
			final ManagedDataType[] types, final boolean includeInitial) throws RemoteException {
		super();
		this.id = id;
		this.types = Arrays.asList(types);
		this.agentInterface = ai;
		this.alarmActionInterface = new DefaultAlarmActionInterface(ai, this);
		eventQueue = new LinkedBlockingQueue<ManagedDataEvent<?>>(500000);
		this.incudeInitial = includeInitial;
		m_Measures = new Measures(SECONDS);
		BeanServerHelper.registerMDynamicBean(this, DefaultAgentSessionMBean.class);
	}

	void initialize() {
		synchronized (agentInterface.getManagedModel()) {
			bufferNonInitial();
			addStartEvent("");
			for(final ManagedDataType type : types){
				for (final ManagedDataList protocol : agentInterface.getManagedModel().getManagedDataLists(type)) {
					protocol.addManagedDataListener(this, incudeInitial);
				}
			}
			addEndEvent("");
			sendNonInitialBuffer();
			agentInterface.getManagedModel().addManagedModelListener(this);
		}
	}
	
	private synchronized void bufferNonInitial(){
		m_BufferNonInitial = true;
		m_NonInitialBuffer = new LinkedBlockingQueue<ManagedDataEvent<?>>();
	}

	private synchronized void sendNonInitialBuffer() {
		eventQueue.addAll(m_NonInitialBuffer);
		m_BufferNonInitial = false;
		m_NonInitialBuffer = null;
	}

	private void addStartEvent(final String fdn) {
		if(incudeInitial){
			pushDataEvent(new ManagedDataEvent(new ManagedDataMarker(fdn),
				ManagedDataEvent.Action.INITIAL_START));
		}
	}

	private void addEndEvent(final String fdn) {
		if(incudeInitial){
			pushDataEvent(new ManagedDataEvent(new ManagedDataMarker(fdn),
					ManagedDataEvent.Action.INITIAL_END));
		}
	}

	public String getId() {
		return id;
	}

	public List<ManagedDataEvent<?>> getEvents() throws RemoteException {
		final List<ManagedDataEvent<?>> events = new Vector<ManagedDataEvent<?>>();
		try {
			final ManagedDataEvent<?> event = eventQueue.poll(30, TimeUnit.SECONDS);
			if (event == null) {
				return events;
			}
			events.add(event);
			if (eventQueue.size() < 1000) {
				Thread.sleep(100);
			}
			eventQueue.drainTo(events, 1000);
			m_AlarmsDelivered += events.size();
			m_Measures.addValue(System.currentTimeMillis(), events.size());
		} catch (final InterruptedException e) {/* Totally fine */
		}
		if (m_Logger.isDebugEnabled()) {
			m_Logger.debug(classname + ".getEvents(); Session: " + getId()
					+ ", Events sent: " + events.size());
		}
		return events;
	}

	public synchronized void pushDataEvent(final ManagedDataEvent event) {
		if (m_IsUnreferenced) {
			// Lets not add more into this queue as this object will
			// be garbage collected soon.
			return;
		}
		final ManagedDataEvent serializableEvent = new ManagedDataEvent(
				event.getData().getSerializable(), event.getAction(), event.stateChanged());
		if (m_BufferNonInitial && !event.getAction().equals(Action.INITIAL) 
				&& !event.getAction().equals(Action.INITIAL_END) 
				&& !event.getAction().equals(Action.INITIAL_START)) {
			m_NonInitialBuffer.add(serializableEvent);
		} else if (!eventQueue.offer(serializableEvent)) {
			m_Logger.error(classname
							+ ".pushDataEvent(); Event queue is full for agent session with ID "
							+ id);
			unreferenced();
		}
	}

	/**
	 * Remove all references except the alarm listener registration as those are
	 * weak references and will be handled by GC.
	 */
	public void unreferenced() {
		if(m_Logger.isDebugEnabled()){
			m_Logger.debug(classname
					+ ".unreferenced(); --> ");
		}
		BeanServerHelper.unRegisterMBean(this);
		m_IsUnreferenced = true;
		agentInterface.remove(this);
		if(m_Logger.isDebugEnabled()){
			m_Logger.debug(classname
					+ ".unreferenced(); <-- ");
		}
	}

	

	public void systemAdded(final ManagedModelEvent event) {
		event.getManaged().addManagedDataListener(this, true);
	}

	public void systemRemoved(final ManagedModelEvent event) {
		event.getManaged().removeManagedDataListener(this);
	}
	
	public void reinitialize() throws RemoteException {
		final DefaultAgentSession session = this;
		final Thread reinitializeThread = new Thread("Reinitalize Thread: " + id){
			public void run() {
				incudeInitial = true;
				final List<? extends ManagedDataList> managedList = agentInterface.getManagedModel().getManagedDataLists(ManagedDataType.Alarm);
				synchronized (managedList) {
					for (final ManagedDataList managed : managedList) {
						managed.removeManagedDataListener(session);
					}
					agentInterface.getManagedModel().removeManagedModelListener(session);
					initialize();
				}
			}
		};
		reinitializeThread.start();
	}

	public void reinitialize(final List<String> fdns) throws RemoteException, SupervisionException {
		incudeInitial = true;
		/*
		final StringBuffer failed = new StringBuffer();
		for(String fdn : fdns){
			final List<ManagedDataList> managedLists = (List<ManagedDataList>) agentInterface.getManagedModel().getManaged(fdn);
			for(ManagedDataList managed : managedLists){
				if(managed != null){
					managed.removeManagedDataListener(this);
					addStartEvent(managed.getFDN());
					managed.addManagedDataListener(this, true);
					addEndEvent(managed.getFDN());
				} else {
					failed.append(fdn);
					failed.append(" ");
				}
			}
		}
		assertFailed(failed, "No managed system(s) available with FDN(s): ");
		*/
	} 
	
	static void assertFailed(final StringBuffer failed, final String message)
		throws SupervisionException {
		if (failed.length() > 0) {
			throw new SupervisionException(message + failed.toString());
		}
	}

	public String getID() {
		return id;
	}

	public String getInstanceName() {
		return "AgentSession."+id;
	}

	public int getEventQueueSize() {
		return eventQueue.size();
	}

	public int getEventsDelivered() {
		return m_AlarmsDelivered;
	}

	public float getOutgoingEventRate() {
		m_Measures.getLastSecond(System.currentTimeMillis());
		final float sum = m_Measures.getSumOfAll();
		if(sum == 0){
			return 0;
		}
		return (sum/SECONDS);
	}

	@Override
	public AlarmActionInterface getAlarmActionInterface()
			throws RemoteException {
		return alarmActionInterface;
	}

	@Override
	public List<ManagedDataType> getTypes() {
		return types;
	}

}
