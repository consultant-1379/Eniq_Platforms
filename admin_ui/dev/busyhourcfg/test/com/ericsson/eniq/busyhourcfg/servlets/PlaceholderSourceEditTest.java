/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.servlets;

import static org.junit.Assert.*;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import com.ericsson.eniq.busyhourcfg.data.vo.Placeholder;
import com.ericsson.eniq.busyhourcfg.data.vo.Source;
import com.ericsson.eniq.busyhourcfg.database.BusyhourUpdater;
import com.ericsson.eniq.busyhourcfg.database.BusyhourUpdaterFactory;
import com.ericsson.eniq.busyhourcfg.database.RockDatabaseSession;
import com.ericsson.eniq.busyhourcfg.exceptions.DatabaseException;

/**
 * @author eheijun
 * 
 */
public class PlaceholderSourceEditTest {

  private final Mockery context = new JUnit4Mockery() {

    {
      setImposteriser(ClassImposteriser.INSTANCE);
    }
  };

  private HttpSession webmockSession;

  private RockDatabaseSession mockDatabaseSession;

  private BusyhourUpdater mockBusyhourUpdater;

  private MockHttpServletRequest webmockRequest;

  private MockHttpServletResponse webmockResponse;

  private Placeholder mockPlaceholder;

  private List<Source> mockSources;

  private Source mockSource1;

  private Source mockSource2;

  @Before
  public void setUp() throws RemoteException {
    webmockSession = new MockHttpSession(null);
    mockDatabaseSession = context.mock(RockDatabaseSession.class);
    mockBusyhourUpdater = context.mock(BusyhourUpdater.class);
    mockPlaceholder = context.mock(Placeholder.class);
    mockSource1 = context.mock(Source.class, "0");
    mockSource2 = context.mock(Source.class, "1");
    mockSources = new ArrayList<Source>();
    mockSources.add(mockSource1);
    mockSources.add(mockSource2);
    BusyhourUpdaterFactory.setUpdater(mockBusyhourUpdater);
    context.checking(new Expectations() {

      {
        try {
          allowing(mockBusyhourUpdater).generateBusyhourrankkeys(with(any(Placeholder.class)));
        } catch (DatabaseException e) {
          fail(e.getMessage());
        }
        allowing(mockPlaceholder).getSources();
        will(returnValue(mockSources));
        allowing(mockPlaceholder).setSources(mockSources);
        allowing(mockPlaceholder).addSource(with(any(String.class)));
        allowing(mockPlaceholder).removeSource(with(any(Source.class)));
        allowing(mockPlaceholder).undoSourceChanges();
        allowing(mockSource1).getTypename();
        will(returnValue("TESTTYPENAME1"));
        allowing(mockSource1).setTypename(with(any(String.class)));
        allowing(mockSource2).getTypename();
        will(returnValue("TESTTYPENAME2"));
        allowing(mockSource2).setTypename(with(any(String.class)));
      }
    });
    webmockSession.setAttribute("databasesession", mockDatabaseSession);
    webmockSession.setAttribute("currentPlaceholder", mockPlaceholder);
    webmockRequest = new MockHttpServletRequest("GET", "/servlet/PlaceholderSourceEdit");
    webmockRequest.setParameter("typename.0", mockSource1.getTypename());
    webmockRequest.setParameter("typename.1", mockSource2.getTypename());
    webmockRequest.setSession(webmockSession);
    webmockResponse = new MockHttpServletResponse();
  }

  @After
  public void tearDown() throws RemoteException {
    BusyhourUpdaterFactory.setUpdater(null);
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.servlets.PlaceholderTypeEdit#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
   * .
   */
  @Test
  public void testDoPostHttpServletRequestHttpServletResponseWithCancel() {
    webmockRequest.setParameter("cancel", "true");
    final PlaceholderSourceEdit pse = new PlaceholderSourceEdit();
    try {
      pse.doPost(webmockRequest, webmockResponse);
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
    final PlaceholderSourceEdit pse = new PlaceholderSourceEdit();
    try {
      pse.doPost(webmockRequest, webmockResponse);
      final String redirectURL = webmockResponse.getRedirectedUrl();
      assertTrue(View.PLACEHOLDERSOURCEVIEW.equals(redirectURL));
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
    final PlaceholderSourceEdit pse = new PlaceholderSourceEdit();
    try {
      pse.doPost(webmockRequest, webmockResponse);
      final String forwardURL = webmockResponse.getForwardedUrl();
      assertTrue(View.PLACEHOLDERVIEW.equals(forwardURL));
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
  public void testDoPostHttpServletRequestHttpServletResponseWithDelete() {
    webmockRequest.setParameter("delete.1", "true");
    final PlaceholderSourceEdit pse = new PlaceholderSourceEdit();
    try {
      pse.doPost(webmockRequest, webmockResponse);
      final String forwardURL = webmockResponse.getForwardedUrl();
      assertTrue(View.PLACEHOLDERSOURCEVIEW.equals(forwardURL));
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
  public void testDoPostHttpServletRequestHttpServletResponseWithAdd() {
    webmockRequest.setParameter("add", "true");
    final PlaceholderSourceEdit pse = new PlaceholderSourceEdit();
    try {
      pse.doPost(webmockRequest, webmockResponse);
      final String forwardURL = webmockResponse.getForwardedUrl();
      assertTrue(View.PLACEHOLDERSOURCEVIEW.equals(forwardURL));
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
  public void testDoPostHttpServletRequestHttpServletResponseWithEdit() {
    webmockRequest.setParameter("edittypename.1", "true");
    final PlaceholderSourceEdit pse = new PlaceholderSourceEdit();
    try {
      pse.doPost(webmockRequest, webmockResponse);
      final String redirectURL = webmockResponse.getRedirectedUrl();
      assertTrue(View.PLACEHOLDERSOURCEBROWSEVIEW.equals(redirectURL));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }
}
