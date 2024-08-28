package com.ericsson.eniq.techpacksdk.view.group;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.distocraft.dc5000.repository.dwhrep.Grouptypes;


public class GroupTableTest {

  private static final String TEST_VERSION_ID = "TEST_VERSION_ID";
  private static final String TEST_GROUP_TYPE_NAME = "TEST_GROUP_TYPE_NAME";
  GroupTable groupTable;
  @Before
  public void setUp() {
    groupTable = new GroupTable();
  }

  @Test
  public void testGroupColumnsIsInitiallyEmpty() {
    assertTrue(groupTable.getGroupTypeColumns().isEmpty());
  }

  @Test
  public void testVersionIdIsInitiallyNull() {
    assertTrue(groupTable.getVersionid() == null);
  }

  @Test
  public void testTypeNameIsInitiallyNull() {
    assertTrue(groupTable.getTypeName() == null);
  }

  @Test
  public void testGroupColumnsHasDataAfterSetting() {
    List<Grouptypes> groupTypeCols = new ArrayList<Grouptypes>();
    Grouptypes groupType = new Grouptypes(null);
    groupTypeCols.add(groupType);
    groupTable.setGroupTypeColumns(groupTypeCols);
    assertFalse(groupTable.getGroupTypeColumns().isEmpty());
  }

  @Test
  public void testVersionidHasDataAfterSetting() {
    groupTable.setVersionid(TEST_VERSION_ID);
    assertTrue(groupTable.getVersionid().equals(TEST_VERSION_ID));
  }

  @Test
  public void testTypeNameHasDataAfterSetting() {
    groupTable.setTypeName(TEST_GROUP_TYPE_NAME);
    assertTrue(groupTable.getTypeName().equals(TEST_GROUP_TYPE_NAME));
  }

  @Test
  public void testAddGroupColumn() {
    Grouptypes groupType = new Grouptypes(null);
    groupTable.addGroupTypeColumns(groupType);
    assertFalse(groupTable.getGroupTypeColumns().isEmpty());
  }
}
