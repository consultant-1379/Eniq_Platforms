/**
 * 
 */
package com.ericsson.eniq.techpacksdk.view.group;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import ssc.rockfactory.RockDBObject;
import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.repository.dwhrep.Grouptypes;
import com.distocraft.dc5000.repository.dwhrep.GrouptypesFactory;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.component.ExceptionHandler;
import com.ericsson.eniq.techpacksdk.datamodel.DataModel;
import com.ericsson.eniq.techpacksdk.datamodel.TechPackDataModel;

/**
 * @author epaujor
 * 
 */
public class GroupTypeDataModel implements DataModel {

  private static final Logger logger = Logger.getLogger(GroupTypeDataModel.class.getName());

  private final RockFactory rockFactory;

  private Versioning currentVersioning;

  private Versioning baseVersioning;

  private List<GroupTypeData> groups;

  private TechPackDataModel techPackDataModel;

  public GroupTypeDataModel(final RockFactory rockFactory) {
    this.rockFactory = rockFactory;
  }

  @Override
  public RockFactory getRockFactory() {
    return rockFactory;
  }

  public void setCurrentVersioning(final Versioning versioning) {
    currentVersioning = versioning;
  }

  public Versioning getCurrentVersioning() {
    return techPackDataModel.getVersioning();
  }

  public Versioning getBaseVersioning() {
    return baseVersioning;
  }

  public void setBaseVersioning(final Versioning versioning) {
    baseVersioning = versioning;
  }

  public List<GroupTypeData> getGroups() {
    return groups;
  }

  @Override
  public void refresh() {
    logger.finest("ReferenceTableDataModel starting refresh from DB");
    groups = new ArrayList<GroupTypeData>();

    if (currentVersioning != null) {
      for (GroupTable groupTable : getGroupTables(currentVersioning.getVersionid())) {
        final GroupTypeData data = createNewGroupTypeData(groupTable);
        groups.add(data);
      }
    }

    logger.info("ReferenceTableDataModel refreshed from DB");
  }

  private GroupTypeData createNewGroupTypeData(final GroupTable groupTable) {
    return new GroupTypeData(currentVersioning, groupTable);
  }

  @Override
  public void save() {

  }

  /**
   * Returns a list of group types by versionId
   * 
   * @return results a list of Referencetables
   */
  private Collection<GroupTable> getGroupTables(final String versionId) {

    final Map<String, GroupTable> results = new HashMap<String, GroupTable>();

    final Grouptypes whereGroupTypes = new Grouptypes(rockFactory);
    whereGroupTypes.setVersionid(versionId);
    try {
      final GrouptypesFactory grouptypeFactory = new GrouptypesFactory(rockFactory, whereGroupTypes,
          true);

      for (Grouptypes grouptype : grouptypeFactory.get()) {
        final String groupTypeName = grouptype.getGrouptype();

        GroupTable groupTable;

        if (results.get(groupTypeName) == null) {
          groupTable = createNewGroupTable(groupTypeName);
          results.put(groupTypeName, groupTable);
        } else {
          groupTable = results.get(groupTypeName);
        }

        groupTable.addGroupTypeColumns(grouptype);
      }
    } catch (SQLException e) {
      logger.log(Level.SEVERE, "SQL error in getReferenceTables", e);
      ExceptionHandler.instance().handle(e);
    } catch (RockException e) {
      logger.log(Level.SEVERE, "ROCK error in getReferenceTables", e);
    } catch (Exception e) {
      logger.log(Level.SEVERE, "FATAL error in getReferenceTables", e);
    }

    return results.values();
  }

  private GroupTable createNewGroupTable(final String groupTypeName) {
    final GroupTable groupTable = new GroupTable();
    groupTable.setTypeName(groupTypeName);
    return groupTable;
  }

  @Override
  public boolean delObj(final RockDBObject obj) {
    return false;
  }

  @Override
  public boolean modObj(final RockDBObject obj) {
    return false;
  }

  @Override
  public boolean modObj(final RockDBObject[] obj) {
    return false;
  }

  @Override
  public boolean newObj(final RockDBObject obj) {
    return false;
  }

  @Override
  public boolean validateDel(final RockDBObject obj) {
    return false;
  }

  @Override
  public boolean validateMod(final RockDBObject obj) {
    return false;
  }

  @Override
  public boolean validateNew(final RockDBObject obj) {
    return false;
  }

  @Override
  public boolean updated(final DataModel dataModel) {
    if (dataModel instanceof TechPackDataModel) {
      techPackDataModel = (TechPackDataModel) dataModel;
      if (techPackDataModel.getVersioning() != null) {
        this.setCurrentVersioning(techPackDataModel.getVersioning());
        this.setBaseVersioning(techPackDataModel.getBaseversioning());
        refresh();
      }
      return true;
    }
    return false;
  }
}
