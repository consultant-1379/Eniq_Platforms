package com.ericsson.eniq.techpacksdk.view.actionViews;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.ericsson.eniq.component.ExceptionHandler;

/**
 * Copyright Distocraft 2005<br>
 * <br>
 * $id$
 * 
 * @author lemminkainen
 */
public class UpdateDimSessionActionView implements ActionView {

  // private JTextField source;
  // private Meta_transfer_actions action;
  private JTextField element;

  private GenericPropertiesView gpv;

  public UpdateDimSessionActionView(JPanel parent, Meta_transfer_actions action, JDialog pdialog) {
    parent.removeAll();

    Set s = new HashSet();
    Properties orig = new Properties();

    if (action != null) {

      String where = action.getWhere_clause();

      if (where != null && where.length() > 0) {

        try {
          ByteArrayInputStream bais = new ByteArrayInputStream(where.getBytes());
          orig.load(bais);
          bais.close();
        } catch (Exception e) {
          ExceptionHandler.instance().handle(e);
          e.printStackTrace();
        }
      }
    }

    // for hidden values.

    gpv = new GenericPropertiesView(orig, s, pdialog);

    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2, 2, 2, 2);

    c.weightx = 0;
    c.weighty = 0;
    JLabel l_element = new JLabel("Element");
    l_element.setToolTipText("Defines the element that is used when updating DimSession");
    parent.add(l_element, c);

    element = new JTextField(orig.getProperty("element", ""), 20);
    element.setToolTipText("Defines the element that is used when updating DimSession");
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    c.gridx = 1;
    parent.add(element, c);

    parent.invalidate();
    parent.revalidate();
    parent.repaint();
  }

  public String getType() {

    return "UpdateDimSession";
  }

  public String getContent() throws Exception {
    return "";
  }

  public String getWhere() throws Exception {

    Properties p = gpv.getProperties();
    p.setProperty("element", element.getText().trim());

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    p.store(baos, "");

    return baos.toString();

  }

  public boolean isChanged() {
    return true;
  }

  public String validate() {
    return "";
  }

}
