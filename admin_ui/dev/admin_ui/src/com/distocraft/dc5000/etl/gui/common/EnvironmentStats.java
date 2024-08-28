/**
 * 
 */
package com.distocraft.dc5000.etl.gui.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.distocraft.dc5000.common.RmiUrlFactory;
import com.distocraft.dc5000.etl.gui.enminterworking.EnmInterUtils;
import com.ericsson.eniq.common.DatabaseConnections;
import com.ericsson.eniq.enminterworking.IEnmInterworkingRMI;
import com.ericsson.eniq.exception.LicensingException;
import com.ericsson.eniq.repository.ETLCServerProperties;

import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;

/**
 * @author eheijun
 *
 */
public class EnvironmentStats implements Environment {

  private static final String DWH_NAME = "dwh";

  /*
   * (non-Javadoc)
   * @see com.distocraft.dc5000.etl.gui.common.Environment#getType()
   */
  @Override
  public Type getType() {
    return Type.STATS;
  }

  /*
   * (non-Javadoc)
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
    return false;
  }

  /*
   * (non-Javadoc)
   * @see com.distocraft.dc5000.etl.gui.common.Environment#showDatasourceLog()
   */
  @Override
  public Boolean showDatasourceLog() {
    return false;
  }

  /*
   * (non-Javadoc)
   * @see com.distocraft.dc5000.etl.gui.common.Environment#showBusyhourInfo()
   */
  @Override
  public Boolean showBusyhourInfo() {
    return true;
  }

  /*
   * (non-Javadoc)
   * @see com.distocraft.dc5000.etl.gui.common.Environment#showMonitoringRules()
   */
  @Override
  public Boolean showMonitoringRules() {
    return true;
  }

  /*
   * (non-Javadoc)
   * @see com.distocraft.dc5000.etl.gui.common.Environment#showUnmatchedTopology()
   */
  @Override
  public Boolean showUnmatchedTopology() {
    return false;
  }

  /*
   * (non-Javadoc)
   * @see com.distocraft.dc5000.etl.gui.common.Environment#showAdminConfiguration()
   */
  @Override
  public Boolean showAdminConfiguration() {
    return false;
  }

  /*
   * (non-Javadoc)
   * @see com.distocraft.dc5000.etl.gui.common.Environment#showEBSUpgrader()
   */
  @Override
  public Boolean showEBSUpgrader() {
    return true;
  }

  /*
   * (non-Javadoc)
   * @see com.distocraft.dc5000.etl.gui.common.Environment#showNodeVersionUpgrader()
   */
  @Override
  public Boolean showNodeVersionUpgrader() {
    return true;
  }

  /*
   * (non-Javadoc)
   * @see com.distocraft.dc5000.etl.gui.common.Environment#showUserAdministration()
   */
  @Override
  public Boolean showUserAdministration() {
    return false;
  }

  /*
   * (non-Javadoc)
   * @see com.distocraft.dc5000.etl.gui.common.Environment#showManual()
   */
  @Override
  public Boolean showManual() {
    return true;
  }
//returns true only if connected to ENM
  /*
   * (non-Javadoc)
   * @see com.distocraft.dc5000.etl.gui.common.Environment#showFMAlarm()
   */
  @Override
  public Boolean showFMAlarm(){
	  try { 
	    	final String propertiesFile = System.getProperty("CONF_DIR", "/eniq/sw/conf") + "/enmserverdetail";
	    	final File propFile = new File(propertiesFile);
			if(!propFile.exists()){
				return false;
			}else{
				return true;
			}
  }
	  catch(Exception e){
		  return false;
	  }
}
  /* (non-Javadoc)
   * @see com.distocraft.dc5000.etl.gui.common.Environment#showEniqMonitoring()
   */
  @Override
  public Boolean showEniqMonitoring(){
	return true;  
}
/* Returns true only if UNASSIGNED or MASTER ENIQ-S
   * (non-Javadoc)
   * @see com.distocraft.dc5000.etl.gui.common.Environment#showRAT()
   */
  @Override
  public Boolean showRAT(){
	  RockFactory dwhrep = null;
	  try{
		  if(isFlsEnabled())
		  {
		  dwhrep = DatabaseConnections.getDwhRepConnection();
		  String role = EnmInterUtils.getSelfRole(dwhrep.getConnection());
		  if (role.equals("MASTER") || role.equals("UNASSIGNED"))
		  {
			  return true;
		  }
		  }
	  }
	  catch (Exception e){
		  return false;
	  }
	  finally{
		  try{
			  if (dwhrep != null){
				  dwhrep.getConnection().close();
			  }
		  }
		  catch (Exception e){
			  
		  }
	  }
	  return false;
  }
  
  /* Returns true only if MASTER ENIQ-S
   * (non-Javadoc)
   * @see com.distocraft.dc5000.etl.gui.common.Environment#showNAT()
   */
  @Override
  public Boolean showNAT(){
	  RockFactory dwhrep = null;
	  try{
		  if(isFlsEnabled()){
		  dwhrep = DatabaseConnections.getDwhRepConnection();
		  String role = EnmInterUtils.getSelfRole(dwhrep.getConnection());
		  if (role.equals("MASTER"))
		  {
			  return true;
		  }
		  }
	  }
	  catch (Exception e){
		  return false;
	  }
	  finally{
		  try{
			  if (dwhrep != null){
				  dwhrep.getConnection().close();
			  }
		  }
		  catch (Exception e){
			  
		  }
	  }
	  return false;
  }
  
  /**
 * @return 	true if FLS is enabled in integrated ENM server
 * 			false if FLS is not enabled
 */
  private Boolean isFlsEnabled(){
	  try {
		  IEnmInterworkingRMI multiEs =  (IEnmInterworkingRMI) Naming.lookup(RmiUrlFactory.getInstance().getMultiESRmiUrl(EnmInterUtils.getEngineIP()));
		  return multiEs.flsConfExists();
		}
	  catch(Exception e){
		  return false;
	  }
  }
  
    /**
   * True only if User has to enter Date and Time
   */
  @Override
  public Boolean showFLS(){
	  try{
		  IEnmInterworkingRMI multiEs =  (IEnmInterworkingRMI) Naming.lookup(RmiUrlFactory.getInstance().getMultiESRmiUrl(EnmInterUtils.getEngineIP()));
		  
		  if(multiEs.isFlsMonitorOn()){
			  return true;
		  }
		  else{
			  return false;
		  }
		 }
		 catch (Exception e){
			  return false;
		 }
 }
  public Boolean isFrhLicenseValid() {
		FRHLicenseCheck obj = FRHLicenseCheck.getInstance();
		try {
			if(!obj.checkFrhLicense())
			{
				return false;
			}
			else
			{
				return true;
			}
		} catch (final LicensingException licExp) {
			return false;
		} catch (final Exception e) {
			return false;
		}
	}

	public Boolean getFrhService() {
		String s = null;
		String result = null;
		final String[] command = { "/bin/sh", "-c", "cat /eniq/sw/conf/service_names | grep -i frh" };
		Process process;
		try {
			process = Runtime.getRuntime().exec(command);
			process.waitFor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

			while ((s = reader.readLine()) != null) {
				result = s;
			}
			if(!result.contains("#")&& result.contains("frh"))
			{
				return true;
			}
			else{
				return false;
			}
		} catch (IOException e) {
			return false;
		} catch (InterruptedException e) {
			return false;
		} catch (Exception e) {
			return false;
		}		
	}
}
