/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.database;

import java.util.Vector;
import java.util.logging.Logger;

import com.ericsson.eniq.common.Utils;
import com.distocraft.dc5000.repository.dwhrep.Measurementkey;
import com.ericsson.eniq.busyhourcfg.data.vo.DefaultKey;
import com.ericsson.eniq.busyhourcfg.data.vo.DefaultPlaceholder;
import com.ericsson.eniq.busyhourcfg.data.vo.DefaultTechPack;
import com.ericsson.eniq.busyhourcfg.data.vo.Key;
import com.ericsson.eniq.busyhourcfg.data.vo.Placeholder;
import com.ericsson.eniq.busyhourcfg.data.vo.Source;
import com.ericsson.eniq.busyhourcfg.data.vo.TechPack;
import com.ericsson.eniq.busyhourcfg.dwhmanager.DWHStorageTimeAction;
import com.ericsson.eniq.busyhourcfg.exceptions.DatabaseException;

/**
 * @author eheijun
 * 
 */
public class RockBusyhourUpdater implements BusyhourUpdater {

  private final Logger log = Logger.getLogger(RockBusyhourUpdater.class.getName());
	
  private final RockDatabaseSession databaseSession;

  private final DWHStorageTimeAction storageTimeAction;

  /**
   * Constructor for rock database updater
   * 
   * @param databaseSession
   */
  public RockBusyhourUpdater(final DatabaseSession databaseSession, final DWHStorageTimeAction storageTimeAction) {
    super();
    this.databaseSession = (RockDatabaseSession) databaseSession;
    this.storageTimeAction = storageTimeAction;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ericsson.eniq.busyhourcfg.database.BusyhourUpdater#savePlaceholder(
   * com.ericsson.eniq.busyhourcfg.data.vo.Placeholder)
   */
  @Override
  public void savePlaceholder(final Placeholder placeholder) throws DatabaseException {
    if (placeholder instanceof DefaultPlaceholder) {
      final DefaultPlaceholder defaultPlaceholder = (DefaultPlaceholder) placeholder;
      databaseSession.setPlaceholder(defaultPlaceholder);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ericsson.eniq.busyhourcfg.database.BusyhourUpdater#validatePlaceholder
   * (com.ericsson.eniq.busyhourcfg.data.vo.Placeholder)
   */
  @Override
  public String validatePlaceholder(final Placeholder placeholder) {
    if (placeholder instanceof DefaultPlaceholder) {
      final DefaultPlaceholder defaultPlaceholder = (DefaultPlaceholder) placeholder;
      try {
        // check sources, typenames can not be empty or has duplicate values
    	  if(defaultPlaceholder.getSources().size() == 0){
    		  return "Source can not be empty";    		  
    	  }
    	  else{

    		  for (Source source : defaultPlaceholder.getSources()) {
    			  if (source.getTypename().trim().equals("")) {
    				  return "Typename can not be empty";
    			  }
    			  for (Source otherSource : defaultPlaceholder.getSources()) {
    				  if ((source != otherSource) && (source.getTypename().trim().equals(otherSource.getTypename().trim()))) {
    					  return "Typename must be unique";
    				  }
    			  }
    		  }
    	  }
        // check keys, keyname can not be empty or has duplicate values
        for (Key key : defaultPlaceholder.getKeys()) {
          if (key.getKeyName().trim().equals("") || key.getKeyValue().trim().equals("")) {
            return "Key name or key value can not be empty";
          }
          for (Key otherKey : defaultPlaceholder.getKeys()) {
            if ((key != otherKey) && (key.getKeyName().trim().equals(otherKey.getKeyName().trim()))) {
              return "Typename must be unique";
            }
          }
        }
        return "";
      } catch (Exception e) {
        log.severe("Saving placeholder failed." + e.getMessage());
        return e.getMessage();
      }
    }
    return "";
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ericsson.eniq.busyhourcfg.database.BusyhourUpdater#recreateViews(com
   * .ericsson.eniq.busyhourcfg.data.vo.TechPack)
   */
  @Override
  public void recreateViews(final TechPack techpack) throws DatabaseException {
    if (techpack instanceof DefaultTechPack) {
      final DefaultTechPack defaultTechPack = (DefaultTechPack) techpack;
      databaseSession.performBusyhourActivation(defaultTechPack, storageTimeAction);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ericsson.eniq.busyhourcfg.database.BusyhourUpdater#generateBusyhourrankkeys
   * (com.ericsson.eniq.busyhourcfg.data.vo.Placeholder)
   */
  @Override
  public void generateBusyhourrankkeys(final Placeholder placeholder) throws DatabaseException {
    if (placeholder instanceof DefaultPlaceholder) {
      final DefaultPlaceholder defaultPlaceholder = (DefaultPlaceholder) placeholder;
      if (defaultPlaceholder.getKeys().size() == 0) {
        final String typeid = defaultPlaceholder.getTargetversionid() + ":" + defaultPlaceholder.getBhlevel();
        final Vector<Measurementkey> busyhoursupportKeys = databaseSession.getBusyhourSupportKeys(typeid);
        String sourcename = "";
        if (defaultPlaceholder.getSources().size() > 0) {
          // use first source as default for generating the keys
          sourcename = defaultPlaceholder.getSources().get(0).getTypename();
          for (Measurementkey busyhoursupportKey : busyhoursupportKeys) {
            final String keyname = Utils.replaceNull(busyhoursupportKey.getDataname());
            boolean busyhourrankkeyExists = false;
            for (Key key : defaultPlaceholder.getKeys()) {
              if (key.getKeyName().equals(keyname) && key.getSource().equals(sourcename)) {
                busyhourrankkeyExists = true;
              }
            }
            if (!busyhourrankkeyExists) {
              defaultPlaceholder.getKeys().add(new DefaultKey(keyname, sourcename, keyname));
            }
          }
        }
      }
    }
  }
}
