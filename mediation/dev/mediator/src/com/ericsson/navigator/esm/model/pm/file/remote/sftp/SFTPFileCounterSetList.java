package com.ericsson.navigator.esm.model.pm.file.remote.sftp;

import java.util.List;

import com.ericsson.navigator.esm.manager.pm.file.remote.ParserPluginController;
import com.ericsson.navigator.esm.manager.pm.file.remote.RemoteFileFetchExecutor;
import com.ericsson.navigator.esm.model.AddressInformation;
import com.ericsson.navigator.esm.model.alarm.ProtocolAlarmList;
import com.ericsson.navigator.esm.model.conversion.ConversionController;
import com.ericsson.navigator.esm.model.pm.CounterSetDefinitionsController;
import com.ericsson.navigator.esm.model.pm.file.remote.CounterSetFileFetcher;
import com.ericsson.navigator.esm.model.pm.file.remote.RemoteFileCounterSetList;
import com.ericsson.navigator.esm.model.pm.file.remote.RemoteFileCounterSetScheduling;
import com.ericsson.navigator.esm.util.SupervisionException;

public class SFTPFileCounterSetList extends RemoteFileCounterSetList {

	private final String privateKeyPath;
	
	public SFTPFileCounterSetList(final String fdn,
			final List<RemoteFileCounterSetScheduling> counterSetSchedulings,
			final AddressInformation addressInformation,
			final CounterSetDefinitionsController counterSetDefinitionsController, 
			final ConversionController conversionController, 
			final ProtocolAlarmList<?> alarmList, final RemoteFileFetchExecutor executor,
			final ParserPluginController pluginController, 
			final String privateKeyPath, final String localDirectory, final String persistantStoragePath){
		super(fdn, counterSetSchedulings, addressInformation, 
				counterSetDefinitionsController, conversionController, alarmList, executor, 
				pluginController, localDirectory, persistantStoragePath);
		this.privateKeyPath = privateKeyPath;
	}

	@Override
	protected Runnable createCounterSetRequest(
			final RemoteFileCounterSetScheduling counterSetSchedule) throws SupervisionException {
		return new CounterSetFileFetcher(this, counterSetSchedule, counterSetDefinitionsController,
				localDirectory, pluginController, new SFTPProvider(privateKeyPath));
	}
}
