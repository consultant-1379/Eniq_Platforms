package com.ericsson.eniq.techpacksdk.view.actionViews;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.ericsson.eniq.techpacksdk.common.Constants;

public class CountActionView implements ActionView {
	
	private transient static final Logger LOG = Logger.getLogger("CountActionView");
	
  private transient final JTextArea template;
  private transient final JTextField sourcetype;
  private transient final JTextField targettype;
	
	public CountActionView(final JPanel parent, final Meta_transfer_actions action) {
    
    parent.removeAll();
    
    final JLabel l_timelevels = new JLabel("Template");
    l_timelevels.setToolTipText("SQL Template of counting.");
    
    final GridBagConstraints gridbag = new GridBagConstraints();
    gridbag.anchor = GridBagConstraints.NORTHWEST;
    gridbag.fill = GridBagConstraints.NONE;
    gridbag.insets = new Insets(2,2,2,2);
    gridbag.weightx = 0;
    gridbag.weighty = 0;
    parent.add(l_timelevels,gridbag);
    
    template = new JTextArea(20,20);
    template.setName("Aggregation Clause");
    template.setLineWrap(true);
    template.setWrapStyleWord(true);
    final JScrollPane scrollPane = 
      new JScrollPane(template,
                      JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                      JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    
    gridbag.weightx = 1;
    gridbag.gridx = 1;
    gridbag.fill = GridBagConstraints.BOTH;
    gridbag.gridy++;
    	
    parent.add(scrollPane,gridbag);
    
    final JLabel l_ttype = new JLabel("Source Type");
    l_ttype.setToolTipText("StorageID of source type.");
    
    gridbag.weightx = 0;
    gridbag.gridx = 0;
    gridbag.fill = GridBagConstraints.NONE;
    gridbag.gridy++;
    parent.add(l_ttype,gridbag);
    
    sourcetype = new JTextField(20);

    gridbag.weightx = 1;
    gridbag.gridx = 1;
    parent.add(sourcetype, gridbag);
    
    final JLabel l_stype = new JLabel("Target Type");
    l_stype.setToolTipText("Name of target type. This is catenated with : and level name to get storageID for type.");
    
    gridbag.weightx = 0;
    gridbag.gridx = 0;
    gridbag.gridy++;
    parent.add(l_stype,gridbag);
    
    targettype = new JTextField(20);

    gridbag.weightx = 1;
    gridbag.gridx = 1;
    parent.add(targettype, gridbag);

    if(action != null) {
      
    	final String temp = action.getAction_contents();
    	
    	if(temp != null) {
    		template.setText(temp);
    	}
    	
      final Properties prop = new Properties();

      final String str = action.getWhere_clause();
      
      try {

        if (str != null && str.length() > 0) {

          final ByteArrayInputStream bais = new ByteArrayInputStream(str.getBytes());
          prop.load(bais);
          bais.close();
        }

      } catch (Exception e) {
      	LOG.log(Level.INFO, "Reading parameters failed", e);
      }
      
      targettype.setText(prop.getProperty("targetType", ""));
      sourcetype.setText(prop.getProperty("sourceType", ""));
    }
      
    parent.invalidate();
    parent.revalidate();
    parent.repaint();
  } 
  
  @Override
  public String getType() {
    return Constants.COUNTING_ACTION;
  }
    
  @Override
  public String validate() {
  	final StringBuffer ret = new StringBuffer(80);
  	
    if(template.getText().trim().length() <= 0) {
      ret.append("Parameter SQL Template must be defined\n");
    }
    
    if(targettype.getText().trim().length() <= 0) {
      ret.append("Parameter Target Type must be defined\n");
    }
    
    if(sourcetype.getText().trim().length() <= 0) {
      ret.append("Parameter Source Type must be defined\n");
    }
    
    return ret.toString();
  }
  
  @Override
  public String getContent() {
    return template.getText().trim();
  }
  
  @Override
  public String getWhere() throws IOException {
    
    final Properties props = new Properties();
    props.setProperty("targetType", targettype.getText().trim());
    props.setProperty("sourceType", sourcetype.getText().trim());
    
    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    props.store(baos, "");

    return baos.toString();
  }
  
  @Override
  public boolean isChanged() {
    return true;
  }    
}
