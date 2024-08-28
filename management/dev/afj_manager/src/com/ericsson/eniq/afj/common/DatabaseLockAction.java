/**
 * 
 */
package com.ericsson.eniq.afj.common;

/**
 * @author eheijun
 *
 */
public interface DatabaseLockAction {

  /**
   * Perform database lock
   * @return true if operation was successfully
   */
  Boolean performLock();

  /**
   * Perform database unlock
   * @return true if operation was successfully         
   */
  Boolean performUnlock();
  
}
