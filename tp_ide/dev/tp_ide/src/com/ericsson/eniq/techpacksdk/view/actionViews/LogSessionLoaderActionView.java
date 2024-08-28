package com.ericsson.eniq.techpacksdk.view.actionViews;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Properties;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;

public class LogSessionLoaderActionView  implements ActionView {

  private JTextField logname;

  private JTextArea content;
  
  private JTextField dType;

  public LogSessionLoaderActionView(JPanel parent, Meta_transfer_actions action) {
    parent.removeAll();

    logname = new JTextField(20);

    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2, 2, 2, 2);
    c.weightx = 0;
    c.weighty = 0;

    JLabel l_logname = new JLabel("Logger name");
    l_logname.setToolTipText("Session logger name. ADAPTER, LOADER or AGGREGATOR");
    parent.add(l_logname, c);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    c.gridx = 1;
    logname.setToolTipText("Session logger name. ADAPTER, LOADER or AGGREGATOR");
    parent.add(logname, c);

    content = new JTextArea(20, 20);
    content.setLineWrap(true);
    content.setWrapStyleWord(true);
    
    dType = new JTextField(20);

    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    c.gridx = 0;
    c.gridy = 1;
    JLabel l_dType = new JLabel("Destination Type");
    l_dType.setToolTipText("Destination type where log are loaded.");
    parent.add(l_dType, c);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    c.gridx = 1;
    dType.setToolTipText("Destination type where log are loaded.");
    parent.add(dType, c);

    content = new JTextArea(20, 20);
    content.setLineWrap(true);
    content.setWrapStyleWord(true);
    
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    c.gridx = 0;
    c.gridy = 3;

    JLabel l_content = new JLabel("Load Template");
    l_content.setToolTipText("Template of load clause used.");
    parent.add(l_content, c);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    c.gridx = 1;
    c.weighty = 1;
    content.setToolTipText("Template of load clause used.");
    parent.add(content,c);

    if (action != null) {
      Properties props = stringToProperty(action.getWhere_clause());
      logname.setText(props.getProperty("logname", ""));
      dType.setText(props.getProperty("tablename", ""));     
      content.setText(action.getAction_contents());
    }

    parent.invalidate();
    parent.revalidate();
    parent.repaint();
  }

  public String getType() {
    return "SessionLog Loader";
  }
  
  public String validate() {
    String error = "";
    
    if(logname.getText().length() <= 0)
      error += "Parameter Logger name must be defined.\n";
    
    if(content.getText().length() <= 0)
      error += "Parameter Load template must be defined.\n";
    
    if(dType.getText().length() <= 0)
      error += "Parameter Destination type must be defined.\n";
    
    return error;
  }

  public String getContent() throws Exception {

    return content.getText().trim();
  }

  public String getWhere() throws Exception {
    Properties p = new Properties();
    p.put("logname", logname.getText().trim());
    p.put("tablename", dType.getText().trim());
    return propertyToString(p);
  }

  public boolean isChanged() {
    return true;
  }

  protected Properties stringToProperty(String str) {

    Properties prop = new Properties();

    try {

      if (str != null && str.length() > 0) {

        ByteArrayInputStream bais = new ByteArrayInputStream(str.getBytes());
        prop.load(bais);
        bais.close();
      }

    } catch (Exception e) {

    }

    return prop;

  }

  protected String propertyToString(Properties prop) {

    try {

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      prop.store(baos, "");

      return baos.toString();

    } catch (Exception e) {

    }

    return "";

  }

}
