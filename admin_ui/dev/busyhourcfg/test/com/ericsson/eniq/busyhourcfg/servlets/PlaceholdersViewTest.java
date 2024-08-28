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
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import com.ericsson.eniq.busyhourcfg.data.vo.BusyhourSupport;
import com.ericsson.eniq.busyhourcfg.data.vo.TargetTechPack;
import com.ericsson.eniq.busyhourcfg.data.vo.TechPack;
import com.ericsson.eniq.busyhourcfg.database.RockDatabaseSession;

/**
 * @author eheijun
 * 
 */
public class PlaceholdersViewTest {

  private final Mockery context = new JUnit4Mockery() {

    {
      setImposteriser(ClassImposteriser.INSTANCE);
    }
  };

  private static final String TEST_TP = "TEST:((1))";

  private static final String TEST_BHSUPPORT = "TEST_BHSUPPORT";

  private MockHttpSession webmockSession;

  private RockDatabaseSession mockDatabaseSession;

  private TechPack mockTechPack;

  private TargetTechPack mockTargetTechPack;

  private BusyhourSupport mockBusyhourSupportNew;

  private BusyhourSupport mockBusyhourSupportOld;

  private MockHttpServletRequest webmockRequest;

  private MockHttpServletResponse webmockResponse;

  @Before
  public void setUp() throws RemoteException {
    webmockSession = new MockHttpSession(null);
    mockDatabaseSession = context.mock(RockDatabaseSession.class);
    mockTechPack = context.mock(TechPack.class);
    mockTargetTechPack = context.mock(TargetTechPack.class);
    mockBusyhourSupportNew = context.mock(BusyhourSupport.class, "NEW");
    mockBusyhourSupportOld = context.mock(BusyhourSupport.class, "OLD");
    context.checking(new Expectations() {

      {
        allowing(mockTargetTechPack).getVersionId();
        will(returnValue(TEST_TP));
        allowing(mockBusyhourSupportNew).getBhlevel();
        will(returnValue(TEST_BHSUPPORT));
        allowing(mockTechPack).getTargetTechPackByVersionId(with(any(String.class)));
        will(returnValue(mockTargetTechPack));
        allowing(mockTargetTechPack).getBusyhourSupportByBhlevel(with(any(String.class)));
        will(returnValue(mockBusyhourSupportNew));
      }
    });
    webmockSession.setAttribute("databasesession", mockDatabaseSession);
    webmockSession.setAttribute("sourcetp", mockTechPack);
    webmockSession.setAttribute("targettp", mockTargetTechPack);
    webmockSession.setAttribute("bhsupport", mockBusyhourSupportOld);
    webmockRequest = new MockHttpServletRequest("GET", "/servlet/PlaceholdersView");
    webmockRequest.setSession(webmockSession);
    webmockResponse = new MockHttpServletResponse();
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.servlets.PlaceholdersView#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
   * .
   */
  @Test
  public void testServiceHttpServletRequestHttpServletResponseWithCancel() {
    webmockRequest.setParameter("cancel", "true");
    final PlaceholdersView pv = new PlaceholdersView();
    try {
      pv.service(webmockRequest, webmockResponse);
      final String redirectURL = webmockResponse.getRedirectedUrl();
      assertTrue(View.BUSYHOURVIEW.equals(redirectURL));
      assertTrue(webmockSession.getAttribute("bhsupport") == mockBusyhourSupportOld);
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.servlets.PlaceholdersView#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
   * .
   */
  @Test
  public void testServiceHttpServletRequestHttpServletResponseWithSelect() {
    webmockRequest.setParameter("targettp", mockTargetTechPack.getVersionId());
    webmockRequest.setParameter("bhsupport", mockBusyhourSupportNew.getBhlevel());
    final PlaceholdersView pv = new PlaceholdersView();
    try {
      pv.service(webmockRequest, webmockResponse);
      final String forwardURL = webmockResponse.getForwardedUrl();
      assertTrue(View.PLACEHOLDERS_VIEW_JSP.equals(forwardURL));
      assertTrue(webmockSession.getAttribute("bhsupport") == mockBusyhourSupportNew);
      assertTrue(webmockSession.getAttribute("placeholder") == null);
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }
}
