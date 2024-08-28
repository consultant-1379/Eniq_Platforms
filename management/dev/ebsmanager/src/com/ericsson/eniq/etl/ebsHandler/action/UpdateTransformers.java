package com.ericsson.eniq.etl.ebsHandler.action;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.techpacksdk.view.transformer.TransformerDataModel;

public class UpdateTransformers {
  
  public void update(Versioning currentVersioning, RockFactory rockFactory) throws Exception {
   
    TransformerDataModel transformerDataModel = new TransformerDataModel(rockFactory);
    transformerDataModel.setCurrentVersioning(currentVersioning);

    transformerDataModel.refresh();
    transformerDataModel.updateTransformations(false, "", "");
    
  }
}
