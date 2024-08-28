package com.ericsson.eniq.techpacksdk.view.schedulingViews;

import com.distocraft.dc5000.etl.rock.Meta_schedulings;


/**
 * Copyright Distocraft 2005<br>
 * <br>
 * $id$
 *
 * @author lemminkainen
 */
public interface ScheduleView {

  public void fill(Meta_schedulings meta);
  public String validate();
  
}
