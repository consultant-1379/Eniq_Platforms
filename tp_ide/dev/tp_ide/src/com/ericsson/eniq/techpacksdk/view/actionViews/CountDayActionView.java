/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.techpacksdk.view.actionViews;

import javax.swing.JPanel;

import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.ericsson.eniq.techpacksdk.common.Constants;


/**
 * Used for creating CountDayAction for data-tiering
 * @author epaujor
 * @since 2012
 *
 */
public class CountDayActionView extends CountActionView {

  /**
   * @param parent
   * @param action
   */
  public CountDayActionView(final JPanel parent, final Meta_transfer_actions action) {
    super(parent, action);
  }
  
  @Override
  public String getType() {
    return Constants.COUNTING_DAY_ACTION;
  }
}
