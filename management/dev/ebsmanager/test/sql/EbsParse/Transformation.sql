insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSG:((3)):ALL:ebs', '0', 'calculation', 'PERIOD_DURATION', 'PERIOD_DURATION', 'formula=/60
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSG:((3)):ALL:ebs', '1', 'databaseLookup', 'PERIOD_DURATION', 'TIMELEVEL', 'sql=select DURATIONMIN, TIMELEVEL from DIM_TIMELEVEL where TABLELEVEL = ''RAW''
basetable=DIM_TIMELEVEL
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSG:((3)):ALL:ebs', '2', 'lookup', 'DIRNAME', 'OSS_ID', 'pattern=.+/(.+)/.+?/.+?
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSG:((3)):ALL:ebs', '3', 'propertytokenizer', 'nedn', 'nedn', 'delim=,
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSG:((3)):ALL:ebs', '4', 'fixed', ' ', 'NE_VERSION', 'value=null
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSG:((3)):ALL:ebs', '5', 'copy', 'nedn.ManagedElement', 'TempBSC', '');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSG:((3)):ALL:ebs', '6', 'preappender', 'TempBSC', 'TempBSC', 'fixed=:
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSG:((3)):ALL:ebs', '7', 'preappender', 'TempBSC', 'TempBSC', 'field=OSS_ID
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSG:((3)):ALL:ebs', '8', 'databaseLookup', 'TempBSC', 'NE_VERSION', 'sql=select OSS_ID||'':''||BSC_NAME,BSC_VERSION from DIM_E_GRAN_BSC
basetable=DIM_E_GRAN_BSC
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSG:((3)):ALL:ebs', '9', 'copy', 'OSS_ID', 'DC_SOURCE', '');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSG:((3)):ALL:ebs', '10', 'databaseLookup', 'TempBSC', 'TRC_VERSION', 'sql=select OSS_ID||'':''||TRC_NAME, SITE_FDN from DIM_E_GRAN_TRC
basetable=DIM_E_GRAN_TRC
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSG:((3)):ALL:ebs', '11', 'condition', 'NE_VERSION', 'DC_RELEASE', 'factor=null
result1field=TRC_VERSION
result2field=NE_VERSION
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSG:((3)):ALL:ebs', '12', 'fixed', ' ', 'DC_TIMEZONE', 'value=TIMEZONE
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSG:((3)):ALL:ebs', '13', 'lookup', 'filename', 'DC_TIMEZONE', 'pattern=A.{13}(.{5})-.+
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSG:((3)):ALL:ebs', '14', 'condition', 'DC_TIMEZONE', 'DC_TIMEZONE', 'factor=TIMEZONE
result1field=JVM_TIMEZONE
result2field=DC_TIMEZONE
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSG:((3)):ALL:ebs', '15', 'fixed', ' ', 'NE_SITE_FDN', 'value=null
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSG:((3)):ALL:ebs', '16', 'databaseLookup', 'TempBSC', 'NE_SITE_FDN', 'sql=select OSS_ID||'':''||BSC_NAME, SITE_FDN from DIM_E_GRAN_BSC
basetable=DIM_E_GRAN_BSC
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSG:((3)):ALL:ebs', '17', 'databaseLookup', 'TempBSC', 'TRC_SITE_FDN', 'sql=select OSS_ID||'':''||TRC_NAME, SITE_FDN from DIM_E_GRAN_TRC
basetable=DIM_E_GRAN_TRC
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSG:((3)):ALL:ebs', '18', 'condition', 'NE_SITE_FDN', 'TempSITE_NAME', 'factor=null
result1field=TRC_SITE_FDN
result2field=NE_SITE_FDN
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSG:((3)):ALL:ebs', '19', 'preappender', 'TempSITE_NAME', 'TempSITE_NAME', 'fixed=:
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSG:((3)):ALL:ebs', '20', 'preappender', 'TempSITE_NAME', 'TempSITE_NAME', 'field=OSS_ID
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSG:((3)):ALL:ebs', '21', 'fixed', ' ', 'NE_TIMEZONE', 'value=null
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSG:((3)):ALL:ebs', '22', 'databaseLookup', 'TempSITE_NAME', 'NE_TIMEZONE', 'sql=select OSS_ID||'':''||SITE_FDN, TIMEZONE_VALUE from DIM_E_GRAN_SITE
basetable=DIM_E_GRAN_SITE
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSG:((3)):ALL:ebs', '23', 'condition', 'NE_TIMEZONE', 'DC_TIMEZONE', 'factor=null
result1field=DC_TIMEZONE
result2field=NE_TIMEZONE
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSG:((3)):ALL:ebs', '24', 'lookup', 'DATETIME_ID', 'UTC_DATETIME_ID', 'pattern=(\\d{14})
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSG:((3)):ALL:ebs', '25', 'DateFormat', 'UTC_DATETIME_ID', 'UTC_DATETIME_ID', 'oldformat=yyyyMMddHHmmss
newformat=yyyy-MM-dd HH:mm:ss
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSG:((3)):ALL:ebs', '26', 'DateFormat', 'UTC_DATETIME_ID', 'DATETIME_ID', 'oldformat=yyyy-MM-dd HH:mm:ss
newformat=yyyy-MM-dd HH:mm:ss
oldtimezone=+0000
newtimezonefield=DC_TIMEZONE
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSG:((3)):ALL:ebs', '27', 'defaulttimehandler', 'DATETIME_ID', ' ', 'oldformat=yyyy-MM-dd HH:mm:ss
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSG:((3)):ALL:ebs', '28', 'condition', 'DC_SUSPECTFLAG', 'ROWSTATUS', 'factor=TRUE
result1=SUSPECTED
result2field=ROWSTATUS
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSG:((3)):ALL:ebs', '29', 'condition', 'DC_SUSPECTFLAG', 'DC_SUSPECTFLAG', 'factor=TRUE
result1=1
result2field=DC_SUSPECTFLAG
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSG:((3)):ALL:ebs', '30', 'condition', 'DC_SUSPECTFLAG', 'DC_SUSPECTFLAG', 'factor=FALSE
result1=0
result2field=DC_SUSPECTFLAG
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSG:((3)):PM_E_EBSG_CELL:ebs', '31', 'lookup', 'MOID', 'CELL_NAME', 'pattern=.+GsmCell=(.+)
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSG:((3)):PM_E_EBSG_TRX:ebs', '32', 'lookup', 'MOID', 'TRX', 'pattern=.+Trx=(.+)
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSG:((3)):PM_E_EBSG_TRX:ebs', '33', 'lookup', 'MOID', 'CELL_NAME', 'pattern=.+GsmCell=(.+),.+
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSS:((2)):ALL:ebs', '0', 'propertytokenizer', 'nedn', 'nedn', 'delim=,
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSS:((2)):ALL:ebs', '1', 'fixed', ' ', 'DC_TIMEZONE', 'value=TIMEZONE
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSS:((2)):ALL:ebs', '2', 'lookup', 'filename', 'DC_TIMEZONE', 'pattern=A.{13}(.{5})-.+
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSS:((2)):ALL:ebs', '3', 'condition', 'DC_TIMEZONE', 'DC_TIMEZONE', 'factor=TIMEZONE
result1field=JVM_TIMEZONE
result2field=DC_TIMEZONE
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSS:((2)):ALL:ebs', '4', 'lookup', 'DIRNAME', 'OSS_ID', 'pattern=.+/(.+)/.+/.+
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSS:((2)):ALL:ebs', '5', 'propertytokenizer', 'sn', 'sn', 'delim=,
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSS:((2)):ALL:ebs', '6', 'copy', 'nedn.ManagedElement', 'NE_Identifier', '');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSS:((2)):ALL:ebs', '7', 'preappender', 'NE_Identifier', 'NE_Identifier', 'fixed=:
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSS:((2)):ALL:ebs', '8', 'preappender', 'NE_Identifier', 'NE_Identifier', 'field=OSS_ID
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSS:((2)):ALL:ebs', '9', 'fixed', ' ', 'NE_SITE_FDN', 'value=null
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSS:((2)):ALL:ebs', '10', 'databaseLookup', 'NE_Identifier', 'NE_SITE_FDN', 'sql=select OSS_ID||'':''||NE_ID, SITE_FDN from DIM_E_CN_SGSN
basetable=DIM_E_CN_SGSN
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSS:((2)):ALL:ebs', '11', 'preappender', 'NE_SITE_FDN', 'NE_SITE_FDN', 'fixed=:
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSS:((2)):ALL:ebs', '12', 'preappender', 'NE_SITE_FDN', 'NE_SITE_FDN', 'field=OSS_ID
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSS:((2)):ALL:ebs', '13', 'fixed', ' ', 'NE_TIMEZONE', 'value=null
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSS:((2)):ALL:ebs', '14', 'databaseLookup', 'NE_SITE_FDN', 'NE_TIMEZONE', 'sql=select OSS_ID||'':''||SITE_FDN, TIMEZONE_VALUE from DIM_E_CN_SITE
basetable=DIM_E_CN_SITE
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSS:((2)):ALL:ebs', '15', 'condition', 'NE_TIMEZONE', 'DC_TIMEZONE', 'factor=null
result1field=DC_TIMEZONE
result2field=NE_TIMEZONE
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSS:((2)):ALL:ebs', '16', 'lookup', 'DATETIME_ID', 'UTC_DATETIME_ID', 'pattern=(\\d{14})
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSS:((2)):ALL:ebs', '17', 'DateFormat', 'UTC_DATETIME_ID', 'UTC_DATETIME_ID', 'oldformat=yyyyMMddHHmmss
newformat=yyyy-MM-dd HH:mm:ss
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSS:((2)):ALL:ebs', '21', 'calculation', 'PERIOD_DURATION', 'PERIOD_DURATION', 'formula=/60
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSS:((2)):ALL:ebs', '18', 'DateFormat', 'UTC_DATETIME_ID', 'DATETIME_ID', 'oldformat=yyyy-MM-dd HH:mm:ss
newformat=yyyy-MM-dd HH:mm:ss
oldtimezone=+0000
newtimezonefield=DC_TIMEZONE
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSS:((2)):ALL:ebs', '19', 'defaulttimehandler', 'DATETIME_ID', ' ', 'oldformat=yyyy-MM-dd HH:mm:ss
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSS:((2)):ALL:ebs', '20', 'databaseLookup', 'NE_Identifier', 'DC_RELEASE', 'sql=select OSS_ID||'':''||NE_ID, NE_VERSION from DIM_E_CN_SGSN
basetable=DIM_E_CN_SGSN
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSS:((2)):ALL:ebs', '22', 'databaseLookup', 'PERIOD_DURATION', 'TIMELEVEL', 'sql=select DURATIONMIN, TIMELEVEL from DIM_TIMELEVEL where TABLELEVEL = ''RAW''
basetable=DIM_TIMELEVEL
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSS:((2)):ALL:ebs', '23', 'copy', 'OSS_ID', 'DC_SOURCE', '');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSS:((2)):ALL:ebs', '24', 'condition', 'DC_SUSPECTFLAG', 'ROWSTATUS', 'factor=TRUE
result1=SUSPECTED
result2field=ROWSTATUS
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSS:((2)):ALL:ebs', '25', 'condition', 'DC_SUSPECTFLAG', 'DC_SUSPECTFLAG', 'factor=TRUE
result1=1
result2field=DC_SUSPECTFLAG
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSS:((2)):ALL:ebs', '26', 'condition', 'DC_SUSPECTFLAG', 'DC_SUSPECTFLAG', 'factor=FALSE
result1=0
result2field=DC_SUSPECTFLAG
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSS:((2)):PM_E_EBSS_APN:ebs', '27', 'lookup', 'MOID', 'moid.APN', 'pattern=APN=(.+)
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSS:((2)):PM_E_EBSS_CELL:ebs', '28', 'lookup', 'MOID', 'moid.MCC', 'pattern=.+=(\\d{3})[\\d|_]\\d{2}\\d{5}\\d{5}
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSS:((2)):PM_E_EBSS_CELL:ebs', '29', 'lookup', 'MOID', 'mnc_group1', 'pattern=.+=\\d{3}([\\d|_]\\d{2})\\d{5}\\d{5}
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSS:((2)):PM_E_EBSS_CELL:ebs', '30', 'lookup', 'mnc_group1', 'mnc_char', 'pattern=(.).*
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSS:((2)):PM_E_EBSS_CELL:ebs', '31', 'lookup', 'mnc_group1', 'mnc_group2', 'pattern=.(.*)
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSS:((2)):PM_E_EBSS_CELL:ebs', '32', 'condition', 'mnc_char', 'moid.MNC', 'factor=_
result1field=mnc_group2
result2field=mnc_group1
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSS:((2)):PM_E_EBSS_CELL:ebs', '33', 'lookup', 'MOID', 'moid.LAC', 'pattern=.+(\\d{5})\\d{5}$
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSS:((2)):PM_E_EBSS_CELL:ebs', '34', 'lookup', 'MOID', 'moid.CI', 'pattern=.+(\\d{5})$
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSS:((2)):PM_E_EBSS_GGSN:ebs', '35', 'lookup', 'MOID', 'moid.GGSN', 'pattern=GGSN=(.+)
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSS:((2)):PM_E_EBSS_HLR:ebs', '36', 'lookup', 'MOID', 'moid.HLR', 'pattern=HLR=(.+)
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSS:((2)):PM_E_EBSS_HNI:ebs', '37', 'lookup', 'MOID', 'moid.HNI', 'pattern=HNI=(.+)
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSS:((2)):PM_E_EBSS_HZI:ebs', '38', 'lookup', 'MOID', 'moid.HZI', 'pattern=HZI=(.+)
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSS:((2)):PM_E_EBSS_RA:ebs', '39', 'lookup', 'MOID', 'moid.MCC', 'pattern=.+=(\\d{3})[\\d|_]\\d{2}\\d{5}\\d{3}
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSS:((2)):PM_E_EBSS_RA:ebs', '40', 'lookup', 'MOID', 'mnc_group1', 'pattern=.+=\\d{3}([\\d|_]\\d{2})\\d{5}\\d{3}
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSS:((2)):PM_E_EBSS_RA:ebs', '41', 'lookup', 'mnc_group1', 'mnc_char', 'pattern=(.).*
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSS:((2)):PM_E_EBSS_RA:ebs', '42', 'lookup', 'mnc_group1', 'mnc_group2', 'pattern=.(.*)
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSS:((2)):PM_E_EBSS_RA:ebs', '43', 'condition', 'mnc_char', 'moid.MNC', 'factor=_
result1field=mnc_group2
result2field=mnc_group1
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSS:((2)):PM_E_EBSS_RA:ebs', '44', 'lookup', 'MOID', 'moid.LAC', 'pattern=.+(\\d{5})\\d{3}$
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSS:((2)):PM_E_EBSS_RA:ebs', '45', 'lookup', 'MOID', 'moid.RAC', 'pattern=.+(\\d{3})$
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSS:((2)):PM_E_EBSS_SA:ebs', '46', 'lookup', 'MOID', 'moid.MCC', 'pattern=.+=(\\d{3})[\\d|_]\\d{2}\\d{5}\\d{5}
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSS:((2)):PM_E_EBSS_SA:ebs', '47', 'lookup', 'MOID', 'mnc_group1', 'pattern=.+=\\d{3}([\\d|_]\\d{2})\\d{5}\\d{5}
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSS:((2)):PM_E_EBSS_SA:ebs', '48', 'lookup', 'mnc_group1', 'mnc_char', 'pattern=(.).*
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSS:((2)):PM_E_EBSS_SA:ebs', '49', 'lookup', 'mnc_group1', 'mnc_group2', 'pattern=.(.*)
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSS:((2)):PM_E_EBSS_SA:ebs', '50', 'condition', 'mnc_char', 'moid.MNC', 'factor=_
result1field=mnc_group2
result2field=mnc_group1
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSS:((2)):PM_E_EBSS_SA:ebs', '51', 'lookup', 'MOID', 'moid.LAC', 'pattern=.+(\\d{5})\\d{5}$
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSS:((2)):PM_E_EBSS_SA:ebs', '52', 'lookup', 'MOID', 'moid.SAC', 'pattern=.+(\\d{5})$
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSS:((2)):PM_E_EBSS_TAC:ebs', '53', 'lookup', 'MOID', 'moid.TAC', 'pattern=TAC=(.+)
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSS:((2)):PM_E_EBSS_TACSVN:ebs', '54', 'lookup', 'MOID', 'moid.TA', 'pattern=.+=(\\d{6}).*
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSS:((2)):PM_E_EBSS_TACSVN:ebs', '55', 'lookup', 'MOID', 'moid.SVN', 'pattern=.+=.+(\\d{2})$
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSW:((2)):ALL:ebs', '20', 'copy', 'SNString', 'SN', '');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSW:((2)):ALL:ebs', '0', 'propertytokenizer', 'MOID', 'MOID', 'delim=,
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSW:((2)):ALL:ebs', '1', 'fixed', ' ', 'DC_TIMEZONE', 'value=TIMEZONE
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSW:((2)):ALL:ebs', '2', 'lookup', 'filename', 'DC_TIMEZONE', 'pattern=A.{13}(.{5})-.+
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSW:((2)):ALL:ebs', '3', 'condition', 'DC_TIMEZONE', 'DC_TIMEZONE', 'factor=TIMEZONE
result1field=JVM_TIMEZONE
result2field=DC_TIMEZONE
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSW:((2)):ALL:ebs', '4', 'lookup', 'DIRNAME', 'OSS_ID', 'pattern=.+/(.+)/.+/.+
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSW:((2)):ALL:ebs', '5', 'lookup', 'filename', 'SN_file', 'pattern=^A.+?_(.+?)_statsfile.+$
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSW:((2)):ALL:ebs', '6', 'condition', 'SN', 'SN', 'result1field=SN_file
result2field=SN
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSW:((2)):ALL:ebs', '7', 'fixed', ' ', 'RemovedString', 'value=_R
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSW:((2)):ALL:ebs', '8', 'lookup', 'SN', 'SubNetwork1', 'pattern=SubNetwork=(.+?),.+
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSW:((2)):ALL:ebs', '9', 'lookup', 'SN', 'SubNetwork2', 'pattern=.+,SubNetwork=(.+?),.+
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSW:((2)):ALL:ebs', '10', 'lookup', 'SN', 'SNEnd', 'pattern=.+?,.+?,(.+)
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSW:((2)):ALL:ebs', '11', 'lookup', 'SubNetwork1', 'SubNetwork1End', 'pattern=.+?(..$)
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSW:((2)):ALL:ebs', '12', 'lookup', 'SubNetwork1', 'SubNetwork1Substr', 'pattern=(.+)..
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSW:((2)):ALL:ebs', '13', 'condition', 'SubNetwork1End', 'SubNetwork1', 'factorfield=RemovedString
result1field=SubNetwork1Substr
result2field=SubNetwork1
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSW:((2)):ALL:ebs', '14', 'fixed', ' ', 'SNString', 'value=SubNetwork=
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSW:((2)):ALL:ebs', '15', 'postappender', 'SNString', 'SNString', 'field=SubNetwork1
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSW:((2)):ALL:ebs', '16', 'postappender', 'SNString', 'SNString', 'fixed=,SubNetwork=
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSW:((2)):ALL:ebs', '17', 'postappender', 'SNString', 'SNString', 'field=SubNetwork2
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSW:((2)):ALL:ebs', '18', 'postappender', 'SNString', 'SNString', 'fixed=,
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSW:((2)):ALL:ebs', '19', 'postappender', 'SNString', 'SNString', 'field=SNEnd
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSW:((2)):ALL:ebs', '21', 'propertytokenizer', 'nedn', 'nedn', 'delim=,
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSW:((2)):ALL:ebs', '22', 'copy', 'SN', 'NE_Identifier', '');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSW:((2)):ALL:ebs', '23', 'preappender', 'NE_Identifier', 'NE_Identifier', 'fixed=:
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSW:((2)):ALL:ebs', '24', 'preappender', 'NE_Identifier', 'NE_Identifier', 'field=OSS_ID
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSW:((2)):ALL:ebs', '25', 'fixed', ' ', 'NE_SITE_FDN', 'value=null
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSW:((2)):ALL:ebs', '26', 'databaseLookup', 'NE_Identifier', 'NE_SITE_FDN', 'sql=select OSS_ID||'':''||RNC_FDN, SITE_FDN from DIM_E_RAN_RNC
basetable=DIM_E_RAN_RNC
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSW:((2)):ALL:ebs', '27', 'preappender', 'NE_SITE_FDN', 'NE_SITE_FDN', 'fixed=:
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSW:((2)):ALL:ebs', '28', 'preappender', 'NE_SITE_FDN', 'NE_SITE_FDN', 'field=OSS_ID
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSW:((2)):ALL:ebs', '29', 'fixed', ' ', 'NE_TIMEZONE', 'value=null');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSW:((2)):ALL:ebs', '30', 'databaseLookup', 'NE_SITE_FDN', 'NE_TIMEZONE', 'sql=select OSS_ID||'':''||SITE_FDN, TIMEZONE_VALUE from DIM_E_RAN_SITE
basetable=DIM_E_RAN_SITE
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSW:((2)):ALL:ebs', '31', 'condition', 'NE_TIMEZONE', 'DC_TIMEZONE', 'factor=null
result1field=DC_TIMEZONE
result2field=NE_TIMEZONE
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSW:((2)):ALL:ebs', '32', 'lookup', 'DATETIME_ID', 'UTC_DATETIME_ID', 'pattern=(\\d{14})
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSW:((2)):ALL:ebs', '33', 'DateFormat', 'UTC_DATETIME_ID', 'UTC_DATETIME_ID', 'oldformat=yyyyMMddHHmmss
newformat=yyyy-MM-dd HH:mm:ss
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSW:((2)):ALL:ebs', '34', 'DateFormat', 'UTC_DATETIME_ID', 'DATETIME_ID', 'oldformat=yyyy-MM-dd HH:mm:ss
newformat=yyyy-MM-dd HH:mm:ss
oldtimezone=+0000
newtimezonefield=DC_TIMEZONE
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSW:((2)):ALL:ebs', '35', 'defaulttimehandler', 'DATETIME_ID', ' ', 'oldformat=yyyy-MM-dd HH:mm:ss
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSW:((2)):ALL:ebs', '36', 'databaseLookup', 'NE_Identifier', 'DC_RELEASE', 'sql=select OSS_ID||'':''||RNC_FDN, RNC_VERSION from DIM_E_RAN_RNC
basetable=DIM_E_RAN_RNC
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSW:((2)):ALL:ebs', '37', 'calculation', 'PERIOD_DURATION', 'PERIOD_DURATION', 'formula=/60
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSW:((2)):ALL:ebs', '38', 'databaseLookup', 'PERIOD_DURATION', 'TIMELEVEL', 'sql=select DURATIONMIN, TIMELEVEL from DIM_TIMELEVEL where TABLELEVEL = ''RAW''
basetable=DIM_TIMELEVEL
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSW:((2)):ALL:ebs', '39', 'copy', 'OSS_ID', 'DC_SOURCE', '');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSW:((2)):ALL:ebs', '40', 'condition', 'DC_SUSPECTFLAG', 'ROWSTATUS', 'factor=TRUE
result1=SUSPECTED
result2field=ROWSTATUS
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSW:((2)):ALL:ebs', '41', 'condition', 'DC_SUSPECTFLAG', 'DC_SUSPECTFLAG', 'factor=TRUE
result1=1
result2field=DC_SUSPECTFLAG
');
insert into Transformation (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('PM_E_EBSW:((2)):ALL:ebs', '42', 'condition', 'DC_SUSPECTFLAG', 'DC_SUSPECTFLAG', 'factor=FALSE
result1=0
result2field=DC_SUSPECTFLAG
');
