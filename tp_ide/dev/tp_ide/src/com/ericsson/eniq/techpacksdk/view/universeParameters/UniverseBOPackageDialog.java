package com.ericsson.eniq.techpacksdk.view.universeParameters;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.Task;

import com.distocraft.dc5000.install.ant.ZipCrypter;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.techpacksdk.BusyIndicator;
import com.ericsson.eniq.techpacksdk.BusyIndicatorInputBlocker;
import com.ericsson.eniq.techpacksdk.TechPackIDE;
import com.ericsson.eniq.techpacksdk.User;
import com.ericsson.eniq.techpacksdk.common.Utils;

/**
 * This class is used for prompting the user Business Objects Package creation
 * parameters and creating the package.
 * 
 * @author eheitur
 * 
 */
@SuppressWarnings("serial")
public class UniverseBOPackageDialog extends JDialog {

  private final static String CRYPTMODE = "encrypt";

  private final static String ISPUBLICKEY = "false";

  final private Logger log = Logger.getLogger(UniverseBOPackageDialog.class.getName());

  private final JPanel myPanel;

  private final JTextField packageNameTextField;

  private final JTextField universeFolderTextField;

  private final JTextField reportFolderTextField;

  private final JTextField workingDirTextField;

  private final JTextField buildNumberTextField;

  private final JButton createButton;

  private final JButton cancelButton;

  private final Application application;

  private final Properties props;

  private final User user;

  private String versionTemplateName = "version.vm";

  private Versioning versioning;

  private String packageName = "";


  // Protected constructor to be used only for tests.
  protected UniverseBOPackageDialog(Versioning versioning, User user, javax.swing.JTextField textFieldMock) {
    myPanel = null;
    packageNameTextField = textFieldMock;
    universeFolderTextField = textFieldMock;
    reportFolderTextField = textFieldMock;
    workingDirTextField = textFieldMock;
    buildNumberTextField = textFieldMock;
    props = null;
    createButton = null;
    cancelButton = null;
    application = null;
    this.versioning = versioning;
    this.user = user;
  }

  /**
   * Constructor.
   * 
   * @param parentFrame
   * @param title
   * @param message
   */
  public UniverseBOPackageDialog(final Application application, final JFrame parentFrame, final Properties props,
      User user, Versioning versioning) {
    super(parentFrame, "Create Business Objects Package", true);
    log.log(Level.INFO, " UniverseBOPackageDialog Object Created" ); //EQEV-2863

    this.application = application;
    this.props = props;
    this.user = user;
    this.versioning = versioning;
    this.packageName = versioning.getTechpack_name();
    // IDE #681 BO package names incorrect when Press Create BO Package for EBS TPs
    int firstUS = this.packageName.indexOf('_');
    if (firstUS > -1) {
      // Replace before the first underscore with BO.
      // E.G. PM_E_EBSS -> BO_E_EBSS
      this.packageName = "BO" + this.packageName.substring(firstUS);
    } else {
      // Create the package name by changing the "DC" to "BO" in the current
      // techpack name.
      this.packageName = this.packageName.replace("DC", "BO");
    }
    final String universeFolder = props.getProperty("businessobjects.package.universe.folder", System
        .getProperty("user.home"));
    final String reportFolder = props.getProperty("businessobjects.package.report.folder", System
        .getProperty("user.home"));
    final String workingDir = props.getProperty("businessobjects.package.working.dir", System.getProperty("user.home"));
    String buildNumber = props.getProperty("businessobjects.package.build.number", "");

    // Strip the 'b' from the beginning of the build number if exists.
    if (buildNumber.toLowerCase().startsWith("b")) {
      buildNumber = buildNumber.substring(1);
    }

    // Create the panel and set the layout
    myPanel = new JPanel();
    myPanel.setLayout(new GridBagLayout());

    // Create the constraints for the layout
    final GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.insets = new Insets(2, 2, 2, 2);
    c.gridwidth = 1;

    // Package name text field
    c.gridy = 1;
    c.gridx = 0;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    myPanel.add(new JLabel("Package name"), c);

    packageNameTextField = new JTextField(packageName, 26);
    packageNameTextField.setPreferredSize(new Dimension(26, 28));

    c.gridx = GridBagConstraints.RELATIVE;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    myPanel.add(packageNameTextField, c);

    // Do not allow package name to be changed.
    // TODO: Check if this is wanted.
    packageNameTextField.setEnabled(false);

    // Universe folder text field
    c.gridy = 2;
    c.gridx = 0;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    myPanel.add(new JLabel("Universe folder"), c);

    universeFolderTextField = new JTextField(universeFolder, 26);
    universeFolderTextField.setPreferredSize(new Dimension(26, 28));
    universeFolderTextField.setName("Universe Folder"); //set name to catch it in GUI testing 

    c.gridx = GridBagConstraints.RELATIVE;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    myPanel.add(universeFolderTextField, c);

    // Universe folder button
    c.gridx = GridBagConstraints.RELATIVE;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    final JButton manage1 = new JButton("...");
    manage1.setAction(this.getAction("createBOPackageSelectUnivDir"));
    myPanel.add(manage1, c);

    // Report folder text field
    c.gridy = 3;
    c.gridx = 0;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    myPanel.add(new JLabel("Report folder"), c);

    reportFolderTextField = new JTextField(reportFolder, 26);
    reportFolderTextField.setPreferredSize(new Dimension(26, 28));
    reportFolderTextField.setName("Report Folder"); //set name to catch it in GUI testing 

    c.gridx = GridBagConstraints.RELATIVE;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    myPanel.add(reportFolderTextField, c);

    // Report folder button
    c.gridx = GridBagConstraints.RELATIVE;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    final JButton manage2 = new JButton("...");
    manage2.setAction(this.getAction("createBOPackageSelectRepoDir"));
    myPanel.add(manage2, c);

    // Working directory text field
    c.gridy = 4;
    c.gridx = 0;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    myPanel.add(new JLabel("Working directory"), c);

    workingDirTextField = new JTextField(workingDir, 26);
    workingDirTextField.setPreferredSize(new Dimension(26, 28));
    workingDirTextField.setName("Working Dir");//set name to catch it in GUI testing 

    c.gridx = GridBagConstraints.RELATIVE;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    myPanel.add(workingDirTextField, c);

    // Working directory button
    c.gridx = GridBagConstraints.RELATIVE;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    final JButton manage3 = new JButton("...");
    manage3.setAction(this.getAction("createBOPackageSelectWorkDir"));
    myPanel.add(manage3, c);

    // Build number text field
    c.gridy = 5;
    c.gridx = 0;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    myPanel.add(new JLabel("Build number"), c);

    buildNumberTextField = new JTextField(buildNumber, 26);
    buildNumberTextField.setPreferredSize(new Dimension(26, 28));
    buildNumberTextField.setName("Build Number");

    c.gridx = GridBagConstraints.RELATIVE;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    myPanel.add(buildNumberTextField, c);

    // Working directory button
    // c.gridx = GridBagConstraints.RELATIVE;
    // c.fill = GridBagConstraints.NONE;
    // c.weightx = 0;
    // final JButton manage4 = new JButton("...");
    // manage4.setAction(this.getAction("selectBuildNumber"));
    // myPanel.add(manage4, c);

    // Create the button panel
    c.gridy = 6;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridwidth = 3;
    c.weighty = 0;
    c.weightx = 1;

    final JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new GridBagLayout());

    myPanel.add(buttonPanel, c);

    // Add Cancel button
    c.gridx = 0;
    c.gridy = 0;
    c.fill = GridBagConstraints.NONE;
    c.anchor = GridBagConstraints.SOUTHEAST;
    c.weightx = 1;
    c.weighty = 0;
    c.gridwidth = 1;

    cancelButton = new JButton("Cancel");
    cancelButton.setName("UniverseBOPackageCancelButton");
    cancelButton.setAction(this.getAction("discard"));
    buttonPanel.add(cancelButton, c);

    c.gridx = 1;
    c.weightx = 0;

    createButton = new JButton("Create Package");
    createButton.setName("UniverseBOPackageCreateButton");
    createButton.setAction(this.getAction("createBOPackageCreate"));
    buttonPanel.add(createButton, c);

    getContentPane().add(myPanel);

    // Show the dialog
    pack();
    Utils.center(this);
    setAlwaysOnTop(true);
    setModal(false); // EQEV-2866 Changes
    setVisible(true);
    UniverseGenerateView.busyIndicator.setVisible(true); //EQEV-2866








  }








  private javax.swing.Action getAction(final String actionName) {
    log.log(Level.FINE, " getAction " + actionName+" action performed"); //EQEV-2863
    if (application != null) {
      return application.getContext().getActionMap(this).get(actionName);
    }
    return null;
  }

  @Action
  public Task<Boolean, Void> createBOPackageCreate() {
    log.log(Level.INFO, "createBOPackageCreate action performed"); //EQEV-2863

    // Strip the 'b' from the beginning of the build number if exists.
    String buildNumber = buildNumberTextField.getText().toLowerCase();
    if (buildNumber.startsWith("b")) {
      buildNumber = buildNumber.substring(1);
    }

    // Store the values to the connection.properties file
    props.setProperty("businessobjects.package.universe.folder", universeFolderTextField.getText());
    props.setProperty("businessobjects.package.report.folder", reportFolderTextField.getText());
    props.setProperty("businessobjects.package.working.dir", workingDirTextField.getText());
    props.setProperty("businessobjects.package.build.number", buildNumber);
    storeConnectionProperties();

    // The private key must exist for this user.
    // If the key exists, then execute the create task.
    if (user.getPrivateKeyMod() == null || user.getPrivateKeyExp() == null) {
      log.log(Level.SEVERE, "Key file missing. Creation of an encrypted BO Package is not possible.");
      JOptionPane.showMessageDialog(this, "Encrypting requires key file.", "Missing File", JOptionPane.ERROR_MESSAGE);
    } else {

      final Task<Boolean, Void> createTask = new CreateTask(application);
      BusyIndicator busyIndicator = new BusyIndicator();

      this.setGlassPane(busyIndicator);
      createTask.setInputBlocker(new BusyIndicatorInputBlocker(createTask, busyIndicator));

      return createTask;
    }
    return null;
  }

  @Action
  public void discard() {
    log.log(Level.INFO, "discard action performed"); //EQEV-2863
    setVisible(false);
	UniverseGenerateView.busyIndicator.setVisible(false); //EQEV-2866
	
  }

  @Action
  public void createBOPackageSelectUnivDir() {
    log.log(Level.INFO, "createBOPackageSelectUnivDir action performed"); //EQEV-2863
    selectFolder(universeFolderTextField.getText(), universeFolderTextField, "Select Universe Directory");
  }

  @Action
  public void createBOPackageSelectRepoDir() {
    log.log(Level.INFO, "createBOPackageSelectRepoDir action performed"); //EQEV-2863
    selectFolder(reportFolderTextField.getText(), reportFolderTextField, "Select Report Directory");
  }

  @Action
  public void createBOPackageSelectWorkDir() {
    log.log(Level.INFO, "createBOPackageSelectWorkDir action performed"); //EQEV-2863
    selectFolder(workingDirTextField.getText(), workingDirTextField, "Select Working Directory");
  }

  // @Action
  // public void createBOPackageSelectBuildNum() {
  // selectFolder(buildNumberTextField.getText(), buildNumberTextField);
  // }

  // @Action
  // public void selectbofolder() {
  // selectFolder(bofolderTextField.getText(), bofolderTextField);
  // }

  // public void selectKeyFile(final String defaultlocation, final JTextField
  // defaultvalue) {
  //
  // while (true) {
  // final JFileChooser jfc = new JFileChooser(new File(defaultlocation));
  // jfc.setFileFilter(new FileFilter() {
  //
  // public boolean accept(final File f) {
  // return f.isDirectory() || (!f.isDirectory() && f.exists() &&
  // f.getName().endsWith(".key"));
  // }
  //
  // public String getDescription() {
  // return "Key file";
  // }
  // });
  //
  // final int returnVal = jfc.showDialog(this, "Select keyfile");
  //
  // if (returnVal == JFileChooser.APPROVE_OPTION) {
  // final File f = jfc.getSelectedFile();
  // defaultvalue.setText(f.getAbsolutePath());
  //
  // }
  //
  // break;
  //
  // }
  //
  // }

  /**
   * @param defaultlocation
   * @param defaultvalue
   * @param title
   */
  public void selectFolder(final String defaultlocation, final JTextField defaultvalue, final String title) {


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

      final int returnVal = jfc.showDialog(this, title);

      if (returnVal == JFileChooser.APPROVE_OPTION) {
        final File f = jfc.getSelectedFile();
        defaultvalue.setText(f.getAbsolutePath());

      }

      break;

    }

  }

  /**
   * 
   */
  private void storeConnectionProperties() {
    log.log(Level.INFO, "storeConnectionProperties"); //EQEV-2863
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

  /**
   * The task for creating the BO Package.
   * 
   * @author eheitur
   * 
   */
  private class CreateTask extends Task<Boolean, Void> {

    public CreateTask(Application app) {
      super(app);
      log.log(Level.INFO, "CreateTask object created"); //EQEV-2863
    }
    
    @Override
    protected Boolean doInBackground() throws Exception {

      log.log(Level.INFO, "Creation of BO Package started.");
      boolean succeeded = true;
      try {
        succeeded = doCreatePackage();        
      } catch (Exception ex) {
        succeeded = false;
      }
      log.log(Level.INFO, "Creation of a BO Package finished.");

      // Hide the dialog.
      setVisible(false);
      return succeeded;
    }

    @Override
    protected void succeeded(Boolean boPackageCreatedOk) {
      // Checks if the package was created successfully:
      log.fine("Finished creating BO package, boPackageCreatedOk = " + boPackageCreatedOk);
      if (boPackageCreatedOk) {
        JOptionPane.showMessageDialog(myPanel, "Successfully created BO package", "Business Object package",
            JOptionPane.INFORMATION_MESSAGE);
      } else {
        JOptionPane.showMessageDialog(myPanel, "Creation of BO Package failed", "Error", JOptionPane.ERROR_MESSAGE);
      }
      UniverseGenerateView.busyIndicator.setVisible(false); //EQEV-2866
    }

    @Override
    protected void failed(java.lang.Throwable cause) {
      JOptionPane.showMessageDialog(myPanel, "Creation of BO Package failed", "Error", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    protected void cancelled() {
      JOptionPane
          .showMessageDialog(myPanel, "Creation of BO Package was cancelled", "Error", JOptionPane.ERROR_MESSAGE);
    }    
  }
  
  /**
   * Creates the BO package.
   * @return succeeded    This is true if creating the package completed ok.
   * @throws IOException
   */
  protected boolean doCreatePackage() throws IOException {

    log.log(Level.INFO, "Creation of BO Package started.");

    boolean succeeded = true;
    ZipOutputStream out = null;
    ZipOutputStream outUn = null;
    String outputFile = "";
    try {
      String buildNumber = getBuildNumber();

      outputFile = getOutputFilename(buildNumber, true);
      // Create the output file (tpi-file), encrypted file
      out = createZipOutputStream(outputFile);

      String unencryptedOutputFile = getOutputFilename(buildNumber, false);
      // Create a second unencrypted output file (tpi-file)
      outUn = createZipOutputStream(unencryptedOutputFile);

      // Add the universe directory to the package
      zipDir("unv", universeFolderTextField.getText(), out);
      zipDir("unv", universeFolderTextField.getText(), outUn);

      // Add the report directory to the package
      zipDir("rep", reportFolderTextField.getText(), out);
      zipDir("rep", reportFolderTextField.getText(), outUn);

      // Create the version.properties file.
      createVersionProperties("install/version.properties", out);
      createVersionProperties("install/version.properties", outUn);
    } catch (Exception ex) {
      log.log(Level.SEVERE, "Creation of a BO Package failed: ", ex.toString());
      succeeded = false;
    } finally {
      out.close();
      outUn.close();
    }
    
    try {      
      encryptZipFile(outputFile);
    } catch (Exception exc) {
      log.log(Level.SEVERE, "Creation of a BO Package failed: ", exc.toString());
      succeeded = false;
    }
    
    log.log(Level.INFO, "Creation of a BO Package finished.");
    return succeeded;
  }

  /**
   * Gets the build number from the buildNumberTextField.
   * @return The build number in format b<number>.
   */
  private String getBuildNumber() {
    // Append the 'b' to the beginning of the build number if needed.
    log.log(Level.INFO, "Getting Build Number:"); //EQEV-2863
    String buildNumber = buildNumberTextField.getText().toLowerCase();
    if (!buildNumber.startsWith("b")) {
      buildNumber = "b" + buildNumber;
      log.log(Level.FINEST, "Got Build Number: "+buildNumber); //EQEV-2863
    }
    return buildNumber;
  }

  /**
   * Gets the output filename of the BO package.
   * @param buildNumber   The build number.
   * @param encrypted     True if the tech pack will be encrypted. 
   * @return outputFile   The output filename.
   */
  private String getOutputFilename(String buildNumber, boolean encrypted) {
    log.log(Level.FINEST, "Getting output filename of the BO package which has build number: "+buildNumber); //EQEV-2863
    StringBuffer outputFile = new StringBuffer();
    outputFile.append(workingDirTextField.getText());
    outputFile.append(File.separatorChar);
    if (!encrypted) {
      outputFile.append("unencrypted_");
    }
    outputFile.append(packageName);
    outputFile.append("_");
    outputFile.append(versioning.getTechpack_version());
    outputFile.append("_");
    outputFile.append(buildNumber);
    outputFile.append(".tpi");

    log.log(Level.FINEST, "Got output filename: "+outputFile.toString()); //EQEV-2863


    return outputFile.toString();

  }

  /**
   * Encrypts the BO zip file.
   * @param outputFile  The name of the file to encrypt.
   */
  private void encryptZipFile(String outputFile) {
      log.log(Level.INFO, "encryptZipFile: Encrypting the BO zip file"); //EQEV-2863
    // Encrypt the zip file.
    if (outputFile != null) {
      ZipCrypter zipCrypter = createZipCrypter();
      zipCrypter.setFile(outputFile);
      zipCrypter.setCryptType(CRYPTMODE);
      zipCrypter.setIsPublicKey(ISPUBLICKEY);

      zipCrypter.setKeyModulate(String.valueOf(user.getPrivateKeyMod()));
      zipCrypter.setKeyExponent(String.valueOf(user.getPrivateKeyExp()));
      zipCrypter.execute();
    }
  }

  /**
   * Creates the version.properties file to the zip output stream.
   * 
   * @param outputname
   *          relative path + filename inside the zip package.
   * @param out
   *          zip output stream
   * @throws Exception
   */
  private void createVersionProperties(String outputname, ZipOutputStream out) throws Exception {
    log.log(Level.INFO, "CreateTask createVersionProperties: Creating the version.properties file to the zip output stream."); //EQEV-2863
    ZipEntry entry = new ZipEntry(outputname);
    out.putNextEntry(entry);

    StringWriter strw = new StringWriter();
    VelocityContext context = new VelocityContext();

    context.put("buildnumber", buildNumberTextField.getText().replace("b", ""));
    context.put("buildtag", buildNumberTextField.getText());
    context.put("techpackname", packageName);
    context.put("licenseName", versioning.getLicensename());
    context.put("author", user.getName());
    context.put("version", versioning.getTechpack_version());
    context.put("metadataversion", "");

    boolean isMergeOk = mergeVelocityTemplate(strw, context);

    if (isMergeOk) {
      out.write(strw.toString().getBytes());
    }
  }

  /**
   * Add a directory to a zip output stream.
   * 
   * @param dir2zip
   *          the directory to be added to the zip package
   * @param zos
   *          the zip output stream
   * @throws Exception
   */
  public void zipDir(String targetDirName, String dir2zip, ZipOutputStream zos) throws Exception {
     log.log(Level.FINEST, " Added a "+dir2zip+" directory to "+zos+" zip file."); //EQEV-2863

    // Create the directory entry to the zip.
    // ZipEntry dirEntry = new ZipEntry(targetDirName +
    // System.getProperty("file.separator") + ".");
    // ZipEntry dirEntry = new ZipEntry(targetDirName + "/.");
    // zos.putNextEntry(dirEntry);

    // Create a new File object based on the directory we have to zip
    File zipDir = createFile(dir2zip);
    // Get a listing of the directory content
    String[] dirList = zipDir.list();
    byte[] readBuffer = new byte[2156];
    int bytesIn = 0;
    // Loop through dirList, and zip the files

    for (int i = 0; i < dirList.length; i++) {
      File f = createFile(zipDir, dirList[i]);
      if (f.isDirectory()) {

        // In case this is a directory, the call the same function recursively.
        // zipDir(targetDirName + System.getProperty("file.separator") +
        // f.getName(), dir2zip
        // + System.getProperty("file.separator") + f.getName(), zos);
        zipDir(targetDirName + "/" + f.getName(), f.getPath(), zos);
        continue;
      }
      // If we reached here, the file object was not a directory.
      // Create a FileInputStream on top of file object.
      FileInputStream fis = createFileInputStream(f);
      // Create a new zip entry
      // ZipEntry anEntry = new ZipEntry(targetDirName +
      // System.getProperty("file.separator") + f.getName());
      ZipEntry anEntry = new ZipEntry(targetDirName + "/" + f.getName());

      // Place the zip entry in the ZipOutputStream object
      zos.putNextEntry(anEntry);

      // Write the content of the file to the ZipOutputStream
      while ((bytesIn = fis.read(readBuffer)) != -1) {
        zos.write(readBuffer, 0, bytesIn);
      }
      // Close the stream
      fis.close();
    }
  }
  
  /**
   * Protected method to merge the velocity template.
   * Can be overridden by tests.
   * @param strw        A StringWriter.
   * @param context     VelocityContext instance
   * @return isMergeOk  true if the merge was completed without errors.
   */
  protected boolean mergeVelocityTemplate(StringWriter strw, VelocityContext context) {

    boolean isMergeOk = true;
    try {
      isMergeOk = Velocity.mergeTemplate(versionTemplateName, Velocity.ENCODING_DEFAULT, context, strw);      
    } catch (Exception ex) {
      log.log(Level.WARNING, "Failed to merge template: " + ex.toString());
      isMergeOk = false;
    }
    return isMergeOk;
  }
  
  /**
   * Protected creator method to create a new ZipCrypter instance.
   * Can be overridden for testing.
   * @return zipCrypter A new ZipCrypter instance.
   */
  protected ZipCrypter createZipCrypter() {

    ZipCrypter zipCrypter = new ZipCrypter();
    return zipCrypter;
  }
  
  /**
   * Protected creator method to create a new zip file.
   * Can be overridden for testing.
   * @param outputFilename   The filename of the zip file.
   * @return out             New instance of ZipOutputStream.
   * @throws FileNotFoundException 
   * @throws Exception
   */
  protected ZipOutputStream createZipOutputStream(String outputFilename) throws FileNotFoundException {
    // Create the zip output stream for the file.
    log.log(Level.INFO, " Creating ZipOutputStream"); //EQEV-2863
    ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(outputFilename)));
    log.log(Level.INFO, " Created ZipOutputStream: "+out); //EQEV-2863

    return out;
  }

  /**
   * Protected creator method to create a new file from a directory.
   * Can be overridden for testing.
   * @param directory
   * @return New file.
   */
  protected File createFile(String directory) {
    log.log(Level.FINEST, " created a file from a directory: "+directory); //EQEV-2863
    File f = new File(directory);
    return f;
  }

  /**
   * Protected creator method to create a new file from a directory and file
   * name.
   * Can be overridden for testing.
   * @param parent      The parent directory.
   * @param child       The file name.
   * @return f          A new file object.
   */
  protected File createFile(File parent, String child) {
    log.log(Level.FINEST, " created a new file from a directory: "+parent+" and file name: "+child); //EQEV-2863
    File f = new File(parent, child);
    return f;
  }

  /**
   * Protected creator method to create a new FileInputStream.
   * @param file        A reference to a File object.
   * @return fis        A new FileInputStream 
   * @throws FileNotFoundException 
   */
  protected FileInputStream createFileInputStream(File file) throws FileNotFoundException {
    log.log(Level.FINEST, "createFileInputStream: "+file); //EQEV-2863
    FileInputStream fis = new FileInputStream(file);
    return fis;
  }

}

