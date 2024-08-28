/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.servlets;

import static org.junit.Assert.*;

import java.rmi.RemoteException;

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

import com.ericsson.eniq.busyhourcfg.data.vo.TargetTechPack;
import com.ericsson.eniq.busyhourcfg.data.vo.TechPack;
import com.ericsson.eniq.busyhourcfg.database.BusyhourUpdater;
import com.ericsson.eniq.busyhourcfg.database.BusyhourUpdaterFactory;
import com.ericsson.eniq.busyhourcfg.database.RockDatabaseSession;
import com.ericsson.eniq.busyhourcfg.database.TechPackReader;
import com.ericsson.eniq.busyhourcfg.database.TechPackReaderFactory;
import com.ericsson.eniq.busyhourcfg.dwhmanager.DWHStorageTimeAction;
import com.ericsson.eniq.busyhourcfg.exceptions.DatabaseException;

/**
 * @author eheijun
 * 
 */
public class BusyhourViewTest {

  private final Mockery context = new JUnit4Mockery() {

    {
      setImposteriser(ClassImposteriser.INSTANCE);
    }
  };

  private static final String TEST_TP = "TEST:((1))";

  private MockHttpSession webmockSession;

  private RockDatabaseSession mockDatabaseSession;

  private DWHStorageTimeAction mockStorageTimeAction;

  private TechPackReader mockTechPackReader;

  private TechPack mockTechPack;

  private TargetTechPack mockTargetTechPackNew;

  private TargetTechPack mockTargetTechPackOld;

  private BusyhourUpdater mockBusyhourUpdater;

  private MockHttpServletRequest webmockRequest;

  private MockHttpServletResponse webmockResponse;

  @Before
  public void setUp() throws RemoteException {
    webmockSession = new MockHttpSession(null);
    mockDatabaseSession = context.mock(RockDatabaseSession.class);
    mockStorageTimeAction = context.mock(DWHStorageTimeAction.class);
    mockTechPackReader = context.mock(TechPackReader.class);
    mockBusyhourUpdater = context.mock(BusyhourUpdater.class);
    mockTechPack = context.mock(TechPack.class);
    mockTargetTechPackNew = context.mock(TargetTechPack.class, "NEW");
    mockTargetTechPackOld = context.mock(TargetTechPack.class, "OLD");
    webmockSession.setAttribute("databasesession", mockDatabaseSession);
    webmockSession.setAttribute("dwhstoragetimeaction", mockStorageTimeAction);
    webmockRequest = new MockHttpServletRequest("GET", View.BUSYHOURVIEW);
    webmockRequest.setSession(webmockSession);
    webmockResponse = new MockHttpServletResponse();
    TechPackReaderFactory.setReader(mockTechPackReader);
    BusyhourUpdaterFactory.setUpdater(mockBusyhourUpdater);
    try {
      context.checking(new Expectations() {

        {
          allowing(mockTechPackReader).getTechPackByVersionId(with(any(String.class)));
          will(returnValue(mockTechPack));
          allowing(mockTechPack).getVersionId();
          will(returnValue(TEST_TP));
          allowing(mockTechPack).getTargetTechPackByVersionId(with(any(String.class)));
          will(returnValue(mockTargetTechPackNew));
          allowing(mockBusyhourUpdater).recreateViews(with(any(TechPack.class)));
        }
      });
    } catch (DatabaseException e) {
      fail(e.getMessage());
    }
  }

  @After
  public void tearDown() throws RemoteException {
    TechPackReaderFactory.setReader(null);
    BusyhourUpdaterFactory.setUpdater(null);
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.servlets.BusyhourView#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
   * .
   */
  @Test
  public void testServiceHttpServletRequestHttpServletResponseWithCancel() {
    webmockRequest.setParameter("cancel", "true");
    webmockSession.setAttribute("sourcetp", mockTechPack);
    webmockSession.setAttribute("targettp", mockTargetTechPackOld);
    final BusyhourView bv = new BusyhourView();
    try {
      bv.service(webmockRequest, webmockResponse);
      final String redirectURL = webmockResponse.getRedirectedUrl();
      assertTrue(View.TECHPACKVIEW.equals(redirectURL));
      assertTrue(webmockSession.getAttribute("targettp") == mockTargetTechPackOld);
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.servlets.BusyhourView#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
   * .
   */
  @Test
  public void testServiceHttpServletRequestHttpServletResponseWithSelect() {
    webmockRequest.setParameter("sourcetp", TEST_TP);
    webmockSession.setAttribute("targettp", mockTargetTechPackOld);
    final BusyhourView bv = new BusyhourView();
    try {
      bv.service(webmockRequest, webmockResponse);
      final String forwardURL = webmockResponse.getForwardedUrl();
      assertTrue(View.BUSYHOUR_VIEW_JSP.equals(forwardURL));
      assertTrue(webmockSession.getAttribute("targettp") == mockTargetTechPackNew);
      assertTrue(webmockSession.getAttribute("bhsupport") == null);
      assertTrue(webmockSession.getAttribute("placeholder") == null);
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.servlets.BusyhourView#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
   * .
   */
  @Test
  public void testServiceHttpServletRequestHttpServletResponseWithActivate() {
    webmockSession.setAttribute("sourcetp", mockTechPack);
    webmockSession.setAttribute("targettp", mockTargetTechPackOld);
    webmockSession.setAttribute("versionid", TEST_TP);
    webmockRequest.setParameter("activate", "true");
    final BusyhourView bv = new BusyhourView();
    try {
      bv.service(webmockRequest, webmockResponse);
      final String forwardURL = webmockResponse.getForwardedUrl();
      assertTrue(View.ACTIVATIONSTART.equals(forwardURL));
//      final String redirectURL = webmockResponse.getRedirectedUrl();
//      final String expectedResult = View.BUSYHOURVIEW + "?infomessage=Recreation of the criteria successfully done.";
//      assertTrue(expectedResult.equals(redirectURL));
      assertTrue(webmockSession.getAttribute("targettp") == mockTargetTechPackNew);
      assertTrue(webmockSession.getAttribute("bhsupport") == null);
      assertTrue(webmockSession.getAttribute("placeholder") == null);
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }
}
