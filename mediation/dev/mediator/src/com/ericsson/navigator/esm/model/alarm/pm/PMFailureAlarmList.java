package com.ericsson.navigator.esm.model.alarm.pm;

import com.ericsson.navigator.esm.model.DefaultAddressInformation;
import com.ericsson.navigator.esm.model.alarm.AbstractProtocolAlarmList;
import com.ericsson.navigator.esm.model.alarm.Alarm;
import com.ericsson.navigator.esm.model.conversion.ConversionController;
import com.ericsson.navigator.esm.util.SupervisionException;
import com.ericsson.navigator.esm.util.log.Log;

public class PMFailureAlarmList extends AbstractProtocolAlarmList<DefaultAddressInformation> {

	public PMFailureAlarmList(final Log<Alarm> log, final ConversionController conversionController) {
		super("Default", new DefaultAddressInformation("N/A", "PM Failure Alarm list"),
				log, conversionController);
	}

	@Override
	public void checkHeartbeat() throws SupervisionException {}

	@Override
	public void startSupervisionNoneHB() throws SupervisionException {}

	@Override
	public void stopSupervisionNoneHB() throws SupervisionException {}

	@Override
	public void getActiveAlarmList() throws SupervisionException {}

	@Override
	public boolean isSynchable() {
		return false;
	}
}
