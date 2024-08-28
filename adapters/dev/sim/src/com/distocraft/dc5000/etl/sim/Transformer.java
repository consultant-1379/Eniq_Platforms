package com.distocraft.dc5000.etl.sim;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created on Jan 31, 2005
 * 
 * @author lemminkainen
 */
public interface Transformer {

  void transform(Map data, Logger clog) throws Exception;
  void addDebugger(SimDebugger simDebugger);
  List getTransformations();
  
}
