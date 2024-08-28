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
 * Used for creating CountTriggerfor data-tiering
 * @author epaujor
 * @since 2012
 *
 */
public class CountDayTriggerActionView extends CountTriggerActionView {

  /**
   * @param parent
   * @param action
   */
  public CountDayTriggerActionView(final JPanel parent, final Meta_transfer_actions action) {
    super(parent, action);
  }
  
  @Override
  public String getType() {
    return Constants.COUNTING_DAY_TRIGGER;
  }
}
