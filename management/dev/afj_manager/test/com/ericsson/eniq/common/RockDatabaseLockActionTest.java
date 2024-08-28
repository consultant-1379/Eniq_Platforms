/**
 * 
 */
package com.ericsson.eniq.common;

import static org.junit.Assert.*;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.hamcrest.Matchers;import com.ericsson.eniq.afj.common.RockDatabaseLockAction;

import ssc.rockfactory.RockFactory;


/**
 * @author eheijun
 *
 */
public class RockDatabaseLockActionTest {

  private final Mockery context = new JUnit4Mockery() {

    {
      setImposteriser(ClassImposteriser.INSTANCE);
    }
  };

  private static final String LOCKUSER1 = "lockuser1";
  
  private static final String LOCKUSER2 = "lockuser2";

  private RockFactory mockDbaConn;

  private Connection mockConnection;

  private CallableStatement mockLockUserStatement;

  private CallableStatement mockUnlockUserStatement;
  
  private CallableStatement mockDropUserStatement;

  private ResultSet mockLockRs;
  
  private ResultSet mockUnlockRs;
  
  private ResultSet mockDropRs;

  private ArrayList<String> userList;
  
  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    
    mockDbaConn = context.mock(RockFactory.class);
    mockConnection = context.mock(Connection.class);
    mockLockUserStatement = context.mock(CallableStatement.class, "lock_stmt");
    mockUnlockUserStatement = context.mock(CallableStatement.class, "unlock_stmt");
    mockDropUserStatement = context.mock(CallableStatement.class, "drop_stmt");
    mockLockRs = context.mock(ResultSet.class, "lockRs");
    mockUnlockRs = context.mock(ResultSet.class, "unlockRs");
    mockDropRs = context.mock(ResultSet.class, "dropRs");
    
    context.checking(new Expectations() {
      {
      }
    });
    
    userList = new ArrayList<String>(Arrays.asList(new String[] { LOCKUSER1, LOCKUSER2 }));
    
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
    mockDbaConn = null;
  }

  /**
   * Test method for {@link com.ericsson.eniq.afj.common.RockDatabaseLockAction#performLock()}.
   * @throws SQLException 
   */
  @Test
  public void testPerformLock() throws SQLException {

    context.checking(new Expectations() {
      {
        allowing(mockDbaConn).getConnection();
        will(returnValue(mockConnection));
        
        allowing(mockConnection).prepareCall(with(Matchers.containsString(" lock_user ")));
        will(returnValue(mockLockUserStatement));
        
        allowing(mockConnection).prepareCall(with(Matchers.containsString(" drop_user_connections ")));
        will(returnValue(mockDropUserStatement));
        
        allowing(mockLockUserStatement).executeQuery();
        will(returnValue(mockLockRs));
        
        allowing(mockDropUserStatement).executeQuery();        
        will(returnValue(mockDropRs));
        
        oneOf(mockLockRs).next();
        will(returnValue(true));
        
        oneOf(mockLockRs).getString("msg");
        will(returnValue("User " + LOCKUSER1 + " locked at XXXX-XX-XX X:XX:XX"));
        
        oneOf(mockLockRs).next();
        will(returnValue(false));
        
        oneOf(mockLockRs).next();
        will(returnValue(true));
        
        oneOf(mockLockRs).getString("msg");
        will(returnValue("User " + LOCKUSER2 + " locked at XXXX-XX-XX X:XX:XX"));
        
        oneOf(mockLockRs).next();
        will(returnValue(false));
        
        oneOf(mockDropRs).next();
        will(returnValue(true));
        
        oneOf(mockDropRs).getString("msg");
        will(returnValue("User " + LOCKUSER1 + " connection with ConnHandle XXXXXX dropped at XXXX-XX-XX XX:XX:XX"));
        
        oneOf(mockDropRs).next();
        will(returnValue(true));
        
        oneOf(mockDropRs).getString("msg");
        will(returnValue("User " + LOCKUSER1 + " connection with ConnHandle XXXXXX dropped at XXXX-XX-XX XX:XX:XX"));
        
        oneOf(mockDropRs).next();
        will(returnValue(false));
        
        oneOf(mockDropRs).next();
        will(returnValue(true));
        
        oneOf(mockDropRs).getString("msg");
        will(returnValue("User " + LOCKUSER2 + " connection with ConnHandle XXXXXX dropped at XXXX-XX-XX XX:XX:XX"));
        
        oneOf(mockDropRs).next();
        will(returnValue(false));
        
        allowing(mockLockRs).close();
        allowing(mockDropRs).close();
        
        allowing(mockLockUserStatement).close();
        allowing(mockDropUserStatement).close();
        
      }
    });
    
    try {
      final RockDatabaseLockAction rockDatabaseLockAction = new RockDatabaseLockAction(mockDbaConn, userList);
      final Boolean result = rockDatabaseLockAction.performLock();
      assertTrue(result);
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  /**
   * Test method for {@link com.ericsson.eniq.afj.common.RockDatabaseLockAction#performLock()}.
   * @throws SQLException 
   */
  @Test
  public void testPerformLockFail() throws SQLException {

    context.checking(new Expectations() {
      {
        allowing(mockDbaConn).getConnection();
        will(returnValue(mockConnection));
        
        allowing(mockConnection).prepareCall(with(Matchers.containsString(" lock_user ")));
        will(returnValue(mockLockUserStatement));
        
        allowing(mockConnection).prepareCall(with(Matchers.containsString(" drop_user_connections ")));
        will(returnValue(mockDropUserStatement));
        
        allowing(mockLockUserStatement).executeQuery();
        will(returnValue(mockLockRs));
        
        allowing(mockDropUserStatement).executeQuery();        
        will(returnValue(mockDropRs));
        
        oneOf(mockLockRs).next();
        will(returnValue(true));
        
        oneOf(mockLockRs).getString("msg");
        will(returnValue("User " + LOCKUSER1 + " locked at XXXX-XX-XX X:XX:XX"));
        
        oneOf(mockLockRs).next();
        will(returnValue(false));
        
        oneOf(mockDropRs).next();
        will(returnValue(true));
        
        oneOf(mockDropRs).getString("msg");
        will(returnValue("User " + LOCKUSER1 + " connection with ConnHandle XXXXXX dropped at XXXX-XX-XX XX:XX:XX"));
        
        oneOf(mockDropRs).next();
        will(returnValue(false));
        
        oneOf(mockLockRs).next();
        will(throwException(new SQLException("Testing locking fail operation.")));
        
        allowing(mockConnection).prepareCall(with(Matchers.containsString(" unlock_user ")));
        will(returnValue(mockUnlockUserStatement));
        
        allowing(mockUnlockUserStatement).executeQuery();
        will(returnValue(mockUnlockRs));
        
        oneOf(mockUnlockRs).next();
        will(returnValue(true));
        
        oneOf(mockUnlockRs).getString("msg");
        will(returnValue("User " + LOCKUSER1 + " unlocked at XXXX-XX-XX X:XX:XX"));
        
        oneOf(mockUnlockRs).next();
        will(returnValue(false));
        
        allowing(mockLockRs).close();
        allowing(mockUnlockRs).close();
        allowing(mockDropRs).close();
        
        allowing(mockLockUserStatement).close();
        allowing(mockUnlockUserStatement).close();
        allowing(mockDropUserStatement).close();
        
      }
    });
    
    try {
    	final RockDatabaseLockAction rockDatabaseLockAction = new RockDatabaseLockAction(mockDbaConn, userList);
    	final Boolean result = rockDatabaseLockAction.performLock();
      assertFalse(result);
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  /**
   * Test method for {@link com.ericsson.eniq.afj.common.RockDatabaseLockAction#performUnlock()}.
   * @throws SQLException 
   */
  @Test
  public void testPerformUnlock() throws SQLException {

    context.checking(new Expectations() {
      {
        allowing(mockDbaConn).getConnection();
        will(returnValue(mockConnection));
        
        allowing(mockConnection).prepareCall(with(Matchers.containsString(" unlock_user ")));
        will(returnValue(mockUnlockUserStatement));
        
        allowing(mockUnlockUserStatement).executeQuery();
        will(returnValue(mockUnlockRs));
        
        oneOf(mockUnlockRs).next();
        will(returnValue(true));
        
        oneOf(mockUnlockRs).getString("msg");
        will(returnValue("User " + LOCKUSER1 + " unlocked at XXXX-XX-XX X:XX:XX"));
        
        oneOf(mockUnlockRs).next();
        will(returnValue(false));
        
        oneOf(mockUnlockRs).next();
        will(returnValue(true));
        
        oneOf(mockUnlockRs).getString("msg");
        will(returnValue("User " + LOCKUSER2 + " unlocked at XXXX-XX-XX X:XX:XX"));
        
        oneOf(mockUnlockRs).next();
        will(returnValue(false));
        
        allowing(mockUnlockRs).close();

        allowing(mockUnlockUserStatement).close();
        
      }
    });
    
    try {
      final ArrayList<String> userList = new ArrayList<String>(Arrays.asList(new String[] { LOCKUSER1, LOCKUSER2 }));
      final RockDatabaseLockAction rockDatabaseLockAction = new RockDatabaseLockAction(mockDbaConn, userList);
      final Boolean result = rockDatabaseLockAction.performUnlock();
      assertTrue(result);
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

}
