package com.ericsson.eniq.techpacksdk.view.actionViews.parserViews;

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

import com.ericsson.eniq.component.NumericDocument;



public class ASCIIParseView implements ParseView {

  private static final String[] TAG_ID_MODES = { "from config", "parsed from filename" };

  private static final String[] TAG_ID_PARAM = { "TagID", "TagID filename pattern" };

  private static final String[] DATA_ID_MODES = { "by parsing file header row", "by parsing config header row",
      "by column order" };

  private static final String[] DATATIME_MODES = { "skipped", "from column", "parsed from filename" };

  private static final String[] DATATIME_COLUMN = { "", "Column name", "Filename pattern" };

  private final JPanel parent;

  private final JTextField row_delimiter;

  private final JTextField column_delimiter;

  private final JComboBox tag_id_mode;

  private final JLabel h_tag_id;

  private final JTextField tag_id;

  private final JComboBox data_id_mode;

  private final JLabel h_header_row;

  private final JTextField header_row;

  private final JComboBox datatime_mode;

  private final JLabel h_datatime_column;

  private final JTextField datatime_column;

  private final JTextField header_skip;
  
  private final JTextField header_in_row;

  public ASCIIParseView(Properties p, JPanel par) {

    this.parent = par;

    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2, 2, 2, 2);
    c.weightx = 0;
    c.weighty = 0;

    parent.setLayout(new GridBagLayout());

    //

    row_delimiter = new JTextField(p.getProperty("row_delimiter", ""), 5);
    row_delimiter.setToolTipText("Delimiter character separating rows in file. Default: new line-character");

    c.fill = GridBagConstraints.NONE;
    JLabel l_row_delimiter = new JLabel("Row delimiter");
    l_row_delimiter.setToolTipText("Delimiter character separating rows in file. Default: new line-character");
    parent.add(l_row_delimiter, c);

    c.gridx = 1;
    parent.add(row_delimiter, c);

    //

    column_delimiter = new JTextField(p.getProperty("column_delimiter", ""), 5);
    column_delimiter.setToolTipText("Delimiter character separating columns in file. Default: tab-character");

    c.gridy = 1;
    c.gridx = 0;
    JLabel l_column_delimiter = new JLabel("Column delimiter");
    l_column_delimiter.setToolTipText("Delimiter character separating columns in file. Default: tab-character");
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

    data_id_mode = new JComboBox(DATA_ID_MODES);
    data_id_mode.setToolTipText("Method to identify data columns of a row of data.");
    int di_ix = 0;
    try {
      di_ix = Integer.parseInt(p.getProperty("data_id_mode", ""));
    } catch (Exception e) {
    }
    data_id_mode.setSelectedIndex(di_ix);
    data_id_mode.addActionListener(new ActionListener() {

      public void actionPerformed(final ActionEvent ae) {
        if (data_id_mode.getSelectedIndex() == 1) {
          header_row.setEnabled(true);
          h_header_row.setEnabled(true);
        } else {
          header_row.setEnabled(false);
          h_header_row.setEnabled(false);
        }
        parent.invalidate();
        parent.revalidate();
        parent.repaint();
      }
    });

    c.gridy = 4;
    c.gridx = 0;

    JLabel l_data_id_mode = new JLabel("Data identification");
    parent.add(l_data_id_mode, c);
    l_data_id_mode.setToolTipText("Method to identify data columns of a row of data.");
    c.gridx = 1;
    parent.add(data_id_mode, c);

    //

    header_row = new JTextField(p.getProperty("header_row", ""), 50);

    h_header_row = new JLabel("Header row");

    if (data_id_mode.getSelectedIndex() == 1) {
      header_row.setEnabled(true);
      h_header_row.setEnabled(true);
    } else {
      header_row.setEnabled(false);
      h_header_row.setEnabled(false);
    }

    c.gridy = 5;
    c.gridx = 0;
    parent.add(h_header_row, c);

    c.gridx = 1;
    parent.add(header_row, c);

    //

    datatime_mode = new JComboBox(DATATIME_MODES);
    datatime_mode.setToolTipText("Method to identify datatime of the data.");
    int dt_ix = 0;
    try {
      dt_ix = Integer.parseInt(p.getProperty("datatime_mode", ""));
    } catch (Exception e) {
    }
    datatime_mode.setSelectedIndex(dt_ix);
    datatime_mode.addActionListener(new ActionListener() {

      public void actionPerformed(final ActionEvent ae) {
        h_datatime_column.setText(DATATIME_COLUMN[datatime_mode.getSelectedIndex()]);
        if (datatime_mode.getSelectedIndex() > 0) {
          datatime_column.setEnabled(true);
          h_datatime_column.setEnabled(true);
        } else {
          datatime_column.setEnabled(false);
          h_datatime_column.setEnabled(false);
        }
        parent.invalidate();
        parent.revalidate();
        parent.repaint();
      }
    });

    c.gridy = 6;
    c.gridx = 0;
    JLabel l_datatime_mode = new JLabel("Datatime mode");
    l_datatime_mode.setToolTipText("Method to identify datatime of the data.");
    parent.add(l_datatime_mode, c);

    c.gridx = 1;
    parent.add(datatime_mode, c);

    //

    datatime_column = new JTextField(p.getProperty("datatime_column", ""), 20);

    h_datatime_column = new JLabel(DATATIME_COLUMN[datatime_mode.getSelectedIndex()]);

    if (datatime_mode.getSelectedIndex() > 0) {
      datatime_column.setEnabled(true);
      h_datatime_column.setEnabled(true);
    } else {
      datatime_column.setEnabled(false);
      h_datatime_column.setEnabled(false);
    }

    c.gridy = 7;
    c.gridx = 0;
    parent.add(h_datatime_column, c);

    c.gridx = 1;
    parent.add(datatime_column, c);

    //

    header_skip = new JTextField(new NumericDocument(),p.getProperty("header_skip", ""), 5);
    header_skip.setToolTipText("Number of rows at the start of the file skipped as headers. Default: 1 (rows)");

    c.gridy = 8;
    c.gridx = 0;
    JLabel l_header_skip = new JLabel("Header skip");
    l_header_skip.setToolTipText("Number of rows at the start of the file skipped as headers. Default: 1 (rows)");
    parent.add(l_header_skip, c);

    c.gridx = 1;
    parent.add(header_skip, c);

    header_in_row = new JTextField(new NumericDocument(),p.getProperty("header_in_row", ""), 5);
    header_in_row.setToolTipText("Row number where to read the header (row count starts from 0). Default: 0");

    c.gridy = 9;
    c.gridx = 0;
    JLabel l_header_in_row = new JLabel("Header in row");
    l_header_in_row.setToolTipText("Row number where to read the header (row count starts from 0). Default: 0");
    parent.add(l_header_in_row, c);

    c.gridx = 1;
    parent.add(header_in_row, c);
    
    c.gridx = 2;
    c.gridy = 10;
    c.weightx = 1;
    c.weighty = 1;
    c.fill = GridBagConstraints.BOTH;
    parent.add(Box.createRigidArea(new Dimension(1, 1)), c);
    

  }

  public String validate() {
    String returnValue = "";
    
    final String hs = header_skip.getText();
    if (hs.length() > 0) {
      try {
        Integer.parseInt(header_skip.getText());
      } catch (NumberFormatException nfe) {
        returnValue += "Parameter Header skip: Not a number\n";
      }
    }
    
    final String hir = header_in_row.getText();
    if (hir.length() > 0) {
      try {
        Integer.parseInt(header_in_row.getText());
      } catch (NumberFormatException nfe) {
        returnValue += "Parameter Header in row: Not a number\n";
      }
    }
    
    return returnValue;
  }

  /**
   * Returns a set of parameter names that ParseView represents.
   * 
   * @return Set of parameter names
   */
  public Set getParameterNames() {
    final Set s = new HashSet();

    s.add("row_delimiter");
    s.add("column_delimiter");
    s.add("tag_id_mode");
    s.add("tag_id");
    s.add("data_id_mode");
    s.add("header_row");
    s.add("datatime_mode");
    s.add("datatime_column");
    s.add("header_skip");
    s.add("header_in_row");

    return s;
  }

  /**
   * Sets ParseView specific parameters to a properties object.
   * 
   * @param p
   *          Properties object
   */
  public void fillParameters(final Properties p) {
    p.setProperty("row_delimiter", row_delimiter.getText());
    p.setProperty("column_delimiter", column_delimiter.getText());
    p.setProperty("tag_id_mode", String.valueOf(tag_id_mode.getSelectedIndex()));
    p.setProperty("tag_id", tag_id.getText());
    p.setProperty("data_id_mode", String.valueOf(data_id_mode.getSelectedIndex()));
    if (header_row.isEnabled()) {
      p.setProperty("header_row", String.valueOf(header_row.getText()));
    }
    p.setProperty("datatime_mode", String.valueOf(datatime_mode.getSelectedIndex()));
    if (datatime_column.isEnabled()) {
      p.setProperty("datatime_column", datatime_column.getText());
    }
    p.setProperty("header_skip", header_skip.getText());
    p.setProperty("header_in_row", header_in_row.getText());
  }

}
