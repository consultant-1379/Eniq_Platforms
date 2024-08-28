package com.ericsson.eniq.techpacksdk.view.actionViews.parserViews;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class XMLParseView implements ParseView {

  private static final String[] VENDOR_TAG_MODES = { "from config", "from parsed column", "parsed from filename" };

  private final JPanel parent;

  private final JTextField headerTag;

  private final JTextField mtypeTag;

  private final JTextField rowTag;

  private final JComboBox vendorTagMode;

  private final JLabel h_vendorTag;
  
  private final JTextField vendorTag;

  private final JTextField timeColumn;

  public XMLParseView(Properties p, JPanel par) {

    this.parent = par;
    
    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2, 2, 2, 2);
    c.weightx = 0;
    c.weighty = 0;

    parent.setLayout(new GridBagLayout());

    //

    headerTag = new JTextField(p.getProperty("headerTag", ""), 20);
    headerTag.setToolTipText("Tag delimiting header part of source file.");

    c.fill = GridBagConstraints.NONE;
    JLabel l_headerTag = new JLabel("Header tag");
    l_headerTag.setToolTipText("Tag delimiting header part of source file.");
    parent.add(l_headerTag, c);

    c.gridx = 1;
    parent.add(headerTag, c);

    //

    mtypeTag = new JTextField(p.getProperty("mtypeTag", ""), 20);
    mtypeTag.setToolTipText("Tag delimiting a measurement type from source file.");

    c.gridy = 1;
    c.gridx = 0;
    JLabel l_mtypeTag = new JLabel("Measurement type tag");
    l_mtypeTag.setToolTipText("Tag delimiting a measurement type from source file.");
    parent.add(l_mtypeTag, c);

    c.gridx = 1;
    parent.add(mtypeTag, c);

    //

    rowTag = new JTextField(p.getProperty("rowTag", ""), 20);
    rowTag.setToolTipText("Tag delimiting a row of a measurement from source file.");
    
    c.gridy = 3;
    c.gridx = 0;
    JLabel l_rowTag = new JLabel("Measurement row tag");
    l_rowTag.setToolTipText("Tag delimiting a row of a measurement from source file.");
    parent.add(l_rowTag, c);

    c.gridx = 1;
    parent.add(rowTag, c);

    //

    vendorTagMode = new JComboBox(VENDOR_TAG_MODES);
    vendorTagMode.setToolTipText("The method how vendor tag is determined from source file.");
    int di_ix = 0;
    try {
      di_ix = Integer.parseInt(p.getProperty("vendorTagMode", ""));
    } catch (Exception e) {
    }
    vendorTagMode.setSelectedIndex(di_ix);
    vendorTagMode.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent ae) {
        if (vendorTagMode.getSelectedIndex() == 0) {
          h_vendorTag.setText("Vendor tag");
        } else if (vendorTagMode.getSelectedIndex() == 1) {
          h_vendorTag.setText("Vendor tag column name");
        } else if (vendorTagMode.getSelectedIndex() == 2) {
          h_vendorTag.setText("Vendor parse pattern");
        }
        parent.invalidate();
        parent.revalidate();
        parent.repaint();
      }
    });

    c.gridy = 4;
    c.gridx = 0;
    
    JLabel l_vendorTagMode = new JLabel("Vendor tag mode");
    l_vendorTagMode.setToolTipText("The method how vendor tag is determined from source file.");
    parent.add(l_vendorTagMode, c);
    c.gridx = 1;
    parent.add(vendorTagMode, c);

    //

    vendorTag = new JTextField(p.getProperty("vendorTag", ""),50);

    h_vendorTag = new JLabel("Header row");

    if (vendorTagMode.getSelectedIndex() == 0) {
      h_vendorTag.setText("Vendor tag");
    } else if (vendorTagMode.getSelectedIndex() == 1) {
      h_vendorTag.setText("Vendor tag column name");
    } else if (vendorTagMode.getSelectedIndex() == 2) {
      h_vendorTag.setText("Vendor parse pattern");
    }

    c.gridy = 5;
    c.gridx = 0;
    parent.add(h_vendorTag, c);

    c.gridx = 1;
    parent.add(vendorTag, c);

    //

    timeColumn = new JTextField(p.getProperty("timeColumn", ""),20);
    timeColumn.setToolTipText("Column name used as datatime.");

    c.gridy = 7;
    c.gridx = 0;
    JLabel l_timeColumn = new JLabel("Time column");
    l_timeColumn.setToolTipText("Column name used as datatime.");
    parent.add(l_timeColumn, c);

    c.gridx = 1;
    parent.add(timeColumn, c);

    //
    
    c.gridx = 2;
    c.gridy = 9;
    c.weightx = 1;
    c.weighty = 1;
    c.fill = GridBagConstraints.BOTH;
    parent.add(Box.createRigidArea(new Dimension(1,1)),c);

  }

  /**
   * Returns a set of parameter names that ParseView represents.
   * 
   * @return Set of parameter names
   */
  public Set getParameterNames() {
    final Set s = new HashSet();

    s.add("headerTag");
    s.add("mtypeTag");
    s.add("rowTag");
    s.add("vendorTagMode");
    s.add("vendorTag");
    s.add("timeColumn");

    return s;
  }
  
  public String validate() {
    String error = "";
    if(mtypeTag.getText().trim().length() <= 0) {
      error += "Parameter Measurement type tag must be defined\n";
    }
    if(rowTag.getText().trim().length() <= 0) {
      error += "Parameter Measurement row tag must be defined\n";
    }
    
    return error;
  }

  /**
   * Sets ParseView specific parameters to a properties object.
   * 
   * @param p
   *          Properties object
   */
  public void fillParameters(final Properties p) {
    p.setProperty("headerTag",headerTag.getText());
    p.setProperty("mtypeTag",mtypeTag.getText());
    p.setProperty("rowTag",rowTag.getText());
    p.setProperty("vendorTagMode", String.valueOf(vendorTagMode.getSelectedIndex()));
    p.setProperty("vendorTag", vendorTag.getText());
    p.setProperty("timeColumn",timeColumn.getText());
  }


}
