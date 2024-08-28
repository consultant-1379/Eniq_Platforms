package com.ericsson.eniq.techpacksdk.view.actionViews;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;


/**
 * Copyright Distocraft 2005 <br>
 * <br>
 * $id$
 * 
 * @author lemminkainen
 */
public class GenericPropertiesView extends JScrollPane {

  private final JDialog parent;
  
  private final Properties props;

  private final DefaultTableModel dtm;
  private final JTable jt;
  
  private boolean enabled = true;

  public GenericPropertiesView(final Properties prs, final Set usedParams, final JDialog parent) {
    super(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    this.parent = parent;
    
    final String[] cols = { "Name", "Value" };

    dtm = new DefaultTableModel(cols, 0);
    jt = new GPVJTable(dtm);
   

    this.setViewportView(jt);

    if (prs == null) {
      this.props = new Properties();
    } else {
      this.props = prs;
    }
      
    Enumeration keys = props.keys();
    while (keys.hasMoreElements()) {
      String key = (String) keys.nextElement();

      
      if (usedParams.contains(key)) {
        continue;
      } else {
        boolean found = false;
        Iterator i = usedParams.iterator();
        while(i.hasNext()) {
          String usedKey = (String)i.next();
          
          if(usedKey.endsWith("*")) {
            if(key.startsWith(usedKey.substring(0,usedKey.length()-1))) {
              found = true;
              break;
            }
          } else {
            continue;
          }
        }
        
        if(!found) {
          String[] row = { key, (String) props.get(key) };
          dtm.addRow(row);
        }
        
      }
      
    }

    jt.addMouseListener(new GPVMouseListener());
    jt.getTableHeader().addMouseListener(new GPVMouseListener());
    
    Dimension pd = jt.getPreferredSize();
    jt.setPreferredSize(new Dimension(pd.width,6*jt.getRowHeight()));
    
    this.setPreferredSize(new Dimension(pd.width,6*jt.getRowHeight()+10));
  }
  
  public void setEnabled(final boolean enabled) {
    jt.setEnabled(enabled);
    this.enabled = enabled;
  }

  public Properties getProperties() {

    final Properties p = new Properties();

    for (int i = 0; i < dtm.getRowCount(); i++) {
      final String key = (String) dtm.getValueAt(i, 0);
      final String val = (String) dtm.getValueAt(i, 1);

      if(key != null && val != null) {
        p.setProperty(key, val);
      }
      
    }

    return p;
  }

  public class GPVJTable extends JTable {
    public GPVJTable(final DefaultTableModel dm) {
      super(dm);
    }
    public boolean isCellEditable(final int row, final int col) {
      return false;
    }
  };
  
  public class GPVMouseListener extends MouseAdapter {

    public void mouseClicked(final MouseEvent e) {
      final int row = jt.rowAtPoint(e.getPoint());
      
      if(!enabled) {
        return;
      }
      
      if (e.getButton() == MouseEvent.BUTTON3) { // PopUp Menu
        final JPopupMenu pop = new JPopupMenu("Actions");

        final JMenuItem atp = new JMenuItem("Create new Parameter", null);
        atp.addActionListener(new ActionListener() {

          public void actionPerformed(ActionEvent ae) {
            final GPVDialog gpvd = new GPVDialog(parent, "New", "", "");
            
            if(gpvd.getName() != null) {
              final Object[] oa = new Object[2];
              oa[0] = gpvd.getName();
              oa[1] = gpvd.getValue();
              dtm.addRow(oa);
            }
          }
        });
        pop.add(atp);
        if (row >= 0) {
          final JMenuItem inp = new JMenuItem("Delete this Parameter", null);
          inp.addActionListener(new ActionListener() {

            public void actionPerformed(final ActionEvent ae) {
              dtm.removeRow(row);
            }
          });
          pop.add(inp);
        }

        pop.show(e.getComponent(), e.getX(), e.getY());

      } else if(e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() >= 2) {
        final int r = jt.rowAtPoint(e.getPoint());

        String key = "";
        String val = "";
        
        if(r >= 0) {
          key = (String) dtm.getValueAt(r, 0);
          val = (String) dtm.getValueAt(r, 1);
        }
        
        final GPVDialog gpvd = new GPVDialog(parent, "Edit", key, val);
        
        if(gpvd.getName() != null) {
          dtm.setValueAt(gpvd.getName(),r,0);
          dtm.setValueAt(gpvd.getValue(),r,1);
        }
        
      }
        
    }
  };
  
  public class GPVDialog extends JDialog {
    private final JTextField name;
    private final JTextField value;
    
    private String rname = null;
    private String rvalue = null;
    
    public GPVDialog(final JDialog owner, final String title, final String sname, final String svalue) {
      super(owner, title, true);
      
      final GridBagLayout gbl = new GridBagLayout();
      final GridBagConstraints c = new GridBagConstraints();
      
      getContentPane().setLayout(gbl);
      
      c.weightx = 0;
      c.weighty = 0;
      c.fill = GridBagConstraints.NONE;
      c.anchor = GridBagConstraints.NORTHWEST;
      getContentPane().add(Box.createRigidArea(new Dimension(10,10)),c);
      
      c.gridx = 1;
      c.gridy = 1;
      getContentPane().add(new JLabel("Name"),c);
      
      c.gridx = 2;
      getContentPane().add(Box.createRigidArea(new Dimension(5,5)),c);
      
      c.gridx = 3;
      name = new JTextField(sname,10);
      name.addActionListener(new ActionListener() {
        public void actionPerformed(final ActionEvent ae) {
          ready();
        }
      });
      getContentPane().add(name,c);
      
      c.gridx = 4;
      getContentPane().add(Box.createRigidArea(new Dimension(10,10)),c);
      
      c.gridx = 1;
      c.gridy = 2;
      getContentPane().add(new JLabel("Value"),c);
      
      c.gridx = 3;
      value = new JTextField(svalue,10);
      value.addActionListener(new ActionListener() {
        public void actionPerformed(final ActionEvent ae) {
          ready();
        }
      });
      getContentPane().add(value,c);
      
      c.gridx = 1;
      c.gridy = 3;
      getContentPane().add(Box.createRigidArea(new Dimension(10,10)),c);
      
      c.gridy = 4;
      c.gridwidth = 3;
      c.anchor = GridBagConstraints.CENTER;
      JButton ok = new JButton("OK");
      ok.addActionListener(new ActionListener() {
        public void actionPerformed(final ActionEvent ae) {
          ready();
        }
      });
      getContentPane().add(ok,c);
      
      pack();
      setVisible(true);
      
    }
    
    private void ready() {
      rvalue = value.getText().trim();
      
      rname = name.getText().trim();
      if(rname.length() <= 0) {
        rname = null;
        rvalue = null;
      } else {
        setVisible(false);
      }
    }
    
    public String getName() {
      return rname;
    }
    
    public String getValue() {
      return rvalue;
    }
    
    
  };

}
