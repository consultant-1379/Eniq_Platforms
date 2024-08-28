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
public class PlaceholderTypeView extends CommonServlet {

  /**
   * 
   */
  private static final long serialVersionUID = -5333027387577732585L;
  
  private final Logger log = Logger.getLogger(PlaceholderTypeView.class.getName());

  /**
   * @see HttpServlet#HttpServlet()
   */
  public PlaceholderTypeView() {
    log.fine("Instantiated servlet " + this.getClass().getName());
  }
  
  /**
   * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  protected void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
    IOException {
    try {
        request.getRequestDispatcher(View.PLACEHOLDERTYPE_VIEW_JSP).forward(request, response);
    } catch (Exception e) {
      request.setAttribute("errormessage", e.getMessage());
      request.getRequestDispatcher(View.ERROR_PAGE).forward(request, response);
    }
  }

}
