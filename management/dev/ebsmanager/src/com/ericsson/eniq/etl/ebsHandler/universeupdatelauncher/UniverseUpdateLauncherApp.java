package com.ericsson.eniq.etl.ebsHandler.universeupdatelauncher;

import java.awt.Color;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.Properties;


import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;












/**
 * An application for launching universe update application with Web Start. 
 * The application is written from jar file into the client computer, and then 
 * executed. 
 * 
 * @author epiituo
 * 
 */
public class UniverseUpdateLauncherApp extends Frame {

  /*
   * The title of the application's main frame.
   */
  private static final String FRAME_TITLE = "Universe update";

  /*
   * The labels' texts
   */
  private static final String BO_REPOSITORY_LABEL_TEXT =        "BO Repository";
 private static final String BO_USER_NAME_LABEL_TEXT =         "BO Username";
  private static final String BO_PASSWORD_LABEL_TEXT =          "BO Password";
  private static final String ODBC_CONNECTION_LABEL_TEXT =      "ODBC Connection";
  private static final String TP_IDENTIFICATION_LABEL_TEXT =    "Techpack Identification";
  private static final String BASE_IDENTIFICATION_LABEL_TEXT =  "Base Identification";
  private static final String WORKING_DIRECTORY_LABEL_TEXT =    "Working Directory";

  /*
   * The buttons' labels.
   */
  private static final String UPDATE_BUTTON_LABEL = "Initiate update";
  private static final String WORKING_DIRECTORY_BUTTON_LABEL = "...";

  /*
   * The width and height of the application's main frame.
   */
  private static final int FRAME_WIDTH = 400;
  private static final int FRAME_HEIGHT = 600;

  /*
   * The width and height of the update-starting button in the application's
   * main frame.
   */
  private static final int BUTTON_WIDTH = 200;
  private static final int BUTTON_HEIGHT = 50;

  /*
   * The property file where the user-submitted values are stored.
   */
 private static final String PROPERTY_FILE_DIRECTORY = System.getProperty("user.home");
  private static final String PROPERTY_FILE_FILENAME = "universeupdate.properties";








  
  private ActionListener buttonListener;
  private WindowAdapter windowListener;
  private DocumentListener emptyFieldListener;
  
  private JTextField boRepositoryTextField;

  private JTextField boUserNameTextField;

  private JPasswordField boPasswordTextField;

  private JTextField odbcConnectionTextField;

  private JComboBox<String> tpIdentificationComboBox;

  private JTextField baseIdentificationTextField;

  private JTextField workingDirectoryTextField;

  private JLabel boRepositoryLabel;

  private JLabel boUserNameLabel;

  private JLabel boPasswordLabel;

  private JLabel odbcConnectionLabel;

  private JLabel tpIdentificationLabel;

  private JLabel baseIdentificationLabel;

  private JLabel workingDirectoryLabel;
  
  private String[] techpackOptions;
  
  private String[] basetechpackOptions;

  private JButton updateButton;

  private JButton workingDirectoryButton;
  
  private JTextArea textArea;

  private TextAreaOutputStream textAreaOutputStream;

  private Installer installer;

  private Arguments installerArguments;
 
  /*
   *  A boolean for storing the information whether or not the update has been
   *  started. The variable is used to keep the update button disabled after
   *  starting the update.
   */
  private boolean updateHasBeenStartedFlag = false;

  /**
   * Main method
   * 
   * @param args, not used by the method
   */
  public static void main(String[] args) {

    UniverseUpdateLauncherApp frame = new UniverseUpdateLauncherApp();
    frame.setVisible(true);
  }

  /**
   * Constructor
   */
  public UniverseUpdateLauncherApp() {

    super(FRAME_TITLE);
    this.setLayout(new GridLayout(3, 1));
    this.setSize(FRAME_WIDTH, FRAME_HEIGHT);

    this.installer = new Installer();

    // Create the application's listeners
    this.buttonListener = createButtonListener();
    this.emptyFieldListener = createDocumentChangeListener();
    this.windowListener = createWindowListener();
    this.addWindowListener(this.windowListener);



    // Parse the techpack and base techpack options
    this.techpackOptions = getTechpackOptions();
    this.basetechpackOptions = getBasetechpackOptions();
    
    // Create three panels, and add them to the main frame
    JPanel upperPanel = new JPanel();
    upperPanel.setLayout(new GridBagLayout()); // Contains the text fields
    this.add(upperPanel);
    JPanel middlePanel = new JPanel(); // Contains the button
    middlePanel.setLayout(new GridBagLayout());
    this.add(middlePanel);
    JPanel lowerPanel = new JPanel(); // Contains the text area
    lowerPanel.setLayout(new GridLayout(1, 1));
    this.add(lowerPanel);

    // Create the labels and text fields
    this.boRepositoryLabel = new JLabel(BO_REPOSITORY_LABEL_TEXT);
    this.boRepositoryTextField = createTextField();
    add(upperPanel, this.boRepositoryLabel, this.boRepositoryTextField);
    
   this.boUserNameLabel = new JLabel(BO_USER_NAME_LABEL_TEXT);
   this.boUserNameTextField = createTextField();
    add(upperPanel, this.boUserNameLabel, this.boUserNameTextField);
    
    this.boPasswordLabel = new JLabel(BO_PASSWORD_LABEL_TEXT);
    this.boPasswordTextField = new JPasswordField();
    this.boPasswordTextField.setBackground(Color.WHITE);
    this.boPasswordTextField.getDocument().addDocumentListener(this.emptyFieldListener);
    add(upperPanel, this.boPasswordLabel, this.boPasswordTextField);
    
    this.odbcConnectionLabel = new JLabel(ODBC_CONNECTION_LABEL_TEXT);
    this.odbcConnectionTextField = createTextField();
    add(upperPanel, this.odbcConnectionLabel, this.odbcConnectionTextField);
    
    this.tpIdentificationLabel = new JLabel(TP_IDENTIFICATION_LABEL_TEXT);
    this.tpIdentificationComboBox = new JComboBox<String>(this.techpackOptions);
    this.tpIdentificationComboBox.addActionListener(createTechpackComboBoxListener());
    add(upperPanel, this.tpIdentificationLabel, this.tpIdentificationComboBox);
    
    this.baseIdentificationLabel = new JLabel(BASE_IDENTIFICATION_LABEL_TEXT);
    this.baseIdentificationTextField = createTextField();
    this.baseIdentificationTextField.setEditable(false);
    add(upperPanel, this.baseIdentificationLabel, this.baseIdentificationTextField);
    
    this.workingDirectoryLabel = new JLabel(WORKING_DIRECTORY_LABEL_TEXT);
    JPanel workingDirectoryPanel = new JPanel(new GridBagLayout());
    GridBagConstraints gc = new GridBagConstraints();
    this.workingDirectoryTextField = createTextField();
    gc.fill = GridBagConstraints.BOTH;
    gc.gridx = 0;
    gc.weightx = 1;
    workingDirectoryPanel.add(this.workingDirectoryTextField, gc);
    gc.fill = GridBagConstraints.NONE;
    gc.gridx = 1;
    gc.weightx = 0;
    this.workingDirectoryButton = new JButton(WORKING_DIRECTORY_BUTTON_LABEL);
    this.workingDirectoryButton.addActionListener(this.buttonListener);
    workingDirectoryPanel.add(this.workingDirectoryButton, gc);
    add(upperPanel, this.workingDirectoryLabel, workingDirectoryPanel);
    
    // Create the button control, and add it to the upper panel
    this.updateButton = new JButton(UPDATE_BUTTON_LABEL);
    this.updateButton.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
    this.updateButton.addActionListener(this.buttonListener);
    GridBagConstraints gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.ipadx = BUTTON_WIDTH;
    gridBagConstraints.ipady = BUTTON_HEIGHT;
    middlePanel.add(this.updateButton, gridBagConstraints);

    // Create the text area control, and add it to the lower panel
    this.textArea = new JTextArea();
    this.textArea.setBackground(Color.WHITE);
    this.textArea.setEditable(false);
    lowerPanel.add(new JScrollPane(textArea));

    // Set the installer's output to be written to the text field.
    this.textAreaOutputStream = new TextAreaOutputStream(this.textArea);
    this.installer.setOutputStream(this.textAreaOutputStream);
    
   // Load stored values or set the default values for the form's fields
    if(storedValuesExist()) {
      try { 
        loadAndSetStoredValues();
      }
      catch (IOException e) {
        e.printStackTrace();
        setDefaultValues();
      }
    }
    else {
      setDefaultValues();
    }
    
// TODO
    
    enableUpdateButton();
    
   // // If there are empty fields in the form, disable the update button. If not,
  //  // enable the button.
   // boolean emptyFields = this.checkEmptyFields();
  // if (!emptyFields) {
  //    enableUpdateButton();
  //  else {
  //  }
  //    disableUpdateButton();
  //  }
  }
  
  private JTextField createTextField() {
    JTextField result = new JTextField();
    result.setBackground(Color.WHITE);
    result.getDocument().addDocumentListener(this.emptyFieldListener);
    return result;
  }
  
  private void add(JPanel panel, JLabel label, JComponent component) {
    GridBagConstraints gridBagConstraints = new GridBagConstraints();
    
    gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
    gridBagConstraints.gridx = 0;
    gridBagConstraints.insets = new Insets(2, 2, 2, 2);
    gridBagConstraints.fill = GridBagConstraints.NONE;
    gridBagConstraints.weightx = 0;
    panel.add(label, gridBagConstraints);
    
    gridBagConstraints.gridx = 1;
    gridBagConstraints.insets = new Insets(2, 2, 2, 2);
    gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1;
    panel.add(component, gridBagConstraints);
  }

  /**
   * Returns the names of ebs techpacks, defined in the property "techpacks", 
   * as an array.
   * 
   * The properties "techpacks" and "basetechpacks" should contain the techpack 
   * names in such order that getBasetechpackOptions()[i] is the base techpack 
   * of getTechpackOptions()[i].
   * 
   * @return
   */
  private String[] getTechpackOptions() {
    String techpacksProperty = System.getProperty("jnlp.universeupdateinstaller.techpacks");
    String[] techpackNames;
    if (techpacksProperty != null) {
      techpackNames = techpacksProperty.split(",");
    }
    else {
      techpackNames = new String[] { "PM_E_EBSW","PM_E_EBSG","PM_E_EBSS" };
    }
    
    return techpackNames;
  }
  
  /**
   * Returns the names of base techpacks, defined in the property basetechpacks, 
   * as an array.
   * 
   * The properties "techpacks" and "basetechpacks" should contain the techpack 
   * names in such order that getBasetechpackOptions()[i] is the base techpack 
   * of getTechpackOptions()[i].
   * 
   * @return
   */
  private String[] getBasetechpackOptions() {
    String baseTechpacksProperty = System.getProperty("jnlp.universeupdateinstaller.basetechpacks");
    String[] baseTechpackNames;
    if (baseTechpacksProperty != null) {
      baseTechpackNames = baseTechpacksProperty.split(",");
    }
    else {
      baseTechpackNames = new String[] { "" };
    }
    
    return baseTechpackNames;
  }

  /**
   * Returns the information whether or not the property file used by this 
   * application exists on local file system.
   * 
   * @return
   */
  private boolean storedValuesExist() {
    File propertyFile = new File(PROPERTY_FILE_DIRECTORY + "\\" + PROPERTY_FILE_FILENAME);
    return propertyFile.exists();
  }
  
  /**
   * Loads the values from a property file, and sets them into the form's 
   * fields.
   * 
   * @throws IOException
   */
  private void loadAndSetStoredValues() throws IOException {
    // Load the stored values
    File propertyFile = new File(PROPERTY_FILE_DIRECTORY + "\\" + PROPERTY_FILE_FILENAME);
    FileInputStream fis = new FileInputStream(propertyFile);
    Properties properties = new Properties();
    properties.load(fis);
    fis.close();
    
    // Set the fields' values.
    this.boUserNameTextField.setText("Administrator");
    this.boRepositoryTextField.setText(properties.getProperty(""));
    this.boPasswordTextField.setText("");
    this.odbcConnectionTextField.setText(properties.getProperty(""));
    this.tpIdentificationComboBox.setSelectedItem(properties.getProperty("tpIdentification"));
    this.workingDirectoryTextField.setText(properties.getProperty("workingDirectory"));
  }
  
  /**
   * Stores the current fields' values into a local property file.
   * 
   * @throws IOException
   */
  
  
  private void storeValues() throws IOException {
    File propertyFile = new File(PROPERTY_FILE_DIRECTORY + "\\" + PROPERTY_FILE_FILENAME);
    FileOutputStream fos = new FileOutputStream(propertyFile);
    
    Properties properties = new Properties();
    properties.setProperty("boRepository", this.boRepositoryTextField.getText());
    properties.setProperty("odbcConnection", this.odbcConnectionTextField.getText());
    properties.setProperty("tpIdentification", (String)this.tpIdentificationComboBox.getSelectedItem());
    properties.setProperty("workingDirectory", this.workingDirectoryTextField.getText());
  
    properties.store(fos, "Stored properties for ENIQ universe update application");
    fos.close();
  }
  
  /**
   * Sets empty values for the fields.
   * 
   */
  private void setDefaultValues() {
    this.boRepositoryTextField.setText("");
    this.boUserNameTextField.setText("Administrator");
    this.boPasswordTextField.setText("");
    this.odbcConnectionTextField.setText("");
    this.tpIdentificationComboBox.setSelectedIndex(0);
    this.baseIdentificationTextField.setText(this.basetechpackOptions[0]);
    this.workingDirectoryTextField.setText("");
  }
  
  /**
   * Returns the information, whether or not there are empty fields in the 
   * application's form. Also, the method changes the empty fields' colours, 
   * as a signal to the user, that some input for that field is required.
   * 
   * @return
   */
  private boolean checkEmptyFields() {
    boolean emptyFields = false;
    
    Color normalBackground = Color.white;
    Color errorBackground = Color.PINK;
    
    if (UniverseUpdateLauncherApp.this.boRepositoryTextField.getText().isEmpty()) {
      this.boRepositoryTextField.setBackground(errorBackground);
      emptyFields = true;
    } else {
      this.boRepositoryTextField.setBackground(normalBackground);
    }
    
    if (this.boUserNameTextField.getText().isEmpty()) {
      this.boUserNameTextField.setBackground(errorBackground);
      emptyFields = true;
    } else {
    this.boUserNameTextField.setBackground(normalBackground);
    }
   /* 
    if (this.boPasswordTextField.getText().isEmpty()) {
      this.boPasswordTextField.setBackground(errorBackground);
      emptyFields = true;
    } else {
      this.boPasswordTextField.setBackground(normalBackground);
    }
    */
    if (this.odbcConnectionTextField.getText().isEmpty()) {
      this.odbcConnectionTextField.setBackground(errorBackground);
      emptyFields = true;
    } else {
      this.odbcConnectionTextField.setBackground(normalBackground);
    }
    
    String selectedTP = (String)this.tpIdentificationComboBox.getSelectedItem();
    if (selectedTP.trim().isEmpty()) {
      this.tpIdentificationComboBox.setBackground(errorBackground);
      emptyFields = true;
    } else {
      this.tpIdentificationComboBox.setBackground(normalBackground);
    }
    
    if (this.baseIdentificationTextField.getText().isEmpty()) {
      this.baseIdentificationTextField.setBackground(errorBackground);
      emptyFields = true;
    } else {
      this.baseIdentificationTextField.setBackground(normalBackground);
    }
    
    if (this.workingDirectoryTextField.getText().isEmpty()) {
      this.workingDirectoryTextField.setBackground(errorBackground);
      emptyFields = true;
    } else {
      this.workingDirectoryTextField.setBackground(normalBackground);
    }
    
    return emptyFields;
  }

  private void performInstallation() {
    try {
      
      //updateUnv administrator ""  "dcweb4-a:6400" eniq25-a.lmf.ericsson.se PM_E_EBSW:((3)) TP_BASE:B4_pkeys_unv "C:\TPIDE" XI Enterprise
      
      // Set the arguments
      this.installerArguments = new Arguments();
      String executableName = "C:\\temp\\TPIDE_BOIntf\\TPIDE_BOIntf.exe";

      this.installerArguments.addArgument(executableName, "updateEbsUnv");

      String userName = "\""+this.boUserNameTextField.getText()+"\"";
      this.installerArguments.addArgument(executableName, userName);

      String password = "\""+this.boPasswordTextField.getPassword().toString()+"\"";
      this.installerArguments.addArgument(executableName, password);

      String boRepository = "\""+this.boRepositoryTextField.getText()+"\"";
      this.installerArguments.addArgument(executableName, boRepository);

      String odbcConnection = this.odbcConnectionTextField.getText();
      this.installerArguments.addArgument(executableName, odbcConnection);

      String techPackId = "\""+(String)this.tpIdentificationComboBox.getSelectedItem()+"\"";
      this.installerArguments.addArgument(executableName, techPackId);

      String baseId = "\""+this.baseIdentificationTextField.getText()+"\"";
      this.installerArguments.addArgument(executableName, baseId);
      
      String workingdir = this.workingDirectoryTextField.getText();
      this.installerArguments.addArgument(executableName, workingdir);
      
      String BOVersion = "\"XI\"";
      this.installerArguments.addArgument(executableName, BOVersion);
      
      String BOAuthentication = "\"Enterprise\"";
      this.installerArguments.addArgument(executableName, BOAuthentication);
      
    } catch (Exception e) {
      System.out.println(e);
    }
    
    Thread installerThread = new Thread() {
      public void run() {
        try {
          installer.executeInstallScript("bointf/installScript.txt", installerArguments);
        }
        catch (IOException ioe) {
          printToTextArea(ioe.toString());
        }
      }
    };
    installerThread.start();
  }

  private void enableUpdateButton() {
    if (!this.updateHasBeenStartedFlag) {
      this.updateButton.setEnabled(true);
    }
  }
  
  private void disableUpdateButton() {
    this.updateButton.setEnabled(false);
  }
  
  private void toggleUpdateHasBeenStartedFlag() {
    this.updateHasBeenStartedFlag = true;
  }
  
  /**
   * Creates a button listener instance, with an overridden actionPerformed()
   * method. The method causes the defined executable resource file, to be
   * written into a temporary file, and executed.
   * 
   * @return ActionListener
   */
  private ActionListener createButtonListener() {
    ActionListener result = new ActionListener() {

      public void actionPerformed(ActionEvent ae) {
        String action = ae.getActionCommand();
        if (action.equals(UPDATE_BUTTON_LABEL)) {
          // Update button has been pressed.
          try {
            storeValues();
          } catch (IOException e) {
            e.printStackTrace();
          }
          toggleUpdateHasBeenStartedFlag();
          disableUpdateButton();
          performInstallation();        
        } else if (action.equals(WORKING_DIRECTORY_BUTTON_LABEL)) {
          // Working directory chooser button has been pressed
          JFileChooser fileChooser = new JFileChooser(); 
          fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
          fileChooser.setDialogTitle("Select working directory");
          fileChooser.setApproveButtonText("Select");
          int returnValue = fileChooser.showOpenDialog(UniverseUpdateLauncherApp.this);
          if (returnValue == JFileChooser.APPROVE_OPTION) {
            String selectedDirectory = fileChooser.getSelectedFile().getAbsolutePath();
            UniverseUpdateLauncherApp.this.workingDirectoryTextField.setText(selectedDirectory);
          }
        } else {
          // Unknown action, do nothing
        }
      }
    };
    return result;
  }
  
  /**
   * Creates a listener for techpack combo box. When the selected techpack in 
   * the combo box changes, the listener changes the base techpack field's value
   * to selected techpack's base techpack's name. 
   * 
   * @return ActionListener
   */
  private ActionListener createTechpackComboBoxListener() {
    ActionListener result = new ActionListener() {

      public void actionPerformed(ActionEvent ae) {
        int selectedIndex = UniverseUpdateLauncherApp.this.tpIdentificationComboBox.getSelectedIndex();
        String baseTechpackName = UniverseUpdateLauncherApp.this.basetechpackOptions[selectedIndex];
        UniverseUpdateLauncherApp.this.baseIdentificationTextField.setText(baseTechpackName);
        UniverseUpdateLauncherApp.this.checkEmptyFields();
      }
    };
    return result;
  }
  
  /**
   * Creates a listener for setting the update button enabled or disabled, 
   * according to whether or not there are empty fieds in the form.
   * 
   * @return
   */
  private DocumentListener createDocumentChangeListener() {
    DocumentListener result = new DocumentListener() {

      public void changedUpdate(DocumentEvent e) {
        boolean someFieldsAreEmpty = UniverseUpdateLauncherApp.this.checkEmptyFields();
        if(!someFieldsAreEmpty) {
          enableUpdateButton();
        }
        else {
          disableUpdateButton();
        }
      }

      public void insertUpdate(DocumentEvent e) {
        changedUpdate(e);
      }

      public void removeUpdate(DocumentEvent e) {
        changedUpdate(e);
      }

    };
    return result;
  }
  
  /**
   * Creates a window adapter instance, with an overridden windowClosing()
   * method, which causes the application to exit.
   * 
   * @return WindowAdapter
   */
  private WindowAdapter createWindowListener() {
    WindowAdapter result = new WindowAdapter() {

      public void windowClosing(WindowEvent e) {
        System.exit(0);
      }
    };
    return result;
  }


  /**
   * Appends the text in the application's text area component with a newline
   * character, and the string given as parameter.
   * 
   * @param text
   */
  private void printToTextArea(String text) {
    this.textArea.append("\n" + text);
  }


}
