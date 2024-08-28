/**
 ** 
 */
package com.ericsson.eniq.licensing.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ericsson.eniq.licensing.cache.LicensingCacheAdmin;
import com.ericsson.eniq.licensing.cache.LicensingCacheSecurityManager;
import com.ericsson.eniq.licensing.cache.LicensingSettings;
import rainbow.lmclient.*;

/**
 * @author ecarbjo
 * 
 */
public class LicensingServerAdmin {

  final private LicensingSettings settings;

  // the default string to use as a version if none is given.
  private Logger log;

  /**
   * @param args
   */
  public static void main(final String[] args) {
    if (args.length < 1) {
      System.exit(1);
    }

    final LicensingServerAdmin admin = new LicensingServerAdmin();
    System.setSecurityManager(new LicensingCacheSecurityManager());

    if (args[0].equalsIgnoreCase("uninstall") && args.length > 1) {
      admin.uninstall(args[1]);
    } else if (args[0].equalsIgnoreCase("serverstatus")) {
      admin.printServerStatus();
    } else if (args[0].equalsIgnoreCase("listserv")) {
      admin.printServerList();
    } else if (args[0].equalsIgnoreCase("install") && args.length > 1) {
      admin.install(args[1]);
    } else if (args[0].equalsIgnoreCase("uninstall") && args.length > 1) {
      admin.uninstall(args[1]);
    } else {
      System.out.println("Unknown command \"" + args[0] + "\"");
      System.exit(1);
    }

    System.exit(0);
  }

  /**
   * Installs new licenses from a given license file.
   * 
   * @param filename
   *          the file that contains the licenses to install
   */
  private void install(final String filename) {
    if (filename == null || filename.equals("")) {
      System.out.println("File name is empty.");
    } else {
      // create the file object for the file.
      final File licFile = new File(filename);
      try {
        // init the readers
        final BufferedReader br = new BufferedReader(new FileReader(licFile));
        String in;

        // get the api for the sentinel
        final WLSApi licApi = WLSApiFactory.getWLSApi();

        // and get the servers
        final String[] servers = settings.getLicensingServers();

        try {
          while ((in = br.readLine()) != null) {
            // find the cxc number in the license.
            // NOTE: If there is a way to do this with the help of the rainbow
            // API that method should be used. As I couldn't find such a method
            // I'm using a regexp at the moment.

            // the regexp below is hard to read with all the escaping. Without
            // the extra java escaping it's \s[\"\']?(CXC[0-9]+)[\"\']?\s
            final Pattern p = Pattern.compile("\\s[\\\"\\\']?(CXC[0-9]+)[\\\"\\\']?\\s");
            final Matcher match = p.matcher(in);
            String cxc = "";
            if (match.find()) {
              cxc = match.group(1);
              for (int i = 0; i < servers.length; i++) {
                // change the server to the next in the list
                licApi.VLSsetServerName(servers[i]);

                // remove (temporary) the old license before adding a new
                // version of the CXC (if it exists)
                licApi.VLSdeleteFeature(cxc, "", "Removing old version of license before installation", null);

                // and install the license
                final int retValue = licApi.VLSaddFeatureToFile(in, "Adding license from license server admin", null);

                if (retValue == WLSApi.LS_SUCCESS) {
                  System.out.println("Installed feature " + cxc + " on " + servers[i]);
                  log.info("Installed feature " + cxc + " on " + servers[i]);
                } else if (retValue == WLSApi.VLS_DUPLICATE_LICENSE) {
                  System.out.println("The feature " + cxc + " is already installed on " + servers[i]);
                } else {
                  log.warning("Could not install feature " + cxc + " on " + servers[i] + " (Code: " + retValue + ")");
                  System.out.println("Could not install feature " + cxc + " on " + servers[i] + " (Code: " + retValue
                      + ")");
                }
              }
            } else {
              System.out.println("Could not find CXC number in: " + in);
            }
          }
        } catch (IOException e) {
          System.out.println("Could not read " + licFile.getAbsolutePath());
        } finally {
          try {
            // close the reader
            br.close();
          } catch (Exception e) {
            System.out.println("Caught an exception when trying to close the file reader.");
          }

          // update the cache
          try {
            final LicensingCacheAdmin cacheAdmin = new LicensingCacheAdmin();
            cacheAdmin.update();
          } catch (Exception e) {
            System.out.println("Could not update the cache!");
          }
        }
      } catch (FileNotFoundException e) {
        System.out.println("Cannot find the file " + licFile.getAbsolutePath());
      }
    }
  }

  /**
   * Uninstall a given license from the servers in LSHOST
   * 
   * @param featureName
   *          the feature name to remove.
   */
  private void uninstall(final String featureName) {
    // get the api for the sentinel
    final WLSApi licApi = WLSApiFactory.getWLSApi();

    // and get the servers
    final String[] servers = settings.getLicensingServers();

    for (int i = 0; i < servers.length; i++) {
      // change the server to the next in the list
      licApi.VLSsetServerName(servers[i]);

      // and install the license
      final int retValue = licApi.VLSdeleteFeature(featureName, "", "Removing license via the license server admin",
          null);

      if (retValue == WLSApi.LS_SUCCESS) {
        log.info("Removed feature " + featureName + " from " + servers[i]);
        System.out.println("Removed feature " + featureName + " from " + servers[i]);
      } else if (retValue == WLSApi.VLS_NO_SUCH_FEATURE || retValue == WLSApi.VLS_BAD_SERVER_MESSAGE) {
        System.out.println("Feature " + featureName + " does not exist in " + servers[i]);
      } else {
        log.warning("Could not remove feature " + featureName + " from " + servers[i] + " (Code: " + retValue + ")");
        System.out.println("Could not remove feature " + featureName + " from " + servers[i] + " (Code: " + retValue
            + ")");
      }
    }

    // update the cache
    try {
      final LicensingCacheAdmin cacheAdmin = new LicensingCacheAdmin();
      cacheAdmin.update();
    } catch (Exception e) {
      System.out.println("Could not update the cache!");
    }
  }

  /**
   * Print a list of servers available on the local sub-net
   */
  private void printServerList() {
    log.info("Listing servers on local subnet.");
    System.out.println("Listing active servers on local subnet:");

    // create the discoverInfo vector
    final Vector<WLSDiscoverInfo> discoverInfo = new Vector<WLSDiscoverInfo>();

    // get the api
    final WLSApi licApi = WLSApiFactory.getWLSApi();

    // get all servers on the subnet (include null for no specific query)
    licApi.VLSDiscover(null, null, "LicensingServerAdmin discovery request", discoverInfo, 0, 0, null);

    // will VLSDiscover null the vector if it isn't used?
    if (discoverInfo == null || discoverInfo.size() == 0) {
      System.out.println("No servers found.");
    } else {
      // get an iterator and output an empty line
      final Iterator<WLSDiscoverInfo> it = discoverInfo.iterator();
      System.out.println();

      // print all servers.
      while (it.hasNext()) {
        final WLSDiscoverInfo info = it.next();
        
        try {
          final InetAddress addr = InetAddress.getByName(info.getIpAddress());
          System.out.println("Server: " + info.getIpAddress() + " (" + addr.getCanonicalHostName() + ")");
        } catch (UnknownHostException e) {
          System.out.println("Server: " + info.getIpAddress());
        }
      }
    }
    System.out.println();
  }

  /**
   * Print the status of the servers set in LSHOST
   */
  private void printServerStatus() {
    System.out.println("Showing current server status:");
    // get the server list to use.
    final String[] servers = settings.getLicensingServers();

    // loop through the known servers.
    for (int i = 0; i < servers.length; i++) {
      final Vector<WLSServerInfo> info = new Vector<WLSServerInfo>();

      // get the licensing server api:
      final WLSApi licApi = WLSApiFactory.getWLSApi();
      licApi.VLSsetServerName(servers[i]);
      final int retValue = licApi.VLSgetServerInfo(info, null, "License Server Admin checking server status");
      if (retValue == WLSApiConstants.LS_SUCCESS) {
        // TODO: Add some more information from the info vector?
        System.out.println("Server: " + servers[i] + " is online.");
      } else {
        System.out.println("Server: " + servers[i] + " seems to be offline.");
      }
    }
    log.finest("exit remove()");
  }

  /**
   * Default contructor.
   */
  public LicensingServerAdmin() {
    settings = new LicensingSettings();
    init();
  }

  /**
   * contructor.
   */
  public LicensingServerAdmin(final String settingsFile) {
    settings = new LicensingSettings(settingsFile);
    init();
  }

  /**
   * Initialize the logger.
   */
  public void init() {
    log = Logger.getLogger("licensing.server.LicensingServerAdmin");
    reloadLogging();
  }

  /**
   * Re-reads the logging properties.
   */
  public void reloadLogging() {
    log.finest("enter reloadLogging()");

    // Relaod logging configurations
    LogManager.getLogManager().reset();

    try {
      LogManager.getLogManager().readConfiguration();
      log.fine("Logger properties reloaded");
    } catch (Exception e) {
    }

    log.finest("exit reloadLogging()");
  }
}
