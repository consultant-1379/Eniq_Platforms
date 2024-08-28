package com.distocraft.dc5000.etl.gui.iface;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import ssc.rockfactory.RockFactory;


import com.distocraft.dc5000.etl.gui.ConfigTool;
import com.distocraft.dc5000.etl.gui.ErrorDialog;
import com.distocraft.dc5000.repository.dwhrep.Datainterface;


public class InterfaceWindow extends JDialog {

  private RockFactory dwhrepRock;

  private Datainterface di;

  private JTextField ifname = null;

  private JComboBox status = null;

  private JComboBox iftype = null;
  
  private JTextArea comment = null;

  private boolean commit = false;

  InterfaceWindow(JFrame parent, RockFactory dwhrepRock, Datainterface di) {
    super(parent, true);

    this.dwhrepRock = dwhrepRock;
    this.di = di;

    try {

      if (di == null)
        setTitle("New Interface");
      else
        setTitle("Interace " + di.getInterfacename());

      final Container con = getContentPane();
      con.setLayout(new GridBagLayout());
      GridBagConstraints c = new GridBagConstraints();

      c.anchor = GridBagConstraints.NORTHWEST;
      c.fill = GridBagConstraints.NONE;
      c.weightx = 0;
      c.weighty = 0;
      c.insets = new Insets(2, 4, 2, 2);

      con.add(Box.createRigidArea(new Dimension(5, 5)), c);

      c.gridy = 1;
      c.gridx = 1;
      con.add(new JLabel("Name"), c);

      c.gridx = 2;
      if (di == null) {
        ifname = new JTextField(15);
        con.add(ifname, c);
      } else {
        con.add(new JLabel(di.getInterfacename()), c);
      }

      c.gridx = 1;
      c.gridy = 2;
      con.add(new JLabel("Status"), c);

      c.gridx = 2;
      Integer[] stats = { new Integer(0), new Integer(1) };
      status = new JComboBox(stats);
      if(di != null && di.getStatus().intValue() == 1)
        status.setSelectedIndex(1);
      else
        status.setSelectedIndex(0);
        
      status.setRenderer(new StatusRenderer());
      con.add(status, c);

      c.gridx = 1;
      c.gridy = 3;
      con.add(new JLabel("Type"), c);
      
      c.gridx = 2;
      iftype = new JComboBox(ConfigTool.ADAPTER_TYPES);
      iftype.setEditable(true);
      if(di != null && di.getInterfacetype() != null) {
        iftype.setSelectedItem(di.getInterfacetype());
      }
      con.add(iftype,c);
      
      c.gridx = 1;
      c.gridy = 4;
      con.add(new JLabel("Description"), c);

      c.gridx = 2;
      comment = new JTextArea(4, 25);
      comment.setLineWrap(true);
      comment.setWrapStyleWord(true);
      if(di != null && di.getDescription() != null)
        comment.setText(di.getDescription());
      con.add(comment, c);
      
      c.gridx = 1;
      c.gridy = 5;
      con.add(Box.createRigidArea(new Dimension(5, 5)), c);

      c.gridx = 1;
      c.gridy = 6;
      JButton discard = new JButton("Discard", ConfigTool.delete);
      discard.addActionListener(new ActionListener() {

        public void actionPerformed(ActionEvent ae) {
          commit = false;
          setVisible(false);
        }
      });
      con.add(discard, c);

      c.gridx = 2;
      JButton save = new JButton("Save", ConfigTool.check);
      save.addActionListener(new ActionListener() {

        public void actionPerformed(ActionEvent ae) {

          String error = "";

          if(ifname != null && ifname.getText().trim().length() <= 0)
            error += "Name must be defined\n";

          if (error.length() > 0) {
            JOptionPane.showMessageDialog(InterfaceWindow.this, error, "Invalid configuration", JOptionPane.ERROR_MESSAGE);
            return;
          }

          ConfigTool.reloadConfig();
          commit = true;
          setVisible(false);
        }
      });
      con.add(save, c);

      c.gridx = 3;
      c.gridy = 7;
      c.insets = new Insets(0, 0, 0, 0);
      con.add(Box.createRigidArea(new Dimension(5, 5)), c);

      pack();
      setVisible(true);

      if (!commit)
        return;

      if (di == null)
        di = new Datainterface(dwhrepRock);
      
      if(ifname != null)
        di.setInterfacename(ifname.getText().trim());
      
      di.setStatus(new Long(status.getSelectedIndex()));
      
      di.setInterfacetype((String)iftype.getSelectedItem());
      
      di.setDescription(comment.getText().trim());
      
      if(ifname == null) {
        di.updateDB();
      } else {
        di.insertDB();
      }
      //di.saveDB();
      
    } catch (Exception e) {
      ErrorDialog ed = new ErrorDialog(parent, "Error", "Unable to save DB", e);
    }

  }
  
  public boolean committed() {
    return commit;
  }

  public class StatusRenderer extends JLabel implements ListCellRenderer {

    private Border noFocusBorder;

    /**
     * Constructs a default renderer object for an item in a list.
     */
    public StatusRenderer() {
      super();

      noFocusBorder = new EmptyBorder(1, 1, 1, 1);

      setOpaque(true);
      setBorder(noFocusBorder);
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
        boolean cellHasFocus) {

      Integer stat = (Integer) value;

      setComponentOrientation(list.getComponentOrientation());
      if (isSelected) {
        setBackground(list.getSelectionBackground());
        setForeground(list.getSelectionForeground());
      } else {
        setBackground(list.getBackground());
        setForeground(list.getForeground());
      }

      if (stat.intValue() == 1)
        setText("enabled");
      else
        setText("disabled");

      setEnabled(list.isEnabled());
      setFont(list.getFont());
      setBorder((cellHasFocus) ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);

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

  };
  
}
