/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.techpacksdk;

import java.util.zip.ZipEntry;

import org.hamcrest.Description;
import org.junit.internal.matchers.TypeSafeMatcher;

/**
 * 
 * for use in unit tests, eg when specifying jmock expectations
 * 
 * @author eemecoy
 * 
 */
public class ZipEntryMatcher extends TypeSafeMatcher<ZipEntry> {

  private ZipEntry zipEntry;

  /**
   * @param inZipEntry
   */
  public ZipEntryMatcher(ZipEntry inZipEntry) {
    zipEntry = inZipEntry;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.junit.internal.matchers.TypeSafeMatcher#matchesSafely(java.lang.Object)
   */
  @Override
  public boolean matchesSafely(ZipEntry zipEntryToMatch) {
    String zipEntryName = zipEntry.getName();
    String zipEntryToMatchName = zipEntryToMatch.getName();
    return zipEntryName.equals(zipEntryToMatchName);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.hamcrest.SelfDescribing#describeTo(org.hamcrest.Description)
   */
  @Override
  public void describeTo(Description description) {
    description.appendText("File names should match");
  }

  public static ZipEntryMatcher zipEntryEquals(ZipEntry zipEntry) {
    return new ZipEntryMatcher(zipEntry);
  }

}
