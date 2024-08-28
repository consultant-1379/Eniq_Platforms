/**
 * 
 */
package org.apache.catalina.mbeans;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.notNullValue;

import org.junit.Test;


/**
 * @author eheijun
 *
 */
public class EniqGlobalResourcesLifecycleListenerTest {

  @Test
  public void test() {
    EniqGlobalResourcesLifecycleListener listener = new EniqGlobalResourcesLifecycleListener();
    assertThat(listener, notNullValue());
  }

}
