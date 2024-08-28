package com.ericsson.eniq.techpacksdk.view.measurement;

import java.util.List;

import org.jdesktop.application.Application;

import ssc.rockfactory.RockFactory;
import tableTreeUtils.TCTableModel;

import com.distocraft.dc5000.repository.dwhrep.Measurementtypeclass;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.techpacksdk.common.Constants;

public class MeasurementTypeParamModelFactory {

  public static MeasurementTypeParameterModel createMeasurementTypeParamModel(final Application application,
      final Versioning versioning, final List<Measurementtypeclass> mtypeclasses,
      final MeasurementtypeExt measurementtypeExt, final TCTableModel dcsupportTableModel,
      final TCTableModel objectbhTableModel, final String[] universeExtensions, final RockFactory rockFactory,
      final boolean isTreeEditable) {

    if (versioning.getTechpack_type().equals(Constants.ENIQ_EVENT)) {
      return new EventMeasurementTypeParameterModel(application, versioning, mtypeclasses, measurementtypeExt,
          dcsupportTableModel, objectbhTableModel, universeExtensions, rockFactory, isTreeEditable);
    }

    return new StatsMeasurementTypeParameterModel(application, versioning, mtypeclasses, measurementtypeExt,
        dcsupportTableModel, objectbhTableModel, universeExtensions, rockFactory, isTreeEditable);

  }
}
