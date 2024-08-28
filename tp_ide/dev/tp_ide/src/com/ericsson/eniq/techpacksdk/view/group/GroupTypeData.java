package com.ericsson.eniq.techpacksdk.view.group;

import com.distocraft.dc5000.repository.dwhrep.Versioning;

public class GroupTypeData {

  private Versioning versioning;

  private GroupTable groupTable;

  public final static String DEFAULT_NEW_NAME = "NEW_GROUP_TYPE";

  public GroupTypeData(final Versioning versioning) {
    this.versioning = versioning;
    final String typename = DEFAULT_NEW_NAME;
    final String versionid = this.versioning.getVersionid();
    this.groupTable = new GroupTable();
    this.groupTable.setTypeName(typename);
    this.groupTable.setVersionid(versionid);
  }

  public GroupTypeData(final Versioning versioning, final GroupTable groupTable) {
    this.versioning = versioning;
    this.groupTable = groupTable;
  }

  public GroupTable getGroupTable() {
    return groupTable;
  }

  public void setGroupTable(final GroupTable groupTable) {
    this.groupTable = groupTable;
  }

  public Versioning getVersioning() {
    return versioning;
  }

  public void setVersioning(final Versioning versioning) {
    this.versioning = versioning;
  }
}
