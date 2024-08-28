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
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import com.ericsson.eniq.busyhourcfg.data.vo.MappedType;
import com.ericsson.eniq.busyhourcfg.data.vo.Placeholder;
import com.ericsson.eniq.busyhourcfg.database.RockDatabaseSession;

/**
 * @author eheijun
 * 
 */
public class PlaceholderMappedTypesEditTest {

  private final Mockery context = new JUnit4Mockery() {

    {
      setImposteriser(ClassImposteriser.INSTANCE);
    }
  };

  private static final String TESTTARGETTYPE1 = "TESTTARGETTYPE1";

  private static final String TESTTARGETTYPE2 = "TESTTARGETTYPE2";

  private HttpSession webmockSession;

  private RockDatabaseSession mockDatabaseSession;

  private MockHttpServletRequest webmockRequest;

  private MockHttpServletResponse webmockResponse;

  private Placeholder mockPlaceholder;

  private List<MappedType> mockMappedTypes;

  private MappedType mockMappedType1;

  private MappedType mockMappedType2;

  @Before
  public void setUp() throws RemoteException {
    webmockSession = new MockHttpSession(null);
    mockDatabaseSession = context.mock(RockDatabaseSession.class);
    mockPlaceholder = context.mock(Placeholder.class);
    mockMappedType1 = context.mock(MappedType.class, "0");
    mockMappedType2 = context.mock(MappedType.class, "1");
    mockMappedTypes = new ArrayList<MappedType>();
    mockMappedTypes.add(mockMappedType1);
    mockMappedTypes.add(mockMappedType2);
    context.checking(new Expectations() {

      {
        allowing(mockPlaceholder).getMappedTypes();
        will(returnValue(mockMappedTypes));
        allowing(mockPlaceholder).setMappedTypes(mockMappedTypes);
        allowing(mockMappedType1).getBhTargettype();
        will(returnValue(TESTTARGETTYPE1));
        allowing(mockMappedType2).getBhTargettype();
        will(returnValue(TESTTARGETTYPE2));
        allowing(mockMappedType1).setEnabled(with(any(Boolean.class)));
        allowing(mockMappedType2).setEnabled(with(any(Boolean.class)));
        allowing(mockPlaceholder).undoMappedTypeChanges();
      }
    });
    webmockSession.setAttribute("databasesession", mockDatabaseSession);
    webmockSession.setAttribute("currentPlaceholder", mockPlaceholder);
    webmockRequest = new MockHttpServletRequest("GET", "/servlet/PlaceholderMappedTypesEdit");
    webmockRequest.setSession(webmockSession);
    webmockResponse = new MockHttpServletResponse();
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.servlets.PlaceholderMappedTypesEdit#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
   * .
   */
  @Test
  public void testDoPostHttpServletRequestHttpServletResponseWithCancel() {
    webmockRequest.setParameter("cancel", "true");
    final PlaceholderMappedTypesEdit pme = new PlaceholderMappedTypesEdit();
    try {
      pme.doPost(webmockRequest, webmockResponse);
      final String redirectURL = webmockResponse.getRedirectedUrl();
      assertTrue(View.PLACEHOLDERVIEW.equals(redirectURL));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.servlets.PlaceholderMappedTypesEdit#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
   * .
   */
  @Test
  public void testDoPostHttpServletRequestHttpServletResponseWithInvalidParams() {
    final PlaceholderMappedTypesEdit pme = new PlaceholderMappedTypesEdit();
    try {
      pme.doPost(webmockRequest, webmockResponse);
      final String forwardURL = webmockResponse.getForwardedUrl();
      assertTrue(View.PLACEHOLDERMAPPEDTYPES_VIEW_JSP.equals(forwardURL));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.servlets.PlaceholderMappedTypesEdit#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
   * .
   */
  @Test
  public void testDoPostHttpServletRequestHttpServletResponseWithSave() {
    webmockRequest.setParameter("save", "true");
    webmockRequest.setParameter("targettype.0", TESTTARGETTYPE1);
    webmockRequest.setParameter("enabled.0", "unchecked");
    webmockRequest.setParameter("targettype.1", TESTTARGETTYPE2);
    webmockRequest.setParameter("enabled.1", "checked");
    final PlaceholderMappedTypesEdit pme = new PlaceholderMappedTypesEdit();
    try {
      pme.doPost(webmockRequest, webmockResponse);
      final String forwardURL = webmockResponse.getForwardedUrl();
      assertTrue(View.PLACEHOLDERVIEW.equals(forwardURL));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }
}
