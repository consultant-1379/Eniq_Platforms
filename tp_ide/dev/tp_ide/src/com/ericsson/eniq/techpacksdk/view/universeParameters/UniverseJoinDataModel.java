package com.ericsson.eniq.techpacksdk.view.universeParameters;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ssc.rockfactory.RockDBObject;
import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.repository.dwhrep.Universejoin;
import com.distocraft.dc5000.repository.dwhrep.UniversejoinFactory;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;

public class UniverseJoinDataModel extends GenericUniverseDataModel<RockDBObject> {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public static final Logger logger = Logger.getLogger(UniverseJoinView.class.getName());

  public GenericUniverseTableModel<Universejoin> tablemodel;

  private String[] extensionsInTP;

  // Regular expression pattern to check universe extensions.
  // This matches either empty strings, or alphanumeric extension. Can't have "," on it's own or at the end.
  public static Pattern ALPHANUMERIC = Pattern.compile("^[\\s]*$|^([a-zA-Z0-9 ]+,)*[a-zA-Z0-9 ]+$");

  protected UniverseJoinDataModel(final DataModelController dataModelController, final Versioning versioning) {
    // Constructor for unit tests only.
    this.dataModelController = dataModelController;
    this.versioning = versioning;
  }

  /**
   * Constructor
   *
   * @param etlRock
   */
  public UniverseJoinDataModel(RockFactory etlRock) {
    super(etlRock);

  }


  /**
   * Creates tablemodel
   *
   * @param v
   * @return
   * @throws RockException
   * @throws SQLException
   */
  public UniverseJoinTableModel createTableModel(Versioning v) throws RockException, SQLException {

    Vector<Universejoin> u = null;
    if (v != null) {
      Universejoin U = new Universejoin(etlRock);
      U.setVersionid(v.getVersionid());
      UniversejoinFactory UF = new UniversejoinFactory(etlRock, U, "order by ordernro");
      u = UF.get();
    }

    UniverseJoinTableModel tm = new UniverseJoinTableModel(u, etlRock);

    return tm;
  }

  /**
   * Save to database
   */
  public void save() throws Exception {
    // Get Versionid and throw if it is empty
    String vrVersionid = versioning.getVersionid();
    if (vrVersionid == null || vrVersionid.equals(""))
      throw new Exception("Trying to save without versionID");

    // Remove with this versionId
    try {
      Universejoin delU = new Universejoin(etlRock);
      delU.setVersionid(vrVersionid);

      UniversejoinFactory delUF = new UniversejoinFactory(etlRock, delU);
      Iterator deliter = delUF.get().iterator();

      while (deliter.hasNext()) {
        Universejoin tmpdelU = (Universejoin) deliter.next();
        tmpdelU.deleteDB();
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }

    // Then add the ones on the Screen
    try {
      GenericUniverseTableModel<Universejoin> tableModel = getTableModel();
      for (Object o : tableModel.data) {
        Universejoin u = (Universejoin) o;
        if (!vrVersionid.equals(u.getVersionid()))
          throw new Exception("Trying to save for wrong versionId");

        u.insertDB();
      }
    } catch (Exception e) {
      System.out.println(e);
      throw e;
    }
  }

  /**
   * Data validation
   *
   * @return errormessages
   */
  public Vector<String> validateData() {
    Vector<String> errorStrings = new Vector<String>();
    GenericUniverseTableModel<Universejoin> tm = getTableModel();

    // Check if there is data to validate.
    if (tm == null || tm.data == null || tm.data.isEmpty())
      return errorStrings;

    // Iterate through the table data.
    List<Universejoin> us = new ArrayList<Universejoin>();
    for (Object o : tm.data) {
      Universejoin u1 = (Universejoin) o;

      // Replace null values with empty values.
      u1.removeNulls();

      // Verify that mandatory parameters are not empty
      if (u1.getSourcetable().equals("") || u1.getSourcecolumn().equals("") || u1.getTargettable().equals("")
          || u1.getTargetcolumn().equals("") || u1.getVersionid().equals("")) {
        errorStrings.add("No empty values allowed for Source table, Source column, Target table and Target column.");
      }

      // Verify that there are no duplicate primary keys. A duplicate value is
      // detected if all the primary key column values are the same.
      for (Universejoin u2 : us) {
        if (u1.dbEquals(u2)) {
          errorStrings.add("Duplicate entries found with Source table: " + u1.getSourcetable() + ", Source column: "
              + u1.getSourcecolumn() + ", Target table: " + u1.getTargettable() + " and Target column: "
              + u1.getTargetcolumn());
        }
      }

      // Validate the universe extensions:
      for (Universejoin univJoin : us) {
        errorStrings = (Vector<String>)validateExtensions(univJoin.getUniverseextension(), errorStrings);
      }
      us.add(u1);
    }

    return errorStrings;
  }

  /**
   * Validates the universe extensions entered for a join.
   * @param inputString     The extensions entered.
   * @return errorStrings   The vector of error messages, updated with any new error messages.
   */
  public List<String> validateExtensions(String inputString, List<String> errorStrings) {

    extensionsInTP = getExtensions();

    if (inputString != null) {
      Matcher m = ALPHANUMERIC.matcher(inputString);
      if (!m.matches()) {
        final String newError = inputString + " is not a valid input for universe extensions. "
            + "Valid values are ALL, a single extension or a list of extensions separated by \",\"";
        if (!errorStrings.contains(newError)) {
          errorStrings.add(newError);
          return errorStrings;
        }
      }
    }

    if (extensionsInTP.length == 0) {
      return errorStrings;
    }

    if (inputString != null) {
      inputString = inputString.trim();
    }

    if (errorStrings == null) {
      errorStrings = new Vector<String>();
    }

    if (inputString == null || inputString.equals("") || inputString.equalsIgnoreCase("ALL")) {
      return errorStrings;
    } else if (!inputString.contains(",")) {
      errorStrings = validateSingleExt(inputString, errorStrings);
    } else {
      errorStrings = validateListOfExt(inputString, errorStrings);
    }
    return errorStrings;
  }

  /**
   * Validates a single extension.
   * @param inputString   The text entered for the universe extension.
   * @param errorStrings  The list of error strings.
   * @return
   */
  protected List<String> validateSingleExt(final String inputString, final List<String> errorStrings) {
    // Single letter entry. Check that the extension is defined for the tech pack:
    if (!Arrays.asList(extensionsInTP).contains(inputString.toUpperCase())) {
      final String newError = inputString + " is not defined as a universe extension in this tech pack";
      if (!errorStrings.contains(newError)) {
        errorStrings.add(newError);
      }
    }
    return errorStrings;
  }

  /**
   * Validates a list of extensions separated by ",".
   * Updates the list of error string
   * @param inputString   The text entered for the universe extension.
   * @param errorStrings  The list of error strings.
   * @return
   */
  protected List<String> validateListOfExt(final String inputString, final List<String> errorStrings) {
    // Get an array of the extensions entered by the user:
    final String[] extensions = inputString.split(",");
    // Check each one:
    for (String ext : extensions) {
      ext = ext.trim();
      if (!Arrays.asList(extensionsInTP).contains(ext.toUpperCase()) && !ext.toUpperCase().equals("ALL")) {
        final String newError = ext + " is not defined as a universe extension in this tech pack";
        if (!errorStrings.contains(newError)) {
          errorStrings.add(newError);
        }
      }
    }
    return errorStrings;
  }

  /**
   * Gets the universe extensions defined in the tech pack in upper case.
   * @return Universe extensions as a String array.
   */
  protected String[] getExtensions() {
    // Get the extensions defined in the tech pack:
    String[] extensions = null;
    // Store extensions converted to upper case:
    String[] extensionsInUpperCase = new String[] {};
    try {
      extensions = dataModelController.getUniverseTablesDataModel().getUniverseExtensions(
          versioning.getVersionid());
      if (extensions == null) {
        logger.warning("Failed to get universe extensions defined in the tech pack.");
      } else {
        extensionsInUpperCase = new String[extensions.length];
        for (int i=0; i<extensions.length; i++) {
          extensionsInUpperCase[i] = extensions[i].toUpperCase();
        }
      }
    } catch (Exception exc) {
      logger.severe("Error getting extensions defined for universe: " + exc.toString());
    }
    return extensionsInUpperCase;
  }

  /**
   * @return
   */
  public String[] getExtensionsInTP() {
    return extensionsInTP;
  }

  /**
   * @param extensions
   */
  public void setExtensionsInTP(final String[] extensions) {
    this.extensionsInTP = extensions;
  }



}
