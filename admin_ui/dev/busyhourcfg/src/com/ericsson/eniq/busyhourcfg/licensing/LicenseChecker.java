/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.licensing;

/**
 * @author ecarbjo
 * 
 */
public interface LicenseChecker {

  /**
   * Checks if the LicenseChecker can obtain a valid license from whatever
   * license system it implements.
   * 
   * @param cxc license number of the product  
   * @return true if a valid license can be found, false otherwise.
   */
  boolean isLicenseValid(String cxc);
}
