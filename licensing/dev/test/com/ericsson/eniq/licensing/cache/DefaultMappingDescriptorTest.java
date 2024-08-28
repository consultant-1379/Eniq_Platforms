/***
 * 
 */
package com.ericsson.eniq.licensing.cache;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ericsson.eniq.licensing.cache.MappingDescriptor.MappingType;


/**
 * @author ecarbjo
 *
 */
public class DefaultMappingDescriptorTest {

  /**
   * @throws java.lang.Exception
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  /**
   * @throws java.lang.Exception
   */
  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
  }

  /**
   * Test method for {@link com.ericsson.eniq.licensing.cache.DefaultMappingDescriptor#getType()}.
   */
  @Test
  public void testGetType() {
    DefaultMappingDescriptor md = new DefaultMappingDescriptor(new String[]{"test"}, MappingType.FAJ);
    assertEquals(md.getType(), MappingType.FAJ);
    
    md = new DefaultMappingDescriptor(new String[]{"test"}, MappingType.DESCRIPTION);
    assertEquals(md.getType(), MappingType.DESCRIPTION);

    md = new DefaultMappingDescriptor(new String[]{"test"}, MappingType.INTERFACE);
    assertEquals(md.getType(), MappingType.INTERFACE);
}

  /**
   * Test method for {@link com.ericsson.eniq.licensing.cache.DefaultMappingDescriptor#getName()}.
   */
  @Test
  public void testGetName() {
    DefaultMappingDescriptor md = new DefaultMappingDescriptor(new String[]{"test"}, MappingType.FAJ);
    assertEquals(md.getName(), "test");
  }

  /**
   * Test method for {@link com.ericsson.eniq.licensing.cache.DefaultMappingDescriptor#getFeatureNames()}.
   */
  @Test
  public void testGetFeatureNames() {
    DefaultMappingDescriptor md = new DefaultMappingDescriptor(null, MappingType.FAJ);
    assertNull(md.getFeatureNames());
  }

}
