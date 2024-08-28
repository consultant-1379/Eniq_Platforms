/**
 * 
 */
package org.apache.catalina.mbeans;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.catalina.Group;
import org.apache.catalina.Role;
import org.apache.catalina.User;
import org.apache.tomcat.util.modeler.ManagedBean;
import org.apache.tomcat.util.modeler.Registry;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;

/**
 * Class for mocking real org.apache.catalina.mbeans
 * 
 * @author eheijun
 * 
 */
public class MBeanUtils {

  private final static Mockery context = new JUnit4Mockery() {

    {
      setImposteriser(ClassImposteriser.INSTANCE);
    }
  };

  static final Registry mockRegistry = context.mock(Registry.class);

  static final ManagedBean mockDatabase = context.mock(ManagedBean.class);

  static final ManagedBean mockManagedGroup = context.mock(ManagedBean.class, "managedGroup");

  static final ManagedBean mockManagedRole = context.mock(ManagedBean.class, "managedRole");

  static final ManagedBean mockManagedUser = context.mock(ManagedBean.class, "managedUser");

  static {
    context.checking(new Expectations() {

      {
        allowing(mockRegistry).findManagedBean(with("EniqUserDatabase"));
        will(returnValue(mockDatabase));
        allowing(mockRegistry).findManagedBean(with("Group"));
        will(returnValue(mockManagedGroup));
        allowing(mockRegistry).findManagedBean(with("Role"));
        will(returnValue(mockManagedRole));
        allowing(mockRegistry).findManagedBean(with("User"));
        will(returnValue(mockManagedUser));
        allowing(mockRegistry).loadDescriptors(with("com.ericsson.catalina.users"), with(any(ClassLoader.class)));
        allowing(mockManagedGroup).getDomain();
        will(returnValue("GroupDomain"));
        allowing(mockManagedRole).getDomain();
        will(returnValue("RoleDomain"));
        allowing(mockManagedUser).getDomain();
        will(returnValue("UserDomain"));
      }
    });
  }

  public static Registry createRegistry() {
    return mockRegistry;
  }

  public static void createMBean(Group group) throws Exception {
  }

  public static void createMBean(Role role) throws Exception {
  }

  public static void createMBean(User user) throws Exception {
  }

  public static ObjectName createObjectName(String domain, Group group) throws MalformedObjectNameException {
    return new ObjectName("group:groupname=eniq_group");
  }

  public static ObjectName createObjectName(String domain, Role role) throws MalformedObjectNameException {
    return new ObjectName("role:rolename=eniq_role");
  }

  public static ObjectName createObjectName(String domain, User user) throws MalformedObjectNameException {
    return new ObjectName("user:username=eniq_user");
  }

  public static void destroyMBean(Group group) {
  }

  public static void destroyMBean(Role role) {
  }

  public static void destroyMBean(User user) {
  }

}
