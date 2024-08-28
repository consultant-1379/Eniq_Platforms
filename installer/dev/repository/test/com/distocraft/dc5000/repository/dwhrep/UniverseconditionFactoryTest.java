package com.distocraft.dc5000.repository.dwhrep;

import static org.junit.Assert.*;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Vector;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ssc.rockfactory.RockFactory;

/**
 * Test class for UniverseconditionFactory. Testing handling of all the objects in
 * Universecondition table.
 */
public class UniverseconditionFactoryTest {

  private static UniverseconditionFactory objUnderTest;

  private static RockFactory rockFactory;

  private static Universecondition whereObject;

  private static Connection con = null;

  private static Statement stmt;

  private static Field vec;
  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {

    /* Reflecting the private fields */
    vec = UniverseconditionFactory.class.getDeclaredField("vec");
    vec.setAccessible(true);

    /* Creating connection for rockfactory */
    try {
      Class.forName("org.hsqldb.jdbcDriver").newInstance();
      con = DriverManager.getConnection("jdbc:hsqldb:mem:testdb", "sa", "");
    } catch (Exception e) {
      e.printStackTrace();
    }
    stmt = con.createStatement();
    stmt.execute("CREATE TABLE Universecondition ( VERSIONID VARCHAR(31)  ,CLASSNAME VARCHAR(31) ,UNIVERSEEXTENSION VARCHAR(31) ,UNIVERSECONDITION VARCHAR(31) ,DESCRIPTION VARCHAR(31) ,CONDWHERE VARCHAR(31) ,AUTOGENERATE INTEGER  ,CONDOBJCLASS VARCHAR(31) ,CONDOBJECT VARCHAR(31) ,PROMPTTEXT VARCHAR(31) ,MULTISELECTION INTEGER  ,FREETEXT INTEGER  ,OBJ_BH_REL INTEGER  ,ELEM_BH_REL INTEGER  ,INHERITANCE INTEGER  ,ORDERNRO BIGINT )");
    
    /* Initializing rockfactory */
    rockFactory = new RockFactory("jdbc:hsqldb:mem:testdb", "sa", "", "org.hsqldb.jdbcDriver", "con", true);

    /* Creating where object which tells what sort of query is to be done */
    whereObject = new Universecondition(rockFactory);
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {

    /* Cleaning up after test */
    stmt.execute("DROP TABLE Universecondition");
    con = null;
    objUnderTest = null;
  }
  
  @Before
  public void setUp() throws Exception {

    /* Adding example data to table */
	  stmt.executeUpdate("INSERT INTO Universecondition VALUES( 'testVERSIONID3'  ,'testCLASSNAME3'  ,'testUNIVERSEEXTENSION3'  ,'testUNIVERSECONDITION3'  ,'testDESCRIPTION3'  ,'testCONDWHERE3'  ,3  ,'testCONDOBJCLASS3'  ,'testCONDOBJECT3'  ,'testPROMPTTEXT3'  ,3  ,3  ,3  ,3  ,3  ,3 )");
	  stmt.executeUpdate("INSERT INTO Universecondition VALUES( 'testVERSIONID2'  ,'testCLASSNAME2'  ,'testUNIVERSEEXTENSION2'  ,'testUNIVERSECONDITION2'  ,'testDESCRIPTION2'  ,'testCONDWHERE2'  ,2  ,'testCONDOBJCLASS2'  ,'testCONDOBJECT2'  ,'testPROMPTTEXT2'  ,2  ,2  ,2  ,2  ,2  ,2 )");
	  stmt.executeUpdate("INSERT INTO Universecondition VALUES( 'testVERSIONID1'  ,'testCLASSNAME1'  ,'testUNIVERSEEXTENSION1'  ,'testUNIVERSECONDITION1'  ,'testDESCRIPTION1'  ,'testCONDWHERE1'  ,1  ,'testCONDOBJCLASS1'  ,'testCONDOBJECT1'  ,'testPROMPTTEXT1'  ,1  ,1  ,1  ,1  ,1  ,1 )");

    /* Initializing tested object before each test */
    objUnderTest = new UniverseconditionFactory(rockFactory, whereObject);
  }
  
  @After
  public void tearDown() throws Exception {

    /* Cleaning up after each test */
    stmt.executeUpdate("DELETE FROM Universecondition");
    objUnderTest = null;
  }
  
  /**
   * Testing UniverseconditionFactory constructor. All rows found from Universecondition
   * table are put into vector.
   */
  @Test
  public void testUniverseconditionFactoryConstructorWithWhereObject() throws Exception {

    /* Calling the tested constructor */
    objUnderTest = new UniverseconditionFactory(rockFactory, whereObject);

    /* Asserting all Universeconditions are found and put into vector */
    try {
      Vector<Universecondition> actualVector = (Vector) vec.get(objUnderTest);
      String actual = actualVector.size() + ", " + actualVector.get(0).getVersionid() + ", " +  actualVector.get(1).getVersionid() + ", " +  actualVector.get(2).getVersionid();
      String expected = "3, testVERSIONID3, testVERSIONID2, testVERSIONID1";
      assertEquals(expected, actual);
    } catch (ArrayIndexOutOfBoundsException aioobe) {
      fail("Test Failed - One or more Universeconditions was not loaded from the table!\n " + aioobe);
    }
  }
  
  /**
   * Testing constructor with negative case where rockfactory object is null.
   */
  @Test
  public void testUniverseconditionFactoryConstructorWithWhereObjectNullRockfactory() throws Exception {

    /* Asserting that variables are initialized */
    try {
      objUnderTest = new UniverseconditionFactory(null, whereObject);
      fail("Test failed - NullPointerException was expected as rockfactory was initialized as null!");
    } catch (NullPointerException npe) {
      // test passed
    } catch (Exception e) {
      fail("Test failed - Unexpected exception occurred!\n" + e);
    }
  }
  
  /**
   * Testing UniverseconditionFactory constructor. All rows found from Universecondition
   * table are put into vector and data validation is on.
   */
  @Test
  public void testUniverseconditionFactoryConstructorWithValidate() throws Exception {

    /* Calling the tested constructor */
    objUnderTest = new UniverseconditionFactory(rockFactory, whereObject, true);

    /* Asserting all Universeconditions are found and put into vector */
    try {     
      Vector<Universecondition> actualVector = (Vector) vec.get(objUnderTest);
      String actual = actualVector.size() + ", " + actualVector.get(0).isValidateData() + ", " +  actualVector.get(1).isValidateData() + ", " +  actualVector.get(2).isValidateData();
      String expected = 3 + ", " + true + ", " + true + ", " + true;
      assertEquals(expected, actual);
    } catch (ArrayIndexOutOfBoundsException aioobe) {
      fail("Test Failed - One or more aggregations was not loaded from the table!\n " + aioobe);
    }
  }
  
  /**
   * Testing constructor with negative case where rockfactory object is null.
   */
  @Test
  public void testUniverseconditionFactoryConstructorWithValidateNullRockfactory() throws Exception {

    /* Asserting that variables are initialized */
    try {
      objUnderTest = new UniverseconditionFactory(null, whereObject, true);
      fail("Test failed - NullPointerException was expected as rockfactory was initialized as null!");
    } catch (NullPointerException npe) {
      // test passed
    } catch (Exception e) {
      fail("Test failed - Unexpected exception occurred!\n" + e);
    }
  }
  
  /**
   * Testing UniverseconditionFactory constructor. All rows found from Universecondition
   * table are put into vector and data validation is on.
   */
  @Test
  public void testUniverseconditionFactoryConstructorWithOrderClause() throws Exception {

    /* Calling the tested constructor */
    objUnderTest = new UniverseconditionFactory(rockFactory, whereObject, "ORDER BY VERSIONID");

    /* Asserting all Universeconditions are found and put into vector */
    try {
      Vector<Universecondition> actualVector = (Vector) vec.get(objUnderTest);
      String actual = actualVector.size() + ", " + actualVector.get(0).getVersionid() + ", " +  actualVector.get(1).getVersionid() + ", " +  actualVector.get(2).getVersionid();
      String expected = "3, testVERSIONID1, testVERSIONID2, testVERSIONID3";
      assertEquals(expected, actual);
    } catch (ArrayIndexOutOfBoundsException aioobe) {
      fail("Test Failed - One or more Universeconditions was not loaded from the table!\n " + aioobe);
    }
  }
  
  /**
   * Testing constructor with negative case where rockfactory object is null.
   */
  @Test
  public void testUniverseconditionFactoryConstructorWithOrderClauseNullRockfactory() throws Exception {

    /* Asserting that variables are initialized */
    try {
      objUnderTest = new UniverseconditionFactory(null, whereObject, "ORDER BY VERSIONID");
      fail("Test failed - NullPointerException was expected as rockfactory was initialized as null!");
    } catch (NullPointerException npe) {
      // test passed
    } catch (Exception e) {
      fail("Test failed - Unexpected exception occurred!\n" + e);
    }
  }
  
  /**
   * Testing Element retrieving from a vector at certain location.
   */
  @Test
  public void testGetElementAtWithGenericInput() throws Exception {

    assertEquals( "testVERSIONID2" , objUnderTest.getElementAt(1).getVersionid().toString());
  }
  
  /**
   * Testing Element retrieving from a vector at certain location.
   */
  @Test
  public void testGetElementAtOutOfBounds() throws Exception {

    assertEquals(null, objUnderTest.getElementAt(5));
  }
  
  /**
   * Testing size retrieving of the vector containing Universecondition objects.
   */
  @Test
  public void testSize() throws Exception {

    assertEquals(3, objUnderTest.size());
  }
  
  /**
   * Testing vector retrieving containing Universecondition objects.
   */
  @Test
  public void testGet() throws Exception {

    try {
      Vector<Universecondition> actualVector = objUnderTest.get();
      String actual = actualVector.size() + ", " + actualVector.get(0).getVersionid() + ", " +  actualVector.get(1).getVersionid() + ", " +  actualVector.get(2).getVersionid();
      String expected = "3, testVERSIONID3, testVERSIONID2, testVERSIONID1";
      assertEquals(expected, actual);
    } catch (ArrayIndexOutOfBoundsException aioobe) {
      fail("Test Failed - One or more aggregations was not loaded from the table!\n " + aioobe);
    }
  }
  
  /**
   * Test comparing two Universecondition objects. True is returned if the two vectors
   * containing the objects are the same, otherwise false.
   */
  @Test
  public void testEqualsWithSameObjects() throws Exception {

    /* Creating another vector with the same objects */
    Vector otherVector = new Vector();
    for (int i = 3; i > 0; i--) {
      Universecondition testObject = new Universecondition(rockFactory, "testVERSIONID" + i, "testCLASSNAME" + i, "testUNIVERSEEXTENSION" + i, "testUNIVERSECONDITION" + i);
      otherVector.add(testObject);
    }

    /* Asserting the two vectors are the same */
    assertEquals(true, objUnderTest.equals(otherVector));
  }
  
  /**
   * Test comparing two Universecondition objects. True is returned if the two vectors
   * containing the objects are the same, otherwise false.
   */
  @Test
  public void testEqualsWithSameVector() throws Exception {

    /* Creating another vector with the same vector */
    Vector otherVector = (Vector) vec.get(objUnderTest);

    /* Asserting the two vectors are the same */
    assertEquals(true, objUnderTest.equals(otherVector));
  }
  
  /**
   * Test comparing two Universecondition objects. True is returned if the two vectors
   * containing the objects are the same, otherwise false.
   */
  @Test
  public void testEqualsWithNullVector() throws Exception {

    Vector otherVector = null;
    assertEquals(false, objUnderTest.equals(otherVector));
  }
  
  /**
   * Test comparing two Universecondition objects. True is returned if the two vectors
   * containing the objects are the same, otherwise false.
   */
  @Test
  public void testEqualsWithDifferentAmountOfObjects() throws Exception {

    /* Creating another vector with only one object */
    Vector otherVector = new Vector();
    Universecondition testObject = new Universecondition(rockFactory, "testVERSIONID1", "testCLASSNAME1", "testUNIVERSEEXTENSION1", "testUNIVERSECONDITION1");
    otherVector.add(testObject);
    
    /* Asserting the two vectors are the same */
    assertEquals(false, objUnderTest.equals(otherVector));
  }
  
  /**
   * Test comparing two Universecondition objects. True is returned if the two vectors
   * containing the objects are the same, otherwise false.
   */
  @Test
  public void testEqualsWithDifferentObjects() throws Exception {

    /* Creating another vector with different objects */
    Vector otherVector = new Vector();
    for (int i = 1; i < 4; i++) {
      Universecondition testObject = new Universecondition(rockFactory, "testVERSIONID" + i, "testCLASSNAME" + i, "testUNIVERSEEXTENSION" + i, "testUNIVERSECONDITION" + i);
      otherVector.add(testObject);
    }
    
    /* Asserting the two vectors are the same */
    assertEquals(false, objUnderTest.equals(otherVector));
  }
  
  /**
   * Test deleting objects from the database.
   */
  @Test
  public void testDeleteDB() throws Exception {
    
    /* Calling the tested object */
    String actual = objUnderTest.deleteDB() + ", ";
    
    /* Getting row count */
    int rows = 0;
    ResultSet res = stmt.executeQuery("SELECT COUNT(*) FROM Universecondition");
    while (res.next()) {
      rows = res.getInt(1);
    }
    
    /* Asserting object is deleted from the database */
    actual += rows;
    assertEquals(3 + ", " + 0, actual);
  }
  
  /**
   * Test object cloning.
   */
  @Test
  public void testClone() throws Exception {
    
    /* Asserting if cloning works */
    Object clonedObject = objUnderTest.clone();
    assertEquals(UniverseconditionFactory.class, clonedObject.getClass());
  }
}