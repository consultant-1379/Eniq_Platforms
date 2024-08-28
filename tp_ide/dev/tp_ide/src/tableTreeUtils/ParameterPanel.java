package tableTreeUtils;

import javax.swing.JPanel;

import tableTree.TTParameterModel;

/**
 * A class used to create a displayable panel for the data contained in an
 * instance of TTParameterModel or one of its subclasses
 * 
 * @author ejeahei enaland
 */

public class ParameterPanel extends JPanel implements SavableTreeNode {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * the TTParameterModel of this ParameterPanel
     */
    protected TTParameterModel myModel = null;

    /**
     * Set the parameter model
     * 
     * @param aModel
     */
    public void setModel(TTParameterModel aModel) {
	this.myModel = aModel;
    }

    /**
     * Get the parameter model.
     * 
     * @return theModel
     */
    public TTParameterModel getModel() {
	return this.myModel;
    }

    /**
     * Save the changed or an added parameters model in the DB
     */
    public void saveChanges() {
	this.myModel.saveChanges();
    }

    /**
     * Remove the parameters model from the DB
     */
    public void removeFromDB() {
	this.myModel.removeFromDB();

    }
    
    /**
     * Return the parameter model
     * @return the parameter model
     */
    public TTParameterModel getParameterModel(){
	return this.myModel;
    }
}
