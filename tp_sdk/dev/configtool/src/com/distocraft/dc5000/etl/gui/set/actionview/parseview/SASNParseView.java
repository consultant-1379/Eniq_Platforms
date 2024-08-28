package com.distocraft.dc5000.etl.gui.set.actionview.parseview;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class SASNParseView implements ParseView {

  private static final String[] TAG_ID_MODES = { "from config", "parsed from filename" };

  private static final String[] TAG_ID_PARAM = { "TagID", "TagID filename pattern" };

  private final JPanel parent;

  private final JTextField column_delimiter;

  private final JComboBox tag_id_mode;

  private final JLabel h_tag_id;

  private final JTextField tag_id;

  private final JLabel h_header_row;

  public SASNParseView(final Properties p, final JPanel par) {

    this.parent = par;

    final GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2, 2, 2, 2);
    c.weightx = 0;
    c.weighty = 0;

    parent.setLayout(new GridBagLayout());

    //

    column_delimiter = new JTextField(p.getProperty("column_delimiter", ""), 5);
    column_delimiter.setToolTipText("Delimiter character separating columns in file. Default: white-space-character");

    c.gridy = 1;
    c.gridx = 0;
    final JLabel l_column_delimiter = new JLabel("Column delimiter");
    l_column_delimiter.setToolTipText("Delimiter character separating columns in file. Default: white-space-character");
    parent.add(l_column_delimiter, c);

    c.gridx = 1;
    parent.add(column_delimiter, c);

    //

    tag_id_mode = new JComboBox(TAG_ID_MODES);
    tag_id_mode.setToolTipText("Method to determine tagid of the file.");
    int tig_ix = 0;
    try {
      tig_ix = Integer.parseInt(p.getProperty("tag_id_mode", ""));
    } catch (Exception e) {
    }
    tag_id_mode.setSelectedIndex(tig_ix);
    tag_id_mode.addActionListener(new ActionListener() {

      public void actionPerformed(final ActionEvent ae) {
        h_tag_id.setText(TAG_ID_PARAM[tag_id_mode.getSelectedIndex()]);
        parent.invalidate();
        parent.revalidate();
        parent.repaint();
      }
    });

    c.gridy = 2;
    c.gridx = 0;
    JLabel l_tag_id_mode = new JLabel("TagID mode");
    l_tag_id_mode.setToolTipText("Method to determine tagid of the file.");
    parent.add(l_tag_id_mode, c);

    c.gridx = 1;
    parent.add(tag_id_mode, c);

    //

    tag_id = new JTextField(p.getProperty("tag_id", ""), 30);

    h_tag_id = new JLabel(TAG_ID_PARAM[tag_id_mode.getSelectedIndex()]);

    c.gridy = 3;
    c.gridx = 0;
    parent.add(h_tag_id, c);

    c.gridx = 1;
    parent.add(tag_id, c);

    //

    //

    h_header_row = new JLabel("Header rows");

    h_header_row.setEnabled(true);

    c.gridy = 5;
    c.gridx = 0;
    parent.add(h_header_row, c);


    c.gridx = 2;
    c.gridy = 9;
    c.weightx = 1;
    c.weighty = 1;
    c.fill = GridBagConstraints.BOTH;
    parent.add(Box.createRigidArea(new Dimension(1, 1)), c);

  }

  public String validate() {
    
    return "";
  }

  /**
   * Returns a set of parameter names that ParseView represents.
   * 
   * @return Set of parameter names
   */
  public Set getParameterNames() {
    final Set s = new HashSet();

    s.add("column_delimiter");
    s.add("tag_id_mode");
    s.add("tag_id");

    return s;
  }

  /**
   * Sets ParseView specific parameters to a properties object.
   * 
   * @param p
   *          Properties object
   */
  public void fillParameters(final Properties p) {
    p.setProperty("column_delimiter", column_delimiter.getText());
    p.setProperty("tag_id_mode", String.valueOf(tag_id_mode.getSelectedIndex()));
    p.setProperty("tag_id", tag_id.getText());
  }

}
