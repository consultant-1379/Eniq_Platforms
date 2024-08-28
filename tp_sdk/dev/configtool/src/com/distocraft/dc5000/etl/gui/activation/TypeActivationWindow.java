package com.distocraft.dc5000.etl.gui.activation;

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
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.gui.ConfigTool;
import com.distocraft.dc5000.etl.gui.ErrorDialog;
import com.distocraft.dc5000.repository.dwhrep.Typeactivation;

public class TypeActivationWindow extends JDialog {

  private Typeactivation typeActivation = null;

  private JComboBox status = null;

  private JTextField storageTime = null;

  private boolean commit = false;

  TypeActivationWindow(JFrame parent, RockFactory dwhrepRockFactory, Typeactivation typeActivation) {
    super(parent, true);

    this.typeActivation = typeActivation;

    try {

      setTitle("TypeActivation " + this.typeActivation.getTypename());

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
      con.add(new JLabel(this.typeActivation.getTypename()), c);

      c.gridx = 1;
      c.gridy = 2;
      con.add(new JLabel("Level"), c);

      c.gridx = 2;
      con.add(new JLabel(this.typeActivation.getTablelevel()), c);

      c.gridx = 1;
      c.gridy = 3;
      con.add(new JLabel("Storagetime"), c);

      c.gridx = 2;

      storageTime = new JTextField(this.typeActivation.getStoragetime().toString(),5);
      con.add(storageTime, c);

      c.gridx = 1;
      c.gridy = 4;
      con.add(new JLabel("Status"), c);
      
      c.gridx = 2;

      String[] statusOptions = { new String("INACTIVE"), new String("ACTIVE") };
      status = new JComboBox(statusOptions);
      if (this.typeActivation.getStatus().equalsIgnoreCase("ACTIVE"))
        status.setSelectedIndex(1);
      else
        status.setSelectedIndex(0);

      status.setRenderer(new StatusRenderer());
      con.add(status, c);

      c.gridx = 1;
      c.gridy = 5;
      con.add(new JLabel("Type"), c);
      
      c.gridx = 2;

      con.add(new JLabel(this.typeActivation.getType()), c);

      c.gridx = 1;
      c.gridy = 6;
      con.add(Box.createRigidArea(new Dimension(5, 5)), c);

      c.gridx = 1;
      c.gridy = 7;

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

          try {
            Long.parseLong(storageTime.getText());
          } catch ( NumberFormatException e ) {
            error += "Storagetime must be a number"; 
          }
          
          /*
          if(TypeActivationWindow.this.storageTime.getText().equals("")) {
            error += "Storagetime must be a number";
          }
          */
          
          if (error.length() > 0) {
            JOptionPane.showMessageDialog(TypeActivationWindow.this, error, "Invalid configuration",
                JOptionPane.ERROR_MESSAGE);
            
            ConfigTool.reloadConfig();
            
            return;
          }

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

      boolean newActivation = false;

      // Start saving the values of this TypeActivation to database.

      Long storageTimeValue = new Long( this.storageTime.getText() );
      
      
      this.typeActivation.setStoragetime(storageTimeValue);

      this.typeActivation.setStatus((String) this.status.getSelectedItem().toString().toUpperCase());

      this.typeActivation.updateDB();
      
      // Reload the changes from the database.
      ConfigTool.reloadConfig();

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

      String status = (String) value;

      setComponentOrientation(list.getComponentOrientation());
      if (isSelected) {
        setBackground(list.getSelectionBackground());
        setForeground(list.getSelectionForeground());
      } else {
        setBackground(list.getBackground());
        setForeground(list.getForeground());
      }

      setText(status);

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
