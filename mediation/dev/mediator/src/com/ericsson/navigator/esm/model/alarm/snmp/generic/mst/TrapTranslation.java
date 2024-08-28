package com.ericsson.navigator.esm.model.alarm.snmp.generic.mst;

import java.util.LinkedList;
import java.util.List;

import com.adventnet.snmp.snmp2.SnmpPDU;
import com.ericsson.navigator.esm.model.alarm.Alarm.RecordType;
import com.ericsson.navigator.esm.model.alarm.snmp.generic.mst.composer.condition.ICondition;
import com.ericsson.navigator.esm.model.alarm.snmp.generic.mst.composer.expression.IExpression;

/**
 * A translation will take a trap and translate it to a internal alarm.
 * 
 * @author qbacfre
 * 
 */
public class TrapTranslation { //NOPMD

	// The expressions that will end up as the information in the trap.
	private IExpression idExpression;
	private IExpression additionalTextExpression;
	private IExpression eventTypeExpression;
	private IExpression fdnExpression;
	private IExpression probableCauseExpression;
	private IExpression specificProblemExpression;
	private IExpression perceivedSeverityExpression;
	private RecordType recordType = RecordType.EVENT;
	private boolean suppress;
	
	private String idStringMapId;
	private String additionalTextMapId;
	private String specificProblemMapId;
	private String fdnMapId;
	private String eventTypeMapId;
	private String probableCauseMapId;
	private String perceivedSeverityMapId;
	
	private static final String RAW = "raw";
	private static final String MIB = "mib";	

	private final List<ICondition> conditions;

	/**
	 * Constructor.
	 */
	public TrapTranslation() {
		conditions = new LinkedList<ICondition>();
	}

	/**
	 * Translates the trap into an alarm
	 * 
	 * @param pdu
	 *            The trap to translate
	 * @return MSTAlarm
	 */
	public MSTAlarm translate(final SnmpPDU pdu, final MSTAlarmList system, final List<TrapMap> maps) { //NOPMD

		final StringBuffer buffer = new StringBuffer();
		
		String idString = "";
		String additionalText = "";
		String specificProblem = "";
		String fdn = "";
		String eventTypeString = "";
		String probableCauseString = "";
		String perceivedSeverityString = "";
		
		if (suppress) {
			return new MSTAlarm("0", null, null, null, null, null, "100",
					RecordType.NON_SYNC_ALARM, true);
		}

		if (idExpression != null) {
			evaluate(idExpression, idStringMapId, pdu, system, maps, buffer);
			idString = buffer.toString();
			buffer.delete(0, buffer.length());
		}

		if (additionalTextExpression != null) {
			evaluate(additionalTextExpression, additionalTextMapId, pdu, system, maps, buffer);
			additionalText = buffer.toString();
			buffer.delete(0, buffer.length());
		}

		if (specificProblemExpression != null) {
			evaluate(specificProblemExpression, specificProblemMapId, pdu, system, maps, buffer);
			specificProblem = buffer.toString();
			buffer.delete(0, buffer.length());
		}

		if (fdnExpression != null) {
			evaluate(fdnExpression, fdnMapId, pdu, system, maps, buffer);
			fdn = buffer.toString();
			buffer.delete(0, buffer.length());
		}

		if (eventTypeExpression != null) {
			evaluate(eventTypeExpression, eventTypeMapId, pdu, system, maps, buffer);
			eventTypeString = buffer.toString();
			buffer.delete(0, buffer.length());
		}

		if (probableCauseExpression != null) {
			evaluate(probableCauseExpression, probableCauseMapId, pdu, system, maps, buffer);
			probableCauseString = buffer.toString();
			buffer.delete(0, buffer.length());
		}

		if (perceivedSeverityExpression != null) {
			evaluate(perceivedSeverityExpression, perceivedSeverityMapId, pdu, system, maps, buffer);
			perceivedSeverityString = buffer.toString();
			buffer.delete(0, buffer.length());
		}

		String moi = "";
		if (fdn.equalsIgnoreCase("")) {
			moi = system.getFDN();
		} else {
			moi = system.getFDN() + "," + fdn;
		}

		final MSTAlarm alarm = new MSTAlarm(idString, specificProblem, moi,
				additionalText, eventTypeString, probableCauseString,
				perceivedSeverityString, recordType, false);

		return alarm;
	}

	/**
	 * Evaluates the expression. 
	 * 
	 * Three evaluations are valid:
	 * 
	 *		Explicitly given from XML translation:
	 * 			MIB 	- Lookup pdu value in MIB. Default evaluation
	 * 					  if no map attribute present.
	 * 			RAW 	- Take raw value from pdu.
	 * 			mapX	- If a valid map id is found the RAW evaluation	is mapped
	 * 					  to map value else translate like RAW.
	 * 
	 * @param expression
	 * @param mapId
	 * @param pdu
	 * @param system
	 * @param maps
	 * @param buffer
	 * 			Return value containing result from evaluation.
	 */
	private void evaluate(final IExpression expression, final String mapId,
			final SnmpPDU pdu, final MSTAlarmList system,
			final List<TrapMap> maps, final StringBuffer buffer) {
		if (mapId != null) {
			if (mapId.equalsIgnoreCase(RAW)) {
				expression.evaluate(pdu, buffer);
			} else if (mapId.equalsIgnoreCase(MIB)) {
				expression.evaluate(pdu, system.getMibOperations(), buffer);
			} else { // NOTE: If no map found (i.e. findMap returns null) => translate just RAW
				expression.evaluate(pdu, findMap(mapId, maps), buffer);
			}
		}
		else { // If map attribute is missing in XML then default translate with MIB
			expression.evaluate(pdu, system.getMibOperations(), buffer);
		}
	}

	/**
	 * Finds a map in maps based on mapId.
	 * 
	 * @param mapId
	 * 			String pointing to a map
	 * @param maps
	 * 			A list of maps
	 * @return Returns a map with map id mapId, else if map not found null.
	 */
	private TrapMap findMap(final String mapId, final List<TrapMap> maps) {
		if (maps != null) {
			for (final TrapMap map : maps){
				if (map.getId().equalsIgnoreCase(mapId)) {
					return map;
				}
			}
		}
		return null;
	}

	/**
	 * Set the alarm ID and translation map id.
	 * 
	 * @param expression
	 * @param map
	 */
	public void setAlarmID(final IExpression expression, final String map) {
		idExpression = expression;
		idStringMapId = map;
	}

	/**
	 * Set additional text expression and translation map id.
	 * 
	 * @param expression
	 * @param map
	 */
	public void setAdditionalText(final IExpression expression, final String map) {
		additionalTextExpression = expression;
		additionalTextMapId = map;
	}

	/**
	 * Set the event type expression and translation map id.
	 * 
	 * @param expression
	 * @param map
	 */
	public void setEventType(final IExpression expression, final String map) {
		eventTypeExpression = expression;
		eventTypeMapId = map;
	}

	/**
	 * Set the FDN expression and translation map id.
	 * 
	 * @param expression
	 * @param map
	 */
	public void setManagedObjectInstance(final IExpression expression, final String map) {
		fdnExpression = expression;
		fdnMapId = map;
	}

	/**
	 * Set the probable cause expression and translation map id.
	 * 
	 * @param expression
	 * @param map
	 */
	public void setProbableCause(final IExpression expression, final String map) {
		probableCauseExpression = expression;
		probableCauseMapId = map;
	}

	/**
	 * Set specific problem expression and translation map id.
	 * 
	 * @param specificProblem
	 * @param map
	 */
	public void setSpecificProblem(final IExpression specificProblem, final String map) {
		this.specificProblemExpression = specificProblem;
		specificProblemMapId = map;
	}

	/**
	 * Sets the perceived severity expression and translation map id.
	 * 
	 * @param expression
	 */
	public void setPerceivedSeverity(final IExpression expression, final String map) {
		perceivedSeverityExpression = expression;
		perceivedSeverityMapId = map;
	}

	/**
	 * Set the record type.
	 * 
	 * @param type
	 */
	public void setRecordType(final RecordType type) {
		recordType = type;
	}

	/**
	 * Add a condition that has to be matched.
	 * 
	 * @param condition
	 */
	public void addCondition(final ICondition condition) {
		conditions.add(condition);
	}

	/**
	 * This will check if a PDU matches the conditions set by the translation
	 * 
	 * @param pdu
	 */
	public boolean match(final SnmpPDU pdu) {

		for (final ICondition condition : conditions) {
			if (!condition.matches(pdu)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * This will set if the alarm should be suppressed or not.
	 * 
	 * @param b
	 */
	public void setSuppress(final boolean suppress) {
		this.suppress = suppress;
	}
}
