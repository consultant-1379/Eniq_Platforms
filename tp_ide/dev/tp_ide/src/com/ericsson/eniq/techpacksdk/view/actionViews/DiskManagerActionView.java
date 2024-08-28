package com.ericsson.eniq.techpacksdk.view.actionViews;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;

import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.ericsson.eniq.component.ExceptionHandler;
import com.ericsson.eniq.component.NumericDocument;

/**
 * Copyright Distocraft 2005 <br>
 * <br>
 * $id$
 * 
 * @author lemminkainen
 */
public class DiskManagerActionView implements ActionView {

  public final static String[] ARCHIVE_MODES = { "0", "1", "2", "3", "4" };

  public final static String[] ARCHIVE_MODES_TEXT = { "move files to out dir",
      "create archive named by archive prefix", "create archive named by current day",
      "create archive named by youngest and oldest file", "delete files" };

  public final static String[] FILE_AGE_MODES = { "0", "1" };

  public final static String[] FILE_AGE_MODES_TEXT = { "use file modiftime", "parse timestamp from filename" };

  public final static String[] DELDIR_MODES = { "true", "false" };

  public static final String INVALID_AGE_DAYS_FIELD_ERROR = "File age in days is not a valid. Allowed values are:\n (i) an integer or\n(ii) a static property key plus default value in the format xxxxxx:nn\n\t(where xxxxxx is a valid key and nn is an integer)\n";
  public static final String INVALID_AGE_HOURS_FIELD_ERROR = "File age in hours is not a valid. Allowed values are:\n (i) an integer or\n(ii) a static property key plus default value in the format xxxxxx:nn\n\t(where xxxxxx is a valid key and nn is an integer)\n";
  public static final String INVALID_AGE_MINS_FIELD_ERROR = "File age in minutes is not a valid. Allowed values are:\n (i) an integer or\n(ii) a static property key plus default value in the format xxxxxx:nn\n\t(where xxxxxx is a valid key and nn is an integer)\n";

  // private Meta_transfer_actions action;

  private JPanel parent;

  private GridBagConstraints c;

  private JTextField inDir;

  private JTextField outDir;

  private JTextField selMask;

  private JComboBox archiveMode;

  private JTextField fileAgeDay;

  private JTextField fileAgeMinutes;

  private JTextField fileAgeHour;

  private JTextField directoryDepth;

  private JTextField fileMask;

  private JComboBox fileAgeMode;

  private JTextField timeMask;

  private JTextField archivePrefix;

  private JTextField dateFormatOutput;

  private JTextField dateFormatInput;

  private JComboBox deleteemptydirectories;

  public DiskManagerActionView(JPanel parent, Meta_transfer_actions action) {
    // this.action = action;
    this.parent = parent;

    parent.removeAll();

    c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2, 2, 2, 2);
    c.weightx = 0;
    c.weighty = 0;

    Properties orig = new Properties();

    if (action != null) {

      String act_cont = action.getAction_contents();

      if (act_cont != null && act_cont.length() > 0) {

        try {
          ByteArrayInputStream bais = new ByteArrayInputStream(act_cont.getBytes());
          orig.load(bais);
          bais.close();
        } catch (Exception e) {
          ExceptionHandler.instance().handle(e);
          e.printStackTrace();
        }
      }
    }

    inDir = new JTextField(orig.getProperty("diskManager.dir.inDir", ""), 10);
    outDir = new JTextField(orig.getProperty("diskManager.dir.outDir", ""), 10);
    archiveMode = new JComboBox(ARCHIVE_MODES);
    archiveMode.setRenderer(new ListCellRenderer() {

      public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
          boolean cellHasFocus) {
        String mc = (String) value;

        try {
          int ix = Integer.parseInt(mc);
          return new JLabel(ARCHIVE_MODES_TEXT[ix]);
        } catch (Exception e) {
          return new JLabel(mc);
        }

      }
    });

    try {
      archiveMode.setSelectedIndex(Integer.parseInt(orig.getProperty("diskManager.dir.archiveMode")));
    } catch (Exception e) {
    }

    archiveMode.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent ae) {
        makeLayout();
      }
    });

    selMask = new JTextField(orig.getProperty("diskManager.dir.fileMask", ""), 10);

    fileAgeDay = new JTextField(orig.getProperty("diskManager.dir.fileAgeDay"), 10);

    fileAgeHour = new JTextField(orig.getProperty("diskManager.dir.fileAgeHour"), 10);

    fileAgeMinutes = new JTextField(orig.getProperty("diskManager.dir.fileAgeMinutes"), 10);

    if (fileAgeMinutes.getText().equalsIgnoreCase("")) {
      // Set the default for fileAgeMinutes if not defined.
      fileAgeMinutes.setText("0");
    }

    directoryDepth = new JTextField(new NumericDocument(), getIntParameter(orig, "diskManager.dir.directoryDepth"), 4);

    fileMask = new JTextField(orig.getProperty("diskManager.dir.fileMask", ""), 10);

    fileAgeMode = new JComboBox(FILE_AGE_MODES);

    fileAgeMode.setRenderer(new ListCellRenderer() {

      public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
          boolean cellHasFocus) {
        String mc = (String) value;

        try {
          int ix = Integer.parseInt(mc);
          return new JLabel(FILE_AGE_MODES_TEXT[ix]);
        } catch (Exception e) {
          return new JLabel(mc);
        }

      }
    });

    try {
      fileAgeMode.setSelectedIndex(Integer.parseInt(orig.getProperty("diskManager.dir.fileAgeMode")));
    } catch (Exception e) {
      fileAgeMode.setSelectedIndex(0);
    }

    fileAgeMode.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent ae) {
        makeLayout();
      }
    });

    archivePrefix = new JTextField(orig.getProperty("diskManager.dir.archivePrefix"), 10);

    dateFormatInput = new JTextField(orig.getProperty("diskManager.dir.dateFormatInput"), 10);

    dateFormatOutput = new JTextField(orig.getProperty("diskManager.dir.dateFormatOutput"), 10);

    timeMask = new JTextField(orig.getProperty("diskManager.dir.timeMask"));

    deleteemptydirectories = new JComboBox(DELDIR_MODES);
    try {
      deleteemptydirectories.setSelectedItem(orig.getProperty("diskManager.dir.deleteEmptyDirectories", "false"));
    } catch (Exception e) {
      deleteemptydirectories.setSelectedIndex(1);
    }

    makeLayout();

  }

  private void makeLayout() {

    parent.removeAll();

    c.gridx = 0;
    c.gridy = 0;
    c.weighty = 0;
    c.weightx = 0;
    c.fill = GridBagConstraints.NONE;
    JLabel l_inDir = new JLabel("IN directory");
    l_inDir.setToolTipText("Archived directory.");
    parent.add(l_inDir, c);

    c.gridx = 1;
    c.weightx = 1;
    c.fill = GridBagConstraints.HORIZONTAL;
    inDir.setToolTipText("Archived directory.");
    parent.add(inDir, c);

    c.gridx = 0;
    c.gridy = 1;
    c.weightx = 0;
    c.fill = GridBagConstraints.NONE;
    parent.add(new JLabel("Archiving mode"), c);
    int i_archiveMode = archiveMode.getSelectedIndex();

    c.gridx = 1;
    c.weightx = 1;
    parent.add(archiveMode, c);

    if (i_archiveMode != 4) { // delete files no need to ask output

      c.gridx = 0;
      c.gridy = 2;
      c.weightx = 0;
      c.fill = GridBagConstraints.NONE;
      JLabel l_outDir = new JLabel("OUT directory");
      l_outDir.setToolTipText("Directory where archive files are written.");
      parent.add(l_outDir, c);

      c.gridx = 1;
      c.weightx = 1;
      c.fill = GridBagConstraints.HORIZONTAL;
      outDir.setToolTipText("Directory where archive files are written.");
      parent.add(outDir, c);

    }

    c.gridx = 0;
    c.gridy = 3;
    c.weightx = 0;
    c.fill = GridBagConstraints.HORIZONTAL;
    JLabel l_fileAgeDay = new JLabel("File age in days");
    l_fileAgeDay.setToolTipText("Minimum age of archived files in days.");
    parent.add(l_fileAgeDay, c);

    c.gridx = 1;
    c.weightx = 1;
    fileAgeDay.setToolTipText("Minimum age of archived files in days.");
    parent.add(fileAgeDay, c);

    c.gridx = 0;
    c.gridy = 4;
    c.weightx = 0;
    c.fill = GridBagConstraints.HORIZONTAL;
    JLabel l_fileAgeHour = new JLabel("File age in hours");
    l_fileAgeHour.setToolTipText("Minimum age of archived files in hours.");
    parent.add(l_fileAgeHour, c);

    c.gridx = 1;
    c.weightx = 1;
    fileAgeHour.setToolTipText("Minimum age of archived files in hours.");
    parent.add(fileAgeHour, c);

    c.gridx = 0;
    c.gridy = 5;
    c.weightx = 0;
    c.fill = GridBagConstraints.HORIZONTAL;
    JLabel l_fileAgeMinutes = new JLabel("File age in minutes");
    l_fileAgeMinutes.setToolTipText("Minimum age of archived files in minutes.");
    parent.add(l_fileAgeMinutes, c);

    c.gridx = 1;
    c.weightx = 1;
    fileAgeMinutes.setToolTipText("Minimum age of archived files in minutes.");
    parent.add(fileAgeMinutes, c);

    c.gridx = 0;
    c.gridy = 6;
    c.weightx = 0;
    c.fill = GridBagConstraints.NONE;
    JLabel l_directoryDepth = new JLabel("Directory depth");
    l_directoryDepth.setToolTipText("Maximum directory depth of archived files in archived subdirectories.");
    parent.add(l_directoryDepth, c);

    c.gridx = 1;
    c.weightx = 1;
    directoryDepth.setToolTipText("Maximum directory depth of archived files in archived subdirectories.");
    parent.add(directoryDepth, c);

    c.gridx = 0;
    c.gridy = 7;
    c.weightx = 0;
    c.fill = GridBagConstraints.NONE;
    JLabel l_fileMask = new JLabel("File mask");
    l_fileMask.setToolTipText("RegExp mask used to select archived files.");
    parent.add(l_fileMask, c);

    c.gridx = 1;
    c.weightx = 1;
    c.fill = GridBagConstraints.HORIZONTAL;
    fileMask.setToolTipText("RegExp mask used to select archived files.");
    parent.add(fileMask, c);

    if (i_archiveMode > 0 && i_archiveMode < 4) { // archive files

      c.gridx = 0;
      c.gridy = 8;
      c.weightx = 0;
      c.fill = GridBagConstraints.NONE;
      parent.add(new JLabel("File age mode"), c);

      c.gridx = 1;
      c.weightx = 1;
      c.fill = GridBagConstraints.HORIZONTAL;
      parent.add(fileAgeMode, c);

      if (fileAgeMode.getSelectedIndex() > 0) {

        c.gridx = 0;
        c.gridy = 9;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        JLabel l_timeMask = new JLabel("Filename time mask");
        l_timeMask.setToolTipText("RegExp mask used while parsing timestamp from filename.");
        parent.add(l_timeMask, c);

        c.gridx = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        timeMask.setToolTipText("RegExp mask used while parsing timestamp from filename.");
        parent.add(timeMask, c);

      }

      c.gridx = 0;
      c.gridy = 10;
      c.weightx = 0;
      c.fill = GridBagConstraints.NONE;
      JLabel l_archivePrefix = new JLabel("Archive prefix");
      l_archivePrefix.setToolTipText("Prefix for outputfilenames.");
      parent.add(l_archivePrefix, c);

      c.gridx = 1;
      c.weightx = 1;
      c.fill = GridBagConstraints.HORIZONTAL;
      archivePrefix.setToolTipText("Prefix for outputfilenames.");
      parent.add(archivePrefix, c);

      if (fileAgeMode.getSelectedIndex() > 0) {

        c.gridx = 0;
        c.gridy = 11;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        JLabel l_dateFormatInput = new JLabel("Date format input");
        l_dateFormatInput.setToolTipText("In which format (yyyyMMdd) is timestamp read from filename.");
        parent.add(l_dateFormatInput, c);

        c.gridx = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        dateFormatInput.setToolTipText("In which format (yyyyMMdd) is timestamp read from filename.");
        parent.add(dateFormatInput, c);

      }

      if (i_archiveMode != 1) {

        c.gridx = 0;
        c.gridy = 12;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        JLabel l_dateFormatOutput = new JLabel("Date format output");
        l_dateFormatOutput.setToolTipText("In which format (yyyyMMdd) is timestamp written in outputfile.");
        parent.add(l_dateFormatOutput, c);

        c.gridx = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        dateFormatOutput.setToolTipText("In which format (yyyyMMdd) is timestamp written in outputfile.");
        parent.add(dateFormatOutput, c);

      }

    }

    c.gridx = 0;
    c.gridy = 13;
    c.weightx = 0;
    c.fill = GridBagConstraints.NONE;
    JLabel l_deleteemptydirectories = new JLabel("Delete empty directories");
    l_deleteemptydirectories.setToolTipText("Are empty directories deleted.");
    parent.add(l_deleteemptydirectories, c);

    c.gridx = 1;
    c.weightx = 1;
    c.fill = GridBagConstraints.HORIZONTAL;
    deleteemptydirectories.setToolTipText("Are empty directories deleted.");
    parent.add(deleteemptydirectories, c);

    c.gridx = 1;
    c.gridy = 14;
    c.weightx = 1;
    c.weighty = 1;
    c.fill = GridBagConstraints.BOTH;
    parent.add(Box.createRigidArea(new Dimension(10, 10)), c);

    parent.invalidate();
    parent.revalidate();
    parent.repaint();

  }

  private String getIntParameter(Properties p, String pname) {
    String prop = p.getProperty(pname, "");
    try {
      return String.valueOf(Integer.parseInt(prop)); // Verify parameter value
    } catch (Exception e) {
      return "";
    }
  }

  public String getType() {
    return "Diskmanager";
  }

  public String getContent() throws Exception {
    Properties p = new Properties();

    p.setProperty("diskManager.dir.inDir", inDir.getText().trim());
    p.setProperty("diskManager.dir.outDir", outDir.getText().trim());

    String SarchiveMode = (String) archiveMode.getSelectedItem();

    p.setProperty("diskManager.dir.archiveMode", SarchiveMode);
    p.setProperty("diskManager.dir.fileMask", selMask.getText().trim());
    p.setProperty("diskManager.dir.directoryDepth", String.valueOf(Integer.parseInt(directoryDepth.getText().trim())));
    p.setProperty("diskManager.dir.fileAgeDay", fileAgeDay.getText().trim());
    p.setProperty("diskManager.dir.fileAgeHour", fileAgeHour.getText().trim());
    p.setProperty("diskManager.dir.fileAgeMinutes", fileAgeMinutes.getText().trim());
    p.setProperty("diskManager.dir.fileMask", fileMask.getText().trim());
    p.setProperty("diskManager.dir.timeMask", timeMask.getText().trim());
    p.setProperty("diskManager.dir.deleteEmptyDirectories", (String) deleteemptydirectories.getSelectedItem());

    if (!"0".equals(SarchiveMode)) {

      p.setProperty("diskManager.dir.fileAgeMode", (String) fileAgeMode.getSelectedItem());
      p.setProperty("diskManager.dir.archivePrefix", archivePrefix.getText().trim());
      p.setProperty("diskManager.dir.dateFormatOutput", dateFormatOutput.getText().trim());
      p.setProperty("diskManager.dir.dateFormatInput", dateFormatInput.getText().trim());

    }

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    p.store(baos, "");

    return baos.toString();
  }

  public String getWhere() throws Exception {
    return "";
  }

  public boolean isChanged() {
    return true;
  }

  public String validate() {

    String error = "";

    try {

      Pattern.compile(timeMask.getText());
    } catch (PatternSyntaxException pse) {
      error += "Filename time mask: Not a valid regExp pattern.\n";
    }

    try {

      Pattern.compile(fileMask.getText());
    } catch (PatternSyntaxException pse) {
      error += "File mask: Not a valid regExp pattern.\n";
    }

    if (directoryDepth.getText().length() <= 0) {
    } else {
      try {
        Integer.parseInt(directoryDepth.getText());
      } catch (NumberFormatException nfe) {
        error += "Directory depth is not a valid number.\n";
      }
    }

    if (fileAgeDay.getText().length() <= 0) {
    } else {
      if (!isValidAgeField(fileAgeDay.getText())) {
          error += INVALID_AGE_DAYS_FIELD_ERROR;
      }
    }

    if (fileAgeHour.getText().length() <= 0) {
    } else {
      if (!isValidAgeField(fileAgeHour.getText())) {
          error += INVALID_AGE_HOURS_FIELD_ERROR;
      }
    }

    if (fileAgeMinutes.getText().length() <= 0) {
    } else {
      if (!isValidAgeField(fileAgeMinutes.getText())) {
          error += INVALID_AGE_MINS_FIELD_ERROR;
      }
    }

    return error;
  }

  /**
   * Checks text to see that the value is valid.
   * The value is valid if:
   * 	1. It is an integer
   * 	2. It is a static.property key followed by a colon followed by an integer default
   * 	   e.g. serviceaudit.fileAgeDay:65 
   * 
   * Anything else, including empty string and null, is invalid.
   * 
   * @param text String to validate
   * @return true if text passes validation, false otherwise
   */
  public boolean isValidAgeField(String text) {
	  boolean fieldValidates = false;
	  // must be non-empty
	  if (text != null && !text.trim().equals("")) {

		  boolean textIsInt = true;
		  
		  try {
			  int age = Integer.valueOf(text);
		  } catch (Exception e) {
			  textIsInt = false;
		  }
		  
		  if (textIsInt) {
			  fieldValidates=true;
		  } else {
			  Pattern p = Pattern.compile("[a-zA-Z][\\w.]*[\\w]:[0-9]+");
  			  Matcher m = p.matcher(text);
  			  fieldValidates = m.matches();
		  }
	  }
	  
	  return fieldValidates;
  }
  
  
}
