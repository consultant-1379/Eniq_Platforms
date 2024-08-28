/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.servlets;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ericsson.eniq.busyhourcfg.common.Constants;
import com.ericsson.eniq.busyhourcfg.common.Utils;
import com.ericsson.eniq.busyhourcfg.data.vo.BusyhourSupport;
import com.ericsson.eniq.busyhourcfg.data.vo.Placeholder;
import com.ericsson.eniq.busyhourcfg.data.vo.TargetTechPack;
import com.ericsson.eniq.busyhourcfg.data.vo.TechPack;
import com.ericsson.eniq.busyhourcfg.database.BusyhourUpdater;
import com.ericsson.eniq.busyhourcfg.database.BusyhourUpdaterFactory;
import com.ericsson.eniq.busyhourcfg.database.DatabaseSession;
import com.ericsson.eniq.busyhourcfg.database.TechPackReader;
import com.ericsson.eniq.busyhourcfg.database.TechPackReaderFactory;
import com.ericsson.eniq.busyhourcfg.dwhmanager.DWHStorageTimeAction;

/**
 * @author eheijun
 * 
 */
public class PlaceholderEdit extends CommonServlet {

  /**
   * 
   */
  private static final long serialVersionUID = -2141065182349157279L;
  
  private final Logger log = Logger.getLogger(PlaceholderEdit.class.getName());

  /**
   * Servlet for handling PlaceholderView post
   * @see HttpServlet#HttpServlet()
   */
  public PlaceholderEdit() {
    log.fine("Instantiated servlet " + this.getClass().getName());
  }

  /**
   * Handles actions generated from PlaceholderView 
   * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */
  protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
      IOException {
    try {
      if (isRequestValid(request, response)) {
        final HttpSession session = request.getSession(false);
        final DWHStorageTimeAction dwhStorageTimeAction = (DWHStorageTimeAction) session.getAttribute("dwhstoragetimeaction");
        final DatabaseSession databaseSession = (DatabaseSession) session.getAttribute("databasesession");
        final TechPack selectedTechPack = (TechPack) session.getAttribute("sourcetp");

        final String sourcetp = selectedTechPack.getVersionId();
        final String targettp = (String) request.getParameter("targettp");
        final String bhlevel = (String) request.getParameter("bhsupport");
        final String bhtype = (String) request.getParameter("bhtype");
        
        TargetTechPack currentTargetTechPack = null;
        BusyhourSupport currentBusyhourSupport = null;
        Placeholder currentPlaceholder = null;
        if (selectedTechPack != null) {
          if (targettp != null) {
            currentTargetTechPack = selectedTechPack.getTargetTechPackByVersionId(targettp);
            if (bhlevel != null) {
              currentBusyhourSupport = currentTargetTechPack.getBusyhourSupportByBhlevel(bhlevel);
              if (bhtype != null) {
                currentPlaceholder = currentBusyhourSupport.getPlaceholderByBhtype(bhtype);
              }
            }
          }
        }
        
        session.setAttribute("currentTargetTechPack", currentTargetTechPack);
        session.setAttribute("currentBusyhourSupport", currentBusyhourSupport);
        session.setAttribute("currentPlaceholder", currentPlaceholder);
        
        String redirectURL = View.PLACEHOLDERSVIEW + "?targettp=" + URLEncoder.encode(Utils.replaceNull(targettp), Constants.DEFAULT_ENCODING) + "&bhsupport="
          + URLEncoder.encode(Utils.replaceNull(bhlevel), Constants.DEFAULT_ENCODING);

        final String cancel = (String) request.getParameter("cancel");
        if ((cancel != null)) {
          log.fine("cancel button pressed");
          if (currentPlaceholder != null) {
            currentPlaceholder.undoAllChanges();
          }
          response.sendRedirect(redirectURL);
          return;
        }
        
        final String description = Utils.replaceNull((String) request.getParameter("description"));
        final String where = Utils.replaceNull((String) request.getParameter("where"));
        final String criteria = Utils.replaceNull((String) request.getParameter("criteria"));
        String grouping = (String) request.getParameter("grouping");
        if (grouping == null ){
        	grouping = currentPlaceholder.getGrouping();
        }
        
        currentPlaceholder.setDescription(description);
        currentPlaceholder.setWhere(where);
        currentPlaceholder.setCriteria(criteria);
        currentPlaceholder.setGrouping(grouping);
        
        String infomessage = "";
        String errormessage = "";
        
        final String clear = (String) request.getParameter("clear");
        if (clear != null) {
          log.fine("clear button pressed");
          final BusyhourUpdater busyhourUpdater = BusyhourUpdaterFactory.getBusyhourUpdater(databaseSession, dwhStorageTimeAction);
          currentPlaceholder.clearAllData();
          busyhourUpdater.savePlaceholder(currentPlaceholder);
          infomessage = "Placeholder successfully cleared.";
          redirectURL += (infomessage == null ? "" : "&infomessage=" + URLEncoder.encode(infomessage, Constants.DEFAULT_ENCODING));
          response.sendRedirect(redirectURL);
          return;
        }

        final String save = (String) request.getParameter("save");
        if (save != null) {
          log.fine("save button pressed");
          final BusyhourUpdater busyhourUpdater = BusyhourUpdaterFactory.getBusyhourUpdater(databaseSession, dwhStorageTimeAction);
          errormessage = busyhourUpdater.validatePlaceholder(currentPlaceholder);
          if (errormessage.equals("")) {
            try {
              busyhourUpdater.savePlaceholder(currentPlaceholder);
              final TechPackReader reader = TechPackReaderFactory.getTechPackReader(databaseSession);
              final TechPack tp = reader.getTechPackByVersionId(sourcetp);
              session.setAttribute("sourcetp", tp);
              final TargetTechPack ttp = tp.getTargetTechPackByVersionId(targettp);
              session.setAttribute("targettp", ttp);
              final BusyhourSupport bhs = currentTargetTechPack.getBusyhourSupportByBhlevel(bhlevel);
              session.setAttribute("bhsupport", bhs);
              infomessage = "Placeholder " + currentPlaceholder.getBhtype() + " successfully saved.";
              if (tp.getModified() > 0) {
                infomessage += " Tech Pack Busy Hours still needs to be updated.";
              }
              log.info(infomessage);
              redirectURL += (infomessage == null ? "" : "&infomessage=" + infomessage);
              response.sendRedirect(redirectURL);
              return;
            } catch (Exception e) {
              errormessage = e.getMessage();
            }
          }
        }

        final String editsource = (String) request.getParameter("editsource");
        if ((editsource != null)) {
          log.fine("editsource button pressed");
          response.sendRedirect(View.PLACEHOLDERSOURCEVIEW);
          return;
        }
        
        final String edittype = (String) request.getParameter("edittype");
        if ((edittype != null)) {
          log.fine("edittype button pressed");
          response.sendRedirect(View.PLACEHOLDERTYPEVIEW);
          return;
        }
        
        final String mappedtype = (String) request.getParameter("editmappedtype");
        if ((mappedtype != null)) {
          log.fine("editmappedtype button pressed");
          response.sendRedirect(View.PLACEHOLDERMAPPEDTYPESVIEW);
          return;
        }
        
        final String rankkeys = (String) request.getParameter("editkeys");
        if ((rankkeys != null)) {
          log.fine("editkeys button pressed");
          response.sendRedirect(View.PLACEHOLDERRANKKEYSVIEW);
          return;
        }
        
        request.setAttribute("errormessage", errormessage);
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
