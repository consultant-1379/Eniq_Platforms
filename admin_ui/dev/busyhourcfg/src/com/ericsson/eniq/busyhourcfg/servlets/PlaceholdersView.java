/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.servlets;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ericsson.eniq.busyhourcfg.common.Constants;
import com.ericsson.eniq.busyhourcfg.data.vo.BusyhourSupport;
import com.ericsson.eniq.busyhourcfg.data.vo.TargetTechPack;

/**
 * @author eheijun
 *
 */
public class PlaceholdersView extends CommonServlet {

  /**
   * 
   */
  private static final long serialVersionUID = -1732056550180291377L;

  private final Logger log = Logger.getLogger(PlaceholdersView.class.getName());

  /**
   * @see HttpServlet#HttpServlet()
   */
  public PlaceholdersView() {
    log.fine("Instantiated servlet " + this.getClass().getName());
  }
  
  /**
   * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  protected void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
    IOException {
    try {
      if (isRequestValid(request, response)) {
        
        final HttpSession session = request.getSession(false);
        
        final String cancel = (String) request.getParameter("cancel");
        if ((cancel != null)) {
          log.fine("cancel button pressed");
          response.sendRedirect(View.BUSYHOURVIEW);
          return;
        }
        
        final String infomessage = (String) request.getParameter("infomessage");
        final String errormessage = (String) request.getParameter("errormessage");
        
//        TechPack currentTechPack = (TechPack) session.getAttribute("sourcetp");
        final TargetTechPack currentTargetTechPack = (TargetTechPack) session.getAttribute("targettp");
        BusyhourSupport currentBusyhourSupport = (BusyhourSupport) session.getAttribute("bhsupport");

        final String bhlevel = (String) request.getParameter("bhsupport");
        if (bhlevel != null) {
          session.removeAttribute("bhsupport");
          session.removeAttribute("placeholder");
        }
        
        String selectedBHLevel = "";
        if (bhlevel == null) {
          if (currentBusyhourSupport != null) {
            selectedBHLevel = currentBusyhourSupport.getBhlevel();
          }
        } else {
          selectedBHLevel = bhlevel; 
        }
        
        if (currentTargetTechPack == null) {
          currentBusyhourSupport = null;
        } else {
          currentBusyhourSupport = currentTargetTechPack.getBusyhourSupportByBhlevel(selectedBHLevel);
        }
        session.setAttribute("bhsupport", currentBusyhourSupport);

        if (infomessage != null) {
          request.setAttribute("infomessage", infomessage);
        }
        if (errormessage != null) {
          request.setAttribute("errormessage", errormessage);
        }
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
