/**
 * 
 */
package com.ericsson.eniq.afj.upgrade;

import static com.ericsson.eniq.afj.common.PropertyConstants.DATAFORMAT_DELIMITER;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROPERTY_DELIMITER;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROPERTY_UNDERSCORE;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_COLTYPE;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_DATASCALE;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_DATASIZE;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_DEFAULT_DIRECTORYCHECKER;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_DEFAULT_PARTITIONPLAN;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_DEFAULT_STATUS;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_DEFAULT_STORAGE_TIME;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_DEFAULT_TABLE_LEVEL;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_DEFAULT_TYPE;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_DEF_DATAFORMATSUPPORT;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_DEF_DELTACALCSUPPORT;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_DEF_DESCRIPTION;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_DEF_ELEMENTBHSUPPORT;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_DEF_JOINABLE;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_DEF_OBJECTTYPE;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_DEF_PARTITIONPLAN;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_DEF_PARTITIONTYPE;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_DEF_PLAINTABLE;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_DEF_PROCESSINSTRUCTION;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_DEF_RANKINGTABLE;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_DEF_SIZING;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_DEF_STATUS;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_DEF_TABLELEVEL;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_DEF_TECHPACKVERSION;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_DEF_TOTALAGG;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_DEF_TYPE;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_DEF_UNIVERSEEXTENSION;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_DEF_VECTORSUPPORT;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_ETLDATA_DIR;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_ETLDATA_JOINED_DIR;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_FOLLOWJOHN;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_IDE_TEMPLATES;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_INCLUDESQL;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_INDEXES;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_INTERFACE;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_IQLOADER_DIR;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_KEYS;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_MEASNAME;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_MOM;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_NULLABLE;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_PARSER;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_REJECTED_DIR;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_RELEASEID;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_TEMPLATE_DIR;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_UNIQUEKEY;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_UNIQUEVALUE;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_UNIVERSECLASS;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_UNIVERSEOBJECT;

import java.io.File;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.dwhm.VersionUpdateAction;
import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.repository.dwhrep.Dataformat;
import com.distocraft.dc5000.repository.dwhrep.Dataitem;
import com.distocraft.dc5000.repository.dwhrep.Defaulttags;
import com.distocraft.dc5000.repository.dwhrep.Dwhtype;
import com.distocraft.dc5000.repository.dwhrep.Interfacemeasurement;
import com.distocraft.dc5000.repository.dwhrep.Measurementcolumn;
import com.distocraft.dc5000.repository.dwhrep.Measurementcounter;
import com.distocraft.dc5000.repository.dwhrep.Measurementkey;
import com.distocraft.dc5000.repository.dwhrep.Measurementtable;
import com.distocraft.dc5000.repository.dwhrep.Measurementtype;
import com.distocraft.dc5000.repository.dwhrep.Measurementtypeclass;
import com.distocraft.dc5000.repository.dwhrep.Transformation;
import com.distocraft.dc5000.repository.dwhrep.Transformer;
import com.distocraft.dc5000.repository.dwhrep.Typeactivation;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.afj.common.AFJKey;
import com.ericsson.eniq.afj.common.AFJMeasurementCounter;
import com.ericsson.eniq.afj.common.AFJMeasurementType;
import com.ericsson.eniq.afj.common.CommonSetGenerator;
import com.ericsson.eniq.afj.common.CommonSetGeneratorFactory;
import com.ericsson.eniq.afj.common.PropertiesUtility;
import com.ericsson.eniq.afj.common.PropertyConstants;
import com.ericsson.eniq.afj.database.AFJDatabaseCommonUtils;
import com.ericsson.eniq.afj.database.AFJDatabaseCommonUtilsFactory;
import com.ericsson.eniq.dwhm.CreateViewsActionFactory;
import com.ericsson.eniq.dwhm.PartitionActionFactory;
import com.ericsson.eniq.dwhm.StorageTimeActionFactory;
import com.ericsson.eniq.dwhm.VersionUpdateActionFactory;
import com.ericsson.eniq.exception.AFJException;
import com.ericsson.eniq.repository.ActivationCacheWrapper;
import com.ericsson.eniq.repository.DataFormatCacheWrapper;
import com.ericsson.eniq.repository.PhysicalTableCacheWrapper;
import com.ericsson.eniq.techpacksdk.CreateLoaderSetFactory;
import com.ericsson.eniq.techpacksdk.CreateTPDirCheckerSetFactory;
import com.ericsson.eniq.common.setWizards.CreateLoaderSet;
import com.ericsson.eniq.common.setWizards.CreateTPDirCheckerSet;

/**
 * @author esunbal
 *
 */
public class CommonUpgradeUtil {
	
	private final Logger log = Logger.getLogger(this.getClass().getName());
	
	private final RockFactory dwhrep;	
	
	private final RockFactory etlrep;
	
	private final RockFactory dwh;
	
	private final RockFactory dbadwh;
	
	private final boolean isCalledByNVU = true;
	
	final private AFJDatabaseCommonUtils commonUtils = AFJDatabaseCommonUtilsFactory.getInstance();
	
	public CommonUpgradeUtil(final RockFactory dwhrep, final RockFactory etlrep,final RockFactory dwh,final RockFactory dbadwh){
		this.dwhrep = dwhrep;
		this.etlrep = etlrep;
		this.dwh = dwh;
		this.dbadwh = dbadwh;
	}
	
		
	
	public void initialiseCaches(){
		// Initialise the caches.
		log.info("Initialising DataFormatCache.");
		DataFormatCacheWrapper.initialize(etlrep);
        
		log.info("Initialize PhysicalTableCache");
		PhysicalTableCacheWrapper.initialize(etlrep);
	}
	
	/**
	 * Initialisation method for the CommonSetGenerator class.
	 * @param techpackName
	 * @return
	 * @throws AFJException
	 * @throws Exception
	 */
	public CommonSetGenerator initialiseCommonSetGenerator(final String techpackName) throws AFJException, Exception{
		final String tpVersion = commonUtils.getActiveTechPackVersion(techpackName, dwhrep);
		final String versionId = getVersion(tpVersion);
		final Meta_collection_sets techPack = commonUtils.getTechpack(techpackName, versionId, etlrep);
		final String setName = techPack.getCollection_set_name();
		final String setVersion = techPack.getVersion_number();
		final int techPackId = techPack.getCollection_set_id().intValue();
		final String templateDir = PropertiesUtility.getProperty(PROP_TEMPLATE_DIR);
		
		final CommonSetGenerator csg = CommonSetGeneratorFactory.getInstance(templateDir,
				setName, setVersion, dwhrep, etlrep,
				techPackId, setName, true);
		
		return csg;
	}	

	
	/**
	 * Returns only the version e.g. for input string DC_E_STN:((2)), output = ((2))
	 * Used in meta_collection_sets querying. 
	 * @param techpackVersion
	 * @return
	 * @throws AFJException
	 */
	private String getVersion(final String techpackVersion) throws AFJException{
		String version = null;
		version = techpackVersion.substring(techpackVersion.indexOf(":")+1);		
		return version;
	}
	
	/**
	 * Method to create the required dwhrep entries for adding a new counter. The method inserts rows in the following tables:
	 * 1.) DataItem
	 * 2.) MeasurementColumn
	 * 3.) MeasurementCounter
	 * 
	 * @param tpName
	 * @param mType
	 * @param mCounter
	 * @param measColumnNumber
	 * @throws AFJException
	 */
	public void createDwhrepEntries(final String tpName, final String mType, final AFJMeasurementCounter mCounter, final long measColumnNumber, final long measCounterNumber) throws AFJException{	
		
		try{		
		// Populate the common fields for dataitem, measurmentcolumn and measurementcounter tables.
		log.info("TechPackName:"+tpName);
		final String dataName = mCounter.getCounterName();
		log.info("DataName:"+dataName);
		String dataId = dataName;		
		
    	if(dataName.startsWith("SCABISDEL")){
          int index = dataName.indexOf("_");
          index ++;
          dataId = dataName.substring(index);
                                    
    	}
    	else{
    		dataId = dataName;
	}
		
		log.info("DataId:"+dataId);
		final String dataType = PropertiesUtility.getProperty(PROP_MOM+mCounter.getResultType());
		log.info("DataType:"+dataType);
		if(dataType == null){
			log.info("Data type not defined for ResultType:"+mCounter.getResultType()+" in "+mCounter.getCounterName());
			throw new AFJException("Data type not defined for ResultType:"+mCounter.getResultType()+" in "+mCounter.getCounterName());
		}
		final String description = mCounter.getDescription();
		log.info("Description:"+description);
		final int includeSQL = Integer.parseInt(PropertiesUtility.getProperty(PROP_INCLUDESQL)); // Defaulting it to 0
		final int followJohn =Integer.parseInt(PropertiesUtility.getProperty(PROP_FOLLOWJOHN)); // Its a follow John counter
		
		// Get the dataitem insert elements ready.
		String dataFormatId = mType + DATAFORMAT_DELIMITER;		
		final String parserProperty = tpName + PROPERTY_DELIMITER + PROP_PARSER;
		log.info("parserProperty:"+parserProperty);
		dataFormatId = dataFormatId + PropertiesUtility.getProperty(parserProperty);
		
		log.info("DataFormatId:"+dataFormatId);		
		log.info("ColNumber:"+measColumnNumber);		
		final String processInstruction = getProcessInstruction(mCounter.getType()); // Should not return null.
		log.info("ProcessInstruction:"+processInstruction);		
		
		final String dataSizeProperty = tpName + PROPERTY_DELIMITER + PROP_DATASIZE;
		log.info("dataSizeProperty:"+dataSizeProperty);
		final int dataSize = Integer.parseInt(PropertiesUtility.getProperty(dataSizeProperty+PROPERTY_DELIMITER+processInstruction+PROPERTY_DELIMITER+dataType));
		log.info("DataSize:"+dataSize);
		final String dataScaleProperty = tpName + PROPERTY_DELIMITER + PROP_DATASCALE;
		log.info("dataScaleProperty:"+dataScaleProperty);
		final int dataScale = Integer.parseInt(PropertiesUtility.getProperty(dataScaleProperty+PROPERTY_DELIMITER+processInstruction+PROPERTY_DELIMITER+dataType));
		log.info("DataScale:"+dataScale);
		
		// Actual dataitem object for db insert.
		final Dataitem dataItemObject = getDataItemObject(dataFormatId, dataName, measColumnNumber, dataId, processInstruction, dataType, dataSize, dataScale);		
		final int dataItemInsertValue = dataItemObject.insertDB();
		log.info("Insert value for dataItem:"+dataItemInsertValue);
		log.info("Insert statement for dataItem:"+dataItemObject.toSQLInsert());
		
		// Populate the specific measurementcolumn fields.
		final String mTableId = mType + DATAFORMAT_DELIMITER + PropertiesUtility.getProperty(PROP_DEF_PARTITIONTYPE);
		final long uniqueValue = Long.parseLong(PropertiesUtility.getProperty(PROP_UNIQUEVALUE)); // Defaulting it to 0.
		final int nullable = Integer.parseInt(PropertiesUtility.getProperty(PROP_NULLABLE)); // Assuming we can null the value
		final String indexes = PropertiesUtility.getProperty(PROP_INDEXES); //Assuming empty is fine		
		final String releaseId = PropertiesUtility.getProperty(PROP_RELEASEID); // Assuming empty is fine
		final int uniqueKey = Integer.parseInt(PropertiesUtility.getProperty(PROP_UNIQUEKEY)); // Defaulting it to 0		
		final String colType = PropertiesUtility.getProperty(PROP_COLTYPE); // Has to be counter	
		
		// Actual measurementcolumn object for db insert.
		final Measurementcolumn mColumnObject = getMeasurementColumnObject(mTableId, dataName, measColumnNumber, dataType, dataSize, dataScale, uniqueValue, nullable, indexes, 
				description, dataId, releaseId, uniqueKey, includeSQL, colType,followJohn);			
		final int mColumnObjectInsertValue = mColumnObject.insertDB();
		log.info("Insert value for mColumnObject:"+mColumnObjectInsertValue);
		log.info("Insert statement for measurementcolumn:"+mColumnObject.toSQLInsert());

		
		// Populate the specific measurementcounter fields.			
		final String typeId = mType;		
		final String timeAggregation = mCounter.getSubType(); // Assumption from Systems.
		final String groupAggregation = mCounter.getSubType();
		final String countAggregation = mCounter.getSubType();
		final String universeObject = PropertiesUtility.getProperty(PROP_UNIVERSEOBJECT); // Not dealing with universes
		final String universeClass = PropertiesUtility.getProperty(PROP_UNIVERSECLASS); // Not dealing with universes
		final String counterType = processInstruction; // Will have PEG, GUAGE or null values.
		final String counterProcess = counterType;
		
		// Actual measurementcounter object for db insert.
		final Measurementcounter mCounterObject = getMeasurementCounterObject(typeId, dataName, description, timeAggregation, groupAggregation, countAggregation, measCounterNumber, 
				dataType, dataSize, dataScale, includeSQL, universeObject, universeClass, counterType, counterProcess, dataId, followJohn);		
		final int mCounterObjectInsertValue = mCounterObject.insertDB();
		log.info("Insert value for mCounterObject:"+mCounterObjectInsertValue);
		log.info("Insert statement for measurementcounter:"+mCounterObject.toSQLInsert());
		}
		catch(SQLException se){
			throw new AFJException("SQL exception in inserting values into dwhrep:"+se.getMessage());
		}
		catch(RockException re){
			throw new AFJException("Rock exception in inserting values into dwhrep:"+re.getMessage());
		}

	}
	
	
	
	/**
	 * Method to call the VersionUpdateAction and StorageTimeAction logic of the dwhmanager.
	 * @param tpName
	 * @param measName
	 * @throws AFJException
	 */
	public void versionUpdateAction(final String tpName, final String measName, final boolean isNewMeasType) throws AFJException{
		log.info("Inside the versionUpdateAction");
		try {			
			
			// Revalidate the ActivationCache to get the uncommitted insert values of new meas types into the cache.
			if(isNewMeasType){
				log.info("Revalidate ActivationCache");			
				ActivationCacheWrapper.initialize(dwhrep, true);
				log.info("Revalidated ActivationCache");
			}
			
			log.info("Calling version update action");
			final VersionUpdateAction vua = VersionUpdateActionFactory.getInstance(dwhrep, dwh, tpName, log);
			vua.execute(measName);					
			log.info("Calling version update action finished.");
			
			// Call the PartitionAction for new counters in existing meas types.
			if(!isNewMeasType){
				log.info("Calling CreateViewsAction");
				final Dwhtype dwhType = commonUtils.getDwhType(measName, dwhrep);
				CreateViewsActionFactory.getInstance(dbadwh, dwh, dwhrep, dwhType, log);
				log.info("Calling CreateViewsAction finished.");
			}
			
			// Creates the actual raw tables and partitions.
			// Note: There is an exception User 'dwhrep' has the row in 'DWHType' locked 
			// in com.distocraft.dc5000.dwhm.SanityChecker.sanityCheck(SanityChecker.java:280)
			// The exception occurs due to a new connection created in dwhmanager and we have the table locked here. 
			// Valid scenario,doesn't affect the upgrade - can't refactor dwhmanager code.
			// The exception doesn't stop the flow of code. Also we are anyways refreshing the PhysicalTable cache at the end of upgrade.
			
			// Call the StorageTimeAction for new meas types.
			if(isNewMeasType){
				log.info("Calling storage time action");            
				StorageTimeActionFactory.getInstance(dwhrep, etlrep, dwh, dbadwh, tpName, log, measName,isCalledByNVU);
				log.info("Calling storage time action finished.");
				log.info("Calling partition action");
				PartitionActionFactory.getInstance(dwhrep, dwh, tpName, log,isCalledByNVU);
				log.info("Calling partition action finished.");
			}
			
		} catch (Exception e) {			
			e.printStackTrace();
			throw new AFJException("Error in versionupdateaction. Message:"+e.getMessage()+" Exception:"+e.toString());			
		}		
	}	
	
	/**
	 * Method to determine the process instruction based on the input xml's type field in the counter.
	 * @param type
	 * @return
	 */
	private String getProcessInstruction(final String type) throws AFJException{
		if (type.equalsIgnoreCase("CC")){
			return "PEG";
		}
		else if (type.equalsIgnoreCase("GAUGE")){
			return "GAUGE";
		}
		else{
			throw new AFJException("The counter type is invalid. Should be PEG or GUAGE.");
		}
	}
	
	/**
	 * Method to create the DataItem db object.
	 * @param dataFormatId
	 * @param dataName
	 * @param columnNumber
	 * @param dataId
	 * @param processInstruction
	 * @param dataType
	 * @param dataSize
	 * @param dataScale
	 * @return
	 */
	public Dataitem getDataItemObject(final String dataFormatId, final String dataName, final long columnNumber, final String dataId, final String processInstruction, final String dataType, final int dataSize, final int dataScale){
		final Dataitem dataItemObject = new Dataitem(dwhrep);
		dataItemObject.setDataformatid(dataFormatId);
		dataItemObject.setDataname(dataName);
		dataItemObject.setColnumber(columnNumber);
		dataItemObject.setDataid(dataId);
		dataItemObject.setProcess_instruction(processInstruction);
		dataItemObject.setDatatype(dataType);
		dataItemObject.setDatasize(dataSize);
		dataItemObject.setDatascale(dataScale);
		return dataItemObject;
	}
	
	
	/**
	 * Method to create teh MeasurementColumn db object.
	 * @param mTableId
	 * @param dataName
	 * @param columnNumber
	 * @param dataType
	 * @param dataSize
	 * @param dataScale
	 * @param uniqueValue
	 * @param nullable
	 * @param indexes
	 * @param description
	 * @param dataId
	 * @param releaseId
	 * @param uniqueKey
	 * @param includeSQL
	 * @param colType
	 * @param followJohn
	 * @return
	 */
	public Measurementcolumn getMeasurementColumnObject(final String mTableId, final String dataName, final Long columnNumber, final String dataType, final Integer dataSize, final Integer dataScale, final 
			Long uniqueValue, final Integer nullable, final String indexes, final String description, final String dataId, final String releaseId, final Integer uniqueKey, final Integer includeSQL, final String colType, final int followJohn){
		final Measurementcolumn mColumnObject = new Measurementcolumn(dwhrep);
		mColumnObject.setMtableid(mTableId);
		mColumnObject.setDataname(dataName);
		mColumnObject.setColnumber(columnNumber);
		mColumnObject.setDatatype(dataType);
		mColumnObject.setDatasize(dataSize);
		mColumnObject.setDatascale(dataScale);
		mColumnObject.setUniquevalue(uniqueValue);
		mColumnObject.setNullable(nullable);
		mColumnObject.setIndexes(indexes);
		mColumnObject.setDescription(description);
		mColumnObject.setDataid(dataId);
		mColumnObject.setReleaseid(releaseId);
		mColumnObject.setUniquekey(uniqueKey);
		mColumnObject.setIncludesql(includeSQL);
		mColumnObject.setColtype(colType);
		mColumnObject.setFollowjohn(followJohn);
		
		return mColumnObject;
	}
	
	/**
	 * Method to create the MeasurementCounter db object.
	 * @param typeId
	 * @param dataName
	 * @param description
	 * @param timeAggregation
	 * @param groupAggregation
	 * @param countAggregation
	 * @param columnNumber
	 * @param dataType
	 * @param dataSize
	 * @param dataScale
	 * @param includeSQL
	 * @param universeObject
	 * @param universeClass
	 * @param counterType
	 * @param counterProcess
	 * @param dataId
	 * @param followJohn
	 * @return
	 */
	public Measurementcounter getMeasurementCounterObject(final String typeId, final String dataName, final String description, final String timeAggregation, final String groupAggregation, 
			final String countAggregation, final Long columnNumber, final String dataType, final Integer dataSize, final Integer dataScale, final Integer includeSQL, final String universeObject, 
			final String universeClass, final String counterType, final String counterProcess, final String dataId, final int followJohn){
		final Measurementcounter mCounterObject = new Measurementcounter(dwhrep);
		mCounterObject.setTypeid(typeId);
		mCounterObject.setDataname(dataName);
		mCounterObject.setDescription(description);
		mCounterObject.setTimeaggregation(timeAggregation);
		mCounterObject.setGroupaggregation(groupAggregation);
		mCounterObject.setCountaggregation(countAggregation);
		mCounterObject.setColnumber(columnNumber);
		mCounterObject.setDatatype(dataType);
		mCounterObject.setDatasize(dataSize);
		mCounterObject.setDatascale(dataScale);
		mCounterObject.setIncludesql(includeSQL);
		mCounterObject.setUnivobject(universeObject);
		mCounterObject.setUnivclass(universeClass);
		mCounterObject.setCountertype(counterType);
		mCounterObject.setCounterprocess(counterProcess);
		mCounterObject.setDataid(dataId);
		mCounterObject.setFollowjohn(followJohn);	
		return mCounterObject;
	}
	
	/**
	 * Inserts a row in the Dataformat table.
	 * @param mType
	 * @throws AFJException
	 */
	public void populateDataFormat(final AFJMeasurementType mType) throws AFJException{
		final String versionId = commonUtils.getActiveTechPackVersion(mType.getTpName(), dwhrep);		
		final String folderName = mType.getTypeName();
		final String typeId =  versionId + DATAFORMAT_DELIMITER + folderName; 
		final String parserProperty = mType.getTpName() + PROPERTY_DELIMITER + PROP_PARSER;
		log.info("parserProperty:"+parserProperty);
		final String dataFormatType = PropertiesUtility.getProperty(parserProperty);
		final String dataFormatId = typeId + DATAFORMAT_DELIMITER + dataFormatType;		
		final String objectType = PropertiesUtility.getProperty(PROP_DEF_OBJECTTYPE);
		
		final Dataformat dataFormat = getDataFormatObject(dataFormatId,typeId,versionId,objectType,folderName,dataFormatType);
		log.info("SQL:"+dataFormat.toSQLInsert());
		try {
			final int insertValue = dataFormat.insertDB();
			log.info("Inserted record into the DataFormat table with exit value:"+insertValue);
		} catch (SQLException e) {
			throw new AFJException("SQLException in inserting dataformat object:"+e.getMessage());
		} catch (RockException e) {
			throw new AFJException("RockException in inserting dataformat object:"+e.getMessage());
		}
	}
	
	/**
	 * @param dataFormatId
	 * @param typeId
	 * @param versionId
	 * @param objectType
	 * @param folderName
	 * @param dataFormatType
	 * @return
	 */
	public Dataformat getDataFormatObject(final String dataFormatId,final String typeId,final String versionId,final String objectType,final String folderName,final String dataFormatType){
		final Dataformat dataFormatObject = new Dataformat(dwhrep);
		dataFormatObject.setDataformatid(dataFormatId);
		dataFormatObject.setTypeid(typeId);
		dataFormatObject.setVersionid(versionId);
		dataFormatObject.setObjecttype(objectType);
		dataFormatObject.setFoldername(folderName);
		dataFormatObject.setDataformattype(dataFormatType);
		return dataFormatObject;
	}
	
	/**
	 * Method to populate the defaulttags table.
	 * @param mType
	 * @throws AFJException
	 */
	public void populateDefaultTags(final AFJMeasurementType mType) throws AFJException{
		final String parserProperty = mType.getTpName() + PROPERTY_DELIMITER + PROP_PARSER;
		log.info("parserProperty:"+parserProperty);
		final String dataFormatId = mType.getTpVersion() + DATAFORMAT_DELIMITER + mType.getTypeName() 
		+ DATAFORMAT_DELIMITER + PropertiesUtility.getProperty(parserProperty);
		final String tagId = mType.getTags().get(0).getTagName(); // This may be made more generic for BSS handling as well.
		final String description = mType.getDescription();
		
		final Defaulttags defaultTags = getDefaultTagsObject(dataFormatId,tagId,description);
		log.info("SQL:"+defaultTags.toSQLInsert());
		try {
			final int insertValue = defaultTags.insertDB();
			log.info("Inserted values into defaultTags with exit value:"+insertValue);
		} catch (SQLException e) {
			throw new AFJException("SQLException in inserting defaultTags object:"+e.getMessage());
		} catch (RockException e) {
			throw new AFJException("RockException in inserting defaultTags object:"+e.getMessage());
		}
	}
	
	/**
	 * Method to return a defaulttags object.
	 * @param dataFormatId
	 * @param tagId
	 * @param description
	 * @return
	 */
	public Defaulttags getDefaultTagsObject(final String dataFormatId,final String tagId,final String description){
		final Defaulttags defaultTagsObject = new Defaulttags(dwhrep);
		defaultTagsObject.setDataformatid(dataFormatId);
		defaultTagsObject.setTagid(tagId);
		defaultTagsObject.setDescription(description);
		return defaultTagsObject;
	}
	
	/**
	 * Method to populate the MeasurementTypeClass table
	 * @param mType
	 * @throws AFJException
	 */
	public void populateMeasurementTypeClass(final AFJMeasurementType mType) throws AFJException{
		
		final String versionId = mType.getTpVersion();
		final String typeClassId = versionId + DATAFORMAT_DELIMITER + mType.getTypeName();
		final String description = mType.getTags().get(0).getTagName();		
		
		final Measurementtypeclass measurementTypeClass = getMeasurementTypeClassObject(typeClassId, versionId, description);
		log.info("SQL:"+measurementTypeClass.toSQLInsert());
		try {
			final int insertValue = measurementTypeClass.insertDB();
			log.info("Inserted values into measurementTypeClass with exit value:"+insertValue);
		} catch (SQLException e) {
			throw new AFJException("SQLException in inserting measurementTypeClass object:"+e.getMessage());
		} catch (RockException e) {
			throw new AFJException("RockException in inserting measurementTypeClass object:"+e.getMessage());
		}
	}
	
	
	
	/**
	 * @param typeClassId
	 * @param versionId
	 * @param description
	 * @return
	 */
	public Measurementtypeclass getMeasurementTypeClassObject(final String typeClassId, final String versionId, final String description){
		final Measurementtypeclass measurementTypeClass = new Measurementtypeclass(dwhrep);
		measurementTypeClass.setTypeclassid(typeClassId);
		measurementTypeClass.setVersionid(versionId);
		measurementTypeClass.setDescription(description);
		return measurementTypeClass;
	}
	
	
	/**
	 * Inserts a row into the measurementtype table.
	 * @param mType
	 * @throws AFJException
	 */
	public void populateMeasurmentType(final AFJMeasurementType mType) throws AFJException{
		final String vendorId = mType.getTpName();
		final String versionId = mType.getTpVersion();
		final String folderName = mType.getTypeName();
		final String typeId =  versionId + DATAFORMAT_DELIMITER + folderName;
		final String typeClassId = typeId; // Assumption, varies only for TIMINGFUNCTION,HDLCPWECHAN
		final String typeName = folderName;
		final String description = mType.getDescription();
		final Long status = null;
		final String objectId = versionId + DATAFORMAT_DELIMITER + folderName;
		final String objectName = folderName;
		final Integer objectVersion = null; // Default to null
		final String objectType = null; // Default to null
		final String joinable = PropertiesUtility.getProperty(PROP_DEF_JOINABLE); // Default to empty string
		final String sizing = PropertiesUtility.getProperty(PROP_DEF_SIZING); // Default value medium
		final int totalAgg = new Integer(PropertiesUtility.getProperty(PROP_DEF_TOTALAGG)); // Default value
		final int elementBHSupport = new Integer(PropertiesUtility.getProperty(PROP_DEF_ELEMENTBHSUPPORT)); // Default value - Check with Systems - ELEMENT BH is the only Busy Hour Support given.
		final int rankingTable = new Integer(PropertiesUtility.getProperty(PROP_DEF_RANKINGTABLE)); // Default value;
		final int deltaCalcSupport = new Integer(PropertiesUtility.getProperty(PROP_DEF_DELTACALCSUPPORT)); // Default value;
		final int plainTable = new Integer(PropertiesUtility.getProperty(PROP_DEF_PLAINTABLE)); // Default value;
		final String universeExtension = PropertiesUtility.getProperty(PROP_DEF_UNIVERSEEXTENSION); // Default value;
		final int vectorSupport = new Integer(PropertiesUtility.getProperty(PROP_DEF_VECTORSUPPORT)); // Default value;
		final int dataFormatSupport = new Integer(PropertiesUtility.getProperty(PROP_DEF_DATAFORMATSUPPORT)); // Default value;
		final int followJohn = new Integer(PropertiesUtility.getProperty(PROP_FOLLOWJOHN)); //Default value	
		
		
		final Measurementtype measurementType = getMeasurementTypeObject(typeId, typeClassId, typeName, vendorId, folderName, 
				description, status, versionId, objectId, objectName, objectVersion, objectType, joinable, sizing, totalAgg, 
				elementBHSupport, rankingTable, deltaCalcSupport, plainTable, universeExtension, vectorSupport, dataFormatSupport, followJohn);
		log.info("SQL:"+measurementType.toSQLInsert());
		try {
			final int insertValue = measurementType.insertDB();
			log.info("Inserted values into measurementType with exit value:"+insertValue);
		} catch (SQLException e) {
			throw new AFJException("SQLException in inserting measurementType object:"+e.getMessage());
		} catch (RockException e) {
			throw new AFJException("RockException in inserting measurementType object:"+e.getMessage());
		}
	}
	
	/**
	 * Returns a measurementtype object.
	 * @param typeId
	 * @param typeClassId
	 * @param typeName
	 * @param vendorId
	 * @param folderName
	 * @param description
	 * @param status
	 * @param versionId
	 * @param objectId
	 * @param objectName
	 * @param objectVersion
	 * @param objectType
	 * @param joinable
	 * @param sizing
	 * @param totalAgg
	 * @param elementBHSupport
	 * @param rankingTable
	 * @param deltaCalcSupport
	 * @param plainTable
	 * @param universeExtension
	 * @param vectorSupport
	 * @param dataFormatSupport
	 * @return
	 */
	public Measurementtype getMeasurementTypeObject(final String typeId, final String typeClassId, final String typeName, final String vendorId, final String folderName, 
			final String description, final Long status, final String versionId, final String objectId, final String objectName, final Integer objectVersion, 
			final String objectType, final String joinable, final String sizing, final Integer totalAgg, final Integer elementBHSupport, final Integer rankingTable, 
			final Integer deltaCalcSupport, final Integer plainTable, final String universeExtension, final Integer vectorSupport, final Integer dataFormatSupport, final Integer followJohn){
		final Measurementtype measurementType = new Measurementtype(dwhrep);
		measurementType.setTypeid(typeId);
		measurementType.setTypeclassid(typeClassId);
		measurementType.setTypename(typeName);
		measurementType.setVendorid(vendorId);
		measurementType.setFoldername(folderName);
		measurementType.setDescription(description);
		measurementType.setStatus(status);
		measurementType.setVersionid(versionId);
		measurementType.setObjectid(objectId);
		measurementType.setObjectname(objectName);
		measurementType.setObjectversion(objectVersion);
		measurementType.setObjecttype(objectType);
		measurementType.setJoinable(joinable);
		measurementType.setSizing(sizing);
		measurementType.setTotalagg(totalAgg);
		measurementType.setElementbhsupport(elementBHSupport);
		measurementType.setRankingtable(rankingTable);
		measurementType.setDeltacalcsupport(deltaCalcSupport);
		measurementType.setPlaintable(plainTable);
		measurementType.setUniverseextension(universeExtension);
		measurementType.setVectorsupport(vectorSupport);
		measurementType.setDataformatsupport(dataFormatSupport);
		measurementType.setFollowjohn(followJohn);		
		return measurementType;
		
	}	
	
	/**
	 * Inserts a row in the measurementtable table.
	 * @param mType
	 * @throws AFJException
	 */
	public void populateMeasurementTable(final AFJMeasurementType mType) throws AFJException{
		//final String vendorId = mType.getTpName();
		final String versionId = mType.getTpVersion();
		final String folderName = mType.getTypeName();
		final String typeId =  versionId + DATAFORMAT_DELIMITER + folderName;
		final String tableLevel = PropertiesUtility.getProperty(PROP_DEF_TABLELEVEL); // need to put the RAW in properties file.
		final String mTableId = typeId + DATAFORMAT_DELIMITER + tableLevel;
		final String baseTableName = folderName + PROPERTY_UNDERSCORE + tableLevel;
		final String defaultTemplate = null; // Default value
		final String partitionPlan = PropertiesUtility.getProperty(PROP_DEF_PARTITIONPLAN); // Systems assumption - put it in properties file.

		final Measurementtable measurementTable = getMeasurementTableObject(typeId, tableLevel, mTableId, baseTableName, defaultTemplate, partitionPlan);
		log.info("SQL:"+measurementTable.toSQLInsert());
		try {
			final int insertValue = measurementTable.insertDB();
			log.info("Inserted values into measurementTable with exit value:"+insertValue);
		} catch (SQLException e) {
			throw new AFJException("SQLException in inserting measurementTable object:"+e.getMessage());
		} catch (RockException e) {
			throw new AFJException("RockException in inserting measurementTable object:"+e.getMessage());
		}
	}
	
	/**
	 * Returns a measurementtable object.
	 * @param typeId
	 * @param tableLevel
	 * @param mTableId
	 * @param baseTableName
	 * @param defaultTemplate
	 * @param partitionPlan
	 * @return
	 */
	public Measurementtable getMeasurementTableObject(final String typeId, final String tableLevel, final String mTableId, final String baseTableName, final String defaultTemplate, final String partitionPlan){
		final Measurementtable measurementTable = new Measurementtable(dwhrep);
		measurementTable.setTypeid(typeId);
		measurementTable.setTablelevel(tableLevel);
		measurementTable.setMtableid(mTableId);
		measurementTable.setBasetablename(baseTableName);
		measurementTable.setDefault_template(defaultTemplate);
		measurementTable.setPartitionplan(partitionPlan);
		return measurementTable;
	}
	
	/**
	 * Inserts a row into the transformer table.
	 * @param mType
	 * @throws AFJException
	 */
	public void popluateTransformer(final AFJMeasurementType mType) throws AFJException{
		//final String vendorId = mType.getTpName();
		final String versionId = mType.getTpVersion();
		final String folderName = mType.getTypeName();
		final String parserProperty = mType.getTpName() + PROPERTY_DELIMITER + PROP_PARSER;
		log.info("parserProperty:"+parserProperty);
		final String transformerId =  versionId + DATAFORMAT_DELIMITER + folderName + DATAFORMAT_DELIMITER + PropertiesUtility.getProperty(parserProperty);
		final String description = PropertiesUtility.getProperty(PROP_DEF_DESCRIPTION); // Assumption is empty. Didn't find anything.
		final String type = PropertiesUtility.getProperty(PROP_DEF_TYPE); // Assumption. This is the only value in the database for STN. Need to put in the properties file.
		
		final Transformer transformer = getTransformerObject(transformerId, versionId, description, type);
		log.info("SQL:"+transformer.toSQLInsert());
		try {
			final int insertValue = transformer.insertDB();
			log.info("Inserted values into transformer with exit value:"+insertValue);
		} catch (SQLException e) {
			throw new AFJException("SQLException in inserting transformer object:"+e.getMessage());
		} catch (RockException e) {
			throw new AFJException("RockException in inserting transformer object:"+e.getMessage());
		}
	}
	
	/**
	 * Returns a transformer object.
	 * @param transformerId
	 * @param versionId
	 * @param description
	 * @param type
	 * @return
	 */
	public Transformer getTransformerObject(final String transformerId, final String versionId, final String description, final String type){
		final Transformer transformer = new Transformer(dwhrep);
		transformer.setTransformerid(transformerId);
		transformer.setVersionid(versionId);
		transformer.setDescription(description);
		transformer.setType(type);
		return transformer;
	}
	
	/**
	 * Method to populate the InterfaceMeasurement table.
	 * @param mType
	 * @throws AFJException
	 */
	public void populateInterfaceMeasurement(final AFJMeasurementType mType) throws AFJException{
		final String tagId = mType.getTags().get(0).getTagName(); // This may be made more generic for BSS handling as well.
		final String parserProperty = mType.getTpName() + PROPERTY_DELIMITER + PROP_PARSER;
		log.info("parserProperty:"+parserProperty);
		final String parserType = PropertiesUtility.getProperty(parserProperty);
		final String tpVersion = mType.getTpVersion();
		final String dataFormatId = tpVersion + DATAFORMAT_DELIMITER + mType.getTypeName() + DATAFORMAT_DELIMITER + parserType;
		final String propertyInterface = mType.getTpName() + PROPERTY_DELIMITER + PROP_INTERFACE;
		final String interfaceName = PropertiesUtility.getProperty(propertyInterface);
		final String transformerId = dataFormatId;
		final long status = new Long(PropertiesUtility.getProperty(PROP_DEF_STATUS));
		final Timestamp modifiedTime = new Timestamp(System.currentTimeMillis());
		final String description = "Default tags for "+mType.getTypeName()+ " in "+ tpVersion +" with format "+ parserType+".";
		final String techpackVersion = PropertiesUtility.getProperty(PROP_DEF_TECHPACKVERSION);
		final String interfaceVersion = commonUtils.getInterfaceVersion(interfaceName, dwhrep);		
		
		final Interfacemeasurement interfaceMeasurement = getInterfaceMeasurementObject(tagId, dataFormatId, interfaceName, transformerId, status, modifiedTime,
				description, techpackVersion, interfaceVersion);
		log.info("SQL:"+interfaceMeasurement.toSQLInsert());
		try {
			final int insertValue = interfaceMeasurement.insertDB();
			log.info("Inserted values into interfaceMeasurement with exit value:"+insertValue);
		} catch (SQLException e) {
			throw new AFJException("SQLException in inserting interfaceMeasurement object:"+e.getMessage());
		} catch (RockException e) {
			throw new AFJException("RockException in inserting interfaceMeasurement object:"+e.getMessage());
		}
	}
	
	/**
	 * Method to get the dwhrep object for InterfaceMeasurement.
	 * @param tagId
	 * @param dataFormatId
	 * @param interfaceName
	 * @param transformerId
	 * @param status
	 * @param modifiedTime
	 * @param description
	 * @param techpackVersion
	 * @param interfaceVersion
	 * @return
	 */
	public Interfacemeasurement getInterfaceMeasurementObject(final String tagId, final String dataFormatId, final String interfaceName, final String transformerId, final Long status, final Timestamp modifiedTime, final String description, final String techpackVersion, final String interfaceVersion){
		final Interfacemeasurement interfaceMeasurement = new Interfacemeasurement(dwhrep);
		interfaceMeasurement.setTagid(tagId);
		interfaceMeasurement.setDataformatid(dataFormatId);
		interfaceMeasurement.setInterfacename(interfaceName);
		interfaceMeasurement.setTransformerid(transformerId);
		interfaceMeasurement.setStatus(status);
		interfaceMeasurement.setModiftime(modifiedTime);
		interfaceMeasurement.setDescription(description);
		interfaceMeasurement.setTechpackversion(techpackVersion);
		interfaceMeasurement.setInterfaceversion(interfaceVersion);		
		return interfaceMeasurement;
	}
	
	/**
	 * Creates the new measurement keys. Inserts rows into the dataitem, measurementcolumn and measurementkey tables.
	 * @param mType
	 * @param measColumnNumber
	 * @throws AFJException
	 */
	public void insertKeysIntoDwhrep(final String mType, final long measColumnNumber, final String tpName, final String versionId) throws AFJException{
		
		log.info("Inserting key values into the dwhrep.");
		final String propertyKey = tpName + PROPERTY_DELIMITER + PROP_KEYS;
		final String tpKeys = PropertiesUtility.getProperty(propertyKey);
		final String[] keys = tpKeys.split(",");
		AFJKey keyObject = null;
		final List<AFJKey> keyList = new ArrayList<AFJKey>();
		for(String key: keys){
			keyObject = new AFJKey();
			final String[] values = key.split(":");
			keyObject.setName(values[0]);
			keyObject.setDataId(values[1]);
			keyObject.setUniqueKey(new Integer(values[2]));
			keyObject.setNullable(new Integer(values[3]));
			keyObject.setDataSize(new Integer(values[4]));
			keyObject.setDataType(values[5]);
			keyObject.setDataScale(new Integer(values[6]));
			keyObject.setIndexes(values[7]);
			keyObject.setUniqueValue(new Integer(values[8]));
			keyObject.setIncludeSql(new Integer(values[9]));
			keyObject.setColType(values[10]);
			keyObject.setUnivObject(values[11]);
			keyObject.setJoinable(new Integer(values[12]));
			keyObject.setIsElement(new Integer(values[13]));
			keyList.add(keyObject);		
		}
		
		if(keyList.size() == 0){
			return;
		}
		
		// Create the entries in dataitem, measurementcolumn, measurementkey tables.
		
		long columnNumber = measColumnNumber;
		for(AFJKey keyValueObject:keyList){			
			final String folderName = mType;			
			final String parserProperty = tpName + PROPERTY_DELIMITER + PROP_PARSER;
			log.info("parserProperty:"+parserProperty);
			final String dataFormatId =  folderName + DATAFORMAT_DELIMITER +
									PropertiesUtility.getProperty(parserProperty);
			final String dataName = keyValueObject.getName();
			
			final String dataId = keyValueObject.getDataId();
			final String processInstruction = PropertiesUtility.getProperty(PROP_DEF_PROCESSINSTRUCTION); // Assumption, no values found in the db.
			final String dataType = keyValueObject.getDataType();
			final int dataSize = keyValueObject.getDataSize();
			final int dataScale = keyValueObject.getDataScale();
						
			// DataItem
			final Dataitem dataItem = getDataItemObject(dataFormatId, dataName, columnNumber, dataId, 
					processInstruction, dataType, dataSize, dataScale);
			try {
				final int insertValue = dataItem.insertDB();
				log.info("Inserted values into dataItem with exit value:"+insertValue);
			} catch (SQLException e) {
				throw new AFJException("SQLException in inserting dataItem object:"+e.getMessage());
			} catch (RockException e) {
				throw new AFJException("RockException in inserting dataItem object:"+e.getMessage());
			}
			
			//MeasurementColumn
			final String mTableId = folderName + DATAFORMAT_DELIMITER + PropertiesUtility.getProperty(PROP_DEF_PARTITIONTYPE);
			//dataname already there.
			//colnumber already there
			//datatype already there
			//datasize already there
			//datascale already there
			final long uniqueValue = keyValueObject.getUniqueValue();
			final int nullable = keyValueObject.getNullable();
			final String indexes = keyValueObject.getIndexes();
			final String description = ""; // Not sure how will get this.
			//dataId already there
			final String releaseId = versionId;
			final int uniqueKey = keyValueObject.getUniqueKey();
			final int includeSQL = keyValueObject.getIncludeSql();
			final String colType = keyValueObject.getColType();
			final int followJohn = new Integer(PropertiesUtility.getProperty(PROP_FOLLOWJOHN)); //Assumption 1 defines follow john.
			
			final Measurementcolumn measurementColumn = getMeasurementColumnObject(mTableId, dataName, columnNumber, 
					dataType, dataSize, dataScale, uniqueValue, nullable, indexes, description, dataId, 
					releaseId, uniqueKey, includeSQL, colType, followJohn);
			try {
				final int insertValue = measurementColumn.insertDB();
				log.info("Inserted values into measurementColumn with exit value:"+insertValue);
			} catch (SQLException e) {
				throw new AFJException("SQLException in inserting measurementColumn object:"+e.getMessage());
			} catch (RockException e) {
				throw new AFJException("RockException in inserting measurementColumn object:"+e.getMessage());
			}
			
			// MeasurementKey
			final String typeId = folderName;
			//dataname already there
			//description already there.
			final int isElement = keyValueObject.getIsElement();//Defaulting it. Maybe required in the properties file.
			//unique key already there
			//colNumber already there.
			//datatype already there
			//datasize already there
			//datascale already there
			//uniqueValue already there
			//nullable already there
			//indexes already there
			//includeSQL already there
			final String univObject = keyValueObject.getUnivObject();
			final int joinable = keyValueObject.getJoinable();			
			//dataId already there
			
			final Measurementkey measurementKey = getMeasurementKeyObject(typeId, dataName, description, isElement, 
					uniqueKey, columnNumber, dataType, dataSize, dataScale, uniqueValue, nullable, 
					indexes, includeSQL, univObject, joinable, dataId);
			try {
				final int insertValue = measurementKey.insertDB();
				log.info("Inserted values into measurementKey with exit value:"+insertValue);
			} catch (SQLException e) {
				throw new AFJException("SQLException in inserting measurementKey object:"+e.getMessage());
			} catch (RockException e) {
				throw new AFJException("RockException in inserting measurementKey object:"+e.getMessage());
			}
			
			columnNumber++;
		}
		log.info("Inserted key values into the dwhrep.");
	}
	
	/**
	 * @param typeId
	 * @param dataName
	 * @param description
	 * @param isElement
	 * @param uniqueKey
	 * @param colNumber
	 * @param dataType
	 * @param dataSize
	 * @param dataScale
	 * @param uniqueValue
	 * @param nullable
	 * @param indexes
	 * @param includeSql
	 * @param univObject
	 * @param joinable
	 * @return
	 */
	public Measurementkey getMeasurementKeyObject(final String typeId, final String dataName, final String description, final Integer isElement, final Integer uniqueKey, final Long colNumber, final String dataType, final Integer dataSize, final Integer dataScale, final Long uniqueValue, final Integer nullable, final String indexes, final Integer includeSql, final String univObject, final Integer joinable, final String dataId){
		final Measurementkey measurementKey = new Measurementkey(dwhrep);
		measurementKey.setTypeid(typeId);
		measurementKey.setDataname(dataName);
		measurementKey.setDescription(description);
		measurementKey.setIselement(isElement);
		measurementKey.setUniquekey(uniqueKey);
		measurementKey.setColnumber(colNumber);
		measurementKey.setDatatype(dataType);
		measurementKey.setDatasize(dataSize);
		measurementKey.setDatascale(dataScale);
		measurementKey.setUniquevalue(uniqueValue);
		measurementKey.setNullable(nullable);
		measurementKey.setIndexes(indexes);
		measurementKey.setIncludesql(includeSql);
		measurementKey.setUnivobject(univObject);
		measurementKey.setJoinable(joinable);
		measurementKey.setDataid(dataId);
		return measurementKey;
	}
	
	/**
	 * Inserts values into the typeactivation table. If the entry already exists then don't insert.
	 * @param mType
	 * @throws AFJException
	 */
	public void insertValuesIntoTypeActivation(final AFJMeasurementType mType) throws AFJException{
		// Check if the value exists in the typeactivation table. If so then ignore it. Else insert it.

		try {
			final List<Typeactivation> typeActivationList = commonUtils.getTypeActivations(mType.getTypeName(),  mType.getTpName(), dwhrep);
			if(typeActivationList.isEmpty()){
				final Typeactivation targetTypeActivation = new Typeactivation(dwhrep);
				targetTypeActivation.setTypename(mType.getTypeName());
				targetTypeActivation.setTablelevel(PropertiesUtility.getProperty(PROP_DEFAULT_TABLE_LEVEL));
				targetTypeActivation.setStoragetime(Long.parseLong(PropertiesUtility.getProperty(PROP_DEFAULT_STORAGE_TIME)));
				targetTypeActivation.setType(PropertiesUtility.getProperty(PROP_DEFAULT_TYPE));
				targetTypeActivation.setTechpack_name(mType.getTpName());
				targetTypeActivation.setStatus(PropertiesUtility.getProperty(PROP_DEFAULT_STATUS));
				targetTypeActivation.setPartitionplan(PropertiesUtility.getProperty(PROP_DEFAULT_PARTITIONPLAN));
				final int insertValue = targetTypeActivation.insertDB();
				log.info("Inserted values into Typeactivation with exit value:"+insertValue);
			}
			else{
				log.info("TypeActivation entry already exists for measName;"+mType.getTypeName()+", tpName:"+mType.getTpName());
			}
		} catch (SQLException e) {
			throw new AFJException("SQLException in inserting typeactivation object:"+e.getMessage());
		} catch (RockException e) {
			throw new AFJException("RockException in inserting typeactivation object:"+e.getMessage());
		}

	}
	
	/**
	 * Populates the public keys for a tp into the respective measurement types in measurementcolumn and dataitem tables.
	 * For example the measurement type checked for STN is DC_E_STN_STN. Its defined in the AFJManager.properties file
	 * @param mType
	 * @throws AFJException
	 */
	public void populateMeasurementColumnBaseKeys(final AFJMeasurementType mType, long measColumnNumber) throws AFJException{		
		final String versionId = commonUtils.getActiveTechPackVersion(mType.getTpName(), dwhrep);			
		
		try{
			final Versioning versioning = commonUtils.getVersioning(versionId, dwhrep);
			final String tpVersionId = versioning.getVersionid();
			log.info("VersionId of tp:"+mType.getTpVersion()+" is "+tpVersionId);
			
			final String propertyMeasName = mType.getTpName() + PROPERTY_DELIMITER + PROP_MEASNAME;
			log.info("propertyMeasName:"+propertyMeasName);
			final String parserProperty = mType.getTpName() + PROPERTY_DELIMITER + PROP_PARSER;
			log.info("parserProperty:"+parserProperty);			
			final String mTableId = tpVersionId + DATAFORMAT_DELIMITER + PropertiesUtility.getProperty(propertyMeasName) 
			+ DATAFORMAT_DELIMITER + PropertiesUtility.getProperty(PROP_DEF_PARTITIONTYPE);
			log.info("mTableId for base tp definition:"+mTableId);
			
			final List<Measurementcolumn> measColumnList = commonUtils.getMeasurementColumns(mTableId, dwhrep);
			
			final int followJohn = new Integer(PropertiesUtility.getProperty(PROP_FOLLOWJOHN)); //Assumption 1 defines follow john.			
			String actualMtableid = null;
			int insertValue = 0;
			String dataFormatId = null;
			final String processInstruction = "";
			
			for(Measurementcolumn measColumn: measColumnList){
				actualMtableid = mType.getTpVersion() + DATAFORMAT_DELIMITER + mType.getTypeName() + DATAFORMAT_DELIMITER + 
				PropertiesUtility.getProperty(PROP_DEF_PARTITIONTYPE);							
				final Measurementcolumn measurementColumn = getMeasurementColumnObject(actualMtableid, measColumn.getDataname(), 
						measColumnNumber, measColumn.getDatatype(),measColumn.getDatasize(),measColumn.getDatascale(), 
						measColumn.getUniquevalue(),measColumn.getNullable(),measColumn.getIndexes(),measColumn.getDescription(),
						measColumn.getDataid(),measColumn.getReleaseid(), measColumn.getUniquekey(),measColumn.getIncludesql(),
						measColumn.getColtype(),followJohn);
				log.info("SQL:"+measurementColumn.toSQLInsert());
				insertValue = measurementColumn.insertDB();
				log.info("Inserted record into the measurementColumn table with exit value:"+insertValue);
				
				dataFormatId = mType.getTpVersion() + DATAFORMAT_DELIMITER + mType.getTypeName() + DATAFORMAT_DELIMITER 
				+ PropertiesUtility.getProperty(parserProperty);
				
				final Dataitem dataItem = getDataItemObject(dataFormatId, measColumn.getDataname(), measColumnNumber, measColumn.getDataid(), processInstruction, measColumn.getDatatype(),
						measColumn.getDatasize(), measColumn.getDatascale());
				log.info("SQL:"+dataItem.toSQLInsert());
				insertValue = dataItem.insertDB();
				log.info("Inserted record into the dataItem table with exit value:"+insertValue);
				measColumnNumber++;
			}
		} catch (SQLException e) {
			throw new AFJException("SQLException in inserting dataformat object:"+e.getMessage());
		} catch (RockException e) {
			throw new AFJException("RockException in inserting dataformat object:"+e.getMessage());
		}
	}
	
	/**
	 * Creates the loader set for a given measurement type.
	 * @param mType
	 * @throws Exception
	 */
	public void createLoaderSets(final AFJMeasurementType mType) throws Exception{
		final Meta_collection_sets mcs = commonUtils.getTechpack(mType.getTpName(),mType.getTpVersion(),etlrep);
		final String setName = mcs.getCollection_set_name();
		final String setVersion = mcs.getVersion_number();			
		final Long techPackId = mcs.getCollection_set_id();
		final String techPackName = mcs.getCollection_set_name();
		final String versionId = mType.getTpVersion();
		final String templateDir = PropertiesUtility.getProperty(PROP_IDE_TEMPLATES);
		final CreateLoaderSet cls = CreateLoaderSetFactory.getInstance(templateDir,
				setName, setVersion, versionId, dwhrep, etlrep,
				techPackId, techPackName, true);
		cls.create(mType.getTypeName());		
	}
	
	/**
	 * Creates the directory checker set for a given measurement type.
	 * @param mType
	 * @throws Exception
	 */
		public void createDirectoryCheckerSets(final AFJMeasurementType mType) throws Exception{
		  final Meta_collection_sets mcs = commonUtils.getTechpack(mType.getTpName(),mType.getTpVersion(),etlrep);
		  final String setName = mcs.getCollection_set_name();
		  final String setVersion = mcs.getVersion_number();			
		  final Long techPackId = mcs.getCollection_set_id();
		  final String versionId = mType.getTpVersion();
		  final String directoryCheckerString = PropertiesUtility.getProperty(PROP_DEFAULT_DIRECTORYCHECKER) + mType.getTpName();

		  final Vector<Meta_collections> mcList = commonUtils.getMeta_collections(techPackId, setVersion, directoryCheckerString, etlrep);

		  final long collectionId = mcList.firstElement().getCollection_id();	

		  final CreateTPDirCheckerSet cts = CreateTPDirCheckerSetFactory.getInstance(setName, setVersion, versionId, dwhrep, etlrep,
		      techPackId, null);

		  cts.createForAFJ(mType.getTypeName(), collectionId);		
		}
		
		/**
		 * @param tpName
		 * @param typeName
		 * @throws AFJException
		 * Creates the needed directory checker sets for a new measurement type.
		 */
		public void createDirectoryCheckerDirectories(final String tpName, final String typeName) throws AFJException{
			final String IQ_LOADER_DIR = PropertiesUtility.getProperty(PROP_IQLOADER_DIR) + File.separator + 
			tpName.toUpperCase() + File.separator + typeName.toUpperCase() + PROPERTY_UNDERSCORE + 
			PropertiesUtility.getProperty(PROP_DEF_PARTITIONTYPE);

			final String REJECTED_DIR = PropertiesUtility.getProperty(PROP_REJECTED_DIR) + File.separator + 
			tpName.toUpperCase() + File.separator + typeName.toUpperCase() + 
			PROPERTY_UNDERSCORE +PropertiesUtility.getProperty(PROP_DEF_PARTITIONTYPE);

			final String ETLDATA_DIR_BASE = PropertiesUtility.getProperty(PROP_ETLDATA_DIR);
			final String ETLDATA_DIR = ETLDATA_DIR_BASE + File.separator + typeName.toLowerCase();

			final String ETLDATA_DIR_RAW = ETLDATA_DIR_BASE + File.separator + typeName.toLowerCase() + File.separator + 
			PropertiesUtility.getProperty(PROP_DEF_PARTITIONTYPE).toLowerCase();

			final String ETLDATA_DIR_JOINED = ETLDATA_DIR_BASE + File.separator + typeName.toLowerCase() + File.separator + 
			PropertiesUtility.getProperty(PROP_ETLDATA_JOINED_DIR).toLowerCase();

			final List<String> directoriesList = new ArrayList<String>();
			directoriesList.add(IQ_LOADER_DIR);
			directoriesList.add(REJECTED_DIR);
			directoriesList.add(ETLDATA_DIR);
			directoriesList.add(ETLDATA_DIR_RAW);
			directoriesList.add(ETLDATA_DIR_JOINED);

			File newDirectory = null;
			boolean dirCreated = false;
			for(String directoryName:directoriesList){
				newDirectory = new File(directoryName);
				if(newDirectory.exists()){
					log.info("Directory:"+newDirectory+" already exists. Not creating it.");
				}
				else{
					dirCreated = newDirectory.mkdirs();
					if(!dirCreated){
						throw new AFJException("Unable to create the directory:"+directoryName);
					}
				}
			}
		}
		
		/**
		 * Inserts a row into the transformation table for the BSC column table.
		 * @throws AFJException
		 */
		public void popluateTransformation(final AFJMeasurementType mType) throws AFJException{				

			try{
				final String techPackVersion = mType.getTpVersion();
				final String techPack = mType.getTpName();
				log.info("VersionId of tp:"+ techPackVersion);			
				long maxOrderNo = commonUtils.getMaxOrderNumber(techPackVersion, dwhrep);
				maxOrderNo += 1;			
				log.info("Order No. available in Transformation Table for tp:"+techPackVersion+"="+maxOrderNo);
				
				final String parserProperty = mType.getTpName() + PROPERTY_DELIMITER + PROP_PARSER;
				log.info("parserProperty:"+parserProperty);
				
				final String transformerId = techPackVersion + DATAFORMAT_DELIMITER + mType.getTypeName() 
				+ DATAFORMAT_DELIMITER + PropertiesUtility.getProperty(parserProperty);
				log.info("transformerId:"+transformerId);
				final String type = PropertiesUtility.getProperty(techPack + PROPERTY_DELIMITER + PropertyConstants.PROP_TRANSFORMATIONTYPE);
				final String target = PropertiesUtility.getProperty(techPack + PROPERTY_DELIMITER + PropertyConstants.PROP_TRANSFORMATIONTARGET);
				final String source = PropertiesUtility.getProperty(techPack + PROPERTY_DELIMITER + PropertyConstants.PROP_TRANSFORMATIONSOURCE);
				final String config = PropertiesUtility.getProperty(techPack + PROPERTY_DELIMITER + PropertyConstants.PROP_TRANSFORMATIONCONFIG);

				final Transformation transformationObject = getTransformationObject(transformerId, maxOrderNo, type, source, target, config);
				log.info("SQL:"+transformationObject.toSQLInsert());
				final int insertValue = transformationObject.insertDB();

				log.info("Inserted values into Transformation table with exit value:"+insertValue);

			}
			catch (SQLException e) {
				throw new AFJException("SQLException in inserting transformation object:"+e.getMessage());
			} catch (RockException e) {
				throw new AFJException("RockException in inserting transformation object:"+e.getMessage());
			}
		}
		
		/**
		 * @param transformerId
		 * @param maxOrderNo
		 * @param type
		 * @param source
		 * @param target
		 * @param description
		 * @return
		 */
		public Transformation getTransformationObject(final String transformerId, final long maxOrderNo, final String type, final String source, final String target, final String config){
			final Transformation transformationObject = new Transformation(dwhrep);
			transformationObject.setTransformerid(transformerId);
			transformationObject.setOrderno(maxOrderNo);
			transformationObject.setType(type);
			transformationObject.setSource(source);
			transformationObject.setTarget(target);
			transformationObject.setConfig(config);
			return transformationObject;
		}
}
