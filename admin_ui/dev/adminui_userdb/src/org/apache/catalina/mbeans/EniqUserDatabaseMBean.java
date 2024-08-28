package org.apache.catalina.mbeans;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;

import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.RuntimeOperationsException;

import org.apache.catalina.Group;
import org.apache.catalina.Role;
import org.apache.catalina.User;
import org.apache.catalina.UserDatabase;
import org.apache.tomcat.util.modeler.BaseModelMBean;
import org.apache.tomcat.util.modeler.ManagedBean;
import org.apache.tomcat.util.modeler.Registry;

import com.ericsson.catalina.users.EniqUserDatabaseException;

/*
 * Implementation for ENIQ UserDatabase MBean
 * 
 * This is located in the org.apache.catalina.mbeans package
 * so we can use MBeanUtils originally located there 
 */
public class EniqUserDatabaseMBean extends BaseModelMBean {

  public EniqUserDatabaseMBean() throws MBeanException, RuntimeOperationsException {

    super();

  }

  protected Registry registry = MBeanUtils.createRegistry();

  protected ManagedBean managed = registry.findManagedBean("EniqUserDatabase");

  protected ManagedBean managedGroup = registry.findManagedBean("Group");

  protected ManagedBean managedRole = registry.findManagedBean("Role");

  protected ManagedBean managedUser = registry.findManagedBean("User");

  public String[] getGroups() {

    final UserDatabase database = (UserDatabase) this.resource;
    final ArrayList<String> results = new ArrayList<String>();
    final Iterator<Group> groups = database.getGroups();
    while (groups.hasNext()) {
      final Group group = groups.next();
      results.add(findGroup(group.getGroupname()));
    }
    return results.toArray(new String[results.size()]);

  }

  public String[] getRoles() {

    final UserDatabase database = (UserDatabase) this.resource;
    final ArrayList<String> results = new ArrayList<String>();
    final Iterator<Role> roles = database.getRoles();
    while (roles.hasNext()) {
      final Role role = roles.next();
      results.add(findRole(role.getRolename()));
    }
    return results.toArray(new String[results.size()]);

  }

  public String[] getUsers() {

    final UserDatabase database = (UserDatabase) this.resource;
    final ArrayList<String> results = new ArrayList<String>();
    final Iterator<User> users = database.getUsers();
    while (users.hasNext()) {
      final User user = users.next();
      results.add(findUser(user.getUsername()));
    }
    return results.toArray(new String[results.size()]);

  }

  /*
   * Helper method created to avoid strange java.lang.NoSuchMethodError when code was running at the CI server
   */
  private Method getMBeanUtilsMethod(final String methodName, final Class<?>... paramClass) {
    try {
      final Class<?> mbeanClass = Class.forName(MBeanUtils.class.getName());
      return mbeanClass.getMethod(methodName, paramClass);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (SecurityException e) {
      e.printStackTrace();
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    }
    return null;
  }

  /*
   * Helper method created to avoid strange java.lang.NoSuchMethodError when code was running at the CI server
   */
  private void callCreateMBeanMethod(final Class<?> paramClass, final Object param) {
    try {
      final Method m = getMBeanUtilsMethod("createMBean", paramClass);
      m.invoke(null, param);
    } catch (SecurityException e) {
      e.printStackTrace();
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
  }

  public String createGroup(final String groupname, final String description) {

    final UserDatabase database = (UserDatabase) this.resource;
    final Group group = database.createGroup(groupname, description);
    try {
      callCreateMBeanMethod(Group.class, group);
    } catch (Exception e) {
      final IllegalArgumentException iae = new IllegalArgumentException("Exception creating group " + group + " MBean");
      iae.initCause(e);
      throw iae;
    }
    return (findGroup(groupname));

  }

  public String createRole(final String rolename, final String description) {

    final UserDatabase database = (UserDatabase) this.resource;
    final Role role = database.createRole(rolename, description);
    try {
      callCreateMBeanMethod(Role.class, role);
    } catch (Exception e) {
      final IllegalArgumentException iae = new IllegalArgumentException("Exception creating role " + role + " MBean");
      iae.initCause(e);
      throw iae;
    }
    return (findRole(rolename));

  }
  
  public String createUser(final String username, final String password, final String fullName) throws EniqUserDatabaseException {

    final UserDatabase database = (UserDatabase) this.resource;
    User user;
    try {
      user = database.createUser(username, password, fullName);
      try {
        callCreateMBeanMethod(User.class, user);
      } catch (Exception e) {
        final IllegalArgumentException iae = new IllegalArgumentException("Exception creating user " + user + " MBean");
        iae.initCause(e);
        throw iae;
      }
    } catch (Exception e) {
      throw new EniqUserDatabaseException(e); 
    }
    return (findUser(username));

  }

  public String findGroup(final String groupname) {

    final UserDatabase database = (UserDatabase) this.resource;
    final Group group = database.findGroup(groupname);
    if (group == null) {
      return (null);
    }
    try {
      final ObjectName oname = MBeanUtils.createObjectName(managedGroup.getDomain(), group);
      return (oname.toString());
    } catch (MalformedObjectNameException e) {
      final IllegalArgumentException iae = new IllegalArgumentException("Cannot create object name for group " + group);
      iae.initCause(e);
      throw iae;
    }

  }

  public String findRole(final String rolename) {

    final UserDatabase database = (UserDatabase) this.resource;
    final Role role = database.findRole(rolename);
    if (role == null) {
      return (null);
    }
    try {
      final ObjectName oname = MBeanUtils.createObjectName(managedRole.getDomain(), role);
      return (oname.toString());
    } catch (MalformedObjectNameException e) {
      final IllegalArgumentException iae = new IllegalArgumentException("Cannot create object name for role " + role);
      iae.initCause(e);
      throw iae;
    }

  }

  public String findUser(final String username) {

    final UserDatabase database = (UserDatabase) this.resource;
    final User user = database.findUser(username);
    if (user == null) {
      return (null);
    }
    try {
      final ObjectName oname = MBeanUtils.createObjectName(managedUser.getDomain(), user);
      return (oname.toString());
    } catch (MalformedObjectNameException e) {
      final IllegalArgumentException iae = new IllegalArgumentException("Cannot create object name for user " + user);
      iae.initCause(e);
      throw iae;
    }

  }

  public void removeGroup(final String groupname) {

    final UserDatabase database = (UserDatabase) this.resource;
    final Group group = database.findGroup(groupname);
    if (group == null) {
      return;
    }
    try {
      MBeanUtils.destroyMBean(group);
      database.removeGroup(group);
    } catch (Exception e) {
      final IllegalArgumentException iae = new IllegalArgumentException("Exception destroying group " + group + " MBean");
      iae.initCause(e);
      throw iae;
    }

  }

  public void removeRole(final String rolename) {

    final UserDatabase database = (UserDatabase) this.resource;
    final Role role = database.findRole(rolename);
    if (role == null) {
      return;
    }
    try {
      MBeanUtils.destroyMBean(role);
      database.removeRole(role);
    } catch (Exception e) {
      final IllegalArgumentException iae = new IllegalArgumentException("Exception destroying role " + role + " MBean");
      iae.initCause(e);
      throw iae;
    }

  }

  public void removeUser(final String username) {

    final UserDatabase database = (UserDatabase) this.resource;
    final User user = database.findUser(username);
    if (user == null) {
      return;
    }
    try {
      MBeanUtils.destroyMBean(user);
      database.removeUser(user);
    } catch (Exception e) {
      final IllegalArgumentException iae = new IllegalArgumentException("Exception destroying user " + user + " MBean");
      iae.initCause(e);
      throw iae;
    }

  }

}
