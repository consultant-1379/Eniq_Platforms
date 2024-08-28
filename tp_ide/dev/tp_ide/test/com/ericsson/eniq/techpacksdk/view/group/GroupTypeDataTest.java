package com.ericsson.eniq.techpacksdk.view.group;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.distocraft.dc5000.repository.dwhrep.Versioning;

public class GroupTypeDataTest {

  private static final String VERSION_ID = "VERSION_ID";

  GroupTypeData groupTypeData;
  @Before
  public void setUp() {
    Versioning versioning = createVersioning(VERSION_ID);
    groupTypeData = new GroupTypeData(versioning);
  }

  @Test
  public void checkThatTypeNameIsSetToDefault() {
    GroupTable groupTable = groupTypeData.getGroupTable();
    assertEquals("NEW_GROUP_TYPE", groupTable.getTypeName());
  }

  @Test
  public void checkThatVersionIdIsCorrect() {
    GroupTable groupTable = groupTypeData.getGroupTable();
    assertEquals(VERSION_ID, groupTable.getVersionid());
  }

  @Test
  public void checkThatVersionIdIsSetCorrectly() {
    String newVersionId = "NEW_VERSION_ID";
    Versioning versioning = createVersioning(newVersionId);
    groupTypeData.setVersioning(versioning);
    assertEquals(newVersionId, groupTypeData.getVersioning().getVersionid());
  }

  @Test
  public void checkThatGroupTableIsSetCorrectly() {
    String newTypeName = "NEW_TYPE_NAME";
    GroupTable groupTable = new GroupTable();
    groupTable.setTypeName(newTypeName);
    groupTypeData.setGroupTable(groupTable);
    assertEquals(newTypeName, groupTypeData.getGroupTable().getTypeName());
  }

  @Test
  public void checkGroupTableIsCorrectlyPassedDuringConstruction() {
    String newVersionId = "NEW_VERSION_ID";
    String groupTypeName = "GROUP_TYPE_NAME";
    Versioning versioning = createVersioning(newVersionId);
    GroupTable gpTable = new GroupTable();
    gpTable.setTypeName(groupTypeName);
    groupTypeData = new GroupTypeData(versioning, gpTable);
    assertEquals(groupTypeName, groupTypeData.getGroupTable().getTypeName());
  }

  private Versioning createVersioning(final String versionId) {
    Versioning versioning = new Versioning(null);
    versioning.setVersionid(versionId);
    return versioning;
  }
}
