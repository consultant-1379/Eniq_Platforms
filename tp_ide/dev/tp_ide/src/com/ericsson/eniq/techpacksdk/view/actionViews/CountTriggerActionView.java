package com.ericsson.eniq.techpacksdk.view.actionViews;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.ericsson.eniq.techpacksdk.common.Constants;
import com.ericsson.eniq.techpacksdk.common.Utils;

public class CountTriggerActionView implements ActionView {
	
  private static final String LOCK_TABLE = "lockTable";

  private static final String SET_NAME = "setName";

  private static final int VALID_MSG_LENGTH = 114;

  private transient static final Logger LOG = Logger.getLogger(CountTriggerActionView.class.getName());
	
  private transient final JCheckBox[] boxes;
  private transient final JTextField setname;
  private transient final JTextField locktable;
	
	public CountTriggerActionView(final JPanel parent, final Meta_transfer_actions action) {
    
    parent.removeAll();
    
    final JLabel l_timelevels = new JLabel("Timelevels");
    l_timelevels.setToolTipText("Timelevels this counting is performed.");
    
    final GridBagConstraints gridbag = new GridBagConstraints();
    gridbag.anchor = GridBagConstraints.NORTHWEST;
    gridbag.fill = GridBagConstraints.NONE;
    gridbag.insets = new Insets(2,2,2,2);
    gridbag.weightx = 0;
    gridbag.weighty = 0;
    parent.add(l_timelevels,gridbag);
    
    boxes = new JCheckBox[Constants.COUNTING_LEVELS.length];
    
    for (int i = 0 ; i < Constants.COUNTING_LEVELS.length ; i++) {
    
    	boxes[i] = new JCheckBox(Constants.COUNTING_DESCRIPTIONS[i]);
    
    	gridbag.weightx = 1;
    	gridbag.gridx = 1;
    	gridbag.gridy++;
    	
    	parent.add(boxes[i],gridbag);
    
    }
    
    final JLabel l_setname = new JLabel("Calculation Set");
    l_setname.setToolTipText("Name of calculation set to trigger.");
    
    gridbag.weightx = 0;
    gridbag.gridx = 0;
    gridbag.gridy++;
    parent.add(l_setname,gridbag);
    
    setname = new JTextField(20);

    gridbag.weightx = 1;
    gridbag.gridx = 1;
    parent.add(setname,gridbag);
    
    final JLabel l_locktable = new JLabel("Lock Table");
    l_locktable.setToolTipText("Basename for target table for locking.");
    
    gridbag.weightx = 0;
    gridbag.gridx = 0;
    gridbag.gridy++;
    parent.add(l_locktable,gridbag);
    
    locktable = new JTextField(20);
    
    gridbag.weightx = 1;
    gridbag.gridx = 1;
    parent.add(locktable,gridbag);
        
    if(action != null) {
      
      Properties prop = new Properties();
      
      try {
        prop = Utils.stringToProperty(action.getWhere_clause());
      } catch (Exception e) {
      	LOG.log(Level.INFO, "Parameter read failed", e);
      }
      
      setname.setText(prop.getProperty(SET_NAME, ""));
      
      for (String t : prop.getProperty("timelevels", "").split(",")) {
        if (t != null && t.length() > 0) {
          for (int i = 0; i < Constants.COUNTING_LEVELS.length; i++) {
            try {
              if (Constants.COUNTING_LEVELS[i] == Integer.parseInt(t)) {
                boxes[i].setSelected(true);
              }
            } catch (NumberFormatException e) {
              LOG.log(Level.INFO, "NumberFormatException for " + t, e);
            }
          }
        }
      }
      
      locktable.setText(prop.getProperty(LOCK_TABLE,""));
            
    }
      
    parent.invalidate();
    parent.revalidate();
    parent.repaint();
  }
  
  @Override
  public String getType() {
    return Constants.COUNTING_TRIGGER;
  }
    
  @Override
  public String validate() {
  	final StringBuffer ret = new StringBuffer(VALID_MSG_LENGTH);
  	
    if(setname.getText().trim().length() <= 0) {
      ret.append("Parameter Calculation Set must be defined\n");
    }
    
    if(locktable.getText().trim().length() <= 0) {
      ret.append("Parameter LockTable must be defined\n");
    }
    
    boolean checked = false;
    for(int i = 0 ; i < boxes.length ; i++) {
    	if(boxes[i].isSelected()) {
    		checked = true; 
        break;
    	}
    }
    
    if(!checked) {
    	ret.append("At least 1 Timelevel must be set\n");
    }
    
    return ret.toString();
  }
  
  @Override
  public String getContent() {
    return "";
  }
  
  @Override
  public String getWhere() throws IOException {
    
    final Properties props = new Properties();
    props.setProperty(SET_NAME, setname.getText().trim());
    props.setProperty(LOCK_TABLE, locktable.getText().trim());
    
    final StringBuffer buffer = new StringBuffer();
    for(int i = 0 ; i < boxes.length ; i++) {
    	if(boxes[i].isSelected()) {
    		if(buffer.length() > 0) {
    			buffer.append(',');
    		}
    		buffer.append(Constants.COUNTING_LEVELS[i]);
    	}
    }
    props.setProperty("timelevels", buffer.toString());
    
    return Utils.propertyToString(props);
  }
  
  @Override
  public boolean isChanged() {
    return true;
  }
    
}
