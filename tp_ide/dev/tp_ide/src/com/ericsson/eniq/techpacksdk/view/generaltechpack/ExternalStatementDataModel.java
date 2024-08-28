package com.ericsson.eniq.techpacksdk.view.generaltechpack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.DefaultTreeModel;
import ssc.rockfactory.RockDBObject;
import ssc.rockfactory.RockFactory;
import com.distocraft.dc5000.repository.dwhrep.Externalstatement;
import com.distocraft.dc5000.repository.dwhrep.ExternalstatementFactory;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.distocraft.dc5000.repository.dwhrep.VersioningFactory;
import com.ericsson.eniq.component.DataTreeNode;
import com.ericsson.eniq.component.ExceptionHandler;
import com.ericsson.eniq.techpacksdk.datamodel.DataModel;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;

public class ExternalStatementDataModel extends DefaultTreeModel implements DataModel {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private static final Logger logger = Logger.getLogger(ManageExternalStatementView.class.getName());

  public String vrVersionid;

  class ESTableModel extends AbstractTableModel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private List columnNames;

    private Vector<Externalstatement> rowData = new Vector<Externalstatement>();

    private Vector<Externalstatement> removedData = new Vector<Externalstatement>();

    public ESTableModel(final List columnNames, final Versioning obj) {
      try {

        this.columnNames = columnNames;

        if (obj != null) {

        	final Externalstatement es = new Externalstatement(etlRock);
          vrVersionid = ((Versioning) obj).getVersionid();
          es.setVersionid(vrVersionid);

          final ExternalstatementFactory esF = new ExternalstatementFactory(etlRock, es, "ORDER BY EXECUTIONORDER");
          final Iterator iter = esF.get().iterator();

          while (iter.hasNext()) {
        	  final Externalstatement tmpEs = (Externalstatement) iter.next();
            // Only statements with positive execution order and
            // non-base definitions are included in the model.
            if (tmpEs.getExecutionorder() > 0) {

              if (tmpEs != null && (tmpEs.getBasedef() == null || tmpEs.getBasedef() != 1)) {
                rowData.add((Externalstatement) tmpEs.clone());
              }
            }
          }
        }
      } catch (Exception e) {
        ExceptionHandler.instance().handle(e);
        e.printStackTrace();
      }
    }

    public String getColumnName(final int col) {
      return columnNames.get(col).toString();
    }

    public int getRowCount() {
      return rowData.size();
    }

    public Vector<Externalstatement> getDeletedRows() {
      return removedData;
    }

    public int getColumnCount() {
      return columnNames.size();
    }

    public Object getValueAt(final int row, final int col) {

    	final Externalstatement extstate = rowData.get(row);

      // name
      if (col == 0) {
        return extstate.getStatementname();
      }

      // dbconnection
      if (col == 1) {
        return extstate.getDbconnection();
      }

      if (col == 2) {
        return extstate.getStatement();
      }
      return null;
    }

    public boolean isCellEditable(final int row, final int col) {
      return true;
    }

    public void setValueAt(final Object value, final int row, final int col) {

      try {

    	  final Externalstatement estate = rowData.get(row);

        if (col == 0) {
          estate.setStatementname((String) value);
          this.fireTableChanged(null);
        }

        if (col == 1) {
          estate.setDbconnection((String) value);
          this.fireTableChanged(null);
        }
        if (col == 2) {
          estate.setStatement((String) value);
          this.fireTableChanged(null);
        }

      } catch (Exception e) {
        ExceptionHandler.instance().handle(e);
        e.printStackTrace();
      }
    }

    public void addEmptyNewRow(final int row) {

    	final Externalstatement es = new Externalstatement(etlRock, true);
      es.setStatementname("");
      es.setDbconnection("");
      es.setStatement("");
      rowData.add(row + 1, es);

      this.fireTableChanged(null);
    }

    public void addNewRow(final Externalstatement row) {

      rowData.add(row);
      this.fireTableChanged(null);
    }

    public void removeRow(final int row) {
      removedData.add((Externalstatement) rowData.get(row).clone());
      rowData.remove(row);

      this.fireTableChanged(null);
    }

    public void moveUpRow(final int row) {
      if (row > 0) {
        rowData.add(row - 1, (Externalstatement) (rowData.get(row)));
        rowData.remove(row + 1);
      }
    }

    public void moveDownRow(final int row) {
      if (row < rowData.size() - 1) {
        rowData.add(row + 2, (Externalstatement) (rowData.get(row)));
        rowData.remove(row);
      }
    }

    public void duplicateRow(final int row) {

      rowData.add(row + 1, (Externalstatement) (rowData.get(row).clone()));
    }

    public Externalstatement getRow(final int row) {
      return rowData.get(row);
    }
  }

  class DESTableModel extends AbstractTableModel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private List columnNames;

    private Vector<Externalstatement> rowData = new Vector<Externalstatement>();

    private Vector<Externalstatement> removedData = new Vector<Externalstatement>();

    public DESTableModel(final List columnNames, final Versioning obj) {

      this.columnNames = columnNames;

      try {
        if (obj != null) {

        	final Externalstatement des = new Externalstatement(etlRock);
        	final String vrVersionid = ((Versioning) obj).getVersionid();
          des.setVersionid(vrVersionid);
          final ExternalstatementFactory desF = new ExternalstatementFactory(etlRock, des, "ORDER BY EXECUTIONORDER");
          final Iterator deiter = desF.get().iterator();
          while (deiter.hasNext()) {
        	  final Externalstatement tmpDEs = (Externalstatement) deiter.next();
            // Only statements with negative execution order and
            // non-base definitions are included in the model.
            if (tmpDEs.getExecutionorder() < 0) {

              if (tmpDEs != null && (tmpDEs.getBasedef() == null || tmpDEs.getBasedef() != 1)) {
                rowData.add((Externalstatement) tmpDEs.clone());
              }
            }
          }
        }
      } catch (Exception e) {
        ExceptionHandler.instance().handle(e);
        e.printStackTrace();
      }
    }

    public String getColumnName(final int col) {
      return columnNames.get(col).toString();
    }

    public int getRowCount() {
      return rowData.size();
    }

    public Vector<Externalstatement> getDeletedRows() {
      return removedData;
    }

    public int getColumnCount() {
      return columnNames.size();
    }

    public Object getValueAt(final int row, final int col) {

    	final Externalstatement deextstate = rowData.get(row);

      // name
      if (col == 0) {
        return deextstate.getStatementname();
      }

      // dbconnection
      if (col == 1) {
        return deextstate.getDbconnection();
      }

      if (col == 2) {
        return deextstate.getStatement();
      }
      return null;
    }

    public boolean isCellEditable(final int row, final int col) {
      return true;
    }

    public void setValueAt(final Object value, final int row, final int col) {

      try {

    	  final Externalstatement deestate = rowData.get(row);

        if (col == 0) {
          deestate.setStatementname((String) value);
          this.fireTableChanged(null);
        }

        if (col == 1) {
          deestate.setDbconnection((String) value);
          this.fireTableChanged(null);
        }
        if (col == 2) {
          deestate.setStatement((String) value);
          this.fireTableChanged(null);
        }

      } catch (Exception e) {
        ExceptionHandler.instance().handle(e);
        e.printStackTrace();
      }
    }

    public void addEmptyNewRow(final int row) {

    	final Externalstatement es = new Externalstatement(etlRock, true);
      es.setStatementname("");
      es.setDbconnection("");
      es.setStatement("");
      rowData.add(row + 1, es);

      this.fireTableChanged(null);
    }

    public void addNewRow(final Externalstatement row) {

      rowData.add(row);
      this.fireTableChanged(null);
    }

    public void removeRow(final int row) {
      removedData.add((Externalstatement) rowData.get(row).clone());
      rowData.remove(row);

      this.fireTableChanged(null);
    }

    public void moveUpRow(final int row) {
      if (row > 0) {
        rowData.add(row - 1, (Externalstatement) (rowData.get(row)));
        rowData.remove(row + 1);
      }
    }

    public void moveDownRow(final int row) {
      if (row < rowData.size() - 1) {
        rowData.add(row + 2, (Externalstatement) (rowData.get(row)));
        rowData.remove(row);
      }
    }

    public void duplicateRow(final int row) {

      rowData.add(row + 1, (Externalstatement) (rowData.get(row).clone()));
    }

    public Externalstatement getRow(final int row) {
      return rowData.get(row);
    }
  }

  RockFactory etlRock;

  private ESTableModel estm;

  private DESTableModel destm;

  DataModelController dataModelController = null;

  Externalstatement externalstatement;

  Externalstatement deexternalstatement;

  public ExternalStatementDataModel(final RockFactory etlRock) {
    super(null);
    this.estm = createESTableModel(null);
    this.destm = createDESTableModel(null);
    this.etlRock = etlRock;
  }

  public void setExternalStatement(final DataTreeNode dataTreeNode) {
    externalstatement = (Externalstatement) dataTreeNode.getRockDBObject();
    deexternalstatement = (Externalstatement) dataTreeNode.getRockDBObject();
  }

  public ESTableModel getESTableModel() {
    return estm;
  }

  public DESTableModel getDESTableModel() {
    return destm;
  }

  public void setDataModelController(final DataModelController dataModelController) {
    this.dataModelController = dataModelController;
  }

  public Object[] getTechPacks() {

    try {

    	final Versioning ver = new Versioning(etlRock);
    	final VersioningFactory verF = new VersioningFactory(etlRock, ver);

    	final Object[] result = new Object[verF.get().size()];
      for (int i = 0; i < verF.get().size(); i++) {
    	  final Versioning v = (Versioning) verF.get().get(i);
        result[i] = (v.getTechpack_name() + ":" + v.getTechpack_version());
      }

      return result;

    } catch (Exception e) {
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    }
    return null;
  }

  private void saveESTableModel() throws Exception {

    // Vector<Externalstatement> deleted =
    // dataModelController.getExternalStatementDataModel().estm.getDeletedRows();

    // Remove all external statements from ExternalStatement table (including
    // the base definitions). The external statements have a positive execution
    // order value.
	  final Externalstatement delEs = new Externalstatement(etlRock);
    delEs.setVersionid(vrVersionid);
    final ExternalstatementFactory delEsF = new ExternalstatementFactory(etlRock, delEs);
    final Iterator deliter = delEsF.get().iterator();
    while (deliter.hasNext()) {
    	final Externalstatement tmpdelEs = (Externalstatement) deliter.next();
      if (tmpdelEs.getExecutionorder() > 0) {
        tmpdelEs.deleteDB();
      }

    }

    // Initialize the execution order for the statements.
    long execorder = 0;

    // Store the statements from the base techpack. This will only be done if
    // the current techpack is not a base techpack and the base has been
    // defined.

    final Versioning versioning = dataModelController.getTechPackTreeDataModel().getVersionByVersionId(vrVersionid);

    if (!versioning.getTechpack_type().equalsIgnoreCase("base")
        && (versioning.getBasedefinition() != null && versioning.getBasedefinition() != "")) {

      // Get the base version
    	final Versioning baseVersioning = dataModelController.getTechPackTreeDataModel().getVersionByVersionId(
          versioning.getBasedefinition());
    	final String baseVersionId = baseVersioning.getVersionid();

    	final Externalstatement es = new Externalstatement(etlRock);
      es.setVersionid(baseVersionId);
      final ExternalstatementFactory esF = new ExternalstatementFactory(etlRock, es, "ORDER BY EXECUTIONORDER");
      final Iterator iter = esF.get().iterator();

      // Iterate through all the statements in the base techpack and store them
      // as statements for the current techpack.
      while (iter.hasNext()) {
    	  final Externalstatement tmpEs = (Externalstatement) iter.next();
        // Only statements with positive execution order are included, since the
        // negative values are used for de-externalstatements.
        if (tmpEs.getExecutionorder() > 0) {

          String statementName = tmpEs.getStatementname();
          String statement = tmpEs.getStatement();

          // Special handling for the external statement
          // 'create view SELECT_(((TPName)))_AGGLEVEL': Replace the
          // (((TPNAME))) with the current techpack (without 'DC_').
          if (tmpEs.getStatementname().equals("create view SELECT_(((TPName)))_AGGLEVEL")) {
        	  final String tpName = versioning.getTechpack_name().replace("DC_", "");
            statementName = statementName.replace("(((TPName)))", tpName);
            statement = statement.replace("(((TPName)))", tpName);
          }

          // Create and save the new statement
          final Externalstatement newEs = new Externalstatement(etlRock);
          newEs.setVersionid(vrVersionid);
          newEs.setStatementname(statementName);
          execorder = execorder + 1;
          newEs.setExecutionorder(execorder);
          newEs.setDbconnection(tmpEs.getDbconnection());
          newEs.setStatement(statement);
          newEs.setBasedef(1);
          newEs.saveDB();

          // System.out.println("saveESTableModel(): DEBUG: Saved name: " +
          // statementName + ", statement: " + statement);
        }
      }
    }

    // Store the statements from the model. The execution order will continue
    // from where the statements from the base techpack left.
    final int row = dataModelController.getExternalStatementDataModel().estm.getRowCount();

    for (int r = 0; r < row; r++) {

    	final Externalstatement tmpes = new Externalstatement(etlRock);
      tmpes.setVersionid(vrVersionid);
      tmpes.setStatementname(dataModelController.getExternalStatementDataModel().estm.getRow(r).getStatementname());
      execorder = execorder + 1;
      tmpes.setExecutionorder(execorder);
      tmpes.setDbconnection(dataModelController.getExternalStatementDataModel().estm.getRow(r).getDbconnection());
      tmpes.setStatement(dataModelController.getExternalStatementDataModel().estm.getRow(r).getStatement());
      tmpes.setBasedef(0);
      tmpes.saveDB();
    }

  }

  private void saveDESTableModel() throws Exception {

    // Vector<Externalstatement> deleted =
    // dataModelController.getExternalStatementDataModel().destm.getDeletedRows();

    // Remove all de-external statements from ExternalStatement table (including
    // the base definitions). The de-external statements have a negative
    // execution order value.
	  final Externalstatement delEs = new Externalstatement(etlRock);
    delEs.setVersionid(vrVersionid);
    final ExternalstatementFactory delEsF = new ExternalstatementFactory(etlRock, delEs);
    final Iterator deliter = delEsF.get().iterator();
    while (deliter.hasNext()) {
    	final Externalstatement tmpdelEs = (Externalstatement) deliter.next();
      if (tmpdelEs.getExecutionorder() < 0) {
        tmpdelEs.deleteDB();
      }

    }

    // Initialize the execution order for the statements.
    long execorder = 0;

    // Store the statements from the base techpack. This will only be done if
    // the current techpack is not a base techpack and the base has been
    // defined.

    final Versioning versioning = dataModelController.getTechPackTreeDataModel().getVersionByVersionId(vrVersionid);

    if (!versioning.getTechpack_type().equalsIgnoreCase("base")
        && (versioning.getBasedefinition() != null && versioning.getBasedefinition() != "")) {

      // Get the base version
    	final Versioning baseVersioning = dataModelController.getTechPackTreeDataModel().getVersionByVersionId(
          versioning.getBasedefinition());
    	final String baseVersionId = baseVersioning.getVersionid();

    	final Externalstatement es = new Externalstatement(etlRock);
      es.setVersionid(baseVersionId);
      final ExternalstatementFactory esF = new ExternalstatementFactory(etlRock, es, "ORDER BY EXECUTIONORDER");
      final Iterator iter = esF.get().iterator();

      // Iterate through all the statements in the base techpack and store them
      // as statements for the current techpack.
      while (iter.hasNext()) {
    	  final Externalstatement tmpDes = (Externalstatement) iter.next();
        // Only statements with negative execution order are included.
        if (tmpDes.getExecutionorder() < 0) {

          // Create and save the new statement
        	final Externalstatement newDes = new Externalstatement(etlRock);
          newDes.setVersionid(vrVersionid);
          newDes.setStatementname(tmpDes.getStatementname());
          execorder = execorder - 1;
          newDes.setExecutionorder(execorder);
          newDes.setDbconnection(tmpDes.getDbconnection());
          newDes.setStatement(tmpDes.getStatement());
          newDes.setBasedef(1);
          newDes.saveDB();
        }
      }
    }

    // Store the statements from the model. The execution order will continue
    // from where the statements from the base techpack left.
    final int row = dataModelController.getExternalStatementDataModel().destm.getRowCount();
    for (int r = 0; r < row; r++) {

    	final Externalstatement tmpdes = new Externalstatement(etlRock);
      tmpdes.setVersionid(vrVersionid);
      tmpdes.setStatementname(dataModelController.getExternalStatementDataModel().destm.getRow(r).getStatementname());
      execorder = execorder - 1;
      tmpdes.setExecutionorder(execorder);

      tmpdes.setDbconnection(dataModelController.getExternalStatementDataModel().destm.getRow(r).getDbconnection());
      tmpdes.setStatement(dataModelController.getExternalStatementDataModel().destm.getRow(r).getStatement());
      tmpdes.setBasedef(0);
      tmpdes.saveDB();
    }
  }

  private ESTableModel createESTableModel(final Versioning obj) {

	  final List<String> columnNames = new ArrayList<String>();
    columnNames.add("Name");
    columnNames.add("DB Connection");
    columnNames.add("Statement");

    final ESTableModel estm = new ESTableModel(columnNames, obj);
    return estm;
  }

  private DESTableModel createDESTableModel(final Versioning obj) {

	  final List<String> columnNames = new ArrayList<String>();
    columnNames.add("Name");
    columnNames.add("DB Connection");
    columnNames.add("Statement");

    final DESTableModel destm = new DESTableModel(columnNames, obj);
    return destm;
  }

  public void refresh(final Versioning versioning) {
    try {
      this.estm = createESTableModel(versioning);
      this.destm = createDESTableModel(versioning);

      final Externalstatement es = new Externalstatement(etlRock);
      es.setVersionid(vrVersionid);
      final ExternalstatementFactory esF = new ExternalstatementFactory(etlRock, es);
      externalstatement = esF.getElementAt(0);

      final Externalstatement des = new Externalstatement(etlRock);
      des.setVersionid(vrVersionid);
      final ExternalstatementFactory desF = new ExternalstatementFactory(etlRock, des);
      deexternalstatement = desF.getElementAt(0);

    } catch (final Exception e) {
      logger.warning(e.getMessage());
    }
  }

  public void refresh() {
    this.estm = createESTableModel(null);
    this.destm = createDESTableModel(null);
  }

  public RockFactory getRockFactory() {
    return etlRock;
  }

  public String getVersionid() {

    if (externalstatement == null) {
      return "";
    }

    return externalstatement.getVersionid();
  }

  public void setVersionid(final String str) {
    externalstatement.setVersionid(str);
  }

  public String setVersionid() {

    if (externalstatement == null) {
      return "";
    }

    return externalstatement.getVersionid();
  }

  public String getStatementname() {
    if (externalstatement == null) {
      return "";
    }
    return externalstatement.getStatementname();
  }

  public Long getExecutionorder() {
    if (externalstatement == null) {
      return null;
    }
    return externalstatement.getExecutionorder();
  }

  public String getDbconnection() {
    if (externalstatement == null) {
      return "";
    }
    return externalstatement.getDbconnection();
  }

  public String getStatement() {
    if (externalstatement == null) {
      return "";
    }
    return externalstatement.getStatement();
  }

  public void save() {

    try {

      dataModelController.getRockFactory().getConnection().setAutoCommit(false);

      saveESTableModel();
      saveDESTableModel();

      dataModelController.rockObjectsModified(this);
      dataModelController.getRockFactory().getConnection().commit();

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
      } catch (Exception e) {
        ExceptionHandler.instance().handle(e);
        e.printStackTrace();
      }
    }
  }

  /**
   * Data validation
   * 
   * @return errormessages
   */
  public Vector<String> validateData() {
    // boolean origAutoCommit = false;
	  final Vector<String> strVector = new Vector<String>();

    // check values of estm
	  final ESTableModel estm = getESTableModel();
    for (Externalstatement es1 : estm.rowData) {
      if (es1.getStatementname().isEmpty()) {
        strVector.add("ERROR: Name is empty in External statements");
      }

      if (es1.getDbconnection().isEmpty()) {
        strVector.add("ERROR: DB Connection is empty in External statements");
      }

      if (es1.getStatement().isEmpty()) {
        strVector.add("ERROR: Statement is empty in External statements");
      }
    }

    // check values of dtm
    final DESTableModel dtm2 = getDESTableModel();
    for (Externalstatement es1 : dtm2.rowData) {
      if (es1.getStatementname().isEmpty()) {
        strVector.add("ERROR: Name is empty in De-External Statements");
      }

      if (es1.getDbconnection().isEmpty()) {
        strVector.add("ERROR: DB Connection is empty in De-External Statements");
      }

      if (es1.getStatement().isEmpty()) {
        strVector.add("ERROR: Statement is empty in De-External Statements");
      }
    }

    // validate duplicates of estm
    List<String> names = new ArrayList<String>();
    for (Externalstatement es : estm.rowData) {
    	final String sn = es.getStatementname();
      for (String str : names) {
        if (str.equals(sn)){
        	strVector.add("ERROR: Cannot have same names " + str + " and " + sn + " in External statement");
        }
      }
      names.add(sn);
    }

    // validate duplicates of dtm
    names = new ArrayList<String>();
    final DESTableModel dtm = getDESTableModel();
    for (Externalstatement es : dtm.rowData) {
    	final String sn = es.getStatementname();
      for (String str : names) {
        if (str.equals(sn)){
        	strVector.add("ERROR: Cannot have same names " + str + " and " + sn + " in DE-External statement");
        }
      }
      names.add(sn);
    }

    // validate duplicates of dtm and estm
    final DESTableModel dtm1 = getDESTableModel();
    final ESTableModel estm1 = getESTableModel();
    for (Externalstatement esA : dtm1.rowData) {
    	final String snA = esA.getStatementname();
      for (Externalstatement esB : estm1.rowData) {
        String snB = esB.getStatementname();
        if (snB.equals(snA)) {
          String err = "ERROR: Cannot have same names in External statement " + snA + " and in De-External statement "
              + snB;
          if (!strVector.contains(err)) {
            strVector.add(err);
          }
        }
      }
    }

    return strVector;
  }

  public boolean validateNew(final RockDBObject rObj) {
    return true;
  }

  public boolean validateDel(final RockDBObject rObj) {
    return true;
  }

  public boolean validateMod(final RockDBObject rObj) {
    return true;
  }

  public boolean newObj(final RockDBObject rObj) {
    try {
      rObj.insertDB();
    } catch (Exception e) {
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    }
    return true;
  }

  public boolean delObj(final RockDBObject rObj) {
    try {
      rObj.deleteDB();
    } catch (Exception e) {
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    }
    return true;
  }

  public boolean modObj(final RockDBObject rObj) {
    RockDBObject[] r = new RockDBObject[1];
    r[0] = rObj;
    return modObj(r);
  }

  public boolean modObj(final RockDBObject rObj[]) {

    try {

      etlRock.getConnection().setAutoCommit(false);

      for (int i = 0; i < rObj.length; i++) {
        rObj[i].updateDB();
      }

      dataModelController.rockObjectsModified(this);

      etlRock.getConnection().commit();

    } catch (final Exception e) {
      try {
        etlRock.getConnection().rollback();
      } catch (final Exception sqlE) {
        ExceptionHandler.instance().handle(sqlE);
        sqlE.printStackTrace();
      }
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    } finally {
      try {
        etlRock.getConnection().setAutoCommit(true);
      } catch (Exception sqlE) {
        ExceptionHandler.instance().handle(sqlE);
        sqlE.printStackTrace();
      }
    }

    return true;
  }

  public boolean updated(final DataModel dataModel) {
    return false;
  }
}
