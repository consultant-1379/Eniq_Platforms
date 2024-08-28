/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.techpacksdk.view.actionViews;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.ericsson.eniq.techpacksdk.common.Constants;

/**
 * Creates the action view that will be visible in the techpackIDE. This allows a set to be created that will update the
 * COLLECTED column in the tables, LOG_SESSION_COLLECTED_DATA.
 * 
 * @author epaujor
 * 
 */
public class UpdateCollectedDataActionView implements ActionView {

  private transient static final Logger LOG = Logger.getLogger("UpdateCollectedDataActionView");
  private transient final JTextArea velocityTemplate;
  private transient final JTextField sourcetype;
  private transient final JTextField targettype;

  /**
   * Creates this action view will all the relevant JTextArea, JLabel, etc.
   * 
   * @param parent
   * @param action
   */
  public UpdateCollectedDataActionView(final JPanel parent, final Meta_transfer_actions action) {

    parent.removeAll();

    final JLabel velocityTemplateLabel = new JLabel("Velocity Template");
    velocityTemplateLabel.setToolTipText("Used for updating the COLLECTED column in LOG_SESSION_COLLECTED_DATA table.");

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

    final JLabel l_ttype = new JLabel("Source Type");
    l_ttype.setToolTipText("StorageID of source type.");

    gridbag.weightx = 0;
    gridbag.gridx = 0;
    gridbag.fill = GridBagConstraints.NONE;
    gridbag.gridy++;
    parent.add(l_ttype, gridbag);

    sourcetype = new JTextField(20);

    gridbag.weightx = 1;
    gridbag.gridx = 1;
    parent.add(sourcetype, gridbag);

    final JLabel l_stype = new JLabel("Target Type");
    l_stype.setToolTipText("Name of target type. This is catenated with : and level name to get storageID for type.");

    gridbag.weightx = 0;
    gridbag.gridx = 0;
    gridbag.gridy++;
    parent.add(l_stype, gridbag);

    targettype = new JTextField(20);

    gridbag.weightx = 1;
    gridbag.gridx = 1;
    parent.add(targettype, gridbag);

    if (action != null) {

      final String temp = action.getAction_contents();

      if (temp != null) {
        velocityTemplate.setText(temp);
      }

      final Properties prop = new Properties();

      final String str = action.getWhere_clause();

      try {

        if (str != null && str.length() > 0) {

          final ByteArrayInputStream bais = new ByteArrayInputStream(str.getBytes());
          prop.load(bais);
          bais.close();
        }

      } catch (Exception e) {
        LOG.log(Level.INFO, "Reading parameters failed", e);
      }

      targettype.setText(prop.getProperty("targetType", ""));
      sourcetype.setText(prop.getProperty("sourceType", ""));
    }

    parent.invalidate();
    parent.revalidate();
    parent.repaint();
  }

  @Override
  public String getType() {
    return Constants.UPDATE_COLLECTED_DATA_FILES;
  }

  @Override
  public String validate() {
    final StringBuffer ret = new StringBuffer(80);

    if (velocityTemplate.getText().trim().length() <= 0) {
      ret.append("Parameter velocity Template must be defined\n");
    }

    if (sourcetype.getText().trim().length() <= 0) {
      ret.append("Parameter Source Type must be defined\n");
    }

    if (targettype.getText().trim().length() <= 0) {
      ret.append("Parameter Target Type must be defined\n");
    }

    return ret.toString();
  }

  @Override
  public String getContent() {
    return velocityTemplate.getText().trim();
  }

  @Override
  public String getWhere() throws IOException {

    final Properties props = new Properties();
    props.setProperty("sourceType", sourcetype.getText().trim());
    props.setProperty("targetType", targettype.getText().trim());

    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    props.store(baos, "");

    return baos.toString();
  }

  @Override
  public boolean isChanged() {
    return true;
  }

}
