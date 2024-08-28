package com.ericsson.navigator.esm;

import java.io.File;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.Naming;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

//import com.ericsson.eniq.licensing.cache.LicensingCache;
import com.ericsson.navigator.esm.agent.pm.PmAgent3gppXml;
import com.ericsson.navigator.esm.agent.rmi.ManagedSystemAgentInterface;
import com.ericsson.navigator.esm.manager.pm.file.local.irp.CounterSetFileLoader;
import com.ericsson.navigator.esm.manager.pm.file.remote.MaverickLicenseController;
import com.ericsson.navigator.esm.manager.pm.file.remote.ParserPluginController;
import com.ericsson.navigator.esm.manager.pm.file.remote.RemoteFileFetchExecutor;
import com.ericsson.navigator.esm.manager.snmp.AsynchronousSnmpRequestHandler;
import com.ericsson.navigator.esm.manager.snmp.TrapReceiver;
import com.ericsson.navigator.esm.manager.text.txf.FileReceiver;
import com.ericsson.navigator.esm.model.DefaultManagedModel;
import com.ericsson.navigator.esm.model.ManagedController;
import com.ericsson.navigator.esm.model.ManagedDataListFileStore;
import com.ericsson.navigator.esm.model.ManagedModel;
import com.ericsson.navigator.esm.model.Protocol;
import com.ericsson.navigator.esm.model.alarm.snmp.generic.MibController;
import com.ericsson.navigator.esm.model.alarm.snmp.generic.mst.TrapTranslator;
import com.ericsson.navigator.esm.model.alarm.text.txf.TXFTranslator;
import com.ericsson.navigator.esm.model.conversion.ConversionController;
import com.ericsson.navigator.esm.model.pm.CounterSetDefinitionsController;
import com.ericsson.navigator.esm.model.pm.file.local.irp.CounterSetXMLParser;
//import com.ericsson.navigator.esm.pm.db.CounterSetTable;
//import com.ericsson.navigator.esm.pm.db.DatabaseUtil;
//import com.ericsson.navigator.esm.pm.db.ManagedObjects;
//import com.ericsson.navigator.esm.pm.db.PmAgentDatabase;
//import com.ericsson.navigator.esm.pm.db.PmProperties;
//import com.ericsson.navigator.esm.pm.db.PmTransformationController;
import com.ericsson.navigator.esm.util.Environment;
import com.ericsson.navigator.esm.util.component.Component;
import com.ericsson.navigator.esm.util.component.ComponentInitializationException;
import com.ericsson.navigator.esm.util.component.ComponentShutdownException;
//import com.ericsson.navigator.esm.util.db.ConnectionManager;
import com.ericsson.navigator.esm.util.log.zip.CyclicAlarmZipLog;
import com.ericsson.navigator.esm.util.log.zip.LoggingOutputStream;

//import com.ericsson.eniq.licensing.cache.DefaultLicenseDescriptor;
//import com.ericsson.eniq.licensing.cache.DefaultLicensingCache;
//import com.ericsson.eniq.licensing.cache.LicenseDescriptor;
//import com.ericsson.eniq.licensing.cache.LicensingCache;
//import com.ericsson.eniq.licensing.cache.LicensingResponse;
//import com.ericsson.eniq.licensing.cache.LicenseInformation;

import com.ericsson.navigator.esm.agent.status.AddServerIntf;
import com.ericsson.navigator.esm.agent.status.AddServerImpl;

public class MVM {
	
  public final static String SIM_STARTER_LICENSE = "CXC";
  private static final String classname = MVM.class.getName(); 
	public static Logger logger = Logger.getLogger(classname);

	private final List<Component> components;
	private final ManagedModel<Protocol<?,?,?>> managedProtocolModel;
	private final ManagedDataListFileStore fileStore;
	private final ManagedController managedSystemController;
	private ManagedSystemAgentInterface m_AgentInterface = null;
	private final CyclicAlarmZipLog alarmLog;
	private final TrapReceiver trapReceiver;
	private final MibController mibController;
	private final TrapTranslator trapTranslator;
	private final FileReceiver txfFileReceiver;
	private final TXFTranslator txfTranslator;
	private final CounterSetDefinitionsController counterSetDefinitionsController;
	private final CounterSetFileLoader counterSetFileLoader;
	private final RemoteFileFetchExecutor remoteFileFetchExecutor;
	private final ParserPluginController parserPluginController;
	private final MaverickLicenseController maverickLicenseController;
	private final ConversionController conversionController;
	private static MemUsageListener memUsageListener;
	public static long startedAt = 0L;

	public MVM() {
		components = new ArrayList<Component>(); 
		mibController = new MibController(MVMEnvironment.MIBFILESDIR, MVMEnvironment.USERMIBFILESDIR, Environment.POLL_DELAY);
		/*
		 * parserPluginController and conversionController needs to be
		 * initialized before other job to start as Bean Shell and counter files
		 * loading is dependent on this .
		 */
		parserPluginController = new ParserPluginController(
				MVMEnvironment.PARSERPLUGINDIR,
				MVMEnvironment.PARSERPLUGINPROPERTYFILE, Environment.POLL_DELAY);
		conversionController = new ConversionController(
				MVMEnvironment.CONVERSIONS_PATH, Environment.POLL_DELAY);
		txfFileReceiver = new FileReceiver(MVMEnvironment.TXFALARMFILEPATH, MVMEnvironment.TXFALLOWEDFILESIZE);
		trapReceiver = new TrapReceiver(new InetSocketAddress(
				MVMEnvironment.SNMPBIND_HOSTNAME, MVMEnvironment.SNMPBIND_PORT), logger, mibController);
		alarmLog = new CyclicAlarmZipLog(MVMEnvironment.LOGGERCONFIGFILE);
		managedProtocolModel = new DefaultManagedModel<Protocol<?,?,?>>();
		fileStore = new ManagedDataListFileStore( 
				managedProtocolModel, Environment.DATALISTFILE, 
				Environment.ALARMLISTFILEBACKUP, Environment.DATALISTSAVEDELAY);
		trapTranslator = new TrapTranslator();
		trapTranslator.addDirectory(new File(MVMEnvironment.MSTTRANSLATIONDIR));
		trapTranslator.addDirectory(new File(MVMEnvironment.MSTCUSTOMERTRANSLATIONSDIR));
		maverickLicenseController = new MaverickLicenseController(MVMEnvironment.MAVERICK_LICENSE_PATH);
		//registerAgentInterface();
		txfTranslator = new TXFTranslator(MVMEnvironment.TXFTRANSLATIONMAPPATH, Environment.POLL_DELAY);
		counterSetDefinitionsController = new CounterSetDefinitionsController(
				MVMEnvironment.COUNTERSETDIR, Environment.POLL_DELAY);
		counterSetFileLoader = new CounterSetFileLoader(
				Environment.POLL_DELAY, MVMEnvironment.LOCALFILE_PMIRPINPUT, MVMEnvironment.LOCALFILE_PMIRPARCHIVE, MVMEnvironment.LOCALFILE_PMIRPCORRUPTED);
		counterSetFileLoader.addFileParser(new CounterSetXMLParser(counterSetFileLoader));
		remoteFileFetchExecutor = new RemoteFileFetchExecutor(
				MVMEnvironment.REMOTEFILE_COREPOOLSIZE, MVMEnvironment.REMOTEFILE_MAXPOOLSIZE, 
				MVMEnvironment.REMOTEFILE_KEEPALIVETIME, MVMEnvironment.REMOTEFILE_POOLQUEUESIZE);


		managedSystemController = new ManagedController(
				managedProtocolModel, alarmLog, trapReceiver, Environment.TOPOLOGYFILE, 
				Environment.POLL_DELAY, mibController, trapTranslator, txfFileReceiver, 
				txfTranslator, counterSetFileLoader, MVMEnvironment.TXFSYNCTIMEOUT, counterSetDefinitionsController,
				remoteFileFetchExecutor, conversionController, parserPluginController, MVMEnvironment.REMOTEFILE_PMDOWNLOADDIR, 
				MVMEnvironment.SFTP_PRIVATEKEY_PATH, MVMEnvironment.SFTP_LISTEDFILES_STORAGE_PATH);

		if (MVMEnvironment.ENABLEPARSER) {
			components.add(counterSetDefinitionsController);
			components.add(counterSetFileLoader);
		}

		if (MVMEnvironment.ENABLESNMP) {
			components.add(mibController);
			components.add(AsynchronousSnmpRequestHandler.getInstance());
		}

		components.add(parserPluginController);
		components.add(conversionController);
		// components.add(trapReceiver);
		components.add(alarmLog);
		// components.add(trapTranslator);
		// components.add(txfTranslator);

		components.add(maverickLicenseController);

		// /components.add(txfFileReceiver);
		// components.add(fileStore);

		components.add(remoteFileFetchExecutor);

		if(MVMEnvironment.PMAGENT3GPP){
			components.add(new PmAgent3gppXml(m_AgentInterface));
		}
		
		
		components.add(managedSystemController);
		
		/*if(MVMEnvironment.PMAGENTDATABASE){
			final DatabaseUtil databaseUtil=new DatabaseUtil(new ConnectionManager(PmProperties.PMDBURL, PmProperties.PMDBUSER, PmProperties.PMDBPWD, true));
			final Map<String, CounterSetTable> counterSetTables=new HashMap<String,CounterSetTable>();
			final PmAgentDatabase pmAgentDatabase=new PmAgentDatabase(m_AgentInterface, counterSetDefinitionsController,new ManagedObjects(databaseUtil),counterSetTables,databaseUtil);
			components.add(pmAgentDatabase);
			
			final PmTransformationController transform = new PmTransformationController(new DatabaseUtil(new ConnectionManager(PmProperties.PMDBURL, PmProperties.PMDBUSER, PmProperties.PMDBPWD, true)),counterSetDefinitionsController);
			components.add(transform);
		}*/
	}

	/**
   * Checks the starter license.
   * @param cache               The licensing cache.
   * @param starterLicense      The starter license number.
   * @param licenseDescription  String description of the license.
   * @return starterLicenseOk   A boolean value that is true if the starter license is valid.
   */
//  private static boolean checkStarterLicense(final LicensingCache cache, final String starterLicense, final String licenseDescription) {
//    boolean starterLicenseOk = true;
//    logger.info("Checking Eniq starter license: " + starterLicense);   
//    
//    try {
//      // create a dummy license descriptor. CXC4011287 = FAJ 121 1874 = SIM
//      // Starter license
//      final LicenseDescriptor license = new DefaultLicenseDescriptor(starterLicense);
//      
//      // get a licensing response for the created descriptors.
//      LicensingResponse response = null;
//      
//      if (SIM_STARTER_LICENSE.equals(starterLicense)) {
//        response = cache.checkLicense(license);
//      }
//      else {
//        logger.error("Eniq starter license not recognised: " + starterLicense);
//        return false;
//      }
//      
//      if (response.isValid()) {
//        logger.info(
//            "The Eniq Starter license is valid: " + response.isValid() + " msg: " + response.getMessage()
//                + ". Engine will start normally.");
//      } else {
//        logger.error(
//                licenseDescription + " is not valid. Engine will not start. Please check the validity of the " + licenseDescription);
//        System.out
//            .println(licenseDescription + " is not valid. Engine will not start. Please check the validity of the " + licenseDescription);
//        starterLicenseOk = false;
//      }
//    } catch (Exception exc) {
//      logger.error("Error checking starter license: " + exc.toString());
//      starterLicenseOk = false;
//    }
//    return starterLicenseOk;
//  }

  /**
   * Gets the licensing cache.
   * @return cache  The LicensingCache.
   * @throws NotBoundException
   * @throws MalformedURLException
   * @throws RemoteException
   */
//  private static LicensingCache retrieveLicensingCache() throws NotBoundException, MalformedURLException, RemoteException {
//    // contact the registry and get the cache instance.
//    String serverHostName = "localhost";
//    final LicensingCache cache = (LicensingCache) Naming.lookup("rmi://" + serverHostName + ":"
//            + "1200" + "/LicensingCache");
//    return cache;
//  }
  	
	
	private void registerAgentInterface() {
		try {
			m_AgentInterface = new ManagedSystemAgentInterface(managedProtocolModel);
			final Registry registry = LocateRegistry.createRegistry(12341);
			//final Registry registry = LocateRegistry.createRegistry(Environment.REGISTRYPORT);
			registry.bind(Environment.AGENTINTERFACE, m_AgentInterface);
		} catch (final AlreadyBoundException e) {
			logger.error(classname+".registerAgentInterface(); Error binding agent to RMI registry", e);
		} catch (final RemoteException e) {
			logger.error(classname+".registerAgentInterface(); Error creating registry", e);
		}
	}

	private void exit() {
		//eniqServiceListener.shutdown();
		Collections.reverse(components);
		for(Component c : components){
			try {
				logger.info(classname+".exit(); Shutdown component: " + c.getComponentName());
				c.shutdown();
			} catch (ComponentShutdownException e) {
				logger.error(classname+".exit(); Failed to shutdown component " + 
						c.getComponentName(), e);
			}
		}
	}

	public void initialize() {
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {

				logger.info(classname+".initialize(); Mediator shutdown...");
				exit();
			}
		}));
		for(Component c : components){
			try {

				logger.info(classname+".initialize(); Initializing component: " + c.getComponentName());
				c.initialize();

			} catch( ComponentInitializationException e) {
				logger.error(classname+".initialize(); Failed to initialize component " + 
						c.getComponentName(), e);
				if(e.isFatal()){
					logger.error(classname+".initialize(); Fatal error from component " + 
							c.getComponentName() + " detected. Exiting Mediator!");
					System.exit(1);
				}
			} catch(Exception e) {
				logger.error(classname+".initialize(); Failed to initialize component " + 
						c.getComponentName(), e);
			}
		}
		logger.info(classname+".initialize(); Mediator started successfully!");
		
		startedAt = System.currentTimeMillis();
		
		//Fix for TR HR88496
		logger.info("mediator started=" + startedAt);
		//logger.info(classname + ".startedAt value is :" + startedAt);
	}

	public static void main(final String[] args) {
		final MVM manager = new MVM();
		MVM.initLogging();
		try{  
			//final LicensingCache cache = retrieveLicensingCache();
			//final boolean simStarterLicenseOk = checkStarterLicense(cache, SIM_STARTER_LICENSE, "Starter license for SIM");
			memUsageListener = MemUsageListener.getInstance();
			//eniqServiceListener = EniqServiceListener.getInstance();
			//String status = System.getProperty("EniqServiceStatus", "Disabled");
			//if((status.equalsIgnoreCase("Online"))&& simStarterLicenseOk){
				logger.info(classname+".main(); Mediator starting...");
				final Thread mainThread = new Thread(new Runnable() {
					public void run() {	
						manager.initialize();
					}
				}, "Main Thread");
				mainThread.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
					public void uncaughtException(final Thread t, final Throwable e) {
						logger.error(classname+".main(); Uncaught error detected in main thread!", e);
					}
				});
				//mainThread.setDaemon(true);
				mainThread.start();
	    
				//Fix for TR HR88496
				/* ******************************************************
				try{
					final AddServerImpl addServerImpl = new AddServerImpl();
					try{				
						Naming.rebind("AddServer", addServerImpl);
						logger.info("Server registered to already running RMI naming");
					}catch(Exception e){
						try{
							LocateRegistry.createRegistry(1099);
							Naming.bind("AddServer", addServerImpl);
							logger.info("Server registered to started RMI naming");
						}catch(Exception exception){
							logger.error("Unable to initialize LocateRegistry", exception);
						}
					}
			
				}catch(Exception e){
		
				}
			******************************************************************** */
			//}else{
			//	System.exit(0);
			//}
			//} catch (final ConnectException ce) {
			// logger.error("Failed to create connection to LicenseManager. Engine will not start.");
		}catch (final Exception e) {
			logger.error("Unknown error. Engine will not start", e);
		}
	    	    
	}
	private static void initLogging() {
	    final File f = new File(MVMEnvironment.LOGGERCONFIGFILE);
	    if (f.exists()) {
			PropertyConfigurator.configureAndWatch(MVMEnvironment.LOGGERCONFIGFILE, 10000); //10s
	    } else {
	    	logger.setLevel(Level.OFF);
	    }
	    // Everything sent to System.err is logged
	    System.setErr(new PrintStream(new
	           LoggingOutputStream(logger,
	             Level.WARN), true));
	}
	
	public static long getTime(){
		return startedAt;
	}
	
}