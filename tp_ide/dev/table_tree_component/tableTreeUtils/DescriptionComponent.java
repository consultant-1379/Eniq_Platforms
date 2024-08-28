package tableTreeUtils;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import tableTree.CustomParameterComponent;

/**
 * Helper class for displaying a description component i.e. a pair of a text
 * field and an edit button. The text can be edited directly in the text field
 * or in a separate editor window opened by the edit button.
 * 
 * @author eheitur
 * 
 */
public class DescriptionComponent extends JPanel implements ActionListener,
		FocusListener, CustomParameterComponent, KeyListener {

	/**
     * 
     */
	private static final long serialVersionUID = -5932388242387081844L;

	/**
	 * The frame for the edit pane. This is static so one frame is displayed at
	 * a time.
	 */
	static JFrame thisFrame = null;

	/*
	 * Constants for the button actions used.
	 */
	/**
     * 
     */
	public static final String EDITTEXT = "edittext";
	/**
     * 
     */
	public static final String EDIT = "edit";
	/**
     * 
     */
	public static final String OK = "ok";
	/**
     * 
     */
	public static final String CANCEL = "cancel";
	/**
     * 
     */
	public static final String DESCRIPTION = "Description";

	/**
	 * the description component's text field width, measured in number of
	 * characters
	 */
	private static int defaultTextFieldWidth = 12;

	/**
	 * The value of the edited string
	 */
	protected String newValue = null;

	/**
	 * The edit frame that is displayed when the edit action is triggered.
	 */
	private JFrame editFrame = null;

	/**
	 * The edit pane contained within the edit frame. Used to set and get the
	 * data.
	 */
	private JEditorPane editPane = null;

	/**
	 * The text field for showing the description text
	 */
	JTextField textField = null;

	/**
	 * The edit button for opening the editor window
	 */
	JButton editButton = new JButton("...");

	/**
	 * Action listener for when this is used in parameter panels
	 */
	ActionListener myListener = null;

	/**
	 * Action command for when the action listener is triggered
	 */
	String actionCommand = null;

	/**
	 * Indicator if the component should be enabled or not. This is used when
	 * the text field and the ok-button are disabled in the read-only mode.
	 */
	boolean isEnabled = false;

	/**
	 * Constructor. Initiates the panel (with the text field and the edit
	 * button) and the edit frame.
	 * 
	 * @param value
	 *            the initial text value
	 * @param textFieldWidth
	 *            the width of the text field in characters. In case equal or
	 *            less than zero is specified, then the default value in
	 *            TableTreeConstants will be used.
	 * 
	 */
	public DescriptionComponent(String value, int textFieldWidth) {
		super(new GridBagLayout());
		// Create the new text field
		textField = new JTextField(value);

		// If the given text field width is equal or less than zero, then the
		// default value is used.
		if (textFieldWidth <= 0) {
			// textFieldWidth = TableTreeConstants.DEFAULT_TEXTFIELD_WIDTH;
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

		// Store the new value
		newValue = value;

		// Create the constraints
		GridBagConstraints gbc = null;

		// Configure the JPanel
		this.setBackground(Color.white);

		// Configure the edit button and text field action commands and
		// listeners.
		editButton.setActionCommand(EDIT);
		editButton.addActionListener(this);
		textField.setActionCommand(EDITTEXT);
		textField.addActionListener(this);
		textField.addFocusListener(this);
		textField.setFocusTraversalKeysEnabled(false);
		textField.addKeyListener(this);

		// Configure the layout and add the text field and the button to the
		// panel.

		// Text field
		// textField.setBorder(null);
		gbc = new GridBagConstraints();
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1.0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 0;
		// gbc.insets = new Insets(2, 2, 2, 2);

		this.add(textField, gbc);

		// Edit button
		// editButton.setBorderPainted(false);
		gbc = new GridBagConstraints();
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		// gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.VERTICAL;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.gridx = 1;
		gbc.gridy = 0;
		// gbc.insets = new Insets(2, 2, 2, 2);
		this.add(editButton, gbc);

	}

	/**
	 * This is the listener callback for the three buttons: the cells edit
	 * button, and the dialog's OK and CANCEL buttons.
	 * 
	 * @param e
	 *            the action event
	 */
	public void actionPerformed(ActionEvent e) {

		if (EDIT.equals(e.getActionCommand())) {
			// Starting to edit the cell.
			// Create the edit frame, set the text and show the frame.
			editFrame = createFrame();
			editPane.setText(textField.getText());
			editFrame.setVisible(true);
		} else if (EDITTEXT.equals(e.getActionCommand())) {
			// If the value was updated, store the data and notify listeners.
			if (!newValue.equals(textField.getText())) {
				// Text box editing done. Store data.
				newValue = textField.getText();
				// Fire an event so that the listener knows the value was
				// updated
				if (myListener != null) {
					myListener.actionPerformed(new ActionEvent(this, 0,
							DESCRIPTION));
				}
			}
		} else if (OK.equals(e.getActionCommand())) {
			// Editing done, OK clicked.

			// Store the edited text, dispose the frame.
			String editedText = editPane.getText();
			editFrame.setVisible(false);
			editFrame.dispose();

			// If the value was updated, store the data and notify listeners.
			if (!editedText.equals(textField.getText())) {
				// Store the data, hide and dispose the frame.
				newValue = editPane.getText();
				textField.setText(newValue);

				// Fire an event so that the listener knows the value was
				// updated
				if (myListener != null) {
					myListener.actionPerformed(new ActionEvent(this, 0,
							DESCRIPTION));
				}
			}
		} else { // CANCEL
			// Editing done, CANCEL clicked.
			// Hide and dispose the frame.
			editFrame.setVisible(false);
			editFrame.dispose();
			// editFrame = null;
		}
	}

	/**
	 * Helper method to create the pane and frame.
	 * 
	 * @return the frame
	 */
	private JFrame createFrame() {
		// Create frame
		// JFrame thisFrame = new JFrame();

		// This is done so that only one description frame can be visisble at
		// any one time
		if (thisFrame != null) {
			thisFrame.dispose();
			// thisFrame = null;
		}

		thisFrame = new JFrame();
		thisFrame.setName("DescriptionEditFrame");
		thisFrame.setAlwaysOnTop(true);

		// Create pane
		editPane = new JEditorPane();
		editPane.setName("DescriptionEditFrameEditPane");
		editPane.setPreferredSize(new Dimension(500, 200));
		editPane.setVisible(true);

		// Create buttons
		JButton ok = new JButton("Ok");
		ok.setName("DescriptionEditFrameOkButton");
		JButton cancel = new JButton("Cancel");
		cancel.setName("DescriptionEditFrameCancelButton");
		ok.setPreferredSize(cancel.getPreferredSize());
		ok.setActionCommand(OK);
		ok.addActionListener(this);
		cancel.setActionCommand(CANCEL);
		cancel.addActionListener(this);

		// Set the OK-button and the edit pane enabled according to the current
		// editing mode. This will prevent modifications of the existing values
		// in read-only mode, but the edit pane is still usable.
		ok.setEnabled(this.isEnabled);
		editPane.setEnabled(this.isEnabled);

		// Layout the contents of the frame
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		thisFrame.setLayout(gbl);
		thisFrame.setBackground(Color.white);

		// Place the pane in the layout
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridy = 0;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbl.setConstraints(editPane, gbc);

		thisFrame.add(editPane);

		// Place the buttons in the layout
		gbc.gridwidth = 1;
		gbc.weightx = 1.0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.VERTICAL;
		gbc.gridy = 1;

		gbl.setConstraints(ok, gbc);
		thisFrame.add(ok);

		gbc.anchor = GridBagConstraints.EAST;

		gbl.setConstraints(cancel, gbc);
		thisFrame.add(cancel);

		// And we're done
		thisFrame.pack();
		return thisFrame;
	}

	/**
	 * Set the action listener for this instance
	 * 
	 * @param listener
	 *            the action listener
	 */
	public void addActionListener(ActionListener listener) {
		myListener = listener;
	}

	/**
	 * Update the value of this components textField and then return this
	 * component
	 * 
	 * @return the component
	 */
	public JComponent getComponent() {
		textField.setText(newValue);
		return this;
	}

	/**
	 * Return this component's text field
	 * 
	 * @return the text field
	 */
	public JTextField getTextField() {
		return textField;
	}

	/**
	 * Return this component's edit button
	 * 
	 * @return the edit button
	 */
	public JButton getEditButton() {
		return editButton;
	}

	/**
	 * Use this to set the colors of the DescriptionComponent
	 * 
	 * @param panelBG
	 * @param panelFG
	 * @param textFieldBG
	 * @param textFieldFG
	 */
	public void setColors(Color panelBG, Color panelFG, Color textFieldBG,
			Color textFieldFG) {
		if (panelBG != null) {
			this.setBackground(panelBG);
		}
		if (panelFG != null) {
			this.setForeground(panelFG);
		}
		if (textFieldBG != null) {
			textField.setBackground(textFieldBG);
		}
		if (textFieldFG != null) {
			textField.setForeground(textFieldFG);
		}
	}

	/**
	 * Return the component's text field value
	 * 
	 * @return the text field value
	 */
	public Object getValue() {
		return textField.getText();
	}

	/**
	 * Set the action commmand for this component
	 * 
	 * @param command
	 *            the action command
	 */
	public void setActionCommand(String command) {
		actionCommand = command;
	}

	/**
	 * Execute this code whenever this component gains focus
	 * 
	 * @param e
	 *            the focus event
	 */
	public void focusGained(FocusEvent e) {
		// No actions needed when the component gets the focus.
	}

	/**
	 * Execute this code whenever this component looses focus
	 * 
	 * @param e
	 *            the focus event
	 */
	public void focusLost(FocusEvent e) {
		// If the component loses focus (e.g. user clicks outside the component
		// or moves to another component with tab, then the value text value in
		// the component value is updated based on the current text field value.
		if (!newValue.equals(textField.getText())) {
			newValue = textField.getText();
			if (myListener != null) {
				myListener
						.actionPerformed(new ActionEvent(this, 0, DESCRIPTION));
			}
		}
	}

	/**
	 * Use this method for enabling/disabling the component depending on the
	 * tree's isEditable value
	 * 
	 * @param isEnabled
	 */
	public void setEnabled(boolean isEnabled) {

		// Set the enabled value of the text field according to the parameter
		this.textField.setEnabled(isEnabled);

		// Store the value, so that it can be used when the edit frame is
		// created.
		this.isEnabled = isEnabled;

		// int nrOfComponents = this.getComponentCount();
		// for (int i = 0; i < nrOfComponents; i++) {
		// this.getComponent(i).setEnabled(isEnabled);
		// }
	}

	/**
	 * Overridden version of the method for catching key presses for editing and
	 * focus traversal.
	 * 
	 * @param e
	 *            the key event
	 */
	public void keyPressed(KeyEvent e) {
		// Catch Escape, Enter and Tab presses
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			// Cancel the changes: Copy the old value back to the text field.
			textField.setText(newValue);
		} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			// Transfer the focus to the next component
			e.getComponent().transferFocus();
		} else if (e.getKeyCode() == KeyEvent.VK_TAB) {
			// Transfer the focus to the next component
			e.getComponent().transferFocus();
		}
	}

	/**
	 * No action in case key released.
	 * 
	 * @param e
	 *            the key event
	 */
	public void keyReleased(KeyEvent e) {
		// Intentionally left blank
	}

	/**
	 * No action in case key typed.
	 * 
	 * @param e
	 *            the key event
	 */
	public void keyTyped(KeyEvent e) {
		// Intentionally left blank
	}

}
