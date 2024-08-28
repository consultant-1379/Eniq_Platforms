/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ericsson.eniq.busyhourcfg.database.DatabaseSession;
import com.ericsson.eniq.busyhourcfg.database.TechPackReader;
import com.ericsson.eniq.busyhourcfg.database.TechPackReaderFactory;

/**
 * @author eheijun
 * 
 */
public class BusyhourSourceList extends CommonServlet {

  /**
   * 
   */
  private static final long serialVersionUID = -7006802963229468954L;

  private final Logger log = Logger.getLogger(BusyhourSourceList.class.getName());

  /**
   * @see HttpServlet#service(HttpServletRequest request,
   *      HttpServletResponseresponse)
   */
  protected void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
      IOException {
    if (isRequestValid(request, response, false)) {
      final boolean techpackList = (request.getParameter("getSourceTechpacks") != null);
      final boolean basetableList = (request.getParameter("getSourceBasetables") != null);
      final StringBuffer result = new StringBuffer();
      final HttpSession session = request.getSession(false);
      if (session != null) {
        final DatabaseSession databaseSession = (DatabaseSession) session.getAttribute("databasesession");
        if (databaseSession != null) {
          final TechPackReader reader = TechPackReaderFactory.getTechPackReader(databaseSession);
          if (techpackList) {
            // String paramvalue = request.getParameter("getSourceTechpacks");
            try {
              final List<String> techpacks = reader.getAllBusyhourSourceTechpacks();
              result.append("({\n\"name\": [");
              boolean first = true;
              for (String versionid : techpacks) {
                if (first) {
                  first = false;
                } else {
                  result.append(",");
                }
                result.append("\n\"" + versionid + "\"");
              }
              result.append("]\n})");
            } catch (Exception e) {
              log.severe(e.getMessage());
            }
          } else if (basetableList) {
            final String paramvalue = request.getParameter("getSourceBasetables");
            try {
              final List<String> basetables = reader.getAllBusyhourBasetables(paramvalue);
              result.append("({\n\"name\": [");
              boolean first = true;
              for (String basetable : basetables) {
                if (first) {
                  first = false;
                } else {
                  result.append(",");
                }
                result.append("\n\"" + basetable + "\"");
              }
              result.append("]\n})");
            } catch (Exception e) {
              log.severe(e.getMessage());
            }
          } else {
            result.append("({\n\"name\": [");
            result.append("\n\"\",");
            result.append("]\n})");
            log.warning("Invalid call, return empty JSON set");
          }
          final PrintWriter out = response.getWriter();
          out.println(result.toString());
        }
      }
    }
  }

}
