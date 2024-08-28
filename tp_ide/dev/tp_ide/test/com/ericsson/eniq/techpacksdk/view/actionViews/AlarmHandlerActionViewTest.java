package com.ericsson.eniq.techpacksdk.view.actionViews;

import javax.swing.JPasswordField;
import javax.swing.JTextField;
import junit.framework.TestCase;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.classextension.EasyMock.createNiceMock;
import static org.easymock.classextension.EasyMock.expectLastCall;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import org.junit.After;
import org.junit.Before;

/**
 * Test for AlarmHandlerActionView.
 * 
 * @author eciacah
 */
public class AlarmHandlerActionViewTest extends TestCase {

  // Test instance:
  private AlarmHandlerActionView testInstance = null;

  // Mocks:
  private JTextField interfaceId;

  private JTextField hostname;

  private JTextField username;

  private JPasswordField password;

  private JTextField authmethod;

  private JTextField maxDownloadThreads;

  private JTextField outputPath;

  private JTextField outputFilePrefix;

  private JTextField protocol;

  private JTextField cms;

  public AlarmHandlerActionViewTest() {
    super("AlarmHandlerActionViewTest");
  }

  @Override
  @Before
  protected void setUp() throws Exception {
    interfaceId = createNiceMock(JTextField.class);
    hostname = createNiceMock(JTextField.class);
    username = createNiceMock(JTextField.class);
    password = createNiceMock(JPasswordField.class);
    authmethod = createNiceMock(JTextField.class);
    maxDownloadThreads = createNiceMock(JTextField.class);
    outputPath = createNiceMock(JTextField.class);
    outputFilePrefix = createNiceMock(JTextField.class);
    
    protocol = createNiceMock(JTextField.class);
    cms = createNiceMock(JTextField.class);
    

    // Create new test instance:
    testInstance = new AlarmHandlerActionView(interfaceId, hostname, username, password, outputPath, outputFilePrefix,
        authmethod, maxDownloadThreads, protocol, cms);
  }

  @Override
  @After
  protected void tearDown() throws Exception {
    testInstance = null;
  }

  /**
   * Test that getContent() returns the correct information.
   * 
   * @throws Exception
   */
  public void testGetContent() throws Exception {

    final String interfaceIdValue = "test interface";
    final String protocolValue = "protocol";
    final String hostnameValue = "host name value";
    final String cmsValue = "cms value";
    final String usernameValue = "test_username";
    final String passwordValue = "test_password";
    final String outputPathValue = "testOutputPath";
    final String outputFilePrefixValue = "test prefix";
    final String authmethodValue = "authentication";
    final String maxDownloadThreadsValue = "max_download_value";

    // Set up expectations:
    interfaceId.getText();
    expectLastCall().andReturn(interfaceIdValue);
    replay(interfaceId);
    
    protocol.getText();
    expectLastCall().andReturn(protocolValue);
    replay(protocol);

    hostname.getText();
    expectLastCall().andReturn(hostnameValue);
    replay(hostname);
    
    cms.getText();
    expectLastCall().andReturn(cmsValue);
    replay(cms);

    username.getText();
    expectLastCall().andReturn(usernameValue);
    replay(username);

    password.getPassword();
    expectLastCall().andReturn(passwordValue.toCharArray());
    replay(password);

    authmethod.getText();
    expectLastCall().andReturn(authmethodValue);
    replay(authmethod);
    
    maxDownloadThreads.getText();
    expectLastCall().andReturn(maxDownloadThreadsValue);
    replay(maxDownloadThreads);
    
    outputPath.getText();
    expectLastCall().andReturn(outputPathValue);
    replay(outputPath);

    outputFilePrefix.getText();
    expectLastCall().andReturn(outputFilePrefixValue);
    replay(outputFilePrefix);

    String result = testInstance.getContent();
    
    verify(interfaceId);
    verify(protocol);
    verify(hostname);
    verify(cms);
    verify(username);
    verify(password);
    verify(authmethod);
    verify(maxDownloadThreads);
    verify(outputPath);  
    verify(outputFilePrefix); 
    
    assertTrue("Value should be stored for interfaceId", result.contains("interfaceId=" + interfaceIdValue));
    // protocol
    assertTrue("Value should be stored for protocol", result.contains("protocol=" + protocolValue));
    assertTrue("Value should be stored for hostname", result.contains("hostname=" + hostnameValue));
    // cms
    assertTrue("Value should be stored for cms", result.contains("cms=" + cmsValue));
    assertTrue("Value should be stored for username", result.contains("username=" + usernameValue));
    assertTrue("Value should be stored for password", result.contains("password=" + passwordValue));
    assertTrue("Value should be stored for authmethod", result.contains("authmethod=" + authmethodValue));
    assertTrue("Value should be stored for maxDownloadThreads",
        result.contains("maxDownloadThreads=" + maxDownloadThreadsValue));
    assertTrue("Value should be stored for outputPath", result.contains("outputPath=" + outputPathValue));
    assertTrue("Value should be stored for outputFilePrefix",
        result.contains("outputFilePrefix=" + outputFilePrefixValue));
  }

  /**
   * Test validate() method with incorrect value for maxDownloadThreads. Should return 'false'.
   */
  public void testValidateInvalidMaxDownloadThreads() {

    // Set up expectations:
    final String interfaceIdValue = "test interface";
    final String hostnameValue = "atrcx892zone3.athtem.eei.ericsson.se:8080";
    final String usernameValue = "test_username";
    final String passwordValue = "test_password";
    final String outputPathValue = "testOutputPath";
    final String outputFilePrefixValue = "test prefix";
    final String authmethodValue = "authentication";
    final String maxDownloadThreadsValue = "max_download_value";

    interfaceId.getText();
    expectLastCall().andReturn(interfaceIdValue);
    replay(interfaceId);

    hostname.getText();
    expectLastCall().andReturn(hostnameValue);
    replay(hostname);

    username.getText();
    expectLastCall().andReturn(usernameValue);
    replay(username);

    password.getPassword();
    expectLastCall().andReturn(passwordValue.toCharArray());
    replay(password);

    outputPath.getText();
    expectLastCall().andReturn(outputPathValue);
    replay(outputPath);

    outputFilePrefix.getText();
    expectLastCall().andReturn(outputFilePrefixValue);
    replay(outputFilePrefix);

    maxDownloadThreads.getText();
    expectLastCall().andReturn(maxDownloadThreadsValue);
    replay(maxDownloadThreads);

    final String result = testInstance.validate();

    assertTrue("Error message should be added if maxDownloadThreads value is invalid", result.length() > 0);
  }

  /**
   * Test validate() method with incorrect value for hostname. Should return 'false'.
   */
  public void testValidateInvalidHostname() {

    // Set up expectations:
    final String interfaceIdValue = "test interface";
    final String hostnameValue = "atrcx892zone3.athtem.eei.ericsson.se:8080";
    final String usernameValue = "test_username";
    final String passwordValue = "test_password";
    final String outputPathValue = "testOutputPath";
    final String outputFilePrefixValue = "test prefix";
    final String authmethodValue = "authentication";
    final String maxDownloadThreadsValue = "max_download_value";

    interfaceId.getText();
    expectLastCall().andReturn(interfaceIdValue);
    replay(interfaceId);

    hostname.getText();
    expectLastCall().andReturn(hostnameValue);
    replay(hostname);

    username.getText();
    expectLastCall().andReturn(usernameValue);
    replay(username);

    password.getPassword();
    expectLastCall().andReturn(passwordValue.toCharArray());
    replay(password);

    outputPath.getText();
    expectLastCall().andReturn(outputPathValue);
    replay(outputPath);

    outputFilePrefix.getText();
    expectLastCall().andReturn(outputFilePrefixValue);
    replay(outputFilePrefix);

    maxDownloadThreads.getText();
    expectLastCall().andReturn(maxDownloadThreadsValue);
    replay(maxDownloadThreads);

    final String result = testInstance.validate();

    assertTrue("Error message should be added if host name value is invalid", result.length() > 0);
  }

  /**
   * Test validate() method with correct values. Should return 'true'.
   */
  public void testValidate() {

    // Set up expectations:
    final String interfaceIdValue = "test interface";
    final String hostnameValue = "atrcx892zone3.athtem.eei.ericsson.se:8080";
    final String usernameValue = "test_username";
    final String passwordValue = "test_password";
    final String outputPathValue = "testOutputPath";
    final String outputFilePrefixValue = "test prefix";
    final String authmethodValue = "authentication";
    final String maxDownloadThreadsValue = "15";

    interfaceId.getText();
    expectLastCall().andReturn(interfaceIdValue);
    replay(interfaceId);

    hostname.getText();
    expectLastCall().andReturn(hostnameValue);
    replay(hostname);

    username.getText();
    expectLastCall().andReturn(usernameValue);
    replay(username);

    password.getPassword();
    expectLastCall().andReturn(passwordValue.toCharArray());
    replay(password);

    outputPath.getText();
    expectLastCall().andReturn(outputPathValue);
    replay(outputPath);

    outputFilePrefix.getText();
    expectLastCall().andReturn(outputFilePrefixValue);
    replay(outputFilePrefix);

    maxDownloadThreads.getText();
    expectLastCall().andReturn(maxDownloadThreadsValue);
    replay(maxDownloadThreads);

    final String result = testInstance.validate();

    assertTrue("There should be no error messages if the parameters are correct", result.length() == 0);
  }

}
