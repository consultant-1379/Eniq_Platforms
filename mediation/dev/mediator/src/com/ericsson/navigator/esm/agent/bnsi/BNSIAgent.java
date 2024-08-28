package com.ericsson.navigator.esm.agent.bnsi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import com.ericsson.navigator.esm.agent.Agent;
import com.ericsson.navigator.esm.model.ManagedData;
import com.ericsson.navigator.esm.model.ManagedDataEvent;
import com.ericsson.navigator.esm.model.ManagedDataType;
import com.ericsson.navigator.esm.model.alarm.AbstractAlarm;
import com.ericsson.navigator.esm.model.alarm.Alarm;
import com.ericsson.navigator.esm.util.Environment;
import com.ericsson.navigator.esm.util.SupervisionException;

public class BNSIAgent extends Agent {

	private static final long serialVersionUID = 1L;
	private Timer m_HBTimer = null;
	private boolean m_Synchronize = false;
	public static final SimpleDateFormat TIMEFORMATTER = new SimpleDateFormat(
			"yyyyMMddHHmmss");
	private Thread m_ActionThread = null;
	private boolean m_32Bit = false;

	public enum Response {
		Executed, Rejected,
	}

	public BNSIAgent(final String[] args, final String agentInterface,
			final String host, final int port) {
		super((args.length > 0 ? args[0] : ""), agentInterface, host, port);
		parseArgs(args);// NOPMD
	}

	public void initialize() {
		addShutdownHook();
		connect(new ManagedDataType[]{ManagedDataType.Alarm}, m_Synchronize);
		startActionReader();
		print("#Version=3");
		check32bit();
		listenForAlarms();// NOPMD
	}

	private void check32bit() {
		if (m_32Bit) {
			print("#Running in 32 bit mode. Actions are not supported!");
		}
	}

	private void addShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				cleanup(ExitCode.NORMAL);
			}
		}));
	}

	private void cleanup(final ExitCode code) {
		if (m_ActionThread != null) {
			m_ActionThread.interrupt();
		}
		if (m_HBTimer != null) {
			m_HBTimer.cancel();
		}
		super.disconnect(code);
	}

	@Override
	public void disconnect(final ExitCode code) {
		cleanup(code);
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

	private void startActionReader() {
		m_ActionThread = new Thread(new Runnable() {
			public void run() {
				readActions();
			}
		}, "Action Reader Thread");
		m_ActionThread.start();
	}

	private void readActions() {
		final BufferedReader in = new BufferedReader(new InputStreamReader(
				System.in));
		while (!m_Exiting) {
			try {
				if(!readAction(in)){
					return; //EOF
				}
			} catch (IOException e) {
				error(e);
			} catch (InvalidActionException e) {
				error(e);
			} catch (SupervisionException e) {
				error(e);
			}
		}
	}

	private void error(final Exception e) {
		if (!m_Exiting) {
			printError(e.getMessage());
			printResponse(Response.Rejected);
		}
	}

	private boolean readAction(final BufferedReader in) throws IOException,
			InvalidActionException, RemoteException, SupervisionException {
		String line = null;
		while ((line = in.readLine()) != null) {
			if (line.equals("*Synchronize") || line.startsWith("*XSynchronize")) {
				m_Session.reinitialize();
			} else if (line.startsWith("*Synchronize=")) {
				synchronize(getID(line));
			} else if (line.startsWith("*Acknowledge=")) {
				m_AlarmActionInterface.acknowledgeAlarms(getIdList(getID(line)), m_Name);
			} else if (line.startsWith("*Unacknowledge=")) {
				m_AlarmActionInterface.unAcknowledgeAlarms(getIdList(getID(line)), m_Name);
			} else if (line.startsWith("*Terminate=")) {
				m_AlarmActionInterface.deleteAlarms(getIdList(getID(line)), m_Name);
			} else {
				printResponse(Response.Rejected);
				continue;
			}
			printResponse(Response.Executed);
		}
		return line != null; //EOF?
	}

	private void synchronize(final String managedSystem)
			throws RemoteException, SupervisionException {
		final List<String> list = new Vector<String>();
		list.add(managedSystem);
		m_Session.reinitialize(list);
	}

	/**
	 * #Response=Executed Action fully completed. #Response=Rejected Action
	 * failed, but no resources were affected by the action, since nothing was
	 * done or a rollback was performed. #Response=Failed Action failed, but
	 * resources may be affected by the attempted action. This response may also
	 * be given if the action was partly completed. #Response=Ordered Queue to
	 * be done later, or action takes considerable time to complete execution.
	 * This type of response must be followed by a response of another type,
	 * e.g. Executed. #Response=Denied Authority check denied execution or it
	 * may be due to lack of resources in the local host or environment of the
	 * agent. The agent may also in this case use the response Rejected.
	 */
	private void printResponse(final Response response) {
		final StringBuffer responseBuffer = new StringBuffer();
		responseBuffer.append("\n#Response=");
		switch (response) {
		case Executed:
			responseBuffer.append("Executed");
			break;
		default:
			responseBuffer.append("Rejected");
		}
		print(responseBuffer.toString());
	}

	private List<Long> getIdList(final String id) throws InvalidActionException {
		try {
			final List<Long> list = new Vector<Long>();
			list.add(Long.parseLong(id));
			return list;
		} catch (NumberFormatException e) {
			throw new InvalidActionException("Invalid NotificationIdentifier: "
					+ id);
		}
	}

	private String getID(final String s) throws InvalidActionException {
		int index = s.indexOf('=');
		if (index == -1) {
			throw new InvalidActionException("Missing equals sign in action: "
					+ s);
		}
		if (++index == s.length()) {
			throw new InvalidActionException("Empty ID in action: " + s);
		}
		return s.substring(index);
	}

	private void parseArgs(final String[] args) {
		if (args.length > 0) {
			for (int i = 1; i < args.length; ++i) {
				final String s = args[i];
				if (s.equals("-sync") || s.equals("-xsync")) {
					m_Synchronize = true;
				}
				if (s.equals("-hbint") && args.length > ++i) {
					setHB(args[i]);
				}
				if (s.equals("-32bit")) {
					m_32Bit = true;
				}
			}
		} else {
			usage();
			exit(ExitCode.AGENT_ERROR);
		}
	}

	private void setHB(final String arg) {
		try {
			final int interval = Integer.parseInt(arg);
			if (interval > 0) {
				startHB(interval);
			} else {
				printError("Invalid HB interval: " + interval);
				exit(ExitCode.AGENT_ERROR);
			}
		} catch (final NumberFormatException e) {
			printError("HB interval must be an valid integer!");
		}
	}

	private void listenForAlarms() {
		while (!m_Exiting) {
			final StringBuffer alarmTexts = new StringBuffer();// NOPMD
			try {
				for (final ManagedDataEvent<?> event : m_Session.getEvents()) {
					printAlarm(event, alarmTexts);
				}
			} catch (final RemoteException e) {
				printError("Error retrieving alarms!");
				exit(ExitCode.CONNECTION_BROKEN);
			}
			final String text = alarmTexts.toString();
			if (text.length() != 0) {
				print(text);
			}
		}
	}

	private void print(final String s) {
		System.out.println(s);
		if (System.out.checkError()) {
			exit(ExitCode.CONNECTION_BROKEN);
		}
	}

	private void usage() {
		printError(Environment.BNSI_USAGE);
	}

	private void startHB(final int interval) {
		m_HBTimer = new Timer();
		final int halfInterval = interval * 1000;
		m_HBTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				print("#HB");
			}
		}, halfInterval, halfInterval);
	}

	public static void main(final String[] args) {
		final BNSIAgent agent = new BNSIAgent(args, Environment.AGENTINTERFACE,
				Environment.REGISTRYHOST, Environment.REGISTRYPORT);
		agent.initialize();
	}

	private void printError(final String error) {
		final StringBuffer errorBuffer = new StringBuffer();
		errorBuffer.append("\n#Error=");
		errorBuffer.append(error);
		print(errorBuffer.toString());
	}

	private RecordType getSyncRecordType(final Alarm alarm) {
		if (alarm.getRecordType().equals(Alarm.RecordType.EVENT)) {
			return RecordType.SYNC_EVENT;
		} else {
			return RecordType.SYNC_ALARM;
		}
	}

	private class ClearAlarm extends AbstractAlarm {

		private static final long serialVersionUID = 1L;

		public ClearAlarm(final Alarm alarm) {
			super(alarm);
			m_PerceivedSeverity = PerceivedSeverity.CLEARED;
		}
	}

	private void printAlarm(final ManagedDataEvent<?> event,
			final StringBuffer alarmTexts) {
		switch (event.getAction()) {
		case INITIAL:
			//printAlarm((Alarm)event.getData(), getSyncRecordType((Alarm)event.getData()),alarmTexts);
			break;
		case INITIAL_START:
			printSync(event.getData(), RecordType.START_SYNC, alarmTexts);
			break;
		case INITIAL_END:
			printSync(event.getData(), RecordType.END_SYNC, alarmTexts);
			break;
		case DELETE:
			//final Alarm alarm = new ClearAlarm((Alarm)event.getData());
			//printAlarm(alarm, getRecordType(alarm.getRecordType()), alarmTexts);
			break;
		case UPDATE:
			if(forwardAcknowledge(event)){
				//printAlarm((Alarm)event.getData(), RecordType.REPEATED, alarmTexts);
			}
			break;
		default:
			//printAlarm((Alarm)event.getData(), getRecordType(((Alarm)event.getData()).getRecordType()), alarmTexts);
		}
	}

	private boolean forwardAcknowledge(final ManagedDataEvent<?> event) {
		return !(m_32Bit && event.stateChanged());
	}

	private void printSync(final ManagedData<?, ?> data, final RecordType recordType,
			final StringBuffer alarmTexts) {
		alarmTexts.append("\n%a");
		alarmTexts.append("\n-RecordType=");
		alarmTexts.append(recordType.getValue());
		alarmTexts.append("\n-ObjectOfReference=");
		alarmTexts.append(m_Name);
		if (data.getManagedObjectInstance() != null
				&& data.getManagedObjectInstance().length() != 0) {
			alarmTexts.append(',');
			alarmTexts.append(data.getManagedObjectInstance());
		}
		alarmTexts.append("\n%A");

	}

	private void printAlarm(final Alarm alarm, final RecordType recordType,
			final StringBuffer alarmTexts) {
		alarmTexts.append("\n%a");
		alarmTexts.append("\n-NotificationIdentifier=");
		alarmTexts.append(modifyUnique(alarm.getUniqueId()));
		if (alarm.getAlarmId() != null) {
			alarmTexts.append("\n-AlarmNumber=");
			alarmTexts.append(modifyUnique(alarm.getUniqueId()));
		}
		alarmTexts.append("\n-RecordType=");
		alarmTexts.append(recordType.getValue());
		if (alarm.getEventTime() != null) {
			alarmTexts.append("\n-EventTime=");
			alarmTexts.append(TIMEFORMATTER.format(alarm.getEventTime()));
		}
		alarmTexts.append("\n-ObjectOfReference=");
		alarmTexts.append(m_Name);
		if (alarm.getManagedObjectInstance() != null
				&& alarm.getManagedObjectInstance().length() != 0) {
			alarmTexts.append(',');
			alarmTexts.append(alarm.getManagedObjectInstance());
		}
		alarmTexts.append("\n-SpecificProblemText=");
		alarmTexts.append(alarm.getSpecificProblem());
		alarmTexts.append("\n-ProbableCauseText=");
		alarmTexts.append(alarm.getProbableCause());
		alarmTexts.append("\n-EventTypeText=");
		alarmTexts.append(alarm.getEventType());
		alarmTexts.append("\n-PerceivedSeverity=");
		alarmTexts.append(alarm.getPerceivedSeverity().ordinal());
		alarmTexts.append("\n-CorrelationCount=");
		alarmTexts.append(alarm.getCorrelationCount());
		appendState(alarm, alarmTexts);
		if (alarm.getAdditionalText() != null) {
			alarmTexts.append("\n");
			alarmTexts.append(alarm.getAdditionalText());
		}
		alarmTexts.append("\n%A");
	}

	/**
	 * Not all NMS systems might support 64 bit long for the unique ID.
	 * Therefore it is possible to shorten the string to 32 bit if the FDN is
	 * also used to make the alarm unique. Note that operations are not
	 * supported when running in 32 bit mode.
	 * 
	 * @param uniqueId
	 *            The unique ID of the alarm.
	 * @return A String of either the unique ID or the hashed 32 bit version of
	 *         the unique ID.
	 */
	private String modifyUnique(final long uniqueId) {
		if (m_32Bit) {
			return String.valueOf(new Long(uniqueId).hashCode());
		}
		return String.valueOf(uniqueId);
	}

	/**
	 * -Acknowledge=0 Not acknowledged -Acknowledge=1 Acknowledged
	 * -Acknowledge=2 Acknowledgment regretted
	 */
	private void appendState(final Alarm alarm, final StringBuffer alarmTexts) {
		switch (alarm.getState()) {
		case ACKNOWLEDGED:
			alarmTexts.append("\n-Acknowledge=");
			alarmTexts.append(1);
			alarmTexts.append("\n-AcknowledgeUser=");
			alarmTexts.append(alarm.getStateUser());
			alarmTexts.append("\n-AcknowledgeTime=");
			alarmTexts.append(TIMEFORMATTER.format(alarm.getStateTime()));
			break;
		case DELETED:
			alarmTexts.append("\n-TerminationUser=");
			alarmTexts.append(alarm.getStateUser());
			alarmTexts.append("\n-TerminationTime=");
			alarmTexts.append(TIMEFORMATTER.format(alarm.getStateTime()));
		default:
			alarmTexts.append("\n-Acknowledge=");
			alarmTexts.append(0);
		}
	}

	private RecordType getRecordType(final Alarm.RecordType recordType) {
		if (recordType.equals(Alarm.RecordType.EVENT)) {
			return RecordType.EVENT;
		} else if (recordType.equals(Alarm.RecordType.HEARTBEAT_ALARM)) {
			return RecordType.HEARTBEAT;
		} else if (recordType.equals(Alarm.RecordType.SYNC_ABORTED)) {
			return RecordType.END_SYNC;
		} else {
			return RecordType.ALARM;
		}
	}

	@Override
	protected void error(final String error, final Exception e) {
		printError(error);
	}
	
	
}
