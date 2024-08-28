package com.ericsson.navigator.esm.manager.snmp;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;
import java.util.TimeZone;

import com.adventnet.snmp.mibs.MibOperations;
import com.adventnet.snmp.snmp2.SnmpVarBind;

/**
 * Object used to handle DateAndTime Textual convention (TC):
 * 
 * SNMPv2-TC: A date-time specification.
 * 
 * field octets contents range ----- ------ -------- ----- 1 1-2 year 0..65536 2
 * 3 month 1..12 3 4 day 1..31 4 5 hour 0..23 5 6 minutes 0..59 6 7 seconds
 * 0..60 (use 60 for leap-second) 7 8 deci-seconds 0..9 8 9 direction from UTC
 * '+' / '-' 9 10 hours from UTC 0..11 10 11 minutes from UTC 0..59
 * 
 * For example, Tuesday May 26, 1992 at 1:30:15 PM EDT would be displayed as:
 * 
 * 1992-5-26,13:30:15.0,-4:0
 * 
 * Note that if only local time is known, then timezone information (fields
 * 8-10) is not present.
 */
public abstract class DateAndTime {
	
	private static final MibOperations OPERATIONS = new MibOperations();
	
	public static Date getTime(final SnmpVarBind varBind) throws DateAndTimeException {
		final String hexDate = OPERATIONS.toString(varBind.getVariable(), varBind.getObjectID());
		try {
			final StringTokenizer stringtokenizer = new StringTokenizer(hexDate);
	        final GregorianCalendar gregoriancalendar = new GregorianCalendar();
	        gregoriancalendar.set(1, Integer.valueOf(
	        		(new StringBuilder()).append(stringtokenizer.nextToken()).append(
	        				stringtokenizer.nextToken()).toString(), 16).intValue());
	        gregoriancalendar.set(2, Integer.valueOf(stringtokenizer.nextToken(), 16).intValue() - 1);
	        gregoriancalendar.set(5, Integer.valueOf(stringtokenizer.nextToken(), 16).intValue());
	        gregoriancalendar.set(11, Integer.valueOf(stringtokenizer.nextToken(), 16).intValue());
	        gregoriancalendar.set(12, Integer.valueOf(stringtokenizer.nextToken(), 16).intValue());
	        gregoriancalendar.set(13, Integer.valueOf(stringtokenizer.nextToken(), 16).intValue());
	        if(stringtokenizer.countTokens() == 4)
	        {
	            stringtokenizer.nextToken();
	            gregoriancalendar.setTimeZone(TimeZone.getTimeZone(getTimeZoneHex(stringtokenizer)));
	        }
	        return gregoriancalendar.getTime();
		} catch (Exception e) {
			throw new DateAndTimeException("Failed to parse hex date value: " + hexDate, e);
		}
	}

    private static String getTimeZoneHex(final StringTokenizer stringtokenizer)
    {
        final StringBuffer stringbuffer = new StringBuffer("GMT");
        final int i = Integer.valueOf(stringtokenizer.nextToken(), 16).intValue();
        if(i == 45) {
			stringbuffer.append('-');
		} else {
			stringbuffer.append('+');
		}
        final int j = Integer.valueOf(stringtokenizer.nextToken(), 16).intValue();
        stringbuffer.append(j);
        final int k = Integer.valueOf(stringtokenizer.nextToken(), 16).intValue();
        stringbuffer.append(':');
        stringbuffer.append(k);
        if(k < 10) {
			stringbuffer.append(0);
		}
        return stringbuffer.toString();
    }
}

