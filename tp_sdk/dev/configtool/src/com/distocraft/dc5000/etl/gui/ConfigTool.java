package com.distocraft.dc5000.etl.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

import org.apache.velocity.app.Velocity;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.engine.main.EngineAdmin;
import com.distocraft.dc5000.etl.gui.activation.ActivationTab;
import com.distocraft.dc5000.etl.gui.dwhmanagement.DwhManagementTab;
import com.distocraft.dc5000.etl.gui.iface.InterfaceTab;
import com.distocraft.dc5000.etl.gui.meta.MetaTab;
import com.distocraft.dc5000.etl.gui.schedule.ScheduleTab;
import com.distocraft.dc5000.etl.gui.set.SetTab;
import com.distocraft.dc5000.etl.scheduler.SchedulerAdmin;

/**
 * @author lemminkainen Copyright Distocraft 2005 $id$
 */
public class ConfigTool implements WindowListener, SystemStructureNotificate, UI {

  public static final String BUILD_NUMBER = "@@BLD@@";

  public static final String[] TECHPACK_TYPES = { "Interface", "Techpack", "Custompack", "Maintenance", "Other" };

  public static final String[] STATUSES = { "enabled", "disabled" };

  public static final String[] SET_TYPES = { "Adapter", "Aggregator", "Alarm", "Install", "Loader", "Mediation",
      "Partition", "Service", "Support", "Topology" };

  public static final String[] ACTION_TYPES = { "Aggregation", "AggregationRuleCopy", "AlarmHandler",
      "AlarmInterfaceUpdate", "AlarmMarkup", "AutomaticAggregation", "AutomaticREAggregati", "CreateDir",
      "DirectoryDiskmanager", "Diskmanager", "Distribute", "DuplicateCheck", "DWHMigrate", "ExecutionProfiler", "GateKeeper",
      "JDBC Mediation", "Join", "JVMMonitor", "Load", "ManualReAggregation", "Mediation", "Parse", "PartitionAction",
      "Partitioned Loader", "PartitionedSQLExec", "RefreshDBLookup", "ReloadDBLookups", "ReloadProperties", "ReloadTransformation", "SanityCheck", "SessionLog Loader", "SetTypeTrigger",
      "SMTP Mediation", "SNMP Poller", "SQL Execute", "SQL Extract", "StorageTimeAction", "System Call",
      "System Monitor", "TableCheck", "TableCleaner", "TriggerScheduledSet", "Uncompress", "UnPartitioned Loader",
      "UpdateDimSession", "UpdateMonitoredTypes", "UpdateMonitoring", "UpdatePlan", "VersionUpdate" };

  public static final String[] SCHEDULE_TYPES = { "fileExists", "interval", "monthly", "once", "onStartup", "wait",
      "weekly", "weeklyinterval" };

  public static final String[] ADAPTER_TYPES = { "alarm", "ascii", "csexport", "ct", "eniqasn1", "mdc", "nascii", "nossdb",
    "omes", "omes2", "raml", "sasn", "separator", "spf", "stfiop", "xml" , "unittest","3gpp32435"};

  public static final String[] INTERFACE_TYPES = { "Measurement", "Reference" };

  public static boolean DEV_MODE = false;

  public static File fileDialogLastSelection = null;

  public static Icon dataConnection;

  public static Icon delete;

  public static Icon door;

  public static Icon data;

  public static Icon chain;

  public static Icon clock;

  public static Icon gearWheel;

  public static Icon computerIn;

  public static Icon computerOut;

  public static Icon folder;

  public static Icon plug;

  public static Icon unplug;

  public static Icon bulb;

  public static Icon box;

  public static Icon check;

  public static Icon newIcon;

  public static Icon noWay;

  public static Icon paste;

  public static Icon draw;

  public static Icon copy;

  public static Icon openDoc;

  public static Icon dataStore;

  public static Icon dataExtract;

  public static Icon eniqLogo;

  public static Icon refresh;

  public static Icon activation;

  public static Icon dwh;

  public static String serverHost;

  public static int serverPort = 1200;

  private JFrame frame;

  private JMenuBar mbar;

  private JMenu connection;

  private JMenuItem connect;

  private JMenu etlc;

  private JMenuItem etlcEngine;

  private JMenuItem etlcScheduler;

  private JMenuItem disconnect;

  private JMenuItem exit;

  private JMenu helpmenu;

  private JMenuItem about;

  private JTabbedPane tab;

  private MetaTab metaTab;

  private InterfaceTab interfaceTab;

  private SetTab setTab;

  private ScheduleTab scheduleTab;

  private ExportTab buildTab;

  private InstallTab installTab;

  private ActivationTab activationTab;

  private DwhManagementTab dwhManagementTab;

  private StatusBar status;

  private RockFactory rock = null;

  private RockFactory dwhrepRock = null;

  private Properties conProps;

  private List structureListen = new ArrayList();

  private ImageIcon loadIcon(String name) {

    ClassLoader cl = this.getClass().getClassLoader();
    java.net.URL imgURL = cl.getResource("icons/" + name);

    if (imgURL != null) { // Classloader didn't find icon from jar
      return new ImageIcon(imgURL);
    } else {
      ImageIcon ii = new ImageIcon("jar/icons/" + name);
      if (ii != null)
        return ii;
      else
        return new ImageIcon(new byte[0]);
    }
  }

  private void loadIcons() {
    eniqLogo = loadIcon("ericsson_logo.gif");
    dataConnection = loadIcon("DataConnection.gif");
    delete = loadIcon("Delete.gif");
    door = loadIcon("Door.gif");
    data = loadIcon("Data.gif");
    chain = loadIcon("chain.gif");
    clock = loadIcon("Clock.gif");
    gearWheel = loadIcon("Gearwheel.gif");
    computerIn = loadIcon("ComputerIn.gif");
    computerOut = loadIcon("ComputerOut.gif");
    folder = loadIcon("Folder.gif");
    plug = loadIcon("Plug.gif");
    unplug = loadIcon("UnPlug.gif");
    bulb = loadIcon("Bulb.gif");
    box = loadIcon("Box.gif");
    check = loadIcon("Check.gif");
    newIcon = loadIcon("New.gif");
    noWay = loadIcon("NoWay.gif");
    paste = loadIcon("Paste.gif");
    draw = loadIcon("Draw.gif");
    copy = loadIcon("Copy.gif");
    openDoc = loadIcon("OpenDoc.gif");
    dataStore = loadIcon("DataStore.gif");
    dataExtract = loadIcon("DataExtract.gif");
    refresh = loadIcon("Redo.gif");
    activation = loadIcon("GreenFlag.gif");
    dwh = loadIcon("DeleteData.gif");
  }

  private ConfigTool() {
    loadIcons();

    JFrame.setDefaultLookAndFeelDecorated(true);

    frame = new JFrame("Ericsson Network IQ ETLC ConfigTool");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.addWindowListener(this);

    mbar = new JMenuBar();

    connection = new JMenu("Connection");
    connect = new JMenuItem("Connect..", dataConnection);
    connect.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent ae) {
        ConnectWorker cw = new ConnectWorker();
        cw.start();

        try {
          Thread.sleep(5000);
        } catch (Exception e) {

        }
        if (testEngine()) {
          etlcEngine.setEnabled(true);
          etlc.setEnabled(true);
        } else
          etlcEngine.setEnabled(false);

        if (testScheduler()) {
          etlcScheduler.setEnabled(true);
          etlc.setEnabled(true);
        } else
          etlcScheduler.setEnabled(false);

      }
    });
    connection.add(connect);
    disconnect = new JMenuItem("Disconnect", delete);
    disconnect.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent ae) {
        cleanUp();

        metaTab.disconnected();
        interfaceTab.disconnected();
        setTab.disconnected();
        scheduleTab.disconnected();
        buildTab.disconnected();
        installTab.disconnected();
        activationTab.disconnected();
        dwhManagementTab.disconnected();
        status.setHostName(null);

        etlcEngine.setEnabled(false);
        etlcScheduler.setEnabled(false);
        etlc.setEnabled(false);

        disconnect.setEnabled(false);
        connect.setEnabled(true);
      }
    });
    disconnect.setEnabled(false);
    connection.add(disconnect);

    connection.addSeparator();

    exit = new JMenuItem("Exit", door);
    exit.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent ae) {
        frame.setVisible(false);
        cleanUp();
        System.exit(0);
      }
    });
    connection.add(exit);
    mbar.add(connection);

    etlc = new JMenu("ETLC");
    etlcEngine = new JMenuItem("Reload Engine", refresh);
    etlcScheduler = new JMenuItem("Reload Scheduler", refresh);

    etlcEngine.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent ae) {

        if (reloadConfig()) {

        } else {

          JOptionPane.showMessageDialog(frame, "Could not Reload Engine.", "Reload Error", JOptionPane.ERROR_MESSAGE);
        }

      }
    });

    etlcScheduler.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent ae) {

        if (activateScheduler()) {

        } else {
          JOptionPane
              .showMessageDialog(frame, "Could not Reload Scheduler.", "Reload Error", JOptionPane.ERROR_MESSAGE);

        }

      }
    });

    etlcEngine.setEnabled(false);
    etlcScheduler.setEnabled(false);
    etlc.setEnabled(false);

    etlc.add(etlcEngine);
    etlc.add(etlcScheduler);

    mbar.add(etlc);

    mbar.add(Box.createHorizontalGlue());

    helpmenu = new JMenu("Help");
    about = new JMenuItem("About ConfigTool..");
    about.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent ae) {
        AboutDialog ad = new AboutDialog(frame);
      }
    });
    helpmenu.add(about);

    mbar.add(helpmenu);

    frame.setJMenuBar(mbar);

    tab = new JTabbedPane();
    metaTab = new MetaTab(frame, rock, dwhrepRock, this, this);
    tab.addTab("Metadata", data, metaTab);
    interfaceTab = new InterfaceTab(frame, this, this);
    tab.addTab("Interface", plug, interfaceTab);
    setTab = new SetTab(frame, this, this);
    tab.addTab("Set", chain, setTab);
    scheduleTab = new ScheduleTab(frame, this, this);
    tab.addTab("Scheduling", clock, scheduleTab);
    buildTab = new ExportTab(frame, this, this);
    tab.addTab("Export", computerOut, buildTab);
    installTab = new InstallTab(frame, this, this);
    tab.addTab("Import", computerIn, installTab);
    activationTab = new ActivationTab(frame, rock, this, this);
    tab.addTab("Activation", activation, activationTab);
    dwhManagementTab = new DwhManagementTab(frame, rock, this, this);
    tab.addTab("DWH", dwh, dwhManagementTab);

    frame.getContentPane().add(tab, BorderLayout.CENTER);

    status = new StatusBar();

    frame.getContentPane().add(status, BorderLayout.SOUTH);

    frame.pack();
    frame.setSize(new Dimension(800, 600));
    frame.setVisible(true);

    conProps = new Properties();
    try {
      File f = new File("connection.properties");
      if (f.exists() && f.isFile() && f.canRead()) {
        FileInputStream fis = new FileInputStream(f);
        conProps.load(fis);

        if ("true".equalsIgnoreCase(conProps.getProperty("dev", "false"))) {
          DEV_MODE = true;
        }
      } else {
        System.out.println("connection.properties does not exists");
      }
    } catch (Exception e) {
      System.out.println("Unable to read connection.properties");
      e.printStackTrace();
    }

  }

  public class ConnectWorker extends SwingWorker {

    private LoginDialog ld;

    private String connID;

    public ConnectWorker() {

      ld = new LoginDialog(frame, conProps);

      if (ld.connect()) {
        connID = ld.getConnectionID();
        startOperation("Connecting " + connID + "...");
      }

    }

    public Object construct() {

      if (ld.connect()) {

        try {

          if (System.getProperty("configtool.templatedir", null) != null) {
            Velocity.setProperty("file.resource.loader.path", System.getProperty("configtool.templatedir"));
          } else {
            Velocity.setProperty("resource.loader", "class");
            Velocity.setProperty("class.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
          }

          Velocity.init();

          rock = ld.initializeRock();

          if (rock == null)
            return null;

          dwhrepRock = ld.initializeDwhrepRock(rock);

          serverHost = ld.getServerHost();

        } catch (Exception e) {
          return e;
        }

      }

      return null;

    }

    public void finished() {

      if (ld.connect()) {

        Exception e = (Exception) get();

        if (e == null) {

          metaTab.connected(rock, dwhrepRock, connID);
          setTab.connected(rock, dwhrepRock, connID);
          scheduleTab.connected(rock, dwhrepRock, connID);
          interfaceTab.connected(rock, dwhrepRock, connID);
          buildTab.connected(rock, dwhrepRock, connID);
          installTab.connected(rock, dwhrepRock, connID);
          activationTab.connected(rock, dwhrepRock, connID);
          dwhManagementTab.connected(rock, dwhrepRock, connID);

          status.setHostName(connID);

          connect.setEnabled(false);
          disconnect.setEnabled(true);

        } else {

          ErrorDialog ed = new ErrorDialog(frame, "Connection error", "Unable to connect", e);

          metaTab.disconnected();
          setTab.disconnected();
          scheduleTab.disconnected();
          interfaceTab.disconnected();
          buildTab.disconnected();
          installTab.disconnected();
          activationTab.disconnected();
          dwhManagementTab.disconnected();
          status.setHostName(null);

          connect.setEnabled(true);
          disconnect.setEnabled(false);
        }

        frame.invalidate();
        frame.repaint();
        endOperation();

      }

    }

  };

  public void cleanUp() {

    // Just to make sure that connections are closed

    if (dwhrepRock != null) {
      try {
        dwhrepRock.getConnection().close();
      } catch (Exception e) {
      }
    }

    if (rock != null) {
      try {
        rock.getConnection().close();
      } catch (Exception e) {
      }
    }

  }

  public void windowActivated(WindowEvent e) {
  }

  public void windowClosed(WindowEvent e) {
  }

  public void windowClosing(WindowEvent e) {
    cleanUp();
    System.exit(0);
  }

  public void windowDeactivated(WindowEvent e) {
  }

  public void windowDeiconified(WindowEvent e) {
  }

  public void windowIconified(WindowEvent e) {
  }

  public void windowOpened(WindowEvent e) {
  }

  public static void main(String[] args) {

    javax.swing.SwingUtilities.invokeLater(new Runnable() {

      public void run() {
        ConfigTool ct = new ConfigTool();
      }
    });

  }

  static public boolean activateScheduler() {

    try {

      // activate Scheduler
      SchedulerAdmin admin = new SchedulerAdmin(ConfigTool.serverHost, ConfigTool.serverPort);
      // admin.setRMI(ConfigTool.serverHost, ConfigTool.serverPort);
      admin.activate_silent();

    } catch (Exception e) {
      return false;
    }
    return true;
  }

  static public boolean reloadConfig() {

    try {

      // activate Scheduler
      EngineAdmin admin = new EngineAdmin(ConfigTool.serverHost, ConfigTool.serverPort);
      admin.reloadPropertiesFromConfigTool();

    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  static public boolean testScheduler() {

    try {

      // activate Scheduler
      SchedulerAdmin admin = new SchedulerAdmin(ConfigTool.serverHost, ConfigTool.serverPort);
      // admin.setRMI(ConfigTool.serverHost, ConfigTool.serverPort);
      return admin.testConnection();

    } catch (Exception e) {
      return false;
    }
  }

  static public boolean testEngine() {

    try {

      // activate Scheduler
      EngineAdmin admin = new EngineAdmin(ConfigTool.serverHost, ConfigTool.serverPort);
      // admin.setRMI(ConfigTool.serverHost, ConfigTool.serverPort);
      return admin.testConnection();

    } catch (Exception e) {
      return false;
    }

  }

  public void metaDataChange() {
    Iterator i = structureListen.iterator();
    while (i.hasNext()) {
      SystemStructureListener ssl = (SystemStructureListener) i.next();
      ssl.metaDataChange();
    }
  }

  public void techPackChange() {
    Iterator i = structureListen.iterator();
    while (i.hasNext()) {
      SystemStructureListener ssl = (SystemStructureListener) i.next();
      ssl.techPackChange();
    }
  }

  public void setChange() {
    Iterator i = structureListen.iterator();
    while (i.hasNext()) {
      SystemStructureListener ssl = (SystemStructureListener) i.next();
      ssl.setChange();
    }
  }

  public void addSystemStructureListener(SystemStructureListener ssl) {
    structureListen.add(ssl);
  }

  public void removeSystemStructureListener(SystemStructureListener ssl) {
    try {
      structureListen.remove(ssl);
    } catch (Exception e) {
    }
  }

  public void startOperation(String msg) {
    status.startOperation(msg);
    javax.swing.SwingUtilities.invokeLater(new Runnable() {

      public void run() {
        tab.setEnabled(false);
        mbar.setEnabled(false);
        connection.setEnabled(false);
        helpmenu.setEnabled(false);
      }
    });
  }

  public void endOperation() {
    status.endOperation();
    javax.swing.SwingUtilities.invokeLater(new Runnable() {

      public void run() {
        tab.setEnabled(true);
        mbar.setEnabled(true);
        connection.setEnabled(true);
        helpmenu.setEnabled(true);
      }
    });
  }

}
