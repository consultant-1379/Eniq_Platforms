/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2013
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.eniq.techpacksdk.view.actionViews;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.IOException;

import javax.swing.*;

import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.ericsson.eniq.techpacksdk.common.Constants;

public class GateKeeperPropertyActionView implements ActionView {

    private static final String EMPTY_STRING = "";
    private transient final JTextField template;

    public GateKeeperPropertyActionView(final JPanel parent, final Meta_transfer_actions action) {

        parent.removeAll();

        final GridBagConstraints gridbag = new GridBagConstraints();
        gridbag.anchor = GridBagConstraints.NORTHWEST;
        gridbag.fill = GridBagConstraints.NONE;
        gridbag.insets = new Insets(2, 2, 2, 2);
        gridbag.weightx = 0;
        gridbag.weighty = 0;

        final JLabel l_stype = new JLabel("Property Name");
        l_stype.setToolTipText("The name of the property.");
        parent.add(l_stype, gridbag);

        template = new JTextField(20);

        gridbag.weightx = 1;
        gridbag.gridx = 1;
        parent.add(template, gridbag);

        if (action != null) {

            final String temp = action.getAction_contents();

            if (temp != null) {
                template.setText(temp);
            }
        }

        parent.invalidate();
        parent.revalidate();
        parent.repaint();
    }

    @Override
    public String getType() {
        return Constants.GATE_KEEPER_PROPERTY;
    }

    @Override
    public String validate() {
        final StringBuffer ret = new StringBuffer(90);

        if (template.getText().trim().length() <= 0) {
            ret.append("Parameter Property Name must be defined\n");
        }

        return ret.toString();
    }

    @Override
    public String getContent() {
        return template.getText().trim();
    }

    @Override
    public String getWhere() throws IOException {
        return EMPTY_STRING;
    }

    @Override
    public boolean isChanged() {
        return true;
    }

}
