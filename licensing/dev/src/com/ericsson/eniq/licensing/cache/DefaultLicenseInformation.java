/**
 * 
 */
package com.ericsson.eniq.licensing.cache;

/**
 * @author ecarbjo
 * 
 */
public class DefaultLicenseInformation implements LicenseInformation {

  private static final long serialVersionUID = 7715985904729427155L;

  final private String server;

  final private long birthday;

  final private long deathday;

  final private String description;

  final private String fajNumber;

  final private String cxcNumber;

  final private int capacity;

  /**
   * Default constructor
   * 
   * @param cxcNumber
   * @param fajNumber
   * @param description
   * @param deathday
   * @param birthday
   * @param server
   */
  public DefaultLicenseInformation(final String cxcNumber, final String fajNumber, final String description, final long deathday,
      final long birthday, final String server) {
    this.server = server;
    this.birthday = birthday;
    this.deathday = deathday;
    this.description = description;
    this.cxcNumber = cxcNumber;
    this.fajNumber = fajNumber;
    this.capacity = -1;
  }

  /**
   * @param cxcNumber
   * @param fajNumber
   * @param description
   * @param deathday
   * @param birthday
   * @param server
   * @param capacity
   */
  public DefaultLicenseInformation(final String cxcNumber, final String fajNumber, final String description, final long deathday,
      final long birthday, final String server, final int capacity) {
    this.server = server;
    this.birthday = birthday;
    this.deathday = deathday;
    this.description = description;
    this.cxcNumber = cxcNumber;
    this.fajNumber = fajNumber;
    this.capacity = capacity;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.licensing.cache.LicenseInformation#getBirthDay()
   */
  public long getBirthDay() {
    return this.birthday;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.licensing.cache.LicenseInformation#getDeathDay()
   */
  public long getDeathDay() {
    return this.deathday;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.licensing.cache.LicenseInformation#getDescription()
   */
  public String getDescription() {
    return this.description;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.licensing.cache.LicenseInformation#getFajNumber()
   */
  public String getFajNumber() {
    return this.fajNumber;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.licensing.cache.LicenseInformation#getFeatureName()
   */
  public String getFeatureName() {
    return this.cxcNumber;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.licensing.cache.LicenseInformation#getServer()
   */
  public String getServer() {
    return this.server;
  }

  @Override
  public int getCapacity() {
    return this.capacity;
  }

}
