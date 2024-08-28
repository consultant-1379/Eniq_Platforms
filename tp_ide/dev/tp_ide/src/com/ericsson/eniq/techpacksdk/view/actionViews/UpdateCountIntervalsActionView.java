/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
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
import javax.swing.JTextField;

import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.ericsson.eniq.techpacksdk.common.Constants;

/**
 * Action view for updating counting intervals in dwhrep.CountingManagement
 * table on upgrade if necessary
 * 
 * @author epaujor
 * @since 2012
 * 
 */
public class UpdateCountIntervalsActionView implements ActionView {

  private transient static final Logger LOG = Logger.getLogger("UpdateCountIntervalsActionView");

  private transient final JTextField targetType;

  public UpdateCountIntervalsActionView(final JPanel parent, final Meta_transfer_actions action) {

    parent.removeAll();

    GridBagConstraints gridbag = new GridBagConstraints();
    gridbag.anchor = GridBagConstraints.NORTHWEST;
    gridbag.insets = new Insets(2, 2, 2, 2);
    gridbag.weightx = 0;
    gridbag.weighty = 0;

    final JLabel l_stype = new JLabel("Storage ID");
    l_stype.setToolTipText("StorageID to update count intervals for.");

    gridbag.weightx = 0;
    gridbag.gridx = 0;
    gridbag.gridy++;
    parent.add(l_stype, gridbag);

    targetType = new JTextField(20);

    gridbag.weightx = 1;
    gridbag.gridx = 1;
    parent.add(targetType, gridbag);

    if (action != null) {

      final Properties prop = new Properties();

      final String str = action.getWhere_clause();

      try {
        if (str != null && str.length() > 0) {
          final ByteArrayInputStream bais = new ByteArrayInputStream(str.getBytes());
          prop.load(bais);
          bais.close();
        }
      } catch (IOException e) {
        LOG.log(Level.INFO, "Reading parameters failed", e);
      }
      targetType.setText(prop.getProperty("targetType", ""));
    }

    parent.invalidate();
    parent.revalidate();
    parent.repaint();
  }

  @Override
  public String getType() {
    return Constants.UPDATE_COUNT_INTERVALS;
  }

  @Override
  public String validate() {
    final StringBuffer ret = new StringBuffer(90);

    if (targetType.getText().trim().length() <= 0) {
      ret.append("Parameter Storage ID must be defined\n");
    }

    return ret.toString();
  }

  @Override
  public String getContent() {
    return "";
  }

  @Override
  public String getWhere() throws IOException {

    final Properties props = new Properties();
    final String storageId = targetType.getText().trim();
    props.setProperty("targetType", storageId);

    if (storageId== null || storageId.isEmpty()){
      throw new IOException("Storage ID is not known");
    }

    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    props.store(baos, "");
    return baos.toString();
  }

  @Override
  public boolean isChanged() {
    return true;
  }
}
