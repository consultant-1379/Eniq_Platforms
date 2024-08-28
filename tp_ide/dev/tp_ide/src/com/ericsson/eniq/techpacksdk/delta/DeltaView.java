//package com.ericsson.eniq.techpacksdk.delta;
//
//import java.awt.Color;
//import java.awt.Component;
//import java.awt.Dimension;
//import java.awt.FlowLayout;
//import java.awt.GridBagConstraints;
//import java.awt.GridBagLayout;
//import java.awt.Insets;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.io.File;
//import java.io.FileInputStream;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
//import javax.swing.BorderFactory;
//import javax.swing.JButton;
//import javax.swing.JComboBox;
//import javax.swing.JFileChooser;
//import javax.swing.JFrame;
//import javax.swing.JOptionPane;
//import javax.swing.JPanel;
//import javax.swing.JScrollPane;
//import javax.swing.JTabbedPane;
//import javax.swing.JTextField;
//import javax.swing.JTree;
//import javax.swing.event.ChangeEvent;
//import javax.swing.event.ChangeListener;
//import javax.swing.event.TreeSelectionEvent;
//import javax.swing.event.TreeSelectionListener;
//import javax.swing.filechooser.FileFilter;
//import javax.swing.tree.DefaultMutableTreeNode;
//import javax.swing.tree.DefaultTreeCellRenderer;
//import javax.swing.tree.TreePath;
//
//import org.apache.xerces.parsers.DOMParser;
//import org.jdesktop.application.Action;
//import org.jdesktop.application.Application;
//import org.jdesktop.application.SingleFrameApplication;
//import org.jdesktop.application.Task;
//import org.w3c.dom.Document;
//import org.xml.sax.InputSource;
//
//import com.distocraft.dc5000.repository.dwhrep.Versioning;
//import com.ericsson.eniq.component.DataTreeNode;
//import com.ericsson.eniq.component.ExceptionHandler;
//import com.ericsson.eniq.component.GenericActionTree;
//import com.ericsson.eniq.techpacksdk.BusyIndicator;
//import com.ericsson.eniq.techpacksdk.BusyIndicatorInputBlocker;
//import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;
//import com.ericsson.eniq.techpacksdk.parserDebugger.ParserDebuggerView;
//
//@SuppressWarnings("serial")
//public class DeltaView extends JPanel {
//
//  private GenericActionTree tpTree;
//
//  private GenericActionTree tpTree2;
//
//  private JComboBox confB;
//
//  private static final Logger logger = Logger.getLogger(ParserDebuggerView.class.getName());
//
//  private SingleFrameApplication application;
//
//  private JTextField confF;
//
//  private JTextField defaultF;
//
//  private JFrame frame;
//
//  private JTree deltaTree;
//
//  //private DataModelController dataModelController;
//
//  private JScrollPane tpScrollPane;
//
//  private JScrollPane tpScrollPane2;
//
//  private JScrollPane deltaScrollPane;
//
//  private String selectedVersionid1;
//
//  private String selectedVersionid2;
//
//  private JTabbedPane tabbedPanel;
//
//  public DeltaView(SingleFrameApplication application, DataModelController dataModelController, JFrame frame) {
//    super(new GridBagLayout());
//
//    this.frame = frame;
//
//    //this.dataModelController = dataModelController;
//
//    this.application = application;
//
//    // ************** Main panel **********************
//
//    Dimension fieldDim = new Dimension(250, 25);
//
//    GridBagConstraints c = new GridBagConstraints();
//    c.anchor = GridBagConstraints.NORTHWEST;
//    c.fill = GridBagConstraints.HORIZONTAL;
//    c.insets = new Insets(2, 2, 2, 2);
//    c.weightx = 1;
//    c.weighty = 1;
//
//    JPanel upperPanel = new JPanel(new GridBagLayout());
//
//    JPanel leftPanel = new JPanel(new GridBagLayout());
//    leftPanel.setLayout(new GridBagLayout());
//    leftPanel.setBorder(BorderFactory.createTitledBorder("left"));
//    JScrollPane jspl = new JScrollPane(leftPanel);
//
//    JPanel rightPanel = new JPanel(new GridBagLayout());
//    rightPanel.setLayout(new GridBagLayout());
//    rightPanel.setBorder(BorderFactory.createTitledBorder("right"));
//
//    tabbedPanel = new JTabbedPane();
//    tabbedPanel.addChangeListener(new changeListener());
//    tabbedPanel.setTabPlacement(JTabbedPane.TOP);
//
//    // ***********************************************************
//
//    // techpack selector (leftPanel)
//
//    tpTree = new GenericActionTree(dataModelController.getTechPackTreeDataModel());
//
//    tpTree.addTreeSelectionListener(new SelectionListener1());
//    tpTree.setToolTipText("");
//    tpScrollPane = new JScrollPane(tpTree);
//
//    c.fill = GridBagConstraints.HORIZONTAL;
//    c.gridx = 0;
//    c.gridy = 0;
//    c.weightx = 1;
//    c.weighty = 1;
//
//    leftPanel.add(tpScrollPane, c);
//
//    // techpack selector (tabbedPanel)
//
//    tpTree2 = new GenericActionTree(dataModelController.getTechPackTreeDataModel());
//
//    tpTree2.addTreeSelectionListener(new SelectionListener2());
//    tpTree2.setToolTipText("");
//    tpScrollPane2 = new JScrollPane(tpTree2);
//
//    tabbedPanel.addTab("Techpack", tpScrollPane2);
//
//    JPanel conffilePane = new JPanel(new GridBagLayout());
//
//    // conf file (tabbedPanel)
//
//    confF = new JTextField("");
//    confF.setPreferredSize(fieldDim);
//    confF.addActionListener(new myActionListener());
//
//    c.fill = GridBagConstraints.HORIZONTAL;
//    c.gridx = 0;
//    c.gridy = 1;
//
//    conffilePane.add(confF, c);
//
//    JButton cpb2 = new JButton();
//    cpb2.setAction(getAction("selectConfFile"));
//    c.fill = GridBagConstraints.NONE;
//    c.gridx = 1;
//    c.gridy = 1;
//
//    conffilePane.add(cpb2, c);
//
//    String[] comboTypes = { "MIM", "MOM", "ASCII" };
//
//    confB = new JComboBox(comboTypes);
//    c.gridx = 0;
//    c.gridy = 2;
//
//    conffilePane.add(confB, c);
//
//    tabbedPanel.addTab("Configuration File", conffilePane);
//
//    c.fill = GridBagConstraints.HORIZONTAL;
//    c.gridx = 0;
//    c.gridy = 3;
//    c.weightx = 0;
//
//    leftPanel.add(tabbedPanel, c);
//
//    // default file (leftPanel)
//
//    JPanel defaultPanel = new JPanel();
//
//    defaultPanel.setBorder(BorderFactory.createTitledBorder("Default File"));
//
//    defaultF = new JTextField("");
//    defaultF.setPreferredSize(fieldDim);
//
//    c.fill = GridBagConstraints.NONE;
//    c.gridx = 0;
//    c.gridy = 1;
//
//    defaultPanel.add(defaultF, c);
//
//    JButton b2 = new JButton();
//    b2.setAction(getAction("selectDefaultFile"));
//    c.fill = GridBagConstraints.NONE;
//    c.gridx = 0;
//    c.gridy = 2;
//
//    defaultPanel.add(b2, c);
//
//    c.fill = GridBagConstraints.NONE;
//    c.gridx = 0;
//    c.gridy = 4;
//    c.weightx = 0;
//
//    leftPanel.add(defaultPanel, c);
//
//    // compare button (leftPanel)
//
//    JButton compareB = new JButton();
//    compareB.setActionCommand("compare");
//    compareB.setAction(getAction("compare"));
//    compareB.setToolTipText("compare");
//
//    c.fill = GridBagConstraints.NONE;
//    c.gridx = 0;
//    c.gridy = 5;
//    c.weightx = 0;
//
//    leftPanel.add(compareB, c);
//
//    // delta tree (rightPanel)
//
//    deltaTree = new JTree();
//    deltaTree.setModel(null);
//
//    deltaScrollPane = new JScrollPane(deltaTree);
//    c.anchor = GridBagConstraints.NORTHWEST;
//    c.fill = GridBagConstraints.BOTH;
//    c.gridx = 0;
//    c.gridy = 0;
//    c.weightx = 1;
//    c.weighty = 1;
//    rightPanel.add(deltaScrollPane, c);
//
//    JPanel infoPanel = new JPanel(new GridBagLayout());
//    c.anchor = GridBagConstraints.NORTHWEST;
//    c.fill = GridBagConstraints.BOTH;
//    c.gridx = 0;
//    c.gridy = 1;
//    c.weightx = 1;
//    c.weighty = 2;
//    JScrollPane infoPanelS = new JScrollPane(infoPanel);
//    rightPanel.add(infoPanelS, c);
//
//    // ***********************************************************
//
//    c.anchor = GridBagConstraints.NORTHWEST;
//    c.fill = GridBagConstraints.BOTH;
//    c.gridx = 0;
//    c.gridy = 0;
//    c.weightx = 1;
//    c.weighty = 1;
//
//    upperPanel.add(jspl, c);
//    c.gridx = 1;
//    c.gridy = 0;
//    c.weightx = 3;
//    c.weighty = 1;
//    upperPanel.add(rightPanel, c);
//
//    // ************** buttons **********************
//
//    JButton cancel;
//    cancel = new JButton("closeDialog");
//    cancel.setActionCommand("closeDialog");
//    cancel.setAction(getAction("closeDialog"));
//    cancel.setToolTipText("closeDialog");
//
//    JButton update;
//    update = new JButton();
//    update.setActionCommand("update");
//    update.setAction(getAction("update"));
//    update.setToolTipText("update");
//
//    // ************** button panel **********************
//
//    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
//
//    buttonPanel.add(update);
//    buttonPanel.add(cancel);
//
//    // ************** Main panel**********************
//
//    final GridBagConstraints gbc = new GridBagConstraints();
//    gbc.gridheight = 1;
//    gbc.gridwidth = 2;
//    gbc.fill = GridBagConstraints.BOTH;
//    gbc.anchor = GridBagConstraints.NORTHWEST;
//
//    gbc.weightx = 4;
//    gbc.weighty = 4;
//    gbc.gridx = 0;
//    gbc.gridy = 0;
//
//    this.add(upperPanel, gbc);
//
//    gbc.weightx = 0;
//    gbc.weighty = 0;
//    gbc.gridx = 0;
//    gbc.gridy = 1;
//
//    this.add(buttonPanel, gbc);
//
//    repaint();
//
//  }
//
//  @Action
//  public void selectDefaultFile() {
//
//    while (true) {
//      final JFileChooser jfc = new JFileChooser(new File(defaultF.getText()));
//      jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
//      jfc.setFileFilter(new FileFilter() {
//
//        public boolean accept(File f) {
//          return f.exists() && f.canWrite();
//        }
//
//        public String getDescription() {
//          return "Select Defaults file";
//        }
//      });
//
//      final int returnVal = jfc.showDialog(this, "Select configuration file");
//
//      if (returnVal == JFileChooser.APPROVE_OPTION) {
//        final File f = jfc.getSelectedFile();
//
//        if (!f.exists() || !f.canWrite()) {
//          JOptionPane.showMessageDialog(this.frame, "5", "6", JOptionPane.ERROR_MESSAGE);
//          continue;
//        }
//        defaultF.setText(f.getAbsolutePath());
//      }
//      break;
//    }
//  }
//
//  @Action
//  public void selectConfFile() {
//
//    while (true) {
//      final JFileChooser jfc = new JFileChooser(new File(confF.getText()));
//      jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
//      jfc.setFileFilter(new FileFilter() {
//
//        public boolean accept(File f) {
//          return f.exists() && f.canWrite();
//        }
//
//        public String getDescription() {
//          return "Select configuration file";
//        }
//      });
//
//      final int returnVal = jfc.showDialog(this, "Select configuration file");
//
//      if (returnVal == JFileChooser.APPROVE_OPTION) {
//        final File f = jfc.getSelectedFile();
//
//        if (!f.exists() || !f.canWrite()) {
//          JOptionPane.showMessageDialog(this.frame, "5", "6", JOptionPane.ERROR_MESSAGE);
//          continue;
//        }
//        confF.setText(f.getAbsolutePath());
//        setCompareStatus();
//      }
//      break;
//    }
//  }
//
//  class TechPackRenderer extends DefaultTreeCellRenderer {
//
//    private DataModelController dataModelController;
//
//    public TechPackRenderer(DataModelController dataModelController) {
//      this.dataModelController = dataModelController;
//    }
//
//    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
//        boolean leaf, int row, boolean hasFocus) {
//
//      super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
//
//      setForeground(Color.black);
//      Object aUserObject = null;
//
//      if (value != null) {
//        aUserObject = ((DefaultMutableTreeNode) value).getUserObject();
//        if (aUserObject instanceof DataTreeNode) {
//
//          DataTreeNode t = (DataTreeNode) aUserObject;
//          Versioning v = (Versioning) t.getRockDBObject();
//          String toolTip = " ";
//
//          if (t.locked != null && t.locked.length() > 0) {
//
//            if (t.locked.equalsIgnoreCase(dataModelController.getUserName())) {
//              toolTip += " LOCKED BY " + t.locked;
//
//            } else {
//              setForeground(Color.LIGHT_GRAY);
//              toolTip += " LOCKED BY " + t.locked;
//            }
//
//          } else {
//            toolTip += " UNLOCKED ";
//          }
//
//          if (v.getEniq_level() == null || v.getEniq_level().equalsIgnoreCase("1.0")) {
//            setForeground(Color.LIGHT_GRAY);
//            toolTip += " Version too old";
//          }
//
//          setToolTipText(toolTip);
//
//        }
//      }
//
//      return this;
//
//    }
//  }
//
//  public Document parse(String filename) throws Exception {
//
//    try {
//
//      DOMParser p = new DOMParser();
//      FileInputStream fis = new FileInputStream(new File(filename));
//      p.parse(new InputSource(fis));
//
//      return p.getDocument();
//
//    } catch (Exception e) {
//      ExceptionHandler.instance().handle(e);
//      e.printStackTrace();
//    }
//
//    return null;
//  }
//
//  private class SelectionListener1 implements TreeSelectionListener {
//
//    public void valueChanged(TreeSelectionEvent e) {
//      TreePath t = e.getPath();
//      Object pointed = t.getLastPathComponent();
//      if (pointed instanceof DefaultMutableTreeNode) {
//        DefaultMutableTreeNode node = (DefaultMutableTreeNode) pointed;
//        Object tmp = node.getUserObject();
//        if (tmp instanceof DataTreeNode) {
//          System.out.println(tmp.toString());
//          selectedVersionid1 = tmp.toString();
//          setCompareStatus();
//        } else {
//          selectedVersionid1 = null;
//          setCompareStatus();
//        }
//      } else {
//        setCompareStatus();
//      }
//    }
//  }
//
//  private class SelectionListener2 implements TreeSelectionListener {
//
//    public void valueChanged(TreeSelectionEvent e) {
//      TreePath t = e.getPath();
//      Object pointed = t.getLastPathComponent();
//      if (pointed instanceof DefaultMutableTreeNode) {
//        DefaultMutableTreeNode node = (DefaultMutableTreeNode) pointed;
//        Object tmp = node.getUserObject();
//        if (tmp instanceof DataTreeNode) {
//          selectedVersionid2 = tmp.toString();
//          setCompareStatus();
//        } else {
//          selectedVersionid2 = null;
//          setCompareStatus();
//        }
//      } else {
//        setCompareStatus();
//      }
//    }
//  }
//
//  private void setCompareStatus() {
//
//    if (selectedVersionid1 != null
//        && ((tabbedPanel.getSelectedIndex() == 0 && selectedVersionid2 != null) || (tabbedPanel.getSelectedIndex() == 1 && confF
//            .getText().length() > 0))) {
//      setCompare(true);
//    } else {
//      setCompare(false);
//    }
//
//  }
//
//  @Action
//  public void closeDialog() {
//    logger.log(Level.INFO, "closeDialog");
//    frame.dispose();
//  }
//
//  private class UpdateTask extends Task<Void, Void> {
//
//    public UpdateTask(Application app) {
//      super(app);
//    }
//
//    @Override
//    protected Void doInBackground() throws Exception {
//
//      return null;
//    }
//  }
//
//  private boolean update = false;
//
//  public boolean isUpdate() {
//    return update;
//  }
//
//  public void setisUpdate(boolean update) {
//    boolean oldvalue = this.update;
//    this.update = update;
//    firePropertyChange("update", oldvalue, update);
//  }
//
//  @Action(enabledProperty = "update")
//  public Task<Void, Void> update() throws Exception {
//
//    final Task<Void, Void> UpdateTask = new UpdateTask(application);
//    BusyIndicator busyIndicator = new BusyIndicator();
//
//    application.getMainFrame().setGlassPane(busyIndicator);
//    UpdateTask.setInputBlocker(new BusyIndicatorInputBlocker(UpdateTask, busyIndicator));
//
//    return UpdateTask;
//
//  }
//
//  private class CompareTask extends Task<Void, Void> {
//
//    public CompareTask(Application app) {
//      super(app);
//    }
//
//    @Override
//    protected Void doInBackground() throws Exception {
//
//      try {
//
//        xmlRead_MOM mom2 = new xmlRead_MOM();
//
//        delta d1 = new delta();
//        delta d2 = new delta();
//        String conffile2 = "";
//
//        xmlRead_Delta xr = new xmlRead_Delta();
//
//        if (selectedVersionid2 == null) {
//          // no techpack 1 selected
//          return null;
//        }
//
//        if (selectedVersionid2 == null) {
//
//          if (confF.getText().length() > 0) {
//
//            // we have a conffile file
//
//            if (confB.getSelectedIndex() == 0) {
//              // MIM
//
//            } else if (confB.getSelectedIndex() == 1) {
//
//              File momf = new File(confF.getText());
//
//              // MOM
//              if (momf.isFile()) {
//                // read defaults from disk
//                conffile2 = mom2.toXML(momf);
//              } else {
//                // usde default defaults
//                conffile2 = mom2.toXML(new File("/deltaDefaults.xml"));
//              }
//
//            } else if (confB.getSelectedIndex() == 2) {
//              // ascii
//
//            } else {
//              // undefined
//            }
//
//          } else {
//            // no techpack or configfile selected
//            return null;
//          }
//        }
//
//        xr.addXML1(d1.createXML(selectedVersionid1, true));
//
//        if (selectedVersionid2 != null) {
//          // TP
//          xr.addXML2(d2.createXML(selectedVersionid2, true));
//        } else {
//          // CONFILE
//          xr.addXML2(conffile2);
//        }
//
//        xr.doDelta();
//
//        deltaTree.setModel(new XMLTreeDataModel("", xr.getDeltaTree()));
//
//      } catch (Exception e) {
//        e.printStackTrace();
//      }
//
//      return null;
//    }
//  }
//
//  private boolean compare = false;
//
//  public boolean isCompare() {
//    return compare;
//  }
//
//  public void setCompare(boolean compare) {
//    boolean oldvalue = this.compare;
//    this.compare = compare;
//    firePropertyChange("compare", oldvalue, compare);
//  }
//
//  @Action(enabledProperty = "compare")
//  public Task<Void, Void> compare() throws Exception {
//
//    final Task<Void, Void> CompareTask = new CompareTask(application);
//    BusyIndicator busyIndicator = new BusyIndicator();
//
//    frame.setGlassPane(busyIndicator);
//    CompareTask.setInputBlocker(new BusyIndicatorInputBlocker(CompareTask, busyIndicator));
//
//    return CompareTask;
//
//  }
//
//  private javax.swing.Action getAction(final String actionName) {
//    return application.getContext().getActionMap(this).get(actionName);
//  }
//
//  public Application getApplication() {
//    return application;
//  }
//
//  private class changeListener implements ChangeListener {
//
//    public void stateChanged(ChangeEvent e) {
//      setCompareStatus();
//    }
//
//  }
//
//  private class myActionListener implements ActionListener {
//
//    public void actionPerformed(ActionEvent e) {
//      setCompareStatus();
//    }
//
//  }
//
//}
