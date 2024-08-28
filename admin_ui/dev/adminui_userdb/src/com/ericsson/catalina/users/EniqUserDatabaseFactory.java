/**
 * 
 */
package com.ericsson.catalina.users;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

import org.apache.catalina.UserDatabase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;


/**
 * @author eheijun
 * 
 */
public class EniqUserDatabaseFactory implements ObjectFactory { // NOPMD by eheijun on 01/07/11 10:28
  
  private static final Log log = LogFactory.getLog(EniqUserDatabaseFactory.class);

  /*
   * (non-Javadoc)
   * 
   * @see javax.naming.spi.ObjectFactory#getObjectInstance(java.lang.Object, javax.naming.Name, javax.naming.Context,
   * java.util.Hashtable)
   */
  @Override
  public Object getObjectInstance(final Object obj, final Name name, final Context nameCtx,
      final Hashtable<?, ?> environment) throws EniqUserDatabaseException {

    if ((obj == null) || !(obj instanceof Reference)) {
      return null;
    }

    final Reference ref = (Reference) obj;
    if (!"org.apache.catalina.UserDatabase".equals(ref.getClassName())) {
      return null;
    }

    String databaseName = null;
    String pathName = null;
    boolean readOnly = false;
    
    if (name != null) {
      databaseName = name.toString();
    }
    
    RefAddr ra = null;

    ra = ref.get("pathname");
    if (ra != null) {
      pathName = ra.getContent().toString();
    }
    
    ra = ref.get("readonly");
    if (ra != null) {
      readOnly = Boolean.valueOf(ra.getContent().toString());
    }

    try {

      final UserDatabase subjectUserDatabase = EniqUserDatabaseSubjectFactory.getInstance(pathName, databaseName, readOnly);
      final EniqUserDatabase database = new EniqUserDatabase(subjectUserDatabase);
    
      database.open();
      if (!readOnly) {
        database.save();
      }
      log.info("ENIQ UserDatabase successfully created.");
      return database;
    } catch (Exception e) {
      log.error("Error while creating ENIQ UserDatabase.", e);
      throw new EniqUserDatabaseException(e);
    }

  }

}
