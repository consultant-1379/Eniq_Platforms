package com.ericsson.eniq.techpacksdk.view.transformationViews;



/**
 * Copyright Distocraft 2005<br>
 * <br>
 * $id$
 *
 * @author lemminkainen
 */
public interface TransformationView {
  
  public String getType();
  public String validate();
  public String getContent() throws Exception;
  public String getWhere() throws Exception;
  public boolean isChanged();

}
