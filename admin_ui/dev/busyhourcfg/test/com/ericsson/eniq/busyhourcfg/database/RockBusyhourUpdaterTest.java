/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.database;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ericsson.eniq.busyhourcfg.data.vo.DefaultPlaceholder;
import com.ericsson.eniq.busyhourcfg.data.vo.DefaultTechPack;
import com.ericsson.eniq.busyhourcfg.data.vo.Key;
import com.ericsson.eniq.busyhourcfg.data.vo.Source;
import com.ericsson.eniq.busyhourcfg.dwhmanager.DWHStorageTimeAction;

/**
 * @author eheijun
 *
 */
@RunWith(JMock.class)
public class RockBusyhourUpdaterTest {

  private final Mockery context = new JUnit4Mockery() {

    {
      setImposteriser(ClassImposteriser.INSTANCE);
    }
  };

  public static final String TEST_APPLICATION = RockTechPackReaderTest.class.getName();

  private static final String TEST_SOME_NAME1 = "TEST_SOME_NAME1";

  private static final String TEST_SOME_NAME2 = "TEST_SOME_NAME2";

  private static final String TEST_SOME_NAME3 = "TEST_SOME_NAME3";

  private static final String TEST_SOME_VALUE1 = "TEST_SOME_VALUE1";

  private static final String TEST_SOME_VALUE2 = "TEST_SOME_VALUE2";

  private static final String TEST_SOME_TYPENAME1 = "TEST_SOME_TYPENAME1";

  private static final String TEST_SOME_TYPENAME2 = "TEST_SOME_TYPENAME2";

  private static final String TEST_SOME_TYPENAME3 = "TEST_SOME_TYPENAME3";

  /**
   * Test method for {@link com.ericsson.eniq.busyhourcfg.database.RockBusyhourUpdater#savePlaceholder(com.ericsson.eniq.busyhourcfg.data.vo.Placeholder)}.
   */
  @Test
  public void testSavePlaceholder() {
    try {
      final RockDatabaseSession rockDatabaseSession = context.mock(RockDatabaseSession.class);
      final DWHStorageTimeAction mockStorageTimeAction = context.mock(DWHStorageTimeAction.class);
      final DefaultPlaceholder mockPlaceholder = context.mock(DefaultPlaceholder.class);
      context.checking(new Expectations() {
        {
          // set values for database session mock 
          allowing(rockDatabaseSession).setPlaceholder(mockPlaceholder);
        }
      });
      final RockBusyhourUpdater rockBusyhourUpdater = new RockBusyhourUpdater(rockDatabaseSession, mockStorageTimeAction);
      rockBusyhourUpdater.savePlaceholder(mockPlaceholder);
    } catch (Exception e) {
      fail("Saving placeholder failed: " + e.getMessage());
    }
  }

  /**
   * Test method for {@link com.ericsson.eniq.busyhourcfg.database.RockBusyhourUpdater#validatePlaceholder(com.ericsson.eniq.busyhourcfg.data.vo.Placeholder)}.
   */
  @Test
  public void testValidatePlaceholderWithSources() {
    try {
      final RockDatabaseSession rockDatabaseSession = context.mock(RockDatabaseSession.class);
      final DWHStorageTimeAction mockStorageTimeAction = context.mock(DWHStorageTimeAction.class);
      final DefaultPlaceholder mockPlaceholder = context.mock(DefaultPlaceholder.class);
      final Source mockSource1 = context.mock(Source.class, TEST_SOME_TYPENAME1);
      final Source mockSource2 = context.mock(Source.class, TEST_SOME_TYPENAME2);
      final Source mockSource3 = context.mock(Source.class, TEST_SOME_TYPENAME3);
      final List<Source> sources = new ArrayList<Source>();
      sources.add(mockSource1);
      sources.add(mockSource2);
      final List<Key> keys = new ArrayList<Key>();
      
      context.checking(new Expectations() {
        {
          allowing(mockSource1).getTypename();
          will(returnValue(TEST_SOME_TYPENAME1));
          allowing(mockSource2).getTypename();
          will(returnValue(TEST_SOME_TYPENAME2));
          allowing(mockSource3).getTypename();
          will(returnValue(TEST_SOME_TYPENAME1));
          allowing(mockPlaceholder).getSources();
          will(returnValue(sources));
          allowing(mockPlaceholder).getKeys();
          will(returnValue(keys));
        }
      });
      final RockBusyhourUpdater rockBusyhourUpdater = new RockBusyhourUpdater(rockDatabaseSession, mockStorageTimeAction);
      String result = rockBusyhourUpdater.validatePlaceholder(mockPlaceholder);
      assertTrue(result.equals(""));
      sources.add(mockSource3);
      result = rockBusyhourUpdater.validatePlaceholder(mockPlaceholder);
      assertFalse(result.equals(""));
    } catch (Exception e) {
      fail("Validating placeholder failed: " + e.getMessage());
    }
  }

  /**
   * Test method for {@link com.ericsson.eniq.busyhourcfg.database.RockBusyhourUpdater#validatePlaceholder(com.ericsson.eniq.busyhourcfg.data.vo.Placeholder)}.
   */
  @Test
  public void testValidatePlaceholderWithKeys() {
    try {
      final RockDatabaseSession rockDatabaseSession = context.mock(RockDatabaseSession.class);
      final DWHStorageTimeAction mockStorageTimeAction = context.mock(DWHStorageTimeAction.class);
      final DefaultPlaceholder mockPlaceholder = context.mock(DefaultPlaceholder.class);
      final List<Source> sources = new ArrayList<Source>();
      final Key mockKey1 = context.mock(Key.class, TEST_SOME_NAME1);
      final Key mockKey2 = context.mock(Key.class, TEST_SOME_NAME2);
      final Key mockKey3 = context.mock(Key.class, TEST_SOME_NAME3);
      final Source source1 = context.mock(Source.class, "SOURCE_NAME1");
      final Source source2 = context.mock(Source.class, "SOURCE_NAME2");
      final List<Key> keys = new ArrayList<Key>();
      keys.add(mockKey1);
      keys.add(mockKey2);
      sources.add(source1);
      sources.add(source2);
      
      context.checking(new Expectations() {
        {
          allowing(mockKey1).getKeyName();
          will(returnValue(TEST_SOME_NAME1));
          allowing(mockKey1).getKeyValue();
          will(returnValue(TEST_SOME_VALUE1));
          allowing(mockKey2).getKeyName();
          will(returnValue(TEST_SOME_NAME2));
          allowing(mockKey2).getKeyValue();
          will(returnValue(TEST_SOME_VALUE2));
          allowing(mockKey3).getKeyName();
          will(returnValue(TEST_SOME_NAME1));
          allowing(mockKey3).getKeyValue();
          will(returnValue(TEST_SOME_VALUE1));
          allowing(source1).getTypename();
          will(returnValue("SOURCE_VALUE1"));
          allowing(source2).getTypename();
          will(returnValue("SOURCE_VALUE2"));
          allowing(mockPlaceholder).getSources();
          will(returnValue(sources));
          allowing(mockPlaceholder).getKeys();
          will(returnValue(keys));
        }
      });
      final RockBusyhourUpdater rockBusyhourUpdater = new RockBusyhourUpdater(rockDatabaseSession, mockStorageTimeAction);
      String result = rockBusyhourUpdater.validatePlaceholder(mockPlaceholder);
      assertTrue(result.equals(""));
      keys.add(mockKey3);
      result = rockBusyhourUpdater.validatePlaceholder(mockPlaceholder);
      assertFalse(result.equals(""));
    } catch (Exception e) {
      fail("Validating placeholder failed: " + e.getMessage());
    }
  }

  /**
   * Test method for {@link com.ericsson.eniq.busyhourcfg.database.RockBusyhourUpdater#recreateViews(com.ericsson.eniq.busyhourcfg.data.vo.TechPack)}.
   */
  @Test
  public void testRecreateViews() {
    try {
      final DWHStorageTimeAction mockStorageTimeAction = context.mock(DWHStorageTimeAction.class);
      final DefaultTechPack mockTechpack = context.mock(DefaultTechPack.class);
      final RockDatabaseSession rockDatabaseSession = context.mock(RockDatabaseSession.class);
      context.checking(new Expectations() {
        {
          // set values for database session mock 
          allowing(rockDatabaseSession).performBusyhourActivation(mockTechpack, mockStorageTimeAction);;
        }
      });
      final RockBusyhourUpdater rockBusyhourUpdater = new RockBusyhourUpdater(rockDatabaseSession, mockStorageTimeAction);
      rockBusyhourUpdater.recreateViews(mockTechpack);
    } catch (Exception e) {
      fail("Recreating views failed: " + e.getMessage());
    }
  }

}
