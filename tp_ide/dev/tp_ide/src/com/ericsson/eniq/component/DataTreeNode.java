package com.ericsson.eniq.component;

import ssc.rockfactory.RockDBObject;

public class DataTreeNode extends GenericActionNode {


    public boolean active = false;
    public String locked = null;
    String name = "";
    private final RockDBObject rObj;

    
    public DataTreeNode(final String str, final RockDBObject rObj, final boolean active, final String locked) {
      super();
      this.active = active;
      this.locked = locked; 
      this.name = str;
      this.rObj = rObj;
    }     
          
    public String toString(){
      return name;
    }
      
    public RockDBObject getRockDBObject(){
      return rObj;
    }
    
    public boolean isActive(){
      return active;
    }
}
