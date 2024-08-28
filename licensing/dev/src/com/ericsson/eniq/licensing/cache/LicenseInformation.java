/***
 * This interface represents a light version of the WLSFeatureInfo class. It
 * will primarily be used to transfer information about the licenses in the
 * license manager cache to other parts of ENIQ via RMI.
 */
package com.ericsson.eniq.licensing.cache;

import java.io.Serializable;

/**
 * @author ecarbjo
 */
public interface LicenseInformation extends Serializable {

  String getFajNumber();

  String getFeatureName();

  String getDescription();

  long getBirthDay();

  long getDeathDay();
  
  String getServer();

  int getCapacity();
  
}
