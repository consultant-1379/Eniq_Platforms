package com.ericsson.eniq.techpacksdk.view.actionViews;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Properties;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.ericsson.eniq.component.ExceptionHandler;
import com.ericsson.eniq.component.NumericDocument;

public class SQLExtractActionView implements ActionView {

  private JTextArea clause;

  private JTextArea templateBody;

  private JTextArea templateHeader;

  private JTextArea templateFooter;

  private JTextField outputDir;

  private JTextField rowBufferSize;

  private JTextField dateColName;

  private JTextField filenameTemplate;

  private JTextField timestampFormat;

  public SQLExtractActionView(JPanel parent, Meta_transfer_actions action) {

    parent.removeAll();

    Properties orig = new Properties();

    if (action != null) {

      String act_cont = action.getAction_contents();

      if (act_cont != null && act_cont.length() > 0) {

        try {
          ByteArrayInputStream bais = new ByteArrayInputStream(act_cont.getBytes());
          orig.load(bais);
          bais.close();
        } catch (Exception e) {
          ExceptionHandler.instance().handle(e);
          e.printStackTrace();
        }
      }
    }

    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2, 2, 2, 2);
    c.gridx = 0;
    c.gridy = 0;
    c.weightx = 0;
    c.weighty = 0;

    clause = new JTextArea(10, 30);
    clause.setLineWrap(true);
    clause.setWrapStyleWord(true);
    clause.setText(orig.getProperty("clause", ""));
    JScrollPane scrollPane = new JScrollPane(clause, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    scrollPane.setToolTipText("SQL clause to be executed");
    JLabel l_scrollPane = new JLabel("SQL Clause");
    l_scrollPane.setToolTipText("SQL clause to be executed");
    parent.add(l_scrollPane, c);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    c.gridx = 2;
    parent.add(scrollPane, c);

    // -----

    c.gridy = 1;
    c.gridx = 0;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    templateHeader = new JTextArea(10, 30);
    templateHeader.setLineWrap(true);
    templateHeader.setWrapStyleWord(true);
    templateHeader.setText(orig.getProperty("templateHeader", ""));
    JScrollPane tscrollPaneh = new JScrollPane(templateHeader, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    JLabel l_tscrollPaneh = new JLabel("Template Header");
    l_tscrollPaneh.setToolTipText("Template header, is added to the output file at the top once.");
    parent.add(l_tscrollPaneh, c);
    tscrollPaneh.setToolTipText("Template header, is added to the output file at the top once.");
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    c.gridx = 2;
    parent.add(tscrollPaneh, c);

    // -----

    c.gridy = 2;
    c.gridx = 0;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    templateBody = new JTextArea(10, 30);
    templateBody.setLineWrap(true);
    templateBody.setWrapStyleWord(true);
    templateBody.setText(orig.getProperty("template", ""));
    JScrollPane tscrollPaneb = new JScrollPane(templateBody, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    tscrollPaneb.setToolTipText("Template body, is writen 1 or more times (see. buffer size) in output file.");
    JLabel l_tscrollPaneb = new JLabel("Template Body");
    l_tscrollPaneb.setToolTipText("Template body, is writen 1 or more times (see. buffer size) in output file.");
    parent.add(l_tscrollPaneb, c);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    c.gridx = 2;
    parent.add(tscrollPaneb, c);

    // -----

    c.gridy = 3;
    c.gridx = 0;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    templateFooter = new JTextArea(10, 30);
    templateFooter.setLineWrap(true);
    templateFooter.setWrapStyleWord(true);
    templateFooter.setText(orig.getProperty("templateFooter", ""));
    JScrollPane tscrollPanef = new JScrollPane(templateFooter, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    tscrollPanef.setToolTipText("Template footer, is added to the output file at the bottom once.");
    JLabel l_tscrollPanef = new JLabel("Template Footer");
    l_tscrollPanef.setToolTipText("Template footer, is added to the output file at the bottom once.");
    parent.add(l_tscrollPanef, c);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    c.gridx = 2;
    parent.add(tscrollPanef, c);

    // -----

    c.gridy = 4;
    c.gridx = 0;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    dateColName = new JTextField(orig.getProperty("dateColName", ""), 20);
    dateColName.setToolTipText("Output file buffer size. How many rows is written into output file in one iteration.");
    JLabel l_dateColName = new JLabel("Date column name");
    l_dateColName
        .setToolTipText("Defines the column where the data date is retrieved, if empty data date is the extraction date.");
    parent.add(l_dateColName, c);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    c.gridx = 2;
    parent.add(dateColName, c);

    // -----

    c.gridy = 5;
    c.gridx = 0;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    rowBufferSize = new JTextField(new NumericDocument(), orig.getProperty("rowBufferSize", ""), 20);
    rowBufferSize
        .setToolTipText("Output file buffer size. How many rows is written into output file in one iteration.");
    JLabel l_rowBufferSize = new JLabel("Row Buffer Size");
    l_rowBufferSize
        .setToolTipText("Output file buffer size. How many rows is written into output file in one iteration.");
    parent.add(l_rowBufferSize, c);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    c.gridx = 2;
    parent.add(rowBufferSize, c);

    // -----

    c.gridy = 6;
    c.gridx = 0;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    outputDir = new JTextField(20);
    outputDir.setText(orig.getProperty("outputDir", ""));
    outputDir.setToolTipText("Output Directory. Where output file is written.");
    JLabel l_outputDir = new JLabel("Output Dir");
    l_outputDir.setToolTipText("Output Directory. Where output file is written.");
    parent.add(l_outputDir, c);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    c.gridx = 2;
    parent.add(outputDir, c);

    // -----

    c.gridy = 7;
    c.gridx = 0;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    filenameTemplate = new JTextField(20);
    filenameTemplate.setText(orig.getProperty("filenameTemplate", ""));
    filenameTemplate.setToolTipText("Output filename.$-sign is replaced with datetime String. see. Timestamp format");
    JLabel l_filenameTemplate = new JLabel("Filename template");
    l_filenameTemplate.setToolTipText("Output filename.$-sign is replaced with timestamp. see. Timestamp format");
    parent.add(l_filenameTemplate, c);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    c.gridx = 2;
    parent.add(filenameTemplate, c);

    // -----

    c.gridy = 8;
    c.gridx = 0;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    timestampFormat = new JTextField(20);
    timestampFormat.setText(orig.getProperty("timestampFormat", ""));
    timestampFormat.setToolTipText("Defines the format for timestamp");
    JLabel l_timestampFormat = new JLabel("Timestamp format.");
    l_timestampFormat.setToolTipText("Timestamp format.");
    parent.add(l_timestampFormat, c);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    c.gridx = 2;
    parent.add(timestampFormat, c);

    // -----

    parent.invalidate();
    parent.revalidate();
    parent.repaint();
  }

  public String getType() {
    return "SQL Extract";
  }

  public String getContent() throws Exception {
    Properties p = new Properties();

    p.setProperty("outputDir", outputDir.getText());
    p.setProperty("clause", clause.getText());
    p.setProperty("template", templateBody.getText());
    p.setProperty("templateFooter", templateFooter.getText());
    p.setProperty("templateHeader", templateHeader.getText());
    p.setProperty("filenameTemplate", filenameTemplate.getText());
    p.setProperty("timestampFormat", timestampFormat.getText());
    p.setProperty("rowBufferSize", rowBufferSize.getText());
    p.setProperty("dateColName", dateColName.getText());

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    p.store(baos, "");

    return baos.toString();
  }

  public String getWhere() throws Exception {
    return "";
  }

  public boolean isChanged() {
    return true;
  }

  public String validate() {

    String error = "";

    if (rowBufferSize.getText().length() <= 0) {
    } else {
      try {
        Integer.parseInt(rowBufferSize.getText());
      } catch (NumberFormatException nfe) {
        error += "Row Buffer Size is not a valid number.\n";
      }
    }

    return error;
  }

}
