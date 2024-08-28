package ssc.rockfactory;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.Vector;

public class MeasurementcounterFactory implements Cloneable {

  private Vector vec;

  private RockFactory rockFact;

  private Measurementcounter whereObject;

  public MeasurementcounterFactory(RockFactory rockFact, Measurementcounter whereObject) throws SQLException,
      RockException {
    this.whereObject = whereObject;

    this.vec = new Vector();
    this.rockFact = rockFact;
    RockResultSet results = rockFact.setSelectSQL(false, whereObject);
    Iterator it = rockFact.getData(whereObject, results);
    Measurementcounter o = new Measurementcounter(rockFact);

    while (it.hasNext()) {
      o = (Measurementcounter) it.next();
      o.cleanModifiedColumns();
      this.vec.addElement(o);
    }
    results.close();
  }

  public MeasurementcounterFactory(RockFactory rockFact, Measurementcounter whereObject, String orderByClause)
      throws SQLException, RockException {
    this.whereObject = whereObject;
    this.vec = new Vector();
    this.rockFact = rockFact;
    RockResultSet results = rockFact.setSelectSQL(false, whereObject, orderByClause);
    Iterator it = rockFact.getData(whereObject, results);
    Measurementcounter o = new Measurementcounter(rockFact);
    while (it.hasNext()) {
      o = (Measurementcounter) it.next();
      o.cleanModifiedColumns();
      this.vec.addElement(o);
    }
    results.close();
  }

  /**
   * Get an element from the vector
   * 
   * @param i
   *          the element indicator
   */
  public Measurementcounter getElementAt(int i) {
    if (i < this.vec.size()) {
      return (Measurementcounter) this.vec.elementAt(i);
    }
    return (Measurementcounter) null;
  }

  /**
   * The size of the RockFactory vector
   */
  public int size() {
    return this.vec.size();
  }

  /**
   * The generated GET METHODS
   */
  public Vector get() {
    return vec;
  }

  /**
   * equals method test wheather the objects field values and and the parametrs
   * objects field values are equal.
   */
  public boolean equals(Vector otherVector) {
    if (this.vec == otherVector)
      return true;
    if ((this.vec == null) || (otherVector == null))
      return false;
    if (this.vec.size() != otherVector.size())
      return false;
    for (int i = 0; i < this.vec.size(); i++) {
      MeasurementcounterFactory o = (MeasurementcounterFactory) this.vec.elementAt(i);
      MeasurementcounterFactory otherO = (MeasurementcounterFactory) otherVector.elementAt(i);
      if (o.equals(otherO) == false)
        return false;
    }
    return true;
  }

  /**
   * Delete object contents from database
   * 
   * @exception SQLException
   */
  public int deleteDB() throws SQLException, RockException {
    return this.rockFact.deleteData(false, this.whereObject);
  }

  /**
   * to enable a public clone method inherited from Object class (private
   * method)
   */
  public Object clone() {
    Object o = null;
    try {
      o = super.clone();
    } catch (CloneNotSupportedException e) {
    }
    return o;
  }

}
