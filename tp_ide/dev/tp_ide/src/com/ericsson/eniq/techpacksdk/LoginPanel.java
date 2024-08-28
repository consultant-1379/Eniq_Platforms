package com.ericsson.eniq.techpacksdk;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.Task;
import org.jdesktop.application.Task.BlockingScope;

@SuppressWarnings("serial")
public class LoginPanel extends JPanel {

  final private Logger log = Logger.getLogger(LoginPanel.class.getName());

  final private TechPackIDE tpide;

  final private ResourceMap resourceMap;

  final private Properties props;

  private JComboBox server;

  private JTextField user;

  private JPasswordField pass;

  private JTextField wdir;

  private JButton loginbutton;

  private JButton quitbutton;

  private String dbUrl;

  private String dbDriver;

  public LoginPanel(TechPackIDE tpide, final ResourceMap resourceMap, final Properties props) {
    this.tpide = tpide;
    this.resourceMap = resourceMap;
    this.props = props;

    // Get the database URL and driver from the properties. The values can be
    // overridden e.g. for unit testing purposes with the setters.
    dbDriver = resourceMap.getString("etlDbDriverName");
    dbUrl = resourceMap.getString("etlDbURL");
    
    setLayout(new GridBagLayout());

    final GridBagConstraints c = new GridBagConstraints();

    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2, 2, 2, 2);
    c.gridwidth = 5;
    // add(new JLabel(resourceMap.getString("LoginPanel.title")),c);
    c.gridwidth = 1;
    c.gridy = 1;
    add(new JLabel(resourceMap.getString("LoginPanel.server")), c);

    c.gridx = 1;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    server = new JComboBox();
    server.setEditable(false);
    Vector<String> serverList = new Vector<String>();
    final Enumeration<Object> e = props.keys();
    while (e.hasMoreElements()) {
      String key = (String) e.nextElement();
      if (key.endsWith(".host")) {
    	  serverList.add(props.getProperty(key));
      }
    }
    // Sort server list.
    Collections.sort(serverList);
    server = new JComboBox(serverList);
    server.setEditable(false);
    server.setName("ServerField");
    server.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent ae) {
        user.setText(props.getProperty(server.getSelectedItem() + ".lastuser", ""));
        pass.setText("");
        updateUIstate();
      }
    });

    server.setRenderer(new ServerComboRenderer(resourceMap.getImageIcon("DataConnection.icon")));

    add(server, c);

    c.gridx = 2;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    final JButton manage = new JButton(tpide.getContext().getActionMap(this).get("manageservers"));
    add(manage, c);

    c.gridx = 0;
    c.gridy = 2;
    add(new JLabel(resourceMap.getString("LoginPanel.user")), c);

    c.gridx = 1;
    c.weightx = 1;
    c.fill = GridBagConstraints.BOTH;
    user = new LimitedSizeTextField("", 16, 255, true);
    user.setName("UserField");
    user.getDocument().addDocumentListener(new UsernameFieldValidator());
    add(user, c);

    c.gridx = 0;
    c.gridy = 3;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    add(new JLabel(resourceMap.getString("LoginPanel.password")), c);

    c.gridx = 1;
    c.weightx = 1;
    c.fill = GridBagConstraints.BOTH;
    pass = new LimitedSizePasswordField(8, 16, true);
    pass.setName("PasswordField");
    pass.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent ae) {
        if (pass.getPassword().length > 0) {
          final javax.swing.Action a = LoginPanel.this.tpide.getContext().getActionMap(LoginPanel.this).get("login");
          a.actionPerformed(ae);
        }
      }
    });
    pass.getDocument().addDocumentListener(new PasswordFieldValidator());
    add(pass, c);

    c.gridx = 0;
    c.gridy = 4;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    add(new JLabel(resourceMap.getString("LoginPanel.workDir")), c);

    c.gridx = 1;
    c.weightx = 1;
    c.fill = GridBagConstraints.BOTH;
    wdir = new JTextField(props.getProperty("workingDir", System.getProperty("user.home")), 16);
    wdir.setEditable(false);
    add(wdir, c);

    c.gridx = 2;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    add(new JButton(tpide.getContext().getActionMap(this).get("selectworkingdir")), c);

    c.gridx = 1;
    c.gridy = 5;
    c.weighty = 1;
    c.fill = GridBagConstraints.VERTICAL;
    add(Box.createRigidArea(new Dimension(0, 0)), c);

    c.gridx = 0;
    c.gridy = 6;
    c.weighty = 0;
    c.gridwidth = 4;
    c.fill = GridBagConstraints.HORIZONTAL;
    final JPanel bPanel = new JPanel(new GridBagLayout());
    add(bPanel, c);

    c.gridx = 0;
    c.gridy = 0;
    c.gridwidth = 1;
    c.weightx = 0;
    c.fill = GridBagConstraints.VERTICAL;
    bPanel.add(new JButton(tpide.getContext().getActionMap(this).get("aboutide")), c);

    c.gridx = 1;
    c.gridy = 0;
    c.gridwidth = 1;
    c.weightx = 0;
    c.fill = GridBagConstraints.VERTICAL;
    bPanel.add(new JButton(tpide.getContext().getActionMap(this).get("checkLocalSetup")), c);
    
    c.gridx = 2;
    bPanel.add(new JButton(tpide.getContext().getActionMap(this).get("showoptionsdialog")), c);

    c.gridx = 3;
    c.anchor = GridBagConstraints.NORTHEAST;
    c.weightx = 1;
    loginbutton = new JButton(tpide.getContext().getActionMap(this).get("login"));
    loginbutton.setName("LoginButton");
    loginbutton.setEnabled(false);
    bPanel.add(loginbutton, c);

    c.gridx = 4;
    c.anchor = GridBagConstraints.NORTHWEST;
    c.weightx = 0;
    quitbutton = new JButton(tpide.getContext().getActionMap(this).get("quit"));
    quitbutton.setName("QuitButton");
    bPanel.add(quitbutton, c);
    

    String lastserver = props.getProperty("lastServer", null);
    for (int i = 0; i < server.getItemCount(); i++) {
      if (((String) server.getItemAt(i)).equals(lastserver)) {
        server.setSelectedIndex(i);
        break;
      }
    }

    //20110311, eanguan, LoginInUserAdminWindow IMP :: Remove final so as to make it changeable
    String lastuser = props.getProperty(server.getSelectedItem() + ".lastuser");
    
    if (lastuser != null) {
    	//20110311, eanguan, LoginInUserAdminWindow IMP :: To set the user name
        // selected in UserAdminTableWindow
    	if(this.tpide.uap != null && this.tpide.uap.getUserName() != null && this.tpide.uap.getUserName() != ""){
    		lastuser = this.tpide.uap.getUserName() ;
    	}
      user.setText(lastuser);
    }

    final String lastworkingdir = props.getProperty(server.getSelectedItem() + ".lastworkingdir");
    if (lastworkingdir != null) {
      wdir.setText(lastworkingdir);
    }

    this.setName("com.ericsson.eniq.techpacksdk.LoginPanel");

    updateUIstate();

  }

  @Action
  public void manageservers() {

    final ManageServerDialog msd = new ManageServerDialog(tpide, resourceMap, props, (String) server.getSelectedItem(), dbUrl, dbDriver);
    msd.pack();
    msd.setVisible(true);

    storeConnectionProperties();

    server.removeAllItems();
    final Enumeration<Object> e = props.keys();
    while (e.hasMoreElements()) {
      String key = (String) e.nextElement();
      if (key.endsWith(".host")) {
        server.addItem(props.getProperty(key));
      }
    }

    for (int i = 0; i < server.getItemCount(); i++) {
      if (server.getItemAt(i).equals(msd.getSelectedServer())) {
        server.setSelectedIndex(i);
        user.setText(props.getProperty(server.getSelectedItem() + ".lastuser", ""));
      }
    }

  }

  @Action
  public void selectworkingdir() {

    while (true) {
      final JFileChooser jfc = new JFileChooser(new File(wdir.getText()));
      jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
      jfc.setFileFilter(new FileFilter() {

        public boolean accept(File f) {
          return f.isDirectory() && f.exists() && f.canWrite();
        }

        public String getDescription() {
          return resourceMap.getString("LoginPanel.filedescription");
        }
      });

      final int returnVal = jfc.showDialog(this, resourceMap.getString("LoginPanel.filechoose"));

      if (returnVal == JFileChooser.APPROVE_OPTION) {
        final File f = jfc.getSelectedFile();

        if (!f.isDirectory() || !f.exists() || !f.canWrite()) {
          JOptionPane.showMessageDialog(tpide.getMainFrame(), resourceMap.getString("Login.workingdirerror.message"),
              resourceMap.getString("Login.workingdirerror.title"), JOptionPane.ERROR_MESSAGE);
          continue;
        }

        wdir.setText(f.getAbsolutePath());

      }

      break;

    }

  }

  @Action
  public void showoptionsdialog() {
    new OptionsDialog((Application) tpide, tpide.getMainFrame(), props, resourceMap);
  }

  @Action(block = BlockingScope.APPLICATION)
  public Task<Void, Void> login() {
    props.setProperty(server.getSelectedItem() + ".lastuser", user.getText());
    props.setProperty(server.getSelectedItem() + ".lastworkingdir", wdir.getText());

    props.setProperty("lastServer", (String) server.getSelectedItem());

    storeConnectionProperties();

    final Task<Void, Void> logintask = new LoginTask(tpide, resourceMap, this, dbUrl, dbDriver);
    BusyIndicator busyIndicator = new BusyIndicator();
    tpide.getMainFrame().setGlassPane(busyIndicator);
    logintask.setInputBlocker(new BusyIndicatorInputBlocker(logintask, busyIndicator));

    return logintask;
  }

  private void updateUIstate() {
    if (server.getSelectedItem() != null && user.getText().length() > 0 && pass.getPassword().length > 0
        && wdir.getText().length() > 0) {
      loginbutton.setEnabled(true);
    } else {
      loginbutton.setEnabled(false);
    }

  }

  private void storeConnectionProperties() {
    FileOutputStream fos = null;
    File target = new File(TechPackIDE.CONPROPS_FILE);
    try {
      log.fine("Storing values in " + target.getAbsolutePath());
      fos = new FileOutputStream(target);
      props.store(fos, "Stored by TPIDE " + (new SimpleDateFormat("dd.MM.yyyy HH:mm:ss")).format(new Date()));
    } catch (Exception e) {
      log.log(Level.WARNING, "Failed to write connection properties file to " + target.getAbsolutePath(), e);
    } finally {
      try {
        fos.close();
      } catch (Exception e) {
      }
    }
  }

  public class UsernameFieldValidator implements DocumentListener {

    public void changedUpdate(DocumentEvent e) {
      updateUIstate();
    }

    public void insertUpdate(DocumentEvent e) {
      updateUIstate();
    }

    public void removeUpdate(DocumentEvent e) {
      updateUIstate();
    }

  }

  public class PasswordFieldValidator implements DocumentListener {

    public void changedUpdate(DocumentEvent e) {
      updateUIstate();
    }

    public void insertUpdate(DocumentEvent e) {
      updateUIstate();
    }

    public void removeUpdate(DocumentEvent e) {
      updateUIstate();
    }

  }
  
  public Properties getProp(){
	  return props ;
  }

  public String getServer() {
    return (String) server.getSelectedItem();
  }

  public String getETLDBPort() {
    return props.getProperty(getServer() + ".port");
  }

  public String getETLDBUser() {
    return props.getProperty(getServer() + ".etlUser");
  }

  public String getETLDBPass() {
    return props.getProperty(getServer() + ".etlPwd");
  }
  
  public String getSrvUser() {
	    return props.getProperty(getServer() + ".user");
  }

  public String getSrvPass() {
	    return props.getProperty(getServer() + ".pass" );
  }

  public String getUser() {
    return user.getText();
  }

  public char[] getPass() {
    return pass.getPassword();
  }

  public File getWdir() {
    return new File(wdir.getText());
  }

  /**
   * @return the database URL (etlDbUrl).
   */
  public String getDbUrl() {
    return dbUrl;
  }

  /**
   * This sets the etlDbURL which is used in the login task for connecting to
   * the ETL repository database. This method is used in unit testing only, as
   * it overrides any value defined in the TechPackIDE.properties file.
   * 
   * @param url
   */
  public void setDbUrl(String dbUrl) {
    this.dbUrl = dbUrl;
  }

  /**
   * @return the database driver
   */
  public String getDbDriver() {
    return dbDriver;
  }

  /**
   * This sets the database driver used in the login task for connecting to the
   * ETL repository database. This method is used in unit testing only, as it
   * overrides any value defined in the TechPackIDE.properties file.
   * 
   * @param dbDriver
   */
  public void setDbDriver(String dbDriver) {
    this.dbDriver = dbDriver;
  }
}
