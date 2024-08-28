package com.ericsson.eniq.glassfish;
/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */

 
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


public class RecordedHashMapTest {

  @Test
  /* Code Coverage Only ... */
  public void testDisplay() {
    final RecordedHashMap map = new RecordedHashMap();
    map.put("aa", 123);
    map.put("bb", null);
    List<String> display = map.getChanges();
    assertTrue(display.contains("aa null -> 123 (Added)"));
    assertTrue(display.contains("bb null -> null (Added)"));

    map.clearChanges();
    map.remove("aa");
    map.put("bb", "ffff");
    display = map.getChanges();
    assertTrue(display.contains("aa 123 -> null (Deleted)"));
    assertTrue(display.contains("bb null -> ffff (Modified)"));
  }

  @Test
  public void testPut() {
    final RecordedHashMap map = new RecordedHashMap();
    map.put("aa", 123);
    assertTrue(map.hasChanges());
    assertTrue("No entries were removed?", map.removed().isEmpty());
    assertTrue("No entries were modified?", map.modified().isEmpty());
    assertEquals("Only one entry was added?", 1, map.added().size());

    map.put("aa", 567);
    assertTrue(map.hasChanges());
    assertTrue("No entries were removed?", map.removed().isEmpty());
    assertTrue("No entries were modified?", map.modified().isEmpty());
    assertEquals("Only one entry was added (readded)?", 1, map.added().size());


    map.clearChanges();
    map.remove("aa");
    map.put("aa", 234);
    assertTrue(map.hasChanges());
    assertTrue("No entries were removed (it was added again after being removed)?", map.removed().isEmpty());
    assertTrue("No entries were added?", map.added().isEmpty());
    assertEquals("Only one entry was modified (readded)?", 1, map.modified().size());

    map.clearChanges();


    final Object value = map.put("bb", null);
    assertNull("New value inserted should result in null getting returned", value);
    map.clearChanges();

    map.put("bb", null);
    assertFalse(map.hasChanges());
    assertTrue("No entries were removed?", map.removed().isEmpty());
    assertTrue("Only one entry was modified?", map.modified().isEmpty());
    assertTrue("No entries were added?", map.added().isEmpty());


    map.put("cc", 98754);
    map.clearChanges();
    map.put("cc", null);
    assertTrue(map.hasChanges());
    assertTrue("No entries were removed?", map.removed().isEmpty());
    assertTrue("No entries were added?", map.added().isEmpty());
    assertEquals("Only one entry was modified?", 1, map.modified().size());

    map.put("cc", null);
    map.clearChanges();
    map.put("cc", 98754);
    assertTrue(map.hasChanges());
    assertTrue("No entries were removed?", map.removed().isEmpty());
    assertTrue("No entries were added?", map.added().isEmpty());
    assertEquals("Only one entry was modified?", 1, map.modified().size());

  }

  @Test
  public void testRemove() {
    final RecordedHashMap map = new RecordedHashMap();


    map.remove("aa");
    assertFalse(map.hasChanges());

    map.put("aa", 11);
    map.clearChanges();


    map.remove("aa");
    assertFalse("Entry was not removed?", map.containsKey("aa"));
    assertTrue(map.hasChanges());
    assertEquals("Only one entry was removed?", 1, map.removed().size());
    assertTrue("No entries were added after reset?", map.added().isEmpty());
    assertTrue("No entries were modified?", map.modified().isEmpty());

    map.clearChanges();
    assertFalse("Changes were cleared but Map is still showing them?", map.hasChanges());

  }

  @Test
  public void testAddNew() {
    final RecordedHashMap map = new RecordedHashMap();
    assertFalse(map.hasChanges());


    map.put("aaa", 123);
    assertTrue(map.hasChanges());

    assertTrue("No entries were removed?", map.removed().isEmpty());
    assertTrue("No entries were modified?", map.modified().isEmpty());
    assertEquals("Only one entry was added?", 1, map.added().size());

    map.clearChanges();
    assertFalse("Changes were cleared but Map is still showing them?", map.hasChanges());
  }
}