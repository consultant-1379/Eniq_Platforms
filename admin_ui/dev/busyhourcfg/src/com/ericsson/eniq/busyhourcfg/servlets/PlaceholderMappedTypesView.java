/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.servlets;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author eheijun
 *
 */
public class PlaceholderMappedTypesView extends CommonServlet {

  /**
   * 
   */
  private static final long serialVersionUID = 6244423763212051936L;
  
  private final Logger log = Logger.getLogger(PlaceholderMappedTypesView.class.getName());

  /**
   * @see HttpServlet#HttpServlet()
   */
  public PlaceholderMappedTypesView() {
    log.fine("Instantiated servlet " + this.getClass().getName());
  }
  
  /**
   * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  protected void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
    IOException {
    try {
        request.getRequestDispatcher(View.PLACEHOLDERMAPPEDTYPES_VIEW_JSP).forward(request, response);
    } catch (Exception e) {
      request.setAttribute("errormessage", e.getMessage());
      request.getRequestDispatcher(View.ERROR_PAGE).forward(request, response);
    }
  }

}
