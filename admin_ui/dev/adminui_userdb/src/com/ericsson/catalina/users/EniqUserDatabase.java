/**
 * 
 */
package com.ericsson.catalina.users;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.catalina.Group;
import org.apache.catalina.Role;
import org.apache.catalina.User;
import org.apache.catalina.UserDatabase;


/**
 * Proxy class for Tomcat UserDatabase implementation
 * @author eheijun
 * 
 */
public class EniqUserDatabase implements UserDatabase {

  private static final String ENIQ_ROLE = "eniq";
  
  private final UserDatabase realUserDatabase;

  /**
   * Constructor for crating UserDatabase proxy
   * 
   * @param userDatabase
   */
  EniqUserDatabase(final UserDatabase realUserDatabase) {
    super();
    this.realUserDatabase = realUserDatabase;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.catalina.UserDatabase#close()
   */
  @Override
  public void close() throws Exception { // NOPMD by eheijun on 30/06/11 14:51
    realUserDatabase.close();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.catalina.UserDatabase#createGroup(java.lang.String, java.lang.String)
   */
  @Override
  public Group createGroup(final String groupname, final String description) {
    return realUserDatabase.createGroup(groupname, description);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.catalina.UserDatabase#createRole(java.lang.String, java.lang.String)
   */
  @Override
  public Role createRole(final String rolename, final String description) {
    return realUserDatabase.createRole(rolename, description);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.catalina.UserDatabase#createUser(java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public User createUser(final String username, final String password, final String fullName) {
    try {
      final User user = realUserDatabase.createUser(username, AsciiCrypter.getInstance().encrypt(password), fullName);
      if (isEniqUser(user) && user.getPassword().startsWith(AsciiCrypter.CRYPT_STAMP)) {
        user.setPassword(AsciiCrypter.getInstance().decrypt(user.getPassword()));
      }
      return user;
    } catch (Exception e) {
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.catalina.UserDatabase#findGroup(java.lang.String)
   */
  @Override
  public Group findGroup(final String groupname) {
    return realUserDatabase.findGroup(groupname);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.catalina.UserDatabase#findRole(java.lang.String)
   */
  @Override
  public Role findRole(final String rolename) {
    return realUserDatabase.findRole(rolename);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.catalina.UserDatabase#findUser(java.lang.String)
   */
  @Override
  public User findUser(final String username) {
    try {
      final User user = realUserDatabase.findUser(username);
      if (isEniqUser(user) && user.getPassword().startsWith(AsciiCrypter.CRYPT_STAMP)) {
        user.setPassword(AsciiCrypter.getInstance().decrypt(user.getPassword()));
      }
      return user;
    } catch (Exception e) {
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.catalina.UserDatabase#getGroups()
   */
  @Override
  public Iterator<Group> getGroups() {
    return realUserDatabase.getGroups();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.catalina.UserDatabase#getId()
   */
  @Override
  public String getId() {
    return realUserDatabase.getId();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.catalina.UserDatabase#getRoles()
   */
  @Override
  public Iterator<Role> getRoles() {
    return realUserDatabase.getRoles();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.catalina.UserDatabase#getUsers()
   */
  @Override
  public Iterator<User> getUsers() {
    final Iterator<User> userIterator = realUserDatabase.getUsers();
    final List<User> users = new ArrayList<User>();
    while (userIterator.hasNext()) {
      final User user = userIterator.next();
      try {
        if (isEniqUser(user) && user.getPassword().startsWith(AsciiCrypter.CRYPT_STAMP)) {
          user.setPassword(AsciiCrypter.getInstance().decrypt(user.getPassword()));
        }
        users.add(user);
      } catch (IllegalBlockSizeException e) {
        user.setPassword(user.getPassword());
      } catch (BadPaddingException e) {
        user.setPassword(user.getPassword());
      } catch (IOException e) {
        user.setPassword(user.getPassword());
      } catch (InvalidKeyException e) {
        user.setPassword(user.getPassword());
      } catch (NoSuchAlgorithmException e) {
        user.setPassword(user.getPassword());
      } catch (NoSuchPaddingException e) {
        user.setPassword(user.getPassword());
      }
    }
    return users.iterator();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.catalina.UserDatabase#open()
   */
  @Override
  public void open() throws Exception { // NOPMD by eheijun on 30/06/11 14:51
    realUserDatabase.open();
    // check that all eniq passwords are encrypted
    final Iterator<User> users = realUserDatabase.getUsers();
    while (users.hasNext()) {
      final User user = users.next();
      if (isEniqUser(user) && !user.getPassword().startsWith(AsciiCrypter.CRYPT_STAMP)) {
        user.setPassword(AsciiCrypter.getInstance().encrypt(user.getPassword()));
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.catalina.UserDatabase#removeGroup(org.apache.catalina.Group)
   */
  @Override
  public void removeGroup(final Group group) {
    realUserDatabase.removeGroup(group);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.catalina.UserDatabase#removeRole(org.apache.catalina.Role)
   */
  @Override
  public void removeRole(final Role role) {
    realUserDatabase.removeRole(role);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.catalina.UserDatabase#removeUser(org.apache.catalina.User)
   */
  @Override
  public void removeUser(final User user) {
    realUserDatabase.removeUser(user);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.catalina.UserDatabase#save()
   */
  @Override
  public void save() throws Exception { // NOPMD by eheijun on 30/06/11 14:52
    realUserDatabase.save();
  }
  
  /**
   * Check if user has role ENIQ
   * @param user
   * @return
   */
  private Boolean isEniqUser(User user) {
    final Iterator<Role> roles = user.getRoles();
    while (roles.hasNext()) {
      final Role role = roles.next();
      if (ENIQ_ROLE.equals(role.getRolename().trim())) {
        return true;
      }
    }
    return false;
  }

}
