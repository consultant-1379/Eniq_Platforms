/**
 ** 
 */
package com.ericsson.eniq.licensing.cache;

/**
 * @author ecarbjo
 * 
 */
public interface MappingDescriptor extends ElementDescriptor {

  public static enum MappingType {
    DESCRIPTION, FAJ, INTERFACE, REPORTPACKAGE
  };

  /**
   * This returns an array of licenses created from the name attribute of the
   * ElementDescriptor. The name can be a colon separated list, or just a single
   * element.
   * 
   * @return a set of the license(s) in this descriptor
   */
  String[] getFeatureNames();

  /**
   * @return the corresponding TYPE_ constant
   */
  MappingType getType();

}
