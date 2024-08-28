package com.ericsson.navigator.esm.util.cssr.io;

import java.util.ArrayList;
import java.util.List;

/**
 * Class represents the components that a CSElement object can contain, including hierarchy 
 * information.
 * 
 * @author ejammor
 *
 */
public class CSComponents {
   private List<NameTypePair> hierarchy = null;//NOPMD
   private List<NameTypePair> components = null;//NOPMD
   private int hierarchyCounter = 0;
   private int componentCounter = 0;
      
   public CSComponents() {
	   hierarchy = new ArrayList<NameTypePair>();
	   components = new ArrayList<NameTypePair>();
   }
   
   public void addHierarchy(final NameTypePair pair) {
	   hierarchy.add(pair);
   }
   
   public void addComponent(final NameTypePair pair) {
	   components.add(pair);
   }
   
   public NameTypePair peekHierachy() {
	   return nextHierarchyHelper(false);
   }
   
   public NameTypePair nextComponent() {
	   return nextComponentHelper(true);
   }
   
   public NameTypePair peekComponent() {
	   return nextComponentHelper(false);
   }
   
   public NameTypePair nextHierachy() {
	   return nextHierarchyHelper(true);
   }
   
   // need reuse
   public void resetHierarchy() {
	   hierarchyCounter = 0;
   }
   
   public void resetComponent() {
	   componentCounter = 0;
   }
   
   private NameTypePair nextHierarchyHelper(final boolean increment) {
	   NameTypePair next = null;
			
	  if(hierarchyCounter<hierarchy.size()) {
		 next = hierarchy.get(hierarchyCounter);
		 if(increment) {
			hierarchyCounter++;
		 }
	  }
	  return next;
	}
   
   private NameTypePair nextComponentHelper(final boolean increment) {
	   NameTypePair next = null;
			
	  if(componentCounter<components.size()) {
		 next = components.get(componentCounter);
		 if(increment) {
			 componentCounter++;
		 }
	  }
	  return next;
	}
}