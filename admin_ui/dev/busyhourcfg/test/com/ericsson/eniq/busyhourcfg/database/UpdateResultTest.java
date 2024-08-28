/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.database;

import static org.junit.Assert.*;

import org.junit.Test;

import com.ericsson.eniq.busyhourcfg.database.UpdateResult.ResultType;


/**
 * @author eheijun
 *
 */
public class UpdateResultTest {

  /**
   * 
   */
  private static final String JUST_SOME_MESSAGE = "Just some message.";

  /**
   * Test method for {@link com.ericsson.eniq.busyhourcfg.database.UpdateResult#UpdateResult(java.lang.String, com.ericsson.eniq.busyhourcfg.database.UpdateResult.ResultType)}.
   */
  @Test
  public void testUpdateResultINFO() {
    try {
      UpdateResult updateResult = new UpdateResult(JUST_SOME_MESSAGE, ResultType.INFO);
      assertTrue(updateResult.isInfo());
      assertFalse(updateResult.isError());
      assertTrue(updateResult.getText().equals(JUST_SOME_MESSAGE));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  /**
   * Test method for {@link com.ericsson.eniq.busyhourcfg.database.UpdateResult#UpdateResult(java.lang.String, com.ericsson.eniq.busyhourcfg.database.UpdateResult.ResultType)}.
   */
  @Test
  public void testUpdateResultERROR() {
    try {
      UpdateResult updateResult = new UpdateResult(JUST_SOME_MESSAGE, ResultType.ERROR);
      assertFalse(updateResult.isInfo());
      assertTrue(updateResult.isError());
      assertTrue(updateResult.getText().equals(JUST_SOME_MESSAGE));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

}
