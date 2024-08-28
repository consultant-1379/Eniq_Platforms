/**
 * 
 */
package com.ericsson.eniq.techpacksdk.view.measurement;

import java.sql.SQLException;
import java.util.Set;
import java.util.Vector;

import ssc.rockfactory.RockException;

import com.distocraft.dc5000.repository.dwhrep.Busyhourplaceholders;
import com.distocraft.dc5000.repository.dwhrep.Measurementtype;
import com.ericsson.eniq.techpacksdk.common.Constants;

/**
 * @author eheijun
 * 
 */
public class MeasurementtypeExt implements java.lang.Cloneable, ssc.rockfactory.RockDBObject {

  private Measurementtype measurementtype;

  private Vector<Object> objBHSupport;

  private Vector<Object> deltaCalcSupport;
  

  private boolean useplaceholders = false;

  Busyhourplaceholders busyhourplaceholders;

  public MeasurementtypeExt(final Measurementtype measurementtype, final Vector<Object> deltaCalcSupport,
      final Vector<Object> objBHSupport, final Busyhourplaceholders busyhourplaceholders) {
    super();
    this.measurementtype = measurementtype;
    this.deltaCalcSupport = deltaCalcSupport;
    this.objBHSupport = objBHSupport;
    this.busyhourplaceholders = busyhourplaceholders;
  }

  public Measurementtype getMeasurementtype() {
    return measurementtype;
  }

  public void setMeasurementtype(final Measurementtype measurementtype) {
    this.measurementtype = measurementtype;
  }

  public Vector<Object> getDeltaCalcSupport() {
    return deltaCalcSupport;
  }

  public void setDeltaCalcSupport(final Vector<Object> deltaCalcSupport) {
    // hack
    measurementtype.setDeltacalcsupport(measurementtype.getDeltacalcsupport());

    this.deltaCalcSupport = deltaCalcSupport;
  }

  public int getBHCustomPlaceholders() {
    if (busyhourplaceholders == null) {
      return Constants.DEFAULT_NUMBER_OF_BH_CUSTOM_PLACE_HOLDERS;
    } else {
      return busyhourplaceholders.getCustomplaceholders();
    }
  }

  public int getBHProductPlaceholders() {
    if (busyhourplaceholders == null) {
      return Constants.DEFAULT_NUMBER_OF_BH_PRODUCT_PLACE_HOLDERS;
    } else {
      return busyhourplaceholders.getProductplaceholders();
    }
  }

  public void setBHCustomPlaceholders(final int custom) {
    // Make a dummy change so that the measurement type is marked as modified.
    measurementtype.setElementbhsupport(measurementtype.getElementbhsupport());

    if (busyhourplaceholders == null) {
      System.out.println("NULL");
    } else {
      busyhourplaceholders.setCustomplaceholders(custom);
    }
  }

  public void setBHProductPlaceholders(final int product) {
    // Make a dummy change so that the measurement type is marked as modified.
    measurementtype.setElementbhsupport(measurementtype.getElementbhsupport());

    if (busyhourplaceholders == null) {
      System.out.println("NULL");
    } else {
      busyhourplaceholders.setProductplaceholders(product);
    }
  }

  public Vector<Object> getObjBHSupport() {
    return objBHSupport;
  }
     
  public void setObjBHSupport(final Vector<Object> objBHSupport) {
    // hack
    measurementtype.setElementbhsupport(measurementtype.getElementbhsupport());

    this.objBHSupport = objBHSupport;
  }

  public void cleanModifiedColumns() {
    measurementtype.cleanModifiedColumns();
  }

  public Set<String> gimmeModifiedColumns() {
    return measurementtype.gimmeModifiedColumns();
  }

  public int deleteDB() throws SQLException, RockException {
    if (useplaceholders) {
      busyhourplaceholders.deleteDB();
    }
    return measurementtype.deleteDB();
  }

  public int insertDB() throws SQLException, RockException {
    if (useplaceholders) {
      busyhourplaceholders.insertDB();
    }
    final int result = measurementtype.insertDB();
    return result;
  }

  public int updateDB() throws SQLException, RockException {
    if (useplaceholders) {
      busyhourplaceholders.updateDB();
    }
    final int result = measurementtype.updateDB();
    return result;
  }

  public void saveToDB() throws SQLException, RockException {
    measurementtype.saveToDB();
    if (useplaceholders) {
      busyhourplaceholders.saveToDB();
    } else {
      // if we don't want to use place holders, lets try to remove the old
      // one...
      try {
        busyhourplaceholders.deleteDB();
      } catch (final Exception e) {
        // ignore exception because we are not sure that this object exists
      }
    }
  }

  public String getTypeid() {
    return measurementtype.getTypeid();
  }

  public String getTypeclassid() {
    return measurementtype.getTypeclassid();
  }

  public String getTypename() {
    return measurementtype.getTypename();
  }

  public String getVendorid() {
    return measurementtype.getVendorid();
  }

  public String getFoldername() {
    return measurementtype.getFoldername();
  }

  public String getDescription() {
    return measurementtype.getDescription();
  }

  public Long getStatus() {
    return measurementtype.getStatus();
  }

  public String getVersionid() {
    return measurementtype.getVersionid();
  }

  public String getObjectid() {
    return measurementtype.getObjectid();
  }

  public String getObjectname() {
    return measurementtype.getObjectname();
  }

  public Integer getObjectversion() {
    return measurementtype.getObjectversion();
  }

  public String getObjecttype() {
    return measurementtype.getObjecttype();
  }

  public String getJoinable() {
    return measurementtype.getJoinable();
  }
  
  public String getRopGrpCell() {
	    return measurementtype.getRopgrpcell();
  }

  public String getSizing() {
    return measurementtype.getSizing();
  }

  public Integer getTotalagg() {
    return measurementtype.getTotalagg();
  }
  
  public Integer getSonAgg() {
	return measurementtype.getSonagg();
  }
  
  public Integer getSonFifteenMinAgg() {
	  return measurementtype.getSonfifteenminagg();
  }

  public Integer getOneminagg() {
    return measurementtype.getOneminagg();
  }

  public Integer getFifteenminagg() {
    return measurementtype.getFifteenminagg();
  }

  public Integer getLoadfile_dup_check() {
    return measurementtype.getLoadfile_dup_check();
  }

  public Integer getEventscalctable() {
    return measurementtype.getEventscalctable();
  }

  public Integer getMixedpartitionstable() {
    return measurementtype.getMixedpartitionstable();
  }

  public Integer getElementbhsupport() {
    return measurementtype.getElementbhsupport();
  }

  public Integer getRankingtable() {
    return measurementtype.getRankingtable();
  }

  public Integer getDeltacalcsupport() {
    return measurementtype.getDeltacalcsupport();
  }

  public Integer getPlaintable() {
    return measurementtype.getPlaintable();
  }

  public String getUniverseextension() {
    return measurementtype.getUniverseextension();
  }

  public Integer getVectorsupport() {
    return measurementtype.getVectorsupport();
  }

  public Integer getDataformatsupport() {
    return measurementtype.getDataformatsupport();
  }

  public Integer getProductPlaceholders() {
    return measurementtype.getVectorsupport();
  }

  public Integer getCustomPlaceholders() {
    return measurementtype.getDataformatsupport();
  }

  public void setTypeid(final String typeid) {
    measurementtype.setTypeid(typeid);
  }

  public void setTypeclassid(final String typeclassid) {
    measurementtype.setTypeclassid(typeclassid);
  }

  public void setTypename(final String typename) {
    measurementtype.setTypename(typename);
  }

  public void setVendorid(final String vendorid) {
    measurementtype.setVendorid(vendorid);
  }

  public void setFoldername(final String foldername) {
    measurementtype.setFoldername(foldername);
  }

  public void setDescription(final String description) {
    measurementtype.setDescription(description);
  }

  public void setStatus(final Long status) {
    measurementtype.setStatus(status);
  }

  public void setVersionid(final String versionid) {
    measurementtype.setVersionid(versionid);
  }

  public void setObjectid(final String objectid) {
    measurementtype.setObjectid(objectid);
  }

  public void setObjectname(final String objectname) {
    measurementtype.setObjectname(objectname);
  }

  public void setObjectversion(final Integer objectversion) {
    measurementtype.setObjectversion(objectversion);
  }

  public void setObjecttype(final String objecttype) {
    measurementtype.setObjecttype(objecttype);
  }

  public void setJoinable(final String joinable) {
    measurementtype.setJoinable(joinable);
  }
  
  public void setRopGrpCell(final String ropGrpCell) {
	    measurementtype.setRopgrpcell(ropGrpCell);
  }

  public void setSizing(final String sizing) {
    measurementtype.setSizing(sizing);
  }

  public void setTotalagg(final Integer totalagg) {
    measurementtype.setTotalagg(totalagg);
  }
  
  public void setSonAgg(final Integer sonagg) {
	measurementtype.setSonagg(sonagg);
  }
  
  public void setSonFifteenMinAgg(final Integer sonFifteenMinAgg) {
	  measurementtype.setSonfifteenminagg(sonFifteenMinAgg);
  }

  public void setOneminagg(final Integer oneminagg) {
    measurementtype.setOneminagg(oneminagg);
  }

  public void setFifteenminagg(final Integer fifteenminagg) {
    measurementtype.setFifteenminagg(fifteenminagg);
  }

  public void setLoadfile_dup_check(final Integer loadFileDupCheck) {
    measurementtype.setLoadfile_dup_check(loadFileDupCheck);
  }

  public void setEventscalctable(final Integer eventcalctable) {
    measurementtype.setEventscalctable(eventcalctable);
  }

  public void setMixedpartitionstable(final Integer mixedpartitionstable) {
    measurementtype.setMixedpartitionstable(mixedpartitionstable);
  }

  public void setElementbhsupport(final Integer elementbhsupport) {
    measurementtype.setElementbhsupport(elementbhsupport);
  }

  public void setRankingtable(final Integer rankingtable) {
    measurementtype.setRankingtable(rankingtable);
  }

  public void setDeltacalcsupport(final Integer deltacalcsupport) {
    measurementtype.setDeltacalcsupport(deltacalcsupport);
  }

  public void setPlaintable(final Integer plaintable) {
    measurementtype.setPlaintable(plaintable);
  }

  public void setUniverseextension(final String universeextension) {
    measurementtype.setUniverseextension(universeextension);
  }

  public void setVectorsupport(final Integer vectorsupport) {
    measurementtype.setVectorsupport(vectorsupport);
  }

  public void setDataformatsupport(final Integer dataformatsupport) {
    measurementtype.setDataformatsupport(dataformatsupport);
  }

  public boolean isNew() {
    return measurementtype.isNew();
  }

  public boolean isUpdated() {
    return measurementtype.isUpdated();
  }

  public Measurementtype getOriginal() {
    return measurementtype.getOriginal();
  }

  public void setBHLevel(final String bhlevel) {
    busyhourplaceholders.setBhlevel(bhlevel);
  }

  @Override
  public Object clone() {
    Object o = null;
    try {
      o = super.clone();
    } catch (final CloneNotSupportedException e) {
    }
    return o;
  }

  public void setUseplaceholders(final boolean b) {
    useplaceholders = b;
  }

}
