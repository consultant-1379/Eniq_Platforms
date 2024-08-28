/*
 * ---------------------------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * ---------------------------------------------------------------------------------------
 */

package com.ericsson.eniq.licensing.cache.stubs;

import java.io.PrintStream;
import rainbow.lmclient.WLSFeatureInfo;

public class WLSFeatureInfo_Stub implements WLSFeatureInfo {
  private final String featureName;
  private final int numLicenses;
  private boolean expires = true;
  private long pastExpiration = 3600000;

  public WLSFeatureInfo_Stub(final String fName){
    this(fName, 1);
  }
  public WLSFeatureInfo_Stub(final String fName, final boolean expires){
    this(fName);
    this.expires = expires;
  }
  public WLSFeatureInfo_Stub(final String fName,  final long pastExpiration){
    this(fName);
    this.pastExpiration = pastExpiration;
  }
  public WLSFeatureInfo_Stub(final String fName, final int licCap){
    featureName = fName;
    numLicenses = licCap;
  }
  @Override
  public long getAvgQueueTime() {
    return 0;
  }

  @Override
  public long getBirthDay() {
    return System.currentTimeMillis();
  }

  @Override
  public long getCapacity() {	getCapacity();  
    return 0;
  }

  @Override
  public boolean getCheckTimeTamper() {
    return false;
  }

  @Override
  public String getClientLockInfo() {
    return null;
  }

  @Override
  public long getCommuterdays() {
    return 0;
  }

  @Override
  public int getConcurrency() {
    return 0;
  }

  @Override
  public long getConversionTime() {
    return 0;
  }

  @Override
  public long getDeathDay() {
    if(expires){
      // sentinal doers stuff in epoch _seconds_ instead of milliseconds
      return (System.currentTimeMillis() + pastExpiration) / 1000;
    } else {
      return -1;
    }
  }

  @Override
  public int getElanKeyFlag() {
    return 0;
  }

  @Override
  public String getFeatureName() {
    return this.featureName;
  }

  @Override
  public long getGraceCalanderDays() {
    return 0;
  }

  @Override
  public long getGraceCalanderHours() {
    return 0;
  }

  @Override
  public long getGraceFlag() {
    return 0;
  }

  @Override
  public long getHoldTime() {
    return 0;
  }

  @Override
  public int getHoldingCriterion() {
    return 0;
  }

  @Override
  public boolean getIsAdditive() {
    return false;
  }

  @Override
  public int getIsCapacity() {
    return 0;
  }

  @Override
  public int getIsCommuter() {
    return 0;
  }

  @Override
  public long getIsGrace() {
    return 0;
  }

  @Override
  public int getIsLocalRequestLockcrit() {
    return 0;
  }

  @Override
  public int getIsMajorityRule() {
    return 0;
  }

  @Override
  public long getIsOverDraft() {
    return 0;
  }

  @Override
  public int getIsRedundant() {
    return 0;
  }

  @Override
  public int getIsStandalone() {
    return 0;
  }

  @Override
  public long getKeyLifeTime() {
    return 0;
  }

  @Override
  public int getLicType() {
    return 0;
  }

  @Override
  public int getLicenseVersion() {
    return 0;
  }

  @Override
  public int getLicensesFromFree() {
    return 0;
  }

  @Override
  public int getLicensesFromReserved() {
    return 0;
  }

  @Override
  public int getLocalRequestLockcritFloat() {
    return 0;
  }

  @Override
  public int getLocalRequestLockcritMinNum() {
    return 0;
  }

  @Override
  public int getLocalRequestLockcritRequired() {
    return 0;
  }

  @Override
  public int getLockingCriterion() {
    return 0;
  }

  @Override
  public int getLogEncryptionLevel() {
    return 0;
  }

  @Override
  public int getMeterValue() {
    return 0;
  }

  @Override
  public int getNodelockCriterion() {
    return 0;
  }

  @Override
  public int getNumLicenses() {
    return numLicenses;
  }

  @Override
  public int getNumServers() {
    return 1;
  }

  @Override
  public int getNumSubnets() {
    return 1;
  }

  @Override
  public long getOverDraftHours() {
    return 0;
  }

  @Override
  public long getOverDraftUsers() {
    return 0;
  }

  @Override
  public int getQLicensesFromFree() {
    return 0;
  }

  @Override
  public int getQLicensesFromReserved() {
    return 0;
  }

  @Override
  public long getQueueLength() {
    return 0;
  }

  @Override
  public String getServerLockInfo() {
    return null;
  }

  @Override
  public int getSharingCriterion() {
    return 0;
  }

  @Override
  public int getSharingLimit() {
    return 0;
  }

  @Override
  public String getSiteLicenseInfo() {
    return null;
  }

  @Override
  public int getSoftLimit() {
    return 0;
  }

  @Override
  public int getTotalLicReqd() {
    return 0;
  }

  @Override
  public int getTotalReserved() {
    return 0;
  }

  @Override
  public int getTrialCalendarPeriodLeft() {
    return 0;
  }

  @Override
  public int getTrialCurrentStatus() {
    return 0;
  }

  @Override
  public int getTrialDaysCount() {
    return 0;
  }

  @Override
  public int getTrialElapsedHours() {
    return 0;
  }

  @Override
  public int getTrialElapsedPeriodLeft() {
    return 0;
  }

  @Override
  public int getTrialExecutionCount() {
    return 0;
  }

  @Override
  public int getTrialExecutionsLeft() {
    return 0;
  }

  @Override
  public String getVendorInfo() {
    return null;
  }

  @Override
  public String getVersion() {
    return null;
  }

  @Override
  public String getplainVendorInfo() {
    return null;
  }

  @Override
  public void printFeature() {
  }

  @Override
  public void printFeature(PrintStream printStream) {
  }
}
