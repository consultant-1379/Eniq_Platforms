package tableTree;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Observable;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import ssc.rockfactory.RockFactory;
import tableTreeUtils.LimitedSizeTextField;
import tableTreeUtils.PairComponent;
import tableTreeUtils.ParameterPanel;

/**
 * Abstract superclass for parameter models. A parameter model maps
 * RockDBObjects to parameter views in the tree. Used by the ParameterPanel
 * class for populating panels with data.
 * 
 * @author ejeahei enaland eheitur
 * 
 */
public abstract class TTParameterModel extends Observable implements
	ActionListener, FocusListener, KeyListener {

    /**
     * The width for the attribute pane (parameters panel). Set this in the
     * extending class if the default width is not suitable.
     */
    protected int attributePaneWidth;
    protected int defaultAttributePaneWidth = 600;

    /**
     * The default width of text fields, measured in number of characters
     */
    private final int defaultTextFieldWidth = 12;

    /**
     * The panel displaying the contents of this class.
     */
    public ParameterPanel parameterPanel;

    /**
     * A boolean that keeps track of whether the tree is in read-only mode or
     * not
     */
    private boolean isTreeEditable;

    /**
     * Maps component names to graphical components. Used for identifying which
     * component has generated an action event.
     */
    private TreeMap<String, Object> components = null;

    /**
     * The rockFactory is used for accessing the database.
     */
    protected RockFactory rockFactory = null;

    /**
     * Constructor. Initializes private variables.
     * 
     * @param RF
     * @param isTreeEditable
     */
    public TTParameterModel(RockFactory RF, boolean isTreeEditable) {
	components = new TreeMap<String, Object>();
	rockFactory = RF;
	this.isTreeEditable = isTreeEditable;
    }

    /**
     * This method is used to add the tree as an observer of this type of models
     * 
     * @param comp
     */
    public void addTableTreeComponentAsObserver(TableTreeComponent comp) {
	addObserver(comp);
    }

    /**
     * Abstract method for retrieving the node name of the main node, e.g. the
     * MeasurementType name. OVERRIDE this to retrieve the actual name based on
     * the RockDBObject.
     * 
     * @return nodeName
     */
    public abstract String getMainNodeName();

    /**
     * Method for setting the width of the panel.
     * 
     * @param width
     */
    protected abstract void setPanelWidth(int width);

    /**
     * Abstract method for setting the node name of the main node, e.g. the
     * MeasurementType name. OVERRIDE this to set the actual name in the
     * RockDBObject.
     * 
     * @param nodeName
     */
    public abstract void setMainNodeName(String nodeName);

    /**
     * Abstract method to initialize the graphical components of the parameter
     * panel. OVERRIDE this to add the components specific to the view.
     */
    protected abstract void initializeComponents();

    /**
     * Abstract method for saving the changes of the parameters in the DB.
     * OVERRIDE this to save the changes.
     */
    public abstract void saveChanges();

    /**
     * Abstract method for removing the parameter objects from the DB. OVERRIDE
     * this to remove.
     */
    public abstract void removeFromDB();

    /**
     * Abstract method acting as a listener for updates to the component data.
     * OVERRIDE this to update the corresponding RockDBObject fields.
     * 
     * @param value
     *                the new value of the data
     * @param identifier
     *                the identifier of the RockDBObject
     */
    public abstract void setValueAt(Object value, String identifier);

    /**
     * Abstract method getting the component data. OVERRIDE this to get the
     * value of the corresponding RockDBObject field.
     * 
     * @param identifier
     *                the identifier of the RockDBObject
     * @return the component data object
     */
    public abstract Object getValueAt(String identifier);

    /**
     * Creates the actual panel for the parameters. Calls initializeComponents
     * to add the components to the panel. No need to override this, unless you
     * need to control the graphical behavior of the panel.
     * 
     * @return parameterPanel
     */
    public ParameterPanel createParametersPanel() {

	// Layout and basic data of the panel
	FlowLayout lay = new FlowLayout();
	parameterPanel = new ParameterPanel();
	parameterPanel.setModel(this);
	parameterPanel.setLayout(lay);
	parameterPanel
		.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

	// Add the individual components. This method is implemented in concrete
	// subclasses.
	initializeComponents();

	// Size and layout
	int componentWspace = lay.getHgap();
	int componentHspace = lay.getVgap();
	int rowHeight = 0;
	double widthTracker = componentWspace;
	int rowMultiplicity = 1;
	Component currentComponent = null;

	// set the parameter panel width to the default width if it has not been
	// set before
	if (attributePaneWidth <= 0)
	    attributePaneWidth = defaultAttributePaneWidth;

	for (int i = 0; i < parameterPanel.getComponentCount(); i++) {
	    currentComponent = parameterPanel.getComponent(i);
	    widthTracker += currentComponent.getPreferredSize().getWidth()
		    + componentWspace;
	    if (widthTracker > attributePaneWidth) {
		rowMultiplicity++;
		widthTracker = currentComponent.getPreferredSize().getWidth()
			+ componentWspace;
	    }
	    if (currentComponent.getPreferredSize().getHeight() > rowHeight) {
		rowHeight = (int) currentComponent.getPreferredSize()
			.getHeight();
	    }
	}

	int panelHeight = ((rowMultiplicity + 1) * componentHspace)
		+ (rowMultiplicity * rowHeight);

	parameterPanel.setPreferredSize(new Dimension(attributePaneWidth,
		panelHeight));
	parameterPanel.setBorder(new LineBorder(Color.black));

	return parameterPanel;
    }

    /**
     * Helper method used by the subclasses to add text field components to the
     * panel. This should be used by the subclasses to instantiate the contents
     * of the panel.
     * 
     * @param title
     *                the title of the component, will be shown as a label to
     *                the left of the component. This needs to be unique in the
     *                panel.
     * @param initData
     *                the data initially displayed in the component
     * @param textFieldWidth
     *                the width of the the text field (number of characters).
     * @return the added pair component
     */
    protected PairComponent addTextField(String title, String initData,
	    int textFieldWidth) {
	assert !components.containsKey(title);

	JTextField textField = new JTextField(initData);

	// If the given text field width is equal or less than zero, then the
	// default value is used.
	if (textFieldWidth <= 0) {
	    textFieldWidth = defaultTextFieldWidth;
	}

	// Get the character width from the current font of the text field (of
	// character 'W').
	int charWidth = textField.getFontMetrics(textField.getFont())
		.charWidth('W');

	// Set the size of the text field to match the width calculated
	// previously (number of characters * character width).
	textField.setPreferredSize(new Dimension(charWidth * textFieldWidth,
		textField.getMinimumSize().height));

	// Add it to the panel and set the listener
	PairComponent comp = new PairComponent(title, textField);
	parameterPanel.add(comp);
	textField.setActionCommand(title);
	textField.addActionListener(this);
	textField.addFocusListener(this);
	textField.addKeyListener(this);
	textField.setEnabled(this.isTreeEditable);
	textField.setFocusTraversalKeysEnabled(false);

	// Store it in the vector of components
	components.put(title, textField);

	return comp;
    }

    /**
     * Helper method used by the subclasses to add text field components to the
     * panel. This should be used by the subclasses to instantiate the contents
     * of the panel.
     * 
     * @param title
     *                the title of the component, will be shown as a label to
     *                the left of the component. This needs to be unique in the
     *                panel.
     * @param initData
     *                the data initially displayed in the component
     * @param limit
     *                the width of the text field in characters
     * @param limit
     *                the maximum number of characters. Used also as the width
     *                of the text field.
     * @return the added pair component
     */
    protected PairComponent addTextFieldWithLimitedSize(String title,
	    String initData, int width, int limit, boolean required) {
	assert !components.containsKey(title);

	LimitedSizeTextField textField = new LimitedSizeTextField(limit,
		required);
	textField.setText(initData);

	// Get the character width from the current font of the text field (of
	// character 'W').
	int charWidth = textField.getFontMetrics(textField.getFont())
		.charWidth('W');

	// Set the size of the text field to match the width calculated
	// previously (number of characters * character width).
	textField.setPreferredSize(new Dimension(charWidth * width, textField
		.getMinimumSize().height));

	// Add it to the panel and set the listener
	PairComponent comp = new PairComponent(title, textField);
	parameterPanel.add(comp);
	textField.setActionCommand(title);
	textField.addActionListener(this);
	textField.addFocusListener(this);
	textField.addKeyListener(this);
	textField.setEnabled(this.isTreeEditable);
	textField.setFocusTraversalKeysEnabled(false);

	// Store it in the vector of components
	components.put(title, textField);

	return comp;
    }

    /**
     * Helper method used by the subclasses to add check box components to the
     * panel.
     * 
     * @param title
     *                the title of the component, will be shown as a label to
     *                the left of the component. This needs to be unique in the
     *                panel.
     * @param selected
     *                the initial selection state of the component
     * @return the added pair component
     */
    protected PairComponent addCheckBox(String title, boolean selected) {
	assert !components.containsKey(title);

	// Create it
	JCheckBox checkBox = new JCheckBox();
	checkBox.setSelected(selected);

	// Add it and set the listener
	PairComponent comp = new PairComponent(title, checkBox);
	parameterPanel.add(comp);
	checkBox.setActionCommand(title);
	checkBox.addActionListener(this);
	// checkBox.addFocusListener(this);
	checkBox.setEnabled(this.isTreeEditable);

	// Store it in the vector of components
	components.put(title, checkBox);

	return comp;
    }

    /**
     * Helper method used by the subclasses to add combo box components to the
     * panel.
     * 
     * @param title
     *                the title of the component, will be shown as a label to
     *                the left of the component. This needs to be unique in the
     *                panel.
     * @param comboItems
     *                the list of items in the combo box
     * @param selected
     *                the element initially selected in the combo box
     * @return the added pair component
     */
    protected PairComponent addComboBox(String title, String[] comboItems,
	    String selectedItem) {
	assert !components.containsKey(title);

	// Create it
	JComboBox comboBox = new JComboBox(comboItems);
	comboBox.setEditable(false);
	// ComboBox selection fixed by eheijun 2.7.2008
	// comboBox.setSelectedItem(selectedItem);
	// Set the selected item in the list. The value can be null in the
	// database, so the selection match against the combo box items is
	// checked first.
	comboBox.setSelectedIndex(-1);
	for (int ind = 0; ind < comboBox.getItemCount(); ind++) {
	    if (comboBox.getItemAt(ind).equals(selectedItem)) {
		comboBox.setSelectedItem(selectedItem);
		break;
	    }
	}

	// Add it to the panel and set the listener
	PairComponent comp = new PairComponent(title, comboBox);
	parameterPanel.add(comp);
	comboBox.setActionCommand(title);
	comboBox.addActionListener(this);
	comboBox.setEnabled(this.isTreeEditable);

	// Store it in the vector of components
	components.put(title, comboBox);

	return comp;
    }

    /**
     * Helper method used by the subclasses to add generic components to the
     * panel.
     * 
     * @param title
     *                the title of the component, will be shown as a label to
     *                the left of the component. This needs to be unique in the
     *                panel.
     * @param component
     *                the component to be added, must implement the
     *                CustomParameterComponent interface
     * @return the added pair component
     */
    protected PairComponent addComponent(String title,
	    CustomParameterComponent component) {
	assert !components.containsKey(title);

	// Add it to the panel and set the listener
	PairComponent comp = new PairComponent(title, component.getComponent());
	parameterPanel.add(comp);
	component.setActionCommand(title);
	component.addActionListener(this);
	component.setEnabled(this.isTreeEditable);

	// Store it in the vector of components
	components.put(title, component);

	return comp;
    }

    /**
     * Callback method for the component listener. The event will contain the
     * key of the component in the map.
     * 
     * @param e
     *                the action event
     */
    public void actionPerformed(ActionEvent e) {
	String key = e.getActionCommand();

	// Get the component out of the vector
	Object modifiedComponent = components.get(key);

	// Check what type of component and delegate the handling
	// to the subclass
	if (modifiedComponent instanceof JCheckBox) {
	    boolean value = ((JCheckBox) modifiedComponent).isSelected();
	    this.setValueAt(value, key);
	} else if (modifiedComponent instanceof JComboBox) {
	    Object value = ((JComboBox) modifiedComponent).getSelectedItem();
	    this.setValueAt(value, key);
	} else if (modifiedComponent instanceof JTextField) {
	    // If the text value was updated, then set the value. Otherwise just
	    // exit. This is needed to avoid unnecessary updating of the field
	    // when the component loses focus without any update to the text.
	    Object value = ((JTextField) modifiedComponent).getText();
	    if (!value.equals((String) this.getValueAt(key))) {
		this.setValueAt(value, key);
	    } else {
		// No change in the text
		return;
	    }
	} else if (modifiedComponent instanceof CustomParameterComponent) {
	    Object value = ((CustomParameterComponent) modifiedComponent)
		    .getValue();
	    this.setValueAt(value, key);
	} else {
	    // Error: invalid component
	    System.out.println(this.getClass()
		    + "actionPerformed(): Invalid component: "
		    + modifiedComponent.toString() + ".");
	}
	this.setChanged();
	this.notifyObservers();
    }

    /**
     * Method for handling the focus gained event. No action at the moment.
     * 
     * @param e
     *                focus event
     */
    public void focusGained(FocusEvent e) {
	// intentionally empty
    }

    /**
     * Method for handling the focus lost event for the parameter panel. It is
     * used to tell the component being edited at the moment of losing focus
     * that something happened. This info can be used to save or discard the
     * changes, etc...
     * 
     * @param e
     *                focus event
     */
    public void focusLost(FocusEvent e) {
	if (e.getComponent() instanceof JTextField) {
	    JTextField text = (JTextField) e.getComponent();
	    text.postActionEvent();
	}
    }

    /**
     * Overridden version of the method for catching key presses for editing and
     * focus traversal. The key presses are caught only for the normal text
     * fields.
     * 
     * @param e
     *                key event
     */
    public void keyPressed(KeyEvent e) {
	// Only catch key presses for the text fields.
	if (e.getComponent() instanceof JTextField) {
	    JTextField comp = (JTextField) e.getComponent();
	    // Catch Escape, Enter and Tab presses
	    if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
		// Cancel the changes: Copy the old value back to the text
		// field.
		String oldValue = (String) this
			.getValueAt(((PairComponent) comp.getParent())
				.getTitle());
		System.out.println("old: " + oldValue + ", new: "
			+ comp.getText());
		comp.setText(oldValue);
	    } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
		// Transfer the focus to the next component
		e.getComponent().transferFocus();
	    } else if (e.getKeyCode() == KeyEvent.VK_TAB) {
		// Transfer the focus to the next component
		e.getComponent().transferFocus();
	    }
	}
    }

    /**
     * Method for handling key released event. No action at the moment.
     * 
     * @param e
     *                key event
     */
    public void keyReleased(KeyEvent e) {
	// Intentionally left black
    }

    /**
     * Method for handling key typed event. No action at the moment.
     * 
     * @param e
     *                key event
     */
    public void keyTyped(KeyEvent e) {
	// Intentionally left black
    }

    /**
     * Validates the parameter models data. OVERRIDE this so that all necessary
     * validations are executed for this table model.
     * 
     * @return validation error messages. Empty vector in case validation was
     *         successful.
     */
    public abstract Vector<String> validateData();
}
