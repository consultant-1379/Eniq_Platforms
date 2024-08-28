//package com.ericsson.eniq.techpacksdk.parserDebugger;
//
//import java.awt.Dimension;
//import java.awt.FlowLayout;
//import java.awt.GridBagConstraints;
//import java.awt.GridBagLayout;
//import java.awt.GridLayout;
//import java.awt.Insets;
////import java.awt.TextField;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//
//import java.awt.event.MouseEvent;
//import java.awt.event.MouseListener;
//import java.io.ByteArrayInputStream;
//import java.io.File;
//
//import java.util.HashMap;
//import java.util.Hashtable;
//import java.util.Iterator;
//
//import java.util.Map;
//import java.util.Properties;
//import java.util.Vector;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
//import javax.swing.JButton;
////import javax.swing.JCheckBox;
//
//import javax.swing.JFileChooser;
//import javax.swing.JFrame;
//import javax.swing.JLabel;
//
//import javax.swing.JOptionPane;
//import javax.swing.JPanel;
//
//import javax.swing.JScrollPane;
////import javax.swing.JSeparator;
//import javax.swing.JTable;
//import javax.swing.JTextArea;
//import javax.swing.JTextField;
//import javax.swing.ListSelectionModel;
////import javax.swing.SwingConstants;
////import javax.swing.event.DocumentEvent;
////import javax.swing.event.DocumentListener;
//import javax.swing.event.TableModelEvent;
//import javax.swing.event.TableModelListener;
////import javax.swing.event.TreeSelectionEvent;
////import javax.swing.event.TreeSelectionListener;
//import javax.swing.filechooser.FileFilter;
//import javax.swing.table.AbstractTableModel;
////import javax.swing.tree.DefaultMutableTreeNode;
////import javax.swing.tree.TreePath;
//
//import org.jdesktop.application.Action;
//import org.jdesktop.application.Application;
//import org.jdesktop.application.Task;
//
//import parserDebugger.ParsedRowData;
//import parserDebugger.ParserDebuggerComponent;
//
//import ssc.rockfactory.RockDBObject;
////import ssc.rockfactory.RockFactory;
//
//import com.distocraft.dc5000.common.StaticProperties;
//import com.distocraft.dc5000.etl.parser.ParserDebuggerCache;
//import com.distocraft.dc5000.etl.parser.StandaloneMain;
//import com.distocraft.dc5000.etl.parser.TransformerCache;
//import com.distocraft.dc5000.etl.parser.xmltransformer.Transformation;
//import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
//import com.distocraft.dc5000.etl.rock.Meta_collection_setsFactory;
//import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
//import com.distocraft.dc5000.etl.rock.Meta_transfer_actionsFactory;
//import com.distocraft.dc5000.repository.dwhrep.Datainterface;
//import com.distocraft.dc5000.repository.dwhrep.Dataitem;
//import com.distocraft.dc5000.repository.dwhrep.DataitemFactory;
//import com.ericsson.eniq.component.ExceptionHandler;
//import com.ericsson.eniq.techpacksdk.BusyIndicator;
//import com.ericsson.eniq.techpacksdk.BusyIndicatorInputBlocker;
//import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;
//
//public class ParserDebuggerView extends JPanel {
//
//  private static final Logger logger = Logger.getLogger(ParserDebuggerView.class.getName());
//
//  private Application application;
//
//  private JTextField inputF;
//
//  private JTextField outputF;
//
//  private JFrame frame;
//
//  private DataModelController dataModelController;
//
//  private RockDBObject source;
//
//  private StandaloneMain main = null;
//  
//  private int currRow = -1;
//
//  // private JCheckBox reload;
//
//  private ParserDebuggerComponent parserDebuggerComponent;
//
//  private ParserDebuggerCache pdc;
//
//  private Vector<String> meastypes = new Vector<String>();
//
//  private Vector<Integer> meastypeCounts = new Vector<Integer>();
//
//  private JTable jt;
//
//  private JLabel current;
//
//  // private JButton showdataButton;
//
//  private MyDataDataModel datam;
//
//  private JTextArea fileWindow;
//
//  private JFrame dataFrame;
//
////  private JButton refresh;
//  
//  public ParserDebuggerView(final Application application, final DataModelController dataModelController, final JFrame frame,
//		  final RockDBObject source, final Datainterface datainterface) {
//
//    super(new GridBagLayout());
//
//    this.frame = frame;
//
//    this.dataModelController = dataModelController;
//
//    this.application = application;
//
//    this.source = source;
//
//    // ************** Text panel **********************
//
//    final Dimension fieldDim = new Dimension(250, 25);
//    final Dimension tableDim = new Dimension(550, 230);
//    final Dimension smallTableDim = new Dimension(400, 150);
//
//    final JPanel upperPanel = new JPanel(new GridBagLayout());
//    final JPanel leftPanel = new JPanel(new GridBagLayout());
//    final JPanel rightPanel = new JPanel(new GridBagLayout());
//
//    final GridBagConstraints c = new GridBagConstraints();
//    c.anchor = GridBagConstraints.NORTHWEST;
//    c.fill = GridBagConstraints.NONE;
//    c.insets = new Insets(2, 2, 2, 2);
//    c.weightx = 1;
//    c.weighty = 1;
//    c.anchor = GridBagConstraints.NORTHEAST;
//    upperPanel.add(leftPanel, c);
//
//    c.gridx = 1;
//    c.gridy = 0;
//    c.weightx = 1;
//    c.weighty = 1;
//    c.anchor = GridBagConstraints.NORTHWEST;
//    upperPanel.add(rightPanel, c);
//
//    // name
//
//    c.weightx = 0;
//    c.weighty = 0;
//    c.anchor = GridBagConstraints.NORTHWEST;
//
//    final JPanel jp1 = new JPanel();
//
//    final JLabel iname = new JLabel("Input Directory   ");
//    c.fill = GridBagConstraints.NONE;
//    c.gridx = 0;
//    c.gridy = 0;
//    c.weightx = 0;
//    jp1.add(iname, c);
//
//    File fi = new File(dataModelController.getWorkingDir().getAbsolutePath() + "\\input");
//    if (!fi.exists()){
//      try {
//        fi.mkdir();
//      } catch (Exception e){
//        fi = new File(dataModelController.getWorkingDir().getAbsolutePath());
//      }
//    }
//    
//    inputF = new JTextField(fi.getAbsolutePath());
//    inputF.setPreferredSize(fieldDim);
//    c.fill = GridBagConstraints.NONE;
//    c.gridx = 1;
//    c.gridy = 0;
//    c.weightx = 1;
//
//    jp1.add(inputF, c);
//
//    final JButton b1 = new JButton("...");
//    b1.setAction(getAction("selectInputDir"));
//    c.fill = GridBagConstraints.NONE;
//    c.gridx = 2;
//    c.gridy = 0;
//    c.weightx = 1;
//
//    jp1.add(b1, c);
//
//    c.gridx = 0;
//    c.gridy = 1;
//    c.weightx = 1;
//
//    leftPanel.add(jp1, c);
//
//    // name
//
//    final JPanel jp2 = new JPanel();
//
//    final JLabel oname = new JLabel("Output Directory");
//    c.fill = GridBagConstraints.NONE;
//    c.gridx = 0;
//    c.gridy = 1;
//    c.weightx = 0;
//    jp2.add(oname, c);
//
//    File fo = new File(dataModelController.getWorkingDir().getAbsolutePath() + "\\output");
//    if (!fo.exists()){
//      try {
//        fo.mkdir();
//      } catch (Exception e){
//        fo = new File(dataModelController.getWorkingDir().getAbsolutePath());
//      }
//    }
//    outputF = new JTextField(fo.getAbsolutePath());
//    outputF.setPreferredSize(fieldDim);
//
//    c.fill = GridBagConstraints.NONE;
//    c.gridx = 1;
//    c.gridy = 1;
//    c.weightx = 1;
//    jp2.add(outputF, c);
//
//    final JButton b2 = new JButton("...");
//    b2.setAction(getAction("selectOutputDir"));
//    c.fill = GridBagConstraints.NONE;
//    c.gridx = 2;
//    c.gridy = 1;
//    c.weightx = 1;
//
//    jp2.add(b2, c);
//
//    c.gridx = 0;
//    c.gridy = 2;
//    c.weightx = 1;
//
//    leftPanel.add(jp2, c);
//
//    // meatype view
//
//    jt = new JTable();
//    jt.setModel(new myt(new HashMap()));
//    jt.addMouseListener(new MySelectionListener());
//    jt.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//    
//    final JScrollPane tableS = new JScrollPane(jt);
//    tableS.setPreferredSize(tableDim);
//
//    c.fill = GridBagConstraints.NONE;
//    c.gridx = 0;
//    c.gridy = 0;
//    c.weightx = 1;
//
//    rightPanel.add(tableS, c);
//
//    // meatype panel
//
//    final JPanel jpm = new JPanel();
//
//    // meatype text
//
//    current = new JLabel("No Meastype Selected");
//    current.setVisible(true);
//    c.fill = GridBagConstraints.NONE;
//    c.gridx = 1;
//    c.gridy = 1;
//    c.weightx = 1;
//    c.weighty = 1;
//    jpm.add(current, c);
//
//    // meatype button
//
//    /*
//     * showdataButton = new JButton("Show Data"); showdataButton.setAction(getAction("showData"));
//     * showdataButton.setEnabled(false); c.fill = GridBagConstraints.NONE; c.gridx = 0; c.gridy = 3; c.weightx = 1;
//     * 
//     * jpm.add(showdataButton, c);
//     */
//
//    c.fill = GridBagConstraints.NONE;
//    c.gridx = 0;
//    c.gridy = 1;
//    c.weightx = 1;
//    c.weighty = 1;
//    rightPanel.add(jpm, c);
//
//    // shows files in in directory
//
//    final JLabel files = new JLabel("Files in input directory");
//    c.fill = GridBagConstraints.NONE;
//    c.gridx = 0;
//    c.gridy = 3;
//    c.weightx = 0;
//    leftPanel.add(files, c);
//
//    fileWindow = new JTextArea();
//    fileWindow.setVisible(true);
//    fileWindow.setEditable(false);
//
//    final File f = new File(inputF.getText());
//    inputF.setText(f.getAbsolutePath());
//    final File[] ff = f.listFiles();
//    String text = "";
//    if (ff != null) {
//      for (int i = 0; i < ff.length; i++) {
//        text += " " + ff[i].getName() + "\n";
//      }
//    }
//    if (text.trim().length() == 0) {
//      text = "#NO FILES#";
//    }
//
//    fileWindow.setText(text);
//
//    final JScrollPane fileWindowS = new JScrollPane(fileWindow);
//    fileWindowS.setPreferredSize(smallTableDim);
//
//    c.fill = GridBagConstraints.NONE;
//    c.gridx = 0;
//    c.gridy = 4;
//    c.weightx = 0;
//    leftPanel.add(fileWindowS, c);
//
//    // Parse button
//
//    final JButton p = new JButton("Test");
//    p.setActionCommand("test");
//    p.setAction(getAction("test"));
//    p.setToolTipText("Parses the files in the input directory");
//    c.fill = GridBagConstraints.NONE;
//    c.gridx = 0;
//    c.gridy = 5;
//    c.weightx = 0;
//    leftPanel.add(p, c);
//
//    
//    // Refresh button
//    /*
//    refresh = new JButton("Refresh");
//    refresh.setActionCommand("refresh");
//    refresh.setAction(getAction("refresh"));
//    refresh.setToolTipText("Refreshes the caches");
//    refresh.setEnabled(false);
//    c.fill = GridBagConstraints.NONE;
//    c.gridx = 1;
//    c.gridy = 5;
//    c.weightx = 0;
//    leftPanel.add(refresh, c);
//    */
//    /*
//     * reload = new JCheckBox("reload"); reload.setSelected(true); c.fill = GridBagConstraints.NONE; c.gridx = 0;
//     * c.gridy = ; c.weightx = 0; leftPanel.add(reload, c);
//     */
//
//    // ************** Debug view component **********************
//    // Create the ParserDebuggerComponent
//    parserDebuggerComponent = new ParserDebuggerComponent();
//
//    // Create a panel for the buttons
//    final JPanel debugButtons = new JPanel(new GridLayout(0, 4));
//
//    final JButton next = new JButton(">");
//    next.setToolTipText("Move to the next transformation");
//    next.setActionCommand("next");
//    next.addActionListener(new pp());
//    // debugButtons.add(next);
//
//    final JButton last = new JButton(">|");
//    last.setToolTipText("Show All transformations of this row"); 
//    last.setActionCommand("end");
//    last.addActionListener(new pp());
//    // debugButtons.add(last);
//
//    final JButton prevRow = new JButton("<<");
//    prevRow.setToolTipText("Move to the previous row"); // IDE #677, Wrong info/help text for buttons "<<" and ">>" ,24/07/09,ejohabd.
//    prevRow.setActionCommand("prevrow");
//    prevRow.addActionListener(new pp());
//
//    final JButton nextRow = new JButton(">>");
//    nextRow.setToolTipText("Move to the next row");
//    nextRow.setActionCommand("nextrow");
//    nextRow.addActionListener(new pp());
//    
//    // debugButtons.add(end);
//
//    // ************** buttons **********************
//
//    JButton cancel;
//
//    cancel = new JButton("closeDialog");
//    cancel.setActionCommand("closeDialog");
//    cancel.setAction(getAction("closeDialog"));
//    cancel.setToolTipText("closeDialog");
//
//    // ************** button panel **********************
//
//    final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
//    buttonPanel.add(prevRow);
//    buttonPanel.add(next);
//    buttonPanel.add(last);
//    buttonPanel.add(nextRow);
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
//    final JScrollPane upperPanelS = new JScrollPane(upperPanel);
//    tableS.setPreferredSize(tableDim);
//
//    this.add(upperPanelS, gbc);
//
//    gbc.weightx = 6;
//    gbc.weighty = 6;
//    gbc.gridx = 0;
//    gbc.gridy = 1;
//
//    this.add(parserDebuggerComponent, gbc);
//
//    gbc.weightx = 0;
//    gbc.weighty = 0;
//    gbc.gridx = 0;
//    gbc.gridy = 2;
//
//    // Add the buttons panel to the bottom of the frame
//    this.add(debugButtons, gbc);
//
//    gbc.weightx = 0;
//    gbc.weighty = 0;
//    gbc.gridx = 0;
//    gbc.gridy = 3;
//
//    this.add(buttonPanel, gbc);
//
//    // Redraw the component
//    parserDebuggerComponent.redrawComponent();
//  }
//
//  private class pp implements ActionListener {
//    public void actionPerformed(final ActionEvent e) {
//
//      if (e.getActionCommand().equals("next")) {
//
//        parserDebuggerComponent.moveNext();
//        logger.finest("Next clicked " + parserDebuggerComponent.getRowSize());
//
//      } else if (e.getActionCommand().equals("last")) {
//
//        parserDebuggerComponent.moveLast();
//        logger.finest("Last clicked " + parserDebuggerComponent.getRowSize());
//
//      } else if (e.getActionCommand().equals("prevrow")) {
//        if (jt.getSelectedRow()>=0){  
//        currRow--;
//        if ( currRow <=0 ) {
//        	currRow = 0;
//        }
//        setDatarow(jt.getSelectedRow(),currRow);
//        }
//        logger.finest("Previous Transformation " + parserDebuggerComponent.getRowSize());
//      } else if (e.getActionCommand().equals("nextrow")) {
//        if (jt.getSelectedRow()>=0){  
//        currRow++;
//        if ( currRow > meastypeCounts.get(jt.getSelectedRow()).intValue()-1 ){
//        	currRow = meastypeCounts.get(jt.getSelectedRow()).intValue()-1;
//        }
//        setDatarow(jt.getSelectedRow(),currRow);
//        }
//        logger.finest("Next Transformation " + parserDebuggerComponent.getRowSize());
//      } else if (e.getActionCommand().equals("end")) {
//          parserDebuggerComponent.moveEnd();
//          logger.finest("End clicked " + parserDebuggerComponent.getRowSize());
//      }
//
//      frame.validate();
//
//      // Redraw the component
//      parserDebuggerComponent.redrawComponent();
//
//      if (jt.getSelectedRow()>=0){     
//    	  final int c = meastypeCounts.get(jt.getSelectedRow()).intValue();
//        current.setText(meastypes.get(jt.getSelectedRow()) + "(" + (currRow+1) + "/" + c + ")");
//      }
//    }
//  }
//
//  @Action
//  public Task test() {
//
//    final Task ParseTask = new ParseTask(application);
//    final BusyIndicator busyIndicator = new BusyIndicator();
//
//    frame.setGlassPane(busyIndicator);
//    ParseTask.setInputBlocker(new BusyIndicatorInputBlocker(ParseTask, busyIndicator));
//
//    return ParseTask;
//  }
//
//  private class ParseTask extends Task<Void, Void> {
//
//    public ParseTask(final Application app) {
//      super(app);
//    }
//
//    @Override
//    protected Void doInBackground() throws Exception {
//
//      try {
//
//    	  final Datainterface di = (Datainterface) source;
//
//    	  final Properties properties = new Properties();
//
//    	  final Meta_collection_sets mcols = new Meta_collection_sets(dataModelController.getEtlRockFactory());
//        mcols.setCollection_set_name(di.getInterfacename());
//        mcols.setVersion_number(di.getInterfaceversion());
//        final Meta_collection_setsFactory mcolsF = new Meta_collection_setsFactory(dataModelController.getEtlRockFactory(),
//            mcols);
//        final Meta_collection_sets tp = mcolsF.getElementAt(0);
//
//        final Meta_transfer_actions mta = new Meta_transfer_actions(dataModelController.getEtlRockFactory());
//        mta.setVersion_number(di.getInterfaceversion());
//        mta.setCollection_set_id(tp.getCollection_set_id());
//        mta.setAction_type("Parse"); // Values stored in db are [capital P] "Parse", not "parse", eeoidiv 20090812
//        final Meta_transfer_actionsFactory mtaF = new Meta_transfer_actionsFactory(dataModelController.getEtlRockFactory(),
//            mta);
//
//        String act_cont = null;
//        if(mtaF.size()>0) {
//        	final Meta_transfer_actions action = mtaF.getElementAt(0);
//          act_cont = action.getAction_contents();
//        }
//
//        if (act_cont != null && act_cont.length() > 0) {
//
//          try {
//        	  final ByteArrayInputStream bais = new ByteArrayInputStream(act_cont.getBytes());
//            properties.load(bais);
//            bais.close();
//          } catch (Exception e) {
//            logger.warning("Error loading action contents " + e);
//          }
//        }
//
//        // create temp directories to working dir
//        final File debugDir = new File(dataModelController.getWorkingDir().getAbsolutePath() + "/debugDir/");
//        final File inDir = new File(inputF.getText());
//        final File outDir = new File(outputF.getText());
//        final File processedDir = new File(dataModelController.getWorkingDir().getAbsolutePath() + "/debugDir/processed/");
//        final File failedDir = new File(dataModelController.getWorkingDir().getAbsolutePath() + "/debugDir/failed/");
//        final File doubleDir = new File(dataModelController.getWorkingDir().getAbsolutePath() + "/debugDir/double/");
//        final File archiveDir = new File(dataModelController.getWorkingDir().getAbsolutePath() + "/debugDir/archive/");
//        final File sessionDir = new File(StaticProperties.getProperty("SessionHandling.storageFile")).getParentFile();
//            
//        if (!debugDir.exists()) {
//          debugDir.mkdir();
//        }
//
//        if (!inDir.exists()) {
//          inDir.mkdir();
//        }
//
//        if (!outDir.exists()) {
//          outDir.mkdir();
//        }
//
//        if (!processedDir.exists()) {
//          processedDir.mkdir();
//        }
//
//        if (!failedDir.exists()) {
//          failedDir.mkdir();
//        }
//
//        if (!doubleDir.exists()) {
//          doubleDir.mkdir();
//        }
//
//        if (!archiveDir.exists()) {
//          archiveDir.mkdir();
//        }
//
//        if (!sessionDir.exists()) {
//          sessionDir.mkdirs();
//        }
//        
//        properties.setProperty("inDir", inDir.getAbsolutePath());
//        properties.setProperty("outDir", outDir.getAbsolutePath());
//        properties.setProperty("loaderDir", outDir.getAbsolutePath());
//        properties.setProperty("baseDir", debugDir.getAbsolutePath());
//        properties.setProperty("dublicateCheck", "false");
//        properties.setProperty("useDebugger", "true");
//        properties.setProperty("afterParseAction", "no");
//        properties.setProperty("interfaceTester", "true");
//        properties.setProperty("MDCParser.InputBufferSize", "1000");
//        if (properties.getProperty("MDCParser.VendorPatterns","").equalsIgnoreCase("")){
//          properties.setProperty("MDCParser.VendorPatterns","a"); 
//          properties.setProperty("MDCParser.VendorIDPattern.a","a");
//        }
//        
//        /*
//        properties.setProperty("brokenLinkCheck", "0");
//        properties.setProperty("archivePeriod","");
//        properties.setProperty("archiveStampFormat", "yyyyMMddHHmm");
//        properties.setProperty("ProcessedFiles.fileNameFormat", "");
//        properties.setProperty("Parser.tempFileHandlingCase", "3");
//         */
//        
//        final com.distocraft.dc5000.common.Properties props = new com.distocraft.dc5000.common.Properties(di
//            .getInterfacename(), (Hashtable) properties.clone(),properties);
//
//        // if (main == null){ || reload.isSelected()) {
//        main = new StandaloneMain(di.getInterfacename(), dataModelController.getEtlRockFactory(), dataModelController
//            .getDwhRockFactory(), dataModelController.getRockFactory());
//        // }
//
//        ParserDebuggerCache.initialize();
//        pdc = ParserDebuggerCache.getCache();
//
//        main.runInterface(di.getInterfacename(), props, dataModelController.getEtlRockFactory(), dataModelController
//            .getRockFactory());
//
//        final Iterator iter = pdc.getData().keySet().iterator();
//        while (iter.hasNext()) {
//        	final String key = (String) iter.next();
//          meastypes.add(key);
//          meastypeCounts.add(new Integer(((Vector) pdc.getData().get(key)).size()));
//        }
//
//        jt.setModel(new myt(pdc.getData()));
//
//      } catch (final Exception e) {
//        ExceptionHandler.instance().handle(e);
//        e.printStackTrace();
//      }
//
//      return null;
//    }
//  }
//
//  private class MySelectionListener implements TableModelListener, ActionListener, MouseListener {
//
//    public void tableChanged(final TableModelEvent e) {
//      // TODO Auto-generated method stub
//
//    }
//
//    public void actionPerformed(final ActionEvent e) {
//      // TODO Auto-generated method stub
//
//    }
//
//    public void mouseClicked(final MouseEvent e) {
//      // TODO Auto-generated method stub
//      // refresh();
//      getAction("refresh").actionPerformed(null);
//    }
//
//    public void mouseEntered(final MouseEvent e) {
//      // TODO Auto-generated method stub
//    }
//
//    public void mouseExited(final MouseEvent e) {
//      // TODO Auto-generated method stub
//
//    }
//
//    public void mousePressed(final MouseEvent e) {
//      // TODO Auto-generated method stub
//
//    }
//
//    public void mouseReleased(final MouseEvent e) {
//      // TODO Auto-generated method stub
//
//    }
//  }
//
//  @Action
//  public Task refresh() {
//
//    final Task SaveTask = new SaveTask(application);
//    final BusyIndicator busyIndicator = new BusyIndicator();
//
//    frame.setGlassPane(busyIndicator);
//    SaveTask.setInputBlocker(new BusyIndicatorInputBlocker(SaveTask, busyIndicator));
//
//    return SaveTask;
//  }
//
//  private class SaveTask extends Task<Void, Void> {
//
//    public SaveTask(final Application app) {
//      super(app);
//    }
//
//    @Override
//    protected Void doInBackground() throws Exception {
//      currRow = 0;
//      parserDebuggerComponent.moveReset();
//      setDatarow(jt.getSelectedRow(),currRow);
//      parserDebuggerComponent.moveNext();
//      parserDebuggerComponent.redrawComponent();
//      final int c = meastypeCounts.get(jt.getSelectedRow()).intValue();
//      current.setText(meastypes.get(jt.getSelectedRow()) + "(" + (currRow + 1) + "/" + c + ")");
//      //datam.fireTableDataChanged();
//      return null;
//    }
//  }
//
//  @Action
//  public Task showData() {
//
//    final Task showDataTask = new showDataTask(application);
//    final BusyIndicator busyIndicator = new BusyIndicator();
//
//    frame.setGlassPane(busyIndicator);
//    showDataTask.setInputBlocker(new BusyIndicatorInputBlocker(showDataTask, busyIndicator));
//
//    return showDataTask;
//  }
//
//  private class showDataTask extends Task<Void, Void> {
//
//    public showDataTask(final Application app) {
//      super(app);
//    }
//
//    @Override
//    protected Void doInBackground() throws Exception {
//
//    	final Vector<Vector> data = new Vector<Vector>();
//
//    	final String key = (String) meastypes.get(jt.getSelectedRow());
//    	final Vector tvec = (Vector) pdc.getData().get(key);
//    	final Iterator iter = tvec.iterator();
//      while (iter.hasNext()) {
//
//    	  final Vector v = (Vector) iter.next();
//
//    	  final Vector<String> d1 = new Vector<String>();
//
//        // v contais transformation lines so we are only intrested for the last...
//    	  final Map m = (Map) v.lastElement();
//
//    	  final Iterator iter3 = m.keySet().iterator();
//        while (iter3.hasNext()) {
//
//        	final String key3 = (String) iter3.next();
//          if (m.get(key3) instanceof String) {
//            d1.add((String) m.get(key3));
//          }
//        }
//
//        data.add(d1);
//      }
//
//      datam = new MyDataDataModel(pdc.getDataitems((String) meastypes.get(jt.getSelectedRow())), data);
//
//      if (dataFrame != null) {
//        dataFrame.dispose();
//      }
//
//      final JTable jt = new JTable();
//      jt.setModel(datam);
//      final JScrollPane tableS = new JScrollPane(jt);
//
//      dataFrame = new JFrame();
//      dataFrame.add(tableS);
//      dataFrame.setSize(800, 600);
//      dataFrame.setTitle(key);
//      dataFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//      dataFrame.setVisible(true);
//
//      return null;
//
//    }
//  }
//
//  private class myt extends AbstractTableModel {
//
//    private static final long serialVersionUID = 1L;
//
//    private Vector<String> m;
//
//    private Vector c;
//
//    public myt(final Map map) {
//
//      m = new Vector();
//      c = new Vector();
//
//      final Iterator iter = map.keySet().iterator();
//      while (iter.hasNext()) {
//    	  final String key = (String) iter.next();
//    	  final Vector v = (Vector) map.get(key);
//        m.add(key);
//        c.add(v.size());
//      }
//    }
//
//    public int getColumnCount() {
//      // TODO Auto-generated method stub
//      return 2;
//    }
//
//    public int getRowCount() {
//      // TODO Auto-generated method stub
//      if (m != null) {
//        return m.size();
//      } else {
//        return 0;
//      }
//
//    }
//
//    public Object getValueAt(final int rowIndex, final int columnIndex) {
//      // TODO Auto-generated method stub
//
//      if (columnIndex == 0) {
//        return m.get(rowIndex);
//      }
//      if (columnIndex == 1) {
//        return c.get(rowIndex);
//      }
//
//      return null;
//    }
//
//    public String getColumnName(final int col) {
//
//      if (col == 0) {
//        return "Meastype";
//      }
//      if (col == 1) {
//        return "Count";
//      }
//
//      return "";
//    }
//  }
//
//  private class MyDataDataModel extends AbstractTableModel {
//
//    private static final long serialVersionUID = 1L;
//
//    private final Vector<Vector> data;
//
//    private final Vector<String> cols;
//
//    public MyDataDataModel(final Vector<String> cols, final Vector<Vector> data) {
//
//      this.data = data;
//      this.cols = cols;
//
//    }
//
//    public int getColumnCount() {
//      // TODO Auto-generated method stub
//      if (data != null && data.get(0) != null) {
//        return data.get(0).size();
//      } else {
//        return 0;
//      }
//    }
//
//    public int getRowCount() {
//      // TODO Auto-generated method stub
//      if (data != null) {
//        return data.size();
//      } else {
//        return 0;
//      }
//    }
//
//    public Object getValueAt(final int rowIndex, final int columnIndex) {
//      // TODO Auto-generated method stub
//
//      if (data != null && data.get(rowIndex) != null) {
//        return data.get(rowIndex).get(columnIndex);
//      } else {
//        return null;
//      }
//    }
//
//    public String getColumnName(final int col) {
//
//      return cols.get(col);
//    }
//  }
//
//  private void setDatarow(final int rowNbr, final int iRow) {
//
//    try {
//
//      if (iRow == -1){
//        return;
//      }
//      
//      // one meastypes all datarows and all transformations
//      final String key = (String) meastypes.get(rowNbr);
//      final Vector tvec = (Vector) pdc.getData().get(key);
//
//      parserDebuggerComponent.dataReset();     
//
//      final Vector<String> ic = new Vector<String>();
//      final Dataitem di = new Dataitem(dataModelController.getRockFactory());
//      di.setDataformatid(meastypes.get(jt.getSelectedRow()));
//      final DataitemFactory diF = new DataitemFactory(dataModelController.getRockFactory(),di,"order by colnumber");
//      final Iterator i = diF.get().iterator();
//      while(i.hasNext()){
//    	  final Dataitem ditem = (Dataitem)i.next();
//        if (ditem.getColnumber() <= 100){
//          ic.add(ditem.getDataname());
//        }
//      }
//      
//      parserDebuggerComponent.setImportantColumns(ic);
//
//      // transformations (rules)
//
//      final String[] ss = {};
//      final String[] tt = {};
//      
//      int ii = 1;
//      final Iterator iter2 = TransformerCache.getCache().getTransformer(key).getTransformations().iterator();
//      while (iter2.hasNext()) {
//    	  final Transformation tra = (Transformation) iter2.next();
//
//        if (tra == null) {
//          continue;
//        }
//
//        String[] s = null;
//        String[] t = null;
//
//        if (tra.getSource() != null) {
//          s = tra.getSource().split(",");
//        } else {
//          s = new String[0];
//        }
//
//        if (tra.getTarget() != null) {
//          t = tra.getTarget().split(",");
//        } else {
//          t = new String[0];
//        }
//
//        parserDebuggerComponent.addTransformation(ii + ":" + tra.getName(), s, t);
//        ii++;
//      }
//
//      parserDebuggerComponent.addTransformation(" - ", ss, tt);
//      
//      // data rows
//      //for (int i = 0; i < mc; i++) {
//
//      final ParsedRowData row = this.parserDebuggerComponent.createRow();
//
//      final Vector v = (Vector) tvec.get(iRow);
//
//        int iii = 0;
//        // transformation rows
//        final Iterator iter3 = v.iterator();
//        while (iter3.hasNext()) {
//
//        	final HashMap<String, Object> tableData = new HashMap<String, Object>();
//
//        	final Map m = (Map) iter3.next();
//
//          // cols
//        	final  Iterator iter = m.keySet().iterator();
//          while (iter.hasNext()) {
//        	  final String key2 = (String) iter.next();
//            if (m.get(key2) instanceof String) {
//              tableData.put(key2, m.get(key2));
//            }
//          }
//          row.addTable(tableData);
//          iii++;
//        }
//      //}
//
//      parserDebuggerComponent.redrawComponent();
//
//      // showdataButton.setEnabled(true);
//
//    } catch (final Exception e) {
//      ExceptionHandler.instance().handle(e);
//      e.printStackTrace();
//    }
//
//  }
//
//  @Action
//  public void selectOutputDir() {
//
//    while (true) {
//      final JFileChooser jfc = new JFileChooser(new File(outputF.getText()));
//      jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//      jfc.setFileFilter(new FileFilter() {
//
//        public boolean accept(final File f) {
//          return f.isDirectory() && f.exists() && f.canWrite();
//        }
//
//        public String getDescription() {
//          return "Select output directory";
//        }
//      });
//
//      final int returnVal = jfc.showDialog(this, "Select output directory");
//
//      if (returnVal == JFileChooser.APPROVE_OPTION) {
//        final File f = jfc.getSelectedFile();
//
//        if (!f.isDirectory() || !f.exists() || !f.canWrite()) {
//          JOptionPane.showMessageDialog(this.frame, "2", "3", JOptionPane.ERROR_MESSAGE);
//          continue;
//        }
//        outputF.setText(f.getAbsolutePath());
//      }
//      break;
//    }
//  }
//
//  @Action
//  public void selectInputDir() {
//
//    while (true) {
//      final JFileChooser jfc = new JFileChooser(new File(inputF.getText()));
//      jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//      jfc.setFileFilter(new FileFilter() {
//
//        public boolean accept(final File f) {
//          return f.isDirectory() && f.exists() && f.canWrite();
//        }
//
//        public String getDescription() {
//          return "Select input directory";
//        }
//      });
//
//      final int returnVal = jfc.showDialog(this, "Select input directory");
//
//      if (returnVal == JFileChooser.APPROVE_OPTION) {
//        final File f = jfc.getSelectedFile();
//
//        if (!f.isDirectory() || !f.exists() || !f.canWrite()) {
//          JOptionPane.showMessageDialog(this.frame, "2", "3", JOptionPane.ERROR_MESSAGE);
//          continue;
//        }
//
//        inputF.setText(f.getAbsolutePath());
//        final File[] ff = f.listFiles();
//        String text = "";
//        for (int i = 0; i < ff.length; i++) {
//          text += " " + ff[i].getName() + "\n";
//        }
//
//        if (text.trim().length() == 0) {
//          text = "#NO FILES#";
//        }
//
//        fileWindow.setText(text);
//        fileWindow.setVisible(true);
//      }
//      break;
//    }
//  }
//
//  @Action
//  public void closeDialog() {
//    logger.log(Level.INFO, "closeDialog");
//    frame.dispose();
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
//}
