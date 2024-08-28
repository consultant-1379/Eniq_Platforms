/**
 * 
 */
package com.ericsson.eniq.afj.xml;

import static com.ericsson.eniq.afj.common.PropertyConstants.DATAFORMAT_DELIMITER;
import static com.ericsson.eniq.afj.common.PropertyConstants.MEASTYPE_TOKEN;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROPERTY_DELIMITER;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_BSS_NAME;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_IDENTITY_COUNTERTYPE;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_INTERFACE;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_MOCLEVEL_BSS;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_MOCLEVEL_STS;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_PARSER;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_SPECIALMULTIPLEMOCS;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_STS_NAME;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import ssc.rockfactory.RockFactory;

import com.ericsson.eniq.afj.common.AFJMeasurementCounter;
import com.ericsson.eniq.afj.common.AFJMeasurementTag;
import com.ericsson.eniq.afj.common.AFJMeasurementType;
import com.ericsson.eniq.afj.common.PropertiesUtility;
import com.ericsson.eniq.afj.database.AFJDatabaseCommonUtils;
import com.ericsson.eniq.afj.database.AFJDatabaseHandler;
import com.ericsson.eniq.afj.schema.CounterType;
import com.ericsson.eniq.afj.schema.DataType;
import com.ericsson.eniq.afj.schema.GroupType;
import com.ericsson.eniq.afj.schema.MocType;
import com.ericsson.eniq.afj.schema.PM;
import com.ericsson.eniq.exception.AFJException;

/**
 * @author esunbal
 *
 */
public class BSSComparator implements CompareInterface{

  private final Logger log = Logger.getLogger(this.getClass().getName());

	private String bssInterfaceName;	

	private String bssTechPackName;

	private String commonTechPackName;

	private String bssTechPackVersion;

	private String commonTechPackVersion;

	private AFJDatabaseHandler connection;

	private RockFactory dwhrep;

	private RockFactory etlrep;	
	
	private AFJDatabaseCommonUtils commonUtils;

	private List<AFJMeasurementType> deltaList = new ArrayList<AFJMeasurementType>();

	/* List to store the meas tags which are associated to a single meas type */
	private List<String> addVendorIdList = new ArrayList<String>();

	/* List to store the exceptional moc handling. Multiple mocs like SCABISDEL which doesn't find mention in addVendorIdList*/
	private List<String> specialMultipleMocsList = new ArrayList<String>();

	/*Map to store the meas type tag to the list of counters associated with it in the ENIQ db.*/
	private Map<String, List<String>> tagToCountersMap = new HashMap<String, List<String>>();

	/*Map to store the meas type tag to exact meas type name defined in the ENIQ db.*/	
	private Map<String, String> tagToMeasTypeMap = new HashMap<String, String>();	

	/* (non-Javadoc)
	 * @see com.ericsson.eniq.afj.xml.CompareInterface#getMeasTypeDelta(java.lang.Object)
	 * Takes in an object - generally of type PM. Generates the delta list for the xml against the ENIQ db.
	 */
	@Override
	public List<AFJMeasurementType> getMeasTypeDelta(final Object object) throws AFJException{
		final PM pmObject = (PM) object;
		
		bssTechPackName = PropertiesUtility.getProperty(PROP_BSS_NAME);
		bssInterfaceName = PropertiesUtility.getProperty(bssTechPackName + PROPERTY_DELIMITER + PROP_INTERFACE);
		commonTechPackName = PropertiesUtility.getProperty(PROP_STS_NAME);

		connection = AFJDatabaseHandler.getInstance();		
		dwhrep = connection.getDwhrep();
		etlrep = connection.getEtlrep();
		commonUtils = new AFJDatabaseCommonUtils();
		bssTechPackVersion = commonUtils.getActiveTechPackVersion(bssTechPackName,dwhrep);
		commonTechPackVersion = commonUtils.getActiveTechPackVersion(commonTechPackName,dwhrep);
		log.info("BSS Versionid="+bssTechPackVersion);
		log.info("Common Versionid="+commonTechPackVersion);

		// Populate the hashmap tagToMeasTypeMap
		this.tagToMeasTypeMap = commonUtils.populateDefaultTagsCache(bssTechPackVersion,commonTechPackVersion,dwhrep);
		
		// Populate the hashmap tagToCountersMap 
		this.tagToCountersMap = commonUtils.populateMeasurementCountersMap(this.tagToMeasTypeMap,dwhrep);	
		
		// Populate the vendorIdList - which gives the mapping for which all measurement types handle multiple meas type tags.
		this.addVendorIdList = commonUtils.populateAddVendorIdList(bssTechPackVersion,bssInterfaceName,etlrep);

		this.specialMultipleMocsList = getSpecialMultipleMocs();
		
		// Preprocess the xml file to see if it has the necessary tags. If there are any error messages throw an exception.
		final String preProcessErrors = preProcessInput(pmObject);
		if(preProcessErrors.length() != 0){
			log.severe(preProcessErrors);
			throw new AFJException(preProcessErrors);
		}

		// Get the delta list of measurement types
		deltaList = generateDelta(pmObject);
		log.info("Size of the deltaList="+deltaList.size()); 

		List<AFJMeasurementCounter> mcList = null;
		List<AFJMeasurementTag> tagList = null;

		for(AFJMeasurementType type: deltaList){
			log.info("Measurement Type Name:"+type.getTypeName());
			tagList = type.getTags();
			for(AFJMeasurementTag tag:tagList){
				log.info("	-Measurement tag:"+tag.getTagName());
				mcList = tag.getNewCounters();
				for(AFJMeasurementCounter mc: mcList){
					log.info("        	--Counter:"+mc.getCounterName());
				}
			}
			log.info("<-------------------------------->");
		}
		return this.deltaList;
	}

	/**
	 * Method to check whether the xml is having the required information for generating delta.
	 * 
	 * 1.) No duplicate moc tags inside the xml
	 * 2.) moc_level -> determines whether its bss or common_sts
	 * 3.) No duplicate counter definition inside the moc
	 * 
	 * @param object
	 * @throws AFJException
	 */
	private String preProcessInput(final PM object) throws AFJException{
		final PM pmObject = object;
		final List<String> mocNames = new ArrayList<String>();
		List<String> counterNames = null;
		final List<MocType> mocTypeList = pmObject.getData().getMoc();
		final StringBuffer errorMessage = new StringBuffer();
		BigInteger mocLevel = null;

		for(MocType mt: mocTypeList){
			/* Systems Call: Ignore TRAPEVENT as some time in the past, when a designer decided which Tech Pack 
			 * should support TRAPEVENT, they chose the wrong one. Ideally TRAPEVENT should come as moc_level 0 but
			 * BSS won't change it as they are correct in their in interpretation. We set the moclevel as 0 for TRAPEVENT  */
			
			if(mt.getName().equalsIgnoreCase("TRAPEVENT")){				
				mt.setMocLevel(new BigInteger(PropertiesUtility.getProperty(PROP_MOCLEVEL_STS)));
				continue;
			}
			
			mocLevel = mt.getMocLevel();			
			if(mocLevel == null){
				errorMessage.append("\nNo moc_level tag defined for moc:"+mt.getName());
			}
			if(mocLevel.intValue() == Integer.parseInt(PropertiesUtility.getProperty(PROP_MOCLEVEL_BSS))){
				final String checkString = PropertiesUtility.getProperty(PROP_STS_NAME) + DATAFORMAT_DELIMITER;
				if(tagToMeasTypeMap.containsKey(checkString+mt.getName().toUpperCase())){
					errorMessage.append("\nMoc name:"+mt.getName()+" defined with wrong moclevel:"+mocLevel);
				}
			}
			else if(mocLevel.intValue() == Integer.parseInt(PropertiesUtility.getProperty(PROP_MOCLEVEL_STS))){
				final String checkString = PropertiesUtility.getProperty(PROP_BSS_NAME) + DATAFORMAT_DELIMITER;
				if(tagToMeasTypeMap.containsKey(checkString+mt.getName().toUpperCase())){
					errorMessage.append("\nMoc name:"+mt.getName()+" defined with wrong moclevel:"+mocLevel);
				}
			}
			else{
				errorMessage.append("\nMoc name:"+mt.getName()+" defined with wrong moclevel:"+mocLevel);
			}
			
			if(mocNames.contains(mt.getName().toUpperCase()+mocLevel)){
				errorMessage.append("\nDuplicate moc name specification:"+mt.getName());
			}
			
			if(mt.getName().length() > 128){
				errorMessage.append("\nThe character length exceeds Sybase IQ limitation of 128:"+mt.getName());
			}
			
			if(!sybaseNameCheck(mt.getName())){
				errorMessage.append("\nInvalid moc name:"+mt.getName()+
						". Name must begin with a letter (A through Z), underscore (_), at sign (@), dollar sign ($), " +
						"or pound sign (#). Only special chars allowed are _@$#");
				
			}
			
			mocNames.add(mt.getName().toUpperCase()+mocLevel);		
			counterNames = new ArrayList<String>();
			for(GroupType gt: mt.getGroup()){
				for(CounterType ct: gt.getCounter()){
					if(counterNames.contains(ct.getMeasurementName().toUpperCase())){
						errorMessage.append("\nDuplicate counter name specification:"+ct.getMeasurementName()+" in the moc:"+mt.getName());

					}
					
					if(ct.getMeasurementName().length() > 128){
						errorMessage.append("\nThe character length exceeds Sybase IQ limitation of 128:"+ct.getMeasurementName());
					}
					
					if(!sybaseNameCheck(ct.getMeasurementName())){
						errorMessage.append("\nInvalid counter name:"+ct.getMeasurementName()+
								". Name must begin with a letter (A through Z), underscore (_), at sign (@), dollar sign ($), " +
								"or pound sign (#)");
						
					}
					
					counterNames.add(ct.getMeasurementName().toUpperCase());
				}
			}
		}		
		return errorMessage.toString();
	}

	/**
	 * Generates the delta between the PM MIM input xml and the values stored in the ENIQ database.
	 * Logic:
	 * 1.) Iterate through the xml tag moc.
	 * 2.) Check if the moc name exists in the defaultTagsToMeasTypeNameMap - implies whether the meas type already exists in the db.
	 * 3.) If no match found in defaultTagsToMeasTypeNameMap, it means its a new meas type. 
	 * 			a.) Add in all the counters into the meastype and add the meastype into delta list.
	 * 4.) If a match is found in defaultTagsToMeasTypeNameMap, it means an existing meas type. May have new counters defined.
	 * 			a.) Check for new counters. Match the counter tag in the xml to the list of counters associated with a meas type in defaultTagsToMeasTypeNameMap.
	 * 			b.) If atleast 1 new counter is found then add the counter to the meas type and add the meas type to the delta list.
	 *  
	 * @param pmObject
	 * @return
	 */
	private List<AFJMeasurementType> generateDelta(final PM pmObject) throws AFJException{
		final List<AFJMeasurementType> measTypeList = new ArrayList<AFJMeasurementType>();
		AFJMeasurementType measTypeObject = null;
		AFJMeasurementType tempMeasTypeObject = null;		
		AFJMeasurementTag measTagObject = null;
		AFJMeasurementTag tempMeasTagObject = null;
		AFJMeasurementCounter measCounterObject = null;
		List<AFJMeasurementCounter> measCounterList = null;
		List<AFJMeasurementTag> tagList = null;
		List<AFJMeasurementTag> tempTagList = null;
		final DataType dataType = pmObject.getData();
		final List<MocType> mocTypes = dataType.getMoc();

		BigInteger mocLevel = null;		
		String techpackName = null;
		String key = null;
		String techpackVersion = null;
		String[] measTypeNameTokens = null;
		final String parserType = PropertiesUtility.getProperty(bssTechPackName + PROPERTY_DELIMITER + PROP_PARSER);
		log.info("Parser Type:"+parserType);

		final Map<String, AFJMeasurementType> finalObjectMap = new HashMap<String, AFJMeasurementType>();

		for(MocType mocType: mocTypes){
			measCounterList = new ArrayList<AFJMeasurementCounter>();
			measTypeObject = new AFJMeasurementType();			
			mocLevel = mocType.getMocLevel();
			if(mocLevel.intValue() == Integer.parseInt(PropertiesUtility.getProperty(PROP_MOCLEVEL_BSS))){
				techpackName = this.bssTechPackName;				
				techpackVersion = this.bssTechPackVersion;
			}
			else {
				continue; // We are only interested in moc's with moclevel 1. We ignore all other mocs.
			}

			measTypeObject.setTpName(techpackName);
			measTypeObject.setTpVersion(techpackVersion);
			
			key = techpackName + DATAFORMAT_DELIMITER;
			log.info("Key:"+key);

			if(!tagToMeasTypeMap.containsKey(key+mocType.getName().toUpperCase())){
				// New MeasurementType. Add it straight into delta object.				
				measTypeObject.setTypeName(techpackName+"_"+mocType.getName().toUpperCase());
				measTypeObject.setTypeNew(true);	
				measTagObject = new AFJMeasurementTag();
				measTagObject.setTagName(mocType.getName().toUpperCase());
				measTagObject.setDataformattype(parserType);
				
				// Loop through to get the counters
				for(GroupType gt: mocType.getGroup()){
					for(CounterType ct: gt.getCounter()){
						// Ignore the IDENTITY counters. These are IDs not counters.
						if(ct.getCollectionMethod().getType().equalsIgnoreCase(PropertiesUtility.getProperty(PROP_IDENTITY_COUNTERTYPE))){
							continue;
						}
						measCounterObject = createCounterObject(ct);						
						measCounterList.add(measCounterObject);
					}
				}
				
				// Populate and add teh measTypeObject only if it has 1 counter
				if(measCounterList.size() > 0){
					// Set new counters to tag object. Add it to tag list. Add the tag list to the measType object. Put the measTypeObject into the return list.
					measTagObject.setNewCounters(measCounterList);
					tagList = new ArrayList<AFJMeasurementTag>();
					tagList.add(measTagObject);
					measTypeObject.setTags(tagList);
					measTypeList.add(measTypeObject);
				}
			}
			else{
				// Existing measurement type. Check whether there are new counters.		
				measTypeNameTokens = tagToMeasTypeMap.get(key+mocType.getName().toUpperCase()).split(DATAFORMAT_DELIMITER);
				measTypeObject.setTypeName(measTypeNameTokens[MEASTYPE_TOKEN]);
				measTypeObject.setTypeNew(false);				
				measTagObject = new AFJMeasurementTag();
				measTagObject.setTagName(mocType.getName());
				measTagObject.setDataformattype(parserType);
				for(GroupType gt: mocType.getGroup()){
					for(CounterType ct: gt.getCounter()){
						// Ignore the IDENTITY counters. These are IDs not counters.
						if(ct.getCollectionMethod().getType().equalsIgnoreCase(PropertiesUtility.getProperty(PROP_IDENTITY_COUNTERTYPE))){
							continue;
						}						
						if(!isCounterPresent(key,mocType.getName().toUpperCase(),ct.getMeasurementName().toUpperCase())){
							// New counter in existing meas type found. Add it to counter list.
							measCounterObject = createCounterObject(ct,mocType.getName());
							measCounterList.add(measCounterObject);
						}
					}
				}
				
				// Implies we have atleast 1 new counter in an existing measurement type.
				if(!measCounterList.isEmpty()){
					// Check if the measType already exists in final hashmap. Get the tag list and update it.
					if(finalObjectMap.containsKey(measTypeObject.getTypeName())){
						tempMeasTypeObject = finalObjectMap.get(measTypeObject.getTypeName());
						tempTagList = tempMeasTypeObject.getTags();
						tempMeasTagObject = new AFJMeasurementTag();
						tempMeasTagObject.setTagName(mocType.getName().toUpperCase());
						tempMeasTagObject.setDataformattype(parserType);						
						tempMeasTagObject.setNewCounters(measCounterList);
						tempTagList.add(tempMeasTagObject);
						measTypeObject.setTags(tempTagList); // Check this statement
						finalObjectMap.remove(measTypeObject.getTypeName());
						finalObjectMap.put(measTypeObject.getTypeName(),measTypeObject);
					}
					else{
						// First time putting the object into the final hashmap.
						measTagObject.setTagName(mocType.getName().toUpperCase());
						measTagObject.setDataformattype(parserType);						
						measTagObject.setNewCounters(measCounterList);
						tagList = new ArrayList<AFJMeasurementTag>();
						tagList.add(measTagObject);
						measTypeObject.setTags(tagList);
						finalObjectMap.put(measTypeObject.getTypeName(), measTypeObject);
					}					
				}				
			}
		}
		
		// Iterate through the finalObjectMap and add the meastype objects into the list.
		Iterator<String> iter = null;		
		for (iter = finalObjectMap.keySet().iterator(); iter.hasNext();) { 
			final String measName = iter.next(); 
			log.info("->Measurement Type Name:"+measName);
			measTypeObject = (AFJMeasurementType) finalObjectMap.get(measName);
			final List<AFJMeasurementTag> measTagList = measTypeObject.getTags();
			int counters = 0;
			for(AFJMeasurementTag amt:measTagList){
				counters += amt.getNewCounters().size();
			}
			final String ratio = getRatio(measTypeObject.getTpVersion()+DATAFORMAT_DELIMITER+measTypeObject.getTypeName(),counters);
			log.info("Ratio:"+ratio);
			measTypeObject.setRatio(ratio);
			measTypeList.add(measTypeObject);
		}

		return measTypeList;
	}


	
	/**
	 * Method to calculate the ratio of no. of new counters to no. of counters in the db for an existing meas type.
	 * @param tagName
	 * @param counterList
	 * @return
	 * @throws AFJException
	 */
	private String getRatio(final String typeId, final int numerator)throws AFJException{		
		final int denominator = commonUtils.getNumberOfCounters(typeId, dwhrep);		
		final String ratio = numerator + "/" + denominator;
		return ratio;
	}
	

	/**
	 * Creates the counter object by getting the necessary details from the xml file.
	 * @param ct
	 * @return
	 */
	public AFJMeasurementCounter createCounterObject(final CounterType ct){
		// Need to add more things useful during the upgrade.
		final AFJMeasurementCounter measCounterObject = new AFJMeasurementCounter();
		measCounterObject.setCounterName(ct.getMeasurementName());		
		measCounterObject.setCounterNew(true);
		measCounterObject.setResultType(ct.getMeasurementResult().getResultType());
		measCounterObject.setSubType(ct.getCollectionMethod().getSubtype());
		measCounterObject.setType(ct.getCollectionMethod().getType());	
		measCounterObject.setDescription(ct.getDescription());
		return measCounterObject;
	}
	
	/**
	 * Creates a counter object.
	 * @param ct
	 * @param mocName
	 * @return
	 */
	public AFJMeasurementCounter createCounterObject(final CounterType ct, final String mocName){
		// Need to add more things useful during the upgrade.
		final AFJMeasurementCounter measCounterObject = new AFJMeasurementCounter();
		String counterName = ct.getMeasurementName();
		if(!isMeasTypeSingle(mocName)){
			counterName = mocName + "_" + counterName;				
		}
		measCounterObject.setCounterName(counterName);
		measCounterObject.setCounterNew(true);
		measCounterObject.setResultType(ct.getMeasurementResult().getResultType());
		measCounterObject.setSubType(ct.getCollectionMethod().getSubtype());
		measCounterObject.setType(ct.getCollectionMethod().getType());	
		measCounterObject.setDescription(ct.getDescription());
		return measCounterObject;
	}
	
	/**
	 * Method to determine if a measurement type maps more than one moc tag.
	 * @param measName
	 * @return
	 */
	public boolean isMeasTypeSingle(final String measTag){
		boolean returnValue = true;
		if(addVendorIdList.contains(measTag)){
			returnValue = false;
		}
		
		/* More check to handle exceptional mocs like SCABISDEL which should have been single mocs
		 * as its not mentioned in etlrep but we need to handle it as multiple moc */
		if(specialMultipleMocsList.contains(measTag)){
			returnValue = false;
		}
		return returnValue;
	}
	
	/**
	 * 
	 * Method to check if a counter name is associated with a measurement tag.
	 * If the measurement type maps to more than  1 moc tag then need to append the mocName before the counter
	 * @param key
	 * @param tagName
	 * @param measName
	 * @param counterName
	 * @return
	 */
	public boolean isCounterPresent(final String key, final String tagName, String counterName){
		boolean returnValue = false;
		if(!isMeasTypeSingle(tagName)){
			counterName = tagName + "_" + counterName;
		}

		final List<String> counters = tagToCountersMap.get(key+tagName);
		if(counters == null){
			returnValue = false;
		}
		else{
			returnValue = counters.contains(counterName);	
		}	

		return returnValue;
	}
	

	/**
	 * Method to check the sybase validity of the counter name or the meas name.
	 * A name must begin with a letter (A through Z), underscore (_), at sign (@), dollar sign ($), or pound sign (#).
	 * @param name
	 * @return
	 */
	private boolean sybaseNameCheck(final String name){
		boolean validity = false;		
		// A name must begin with a letter (A through Z), underscore (_), at sign (@), dollar sign ($), or pound sign (#). Only special chars allowed are _@$# 
		final String sybaseRegExp = "(^[A-Za-z_@$#]+[A-Za-z0-9_@$#]*$)";
		validity = Pattern.compile(sybaseRegExp).matcher(name).find();
		log.info("Check for name:"+name+" in regex returned value:"+validity);		
		return validity;		
	}
	


	/**
	 * Populates the special multiple mocs 
	 * @return
	 * @throws AFJException
	 */
	private List<String> getSpecialMultipleMocs() throws AFJException{
		final List<String> returnList = new ArrayList<String>();
		final String specialMocs = PropertiesUtility.getProperty(PROP_SPECIALMULTIPLEMOCS);
		final String[] mocsArray = specialMocs.split(",");
		for(String moc: mocsArray){
			returnList.add(moc);
		}
		return returnList;
	}
}
