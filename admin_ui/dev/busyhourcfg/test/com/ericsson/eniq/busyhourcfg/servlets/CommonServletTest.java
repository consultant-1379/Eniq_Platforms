/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.servlets;

import static org.junit.Assert.*;

import java.rmi.RemoteException;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import com.ericsson.eniq.busyhourcfg.database.RockDatabaseSession;

/**
 * @author eheijun
 * 
 */
public class CommonServletTest {

  private final Mockery context = new JUnit4Mockery() {

    {
      setImposteriser(ClassImposteriser.INSTANCE);
    }
  };

  private MockHttpSession webmockSession;

  private RockDatabaseSession mockDatabaseSession;

  private MockHttpServletRequest webmockRequest;

  private MockHttpServletResponse webmockResponse;

  @Before
  public void setUp() throws RemoteException {
    webmockSession = new MockHttpSession(null);
    mockDatabaseSession = context.mock(RockDatabaseSession.class);
    webmockSession.setAttribute("databasesession", mockDatabaseSession);
    webmockRequest = new MockHttpServletRequest();
    webmockResponse = new MockHttpServletResponse();
    webmockRequest.setSession(webmockSession);
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.servlets.CommonServlet#isRequestValid(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, boolean)}
   * .
   */
  @Test
  public void testIsRequestValidHttpServletRequestHttpServletResponseNoRedirect() {
    final CommonServlet cs = new CommonServlet();
    try {
      final boolean result = cs.isRequestValid(webmockRequest, webmockResponse, false);
      assertTrue(result);
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.servlets.CommonServlet#isRequestValid(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, boolean)}
   * .
   */
  @Test
  public void testIsRequestValidHttpServletRequestHttpServletResponseFail() {
    final CommonServlet cs = new CommonServlet();
    try {
      // invalidate session so request should not be valid after that
      webmockSession.invalidate();
      final boolean result = cs.isRequestValid(webmockRequest, webmockResponse, false);
      assertFalse(result);
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.servlets.CommonServlet#isRequestValid(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
   * .
   */
  @Test
  public void testIsRequestValidHttpServletRequestHttpServletResponseRedirect() {
    final CommonServlet cs = new CommonServlet();
    try {
      final boolean result = cs.isRequestValid(webmockRequest, webmockResponse);
      assertTrue(result);
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.servlets.CommonServlet#logout(javax.servlet.http.HttpSession)}
   * .
   */
  @Test
  public void testLogout() {
    final CommonServlet cs = new CommonServlet();
    try {
      cs.logout(webmockSession);
      assertTrue(webmockSession.isInvalid());
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }
}
