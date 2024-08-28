create table META_DATABASES (
    USERNAME varchar(30) , 
    VERSION_NUMBER varchar(32) not null,
    TYPE_NAME varchar(15) not null, 
    CONNECTION_ID numeric(31) not null,
    CONNECTION_NAME varchar(30) not null,  
    CONNECTION_STRING varchar(200) not null,
    PASSWORD varchar(30) , 
    DESCRIPTION varchar(32000) , 
    DRIVER_NAME varchar(100) not null,
    DB_LINK_NAME varchar(128));  
  
create table META_COLLECTION_SETS (
	COLLECTION_SET_ID numeric(31) not null,
	COLLECTION_SET_NAME varchar(128) not null,
	DESCRIPTION varchar(32000) ,
	VERSION_NUMBER varchar(32) not null,
	ENABLED_FLAG varchar(1) ,
	TYPE varchar(32) 
);

 alter table META_COLLECTION_SETS
       add primary key (COLLECTION_SET_ID, VERSION_NUMBER);

