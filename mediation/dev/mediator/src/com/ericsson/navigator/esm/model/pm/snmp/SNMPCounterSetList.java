package com.ericsson.navigator.esm.model.pm.snmp;

import java.io.IOException;
import java.util.List;

import com.ericsson.navigator.esm.MVM;
import com.ericsson.navigator.esm.manager.snmp.AsynchronousSnmpRequestHandler;
import com.ericsson.navigator.esm.manager.snmp.RequestChannel;
import com.ericsson.navigator.esm.manager.snmp.SNMPProtocol;
import com.ericsson.navigator.esm.manager.snmp.SNMPRequest;
import com.ericsson.navigator.esm.model.alarm.ProtocolAlarmList;
import com.ericsson.navigator.esm.model.alarm.snmp.SNMPAddressInformation;
import com.ericsson.navigator.esm.model.conversion.ConversionController;
import com.ericsson.navigator.esm.model.pm.AbstractProtocolCounterSetList;
import com.ericsson.navigator.esm.model.pm.CounterDefinition;
import com.ericsson.navigator.esm.model.pm.CounterSet;
import com.ericsson.navigator.esm.model.pm.CounterSetDefinition;
import com.ericsson.navigator.esm.model.pm.CounterSetDefinitionsController;
import com.ericsson.navigator.esm.model.pm.DefaultCounterSetIdentification;
import com.ericsson.navigator.esm.model.pm.DefaultCounterSetScheduling;

import com.ericsson.navigator.esm.util.SupervisionException;

public class SNMPCounterSetList extends AbstractProtocolCounterSetList<SNMPAddressInformation, 
	DefaultCounterSetScheduling<DefaultCounterSetIdentification>> 
	implements SNMPProtocol<CounterSet.State, CounterSet, SNMPAddressInformation> {
   	private RequestChannel channel = null;
	private final String classname = getClass().getName();
	private String ipAddress;
	
	public SNMPCounterSetList(final String fdn, 
			final List<DefaultCounterSetScheduling<DefaultCounterSetIdentification>> counterSets,
			final SNMPAddressInformation addressInformation, 
			final CounterSetDefinitionsController counterSetDefinitionsController,
			final ConversionController conversionController,
			final ProtocolAlarmList<?> alarmList) {
		super(fdn, counterSets, addressInformation, null, alarmList,
				counterSetDefinitionsController, conversionController);
		ipAddress = addressInformation.getAddress();
	}

	@Override
	public RequestChannel getRequestChannel() {
		return channel;
	}
	
	

	@Override
	public void setRequestChannel(final RequestChannel channel) {
		this.channel = channel;
	}

	@Override
	protected void fetchCounterSet(final DefaultCounterSetScheduling<DefaultCounterSetIdentification> counterSetSchedule)
			throws SupervisionException {
		SNMPRequest<SNMPCounterSetList> request = null;
		final String fileName = counterSetSchedule.getIdentifications().get(0).getFileName();
		final CounterSetDefinition definition = 
			counterSetDefinitionsController.getCounterSetDefinitionByFileName(fileName);
		if(definition == null){
			MVM.logger.error(classname
					+ ".fetchCounterSet(); No CounterSet definition found for scheduling with FDN: "
					+ getFDN() + ", fileName: " + fileName);
			return;
		}
		if(System.getProperty("HeartbeatStatus:"+ipAddress, "false").equals("true")){
			SNMPCounterSetStore.getInstance().addNewCounterSetFile(counterSetSchedule.getFdn(), ipAddress, definition); 
		
			if(isScalars(definition)){
				MVM.logger.info(classname + ".fetchCounterSet(); Sending Get request for counterset "+definition.getFileName()+" to "+ counterSetSchedule.getFdn() +", "+definition.getDefinitions().size()+" counters.");
				request = new SNMPGetCounterSetRequest(definition, this, counterSetSchedule.getRop(),counterSetSchedule);
			} else {
				MVM.logger.info(classname + ".fetchCounterSet(); Sending Get Next request for counterset "+definition.getFileName()+" to "+ counterSetSchedule.getFdn() +", "+definition.getDefinitions().size()+" counters.");
				request = new SNMPGetNextCounterSetRequest(definition, this, counterSetSchedule.getRop(),counterSetSchedule);
			}
			snapshot(definition.getCounterSetId());
			try {
				AsynchronousSnmpRequestHandler.getInstance().addSNMPRequest(request);
				
			} catch (IOException e) {
				throw new SupervisionException(
						"Error on sending SNMP request for managed system named "
								+ getFDN(), e);
			}
		}else{
			MVM.logger.error("Request was not sent for counterset "+definition.getFileName() +" for fdn "+ counterSetSchedule.getFdn() +" : Heartbeat was not found for node!");
		}
		//this.parsefilePM(counterSetSchedule);
		
	}
	
	public void pmStatus(final DefaultCounterSetScheduling<DefaultCounterSetIdentification> counterSetSchedule)//Called after snmpData is processed.
	{
		
		if(counterSetSchedule !=null){
		this.parsefilePM(counterSetSchedule);
		}
	}

	private boolean isScalars(final CounterSetDefinition definitionSet) {
		for(CounterDefinition counterDefinition : 
			definitionSet.getDefinitions().values()){
			if(!counterDefinition.getId().endsWith(".0")){
				return false;
			}
		}
		return true;
	}

	
}
