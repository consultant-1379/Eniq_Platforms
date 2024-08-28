package com.ericsson.navigator.esm.model;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.ericsson.navigator.esm.MVM;
import com.ericsson.navigator.esm.MVMEnvironment;
import com.ericsson.navigator.esm.manager.pm.file.local.irp.CounterSetFileLoader;
import com.ericsson.navigator.esm.manager.pm.file.remote.ParserPluginController;
import com.ericsson.navigator.esm.manager.pm.file.remote.RemoteFileFetchExecutor;
import com.ericsson.navigator.esm.manager.snmp.DefaultSNMPAddressInformation;
import com.ericsson.navigator.esm.manager.snmp.TrapReceiver;
import com.ericsson.navigator.esm.manager.text.txf.FileReceiver;
import com.ericsson.navigator.esm.model.alarm.Alarm;
import com.ericsson.navigator.esm.model.alarm.ProtocolAlarmList;
import com.ericsson.navigator.esm.model.alarm.img.GenericMVMAlarmList;
import com.ericsson.navigator.esm.model.alarm.pm.PMFailureAlarmList;
import com.ericsson.navigator.esm.model.alarm.snmp.SNMPAddressInformation;
import com.ericsson.navigator.esm.model.alarm.snmp.SNMPAddressInformation.PROTOCOL_TYPE;
import com.ericsson.navigator.esm.model.alarm.snmp.generic.GenericSNMPAlarmList;
import com.ericsson.navigator.esm.model.alarm.snmp.generic.MibController;
import com.ericsson.navigator.esm.model.alarm.snmp.generic.mst.MSTAlarmList;
import com.ericsson.navigator.esm.model.alarm.snmp.generic.mst.TrapTranslator;
import com.ericsson.navigator.esm.model.alarm.snmp.irp.IRPAlarmList;
import com.ericsson.navigator.esm.model.alarm.snmp.snf.SNFAlarmList;
import com.ericsson.navigator.esm.model.alarm.text.txf.TXFAlarmList;
import com.ericsson.navigator.esm.model.alarm.text.txf.TXFTranslator;
import com.ericsson.navigator.esm.model.conversion.ConversionController;
import com.ericsson.navigator.esm.model.pm.CounterSetDefinitionsController;
import com.ericsson.navigator.esm.model.pm.CounterSetScheduling;
import com.ericsson.navigator.esm.model.pm.DefaultCounterSetIdentification;
import com.ericsson.navigator.esm.model.pm.DefaultCounterSetScheduling;
import com.ericsson.navigator.esm.model.pm.RegexpCounterSetIdentification;
import com.ericsson.navigator.esm.model.pm.file.local.irp.XMLCounterSetList;
import com.ericsson.navigator.esm.model.pm.file.remote.RemoteFileCounterSetScheduling;
import com.ericsson.navigator.esm.model.pm.file.remote.sftp.SFTPFileCounterSetList;
import com.ericsson.navigator.esm.model.pm.snmp.SNMPCounterSetList;
import com.ericsson.navigator.esm.util.SupervisionException;
import com.ericsson.navigator.esm.util.component.Component;
import com.ericsson.navigator.esm.util.component.ComponentInitializationException;
import com.ericsson.navigator.esm.util.component.ComponentShutdownException;
import com.ericsson.navigator.esm.util.file.FileListener;
import com.ericsson.navigator.esm.util.file.FileMonitor;
import com.ericsson.navigator.esm.util.log.Log;
import com.ericsson.navigator.sr.AbstractSRFContentHandler;
import com.ericsson.navigator.sr.SystemRegistration;
import com.ericsson.navigator.sr.ir.SNOSNE;

public class ManagedController extends AbstractSRFContentHandler
implements FileListener, Component {

	private static final ProtocolType[] PM_PROTOCOL_TYPES = new ProtocolType[]{ProtocolType.PM_SNMP, 
						ProtocolType.PM_IRPXML, ProtocolType.PM_SFTP};
	private static final ProtocolType[] ALARM_PROTOCOL_TYPES = new ProtocolType[]{ProtocolType.ALARM_SNF, 
						ProtocolType.ALARM_IRP, ProtocolType.ALARM_MST, 
						ProtocolType.ALARM_SNMP, ProtocolType.ALARM_TXF, ProtocolType.ALARM_MVMALARM};
	private final ManagedModel<Protocol<?,?,?>> managedDataListModel;
	private final Stack<String> fdns;
	private enum ProtocolType {ALARM_IRP, ALARM_SNF, ALARM_MST, ALARM_TXF, ALARM_SNMP, ALARM_MVMALARM, PM_SNMP, PM_IRPXML, PM_SFTP, PM_REMOTE_FILE_COUNTERSET};
	private final Map<ProtocolType, Protocol<?,?,?>> parsingProtocols;
	private final Map<ProtocolType, Object> parsingProtocolMappings;
	
	private final FileMonitor fileMonitor;
	private List<Protocol<?, ?, ?>> previousProtocols = null;
	private final Log<Alarm> alarmLog;

	private final String classname = getClass().getName();
	private int m_TopologyFileRetries = 0;
	private static final int MAX_TOPOLOGY_RETRIES = 3;
	private final String m_TopologyFile;
	private final TrapReceiver m_TrapReceiver;
	private final MibController m_MibController;
	private final TrapTranslator m_TrapTranslator;
	private final FileReceiver m_FileReceiver;
	private final TXFTranslator m_TXFTranslator;
	private final CounterSetFileLoader counterSetFileLoader;
	private final long m_TxfSyncTimeout;
	private final CounterSetDefinitionsController counterSetDefinitionsController;
	private final ParserPluginController parserPluginController;
	private final ConversionController conversionController;
	private ProtocolAlarmList<?> pmProtocolAlarmList = null;
	private SNMPAddressInformation currentSnmpAddressInformation = null;
	private final RemoteFileFetchExecutor fileFetchExecutor;
	private final String sftpLocalDiretory;
	private final String sftpPrivateKeyPath;
	private final String sftpFileFetchedListStoragePath;
	
	public ManagedController(final ManagedModel<Protocol<?,?,?>> managedDataListModel,
			final Log<Alarm> alarmLog, 
			final TrapReceiver trapReceiver, 
			final String topologyFile, final long SR_POLL_DELAY, final MibController mibController, 
			final TrapTranslator trapTranslator,
			final FileReceiver fileReceiver,
			final TXFTranslator txfTranslator,
			final CounterSetFileLoader counterSetFileLoader, final long txfSyncTimeout, 
			final CounterSetDefinitionsController counterSetDefinitionsController, final RemoteFileFetchExecutor fileFetchExecutor,
			final ConversionController conversionController, final ParserPluginController parserPluginController,
			final String sftpLocalDiretory, final String sftpPrivateKeyPath, final String sftpFileFetchedListStoragePath) {
		fdns = new Stack<String>();
		this.managedDataListModel = managedDataListModel;
		this.fileFetchExecutor = fileFetchExecutor;
		fileMonitor = new FileMonitor(SR_POLL_DELAY);
		this.alarmLog = alarmLog;
		m_TopologyFile = topologyFile;
		m_TrapReceiver = trapReceiver;
		m_MibController = mibController;
		m_TrapTranslator = trapTranslator;
		m_FileReceiver = fileReceiver;
		m_TXFTranslator = txfTranslator;
		this.counterSetFileLoader = counterSetFileLoader;
		this.parserPluginController = parserPluginController;
		this.conversionController = conversionController;
		m_TxfSyncTimeout = txfSyncTimeout;
		this.counterSetDefinitionsController = counterSetDefinitionsController;
		this.sftpLocalDiretory = sftpLocalDiretory;
		this.sftpPrivateKeyPath = sftpPrivateKeyPath;
		this.sftpFileFetchedListStoragePath = sftpFileFetchedListStoragePath;
		parsingProtocols = new HashMap<ProtocolType, Protocol<?,?,?>>();
		parsingProtocolMappings = new HashMap<ProtocolType, Object>();
	}

	int getRetries(){
		return m_TopologyFileRetries;
	}

	private synchronized void correlateProtocols() throws IOException, SAXException {
		if (MVM.logger.isDebugEnabled()) {
			MVM.logger.debug(classname + ".correlateProtocols(); -->");
		}
		parsingProtocols.clear();
		parsingProtocolMappings.clear();
		fdns.clear();
		previousProtocols = managedDataListModel.getAllManagedDataLists();
		final SystemRegistration sr = new SystemRegistration();
		addPMManagedAlarmList();
		sr.srfRead(m_TopologyFile, this, this);
		removeOldProtocols();
		if (MVM.logger.isDebugEnabled()) {
			MVM.logger.debug(classname + ".correlateProtocols(); <--");
		}
	}

	private void addPMManagedAlarmList() {
		if(pmProtocolAlarmList == null){
			pmProtocolAlarmList = new PMFailureAlarmList(alarmLog, conversionController);
		}
		correlate(pmProtocolAlarmList);
	}

	private void removeOldProtocols() {
		synchronized (managedDataListModel) {
			for (final Protocol<?, ?, ?> protocol : previousProtocols) {
				MVM.logger.info(classname
						+ ".removeOldProtocols(); Protocol removed "
						+ protocol.getFDN());
				stopProtocol(protocol, true);
				managedDataListModel.removeManaged(protocol);
			}
		}
	}

	private void stopProtocol(final Protocol<?, ?, ?> managedProtocol, final boolean isRemoved) {
		if (MVM.logger.isDebugEnabled()) {
			MVM.logger.debug(classname
					+ ".stopProtocol(); Stop supervision: "
					+ managedProtocol.getFDN());
		}
		try {
			managedProtocol.stopSupervision(isRemoved);
		} catch (final SupervisionException e) {
			MVM.logger.error(classname
					+ ".stopProtocol(); Failed to stop supervision on "
					+ managedProtocol.getFDN(), e);
		} catch (final Exception e) {
			MVM.logger.error(classname
					+ ".stopProtocol(); Failed to stop supervision on "
					+ managedProtocol.getFDN(), e);
		}
	}

	private int getPort(final String port) {
		try {
			return Integer.parseInt(port);
		} catch (final NumberFormatException e) {
			return 161;
		}
	}

	private void createAlarmProtocol(final ProtocolType type, final Attributes attributes) {
		final String name = resolveAttribute(SNOSNE.CIMNAME, attributes, "");
		String ipAddress = resolveAttribute(SNOSNE.SNOSHOSTIP, attributes, null);
		String backupIpAddress = resolveAttribute(SNOSNE.SNOSBACKUPIP, attributes, "");

		final String version = resolveAttribute(SNOSNE.SNOSVERSION, attributes, "V2C");
		final String community = resolveAttribute(SNOSNE.SNOSSNMPCOMMUNITY,
				attributes, "SNOS-PE");
		final String snostype = resolveAttribute(SNOSNE.SNOSTYPE, attributes, "");
	final int port = getPort(resolveAttribute("snossnmpport", attributes, "161"));
	
		if (ipAddress == null || ipAddress.length() == 0) {
			ipAddress = getIPaddress(name, resolveAttribute(
					SNOSNE.SNOSHOSTNAME, attributes, ""));
		}
		final String fdn = fdns.peek();
		currentSnmpAddressInformation = new DefaultSNMPAddressInformation(
				ipAddress, backupIpAddress, port, PROTOCOL_TYPE.SNMP, snostype,
				community, getVersion(version));
	
	
		if (type.equals(ProtocolType.ALARM_IRP)) {
			final Map<String, String> moiMap = new TreeMap<String, String>();
			parsingProtocolMappings.put(ProtocolType.ALARM_IRP, moiMap);
			parsingProtocols.put(ProtocolType.ALARM_IRP, new IRPAlarmList(fdn,
					currentSnmpAddressInformation, moiMap, alarmLog, 
					conversionController, m_TrapReceiver));
		} else if (type.equals(ProtocolType.ALARM_SNMP)) {
			parsingProtocols.put(ProtocolType.ALARM_SNMP, new GenericSNMPAlarmList(fdn,
					new DefaultSNMPAddressInformation(ipAddress, backupIpAddress, port, PROTOCOL_TYPE.SNMP, snostype,
							community, getVersion(version)), alarmLog, 
							conversionController, m_TrapReceiver, m_MibController));
		} else if (type.equals(ProtocolType.ALARM_MST)) {
			parsingProtocols.put(ProtocolType.ALARM_MST, new MSTAlarmList(fdn,
					new DefaultSNMPAddressInformation(ipAddress, backupIpAddress, port, PROTOCOL_TYPE.MST, snostype,
							community, getVersion(version)), alarmLog, 
							conversionController, m_TrapReceiver, m_MibController, m_TrapTranslator));
		} 
		else if (type.equals(ProtocolType.ALARM_TXF)) {
			parsingProtocols.put(ProtocolType.ALARM_TXF, new TXFAlarmList(fdn,
					new DefaultAddressInformation(ipAddress, snostype), 
					alarmLog, conversionController, m_FileReceiver, 
					m_TXFTranslator.load(snostype), 
					m_TxfSyncTimeout));
		} 
		else if (type.equals(ProtocolType.ALARM_MVMALARM)) {
			parsingProtocols.put(ProtocolType.ALARM_MVMALARM, new GenericMVMAlarmList(fdn,
					new DefaultAddressInformation(ipAddress, snostype, backupIpAddress), alarmLog));
		}
		
	else if (type.equals(ProtocolType.ALARM_SNF)) {
			final DefaultSNMPAddressInformation adressInformation = new DefaultSNMPAddressInformation(
					ipAddress, backupIpAddress, port, PROTOCOL_TYPE.SNF, snostype, community,
					SNMPAddressInformation.VERSION.V2C);
			final Map<String, String> resourceIdMap = new TreeMap<String, String>();
			parsingProtocolMappings.put(ProtocolType.ALARM_SNF, resourceIdMap);
			parsingProtocols.put(ProtocolType.ALARM_SNF, new SNFAlarmList(fdn,
					adressInformation, resourceIdMap, alarmLog, 
					conversionController, m_TrapReceiver));
		}
	}

	@Override
	public void startElement(final String namespaceURI, final String localName,
			final String qname, final Attributes attributes) {
		setDocumentLocator(myLoc);
		if (localName.equals("SNOSNE")) {
			final String name = resolveAttribute(SNOSNE.CIMNAME, attributes, "");
			final String snostype = resolveAttribute(SNOSNE.SNOSTYPE, attributes, "");
			appendFDN(snostype+"="+name);
		}
		startProtocol(localName, attributes);
	}

	private void startProtocol(final String tag, final Attributes attributes) {
		final ProtocolType type = getProtocolType(tag, attributes);
		
		if(type == null){
			return;
		}
		final Protocol<?,?,?> protocol = parsingProtocols.get(type);
		if(protocol == null){
			createProtocol(type, attributes);
		}
		addMapping(type, attributes);
	}

	
	private ProtocolType getProtocolType(final String localName, final Attributes attributes) {
		if (localName.equals("SnmpCounterSet")) {
			return ProtocolType.PM_SNMP;
		} else if (localName.equals("IrpXmlCounterSet")) {
			return ProtocolType.PM_IRPXML;	
		} else if (localName.equals("RemoteFileFetch")) {
			return ProtocolType.PM_SFTP;	
		} else if (localName.equals("RemoteFileCounterSet")) {
			return ProtocolType.PM_REMOTE_FILE_COUNTERSET;	
		} else if (localName.equals("SNOSNE")){
		    return getSnosNeProtocolType(attributes);
		}
		return null;
	}
	
	

	private ProtocolType getSnosNeProtocolType(final Attributes attributes) {
		final String alarmMOI = resolveAttribute("snosalarmmoi", attributes, null);
		final String snosprotocoltype = resolveAttribute(SNOSNE.SNOSPROTOCOLTYPE,
				attributes, SNOSNE.getProtocolType(alarmMOI));
		if (snosprotocoltype.equals(SNOSNE.PROTOCOLTYPE_ALARMIRP)) {
			return ProtocolType.ALARM_IRP;
		} else if (snosprotocoltype.equals(SNOSNE.PROTOCOLTYPE_SNMP)) {
			return ProtocolType.ALARM_SNMP;
		} else if (snosprotocoltype.equals(SNOSNE.PROTOCOLTYPE_MST)) {
			return ProtocolType.ALARM_MST;
		} else if (snosprotocoltype.equals(SNOSNE.PROTOCOLTYPE_TXF)) {
			return ProtocolType.ALARM_TXF;
		} else {
			return ProtocolType.ALARM_MVMALARM;
		}
	}
	
	

	@SuppressWarnings("unchecked")
	private void addMapping(final ProtocolType type, final Attributes attributes) {
		final Object mapping = parsingProtocolMappings.get(type);
		if(mapping == null){
			return;
		}
		switch(type){
			case ALARM_SNF:
				final String resourceId = resolveAttribute("cimotheridentifyinginfo",
						attributes, "");
			((Map<String, String>)mapping).put(resourceId, fdns.peek());
				break;
			case ALARM_IRP:
				final String moi = resolveAttribute("snosalarmmoi",
						attributes, "");
				if(!moi.isEmpty()){
					((Map<String, String>)mapping).put(moi, fdns.peek());
				}
				break;
			case PM_SNMP:
				((List<CounterSetScheduling>)mapping).add(createDefaultCounterSetScheduling(attributes));
				break;
			case PM_SFTP:
				final CounterSetScheduling<RegexpCounterSetIdentification> scheduling = 
					createSFTPCounterSetScheduling(attributes);
				((List<CounterSetScheduling>)mapping).add(scheduling);
				parsingProtocolMappings.put(ProtocolType.PM_REMOTE_FILE_COUNTERSET, scheduling);
				break;
			case PM_REMOTE_FILE_COUNTERSET:
				addIdentification(attributes, mapping);
				break;
			case PM_IRPXML:
				((List<CounterSetScheduling>)mapping).add(createXMLCounterSetScheduling(attributes));
				break;
		}
	}

	@SuppressWarnings("unchecked")
	private void addIdentification(final Attributes attributes,
			final Object mapping) {
		final CounterSetScheduling<RegexpCounterSetIdentification> scheduling = 
			(CounterSetScheduling<RegexpCounterSetIdentification>)mapping;
		final String fileName = resolveAttribute("fileName", attributes, "");
		final String regExp = resolveAttribute("regExp", attributes, ".*" + fileName + ".*");
		scheduling.getIdentifications().add(new RegexpCounterSetIdentification(regExp, fileName));
	}

	private CounterSetScheduling<RegexpCounterSetIdentification> createSFTPCounterSetScheduling(
			final Attributes attributes) {
		final int rop = Integer.parseInt(resolveAttribute("ROP", attributes, ""));
		final String pluginDir = resolveAttribute("pluginDir", attributes, "default");
		final int offset = Integer.parseInt(resolveAttribute("Offset", attributes, "0"));		
		final List<RegexpCounterSetIdentification> identificationList = new ArrayList<RegexpCounterSetIdentification>();
		return new RemoteFileCounterSetScheduling(fdns.peek(), rop, pluginDir, offset, identificationList);
	}

	private CounterSetScheduling<DefaultCounterSetIdentification> createDefaultCounterSetScheduling(final Attributes attributes) {
		final String fileName = resolveAttribute("fileName", attributes, "");
		final int rop = Integer.parseInt(resolveAttribute("ROP", attributes, ""));
		final List<DefaultCounterSetIdentification> identificationList = new ArrayList<DefaultCounterSetIdentification>();
		identificationList.add(new DefaultCounterSetIdentification(fileName));
		return new DefaultCounterSetScheduling<DefaultCounterSetIdentification>(fdns.peek(), rop, identificationList);
	}

	private CounterSetScheduling<RegexpCounterSetIdentification> createXMLCounterSetScheduling(final Attributes attributes) {
		final String fileName = resolveAttribute("fileName", attributes, "");
		final int rop = Integer.parseInt(resolveAttribute("ROP", attributes, ""));
		final String regExp = resolveAttribute("regExp", attributes, ".*" + fileName + ".*");
		final List<RegexpCounterSetIdentification> identificationList = new ArrayList<RegexpCounterSetIdentification>();
		identificationList.add(new RegexpCounterSetIdentification(regExp, fileName));
		return new DefaultCounterSetScheduling<RegexpCounterSetIdentification>(fdns.peek(), rop, identificationList);
	}

	private void createProtocol(final ProtocolType type, final Attributes attributes) {
		if(isProtocolTypeOf(type, ALARM_PROTOCOL_TYPES)){
			final String ipAddress = resolveAttribute("snoshostip", attributes, null);
			if(ipAddress == null || ipAddress.isEmpty()){
				return;
			}
			createAlarmProtocol(type, attributes);
		} 
		 if(isProtocolTypeOf(type, PM_PROTOCOL_TYPES)){
			createPmProtocol(type);
		}
	}

	private void createPmProtocol(final ProtocolType type) {    //, final String parserState) {
		switch(type){
			case PM_SNMP:
				final List<DefaultCounterSetScheduling<DefaultCounterSetIdentification>> snmpSchedulings = 
					new ArrayList<DefaultCounterSetScheduling<DefaultCounterSetIdentification>>();
				parsingProtocolMappings.put(type, snmpSchedulings);
				parsingProtocols.put(type, new SNMPCounterSetList(
						fdns.peek(), snmpSchedulings, currentSnmpAddressInformation, 
						counterSetDefinitionsController, conversionController, pmProtocolAlarmList));
				break;
			case PM_SFTP:
				final List<RemoteFileCounterSetScheduling> sftpSchedulings = new ArrayList<RemoteFileCounterSetScheduling>();
				parsingProtocolMappings.put(type, sftpSchedulings);
				parsingProtocols.put(type, new SFTPFileCounterSetList(
						fdns.peek(), sftpSchedulings, currentSnmpAddressInformation, 
						counterSetDefinitionsController, conversionController, pmProtocolAlarmList, 
						fileFetchExecutor, parserPluginController, sftpPrivateKeyPath, 
						sftpLocalDiretory, sftpFileFetchedListStoragePath));
				break;
			case PM_IRPXML:
				final List<DefaultCounterSetScheduling<RegexpCounterSetIdentification>> irpSchedulings = 
					new ArrayList<DefaultCounterSetScheduling<RegexpCounterSetIdentification>>();
				parsingProtocolMappings.put(type, irpSchedulings);
				parsingProtocols.put(type, new XMLCounterSetList(
						fdns.peek(), irpSchedulings, currentSnmpAddressInformation, 
						counterSetFileLoader, counterSetDefinitionsController, 
						conversionController, pmProtocolAlarmList));
				break;
		}
	}

	private boolean isProtocolTypeOf(final ProtocolType type,
			final ProtocolType[] protocolTypes) {
		for(ProtocolType listType : protocolTypes){
			if(listType.equals(type)){
				return true;
			}
		}
		return false;
	}

	private SNMPAddressInformation.VERSION getVersion(final String version) {
		if (version.contains("1")) {
			return SNMPAddressInformation.VERSION.V1;
		}
		return SNMPAddressInformation.VERSION.V2C;
	}

	private String getIPaddress(final String name, final String hostName) {
		try {
			return InetAddress.getByName(hostName).getHostAddress();
		} catch (final UnknownHostException e) {
			MVM.logger
			.error(
					classname
					+ ".getIPaddress(); Unable to resolve hostname for protocol named : "
					+ name, e);
			return null;
		}
	}

	private void appendFDN(final String name) {
		final StringBuffer fdn = new StringBuffer();
		if (!fdns.isEmpty()) {
			fdn.append(fdns.peek());
			fdn.append(',');
		}
		fdn.append(name);
		fdns.push(fdn.toString());
	}

	public void correlate(final Protocol<?, ?, ?> protocol) {
		synchronized (managedDataListModel) {
			final int index = previousProtocols.indexOf(protocol);
			if (index != -1) {
				final Protocol<?, ?, ?> oldManagedProtocol = previousProtocols
				.remove(index);
				if (oldManagedProtocol.isUpdated(protocol)) {
					stopProtocol(oldManagedProtocol, true);
					managedDataListModel.removeManaged(oldManagedProtocol);
					MVM.logger.info(classname
							+ ".correlate(); Protocol updated "
							+ protocol.getFDN());
					managedDataListModel.addManaged(protocol);
					startProtocol(protocol);
				}
			} else {
				MVM.logger.info(classname
						+ ".correlate(); Protocol added "
						+ protocol.getFDN());
				managedDataListModel.addManaged(protocol);
				startProtocol(protocol);
			}
		}
	}

	private void startProtocol(final Protocol<?, ?, ?> protocol) {
		if (MVM.logger.isDebugEnabled()) {
			MVM.logger.debug(classname
					+ ".startProtocol(); Starting supervision: "
					+ protocol.getFDN());
		}
		try {
			protocol.startSupervision();
		} catch (final SupervisionException e) {
			MVM.logger.error(classname
					+ ".startProtocol(); Failed to start supervision on "
					+ protocol.getFDN(), e);
		} catch (final Exception e) {
			MVM.logger.error(classname
					+ ".startProtocol(); Failed to start supervision on "
					+ protocol.getFDN(), e);
		}
	}

	@Override
	public void endElement(final String namespaceURI, final String localName, final String qname) {
		if (localName.equals("SNOSNE")) {
			endProtocols();
			fdns.pop();
		}
	}

	private void endProtocols() {
		final Set<ProtocolType> types = new HashSet<ProtocolType>(parsingProtocols.keySet());
		for(ProtocolType type : types){
			final Protocol<?,?,?> protocol = parsingProtocols.get(type);
			if(protocol.getFDN().equals(fdns.peek())){
				correlate(protocol);
				parsingProtocols.remove(type);
				parsingProtocolMappings.remove(type);
			}
		}
	}

	@Override
	public void error(final SAXParseException e) throws SAXException {
		if(m_TopologyFileRetries >= MAX_TOPOLOGY_RETRIES){
			MVM.logger.error(classname + ".error(); Parse error", e);
		}
	}

	@Override
	public void fatalError(final SAXParseException e) throws SAXException {
		if(m_TopologyFileRetries >= MAX_TOPOLOGY_RETRIES){
			if(System.getProperty("MediatorInitialStart", "false").equals("true")){
				MVM.logger.error(classname + ".error(); Parse fatal error", e);
			}
		}
	}

	@Override
	public void warning(final SAXParseException e) throws SAXException {
		if(m_TopologyFileRetries >= MAX_TOPOLOGY_RETRIES){
			MVM.logger.warn(classname + ".warning(); Parse warning", e);
		}
	}

	public synchronized void checkProtocols(){
		try {
			correlateProtocols();
			m_TopologyFileRetries = 0;
		} catch (final IOException e) {
			MVM.logger.error(classname
					+ ".checkProtocols(); Failed to read system registration", e);
		} catch (final SAXException e) {
			//Somebody might still be writing to the system registration file retry?
			if(++m_TopologyFileRetries <= MAX_TOPOLOGY_RETRIES){
				try {
					Thread.sleep(1000);
				} catch (final InterruptedException e1) {
					return;
				}
				checkProtocols();
			}else{
				m_TopologyFileRetries = 0;
				if(System.getProperty("MediatorInitialStart", "false").equals("true")){
					MVM.logger.error(classname+".checkProtocols(); Failed to parse system registration file",e);
				}

				System.setProperty("MediatorInitialStart",  "true");
			}
		}
	}

	public void fileChanged(final File file) {
		checkProtocols();
	}

	@Override
	public String getComponentName() {
		return ManagedController.class.getSimpleName();
	}

	@Override
	public void initialize() throws ComponentInitializationException {
		isInitalStart();
		fileMonitor.addFile(new File(m_TopologyFile));
		fileMonitor.addListener(this);
		checkProtocols();
	}

	@Override
	public void shutdown() throws ComponentShutdownException {
		fileMonitor.removeListener(this);
		fileMonitor.stop();
		synchronized(managedDataListModel){
			for(Protocol<?,?,?> protocol : managedDataListModel.getAllManagedDataLists()){
				stopProtocol(protocol, false);
			}
		}
	}
	
	public void isInitalStart(){
		File runtime = new File(MVMEnvironment.SFTP_LISTEDFILES_STORAGE_PATH);
		File[] contents = runtime.listFiles();
		boolean foundMatch = false;
		for(int index = 0; foundMatch == false && index < contents.length; index++){
			File file = contents[index];
			if(file.isFile() && file.getName().endsWith(".ser")){
				foundMatch = true;
			}
		}
		System.setProperty("MediatorInitialStart",  new Boolean(foundMatch).toString());
	}
}
