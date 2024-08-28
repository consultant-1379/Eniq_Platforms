insert into TPACTIVATION (TECHPACK_NAME, STATUS, VERSIONID, TYPE, MODIFIED) values ('DC_E_STN', 'ACTIVE', 'DC_E_STN:((999))', 'PM', 1);

insert into VERSIONING (VERSIONID, DESCRIPTION, STATUS, TECHPACK_NAME, TECHPACK_VERSION, TECHPACK_TYPE, PRODUCT_NUMBER, LOCKEDBY, LOCKDATE, BASEDEFINITION, INSTALLDESCRIPTION, ENIQ_LEVEL, LICENSENAME) values ('DC_E_STN:((999))', 'Ericsson STN', '1', 'DC_E_STN', 'R9A', 'PM', 'COA 252 143/1', 'AFJ_MANAGER', '2010-08-31 10:00:00.0', 'TP_BASE:BASE_TP_20100506', '', '11', 'CXC4010585');

insert into MEASUREMENTTYPECLASS (TYPECLASSID, VERSIONID, DESCRIPTION) values ('DC_E_STN:((999)):DC_E_STN_E1INTERFACE', 'DC_E_STN:((999))', 'E1Interface');

insert into MEASUREMENTTYPECLASS (TYPECLASSID, VERSIONID, DESCRIPTION) values ('DC_E_STN:((999)):DC_E_STN_FOLLOWJOHN', 'DC_E_STN:((999))', 'Followjohn');

insert into MEASUREMENTTYPE (TYPEID, TYPECLASSID, TYPENAME, VENDORID, FOLDERNAME, DESCRIPTION, STATUS, VERSIONID, OBJECTID, OBJECTNAME, OBJECTVERSION, OBJECTTYPE, JOINABLE, SIZING, TOTALAGG, ELEMENTBHSUPPORT, RANKINGTABLE, DELTACALCSUPPORT, PLAINTABLE, UNIVERSEEXTENSION, VECTORSUPPORT, DATAFORMATSUPPORT, FOLLOWJOHN ) values ( 'DC_E_STN:((999)):DC_E_STN_E1INTERFACE', 'DC_E_STN:((999)):DC_E_STN_E1INTERFACE', 'DC_E_STN_E1INTERFACE', 'DC_E_STN', 'DC_E_STN_E1INTERFACE', 'This MO is created by the junit test.', null, 'DC_E_STN:((999))', 'DC_E_STN:((999)):DC_E_STN_E1INTERFACE', 'DC_E_STN_E1INTERFACE', null, null, '', 'small', 1, 1, 0, 0, 0, 'b', 0, 1, 0 );

insert into MEASUREMENTTYPE (TYPEID, TYPECLASSID, TYPENAME, VENDORID, FOLDERNAME, DESCRIPTION, STATUS, VERSIONID, OBJECTID, OBJECTNAME, OBJECTVERSION, OBJECTTYPE, JOINABLE, SIZING, TOTALAGG, ELEMENTBHSUPPORT, RANKINGTABLE, DELTACALCSUPPORT, PLAINTABLE, UNIVERSEEXTENSION, VECTORSUPPORT, DATAFORMATSUPPORT, FOLLOWJOHN ) values ( 'DC_E_STN:((999)):DC_E_STN_FOLLOWJOHN', 'DC_E_STN:((999)):DC_E_STN_FOLLOWJOHN', 'DC_E_STN_FOLLOWJOHN', 'DC_E_STN', 'DC_E_STN_FOLLOWJOHN', 'This MO is created by the junit test.', null, 'DC_E_STN:((999))', 'DC_E_STN:((999)):DC_E_STN_FOLLOWJOHN', 'DC_E_STN_FOLLOWJOHN', null, null, '', 'small', 1, 1, 0, 0, 0, 'b', 0, 1, 1 );

insert into MEASUREMENTKEY(TYPEID, DATANAME, DESCRIPTION, ISELEMENT, UNIQUEKEY, COLNUMBER, DATATYPE, DATASIZE, DATASCALE, UNIQUEVALUE, NULLABLE, INDEXES, INCLUDESQL, UNIVOBJECT, JOINABLE, DATAID) values ('DC_E_STN:((999)):DC_E_STN_E1INTERFACE', 'DUMMY_ID', 'Dummy Identification', 0, 0, null, null, null, null, null, null, null, null, null, null, null);

insert into MEASUREMENTKEY(TYPEID, DATANAME, DESCRIPTION, ISELEMENT, UNIQUEKEY, COLNUMBER, DATATYPE, DATASIZE, DATASCALE, UNIQUEVALUE, NULLABLE, INDEXES, INCLUDESQL, UNIVOBJECT, JOINABLE, DATAID) values ('DC_E_STN:((999)):DC_E_STN_FOLLOWJOHN', 'DUMMY_ID', 'Dummy Identification', 0, 0, null, null, null, null, null, null, null, null, null, null, null);

insert into MEASUREMENTCOUNTER (TYPEID, DATANAME, DESCRIPTION, TIMEAGGREGATION, GROUPAGGREGATION, COUNTAGGREGATION, COLNUMBER, DATATYPE, DATASIZE, DATASCALE, INCLUDESQL, UNIVOBJECT, UNIVCLASS, COUNTERTYPE, COUNTERPROCESS, DATAID, FOLLOWJOHN) values('DC_E_STN:((999)):DC_E_STN_E1INTERFACE','ifInErrors','Number of inbound packets that ...','SUM','SUM','PEG',102,'numeric',20,0,1,'ifInErrors',null,'PEG','PEG','ifInErrors',null);

insert into MEASUREMENTCOUNTER (TYPEID, DATANAME, DESCRIPTION, TIMEAGGREGATION, GROUPAGGREGATION, COUNTAGGREGATION, COLNUMBER, DATATYPE, DATASIZE, DATASCALE, INCLUDESQL, UNIVOBJECT, UNIVCLASS, COUNTERTYPE, COUNTERPROCESS, DATAID, FOLLOWJOHN) values('DC_E_STN:((999)):DC_E_STN_E1INTERFACE','ifOutErrors','Number of outbound packets that ...','SUM','SUM','PEG',103,'numeric',20,0,1,'ifOutErrors',null,'PEG','PEG','ifOutErrors',1);

insert into MEASUREMENTCOUNTER (TYPEID, DATANAME, DESCRIPTION, TIMEAGGREGATION, GROUPAGGREGATION, COUNTAGGREGATION, COLNUMBER, DATATYPE, DATASIZE, DATASCALE, INCLUDESQL, UNIVOBJECT, UNIVCLASS, COUNTERTYPE, COUNTERPROCESS, DATAID, FOLLOWJOHN) values('DC_E_STN:((999)):DC_E_STN_FOLLOWJOHN','ifInErrors','Number of inbound packets...','SUM','SUM','PEG',102,'numeric',20,0,1,'ifInErrors',null,'PEG','PEG','ifInErrors', 1);

insert into MEASUREMENTTABLE (MTABLEID, TABLELEVEL, TYPEID, BASETABLENAME, DEFAULT_TEMPLATE, PARTITIONPLAN ) values ( 'DC_E_STN:((999)):DC_E_STN_E1INTERFACE:RAW', 'RAW', 'DC_E_STN:((999)):DC_E_STN_E1INTERFACE', 'DC_E_STN_E1INTERFACE_RAW', null, 'small_raw' );

insert into MEASUREMENTTABLE (MTABLEID, TABLELEVEL, TYPEID, BASETABLENAME, DEFAULT_TEMPLATE, PARTITIONPLAN ) values ( 'DC_E_STN:((999)):DC_E_STN_FOLLOWJOHN:RAW', 'RAW', 'DC_E_STN:((999)):DC_E_STN_FOLLOWJOHN', 'DC_E_STN_FOLLOWJOHN_RAW', null, 'small_raw' );

insert into MEASUREMENTCOLUMN (MTABLEID, DATANAME, COLNUMBER, DATATYPE, DATASIZE, DATASCALE, UNIQUEVALUE, NULLABLE, INDEXES, DESCRIPTION, DATAID, RELEASEID, UNIQUEKEY, INCLUDESQL, COLTYPE, FOLLOWJOHN) values('DC_E_STN:((999)):DC_E_STN_E1INTERFACE:RAW','DUMMY_ID',26,'numeric',20,0,255,1,'HG','ID','DUMMY_ID','DC_E_STN:((B222))',0,1,'KEY',NULL);
		
insert into MEASUREMENTCOLUMN (MTABLEID, DATANAME, COLNUMBER, DATATYPE, DATASIZE, DATASCALE, UNIQUEVALUE, NULLABLE, INDEXES, DESCRIPTION, DATAID, RELEASEID, UNIQUEKEY, INCLUDESQL, COLTYPE, FOLLOWJOHN) values('DC_E_STN:((999)):DC_E_STN_E1INTERFACE:RAW','ifInErrors',26,'numeric',20,0,255,1,'','Number of inbound packets that contained error preventing them from being deliverable to higher-layer protocol.','ifInErrors','DC_E_STN:((B222))',0,1,'COUNTER',NULL);
		
insert into MEASUREMENTCOLUMN (MTABLEID, DATANAME, COLNUMBER, DATATYPE, DATASIZE, DATASCALE, UNIQUEVALUE, NULLABLE, INDEXES, DESCRIPTION, DATAID, RELEASEID, UNIQUEKEY, INCLUDESQL, COLTYPE, FOLLOWJOHN) values('DC_E_STN:((999)):DC_E_STN_E1INTERFACE:RAW','ifOutErrors',27,'numeric',20,0,255,1,'','Number of inbound packets that contained error preventing them from being deliverable to higher-layer protocol.','ifOutErrors','DC_E_STN:((B222))',0,1,'COUNTER', 1);

insert into MEASUREMENTCOLUMN (MTABLEID, DATANAME, COLNUMBER, DATATYPE, DATASIZE, DATASCALE, UNIQUEVALUE, NULLABLE, INDEXES, DESCRIPTION, DATAID, RELEASEID, UNIQUEKEY, INCLUDESQL, COLTYPE, FOLLOWJOHN) values('DC_E_STN:((999)):DC_E_STN_FOLLOWJOHN:RAW','DUMMY_ID',26,'numeric',20,0,255,1,'HG','ID','DUMMY_ID','DC_E_STN:((B222))',0,1,'KEY',NULL);
		
insert into MEASUREMENTCOLUMN (MTABLEID, DATANAME, COLNUMBER, DATATYPE, DATASIZE, DATASCALE, UNIQUEVALUE, NULLABLE, INDEXES, DESCRIPTION, DATAID, RELEASEID, UNIQUEKEY, INCLUDESQL, COLTYPE, FOLLOWJOHN) values('DC_E_STN:((999)):DC_E_STN_FOLLOWJOHN:RAW','ifInErrors',26,'numeric',20,0,255,1,'','Number of inbound packets that contained error preventing them from being deliverable to higher-layer protocol.','ifInErrors','DC_E_STN:((B222))',0,1,'COUNTER', 1);
		
insert into INTERFACETECHPACKS (INTERFACENAME, TECHPACKNAME, TECHPACKVERSION, INTERFACEVERSION) values ('INTF_DC_E_STN','DC_E_STN','R1A','((100))');

insert into INTERFACEMEASUREMENT (TAGID, DATAFORMATID, INTERFACENAME, TRANSFORMERID, STATUS, MODIFTIME, DESCRIPTION, TECHPACKVERSION, INTERFACEVERSION) values ('E1Interface', 'DC_E_STN:((999)):DC_E_STN_E1INTERFACE:mdc', 'INTF_DC_E_STN', 'DC_E_STN:((999)):DC_E_STN_E1INTERFACE:mdc', 1, '2010-08-24 22:09:34.0', 'Default tags for DC_E_STN_E1INTERFACE in DC_E_STN:((999)) with format mdc.', 'N/A', '((101))');

insert into INTERFACEMEASUREMENT (TAGID, DATAFORMATID, INTERFACENAME, TRANSFORMERID, STATUS, MODIFTIME, DESCRIPTION, TECHPACKVERSION, INTERFACEVERSION) values ('Followjohn', 'DC_E_STN:((999)):DC_E_STN_FOLLOWJOHN:mdc', 'INTF_DC_E_STN', 'DC_E_STN:((999)):DC_E_STN_FOLLOWJOHN:mdc', 1, '2010-08-24 22:09:34.0', 'Default tags for DC_E_STN_FOLLOWJOHN in DC_E_STN:((999)) with format mdc.', 'N/A', '((101))');

insert into DEFAULTTAGS (TAGID,DATAFORMATID,DESCRIPTION) values ('E1Interface','DC_E_STN:((999)):DC_E_STN_E1INTERFACE:mdc','Default tags for DC_E_STN_E1INTERFACE in DC_E_STN:((999)) with format mdc.');

insert into DEFAULTTAGS (TAGID,DATAFORMATID,DESCRIPTION) values ('Followjohn','DC_E_STN:((999)):DC_E_STN_FOLLOWJOHN:mdc','Default tags for DC_E_STN_FOLLOWJOHN in DC_E_STN:((999)) with format mdc.');

insert into DEFAULTTAGS (TAGID,DATAFORMATID,DESCRIPTION) values ('XYZ','DC_E_BSS:((16)):DC_E_BSS_XYZ:mdc','Default tags for DC_E_BSS_XYZ in DC_E_BSS:((16)) with format mdc.');

insert into DEFAULTTAGS (TAGID,DATAFORMATID,DESCRIPTION) values ('ABC','DC_E_CMN_STS:((3)):DC_E_CMN_STS_ABC:mdc','Default tags for DC_E_CMN_STS_ABC in DC_E_CMN_STS:((3)) with format mdc.');

insert into DATAFORMAT (DATAFORMATID, TYPEID, VERSIONID, OBJECTTYPE, FOLDERNAME, DATAFORMATTYPE) VALUES ('DC_E_STN:((999)):DC_E_STN_E1INTERFACE:mdc', 'DC_E_STN:((999)):DC_E_STN_E1INTERFACE', 'DC_E_STN:((999))', 'Measurement', 'DC_E_STN_E1INTERFACE', 'mdc');

insert into DATAFORMAT (DATAFORMATID, TYPEID, VERSIONID, OBJECTTYPE, FOLDERNAME, DATAFORMATTYPE) VALUES ('DC_E_STN:((999)):DC_E_STN_FOLLOWJOHN:mdc', 'DC_E_STN:((999)):DC_E_STN_FOLLOWJOHN', 'DC_E_STN:((999))', 'Measurement', 'DC_E_STN_FOLLOWJOHN', 'mdc');

insert into DATAITEM (DATAFORMATID, DATANAME, COLNUMBER, DATAID, PROCESS_INSTRUCTION, DATATYPE, DATASIZE, DATASCALE) VALUES ('DC_E_STN:((999)):DC_E_STN_E1INTERFACE:mdc', 'DUMMY_ID', 1, 'DUMMY_ID', '', 'varchar', 50, 0);

insert into DATAITEM (DATAFORMATID, DATANAME, COLNUMBER, DATAID, PROCESS_INSTRUCTION, DATATYPE, DATASIZE, DATASCALE) VALUES ('DC_E_STN:((999)):DC_E_STN_E1INTERFACE:mdc', 'ifInErrors', 102, 'ifInErrors', 'PEG', 'varchar', 50, 0);

insert into DATAITEM (DATAFORMATID, DATANAME, COLNUMBER, DATAID, PROCESS_INSTRUCTION, DATATYPE, DATASIZE, DATASCALE) VALUES ('DC_E_STN:((999)):DC_E_STN_E1INTERFACE:mdc', 'ifOutErrors', 103, 'ifOutErrors', 'PEG', 'varchar', 50, 0);

insert into DATAITEM (DATAFORMATID, DATANAME, COLNUMBER, DATAID, PROCESS_INSTRUCTION, DATATYPE, DATASIZE, DATASCALE) VALUES ('DC_E_STN:((999)):DC_E_STN_FOLLOWJOHN:mdc', 'DUMMY_ID', 1, 'DUMMY_ID', '', 'varchar', 50, 0);

insert into DATAITEM (DATAFORMATID, DATANAME, COLNUMBER, DATAID, PROCESS_INSTRUCTION, DATATYPE, DATASIZE, DATASCALE) VALUES ('DC_E_STN:((999)):DC_E_STN_FOLLOWJOHN:mdc', 'ifInErrors', 102, 'ifInErrors', 'PEG', 'varchar', 50, 0);

insert into Transformer (TRANSFORMERID, VERSIONID, DESCRIPTION, TYPE) values ('DC_E_STN:((999)):DC_E_STN_E1INTERFACE:mdc', 'DC_E_STN:((999))', null, 'SPECIFIC');

insert into Transformer (TRANSFORMERID, VERSIONID, DESCRIPTION, TYPE) values ('DC_E_STN:((999)):DC_E_STN_FOLLOWJOHN:mdc', 'DC_E_STN:((999))', null, 'SPECIFIC');

insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG, DESCRIPTION) values ('DC_E_STN:((999)):DC_E_STN_E1INTERFACE:mdc', 1, 'SomeType', 'SOURCE', 'TARGET', 'xxx', null); 

insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG, DESCRIPTION) values ('DC_E_STN:((999)):DC_E_STN_FOLLOWJOHN:mdc', 1, 'SomeType', 'SOURCE', 'TARGET', 'xxx', null);

insert into Typeactivation (TECHPACK_NAME, STATUS, TYPENAME, TABLELEVEL, STORAGETIME, TYPE, PARTITIONPLAN) values ('DC_E_STN', 'ACTIVE', 'DC_E_STN_E1INTERFACE', 'RAW',	-1,	'Measurement', 'medium_raw');

insert into Typeactivation (TECHPACK_NAME, STATUS, TYPENAME, TABLELEVEL, STORAGETIME, TYPE, PARTITIONPLAN) values ('DC_E_STN', 'ACTIVE', 'DC_E_STN_FOLLOWJOHN', 'RAW',	-1,	'Measurement', 'medium_raw');

insert into DWHType (TECHPACK_NAME, TYPENAME, TABLELEVEL, STORAGEID, PARTITIONSIZE, PARTITIONCOUNT, STATUS, TYPE, OWNER, VIEWTEMPLATE, CREATETEMPLATE, NEXTPARTITIONTIME, BASETABLENAME, DATADATECOLUMN, PUBLICVIEWTEMPLATE, PARTITIONPLAN) values ('DC_E_STN', 'DC_E_STN_E1INTERFACE', 'RAW', 'DC_E_STN_E1INTERFACE:RAW', -1, 6, 'ENABLED', 'PARTITIONED', 'dc', 'createview.vm', 'createpartition.vm', null, 'DC_E_STN_E1INTERFACE_RAW', 'DATE_ID', 'createpublicview.vm', 'medium_raw');

insert into DWHType (TECHPACK_NAME, TYPENAME, TABLELEVEL, STORAGEID, PARTITIONSIZE, PARTITIONCOUNT, STATUS, TYPE, OWNER, VIEWTEMPLATE, CREATETEMPLATE, NEXTPARTITIONTIME, BASETABLENAME, DATADATECOLUMN, PUBLICVIEWTEMPLATE, PARTITIONPLAN) values ('DC_E_STN', 'DC_E_STN_FOLLOWJOHN', 'RAW', 'DC_E_STN_FOLLOWJOHN:RAW', -1, 6, 'ENABLED', 'PARTITIONED', 'dc', 'createview.vm', 'createpartition.vm', null, 'DC_E_STN_E1INTERFACE_RAW', 'DATE_ID', 'createpublicview.vm', 'medium_raw');
