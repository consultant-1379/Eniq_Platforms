
insert into META_DATABASES( USERNAME, VERSION_NUMBER, TYPE_NAME, CONNECTION_ID, CONNECTION_NAME, CONNECTION_STRING, PASSWORD, DESCRIPTION, DRIVER_NAME, DB_LINK_NAME) values ('etlrep', '0', 'USER', 0, 'etlrep', 'jdbc:hsqldb:mem:etlrep', 'etlrep', 'ETL Repository Database', 'org.hsqldb.jdbcDriver', null);
insert into META_DATABASES( USERNAME, VERSION_NUMBER, TYPE_NAME, CONNECTION_ID, CONNECTION_NAME, CONNECTION_STRING, PASSWORD, DESCRIPTION, DRIVER_NAME, DB_LINK_NAME) values ('dwhrep', '0', 'USER', 1, 'dwhrep', 'jdbc:hsqldb:mem:dwhrep', 'dwhrep', 'DWH Repository Database', 'org.hsqldb.jdbcDriver', null);
insert into META_DATABASES( USERNAME, VERSION_NUMBER, TYPE_NAME, CONNECTION_ID, CONNECTION_NAME, CONNECTION_STRING, PASSWORD, DESCRIPTION, DRIVER_NAME, DB_LINK_NAME) values ('dc', '0', 'USER', 2, 'dwh', 'jdbc:hsqldb:mem:dwh', 'dc', 'The DataWareHouse Database', 'org.hsqldb.jdbcDriver', null);

insert into META_COLLECTION_SETS( COLLECTION_SET_ID, COLLECTION_SET_NAME, DESCRIPTION, VERSION_NUMBER, ENABLED_FLAG, TYPE) values (0, 'DWH_BASE', '', 'R2D_b999', 'Y', 'Maintenance');
insert into META_COLLECTION_SETS( COLLECTION_SET_ID, COLLECTION_SET_NAME, DESCRIPTION, VERSION_NUMBER, ENABLED_FLAG, TYPE) values (1, 'DWH_MONITOR', 'Monitoring', 'R2D_b999', 'Y', 'Maintenance');
insert into META_COLLECTION_SETS (COLLECTION_SET_ID, COLLECTION_SET_NAME, DESCRIPTION, VERSION_NUMBER, ENABLED_FLAG, TYPE) values (2, 'DC_E_STN', '', '((999))', 'Y', '');
