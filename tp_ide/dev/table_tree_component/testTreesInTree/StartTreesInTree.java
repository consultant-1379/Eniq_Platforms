package testTreesInTree;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;

/**
 * Starter for the TreesInTree demo.
 * 
 * @author enaland
 * 
 */
public class StartTreesInTree {

    JTree tree = null;
    static JScrollPane container = null;
    static JFrame frame = new JFrame();

    /**
     * The main starter method for the demo
     * @param args
     */
    public static void main(String[] args) {
	StartTreesInTree ST = new StartTreesInTree();
	ST.tree = new TreesTree();
	container = new JScrollPane(ST.tree);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	frame.setPreferredSize(new Dimension(500, 600));
	frame.add(container);
	frame.pack();
	frame.setEnabled(true);
	frame.setVisible(true);
    }

    /**
     * Constructor.
     */
    public StartTreesInTree() {

    }

}
