package com.ericsson.eniq.techpacksdk.view.group;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import ssc.rockfactory.RockFactory;


public class GroupTypeParameterModelTest {

  private static final String GROUP_TABLE_TYPE_NAME = "groupTableTypeName";

  RockFactory rockFactory = null;

  GroupTable groupTable = null;

  boolean isTreeEditable = false;
  
  GroupTypeParameterModel groupTypeParam;

  @Before
  public void setUp() {
    groupTable = new GroupTable();
    groupTable.setTypeName(GROUP_TABLE_TYPE_NAME);
    groupTypeParam = new GroupTypeParameterModel(rockFactory, groupTable, isTreeEditable);
  }

  @Test
  public void checkThatGroupTableIsCorrect() {
    assertEquals(GROUP_TABLE_TYPE_NAME, groupTypeParam.getGroupTable().getTypeName());
  }

  @Test
  public void checkNodeNameIsSet() {
    final String newGroupTypeName = "newGroupTypeName";
    assertEquals(GROUP_TABLE_TYPE_NAME, groupTypeParam.getMainNodeName());
    groupTypeParam.setMainNodeName(newGroupTypeName);
    assertEquals(newGroupTypeName, groupTypeParam.getMainNodeName());
  }

  @Test
  public void checkValidateDataIsEmpty() {
    assertTrue(groupTypeParam.validateData().isEmpty());
  }
}
