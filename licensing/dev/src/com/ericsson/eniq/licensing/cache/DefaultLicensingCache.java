/**
 *
 */
package com.ericsson.eniq.licensing.cache;

import java.io.File;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import rainbow.lmclient.WLSApi;
import rainbow.lmclient.WLSApiConstants;
import rainbow.lmclient.WLSApiFactory;
import rainbow.lmclient.WLSFeatureInfo;
import rainbow.lmclient.WLSServerInfo;

import com.distocraft.dc5000.common.ENIQRMIRegistryManager;
import com.distocraft.dc5000.common.StaticProperties;
import com.distocraft.dc5000.common.monitor.ServiceMonitor;
import com.ericsson.eniq.licensing.cache.MappingDescriptor.MappingType;
import com.ericsson.eniq.repository.ETLCServerProperties;

/**
 * @author ecarbjo
 *
 */
public class DefaultLicensingCache extends UnicastRemoteObject implements LicensingCache {

  private static final long serialVersionUID = -4269768113400163275L;

  private final LicensingSettings settings;

  private final FeatureMapper mapper;

  private final static Logger log = Logger.getLogger("licensing.cache.DefaultLicensingCache");

  private static final boolean isDummy = false;

  private final String DC5000_CONFIG_DIR =  "/eniq/sw/conf/" ;

  // hash table for the cache. We need to keep track of each server separately
  // so that we know what
  // data NOT to clear if a server goes down.
  private Hashtable<String, Vector<WLSFeatureInfo>> cache;

  private static AtomicBoolean isUpdated = new AtomicBoolean(false);
  private static final CountDownLatch CALL_BLOCKER = new CountDownLatch(1);
  private static final AtomicBoolean IS_SHUTTING_DOWN = new AtomicBoolean(false);
  private static String licMgrMaintCommand = "/eniq/smf/bin/eniq_service_start_stop.bsh -s licmgr -a maint";

  /**
   * Test constructor. To be used only for unit testing.
   * @param licenseSettings
   * @param mockFeatureMapper
   * @param cache
   * @param updated
   * @throws RemoteException
   */
  protected DefaultLicensingCache(final LicensingSettings licenseSettings, final FeatureMapper mockFeatureMapper,
      final Hashtable<String, Vector<WLSFeatureInfo>> cache, final AtomicBoolean updated) throws RemoteException {
    settings = licenseSettings;
    mapper = mockFeatureMapper;
    this.cache = cache;
    isUpdated = updated;
  }

  /**
   * @param propertiesFile
   *          specifies the .properties file to use.
   * @throws RemoteException
   */
  public DefaultLicensingCache(final String propertiesFile) throws RemoteException {
    super();
    if(propertiesFile == null){
      this.settings = new LicensingSettings();
    } else {
      this.settings = new LicensingSettings(propertiesFile);
    }
    this.mapper = new FeatureMapper(settings);
    init();
    log.finest("License manager started with properties file = " + propertiesFile);
  }

  public void init() throws RemoteException {

    // reload logging info
    reloadLogging();

    // set the new security manager to be able to use RMI.
    System.setSecurityManager(new LicensingCacheSecurityManager());

    // initialize the cache hashtable.
    cache = new Hashtable<String, Vector<WLSFeatureInfo>>();

    //A bit of Hack
    if(System.getProperty(StaticProperties.DC5000_CONFIG_DIR) == null){
    	log.info("Setting the property: " + StaticProperties.DC5000_CONFIG_DIR + " to value: " + this.DC5000_CONFIG_DIR);
    	System.setProperty(StaticProperties.DC5000_CONFIG_DIR, this.DC5000_CONFIG_DIR);
    }

    //Loading Static Properties
    // Create static properties.
 	try {
 		StaticProperties.reload();
 	}catch (IOException e1) {
 		log.log(Level.SEVERE, "IOException when loading StaticProperties",
					e1);
 	}

    // bind into the RMI registry.
    this.rebind();

    try{
      // update the cache to make sure that we have a fresh cache right a way.
      updateCache();// Dont use the blocking method....
    } finally {
      // unblock calls regardless
      CALL_BLOCKER.countDown();
      log.info("License Manager Cache Update Complete, unblocking calls.");
    }

    log.info("License manager initialized.");
  }

  /*
   * (non-Javadoc)
   *
   * @see com.ericsson.eniq.licensing.cache.LicensingCache#reloadLogging()
   */
  @Override
  public void reloadLogging() throws RemoteException {
    log.finest("enter reloadLogging()");

    // Reload logging configurations
    LogManager.getLogManager().reset();

    try {
      LogManager.getLogManager().readConfiguration();
      log.fine("Logger properties reloaded");
    } catch (Exception e) {
      throw new RemoteException("Reading or writing of logging properties failed: " + e.getMessage(), e);
    }

    log.finest("exit reloadLogging()");
  }

  /**
   * Checks a capacity license. Checks the cache for a single license only.
   *
   * @see com.ericsson.eniq.licensing.cache.LicensingCache#checkCapacityLicense(LicenseDescriptor, int)
   */
  @Override
  public LicensingResponse checkCapacityLicense(final LicenseDescriptor license, final int capacity)
      throws RemoteException {
    log.finest("Entering checkCapacityLicense()");

    // check to see if we should return a positive or negative response.
    if (license == null || license.getNumFeatures() == 0) {
      log.info("Received a checkLicense request on a null license, returning LICENSE_MISSING.");
      return new DefaultLicensingResponse(LicensingResponse.LICENSE_MISSING, "The license is empty!");
    } else {
      // Get the feature name:
      final String featureName = license.getName();

      log.finest("Checking licenses for feature " + featureName);
      if (cache == null || !isUpdated.get()) {

        log.warning("Cache is empty or not updated. Probable cause is that the server has not been available since the license manager was started!");
        return new DefaultLicensingResponse(LicensingResponse.LICENSE_SERVER_NOT_FOUND,
            "The license manager is not updated and therefore does not contain any licenses!");
      }

      // get the mapped feature names.
      log.finest("Iterating through licenses");

      // Set up default message for license response:
      String message = "The license is valid and capacity requirement met.";
      int licenseState = LicensingResponse.LICENSE_VALID;
       // Get all the elements in the cache:
      final ArrayList<Vector<WLSFeatureInfo>> cacheElements = Collections.list(cache.elements());

      // Search for the license in the cache:
      final WLSFeatureInfo licenseInCache = findLicenseInCache(cacheElements, featureName);

      // Check if the license can be found:
      if (licenseInCache == null) {
        message = "Valid license for feature " + featureName + " with " + license.getNumFeatures()
            + " features could not be found. ";
        licenseState = LicensingResponse.LICENSE_INVALID;
      }

      if (licenseState == LicensingResponse.LICENSE_VALID) {
        // Check if the license is expired:
        if (hasExpired(licenseInCache)) {
          message = "Valid license for feature " + featureName + " with capacity of " + licenseInCache.getNumLicenses()
              + " is expired. ";
          licenseState = LicensingResponse.LICENSE_INVALID;
        }

        // Check the capacity of the license:
        if (licenseState == LicensingResponse.LICENSE_VALID) {
          if (license.getCapacity() == -1) {
            // no capacity checks.
            log.info("Valid license for " + featureName + " found, returning LICENSE_VALID.");
            message = "The license is valid.";
          } else {
            // Get the 'hard limit' defined in the license:
            final int licensedCPUs = licenseInCache.getNumLicenses();
            if (licensedCPUs >= capacity) {
              log.info("Found valid capacity license ("+ featureName + ") with License of (" + licensedCPUs + ") for a capacity of " + capacity);
            } else {
              message = "Valid license found for feature " + featureName + " but capacity of Licence is not " +
              		"less than the required capacity on server" + ". Licensed CPUs = "
                  + licensedCPUs + ", detected CPUs = " + capacity + ". ";
              licenseState = LicensingResponse.LICENSE_INVALID;
            }
          }
        }
      }

      if (licenseState != LicensingResponse.LICENSE_VALID) {
        log.warning(message);
        return new DefaultLicensingResponse(LicensingResponse.LICENSE_INVALID, message);
      } else {
        // Return a valid response:
        return new DefaultLicensingResponse(LicensingResponse.LICENSE_VALID, message);
      }
    }
  }

  /**
   * Finds a single license in the cache.
   * @param   cacheElements   The elements in the cache
   * @param   featureName     The feature name (CXC number) of the license.
   * @return  currentLicense
   */
  private WLSFeatureInfo findLicenseInCache(final List<Vector<WLSFeatureInfo>> cacheElements, final String featureName) {
    WLSFeatureInfo currentLicense = null;
    WLSFeatureInfo tempCurrentLicense = null;
    for (int i = 0; i < cacheElements.size(); i++) {
      final ArrayList<WLSFeatureInfo> licenses = Collections.list(cacheElements.get(i).elements());

        // Go through licenses:
        for (int k = 0; k < licenses.size(); k++) {
        	tempCurrentLicense = licenses.get(k);

          log.info("Current cache " +tempCurrentLicense.getFeatureName());
          // Check if the feature name is the same:
          if (tempCurrentLicense.getFeatureName().equals(featureName)) {
            // Stop searching:
        	  currentLicense = tempCurrentLicense;
        	  log.info("Current cache found " +currentLicense.getFeatureName());
            return currentLicense;
          }
        }
    }
    return currentLicense;
  }

  private void blockForUpdate(){
    try {
      CALL_BLOCKER.await();
    } catch (InterruptedException e) {/**/}
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * com.ericsson.dc5000.licensing.cache.LicensingCache#checkLicense(com.ericsson
   * .dc5000.licensing.cache.LicenseDescriptor)
   */
 // @Override
  @Override
  public LicensingResponse checkLicense(final LicenseDescriptor license) throws RemoteException {
    log.finest("Entering checkLicense()");
    blockForUpdate();
    if (isDummy) {
      log.finer("License manager in dummy mode, returning LICENSE_VALID.");
      return new DefaultLicensingResponse(LicensingResponse.LICENSE_VALID, "License is valid");
    } else {
      // check to see if we should return a positive or negative response.
      if (license == null || license.getNumFeatures() == 0) {
        log.info("Received a checkLicense request on a null license, returning LICENSE_MISSING.");
        return new DefaultLicensingResponse(LicensingResponse.LICENSE_MISSING, "The license is empty!");
      } else {

        // get the FAJ number from the mapper. Use the CXC number if it cannot
        // be mapped.
        final Vector<String> faj = mapper.map(new DefaultMappingDescriptor(license.getFeatureNames(), MappingType.FAJ));
        String featureName = license.getName();
        if (faj != null && faj.size() > 0) {
          featureName = faj.get(0);
        }

        log.finest("Checking licenses for feature " + featureName);
        if ((cache != null && !cache.isEmpty()) && isUpdated.get()) {
          // get the mapped feature names.
          log.finest("Iterating through licenses");
          final String[] featureNames = license.getFeatureNames();

          // loop through all given feature names.
          for (int i = 0; i < featureNames.length; i++) {

            // get the enumeration for the cache.
            final Enumeration<Vector<WLSFeatureInfo>> cacheElements = cache.elements();

            // loop through the cached elements.
            while (cacheElements.hasMoreElements()) {
              final Enumeration<WLSFeatureInfo> licenses = cacheElements.nextElement().elements();

              while (licenses.hasMoreElements()) {
                // search the cache vector to find a matching license.
                final WLSFeatureInfo current = licenses.nextElement();

                if (current.getFeatureName().equals(featureNames[i])) {

                  // found a match. now check that the license is not expired.
                  if (!hasExpired(current)) {
                    if (license.getCapacity() == -1) {
                      // no capacity checks.
                      log.info("Valid license for " + featureName + " found, returning LICENSE_VALID.");

                      return new DefaultLicensingResponse(LicensingResponse.LICENSE_VALID, "The license is valid.");
                    } else {
                      // check that the capacity is in order.
                      log.finest("Checking capcity for feature " + featureName);

                      // get the number of cores that is available from the Hard
                      // limit field (via getNumLicenses for some reason...)
                      final int licCores = current.getNumLicenses();
                      int realCores = license.getCapacity();
                      if (licCores >= realCores) {
                        log.info("Found valid capacity license for a capacity of " + realCores);
                        return new DefaultLicensingResponse(LicensingResponse.LICENSE_VALID,
                            "The license is valid and capacity requirement met.");
                      } else {
                        log.info("Capacity license found but check failed since the real capacity of " + realCores
                            + " exceeds the licensed capacity of " + licCores + ". Continuing license query.");
                      }
                    }
                  } else {
                    log.info("Found matching license for feature " + featureNames[i] + " but it has already expired.");
                  }
                }
              }
            }
          }
          // a license was not found for this feature if we got to this point
          // without a return.
          log.info("License for " + featureName + " not found, returning LICENSE_INVALID.");

          if (license.getCapacity() == -1) {
            return new DefaultLicensingResponse(LicensingResponse.LICENSE_INVALID, "License for feature " + featureName
                + " not valid.");
          } else {
            return new DefaultLicensingResponse(LicensingResponse.LICENSE_INVALID, "Valid license for feature "
                + featureName + " with capacity " + license.getNumFeatures() + " could not be found.");
          }
        } else {
          log
              .warning("Cache is empty or not updated. Probable cause is that the server has not been available since the license manager was started!");
          return new DefaultLicensingResponse(LicensingResponse.LICENSE_SERVER_NOT_FOUND,
              "The license manager is not updated and therefore does not contain any licenses!");
        }
      }
    }
  }

  /**
   * Checks if the license has expired or not.
   *
   * @param current
   *          the WLSFeatureInfo to check
   * @return true if the license has expired, false otherwize.
   */
  private boolean hasExpired(final WLSFeatureInfo current) {
    if (current.getDeathDay() == WLSApi.VLS_NO_EXPIRATION) {
      // the license never expires, return false.
      return false;
    } else {
      // check the dates, and return true if "now" is after the license
      // "death day".
      final Date deathDay = new Date((current.getDeathDay()) * 1000);
      final Date now = new Date();
      return now.after(deathDay);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see com.ericsson.dc5000.licensing.cache.LicensingCache#getSettings()
   */
  //@Override
  @Override
  public LicensingSettings getSettings() throws RemoteException {
    log.finest("entering/exiting getSettings()");
    return settings.clone();
  }

  /*
   * (non-Javadoc)
   *
   * @see com.ericsson.dc5000.licensing.cache.LicensingCache#shutdown()
   */
 // @Override
  @Override
  public void shutdown() throws RemoteException {
    IS_SHUTTING_DOWN.set(true);
    ENIQRMIRegistryManager rmi = new ENIQRMIRegistryManager(settings.getServerHostName(), settings.getServerPort());
    try {
      log.info("Shutting down the license manager");
      //Adding the logic to avoid the exception: java.io.EOFException, while exiting the application
      // Locate the registry.
      try {
    	  	Registry registry = rmi.getRegistry();
    	    registry.unbind(settings.getServerRefName());
    	    UnicastRemoteObject.unexportObject(this, false);
    	  } catch (final Exception e) {
    	    throw new RemoteException("Could not unregister Licensing RMI service, quiting anyway.", e);
    	  }
      new Thread() {
    	  @Override
    	  public void run() {
    		  try {
    		        sleep(2000);
    		      } catch (InterruptedException e) {
    		        // No action to take
    		      }
      System.exit(0);
    	  }//run
      }.start();
    } catch (Exception e) {
      log.log(Level.WARNING, "Could not shut down!", e);
      throw new RemoteException("Shutdown failed exceptionally", e);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see com.ericsson.dc5000.licensing.cache.LicensingCache#status()
   */
  //@Override
  @Override
  public List<String> status() throws RemoteException {
    log.finest("entering status()");
    final List<String> retList = new LinkedList<String>();
    if(IS_SHUTTING_DOWN.get()){
      retList.add("Licensing manager is shutting down.");
    } else if(isUpdated.get()){
      retList.add("Licensing manager is running, cache updated.");
    } else {
      retList.add("Licensing manager is running, cache still updating.");
    }
    log.finest("returning from status()");
    return retList;
  }


  @Override
  public List<String> sentinal_status() throws RemoteException {
    final String[] servers = settings.getLicensingServers();

    final List<String> sstatus = new ArrayList<String>();

    for (String server : servers) {
      final Vector<WLSServerInfo> info = new Vector<WLSServerInfo>();
      // get the licensing server api:
      final WLSApi licApi = getSentinalApi();
      licApi.VLSsetServerName(server);
      final int retValue = licApi.VLSgetServerInfo(info, null, "License Server Admin checking server status");
      if (retValue == WLSApiConstants.LS_SUCCESS) {
        sstatus.add("Server: " + server + " is online.");
      } else {
        sstatus.add("Server: " + server + " seems to be offline.");
      }
    }
    return sstatus;
  }

  protected WLSApi getSentinalApi(){
    return WLSApiFactory.getWLSApi();
  }

  /*
   * (non-Javadoc)
   *
   * @see com.ericsson.dc5000.licensing.cache.LicensingCache#update()
   */
 // @Override
  @Override
  public void update() throws RemoteException {
    log.finest("entering update()");
    blockForUpdate();
    updateCache();
  }
  protected void updateCache(){
    if (isDummy) {
      log.info("Licensing manager operates in dummy mode. Nothing updated.");
    } else {
      log.fine("Starting license manager update");
      final String[] servers = settings.getLicensingServers();
      final int numRetries = findOutNumberOfRetries();
      log.finer("Retries for License Manager is " + numRetries);
      try {
        final java.util.Properties appProps = new ETLCServerProperties(System.getProperty("dc.conf.dir")
            + File.separator + "niq.rc");
        final String smfBinDir = appProps.getProperty("SMF_BIN_DIR");
        licMgrMaintCommand = smfBinDir + File.separator + "eniq_service_start_stop.bsh -s licmgr -a maint";
      } catch (IOException ie) {
        log.severe("Could not read file " + System.getProperty("CONF_DIR") + File.separator + "niq.rc");
      }
      log.finer("licMgrMaintCommand is " + licMgrMaintCommand);
      // loop through the known servers and update their respective cache.
      for (int i = 0; i < servers.length; i++) {
        int returnValue = checkLicense(servers[i]);
        if (returnValue == WLSApi.LS_SUCCESS || returnValue == WLSApi.VLS_NO_MORE_FEATURES) {
          continue;
        }

        int localNumRetries = numRetries;
        while (localNumRetries > 0) {
          log.warning("Retrying to get license/s from " + servers[i] + ".");
          returnValue = checkLicense(servers[i]);
          if (returnValue == WLSApi.LS_SUCCESS || returnValue == WLSApi.VLS_NO_MORE_FEATURES) {
            break;
          }
          localNumRetries--;
          try {
            Thread.sleep(1000);
          } catch (InterruptedException e) {
            // Do nothing. InterruptedException is not expected.
          }
          log.warning("Retries remaining " + localNumRetries + ".");
        }
        if (!isUpdated.get()) {
          log.severe("Could not get license for " + servers[i] + " even after " + numRetries
              + " retries. Putting licmgr in " + "maintenence state.");
          try {
            Runtime.getRuntime().exec(licMgrMaintCommand);
          } catch (IOException e) {
            log.severe("Failed to put licmgr in maintenence state.");
          }
          log.severe("Doing a System.exit(1).");
          System.exit(1);
        }
      }
      // clean up the potentially old entries (in case the servers have changed)
      final Enumeration<String> keys = cache.keys();
      while (keys.hasMoreElements()) {
        final String key = keys.nextElement();
        boolean notFound = true;

        // loop through the servers to find a match
        for (int i = 0; i < servers.length; i++) {
          if (servers[i].equals(key)) {
            notFound = false;
            break;
          }
        }

        if (notFound) {
          // if the key was not found, remove it from the cache Hashtable.
          cache.remove(key);
        } else {
          // if the key WAS found, then print the contents to the log.
          final Enumeration<WLSFeatureInfo> features = cache.get(key).elements();
          while (features.hasMoreElements()) {
            log.finest("Feature name in license manager: " + features.nextElement().getFeatureName());
          }
        }
      }
    }
    log.finest("exiting update()");
  }

  /**
   * @param licServerName
   * @return
   */
  private int checkLicense(final String licServerName) {

        // get the licensing server api:
        final WLSApi licApi = getSentinalApi();
    licApi.VLSsetServerName(licServerName);

        // get the new cache into a temporary vector.
        final Vector<WLSFeatureInfo> info = new Vector<WLSFeatureInfo>();
        final int retValue = licApi.VLSgetFeatureInfo(null, null, info, "Licensing Manager update");

        if (retValue == WLSApi.LS_SUCCESS) {
          log.fine("An updated list of licenses retrieved from the license server.");

          // replace the old cache with the new one. This needs to be
          // synchronized in case the update method is called from several
          // places at once.
          synchronized (cache) {
        cache.put(licServerName, info);
          }
      log.info("License manager updated from " + licServerName + " with " + info.size() + " licenses");
          isUpdated.set(true);
        } else if (retValue == WLSApi.VLS_NO_MORE_FEATURES) {

          // the server is up and running, but it does not contain any licenses.
          // The cache for this server needs to be cleared so that it will
          // conform to the server state
      synchronized (cache) {
        cache.put(licServerName, new Vector<WLSFeatureInfo>());
      }
      log.info("The server " + licServerName
              + " is online, but does not contain any licenses. Removing old licenses from the licensing manager.");
          isUpdated.set(true);
        } else {
      log.info("License manager update failed from " + licServerName + "! The license server returned error code: "
              + retValue);
        }
    return retValue;
  }


  /**
   * @return
   */
  private int findOutNumberOfRetries() {
    int retriesForLicMgr = 5;
    try {
      final Properties p = new Properties(System.getProperties());
      p.setProperty("dc5000.config.directory", System.getProperty("dc.conf.dir"));
      System.setProperties(p);
      StaticProperties.reload();
      final String numRetriesForLicMgr = StaticProperties.getProperty("numRetriesForLicMgr", "5");
      retriesForLicMgr = Integer.parseInt(numRetriesForLicMgr);
    } catch (Exception ie) {
      log.severe("Exception encountered while doing StaticProperties.reload()");
    }
    return retriesForLicMgr;
  }


  /**
   * Bind (rebind) the server to the RMI registry.
   *
   * @throws RemoteException
   */
  public void rebind() throws RemoteException {
    log.finest("entering rebind()");
    final String rmiAddr = "//" + settings.getServerHostName() + ":" + settings.getServerPort() + "/" + settings.getServerRefName();
    log.info("Trying to bind to RMI ref:  " + rmiAddr);
    ENIQRMIRegistryManager rmiRgty = new ENIQRMIRegistryManager(settings.getServerHostName(), settings.getServerPort());
    try {
    	Registry rmi = rmiRgty.getRegistry();
    	log.fine("Rebinding to rmiregistry on host: " + settings.getServerHostName() + " on port: " + settings.getServerPort() +
    			" with name: " + settings.getServerRefName());
    	rmi.rebind(settings.getServerRefName(), this);
    	log.info("Licensing server binds successfully to already running rmiregistry");
    } catch (final Exception e) {
    	if(!(ServiceMonitor.isSmfEnabled())){
    		//SMF is disabled
    		log.log(Level.SEVERE, "Unable to bind to the rmiregistry using refrence: " + rmiAddr, e);
        	throw new Error("Unable to bind to the rmiregistry using refrence: " + rmiAddr);
    	}else{
    		//SMF is enabled
    		try{
    			log.info("Starting RMI-Registry on port " + settings.getServerPort());
    			Registry rmi = rmiRgty.createNewRegistry();
    			log.info("New RMI-Registry created");
    			rmi.bind(settings.getServerRefName(), this);
    			log.fine("License Manager registered to this newly started rmiregistry.");
    		}catch (final Exception exception) {
        exception.printStackTrace();
        log.info("Could not start or locate the RMI registry. Bailing out!");
        throw new Error("Could not start or locate the RMI registry. Bailing out!");
      }
    	}//else
    }//catch
    log.info("License manager bound to " + rmiAddr);
    log.finest("exiting rebind()");
  }

  /**
   * removes the server from the RMI registry.
   *
   * @throws RemoteException
   * @throws NotBoundException
   */
  protected void unbind() throws RemoteException, NotBoundException {
    log.finest("entering unbind()");

    // Locate the registry.
    Registry registry = null;
    try {
      registry = LocateRegistry.getRegistry(settings.getServerHostName(), settings.getServerPort());
    } catch (Exception e) {
      registry = LocateRegistry.getRegistry("localhost", settings.getServerPort());
    }

    // and unbind.
    registry.unbind(settings.getServerRefName());

    log.info("License manager unbound");
    log.finest("exiting unbind()");
  }

  private static DefaultLicensingCache _this = null;

  /**
   * This is the main method that starts the cache.
   *
   * @param args
   */
  public static void main(final String args[]) {
    try {
      if (args != null && args.length > 0 && args[0].equalsIgnoreCase("dummy")) {
        _this = new DefaultLicensingCache(null);
        log.info("Started license manager in dummy mode!");
      } else {
        _this = new DefaultLicensingCache(null);
      }
      log.info("License manager bound to RMI registry.");
    } catch (Exception e) {
      log.severe("License manager initialization failed!");
      e.printStackTrace();
    }
  }

  /**
   * Detects the number of physical CPU processors on the host machine.
   * @param forceLocal If this is set to TRUE, a process is spawned directly from the current JVM
   *                   to get the CPU count. If set to FALSE, the light weight process helper is used.
   *                   NOTE: The light weight process helper is running on the same host as engine so be sure you know
   *                   where this JVM is running and what host you're getting the CPU count for. This only really effects
   *                   how engine spawns Runtime processes.
   *
   * @return The number of processors.
   */
  public static int getNumberOfPhysicalCPUs(final boolean forceLocal) {
    final CPUDetector cpuDetector = new CPUDetector();
    return cpuDetector.getNumberOfPhysicalCPUs(forceLocal);
  }

  /**
   * Detects the number of CPU cores on the host machine.
   * @param forceLocal If this is set to TRUE, a process is spawned directly from the current JVM
   *                   to get the CPU count. If set to FALSE, the light weight process helper is used.
   *                   NOTE: The light weight process helper is running on the same host as engine so be sure you know
   *                   where this JVM is running and what host you're getting the CPU count for. This only really effects
   *                   how engine spawns Runtime processes.
   * @return The number of cores.
   */
  public static int getNumberOfPhysicalCores(final boolean forceLocal) {
    final CPUDetector cpuDetector = new CPUDetector();
    return cpuDetector.getNumberOfCores(forceLocal);
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * com.ericsson.eniq.licensing.cache.LicensingCache#map(com.ericsson.eniq.
   * licensing.cache.MappingDescriptor)
   */
  @Override
  public Vector<String> map(final MappingDescriptor mapping) {
    return mapper.map(mapping);
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * com.ericsson.eniq.licensing.cache.LicensingCache#getLicenseInformation()
   */
  @Override
  public Vector<LicenseInformation> getLicenseInformation() throws RemoteException {
    blockForUpdate();
    if (cache != null) {
      final Vector<LicenseInformation> licInfo = new Vector<LicenseInformation>();

      // loop through the cache and create a list of all licenses.
      final Enumeration<String> servers = cache.keys();
      while (servers.hasMoreElements()) {
        // get the next server.
        final String server = servers.nextElement();

        // get the cache for the current server.
        final Enumeration<WLSFeatureInfo> features = cache.get(server).elements();
        while (features.hasMoreElements()) {
          // now get all the data we need for the license information object.
          final WLSFeatureInfo feature = features.nextElement();
          final String featureName = feature.getFeatureName();

          // get FAJ mappings for this CXC.
          final Vector<String> faj = mapper.map(new DefaultMappingDescriptor(new String[] { featureName },
              MappingType.FAJ));
          String fajNumber = "";
          if (faj != null && faj.size() > 0) {
            fajNumber = faj.firstElement();
          }

          // and now for the description
          final Vector<String> desc = mapper.map(new DefaultMappingDescriptor(new String[] { featureName },
              MappingType.DESCRIPTION));
          String description = "";
          if (desc != null && desc.size() > 0) {
            description = desc.firstElement();
          }

          // get and fix the birth/death days. This is given by the sentinel in
          // epoch _seconds_ instead of milliseconds, so multiply them by 1000.
          final long birthDay = (feature.getBirthDay()) * 1000;
          final long deathDay = (feature.getDeathDay()) * 1000;

          // get the capacity if it is set.
          int capacity = -1;
          if(description.indexOf("Capacity")>=0) {
        	  if (feature.getNumLicenses() != 0xFFFF) {
        		  capacity = feature.getNumLicenses();
        	  }
          }
          // now create the LicenseInformation object and add this to the return
          // vector.
          final LicenseInformation li = new DefaultLicenseInformation(featureName, fajNumber, description, deathDay,
              birthDay, server, capacity);
          licInfo.add(li);
        }
      }

      return licInfo;
    } else {
      // cache is empty, return null.
      return null;
    }
  }

  /**
   * Temporary solution until Mediation integrate with Sentinal....
   * @param component The component
   * @return The key
   * @throws RemoteException errors
   */
  @Override
  public String getKeyForComponent(final String component) throws RemoteException {
    if ("CXC4011426".equals(component) || "CXC4011275".equals(component)) {
      return "f3f84fc3-cbb6-43";
    }
    return null;
  }
}
