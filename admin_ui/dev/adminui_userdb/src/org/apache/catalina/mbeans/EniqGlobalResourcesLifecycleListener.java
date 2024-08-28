package org.apache.catalina.mbeans;


import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

/*
 * This class loads EniqUserDatabaseMbean descriptor in to the Tomcat MBean registry 
 */
public class EniqGlobalResourcesLifecycleListener extends GlobalResourcesLifecycleListener {  
  
    private static final Log log = LogFactory.getLog(EniqGlobalResourcesLifecycleListener.class);

    {
      final ClassLoader cl = EniqGlobalResourcesLifecycleListener.class.getClassLoader();
      if (registry == null) {
        registry = MBeanUtils.createRegistry();
      }
      // Loads mbeans-descriptors.xml from com.ericsson.catalina.users package
      registry.loadDescriptors("com.ericsson.catalina.users", cl);
      log.info("EniqUserDatabaseMbean descriptor loaded");
    }

}