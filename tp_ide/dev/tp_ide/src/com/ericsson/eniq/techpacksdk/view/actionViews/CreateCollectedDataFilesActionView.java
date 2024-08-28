/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.techpacksdk.view.actionViews;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.ericsson.eniq.techpacksdk.common.Constants;

/**
 * Creates the action view that will be visible in the techpackIDE. This allows the creation of a set that will create
 * files to load into the tables, LOG_SESSION_COLLECTED_DATA. See CreateCollectedDataFilesAction.
 * 
 * @author epaujor
 * 
 */
public class CreateCollectedDataFilesActionView implements ActionView {

  private transient final JTextArea velocityTemplate;

  /**
   * Creates this action view will all the relevant JTextArea, JLabel, etc.
   * 
   * @param parent
   * @param action
   */
  public CreateCollectedDataFilesActionView(final JPanel parent, final Meta_transfer_actions action) {

    parent.removeAll();

    final JLabel velocityTemplateLabel = new JLabel("Velocity Template");
    velocityTemplateLabel.setToolTipText("Used for creating files to be loaded into LOG_COLLECTED_DATA table.");

    final GridBagConstraints gridbag = new GridBagConstraints();
    gridbag.anchor = GridBagConstraints.NORTHWEST;
    gridbag.fill = GridBagConstraints.NONE;
    gridbag.insets = new Insets(2, 2, 2, 2);
    gridbag.weightx = 0;
    gridbag.weighty = 0;
    parent.add(velocityTemplateLabel, gridbag);

    velocityTemplate = new JTextArea(20, 20);
    velocityTemplate.setName("Velocity Template");
    velocityTemplate.setLineWrap(true);
    velocityTemplate.setWrapStyleWord(true);
    final JScrollPane scrollPane = new JScrollPane(velocityTemplate, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    gridbag.weightx = 1;
    gridbag.gridx = 1;
    gridbag.fill = GridBagConstraints.BOTH;
    gridbag.gridy++;

    parent.add(scrollPane, gridbag);

    if (action != null) {

      final String temp = action.getAction_contents();

      if (temp != null) {
        velocityTemplate.setText(temp);
      }
    }

    parent.invalidate();
    parent.revalidate();
    parent.repaint();
  }

  public String getType() {
    return Constants.CREATE_COLLECTED_DATA_FILES;
  }

  public String validate() {
    final StringBuffer ret = new StringBuffer(80);

    if (velocityTemplate.getText().trim().length() <= 0) {
      ret.append("Parameter velocity Template must be defined\n");
    }

    return ret.toString();
  }

  public String getContent() {
    return velocityTemplate.getText().trim();
  }

  public String getWhere() {
    return "";
  }

  public boolean isChanged() {
    return true;
  }
}
