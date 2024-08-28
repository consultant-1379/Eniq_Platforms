package com.ericsson.navigator.esm.model.alarm.snmp.snf;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import com.adventnet.snmp.snmp2.SnmpPDU;
import com.adventnet.snmp.snmp2.SnmpVarBind;
import com.ericsson.navigator.esm.model.alarm.DefaultAlarm;
import com.ericsson.navigator.esm.model.alarm.Alarm;

import static com.ericsson.navigator.esm.util.Hash.fnv_64_1a;

public class SNFAlarm extends DefaultAlarm { // NOPMD

	private static final long serialVersionUID = 1L;
	private static final String ALARMMODELDESCRIPTIONOID = ".1.3.6.1.4.1.193.110.2.666.1.1.2.1.6";
	private static final String ALARMACTIVERESOURCEIDOID = ".1.3.6.1.4.1.193.110.2.666.1.2.2.1.10";
	private static final String ALARMACTIVEDESCRIPTIONOID = ".1.3.6.1.4.1.193.110.2.666.1.2.2.1.11";
	private static final String ITUALARMEVENTTYPEOID = ".1.3.6.1.4.1.193.110.2.667.1.1.1.1.2";
	private static final String ITUALARMPROBABLECAUSEOID = ".1.3.6.1.4.1.193.110.2.667.1.1.1.1.3";
	public static final String RAISEOID = ".1.3.6.1.4.1.193.110.2.10.2.0.1";
	public static final String CEASEOID = ".1.3.6.1.4.1.193.110.2.10.2.0.2";

	private String m_ResourceID = null; //NOPMD

	/*
	 * Set the event time attribute using the resourceOID that contains:
	 * alarmActiveDescription OID.<MODULE
	 * NAME>.YY.YY.MM.DD.HH.MM.SS.dS.<directon from UTC (+/.)>.<hours from
	 * UTC>.<minutes from UTC>.<INDEX>
	 */
	private void setEventTime(final String alarmActiveResourceOID) {
		final String[] items = alarmActiveResourceOID.split("\\.");
		final int lengthOfActiveResourceId = 16;
		final int sizeOfModuleId = Integer
				.parseInt(items[lengthOfActiveResourceId]);
		int timeIndex = lengthOfActiveResourceId + sizeOfModuleId + 1;
		final GregorianCalendar calendar = new GregorianCalendar();
		final String yearPart1Hex = Integer.toHexString(Integer
				.parseInt(items[++timeIndex]));
		final String yearPart2Hex = Integer.toHexString(Integer
				.parseInt(items[++timeIndex]));
		calendar.set(Calendar.YEAR, Integer.parseInt(yearPart1Hex
				+ yearPart2Hex, 16));
		calendar.set(Calendar.MONTH, Integer.parseInt(items[++timeIndex]) - 1);
		calendar.set(Calendar.DATE, Integer.parseInt(items[++timeIndex]));
		calendar
				.set(Calendar.HOUR_OF_DAY, Integer.parseInt(items[++timeIndex]));
		calendar.set(Calendar.MINUTE, Integer.parseInt(items[++timeIndex]));
		calendar.set(Calendar.SECOND, Integer.parseInt(items[++timeIndex]));
		// Don't care about the milliseconds
		++timeIndex;
		// Handle timezone
		final StringBuffer timezone = new StringBuffer();
		if (items[++timeIndex].equals("45")) {
			timezone.append('-');
		} else {
			timezone.append('+');
		}
		timezone.append(Integer.parseInt(items[++timeIndex]));
		timezone.append(':');
		timezone.append(Integer.parseInt(items[++timeIndex]));
		calendar.getTime();
		calendar.setTimeZone(TimeZone.getTimeZone(timezone.toString()));
		m_EventTime = calendar.getTime();
	}

	@SuppressWarnings("unchecked")
	public SNFAlarm(final SNFAlarmList managedSystem, final SnmpPDU pdu,
			final boolean clear) {
		for (final SnmpVarBind varBind : (List<SnmpVarBind>) pdu
				.getVariableBindings()) {
			if (varBind.getObjectID().toString().startsWith(
					ALARMMODELDESCRIPTIONOID)) {
				m_SpecificProblem = varBind.getVariable().toString();
			} else if (varBind.getObjectID().toString().startsWith(
					ALARMACTIVERESOURCEIDOID)) {
				managedObjectInstance = managedSystem.getFDN(varBind
						.getVariable().toString());
				m_ResourceID = varBind.getVariable().toString();
				setEventTime(varBind.getObjectID().toString());
			} else if (varBind.getObjectID().toString().startsWith(
					ALARMACTIVEDESCRIPTIONOID)) {
				m_AdditionalText = varBind.getVariable().toString();
				m_AdditionalText += ("\nResource ID: " + m_ResourceID);
			} else if (varBind.getObjectID().toString().startsWith(
					ITUALARMEVENTTYPEOID)) {
				m_EventType = getItuEventType(Integer.parseInt(varBind
						.getVariable().toString()));
			} else if (varBind.getObjectID().toString().startsWith(
					ITUALARMPROBABLECAUSEOID)) {
				m_ProbableCause = getItuProbableCause(Integer.parseInt(varBind
						.getVariable().toString()));
				if (clear) {
					m_PerceivedSeverity = Alarm.PerceivedSeverity.CLEARED;
				} else {
					m_PerceivedSeverity = getItuPerceivedSeverity(varBind // NOPMD
							.getObjectID().toString());
				}
			}
		}
		generateUniqueId();
	}

	@Override
	protected final void generateUniqueId() {
		uniqueId = fnv_64_1a(managedObjectInstance + m_ResourceID + UNIQUESEPERATOR + m_SpecificProblem
				+ UNIQUESEPERATOR + m_EventType + UNIQUESEPERATOR + m_ProbableCause);
	}

	PerceivedSeverity getItuPerceivedSeverity(
			final String ituAlarmProbableCauseOID) {
		final String severityString = ituAlarmProbableCauseOID.substring(
				ituAlarmProbableCauseOID.lastIndexOf(".") + 1,
				ituAlarmProbableCauseOID.length());
		final int severity = Integer.parseInt(severityString);
		return getItuPerceivedSeverity(severity);
	}

	static PerceivedSeverity getItuPerceivedSeverity(final int severity) {
		switch (severity) {
		case 1:
			return PerceivedSeverity.CLEARED;
		case 3:
			return PerceivedSeverity.CRITICAL;
		case 4:
			return PerceivedSeverity.MAJOR;
		case 5:
			return PerceivedSeverity.MINOR;
		case 6:
			return PerceivedSeverity.WARNING;
		default:
			return PerceivedSeverity.INDETERMINATE;
		}
	}

	public static String getItuEventType(final int eventType) {// NOPMD
		switch (eventType) {
		case 2:
			return "Communications alarm";
		case 3:
			return "Quality of service alarm";
		case 4:
			return "Processing error alarm";
		case 5:
			return "Equipment alarm";
		case 6:
			return "Environmental alarm";
		case 7:
			return "Integrity violation";
		case 8:
			return "Operational violation";
		case 9:
			return "Physical violation";
		case 10:
			return "Security service or mechanism violation";
		case 11:
			return "Time domain violation";
		default:
			return "Other";
		}
	}

	public static String getItuProbableCause(final int probableCause) {// NOPMD
		switch (probableCause) {
		case 1:
			return "AIS";
		case 2:
			return "Call set up failure";
		case 3:
			return "Degraded signal";
		case 4:
			return "Far end receiver failure";
		case 5:
			return "Framing error";
		case 6:
			return "Loss of frame";
		case 7:
			return "Loss of pointer";
		case 8:
			return "Loss of signal";
		case 9:
			return "Payload type mismatch";
		case 10:
			return "Transmission error";
		case 11:
			return "Remote alarm interface";
		case 12:
			return "Excessive BER";
		case 13:
			return "Path trace mismatch";
		case 14:
			return "Unavailable";
		case 15:
			return "Signal label mismatch";
		case 16:
			return "Loss of multi frame";
		case 17:
			return "Receive failure";
		case 18:
			return "Transmit failure";
		case 19:
			return "Modulation failure";
		case 20:
			return "Demodulation failure";
		case 21:
			return "Broadcast channel failure";
		case 22:
			return "Connection establishment error";
		case 23:
			return "Invalid message received";
		case 24:
			return "Local node transmission error";
		case 25:
			return "Remote node transmission error";
		case 26:
			return "Routing failure";
		case 51:
			return "Backplane failure";
		case 52:
			return "Data set problem";
		case 53:
			return "Equipment identifier duplication";
		case 54:
			return "External IF device problem";
		case 55:
			return "Line card problem";
		case 56:
			return "Multiplexer problem";
		case 57:
			return "NE identifier duplication";
		case 58:
			return "Power problem";
		case 59:
			return "Processor problem";
		case 60:
			return "Protection path failure";
		case 61:
			return "Receiver failure";
		case 62:
			return "Replaceable unit missing";
		case 63:
			return "Replaceable unit type mismatch";
		case 64:
			return "Synchronization source mismatch";
		case 65:
			return "Terminal problem";
		case 66:
			return "Timing problem";
		case 67:
			return "Transmitter failure";
		case 68:
			return "Trunk card problem";
		case 69:
			return "Replaceable unit problem";
		case 70:
			return "Real time clock failure";
		case 71:
			return "Antenna failure";
		case 72:
			return "Battery charging failure";
		case 73:
			return "Disk failure";
		case 74:
			return "Frequency hopping failure";
		case 75:
			return "IO device error";
		case 76:
			return "Loss of synchronisation";
		case 77:
			return "Loss of redundancy";
		case 78:
			return "Power supply failure";
		case 79:
			return "Signal quality evaluation failure";
		case 80:
			return "Tranceiver failure";
		case 81:
			return "Protection mechanism failure";
		case 82:
			return "Protecting resource failure";
		case 101:
			return "Air compressor failure";
		case 102:
			return "Air conditioning failure";
		case 103:
			return "Air dryer failure";
		case 104:
			return "Battery discharging";
		case 105:
			return "Battery failure";
		case 106:
			return "Commercial power failure";
		case 107:
			return "Cooling fan failure";
		case 108:
			return "Engine failure";
		case 109:
			return "Fire detector failure";
		case 110:
			return "Fuse failure";
		case 111:
			return "Generator failure";
		case 112:
			return "Low battery threshold";
		case 113:
			return "Pump failure";
		case 114:
			return "Rectifier failure";
		case 115:
			return "Rectifier high voltage";
		case 116:
			return "Rectifier low F voltage";
		case 117:
			return "Ventilations system failure";
		case 118:
			return "Enclosure door open";
		case 119:
			return "Explosive gas";
		case 120:
			return "Fire";
		case 121:
			return "Flood";
		case 122:
			return "High humidity";
		case 123:
			return "High temperature";
		case 124:
			return "High wind";
		case 125:
			return "Ice build up";
		case 126:
			return "Intrusion detection";
		case 127:
			return "Low fuel";
		case 128:
			return "Low humidity";
		case 129:
			return "Low cable pressure";
		case 130:
			return "Low temperatue";
		case 131:
			return "Low water";
		case 132:
			return "Smoke";
		case 133:
			return "Toxic gas";
		case 134:
			return "Cooling system failure";
		case 135:
			return "External equipment failure";
		case 136:
			return "External point failure";
		case 151:
			return "Storage capacity problem";
		case 152:
			return "Memory mismatch";
		case 153:
			return "Corrupt data";
		case 154:
			return "Out of CPU cycles";
		case 155:
			return "Sfwr environment problem";
		case 156:
			return "Sfwr download failure";
		case 157:
			return "Loss of real timel";
		case 158:
			return "Application subsystem failure";
		case 159:
			return "Configuration or customisation error";
		case 160:
			return "Database inconsistency";
		case 161:
			return "File error";
		case 162:
			return "Out of memory";
		case 163:
			return "Software error";
		case 164:
			return "Timeout expired";
		case 165:
			return "Underlaying resource unavailable";
		case 166:
			return "Version mismatch";
		case 201:
			return "Bandwidth reduced";
		case 202:
			return "Congestion";
		case 203:
			return "Excessive error rate";
		case 204:
			return "Excessive response time";
		case 205:
			return "Excessive retransmission rate";
		case 206:
			return "Reduced logging capability";
		case 207:
			return "System resources overload";
		case 500:
			return "Adapter error";
		case 501:
			return "Application subsystem failture";
		case 502:
			return "Bandwidth reduced X733";
		case 503:
			return "Call establishment error";
		case 504:
			return "Communications protocol error";
		case 505:
			return "Communications subsystem failure";
		case 506:
			return "Configuration or customization error";
		case 507:
			return "Congestion X733";
		case 508:
			return "Corupt data";
		case 509:
			return "Cpu cycles limit exceeded";
		case 510:
			return "Data set or modem error";
		case 511:
			return "Degraded signal X733";
		case 512:
			return "Dte dce interface error";
		case 513:
			return "Enclosure door open X733";
		case 514:
			return "Equipment malfunction";
		case 515:
			return "Excessive vibration";
		case 516:
			return "File error X733";
		case 517:
			return "Fire detected";
		case 518:
			return "Framing error X733";
		case 519:
			return "Heating vent cooling system problem";
		case 520:
			return "Humidity unacceptable";
		case 521:
			return "Input output device error";
		case 522:
			return "Input device error";
		case 523:
			return "Lan error";
		case 524:
			return "Leak detected";
		case 525:
			return "Local node transmission error X733";
		case 526:
			return "Loss of frame X733";
		case 527:
			return "Loss of signal X733";
		case 528:
			return "Material supply exhausted";
		case 529:
			return "Multiplexer problem X733";
		case 530:
			return "Out of memory X733";
		case 531:
			return "Ouput device error";
		case 532:
			return "Performance degraded";
		case 533:
			return "Power problems";
		case 534:
			return "Pressure unacceptable";
		case 535:
			return "Processor problems";
		case 536:
			return "Pump failure X733";
		case 537:
			return "Queue size exceeded";
		case 538:
			return "Receive failure X733";
		case 539:
			return "Receiver failure X733";
		case 540:
			return "Remote node transmission error X733";
		case 541:
			return "Resource at or nearing capacity";
		case 542:
			return "Response time execessive";
		case 543:
			return "Retransmission rate excessive";
		case 544:
			return "Software error X733";
		case 545:
			return "Software program abnormally terminated";
		case 546:
			return "Software program error";
		case 547:
			return "Storage capacity problem X733";
		case 548:
			return "Temperature unacceptable";
		case 549:
			return "Threshold crossed";
		case 550:
			return "Timing problem X733";
		case 551:
			return "Toxic leak detected";
		case 552:
			return "Transmit failure X733";
		case 553:
			return "Transmiter failure";
		case 554:
			return "Underlying resource unavailable";
		case 555:
			return "Version mismatch X733";
		case 600:
			return "Authentication failure";
		case 601:
			return "Breach of confidentiality";
		case 602:
			return "Cable tamper";
		case 603:
			return "Delayed information";
		case 604:
			return "Denial of service";
		case 605:
			return "Duplicate information";
		case 606:
			return "Information missing";
		case 607:
			return "Information modification detected";
		case 608:
			return "Information out of sequence";
		case 609:
			return "Key expired";
		case 610:
			return "Non repudiation failure";
		case 611:
			return "Out of hours activity";
		case 612:
			return "Out of service";
		case 613:
			return "Procedural error";
		case 614:
			return "Unauthorized access attempt";
		case 615:
			return "Unexpected information";
		default:
			return "Other";
		}
	}
}
