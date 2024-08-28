package com.ericsson.eniq.techpacksdk.view.transformationViews;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.distocraft.dc5000.repository.dwhrep.Transformation;

/**
 *
 * @author eromsza
 *
 */
public class CalculationView extends GenericTransformationView {

    private JTextField formulaTF = null;

    private JTextField varformulaTF = null;

    private JCheckBox useFloat = null;

    private JTextField useFloatTF = null;

    //private JTextField oneFromGroup = null;

    public CalculationView(JPanel parent, Transformation transformation) {
        super();
        setTransformation(transformation);
        parent.removeAll();

        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(2, 2, 2, 2);
        c.weightx = 1;
        c.weighty = 1;

        // formula field
        String name = "formula";
        String toolTip = "Used calculation formula with a constant. For example -60 or 60/";
        c.gridx = 0;
        c.gridy = 0;
        JLabel label = new JLabel(name);
        label.setToolTipText(toolTip);
        parent.add(label, c);
        c.gridx = 1;
        c.gridy = 0;
        formulaTF = new JTextField(25);
        formulaTF.setName(name);
        formulaTF.setToolTipText(toolTip);
        formulaTF.setText(getOrigValue(name));
        textFieldContainer.add(formulaTF);
        parent.add(formulaTF, c);

        // varformula field
        name = "varformula";
        toolTip = "Used calculation formula with variable name. For example -VAR_NAME or VAR_NAME/";
        c.gridx = 0;
        c.gridy = 1;
        label = new JLabel(name);
        label.setToolTipText(toolTip);
        parent.add(label, c);
        c.gridx = 1;
        c.gridy = 1;
        varformulaTF = new JTextField(25);
        varformulaTF.setName(name);
        varformulaTF.setToolTipText(toolTip);
        varformulaTF.setText(getOrigValue(name));
        textFieldContainer.add(varformulaTF);
        parent.add(varformulaTF, c);

        // usefloat checkbox
        name = "usefloat";
        toolTip = "Use floating point calculation (false by default).";
        c.gridx = 0;
        c.gridy = 2;
        label = new JLabel(name);
        label.setToolTipText(toolTip);
        parent.add(label, c);
        c.gridx = 1;
        c.gridy = 2;
        // usefloat field containing the checkbox status ("true" = ticked, "false" = unticked), default is false
        useFloatTF = new JTextField(5);
        useFloatTF.setName(name);
        useFloatTF.setToolTipText(toolTip);
        useFloatTF.setText(getOrigValue(name));
        textFieldContainer.add(useFloatTF);
        // create a checkbox as a source of "true"/"false" values in JTextField
        useFloat = new JCheckBox();
        useFloat.setSelected("true".equalsIgnoreCase(getOrigValue(name)));
        useFloat.setFocusable(false);
        parent.add(useFloat, c);
        // add a listener for manipulating with the ticked and the unticked states
        useFloat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                JCheckBox checkBox = (JCheckBox) event.getSource();
                if (checkBox.isSelected()) {
                    useFloatTF.setText("true");
                } else {
                    useFloatTF.setText("false");
                }
            }
        });

        parent.invalidate();
        parent.revalidate();
        parent.repaint();
    }

    /**
     *
     * @return
     */
    public String validate() {
        int filled = 0;

        if (!formulaTF.getText().equals(""))
            filled++;

        if (!varformulaTF.getText().equals(""))
            filled++;

        if (filled != 1)
            return "One and only one must be filled (formula or varformula).\n";

        return "";
    }

}