package com.ericsson.eniq.busyhourcfg.servlets;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ericsson.eniq.busyhourcfg.common.Constants;
import com.ericsson.eniq.busyhourcfg.config.BusyhourConfigurationFactory;
import com.ericsson.eniq.busyhourcfg.config.BusyhourEnvironment;
import com.ericsson.eniq.busyhourcfg.config.BusyhourEnvironmentFactory;
import com.ericsson.eniq.busyhourcfg.config.BusyhourProperties;
import com.ericsson.eniq.busyhourcfg.database.DatabaseConnector;
import com.ericsson.eniq.busyhourcfg.database.DatabaseConnectorFactory;
import com.ericsson.eniq.busyhourcfg.database.DatabaseSession;
import com.ericsson.eniq.busyhourcfg.dwhmanager.DWHStorageTimeAction;
import com.ericsson.eniq.busyhourcfg.dwhmanager.DWHStorageTimeActionFactory;
import com.ericsson.eniq.busyhourcfg.exceptions.BusyhourConfigurationException;
import com.ericsson.eniq.busyhourcfg.exceptions.DWHManagerException;
import com.ericsson.eniq.busyhourcfg.exceptions.DatabaseException;
import com.ericsson.eniq.busyhourcfg.licensing.LicenseChecker;
import com.ericsson.eniq.busyhourcfg.licensing.LicenseCheckerFactory;
import com.ericsson.eniq.repository.ETLCServerProperties;

/**
 * Servlet implementation class TechpackView
 * 
 * @author eheijun
 * @copyright Ericsson (c) 2009
 */
public class TechpackView extends CommonServlet {

  private static final String CXC_NUMBER = "CXC4010932";

  /**
   * 
   */
  private static final long serialVersionUID = -7931799236072754914L;

  private final Logger log = Logger.getLogger(TechpackView.class.getName());

  /**
   * @see HttpServlet#HttpServlet()
   */
  public TechpackView() {
    log.fine("Instantiated servlet " + this.getClass().getName());
  }

  /**
   * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
   *      response)
   */
  protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
      IOException {
    log.fine("Get the TechpackView servlet");
    if (!isRequestValid(request, response, false)) {
      final HttpSession session = request.getSession(false);
      if (session == null) {
        request.getRequestDispatcher(View.ERROR_PAGE).forward(request, response);
      } else {
        if (request.getHeader("User-Agent") != null){
          final String userAgent = (String) request.getHeader("User-Agent");
          log.info(userAgent);
        }
        
        if (session.getAttribute("databasesession") == null) {
          
          ETLCServerProperties etlcServerProperties = null;
          BusyhourProperties busyhourProperties = null;
          final BusyhourEnvironment busyhourEnvironment = BusyhourEnvironmentFactory.getBusyhourEnvironment();
          try {
            etlcServerProperties = BusyhourConfigurationFactory.getBusyhourConfiguration().getETLCServerProperties(
                session.getServletContext(), busyhourEnvironment);
            busyhourProperties = BusyhourConfigurationFactory.getBusyhourConfiguration().getBusyhourProperties(
                session.getServletContext(), busyhourEnvironment);
            log.fine("Configurations read successfully");
          } catch (BusyhourConfigurationException e) {
            log.severe(e.getMessage());
          }
          
          final LicenseChecker lc = LicenseCheckerFactory.getLicenseChecker(etlcServerProperties);
          if (lc.isLicenseValid(CXC_NUMBER)) {
            DatabaseSession databaseSession = null;
            DWHStorageTimeAction dwhStorageTimeAction = null;
            try {
              if (busyhourProperties != null) {
                final DatabaseConnector databaseConnector = DatabaseConnectorFactory.getDatabaseConnector(etlcServerProperties, busyhourProperties);
                try{
	                databaseSession = databaseConnector.createDatabaseSession(DatabaseConnectorFactory.getApplicationName());
	                log.fine("Database session created successfully");
	                try {
	                  dwhStorageTimeAction = DWHStorageTimeActionFactory.getDWHStorageTimeAction(databaseSession);
	                } catch (DWHManagerException e) {
	                  log.severe(e.getMessage());
	                }
                }finally{
                	DatabaseConnectorFactory.setDatabaseConnector(null);
                }
              }
            } catch (DatabaseException e) {
              log.severe(e.getMessage());
            }
            session.setAttribute("databasesession", databaseSession);
            log.fine("session " + session.getId() + " databasesession saved");
            session.setAttribute("dwhstoragetimeaction", dwhStorageTimeAction);
            log.fine("session " + session.getId() + " dwhstoragetimeaction saved");
          } else {
            // do not allow the user to log in since the license is invalid!
            log.warning("License validation failed!");
            // show a message on the error page for the user.
            request.setAttribute("errormessage",
                "Could not find a valid license for the busyhour configuration module! Please contact the system administrator.");
            // now forward this request to the view.
            request.getRequestDispatcher(View.ERROR_PAGE).forward(request, response);
            // invalidate session
            //session.invalidate();
            return;
          }
        } else {
          log.fine("session " + session.getId() + " is valid");
        }
      }
    }
    request.getRequestDispatcher(View.TECHPACK_VIEW_JSP).forward(request, response);
  }

  /**
   * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
   *      response)
   */
  protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
      IOException {
    log.fine("Post the TechpackView servlet");
    try {
      if (isRequestValid(request, response, false)) {
        final HttpSession session = request.getSession(false);
        final String versionid = (String) request.getParameter("select_techpacks");
        if (versionid == null) {
          log.fine("Nothing was selected");
          request.setAttribute("errormessage", "TechPack must be selected");
          request.getRequestDispatcher(View.TECHPACK_VIEW_JSP).forward(request, response);
        } else {
          log.fine(versionid + " was selected");
          session.setAttribute("versionid", versionid);
          response.sendRedirect(View.BUSYHOURVIEW);
        }
      } else {
        request.setAttribute("errormessage", Constants.MSG_EXPIRED);
        request.getRequestDispatcher(View.ERROR_PAGE).forward(request, response);
      }
    } catch (Exception e) {
      request.setAttribute("errormessage", e.getMessage());
      request.getRequestDispatcher(View.ERROR_PAGE).forward(request, response);
    }
  }

}
