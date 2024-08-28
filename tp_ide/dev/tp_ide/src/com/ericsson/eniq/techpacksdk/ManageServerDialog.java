package com.ericsson.eniq.techpacksdk;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.Task;
import org.jdesktop.application.Task.BlockingScope;

import com.ericsson.eniq.repository.AsciiCrypter;

@SuppressWarnings("serial")
public class ManageServerDialog extends JDialog {

  public final Logger log = Logger.getLogger(ManageServerDialog.class.getName());

  private final SingleFrameApplication application;

  private final ResourceMap resourceMap;

  private final Properties props;

  private final JPanel pane;

  private final JComboBox hostname;

  private final JTextField user;

  private final JPasswordField password;
  
  //For 17B changes
  private final JTextField dBEtlUser;
  private final JPasswordField dBEtlPwd;

  private final JButton testbutton;

  private final JButton deletebutton;

  private final JButton newbutton;

  private final JButton closedbutton;

  private final JButton savebutton;

  private final JButton discardbutton;

  private boolean dirty = false;

  private String dbUrl;

  private String dbDriver;

  public ManageServerDialog(final SingleFrameApplication application, final ResourceMap resourceMap,
      final Properties props, final String initialServer, final String dbUrl, final String dbDriver) {
    super(application.getMainFrame(), true);

    //20111017 eanguan :: SMF IP :: Removing the port number dependenciey, Instaed of ETLREP DB details now user has to enter Server details
    this.application = application;
    this.resourceMap = resourceMap;
    this.props = props;
    this.dbUrl = dbUrl;
    this.dbDriver = dbDriver;

    setTitle(resourceMap.getString("ManageServerDialog.title"));

    pane = new JPanel(new GridBagLayout());
    pane.setOpaque(true);

    final GridBagConstraints c = new GridBagConstraints();

    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2, 2, 2, 2);
    pane.add(new JLabel(resourceMap.getString("ManageServerDialog.hostname")), c);

    c.gridx = 1;
    hostname = new JComboBox();
    hostname.setEditable(false);
    hostname.setRenderer(new ServerComboRenderer(resourceMap.getImageIcon("DataConnection.icon")));
    hostname.addActionListener(new HostNameListener());

    pane.add(hostname, c);

//    c.gridx = 0;
//    c.gridy = 1;
    //pane.add(new JLabel(resourceMap.getString("ManageServerDialog.port")), c);

//    c.gridx = 1;
//    port = new LimitedRangeIntegerField(5, 1, 32000, true);
//    port.setToolTipText("Please enter the RepDB database port");
    //pane.add(port, c);

    c.gridx = 0;
    c.gridy = 2;
    pane.add(new JLabel(resourceMap.getString("ManageServerDialog.user")), c);

    c.gridx = 1;
    user = new LimitedSizeTextField(8, 32, true);
    user.setToolTipText("Please enter the user name by which login is possible into the server.");
    pane.add(user, c);

    c.gridx = 0;
    c.gridy = 3;
    pane.add(new JLabel(resourceMap.getString("ManageServerDialog.password")), c);

    c.gridx = 1;
    password = new LimitedSizePasswordField(8, 32, true);
    password.setToolTipText("Please enter the password for the above username.");
    password.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent ae) {
        if (password.getText().length() > 0) {
          final javax.swing.Action a = application.getContext().getActionMap(ManageServerDialog.this).get(
              "savemanageserverdialog");
          a.actionPerformed(ae);
          final javax.swing.Action c = application.getContext().getActionMap(ManageServerDialog.this).get(
              "closemanageserverdialog");
          c.actionPerformed(ae);
        }
      }
    });
    pane.add(password, c);

    //For 17B changes
    c.gridx = 0;
    c.gridy = 4;
    pane.add(new JLabel(resourceMap.getString("ManageServerDialog.etluser")), c);
    c.gridx = 1;
    dBEtlUser = new LimitedSizeTextField(8, 32, true);
    dBEtlUser.setToolTipText("Please enter the etlrep username.");
    pane.add(dBEtlUser, c);
    
    c.gridx = 0;
    c.gridy = 5;
    pane.add(new JLabel(resourceMap.getString("ManageServerDialog.etlpassword")), c);
    c.gridx = 1;
    dBEtlPwd = new LimitedSizePasswordField(8, 32, true);
    dBEtlPwd.setToolTipText("Please enter the etlrep password.");
    pane.add(dBEtlPwd, c);
    //End of 17B
    
    c.gridx = 0;
    c.gridy = 6;
    c.gridwidth = 2;
    c.fill = GridBagConstraints.HORIZONTAL;
    final JPanel bPanel = new JPanel(new GridBagLayout());
    pane.add(bPanel, c);

    c.gridx = 0;
    c.gridy = 0;
    c.gridwidth = 1;
    c.weightx = 0;
    c.fill = GridBagConstraints.NONE;
    testbutton = new JButton(application.getContext().getActionMap(this).get("testserver"));
    bPanel.add(testbutton, c);

    c.gridx = 1;
    deletebutton = new JButton(application.getContext().getActionMap(this).get("deleteserver"));
    bPanel.add(deletebutton, c);

    c.gridx = 2;
    newbutton = new JButton(application.getContext().getActionMap(this).get("newserver"));
    bPanel.add(newbutton, c);

    c.gridx = 3;
    discardbutton = new JButton(application.getContext().getActionMap(this).get("discardmanageserverdialog"));
    bPanel.add(discardbutton, c);

    c.gridx = 4;
    savebutton = new JButton(application.getContext().getActionMap(this).get("savemanageserverdialog"));
    bPanel.add(savebutton, c);

    c.gridx = 5;
    closedbutton = new JButton(application.getContext().getActionMap(this).get("closemanageserverdialog"));
    bPanel.add(closedbutton, c);

    loadServers();

    if (initialServer != null) {
      for (int i = 0; i < hostname.getItemCount(); i++) {
        if (hostname.getItemAt(i).equals(initialServer)) {
          hostname.setSelectedIndex(i);
        }
      }
    }

    if (hostname.getItemCount() <= 0) {
      hostname.setEnabled(false);
//      port.setEnabled(false);
      user.setEnabled(false);
      password.setEnabled(false);
      testbutton.setEnabled(false);
      deletebutton.setEnabled(false);
      newbutton.setEnabled(true);
      savebutton.setEnabled(false);
    }

//    port.getDocument().addDocumentListener(new ButtonEnabler());
    user.getDocument().addDocumentListener(new ButtonEnabler());
    password.getDocument().addDocumentListener(new ButtonEnabler());

    updateUIState();

    setContentPane(pane);

  }

  public String getSelectedServer() {
    return (String) hostname.getSelectedItem();
  }

  @Action(block = BlockingScope.APPLICATION)
  public Task testserver() {
	
	// Test the connection with the current values in the dialog with the
    // database driver and the temporary dbUrl.
    final Task testservertask = new TestServerTask(application, resourceMap, (String) hostname.getSelectedItem(),
        user.getText(), password.getText(), dbUrl, dbDriver, props, dBEtlUser.getText(), dBEtlPwd.getText());
    BusyIndicator busyIndicator = new BusyIndicator();
    this.setGlassPane(busyIndicator);
    testservertask.setInputBlocker(new BusyIndicatorInputBlocker(testservertask, busyIndicator));
    return testservertask;
  }

  @Action
  public void newserver() {

    final NewServerDialog nsd = new NewServerDialog(application, resourceMap);

    final String newServ = nsd.getServerName();

    if (newServ.length() > 0) {
      for (int i = 0; i < hostname.getItemCount(); i++) {
        if (hostname.getItemAt(i).equals(newServ)) {
          JOptionPane.showMessageDialog(application.getMainFrame(), resourceMap
              .getString("ManageServerDialog.alreadyAdded.message"), resourceMap
              .getString("ManageServerDialog.alreadyAdded.title"), JOptionPane.INFORMATION_MESSAGE);
          return; // Already added
        }
      }

      hostname.addItem(newServ);
      hostname.setSelectedItem(newServ);
      hostname.setEnabled(true);
      user.setText("dcuser");
      user.setEnabled(true);
//      port.setText("2641");
//      port.setEnabled(true);
      password.setText("dcuser");
      password.setEnabled(true);
      password.requestFocus();

      dirty = true;

      updateUIState();

    } // else cancelled or left empty

  }

  @Action
  public void deleteserver() {

    String key = (String) hostname.getSelectedItem();

    int selectedValue = JOptionPane.showConfirmDialog(null, "Are you sure that you want to remove server " + key,
        "Remove server?", JOptionPane.YES_NO_OPTION);

    if (selectedValue == JOptionPane.YES_OPTION) {

      props.remove(key + ".host");
      props.remove(key + ".port");
//      port.setText("");
      props.remove(key + ".user");
      user.setText("");
      props.remove(key + ".pass");
      password.setText("");

      hostname.removeItem(key);

      if (hostname.getItemCount() <= 0) {
        hostname.setEnabled(false);
//        port.setEnabled(false);
        user.setEnabled(false);
        password.setEnabled(false);
        newbutton.setEnabled(true);
      }

      dirty = false;
    }

    updateUIState();
  }

  @Action
  public void closemanageserverdialog() {
    if (dirty) {
      if (hostname.getSelectedIndex() >= 0 && user.getText().length() > 0
          && password.getText().length() > 0) {
        if (JOptionPane.showConfirmDialog(application.getMainFrame(), resourceMap
            .getString("ManageServer.IgnoreConfirm.message"),
            resourceMap.getString("ManageServer.IgnoreConfirm.title"), JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE) == 0) {
          savemanageserverdialog();
        }
      } else if (JOptionPane.showConfirmDialog(application.getMainFrame(), resourceMap
          .getString("ManageServer.CloseConfirm.message"), resourceMap.getString("ManageServer.CloseConfirm.title"),
          JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == 1) {
        return;
      }

    }

    setVisible(false);
  }

  @Action
  public void discardmanageserverdialog() {
    loadServers();
  }

  @Action
  public void savemanageserverdialog() {
    final String host = (String) hostname.getSelectedItem();
    final String srvUser = user.getText().trim() ;
    final String srvPass = password.getText().trim() ;
    // For 17B changes
    final String etlUser = dBEtlUser.getText().trim();
    final String etlPwd = dBEtlPwd.getText().trim();
    
    String encSrvPass = null ;
	//Encrypting the password for server
	try{
		encSrvPass = AsciiCrypter.getInstance().encrypt(srvPass.trim());
	}catch(final Exception e){
		//encryption fails, saving the password in text only
		final String errMsg = "Failed to encrypt the password. Can not save server details." ;
		log.log(Level.SEVERE, errMsg, e);
		JOptionPane.showMessageDialog(application.getMainFrame(), errMsg, resourceMap
		        .getString("ManageServerDialog.test.failed.title"), JOptionPane.ERROR_MESSAGE);
		return ;
	}
	if(encSrvPass == null || encSrvPass.length() <= 0){
		final String errMsg = "Failed to encrypt the password. Can not save server details." ;
		log.severe(errMsg);
		JOptionPane.showMessageDialog(application.getMainFrame(), errMsg, resourceMap
		        .getString("ManageServerDialog.test.failed.title"), JOptionPane.ERROR_MESSAGE);
		return ;
	}
	
	//Remove if port property is there
	if(props.getProperty(host + ".port", null) != null){
		props.remove(host + ".port");
	}
	//Saving the info in IDE properties file
	props.setProperty(host + ".host", host);
	props.setProperty(host + ".user", srvUser.trim());
	props.setProperty(host + ".pass", encSrvPass.trim());
	// For 17B changes
	props.setProperty(host + ".etlUser", etlUser);
	props.setProperty(host + ".etlPwd", etlPwd);
	
    dirty = false;

    updateUIState();
  }

  private void loadServers() {

    final String orgHost = (String) hostname.getSelectedItem();

    hostname.removeAllItems();

    final Enumeration<Object> e = props.keys();
    while (e.hasMoreElements()) {
      String key = (String) e.nextElement();
      if (key.endsWith(".host")) {
        hostname.addItem(props.getProperty(key));
      }
    }

    if (orgHost != null) {
      for (int i = 0; i < hostname.getItemCount(); i++) {
        if (orgHost.equals(hostname.getItemAt(i))) {
          hostname.setSelectedIndex(i);
        }
      }

    }

  }

  private void updateUIState() {
    deletebutton.setEnabled(hostname.getSelectedIndex() >= 0);
    newbutton.setEnabled(!dirty);
    testbutton.setEnabled(hostname.getSelectedIndex() >= 0 && user.getText().length() > 0 && password.getText().length() > 0);
    savebutton.setEnabled(dirty && testbutton.isEnabled());
    discardbutton.setEnabled(dirty);
    hostname.setEnabled(!dirty && hostname.getItemCount() > 0);
    pack();
  }

  public final class ButtonEnabler implements DocumentListener {

    public void changedUpdate(DocumentEvent e) {
      dirty = true;
      updateUIState();
    }

    public void insertUpdate(DocumentEvent e) {
      changedUpdate(e);
    }

    public void removeUpdate(DocumentEvent e) {
      changedUpdate(e);
    }

  };

  public final class HostNameListener implements ActionListener {

    public void actionPerformed(final ActionEvent ae) {

      String key = (String) hostname.getSelectedItem();
      
      
    //Check whether the property set for this server is old or new
      if(props.getProperty(key + ".port") != null){
      	//Entry is old needs to migrate
//    	JOptionPane.showMessageDialog(application.getMainFrame(), "IDE connection properties needs to be migrated for this server: " + key,
//    			resourceMap.getString("ManageServerDialog.wrong.connection.info"),  JOptionPane.INFORMATION_MESSAGE);
      	user.setText("");
  		password.setText("");
  		dirty = false;
        updateUIState();
  		return ; 
      }
  
//      String tport = props.getProperty(key + ".port", null);
//      if (tport != null) {
//        port.setText(tport);
//      }

      String tuser = props.getProperty(key + ".user", null);
      if (tuser != null) {
        user.setText(tuser);
      }

      String tpass = props.getProperty(key + ".pass", null);
      if (tpass != null) {
    	  //First decrypt the password then show it
    	  String srvPassword = tpass ;
		  //Decrypt the password first
		  try{
		  	srvPassword = AsciiCrypter.getInstance().decrypt(srvPassword);
		  }catch(final Exception e){
		  	//decryption fails, may be the password was not encrypted
		  	log.log(Level.WARNING, "Failed to decrypt the password.", e);
		  }
		  if (srvPassword == null || srvPassword.length() <= 0 || srvPassword.equals(tpass)) {
			  log.warning("Failed to decrypt the password.");
		  }
		  password.setText(srvPassword);
      }

      dirty = false;

      updateUIState();

    }
  };

}
