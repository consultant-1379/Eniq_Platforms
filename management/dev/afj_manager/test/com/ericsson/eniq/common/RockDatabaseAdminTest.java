/**
 * 
 */
package com.ericsson.eniq.common;

import static org.junit.Assert.*;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;import com.ericsson.eniq.afj.common.DatabaseState;import com.ericsson.eniq.afj.common.RockDatabaseAdmin;

import ssc.rockfactory.RockFactory;


/**
 * @author eheijun
 *
 */
public class RockDatabaseAdminTest {

  private final Mockery context = new JUnit4Mockery() {

    {
      setImposteriser(ClassImposteriser.INSTANCE);
    }
  };
  
  private RockFactory mockFactory;

  private Connection mockConnection;
  
  private CallableStatement mockStatement;
  
  private ResultSet mockRs;
  
  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    mockFactory = context.mock(RockFactory.class);
    mockConnection = context.mock(Connection.class);
    mockStatement = context.mock(CallableStatement.class);
    mockRs = context.mock(ResultSet.class);
    
    context.checking(new Expectations() {
      {
        allowing(mockFactory).getConnection();
        will(returnValue(mockConnection));
        allowing(mockConnection).createStatement();
        will(returnValue(mockStatement));
        allowing(mockStatement).executeQuery(with(any(String.class)));
        will(returnValue(mockRs));
        allowing(mockStatement).close();
      }
    });
    
  }
  
  /**
   * Test method for {@link com.ericsson.eniq.afj.common.RockDatabaseAdmin#getDatabaseState()}.
   * @throws SQLException 
   */
  @Test
  public void testGetDatabaseNormalState() throws SQLException {

    context.checking(new Expectations() {
      {
        oneOf(mockRs).next();
        will(returnValue(true));
        oneOf(mockRs).getString("Name");
        will(returnValue(" Main IQ Blocks Used:"));
        oneOf(mockRs).getString("Value");
        will(returnValue("XXXXXX of 10560000, 89%=XXGb, Max Block#: XXXXXXXX"));
        oneOf(mockRs).next();
        will(returnValue(false));
        oneOf(mockRs).close();
      }
    });
    
    final RockDatabaseAdmin rockDatabaseAdmin = new RockDatabaseAdmin(mockFactory);
    try {
      assertTrue(rockDatabaseAdmin.isState(DatabaseState.NORMAL));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  /**
   * Test method for {@link com.ericsson.eniq.afj.common.RockDatabaseAdmin#getDatabaseState()}.
   * @throws SQLException 
   */
  @Test
  public void testGetDatabaseMaintenanceState() throws SQLException {

    context.checking(new Expectations() {
      {
        oneOf(mockRs).next();
        will(returnValue(true));
        oneOf(mockRs).getString("Name");
        will(returnValue(" Main IQ Blocks Used:"));
        oneOf(mockRs).getString("Value");
        will(returnValue("XXXXXXX of 10560000, 91%=XXGb, Max Block#: XXXXXXXX"));
        oneOf(mockRs).next();
        will(returnValue(false));
        oneOf(mockRs).close();
      }
    });
    
    final RockDatabaseAdmin rockDatabaseAdmin = new RockDatabaseAdmin(mockFactory);
    try {
      assertTrue(rockDatabaseAdmin.isState(DatabaseState.MAINTENANCE));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

}
