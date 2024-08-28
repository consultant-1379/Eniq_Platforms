/**
 * 
 */
package com.distocraft.dc5000.etl.gui;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.gui.common.EnvironmentEvents;
import com.distocraft.dc5000.etl.gui.common.EnvironmentMixed;
import com.distocraft.dc5000.etl.gui.common.EnvironmentNone;
import com.distocraft.dc5000.etl.gui.common.EnvironmentStats;
import com.distocraft.dc5000.repository.dwhrep.Tpactivation;
import com.distocraft.dc5000.repository.dwhrep.TpactivationFactory;

/**
 * @author eheijun
 * 
 */
public class EniqAdminuiSessionListener implements HttpSessionListener {

  private static final String ENVIRONMENT = "environment";

  private static Log log = LogFactory.getLog(EniqAdminuiSessionListener.class);

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.http.HttpSessionListener#sessionCreated(javax.servlet.http.HttpSessionEvent)
   */
  @Override
  public void sessionCreated(final HttpSessionEvent sessionEvent) {

    final HttpSession session = sessionEvent.getSession();
    session.setAttribute(ENVIRONMENT, new EnvironmentNone());

    try {
      if (DbConnectionFactory.getInstance().initialiseConnections(session) != null) {
    	  try{
    		  final RockFactory dwhrep = (RockFactory) session.getAttribute(RockFactoryType.ROCK_DWH_REP.getName());
    		  if (dwhrep != null) {
    			  final Tpactivation whereRef = new Tpactivation(dwhrep);
    	          whereRef.setStatus("ACTIVE");
    	          whereRef.setType("ENIQ_EVENT");
    	          final TpactivationFactory tpaEvents = new TpactivationFactory(dwhrep, whereRef);
    	          final Boolean isEvents = tpaEvents.get().size() > 0;
    	          whereRef.setType("PM");
    	          final TpactivationFactory tpaStats = new TpactivationFactory(dwhrep, whereRef);
    	          final Boolean isStats = tpaStats.get().size() > 0;
    	          if (isEvents && isStats) {
    	            session.setAttribute(ENVIRONMENT, new EnvironmentMixed());
    	            log.info("System environment is mix of ENIQ Events and Stats");
    	          } else if (isEvents) {
    	            session.setAttribute(ENVIRONMENT, new EnvironmentEvents());
    	            log.info("System environment is ENIQ Events");
    	          } else if (isStats) {
    	            session.setAttribute(ENVIRONMENT, new EnvironmentStats());
    	            log.info("System environment is ENIQ Stats");
    	          } else {
    	            log.info("System environment is Unknown.");
    	          }
    		  }else{
    	          log.error("No database information in the session");
    		  }
    	  }finally{
    		  DbConnectionFactory.getInstance().finalizeConnections(session);
    	  }
      } else {
        log.error("Database initialisation for the session failed.");
      }
    } catch (SQLException e) {
      log.error("SQL Error when creating session.", e);
    } catch (RockException e) {
      log.error("Rock Error when creating session.", e);
    } catch (IOException e) {
      log.error("IO Error when creating session.", e);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.http.HttpSessionListener#sessionDestroyed(javax.servlet.http.HttpSessionEvent)
   */
  @Override
  public void sessionDestroyed(final HttpSessionEvent sessionEvent) {
    try {
      DbConnectionFactory.getInstance().finalizeConnections(sessionEvent.getSession());
    } catch (SQLException e) {
      log.error("SQL Error when removing session.", e);
    }
  }

}
