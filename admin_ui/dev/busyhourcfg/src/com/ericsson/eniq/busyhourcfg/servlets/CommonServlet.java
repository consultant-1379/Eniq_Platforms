package com.ericsson.eniq.busyhourcfg.servlets;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
/**
 * This is a master class for all servlets in the application. 
 * Include common methods and attributes in this class.
 */
public class CommonServlet extends HttpServlet {

  /**
   * 
   */
  private static final long serialVersionUID = -7358665109905085496L;
  
  private final Logger log = Logger.getLogger(CommonServlet.class.getName());

  /**
   * Check if the request is valid. To have a valid request we need to make sure
   * that the session exists and contains the necessary data, and that the
   * session is properly authenticated. If this is not the case, we send a
   * redirect to the login page. If all data is properly initialized the method
   * returns true.
   * 
   * @param request
   *          the request to check
   * @param response
   *          the response to redirect if necessary
   * @param redirect
   *          true if messages and redirects should be issued.
   * @return true if all session data is valid, false (and redirect) otherwise.
   */
  protected boolean isRequestValid(final HttpServletRequest request, final HttpServletResponse response,
      final boolean redirect) throws ServletException, IOException {
    // get the session, but make sure not to create one if none is already available.
    final HttpSession session = request.getSession(false);
    if (session == null) {
      if (redirect) {
        redirectToLogin(request, response, "The session is not set or has expired. Please log in again.");
      }
      return false;
    } else {
      final Object databaseSession = session.getAttribute("databasesession");
      if (databaseSession == null) {
        if (redirect) {
          redirectToLogin(request, response, "The session is not valid. Please log in again.");
        }
        return false;
      }
      return true;
    }
  }

  /**
   * Helper method for the isRequestValid(HttpServletRequest,
   * HttpServletResponse, boolean) method.
   * 
   * @param request
   * @param response
   * @return
   * @throws ServletException
   * @throws IOException
   */
  protected boolean isRequestValid(final HttpServletRequest request, final HttpServletResponse response)
      throws ServletException, IOException {
    return isRequestValid(request, response, true);
  }

  /**
   * Redirects to the login page via the error page showing a message of choice.
   * 
   * @param request
   * @param response
   * @param errormessage
   * @throws ServletException
   * @throws IOException
   */
  private void redirectToLogin(final HttpServletRequest request, final HttpServletResponse response,
      final String errormessage) throws ServletException, IOException {
    log.fine("Redirecting to TechpackView with the message: " + errormessage);
    request.setAttribute("errormessage", errormessage);
    request.setAttribute("redirect", View.TECHPACKVIEW);
    request.getRequestDispatcher(View.ERROR_PAGE).forward(request, response);
  }

  /**
   * Cleans up the needed data and invalidates the session.
   * 
   * @param session
   *          the session to log out from.
   */
  protected void logout(final HttpSession session) {
    if (session != null) {
      session.invalidate();
    }
  }
}
