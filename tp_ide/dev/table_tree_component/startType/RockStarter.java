package startType;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import measurementType.MeasurementTypeFactory;
import tableTree.TableTreeComponent;
import tableTree.TreeDataFactory;

/**
 * This test class creates a TableTreeComponent for MeasurementTypes and
 * displays it in a frame.
 * 
 * @author enaland ejeahei
 * 
 */
public class RockStarter implements ActionListener, DocumentListener {

    /**
     * The factory
     */
    public TreeDataFactory treeDataFactory;

    /**
     * The component
     */
    public TableTreeComponent myTTC;

    /**
     * True in case the tree is editable
     */
    public static boolean isTreeEditable;

    /**
     * The main starter method.
     * 
     * @param args
     */
    public static void main(String[] args) {
	RockStarter rs = new RockStarter();

	JFrame myFrame = new JFrame();
	BorderLayout GL = new BorderLayout();
	myFrame.setLayout(GL);

	isTreeEditable = true;

	rs.treeDataFactory = new MeasurementTypeFactory(isTreeEditable,
		"PREFIX_");
	rs.myTTC = new TableTreeComponent(rs.treeDataFactory);
	rs.myTTC.addDocumentListener(rs);

	JScrollPane scrollPane = new JScrollPane(rs.myTTC);
	myFrame.add(scrollPane, BorderLayout.CENTER);

	JPanel buttons = new JPanel();

	JButton validate = new JButton("Validate");
	validate.setActionCommand("validate");
	validate.addActionListener(rs);
	buttons.add(validate);

	JButton save = new JButton("Save");
	save.setActionCommand("save");
	save.addActionListener(rs);
	buttons.add(save);

	JButton discard = new JButton("Discard");
	discard.setActionCommand("discard");
	discard.addActionListener(rs);
	buttons.add(discard);

	JPanel panel = new JPanel(new GridLayout(0, 3));
	panel.add(validate);
	panel.add(save);
	panel.add(discard);
	myFrame.add(panel, BorderLayout.SOUTH);

	// Let's rock and roll!
	myFrame.setSize(700, 700);
	myFrame.setTitle("Rock 'n' roll");
	myFrame.setVisible(true);
	myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * @param e
     *                the action event
     */
    public void actionPerformed(ActionEvent e) {

	if (e.getActionCommand().equals("save")) {
	    // System.out.println(myTTC.getDeletedData().size());
	    // System.out.println(myTTC.getNewUnsavedData().size());
	    // System.out.println(myTTC.getAllData().size());
	    myTTC.getDeletedData();
	    myTTC.getNewUnsavedData();
	    myTTC.getAllData();
	    myTTC.saveChanges();
	} else if (e.getActionCommand().equals("discard")) {
	    myTTC.discardChanges();
	} else if (e.getActionCommand().equals("validate")) {
	    System.out.println("saveChanges(): Validating table data.");
	    Vector<String> errors = myTTC.validateData();
	    if (errors.size() > 0) {
		System.out.println("Data validation error messages: ");
		Iterator<String> iter = errors.iterator(); 
		    while (iter.hasNext()){
			System.out.println(iter.next());
		    }
	    } else
		System.out.println("Data validation successful.");

	}

    }

    /**
     * This method is used to receive the change indications from the document
     * listener.
     * 
     * @param e
     *                the document event
     */
    public void changedUpdate(DocumentEvent e) {
	// This changed event can be used to e.g. enabling the save button when
	// something has changed in the tableTreeComponent.
	System.out
		.println("RockStarter: changedUpdate event from the listener.");
    }

    /**
     * This method is used to receive insert updates from the document listener.
     * No action at the moment.
     * 
     * @param e
     *                the document event
     */
    public void insertUpdate(DocumentEvent e) {
	// Intentionally left blank
    }

    /**
     * This method is used to receive remove updates from the document listener.
     * No action at the moment.
     * 
     * @param e
     *                the document event
     */
    public void removeUpdate(DocumentEvent e) {
	// Intentionally left blank }
    }
}
