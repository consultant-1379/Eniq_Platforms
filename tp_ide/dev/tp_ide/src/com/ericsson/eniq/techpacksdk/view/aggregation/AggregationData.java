/**
 * 
 */
package com.ericsson.eniq.techpacksdk.view.aggregation;

import java.util.Vector;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.repository.dwhrep.Aggregation;
import com.distocraft.dc5000.repository.dwhrep.Aggregationrule;
import com.distocraft.dc5000.repository.dwhrep.Versioning;


/**
 * @author eheijun
 *
 */
public class AggregationData {

  private Versioning currentVersioning;
  private Aggregation aggregation;
  private Vector<Object> rules;

  public AggregationData(Versioning currentVersioning, Aggregation aggregation, Vector<Aggregationrule> rules,
      RockFactory rockFactory) {
    this.currentVersioning = currentVersioning;
    this.aggregation = aggregation;
    if (rules == null) {
      this.rules = new Vector<Object>();
    } else {
      this.rules = new Vector<Object>(rules);
    }
  }

  
  public AggregationData(Versioning versioning, RockFactory rockFactory) {
    this.currentVersioning = versioning;
    this.aggregation = new Aggregation(rockFactory);
    this.rules = new Vector<Object>();
  }


  public Versioning getVersioning() {
    return currentVersioning;
  }

  
  public void setVersioning(Versioning currentVersioning) {
    this.currentVersioning = currentVersioning;
  }

  
  public Aggregation getAggregation() {
    return aggregation;
  }

  
  public void setAggregation(Aggregation aggregation) {
    this.aggregation = aggregation;
  }

  
  public Vector<Object> getRules() {
    return rules;
  }

  
  public void setRules(Vector<Object> rules) {
    this.rules = rules;
  }
}
