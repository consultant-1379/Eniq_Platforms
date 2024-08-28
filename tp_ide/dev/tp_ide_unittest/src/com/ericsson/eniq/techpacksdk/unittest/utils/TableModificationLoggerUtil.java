package com.ericsson.eniq.techpacksdk.unittest.utils;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import ssc.rockfactory.TableModificationLogger;

@SuppressWarnings("serial")
public class TableModificationLoggerUtil extends JFrame {

  private TableModificationLogger tableModificationLogger = TableModificationLogger.instance();
  
  private JTextArea textArea;
  private JButton resetButton;
  private JButton getButton;
  
//  public static void main(String[] args) {
//    TableUsageUtility tableUsageUtility = new TableUsageUtility();
//    tableUsageUtility.setVisible(true);
//    
//    while(true);
//  }
  
  public TableModificationLoggerUtil() {
    super("Table modification logger");
    this.setPreferredSize(new Dimension(200, 300));
    
    // Create layout for the frame
    this.setLayout(new GridBagLayout());
    GridBagConstraints gc = new GridBagConstraints();
    
    // Construct the text panel
    this.textArea = new JTextArea();
    JScrollPane textPanelS = new JScrollPane(textArea);
    
    gc.anchor = GridBagConstraints.NORTH;
    gc.fill = GridBagConstraints.BOTH;
    gc.gridx = 0;
    gc.gridy = 0;
    gc.weightx = 1;
    gc.weighty = 1;
    this.add(textPanelS, gc);
    
    // Construct the button panel
    JPanel buttonPanel = new JPanel(new FlowLayout());
    
    this.resetButton = new JButton("Reset");
    this.resetButton.addActionListener(new ResetButtonActionListener());
    buttonPanel.add(this.resetButton);
    
    this.getButton = new JButton("Get");
    this.getButton.addActionListener(new GetButtonActionListener());
    buttonPanel.add(this.getButton);
    
    gc.anchor = GridBagConstraints.SOUTH;
    gc.fill = GridBagConstraints.NONE;
    gc.gridx = 0;
    gc.gridy = 1;
    gc.weightx = 0;
    gc.weighty = 0;
    this.add(buttonPanel, gc);
    
    this.pack();
    
    // Start the database table modification logger 
    tableModificationLogger.startLogging();
  }
  
  private class ResetButtonActionListener implements ActionListener {

    public void actionPerformed(ActionEvent e) {
      tableModificationLogger.reset();
      textArea.setText("");
    }
    
  }
  
  private class GetButtonActionListener implements ActionListener {

    public void actionPerformed(ActionEvent e) {
      String[] usedTables = tableModificationLogger.get();
      Arrays.sort(usedTables);
      
      StringBuffer stringBuffer = new StringBuffer();
      for(int i=0; i<usedTables.length; ++i) {
        stringBuffer.append(usedTables[i] + "\n");
      }
      textArea.setText(stringBuffer.toString());
    }
    
  }
}
