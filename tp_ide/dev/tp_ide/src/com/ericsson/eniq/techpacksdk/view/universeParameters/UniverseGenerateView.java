package com.ericsson.eniq.techpacksdk.view.universeParameters;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

import com.distocraft.dc5000.repository.dwhrep.Universename;
import com.distocraft.dc5000.repository.dwhrep.UniversenameFactory;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.component.ErrorMessageComponent;
import com.ericsson.eniq.component.ExceptionHandler;
import com.ericsson.eniq.techpacksdk.BusyIndicator;
import com.ericsson.eniq.techpacksdk.BusyIndicatorInputBlocker;
import com.ericsson.eniq.techpacksdk.TechPackIDE;
import com.ericsson.eniq.techpacksdk.common.Utils;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;

/**
 * This view contains the general universe details and the buttons for executing
 * the universe generate actions.
 * 
 * @author eheijun
 * @author eheitur
 */
@SuppressWarnings("serial")
public class UniverseGenerateView extends JPanel {

  String BOreposityory, BOusername, BOpassword, ODBCconnection, TPIdentification, BaseIdentification, OutputFolder,
      BOdomains, BOgroups;

  private static final Logger logger = Logger.getLogger(UniverseGenerateView.class.getName());

  private SingleFrameApplication application;

  private boolean createVerRepEnabled = true;

  private boolean updateUnvEnabled = true;

  private boolean createUnvEnabled = true;

  private boolean createUnvRefDocEnabled = true;

  private boolean createBOPackageEnabled = false;
  
  // private boolean cancelEnabled = false;

  private UniverseTabs parentPanel;

  public static final Color ERROR_BG = Color.PINK;

  public static final Color WHITE = Color.WHITE;

  private final Color neutralBg;

  JScrollPane scrollPane;

  JTextField TPIdentificationF = new JTextField("");

  JTextField BaseIdentificationF = new JTextField("");

  JTextField OutputFolderF = new JTextField("");

  JComboBox DomainCB = new JComboBox();

  JComboBox GroupsCB = new JComboBox();

  JComboBox UniverseCB = new JComboBox();

  Versioning versioning;

  DataModelController dataModelController;

  String universeName;

  JFrame frame;

  /**
   * For error handling
   */
  Vector<String> errors = new Vector<String>();

  /**
   * For error handling
   */
  private ErrorMessageComponent errorMessageComponent;

  // private String techpackType;

  public UniverseGenerateView(final SingleFrameApplication application, DataModelController dataModelController,
      Versioning versioning, boolean editable, UniverseTabs parentPanel, JFrame frame) {
    super(new GridBagLayout());

    this.frame = frame;
    neutralBg = null;

    this.dataModelController = dataModelController;
    this.application = application;
    this.parentPanel = parentPanel;

    this.versioning = versioning;

    // Get the universe name for this techpack
    universeName = getUniversename(versioning.getVersionid());

    // Text panel
    int fieldSize = 30;

    Dimension fieldDim = new Dimension(279, fieldSize);

    JPanel txtPanel = new JPanel(new GridBagLayout());
    JScrollPane txtPanelS = new JScrollPane(txtPanel);

    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2, 2, 2, 2);
    c.weightx = 0;
    c.weighty = 0;

    // Techpack identification
    JLabel TPIdentification = new JLabel("Tech Pack Identification");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = GridBagConstraints.RELATIVE;
    c.weightx = 0;
    txtPanel.add(TPIdentification, c);

    TPIdentificationF.setText(versioning.getVersionid());
    TPIdentificationF.setPreferredSize(fieldDim);
    TPIdentificationF.setBackground(neutralBg);
    TPIdentificationF.setEditable(false);
    TPIdentificationF.getDocument().addDocumentListener(new MyOtherDocumentListener());

    c.fill = GridBagConstraints.NONE;
    c.gridx = 1;
    // c.gridy = 5;
    c.weightx = 1;
    txtPanel.add(TPIdentificationF, c);

    // Base identification

    JLabel basedef = new JLabel("Base Identification");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = GridBagConstraints.RELATIVE;
    c.weightx = 0;
    txtPanel.add(basedef, c);

    BaseIdentificationF.setText(versioning.getBasedefinition());
    BaseIdentificationF.setBackground(neutralBg);
    BaseIdentificationF.setEditable(false);
    BaseIdentificationF.setPreferredSize(fieldDim);
    BaseIdentificationF.getDocument().addDocumentListener(new MyOtherDocumentListener());

    c.fill = GridBagConstraints.NONE;
    c.gridx = 1;
    // c.gridy = 6;
    c.weightx = 1;
    txtPanel.add(BaseIdentificationF, c);

    // OutputFolder
    JLabel outputdir = new JLabel("Working Directory");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = GridBagConstraints.RELATIVE;
    c.weightx = 0;
    txtPanel.add(outputdir, c);

    OutputFolderF = new JTextField(dataModelController.getWorkingDir().getAbsolutePath(), 25);
    // OutputFolderF.setBackground(neutralBg);
    OutputFolderF.setEditable(false);
    OutputFolderF.setPreferredSize(fieldDim);
    OutputFolderF.getDocument().addDocumentListener(new MyOtherDocumentListener());

    c.fill = GridBagConstraints.NONE;
    c.gridx = 1;
    c.weightx = 1;
    txtPanel.add(OutputFolderF, c);

    // View mode
    if (editable == false) {

      TPIdentificationF.setEnabled(false);
      BaseIdentificationF.setEnabled(false);
      OutputFolderF.setEnabled(false);
      DomainCB.setEnabled(false);
      GroupsCB.setEnabled(false);
      UniverseCB.setEnabled(false);
    }

    // Buttons
    
    JButton createUnv;
    JButton updateUnv;
    JButton createVerRep;
    JButton createTPUnvRefDoc;
    JButton createBOPackage;

    createUnv = new JButton("Create");
    createUnv.setAction(getAction("createUniverse"));
    createUnv.setToolTipText("Create");
    createUnv.setName("Create Universe");

    updateUnv = new JButton("Update Unv");
    updateUnv.setAction(getAction("updateUniverse"));
    updateUnv.setToolTipText("Update");
    updateUnv.setName("UpdateUniverse");

    createVerRep = new JButton("TP Ver. Rep.");
    createVerRep.setAction(getAction("createVerRep"));
    createVerRep.setToolTipText("Report");
    createVerRep.setName("VerificationReports");
    createVerRep.setEnabled(false);

    createTPUnvRefDoc = new JButton("TP Universe Reference Document");
    createTPUnvRefDoc.setAction(getAction("createUnvRefDoc"));
    createTPUnvRefDoc.setToolTipText("Tech Pack Universe Reference Document");
    createTPUnvRefDoc.setName("TPUnvRefDoc");

    createBOPackage = new JButton("Create BO Package");
    createBOPackage.setAction(getAction("createBOPackage"));
    createBOPackage.setToolTipText("Create BO Package");
    createBOPackage.setName("CreateBOPackage");

    setCreateUnvEnabled(true);
    setUpdateUnvEnabled(true);
    setCreateVerRepEnabled(true);
    setCreateUnvRefDocEnabled(true);
    setCreateBOPackageEnabled(true);

    final JButton closeDialog = new JButton("Close");
    closeDialog.setAction(getParentAction("closeDialog"));
    closeDialog.setEnabled(true);
    closeDialog.setName("UniverseGenerateClose");

    errorMessageComponent = new ErrorMessageComponent(application);
    errorMessageComponent.setValue(new Vector<String>());

    // Button panel

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

    buttonPanel.add(errorMessageComponent);

    buttonPanel.add(createUnv);
    buttonPanel.add(updateUnv);
    buttonPanel.add(createVerRep);
    buttonPanel.add(createTPUnvRefDoc);
    buttonPanel.add(createBOPackage);
    buttonPanel.add(closeDialog);

    // Right & left panels, left panel contains tab

    // JPanel lpanel = new JPanel(new BorderLayout());
    // JPanel rpanel = new JPanel(new BorderLayout());
    // lpanel.add(txtPanelS);
    //
    // // Inner panel panel with right and left panels
    //
    // JPanel innerPanel = new JPanel();
    // innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.LINE_AXIS));
    // innerPanel.add(lpanel, BorderLayout.LINE_START);
    // innerPanel.add(new JSeparator(SwingConstants.VERTICAL));
    // innerPanel.add(rpanel, BorderLayout.LINE_START);

    // Main panel with inner panel and button panel
    // Insert both panels in main view

    // Create a panel for the tab.
    JPanel innerPanel = new JPanel();
    innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.LINE_AXIS));
    innerPanel.add(txtPanelS);

    // Add the tab panel to the main view
    final GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridheight = 1;
    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.anchor = GridBagConstraints.NORTHWEST;

    gbc.weightx = 0;
    gbc.weighty = 0.8;
    gbc.gridx = 0;
    gbc.gridy = 0;
    this.add(innerPanel, gbc);

    // Add the button panel to the main view
    gbc.weightx = 1;
    gbc.weighty = 0.2;
    gbc.gridx = 0;
    gbc.gridy = 1;
    this.add(buttonPanel, gbc);

    // Store the teckpack type for being able to disable the some buttons for
    // base TechPack
    // this.techpackType = versioning.getTechpack_type();

    // Disable the buttons in view mode or universe name has not been defined for this techpack.
    if (editable == false || (this.universeName.equals(""))) {
    	disableButtons();
    }
    
    // Commenting it as it was adjusting the size of window while choosing Tab
    //frame.pack();
    // frame.repaint();
  }

  /**
   * Perform refresh
   * 
   */
  public void refresh() {
    // nothing to do in this view
  }

  @Action
  public void close() {
    logger.log(Level.INFO, "close");
    frame.dispose();

    
  }

  static BusyIndicator busyIndicator = new BusyIndicator(); //EQEV-2866 changes
  
  @Action(enabledProperty = "createUnvEnabled")
  public void createUniverse() {
	  
    try {
      Properties props = Utils.getProperties(TechPackIDE.CONPROPS_FILE);
      new UniverseWizard(application, frame, props, UniverseWizard.CREATENEWUNV, TPIdentificationF.getText(),
          BaseIdentificationF.getText(), OutputFolderF.getText());
           
      //EQEV-2866 Start
      frame.setGlassPane(busyIndicator);
      busyIndicator.setVisible(true);
      //EQEV-2866 End
       
      getParentAction("enableTabs").actionPerformed(null);
      frame.repaint();
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, e.getMessage());
    }
  
  }

  @Action(enabledProperty = "updateUnvEnabled")
  public void updateUniverse() {
    try {
      Properties props = Utils.getProperties(TechPackIDE.CONPROPS_FILE);
      new UniverseWizard(application, frame, props, UniverseWizard.UPDATEUNV, TPIdentificationF.getText(),
          BaseIdentificationF.getText(), OutputFolderF.getText());
      
      //EQEV-2866 Start
      frame.setGlassPane(busyIndicator);
      busyIndicator.setVisible(true);
      //EQEV-2866 End
      
      getParentAction("enableTabs").actionPerformed(null);
      frame.repaint();
    } catch (Exception e) {
      logger.log(Level.SEVERE, e.getMessage());
    }
  }

  @Action(enabledProperty = "createVerRepEnabled")
  public void createVerRep() {
    try {
      Properties props = Utils.getProperties(TechPackIDE.CONPROPS_FILE);
      new UniverseWizard(application, frame, props, UniverseWizard.CREATEREP, TPIdentificationF.getText(),
          BaseIdentificationF.getText(), OutputFolderF.getText());
      
      //EQEV-2866 Start
      frame.setGlassPane(busyIndicator);
      busyIndicator.setVisible(true);
      //EQEV-2866 End
      getParentAction("enableTabs").actionPerformed(null);
      frame.repaint();
    } catch (Exception e) {
      logger.log(Level.SEVERE, e.getMessage());
    }
  }

  @Action(enabledProperty = "createUnvRefDocEnabled")
  public void createUnvRefDoc() {
    try {
      Properties props = Utils.getProperties(TechPackIDE.CONPROPS_FILE);
      new UniverseWizard(application, frame, props, UniverseWizard.CREATEDOC, TPIdentificationF.getText(),
          BaseIdentificationF.getText(), OutputFolderF.getText());
      
      //EQEV-2866 Start
      frame.setGlassPane(busyIndicator);
      busyIndicator.setVisible(true);
      //EQEV-2866 End
      
      getParentAction("enableTabs").actionPerformed(null);
      frame.repaint();
    } catch (Exception e) {
      logger.log(Level.SEVERE, e.getMessage());
    }
  }

  @Action(enabledProperty = "createBOPackageEnabled")
  public void createBOPackage() {
    try {
      Properties props = Utils.getProperties(TechPackIDE.CONPROPS_FILE);
      new UniverseBOPackageDialog(application, frame, props, dataModelController.getUser(), versioning);

      //EQEV-2866 Start
      frame.setGlassPane(busyIndicator);
      busyIndicator.setVisible(true);
      //EQEV-2866 End
      
      getParentAction("enableTabs").actionPerformed(null);
      frame.repaint();
    } catch (Exception e) {
      logger.log(Level.SEVERE, e.getMessage());
    }
  }

  private javax.swing.Action getAction(final String actionName) {
    return application.getContext().getActionMap(this).get(actionName);
  }

  /**
   * Helper function, returns action by name from parent panel
   * 
   * @param actionName
   * @return
   */
  private javax.swing.Action getParentAction(final String actionName) {
    if (application != null) {
      return application.getContext().getActionMap(parentPanel).get(actionName);
    }
    return null;
  }

  // public boolean isCancelEnabled() {
  // return cancelEnabled;
  // }
  //
  // public void setCancelEnabled(boolean cancelEnabled) {
  // boolean oldvalue = this.cancelEnabled;
  // this.cancelEnabled = cancelEnabled;
  // firePropertyChange("cancelEnabled", oldvalue, cancelEnabled);
  // }

  // /**
  // * Error handling, sets the message for errorMessageComponent
  // *
  // * @param message
  // */
  // private void setScreenMessage(final Vector<String> message) {
  // errorMessageComponent.setValue(message);
  // }

  public Application getApplication() {
    return application;
  }

  public void setApplication(final SingleFrameApplication application) {
    this.application = application;
  }

  private void handleButtons() {

    if (!UniverseCB.getSelectedItem().toString().equals(""))

    // !BoUserNameF.getText ().equals("") ||
    // !BoPasswordF.getText().equals("")) ||
    // !BoRepositoryCB.getSelectedItem().toString().equals("") ||
    // !BoRepositoryF.getText().equals("") ||
    // !ODBCConnectionF.getText().equals("") ||
    // !TPIdentificationF.getText().equals("") ||
    // !BaseIdentificationF.getText().equals("") ||
    // !OutputFolderF.getText().equals(""))
    {
      // this.setCreateUnvEnabled(true);
      // this.setUpdateUnvEnabled(true);
      // this.setCreateVerRepEnabled(true);
      // this.setCreateUnvRefDocEnabled(true);

    } else {
      getParentAction("enableTabs").actionPerformed(null);
    }

  }

  private class MyOtherDocumentListener implements DocumentListener {

    public void changedUpdate(DocumentEvent e) {
      handleButtons();
    }

    public void insertUpdate(DocumentEvent e) {
      handleButtons();
    }

    public void removeUpdate(DocumentEvent e) {
      handleButtons();
    }
  }

  public boolean isCreateVerRepEnabled() {
    return createVerRepEnabled;
  }

  public void setCreateVerRepEnabled(boolean verifyreportEnabled) {
    boolean oldvalue = this.isCreateVerRepEnabled();
    this.createVerRepEnabled = verifyreportEnabled;
    firePropertyChange("createVerRepEnabled", oldvalue, verifyreportEnabled);
  }

  public boolean isUpdateUnvEnabled() {
    return updateUnvEnabled;
  }

  public void setUpdateUnvEnabled(boolean updatebuttonEnabled) {
    boolean oldvalue = this.isUpdateUnvEnabled();
    this.updateUnvEnabled = updatebuttonEnabled;
    firePropertyChange("updateUnvEnabled", oldvalue, updatebuttonEnabled);
  }

  public boolean isCreateUnvEnabled() {
    return createUnvEnabled;
  }

  public void setCreateUnvEnabled(boolean createEnabled) {
    boolean oldvalue = this.isCreateUnvEnabled();
    this.createUnvEnabled = createEnabled;
    firePropertyChange("createUnvEnabled", oldvalue, createEnabled);
  }

  public boolean isCreateUnvRefDocEnabled() {
    return createUnvRefDocEnabled;
  }

  public void setCreateUnvRefDocEnabled(boolean createUnvRefEnabled) {
    boolean oldvalue = this.isCreateUnvRefDocEnabled();
    this.createUnvRefDocEnabled = createUnvRefEnabled;
    firePropertyChange("createUnvRefDocEnabled", oldvalue, createUnvRefEnabled);
  }

  public boolean isCreateBOPackageEnabled() {
    return createBOPackageEnabled;
  }

  public void setCreateBOPackageEnabled(boolean createBOPackageEnabled) {
    if (this.createBOPackageEnabled != createBOPackageEnabled) {
      boolean oldvalue = this.isCreateBOPackageEnabled();
      this.createBOPackageEnabled = createBOPackageEnabled;
      firePropertyChange("createBOPackageEnabled", oldvalue, createBOPackageEnabled);
    }
  }

  /**
   * Universe Tab Buttons are disabled if no universe name has been defined for
   * this techpack.
   */
  public void disableButtons() {
      setCreateUnvEnabled(false);
      setUpdateUnvEnabled(false);
      setCreateVerRepEnabled(false);
      setCreateUnvRefDocEnabled(false);
      setCreateBOPackageEnabled(false);
    
  }

  /**
   * Gets the universe name for this techpack.
   * 
   * @param Versionid
   * @return the universe name. Empty string if name is not found.
   */
  public String getUniversename(String Versionid) {

    try {
      Universename universename = new Universename(dataModelController.getRockFactory());
      universename.setVersionid(Versionid);
      UniversenameFactory universenameF = new UniversenameFactory(dataModelController.getRockFactory(), universename);
      Iterator iter = universenameF.get().iterator();
      while (iter.hasNext()) {
        Universename tmpuUniversename = (Universename) iter.next();
        return tmpuUniversename.getUniversename();
      }
    } catch (Exception e) {
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    }
    return "";
  }
}
