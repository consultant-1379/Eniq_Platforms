package com.ericsson.navigator.esm.model.pm.snmp;

import java.math.BigDecimal;

import com.adventnet.snmp.snmp2.SnmpAPI;
import com.adventnet.snmp.snmp2.SnmpPDU;
import com.adventnet.snmp.snmp2.SnmpVar;
import com.adventnet.snmp.snmp2.SnmpVarBind;
import com.ericsson.navigator.esm.MVM;
import com.ericsson.navigator.esm.manager.snmp.SNMPRequest;
import com.ericsson.navigator.esm.model.pm.Counter;
import com.ericsson.navigator.esm.model.pm.CounterDefinition;
import com.ericsson.navigator.esm.model.pm.CounterSet;
import com.ericsson.navigator.esm.model.pm.CounterSetDefinition;
import com.ericsson.navigator.esm.model.pm.DefaultCounter;
import com.ericsson.navigator.esm.model.pm.DeltaPegCounter;
import com.ericsson.navigator.esm.model.pm.ProtocolCounterSet;
import com.ericsson.navigator.esm.model.pm.Counter.CounterType;
import com.ericsson.navigator.esm.util.Hash;

@SuppressWarnings( "PMD.CyclomaticComplexity" )
public class SNMPCounterSet extends ProtocolCounterSet {

	private static final BigDecimal USIGNED32PLUS1 = new BigDecimal("4294967296");
	private static final long serialVersionUID = 1L;
	private static final String SEPERATOR = ">";
	private long systemUpTime;
	private final String classname = SNMPCounterSet.class.getName();
	
	@SuppressWarnings("null")
    public SNMPCounterSet(final String fdn, final int gp, final CounterSetDefinition setDefinition, 
			final String[] oids, final SnmpPDU pdu, final String instance) {
		super(fdn, gp, setDefinition.getCounterSetId());
		managedObjectInstance = fdn;
		moid = setDefinition.getMoidString(fdn) + instance;
		String constructedMoid = setDefinition.getMoidString(fdn);
		int i = 0;
		for(String oid : oids){
			final CounterDefinition definition = setDefinition.getDefinitions().get(oid);
			final SnmpVarBind varBind = pdu.getVariableBinding(i++);
			if(varBind.getObjectID().toString().startsWith(SNMPRequest.SYSTEMUPTIME_EX_INDEX)){
				systemUpTime = (Long) varBind.getVariable().getVarObject();
				if(definition == null && oid.startsWith(SNMPRequest.SYSTEMUPTIME_EX_INDEX)) {
					continue;
				}
			}
			if( definition != null){
				if(definition.getType().equals(CounterType.INDEX)) {
					constructedMoid = constructedMoid + getIndexValue(varBind.getVariable());
					moid = constructedMoid;
					continue;
				} 
				try{
					if(varBind.getVariable().getType() != SnmpAPI.NULLOBJ){
						Counter counter = null;
						if(definition.getType().equals(CounterType.GAUGE)){
							String valueStr = "";

							if(varBind.getVariable().getType() == SnmpAPI.IPADDRESS || varBind.getVariable().getType() == SnmpAPI.STRING){
								valueStr = getIndexValue(varBind.getVariable());
							}else{
								valueStr = getValue(varBind.getVariable()).toString();
							}

							counter = new DefaultCounter(definition.getName(), oid, valueStr, CounterType.GAUGE); //NOPMD 
						}else if( definition.getType().equals(CounterType.PEG)){
							final BigDecimal value = getValue(varBind.getVariable()); //NOPMD
							counter = new DeltaPegCounter(definition.getName(), oid, //NOPMD
									value, getMaxValue(varBind.getVariable())); 
						} else if( definition.getType().equals(CounterType.KPI)){
							final BigDecimal value = getValue(varBind.getVariable()); //NOPMD
							counter = new DefaultCounter(definition.getName(), definition.getId(), value, CounterType.KPI); //NOPMD
						} else if ( definition.getType().equals(CounterType.INDEX)) {
							continue;
						}
						else {
							final BigDecimal value = getValue(varBind.getVariable()); 
							counter = new DefaultCounter(definition.getName(), oid, value, CounterType.GAUGE); //NOPMD
						}
						counters.put(definition.getName(), counter);
					}
				} catch(NumberFormatException e){
					MVM.logger.error(classname + 
							".SNMPCounterSet(); SNMP counter " + definition.getName() + 
							" could not be parsed into a number for counter set definition " + 
							setDefinition.getFileName());
				}
			}
		}
		granularityPeriod = gp;
		uniqueId = Hash.fnv_64_1a(fdn + SEPERATOR + setDefinition.getFileName() + SEPERATOR + moid);

	}
	
	static BigDecimal getValue(final SnmpVar variable) {
		switch(variable.getType()){
			case SnmpAPI.COUNTER64:
				final long[] value = (long[]) variable.getVarObject();
				final BigDecimal highest = USIGNED32PLUS1.multiply(BigDecimal.valueOf(value[1]));
				return BigDecimal.valueOf(value[0]).add(highest);
			default:
				return new BigDecimal(variable.getVarObject().toString());
		}
	}
	static String getIndexValue(final SnmpVar variable) {
		switch(variable.getType()){
			case SnmpAPI.IPADDRESS:
			case SnmpAPI.STRING:
				return variable.getVarObject().toString();
			default:
				return (variable.getVarObject().toString());
		}
	}	

	private BigDecimal getMaxValue(final SnmpVar variable) {
		switch(variable.getType()){
			case SnmpAPI.COUNTER64:
				return new BigDecimal("18446744073709551615");
			case SnmpAPI.TIMETICKS:
			case SnmpAPI.UNSIGNED32:
			case SnmpAPI.COUNTER:
				return new BigDecimal("4294967295");
			case SnmpAPI.INTEGER:
				return new BigDecimal(Integer.MAX_VALUE);
			default:
				return new BigDecimal(Integer.MAX_VALUE);
		}
	}
	
	@Override
	protected boolean missedRopDetected(final CounterSet data) {
		final boolean missed = super.missedRopDetected(data);
		if(missed){
			MVM.logger.warn(classname + 
					".missedRopDetected(); Missed rop data for counter set with moid: " + getMoid());
			state = State.RopDataMissing;
		}
		return missed;
	}

	public SNMPCounterSet(final String fdn, final int gp, final CounterSetDefinition setDefinition, 
			final String[] oids, final SnmpPDU pdu) {
		this(fdn, gp, setDefinition, oids, pdu, "");
	}
	
	@Override
	protected boolean updateData(final CounterSet data) {
		final SNMPCounterSet snmpCounterSetData = (SNMPCounterSet) data;
		state = State.Active;
		if(restartDetected(snmpCounterSetData)){
			state = State.NodeRestarted;
			MVM.logger.debug(classname + 
					".updateData(); Restart has been detected for counter set with moid: " + moid);
			resetCounters();
		}
		systemUpTime = snmpCounterSetData.systemUpTime;
		return super.updateData(data);
	}

	/**
	 * True if system up time has been reset since last poll.
	 */
	private boolean restartDetected(final SNMPCounterSet data) {
		return systemUpTime > data.systemUpTime;
	}
}
