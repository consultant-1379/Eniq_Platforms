package com.ericsson.navigator.esm.util.csv;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;

import com.ericsson.navigator.esm.model.ManagedData;
import com.ericsson.navigator.esm.model.ManagedDataEvent;
import com.ericsson.navigator.esm.model.ManagedDataType;
import com.ericsson.navigator.esm.model.alarm.Alarm;
import com.ericsson.navigator.esm.model.pm.Counter;
import com.ericsson.navigator.esm.model.pm.CounterSet;

public abstract class CSVPrinter {

	private static final SimpleDateFormat TIMEFORMATTER = new SimpleDateFormat(
			"yyyyMMddHHmmss");
	
	public static void print(final ManagedData<?,?> data, final StringBuffer text){
		printManagedData(data, text);
		if(data.getDataType().equals(ManagedDataType.Alarm)){
			//final Alarm alarm = (Alarm)data;
			//printAlarm(alarm, text);
		} else if (data.getDataType().equals(ManagedDataType.CounterSet)){
			//final CounterSet counterSet = (CounterSet)data;
			//printCounterSet(counterSet, text);
		}
		printManagedState(data, text);
	}

	public static void print(final ManagedDataEvent<?> event, final StringBuffer text) {
		text.append("Action=");
		text.append(event.getAction().toString());
		text.append(",");
		print(event.getData(), text);
	}

	private static void printManagedData(final ManagedData<?,?> data,
			final StringBuffer text) {
		text.append("Type=");
		text.append(data.getDataType());
		text.append(",");
		text.append("UniqueId=");
		text.append(data.getUniqueId());
		text.append(",");
		text.append("ManagedObjectInstance=\"");
		text.append(data.getManagedObjectInstance());
		text.append("\",");
	}

	private static void printManagedState(final ManagedData<?,?> data,
			final StringBuffer alarmTexts) {
		alarmTexts.append(",StateUserId=");
		alarmTexts.append(nonNull(data.getStateUser()));
		alarmTexts.append(",State=");
		alarmTexts.append(data.getState().toString());
		alarmTexts.append(",StateTime=");
		alarmTexts.append(data.getStateTime() == null ? ""
				: TIMEFORMATTER.format(data.getStateTime()));
		alarmTexts.append(',');
	}

	private static void printCounterSet(final CounterSet counterSet,
			final StringBuffer text) {
		text.append("CounterSetId=\"");
		text.append(counterSet.getCounterSetId());
		text.append("\",Moid=\"");
		text.append(counterSet.getMoid());
		text.append("\",EndTime=\"");
		text.append(counterSet.getEndTime() == null ? ""
				: TIMEFORMATTER.format(counterSet.getEndTime()));
		text.append("\",GranularityPeriod=\"");
		text.append(counterSet.getGranularityPeriod());
		text.append("\"");
		final ArrayList<String> keyList = new ArrayList<String>(counterSet.getCounters().keySet());
		Collections.sort(keyList);
		for(String counterName : keyList){
			final Counter counter = counterSet.getCounters().get(counterName);
			text.append(",");
			text.append(counter.getName());
			text.append("(");
			text.append(counter.getType());
			text.append(")");
			text.append("=\"");
			if(counter.isValid()){
				text.append(counter.getValue());
			} else {
				text.append("NaN");
			}
			text.append("\"");
		}
	}

	private static String nonNull(final String s) {
		if (s == null) {
			return "";
		}
		return s;
	}

	private static String removeNewLine(final String additionalText) {
		return additionalText.replace('\n', ' ');
	}
	
	private static void printAlarm(final Alarm alarm, final StringBuffer text) {
		text.append("AlarmId=\"");
		text.append(alarm.getAlarmId());
		text.append("\",");
		text.append("CorrCount=\"");
		text.append(alarm.getCorrelationCount());
		text.append("\",");
		text.append("AlarmType=\"");
		text.append(alarm.getRecordType());
		text.append("\",");
		text.append("EventTime=\"");
		text.append(TIMEFORMATTER.format(alarm.getEventTime()));
		text.append("\",");
		text.append("SpecificProblem=\"");
		text.append(alarm.getSpecificProblem());
		text.append("\",");
		text.append("ProbableCause=\"");
		text.append(alarm.getProbableCause());
		text.append("\",");
		text.append("EventType=\"");
		text.append(alarm.getEventType());
		text.append("\",");
		text.append("PerceivedSeverity=\"");
		text.append(alarm.getPerceivedSeverity());
		text.append("\",");
		text.append("AdditionalText=\"");
		if (alarm.getAdditionalText() != null) {
			text.append(removeNewLine(alarm.getAdditionalText()));
		}
		text.append("\"");
	}
}
