/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ericsson.eniq.busyhourcfg.common.Constants;
import com.ericsson.eniq.busyhourcfg.data.vo.MappedType;
import com.ericsson.eniq.busyhourcfg.data.vo.Placeholder;

/**
 * @author eheijun
 * 
 */
public class PlaceholderMappedTypesEdit extends CommonServlet {

  /**
   * 
   */
  private static final long serialVersionUID = -7909652654235547783L;
  
  private final Logger log = Logger.getLogger(PlaceholderMappedTypesEdit.class.getName());

  /**
   * @see HttpServlet#HttpServlet()
   */
  public PlaceholderMappedTypesEdit() {
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
        
        final List<Integer> checked = new ArrayList<Integer>();
        final Map<String,Integer> targettypes = new HashMap<String,Integer>();

        while (names.hasMoreElements()) {
          final String name = (String) names.nextElement();
          final String value = request.getParameter(name);
          if (name.startsWith("enabled.")) {
            final String index = name.substring(8, name.length());
            checked.add(Integer.parseInt(index));           
          }
          if (name.startsWith("targettype.")) {
              final String index = name.substring(11, name.length());
              targettypes.put(value,Integer.parseInt(index));               
            }
          log.fine("request parameter" + name + "=" + value);
        }
        
        final HttpSession session = request.getSession(false);
        final Placeholder currentPlaceholder = (Placeholder) session.getAttribute("currentPlaceholder");

        final String cancel = (String) request.getParameter("cancel");
        if ((cancel != null)) {
          log.fine("cancel button pressed");
          currentPlaceholder.undoMappedTypeChanges();
          response.sendRedirect(View.PLACEHOLDERVIEW);
          return;
        }
      
        final String save = (String) request.getParameter("save");
        if ((save != null)) {
          log.fine("save button pressed");
          
          

          final List<MappedType> mappedTypes = currentPlaceholder.getMappedTypes();
          for(MappedType mt : mappedTypes){
        	  

        		  if (checked.contains(targettypes.get(mt.getBhTargettype()))){
                mt.setEnabled(true);
        		  } else {
                mt.setEnabled(false);
        		  }

        	  
          }
          
          currentPlaceholder.setMappedTypes(mappedTypes);
          
          
          
          request.getRequestDispatcher(View.PLACEHOLDERVIEW).forward(request, response);
          return;
        }
                
        request.getRequestDispatcher(View.PLACEHOLDERMAPPEDTYPES_VIEW_JSP).forward(request, response);
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
