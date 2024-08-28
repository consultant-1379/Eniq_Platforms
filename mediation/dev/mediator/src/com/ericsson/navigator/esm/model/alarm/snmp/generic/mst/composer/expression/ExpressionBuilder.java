package com.ericsson.navigator.esm.model.alarm.snmp.generic.mst.composer.expression;

import org.apache.log4j.Logger;

import com.ericsson.navigator.esm.model.alarm.snmp.generic.mst.composer.ComposerConstants;

/**
 * Takes a String and creates a expression from it.
 * 
 * A expression is a chain of Expressions.
 * 
 * @author qbacfre
 * 
 */
@SuppressWarnings( "PMD.CyclomaticComplexity" )
public abstract class ExpressionBuilder {

	private static Logger logger = Logger.getLogger(ExpressionBuilder.class.getName());
	private static boolean compilationSuccessfull;
	
	/**
	 * Takes a string and creates a expression from it.
	 * 
	 * @param expression
	 */
	public static IExpression compileExpression(final String expression) {
		logger.debug("Creating expression from " + expression);
		compilationSuccessfull = true;
				
		final ExpressionManager manager = ExpressionManager.getInstance();

		final CompositeExpression root = new CompositeExpression();
		
		IExpression exp;

		if (expression == null || expression.equalsIgnoreCase("")) {
			return root;
		}
		
		final StringBuffer buffer = new StringBuffer(expression);
		
		boolean go = true;
		while (go) {
			exp = getNextExpression(buffer, manager);

			if (exp != null) {
				root.addExpression(exp);
			} else {
				go = false;
			}
		}

		logger.debug("Creating expression from " + expression + " finished");
		return root;
	}

	/**
	 * This will extract the next expression from the buffer.
	 * @param buffer
	 * @return
	 */
	private static IExpression getNextExpression(StringBuffer buffer, final ExpressionManager manager) {

		if (buffer.length() == 0) {
			return null;
		}

		IExpression exp;
		
		final int startExpression = buffer.indexOf(ComposerConstants.EXPRESSION_START);
		boolean brackets = false;
		
		// check for the first instance of a special character.
		int firstVariableIndex;
		
		final int attIndex = buffer.indexOf(ComposerConstants.VARIABLE_CHARACTER);
		final int dollarIndex = buffer.indexOf(ComposerConstants.INDEX_CHARACTER);
		
		// Check if it is a constant or variable. 
		if (attIndex < 0 || (dollarIndex < attIndex && dollarIndex != -1)) {
			firstVariableIndex = dollarIndex;
		} else {
			firstVariableIndex = attIndex;
		}
		
		// check if it is enclosed in brackets.
		if (startExpression < firstVariableIndex && startExpression != -1) {
			firstVariableIndex = startExpression;
			brackets = true;
		}
		
		// extract the expression
		if (firstVariableIndex < 0) {
			exp = new StringExpression(buffer.toString());
			buffer.delete(0, buffer.length());
		} else if (firstVariableIndex == 0) {
			
			int endIndex;
			if (brackets) {
				endIndex = buffer.indexOf(ComposerConstants.EXPRESSION_END);
			} else {
				endIndex = buffer.indexOf(" ");
			}

			String variable;

			if (endIndex == -1) {
				if (brackets) {
					logger.error("Unclosed bracket found in expression: " + buffer.toString());
					compilationSuccessfull = false;
					return null;
				} else {
					variable = buffer.toString();
					buffer = buffer.delete(0, buffer.length());
				}
			} else {
				if (brackets) {
					variable = buffer.substring(1, endIndex);
					buffer = buffer.delete(0, endIndex+1);
				} else {
					variable = buffer.substring(0, endIndex);
					buffer = buffer.delete(0, endIndex);
				}
			}
			
			if (manager.constantExist(variable)) {
				exp = manager.getExpression(variable);
			} else {
				logger.error("The expression " + variable + " does not exist");
				compilationSuccessfull = false;
				return null;
			}
		} else {			
			exp = new StringExpression(buffer.substring(0, firstVariableIndex));
			buffer = buffer.delete(0, firstVariableIndex);
		}

		return exp;
	}
	
	/**
	 * This will check if the compilation was successful.
	 */
	public static boolean isSuccessfull() {
		return compilationSuccessfull;
	}
}
