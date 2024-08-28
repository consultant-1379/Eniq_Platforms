package com.ericsson.eniq.techpacksdk.view.actionViews;

import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.classextension.EasyMock.createNiceMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.reset;
import junit.framework.TestCase;
import javax.swing.JPanel;
import javax.swing.JTextField;
import static org.easymock.classextension.EasyMock.verify;
import org.junit.After;
import org.junit.Before;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import java.util.logging.Level;

/**
 * Test for CreateAlarmFileActionView.
 * @author eciacah
 */
public class CreateAlarmFileActionViewTest extends TestCase {
  
  //Test instance:
  private CreateAlarmFileActionView testInstance = null;

  // Mocks:
  private JTextField directoryTextboxMock;
  private JTextField filenameTextboxMock;
  
  // Constant for carriage return in properties:
  private final String LINE_SEPARATOR = System.getProperty("line.separator");

  private final String filenameValue = "filename.txt"; ;

  private final String directoryValue = "testdirectory";

  public CreateAlarmFileActionViewTest() {
    super("CreateFileActionViewTest");    
  }

  @Override
  @Before
  protected void setUp() throws Exception {    
    directoryTextboxMock = createNiceMock(JTextField.class);
    filenameTextboxMock = createNiceMock(JTextField.class);

    // Create new test instance:
    testInstance = new CreateAlarmFileActionView(directoryTextboxMock, filenameTextboxMock);
    // Disable logging:
    testInstance.getLogger().setLevel(Level.OFF);
  }

  @Override
  @After
  protected void tearDown() throws Exception {
    testInstance = null;
  }
  
  public void testCreateAction() {
    final JPanel panel = new JPanel();
    final Meta_transfer_actions mta = createNiceMock(Meta_transfer_actions.class);
    testInstance = new CreateAlarmFileActionView(panel, mta);
  }

  /**
   * Test that getContent() returns the correct information.
   * 
   * @throws Exception
   */
  public void testGetContent() throws Exception {

    final String directoryValue = "test directory";
    final String filenameValue = "testFilename.txt";

    // Set up expectations:
    directoryTextboxMock.getText();
    expectLastCall().andReturn(directoryValue);
    replay(directoryTextboxMock);

    filenameTextboxMock.getText();
    expectLastCall().andReturn(filenameValue);
    replay(filenameTextboxMock);

    final String result = testInstance.getContent();
    assertTrue("Value should be stored for directory", result.contains(CreateAlarmFileActionView.DIRECTORY_PROPERTY_KEY + "=" + directoryValue));
    assertTrue("Value should be stored for filename", result.contains(CreateAlarmFileActionView.FILENAME_PROPERTY_KEY + "=" + filenameValue));
  }

  /**
   * Test validate() method with incorrect value for filename. Should return an error message.
   */
  public void testValidateInvalidFilename() {

    String invalidFilenameValue = ""; 
    // Set up expectations:
    expectGetTextboxValues(2, directoryValue, invalidFilenameValue);

    String result = testInstance.validate();
    assertTrue("Error message should be added if filename value is invalid", result.length() > 0);   
    
    // Do validate with null values, not just empty string:
    invalidFilenameValue = null;
    // Reset the mocks to run test again:
    reset(directoryTextboxMock);
    reset(filenameTextboxMock);
    
    directoryTextboxMock.getText();
    expectLastCall().andReturn(directoryValue).times(2);
    replay(directoryTextboxMock);

    filenameTextboxMock.getText();
    expectLastCall().andReturn(invalidFilenameValue);
    replay(filenameTextboxMock);

    result = testInstance.validate();
    assertTrue("Error message should be added if filename value is invalid", result.length() > 0);
    
    verify(directoryTextboxMock);
    verify(filenameTextboxMock);
  }
  
  /**
   * Test validate() method with incorrect value for directory. Should return an error message.
   */
  public void testValidateInvalidDirectory() {

    // Set up expectations:
    String invalidDirectoryValue = "";
    final String filenameValue = "filename.txt";
    expectGetTextboxValues(2, invalidDirectoryValue, filenameValue);

    String result = testInstance.validate();

    assertTrue("Error message should be added if directory value is invalid", result.length() > 0);
    
    verify(directoryTextboxMock);
    verify(filenameTextboxMock);
    
    // Do validate with null value for directory:
    reset(directoryTextboxMock);
    reset(filenameTextboxMock);
        
    invalidDirectoryValue = null;
    directoryTextboxMock.getText();
    expectLastCall().andReturn(invalidDirectoryValue);
    replay(directoryTextboxMock);

    filenameTextboxMock.getText();
    expectLastCall().andReturn(filenameValue).times(2);
    replay(filenameTextboxMock);

    result = testInstance.validate();
    assertTrue("Error message should be added if directory value is invalid", result.length() > 0);
    
    verify(directoryTextboxMock);
    verify(filenameTextboxMock);
  }

  /**
   * Test validate() method with correct values. Should return 'true'.
   */
  public void testValidate() {

    // Set up expectations:
    expectGetTextboxValues(2, directoryValue, filenameValue);

    final String result = testInstance.validate();

    assertTrue("There should be no error messages if the parameters are correct", result.length() == 0);
  }
  
  public void testSetTextboxValues() {
    
    // Create new mock for Meta_transfer_actions:
    final Meta_transfer_actions actionMock = createNiceMock(Meta_transfer_actions.class);
    
    actionMock.getAction_contents();
    expectLastCall().andReturn(CreateAlarmFileActionView.DIRECTORY_PROPERTY_KEY + "=" + directoryValue 
        + LINE_SEPARATOR 
        + CreateAlarmFileActionView.FILENAME_PROPERTY_KEY + "=" + filenameValue 
        + LINE_SEPARATOR);
    replay(actionMock);
    
    directoryTextboxMock.setText(directoryValue);
    expectLastCall();
    replay(directoryTextboxMock);

    filenameTextboxMock.setText(filenameValue);
    expectLastCall();
    replay(filenameTextboxMock);
    
    testInstance.setTextboxValues(actionMock);
    verify(actionMock);
    verify(directoryTextboxMock);
    verify(filenameTextboxMock);
  }
  
  public void testSetTextboxValuesWithError() {
    
    // Create new mock for Meta_transfer_actions:
    final Meta_transfer_actions actionMock = createNiceMock(Meta_transfer_actions.class);
    
    actionMock.getAction_contents();
    expectLastCall().andReturn(CreateAlarmFileActionView.DIRECTORY_PROPERTY_KEY + "=" + directoryValue 
        + LINE_SEPARATOR 
        + CreateAlarmFileActionView.FILENAME_PROPERTY_KEY + "=" + filenameValue 
        + LINE_SEPARATOR);
    replay(actionMock);
    
    directoryTextboxMock.setText(directoryValue);
    expectLastCall();
    replay(directoryTextboxMock);

    // Error setting the value of the filename text box: 
    filenameTextboxMock.setText(filenameValue);
    expectLastCall().andThrow(new RuntimeException("Error setting value"));
    replay(filenameTextboxMock);
    
    testInstance.setTextboxValues(actionMock);
    verify(actionMock);
    verify(directoryTextboxMock);
    verify(filenameTextboxMock);
  }
  
  /**
   * Test case where Meta_transfer_actions object passed into setTextboxValues() is null.
   */
  public void testSetTextboxValuesActionNull() {
    
    // Meta_transfer_actions object is null:
    final Meta_transfer_actions actionMock = null;       
    testInstance.setTextboxValues(actionMock);
  }
  
  public void testGetWhere() throws Exception {
    final String result = testInstance.getWhere();
    assertEquals("getWhere() should return an empty string", "", result);
  }
  
  public void testIsChanged() throws Exception {
    final boolean result = testInstance.isChanged();
    assertEquals("isChanged() should return true", true, result);
  }
  
  public void testGetType() throws Exception {
    final String result = testInstance.getType();
    assertEquals("getType() should return 'CreateFile'", "CreateFile", result);
  }
  
  private void expectGetTextboxValues(final int noOfTimes, final String directoryValue, final String filenameValue) {
    directoryTextboxMock.getText();
    expectLastCall().andReturn(directoryValue).times(noOfTimes);
    replay(directoryTextboxMock);

    filenameTextboxMock.getText();
    expectLastCall().andReturn(filenameValue).times(noOfTimes);
    replay(filenameTextboxMock);
  }

}

