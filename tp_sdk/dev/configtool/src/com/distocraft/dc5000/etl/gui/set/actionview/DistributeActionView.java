package com.distocraft.dc5000.etl.gui.set.actionview;

import java.awt.Component;
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
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;

import com.distocraft.dc5000.etl.gui.NumericDocument;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;


/**
 * @author melantie
 * Copyright Distocraft 2005
 * 
 * $id$
 */

public class DistributeActionView implements ActionView {

  private Meta_transfer_actions action;
  
  private JTextField inDir;
  private JTextField outDir;
  private JTextField defOutDir;
  private JTextField pattern;
  private JComboBox fileMethod;
  private JTextField minFileAge;
  private JTextField bufferSize;
  private JComboBox bufferType;
  
  private GridBagConstraints c;
  private JPanel parent;

  public final static String[] FILE_METHOD_MODES_TEXT = { "Move", "Copy" };
  
  public final static String[] BUFFER_TYPE_MODES = { "0", "1" };
  public final static String[] BUFFER_TYPE_MODES_TEXT = { "Filename", "Content" };

  
  public DistributeActionView(JPanel parent, Meta_transfer_actions action) {
    
    this.action = action;
    this.parent = parent;
   
    getProperties();
    printLayout ();
   
  }
  
  public void printLayout () {
    
    parent.removeAll();
    
    GridBagConstraints c = new GridBagConstraints();
    
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2,2,2,2);
    c.weightx = 0;
    c.weighty = 0;
   
    c.gridx = 0;
    c.gridy = 0;
    c.weighty = 0;
    c.weightx = 0;
    c.fill = GridBagConstraints.NONE;
    JLabel l_inDir = new JLabel("IN directory");
    l_inDir.setToolTipText("Input directory, where files are read.");
    parent.add(l_inDir, c);

    c.gridx = 1;
    c.weightx = 1;
    c.fill = GridBagConstraints.HORIZONTAL;
    parent.add(inDir, c);

    //************************************
    
    c.gridx = 0;
    c.gridy = 1;
    c.weightx = 0;
    c.fill = GridBagConstraints.NONE;
    JLabel l_outDir = new JLabel("OUT directory");
    l_outDir.setToolTipText("Output directory, where file matching pattern is moved/copied.");
    parent.add(l_outDir, c);

    c.gridx = 1;
    c.weightx = 1;
    c.fill = GridBagConstraints.HORIZONTAL;
    parent.add(outDir, c);

    //************************************
    
    c.gridx = 0;
    c.gridy = 2;
    c.weightx = 0;
    c.fill = GridBagConstraints.NONE;
    JLabel l_defOutDir = new JLabel("Default out directory");
    l_defOutDir.setToolTipText("If file does not match pattern, file is moved/copied here.");
    parent.add(l_defOutDir, c);

    c.gridx = 1;
    c.weightx = 1;
    c.fill = GridBagConstraints.HORIZONTAL;
    parent.add(defOutDir, c);

    //************************************
    
    c.gridx = 0;
    c.gridy = 3;
    c.weightx = 0;
    c.fill = GridBagConstraints.NONE;
    JLabel l_pattern = new JLabel("Pattern");
    l_pattern.setToolTipText("RegExp patter that must match to the distributed file.");
    parent.add(l_pattern, c);

    c.gridx = 1;
    c.weightx = 1;
    c.fill = GridBagConstraints.HORIZONTAL;
    parent.add(pattern, c);

    //************************************
    
    c.gridx = 0;
    c.gridy = 4;
    c.weightx = 0;
    c.fill = GridBagConstraints.NONE;
    JLabel l_fileMethod = new JLabel("File handling method");
    l_fileMethod.setToolTipText("If pattern is matched what is done with the file.");
    parent.add(l_fileMethod, c);

    c.gridx = 1;
    c.weightx = 1;
    //c.fill = GridBagConstraints.HORIZONTAL;
    parent.add(fileMethod, c);
    
    
    //************************************
   

    c.gridx = 0;
    c.gridy = 5;
    c.weightx = 0;
    c.fill = GridBagConstraints.NONE;
    JLabel l_minFileAge = new JLabel("Min file age");
    l_minFileAge.setToolTipText("Min age of the distributet file in minutes. File must be at least this old to be distributet.");
    parent.add(l_minFileAge, c);

    c.gridx = 1;
    c.weightx = 1;
    parent.add(minFileAge, c);
  
    //************************************
    
    c.gridx = 0;
    c.gridy = 6;
    c.weightx = 0;
    c.fill = GridBagConstraints.NONE;
    JLabel l_bufferType = new JLabel("Buffer type");
    l_bufferType.setToolTipText("Where is the pattern matching done.");
    parent.add(l_bufferType, c);

    c.gridx = 1;
    c.weightx = 1;
    //c.fill = GridBagConstraints.HORIZONTAL;
    parent.add(bufferType, c);
  
    
    //************************************
    
    if (bufferType.getSelectedIndex()>0){
      

      c.gridx = 0;
      c.gridy = 7;
      c.weightx = 0;
      c.fill = GridBagConstraints.NONE;
      JLabel l_bufferSize = new JLabel("Buffer size");
      l_bufferSize.setToolTipText("Size of the buffer (characters read from the begining of the file) where matching is done.");
      parent.add(l_bufferSize, c);
    
      c.gridx = 1;
      c.weightx = 1;
      parent.add(bufferSize, c);
      
    }
    

    //************************************
    
    c.gridx = 1;
    c.gridy = 8;
    c.weightx = 1;
    c.weighty = 1;
    
    c.fill = GridBagConstraints.BOTH;
    //parent.add(Box.createRigidArea(new Dimension(8, 8)), c);

    parent.invalidate();
    parent.revalidate();
    parent.repaint();
    
  }
  
  public String getType() {
    return "Distribute";
  }

  public void getProperties() {
  
  Properties orig = new Properties();

  if (action != null) {

    String act_cont = action.getAction_contents();

    if (act_cont != null && act_cont.length() > 0) {

      try {
        ByteArrayInputStream bais = new ByteArrayInputStream(act_cont.getBytes());
        orig.load(bais);
        bais.close();
      } catch (Exception e) {
        System.out.println("Error loading action contents");
        e.printStackTrace();
      }
    }
  }

  inDir = new JTextField(orig.getProperty("inDir", ""), 10);
  outDir = new JTextField(orig.getProperty("outDir", ""), 10);
  defOutDir = new JTextField(orig.getProperty("defaultOutDir", ""), 10);
  pattern = new JTextField(orig.getProperty("pattern", ""), 10);
  minFileAge = new JTextField(new NumericDocument(),orig.getProperty("minFileAge", ""), 4);
  bufferSize = new JTextField(orig.getProperty("bufferSize", ""), 8);
  
  inDir.setToolTipText("Output directory, where file matching pattern is moved/copied.");
  outDir.setToolTipText("If file does not match pattern, file is moved/copied here.");
  defOutDir.setToolTipText("If file does not match pattern, file is moved/copied here.");
  pattern.setToolTipText("RegExp patter that must match to the distributed file.");
  minFileAge.setToolTipText("Min age of the distributet file in minutes. File must be at least this old to be distributet.");
  bufferSize.setToolTipText("Size of the buffer (characters read from the begining of the file) where matching is done.");
  
  fileMethod = new JComboBox(FILE_METHOD_MODES_TEXT);
  
  fileMethod.setToolTipText("If pattern is matched what is done with the file.");
  
  try {
    for(int i = 0 ; i < FILE_METHOD_MODES_TEXT.length ; i++) {
      if(FILE_METHOD_MODES_TEXT[i].equals(orig.getProperty("method")))
        fileMethod.setSelectedIndex(i);     
    }
  } catch (Exception e) {
    fileMethod.setSelectedIndex(0);
  }
  
  bufferType = new JComboBox(BUFFER_TYPE_MODES_TEXT);
  bufferType.setToolTipText("Where is the pattern matching done.");
  
  try {
    for(int i = 0 ; i < BUFFER_TYPE_MODES_TEXT.length ; i++) {
      if(BUFFER_TYPE_MODES_TEXT[i].equals(orig.getProperty("type")))
        bufferType.setSelectedIndex(i);     
    }
  } catch (Exception e) {
    bufferType.setSelectedIndex(0);
  }
  
  
  bufferType.addActionListener(new ActionListener() {

    public void actionPerformed(ActionEvent ae) {
      printLayout();
    }
  });
  
  }
  
  public String getContent() throws Exception {
    Properties p = new Properties();

    p.setProperty("inDir", inDir.getText().trim());
    p.setProperty("outDir", outDir.getText().trim());
   
    String SfileMethod = (String) fileMethod.getSelectedItem();
    p.setProperty("method", SfileMethod);
    
    p.setProperty("defaultOutDir", defOutDir.getText().trim());
    p.setProperty("pattern", pattern.getText().trim());
    p.setProperty("minFileAge", String.valueOf(Integer.parseInt(minFileAge.getText().trim())));
    p.setProperty("bufferSize", String.valueOf(Integer.parseInt(bufferSize.getText().trim())));
    
    String SbufferType = (String) bufferType.getSelectedItem();
    p.setProperty("type", SbufferType);
    
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    p.store(baos, "");

    return baos.toString();
  }

  private String getIntParameter(Properties p, String pname) {
    String prop = p.getProperty(pname, "");
    try {
      return String.valueOf(Integer.parseInt(prop)); // Verify parameter value
    } catch (Exception e) {
      return "";
    }
  }
  
  public String getWhere() throws Exception {
    return "";
  }

  public boolean isChanged() {
    return false;
  } 
  
  public String validate() {
    
    String error = "";
    
    try {
      
      Pattern.compile(pattern.getText());
    } catch(PatternSyntaxException  pse) {
      error += "Pattern: Not a valid regExp pattern\n";
    }
    
    if(minFileAge.getText().length() <= 0) {
    } else {
      try {
        Integer.parseInt(minFileAge.getText());
      } catch(NumberFormatException nfe) {
        error += "Min file age is not a valid number.\n";
      }
    }
    
    if(bufferSize.getText().length() <= 0) {
    } else {
      try {
        Integer.parseInt(bufferSize.getText());
      } catch(NumberFormatException nfe) {
        error += "Buffer size is not a valid number.\n";
      }
    }
      
    
    
    return error;
  }
  
  
}

