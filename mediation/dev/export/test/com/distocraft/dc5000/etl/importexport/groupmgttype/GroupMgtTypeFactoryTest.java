package com.distocraft.dc5000.etl.importexport.groupmgttype;

import static org.junit.Assert.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import org.junit.Test;

import ssc.rockfactory.RockException;

import com.distocraft.dc5000.etl.importexport.DatabaseHelper;
import com.distocraft.dc5000.repository.cache.GroupTypeDef;
import com.distocraft.dc5000.repository.dwhrep.Grouptypes;


public class GroupMgtTypeFactoryTest {

  @Test
  public void checkThatGroupMgtTypeIsNotTacOrRatGroupMgtType() throws SQLException, RockException, IOException {
    final GroupTypeDef gpType = new GroupTypeDef(new ArrayList<Grouptypes>(), "TESTGRP", "EVENT_E_TECHPACK:((123))");
    final GroupMgtType groupMgtType = GroupMgtTypeFactory.createGroupMgtType(gpType, 2, new DatabaseHelper());
    assertFalse(groupMgtType instanceof TacGroupMgtType);
    assertFalse(groupMgtType instanceof RatGroupMgtType);
  }

  @Test
  public void checkThatGroupMgtTypeIsRatGroupMgtType() throws SQLException, RockException, IOException {
    final GroupTypeDef gpType = new GroupTypeDef(new ArrayList<Grouptypes>(), "RAT_VEND_HIER3",
        "EVENT_E_TECHPACK:((123))");
    final GroupMgtType groupMgtType = GroupMgtTypeFactory.createGroupMgtType(gpType, 2, new DatabaseHelper());
    assertFalse(groupMgtType instanceof TacGroupMgtType);
    assertTrue(groupMgtType instanceof RatGroupMgtType);
  }

  @Test
  public void checkThatGroupMgtTypeIsTacGroupMgtType() throws SQLException, RockException, IOException {
    final GroupTypeDef gpType = new GroupTypeDef(new ArrayList<Grouptypes>(), "TAC", "EVENT_E_TECHPACK:((123))");
    final GroupMgtType groupMgtType = GroupMgtTypeFactory.createGroupMgtType(gpType, 2, new DatabaseHelper());
    assertTrue(groupMgtType instanceof TacGroupMgtType);
    assertFalse(groupMgtType instanceof RatGroupMgtType);
  }
}
