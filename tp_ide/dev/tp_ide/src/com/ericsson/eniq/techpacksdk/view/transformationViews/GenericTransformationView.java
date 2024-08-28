package com.ericsson.eniq.techpacksdk.view.transformationViews;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.StringBufferInputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.text.JTextComponent;

import com.distocraft.dc5000.repository.dwhrep.Transformation;

/**
 * This is a view to AlarmMarkupAction. Copyright (c) 1999 - 2007 AB LM Ericsson
 * Oy All rights reserved.
 * 
 * @author etogust
 * 
 */
public class GenericTransformationView {

  protected List<JTextComponent> textFieldContainer = null;

  private Transformation transformation;


  
  public GenericTransformationView() {
    textFieldContainer = new ArrayList<JTextComponent>();
  }
  
  public GenericTransformationView(JPanel parent, Transformation transformation) {
    setTransformation(transformation);
    parent.removeAll();

    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.BOTH;
    c.insets = new Insets(2, 2, 2, 2);
    c.weightx = 1;
    c.weighty = 1;


    JLabel label = new JLabel();
    label.setText("This transformation does not take any parameters");
    parent.add(label, c);
  }  
  
  /**
   * Gets orig value
   * 
   * @param name
   * @return
   */
  protected String getOrigValue(String name) {    


    if (getTransformation () == null)
      return "";

    String config = transformation.getConfig();
    if (config == null)
      return "";
    
    StringReader sr = new StringReader (config);
    Properties properties = new Properties ();
    try {
      // TODO::TGU. Remove this quick dirty kludge, added in order to make this work with java 5
      StringBufferInputStream sbis = new StringBufferInputStream(config);
      properties.load (sbis);
    } catch (Exception e) {
      // TODO: handle exception
    } 
    
    Set keys = properties.keySet();
    for (Object o : keys){
      String key = (String)o;
      
      if (key.equals(name)){
        String value = config.substring(config.indexOf(key+"=") + key.length()+1);
        Pattern p = Pattern.compile("[^\\\\]\n");
        Matcher m = p.matcher(value);
        if (m.find())
          value = value.substring(0, m.end() -1 );
        value = value.replaceAll("[\\\\]\n", "\n");
        return value;
      }
    }
    return "";
  }

  /**
   * 
   * @return
   */
  public String validate() {
   return "";
  }

  /**
   * Gets content of transformation->config
   */
  public String getContent() throws Exception {

    StringBuilder sb = new StringBuilder();
    if (textFieldContainer!=null){
    for (JTextComponent tf : textFieldContainer) {

      if (tf.getText().equals(""))
        continue;

      sb.append(tf.getName() + "=");
      sb.append(tf.getText());
      sb.append("\n");
    }
    }
    return sb.toString();
  }

  private Transformation getTransformation (){
    return this.transformation;
  }

  protected void setTransformation (Transformation transformation){
    this.transformation = transformation;
  }
  
  public String getWhere() throws Exception {
    return "";
  }

  public boolean isChanged() {
    return true;
  }

}
