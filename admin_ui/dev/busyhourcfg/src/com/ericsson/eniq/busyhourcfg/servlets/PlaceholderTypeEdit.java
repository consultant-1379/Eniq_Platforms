/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.servlets;

import java.io.IOException;
import java.util.Enumeration;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ericsson.eniq.busyhourcfg.common.Constants;
import com.ericsson.eniq.busyhourcfg.common.Utils;
import com.ericsson.eniq.busyhourcfg.data.vo.Placeholder;


/**
 * @author eheijun
 * 
 */
public class PlaceholderTypeEdit extends CommonServlet {

  /**
   * 
   */
  private static final long serialVersionUID = 3843992804063227468L;
  
  private final Logger log = Logger.getLogger(PlaceholderTypeEdit.class.getName());

  /**
   * @see HttpServlet#HttpServlet()
   */
  public PlaceholderTypeEdit() {
    log.fine("Instantiated servlet " + this.getClass().getName());
  }

  /**
   * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */
  protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
      IOException {
    try {
      if (isRequestValid(request, response)) {
        final Enumeration<?> names = request.getParameterNames();
        while (names.hasMoreElements()) {
          final String name = (String) names.nextElement();
          final String value = request.getParameter(name);
          log.fine("type edited, request parameter" + name + "=" + value);
        }
        final HttpSession session = request.getSession(false);
        final Placeholder currentPlaceholder = (Placeholder) session.getAttribute("currentPlaceholder");

        final String cancel = (String) request.getParameter("cancel");
        if ((cancel != null)) {
          log.fine("cancel button pressed");
          currentPlaceholder.undoBusyHourTypeChanges();
          response.sendRedirect(View.PLACEHOLDERVIEW);
          return;
        }

        boolean valid = true;
        String errormessage = "";

        final String type = Utils.replaceNull((String) request.getParameter("type"));
        currentPlaceholder.setAggregationType(type);

        Integer lookback = null;
        Integer pthreshold = null;
        Integer nthreshold = null;
        
        try {
          lookback = Utils.stringToIntegerDef(Utils.replaceNull((String) request.getParameter("lookback")), null);
          currentPlaceholder.setLookback(lookback);
        } catch (NumberFormatException e) {
          valid = false;
          errormessage = "Invalid parameter value(s)";
        }
        try {
          pthreshold = Utils.stringToIntegerDef(Utils.replaceNull((String) request.getParameter("pthreshold")), null);
          currentPlaceholder.setPThreshold(pthreshold);
        } catch (NumberFormatException e) {
          valid = false;
          errormessage = "Invalid parameter value(s)";
        }
        try {
          nthreshold = Utils.stringToIntegerDef(Utils.replaceNull((String) request.getParameter("nthreshold")), null);
          currentPlaceholder.setNThreshold(nthreshold);
        } catch (NumberFormatException e) {
          valid = false;
          errormessage = "Invalid parameter value(s)";
        }
        
        if (currentPlaceholder.isTimelimited()) {
          if ((lookback != null) || (pthreshold != null) || ((nthreshold != null)) ) {
            valid = false;
            errormessage = "Invalid parameter(s)";
          }
        } else if (currentPlaceholder.isSlidingwindow() || currentPlaceholder.isPeakrop()) {
          if ((lookback != null) || (pthreshold != null) || ((nthreshold != null)) ) {
            valid = false;
            errormessage = "Invalid parameter(s)";
          }
        } else if (currentPlaceholder.isTimelimitedTimeconsistent()) {
          if ((lookback == null) || (pthreshold == null) || ((nthreshold == null)) ) {
            valid = false;
            errormessage = "Invalid parameter(s)";
          }
        } else if (currentPlaceholder.isSlidingwindowTimeconsistent()) {
          if ((lookback == null) || (pthreshold == null) || ((nthreshold == null)) ) {
            valid = false;
            errormessage = "Invalid parameter(s)";
          }
        } else {
          valid = false;
          errormessage = "Invalid type";
        }

        if (valid) {
          final String save = (String) request.getParameter("save");
          if ((save != null)) {
            log.fine("save button pressed");
            request.getRequestDispatcher(View.PLACEHOLDERVIEW).forward(request, response);
            return;
          }
        } else {
          request.setAttribute("errormessage", errormessage);
        }
        
        request.getRequestDispatcher(View.PLACEHOLDERTYPE_VIEW_JSP).forward(request, response);
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
