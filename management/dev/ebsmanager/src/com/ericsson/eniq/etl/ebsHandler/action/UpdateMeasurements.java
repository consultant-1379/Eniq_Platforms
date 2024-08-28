package com.ericsson.eniq.etl.ebsHandler.action;

import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Logger;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.repository.dwhrep.Measurementtype;
import com.distocraft.dc5000.repository.dwhrep.MeasurementtypeFactory;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.distocraft.dc5000.repository.dwhrep.VersioningFactory;
import com.ericsson.eniq.techpacksdk.view.measurement.MeasurementTypeDataModel;
import com.ericsson.eniq.techpacksdk.view.measurement.MeasurementtypeExt;


public class UpdateMeasurements {

  private static final Logger logger = Logger.getLogger(UpdateMeasurements.class.getName());

  private RockFactory rockFactory = null;

  private Versioning currentVersioning;

  private String typename;

  private MeasurementTypeDataModel measurementTypeDataModel;
  
  
  public UpdateMeasurements(String typename, Versioning currentVersioning, Versioning baseVersioning, RockFactory rockFactory){
    this.rockFactory = rockFactory;
    this.typename = typename;
    this.currentVersioning = currentVersioning;
    measurementTypeDataModel = new MeasurementTypeDataModel(rockFactory);
    measurementTypeDataModel.setBaseVersioning(baseVersioning);
    measurementTypeDataModel.setCurrentVersioning(currentVersioning);
  }
  
  private Vector<Versioning> getAllVersions(String tpName) {

    Vector<Versioning> versions = new Vector<Versioning>();

    try {

      Versioning m = new Versioning(rockFactory, true);
      m.setTechpack_name(tpName);
      VersioningFactory mF = new VersioningFactory(rockFactory, m, true);
      versions = mF.get();

    } catch (Exception e) {

      e.printStackTrace();
    }

    return versions;
  }
  
  private Vector<MeasurementtypeExt> getAllMeasurementtypeExts(Versioning ver) {

    Vector<MeasurementtypeExt> root = new Vector<MeasurementtypeExt>();

    Iterator<Versioning> iter = getAllVersions(ver.getTechpack_name()).iterator();

    while (iter.hasNext()) {

      Versioning v = (Versioning) iter.next();

      try {

        Measurementtype mt = new Measurementtype(rockFactory);
        mt.setVersionid(v.getVersionid());
        MeasurementtypeFactory mtF = new MeasurementtypeFactory(rockFactory, mt);

        MeasurementtypeExt mte = new MeasurementtypeExt(mtF.getElementAt(0), null, null, null);

        root.add(mte);

      } catch (Exception e) {

      }
    }

    return root;
  }
  
  public void update() throws Exception {
   
    final Vector<MeasurementtypeExt> alltypes = getAllMeasurementtypeExts(currentVersioning);

    Measurementtype mt = new Measurementtype(rockFactory);
    mt.setVersionid(currentVersioning.getVersionid());
    mt.setTypename(typename);

    MeasurementtypeFactory mtF = new MeasurementtypeFactory(rockFactory, mt);

    Measurementtype newMt = mtF.getElementAt(0);
    MeasurementtypeExt mte = new MeasurementtypeExt(newMt, new Vector<Object>(), new Vector<Object>(), null);

    measurementTypeDataModel.refresh();
    measurementTypeDataModel.deleteGenerated(false, mte);
    measurementTypeDataModel.createGenerated(mte, alltypes);

  }
}
