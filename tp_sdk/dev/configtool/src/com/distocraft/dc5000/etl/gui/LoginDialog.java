package com.distocraft.dc5000.etl.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.rock.Meta_databases;
import com.distocraft.dc5000.etl.rock.Meta_databasesFactory;


/**
 * Copyright Distocraft 2005 <br>
 * <br>
 * $id$
 * 
 * @author lemminkainen
 */
public class LoginDialog {

  public static final String URL_PREFIX = "jdbc:sybase:Tds:";

  public static final String DB_DRIVER = "com.sybase.jdbc3.jdbc.SybDriver";

  private Properties conProps;

  private boolean connectFlag = false;

  private JComboBox cid;

  private JTextField host;

  private JTextField port;

  private JTextField user;

  private JPasswordField pass;

  private JCheckBox save;
  
  private String hostname = null;

  public LoginDialog(Frame owner, final Properties conProps) {
    this.conProps = conProps;

    cid = new JComboBox();
    cid.setRenderer(new ConnectionListCellRenderer());
    cid.addItem("");
    host = new JTextField("", 15);
    port = new JTextField("2641", 5);
    user = new JTextField("etlrep", 8);
    pass = new JPasswordField("", 8);
    save = new JCheckBox("Save password", false);

    final JDialog jd = new JDialog(owner, true);

    jd.setTitle("Connection Settings");

    Container con = jd.getContentPane();
    con.setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2, 2, 2, 2);

    con.add(Box.createRigidArea(new Dimension(5, 5)), c);

    c.gridy = 1;
    c.gridx = 1;
    c.gridwidth = 2;
    c.weightx = 1;
    c.fill = GridBagConstraints.HORIZONTAL;
    con.add(new JLabel("Connection settings for ETLC Repository"), c);

    c.gridy = 2;
    con.add(Box.createRigidArea(new Dimension(5, 5)), c);

    c.gridy = 3;
    c.gridwidth = 1;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    con.add(new JLabel("Connection ID"), c);

    c.gridx = 2;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;

    Enumeration e = conProps.keys();
    while (e.hasMoreElements()) {
      String key = (String) e.nextElement();
      if (key.endsWith(".key")) {
        cid.addItem(key.substring(0, key.lastIndexOf(".")));
      }
    }

    cid.setEditable(true);
    cid.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent ae) {
        String key = (String) cid.getSelectedItem();

        String thost = conProps.getProperty(key + ".host", null);
        if (thost != null)
          host.setText(thost);

        String tport = conProps.getProperty(key + ".port", null);
        if (tport != null)
          port.setText(tport);

        String tuser = conProps.getProperty(key + ".user", null);
        if (tuser != null)
          user.setText(tuser);

        String ss = conProps.getProperty(key + ".save", null);
        if (ss != null) {
          if (ss.equals("true")) {
            pass.setText(conProps.getProperty(key + ".pass", ""));
            save.setSelected(true);
          } else {
            pass.setText("");
            save.setSelected(false);
          }
        }
      }
    });
    con.add(cid, c);

    c.gridy = 4;
    c.gridx = 1;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    con.add(new JLabel("DB Host"), c);

    c.gridx = 2;
    c.weightx = 1;
    con.add(host, c);

    c.gridy = 5;
    c.gridx = 1;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    con.add(new JLabel("DB Port"), c);

    c.gridx = 2;
    c.weightx = 1;
    con.add(port, c);

    c.gridy = 6;
    c.gridx = 1;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    con.add(new JLabel("DB User"), c);

    c.gridx = 2;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    con.add(user, c);

    c.gridy = 7;
    c.gridx = 1;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    con.add(new JLabel("Password"), c);

    c.gridx = 2;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    con.add(pass, c);

    c.gridy = 8;
    con.add(save, c);

    c.gridy = 9;
    c.gridx = 3;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    con.add(Box.createRigidArea(new Dimension(5, 5)), c);

    c.gridy = 10;
    c.gridx = 1;
    c.gridwidth = 2;
    c.anchor = GridBagConstraints.CENTER;
    JButton conbutton = new JButton("Connect", ConfigTool.dataConnection);
    conbutton.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent ae) {
        if (host.getText().trim().length() <= 0 || port.getText().trim().length() <= 0
            || user.getText().trim().length() <= 0 || pass.getText().trim().length() <= 0)
          return;

        connectFlag = true;
        hostname = host.getText().trim();
        jd.setVisible(false);
      }
    });
    con.add(conbutton, c);

    jd.pack();
    jd.setVisible(true);

  }

  public boolean connect() {
    return connectFlag;
  }

  
  public String getServerHost(){
   return hostname;
  }
      
  public RockFactory initializeRock() throws Exception {

    int iport = Integer.parseInt(port.getText().trim());
    String iuser = user.getText().trim();
    String ipass = pass.getText().trim();

    String url = URL_PREFIX + hostname + ":" + iport;

    RockFactory rf = new RockFactory(url, iuser, ipass, DB_DRIVER, "CTool", true);

    if (rf == null)
      throw new Exception("Unable to initialize DB connection");

    try {
      String connID = (String) cid.getSelectedItem();

      if (connID != null && connID.trim().length() > 0) {
        conProps.setProperty(connID + ".key", connID);
        conProps.setProperty(connID + ".host", hostname);
        conProps.setProperty(connID + ".port", String.valueOf(iport));
        conProps.setProperty(connID + ".user", iuser);

        if (save.isSelected()) {
          conProps.setProperty(connID + ".pass", ipass);
          conProps.setProperty(connID + ".save", "true");
        } else {
          conProps.setProperty(connID + ".pass", "");
          conProps.setProperty(connID + ".save", "false");
        }

        FileOutputStream fos = new FileOutputStream("connection.properties");
        conProps.store(fos, "");
        fos.close();
      }

    } catch (Exception x) {
      System.out.println("Unable to save connection properties: " + x.getMessage());
    }

    return rf;

  }

  /**
   * Initializes DWHRep rockfactory
   */
  public RockFactory initializeDwhrepRock(RockFactory rock) throws Exception {

    RockFactory dwhrepRock = null;
    Meta_databases selO = new Meta_databases(rock);
    selO.setConnection_name("dwhrep");
    selO.setType_name("USER");
    Meta_databasesFactory mdbf = new Meta_databasesFactory(rock, selO);
    Vector dbs = mdbf.get();
    
    if (dbs != null || dbs.size() == 1) {
      Meta_databases repdb = (Meta_databases) dbs.get(0);
 
      String url = repdb.getConnection_string();
      
      if(url.indexOf("repdb") > 0) {
        String f_url = url.substring(0,url.indexOf("repdb"));
        f_url += hostname;
        f_url += url.substring(url.indexOf("repdb")+5);
        url = f_url;
      }
      
      dwhrepRock = new RockFactory(url, repdb.getUsername(), repdb.getPassword(), DB_DRIVER, "CTool", true);

    } else {
      System.out.println("Unable to connect metadata (No dwhrep or multiple dwhreps defined in Meta_databases)");
    }

    return dwhrepRock;
  }
  
  public String getConnectionID() {
    String scid = (String) cid.getSelectedItem();
    if (scid == null || scid.trim().length() <= 0)
      return host.getText().trim();
    else
      return scid;
  }

  public class ConnectionListCellRenderer extends JLabel implements ListCellRenderer, Serializable {

    public ConnectionListCellRenderer() {
      super();

      setOpaque(true);
      setBorder(new EmptyBorder(1, 1, 1, 1));
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
        boolean cellHasFocus) {
      setComponentOrientation(list.getComponentOrientation());
      if (isSelected) {
        setBackground(list.getSelectionBackground());
        setForeground(list.getSelectionForeground());
      } else {
        setBackground(list.getBackground());
        setForeground(list.getForeground());
      }

      String sval = (String) value;
      if (sval != null && sval.length() > 0) {
        setIcon(ConfigTool.dataConnection);
        setText(sval);
      } else {
        setIcon(null);
        setText("");
      }

      setEnabled(list.isEnabled());
      setFont(list.getFont());
      setBorder((cellHasFocus) ? UIManager.getBorder("List.focusCellHighlightBorder") : new EmptyBorder(1, 1, 1, 1));

      return this;
    }

    public void validate() {
    }

    public void revalidate() {
    }

    public void repaint(long tm, int x, int y, int width, int height) {
    }

    public void repaint(Rectangle r) {
    }

    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
      if (propertyName == "text")
        super.firePropertyChange(propertyName, oldValue, newValue);
    }

    public void firePropertyChange(String propertyName, byte oldValue, byte newValue) {
    }

    public void firePropertyChange(String propertyName, char oldValue, char newValue) {
    }

    public void firePropertyChange(String propertyName, short oldValue, short newValue) {
    }

    public void firePropertyChange(String propertyName, int oldValue, int newValue) {
    }

    public void firePropertyChange(String propertyName, long oldValue, long newValue) {
    }

    public void firePropertyChange(String propertyName, float oldValue, float newValue) {
    }

    public void firePropertyChange(String propertyName, double oldValue, double newValue) {
    }

    public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
    }

  }

}
