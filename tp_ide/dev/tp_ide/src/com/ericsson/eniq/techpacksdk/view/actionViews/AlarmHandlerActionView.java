package com.ericsson.eniq.techpacksdk.view.actionViews;


import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Properties;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.ericsson.eniq.component.ExceptionHandler;

/**
 * Copyright Distocraft 2006<br>
 * <br>
 * $id$
 * 
 * @author berggren
 */
public class AlarmHandlerActionView implements ActionView {

  private JTextField interfaceId;
  private JTextField protocol;
  private JTextField hostname;
  private JTextField cms;
  private JTextField username;
  private JPasswordField password;
  private JTextField authmethod;
  private JTextField maxDownloadThreads;
  private JTextField outputPath;
  private JTextField outputFilePrefix;

  //private Meta_transfer_actions action;

  private Properties orig;
  
  /**
   * Constructor to be used only for tests.
   */
  protected AlarmHandlerActionView(JTextField interfaceId, JTextField hostname, JTextField username, JPasswordField password,
      JTextField outputPath, JTextField outputFilePrefix, JTextField authmethod, JTextField maxDownloadThreads,
      JTextField protocol, JTextField cms) {
    this.interfaceId = interfaceId;
    this.protocol = protocol;
    this.hostname = hostname;
    this.cms = cms;
    this.username = username;
    this.password = password;
    this.authmethod = authmethod;
    this.maxDownloadThreads = maxDownloadThreads;
    this.outputPath = outputPath;
    this.outputFilePrefix = outputFilePrefix;  
  }

  public AlarmHandlerActionView(JPanel parent, Meta_transfer_actions action) {
    //this.action = action;

    orig = new Properties();

    if (action != null) {

      String act_cont = action.getAction_contents();

      if (act_cont != null && act_cont.length() > 0) {

        try {
          ByteArrayInputStream bais = new ByteArrayInputStream(act_cont.getBytes());
          orig.load(bais);
          bais.close();
        } catch (Exception e) {
          ExceptionHandler.instance().handle(e);
          e.printStackTrace();
        }
      }
    }

    parent.removeAll();

    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2, 2, 2, 2);
    c.weightx = 0;
    c.weighty = 0;
    c.gridy = 0;
    c.gridx = 0;
    
    JLabel l_interfaceId = new JLabel("Interface ID");
    l_interfaceId.setToolTipText("ID of an alarm interface.");
    parent.add(l_interfaceId, c);

    c.weightx = 1;
    c.gridx = 1;
    interfaceId = new JTextField(20);
    interfaceId.setToolTipText("ID of an alarm interface.");
    interfaceId.setText(orig.getProperty("interfaceId", ""));
    parent.add(interfaceId, c);

    c.gridy = GridBagConstraints.RELATIVE;
    c.gridx = 0;
    JLabel l_protocol = new JLabel("protocol");
    l_protocol.setToolTipText("Protocol to alarmcfg.");
    parent.add(l_protocol, c);
    protocol = new JTextField(20);
    protocol.setToolTipText("Protocol to alarmcfg.");
    protocol.setText(orig.getProperty("protocol", "http"));
    c.weightx = 1;
    c.gridx = 1;
    parent.add(protocol, c);
    
    c.gridy = GridBagConstraints.RELATIVE;
    c.gridx = 0;
    JLabel l_host = new JLabel("Hostname");
    l_host.setToolTipText("Address of Alarmcfg host. Format ipaddress:port_number.");
    parent.add(l_host, c);
    c.weightx = 1;
    c.gridx = 1;
    hostname = new JTextField(20);
    hostname.setToolTipText("Address of Alarmcfg host. Format ipaddress:port_number.");
    hostname.setText(orig.getProperty("hostname", ""));
    parent.add(hostname, c);

    c.gridy = GridBagConstraints.RELATIVE;
    c.gridx = 0;
    JLabel l_cms = new JLabel("CMS");
    l_cms.setToolTipText("Address of CMS. Format ipaddress:port_number.");
    parent.add(l_cms, c);
    cms = new JTextField(20);
    cms.setToolTipText("Address of CMS.  Format ipaddress:port_number.");
    cms.setText(orig.getProperty("CMS", "webportal:6400"));
    c.weightx = 1;
    c.gridx = 1;
    parent.add(cms, c);
    
    c.gridy = GridBagConstraints.RELATIVE;
    c.gridx = 0;
    JLabel l_username = new JLabel("Username");
    l_username.setToolTipText("Username of alarmcfg.");
    parent.add(l_username, c);
    username = new JTextField(20);
    username.setToolTipText("Username of alarmcfg.");
    username.setText(orig.getProperty("username", ""));
    c.weightx = 1;
    c.gridx = 1;
    parent.add(username, c);
    
    c.gridy = GridBagConstraints.RELATIVE;
    c.gridx = 0;
    JLabel l_password = new JLabel("Password");
    l_password.setToolTipText("Password of alarmcfg.");
    parent.add(l_password, c);
    c.weightx = 1;
    c.gridx = 1;
    password = new JPasswordField(20);
    password.setToolTipText("Password of alarmcfg.");
    password.setText(orig.getProperty("password", ""));
    parent.add(password, c);

    c.gridy = GridBagConstraints.RELATIVE;
    c.gridx = 0;
    JLabel l_authmethod = new JLabel("Authmethod");
    l_authmethod.setToolTipText("Authentication method of alarmcfg.");
    parent.add(l_authmethod, c);
    c.weightx = 1;
    c.gridx = 1;
    authmethod = new JTextField(20);
    authmethod.setToolTipText("Authentication method of alarmcfg.");
    authmethod.setText(orig.getProperty("authmethod", ""));
    parent.add(authmethod, c);
    
    // Max download threads:
    c.gridy = GridBagConstraints.RELATIVE;
    c.gridx = 0;
    JLabel l_maxDownloadThreads = new JLabel("Max Download Threads");
    l_authmethod.setToolTipText("Maximum number of download threads");
    parent.add(l_maxDownloadThreads, c);
    c.weightx = 1;
    c.gridx = 1;
    maxDownloadThreads = new JTextField(20);
    maxDownloadThreads.setToolTipText("Maximum number of download threads");
    maxDownloadThreads.setText(orig.getProperty("maxDownloadThreads", ""));
    parent.add(maxDownloadThreads, c);

    c.gridy = GridBagConstraints.RELATIVE;
    c.gridx = 0;
    JLabel l_outputPath = new JLabel("Filepath to save reports");
    l_outputPath.setToolTipText("Input directory of alarm adapter.");
    parent.add(l_outputPath, c);
    c.weightx = 1;
    c.gridx = 1;
    outputPath = new JTextField(30);
    outputPath.setToolTipText("Input directory of alarm adapter.");
    outputPath.setText(orig.getProperty("outputPath", ""));
    parent.add(outputPath, c);

    c.gridy = GridBagConstraints.RELATIVE;
    c.gridx = 0;
    JLabel l_outputFilePrefix = new JLabel("File prefix");
    l_outputFilePrefix.setToolTipText("Filename prefix of alarm report file.");
    parent.add(l_outputFilePrefix, c);
    c.weightx = 1;
    c.gridx = 1;
    outputFilePrefix = new JTextField(20);
    outputFilePrefix.setText(orig.getProperty("outputFilePrefix", ""));
    outputFilePrefix.setToolTipText("Filename prefix of alarm report file.");
    parent.add(outputFilePrefix, c);

    parent.invalidate();
    parent.revalidate();
    parent.repaint();
  }

  public String getType() {
    return "AlarmHandler";
  }

  public String validate() {
    String error = "";
    
    if(interfaceId.getText().length() <= 0)
      error += "Parameter InterfaceId must be defined\n";
    
    String hs = hostname.getText();
    if(hs.length() <= 0)
      error += "Parameter Host must be defined\n";
    if(hs.indexOf(":") <= 0) {
      error += "Parameter Host: Format is hostname:portnumber\n";
    } else {
      String hhost = hs.substring(0,hs.indexOf(":"));
      String hport = hs.substring(hs.indexOf(":")+1);
      
      if(hhost.length() <= 0) 
        error += "Parameter Host: Format is hostname:portnumber (hostname missing)\n";
      
      try {
        int i = Integer.parseInt(hport);
        
        if(i <= 0 || i > 65536)
          throw new NumberFormatException();
      } catch(NumberFormatException nfe) {
          error += "Parameter Host: Format is hostname:portnumber (portnumber missing or invalid)\n";
      }
      
    }
    
    if(username.getText().length() <= 0)
      error += "Parameter Username must be defined\n";
    
    if(password.getPassword().toString().length() <= 0)
      error += "Parameter Password must be defined\n";
    
    if(outputPath.getText().length() <= 0)
      error += "Parameter Filepath to save reports must be defined\n";
    
    if(outputFilePrefix.getText().length() <= 0)
      error += "Parameter File prefix must be defined\n";
      
    // Check maxDownloadThreads parameter:
    final String maxThreadsString = maxDownloadThreads.getText();
    try {
      final int numberOfThreads = Integer.parseInt(maxThreadsString);
    } catch (NumberFormatException nfe) {
      error += "Parameter Max Download Threads must be a valid number\n";
    }
        
    return error;
  }
  
  public String getContent() throws Exception {
    Properties p = new Properties();
    p.setProperty("interfaceId", interfaceId.getText());
    p.setProperty("protocol", new String(protocol.getText()));
    p.setProperty("hostname", hostname.getText());
    p.setProperty("cms", cms.getText());
    p.setProperty("username", username.getText());
    p.setProperty("password", new String(password.getPassword()));
    // authmethod:
    p.setProperty("authmethod", authmethod.getText());
    // maxDownloadThreads:
    p.setProperty("maxDownloadThreads", maxDownloadThreads.getText());
    p.setProperty("outputPath", outputPath.getText());
    p.setProperty("outputFilePrefix", outputFilePrefix.getText());
    
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    p.store(baos, "");

    return baos.toString();
  }

  public String getWhere() throws Exception {
    return "";
  }

  public boolean isChanged() {
    return true;
  }

}
