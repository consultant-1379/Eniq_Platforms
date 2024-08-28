package com.ericsson.navigator.esm.model.alarm.snmp.generic.mst.composer.condition;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.log4j.Logger;

import com.adventnet.snmp.snmp2.SnmpPDU;
import com.adventnet.snmp.snmp2.SnmpVar;
import com.ericsson.navigator.esm.model.alarm.snmp.generic.mst.composer.ComposerConstants;
import com.ericsson.navigator.esm.model.alarm.snmp.generic.mst.composer.expression.ExpressionManager;

/**
 * The condition class represents a condition. 
 * 
 * A condition is made up of two parts. The thing to be measured and the value 
 * it should have for the condition to be true. 
 * 
 * For instance a condition could be written on a index number or on one of the 
 * trap constants. The constants are defined in ComposerConstatnts.Constants. 
 * 
 * The expression can be a static expression or a regexp. The regexp should be
 * a java regexp like http://java.sun.com/j2se/1.4.2/docs/api/java/util/regex/Pattern.html
 * 
 * @author qbacfre
 *
 */

@SuppressWarnings( "PMD.CyclomaticComplexity" )
public class Condition implements ICondition {

	private static Logger logger = Logger.getLogger(Condition.class.getName());
	
	private final String variable; 
	private final String expression;
	private int index;
	private String constant;
	private Matcher matcher;
	private Pattern pattern;
	private boolean isCompiled;
	private final StringBuffer buffer;
	
	/**
	 * Constructor.
	 * 
	 * @param variable The variable that should be checked. This can be on the form $1 or one of the constants in the ComposerConstants.Variables enum.
	 * @param expression The expression can be a static expression or a regular expression like java wants it. 
	 */
	public Condition(final String variable, final String expression) {
		this.variable = variable;
		this.expression = expression;
		
		buffer = new StringBuffer();
		isCompiled = false;
		index = -1;
	}	
	
	/* (non-Javadoc)
	 * @see com.ericsson.snms.engine.composer.condition.ICondition#compile()
	 */
	public boolean compile() {
		logger.debug("Compiling condition for variable " + variable + " and expression " + expression);
		
		if (variable == null || variable.equalsIgnoreCase("")) {
			logger.error("Unable to compile the condition, the variable is null or empty");
			return false;
		} else if (expression == null || expression.equalsIgnoreCase("")) {
			logger.error("Unable to compile the condition, the condition is null or empty");
			return false;
		}
		
		if (variable.startsWith(ComposerConstants.INDEX_CHARACTER)) {
			try {
				index = Integer.parseInt(variable.substring(1, variable.length()));
				
				if (index < 0) {
					logger.error("Condition unable to compile since the variable index was negative " + index);
					return false;
				}
				
			} catch (final Exception e) {
				logger.error("Unable to compile the condition, not a numeric value after the variable sign. " + variable);
				return false;
			}
		} else if (variable.startsWith(ComposerConstants.VARIABLE_CHARACTER)) {
			if (!ExpressionManager.getInstance().constantExist(variable)) {
				logger.error("Unable to compile the condition, the constant "  + variable + " does not exist");
				return false;
			} 
			
			constant = variable;
		} else {
			logger.error("Unable to compile the condition, variable " + variable + " is not a index or a constant expression");
			return false;
		}
		
		try {
			pattern = Pattern.compile(expression);
		} catch (final PatternSyntaxException e) {
			logger.error("Unable to compile the condition, syntax for the expression is wrong. " + e.getMessage());
			return false;
		}
		
		logger.debug("Compiling condition for variable " + variable + " and expression " + expression + " successfull");
		isCompiled = true;
		return true;
	}
	
	/* (non-Javadoc)
	 * @see com.ericsson.snms.engine.composer.condition.ICondition#matches(com.adventnet.snmp.snmp2.SnmpPDU)
	 */
	public boolean matches(final SnmpPDU pdu) {
		
		if (!isCompiled) {
			logger.error("Unable to match condition, it is not compiled");
			return false;
		}
		
		// Check if it is a variable. 
		if (index >= 0) {
			
			final SnmpVar var = pdu.getVariable(index);
			
			if (var == null) {
				return false;
			} else {
				matcher = pattern.matcher(var.toString());
			}
		} else if (constant != null) {
			// grab the value pointed out by the constant.
			buffer.delete(0, buffer.length());
			ExpressionManager.getInstance().getExpression(constant).evaluate(pdu, buffer);
			
			matcher = pattern.matcher(buffer.toString());
		} else {
			logger.error("Condition ended up in a state it should not be able to get to");
			return false;
		}
				
		return matcher.matches();
	}
}
