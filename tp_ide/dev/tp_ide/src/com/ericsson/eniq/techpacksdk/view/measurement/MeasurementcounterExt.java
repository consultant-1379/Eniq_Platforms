/**
 * 
 */
package com.ericsson.eniq.techpacksdk.view.measurement;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.repository.dwhrep.Measurementcounter;
import com.distocraft.dc5000.repository.dwhrep.Measurementvector;


/**
 * @author eheijun
 *
 */
public class MeasurementcounterExt implements ssc.rockfactory.RockDBObject {
  

  private Measurementcounter measurementcounter;
  
  private Vector<Object> vectorcounters;
  
  private MeasurementVectorTableModel vectorcountertablemodel;
  
  public MeasurementcounterExt(RockFactory rockFactory) {
    super();
    this.measurementcounter = new Measurementcounter(rockFactory);
    this.vectorcounters = new Vector<Object>(); 
  }
  
  public MeasurementcounterExt(Measurementcounter measurementcounter, Vector<Object> vectorcounters) {
    super();
    this.measurementcounter = measurementcounter;
    this.vectorcounters = vectorcounters; 
  }

  public Measurementcounter getMeasurementcounter() {
    return measurementcounter;
  }
  
  public void setMeasurementcounter(final Measurementcounter measurementcounter) {
    this.measurementcounter = measurementcounter;
  }

  public Vector<Object> getVectorcounters() {
    return vectorcounters;
  }

  public void setVectorcounters(final Vector<Object> vectorcounters) {
    this.vectorcounters = vectorcounters;
  }

  public void cleanModifiedColumns() {
    measurementcounter.cleanModifiedColumns();
  }

  public int deleteDB() throws SQLException, RockException {
    return measurementcounter.deleteDB();
  }

  public int insertDB() throws SQLException, RockException {
    return measurementcounter.insertDB();
  }

  public int updateDB() throws SQLException, RockException {
    return measurementcounter.updateDB();
  }
  
  public Object clone (){
    return measurementcounter.clone();
  }
  
  public void saveToDB() throws SQLException, RockException {
    measurementcounter.saveToDB();
  }
  
  public Object getClone() {
    Measurementcounter tmp = (Measurementcounter) measurementcounter.clone();
    Vector<Object> vectorcountersCopy = new Vector<Object>();
    for (Iterator<Object> iter = vectorcounters.iterator(); iter.hasNext(); ) {
      Object tmpvc = iter.next();
      if (tmpvc instanceof Measurementvector) {
        Measurementvector measurementvector = (Measurementvector) tmpvc;
        Measurementvector measurementvectorCopy = (Measurementvector) measurementvector.clone();
        measurementvectorCopy.setNewItem(true);
        vectorcountersCopy.add(measurementvectorCopy);
      }
    }
    MeasurementcounterExt copy = new MeasurementcounterExt(tmp, vectorcountersCopy);
    return copy;
  }
  
  public Set<String> gimmeModifiedColumns() {
    return measurementcounter.gimmeModifiedColumns();
  }

  
  public String getTypeid() {
    return measurementcounter.getTypeid();
  }

  
  public void setTypeid(final String typeid) {
    measurementcounter.setTypeid(typeid);
  }

  
  public String getDataname() {
    return measurementcounter.getDataname();
  }

  
  public void setDataname(final String dataname) {
    measurementcounter.setDataname(dataname);
  }

  
  public String getDescription() {
    return measurementcounter.getDescription();
  }

  
  public void setDescription(final String description) {
    measurementcounter.setDescription(description);
  }

  
  public String getTimeaggregation() {
    return measurementcounter.getTimeaggregation();
  }

  
  public void setTimeaggregation(final String timeaggregation) {
    measurementcounter.setTimeaggregation(timeaggregation);
  }

  
  public String getGroupaggregation() {
    return measurementcounter.getGroupaggregation();
  }

  
  public void setGroupaggregation(final String groupaggregation) {
    measurementcounter.setGroupaggregation(groupaggregation);
  }

  
  public String getCountaggregation() {
    return measurementcounter.getCountaggregation();
  }

  
  public void setCountaggregation(final String countaggregation) {
    measurementcounter.setCountaggregation(countaggregation);
  }

  
  public Long getColnumber() {
    return measurementcounter.getColnumber();
  }

  
  public void setColnumber(final Long colnumber) {
    measurementcounter.setColnumber(colnumber);
  }

  
  public String getDatatype() {
    return measurementcounter.getDatatype();
  }

  
  public void setDatatype(final String datatype) {
    measurementcounter.setDatatype(datatype);
  }

  
  public Integer getDatasize() {
    return measurementcounter.getDatasize();
  }

  
  public void setDatasize(final Integer datasize) {
    measurementcounter.setDatasize(datasize);
  }

  
  public Integer getDatascale() {
    return measurementcounter.getDatascale();
  }

  
  public void setDatascale(final Integer datascale) {
    measurementcounter.setDatascale(datascale);
  }

  
  public Integer getIncludesql() {
    return measurementcounter.getIncludesql();
  }

  
  public void setIncludesql(final Integer includesql) {
    measurementcounter.setIncludesql(includesql);
  }

  
  public String getUnivobject() {
    return measurementcounter.getUnivobject();
  }

  
  public void setUnivobject(final String univobject) {
    measurementcounter.setUnivobject(univobject);
  }

  
  public String getUnivclass() {
    return measurementcounter.getUnivclass();
  }

  
  public void setUnivclass(final String univclass) {
    measurementcounter.setUnivclass(univclass);
  }

  
  public String getCountertype() {
    return measurementcounter.getCountertype();
  }

  
  public void setCountertype(final String countertype) {
    measurementcounter.setCountertype(countertype);
  }

  
  public String getCounterprocess() {
    return measurementcounter.getCounterprocess();
  }

  
  public void setCounterprocess(final String counterprocess) {
    measurementcounter.setCounterprocess(counterprocess);
  }

  public void setNewItem(final boolean newItem) {
    measurementcounter.setNewItem(newItem);
  }

  
  public MeasurementVectorTableModel getVectorCounterTableModel() {
    return vectorcountertablemodel;
  }

  
  public void setVectorCounterTableModel(final MeasurementVectorTableModel vectorCounterTableModel) {
    this.vectorcountertablemodel = vectorCounterTableModel;
  }

  public String getDataid() {
    return measurementcounter.getDataid();
  }

  
  public void setDataid(final String dataid) {
    measurementcounter.setDataid(dataid);
  }

}
