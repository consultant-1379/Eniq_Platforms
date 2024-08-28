package com.ericsson.eniq.etl.ebsHandler.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.repository.dwhrep.Measurementcounter;
import com.distocraft.dc5000.repository.dwhrep.MeasurementcounterFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementtype;
import com.distocraft.dc5000.repository.dwhrep.MeasurementtypeFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementtypeclass;
import com.distocraft.dc5000.repository.dwhrep.MeasurementtypeclassFactory;
import com.distocraft.dc5000.repository.dwhrep.Universeclass;
import com.distocraft.dc5000.repository.dwhrep.UniverseclassFactory;
import com.distocraft.dc5000.repository.dwhrep.Universecomputedobject;
import com.distocraft.dc5000.repository.dwhrep.UniversecomputedobjectFactory;
import com.distocraft.dc5000.repository.dwhrep.Universeparameters;
import com.distocraft.dc5000.repository.dwhrep.UniverseparametersFactory;

public class DoDelta extends DefaultHandler {

  private Document sorceNode;

  private Document targetNode;

  private List<String> comparedTypes;
  
  private List<String> ignoredParameters;

  protected Logger log = Logger.getLogger("DoDelta");

  private static int ROWSPEREXECUTE = 100;

  private int rowsUpdated = 0;

  public DoDelta(final Logger log, final List<String> comparedTypes, final List<String> ignoredParameters) {
    this.log = log;
    this.comparedTypes = comparedTypes;
    this.ignoredParameters = ignoredParameters;
  }

  private Document parse(final File file) throws Exception {

    return parse(new InputSource(new FileInputStream(file)));
  }

  private Document parse(final String str) throws Exception {

    return parse(new InputSource(new StringReader(str)));
  }

  private Document parse(final InputSource is) throws Exception {

    try {

      final DOMParser p = new DOMParser();
      p.parse(is);

      return p.getDocument();

    } catch (Exception e) {
      e.printStackTrace();
    }

    return null;
  }

  public StringBuffer invertRowOrder(final String sql) {
    final StringBuffer result = new StringBuffer();
    final String[] rows = sql.split(";\n");
    for (int i = rows.length - 1; i >= 0; i--) {
      result.append(rows[i] + ";\n");
    }
    return result;
  }

  public int rowdUpdated() {
    return rowsUpdated;
  }
  
  /**
   * @param rock
   * @param versionId
   * @throws Exception
   * New method to delete the existing entries in the Universeclass, Universeparameters, Universecomputedobject 
   * and Measurementcounter tables before a new MOM upgrade happens.
   * TR - HM78432
   */
  private void removeFromDB(final RockFactory rock, final String versionId) throws Exception{	  
		  // Remove from UniverseParameters	  
		  final Universeparameters upWhereObject = new Universeparameters(rock);
		  upWhereObject.setVersionid(versionId);	  
		  final UniverseparametersFactory upFactory = new UniverseparametersFactory(rock, upWhereObject);
		  log.info("Deleting the existing rows in UniverseParameters for versionId:"+versionId);
		  final int upDeleteRows = upFactory.deleteDB();
		  log.info("Deleted "+upDeleteRows+" from the UniverseParameters table.");

		  // Remove from UniverseComputedObject
		  final Universecomputedobject ucoWhereObject = new Universecomputedobject(rock);
		  ucoWhereObject.setVersionid(versionId);
		  final UniversecomputedobjectFactory ucoFactory = new UniversecomputedobjectFactory(rock, ucoWhereObject);
		  log.info("Deleting the existing rows in UniverseComputedObject for versionId:"+versionId);
		  final int ucoDeleteRows = ucoFactory.deleteDB();	  
		  log.info("Deleted "+ucoDeleteRows+" from the UniverseComputedObject table.");

		  // Remove from UniverseClass
		  final Universeclass ucWhereObject = new Universeclass(rock);
		  ucWhereObject.setVersionid(versionId);
		  ucWhereObject.setParent("Computed Counters");
		  final UniverseclassFactory ucFactory = new UniverseclassFactory(rock, ucWhereObject);	  
		  log.info("Deleting the existing rows in UniverseClass for versionId:"+versionId+" and Parent=Computed Counters");
		  final int ucDeleteRows = ucFactory.deleteDB();
		  log.info("Deleted "+ucDeleteRows+" from the UniverseClass table.");

		  // Remove from MeasurementCounter - Start from MeasurementTypeClass -> MeasurementType -> MeasurementCounter  
		  final Measurementtypeclass measurementTypeClass = new Measurementtypeclass(rock);
		  measurementTypeClass.setVersionid(versionId);
		  final MeasurementtypeclassFactory mtcFactory = new MeasurementtypeclassFactory(rock, measurementTypeClass);
		  final Iterator<Measurementtypeclass> mtcFactoryIterator = mtcFactory.get().iterator();
		  while (mtcFactoryIterator.hasNext()) {
			  final Measurementtypeclass mtc = (Measurementtypeclass) mtcFactoryIterator.next();

			  final Measurementtype measurementtype = new Measurementtype(rock);
			  measurementtype.setTypeclassid(mtc.getTypeclassid());
			  measurementtype.setVersionid(mtc.getVersionid());
			  final MeasurementtypeFactory mtFactory = new MeasurementtypeFactory(rock, measurementtype);

			  final Iterator<Measurementtype> mtFactoryIterator = mtFactory.get().iterator();
			  while (mtFactoryIterator.hasNext()) {
				  final Measurementtype mt = (Measurementtype) mtFactoryIterator.next();
				  Measurementcounter measurementcounter = new Measurementcounter(rock);
				  measurementcounter.setTypeid(mt.getTypeid());
				  final MeasurementcounterFactory mcFactory = new MeasurementcounterFactory(rock, measurementcounter);
				  log.info("Deleting the existing rows in MeasurementCounter for typeId:"+mt.getTypeid());
				  final int mcDeleteRows = mcFactory.deleteDB();
				  log.info("Deleted "+mcDeleteRows+" from the MeasurementCounter table.");
			  }          
		  }	  
    }

  public void updateDB(final RockFactory rock, final String versionId) throws Exception {

    final StringBuffer sql = new StringBuffer();
    /*
     *  New implementation for HM78432. New method removeFromDB(RockFactory, String) for removing all records from 
     *  Universeclass, Universeparameters, Universecomputedobjectfirst and Measurementcounter before the new upgrade. 
     */   

    log.info("Removing existing values from Universeclass, Universeparameters,Universecomputedobjectfirst and Measurementcounter");
    removeFromDB(rock, versionId);
    log.info("Inserting values from new xml.");
    // TODO: Load table command is far more effective than insert.
    inserIntoDB(rock, sorceNode, sql);

    final String[] sqls = sql.toString().split(";\n");
    rowsUpdated = sqls.length;
    int i = 0;

    while (i < sqls.length) {
      String sqlTmp = "";
      int max = i + ROWSPEREXECUTE;
      if (max > sqls.length)
        max = sqls.length;
      for (int ii = i; ii < max; ii++) {
        if (sqls[ii].length() > 0) {
          sqlTmp += sqls[ii] + ";\n";
        }
      }
      i = max;
      log.finest("SQL: " + sqlTmp);
      if (sqlTmp.length() > 0) {
        try {
          final Statement s = rock.getConnection().createStatement();
          s.executeUpdate(sqlTmp);
          s.close();
        } catch (Exception e) {
          log.severe("Error in SQL: " + sqlTmp);
          throw new Exception(e);
        }
      }
    }

  }

  /**
   * 
   * Creates an insert into clause from the given node
   * 
   * @param rock
   * @param n
   * @param sql
   * @return
   */
  private void inserIntoDB(final RockFactory rock, final Node n, final StringBuffer sql) {

    final NodeList tnl = n.getChildNodes();

    for (int i = 0, cnt = tnl.getLength(); i < cnt; i++) {

      if (tnl.item(i).getNodeType() != 3) {
        if (tnl.item(i) != null && tnl.item(i).getParentNode() != null && tnl.item(i).getAttributes() != null) {
          if (tnl.item(i).getAttributes().getNamedItem("DiffStatus") != null
              && !tnl.item(i).getAttributes().getNamedItem("DiffStatus").getNodeValue().equalsIgnoreCase("IGNORE")) {

            if (comparedTypes != null && (comparedTypes.contains(tnl.item(i).getNodeName()))) {

              // insert
              sql.append("insert into " + tnl.item(i).getNodeName() + " (" + getInsertAttributeNames(tnl.item(i))
                  + ") " + " values (" + getInsertAttributes(tnl.item(i)) + ");\n");
            }
          }
        }
      }

      inserIntoDB(rock, tnl.item(i), sql);
    }
  }

  public void doDelta() throws Exception {

    doDelta(sorceNode, targetNode, comparedTypes, ignoredParameters);
  }

  public void addSourceXML(final File file) throws Exception {
    sorceNode = parse(file);
  }

  public void addTargetXML(final File file) throws Exception {
    targetNode = parse(file);
  }

  public void addSourceXML(final String str) throws Exception {
    sorceNode = parse(str);
  }

  public void addTargetXML(final String str) throws Exception {
    targetNode = parse(str);
  }

  /**
   * 
   * Compares two node to each other,
   * 
   * @param n1
   * @param n2
   * @param comparedTypes
   * @return
   */
  private boolean compareNodes(final Node n1, final Node n2, final List<String> comparedTypes, final List<String> ignoredParameters) {

    if (comparedTypes != null
        && (!comparedTypes.contains(n1.getNodeName()) || !comparedTypes.contains(n2.getNodeName()))) {
      return true;
    }

    // same name & type
    if (!n1.getNodeName().equals(n2.getNodeName()) || n1.getNodeType() != n2.getNodeType()) {

      return false;
    }

    // same number of attributes
    if (n1.getAttributes().getLength() != n2.getAttributes().getLength()) {
      return false;
    }

    // same attribute names and values
    final int size = n1.getAttributes().getLength();
    int i = 0;
    for (int i1 = 0; i1 < size; i1++) {
      for (int i2 = 0; i2 < size; i2++) {

        if (n1.getAttributes().item(i1).getNodeName().equals(n2.getAttributes().item(i2).getNodeName())) {
          // if the parameter name is in the list we ignore it..
          if (!ignoredParameters.contains(n1.getNodeName()+"/"+n1.getAttributes().item(i1).getNodeName())) {
            if (n1.getAttributes().item(i1).getNodeValue().equals(n2.getAttributes().item(i2).getNodeValue())) {
              i++;
              break;
            }
          } else {
            i++;
          }
        }
      }
    }

    if (i != size) {
      return false;
    }

    // the same..
    return true;
  }

  private void setAttribute(final Node n, final String attrName, final String newValue) {
    if (n != null && n != null && n.getAttributes() != null) {
      if (n.getAttributes().getNamedItem(attrName) != null) {
        n.getAttributes().getNamedItem(attrName).setNodeValue(newValue);
      }
    }
  }

  private String getAttribute(final Node n, final String attrName) {
    if (n != null && n != null && n.getAttributes() != null) {
      if (n.getAttributes().getNamedItem(attrName) != null) {
        return n.getAttributes().getNamedItem(attrName).getNodeValue();
      }
    }
    return "";
  }

  private void doDelta(final Node n1, final Node n2, final List<String> comparedTypes, final List<String> ignoredParameters) throws Exception {

    final NodeList nl1 = n1.getChildNodes();
    final NodeList nl2 = n2.getChildNodes();

    for (int i1 = 0; i1 < nl1.getLength(); i1++) {
      if (nl1.item(i1).getNodeType() != 3) {

        for (int i2 = 0; i2 < nl2.getLength(); i2++) {
          if (nl2.item(i2).getNodeType() != 3 && !getAttribute(nl2.item(i2), "DiffStatus").equals("IGNORE")) {

            if (nl1.item(i1).getNodeName().equals(nl2.item(i2).getNodeName())
                && nl1.item(i1).getNodeType() == nl2.item(i2).getNodeType()) {

              doDelta(nl1.item(i1), nl2.item(i2), comparedTypes, ignoredParameters);

              if (compareNodes(nl1.item(i1), nl2.item(i2), comparedTypes,ignoredParameters)) {

                setAttribute(nl2.item(i2), "DiffStatus", "IGNORE");
                setAttribute(nl1.item(i1), "DiffStatus", "IGNORE");
                /*
                 * try { System.out.println(i1/2 + " " + i2/2 + " " +
                 * nl1.item(i1).getNodeName() + " " +
                 * nl1.item(i1).getAttributes(
                 * ).getNamedItem("CLASSNAME").getNodeName()+ " " +
                 * nl1.item(i1).
                 * getAttributes().getNamedItem("CLASSNAME").getNodeValue()+ " "
                 * +nl1.item(i1).getAttributes().getNamedItem("DiffStatus").
                 * getNodeValue()); } catch (Exception e){
                 * 
                 * }
                 */
                break;
              }
            }
          }
        }
      }
    }
  }

  public String toString() {
    String result = "";
    result += "-------------Remove-------------\n";
    result = printXML(result, targetNode);
    result += "-------------Add-------------\n";
    result = printXML(result, sorceNode);
    return result;
  }

  private String printXML(String result, final Node node) {

    final NodeList nl = node.getChildNodes();
    for (int i = 0, cnt = nl.getLength(); i < cnt; i++) {

      final String s = getAttributes(nl.item(i));

      if (nl.item(i).getNodeType() != 3) {
        if (nl.item(i) != null && nl.item(i).getParentNode() != null && nl.item(i).getAttributes() != null) {
          if (nl.item(i).getAttributes().getNamedItem("DiffStatus") != null
              && !nl.item(i).getAttributes().getNamedItem("DiffStatus").getNodeValue().equalsIgnoreCase("IGNORE")) {
            result += nl.item(i).getNodeName() + " " + s + "\n";
          }
        }
      }

      result = printXML(result, nl.item(i));
    }

    return result;
  }

  private String getAttributes(final Node n) {
    // columns
    String s = "";
    if (n != null && n.getAttributes() != null) {

      for (int a = 0; a < n.getAttributes().getLength(); a++) {
        n.getAttributes().item(a);
        s += " " + n.getAttributes().item(a).getNodeName() + " = \"" + n.getAttributes().item(a).getNodeValue() + "\"";
      }
    }
    return s;
  }

  private String getInsertAttributes(final Node n) {

    // columns
    String s = "";
    boolean first = true;
    if (n != null && n.getAttributes() != null) {

      for (int a = 0; a < n.getAttributes().getLength(); a++) {
        n.getAttributes().item(a);
        if (!n.getAttributes().item(a).getNodeName().equalsIgnoreCase("DiffStatus")) {
          if (first) {
            s += " " + handleNull(n.getAttributes().item(a).getNodeValue()) + "";
            first = false;
          } else {
            s += " ,  " + handleNull(n.getAttributes().item(a).getNodeValue()) + "";
          }

        }
      }
    }
    return s;
  }

  private String getInsertAttributeNames(final Node n) {

    // columns
    String s = "";
    boolean first = true;
    if (n != null && n.getAttributes() != null) {

      for (int a = 0; a < n.getAttributes().getLength(); a++) {
        n.getAttributes().item(a);
        if (!n.getAttributes().item(a).getNodeName().equalsIgnoreCase("DiffStatus")) {
          if (first) {
            s += " " + n.getAttributes().item(a).getNodeName() + "";
            first = false;
          } else {
            s += " ,  " + n.getAttributes().item(a).getNodeName() + "";
          }

        }
      }
    }
    return s;
  }

  private String handleNull(final String s) {
    if (s.equalsIgnoreCase("'null'")) {
      return "null";
    } else if (s.trim().equalsIgnoreCase("")) {
      return "null";
    } else {
      return s;
    }
  }  

  public Document getDeltaTree() {
    return sorceNode;
  }

}
