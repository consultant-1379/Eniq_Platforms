/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.config;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.servlet.ServletContext;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.ericsson.eniq.busyhourcfg.exceptions.BusyhourConfigurationException;
import com.ericsson.eniq.repository.ETLCServerProperties;
import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.servletunit.InvocationContext;
import com.meterware.servletunit.ServletRunner;
import com.meterware.servletunit.ServletUnitClient;

/**
 * @author eheijun
 *
 */
public class DefaultBusyhourConfigurationTest {
  
  Mockery context;

  ServletRunner sr;
  
  ServletUnitClient sc = null;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
    context = new JUnit4Mockery();
    try {
      sr = new ServletRunner(new File("web/WEB-INF/web.xml"), "");
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    sc = sr.newClient();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link com.ericsson.eniq.busyhourcfg.config.DefaultBusyhourConfiguration#getBusyhourProperties(javax.servlet.ServletContext)}.
	 */
	@Test
	public void testGetBusyhourProperties() {
	  
    final BusyhourEnvironment be = context.mock(BusyhourEnvironment.class);
    
    context.checking(new Expectations(){
      { 
        allowing(be).getEnvEntryString("conffilefolder"); will (returnValue("busyhourcfg\\conf")); 
        allowing(be).getEnvEntryString("conffile"); will (returnValue("busyhourcfg.properties")); 
      }});
    
    final WebRequest request = new GetMethodWebRequest("http://localhost/busyhourcfg/");
    try {
      final InvocationContext ic = sc.newInvocation(request);
      final ServletContext sc = ic.getRequest().getSession(true).getServletContext();
      final DefaultBusyhourConfiguration busyhourConfiguration = new DefaultBusyhourConfiguration();
      final BusyhourProperties busyhourProperties = busyhourConfiguration.getBusyhourProperties(sc, be);
      assertTrue(busyhourProperties != null);
      assertFalse(busyhourProperties.getProperty("appName").equals(""));
    } catch (MalformedURLException e) {
      fail(e.getMessage()); 
    } catch (IOException e) {
      fail(e.getMessage()); 
    } catch (BusyhourConfigurationException e) {
      fail(e.getMessage()); 
    }
	}

	/**
	 * Test method for {@link com.ericsson.eniq.busyhourcfg.config.DefaultBusyhourConfiguration#getETLCServerProperties(javax.servlet.ServletContext)}.
	 */
	@Test
	public void testGetETLCServerProperties() {
    final BusyhourEnvironment be = context.mock(BusyhourEnvironment.class);
    System.setProperty("CONF_DIR", "C:\\work\\eniq\\conf");
    
    final WebRequest request = new GetMethodWebRequest("http://localhost/busyhourcfg/");
    try {
      final InvocationContext ic = sc.newInvocation(request);
      final ServletContext sc = ic.getRequest().getSession(true).getServletContext();
      final DefaultBusyhourConfiguration busyhourConfiguration = new DefaultBusyhourConfiguration();
      final ETLCServerProperties etlcServerProperties = busyhourConfiguration.getETLCServerProperties(sc, be);
      assertTrue(etlcServerProperties != null);
      assertFalse(etlcServerProperties.getProperty("ENGINE_DB_URL").equals(""));
      assertFalse(etlcServerProperties.getProperty("ENGINE_DB_USERNAME").equals(""));
      assertFalse(etlcServerProperties.getProperty("ENGINE_DB_PASSWORD").equals(""));
      assertFalse(etlcServerProperties.getProperty("ENGINE_DB_DRIVERNAME").equals(""));
    } catch (MalformedURLException e) {
      fail(e.getMessage()); 
    } catch (IOException e) {
      fail(e.getMessage()); 
    } catch (BusyhourConfigurationException e) {
      fail(e.getMessage()); 
    }
	}

}
