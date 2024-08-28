/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.servlets;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ericsson.eniq.busyhourcfg.config.BusyhourConfigurationFactory;
import com.ericsson.eniq.busyhourcfg.config.BusyhourEnvironment;
import com.ericsson.eniq.busyhourcfg.config.BusyhourEnvironmentFactory;
import com.ericsson.eniq.busyhourcfg.config.BusyhourProperties;
import com.ericsson.eniq.busyhourcfg.data.vo.TechPack;
import com.ericsson.eniq.busyhourcfg.database.ActivationAction;
import com.ericsson.eniq.busyhourcfg.database.UpdateResult;
import com.ericsson.eniq.busyhourcfg.exceptions.BusyhourConfigurationException;
import com.ericsson.eniq.repository.ETLCServerProperties;

/**
 * @author eheijun
 * 
 */
public class ActivationStartServlet extends CommonServlet {

  /**
   * 
   */
  private static final long serialVersionUID = -276745231645949117L;

  private final Logger log = Logger.getLogger(ActivationStartServlet.class.getName());

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest,
   * javax.servlet.http.HttpServletResponse)
   */
  protected void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
      IOException {
    final HttpSession session = request.getSession(false);
    try {

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
      
      if (etlcServerProperties == null || busyhourProperties == null) {
        throw new Exception("Invalid configuration: check your installation.");
      }

      final TechPack currentTechPack = (TechPack) session.getAttribute("sourcetp");

      if (currentTechPack == null) {
        throw new Exception("Invalid session: please login again.");
      } else {
        final ActivationAction action = new ActivationAction(etlcServerProperties, busyhourProperties,
            currentTechPack);

        final ExecutorService executorService = (ExecutorService) session.getServletContext().getAttribute("ExecutorService");

        if (executorService == null) {
          throw new Exception("Invalid thread pool: webapp needs to be restarted.");
        } else {
          try {
            final Future<UpdateResult> future = executorService.submit(action);
            log.info("Busy Hour Update process started.");
            session.setAttribute("actionFuture", future);
            response.sendRedirect(View.ACTIVATIONSTATUSVIEW);
            return;
          } catch (Exception e) {
            throw e;
          }
        }
      }
    } catch (Exception e) {
      log.severe(e.getMessage());
      request.setAttribute("errormessage", e.getMessage());
      request.getRequestDispatcher(View.ERROR_PAGE).forward(request, response);
      return;
    }
  }
}
