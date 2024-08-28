/**
 * 
 */
package com.ericsson.catalina.users;

import static org.junit.Assert.*;

import javax.naming.CompositeName;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.StringRefAddr;

import org.apache.catalina.UserDatabase;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ericsson.catalina.users.EniqUserDatabaseException;
import com.ericsson.catalina.users.EniqUserDatabaseFactory;
import com.ericsson.catalina.users.EniqUserDatabaseSubjectFactory;


/**
 * @author eheijun
 *
 */
public class EniqUserDatabaseFactoryTest {

  private final Mockery context = new JUnit4Mockery() {

    {
      setImposteriser(ClassImposteriser.INSTANCE);
    }
  };

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    final UserDatabase mockUserDatabase = context.mock(UserDatabase.class);
    EniqUserDatabaseSubjectFactory.setInstance(mockUserDatabase);
    context.checking(new Expectations() {

      {
        allowing(mockUserDatabase).open();
        allowing(mockUserDatabase).getUsers();
        allowing(mockUserDatabase).save();
      }
    });
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
    EniqUserDatabaseSubjectFactory.setInstance(null);
  }

  /**
   * Test method for {@link com.ericsson.catalina.users.EniqUserDatabaseFactory#getObjectInstance(java.lang.Object, javax.naming.Name, javax.naming.Context, java.util.Hashtable)}.
   * @throws EniqUserDatabaseException 
   * @throws InvalidNameException 
   */
  @Test
  public void testGetObjectInstance() throws EniqUserDatabaseException, InvalidNameException {
    EniqUserDatabaseFactory eniqUserDatabaseFactory = new EniqUserDatabaseFactory();
    
    Reference reference = new Reference("org.apache.catalina.UserDatabase");
    
    RefAddr refAddr1 = new StringRefAddr("pathname", "eniq.xml"); 
    reference.add(refAddr1);
    
    RefAddr refAddr2 = new StringRefAddr("readonly", "false"); 
    reference.add(refAddr2);
    
    Name name  = new CompositeName("UserDatabase");
    UserDatabase userDatabase = (UserDatabase) eniqUserDatabaseFactory.getObjectInstance(reference, name, null, null);
    assertTrue(userDatabase != null);
    
  }

}
