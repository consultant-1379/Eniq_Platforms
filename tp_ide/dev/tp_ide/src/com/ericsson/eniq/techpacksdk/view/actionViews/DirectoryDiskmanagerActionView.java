package com.ericsson.eniq.techpacksdk.view.actionViews;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.Properties;

import javax.swing.Box;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

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
public class DirectoryDiskmanagerActionView implements ActionView {

  // private Meta_transfer_actions action;

  private JDialog pdialog;

  private JPanel parent;

  private GridBagConstraints c;

  private JTextField inDir;

  private JTextField outDir;

  private JTextField fileAgeHour;

  private JTextField archivePrefix;

  // private JPanel pvcanvas;

  private GenericPropertiesView gpv;

  private Properties orig;

  public DirectoryDiskmanagerActionView(JPanel parent, Meta_transfer_actions action, JDialog pdialog) {
    // this.action = action;
    this.parent = parent;
    this.pdialog = pdialog;

    parent.removeAll();

    c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2, 2, 2, 2);
    c.weightx = 0;
    c.weighty = 0;

    orig = new Properties();

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

    inDir = new JTextField(orig.getProperty("directoryDiskManager.inDir", ""), 10);
    outDir = new JTextField(orig.getProperty("directoryDiskManager.outDir", ""), 10);
    fileAgeHour = new JTextField(new NumericDocument(), getIntParameter(orig, "directoryDiskManager.timeLimit"), 5);
    archivePrefix = new JTextField(orig.getProperty("directoryDiskManager.prefix"), 10);
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
    JLabel l_outDir = new JLabel("OUT directory");
    l_outDir.setToolTipText("Directory where archive files are written.");
    parent.add(l_outDir, c);

    c.gridx = 1;
    c.weightx = 1;
    c.fill = GridBagConstraints.HORIZONTAL;
    outDir.setToolTipText("Directory where archive files are written.");
    parent.add(outDir, c);

    c.gridx = 0;
    c.gridy = 2;
    c.weightx = 0;
    c.fill = GridBagConstraints.NONE;
    JLabel l_fileAgeHour = new JLabel("Dir age in hours");
    l_fileAgeHour.setToolTipText("Minimum age of archived files in hours.");
    parent.add(l_fileAgeHour, c);

    c.gridx = 1;
    c.weightx = 1;
    fileAgeHour.setToolTipText("Minimum age of archived files in hours.");
    parent.add(fileAgeHour, c);

    c.gridx = 0;
    c.gridy = 3;
    c.weightx = 0;
    c.fill = GridBagConstraints.NONE;
    JLabel l_archivePrefix = new JLabel("Archive prefix");
    l_archivePrefix.setToolTipText("Prefix used to name archive files.");
    parent.add(l_archivePrefix, c);

    c.gridx = 1;
    c.weightx = 1;
    c.fill = GridBagConstraints.HORIZONTAL;
    archivePrefix.setToolTipText("Prefix used to name archive files.");
    parent.add(archivePrefix, c);

    c.gridx = 1;
    c.gridy = 10;
    c.weightx = 1;
    c.weighty = 1;
    c.fill = GridBagConstraints.BOTH;
    parent.add(Box.createRigidArea(new Dimension(10, 10)), c);

    HashSet<String> hs = new HashSet<String>();
    hs.add("directoryDiskManager.inDir");
    hs.add("directoryDiskManager.outDir");
    hs.add("directoryDiskManager.timeLimit");
    hs.add("directoryDiskManager.prefix");

    gpv = new GenericPropertiesView(orig, hs, pdialog);

    c.gridx = 0;
    c.gridy = 7;
    c.gridwidth = 2;
    c.fill = GridBagConstraints.BOTH;
    c.weightx = 1;
    c.weighty = 1;

    parent.add(gpv, c);

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

  public String validate() {
    String error = "";

    if (inDir.getText().length() <= 0)
      error += "Parameter IN directory must be defined.\n";

    if (outDir.getText().length() <= 0)
      error += "Parameter OUT directory must be defined.\n";

    if (fileAgeHour.getText().length() <= 0) {
      error += "Parameter Dir age in hours must be defined.\n";
    } else {
      try {
        Integer.parseInt(fileAgeHour.getText());
      } catch (NumberFormatException nfe) {
        error += "Parameter Dir age in hours is not a valid number.\n";
      }
    }

    if (archivePrefix.getText().length() <= 0)
      error += "Parameter Archive prefix must be defined\n";

    return error;
  }

  public String getType() {
    return "DirectoryDiskmanager";
  }

  public String getContent() throws Exception {
    Properties p = gpv.getProperties();

    p.setProperty("directoryDiskManager.inDir", inDir.getText().trim());
    p.setProperty("directoryDiskManager.outDir", outDir.getText().trim());
    p.setProperty("directoryDiskManager.timeLimit", String.valueOf(Integer.parseInt(fileAgeHour.getText().trim())));
    p.setProperty("directoryDiskManager.prefix", archivePrefix.getText().trim());

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

}
