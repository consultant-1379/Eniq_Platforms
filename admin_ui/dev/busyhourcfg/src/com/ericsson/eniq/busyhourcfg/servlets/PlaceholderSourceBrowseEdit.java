/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.servlets;

import java.io.IOException;
import java.util.logging.Logger;
//import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ericsson.eniq.busyhourcfg.common.Constants;
import com.ericsson.eniq.busyhourcfg.data.vo.Source;

/**
 * @author eheijun
 * 
 */
public class PlaceholderSourceBrowseEdit extends CommonServlet {

  /**
   * 
   */
  private static final long serialVersionUID = 8552214192564646807L;
  
  private final Logger log = Logger.getLogger(PlaceholderSourceBrowseEdit.class.getName());

  /**
   * @see HttpServlet#HttpServlet()
   */
  public PlaceholderSourceBrowseEdit() {
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

        final HttpSession session = request.getSession(false);
        final Source currentSource = (Source) session.getAttribute("currentSource");

//        final Enumeration<String> names = request.getParameterNames();
//        while (names.hasMoreElements()) {
//          final String name = names.nextElement();
//          final String value = request.getParameter(name);
//          System.out.println(name + "=" + value);
//        }

        final String cancel = (String) request.getParameter("cancel");
        if ((cancel != null)) {
          log.fine("cancel button pressed");
          response.sendRedirect(View.PLACEHOLDERSOURCEVIEW);
          return;
        }
        
        final String basetable = request.getParameter("select_basetable");

        final String save = (String) request.getParameter("save");
        if ((save != null)) {
          log.fine("save button pressed");
          currentSource.setTypename(basetable);
          request.getRequestDispatcher(View.PLACEHOLDERSOURCEVIEW).forward(request, response);
          return;
        }

        request.getRequestDispatcher(View.PLACEHOLDERSOURCE_VIEW_JSP).forward(request, response);
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
