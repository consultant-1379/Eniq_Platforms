package com.ericsson.eniq.techpacksdk.view.actionViews;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.ericsson.eniq.techpacksdk.common.Utils;
import com.ericsson.eniq.component.ExceptionHandler;
import com.ericsson.eniq.component.NumericDocument;
import com.ericsson.eniq.techpacksdk.view.actionViews.parserViews.ASCIIParseView;
import com.ericsson.eniq.techpacksdk.view.actionViews.parserViews.AlarmParseView;
import com.ericsson.eniq.techpacksdk.view.actionViews.parserViews.CSExportParseView;
import com.ericsson.eniq.techpacksdk.view.actionViews.parserViews.CTParseView;
import com.ericsson.eniq.techpacksdk.view.actionViews.parserViews.CustomParseView;
import com.ericsson.eniq.techpacksdk.view.actionViews.parserViews.ENIQASN1ParseView;
import com.ericsson.eniq.techpacksdk.view.actionViews.parserViews.MDCParseView;
import com.ericsson.eniq.techpacksdk.view.actionViews.parserViews.MLXmlParseView;
import com.ericsson.eniq.techpacksdk.view.actionViews.parserViews.ParseView;
import com.ericsson.eniq.techpacksdk.view.actionViews.parserViews.RAMLParseView;
import com.ericsson.eniq.techpacksdk.view.actionViews.parserViews.SASNParseView;
import com.ericsson.eniq.techpacksdk.view.actionViews.parserViews.WIFIInventoryParseView;
import com.ericsson.eniq.techpacksdk.view.actionViews.parserViews.WIFIParseView;
import com.ericsson.eniq.techpacksdk.view.actionViews.parserViews.XMLParseView;
import com.ericsson.eniq.techpacksdk.view.actionViews.parserViews.Xml3GPP32435ParseView;
import com.ericsson.eniq.techpacksdk.view.actionViews.parserViews.BCDParseView;
import com.ericsson.eniq.techpacksdk.view.actionViews.parserViews.TwampMParseView;
import com.ericsson.eniq.techpacksdk.view.actionViews.parserViews.TwampPTParseView;
import com.ericsson.eniq.techpacksdk.view.actionViews.parserViews.TwampSTParseView;


/**
 * 
 * @author lemminkainen
 */
public class ParseActionView implements ActionView {

  private static final String[] AFT_PARSE_ACTIONS = { "move", "delete", "no" };

  // private static final String[] THRESHOLD_METHODS = { "less", "more" };

  private JPanel parent;

  private JTextField baseDir;

  private JTextField outDir;

  private JTextField maxFiles;

  private JTextField dirThreshold;

  private JComboBox dublicateCheck;

  private JTextField minFileAge;

  private JTextField interfaceName;

  private JComboBox afterParseAction;

  private JComboBox doubleCheckAction;

  private JLabel l_doubleCheckAction;

  private JComboBox failedAction;

  private JLabel l_archivePeriod;

  private JTextField archivePeriod;

  private JTextField loaderDir;

  private JTextField inDir;

  private JComboBox parserType;

  private JLabel l_fileNameFormat;

  private JTextField fileNameFormat;

  private JLabel l_processedDir;

  private JTextField processedDir;

  private JPanel pvcanvas;

  private ParseView parseView;

  private GenericPropertiesView gpv;

  // private JTextField parseThreshold;

  private JComboBox thresholdMethod;

  private JComboBox useZip;
  
  private String techpackName;

  @SuppressWarnings("unchecked")
public ParseActionView(JPanel parent, final Meta_transfer_actions action, final JDialog pdialog, final String techpackName) {

    this.parent = parent;
    this.techpackName = techpackName;

    parent.removeAll();

    Properties orig = new Properties();

    if (action != null) {
    	try {
    		orig = Utils.stringToProperty(action.getAction_contents());
        } catch (Exception e) {
          ExceptionHandler.instance().handle(e);
          e.printStackTrace();
        }
    }

    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2, 2, 2, 2);
    c.weightx = 0;
    c.weighty = 0;

    JLabel l_in = new JLabel("Interface name");
    l_in.setToolTipText("The name of the interface. References to Datainterface.INTERFACENAME.");
    parent.add(l_in, c);

    interfaceName = new JTextField(orig.getProperty("interfaceName", ""), 15);
    c.weightx = 1;
    c.gridx = 1;
    parent.add(interfaceName, c);

    c.gridx = 0;
    c.gridy = 1;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;

    JLabel l_bd = new JLabel("Base directory");
    l_bd.setToolTipText("Base directory of this parser.");
    parent.add(l_bd, c);

    baseDir = new JTextField(orig.getProperty("baseDir", ""), 20);
    baseDir.setToolTipText("Base directory of this parser.");
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    c.gridx = 1;
    parent.add(baseDir, c);

    c.gridx = 0;
    c.gridy = 2;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    JLabel inDirLabel = new JLabel("Input directory");
    inDirLabel.setToolTipText("Input directory from where source files are read. If not defined basedir/in is used.");
    parent.add(inDirLabel, c);

    inDir = new JTextField(orig.getProperty("inDir", ""), 20);
    inDir.setToolTipText("Input directory from where source files are read. If not defined basedir/in is used.");
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    c.gridx = 1;
    parent.add(inDir, c);

    c.gridx = 0;
    c.gridy = 3;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    JLabel l_od = new JLabel("Output directory");
    l_od
        .setToolTipText("The output directory where result files are stored. Files are moved into Loader Directory after parse is finished.");
    parent.add(l_od, c);

    outDir = new JTextField(orig.getProperty("outDir", ""), 20);
    outDir
        .setToolTipText("The output directory where result files are stored. Files are moved into Loader Directory after parse is finished.");
    c.gridx = 1;
    c.fill = GridBagConstraints.HORIZONTAL;
    parent.add(outDir, c);

    c.gridx = 0;
    c.gridy = 4;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    JLabel l_ld = new JLabel("Loader directory");
    l_ld
        .setToolTipText("The input directory of loader. After the parsing is finished the output files are moved here for loader.");
    parent.add(l_ld, c);

    loaderDir = new JTextField(orig.getProperty("loaderDir", ""), 20);
    loaderDir
        .setToolTipText("The input directory of loader. After the parsing is finished the output files are moved here for loader.");
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    c.gridx = 1;
    parent.add(loaderDir, c);

    c.gridx = 0;
    c.gridy = 5;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    JLabel l_mfa = new JLabel("Minimum file age");
    l_mfa.setToolTipText("Minimum age of file, in minutes, after the file is parsed.");
    parent.add(l_mfa, c);

    minFileAge = new JTextField(new NumericDocument(), orig.getProperty("minFileAge", ""), 5);
    minFileAge.setToolTipText("Minimum age of file, in minutes, after the file is parsed.");
    c.gridx = 1;
    parent.add(minFileAge, c);

    c.gridx = 0;
    c.gridy = 6;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    JLabel l_mf = new JLabel("Max files per run");
    l_mf.setToolTipText("Maximum amout of files parsed during one parser execution. Default: all files");
    parent.add(l_mf, c);

    maxFiles = new JTextField(new NumericDocument(), orig.getProperty("maxFilesPerRun", ""), 5);
    maxFiles.setToolTipText("Maximum amout of files parsed during one parser execution. Default: all files");
    c.gridx = 1;
    parent.add(maxFiles, c);

    // ****************************

    c.gridx = 0;
    c.gridy = 7;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;

    c.gridx = 1;

    // ******************************

    c.gridx = 0;
    c.gridy = 8;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;

    JLabel l_tm = new JLabel("Directory threshold");
    l_tm.setToolTipText("How many hours old must directory be. Default: 24h");
    parent.add(l_tm, c);

    /*
     * String[] method = { "less", "more" }; thresholdMethod = new
     * JComboBox(method);thresholdMethod.setToolTipText(
     * "How many hours old must directory be. Default: 24h");
     * 
     * thresholdMethod.setSelectedIndex(0); if
     * ("less".equalsIgnoreCase(orig.getProperty("thresholdMethod", "")))
     * thresholdMethod.setSelectedIndex(0); if
     * ("more".equalsIgnoreCase(orig.getProperty("thresholdMethod", "")))
     * thresholdMethod.setSelectedIndex(1);
     */

    dirThreshold = new JTextField(new NumericDocument(), orig.getProperty("dirThreshold", ""), 5);
    dirThreshold.setToolTipText("How many hours old must directory be. Default: 24h");

    c.gridx = 1;
    parent.add(dirThreshold, c);

    /*
     * parent.add(thresholdMethod, c);
     */

    // ******************************
    c.gridx = 0;
    c.gridy = 9;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;

    JLabel thresholdMethodLabel = new JLabel("Threshold method");

    thresholdMethodLabel
        .setToolTipText("Method of parsing. \"more\" means that the file or directory is more older than days defined in Parse threshold."
            + "\"less\" means that file or directory is less older than days defined in Parse threshold.");

    parent.add(thresholdMethodLabel, c);

    c.gridx = 1;
    String[] method = { "less", "more" };
    thresholdMethod = new JComboBox(method);
    thresholdMethod
        .setToolTipText("Method of parsing. \"more\" means that the file or directory is more older than days defined in Parse threshold."
            + "\"less\" means that file or directory is less older than days defined in Parse threshold.");

    thresholdMethod.setSelectedIndex(0);
    if ("less".equalsIgnoreCase(orig.getProperty("thresholdMethod", ""))) {
      thresholdMethod.setSelectedIndex(0);
    }

    if ("more".equalsIgnoreCase(orig.getProperty("thresholdMethod", ""))) {
      thresholdMethod.setSelectedIndex(1);
    }

    parent.add(thresholdMethod, c);

    // ******************************

    c.gridx = 0;
    c.gridy = 10;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    JLabel l_fa = new JLabel("After failed parsing");
    l_fa.setToolTipText("Action performed if parsing fails. Move moves file to failed directory. Delete deletes file.");
    parent.add(l_fa, c);

    String[] fa = { "move", "delete" };

    failedAction = new JComboBox(fa);
    failedAction
        .setToolTipText("Action performed if parsing fails. Move moves file to failed directory. Delete deletes file.");
    String selFAction = orig.getProperty("failedAction", "");
    for (int i = 0; i < fa.length; i++) {
      if (fa[i].equals(selFAction)) {
        failedAction.setSelectedIndex(i);
        break;
      }
    }

    c.gridx = 1;
    parent.add(failedAction, c);

    // ******************************

    c.gridx = 0;
    c.gridy = 11;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    JLabel l_dc = new JLabel("Duplicate check");
    l_dc
        .setToolTipText("Use duplicateCheck to avoid parsing same file for many times? If used, \"Filename format\" must be defined.");
    parent.add(l_dc, c);

    String[] bools = { "true", "false" };
    dublicateCheck = new JComboBox(bools);
    dublicateCheck
        .setToolTipText("Use duplicateCheck to avoid parsing same file for many times? If used, \"Filename format\" must be defined.");
    if ("false".equalsIgnoreCase(orig.getProperty("dublicateCheck", ""))) {
      dublicateCheck.setSelectedIndex(1);
    } else {
      dublicateCheck.setSelectedIndex(0);
    }
    dublicateCheck.addActionListener(new ActionListener() {

      public void actionPerformed(final ActionEvent ae) {
        l_fileNameFormat.setEnabled(dublicateCheck.getSelectedIndex() == 0);
        fileNameFormat.setEnabled(dublicateCheck.getSelectedIndex() == 0);
        l_processedDir.setEnabled(dublicateCheck.getSelectedIndex() == 0);
        processedDir.setEnabled(dublicateCheck.getSelectedIndex() == 0);
        l_doubleCheckAction.setEnabled(dublicateCheck.getSelectedIndex() == 0);
        doubleCheckAction.setEnabled(dublicateCheck.getSelectedIndex() == 0);
      }
    });
    c.gridx = 1;
    parent.add(dublicateCheck, c);

    // ********************************

    c.gridx = 0;
    c.gridy = 12;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    l_doubleCheckAction = new JLabel("After duplicate check action");
    l_doubleCheckAction
        .setToolTipText("Action performed after the duplicate check. Move moves file to duplicate directory. Delete deletes file.");
    l_doubleCheckAction.setEnabled(dublicateCheck.getSelectedIndex() == 0);
    parent.add(l_doubleCheckAction, c);

    String[] adca = { "move", "delete" };

    doubleCheckAction = new JComboBox(adca);
    doubleCheckAction
        .setToolTipText("Action performed after the duplicate check. Move moves file to duplicate directory. Delete deletes file.");
    String selDCAction = orig.getProperty("doubleCheckAction", "");
    for (int i = 0; i < adca.length; i++) {
      if (adca[i].equals(selDCAction)) {
        doubleCheckAction.setSelectedIndex(i);
        break;
      }
    }

    c.gridx = 1;
    doubleCheckAction.setEnabled(dublicateCheck.getSelectedIndex() == 0);
    parent.add(doubleCheckAction, c);

    // ********************************
    // * Filename format
    // ********************************

    c.gridx = 0;
    c.gridy = 13;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    l_fileNameFormat = new JLabel("Filename format");
    l_fileNameFormat
        .setToolTipText("RegExp pattern to find timestamp from filename. Used to identify the date information in duplicate check.");
    l_fileNameFormat.setEnabled(dublicateCheck.getSelectedIndex() == 0);
    parent.add(l_fileNameFormat, c);

    fileNameFormat = new JTextField(orig.getProperty("ProcessedFiles.fileNameFormat", ""), 20);
    fileNameFormat
        .setToolTipText("RegExp pattern to find timestamp from filename. Used to identify the date of input file in duplicate check.");
    fileNameFormat.setEnabled(dublicateCheck.getSelectedIndex() == 0);
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    c.gridx = 1;
    parent.add(fileNameFormat, c);
    
    // ********************************
    // * Processed list directory
    // ********************************
    c.gridx = 0;
    c.gridy = 14;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    l_processedDir = new JLabel("Processed list directory");
    l_processedDir
        .setToolTipText("Location where list of processed files are kept. File lists are used in duplicate check.");
    l_processedDir.setEnabled(dublicateCheck.getSelectedIndex() == 0);
    parent.add(l_processedDir, c);

    processedDir = new JTextField(orig.getProperty("ProcessedFiles.processedDir", ""), 20);
    processedDir
        .setToolTipText("Location where list of processed files are kept. File lists are used in duplicate check.");
    processedDir.setEnabled(dublicateCheck.getSelectedIndex() == 0);
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    c.gridx = 1;
    parent.add(processedDir, c);

    c.gridx = 0;
    c.gridy = 15;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    JLabel l_apa = new JLabel("After parse action");
    l_apa.setToolTipText("Action performed after the source file is parsed. Move moves file to archive directory.");
    parent.add(l_apa, c);

    afterParseAction = new JComboBox(AFT_PARSE_ACTIONS);
    afterParseAction
        .setToolTipText("Action performed after the source file is parsed. Move moves file to archive directory.");
    String selAPAction = orig.getProperty("afterParseAction", "");
    for (int i = 0; i < AFT_PARSE_ACTIONS.length; i++) {
      if (AFT_PARSE_ACTIONS[i].equals(selAPAction)) {
        afterParseAction.setSelectedIndex(i);
        break;
      }
    }
    afterParseAction.addActionListener(new ActionListener() {

      public void actionPerformed(final ActionEvent ae) {
        l_archivePeriod.setEnabled(afterParseAction.getSelectedIndex() == 0);
        archivePeriod.setEnabled(afterParseAction.getSelectedIndex() == 0);
      }
    });
    c.gridx = 1;
    parent.add(afterParseAction, c);

    c.gridx = 0;
    c.gridy = 16;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    l_archivePeriod = new JLabel("Archive directory length");
    l_archivePeriod
        .setToolTipText("Length of archive directory, in hours, where parsed files are moved. Default: 168 (hours = 1 week)");
    l_archivePeriod.setEnabled(afterParseAction.getSelectedIndex() == 0);
    parent.add(l_archivePeriod, c);

    archivePeriod = new JTextField(new NumericDocument(), orig.getProperty("archivePeriod", ""), 5);
    archivePeriod
        .setToolTipText("Length of archive directory, in hours, where parsed files are moved. Default: 168 (hours = 1 week)");
    archivePeriod.setEnabled(afterParseAction.getSelectedIndex() == 0);
    c.gridx = 1;
    parent.add(archivePeriod, c);

    /*
     * c.gridx = 0; c.gridy = 14; c.fill = GridBagConstraints.NONE; c.weightx =
     * 0; JLabel thresholdLabel = new JLabel("Parse threshold");
     * parent.add(thresholdLabel, c);
     * 
     * parseThreshold = new JTextField(orig.getProperty("dirThreshold", "24"),
     * 5); parseThreshold.setToolTipText("Parsing threshold time in days.");
     * c.fill = GridBagConstraints.NONE; c.weightx = 1; c.gridx = 1;
     * parent.add(parseThreshold, c);
     */
    /*
     * 
     * c.gridx = 0; c.gridy = 12; c.fill = GridBagConstraints.NONE; c.weightx =
     * 0; JLabel thresholdMethodLabel = new JLabel("Threshold method");
     * parent.add(thresholdMethodLabel, c);
     * 
     * thresholdMethod = new JComboBox(THRESHOLD_METHODS); thresholdMethod
     * .setToolTipText(
     * "Method of parsing. \"more\" means that the file or directory is more older than days defined in Parse threshold."
     * +
     * "\"less\" means that file or directory is less older than days defined in Parse threshold."
     * );
     * 
     * String selectedThresholdMethod = orig.getProperty("thresholdMethod",
     * "less"); for (int i = 0; i < THRESHOLD_METHODS.length; i++) { if
     * (THRESHOLD_METHODS[i].equals(selectedThresholdMethod)) {
     * thresholdMethod.setSelectedIndex(i); break; } } c.weightx = 1; c.gridx =
     * 1; parent.add(thresholdMethod, c);
     */

    // ******************************
    c.gridx = 0;
    c.gridy = 17;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    JLabel l_uz = new JLabel("UnZip input files");
    l_uz.setToolTipText("Tries to unzip input files.");
    parent.add(l_uz, c);

    String[] unzip = { "none", "zip", "gzip" };
    // maps the older version true and false instructions to none and zip
    String[] oldunzip = { "false", "true" };

    useZip = new JComboBox(unzip);
    useZip.setToolTipText("How to unzip input files.");

    String unzipAction = orig.getProperty("useZip", "");
    for (int i = 0; i < unzip.length; i++) {
      if (unzip[i].equals(unzipAction)) {
        useZip.setSelectedIndex(i);
        break;
      }
    }

    for (int i = 0; i < oldunzip.length; i++) {
      if (oldunzip[i].equals(unzipAction)) {
        useZip.setSelectedIndex(i);
        break;
      }
    }

    c.gridx = 1;
    parent.add(useZip, c);

    // ********************************
    // * parserType
    // ********************************

    c.gridx = 0;
    c.gridy = 18;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    JLabel l_pt = new JLabel("Input data format");
    l_pt.setToolTipText("Format of input data. Determines the used parser implementation.");
    parent.add(l_pt, c);
    
    // 20100715, eeoidiv,CR 26/109 18-FCP 103 8147/12:Removing parser dependency from ENIQ platform
    // new parser can be added without changing the platform code
    Object[] parserFormatsList = Utils.getParserFormats();
	parserType = new JComboBox(parserFormatsList);
	parserType.setSelectedIndex(-1);
    String selPType = orig.getProperty("parserType", "");
    String altName = Utils.getParserFormatNameFromPath(selPType);
    selectNameInComboList(parserType, parserFormatsList, selPType, altName);
    parserType.setName("parserType");
    parserType.setToolTipText("Format of input data. Determines the used parser implementation.");
    parserType.setEnabled(true);
    parserType.addActionListener(new ActionListener() {

      public void actionPerformed(final ActionEvent ae) {
        pvcanvas.removeAll();
        parseView = getParseView((String) parserType.getSelectedItem(), new Properties(), pvcanvas, pdialog);
        ParseActionView.this.parent.invalidate();
        ParseActionView.this.parent.revalidate();
        ParseActionView.this.parent.repaint();
      }
    });

    c.fill = GridBagConstraints.NONE;
    c.weightx = 1;
    c.gridx = 1;
    parent.add(parserType, c);
    
    // ********************************
    // * properties based on parserType
    // ********************************

    pvcanvas = new JPanel();
    parseView = getParseView((String) parserType.getSelectedItem(), orig, pvcanvas, pdialog);
    c.gridx = 0;
    c.gridy = 19;
    c.gridwidth = 2;
    c.weightx = 1;
    c.fill = GridBagConstraints.HORIZONTAL;
    parent.add(pvcanvas, c);

    Set<String> s = new HashSet<String>();
    s.add("inDir");
    s.add("baseDir");
    s.add("outDir");
    s.add("maxFilesPerRun");
    s.add("dublicateCheck");
    s.add("minFileAge");
    s.add("source");
    s.add("interfaceName");
    s.add("afterParseAction");
    s.add("archivePeriod");
    s.add("loaderDir");
    s.add("parserType");
    s.add("thresholdMethod");
    s.add("dirThreshold");
    s.add("ProcessedFiles.fileNameFormat");
    s.add("ProcessedFiles.processedDir");
    s.add("thresholdMethod");
    s.add("dirThreshold");
    s.add("doubleCheckAction");
    s.add("failedAction");
    s.add("useZip");

    if (parseView != null) {
      s.addAll(parseView.getParameterNames());
    }

    gpv = new GenericPropertiesView(orig, s, pdialog);
    c.weighty = 1;
    c.gridy = 20;
    parent.add(gpv, c);

    parent.invalidate();
    parent.revalidate();
    parent.repaint();

  } // ParseActionView
  
  protected ParseActionView(){
	  // Used for testing.
  }
  
  protected void selectNameInComboList(JComboBox jcombo, Object[] comboList, String name, String altName) {
	  if (name != null && name.length() > 0) {
	      int i = selectInList(comboList, name, altName);
	      jcombo.setSelectedIndex(i);
	  }
  } // selectInComboList

  /**
   * @param comboList
   * @param name
   * @param altName
   * @return
   */
  protected int selectInList(Object[] comboList, String name, String altName) {
	  int i = -1;
	  i = findInList(comboList, name);
	  if (i<0) {
		  i = findInList(comboList, altName);
	  } 
	  if(i<0) {
	    // Select CUSTOM if not recognised
	    i = findInList(comboList, "CUSTOM");
	  }
	  return i;
  }
  protected int findInList(Object[] comboList, String name) {
    int found = -1;
    if (name != null && name.length() > 0) {
	    for (int i = 0; i < comboList.length; i++) {
	      if (comboList[i].equals(name)) {
	        found = i;
	        break;
	      }
	    }
    } // findInList
    return found;
  }

  public String getType() {
    return "Parse";
  }

  public JPanel getJPanel() {
    return parent;
  }

  public String getContent() throws Exception {
    final Properties p = gpv.getProperties();

    p.setProperty("inDir", inDir.getText().trim());
    p.setProperty("baseDir", baseDir.getText().trim());
    p.setProperty("outDir", outDir.getText().trim());
    p.setProperty("maxFilesPerRun", maxFiles.getText().trim());
    p.setProperty("dublicateCheck", (String) dublicateCheck.getSelectedItem());
    p.setProperty("minFileAge", minFileAge.getText().trim());
    p.setProperty("interfaceName", interfaceName.getText().trim());
    p.setProperty("afterParseAction", (String) afterParseAction.getSelectedItem());
    p.setProperty("failedAction", (String) failedAction.getSelectedItem());
    p.setProperty("doubleCheckAction", (String) doubleCheckAction.getSelectedItem());
    p.setProperty("archivePeriod", archivePeriod.getText().trim());
    p.setProperty("loaderDir", loaderDir.getText().trim());
    p.setProperty("inDir", inDir.getText().trim());
    p.setProperty("parserType", (String)parserType.getSelectedItem());
    p.setProperty("ProcessedFiles.fileNameFormat", fileNameFormat.getText().trim());
    p.setProperty("ProcessedFiles.processedDir", processedDir.getText().trim());
    p.setProperty("dirThreshold", dirThreshold.getText().trim());
    p.setProperty("thresholdMethod", (String) thresholdMethod.getSelectedItem());
    p.setProperty("useZip", (String) useZip.getSelectedItem());
    
    // Let any extra parseView parameters override the default ones.
    if (parseView != null) {
      parseView.fillParameters(p);
    }
    // 20100715, eeoidiv,CR 26/109 18-FCP 103 8147/12:Removing parser dependency from ENIQ platform
    // new parser can be added without changing the platform code
    // For Config Parsers specify path
    p.setProperty("parserType", (String) Utils.getParserFormatPath((String)p.getProperty("parserType")));

    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    p.store(baos, "");

    return baos.toString();

  } // getContent

  public String getWhere() throws Exception {
    return "";
  }

  public String validate() {
    String error = "";

    if (interfaceName.getText().trim().length() <= 0) {
      error += "Parameter Interface name must ne defined\n";
    }

    if (baseDir.getText().trim().length() <= 0) {
      error += "Parameter Base directory must ne defined\n";
    }

    if (outDir.getText().trim().length() <= 0) {
      error += "Parameter Output directory must ne defined\n";
    }

    if (interfaceName.getText().trim().length() <= 0) {
      error += "Parameter Interface name must ne defined\n";
    }

    final String mfa = minFileAge.getText().trim();
    if (mfa.length() > 0) {
      try {
        Integer.parseInt(minFileAge.getText().trim());
      } catch (NumberFormatException nfe) {
        error += "Parameter Minimum file age is not a number\n";
      }
    }

    final String mfp = maxFiles.getText().trim();
    if (mfp.length() > 0) {
      try {
        Integer.parseInt(mfp);
      } catch (NumberFormatException nfe) {
        error += "Parameter Max files per run is not a number\n";
      }
    }

    if (afterParseAction.getSelectedIndex() == 0) {
      final String adl = archivePeriod.getText().trim();
      if (adl.length() > 0) {
        try {
          Integer.parseInt(adl);
        } catch (NumberFormatException nfe) {
          error += "Parameter Archive directory length is not a number\n";
        }
      }
    }

    final String directoryThresholdValue = this.dirThreshold.getText().trim();
    if (directoryThresholdValue.length() > 0) {
      try {
        Integer.parseInt(directoryThresholdValue);
      } catch (NumberFormatException nfe) {
        error += "Parameter Directory threshold is not a number\n";
      }
    }

    if (parseView != null) {
      error += parseView.validate();
    }

    return error;
  }

  public boolean isChanged() {
    return true;
  }

  public ParseView getParseView(final String parserType, final Properties p, final JPanel pvcanvas, final JDialog parent) {
    if(parserType == null) {return null;}
    if (parserType.equals("alarm") || parserType.equals("com.distocraft.dc5000.etl.alarm.AlarmParser")) {
      return new AlarmParseView(p, pvcanvas);
    } else if (parserType.equals("ascii") || parserType.equals("com.distocraft.dc5000.etl.ascii.ASCIIParser")) {
      return new ASCIIParseView(p, pvcanvas);
    } else if (parserType.equals("xml") || parserType.equals("com.distocraft.dc5000.etl.xml.XMLParser")) {
      return new XMLParseView(p, pvcanvas);
    } else if (parserType.equals("csexport") || parserType.equals("com.distocraft.dc5000.etl.csexport.CSExportParser")) {
      return new CSExportParseView(p, pvcanvas);
    } else if (parserType.equals("ct") || parserType.equals("com.ericsson.eniq.etl.ct.CTParser")) {
      return new CTParseView(p, pvcanvas);
    } else if (parserType.equals("mdc") || parserType.equals("com.distocraft.dc5000.etl.mdc.MDCParser")) {
      return new MDCParseView(p, pvcanvas, parent, this.techpackName);
    } else if (parserType.equals("raml") || parserType.equals("com.distocraft.dc5000.etl.raml.RAMLParser")) {
      return new RAMLParseView(p, pvcanvas);
    } else if (parserType.equals("eniqasn1") || parserType.equals("com.ericsson.eniq.etl.asn1.ASN1Parser")) {
      return new ENIQASN1ParseView(p, pvcanvas);
    } else if (parserType.equals("sasn") || parserType.equals("com.ericsson.eniq.etl.sasn.SASNParser")) {
      return new SASNParseView(p, pvcanvas);
    } else if (parserType.equals("3gpp32435")
        || parserType.equals("com.distocraft.dc5000.etl.xml3GPP32435.Xml3GPP32435Parser")) {
      return new Xml3GPP32435ParseView(p, pvcanvas, parent);
    } else if (parserType.equals("ebs") || parserType.equals("com.distocraft.dc5000.etl.ebs.EBSParser")) {
      return new Xml3GPP32435ParseView(p, pvcanvas, parent);
    } else if (parserType.equals("bcd") || parserType.equals("com.ericsson.eniq.etl.bcd.BCDParser")) {
        return new BCDParseView(p, pvcanvas);
    } else if (parserType.equals("wifi")|| parserType.equals("com.ericsson.wifi.ewmnbi.WIFIParser")) {
    	return new WIFIParseView(p,pvcanvas);
    } else if (parserType.equals("wifiinventory")|| parserType.equals("com.ericsson.wifi.ewmnbi.WIFIInventoryParser")){
    	return new WIFIInventoryParseView(p, pvcanvas);
    } else if (parserType.equals("twampM")|| parserType.equals("com.ericsson.eniq.etl.twampM.twampM")){
    	return new TwampMParseView(p, pvcanvas);
    } else if (parserType.equals("twampPT")|| parserType.equals("com.ericsson.eniq.etl.twampPT.twampPT")){
    	return new TwampPTParseView(p, pvcanvas);
    } else if (parserType.equals("twampST")|| parserType.equals("com.ericsson.eniq.etl.twampST.twampST")){
    	return new TwampSTParseView(p, pvcanvas);
    }else if (parserType.equals("mlXmlParser")|| parserType.equals("com.distocraft.dc5000.etl.mlxml")){
    	return new MLXmlParseView(p, pvcanvas, parent);
    }    
    else if (parserType.equals("CUSTOM") ) {
      return new CustomParseView(p, pvcanvas);
    } else {
      return null;
    }
  }
}
