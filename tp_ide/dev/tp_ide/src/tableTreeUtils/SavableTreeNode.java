package tableTreeUtils;

/**
 * Interface for tree nodes that can be saved in and removed from the DB.
 * 
 * @author enaland ejeahei
 * 
 */
public interface SavableTreeNode {

    /**
     * Save changes to the database
     */
    public void saveChanges();

    /**
     * Remove the node from the database
     */
    public void removeFromDB();
}
