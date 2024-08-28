package com.ericsson.navigator.esm.model.pm.file.remote.comparator;

import java.util.Calendar;
import java.util.Comparator;

public class TopCsComparator implements Comparator {

	@Override
	public int compare(Object orig, Object test) {
			String[] origParts = ((String) orig).split("_");
			String[] testParts = ((String) test).split("_");

			Calendar origcal = Calendar.getInstance();
			origcal.set(Integer.valueOf(origParts[1]),
					Integer.valueOf(origParts[2]) - 1,
					Integer.valueOf(origParts[3]));
			// System.out.println(origcal.getTime());

			Calendar testcal = Calendar.getInstance();
			testcal.set(Integer.valueOf(testParts[1]),
					Integer.valueOf(testParts[2]) - 1,
					Integer.valueOf(testParts[3]));
			// System.out.println(testcal.getTime());

			if (origcal.after(testcal)) {
				return -1;
			} else if (origcal.before(testcal)) {
				return 1;
			}
		return 0;
	}

}