package com.ericsson.navigator.esm.model.alarm.snmp.irp;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.adventnet.snmp.snmp2.SnmpPDU;
import com.adventnet.snmp.snmp2.SnmpVarBind;
import com.ericsson.navigator.esm.manager.snmp.DateAndTime;
import com.ericsson.navigator.esm.model.alarm.DefaultAlarm;

public class IRPAlarm extends DefaultAlarm {// NOPMD

	private static final String classname = IRPAlarm.class.getName();
	private static final Logger logger = Logger.getLogger(classname);
	
	private static final long serialVersionUID = 1L;
	private static final String PROBABLECAUSEOID = ".1.3.6.1.4.1.3881.2.1.8.1.6";
	private static final String EVENTTYPEOID = ".1.3.6.1.4.1.3881.2.1.8.1.5";
	private static final String ALARMIDOID = ".1.3.6.1.4.1.3881.2.1.8.1.1";
	private static final String ADDITIONALTEXTOID = ".1.3.6.1.4.1.3881.2.1.8.1.13";
	private static final String SPECIFICPROBLEMOID = ".1.3.6.1.4.1.3881.2.1.8.1.8";
	private static final String EVENTTIMEOID = ".1.3.6.1.4.1.3881.2.1.8.1.4";
	private static final String MANAGEDOBJECTINSTANCEOID = ".1.3.6.1.4.1.3881.2.1.8.1.3";
	private static final String PERCEIVEDSEVERITYOID = ".1.3.6.1.4.1.3881.2.1.8.1.7";
	public static final String IRPALARMOID = ".1.3.6.1.4.1.3881.2.2.0";
	public static final String IRPALARMMOC = ".1.3.6.1.4.1.3881.2.1.8.1.2";

	
	@SuppressWarnings("unchecked")
	public IRPAlarm(final IRPAlarmList managedSystem, final SnmpPDU trapPDU) {
		for (final SnmpVarBind varBind : (List<SnmpVarBind>) trapPDU
				.getVariableBindings()) {
			setAttributes(varBind, managedSystem);
		}
		generateUniqueId();
	}
	
	private void setAttributes(final SnmpVarBind varBind, //NOPMD
			final IRPAlarmList managedSystem) {
		if (startsWith(varBind.getObjectID().toString(), SPECIFICPROBLEMOID)) {
			m_SpecificProblem = varBind.getVariable().toString();
		} else if (startsWith(varBind.getObjectID().toString(),
				MANAGEDOBJECTINSTANCEOID)) {
			managedObjectInstance = managedSystem.getFDN(varBind
					.getVariable().toString());
		} else if (startsWith(varBind.getObjectID().toString(), EVENTTIMEOID)) {
			m_EventTime = getTime(varBind, managedSystem);
		} else if (startsWith(varBind.getObjectID().toString(), EVENTTYPEOID)) {
			m_EventType = getIRPEventType(varBind.getVariable().toString());
		} else if (startsWith(varBind.getObjectID().toString(),
				PROBABLECAUSEOID)) {
			m_ProbableCause = getIRPProbableCause(varBind.getVariable()
					.toString());
		} else if (startsWith(varBind.getObjectID().toString(),
				PERCEIVEDSEVERITYOID)) {
			m_PerceivedSeverity = getPerceivedSeverity(varBind.getVariable()
					.toString());
		} else if (startsWith(varBind.getObjectID().toString(), ALARMIDOID)) {
			m_AlarmId = varBind.getVariable().toString();
		} else if (startsWith(varBind.getObjectID().toString(),
				ADDITIONALTEXTOID)) {
			if(!m_AdditionalText.trim().equals("")){
			m_AdditionalText += "\n";
			}
			m_AdditionalText += varBind.getVariable().toString();
		}else if (startsWith(varBind.getObjectID().toString(),
				IRPALARMMOC)) {
			if(!m_AdditionalText.trim().equals("")){
			m_AdditionalText += "\n";
			}
			m_AdditionalText += "MOC="+varBind.getVariable().toString();
		}
	}
	
	private Date getTime(final SnmpVarBind varBind, final IRPAlarmList managedSystem){
		try{
			return DateAndTime.getTime(varBind);
		} catch (Exception e){
			logger.warn(classname + ".getTime(); Failed to parse alarm IRP time for FDN: " + 
					managedSystem.getFDN());
			return new Date();
		}
	}

	/**
	 * Since alarmID OID contains the additionalTextOID we need to make sure we
	 * don't mix these up.
	 */
	private static boolean startsWith(final String receivedTrapOID,
			final String oid) {
		return receivedTrapOID.startsWith(oid + ".")
				|| receivedTrapOID.equals(oid);
	}

	public static String getIRPProbableCause(final String probableCause) {// NOPMD
		switch (Integer.parseInt(probableCause)) {
		case 1:
			return "M3100 alarm indication signal";
		case 2:
			return "M3100 call setup failure";
		case 3:
			return "M3100 degraded signal";
		case 4:
			return "M3100 far end receiver failure";
		case 5:
			return "M3100 framing error";
		case 6:
			return "M3100 loss of frame";
		case 7:
			return "M3100 loss of pointer";
		case 8:
			return "M3100 loss of signal";
		case 9:
			return "M3100 payload type mismatch";
		case 10:
			return "M3100 transmission error";
		case 11:
			return "M3100 remote alarm interface";
		case 12:
			return "M3100 excessive bit error rate";
		case 13:
			return "M3100 path trace mismatch";
		case 14:
			return "M3100 unavailable";
		case 15:
			return "M3100 signal label mismatch";
		case 16:
			return "M3100 loss of multi frame";
		case 51:
			return "M3100 back plane failure";
		case 52:
			return "M3100 data set problem";
		case 53:
			return "M3100 equipment identifier duplication";
		case 54:
			return "M3100 external if device problem";
		case 55:
			return "M3100 line card problem";
		case 56:
			return "M3100 multiplexer problem";
		case 57:
			return "M3100 ne identifier duplication";
		case 58:
			return "M3100 power problem";
		case 59:
			return "M3100 processor problem";
		case 60:
			return "M3100 protection path failure";
		case 61:
			return "M3100 receiver failure";
		case 62:
			return "M3100 replaceable unit missing";
		case 63:
			return "M3100 replaceable unit type mismatch";
		case 64:
			return "M3100 synchronisation source mismatch";
		case 65:
			return "M3100 terminal problem";
		case 66:
			return "M3100 timing problem";
		case 67:
			return "M3100 transmitter failure";
		case 68:
			return "M3100 trunk card problem";
		case 69:
			return "M3100 replaceable unit problem";
		case 101:
			return "M3100 air compressor failure";
		case 102:
			return "M3100 air conditioning failure";
		case 103:
			return "M3100 air dryer failure";
		case 104:
			return "M3100 battery discharging";
		case 105:
			return "M3100 battery failure";
		case 106:
			return "M3100 commercial power failure";
		case 107:
			return "M3100 cooling fan failure";
		case 108:
			return "M3100 engine failure";
		case 109:
			return "M3100 fire detector failure";
		case 110:
			return "M3100 fuse failure";
		case 111:
			return "M3100 generator failure";
		case 112:
			return "M3100 low battery threshold";
		case 113:
			return "M3100 pump failure";
		case 114:
			return "M3100 rectifier failure";
		case 115:
			return "M3100 rectifier high voltage";
		case 116:
			return "M3100 rectifier low voltage";
		case 117:
			return "M3100 ventilation system failure";
		case 118:
			return "M3100 enclosure door open";
		case 119:
			return "M3100 explosive gas";
		case 120:
			return "M3100 fire";
		case 121:
			return "M3100 flood";
		case 122:
			return "M3100 high humidity";
		case 123:
			return "M3100 high temperature";
		case 124:
			return "M3100 high wind";
		case 125:
			return "M3100 ice build up";
		case 127:
			return "M3100 low fuel";
		case 128:
			return "M3100 low humidity";
		case 129:
			return "M3100 low cable pressure";
		case 130:
			return "M3100 low temperature";
		case 131:
			return "M3100 low water";
		case 132:
			return "M3100 smoke";
		case 133:
			return "M3100 toxic gas";
		case 151:
			return "M3100 storage capacity problem";
		case 152:
			return "M3100 memory mismatch";
		case 153:
			return "M3100 corrupt data";
		case 154:
			return "M3100 out of CPU cycles";
		case 155:
			return "M3100 software environment problem";
		case 156:
			return "M3100 software download failure";
		case 301:
			return "X733 adapter error";
		case 302:
			return "X733 application subsystem failure";
		case 303:
			return "X733 bandwidth reduced";
		case 305:
			return "X733 communications protocol error";
		case 306:
			return "X733 communications subsystem failure";
		case 307:
			return "X733 configuration or customization error";
		case 308:
			return "X733 congestion";
		case 310:
			return "X733 cpu cycles limit exceeded";
		case 311:
			return "X733 data set or modem error";
		case 313:
			return "X733 DTEDCE interface error";
		case 315:
			return "X733 equipment malfunction";
		case 316:
			return "X733 excessive vibration";
		case 317:
			return "X733 file error";
		case 321:
			return "X733 heating or ventilation or cooling system problem";
		case 322:
			return "X733 humidity unacceptable";
		case 323:
			return "X733 input output device error";
		case 324:
			return "X733 input device error";
		case 325:
			return "X733 LAN error";
		case 326:
			return "X733 leak detected";
		case 327:
			return "X733 local node transmission error";
		case 330:
			return "X733 material supply exhausted";
		case 332:
			return "X733 out of memory";
		case 333:
			return "X733 ouput device error";
		case 334:
			return "X733 performance degraded";
		case 336:
			return "X733 pressure unacceptable";
		case 339:
			return "X733 queue size exceeded";
		case 340:
			return "X733 receive failure";
		case 342:
			return "X733 remote node transmission error";
		case 343:
			return "X733 resource at or nearing capacity";
		case 344:
			return "X733 response time excessive";
		case 345:
			return "X733 retransmission rate excessive";
		case 346:
			return "X733 software error";
		case 347:
			return "X733 software program abnormally terminated";
		case 348:
			return "X733 software program error";
		case 350:
			return "X733 temperature unacceptable";
		case 351:
			return "X733 threshold crossed";
		case 353:
			return "X733 toxic leak detected";
		case 354:
			return "X733 transmit failure";
		case 356:
			return "X733 underlying resource unavailable";
		case 357:
			return "X733 version mismatch";
		case 401:
			return "X736 authentication failure";
		case 402:
			return "X736 breach of confidentiality";
		case 403:
			return "X736 cable tamper";
		case 404:
			return "X736 delayed information";
		case 405:
			return "X736 denial of service";
		case 406:
			return "X736 duplicate information";
		case 407:
			return "X736 information missing";
		case 408:
			return "X736 information modification detected";
		case 409:
			return "X736 information out of sequence";
		case 410:
			return "X736 intrusion detection";
		case 411:
			return "X736 key expired";
		case 412:
			return "X736 non repudiation failure";
		case 413:
			return "X736 out of hours activity";
		case 414:
			return "X736 out of service";
		case 415:
			return "X736 procedural error";
		case 416:
			return "X736 unauthorized access attempt";
		case 417:
			return "X736 unexpected information";
		case 418:
			return "X736 unspecified reason";
		case 501:
			return "Gsm1211 abis to BTS interface failure";
		case 502:
			return "Gsm1211 abis to TRX interface failure";
		case 503:
			return "Gsm1211 antenna problem";
		case 504:
			return "Gsm1211 battery breakdown";
		case 505:
			return "Gsm1211 battery charging fault";
		case 5056:
			return "Gsm1211 clock synchronisation problem";
		case 507:
			return "Gsm1211 combiner problem";
		case 508:
			return "Gsm1211 disk problem";
		case 510:
			return "Gsm1211 excessive receiver temperature";
		case 511:
			return "Gsm1211 excessive transmitter output power";
		case 512:
			return "Gsm1211 excessive transmitter temperature";
		case 513:
			return "Gsm1211 frequency hopping degraded";
		case 514:
			return "Gsm1211 frequency hopping failure";
		case 515:
			return "Gsm1211 frequency redefinition failed";
		case 516:
			return "Gsm1211 line interface failure";
		case 517:
			return "Gsm1211 link failure";
		case 518:
			return "Gsm1211 loss of synchronisation";
		case 519:
			return "Gsm1211 lost redundancy";
		case 520:
			return "Gsm1211 mains breakdown with battery back up";
		case 521:
			return "Gsm1211 mains breakdown without battery back up";
		case 522:
			return "Gsm1211 power supply failure";
		case 523:
			return "Gsm1211 receiver antenna fault";
		case 525:
			return "Gsm1211 receiver multicoupler failure";
		case 526:
			return "Gsm1211 reduced transmitter output power";
		case 527:
			return "Gsm1211 signal quality evaluation fault";
		case 528:
			return "Gsm1211 timeslot hardware failure";
		case 529:
			return "Gsm1211 transceiver problem";
		case 530:
			return "Gsm1211 transcoder problem";
		case 531:
			return "Gsm1211 transcoder or rate adapter problem";
		case 532:
			return "Gsm1211 transmitter antenna failure";
		case 533:
			return "Gsm1211 transmitter antenna not adjusted";
		case 535:
			return "Gsm1211 transmitter low voltage or current";
		case 536:
			return "Gsm1211 transmitter off frequency";
		case 537:
			return "Gsm1211 database inconsistency";
		case 538:
			return "Gsm1211 file system call unsuccessful";
		case 539:
			return "Gsm1211 input parameter out of range";
		case 540:
			return "Gsm1211 invalid parameter";
		case 541:
			return "Gsm1211 invalid pointer";
		case 542:
			return "Gsm1211 message not expected";
		case 543:
			return "Gsm1211 message not initialised";
		case 544:
			return "Gsm1211 message out of sequence";
		case 545:
			return "Gsm1211 system call unsuccessful";
		case 546:
			return "Gsm1211 timeout expired";
		case 547:
			return "Gsm1211 variable out of range";
		case 548:
			return "Gsm1211 watch dog timer expired";
		case 549:
			return "Gsm1211 cooling system failure";
		case 550:
			return "Gsm1211 external equipment failure";
		case 551:
			return "Gsm1211 external power supply failure";
		case 552:
			return "Gsm1211 external transmission device failure";
		case 561:
			return "Gsm1211 reduced alarm reporting";
		case 562:
			return "Gsm1211 reduced event reporting";
		case 563:
			return "Gsm1211 reduced logging capability";
		case 564:
			return "Gsm1211 system resources overload";
		case 565:
			return "Gsm1211 broadcast channel failure";
		case 566:
			return "Gsm1211 connection establishment error";
		case 567:
			return "Gsm1211 invalid message received";
		case 568:
			return "Gsm1211 invalid MSU received";
		case 569:
			return "Gsm1211 LAPD link protocol failure";
		case 570:
			return "Gsm1211 local alarm indication";
		case 571:
			return "Gsm1211 remote alarm indication";
		case 572:
			return "Gsm1211 routing failure";
		case 573:
			return "Gsm1211 SS7 protocol failure";
		case 574:
			return "Gsm1211 transmission error";
		default:
			return "M3100 indeterminate";
		}
	}

	public static String getIRPEventType(final String eventType) {// NOPMD
		switch (Integer.parseInt(eventType)) {
		case 2:
			return "Communications alarm";
		case 3:
			return "Environmental alarm";
		case 4:
			return "Equipment alarm";
		case 10:
			return "Processing error alarm";
		case 11:
			return "Quality of service alarm";
		case 15:
			return "Integrity violation";
		case 16:
			return "Operational violation";
		case 17:
			return "Physical violation";
		case 18:
			return "Security service violation";
		case 19:
			return "Time domain violation";
		default:
			return "Unknown";

		}
	}

	static PerceivedSeverity getPerceivedSeverity(final String s) {
		int severity = -1;
		try {
			severity = Integer.parseInt(s);
		} catch (final NumberFormatException e) {
			return PerceivedSeverity.INDETERMINATE;
		}
		switch (severity) {
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
}
