package com.ericsson.eniq.techpacksdk;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.Context;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.Task;

import ssc.rockfactory.RockException;

import com.distocraft.dc5000.install.ant.ZipCrypter;
import com.distocraft.dc5000.repository.dwhrep.Datainterface;
import com.distocraft.dc5000.repository.dwhrep.Interfacedependency;
import com.distocraft.dc5000.repository.dwhrep.InterfacedependencyFactory;
import com.distocraft.dc5000.repository.dwhrep.Techpackdependency;
import com.distocraft.dc5000.repository.dwhrep.TechpackdependencyFactory;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.techpacksdk.common.Constants;
import com.ericsson.eniq.techpacksdk.common.Utils;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;

@SuppressWarnings("serial")
public class CreateInstallationPackageView extends JPanel {

  private static final Logger LOGGER = Logger
  .getLogger(CreateInstallationPackageView.class.getName());

  private final static String CRYPTMODE = "encrypt";
  private final static String ISPUBLICKEY = "false";

  private JCheckBox crypt;

  private final SingleFrameApplication application;

  private boolean saveEnabled = false;

  // private LimitedSizeTextField buildTagF;

  private final LimitedSizeTextField builNumberF;

  private final JFrame frame;

  private final DataModelController dataModelController;

  private final Object id;

  private final JTextField wdir;

  private final ResourceMap resourceMap;

  private String newBuildTag = "";

  private String techPackName = "";

  private String oldVersion = "";

  //comes from events
  private final JTextField externalFilesBase_TF;
  private final BundleExternalFiles externalFilesHelper;

  private static final String INSTALL_TEMPLATE_NAME = "install.vm";
  private static final String INSTALL_TEMPLATE_NAME_ALARM_INTERFACES = "install_AlarmInterfaces.vm";
  private static final String INSTALL_TEMPLATE_NAME_DC_Z_ALARM = "install_DC_Z_ALARM.vm";  
  private static final String VERSION_TEMPLATE_NAME = "version.vm";
  private static final String INSTALL_XML_FILEPATH = "install/install.xml";
  private static final String VERSION_PROPERTIES_FILEPATH = "install/version.properties";

  public String intfOutputFile = "";

  // Only used for testing purpose
  protected CreateInstallationPackageView(final DataModelController dmc, final Object id, final JTextField textFieldMock,
      final LimitedSizeTextField limitedTextFieldMock, final JCheckBox checkBoxMock) {
    this.id = id;
    this.wdir = textFieldMock;
    this.builNumberF = limitedTextFieldMock;
    this.crypt = checkBoxMock;
    this.dataModelController = dmc;
    externalFilesBase_TF=new JTextField("ERROR");
    externalFilesHelper = new BundleExternalFiles(techPackName, LOGGER);
    resourceMap=null;
    application=null;
    frame=new JFrame();
  }

  public CreateInstallationPackageView(final SingleFrameApplication application,
      final DataModelController dataModelController, final JFrame frame, final Object id) {
    super(new GridBagLayout());

    this.frame = frame;

    this.id = id;

    this.application = application;

    this.dataModelController = dataModelController;

    resourceMap = dataModelController.getResourceMap();

    // ************** Text panel **********************

    int fieldSize = 25;

    JPanel txtPanel = new JPanel(new GridBagLayout());

    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2, 2, 2, 2);
    c.weightx = 0;
    c.weighty = 0;

    JLabel wLabel = new JLabel("Directory");
    c.gridx = 0;
    c.gridy = 0;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    txtPanel.add(wLabel, c);

    boolean isVersioning = true;
    if(id instanceof Versioning){
      final Versioning version = (Versioning)id;
      oldVersion = version.getVersionid();
      oldVersion = oldVersion.substring(oldVersion.lastIndexOf(":") + 1);
      techPackName = version.getTechpack_name();
    } else if(id instanceof Datainterface){
      isVersioning = false;
      oldVersion = ((Datainterface) id).getInterfaceversion();
      crypt = new JCheckBox();
      crypt.setSelected(false);
    }

    externalFilesHelper = new BundleExternalFiles(techPackName, LOGGER);    

    c.gridx = 1;
    c.weightx = 1;
    c.fill = GridBagConstraints.BOTH;
    wdir = new JTextField(dataModelController.getWorkingDir()
        .getAbsolutePath(), 16);
    wdir.setName("WDIR"); // To set the name of text field so that IDE Gui test can get and put some value in this
    wdir.setEditable(false);
    txtPanel.add(wdir, c);

    JButton b = new JButton(getAction("selectworkingdir"));
    c.gridx = 2;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    txtPanel.add(b, c);

    /*
    JLabel name = new JLabel("Build Tag");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 1;
    c.weightx = 0;
    txtPanel.add(name, c);
     */

    builNumberF = new LimitedSizeTextField("0,1,2,3,4,5,6,7,8,9","",fieldSize, Versioning
        .getTechpack_versionColumnSize(), true);
    builNumberF.setText("");
    builNumberF.setName("BuildNumber"); // To set the name of text field so that IDE Gui test can get and put some value in this
    

    /*
    buildTagF = new LimitedSizeTextField(fieldSize, Versioning
        .getTechpack_versionColumnSize(), true);
    buildTagF.setText("");
    buildTagF.getDocument().addDocumentListener(new MyDocumentListener());
     */

    
    if (isVersioning) {

      JLabel cn = new JLabel("Encrypt");
      c.fill = GridBagConstraints.NONE;
      c.gridx = 0;
      c.gridy = 3;
      c.weightx = 1;
      txtPanel.add(cn, c);

      final User user = dataModelController.getUser();

      crypt = new JCheckBox();
      crypt.setName("Crypt"); // To set the name of CheckBox field so that IDE Gui test can get and put some value in this
      if (user.getPrivateKeyMod() == null || user.getPrivateKeyExp() == null) {
        crypt.setSelected(false);
        crypt.setEnabled(false);
        cn.setEnabled(false);
      } else {
        crypt.setSelected(true);
        crypt.setEnabled(true);
        cn.setEnabled(true);
      }
      c.fill = GridBagConstraints.NONE;
      c.gridx = 1;
      c.gridy = 3;
      c.weightx = 1;
      txtPanel.add(crypt, c);

    } else{
    	crypt = new JCheckBox();
    	crypt.setSelected(false);
    }

    /*
    c.fill = GridBagConstraints.NONE;
    c.gridx = 1;
    c.gridy = 1;
    c.weightx = 1;
    txtPanel.add(buildTagF, c);
    */

    JLabel bn = new JLabel("Build Number");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 2;
    c.weightx = 0;
    txtPanel.add(bn, c);

    builNumberF.getDocument().addDocumentListener(new MyDocumentListener());

    c.fill = GridBagConstraints.NONE;
    c.gridx = 1;
    c.gridy = 2;
    c.weightx = 1;
    txtPanel.add(builNumberF, c);
    
    final String tooltip =  resourceMap.getString("selectexternalfiledir.tooltip");


    final JLabel extFiles_l = new JLabel("External Files Base");
    extFiles_l.setToolTipText(tooltip);
    externalFilesBase_TF = new JTextField("", 16);
    externalFilesBase_TF.setName("ExternalFileBase"); // To set the name of text field so that IDE Gui test can get and put some value in this
    externalFilesBase_TF.setToolTipText(tooltip);
    externalFilesBase_TF.setEditable(false);
    externalFilesBase_TF.setText(getBuildProperty(BundleExternalFiles.PATH_TO_TECH_PACK_FILES_PROPERTY_NAME, ""));
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 4;
    txtPanel.add(extFiles_l, c);
    c.gridx = 1;
    c.fill = GridBagConstraints.BOTH;
    txtPanel.add(externalFilesBase_TF, c);
    c.fill = GridBagConstraints.NONE;
    final JButton eButton = new JButton(getAction("selectexternalfiledir"));
    c.gridx = 2;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    txtPanel.add(eButton, c);
    

    // ************** buttons **********************

    JButton cancel;
    JButton create;

    cancel = new JButton("Cancel");
    // cancel.setActionCommand("cancel"); 
    cancel.setAction(getAction("discard"));
    cancel.setToolTipText("Cancel");
    cancel.setName("Cancel"); // To set the name of button so that IDE Gui test can get this

    create = new JButton("Create");
    // create.setActionCommand("create");
    create.setAction(getAction("create"));
    create.setToolTipText("Create");
    create.setName("Create"); // To set the name of button so that IDE Gui test can get this
 
    // ************** button panel **********************

    JPanel buttonPanel = new JPanel(
        new FlowLayout(FlowLayout.RIGHT, 10, 10));
    buttonPanel.add(create);
    buttonPanel.add(cancel);

    // ************** Main panel with inner panel and button panel
    // **********************

    // insert both panels in main view

    final GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridheight = 1;
    gbc.gridwidth = 2;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.anchor = GridBagConstraints.NORTHWEST;

    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.gridx = 0;
    gbc.gridy = 0;

    this.add(txtPanel, gbc);

    gbc.weightx = 0;
    gbc.weighty = 0;
    gbc.gridx = 0;
    gbc.gridy = 1;

    this.add(buttonPanel, gbc);
  }
  
  private String getBuildProperty(final String propName, final String defValue){
	    final Properties p = externalFilesHelper.createPropertiesObjectAndLoadFile();
	    return p.getProperty(propName, defValue);
	  }

  @Action
  public void selectworkingdir() {

    while (true) {
      final JFileChooser jfc = new JFileChooser(new File(wdir.getText()));
      jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
      jfc.setFileFilter(new FileFilter() {

    	  @Override
        public boolean accept(File f) {
          return f.isDirectory() && f.exists() && f.canWrite();
        }

    	  @Override
        public String getDescription() {
          return resourceMap.getString("LoginPanel.filedescription");
        }
      });

      final int returnVal = jfc.showDialog(this, resourceMap
          .getString("LoginPanel.filechoose"));

      if (returnVal == JFileChooser.APPROVE_OPTION) {
        final File f = jfc.getSelectedFile();

        if (!f.isDirectory() || !f.exists() || !f.canWrite()) {
          JOptionPane.showMessageDialog(frame, resourceMap
              .getString("Login.workingdirerror.message"),
              resourceMap
                  .getString("Login.workingdirerror.title"),
              JOptionPane.ERROR_MESSAGE);
          continue;
        }

        wdir.setText(f.getAbsolutePath());

      }

      break;

    }
  }
  
  @Action
  public void selectexternalfiledir() {
      final JFileChooser fc = new JFileChooser(externalFilesBase_TF.getText());
      fc.setMultiSelectionEnabled(false);
      fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
      final int option = fc.showDialog(this, "Use");
      if(option == JFileChooser.APPROVE_OPTION){
        final String extDir = fc.getSelectedFile().getAbsolutePath();
        externalFilesBase_TF.setText(extDir);
        externalFilesHelper.setExternalFilesBaseDir(extDir);
      }
  }

  private class SaveTask extends Task<Boolean, Void> {

    public SaveTask(Application app) {
      super(app);
    }
        
    @Override
    protected Boolean doInBackground() {
      LOGGER.log(Level.INFO, "create");
      boolean succeeded = true;

      try {
        if (id instanceof Versioning) {
          LOGGER.log(Level.INFO, "Tech Pack");
          succeeded = createTPInstallFile();
        } else if (id instanceof Datainterface) {
          LOGGER.log(Level.INFO, "Interface");
          createInterfaceInstallFile();
        }
      } catch (Exception ex) {
        LOGGER.log(Level.WARNING, "Error creating installation package: " + ex.toString());
        succeeded = false;
      } finally {
        frame.dispose();
      }
      LOGGER.log(Level.INFO, "installed");
      return succeeded;
    }
    
    @Override
    protected void succeeded(final Boolean tpiPackageCreatedOk) {
      LOGGER.fine("Finished creating tpi package, tpiPackageCreatedOk = " + tpiPackageCreatedOk);
      if (tpiPackageCreatedOk) {
        JOptionPane.showMessageDialog(frame, "Created install file successfully", "Install",
            JOptionPane.INFORMATION_MESSAGE);
      } else {
        JOptionPane.showMessageDialog(frame, "Error creating install file ", "Error", JOptionPane.ERROR_MESSAGE);
      }
    }

    @Override
    protected void failed(final java.lang.Throwable cause) {
      JOptionPane.showMessageDialog(frame, "Error creating install file ", "Error", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    protected void cancelled() {
      JOptionPane.showMessageDialog(frame, "Error creating install file ", "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  /**
   * @return
   * @throws Exception
   */
  protected boolean createTPInstallFile() throws IOException {
    LOGGER.log(Level.FINE, "Creating tech pack install file");
    boolean successful = true;

    ZipOutputStream out = null;
    Versioning versioning = null;
    String outputFile = "";
    VelocityContext context = new VelocityContext();
    
    try {
      versioning = (Versioning) id;
      newBuildTag = versioning.getTechpack_version() + "_b" + builNumberF.getText();

      final String techpackName = versioning.getTechpack_name();

      // Set the output file name
      outputFile = getOutputFilename(techpackName, crypt.isSelected());
      out = createZipOutputStream(outputFile);
      final CreateTPInstallFile ci = createCreateTPInstallFile(versioning);
      ci.create("sql/", out);

      final CreateSetInstallFile cs = createCreateSetInstallFile(
          techpackName, oldVersion, oldVersion, "(("+builNumberF.getText()+"))", dataModelController);
      cs.create("set/", out, false);

      // Create velocity context for AlarmInterfaces or DC_Z_ALARM tech packs:
      if (techpackName.equalsIgnoreCase(Constants.ALARM_INTERFACES_TECHPACK_NAME) || 
          techpackName.equalsIgnoreCase(Constants.DC_Z_ALARM_TECHPACK_NAME)) {
      context.put("configurationDirectory", "${configurationDirectory}");
      context.put("binDirectory", "${binDirectory}");
      }
        
      // Get the Install Description from Versioning for a techpack
      final String installXmlContent = versioning.getInstalldescription();

      // Create the install.xml file:
      if (installXmlContent != null && installXmlContent.length() > 0) {
        createInstallXML(INSTALL_XML_FILEPATH, out, installXmlContent);
      } else if (techpackName.equalsIgnoreCase(Constants.ALARM_INTERFACES_TECHPACK_NAME)) {
        // Check if the tech pack is the Alarm Interfaces tech pack:
        createInstallXML(INSTALL_XML_FILEPATH, out, INSTALL_TEMPLATE_NAME_ALARM_INTERFACES, context);  
      } else if (techpackName.equalsIgnoreCase(Constants.DC_Z_ALARM_TECHPACK_NAME)){
        // Check if the tech pack is the DC_Z_ALARM tech pack:
        createInstallXML(INSTALL_XML_FILEPATH, out, INSTALL_TEMPLATE_NAME_DC_Z_ALARM, context);
      } else {
        // For all other techpacks, use default install.vm file:
        createInstallXML(INSTALL_XML_FILEPATH, out, INSTALL_TEMPLATE_NAME, null);        
      }
      createVersionProperties(VERSION_PROPERTIES_FILEPATH, out, versioning);

      if (externalFilesHelper.areExternalFilesPresentToBundle()) {
        externalFilesHelper
            .zipExternalFilesAndFoldersForTechPack(BundleExternalFiles.PATH_FOR_ZIPPED_ITEMS_IN_ZIP, out);
      }
    } catch (Exception ex) {
      LOGGER.log(Level.FINE, "Error creating tech pack install file: " + ex.toString());
      successful = false;
    } finally {
      out.close();
    }

    try {
      if (crypt.isSelected()) {
        encryptTechpackInstallFile(versioning, outputFile);
      }
    } catch (Exception ex) {
      LOGGER.log(Level.FINE, "Error encrypting tech pack install file: " + ex.toString());
      successful = false;
    }

    LOGGER.log(Level.FINE, "Finished creating interface install file");
    return successful;
  }

  /**
   * @return
   * @throws Exception
   */
  protected boolean createInterfaceInstallFile() throws IOException {
    LOGGER.log(Level.FINE, "Creating interface install file");
    boolean successful = true;
    ZipOutputStream out = null;

    final Datainterface datainterface = (Datainterface) id;
    newBuildTag = datainterface.getRstate() + "_b" + builNumberF.getText();

    intfOutputFile = getOutputFilename(datainterface.getInterfacename(), false);

    try {
      out = createZipOutputStream(intfOutputFile);
      CreateINTFInstallFile ci = createCreateINTFInstallFile();
      ci.create("interface/", out);

      CreateSetInstallFile cs = createCreateSetInstallFile(
              datainterface.getInterfacename(), datainterface
                      .getInterfaceversion(), oldVersion, "(("+builNumberF.getText()+"))", dataModelController);
      
      cs.create("interface/", out, true);

      // Get the Install Description from Versioning for a techpack
      final String installXmlContent = datainterface.getInstalldescription();
      // Create the install.xml file:
      if (installXmlContent != null && installXmlContent.length() > 0) {
        createInstallXML(INSTALL_XML_FILEPATH, out, installXmlContent);
      } else {
        createInstallXML(INSTALL_XML_FILEPATH, out, INSTALL_TEMPLATE_NAME, null);
      }

      createVersionProperties(VERSION_PROPERTIES_FILEPATH,
          out, datainterface);
    } catch (Exception ex) {
      LOGGER.log(Level.FINE, "Error creating interface install file: " + ex.toString());
      successful = false;
    } finally {
      out.close();
    }
    LOGGER.log(Level.FINE, "Finished creating interface install file");
    return successful;
  }
  
  /**
   * @param itemName
   * @param encrypted
   * @return
   */
  protected String getOutputFilename(final String itemName, final boolean encrypted) {
    final StringBuffer outputFile = new StringBuffer();
    outputFile.append(wdir.getText());
    outputFile.append(File.separatorChar);
    //HM78271 -Interface TPI file getting named incorrectly 
    if (!encrypted && !itemName.startsWith("INTF")) {
      outputFile.append("Unencrypted_");
    }
    outputFile.append(itemName);
    outputFile.append("_");
    outputFile.append(newBuildTag);
    outputFile.append(".tpi");

    return outputFile.toString();
  }

  /**
   * @param versioning
   * @param outputFile
   * @throws Exception
   */
  private void encryptTechpackInstallFile(final Versioning versioning, final String outputFile) throws IOException {
    LOGGER.log(Level.FINE, "Encrypting tech pack install file");

    if (outputFile != null) {
      // System TechPacks should be encrypted in ENIQ 2.0
      if (versioning.getTechpack_type().equals("PM") || versioning.getTechpack_type().equals("CM")
          || versioning.getTechpack_type().equals("Topology") || versioning.getTechpack_type().equals("EVENT")
          || versioning.getTechpack_type().equals(Constants.ENIQ_EVENT)) {
        final User user = dataModelController.getUser();
        final String privateKeyMod = String.valueOf(user.getPrivateKeyMod());
        final String privateKeyExp = String.valueOf(user.getPrivateKeyExp());

        final ZipCrypter zipCrypter = setupZipCrypter(outputFile, privateKeyMod, privateKeyExp);
        zipCrypter.execute();
      }
    }
    LOGGER.log(Level.FINE, "Finished encrypting tech pack install file");
  }

  /**
   * @param outputFile
   * @param privateKeyMod
   * @param privateKeyExp
   * @return
   */
  protected ZipCrypter setupZipCrypter(final String outputFile, final String privateKeyMod, final String privateKeyExp) {
    final ZipCrypter zipCrypter = new ZipCrypter();
    zipCrypter.setFile(outputFile);
    zipCrypter.setCryptType(CRYPTMODE);
    zipCrypter.setIsPublicKey(ISPUBLICKEY);

    zipCrypter.setKeyModulate(privateKeyMod);
    zipCrypter.setKeyExponent(privateKeyExp);
    return zipCrypter;
  }

  
  public class VersionInfo {

    private String version;

    private String techpackname;

    public void setVersion(String version) {
      this.version = version;
    }

    public void setTechpackname(String techpackname) {
      this.techpackname = techpackname;
    }

    public String getVersion() {
      return version;
    }

    public String getTechpackname() {
      return techpackname;
    }

  }

  /*
   * 
   */
  private void createVersionProperties(String outputname,
      ZipOutputStream out, Object obj) throws Exception {

    
    
    ZipEntry entry = new ZipEntry(outputname);
    out.putNextEntry(entry);

    StringWriter strw = new StringWriter();
    VelocityContext context = new VelocityContext();

    String version = "";

    String name = "";

    String author = "";

    String buildnumber = "";

    String licenseName = "";

    if (obj instanceof Versioning) {

      Versioning versioning = (Versioning) obj;

      Vector<VersionInfo> vec = new Vector<VersionInfo>();
      TechpackdependencyFactory tpdF = createTechpackdependencyFactory(versioning);
      Iterator<Techpackdependency> tpdFI = tpdF.get().iterator();
      while (tpdFI.hasNext()) {
        Techpackdependency t = tpdFI.next();
        VersionInfo vi = new VersionInfo();
        vi.setTechpackname(t.getTechpackname());
        vi.setVersion(t.getVersion());
        vec.add(vi);
      }

      licenseName = Utils.replaceNull(versioning.getLicensename());

      name = versioning.getTechpack_name();

      author = versioning.getLockedby();

      version = versioning.getTechpack_version();

      buildnumber = builNumberF.getText();

      context.put("required_tech_packs", vec);

    } else if (obj instanceof Datainterface) {

      Datainterface datainterface = (Datainterface) obj;

      Vector<VersionInfo> vec = new Vector<VersionInfo>();
      InterfacedependencyFactory itdF = createInterfacedependencyFactory(datainterface);

      Iterator<Interfacedependency> tpdFI = itdF.get().iterator();
      while (tpdFI.hasNext()) {
        Interfacedependency i = tpdFI.next();
        VersionInfo vi = new VersionInfo();
        vi.setTechpackname(i.getTechpackname());
        vi.setVersion(i.getTechpackversion());
        vec.add(vi);
      }

      licenseName = "";

      version = datainterface.getRstate();

      name = datainterface.getInterfacename();

      author = datainterface.getLockedby();

      buildnumber = builNumberF.getText();

      context.put("required_tech_packs", vec);

    }

    context.put("metadataversion", "3");
    context.put("techpackname", name);
    context.put("author", author);
    context.put("version", version);
    context.put("buildnumber", buildnumber);
    context.put("buildtag", "");
    context.put("licenseName", licenseName);

    
    final boolean isMergeOk = mergeVelocityTemplate(VERSION_TEMPLATE_NAME,
        Velocity.ENCODING_DEFAULT, context, strw);

    if (isMergeOk) {
      out.write(strw.toString().getBytes());
    }
  }
  
  protected void createInstallXML(String outputname, ZipOutputStream out, String templateName, Context context)
  throws Exception {

    ZipEntry entry = new ZipEntry(outputname);
    out.putNextEntry(entry);

    StringWriter strw = new StringWriter();

    boolean isMergeOk = mergeVelocityTemplate(templateName,
      Velocity.ENCODING_DEFAULT, context, strw);

    if (isMergeOk) {
      out.write(strw.toString().getBytes());
    }
  }

  private void createInstallXML(final String outputname, ZipOutputStream out, final String installXmlContent)
      throws IOException {
    ZipEntry entry = new ZipEntry(outputname);
    out.putNextEntry(entry);
    out.write(installXmlContent.getBytes());
  }

  @Action(enabledProperty = "saveEnabled")
  public Task<Boolean, Void> create() {

    final User user = dataModelController.getUser();

    if (crypt.isSelected() && (user.getPrivateKeyMod() == null || user.getPrivateKeyExp() == null)) {
      JOptionPane.showMessageDialog(frame, resourceMap.getString("CreateInstallationPackageView.keyfileMissing"),
          resourceMap.getString("CreateInstallationPackageView.keyfileMissing"), JOptionPane.ERROR_MESSAGE);
      return null;
    }

    final Task<Boolean, Void> saveTask = new SaveTask(application);
    BusyIndicator busyIndicator = new BusyIndicator();

    frame.setGlassPane(busyIndicator);
    saveTask.setInputBlocker(new BusyIndicatorInputBlocker(saveTask,
        busyIndicator));

    return saveTask;
  }

  @Action
  public void discard() {
    LOGGER.log(Level.INFO, "cancel");
    frame.dispose();
  }

  public boolean isSaveEnabled() {
    return saveEnabled;
  }

  public void setSaveEnabled(boolean saveEnabled) {
    boolean oldvalue = this.saveEnabled;
    this.saveEnabled = saveEnabled;
    firePropertyChange("saveEnabled", oldvalue, saveEnabled);
  }

  private void handleButtons() {
    boolean saveIsEnabled = false;

    if (this.builNumberF.getText().length() > 0) {
      saveIsEnabled = true;
    }

    this.setSaveEnabled(saveIsEnabled);
  }

  private class MyDocumentListener implements DocumentListener {

	  @Override
    public void changedUpdate(DocumentEvent e) {
      handleButtons();
    }

	  @Override
    public void insertUpdate(DocumentEvent e) {
      handleButtons();
    }

	  @Override
    public void removeUpdate(DocumentEvent e) {
      handleButtons();
    }
  }

  protected javax.swing.Action getAction(final String actionName) {
    return application.getContext().getActionMap(this).get(actionName);
  }

  // Protected creator methods:
  /**
   * Creates a new instance of CreateINTFInstallFile. Can be overridden for
   * testing.
   * 
   * @return ci New instance of CreateINTFInstallFile.
   */
  protected CreateINTFInstallFile createCreateINTFInstallFile() {
    final CreateINTFInstallFile ci = new CreateINTFInstallFile((Datainterface) id, oldVersion, "(("
        + builNumberF.getText() + "))", dataModelController);
    return ci;
  }

  /**
   * Creates a new instance of ZipOutputStream. Can be overridden for testing.
   * 
   * @param outputFilename
   *          The name of the zip file.
   * @return out A new ZipOutputStream.
   * @throws Exception
   */
  protected ZipOutputStream createZipOutputStream(final String outputFilename) throws FileNotFoundException {
    // Create the zip output stream for the file.
    final ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(outputFilename)));
    return out;
  }

  /**
   * Creates a new instance of CreateTPInstallFile. Can be overridden for
   * testing.
   * 
   * @param versioning
   *          Instance of Versioning.
   * @return ci A new CreateTPInstallFile instance.
   */
  protected CreateTPInstallFile createCreateTPInstallFile(final Versioning versioning) {
    final CreateTPInstallFile ci = new CreateTPInstallFile(versioning, oldVersion, "((" + builNumberF.getText() + "))",
        dataModelController);
    return ci;
  }

  /**
   * Creates a new instance of CreateSetInstallFile. Can be overridden for
   * testing.
   * 
   * @param name
   *          Set name.
   * @param version
   *          version string.
   * @param oldBuildNumber
   *          Old build number.
   * @param newBuildNumber
   *          New build number.
   * @param dmc
   *          DataModelController instance.
   * @return cs A new instance of CreateSetInstallFile.
   */
  protected CreateSetInstallFile createCreateSetInstallFile(final String name, final String version,
      final String oldBuildNumber, final String newBuildNumber, final DataModelController dmc) {
    final CreateSetInstallFile cs = new CreateSetInstallFile(name, version, oldBuildNumber, newBuildNumber, dmc);
    return cs;
  }
  
  /**
   * Merges the velocity template.
   * 
   * @param templateName
   *          Name of the template.
   * @param encoding
   *          The encoding string e.g. Velocity.ENCODING_DEFAULT, "ISO-8859-1"
   * @param context
   * @param writer
   * @return Returns true if the merge completed ok.
   * @throws Exception
   */
  protected boolean mergeVelocityTemplate(final String templateName, final String encoding, final Context context,
      final Writer writer) {
    boolean isMergeOk = true;
    try {
      isMergeOk = Velocity.mergeTemplate(templateName, encoding, context, writer);
    } catch (Exception ex) {
      LOGGER.log(Level.WARNING, "Error merging velocity template: " + ex.toString());
    }
    return isMergeOk;
  }

  /**
   * Creates a new InterfacedependencyFactory.
   * 
   * @param datainterface
   *          The Datainterface instance.
   * @return itdF The new InterfacedependencyFactory.
   * @throws SQLException
   * @throws RockException
   */
  protected InterfacedependencyFactory createInterfacedependencyFactory(final Datainterface datainterface)
      throws SQLException, RockException {
    final Interfacedependency itd = new Interfacedependency(dataModelController.getRockFactory());
    itd.setInterfacename(datainterface.getInterfacename());
    itd.setInterfaceversion(datainterface.getInterfaceversion());
    final InterfacedependencyFactory itdF = new InterfacedependencyFactory(dataModelController.getRockFactory(), itd);
    return itdF;
  }

  /**
   * Creates a new TechpackdependencyFactory.
   * 
   * @param versioning
   *          Versioning instance.
   * @return tpdF The new TechpackdependencyFactory.
   * @throws SQLException
   * @throws RockException
   */
  protected TechpackdependencyFactory createTechpackdependencyFactory(final Versioning versioning) throws SQLException,
      RockException {
    final Techpackdependency tpd = new Techpackdependency(dataModelController.getRockFactory());
    tpd.setVersionid(versioning.getVersionid());
    final TechpackdependencyFactory tpdF = new TechpackdependencyFactory(dataModelController.getRockFactory(), tpd);
    return tpdF;
  }
}
