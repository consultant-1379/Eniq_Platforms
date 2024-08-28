/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.servlets;

import static org.junit.Assert.*;

import java.rmi.RemoteException;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author eheijun
 * 
 */
public class PlaceholderTypeViewTest {

  private MockHttpServletRequest webmockRequest;

  private MockHttpServletResponse webmockResponse;

  @Before
  public void setUp() throws RemoteException {
    webmockRequest = new MockHttpServletRequest("GET", "/servlet/PlaceholderTypeView");
    webmockResponse = new MockHttpServletResponse();
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.servlets.PlaceholderTypeView#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
   * .
   */
  @Test
  public void testServiceHttpServletRequestHttpServletResponse() {
    final PlaceholderTypeView pv = new PlaceholderTypeView();
    try {
      pv.service(webmockRequest, webmockResponse);
      final String forwardURL = webmockResponse.getForwardedUrl();
      assertTrue(View.PLACEHOLDERTYPE_VIEW_JSP.equals(forwardURL));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }
}
