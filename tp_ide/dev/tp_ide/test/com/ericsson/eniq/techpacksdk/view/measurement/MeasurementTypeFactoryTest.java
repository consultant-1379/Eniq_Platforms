package com.ericsson.eniq.techpacksdk.view.measurement;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ssc.rockfactory.RockFactory;
import tableTree.TTTableModel;

import com.distocraft.dc5000.repository.dwhrep.Measurementkey;


/**
 * Unit tests for MeasurementTypeFactory class.
 * @author ECIACAH
 *
 */
@RunWith(JMock.class)
public class MeasurementTypeFactoryTest extends TestCase {

  /** Test instance **/
  private MeasurementTypeFactory measTypeFactory = null;

  /** Mock context: **/
  protected Mockery context = new JUnit4Mockery();
  {
    // we need to mock classes, not just interfaces.
    context.setImposteriser(ClassImposteriser.INSTANCE);
  }

  /** Mocks **/
  private TTTableModel tableModelMock;
  private RockFactory rockFactoryMock;

  
  @Override
  @Before
  public void setUp() {    
    // Create the mocks:
    createMocks();
    // Initialise test instance:
    measTypeFactory = new MeasurementTypeFactory();
  }
  
  private void createMocks() {
    tableModelMock = context.mock(TTTableModel.class);
    rockFactoryMock = context.mock(RockFactory.class);
  }

  @Override
  @After
  public void tearDown() {
    context.assertIsSatisfied();
  }
  
  /**
   * Test copying a MeasurementKey.
   */
  @Test
  public void testCreateMeasKey() {
    final String typeID = "typeID1";
    
    final Measurementkey measKey = new Measurementkey(rockFactoryMock);
    measKey.setDataname("test data name");
    measKey.setDataid("test data id");
    measKey.setUnivobject("test universe object");    
    
    context.checking(new Expectations() {

      {
        // get universe folder:
        oneOf(tableModelMock).copyOf(measKey);
        will(returnValue(measKey));
      }
    });
    
    Measurementkey result = measTypeFactory.copyMeasKey(typeID, tableModelMock, tableModelMock, measKey);
    Assert.assertTrue(result instanceof Measurementkey);
    Assert.assertNotNull(result instanceof Measurementkey);
    Assert.assertTrue(result.getDataname().equals(measKey.getDataname()));
    Assert.assertTrue(result.getDataid().equals(measKey.getDataid()));
  }
  
  /**
   * Test copying a MeasurementCounter.
   */
  @Test
  public void testCopyMeasCounter() {
    final String typeID = "typeID1";
    
    final MeasurementcounterExt measCounter = new MeasurementcounterExt(rockFactoryMock);
    measCounter.setDataname("test data name");
    measCounter.setDataid("test data id");
    measCounter.setUnivobject("test universe object");
    measCounter.setColnumber(new Long(5));
    
    context.checking(new Expectations() {

      {
        // get universe folder:
        oneOf(tableModelMock).copyOf(measCounter);
        will(returnValue(measCounter));
      }
    });
    
    MeasurementcounterExt result = measTypeFactory.copyMeasCounter(typeID, tableModelMock, tableModelMock, measCounter);
    Assert.assertTrue(result instanceof MeasurementcounterExt);
    Assert.assertNotNull(result instanceof MeasurementcounterExt);
    Assert.assertTrue(result.getDataname().equals(measCounter.getDataname()));
    Assert.assertTrue(result.getDataid().equals(measCounter.getDataid()));
    Assert.assertTrue(result.getColnumber().equals(new Long(5)));
  }
  
  /**
   * Tests situation where MeasurementCounter being copied has null values.
   */
  @Test
  public void testCopyMeasCounterHandleNullValues() {
    final String typeID = "typeID1";
    
    final MeasurementcounterExt measCounter = new MeasurementcounterExt(rockFactoryMock);
    measCounter.setDataname(null);
    measCounter.setDataid(null);
    measCounter.setUnivobject(null);
    measCounter.setColnumber(null);
    
    context.checking(new Expectations() {

      {
        // get universe folder:
        oneOf(tableModelMock).copyOf(measCounter);
        will(returnValue(measCounter));
      }
    });
    
    MeasurementcounterExt result = measTypeFactory.copyMeasCounter(typeID, tableModelMock, tableModelMock, measCounter);
    Assert.assertTrue(result instanceof MeasurementcounterExt);
    Assert.assertNotNull(result instanceof MeasurementcounterExt);
    Assert.assertTrue(result.getDataname().equals(""));
    Assert.assertTrue(result.getDataid().equals(""));
    Assert.assertTrue(result.getUnivobject().equals(""));
    Assert.assertTrue(result.getColnumber().equals(new Long(0)));
  }
  
  /**
   * Tests situation where MeasurementKey being copied has null values.
   */
  @Test
  public void testCopyMeasKeyHandleNullValues() {
    final String typeID = "typeID1";
    
    final Measurementkey measCounter = new Measurementkey(rockFactoryMock);
    measCounter.setDataname(null);
    measCounter.setDataid(null);
    measCounter.setUnivobject(null);
    measCounter.setColnumber(null);
    
    context.checking(new Expectations() {

      {
        // get universe folder:
        oneOf(tableModelMock).copyOf(measCounter);
        will(returnValue(measCounter));
      }
    });
    
    Measurementkey result = measTypeFactory.copyMeasKey(typeID, tableModelMock, tableModelMock, measCounter);
    Assert.assertTrue(result instanceof Measurementkey);
    Assert.assertNotNull(result instanceof Measurementkey);
    Assert.assertTrue(result.getDataname().equals(""));
    Assert.assertTrue(result.getDataid().equals(""));
    Assert.assertTrue(result.getUnivobject().equals(""));
    Assert.assertTrue(result.getColnumber().equals(new Long(0)));
  }

}
