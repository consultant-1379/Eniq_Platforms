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
public class PlaceholderRankKeySourceViewTest {

  private MockHttpServletRequest webmockRequest;

  private MockHttpServletResponse webmockResponse;

  @Before
  public void setUp() throws RemoteException {
    webmockRequest = new MockHttpServletRequest("GET", "/servlet/PlaceholderRankKeySourceView");
    webmockResponse = new MockHttpServletResponse();
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.servlets.PlaceholderSourceView#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
   * .
   */
  @Test
  public void testServiceHttpServletRequestHttpServletResponse() {
    final PlaceholderRankKeySourceView pv = new PlaceholderRankKeySourceView();
    try {
      pv.service(webmockRequest, webmockResponse);
      final String forwardURL = webmockResponse.getForwardedUrl();
      assertTrue(View.PLACEHOLDERRANKKEYSOURCE_VIEW_JSP.equals(forwardURL));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }
}
