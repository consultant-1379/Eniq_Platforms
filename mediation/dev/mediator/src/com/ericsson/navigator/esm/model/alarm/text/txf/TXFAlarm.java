package com.ericsson.navigator.esm.model.alarm.text.txf;

import java.text.ParseException;
import java.util.Date;
import java.util.StringTokenizer;

import com.ericsson.navigator.esm.agent.bnsi.BNSIAgent;
import com.ericsson.navigator.esm.model.alarm.DefaultAlarm;

public class TXFAlarm extends DefaultAlarm { // NOPMD

	private static final long serialVersionUID = 1L;
	
	
	private String proposedRepairAction = "";

	public TXFAlarm(final String attributeLines) {
		m_RecordType = RecordType.EVENT;
		final StringTokenizer lines = new StringTokenizer(attributeLines, "\n");
		while (lines.hasMoreTokens()) {
			final String line = lines.nextToken();
			parseLine(line);
		}
		generateUniqueId();
	}

	private void parseLine(final String line) { // NOPMD
		if (line.startsWith("-ObjectOfReference=")) {
			managedObjectInstance = getAttributeValue(line);
		} else if (line.startsWith("-SpecificProblem=") || 
				line.startsWith("-SpecificProblemText=") || 
				line.startsWith("-SPtext=")) {
			m_SpecificProblem = getAttributeValue(line);
		} else if (line.startsWith("-AlarmNumber=")) {
			m_AlarmId = getAttributeValue(line);
		} else if (line.startsWith("-ProbableCause=") || 
				line.startsWith("-ProbableCauseText=") || 
				line.startsWith("-PCtext=")) {
			m_ProbableCause = getAttributeValue(line);
		} else if (line.startsWith("-EventType=")  || 
				line.startsWith("-EventTypeText=") || 
				line.startsWith("-ETtext=")) {
			m_EventType = getAttributeValue(line);
		} else if (line.startsWith("-PerceivedSeverity=")) {
			m_PerceivedSeverity = getSeverity(getAttributeValue(line));
		} else if (line.startsWith("-EventTime=")) {
			m_EventTime = getDate(getAttributeValue(line));
		} else if (line.startsWith("-RecordType=")) {
			m_RecordType = getRecordType(getAttributeValue(line));
		} else if (line.startsWith("-CorrelationCount=")) {
			m_CorrelationCount = getInteger(getAttributeValue(line));
		} else if (line.startsWith("-ProposedRepairAction=")) {
			setProposedRepairAction(getAttributeValue(line));
		} else if (!line.startsWith("-") || 
				line.startsWith("-ProblemText=") || 
				line.startsWith("-ProblemData=")) {
			m_AdditionalText += getAttributeValue(line) + "\n";
		} else if (line.startsWith("-")) {
			m_AdditionalText += line + "\n";
		}
	}

	/**
	 * -RecordType=[1|2|3|4|5|6|7|8|9|10|11|14|15|16|17|18|19|20] 
	 * 1 = Alarm
	 * 2 = Error message
	 * 3 = Non_synchable Alarm
	 * 4 = Repeated Alarm
	 * 5 = Synchronisation Alarm
	 * 6 = Heartbeat Alarm
	 * 7 = Synchronisation Started
	 * 8 = Synchronisation Ended
	 * 9 = Synchronisation Aborted
	 * 10 = Synchronisation Ignored
	 * 14 = Clear List
	 * 18=Repeated Error Message
	 * 19=Repeated Non_Synchable Alarm
	 * 20=Update.
	 * 
	 * @param attributeValue
	 * @return
	 */
	private RecordType getRecordType(final String attributeValue) { //NOPMD
		switch (getInteger(attributeValue)) {
			case 1:
			case 4:
				return RecordType.NON_SYNC_ALARM;
			case 2:
			case 18:
				return RecordType.EVENT;
			case 3:
			case 19:
				return RecordType.NON_SYNC_ALARM;
			case 5:
				return RecordType.SYNC_ALARM;
			case 6:
				return RecordType.NON_SYNC_ALARM;
			case 7:
				return RecordType.SYNC_START;
			case 8:
				return RecordType.SYNC_END;
			case 9:
			case 10:
				return RecordType.SYNC_ABORTED;
			case 14:
				return RecordType.CLEAR_ALL;
		}
		return RecordType.EVENT;
	}

	private Date getDate(final String attributeValue) {
		try {
			synchronized(BNSIAgent.TIMEFORMATTER) {
				return BNSIAgent.TIMEFORMATTER.parse(attributeValue);
			}
		} catch (ParseException e) {
			return new Date();
		}
	}

	private PerceivedSeverity getSeverity(final String attributeValue) {
		switch (getInteger(attributeValue)) {
		case 1:
			return PerceivedSeverity.CRITICAL;
		case 2:
			return PerceivedSeverity.MAJOR;
		case 3:
			return PerceivedSeverity.MINOR;
		case 4:
			return PerceivedSeverity.WARNING;
		case 5:
			return PerceivedSeverity.CLEARED;
		default:
			return PerceivedSeverity.INDETERMINATE;
		}
	}

	private int getInteger(final String attributeValue) {
		try {
			return Integer.parseInt(attributeValue);
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	private String getAttributeValue(final String line) {
		return line.substring(line.indexOf('=') + 1).trim();
	}
	
	public String getProposedRepairAction(){
		return proposedRepairAction;
	}

	/**
     * @param i the proposedRepairAction to set
     */
    private void setProposedRepairAction(final String proposedRepairAction) {
	    this.proposedRepairAction = proposedRepairAction;
    }
}
