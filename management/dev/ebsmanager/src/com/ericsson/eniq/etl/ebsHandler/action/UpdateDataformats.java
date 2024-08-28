package com.ericsson.eniq.etl.ebsHandler.action;



import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.techpacksdk.view.dataFormat.DataformatDataModel;


public class UpdateDataformats {
  
  public void update(final Versioning currentVersioning, final RockFactory rockFactory) throws Exception {

    final DataformatDataModel dataformatDataModel = new DataformatDataModel(rockFactory, null);
    dataformatDataModel.setVersionid(currentVersioning.getVersionid());
    dataformatDataModel.setDataformatsRenamed(false);
    dataformatDataModel.refresh();
    dataformatDataModel.updateDataformats();    
  }
}
