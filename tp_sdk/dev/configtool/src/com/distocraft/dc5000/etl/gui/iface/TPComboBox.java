package com.distocraft.dc5000.etl.gui.iface;

import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.distocraft.dc5000.repository.dwhrep.VersioningFactory;

import ssc.rockfactory.RockFactory;


public class TPComboBox extends JComboBox {

  public TPComboBox(RockFactory dwhrepRock) {
    try {
      
      //Query query = session.createQuery("from Versioning");
      //query.setCacheMode(CacheMode.REFRESH);
      //List qList = query.list();

      Versioning aver = new Versioning(dwhrepRock);
      VersioningFactory averf= new VersioningFactory(dwhrepRock,aver);
      List qList = averf.get();
      
      if (!qList.isEmpty()){
       
        DefaultComboBoxModel dcbm = new DefaultComboBoxModel((Versioning[]) qList.toArray(new Versioning[qList.size()]));
        setModel(dcbm);
        setRenderer(new TPRenderer());

      }

    } catch (Exception e) {
      e.printStackTrace();
    }

  }
  
  public void setSelectedVersionID(String versionid){
    
    for (int i = 0; i < this.getItemCount(); i++ ){
      
      Versioning ver = (Versioning) this.getItemAt(i);
      if (ver.getVersionid().equals(versionid)){
        this.setSelectedIndex(i);
        break;
      }
              
    }
    
  }
  
  public TPComboBox(Versioning[] ar) {
    try {
      DefaultComboBoxModel dcbm = new DefaultComboBoxModel(ar);

      setModel(dcbm);
      setRenderer(new TPRenderer());

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  public class TPRenderer extends JLabel implements ListCellRenderer {

    private Border noFocusBorder;
    
    /**
     * Constructs a default renderer object for an item in a list.
     */
    public TPRenderer() {
      super();

      noFocusBorder = new EmptyBorder(1, 1, 1, 1);
      
      setOpaque(true);
      setBorder(noFocusBorder);
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
        boolean cellHasFocus) {
      
      Versioning vers = (Versioning) value;

      setComponentOrientation(list.getComponentOrientation());
      if (isSelected) {
        setBackground(list.getSelectionBackground());
        if(vers.getStatus().intValue() == 1)
          setForeground(list.getSelectionForeground());
        else
          setForeground(Color.gray);
      } else {
        setBackground(list.getBackground());
        if(vers.getStatus().intValue() == 1)
          setForeground(list.getForeground());
        else
          setForeground(Color.gray);
      }

      setText(vers.getTechpack_name() + " " + vers.getTechpack_version());

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
