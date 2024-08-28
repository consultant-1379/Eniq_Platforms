package com.ericsson.eniq.glassfish;
/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to keeps track of changes to the values in the underlying map
 */
public class RecordedHashMap extends LinkedHashMap<String, Object> {
  private final Map<String, Object> ADDED = new HashMap<String, Object>();
  private final Map<String, Object> REMOVED = new HashMap<String, Object>();
  private final Map<String, Object> MODIFIED = new HashMap<String, Object>();

  public void clearChanges() {
    synchronized (REMOVED) {
      REMOVED.clear();
    }
    synchronized (ADDED) {
      ADDED.clear();
    }
    synchronized (MODIFIED) {
      MODIFIED.clear();
    }
  }

  @Override
  public Object put(final String key, final Object newValue) {
    if (REMOVED.containsKey(key)) {
      final Object value = REMOVED.remove(key);
      super.put(key, value);
    }
    if (containsKey(key)) {
      final Object originalValue = super.put(key, newValue);
      if (newValue != null && originalValue != null) {
        if (!newValue.equals(originalValue)) {
          markAsChanged(key, originalValue);
        }
      } else if (newValue == null && originalValue != null) {
        markAsChanged(key, originalValue);
      } else if (originalValue == null && newValue != null) {
        markAsChanged(key, null);
      }
      return originalValue;
    } else {
      ADDED.put(key, null);
      super.put(key, newValue);
      return null;
    }
  }

  @Override
  public Object remove(final Object key) {
    if (containsKey(key)) {
      final Object value = super.remove(key);
      REMOVED.put((String) key, value);
      return value;
    }
    return null;
  }

  private void markAsChanged(final String key, final Object oldValue) {
    if (ADDED.containsKey(key)) {
      ADDED.put(key, oldValue);
    } else {
      if (!MODIFIED.containsKey(key)) {
        MODIFIED.put(key, oldValue);
      }
    }
  }

  public boolean hasChanges() {
    return !REMOVED.isEmpty() || !ADDED.isEmpty() || !MODIFIED.isEmpty();
  }

  /* For Testing Only */
  final Map<String, Object> added() {
    return Collections.unmodifiableMap(ADDED);
  }

  /* For Testing Only */
  final Map<String, Object> removed() {
    return Collections.unmodifiableMap(REMOVED);
  }

  /* For Testing Only */
  final Map<String, Object> modified() {
    return Collections.unmodifiableMap(MODIFIED);
  }

  public List<String> getChanges() {
    final List<String> toString = new ArrayList<String>();
    if (!REMOVED.isEmpty()) {
      for (String key : REMOVED.keySet()) {
        final Object originalValue = REMOVED.get(key);
        toString.add(asString(key, originalValue, "null (Deleted)"));
      }
    }
    if (!ADDED.isEmpty()) {
      for (String key : ADDED.keySet()) {
        final Object value = get(key);
        toString.add(asString(key, null, value + " (Added)"));
      }
    }
    if (!MODIFIED.isEmpty()) {
      for (String key : MODIFIED.keySet()) {
        final Object originalValue = MODIFIED.get(key);
        final Object currentValue = super.get(key);
        toString.add(asString(key, originalValue, currentValue + " (Modified)"));
      }
    }
    return toString;
  }

  private String asString(final String key, final Object originalValue, final Object currentValue) {
    return key + " " + originalValue + " -> " + currentValue;
  }
}