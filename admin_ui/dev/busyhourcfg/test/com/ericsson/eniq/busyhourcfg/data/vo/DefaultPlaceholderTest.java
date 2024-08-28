/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.data.vo;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;

import com.ericsson.eniq.busyhourcfg.common.Constants;

/**
 * @author eheijun
 * 
 */
public class DefaultPlaceholderTest {

  private static final String SOME_VERSIONID = "SOME_VERSIONID";

  private static final String SOME_BHLEVEL = "SOME_BHLEVEL";

  private static final String SOME_BHTYPE = "SOME_BHTYPE";

  private static final String SOME_TARGETVERSIONID = "SOME_TARGETVERSIONID";

  private static final String SOME_BHOBJECT = "SOME_BHOBJECT";

  private static final String SOME_TYPENAME = "SOME_TYPENAME";

  private static final String OTHER_TYPENAME = "OTHER_TYPENAME";

  private static final String THIRD_TYPENAME = "THIRD_TYPENAME";

  private static final String SOME_KEYNAME = "SOME_KEYNAME";

  private static final String OTHER_KEYNAME = "OTHER_KEYNAME";

  private static final String THIRD_KEYNAME = "THIRD_KEY";

  private static final String SOME_TARGETTYPE = "SOME_TARGETTYPE";

  private static final String OTHER_TARGETTYPE = "OTHER_TARGETTYPE";

  private static final String SOME_AGGREGATIONTYPE = "SOME_AGGREGATIONTYPE";

  //private static final Integer SOME_OFFSET = 99;

  private static final Integer SOME_LOOKBACK = 99;

  private static final Integer SOME_NTHRESHOLD = 99;

  private static final Integer SOME_PTHRESHOLD = 99;

  private JUnit4Mockery context;

  private Key key1;

  private Key key2;

  private List<Key> keys;

  private MappedType mappedtype1;

  private MappedType mappedtype2;

  private List<MappedType> mappedTypes;

  private Source source1;

  private Source source2;

  private List<Source> sources;

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    context = new JUnit4Mockery();
    key1 = context.mock(Key.class, SOME_KEYNAME);
    key2 = context.mock(Key.class, OTHER_KEYNAME);
    keys = new ArrayList<Key>();
    keys.add(key1);
    keys.add(key2);
    mappedtype1 = context.mock(MappedType.class, SOME_TARGETTYPE);
    mappedtype2 = context.mock(MappedType.class, OTHER_TARGETTYPE);
    mappedTypes = new ArrayList<MappedType>();
    mappedTypes.add(mappedtype1);
    mappedTypes.add(mappedtype2);
    source1 = context.mock(Source.class, SOME_TYPENAME);
    source2 = context.mock(Source.class, OTHER_TYPENAME);
    sources = new ArrayList<Source>();
    sources.add(source1);
    sources.add(source2);
    try {
      context.checking(new Expectations() {

        {
          allowing(source1).getTypename();
          will(returnValue(SOME_TYPENAME));
          allowing(source2).getTypename();
          will(returnValue(OTHER_TYPENAME));
          allowing(mappedtype1).getBhTargettype();
          will(returnValue(SOME_TARGETTYPE));
          allowing(mappedtype1).getEnabled();
          will(returnValue(false));

          allowing(mappedtype2).getBhTargettype();
          will(returnValue(OTHER_TARGETTYPE));
          allowing(mappedtype1).setEnabled(with(any(Boolean.class)));
          allowing(key1).getKeyName();
          will(returnValue(SOME_KEYNAME));
          allowing(key2).getKeyName();
          will(returnValue(OTHER_KEYNAME));
          
        }
      });
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.data.vo.DefaultPlaceholder#DefaultPlaceholder(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}
   * .
   */
  @Test
  public void testDefaultPlaceholder() {
    try {
      final DefaultPlaceholder defaultPlaceholder = new DefaultPlaceholder(SOME_VERSIONID, SOME_BHLEVEL, SOME_BHTYPE,
          SOME_TARGETVERSIONID, SOME_BHOBJECT, SOME_AGGREGATIONTYPE, SOME_LOOKBACK, SOME_NTHRESHOLD, 
          SOME_PTHRESHOLD, null, null, null, null, null, null, null);
      assertEquals(SOME_VERSIONID, defaultPlaceholder.getVersionid());
      assertEquals(SOME_BHLEVEL, defaultPlaceholder.getBhlevel());
      assertEquals(SOME_BHTYPE, defaultPlaceholder.getBhtype());
      assertEquals(SOME_TARGETVERSIONID, defaultPlaceholder.getTargetversionid());
      assertEquals(SOME_BHOBJECT, defaultPlaceholder.getBhobject());
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.data.vo.DefaultPlaceholder#getKeys()}.
   */
  @Test
  public void testGetKeys() {
    try {
      final DefaultPlaceholder defaultPlaceholder = new DefaultPlaceholder(SOME_VERSIONID, SOME_BHLEVEL, SOME_BHTYPE,
          SOME_TARGETVERSIONID, SOME_BHOBJECT, SOME_AGGREGATIONTYPE, SOME_LOOKBACK, SOME_NTHRESHOLD, 
          SOME_PTHRESHOLD, null, null, null, null, null, null, null);
      defaultPlaceholder.setKeys(keys);
      assertTrue(defaultPlaceholder.getKeys().get(0).equals(key1));
      assertTrue(defaultPlaceholder.getKeys().get(1).equals(key2));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.data.vo.DefaultPlaceholder#getMappedTypes()}
   * .
   */
  @Test
  public void testGetMappedTypes() {
    try {
      final DefaultPlaceholder defaultPlaceholder = new DefaultPlaceholder(SOME_VERSIONID, SOME_BHLEVEL, SOME_BHTYPE,
          SOME_TARGETVERSIONID, SOME_BHOBJECT, SOME_AGGREGATIONTYPE, SOME_LOOKBACK, SOME_NTHRESHOLD, 
          SOME_PTHRESHOLD, null, null, null, null, null, null, null);
      defaultPlaceholder.setMappedTypes(mappedTypes);
      assertTrue(defaultPlaceholder.getMappedTypes().get(0).equals(mappedtype1));
      assertTrue(defaultPlaceholder.getMappedTypes().get(1).equals(mappedtype2));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.data.vo.DefaultPlaceholder#getSources()}
   * .
   */
  @Test
  public void testGetSources() {
    try {
      final DefaultPlaceholder defaultPlaceholder = new DefaultPlaceholder(SOME_VERSIONID, SOME_BHLEVEL, SOME_BHTYPE,
          SOME_TARGETVERSIONID, SOME_BHOBJECT, SOME_AGGREGATIONTYPE, SOME_LOOKBACK, SOME_NTHRESHOLD, 
          SOME_PTHRESHOLD, null, null, null, null, null, null, null);
      defaultPlaceholder.setSources(sources);
      assertTrue(defaultPlaceholder.getSources().get(0).equals(source1));
      assertTrue(defaultPlaceholder.getSources().get(1).equals(source2));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.data.vo.DefaultPlaceholder#addSource(java.lang.String)}
   * .
   */
  @Test
  public void testAddSource() {
    try {
      final DefaultPlaceholder defaultPlaceholder = new DefaultPlaceholder(SOME_VERSIONID, SOME_BHLEVEL, SOME_BHTYPE,
          SOME_TARGETVERSIONID, SOME_BHOBJECT, SOME_AGGREGATIONTYPE, SOME_LOOKBACK, SOME_NTHRESHOLD, 
          SOME_PTHRESHOLD, null, null, null, null, null, null, null);
      defaultPlaceholder.addSource(SOME_TYPENAME);
      defaultPlaceholder.addSource(OTHER_TYPENAME);
      assertTrue(defaultPlaceholder.getSources().get(0).getTypename().equals(SOME_TYPENAME));
      assertTrue(defaultPlaceholder.getSources().get(1).getTypename().equals(OTHER_TYPENAME));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.data.vo.DefaultPlaceholder#removeSource(com.ericsson.eniq.busyhourcfg.data.vo.Source)}
   * .
   */
  @Test
  public void testRemoveSource() {
    try {
      final DefaultPlaceholder defaultPlaceholder = new DefaultPlaceholder(SOME_VERSIONID, SOME_BHLEVEL, SOME_BHTYPE,
          SOME_TARGETVERSIONID, SOME_BHOBJECT, SOME_AGGREGATIONTYPE, SOME_LOOKBACK, SOME_NTHRESHOLD, 
          SOME_PTHRESHOLD, null, null, null, null, null, null, null);
      defaultPlaceholder.setSources(sources);
      defaultPlaceholder.removeSource(defaultPlaceholder.getSources().get(0));
      assertTrue(defaultPlaceholder.getSources().get(0).equals(source2));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }
  
  /**
   * Test method for {@link com.ericsson.eniq.busyhourcfg.data.vo.DefaultPlaceholder#addKey(java.lang.String, java.lang.String, java.lang.String)}.
   */
  @Test
  public void testAddKey() {
    try {
      final DefaultPlaceholder defaultPlaceholder = new DefaultPlaceholder(SOME_VERSIONID, SOME_BHLEVEL, SOME_BHTYPE,
          SOME_TARGETVERSIONID, SOME_BHOBJECT, SOME_AGGREGATIONTYPE, SOME_LOOKBACK, SOME_NTHRESHOLD, 
          SOME_PTHRESHOLD, null, null, null, null, null, null, null);
      defaultPlaceholder.addKey(SOME_KEYNAME, "", "");
      defaultPlaceholder.addKey(OTHER_KEYNAME, "", "");
      assertTrue(defaultPlaceholder.getKeys().get(0).getKeyName().equals(SOME_KEYNAME));
      assertTrue(defaultPlaceholder.getKeys().get(1).getKeyName().equals(OTHER_KEYNAME));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  /**
   * Test method for {@link com.ericsson.eniq.busyhourcfg.data.vo.DefaultPlaceholder#removeKey(com.ericsson.eniq.busyhourcfg.data.vo.Key)}.
   */
  @Test
  public void testRemoveKey() {
    try {
      final DefaultPlaceholder defaultPlaceholder = new DefaultPlaceholder(SOME_VERSIONID, SOME_BHLEVEL, SOME_BHTYPE,
          SOME_TARGETVERSIONID, SOME_BHOBJECT, SOME_AGGREGATIONTYPE, SOME_LOOKBACK, SOME_NTHRESHOLD, 
          SOME_PTHRESHOLD, null, null, null, null, null, null, null);
      defaultPlaceholder.setKeys(keys);
      defaultPlaceholder.removeKey(defaultPlaceholder.getKeys().get(0));
      assertTrue(defaultPlaceholder.getKeys().get(0).equals(key2));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.data.vo.DefaultPlaceholder#isCustom()}
   * .
   */
  @Test
  public void testIsCustom() {
    try {
      final DefaultPlaceholder defaultPlaceholder = new DefaultPlaceholder(SOME_VERSIONID, SOME_BHLEVEL, SOME_BHTYPE,
          SOME_TARGETVERSIONID, SOME_BHOBJECT, SOME_AGGREGATIONTYPE, SOME_LOOKBACK, SOME_NTHRESHOLD, 
          SOME_PTHRESHOLD, null, null, null, null, null, null, null);
      defaultPlaceholder.setPlaceholdertype(Constants.CUSTOM);
      assertTrue(defaultPlaceholder.isCustom());
      defaultPlaceholder.setPlaceholdertype(Constants.PRODUCT);
      assertFalse(defaultPlaceholder.isCustom());
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.data.vo.DefaultPlaceholder#isProduct()}
   * .
   */
  @Test
  public void testIsProduct() {
    try {
      final DefaultPlaceholder defaultPlaceholder = new DefaultPlaceholder(SOME_VERSIONID, SOME_BHLEVEL, SOME_BHTYPE,
          SOME_TARGETVERSIONID, SOME_BHOBJECT, SOME_AGGREGATIONTYPE, SOME_LOOKBACK, SOME_NTHRESHOLD, 
          SOME_PTHRESHOLD, null, null, null, null, null, null, null);
      defaultPlaceholder.setPlaceholdertype(Constants.PRODUCT);
      assertTrue(defaultPlaceholder.isProduct());
      defaultPlaceholder.setPlaceholdertype(Constants.CUSTOM);
      assertFalse(defaultPlaceholder.isProduct());
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  /**
   * Test method for {@link com.ericsson.eniq.busyhourcfg.data.vo.DefaultPlaceholder#undoAllChanges()}.
   */
  @Test
  public void testUndoAllChanges() {
    try {
      final DefaultPlaceholder defaultPlaceholder = new DefaultPlaceholder(SOME_VERSIONID, SOME_BHLEVEL, SOME_BHTYPE,
          SOME_TARGETVERSIONID, SOME_BHOBJECT, SOME_AGGREGATIONTYPE, SOME_LOOKBACK, SOME_NTHRESHOLD, 
          SOME_PTHRESHOLD, null, null, null, null, null, null, null);
      
      defaultPlaceholder.setSources(sources);
      defaultPlaceholder.removeSource(source1);
      defaultPlaceholder.addSource(THIRD_TYPENAME);
      
      defaultPlaceholder.setKeys(keys);
      defaultPlaceholder.removeKey(key1);
      defaultPlaceholder.addKey(THIRD_KEYNAME, "", "");

      defaultPlaceholder.setMappedTypes(mappedTypes);
      
      assertFalse(defaultPlaceholder.getSources().get(0).getTypename().equals(SOME_TYPENAME));
      assertFalse(defaultPlaceholder.getSources().get(1).getTypename().equals(OTHER_TYPENAME));
      assertFalse(defaultPlaceholder.getKeys().get(0).getKeyName().equals(SOME_KEYNAME));
      assertFalse(defaultPlaceholder.getKeys().get(1).getKeyName().equals(OTHER_KEYNAME));
      
      defaultPlaceholder.undoAllChanges();
      
      assertTrue(defaultPlaceholder.getSources().get(0).getTypename().equals(SOME_TYPENAME));
      assertTrue(defaultPlaceholder.getSources().get(1).getTypename().equals(OTHER_TYPENAME));
      assertTrue(defaultPlaceholder.getKeys().get(0).getKeyName().equals(SOME_KEYNAME));
      assertTrue(defaultPlaceholder.getKeys().get(1).getKeyName().equals(OTHER_KEYNAME));
      
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

}
