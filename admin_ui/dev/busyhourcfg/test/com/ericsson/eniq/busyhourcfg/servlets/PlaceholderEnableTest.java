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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import com.ericsson.eniq.busyhourcfg.data.vo.BusyhourSupport;
import com.ericsson.eniq.busyhourcfg.data.vo.Placeholder;
import com.ericsson.eniq.busyhourcfg.data.vo.TargetTechPack;
import com.ericsson.eniq.busyhourcfg.data.vo.TechPack;
import com.ericsson.eniq.busyhourcfg.database.BusyhourUpdater;
import com.ericsson.eniq.busyhourcfg.database.BusyhourUpdaterFactory;
import com.ericsson.eniq.busyhourcfg.database.RockDatabaseSession;
import com.ericsson.eniq.busyhourcfg.exceptions.DatabaseException;

/**
 * @author eheijun
 * 
 */
public class PlaceholderEnableTest {

  private static final String TEST_TP = "TEST:((1))";

  private static final String TEST_BHSUPPORT = "TEST_BHSUPPORT";

  private static final String TEST_BHTYPE = "TEST_BHTYPE";

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

  private TechPack mockTechPack;

  private TargetTechPack mockTargetTechPack;

  private BusyhourSupport mockBusyhourSupport;

  @Before
  public void setUp() throws RemoteException {
    webmockSession = new MockHttpSession(null);
    mockDatabaseSession = context.mock(RockDatabaseSession.class);
    mockBusyhourUpdater = context.mock(BusyhourUpdater.class);
    mockPlaceholder = context.mock(Placeholder.class);
    mockTechPack = context.mock(TechPack.class);
    mockTargetTechPack = context.mock(TargetTechPack.class);
    mockBusyhourSupport = context.mock(BusyhourSupport.class);
    BusyhourUpdaterFactory.setUpdater(mockBusyhourUpdater);
    context.checking(new Expectations() {

      {
        try {
          allowing(mockBusyhourUpdater).validatePlaceholder(with(any(Placeholder.class)));
          allowing(mockBusyhourUpdater).savePlaceholder(with(any(Placeholder.class)));
        } catch (DatabaseException e) {
          fail(e.getMessage());
        }
        allowing(mockTargetTechPack).getVersionId();
        will(returnValue(TEST_TP));
        allowing(mockBusyhourSupport).getBhlevel();
        will(returnValue(TEST_BHSUPPORT));
        allowing(mockBusyhourSupport).getPlaceholderByBhtype(with(any(String.class)));
        will(returnValue(mockPlaceholder));
        allowing(mockPlaceholder).getBhtype();
        will(returnValue(TEST_BHTYPE));
        allowing(mockPlaceholder).setEnabled(with(any(Boolean.class)));
        allowing(mockTechPack).getTargetTechPackByVersionId(with(any(String.class)));
        will(returnValue(mockTargetTechPack));
        allowing(mockTargetTechPack).getBusyhourSupportByBhlevel(with(any(String.class)));
        will(returnValue(mockBusyhourSupport));
      }
    });
    webmockSession.setAttribute("databasesession", mockDatabaseSession);
    webmockSession.setAttribute("currentPlaceholder", mockPlaceholder);
    webmockRequest = new MockHttpServletRequest("GET", "/servlet/PlaceholderEnableTest");
    webmockRequest.setSession(webmockSession);
    webmockResponse = new MockHttpServletResponse();
  }

  @After
  public void tearDown() throws RemoteException {
    BusyhourUpdaterFactory.setUpdater(null);
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.servlets.PlaceholderEnable#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
   * .
   */
  @Test
  public void testServiceHttpServletRequestHttpServletResponse() {
    webmockSession.setAttribute("sourcetp", mockTechPack);
    webmockSession.setAttribute("targettp", mockTargetTechPack);
    webmockRequest.setParameter("targettp", mockTargetTechPack.getVersionId());
    webmockRequest.setParameter("bhsupport", mockBusyhourSupport.getBhlevel());
    webmockRequest.setParameter("bhtype", mockPlaceholder.getBhtype());
    webmockRequest.setParameter("enabled", "true");
    final PlaceholderEnable placeholderEnable = new PlaceholderEnable();
    try {
      placeholderEnable.service(webmockRequest, webmockResponse);
      final String redirectURL = webmockResponse.getRedirectedUrl();
      assertTrue(redirectURL.startsWith(View.PLACEHOLDERSVIEW));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }
}
