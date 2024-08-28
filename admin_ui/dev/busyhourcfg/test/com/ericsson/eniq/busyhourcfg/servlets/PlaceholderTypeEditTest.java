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

import com.ericsson.eniq.busyhourcfg.data.vo.Placeholder;
import com.ericsson.eniq.busyhourcfg.database.RockDatabaseSession;

/**
 * @author eheijun
 * 
 */
public class PlaceholderTypeEditTest {

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

  @Before
  public void setUp() throws RemoteException {
    webmockSession = new MockHttpSession(null);
    mockDatabaseSession = context.mock(RockDatabaseSession.class);
    mockPlaceholder = context.mock(Placeholder.class);
    context.checking(new Expectations() {

      {
        allowing(mockPlaceholder).isTimelimited();
        will(returnValue(false));
        allowing(mockPlaceholder).isSlidingwindow();
        will(returnValue(false));
        allowing(mockPlaceholder).isTimelimitedTimeconsistent();
        will(returnValue(false));
        allowing(mockPlaceholder).isPeakrop();
        will(returnValue(false));
        allowing(mockPlaceholder).isSlidingwindowTimeconsistent();
        will(returnValue(true));
        allowing(mockPlaceholder).setAggregationType(with(any(String.class)));
        allowing(mockPlaceholder).setLookback(with(any(Integer.class)));
        allowing(mockPlaceholder).setPThreshold(with(any(Integer.class)));
        allowing(mockPlaceholder).setNThreshold(with(any(Integer.class)));
        allowing(mockPlaceholder).undoBusyHourTypeChanges();
      }
    });
    webmockSession.setAttribute("databasesession", mockDatabaseSession);
    webmockSession.setAttribute("currentPlaceholder", mockPlaceholder);
    webmockRequest = new MockHttpServletRequest("GET", "/servlet/PlaceholderTypeEdit");
    webmockRequest.setSession(webmockSession);
    webmockResponse = new MockHttpServletResponse();
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.servlets.PlaceholderTypeEdit#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
   * .
   */
  @Test
  public void testDoPostHttpServletRequestHttpServletResponseWithCancel() {
    webmockRequest.setParameter("cancel", "true");
    final PlaceholderTypeEdit pte = new PlaceholderTypeEdit();
    try {
      pte.doPost(webmockRequest, webmockResponse);
      final String redirectURL = webmockResponse.getRedirectedUrl();
      assertTrue(View.PLACEHOLDERVIEW.equals(redirectURL));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.servlets.PlaceholderTypeEdit#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
   * .
   */
  @Test
  public void testDoPostHttpServletRequestHttpServletResponseWithInvalidParams() {
    webmockRequest.setParameter("type", "");
    webmockRequest.setParameter("lookback", "");
    webmockRequest.setParameter("pthreshold", "");
    webmockRequest.setParameter("nthreshold", "");
    final PlaceholderTypeEdit pte = new PlaceholderTypeEdit();
    try {
      pte.doPost(webmockRequest, webmockResponse);
      final String forwardURL = webmockResponse.getForwardedUrl();
      assertTrue(View.PLACEHOLDERTYPE_VIEW_JSP.equals(forwardURL));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.servlets.PlaceholderTypeEdit#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
   * .
   */
  @Test
  public void testDoPostHttpServletRequestHttpServletResponseWithSave() {
    webmockRequest.setParameter("save", "true");
    webmockRequest.setParameter("type", "RANKBH_TIMECONSISTENT_SLIDINGWINDOW");
    webmockRequest.setParameter("lookback", "15");
    webmockRequest.setParameter("pthreshold", "15");
    webmockRequest.setParameter("nthreshold", "15");
    final PlaceholderTypeEdit pte = new PlaceholderTypeEdit();
    try {
      pte.doPost(webmockRequest, webmockResponse);
      final String forwardURL = webmockResponse.getForwardedUrl();
      assertTrue(View.PLACEHOLDERVIEW.equals(forwardURL));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }
}
