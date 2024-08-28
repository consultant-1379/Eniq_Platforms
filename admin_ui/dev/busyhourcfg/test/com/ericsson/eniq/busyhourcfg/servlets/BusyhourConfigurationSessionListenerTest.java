/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.servlets;

import static org.junit.Assert.*;

import javax.servlet.http.HttpSessionEvent;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.springframework.mock.web.MockHttpSession;

import com.ericsson.eniq.busyhourcfg.database.DatabaseSession;
import com.ericsson.eniq.busyhourcfg.exceptions.DatabaseException;

/**
 * @author eheijun
 * 
 */
public class BusyhourConfigurationSessionListenerTest {

  private final Mockery context = new JUnit4Mockery() {

    {
      setImposteriser(ClassImposteriser.INSTANCE);
    }
  };

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.servlets.BusyhourConfigurationSessionListener#sessionDestroyed(javax.servlet.http.HttpSessionEvent)}
   * .
   */
  @Test
  public void testSessionDestroyed() {
    final HttpSessionEvent mockEvent = context.mock(HttpSessionEvent.class);
    final DatabaseSession mockDatabaseSession = context.mock(DatabaseSession.class);
    final MockHttpSession webmockSession = new MockHttpSession(null);
    webmockSession.setAttribute("databasesession", mockDatabaseSession);
    try {
      context.checking(new Expectations() {

        {
          allowing(mockEvent).getSession();
          will(returnValue(webmockSession));
          allowing(mockDatabaseSession).clear();
        }
      });
    } catch (DatabaseException e) {
      fail(e.getMessage());
    }
    final BusyhourConfigurationSessionListener listener = new BusyhourConfigurationSessionListener();
    try {
      listener.sessionDestroyed(mockEvent);
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }
}
