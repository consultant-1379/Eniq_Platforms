 create schema SYS authorization DBA
 create view SYSCOLUMNS as select sc.TABLE_NAME TNAME, sc.COLUMN_NAME CNAME, sc.ordinal_position COLNO, sc.data_type COLTYPE, sc.is_nullable NULLS, column_size length, sc.column_def DEFAULT_VALUE, sc.remarks REMARKS from information_schema.SYSTEM_COLUMNS sc
 create view SYSINDEXES as select si.index_name INAME, si.table_name TNAME, si.type INDEXTYPE, si.column_name COLNAMES, si.asc_or_desc INTERVAL from information_schema.SYSTEM_INDEXINFO si 
 create view SYSINDEX as select si.index_name INAME, si.type INDEXTYPE from information_schema.SYSTEM_INDEXINFO si 
 create view SYSTABLE as select st.table_name TABLE_NAME, st.table_type TABLE_TYPE, st.remarks REMARKS from information_schema.SYSTEM_TABLES st;
