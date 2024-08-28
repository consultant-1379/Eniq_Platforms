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
public class DefaultBusyhourSupportTest {

  private static final String SOME_BHLEVEL = "SOME_BH_LEVEL";

  private static final String SOME_BHTYPE = "SOME_BH_TYPE";

  private static final String OTHER_BHTYPE = "OTHER_BH_TYPE";

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
   * {@link com.ericsson.eniq.busyhourcfg.data.vo.DefaultBusyhourSupport#DefaultBusyhourSupport(java.lang.String)}
   * .
   */
  @Test
  public void testDefaultBusyhourSupport() {
    try {
      final DefaultBusyhourSupport defaultBusyhourSupport = new DefaultBusyhourSupport(SOME_BHLEVEL);
      assertEquals(SOME_BHLEVEL, defaultBusyhourSupport.getBhlevel());
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.data.vo.DefaultBusyhourSupport#getPlaceholders()}
   * .
   * 
   */
  @Test
  public void testGetPlaceholders() {
    try {
      final Placeholder placeholder1 = context.mock(Placeholder.class, SOME_BHTYPE);
      final Placeholder placeholder2 = context.mock(Placeholder.class, OTHER_BHTYPE);
      context.checking(new Expectations() {

        {
          atLeast(1).of(placeholder1).getBhtype();
          will(returnValue(SOME_BHTYPE));
          atLeast(1).of(placeholder2).getBhtype();
          will(returnValue(OTHER_BHTYPE));
        }
      });
      final DefaultBusyhourSupport defaultBusyhourSupport = new DefaultBusyhourSupport(SOME_BHLEVEL);
      defaultBusyhourSupport.addPlaceHolder(placeholder1);
      defaultBusyhourSupport.addPlaceHolder(placeholder2);
      final Placeholder placeholderTmp1 = defaultBusyhourSupport.getPlaceholders().get(0);
      assertEquals(placeholderTmp1, placeholder1);
      final Placeholder placeholderTmp2 = defaultBusyhourSupport.getPlaceholders().get(1);
      assertEquals(placeholderTmp2, placeholder2);
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.data.vo.DefaultBusyhourSupport#getPlaceholderByBhtype(java.lang.String)}
   * .
   */
  @Test
  public void testGetPlaceholderByBhtype() {
    try {
      final Placeholder placeholder1 = context.mock(Placeholder.class, SOME_BHTYPE);
      final Placeholder placeholder2 = context.mock(Placeholder.class, OTHER_BHTYPE);
      context.checking(new Expectations() {

        {
          atLeast(1).of(placeholder1).getBhtype();
          will(returnValue(SOME_BHTYPE));
          atLeast(1).of(placeholder2).getBhtype();
          will(returnValue(OTHER_BHTYPE));
        }
      });
      final DefaultBusyhourSupport defaultBusyhourSupport = new DefaultBusyhourSupport(SOME_BHLEVEL);
      defaultBusyhourSupport.addPlaceHolder(placeholder1);
      defaultBusyhourSupport.addPlaceHolder(placeholder2);
      final Placeholder placeholderTmp1 = defaultBusyhourSupport.getPlaceholderByBhtype(SOME_BHTYPE);
      assertEquals(placeholderTmp1, placeholder1);
      final Placeholder placeholderTmp2 = defaultBusyhourSupport.getPlaceholderByBhtype(OTHER_BHTYPE);
      assertEquals(placeholderTmp2, placeholder2);
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

//  /**
//   * Test method for
//   * {@link com.ericsson.eniq.busyhourcfg.data.vo.DefaultBusyhourSupport#getCurrentPlaceholder()}
//   * .
//   */
//  @Test
//  public void testGetCurrentPlaceholder() {
//    try {
//      final Placeholder placeholder1 = context.mock(Placeholder.class, SOME_BHTYPE);
//      final Placeholder placeholder2 = context.mock(Placeholder.class, OTHER_BHTYPE);
//      context.checking(new Expectations() {
//
//        {
//          atLeast(1).of(placeholder1).getBhtype();
//          will(returnValue(SOME_BHTYPE));
//          atLeast(1).of(placeholder2).getBhtype();
//          will(returnValue(OTHER_BHTYPE));
//        }
//      });
//      final DefaultBusyhourSupport defaultBusyhourSupport = new DefaultBusyhourSupport(SOME_BHLEVEL);
//      defaultBusyhourSupport.addPlaceHolder(placeholder1);
//      final Placeholder placeholderTmp1 = defaultBusyhourSupport.getCurrentPlaceholder();
//      assertEquals(placeholderTmp1, placeholder1);
//      defaultBusyhourSupport.addPlaceHolder(placeholder2);
//      defaultBusyhourSupport.setCurrentPlaceholder(placeholder2);
//      final Placeholder placeholderTmp2 = defaultBusyhourSupport.getCurrentPlaceholder();
//      assertEquals(placeholderTmp2, placeholder2);
//    } catch (Exception e) {
//      fail(e.getMessage());
//    }
//  }

}
