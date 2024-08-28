package com.ericsson.eniq.etl.ebsHandler.action;

import com.ericsson.eniq.common.setWizards.CreateAggregatorSet;
import com.ericsson.eniq.common.setWizards.CreateLoaderSet;
import com.ericsson.eniq.common.setWizards.CreateLoaderSetFactory;
import com.ericsson.eniq.common.setWizards.SetCreator;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.velocity.app.Velocity;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.dwhm.PartitionAction;
import com.distocraft.dc5000.dwhm.StorageTimeAction;
import com.distocraft.dc5000.dwhm.VersionUpdateAction;
import com.distocraft.dc5000.etl.engine.main.EngineAdmin;
import com.distocraft.dc5000.etl.engine.structure.TransferActionBase;
import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.etl.rock.Meta_collection_setsFactory;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_databases;
import com.distocraft.dc5000.etl.rock.Meta_databasesFactory;
import com.distocraft.dc5000.etl.scheduler.SchedulerAdmin;
import com.distocraft.dc5000.repository.cache.ActivationCache;
import com.distocraft.dc5000.repository.cache.AggregationRuleCache;
import com.distocraft.dc5000.repository.cache.DataFormatCache;
import com.distocraft.dc5000.repository.cache.PhysicalTableCache;
import com.distocraft.dc5000.repository.dwhrep.Dataformat;
import com.distocraft.dc5000.repository.dwhrep.DataformatFactory;
import com.distocraft.dc5000.repository.dwhrep.Defaulttags;
import com.distocraft.dc5000.repository.dwhrep.DefaulttagsFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementtype;
import com.distocraft.dc5000.repository.dwhrep.MeasurementtypeFactory;
import com.distocraft.dc5000.repository.dwhrep.Tpactivation;
import com.distocraft.dc5000.repository.dwhrep.TpactivationFactory;
import com.distocraft.dc5000.repository.dwhrep.Universeformulas;
import com.distocraft.dc5000.repository.dwhrep.UniverseformulasFactory;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.distocraft.dc5000.repository.dwhrep.VersioningFactory;

/**
 */
public class EBSUpdater extends TransferActionBase {

    protected Logger log = Logger.getLogger("EBSUpdater");

    protected Meta_collections collection;

    protected RockFactory etlRepRockFactory;

    protected RockFactory dwhRockFactory;

    protected RockFactory dwhRepRockFactory;

    protected RockFactory dbaDwhRockFactory;

    private Versioning baseVersioning;

    private String baseversionid;

    private String templateDir;

    private List<String> comparedTypes;

    private List<String> ignoredParameters;

    private String ebsdefaults;

    final private Map<String, String> ebsTypes;

    private Map<String, String> ebsArchives;

    final private String afterUpdate = "move";

    final private String afterFailed = "move";

    private String tpName = "";

		private static final String LOCK = "lock";
		private static final String UNLOCK = "unlock";
	private String ebsPropFile = "/eniq/sw/conf/EBSManager.properties"; // default setting
	/**
	 * Default Scheduler retry delay in milliseconds
	 */
	private int retryDelay = 5000;

	/**
	 * Used for testing only
	 */
	public EBSUpdater(final Properties props, final Map<String, String> ebsTypes){
		this.ebsTypes = ebsTypes;
		if(props != null){
			retryDelay = Integer.parseInt(props.getProperty("retry.delay", "1000"));
			ebsPropFile = props.getProperty("propertyfile", "/eniq/sw/conf/EBSManager.properties");
		}
	}

    /**
     * @param properties EBS properties
     * @param etlrep     A connection to etlrep
     * @param log        Parent logger
     * @throws Exception If the db connection fails
     */
    public EBSUpdater(final Properties properties, final RockFactory etlrep, final Logger log) throws Exception {
        this.log = log;
        log.finest("Initializing");
        sendEventToListener("Initializing");
        log.finest("" + properties);
        tpName = properties.getProperty("tpName", "#all#");
        ebsPropFile = properties.getProperty("propertyfile", "/eniq/sw/conf/EBSManager.properties");
        log.fine("propertyfile: " + ebsPropFile);
        final Properties fileProperties = loadProperties(ebsPropFile);






        // get base techpack
        baseversionid = properties.getProperty("baseversionid", fileProperties.getProperty("baseversionid", ""));
        templateDir = properties.getProperty("templateDir", fileProperties.getProperty("templateDir", "5.2"));
        log.fine("templateDir: " + templateDir);
        ebsdefaults = properties
                .getProperty(
                        "ebsdefaults",
                        fileProperties
                                .getProperty(
                                "ebsdefaults",
                                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<techpack>\n<Versioning " +
                                "VERSIONID=\"\" DESCRIPTION=\"\" STATUS=\"\" TECHPACK_NAME=\"\" TECHPACK_VERSION=\"\" " +
                                "TECHPACK_TYPE=\"\" PRODUCT_NUMBER=\"\" LOCKEDBY=\"\" LOCKDATE=\"\" BASEDEFINITION=\"\" " +
                                "BASEVERSION=\"\" INSTALLDESCRIPTION=\"\" UNIVERSENAME=\"\" UNIVERSEEXTENSION=\"\" " +
                                "ENIQ_LEVEL=\"\" DiffStatus=\"\" />\n<Measurementtypeclass TYPECLASSID=\"\" VERSIONID=\"\" " +
                                "DESCRIPTION=\"\" DiffStatus=\"\" />\n<Measurementtype TYPEID=\"\" TYPECLASSID=\"\" " +
                                "TYPENAME=\"\" VENDORID=\"\" FOLDERNAME=\"\" DESCRIPTION=\"\" STATUS=\"\" VERSIONID=\"\" " +
                                "OBJECTID=\"\" OBJECTNAME=\"\" OBJECTVERSION=\"\" OBJECTTYPE=\"\" JOINABLE=\"\" SIZING=\"\" " +
                                "TOTALAGG=\"\" ELEMENTBHSUPPORT=\"\" RANKINGTABLE=\"\" DELTACALCSUPPORT=\"\" PLAINTABLE=\"\" " +
                                "UNIVERSEEXTENSION=\"\" VECTORSUPPORT=\"\" DATAFORMATSUPPORT=\"\" DiffStatus=\"\" />\n<Measurementkey " +
                                "TYPEID=\"\" DATANAME=\"\" DESCRIPTION=\"\" ISELEMENT=\"\" UNIQUEKEY=\"\" COLNUMBER=\"\" " +
                                "DATATYPE=\"\" DATASIZE=\"\" DATASCALE=\"\" UNIQUEVALUE=\"\" NULLABLE=\"\" INDEXES=\"\" " +
                                "INCLUDESQL=\"\" UNIVOBJECT=\"\" JOINABLE=\"\" DiffStatus=\"\" />\n<Measurementobjbhsupport " +
                                "TYPEID=\"\" OBJBHSUPPORT=\"\" DiffStatus=\"\" />\n<Measurementcounter TYPEID=\"\" " +
                                "DATANAME=\"\" DESCRIPTION=\"\" TIMEAGGREGATION=\"''\" GROUPAGGREGATION=\"''\" COUNTAGGREGATION=\"''\" " +
                                "COLNUMBER=\"\" DATATYPE=\"\" DATASIZE=\"\" DATASCALE=\"\" INCLUDESQL=\"\" UNIVOBJECT=\"\" " +
                                "UNIVCLASS=\"''\" COUNTERTYPE=\"\" COUNTERPROCESS=\"\" DATAID=\"\" DiffStatus=\"\" />\n<Measurementtable " +
                                "MTABLEID=\"\" TABLELEVEL=\"\" TYPEID=\"\" BASETABLENAME=\"\" DEFAULT_TEMPLATE=\"\" " +
                                "PARTITIONPLAN=\"\" DiffStatus=\"\" />\n<Measurementvector TYPEID=\"\" DATANAME=\"\" " +
                                "VENDORRELEASE=\"\" VINDEX=\"\" VFROM=\"\" VTO=\"\" MEASURE=\"\" " +
                                "DiffStatus=\"\" />\n<Measurementdeltacalcsupport TYPEID=\"\" VENDORRELEASE=\"\" " +
                                "DELTACALCSUPPORT=\"\" VERSIONID=\"\" DiffStatus=\"\" />\n<Measurementcolumn " +
                                "MTABLEID=\"\" DATANAME=\"\" COLNUMBER=\"\" DATATYPE=\"\" DATASIZE=\"\" DATASCALE=\"\" " +
                                "UNIQUEVALUE=\"\" NULLABLE=\"\" INDEXES=\"\" DESCRIPTION=\"\" DATAID=\"\" RELEASEID=\"\" " +
                                "UNIQUEKEY=\"\" INCLUDESQL=\"\" COLTYPE=\"\" DiffStatus=\"\" />\n<Referencetable TYPEID=\"\" " +
                                "VERSIONID=\"\" TYPENAME=\"\" OBJECTID=\"\" OBJECTNAME=\"\" OBJECTVERSION=\"\" OBJECTTYPE=\"\" " +
                                "DESCRIPTION=\"\" STATUS=\"\" UPDATE_POLICY=\"\" TABLE_TYPE=\"\" DATAFORMATSUPPORT=\"\" " +
                                "DiffStatus=\"\" />\n<Referencecolumn TYPEID=\"\" DATANAME=\"\" COLNUMBER=\"\" DATATYPE=\"\" " +
                                "DATASIZE=\"\" DATASCALE=\"\" UNIQUEVALUE=\"\" NULLABLE=\"\" INDEXES=\"\" UNIQUEKEY=\"\" " +
                                "INCLUDESQL=\"\" INCLUDEUPD=\"\" COLTYPE=\"\" DESCRIPTION=\"\" UNIVERSECLASS=\"\" " +
                                "UNIVERSEOBJECT=\"\" UNIVERSECONDITION=\"\" DiffStatus=\"\" />\n<Dataformat DATAFORMATID=\"\" " +
                                "TYPEID=\"\" VERSIONID=\"\" OBJECTTYPE=\"\" FOLDERNAME=\"\" DATAFORMATTYPE=\"\" " +
                                "DiffStatus=\"\" />\n<Defaulttags TAGID=\"\" DATAFORMATID=\"\" DESCRIPTION=\"\" " +
                                "DiffStatus=\"\" />\n<Dataitem DATAFORMATID=\"\" DATANAME=\"\" COLNUMBER=\"\" DATAID=\"\" " +
                                "PROCESS_INSTRUCTION=\"\" DiffStatus=\"\" />\n<Versioning VERSIONID=\"\" DESCRIPTION=\"\" " +
                                "STATUS=\"\" TECHPACK_NAME=\"\" TECHPACK_VERSION=\"\" TECHPACK_TYPE=\"\" PRODUCT_NUMBER=\"\" " +
                                "LOCKEDBY=\"\" LOCKDATE=\"\" BASEDEFINITION=\"\" BASEVERSION=\"\" INSTALLDESCRIPTION=\"\" " +
                                "UNIVERSENAME=\"\" UNIVERSEEXTENSION=\"\" ENIQ_LEVEL=\"\" DiffStatus=\"\" />\n<Aggregation " +
                                "AGGREGATION=\"\" VERSIONID=\"\" AGGREGATIONSET=\"\" AGGREGATIONGROUP=\"\" REAGGREGATIONSET=\"\" " +
                                "REAGGREGATIONGROUP=\"\" GROUPORDER=\"\" AGGREGATIONORDER=\"\" AGGREGATIONTYPE=\"\" " +
                                "AGGREGATIONSCOPE=\"\" DiffStatus=\"\" />\n<Externalstatement VERSIONID=\"\" STATEMENTNAME=\"\" " +
                                "EXECUTIONORDER=\"\" DBCONNECTION=\"\" STATEMENT=\"\" DiffStatus=\"\" />\n<Transformer " +
                                "TRANSFORMERID=\"\" VERSIONID=\"\" DESCRIPTION=\"\" TYPE=\"\" DiffStatus=\"\" />\n<Transformation " +
                                "TRANSFORMERID=\"\" ORDERNO=\"\" TYPE=\"\" SOURCE=\"\" TARGET=\"\" CONFIG=\"\" DESCRIPTION=\"\" " +
                                "DiffStatus=\"\" />\n<Supportedvendorrelease VERSIONID=\"\" VENDORRELEASE=\"\" " +
                                "DiffStatus=\"\" />\n<Techpackdependency VERSIONID=\"\" TECHPACKNAME=\"\" VERSION=\"\" " +
                                "DiffStatus=\"\" />\n<Vectorcounter MTABLEID=\"\" DATANAME=\"\" VENDORRELEASE=\"\" VINDEX=\"\" " +
                                "VFROM=\"\" VTO=\"\" MEASURE=\"\" DiffStatus=\"\" />\n<Universeclass VERSIONID=\"\" " +
                                "CLASSNAME=\"\" UNIVERSEEXTENSION=\"\" DESCRIPTION=\"\" PARENT=\"\" OBJ_BH_REL=\"\" " +
                                "ELEM_BH_REL=\"\" INHERITANCE=\"\" ORDERNRO=\"\" DiffStatus=\"\" />\n<Verificationobject " +
                                "VERSIONID=\"\" OBJNAME=\"\" TYPE=\"\" QUALIFICATION=\"\" CLASSNAME=\"\" AGGREGATION=\"\" " +
                                "OBJSELECT=\"\" OBJWHERE=\"\" DESCRIPTION=\"\" DiffStatus=\"\" />\n<Verificationcondition " +
                                "VERSIONID=\"\" CLASSNAME=\"\" CONDITION=\"\" CONDWHERE=\"\" DESCRIPTION=\"\" " +
                                "DiffStatus=\"\" />\n<Universetable VERSIONID=\"\" TABLENAME=\"\" UNIVERSEEXTENSION=\"\" " +
                                "OWNER=\"\" ALIAS=\"\" OBJ_BH_REL=\"\" ELEM_BH_REL=\"\" INHERITANCE=\"\" " +
                                "DiffStatus=\"\" />\n<Universename VERSIONID=\"\" UNIVERSENAME=\"\" UNIVERSEEXTENSION=\"\" " +
                                "DiffStatus=\"\" />\n<Universejoin VERSIONID=\"\" SOURCETABLE=\"\" SOURCELEVEL=\"\" " +
                                "SOURCECOLUMN=\"\" TARGETTABLE=\"\" TARGETLEVEL=\"\" TARGETCOLUMN=\"\" EXPRESSION=\"\" " +
                                "CARDINALITY=\"\" CONTEXT=\"\" EXCLUDEDCONTEXTS=\"\" DiffStatus=\"\" />\n<Universeobject " +
                                "VERSIONID=\"\" CLASSNAME=\"\" UNIVERSEEXTENSION=\"\" OBJECTNAME=\"\" DESCRIPTION=\"\" " +
                                "OBJECTTYPE=\"\" QUALIFICATION=\"\" AGGREGATION=\"\" OBJSELECT=\"\" OBJWHERE=\"\" " +
                                "PROMPTHIERARCHY=\"\" OBJ_BH_REL=\"\" ELEM_BH_REL=\"\" INHERITANCE=\"\" " +
                                "DiffStatus=\"\" />\n<Universecomputedobject VERSIONID=\"\" CLASSNAME=\"\" UNIVERSEEXTENSION=\"\" " +
                                "OBJECTNAME=\"\" DESCRIPTION=\"\" OBJECTTYPE=\"\" QUALIFICATION=\"\" AGGREGATION=\"\" " +
                                "OBJSELECT=\"\" OBJWHERE=\"\" PROMPTHIERARCHY=\"\" OBJ_BH_REL=\"\" ELEM_BH_REL=\"\" " +
                                "INHERITANCE=\"\" ORDERNRO=\"\" DiffStatus=\"\" />\n<Universecondition VERSIONID=\"\" " +
                                "CLASSNAME=\"\" UNIVERSEEXTENSION=\"\" UNIVERSECONDITION=\"\" DESCRIPTION=\"\" CONDWHERE=\"\" " +
                                "AUTOGENERATE=\"\" CONDOBJCLASS=\"\" CONDOBJECT=\"\" PROMPTTEXT=\"\" MULTISELECTION=\"\" " +
                                "FREETEXT=\"\" OBJ_BH_REL=\"\" ELEM_BH_REL=\"\" INHERITANCE=\"\" DiffStatus=\"\" />\n<Universeformulas " +
                                "VERSIONID=\"\" TECHPACK_TYPE=\"\" NAME=\"\" FORMULA=\"\" OBJECTTYPE=\"\" QUALIFICATION=\"\" " +
                                "AGGREGATION=\"\" DiffStatus=\"\" />\n<Universeparameters VERSIONID=\"\" CLASSNAME=\"\" " +
                                "OBJECTNAME=\"\" UNIVERSEEXTENSION=\"\" ORDERNRO=\"\" NAME=\"\" TYPENAME=\"\" " +
                                "DiffStatus=\"\" />\n</techpack>"));
        log.fine("ebsdefaults: " + ebsdefaults);
        // defines which tables are considered when doing the delta
        final String handledTables = properties.getProperty("handledTables", fileProperties.getProperty("handledTables",
                "Measurementcounter,Universeclass,Universecomputedobject,Universeparameters"));
        log.fine("handledTables: " + handledTables);
        comparedTypes = new ArrayList<String>();
        final String[] tokens11 = handledTables.split(",");
        for (String aTokens11 : tokens11) {
            log.fine("comparedTypes: " + aTokens11);
            comparedTypes.add(aTokens11);
        }
        // defines the table/column which are ignored when delta is calculated
        final String ignoredParametersStr = properties.getProperty("ignoredParameters", fileProperties.getProperty(
                "ignoredParameters", "Universecomputedobject/ORDERNRO"));
        log.fine("ignoredParameters: " + ignoredParametersStr);
        ignoredParameters = new ArrayList<String>();
        final String[] tokens1 = ignoredParametersStr.split(",");
        for (String aTokens1 : tokens1) {
            log.fine("ignoredParameters: " + aTokens1);
            ignoredParameters.add(aTokens1);
        }

        // get input directories
        final String ebsDirs = properties.getProperty("ebsDirs", fileProperties.getProperty("ebsDirs", ""));
        log.fine("ebsDirs: " + ebsDirs);
        ebsTypes = new HashMap<String, String>();
        final String[] tokens12 = ebsDirs.split(",");
        for (String aTokens12 : tokens12) {
            final String[] tokens13 = aTokens12.split("=");
            if (tokens13.length > 1) {
                log.fine("ebsTypes: " + tokens13[0] + " / " + tokens13[1]);
                ebsTypes.put(tokens13[0], tokens13[1]);
            } else {
                log.fine("No ebs types found in: " + aTokens12);
            }
        }

        // get archive directories
        final String ebsArchiveDirs = properties.getProperty("ebsArchiveDirs",
                fileProperties.getProperty("ebsArchiveDirs", ""));
        log.fine("ebsDirs: " + ebsArchiveDirs);
        ebsArchives = new HashMap<String, String>();
        final String[] tokens2 = ebsArchiveDirs.split(",");
        for (String aTokens2 : tokens2) {
            final String[] tokens3 = aTokens2.split("=");
            if (tokens3.length > 1) {
                log.fine("ebsTypes: " + tokens3[0] + " / " + tokens3[1]);
                ebsArchives.put(tokens3[0], tokens3[1]);
            } else {
                log.fine("No ebs types found in: " + aTokens2);
            }
        }

        this.etlRepRockFactory = etlrep;
        this.dwhRockFactory = getDwhRockFactory(this.etlRepRockFactory);
        if (this.dwhRockFactory == null) {
            throw new Exception("Database dwh is not defined in Meta_databases");
        }
        this.dwhRepRockFactory = getDwhRepRockFactory(this.etlRepRockFactory);
        if (this.dwhRepRockFactory == null) {
            throw new Exception("Database dwhrep is not defined in Meta_databases");
        }
        this.dbaDwhRockFactory = getDbaDwhRockFactory(this.etlRepRockFactory);
        if (this.dbaDwhRockFactory == null) {
            throw new Exception("Database DBA is not defined in Meta_databases");
        }
        try {
            log.fine("Intializing Velocity");
            if (System.getProperty("configtool.templatedir", null) != null) {
                Velocity.setProperty("file.resource.loader.path", System.getProperty("configtool.templatedir"));
            } else {
                Velocity.setProperty("resource.loader", "class");
                Velocity.setProperty("class.resource.loader.class",
                        "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
            }
            Velocity.init();
            log.fine("Velocity initialized");

        } catch (Exception e) {
            log.log(Level.SEVERE, "Velocity initialization failed", e);
        }
    }

    private void closeDbConnections(final RockFactory toClose) {
        if (toClose != null && toClose.getConnection() != null) {
            try {
                if (!toClose.getConnection().isClosed()) {
                    toClose.getConnection().close();
                }
            } catch (SQLException e) {
                // ignore
            } catch (Throwable t) {
                // ignore
            }
        }
    }

    /**
     * Loads properties from a property file..
     *
     * @param fileName the property file to load
     * @return object containing the properties
     */
    private Properties loadProperties(final String fileName) {
        log.info("Trying to read properties for ebs");
        final Properties props = new Properties();
        final FileInputStream fis;
        final File file;
        try {
            file = new File(fileName);
            fis = new FileInputStream(file);
            props.load(fis);
            fis.close();
            log.info("Succesfully loaded propertyfile:" + fileName);
        } catch (FileNotFoundException e) {
            log.info("File=" + fileName + " could not be found.");
        } catch (IOException e) {
            log.severe("IO Error : File=" + fileName + " could not be found.");
        }
        return props;
    }

    /**
     * Comparator for sorting files with last modified time
     */
    private final static Comparator<File> FILECOMPARATOR = new Comparator<File>() {
        public int compare(final File f1, final File f2) {
            final Long i1 = f1.lastModified();
            final Long i2 = f2.lastModified();
            return i1.compareTo(i2);
        }
    };

    /**
     * Reads the configuration files from a techpackName specified directory
     *
     * @param techpackName The tech pack name e.g. PM_E_EBSS
     * @return List containing the files
     */
    private List<File> readConfigurationFiles(final String techpackName) {
        final List<File> files = new ArrayList<File>();
        final String dirname = ebsTypes.get(techpackName);
        log.info("Searching configuration files for " + techpackName + " from directory " + dirname);
        sendEventToListener("Searching configuration files for " + techpackName + " from directory " + dirname);
        final File[] fileList = new File(dirname).listFiles();
        if (fileList == null) {
            log.info("Could not find directory " + dirname);
            return files;
        }
        files.addAll(Arrays.asList(fileList));
        Collections.sort(files, FILECOMPARATOR);
        log.info("found " + files.size() + " configuration files.");
        sendEventToListener("found " + files.size() + " configuration files.");
        return files;
    }

    /**
     * Updates/generates the measurement types in the DB with the information from
     * the pmmom
     *
     * @param mom         The mom to update
     * @param currVersion The current TP version
     * @throws Exception Db Errors
     */
    private void updateMeastypes(final Pmmom mom, final Versioning currVersion) throws Exception {
        for (Meastype mtype : mom.getMeastypes()) {
            log.info("Updating columns for Meastype: " + mtype.getName());
            sendEventToListener("Updating columns for Meastype: " + mtype.getName());
            final UpdateMeasurements um = new UpdateMeasurements(mtype.getMtype().getTypename(), currVersion,
                    baseVersioning, dwhRepRockFactory);
            um.update();
        }
    }

    /**
     * Gets the techpack (set) from DB
     *
     * @param techpackName the tech pack name
     * @param versionid    the tech pack version id
     * @return The collection set or null if nothing found
     * @throws Exception Db Errors
     */
    private Meta_collection_sets getTechpack(final String techpackName, final String versionid) throws Exception {
        final String version = versionid.substring(versionid.lastIndexOf(":") + 1);
        final Meta_collection_sets mts = new Meta_collection_sets(etlRepRockFactory);
        mts.setCollection_set_name(techpackName);
        mts.setVersion_number(version);
        final Meta_collection_setsFactory mtsF = new Meta_collection_setsFactory(etlRepRockFactory, mts);
        if (mtsF.get().size() > 0) {
            return mtsF.getElementAt(0);
        }
        return null;
    }

    /**
     * Gets the version id from DB.
     *
     * @param techpackName The tech pack name
     * @return The versionID or empty string if nothing found
     * @throws Exception Db errors
     */
    private String getVersionID(final String techpackName) throws Exception {
        final Tpactivation tpa = new Tpactivation(dwhRepRockFactory);
        tpa.setTechpack_name(techpackName);
        final TpactivationFactory tpaF = new TpactivationFactory(dwhRepRockFactory, tpa);
        if (tpaF.size() > 0) {
            return tpaF.getElementAt(0).getVersionid();
        }
        return "";
    }

    /**
     * Gets the versioning information from DB
     *
     * @param versionid The version id
     * @return The Versioning object or null if nothing found
     * @throws Exception Db errors
     */
    private Versioning getVersioning(final String versionid) throws Exception {
        final Versioning ver = new Versioning(dwhRepRockFactory);
        ver.setVersionid(versionid);
        final VersioningFactory verF = new VersioningFactory(dwhRepRockFactory, ver);
        if (verF.get().size() == 0) {
            return null;
        }
        return verF.getElementAt(0);
    }

    protected VersionUpdateAction getVersionUpdateAction(final String tpName) throws Exception {
        return new VersionUpdateAction(dwhRepRockFactory, dwhRockFactory, tpName, log);
    }

    private void importMom(final File file, final String techpackName, final String versionid,
                           final Versioning currVersion, final Meta_collection_sets techPack) throws Exception {
        final long start = System.currentTimeMillis();
        try {
            log.info("Reading configuration file " + file.getAbsolutePath() + " for " + techpackName);
            sendEventToListener("Reading configuration file " + file.getAbsolutePath() + " for "
                    + techpackName);
            final Pmmom mom = new Pmmom();
            mom.setTechpackName(techpackName);
            mom.setVersionid(versionid);
            final Vector<Map<String, Universeformulas>> fMap = getFormulas(versionid);
            mom.setFormulasMap(fMap);
            final Map<String, Measurementtype> mtMap = getMeastypes(versionid);
            mom.setMeastypesMap(mtMap);
            mom.read(file);
            log.info("ApplicationVersion: " + mom.getApplicationVersion());
            sendEventToListener("ApplicationVersion: " + mom.getApplicationVersion());
            log.info("PmMimVersion: " + mom.getPmMimVersion());
            sendEventToListener("PmMimVersion: " + mom.getPmMimVersion());
            final DoDelta delta = new DoDelta(log, comparedTypes, ignoredParameters);
            final CreateXML dd = new CreateXML(log, dwhRepRockFactory);
            log.info("Creating delta between configuration file(" + file.getAbsolutePath() + ") " +
                    "and EBS techpack (" + techpackName + ")");
            sendEventToListener("Creating delta between configuration file(" + file.getAbsolutePath()
                    + ") and EBS techpack (" + techpackName + ")");

            final String dbXML = dd.createXML(versionid, true, comparedTypes);
            
            // Fix for HM78432 - No need for the delta.

            final String momXML = mom.toXML(ebsdefaults);
            log.finest("momXML: " + momXML);
            log.info(momXML.length() + " rows read from MOM file.");
            sendEventToListener(momXML.length() + " rows read from MOM file.");

            delta.addTargetXML(dbXML);
            delta.addSourceXML(momXML);

            // update tables in ETLREP
            log.info("Updating tables in ETLREP");
            sendEventToListener("Updating tables in ETLREP");
            delta.updateDB(dwhRepRockFactory,versionid);

            log.info(delta.rowdUpdated() + " rows updated to DB.");
            sendEventToListener(delta.rowdUpdated() + " rows updated to DB.");
            if (delta.rowdUpdated() > 0) {
                updateMeastypes(mom, currVersion);
                log.info("Updating dataformats for Meastype: " + currVersion.getTechpack_name());
                sendEventToListener("Updating dataformats for Meastype: " + currVersion.getTechpack_name());
                final UpdateDataformats ud = new UpdateDataformats();
                ud.update(currVersion, dwhRepRockFactory);
                log.info("Updating Loaders for techpack: " + currVersion.getTechpack_name());
                sendEventToListener("Updating Loaders for techpack: " + currVersion.getTechpack_name());
                updateLoaderSets(techPack, versionid);
                log.info("Updating Aggregators for techpack: " + currVersion.getTechpack_name());
                sendEventToListener("Updating Aggregators for techpack: " + currVersion.getTechpack_name());
                updateAggregatorSets(techPack, versionid);
                log.info("Updating DWHManager for techpack: " + currVersion.getTechpack_name());
                sendEventToListener("Updating DWHManager for techpack: " + currVersion.getTechpack_name());
                final VersionUpdateAction vua = getVersionUpdateAction(currVersion.getTechpack_name());
                vua.execute(true);
                new StorageTimeAction(dwhRepRockFactory, etlRepRockFactory, dwhRockFactory, dbaDwhRockFactory,
                        currVersion.getTechpack_name(), log);
                new PartitionAction(dwhRepRockFactory, dwhRockFactory, currVersion.getTechpack_name(), log);

                // Proprietary cache refresh
                log.info("Revalidate DataFormatCache");
                sendEventToListener("Revalidate DataFormatCache");
                DataFormatCache.getCache().revalidate();
                log.info("Revalidate PhysicalTableCache");
                sendEventToListener("Revalidate PhysicalTableCache");
                PhysicalTableCache.getCache().revalidate();
                log.info("Revalidate ActivationCache");
                sendEventToListener("Revalidate ActivationCache");
                ActivationCache.getCache().revalidate();
                log.info("Revalidate AggregationRuleCache");
                sendEventToListener("Revalidate AggregationRuleCache");
                AggregationRuleCache.getCache().revalidate();
            }

            log.info("EBS file " + file.getAbsolutePath() + " update completed in "
                    + (System.currentTimeMillis() - start) + " ms. ");

            sendEventToListener("EBS file " + file.getAbsolutePath() + " updated");
            if (afterUpdate.equalsIgnoreCase("delete")) {
                log.info("Remove the file " + file.getAbsolutePath());
                if (!file.delete()) {
                    log.info("Remove of file " + file.getAbsolutePath() + " failed.");
                }
            }
            final String archiveDir = ebsArchives.get(techpackName) + "/archive/";
            if (afterUpdate.equalsIgnoreCase("move")) {
                final File arc = new File(archiveDir);
                arc.mkdirs();
                log.info("Move the file " + file.getAbsolutePath() + " to " + archiveDir);
                final File f = new File(archiveDir + file.getName());
                if (f.exists()) {
                    if (!f.delete()) {
                        log.info("Move the file " + file.getAbsolutePath() + " failed");
                    }
                }
                final boolean moveSuccess = file.renameTo(f);
                if (!moveSuccess) {
                    log.finer("renameTo failed. Moving with memory copy");
                    moveFile(file, f);
                }
            }
        } catch (Exception e) {
            sendEventToListener("EBS file " + file.getAbsolutePath() + " failed to upgrade : " + e.getMessage());
            log.log(Level.SEVERE, "Error while updating EBS: " + file.getAbsolutePath() + " ", e);
            if (afterFailed.equalsIgnoreCase("delete")) {
                log.info("Remove the file " + file.getAbsolutePath());
                file.delete();
            }
            final String failedDir = ebsArchives.get(techpackName) + "/failed/";
            if (afterFailed.equalsIgnoreCase("move")) {
                new File(failedDir).mkdirs();
                log.info("Move the file " + file.getAbsolutePath() + " to " + failedDir);
                final File f = new File(failedDir + file.getName());
                if (f.exists()) {
                    f.delete();
                }
                final boolean moveSuccess = file.renameTo(f);
                if (!moveSuccess) {
                    log.finer("renameTo failed. Moving with memory copy");
                    moveFile(file, f);
                }
            }
            throw e;

        }
    }

	protected String getLockUserColumnReturnName(){
		return "msg";
	}

	protected String getLockUserSPCommand(final String lockType, final String user){
		return "{call " + lockType + "_user " + user + "}";
	}
	protected String getDropConnectionsSPCommand(final String user){
		return "{call drop_user_connections " + user + "}";
	}

	private void doLockUnlock(final List<String> userList, final boolean undoIfErrors, final RockFactory dwhDbaConn,
														final String opType) {
			final List<String> locked = new ArrayList<String>(); // store whos locked so it can be undone if theres an error
			String currentUser = "?";
			try {
				for (String user : userList) {
					currentUser = user;
					final String lockSql = getLockUserSPCommand(opType, user);
					final CallableStatement lockCs = dwhDbaConn.getConnection().prepareCall(lockSql);
					try {
						final ResultSet rs = lockCs.executeQuery();
						if (rs.next()) {
							final String colName = getLockUserColumnReturnName();
							final String lockResult = rs.getString(colName);
							if (!lockResult.startsWith("User " + user + " " + opType + "ed at ")) {
								log.log(Level.SEVERE, "User " + user + " could not be " + opType + "ed : " + lockResult);
								break;
							} else {
								locked.add(user);
								log.log(Level.FINER, opType + "ed user " + user);
								sendEventToListener(opType + "ed user " + user);
							}
						} else {
							// no result back??????
							log.log(Level.SEVERE, "No result returned while attempting to " + opType + " user " + user + "!?!");
							break;
						}
						if (opType.equals(LOCK)) {
							final String dropSql = getDropConnectionsSPCommand(user);
							final CallableStatement dropCs = dwhDbaConn.getConnection().prepareCall(dropSql);
							try {
								final ResultSet dropRs = dropCs.executeQuery();
								if (dropRs.next()) {
									final String colName = getLockUserColumnReturnName();
									final String dropResult = rs.getString(colName);
									log.log(Level.FINER, "Dropped user connections for " + user);
									sendEventToListener("Dropped user connections for " + user);
									log.log(Level.FINER, "Drop " + dropResult);
									sendEventToListener("Drop " + dropResult);
								} else {
									log.log(Level.WARNING, "No msg returned from drop_user...??");
								}
							} finally {
								dropCs.close();
							}
						}
					} finally {
						lockCs.close();
					}
				}
			} catch (Throwable t) {
				log.log(Level.SEVERE, "Failed to " + opType + " user '" + currentUser + "'", t);
			}
			if (locked.size() != userList.size()) {
				String undoOp = LOCK;
				if (opType.equals(LOCK)) {
					undoOp = UNLOCK;
				}
				if (undoIfErrors) {
					doLockUnlock(locked, false, dwhDbaConn, undoOp);
				} else {
					log.log(Level.WARNING, "DBA needs to manually " + undoOp + " users " + locked + " using " + undoOp + "_user command (sp_iqlistlockedusers to show current locked users)");
				}
			}
		}

	protected List<String> getUsersToLock(){
		final String lKey = "upgrade.users.lock";
		final String _defaults = "dcbo;dcpublic";
		final Properties ebsProps = loadProperties(ebsPropFile);
		final String usersToLock = ebsProps.getProperty(lKey, _defaults);
		final StringTokenizer st = new StringTokenizer(usersToLock, ";");
		final List<String> users = new ArrayList<String>();
		while(st.hasMoreTokens()){
			users.add(st.nextToken());
		}
		return users;
	}

    /**
     * Collects file(s) from predefined (ebsTypes) directories and updates the
     * corresponding ebs techpack (also defined in ebsTypes) according these files
     */
		public void execute() throws Exception {
			try {
				final Long starttime = System.currentTimeMillis();
				log.info("EBS update starts");
				sendEventToListener("EBS update starts");
				final List<String> usersToLock = getUsersToLock();
				doLockUnlock(usersToLock, true, dbaDwhRockFactory, LOCK);
				try {
					doEbsUpgrade();
				} finally {
					doLockUnlock(usersToLock, true, dbaDwhRockFactory, UNLOCK);
				}
				log.info("Total EBS update completed in " + (System.currentTimeMillis() - starttime) + " ms. ");
				sendEventToListener("Total EBS update completed");
			} catch (Exception e) {
				log.severe("Error while updating EBS: " + e.getMessage());
				sendEventToListener("Error while updating EBS: ");
				throw e;

			} finally {
				closeDbConnections(dwhRockFactory);
				closeDbConnections(dwhRepRockFactory);
				closeDbConnections(dbaDwhRockFactory);
				resetEngine();
				resetScheduler();
			}
		}

	private void doEbsUpgrade() throws Exception {
		for (String techpackName : ebsTypes.keySet()) {
				if (!tpName.equalsIgnoreCase("#all#") && !tpName.equalsIgnoreCase(techpackName)) {
						// skip the not handled techpacks..
						continue;
				}
				final String versionid = getVersionID(techpackName);
				final Meta_collection_sets techPack = getTechpack(techpackName, versionid);
				final Versioning currVersion = getVersioning(versionid);
				if (baseversionid.length() == 0) {
						baseversionid = currVersion.getBasedefinition();
				}
				log.fine("baseversionid: " + baseversionid);
				baseVersioning = getVersioning(baseversionid);
				final List<File> files = readConfigurationFiles(techpackName);
				for (File file : files) {
						importMom(file, techpackName, versionid, currVersion, techPack);
				}
		}
	}

    private void moveFile(final File from, final File to) {
        try {
            final InputStream in = new FileInputStream(from);
            final OutputStream out = new FileOutputStream(to);
            final byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            from.delete();
        } catch (Exception ex) {
            log.log(Level.WARNING, "Move with memory copy failed", ex);
        }
    }

	protected SchedulerAdmin getSchedulerAdmin() throws Exception {
		return new SchedulerAdmin();
	}

	protected void resetScheduler() {
		log.info("Refreshing Schedulers.");
		sendEventToListener("Refreshing Schedulers.");
		boolean able;
		int retries = 0;
		boolean schedulerResetOK = false;
		do {
			try{
				final SchedulerAdmin sa = getSchedulerAdmin();
				sa.activate_silent();
				schedulerResetOK = true;
				break;
			} catch (Exception e) {
				log.severe("EBSUpdater could not refresh the schedulers, will retry in "+retryDelay/1000+" seconds : " + e);
				try {
					Thread.sleep(retryDelay);
				} catch (InterruptedException e1) {/**/}
				retries++;
				able = false;
			}
		} while (!able && retries<3);
		if(!schedulerResetOK){
			sendEventToListener("Warning : Could not refresh Scheduler automatically, please refresh manually.");
		} else {
			sendEventToListener("Scheduler automatically refreshed.");
		}
	}
	protected EngineAdmin getEngineAdmin(){
		try{
		return new EngineAdmin();
	}catch (Exception e) {
		return null;
	}
}

    protected void resetEngine() {
          try {
			final EngineAdmin ea = getEngineAdmin();
			if (ea != null)
			{
				ea.changeProfile("Normal");
			}
			else
			{
				sendEventToListener("EBSUpdater could not change the engine profile. Please do this manually");
				return;
			}
			log.info("Engine profile changed to Normal");
            sendEventToListener("Engine profile changed to Normal");

        } catch (Exception e) {
            log.warning(""+e);
            log.info("EBSUpdater could not change the engine profile. Please do this manually");
            sendEventToListener("EBSUpdater could not change the engine profile. Please do this manually");

        }
    }

    /**
     * Creates a loader set creating object, and updates the sets.
     *
     * @param techPack  The tech pack name.
     * @param versionId The version id.
     * @throws Exception Errors creating the set.
     */
    private void updateLoaderSets(final Meta_collection_sets techPack, final String versionId) throws Exception {
        final String setName = techPack.getCollection_set_name();
        final String setVersion = techPack.getVersion_number();
        final int techPackId = techPack.getCollection_set_id().intValue();
        final String techPackName = techPack.getCollection_set_name();
        final boolean scheduling = true;
        // Create the CreateLoaderSet
        /*final SetCreator loaderSetCreator = getLoaderSetCreator(templateDir, setName, setVersion, versionId,
                this.dwhRepRockFactory, this.etlRepRockFactory, techPackId, techPackName, scheduling);
        loaderSetCreator.removeSets();
        loaderSetCreator.create(false);*/

        //Fix for TR HP63145 
        final CreateLoaderSet loaderSetCreator = CreateLoaderSetFactory.createLoaderSet
        		(templateDir, setName, setVersion, versionId,this.dwhRepRockFactory, this.etlRepRockFactory, techPackId, techPackName, scheduling);
        log.info("Creating loaderSetCreator");
        sendEventToListener("Creating loaderSetCreator");
        loaderSetCreator.removeSets();
        log.info("Removed the sets using removeSets");
        sendEventToListener("Removed the sets using removeSets");
        loaderSetCreator.create(false);
        log.info("Create the set using create(false)");
        sendEventToListener("Create the set using create(false)");
    }

    

    /**
     * Creates an aggregator set creating object, and updates the sets.
     *
     * @param techPack  The tech pack name
     * @param versionId The version id
     * @throws Exception Errors creating the set
     */
    private void updateAggregatorSets(final Meta_collection_sets techPack, final String versionId) throws Exception {
        final String setName = techPack.getCollection_set_name();
        final String setVersion = techPack.getVersion_number();
        final int techPackId = techPack.getCollection_set_id().intValue();
        final boolean scheduling = true;
        // Create the CreateLoaderSet
        final SetCreator aggregatorSetCreator = getAggregatorSetCreator(templateDir, setName, setVersion, versionId,
                this.dwhRepRockFactory, this.etlRepRockFactory, techPackId, scheduling);
        aggregatorSetCreator.removeSets();
        aggregatorSetCreator.create(false);
    }

    protected RockFactory getDbaDwhRockFactory(final RockFactory etlRepRock) {
        return getRockFactory("DBA", etlRepRock, "dwh");
    }

    protected RockFactory getDwhRockFactory(final RockFactory etlRepRock) {
        return getRockFactory("USER", etlRepRock, "dwh");
    }

    protected RockFactory getDwhRepRockFactory(final RockFactory etlRepRock) {
        return getRockFactory("USER", etlRepRock, "dwhrep");
    }

    protected RockFactory getRockFactory(final String type, final RockFactory etlRepRock, final String name) {
        RockFactory result = null;
        try {
            final Meta_databases md_cond = new Meta_databases(etlRepRock);
            md_cond.setType_name(type);
            final Meta_databasesFactory md_fact = new Meta_databasesFactory(etlRepRock, md_cond);
            final Vector<Meta_databases> dbs = md_fact.get();
            for (Meta_databases db : dbs) {
                if (db.getConnection_name().equalsIgnoreCase(name)) {
                    result = new RockFactory(db.getConnection_string(), db.getUsername(), db.getPassword(),
                            db.getDriver_name(), "EBSHandler", true);
                }
            }
        } catch (Exception e) {
            result = null;
        }
        return result;
    }

    /**
     * Creates a loader set creator/updater
     *
     * @param templateDir  Template dir
     * @param setName      The set name
     * @param setVersion   The set version
     * @param versionId    The tp versionID
     * @param dwhRepRock   dwhrep connection
     * @param etlRepRock   etlrep connewction
     * @param techPackId   Techpack ID
     * @param techPackName Tech pack name
     * @param schedulings  Include schedulings
     * @return The SetCreator
     * @throws Exception Errors
     */
    protected SetCreator getLoaderSetCreator(final String templateDir, final String setName, final String setVersion,
                                             final String versionId, final RockFactory dwhRepRock,
                                             final RockFactory etlRepRock, final int techPackId,
                                             final String techPackName, final boolean schedulings) throws Exception {
        return new CreateLoaderSet(templateDir, setName, setVersion, versionId, dwhRepRock, etlRepRock,
          techPackId, techPackName, schedulings) {
          @Override
          protected String getLoaderActionTypeName() {
            return "Loader";
          }
        };
    }

    /**
     * Creates a aggregator set creator/updater
     *
     * @param templateDir Template dir
     * @param setName     The set name
     * @param setVersion  The set version
     * @param versionId   The tp versionID
     * @param dwhRepRock  dwhrep connection
     * @param etlRepRock  etlrep connewction
     * @param techPackId  Techpack ID
     * @param schedulings Include schedulings
     * @return The Aggregator Set creator
     * @throws Exception Errors
     */
    protected SetCreator getAggregatorSetCreator(final String templateDir, final String setName, final String setVersion,
                                                 final String versionId, final RockFactory dwhRepRock,
                                                 final RockFactory etlRepRock, final int techPackId,
                                                 final boolean schedulings) throws Exception {
        return new CreateAggregatorSet(templateDir, setName, setVersion, versionId, dwhRepRock,
                etlRepRock, techPackId, schedulings);
    }

    /**
     * Retrieves the universe formulas from DB.
     * <p/>
     * Order is following: #1 search formulas from own techpack #2 search formulas
     * from own techpacks base techpack #3 search formulas from other non base
     * techpacks #4 search formulas from other base techpacks
     * <p/>
     * Only unique names are fetched.
     *
     * @param versionid The versionID
     * @return List containing the universe formulas
     * @throws Exception Errors
     */
    private Vector<Map<String, Universeformulas>> getFormulas(final String versionid) throws Exception {
        final Vector<Map<String, Universeformulas>> result = new Vector<Map<String, Universeformulas>>();

        // #1 search from own techpack
        final Map<String, Universeformulas> r1 = new HashMap<String, Universeformulas>();
        final Universeformulas uf = new Universeformulas(dwhRepRockFactory);
        uf.setVersionid(versionid);
        final UniverseformulasFactory ufF = new UniverseformulasFactory(dwhRepRockFactory, uf);
        final List<Universeformulas> forms = ufF.get();
        for (Universeformulas f : forms) {
            r1.put(f.getName(), f);
        }
        result.add(r1);

        // #2 search from own techpacks base techpack
        final Map<String, Universeformulas> r2 = new HashMap<String, Universeformulas>();
        final Versioning v = new Versioning(dwhRepRockFactory);
        v.setVersionid(versionid);
        final VersioningFactory vF = new VersioningFactory(dwhRepRockFactory, v);
        final Universeformulas _uf2 = new Universeformulas(dwhRepRockFactory);
        _uf2.setVersionid(vF.getElementAt(0).getBasedefinition());
        final UniverseformulasFactory ufF2 = new UniverseformulasFactory(dwhRepRockFactory, _uf2);
        final Vector<Universeformulas> frms = ufF2.get();
        for (Universeformulas u : frms) {
            r2.put(u.getName(), u);
        }
        result.add(r2);

        // #3 search from other non base techpacks
        final Map<String, Universeformulas> r3 = new HashMap<String, Universeformulas>();
        final Universeformulas _uf3 = new Universeformulas(dwhRepRockFactory);
        final UniverseformulasFactory ufF3 = new UniverseformulasFactory(dwhRepRockFactory, _uf3);
        final List<Universeformulas> uf3 = ufF3.get();
        for (Universeformulas u : uf3) {
            if (u.getTechpack_type() == null || !u.getTechpack_type().equals("BASE")) {
                r3.put(u.getName(), u);
            }
        }
        result.add(r3);

        // #4 search from other base techpacks
        final Map<String, Universeformulas> r4 = new HashMap<String, Universeformulas>();
        final Universeformulas uf4 = new Universeformulas(dwhRepRockFactory);
        uf4.setTechpack_type("BASE");
        final UniverseformulasFactory ufF4 = new UniverseformulasFactory(dwhRepRockFactory, uf4);
        final List<Universeformulas> moreFrms = ufF4.get();
        for (Universeformulas u : moreFrms) {
            r4.put(u.getName(), u);
        }
        result.add(r4);
        return result;
    }

    /**
     * Creates a map containing meastypes of the given version.
     *
     * @param versionid The tp versionID
     * @return List of measurement types
     * @throws Exception Errors
     */
    private Map<String, Measurementtype> getMeastypes(final String versionid) throws Exception {
        final Map<String, Measurementtype> result = new HashMap<String, Measurementtype>();
        final Dataformat dt = new Dataformat(dwhRepRockFactory);
        dt.setVersionid(versionid);
        final DataformatFactory dtF = new DataformatFactory(dwhRepRockFactory, dt);
        for (Dataformat dformat : dtF.get()) {
            final Defaulttags dft = new Defaulttags(dwhRepRockFactory);
            dft.setDataformatid(dformat.getDataformatid());
            final DefaulttagsFactory dftF = new DefaulttagsFactory(dwhRepRockFactory, dft);
            for (Defaulttags deftags : dftF.get()) {
                final Measurementtype mt = new Measurementtype(dwhRepRockFactory);
                mt.setTypeid(dformat.getTypeid());
                final MeasurementtypeFactory mtF = new MeasurementtypeFactory(dwhRepRockFactory, mt);
                result.put(deftags.getTagid(), mtF.getElementAt(0));
            }
        }
        return result;
    }

    /*
    * for Junit tests
    */
    protected Meta_collection_sets getTechPack(final RockFactory etlRepRock, final Long techPackId) {
        final Meta_collection_sets mcs_cond = new Meta_collection_sets(etlRepRock);
        mcs_cond.setCollection_set_id(techPackId);
        try {
            final Meta_collection_setsFactory mcs_fact = new Meta_collection_setsFactory(etlRepRock, mcs_cond);
            final Vector<Meta_collection_sets> tps = mcs_fact.get();
            return tps.get(0);
        } catch (Exception e) {
            return null;
        }
    }
}
