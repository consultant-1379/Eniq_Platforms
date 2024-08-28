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

public class UpdateHashIdsActionView implements ActionView {

  private transient static final Logger LOG = Logger.getLogger("UpdateHashIdsActionView");

  private transient final JTextField targetType;

  public UpdateHashIdsActionView(final JPanel parent, final Meta_transfer_actions action) {

    parent.removeAll();

    final JLabel l_stype = new JLabel("Target Type");
    l_stype.setToolTipText("StorageID of target type.");
    final GridBagConstraints gridbag = new GridBagConstraints();
    gridbag.anchor = GridBagConstraints.NORTHWEST;
    gridbag.fill = GridBagConstraints.NONE;
    gridbag.insets = new Insets(2, 2, 2, 2);
    gridbag.weightx = 0;
    gridbag.gridx = 0;
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

      } catch (Exception e) {
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
    return Constants.UPDATE_HASH_IDS;
  }

  @Override
  public String validate() {
    final StringBuffer ret = new StringBuffer(90);

    if (targetType.getText().trim().length() <= 0) {
      ret.append("Parameter target Type must be defined\n");
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
    props.setProperty("targetType", targetType.getText().trim());

    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    props.store(baos, "");

    return baos.toString();
  }

  @Override
  public boolean isChanged() {
    return true;
  }
}
