package ttctests;

import tableTree.TTTableModel;
import tableTreeUtils.TableContainer;

public class stubbedTableContainer extends TableContainer {

    public stubbedTableContainer(TTTableModel inModel) {
	super(inModel);
    }

    /**
     * 
     */
    private static final long serialVersionUID = -7592152537532192937L;

    /*
     * (non-Javadoc)
     * 
     * @see tableTreeUtils.TableContainer#tuneSize()
     */
    @Override
    public void tuneSize() {
	// do nothing
    }
}
