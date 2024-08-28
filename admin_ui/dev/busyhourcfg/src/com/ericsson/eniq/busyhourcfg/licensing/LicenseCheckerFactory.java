/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.licensing;

import com.ericsson.eniq.repository.ETLCServerProperties;

/**
 * @author eheijun
 *
 */
public class LicenseCheckerFactory {
  
  private static LicenseChecker _instance;
  
  private LicenseCheckerFactory() {
    super();
  }
  
  public static LicenseChecker getLicenseChecker(final ETLCServerProperties etlcServerProperties) {
    if (_instance == null) {
      _instance = new DefaultLicenseChecker(etlcServerProperties); 
    }
    return _instance;
  }
  
  public static void setLicenseChecker(final LicenseChecker instance) {
    _instance = instance;
  }

}
