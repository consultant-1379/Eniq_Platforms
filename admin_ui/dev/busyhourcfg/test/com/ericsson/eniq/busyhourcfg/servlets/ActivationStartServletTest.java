/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.servlets;

import static org.junit.Assert.*;

import java.rmi.RemoteException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

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
import com.ericsson.eniq.busyhourcfg.data.vo.TechPack;
import com.ericsson.eniq.busyhourcfg.database.ActivationAction;
import com.ericsson.eniq.busyhourcfg.database.RockDatabaseSession;
import com.ericsson.eniq.busyhourcfg.database.UpdateResult;
import com.ericsson.eniq.busyhourcfg.exceptions.BusyhourConfigurationException;
import com.ericsson.eniq.repository.ETLCServerProperties;


/**
 * @author eheijun
 *
 */
public class ActivationStartServletTest {

  private final Mockery context = new JUnit4Mockery() {

    {
      setImposteriser(ClassImposteriser.INSTANCE);
    }
  };

  private MockHttpSession webmockSession;

  private BusyhourEnvironment mockBusyhourEnvironment;

  private TechPack mockTechPack;
  
  private ExecutorService mockExecutorService;
  
  private Future<UpdateResult> mockFuture;
  
  private RockDatabaseSession mockDatabaseSession;

  private BusyhourConfiguration mockBusyhourConfiguration;

  /**
   * @throws java.lang.Exception
   */
  
  @SuppressWarnings("unchecked")
  @Before
  public void setUp() throws RemoteException {
    webmockSession = new MockHttpSession(null);
    mockDatabaseSession = context.mock(RockDatabaseSession.class);
    mockBusyhourEnvironment = context.mock(BusyhourEnvironment.class);
    mockBusyhourConfiguration = context.mock(BusyhourConfiguration.class);
    mockTechPack = context.mock(TechPack.class);
    mockExecutorService = context.mock(ExecutorService.class);
    mockFuture = context.mock(Future.class);
    webmockSession.setAttribute("databasesession", mockDatabaseSession);
    
    final ETLCServerProperties mockETLCServerProperties = context.mock(ETLCServerProperties.class);
    final BusyhourProperties mockBusyhourProperties = context.mock(BusyhourProperties.class);
    
    try {
      context.checking(new Expectations() {

        {
          allowing(mockExecutorService).submit(with(any(ActivationAction.class)));
          will(returnValue(mockFuture));
          allowing(mockBusyhourConfiguration).getETLCServerProperties(with(any(ServletContext.class)), with(any(BusyhourEnvironment.class)));
          will(returnValue(mockETLCServerProperties));
          allowing(mockBusyhourConfiguration).getBusyhourProperties(with(any(ServletContext.class)), with(any(BusyhourEnvironment.class)));
          will(returnValue(mockBusyhourProperties));
        }
      });
    } catch (BusyhourConfigurationException e) {
      fail(e.getMessage());
    }
    
    BusyhourEnvironmentFactory.setBusyhourEnvironment(mockBusyhourEnvironment);
    BusyhourConfigurationFactory.setBusyhourConfiguration(mockBusyhourConfiguration);
    
  }

  /**
   * Test method for {@link com.ericsson.eniq.busyhourcfg.servlets.ActivationStartServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
   */
  @Test
  public void testServiceHttpServletRequestHttpServletResponse() {
    final MockHttpServletRequest webmockRequest = new MockHttpServletRequest("GET", View.ACTIVATIONSTART);
    
    webmockRequest.setSession(webmockSession);
    webmockSession.setAttribute("sourcetp", mockTechPack);
    webmockSession.getServletContext().setAttribute("ExecutorService", mockExecutorService);
    
    final MockHttpServletResponse webmockResponse = new MockHttpServletResponse();
    final ActivationStartServlet as = new ActivationStartServlet();
    try {
      as.service(webmockRequest, webmockResponse);
      final String redirectURL = webmockResponse.getRedirectedUrl();
      assertTrue(View.ACTIVATIONSTATUSVIEW.equals(redirectURL));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

}
