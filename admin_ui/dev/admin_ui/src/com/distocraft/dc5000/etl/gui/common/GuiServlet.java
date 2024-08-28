/**
 *
 */
package com.distocraft.dc5000.etl.gui.common;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.velocity.Template;
import org.apache.velocity.context.Context;
import org.apache.velocity.servlet.VelocityServlet;


/**
 * @author eheijun
 *
 */
@SuppressWarnings("deprecation")
public abstract class GuiServlet extends VelocityServlet {

  private static final long serialVersionUID = 1L;

  private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  private static final String THE_USER = "theuser";

  private static final String CURRENT_TIME = "currenttime";

  protected static final String ENVIRONMENT = "environment";

  @Override
  public Template handleRequest(final HttpServletRequest request, final HttpServletResponse response, final Context context) throws Exception {

    final HttpSession session = request.getSession();

    final String userName = getUserName(request);
    if (userName != null) {
      context.put(THE_USER, userName);
    }

    context.put(CURRENT_TIME, sdf.format(new Date()));

    if (session.getAttribute(ENVIRONMENT) != null) {
      context.put(ENVIRONMENT, session.getAttribute(ENVIRONMENT));
    } else {
      context.put(ENVIRONMENT, new EnvironmentNone());
    }

    return null;
  }

  private String getUserName(final HttpServletRequest request) {
    String userName = null;
    if (request.getUserPrincipal() != null) {
      userName = request.getUserPrincipal().getName();
    }
    return userName;
  }

}
