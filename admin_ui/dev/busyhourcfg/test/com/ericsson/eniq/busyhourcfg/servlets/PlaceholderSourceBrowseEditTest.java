/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.servlets;

import static org.junit.Assert.*;

import java.rmi.RemoteException;

import javax.servlet.http.HttpSession;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import com.ericsson.eniq.busyhourcfg.data.vo.Source;
import com.ericsson.eniq.busyhourcfg.database.RockDatabaseSession;

/**
 * @author eheijun
 * 
 */
public class PlaceholderSourceBrowseEditTest {

  private final Mockery context = new JUnit4Mockery() {

    {
      setImposteriser(ClassImposteriser.INSTANCE);
    }
  };

  private HttpSession webmockSession;

  private RockDatabaseSession mockDatabaseSession;

  private MockHttpServletRequest webmockRequest;

  private MockHttpServletResponse webmockResponse;

  private Source mockSource;

  @Before
  public void setUp() throws RemoteException {
    webmockSession = new MockHttpSession(null);
    mockDatabaseSession = context.mock(RockDatabaseSession.class);
    mockSource = context.mock(Source.class);
    context.checking(new Expectations() {

      {
        allowing(mockSource).setTypename(with(any(String.class)));
      }
    });
    webmockSession.setAttribute("databasesession", mockDatabaseSession);
    webmockSession.setAttribute("currentSource", mockSource);
    webmockRequest = new MockHttpServletRequest("GET", "/servlet/PlaceholderSourceBrowseEdit");
    webmockRequest.setSession(webmockSession);
    webmockResponse = new MockHttpServletResponse();
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.servlets.PlaceholderSourceBrowseEdit#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
   * .
   */
  @Test
  public void testDoPostHttpServletRequestHttpServletResponseWithCancel() {
    webmockRequest.setParameter("cancel", "true");
    final PlaceholderSourceBrowseEdit psbe = new PlaceholderSourceBrowseEdit();
    try {
      psbe.doPost(webmockRequest, webmockResponse);
      final String redirectURL = webmockResponse.getRedirectedUrl();
      assertTrue(View.PLACEHOLDERSOURCEVIEW.equals(redirectURL));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.servlets.PlaceholderSourceBrowseEdit#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
   * .
   */
  @Test
  public void testDoPostHttpServletRequestHttpServletResponseWithInvalidParams() {
    final PlaceholderSourceBrowseEdit psbe = new PlaceholderSourceBrowseEdit();
    try {
      psbe.doPost(webmockRequest, webmockResponse);
      final String forwardURL = webmockResponse.getForwardedUrl();
      assertTrue(View.PLACEHOLDERSOURCE_VIEW_JSP.equals(forwardURL));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.servlets.PlaceholderSourceBrowseEdit#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
   * .
   */
  @Test
  public void testDoPostHttpServletRequestHttpServletResponseWithSave() {
    webmockRequest.setParameter("select_basetable", "TEST_TABLE");
    webmockRequest.setParameter("save", "true");
    final PlaceholderSourceBrowseEdit psbe = new PlaceholderSourceBrowseEdit();
    try {
      psbe.doPost(webmockRequest, webmockResponse);
      final String forwardURL = webmockResponse.getForwardedUrl();
      assertTrue(View.PLACEHOLDERSOURCEVIEW.equals(forwardURL));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }
}
