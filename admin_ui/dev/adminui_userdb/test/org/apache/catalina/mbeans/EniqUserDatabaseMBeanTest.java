/**
 * 
 */
package org.apache.catalina.mbeans;

//import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Properties;

import org.apache.catalina.Group;
import org.apache.catalina.Role;
import org.apache.catalina.User;
import org.apache.catalina.UserDatabase;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ericsson.catalina.users.EniqUserDatabaseException;


/**
 * @author eheijun
 *
 */
public class EniqUserDatabaseMBeanTest {

  private static final String PASSWORD = "eniq";

  private final Mockery context = new JUnit4Mockery();
  
  private UserDatabase mockUserDatabase;
  
  private EniqUserDatabaseMBean userDatabase;
  
  private Group mockGroup;
  private Role mockRoleEniq;
  private User mockUserEniq;

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
    mockUserDatabase = context.mock(UserDatabase.class);
    mockGroup = context.mock(Group.class);
    mockRoleEniq = context.mock(Role.class, "eniq_role");
    mockUserEniq = context.mock(User.class, "eniq_user");
    userDatabase = new EniqUserDatabaseMBean();
    userDatabase.setManagedResource(mockUserDatabase, "org.apache.catalina.UserDatabase");
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
  }

  /**
   * Test method for {@link org.apache.catalina.users.EniqUserDatabaseMBean#createGroup(java.lang.String, java.lang.String)}.
   * @throws Exception 
   */
  @Test
  public void testCreateGroup() throws Exception {
    context.checking(new Expectations() {
      {
        allowing(mockUserDatabase).createGroup(with(any(String.class)), with(any(String.class)));
        will(returnValue(mockGroup));
        allowing(mockUserDatabase).findGroup(with(any(String.class)));
        will(returnValue(mockGroup));
      }
    });
    String group = userDatabase.createGroup("eniq_group", "eniq users group");
    assertThat(group, notNullValue());
  }

  /**
   * Test method for {@link org.apache.catalina.users.EniqUserDatabaseMBean#createRole(java.lang.String, java.lang.String)}.
   */
  @Test
  public void testCreateRole() {
    context.checking(new Expectations() {
      {
        allowing(mockUserDatabase).createRole(with(any(String.class)), with(any(String.class)));
        will(returnValue(mockRoleEniq));
        allowing(mockUserDatabase).findRole(with(any(String.class)));
        will(returnValue(mockRoleEniq));
      }
    });
    String role = userDatabase.createRole("eniq_role", "eniq users role");
    assertThat(role, notNullValue());
  }

  /**
   * Test method for {@link org.apache.catalina.users.EniqUserDatabaseMBean#createUser(java.lang.String, java.lang.String, java.lang.String)}.
   * @throws EniqUserDatabaseException 
   */
  @Test
  public void testCreateEniqUser() throws EniqUserDatabaseException {
    context.checking(new Expectations() {
      {
        allowing(mockUserDatabase).createUser(with(any(String.class)), with(any(String.class)), with(any(String.class)));
        will(returnValue(mockUserEniq));
        allowing(mockUserDatabase).findUser(with(any(String.class)));
        will(returnValue(mockUserEniq));
      }
    });
    String user = userDatabase.createUser("eniq_user", PASSWORD, "");
    assertThat(user, notNullValue());
  }

  /**
   * Test method for {@link org.apache.catalina.users.EniqUserDatabaseMBean#findGroup(java.lang.String)}.
   */
  @Test
  public void testFindGroup() {
    context.checking(new Expectations() {
      {
        allowing(mockUserDatabase).findGroup(with(any(String.class)));
        will(returnValue(mockGroup));
      }
    });
    String group = userDatabase.findGroup("eniq_group");
    assertThat(group, notNullValue());
  }

  /**
   * Test method for {@link org.apache.catalina.users.EniqUserDatabaseMBean#findRole(java.lang.String)}.
   */
  @Test
  public void testFindRole() {
    context.checking(new Expectations() {
      {
        allowing(mockUserDatabase).findRole(with(any(String.class)));
        will(returnValue(mockRoleEniq));
      }
    });
    String role = userDatabase.findRole("eniq_role");
    assertThat(role, notNullValue());
  }

  /**
   * Test method for {@link org.apache.catalina.users.EniqUserDatabaseMBean#findUser(java.lang.String)}.
   */
  @Test
  public void testFindEniqUser() {
    context.checking(new Expectations() {
      {
        allowing(mockUserDatabase).findUser(with(any(String.class)));
        will(returnValue(mockUserEniq));
      }
    });
    String user = userDatabase.findUser("eniq_user");
    assertThat(user, notNullValue());
  }

  /**
   * Test method for {@link org.apache.catalina.users.EniqUserDatabaseMBean#getGroups()}.
   */
  @Test
  public void testGetGroups() {
    context.checking(new Expectations() {
      Group[] groupTable = new Group[] { mockGroup };
      {
        allowing(mockUserDatabase).getGroups();
        will(returnValue(Arrays.asList(groupTable).iterator()));
        allowing(mockGroup).getGroupname();
        will(returnValue("eniq_group"));
        allowing(mockUserDatabase).findGroup("eniq_group");
        will(returnValue(mockGroup));
      }
    });
    String[] groups = userDatabase.getGroups();
    assertThat(groups.length, not(0));
  }

  /**
   * Test method for {@link org.apache.catalina.users.EniqUserDatabaseMBean#getRoles()}.
   */
  @Test
  public void testGetRoles() {
    context.checking(new Expectations() {
      Role[] roleTable = new Role[] { mockRoleEniq };
      {
        allowing(mockUserDatabase).getRoles();
        will(returnValue(Arrays.asList(roleTable).iterator()));
        allowing(mockRoleEniq).getRolename();
        will(returnValue("eniq_role"));
        allowing(mockUserDatabase).findRole("eniq_role");
        will(returnValue(mockRoleEniq));
      }
    });
    String[] roles = userDatabase.getRoles();
    assertThat(roles.length, not(0));
  }

  /**
   * Test method for {@link org.apache.catalina.users.EniqUserDatabaseMBean#getUsers()}.
   */
  @Test
  public void testGetUsers() {
    context.checking(new Expectations() {
      User[] userTable = new User[] { mockUserEniq };
      {
        allowing(mockUserDatabase).getUsers();
        will(returnValue(Arrays.asList(userTable).iterator()));
        allowing(mockUserEniq).getUsername();
        will(returnValue("eniq_user"));
        allowing(mockUserDatabase).findUser("eniq_user");
        will(returnValue(mockUserEniq));
      }
    });
    String[] users = userDatabase.getUsers();
    assertThat(users.length, not(0));
  }

  /**
   * Test method for {@link org.apache.catalina.users.EniqUserDatabaseMBean#removeGroup(org.apache.catalina.Group)}.
   */
  @Test
  public void testRemoveGroup() {
    context.checking(new Expectations() {
      {
        allowing(mockUserDatabase).findGroup(with(any(String.class)));
        will(returnValue(mockGroup));
        allowing(mockUserDatabase).removeGroup(with(any(Group.class)));
      }
    });
    userDatabase.removeGroup("eniq");
  }

  /**
   * Test method for {@link org.apache.catalina.users.EniqUserDatabaseMBean#removeRole(org.apache.catalina.Role)}.
   */
  @Test
  public void testRemoveRole() {
    context.checking(new Expectations() {
      {
        allowing(mockUserDatabase).findRole(with(any(String.class)));
        will(returnValue(mockRoleEniq));
        allowing(mockUserDatabase).removeRole(with(any(Role.class)));
      }
    });
    userDatabase.removeRole("eniq");
  }

  /**
   * Test method for {@link org.apache.catalina.users.EniqUserDatabaseMBean#removeUser(org.apache.catalina.User)}.
   */
  @Test
  public void testRemoveUser() {
    context.checking(new Expectations() {
      {
        allowing(mockUserDatabase).findUser(with(any(String.class)));
        will(returnValue(mockUserEniq));
        allowing(mockUserDatabase).removeUser(with(any(User.class)));
      }
    });
    userDatabase.removeUser("eniq");
  }

}
