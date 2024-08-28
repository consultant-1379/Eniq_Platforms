/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.servlets;

import java.io.IOException; //import java.util.Enumeration;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse; 
import javax.servlet.http.HttpSession;

import com.ericsson.eniq.busyhourcfg.common.Constants;

import com.ericsson.eniq.busyhourcfg.data.vo.Key;

/**
 * @author eheijun
 * 
 */
public class PlaceholderRankKeySourceEdit extends CommonServlet {

  /**
   * 
   */
  private static final long serialVersionUID = 1790188155235211946L;

  private final Logger log = Logger.getLogger(PlaceholderRankKeySourceEdit.class.getName());

  /**
   * @see HttpServlet#HttpServlet()
   */
  public PlaceholderRankKeySourceEdit() {
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
        final Key currentRankKey = (Key) session.getAttribute("currentRankKey");
        //    	  
        //    	final Enumeration<?> names = request.getParameterNames();
        //        String sourceTable = currentRankKey.getSource();
        //        while (names.hasMoreElements()) {
        //            final String name = (String) names.nextElement();  
        //            if (name.equals("source")){
        //            	sourceTable = request.getParameter(name);
        //            }
        //        }
        final String cancel = (String) request.getParameter("cancel");
        if ((cancel != null)) {
          log.fine("cancel button pressed");
          response.sendRedirect(View.PLACEHOLDERRANKKEYSVIEW);
          return;
        }
        
        final String sourcetable = request.getParameter("source_table");
        
        final String save = (String) request.getParameter("save");
        if ((save != null)) {
          log.fine("save button pressed");
          currentRankKey.setSource(sourcetable);
          request.getRequestDispatcher(View.PLACEHOLDERRANKKEYSVIEW).forward(request, response);
          return;
        }
        request.getRequestDispatcher(View.PLACEHOLDERRANKKEYS_VIEW_JSP).forward(request, response);
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
