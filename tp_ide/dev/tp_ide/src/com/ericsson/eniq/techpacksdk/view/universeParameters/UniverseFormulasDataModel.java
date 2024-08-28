package com.ericsson.eniq.techpacksdk.view.universeParameters;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import ssc.rockfactory.RockDBObject;
import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.repository.dwhrep.Universecomputedobject;
import com.distocraft.dc5000.repository.dwhrep.UniversecomputedobjectFactory;
import com.distocraft.dc5000.repository.dwhrep.Universeformulas;
import com.distocraft.dc5000.repository.dwhrep.UniverseformulasFactory;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.distocraft.dc5000.repository.dwhrep.VersioningFactory;

public class UniverseFormulasDataModel extends GenericUniverseDataModel<RockDBObject> {

  private static final long serialVersionUID = 1L;

  public static final Logger logger = Logger.getLogger(UniverseJoinView.class.getName());

  public static final String[] OBJECT_TYPE_OPTIONS = { "character", "number", "date" };

  public static final String[] QUALIFICATION_OPTIONS = { "dimension", "measure", "detail" };

  public static final String[] AGGREGATION_OPTIONS = { "none", "sum", "avg", "max", "min" }; // IDE #676 - Aggregation Formula NONE should be added, 24/07/09 ,ejohabd
  
  private UniverseFormulasTableModel ufTableModel = null;

  public UniverseFormulasDataModel(RockFactory etlRock) {
    super(etlRock);
  }

  @Override
  public GenericUniverseTableModel createTableModel(Versioning v) throws RockException, SQLException {

    Vector<Universeformulas> universeFormulasVector = null;
    if (v != null) {
      Universeformulas uF = new Universeformulas(etlRock);
      uF.setVersionid(v.getVersionid());
      UniverseformulasFactory uFFactory = new UniverseformulasFactory(etlRock, uF, "order by ordernro");
      universeFormulasVector = uFFactory.get();
    }
    setUniverseFormulasTableModel(new UniverseFormulasTableModel(universeFormulasVector, this.etlRock));
    return getUniverseFormulasTableModel();
  }

  @Override
  public void save() throws Exception {
    // Get versionid, and throw an exception if it is null
    String vrVersionid = versioning.getVersionid();
    if (vrVersionid == null || vrVersionid.equals("")) {
      throw new Exception("Trying to save without versionID");
    }
    // VersionId is valid.
    //
    // First remove all formulas with this versionId. Also all related computed
    // objects will have the "select" value emptied for the formulas which will
    // be "really" deleted (i.e. deleted and not reinserted to the database).

    // List<Universecomputedobject> computedobjects = new
    // ArrayList<Universecomputedobject>();

    try {
      Universeformulas universeFormulas = new Universeformulas(etlRock);
      universeFormulas.setVersionid(vrVersionid);

      UniverseformulasFactory delUF = new UniverseformulasFactory(etlRock, universeFormulas);
      Iterator<Universeformulas> deletionIterator = delUF.get().iterator();

      while (deletionIterator.hasNext()) {
        Universeformulas deletionItem = (Universeformulas) deletionIterator.next();

        // If the current universe formula is not in the visible table, i.e. it
        // is going to be really deleted, then iterate through all universe
        // computed objects and empty the matching Select-value.
        GenericUniverseTableModel tableModel = getTableModel();
        boolean visible = false;
        for (Object obj : tableModel.data) {
          Universeformulas uFormulas = (Universeformulas) obj;
          if (uFormulas.dbEquals(deletionItem)) {
            visible = true;
            break;
          }
        }

        if (getBaseFormulas(etlRock, deletionItem).size() == 0) {

          // If the formula is not found in the table, then empty the select
          // value of the universe computed object.
          if (!visible) {
            Universecomputedobject universeComputedObject = new Universecomputedobject(etlRock);
            universeComputedObject.setVersionid(vrVersionid);
            UniversecomputedobjectFactory ocof = new UniversecomputedobjectFactory(etlRock, universeComputedObject);
            Iterator<Universecomputedobject> ucoIter = ocof.get().iterator();
            while (ucoIter.hasNext()) {
              Universecomputedobject currentCompObj = ucoIter.next();
              if (currentCompObj.getObjselect().equals(deletionItem.getName())) {
                // Empty the select value
                currentCompObj.setObjselect("");
                currentCompObj.saveDB();
              }
            }
          }
        }

        // Delete the universe formulas
        deletionItem.deleteDB();
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }

    // Then save the formulas in the table into the database
    try {
      GenericUniverseTableModel tableModel = getTableModel();
      for (Object o : tableModel.data) {
        Universeformulas u = (Universeformulas) o;

        // Set techpack and object types
        u.setTechpack_type(this.versioning.getTechpack_type());

        if (!vrVersionid.equals(u.getVersionid()))
          throw new Exception("Trying to save for wrong versionId");

        u.insertDB();
      }
    } catch (Exception e) {
      System.out.println(e);
      throw e;
    }
  }

  @Override
  public Vector<String> validateData() {
    Vector<String> errorStrings = new Vector<String>();
    GenericUniverseTableModel tm = getTableModel();

    // Check if there is data to validate.
    if (tm == null || tm.data == null || tm.data.isEmpty()) {
      return errorStrings;
    }

    // Iterate through the table data.
    List<Universeformulas> duplicatesCheckList = new ArrayList<Universeformulas>();
    for (Object o : tm.data) {
      Universeformulas u1 = (Universeformulas) o;

      // Replace null values with empty values.
      u1.removeNulls();

      // Verify that mandatory parameters are not empty
      if (u1.getName().equals("") || u1.getFormula().equals("") || u1.getQualification().equals("")) {
        errorStrings.add("No empty values allowed for Name, Formula or Qualification.");
      }

      // Verify that there are no duplicate primary keys. A duplicate value is
      // detected if all the primary key column values are the same.
      for (Universeformulas u2 : duplicatesCheckList) {
        if (u1.dbEquals(u2)) {
          errorStrings.add("Duplicate entries found with Name: " + u1.getName() + ".");
        }
      }
      duplicatesCheckList.add(u1);
    }
    return errorStrings;
  }

  /**
   * Confirmation of deleting related objects
   * 
   * @return
   */
  public String isOkToSave() {

    // Get all the universe formulas from DB with the versionId
    List<Universeformulas> list = new ArrayList<Universeformulas>();
    try {
      Universeformulas uf = new Universeformulas(etlRock);
      uf.setVersionid(versioning.getVersionid());
      UniverseformulasFactory uff = new UniverseformulasFactory(etlRock, uf);
      list = uff.get();
    } catch (Exception e) {
      return "Error in validation.";
    }

    // Then get the ones on the screen in order to check which are
    // "meaningfully" deleted / primary keys changed
    UniverseFormulasTableModel tableModel = getUniverseFormulasTableModel();
    for (Universeformulas univf : tableModel.data) {

      for (Universeformulas check : list) {
        if (check.dbEquals(univf)) {
          list.remove(check);
          break;
        }
      }

    }

    // now the list contains the classes, which relationship to children will be
    // broken
    return UniverseFormulasDataModel.validateDelete(etlRock, list);
  }

  static private List<Universeformulas> getBaseFormulas(RockFactory rock, Universeformulas uf) throws Exception {
    // check also if there are formulas from the base techpack with the same
    // name

    List<Universeformulas> compList = new ArrayList<Universeformulas>();

    Versioning v = new Versioning(rock);
    v.setVersionid(uf.getVersionid());
    VersioningFactory vF = new VersioningFactory(rock, v);

    if (vF != null && vF.size() > 0) {

      Versioning ver = vF.getElementAt(0);
      boolean isBase = ver.getTechpack_type().equalsIgnoreCase("base");
      if (!isBase) {

        Universeformulas ufo = new Universeformulas(rock);
        ufo.setName(uf.getName());
        ufo.setVersionid(ver.getBasedefinition());
        UniverseformulasFactory ufoF = new UniverseformulasFactory(rock, ufo);
        compList.addAll(ufoF.get());

      }
    }

    return compList;
  }

  static private boolean isBase(RockFactory rock, Universeformulas uf) throws Exception {

    boolean isBase = false;
    Versioning v = new Versioning(rock);
    v.setVersionid(uf.getVersionid());
    VersioningFactory vF = new VersioningFactory(rock, v);

    if (vF != null && vF.size() > 0) {

      Versioning ver = vF.getElementAt(0);
      isBase = ver.getTechpack_type().equalsIgnoreCase("base");
    }

    return isBase;
  }

  /**
   * Validation method for removing a universe formulas table row. The method
   * will search for matching "select" value in universe computed objects using
   * the name of the formula.
   * 
   * @param rock
   *          the rock factory
   * @param uflist
   *          a list of universe formulas objects
   * @return
   */
  static String validateDelete(RockFactory rock, List<Universeformulas> uflist) {

    List<Universecomputedobject> compList = new ArrayList<Universecomputedobject>();
    List<Universeformulas> compList2 = new ArrayList<Universeformulas>();

    boolean isBase = false;
    for (Universeformulas uf : uflist) {
      try {

        Universecomputedobject uco = new Universecomputedobject(rock);
        uco.setVersionid(uf.getVersionid());
        uco.setObjselect(uf.getName());
        UniversecomputedobjectFactory CF = new UniversecomputedobjectFactory(rock, uco);
        compList.addAll(CF.get());

        isBase = isBase(rock, uf);
        compList2 = getBaseFormulas(rock, uf);

      } catch (Exception ex) {
        return "Error in validation.";
      }
    }

    if (compList.size() > 0 && compList2.isEmpty()) {
      StringBuilder sb = new StringBuilder("");
      sb.append("This action will cause the select value to be\n");
      sb.append("emptied for " + compList.size() + " universe computed objects\n");
      sb.append("Do you want to continue?");

      return sb.toString();
    }

    if (compList.size() > 0 && compList2.size() > 0) {
      StringBuilder sb = new StringBuilder("");
      sb.append("If you remove this formula system will start using\n");
      sb.append("a formula with the same name from the base tech pack\n");
      sb.append("Do you want to continue?");

      return sb.toString();
    }

    if (isBase) {
      StringBuilder sb = new StringBuilder("");
      sb.append("Removing this base techpacks formula\n");
      sb.append("can have effect on every tech pack using this base tech pack\n");
      sb.append("Do you want to continue?");

      return sb.toString();
    }

    return "";
  }

  /**
   * Return the universe formulas table model.
   * 
   * @return table model
   */
  public UniverseFormulasTableModel getUniverseFormulasTableModel() {
    return ufTableModel;
  }

  /**
   * Sets the universe formulas table model.
   * 
   * @param ufTableModel
   */
  public void setUniverseFormulasTableModel(UniverseFormulasTableModel ufTableModel) {
    this.ufTableModel = ufTableModel;
  }

}
