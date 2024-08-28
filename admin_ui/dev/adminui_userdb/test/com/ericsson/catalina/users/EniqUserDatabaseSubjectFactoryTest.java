/**
 * 
 */
package com.ericsson.catalina.users;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;

import org.apache.catalina.UserDatabase;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ericsson.catalina.users.EniqUserDatabaseSubjectFactory;


/**
 * @author eheijun
 *
 */
public class EniqUserDatabaseSubjectFactoryTest {

  private final Mockery context = new JUnit4Mockery() {

    {
      setImposteriser(ClassImposteriser.INSTANCE);
    }
  };
  
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
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
  }

  /**
   * Test method for {@link com.ericsson.catalina.users.EniqUserDatabaseSubjectFactory#getInstance(java.lang.String, java.lang.String, java.lang.Boolean)}.
   */
  @Test
  public void testGetMockInstance() {
    final UserDatabase mockUserDatabase = context.mock(UserDatabase.class);
    try {
      EniqUserDatabaseSubjectFactory.setInstance(mockUserDatabase);
      UserDatabase userDatabaseSubject = EniqUserDatabaseSubjectFactory.getInstance("", "", true);
      assertThat(userDatabaseSubject, notNullValue());
    } finally {
      EniqUserDatabaseSubjectFactory.setInstance(null);
    }
  }

  /**
   * Test method for {@link com.ericsson.catalina.users.EniqUserDatabaseSubjectFactory#getInstance(java.lang.String, java.lang.String, java.lang.Boolean)}.
   */
  @Test
  public void testGetDefaultInstance() {
    UserDatabase userDatabaseSubject = EniqUserDatabaseSubjectFactory.getInstance("tomcat-users.xml", "UserDatabase", false);
    assertThat(userDatabaseSubject, notNullValue());
  }

}
