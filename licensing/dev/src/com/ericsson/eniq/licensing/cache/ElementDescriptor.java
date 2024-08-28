/**
 ** This is the super interface for the descriptor interfaces used for RMI communications.
 */
package com.ericsson.eniq.licensing.cache;

import java.io.Serializable;

/**
 * @author ecarbjo
 */

public interface ElementDescriptor extends Serializable {
	String getName();
}
