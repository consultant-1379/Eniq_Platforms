/**
 * Collection of common functions in Busyhourcfg
 */
package com.ericsson.eniq.busyhourcfg.common;

import java.util.List;

/**
 * @author eheijun
 * 
 */
public class Utils {

  private Utils() {
  }

  public static Integer replaceNull(final Integer value) {
    if (value == null) {
      return Integer.valueOf(0);
    }
    return value;
  }

  public static String replaceNull(final String value) {
    if (value == null) {
      return "";
    }
    return value;
  }

  public static Double replaceNull(final Double value) {
    if (value == null) {
      return new Double(0);
    }
    return value;
  }

  public static Long replaceNull(final Long value) {
    if (value == null) {
      return Long.valueOf(0);
    }
    return value;
  }

  public static Integer booleanToInteger(final Boolean value) {
    if ((value != null) && (value.booleanValue())) {
      return Integer.valueOf(1);
    }
    return Integer.valueOf(0);
  }

  public static String booleanToString(final Boolean value) {
    if ((value != null) && (value.booleanValue())) {
      return Constants.TRUE;
    }
    return Constants.FALSE;
  }
  
  public static Boolean stringToBoolean(final String value) {
    if ((value != null) && (Boolean.valueOf(value))) {
      return Boolean.TRUE;
    }
    return Boolean.FALSE;
  }

  public static Boolean integerToBoolean(final Integer value) {
    if ((value == null) || (value.intValue() == 0)) {
      return Boolean.valueOf(false);
    }
    return Boolean.valueOf(true);
  }
  
  public static Integer stringToInteger(final String value) {
    if ((value != null) && (!value.trim().equals(""))) {
      return new Integer(value);
    }
    return Integer.valueOf(0);
  }

  public static Integer stringToIntegerDef(final String value, final Integer def) {
    if ((value != null) && (!value.trim().equals(""))) {
      return new Integer(value);
    }
    return def;
  }

  public static String stringListToString(final List<String> list) {
    final StringBuffer result = new StringBuffer();
    if ((list != null) && (list.size() > 0)) {
      for (String item : list) {
        if (result.length() > 0) {
          result.append("\n");
        }
        result.append(item);
      }
    }
    return result.toString();
  }
  
  /**
   * This method is used to extract digits from the end of a string. for example if the 
   * input string = CTP_PP45, this method returns 45.
   * Also note that if the string = CTP_9PP45, this method still returns 45. 
   * @param extractDigitsFromMe
   * @return
   */
  public static Integer extractDigitsFromEndOfString(final String extractDigitsFromMe) {
	  
	  if(extractDigitsFromMe.length() == 0){
		  return new Integer(0);
	  }
	  
	  char c;
	  final StringBuffer theDigits = new StringBuffer();
	  //start from the end of the String.
	  for (int i = extractDigitsFromMe.length() -1; i > 0; i--){
		  //get the char...
		  c = extractDigitsFromMe.charAt(i);
		  //is the char a digit?
		  if(Character.isDigit(c)){
			  theDigits.append(c);
		  }else{
			  //The char is not a digit, finish looking at the rest of the String.
			  break;
		  }
	  }
	  
	  //This handles the case where the BH wasn't migrated.
	  if(theDigits.length() == 0){
		  return new Integer(0);
	  }

	  //reverse the buffer and convert it into an Integer.
	  return new Integer(theDigits.reverse().toString());
  } //extractDigitsFromEndOfString

}
