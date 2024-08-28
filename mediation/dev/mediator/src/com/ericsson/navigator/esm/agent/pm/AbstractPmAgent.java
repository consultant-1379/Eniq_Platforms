package com.ericsson.navigator.esm.agent.pm;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.log4j.Logger;

import com.ericsson.navigator.esm.agent.rmi.AgentInterface;
import com.ericsson.navigator.esm.agent.rmi.AgentSession;
import com.ericsson.navigator.esm.agent.rmi.DefaultAgentSession;
import com.ericsson.navigator.esm.model.ManagedDataEvent;
import com.ericsson.navigator.esm.model.ManagedDataType;
import com.ericsson.navigator.esm.model.ManagedDataEvent.Action;
import com.ericsson.navigator.esm.model.pm.CounterSet;
import com.ericsson.navigator.esm.util.component.ComponentInitializationException;
import com.ericsson.navigator.esm.util.component.ComponentShutdownException;

public abstract class AbstractPmAgent implements PmAgent {

	private static final String classname = AbstractPmAgent.class.getName();
	private static Logger logger = Logger.getLogger(classname);

	private static final ManagedDataType[] types = new ManagedDataType[] { ManagedDataType.CounterSet };
	
	private final AgentInterface agentInterface;
	private AgentSession session = null;
	private boolean exiting = false;

	public AbstractPmAgent(final AgentInterface agentInterface) {
		this.agentInterface = agentInterface;
	}

	@Override
	public void initialize() throws ComponentInitializationException{
		if (agentInterface != null) {
			logger.debug(classname+".initalize; -->");
			try {
				session = agentInterface.connect(getAgentId(), types, false);
				listenForCounterSets();
			} catch (RemoteException e) {
				logger.error(classname+".initialize; Failed to connect to agent interface. No PM ", e);
			}
		}
	}

	@Override
	public void shutdown() throws ComponentShutdownException{
		logger.debug(classname+".shutdown; -->");
		exiting = true;
		if (session != null) {
			((DefaultAgentSession)session).unreferenced();
		}
	}

	protected abstract String getAgentId();

	private void listenForCounterSets() {
		final Thread thread = new Thread(getAgentId()){
			@Override
			public void run() {
				while (!exiting ) {
					try {
						final List<ManagedDataEvent<?>> events = session.getEvents();
						for (final ManagedDataEvent<?> event : events) {
							if(event.getData().getDataType().equals(ManagedDataType.CounterSet) 
									&& (event.getAction().equals(Action.ADD) || 
											event.getAction().equals(Action.UPDATE))) {
								//final CounterSet counterSet = (CounterSet) event.getData();
								//process(counterSet);
							}
						}
					} catch (final RemoteException e) {
						logger.warn(classname+".listenForCounterSets; Connection lost", e);
					}
				}
			}
		};
		thread.setDaemon(true);
		thread.start();
	}
}
