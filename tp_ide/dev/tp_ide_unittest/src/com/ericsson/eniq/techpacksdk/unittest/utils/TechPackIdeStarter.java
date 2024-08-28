package com.ericsson.eniq.techpacksdk.unittest.utils;

import static org.fest.assertions.Assertions.assertThat;

import java.awt.Component;
import java.awt.Frame;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

import org.fest.swing.core.BasicComponentFinder;
import org.fest.swing.core.BasicRobot;
import org.fest.swing.core.ComponentFinder;
import org.fest.swing.core.ComponentMatcher;
import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.core.Robot;
import org.fest.swing.exception.ComponentLookupException;
import org.fest.swing.exception.WaitTimedOutError;
import org.fest.swing.finder.JOptionPaneFinder;
import org.fest.swing.finder.WindowFinder;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JOptionPaneFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.fixture.JTabbedPaneFixture;
import org.fest.swing.timing.Condition;
import org.fest.swing.timing.Pause;
import ssc.rockfactory.RockFactory;

import com.ericsson.eniq.common.testutilities.UnitDatabaseTestCase.Schema;
import com.ericsson.eniq.repository.ETLCServerProperties;
import com.ericsson.eniq.techpacksdk.LoginPanel;
import com.ericsson.eniq.techpacksdk.TechPackIDE;
import com.ericsson.eniq.common.Constants;




/**
 * A singleton class used for executing a login to the TechPackIDE once, so that
 * the main window fixture can be used by all test cases without the need to
 * startup the IDE for each test case.
 * 
 * @author efaigha
 * 
 */
public class TechPackIdeStarter {

  /**
   * The singleton instance
   */
  private static TechPackIdeStarter instance = null;

  private static FrameFixture myWindow = null;

  private static TechPackIDE myTPIde = null;

  private static Robot myRobot = null;

  private static JTabbedPaneFixture myMainPanel = null;

  private static boolean techPackIdeRunning = false;

  private static RockFactory dwhRepDb = null;

  private static RockFactory etlRepDb = null;

  private static RockFactory dwhDb = null;
  
  static boolean isHostThere = false;
  
  private static  String sybase_db_driver = "";
  
  /**
   * Empty constructor. Exists only to defeat instantiation.
   *//*
  protected TechPackIdeStarter() {
    // No code needed
  }
*/
  /**
   * @return the sigleton instance of the starter class
   */
  public static TechPackIdeStarter getInstance() {
    if (instance == null) {
      instance = new TechPackIdeStarter();
    }
    return instance;
  }

  public static void main(String[] args) throws SQLException {
    System.out.println("TechPackIdeStarter: Starting TechPackIDE.");
    
    Runtime.getRuntime().addShutdownHook(new Thread() {
 
        public void run() {
        	try {
        		System.out.println("TechPackIdeStarter: Closing the Sybase DB and TechPackIDE application.");        		
				closeTechPackIde();
				closeSybaseDatabase();
			} catch (Exception e) {
				System.out.println("TechPackIdeStarter: Exception in closing TechPackIDE!!!!!"+e.getMessage());
				e.printStackTrace();
			} 
        }
      });
 
    Frame rootFrame = JOptionPane.getRootFrame();

    TechPackIdeStarterDialog dialog = TechPackIdeStarterDialog.showDialog(rootFrame);
    if (!dialog.closedWithOkButton()) {
      System.out.println("TechPackIdeStarter: TechPackIDE start aborted.");
      System.exit(0);
    }

    boolean startDBModificationLogger = dialog.tableModificationLoggerSelected();
    boolean startSwingHierarchyUtility = dialog.swingHierarchyUtilitySelected();
    boolean automaticLogin = dialog.automaticLoginSelected();
    boolean createTestTechpack = dialog.createTesttechpackSelected();

    dialog.dispose();   
    
    try {
			startSybaseDatabase(false);
		} catch (Exception e) {			
			System.out.println("TechPackIdeStarter(): Exception in starting the sybase DB!!!!! " + e.getMessage()+"");			
		}	
		
	// Create the robot before launching TechPackIDE
	myRobot = BasicRobot.robotWithCurrentAwtHierarchy();
	
    // Start the tech pack ide
    String[] arguments = {};
    TechPackIDE.launch(TechPackIDE.class, arguments);
    
    

    // Override the default settings for the dbUrl and dbDriver
    System.out.print("TechPackIdeStarter: Modifying dbUrl and dbDriver settings for techpack IDE... ");
    myWindow = WindowFinder.findFrame("mainFrame").withTimeout(10000).using(myRobot);
    JPanelFixture panel = myWindow.panel("com.ericsson.eniq.techpacksdk.LoginPanel");
    ((LoginPanel) panel.target).setDbDriver(SybaseDBSetup.getSybaseDriver());
    ((LoginPanel) panel.target).setDbUrl(etlRepDb.getDbURL());    
    // After window is visible Application will change to TechPackIDE (from Application$NoApplication)
    TechPackIdeStarter.setMyTPIde((TechPackIDE)TechPackIDE.getInstance());
    myTPIde.setEtlAuth(""); 
    System.out.print("Done!\n");
    
    
    if (startSwingHierarchyUtility) {
        Thread startSwingHierarchyUtilityThread = new Thread() {

          public void run() {
            SwingHierarchyUtility swingHierarchyUtility = new SwingHierarchyUtility();
            swingHierarchyUtility.setVisible(true);
          }
        };
        startSwingHierarchyUtilityThread.start();
      }

      if (startDBModificationLogger) {
        Thread tableModificationUtilThread = new Thread() {

          public void run() {
            TableModificationLoggerUtil tableModificationUtil = new TableModificationLoggerUtil();
            tableModificationUtil.setVisible(true);
          }
        };
        tableModificationUtilThread.start();
      }

    // Execute automatic login if the option was selected
    if (automaticLogin) {
       executeLogin();
    }
    
    // Automatically create an empty test techpack.
    if (automaticLogin && createTestTechpack) {
      System.out.println("TechPackIdeStarter: Creating a test techpack.");
      CommonUtils.createNewTechPack("TEST", "123", "CXC12345678", "P1A", "Universe",
          TestSetupConstants.BASE_TP_VERSIONID);
    }

    System.out.println("TechPackIdeStarter: Finished starting techpack IDE.");
    
    
  }
  
/**
   * Starts the techpackide and executes login. Returns true if the ide was
   * started and login performed successfully, or if a successful start and
   * login has already been done before.
   * 
   * @return true if successful.
   */
  public boolean startTechPackIde() {
    System.out.println("startTechPackIde()");

    // First check if the IDE is already running. If it it, the login will
    // not be done again.
    if (techPackIdeRunning) {
      System.out.println("startTechPackIde(): TechPackIDE is already running.");
      return true;
    }

    // Start the Sybase database.
    try {
		startSybaseDatabase(false);
	} catch (Exception e) {
		System.out.println("startTechPackIde(): Error in starting Sybase database" + e.getMessage());
		e.printStackTrace();
	}

    // Create the robot
    myRobot = BasicRobot.robotWithNewAwtHierarchy();

    String[] args = {};
    TechPackIDE.launch(TechPackIDE.class, args);
    myWindow = WindowFinder.findFrame("mainFrame").withTimeout(10000).using(myRobot);
    assertThat(myWindow).isNotNull();
    // After window is visible Application will change to TechPackIDE (from Application$NoApplication)
    TechPackIdeStarter.setMyTPIde((TechPackIDE)TechPackIDE.getInstance());
    assertThat(myTPIde).isNotNull();
    myTPIde.setEtlAuth(""); // Set authentication to be an empty string for the hsqldb.
    
    if (!executeLogin()) {
      // Login failed
    }

    // Create a matcher for the main pane
    ComponentMatcher matcher = new ComponentMatcher() {

      public boolean matches(Component c) {
        if (!(c instanceof JTabbedPane))
          return false;
        return ((JTabbedPane) c).isVisible();
      }
    };

    // Get the main pane
    ComponentFinder finder = (ComponentFinder) BasicComponentFinder.finderWithCurrentAwtHierarchy();
    JTabbedPane target = (JTabbedPane) finder.find(matcher);

    myMainPanel = new JTabbedPaneFixture(myWindow.robot, target);
    if (myMainPanel == null)
      return false;   
    // Now we have the main pane visible!!
    System.out.println("startTechPackIde(): IDE Main window found.");

    // Mark that the IDE is running
    techPackIdeRunning = true;

    System.out.println("startTechPackIde(): Done.");
    return true;
  }
  
  /**
   * Starts the techpackide without loggingin. 
   * This is used to test the functionality at login page.
   * 
   * @return true if successful.
   */
  public boolean openTechPackIdeLoginFrame() {
    System.out.println("openTechPackIdeLoginFrame()");
    
    if (techPackIdeRunning) {
      System.out.println("openTechPackIdeLoginFrame(): TechPackIDE is already running.");
      return true;
    }   
    try {
		startSybaseDatabase(false);
	} catch (Exception e) {
		System.out.println("openTechPackIdeLoginFrame(): Error in starting Sybase database" + e.getMessage());
		e.printStackTrace();
	}
    
    myRobot = BasicRobot.robotWithNewAwtHierarchy();

    String[] args = {};
    TechPackIDE.launch(TechPackIDE.class, args);
    myWindow = WindowFinder.findFrame("mainFrame").withTimeout(10000).using(myRobot);
    assertThat(myWindow).isNotNull();
    // After window is visible Application will change to TechPackIDE (from Application$NoApplication)
    TechPackIdeStarter.setMyTPIde((TechPackIDE)TechPackIDE.getInstance());
    assertThat(myTPIde).isNotNull();
    myTPIde.setEtlAuth(""); // Set authentication to be an empty string for the hsqldb.
    
//    if (!executeLogin()) {
//      System.out.println("IDE Login failed!!!!");
//    }
    
    techPackIdeRunning = true;

    System.out.println("openTechPackIdeLoginFrame(): Done.");
    return true;
  }
  

  public static void closeTechPackIde() throws Exception {
    System.out.println("closeTechPackIde(): Start.");
    //LoginTask.closeAllDBConn();

    if (!techPackIdeRunning) {
      System.out.println("closeTechPackIde(): TechPackIDE is not running. " + "No action needed.");
      return;
    }
    System.out.println("closeTechPackIde(): Pausing " + "for 5 seconds, and then quitting.");
    Pause.pause(5000);

    // Cleanup
    myWindow.cleanUp();

    myWindow = null;
    myTPIde = null;
    myRobot = null;
    myMainPanel = null;
    techPackIdeRunning = false;

    System.out.println("closeTechPackIde(): Done.");
  }

  /**
   * Starts the simulated database in memory.
 * @throws Exception 
   */
  protected static void startSybaseDatabase(boolean option) throws Exception {    
    System.out.println("\n" + "startSybaseDatabase(): Setting up Sybase Database on "+ TestSetupConstants.TEST_SERVER+"...");
    
    // This setup the live repdb on atrcx892zone3 
    if (option){
    	SybaseDBSetup.createStatsVer(true);
    }
    else{
    	SybaseDBSetup.createStatsVer();
    }
    
    
    // Get references for dwhRep and etlRep...
    dwhRepDb = SybaseDBSetup.getRockFactory(Schema.dwhrep);    
    etlRepDb = SybaseDBSetup.getRockFactory(Schema.etlrep);
    dwhDb = SybaseDBSetup.getRockFactory(Schema.dc);
    try{
    	createIDERqeStuff();
    }catch(Exception e){
    	System.out.println(" Exception : " + e.getMessage());
    }
    
    System.out.println("startsybaseDatabase(): RepDB successfully loaded on "+ TestSetupConstants.TEST_SERVER);
  }
  
  // To create users accounts in database for testing purposes 
  protected static void createIDERqeStuff() throws SQLException{
	   
	    String propertiesFile;  
		String conf_dir= "/eniq/sw/conf"; 		       
		  
		                
		if (!conf_dir.endsWith(File.separator)) 
		{                      
		conf_dir += File.separator;  
		}   
		propertiesFile = conf_dir + "ETLCServer.properties";  
		ETLCServerProperties connProps =new ETLCServerProperties(propertiesFile);
		sybase_db_driver=connProps.getProperty(Constants.ENGINE_DB_DRIVERNAME);
	  
	   final Statement stmt = etlRepDb.getConnection().createStatement();
	   final Statement dwhstmt = dwhRepDb.getConnection().createStatement();
   
	   final String query = "insert into META_DATABASES (USERNAME, VERSION_NUMBER, TYPE_NAME, CONNECTION_ID, CONNECTION_NAME, CONNECTION_STRING, PASSWORD, DESCRIPTION, DRIVER_NAME, DB_LINK_NAME) values ('dc', '0', 'USER', 11, 'dwh', 'jdbc:sybase:Tds:" + TestSetupConstants.TEST_SERVER +":3640/dwhdb_" + TestSetupConstants.TEST_SERVER +"?SQLINITSTRING=SET TEMPORARY OPTION CONNECTION_AUTHENTICATION=''Company=Ericsson;Application=ENIQ;Signature=000fa55157edb8e14d818eb4fe3db41447146f1571g539f0a8f80fd6239ea117b9d74be36c19c58dc14''', 'dc', null, '"+ sybase_db_driver +"', null);";
	   stmt.executeUpdate(query);
	   final String query1 = "insert into META_DATABASES (USERNAME, VERSION_NUMBER, TYPE_NAME, CONNECTION_ID, CONNECTION_NAME, CONNECTION_STRING, PASSWORD, DESCRIPTION, DRIVER_NAME, DB_LINK_NAME) values ('dc', '0', 'DBA', 12, 'dwh', 'jdbc:sybase:Tds:" + TestSetupConstants.TEST_SERVER +":3640/dwhdb_" + TestSetupConstants.TEST_SERVER +"?SQLINITSTRING=SET TEMPORARY OPTION CONNECTION_AUTHENTICATION=''Company=Ericsson;Application=ENIQ;Signature=000fa55157edb8e14d818eb4fe3db41447146f1571g539f0a8f80fd6239ea117b9d74be36c19c58dc14''', 'dc', null, '"+ sybase_db_driver +"', null);";
	   stmt.executeUpdate(query1);
	    
	   final String query2 = "INSERT INTO UserAccount (NAME, PASSWORD, ROLE) VALUES ('"+ TestSetupConstants.TEST_USERID +"', '"+ TestSetupConstants.TEST_USER_PASSWD +"', 'RnD');";
	   dwhstmt.executeUpdate(query2);
	        
		stmt.close();
		dwhstmt.close();
  }
  
  public static void reinitializeDatabases(){
	  
  }

  /**
   * Overridden version of the method to make sure the application is closed
   * before exiting.
   */
  protected void finalize() throws Throwable {
    System.out.println("finalize(): Closing the TechPackIDE application.");   
    closeTechPackIde();    
    super.finalize(); 
  }

  private static void closeSybaseDatabase() {
	  System.out.println("closeSybaseDatabase(): Closing the SybaseDatabase....");		 
	  try {
		  SybaseDBSetup.__afterclass__();		 
		
	  }catch (NullPointerException npe){
		  System.out.println("closeSybaseDatabase(): Exception in Closing Db before IDE launch " + npe.getMessage());
	  }
	  System.out.println("closeSybaseDatabase(): done.");	
	  		
}
 
public static boolean executeLogin() {
    System.out.println("executeLogin(): Starting login.");
   

    // Find the login panel from the window
    JPanelFixture panel = myWindow.panel("com.ericsson.eniq.techpacksdk.LoginPanel");
    assertThat(panel).isNotNull();

    ((LoginPanel) panel.target).setDbDriver(SybaseDBSetup.getSybaseDriver());
    ((LoginPanel) panel.target).setDbUrl(etlRepDb.getDbURL());

    // Fill in the login details
    checkIntegrationHost();
    if (isHostThere) {
    		try {
    			if (!((String) panel.comboBox("ServerField").target.getSelectedItem()).equals(SybaseDBSetup.getTestServer())) {
        	        panel.comboBox("ServerField").selectItem(SybaseDBSetup.getTestServer());
        	      }
    		 } catch (ComponentLookupException cle) {
       	      // Failure in setting the login window values
       	      System.out.println("executeLogin(): Failed to fill in login info to the login panel.");
       	      cle.printStackTrace();
       	    }      		
    	      
    	  }else {
    		  System.out.println("executeLogin():Integration host dose not exist in IDE. Adding Host....");
    		  addHost();
    		  try {
      			if (!((String) panel.comboBox("ServerField").target.getSelectedItem()).equals(SybaseDBSetup.getTestServer())) {
          	        panel.comboBox("ServerField").selectItem(SybaseDBSetup.getTestServer());
          	      }
      		 } catch (ComponentLookupException cle) {
         	      // Failure in setting the login window values
         	      System.out.println("executeLogin(): Failed to fill in login info to the login panel.");
         	      cle.printStackTrace();
         	    }      			  
    	  }
    try {
    	      if (!panel.textBox("UserField").text().equals(TestSetupConstants.TEST_USERID)) {       
    	        panel.textBox("UserField").setText(TestSetupConstants.TEST_USERID);
    	      }  
    	      panel.textBox("PasswordField").setText(TestSetupConstants.TEST_USER_PASSWD);
    	      panel.button("LoginButton").click();
    	    } catch (ComponentLookupException cle) {
    	      // Failure in setting the login window values
    	      System.out.println("executeLogin(): Failed to fill in login info to the login panel.");
    	      cle.printStackTrace();
    	    }  
     
     
   

    Condition mainPaneFound = new Condition("Main pane found") {

      public boolean test() {

        // Create a matcher for the main pane
        ComponentMatcher matcher = new ComponentMatcher() {

          public boolean matches(Component c) {
            if (!(c instanceof JTabbedPane))
              return false;
            return ((JTabbedPane) c).isVisible();
          }
        };

        ComponentFinder finder = (ComponentFinder) BasicComponentFinder.finderWithCurrentAwtHierarchy();

        try {
          finder.find(matcher);
        } catch (ComponentLookupException e) {          
          return false;
        }       
        return true;
      }
    };

    System.out.println("executeLogin(): Waiting for the login to succeed...");
    try {
      Pause.pause(mainPaneFound, TestSetupConstants.LOGIN_TIMEOUT);
    } catch (WaitTimedOutError e) {
      System.out.println("executeLogin(): Login failed due to timeout!");
      return false;
    }

    // Create a matcher for the main pane
    ComponentMatcher matcher = new ComponentMatcher() {

      public boolean matches(Component c) {
        if (!(c instanceof JTabbedPane))
          return false;
        return ((JTabbedPane) c).isVisible();
      }
    };

    // Get the main pane
    ComponentFinder finder = (ComponentFinder) BasicComponentFinder.finderWithCurrentAwtHierarchy();
    JTabbedPane target = (JTabbedPane) finder.find(matcher);

    myMainPanel = new JTabbedPaneFixture(myWindow.robot, target);
    if (myMainPanel == null)
      return false;

    // Now we have the main pane visible.
    System.out.println("executeLogin(): IDE Main window found. Login successful.");
    return true;
    
  }
  
private static void addHost() {
	JPanelFixture panel = myWindow.panel("com.ericsson.eniq.techpacksdk.LoginPanel");
    assertThat(panel).isNotNull();
	panel.button(new GenericTypeMatcher<JButton>(JButton.class) {
	    	
		  @Override
		  protected boolean isMatching(JButton button){
		  return button.getText().equalsIgnoreCase("Manage...");
		  }
		}).click();
			    
		DialogFixture manageServerDialog = CommonUtils.findDialogWithTitleName(getMyRobot(), "Manage Server");
		GenericTypeMatcher<JButton> newButtonMatcher = new GenericTypeMatcher<JButton>(JButton.class) {
	    	
	    	@Override
	    	protected boolean isMatching(JButton button){
	    		return button.getText().equalsIgnoreCase("New");
	    	}
	    };
		manageServerDialog.button(newButtonMatcher).click();
		
		DialogFixture newServerDialog = CommonUtils.findDialogWithTitleName(getMyRobot(), "New Server");
		newServerDialog.textBox().enterText(TestSetupConstants.TEST_SERVER);
		assertThat(newServerDialog.button(newButtonMatcher).target.isEnabled()).isTrue();	
		try {
			newServerDialog.button(newButtonMatcher).click();
			JOptionPaneFixture serverExistsNotice = JOptionPaneFinder.findOptionPane(new GenericTypeMatcher<JOptionPane>(JOptionPane.class) {
	  	   protected boolean isMatching(JOptionPane optionPane) {
	  	     return "Server already configured.".equals(optionPane.getMessage());
	  	   }
	  	 }).withTimeout(10000).using(getMyRobot());
			
			if (serverExistsNotice.target.isVisible()) {
				serverExistsNotice.okButton().click();
				
			}
		} catch (Exception e){
			System.out.println("Server already configured notice dialog not found");
			manageServerDialog.button(new GenericTypeMatcher<JButton>(JButton.class) {
		    	
		    	@Override
		    	protected boolean isMatching(JButton button){
		    		return button.getText().equalsIgnoreCase("Save");
		    	}
		    }).click();
		}
		
		manageServerDialog.comboBox().selectItem(TestSetupConstants.TEST_SERVER);		
		manageServerDialog.button(new GenericTypeMatcher<JButton>(JButton.class) {
	    	
	    	@Override
	    	protected boolean isMatching(JButton button){
	    		return button.getText().equalsIgnoreCase("Close");
	    	}
	    }).click();
	
}

private static void checkIntegrationHost() {
	System.out.println("checkIntegrationHost(): checking if integration host is already saved in IDE");
	String server;
	JPanelFixture panel = myWindow.panel("com.ericsson.eniq.techpacksdk.LoginPanel");
    assertThat(panel).isNotNull();
    for (int i=0;i<panel.comboBox().target.getItemCount();i++){
    	 server = (String) panel.comboBox().target.getItemAt(i);
    	 if (server.equals(SybaseDBSetup.getTestServer())){
    		 isHostThere = true;
    	 }
    }    	
	
}
  // Getters and setters

  public static FrameFixture getMyWindow() {
    return myWindow;
  }

  public static void setMyWindow(FrameFixture myWindow) {
    TechPackIdeStarter.myWindow = myWindow;
  }

  public static TechPackIDE getMyTPIde() {
    return myTPIde;
  }

  public static void setMyTPIde(TechPackIDE myTPIde) {
    TechPackIdeStarter.myTPIde = myTPIde;
  }

  public static Robot getMyRobot() {
    return myRobot;
  }

  public static void setMyRobot(Robot myRobot) {
    TechPackIdeStarter.myRobot = myRobot;
  }

  public static JTabbedPaneFixture getMyMainPanel() {
    return myMainPanel;
  }

  public static void setMyMainPanel(JTabbedPaneFixture myMainPanel) {
    TechPackIdeStarter.myMainPanel = myMainPanel;
  }

  public static RockFactory getDwhRepDb() {
    return dwhRepDb;
  }

  public static void setDwhRepDb(RockFactory dwhRepDb) {
    TechPackIdeStarter.dwhRepDb = dwhRepDb;
  }

  public static RockFactory getEtlRepDb() {
    return etlRepDb;
  }

  public static void setEtlRepDb(RockFactory etlRepDb) {
    TechPackIdeStarter.etlRepDb = etlRepDb;
  }

  public static RockFactory getDwhDb() {
    return dwhDb;
  }

  public static void setDwhDb(RockFactory dwhDb) {
    TechPackIdeStarter.dwhDb = dwhDb;
  }

  public static Connection getEtlRepDbConnection() {
    return TechPackIdeStarter.etlRepDb.getConnection();
  }

  public static Connection getDwhRepDbConnection() {
    return TechPackIdeStarter.etlRepDb.getConnection();
  }

  public static Connection getDwhDbConnection() {
    return TechPackIdeStarter.etlRepDb.getConnection();
  } 
 

}
