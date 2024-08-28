/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.servlets;

import static org.junit.Assert.*;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import com.ericsson.eniq.busyhourcfg.database.UpdateResult;


/**
 * @author eheijun
 *
 */
public class ActivationStatusViewTest {

  private final Mockery context = new JUnit4Mockery() {

    {
      setImposteriser(ClassImposteriser.INSTANCE);
    }
  };

  private MockHttpSession webmockSession;

  private Future<UpdateResult> mockFuture;
  
  private UpdateResult mockUpdateResult;

  /**
   * @throws java.lang.Exception
   */
  @SuppressWarnings("unchecked")
  @Before
  public void setUp() throws Exception {
    webmockSession = new MockHttpSession(null);
    mockFuture = context.mock(Future.class);
    mockUpdateResult = context.mock(UpdateResult.class);
  }

  /**
   * Test method for {@link com.ericsson.eniq.busyhourcfg.servlets.ActivationStatusView#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
   */
  @Test
  public void testServiceHttpServletRequestHttpServletResponseProcessReady() {

    try {
      context.checking(new Expectations() {

        {
          allowing(mockFuture).isDone();
          will(returnValue(true));
          allowing(mockFuture).get();
          will(returnValue(mockUpdateResult));
          allowing(mockUpdateResult).isInfo();
          will(returnValue(true));
        }
      });
    } catch (InterruptedException e) {
      fail(e.getMessage());
    } catch (ExecutionException e) {
      fail(e.getMessage());
    }
    
    final MockHttpServletRequest webmockRequest = new MockHttpServletRequest("GET", View.ACTIVATIONSTATUSVIEW);
    
    webmockRequest.setSession(webmockSession);
    
    webmockSession.setAttribute(ActivationStatusView.ACTION_FUTURE, mockFuture);
    
    final MockHttpServletResponse webmockResponse = new MockHttpServletResponse();
    final ActivationStatusView as = new ActivationStatusView();
    try {
      as.service(webmockRequest, webmockResponse);
      final String redirectURL = webmockResponse.getRedirectedUrl();
      assertTrue(redirectURL.startsWith("BusyhourView?infomessage="));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  /**
   * Test method for {@link com.ericsson.eniq.busyhourcfg.servlets.ActivationStatusView#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
   */
  @Test
  public void testServiceHttpServletRequestHttpServletResponseProcessNotReady() {

    try {
      context.checking(new Expectations() {

        {
          allowing(mockFuture).isDone();
          will(returnValue(false));
          allowing(mockFuture).get();
          will(returnValue(mockUpdateResult));
          allowing(mockUpdateResult).isInfo();
          will(returnValue(true));
        }
      });
    } catch (InterruptedException e) {
      fail(e.getMessage());
    } catch (ExecutionException e) {
      fail(e.getMessage());
    }
    
    final MockHttpServletRequest webmockRequest = new MockHttpServletRequest("GET", View.ACTIVATIONSTATUSVIEW);
    
    webmockRequest.setSession(webmockSession);
    
    webmockSession.setAttribute(ActivationStatusView.ACTION_FUTURE, mockFuture);
    
    final MockHttpServletResponse webmockResponse = new MockHttpServletResponse();
    final ActivationStatusView as = new ActivationStatusView();
    try {
      as.service(webmockRequest, webmockResponse);
      final String forwardURL = webmockResponse.getForwardedUrl();
      assertTrue(View.ACTIVATIONSTATUS_VIEW_JSP.equals(forwardURL));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  /**
   * Test method for {@link com.ericsson.eniq.busyhourcfg.servlets.ActivationStatusView#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
   */
  @Test
  public void testServiceHttpServletRequestHttpServletResponseProcessNotStarted() {

    final MockHttpServletRequest webmockRequest = new MockHttpServletRequest("GET", View.ACTIVATIONSTATUSVIEW);
    
    webmockRequest.setSession(webmockSession);
    
    webmockSession.setAttribute(ActivationStatusView.ACTION_FUTURE, null);
    
    final MockHttpServletResponse webmockResponse = new MockHttpServletResponse();
    final ActivationStatusView as = new ActivationStatusView();
    try {
      as.service(webmockRequest, webmockResponse);
      final String redirectURL = webmockResponse.getRedirectedUrl();
      assertTrue(View.BUSYHOURVIEW.equals(redirectURL));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }
}
