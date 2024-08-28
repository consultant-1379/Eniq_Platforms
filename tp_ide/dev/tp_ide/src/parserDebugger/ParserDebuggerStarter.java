package parserDebugger;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Test class used for creating a ParserDebuggerComponent and displaying it in a
 * frame. Test data simulating the output from the parser is also created.
 * 
 * @author eheitur
 * 
 */
public class ParserDebuggerStarter implements ActionListener {

	private static final long serialVersionUID = 1L;

	/**
	 * The parser debugger component reference
	 */
	ParserDebuggerComponent pdc = null;

	/**
	 * The frame holding the component and buttons for testing
	 */
	static JFrame frame = null;

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event-dispatching thread.
	 */
	private static void createAndShowGUI() {
		ParserDebuggerStarter pd = new ParserDebuggerStarter();

		// Create and set up the window.
		frame = new JFrame("Parser Debugger Demo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());

		// Create a dummy JLabel to the top of the frame so that the
		// ParserDebuggerComponent is not in the top-left corner. This is just
		// for testing the layout inside the frame.
		JLabel dummyLabel = new JLabel("Dummy label outside the component");
		// dummyLabel.setBorder(BorderFactory.createLineBorder(Color.RED));
		frame.add(dummyLabel, BorderLayout.NORTH);

		// Create the ParserDebuggerComponent and add it to the center of the
		// frame.
		pd.pdc = new ParserDebuggerComponent();
		pd.pdc.setPreferredSize(new Dimension(600, 600));
		frame.add(pd.pdc, BorderLayout.CENTER);

		// Create a panel for the buttons
		JPanel buttons = new JPanel(new GridLayout(0, 4));
		// buttons.setBorder(BorderFactory.createLineBorder(Color.RED));

		JButton next = new JButton(">");
		next.setToolTipText("Move to the next transformation");
		next.setActionCommand("next");
		next.addActionListener(pd);
		buttons.add(next);

		JButton last = new JButton(">>");
		last.setToolTipText("Move to last transformation for this row");
		last.setActionCommand("last");
		last.addActionListener(pd);
		buttons.add(last);

		JButton end = new JButton(">|");
		end.setToolTipText("Move to last transformation for the last row");
		end.setActionCommand("end");
		end.addActionListener(pd);
		buttons.add(end);

		JButton reset = new JButton("X");
		reset.setToolTipText("Reset transformations");
		reset.setActionCommand("reset");
		reset.addActionListener(pd);
		buttons.add(reset);

		// Add the buttons panel to the bottom of the frame
		frame.add(buttons, BorderLayout.SOUTH);

		// Display the frame and the glass pane.
		frame.pack();
		frame.setVisible(true);
		// frame.getGlassPane().setVisible(true);

		// Create testing data
		pd.createTestingData();

		// Redraw the component
		pd.pdc.redrawComponent();
	}

	public void createTestingData() {

		// Mark the important columns, which will be eventually stored to the DB
		// after all transformations are done.
		Vector<String> importantColumns = new Vector<String>();
		importantColumns.add("Col2");
		importantColumns.add("Col4");
		importantColumns.add("Col6");
		importantColumns.add("Col7");
		this.pdc.setImportantColumns(importantColumns);

		// Create the transformations
		String[] fromColumns1 = { "Col1", "Col2" };
		String[] toColumns1 = { "Col6" };
		this.pdc.addTransformation("Trans1", fromColumns1, toColumns1);
		String[] fromColumns2 = { "Col4" };
		String[] toColumns2 = { "Col4" };
		this.pdc.addTransformation("Trans2", fromColumns2, toColumns2);
		String[] fromColumns3 = { "Col5" };
		String[] toColumns3 = { "Col7" };
		this.pdc.addTransformation("Trans3", fromColumns3, toColumns3);
		String[] fromColumns4 = { "Col4", "Col6" };
		String[] toColumns4 = { "Col4", "Col8" };
		this.pdc.addTransformation("Trans4", fromColumns4, toColumns4);

		// Create a new parsed row object
		ParsedRowData row1 = this.pdc.createRow();

		// Create the tables for row1
		//
		// Create table 1
		HashMap<String, Object> tableData = new HashMap<String, Object>();

		tableData.put("Col1", "Mary");
		tableData.put("Col2", "Campione");
		tableData.put("Col3", "Snowboarding");
		tableData.put("Col4", new Integer(5));
		tableData.put("Col5", new Boolean(false));
		row1.addTable(tableData);

		// Create table 2
		tableData = new HashMap<String, Object>();
		tableData.put("Col1", "Mary");
		tableData.put("Col2", "Campione");
		tableData.put("Col3", "Snowboarding");
		tableData.put("Col4", new Integer(5));
		tableData.put("Col5", new Boolean(false));
		tableData.put("Col6", "Mary Campione");
		row1.addTable(tableData);

		// Create table 3
		tableData = new HashMap<String, Object>();
		tableData.put("Col1", "Mary");
		tableData.put("Col2", "Campione");
		tableData.put("Col3", "Snowboarding");
		tableData.put("Col4", new Integer(6));
		tableData.put("Col5", new Boolean(false));
		tableData.put("Col6", "Mary Campione");
		row1.addTable(tableData);

		// Create table 4
		tableData = new HashMap<String, Object>();
		tableData.put("Col1", "Mary");
		tableData.put("Col2", "Campione");
		tableData.put("Col3", "Snowboarding");
		tableData.put("Col4", new Integer(6));
		tableData.put("Col5", new Boolean(false));
		// tableData.put("Col6", "Mary Campione");
		// tableData.put("Col7", new Boolean(false));
		// System.out.print("HashMap: " + tableData + ".");
		row1.addTable(tableData);

		// Create table 5
		tableData = new HashMap<String, Object>();
		tableData.put("Col1", "Mary");
		tableData.put("Col2", "Campione");
		tableData.put("Col3", "Snowboarding");
		tableData.put("Col4", new Integer(7));
		tableData.put("Col5", new Boolean(false));
		tableData.put("Col6", "Mary Campione");
		tableData.put("Col7", new Boolean(false));
		tableData.put("Col8", "Mary Campione 7");
		// System.out.print("HashMap: " + tableData + ".");
		row1.addTable(tableData);

		// Create a new parsed row object
		ParsedRowData row2 = this.pdc.createRow();

		// Create the tables for row1
		//
		// Create table 1
		tableData = new HashMap<String, Object>();
		tableData.put("Col1", "abcd");
		tableData.put("Col2", "efgh");
		tableData.put("Col3", "ijkl");
		tableData.put("Col4", new Integer(100));
		tableData.put("Col5", new Boolean(false));
		row2.addTable(tableData);

		// Create table 2
		tableData = new HashMap<String, Object>();
		tableData.put("Col1", "abcd");
		tableData.put("Col2", "efgh");
		tableData.put("Col3", "ijkl");
		tableData.put("Col4", new Integer(100));
		tableData.put("Col5", new Boolean(false));
		tableData.put("Col6", "ef");
		tableData.put("Col7", "gh");
		row2.addTable(tableData);

		// Create table 3
		tableData = new HashMap<String, Object>();
		tableData.put("Col1", "abcd");
		tableData.put("Col2", "efgh");
		tableData.put("Col3", "ijkl");
		tableData.put("Col4", new Integer(100));
		tableData.put("Col5", new Boolean(false));
		tableData.put("Col6", "ef");
		tableData.put("Col7", "gh");
		tableData.put("Col8", new Integer(100));
		row2.addTable(tableData);

		// Create table 4
		tableData = new HashMap<String, Object>();
		tableData.put("Col1", "abcd-100");
		tableData.put("Col2", "efgh");
		tableData.put("Col3", "ijkl");
		tableData.put("Col4", new Integer(100));
		tableData.put("Col5", new Boolean(false));
		tableData.put("Col6", "ef");
		tableData.put("Col7", "gh");
		tableData.put("Col8", new Integer(100));
		row2.addTable(tableData);

		// Create table 5
		tableData = new HashMap<String, Object>();
		tableData.put("Col1", "abcd-100");
		tableData.put("Col2", "efgh");
		tableData.put("Col3", "ijkl");
		tableData.put("Col4", new Integer(101));
		tableData.put("Col5", new Boolean(false));
		tableData.put("Col6", "ef");
		tableData.put("Col7", "gh");
		tableData.put("Col8", "101 ef");
		row2.addTable(tableData);

	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("next")) {
			System.out.println("Next clicked");
			this.pdc.moveNext();
		} else if (e.getActionCommand().equals("last")) {
			System.out.println("Last clicked");
			this.pdc.moveLast();
		} else if (e.getActionCommand().equals("end")) {
			System.out.println("End clicked");
			this.pdc.moveEnd();
		} else {
			System.out.println("Reset clicked");
			this.pdc.moveReset();
		}
		frame.validate();

		// Redraw the component
		pdc.redrawComponent();

	}

	public static void main(String[] args) {
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

}