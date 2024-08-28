/*
 * ---------------------------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * ---------------------------------------------------------------------------------------
 */

package com.ericsson.eniq.licensing.cache.stubs;

import java.util.Vector;
import rainbow.lmclient.Challenge;
import rainbow.lmclient.CustomMachineIDFunc;
import rainbow.lmclient.QueuePreference;
import rainbow.lmclient.SharedIdFunc;
import rainbow.lmclient.WLSApi;
import rainbow.lmclient.WLSFeatureInfo;

public class WLSApi_Stub implements WLSApi {
  private long getCallDelay = 0;

  private static final Vector<WLSFeatureInfo> dummyLicenses = new Vector<WLSFeatureInfo>();
  static {
    dummyLicenses.add(new WLSFeatureInfo_Stub("TEST_FEATURE_NAME"));
    dummyLicenses.add(new WLSFeatureInfo_Stub("TFN-3LIC", 3));
    dummyLicenses.add(new WLSFeatureInfo_Stub("TFN-10LIC", 10));
    dummyLicenses.add(new WLSFeatureInfo_Stub("CXCSOMETHING"));
    dummyLicenses.add(new WLSFeatureInfo_Stub("CXC-NoExpiry", false));
    dummyLicenses.add(new WLSFeatureInfo_Stub("CXC-Expired", -360000L));
    dummyLicenses.add(new WLSFeatureInfo_Stub("CXC-Expires", 460000L));
    dummyLicenses.add(new WLSFeatureInfo_Stub("CXC123456")); // feature mapped
  }

  @Override @SuppressWarnings("unchecked")
  public int VLSgetFeatureInfo(String s, String s1, Vector vector, String s2) {
    delayGetCall();
    //noinspection unchecked
    vector.addAll(dummyLicenses);
    return 0;
  }
  public static int getDummyLicenseCount(){
    return dummyLicenses.size();
  }

  public void setGetCallDelay(final long callDelay){
    this.getCallDelay = callDelay;
  }

  private void delayGetCall(){
    try {
      Thread.sleep(getCallDelay);
    } catch (InterruptedException e) {/**/}
  }

  @Override
  public int LSRelease(long l, String s) {
    return 0;
  }

  @Override
  public int LSRequest(String s, String s1, String s2, String s3, long l, String s4, Challenge challenge) {
    return 0;
  }

  @Override
  public int LSUpdate(long l, long l1, String s, Challenge challenge) {
    return 0;
  }

  @Override
  public int VLSDiscover(String s, String s1, String s2, Vector vector, int i, int i1, String s3) {
    return 0;
  }

  @Override
  public int VLSRelease() {
    return 0;
  }

  @Override
  public int VLSRequest(String s, String s1) {
    return 0;
  }

  @Override
  public int VLSUpdate() {
    return 0;
  }

  @Override
  public int VLSaddFeature(String s, String s1, Challenge challenge) {
    return 0;
  }

  @Override
  public int VLSaddFeatureToFile(String s, String s1, Challenge challenge) {
    return 0;
  }

  @Override
  public int VLSaddServerToPool(String s, String s1) {
    return 0;
  }

  @Override
  public int VLSdelServerFromPool(String s, String s1) {
    return 0;
  }

  @Override
  public int VLSdeleteFeature(String s, String s1, String s2, Challenge challenge) {
    return 0;
  }

  @Override
  public int VLSdisableAutoTimer(int i) {
    return 0;
  }

  @Override
  public int VLSdisableLocalRenewal(boolean b) {
    return 0;
  }

  @Override
  public int VLSgetClientInfo(String s, String s1, Vector vector, String s2) {
    return 0;
  }

  @Override
  public int VLSgetDistbCrit(String s, String s1, Vector vector) {
    return 0;
  }

  @Override
  public String VLSgetFeatureFromInterface() {
    return null;
  }

  @Override
  public String VLSgetHostId() {
    return null;
  }

  @Override
  public String VLSgetHostName() {
    return null;
  }

  @Override
  public int VLSgetLeaderServer(Vector vector) {
    return 0;
  }

  @Override
  public String VLSgetLibInfo() {
    return null;
  }

  @Override
  public int VLSgetLicSharingServerList(String s, String s1, Vector vector) {
    return 0;
  }

  @Override
  public int VLSgetLicenseInfo(String s, String s1, int i, String s2, int i1, int i2, Vector vector) {
    return 0;
  }

  @Override
  public String VLSgetMachineIdString() {
    return null;
  }

  @Override
  public int VLSgetMaxRetries() {
    return 0;
  }

  @Override
  public int VLSgetPoolServerList(Vector vector) {
    return 0;
  }

  @Override
  public int VLSgetQueuedLicense() {
    return 0;
  }

  @Override
  public int VLSgetServerInfo(Vector vector, Challenge challenge, String s) {
    return 0;
  }

  @Override
  public String VLSgetServerName() {
    return null;
  }

  @Override
  public int VLSgetServerPort() {
    return 0;
  }

  @Override
  public int VLSgetTimeoutInterval() {
    return 0;
  }

  @Override
  public int VLSgetTrialPeriodLeft(String s, String s1, int[] ints) {
    return 0;
  }

  @Override
  public int VLSgetTrialUsageInfo(String s, String s1, int i, Vector vector) {
    return 0;
  }

  @Override
  public String VLSgetVersionFromInterface() {
    return null;
  }

  @Override
  public int VLSgetVersions(String s, Vector vector, String s1) {
    return 0;
  }

  @Override
  public boolean VLSisLocalRenewalDisabled() {
    return false;
  }

  @Override
  public int VLSlicense(String s, String s1) {
    return 0;
  }

  @Override
  public int VLSqueuedRequest(String s, String s1, int i, QueuePreference queuePreference, int i1, Challenge challenge) {
    return 0;
  }

  @Override
  public int VLSremoveQueue(String s, String s1, String s2) {
    return 0;
  }

  @Override
  public int VLSremoveQueuedClient(String s, String s1, String s2) {
    return 0;
  }

  @Override
  public int VLSsetHoldTime(String s, String s1, int i) {
    return 0;
  }

  @Override
  public int VLSsetHostId(String s) {
    return 0;
  }

  @Override
  public int VLSsetHostIdFunc(CustomMachineIDFunc customMachineIDFunc) {
    return 0;
  }

  @Override
  public int VLSsetHostName(String s) {
    return 0;
  }

  @Override
  public int VLSsetMaxRetries(int i) {
    return 0;
  }

  @Override
  public int VLSsetRemoteRenewalTime(int i) {
    return 0;
  }

  @Override
  public int VLSsetServerLogState(int i, boolean b) {
    return 0;
  }

  @Override
  public int VLSsetServerName(String s) {
    return 0;
  }

  @Override
  public int VLSsetServerPort(int i) {
    return 0;
  }

  @Override
  public void VLSsetSharedID(int i, SharedIdFunc sharedIdFunc) {
  }

  @Override
  public int VLSsetTimeoutInterval(int i) {
    return 0;
  }

  @Override
  public int VLSupdateQueuedClient(int[] ints, String s, Challenge challenge) {
    return 0;
  }
}
