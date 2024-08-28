/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.dwhmanager;

import java.util.logging.Logger;

import com.distocraft.dc5000.dwhm.StorageTimeAction;
import com.distocraft.dc5000.repository.dwhrep.Busyhour;
import com.distocraft.dc5000.repository.dwhrep.Dwhtechpacks;
import com.ericsson.eniq.busyhourcfg.exceptions.DWHManagerException;
import ssc.rockfactory.RockFactory;


/**
 * @author eheijun
 *
 */
public class DefaultDWHStorageTimeAction implements DWHStorageTimeAction {

  private final Logger log = Logger.getLogger(DefaultDWHStorageTimeAction.class.getName());
  
  private RockFactory dwhrepRockFactory;

  private RockFactory dwhRockFactory;

  public DefaultDWHStorageTimeAction(final RockFactory dwhRockFactory, final RockFactory dwhrepRockFactory) throws DWHManagerException {
    try {
      this.dwhRockFactory = dwhRockFactory;
      this.dwhrepRockFactory = dwhrepRockFactory;
    } catch (Exception e) {
      log.severe(e.getMessage());
      e.printStackTrace();
      throw new DWHManagerException("Failed to create StorageTimeAction", e);
    }
  }
  
  /*
   * (non-Javadoc)
   * @see com.ericsson.eniq.busyhourcfg.dwhmanager.DWHManagerConnector#performDropBhRankViews()
   */
  @Override
  public void performDropBhRankViews(final Busyhour busyhour) throws DWHManagerException {
    try {
      final StorageTimeAction sta = new StorageTimeAction(dwhRockFactory, dwhrepRockFactory, log);
      sta.dropBhRankViews(busyhour);
    } catch (Exception e) {
      log.severe(e.getMessage());
      e.printStackTrace();
      throw new DWHManagerException("Failed to drop BH rank views", e);
    }
  }
  
  /*
   * (non-Javadoc)
   * @see com.ericsson.eniq.busyhourcfg.dwhmanager.DWHManagerConnector#performCreateBhRankViews()
   */
  @Override
  public void performCreateBhRankViews(final Busyhour busyhour) throws DWHManagerException {
    try {
      final StorageTimeAction sta = new StorageTimeAction(dwhRockFactory, dwhrepRockFactory, log);
      //20110509,eeoidiv,HO15521:If TargetVersionId is not the same as VersionId, do like Custom.
      if (!busyhour.getVersionid().equals(busyhour.getTargetversionid())) {
    	  sta.createCustomBhViewCreates(busyhour);
      }else{
    	  sta.createBhRankViews(busyhour);
      }
    } catch (Exception e) {
      log.severe(e.getMessage());
      //e.printStackTrace();
      throw new DWHManagerException("Failed to create BH rank views", e);
    }
  }

//  /*
//   * (non-Javadoc)
//   * @see com.ericsson.eniq.busyhourcfg.dwhmanager.StorageTimeActionConnector#getCustomBhViewCreates(com.distocraft.dc5000.repository.dwhrep.Busyhour)
//   */
//  @Override
//  public String getCustomBhViewCreates(final Busyhour busyhour) throws DWHManagerException {
//    try {
//      return StorageTimeAction.getCustomBhViewCreates(busyhour, dwhrepRockFactory);
//    } catch (Exception e) {
//      log.severe(e.getMessage());
//      e.printStackTrace();
//      throw new DWHManagerException("Failed to get custom bh view creates", e);
//    }
//  }
//
//  /*
//   * (non-Javadoc)
//   * @see com.ericsson.eniq.busyhourcfg.dwhmanager.StorageTimeActionConnector#getPlaceholderCreateStatement(com.distocraft.dc5000.repository.dwhrep.Busyhour, java.lang.String, java.lang.String)
//   */
//  @Override
//  public String getPlaceholderCreateStatement(final Busyhour busyhour, final String techpackVersionId, final String techpackName) throws DWHManagerException {
//    try {
//      return StorageTimeAction.getPlaceholderCreateStatement(busyhour, techpackVersionId, techpackName, dwhrepRockFactory);
//    } catch (Exception e) {
//      log.severe(e.getMessage());
//      e.printStackTrace();
//      throw new DWHManagerException("Failed to get placeholder create statement", e);
//    }
//  }
  
  /**
   * Call updateBHCounters method of the StorageTimeAcrtion
   * @param dwhtechpack
   */
  @Override
  public void updateBHCounters(Dwhtechpacks dwhtechpack) {
	  try {
	      final StorageTimeAction sta = new StorageTimeAction(dwhRockFactory, dwhrepRockFactory, log);
	      sta.updateBHCounters(dwhtechpack);
	  } catch (Exception e) {
	      log.severe("Exception updating BH counters for TP:"+dwhtechpack.getVersionid()+", Exception: "+e.getMessage());
	  }
  } //updateBHCounters

  /**
   * Call updateBHCountersForCustomTechpack method of the StorageTimeAcrtion
   * @param dwhtechpack
   */
  @Override
  public void updateBHCountersForCustomTechpack(Dwhtechpacks dwhtechpack) {
	  try {
	      final StorageTimeAction sta = new StorageTimeAction(dwhRockFactory, dwhrepRockFactory, log);
	      sta.updateBHCountersForCustomTechpack(dwhtechpack);
	  } catch (Exception e) {
		  log.severe("Exception updating BH counters for CUSTOM TP:"+dwhtechpack.getVersionid()+", Exception: "+e.getMessage());
	  }
  } //updateBHCountersForCustomTechpack
  
}
