/**
 * 
 */
package com.ericsson.eniq.licensing.cache;

import java.io.IOException;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ecarbjo Data structure class for the mappings.
 */
public class FeatureMapping {

  final private String featureName;

  private String fajNumber;

  final private String interfaceName;

  final private String description;

  final private String reportPackage;

  private static Logger log;

  /**
   * @param featureName
   *          the feature name that is mapped. This should be a CXC number that
   *          the sentinel server can map.
   * @param mapstring
   *          the string that is mapped onto the feature name. It could be
   *          either interface or report package name.
   */
  public FeatureMapping(final String featureName, final String mapstring) {
    log = Logger.getLogger("licensing.cache.FeatureMapping");
    this.featureName = featureName;

    if (mapstring.startsWith("BO")) {
      this.interfaceName = null;
      this.reportPackage = mapstring;
    } else {
      this.interfaceName = mapstring;
      this.reportPackage = null;
    }

    this.fajNumber = null;
    this.description = null;
  }

  /**
   * @param featureName
   *          the feature name that is mapped. This should be a CXC number that
   *          the sentinel server can map.
   * @param fajNumber
   *          the string that is mapped onto the feature name.
   * @param description
   *          the description for this feature name.
   */
  public FeatureMapping(final String featureName, final String fajNumber, final String description) {
    log = Logger.getLogger("licensing.cache.FeatureMapping");
    this.fajNumber = fajNumber;
    this.featureName = featureName;
    this.description = description;
    this.interfaceName = null;
    this.reportPackage = null;
  }

  /**
   * This will take a string in the format (as a regexp:) ^(CXC[0-9]+)::([^:]+)(::(FAJ[\\ 0-9]+))?(/[0-9]+G)?$ or as an
   * example:
   * 
   * CXC4010583::Ericsson Network IQ Starter::FAJ 121 1137
   * 
   * CXC4010584::INTF_DC_Z_ALARM
   * 
   * CXC4010585::BO_E_BSS CXC4010585::BO_E_STN CXC4010585::BO_E_CMN_STS
   * 
   * CXC4010924::Ericsson SGSN-MME 3G Event Tech Pack::FAJ 121 1533/3G (Note extra /3G for ENIQ EVENTS)
   * 
   * @param mapFileString
   * @throws IOException
   */
  public FeatureMapping(final String mapFileString) throws IOException {
    log = Logger.getLogger("licensing.cache.FeatureMapping");

    log.finest("enter FeatureMapping(mapFileString)");
    // create a new pattern for matching the mapping file syntax.
    final Pattern p = Pattern.compile("^(CXC[0-9]+)::([^:]+)(::(FAJ[\\ 0-9]+))?(/[0-9]+G)?$");
    final Matcher match = p.matcher(mapFileString);

    if (match.find()) {
      // check the tokens
      // insert the tokens into the right field.
      featureName = match.group(1);

      // the regexp matched a description and FAJ number.
      if (match.group(4) == null) {

        if (match.group(2).startsWith("BO")) {
          // this is an report package
          interfaceName = null;
          reportPackage = match.group(2);
        } else {
          // this is an interface
          interfaceName = match.group(2);
          reportPackage = null;
        }

        // null the description and faj.
        fajNumber = null;
        description = null;
      } else {
        // the groups should have matched a DESC/FAJ
        description = match.group(2);
        fajNumber = match.group(4);

        if (match.group(5) != null) {
          fajNumber = fajNumber + match.group(5);
        }

        // null the interface name and report package
        interfaceName = null;
        reportPackage = null;
      }
    } else {
      // null all descriptors
      interfaceName = null;
      fajNumber = null;
      description = null;
      featureName = null;
      reportPackage = null;

      log.warning("Could not parse the string " + mapFileString);
      throw new IOException("Could not parse the string " + mapFileString);
    }
  }

  /**
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * @return the mapped feature name
   */
  public String getFeatureName() {
    return featureName;
  }

  /**
   * @return the string that is mapped onto the feature name.
   */
  public String getFajNumber() {
    return fajNumber;
  }

  /**
   * @return the string that is mapped onto the feature name.
   */
  public String getInterfaceName() {
    return interfaceName;
  }

  /**
   * @return the report package
   */
  public String getReportPackage() {
    return reportPackage;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof FeatureMapping) {
      final FeatureMapping fm = (FeatureMapping) obj;

      // return true if all the fields match, false otherwise.
      return (stringsMatch(this.featureName, fm.getFeatureName())
          && stringsMatch(this.interfaceName, fm.getInterfaceName())
          && stringsMatch(this.description, fm.getDescription()) && stringsMatch(this.fajNumber, fm.getFajNumber()) && stringsMatch(
          this.reportPackage, fm.getReportPackage()));
    } else {
      return false;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return super.hashCode();
  }

  /**
   * Check to see if two strings match. Used as a helper method in equals()
   * 
   * @param str1
   *          the first strings
   * @param str2
   *          the second string
   * @return true if both strings match. They are determined to match if both
   *         strings contains the same characters, or if both strings are null
   */
  public boolean stringsMatch(final String str1, final String str2) {
    // first check for null
    if (str1 == null) {
      return (str2 == null);
    } else {
      // str1 is not null, so now we can check it.
      return str1.equals(str2);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    if (featureName == null) {
      return "Empty mapping";
    } else {
      String retString = featureName;

      // append the mappings. Only one or two of these will be used.
      if (fajNumber != null) {
        retString += " --> " + fajNumber + " (FAJ)";
      }

      if (interfaceName != null) {
        retString += " --> " + interfaceName + " (IFACE)";
      }

      if (description != null) {
        retString += " --> " + description + " (DESC)";
      }

      if (reportPackage != null) {
        retString += " --> " + reportPackage + " (REPORTPACKAGE)";
      }

      return retString;
    }
  }
}
