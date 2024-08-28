/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ericsson.eniq.busyhourcfg.common.Constants;
import com.ericsson.eniq.busyhourcfg.data.vo.Placeholder;
import com.ericsson.eniq.busyhourcfg.data.vo.Source;
import com.ericsson.eniq.busyhourcfg.database.BusyhourUpdater;
import com.ericsson.eniq.busyhourcfg.database.BusyhourUpdaterFactory;
import com.ericsson.eniq.busyhourcfg.database.DatabaseSession;
import com.ericsson.eniq.busyhourcfg.dwhmanager.DWHStorageTimeAction;

/**
 * @author eheijun
 * 
 */
public class PlaceholderSourceEdit extends CommonServlet {

  /**
   * 
   */
  private static final long serialVersionUID = -7240282251708358383L;
  
  
  private final Logger log = Logger.getLogger(PlaceholderSourceEdit.class.getName());

  /**
   * @see HttpServlet#HttpServlet()
   */
  public PlaceholderSourceEdit() {
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
        final List<Integer> typenameIdList = new ArrayList<Integer>();
        final List<Integer> removedIdList = new ArrayList<Integer>();
        final List<Integer> browseIdList = new ArrayList<Integer>();
        while (names.hasMoreElements()) {
          final String name = (String) names.nextElement();
          if (name.startsWith("typename.")) {
            final String value = name.substring(9, name.length());
            try {
              typenameIdList.add(new Integer(value));
            } catch (NumberFormatException e) {
              log.warning("invalid parameter " + name);
            }
          }
          if (name.startsWith("delete.")) {
            final String value = name.substring(7, name.length());
            try {
              removedIdList.add(new Integer(value));
            } catch (NumberFormatException e) {
              log.warning("invalid parameter " + name);
            }
          }
          if (name.startsWith("edittypename.")) {
            final String value = name.substring(13, name.length());
            try {
              browseIdList.add(new Integer(value));
            } catch (NumberFormatException e) {
              log.warning("invalid parameter " + name);
            }
          }
          final String value = request.getParameter(name);
          log.fine("request parameter" + name + "=" + value);
        }
        
        
        
        final HttpSession session = request.getSession(false);
        final DWHStorageTimeAction dwhStorageTimeAction = (DWHStorageTimeAction) session.getAttribute("dwhstoragetimeaction");
        final DatabaseSession databaseSession = (DatabaseSession) session.getAttribute("databasesession");
        final Placeholder currentPlaceholder = (Placeholder) session.getAttribute("currentPlaceholder");

        final String cancel = (String) request.getParameter("cancel");
        if ((cancel != null)) {
          log.fine("cancel button pressed");
          currentPlaceholder.undoSourceChanges();          
          response.sendRedirect(View.PLACEHOLDERVIEW);
          return;
        }

        for (Integer typenameId : typenameIdList) {
          final Source source = currentPlaceholder.getSources().get(typenameId);
          source.setTypename(request.getParameter("typename." + typenameId));
        }
        
        if ((removedIdList.size() > 0)) {
          log.fine("delete button pressed");
          // Currently only one row can be removed at a time
          final Source source = currentPlaceholder.getSources().get(removedIdList.get(0)); 
          currentPlaceholder.removeSource(source);
          request.getRequestDispatcher(View.PLACEHOLDERSOURCEVIEW).forward(request, response);
          return;
        }
        
        final String add = (String) request.getParameter("add");
        if ((add != null)) {
          log.fine("add button pressed");
          final String newName = "";
          currentPlaceholder.addSource(newName);
          request.getRequestDispatcher(View.PLACEHOLDERSOURCEVIEW).forward(request, response);
          return;
        }
        
        final String save = (String) request.getParameter("save");
        if ((save != null)) {
          log.fine("save button pressed");
          
          
          final BusyhourUpdater busyhourUpdater = BusyhourUpdaterFactory.getBusyhourUpdater(databaseSession, dwhStorageTimeAction);
          busyhourUpdater.generateBusyhourrankkeys(currentPlaceholder);          
          request.getRequestDispatcher(View.PLACEHOLDERVIEW).forward(request, response);
          return;
        }

        if ((browseIdList.size() > 0)) {
          log.fine("edittypename button pressed");
          // Currently only one row can be selected at a time
          final Source currentSource = currentPlaceholder.getSources().get(browseIdList.get(0));
          session.setAttribute("currentSource", currentSource);
          response.sendRedirect(View.PLACEHOLDERSOURCEBROWSEVIEW);
          return;
        }
        
        response.sendRedirect(View.PLACEHOLDERSOURCEVIEW);
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
