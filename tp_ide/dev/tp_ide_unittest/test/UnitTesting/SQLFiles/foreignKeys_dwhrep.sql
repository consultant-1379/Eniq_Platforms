
 
alter table Aggregation
       add constraint FK_AGGREGAT_REFERENCE_VERSIONI foreign key (VERSIONID)
       references Versioning (VERSIONID)
       on delete restrict on update restrict;

 
alter table AggregationRule
       add constraint FK_AGGREGAT_AGGREGATI_AGGREGAT foreign key (AGGREGATION, VERSIONID)
       references Aggregation (AGGREGATION, VERSIONID)
       on delete restrict on update restrict;

 
alter table AggregationRule
       add constraint FK_AGGREGAT_TARGET_OF_MEASUREM foreign key (TARGET_MTABLEID)
       references MeasurementTable (MTABLEID)
       on delete restrict on update restrict;

 
alter table AlarmReport
       add constraint FK_AlarmReport_To_AlarmInterface foreign key (INTERFACEID)
       references AlarmInterface (INTERFACEID)
       on delete restrict on update restrict;

 
alter table AlarmReportParameter
       add constraint FK_AlarmReportParameter_To_AlarmReport foreign key (REPORTID)
       references AlarmReport (REPORTID)
       on delete restrict on update restrict;

 
alter table Busyhour
       add constraint FK_Busyhour_TO_Versioning foreign key (VERSIONID)
       references Versioning (VERSIONID)
       on delete restrict on update restrict;


alter table BusyhourMapping
       add constraint FK_BusyhourMapping_TO_MeasurementObjBHSupport foreign key (BHOBJECT, TYPEID)
       references MeasurementObjBHSupport(OBJBHSUPPORT, TYPEID)
       on delete restrict on update restrict;


alter table BusyhourMapping
       add constraint FK_BusyhourMapping_TO_Busyhour foreign key (VERSIONID, BHLEVEL, BHTYPE, TARGETVERSIONID, BHOBJECT)
       references Busyhour(VERSIONID, BHLEVEL, BHTYPE, TARGETVERSIONID, BHOBJECT)
       on delete restrict on update restrict;


alter table BusyhourPlaceholders
       add constraint FK_BusyhourPlaceholders_TO_Versioning foreign key (VERSIONID)
       references Versioning(VERSIONID)
       on delete restrict on update restrict;


alter table BusyhourRankkeys
       add constraint FK_BusyhourRankkeys_TO_Busyhour foreign key (VERSIONID, BHLEVEL, BHTYPE, TARGETVERSIONID, BHOBJECT)
       references Busyhour (VERSIONID, BHLEVEL, BHTYPE, TARGETVERSIONID, BHOBJECT)
       on delete restrict on update restrict;

 
alter table BusyhourSource
       add constraint FK_BusyhourSource_TO_Busyhour foreign key (VERSIONID, BHLEVEL, BHTYPE, TARGETVERSIONID, BHOBJECT)
       references Busyhour (VERSIONID, BHLEVEL, BHTYPE, TARGETVERSIONID, BHOBJECT)
       on delete restrict on update restrict;

 
alter table DataFormat
       add constraint FK_DataFormat_TO_Versioning foreign key (VERSIONID)
       references Versioning (VERSIONID)
       on delete restrict on update restrict;

 
alter table DataItem
       add constraint FK_DataItem_TO_DataFormat foreign key (DATAFORMATID)
       references DataFormat (DATAFORMATID)
       on delete restrict on update restrict;

 
alter table DefaultTags
       add constraint FK_DefaultTags_TO_DataFormat foreign key (DATAFORMATID)
       references DataFormat (DATAFORMATID)
       on delete restrict on update restrict;

 
alter table DWHColumn
       add constraint FK_DWHColumn_To_DWHType foreign key (STORAGEID)
       references DWHType (STORAGEID)
       on delete restrict on update restrict;

 
alter table DWHPartition
       add constraint FK_DWHPartition_To_DWHType foreign key (STORAGEID)
       references DWHType (STORAGEID)
       on delete restrict on update restrict;

 
alter table DWHType
       add constraint FK_DWHType_To_DWHTechPacks foreign key (TECHPACK_NAME)
       references DWHTechPacks (TECHPACK_NAME)
       on delete restrict on update restrict;

 
alter table ExternalStatement
       add constraint FK_ExternalStatement_To_Versioning foreign key (VERSIONID)
       references Versioning (VERSIONID)
       on delete restrict on update restrict;

 
alter table ExternalStatementStatus
       add constraint FK_ExternalStatementStatus_To_DWHTechPacks foreign key (TECHPACK_NAME)
       references DWHTechPacks (TECHPACK_NAME)
       on delete restrict on update restrict;

 
alter table InterfaceDependency
       add constraint FK_InterfaceDependency_TO_DataInterface foreign key (INTERFACENAME, INTERFACEVERSION)
       references DataInterface (INTERFACENAME, INTERFACEVERSION)
       on delete restrict on update restrict;

 
alter table InterfaceMeasurement
       add constraint FK_InterfaceMeasurement_TO_DataInterface foreign key (INTERFACENAME, INTERFACEVERSION)
       references DataInterface (INTERFACENAME, INTERFACEVERSION)
       on delete restrict on update restrict;

 
alter table InterfaceMeasurement
       add constraint FK_InterfaceMeas_TO_DataFormat foreign key (DATAFORMATID)
       references DataFormat (DATAFORMATID)
       on delete restrict on update restrict;

 
alter table InterfaceMeasurement
       add constraint FK_InterfaceMeas_TO_Transformer foreign key (TRANSFORMERID)
       references Transformer (TRANSFORMERID)
       on delete restrict on update restrict;

 
alter table InterfaceTechpacks
       add constraint FK_InterfaceTechpacks_TO_DataInterface foreign key (INTERFACENAME, INTERFACEVERSION)
       references DataInterface (INTERFACENAME, INTERFACEVERSION)
       on delete restrict on update restrict;

 
alter table MeasurementColumn
       add constraint FK_MeasurementData_To_StorageInfo foreign key (MTABLEID)
       references MeasurementTable (MTABLEID)
       on delete restrict on update restrict;

 
alter table MeasurementCounter
       add constraint FK_MEASUREM_MEASUREME_MEASUREM_1 foreign key (TYPEID)
       references MeasurementType (TYPEID)
       on delete restrict on update restrict;

 
alter table MeasurementDeltaCalcSupport
       add constraint FK_MeasurementDeltaCalcSupport_TO_Measurementtype foreign key (TYPEID)
       references MeasurementType (TYPEID)
       on delete restrict on update restrict;

 
alter table MeasurementDeltaCalcSupport
       add constraint FK_MeasurementDeltaCalcSupport_TO_Vendorrelease foreign key (VERSIONID, VENDORRELEASE)
       references SupportedVendorRelease (VERSIONID, VENDORRELEASE)
       on delete restrict on update restrict;

 
alter table MeasurementKey
       add constraint FK_MEASUREM_MEASUREME_MEASUREM foreign key (TYPEID)
       references MeasurementType (TYPEID)
       on delete restrict on update restrict;

 
alter table MeasurementObjBHSupport
       add constraint FK_MeasurementObjBHSupport_TO_Measurementtype foreign key (TYPEID)
       references MeasurementType (TYPEID)
       on delete restrict on update restrict;

 
alter table MeasurementTable
       add constraint FK_MeasurementTable_To_MeasurementType foreign key (TYPEID)
       references MeasurementType (TYPEID)
       on delete restrict on update restrict;

 
alter table MeasurementType
       add constraint FK_MeasurementType_To_MeasurementTypeClass foreign key (TYPECLASSID)
       references MeasurementTypeClass (TYPECLASSID)
       on delete restrict on update restrict;

 
alter table MeasurementType
       add constraint FK_MEASUREM_MEASUREME_VERSIONI foreign key (VERSIONID)
       references Versioning (VERSIONID)
       on delete restrict on update restrict;

 
alter table MeasurementTypeClass
       add constraint FK_MeasurementTypeClass_To_Versioning foreign key (VERSIONID)
       references Versioning (VERSIONID)
       on delete restrict on update restrict;

 
alter table MeasurementVector
       add constraint FK_MeasurementVector_To_MeasurementCounter foreign key (TYPEID, DATANAME)
       references MeasurementCounter (TYPEID, DATANAME)
       on delete restrict on update restrict;

 
alter table Prompt
       add constraint FK_Prompt_TO_Implementor foreign key (VERSIONID, PROMPTIMPLEMENTORID)
       references PromptImplementor (VERSIONID, PROMPTIMPLEMENTORID)
       on delete restrict on update restrict;

 
alter table PromptImplementor
       add constraint FK_PromptImplementor_TO_Versioning foreign key (VERSIONID)
       references Versioning (VERSIONID)
       on delete restrict on update restrict;

 
alter table PromptOption
       add constraint FK_Option_TO_Implementor foreign key (VERSIONID, PROMPTIMPLEMENTORID)
       references PromptImplementor (VERSIONID, PROMPTIMPLEMENTORID)
       on delete restrict on update restrict;

 
alter table ReferenceColumn
       add constraint FK_REFERENC_REFERENCE_REFERENC foreign key (TYPEID)
       references ReferenceTable (TYPEID)
       on delete restrict on update restrict;

 
alter table ReferenceTable
       add constraint FK_REFERENC_REFERENCE_VERSIONI foreign key (VERSIONID)
       references Versioning (VERSIONID)
       on delete restrict on update restrict;

 
alter table SupportedVendorRelease
       add constraint FK_SupportedVendorRelease_TO_Versioning foreign key (VERSIONID)
       references Versioning (VERSIONID)
       on delete restrict on update restrict;

 
alter table TechPackDependency
       add constraint FK_TechPackDependency_TO_Versioning foreign key (VERSIONID)
       references Versioning (VERSIONID)
       on delete restrict on update restrict;

 
alter table Transformation
       add constraint FK_Transformation_TO_Transformer foreign key (TRANSFORMERID)
       references Transformer (TRANSFORMERID)
       on delete restrict on update restrict;

 
alter table Transformer
       add constraint FK_Transformer_TO_Versioning foreign key (VERSIONID)
       references Versioning (VERSIONID)
       on delete restrict on update restrict;

 
alter table TypeActivation
       add constraint FK_TypeActivation_To_TPActivation foreign key (TECHPACK_NAME)
       references TPActivation (TECHPACK_NAME)
       on delete restrict on update restrict;

 
alter table UniverseClass
       add constraint FK_UniverseClass_TO_Versioning foreign key (VERSIONID)
       references Versioning (VERSIONID)
       on delete restrict on update restrict;

 
alter table UniverseComputedObject
       add constraint FK_UniverseComputedObject_TO_UniverseClass foreign key (VERSIONID, UNIVERSEEXTENSION, CLASSNAME)
       references UniverseClass (VERSIONID, UNIVERSEEXTENSION, CLASSNAME)
       on delete restrict on update restrict;

 
alter table UniverseCondition
       add constraint FK_UniverseCondition_TO_UniverseClass foreign key (VERSIONID, UNIVERSEEXTENSION, CLASSNAME)
       references UniverseClass (VERSIONID, UNIVERSEEXTENSION, CLASSNAME)
       on delete restrict on update restrict;

 
alter table UniverseJoin
       add constraint FK_UniverseJoin_TO_Versioning foreign key (VERSIONID)
       references Versioning (VERSIONID)
       on delete restrict on update restrict;

 
alter table UniverseName
       add constraint FK_UniverseName_TO_Versioning foreign key (VERSIONID)
       references Versioning (VERSIONID)
       on delete restrict on update restrict;

 
alter table UniverseObject
       add constraint FK_UniverseObject_TO_UniverseClass foreign key (VERSIONID, UNIVERSEEXTENSION, CLASSNAME)
       references UniverseClass (VERSIONID, UNIVERSEEXTENSION, CLASSNAME)
       on delete restrict on update restrict;

 
alter table UniverseParameters
       add constraint FK_UniverseParameters_TO_UniverseComputedObject foreign key (VERSIONID, CLASSNAME, UNIVERSEEXTENSION, OBJECTNAME)
       references UniverseComputedObject (VERSIONID, CLASSNAME, UNIVERSEEXTENSION, OBJECTNAME)
       on delete restrict on update restrict;

 
alter table UniverseTable
       add constraint FK_UniverseTable_TO_Versioning foreign key (VERSIONID)
       references Versioning (VERSIONID)
       on delete restrict on update restrict;

 
alter table VerificationCondition
       add constraint FK_VerificationCondition_TO_Versioning foreign key (VERSIONID)
       references Versioning (VERSIONID)
       on delete restrict on update restrict;

 
alter table VerificationObject
       add constraint FK_VerificationObject_TO_Versioning foreign key (VERSIONID)
       references Versioning (VERSIONID)
       on delete restrict on update restrict;
