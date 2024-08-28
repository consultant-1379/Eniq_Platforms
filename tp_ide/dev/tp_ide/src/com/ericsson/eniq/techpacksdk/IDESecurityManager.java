package com.ericsson.eniq.techpacksdk;

import java.rmi.RMISecurityManager;
import java.security.Permission;

public class IDESecurityManager extends RMISecurityManager {

  /**
   * Grant all permissions
   */
  public void checkPermission(final Permission perm) {

  }

}
