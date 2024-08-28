package com.ericsson.eniq.common;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.velocity.app.VelocityEngine;

public final class VelocityPool {

  private static List pool = new ArrayList(10);

  private static int count = 0;

  private VelocityPool() {

  }

  /**
   * Returns instance of Velocity engine. Instance should be released after not
   * used any more.
   * 
   * @return VelocityEngine instance
   * @throws Exception
   *           if VelocityEngine can't be returned
   */
  public static VelocityEngine reserveEngine() throws Exception {

    synchronized (pool) {
      if (pool.size() > 0) {
        final VelocityEngine ve = (VelocityEngine) pool.remove(0);
        return ve;
      }
    }
    
    final VelocityEngine ve = new VelocityEngine();
    ve.init();
    count++;
    Logger.getLogger("etlengine.VelocityPool").info("Created new VelocityInstance # " + count);
    return ve;
    
  }

  public static void releaseEngine(final VelocityEngine ve) {
    if (ve != null) {
      synchronized (pool) {
        pool.add(ve);
      }
    }
  }

}
