package com.ericsson.eniq.techpacksdk.datamodel;

import ssc.rockfactory.RockDBObject;
import ssc.rockfactory.RockFactory;

public interface DataModel {

public void refresh();
public RockFactory getRockFactory();

// TODO force set controller
//public void setDataModelController(DataModelController dataModelController);

public void save() throws Exception;

public boolean validateNew(RockDBObject rObj);
public boolean validateDel(RockDBObject rObj);
public boolean validateMod(RockDBObject rObj);

public boolean newObj(RockDBObject rObj);
public boolean delObj(RockDBObject rObj);
public boolean modObj(RockDBObject rObj);
public boolean modObj(RockDBObject rObj[]);
  
public boolean updated(DataModel dataModel) throws Exception;

}
