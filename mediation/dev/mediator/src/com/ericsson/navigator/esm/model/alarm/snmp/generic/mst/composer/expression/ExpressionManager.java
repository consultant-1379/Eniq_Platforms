package com.ericsson.navigator.esm.model.alarm.snmp.generic.mst.composer.expression;

import java.util.Hashtable;

import org.apache.log4j.Logger;

import com.ericsson.navigator.esm.model.alarm.snmp.generic.mst.composer.ComposerConstants;
import com.ericsson.navigator.esm.model.alarm.snmp.generic.mst.composer.ComposerConstants.Variables;

/**
 * This will compile the expression given in a filter. 
 * 
 * The expression can be contain special words that will be connected
 * to certain information in the trap. 
 * 
 * Two special types exist. 
 * 
 * The variable expression. This is a expression that will be converted into
 * a object that runtime fetches a value from the trap. It is a generic variable in the trap
 * For instance a variable expression can be the agent_address, enterprise or time_tick of a trap. 
 * These are general values that always exist in a trap.
 * 
 * The index expression is a expression that will take the SNMP index from the
 * pdu pointed out by a specific index and convert this into a string. This is just 
 * a number index into the trap, no name can be given to the index since traps can have
 * different information at the same index. 
 * 
 * 
 * @author qbacfre
 *
 */
public class ExpressionManager {

	private static Logger logger = Logger.getLogger(ExpressionManager.class.getName());
	
	private static Hashtable<String, IExpression> variableExpressions; 
	
	private static ExpressionManager instance; 
	
	/**
	 * Constructor of the expression manager
	 */
	private ExpressionManager() {
		variableExpressions = new Hashtable<String, IExpression>();
		variableExpressions.put(ComposerConstants.VARIABLE_CHARACTER + Variables.AGENTADDRESS, new AgentAddressExpression());
		variableExpressions.put(ComposerConstants.VARIABLE_CHARACTER + Variables.ENTERPRISE, new EnterpriseExpression());
		variableExpressions.put(ComposerConstants.VARIABLE_CHARACTER + Variables.GENERICTYPE, new GenericTrapExpression());
		variableExpressions.put(ComposerConstants.VARIABLE_CHARACTER + Variables.SPECIFICTYPE, new SpecificTypeExpression());
		variableExpressions.put(ComposerConstants.VARIABLE_CHARACTER + Variables.UPTIME, new TimeStampExpression());
		variableExpressions.put(ComposerConstants.VARIABLE_CHARACTER + Variables.OID, new OidExpression());
	}
	
	/**
	 * Gets the only instance of this object.
	 */
	public static ExpressionManager getInstance() {
		if (instance == null) {
			instance = new ExpressionManager();
		}
		
		return instance;
	}
	
	/**
	 * Returns a evaluator for the given keyword.
	 * @param keyword The keyword to get the evaluator for
	 * @return A evaluator or null if none was found for the keyword.
	 */
	public IExpression getExpression(final String keyword) {
		if (keyword == null || keyword.equalsIgnoreCase("")) {
			logger.error("The keyword sent to the ExpressionManager was null or empty");
			return null;
		}
		
		IExpression expression;
		
		if (keyword.startsWith(ComposerConstants.INDEX_CHARACTER)) {
			
			try {
				final int index = Integer.parseInt(keyword.substring(1, keyword.length()));
				
				if (index < 0) {
					logger.error("ExpressionManager unable to retrieve the expression, index is negative");
					return null; // A trap has never negative index.
				}
				
				expression = new IndexExpression(index);
			} catch (final Exception e) {
				logger.error("The index sent did not contain a number after the $ sign. Index was " + keyword);
				return null;
			}
		} else {
			expression = variableExpressions.get(keyword);
		}
		
		if (expression == null) {
			logger.warn("There is no evaluator for the keyword " + keyword + " in the ExpressionManager");
			return null;
		}
		
		return expression;
	}

	/**
	 * This method will check if a expression exist in the expression list.
	 * @param variable The variable containing the constant expression.
	 */
	public boolean constantExist(final String variable) {
		if (variable == null) {
			return false;
		}
		
		if (variable.startsWith(ComposerConstants.INDEX_CHARACTER)) {
			return true;
		}
		
		return variableExpressions.containsKey(variable);
	}
}
