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
import com.ericsson.eniq.busyhourcfg.data.vo.Placeholder;
import com.ericsson.eniq.busyhourcfg.data.vo.TargetTechPack;
import com.ericsson.eniq.busyhourcfg.data.vo.TechPack;

/**
 * @author eheijun
 *
 */
public class PlaceholderView extends CommonServlet {

  /**
   * 
   */
  private static final long serialVersionUID = -7269728000768196146L;
  
  private final Logger log = Logger.getLogger(PlaceholderView.class.getName());

  /**
   * @see HttpServlet#HttpServlet()
   */
  public PlaceholderView() {
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
        final TechPack selectedTechPack = (TechPack) request.getSession(false).getAttribute("sourcetp");
        if (selectedTechPack != null) {
          final String targettp = (String) request.getParameter("targettp");
          if (targettp != null) {
            final TargetTechPack currentTargetTechPack = selectedTechPack.getTargetTechPackByVersionId(targettp);
            //selectedTechPack.setCurrentTargetTechPack(currentTargetTechPack);
            final String bhlevel = (String) request.getParameter("bhsupport");
            if (bhlevel != null) {
              final BusyhourSupport currentBusyhourSupport = currentTargetTechPack.getBusyhourSupportByBhlevel(bhlevel);
              //currentTargetTechPack.setCurrentBHSupport(currentBusyhourSupport);
              final String bhtype = (String) request.getParameter("bhtype");
              if (bhtype != null) {
                session.removeAttribute("placeholder");
                final Placeholder currentPlaceholder = currentBusyhourSupport.getPlaceholderByBhtype(bhtype);
                //currentBusyhourSupport.setCurrentPlaceholder(currentPlaceholder);
                session.setAttribute("placeholder", currentPlaceholder);
              }
            }
          }
        }
        request.getRequestDispatcher(View.PLACEHOLDER_VIEW_JSP).forward(request, response);
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
