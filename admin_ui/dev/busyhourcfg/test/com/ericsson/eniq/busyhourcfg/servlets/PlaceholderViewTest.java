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
import com.ericsson.eniq.busyhourcfg.data.vo.Placeholder;
import com.ericsson.eniq.busyhourcfg.data.vo.TargetTechPack;
import com.ericsson.eniq.busyhourcfg.data.vo.TechPack;
import com.ericsson.eniq.busyhourcfg.database.RockDatabaseSession;

public class PlaceholderViewTest {

  private final Mockery context = new JUnit4Mockery() {

    {
      setImposteriser(ClassImposteriser.INSTANCE);
    }
  };

  private static final String TEST_TP = "TEST:((1))";

  private static final String TEST_BHSUPPORT = "TEST_BHSUPPORT";

  private static final String TEST_BHTYPE = "PP0";

  private MockHttpSession webmockSession;

  private RockDatabaseSession mockDatabaseSession;

  private TechPack mockTechPack;

  private TargetTechPack mockTargetTechPack;

  private BusyhourSupport mockBusyhourSupport;

  private Placeholder mockPlaceholderNew;

  private Placeholder mockPlaceholderOld;

  private MockHttpServletRequest webmockRequest;

  private MockHttpServletResponse webmockResponse;

  @Before
  public void setUp() throws RemoteException {
    webmockSession = new MockHttpSession(null);
    mockDatabaseSession = context.mock(RockDatabaseSession.class);
    mockTechPack = context.mock(TechPack.class);
    mockTargetTechPack = context.mock(TargetTechPack.class);
    mockBusyhourSupport = context.mock(BusyhourSupport.class);
    mockPlaceholderNew = context.mock(Placeholder.class, "NEW");
    mockPlaceholderOld = context.mock(Placeholder.class, "OLD");
    context.checking(new Expectations() {

      {
        allowing(mockTargetTechPack).getVersionId();
        will(returnValue(TEST_TP));
        allowing(mockBusyhourSupport).getBhlevel();
        will(returnValue(TEST_BHSUPPORT));
        allowing(mockPlaceholderNew).getBhtype();
        will(returnValue(TEST_BHTYPE));
        allowing(mockTechPack).getTargetTechPackByVersionId(with(any(String.class)));
        will(returnValue(mockTargetTechPack));
        allowing(mockTargetTechPack).getBusyhourSupportByBhlevel(with(any(String.class)));
        will(returnValue(mockBusyhourSupport));
        allowing(mockBusyhourSupport).getPlaceholderByBhtype(with(any(String.class)));
        will(returnValue(mockPlaceholderNew));
      }
    });
    webmockSession.setAttribute("databasesession", mockDatabaseSession);
    webmockSession.setAttribute("sourcetp", mockTechPack);
    webmockSession.setAttribute("targettp", mockTargetTechPack);
    webmockSession.setAttribute("placeholder", mockPlaceholderOld);
    webmockRequest = new MockHttpServletRequest("GET", "/servlet/PlaceholderView");
    webmockRequest.setParameter("targettp", mockTargetTechPack.getVersionId());
    webmockRequest.setParameter("bhsupport", mockBusyhourSupport.getBhlevel());
    webmockRequest.setParameter("bhtype", mockPlaceholderNew.getBhtype());
    webmockRequest.setSession(webmockSession);
    webmockResponse = new MockHttpServletResponse();
  }

  @Test
  public void testServiceHttpServletRequestHttpServletResponse() {
    final PlaceholderView pv = new PlaceholderView();
    try {
      pv.service(webmockRequest, webmockResponse);
      final String forwardURL = webmockResponse.getForwardedUrl();
      assertTrue(View.PLACEHOLDER_VIEW_JSP.equals(forwardURL));
      assertTrue(webmockSession.getAttribute("placeholder") == mockPlaceholderNew);
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }
}
