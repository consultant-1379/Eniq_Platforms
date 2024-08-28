/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.servlets;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ericsson.eniq.busyhourcfg.common.Constants;
import com.ericsson.eniq.busyhourcfg.database.UpdateResult;

/**
 * @author eheijun
 * 
 */
public class ActivationStatusView extends CommonServlet {

  /**
   * 
   */
  private static final long serialVersionUID = 3672885778464979488L;

  static final String ACTION_FUTURE = "actionFuture";

  private final Logger log = Logger.getLogger(ActivationStatusView.class.getName());

  private static final String SUCCESSFULLY_DONE = "Busy Hour Update successfully done.";

  private static final String UPDATE_FAILED = "Busy Hour Update failed.";

  /**
   * @see HttpServlet#HttpServlet()
   */
  public ActivationStatusView() {
    log.fine("Instantiated servlet " + this.getClass().getName());
  }

  /**
   * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */
  protected void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
      IOException {
    final HttpSession session = request.getSession(false);
    try {
      String redirectURL = View.BUSYHOURVIEW;
      final Object tmpFuture = session.getAttribute(ACTION_FUTURE);
      if (tmpFuture != null && tmpFuture instanceof Future<?>) {
        final Future<?> future = (Future<?>) tmpFuture;
        if (future.isDone()) {
          session.removeAttribute(ACTION_FUTURE);
          final UpdateResult success = (UpdateResult) future.get();
          if (success.isInfo()) {
            redirectURL += "?infomessage=" + URLEncoder.encode(SUCCESSFULLY_DONE, Constants.DEFAULT_ENCODING);
          } else if (success.isError()) {
            redirectURL += "?errormessage=" + URLEncoder.encode(UPDATE_FAILED, Constants.DEFAULT_ENCODING);
          }
          response.sendRedirect(redirectURL);
          return;
        }
      } else {
        response.sendRedirect(redirectURL);
        return;
      }
      request.getRequestDispatcher(View.ACTIVATIONSTATUS_VIEW_JSP).forward(request, response);
    } catch (Exception e) {
      request.setAttribute("errormessage", e.getMessage());
      request.getRequestDispatcher(View.ERROR_PAGE).forward(request, response);
    }
  }

}
