package com.ericsson.eniq.techpacksdk;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.repository.dwhrep.Useraccount;
import com.distocraft.dc5000.repository.dwhrep.UseraccountFactory;
import com.ericsson.eniq.techpacksdk.UserAdminPanel.UserTableModel;

public class UserAdminPanelTest{
	private static final String TESTDB_DRIVER = "org.hsqldb.jdbcDriver";
	  private static final String DWHREP_URL = "jdbc:hsqldb:mem:dwhrep";
	  private static final String USERNAME = "SA";
	  private static final String PASSWORD = "";
	  private Statement stm;
	  RockFactory rock = null;
	  private UserAdminPanel testUAP ;
	  private UserTableModel testUTM ;
	  
	  
	  private final Mockery context = new JUnit4Mockery() {
		    {
		      setImposteriser(ClassImposteriser.INSTANCE);
		    }
		};
	  
	  
	  @Before
	  public void setUp() throws Exception {
		  rock = new RockFactory(DWHREP_URL, "SA", "", TESTDB_DRIVER, "test", true);
		  //Create table UserAccount
		  try{
			  Class.forName(TESTDB_DRIVER);
			  Connection c;
			  c = DriverManager.getConnection(DWHREP_URL, USERNAME, PASSWORD);
			  stm = c.createStatement();
			  stm.execute("CREATE TABLE UserAccount (NAME VARCHAR(255), PASSWORD VARCHAR(16), ROLE VARCHAR(16), LASTLOGIN timestamp)");
			  stm.executeUpdate("insert into UserAccount (NAME, PASSWORD, ROLE) VALUES('admin', 'eniq', 'Admin')");
			  stm.executeUpdate("insert into UserAccount (NAME, PASSWORD, ROLE) VALUES('eanguan', 'saring', 'RnD')");
			  stm.executeUpdate("insert into UserAccount (NAME, PASSWORD, ROLE) VALUES('test', 'test', 'User')");
		  }catch(Exception e){
			  fail("Exception comes : " + e.getMessage());
		  }
		  testUAP = new UserAdminPanel(rock);
		  Useraccount uap = new Useraccount(rock);
		  UseraccountFactory uaf = new UseraccountFactory(rock, uap);
		  testUTM = testUAP.new UserTableModel(uaf.get());
	  }

	  @After
	  public void tearDown() throws Exception {
		  rock = null;
		  testUAP = null;
		  testUTM = null ;
	  }
	  
	  @Test
	  public void testAddUser() throws Exception{
		  testUTM.addRow("TESTUSERNAME", "TESTPASSWORD", "RnD");
		  
		  //Test whether record updated successfully or not
		  try{
			  Class.forName(TESTDB_DRIVER);
			  Connection c;
			  c = DriverManager.getConnection(DWHREP_URL, USERNAME, PASSWORD);
			  stm = c.createStatement();
			  
			  ResultSet value = stm.executeQuery("select * from UserAccount where NAME='TESTUSERNAME' AND PASSWORD='TESTPASSWORD' AND ROLE='RnD'");
			  if(!value.next()){
				  fail("Database not updated correctly. New user not added in database.");
			  }
			  value.close();
			  stm.close();
			  c.close();
		  }catch(Exception e){
			  fail("Exception comes : " + e.getMessage());
		  }
	  }
}
