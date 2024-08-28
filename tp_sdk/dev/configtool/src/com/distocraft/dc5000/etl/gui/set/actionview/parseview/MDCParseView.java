package com.distocraft.dc5000.etl.gui.set.actionview.parseview;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.distocraft.dc5000.etl.gui.set.actionview.GenericPropertiesView;

public class MDCParseView implements ParseView {

  private static final String[] VENDOR_ID_FROM = { "data", "file", "sgsn" };

  private static final String[] BOOLEAN_MODES = { "true", "false" };

  private final JComboBox vendorIDFrom;

  private final JTextField vendorIDMask;

  private final JLabel lsgsnEmptyMOIDVendorID;
  
  private final JTextField sgsnEmptyMOIDVendorID;
  
  private final JLabel lsgsnVendorIDPatterns;

  private final GenericPropertiesView sgsnVendorIDPatterns;

  private final JComboBox useVector;

  public MDCParseView(final Properties p, final JPanel par, final JDialog parentDialog) {

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
      iv = getIndex(VENDOR_ID_FROM, p.getProperty("MDCParser.readVendorIDFrom", "data"), 0);
    } catch (Exception e) {
    }
    vendorIDFrom.setSelectedIndex(iv);
    vendorIDFrom.addActionListener(new ActionListener() {

      public void actionPerformed(final ActionEvent ae) {
       refreshActives();
      }
    });
    
    c.fill = GridBagConstraints.NONE;
    final JLabel jl1 = new JLabel("VendorID from");
    jl1.setToolTipText("Defines the method for determining the vendorID.");
    par.add(jl1, c);

    c.gridx = 1;
    par.add(vendorIDFrom, c);
    
    //

    vendorIDMask = new JTextField(p.getProperty("MDCParser.vendorIDMask", ""), 35);
    vendorIDMask.setToolTipText("Regular expression pattern for parsing VendorID.");
    
    c.gridy = 1;
    c.gridx = 0;
    final JLabel jl2 = new JLabel("VendorID Mask");
    jl2.setToolTipText("Regular expression pattern for parsing VendorID.");
    par.add(jl2, c);

    c.gridx = 1;
    par.add(vendorIDMask, c);
    
    //
    
    sgsnEmptyMOIDVendorID = new JTextField(p.getProperty("MDCParser.emptyMOIDVendorID", ""), 35);
    sgsnEmptyMOIDVendorID.setToolTipText("VendorID used if MOID is empty in sgsn data.");
    
    c.gridy = 2;
    c.gridx = 0;
    lsgsnEmptyMOIDVendorID = new JLabel("VendorID for empty MOID");
    lsgsnEmptyMOIDVendorID.setToolTipText("VendorID used if MOID is empty in sgsn data.");
    par.add(lsgsnEmptyMOIDVendorID, c);

    c.gridx = 1;
    par.add(sgsnEmptyMOIDVendorID, c);
    
    //
    
    final Properties gpp = new Properties();
    final Iterator i = p.keySet().iterator();
    while(i.hasNext()) {
      final String key = (String)i.next();
      
      if(key != null && key.startsWith("MDCParser.VendorIDPattern.")) {
        gpp.put(key.substring(26), p.getProperty(key));
      } 
    }
    
    sgsnVendorIDPatterns = new GenericPropertiesView(gpp,new HashSet(), parentDialog);
    
    c.gridy = 3;
    c.gridx = 0;
    lsgsnVendorIDPatterns = new JLabel("VendorID Patterns");
    //jl4.setToolTipText("");
    par.add(lsgsnVendorIDPatterns, c);

    c.gridx = 1;
    par.add(sgsnVendorIDPatterns, c);
    
    //
    
    useVector = new JComboBox(BOOLEAN_MODES);
    int ib = 0;
    try {
      ib = getIndex(BOOLEAN_MODES, p.getProperty("MDCParser.UseVector", "false"), 0);
    } catch (Exception e) {
    }

    useVector.setSelectedIndex(ib);
    useVector.setToolTipText("Is the vector data parsed from the source.");
    
    c.gridy = 4;
    c.gridx = 0;
    final JLabel jl5 = new JLabel("Use Vector Data");
    jl5.setToolTipText("Regular expression pattern for parsing VendorID.");
    par.add(jl5, c);

    c.gridx = 1;
    par.add(useVector, c);
    
    refreshActives();
        
  }

  private void refreshActives() {

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

  }

  /**
   * Returns a set of parameter names that ParseView represents. Implements
   * ParseView.
   * 
   * @return Set of parameter names
   */
  public Set getParameterNames() {
    final Set s = new HashSet();

    s.add("MDCParser.readVendorIDFrom");
    s.add("MDCParser.dtdfile");
    s.add("MDCParser.vendorIDMask");
    s.add("MDCParser.UseVector");
    s.add("MDCParser.emptyMOIDVendorID");
    s.add("MDCParser.VendorIDPattern.*");
    s.add("MDCParser.VendorPatterns");

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
    p.setProperty("MDCParser.readVendorIDFrom", getString(VENDOR_ID_FROM, vendorIDFrom.getSelectedIndex()));
    p.setProperty("MDCParser.vendorIDMask", vendorIDMask.getText());
    p.setProperty("MDCParser.UseVector", getString(BOOLEAN_MODES, useVector.getSelectedIndex()));
    p.setProperty("MDCParser.emptyMOIDVendorID", sgsnEmptyMOIDVendorID.getText());
    
    final Properties x = sgsnVendorIDPatterns.getProperties();
    final Iterator i = x.keySet().iterator();
    final StringBuffer sb = new StringBuffer("");
    while(i.hasNext()) {
      final String key = (String)i.next();
      p.put("MDCParser.VendorIDPattern." + key, x.get(key));
      sb.append(key);
      if(i.hasNext()) {
        sb.append(",");
      }
    }
    p.setProperty("MDCParser.VendorPatterns", sb.toString());
    
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
