/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.data.vo;

import static org.junit.Assert.*;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;

/**
 * @author eheijun
 * 
 */
public class DefaultTargetTechPackTest {

  private static final String SOME_VERSIONID = "SOME_VERSIONID";

  private static final String SOME_BHLEVEL = "SOME_BH_LEVEL";

  private static final String OTHER_BHLEVEL = "OTHER_BH_LEVEL";

  private JUnit4Mockery context;

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    context = new JUnit4Mockery();
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.data.vo.DefaultTargetTechPack#DefaultTargetTechPack(java.lang.String)}
   * .
   */
  @Test
  public void testDefaultTargetTechPack() {
    try {
      final DefaultTargetTechPack defaultTargetTechPack = new DefaultTargetTechPack(SOME_VERSIONID);
      assertEquals(SOME_VERSIONID, defaultTargetTechPack.getVersionId());
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.data.vo.DefaultTargetTechPack#getBusyhourSupports()}
   * .
   */
  @Test
  public void testGetBusyhourSupports() {
    try {
      final BusyhourSupport busyhoursupport1 = context.mock(BusyhourSupport.class, SOME_BHLEVEL);
      final BusyhourSupport busyhoursupport2 = context.mock(BusyhourSupport.class, OTHER_BHLEVEL);
      context.checking(new Expectations() {

        {
          atLeast(1).of(busyhoursupport1).getBhlevel();
          will(returnValue(SOME_BHLEVEL));
          atLeast(1).of(busyhoursupport2).getBhlevel();
          will(returnValue(OTHER_BHLEVEL));
        }
      });
      final DefaultTargetTechPack defaultTargetTechPack = new DefaultTargetTechPack(SOME_VERSIONID);
      defaultTargetTechPack.addBusyhourSupport(busyhoursupport1);
      defaultTargetTechPack.addBusyhourSupport(busyhoursupport2);
      final BusyhourSupport busyhoursupportTmp = defaultTargetTechPack.getBusyhourSupports().get(0);
      assertEquals(busyhoursupportTmp, busyhoursupport1);
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.data.vo.DefaultTargetTechPack#getBusyhourSupportByBhlevel(java.lang.String)}
   * .
   */
  @Test
  public void testGetBusyhourSupportByBhlevel() {
    try {
      final BusyhourSupport busyhoursupport1 = context.mock(BusyhourSupport.class, SOME_BHLEVEL);
      final BusyhourSupport busyhoursupport2 = context.mock(BusyhourSupport.class, OTHER_BHLEVEL);
      context.checking(new Expectations() {

        {
          atLeast(1).of(busyhoursupport1).getBhlevel();
          will(returnValue(SOME_BHLEVEL));
          atLeast(1).of(busyhoursupport2).getBhlevel();
          will(returnValue(OTHER_BHLEVEL));
        }
      });
      final DefaultTargetTechPack defaultTargetTechPack = new DefaultTargetTechPack(SOME_VERSIONID);
      defaultTargetTechPack.addBusyhourSupport(busyhoursupport1);
      defaultTargetTechPack.addBusyhourSupport(busyhoursupport2);
      final BusyhourSupport busyhoursupportTmp1 = defaultTargetTechPack.getBusyhourSupportByBhlevel(SOME_BHLEVEL);
      assertEquals(busyhoursupportTmp1, busyhoursupport1);
      final BusyhourSupport busyhoursupportTmp2 = defaultTargetTechPack.getBusyhourSupportByBhlevel(OTHER_BHLEVEL);
      assertEquals(busyhoursupportTmp2, busyhoursupport2);
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

//  /**
//   * Test method for
//   * {@link com.ericsson.eniq.busyhourcfg.data.vo.DefaultTargetTechPack#getCurrentBHSupport()}
//   * .
//   */
//  @Test
//  public void testGetCurrentBHSupport() {
//    try {
//      final BusyhourSupport busyhoursupport1 = context.mock(BusyhourSupport.class, SOME_BHLEVEL);
//      final BusyhourSupport busyhoursupport2 = context.mock(BusyhourSupport.class, OTHER_BHLEVEL);
//      context.checking(new Expectations() {
//
//        {
//          atLeast(1).of(busyhoursupport1).getBhlevel();
//          will(returnValue(SOME_BHLEVEL));
//          atLeast(1).of(busyhoursupport2).getBhlevel();
//          will(returnValue(OTHER_BHLEVEL));
//        }
//      });
//      final DefaultTargetTechPack defaultTargetTechPack = new DefaultTargetTechPack(SOME_VERSIONID);
//      defaultTargetTechPack.addBusyhourSupport(busyhoursupport1);
//      final BusyhourSupport busyhoursupportTmp1 = defaultTargetTechPack.getCurrentBHSupport();
//      assertEquals(busyhoursupportTmp1, busyhoursupport1);
//      defaultTargetTechPack.addBusyhourSupport(busyhoursupport2);
//      defaultTargetTechPack.setCurrentBHSupport(busyhoursupport2);
//      final BusyhourSupport busyhoursupportTmp2 = defaultTargetTechPack.getCurrentBHSupport();
//      assertEquals(busyhoursupportTmp2, busyhoursupport2);
//    } catch (Exception e) {
//      fail(e.getMessage());
//    }
//  }

}
