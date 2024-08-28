/***
 * This interface extends a ElementDescriptor to describe a given license. No extra functionality
 * has been added here (yet).
 */
package com.ericsson.eniq.licensing.cache;

/**
 * @author ecarbjo
 */

public interface LicenseDescriptor extends ElementDescriptor {

  /**
   * Gets the number of CPU cores for this license.
   * 
   * @return the number of cores that this license is to be checked for, or -1
   *         if not applicable.
   */
  int getCapacity();
  
  /**
   * 
   * @param capacity: set the number of CPUs
   */
  void setCapacity(final int capacity);

  /**
   * This returns an array of licenses created from the name attribute of the
   * ElementDescriptor. The name can be a colon separated list, or just a single
   * element.
   * 
   * @return a set of the license(s) in this descriptor
   */
  String[] getFeatureNames();

  /**
   * This method reports the number of feature names in this descriptor.
   * 
   * @return the number of feature names that this descriptor contains.
   */
  int getNumFeatures();
}
