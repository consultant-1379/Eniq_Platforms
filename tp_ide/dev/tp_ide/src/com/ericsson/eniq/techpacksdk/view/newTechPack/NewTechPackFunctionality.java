package com.ericsson.eniq.techpacksdk.view.newTechPack;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.repository.dwhrep.Aggregation;
import com.distocraft.dc5000.repository.dwhrep.AggregationFactory;
import com.distocraft.dc5000.repository.dwhrep.Aggregationrule;
import com.distocraft.dc5000.repository.dwhrep.AggregationruleFactory;
import com.distocraft.dc5000.repository.dwhrep.Busyhour;
import com.distocraft.dc5000.repository.dwhrep.BusyhourFactory;
import com.distocraft.dc5000.repository.dwhrep.Busyhourmapping;
import com.distocraft.dc5000.repository.dwhrep.BusyhourmappingFactory;
import com.distocraft.dc5000.repository.dwhrep.Busyhourplaceholders;
import com.distocraft.dc5000.repository.dwhrep.BusyhourplaceholdersFactory;
import com.distocraft.dc5000.repository.dwhrep.Busyhourrankkeys;
import com.distocraft.dc5000.repository.dwhrep.BusyhourrankkeysFactory;
import com.distocraft.dc5000.repository.dwhrep.Busyhoursource;
import com.distocraft.dc5000.repository.dwhrep.BusyhoursourceFactory;
import com.distocraft.dc5000.repository.dwhrep.Dataformat;
import com.distocraft.dc5000.repository.dwhrep.DataformatFactory;
import com.distocraft.dc5000.repository.dwhrep.Dataitem;
import com.distocraft.dc5000.repository.dwhrep.DataitemFactory;
import com.distocraft.dc5000.repository.dwhrep.Defaulttags;
import com.distocraft.dc5000.repository.dwhrep.DefaulttagsFactory;
import com.distocraft.dc5000.repository.dwhrep.Externalstatement;
import com.distocraft.dc5000.repository.dwhrep.ExternalstatementFactory;
import com.distocraft.dc5000.repository.dwhrep.Grouptypes;
import com.distocraft.dc5000.repository.dwhrep.GrouptypesFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementcolumn;
import com.distocraft.dc5000.repository.dwhrep.MeasurementcolumnFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementcounter;
import com.distocraft.dc5000.repository.dwhrep.MeasurementcounterFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementdeltacalcsupport;
import com.distocraft.dc5000.repository.dwhrep.MeasurementdeltacalcsupportFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementkey;
import com.distocraft.dc5000.repository.dwhrep.MeasurementkeyFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementobjbhsupport;
import com.distocraft.dc5000.repository.dwhrep.MeasurementobjbhsupportFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementtable;
import com.distocraft.dc5000.repository.dwhrep.MeasurementtableFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementtype;
import com.distocraft.dc5000.repository.dwhrep.MeasurementtypeFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementtypeclass;
import com.distocraft.dc5000.repository.dwhrep.MeasurementtypeclassFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementvector;
import com.distocraft.dc5000.repository.dwhrep.MeasurementvectorFactory;
import com.distocraft.dc5000.repository.dwhrep.Prompt;
import com.distocraft.dc5000.repository.dwhrep.PromptFactory;
import com.distocraft.dc5000.repository.dwhrep.Promptimplementor;
import com.distocraft.dc5000.repository.dwhrep.PromptimplementorFactory;
import com.distocraft.dc5000.repository.dwhrep.Promptoption;
import com.distocraft.dc5000.repository.dwhrep.PromptoptionFactory;
import com.distocraft.dc5000.repository.dwhrep.Referencecolumn;
import com.distocraft.dc5000.repository.dwhrep.ReferencecolumnFactory;
import com.distocraft.dc5000.repository.dwhrep.Referencetable;
import com.distocraft.dc5000.repository.dwhrep.ReferencetableFactory;
import com.distocraft.dc5000.repository.dwhrep.Supportedvendorrelease;
import com.distocraft.dc5000.repository.dwhrep.SupportedvendorreleaseFactory;
import com.distocraft.dc5000.repository.dwhrep.Techpackdependency;
import com.distocraft.dc5000.repository.dwhrep.Tpactivation;
import com.distocraft.dc5000.repository.dwhrep.TpactivationFactory;
import com.distocraft.dc5000.repository.dwhrep.Transformation;
import com.distocraft.dc5000.repository.dwhrep.TransformationFactory;
import com.distocraft.dc5000.repository.dwhrep.Transformer;
import com.distocraft.dc5000.repository.dwhrep.TransformerFactory;
import com.distocraft.dc5000.repository.dwhrep.Universeclass;
import com.distocraft.dc5000.repository.dwhrep.UniverseclassFactory;
import com.distocraft.dc5000.repository.dwhrep.Universecomputedobject;
import com.distocraft.dc5000.repository.dwhrep.UniversecomputedobjectFactory;
import com.distocraft.dc5000.repository.dwhrep.Universecondition;
import com.distocraft.dc5000.repository.dwhrep.UniverseconditionFactory;
import com.distocraft.dc5000.repository.dwhrep.Universeformulas;
import com.distocraft.dc5000.repository.dwhrep.UniverseformulasFactory;
import com.distocraft.dc5000.repository.dwhrep.Universejoin;
import com.distocraft.dc5000.repository.dwhrep.UniversejoinFactory;
import com.distocraft.dc5000.repository.dwhrep.Universename;
import com.distocraft.dc5000.repository.dwhrep.Universeobject;
import com.distocraft.dc5000.repository.dwhrep.UniverseobjectFactory;
import com.distocraft.dc5000.repository.dwhrep.Universeparameters;
import com.distocraft.dc5000.repository.dwhrep.UniverseparametersFactory;
import com.distocraft.dc5000.repository.dwhrep.Universetable;
import com.distocraft.dc5000.repository.dwhrep.UniversetableFactory;
import com.distocraft.dc5000.repository.dwhrep.Verificationcondition;
import com.distocraft.dc5000.repository.dwhrep.VerificationconditionFactory;
import com.distocraft.dc5000.repository.dwhrep.Verificationobject;
import com.distocraft.dc5000.repository.dwhrep.VerificationobjectFactory;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.component.ExceptionHandler;
import com.ericsson.eniq.techpacksdk.common.Constants;
import com.ericsson.eniq.techpacksdk.common.Utils;

/**
 * This class is used for storing a new techpack to the database. All the
 * database table rows are created based on the techpack information set with
 * createTechPack method. This class can also be used for removing the techpack
 * data from the database.
 * 
 * @author eheijun
 * @author eheitur
 * 
 */
public class NewTechPackFunctionality {

  private static final Logger logger = Logger.getLogger(NewTechPackFunctionality.class.getName());

  RockFactory rock = null;

  /**
   * The versionId of the new techpack
   */
  private String newVersionId = null;

  /**
   * The versionId of the old techpack
   */
  private String oldVersionId = null;

  /**
   * The ENIQ Level of the old techpack
   */
  private String oldEniqLevel = null;
  
  //This stores the latest Version of the TP.
  private HashMap<String, String> latestActiveTPCache = null;

  /*
   * The new techpack information object
   */
  private NewTechPackInfo techpack = null;

  /**
   * Stores the new techpack data to the database.
   * 
   * @param techpack
   *          The new tehcpack information
   * @param rock
   */
  public void createTechPack(final NewTechPackInfo techpack, final RockFactory rock) {
    try {

      this.rock = rock;
      rock.getConnection().setAutoCommit(false);

      // Store the new techpack information obejct.
      this.techpack = techpack;

      // Get the new techpack versionId
      this.newVersionId = techpack.getName() + ":" + techpack.getVersion();

      // Get the old techpack versionId
      this.oldVersionId = techpack.getFromTechPack();

      // Get the ENIQ_LEVEL for the old techpack.
      this.oldEniqLevel = Utils.getTechpackEniqLevel(rock, oldVersionId);

      // Create from exist Tech Pack
      createVersioning();
      logger.log(Level.INFO, "Techpack created to Versioning table");
      createUniversename();
      logger.log(Level.INFO, "Techpack created to UniverseName table");
      createSupportedvendorrelease();
      logger.log(Level.INFO, "Techpack created to SupportedVendorRelease table");
      createExternalstatement();
      logger.log(Level.INFO, "Techpack created to ExternalStatement table");
      createTechpackdependency();
      logger.log(Level.INFO, "Techpack created to TechPackDependency table");
      createMeasurement();
      logger.log(Level.INFO, "Techpack created to Measurements tables");
      createBusyhour();
      logger.log(Level.INFO, "Techpack created to Busyhour tables");
      createAggregation();
      logger.log(Level.INFO, "Techpack created to Aggregation table");
      createAggregationrule();
      logger.log(Level.INFO, "Techpack created to AggregationRule table");
      createPropmtimplementor();
      logger.log(Level.INFO, "Techpack created to PromptImplementor table");
      createPromptoption();
      logger.log(Level.INFO, "Techpack created to PromptOption table");
      createPrompt();
      logger.log(Level.INFO, "Techpack created to Prompt table");
      createData();
      logger.log(Level.INFO, "Techpack created to Data tables");
      createReferencetableAndRererencecolumn();
      logger.log(Level.INFO, "Techpack created to Referencetable and Referencecolumn tables");
      // createReferencecolumn(techpack);
      // logger.log(Level.INFO, "Techpack created to Referencecolumn tables");
      createTransformer();
      logger.log(Level.INFO, "Techpack created to Transformer table");
      createTransformation();
      logger.log(Level.INFO, "Techpack created to Transformation table");
      createUniverseclass();
      logger.log(Level.INFO, "Techpack created to Universeclass table");
      createUniversetable();
      logger.log(Level.INFO, "Techpack created to Universetable table");
      createUniversejoin();
      logger.log(Level.INFO, "Techpack created to Universejoin table");
      createUniverseobject();
      logger.log(Level.INFO, "Techpack created to Universeobject table");
      createUniversecomputedobject();
      logger.log(Level.INFO, "Techpack created to Universecomputedobject table");
      createUniversecondition();
      logger.log(Level.INFO, "Techpack created to Universecondition table");
      createUniverseformulas();
      logger.log(Level.INFO, "Techpack created to Universeformulas table");
      createUniverseparameters();
      logger.log(Level.INFO, "Techpack created to Universeparameters table");
      createVerificationCondition();
      logger.log(Level.INFO, "Techpack created to Verificationcondition table");
      createVerificationObject();
      logger.log(Level.INFO, "Techpack created to Verificationobject table");
			createGrouptypes();
			logger.log(Level.INFO, "Techpack created to Grouptypes table");
      rock.getConnection().commit();
    } catch (final Exception e) {
      try {
        rock.getConnection().rollback();
      } catch (final Exception ex) {
        ExceptionHandler.instance().handle(ex);
        ex.printStackTrace();
      }
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    } finally {
      try {
        rock.getConnection().setAutoCommit(true);
      } catch (final SQLException ey) {

        ExceptionHandler.instance().handle(ey);
        ey.printStackTrace();
      }
    }
  }

	/**
	 * IDE has no way of modifying this info so its a matter of manually updating the Grouptypes table if
	 * you want to change/add grouptypes
	 */
	private void createGrouptypes(){
		try{
			final Grouptypes where = new Grouptypes(rock);
			where.setVersionid(oldVersionId);
			final GrouptypesFactory fac = new GrouptypesFactory(rock, where);
			final List<Grouptypes> toCopy = fac.get();
			for(Grouptypes oldType : toCopy){
				final Grouptypes _new = new Grouptypes(rock);
				_new.setVersionid(newVersionId);
				_new.setGrouptype(oldType.getGrouptype());
				_new.setDataname(oldType.getDataname());
				_new.setDatatype(oldType.getDatatype());
				_new.setDatasize(oldType.getDatasize());
				_new.setDatascale(oldType.getDatascale());
        _new.setNullable(oldType.getNullable());
				_new.saveDB();
			}
		} catch (Throwable e){
			logger.log(Level.SEVERE, "SQL error in create Grouptypes", e);
		}
	}

  /**
   * Remove the techpack data from the database.
   * 
   * @param versionid
   *          The version of the techpack.
   * @param rock
   */
  public void removeTechPack(final String versionid, final RockFactory rock) {

    try {
      this.rock = rock;
      rock.getConnection().setAutoCommit(false);

      // Removes Tech Pack rows from tables:

      removeVerificationObject(versionid);
      logger.log(Level.INFO, "Techpack removed from VerificationObject table");

      removeVerificationCondition(versionid);
      logger.log(Level.INFO, "Techpack removed from VerificationCondition table");

      removeExternalstatement(versionid); // ExternalStatement
      logger.log(Level.INFO, "Techpack removed from ExternalStatement table");

      removeUniverseformulas(versionid); // Universeformulas
      logger.log(Level.INFO, "Techpack removed from Universeformulas table");

      removeUniverseparameters(versionid); // Universejoin
      logger.log(Level.INFO, "Techpack removed from Universeparameters table");

      removeUniversejoin(versionid); // Universejoin
      logger.log(Level.INFO, "Techpack removed from UniverseJoin table");

      removeUniverseobject(versionid); // Universeobject
      logger.log(Level.INFO, "Techpack removed from UniverseObject table");

      removeUniversecomputedobject(versionid); // Universecomputedobject
      logger.log(Level.INFO, "Techpack removed from UniverseComputedObject table");

      removeUniversecondition(versionid); // Universecondition
      logger.log(Level.INFO, "Techpack removed from UniverseCondition table");

      removeUniversetable(versionid); // Universetable
      logger.log(Level.INFO, "Techpack removed from UniverseTable");

      removeUniverseclass(versionid); // Universeclass
      logger.log(Level.INFO, "Techpack removed from UniverseClass");

      removeUniversename(versionid); // Universename
      logger.log(Level.INFO, "Techpack removed from UniverseName table");

      removeTechpackdependency(versionid); // Techpackdependency
      logger.log(Level.INFO, "Techpack removed from TechPackDependency table");

      removeAggregationrule(versionid); // AggregationRule
      logger.log(Level.INFO, "Techpack removed from AggregationRule table");

      removeAggregation(versionid); // Aggregation
      logger.log(Level.INFO, "Techpack removed from Aggregation table");

      removeData(versionid); // DataItem, DefaultTags DataFormat
      logger.log(Level.INFO, "Techpack removed from Data tables");

      removePropmt(versionid); // Prompt
      logger.log(Level.INFO, "Techpack removed from Prompt table");

      removePropmtoption(versionid); // Promptoption
      logger.log(Level.INFO, "Techpack removed from PromptOption table");

      removePropmtimplementor(versionid); // Promptimplementor
      logger.log(Level.INFO, "Techpack removed from PromptImplementor table");

      removeReferences(versionid); // ReferenceColumn, ReferenceTable
      logger.log(Level.INFO, "Techpack removed from Reference tables");

      removeTransforms(versionid); // Transformation,Transformer
      logger.log(Level.INFO, "Techpack removed from Transformation tables");

      removeBusyhour(versionid);
      logger.log(Level.INFO, "Techpack removed from Busyhour tables");

      removeMeasurement(versionid); // MeasurementTable, MeasurementCounter,
      // MeasurementDeltaCalcSupport, MeasurementKey, MeasurementColumn,
      // MeasurementType, MeasurementTypeClass and Measurementvector
      logger.log(Level.INFO, "Techpack removed from Measurements tables");

      removeSupportedvendorrelease(versionid); // Supportedvendorrelease
      logger.log(Level.INFO, "Techpack removed from SupportedVendorRelease table");

			//Need to do the Grouptypes before Versioning, fk dependancy.
			removeGrouptypes(versionid);
			logger.log(Level.INFO, "Techpack removed from Grouptypes table");

      removeVersioning(versionid); // Versioning
      logger.log(Level.INFO, "Techpack removed from VERSIONING table");

      rock.getConnection().commit();

    } catch (final Exception e) {
      try {
        rock.getConnection().rollback();
      } catch (final Exception ex) {
        ExceptionHandler.instance().handle(ex);
        ex.printStackTrace();
      }
    } finally {
      try {
        rock.getConnection().setAutoCommit(true);
      } catch (final SQLException ey) {
        ExceptionHandler.instance().handle(ey);
        ey.printStackTrace();
      }
    }
  }

  /*****************************************************************************
   * public void createExternalstatement(String versionid){
   ****************************************************************************/
  private void createExternalstatement() {
    try {

      long execOrder = 0;
      long deExecOrder = 0;

      // If the current techpack is not a base techpack and the base has been
      // defined, then copy the (de)external statements from the base techpack.
      if (!techpack.getType().equalsIgnoreCase("base")
          && (techpack.getBaseDefinition() != null && techpack.getBaseDefinition().length() > 0)) {

        // Get the base version
        final String baseVersionId = techpack.getBaseDefinition();

        // Copy the statements only if the base has been selected.
        if (baseVersionId != null && baseVersionId.length() > 0) {

          final Externalstatement es = new Externalstatement(rock);
          es.setVersionid(baseVersionId);
          final ExternalstatementFactory esF = new ExternalstatementFactory(rock, es, "ORDER BY EXECUTIONORDER");
          final Iterator<Externalstatement> iter = esF.get().iterator();

          // Iterate through all the statements in the base techpack and store
          // them as statements for the current techpack.
          while (iter.hasNext()) {
            final Externalstatement tmpEs = iter.next();

            String statementName = tmpEs.getStatementname();
            String statement = tmpEs.getStatement();

            // Special handling for the external statement
            // 'create view SELECT_(((TPName)))_AGGLEVEL': Replace the
            // (((TPNAME))) with the current techpack (without 'DC_').
            if (tmpEs.getStatementname().equals("create view SELECT_(((TPName)))_AGGLEVEL")) {
              final String tpName = techpack.getName().replace("DC_", "");
              statementName = statementName.replace("(((TPName)))", tpName);
              statement = statement.replace("(((TPName)))", tpName);
            }

            // Create the new base statement
            final Externalstatement newEs = new Externalstatement(rock);
            newEs.setVersionid(newVersionId);
            newEs.setStatementname(statementName);
            newEs.setDbconnection(tmpEs.getDbconnection());
            newEs.setStatement(statement);
            newEs.setBasedef(1);

            // Execution order is handled separately for external statements
            // (positive) and de-external statements (negative).
            if (tmpEs.getExecutionorder() > 0) {
              execOrder = execOrder + 1;
              newEs.setExecutionorder(execOrder);
            } else {
              deExecOrder = deExecOrder - 1;
              newEs.setExecutionorder(deExecOrder);
            }

            // Save the new statement
            newEs.saveDB();
          }
        } else {
          logger
              .log(Level.FINE, "Base techpack has not been defined, so external statements are not copied from base.");
        }
      }

      // If the new techpack is based on an existing techpack, then copy the
      // (de)external statements from the existing techpack to the new one.
      // NOTE: Existing statements from base techpack are ignore here.
      if (techpack.getFromTechPack() != "") {

        // Iterate through all (de)external statements in the old techpack.
        Externalstatement es = new Externalstatement(rock);
        es.setVersionid(oldVersionId);
        final ExternalstatementFactory esf = new ExternalstatementFactory(rock, es, "ORDER BY EXECUTIONORDER");
        final Iterator<Externalstatement> ies = esf.get().iterator();
        while (ies.hasNext()) {

          es = ies.next();
          // The basedef value might be 0 for normal statement, 1 for base
          // techpack statement, or null for statements from a migrated
          // techpack. Ignore base techpack statements.
          if (!(es.getBasedef() != null && es.getBasedef().equals(1))) {

            // In case there is a base defined, a special handling for the
            // external statement 'create view SELECT_(((TPName)))_AGGLEVEL': Do
            // not copy this one, since it was copied from base techpack.
            if (techpack.getBaseDefinition() != null && techpack.getBaseDefinition().length() > 0) {
              if (es.getStatementname().contains("create view SELECT_") && es.getStatementname().contains("_AGGLEVEL")) {
                logger.log(Level.FINE, "External statement: " + es.getStatementname()
                    + " not copied from old techpack, since it was already copied from base techpack.");
                continue;
              }
            }
            // Create the new external statement
            final Externalstatement tmpes = new Externalstatement(rock);
            tmpes.setVersionid(newVersionId);
            tmpes.setStatementname(es.getStatementname());
            tmpes.setExecutionorder(es.getExecutionorder());
            tmpes.setDbconnection(es.getDbconnection());
            tmpes.setStatement(es.getStatement());
            tmpes.setBasedef(0);

            // Execution order is handled separately for external statements
            // (positive) and de-external statements (negative).
            if (es.getExecutionorder() > 0) {
              execOrder = execOrder + 1;
              tmpes.setExecutionorder(execOrder);
            } else {
              deExecOrder = deExecOrder - 1;
              tmpes.setExecutionorder(deExecOrder);
            }

            tmpes.saveDB();
          }

        }
      }
    } catch (final Exception e) {
      logger.log(Level.SEVERE, "SQL error in create Externalstatement", e);
      System.out.println(e);
    }
  }

	private void removeGrouptypes(final String versionid){
		final Grouptypes gt = new Grouptypes(rock);
		gt.setVersionid(versionid);
		try{
			gt.deleteDB();
		} catch (final Exception e) {
      logger.log(Level.SEVERE, "SQL error in remove Grouptypes", e);
      System.out.println(e);
    }
	}

  /*****************************************************************************
   * public void removeVerificationObject(String versionid){
   ****************************************************************************/
  private void removeVerificationObject(final String versionid) {
    try {
      // Remove TP content from VerificationObject table
      final Verificationobject vo = new Verificationobject(rock);
      vo.setVersionid(versionid);
      vo.deleteDB();
    } catch (final Exception e) {
      logger.log(Level.SEVERE, "SQL error in remove VerificationObject", e);
      System.out.println(e);
    }
  }

  /*****************************************************************************
   * public void removeVerificationCondition(String versionid){
   ****************************************************************************/
  private void removeVerificationCondition(final String versionid) {
    try {
      // Remove TP content from VerificationCondition table
      final Verificationcondition vc = new Verificationcondition(rock);
      vc.setVersionid(versionid);
      vc.deleteDB();
    } catch (final Exception e) {
      logger.log(Level.SEVERE, "SQL error in remove VerificationCondition", e);
      System.out.println(e);
    }
  }

  /*****************************************************************************
   * public void removeExternalstatement(String versionid){
   ****************************************************************************/
  private void removeExternalstatement(final String versionid) {
    try {
      // Remove TP content from Externalstatement table
      final Externalstatement exs = new Externalstatement(rock);
      exs.setVersionid(versionid);
      exs.deleteDB();
    } catch (final Exception e) {
      logger.log(Level.SEVERE, "SQL error in remove Externalstatement", e);
      System.out.println(e);
    }
  }

  /*****************************************************************************
   * public void createSupportedvendorrelease(TechPackInfo techpack){
   ****************************************************************************/
  private void createSupportedvendorrelease() {
    try {
      final String versionid = techpack.getName() + ":" + techpack.getVersion();

      // Insert content to Supportedvendorrelease table:

      final List Vendorrelease = techpack.getVendorRelease();

      final int rows = Vendorrelease.size();

      for (int v = 0; v < rows; v++) {
        final Supportedvendorrelease svr = new Supportedvendorrelease(rock);
        svr.setVersionid(versionid);
        svr.setVendorrelease(Vendorrelease.get(v).toString());
        svr.saveDB();
      }
    } catch (final Exception e) {
      logger.log(Level.SEVERE, "SQL error in remove Externalstatement", e);
      System.out.println(e);
    }
  }

  /*****************************************************************************
   * public void removeSupportedvendorrelease(String versionid){
   ****************************************************************************/
  private void removeSupportedvendorrelease(final String versionid) {
    try {
      // Remove TP content from Supportedvendorrelease table
      final Supportedvendorrelease svr = new Supportedvendorrelease(rock);
      svr.setVersionid(versionid);
      svr.deleteDB();
    } catch (final Exception e) {
      logger.log(Level.SEVERE, "SQL error in remove Supportedvendorrelease", e);
      System.out.println(e);
    }
  }

  /*****************************************************************************
   * public void removeSupportedvendorrelease(String typeid, String
   * vendorrelease, String versionid){
   ****************************************************************************/
  private void removeMeasurementDeltaCalcSupport(final String versionid) {
    try {

      // Remove TP content from MeasurementDeltaCalcSupport table
      final Measurementdeltacalcsupport mdcs = new Measurementdeltacalcsupport(rock);
      mdcs.setVersionid(versionid);
      mdcs.deleteDB();

    } catch (final Exception e) {
      logger.log(Level.SEVERE, "SQL error in remove Supportedvendorrelease", e);
      System.out.println(e);
    }

  }

  /*****************************************************************************
   * public void createUniversename(TechPackInfo techpack){
   ****************************************************************************/
  private void createUniversename() {
    try {
      final String versionid = techpack.getName() + ":" + techpack.getVersion();

      final List unvextlist = techpack.getUnvExt();

      int rows = unvextlist.size();
      boolean tmpExt = false;

      if ((rows == 0) && (techpack.getUnvName() != "")) {
        tmpExt = true;
        rows = 1; // Add row to table, but no universe extension
      }

      for (int i = 0; i < rows; i++) {
        // Insert content to Universename table:
        final Universename unvname = new Universename(rock);

        unvname.setVersionid(versionid);
        unvname.setUniversename(techpack.getUnvName());
        if (tmpExt) {
          unvname.setUniverseextension("");
          unvname.setUniverseextensionname("");
        } else {
          final List trow = (List) unvextlist.get(i);
          unvname.setUniverseextension((String) trow.get(0));
          unvname.setUniverseextensionname((String) trow.get(1));
        }
        unvname.saveDB();
      }
    } catch (final Exception e) {
      logger.log(Level.SEVERE, "SQL error in create Universename", e);
      System.out.println(e);
    }
  }

  /*****************************************************************************
   * public void removeUniversename(String versionid){
   ****************************************************************************/
  private void removeUniversename(final String versionid) {
    try {
      // Remove TP content from Universename table
      final Universename un = new Universename(rock);
      un.setVersionid(versionid);
      un.deleteDB();
    } catch (final Exception e) {
      logger.log(Level.SEVERE, "SQL error in remove Universename", e);
      System.out.println(e);
    }
  }

  /*****************************************************************************
   * public void removeUniversejoin(String versionid){
   ****************************************************************************/
  private void removeUniversejoin(final String versionid) {
    try {
      // Remove TP content from Universename table
      final Universejoin un = new Universejoin(rock);
      un.setVersionid(versionid);
      un.deleteDB();
    } catch (final Exception e) {
      logger.log(Level.SEVERE, "SQL error in remove Universejoin", e);
      System.out.println(e);
    }
  }

  /*****************************************************************************
   * public void removeUniverseformulas(String versionid){
   ****************************************************************************/
  private void removeUniverseformulas(final String versionid) {
    try {
      // Remove TP content from Universeformulas table
      final Universeformulas uf = new Universeformulas(rock);
      uf.setVersionid(versionid);
      uf.deleteDB();
    } catch (final Exception e) {
      logger.log(Level.SEVERE, "SQL error in remove Universeformulas", e);
      System.out.println(e);
    }
  }

  /*****************************************************************************
   * public void removeUniversejoin(String versionid){
   ****************************************************************************/
  private void removeUniverseparameters(final String versionid) {
    try {
      // Remove TP content from Universename table
      final Universeparameters un = new Universeparameters(rock);
      un.setVersionid(versionid);
      un.deleteDB();
    } catch (final Exception e) {
      logger.log(Level.SEVERE, "SQL error in remove Universeparameters", e);
      System.out.println(e);
    }
  }

  /*****************************************************************************
   * public void removeUniverseobject(String versionid){
   ****************************************************************************/
  private void removeUniverseobject(final String versionid) {
    try {
      // Remove TP content from Universeobject table
      final Universeobject uo = new Universeobject(rock);
      uo.setVersionid(versionid);
      uo.deleteDB();
    } catch (final Exception e) {
      logger.log(Level.SEVERE, "SQL error in remove Universeobject", e);
      System.out.println(e);
    }
  }

  /*****************************************************************************
   * public void removeUniversecomputedobject(String versionid){
   ****************************************************************************/
  private void removeUniversecomputedobject(final String versionid) {
    try {
      // Remove TP content from Universecomputedobject table
      final Universecomputedobject uco = new Universecomputedobject(rock);
      uco.setVersionid(versionid);
      uco.deleteDB();
    } catch (final Exception e) {
      logger.log(Level.SEVERE, "SQL error in remove Universecomputedobject", e);
      System.out.println(e);
    }
  }

  /*****************************************************************************
   * public void removeUniversecondition(String versionid){
   ****************************************************************************/
  private void removeUniversecondition(final String versionid) {
    try {
      // Remove TP content from Universecondition table
      final Universecondition uc = new Universecondition(rock);
      uc.setVersionid(versionid);
      uc.deleteDB();
    } catch (final Exception e) {
      logger.log(Level.SEVERE, "SQL error in remove Universecondition", e);
      System.out.println(e);
    }
  }

  /*****************************************************************************
   * public void removeUniversetable(String versionid){
   ****************************************************************************/
  private void removeUniversetable(final String versionid) {
    try {
      // Remove TP content from Universetable table
      final Universetable ut = new Universetable(rock);
      ut.setVersionid(versionid);
      ut.deleteDB();
    } catch (final Exception e) {
      logger.log(Level.SEVERE, "SQL error in remove Universetable", e);
      System.out.println(e);
    }
  }

  /*****************************************************************************
   * public void removeUniverseclass(String versionid){
   ****************************************************************************/
  private void removeUniverseclass(final String versionid) {
    try {
      // Remove TP content from Universeclass table
      final Universeclass ucl = new Universeclass(rock);
      ucl.setVersionid(versionid);
      ucl.deleteDB();
    } catch (final Exception e) {
      logger.log(Level.SEVERE, "SQL error in remove Universeclass", e);
      System.out.println(e);
    }
  }

  /*****************************************************************************
   * public void createTechpackdependency(TechPackInfo techpack){
   ****************************************************************************/
  private void createTechpackdependency() {
    try {

      final List tpdep = techpack.getTechPackDep();

      final int rows = tpdep.size();
      for (int i = 0; i < rows; i++) {

        // Insert content to Techpackdependency table:
        final Techpackdependency tpd = new Techpackdependency(rock);

        tpd.setVersionid(newVersionId);
        tpd.setTechpackname(tpdep.get(i).toString());
        tpd.setVersion(tpdep.get(i + 1).toString());
        tpd.saveDB();
        i++;
      }
    } catch (final Exception e) {
      logger.log(Level.SEVERE, "SQL error in create Techpackdependency", e);
      System.out.println(e);
    }
  }

  /*****************************************************************************
   * public void removeTechpackdependency(String versionid){
   ****************************************************************************/
  private void removeTechpackdependency(final String versionid) {
    try {
      // Remove TP content from Techpackdependency table
      final Techpackdependency tpd = new Techpackdependency(rock);
      tpd.setVersionid(versionid);
      tpd.deleteDB();
    } catch (final Exception e) {
      logger.log(Level.SEVERE, "SQL error in remove Techpackdependency", e);
      System.out.println(e);
    }
  }

  /*****************************************************************************
   * public void createAggregation(TechPackInfo techpack){
   ****************************************************************************/
  private void createAggregation() {

    try {

      final Aggregation aggCond = new Aggregation(rock);
      aggCond.setVersionid(oldVersionId);
      final AggregationFactory af = new AggregationFactory(rock, aggCond);
      for (final Aggregation a : af.get()) {

        // If busy hour conversion is needed, then skip the RANKBH aggregations.
        if (isBusyHourConversionNeeded()) {
          if (a.getAggregationtype().equalsIgnoreCase(Constants.RANKBHLEVEL)) {
            continue;
          }
        }

        // Insert content to Aggregation table
        final Aggregation newAgg = new Aggregation(rock);

        newAgg.setVersionid(newVersionId);
        newAgg.setAggregation(a.getAggregation());
        newAgg.setAggregationset(a.getAggregationset());
        newAgg.setAggregationgroup(a.getAggregationgroup());
        newAgg.setReaggregationset(a.getReaggregationset());
        newAgg.setReaggregationgroup(a.getReaggregationgroup());
        newAgg.setGrouporder(a.getGrouporder());
        newAgg.setAggregationorder(a.getAggregationorder());
        newAgg.setAggregationtype(a.getAggregationtype());
        newAgg.setAggregationscope(a.getAggregationscope());
        newAgg.saveDB();
      }
    } catch (final Exception e) {
      logger.log(Level.SEVERE, "SQL error in create Aggregation", e);
      System.out.println(e);
    }

  }

  /*****************************************************************************
   * public void removeAggregation(String versionid){
   ****************************************************************************/
  private void removeAggregation(final String versionid) {
    try {
      // Remove TP content from Aggregation table
      final Aggregation agg = new Aggregation(rock);
      agg.setVersionid(versionid);
      agg.deleteDB();
    } catch (final Exception e) {
      logger.log(Level.SEVERE, "SQL error in remove Aggregation", e);
      System.out.println(e);
    }
  }

  /*****************************************************************************
   * public void createAggregationrule(TechPackInfo techpack){
   ****************************************************************************/
  private void createAggregationrule() {
    try {
      String tmpSource = "";
      String tmpTarget = "";

      final Aggregationrule arCond = new Aggregationrule(rock);
      arCond.setVersionid(oldVersionId);
      final AggregationruleFactory arf = new AggregationruleFactory(rock, arCond);
      for (final Aggregationrule ar : arf.get()) {

        // If busy hour conversion is needed, then skip the RANKBH aggregation
        // rules.
        if (isBusyHourConversionNeeded()) {
          if (ar.getRuletype().equalsIgnoreCase(Constants.RANKBHLEVEL)
              || ar.getRuletype().equalsIgnoreCase(Constants.RANKBHCLASSLEVEL)) {
            continue;
          }
        }

        // Insert content to Aggregationrule table
        final Aggregationrule tmpar = new Aggregationrule(rock);
        tmpar.setVersionid(newVersionId);
        tmpar.setAggregation(ar.getAggregation());
        tmpar.setRuleid(ar.getRuleid());
        tmpar.setRuletype(ar.getRuletype());
        tmpar.setAggregationscope(ar.getAggregationscope());
        tmpar.setBhtype(ar.getBhtype());
        tmpar.setSource_level(ar.getSource_level());
        
        //Get the target version id:
        final String oldTargetmTableId = ar.getTarget_mtableid();
        final String[] splits = oldTargetmTableId.split(":");
        final String oldTargetVersionId = splits[0] + ":" + splits[1];
        String newTargetVersionId = null;
        if (oldTargetVersionId.equals(oldVersionId)) {
        	newTargetVersionId = newVersionId;
        } else {
            // For CUSTOM TPs use latest Active
            newTargetVersionId = getLatestActiveTP(oldTargetVersionId);
        }
        
        // Source_mtableid =VERSIOIID:SOURCE_TYPE:SOURCE_LEVEL
        tmpSource = newTargetVersionId + Constants.TYPESEPARATOR + ar.getSource_type() + Constants.TYPESEPARATOR
            + ar.getSource_level();
        tmpar.setSource_mtableid(tmpSource);
        tmpar.setSource_table(ar.getSource_table());
        tmpar.setSource_type(ar.getSource_type());
        tmpar.setTarget_level(ar.getTarget_level());
        
        // Target_mtableid =VERSIOIID:TARGET_TYPE:TARGET_LEVEL
        tmpTarget = newTargetVersionId + Constants.TYPESEPARATOR + ar.getTarget_type() + Constants.TYPESEPARATOR
            + ar.getTarget_level();
        tmpar.setTarget_mtableid(tmpTarget);
        tmpar.setTarget_table(ar.getTarget_table());
        tmpar.setTarget_type(ar.getTarget_type());
        tmpar.saveDB();
      }
    } catch (final Exception e) {
      logger.log(Level.SEVERE, "SQL error in create Aggregationrule", e);
      System.out.println(e);
    }
  }

  /*****************************************************************************
   * public void removeAggregationrule(String versionid){
   ****************************************************************************/
  private void removeAggregationrule(final String versionid) {
    try {
      // Remove TP content from Aggregationrule table
      final Aggregationrule agr = new Aggregationrule(rock);
      agr.setVersionid(versionid);
      agr.deleteDB();
    } catch (final Exception e) {
      logger.log(Level.SEVERE, "SQL error in remove Aggregationrule", e);
      System.out.println(e);
    }
  }

  /*****************************************************************************
   * public void createVersioning(String versionid){
   ****************************************************************************/
  private void createVersioning() {
    try {
      final Long status = 1L;

      // Insert content to Versioning table:
      final Versioning ver = new Versioning(rock);
      ver.setVersionid(newVersionId);
      ver.setDescription(techpack.getDescription());
      ver.setStatus(status);
      ver.setTechpack_name(techpack.getName());
      ver.setTechpack_version(techpack.getRstate());
      ver.setLicensename(techpack.getLicenseName());
      ver.setTechpack_type(techpack.getType());
      ver.setProduct_number(techpack.getProduct());
      ver.setBasedefinition(techpack.getBaseDefinition());
      ver.setInstalldescription(techpack.getInstallDescription());
      ver.setLockedby(techpack.getUserName());
      ver.setLockdate(new Timestamp(System.currentTimeMillis()));
      ver.setEniq_level(Constants.CURRENT_TECHPACK_ENIQ_LEVEL);
      ver.saveDB();
    } catch (final Exception e) {
      logger.log(Level.SEVERE, "SQL error in create Versioning", e);
      System.out.println(e);
    }
  }

  /*****************************************************************************
   * public void removeVersioning(String versionid){
   ****************************************************************************/
  private void removeVersioning(final String versionid) {
    try {
      // Remove TP content from Versioning table
      final Versioning ver = new Versioning(rock);
      ver.setVersionid(versionid);
      ver.deleteDB();
    } catch (final Exception e) {
      logger.log(Level.SEVERE, "SQL error in remove Versioning", e);
      System.out.println(e);
    }
  }

  /*****************************************************************************
   * public void createPrompt(TechPackInfo techpack){
   ****************************************************************************/
  private void createPrompt() {
    try {

      Prompt p = new Prompt(rock);

      p.setVersionid(oldVersionId);

      final PromptFactory pf = new PromptFactory(rock, p);

      final Iterator<Prompt> ip = pf.get().iterator();

      // Insert content to Prompt table
      while (ip.hasNext()) {
        p = ip.next();
        final Prompt tmpp = new Prompt(rock);

        // Create TP content to Propmpt table
        tmpp.setVersionid(newVersionId);
        tmpp.setPromptimplementorid(p.getPromptimplementorid());
        tmpp.setPromptname(p.getPromptname());
        tmpp.setOrdernumber(p.getOrdernumber());
        tmpp.setUnrefreshable(p.getUnrefreshable());
        tmpp.saveDB();
      }
    } catch (final Exception e) {
      logger.log(Level.SEVERE, "SQL error in create Prompt", e);
      System.out.println(e);
    }
  }

  /*****************************************************************************
   * public void removePrompt(String versionid){
   ****************************************************************************/
  private void removePropmt(final String versionid) {
    try {
      // Remove TP content from Propmpt table
      final Prompt p = new Prompt(rock);
      p.setVersionid(versionid);
      p.deleteDB();
    } catch (final Exception e) {
      logger.log(Level.SEVERE, "SQL error in remove Prompt", e);
      System.out.println(e);
    }
  }

  /*****************************************************************************
   * public void createPromptoption(TechPackInfo techpack){
   ****************************************************************************/
  private void createPromptoption() {
    try {

      Promptoption po = new Promptoption(rock);

      po.setVersionid(oldVersionId);

      final PromptoptionFactory pof = new PromptoptionFactory(rock, po);

      final Iterator<Promptoption> ipo = pof.get().iterator();

      // Insert content to Promptoption table
      while (ipo.hasNext()) {
        po = ipo.next();
        final Promptoption tmppo = new Promptoption(rock);

        // Create TP content to Propmptoption table
        tmppo.setVersionid(newVersionId);
        tmppo.setPromptimplementorid(po.getPromptimplementorid());
        tmppo.setOptionname(po.getOptionname());
        tmppo.setOptionvalue(po.getOptionvalue());
        tmppo.saveDB();
      }
    } catch (final Exception e) {
      logger.log(Level.SEVERE, "SQL error in create PromptOption", e);
      System.out.println(e);
    }
  }

  /*****************************************************************************
   * public void removePromptoption(String versionid){
   ****************************************************************************/
  private void removePropmtoption(final String versionid) {
    try {
      // Remove TP content from Propmptoption table
      final Promptoption po = new Promptoption(rock);
      po.setVersionid(versionid);
      po.deleteDB();
    } catch (final Exception e) {
      logger.log(Level.SEVERE, "SQL error in remove Propmtoption", e);
      System.out.println(e);
    }
  }

  /*****************************************************************************
   * public void createPropmtimplementor(TechPackInfo techpack){
   ****************************************************************************/
  private void createPropmtimplementor() {
    try {

      Promptimplementor pi = new Promptimplementor(rock);

      pi.setVersionid(oldVersionId);

      final PromptimplementorFactory pif = new PromptimplementorFactory(rock, pi);

      final Iterator<Promptimplementor> ipi = pif.get().iterator();

      // Insert content to Promptimplementor table
      while (ipi.hasNext()) {
        pi = ipi.next();
        final Promptimplementor tmppi = new Promptimplementor(rock);

        // Create TP content to Promptimplementor table
        tmppi.setVersionid(newVersionId);
        tmppi.setPromptimplementorid(pi.getPromptimplementorid());
        tmppi.setPromptclassname(pi.getPromptclassname());
        tmppi.setPriority(pi.getPriority());
        tmppi.saveDB();
      }
    } catch (final Exception e) {
      logger.log(Level.SEVERE, "SQL error in create Propmtimplementor", e);
      System.out.println(e);
    }
  }

  /*****************************************************************************
   * public void removePropmtimplementor(String versionid){
   ****************************************************************************/
  private void removePropmtimplementor(final String versionid) {
    try {
      // Remove TP content from Propmpt table
      final Promptimplementor pi = new Promptimplementor(rock);
      pi.setVersionid(versionid);
      pi.deleteDB();
    } catch (final Exception e) {
      logger.log(Level.SEVERE, "SQL error in remove Propmtimplementor", e);
      System.out.println(e);
    }
  }

  /*****************************************************************************
   * public void createMeasurement(TechPackInfo techpack)
   ****************************************************************************/

  private void createMeasurement() {

    try {
      final String basedef = techpack.getBaseDefinition();

      Measurementtypeclass mclass = new Measurementtypeclass(rock);
      mclass.setVersionid(oldVersionId);
      final MeasurementtypeclassFactory mtctf = new MeasurementtypeclassFactory(rock, mclass);
      final Iterator<Measurementtypeclass> imclass = mtctf.get().iterator();

      // Measurementtypeclass
      while (imclass.hasNext()) {

        mclass = imclass.next();

        final String newtypeclassid = mclass.getTypeclassid().replace(oldVersionId, newVersionId);

        final Measurementtypeclass tmpmclass = new Measurementtypeclass(rock);

        // Insert content to Measurementtypeclass table
        tmpmclass.setVersionid(newVersionId);
        tmpmclass.setTypeclassid(newtypeclassid);
        tmpmclass.setDescription(mclass.getDescription());
        tmpmclass.saveDB();

        Measurementtype mtype = new Measurementtype(rock);
        mtype.setTypeclassid(mclass.getTypeclassid());
        final MeasurementtypeFactory mtypef = new MeasurementtypeFactory(rock, mtype);
        final Iterator<Measurementtype> mtypeiter = mtypef.get().iterator(); // Measurementtype
        // iterator

        // Measurementtype
        while (mtypeiter.hasNext()) {

          mtype = mtypeiter.next();

          final String typeid = newVersionId + ":" + mtype.getTypename();
          final String typeclassid = mtype.getTypeclassid().replace(oldVersionId, newVersionId);

          final Measurementtype newType = new Measurementtype(rock);

          newType.setTypeid(typeid);
          newType.setTypeclassid(typeclassid);
          newType.setTypename(mtype.getTypename());
          newType.setVendorid(mtype.getVendorid());
          newType.setFoldername(mtype.getFoldername());
          newType.setDescription(mtype.getDescription());
          newType.setStatus(mtype.getStatus());
          newType.setVersionid(newVersionId);
          newType.setObjectid(typeid);
          newType.setObjectname(mtype.getObjectname());
          newType.setObjectversion(mtype.getObjectversion());
          newType.setObjecttype(mtype.getObjecttype());
          newType.setSizing(mtype.getSizing());
          newType.setRankingtable(mtype.getRankingtable());
          newType.setPlaintable(mtype.getPlaintable());
          newType.setDeltacalcsupport(mtype.getDeltacalcsupport());
          newType.setVectorsupport(mtype.getVectorsupport());
          newType.setTotalagg(mtype.getTotalagg());
          newType.setSonagg(mtype.getSonagg());;
          newType.setSonfifteenminagg(mtype.getSonfifteenminagg());;
          newType.setElementbhsupport(mtype.getElementbhsupport());
          newType.setDataformatsupport(mtype.getDataformatsupport());
          newType.setUniverseextension(mtype.getUniverseextension());
          newType.setJoinable(mtype.getJoinable());
          newType.setRopgrpcell(mtype.getRopgrpcell());
          newType.setOneminagg(mtype.getOneminagg());
          newType.setFifteenminagg(mtype.getFifteenminagg());
          newType.setMixedpartitionstable(mtype.getMixedpartitionstable());
          newType.setEventscalctable(mtype.getEventscalctable());
          newType.setLoadfile_dup_check(mtype.getLoadfile_dup_check());
          newType.saveDB();

          // Measurementkey
          final Measurementkey mkey = new Measurementkey(rock);
          mkey.setTypeid(mtype.getTypeid());
          final MeasurementkeyFactory mkeyf = new MeasurementkeyFactory(rock, mkey);
          final Iterator<Measurementkey> imkeyf = mkeyf.get().iterator();

          while (imkeyf.hasNext()) {
            final Measurementkey key = imkeyf.next();

            final Measurementkey newKey = new Measurementkey(rock);
            final String newtypeid = key.getTypeid().replace(oldVersionId, newVersionId);

            newKey.setTypeid(newtypeid);
            newKey.setDataname(key.getDataname());
            newKey.setDescription(key.getDescription());
            newKey.setIselement(key.getIselement());
            newKey.setUniquekey(key.getUniquekey());
            newKey.setColnumber(key.getColnumber());
            newKey.setDatatype(key.getDatatype());
            newKey.setDatasize(key.getDatasize());
            newKey.setDatascale(key.getDatascale());
            newKey.setUniquevalue(key.getUniquevalue());
            newKey.setNullable(key.getNullable());
            newKey.setIndexes(key.getIndexes());
            newKey.setIncludesql(key.getIncludesql());
            newKey.setUnivobject(key.getUnivobject());
            newKey.setJoinable(key.getJoinable());
            newKey.setRopgrpcell(key.getRopgrpcell());
            newKey.setDataid(key.getDataid());
            newKey.saveDB();

          }
          
          // Measurementdeltacalcsupport
          final Measurementdeltacalcsupport mdcsu = new Measurementdeltacalcsupport(rock);
          mdcsu.setTypeid(mtype.getTypeid());
          final MeasurementdeltacalcsupportFactory mdcsuf = new MeasurementdeltacalcsupportFactory(rock, mdcsu);
          final Iterator<Measurementdeltacalcsupport> imdcsu = mdcsuf.get().iterator();

          while (imdcsu.hasNext()) {

            final Measurementdeltacalcsupport mdcsup = imdcsu.next();
            final Measurementdeltacalcsupport newmdcs = new Measurementdeltacalcsupport(rock);
            final String newtypeid = mdcsup.getTypeid().replace(oldVersionId, newVersionId);

            newmdcs.setTypeid(newtypeid);
            newmdcs.setVendorrelease(mdcsup.getVendorrelease());
            newmdcs.setDeltacalcsupport(mdcsup.getDeltacalcsupport());
            newmdcs.setVersionid(newVersionId);
            newmdcs.saveDB();
          }

          // Measurementcounter
          final Measurementcounter mco = new Measurementcounter(rock);
          mco.setTypeid(mtype.getTypeid());
          final MeasurementcounterFactory mcountf = new MeasurementcounterFactory(rock, mco);
          final Iterator<Measurementcounter> imcountf = mcountf.get().iterator();

          while (imcountf.hasNext()) {

            final Measurementcounter counter = imcountf.next();
            final Measurementcounter newCounter = new Measurementcounter(rock);
            final String newtypeid = counter.getTypeid().replace(oldVersionId, newVersionId);

            newCounter.setTypeid(newtypeid);
            newCounter.setDataname(counter.getDataname());
            newCounter.setDescription(counter.getDescription());
            newCounter.setTimeaggregation(counter.getTimeaggregation());
            newCounter.setGroupaggregation(counter.getGroupaggregation());
           // Dont want to do decode to a non delta calc mtype.
            if (mtype.getDeltacalcsupport() == null || mtype.getDeltacalcsupport() == 0) {
            newCounter.setCountaggregation(counter.getCountaggregation());
            }else{
            	final String d = decode(newtypeid, newVersionId);
            	newCounter.setCountaggregation(d);
            }    
            newCounter.setColnumber(counter.getColnumber());
            newCounter.setDatatype(counter.getDatatype());
            newCounter.setDatasize(counter.getDatasize());
            newCounter.setDatascale(counter.getDatascale());
            newCounter.setIncludesql(counter.getIncludesql());
            newCounter.setUnivobject(counter.getUnivobject());
            newCounter.setUnivclass(counter.getUnivclass());
            newCounter.setCountertype(counter.getCountertype());
            newCounter.setCounterprocess(counter.getCounterprocess());
            newCounter.setDataid(counter.getDataid());
            newCounter.saveDB();

          }

          // Measurementvector
          final Measurementvector mve = new Measurementvector(rock);
          mve.setTypeid(mtype.getTypeid());
          final MeasurementvectorFactory mvef = new MeasurementvectorFactory(rock, mve);
          final Iterator<Measurementvector> imve = mvef.get().iterator();

          while (imve.hasNext()) {

            final Measurementvector mvec = imve.next();
            final Measurementvector newVector = new Measurementvector(rock);
            final String newtypeid = mvec.getTypeid().replace(oldVersionId, newVersionId);

            newVector.setTypeid(newtypeid);
            newVector.setDataname(mvec.getDataname());
            newVector.setVendorrelease(mvec.getVendorrelease());
            newVector.setVindex(mvec.getVindex());
            newVector.setVfrom(mvec.getVfrom());
            newVector.setVto(mvec.getVto());
            newVector.setMeasure(mvec.getMeasure());
            newVector.setQuantity(mvec.getQuantity());
            newVector.saveDB();
          }

          // Measurementobjbhsupport added 30.6 by eheijun
          final Measurementobjbhsupport mobhsu = new Measurementobjbhsupport(rock);
          mobhsu.setTypeid(mtype.getTypeid());
          final MeasurementobjbhsupportFactory mobhsuf = new MeasurementobjbhsupportFactory(rock, mobhsu);
          final Iterator<Measurementobjbhsupport> imobhsu = mobhsuf.get().iterator();

          while (imobhsu.hasNext()) {

            final Measurementobjbhsupport mobhsup = imobhsu.next();
            final Measurementobjbhsupport newmobhs = new Measurementobjbhsupport(rock);
            final String newtypeid = mobhsup.getTypeid().replace(oldVersionId, newVersionId);

            newmobhs.setTypeid(newtypeid);
            newmobhs.setObjbhsupport(mobhsup.getObjbhsupport().toUpperCase());
            newmobhs.saveDB();
          }

          // Copy the measurement tables, measurement columns, and vector
          // counters.

          // Measurementtable

          Measurementtable mtable = new Measurementtable(rock);
          mtable.setTypeid(mtype.getTypeid());
          final MeasurementtableFactory mtablef = new MeasurementtableFactory(rock, mtable);
          final Iterator<Measurementtable> imtable = mtablef.get().iterator();

          while (imtable.hasNext()) {

            mtable = imtable.next();
            final Measurementtable newTable = new Measurementtable(rock);
            final String newtypeid = mtable.getTypeid().replace(oldVersionId, newVersionId);
            final String newMtableid = newtypeid + ":" + mtable.getTablelevel();

            newTable.setMtableid(newMtableid);
            newTable.setTablelevel(mtable.getTablelevel());
            newTable.setTypeid(newtypeid);
            newTable.setBasetablename(mtable.getBasetablename());
            newTable.setDefault_template(mtable.getDefault_template());
            newTable.setPartitionplan(mtable.getPartitionplan());
            newTable.saveDB();

            // Measurementcolumn
            //
            // The columns are copied so that first key columns from the
            // techpack, then public keys from the base techpack, and then
            // counters from the techpack. The column number is set as a running
            // number.

            // Get all the measurement columns from the DB ordered by the column
            // number.
            final Measurementcolumn mCol = new Measurementcolumn(rock);
            Measurementcolumn newColumn = null;
            mCol.setMtableid(mtable.getMtableid());
            final MeasurementcolumnFactory mColf = new MeasurementcolumnFactory(rock, mCol, "ORDER BY COLNUMBER");
            long colNumber = 0L;

            // Copy the key columns
            Iterator<Measurementcolumn> imColf = mColf.get().iterator();

            while (imColf.hasNext()) {

              final Measurementcolumn mcl = imColf.next();
              final String colType = mcl.getColtype();

              if (colType != null && colType.equalsIgnoreCase("KEY")) {
                newColumn = new Measurementcolumn(rock);
                colNumber++;
                newColumn.setMtableid(newMtableid);
                newColumn.setDataname(mcl.getDataname());
                newColumn.setColnumber(colNumber);
                newColumn.setDatatype(mcl.getDatatype());
                newColumn.setDatasize(mcl.getDatasize());
                newColumn.setDatascale(mcl.getDatascale());
                newColumn.setUniquevalue(mcl.getUniquevalue());
                newColumn.setNullable(mcl.getNullable());
                newColumn.setIndexes(mcl.getIndexes());
                newColumn.setDescription(mcl.getDescription());
                newColumn.setDataid(mcl.getDataid());
                newColumn.setReleaseid(mcl.getReleaseid());
                newColumn.setUniquekey(mcl.getUniquekey());
                newColumn.setIncludesql(mcl.getIncludesql());
                newColumn.setColtype(mcl.getColtype());
                newColumn.saveDB();
              }
            }

            // Copy the public keys from the base techpack. Column order is
            // based on the order in the base, even though the
            // column numbers will be overwritten.
            Measurementcolumn baseMCol = new Measurementcolumn(rock);
            String baseDefTbl = basedef + ":" + mtable.getTablelevel();

            if (mtable.getTypeid().endsWith("EVENT_E_LTE_IMSI_SUC") || mtable.getTypeid().endsWith("EVENT_E_SGEH_IMSI_SUC")) {
              // Special case for EVENT_E_SGEH_IMSI_SUC and EVENT_E_LTE_IMSI_SUC. 
              // This is a RAW table but we only want the 15MIN base definition. We do not want the RAW base definition.
              baseDefTbl = basedef + ":15MIN";
            } 
            
            baseMCol.setMtableid(baseDefTbl);
            final MeasurementcolumnFactory baseMColf = new MeasurementcolumnFactory(rock, baseMCol,
                "ORDER BY COLNUMBER");
            final Iterator<Measurementcolumn> ibaseMCol = baseMColf.get().iterator();

            while (ibaseMCol.hasNext()) {

              baseMCol = ibaseMCol.next();

              newColumn = new Measurementcolumn(rock);
              colNumber++;
              newColumn.setMtableid(newMtableid);
              newColumn.setDataname(baseMCol.getDataname());
              newColumn.setColnumber(colNumber);
              newColumn.setDatatype(baseMCol.getDatatype());
              newColumn.setDatasize(baseMCol.getDatasize());
              newColumn.setDatascale(baseMCol.getDatascale());
              newColumn.setUniquevalue(baseMCol.getUniquevalue());
              newColumn.setNullable(baseMCol.getNullable());
              newColumn.setIndexes(baseMCol.getIndexes());
              newColumn.setDescription(baseMCol.getDescription());
              newColumn.setDataid(baseMCol.getDataid());
              newColumn.setReleaseid(baseMCol.getReleaseid());
              newColumn.setUniquekey(baseMCol.getUniquekey());
              newColumn.setIncludesql(baseMCol.getIncludesql());
              newColumn.setColtype("PUBLICKEY");
              newColumn.saveDB();
            }

            // Copy the counter columns
            imColf = mColf.get().iterator();

            while (imColf.hasNext()) {

              final Measurementcolumn mcl = imColf.next();
              final String colType = mcl.getColtype();

              if (colType != null && colType.equalsIgnoreCase("COUNTER")) {
                newColumn = new Measurementcolumn(rock);
                colNumber++;
                newColumn.setMtableid(newMtableid);
                newColumn.setDataname(mcl.getDataname());
                newColumn.setColnumber(colNumber);
                newColumn.setDatatype(mcl.getDatatype());
                newColumn.setDatasize(mcl.getDatasize());
                newColumn.setDatascale(mcl.getDatascale());
                newColumn.setUniquevalue(mcl.getUniquevalue());
                newColumn.setNullable(mcl.getNullable());
                newColumn.setIndexes(mcl.getIndexes());
                newColumn.setDescription(mcl.getDescription());
                newColumn.setDataid(mcl.getDataid());
                newColumn.setReleaseid(mcl.getReleaseid());
                newColumn.setUniquekey(mcl.getUniquekey());
                newColumn.setIncludesql(mcl.getIncludesql());
                newColumn.setColtype(mcl.getColtype());
                newColumn.saveDB();
              }
            }
          }
        }
      }
    } catch (final Exception e) {
      logger.log(Level.SEVERE, "SQL error in create Measurements", e);
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    }
  }

  /*****************************************************************************
   * public void removeMeasurement(String versionid)
   * 
   * Removes MeasurementTable, MeasurementCounter, MeasurementDeltaCalcSupport,
   * MeasurementKey, MeasurementColumn, Measurementvector and Measurementclass
   * TechPack rows from DWHRep Database.
   ****************************************************************************/
  private void removeMeasurement(final String versionid) {
    try {

      // Remove TP content from Measurementcolumn, Measurementtable,
      // Measurementcounter, MeasurementDeltaCalcSupport, Measurementkey,
      // Measurementtype, Measurementtypeclass, Measurementvector

      // Measurementtype
      Measurementtype mtype = new Measurementtype(rock);
      mtype.setVersionid(versionid);
      final MeasurementtypeFactory mtypef = new MeasurementtypeFactory(rock, mtype);
      final Iterator<Measurementtype> imtype = mtypef.get().iterator();

      
      while (imtype.hasNext()) {

        mtype = imtype.next();

        // Measurementtable
        Measurementtable mtable = new Measurementtable(rock);
        mtable.setTypeid(mtype.getTypeid());
        final MeasurementtableFactory mtablef = new MeasurementtableFactory(rock, mtable);
        final Iterator<Measurementtable> imtable = mtablef.get().iterator();

        while (imtable.hasNext()) {

          mtable = imtable.next();

          // Measurementcolumn
          final Measurementcolumn mCol = new Measurementcolumn(rock);
          mCol.setMtableid(mtable.getMtableid());
          mCol.deleteDB(mCol);

          final Measurementtable tmpmt = new Measurementtable(rock);
          tmpmt.setMtableid(mtable.getMtableid());
          tmpmt.deleteDB();
        }

        // Measurementobjbhsupport
        final Measurementobjbhsupport measurementobjbhsupport = new Measurementobjbhsupport(rock);
        measurementobjbhsupport.setTypeid(mtype.getTypeid());
        measurementobjbhsupport.deleteDB(measurementobjbhsupport);

        // MeasurementVector
        final Measurementvector mv = new Measurementvector(rock);
        mv.setTypeid(mtype.getTypeid());
        mv.deleteDB(mv);

        // Measurementcounter
        final Measurementcounter mco = new Measurementcounter(rock);
        mco.setTypeid(mtype.getTypeid());
        mco.deleteDB(mco);

        // MeasurementDeltaCalcSupport
        final Measurementdeltacalcsupport mdcs = new Measurementdeltacalcsupport(rock);
        mdcs.setTypeid(mtype.getTypeid());
        mdcs.deleteDB(mdcs);

        // Measurementkey
        final Measurementkey mkey = new Measurementkey(rock);
        mkey.setTypeid(mtype.getTypeid());
        mkey.deleteDB(mkey);

        // Measurementtype
        final Measurementtype mt = new Measurementtype(rock);
        mt.setTypeid(mtype.getTypeid());
        mt.deleteDB();

      }

      // Typeclass
      final Measurementtypeclass mclass = new Measurementtypeclass(rock);
      mclass.setVersionid(versionid);
      mclass.deleteDB(mclass);

    } catch (final Exception e) {
      logger.log(Level.SEVERE, "SQL error in remove Measurements", e);
      System.out.println(e);
    }
  }

  /*****************************************************************************
   * public void createBusyhour(TechPackInfo techpack)
   ****************************************************************************/
  protected void createBusyhour() {

    try {

      // Check if conversion is needed due to busy hour improvements.
      if (!isBusyHourConversionNeeded()) {

        // No conversion is needed. Just copy all the busy hour data.
        logger.log(Level.FINE, "createBusyhour(): Copying busy hours.");

        final Busyhour whereBusyhour = new Busyhour(rock);
        whereBusyhour.setVersionid(oldVersionId);
        final BusyhourFactory busyhourFactory = new BusyhourFactory(rock, whereBusyhour, true);
        for (final Busyhour oldBH : busyhourFactory.get()) {
          final Busyhour newBH = (Busyhour) oldBH.clone();
          newBH.setNewItem(true);
          newBH.setVersionid(newVersionId);
          newBH.setReactivateviews(0);
          if (oldBH.getVersionid().equals(oldBH.getTargetversionid())) {
            newBH.setTargetversionid(newVersionId);
          }else {
    	    newBH.setTargetversionid(getLatestActiveTP(oldBH.getTargetversionid()));
          }
          newBH.saveToDB();

          // Copy all sources of the busyhour
          final Busyhoursource bhSCond = new Busyhoursource(rock);
          bhSCond.setVersionid(oldBH.getVersionid());
          bhSCond.setTargetversionid(oldBH.getTargetversionid());
          bhSCond.setBhlevel(oldBH.getBhlevel());
          bhSCond.setBhobject(oldBH.getBhobject());
          bhSCond.setBhtype(oldBH.getBhtype());
          final BusyhoursourceFactory busyhoursourceFactory = new BusyhoursourceFactory(rock, bhSCond, true);
          for (final Busyhoursource oldBHS : busyhoursourceFactory.get()) {
            final Busyhoursource newBHS = (Busyhoursource) oldBHS.clone();
            newBHS.setNewItem(true);
            newBHS.setVersionid(newVersionId);
            if (oldBH.getVersionid().equals(oldBH.getTargetversionid())) {
              newBHS.setTargetversionid(newVersionId);
            }else {
        	  newBHS.setTargetversionid(getLatestActiveTP(oldBH.getTargetversionid()));
            }
            newBHS.saveToDB();
          }

          // Copy all rank keys of the busyhour
          final Busyhourrankkeys bhRKCond = new Busyhourrankkeys(rock);
          bhRKCond.setVersionid(oldBH.getVersionid());
          bhRKCond.setTargetversionid(oldBH.getTargetversionid());
          bhRKCond.setBhlevel(oldBH.getBhlevel());
          bhRKCond.setBhobject(oldBH.getBhobject());
          bhRKCond.setBhtype(oldBH.getBhtype());
          final BusyhourrankkeysFactory busyhourrankkeysFactory = new BusyhourrankkeysFactory(rock, bhRKCond, true);
          for (final Busyhourrankkeys oldBHRK : busyhourrankkeysFactory.get()) {
            final Busyhourrankkeys newBHRK = (Busyhourrankkeys) oldBHRK.clone();
            newBHRK.setNewItem(true);
            newBHRK.setVersionid(newVersionId);
            if (oldBH.getVersionid().equals(oldBH.getTargetversionid())) {
              newBHRK.setTargetversionid(newVersionId);
            }else {
        	  newBHRK.setTargetversionid(getLatestActiveTP(oldBH.getTargetversionid()));
            }
            newBHRK.saveToDB();
          }

          // Copy all the mappings for the busy hour.
          final Busyhourmapping bhmCond = new Busyhourmapping(rock);
          bhmCond.setVersionid(oldVersionId);
          bhmCond.setTargetversionid(oldBH.getTargetversionid());
          bhmCond.setBhlevel(oldBH.getBhlevel());
          bhmCond.setBhobject(oldBH.getBhobject());
          bhmCond.setBhtype(oldBH.getBhtype());
          final BusyhourmappingFactory bhmF = new BusyhourmappingFactory(rock, bhmCond, true);
          for (final Busyhourmapping oldBHM : bhmF.get()) {
            final Busyhourmapping newBHM = (Busyhourmapping) oldBHM.clone();
            newBHM.setNewItem(true);
            newBHM.setVersionid(newVersionId);
            
            if (oldBH.getVersionid().equals(oldBH.getTargetversionid())) {
            	newBHM.setTypeid(newVersionId + ":" + newBHM.getBhtargettype());
            	newBHM.setTargetversionid(newVersionId);
            }else {//this is for Custom TPs
            	newBHM.setTargetversionid(getLatestActiveTP(oldBH.getTargetversionid()));
            	newBHM.setTypeid(newBHM.getTargetversionid() + ":" + newBHM.getBhtargettype());
            }
            newBHM.saveDB();
          }
        }

        // Copy all the place holders for this techpack.
        final Busyhourplaceholders bhphCond = new Busyhourplaceholders(rock);
        bhphCond.setVersionid(oldVersionId);
        final BusyhourplaceholdersFactory bhphF = new BusyhourplaceholdersFactory(rock, bhphCond, true);
        for (final Busyhourplaceholders oldBHPH : bhphF.get()) {
          final Busyhourplaceholders newBHPH = (Busyhourplaceholders) oldBHPH.clone();
          newBHPH.setNewItem(true);
          newBHPH.setVersionid(newVersionId);
          newBHPH.saveDB();
        }

      } else {

        // Eniq level is too old. Conversion is needed due to the busy hour
        // improvements.
        logger.log(Level.FINE, "createBusyhour(): Converting busy hours.");
        busyHourImprovementConversion();
      }

    } catch (Exception e) {
        logger.log(Level.SEVERE, "Error in createBusyhour", e);
    }
  }

  /*****************************************************************************
   * public void removeBusyhour(TechPackInfo techpack)
   ****************************************************************************/
  private void removeBusyhour(final String versionId) {

    final Busyhour bhCond = new Busyhour(rock);
    bhCond.setVersionid(versionId);
    try {
      final BusyhourFactory busyhourFactory = new BusyhourFactory(rock, bhCond, true);
      for (final Busyhour busyhour : busyhourFactory.get()) {

        // Delete all sources of the busyhour
        final Busyhoursource bhS = new Busyhoursource(rock);
        bhS.setVersionid(busyhour.getVersionid());
        bhS.setTargetversionid(busyhour.getTargetversionid());
        bhS.setBhlevel(busyhour.getBhlevel());
        bhS.setBhobject(busyhour.getBhobject());
        bhS.setBhtype(busyhour.getBhtype());
        bhS.deleteDB(bhS);

        // Delete all rankeys of the busyhour
        final Busyhourrankkeys bhRK = new Busyhourrankkeys(rock);
        bhRK.setVersionid(busyhour.getVersionid());
        bhRK.setTargetversionid(busyhour.getTargetversionid());
        bhRK.setBhlevel(busyhour.getBhlevel());
        bhRK.setBhobject(busyhour.getBhobject());
        bhRK.setBhtype(busyhour.getBhtype());
        bhRK.deleteDB(bhRK);

        // Delete all mappings for the busyhour
        final Busyhourmapping bhm = new Busyhourmapping(rock);
        bhm.setVersionid(busyhour.getVersionid());
        bhm.setTargetversionid(busyhour.getTargetversionid());
        bhm.setBhlevel(busyhour.getBhlevel());
        bhm.setBhobject(busyhour.getBhobject());
        bhm.setBhtype(busyhour.getBhtype());
        bhm.deleteDB();

        // Delete the busyhour
        busyhour.deleteDB();
      }

      // Delete all placeholders for the techpack.
      final Busyhourplaceholders bhPH = new Busyhourplaceholders(rock);
      bhPH.setVersionid(versionId);
      bhPH.deleteDB();

    } catch (final SQLException e) {
      logger.log(Level.SEVERE, "SQL error in removeBusyhour", e);
    } catch (final RockException e) {
      logger.log(Level.SEVERE, "ROCK error in removeBusyhour", e);
    } catch (final Exception e) {
      logger.log(Level.SEVERE, "FATAL error in removeBusyhour", e);
    }
  }

  /*****************************************************************************
   * public void createReferencetableAndRererencecolumn(TechPackInfo techpack)
   ****************************************************************************/
  private void createReferencetableAndRererencecolumn() {
    try {

      String typeId = "";
      String objectid = "";
      Referencetable publicRefTable = null;

      // If the current techpack is not a base techpack and the base has been
      // defined, then copy the (de)external statements from the base techpack.
      if (!techpack.getType().equalsIgnoreCase("base")
          && (techpack.getBaseDefinition() != null && techpack.getBaseDefinition().length() > 0)) {

        // Get the base version
        final String baseVersionId = techpack.getBaseDefinition();

        // Iterate through all reference table entries in the base techpack and
        // create the matching entries to the database for this techpack.
        final Referencetable rt = new Referencetable(rock);
        rt.setVersionid(baseVersionId);
        final ReferencetableFactory rtF = new ReferencetableFactory(rock, rt);
        final Iterator<Referencetable> tableIter = rtF.get().iterator();
        while (tableIter.hasNext()) {
          final Referencetable tmpRT = tableIter.next();

          // Check if the current base reference table is the special reference
          // type meant for ranking table measurement types, i.e. object name is
          // '(DIM_RANKMT)_BHTYPE'. If yes, then generate the reference tables
          // and columns for ranking table measurement types.
          final String template = "(DIM_RANKMT)_BHTYPE";
          if (tmpRT.getObjectname().equals(template)) {
            createReferenceTypesForRankingTables(tmpRT, techpack.getName() + ":" + techpack.getVersion());
            continue;
          }

          // Check if the current base reference table is the special reference
          // type meant for AGGLEVEL, i.e. object name is
          // 'SELECT_(TPNAME)_AGGLEVEL'. If yes, then copy the reference table
          // entry to the current techpack.
          final String template2 = "SELECT_(TPNAME)_AGGLEVEL";
          if (tmpRT.getObjectname().equals(template2)) {
            createReferenceTypesForAggLevel(tmpRT, techpack.getName(), techpack.getName() + ":" + techpack.getVersion());
            continue;
          }

          // Check if the current base reference table is the special reference
          // type meant for public columns, i.e. object name is
          // 'PUBLIC_REFTYPE'. If yes, store this table for later use. The
          // public columns are copied when the reference tables have been
          // copied from the old techpack.
          if (tmpRT.getObjectname().equals("PUBLIC_REFTYPE")) {
            publicRefTable = tmpRT;
            continue;
          }

          // Anything else defined in the base techpack is now ignored.
          // If other reference types should be possible to be defined in the
          // base, then they must be separately handled here.
          logger.log(Level.WARNING, "Reference type: " + tmpRT.getTypename()
              + " defined in the base techpack is ignored when generating types from the base techpack.");

        }
      } else {
        logger.log(Level.FINE, "Base techpack has not been defined, so reference types are not copied from base.");
      }

      // If the new techpack is based on an existing techpack, then copy the
      // reference types from the existing techpack to the new one.
      // NOTE: Existing types from base techpack are ignored here.
      // NOTE: There is also an extra check for not copying "old" reference
      // tables or columns already taken from the base techpack. Such tables and
      // columns may exist during the first copy after migrate.
      if (techpack.getFromTechPack() != "") {

        // Get a map of all reference tables and columns already copied
        // from the base techpack. If there are matching tables and columns in
        // the source techpack, they will be ignored.
        final HashMap<String, Vector<Referencecolumn>> refTypeMap = new HashMap<String, Vector<Referencecolumn>>();
        final Referencetable rt = new Referencetable(rock);
        rt.setVersionid(techpack.getName() + ":" + techpack.getVersion());
        final ReferencetableFactory rtF = new ReferencetableFactory(rock, rt);
        final Iterator<Referencetable> rtIter = rtF.get().iterator();
        while (rtIter.hasNext()) {
          final Referencetable tmpRT = rtIter.next();

          final Referencecolumn rc = new Referencecolumn(rock);
          rc.setTypeid(tmpRT.getTypeid());
          final ReferencecolumnFactory rcF = new ReferencecolumnFactory(rock, rc);
          final Vector<Referencecolumn> cols = rcF.get();

          // Add the table and columns to the map.
          refTypeMap.put(tmpRT.getTypeid(), cols);
        }

        // Get the list of public columns from the base techpack.
        final Vector<String> publicCols = getPublicColumnNamesFromBase(publicRefTable);

        // Iterate through all reference types in the old techpack, excluding
        // base definitions and possible duplicates for base definitions.
        final Referencetable rt2 = new Referencetable(rock);
        rt2.setVersionid(oldVersionId);
        final ReferencetableFactory rtF2 = new ReferencetableFactory(rock, rt2);
        final Iterator<Referencetable> iter = rtF2.get().iterator();
        while (iter.hasNext()) {

          final Referencetable tmpRT = iter.next();

          // The basedef value might be 0 for normal reference table, 1 for base
          // techpack reference table, or null for tables from a migrated
          // techpack. Ignore base techpack tables.
          if (!(tmpRT.getBasedef() != null && tmpRT.getBasedef().equals(1))) {

            // Check if the table has already been copied from base techpack. If
            // yes, then skip it.
            final String tmpTypeId = newVersionId + tmpRT.getTypeid().substring(tmpRT.getTypeid().lastIndexOf(':'));
            // System.out.println("createReferencetableAndRererencecolumn(): DEBUG: trying to match table: "
            // + tmpTypeId);

            if (refTypeMap.containsKey(tmpTypeId)) {

              logger.log(Level.INFO, "Reference table: " + tmpRT.getTypeid()
                  + " not copied, since it was already taken from the base techpack.");
              continue;
            }

            // Get the old typeId for the table.
            final String oldTypeId = oldVersionId + ":" + tmpRT.getTypename();

            typeId = newVersionId + ":" + tmpRT.getTypename();
            objectid = typeId;

            // System.out.println("createReferencetableAndRererencecolumn(): DEBUG: creating reftable: "
            // + tmpRT.getTypename());

            // Create the new reference table
            final Referencetable newRT = new Referencetable(rock);
            newRT.setVersionid(newVersionId);
            newRT.setTypeid(typeId);
            newRT.setTypename(tmpRT.getTypename());
            newRT.setObjectid(objectid);
            newRT.setObjectname(tmpRT.getObjectname());
            newRT.setObjectversion(tmpRT.getObjectversion());
            newRT.setObjecttype(tmpRT.getObjecttype());
            newRT.setDescription(tmpRT.getDescription());
            newRT.setStatus(tmpRT.getStatus());
            newRT.setUpdate_policy(tmpRT.getUpdate_policy());
            newRT.setDataformatsupport(tmpRT.getDataformatsupport());
            newRT.setBasedef(0);
            newRT.saveDB();

            // System.out.println("createReferencetableAndRererencecolumn(): DEBUG: created reftable: "
            // + newRT.getTypename());

            // Initialize the column number value to 100 for the "normal"
            // columns.
            long colNumber = 100;

            // Create the reference columns for this reference table. The
            // columns are ordered by the column number, so that we can fix the
            // numbering without affecting the original order.
            final Referencecolumn rc = new Referencecolumn(rock);
            rc.setTypeid(oldTypeId);
            final ReferencecolumnFactory rcF = new ReferencecolumnFactory(rock, rc, "ORDER BY COLNUMBER");
            final Iterator<Referencecolumn> rcIter = rcF.get().iterator();
            while (rcIter.hasNext()) {
              final Referencecolumn tmpRC = rcIter.next();

              boolean skip = false;

              // The basedef value might be 0 for normal reference table, 1 for
              // base techpack reference table, or null for tables from a
              // migrated techpack. Ignore base techpack tables.
              if (!(tmpRC.getBasedef() != null && tmpRC.getBasedef().equals(1))) {

                // Check if the column has already been copied from base
                // techpack or if it is a public column. If yes, then skip it.
                if (refTypeMap.containsKey(tmpRT.getTypeid())) {
                  final Vector<Referencecolumn> cols = refTypeMap.get(tmpRT.getTypeid());
                  if (cols != null && !cols.isEmpty()) {
                    final Iterator<Referencecolumn> colIter = cols.iterator();
                    while (colIter.hasNext()) {
                      final Referencecolumn tmpCol = colIter.next();
                      if (tmpCol.getDataname().equals(tmpRC.getDataname())) {

                      }
                      logger.log(Level.INFO, "Reference column: " + tmpRC.getDataname() + " for table: "
                          + tmpRT.getTypeid() + " not copied, since it was already taken from the base techpack.");
                      skip = true;
                    }

                  }
                }
                if (!skip && publicCols.contains(tmpRC.getDataname())) {
                  logger.log(Level.INFO, "Reference column: " + tmpRC.getDataname() + " for table: "
                      + tmpRT.getTypeid() + " not copied, since column is defined in the base techpack.");
                  skip = true;
                }

                // Check if this column should be skipped.
                if (skip) {
                  continue;
                }

                // Step the colnumber
                colNumber++;

                // Create the reference column entry
                final Referencecolumn newRC = new Referencecolumn(rock);
                newRC.setTypeid(typeId);
                newRC.setDataname(tmpRC.getDataname());
                newRC.setColnumber(colNumber);
                newRC.setDatatype(tmpRC.getDatatype());
                newRC.setDatasize(tmpRC.getDatasize());
                newRC.setDatascale(tmpRC.getDatascale());
                newRC.setUniquevalue(tmpRC.getUniquevalue());
                newRC.setNullable(tmpRC.getNullable());
                newRC.setIndexes(tmpRC.getIndexes());
                newRC.setUniquekey(tmpRC.getUniquekey());
                newRC.setIncludesql(tmpRC.getIncludesql());
                newRC.setIncludeupd(tmpRC.getIncludeupd());
                newRC.setColtype("COLUMN");
                newRC.setDescription(tmpRC.getDescription());
                newRC.setUniverseclass(tmpRC.getUniverseclass());
                newRC.setUniverseobject(tmpRC.getUniverseobject());
                newRC.setUniversecondition(tmpRC.getUniversecondition());
                newRC.setDataid(tmpRC.getDataid());
                newRC.setBasedef(0);
                newRC.saveDB();

                // System.out.println("createReferencetableAndRererencecolumn(): DEBUG: created refcol: "
                // + newRC.getDataname());
              }
            }

          }

        }

        // Copy the public columns for "normal" reference tables in this
        // techpack from the base techpack.
        createPublicReferenceColumns(publicRefTable, techpack.getName() + ":" + techpack.getVersion());

      }
    } catch (final Exception e) {
      logger.log(Level.SEVERE, "SQL error in create Referencetable", e);
      System.out.println(e);
    }
  }

  /*****************************************************************************
   * public void removeReferences(String versionid){ Removes TP rows from
   * Referencecolumn and Referencetable tables.
   ****************************************************************************/
  private void removeReferences(final String versionid) {

    try {

      // Removes TP content from ReferenceColumn and Referencetable tables

      final Referencetable rtCond = new Referencetable(rock);
      rtCond.setVersionid(versionid);
      final ReferencetableFactory rtf = new ReferencetableFactory(rock, rtCond);
      for (final Referencetable rt : rtf.get()) {

        final Referencecolumn rcCond = new Referencecolumn(rock);
        rcCond.setTypeid(rt.getTypeid());
        final ReferencecolumnFactory rcf = new ReferencecolumnFactory(rock, rcCond);
        for (final Referencecolumn rc : rcf.get()) {
          rc.deleteDB();
        }
        rt.deleteDB();
      }

    } catch (final Exception e) {
      logger.log(Level.SEVERE, "SQL error in remove References", e);
      System.out.println(e);
    }
  }

  /*****************************************************************************
   * public void createData(){
   ****************************************************************************/
  private void createData() {
    try {

      String dataformatid = "";
      String typeid = "";

      Dataformat df = new Dataformat(rock);

      df.setVersionid(oldVersionId);

      final DataformatFactory dff = new DataformatFactory(rock, df);

      final Iterator<Dataformat> idf = dff.get().iterator();

      // Insert content to create Dataformat table
      while (idf.hasNext()) {
        df = idf.next();

        // DATAFORMAT
        //
        // Note: The DataFormatType and the type part in the DataFormatId are
        // changed to lower case. This is done to avoid mismatches in the values
        // in the Dataformat, Dataitem, Defaulttags, Transformer, and
        // Transformation tables.
        final Dataformat tmpdf = new Dataformat(rock);
        dataformatid = newVersionId + ":" + df.getFoldername() + ":"
            + Utils.replaceNull(df.getDataformattype()).toLowerCase();
        typeid = newVersionId + ":" + df.getFoldername();

        // Create TP content to Dataformat table
        tmpdf.setVersionid(newVersionId);
        tmpdf.setDataformatid(dataformatid);
        tmpdf.setTypeid(typeid);
        tmpdf.setObjecttype(df.getObjecttype());
        tmpdf.setFoldername(df.getFoldername());
        tmpdf.setDataformattype(Utils.replaceNull(df.getDataformattype()).toLowerCase());
        tmpdf.saveDB();

        // DATAITEM
        final Dataitem ditem = new Dataitem(rock);
        ditem.setDataformatid(df.getDataformatid());
        final DataitemFactory dif = new DataitemFactory(rock, ditem);

        final Iterator<Dataitem> idi = dif.get().iterator();

        // Insert content to create Dataitem table
        while (idi.hasNext()) {

          final Dataitem di = idi.next();

          final Dataitem tmpdi = new Dataitem(rock);

          // Create TP content to Dataitem table
          tmpdi.setDataformatid(dataformatid);
          tmpdi.setDataname(di.getDataname());
          tmpdi.setColnumber(di.getColnumber());
          tmpdi.setDataid(di.getDataid());
          tmpdi.setProcess_instruction(di.getProcess_instruction());
          tmpdi.setDatatype(di.getDatatype());
          tmpdi.setDatasize(di.getDatasize());
          tmpdi.setDatascale(di.getDatascale());
          tmpdi.saveDB();
        }

        // DEFAULTTAGS
        final Defaulttags dtag = new Defaulttags(rock);
        dtag.setDataformatid(df.getDataformatid());
        final DefaulttagsFactory dtf = new DefaulttagsFactory(rock, dtag);

        final Iterator<Defaulttags> idt = dtf.get().iterator();

        // Insert content to create Defaulttags table
        while (idt.hasNext()) {

          final Defaulttags dt = idt.next();

          final String olddescription = dt.getDescription();
          final String newdescription = olddescription.replace(oldVersionId, newVersionId);

          final Defaulttags tmpdt = new Defaulttags(rock);
          // Create TP content to Defaulttags table
          tmpdt.setDataformatid(dataformatid);
          tmpdt.setDescription(newdescription);
          tmpdt.setTagid(dt.getTagid());
          tmpdt.saveDB();
        }

      }

    } catch (final Exception e) {
      logger.log(Level.SEVERE, "SQL error in create Data", e);
      System.out.println(e);
    }
  }

  /*****************************************************************************
   * public void removeData(String versionid){
   * 
   * Removes DataItem, DefaultTags and DataFormat Tech Pack rows from DWHRep
   * Database.
   ****************************************************************************/
  private void removeData(final String versionid) {
    try {
      // Remove Tech Pack content from DataItem and DefaultTags tables

      Dataformat df = new Dataformat(rock);
      df.setVersionid(versionid);
      final DataformatFactory dff = new DataformatFactory(rock, df);

      final Iterator<Dataformat> idf = dff.get().iterator();

      while (idf.hasNext()) {

        df = idf.next();

        final String tmp = df.getDataformatid();

        Dataitem di = new Dataitem(rock);
        di.setDataformatid(tmp);

        final DataitemFactory dif = new DataitemFactory(rock, di);
        final Iterator<Dataitem> idi = dif.get().iterator();

        while (idi.hasNext()) {

          di = idi.next();
          di.setDataformatid(tmp);
          di.deleteDB();
        }

        Defaulttags dt = new Defaulttags(rock);
        dt.setDataformatid(tmp);

        final DefaulttagsFactory dtf = new DefaulttagsFactory(rock, dt);
        final Iterator<Defaulttags> idt = dtf.get().iterator();

        while (idt.hasNext()) {

          dt = idt.next();
          dt.setDataformatid(tmp);
          dt.deleteDB();
        }
        df.deleteDB();
      }
    } catch (final Exception e) {
      logger.log(Level.SEVERE, "SQL error in remove Data", e);
      System.out.println(e);
    }
  }

  /*****************************************************************************
   * public void createTransformer(){
   ****************************************************************************/
  private void createTransformer() {
    try {

      String oldtransformerid = "";
      String newtransformerid = "";

      // Create TP content to Transformer table
      Transformer t = new Transformer(rock);

      t.setVersionid(oldVersionId);

      final TransformerFactory tf = new TransformerFactory(rock, t);

      final Iterator<Transformer> it = tf.get().iterator();

      while (it.hasNext()) {
        t = it.next();

        final Transformer tmpt = new Transformer(rock);

        oldtransformerid = t.getTransformerid();

        newtransformerid = oldtransformerid.replace(oldVersionId, newVersionId);

        // Change the data format type part in the transformerId to lower case.
        // For example:
        // DC_E_STN:b10:DC_E_STN_PST:MDC --> DC_E_STN:b10:DC_E_STN_PST:mdc
        final String dataformattype = newtransformerid.substring(newtransformerid.lastIndexOf(":") + 1).toLowerCase();
        final String prefix = newtransformerid.substring(0, newtransformerid.lastIndexOf(":") + 1);
        newtransformerid = prefix + dataformattype;

        // Create TP content to Transformer table
        tmpt.setVersionid(newVersionId);
        tmpt.setDescription(t.getDescription());
        tmpt.setTransformerid(newtransformerid);
        tmpt.setType(t.getType());
        tmpt.saveDB();
      }
    } catch (final Exception e) {
      logger.log(Level.SEVERE, "SQL error in create Transformer", e);
      System.out.println(e);
    }
  }

  /*****************************************************************************
   * public void createTransformation(){
   ****************************************************************************/
  private void createTransformation() {
    try {

      String oldtransformerid = "";
      String newtransformerid = "";

      // Create TP content to Transformation table

      final Transformer transformer = new Transformer(rock);
      transformer.setVersionid(oldVersionId);

      final TransformerFactory tf = new TransformerFactory(rock, transformer);
      final Iterator<Transformer> it = tf.get().iterator();

      while (it.hasNext()) {

        final Transformer t = it.next();
        oldtransformerid = t.getTransformerid();
        final Transformation ta = new Transformation(rock);
        ta.setTransformerid(oldtransformerid);

        final TransformationFactory taf = new TransformationFactory(rock, ta);
        final Iterator<Transformation> ita = taf.get().iterator();

        while (ita.hasNext()) {

          final Transformation transformation = ita.next();

          final Transformation newTa = new Transformation(rock);

          newtransformerid = oldtransformerid.replace(oldVersionId, newVersionId);

          // Change the data format type part in the transformerId to lower
          // case. For example:
          // DC_E_STN:b10:DC_E_STN_PST:MDC --> DC_E_STN:b10:DC_E_STN_PST:mdc
          final String dataformattype = newtransformerid.substring(newtransformerid.lastIndexOf(":") + 1).toLowerCase();
          final String prefix = newtransformerid.substring(0, newtransformerid.lastIndexOf(":") + 1);
          newtransformerid = prefix + dataformattype;

          // Create TP content to Transformer table
          newTa.setTransformerid(newtransformerid);
          newTa.setOrderno(transformation.getOrderno());
          newTa.setType(transformation.getType());
          newTa.setSource(transformation.getSource());
          newTa.setTarget(transformation.getTarget());
          newTa.setConfig(transformation.getConfig());
          newTa.setDescription(transformation.getDescription());
          newTa.saveDB();
        }
      }
    } catch (final Exception e) {
      logger.log(Level.SEVERE, "SQL error in create Transformation", e);
      System.out.println(e);
    }
  }

  /*****************************************************************************
   * public void createUniverseclass(TechPackInfo techpack){
   ****************************************************************************/
  private void createUniverseclass() {
    try {

      // Create TP content to Universeclass table

      final Universeclass unvClass = new Universeclass(rock);
      unvClass.setVersionid(oldVersionId);

      final UniverseclassFactory ucf = new UniverseclassFactory(rock, unvClass);
      final Iterator<Universeclass> it = ucf.get().iterator();

      while (it.hasNext()) {
        final Universeclass uc = it.next();
        final Universeclass newUc = new Universeclass(rock);

        // Create TP content to Universeclass table
        newUc.setVersionid(newVersionId);
        newUc.setClassname(uc.getClassname());
        newUc.setUniverseextension(uc.getUniverseextension());
        newUc.setDescription(uc.getDescription());
        newUc.setParent(uc.getParent());
        newUc.setObj_bh_rel(uc.getObj_bh_rel());
        newUc.setElem_bh_rel(uc.getElem_bh_rel());
        newUc.setInheritance(uc.getInheritance());
        newUc.setOrdernro(uc.getOrdernro());
        newUc.saveDB();
      }
    } catch (final Exception e) {
      logger.log(Level.SEVERE, "SQL error in create Universeclass", e);
      System.out.println(e);
    }
  }

  /*****************************************************************************
   * public void createUniversetable(TechPackInfo techpack){
   ****************************************************************************/
  private void createUniversetable() {
    try {

      // Create TP content to Universetable table
      final Universetable unvTable = new Universetable(rock);
      unvTable.setVersionid(oldVersionId);

      final UniversetableFactory utf = new UniversetableFactory(rock, unvTable);
      final Iterator<Universetable> it = utf.get().iterator();

      while (it.hasNext()) {
        final Universetable ut = it.next();
        final Universetable newUt = new Universetable(rock);

        // Create TP content to Universetable table
        newUt.setVersionid(newVersionId);
        newUt.setTablename(ut.getTablename());
        newUt.setOwner(ut.getOwner());
        newUt.setAlias(ut.getAlias());
        newUt.setUniverseextension(ut.getUniverseextension());
        newUt.setObj_bh_rel(ut.getObj_bh_rel());
        newUt.setElem_bh_rel(ut.getElem_bh_rel());
        newUt.setInheritance(ut.getInheritance());
        newUt.setOrdernro(ut.getOrdernro());
        newUt.saveDB();
      }
    } catch (final Exception e) {
      logger.log(Level.SEVERE, "SQL error in create Universetable", e);
      System.out.println(e);
    }
  }

  /*****************************************************************************
   * public void createUniversejoin(TechPackInfo techpack){
   ****************************************************************************/
  private void createUniversejoin() {
    try {

      // Create TP content to Universejoin table
      final Universejoin unvJoin = new Universejoin(rock);
      unvJoin.setVersionid(oldVersionId);

      final UniversejoinFactory ujf = new UniversejoinFactory(rock, unvJoin);
      final Iterator<Universejoin> it = ujf.get().iterator();

      while (it.hasNext()) {
        final Universejoin uj = it.next();
        final Universejoin newUj = new Universejoin(rock);

        // Create TP content to Universejoin table
        newUj.setVersionid(newVersionId);
        newUj.setSourcetable(uj.getSourcetable());
        newUj.setSourcelevel(uj.getSourcelevel());
        newUj.setSourcecolumn(uj.getSourcecolumn());
        newUj.setTargettable(uj.getTargettable());
        newUj.setTargetlevel(uj.getTargetlevel());
        newUj.setTargetcolumn(uj.getTargetcolumn());
        newUj.setExpression(uj.getExpression());
        newUj.setCardinality(uj.getCardinality());
        newUj.setContext(uj.getContext());
        newUj.setExcludedcontexts(uj.getExcludedcontexts());
        // 20110908 TR HO73838 EANGUAN :: copying the universe extensions also to the newly created TP
        newUj.setUniverseextension(uj.getUniverseextension());
        // TmpCounter will not be set, because it is defined as autoincrement in
        // the database.
        // newUj.setTmpcounter(uj.getTmpcounter());
        newUj.setOrdernro(uj.getOrdernro());
        newUj.saveDB();
      }
    } catch (final Exception e) {
      logger.log(Level.SEVERE, "SQL error in create Universejoin", e);
      System.out.println(e);
    }
  }

  /*****************************************************************************
   * public void createUniverseobject(TechPackInfo techpack){
   ****************************************************************************/
  private void createUniverseobject() {
    try {

      // Create TP content to Universeobject table
      final Universeobject unvObject = new Universeobject(rock);
      unvObject.setVersionid(oldVersionId);

      final UniverseobjectFactory uof = new UniverseobjectFactory(rock, unvObject);
      final Iterator<Universeobject> it = uof.get().iterator();

      while (it.hasNext()) {
        final Universeobject uo = it.next();
        final Universeobject newUo = new Universeobject(rock);

        // Create TP content to Universeobject table
        newUo.setVersionid(newVersionId);
        newUo.setClassname(uo.getClassname());
        newUo.setUniverseextension(uo.getUniverseextension());
        newUo.setObjectname(uo.getObjectname());
        newUo.setDescription(uo.getDescription());
        newUo.setObjecttype(uo.getObjecttype());
        newUo.setQualification(uo.getQualification());
        newUo.setAggregation(uo.getAggregation());
        newUo.setObjselect(uo.getObjselect());
        newUo.setObjwhere(uo.getObjwhere());
        newUo.setPrompthierarchy(uo.getPrompthierarchy());
        newUo.setObj_bh_rel(uo.getObj_bh_rel());
        newUo.setElem_bh_rel(uo.getElem_bh_rel());
        newUo.setInheritance(uo.getInheritance());
        newUo.setOrdernro(uo.getOrdernro());
        newUo.saveDB();
      }
    } catch (final Exception e) {
      logger.log(Level.SEVERE, "SQL error in create Universeobject", e);
      System.out.println(e);
    }
  }

  /*****************************************************************************
   * public void createUniversecomputedobject(TechPackInfo techpack){
   ****************************************************************************/
  private void createUniversecomputedobject() {
    try {

      // Create TP content to Universecomputedobject table
      final Universecomputedobject unvComObj = new Universecomputedobject(rock);
      unvComObj.setVersionid(oldVersionId);

      final UniversecomputedobjectFactory ucof = new UniversecomputedobjectFactory(rock, unvComObj);
      final Iterator<Universecomputedobject> it = ucof.get().iterator();

      while (it.hasNext()) {
        final Universecomputedobject uco = it.next();
        final Universecomputedobject newUco = new Universecomputedobject(rock);

        // Create TP content to Universecomputedobject table
        newUco.setVersionid(newVersionId);
        newUco.setClassname(uco.getClassname());
        newUco.setUniverseextension(uco.getUniverseextension());
        newUco.setObjectname(uco.getObjectname());
        newUco.setDescription(uco.getDescription());
        newUco.setObjecttype(uco.getObjecttype());
        newUco.setQualification(uco.getQualification());
        newUco.setAggregation(uco.getAggregation());
        newUco.setObjselect(uco.getObjselect());
        newUco.setObjwhere(uco.getObjwhere());
        newUco.setPrompthierarchy(uco.getPrompthierarchy());
        newUco.setObj_bh_rel(uco.getObj_bh_rel());
        newUco.setElem_bh_rel(uco.getElem_bh_rel());
        newUco.setInheritance(uco.getInheritance());
        newUco.setOrdernro(uco.getOrdernro());
        newUco.saveDB();
      }
    } catch (final Exception e) {
      logger.log(Level.SEVERE, "SQL error in create Universecomputedobject", e);
      System.out.println(e);
    }
  }

  /*****************************************************************************
   * public void createUniversecondition(TechPackInfo techpack){
   ****************************************************************************/
  private void createUniversecondition() {
    try {

      // Create TP content to Universecondition table
      final Universecondition unvCondition = new Universecondition(rock);
      unvCondition.setVersionid(oldVersionId);

      final UniverseconditionFactory ucf = new UniverseconditionFactory(rock, unvCondition);
      final Iterator<Universecondition> it = ucf.get().iterator();

      while (it.hasNext()) {
        final Universecondition ucond = it.next();
        final Universecondition newUcond = new Universecondition(rock);

        // Create TP content to Universecondition table
        newUcond.setVersionid(newVersionId);
        newUcond.setClassname(ucond.getClassname());
        newUcond.setUniverseextension(ucond.getUniverseextension());
        newUcond.setUniversecondition(ucond.getUniversecondition());
        newUcond.setDescription(ucond.getDescription());
        newUcond.setCondwhere(ucond.getCondwhere());
        newUcond.setAutogenerate(ucond.getAutogenerate());
        newUcond.setCondobjclass(ucond.getCondobjclass());
        newUcond.setCondobject(ucond.getCondobject());
        newUcond.setPrompttext(ucond.getPrompttext());
        newUcond.setMultiselection(ucond.getMultiselection());
        newUcond.setFreetext(ucond.getFreetext());
        newUcond.setObj_bh_rel(ucond.getObj_bh_rel());
        newUcond.setElem_bh_rel(ucond.getElem_bh_rel());
        newUcond.setInheritance(ucond.getInheritance());
        newUcond.setOrdernro(ucond.getOrdernro());
        newUcond.saveDB();
      }
    } catch (final Exception e) {
      logger.log(Level.SEVERE, "SQL error in create Universecondition", e);
      System.out.println(e);
    }
  }

  /*****************************************************************************
   * public void createUniverseformulas(TechPackInfo techpack){
   ****************************************************************************/
  private void createUniverseformulas() {
    try {

      // Create TP content to Universeformulas table
      final Universeformulas unvFormulas = new Universeformulas(rock);
      unvFormulas.setVersionid(oldVersionId);

      final UniverseformulasFactory uff = new UniverseformulasFactory(rock, unvFormulas);
      final Iterator<Universeformulas> it = uff.get().iterator();

      while (it.hasNext()) {

        final Universeformulas uform = it.next();
        final Universeformulas newUform = new Universeformulas(rock);

        // Create TP content to Universeformulas table
        newUform.setVersionid(newVersionId);
        newUform.setTechpack_type(uform.getTechpack_type());
        newUform.setName(uform.getName());
        newUform.setFormula(uform.getFormula());
        newUform.setObjecttype(uform.getObjecttype());
        newUform.setQualification(uform.getQualification());
        newUform.setAggregation(uform.getAggregation());
        newUform.setOrdernro(uform.getOrdernro());
        newUform.saveDB();
      }
    } catch (final Exception e) {
      logger.log(Level.SEVERE, "SQL error in create Universeformulas", e);
      System.out.println(e);
    }
  }

  /*****************************************************************************
   * public void createUniverseparameters(TechPackInfo techpack){
   ****************************************************************************/
  private void createUniverseparameters() {
    try {

      // Create TP content to Universeparameters table
      final Universeparameters unvParameters = new Universeparameters(rock);
      unvParameters.setVersionid(oldVersionId);

      final UniverseparametersFactory uff = new UniverseparametersFactory(rock, unvParameters);
      final Iterator<Universeparameters> it = uff.get().iterator();

      while (it.hasNext()) {

        final Universeparameters uParam = it.next();
        final Universeparameters newUParam = new Universeparameters(rock);

        // Create TP content to Universeparameters table
        newUParam.setVersionid(newVersionId);
        newUParam.setClassname(uParam.getClassname());
        newUParam.setObjectname(uParam.getObjectname());
        newUParam.setUniverseextension(uParam.getUniverseextension());
        newUParam.setOrdernro(uParam.getOrdernro());
        newUParam.setName(uParam.getName());
        newUParam.setTypename(uParam.getTypename());
        newUParam.saveDB();
      }
    } catch (final Exception e) {
      logger.log(Level.SEVERE, "SQL error in create Universeparameters", e);
      System.out.println(e);
    }
  }

  /*****************************************************************************
   * public void createVerificationCondition(TechPackInfo techpack){
   ****************************************************************************/
  private void createVerificationCondition() {
    try {

      // Create TP content to Verificationcondition table
      final Verificationcondition verCond = new Verificationcondition(rock);
      verCond.setVersionid(oldVersionId);

      final VerificationconditionFactory vcf = new VerificationconditionFactory(rock, verCond);
      final Iterator<Verificationcondition> it = vcf.get().iterator(); // Verificationcondition
      // iterator

      while (it.hasNext()) {

        final Verificationcondition vcond = it.next();
        final Verificationcondition newVCond = new Verificationcondition(rock);

        // Create TP content to Verificationcondition table
        newVCond.setVersionid(newVersionId);
        newVCond.setFacttable(vcond.getFacttable());
        newVCond.setVerlevel(vcond.getVerlevel());
        newVCond.setConditionclass(vcond.getConditionclass());
        newVCond.setVercondition(vcond.getVercondition());
        newVCond.setPromptname1(vcond.getPromptname1());
        newVCond.setPromptvalue1(vcond.getPromptvalue1());
        newVCond.setPromptname2(vcond.getPromptname2());
        newVCond.setPromptvalue2(vcond.getPromptvalue2());
        newVCond.setObjectcondition(vcond.getObjectcondition());
        newVCond.setPromptname3(vcond.getPromptname3());
        newVCond.setPromptvalue3(vcond.getPromptvalue3());
        newVCond.saveDB();
      }
    } catch (final Exception e) {
      logger.log(Level.SEVERE, "SQL error in create VerificationCondition", e);
      System.out.println(e);
    }
  }

  /*****************************************************************************
   * public void createVerificationObject(TechPackInfo techpack){
   ****************************************************************************/
  private void createVerificationObject() {
    try {

      // Create TP content to VerificationObject table
      final Verificationobject verObj = new Verificationobject(rock);
      verObj.setVersionid(oldVersionId);

      final VerificationobjectFactory vof = new VerificationobjectFactory(rock, verObj);
      final Iterator<Verificationobject> it = vof.get().iterator(); // Verificationobject
      // iterator

      while (it.hasNext()) {

        final Verificationobject vobj = it.next();
        final Verificationobject newVObj = new Verificationobject(rock);

        // Create TP content to Verificationobject table
        newVObj.setVersionid(newVersionId);
        newVObj.setMeastype(vobj.getMeastype());
        newVObj.setMeaslevel(vobj.getMeaslevel());
        newVObj.setObjectclass(vobj.getObjectclass());
        newVObj.setObjectname(vobj.getObjectname());
        newVObj.saveDB();
      }
    } catch (final Exception e) {
      logger.log(Level.SEVERE, "SQL error in create VerificationObject", e);
      System.out.println(e);
    }
  }

  /*****************************************************************************
   * public void removeTransforms(String versionid){ Removes TP rows from
   * Transformation and Transformer tables
   ****************************************************************************/
  private void removeTransforms(final String versionid) {
    try {

      // Removes Tech Pack content from Transformation and Transformer tables

      final Transformer tt = new Transformer(rock);
      tt.setVersionid(versionid);

      final TransformerFactory trf = new TransformerFactory(rock, tt);
      final Iterator<Transformer> it = trf.get().iterator(); // Transformer
                                                             // iterator

      while (it.hasNext()) {

        final Transformer t = it.next();

        final String tmptransformerid = t.getTransformerid();

        Transformation tf = new Transformation(rock);
        tf.setTransformerid(tmptransformerid);

        final TransformationFactory tff = new TransformationFactory(rock, tf);
        final Iterator<Transformation> itf = tff.get().iterator(); // Transformation
        // iterator

        while (itf.hasNext()) {

          tf = itf.next();
          tf.deleteDB();
        }

        t.deleteDB();
      }

    } catch (final Exception e) {
      logger.log(Level.SEVERE, "SQL error in remove Transforms", e);
      System.out.println(e);
    }
  }

  /*****************************************************************************
   * public void updateTechPack(NewTechPackInfo techpack)
   ****************************************************************************/
  public void updateTechPack(final NewTechPackInfo techpack, final RockFactory rock) {
    try {

      this.rock = rock;
      rock.getConnection().setAutoCommit(false);

      // Store the new techpack information obejct.
      this.techpack = techpack;

      // Get the new techpack versionId
      this.newVersionId = techpack.getName() + ":" + techpack.getVersion();

      // Get the old techpack versionId
      this.oldVersionId = techpack.getFromTechPack();

      // Get the ENIQ_LEVEL for the old techpack.
      this.oldEniqLevel = Utils.getTechpackEniqLevel(rock, oldVersionId);

      // Create from exist Tech Pack
      if (this.oldVersionId != "") {
        updateVersioning();
        logger.log(Level.INFO, "Techpack updated to Versioning table");
        updateUniversename();
        logger.log(Level.INFO, "Techpack updated to UniverseName table");
        updateSupportedvendorrelease();
        logger.log(Level.INFO, "Techpack updated to SupportedVendorRelease table");
        updateTechpackdependency();
        logger.log(Level.INFO, "Techpack updated to Techpackdependency table");

        rock.getConnection().commit();
      }
    } catch (final Exception e) {
      try {
        rock.getConnection().rollback();
      } catch (final Exception ex) {
        ExceptionHandler.instance().handle(ex);
        ex.printStackTrace();
      }
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    } finally {
      try {
        rock.getConnection().setAutoCommit(true);
      } catch (final SQLException ey) {

        ExceptionHandler.instance().handle(ey);
        ey.printStackTrace();
      }
    }
  }

  /*****************************************************************************
   * public void updateVersioning(String versionid){
   ****************************************************************************/
  private void updateVersioning() {
    try {

      // Insert content to Versioning table:
      final Versioning ver = new Versioning(rock);
      ver.setVersionid(newVersionId);
      ver.setTechpack_version(techpack.getRstate());
      ver.setLicensename(techpack.getLicenseName());
      ver.setDescription(techpack.getDescription());
      ver.setInstalldescription(techpack.getInstallDescription());
      ver.updateDB();
    } catch (final Exception e) {
      logger.log(Level.SEVERE, "SQL error in update Versioning", e);
      System.out.println(e);
    }
  }

  private String decode(final String typeid, final String versionId) throws Exception {
    // fetch the meas delta calc support information
    final Vector<Object> deltecalcsupport = new Vector<Object>();

    final Supportedvendorrelease scr = new Supportedvendorrelease(rock);
    scr.setVersionid(versionId);
    final SupportedvendorreleaseFactory scrF = new SupportedvendorreleaseFactory(rock, scr);

    final Iterator<Supportedvendorrelease> scrFI = scrF.get().iterator();
    while (scrFI.hasNext()) {
      final Supportedvendorrelease sven = scrFI.next();

      final Measurementdeltacalcsupport mdcs = new Measurementdeltacalcsupport(rock);
      mdcs.setVendorrelease(sven.getVendorrelease());
      mdcs.setVersionid(versionId);
      mdcs.setTypeid(typeid);

      final MeasurementdeltacalcsupportFactory mdcsF = new MeasurementdeltacalcsupportFactory(rock, mdcs);

      boolean found = false;
      final Iterator<Measurementdeltacalcsupport> mdcsFI = mdcsF.get().iterator();
      while (mdcsFI.hasNext()) {
        final Measurementdeltacalcsupport mdel = mdcsFI.next();
        deltecalcsupport.add(mdel);
        found = true;
      }

      if (!found) {
        mdcs.setDeltacalcsupport(1);
        deltecalcsupport.add(mdcs);
      }
    }

    return Utils.decodeMeasurementDeltaCalcSupport(deltecalcsupport);
  }

  /*****************************************************************************
   * public void updateSupportedvendorrelease(TechPackInfo techpack){
   ****************************************************************************/
  private void updateSupportedvendorrelease() {
    try {

      final List Vendorrelease = techpack.getVendorRelease();
      final Vector<Measurementdeltacalcsupport> mList = new Vector<Measurementdeltacalcsupport>();
      final int rows = Vendorrelease.size();

      // gather the original
      final Measurementdeltacalcsupport mdcs1 = new Measurementdeltacalcsupport(rock);
      mdcs1.setVersionid(newVersionId);
      final MeasurementdeltacalcsupportFactory mdcsF1 = new MeasurementdeltacalcsupportFactory(rock, mdcs1);
      mList.addAll(mdcsF1.get());

      // remove the old ones
      removeMeasurementDeltaCalcSupport(newVersionId);
      removeSupportedvendorrelease(newVersionId);

      // update the content to Supportedvendorrelease table:
      for (int i = 0; i < rows; i++) {

        final Supportedvendorrelease svr = new Supportedvendorrelease(rock);
        svr.setVersionid(newVersionId);
        svr.setVendorrelease(Vendorrelease.get(i).toString());
        svr.saveDB();
      }

      // remove measurementvectors for removed vendor releases
      final Measurementtype mt = new Measurementtype(rock);
      mt.setVersionid(newVersionId);
      final MeasurementtypeFactory mtF = new MeasurementtypeFactory(rock, mt);

      final Iterator<Measurementtype> mtFI = mtF.get().iterator();
      while (mtFI.hasNext()) {
        final Measurementtype mtype = mtFI.next();

        final Measurementvector mv = new Measurementvector(rock);
        mv.setTypeid(mtype.getTypeid());
        final MeasurementvectorFactory mvF = new MeasurementvectorFactory(rock, mv);

        final Iterator<Measurementvector> mvFI = mvF.get().iterator();
        while (mvFI.hasNext()) {
          final Measurementvector mvec = mvFI.next();
          if (!Vendorrelease.contains(mvec.getVendorrelease())) {
            mvec.deleteDB();
          }
        }
      }

      // update Measurementdeltacalcsupport to match the vendor release
      for (int i = 0; i < mList.size(); i++) {
        final Measurementdeltacalcsupport mdcs = mList.get(i);
        // does the old vendorrelease still exist
        if (Vendorrelease.contains(mdcs.getVendorrelease())) {
          // yes
          // add deltacalc also
          final Measurementdeltacalcsupport m = new Measurementdeltacalcsupport(rock);
          m.setVendorrelease(mdcs.getVendorrelease());
          m.setVersionid(mdcs.getVersionid());
          m.setTypeid(mdcs.getTypeid());
          m.setDeltacalcsupport(mdcs.getDeltacalcsupport());
          m.saveDB();
        }
      }

      // alter measuremenCounters Countaggregation
      final Measurementtype mt1 = new Measurementtype(rock);
      mt1.setVersionid(newVersionId);
      final MeasurementtypeFactory mtF1 = new MeasurementtypeFactory(rock, mt1);

      final Iterator<Measurementtype> mtFI1 = mtF1.get().iterator();
      while (mtFI1.hasNext()) {
        final Measurementtype mtype = mtFI1.next();

        // we dont want to do this to an non delta calc mtype...
        if (mtype.getDeltacalcsupport() == null || mtype.getDeltacalcsupport() == 0) {
          continue;
        }

        final Measurementcounter mc = new Measurementcounter(rock);
        mc.setTypeid(mtype.getTypeid());
        final MeasurementcounterFactory mcF = new MeasurementcounterFactory(rock, mc);

        final String d = decode(mtype.getTypeid(), newVersionId);

        final Iterator<Measurementcounter> mcFI = mcF.get().iterator();
        while (mcFI.hasNext()) {
          final Measurementcounter mcou = mcFI.next();
          mcou.setCountaggregation(d);
          mcou.saveDB();
        }
      }

    } catch (final Exception e) {
      logger.log(Level.SEVERE, "SQL error in update Supportedvendorrelease", e);
      System.out.println(e);
    }
  }

  /*****************************************************************************
   * public void updateUniversename(TechPackInfo techpack){
   ****************************************************************************/
  private void updateUniversename() {
    try {

      final List unvextlist = techpack.getUnvExt();

      int rows = unvextlist.size();
      boolean tmpExt = false;

      if ((rows == 0) && (techpack.getUnvName() != "")) {
        tmpExt = true;
        rows = 1; // Add row to table, but no universe extension
      }

      removeUniversename(newVersionId);

      for (int i = 0; i < rows; i++) {

        // Insert content to Universename table:
        final Universename unvname = new Universename(rock);
        unvname.setVersionid(newVersionId);
        unvname.setUniversename(techpack.getUnvName());
        if (tmpExt) {
          unvname.setUniverseextension("");
          unvname.setUniverseextensionname("");
        } else {
          final List trow = (List) unvextlist.get(i);
          unvname.setUniverseextension((String) trow.get(0));
          unvname.setUniverseextensionname((String) trow.get(1));
        }
        unvname.saveDB();
      }
    } catch (final Exception e) {
      logger.log(Level.SEVERE, "SQL error in update Universename", e);
      System.out.println(e);
    }
  }

  /*****************************************************************************
   * public void updateTechpackdependency(TechPackInfo techpack){
   ****************************************************************************/
  private void updateTechpackdependency() {
    try {
      final List tpdep = techpack.getTechPackDep();

      final int rows = tpdep.size();

      removeTechpackdependency(newVersionId);

      for (int i = 0; i < rows; i++) {

        // Insert content to Techpackdependency table:
        final Techpackdependency tpd = new Techpackdependency(rock);

        tpd.setVersionid(newVersionId);
        tpd.setTechpackname(tpdep.get(i).toString());
        tpd.setVersion(tpdep.get(i + 1).toString());
        tpd.saveDB();
        i++;
      }
    } catch (final Exception e) {
      logger.log(Level.SEVERE, "SQL error in update Techpackdependency", e);
      System.out.println(e);
    }
  }

  /**
   * Creates the generated reference table and column entries to the database
   * based on the special base techpack reference table entry.
   * 
   * @param baseReferenceTable
   * @throws Exception
   */
  private void createReferenceTypesForRankingTables(final Referencetable baseReferenceTable,
      final String techPackVersionId) throws Exception {

    // Iterate through all ranking table measurement types and generate
    // the "BHTYPE" reference table and column entries to the database.
    final Measurementtype mt = new Measurementtype(rock);
    mt.setVersionid(techPackVersionId);
    final MeasurementtypeFactory mtF = new MeasurementtypeFactory(rock, mt);
    final Iterator mtIter = mtF.get().iterator();
    while (mtIter.hasNext()) {
      final Measurementtype tmpMT = (Measurementtype) mtIter.next();
      if (tmpMT.getRankingtable().intValue() == 1) {

        // Create the type name by replacing the (SIM_RANKMT) in the
        // measurement type Object Name of the base reference table. For
        // example: "(DIM_RANKMT)_BHTYPE" for DC_E_MGW_AAL2APBH measurement type
        // will be "DIM_E_MGW_AAL2APBH_BHTYPE".
        final String typeName = baseReferenceTable.getTypename().replace("(DIM_RANKMT)",
            "DIM_" + tmpMT.getObjectname().replace("DC_", ""));
        final String typeId = techPackVersionId + ":" + typeName;

        // Create the reference table entry
        final Referencetable newRT = new Referencetable(rock);
        newRT.setTypeid(typeId);
        newRT.setVersionid(techPackVersionId);
        newRT.setTypename(typeName);
        newRT.setObjectid(typeId);
        newRT.setObjectname(typeName);
        newRT.setObjectversion(baseReferenceTable.getObjectversion());
        newRT.setObjecttype(baseReferenceTable.getObjecttype());
        newRT.setDescription(baseReferenceTable.getDescription());
        newRT.setStatus(baseReferenceTable.getStatus());
        newRT.setUpdate_policy(baseReferenceTable.getUpdate_policy());
        newRT.setTable_type(baseReferenceTable.getTable_type());
        newRT.setDataformatsupport(baseReferenceTable.getDataformatsupport());
        newRT.setBasedef(1);
        newRT.saveDB();

        // System.out.println("createReferenceTypesForRankinTables(): DEBUG: created reftype: "
        // + typeId);

        // Create the reference columns for this reference table.
        final Referencecolumn rc = new Referencecolumn(rock);
        rc.setTypeid(baseReferenceTable.getTypeid());
        final ReferencecolumnFactory rcF = new ReferencecolumnFactory(rock, rc);
        final Iterator rcIter = rcF.get().iterator();
        while (rcIter.hasNext()) {
          final Referencecolumn tmpRC = (Referencecolumn) rcIter.next();

          // Create the reference column entry
          final Referencecolumn newRC = new Referencecolumn(rock);
          newRC.setTypeid(typeId);
          newRC.setDataname(tmpRC.getDataname());
          newRC.setColnumber(tmpRC.getColnumber());
          newRC.setDatatype(tmpRC.getDatatype());
          newRC.setDatasize(tmpRC.getDatasize());
          newRC.setDatascale(tmpRC.getDatascale());
          newRC.setUniquevalue(tmpRC.getUniquevalue());
          newRC.setNullable(tmpRC.getNullable());
          newRC.setIndexes(tmpRC.getIndexes());
          newRC.setUniquekey(tmpRC.getUniquekey());
          newRC.setIncludesql(tmpRC.getIncludesql());
          newRC.setIncludeupd(tmpRC.getIncludeupd());
          newRC.setColtype("PUBLICCOL");
          newRC.setDescription(tmpRC.getDescription());
          newRC.setUniverseclass(tmpRC.getUniverseclass());
          newRC.setUniverseobject(tmpRC.getUniverseobject());
          newRC.setUniversecondition(tmpRC.getUniversecondition());
          newRC.setDataid(tmpRC.getDataid());
          newRC.setBasedef(1);
          newRC.saveDB();

          // System.out.println("createReferenceTypesForRankinTables(): DEBUG: created refcol: "
          // + newRC.getDataname());

        }
      }
    }
  }

  /**
   * Creates the generated reference table and column entries to the database
   * based on the special base techpack reference table entry.
   * 
   * @param baseReferenceTable
   * @throws Exception
   */
  private void createReferenceTypesForAggLevel(final Referencetable baseReferenceTable, final String techPackName,
      final String techPackVersionId) throws Exception {

    // Copy the "AGGLEVEL" reference table to the database.

    // Create the type name by replacing the (TPNAME) in the base reference type
    // typeName. For example: "SELECT_(TPNAME)_AGGLEVEL" for DC_E_MGW techpack
    // "SELECT_E_MGW_AGGLEVEL".
    final String typeName = baseReferenceTable.getTypename().replace("(TPNAME)", techPackName.replace("DC_", ""));
    final String typeId = techPackVersionId + ":" + typeName;

    // Create the reference table entry
    final Referencetable newRT = new Referencetable(rock);
    newRT.setTypeid(typeId);
    newRT.setVersionid(techPackVersionId);
    newRT.setTypename(typeName);
    newRT.setObjectid(typeId);
    newRT.setObjectname(typeName);
    newRT.setObjectversion(baseReferenceTable.getObjectversion());
    newRT.setObjecttype(baseReferenceTable.getObjecttype());
    newRT.setDescription(baseReferenceTable.getDescription());
    newRT.setStatus(baseReferenceTable.getStatus());
    newRT.setUpdate_policy(baseReferenceTable.getUpdate_policy());
    newRT.setTable_type(baseReferenceTable.getTable_type());
    newRT.setDataformatsupport(baseReferenceTable.getDataformatsupport());
    newRT.setBasedef(1);
    newRT.saveDB();

    // System.out.println("createReferenceTypesForAggLevel(): DEBUG: created reftype: "
    // + typeId);

  }

  /**
   * Creates the public reference column entries for the reference table entries
   * in the techpack based on the special base techpack reference table entry.
   * 
   * @param baseReferenceTable
   * @throws Exception
   */
  private void createPublicReferenceColumns(final Referencetable baseReferenceTable, final String techPackVersionId)
      throws Exception {

    // Create a vector for the public columns
    final Vector<Referencecolumn> refCols = new Vector<Referencecolumn>();

    // Create the public columns only if the base reference table is defined.
    if (baseReferenceTable != null) {

      // Get the public columns from the base reference table.
      final Referencecolumn rc = new Referencecolumn(rock);
      rc.setTypeid(baseReferenceTable.getTypeid());
      final ReferencecolumnFactory rcF = new ReferencecolumnFactory(rock, rc, "ORDER BY COLNUMBER");
      final Iterator rcIter = rcF.get().iterator();
      while (rcIter.hasNext()) {
        final Referencecolumn tmpRC = (Referencecolumn) rcIter.next();
        refCols.add(tmpRC);
      }

      // Iterate through all "non-base" reference tables in the techpack and
      // generate the public column entries to the database.
      final Referencetable rt = new Referencetable(rock);
      rt.setVersionid(techPackVersionId);
      final ReferencetableFactory rtF = new ReferencetableFactory(rock, rt);
      final Iterator rtIter = rtF.get().iterator();
      while (rtIter.hasNext()) {
        final Referencetable tmpRT = (Referencetable) rtIter.next();
        // Include only reference tables from current techpack, not from base.
        if (tmpRT.getBasedef() == null || (tmpRT.getBasedef() != null && tmpRT.getBasedef().intValue() != 1)) {
          // Fix for IDE #674, HK79489 TechPack IDE is adding columns to
          // topology tables, eeoidiv, 20090727
          // Public Reference Columns should only be added to topology tables
          // that have type Dynamic.
          // Topology tables that are type Predefined and Static should not have
          // those extra columns in any case.
          // Update_policy 2 is dynamic
          // [Static=0,Predefined=1,Dynamic=2,TimedDynamic=3]]
          // Update_policy 3 is Timed Dynamic, assume above 2 like Dynamic.
          // eeoidiv 20091203 : Timed Dynamic topology handling in ENIQ, WI
          // 6.1.2, (284/159 41-FCP 103 8147) Improved WRAN Topology in ENIQ
       	  // 20110830 EANGUAN :: Adding comparison for policy number 4 for History Dynamic (for SON) 
          if ((tmpRT.getUpdate_policy() == 2) || (tmpRT.getUpdate_policy() == 3) || (tmpRT.getUpdate_policy() == 4)) {
            // Count the number of existing columns for this reference table.
            // This
            // is needed for counting the column numbers for the public columns.
            long colNumber = 0;
            final Referencecolumn rc2 = new Referencecolumn(rock);
            rc2.setTypeid(tmpRT.getTypeid());
            final ReferencecolumnFactory rcF2 = new ReferencecolumnFactory(rock, rc2);
            colNumber = rcF2.get().size();

            colNumber = colNumber + 100L;

            // Create the reference columns for this reference table.
            final Iterator rcIter3 = refCols.iterator();
            while (rcIter3.hasNext()) {
              final Referencecolumn tmpRC = (Referencecolumn) rcIter3.next();

              // Increment the column number.
              colNumber++;

              // Create the public reference column entry
              final Referencecolumn newRC = new Referencecolumn(rock);
              newRC.setTypeid(tmpRT.getTypeid());
              newRC.setDataname(tmpRC.getDataname());
              newRC.setColnumber(colNumber);
              newRC.setDatatype(tmpRC.getDatatype());
              newRC.setDatasize(tmpRC.getDatasize());
              newRC.setDatascale(tmpRC.getDatascale());
              newRC.setUniquevalue(tmpRC.getUniquevalue());
              newRC.setNullable(tmpRC.getNullable());
              newRC.setIndexes(tmpRC.getIndexes());
              newRC.setUniquekey(tmpRC.getUniquekey());
              newRC.setIncludesql(tmpRC.getIncludesql());
              newRC.setIncludeupd(tmpRC.getIncludeupd());
              newRC.setColtype("PUBLICCOL");
              newRC.setDescription(tmpRC.getDescription());
              newRC.setUniverseclass(tmpRC.getUniverseclass());
              newRC.setUniverseobject(tmpRC.getUniverseobject());
              newRC.setUniversecondition(tmpRC.getUniversecondition());
              newRC.setDataid(tmpRC.getDataid());
              newRC.setBasedef(1);
              newRC.saveDB();

              // System.out.println("createReferenceTypesForRankingTables(): DEBUG: created public refcol: "
              // + tmpRC.getDataname() + " for table: " + tmpRT.getTypeid());
            }
          }
        }
      }
    }
  }

  /**
   * Get the column names for the given base reference table. An empty vector is
   * returned if the given table is null.
   * 
   * @param publicRefTable
   *          The reference table "PUBLIC_REFTYPE".
   * @return
   * @throws SQLException
   * @throws RockException
   */
  private Vector<String> getPublicColumnNamesFromBase(final Referencetable publicRefTable) throws SQLException,
      RockException {
    final Vector<String> retVector = new Vector<String>();

    if (publicRefTable != null) {
      final Referencecolumn rc = new Referencecolumn(rock);
      rc.setTypeid(publicRefTable.getTypeid());
      final ReferencecolumnFactory rcF = new ReferencecolumnFactory(rock, rc);
      final Iterator rcIter = rcF.get().iterator();
      while (rcIter.hasNext()) {
        final Referencecolumn tmpRC = (Referencecolumn) rcIter.next();
        retVector.add(tmpRC.getDataname());
      }
    }

    return retVector;
  }

  /**
   * Returns true if data conversion is needed for busy hour improvements. The
   * conversion is not needed if the old techpack level is equal to the latest
   * ENIQ level or the level where busy hour improvements were introduced.
   * 
   * @return true if data conversion is needed for busy hour improvements.
   */
  protected boolean isBusyHourConversionNeeded() {
    return (!(oldEniqLevel != null && (oldEniqLevel.equals(Constants.BH_IMPROVEMENT_ENIQ_LEVEL) || oldEniqLevel.equals(Constants.CURRENT_TECHPACK_ENIQ_LEVEL))));
  }

  /**
   * Converts the busy hours and aggregations.
   */
  protected void busyHourImprovementConversion() {

    try {
      // Iterate through all the busy hour supports, i.e. the ranking table
      // measurement types for the old techpack. Add default busy hour place
      // holder information to the database (5 product and 5 custom place
      // holders).
    	logger.log(Level.FINEST,
        "busyHourImprovementConversion(): Creating default busy hour place holders for measurement types.");
      final Measurementtype mtCond = new Measurementtype(rock);
      mtCond.setVersionid(oldVersionId);
      final MeasurementtypeFactory mtFactory = new MeasurementtypeFactory(rock, mtCond, true);
      for (final Measurementtype mt : mtFactory.get()) {

        // Only ranking tables are handled.
        if (Utils.replaceNull(mt.getRankingtable()).equals(1)) {

          // Create the (default) place holders for this ranking table.
          final Busyhourplaceholders newBHPH = new Busyhourplaceholders(rock);
          newBHPH.setVersionid(newVersionId);
          newBHPH.setBhlevel(mt.getTypename());
          newBHPH.setProductplaceholders(5);
          newBHPH.setCustomplaceholders(5);

          // Save the place holder.
          newBHPH.saveDB();
        }
      }// for (Measurementtype mt : mtFactory.get())

      // Current BHLevel for checking when place holder counter should be
      // reset.
      String currentBhLevel = "";

      // Iterate through all busy hours for old techpack. The order based on
      // the busy hour level, so that it is possible to calculate the
      // placeholder values for each busy hour support (ranking table
      // measurement type).
      logger
          .log(Level.FINEST, "busyHourImprovementConversion(): Converting existing busy hours as product busy hours.");
      int bhCount = 0;
      final Busyhour bhCond = new Busyhour(rock);
      bhCond.setVersionid(oldVersionId);
      final BusyhourFactory bhF = new BusyhourFactory(rock, bhCond, "ORDER BY BHLEVEL");
      for (final Busyhour oldBH : bhF.get()) {

        // Check if the BH Level has changed. If yes, then set the current BH
        // level and reset the place holder counter.
        if (!currentBhLevel.equalsIgnoreCase(oldBH.getBhlevel())) {
          currentBhLevel = oldBH.getBhlevel();
          bhCount = 0;
        }

        // Create a new busy hour object, copy and update the data.
        final Busyhour newBH = new Busyhour(rock);

        newBH.setVersionid(newVersionId);
        newBH.setBhlevel(oldBH.getBhlevel());
        newBH.setBhtype(genBhtype(Constants.BH_PRODUCT_PLACE_HOLDER_PREFIX, bhCount++));
        if (oldBH.getTargetversionid().equals(oldVersionId)) {
          newBH.setTargetversionid(newVersionId);
        } else {
        	newBH.setTargetversionid(getLatestActiveTP(oldBH.getTargetversionid()));
        }
        newBH.setBhobject(oldBH.getBhobject().toUpperCase());
        newBH.setAggregationtype(Constants.BH_AGGREGATION_TYPPES[0]);

        newBH.setBhelement(oldBH.getBhelement());

        newBH.setEnable(1);
        newBH.setDescription(oldBH.getDescription());
        newBH.setWhereclause(oldBH.getWhereclause());
        newBH.setBhcriteria(oldBH.getBhcriteria());
        newBH.setOffset(0);
        newBH.setWindowsize(60);
        newBH.setLookback(0);
        newBH.setP_threshold(0);
        newBH.setN_threshold(0);
        newBH.setClause("");
        newBH.setPlaceholdertype(Constants.BH_PRODUCT_PLACE_HOLDER_PREFIX);
        newBH.setGrouping(Constants.BH_GROUPING_TYPES[0]);
        newBH.setReactivateviews(0);

        // Save the new busy hour.
        newBH.saveDB();

        // Iterate through all sources for the old busy hour.
        final Busyhoursource bhsCond = new Busyhoursource(rock);
        bhsCond.setVersionid(oldBH.getVersionid());
        bhsCond.setBhlevel(oldBH.getBhlevel());
        bhsCond.setBhtype(oldBH.getBhtype());
        bhsCond.setTargetversionid(oldBH.getTargetversionid());
        bhsCond.setBhobject(oldBH.getBhobject());
        final BusyhoursourceFactory bhsF = new BusyhoursourceFactory(rock, bhsCond);
        for (final Busyhoursource bhs : bhsF.get()) {

          // Create a new busy hour source object, copy and update the data.
          final Busyhoursource newBHS = new Busyhoursource(rock);

          newBHS.setVersionid(newBH.getVersionid());
          newBHS.setBhlevel(newBH.getBhlevel());
          newBHS.setBhtype(newBH.getBhtype());
          newBHS.setTargetversionid(newBH.getTargetversionid());
          newBHS.setBhobject(newBH.getBhobject());
          newBHS.setTypename(bhs.getTypename());

          // Save the new busy hour source.
          newBHS.saveDB();

        }//for (Busyhoursource bhs : bhsF.get())

        // Iterate through all rank keys for the old busy hour.
        final Busyhourrankkeys bhrkCond = new Busyhourrankkeys(rock);
        bhrkCond.setVersionid(oldBH.getVersionid());
        bhrkCond.setBhlevel(oldBH.getBhlevel());
        bhrkCond.setBhtype(oldBH.getBhtype());
        bhrkCond.setTargetversionid(oldBH.getTargetversionid());
        bhrkCond.setBhobject(oldBH.getBhobject());
        final BusyhourrankkeysFactory bhrkF = new BusyhourrankkeysFactory(rock, bhrkCond);
        for (final Busyhourrankkeys bhrk : bhrkF.get()) {

          // Create a new busy hour rank keys, copy and update the data.
          final Busyhourrankkeys newBHRK = new Busyhourrankkeys(rock);

          newBHRK.setVersionid(newBH.getVersionid());
          newBHRK.setBhlevel(newBH.getBhlevel());
          newBHRK.setBhtype(newBH.getBhtype());
          newBHRK.setTargetversionid(newBH.getTargetversionid());
          newBHRK.setBhobject(newBH.getBhobject());
          newBHRK.setKeyname(bhrk.getKeyname());

          newBHRK.setKeyvalue(bhrk.getKeyvalue());
          newBHRK.setOrdernbr(bhrk.getOrdernbr());

          // Save the new busy hour source.
          newBHRK.saveDB();

        }//for (Busyhourrankkeys bhrk : bhrkF.get())
      }//for (Busyhour oldBH : bhF.get())

      // Iterate through all the newly created place holders for this
      // techpack. Create empty busy hours to fill in the place holders.
      logger.log(Level.FINEST, "busyHourImprovementConversion(): Creating empty busy hour place holders.");
      final Busyhourplaceholders bhphCond = new Busyhourplaceholders(rock);
      bhphCond.setVersionid(newVersionId);
      final BusyhourplaceholdersFactory bhphF = new BusyhourplaceholdersFactory(rock, bhphCond);
      for (final Busyhourplaceholders currentBHPH : bhphF.get()) {

        // Counter for existing product busy hours.
        int numOfProductBHs = 0;

        // Iterate through all the busy hours in this techpack matching the
        // versionId and bhLevel. Count the number of existing product busy
        // hours.
        final Busyhour bhCond2 = new Busyhour(rock);
        bhCond2.setVersionid(newVersionId);
        bhCond2.setBhlevel(currentBHPH.getBhlevel());
        final BusyhourFactory bhF2 = new BusyhourFactory(rock, bhCond2);
        for (final Busyhour bh : bhF2.get()) {

          if (bh.getPlaceholdertype().equals(Constants.BH_PRODUCT_PLACE_HOLDER_PREFIX)) {
            numOfProductBHs++;
          }
        }//for (Busyhour bh : bhF2.get())

        // Update the number of busy hour place holders in case there were
        // more than default number of busy hours in the old techpack.
        if (numOfProductBHs > Constants.DEFAULT_NUMBER_OF_BH_PRODUCT_PLACE_HOLDERS) {
          currentBHPH.setProductplaceholders(numOfProductBHs);
          currentBHPH.updateDB();
        }

        // Create empty product busy hours.
        for (int i = numOfProductBHs; i < Constants.DEFAULT_NUMBER_OF_BH_PRODUCT_PLACE_HOLDERS; i++) {
          final Busyhour newBH = new Busyhour(rock);
          newBH.setVersionid(newVersionId);
          newBH.setBhlevel(currentBHPH.getBhlevel());
          newBH.setBhtype(genBhtype(Constants.BH_PRODUCT_PLACE_HOLDER_PREFIX, i));

          // eeoidiv,20110307, Works for busy hours for the current techpacks. Custom techpacks do not have empty placeholders.
          
          newBH.setTargetversionid(newVersionId);

          newBH.setBhobject(currentBHPH.getBhlevel().substring(currentBHPH.getBhlevel().lastIndexOf("_") + 1,
              currentBHPH.getBhlevel().indexOf("BH")));
          newBH.setAggregationtype(Constants.BH_AGGREGATION_TYPPES[0]);

          if (currentBHPH.getBhlevel().endsWith("ELEMBH")) {
            newBH.setBhelement(1);
          } else {
            newBH.setBhelement(0);
          }
          newBH.setEnable(0);
          newBH.setDescription("");
          newBH.setWhereclause("");
          newBH.setBhcriteria("");
          newBH.setOffset(0);
          newBH.setWindowsize(60);
          newBH.setLookback(0);
          newBH.setP_threshold(0);
          newBH.setN_threshold(0);
          newBH.setClause("");
          newBH.setPlaceholdertype(Constants.BH_PRODUCT_PLACE_HOLDER_PREFIX);
          newBH.setGrouping(Constants.BH_GROUPING_TYPES[0]);
          newBH.setReactivateviews(0);

          // Save the new busy hour.
          newBH.saveDB();
        }//for (int i = numOfProductBHs; i < Constants.DEFAULT_NUMBER_OF_BH_PRODUCT_PLACE_HOLDERS; i++)

        // Create empty custom busy hours.
        for (int i = 0; i < Constants.DEFAULT_NUMBER_OF_BH_CUSTOM_PLACE_HOLDERS; i++) {
          final Busyhour newBH = new Busyhour(rock);
          newBH.setVersionid(newVersionId);
          newBH.setBhlevel(currentBHPH.getBhlevel());
          newBH.setBhtype(genBhtype(Constants.BH_CUSTOM_PLACE_HOLDER_PREFIX, i));

          // eeoidiv,20110307, Works for busy hours for the current techpacks. Custom techpacks do not have empty placeholders.
          
          newBH.setTargetversionid(newVersionId);

          newBH.setBhobject(currentBHPH.getBhlevel().substring(currentBHPH.getBhlevel().lastIndexOf("_") + 1,
              currentBHPH.getBhlevel().indexOf("BH")));
          newBH.setAggregationtype(Constants.BH_AGGREGATION_TYPPES[0]);

          if (currentBHPH.getBhlevel().endsWith("ELEMBH")) {
            newBH.setBhelement(1);
          } else {
            newBH.setBhelement(0);
          }
          newBH.setEnable(0);
          newBH.setDescription("");
          newBH.setWhereclause("");
          newBH.setBhcriteria("");
          newBH.setOffset(0);
          newBH.setWindowsize(60);
          newBH.setLookback(0);
          newBH.setP_threshold(0);
          newBH.setN_threshold(0);
          newBH.setClause("");
          newBH.setPlaceholdertype(Constants.BH_CUSTOM_PLACE_HOLDER_PREFIX);
          newBH.setGrouping(Constants.BH_GROUPING_TYPES[0]);
          newBH.setReactivateviews(0);

          // Save the new busy hour.
          newBH.saveDB();
        }//for (int i = 0; i < Constants.DEFAULT_NUMBER_OF_BH_CUSTOM_PLACE_HOLDERS; i++)
      }//for (Busyhourplaceholders currentBHPH : bhphF.get())

      // Create the busyhour mappings and aggregations for all the newly
      // created busy hour place holders.
      logger.log(Level.FINEST, "busyHourImprovementConversion(): Creating busy "
          + "hour mappings, RANKBH aggregations and aggregation rules.");
      final Busyhour newBhCond = new Busyhour(rock);
      newBhCond.setVersionid(newVersionId);
      final BusyhourFactory newBhF = new BusyhourFactory(rock, newBhCond);
      for (final Busyhour bh : newBhF.get()) {

        // Loop all supports and add mapping for each one with objbhsupport
        // value matching the bhobject.
        final Measurementobjbhsupport mobhsu = new Measurementobjbhsupport(rock);
        mobhsu.setObjbhsupport(bh.getBhobject());
        final MeasurementobjbhsupportFactory mobhsuf = new MeasurementobjbhsupportFactory(rock, mobhsu);
        for (final Measurementobjbhsupport sup : mobhsuf.get()) {

          if (bh.getBhobject().equalsIgnoreCase(sup.getObjbhsupport())) {

        	//Fix for TR HM90783 
          	//Don't want to create the Mappingtypes for ELEMBH Ranking table
          	//20101021, ejohabd, changed approved by ekatkil.
          	if (bh.getBhelement() == 1){
          	  continue;
          	}
          	// Skip if the versionId part of the typeId does not match the target techpack. Want to exclude other TPs with same Object Support name.
            if (!bh.getTargetversionid().equalsIgnoreCase(sup.getTypeid().substring(0, sup.getTypeid().lastIndexOf(":")))) {
              	continue;
            }
              
            // Skip the busy hour support itself
            if (sup.getTypeid().equalsIgnoreCase(bh.getTargetversionid() + Constants.TYPESEPARATOR + bh.getBhlevel())) {
                continue;
            }

            // Create a new mapping.
            // NOTE: New mappings will be enabled by default.
            final Busyhourmapping newBHM = new Busyhourmapping(rock);
            newBHM.setVersionid(bh.getVersionid());
            newBHM.setTargetversionid(bh.getTargetversionid());
            newBHM.setBhlevel(bh.getBhlevel());
            newBHM.setBhobject(sup.getObjbhsupport());
            newBHM.setBhtype(bh.getBhtype());
            newBHM.setTypeid(sup.getTypeid());
            newBHM.setBhtargettype(sup.getTypeid().substring(sup.getTypeid().lastIndexOf(":") + 1));
            newBHM.setBhtargetlevel(Utils.replaceNull(bh.getBhobject()).trim() + Constants.TYPENAMESEPARATOR
                + Utils.replaceNull(bh.getBhtype()));
            // Check if the new mapping already exists (not caring about enabled).
            final BusyhourmappingFactory busyhourmappings = new BusyhourmappingFactory(rock, newBHM);
            if(busyhourmappings.get().size()>0) {
            	break;
            }
            newBHM.setEnable(1);

            newBHM.insertDB();
          }//if (bh.getBhobject().equalsIgnoreCase(sup.getObjbhsupport()))
        }// for (Measurementobjbhsupport sup : mobhsuf.get())

        // get busyhour sources
        final Busyhoursource whereBusyhoursource = new Busyhoursource(rock);
        whereBusyhoursource.setVersionid(bh.getVersionid());
        whereBusyhoursource.setTargetversionid(bh.getTargetversionid());
        whereBusyhoursource.setBhlevel(bh.getBhlevel());
        whereBusyhoursource.setBhobject(bh.getBhobject());
        whereBusyhoursource.setBhtype(bh.getBhtype());

        final BusyhoursourceFactory busyhoursourceFactory = new BusyhoursourceFactory(rock, whereBusyhoursource, true);

        final Vector<Busyhoursource> busyhoursources = busyhoursourceFactory.get();

        // Creating RANKBH aggregations and aggregation rules for this busy
        // hour.
        Utils.createRankbhAggregationsForBusyhour(rock, bh, busyhoursources, bh.getVersionid());

      }

    } catch (final SQLException e) {
      logger.log(Level.SEVERE, "SQL error in createBusyhour", e);
    } catch (final RockException e) {
      logger.log(Level.SEVERE, "ROCK error in createBusyhour", e);
    } catch (final Exception e) {
      logger.log(Level.SEVERE, "FATAL error in createBusyhour", e);
    }
    //Need to clear the cache
    clearTheLatestActiveTP();
  } //busyHourImprovementConversion
  
  /**
   * This method is used to get the latest Active version of a TP.
   * It's used when migrating from an old Custom TP where the targetVersion
   * no longer exists/active on the system.
   * This method takes the "old" Busyhour and get's it's targetVersionId. It 
   * uses this value to search the Tpactivation table to determine the latest active TP.
   * 
   * @param targetVersionId
   * @return
   * @throws SQLException
   * @throws RockException
   */
  protected String getLatestActiveTP(final String targetVersionId) throws SQLException,
  RockException {	  
	  //Get the currently active TP...
	  final String techPackName = targetVersionId.substring(0, targetVersionId.lastIndexOf(":"));
	  
	  String latestActiveTP = isInLatestActiveTPCache(techPackName);
	  
	  //If The TP isn't already stored in the Cache...
	  if(latestActiveTP == null){
		  //Get the latest Active TP from the database.
		  final Tpactivation tpActivationSearch = new Tpactivation(rock);
		  tpActivationSearch.setTechpack_name(techPackName);
		  final TpactivationFactory tpActivationFactorySearch = new TpactivationFactory(rock, tpActivationSearch, true);
		  
		  //There will at most be one value returned from this, as there should only be one TP active at a time.
		  if(tpActivationFactorySearch.size()>0) {
			  latestActiveTP = tpActivationFactorySearch.getElementAt(0).getVersionid();
		  } else {
			  // No Active TP found, default to targetVersionId passed in (targetVersionId is non nullable).
			  latestActiveTP = targetVersionId;
		  }
		  
		  //Store the latest TP in the cache for repeat lookups.
		  updateLatestActiveTPCache(techPackName, latestActiveTP);
	  }
	  return latestActiveTP;
  }
  
  /**
   * Generate BusyHour bhtype, .e.g. PP0
   * Uses Techpack type, if CUSTOM do CTP_PP0/CTP_CP0
   * @return
   */
  protected String genBhtype(final String placeHolderType, final int index) {
	final StringBuffer result = new StringBuffer();
	if((techpack!=null) && (techpack.getType().equalsIgnoreCase(Constants.CUSTOM_TECHPACK))) {
		result.append(Constants.BH_CUSTOM_TP_PREFIX);
		result.append("_");
	}
	result.append(placeHolderType);
	result.append(index);
	return result.toString();
  } // genBhtype

  /**
   * This method updates the LatestActiveTPCache.
   * @param name
   * @param value
   */
  public void updateLatestActiveTPCache(final String name, final String value) {
	  if(latestActiveTPCache == null){
		  latestActiveTPCache = new HashMap<String, String>();
	  }
	  latestActiveTPCache.put(name, value);
  }
  
  /**
   * This method queries the cache and returns the value associated with the name.
   * NULL is returned if the name is not present in the cache.
   * @param name
   * @return
   */
  public String isInLatestActiveTPCache(final String name) {
	  if(latestActiveTPCache == null){
		  latestActiveTPCache = new HashMap<String, String>();
	  }
	  return latestActiveTPCache.get(name);
  }
  /**
   * This method clears the cahce.
   */
  public void clearTheLatestActiveTP() {
	  latestActiveTPCache = null;
  }
}//class NewTechPackFunctionality