package com.ericsson.eniq.techpacksdk.unittest.utils;

import java.util.Arrays;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.Assert;
import ssc.rockfactory.TableModificationLogger;

/**
 * 
 * @author epiituo
 * 
 */
public abstract class DatabaseAssert {

  /**
   * Asserts that the modified tables, logged by the TableModificationLogger,
   * equal the expected tables.
   * 
   * The expected tables array must contain the table names in the following
   * form:
   * 
   * [DATABASE NAME]:[TABLE NAME]
   * 
   * @param expedted
   * @param actual
   */
  public static void assertModifiedTablesEqual(String[] expected, TableModificationLogger actual) {
    TableModificationLogger logger = actual;
    String[] sortedActual = toComparableForm(logger.get());
    Arrays.sort(sortedActual);

    String[] sortedExpected = expected.clone();
    Arrays.sort(sortedExpected);
    
    // Perform the assertions
    // Check if the number of modified tables match the expected.
    if (sortedExpected.length != sortedActual.length) {
      System.out.println("assertModifiedTablesEqual(): The number of expected tables " + sortedExpected.length
          + " does not match the number of modified tables " + sortedActual.length + ".");
      System.out.print("Expected: ");
      for (int i = 0; i < sortedExpected.length; ++i) {
        System.out.print(sortedExpected[i]);
        if (i != sortedExpected.length - 1) {
          System.out.print(", ");
        }
      }
      System.out.println(".");
      System.out.print("Actual:   ");
      for (int i = 0; i < sortedActual.length; ++i) {
        System.out.print(sortedActual[i]);
        if (i != sortedActual.length - 1) {
          System.out.print(", ");
        }
      }
      System.out.println(".");
      Assert.fail("The number of modified tables does not equal the expected number.");
    }

    // Check that the modified tables match the expected ones.
    for (int i = 0; i < sortedExpected.length; ++i) {
      assertEqualsIgnoreCase(sortedExpected[i], sortedActual[i]);
    }
  }

  /**
   * An internal method for transforming the log entries into a form, in which
   * they can be compared to the strings in the table of expected changes.
   * 
   * When retrieved from the logger, the log entries are in the following form:
   * 
   * [DATABASE OPERATION] [DATABASE URL] [TABLE NAME]
   * 
   * in which the database url contains the database's name, for example:
   * 
   * jdbc:hsqldb:mem:dwhrep
   * 
   * This method transforms the entry into the following form:
   * 
   * [DATABASE NAME]:[TABLE NAME]
   * 
   * @param logEntries
   *          in the following form: [DATABASE OPERATION] [DATABASE URL] [TABLE
   *          NAME]
   * @return Log entries in the following form: [DATABASE NAME]:[TABLE NAME]
   */
  private static String[] toComparableForm(String[] logEntries) {
    String[] logEntriesInCorrectForm = new String[logEntries.length];

    for (int i = 0; i < logEntries.length; ++i) {
      String[] tokens = logEntries[i].split(" ");
      //String databaseUrl = tokens[1];
      //String[] databaseUrlTokens = databaseUrl.split(":");
      //String databaseName = databaseUrlTokens[databaseUrlTokens.length - 1];
      String tableName = tokens[tokens.length - 1];
      String tableRegx = "^Meta_.*";
      Pattern tablePattern = Pattern.compile(tableRegx,Pattern.CASE_INSENSITIVE);
      Matcher m = tablePattern.matcher(tableName);
      if (m.matches()){
    	  logEntriesInCorrectForm[i] = "etlrep"+ ":" + tableName.toUpperCase();
      } else {
          logEntriesInCorrectForm[i] =  "dwhrep"+ ":" + tableName.toUpperCase();

      }
    	  
     

      
    }
    return removeDuplicates(logEntriesInCorrectForm);
  }

  private static String[] removeDuplicates(String[] stringArray) {
    Vector<String> resultVector = new Vector<String>();

    for (int i = 0; i < stringArray.length; ++i) {
      if (!resultVector.contains(stringArray[i])) {
        resultVector.add(stringArray[i]);
      }
    }

    String[] result = new String[resultVector.size()];
    result = resultVector.toArray(result);
    return result;
  }

  /**
   * Asserts two strings to be equal (not case sensitive).
   * 
   * @param expected
   * @param actual
   */
  private static void assertEqualsIgnoreCase(String expected, String actual) {
    if (!expected.equalsIgnoreCase(actual)) {
      System.out.println("assertEqualsIgnoreCase(): Values are not equal. Expected: " + expected + ", Actual:" + actual
          + ".");
      Assert.fail("The expected changes do not equal the actual changes: " + expected + " does not equal " + actual
          + ".");
    }
  }
}
