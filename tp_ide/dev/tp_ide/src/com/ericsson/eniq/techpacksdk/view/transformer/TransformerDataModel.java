/**
 * 
 */
package com.ericsson.eniq.techpacksdk.view.transformer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import ssc.rockfactory.RockDBObject;
import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;
import tableTree.TTTableModel;

import com.distocraft.dc5000.repository.dwhrep.Dataformat;
import com.distocraft.dc5000.repository.dwhrep.Measurementtype;
import com.distocraft.dc5000.repository.dwhrep.MeasurementtypeFactory;
import com.distocraft.dc5000.repository.dwhrep.Referencetable;
import com.distocraft.dc5000.repository.dwhrep.ReferencetableFactory;
import com.distocraft.dc5000.repository.dwhrep.Transformation;
import com.distocraft.dc5000.repository.dwhrep.TransformationFactory;
import com.distocraft.dc5000.repository.dwhrep.Transformer;
import com.distocraft.dc5000.repository.dwhrep.TransformerFactory;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.component.ExceptionHandler;
import com.ericsson.eniq.techpacksdk.common.Utils;
import com.ericsson.eniq.techpacksdk.datamodel.DataModel;
import com.ericsson.eniq.techpacksdk.datamodel.TechPackDataModel;
import com.ericsson.eniq.techpacksdk.view.dataFormat.DataformatDataModel;

/**
 * @author eheijun
 * 
 */
public class TransformerDataModel implements DataModel {

  private static final Logger logger = Logger.getLogger(TransformerDataModel.class.getName());

  private final RockFactory rockFactory;

  private Versioning currentVersioning;

  private TechPackDataModel techPackDataModel;

  private Map<String, Vector<TransformerData>> transformers;

  public boolean newDataCreated = false;

  private Map<String, Vector<Transformer>> dataFormats;

  private Vector<Transformation> updatedTransformations;

  private Vector<Transformation> oldTransformations;
  
  private Vector<Transformation> commonForDeletion;
  
  private Vector<Transformation> commonToBeAdded;

  private LinkedHashMap<String, HashMap<Transformation, ArrayList<String>>> mappings;

  private boolean refreshNeeded = false;

  private Map<String, TTTableModel> commonTableModels;

  public TransformerDataModel(RockFactory rockFactory) {
    this.rockFactory = rockFactory;
    mappings = new LinkedHashMap<String, HashMap<Transformation, ArrayList<String>>>();
    commonTableModels = new HashMap<String, TTTableModel>();
  }

  public RockFactory getRockFactory() {
    return rockFactory;
  }

  public Versioning getCurrentVersioning() {
    return techPackDataModel.getVersioning();
  }

  public void setCurrentVersioning(final Versioning versioning) {
    currentVersioning = versioning;
  }

  public List<TransformerData> getTransformerData(String dataformattype) {
    return (List<TransformerData>) transformers.get(dataformattype);
  } 

  /* (non-Javadoc)
   * @see com.ericsson.eniq.techpacksdk.datamodel.DataModel#refresh()
   */
  public void refresh() {
    logger.finest("TransformerDataModel starting refresh from DB");
    int allTransformationsCount = 0;

    try {
      dataFormats = getAllTransformationsDB();
      // Initialise transformers field:
      transformers = new HashMap<String, Vector<TransformerData>>();

      if (currentVersioning != null) {
        final Map<String, Vector<Transformer>> mt = getTransformers(currentVersioning.getVersionid());

        // Go through each data format:
        for (final Iterator<String> it = mt.keySet().iterator(); it.hasNext();) {

          String dataformat = (String) it.next();
          // Get all of the transformers for this data format (data format -> Vector<Transformer>):
          Vector<Transformer> v = (Vector<Transformer>) mt.get(dataformat);

          // For this data format, get each Transformer object and create a TransformerData object.
          // Add to 'transformers' field.
          long highestOrderNo = 0;
          for (final Iterator<Transformer> itt = v.iterator(); itt.hasNext();) {

            final Transformer transformer = itt.next();
            if (!transformer.getType().equals("ALL")) {
              final Vector<Transformation> columns = getTransformations(transformer);
              final TransformerData data = new TransformerData(currentVersioning, transformer, columns, rockFactory);

              if (transformers.containsKey(dataformat)) {
                ((Vector<TransformerData>) transformers.get(dataformat)).add(data);
              } else {
                Vector<TransformerData> vec = new Vector<TransformerData>();
                vec.add(data);
                transformers.put(dataformat, vec);
              }

              // Record the highest order number for this data format (columns is sorted):
              if (!columns.isEmpty()) {
                final long lastOrderNo = columns.lastElement().getOrderno();
                if (lastOrderNo > highestOrderNo) {
                  highestOrderNo = lastOrderNo;                
                }
              }
            }
          }
          
          // Get the ALL transformations if they exist for this data format:
          final boolean foundALLTransformations = getALLTransformations(v, highestOrderNo, dataformat);
          if (foundALLTransformations) {
            allTransformationsCount++;
          }
        }
        
        // If any ALL transformations were found for any of the data formats, refresh the data model again:
        if (allTransformationsCount > 0) {
          refresh();
        }
      }
    } catch (Exception exc) {
      logger.warning("Error refreshing TransformerDataModel: " + exc.toString());      
    }
    logger.info("TransformerDataModel refreshed from DB");
  }

  /**
   * Gets the ALL transformations from repdb. If there are any ALL transformations,
   * copies them into each individual transformer, then deletes the original ALL transformation.
   * 
   * @param tformersForDF           The transformers for the current data format.
   * @param highestOrderNo          The highest order number found for the specific transformations.
   * @param dataformat              The data format (e.g. mdc, ct, ascii etc).
   * @return  gotALLTransformations True if transformations were found for the ALL transformer.
   * @throws SQLException
   * @throws RockException
   */
  protected boolean getALLTransformations(final Vector<Transformer> tformersForDF, long highestOrderNo, final String dataformat)
      throws SQLException, RockException {        
    
    boolean gotALLTransformations = false;
    
    Map<Transformer, ArrayList<Transformation>> newALLTransformations = new HashMap<Transformer, ArrayList<Transformation>>();

    // v has the transformers for the data format:
    for (final Iterator<Transformer> itt = tformersForDF.iterator(); itt.hasNext();) {
      Transformer transformer = itt.next();

      // Check if the transformer is the ALL transformer for the current data format type:
      if (transformer.getType().equalsIgnoreCase("ALL")) {
        final Vector<Transformation> columns = new Vector<Transformation>();
        final TransformerData data = new TransformerData(currentVersioning, transformer, columns, rockFactory);

        if (transformers.containsKey(dataformat)) {
          final Vector<TransformerData> transformerDatas = ((Vector<TransformerData>) transformers.get(dataformat));
          if (!transformerDatas.contains(data)) {
            transformerDatas.add(data);            
          }
        } else {
          Vector<TransformerData> vec = new Vector<TransformerData>();
          vec.add(data);
          transformers.put(dataformat, vec);
        }

        // Get the ALL transformations:
        final Vector<Transformation> ALLTransformations = getTransformations(transformer);

        if (!ALLTransformations.isEmpty()) {
          // record the highest order number for this data format also (columns is sorted):
          final long lastOrderNo = ALLTransformations.lastElement().getOrderno();
          if (lastOrderNo > highestOrderNo) {
            highestOrderNo = lastOrderNo;
          }
          
          highestOrderNo++;
          
          // Go through transformations:
          for (final Iterator<Transformation> sortiter = ALLTransformations.iterator(); sortiter.hasNext();) {
            final Transformation ALLtransformation = sortiter.next();

            // Go through all of the transformers except for all and add the common one to each one:
            for (final Iterator<Transformer> iter2 = tformersForDF.iterator(); iter2.hasNext();) {
              Transformer nextTransformer = iter2.next();          
              
              // Add the ALL transformations
              if (!nextTransformer.getType().equalsIgnoreCase("ALL")) {
                setupALLTransformation(newALLTransformations, ALLtransformation, nextTransformer);                
              }                            
            }
            // Delete the original All transformation:
            ALLtransformation.deleteDB();
          }
          
          writeNewAllTs(newALLTransformations, highestOrderNo);
          
          // A refresh is needed if we have found ALL transformations:
          gotALLTransformations = true;    
        }
      }
    }
    return gotALLTransformations;    
  }

  /**
   * Creates and sets up a new transformation for a specific transformer. The
   * new transformation is a clone of an ALL (common) transformation.
   * 
   * @param newALLTransformations
   *          The total list of common transformations that have to be added to
   *          the specific transformers.
   * @param ALLtransformation
   *          The current common transformation from the common view.
   * @param nextTransformer
   *          The specific transformer that will have the transformation added
   *          to it.
   */
  protected void setupALLTransformation(Map<Transformer, ArrayList<Transformation>> newALLTransformations,
      final Transformation ALLtransformation, Transformer nextTransformer) {
    
    // Set up the new transformation:
    Transformation newTransformation = createTransformation();
    newTransformation = (Transformation) ALLtransformation.clone();
    newTransformation.setTransformerid(nextTransformer.getTransformerid());

    // Add new transformation to overall list:
    ArrayList<Transformation> allTsForTformer = newALLTransformations.get(nextTransformer);
    if (allTsForTformer == null) {
      allTsForTformer = new ArrayList<Transformation>();
      allTsForTformer.add(newTransformation);
      newALLTransformations.put(nextTransformer, allTsForTformer);
    } else {
      allTsForTformer.add(newTransformation);
    }
  }
  
  /**
   * Writes the transformations that were previously in the ALL transformer into
   * the database for the specific transformers.
   * 
   * @param newALLTransformations
   *          The transformations for the specific transformers that used to be
   *          in the ALL transformer.
   * @param highestOrderNo
   *          The highest order no we have so far.
   * @throws SQLException
   * @throws RockException
   */
  protected void writeNewAllTs(Map<Transformer, ArrayList<Transformation>> newALLTransformations, long highestOrderNo)
      throws SQLException, RockException {

    for (Transformer key : newALLTransformations.keySet()) {
      ArrayList<Transformation> newOnes = newALLTransformations.get(key);

      // Save the specific transformations:
      final Vector<Transformation> specificTransformations = getTransformations(key);

      for (Transformation newTransformation : newOnes) {
        // Write the new transformation:
        newTransformation.setOrderno(highestOrderNo);
        newTransformation.insertDB();
        highestOrderNo++;
      }

      for (Transformation tformation : specificTransformations) {
        tformation.setOrderno(highestOrderNo);
        tformation.saveToDB(); // this is not new but has order no updated, so
                               // should be just updated in database.
        highestOrderNo++;
      }
    }
  }

  /**
   * Protected creator method to create a new Transformation.
   * @return newTransformation
   */
  protected Transformation createTransformation() {
    Transformation newTransformation = new Transformation(rockFactory);
    return newTransformation;
  }

  public void save() {

  }

  private final static Comparator<Transformation> TRANSFORMATIONCOMPARATOR = new Comparator<Transformation>() {

    public int compare(final Transformation d1, final Transformation d2) {

      final Long i1 = Utils.replaceNull(d1.getOrderno()).longValue();
      final Long i2 = Utils.replaceNull(d2.getOrderno()).longValue();
      return i1.compareTo(i2);
    }
  };

  /**
   * Returns a list of transformations for transformer
   * 
   * @param transformer
   *          the parent of the columns
   * @param allTransformers 
   * @return results a list of Transformations
   */
  public Vector<Transformation> getTransformations(final Transformer transformer) {
    final Vector<Transformation> results = new Vector<Transformation>();
     
      final Transformation whereTransformation = new Transformation(rockFactory);
      whereTransformation.setTransformerid(transformer.getTransformerid());
      try {
        final TransformationFactory transformationFactory = new TransformationFactory(rockFactory, whereTransformation,
            true);
        final Vector<Transformation> sortTransformation = transformationFactory.get();
        Collections.sort(sortTransformation, TRANSFORMATIONCOMPARATOR);
        
        for (final Iterator<Transformation> sortiter = sortTransformation.iterator(); sortiter.hasNext();) {
          final Transformation transformationTemp = sortiter.next();
          results.add(transformationTemp);          
        }                              
      } catch (SQLException e) {
        ExceptionHandler.instance().handle(e);
        e.printStackTrace();
      } catch (RockException e) {
        ExceptionHandler.instance().handle(e);
        e.printStackTrace();
      }

    return results;
  }

  /**
   * Returns a list of transformers by versionId
   * 
   * @return results a list of Transformers
   */
  public Map<String, Vector<Transformer>> getTransformers(final String versionId) {

    final Map<String, Vector<Transformer>> results = new HashMap<String, Vector<Transformer>>();

    final Transformer whereTransformer = new Transformer(rockFactory);
    whereTransformer.setVersionid(versionId);
    try {
      final TransformerFactory transformerFactory = new TransformerFactory(rockFactory, whereTransformer, true);
      final Vector<Transformer> targetTransformers = transformerFactory.get();

      for (final Iterator<Transformer> iter = targetTransformers.iterator(); iter.hasNext();) {
        final Transformer mt = iter.next();
        String dataformattype = mt.getTransformerid().substring(mt.getTransformerid().lastIndexOf(":") + 1);
        if (results.containsKey(dataformattype)) {
          ((Vector<Transformer>) results.get(dataformattype)).add(mt);
        } else {
          Vector<Transformer> v = new Vector<Transformer>();
          v.add(mt);
          results.put(dataformattype, v);
        }
      }
    } catch (SQLException e) {
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    } catch (RockException e) {
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    }

    return results;
  }

  public boolean delObj(final RockDBObject obj) {
    return false;
  }

  public boolean modObj(final RockDBObject obj) {
    return false;
  }

  public boolean modObj(final RockDBObject[] obj) {
    return false;
  }

  public boolean newObj(final RockDBObject obj) {
    return false;
  }

  public boolean validateDel(final RockDBObject obj) {
    return false;
  }

  public boolean validateMod(final RockDBObject obj) {
    return false;
  }

  public boolean validateNew(final RockDBObject obj) {
    return false;
  }

  public boolean updated(final DataModel dataModel) throws Exception {
    if (dataModel instanceof TechPackDataModel) {
      techPackDataModel = (TechPackDataModel) dataModel;
      this.setCurrentVersioning(techPackDataModel.getVersioning());
      refresh();
      return true;
    } else if (dataModel instanceof DataformatDataModel) {

      ((DataformatDataModel) dataModel).isDataformatsRenamed();

      // create the new transformations
      updateTransformations(((DataformatDataModel) dataModel).isDataformatsRenamed(), ((DataformatDataModel) dataModel)
          .getRenamedFrom(), ((DataformatDataModel) dataModel).getRenamedTo());

      // refresh the datamodel
      refresh();

      newDataCreated = true;
      ((DataformatDataModel) dataModel).setDataformatsRenamed(false);
      return true;
    }
    return false;
  }

  public Map getAllDataFormats() {
    return dataFormats;
  }

  public Vector<Object> getDataFormats(String dataformattype) {
    return (Vector) dataFormats.get(dataformattype);
  }

  /**
   * 
   * 
   */
  public void updateTransformations(boolean renamed, String renamedFrom, String renamedTo) throws Exception {

    Iterator dIter = getAllDataFormatsDB().keySet().iterator();

    while (dIter.hasNext()) {

      String dataformattype = (String) dIter.next();

      Map<String, Vector<Transformer>> newTransformers = new HashMap<String, Vector<Transformer>>();

      Measurementtype mt = new Measurementtype(rockFactory);
      mt.setVersionid(currentVersioning.getVersionid());
      mt.setDataformatsupport(new Integer(1));
      MeasurementtypeFactory mtF = new MeasurementtypeFactory(rockFactory, mt);
      Iterator iter = mtF.get().iterator();

      // SPESIFIC types
      while (iter.hasNext()) {

        Measurementtype mtype = (Measurementtype) iter.next();

        String transformerid = mtype.getTypeid() + ":" + dataformattype;

        Transformer tr = new Transformer(rockFactory);
        tr.setTransformerid(transformerid);
        tr.setVersionid(mtype.getVersionid());
        tr.setDescription("");
        tr.setType("SPECIFIC");

        if (newTransformers.containsKey(dataformattype)) {
          ((Vector) newTransformers.get(dataformattype)).add(tr);
        } else {
          Vector<Transformer> v = new Vector<Transformer>();
          v.add(tr);
          newTransformers.put(dataformattype, v);
        }
      }
      // ALL type
      {
        String transformerid = currentVersioning.getVersionid() + ":ALL:" + dataformattype;

        Transformer tr = new Transformer(rockFactory);
        tr.setTransformerid(transformerid);
        tr.setVersionid(currentVersioning.getVersionid());
        tr.setDescription("");
        tr.setType("ALL");

        if (newTransformers.containsKey(dataformattype)) {
          ((Vector) newTransformers.get(dataformattype)).add(tr);
        } else {
          Vector<Transformer> v = new Vector<Transformer>();
          v.add(tr);
          newTransformers.put(dataformattype, v);
        }
      }

      // Reference

      Referencetable rt = new Referencetable(rockFactory);
      rt.setVersionid(currentVersioning.getVersionid());
      rt.setDataformatsupport(new Integer(1));
      ReferencetableFactory rtF = new ReferencetableFactory(rockFactory, rt);
      Iterator rtFIter = rtF.get().iterator();

      while (rtFIter.hasNext()) {

        Referencetable rtype = (Referencetable) rtFIter.next();

        String transformerid = rtype.getTypeid() + ":" + dataformattype;

        Transformer tr = new Transformer(rockFactory);
        tr.setTransformerid(transformerid);
        tr.setVersionid(rtype.getVersionid());
        tr.setDescription("");
        tr.setType("SPECIFIC");

        if (newTransformers.containsKey(dataformattype)) {
          ((Vector) newTransformers.get(dataformattype)).add(tr);
        } else {
          Vector<Transformer> v = new Vector<Transformer>();
          v.add(tr);
          newTransformers.put(dataformattype, v);
        }
      }

      //

      if (transformers.containsKey(dataformattype)) {

        Vector oldTransformers = (Vector) transformers.get(dataformattype);

        Iterator oldDfsi = oldTransformers.iterator();
        while (oldDfsi.hasNext()) {

          TransformerData oldTransformerData = (TransformerData) oldDfsi.next();

          Transformer oldTransformer = oldTransformerData.getTransformer();

          logger.finest("Investigating Transformation: " + oldTransformer.getTransformerid());

          // does the new list contain oldone (dataformat)
          Transformer matchDf = null;
          if (newTransformers.get(dataformattype) != null) {
            Iterator newDfi = ((Vector) newTransformers.get(dataformattype)).iterator();
            while (newDfi.hasNext()) {

              Transformer newTransformer = (Transformer) newDfi.next();

              if (newTransformer.getTransformerid().equals(oldTransformer.getTransformerid())) {
                matchDf = newTransformer;
                break;
              }
            }
          }

          if (matchDf != null) {
            // newList contains oldOne
            ((Vector) newTransformers.get(dataformattype)).remove(matchDf);
            oldDfsi.remove();

          } else {
            // newList does NOT contain oldOne

            // remove transformations or copy the to the new transformer
            Transformation trn = new Transformation(rockFactory);
            trn.setTransformerid(oldTransformer.getTransformerid());
            TransformationFactory trnF = new TransformationFactory(rockFactory, trn);
            Iterator trnFIter = trnF.get().iterator();

            while (trnFIter.hasNext()) {
              Transformation trns = (Transformation) trnFIter.next();
              trns.deleteDB();
            }

            oldDfsi.remove();
            oldTransformer.deleteDB();
          }
        }
      }

      // Transformers left in new list will be added to DB
      if (newTransformers.get(dataformattype) != null) {
        Iterator newDfi = ((Vector) newTransformers.get(dataformattype)).iterator();
        while (newDfi.hasNext()) {

          Transformer addMe = (Transformer) newDfi.next();
          addMe.insertDB();
        }
      }
      // remove handled transformer
      dataFormats.remove(dataformattype);

    }

    // remove extra transformers and transformations

    dIter = dataFormats.keySet().iterator();

    while (dIter.hasNext()) {

      String olddataformattype = (String) dIter.next();

      // remove all extra Transformers & Transformations
      Vector oldTransformers = (Vector) transformers.get(olddataformattype);

      Iterator oldDfsi = oldTransformers.iterator();
      while (oldDfsi.hasNext()) {

        TransformerData oldTransformerData = (TransformerData) oldDfsi.next();
        Transformer oldTransformer = oldTransformerData.getTransformer();

        // remove transformations if any
        Transformation trn = new Transformation(rockFactory);
        trn.setTransformerid(oldTransformer.getTransformerid());
        TransformationFactory trnF = new TransformationFactory(rockFactory, trn);
        Iterator trnFIter = trnF.get().iterator();

        while (trnFIter.hasNext()) {

          Transformation trns = (Transformation) trnFIter.next();
          if (renamed && renamedFrom.equalsIgnoreCase(olddataformattype)) {
            Transformation newTrn = (Transformation) trns.clone();
            String newtid = trns.getTransformerid().substring(0, trns.getTransformerid().lastIndexOf(":") + 1)
                + renamedTo;
            newTrn.setTransformerid(newtid);
            newTrn.insertDB();
          }
          trns.deleteDB();
        }

        oldDfsi.remove();
        oldTransformer.deleteDB();
      }
    }
  }

  /**
   * Gets all the data formats from the database. The data format types are
   * changed to lower case, so that they match the values used in the
   * transformer tables in the DB.
   * 
   * @return
   */
  public Map getAllDataFormatsDB() {

    Map<String, Vector<Dataformat>> theSets = new HashMap<String, Vector<Dataformat>>();

    try {

      Dataformat df = new Dataformat(rockFactory);
      df.setVersionid(currentVersioning.getVersionid());
      com.distocraft.dc5000.repository.dwhrep.DataformatFactory mcF = new com.distocraft.dc5000.repository.dwhrep.DataformatFactory(
          rockFactory, df);

      Vector<Dataformat> dataformats = mcF.get();

      for (Iterator<Dataformat> iter = dataformats.iterator(); iter.hasNext();) {

        Dataformat mSet = iter.next();

        // Get the data format type and convert it to lower case.
        String dataFormatType = mSet.getDataformattype();
        if (dataFormatType != null)
          dataFormatType = dataFormatType.toLowerCase();

        if (theSets.containsKey(dataFormatType)) {

          ((Vector) theSets.get(dataFormatType)).add(mSet);

        } else {

          Vector<Dataformat> v = new Vector<Dataformat>();
          v.add(mSet);

          theSets.put(dataFormatType, v);
        }
      }

    } catch (SQLException e) {
      ExceptionHandler.instance().handle(e);
      logger.warning(e.getMessage());
    } catch (RockException e) {
      ExceptionHandler.instance().handle(e);
      logger.warning(e.getMessage());
    }

    return theSets;
  }

  public Map getAllTransformationsDB() {

    Map<String, Vector<Transformer>> theSets = new HashMap<String, Vector<Transformer>>();

    try {

      if (currentVersioning == null) {
        return theSets;
      }

      Transformer tr = new Transformer(rockFactory);
      tr.setVersionid(this.currentVersioning.getVersionid());
      TransformerFactory trF = new TransformerFactory(rockFactory, tr);

      Vector<Transformer> transformeres = trF.get();

      for (Iterator<Transformer> iter = transformeres.iterator(); iter.hasNext();) {

        Transformer transf = iter.next();

        String dataformattype = transf.getTransformerid().substring(transf.getTransformerid().lastIndexOf(":") + 1);        

        if (theSets.containsKey(dataformattype)) {

          ((Vector) theSets.get(dataformattype)).add(transf);

        } else {

          Vector<Transformer> v = new Vector<Transformer>();
          v.add(transf);

          theSets.put(dataformattype, v);
        }
      }

    } catch (SQLException e) {
      ExceptionHandler.instance().handle(e);
      logger.warning(e.getMessage());
    } catch (RockException e) {
      ExceptionHandler.instance().handle(e);
      logger.warning(e.getMessage());
    }

    return theSets;
  }

  class TransformationOrderCmp implements Comparator<Transformation> {

    // Comparator interface requires defining compare method.
    public int compare(Transformation t1, Transformation r2) {

      return t1.getOrderno().compareTo(t1.getOrderno());

    }
  }

  private Vector<Transformation> remveDuplicates(Vector<Transformation> transformations) {

    // remove duplicates
    for (int i = 0; i < transformations.size(); i++) {

      for (int ii = i + 1; ii < transformations.size(); ii++) {
        if (Utils.replaceNull(transformations.get(i).getType()).equals(
            Utils.replaceNull(transformations.get(ii).getType()))
            && Utils.replaceNull(transformations.get(i).getSource()).equals(
                Utils.replaceNull(transformations.get(ii).getSource()))
            && Utils.replaceNull(transformations.get(i).getTarget()).equals(
                Utils.replaceNull(transformations.get(ii).getTarget()))
            && Utils.replaceNull(transformations.get(i).getDescription()).equals(
                Utils.replaceNull(transformations.get(ii).getDescription()))
            && Utils.replaceNull(transformations.get(i).getConfig()).equals(
                Utils.replaceNull(transformations.get(ii).getConfig()))) {
          transformations.remove(i);
          i--;
          break;
        }
      }
    }
    return transformations;
  }

  private boolean containsTransformation(Transformation alltransformation, Vector<Transformation> transformations) {

    Iterator<Transformation> transformationsI = transformations.iterator();
    boolean found = false;
    // transformations
    while (transformationsI.hasNext()) {
      Transformation transformation = (Transformation) transformationsI.next();

      if ((Utils.replaceNull(transformation.getSource()).equals(Utils.replaceNull(alltransformation.getSource()))
          && Utils.replaceNull(transformation.getType()).equals(Utils.replaceNull(alltransformation.getType()))
          && Utils.replaceNull(transformation.getTarget()).equals(Utils.replaceNull(alltransformation.getTarget()))
          && Utils.replaceNull(transformation.getTarget()).equals(Utils.replaceNull(alltransformation.getTarget()))
          && Utils.replaceNull(transformation.getDescription()).equals(
              Utils.replaceNull(alltransformation.getDescription())) && Utils.replaceNull(transformation.getConfig())
          .equals(Utils.replaceNull(alltransformation.getConfig())))) {
        found = true;
        break;
      }
    }

    return found;
  }

  /**
   * Migrates the transformer data to match the latest database structure.
   * 
   * @param versionid
   * @param fromEniqLevel
   * @throws Exception
   */
  public void migrate(String versionid, String fromEniqLevel) throws Exception {

    // Check the from version.
    if (!fromEniqLevel.equals("1.0")) {
      // No need for migrate.
      logger.log(Level.FINEST, "No need to migrate transformers.");
      return;
    } else {
      Map dataformats = getTransformers(versionid);

      Iterator dataformatsI = dataformats.keySet().iterator();
      // dataformats
      while (dataformatsI.hasNext()) {
        String dataformattype = (String) dataformatsI.next();

        Vector<Transformation> allTransformations = new Vector<Transformation>();

        Vector transformers = (Vector) dataformats.get(dataformattype);

        Iterator transformersI = transformers.iterator();

        // transformers
        while (transformersI.hasNext()) {

          Transformer transformer = (Transformer) transformersI.next();
          Vector<Transformation> transformations = this.getTransformations(transformer);

          transformations = remveDuplicates(transformations);
          Collections.sort(transformations, new TransformationOrderCmp());

          // in the first loop we take the transformations as a base
          // (alltransformations)
          if (allTransformations.isEmpty()) {

            allTransformations.addAll(transformations);

          } else {

            // after the first loop we check if the the transformations exist in
            // the alltransformations
            Iterator allTransformationsI = allTransformations.iterator();

            // int i = 0;

            // all transformations
            while (allTransformationsI.hasNext()) {
              Transformation alltransformation = (Transformation) allTransformationsI.next();

              if (containsTransformation(alltransformation, transformations) == false) {
                // if alltransformation does NOT exists in transformations we
                // remove it from alltransformations
                allTransformationsI.remove();
              }
            }
          }
        }

        // all common transformations are find out for this dataformat..
        if (!allTransformations.isEmpty()) {

          // Create a new ALL-transformation
          Transformer tr = new Transformer(rockFactory);
          String transformerid = versionid + ":ALL:" + dataformattype;
          tr.setTransformerid(transformerid);
          tr.setVersionid(versionid);
          tr.setDescription("Autogenerated by TPIDE in migration");
          tr.setType("ALL");

          tr.saveToDB();

          // add all the all transformations to the newly created transformer

          Iterator allTransformationsI = allTransformations.iterator();
          // alltransformations
          while (allTransformationsI.hasNext()) {
            Transformation alltransformation = (Transformation) allTransformationsI.next();

            Transformation newt = (Transformation) alltransformation.clone();
            newt.setTransformerid(transformerid);

            newt.saveToDB();

          }

          // remove all the ALL transformation from the spesific transformations
          transformersI = transformers.iterator();
          // transformers
          while (transformersI.hasNext()) {
            Transformer transformer = (Transformer) transformersI.next();
            Vector<Transformation> transformations = this.getTransformations(transformer);
            // transformations = remveDuplicates(transformations);
            Collections.sort(transformations, new TransformationOrderCmp());

            Iterator<Transformation> deletetransformationsI = transformations.iterator();
            Vector<Transformation> duplicates = new Vector<Transformation>();
            // transformations
            while (deletetransformationsI.hasNext()) {
              Transformation deletedtransformation = (Transformation) deletetransformationsI.next();

              if (containsTransformation(deletedtransformation, allTransformations)) {
                // if deletedtransformation does exists in alltransformation we
                // remove it from DB
                deletedtransformation.deleteDB();
              } else {
                // if deletedtransformation does NOT exists in alltransformation
                // we remove duplicates...
                if (containsTransformation(deletedtransformation, duplicates)) {
                  deletedtransformation.deleteDB();
                } else {
                  duplicates.add(deletedtransformation);
                }
              }
            }
          }
        }
      }
    }
  }
  
  public void addOldTransformations(Transformation tr) {
    oldTransformations.add(tr);
  }

  public Vector<Transformation> getOldTransformations() {
    return oldTransformations;
  }

  public void setOldTransformations(Vector<Transformation> updatedTransformations) {
    this.oldTransformations = updatedTransformations;
  }

  public void addUpdatedTransformations(Transformation tr) {
    updatedTransformations.add(tr);
  }

  public Vector<Transformation> getUpdatedTransformations() {
    return updatedTransformations;
  }

  public void setUpdatedTransformations(Vector<Transformation> updatedTransformations) {
    this.updatedTransformations = updatedTransformations;
  }

  public void setCommonForDeletion(Vector<Transformation> commonForDeletion) {
    this.commonForDeletion = commonForDeletion;
  }

  public Vector<Transformation> getCommonForDeletion() {
    if (commonForDeletion == null) {
      commonForDeletion = new Vector<Transformation>();
    }    
    return commonForDeletion;
  }

  public void setCommonToBeAdded(Vector<Transformation> commonToBeAdded) {
    this.commonToBeAdded = commonToBeAdded;
  }

  public Vector<Transformation> getCommonToBeAdded() {
    if (commonToBeAdded == null) {
      commonToBeAdded = new Vector<Transformation>();
    }    
    return commonToBeAdded;
  }

  public HashMap<Transformation, ArrayList<String>> getMappings(final String dataFormat) {
    if (mappings.get(dataFormat) == null) {
      mappings.put(dataFormat, new HashMap<Transformation, ArrayList<String>>());
    } 
    return mappings.get(dataFormat);
  }

  public void setMappings(LinkedHashMap<String, HashMap<Transformation, ArrayList<String>>> mappings) {
    this.mappings = mappings;
  }

  public static Logger getLogger() {
    return logger;
  }
  
  public boolean isRefreshNeeded() {
    return refreshNeeded;
  }
  
  public void setRefreshNeeded(boolean refreshNeeded) {
    this.refreshNeeded = refreshNeeded;
  }
  
  public Map<String, Vector<TransformerData>> getTransformers() {
    return transformers;
  }
  
  public void setTransformers(Map<String, Vector<TransformerData>> transformers) {
    this.transformers = transformers;
  }

  public Map<String, TTTableModel> getCommonTableModels() {
    return commonTableModels;
  }
  
  public void setCommonTableModels(Map<String, TTTableModel> tableModels) {
    commonTableModels = tableModels;
  }

}
