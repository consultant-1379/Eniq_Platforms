package com.distocraft.dc5000.etl.gui.set.actionview.parseview;

import java.util.Properties;
import java.util.Set;

public interface ParseView {

  /**
   * Returns a set of parameter names that ParseView represents.
   * 
   * @return Set of parameter names
   */
  Set getParameterNames();

  /**
   * Sets ParseView specific parameters to a properties object.
   * 
   * @param p
   *          Properties object
   */
  void fillParameters(Properties p);

  String validate();
  
}
