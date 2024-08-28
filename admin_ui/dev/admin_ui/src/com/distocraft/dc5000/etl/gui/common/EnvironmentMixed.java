/**
 * 
 */
package com.distocraft.dc5000.etl.gui.common;


/**
 * @author eheijun
 *
 */
public class EnvironmentMixed implements Environment {

  private static final String DWH_NAME = "dwh_coor";

  /* (non-Javadoc)
   * @see com.distocraft.dc5000.etl.gui.common.Environment#getType()
   */
  @Override
  public Type getType() {
    return Type.MIXED;
  }

  /* (non-Javadoc)
   * @see com.distocraft.dc5000.etl.gui.common.Environment#getDwhName()
   */
  @Override
  public String getDwhName() {
    return DWH_NAME;
  }

  /*
   * (non-Javadoc)
   * @see com.distocraft.dc5000.etl.gui.common.Environment#showLoadings()
   */
  @Override
  public Boolean showLoadings() {
    return true;
  }

  /*
   * (non-Javadoc)
   * @see com.distocraft.dc5000.etl.gui.common.Environment#showAggregations()
   */
  @Override
  public Boolean showAggregations() {
    return true;
  }

  /*
   * (non-Javadoc)
   * @see com.distocraft.dc5000.etl.gui.common.Environment#showOldReAggregations()
   */
  @Override
  public Boolean showOldReAggregations() {
    return true;
  }

  /*
   * (non-Javadoc)
   * @see com.distocraft.dc5000.etl.gui.common.Environment#showNewReAggregations()
   */
  @Override
  public Boolean showNewReAggregations() {
    return true;
  }

  /*
   * (non-Javadoc)
   * @see com.distocraft.dc5000.etl.gui.common.Environment#showDatasourceLog()
   */
  @Override
  public Boolean showDatasourceLog() {
    return true;
  }

  /*
   * (non-Javadoc)
   * @see com.distocraft.dc5000.etl.gui.common.Environment#showBusyhourInfo()
   */
  @Override
  public Boolean showBusyhourInfo() {
    return false;
  }

  /*
   * (non-Javadoc)
   * @see com.distocraft.dc5000.etl.gui.common.Environment#showMonitoringRules()
   */
  @Override
  public Boolean showMonitoringRules() {
    return false;
  }

  /*
   * (non-Javadoc)
   * @see com.distocraft.dc5000.etl.gui.common.Environment#showUnmatchedTopology()
   */
  @Override
  public Boolean showUnmatchedTopology() {
    return true;
  }

  /*
   * (non-Javadoc)
   * @see com.distocraft.dc5000.etl.gui.common.Environment#showAdminConfiguration()
   */
  @Override
  public Boolean showAdminConfiguration() {
    return true;
  }

  /*
   * (non-Javadoc)
   * @see com.distocraft.dc5000.etl.gui.common.Environment#showEBSUpgrader()
   */
  @Override
  public Boolean showEBSUpgrader() {
    return false;
  }

  /*
   * (non-Javadoc)
   * @see com.distocraft.dc5000.etl.gui.common.Environment#showNodeVersionUpgrader()
   */
  @Override
  public Boolean showNodeVersionUpgrader() {
    return false;
  }

  /*
   * (non-Javadoc)
   * @see com.distocraft.dc5000.etl.gui.common.Environment#showUserAdministration()
   */
  @Override
  public Boolean showUserAdministration() {
    return true;
  }

  /*
   * (non-Javadoc)
   * @see com.distocraft.dc5000.etl.gui.common.Environment#showManual()
   */
  @Override
  public Boolean showManual() {
    return true;
  }

  /* (non-Javadoc)
   * @see com.distocraft.dc5000.etl.gui.common.Environment#showFMAlarm()
   */
  @Override
public Boolean showFMAlarm() {
	return true;
}
  /* (non-Javadoc)
   * @see com.distocraft.dc5000.etl.gui.common.Environment#showEniqMonitoring()
   */
  @Override
  public Boolean showEniqMonitoring(){
	return true;  
  } 

@Override
public Boolean showRAT() {
	return false;
}

@Override
public Boolean showNAT() {
	return false;
}

@Override
public Boolean showFLS() {
	return false;
}
}
