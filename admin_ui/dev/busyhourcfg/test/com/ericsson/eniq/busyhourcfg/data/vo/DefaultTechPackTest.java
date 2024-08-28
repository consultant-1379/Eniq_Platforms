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
public class DefaultTechPackTest {

  private static final String SOME_VERSIONID = "SOME_VERSIONID";
  private static final String SOME_NAME = "SOME_NAME";
  private static final String SOME_TARGETVERSIONID = "SOME_TARGETVERSIONID";
  private static final String OTHER_TARGETVERSIONID = "OTHER_TARGETVERSIONID";
  
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
   * {@link com.ericsson.eniq.busyhourcfg.data.vo.DefaultTechPack#getTargetTechPacks()}
   * .
   */
  @Test
  public void testGetTargetTechPacks() {
    try {
      final TargetTechPack targettechpack1 = context.mock(TargetTechPack.class, SOME_TARGETVERSIONID);
      final TargetTechPack targettechpack2 = context.mock(TargetTechPack.class, OTHER_TARGETVERSIONID);
      context.checking(new Expectations() {

        {
          atLeast(1).of(targettechpack1).getVersionId();
          will(returnValue(SOME_TARGETVERSIONID));
          atLeast(1).of(targettechpack2).getVersionId();
          will(returnValue(OTHER_TARGETVERSIONID));
        }
      });
      final DefaultTechPack defaultTechPack = new DefaultTechPack(SOME_VERSIONID, SOME_NAME, 0);
      defaultTechPack.addTargetTechPack(targettechpack1);
      defaultTechPack.addTargetTechPack(targettechpack2);
      final TargetTechPack targettechpackTmp1 = defaultTechPack.getTargetTechPacks().get(0);
      assertEquals(targettechpackTmp1, targettechpack1);
      final TargetTechPack targettechpackTmp2 = defaultTechPack.getTargetTechPacks().get(1);
      assertEquals(targettechpackTmp2, targettechpack2);
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.data.vo.DefaultTechPack#getTargetTechPackByVersionId(java.lang.String)}
   * .
   */
  @Test
  public void testGetTargetTechPackByVersionId() {
    try {
      final TargetTechPack targettechpack1 = context.mock(TargetTechPack.class, SOME_TARGETVERSIONID);
      final TargetTechPack targettechpack2 = context.mock(TargetTechPack.class, OTHER_TARGETVERSIONID);
      context.checking(new Expectations() {

        {
          atLeast(1).of(targettechpack1).getVersionId();
          will(returnValue(SOME_TARGETVERSIONID));
          atLeast(1).of(targettechpack2).getVersionId();
          will(returnValue(OTHER_TARGETVERSIONID));
        }
      });
      final DefaultTechPack defaultTechPack = new DefaultTechPack(SOME_VERSIONID, SOME_NAME, 0);
      defaultTechPack.addTargetTechPack(targettechpack1);
      defaultTechPack.addTargetTechPack(targettechpack2);
      final TargetTechPack targettechpackTmp1 = defaultTechPack.getTargetTechPackByVersionId(SOME_TARGETVERSIONID);
      assertEquals(targettechpackTmp1, targettechpack1);
      final TargetTechPack targettechpackTmp2 = defaultTechPack.getTargetTechPackByVersionId(OTHER_TARGETVERSIONID);
      assertEquals(targettechpackTmp2, targettechpack2);
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

//  /**
//   * Test method for
//   * {@link com.ericsson.eniq.busyhourcfg.data.vo.DefaultTechPack#getCurrentTargetTechPack()}
//   * .
//   */
//  @Test
//  public void testGetCurrentTargetTechPack() {
//    try {
//      final TargetTechPack targettechpack1 = context.mock(TargetTechPack.class, SOME_TARGETVERSIONID);
//      final TargetTechPack targettechpack2 = context.mock(TargetTechPack.class, OTHER_TARGETVERSIONID);
//      context.checking(new Expectations() {
//
//        {
//          atLeast(1).of(targettechpack1).getVersionId();
//          will(returnValue(SOME_TARGETVERSIONID));
//          atLeast(1).of(targettechpack2).getVersionId();
//          will(returnValue(OTHER_TARGETVERSIONID));
//        }
//      });
//      final DefaultTechPack defaultTechPack = new DefaultTechPack(SOME_VERSIONID);
//      defaultTechPack.addTargetTechPack(targettechpack1);
//      defaultTechPack.addTargetTechPack(targettechpack2);
//      final TargetTechPack targettechpackTmp1 = defaultTechPack.getCurrentTargetTechPack();
//      assertEquals(targettechpackTmp1, targettechpack1);
//      defaultTechPack.setCurrentTargetTechPack(targettechpack2);
//      final TargetTechPack targettechpackTmp2 = defaultTechPack.getCurrentTargetTechPack();
//      assertEquals(targettechpackTmp2, targettechpack2);
//    } catch (Exception e) {
//      fail(e.getMessage());
//    }
//  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.data.vo.DefaultTechPack#addTargetTechPack(com.ericsson.eniq.busyhourcfg.data.vo.TargetTechPack)}
   * .
   */
  @Test
  public void testAddTargetTechPack() {
    try {
      final TargetTechPack targettechpack1 = context.mock(TargetTechPack.class, SOME_TARGETVERSIONID);
      final TargetTechPack targettechpack2 = context.mock(TargetTechPack.class, OTHER_TARGETVERSIONID);
      context.checking(new Expectations() {

        {
          atLeast(1).of(targettechpack1).getVersionId();
          will(returnValue(SOME_TARGETVERSIONID));
          atLeast(1).of(targettechpack2).getVersionId();
          will(returnValue(OTHER_TARGETVERSIONID));
        }
      });
      final DefaultTechPack defaultTechPack = new DefaultTechPack(SOME_VERSIONID, SOME_NAME, 0);
      defaultTechPack.addTargetTechPack(targettechpack1);
      defaultTechPack.addTargetTechPack(targettechpack2);
      assertTrue(defaultTechPack.getTargetTechPacks().get(0).equals(targettechpack1));
      assertTrue(defaultTechPack.getTargetTechPacks().get(1).equals(targettechpack2));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

}
