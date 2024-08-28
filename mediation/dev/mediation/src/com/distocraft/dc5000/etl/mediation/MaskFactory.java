package com.distocraft.dc5000.etl.mediation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Logger;

/**
 * TODO intro <br>
 * TODO usage <br>
 * TODO used databases/tables <br>
 * TODO used properties <br>
 * <br>
 * Copyright Distocraft 2005 <br>
 * <br>
 * $id$
 * 
 * @author lemminkainen
 */
public class MaskFactory {

  private static Logger log = Logger.getLogger("MaskFactory");

  /**
   * Previous mask is somehow failed. Getting and parsing next one.
   * 
   * @param ftt
   * @return
   */
  public static FileTransferTask[] getNext(FileTransferTask ftt) throws Exception {

    if (ftt.getMask().indexOf("$") >= 0) { // Failed mask was timestamp mask

      if (ftt.calendarField == Calendar.DAY_OF_MONTH) // DAY failed -> HOUR
        ftt.calendarField = Calendar.HOUR;
      else if (ftt.calendarField == Calendar.HOUR) { // HOUR failed -> next mask

        ftt.maskIndex++;

        if (ftt.getMask().indexOf("$") >= 0)
          ftt.calendarField = Calendar.DAY_OF_MONTH;
        else
          ftt.calendarField = -1;

      }

    } else { // Failed mask was regular mask

      ftt.maskIndex++;

      if (ftt.getMask().indexOf("$") >= 0)
        ftt.calendarField = Calendar.DAY_OF_MONTH;
      else
        ftt.calendarField = -1;

    }

    return parseMask(ftt);

  }

  public static FileTransferTask[] parseMask(FileTransferTask ftt) throws Exception {

    String mask = ftt.getMask();

    int ix = mask.indexOf("$");

    if (ix >= 0) {

      log.finest("Timestamp-mask");

      String tspattern = ftt.getProperty("timestamp");

      log.finest("Timestamp pattern: " + tspattern);

      SimpleDateFormat sdf = new SimpleDateFormat(tspattern);

      int steps = 0;
      if (ftt.calendarField == Calendar.DAY_OF_MONTH)
        steps = Integer.parseInt(ftt.getProperty("lookBackDays"));
      else if (ftt.calendarField == Calendar.HOUR)
        steps = 24;

      Calendar cal = new GregorianCalendar();

      if (ftt.timestamp != null)
        cal.setTime(ftt.timestamp);

      List ftl = new ArrayList(10);

      for (int step = 0; step < steps; step++) {

        FileTransferTask ftn = new FileTransferTask(ftt);

        if (step > 0)
          cal.add(ftt.calendarField, -1);

        ftn.timestamp = cal.getTime();

        String stamp = sdf.format(cal.getTime());

        String nmask = "";
        
        if (ix > 0)
          nmask += mask.substring(0, ix);

        nmask += stamp;

        if (ix < mask.length())
          nmask += mask.substring(ix + 1);

        log.finest("Created mask: " + nmask);

        ftn.setMask(nmask);
        
        ftl.add(ftn);

      }

      log.finest("Total " + ftl.size() + " set-back-masks created.");

      return (FileTransferTask[]) ftl.toArray(new FileTransferTask[ftl.size()]);

    } else { // no timestamp in mask

      log.finest("Plain-mask");

      FileTransferTask[] fta = new FileTransferTask[1];
      fta[0] = ftt;

      return fta;
    }

  }

}
