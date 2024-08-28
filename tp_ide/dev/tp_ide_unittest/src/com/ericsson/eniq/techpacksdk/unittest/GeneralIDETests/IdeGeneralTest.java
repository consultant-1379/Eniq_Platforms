package com.ericsson.eniq.techpacksdk.unittest.GeneralIDETests;

import static org.fest.assertions.Assertions.assertThat;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import junit.framework.JUnit4TestAdapter;

import org.fest.assertions.Fail;
import org.fest.swing.core.BasicComponentFinder;
import org.fest.swing.core.ComponentFinder;
import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.core.Robot;
import org.fest.swing.finder.JOptionPaneFinder;
import org.fest.swing.finder.WindowFinder;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.JOptionPaneFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ssc.rockfactory.TableModificationLogger;
import com.ericsson.eniq.techpacksdk.unittest.utils.CommonUtils;
import com.ericsson.eniq.techpacksdk.unittest.utils.TechPackIdeStarter;
import com.ericsson.eniq.techpacksdk.unittest.utils.TestSetupConstants;



public class IdeGeneralTest {
  
  public static TechPackIdeStarter starter = null;
  
  public static JPanelFixture ideLoginFrame = null;
  
  private static Robot robot = null;
 

  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(IdeGeneralTest.class);
  }

  /**
   * @throws java.lang.Exception
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {

    System.out.println("setUpBeforeClass()");   
    // If table change logging is enabled, start the logger.
    if (TestSetupConstants.TABLE_CHANGE_LOGGING_FLAG) {
      TableModificationLogger.instance().startLogging();
      System.out.println("setUpBeforeClass(): Started table change logging.");
    }  
    
   

    System.out.println("setUpBeforeClass(): Done.");
  }

  /**
   * @throws java.lang.Exception
   */
  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    System.out.println("tearDownAfterClass(): Start.");

    // If table change logging is enabled, stop the logger.
//    if (TestSetupConsants.TABLE_CHANGE_LOGGING_FLAG) {
//      TableModificationLogger.instance().stopLogging();
//      System.out.println("tearDownAfterClass(): Stopped table change logging.");
//    }

    System.out.println("tearDownAfterClass(): Done.");
  }

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
	  System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
	  System.out.println("setUp(): Executing Test Case");
	  System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++");

    // Reset logger
    if (TestSetupConstants.TABLE_CHANGE_LOGGING_FLAG) {
      TableModificationLogger.instance().reset();
    }
    System.out.println("setUp(): Done.");
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
    System.out.println("tearDown(): Start.");

    // Print the changed database tables in the last test

    System.out.println("tearDown(): Done.");
  }

  @Test
  public void testIdeStartUp() throws Exception {
    System.out.println("testIdeStartUp(): Start.");
    
    // Get the TechPackIDE starter and start the IDE.
    starter = TechPackIdeStarter.getInstance();
    if (starter.openTechPackIdeLoginFrame() == false) {
      System.out.println("setUpBeforeClass(): IDE startup failed");
      Fail.fail("TechPackIDE failed to start");
    } 

    // Get the main pane
    ComponentFinder finder = (ComponentFinder) BasicComponentFinder.finderWithCurrentAwtHierarchy();
    JPanel target = (JPanel) finder.findByName("com.ericsson.eniq.techpacksdk.LoginPanel");
    
    robot = TechPackIdeStarter.getMyRobot();
    ideLoginFrame = new JPanelFixture(robot, target);   
    assertThat(ideLoginFrame).isNotNull().as("TechPack IDE login panel should be visible");
    assertThat(ideLoginFrame.target.getName().equals("com.ericsson.eniq.techpacksdk.LoginPanel")).as("At this stage only login panel should be visible");

    System.out.println("testIdeStartUp(): Done.");
  }
  
  /**
   * This test adds a dummy server and then 
   * deletes it
   * @throws java.lang.Exception
   */
  @Test
  public void testDeleteServer() throws Exception {
	  System.out.println("testDeleteServer(): Start.");
	  assertThat(ideLoginFrame).isNotNull().as("TechPack IDE login panel should be visible");
	  	  
	  deleteServer(TestSetupConstants.TEST_SERVER);
	  
	  System.out.println("testDeleteServer(): Done");
	  
  }
  
  /**
   * Test to add a new host server
   * @throws java.lang.Exception
   */
  @Test
  public void testAddNewServer() throws Exception {
	  System.out.println("testAddNewServer(): Start.");
	  assertThat(ideLoginFrame).isNotNull().as("TechPack IDE login panel should be visible");
	  
	  addServer(TestSetupConstants.TEST_SERVER);
	  
	  System.out.println("testAddNewServer(): Done");
	  
  }
  
  
  /**
   * This test clicks on check local setup button on login panel
   * 
   * @throws java.lang.Exception
   */
  @Test
  public void testCheckLocalSetup() throws Exception {
	  System.out.println("testCheckLocalSetup(): Start.");
	  assertThat(ideLoginFrame).isNotNull().as("TechPack IDE login panel should be visible");
	  
	  ideLoginFrame.button(new GenericTypeMatcher<JButton>(JButton.class) {
	    	
	    	@Override
	    	protected boolean isMatching(JButton button){
	    		return button.getText().equalsIgnoreCase("Check Local Setup");
	    	}
	    }).click();
	  
	  JOptionPaneFixture msgPane = JOptionPaneFinder.findOptionPane().withTimeout(40000).using(robot);
	  msgPane.okButton().click();
	  
	  System.out.println("testCheckLocalSetup(): Done");
	  
  }
  
  /**
   * This test clicks on check local setup button on login panel
   * 
   * @throws java.lang.Exception
   */
  @Test
  public void testAboutButton() throws Exception {
	  System.out.println("testCheckLocalSetup(): Start.");
	  assertThat(ideLoginFrame).isNotNull().as("TechPack IDE login panel should be visible");
	  
	  ideLoginFrame.button(new GenericTypeMatcher<JButton>(JButton.class) {	    	
	    	@Override
	    	protected boolean isMatching(JButton button){
	    		return button.getText().equalsIgnoreCase("About...");
	    	}
	    }).click();
	  
	  DialogFixture aboutDialog = WindowFinder.findDialog(new GenericTypeMatcher<JDialog>(JDialog.class) {
		  @Override
		  protected boolean isMatching(JDialog dialog){
	    		return dialog.getTitle().equalsIgnoreCase("About");
	    	}
	  }).withTimeout(30000).using(robot);
	  
	  aboutDialog.button(new GenericTypeMatcher<JButton>(JButton.class) {	    	
	    	@Override
	    	protected boolean isMatching(JButton button){
	    		return button.getText().equalsIgnoreCase("Ok");
	    	}
	    }).click();
	  
  }
  /**
   * Execute login 
   * 
   * @throws java.lang.Exception
   */
  @Test
  public void testIDELogin()throws Exception {
	  System.out.println("testIDELogin(): Start.");
	  assertThat(ideLoginFrame).isNotNull().as("TechPack IDE login panel should be visible");  
	  
	  assertThat(TechPackIdeStarter.executeLogin()).isTrue();
	    
	    System.out.println("testIDELogin(): Start.");
	  
  }
  
  
  
  
  
  
  //*******************
  //  Helper Methods
  //*******************
  
  /**
   * a helper method to add a host server in the list.
   * adds a new server if already does not exist 
   * test new configured server
   * save and close new server dialog.
   */
  public static void addServer(String server) {
	assertThat(ideLoginFrame).isNotNull().as("TechPack IDE login panel should be visible");
	    
	ideLoginFrame.button(new GenericTypeMatcher<JButton>(JButton.class) {
		    	
	  @Override
	  protected boolean isMatching(JButton button){
	  return button.getText().equalsIgnoreCase("Manage...");
	  }
	}).click();
		    
	DialogFixture manageServerDialog = CommonUtils.findDialogWithTitleName(robot, "Manage Server");
	GenericTypeMatcher<JButton> newButtonMatcher = new GenericTypeMatcher<JButton>(JButton.class) {
    	
    	@Override
    	protected boolean isMatching(JButton button){
    		return button.getText().equalsIgnoreCase("New");
    	}
    };
	manageServerDialog.button(newButtonMatcher).click();
	
	DialogFixture newServerDialog = CommonUtils.findDialogWithTitleName(robot, "New Server");
	newServerDialog.textBox().enterText(server);	
	assertThat(newServerDialog.button(newButtonMatcher).target.isEnabled()).isTrue();	
	try {
		newServerDialog.button(newButtonMatcher).click();
		JOptionPaneFixture serverExistsNotice = JOptionPaneFinder.findOptionPane(new GenericTypeMatcher<JOptionPane>(JOptionPane.class) {
  	   protected boolean isMatching(JOptionPane optionPane) {
  	     return "Server already configured.".equals(optionPane.getMessage());
  	   }
  	 }).withTimeout(10000).using(robot);
		
		if (serverExistsNotice.target.isVisible()) {
			serverExistsNotice.okButton().click();
			
		}
	} catch (Exception e){
		System.out.println("Notice dialog not found");		
		manageServerDialog.button(new GenericTypeMatcher<JButton>(JButton.class) {
	    	
	    	@Override
	    	protected boolean isMatching(JButton button){
	    		return button.getText().equalsIgnoreCase("Save");
	    	}
	    }).click();
	}
	manageServerDialog.textBox(new GenericTypeMatcher<JTextField>(JTextField.class) {
    	
    	@Override
    	protected boolean isMatching(JTextField textField){
    		return textField.getToolTipText().contains("Please enter the user name");
    	}
    }).selectAll().enterText(TestSetupConstants.TEST_SERVER_USER);
	manageServerDialog.textBox(new GenericTypeMatcher<JTextField>(JTextField.class) {
	
	@Override
	protected boolean isMatching(JTextField textField){
		return textField.getToolTipText().contains("Please enter the password");
	}
	}).selectAll().enterText(TestSetupConstants.TEST_SERVER_PASS);
	//manageServerDialog.comboBox().selectItem(server);
	manageServerDialog.button(new GenericTypeMatcher<JButton>(JButton.class) {
    	
    	@Override
    	protected boolean isMatching(JButton button){
    		return button.getText().equalsIgnoreCase("Save");
    	}
    }).click();
	manageServerDialog.button(new GenericTypeMatcher<JButton>(JButton.class) {
    	
    	@Override
    	protected boolean isMatching(JButton button){
    		return button.getText().equalsIgnoreCase("Test");
    	}
    }).click();
	
	CommonUtils.waitForBusyIndicator(10000);
	JOptionPaneFixture msgPane = JOptionPaneFinder.findOptionPane().withTimeout(40000).using(robot);
	msgPane.okButton().click();
	
	manageServerDialog.button(new GenericTypeMatcher<JButton>(JButton.class) {
    	
    	@Override
    	protected boolean isMatching(JButton button){
    		return button.getText().equalsIgnoreCase("Close");
    	}
    }).click();
	  
  }
  
  public static void deleteServer(String server) {
	  
	  
	 addServer(server); 
	 ideLoginFrame.button(new GenericTypeMatcher<JButton>(JButton.class) {			    	
		@Override
		protected boolean isMatching(JButton button){
		return button.getText().equalsIgnoreCase("Manage...");
		 }
	 }).click();
	 
	 DialogFixture manageServerDialog = CommonUtils.findDialogWithTitleName(robot, "Manage Server");
	 
	 manageServerDialog.comboBox().selectItem(server);
	 manageServerDialog.button(new GenericTypeMatcher<JButton>(JButton.class) {
	    	
	    	@Override
	    	protected boolean isMatching(JButton button){
	    		return button.getText().equalsIgnoreCase("Delete");
	    	}
	    }).click();
	JOptionPaneFixture msgPane = JOptionPaneFinder.findOptionPane().withTimeout(40000).using(robot);
	msgPane.yesButton().click();
	
	manageServerDialog.button(new GenericTypeMatcher<JButton>(JButton.class) {
    	
    	@Override
    	protected boolean isMatching(JButton button){
    		return button.getText().equalsIgnoreCase("Close");
    	}
    }).click(); 
	 
  }
  
  
  
 
 
}
