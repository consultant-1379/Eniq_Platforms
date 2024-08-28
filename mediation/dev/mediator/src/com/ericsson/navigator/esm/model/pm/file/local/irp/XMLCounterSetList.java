package com.ericsson.navigator.esm.model.pm.file.local.irp;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ericsson.navigator.esm.manager.pm.file.local.irp.CounterSetFileListener;
import com.ericsson.navigator.esm.manager.pm.file.local.irp.CounterSetFileLoader;
import com.ericsson.navigator.esm.model.AddressInformation;
import com.ericsson.navigator.esm.model.alarm.ProtocolAlarmList;
import com.ericsson.navigator.esm.model.conversion.ConversionController;
import com.ericsson.navigator.esm.model.pm.AbstractProtocolCounterSetList;
import com.ericsson.navigator.esm.model.pm.CounterSetDefinition;
import com.ericsson.navigator.esm.model.pm.CounterSetDefinitionsController;
import com.ericsson.navigator.esm.model.pm.DefaultCounterSetScheduling;
import com.ericsson.navigator.esm.model.pm.RegexpCounterSetIdentification;
import com.ericsson.navigator.esm.model.pm.file.FileCounterSet;
import com.ericsson.navigator.esm.util.SupervisionException;

@SuppressWarnings( "PMD.CyclomaticComplexity" )
public class XMLCounterSetList
		extends
		AbstractProtocolCounterSetList<AddressInformation, DefaultCounterSetScheduling<RegexpCounterSetIdentification>>
		implements CounterSetFileListener {

	private static final String classname = XMLCounterSetList.class.getName();
	public static Logger logger = Logger.getLogger(classname);
	
	private final CounterSetFileLoader counterSetXMLLoader;
    private boolean moidMatch;
	public XMLCounterSetList(
			final String fdn,
			final List<DefaultCounterSetScheduling<RegexpCounterSetIdentification>> counterSetSchedulings,
			final AddressInformation addressInformation,
			final CounterSetFileLoader counterSetXMLLoader,
			final CounterSetDefinitionsController counterSetDefinitionsController,
			final ConversionController conversionController,
			final ProtocolAlarmList<?> alarmList) {
		super(fdn, counterSetSchedulings, addressInformation, null, alarmList,
				counterSetDefinitionsController, conversionController);
		this.counterSetXMLLoader = counterSetXMLLoader;
		this.moidMatch = false;
	}

	
	@SuppressWarnings( "PMD.CyclomaticComplexity" )
	@Override
	public void receivedCounterSet(final String nedn, final String moid,
			final int gp, final Date endDate,
			final Map<String, String> observedCounters) {
		logger.debug(classname + ".receivedCounterSet(); --> nedn=\"" + nedn
				+ "\" moid=\"" + moid + "\"");
		for (final DefaultCounterSetScheduling<RegexpCounterSetIdentification> scheduling : counterSetSchedulings) {
			for(final RegexpCounterSetIdentification identification : scheduling.getIdentifications()){
				if (moid.matches(identification.getRegularExpression())) {
					moidMatch=true;
					final String fileName = identification.getFileName();
					String nednComma = nedn;
					if (!nedn.isEmpty() && !moid.isEmpty()) {
						nednComma = nedn + ",";
					}
					if ((nedn + moid).isEmpty()) {
						nednComma = this.getFDN();
					}
					final CounterSetDefinition definition = counterSetDefinitionsController
							.getCounterSetDefinitionByFileName(fileName);
					if(definition != null) {
						final FileCounterSet counterSet = new FileCounterSet(nednComma //NOPMD
								+ moid, gp, endDate, observedCounters, definition);
						logger.debug(classname
								+ ".receivedCounterSet(); Created CounterSet\n"
								+ counterSet);
										
						if(counterSet.isValid()) {
							correlate(counterSet);
						} else {		//TR HM59414 updated debug statement
							logger.debug(classname
									+ ".receivedCounterSet(); CounterSet for counterset id : " + counterSet.getCounterSetId() + " with moid "+ counterSet.getMoid() +" not valid");
						}
					} else {		//TR HM59414 updated debug statement
						logger.debug(classname
								+ ".receivedCounterSet(); CounterSet definition not found " + identification.getFileName());
					}
				}
				
				
			}
		}
		for (final DefaultCounterSetScheduling<RegexpCounterSetIdentification> scheduling : counterSetSchedulings) {
			for(final RegexpCounterSetIdentification identification : scheduling.getIdentifications()){
		if(!moidMatch)
		{
			logger.info(classname
					+ ".receivedCounterSet(); Moid does not match with RegularExpression defined in SystemTopology.xml file . Expected regularexp= [ "+identification.getRegularExpression()+"] Received regularexp= [ "+moid +" ]");
		}
	   }
	  }
		moidMatch = false;
		logger.debug(classname + ".receivedCounterSet(); <--");
	}

	@Override
	public void startSupervision() throws SupervisionException {
		logger.debug(classname + ".startSupervision(); -->");
		counterSetXMLLoader.addFileListener(this);
		super.startSupervision();

	}

	@Override
	public void stopSupervision(final boolean isRemoved)
			throws SupervisionException {
		logger.debug(classname + ".stopSupervision(); -->");
		counterSetXMLLoader.removeFileListener(this);
		super.stopSupervision(isRemoved);
	}

	@Override
	protected void fetchCounterSet(
			final DefaultCounterSetScheduling<RegexpCounterSetIdentification> counterSetSchedule)
			throws SupervisionException {
	}
}
