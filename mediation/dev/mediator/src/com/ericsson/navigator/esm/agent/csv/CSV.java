package com.ericsson.navigator.esm.agent.csv;

import java.io.PrintStream;
import java.rmi.RemoteException;
import java.util.List;

import com.ericsson.navigator.esm.agent.Agent;
import com.ericsson.navigator.esm.model.ManagedDataEvent;
import com.ericsson.navigator.esm.model.ManagedDataType;
import com.ericsson.navigator.esm.util.Environment;
import com.ericsson.navigator.esm.util.csv.CSVPrinter;

public class CSV extends Agent {

	private static final long serialVersionUID = 1L;
	private static boolean m_InitialOnly = false;
	private static boolean m_SpontanousOnly = false;
	private static PrintStream m_OutStream = null;
	private static PrintStream m_ErrorStream = null;
	private static ManagedDataType[] types = ManagedDataType.values();

	public CSV(final String[] args, final String agentInteface,
			final String host, final int port, final PrintStream outStream,
			final PrintStream errorStream) {
		super("CSV", agentInteface, host, port);
		m_OutStream = outStream;
		m_ErrorStream = errorStream;
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				m_Session = null;
				System.gc();
			}
		}));
		for(String arg : args){
			if (arg.equals("-i")) {
				m_InitialOnly = true;
			} else if (arg.equals("-s")) {
				m_SpontanousOnly = true;
			} else if (arg.equals("-pm")) {
				types = new ManagedDataType[]{ManagedDataType.CounterSet};//NOPMD
			} else if (arg.equals("-alarm")) {
				types = new ManagedDataType[]{ManagedDataType.Alarm};//NOPMD
			}
		}
	}

	void initialize() {
		connect(types, !m_SpontanousOnly);
		listenForAlarms();// NOPMD
	}

	private void listenForAlarms() {
		while (!m_Exiting) {
			final StringBuffer alarmTexts = new StringBuffer();// NOPMD
			try {
				final List<ManagedDataEvent<?>> events = m_Session.getEvents();
				for (final ManagedDataEvent<?> event : events) {
					if (isInitialEnd(event)) {
						printEvents(alarmTexts.toString());
						end();
					}
					switch (event.getAction()) {
					case ADD:
					case DELETE:
					case INITIAL:
					case UPDATE:
						CSVPrinter.print(event, alarmTexts);
						alarmTexts.append("\n");
					}
				}
			} catch (final RemoteException e) {
				error("Lost connection with ESM!", null);
				System.exit(4);
			}
			printEvents(alarmTexts.toString());
		}
		end();
	}

	private void printEvents(final String text) {
		if (text.length() != 0) {
			m_OutStream.print(text);
			m_OutStream.flush();
		}
	}

	private void end() {
		// Release reference
		m_Session = null;
		System.gc();
		disconnect(ExitCode.NORMAL);
	}

	@Override
	public void disconnect(final ExitCode code) {
		super.disconnect(code);
		exit(code);
	}

	/**
	 * 0 Normal termination (No problems detected). 1 Termination due to
	 * disturbance or problem in the communication with the management system. 2
	 * Exit due to serious internal communication or communication with NE. 3
	 * Exit due to environmental problem. 4 Termination after local shut down. 5
	 * Exit due to processing error. 6 Exit due to authentication failure or
	 * lack of authority to use a resource. 7 Exit since functionality not
	 * supported by current implementation. 8 Exit due to database or other
	 * storage problems. 9-31 <Reserved for future definitions> 32-63 <Agent
	 * implementation specific numbers>
	 */
	void exit(final ExitCode code) {
		switch (code) {
		case AGENTINTERFACE_NOT_FOUND:
		case REGISTRY_NOT_FOUND:
		case CONNECTION_BROKEN:
			System.exit(1); // Connection problem
			break;
		case AGENT_ERROR:
			System.exit(5); // Input error
			break;
		default:
			System.exit(0);
		}
	}

	private boolean isInitialEnd(final ManagedDataEvent<?> event) {
		return m_InitialOnly
				&& event.getAction().equals(ManagedDataEvent.Action.INITIAL_END)
				&& event.getData().getManagedObjectInstance().equals("");
	}

	public static void main(final String[] args) {
		new CSV(args, Environment.AGENTINTERFACE, Environment.REGISTRYHOST, Environment.REGISTRYPORT,
				System.out, System.err).initialize();
	}

	@Override
	protected void error(final String error, final Exception e) {
		m_ErrorStream.println(error);
		if (e != null) {
			e.printStackTrace(m_ErrorStream);
		}
	}
}
