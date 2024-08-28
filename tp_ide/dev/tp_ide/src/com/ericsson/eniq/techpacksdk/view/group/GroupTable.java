package com.ericsson.eniq.techpacksdk.view.group;

import java.util.List;
import java.util.Vector;

import com.distocraft.dc5000.repository.dwhrep.Grouptypes;

public class GroupTable {

  private List<Grouptypes> groupTypeColumns;

  private String versionId;

  private String typeName;

  public GroupTable() {
    groupTypeColumns = new Vector<Grouptypes>();
  }

  public void setVersionid(final String versionId) {
    this.versionId = versionId;
  }

  public String getVersionid() {
    return versionId;
  }

  public void setTypeName(final String typeName) {
    this.typeName = typeName;
  }

  public String getTypeName() {
    return typeName;
  }

  public void addGroupTypeColumns(final Grouptypes groupTypeColumn) {
    groupTypeColumns.add(groupTypeColumn);
  }

  public void setGroupTypeColumns(final List<Grouptypes> groupTypeColumns) {
    this.groupTypeColumns = groupTypeColumns;
  }

  public List<Grouptypes> getGroupTypeColumns() {
    return groupTypeColumns;
  }
}
