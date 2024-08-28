/**
 * 
 */
package com.ericsson.eniq.techpacksdk;

import java.awt.Component;
import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;

import org.apache.velocity.app.Velocity;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import com.ericsson.eniq.techpacksdk.common.Utils;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;
import com.ericsson.eniq.techpacksdk.datamodel.Engine;
import com.ericsson.eniq.techpacksdk.datamodel.Scheduler;
import com.ericsson.eniq.techpacksdk.monitor.EngineExecutionFrame;

/**
 * @author eheijun
 * 
 */
public class TechPackIDE extends SingleFrameApplication {

  private static final Logger logger = Logger.getLogger(TechPackIDE.class.getName());

  public static final String CONPROPS_FILE = System.getProperty("user.home", "") + File.separator
      + "connection.properties";

  protected static LogFrame logFrame = null;
  
  private EngineExecutionFrame eeframe = null;

  private StatusBar statusBar = null;

  private DataModelController dataModelController = null;

  private String serverName;

  private User currentUser = null;

  // Contains ServerName, User, Password and WorkingDir information
  private LoginPanel loginpanel;

  private final Properties conProps = new Properties();

  private static ResourceMap resourceMap = null;

  private Dimension frameSize = null;

  // private int mainWindowMinWidth = 1440;
  private int mainWindowMinWidth = 1280;

  private int mainWindowMinHeight = 700;
  
  private String etlAuth = null;
  
  //20110311, eanguan, LoginInUserAdminWindow IMP :: Size description of Login window
  // This size configuration is related to login window which comes after pressing
  // Login button in UserAdminPanel window.
  private int loginWindowMinWidth = 600;
  private int loginWindowMinHeight = 180;
  
  // 20110311, eanguan, LoginInUserAdminWindow IMP :: To make UserAdminPanel object a class variable
  // This is done as to get this instance in Loginpanel to get the selecetd username
  protected UserAdminPanel uap ;
  
  private ManageTechPackView manageTechPackTab ;
  

  // /**
  // * Launch action, installs VB stuff and someday also launch it....
  // *
  // * @return
  // */
  // @Action
  // public Task<Void, Void> launch() {
  // JFileChooser fc = new JFileChooser();
  // fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
  // int returnVal = fc.showOpenDialog(getMainFrame());
  //
  // if (returnVal == JFileChooser.APPROVE_OPTION) {
  // File file = fc.getSelectedFile();
  //    
  // final Task<Void, Void> launchTask = new LaunchTask(this);
  // ((LaunchTask) launchTask).setOutputDir(file);
  // BusyIndicator busyIndicator = new BusyIndicator();
  //
  // getMainFrame().setGlassPane(busyIndicator);
  // launchTask.setInputBlocker(new BusyIndicatorInputBlocker(launchTask,
  // busyIndicator));
  //
  // return launchTask;
  // }
  // return null;
  // }

  /**
   * Quit action, performs framework exit
   * 
   * @return
   */
  @Action
  public void quit() {
    this.exit();
  }
  
  /**
   * 20110311, eanguan, LoginInUserAdminWindow IMP :: Action description
   * for Login button present in UserAdminPanel
   * 
   * Note: Don't remove the resize settings coded here. --> Recommended
   * 
   * @return
   */
  @Action
  public void gotologinpanel() {  
	  getMainFrame().setVisible(false);
	  getMainFrame().setMinimumSize(new Dimension(loginWindowMinWidth, loginWindowMinHeight));
	  if (frameSize.width > loginWindowMinWidth && frameSize.height > loginWindowMinHeight) {
		  getMainFrame().setSize(loginWindowMinWidth,loginWindowMinHeight);
	  }else{
		  getMainFrame().setSize(new Dimension(loginWindowMinWidth, loginWindowMinHeight));
	  }
	  loginpanel = null ;
	  loginpanel = new LoginPanel(this, resourceMap, conProps);
	  getMainView().setComponent(loginpanel);
	  getMainView().setStatusBar(null);
	  getMainView().setMenuBar(null);
	  ImageIcon e = resourceMap.getImageIcon("ericsson.icon");
	  getMainFrame().setIconImage(e.getImage());
	  
	  Utils.center(getMainFrame());
	  getMainFrame().setVisible(true);	  
  }

  @Action
  public void aboutide() {
    new AboutIDEDialog(this, resourceMap, conProps);
  }
  
  @Action
  public void checkLocalSetup() {
    CheckLocalSetup.checkETLCFile();
    CheckLocalSetup.checkHOSTSFile();
  }

  /**
   * Helper function, returns action by name
   * 
   * @param actionName
   * @return
   */
  private javax.swing.Action getAction(final String actionName) {
    return getContext().getActionMap(this).get(actionName);
  }

  /**
   * Starts application with login screen
   */
  @Override
  protected void startup() {
	  
    try {

      final Logger rootLogger = Logger.getLogger("");
      rootLogger.setLevel(Level.WARNING);
      final Handler[] hand = rootLogger.getHandlers();
      for (int i = 0; i < hand.length; i++) {
        hand[i].setLevel(Level.WARNING);
      }
      // TPIDE log window
      final Handler ideHandler = new TPIDEHandler();
      rootLogger.addHandler(ideHandler);
      
      // Log to file
      final Handler fileHandler = new TPIDELogFileHandler();
      rootLogger.addHandler(fileHandler);
          
      final Logger ideRootLogger = Logger.getLogger("com.ericsson.eniq.techpacksdk");
      final Logger setWizardLogger = Logger.getLogger("com.ericsson.eniq.common.setWizards");
      
      ideRootLogger.setLevel(Level.FINEST);
      setWizardLogger.setLevel(Level.FINEST);
      
    } catch (Exception e) {
      System.err.println(e);
    }

    System.setSecurityManager(new IDESecurityManager());

    resourceMap = getContext().getResourceMap(getClass());

    addExitListener(new ExitListener());

    logFrame = new LogFrame(this, resourceMap);

    if (System.getProperty("TPIDE.startWithLog", "false").equals("true")) {
      logFrame.setVisible(true);
    }

    logger.info("Starting up");

    try {

      logger.fine("Generating system information");

      Properties props = System.getProperties();
      Enumeration<?> propertyNames = props.propertyNames();
      List<String> names = new ArrayList<String>();
      for (int i = 0; propertyNames.hasMoreElements(); i++) {
        String name = (String) propertyNames.nextElement();
        names.add(name);
      }
      Collections.sort(names);
      for (Iterator<String> iter = names.iterator(); iter.hasNext();) {
        String name = iter.next();
        String propValue = System.getProperty(name);
        logger.fine("System property '" + name + "' = '" + propValue + "'");
      }

    } catch (Exception e) {
      logger.log(Level.WARNING, "System property generation failed", e);
    }

    try {

      logger.fine("Intializing Velocity");

      if (System.getProperty("configtool.templatedir", null) != null) {
        Velocity.setProperty("file.resource.loader.path", System.getProperty("configtool.templatedir"));
      } else {
        Velocity.setProperty("resource.loader", "class");
        Velocity.setProperty("class.resource.loader.class",
            "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
      }

      Velocity.init();

      logger.fine("Velocity initialized");

    } catch (Exception e) {
      logger.log(Level.SEVERE, "Velocity initialization failed", e);
    }

    FileInputStream fis = null;
    try {
      logger.fine("Reading connection properties file");

      File f = new File(CONPROPS_FILE);
      if (f.exists() && f.isFile() && f.canRead()) {
        logger.fine(f.getAbsolutePath() + " exists");
        fis = new FileInputStream(f);
        conProps.load(fis);
      } else {
        logger.info(f.getAbsolutePath() + " does not exists or is not readable. ");
      }
    } catch (Exception e) {
      logger.log(Level.WARNING, "Unable to read connection.properties", e);
    } finally {
      try {
        fis.close();
      } catch (Exception e) {
      }
    }
    loginpanel = new LoginPanel(this, resourceMap, conProps);

    getMainView().setComponent(loginpanel);
    ImageIcon e = resourceMap.getImageIcon("ericsson.icon");
    getMainFrame().setIconImage(e.getImage());

    show(getMainView());

    frameSize = getMainFrame().getSize();

    // Hack. Frame refuces to calculate preferredSize probely. (too narrow)
    final Dimension toSize = getMainFrame().getPreferredSize();
    toSize.width = toSize.width + 40;

    getMainFrame().setSize(toSize);

    Utils.center(getMainFrame());

  }

  /**
   * Continues UI initialization after successfull Login
   */
  void loggedin(final DataModelController dmc, final User user, final String srvname) {

    dataModelController = dmc;
    currentUser = user;
    serverName = srvname;

    getMainFrame().setVisible(false);

    if (frameSize.width > mainWindowMinWidth && frameSize.height > mainWindowMinHeight) {
      getMainFrame().setSize(frameSize);
    } else {
      getMainFrame().setSize(new Dimension(mainWindowMinWidth, mainWindowMinHeight));
    }

    getMainFrame().setMinimumSize(new Dimension(mainWindowMinWidth, mainWindowMinHeight));
    
//    20110311, eanguan, LoginInUserAdminWindow IMP :: Commented it so as to resize the main frame
//	  to the size of Login Winodw when login button is pressed from UserAdminpanel
//	  Don't remove these comments. --> Recommended
//    getMainFrame().addComponentListener(new java.awt.event.ComponentAdapter() {
//    	public void componentResized(ComponentEvent e) {
//        JFrame tmp = (JFrame) e.getSource();
//        if (tmp.getWidth() < mainWindowMinWidth || tmp.getHeight() < mainWindowMinHeight) {
//          tmp.setSize(mainWindowMinWidth, mainWindowMinHeight);
//        }
//      }
//    });

    if (user.authorize(User.DO_BASETP) || user.authorize(User.DO_CUSTOMTP) || user.authorize(User.DO_PRODUCTTP)) {

      final JTabbedPane mainTabbedPanel = createMainComponent();
      getMainView().setComponent(mainTabbedPanel);

    } else if (user.authorize(User.DO_USERADMIN)) {
      try {
    	  // 20110311, eanguan, LoginInUserAdminWindow IMP :: Instead of local, made it a protected 
    	  // class variable
         uap = new UserAdminPanel(TechPackIDE.this, resourceMap, dataModelController
            .getRockFactory(), user);
        final LockAdminPanel lap = new LockAdminPanel(dataModelController, resourceMap, this);

        final JTabbedPane tab = new JTabbedPane();
        tab.setTabPlacement(JTabbedPane.BOTTOM);

        tab
            .addTab(resourceMap.getString("Main.useradmin.tabtitle"), resourceMap.getIcon("Main.useradmin.tabicon"),
                uap);
        tab
            .addTab(resourceMap.getString("Main.lockadmin.tabtitle"), resourceMap.getIcon("Main.lockadmin.tabicon"),
                lap);

        getMainView().setComponent(tab);
      } catch (Exception e) {
        logger.log(Level.WARNING, "UserAdminPanel creation failed", e);
        JOptionPane.showMessageDialog(getMainFrame(), "UserAdminPanel creation failed", "Error",
            JOptionPane.ERROR_MESSAGE);
      }
    } else {
      getMainView().setComponent(new JPanel());
    }

    final JMenuBar mainMenuBar = createMenuBar();
    getMainView().setMenuBar(mainMenuBar);

    statusBar = new StatusBar(serverName, currentUser, resourceMap);
    getMainView().setStatusBar(statusBar);

    Utils.center(getMainFrame());

    getMainFrame().setVisible(true);

  }

  @Override
  protected void shutdown() {
    super.shutdown();
    logger.info("Shutting down");
  }

  private JTabbedPane createMainComponent() {
    manageTechPackTab = new ManageTechPackView(this, dataModelController);
    final ManageDWHTab manageDWHTab = new ManageDWHTab(this, dataModelController);
    final ManageInterfaceTab manageInterfaceTab = new ManageInterfaceTab(this, dataModelController, dataModelController
        .getInterfaceTreeDataModel());

    final JTabbedPane tabbedPanel = new JTabbedPane();

    tabbedPanel.setTabPlacement(JTabbedPane.BOTTOM);

    tabbedPanel.addTab("Manage TechPack", null, manageTechPackTab);
    tabbedPanel.addTab("Manage DWH", null, manageDWHTab);
    tabbedPanel.addTab("Manage Interface", null, manageInterfaceTab);

    return tabbedPanel;
  }

  /**
   * Creates application main menu for actions
   * 
   * @return
   */
  private JMenuBar createMenuBar() {

    final JMenuBar menuBar = new JMenuBar();

    final JMenu fileMenu = new JMenu(resourceMap.getString("Menu.file.name"));

    final JMenu engMenu = new JMenu(resourceMap.getString("Menu.engine.name"));
    engMenu.setEnabled(dataModelController.getEngine() != null);
    final JMenuItem estat = new JMenuItem(getAction("showenginestatus"));
    engMenu.add(estat);
    final JMenuItem eque = new JMenuItem(getAction("showenginemonitor"));
    engMenu.add(eque);
    fileMenu.add(engMenu);

    final JMenu schMenu = new JMenu(resourceMap.getString("Menu.scheduler.name"));
    schMenu.setEnabled(dataModelController.getScheduler() != null);
    final JMenuItem sstat = new JMenuItem(getAction("showschedulerstatus"));
    schMenu.add(sstat);
    fileMenu.add(schMenu);

    final JMenuItem log = new JMenuItem(getAction("showlog"));
    fileMenu.add(log);
    
    final JMenuItem checkLocalSetup = new JMenuItem(getAction("checkLocalSetup"));
    fileMenu.add(checkLocalSetup);

    fileMenu.add(new JSeparator());

    // final JMenuItem launch = new JMenuItem(getAction("launch"));
    // fileMenu.add(launch);
    //
    // fileMenu.add(new JSeparator());

    final JMenuItem quit = new JMenuItem(getAction("quit"));
    fileMenu.add(quit);

    menuBar.add(fileMenu);

    final JMenu helpMenu = new JMenu(resourceMap.getString("Menu.help.name"));

    final JMenuItem about = new JMenuItem(getAction("aboutide"));
    helpMenu.add(about);

    menuBar.add(helpMenu);

    return menuBar;
  }
  
  @Action
  public void showenginemonitor() {
    if(eeframe == null) {
      eeframe = new EngineExecutionFrame(this,resourceMap,dataModelController.getEngine());
    }
    eeframe.showenginemonitor();
  }

  @Action
  public void showlog() {
    logFrame.setVisible(true);
  }

  @Action
  public void showenginestatus() {

    try {
      final Engine eng = dataModelController.getEngine();

      final StringBuffer sb = new StringBuffer();

      final List<String> list = eng.status();

      if (list != null) {
        final Iterator<String> i = list.iterator();
        while (i.hasNext()) {
          final String s = (String) i.next();
          sb.append(s).append("\n");
        }
      }

      JOptionPane.showMessageDialog(null, sb.toString(), resourceMap.getString("showenginestatus.title"), JOptionPane.INFORMATION_MESSAGE);

    } catch (Exception e) {
      JOptionPane.showMessageDialog(null, e.getMessage(), resourceMap.getString("showenginestatus.title"), JOptionPane.ERROR_MESSAGE);
    }

  }

  @Action
  public void showschedulerstatus() {
    
    try {
      final Scheduler sch = dataModelController.getScheduler();

      final StringBuffer sb = new StringBuffer();

      final List<String> list = sch.status();

      if (list != null) {
        final Iterator<String> i = list.iterator();
        while (i.hasNext()) {
          final String s = (String) i.next();
          sb.append(s).append("\n");
        }
      }

      JOptionPane.showMessageDialog(null, sb.toString(), resourceMap.getString("showschedulerstatus.title"), JOptionPane.INFORMATION_MESSAGE);

    } catch (Exception e) {
      JOptionPane.showMessageDialog(null, e.getMessage(), resourceMap.getString("showschedulerstatus.title"), JOptionPane.ERROR_MESSAGE);
    }
    
  }

  private class ExitListener implements Application.ExitListener {

    public boolean canExit(EventObject e) {
      Object source = (e != null) ? e.getSource() : null;
      Component owner = (source instanceof Component) ? (Component) source : null;
      String appname = resourceMap.getString("Application.name");
      boolean isExit = false ;
      List<String> list = null;
      String closeConfirm = "" ;
      //20110112,EANGUAN,HN54220:For adding the message in the prompt 
      // to display the number of Locked TPs to the User before exiting
      // 20110823 EANGUAN :: Changing it a bit to improve the functionality during MERGE so that user can unlock the Locked TPs at one go
	  closeConfirm = "" ;
	  if(dataModelController != null){
    	  list = dataModelController.getTechPackTreeDataModel().listOfLockedTechPacks();
    	  if(list != null && list.size() >0){
    		  final int listSize = list.size();
        	  if(listSize == 1){
        		  closeConfirm = " You have " +  listSize + " locked Tech Pack:  " +  list.toString();
        	  }else if(listSize <= 3){
        		  closeConfirm = " You have " +  listSize + " locked Tech Packs:\n" ;
        		  for(String tp : list){
        			  closeConfirm += tp + "\n";
        		  }
        	  }else{
        		  closeConfirm = " You have " +  listSize + " locked Tech Packs." ;
        	  }
        	  //closeConfirm += "\nIt is recommended to unlock Tech Packs before exiting." ;
        	  closeConfirm += "\nDo you want to unlock ? [ Recommended ]";
        	  closeConfirm += "\n";
        	  
        	  int output = JOptionPane.showConfirmDialog(null, closeConfirm);        	  
        	  if(output == 0){
        		  // Unlock TPs
        		  List<List<Object>> treeState = manageTechPackTab.getTreeState();
        		  dataModelController.getTechPackTreeDataModel().unlockAllLockedTPs();
        		  manageTechPackTab.loadTreeState(treeState);
        		  isExit = true ;
        	  }else if(output == 1){
        		  // User pressed NO. Don't unlock and Exit
        		  isExit = true ;
        	  }else{
        		// User pressed cancel. Don't Exit
        		  isExit = false ;
        	  }
    	  }else{
    		  closeConfirm = resourceMap.getString("Main.close.confirm");
              int result = JOptionPane.showOptionDialog(owner, closeConfirm, appname, JOptionPane.YES_NO_OPTION,
            		  JOptionPane.QUESTION_MESSAGE, null, null, null);
              isExit = (result == JOptionPane.YES_OPTION) ;
    	  }
      }else{
    	  closeConfirm = resourceMap.getString("Main.close.confirm");
          int result = JOptionPane.showOptionDialog(owner, closeConfirm, appname, JOptionPane.YES_NO_OPTION,
        		  JOptionPane.QUESTION_MESSAGE, null, null, null);
          isExit = (result == JOptionPane.YES_OPTION) ;
      }
	  return isExit ;
    }

    public void willExit(EventObject e) {
    	// eanguan 20110819 :: To close the connection to the database if any open
    	LoginTask.closeAllDBConn();
    }
  }
  
  public DataModelController getDataModelController(){
    return this.dataModelController;
  }

  /**
 * @param etlAuth the etlAuth to set
 */
public void setEtlAuth(String etlAuth) {
	this.etlAuth = etlAuth;
}

/**
 * Gets the database authentication string from the property "etlAuth".
 * @return the etlAuth
 * @throws Exception 
 */
public String getEtlAuth() throws Exception {
	if(etlAuth == null) {
		if(resourceMap==null) {
			this.resourceMap = getContext().getResourceMap(getClass());
		}
		//For SQL Anywhere we need to append the authentication string. 
		//This is got from ETLCServer.properties file.
		etlAuth = resourceMap.getString("etlAuth");
	}
	return etlAuth;
} //getEtlAuth

/**
   * @param args
   */
  public static void main(final String[] args) {
    Application.launch(TechPackIDE.class, args);
  }

  /**
   * 20110705 eanguan :: To get the IDE resource in TTC package, to get up and dowm image
   * @return
   */
  public static ResourceMap getResourceMap(){
	  return resourceMap ;
  }
  // /**
  // * Task extracts Reporting Wizard (and needed libraries) from current JAR
  // file
  // * into temp dir and starts it from there. Method does not work if IDE is
  // runned
  // * directly from Eclipse
  // *
  // * @author eheijun
  // *
  // */
  // private class LaunchTask extends Task<Void, Void> {
  //
  // final private String execFileName = "TPIDE_BOIntf.exe";
  //
  // final private String dirName = "bointf";
  //    
  // private File outputDir = new File(System.getProperty("java.io.tmpdir"));
  //
  // public LaunchTask(Application app) {
  // super(app);
  // }
  //
  // private Void createMainDir() throws Exception {
  // String outputDirName = outputDir + File.separator + dirName;
  // File newDir = new File(outputDirName);
  // if (!newDir.exists()) {
  // newDir.mkdir();
  // }
  // return null;
  // }
  //
  // // private Void createSubDir(String newDirName) throws Exception {
  // // String outputDirName = System.getProperty("java.io.tmpdir") + dirName;
  // // File newDir = new File(outputDirName);
  // // if (!newDir.exists()) {
  // // newDir.mkdir();
  // // }
  // // newDirName = outputDirName + File.separator + newDirName;
  // // newDir = new File(newDirName);
  // // if (!newDir.exists()) {
  // // newDir.mkdir();
  // // }
  // // return null;
  // // }
  //
  // private String convertToOutputFile(String fileName) throws Exception {
  // return outputDir + File.separator + dirName + File.separator + fileName;
  // }
  //
  // private Void extractFile(final ClassLoader classLoader, String jarFileName,
  // boolean replaceFile) throws Exception {
  // final String outputFileName = convertToOutputFile(jarFileName);
  // logger.log(Level.INFO, jarFileName + " will be extracted into " +
  // outputFileName);
  // jarFileName = dirName + "/" + jarFileName;
  // if (!replaceFile) {
  // File outputFile = new File(outputFileName);
  // if (outputFile.exists()) {
  // return null;
  // }
  // }
  // Utils.extractFileFromJar(classLoader, jarFileName, outputFileName);
  // return null;
  // }
  //
  // private Void runFile(String fileName) throws Exception {
  // final List<String> command = new ArrayList<String>();
  // // fileName = convertToOutputFile(fileName);
  // command.add(fileName);
  // final ProcessBuilder builder = new ProcessBuilder(command);
  // File mainDir = new File(outputDir + File.separator + dirName);
  // builder.directory(mainDir);
  // builder.start();
  // return null;
  // }
  //
  // @Override
  // protected Void doInBackground() throws Exception {
  //
  // logger.log(Level.INFO, "Start " + execFileName);
  //
  // final String[] inputFileName = { execFileName, "Interop.busobj.dll",
  // "Interop.Designer.dll", "readme.txt", "TPIDE_BOIntf.pdb",
  // "Verification_BH_Template.ret",
  // "Verification_CM_Template.ret", "Verification_Count_Template.ret",
  // "Verification_Template.ret" };
  //      
  // createMainDir();
  //        
  // final ClassLoader cl = this.getClass().getClassLoader();
  //
  // // extract wizard and other needed stuff into file system
  // for (int ind = 0; ind < inputFileName.length; ind++) {
  // final boolean replaceFile = true; //
  // !inputFileName[ind].equals("TPIde Reporting Wizard.exe.config");
  // extractFile(cl, inputFileName[ind], replaceFile);
  // }
  //
  // // run wizard from file system
  // //runFile(execFileName);
  // runFile("cmd.exe");
  //
  // return null;
  // }
  //
  // @Override
  // protected void succeeded(Void ignored) {
  // logger.log(Level.INFO, execFileName + " started");
  // }
  //
  // @Override
  // protected void failed(Throwable e) {
  // logger.log(Level.SEVERE, execFileName + " start failed ", e);
  // }
  //
  //    
  // public File getOutputDir() {
  // return outputDir;
  // }
  //
  //    
  // public void setOutputDir(File outputDir) {
  // this.outputDir = outputDir;
  // }
  //
  // }

}