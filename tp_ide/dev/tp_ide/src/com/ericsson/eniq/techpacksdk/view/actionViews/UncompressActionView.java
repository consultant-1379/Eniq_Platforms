package com.ericsson.eniq.techpacksdk.view.actionViews;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.ericsson.eniq.component.ExceptionHandler;

/**
 * Copyright Distocraft 2005<br>
 * <br>
 * $id$
 * 
 * @author lemminkainen
 */
public class UncompressActionView implements ActionView {

  private JTextField baseDir;

  private JTextField inDir;

  private JTextField outDir;

  private JTextField failedDir;

  private JTextField archiveDir;

  private JTextField fileSuffix;

  private JComboBox afterUncompressCommand;

  private JComboBox uncompressor;

  private JComboBox addSeparator;

  private JTextField unCompressCommand;

  private JTextField directorySeparator;

  // private Meta_transfer_actions action;

  private GridBagConstraints c;

  private Properties actionContent;

  private JPanel parent;

  public UncompressActionView(JPanel aparent, Meta_transfer_actions action) {

    // this.action = action;
    this.parent = aparent;

    parent.removeAll();

    c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2, 2, 2, 2);
    c.weightx = 0;
    c.weighty = 0;

    actionContent = new Properties();

    if (action != null) {

      String act_cont = action.getAction_contents();

      if (act_cont != null && act_cont.length() > 0) {
        try {
          ByteArrayInputStream bais = new ByteArrayInputStream(act_cont.getBytes());
          actionContent.load(bais);
        } catch (Exception e) {
          ExceptionHandler.instance().handle(e);
          e.printStackTrace();
        }
      }
    }

    baseDir = new JTextField(actionContent.getProperty("baseDir", ""), 30);
    inDir = new JTextField(actionContent.getProperty("inDir", ""), 30);
    outDir = new JTextField(actionContent.getProperty("outDir", ""), 30);
    archiveDir = new JTextField(actionContent.getProperty("archiveDir", ""), 30);
    failedDir = new JTextField(actionContent.getProperty("failedDir", ""), 30);
    fileSuffix = new JTextField(actionContent.getProperty("fileSuffix", ""), 10);

    String[] aftCommands = { "copy", "delete", "move" };
    afterUncompressCommand = new JComboBox(aftCommands);
    afterUncompressCommand.setToolTipText("What is done to the input files after uncompression.");
    String old = actionContent.getProperty("afterUncompressCommand", "delete");
    for (int i = 0; i < aftCommands.length; i++) {
      if (aftCommands[i].equals(old))
        afterUncompressCommand.setSelectedIndex(i);
    }

    String[] compressors = { "com.distocraft.dc5000.uncompress.Tar", "com.distocraft.dc5000.uncompress.Uncompress",
        "com.distocraft.dc5000.uncompress.Zip" };
    uncompressor = new JComboBox(compressors);
    uncompressor.setToolTipText("Method used to uncompress files");
    String cold = actionContent.getProperty("uncompressor", "com.distocraft.dc5000.uncompress.Tar");
    for (int i = 0; i < compressors.length; i++) {
      if (compressors[i].equals(cold))
        uncompressor.setSelectedIndex(i);
    }

    uncompressor.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent ae) {

        makeLayout();

      }
    });

    String[] addSeparatorString = { "false", "true" };
    addSeparator = new JComboBox(addSeparatorString);
    addSeparator.setToolTipText("Is directory added to the output filename.");

    String cnt = "false";

    if (actionContent.getProperty("directorySeparator", "").length() > 0) {
      cnt = "true";
    }

    for (int i = 0; i < addSeparatorString.length; i++) {
      if (addSeparatorString[i].equals(cnt))
        addSeparator.setSelectedIndex(i);
    }

    addSeparator.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent ae) {

        makeLayout();

      }
    });

    unCompressCommand = new JTextField(actionContent.getProperty("unCompressCommand", ""), 30);
    unCompressCommand.setToolTipText("System comman used to uncompress files.");

    directorySeparator = new JTextField(actionContent.getProperty("directorySeparator", ""), 5);
    directorySeparator
        .setToolTipText("String that is added to the filename (output file) between directory and actual filename.");

    makeLayout();

  }

  private void makeLayout() {

    parent.removeAll();

    c.gridx = 0;
    c.gridy = 0;
    c.weighty = 0;
    c.weightx = 0;
    c.fill = GridBagConstraints.NONE;

    baseDir.setToolTipText("Base directory where in,out,failed and archive directories are searched.");
    JLabel l_baseDir = new JLabel("Base directory");
    l_baseDir.setToolTipText("Base directory where in,out,failed and archive directories are searched.");
    parent.add(l_baseDir, c);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 1;
    parent.add(baseDir, c);

    inDir.setToolTipText("Input directory where files are read.");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 1;
    c.weightx = 0;
    JLabel l_inDir = new JLabel("In directory");
    l_inDir.setToolTipText("Input directory where files are read.");
    parent.add(l_inDir, c);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 0;
    c.gridx = 1;
    parent.add(inDir, c);

    outDir.setToolTipText("Output directory where files are uncompressed.");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 2;
    c.weightx = 0;
    JLabel l_outDir = new JLabel("Out directory");
    l_outDir.setToolTipText("Output directory where files are uncompressed.");
    parent.add(l_outDir, c);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 0;
    c.gridx = 1;
    parent.add(outDir, c);

    failedDir.setToolTipText("Failed directory where files cause error(s) are moved.");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 3;
    c.weightx = 0;
    JLabel l_failedDir = new JLabel("Failed directory");
    l_failedDir.setToolTipText("Failed directory where files cause error(s) are moved.");
    parent.add(l_failedDir, c);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 0;
    c.gridx = 1;
    parent.add(failedDir, c);

    archiveDir.setToolTipText("Directory where all correctly uncompressed files (source files) are moved/copied.");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 4;
    c.weightx = 0;
    JLabel l_archiveDir = new JLabel("Archive directory");
    l_archiveDir.setToolTipText("Directory where all correctly uncompressed files (source files) are moved/copied.");
    parent.add(l_archiveDir, c);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 0;
    c.gridx = 1;
    parent.add(archiveDir, c);

    fileSuffix.setToolTipText("RegExp mask used filttering input files.");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 5;
    c.weightx = 0;
    JLabel l_fileSuffix = new JLabel("File mask");
    l_fileSuffix.setToolTipText("RegExp mask used filttering input files.");
    parent.add(l_fileSuffix, c);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 0;
    c.gridx = 1;
    parent.add(fileSuffix, c);

    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 6;
    c.weightx = 0;
    JLabel l_afterUncompressCommand = new JLabel("After uncompress action");
    l_afterUncompressCommand.setToolTipText("What is done to the input files after uncompression.");
    parent.add(l_afterUncompressCommand, c);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 0;
    c.gridx = 1;
    parent.add(afterUncompressCommand, c);

    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 7;
    c.weightx = 0;
    JLabel l_uncompressor = new JLabel("Uncompressor");
    l_uncompressor.setToolTipText("Method used to uncompress files");
    parent.add(l_uncompressor, c);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 0;
    c.gridx = 1;
    parent.add(uncompressor, c);

    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 8;
    c.weightx = 0;
    JLabel l_addSeparator = new JLabel("Add directory");
    l_addSeparator.setToolTipText("Is directory added to the output filename.");
    parent.add(l_addSeparator, c);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 0;
    c.gridx = 1;
    parent.add(addSeparator, c);

    if (uncompressor.getSelectedIndex() == 1) {

      unCompressCommand = new JTextField(actionContent.getProperty("unCompressCommand", ""), 30);
      unCompressCommand.setToolTipText("System comman used to uncompress files.");
      c.fill = GridBagConstraints.NONE;
      c.gridx = 0;
      c.gridy = 9;
      c.weightx = 0;
      JLabel l_unCompressCommand = new JLabel("Uncompress command");
      l_unCompressCommand.setToolTipText("System comman used to uncompress files.");
      parent.add(l_unCompressCommand, c);

      c.fill = GridBagConstraints.HORIZONTAL;
      c.weightx = 0;
      c.gridx = 1;
      parent.add(unCompressCommand, c);

    }

    if (addSeparator.getSelectedIndex() == 1) {

      directorySeparator
          .setToolTipText("String that is added to the filename (output file) between directory and actual filename.");
      c.fill = GridBagConstraints.NONE;
      c.gridx = 0;
      c.gridy = 10;
      c.weightx = 0;
      JLabel l_directorySeparator = new JLabel("Directory separator");
      l_directorySeparator
          .setToolTipText("String that is added to the filename (output file) between directory and actual filename.");
      parent.add(l_directorySeparator, c);

      c.fill = GridBagConstraints.HORIZONTAL;
      c.weightx = 0;
      c.gridx = 1;
      parent.add(directorySeparator, c);

    }

    c.gridx = 1;
    c.gridy = 11;
    c.weightx = 1;
    c.weighty = 1;
    c.fill = GridBagConstraints.BOTH;
    parent.add(Box.createRigidArea(new Dimension(10, 10)), c);

    parent.invalidate();
    parent.revalidate();
    parent.repaint();

  }

  public String getType() {
    return "Uncompress";
  }

  public String getContent() throws Exception {
    Properties p = new Properties();

    if (baseDir.getText().length() > 0)
      p.setProperty("baseDir", baseDir.getText());

    if (inDir.getText().length() > 0)
      p.setProperty("inDir", inDir.getText());

    if (outDir.getText().length() > 0)
      p.setProperty("outDir", outDir.getText());

    if (failedDir.getText().length() > 0)
      p.setProperty("failedDir", failedDir.getText());

    if (archiveDir.getText().length() > 0)
      p.setProperty("archiveDir", archiveDir.getText());

    if (fileSuffix.getText().length() > 0)
      p.setProperty("fileSuffix", fileSuffix.getText());

    p.setProperty("afterUncompressCommand", (String) afterUncompressCommand.getSelectedItem());

    p.setProperty("uncompressor", (String) uncompressor.getSelectedItem());

    if (unCompressCommand.getText().length() > 0)
      p.setProperty("unCompressCommand", unCompressCommand.getText());

    if (directorySeparator.getText().length() > 0 && addSeparator.getSelectedIndex() == 1)
      p.setProperty("directorySeparator", directorySeparator.getText());

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

    try {

      Pattern.compile(fileSuffix.getText());
    } catch (PatternSyntaxException pse) {
      return "File mask: Not a valid regExp pattern\n";
    }
    return "";
  }

}
