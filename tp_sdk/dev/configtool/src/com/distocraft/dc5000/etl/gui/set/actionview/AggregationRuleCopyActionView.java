package com.distocraft.dc5000.etl.gui.set.actionview;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;

public class AggregationRuleCopyActionView implements ActionView {

  private JTextField mode;

  public AggregationRuleCopyActionView(JPanel parent, Meta_transfer_actions action) {

    parent.removeAll();

    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2, 2, 2, 2);
    c.weightx = 0;
    c.weighty = 0;

    mode = new JTextField(10);
    mode.setToolTipText("Rule copying mode. UPDATE mode deletes only rules of activated techpacks otherwise deletes all rules.");

    if(action != null)
      mode.setText(action.getAction_contents());
    
    JLabel l_mode = new JLabel("Mode");
    l_mode.setToolTipText("Rule copying mode. UPDATE mode deletes only rules of activated techpacks otherwise deletes all rules.");
    parent.add(l_mode, c);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    c.gridx = 1;
    parent.add(mode, c);

    parent.invalidate();
    parent.revalidate();
    parent.repaint();
  }

  public String getType() {
    return "CopyAggregationRule";
  }
  
  public String validate() {
    return "";
  }

  public String getContent() throws Exception {
    return mode.getText();
  }

  public String getWhere() throws Exception {
    return "";
  }

  public boolean isChanged() {
    return true;
  }

}
