/**
 * 
 */
package com.ericsson.catalina.users;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Iterator;

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

import com.ericsson.catalina.users.EniqUserDatabase;


/**
 * @author eheijun
 *
 */
public class EniqUserDatabaseTest {

  private static final String ENCRYPTED_PASSWORD = "{AsciiCrypter}BbiKN+dVg/U=";

  private static final String PASSWORD = "eniq";

  private final Mockery context = new JUnit4Mockery();
  
  private UserDatabase mockUserDatabase;
  
  private Group mockGroup;
  private Role mockRoleEniq;
  private Role mockRoleNonEniq;
  private User mockUserEniq;
  private User mockUserNonEniq;

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
    mockRoleNonEniq = context.mock(Role.class, "non_eniq_role");
    mockUserEniq = context.mock(User.class, "eniq_user");
    mockUserNonEniq = context.mock(User.class, "non_eniq_user");
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
  }

  /**
   * Test method for {@link com.ericsson.catalina.users.EniqUserDatabase#EniqUserDatabase(org.apache.catalina.UserDatabase)}.
   */
  @Test
  public void testEniqUserDatabase() {
    new EniqUserDatabase(mockUserDatabase);
  }

  /**
   * Test method for {@link com.ericsson.catalina.users.EniqUserDatabase#close()}.
   * @throws Exception 
   */
  @Test
  public void testClose() throws Exception {
    context.checking(new Expectations() {
      {
        allowing(mockUserDatabase).close();
      }
    });
    EniqUserDatabase userDatabase = new EniqUserDatabase(mockUserDatabase);
    userDatabase.close();
  }

  /**
   * Test method for {@link com.ericsson.catalina.users.EniqUserDatabase#createGroup(java.lang.String, java.lang.String)}.
   * @throws Exception 
   */
  @Test
  public void testCreateGroup() throws Exception {
    context.checking(new Expectations() {
      {
        allowing(mockUserDatabase).createGroup(with(any(String.class)), with(any(String.class)));
        will(returnValue(mockGroup));
      }
    });
    EniqUserDatabase userDatabase = new EniqUserDatabase(mockUserDatabase);
    Group group = userDatabase.createGroup("eniq_group", "eniq users group");
    assertThat(group, notNullValue());
  }

  /**
   * Test method for {@link com.ericsson.catalina.users.EniqUserDatabase#createRole(java.lang.String, java.lang.String)}.
   */
  @Test
  public void testCreateRole() {
    context.checking(new Expectations() {
      {
        allowing(mockUserDatabase).createRole(with(any(String.class)), with(any(String.class)));
        will(returnValue(mockRoleEniq));
      }
    });
    EniqUserDatabase userDatabase = new EniqUserDatabase(mockUserDatabase);
    Role role = userDatabase.createRole("eniq_role", "eniq users role");
    assertThat(role, notNullValue());
  }

  /**
   * Test method for {@link com.ericsson.catalina.users.EniqUserDatabase#createUser(java.lang.String, java.lang.String, java.lang.String)}.
   */
  @Test
  public void testCreateEniqUser() {
    context.checking(new Expectations() {
      Role[] roleTable = new Role[] { mockRoleEniq };
      {
        allowing(mockUserDatabase).createUser(with(any(String.class)), with(any(String.class)), with(any(String.class)));
        will(returnValue(mockUserEniq));
        allowing(mockUserEniq).getRoles();
        will(returnValue(Arrays.asList(roleTable).iterator()));
        allowing(mockRoleEniq).getRolename();
        will(returnValue("eniq"));
        allowing(mockUserEniq).getPassword();
        will(returnValue(ENCRYPTED_PASSWORD));
        allowing(mockUserEniq).setPassword(PASSWORD);
      }
    });
    EniqUserDatabase userDatabase = new EniqUserDatabase(mockUserDatabase);
    User user = userDatabase.createUser("eniq_user", PASSWORD, "");
    assertThat(user, notNullValue());
  }

  /**
   * Test method for {@link com.ericsson.catalina.users.EniqUserDatabase#createUser(java.lang.String, java.lang.String, java.lang.String)}.
   */
  @Test
  public void testCreateNonEniqUser() {
    context.checking(new Expectations() {
      Role[] roleTable = new Role[] { mockRoleNonEniq };
      {
        allowing(mockUserDatabase).createUser(with(any(String.class)), with(any(String.class)), with(any(String.class)));
        will(returnValue(mockUserNonEniq));
        allowing(mockUserNonEniq).getRoles();
        will(returnValue(Arrays.asList(roleTable).iterator()));
        allowing(mockRoleNonEniq).getRolename();
        will(returnValue("non_eniq"));
        allowing(mockUserNonEniq).getPassword();
        will(returnValue(PASSWORD));
      }
    });
    EniqUserDatabase userDatabase = new EniqUserDatabase(mockUserDatabase);
    User user = userDatabase.createUser("non_eniq_user", PASSWORD, "");
    assertThat(user, notNullValue());
  }

  /**
   * Test method for {@link com.ericsson.catalina.users.EniqUserDatabase#createUser(java.lang.String, java.lang.String, java.lang.String)}.
   */
  @Test
  public void testCreateEniqUserFail() {
    context.checking(new Expectations() {
      {
        allowing(mockUserDatabase).createUser(with(any(String.class)), with(any(String.class)), with(any(String.class)));
        will(throwException(new Exception("SOME EXCEPITION FOR FAILING TEST")));
      }
    });
    EniqUserDatabase userDatabase = new EniqUserDatabase(mockUserDatabase);
    User user = userDatabase.createUser("eniq_user", PASSWORD, "");
    assertThat(user, nullValue());
  }

  /**
   * Test method for {@link com.ericsson.catalina.users.EniqUserDatabase#findGroup(java.lang.String)}.
   */
  @Test
  public void testFindGroup() {
    context.checking(new Expectations() {
      {
        allowing(mockUserDatabase).findGroup(with(any(String.class)));
        will(returnValue(mockGroup));
      }
    });
    EniqUserDatabase userDatabase = new EniqUserDatabase(mockUserDatabase);
    Group group = userDatabase.findGroup("eniq_group");
    assertThat(group, notNullValue());
  }

  /**
   * Test method for {@link com.ericsson.catalina.users.EniqUserDatabase#findRole(java.lang.String)}.
   */
  @Test
  public void testFindRole() {
    context.checking(new Expectations() {
      {
        allowing(mockUserDatabase).findRole(with(any(String.class)));
        will(returnValue(mockRoleEniq));
      }
    });
    EniqUserDatabase userDatabase = new EniqUserDatabase(mockUserDatabase);
    Role role = userDatabase.findRole("eniq_role");
    assertThat(role, notNullValue());
  }

  /**
   * Test method for {@link com.ericsson.catalina.users.EniqUserDatabase#findUser(java.lang.String)}.
   */
  @Test
  public void testFindEniqUser() {
    context.checking(new Expectations() {
      Role[] roleTable = new Role[] { mockRoleEniq };
      {
        allowing(mockUserDatabase).findUser(with(any(String.class)));
        will(returnValue(mockUserEniq));
        allowing(mockUserEniq).getRoles();
        will(returnValue(Arrays.asList(roleTable).iterator()));
        allowing(mockRoleEniq).getRolename();
        will(returnValue("eniq"));
        allowing(mockUserEniq).getPassword();
        will(returnValue(ENCRYPTED_PASSWORD));
        allowing(mockUserEniq).setPassword(PASSWORD);
      }
    });
    EniqUserDatabase userDatabase = new EniqUserDatabase(mockUserDatabase);
    User user = userDatabase.findUser("eniq_user");
    assertThat(user, notNullValue());
  }

  /**
   * Test method for {@link com.ericsson.catalina.users.EniqUserDatabase#findUser(java.lang.String)}.
   */
  @Test
  public void testFindNonEniqUser() {
    context.checking(new Expectations() {
      Role[] roleTable = new Role[] { mockRoleNonEniq };
      {
        allowing(mockUserDatabase).findUser(with(any(String.class)));
        will(returnValue(mockUserNonEniq));
        allowing(mockUserNonEniq).getRoles();
        will(returnValue(Arrays.asList(roleTable).iterator()));
        allowing(mockRoleNonEniq).getRolename();
        will(returnValue("non_eniq"));
        allowing(mockUserEniq).getPassword();
        will(returnValue(PASSWORD));
      }
    });
    EniqUserDatabase userDatabase = new EniqUserDatabase(mockUserDatabase);
    User user = userDatabase.findUser("eniq_user");
    assertThat(user, notNullValue());
  }

  /**
   * Test method for {@link com.ericsson.catalina.users.EniqUserDatabase#findUser(java.lang.String)}.
   */
  @Test
  public void testFindEniqUserFail() {
    context.checking(new Expectations() {
      {
        allowing(mockUserDatabase).findUser(with(any(String.class)));
        will(throwException(new Exception("SOME EXCEPITION FOR FAILING TEST")));
      }
    });
    EniqUserDatabase userDatabase = new EniqUserDatabase(mockUserDatabase);
    User user = userDatabase.findUser("eniq_user");
    assertThat(user, nullValue());
  }

  /**
   * Test method for {@link com.ericsson.catalina.users.EniqUserDatabase#getGroups()}.
   */
  @Test
  public void testGetGroups() {
    context.checking(new Expectations() {
      Group[] groupTable = new Group[] { mockGroup };
      {
        allowing(mockUserDatabase).getGroups();
        will(returnValue(Arrays.asList(groupTable).iterator()));
      }
    });
    EniqUserDatabase userDatabase = new EniqUserDatabase(mockUserDatabase);
    Iterator<Group> groups = userDatabase.getGroups();
    assertThat(groups.hasNext(), is(Boolean.valueOf(true)));
  }

  /**
   * Test method for {@link com.ericsson.catalina.users.EniqUserDatabase#getId()}.
   */
  @Test
  public void testGetId() {
    context.checking(new Expectations() {
      {
        allowing(mockUserDatabase).getId();
        will(returnValue("UserDatabase"));
      }
    });
    EniqUserDatabase userDatabase = new EniqUserDatabase(mockUserDatabase);
    String id = userDatabase.getId();
    assertThat(id, is("UserDatabase"));
  }

  /**
   * Test method for {@link com.ericsson.catalina.users.EniqUserDatabase#getRoles()}.
   */
  @Test
  public void testGetRoles() {
    context.checking(new Expectations() {
      Role[] roleTable = new Role[] { mockRoleEniq };
      {
        allowing(mockUserDatabase).getRoles();
        will(returnValue(Arrays.asList(roleTable).iterator()));
      }
    });
    EniqUserDatabase userDatabase = new EniqUserDatabase(mockUserDatabase);
    Iterator<Role> roles = userDatabase.getRoles();
    assertThat(roles.hasNext(), is(Boolean.valueOf(true)));
  }

  /**
   * Test method for {@link com.ericsson.catalina.users.EniqUserDatabase#getUsers()}.
   */
  @Test
  public void testGetUsers() {
    context.checking(new Expectations() {
      Role[] roleTableEniq = new Role[] { mockRoleEniq };
      Role[] roleTableNonEniq = new Role[] { mockRoleNonEniq };
      User[] userTable = new User[] { mockUserEniq, mockUserNonEniq };
      {
        allowing(mockUserDatabase).getUsers();
        will(returnValue(Arrays.asList(userTable).iterator()));
        allowing(mockUserEniq).getRoles();
        will(returnValue(Arrays.asList(roleTableEniq).iterator()));
        allowing(mockRoleEniq).getRolename();
        will(returnValue("eniq"));
        allowing(mockUserEniq).getPassword();
        will(returnValue(ENCRYPTED_PASSWORD));
        allowing(mockUserEniq).setPassword(PASSWORD);
        allowing(mockUserNonEniq).getRoles();
        will(returnValue(Arrays.asList(roleTableNonEniq).iterator()));
        allowing(mockRoleNonEniq).getRolename();
        will(returnValue("non_eniq"));
        allowing(mockUserEniq).getPassword();
        will(returnValue(PASSWORD));
      }
    });
    EniqUserDatabase userDatabase = new EniqUserDatabase(mockUserDatabase);
    Iterator<User> users = userDatabase.getUsers();
    assertThat(users.hasNext(), is(Boolean.valueOf(true)));
  }

  /**
   * Test method for {@link com.ericsson.catalina.users.EniqUserDatabase#open()}.
   * @throws Exception 
   */
  @Test
  public void testOpen() throws Exception {
    context.checking(new Expectations() {
      Role[] roleTableEniq = new Role[] { mockRoleEniq };
      Role[] roleTableNonEniq = new Role[] { mockRoleNonEniq };
      User[] userTable = new User[] { mockUserEniq, mockUserNonEniq };
      {
        allowing(mockUserDatabase).open();
        allowing(mockUserEniq).getRoles();
        will(returnValue(Arrays.asList(roleTableEniq).iterator()));
        allowing(mockUserEniq).getPassword();
        will(returnValue(PASSWORD));
        allowing(mockUserEniq).setPassword(ENCRYPTED_PASSWORD);
        allowing(mockUserNonEniq).getRoles();
        will(returnValue(Arrays.asList(roleTableNonEniq).iterator()));
        allowing(mockRoleEniq).getRolename();
        will(returnValue("eniq"));
        allowing(mockRoleNonEniq).getRolename();
        will(returnValue("non_eniq"));
        allowing(mockUserDatabase).getUsers();
        will(returnValue(Arrays.asList(userTable).iterator()));
      }
    });
    EniqUserDatabase userDatabase = new EniqUserDatabase(mockUserDatabase);
    userDatabase.open();
  }

  /**
   * Test method for {@link com.ericsson.catalina.users.EniqUserDatabase#removeGroup(org.apache.catalina.Group)}.
   */
  @Test
  public void testRemoveGroup() {
    context.checking(new Expectations() {
      {
        allowing(mockUserDatabase).removeGroup(with(any(Group.class)));
      }
    });
    EniqUserDatabase userDatabase = new EniqUserDatabase(mockUserDatabase);
    userDatabase.removeGroup(mockGroup);
  }

  /**
   * Test method for {@link com.ericsson.catalina.users.EniqUserDatabase#removeRole(org.apache.catalina.Role)}.
   */
  @Test
  public void testRemoveRole() {
    context.checking(new Expectations() {
      {
        allowing(mockUserDatabase).removeRole(with(any(Role.class)));
      }
    });
    EniqUserDatabase userDatabase = new EniqUserDatabase(mockUserDatabase);
    userDatabase.removeRole(mockRoleEniq);
  }

  /**
   * Test method for {@link com.ericsson.catalina.users.EniqUserDatabase#removeUser(org.apache.catalina.User)}.
   */
  @Test
  public void testRemoveUser() {
    context.checking(new Expectations() {
      {
        allowing(mockUserDatabase).removeUser(with(any(User.class)));
      }
    });
    EniqUserDatabase userDatabase = new EniqUserDatabase(mockUserDatabase);
    userDatabase.removeUser(mockUserEniq);
  }

  /**
   * Test method for {@link com.ericsson.catalina.users.EniqUserDatabase#save()}.
   * @throws Exception 
   */
  @Test
  public void testSave() throws Exception {
    context.checking(new Expectations() {
      {
        allowing(mockUserDatabase).save();
      }
    });
    EniqUserDatabase userDatabase = new EniqUserDatabase(mockUserDatabase);
    userDatabase.save();
  }

}
