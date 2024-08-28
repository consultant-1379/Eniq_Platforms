package com.ericsson.eniq.techpacksdk.view.group;

import static org.junit.Assert.*;

import java.sql.Statement;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import org.jdesktop.application.Application;
import org.junit.BeforeClass;
import org.junit.Test;

import ssc.rockfactory.RockFactory;
import tableTreeUtils.TreeMainNode;

import com.distocraft.dc5000.repository.dwhrep.Versioning;

public class GroupTypeFactoryTest {

  private static final String GROUP_TYPE_NAME = "EVNTSRC";

  private static final String VERSION_ID = "EVENT_E_SGEH:((43))";

  Application application = null;

  private static RockFactory rockFact = null;

  private static GroupTypeDataModel groupTypeDataModel;

  boolean editable = false;

  private static Versioning versioning;

  @BeforeClass
  public static void setUp() throws Exception {
    Class.forName("org.hsqldb.jdbcDriver");
    rockFact = new RockFactory("jdbc:hsqldb:mem:testdb", "SA", "", "org.hsqldb.jdbcDriver", "con", true, -1);
    groupTypeDataModel = new GroupTypeDataModel(rockFact);

    final Statement stmt = rockFact.getConnection().createStatement();
    stmt.execute("CREATE TABLE GroupTypes (GROUPTYPE varchar(64), VERSIONID VARCHAR(128), "
        + "DATANAME VARCHAR(31), DATATYPE VARCHAR(31), DATASIZE int, DATASCALE int, NULLABLE int)");
    stmt.executeUpdate("INSERT INTO GroupTypes VALUES ('" + GROUP_TYPE_NAME + "', '" + VERSION_ID
        + "', 'EVENT_SOURCE_NAME', 'varchar', 128, 0, 0)");
    stmt.close();

    versioning = new Versioning(rockFact);
    versioning.setVersionid(VERSION_ID);
  }

  @Test
  public void checkCreateAndGetModelReturnsGroupTypeFromTable() {
    GroupTypeFactory gpTypeFactory = new GroupTypeFactory(application, groupTypeDataModel, editable, versioning);
    TreeModel treeModel = gpTypeFactory.createAndGetModel();
    Object root = treeModel.getRoot();
    assertTrue(root instanceof DefaultMutableTreeNode);

    TreeNode mainTreeNode = ((DefaultMutableTreeNode) root).getFirstChild();
    assertTrue(mainTreeNode instanceof TreeMainNode);
    assertEquals(GROUP_TYPE_NAME, mainTreeNode.toString());
  }

  @Test
  public void checkCreateEmptyNodeReturnsDefaultGroupType() {
    GroupTypeFactory gpTypeFactory = new GroupTypeFactory(application, groupTypeDataModel, editable, versioning);
    TreeMainNode mainTreeNode = gpTypeFactory.createEmptyNode();

    assertEquals(GroupTypeData.DEFAULT_NEW_NAME, mainTreeNode.toString());
  }
}
