package com.ericsson.navigator.esm.model.pm.file.remote.comparator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

public class PpmXmlComparator implements Comparator{

		@Override
		public int compare(Object orig, Object test) {
			try{
				DateFormat format = new SimpleDateFormat("yyyy_MM_dd");
				String[] origParts = ((String)orig).split("-");
				String[] testParts = ((String)test).split("-");
				
				Date date = (Date) format.parse(origParts[3]);
				Calendar origcal = Calendar.getInstance();
				origcal.setTime(date);
				
				date = (Date) format.parse(testParts[3]);
				Calendar testcal = Calendar.getInstance();
				testcal.setTime(date);
				
				if(origcal.after(testcal)){
					return -1;
				}else if(origcal.before(testcal)){
					return 1;
				}

			}catch(Exception e){
				throw new NullPointerException(e.getMessage());
			}
			return 0;
		}
		
	}