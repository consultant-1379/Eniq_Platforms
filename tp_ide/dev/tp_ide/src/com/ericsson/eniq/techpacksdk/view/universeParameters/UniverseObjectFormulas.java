package com.ericsson.eniq.techpacksdk.view.universeParameters;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Logger;

import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.repository.dwhrep.Universeformulas;
import com.distocraft.dc5000.repository.dwhrep.UniverseformulasFactory;
import com.distocraft.dc5000.repository.dwhrep.Versioning;

/**
 * An utility class for retrieving the formulas that can be selected in universe
 * objects view.
 * 
 * When an instance of UniverseObjectFormulas is constructed, it searches for
 * the available formulas in the following order: 1. The formulas in techpack
 * indicated by the parameter 'versionId' 2. The formulas in the base techpack
 * of techpack indicated by 'versionId'
 * 
 * If the same formula name is included both in the techpack and the base
 * techpack, the name is shown only once.
 * 
 * @author epiituo
 * 
 */
public class UniverseObjectFormulas {

  private static final Logger logger = Logger.getLogger(UniverseObjectFormulas.class.getName());

  private Vector<Universeformulas> formulas;

  private Vector<Universeformulas> baseFormulas;

  private RockFactory rockFactory;

  private String versionId;

  public UniverseObjectFormulas(RockFactory rockFactory, String versionId) {
    this.rockFactory = rockFactory;
    this.versionId = versionId;
    collectFormulas();
  }

  /**
   * Returns an array of the universe formulas from this techpack and from the
   * base techpack.
   * 
   * @return
   */
  public Universeformulas[] get() {
    Universeformulas[] result = new Universeformulas[this.formulas.size() + this.baseFormulas.size()];
    Vector<Universeformulas> allFormulas = new Vector<Universeformulas>();
    allFormulas.addAll(this.formulas);
    allFormulas.addAll(this.baseFormulas);
    result = allFormulas.toArray(result);
    return result;
  }

  /**
   * Returns the list of universe formulas names from this techpack and the base
   * techpack.
   * 
   * @return
   */
  public String[] getNames() {
    String[] result = new String[this.formulas.size() + this.baseFormulas.size()];
    for (int i = 0; i < this.formulas.size(); ++i) {
      result[i] = this.formulas.elementAt(i).getName();
    }
    for (int i = 0; i < this.baseFormulas.size(); ++i) {
      result[this.formulas.size() + i] = this.baseFormulas.elementAt(i).getName();
    }
    return result;
  }

  /**
   * Collects the universe formulas objects from this techpack and the base
   * techpack.
   */
  private void collectFormulas() {
    // Vector<Universeformulas> result = new Vector<Universeformulas>();
    try {
      formulas = getFormulasFromThisTP();
      baseFormulas = getFormulasFromThisTPBase(formulas);
    } catch (RockException e) {
      logger.warning("Unable to retrieve universe formulas.\n" + e);
    } catch (SQLException e) {
      logger.warning("Unable to retrieve universe formulas.\n" + e);
    }
  }

  /**
   * Gets the universe formulas from this techpack.
   * 
   * @return a vector of universe formulas
   * @throws RockException
   * @throws SQLException
   */
  private Vector<Universeformulas> getFormulasFromThisTP() throws RockException, SQLException {
    return getFormulas(this.versionId);
  }

  /**
   * Returns a list of universe formulas from the base techpack. The list
   * includes only those universe formulas which are not included in the list
   * given a parameter, i.e. the duplicates are filtered out.
   * 
   * @param formulas
   * @return the vector of universe formulas
   * @throws RockException
   * @throws SQLException
   */
  private Vector<Universeformulas> getFormulasFromThisTPBase(Vector<Universeformulas> formulas) throws RockException,
      SQLException {
    // Get the versioning for this techpack.
    Versioning versioning = new Versioning(this.rockFactory, this.versionId);

    // Get the versionId for the base techpack.
    String baseVersionId = versioning.getBasedefinition();

    // Get all the formulas from the base techpack.
    Vector<Universeformulas> newFormulas = getFormulas(baseVersionId);

    // Initialize a vector for the filtered formulas.
    Vector<Universeformulas> filteredFormulas = new Vector<Universeformulas>();

    // Iterate through all base techpack formulas and check if they are already
    // included in the formulas of this techpack. If a formula is not included,
    // it is added to the filtered list.
    boolean found;
    Iterator<Universeformulas> newIter = newFormulas.iterator();
    while (newIter.hasNext()) {
      found = false;
      Universeformulas currentNewFormula = newIter.next();

      // Check for match
      Iterator<Universeformulas> iter = formulas.iterator();
      while (iter.hasNext()) {
        Universeformulas currentFormula = iter.next();
        if (currentNewFormula.getName().equals(currentFormula.getName())) {
          found = true;
          break;
        }
      }

      // If the match was not found, then add the formula to the filtered list.
      if (!found) {
        filteredFormulas.add(currentNewFormula);
      }
    }

    return filteredFormulas;
  }

  /*
   * private Vector<Universeformulas>
   * addFormulasFromOtherTPs(Vector<Universeformulas> formulas) throws
   * RockException, SQLException { Versioning v = new
   * Versioning(this.rockFactory); VersioningFactory vF = new
   * VersioningFactory(this.rockFactory, v, true); Iterator<Versioning>
   * versioningIterator = vF.get().iterator(); Vector<Universeformulas>
   * newFormulas = getFormulas(versionId); while (versioningIterator.hasNext())
   * { Versioning versioning = versioningIterator.next(); String techPackType =
   * versioning.getTechpack_type(); // Get formulas from non-base techpacks if
   * (!techPackType.equalsIgnoreCase("BASE")) { String versionId =
   * versioning.getVersionid(); newFormulas.addAll(getFormulas(versionId)); } }
   * merge(formulas, newFormulas); return formulas; }
   */
  /*
   * private Vector<Universeformulas>
   * addFormulasFromOtherBaseTPs(Vector<Universeformulas> formulas) throws
   * RockException, SQLException { Versioning v = new
   * Versioning(this.rockFactory); VersioningFactory vF = new
   * VersioningFactory(this.rockFactory, v, true); Iterator<Versioning>
   * versioningIterator = vF.get().iterator(); Vector<Universeformulas>
   * newFormulas = getFormulas(versionId); while (versioningIterator.hasNext())
   * { Versioning versioning = versioningIterator.next(); String techPackType =
   * versioning.getTechpack_type(); // Get formulas from base techpacks if
   * (techPackType.equalsIgnoreCase("BASE")) { String versionId =
   * versioning.getVersionid(); newFormulas.addAll(getFormulas(versionId)); } }
   * merge(formulas, newFormulas); return formulas; }
   */

  private Vector<Universeformulas> getFormulas(String versionId) throws RockException, SQLException {
    // Get the formulas for this techpack
    Universeformulas uF = new Universeformulas(this.rockFactory);
    uF.setVersionid(versionId);
    UniverseformulasFactory uFFactory = new UniverseformulasFactory(this.rockFactory, uF);
    return uFFactory.get();
  }

  /**
   * Merges the two vector containing Universeformulas objects. The merging is
   * performed by adding the items from secondVector to the firstVector, if
   * there is no item with same name (i.e. result of item.getName()) in the
   * firstVector.
   * 
   * @param firstVector
   * @param secondVector
   * @return firstVector with the non-duplicate items from the secondVector
   */
  /*
   * private void merge(Vector<Universeformulas> firstVector,
   * Vector<Universeformulas> secondVector) {
   * 
   * // First, create a vector containing names of the formulas in first //
   * vector. Vector<String> firstVectorFormulaNames = new Vector<String>();
   * Iterator<Universeformulas> firstVectorIterator = firstVector.iterator();
   * while (firstVectorIterator.hasNext()) { Universeformulas
   * formulaFromFirstVector = firstVectorIterator.next(); String
   * firstVectorFormulaName = formulaFromFirstVector.getName();
   * firstVectorFormulaNames.add(firstVectorFormulaName); }
   * 
   * // Add all the formulas from the second vector, whose name can not be //
   * found from the formula names vector, to the first vector.
   * Iterator<Universeformulas> secondVectorIterator = secondVector.iterator();
   * while (secondVectorIterator.hasNext()) { Universeformulas
   * formulaFromSecondVector = secondVectorIterator.next(); if
   * (!firstVectorFormulaNames.contains(formulaFromSecondVector.getName())) {
   * firstVector.add(formulaFromSecondVector); } } }
   */

}
