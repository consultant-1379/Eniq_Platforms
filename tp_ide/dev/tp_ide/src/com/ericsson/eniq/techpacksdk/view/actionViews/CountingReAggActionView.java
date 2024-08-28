package com.ericsson.eniq.techpacksdk.view.actionViews;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.ericsson.eniq.techpacksdk.common.Constants;

public class CountingReAggActionView implements ActionView {
  private transient static final Logger LOG = Logger.getLogger("CountingReAggActionView");

  private transient final JCheckBox isReaggBasedOn15min;

  public CountingReAggActionView(final JPanel parent, final Meta_transfer_actions action) {

    parent.removeAll();

    final GridBagConstraints gridbag = new GridBagConstraints();
    gridbag.anchor = GridBagConstraints.NORTHWEST;
    gridbag.fill = GridBagConstraints.NONE;
    gridbag.insets = new Insets(2, 2, 2, 2);
    gridbag.weightx = 0;
    gridbag.weighty = 0;

    gridbag.fill = GridBagConstraints.HORIZONTAL;
    gridbag.weightx = 1;
    gridbag.weighty = 1;
    gridbag.gridx = 1;

    isReaggBasedOn15min = new JCheckBox("Re-aggregate DAY based on 15 minute aggregations");
    isReaggBasedOn15min.setToolTipText("Decides whether to re-aggregate DAY based on 15 minute aggregations");
    gridbag.weightx = 0;
    gridbag.gridx = 0;
    gridbag.gridy++;

    gridbag.weightx = 1;
    gridbag.gridx = 1;

    parent.add(isReaggBasedOn15min, gridbag);
    
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
      
      final Boolean isReaggOn15min = Boolean.parseBoolean((prop.getProperty("isReaggBasedOn15min", "false")));
      isReaggBasedOn15min.setSelected(isReaggOn15min);
    } 
    
    parent.invalidate();
    parent.revalidate();
    parent.repaint();
  }

  @Override
  public String getType() {
    return Constants.COUNT_REAGG_ACTION;
  }

  @Override
  public String validate() {
    return "";
  }

  @Override
  public String getContent() {
    return "";
  }

  @Override
  public String getWhere() throws IOException {    
    final Properties props = new Properties();
    final String isReaggOn15min = String.valueOf(isReaggBasedOn15min.isSelected());
    props.put("isReaggBasedOn15min", isReaggOn15min);
    
    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    props.store(baos, "");

    return baos.toString();
  }

  @Override
  public boolean isChanged() {
    return true;
  }

}
