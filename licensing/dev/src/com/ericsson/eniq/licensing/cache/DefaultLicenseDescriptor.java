/***
 * This is the default implementation of the LicenseDescriptor class used to
 * transfer licensing information from the client to the server. It will
 * probably need changing to suit the final version of the protocol.
 */

package com.ericsson.eniq.licensing.cache;

/**
 * @author ecarbjo
 */

public class DefaultLicenseDescriptor implements LicenseDescriptor {

  private static final long serialVersionUID = 4190587807808476403L;

  private final String license;

  private final String[] licenseArray;

  private int capacity = -1;

  /**
   * Default constructor.
   * 
   * @param license
   *          the license string
   */
  public DefaultLicenseDescriptor(final String license) {
    this.license = license;

    // save away the splitted array for use in the getFeatureNames() method.
    if (this.license == null || this.license.equals("")) {
      this.licenseArray = null;
    } else {
      // split the license string on ",". Then also trim the whitespace.
      this.licenseArray = this.license.split("\\s*,\\s*");
      for (int i = 0; i < this.licenseArray.length; i++) {
        this.licenseArray[i] = this.licenseArray[i].trim();
      }
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see com.distrocraft.dc5000.licensing.ElementDescriptor#getName()
   */
  public String getName() {
    return license;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  public String toString() {
    return getName();
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.dc5000.licensing.cache.LicenseDescriptor#getCapacity()
   */
  public int getCapacity() {
    return capacity;
  }

  /**
   * Sets the capacity. This is the number of CPU cores that the license is for.
   */
  public void setCapacity(final int capacity) {
    this.capacity = capacity;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.licensing.cache.LicenseDescriptor#getLicenses()
   */
  public String[] getFeatureNames() {
    return licenseArray.clone();
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.licensing.cache.LicenseDescriptor#getNumFeatures()
   */
  public int getNumFeatures() {
    if (licenseArray == null) {
      return 0;
    } else {
      return licenseArray.length;
    }
  }
}
