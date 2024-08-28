package com.ericsson.eniq.techpacksdk.view.transformer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JTree;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.jdesktop.application.SingleFrameApplication;

import ssc.rockfactory.RockFactory;
import tableTree.TableTreeComponent;

import com.distocraft.dc5000.repository.dwhrep.Transformation;
import com.distocraft.dc5000.repository.dwhrep.Transformer;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.component.ExceptionHandler;
import com.ericsson.eniq.component.TreeState;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;

public class TransformerTreeModel extends DefaultTreeModel {

  private static final long serialVersionUID = 1L;

  private static final Logger logger = Logger.getLogger(TransformerTreeModel.class.getName());

  protected static Map tableTreeComponentMap = null;

  private boolean editable;

  private JTree theTree;

  private TransformerTreeModelListener listener;

  private Versioning versioning;

  private DocumentListener dl;

  public DataModelController dataModelController;
  
  private SingleFrameApplication application;
  
  private TransformationUtils tUtils;
  
  /**
   * Constructor only for test.
   */
  protected TransformerTreeModel(final SingleFrameApplication application, final Versioning versioning, 
      final DataModelController dataModelController, final boolean editable, final JTree theTree, 
      final TransformerTreeModelListener listener, final DocumentListener dl, final boolean test) {
    super(null, true);
    this.dl = dl;
    this.dataModelController = dataModelController;
    this.application = application;
    this.editable = editable;
    this.theTree = theTree;
    this.listener = listener;
    this.versioning = versioning;
    tUtils = createTransformationUtils();
  }

  /**
   * Constructor
   * @param application
   * @param versioning
   * @param dataModelController
   * @param editable
   * @param theTree
   * @param listener
   * @param dl
   */
  public TransformerTreeModel(SingleFrameApplication application, Versioning versioning, DataModelController dataModelController, boolean editable,
      JTree theTree, TransformerTreeModelListener listener, DocumentListener dl) {
    super(null, true);
    tUtils = createTransformationUtils();
    this.dl = dl;
    this.dataModelController = dataModelController;
    this.application = application;
    this.editable = editable;
    this.theTree = theTree;
    this.listener = listener;
    theTree.addMouseListener(listener);
    this.versioning = versioning;

    dataModelController.getTransformerDataModel().setCurrentVersioning(versioning);
    dataModelController.getTransformerDataModel().refresh();

    this.setRoot(refresh());

  }
  
  /**
   * Creates a new TransformationUtils object.
   * Can be overridden for test.
   * @return new TransformationUtils().
   */
  protected TransformationUtils createTransformationUtils() {
    return new TransformationUtils();
  }

  public void update() {
    try {
      if (dataModelController.getTransformerDataModel().newDataCreated) {
        this.setRoot(refresh());
        dataModelController.getTransformerDataModel().newDataCreated = false;
      }
    } catch (Exception e) {
      try {
        this.setRoot(refresh());
        dataModelController.getTransformerDataModel().newDataCreated = false;
      } catch (Exception ex) {
        logger.warning("Unable to refresh Transformation tree " + ex.getMessage());
      }
    }
  }

  /**
   * 
   * 
   * 
   * @return
   */
  private DefaultMutableTreeNode refresh() {

    tableTreeComponentMap = new HashMap();

    DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");

    Vector sort = new Vector();
    sort.addAll(dataModelController.getTransformerDataModel().getAllDataFormats().keySet());
    Collections.sort(sort);
    Iterator iter = sort.iterator();

    while (iter.hasNext()) {

      String dataformatType = (String) iter.next();

      List<Transformer> transformers = (List<Transformer>) dataModelController.getTransformerDataModel()
          .getAllDataFormats().get(dataformatType);

      TransformerFactory dff = new TransformerFactory(application, dataModelController.getTransformerDataModel(), editable, this, dataModelController);
      dff.setVersioning(versioning);
      dff.setDataformatType(dataformatType);

      TableTreeComponent TTComp;

      TTComp = new TableTreeComponent(dff, listener);
      TTComp.addDocumentListener(dl);
      tableTreeComponentMap.put(dataformatType, TTComp);

      DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(dataformatType);
      treeNode.add(new DefaultMutableTreeNode(TTComp));

      root.add(treeNode);

    }

    return root;
  }

  public void addDocumentListener(DocumentListener dl) {

    Iterator iter = tableTreeComponentMap.keySet().iterator();
    while (iter.hasNext()) {
      String dataformatType = (String) iter.next();
      TableTreeComponent ttc = (TableTreeComponent) tableTreeComponentMap.get(dataformatType);
      ttc.addDocumentListener(dl);
    }
  }

  public void save() {
    logger.log(Level.INFO, "save");
    Iterator iter = tableTreeComponentMap.keySet().iterator();
    while (iter.hasNext()) {

      try {

        dataModelController.getRockFactory().getConnection().setAutoCommit(false);

        String dataformatType = (String) iter.next();
        TableTreeComponent ttc = (TableTreeComponent) tableTreeComponentMap.get(dataformatType);

        // check that the vector exists and its empty
        if (dataModelController.getTransformerDataModel().getUpdatedTransformations() == null) {
          dataModelController.getTransformerDataModel().setUpdatedTransformations(new Vector<Transformation>());
        } else {
          dataModelController.getTransformerDataModel().getUpdatedTransformations().clear();
        }

        ttc.saveChanges();

        if (dataModelController.getTransformerDataModel().getOldTransformations() != null) {
          // remove the old (left over from re-numbering) transformations
          Iterator<Transformation> otrFI = dataModelController.getTransformerDataModel().getOldTransformations().iterator();

          while (otrFI.hasNext()) {

            Transformation transf = (Transformation) otrFI.next();
            transf.deleteDB();            
          }
          dataModelController.getTransformerDataModel().getOldTransformations().clear();
        }        

        // remove all old transformations
        Iterator<Transformation> trFI = dataModelController.getTransformerDataModel().getUpdatedTransformations().iterator();

        while (trFI.hasNext()) {
          Transformation transf = (Transformation) trFI.next();
          transf.deleteDB();          
        }       
        
        // Add new transformations and remove old ones if they have been enabled or disabled in the
        // mappings in the common view:
        addAndRemoveFromMappings(dataformatType);
        
        // add new transformations (including old ones).
        trFI = dataModelController.getTransformerDataModel().getUpdatedTransformations().iterator();
        long orderNo = 0;
        while (trFI.hasNext()) {
          Transformation transf = (Transformation) trFI.next();
          transf.setOrderno(orderNo);
          
          if (!transf.isNew()) {
            transf.insertDB();
            logger.fine("Saved transformer (inserted new): " +  transf.getSource() + ", " + transf.getTarget() + ", " + transf.getTransformerid());            
          } else {            
            transf.saveDB();
            logger.fine("Saved transformer (saved): " +  transf.getSource() + ", " + transf.getTarget() + ", " + transf.getTransformerid());
          }
          orderNo++;
        }

        dataModelController.getRockFactory().getConnection().commit();

        dataModelController.rockObjectsModified(dataModelController.getTransformerDataModel());

      } catch (Exception e) {
        try {
          dataModelController.getRockFactory().getConnection().rollback();
        } catch (Exception ex) {
          ExceptionHandler.instance().handle(ex);
          ex.printStackTrace();
        }
        ExceptionHandler.instance().handle(e);
        e.printStackTrace();
      } finally {
        try {
          dataModelController.getRockFactory().getConnection().setAutoCommit(true);
        } catch (Exception ex) {
          ExceptionHandler.instance().handle(ex);
          ex.printStackTrace();
        }
      }
    }

    dataModelController.getTransformerDataModel().refresh();
    this.update();
  }
  
  /**
   * Add new transformations and remove old ones if they have been enabled or
   * disabled in the mappings in the common view.
   */
  protected void addAndRemoveFromMappings(final String dataformatType) {
    // Get the transformer data model:
    final TransformerDataModel tdm = dataModelController.getTransformerDataModel();
    final HashMap<Transformation, ArrayList<String>> mappings = tdm.getMappings(dataformatType);
    final Vector<Transformation> updatedTransformations = tdm.getUpdatedTransformations();

    try {
      final List<Transformation> transformationsToDelete = getTransformationsToDelete(mappings);
      final List<Transformation> transformationsToAdd = getTransformationsToAdd(mappings, dataformatType);

      // Go through all of the transformations. If not present in mappings,
      // remove it from updated. If present in mappings, add it to updated.
      // Delete the ones marked for deletion:
      final Iterator<Transformation> deleteIter = transformationsToDelete.iterator();
      while (deleteIter.hasNext()) {
        final Transformation deleteTransformation = deleteIter.next();

        for (int index = 0; index < updatedTransformations.size(); index++) {
          Transformation updatedTransformation = updatedTransformations.get(index);

          // Check if the transformation is equal to the "updated" Transformation 
          // and if the transformer id is the same:
          final boolean areEqual = tUtils.checkIsTransformationEqual(updatedTransformation, deleteTransformation);
            
          if (areEqual 
              && updatedTransformation.getTransformerid().equalsIgnoreCase(deleteTransformation.getTransformerid())) {
            updatedTransformations.remove(index);
          }
        }
      }
      
      if (transformationsToAdd.size() > 0) {
        addOrdered(transformationsToAdd, dataformatType);        
      }
    } catch (Exception exc) {
      logger.warning("Error adding and removing transformations from mappings: " + exc.toString());
    }
  }
  
  /**
   * Adds the transformations selected in the "common" section to the individual transformers.
   * Orders the transformations being added according to the order in the common section.
   * 
   * @param transformationsToAdd  List of transformations to add.
   * @param dataformatType        The data format type (e.g. mdc or ascii).
   */
  protected void addOrdered(final List<Transformation> transformationsToAdd, final String dataformatType) {
    final TransformerDataModel tdm = dataModelController.getTransformerDataModel();
    final Vector<Transformation> updatedTransformations = tdm.getUpdatedTransformations();
       
    final List<TransformerData> transformers = tdm.getTransformerData(dataformatType);
        
    // Go through transformers, add transformations in each one:
    for (TransformerData tdata : transformers) {
      try {
        final String transformerID = tdata.getTransformer().getTransformerid();
        
        // Get common transformations for the transformer:
        List<Transformation> commonTsForTransformer = getCommonForTransformer(tdm, dataformatType, transformerID);
        
        // Get the transformations for the current transformer: 
        final List<Transformation> tsForThisTransformer = getTransformationsForTransformer(transformationsToAdd, transformerID);
        
        // Sort transformations according to the order of the common transformations:
        List<Transformation> sortedForThisTFormer = sortListWithAnother(commonTsForTransformer, tsForThisTransformer);
        
        // Add sorted transformations:
        addSortedTransformations(updatedTransformations, commonTsForTransformer, sortedForThisTFormer);        
      } catch (Exception exc) {
        logger.severe("Error adding common transformations to transformer: " + exc.toString());
      }
    }
  }
  
  /**
   * Gets the common transformations for a transformer.
   * 
   * @param tdm             TransformerDataModel
   * @param dataformatType  The data format type (e.g. mdc or ascii).
   * @param transformerID   The ID of the transformer/measurement type.
   * @return commonForTransformer The list of common transformations for this transformer.
   */
  protected List<Transformation> getCommonForTransformer(final TransformerDataModel tdm, final String dataformatType,
      final String transformerID) {

    // Get common transformations for this transformation:
    List<Transformation> commonForTransformer = new ArrayList<Transformation>();

    // Get mappings. Mappings are transformation -> list of transformer ids as strings:
    final HashMap<Transformation, ArrayList<String>> mappings = tdm.getMappings(dataformatType);

    if (mappings != null) {
      // Get the list of common transformations from the common section's table model:
      Vector<Object> commonTransformations = tdm.getCommonTableModels().get(dataformatType).getData();      
      for (Object commonT : commonTransformations) {
        // Check if transformer id is the same:       
        ArrayList<String> tformerIDs = tUtils.lookUpTransformationInMap((Transformation)commonT, mappings);
        
        if (tformerIDs != null) {
          if (tformerIDs.contains(transformerID)) {
            commonForTransformer.add((Transformation)commonT);
          }          
        }
      }
    }
    return commonForTransformer;
  }
  
  /**
   * Gets the new transformations being added for a particular transformer.
   * @param transformationsToAdd    The entire list of transformations being added. 
   * @param transformerID           The transformer ID that we are interested in.
   * @return tsForThisTransformer   List of transformations being added for a particular transformer.
   */
  protected List<Transformation> getTransformationsForTransformer(final List<Transformation> transformationsToAdd,
      final String transformerID) {
    final List<Transformation> tsForThisTransformer = new ArrayList<Transformation>();       
    for (Transformation tToAdd : transformationsToAdd) {
      if (tToAdd.getTransformerid().equalsIgnoreCase(transformerID)) {
        tsForThisTransformer.add(tToAdd);
      }
    }
    return tsForThisTransformer;
  }
  
  /**
   * Sorts a list of transformations according to another list.
   * @param listWithCorrectOrder      The list with the correct order. 
   * @param unsortedList              The unsorted list.
   * @return sortedForThisTFormer     New list, sorted according to the list with the correct order.
   */
  private List<Transformation> sortListWithAnother(List<Transformation> listWithCorrectOrder,
      final List<Transformation> unsortedList) {    
    // Sort new tsForThisTransformer according to the common for the transformer:
    List<Transformation> sortedForThisTFormer = new ArrayList<Transformation>();      
    for (Transformation commont : listWithCorrectOrder) {
      for (Transformation unsortedt : unsortedList) {
        if (tUtils.checkIsTransformationEqual(unsortedt, commont)) {
          sortedForThisTFormer.add(unsortedt);
          break;
        }
      }
    }
    return sortedForThisTFormer;
  }

  /**
   * Add the sorted transformations to the list of updated transformations.
   * @param updatedTransformations    
   * @param commonTsForTransformer
   * @param newForThisTFormer (sorted)
   * @return
   */
  protected void addSortedTransformations(final Vector<Transformation> updatedTransformations,
      List<Transformation> commonTsForTransformer, List<Transformation> newForThisTFormer) {
           
    boolean found = false;
    for (Transformation newT : newForThisTFormer) {
      try { 
        final int index = tUtils.searchForTransformation(newT, commonTsForTransformer, false);
                
        if (index == 0) {
          // Add before first common one found in the updated list:
          for (Transformation commonT : commonTsForTransformer) {
            Transformation searchT = (Transformation) commonT.clone();
            searchT.setTransformerid(newT.getTransformerid());
            int position = tUtils.searchForTransformation(searchT, updatedTransformations, true);
            if (position >= 0) {
              // found, insert before the position:
              updatedTransformations.insertElementAt(newT, position);
              found = true;
              break;
            }
          }      
          if (!found) {
            // No existing common transformations, add at end of list:
            updatedTransformations.add(newT);
          }
          // reset the found variable:
          found = false;
        } else if (index > 0) {        
          // Get previous t from common list, add after that:          
          Transformation searchT = (Transformation) commonTsForTransformer.get(index-1).clone();         
          searchT.setTransformerid(newT.getTransformerid());                             
          int previousPosition = tUtils.searchForTransformation(searchT, updatedTransformations, true);
          updatedTransformations.add(previousPosition+1, newT);                               
        } else {
          updatedTransformations.add(newT);
        }
      } catch (Exception exc) {
        logger.warning("Error inserting common transformation in transformer: " + exc.toString());
        // Add at the end of the list by default:
        updatedTransformations.add(newT);
      }
    }
  }
  
  /**
   * Gets the transformations to be added. These are transformations that are
   * enabled in the mappings and don't exist in the individual transformers.
   * 
   * @param mappings
   * @return
   */
  protected List<Transformation> getTransformationsToAdd(final Map<Transformation, ArrayList<String>> mappings, final String dataformatType) {
    final ArrayList<Transformation> transformationsToAdd = new ArrayList<Transformation>();
    final ArrayList<String> foundTransformers = new ArrayList<String>();
    final TransformerDataModel tdm = dataModelController.getTransformerDataModel();
    final Vector<Transformation> updatedTransformations = tdm.getUpdatedTransformations();
    
    // Get the common transformations from the common table model:
    Vector<Object> commonTs = tdm.getCommonTableModels().get(dataformatType).getData();
        
    for (Object tformation : commonTs) {
      try {
        Transformation commonTransformation = (Transformation)tformation;        
        List<String> mappedTransformers = tUtils.lookUpTransformationInMap(commonTransformation, 
            (HashMap<Transformation, ArrayList<String>>)mappings);

        if (mappedTransformers != null) {
          Iterator<Transformation> trFI = updatedTransformations.iterator();
          while (trFI.hasNext()) {
            Transformation individual = (Transformation) trFI.next();

            // does a transformation exist with this transformer id?   
            final boolean areEqual = tUtils.checkIsTransformationEqual(individual, commonTransformation);
            
            if (areEqual && mappedTransformers.contains(individual.getTransformerid())) { // is mapped?
                // found the transformation in this transformer:
                foundTransformers.add(individual.getTransformerid()); // add to list               
            }
          }

          // Get unique mapped transformers:          
          ArrayList<String> uniqueTransformers = new ArrayList<String>();
          // add elements to all, including duplicates
          HashSet<String> hs = new HashSet<String>();
          hs.addAll(mappedTransformers);
          uniqueTransformers.addAll(hs);
          
          
          for (String mappedID : uniqueTransformers) {
            int numberFoundInTransformers = Collections.frequency(foundTransformers, mappedID);
            int numberOfMappingsForTransformer = Collections.frequency(mappedTransformers, mappedID);

            int numberToAdd = numberOfMappingsForTransformer - numberFoundInTransformers;
            if (numberToAdd > 0) {
              
              for (int i=0; i<numberToAdd; i++) {
                final Transformation newTransformation = createTransformation(tdm.getRockFactory());
                // Give the transformation the missing transformer ID:
                newTransformation.setTransformerid(mappedID);
                newTransformation.setConfig(commonTransformation.getConfig());
                newTransformation.setSource(commonTransformation.getSource());
                newTransformation.setTarget(commonTransformation.getTarget());
                newTransformation.setType(commonTransformation.getType());
                newTransformation.setDescription(commonTransformation.getDescription());
                transformationsToAdd.add(newTransformation);                
              }

            }
          }
          foundTransformers.clear();
        }
      } catch (Exception exc) {
        logger.severe("Failed to get new transformations to be added: " + exc.toString());
      }
    }
    return transformationsToAdd;
  }

  /**
   * Protected creator method to create a new Transformation.
   * @param rockFactory A reference to the RockFactory. 
   * @return newTransformation
   */
  protected Transformation createTransformation(final RockFactory rockFactory) {
    return new Transformation(rockFactory);
  }

  /**
   * Gets the transformations to be deleted.
   * These are transformations that are disabled in the mappings.
   * @param mappings
   * @return transformationsToDelete ArrayList of transformations to delete.
   */
  protected List<Transformation> getTransformationsToDelete(Map<Transformation, ArrayList<String>> mappings) {
    final List<Transformation> transformationsToDelete = new ArrayList<Transformation>();
    final TransformerDataModel tdm = dataModelController.getTransformerDataModel();    
    
    // Search the updated transformations:
    final Iterator<Transformation> updatedTsIterator = tdm.getUpdatedTransformations().iterator();
    
    while (updatedTsIterator.hasNext()) {
      final Transformation transformation = (Transformation) updatedTsIterator.next();
      List<Transformation> test = new ArrayList<Transformation>(mappings.keySet());
      List<String> tformerMappings = null;
      int position = tUtils.searchForTransformation(transformation, test, false);
      if (position >= 0) {
        tformerMappings = mappings.get(test.get(position));
      }
      
        // Is there a mapping for this transformation?:
        if (tformerMappings == null || !tformerMappings.contains(transformation.getTransformerid())) {
          // If not, add it to the delete list:
          transformationsToDelete.add(transformation);              
        }
    }
    return transformationsToDelete;
  }
  
  public void discard() {
    logger.log(Level.INFO, "discard");

    if (dataModelController.getTransformerDataModel().getOldTransformations() != null) {
      dataModelController.getTransformerDataModel().getOldTransformations().clear();
    }

    if (dataModelController.getTransformerDataModel().getUpdatedTransformations() != null) {
      dataModelController.getTransformerDataModel().getUpdatedTransformations().clear();
    }

    Map<String, List<?>> m = new HashMap<String, List<?>>();

    Iterator iter = tableTreeComponentMap.keySet().iterator();
    while (iter.hasNext()) {

      String dataformatType = (String) iter.next();
      TableTreeComponent ttc = (TableTreeComponent) tableTreeComponentMap.get(dataformatType);
      List list = TreeState.saveExpansionState((JTree) ttc);
      m.put(dataformatType, list);
      ttc.discardChanges();
      // TreeState.loadExpansionState((JTree)ttc, list);
    }

    try {
      dataModelController.getTransformerDataModel().refresh();
      this.setRoot(refresh());

    } catch (Exception e) {
      try {
        dataModelController.getTransformerDataModel().refresh();
        this.setRoot(refresh());
      } catch (Exception ex) {
        logger.warning(ex.getMessage());
      }
    }

    Iterator iter2 = m.keySet().iterator();
    while (iter2.hasNext()) {
      String t = (String) iter2.next();
      TableTreeComponent ttc = (TableTreeComponent) tableTreeComponentMap.get(t);
      TreeState.loadExpansionState((JTree) ttc, (List) m.get(t));
    }

  } // end discard
  
  /**
   * Checks the OrderNo of a Transformation is unique for Techpack. 
   * @return
   */
  public Vector<String> validateData() {
    Vector<String> result = new Vector<String>();
    if(true) return result; // TODO: REMOVE
  // Get all Transformations from Model
  Map<String, Vector<Transformer>> transformers = dataModelController.getTransformerDataModel().getTransformers(versioning.getVersionid());
  Iterator roots = tableTreeComponentMap.keySet().iterator();
  while (roots.hasNext()) { // Loop through top level, E.g. mdc
    String root = (String) roots.next();
    HashMap<Long, ArrayList> duplicateCheck = new HashMap<Long, ArrayList>();
    ArrayList<Long> duplicateOrderNoList = new ArrayList<Long>();
    TableTreeComponent ttc = (TableTreeComponent) tableTreeComponentMap.get(root);
    Vector<Vector<Object>> transformerList = ttc.getAllData();
    Iterator measurementTypes = transformerList.iterator();
      while (measurementTypes.hasNext()) { // Loop through MeasurementTypes
        Vector<Object> measurementTypeNode = (Vector<Object>)measurementTypes.next();
        String transName = getTransformerName(measurementTypeNode);
        TransformationTableModel measurementTypeTransformerList = (TransformationTableModel) getTransformationTableModel(measurementTypeNode);
        Iterator transformationRows = measurementTypeTransformerList.getData().iterator();
          while (transformationRows.hasNext()) { // Loop through Transformation list for this MeasurementType.
            ArrayList<String> measeTypeList = new ArrayList<String>();
            Transformation transformation = (Transformation)transformationRows.next();
            Long key = transformation.getOrderno();
            if(duplicateCheck.containsKey(key)) {
              // Record duplicates
              if(!duplicateOrderNoList.contains(key)) {
                duplicateOrderNoList.add(transformation.getOrderno());
              }
              measeTypeList = duplicateCheck.get(key);
            }
            if(!measeTypeList.contains(transName)) {
              measeTypeList.add(transName);
            }
            duplicateCheck.put(key, measeTypeList);
          } // Loop through Transformations
      } // Loop through MeasurementTypes
    // Write error message
    if(duplicateOrderNoList.size() > 0) {
      for ( Long key : duplicateOrderNoList) {
        ArrayList value = duplicateCheck.get(key);
        result.add(key+" is not a unique Order No for parser " + root + " within nodes: "+value);
      }
    }  
  } // Loop through top level
  return result;
  } // end validateData
  
  /**
   * Helper method
   * Uses TransformerParameterModel to get the node name to display in the error message.
   * @param node
   * @return
   */
  private String getTransformerName(Vector<Object> node) {
    for (Object object : node) {
    if(object instanceof TransformerParameterModel) {
      TransformerParameterModel paramaterModel = (TransformerParameterModel) node.get(0);
      return paramaterModel.getMainNodeName();
    }
    }
    return "";
  } // end getTransformerName
  
  /**
   * Helper method
   * @param node
   * @return
   */
  private TransformationTableModel getTransformationTableModel(Vector<Object> node) {
    for (Object object : node) {
    if(object instanceof TransformationTableModel) {
      return (TransformationTableModel) object;
    }
    }
    return null;
  } // end getTransformationTableModel


  private class DocEvent implements DocumentEvent {

    public ElementChange getChange(Element elem) {
      return null;
    }

    public Document getDocument() {
      return null;
    }

    public int getLength() {
      return 0;
    }

    public int getOffset() {
      return 0;
    }

    public EventType getType() {
      return DocumentEvent.EventType.CHANGE;
    }

  }

  

}
