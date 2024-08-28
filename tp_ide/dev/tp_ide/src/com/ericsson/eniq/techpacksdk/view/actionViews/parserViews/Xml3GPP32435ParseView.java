package com.ericsson.eniq.techpacksdk.view.actionViews.parserViews;

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

	private static final String[] VENDOR_ID_FROM = { "data", "file" };

	private static final String[] MOID_STYLE = { "inc", "static" };

	private static final String[] BOOLEAN_MODES = { "true", "false" };

	private final JComboBox vendorIDFrom;

	private final JTextField vendorIDMask;

	private final JComboBox fillEmptyMOID;

	private final JComboBox fillEmptyMOIDStyle;

	private final JTextField fillEmptyMOIDValue;

	private JComboBox outputFormat = null;
	
	private final JComboBox useVector;

	private static final String[] OUTPUT_FROMAT_MODES_TEXT = { "ascii", "binary" };

	public Xml3GPP32435ParseView(final Properties p, final JPanel par, final JDialog parentDialog) {

		final GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(2, 2, 2, 2);
		c.weightx = 0;
		c.weighty = 0;

		par.setLayout(new GridBagLayout());
		c.gridy = -1;
		// Output Format
		outputFormat = new JComboBox(OUTPUT_FROMAT_MODES_TEXT);
		int outputFormatIndex = 0;
		try {
			// Show empty, if no existing value.
			outputFormatIndex = Integer.parseInt(p.getProperty("x3GPPParser.outputFormat", "0"));
		} catch (final Exception e) {
		}
		outputFormat.setSelectedIndex(outputFormatIndex);
		outputFormat.setToolTipText("What sort of data is being parsed.");
		c.gridy++;
		c.gridx = 0;
		final JLabel jl6 = new JLabel("Output Format");
		jl6.setToolTipText("Format of output data. Determines the format type the parser expects to be used.");
		par.add(jl6, c);
		c.gridx = 1;
		par.add(outputFormat, c);

		vendorIDFrom = new JComboBox(VENDOR_ID_FROM);
		vendorIDFrom.setToolTipText("Defines the method for determining the vendorID.");
		int iv = 0;
		try {
			iv = getIndex(VENDOR_ID_FROM, p.getProperty("x3GPPParser.readVendorIDFrom", "data"), 0);
		} catch (final Exception e) {
		}
		vendorIDFrom.setSelectedIndex(iv);
		/*
		 * vendorIDFrom.addActionListener(new ActionListener() {
		 * 
		 * public void actionPerformed(final ActionEvent ae) { //refreshActives(); } });
		 */
		// c.gridx = 0;
		// c.gridy = 0;
		c.gridy++;
		c.gridx = 0;
		c.fill = GridBagConstraints.NONE;
		final JLabel jl1 = new JLabel("VendorID from");
		jl1.setToolTipText("Defines the method for determining the vendorID.");
		par.add(jl1, c);
		c.gridx = 1;
		par.add(vendorIDFrom, c);

		// Vendor ID Mask

		vendorIDMask = new JTextField(p.getProperty("x3GPPParser.vendorIDMask", ".+,(.+)=.+"), 15);
		vendorIDMask.setToolTipText("Regular expression pattern for parsing VendorID.");

		c.gridx = 0;
		c.gridy++;
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
		/*
		 * fillEmptyMOID.addActionListener(new ActionListener() {
		 * 
		 * public void actionPerformed(final ActionEvent ae) { //refreshActives(); } });
		 */

		c.gridy++;
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
		} catch (final Exception e) {
		}
		fillEmptyMOIDStyle.setSelectedIndex(iv);
		/*
		 * fillEmptyMOIDStyle.addActionListener(new ActionListener() {
		 * 
		 * public void actionPerformed(final ActionEvent ae) { //refreshActives(); } });
		 */

		c.gridy++;
		c.gridx = 0;
		c.fill = GridBagConstraints.NONE;
		final JLabel jl4 = new JLabel("Empty MOID style");
		jl4.setToolTipText("Defines the filling style of empty MOID.");
		par.add(jl4, c);

		c.gridx = 1;
		par.add(fillEmptyMOIDStyle, c);

		fillEmptyMOIDValue = new JTextField(p.getProperty("x3GPPParser.FillEmptyMOIDValue", "0"), 15);
		fillEmptyMOIDValue.setToolTipText("The value for empty MOID");

		c.gridy++;
		c.gridx = 0;
		c.fill = GridBagConstraints.NONE;
		final JLabel jl5 = new JLabel("Empty MOID value");
		jl5.setToolTipText("Defines the empty MOID value");
		par.add(jl5, c);

		c.gridx = 1;
		par.add(fillEmptyMOIDValue, c);

		// Finishing touch for the ui.

		c.gridx = 2;
		c.gridy++;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		par.add(Box.createRigidArea(new Dimension(1, 1)), c);
		
		// Use Vector Data
		// EQEV-4442 : To Support Vector data for 3GPP32435 Parser
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
	    final JLabel jVector = new JLabel("Use Vector Data");
	    jVector.setToolTipText("Regular expression pattern for parsing VendorID.");
	    par.add(jVector, c);
	    c.gridx = 1;
	    par.add(useVector, c);
		
		// refreshActives();

	}

	/*
	 * private void refreshActives() {
	 * 
	 * if(vendorIDFrom.getSelectedIndex() == 2) { sgsnEmptyMOIDVendorID.setEnabled(true);
	 * lsgsnEmptyMOIDVendorID.setEnabled(true);
	 * 
	 * sgsnVendorIDPatterns.setEnabled(true); lsgsnVendorIDPatterns.setEnabled(true); } else {
	 * sgsnEmptyMOIDVendorID.setEnabled(false); lsgsnEmptyMOIDVendorID.setEnabled(false);
	 * 
	 * sgsnVendorIDPatterns.setEnabled(false); lsgsnVendorIDPatterns.setEnabled(false); }
	 * 
	 * }
	 */

	/**
	 * Returns a set of parameter names that ParseView represents. Implements ParseView.
	 * 
	 * @return Set of parameter names
	 */
	@Override
	public Set getParameterNames() {
		final Set s = new HashSet();

		s.add("x3GPPParser.vendorIDMask");
		s.add("x3GPPParser.readVendorIDFrom");
		s.add("x3GPPParser.FillEmptyMOID");
		s.add("x3GPPParser.FillEmptyMOIDStyle");
		s.add("x3GPPParser.FillEmptyMOIDValue");
		s.add("x3GPPParser.outputFormat");
		return s;
	}

	/**
	 * Sets ParseView specific parameters to a properties object. Implements ParseView.
	 * 
	 * @param p
	 *            Properties object
	 */
	@Override
	public void fillParameters(final Properties p) {
		if ((outputFormat.getSelectedIndex() > -1)) {
			p.setProperty("x3GPPParser.outputFormat", Integer.toString(outputFormat.getSelectedIndex()));
		} else {
			// Clear the property, for the parser
			p.setProperty("x3GPPParser.outputFormat", "");
		}
		p.setProperty("x3GPPParser.vendorIDMask", vendorIDMask.getText());
		p.setProperty("x3GPPParser.readVendorIDFrom", getString(VENDOR_ID_FROM, vendorIDFrom.getSelectedIndex()));
		p.setProperty("x3GPPParser.FillEmptyMOID", getString(BOOLEAN_MODES, fillEmptyMOID.getSelectedIndex()));
		p.setProperty("x3GPPParser.FillEmptyMOIDStyle", getString(MOID_STYLE, fillEmptyMOIDStyle.getSelectedIndex()));
		p.setProperty("x3GPPParser.FillEmptyMOIDValue", fillEmptyMOIDValue.getText());
	}

	/**
	 * Implements ParseView
	 */
	@Override
	public String validate() {

		try {
			Pattern.compile(vendorIDMask.getText());
		} catch (final PatternSyntaxException pse) {
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

		} catch (final Exception e) {

		}

		return "";
	}

}
