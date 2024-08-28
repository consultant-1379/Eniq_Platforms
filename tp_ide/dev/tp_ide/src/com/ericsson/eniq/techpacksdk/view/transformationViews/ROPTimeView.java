package com.ericsson.eniq.techpacksdk.view.transformationViews;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.distocraft.dc5000.repository.dwhrep.Transformation;

/**
 *
 * @author eromsza
 *
 */
public class ROPTimeView extends GenericTransformationView {

    private final JTextField ropTF;

    private final JTextField timezoneFixedTF;

    private final JTextField timezoneFieldTF;

    private final JTextField deltaTF;

    public ROPTimeView(final JPanel parent, final Transformation transformation) {
        super();
        setTransformation(transformation);
        parent.removeAll();

        final GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(2, 2, 2, 2);
        c.weightx = 1;
        c.weighty = 1;

        // ROP field
        String name = "rop";
        String toolTip = "ROP in minutes to round up";
        c.gridx = 0;
        c.gridy = 0;
        JLabel label = new JLabel(name);
        label.setToolTipText(toolTip);
        parent.add(label, c);
        c.gridx = 1;
        c.gridy = 0;
        ropTF = new JTextField(4);
        ropTF.setName(name);
        ropTF.setToolTipText(toolTip);
        ropTF.setText(getOrigValue(name));
        textFieldContainer.add(ropTF);
        parent.add(ropTF, c);

        // timezone fixed
        name = "timezoneFixed";
        toolTip = "Timezone used (uses current timezone of host if not defined or is incorrect)";
        c.gridx = 0;
        c.gridy++;
        label = new JLabel(name);
        label.setToolTipText(toolTip);
        parent.add(label, c);
        c.gridx = 1;
        timezoneFixedTF = new JTextField(25);
        timezoneFixedTF.setName(name);
        timezoneFixedTF.setToolTipText(toolTip);
        timezoneFixedTF.setText(getOrigValue(name));
        textFieldContainer.add(timezoneFixedTF);
        parent.add(timezoneFixedTF, c);

        // timezone field
        name = "timezoneField";
        toolTip = "Timezone from field used (uses current timezone of host if not defined in timezoneFixed or is incorrect)";
        c.gridx = 0;
        c.gridy++;
        label = new JLabel(name);
        label.setToolTipText(toolTip);
        parent.add(label, c);
        c.gridx = 1;
        timezoneFieldTF = new JTextField(25);
        timezoneFieldTF.setName(name);
        timezoneFieldTF.setToolTipText(toolTip);
        timezoneFieldTF.setText(getOrigValue(name));
        textFieldContainer.add(timezoneFieldTF);
        parent.add(timezoneFieldTF, c);

        // ROP Start Time Delta in minutes
        name = "delta";
        toolTip = "Use ROP start time Delta in minutes (0 by default).";
        c.gridx = 0;
        c.gridy++;
        label = new JLabel(name);
        label.setToolTipText(toolTip);
        parent.add(label, c);
        c.gridx = 1;
        deltaTF = new JTextField(11);
        deltaTF.setName(name);
        deltaTF.setToolTipText(toolTip);
        deltaTF.setText(getOrigValue(name));
        textFieldContainer.add(deltaTF);
        parent.add(deltaTF, c);

        parent.invalidate();
        parent.revalidate();
        parent.repaint();
    }

    /**
     *
     * @return
     */
    @Override
    public String validate() {
        try {
            if (Integer.parseInt(ropTF.getText()) > 1440) {
                return "ROP must not exceed a day in minutes.\n";
            } else if (Integer.parseInt(ropTF.getText()) < 1) {
                return "ROP must be greater than zero.\n";
            }

            if (!timezoneFixedTF.getText().isEmpty() && !timezoneFieldTF.getText().isEmpty()) {
                return "Time Zone must be either fixed or specified in the field (not both).\n";
            }

            if (Integer.parseInt(deltaTF.getText()) > Integer.MAX_VALUE
                    || Integer.parseInt(deltaTF.getText()) < Integer.MIN_VALUE) {
                return "Offset must be within 32-bit Integer range.\n";
            }
        } catch (NumberFormatException nfe) {
            return "ROP and Offset must be a decimal number.\n";
        }

        return "";
    }

}
