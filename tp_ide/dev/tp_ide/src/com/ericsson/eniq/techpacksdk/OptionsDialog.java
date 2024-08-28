package com.ericsson.eniq.techpacksdk;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
//import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

//import com.ericsson.eniq.techpacksdk.common.Constants;
import com.ericsson.eniq.techpacksdk.common.Utils;

/**
 * This class is used for prompting the user for licence files.
 * 
 * @author eheijun
 * 
 */
@SuppressWarnings("serial")
public class OptionsDialog extends JDialog {

  final private Logger log = Logger.getLogger(OptionsDialog.class.getName());

  private final JPanel myPanel;
  
  private final JTextField privatekeyTextField;

//  private final JTextField bofolderTextField;
//  
//  private final JComboBox BoVersionComboBox;
//  
//  private final JTextField boRepositoryTextField;
//
//  private final JTextField boUsernameTextField;
//  
//  private final JTextField boPasswordTextField;
//
//  private final JComboBox boxiauthenticationComboBox;
  
  private final JButton okButton;

  private final JButton cancelButton;

  private final Application application;

  private final Properties props;

  /**
   * Constructor.
   * 
   * @param parentFrame
   * @param title
   * @param message
   */
  public OptionsDialog(final Application application, final JFrame parentFrame, final Properties props, final ResourceMap resourceMap) {
    super(parentFrame, "Key Files", true);

    this.application = application;
    this.props = props;
    
    final String privatefile = props.getProperty("keyfile.private", System.getProperty("user.home"));
//    final String bofolder = props.getProperty("businessobjects.folder", "C:\\Program Files\\Business Objects\\");    
//    final String borepository = props.getProperty("businessobjects.repository", "dcweb4-a:6400");
//    final String bousername = props.getProperty("businessobjects.username", "Administrator");
//    final String bopassword = props.getProperty("businessobjects.password", "");
    
    final GridBagConstraints c = new GridBagConstraints();

    myPanel = new JPanel();

    myPanel.setLayout(new GridBagLayout());
    c.anchor = GridBagConstraints.NORTHWEST;
    c.insets = new Insets(2, 2, 2, 2);
    c.gridwidth = 1;

    c.gridy = 1;
    
    c.gridx = 0;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    myPanel.add(new JLabel(resourceMap.getString("optionsDialog.privateKey.label")), c);

    privatekeyTextField = new JTextField(privatefile, 26);
    privatekeyTextField.setPreferredSize(new Dimension(26, 28));
    
    c.gridx = GridBagConstraints.RELATIVE;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    myPanel.add(privatekeyTextField, c);
    
    c.gridx = GridBagConstraints.RELATIVE;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    final JButton manage1 = new JButton("...");
    manage1.setAction(this.getAction("selectprivatekey"));
    myPanel.add(manage1, c);

    c.gridy = 2;
    
//    c.gridx = 0;
//    c.fill = GridBagConstraints.NONE;
//    c.weightx = 0;
//    myPanel.add(new JLabel(resourceMap.getString("optionsDialog.boDir.label")), c);
//
//    bofolderTextField = new JTextField(bofolder, 26);
//    bofolderTextField.setPreferredSize(new Dimension(26, 28));
//
//    c.gridx = GridBagConstraints.RELATIVE;
//    c.fill = GridBagConstraints.HORIZONTAL;
//    c.weightx = 1;
//    myPanel.add(bofolderTextField, c);
//
//    c.gridx = GridBagConstraints.RELATIVE;
//    c.fill = GridBagConstraints.NONE;
//    c.weightx = 0;
//    final JButton manage3 = new JButton("...");
//    manage3.setAction(this.getAction("selectbofolder"));
//    myPanel.add(manage3, c);

//    c.gridy = 3;
//    
//    c.gridx = 0;
//    c.fill = GridBagConstraints.NONE;
//    c.weightx = 0;
//    myPanel.add(new JLabel(resourceMap.getString("optionsDialog.boVersion.label")), c);
//    
//    BoVersionComboBox = new JComboBox(Constants.BOVERSIONS);
//    BoVersionComboBox.setSelectedIndex(0);   
//    c.gridx = GridBagConstraints.RELATIVE;
//    c.fill = GridBagConstraints.HORIZONTAL;
//    c.weightx = 1;
//    myPanel.add(BoVersionComboBox, c);

//    c.gridy = 4;
//    
//    c.gridx = 0;
//    c.fill = GridBagConstraints.NONE;
//    c.weightx = 0;
//    myPanel.add(new JLabel(resourceMap.getString("optionsDialog.boRepository.label")), c);
//
//    boRepositoryTextField = new JTextField(borepository, 26);
//    boRepositoryTextField.setPreferredSize(new Dimension(26, 28));
//    
//    c.gridx = GridBagConstraints.RELATIVE;
//    c.fill = GridBagConstraints.HORIZONTAL;
//    c.weightx = 1;
//    myPanel.add(boRepositoryTextField, c);
    
//    c.gridy = 5;
//    
//    c.gridx = 0;
//    c.fill = GridBagConstraints.NONE;
//    c.weightx = 0;
//    myPanel.add(new JLabel(resourceMap.getString("optionsDialog.boUsername.label")), c);
//
//    boUsernameTextField = new JTextField(bousername, 26);
//    boUsernameTextField.setPreferredSize(new Dimension(26, 28));
//    
//    c.gridx = GridBagConstraints.RELATIVE;
//    c.fill = GridBagConstraints.HORIZONTAL;
//    c.weightx = 1;
//    myPanel.add(boUsernameTextField, c);
    
//    c.gridy = 6;
//    
//    c.gridx = 0;
//    c.fill = GridBagConstraints.NONE;
//    c.weightx = 0;
//    myPanel.add(new JLabel(resourceMap.getString("optionsDialog.boUsername.label")), c);
//
//    boPasswordTextField = new JTextField(bopassword, 26);
//    boPasswordTextField.setPreferredSize(new Dimension(26, 28));
//    
//    c.gridx = GridBagConstraints.RELATIVE;
//    c.fill = GridBagConstraints.HORIZONTAL;
//    c.weightx = 1;
//    myPanel.add(boPasswordTextField, c);     

//    c.gridy = 7;
//    
//    c.gridx = 0;
//    c.fill = GridBagConstraints.NONE;
//    c.weightx = 0;
//    myPanel.add(new JLabel(resourceMap.getString("optionsDialog.boAuthentication")), c);
//    
//    boxiauthenticationComboBox = new JComboBox(Constants.BOXIAUTHENTICATIONS);
//    boxiauthenticationComboBox.setSelectedIndex(0);   
//    c.gridx = GridBagConstraints.RELATIVE;
//    c.fill = GridBagConstraints.HORIZONTAL;
//    c.weightx = 1;
//    myPanel.add(boxiauthenticationComboBox, c);
    
    c.gridy = 8;
    
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridwidth = 3;
    c.weighty = 0;
    c.weightx = 1;
    
    final JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new GridBagLayout());
    
    myPanel.add(buttonPanel,c);
    
    c.gridx = 0;
    c.gridy = 0;
    c.fill = GridBagConstraints.NONE;
    c.anchor = GridBagConstraints.SOUTHEAST;
    c.weightx = 1;
    c.weighty = 0;
    c.gridwidth = 1;
    
    cancelButton = new JButton("Cancel");
    cancelButton.setName("LicenseDialogCancelButton");
    cancelButton.setAction(this.getAction("discard"));
    buttonPanel.add(cancelButton, c);
    
    c.gridx = 1;
    c.weightx = 0;

    okButton = new JButton("Ok");
    okButton.setName("LicenseDialogOkButton");
    okButton.setAction(this.getAction("ok"));
    buttonPanel.add(okButton, c);

    getContentPane().add(myPanel);
    
    pack();
    Utils.center(this);
    setAlwaysOnTop(true);
    setModal(true);
    setVisible(true);
  }

  private javax.swing.Action getAction(final String actionName) {
    if (application != null) {
      return application.getContext().getActionMap(this).get(actionName);
    }
    return null;
  }

  @Action
  public void ok() {
    props.setProperty("keyfile.private", privatekeyTextField.getText());
//    props.setProperty("businessobjects.folder", bofolderTextField.getText());
//    props.setProperty("businessobjects.version", BoVersionComboBox.getSelectedItem().toString());
//    props.setProperty("businessobjects.xiauthentication", boxiauthenticationComboBox.getSelectedItem().toString());
    storeConnectionProperties();
    setVisible(false);
  }
  
  @Action
  public void discard() {
    setVisible(false);
  }
    
  @Action
  public void selectprivatekey() {
    selectKeyFile(privatekeyTextField.getText(), privatekeyTextField);
  }
  
//  @Action
//  public void selectbofolder() {
//    selectFolder(bofolderTextField.getText(), bofolderTextField);
//  }
   
  public void selectKeyFile(final String defaultlocation, final JTextField defaultvalue) {
  
    while (true) {
      final JFileChooser jfc = new JFileChooser(new File(defaultlocation));
      jfc.setFileFilter(new FileFilter() {

        public boolean accept(final File f) {
          return f.isDirectory() || (!f.isDirectory() && f.exists() && f.getName().endsWith(".key"));
        }

        public String getDescription() {
          return "Key file";
        }
      });

      final int returnVal = jfc.showDialog(this, "Select keyfile");

      if (returnVal == JFileChooser.APPROVE_OPTION) {
        final File f = jfc.getSelectedFile();
        defaultvalue.setText(f.getAbsolutePath());

      }

      break;

    }

  }

  public void selectFolder(final String defaultlocation, final JTextField defaultvalue) {
    
    while (true) {
      final JFileChooser jfc = new JFileChooser(new File(defaultlocation));
      jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
      jfc.setFileFilter(new FileFilter() {

        public boolean accept(final File f) {
          return f.isDirectory() && f.exists();
        }

        public String getDescription() {
          return "Select BusinessObjects Folder";
        }
      });

      final int returnVal = jfc.showDialog(this, "Select BusinessObjects Folder");

      if (returnVal == JFileChooser.APPROVE_OPTION) {
        final File f = jfc.getSelectedFile();
        defaultvalue.setText(f.getAbsolutePath());

      }

      break;

    }

  }

  private void storeConnectionProperties() {
    FileOutputStream fos = null;
    final File target = new File(TechPackIDE.CONPROPS_FILE);
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

}
