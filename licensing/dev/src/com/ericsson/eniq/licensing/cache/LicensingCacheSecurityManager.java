/***
 * A more or less dummy extension of the RMISecurityManager. This needed to be done to
 * allow Socket connections to the RMI registry. Would probably need to be tailored a bit
 * once it is clear which ports/servers will be used.
 */
package com.ericsson.eniq.licensing.cache;

import java.rmi.RMISecurityManager;
import java.security.Permission;

/**
 * @author ecarbjo
 */
public class LicensingCacheSecurityManager extends RMISecurityManager {

	@Override
	public void checkPermission(final Permission perm) {
		// this is empty to give all permissions
	}
}
