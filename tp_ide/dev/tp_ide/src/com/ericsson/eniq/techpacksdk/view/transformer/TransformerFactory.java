package com.ericsson.eniq.techpacksdk.view.transformer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import org.jdesktop.application.SingleFrameApplication;

import ssc.rockfactory.RockFactory;
import tableTree.TTEditor;
import tableTree.TTParameterModel;
import tableTree.TTTableModel;
import tableTree.TreeDataFactory;
import tableTreeUtils.TableInformation;
import tableTreeUtils.TreeMainNode;

import com.distocraft.dc5000.repository.dwhrep.Transformation;
import com.distocraft.dc5000.repository.dwhrep.Transformer;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;

/**
 * Concrete factory class for Transformers.
 * 
 * @author eheijun
 * 
 */
public class TransformerFactory extends TreeDataFactory {
  
  private static final Logger logger = Logger.getLogger(TransformerFactory.class.getName());

  /**
	 * The rock factory is the interface towards the DB.
	 */
	private RockFactory rockFactory = null;

	private TransformerDataModel transformerDataModel;
	
	private SingleFrameApplication application;
	
	Map<String, Map<Transformation, Transformer>> individualTransformers = new HashMap<String, Map<Transformation, Transformer>>();

	/**
	 * The constructor initiates the renderer and DB interface for this specific
	 * type of dialogue.
	 */
	private TransformerFactory(RockFactory rockFactory, boolean editable) {
		this.rockFactory = rockFactory;
		this.editable = editable;
		treeCellRenderer = new TransformerRenderer();
		createTableInfoData(); // Collect info about the different table types
		// used
		this.setMaximumTreeNodeNameLength(Transformer
				.getTransformeridColumnSize());
	}

	public TransformerFactory(SingleFrameApplication application, TransformerDataModel transformerDataModel,
      boolean editable, TransformerTreeModel transformerTreeModel, DataModelController dataModelController) {
    this(transformerDataModel.getRockFactory(), editable);
    this.application = application;
    this.transformerDataModel = transformerDataModel;
  }

	private Versioning versioning;

	private String dataformatType;

  /**
   * Dummy ALL Transformer ID for common transformations. 
   */
  private static final String ALL_TRANSFORMER_ID = "ALL";
  
  /** Transformation utilities */
  TransformationUtils tUtils = new TransformationUtils();

	/**
	 * Create a tree cell editor for the specific type of dialogue
	 * (Transformer).
	 * 
	 * @param theTree
	 * @return treeCellEditor.
	 */
	public TTEditor getTreeCellEditor(final JTree theTree) {
		if (treeCellEditor == null) {
			treeCellEditor = new TransformerEditor(theTree);
		}
		return treeCellEditor;
	}

	private class cmpTransformers implements Comparator<TransformerData> {

		public int compare(TransformerData t1, TransformerData t2) {
			return t1.parseTreeName().compareTo(t2.parseTreeName());
		}
	}

  /**
   * Create the tree model for the tree and initiate it with data from the
   * database.
   * 
   * @return treeModel
   */
  public TreeModel createAndGetModel() {

    // Create the root node
    final DefaultMutableTreeNode root = createTreeNodeObject("root");
    final DefaultTreeModel theModel = new DefaultTreeModel(root);

    // Clear the mappings before we start. These are the common transformation mappings:
    // transformation -> ArrayList of transformer ids.
    transformerDataModel.getMappings(dataformatType).clear();
    final List<TransformerData> transformerDataList = transformerDataModel.getTransformerData(dataformatType);
    Collections.sort(transformerDataList, new cmpTransformers());

    Vector<Object> commonTransformations = new Vector<Object>();
    try {
      // Get the common transformations (present in all transformers):
      commonTransformations = (Vector<Object>) createCommonTransformations(transformerDataList);
    } catch (Exception exc) {
      logger.severe("Failed to get common transformations: " + exc.toString());
    }

      if (transformerDataList != null) {
        // Loop through the transformers, get their data and add them to
        // the model
        for (final Iterator<TransformerData> iter = transformerDataList.iterator(); iter.hasNext();) {
          try {
            final TreeMainNode mainNode = createTransformer(iter.next(), commonTransformations, transformerDataList);
            root.add(mainNode);            
          } catch (Exception exc) {
            logger.severe("Failed to get create transformer: " + exc.toString());            
        }
      }
    }

    return theModel;
  }

	/**
	 * Method for collecting info of the different table types
	 */
	protected void createTableInfoData() {
	  final TableInformation transTableInformation = TransformationTableModel.createTableTypeInfo();
		tableInformations.addElement(transTableInformation);
		
		final TableInformation commonTransTableInformation = CommonTransformationTableModel.createTableTypeInfo();
		tableInformations.addElement(commonTransTableInformation);
	}

	/**
	 * Helper method for creating the transformer node and all its children
	 * @param allTransformations 
	 * 
	 * @param transformer
	 * @return
	 * @throws Exception 
	 */
  protected TreeMainNode createTransformer(final TransformerData transformerData, Vector<Object> allTransformations,
      final List<TransformerData> allTransformerData) throws Exception {

    final Transformer transformer = transformerData.getTransformer();
    final Vector<Object> myColumns = transformerData.getTransformations();

    // Create the child node data for all three nodes: parameters, keys and
    // counters
    final TTParameterModel parameterModel;

    TTTableModel transformationModel = null;
    if (transformer.getTransformerid().equalsIgnoreCase(getAllTransformerID())) {
      parameterModel = createParameterModel(transformerData, transformer, "Common");
      transformationModel = createCommonTableModel(allTransformations, allTransformerData);
      // Save this model:
      transformerDataModel.getCommonTableModels().put(dataformatType, transformationModel);
    } else {
      parameterModel = createParameterModel(transformerData, transformer, transformerData.parseTreeName());
      transformationModel = createSpecificTableModel(allTransformerData, transformer);
    }

    // Set the observer/observable relationships
    parameterModel.addObserver(transformationModel);

    // Create the main node
    final TreeMainNode mainNode = createTreeMainNode(parameterModel);

    // Create the child nodes
    final DefaultMutableTreeNode parameterNode = createParameterNode(parameterModel);
    DefaultMutableTreeNode transformationNode = null;

    if (transformer.getTransformerid().equalsIgnoreCase(getAllTransformerID())) {
      transformationNode = createTableNode(allTransformations, transformationModel, false);
    } else {
      transformationNode = createTableNode(myColumns, transformationModel);
    }

    // Connect the nodes
    mainNode.add(parameterNode);
    mainNode.add(transformationNode);

    // Create deletion order
    final Vector<DefaultMutableTreeNode> nodeOrder = new Vector<DefaultMutableTreeNode>();
    nodeOrder.addElement(transformationNode);
    nodeOrder.addElement(parameterNode);

    setDeletionOrder(mainNode, nodeOrder);

    return mainNode;
  }
  
  /**
   * Gets the common transformations.
   * @param transformerList
   * @return
   * @throws Exception
   */
  protected List<Object> createCommonTransformations(final List<TransformerData> transformerList) throws Exception {
    logger.fine("createCommonTransformations(), Getting common transformations");    
    
    List<Object> commonTransformations = new Vector<Object>();
        
    // Get an overall list of the transformations:
    List<Object> allTransformations = getAllTransformations(transformerList);
    
    // checkedTransformations is a list of transformations that have already been checked:
    List<Transformation> checkedTransformations = new ArrayList<Transformation>();
    checkedTransformations.clear();
        
    for (final Iterator<Object> iter = allTransformations.iterator(); iter.hasNext();) {
      Transformation transformation = (Transformation) iter.next();

      // Only check if we haven't done this transformation already:
      if (tUtils.findNumOccurrences(checkedTransformations, transformation) <= 0) {
        List<Transformer> transformerMatches = new ArrayList<Transformer>();

        for (final Iterator<TransformerData> tdIter = transformerList.iterator(); tdIter.hasNext();) {
          TransformerData td = (TransformerData) tdIter.next();
          
          // Check if the transformer has the transformation:
          if (tUtils.findNumOccurrencesWithObjects(td.getTransformations(), transformation) > 0) {
            transformerMatches.add(td.getTransformer());            
          }
        }

        Transformation commonTransformation = createCommonTransformation(transformation, transformerMatches);        
        commonTransformations.add(commonTransformation);        
        transformerMatches.clear();
        checkedTransformations.add(transformation);
      }
    }
    
    logger.fine("createCommonTransformations(), Finished getting common transformations");
    return commonTransformations;    
  }

  /**
   * Creates a single transformation for the "All" view.
   * @param totalTransformersSize
   * @param transformation
   * @param transformerMatches
   * @return newTransformation A new transformation object.
   */
  protected Transformation createCommonTransformation(final Transformation transformation, final List<Transformer> transformerMatches) {
    // Get the total number of transformers (not including the common transformer, so subtract 1):  
    final int totalTransformersSize = transformerDataModel.getTransformerData(dataformatType).size() - 1;

    Transformation newTransformation = new Transformation(transformerDataModel.getRockFactory());
    newTransformation.setSource(transformation.getSource());
    newTransformation.setConfig(transformation.getConfig());
    newTransformation.setType(transformation.getType());
    newTransformation.setTarget(transformation.getTarget());        
    newTransformation.setValidateData(false);
    if (transformerMatches.size() == totalTransformersSize) {
      newTransformation.setTransformerid(ALL_TRANSFORMER_ID);
    } else {
      final StringBuffer transformers = new StringBuffer();
      for (Transformer t : transformerMatches) {
        transformers.append(t.getTransformerid());
        transformers.append(",");
      }
      newTransformation.setTransformerid(transformers.toString());
    }
    return newTransformation;
  }

  /**
   * Gets a list of all of the transformations.
   * @param transformerData
   * @return allTransformations
   */
  protected List<Object> getAllTransformations(final List<TransformerData> transformerData) {
    List<Object> allTransformations = new Vector<Object>();
    
    final String allTransformerID = getAllTransformerID();
    
    // Go through each of the transformers, and add the transformations to allTransformations:
    for (final Iterator<TransformerData> iter = transformerData.iterator(); iter.hasNext();) {
      TransformerData td = (TransformerData) iter.next();
      
      if (!td.getTransformer().getTransformerid().equalsIgnoreCase(allTransformerID)) {
        Vector<Object> transformations = td.getTransformations();
        allTransformations.addAll(transformations);
      }
    }
    return allTransformations;
  }
  
  /**
   * Gets the id of the ALL transformer.
   * @return The id.
   */
  private String getAllTransformerID() {
    return versioning.getVersionid() + ":ALL:" + dataformatType;
  }  
  	
	/**
	 * Overridden version of the corresponding TreeDataFactory method. Used for
	 * creating new empty nodes when the user inserts nodes in a tree.
	 */
	public TreeMainNode createEmptyNode() {
		// Create new type and table objects
		final TransformerData emptyTransformer = new TransformerData(
				versioning, rockFactory);
		final Transformer transformer = emptyTransformer.getTransformer();
		final Vector<Object> myColumns = emptyTransformer.getTransformations();
		
		final List<TransformerData> transformerDataList = transformerDataModel
    .getTransformerData(dataformatType);
		Collections.sort(transformerDataList, new cmpTransformers());		    

		// Create the empty data sets for the child nodes
		final TTParameterModel compositeData = new TransformerParameterModel(
				emptyTransformer.parseTreeName(), emptyTransformer
						.getVersioning(), transformer, rockFactory, editable);
		final TTTableModel transformationModel = new TransformationTableModel(
				transformerDataModel, rockFactory, tableInformations, editable,
				transformer, dataformatType);

		// Set the observer/observable relationships
		compositeData.addObserver(transformationModel);

		final TreeMainNode mainNode = createTreeMainNode(compositeData);
		final DefaultMutableTreeNode parameterNode = createParameterNode(compositeData);
		final DefaultMutableTreeNode transformationNode = createTableNode(
				myColumns, transformationModel);

		// Connect all the nodes
		mainNode.add(parameterNode);
		mainNode.add(transformationNode);

		// Create deletion order
		final Vector<DefaultMutableTreeNode> nodeOrder = new Vector<DefaultMutableTreeNode>();
		nodeOrder.addElement(transformationNode);
		nodeOrder.addElement(parameterNode);

		setDeletionOrder(mainNode, nodeOrder);

		return mainNode;
	}

	/**
	 * Set deletion order, e.g. tables have to be deleted before the
	 * parameterpanel due to references in the data. Nodes are deleted in the
	 * order that they are in the vector sent to
	 * mainNode.setDeletionOrder(vector). Nodes that aren't included in the
	 * vector will be deleted last
	 */
	private void setDeletionOrder(final TreeMainNode parentNode,
			final Vector<DefaultMutableTreeNode> nodes) {
		parentNode.setDeletionOrder(nodes);
	}

	public Versioning getVersioning() {
		return versioning;
	}

	public void setVersioning(final Versioning versioning) {
		this.versioning = versioning;
	}

	public void setDataformatType(String dataformatType) {
		this.dataformatType = dataformatType;
	}

	/**
	 * Duplicates an existing tree node data to the new tree node
	 */
	@Override
	public void duplicateExistingNode(TreeMainNode existingNode,
			TreeMainNode newNode) {
		// Duplicate is not available for transformers.
	}

	/**
	 * @param editable
	 */
	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	/**
	 * Overridden version of the method. Does not allow add/remove/duplicate of
	 * three main nodes.
	 * 
	 * @return false
	 */
	@Override
	public boolean isTreeNodeAddRemoveAllowed() {
		return false;
	}
	
  /**
   * Gets the logger variable. Allows logging to be disabled for unit tests.
   * @return
   */
  public static Logger getLogger() {
    return logger;
  }
  
  /** Protected creator methods: **/
  
  /**
   * Protected creator method to create a DefaultMutableTreeNode.
   * @param treeNodeName
   * @return
   */
  protected DefaultMutableTreeNode createTreeNodeObject(final String treeNodeName) {
    final DefaultMutableTreeNode object = new DefaultMutableTreeNode(treeNodeName);
    return object;
  }
  
  /**
   * Protected creator method to create a TreeMainNode.
   * @param parameterModel
   * @return
   */
  protected TreeMainNode createTreeMainNode(final TTParameterModel parameterModel) {
    final TreeMainNode mainNode = new TreeMainNode(parameterModel);
    return mainNode;
  }
  
  /**
   * Protected creator method to create a specific TransformationTableModel.~
   * @param allTransformerData
   * @param transformer
   * @return
   */
  protected TTTableModel createSpecificTableModel(final List<TransformerData> allTransformerData,
      final Transformer transformer) {
    TTTableModel transformationModel;
    transformationModel = new TransformationTableModel(
          transformerDataModel, rockFactory, tableInformations, editable,
          transformer, dataformatType);
    return transformationModel;
  }

  /**
   * Protected creator method to create a CommonTransformationTableModel.
   * @param allTransformations
   * @param allTransformerData
   * @param transformer
   * @return TTTableModel
   */
  protected TTTableModel createCommonTableModel(Vector<Object> allTransformations,
      final List<TransformerData> allTransformerData) {
    
    final String allTransformerID = getAllTransformerID();
    TTTableModel transformationModel;
    transformationModel = new CommonTransformationTableModel(
          application, transformerDataModel, rockFactory, tableInformations, editable,
          allTransformerData, allTransformations, allTransformerID, dataformatType);
    return transformationModel;
  }

  /**
   * Protected creator method to create a TransformerParameterModel object.
   * @param transformerData
   * @param transformer
   * @param name
   * @return TTParameterModel
   */
  protected TTParameterModel createParameterModel(final TransformerData transformerData, final Transformer transformer, final String name) {
    final TTParameterModel parameterModel;
    parameterModel = new TransformerParameterModel("Common", transformerData.getVersioning(),
        transformer, rockFactory, editable);
    return parameterModel;
  }

}
