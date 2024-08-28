/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.servlets;


import java.util.logging.Logger;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.ericsson.eniq.busyhourcfg.database.DatabaseSession;
import com.ericsson.eniq.busyhourcfg.exceptions.DatabaseException;

/**
 * @author eheijun
 * 
 */
public class BusyhourConfigurationSessionListener implements HttpSessionListener {

  private final Logger log = Logger.getLogger(BusyhourConfigurationSessionListener.class.getName());

  @Override
  public void sessionDestroyed(final HttpSessionEvent sessionEvent) {
    final HttpSession session = sessionEvent.getSession();
    final DatabaseSession databaseSession = (DatabaseSession) session.getAttribute("databasesession");
    
    if (databaseSession != null) {
      // clear database session
      try {
        databaseSession.clear();
        log.fine("session " + session.getId() + " databaseSession cleaned");
      } catch (DatabaseException e) {
        log.warning("session " + session.getId() + " databaseSession clean problem " + e.getMessage());
      }
    }
    log.fine("session " + session.getId() + " destroyed");
  }

  @Override
  public void sessionCreated(final HttpSessionEvent arg0) {
    // Nothing TO DO
  }

}
