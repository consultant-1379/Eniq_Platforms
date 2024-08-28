package com.ericsson.eniq.techpacksdk.view.etlSetHandling;

import java.util.Vector;
import java.util.logging.Logger;

import ssc.rockfactory.RockFactory;
import tableTree.TTParameterModel;

import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.ericsson.eniq.techpacksdk.common.Utils;

/**
 * Concrete class for parameter data for ETL Sets
 * 
 * @author ejarsav
 * 
 */
public class ETLSetModel extends TTParameterModel {

	private static final Logger logger = Logger
			.getLogger(ETLActionTableModel.class.getName());

	/**
	 * The RockDBObject representing the ETL Set
	 */
	private Meta_collections typeObject;

	/**
	 * Identifiers for the components in the panel displaying the parameters.
	 */
	protected static final String TYPE = "Type";

  protected static final String[] TYPE_ITEMS = { "Adapter", "Aggregator", "Alarm", "Count", "Install", "Loader",
      "Mediation", "Partition", "Service", "Support", "Topology", "Backup" };;

	protected static final String STATUS = "Disable";

	protected static final String PRIORITY = "Priority";

	protected static final String QTIME = "Queue Time";

	protected final static int defaultWidth = 900;

	private Vector<Meta_collections> removeList;

	private RockFactory rockFactory;

	/**
	 * Constructor. Stores the given RockDBObjects.
	 * 
	 * @param inType
	 * @param inTable
	 */
	public ETLSetModel(Vector<Meta_collections> removeList,
			Meta_collections inType, RockFactory rockFactory, boolean editable) {
		super(rockFactory, editable);
		this.rockFactory = rockFactory;
		typeObject = inType;
		this.attributePaneWidth = defaultWidth;
		this.removeList = removeList;
	}

	/**
	 * List this specific class' parameter components. The identifiers used here
	 * will be displayed as labels in the panel. They will also serve as
	 * identifiers for update callbacks.
	 */
	protected void initializeComponents() {

		addComboBox(TYPE, TYPE_ITEMS, typeObject.getSettype());
		addCheckBox(STATUS, "Y".equalsIgnoreCase(typeObject.getHold_flag()));
		addTextField(PRIORITY, Utils.replaceNull(typeObject.getPriority())
				.toString(), 4);
		addTextField(QTIME, Utils.replaceNull(typeObject.getQueue_time_limit())
				.toString(), 4);
	}

	/**
	 * Return the name of the main node of the measurement type. This is what
	 * will be displayed as the main node of the subtree.
	 */
	public String getMainNodeName() {
		return typeObject.getCollection_name();
	}

	/**
	 * Set the name of the main node of the measurement type. This is what will
	 * be displayed as the main node of the tree.
	 */
	public void setMainNodeName(String nodeName) {
		typeObject.setCollection_name(nodeName);
	}

	/**
	 * Callback function for updates of the components
	 * 
	 * @param value
	 *            the new value of the component
	 * @param identifier
	 *            the identifier of the updated component
	 */
	public void setValueAt(Object value, String identifier) {

		if (TYPE.equals(identifier)) {
			typeObject.setSettype((String) value);
		} else if (STATUS.equals(identifier)) {
			if ((Boolean) value) {
				typeObject.setHold_flag("Y");
			} else {
				typeObject.setHold_flag("N");
			}
		} else if (PRIORITY.equals(identifier)) {
			typeObject.setPriority(Long.parseLong((String) value));
		} else if (QTIME.equals(identifier)) {
			typeObject.setQueue_time_limit(Long.parseLong((String) value));
		} else {
			// Error
			System.out.println(this.getClass()
					+ "setValueAt(): Invalid identifier: " + identifier);
		}

	}

	/**
	 * Overridden save function. Saves the type and table objects in the DB.
	 */
	public void saveChanges() {

		try {
			if (typeObject.gimmeModifiedColumns().size() > 0) {
				if (typeObject.getCollection_id() == -1) {
					typeObject
							.setCollection_id(Utils.getSetMaxID(rockFactory) + 1);
					typeObject.setNewItem(true);
				}
				typeObject.saveDB();
				logger.info("save counter " + typeObject.getCollection_name()
						+ " of " + typeObject.getVersion_number());
			}
		} catch (Exception e) {
			logger.severe(e.getMessage());
		}

	}

	/**
	 * Overridden remove function. Deletes the type and table objects from the
	 * DB:
	 */
	public void removeFromDB() {

		removeList.add(typeObject);

		/*
		 * try { typeObject.deleteDB(); } catch (SQLException e) {
		 * e.printStackTrace(); } catch (RockException e) { e.printStackTrace();
		 * }
		 */
	}

	@Override
	protected void setPanelWidth(int width) {
		// TODO Auto-generated method stub
	}

	@Override
	public Object getValueAt(String identifier) {
		if (TYPE.equals(identifier)) {
			return typeObject.getSettype();
		} else if (STATUS.equals(identifier)) {
			if (typeObject.getHold_flag().equals("Y")) {
				return true;
			} else {
				return false;
			}
		} else if (PRIORITY.equals(identifier)) {
			return typeObject.getPriority().toString();
		} else if (QTIME.equals(identifier)) {
			return typeObject.getQueue_time_limit().toString();
		} else {
			// Error
			System.out.println(this.getClass()
					+ "getValueAt(): Invalid identifier: " + identifier);
			return null;
		}
	}

	@Override
	public Vector<String> validateData() {
    final Vector<String> errorStrings = new Vector<String>();
    if (Utils.replaceNull(typeObject.getSettype()).trim().equals("")) {
      errorStrings.add(typeObject.getCollection_name() + ": " + TYPE + " is required");
    }
    return errorStrings;
	}

	/**
	 * @return the type object
	 */
	public Meta_collections getTypeObject() {
		return typeObject;
	}

}
