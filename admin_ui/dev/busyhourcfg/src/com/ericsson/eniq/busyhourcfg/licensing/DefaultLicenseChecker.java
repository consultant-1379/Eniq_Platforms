/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.licensing;

import java.net.InetAddress;
import java.rmi.Naming;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ericsson.eniq.licensing.cache.DefaultLicenseDescriptor;
import com.ericsson.eniq.licensing.cache.LicenseDescriptor;
import com.ericsson.eniq.licensing.cache.LicensingCache;
import com.ericsson.eniq.licensing.cache.LicensingCacheSecurityManager;
import com.ericsson.eniq.licensing.cache.LicensingResponse;
import com.ericsson.eniq.repository.ETLCServerProperties;

/**
 * @author ecarbjo
 * 
 */
public class DefaultLicenseChecker implements LicenseChecker {

  private final Logger log = Logger.getLogger(DefaultLicenseChecker.class.getName());

  private String serverHostName = "localhost";

  private int serverPort;

  /**
   * Take the properties that contain the server port and hostname, and store
   * these values for usage in the RMI calls in isLicenseValid()
   */
  public DefaultLicenseChecker(final ETLCServerProperties props) {
    System.setSecurityManager(new LicensingCacheSecurityManager());

    if (props != null) {
      if (props.getProperty("ENGINE_HOSTNAME") != null) {
        serverHostName = props.getProperty("ENGINE_HOSTNAME").trim();
        if (this.serverHostName == null || this.serverHostName.equals("")) {

          try {
            this.serverHostName = InetAddress.getLocalHost().getHostName();
          } catch (java.net.UnknownHostException ex) {
            log.log(Level.FINER, "Could not determine hostname for localhost", ex);
            this.serverHostName = "localhost";
          }
        }
      }

      this.serverPort = 1200;
      if (props.getProperty("ENGINE_PORT") != null) {
        final String portStr = props.getProperty("ENGINE_PORT").trim();
        if (portStr != null && !portStr.equals("")) {
          try {
            this.serverPort = Integer.parseInt(portStr);
          } catch (NumberFormatException nfe) {
            log.config("Could not parse number from string " + portStr + "\". Using default port.");
          }
        }
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.busyhourcfg.licensing.LicenseChecker#isLicenseValid()
   */
  @Override
  public boolean isLicenseValid(final String cxc) {
    try {
      final LicensingCache cache = (LicensingCache) Naming.lookup("rmi://" + this.serverHostName + ":"
          + this.serverPort + "/LicensingCache");

      if (cache == null) {
        log.severe("Could not connect to the LicenseManager at " + this.serverHostName
            + ". Please verify that LicenseManager service is running and try again.");
        return false;
      } else {
        // Get license for given CXC
        final LicenseDescriptor license = new DefaultLicenseDescriptor(cxc);
        // get a licensing response for the created descriptors.
        final LicensingResponse response = cache.checkLicense(license);

        if (response.isValid()) {
          log.fine("The " + cxc + " license is valid.");
          return true;
        } else {
          log.severe(cxc + " license is not valid! Please check the validity of the license before proceeding.");
          return false;
        }
      }
    } catch (Exception e) {
      log
          .severe("Caught an exception while trying to validate the license for " + cxc + ". The fault was: "
              + e.getMessage());
      return false;
    }
  }
}
