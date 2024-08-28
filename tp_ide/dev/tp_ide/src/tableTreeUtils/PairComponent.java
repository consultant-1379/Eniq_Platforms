package tableTreeUtils;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Helper class for displaying pairs of labels and input components e.g. in a
 * parameter pane.
 * 
 * @author ejeahei enaland
 */
public class PairComponent extends JPanel {

    private static final long serialVersionUID = 1L;
    
    /**
     * The title for the pair component.   
     */
    private JLabel myTitle = null;
    
    /**
     * The inner component of the pair component
     */
    private Component myComponent = null;
    
    /**
     * Constructor that creates a pair component using a handed GridBagLayout. A
     * pair component is a component consisting of a label and an interactive
     * component.
     * 
     * @param title
     * @param comp
     * @param GBL
     * @param GBC
     */
    public PairComponent(String title, Component comp, GridBagLayout GBL,
	    GridBagConstraints GBC) {
	myTitle = new JLabel(title);
	this.add(myTitle);
	myComponent = comp;
	this.add(myComponent);
	GBL.setConstraints(this, GBC);
    }

    /**
     * Constructor that creates a default pair component. A pair component is a
     * component consisting of a label and an interactive component.
     * 
     * @param title
     * @param comp
     */
    public PairComponent(String title, Component comp) {
	myTitle = new JLabel(title);
	this.add(myTitle);
	myComponent = comp;
	this.add(myComponent);
    }

    /**
     * Gets the title for this pair component
     * 
     * @return the title
     */
    public String getTitle(){
	return myTitle.getText();
    }
    
    
    /**
     * Gets the inner component for this pair component
     * 
     * @return the component
     */
    public Component getComponent(){
	return myComponent;
    }
    
}
