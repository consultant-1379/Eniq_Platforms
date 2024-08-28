package com.ericsson.eniq.afj.xml;

import static com.ericsson.eniq.afj.common.PropertyConstants.DATAFORMAT_DELIMITER;
import static com.ericsson.eniq.afj.common.PropertyConstants.MEASTYPE_TOKEN;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROPERTY_DELIMITER;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_IDENTITY_COUNTERTYPE;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_MTAS_NAME;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_PARSER;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
import com.ericsson.eniq.exception.AFJException;
import com.ericsson.ims.mim.CounterType;
import com.ericsson.ims.mim.CounterType.CollectionMethod;
import com.ericsson.ims.mim.CounterType.MeasurementResult;
import com.ericsson.ims.mim.DataType;
import com.ericsson.ims.mim.GroupType;
import com.ericsson.ims.mim.MocType;
import com.ericsson.ims.mim.PM;

/**
 * This class is used to compare the counters from the input MOM file with the database entries in the DC_E_MTAS techpack 
 * and give a delta.
 * For MTAS, we are only interested in adding new counters to existing meas types.
 * @author esunbal
 *
 */
public class MTASComparator implements CompareInterface {

	  private final Logger log = Logger.getLogger(this.getClass().getName());
		
		private String techPackName;
		
		private String techPackVersion;
		
		private AFJDatabaseHandler connection;	
		
		private RockFactory dwhrep;			
		
		private AFJDatabaseCommonUtils commonUtils;
		
		private List<AFJMeasurementType> deltaList = new ArrayList<AFJMeasurementType>();
		
		/*Map to store the meas type tag to the list of counters associated with it in the ENIQ db.*/
		private Map<String, List<String>> measTagsToCountersMap = new HashMap<String, List<String>>();
		
		/*Map to store the meas type tag to exact meas type name defined in the ENIQ db.*/
		private Map<String, String> tagsToMeasNameMap = new HashMap<String, String>();
		
		private final String[] validType = {"CC","SI", "GAUGE", "DER", "IDENTITY"};
		
		private final String[] validSubType = {"MIN", "MAX", "LAST_UPDATE", "RAW", "MEAN", "SUM", ""};
		
		private final String[] validResultType = {"decimal", "integer", "float", "byte", "int", "smallInt", "unsignedInt", "long", "double", "dateTime","date", "time", "boolean"};
		
		/* (non-Javadoc)
		 * @see com.ericsson.eniq.afj.xml.CompareInterface#getMeasTypeDelta(java.lang.Object)
		 * Takes in an object - generally of type PM. Generates the delta list for the xml against the ENIQ db.
		 */
		@Override
		public List<AFJMeasurementType> getMeasTypeDelta(final Object object) throws AFJException{
			final PM pmObject = (PM) object;

			// Preprocess the xml file to see if it has the necessary tags. If there are any error messages throw an exception.
			final String preProcessErrors = preProcessInput(pmObject);
			if(preProcessErrors.length() != 0){
				log.severe(preProcessErrors);
				throw new AFJException(preProcessErrors);
			}
			
			connection = AFJDatabaseHandler.getInstance();		
			dwhrep = connection.getDwhrep();		
			commonUtils = new AFJDatabaseCommonUtils();
			techPackName = PropertiesUtility.getProperty(PROP_MTAS_NAME);
			techPackVersion = commonUtils.getActiveTechPackVersion(this.techPackName,dwhrep);		
			
			// Populate the hashmap tagToMeasTypeMap
			this.tagsToMeasNameMap = commonUtils.populateDefaultTagsCache(techPackVersion,dwhrep);
			
			// Populate the hashmap tagToCountersMap 
			this.measTagsToCountersMap = commonUtils.populateMeasurementCountersMap(this.tagsToMeasNameMap,dwhrep);

			// Get the delta list of measurement types
			deltaList = generateDelta(pmObject);

			log.info("Size of the deltaList="+deltaList.size());		

			return this.deltaList;
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
		 * @throws AFJException 
		 */
		private List<AFJMeasurementType> generateDelta(final PM pmObject) throws AFJException{
			final List<AFJMeasurementType> measTypeList = new ArrayList<AFJMeasurementType>();
			List<String> counters = null;
			AFJMeasurementType measTypeObject = null;
			AFJMeasurementCounter measCounterObject = null;
			List<AFJMeasurementCounter> measCounterList = null;
			String[] measTypeNameTokens = null;
			
			final String parserType = PropertiesUtility.getProperty(techPackName + PROPERTY_DELIMITER + PROP_PARSER);
			log.info("Parser Type:"+parserType);
			
			//Map<String, List<AFJMeasurementCounter>> tagToCountersMap = null;
			List<AFJMeasurementTag> measTypeTag = null;		
			
			final DataType dataType = pmObject.getData();
			final List<MocType> mocTypes = dataType.getMoc();
			
			for(MocType mocType: mocTypes){
				measCounterList = new ArrayList<AFJMeasurementCounter>();
				measTypeTag = new ArrayList<AFJMeasurementTag>();
				final AFJMeasurementTag tag = new AFJMeasurementTag();
				measTypeObject = new AFJMeasurementType();
				measTypeObject.setTpName(techPackName);
				measTypeObject.setTpVersion(techPackVersion);

				if(measTagsToCountersMap.containsKey(mocType.getName().toUpperCase())){
					// Existing measurement type. Check whether there are new counters.	
					measTypeNameTokens = tagsToMeasNameMap.get(mocType.getName().toUpperCase()).split(DATAFORMAT_DELIMITER);
					measTypeObject.setTypeName(measTypeNameTokens[MEASTYPE_TOKEN]);
//					measTypeObject.setTypeName(tagsToMeasNameMap.get(mocType.getName().toUpperCase()));
					measTypeObject.setTypeNew(false);				
					counters = measTagsToCountersMap.get(mocType.getName().toUpperCase());				
					for(GroupType gt: mocType.getGroup()){
						for(CounterType ct: gt.getCounter()){
							// Ignore the IDENTITY counters. These are IDs not counters.
							if(ct.getCollectionMethod().getType().equalsIgnoreCase(PropertiesUtility.getProperty(PROP_IDENTITY_COUNTERTYPE))){
								continue;
							}
							if(!counters.contains(ct.getMeasurementName().toUpperCase())){
								// New counter in existing meas type found. Add it to meas type object.
								measCounterObject = createCounterObject(ct);
								measCounterList.add(measCounterObject);
							}
						}
					}
					
					final String ratio = measCounterList.size() + "/" + counters.size();				
					measTypeObject.setRatio(ratio);
					
					// Implies we have an existing measurement type with atleast 1 new counter.
					if(!measCounterList.isEmpty()){
						tag.setTagName(mocType.getName());
						tag.setDataformattype(parserType);
						tag.setNewCounters(measCounterList);
						measTypeTag.add(tag);
						measTypeObject.setTags(measTypeTag);
						measTypeList.add(measTypeObject);
					}				
				}
			}		
			
			return measTypeList;
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
			String type = null;
			String subType = null;
			String resultType = null;
			CollectionMethod cm = null;
			MeasurementResult mr = null;
			
			final List<String> validTypeList = Arrays.asList(validType);
			final List<String> validSubTypeList = Arrays.asList(validSubType);
			final List<String> validResultTypeList = Arrays.asList(validResultType);
			
			log.info("moctypelist size:"+mocTypeList.size());
			for(MocType mt: mocTypeList){			
				if(mocNames.contains(mt.getName().toUpperCase())){
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
				
				mocNames.add(mt.getName().toUpperCase());		
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
						
						cm = ct.getCollectionMethod();
						type = cm.getType();
						if(type == null || !validTypeList.contains(type)){
							errorMessage.append("\nInvalid type:"+type+" in counter:"+ct.getMeasurementName()+" of moc:"+mt.getName());
						}
						subType = cm.getSubtype();
						if(subType == null || !validSubTypeList.contains(subType)){
							errorMessage.append("\nInvalid subType:"+subType+" in counter:"+ct.getMeasurementName()+" of moc:"+mt.getName());
						}
						
						mr = ct.getMeasurementResult();
						resultType = mr.getResultType();
						if(resultType == null || !validResultTypeList.contains(resultType)){
							errorMessage.append("\nInvalid resultType:"+resultType+" in counter:"+ct.getMeasurementName()+" of moc:"+mt.getName());
						}
						counterNames.add(ct.getMeasurementName().toUpperCase());
					}
				}
			}		
			return errorMessage.toString();
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
}
