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
import com.ericsson.eniq.busyhourcfg.database.TechPackReader;
import com.ericsson.eniq.busyhourcfg.database.TechPackReaderFactory;
import com.ericsson.eniq.busyhourcfg.exceptions.DatabaseException;

/**
 * @author eheijun
 * 
 */
public class PlaceholderEditTest {

  private static final String TEST_GROUPING = "TEST_GROUPING";

  private static final String TEST_CRITERIA = "TEST_CRITERIA";

  private static final String TEST_WHERE = "TEST_WHERE";

  private static final String TEST_DESCRIPTION = "TEST_DESCRIPTION";

  private static final String TEST_TP = "TEST:((1))";
  private static final String TEST_TARGET_TP = "TARGET:((1))";

  private static final String TEST_BHSUPPORT = "TEST_BHSUPPORT";

  private static final String TEST_BHTYPE = "TEST_BHTYPE";
  
  private static final Integer TEST_MODIFIED = 1;

  private final Mockery context = new JUnit4Mockery() {

    {
      setImposteriser(ClassImposteriser.INSTANCE);
    }
  };

  private HttpSession webmockSession;

  private RockDatabaseSession mockDatabaseSession;

  private TechPackReader mockTechPackReader;

  private BusyhourUpdater mockBusyhourUpdater;

  private MockHttpServletRequest webmockRequest;

  private MockHttpServletResponse webmockResponse;

  private Placeholder mockPlaceholder;

  private TechPack mockTechPack;
  private TechPack mockTechPack2;

  private TargetTechPack mockTargetTechPack;

  private BusyhourSupport mockBusyhourSupport;

  @Before
  public void setUp() throws RemoteException {
    webmockSession = new MockHttpSession(null);
    mockDatabaseSession = context.mock(RockDatabaseSession.class);
    mockTechPackReader = context.mock(TechPackReader.class);
    mockBusyhourUpdater = context.mock(BusyhourUpdater.class);
    mockPlaceholder = context.mock(Placeholder.class);
    mockTechPack = context.mock(TechPack.class, TEST_TP);
    mockTechPack2 = context.mock(TechPack.class);
    mockTargetTechPack = context.mock(TargetTechPack.class, TEST_TARGET_TP);
    mockBusyhourSupport = context.mock(BusyhourSupport.class);
    TechPackReaderFactory.setReader(mockTechPackReader);
    BusyhourUpdaterFactory.setUpdater(mockBusyhourUpdater);
    context.checking(new Expectations() {

      {
        try {
          allowing(mockTechPack).getVersionId();
          will(returnValue(TEST_TP));
          allowing(mockTechPackReader).getTechPackByVersionId(with(TEST_TP));
          will(returnValue(mockTechPack));
          allowing(mockTechPack2).getVersionId();
          will(returnValue(TEST_TARGET_TP));
          allowing(mockTechPackReader).getTechPackByVersionId(with(TEST_TARGET_TP));
          will(returnValue(mockTechPack2));
          allowing(mockBusyhourUpdater).validatePlaceholder(with(any(Placeholder.class)));
          allowing(mockBusyhourUpdater).savePlaceholder(with(any(Placeholder.class)));
        } catch (DatabaseException e) {
          fail(e.getMessage());
        }
        allowing(mockTechPack).getModified();
        will(returnValue(TEST_MODIFIED));
        allowing(mockTargetTechPack).getVersionId();
        will(returnValue(TEST_TARGET_TP));
        allowing(mockBusyhourSupport).getBhlevel();
        will(returnValue(TEST_BHSUPPORT));
        allowing(mockBusyhourSupport).getPlaceholderByBhtype(with(any(String.class)));
        will(returnValue(mockPlaceholder));
        allowing(mockPlaceholder).getBhtype();
        will(returnValue(TEST_BHTYPE));
        allowing(mockPlaceholder).setDescription(with(any(String.class)));
        allowing(mockPlaceholder).setWhere(with(any(String.class)));
        allowing(mockPlaceholder).setCriteria(with(any(String.class)));
        allowing(mockPlaceholder).setGrouping(with(any(String.class)));
        allowing(mockPlaceholder).clearAllData();
        allowing(mockPlaceholder).undoAllChanges();
        allowing(mockTechPack).getTargetTechPackByVersionId(with(any(String.class)));
        will(returnValue(mockTargetTechPack));
        allowing(mockTechPack2).getTargetTechPackByVersionId(with(any(String.class)));
        will(returnValue(mockTargetTechPack));
        allowing(mockTechPack2).getModified();
        will(returnValue(1));
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
   * {@link com.ericsson.eniq.busyhourcfg.servlets.PlaceholderEdit#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
   * .
   */
  @Test
  public void testDoPostHttpServletRequestHttpServletResponseWithCancel() {
    webmockSession.setAttribute("sourcetp", mockTechPack);
    webmockSession.setAttribute("targettp", mockTargetTechPack);
    webmockRequest.setParameter("targettp", mockTargetTechPack.getVersionId());
    webmockRequest.setParameter("bhsupport", mockBusyhourSupport.getBhlevel());
    webmockRequest.setParameter("bhtype", mockPlaceholder.getBhtype());
    webmockRequest.setParameter("cancel", "true");
    final PlaceholderEdit placeholderEdit = new PlaceholderEdit();
    try {
      placeholderEdit.doPost(webmockRequest, webmockResponse);
      final String redirectURL = webmockResponse.getRedirectedUrl();
      assertTrue(redirectURL.startsWith(View.PLACEHOLDERSVIEW));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.servlets.PlaceholderEdit#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
   * .
   */
  @Test
  public void testDoPostHttpServletRequestHttpServletResponseWithClear() {
    webmockSession.setAttribute("sourcetp", mockTechPack);
    webmockSession.setAttribute("targettp", mockTargetTechPack);
    webmockRequest.setParameter("targettp", mockTargetTechPack.getVersionId());
    webmockRequest.setParameter("bhsupport", mockBusyhourSupport.getBhlevel());
    webmockRequest.setParameter("bhtype", mockPlaceholder.getBhtype());
    webmockRequest.setParameter("description", TEST_DESCRIPTION);
    webmockRequest.setParameter("where", TEST_WHERE);
    webmockRequest.setParameter("criteria", TEST_CRITERIA);
    webmockRequest.setParameter("grouping", TEST_GROUPING);
    webmockRequest.setParameter("clear", "true");
    final PlaceholderEdit placeholderEdit = new PlaceholderEdit();
    try {
      placeholderEdit.doPost(webmockRequest, webmockResponse);
      final String redirectURL = webmockResponse.getRedirectedUrl();
      assertTrue(redirectURL.startsWith(View.PLACEHOLDERSVIEW));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.servlets.PlaceholderEdit#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
   * .
   */
  @Test
  public void testDoPostHttpServletRequestHttpServletResponseWithSave() {
    webmockSession.setAttribute("sourcetp", mockTechPack);
    webmockSession.setAttribute("targettp", mockTargetTechPack);
    webmockRequest.setParameter("targettp", mockTargetTechPack.getVersionId());
    webmockRequest.setParameter("bhsupport", mockBusyhourSupport.getBhlevel());
    webmockRequest.setParameter("bhtype", mockPlaceholder.getBhtype());
    webmockRequest.setParameter("description", TEST_DESCRIPTION);
    webmockRequest.setParameter("where", TEST_WHERE);
    webmockRequest.setParameter("criteria", TEST_CRITERIA);
    webmockRequest.setParameter("grouping", TEST_GROUPING);
    webmockRequest.setParameter("save", "true");
    final PlaceholderEdit placeholderEdit = new PlaceholderEdit();
    try {
      placeholderEdit.doPost(webmockRequest, webmockResponse);
      final String redirectURL = webmockResponse.getRedirectedUrl();
      assertTrue(redirectURL.startsWith(View.PLACEHOLDERSVIEW));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }
  
  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.servlets.PlaceholderEdit#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
   * .
   */
  @Test
  public void testDoPostVersionIdNotEqualTargetVersionId() {
    webmockSession.setAttribute("sourcetp", mockTechPack);
    webmockSession.setAttribute("targettp", mockTargetTechPack);
    webmockRequest.setParameter("targettp", mockTargetTechPack.getVersionId());
    webmockRequest.setParameter("bhsupport", mockBusyhourSupport.getBhlevel());
    webmockRequest.setParameter("bhtype", mockPlaceholder.getBhtype());
    webmockRequest.setParameter("description", TEST_DESCRIPTION);
    webmockRequest.setParameter("where", TEST_WHERE);
    webmockRequest.setParameter("criteria", TEST_CRITERIA);
    webmockRequest.setParameter("grouping", TEST_GROUPING);
    webmockRequest.setParameter("save", "true");
    final PlaceholderEdit placeholderEdit = new PlaceholderEdit();
    try {
      placeholderEdit.doPost(webmockRequest, webmockResponse);
      final String redirectURL = webmockResponse.getRedirectedUrl();
      assertTrue(redirectURL.startsWith(View.PLACEHOLDERSVIEW));
      final TechPack selectedTechPack = (TechPack) webmockSession.getAttribute("sourcetp");
      TargetTechPack currentTargetTechPack = (TargetTechPack) webmockSession.getAttribute("targettp");
      final String sourcetp = selectedTechPack.getVersionId();
      final String targettp = currentTargetTechPack.getVersionId();
      assertFalse("For useful test do not expect TargetVersionId to be equal to VersionId", sourcetp.equals(targettp));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  } //testDoPostVersionIdNotEqualTargetVersionId
}
