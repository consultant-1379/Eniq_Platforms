 alter table Aggregation
 	drop constraint FK_AGGREGAT_REFERENCE_VERSIONI; 

 alter table AggregationRule
 	drop constraint FK_AGGREGAT_AGGREGATI_AGGREGAT; 

 alter table AggregationRule
 	drop constraint FK_AGGREGAT_TARGET_OF_MEASUREM; 

 alter table AlarmReport
 	drop constraint FK_AlarmReport_To_AlarmInterface; 

 alter table AlarmReportParameter
 	drop constraint FK_AlarmReportParameter_To_AlarmReport; 

 alter table Busyhour
 	drop constraint FK_Busyhour_TO_Versioning;  

 alter table BusyhourRankkeys
 	drop constraint FK_BusyhourRankkeys_TO_Busyhour; 

 alter table BusyhourSource
 	drop constraint FK_BusyhourSource_TO_Busyhour; 

 alter table BusyhourMapping
    drop constraint FK_BusyhourMapping_TO_MeasurementObjBHSupport;

 alter table BusyhourMapping
    drop constraint FK_BusyhourMapping_TO_Busyhour;

 alter table BusyhourPlaceholders
    drop constraint FK_BusyhourPlaceholders_TO_Versioning;
 	
 alter table DataFormat
 	drop constraint FK_DataFormat_TO_Versioning; 

 alter table DataItem
 	drop constraint FK_DataItem_TO_DataFormat; 

 alter table DefaultTags
 	drop constraint FK_DefaultTags_TO_DataFormat; 

 alter table DWHColumn
 	drop constraint FK_DWHColumn_To_DWHType; 

 alter table DWHPartition
 	drop constraint FK_DWHPartition_To_DWHType; 

 alter table DWHType
 	drop constraint FK_DWHType_To_DWHTechPacks; 

 alter table ExternalStatement
 	drop constraint FK_ExternalStatement_To_Versioning; 

 alter table ExternalStatementStatus
 	drop constraint FK_ExternalStatementStatus_To_DWHTechPacks; 

 alter table InterfaceDependency
 	drop constraint FK_InterfaceDependency_TO_DataInterface; 

 alter table InterfaceMeasurement
 	drop constraint FK_InterfaceMeasurement_TO_DataInterface; 

 alter table InterfaceMeasurement
 	drop constraint FK_InterfaceMeas_TO_DataFormat; 

 alter table InterfaceMeasurement
 	drop constraint FK_InterfaceMeas_TO_Transformer; 

 alter table InterfaceTechpacks
 	drop constraint FK_InterfaceTechpacks_TO_DataInterface; 

 alter table MeasurementColumn
 	drop constraint FK_MeasurementData_To_StorageInfo; 

 alter table MeasurementCounter
 	drop constraint FK_MEASUREM_MEASUREME_MEASUREM_1; 

 alter table MeasurementDeltaCalcSupport
 	drop constraint FK_MeasurementDeltaCalcSupport_TO_Measurementtype; 

 alter table MeasurementDeltaCalcSupport
 	drop constraint FK_MeasurementDeltaCalcSupport_TO_Vendorrelease; 

 alter table MeasurementKey
 	drop constraint FK_MEASUREM_MEASUREME_MEASUREM; 

 alter table MeasurementObjBHSupport
 	drop constraint FK_MeasurementObjBHSupport_TO_Measurementtype; 

 alter table MeasurementTable
 	drop constraint FK_MeasurementTable_To_MeasurementType; 

 alter table MeasurementType
 	drop constraint FK_MeasurementType_To_MeasurementTypeClass; 

 alter table MeasurementType
 	drop constraint FK_MEASUREM_MEASUREME_VERSIONI; 

 alter table MeasurementTypeClass
 	drop constraint FK_MeasurementTypeClass_To_Versioning; 

 alter table MeasurementVector
 	drop constraint FK_MeasurementVector_To_MeasurementCounter; 

 alter table Prompt
 	drop constraint FK_Prompt_TO_Implementor; 

 alter table PromptImplementor
 	drop constraint FK_PromptImplementor_TO_Versioning; 

 alter table PromptOption
 	drop constraint FK_Option_TO_Implementor; 

 alter table ReferenceColumn
 	drop constraint FK_REFERENC_REFERENCE_REFERENC; 

 alter table ReferenceTable
 	drop constraint FK_REFERENC_REFERENCE_VERSIONI; 

 alter table SupportedVendorRelease
 	drop constraint FK_SupportedVendorRelease_TO_Versioning; 

 alter table TechPackDependency
 	drop constraint FK_TechPackDependency_TO_Versioning; 

 alter table Transformation
 	drop constraint FK_Transformation_TO_Transformer; 

 alter table Transformer
 	drop constraint FK_Transformer_TO_Versioning; 

 alter table TypeActivation
 	drop constraint FK_TypeActivation_To_TPActivation; 

 alter table UniverseClass
 	drop constraint FK_UniverseClass_TO_Versioning; 

 alter table UniverseComputedObject
 	drop constraint FK_UniverseComputedObject_TO_UniverseClass; 

 alter table UniverseCondition
 	drop constraint FK_UniverseCondition_TO_UniverseClass; 

 alter table UniverseJoin
 	drop constraint FK_UniverseJoin_TO_Versioning; 

 alter table UniverseName
 	drop constraint FK_UniverseName_TO_Versioning; 

 alter table UniverseObject
 	drop constraint FK_UniverseObject_TO_UniverseClass; 

 alter table UniverseParameters
 	drop constraint FK_UniverseParameters_TO_UniverseComputedObject; 

 alter table UniverseTable
 	drop constraint FK_UniverseTable_TO_Versioning; 

 alter table VerificationCondition
 	drop constraint FK_VerificationCondition_TO_Versioning; 

 alter table VerificationObject
 	drop constraint FK_VerificationObject_TO_Versioning; 

drop table Aggregation;
drop table AggregationRule;
drop table AlarmInterface;
drop table AlarmReport;
drop table AlarmReportParameter;
drop table Busyhour;
drop table BusyhourRankkeys;
drop table BusyhourSource;
drop table BusyhourMapping;
drop table BusyhourPlaceholders;
drop table Configuration;
drop table DataFormat;
drop table DataInterface;
drop table DataItem;
drop table DefaultTags;
drop table DWHColumn;
drop table DWHPartition;
drop table DWHTechPacks;
drop table DWHType;
drop table ExternalStatement;
drop table ExternalStatementStatus;
drop table InfoMessages;
drop table InterfaceDependency;
drop table InterfaceMeasurement;
drop table InterfaceTechpacks;
drop table MeasurementColumn;
drop table MeasurementCounter;
drop table MeasurementDeltaCalcSupport;
drop table MeasurementKey;
drop table MeasurementObjBHSupport;
drop table MeasurementTable;
drop table MeasurementType;
drop table MeasurementTypeClass;
drop table MeasurementVector;
drop table PartitionPlan;
drop table Prompt;
drop table PromptImplementor;
drop table PromptOption;
drop table ReferenceColumn;
drop table ReferenceTable;
drop table SupportedVendorRelease;
drop table TechPackDependency;
drop table TPActivation;
drop table Transformation;
drop table Transformer;
drop table TypeActivation;
drop table UniverseClass;
drop table UniverseComputedObject;
drop table UniverseCondition;
drop table UniverseFormulas;
drop table UniverseJoin;
drop table UniverseName;
drop table UniverseObject;
drop table UniverseParameters;
drop table UniverseTable;
drop table UserAccount;
drop table VerificationCondition;
drop table VerificationObject;
drop table Versioning;
drop table Grouptypes;
