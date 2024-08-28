package com.ericsson.eniq.techpacksdk.view.universeParameters;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import org.jdesktop.swingworker.SwingWorker;

import com.ericsson.eniq.techpacksdk.LimitedSizeTextArea;
import com.ericsson.eniq.techpacksdk.LimitedSizeTextField;
import com.ericsson.eniq.techpacksdk.common.Utils;

/**
 * An expandable text field component. The component consists of a text field,
 * and a button. The button opens a dialog, containing a text area, in which
 * the text field's contents can be edited. 
 * 
 * @author epiituo
 *
 */
@SuppressWarnings("serial")
public class ExpandableTextField extends JPanel {

	public static final Color ERROR_INDICATING_COLOR = Color.PINK;
	public static final Color NORMAL_COLOR = Color.WHITE;
	
	private LimitedSizeTextField textField;

	private JButton button;

	private JDialog editDialog;

	private int limit;

	private boolean editable;

	 private boolean required;
	
	 private char[] allowedChars;
	
	ActionListener myListener = null;
	
	private String actionCommand;

	public ExpandableTextField(int columns, int limit, boolean required, boolean editable) {
		super();

		this.editable = editable;

		this.required = required;
		
		this.limit = limit;

		GridBagLayout gridBagLayout = new GridBagLayout();
		this.setLayout(gridBagLayout);

		// Create grid bag constraints object
		GridBagConstraints c = new GridBagConstraints();

		// Create a limited-sized textfield and add it to the panel
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1;
		c.weightx = 1;
		this.textField = new LimitedSizeTextField(columns, limit, required);
		this.textField.setEditable(this.editable);
		this.add(textField, c);

		// Get the allowed characters from the limited size textfield, so that
		// it can be edited from the edit dialog.
		this.allowedChars = this.textField.getAllowedChars();

		// Create a button and add it to the panel
		c.anchor = GridBagConstraints.NORTHEAST;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 1;
		c.weightx = 0;
		this.button = new JButton("...");

		ActionListener buttonListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				displayEditDialog();
			}
		};
		this.button.addActionListener(buttonListener);
		this.add(button, c);
	}
	
	private void displayEditDialog() {
		// Dispose the possible previous dialog
		if (this.editDialog != null) {
			this.editDialog.dispose();
		}

		// Open a new edit dialog
		this.editDialog = new EditDialog(this.textField, this.allowedChars, this.editable, this.required);
		Utils.center(this.editDialog);
		this.editDialog.setVisible(true);
	}

	public String getText() {
		return this.textField.getText();
	}

	public void setText(String t) {
		this.textField.setText(t);
	}

	/**
	 * Set the action command for this component
	 */
	public void setActionCommand(final String command) {
		actionCommand = command;
	}

	/**
	 * Set the action listener for this instance
	 */
	public void addActionListener(final ActionListener l) {
		this.textField.addActionListener(l);
		myListener = l;
	}

	public synchronized void removeActionListener(ActionListener l) {
		this.textField.removeActionListener(l);
	}
	
	public synchronized ActionListener[] getActionListeners() {
		return this.textField.getActionListeners();
	}
	
		 
	public class EditDialog extends JDialog {
		
		private static final String EDIT_DIALOG_CAPTION = "Edit";
		private static final String VIEW_DIALOG_CAPTION = "View";

		private static final String OK_BUTTON_TEXT = "OK";
		private static final String CANCEL_BUTTON_TEXT = "Cancel";

		private static final int PREFERRED_EDIT_PANEL_WIDTH = 300;
		private static final int PREFERRED_EDIT_PANEL_HEIGHT = 200;

		private JPanel editPanel;
		private JPanel buttonPanel;
		private JTextArea textArea;
		private JButton okButton;
		private JButton cancelButton;

		private JTextField sourceTextField;

		private boolean editable;



		public EditDialog(JTextField targetTextField, char[] allowedChars, boolean editable, boolean required) {
			super();
			this.editable = editable;
			if (this.editable) {
				this.setTitle(EDIT_DIALOG_CAPTION);
			}
			else {
				this.setTitle(VIEW_DIALOG_CAPTION);
			}

			this.setAlwaysOnTop(true);
			this.setModal(true);
			this.setBackground(Color.WHITE);

			this.sourceTextField = targetTextField;

			// Create a grid bag layout instance
			GridBagLayout gridBagLayout = new GridBagLayout();
			this.setLayout(gridBagLayout);

			// Create grid bag constraints object.
			GridBagConstraints c = new GridBagConstraints();

			// Create text field and add it to the editing panel
			this.textArea = new LimitedSizeTextArea(limit, required) {
				protected Document createDefaultModel() {
					return new LimitedCharactersDocument();
				}
			};
			this.textArea.setText(targetTextField.getText());
			this.textArea.setLineWrap(true);
			this.textArea.setEditable(this.editable);
			//this.textArea.setEnabled(this.editable);
			this.editPanel = new JPanel(gridBagLayout);
			this.editPanel.setPreferredSize(new Dimension(PREFERRED_EDIT_PANEL_WIDTH,
					PREFERRED_EDIT_PANEL_HEIGHT));
			c.anchor = GridBagConstraints.NORTHWEST;
			c.fill = GridBagConstraints.BOTH;
			c.insets = new Insets(0, 0, 0, 0);
			c.weightx = 1;
			c.weighty = 1;
			
	    JScrollPane textAreaPanelS = new JScrollPane(this.textArea);
			
			this.editPanel.add(textAreaPanelS, c);

			// Create the buttons and add them to the button panel
			okButton = new JButton(OK_BUTTON_TEXT);
			okButton.setName("EditDialogOkButton");
			okButton.setActionCommand(OK_BUTTON_TEXT);
			// IDE #660 & 663 Save and Cancel buttons not active after Busy Hour criteria modified by Edit window ,11/09/09, ejohabd
			ActionListener okButtonListener = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					updateSourceTextField(); 
				// Fire an event so that the listener knows the value was updated
				if (myListener != null) {
				    myListener.actionPerformed(new ActionEvent(this, 0,
					    	actionCommand));
				}
					dispose();
				}
			};
			this.okButton.addActionListener(okButtonListener);

			this.cancelButton = new JButton(CANCEL_BUTTON_TEXT);
			this.cancelButton.setName("EditDialogCancelButton");

			ActionListener cancelButtonListener = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			};
			this.cancelButton.addActionListener(cancelButtonListener);

			this.buttonPanel = new JPanel(gridBagLayout);
			c.anchor = GridBagConstraints.EAST;
			c.fill = GridBagConstraints.NONE;
			c.insets = new Insets(2, 2, 2, 2);
			c.gridx = 1;
			c.weightx = 0;
			c.weighty = 0;
			this.buttonPanel.add(this.okButton, c);

			// If the text field is not editable, then a cancel button is not
			// required.
			if (this.editable) {
				c.anchor = GridBagConstraints.EAST;
				c.fill = GridBagConstraints.NONE;
				c.insets = new Insets(2, 2, 2, 2);
				c.gridx = 0;
				c.weightx = 0;
				c.weighty = 0;
				this.buttonPanel.add(this.cancelButton, c);
			}

			// Add the panels to the dialog
			c.anchor = GridBagConstraints.NORTH;
			c.fill = GridBagConstraints.BOTH;
			c.insets = new Insets(0, 0, 0, 0);
			c.gridy = 0;
			c.weightx = 1;
			c.weighty = 1;
			this.add(this.editPanel, c);

			c.anchor = GridBagConstraints.SOUTHEAST;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.insets = new Insets(0, 0, 0, 0);
			c.gridy = 1;
			c.weightx = 1;
			c.weighty = 0;
			this.add(this.buttonPanel, c);

			this.pack();
		}


		private void updateSourceTextField() {
			this.sourceTextField.setText(this.textArea.getText());
		}

		public class LimitedCharactersDocument extends PlainDocument {
			
			public void insertString(final int offs, final String str, final AttributeSet a) throws BadLocationException {
				// First, make sure that the string is not null.
				if (str == null) {
					return;
				}
				
				if (allCharactersAreLegal(str) && textSizeIsWithinLimitBeforeInsertion()) {
					// Will the string fit into the field entirely?
					if (textSizeIsWithinLimitAfterInsertion(str)) {
						// The string will not entirely fit into the field, 
						// therefore insert only a substring.
						super.insertString(offs, str, a);
					}
					else {
						// The string can be inserted entirely.
						super.insertString(offs, str.substring(0, limit - textArea.getText().length()), a);
					}
				} else {
					// All characters were not legal, or the limit for the text
					// size was already reached.
					indicateError();
				}
			}
			
			private boolean allCharactersAreLegal(String string) {
				boolean allCharactersAreLegal = true;
				for (int i = 0; i < string.length(); i++) {
						final char character = string.charAt(i);
						if (!characterIsLegal(character)) {
							allCharactersAreLegal = false;
						}
				}
				return allCharactersAreLegal;
			}
			
			private boolean characterIsLegal(char character) {
				boolean characterWasFound = false;
				int index = 0;
				while (index < allowedChars.length && !characterWasFound) {
					if (character == allowedChars[index]) {
						characterWasFound = true;
					}
					++index;
				}
				return characterWasFound;
			}
			
			private boolean textSizeIsWithinLimitBeforeInsertion() {
				return (textArea.getText().length() <= limit);
			}
			
			private boolean textSizeIsWithinLimitAfterInsertion(String string) {
				return (textArea.getText().length() + string.length() <= limit);
			}

			private void indicateError() {
				final ErrorIndicator v = new ErrorIndicator();
				v.execute();
			}
		}
		
		public class ErrorIndicator extends SwingWorker<Void, Void> {

			public Void doInBackground() {
				try {
					textArea.setBackground(ExpandableTextField.ERROR_INDICATING_COLOR);
					Thread.sleep(50);
				} catch (InterruptedException ie) {

				}
				return null;
			}

			protected void done() {
				textArea.setBackground(ExpandableTextField.NORMAL_COLOR);
			}

		};
	};
          
}
