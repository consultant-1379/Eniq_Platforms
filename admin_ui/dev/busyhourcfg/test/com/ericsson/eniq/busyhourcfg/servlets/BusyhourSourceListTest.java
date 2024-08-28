/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.servlets;

import static org.junit.Assert.*;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

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

import com.ericsson.eniq.busyhourcfg.database.RockDatabaseSession;
import com.ericsson.eniq.busyhourcfg.database.TechPackReader;
import com.ericsson.eniq.busyhourcfg.database.TechPackReaderFactory;

/**
 * @author eheijun
 * 
 */
public class BusyhourSourceListTest {

  private static final String TEST_BASETABLE_2 = "TEST_MEASUREMENT_COUNT";

  private static final String TEST_BASETABLE_1 = "TEST_MEASUREMENT_RAW";

  private static final String TEST_TP_2 = "XEST:((1))";

  private static final String TEST_TP_1 = "TEST:((1))";

  private final Mockery context = new JUnit4Mockery() {

    {
      setImposteriser(ClassImposteriser.INSTANCE);
    }
  };

  private MockHttpSession webmockSession;

  private RockDatabaseSession mockDatabaseSession;

  private TechPackReader mockTechPackReader;

  private MockHttpServletRequest webmockRequest;

  private MockHttpServletResponse webmockResponse;

  @Before
  public void setUp() throws RemoteException {
    webmockSession = new MockHttpSession(null);
    mockDatabaseSession = context.mock(RockDatabaseSession.class);
    webmockSession.setAttribute("databasesession", mockDatabaseSession);
    mockTechPackReader = context.mock(TechPackReader.class);
    webmockRequest = new MockHttpServletRequest("GET", "/ajaxservlet/BusyhourSourceList");
    webmockRequest.setSession(webmockSession);
    webmockResponse = new MockHttpServletResponse();
    final List<String> mockTechPackList = new ArrayList<String>();
    mockTechPackList.add(TEST_TP_1);
    mockTechPackList.add(TEST_TP_2);
    final List<String> mockBasetableList = new ArrayList<String>();
    mockBasetableList.add(TEST_BASETABLE_1);
    mockBasetableList.add(TEST_BASETABLE_2);
    TechPackReaderFactory.setReader(mockTechPackReader);
    context.checking(new Expectations() {

      {
        allowing(mockTechPackReader).getAllBusyhourSourceTechpacks();
        will(returnValue(mockTechPackList));
        allowing(mockTechPackReader).getAllBusyhourBasetables(with(any(String.class)));
        will(returnValue(mockBasetableList));
      }
    });
  }

  @After
  public void tearDown() throws RemoteException {
    TechPackReaderFactory.setReader(null);
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.servlets.BusyhourSourceList#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
   * .
   */
  @Test
  public void testServiceHttpServletRequestHttpServletResponseGetTechPacks() {
    webmockRequest.setParameter("getSourceTechpacks", "true");
    final BusyhourSourceList bsl = new BusyhourSourceList();
    try {
      final String expectedValue = "({\n\"name\": [\n\"" + TEST_TP_1 + "\",\n\"" + TEST_TP_2 + "\"]\n})\r\n";
      bsl.service(webmockRequest, webmockResponse);
      final String content = webmockResponse.getContentAsString();
      assertEquals(expectedValue, content);
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.servlets.BusyhourSourceList#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
   * .
   */
  @Test
  public void testServiceHttpServletRequestHttpServletResponseGetBasetables() {
    webmockRequest.setParameter("getSourceBasetables", TEST_TP_1);
    final BusyhourSourceList bsl = new BusyhourSourceList();
    try {
      final String expectedValue = "({\n\"name\": [\n\"" + TEST_BASETABLE_1 + "\",\n\"" + TEST_BASETABLE_2
          + "\"]\n})\r\n";
      bsl.service(webmockRequest, webmockResponse);
      final String content = webmockResponse.getContentAsString();
      assertEquals(expectedValue, content);
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.servlets.BusyhourSourceList#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
   * .
   */
  @Test
  public void testServiceHttpServletRequestHttpServletResponseFail() {
    final BusyhourSourceList bsl = new BusyhourSourceList();
    try {
      final String expectedValue = "({\n\"name\": [\n\"\",]\n})\r\n";
      bsl.service(webmockRequest, webmockResponse);
      final String content = webmockResponse.getContentAsString();
      assertEquals(expectedValue, content);
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }
}
