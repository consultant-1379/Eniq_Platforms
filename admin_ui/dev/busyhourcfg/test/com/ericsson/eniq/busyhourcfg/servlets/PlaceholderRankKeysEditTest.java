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

import com.ericsson.eniq.busyhourcfg.data.vo.Key;
import com.ericsson.eniq.busyhourcfg.data.vo.Placeholder;
import com.ericsson.eniq.busyhourcfg.database.RockDatabaseSession;

/**
 * @author eheijun
 * 
 */
public class PlaceholderRankKeysEditTest {

  private static final String TESTKEY1 = "TESTKEY1";

  private static final String TESTKEY2 = "TESTKEY2";

  private static final String TESTSOURCE1 = "TESTSOURCE1";

  private static final String TESTSOURCE2 = "TESTSOURCE2";

  private static final String TESTVALUE1 = "TESTVALUE1";

  private static final String TESTVALUE2 = "TESTVALUE2";

  private final Mockery context = new JUnit4Mockery() {

    {
      setImposteriser(ClassImposteriser.INSTANCE);
    }
  };

  private HttpSession webmockSession;

  private RockDatabaseSession mockDatabaseSession;

  private MockHttpServletRequest webmockRequest;

  private MockHttpServletResponse webmockResponse;

  private Placeholder mockPlaceholder;

  private List<Key> mockKeys;

  private List<Key> mockRemovedKeys;

  private Key mockKey1;

  private Key mockKey2;

  @Before
  public void setUp() throws RemoteException {
    webmockSession = new MockHttpSession(null);
    mockDatabaseSession = context.mock(RockDatabaseSession.class);
    mockPlaceholder = context.mock(Placeholder.class);
    mockKey1 = context.mock(Key.class, "0");
    mockKey2 = context.mock(Key.class, "1");
    mockKeys = new ArrayList<Key>();
    mockKeys.add(mockKey1);
    mockKeys.add(mockKey2);
    mockRemovedKeys = new ArrayList<Key>();
    context.checking(new Expectations() {

      {
        allowing(mockPlaceholder).getKeys();
        will(returnValue(mockKeys));
        allowing(mockPlaceholder).getRemovedKeys();
        will(returnValue(mockRemovedKeys));
        allowing(mockPlaceholder).setKeys(mockKeys);
        allowing(mockPlaceholder).addKey(with(any(String.class)), with(any(String.class)), with(any(String.class)));
        allowing(mockPlaceholder).removeKey(with(any(Key.class)));
        allowing(mockPlaceholder).undoKeyChanges();
        allowing(mockKey1).getKeyName();
        will(returnValue(TESTKEY1));
        allowing(mockKey1).getSource();
        will(returnValue(TESTSOURCE1));
        allowing(mockKey1).getKeyValue();
        will(returnValue(TESTVALUE1));
        allowing(mockKey1).setKeyName(with(any(String.class)));
        allowing(mockKey1).setKeyValue(with(any(String.class)));
        allowing(mockKey1).setSource(with(any(String.class)));
        allowing(mockKey2).getKeyName();
        will(returnValue(TESTKEY2));
        allowing(mockKey2).getSource();
        will(returnValue(TESTSOURCE2));
        allowing(mockKey2).getKeyValue();
        will(returnValue(TESTVALUE2));
        allowing(mockKey2).setKeyName(with(any(String.class)));
        allowing(mockKey2).setKeyValue(with(any(String.class)));
        allowing(mockKey2).setSource(with(any(String.class)));
      }
    });
    webmockSession.setAttribute("databasesession", mockDatabaseSession);
    webmockSession.setAttribute("currentPlaceholder", mockPlaceholder);
    webmockRequest = new MockHttpServletRequest("GET", "/servlet/PlaceholderRankKeysEdit");
    webmockRequest.setParameter("keyname.0", mockKey1.getKeyName());
    webmockRequest.setParameter("source.0", mockKey1.getSource());
    webmockRequest.setParameter("keyvalue.0", mockKey1.getKeyValue());
    webmockRequest.setParameter("keyname.1", mockKey2.getKeyName());
    webmockRequest.setParameter("source.1", mockKey2.getSource());
    webmockRequest.setParameter("keyvalue.1", mockKey2.getKeyValue());
    webmockRequest.setSession(webmockSession);
    webmockResponse = new MockHttpServletResponse();
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.servlets.PlaceholderRankKeysEdit#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
   * .
   */
  @Test
  public void testDoPostHttpServletRequestHttpServletResponseWithCancel() {
    webmockRequest.setParameter("cancel", "true");
    final PlaceholderRankKeysEdit pme = new PlaceholderRankKeysEdit();
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
   * {@link com.ericsson.eniq.busyhourcfg.servlets.PlaceholderRankKeysEdit#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
   * .
   */
  @Test
  public void testDoPostHttpServletRequestHttpServletResponseWithInvalidParams() {
    final PlaceholderRankKeysEdit pme = new PlaceholderRankKeysEdit();
    try {
      pme.doPost(webmockRequest, webmockResponse);
      final String forwardURL = webmockResponse.getForwardedUrl();
      assertTrue(View.PLACEHOLDERRANKKEYS_VIEW_JSP.equals(forwardURL));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.servlets.PlaceholderRankKeysEdit#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
   * .
   */
  @Test
  public void testDoPostHttpServletRequestHttpServletResponseWithSave() {
    webmockRequest.setParameter("save", "true");
    final PlaceholderRankKeysEdit pme = new PlaceholderRankKeysEdit();
    try {
      pme.doPost(webmockRequest, webmockResponse);
      final String forwardURL = webmockResponse.getForwardedUrl();
      assertTrue(View.PLACEHOLDERVIEW.equals(forwardURL));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.servlets.PlaceholderRankKeysEdit#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
   * .
   */
  @Test
  public void testDoPostHttpServletRequestHttpServletResponseWithDelete() {
    webmockRequest.setParameter("delete.1", "true");
    final PlaceholderRankKeysEdit pme = new PlaceholderRankKeysEdit();
    try {
      pme.doPost(webmockRequest, webmockResponse);
      final String redirectURL = webmockResponse.getRedirectedUrl();
      assertTrue(View.PLACEHOLDERRANKKEYSVIEW.equals(redirectURL));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.servlets.PlaceholderRankKeysEdit#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
   * .
   */
  @Test
  public void testDoPostHttpServletRequestHttpServletResponseWithAdd() {
    webmockRequest.setParameter("add", "true");
    final PlaceholderRankKeysEdit pme = new PlaceholderRankKeysEdit();
    try {
      pme.doPost(webmockRequest, webmockResponse);
      final String redirectURL = webmockResponse.getRedirectedUrl();
      assertTrue(View.PLACEHOLDERRANKKEYSVIEW.equals(redirectURL));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.servlets.PlaceholderRankKeysEdit#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
   * .
   */
  @Test
  public void testDoPostHttpServletRequestHttpServletResponseWithEdit() {
    webmockRequest.setParameter("editsourcetable.1", "true");
    final PlaceholderRankKeysEdit pme = new PlaceholderRankKeysEdit();
    try {
      pme.doPost(webmockRequest, webmockResponse);
      final String redirectURL = webmockResponse.getRedirectedUrl();
      assertTrue(View.PLACEHOLDERRANKKEYSOURCEVIEW.equals(redirectURL));
      assertTrue(mockKey2.equals(webmockSession.getAttribute("currentRankKey")));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }
}
