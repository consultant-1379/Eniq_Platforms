/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.servlets;

import static org.junit.Assert.*;

import java.rmi.RemoteException;

import javax.servlet.ServletContext;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import com.ericsson.eniq.busyhourcfg.config.BusyhourConfiguration;
import com.ericsson.eniq.busyhourcfg.config.BusyhourConfigurationFactory;
import com.ericsson.eniq.busyhourcfg.config.BusyhourEnvironment;
import com.ericsson.eniq.busyhourcfg.config.BusyhourEnvironmentFactory;
import com.ericsson.eniq.busyhourcfg.config.BusyhourProperties;
import com.ericsson.eniq.busyhourcfg.database.DatabaseConnector;
import com.ericsson.eniq.busyhourcfg.database.DatabaseConnectorFactory;
import com.ericsson.eniq.busyhourcfg.database.RockDatabaseSession;
import com.ericsson.eniq.busyhourcfg.dwhmanager.DWHStorageTimeAction;
import com.ericsson.eniq.busyhourcfg.dwhmanager.DWHStorageTimeActionFactory;
import com.ericsson.eniq.busyhourcfg.exceptions.BusyhourConfigurationException;
import com.ericsson.eniq.busyhourcfg.exceptions.DatabaseException;
import com.ericsson.eniq.busyhourcfg.licensing.LicenseChecker;
import com.ericsson.eniq.busyhourcfg.licensing.LicenseCheckerFactory;
import com.ericsson.eniq.repository.ETLCServerProperties;

/**
 * @author eheijun
 * 
 */
public class TechpackViewTest {

  private final Mockery context = new JUnit4Mockery() {

    {
      setImposteriser(ClassImposteriser.INSTANCE);
    }
  };

  private MockHttpSession webmockSession;

  private BusyhourEnvironment mockBusyhourEnvironment;
  
  private BusyhourConfiguration mockBusyhourConfiguration;

  private DatabaseConnector mockDatabaseConnector;
  
  private RockDatabaseSession mockDatabaseSession;

  private LicenseChecker mockLicenseCheckerValid;

  private LicenseChecker mockLicenseCheckerInvalid;

  private DWHStorageTimeAction mockDWHStorageTimeAction;

  @Before
  public void setUp() throws RemoteException {
    webmockSession = new MockHttpSession(null);
    mockDatabaseSession = context.mock(RockDatabaseSession.class);
    mockBusyhourEnvironment = context.mock(BusyhourEnvironment.class);
    mockBusyhourConfiguration = context.mock(BusyhourConfiguration.class);
    mockDatabaseConnector = context.mock(DatabaseConnector.class);
    mockDWHStorageTimeAction = context.mock(DWHStorageTimeAction.class);
    
    final ETLCServerProperties mockETLCServerProperties = context.mock(ETLCServerProperties.class);
    final BusyhourProperties mockBusyhourProperties = context.mock(BusyhourProperties.class);
    
    mockLicenseCheckerValid = context.mock(LicenseChecker.class, "Valid");
    mockLicenseCheckerInvalid = context.mock(LicenseChecker.class, "Invalid");
    
    try {
      context.checking(new Expectations() {

        {
          allowing(mockBusyhourConfiguration).getETLCServerProperties(with(any(ServletContext.class)), with(any(BusyhourEnvironment.class)));
          will(returnValue(mockETLCServerProperties));
          allowing(mockBusyhourConfiguration).getBusyhourProperties(with(any(ServletContext.class)), with(any(BusyhourEnvironment.class)));
          will(returnValue(mockBusyhourProperties));
          allowing(mockLicenseCheckerValid).isLicenseValid(with(any(String.class)));
          will(returnValue(true));
          allowing(mockLicenseCheckerInvalid).isLicenseValid(with(any(String.class)));
          will(returnValue(false));
          allowing(mockDatabaseConnector).createDatabaseSession(with(any(String.class)));
          will(returnValue(mockDatabaseSession));
        }
      });
    } catch (BusyhourConfigurationException e) {
      fail(e.getMessage());
    } catch (DatabaseException e) {
      fail(e.getMessage());
    }
    
    BusyhourEnvironmentFactory.setBusyhourEnvironment(mockBusyhourEnvironment);
    BusyhourConfigurationFactory.setBusyhourConfiguration(mockBusyhourConfiguration);
    DatabaseConnectorFactory.setDatabaseConnector(mockDatabaseConnector);
    DWHStorageTimeActionFactory.setDWHStorageTimeAction(mockDWHStorageTimeAction);
    
    LicenseCheckerFactory.setLicenseChecker(mockLicenseCheckerValid);
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.servlets.TechpackView#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
   * .
   */
  @Test
  public void testDoGetHttpServletRequestHttpServletResponseWithDatabaseSession() {
    final MockHttpServletRequest webmockRequest = new MockHttpServletRequest("GET", View.TECHPACKVIEW);
    webmockSession.setAttribute("databasesession", mockDatabaseSession);
    webmockRequest.setSession(webmockSession);
    final MockHttpServletResponse webmockResponse = new MockHttpServletResponse();
    final TechpackView tv = new TechpackView();
    try {
      tv.doGet(webmockRequest, webmockResponse);
      final String forwardURL = webmockResponse.getForwardedUrl();
      assertTrue(View.TECHPACK_VIEW_JSP.equals(forwardURL));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.servlets.TechpackView#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
   * .
   */
  @Test
  public void testDoGetHttpServletRequestHttpServletResponseWithoutDatabaseSession() {
    final MockHttpServletRequest webmockRequest = new MockHttpServletRequest("GET", View.TECHPACKVIEW);
    webmockSession.setAttribute("databasesession", null);
    webmockRequest.setSession(webmockSession);
    webmockRequest.addHeader("User-Agent", "TEST_USER_AGENT");
    final MockHttpServletResponse webmockResponse = new MockHttpServletResponse();
    final TechpackView tv = new TechpackView();
    try {
      tv.doGet(webmockRequest, webmockResponse);
      final String forwardURL = webmockResponse.getForwardedUrl();
      assertTrue(View.TECHPACK_VIEW_JSP.equals(forwardURL));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  
  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.servlets.TechpackView#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
   * .
   */
  @Test
  public void testDoPostHttpServletRequestHttpServletResponseWithoutSelection() {
    final MockHttpServletRequest webmockRequest = new MockHttpServletRequest("POST", View.TECHPACKVIEW);
    webmockSession.setAttribute("databasesession", mockDatabaseSession);
    webmockRequest.setSession(webmockSession);
    final MockHttpServletResponse webmockResponse = new MockHttpServletResponse();
    final TechpackView tv = new TechpackView();
    try {
      tv.doPost(webmockRequest, webmockResponse);
      final String forwardURL = webmockResponse.getForwardedUrl();
      assertTrue(View.TECHPACK_VIEW_JSP.equals(forwardURL));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.servlets.TechpackView#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
   * .
   */
  @Test
  public void testDoPostHttpServletRequestHttpServletResponseWithSelection() {
    final MockHttpServletRequest webmockRequest = new MockHttpServletRequest("POST", View.TECHPACKVIEW);
    webmockSession.setAttribute("databasesession", mockDatabaseSession);
    webmockRequest.setParameter("select_techpacks", "TEST");
    webmockRequest.setSession(webmockSession);
    final MockHttpServletResponse webmockResponse = new MockHttpServletResponse();
    final TechpackView tv = new TechpackView();
    try {
      tv.doPost(webmockRequest, webmockResponse);
      final String redirectURL = webmockResponse.getRedirectedUrl();
      assertTrue(View.BUSYHOURVIEW.equals(redirectURL));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }
}
