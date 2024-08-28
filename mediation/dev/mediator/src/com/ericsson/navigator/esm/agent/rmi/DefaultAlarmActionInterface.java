package com.ericsson.navigator.esm.agent.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import org.apache.log4j.Logger;

import com.ericsson.navigator.esm.model.ManagedDataType;
import com.ericsson.navigator.esm.model.alarm.Alarm;
import com.ericsson.navigator.esm.model.alarm.ManagedAlarmList;
import com.ericsson.navigator.esm.util.SupervisionException;

public class DefaultAlarmActionInterface extends UnicastRemoteObject implements
		AlarmActionInterface {

	private static final long serialVersionUID = 1L;
	private final DefaultAgentInterface agentInterface;
	private final DefaultAgentSession session;
	
	private static final String classname = DefaultAlarmActionInterface.class.getName();
	private static Logger logger = Logger.getLogger(classname);

	protected DefaultAlarmActionInterface(final DefaultAgentInterface ai, final DefaultAgentSession session)
			throws RemoteException {
		super();
		this.agentInterface = ai;
		this.session = session;
	}
	
	@SuppressWarnings("unchecked")
	private List<ManagedAlarmList> getAlarmLists(){
		return (List<ManagedAlarmList>)agentInterface.getManagedModel().getManagedDataLists(
				ManagedDataType.Alarm);
	}

	public void synchronize(final List<String> fdns) throws RemoteException,
			SupervisionException {
		if (logger.isDebugEnabled()) {
			logger.debug(classname + ".synchronize();  Session: " + session.getId()
					+ ", " + fdns);
		}
		for (final ManagedAlarmList alarmList : getAlarmLists()) {
			for (final String fdn : fdns) {
				if (fdn.startsWith(alarmList.getFDN() + ',')
						|| fdn.equals(alarmList.getFDN())) {
					alarmList.synchronize();
				}
			}
		}
	}

	public void deleteAlarms(final List<Long> uniqueIds, final String userId)
			throws RemoteException, SupervisionException {
		if (logger.isDebugEnabled()) {
			logger.debug(classname + ".deleteAlarms();  Session: " + session.getId()
					+ ", Delete: " + uniqueIds);
		}
		final StringBuffer failed = new StringBuffer();
		for (final Long uniqueId : uniqueIds) {
			final ManagedAlarmList alarmList = getAlarmList(uniqueId);
			if (alarmList != null) {
				alarmList.changeDataState(uniqueId, Alarm.State.DELETED, userId);
			} else {
				failed.append(uniqueId);
				failed.append(" ");
			}
		}
		DefaultAgentSession.assertFailed(failed, "Alarm(s) not found uniqueId(s): ");
	}

	public void acknowledgeAlarms(final List<Long> uniqueIds,
			final String userId) throws RemoteException, SupervisionException {
		if (logger.isDebugEnabled()) {
			logger.debug(classname + ".acknowledge();  Session: " + session.getId()
					+ ", User : " + userId + " Acknowledge : " + uniqueIds);
		}
		final StringBuffer failed = new StringBuffer();
		for (final Long uniqueId : uniqueIds) {
			final ManagedAlarmList alarmList = getAlarmList(uniqueId);
			if (alarmList != null) {
				alarmList.changeDataState(uniqueId, Alarm.State.ACKNOWLEDGED,
						userId);
			} else {
				failed.append(uniqueId);
				failed.append(" ");
			}
		}
		DefaultAgentSession.assertFailed(failed, "Alarm(s) not found uniqueId(s): ");
	}
	
	private ManagedAlarmList getAlarmList(final long uniqueId){
		return (ManagedAlarmList) 
			agentInterface.getManagedModel().getManaged(
					uniqueId, ManagedDataType.Alarm);
	}

	public void unAcknowledgeAlarms(final List<Long> uniqueIds,
			final String userId) throws RemoteException, SupervisionException {
		if (logger.isDebugEnabled()) {
			logger.debug(classname + ".unAcknowledge();  Session: " + session.getId()
					+ ", User : " + userId + " Unacknowledge : " + uniqueIds);
		}
		final StringBuffer failed = new StringBuffer();
		for (final Long uniqueId : uniqueIds) {
			final ManagedAlarmList alarmList = getAlarmList(uniqueId);
			if (alarmList != null) {
				alarmList.changeDataState(uniqueId, Alarm.State.UNACKNOWLEDGED,
						userId);
			} else {
				failed.append(uniqueId);
				failed.append(" ");
			}
		}
		DefaultAgentSession.assertFailed(failed, "Alarm(s) not found uniqueId(s): ");
	}

	public void synchronizeAll() throws RemoteException, SupervisionException {
		logger.debug(classname + ".synchronizeAll();  Session: " + session.getId());
		for (final ManagedAlarmList alarmList : getAlarmLists()) {
			alarmList.synchronize();
		}
	}

}
