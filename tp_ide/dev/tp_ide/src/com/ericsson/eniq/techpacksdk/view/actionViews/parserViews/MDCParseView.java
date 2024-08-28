package com.ericsson.eniq.techpacksdk.view.actionViews.parserViews;

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

import com.ericsson.eniq.techpacksdk.view.actionViews.GenericPropertiesView;


public class MDCParseView implements ParseView {

  private static final String[] VENDOR_ID_FROM = { "data", "file", "sgsn" };

  private static final String[] BOOLEAN_MODES = { "true", "false" };
  
  private static final String[] OUTPUT_FROMAT_MODES_TEXT = {"ascii", "binary"};

  private final JComboBox vendorIDFrom;

  private final JTextField vendorIDMask;

  private final JLabel lsgsnEmptyMOIDVendorID;
  
  private final JTextField sgsnEmptyMOIDVendorID;
  
  private final JLabel lsgsnVendorIDPatterns;

  private final GenericPropertiesView sgsnVendorIDPatterns;

  private final JComboBox useVector;
  
  private JComboBox outputFormat = null;
  
  private String techpackName;

  public MDCParseView(final Properties p, final JPanel panel, final JDialog parentDialog, final String techpackName) {
	this.techpackName = techpackName;
    final GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2, 2, 2, 2);
    c.weightx = 0;
    c.weighty = 0;
    panel.setLayout(new GridBagLayout());
    c.gridy = -1;
    
    // Output Format (Binary Flag)
    // 20100114, eeoidiv, WRAN Counter Capicity IP: 263/159 41-FCP 103 8147
    
    //20111012, eanguan, For showing the output format for all the mdc parsers :: TR HO87172
//    if(isWranInterface(this.techpackName)) {
    	outputFormat = new JComboBox(OUTPUT_FROMAT_MODES_TEXT);
	    int outputFormatIndex = 0;
	    try {
	    	// Show empty, if no existing value.
	    	outputFormatIndex = Integer.parseInt(p.getProperty("MDCParser.outputFormat", "0"));
	    } catch (Exception e) {
	    }
	    outputFormat.setSelectedIndex(outputFormatIndex);
	    outputFormat.setToolTipText("What sort of data is being parsed.");
	    c.gridy++;
	    c.gridx = 0;
	    final JLabel jl6 = new JLabel("Output Format");
	    jl6.setToolTipText("Format of output data. Determines the format type the parser expects to be used.");
	    panel.add(jl6, c);
	    c.gridx = 1;
	    panel.add(outputFormat , c);
//	} // end if(isWranInterface) 
    
    // vendorID 
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
    c.gridy++;
    c.gridx = 0;
    final JLabel jl1 = new JLabel("VendorID from");
    jl1.setToolTipText("Defines the method for determining the vendorID.");
    panel.add(jl1, c);
    c.gridx = 1;
    panel.add(vendorIDFrom, c);
    
    // VendorID Mask
    vendorIDMask = new JTextField(p.getProperty("MDCParser.vendorIDMask", ""), 35);
    vendorIDMask.setToolTipText("Regular expression pattern for parsing VendorID.");
    c.gridy++;
    c.gridx = 0;
    final JLabel jl2 = new JLabel("VendorID Mask");
    jl2.setToolTipText("Regular expression pattern for parsing VendorID.");
    panel.add(jl2, c);
    c.gridx = 1;
    panel.add(vendorIDMask, c);
    
    // VendorID for empty MOID
    sgsnEmptyMOIDVendorID = new JTextField(p.getProperty("MDCParser.emptyMOIDVendorID", ""), 35);
    sgsnEmptyMOIDVendorID.setToolTipText("VendorID used if MOID is empty in sgsn data.");
    c.gridy++;
    c.gridx = 0;
    lsgsnEmptyMOIDVendorID = new JLabel("VendorID for empty MOID");
    lsgsnEmptyMOIDVendorID.setToolTipText("VendorID used if MOID is empty in sgsn data.");
    panel.add(lsgsnEmptyMOIDVendorID, c);
    c.gridx = 1;
    panel.add(sgsnEmptyMOIDVendorID, c);
    
    // VendorID Patterns
    final Properties gpp = new Properties();
    final Iterator i = p.keySet().iterator();
    while(i.hasNext()) {
      final String key = (String)i.next();
      
      if(key != null && key.startsWith("MDCParser.VendorIDPattern.")) {
        gpp.put(key.substring(26), p.getProperty(key));
      } 
    }
    sgsnVendorIDPatterns = new GenericPropertiesView(gpp,new HashSet(), parentDialog);
    c.gridy++;
    c.gridx = 0;
    lsgsnVendorIDPatterns = new JLabel("VendorID Patterns");
    //jl4.setToolTipText("");
    panel.add(lsgsnVendorIDPatterns, c);
    c.gridx = 1;
    panel.add(sgsnVendorIDPatterns, c);
    
    //Use Vector Data
    useVector = new JComboBox(BOOLEAN_MODES);
    int ib = 0;
    try {
      ib = getIndex(BOOLEAN_MODES, p.getProperty("MDCParser.UseVector", "false"), 0);
    } catch (Exception e) {
    }
    useVector.setSelectedIndex(ib);
    useVector.setToolTipText("Is the vector data parsed from the source.");
    c.gridy++;
    c.gridx = 0;
    final JLabel jl5 = new JLabel("Use Vector Data");
    jl5.setToolTipText("Regular expression pattern for parsing VendorID.");
    panel.add(jl5, c);
    c.gridx = 1;
    panel.add(useVector, c);
    
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
    s.add("MDCParser.outputFormat");
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
	if((outputFormat.getSelectedIndex() > -1) ) {
		p.setProperty("MDCParser.outputFormat", Integer.toString(outputFormat.getSelectedIndex()));
	} else {
		// Clear the property, for the parser
		p.setProperty("MDCParser.outputFormat", "");
	}
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
    
  } // fillParameters

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
  
  /**
   * WRAN if RNC/RBS/RXI
   * //20111012, eanguan, This function is not useful :: TR HO87172
   * @return
   */
//  private boolean isWranInterface(final String name) {
//	boolean result = false;
//	if( (name != null) && (name.endsWith("_E_RNC") || name.endsWith("_E_RBS") || name.endsWith("_E_RXI")) ) {
//		result = true;
//	}
//	return result;
//  }
}
