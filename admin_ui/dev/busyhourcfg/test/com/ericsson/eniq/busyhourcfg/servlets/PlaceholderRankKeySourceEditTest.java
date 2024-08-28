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

import com.ericsson.eniq.busyhourcfg.data.vo.Key;
import com.ericsson.eniq.busyhourcfg.data.vo.Placeholder;
import com.ericsson.eniq.busyhourcfg.database.RockDatabaseSession;

/**
 * @author eheijun
 * 
 */
public class PlaceholderRankKeySourceEditTest {

  private final Mockery context = new JUnit4Mockery() {

    {
      setImposteriser(ClassImposteriser.INSTANCE);
    }
  };

  private HttpSession webmockSession;

  private RockDatabaseSession mockDatabaseSession;

  private MockHttpServletRequest webmockRequest;

  private MockHttpServletResponse webmockResponse;

  private Placeholder mockPlaceholder;

  private Key mockRankKey;

  @Before
  public void setUp() throws RemoteException {
    webmockSession = new MockHttpSession(null);
    mockDatabaseSession = context.mock(RockDatabaseSession.class);
    mockPlaceholder = context.mock(Placeholder.class);
    mockRankKey = context.mock(Key.class);
    context.checking(new Expectations() {

      {
        allowing(mockRankKey).setSource(with(any(String.class)));
      }
    });
    webmockSession.setAttribute("databasesession", mockDatabaseSession);
    webmockSession.setAttribute("currentPlaceholder", mockPlaceholder);
    webmockSession.setAttribute("currentRankKey", mockRankKey);
    webmockRequest = new MockHttpServletRequest("GET", "/servlet/PlaceholderRankKeySourceEdit");
    webmockRequest.setSession(webmockSession);
    webmockResponse = new MockHttpServletResponse();
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.servlets.PlaceholderRankKeySourceEdit#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
   * .
   */
  @Test
  public void testDoPostHttpServletRequestHttpServletResponseWithCancel() {
    webmockRequest.setParameter("cancel", "true");
    final PlaceholderRankKeySourceEdit prse = new PlaceholderRankKeySourceEdit();
    try {
      prse.doPost(webmockRequest, webmockResponse);
      final String redirectURL = webmockResponse.getRedirectedUrl();
      assertTrue(View.PLACEHOLDERRANKKEYSVIEW.equals(redirectURL));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.servlets.PlaceholderRankKeySourceEdit#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
   * .
   */
  @Test
  public void testDoPostHttpServletRequestHttpServletResponseWithInvalidParams() {
    final PlaceholderRankKeySourceEdit prse = new PlaceholderRankKeySourceEdit();
    try {
      prse.doPost(webmockRequest, webmockResponse);
      final String forwardURL = webmockResponse.getForwardedUrl();
      assertTrue(View.PLACEHOLDERRANKKEYS_VIEW_JSP.equals(forwardURL));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.servlets.PlaceholderRankKeySourceEdit#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
   * .
   */
  @Test
  public void testDoPostHttpServletRequestHttpServletResponseWithSave() {
    webmockRequest.setParameter("save", "true");
    webmockRequest.setParameter("source_table", "TESTSOURCETABLE");
    final PlaceholderRankKeySourceEdit prse = new PlaceholderRankKeySourceEdit();
    try {
      prse.doPost(webmockRequest, webmockResponse);
      final String forwardURL = webmockResponse.getForwardedUrl();
      assertTrue(View.PLACEHOLDERRANKKEYSVIEW.equals(forwardURL));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }
}
