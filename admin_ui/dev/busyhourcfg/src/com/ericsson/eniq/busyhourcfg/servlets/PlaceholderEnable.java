/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.servlets;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ericsson.eniq.busyhourcfg.common.Constants;
import com.ericsson.eniq.busyhourcfg.common.Utils;
import com.ericsson.eniq.busyhourcfg.data.vo.BusyhourSupport;
import com.ericsson.eniq.busyhourcfg.data.vo.Placeholder;
import com.ericsson.eniq.busyhourcfg.data.vo.TargetTechPack;
import com.ericsson.eniq.busyhourcfg.data.vo.TechPack;
import com.ericsson.eniq.busyhourcfg.database.BusyhourUpdater;
import com.ericsson.eniq.busyhourcfg.database.BusyhourUpdaterFactory;
import com.ericsson.eniq.busyhourcfg.database.DatabaseSession;
import com.ericsson.eniq.busyhourcfg.dwhmanager.DWHStorageTimeAction;

/**
 * @author eheijun
 * 
 */
public class PlaceholderEnable extends CommonServlet {

  /**
   * 
   */
  private static final long serialVersionUID = -4017784989225690066L;

  private final Logger log = Logger.getLogger(PlaceholderEnable.class.getName());

  /**
   * @see HttpServlet#HttpServlet()
   */
  public PlaceholderEnable() {
    log.fine("Instantiated servlet " + this.getClass().getName());
  }

  /**
   * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */
  protected void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
      IOException {
    try {
      if (isRequestValid(request, response)) {
        String infomessage = "";
        String errormessage = "";
        final HttpSession session = request.getSession(false);
        final DWHStorageTimeAction dwhStorageTimeAction = (DWHStorageTimeAction) session.getAttribute("dwhstoragetimeaction");
        final DatabaseSession databaseSession = (DatabaseSession) session.getAttribute("databasesession");
        final TechPack selectedTechPack = (TechPack) request.getSession(false).getAttribute("sourcetp");
        if (selectedTechPack != null) {
          final String targettp = (String) request.getParameter("targettp");
          if (targettp != null) {
            final TargetTechPack currentTargetTechPack = selectedTechPack.getTargetTechPackByVersionId(targettp);
            final String bhlevel = (String) request.getParameter("bhsupport");
            if (bhlevel != null) {
              final BusyhourSupport currentBusyhourSupport = currentTargetTechPack.getBusyhourSupportByBhlevel(bhlevel);
              final String bhtype = (String) request.getParameter("bhtype");
              if (bhtype != null) {
                final Placeholder currentPlaceholder = currentBusyhourSupport.getPlaceholderByBhtype(bhtype);

                String redirectURL = View.PLACEHOLDERSVIEW + "?targettp="
                    + URLEncoder.encode(targettp, Constants.DEFAULT_ENCODING) + "&bhsupport="
                    + URLEncoder.encode(bhlevel, Constants.DEFAULT_ENCODING);

                final String enabled = (String) request.getParameter("enabled");
                if (enabled != null) {
                  final Boolean newvalue = Utils.stringToBoolean(enabled);
                  currentPlaceholder.setEnabled(newvalue);

                  final BusyhourUpdater busyhourUpdater = BusyhourUpdaterFactory.getBusyhourUpdater(databaseSession, dwhStorageTimeAction);
                  errormessage = busyhourUpdater.validatePlaceholder(currentPlaceholder);
                  if (errormessage.equals("")) {
                    try {
                      busyhourUpdater.savePlaceholder(currentPlaceholder);
                      infomessage = "Placeholder " + currentPlaceholder.getBhtype() + " successfully " + (newvalue ? "enabled" : "disabled") + ".";
                      log.info(infomessage);
                      redirectURL += (infomessage == null ? "" : "&infomessage=" + URLEncoder.encode(infomessage, Constants.DEFAULT_ENCODING));
                      response.sendRedirect(redirectURL);
                      return;
                    } catch (Exception e) {
                      errormessage = e.getMessage();
                    }
                  }
                }
              }
            }
          }
        }
        request.setAttribute("errormessage", errormessage);
        request.getRequestDispatcher(View.PLACEHOLDERS_VIEW_JSP).forward(request, response);
      }
    } catch (IllegalStateException e) {
      request.setAttribute("errormessage", Constants.MSG_EXPIRED);
      request.getRequestDispatcher(View.ERROR_PAGE).forward(request, response);
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      request.setAttribute("errormessage", e.getMessage());
      request.getRequestDispatcher(View.ERROR_PAGE).forward(request, response);
    }
  }

}
