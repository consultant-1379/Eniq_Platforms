/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import com.distocraft.dc5000.repository.dwhrep.Busyhour;
import com.distocraft.dc5000.repository.dwhrep.Busyhourmapping;
import com.distocraft.dc5000.repository.dwhrep.Busyhourplaceholders;
import com.distocraft.dc5000.repository.dwhrep.Busyhourrankkeys;
import com.distocraft.dc5000.repository.dwhrep.Busyhoursource;
import com.distocraft.dc5000.repository.dwhrep.Measurementtable;
import com.distocraft.dc5000.repository.dwhrep.Referencetable;
import com.distocraft.dc5000.repository.dwhrep.Tpactivation;
import com.ericsson.eniq.busyhourcfg.common.Utils;
import com.ericsson.eniq.busyhourcfg.data.vo.BusyhourSupport;
import com.ericsson.eniq.busyhourcfg.data.vo.DefaultBusyhourSupport;
import com.ericsson.eniq.busyhourcfg.data.vo.DefaultMappedType;
import com.ericsson.eniq.busyhourcfg.data.vo.DefaultPlaceholder;
import com.ericsson.eniq.busyhourcfg.data.vo.DefaultKey;
import com.ericsson.eniq.busyhourcfg.data.vo.DefaultPlaceholderCount;
import com.ericsson.eniq.busyhourcfg.data.vo.DefaultSource;
import com.ericsson.eniq.busyhourcfg.data.vo.DefaultTargetTechPack;
import com.ericsson.eniq.busyhourcfg.data.vo.DefaultTechPack;
import com.ericsson.eniq.busyhourcfg.data.vo.Key;
import com.ericsson.eniq.busyhourcfg.data.vo.MappedType;
import com.ericsson.eniq.busyhourcfg.data.vo.Placeholder;
import com.ericsson.eniq.busyhourcfg.data.vo.Source;
import com.ericsson.eniq.busyhourcfg.data.vo.TargetTechPack;
import com.ericsson.eniq.busyhourcfg.data.vo.TechPack;
import com.ericsson.eniq.busyhourcfg.exceptions.DatabaseException;

/**
 * @author eheijun
 * 
 */
public class RockTechPackReader implements TechPackReader {

  private final Logger log = Logger.getLogger(RockTechPackReader.class.getName());

  private final RockDatabaseSession databaseSession;

  /**
   * Default constructor. Takes an instance of the database interface to be able
   * to query the DWH.
   * 
   * @param session
   *          the database session to use.
   */
  public RockTechPackReader(final DatabaseSession session) {
    this.databaseSession = (RockDatabaseSession) session;
  }

  /*
   * (non-Javadoc)
   * @see com.ericsson.eniq.busyhourcfg.data.TechPackFactory#getAllNames()
   */
  @Override
  public List<String> getAllActivatedPMTypeVersionIds() {
    final List<String> names = new ArrayList<String>();
    try {
    	for (Tpactivation item : databaseSession.getActivatedVersionings()) {
    		// Only want PM or CUSTOM //eeoidiv, 20110321, HN30214:Incompatility issues with Custom busyhours and product busyhours and WLE functionality
    		if( (item.getType().equalsIgnoreCase("PM")) || (item.getType().equalsIgnoreCase("CUSTOM")) ) {
    			names.add(item.getVersionid());
    		}
    	}
    } catch (DatabaseException e) {
      log.severe("The database interface threw a DatabaseException when trying to retrieve "
          + "activated techpack names. The exception message was: " + e.getMessage());
      return null;
    }
    return names;
  }

  /*
   * (non-Javadoc)
   * @see com.ericsson.eniq.busyhourcfg.data.TechPackFactory#getTechPackByVersionId(java.lang.String)
   */
  @Override
  public TechPack getTechPackByVersionId(final String versionid) {
    try {
      
      final Tpactivation tpactivation = databaseSession.getActivatedVersioning(versionid);
      final Vector<Busyhour> busyhours = databaseSession.getBusyhours(versionid);
      
      final TechPack techPack = new DefaultTechPack(tpactivation.getVersionid(), tpactivation.getTechpack_name(), tpactivation.getModified());
      
      TargetTechPack targetTechPack = null;
      BusyhourSupport busyhourSupport = null; 
      Placeholder placeholder = null;
      
      for (Busyhour busyhour : busyhours) {
        if ((targetTechPack == null) || (!busyhour.getTargetversionid().equals(targetTechPack.getVersionId()))) {
          targetTechPack = new DefaultTargetTechPack(busyhour.getTargetversionid());
          final Vector<Busyhourplaceholders> busyhourplaceholders = databaseSession.getBusyhourplaceholders(busyhour.getTargetversionid());
          for (Busyhourplaceholders busyhourplaceholder : busyhourplaceholders) {
            final DefaultPlaceholderCount placeholderCount = new DefaultPlaceholderCount(busyhourplaceholder.getBhlevel(), busyhourplaceholder.getProductplaceholders(), busyhourplaceholder.getCustomplaceholders()); 
            targetTechPack.addPlaceholderCount(placeholderCount);
          }
          techPack.addTargetTechPack(targetTechPack);
          busyhourSupport = new DefaultBusyhourSupport(busyhour.getBhlevel());
          targetTechPack.addBusyhourSupport(busyhourSupport);
        } else if ((busyhourSupport == null) || (!busyhour.getBhlevel().equals(busyhourSupport.getBhlevel()))) {
          busyhourSupport = new DefaultBusyhourSupport(busyhour.getBhlevel()); 
          targetTechPack.addBusyhourSupport(busyhourSupport);
        }
        
        final Vector<Busyhoursource> busyhoursources = databaseSession.getBusyhourSources(busyhour);
        
        final List<Source> sources = new ArrayList<Source>(busyhoursources.size());
        for (Busyhoursource busyhoursource : busyhoursources) {
          final Source source = new DefaultSource(busyhoursource.getTypename());
          sources.add(source);
        }
        
        placeholder = new DefaultPlaceholder(busyhour.getVersionid(), busyhour.getBhlevel(), busyhour.getBhtype(), 
            busyhour.getTargetversionid(), busyhour.getBhobject(), busyhour.getAggregationtype(), /*busyhour.getOffset(),*/
            busyhour.getLookback(), busyhour.getN_threshold(), busyhour.getP_threshold(), busyhour.getPlaceholdertype(),
            busyhour.getDescription(), busyhour.getWhereclause(), busyhour.getBhcriteria(), Utils.integerToBoolean(busyhour.getEnable()),
            busyhour.getGrouping(), busyhour.getReactivateviews());

        placeholder.setSources(sources);
        
        
        final Vector<Busyhourmapping> mappedTypes = databaseSession.getBusyhourmappings(busyhour);
        final List<MappedType> mtypes = new ArrayList<MappedType>();
        for (Busyhourmapping busyhourmapping : mappedTypes) {
          final MappedType mt = new DefaultMappedType(busyhourmapping.getTypeid(), busyhourmapping.getBhtargetlevel(), busyhourmapping.getBhtargettype(), Utils.integerToBoolean(busyhourmapping.getEnable()));
          mtypes.add(mt);
        }
        
        placeholder.setMappedTypes(mtypes);
        
        final Vector<Busyhourrankkeys> keys = databaseSession.getBusyhourrankkeys(busyhour);
        final List<Key> rkeys = new ArrayList<Key>();
        for (Busyhourrankkeys busyhourrankkeys : keys) {
          final String keyName = busyhourrankkeys.getKeyname();
          String source = "";
          String keyValue = busyhourrankkeys.getKeyvalue();
          if (keyValue.length() > 0 && keyValue.indexOf(".") >= 0) {
            source = keyValue.substring(0, keyValue.indexOf("."));
            keyValue = Utils.replaceNull(keyValue.substring(keyValue.indexOf(".") + 1));
          }
          final Key key = new DefaultKey(keyName, source, keyValue);
          rkeys.add(key);
        }
        
        placeholder.setKeys(rkeys);
        
        busyhourSupport.addPlaceHolder(placeholder);
      }
      return techPack;
    } catch (DatabaseException e) {
      log.severe("The database interface threw a DatabaseException when trying to retrieve "
          + "techpack version ids. The exception message was: " + e.getMessage());
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * @see com.ericsson.eniq.busyhourcfg.database.TechPackFactory#getAllBusyhourBasetables(java.lang.String)
   */
  @Override
  public List<String> getAllBusyhourBasetables(final String versionid) {
    final List<String> basetables = new ArrayList<String>(); 
    try {
      final Vector<Measurementtable> measurementtables = databaseSession.getBusyhourSourceTables(versionid);
      for (Measurementtable measurementtable : measurementtables) {
        basetables.add(measurementtable.getBasetablename());
      }
      final Vector<Referencetable> referencetables = databaseSession.getBusyhourReferenceTables(versionid);
      for (Referencetable referencetable : referencetables) {
        basetables.add(referencetable.getTypename());
      }
    } catch (DatabaseException e) {
      log.severe("The database interface threw a DatabaseException when trying to retrieve "
        + versionid + " basetables. The exception message was: " + e.getMessage());
      return null;
    }
    return basetables;
  }

  /*
   * (non-Javadoc)
   * @see com.ericsson.eniq.busyhourcfg.database.TechPackFactory#getAllBusyhourSourceTechpacks()
   */
  @Override
  public List<String> getAllBusyhourSourceTechpacks() {
    final List<String> techpacks = new ArrayList<String>();
    try {
      final Vector<Tpactivation> versionings = databaseSession.getActivatedVersionings();
      for (Tpactivation versioning : versionings) {
        techpacks.add(versioning.getVersionid());
      }
    } catch (DatabaseException e) {
      log.severe("The database interface threw a DatabaseException when trying to retrieve "
        + "basetable techpacks. The exception message was: " + e.getMessage());
      return null;
    }
    return techpacks;
  }

}
