/**


* 
 */
package com.ericsson.eniq.techpacksdk.view.universeParameters;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComboBox;
//import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;


import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.Task;

import com.ericsson.eniq.techpacksdk.BusyIndicator;
import com.ericsson.eniq.techpacksdk.BusyIndicatorInputBlocker;
import com.ericsson.eniq.techpacksdk.TechPackIDE;
import com.ericsson.eniq.techpacksdk.common.Constants;
import com.ericsson.eniq.techpacksdk.common.Utils;

/**
* @author eheijun


* 
 */
@SuppressWarnings("serial")
public class UniverseWizard extends JFrame {

  final private Logger logger = Logger.getLogger(UniverseWizard.class.getName());

  public final static String CREATENEWUNV = "CREATENEWUNV";

  public final static String UPDATEUNV = "UPDATEUNV";

  public final static String CREATEREP = "CREATEREP";

  public final static String CREATEDOC = "CREATEDOC";

  private final static String execFileName = "TPIDE_BOIntf.exe";

  private static final String installFolderName = "bointf";

  private final static String TASKLIST = "tasklist"; //EQEV-4220
  
  private final static String KILL = "taskkill /F /IM "; //EQEV-4220
  
  public static boolean BO_OperationFlag = false; //EQEV-4220
  
  private final String workDir;

  private final String runDir;

  private JPanel currentPanel;

  // private JTextField bofolderTextField;

  private JTextField boRepositoryTextField;

  private JTextField boUsernameTextField;

  private JPasswordField boPasswordTextField;

  private JComboBox boxiauthenticationComboBox;

  private JTextField boODBCConnectionF;

  private final Application application;
  
  private final JFrame pFrame;

  private final Properties props;

  private int currentPage;

  // private int maxpages;
  //
  // private boolean linkedUniverse;

  private final String botask;

  private List<String> domains;

  private List<String> universes;

  private String techpackIdent;

  private String basetechpackIdent;

  private String outputDestination;

  private JComboBox boDomainCB;

  private JComboBox boUniverseCB;

  String boTemplateDir = "";

  public UniverseWizard(final Application application, final JFrame parentFrame, final Properties props,
      final String botask, final String techpackIdent, final String basetechpackIdent, final String outputDestination) {
    //super(parentFrame, "Universe Wizard", true);    

	super("Universe Wizard");
	//parentFrame.setTitle("Univer Wizard");
	this.pFrame = parentFrame;
    this.application = application;
    this.props = props;
    this.botask = botask;
    this.techpackIdent = techpackIdent;
    this.basetechpackIdent = basetechpackIdent;
    this.outputDestination = outputDestination;

    // this.maxpages = 1;
    this.currentPage = 1;

    
    final String[] inputFileName = { execFileName, "Interop.busobj.dll", "Interop.Designer.dll", "readme.txt",
        "TP_Reference_SDIF.xslt", "TP_Reference.xslt", "TPIDE_BOIntf.pdb", "Verification_BH_Template_XI.ret",
        "Verification_CM_Template_XI.ret", "Verification_Count_Template_XI.ret", "Verification_Template_XI.ret",
        "Verification_RANKBH_Template_XI.ret" };

    workDir = System.getProperty("java.io.tmpdir");

    boTemplateDir = System.getProperty("user.home") + "\\Documents\\My Business Objects Documents\\templates";

    // Create the directory for the BO Interface files.
    runDir = createMainDir();

    // Create the BO template directory in case it does not exist yet.
    createBOTemplateDir();

    try {

      final ClassLoader cl = this.getClass().getClassLoader();

      // extract wizard and other needed stuff into file system
      for (int ind = 0; ind < inputFileName.length; ind++) {
        final boolean replaceFile = true;
        extractFile(cl, inputFileName[ind], replaceFile);
      }
    } catch (Exception e) {
      logger.log(Level.SEVERE, e.getMessage());
    }

    setCurrentPanel();

    Utils.center(this);
    setVisible(true);
  }

  /**
   * Creates the first view for prompting the connection settings for the BO
   * repository.
   * 
   * @return the panel
   */
  private JPanel createFirstView() {

    JPanel firstPanel = new JPanel();
    firstPanel.setLayout(new GridBagLayout());

    // final String bofolder = props.getProperty("businessobjects.folder",
    // "C:\\Program Files\\Business Objects\\");
    final String borepository = props.getProperty("businessobjects.repository", "");
    final String bousername = props.getProperty("businessobjects.username", "");
    final String bopassword = props.getProperty("businessobjects.password", "");
    int boauthind = 0;
    try {
      boauthind = Integer.parseInt(props.getProperty("businessobjects.xiauthentication", "0"));
    } catch (Exception e) {
      boauthind = 0;
    }
    final String boodbcconn = props.getProperty("businessobjects.odbc", "");

    final GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.insets = new Insets(2, 2, 2, 2);
    c.gridwidth = 1;

    c.gridy = 1;

    // c.gridx = 0;
    // c.fill = GridBagConstraints.NONE;
    // c.weightx = 0;
    // firstPanel.add(new JLabel("Bo folder"), c);
    //
    // bofolderTextField = new JTextField(bofolder, 26);
    // bofolderTextField.setPreferredSize(new Dimension(26, 28));
    //
    // c.gridx = GridBagConstraints.RELATIVE;
    // c.fill = GridBagConstraints.HORIZONTAL;
    // c.weightx = 1;
    // firstPanel.add(bofolderTextField, c);
    //
    // c.gridx = GridBagConstraints.RELATIVE;
    // c.fill = GridBagConstraints.NONE;
    // c.weightx = 0;
    // final JButton manage3 = new JButton("...");
    // manage3.setAction(this.getAction("selectbofolder"));
    // firstPanel.add(manage3, c);
    //
    // c.gridy = 2;

    c.gridx = 0;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    firstPanel.add(new JLabel("Repository"), c);

    boRepositoryTextField = new JTextField(borepository, 26);
    boRepositoryTextField.setPreferredSize(new Dimension(26, 28));
    boRepositoryTextField.setName("BO Repository"); //set name of textfield. this makes easy to find it for GUI tests

    c.gridx = GridBagConstraints.RELATIVE;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    firstPanel.add(boRepositoryTextField, c);

    c.gridy = 2;

    c.gridx = 0;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    firstPanel.add(new JLabel("Username"), c);

    boUsernameTextField = new JTextField(bousername, 26);
    boUsernameTextField.setPreferredSize(new Dimension(26, 28));
    boUsernameTextField.setName("BO Username");//set name for gui tests

    c.gridx = GridBagConstraints.RELATIVE;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    firstPanel.add(boUsernameTextField, c);

    c.gridy = 3;

    c.gridx = 0;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    firstPanel.add(new JLabel("Password"), c);

    boPasswordTextField = new JPasswordField(bopassword, 26);
    boPasswordTextField.setPreferredSize(new Dimension(26, 28));
    boPasswordTextField.setName("BO Password");

    c.gridx = GridBagConstraints.RELATIVE;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    firstPanel.add(boPasswordTextField, c);

    c.gridy = 4;

    c.gridx = 0;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    firstPanel.add(new JLabel("Authentication"), c);

    boxiauthenticationComboBox = new JComboBox(Constants.BOXIAUTHENTICATIONS);
    boxiauthenticationComboBox.setSelectedIndex(boauthind);
    c.gridx = GridBagConstraints.RELATIVE;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    firstPanel.add(boxiauthenticationComboBox, c);

    c.gridy = 5;

    c.gridx = 0;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    firstPanel.add(new JLabel("ODBC Connection"), c);

    boODBCConnectionF = new JTextField(boodbcconn, 26);
    boODBCConnectionF.setPreferredSize(new Dimension(26, 28));
    boODBCConnectionF.setName("ODBC ConnectionName");

    c.gridx = GridBagConstraints.RELATIVE;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    firstPanel.add(boODBCConnectionF, c);

    c.gridy = 6;

    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridwidth = 3;
    c.weighty = 0;
    c.weightx = 1;

    final JPanel buttonPanel1 = new JPanel();
    buttonPanel1.setLayout(new GridBagLayout());

    firstPanel.add(buttonPanel1, c);

    c.gridx = 0;
    c.gridy = 0;
    c.fill = GridBagConstraints.NONE;
    c.anchor = GridBagConstraints.SOUTHEAST;
    c.weightx = 1;
    c.weighty = 0;
    c.gridwidth = 1;

    JButton cancelButton1 = new JButton("Cancel");
    cancelButton1.setName("CancelButton1");
    cancelButton1.setAction(this.getAction("discard"));
    buttonPanel1.add(cancelButton1, c);

    c.gridx = 1;
    c.weightx = 0;

    JButton nextButton = new JButton("Next");
    nextButton.setName("NextButton1");
    if (botask.equals(CREATENEWUNV)) {
      nextButton.setAction(this.getAction("createUniverse"));
    } else if (botask.equals(UPDATEUNV)) {
      nextButton.setAction(this.getAction("updateUniverse"));
    } else if (botask.equals(CREATEREP)) {
      nextButton.setAction(this.getAction("createVerRep"));
    } else if (botask.equals(CREATEDOC)) {
      nextButton.setAction(this.getAction("createUnvRefDoc"));
    }
    buttonPanel1.add(nextButton, c);

    return firstPanel;

  }

  /**
   * Creates the second view for selecting the repository domain when creating
   * or updating a linked universe.
   * 
   * @return the panel
   */
  private JPanel createSecondView() {
    JPanel secondPanel = new JPanel();
    secondPanel.setLayout(new GridBagLayout());

    final GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.insets = new Insets(2, 2, 2, 2);
    c.gridwidth = 1;

    c.gridy = 1;

    c.gridx = 0;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    secondPanel.add(new JLabel("Repository Domains"), c);

    if (domains != null) {
      boDomainCB = new JComboBox(domains.toArray());
      c.gridx = GridBagConstraints.RELATIVE;
      c.fill = GridBagConstraints.HORIZONTAL;
      c.weightx = 1;
      secondPanel.add(boDomainCB, c);
    }

    c.gridy = 2;

    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridwidth = 3;
    c.weighty = 0;
    c.weightx = 1;

    final JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new GridBagLayout());

    secondPanel.add(buttonPanel, c);

    c.gridx = 0;
    c.gridy = 0;
    c.fill = GridBagConstraints.NONE;
    c.anchor = GridBagConstraints.SOUTHEAST;
    c.weightx = 1;
    c.weighty = 0;
    c.gridwidth = 1;

    JButton cancelButton = new JButton("Cancel");
    cancelButton.setName("CancelButton2");
    cancelButton.setAction(this.getAction("discard"));
    buttonPanel.add(cancelButton, c);

    c.gridx = 1;
    c.weightx = 0;

    JButton nextButton = new JButton("Next");
    nextButton.setName("NextButton2");
    nextButton.setAction(this.getAction("getUniverseList"));
    buttonPanel.add(nextButton, c);

    return secondPanel;

  }

  /**
   * Creates the third view for selecting the universe from the previously
   * selected repository domain when creating or updating a linked universe.
   * 
   * @return the panel
   */
  private JPanel createThirdView() {
    JPanel thirdPanel = new JPanel();
    thirdPanel.setLayout(new GridBagLayout());

    final GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.insets = new Insets(2, 2, 2, 2);
    c.gridwidth = 1;

    c.gridy = 1;

    c.gridx = 0;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    thirdPanel.add(new JLabel("Universes"), c);

    if (universes != null) {
      boUniverseCB = new JComboBox(universes.toArray());
      c.gridx = GridBagConstraints.RELATIVE;
      c.fill = GridBagConstraints.HORIZONTAL;
      c.weightx = 1;
      thirdPanel.add(boUniverseCB, c);
    }

    c.gridy = 2;

    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridwidth = 3;
    c.weighty = 0;
    c.weightx = 1;

    final JPanel buttonPanel3 = new JPanel();
    buttonPanel3.setLayout(new GridBagLayout());

    thirdPanel.add(buttonPanel3, c);

    c.gridx = 0;
    c.gridy = 0;
    c.fill = GridBagConstraints.NONE;
    c.anchor = GridBagConstraints.SOUTHEAST;
    c.weightx = 1;
    c.weighty = 0;
    c.gridwidth = 1;

    JButton cancelButton = new JButton("Cancel");
    cancelButton.setName("CancelButton3");
    cancelButton.setAction(this.getAction("discard"));
    buttonPanel3.add(cancelButton, c);

    c.gridx = 1;
    c.weightx = 0;

    JButton nextButton = new JButton("Next");
    nextButton.setName("NextButton3");
    nextButton.setAction(this.getAction("next"));
    buttonPanel3.add(nextButton, c);

    return thirdPanel;
  }

  /**
   * Returns the action object for the given action name.
   * 
   * @param actionName
   * @return
   */
  private javax.swing.Action getAction(final String actionName) {
    if (application != null) {
      return application.getContext().getActionMap(this).get(actionName);
    }
    return null;
  }

  /**
   * Saves the information from the first view to the connection.properties
   * file.
   */
  private void saveFirstView() {
    // props.setProperty("businessobjects.folder", bofolderTextField.getText());
    props.setProperty("businessobjects.repository", boRepositoryTextField.getText());
    props.setProperty("businessobjects.username", boUsernameTextField.getText());
    props.setProperty("businessobjects.xiauthentication", boxiauthenticationComboBox.getSelectedItem().toString());
    props.setProperty("businessobjects.odbc", this.boODBCConnectionF.getText());
    storeConnectionProperties();
  }

  /**
   * @return
   */
  @Action
  public Task<Void, Void> createUniverse() {
    saveFirstView();
    final Task<Void, Void> task = new CreateUniverseTask(application, this, boUsernameTextField.getText(),
        boPasswordTextField.getText(), boRepositoryTextField.getText(), boODBCConnectionF.getText(), techpackIdent,
        basetechpackIdent, outputDestination);
	BusyIndicator busyIndicator = new BusyIndicator();
	setGlassPane(busyIndicator);
	task.setInputBlocker(new BusyIndicatorInputBlocker(task, busyIndicator));
	return task;
  }

  /**
   * @return
   */
  @Action
  public Task<Void, Void> updateUniverse() {
    saveFirstView();    
    final Task<Void, Void> task = new UpdateUniverseTask(application, this, boUsernameTextField.getText(),
        boPasswordTextField.getText(), boRepositoryTextField.getText(), boODBCConnectionF.getText(), techpackIdent,
        basetechpackIdent, outputDestination);
    BusyIndicator busyIndicator = new BusyIndicator();
    setGlassPane(busyIndicator);    
    task.setInputBlocker(new BusyIndicatorInputBlocker(task, busyIndicator));
    return task;
  }

  /**
   * @return
   */
  @Action
  public Task<Void, Void> createVerRep() {
    saveFirstView();
    final Task<Void, Void> task = new CreateVerRepTask(application, this, boUsernameTextField.getText(),
        boPasswordTextField.getText(), boRepositoryTextField.getText(), boODBCConnectionF.getText(), techpackIdent,
        basetechpackIdent, outputDestination);
    BusyIndicator busyIndicator = new BusyIndicator();
    setGlassPane(busyIndicator);
    task.setInputBlocker(new BusyIndicatorInputBlocker(task, busyIndicator));
    return task;
  }

  /**
   * @return
   */
  @Action
  public Task<Void, Void> createUnvRefDoc() {
    saveFirstView();
    final Task<Void, Void> task = new CreateUnvRefDocTask(application, this, boUsernameTextField.getText(),
        boPasswordTextField.getText(), boRepositoryTextField.getText(), boODBCConnectionF.getText(), techpackIdent,
        basetechpackIdent, outputDestination);
    BusyIndicator busyIndicator = new BusyIndicator();
    setGlassPane(busyIndicator);
    task.setInputBlocker(new BusyIndicatorInputBlocker(task, busyIndicator));
    return task;
  }

  /**
  * The action for clicking the next button and moving to the next view.
   */
  @Action
  public void next() {
    currentPage++;
    setCurrentPanel();
    repaint();
  }

  //
  // //@Action
  // public void prev() {
  // currentPage--;
  // setCurrentPanel();
  // repaint();
  // }

  /**
   * Configures the panel contents based on the current page value.
   */
  private void setCurrentPanel() {

    if (currentPanel != null) {
      getContentPane().remove(currentPanel);
    }
    switch (currentPage) {
    case 2:
      currentPanel = createSecondView();
      break;
    case 3:
      currentPanel = createThirdView();
      break;
    default:
      currentPanel = createFirstView();
      break;
    }
    if (currentPanel != null) {
      getContentPane().add(currentPanel);
    }
    getContentPane().invalidate();
    // getContentPane().revalidate();
    getContentPane().repaint();
    pack();
  }

  @Action
  public void discard() {
    setVisible(false);
	UniverseGenerateView.busyIndicator.setVisible(false); //EQEV-2866
  }

  // @Action
  // public void selectbofolder() {
  // selectFolder(bofolderTextField.getText(), bofolderTextField);
  // }

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
      logger.fine("Storing values in " + target.getAbsolutePath());
      fos = new FileOutputStream(target);
      props.store(fos, "Stored by TPIDE " + (new SimpleDateFormat("dd.MM.yyyy HH:mm:ss")).format(new Date()));
    } catch (Exception e) {
      logger.log(Level.WARNING, "Failed to write connection properties file to " + target.getAbsolutePath(), e);
    } finally {
      try {
        fos.close();
      } catch (Exception e) {
      }
    }
  }

  // @Action(enabledProperty = "createUnvEnabled")
  // public Task<Void, Void> create() {
  // 
  // logger.log(Level.INFO, "Create Universe");
  // final Task<Void, Void> createTask = new CreateTask(application,
  // BaseIdentificationF.getText(),
  // TPIdentificationF.getText(),
  // BoUserNameF.getText(),
  // BoPasswordF.getText(),
  // ODBCConnectionF.getText(),
  // OutputFolderF.getText(),
  // BoRepositoryF.getText(),
  // System.getProperty("java.io.tmpdir"));
  // BusyIndicator busyIndicator = new BusyIndicator();
  // 
  // frame.setGlassPane(busyIndicator);
  // createTask.setInputBlocker(new BusyIndicatorInputBlocker(createTask,
  // busyIndicator));
  // 
  // return createTask;
  // }
    private class CreateUniverseTask extends Task<Void, Void> {

    String name;

    String password;

    String repository;

    String odbcconn;

    String tpIdent;

    String baseIdent;

    String output;
    
    private JFrame wizard; //EQEV-2866 changes

    /**
     * task creates universe
     * 
     * @param application
     * @param name
     * @param password
     * @param repository
     * @param odbcconn
     * @param tpIdent
     * @param baseIdent
     * @param output
     */
    public CreateUniverseTask(Application application, JFrame wizard, String name, String password, String repository,
        String odbcconn, String tpIdent, String baseIdent, String output) {
      super(application);
    
      //EQEV-4220 (START)
      setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
      WindowListener exitOnCloseOption = new WindowAdapter(){
	      @Override
	      public void windowClosing(WindowEvent e) {
	            try {
	            	//setDefaultCloseOperation(EXIT_ON_CLOSE);
	        		if(isProcessRunging(execFileName)){
						killProcess(execFileName, "Create Universe");
						discard();
	        		}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
	        	  }
	      };
	  addWindowListener(exitOnCloseOption); 
	  //EQEV-4220 (END)
    
	  this.wizard = wizard; 
      this.name = name;
      this.password = password;
      this.repository = repository;
      this.odbcconn = odbcconn;
      this.tpIdent = tpIdent;
      this.baseIdent = baseIdent;
      this.output = output;
         
    }

    
    @Override
    protected void succeeded(Void ignored) {
    // EQEV-4220 START
    if(BO_OperationFlag){
    	logger.log(Level.INFO, execFileName + " Operation Manually Terminated By User");
      }
      else 
      {
    	  logger.log(Level.INFO, execFileName + " universe creation succeeded");
      }
    // EQEV-4220 END
    	wizard.dispose();
      
      UniverseGenerateView.busyIndicator.setVisible(false); //EQEV-2866
    }

    @Override
    protected void failed(Throwable e) {
      logger.log(Level.SEVERE, execFileName + " universe creation failed ", e);
      String expMess = e.getMessage();
      String firstLineOfExp = "" ;
      if(expMess.contains("\n")){
                  firstLineOfExp = expMess.substring(0, expMess.indexOf('\n'));
      }else{
                  firstLineOfExp = expMess ;
      }
      JOptionPane.showMessageDialog(UniverseWizard.this, firstLineOfExp, application.getContext().getResourceMap()
          .getString("universewizard.error.caption"), JOptionPane.ERROR_MESSAGE);
    }

    @Override
    protected Void doInBackground() throws Exception {

      logger.log(Level.INFO, "Start " + execFileName);

      String[] params = new String[8];
      params[0] = "createUnv";
      params[1] = "\"" + name + "\"";
      params[2] = "\"" + password + "\"";
      params[3] = "\"" + repository + "\"";
      params[4] = "\"" + odbcconn + "\"";
      params[5] = "\"" + tpIdent + "\"";
      params[6] = "\"" + baseIdent + "\"";
      params[7] = "\"" + output + "\"";

      try {
        runBOInterface(runDir, params, logger);
      } catch (Exception e) {
        logger.log(Level.SEVERE, "Universe Creation Failed");
        throw (e);
      }

      return null;
    }
  }

  private class UpdateUniverseTask extends Task<Void, Void> {

    String name;

    String password;

    String repository;

    String odbcconn;

    String tpIdent;

    String baseIdent;

    String output;

    private JFrame wizard; //EQEV-2866 changes

    /**
     * task updates universe
     * 
     * @param application
     * @param name
     * @param password
     * @param repository
     * @param odbcconn
     * @param tpIdent
     * @param baseIdent
     * @param output
     */
    public UpdateUniverseTask(Application application, JFrame wizard, String name, String password, String repository,
        String odbcconn, String tpIdent, String baseIdent, String output) {
      super(application);
      //EQEV-4220 (START)
      setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
      WindowListener exitOnCloseOption = new WindowAdapter(){
	      @Override
	      public void windowClosing(WindowEvent e) {
	            try {
	            	//setDefaultCloseOperation(EXIT_ON_CLOSE);
	        		if(isProcessRunging(execFileName)){
						killProcess(execFileName, "Update Universe");
						discard();
	        		}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
	        	  }
	      };
	  addWindowListener(exitOnCloseOption); 
	  //EQEV-4220 (END)
    
      this.wizard = wizard;            
      this.name = name;
      this.password = password;
      this.repository = repository;
      this.odbcconn = odbcconn;
      this.tpIdent = tpIdent;
      this.baseIdent = baseIdent;
      this.output = output;
    }
   
    @Override
    protected void succeeded(Void ignored) {
     // EQEV-4220 START
      if(BO_OperationFlag){
      	logger.log(Level.INFO, execFileName + " Operation Manually Terminated By User");
        }
        else 
        {
        	logger.log(Level.INFO, execFileName + " universe update succeeded");
        	}
      // EQEV-4220 END
      wizard.dispose();
      
      UniverseGenerateView.busyIndicator.setVisible(false); //EQEV-2866
    }

    @Override
    protected void failed(Throwable e) {
      logger.log(Level.SEVERE, execFileName + " universe update failed ", e);
      String expMess = e.getMessage();
      String firstLineOfExp = "" ;
      if(expMess.contains("\n")){
                  firstLineOfExp = expMess.substring(0, expMess.indexOf('\n'));
      }else{
                  firstLineOfExp = expMess ;
      }
      JOptionPane.showMessageDialog(UniverseWizard.this, firstLineOfExp, application.getContext().getResourceMap()
          .getString("universewizard.error.caption"), JOptionPane.ERROR_MESSAGE);
    }

    @Override
    protected Void doInBackground() throws Exception {


                
      String[] params = new String[8];
      params[0] = "updateUnv";
      params[1] = "\"" + name + "\"";
      params[2] = "\"" + password + "\"";
      params[3] = "\"" + repository + "\"";
      params[4] = "\"" + odbcconn + "\"";
      params[5] = "\"" + tpIdent + "\"";
      params[6] = "\"" + baseIdent + "\"";
      params[7] = "\"" + output + "\"";

      try {
        runBOInterface(runDir, params, logger);
      } catch (Exception e) {
        logger.log(Level.SEVERE, "Universe Update Failed");
        throw (e);
      }

      return null;
    }
  }

  private class CreateVerRepTask extends Task<Void, Void> {

    String name;

    String password;

    String repository;

    String odbcconn;

    String tpIdent;

    String baseIdent;

    String output;

    private JFrame wizard; //EQEV-2866 changes

    /**
     * task creates verification reports
     * 
     * @param application
     * @param name
     * @param password
     * @param repository
     * @param odbcconn
     * @param tpIdent
     * @param baseIdent
     * @param output
     */
    public CreateVerRepTask(Application application, JFrame wizard, String name, String password, String repository,
        String odbcconn, String tpIdent, String baseIdent, String output) {
      super(application);
      //EQEV-4220 (START)
      setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
      WindowListener exitOnCloseOption = new WindowAdapter(){
	      @Override
	      public void windowClosing(WindowEvent e) {
	            try {
	            	//setDefaultCloseOperation(EXIT_ON_CLOSE);
	        		if(isProcessRunging(execFileName)){
						killProcess(execFileName, "Create Verification Reports");
						discard();
	        		}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
	        	  }
	      };
	  addWindowListener(exitOnCloseOption); 
	  //EQEV-4220 (END)
    
      this.wizard = wizard;
      this.name = name;
      this.password = password;
      this.repository = repository;
      this.odbcconn = odbcconn;
      this.tpIdent = tpIdent;
      this.baseIdent = baseIdent;
      this.output = output;
    }
    
    @Override
    protected void succeeded(Void ignored) {
      // EQEV-4220 START
      if(BO_OperationFlag){
      	logger.log(Level.INFO, execFileName + " Operation Manually Terminated By User");
        }
        else 
        {
        	logger.log(Level.INFO, execFileName + " verification report task succeeded");
        	}
      // EQEV-4220 END
      wizard.dispose();
      
      UniverseGenerateView.busyIndicator.setVisible(false); //EQEV-2866
    }

    @Override
    protected void failed(Throwable e) {
      logger.log(Level.SEVERE, execFileName + " verification report task failed ", e);
      String expMess = e.getMessage();
      String firstLineOfExp = "" ;
      if(expMess.contains("\n")){
                  firstLineOfExp = expMess.substring(0, expMess.indexOf('\n'));
      }else{
                  firstLineOfExp = expMess ;
      }
      JOptionPane.showMessageDialog(UniverseWizard.this, firstLineOfExp, application.getContext().getResourceMap()
          .getString("universewizard.error.caption"), JOptionPane.ERROR_MESSAGE);
    }

    @Override
    protected Void doInBackground() throws Exception {

      String[] params = new String[8];
      params[0] = "createRep";
      params[1] = "\"" + name + "\"";
      params[2] = "\"" + password + "\"";
      params[3] = "\"" + repository + "\"";
      params[4] = "\"" + odbcconn + "\"";
      params[5] = "\"" + tpIdent + "\"";
      params[6] = "\"" + baseIdent + "\"";
      params[7] = "\"" + output + "\"";

      try {
        runBOInterface(runDir, params, logger);
      } catch (Exception e) {
        logger.log(Level.SEVERE, "Create Verification Reports Failed");
        throw (e);
      }

      return null;
    }
  }

  private class CreateUnvRefDocTask extends Task<Void, Void> {

    String name;

    String password;

    String repository;

    String odbcconn;

    String tpIdent;

    String baseIdent;

    String output;

    private JFrame wizard; //EQEV-2866 changes

    /**
     * task creates universe docs
     * 
     * @param application
     * @param name
     * @param password
     * @param repository
     * @param odbcconn
     * @param tpIdent
     * @param baseIdent
     * @param output
     */
    public CreateUnvRefDocTask(Application application, JFrame wizard, String name, String password,
        String repository, String odbcconn, String tpIdent, String baseIdent, String output) {
      super(application);
      //EQEV-4220 (START)
      setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
      WindowListener exitOnCloseOption = new WindowAdapter(){
	      @Override
	      public void windowClosing(WindowEvent e) {
	            try {
	            	//setDefaultCloseOperation(EXIT_ON_CLOSE);
	        		if(isProcessRunging(execFileName)){
						killProcess(execFileName,"Create Universe Reference");
						discard();
	        		}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
	        	  }
	      };
	  addWindowListener(exitOnCloseOption); 
	  //EQEV-4220 (END)
    
      this.wizard = wizard;
      this.name = name;
      this.password = password;
      this.repository = repository;
      this.odbcconn = odbcconn;
      this.tpIdent = tpIdent;
      this.baseIdent = baseIdent;
      this.output = output;
    }
    
    @Override
    protected void succeeded(Void ignored) {
		// EQEV-4220 START
        if(BO_OperationFlag){
        	logger.log(Level.INFO, execFileName + " Operation Manually Terminated By User");
          }
          else 
          {
          	logger.log(Level.INFO, execFileName + " reference doc task succeeded");
          	}
        // EQEV-4220 END
    	wizard.dispose();
      
      UniverseGenerateView.busyIndicator.setVisible(false); //EQEV-2866
    }

    @Override
    protected void failed(Throwable e) {
      logger.log(Level.SEVERE, execFileName + " reference doc task failed ", e);
      String expMess = e.getMessage();
      String firstLineOfExp = "" ;
      if(expMess.contains("\n")){
                  firstLineOfExp = expMess.substring(0, expMess.indexOf('\n'));
      }else{
                  firstLineOfExp = expMess ;
      }
      JOptionPane.showMessageDialog(UniverseWizard.this, firstLineOfExp, application.getContext().getResourceMap()
          .getString("universewizard.error.caption"), JOptionPane.ERROR_MESSAGE);
    }

    @Override
    protected Void doInBackground() throws Exception {

      String[] params = new String[8];
      params[0] = "createDoc";
      params[1] = "\"" + name + "\"";
      params[2] = "\"" + password + "\"";
      params[3] = "\"" + repository + "\"";
      params[4] = "\"" + odbcconn + "\"";
      params[5] = "\"" + tpIdent + "\"";
      params[6] = "\"" + baseIdent + "\"";
      params[7] = "\"" + output + "\"";

      try {
        runBOInterface(runDir, params, logger);
      } catch (Exception e) {
        logger.log(Level.SEVERE, "Create Documentation Failed");
      }

      return null;
    }
  }


  //EQEV-4220 (START)
  //These two methods for killing the process in between. if somebody wants to terminate through close
  //button of given window and other one for checking the services whether is in running state or not.
  //on basis of isProcessRunging output, we will kill respective service
  private void killProcess(String serviceName, String operationName) throws Exception {
	  Runtime.getRuntime().exec(KILL + serviceName);
	  //logger.log(Level.INFO, operationName + " Operation Manually Terminated By User");
	BO_OperationFlag=true;  
  }


  private boolean isProcessRunging(String serviceName) throws Exception {
		 Process p = Runtime.getRuntime().exec(TASKLIST);
		 BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		 String line;
		 while ((line = reader.readLine()) != null) {
		  if (line.contains(serviceName)) {
			 return true;
		  }
		 }
		 return false;
	   	}
  //EQEV-4220 (END)

  
  
  private String createMainDir() {
    String outputDirName = workDir + File.separator + installFolderName;
    File newDir = new File(outputDirName);
    if (newDir.exists()) {
      logger.log(Level.INFO, outputDirName + " will be recreated");
      Utils.deleteDir(newDir);
      newDir.mkdir();
    } else {
      logger.log(Level.INFO, outputDirName + " will be created");
      newDir.mkdir();
    }
    return outputDirName;
  }

  private String createBOTemplateDir() {
    String outputDirName = boTemplateDir;
    File newDir = new File(outputDirName);
    if (newDir.exists()) {
      logger.log(Level.FINE, outputDirName + " already exists. No need to create.");
    } else {
      logger.log(Level.INFO, outputDirName + " will be created");
      newDir.mkdirs();
    }
    return outputDirName;
  }

  private Void extractFile(final ClassLoader classLoader, String jarFileName, boolean replaceFile) throws Exception {
    final String outputFileName = convertToOutputFile(jarFileName);
    logger.log(Level.INFO, jarFileName + " will be extracted into " + outputFileName);
    jarFileName = installFolderName + "/" + jarFileName;
    if (!replaceFile) {
      File outputFile = new File(outputFileName);
      if (outputFile.exists()) {
        return null;
      }
    }
    Utils.extractFileFromJar(classLoader, jarFileName, outputFileName);
    return null;
  }

  /**
   * Converts the plain filename to the full file pathname. The path depends on
   * the file extension, since the template files (.ret) will be stored to a
   * different directory.
   * 
   * @param fileName
   * @return
   * @throws Exception
   */
  private String convertToOutputFile(String fileName) throws Exception {
    // Add the absolut path to the filename. The path depends on the file name
    // itself. The BO templates go to a different directory than all the other
    // files.
    if (fileName.contains(".ret")) {
      return boTemplateDir + File.separator + fileName;
    } else {
      return runDir + File.separator + fileName;
    }
  }

  /**
   * This method is used for running the TPIDE BO Interface with the specified
   * parameters. Two parameters 'BO Version' and 'Authentication' are
   * automatically appended, so they should not be included.
   * 
   * @param runDir
   * @param params
   * @param logger
   * @return
   * @throws Exception
   */
  private static List<String> runBOInterface(String runDir, String[] params, Logger logger) throws Exception {
    String errorMessage = "";
    if (!runDir.endsWith(File.separator)) {
      runDir += File.separator;
    }
    List<String> result = new ArrayList<String>();
    final List<String> command = new ArrayList<String>();
    command.add(runDir + execFileName);
    for (int i = 0; i < params.length; i++) {
      command.add(params[i].trim());
    }
    command.add("\"XI\"");
    command.add("\"ENTERPRISE\"");
    logger.log(Level.INFO, hidePassword(command, params)); // IDE #679 Password is displayed as plain text in log as part of parameters for VB, 20070922, eeoidiv
    final ProcessBuilder builder = new ProcessBuilder(command);
    try {
      File startInDir = new File(runDir);
      builder.directory(startInDir);
      Process p = builder.start();
      BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
      BufferedReader errors = new BufferedReader(new InputStreamReader(p.getErrorStream()));
      String line;
      while ((line = input.readLine()) != null) {
        if (line.toLowerCase().contains("exception") || line.toLowerCase().contains("problem") || line.toLowerCase().contains("error")) 
        {
          if (errorMessage.length() > 0) 
          {
            errorMessage += "\n" + line;
          } 
          else
          {
            errorMessage += line;
          }
        }
        result.add(line);
        logger.log(Level.INFO, line);
      }
      while ((line = errors.readLine()) != null) {
        logger.log(Level.SEVERE, line);
      }
      // The process should be done now, but wait to be sure.
      p.waitFor();
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error running file " + execFileName, e);
      throw e;
    }
    if (errorMessage.length() > 0) {
      throw new Exception(errorMessage);
    }
    return result;
  }
  
  /**
   * Given a command list, hide the password
   * Assumes that password is the 3rd parameter for TPIDE_BOIntf.exe command.
   * @param command
   * @param params
   * @return
   */
  private static String hidePassword(List<String> command, String[] params) {
    StringBuffer result = new StringBuffer();
    try {
      final String cmd = command.toString();

      String password = params[2]; // Password is the 3rd parameter
      // remove double quotes " from around paramater
      if(password.startsWith("\"")) {password = password.substring(1);}
      if(password.endsWith("\"")) {password = password.substring(0, password.length()-1);}
      StringBuffer passReplace = new StringBuffer();
      for (int i = 0; i < password.length(); i++) {
        passReplace.append("*");
      }
      result.append(cmd.substring(0, cmd.indexOf(password))); 
      result.append(passReplace); 
      result.append(cmd.substring(cmd.indexOf(password)+password.length()));
    } catch (Exception e) {
      return "";
    }
    return result.toString();
  }

}


