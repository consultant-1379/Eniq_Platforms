package com.ericsson.navigator.esm.model.alarm;

import java.util.List;

import com.ericsson.navigator.esm.model.ManagedDataList;
import com.ericsson.navigator.esm.util.SupervisionException;

public interface ManagedAlarmList extends ManagedDataList<Alarm, Alarm.State> {

	void synchronize() throws SupervisionException;
	void correlateSync(final List<Alarm> syncAlarmList);
	void abortSync(final String reason);
	boolean hasAlarm(Long uniqueId);
	boolean isSynchable();
	int getAlarmCount();

}
