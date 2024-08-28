package com.distocraft.dc5000.etl.gui.set.actionview;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.Properties;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;


public class GateKeeperActionView implements ActionView {

  private JTextArea gate;

  public GateKeeperActionView(JPanel parent, Meta_transfer_actions action) {

    parent.removeAll();

    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2, 2, 2, 2);
    c.weightx = 0;
    c.weighty = 0;
    
    gate = new JTextArea(20, 20);
    gate.setToolTipText("SQL template clause to determine weather the gate is open.");
    gate.setLineWrap(true);
    gate.setWrapStyleWord(true);
    
    if(action != null)
      gate.setText(action.getAction_contents());
    
    JScrollPane scrollPane = new JScrollPane(gate, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    JLabel l_gate = new JLabel("Gate clause");
    l_gate.setToolTipText("SQL template clause to determine weather the gate is open.");
    parent.add(new JLabel("Gate clause"), c);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    c.weighty = 1;
    c.gridx = 1;
    parent.add(scrollPane, c);

    parent.invalidate();
    parent.revalidate();
    parent.repaint();
  }

  public String getType() {
    return "Partitioned Load";
  }
  
  public String validate() {
    if(gate.getText().length() <= 0)
      return "Parameter Gate clause must be defined.\n";
    else
      return "";
  }

  public String getContent() throws Exception {
    return gate.getText();
  }

  public String getWhere() throws Exception {
    return "";
  }

  public boolean isChanged() {
    return true;
  }

}
