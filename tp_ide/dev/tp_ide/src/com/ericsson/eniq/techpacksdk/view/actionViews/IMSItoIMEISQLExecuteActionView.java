package com.ericsson.eniq.techpacksdk.view.actionViews;

import javax.swing.JPanel;

import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.ericsson.eniq.techpacksdk.common.Constants;

public class IMSItoIMEISQLExecuteActionView extends UnknownTopologySQLExecuteActionView {

  public IMSItoIMEISQLExecuteActionView(JPanel parent, Meta_transfer_actions action) {
    super(parent, action);
  }

  @Override
  public String getType() {
    return Constants.IMSI_TO_IMEI;
  }

}
