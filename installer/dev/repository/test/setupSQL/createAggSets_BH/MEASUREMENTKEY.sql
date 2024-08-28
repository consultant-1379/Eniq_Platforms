insert into MEASUREMENTKEY (TYPEID, DATANAME, DESCRIPTION, ISELEMENT, UNIQUEKEY, COLNUMBER, DATATYPE, DATASIZE, DATASCALE, UNIQUEVALUE, NULLABLE, INDEXES, INCLUDESQL, UNIVOBJECT, DATAID) values ('DC_E_BSS:((12)):DC_E_BSS_BSC', 'BSC', 'Base Station Controller', '1', '1', '3', 'varchar', '64', '0', '255', '1', 'HG', '1', 'BSC', 'BSC');
insert into MEASUREMENTKEY (TYPEID, DATANAME, DESCRIPTION, ISELEMENT, UNIQUEKEY, COLNUMBER, DATATYPE, DATASIZE, DATASCALE, UNIQUEVALUE, NULLABLE, INDEXES, INCLUDESQL, UNIVOBJECT, DATAID) values ('DC_E_BSS:((12)):DC_E_BSS_BSC', 'MOID', 'Measured Object Id', '0', '0', '4', 'varchar', '255', '0', '255', '1', 'HG', '1', 'MOID', 'MOID');
insert into MEASUREMENTKEY (TYPEID, DATANAME, DESCRIPTION, ISELEMENT, UNIQUEKEY, COLNUMBER, DATATYPE, DATASIZE, DATASCALE, UNIQUEVALUE, NULLABLE, INDEXES, INCLUDESQL, UNIVOBJECT, DATAID) values ('DC_E_BSS:((12)):DC_E_BSS_BSC', 'OSS_ID', 'OSS Id', '0', '1', '1', 'varchar', '50', '0', '255', '1', 'HG', '1', 'OSS Id', 'OSS_ID');
insert into MEASUREMENTKEY (TYPEID, DATANAME, DESCRIPTION, ISELEMENT, UNIQUEKEY, COLNUMBER, DATATYPE, DATASIZE, DATASCALE, UNIQUEVALUE, NULLABLE, INDEXES, INCLUDESQL, UNIVOBJECT, JOINABLE, DATAID) values ('DC_E_BSS:((12)):DC_E_BSS_BSC', 'RECORDTYPE', 'Object type', '0', '1', '5', 'varchar', '64', '0', '255', '1', 'HG', '0', '', '1', 'RECORDTYPE');
insert into MEASUREMENTKEY (TYPEID, DATANAME, DESCRIPTION, ISELEMENT, UNIQUEKEY, COLNUMBER, DATATYPE, DATASIZE, DATASCALE, UNIQUEVALUE, NULLABLE, INDEXES, INCLUDESQL, UNIVOBJECT, DATAID) values ('DC_E_BSS:((12)):DC_E_BSS_BSC', 'SN', 'Sender Name', '0', '0', '2', 'varchar', '255', '0', '255', '1', '', '1', '', 'SN');
insert into MEASUREMENTKEY (TYPEID, DATANAME, DESCRIPTION, ISELEMENT, UNIQUEKEY, COLNUMBER, DATATYPE, DATASIZE, DATASCALE, UNIQUEVALUE, NULLABLE, INDEXES, INCLUDESQL, UNIVOBJECT, DATAID) values ('DC_E_BSS:((12)):DC_E_BSS_CELL3', 'BSC', 'Base Station Controller', '1', '1', '3', 'varchar', '64', '0', '255', '1', 'HG', '1', 'BSC', 'BSC');
insert into MEASUREMENTKEY (TYPEID, DATANAME, DESCRIPTION, ISELEMENT, UNIQUEKEY, COLNUMBER, DATATYPE, DATASIZE, DATASCALE, UNIQUEVALUE, NULLABLE, INDEXES, INCLUDESQL, UNIVOBJECT, JOINABLE, DATAID) values ('DC_E_BSS:((12)):DC_E_BSS_CELL3', 'CELL_NAME', 'Cell Name', '0', '1', '6', 'varchar', '64', '0', '255', '1', 'HG', '1', 'Cell Name', '0', 'CELL_NAME');
insert into MEASUREMENTKEY (TYPEID, DATANAME, DESCRIPTION, ISELEMENT, UNIQUEKEY, COLNUMBER, DATATYPE, DATASIZE, DATASCALE, UNIQUEVALUE, NULLABLE, INDEXES, INCLUDESQL, UNIVOBJECT, DATAID) values ('DC_E_BSS:((12)):DC_E_BSS_CELL3', 'MOID', 'Measured Object Id', '0', '0', '4', 'varchar', '255', '0', '255', '1', 'HG', '1', 'MOID', 'MOID');
insert into MEASUREMENTKEY (TYPEID, DATANAME, DESCRIPTION, ISELEMENT, UNIQUEKEY, COLNUMBER, DATATYPE, DATASIZE, DATASCALE, UNIQUEVALUE, NULLABLE, INDEXES, INCLUDESQL, UNIVOBJECT, DATAID) values ('DC_E_BSS:((12)):DC_E_BSS_CELL3', 'OSS_ID', 'OSS Id', '0', '1', '1', 'varchar', '50', '0', '255', '1', 'HG', '1', 'OSS Id', 'OSS_ID');
insert into MEASUREMENTKEY (TYPEID, DATANAME, DESCRIPTION, ISELEMENT, UNIQUEKEY, COLNUMBER, DATATYPE, DATASIZE, DATASCALE, UNIQUEVALUE, NULLABLE, INDEXES, INCLUDESQL, UNIVOBJECT, JOINABLE, DATAID) values ('DC_E_BSS:((12)):DC_E_BSS_CELL3', 'RECORDTYPE', 'Object type', '0', '1', '5', 'varchar', '64', '0', '255', '1', 'HG', '0', '', '1', 'RECORDTYPE');
insert into MEASUREMENTKEY (TYPEID, DATANAME, DESCRIPTION, ISELEMENT, UNIQUEKEY, COLNUMBER, DATATYPE, DATASIZE, DATASCALE, UNIQUEVALUE, NULLABLE, INDEXES, INCLUDESQL, UNIVOBJECT, DATAID) values ('DC_E_BSS:((12)):DC_E_BSS_CELL3', 'SN', 'Sender Name', '0', '0', '2', 'varchar', '255', '0', '255', '1', '', '1', '', 'SN');
insert into MEASUREMENTKEY (TYPEID, DATANAME, DESCRIPTION, ISELEMENT, UNIQUEKEY, COLNUMBER, DATATYPE, DATASIZE, DATASCALE, UNIQUEVALUE, NULLABLE, INDEXES, INCLUDESQL, UNIVOBJECT) values ('DC_E_BSS:((12)):DC_E_BSS_CELLBH', 'BSC', 'BSC', '1', '1', '3', 'varchar', '35', '0', '255', '1', 'HG', '1', 'BSC');
insert into MEASUREMENTKEY (TYPEID, DATANAME, DESCRIPTION, ISELEMENT, UNIQUEKEY, COLNUMBER, DATATYPE, DATASIZE, DATASCALE, UNIQUEVALUE, NULLABLE, INDEXES, INCLUDESQL, UNIVOBJECT) values ('DC_E_BSS:((12)):DC_E_BSS_CELLBH', 'CELL_NAME', 'Cell Name', '0', '1', '2', 'varchar', '35', '0', '255', '1', 'HG', '1', 'Cell Name');
insert into MEASUREMENTKEY (TYPEID, DATANAME, DESCRIPTION, ISELEMENT, UNIQUEKEY, COLNUMBER, DATATYPE, DATASIZE, DATASCALE, UNIQUEVALUE, NULLABLE, INDEXES, INCLUDESQL, UNIVOBJECT) values ('DC_E_BSS:((12)):DC_E_BSS_CELLBH', 'OSS_ID', 'OSS Id', '0', '1', '1', 'varchar', '50', '0', '255', '1', 'HG', '1', 'OSS Id');
