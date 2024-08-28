package com.ericsson.eniq.busyhourcfg.servlets;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ericsson.eniq.busyhourcfg.common.Constants;
import com.ericsson.eniq.busyhourcfg.data.vo.TargetTechPack;
import com.ericsson.eniq.busyhourcfg.data.vo.TechPack;
import com.ericsson.eniq.busyhourcfg.database.DatabaseSession;
import com.ericsson.eniq.busyhourcfg.database.TechPackReader;
import com.ericsson.eniq.busyhourcfg.database.TechPackReaderFactory;

/**
 * Servlet implementation class BusyhourView
 */
public class BusyhourView extends CommonServlet {

  /**
   * 
   */
  private static final long serialVersionUID = -9093997028897828891L;
  
  private final Logger log = Logger.getLogger(BusyhourView.class.getName());

  /**
   * @see HttpServlet#HttpServlet()
   */
  public BusyhourView() {
    log.fine("Instantiated servlet " + this.getClass().getName());
  }

  /*
   * (non-Javadoc)
   * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  protected void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
      IOException {
    try {
      if (isRequestValid(request, response)) {
        
        final HttpSession session = request.getSession(false);

        final String cancel = (String) request.getParameter("cancel");
        if ((cancel != null)) {
          log.fine("cancel button pressed");
          response.sendRedirect(View.TECHPACKVIEW);
          return;
        }
        
        final String infomessage = (String) request.getParameter("infomessage");
        final String errormessage = (String) request.getParameter("errormessage");
        
        TechPack currentTechPack = (TechPack) session.getAttribute("sourcetp");
        TargetTechPack currentTargetTechPack = (TargetTechPack) session.getAttribute("targettp");

        final String sourcetp = (String) request.getParameter("sourcetp");
        if (sourcetp != null) {
          session.removeAttribute("sourcetp");
          session.removeAttribute("targettp");
          session.removeAttribute("bhsupport");
          session.removeAttribute("placeholder");
        }
        
        String selectedVersionId = "";
        if (sourcetp == null) {
          selectedVersionId = (String) session.getAttribute("versionid");
        } else {
          selectedVersionId = sourcetp; 
        }

        final DatabaseSession databaseSession = (DatabaseSession) session.getAttribute("databasesession");
        final TechPackReader reader = TechPackReaderFactory.getTechPackReader(databaseSession);
        currentTechPack = reader.getTechPackByVersionId(selectedVersionId);
        if (currentTechPack == null) {
          throw new Exception("Can not read busyhour information from database");
        }
        session.setAttribute("sourcetp", currentTechPack);

        // currently target techpack is always same as  source techpack
        final String selectedTargetTpId = currentTechPack.getVersionId();

        currentTargetTechPack = currentTechPack.getTargetTechPackByVersionId(selectedTargetTpId);
        session.setAttribute("targettp", currentTargetTechPack);

        final String activate = (String) request.getParameter("activate");
        if ((activate != null)) {
          log.fine("activate button pressed");
          request.getRequestDispatcher(View.ACTIVATIONSTART).forward(request, response);
          return;
        }
        
        if (infomessage != null) {
          request.setAttribute("infomessage", infomessage);
        }
        if (errormessage != null) {
          request.setAttribute("errormessage", errormessage);
        }
        request.getRequestDispatcher(View.BUSYHOUR_VIEW_JSP).forward(request, response);
        
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
