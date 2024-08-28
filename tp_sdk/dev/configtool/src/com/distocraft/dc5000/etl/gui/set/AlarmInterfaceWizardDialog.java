package com.distocraft.dc5000.etl.gui.set;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.gui.ConfigTool;
import com.distocraft.dc5000.etl.gui.ErrorDialog;
import com.distocraft.dc5000.etl.gui.SwingWorker;
import com.distocraft.dc5000.etl.gui.UI;
import com.distocraft.dc5000.etl.gui.setwizard.CreateAlarmAdapter;
import com.distocraft.dc5000.etl.gui.setwizard.CreateAlarmDirectoryChecker;
import com.distocraft.dc5000.etl.gui.setwizard.CreateAlarmDiskmanager;
import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_collectionsFactory;

/**
 * This class contains the functionality and ui for the Alarm Interface Wizard dialog window.
 * Copyright Distocraft 2006 <br>
 * <br>
 * $id$
 * 
 * @author berggren
 */
public class AlarmInterfaceWizardDialog extends JDialog implements TreeSelectionListener {

  private JFrame frame;

  private RockFactory rock;

  private Meta_collection_sets targetMetaCollectionSet;

  private JDialog jd;

  private UI ui;

  private JButton generate;

  private JTextField interfaceNameTextField = new JTextField("", 20);

  private JTextField descriptionTextField = new JTextField("", 20);

  private JTextField hostTextField = new JTextField("", 20);

  private JTextField usernameTextField = new JTextField("", 20);

  private JPasswordField passwordTextField = new JPasswordField("", 20);

  private String interfaceName = null;

  private String description = null;

  private String host = null;

  private String username = null;

  private String password = null;

  private String alarmTemplate = null;

  private String[] alarmTemplateDefaults = { "x733.vm", "email.vm" };

  private JComboBox alarmTemplateComboBox = new JComboBox(alarmTemplateDefaults);

  public AlarmInterfaceWizardDialog(JFrame frame, String connID, RockFactory dwhrepRock, RockFactory rock,
      Meta_collection_sets tp, UI ui) {
    this.frame = frame;
    this.rock = rock;
    this.targetMetaCollectionSet = tp;
    this.ui = ui;

    jd = new JDialog(frame, true);

    jd.setTitle("Alarm Interface Wizard");

    Container con = jd.getContentPane();
    con.setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2, 2, 2, 2);

    con.add(Box.createRigidArea(new Dimension(5, 5)), c);

    c.gridy = 1;
    c.gridx = 1;
    c.weightx = 1;
    c.fill = GridBagConstraints.HORIZONTAL;
    con.add(new JLabel("Interface ID"), c);

    c.gridx = 2;
    this.interfaceNameTextField.setToolTipText("The name of the interface to be created.");
    con.add(this.interfaceNameTextField, c);

    c.gridy = 2;
    c.gridx = 1;
    con.add(new JLabel("Description"), c);

    c.gridx = 2;
    this.descriptionTextField.setToolTipText("The description text for the alarm handler action.");
    con.add(this.descriptionTextField, c);

    c.gridy = 3;
    c.gridx = 1;
    con.add(new JLabel("Host"), c);

    c.gridx = 2;
    this.hostTextField
        .setToolTipText("The WebPortal host and port number where the alarm reports are loaded. For example 192.168.1.68:8080");
    con.add(this.hostTextField, c);

    c.gridy = 4;
    c.gridx = 1;
    con.add(new JLabel("Username"), c);

    c.gridx = 2;
    this.usernameTextField.setToolTipText("The username to the WebPortal where the alarm reports are loaded.");
    con.add(this.usernameTextField, c);

    c.gridy = 5;
    c.gridx = 1;
    con.add(new JLabel("Password"), c);

    c.gridx = 2;
    this.passwordTextField.setToolTipText("The password to the WebPortal where the alarm reports are loaded.");
    con.add(this.passwordTextField, c);

    c.gridy = 6;
    c.gridx = 1;
    con.add(new JLabel("Alarm output format"), c);

    c.gridx = 2;
    this.alarmTemplateComboBox.setEditable(true);
    this.alarmTemplateComboBox.setToolTipText("The output format used in alarm transformations.");
    con.add(this.alarmTemplateComboBox, c);

    c.gridy = 7;
    c.insets = new Insets(2, 2, 2, 2);
    c.fill = GridBagConstraints.BOTH;
    con.add(Box.createRigidArea(new Dimension(5, 5)), c);

    c.gridy = 8;
    c.weighty = 0;
    c.weightx = 0;
    c.anchor = GridBagConstraints.SOUTH;
    c.fill = GridBagConstraints.NONE;
    generate = new JButton("Generate", ConfigTool.bulb);
    generate.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent ae) {

        String error = "";

        // Some sanity checking here.
        if (AlarmInterfaceWizardDialog.this.interfaceNameTextField.getText().equalsIgnoreCase("") == true) {
          error += "Interface ID must be defined\n";
        }

        char[] interfaceNameCharacters = AlarmInterfaceWizardDialog.this.interfaceNameTextField.getText().toCharArray();
        for (int i = 0; i < interfaceNameCharacters.length; i++) {
          //Character currentCharacter = new Character(interfaceNameCharacters[i]);
          if (interfaceNameCharacters[i] != '_') {
            if (Character.isLetterOrDigit(interfaceNameCharacters[i]) == false) {
              // Note the error variable set that only one error message is displayed.
              error = "Interface ID cannot contain other characters than letters and digits";
            }
          }
        }

        if (AlarmInterfaceWizardDialog.this.interfaceNameTextField.getText().length() >= 50) {
          error += "Maximum length of interface name is 50 characters\n";
        }

        if (AlarmInterfaceWizardDialog.this.descriptionTextField.getText().equalsIgnoreCase("") == true) {
          error += "Description must be defined\n";
        }

        if (AlarmInterfaceWizardDialog.this.hostTextField.getText().equalsIgnoreCase("") == true) {
          error += "Host must be defined\n";
        }

        if (AlarmInterfaceWizardDialog.this.usernameTextField.getText().equalsIgnoreCase("") == true) {
          error += "Username must be defined\n";
        }

        if (new String(AlarmInterfaceWizardDialog.this.passwordTextField.getPassword()).equalsIgnoreCase("") == true) {
          error += "Password must be defined\n";
        }

        if (new String(AlarmInterfaceWizardDialog.this.alarmTemplateComboBox.getSelectedItem().toString())
            .equalsIgnoreCase("") == true) {
          error += "Alarm output format must be defined\n";
        }

        try {
          // Check that the set's to be created by the wizard doesn't already exist in database.
          // First check for Adapter_[ALARM_SET_NAME_HERE]_alarm set.
          Meta_collections whereMetaCollection = new Meta_collections(AlarmInterfaceWizardDialog.this.rock);
          String adapterSetName = "Adapter_" + AlarmInterfaceWizardDialog.this.interfaceNameTextField.getText()
              + "_alarm";
          whereMetaCollection.setCollection_name(adapterSetName);
          Meta_collectionsFactory metaCollectionsFactory = new Meta_collectionsFactory(
              AlarmInterfaceWizardDialog.this.rock, whereMetaCollection);
          Vector adapterMetaCollections = metaCollectionsFactory.get();
          if (adapterMetaCollections.size() > 0) {
            error += "Adapter " + adapterSetName + " already exists. Cannot create Alarm set.\n";
          }

          // Check for Directory_Checker_[ALARM_SET_NAME_HERE] set.
          whereMetaCollection = new Meta_collections(AlarmInterfaceWizardDialog.this.rock);
          String directoryCheckerSetName = "Directory_Checker_"
              + AlarmInterfaceWizardDialog.this.interfaceNameTextField.getText();
          whereMetaCollection.setCollection_name(directoryCheckerSetName);
          metaCollectionsFactory = new Meta_collectionsFactory(AlarmInterfaceWizardDialog.this.rock,
              whereMetaCollection);
          Vector directoryCheckerMetaCollections = metaCollectionsFactory.get();
          if (directoryCheckerMetaCollections.size() > 0) {
            error += "Directory Checker " + directoryCheckerSetName + " already exists. Cannot create Alarm set.\n";
          }

          // Check for Diskmanager_[ALARM_SET_NAME_HERE] set.
          whereMetaCollection = new Meta_collections(AlarmInterfaceWizardDialog.this.rock);
          String diskmanagerSetName = "Diskmanager_" + AlarmInterfaceWizardDialog.this.interfaceNameTextField.getText();
          whereMetaCollection.setCollection_name(diskmanagerSetName);
          metaCollectionsFactory = new Meta_collectionsFactory(AlarmInterfaceWizardDialog.this.rock,
              whereMetaCollection);
          Vector diskManagerMetaCollections = metaCollectionsFactory.get();
          if (diskManagerMetaCollections.size() > 0) {
            error += "Diskmanager " + diskmanagerSetName + " already exists. Cannot create Alarm set.\n";
          }

        } catch (Exception e) {

        }

        if (error.length() > 0) {
          JOptionPane.showMessageDialog(AlarmInterfaceWizardDialog.this, error, "Invalid configuration",
              JOptionPane.ERROR_MESSAGE);
          return;
        }

        generate.setEnabled(false);

        AlarmInterfaceWizardDialog.this.interfaceName = AlarmInterfaceWizardDialog.this.interfaceNameTextField
            .getText();
        AlarmInterfaceWizardDialog.this.description = AlarmInterfaceWizardDialog.this.descriptionTextField.getText();
        AlarmInterfaceWizardDialog.this.host = AlarmInterfaceWizardDialog.this.hostTextField.getText();
        AlarmInterfaceWizardDialog.this.username = AlarmInterfaceWizardDialog.this.usernameTextField.getText();
        AlarmInterfaceWizardDialog.this.password = new String(AlarmInterfaceWizardDialog.this.passwordTextField
            .getPassword());
        AlarmInterfaceWizardDialog.this.alarmTemplate = new String(
            AlarmInterfaceWizardDialog.this.alarmTemplateComboBox.getSelectedItem().toString());

        WizardWorker wizardWorker = new WizardWorker();
        wizardWorker.start();
      }
    });

    con.add(generate, c);

    c.gridx = 2;
    c.gridy = 9;
    con.add(Box.createRigidArea(new Dimension(5, 5)), c);

    jd.pack();
    jd.setVisible(true);

  }

  public class WizardWorker extends SwingWorker {

    public WizardWorker() {
      ui.startOperation("Generating sets...");
    }

    public Object construct() {

      try {

        // Create the Directory Checker Alarm Set.        
        CreateAlarmDirectoryChecker createAlarmDirectoryChecker = new CreateAlarmDirectoryChecker(
            AlarmInterfaceWizardDialog.this.interfaceName, AlarmInterfaceWizardDialog.this.rock,
            AlarmInterfaceWizardDialog.this.targetMetaCollectionSet, AlarmInterfaceWizardDialog.this.frame);
        createAlarmDirectoryChecker.create();
        // Create the Diskmanager Alarm Set.
        CreateAlarmDiskmanager createAlarmDiskmanager = new CreateAlarmDiskmanager(
            AlarmInterfaceWizardDialog.this.interfaceName, AlarmInterfaceWizardDialog.this.rock,
            AlarmInterfaceWizardDialog.this.targetMetaCollectionSet, AlarmInterfaceWizardDialog.this.frame);
        createAlarmDiskmanager.create();
        // Create the Adapter Set.
        CreateAlarmAdapter createAlarmAdapter = new CreateAlarmAdapter(AlarmInterfaceWizardDialog.this.interfaceName,
            AlarmInterfaceWizardDialog.this.rock, AlarmInterfaceWizardDialog.this.targetMetaCollectionSet,
            AlarmInterfaceWizardDialog.this.description, AlarmInterfaceWizardDialog.this.host,
            AlarmInterfaceWizardDialog.this.username, AlarmInterfaceWizardDialog.this.password,
            AlarmInterfaceWizardDialog.this.alarmTemplate, AlarmInterfaceWizardDialog.this.frame);
        createAlarmAdapter.create();

        ConfigTool.reloadConfig();

      } catch (Exception e) {
        return e;
      }

      return null;
    }

    public void finished() {

      Exception e = (Exception) get();

      if (e != null) {
        new ErrorDialog(frame, "Alarm Interface Wizard Error",
            "Alarm Interface Wizard failed exceptionally", e);
      }

      jd.setVisible(false);
      ui.endOperation();
    }

  };

  public void valueChanged(TreeSelectionEvent e) {
    // Not used in AlarmInterfaceWizard?

  }
  
  /**
   * This function returns the new CollectionId from the table META_COLLECTIONS.
   * @param etlrepRockFactory RockFactory for etlrep database.
   * @param frame JFrame object used for showing error message.
   * @return Returns the new CollectionId.
   */
  public static Long getNewCollectionId(RockFactory etlrepRockFactory, JFrame frame) {
    Long newCollectionId = new Long(0);
    
    try {
      Connection connection = etlrepRockFactory.getConnection();
      Statement statement = connection.createStatement();
      String query = "SELECT collection_id FROM META_COLLECTIONS ORDER BY collection_id DESC;";
      
      ResultSet resultSet = statement.executeQuery(query);
      
      if (resultSet.next()) {
        newCollectionId = new Long(resultSet.getLong("collection_id") + 1);
      }

      connection.close();
      
      return newCollectionId;

    } catch (Exception e) {
      new ErrorDialog(frame, "Creating CollectionId failed.", "Creating CollectionId failed.", e);
    }
    
    return newCollectionId;
  }
  

  /**
   * This function returns the new TransferActionId from the table META_TRANSFER_ACTIONS.
   * @param etlrepRockFactory RockFactory for etlrep database.
   * @param frame JFrame object used for showing error message.
   * @return Returns the new TransferActionId.
   */
  public static Long getNewTransferActionId(RockFactory etlrepRockFactory, JFrame frame) {
    Long newTransferActionId = new Long(0);
    
    try {
      Connection connection = etlrepRockFactory.getConnection();
      Statement statement = connection.createStatement();
      String query = "SELECT tranfer_action_id FROM META_TRANSFER_ACTIONS ORDER BY transfer_action_id DESC;";
      
      ResultSet resultSet = statement.executeQuery(query);
      
      if (resultSet.next()) {
        newTransferActionId = new Long(resultSet.getLong("collection_id") + 1);
      }

      connection.close();
      
      return newTransferActionId;

    } catch (Exception e) {
      new ErrorDialog(frame, "Creating TransferActionId failed.", "Creating TransferActionId failed.", e);
    }
    
    return newTransferActionId;
  }
  
  
}
