package com.distocraft.dc5000.etl.gui.set.actionview.parseview;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Xml3GPP32435ParseView implements ParseView {

  private static final String[] VENDOR_ID_FROM = { "data", "file"};
  
  private static final String[] MOID_STYLE = { "inc", "static"};

  private static final String[] BOOLEAN_MODES = { "true", "false" };

  private final JComboBox vendorIDFrom;

  private final JTextField vendorIDMask;

  private final JComboBox fillEmptyMOID;
    
  private final JComboBox fillEmptyMOIDStyle;
  
  private final JTextField fillEmptyMOIDValue;

  public Xml3GPP32435ParseView(final Properties p, final JPanel par, final JDialog parentDialog) {

    final GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2, 2, 2, 2);
    c.weightx = 0;
    c.weighty = 0;

    par.setLayout(new GridBagLayout());
    
    //
    
    vendorIDFrom = new JComboBox(VENDOR_ID_FROM);
    vendorIDFrom.setToolTipText("Defines the method for determining the vendorID.");
    int iv = 0;
    try {
      iv = getIndex(VENDOR_ID_FROM, p.getProperty("x3GPPParser.readVendorIDFrom", "data"), 0);
    } catch (Exception e) {
    }
    vendorIDFrom.setSelectedIndex(iv);
    /*vendorIDFrom.addActionListener(new ActionListener() {

      public void actionPerformed(final ActionEvent ae) {
       //refreshActives();
      }
    });*/
    c.gridx = 0;
    c.gridy = 0;
    c.fill = GridBagConstraints.NONE;
    final JLabel jl1 = new JLabel("VendorID from");
    jl1.setToolTipText("Defines the method for determining the vendorID.");
    par.add(jl1, c);

    c.gridx = 1;
    par.add(vendorIDFrom, c);
    
    //

    vendorIDMask = new JTextField(p.getProperty("x3GPPParser.vendorIDMask", ".+,(.+)=.+"), 15);
    vendorIDMask.setToolTipText("Regular expression pattern for parsing VendorID.");

    
    c.gridx = 0;
    c.gridy = 1;
    c.fill = GridBagConstraints.NONE;
    final JLabel jl2 = new JLabel("VendorID Mask");
    jl2.setToolTipText("Regular expression pattern for parsing VendorID.");
    par.add(jl2, c);

    c.gridx = 1;
    par.add(vendorIDMask, c);
    
    fillEmptyMOID = new JComboBox(BOOLEAN_MODES);
    fillEmptyMOID.setToolTipText("Defines whether empty MOIDs are filled or not");
    if ("true".equalsIgnoreCase(p.getProperty("x3GPPParser.FillEmptyMOID", ""))) {
      fillEmptyMOID.setSelectedIndex(0);
    } else {
      fillEmptyMOID.setSelectedIndex(1);
    }
    /*fillEmptyMOID.addActionListener(new ActionListener() {

      public void actionPerformed(final ActionEvent ae) {
       //refreshActives();
      }
    });*/

    c.gridy = 2;
    c.gridx = 0;
    c.fill = GridBagConstraints.NONE;
    final JLabel jl3 = new JLabel("Fill empty MOIDs");
    jl3.setToolTipText("Defines whether empty MOIDs are filled or not");
    par.add(jl3, c);
    
    c.gridx = 1;
    par.add(fillEmptyMOID, c);
    
    //
    fillEmptyMOIDStyle = new JComboBox(MOID_STYLE);
    fillEmptyMOIDStyle.setToolTipText("Defines the method for determining the vendorID.");
    iv = 0;
    try {
      iv = getIndex(MOID_STYLE, p.getProperty("x3GPPParser.FillEmptyMOIDStyle", "inc"), 0);
    } catch (Exception e) {
    }
    fillEmptyMOIDStyle.setSelectedIndex(iv);
    /*fillEmptyMOIDStyle.addActionListener(new ActionListener() {

      public void actionPerformed(final ActionEvent ae) {
       //refreshActives();
      }
    });*/

    c.gridy = 3;
    c.gridx = 0;
    c.fill = GridBagConstraints.NONE;
    final JLabel jl4 = new JLabel("Empty MOID style");
    jl4.setToolTipText("Defines the filling style of empty MOID.");
    par.add(jl4, c);

    c.gridx = 1;
    par.add(fillEmptyMOIDStyle, c);
    
    fillEmptyMOIDValue = new JTextField(p.getProperty("x3GPPParser.FillEmptyMOIDValue", "0"), 15);
    fillEmptyMOIDValue.setToolTipText("The value for empty MOID");

    c.gridy = 4;
    c.gridx = 0;
    c.fill = GridBagConstraints.NONE;
    final JLabel jl5 = new JLabel("Empty MOID value");
    jl5.setToolTipText("Defines the empty MOID value");
    par.add(jl5, c);

    c.gridx = 1;
    par.add(fillEmptyMOIDValue, c);
    
    // Finishing touch for the ui.

    c.gridx = 2;
    c.gridy = 5;
    c.weightx = 1;
    c.weighty = 1;
    c.fill = GridBagConstraints.BOTH;
    par.add(Box.createRigidArea(new Dimension(1, 1)), c);
    //refreshActives();
        
  }

  /*private void refreshActives() {

    if(vendorIDFrom.getSelectedIndex() == 2) {
      sgsnEmptyMOIDVendorID.setEnabled(true);
      lsgsnEmptyMOIDVendorID.setEnabled(true);
      
      sgsnVendorIDPatterns.setEnabled(true);
      lsgsnVendorIDPatterns.setEnabled(true);
    } else {
      sgsnEmptyMOIDVendorID.setEnabled(false);
      lsgsnEmptyMOIDVendorID.setEnabled(false);
      
      sgsnVendorIDPatterns.setEnabled(false);
      lsgsnVendorIDPatterns.setEnabled(false);
    }

  }*/

  /**
   * Returns a set of parameter names that ParseView represents. Implements
   * ParseView.
   * 
   * @return Set of parameter names
   */
  public Set getParameterNames() {
    final Set s = new HashSet();

    s.add("x3GPPParser.vendorIDMask");
    s.add("x3GPPParser.readVendorIDFrom");
    s.add("x3GPPParser.FillEmptyMOID");
    s.add("x3GPPParser.FillEmptyMOIDStyle");
    s.add("x3GPPParser.FillEmptyMOIDValue");
    return s;
  }

  /**
   * Sets ParseView specific parameters to a properties object. Implements
   * ParseView.
   * 
   * @param p
   *          Properties object
   */
  public void fillParameters(final Properties p) {
    p.setProperty("x3GPPParser.vendorIDMask", vendorIDMask.getText());
    p.setProperty("x3GPPParser.readVendorIDFrom", getString(VENDOR_ID_FROM, vendorIDFrom.getSelectedIndex()));
    p.setProperty("x3GPPParser.FillEmptyMOID", getString(BOOLEAN_MODES, fillEmptyMOID.getSelectedIndex()));
    p.setProperty("x3GPPParser.FillEmptyMOIDStyle", getString(MOID_STYLE, fillEmptyMOIDStyle.getSelectedIndex()));
    p.setProperty("x3GPPParser.FillEmptyMOIDValue", fillEmptyMOIDValue.getText());    
  }

  /**
   * Implements ParseView
   */
  public String validate() {

    try {
      Pattern.compile(vendorIDMask.getText());
    } catch (PatternSyntaxException pse) {
      return "VendorID Mask: Not a valid regExp pattern\n";
    }
    return "";
  }

  private int getIndex(final String[] strA, final String str, final int defi) {

    for (int i = 0; i < strA.length; i++) {
      final String tmp = strA[i];
      if (tmp.equalsIgnoreCase(str)) {
        return i;
      }
    }

    return defi;
  }

  private String getString(final String[] strA, final int ind) {

    try {

      return strA[ind];

    } catch (Exception e) {

    }

    return "";
  }

}
