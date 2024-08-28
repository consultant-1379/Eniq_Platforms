package com.ericsson.eniq.techpacksdk;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;

@SuppressWarnings("serial")
public class NewUserDialog extends JDialog {

  public final Logger logger = Logger.getLogger(NewUserDialog.class.getName());

  private final SingleFrameApplication application;

  private final ResourceMap resourceMap;

  private final UserAdminPanel.UserTableModel utm;

  private final JTextField user;

  private final JPasswordField password;

  private final JComboBox role;

  private final JButton discardbutton;

  private final JButton addbutton;
  
  // 20110311, eanguan, LoginInUserAdminWindow IMP :: Added to save the JTable instance from UserAdminWindow
  private final JTable utable ;

  //20110311, eanguan, LoginInUserAdminWindow IMP :: Argument added to get the JTable from UserAdminWindow
  public NewUserDialog(final SingleFrameApplication application, final ResourceMap resourceMap,
      final UserAdminPanel.UserTableModel utm, final JTable jt) {

    super(application.getMainFrame(), true);
    this.utable = jt ;
    this.application = application;
    this.resourceMap = resourceMap;
    this.utm = utm;

    setTitle(resourceMap.getString("AddUserDialog.title"));

    final JPanel pane = new JPanel(new GridBagLayout());
    pane.setOpaque(true);

    final GridBagConstraints c = new GridBagConstraints();

    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2, 2, 2, 2);
    pane.add(new JLabel(this.resourceMap.getString("NewUserDialog.name")), c);

    c.gridx = 1;
    user = new LimitedSizeTextField("", 16, 255, true);
    pane.add(user, c);

    c.gridx = 0;
    c.gridy = 1;
    pane.add(new JLabel(resourceMap.getString("NewUserDialog.password")), c);

    c.gridx = 1;
    password = new LimitedSizePasswordField(8, 16, true);
    pane.add(password, c);

    c.gridx = 0;
    c.gridy = 2;
    pane.add(new JLabel(resourceMap.getString("NewUserDialog.role")), c);

    c.gridx = 1;
    role = new JComboBox();
    role.addItem(User.USER);
    role.addItem(User.RND);
    role.addItem(User.ADMIN);
    role.setRenderer(new RoleComboRenderer(resourceMap));
    pane.add(role, c);

    c.gridx = 0;
    c.gridy = 3;
    c.gridwidth = 2;
    c.fill = GridBagConstraints.HORIZONTAL;
    final JPanel bPanel = new JPanel(new GridBagLayout());
    pane.add(bPanel, c);

    c.gridx = 0;
    c.gridy = 0;
    c.gridwidth = 1;
    c.weightx = 1;
    c.fill = GridBagConstraints.NONE;
    c.anchor = GridBagConstraints.NORTHEAST;
    discardbutton = new JButton(application.getContext().getActionMap(this).get("discardnewuser"));
    bPanel.add(discardbutton, c);

    c.gridx = 1;
    c.weightx = 0;
    addbutton = new JButton(application.getContext().getActionMap(this).get("addnewuser"));
    addbutton.setEnabled(false);
    bPanel.add(addbutton, c);

    user.getDocument().addDocumentListener(new ButtonEnabler());
    password.getDocument().addDocumentListener(new ButtonEnabler());

    setContentPane(pane);
  }

  @Action
  public void addnewuser() {
    try {
      utm.addRow(user.getText(), password.getText(), (String) role.getSelectedItem());
      // 20110311, eanguan, LoginInUserAdminWindow IMP :: To select the above added user which 
      // will be added at last in the table
      utable.setRowSelectionInterval(utm.getRowCount()-1, utm.getRowCount()-1);
      this.setVisible(false); // Hide NewUserDialog window
    } catch (Exception e) {
      logger.log(Level.WARNING, "Add user failed", e);
      JOptionPane.showMessageDialog(application.getMainFrame(), "Add user failed", "Error",
          JOptionPane.ERROR_MESSAGE);
    }
  }

  @Action
  public void discardnewuser() {
    setVisible(false);
  }

  public class ButtonEnabler implements DocumentListener {

    public void changedUpdate(DocumentEvent e) {
      addbutton.setEnabled(user.getText().length() > 0 && password.getText().length() > 0);
    }

    public void insertUpdate(DocumentEvent e) {
      changedUpdate(e);
    }

    public void removeUpdate(DocumentEvent e) {
      changedUpdate(e);
    }

  };

}
