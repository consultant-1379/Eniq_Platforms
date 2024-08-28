package com.ericsson.eniq.techpacksdk.view.measurement;

import java.util.List;

import org.jdesktop.application.Application;

import ssc.rockfactory.RockFactory;
import tableTreeUtils.TCTableModel;

import com.distocraft.dc5000.repository.dwhrep.Measurementtypeclass;
import com.distocraft.dc5000.repository.dwhrep.Versioning;

public class StatsMeasurementTypeParameterModel extends MeasurementTypeParameterModel {

  public StatsMeasurementTypeParameterModel(final Application application, final Versioning versioning,
      final List<Measurementtypeclass> mtypeclasses, final MeasurementtypeExt measurementtypeExt,
      final TCTableModel dcsupportTableModel, final TCTableModel objectbhTableModel, final String[] universeExtensions,
      final RockFactory rockFactory, final boolean isTreeEditable) {
    super(application, versioning, mtypeclasses, measurementtypeExt, dcsupportTableModel, objectbhTableModel,
        universeExtensions, rockFactory, isTreeEditable);
  }

}
