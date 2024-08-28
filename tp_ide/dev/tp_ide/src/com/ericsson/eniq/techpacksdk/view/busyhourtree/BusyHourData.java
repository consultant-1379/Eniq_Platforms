/**
 * 
 */
package com.ericsson.eniq.techpacksdk.view.busyhourtree;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.repository.dwhrep.Busyhour;
import com.distocraft.dc5000.repository.dwhrep.Busyhourmapping;
import com.distocraft.dc5000.repository.dwhrep.Busyhourrankkeys;
import com.distocraft.dc5000.repository.dwhrep.Busyhoursource;
import com.distocraft.dc5000.repository.dwhrep.Measurementkey;
import com.distocraft.dc5000.repository.dwhrep.MeasurementkeyFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementobjbhsupport;
import com.distocraft.dc5000.repository.dwhrep.MeasurementobjbhsupportFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementtype;
import com.distocraft.dc5000.repository.dwhrep.MeasurementtypeFactory;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.techpacksdk.common.Constants;
import com.ericsson.eniq.techpacksdk.common.Utils;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;

/**
 * @author eheijun
 * @author eheitur
 * 
 */
public class BusyHourData {

  private Versioning versioning;

  private DataModelController dataModelController;

  private Busyhour busyhour;

  private List<Busyhoursource> busyhourSource;

  private List<Busyhourrankkeys> busyhourRankkeys;

  private List<Busyhourmapping> busyhourmappings;

  private BusyHourSourceTableModel busyhourSourceTableModel;

  private BusyHourRankkeysTableModel busyhourRankkeysTableModel;

  private BusyHourMappingsTableModel busyhourMappingsTableModel;

  private final RockFactory rockFactory;

  private boolean editable = true;

  public BusyHourData(final DataModelController dataModelController, final Versioning versioning, final String targetVersionId, final String bhLevel, final String placeholderType){
	    super();
	    this.editable = true;//editable;
	    this.versioning = versioning;
	    this.dataModelController = dataModelController;
	    this.rockFactory = dataModelController.getRockFactory();
	    this.busyhour = new Busyhour(this.rockFactory);

	    this.busyhour.setVersionid(versioning.getVersionid());
	    this.busyhour.setTargetversionid(targetVersionId);
	    this.busyhour.setBhlevel(bhLevel);
	    this.busyhour.setBhobject(bhLevel.substring(bhLevel.lastIndexOf("_") + 1,bhLevel.indexOf("BH"))); //TODO: Consistent with old migration of guessing Bhobject but could query Select bhobject from MeasurementObjBHSupport where targetVesionId AND bhLevel. 
	    this.busyhour.setBhtype("");
	    this.busyhour.setAggregationtype(Constants.BH_AGGREGATION_TYPPES[0]);

	    this.busyhour.setBhcriteria("");
	    this.busyhour.setWhereclause("");
	    this.busyhour.setDescription("");
	    this.busyhour.setBhelement(0);
	    this.busyhour.setOffset(0);
	    this.busyhour.setWindowsize(60);
	    this.busyhour.setLookback(0);
	    this.busyhour.setP_threshold(0);
	    this.busyhour.setN_threshold(0);
	    this.busyhour.setClause("");
	    this.busyhour.setPlaceholdertype(placeholderType);
	    this.busyhour.setGrouping(Constants.BH_GROUPING_TYPES[0]);
	    this.busyhour.setReactivateviews(0);
	    this.busyhour.setEnable(0);
	    this.setBusyhourRankkeys(new Vector<Busyhourrankkeys>());
	    this.setBusyhourSource(new Vector<Busyhoursource>());
	    this.setBusyhourMappings(new Vector<Busyhourmapping>());
	  
  }
  /**
   * Constructor.
   * 
   * @param versioning
   * @param dataModelController
   * @param editable
   */
  public BusyHourData(Versioning versioning, DataModelController dataModelController, boolean editable) {
    super();
    this.editable = editable;
    this.versioning = versioning;
    this.dataModelController = dataModelController;
    this.rockFactory = dataModelController.getRockFactory();
    this.busyhour = new Busyhour(this.rockFactory);

    this.busyhour.setVersionid(versioning.getVersionid());
    this.busyhour.setTargetversionid(versioning.getVersionid());
    this.busyhour.setBhlevel("");
    this.busyhour.setBhobject("");
    this.busyhour.setBhtype("");
    this.busyhour.setAggregationtype(Constants.BH_AGGREGATION_TYPPES[0]);

    this.busyhour.setBhcriteria("");
    this.busyhour.setWhereclause("");
    this.busyhour.setDescription("");
    this.busyhour.setBhelement(0);
    this.busyhour.setOffset(0);
    this.busyhour.setWindowsize(60);
    this.busyhour.setLookback(0);
    this.busyhour.setP_threshold(0);
    this.busyhour.setN_threshold(0);
    this.busyhour.setClause("");
    this.busyhour.setPlaceholdertype("");
    this.busyhour.setGrouping(Constants.BH_GROUPING_TYPES[0]);
    this.busyhour.setReactivateviews(0);

    this.setBusyhourRankkeys(new Vector<Busyhourrankkeys>());
    this.setBusyhourSource(new Vector<Busyhoursource>());
    this.setBusyhourMappings(new Vector<Busyhourmapping>());
  }

  /**
   * Constructor.
   * 
   * @param versioning
   * @param busyhour
   * @param busyhourRankkeys
   * @param busyhourSource
   * @param busyhourmapping
   * @param dataModelController
   */
  public BusyHourData(Versioning versioning, Busyhour busyhour, List<Busyhourrankkeys> busyhourRankkeys,
      List<Busyhoursource> busyhourSource, List<Busyhourmapping> busyhourmapping,
      DataModelController dataModelController) {
    super();
    this.versioning = versioning;
    this.dataModelController = dataModelController;
    this.busyhour = busyhour;
    this.rockFactory = dataModelController.getRockFactory();

    if (busyhourRankkeys == null) {
      this.setBusyhourRankkeys(new Vector<Busyhourrankkeys>());
    } else {
      this.setBusyhourRankkeys(busyhourRankkeys);
    }
    if (busyhourSource == null) {
      this.setBusyhourSource(new Vector<Busyhoursource>());
    } else {
      this.setBusyhourSource(busyhourSource);
    }

    if (busyhourmapping == null) {
      this.setBusyhourMappings(new Vector<Busyhourmapping>());
    } else {
      this.setBusyhourMappings(busyhourmapping);
    }
  }
 
  /**
   * Removes BH if its target measurement type is removed.
   */
  public void removeUnusedBHs() throws Exception {

    Measurementtype mt = new Measurementtype(this.rockFactory);
    mt.setVersionid(this.versioning.getVersionid());
    MeasurementtypeFactory mtF = new MeasurementtypeFactory(this.rockFactory, mt);

    boolean remove = true;
    for (Measurementtype mtype : mtF.get()) {
      if (this.busyhour.getBhlevel().equalsIgnoreCase(mtype.getTypename())) {
        remove = false;
        break;
      }
    }

    if (remove) {
      this.delete();
    }

  }

  /**
   * Generate the busy hour rank keys.
   * 
   * @param useDirtUpdate
   */
  public void generateBusyHourRankKeys(boolean useDirtUpdate) {

    Vector<Busyhourrankkeys> result = new Vector<Busyhourrankkeys>();
    if (busyhourSource.size() > 0) {

      long order = 0;
      String typeid = "";
      if (busyhour.getBhlevel() != null) {
        try {
          typeid = busyhour.getTargetversionid() + ":" + busyhour.getBhlevel();
        } catch (Exception e) {
          // use empty if something goes wrong
        }
      }

      String defaultBusyhourSourceRow = "";
      if (busyhourSource.size() > 0) {
        defaultBusyhourSourceRow = busyhourSource.get(0).getTypename();
      }

      try {

        Measurementkey mk = new Measurementkey(this.rockFactory);
        mk.setTypeid(typeid);
        MeasurementkeyFactory mkF = new MeasurementkeyFactory(this.rockFactory, mk);

        for (Measurementkey mKey : mkF.get()) {

          Busyhourrankkeys bhrk = new Busyhourrankkeys(this.rockFactory);
          bhrk.setBhlevel(busyhour.getBhlevel());
          bhrk.setBhobject(busyhour.getBhobject());
          bhrk.setBhtype(busyhour.getBhtype());

          String keyname = mKey.getDataname();
          String keyvalue = mKey.getDataname();

          if (!defaultBusyhourSourceRow.trim().isEmpty()) {
            keyvalue = defaultBusyhourSourceRow + "." + mKey.getDataname();
          }

          bhrk.setKeyname(keyname);
          bhrk.setKeyvalue(keyvalue);
          bhrk.setOrdernbr(order);
          bhrk.setTargetversionid(busyhour.getTargetversionid());
          bhrk.setVersionid(busyhour.getVersionid());

          result.add(bhrk);

        }

      } catch (Exception e) {
        System.out.println("UPS");
      }
    }
    this.busyhourRankkeys = result;
    if (useDirtUpdate) {
      getBusyhourRankkeysTableModel().setDirtyData(result);
    } else {
      getBusyhourRankkeysTableModel().setData(result);
    }
  }

  /**
   * Removes the busy hour rank keys for this busy hour from DB.
   * 
   * @throws Exception
   */
  public void removeBusyHourRankKeysFromDB() throws Exception {
    for (Busyhourrankkeys rankKey : busyhourRankkeysTableModel.getData()) {
      rankKey.deleteDB();
    }
  }

  public Versioning getVersioning() {
    return versioning;
  }

  public void setVersioning(final Versioning versioning) {
    this.versioning = versioning;
  }

  public Busyhour getBusyhour() {
    return busyhour;
  }

  public void setBusyhour(final Busyhour busyhour) {
    this.busyhour = busyhour;
  }

  public List<Busyhourrankkeys> getBusyhourRankkeys() {
    return busyhourRankkeys;
  }

  public void setBusyhourRankkeys(final List<Busyhourrankkeys> busyhourRankkeys) {
    this.busyhourRankkeys = busyhourRankkeys;
    if (busyhourRankkeysTableModel == null) {
      this.busyhourRankkeysTableModel = new BusyHourRankkeysTableModel(busyhourSource, rockFactory, busyhour,
          this.busyhourRankkeys, editable);
    } else {
      if (busyhourRankkeysTableModel.getData() != busyhourRankkeys) {
        this.busyhourRankkeysTableModel.setData(busyhourRankkeys);
      }
    }
  }

  public List<Busyhourmapping> getBusyhourmapping() {
    return busyhourmappings;
  }

  public void setBusyhourMappings(final List<Busyhourmapping> busyhourmappings) {
    this.busyhourmappings = busyhourmappings;
    if (busyhourMappingsTableModel == null) {
      this.busyhourMappingsTableModel = new BusyHourMappingsTableModel(rockFactory, this.busyhourmappings);
    } else {
      if (busyhourMappingsTableModel.getData() != busyhourmappings) {
        this.busyhourMappingsTableModel.setData(busyhourmappings);
      }
    }

  }

  public List<Busyhoursource> getBusyhourSource() {
    return busyhourSource;
  }

  public void setBusyhourSource(final List<Busyhoursource> busyhourSource) {
    this.busyhourSource = busyhourSource;
    this.busyhourRankkeysTableModel.setBusyhourSource(busyhourSource);
    if (busyhourSourceTableModel == null) {
      this.busyhourSourceTableModel = new BusyHourSourceTableModel(dataModelController, busyhour, this.busyhourSource,
          editable);
    } else {
      if (busyhourSourceTableModel.getData() != busyhourSource) {
        this.busyhourSourceTableModel.setData(busyhourSource);
      }
    }
  }

  public void save() throws Exception {
    busyhour.saveToDB();
    busyhourSourceTableModel.save();
    busyhourRankkeysTableModel.save();
    busyhourMappingsTableModel.save();

    this.busyhourRankkeysTableModel.setBusyhourSource(busyhourSource);
  }

  public void delete() throws SQLException, RockException {

    busyhourRankkeysTableModel.removeFromDB();
    busyhourSourceTableModel.removeFromDB();
    busyhourMappingsTableModel.removeFromDB();
    busyhour.deleteDB();

  }

  // public void clearPlaceholder() throws SQLException, RockException {
  // Iterator<Busyhourmapping> iter = busyhourmapping.iterator();
  // while (iter.hasNext()) {
  // Busyhourmapping bhm = iter.next();
  // bhm.deleteDB();
  // }
  //
  // busyhourRankkeysTableModel.removeFromDB();
  // busyhourRankkeysTableModel.clear();
  //	    
  // busyhourJoincolumnTableModel.removeFromDB();
  // busyhourJoincolumnTableModel.clear();
  //	    
  // busyhourSourceTableModel.removeFromDB();
  // busyhourSourceTableModel.clear();
  //	    
  // busyhour.setBhcriteria("");
  // busyhour.setWhereclause("");
  // busyhour.setDescription("");
  // busyhour.setEnable(0);
  // busyhour.setAggregationtype(Constants.BH_AGGREGATION_TYPPES[0]);
  // busyhour.setOffset(0);
  // busyhour.setWindowsize(0);
  // busyhour.setLookback(0);
  // busyhour.setP_threshold(0);
  // busyhour.setN_threshold(0);
  // busyhour.setClause("");
  // busyhour.setGrouping(Constants.BH_GROUPING_TYPES[0]);
  // busyhour.updateDB();
  // }

  public BusyHourSourceTableModel getBusyhourSourceTableModel() {
    return busyhourSourceTableModel;
  }

  public void setBusyhourSourceTableModel(final BusyHourSourceTableModel busyhourSourceTableModel) {
    this.busyhourSourceTableModel = busyhourSourceTableModel;
  }

  public BusyHourRankkeysTableModel getBusyhourRankkeysTableModel() {
    return busyhourRankkeysTableModel;
  }

  public void setBusyhourRankkeysTableModel(final BusyHourRankkeysTableModel busyhourRankkeysTableModel) {
    this.busyhourRankkeysTableModel = busyhourRankkeysTableModel;
  }

  public BusyHourMappingsTableModel getBusyhourMappingsTableModel() {
    return busyhourMappingsTableModel;
  }

  public void setBusyhourMappingsTableModel(final BusyHourMappingsTableModel busyhourMappingsTableModel) {
    this.busyhourMappingsTableModel = busyhourMappingsTableModel;
  }

  public Object clone() {
    BusyHourData bd = new BusyHourData(versioning, dataModelController, editable);

    // Copy the busy hour
    Busyhour newb = new Busyhour(rockFactory);
    newb.setVersionid(busyhour.getVersionid());
    newb.setBhlevel(busyhour.getBhlevel());
    newb.setBhtype(busyhour.getBhtype());
    newb.setBhcriteria(busyhour.getBhcriteria());
    newb.setWhereclause(busyhour.getWhereclause());
    newb.setDescription(busyhour.getDescription());
    newb.setTargetversionid(busyhour.getTargetversionid());
    newb.setBhobject(busyhour.getBhobject());
    newb.setBhelement(busyhour.getBhelement());
    newb.setAggregationtype(busyhour.getAggregationtype());
    newb.setOffset(busyhour.getOffset());
    newb.setWindowsize(busyhour.getWindowsize());
    newb.setLookback(busyhour.getLookback());
    newb.setP_threshold(busyhour.getP_threshold());
    newb.setN_threshold(busyhour.getN_threshold());
    newb.setClause(busyhour.getClause());
    newb.setPlaceholdertype(busyhour.getPlaceholdertype());
    newb.setGrouping(busyhour.getGrouping());
    newb.setReactivateviews(busyhour.getReactivateviews());
		newb.setEnable(0);

    bd.setBusyhour(newb);

    // Copy the rank keys
    Vector<Busyhourrankkeys> newRankKeys = new Vector<Busyhourrankkeys>();
    for (Busyhourrankkeys key : busyhourRankkeys) {
      Busyhourrankkeys newKey = (Busyhourrankkeys) key.clone();
      newKey.setNewItem(true);
      newRankKeys.add(newKey);
    }
    bd.setBusyhourRankkeys(newRankKeys);
    bd.setBusyhourRankkeysTableModel(new BusyHourRankkeysTableModel(busyhourSource, rockFactory, newb, newRankKeys,
        editable));

    // Copy the sources
    Vector<Busyhoursource> newSources = new Vector<Busyhoursource>();
    for (Busyhoursource source : busyhourSource) {
      Busyhoursource newSource = (Busyhoursource) source.clone();
      newSource.setNewItem(true);
      newSources.add(newSource);
    }
    bd.setBusyhourSource(newSources);
    bd.setBusyhourSourceTableModel(new BusyHourSourceTableModel(dataModelController, newb, newSources, editable));

    // Copy the mappings
    Vector<Busyhourmapping> newMappings = new Vector<Busyhourmapping>();
    for (Busyhourmapping mapping : busyhourmappings) {
      Busyhourmapping newMapping = (Busyhourmapping) mapping.clone();
      newMapping.setNewItem(true);
      newMappings.add(newMapping);
    }
    bd.setBusyhourMappings(newMappings);
    bd.setBusyhourMappingsTableModel(new BusyHourMappingsTableModel(rockFactory, newMappings));

    // Copy the versioning
    bd.setVersioning((Versioning) versioning.clone());

    return bd;
  }
  /**
   * Gets the TargetVersionId for the BusyHourData.
   * calls getBusyhour.getTargetversionid on Busyhour.
   * @param versionid
   */
  public String getTargetversionid() {
	  if(busyhour!=null) {
		  return busyhour.getTargetversionid();
	  }
	  return null;
  } // getTargetversionid

  /**
   * Sets/Changes the TargetVersionId for the BusyHourData's components.
   * calls .setTargetversionid on Busyhour //BusyhourMapping,BusyhourSource
   * @param versionid
   */
	public void setTargetversionid(final String targetVersionId) {
		if (busyhour != null) {
			busyhour.setTargetversionid(targetVersionId);
		}
		if (busyhourRankkeys != null) {
			// Busyhourrankkeys
			for (Busyhourrankkeys key : busyhourRankkeys) {
				key.setTargetversionid(targetVersionId);
			}
		}
		if (busyhourSource != null) {
			// Busyhoursources
			for (Busyhoursource source : busyhourSource) {
				source.setTargetversionid(targetVersionId);
			}
		}
		if (busyhourmappings != null) {
			// Busyhourmappings
			for (Busyhourmapping mapping : busyhourmappings) {
				mapping.setTargetversionid(targetVersionId);
			}
		}
		System.out.println("BusyHourData.setTargetversionid: Versionid:"+busyhour.getVersionid()+":"+busyhour.getBhlevel()+"_"+busyhour.getBhtype()+",Targetversionid:"+busyhour.getTargetversionid()+",Description:"+busyhour.getDescription()+", "+this); //TODO: REMOVE
  } // setTargetversionid
	
	/**
	 * This method is used to set the BHType of a Busyhour. The number of the placeholder is only known later in the 
	 * process when adding a new row to the placeholders in Busyhour Tab in TPIDE.
	 * @param placeholderNumber
	 */
	public void setBHType(int placeholderNumber) {

		StringBuffer bhType = new StringBuffer();
		
		if(versioning.getTechpack_type().equals(Constants.CUSTOM_TECHPACK)){
			bhType.append(Constants.BH_CUSTOM_TP_PREFIX);
			bhType.append("_");
		}
		bhType.append(this.busyhour.getPlaceholdertype());
		bhType.append(placeholderNumber);
		
		this.busyhour.setBhtype(bhType.toString());

	}
	
	/**
	 * Create the busy hour mappings for the given object busy hour support.
	 * NOTE: New Busyhourmapping will be enabled by default.
	 */
	public void generateBusyHourMappings() {
		this.generateBusyHourMappings(getMeasurementobjbhsupports());
	}
	
	/**
	 * Create the busy hour mappings for the given object busy hour support.
	 * NOTE: New Busyhourmapping will be enabled by default.
	 */
	public void generateBusyHourMappings(final List<Measurementobjbhsupport> supports) {
		List<Busyhourmapping> result = new Vector<Busyhourmapping>();
		try {
			Busyhour bh = this.getBusyhour();
			// Check Busyhour has necessary values to create a Busyhourmapping
			if (bh == null || Utils.isEmpty(bh.getVersionid())
					|| Utils.isEmpty(bh.getTargetversionid())
					|| Utils.isEmpty(bh.getBhlevel())
					|| Utils.isEmpty(bh.getBhtype())) {
				return;
			}
			
			// Loop all supports and add mapping for each one with objBhSupport value
			// matching the bhObject.
			for (Measurementobjbhsupport sup : supports) {
				//Fix for TR HM90783 
	          	//Don't want to create the Mappingtypes for ELEMBH Ranking table
	          	//20101021, ejohabd, changed approved by ekatkil.
	          	if (bh.getBhelement() == 1){
	          	  continue;
	          	}
	          	
	          	// Skip if the versionId part of the typeId does not match the target techpack. Want to exclude other TPs with same Object Support name.
	          	if (!bh.getTargetversionid().equalsIgnoreCase(sup.getTypeid().substring(0, sup.getTypeid().lastIndexOf(":")))) {
	          		continue;
	          	}
	            
				// Skip the busy hour support itself
				if (sup.getTypeid().equalsIgnoreCase(bh.getTargetversionid() + Constants.TYPESEPARATOR + bh.getBhlevel())) {
					continue;
				}
				
				// Create a new mapping.
				final Busyhourmapping newBHM = new Busyhourmapping(rockFactory);
				newBHM.setVersionid(bh.getVersionid());
				newBHM.setTargetversionid(bh.getTargetversionid());
				newBHM.setBhlevel(bh.getBhlevel());
				newBHM.setBhobject(sup.getObjbhsupport());
				newBHM.setBhtype(bh.getBhtype());
				newBHM.setTypeid(sup.getTypeid());
				newBHM.setBhtargettype(sup.getTypeid().substring(sup.getTypeid().lastIndexOf(":") + 1));
				newBHM.setBhtargetlevel(Utils.replaceNull(bh.getBhobject())
						.trim()
						+ Constants.TYPENAMESEPARATOR
						+ Utils.replaceNull(bh.getBhtype()));
				newBHM.setEnable(1);
				result.add(newBHM);
			} //for (Measurementobjbhsupport sup : supports)
		}  catch (Exception e) {
			// use empty if something goes wrong
        }
		this.busyhourmappings = result;
		return;
	} // generateBusyHourMappings
	
	public List<Measurementobjbhsupport> getMeasurementobjbhsupports() {
		List<Measurementobjbhsupport> result = new Vector<Measurementobjbhsupport>();
		Measurementobjbhsupport mobhsu = new Measurementobjbhsupport(this.rockFactory);
        mobhsu.setObjbhsupport(this.getBusyhour().getBhobject());
        MeasurementobjbhsupportFactory mobhsuf;
		try {
			mobhsuf = new MeasurementobjbhsupportFactory(this.rockFactory, mobhsu);
			result = mobhsuf.get();
		} catch (Exception e) {
			e.printStackTrace();
		}
        return result;
	} //getMeasurementobjbhsupports
}
