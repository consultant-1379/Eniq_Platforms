/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.servlets;

import java.io.IOException; //import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
//import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ericsson.eniq.busyhourcfg.common.Constants;
import com.ericsson.eniq.busyhourcfg.data.vo.Key;
import com.ericsson.eniq.busyhourcfg.data.vo.Placeholder;

/**
 * @author eheijun
 * 
 */
public class PlaceholderRankKeysEdit extends CommonServlet {

  /**
   * 
   */
  private static final long serialVersionUID = -1664673908600816472L;

  private final Logger log = Logger.getLogger(PlaceholderRankKeysEdit.class.getName());

  /**
   * @see HttpServlet#HttpServlet()
   */
  public PlaceholderRankKeysEdit() {
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
        final Map<Integer, String> keynameMap = new HashMap<Integer, String>();
        final Map<Integer, String> sourceMap = new HashMap<Integer, String>();
        final Map<Integer, String> keyvalueMap = new HashMap<Integer, String>();
        int editIndex = -1;
        int deleteIndex = -1;
        while (names.hasMoreElements()) {
          final String name = (String) names.nextElement();
          if (name.startsWith("keyname")) {
            final String value = name.substring(8, name.length());
            try {
              final int i = new Integer(value);
              keynameMap.put(i, request.getParameter(name));
            } catch (NumberFormatException e) {
              log.warning("invalid parameter " + name);
            }
          }
          if (name.startsWith("source")) {
            final String value = name.substring(7, name.length());
            try {
              final int i = new Integer(value);
              sourceMap.put(i, request.getParameter(name));
            } catch (NumberFormatException e) {
              log.warning("invalid parameter " + name);
            }
          }
          if (name.startsWith("keyvalue")) {
            final String value = name.substring(9, name.length());
            try {
              final int i = new Integer(value);
              keyvalueMap.put(i, request.getParameter(name));
            } catch (NumberFormatException e) {
              log.warning("invalid parameter " + name);
            }
          }
          if (name.startsWith("editsourcetable")) {
            final String value = name.substring(16, name.length());
            try {
              editIndex = new Integer(value);
            } catch (NumberFormatException e) {
              log.warning("invalid parameter " + name);
            }
          }
          if (name.startsWith("delete")) {
            final String value = name.substring(7, name.length());
            try {
              deleteIndex = new Integer(value);
            } catch (NumberFormatException e) {
              log.warning("invalid parameter " + name);
            }
          }
        }
        
        final HttpSession session = request.getSession(false);
        final Placeholder currentPlaceholder = (Placeholder) session.getAttribute("currentPlaceholder");

        // Cancel
        final String cancel = (String) request.getParameter("cancel");
        if ((cancel != null)) {
          log.fine("cancel button pressed");
          currentPlaceholder.undoKeyChanges();
          response.sendRedirect(View.PLACEHOLDERVIEW);
          return;
        }
        
        final Iterator<Map.Entry<Integer, String>> nameMapIt = keynameMap.entrySet().iterator(); 
        while(nameMapIt.hasNext()) {
          final Map.Entry<Integer, String> entry = nameMapIt.next(); 
          final Integer keyIndex = entry.getKey();
          final String formValue = entry.getValue();
          final Key key = currentPlaceholder.getKeys().get(keyIndex);
          key.setKeyName(formValue);
        }
        
        final Iterator<Map.Entry<Integer, String>> valueMapIt = keyvalueMap.entrySet().iterator(); 
        while(valueMapIt.hasNext()) {
          final Map.Entry<Integer, String> entry = valueMapIt.next(); 
          final Integer valueIndex = entry.getKey();
          final String formValue = entry.getValue();
          final Key key = currentPlaceholder.getKeys().get(valueIndex);
          key.setKeyValue(formValue);
        }
        
        final Iterator<Map.Entry<Integer, String>> sourceMapIt = sourceMap.entrySet().iterator(); 
        while(sourceMapIt.hasNext()) {
          final Map.Entry<Integer, String> entry = sourceMapIt.next(); 
          final Integer sourceIndex = entry.getKey();
          final String formValue = entry.getValue();
          final Key key = currentPlaceholder.getKeys().get(sourceIndex);
          key.setSource(formValue);
        }
        
        // edit a sourcetable
        if ((editIndex > -1)) {
          log.fine("editsourcetable button pressed");
          // store selected rankkeys
          final Key currentRankKey = currentPlaceholder.getKeys().get(editIndex);
          currentRankKey.setKeyName(keynameMap.get(editIndex));
          currentRankKey.setKeyValue(keyvalueMap.get(editIndex));
          currentRankKey.setSource(sourceMap.get(editIndex));
          session.setAttribute("currentRankKey", currentRankKey);
          response.sendRedirect(View.PLACEHOLDERRANKKEYSOURCEVIEW);
          return;
        }
        // Delete
        if ((deleteIndex != -1)) {
          log.fine("Delete button pressed");
          final Key rkey = currentPlaceholder.getKeys().get(deleteIndex);
          currentPlaceholder.removeKey(rkey);
          response.sendRedirect(View.PLACEHOLDERRANKKEYSVIEW);
          return;
        }
        // Add
        final String add = (String) request.getParameter("add");
        if ((add != null)) {
          log.fine("New button pressed");
          final String newName = "NEW_KEYNAME_";
          int cnt = 0;
          for (Key key : currentPlaceholder.getKeys()) {
            if (key.getKeyName().startsWith(newName)) {
              int cur = 0;
              try {
                cur = Integer.valueOf(key.getKeyName().substring(newName.length()));
              } catch (NumberFormatException e) {
                cur = 0;
              }
              cnt = Math.max(cnt, cur);
            }
          }
          cnt++;
          currentPlaceholder.addKey(newName + cnt, "", newName + cnt);
          response.sendRedirect(View.PLACEHOLDERRANKKEYSVIEW);
          return;
        }
        // Save
        final String save = (String) request.getParameter("save");
        if ((save != null)) {
          log.fine("save button pressed");
//          final List<Key> keys = currentPlaceholder.getKeys();
//          int i = 0;
//          for (Key key : keys) {
//            if (!currentPlaceholder.getRemovedKeys().contains(key)) {
//              key.setKeyName(keynameMap.get(i));
//              key.setSource(sourceMap.get(i));
//              key.setKeyValue(keyvalueMap.get(i));
//            }
//            i++;
//          }
//          currentPlaceholder.getRemovedKeys().clear();
//          currentPlaceholder.setKeys(keys);
          request.getRequestDispatcher(View.PLACEHOLDERVIEW).forward(request, response);
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
