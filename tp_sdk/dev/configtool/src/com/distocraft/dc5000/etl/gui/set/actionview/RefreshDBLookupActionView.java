package com.distocraft.dc5000.etl.gui.set.actionview;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;

public class RefreshDBLookupActionView implements ActionView {

  final private JTextField tables;

  public RefreshDBLookupActionView(final JPanel parent, final Meta_transfer_actions action) {

    parent.removeAll();

    tables = new JTextField(20);
    tables.setToolTipText("Comma separated list of names of tables to be refreshed");

    final GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2, 2, 2, 2);
    c.weightx = 0;
    c.weighty = 0;

    final JLabel l_tables = new JLabel("Tables");
    l_tables.setToolTipText("Comma separated list of names of tables to be refreshed");
    parent.add(l_tables, c);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    c.gridx = 1;
    parent.add(tables, c);
    
    if(action != null) {
      tables.setText(action.getWhere_clause());
    }

    parent.invalidate();
    parent.revalidate();
    parent.repaint();
  }

  public String getType() {
    return "RefreshDBLookup";
  }
  
  public String validate() {
    return "";
  }

  public String getContent() throws Exception {
    return "";
  }

  public String getWhere() throws Exception {
    return tables.getText().trim();
  }

  public boolean isChanged() {
    return true;
  }

}
