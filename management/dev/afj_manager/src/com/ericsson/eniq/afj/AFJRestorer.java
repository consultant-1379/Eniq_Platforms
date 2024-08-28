/**
 * 
 */
package com.ericsson.eniq.afj;

import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.log.NullLogSystem;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import com.ericsson.eniq.afj.common.AFJTechPack;
import com.ericsson.eniq.exception.AFJException;


/**
 * @author eheijun
 *
 */
public class AFJRestorer {
  
  private AFJRestorer() {
  }

  /**
   * @param args
   */
  public static void main(final String[] args) {
    if (args.length == 0) {
      System.err.println("Argument missing.");
      return;
    }
    if (args.length > 1) {
      System.err.println("Too many arguments.");
      return;
    }

    final String techPackName = args[0];
    
    try {
      System.out.println("Restoring " + techPackName + " will take few minutes. Please wait.");
      
      Velocity.setProperty(Velocity.RESOURCE_LOADER, "class, file");
      Velocity.setProperty("class.resource.loader.class", ClasspathResourceLoader.class.getName());
      Velocity.setProperty(Velocity.RUNTIME_LOG_LOGSYSTEM_CLASS, NullLogSystem.class.getName());
      Velocity.init();
      
      
      final AFJManager afjManager = AfjManagerFactory.getInstance();
      
      final AFJTechPack afjTechPack = afjManager.getAFJTechPack(techPackName);
      
      if (afjTechPack == null) {
        throw new AFJException(techPackName + " is not AFJTechPack.");
      }
      
      final Boolean result = afjManager.restoreAFJTechPack(afjTechPack);
      
      if (result) {
        System.out.println("Restoring " + techPackName + " finished.");
      } else {
        System.out.println("Restoring " + techPackName + " failed.");
      }
      
    } catch (Exception e) {
      System.err.println("Restoring failed: " + e.getMessage());
    }
  }

}
