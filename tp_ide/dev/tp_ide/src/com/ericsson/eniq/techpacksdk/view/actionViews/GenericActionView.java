package com.ericsson.eniq.techpacksdk.view.actionViews;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;

/**
 * Copyright Distocraft 2005<br>
 * <br>
 * $id$
 * 
 * @author lemminkainen
 */
public class GenericActionView implements ActionView {

  private String type;

  private JTextArea where;

  private JTextArea content;

  private Meta_transfer_actions action;

  public GenericActionView(JPanel parent, Meta_transfer_actions action, String type) {
    this.action = action;
    this.type = type;

    parent.removeAll();

    where = new JTextArea(20, 20);

    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2, 2, 2, 2);
    c.weightx = 0;
    c.weighty = 0;

    parent.add(new JLabel("Where"), c);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    c.gridx = 1;
    parent.add(where, c);

    content = new JTextArea(20, 20);
    content.setLineWrap(true);
    content.setWrapStyleWord(true);

    JScrollPane scrollPane = new JScrollPane(content, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    c.gridx = 0;
    c.gridy = 1;

    parent.add(new JLabel("Content"), c);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    c.gridx = 1;
    parent.add(scrollPane, c);

    if (action != null) {
      where.setText(action.getWhere_clause());
      content.setText(action.getAction_contents());
    }
  }

  public String getType() {
    return type;
  }

  public String validate() {
    return "";
  }

  public String getContent() throws Exception {
    return content.getText().trim();
  }

  public String getWhere() throws Exception {
    return where.getText().trim();
  }

  public boolean isChanged() {
    if (action == null)
      return true;
    else
      return (where.getText().trim().equals(action.getWhere_clause()) || content.getText().trim().equals(
          action.getAction_contents()));
  }

}
