package com.ericsson.eniq.techpacksdk.view.prompts;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import org.jdesktop.application.Application;

import ssc.rockfactory.RockFactory;
import tableTree.TTParameterModel;

import com.distocraft.dc5000.repository.dwhrep.Promptimplementor;
import com.ericsson.eniq.techpacksdk.common.Constants;
import com.ericsson.eniq.techpacksdk.common.Utils;

/**
 * Concrete class for parameter data for prompt implementors.
 * 
 * @author eheijun
 * 
 */
public class PromptImplementorParameterModel extends TTParameterModel {

	private static final Logger logger = Logger
			.getLogger(PromptImplementorParameterModel.class.getName());

	protected final static int defaultWidth = 380;

	/**
	 * The RockDBObject representing the PromptImplementor
	 */
	private final Promptimplementor promptimplementor;

	/**
	 * Identifiers for the components in the panel displaying the parameters.
	 */
	protected static final String PRIORITY = "Priority";

	private final Application application;

	/**
	 * Constructor. Stores the given RockDBObjects.
	 * 
	 * @param inType
	 * @param inTable
	 */
	public PromptImplementorParameterModel(Application application,
			PromptImplementorData implementordata, RockFactory rockFactory,
			boolean isTreeEditable) {
		super(rockFactory, isTreeEditable);
		this.application = application;
		this.promptimplementor = implementordata.getPromptimplementor();
		// Set the panel width, because the default width is not suitable.
		this.attributePaneWidth = defaultWidth;
	}

	/**
	 * List this specific class' parameter components. The identifiers used here
	 * will be displayed as labels in the panel. They will also serve as
	 * identifiers for update callbacks.
	 */
	protected void initializeComponents() {
		addComboBox(PRIORITY, Constants.PROMPTPRIORITY_TEXT,
				getPromptPriorityText(Utils.replaceNull(promptimplementor
						.getPriority())));
	}

	/**
	 * Return the class name of the main node of the implementor. This is what
	 * will be displayed as the main node of the subtree.
	 */
	public String getMainNodeName() {
		return promptimplementor.getPromptclassname();
	}

	/**
	 * Set the name of the main node of the prompt implementor. This is what
	 * will be displayed as the main node of the tree.
	 * 
	 * @throws Exception
	 */
	public void setMainNodeName(final String nodeName) {
		promptimplementor.setPromptclassname(nodeName);
	}

	/**
	 * @param promptPriority
	 * @return
	 */
	private String getPromptPriorityText(final Integer promptPriority) {
		final int pri = promptPriority;
		int ind = 0;
		for (int jnd = 0; jnd < Constants.PROMPTPRIORITY.length; jnd++) {
			if (Constants.PROMPTPRIORITY[jnd] == (pri)) {
				ind = jnd;
				break;
			}
		}
		return Constants.PROMPTPRIORITY_TEXT[ind];
	}

	@Override
	public Object getValueAt(final String identifier) {
		if (PRIORITY.equals(identifier)) {
			return getPromptPriorityText(Utils.replaceNull(promptimplementor
					.getPriority()));
		} else {
			return null;
		}
	}

	/**
	 * Callback function for updates of the components
	 * 
	 * @param value
	 *            the new value of the component
	 * @param identifier
	 *            the identifier of the updated component
	 */
	public void setValueAt(final Object value, final String identifier) {
		if (PRIORITY.equals(identifier)) {
			int ind = 0;
			for (int jnd = 0; jnd < Constants.PROMPTPRIORITY_TEXT.length; jnd++) {
				if (Constants.PROMPTPRIORITY_TEXT[jnd].equals((String) value)) {
					ind = jnd;
					break;
				}
			}
			promptimplementor.setPriority(Constants.PROMPTPRIORITY[ind]);
		}
	}

	/**
	 * Overridden save function. Saves the type and table objects in the DB.
	 */
	public void saveChanges() {
		try {
			if (promptimplementor.gimmeModifiedColumns().size() > 0) {
				promptimplementor.saveToDB();
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), application
					.getContext().getResourceMap().getString(
							"save.error.caption"), JOptionPane.ERROR_MESSAGE);
			logger.log(Level.SEVERE, "Fatal error when saving data", e);
		}
	}

	/**
	 * Overridden remove function. Deletes the type and table objects from the
	 * DB:
	 */
	public void removeFromDB() {
		try {
			promptimplementor.deleteDB();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), application
					.getContext().getResourceMap().getString(
							"delete.error.caption"), JOptionPane.ERROR_MESSAGE);
			logger.log(Level.SEVERE, "Fatal error when removing data", e);
		}
	}

	@Override
	protected void setPanelWidth(final int width) {
		attributePaneWidth = width;
	}

	@Override
	public Vector<String> validateData() {
		return new Vector<String>();
	}

	/**
	 * @return the prompt implementor RockDBObject
	 */
	public Promptimplementor getPromptimplementor() {
		return promptimplementor;
	}

}
