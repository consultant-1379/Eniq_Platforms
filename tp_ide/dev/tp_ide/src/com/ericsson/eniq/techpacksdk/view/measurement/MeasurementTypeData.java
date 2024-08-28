/**
 * 
 */
package com.ericsson.eniq.techpacksdk.view.measurement;

import java.util.Vector;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.repository.dwhrep.Busyhourplaceholders;
import com.distocraft.dc5000.repository.dwhrep.Measurementkey;
import com.distocraft.dc5000.repository.dwhrep.Measurementtype;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.techpacksdk.common.Constants;

/**
 * @author eheijun
 * 
 */
public class MeasurementTypeData {

//  private static final Logger logger = Logger.getLogger(MeasurementTypeData.class.getName());
  
  private Versioning versioning;
  
  private MeasurementtypeExt measurementtypeext;
  
  private Vector<Object> measurementkeys;
  private Vector<Object> measurementcounterExts;
  
  private RockFactory rockFactory;

  public final static String DEFAULT_NEW_NAME = "NEW_MEASUREMENT_TYPE";
  
  public final static String TYPECLASSID = "DEFAULT";
  
  public final static String RAW = "RAW";

  public final static String DAY = "DAY";

  public final static String DAYBH = "DAYBH";

  public final static String COUNT = "COUNT";

  public final static String PLAIN = "PLAIN";

  public final static String RANKBH = "RANKBH";

  public MeasurementTypeData(Versioning versioning, RockFactory rockFactory) {
    this.rockFactory = rockFactory;
    this.versioning = versioning;
    final String typeclassid = this.versioning.getVersionid() + ":" + this.versioning.getTechpack_name() + "_" + TYPECLASSID;
    final String typename = DEFAULT_NEW_NAME;
    final String typeid = this.versioning.getVersionid() + ":" + typename;
    final String vendorid = this.versioning.getTechpack_name();
    final String versionid = this.versioning.getVersionid();
    
    Busyhourplaceholders busyhourplaceholders = new Busyhourplaceholders(this.rockFactory);
    busyhourplaceholders.setVersionid(versionid);
    busyhourplaceholders.setBhlevel(typeid);
    busyhourplaceholders.setProductplaceholders(Constants.DEFAULT_NUMBER_OF_BH_PRODUCT_PLACE_HOLDERS);
    busyhourplaceholders.setCustomplaceholders(Constants.DEFAULT_NUMBER_OF_BH_CUSTOM_PLACE_HOLDERS);
    
    Measurementtype measurementtype = new Measurementtype(this.rockFactory);
    measurementtype.setTypeid(typeid);
    measurementtype.setTypeclassid(typeclassid);
    measurementtype.setTypename(typename);
    measurementtype.setVendorid(vendorid);
    measurementtype.setVersionid(versionid);
    measurementtype.setObjectid(typeid);
    measurementtype.setObjectname(typename);
    measurementtype.setJoinable("");
    measurementtype.setRopgrpcell("");
    measurementtype.setSizing("medium");
    measurementtype.setTotalagg(0);
    measurementtype.setSonagg(0);
    measurementtype.setElementbhsupport(0);
    measurementtype.setRankingtable(0);
    measurementtype.setDeltacalcsupport(0);
    measurementtype.setPlaintable(0);
    measurementtype.setUniverseextension("ALL");
    this.measurementtypeext = new MeasurementtypeExt(measurementtype, new Vector<Object>(), new Vector<Object>(),busyhourplaceholders);
    this.measurementkeys = new Vector<Object>();
    this.measurementcounterExts = new Vector<Object>();
    measurementtype.setDataformatsupport(1);
    measurementtype.setFoldername(typename);
    measurementtype.setDescription("");
    
  }

  public MeasurementTypeData(Versioning versioning, MeasurementtypeExt measurementtypeext, 
      Vector<Measurementkey> measurementkeys, Vector<MeasurementcounterExt> measurementcounterExt,
      RockFactory rockFactory) {
    this.rockFactory = rockFactory;
    this.versioning = versioning;
    this.measurementtypeext = measurementtypeext;
    if (measurementkeys == null) {
      this.measurementkeys = new Vector<Object>();
    } else {
      this.measurementkeys = new Vector<Object>(measurementkeys);
    }
    if (measurementcounterExt == null) {
      this.measurementcounterExts = new Vector<Object>();
    } else {
      this.measurementcounterExts = new Vector<Object>(measurementcounterExt);
    }
  }
  
  public Versioning getVersioning() {
    return versioning;
  }
  
  public void setVersioning(final Versioning versioning) {
    this.versioning = versioning;
  }

  public MeasurementtypeExt getMeasurementtypeExt() {
    return measurementtypeext;
  }
  
  public void setMeasurementtypeExt(final MeasurementtypeExt measurementtypeext) {
    this.measurementtypeext = measurementtypeext;
  }
  
  public Vector<Object> getMeasurementkeys() {
    return measurementkeys;
  }
  
  public void setMeasurementkeys(final Vector<Object> measurementkeys) {
    this.measurementkeys = measurementkeys;
  }
  
  public Vector<Object> getMeasurementcounterExts() {
    return measurementcounterExts;
  }

  public void setMeasurementcounterExts(final Vector<Object> measurementcounterExts) {
    this.measurementcounterExts = measurementcounterExts;
  }

  public RockFactory getRockFactory() {
    return rockFactory;
  }

  public void setRockFactory(final RockFactory rockFactory) {
    this.rockFactory = rockFactory;
  }
  
}
