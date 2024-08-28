package tableTreeUtils;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * This class is used for prompting the user for text input with limited size.
 * 
 * @author eheitur
 * 
 */
public class LimitedSizeTextDialog extends JDialog implements ActionListener {

    private static final long serialVersionUID = 1L;
    private JPanel myPanel = null;
    LimitedSizeTextField textField = null;
    private JButton okButton = null;
    private JButton cancelButton = null;
    private String answer = null;

    /**
     * Constructor.
     * 
     * @param parentFrame
     * @param title
     * @param message
     * @param textFieldWidth
     * @param initialTextValue
     * @param maxTextLength
     */
    public LimitedSizeTextDialog(JFrame parentFrame, String title,
	    String message, int textFieldWidth, String initialTextValue,
	    int maxTextLength) {
	super(parentFrame, title, true);

	myPanel = new JPanel();
	getContentPane().add(myPanel);

	// Show a message if not null or empty
	if (message != null && message != "") {
	    myPanel.add(new JLabel(message));
	}

	// If the given text field width is equal or less than zero, then the
	// default value is used.
	if (textFieldWidth <= 0) {
	    textFieldWidth = 15;
	}

	// Create the text field.
	//
	// NOTE: The limited size text field cannot be created with the real
	// initial string, but null string must be used instead. The real string
	// value will be set after creation is complete. Furthermore, the
	// required
	// value true will not work either, so false must be used.
	textField = new LimitedSizeTextField(null, null, textFieldWidth,
		maxTextLength, false);
	textField.setText(initialTextValue);

	// Get the character width from the current font of the text field (of
	// character 'W').
	int charWidth = textField.getFontMetrics(textField.getFont())
		.charWidth('W');

	// Set the size of the text field to match the width calculated
	// previously (number of characters * character width).
	textField.setPreferredSize(new Dimension(charWidth * textFieldWidth,
		textField.getMinimumSize().height));
	myPanel.add(textField);

	okButton = new JButton("Ok");
	okButton.setName("LimitedSizeTextDialogOkButton");
	okButton.addActionListener(this);
	myPanel.add(okButton);

	cancelButton = new JButton("Cancel");
	cancelButton.setName("LimitedSizeTextDialogCancelButton");
	cancelButton.addActionListener(this);
	myPanel.add(cancelButton);

	pack();
	setLocationRelativeTo(parentFrame);
	setVisible(true);
    }

    /**
     * @param e
     *                action event
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
	if (okButton == e.getSource()) {
	    answer = textField.getText();
	    setVisible(false);
	} else if (cancelButton == e.getSource()) {
	    answer = null;
	    setVisible(false);
	}
    }

    /**
     * @return the answer text
     */
    public String getAnswer() {
	return answer;
    }

}
