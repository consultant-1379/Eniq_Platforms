package com.ericsson.eniq.licensing.cache;

import java.rmi.Naming;
import java.rmi.registry.Registry;
import java.text.DateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.distocraft.dc5000.common.ENIQRMIRegistryManager;
import com.distocraft.dc5000.common.RmiUrlFactory;
import com.ericsson.eniq.licensing.cache.MappingDescriptor.MappingType;

/**
 * Program to control the LicensingCache
 * 
 * @author ecarbjo
 */
public class LicensingCacheAdmin {

  final private LicensingSettings settings;

  private static Logger log;

  /**
   * Default constructor
   * 
   * @throws Exception
   */
  public LicensingCacheAdmin() throws Exception {
    settings = new LicensingSettings();
    log = Logger.getLogger("licensing.cache.LicensingCacheAdmin");
    log.finer("LicensingCacheAdmin instantiated.");
  }

  /**
   * Constructor for using a specific host/port.
   * 
   * @param serverHostName
   * @param serverPort
   */
  public LicensingCacheAdmin(final String serverHostName, final int serverPort) {
    this.settings = new LicensingSettings();
    this.settings.setServerHostName(serverHostName);
    this.settings.setServerPort(serverPort);
  }

  /**
   * @param args
   */
  public static void main(final String args[]) {
    LicensingCacheAdmin admin;

    try {

      System.setSecurityManager(new LicensingCacheSecurityManager());

      admin = new LicensingCacheAdmin();

      if (args.length < 1) {
        System.out.println("Too few arguments");
        System.exit(1);
      }

      if (args[0].equalsIgnoreCase("stop")) {
        admin.shutdown();
      } else if (args[0].equalsIgnoreCase("status")) {
        admin.status();
      } else if (args[0].equalsIgnoreCase("update")) {
        admin.update();
      } else if (args[0].equalsIgnoreCase("map") && args.length > 2) {
        admin.map(args);
      } else if (args[0].equalsIgnoreCase("getlicinfo")) {
        admin.printLicenseInfo();
      } else if (args[0].equalsIgnoreCase("isvalid")) {
        if (args.length < 2) {
          System.out.println("Too few arguments");
          System.exit(2);
        } else {
          admin.isValid(args[1]);
        }
      }
      else if (args[0].equalsIgnoreCase("checkcapacity")) {
          if (args.length < 2) {
            System.out.println("Too few arguments");
            System.exit(2);
          } else {
            admin.checkCapacity(args[1]);
          }
        }

    } catch (java.rmi.UnmarshalException e) {
      // Exception, because connection breaks, when cache is shutdown
      System.exit(3);
    } catch (java.rmi.ConnectException e) {
      System.err.println("Connection to the license manager failed.");
      log.severe("Connection to the license manager failed.");
      System.exit(2);
    } catch (java.rmi.NotBoundException e) {
      System.err.println("The license manager is not bound to the RMI registry.");
      log.severe("The license manager is not bound to the RMI registry.");
      System.exit(4);
    } catch (Exception e) {
      log.warning("Generic exception caught");
      System.err.println("\n");
      e.printStackTrace(System.err);
      System.exit(1);
    }

    System.exit(0);
  }

  /**
   * Map the given feature name onto a type. Interface will be used as the
   * default type.
   * 
   * @param featureName
   * @param mappingType
   * @throws Exception
   */
  public void map(final String[] args) throws Exception {
    log.finest("enter map()");

    final LicensingCache cache = connect();

    // get the first argument that should be the type.
    final String mappingType = args[1];

    // set the mapping type to INTERFACE mapping by default.
    MappingType type = null;
    if (mappingType.equalsIgnoreCase("faj")) {
      type = MappingType.FAJ;
    } else if (mappingType.equalsIgnoreCase("description")) {
      type = MappingType.DESCRIPTION;
    } else if (mappingType.equalsIgnoreCase("interface")) {
      type = MappingType.INTERFACE;
    } else {
      System.out.println("Unknown mapping type: " + mappingType + ". Please specify one of: faj, interface, description.");

      return;
    }
    
    // now get all feature names.
    final Vector<String> featureNames = new Vector<String>(); 
    final Pattern p = Pattern.compile("^\\s*\\\"?(CXC[0-9]+)\\\"?\\s*$");

    for (int i = 2; i < args.length; i++) {
      final Matcher match = p.matcher(args[i]);
      if (match.find() && match.group(1) != null) {
        featureNames.add(match.group(1));
      } else {
        System.err.println(args[i] + " was discarded because it is not a valid feature name.");
      }
    }

    if (featureNames.size() < 1) {
      System.out.println("No features to check.");
    } else {
      String[] featureArray = null; 
      if (type == MappingType.INTERFACE) {
        // move the elements from the vector onto the array.
        featureArray = new String[featureNames.size()];
        for (int i = 0; i<featureArray.length; i++) {
          featureArray[i] = featureNames.get(i);
        }
      } else {
        // if the type is not interface we need to truncate the feature list to
        // only contain one entry. Only interface mapping allows multiple feature


        // names.
        if (args.length > 3) {
          System.out.println("Truncating feature name list to " + featureNames.get(0) + " since mapping type is set to " + args[1]);

        }
        featureArray = new String[] { featureNames.get(0) };
      }
  
      final MappingDescriptor md = new DefaultMappingDescriptor(featureArray, type);
      final Vector<String> mappings = cache.map(md);
  
      // print the returned elements.
      if (mappings == null || mappings.size() < 1) {
        System.out.println("No mappings found.");
      } else {
        final Enumeration<String> elem = mappings.elements();
        while (elem.hasMoreElements()) {
          System.out.println(elem.nextElement());
        }
      }
    }

    log.finest("exit map()");
  }

  public void isValid(final String feature) throws Exception {
    log.finest("enter isValid()");

    final LicensingCache cache = connect();

    log.info("Checking validity of license for feature " + feature);
    final LicensingResponse response = cache.checkLicense(new DefaultLicenseDescriptor(feature));

    System.out.println("License for feature " + feature + " is " + (response.isValid() ? "" : "not ") + "valid");

    log.finest("exit isValid()");

    // do a system exit so that the bash script can retrieve the exit code.
    System.exit(response.isValid() ? 0 : 1);
  }

  /**
   * Connect to the remote LicensingCache object.
   * 
   * @return a reference to the LicensingCache
   * @throws Exception
   */
  private LicensingCache connect() throws Exception {
    log.finest("enter connect()");

//    final String rmiURL = "//" + settings.getServerHostName() + ":" + settings.getServerPort() + "/"
//        + settings.getServerRefName();
//
//    log.info("Looking up for RMI ref: " + rmiURL);
    
    LicensingCache cache = null ;
    try{
    	cache = (LicensingCache) Naming.lookup(RmiUrlFactory.getInstance().getLicmgrRmiUrl());
    }catch(final Exception e){
    	log.info("Exception comes: " + e.getMessage());
    	log.info("Trying Registry way.....");
    	Registry rmi = (new ENIQRMIRegistryManager(settings.getServerHostName(), settings.getServerPort())).getRegistry();
    	cache = (LicensingCache)rmi.lookup(settings.getServerRefName());
    }   
    
    if (cache == null) {
      log.info("Naming lookup returned null");
    }

    log.finest("exit connect()");
    return cache;
  }

  /**
   * Shut down the LicensingCache
   * 
   * @throws Exception
   */
  public void shutdown() throws Exception {
    log.finest("enter shutdown()");

    final LicensingCache cache = connect();
    System.out.println("Shutting down...");

    log.info("Requesting shutdown of the license manager");
    cache.shutdown();

    System.out.println("Shutdown successfully requested");
    log.finest("exit shutdown()");
  }

  /**
   * Updates the licensing cache.
   * 
   * @throws Exception
   */
  public void update() throws Exception {
    log.finest("enter update()");
    final LicensingCache cache = connect();
    System.out.println("Updating license manager");

    log.fine("Requesting license manager update");
    cache.update();

    log.finest("exit update()");
  }

  /**
   * Test the connection to the server object.
   * 
   * @return true if the connections was successful.
   * @throws Exception
   */
  public boolean testConnection() throws Exception {
    log.finest("enter testConnection()");
    try {
      connect();
    } catch (Exception e) {
      log.fine("Connection not active");
      log.finest("exit testConnection()");
      return false;
    }

    log.finest("exit testConnection()");
    return true;
  }

  /**
   * Shows the status of the current LicensingCache
   * 
   * @throws Exception
   */
  public void status() throws Exception {
    log.finest("enter status()");
    final LicensingCache cache = connect();

    System.out.println("Getting status...");
    log.finer("Getting status...");

    final List<String> al = cache.status();

    final Iterator<String> i = al.iterator();
    while (i.hasNext()) {
      final String t = i.next();
      System.out.println(t);
      log.finer(t);
    }

    System.out.println("Finished successfully");
    log.finest("exit status()");
  }

  /**
   * Print license information available from each server in LSHOST
   */
  private void printLicenseInfo() throws Exception {
    log.finest("enter printLicenseInfo()");
    final LicensingCache cache = connect();

    System.out.println("Getting status...");
    log.finer("Getting status...");

    final Vector<LicenseInformation> information = cache.getLicenseInformation();

    final Enumeration<LicenseInformation> elements = information.elements();

    final DateFormat df = DateFormat.getDateTimeInstance();

    while (elements.hasMoreElements()) {
      final LicenseInformation li = elements.nextElement();
      System.out.println("Feature name     : " + li.getFeatureName());
    //The following is modified as per the JIRA EQEV-28077
		if (li.getFeatureName().equals("CXC4011992")) {
			System.out.println("Feature identity : 1/FAJ 121 4356");
			System.out.println("Description      : Ericsson Network Analytics Ad-Hoc Enabler");
		} else if (li.getFeatureName().equals("CXC4011993")) {
			System.out.println("Feature identity : 1/FAJ 121 4446");
			System.out.println("Description      : Network Analytics Consumer Enabler");
		} else {
			System.out.println("Feature identity : " + li.getFajNumber());
			System.out.println("Description      : " + li.getDescription());
		}
      // the birth day and death day of the feature is given in seconds, the
      // date constructor wants milliseconds so we multiply by 1000.
      final Date birthDay = new Date(li.getBirthDay());     
      System.out.println("Start date       : " + df.format(birthDay));
      
      if(li.getDeathDay() < 0){
    	  System.out.println("Expiration date  : License has no expiration.");
      }
      else{
      final Date deathDay = new Date(li.getDeathDay());
      System.out.println("Expiration date  : " + df.format(deathDay));
      }
      
      if (li.getCapacity() != -1) {
        System.out.println("Capacity         : " + li.getCapacity());
      }
      System.out.println();
    }

    System.out.println("Finished successfully");
    log.finest("exit printLicenseInfo()");    
  }
  
  /**
   * Checks the capacity of a license.
   * @param feature     The license to check.


   * @throws Exception
   */
  public void checkCapacity(final String feature) throws Exception {
    log.finest("enter checkCapacity()");
    final LicensingCache cache = connect();

    LicenseInformation li;
    String desc = null;
    String name =null;
    int numberOfCPUs = -1;
    
    final Vector<LicenseInformation> information = cache.getLicenseInformation();

    final Enumeration<LicenseInformation> elements = information.elements();
    
    log.info("Checking license capacity for feature " + feature);

    final DefaultLicenseDescriptor license = new DefaultLicenseDescriptor(feature);
    
    while(elements.hasMoreElements()) {
    	li = elements.nextElement();
    	desc = li.getDescription();
    	name =li.getFeatureName();
    	
    	if((desc.indexOf("Capacity")>=0)&&(name.compareToIgnoreCase(feature)==0)) {
    		numberOfCPUs = DefaultLicensingCache.getNumberOfPhysicalCPUs(true/*we can spawn the process form the local jvm*/);
        }
    }
    
    license.setCapacity(numberOfCPUs);
    final LicensingResponse response = cache.checkCapacityLicense(license, numberOfCPUs);

    System.out.println("Capacity for feature " + feature + " is " + ((response.isValid() && numberOfCPUs !=-1) ? "valid" : "not valid. Capacity is valid only for Starter Capacity license."));

    // do a system exit so that the bash script can retrieve the exit code.
    System.exit(response.isValid() ? 0 : 1);
  }
}
