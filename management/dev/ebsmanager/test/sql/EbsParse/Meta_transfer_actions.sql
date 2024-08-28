insert into Meta_transfer_actions (VERSION_NUMBER, TRANSFER_ACTION_ID, COLLECTION_ID, COLLECTION_SET_ID, ACTION_TYPE, TRANSFER_ACTION_NAME, ORDER_BY_NO, WHERE_CLAUSE_01, ACTION_CONTENTS_01, ENABLED_FLAG, CONNECTION_ID, WHERE_CLAUSE_02, WHERE_CLAUSE_03, ACTION_CONTENTS_02, ACTION_CONTENTS_03) values ('((2))', '15226', '3004', '96', 'EBSUpdate', 'Update', '0', 'typeName=EBSUpdate', '', 'Y', '0', '', '', '', '');
insert into Meta_transfer_actions (VERSION_NUMBER, TRANSFER_ACTION_ID, COLLECTION_ID, COLLECTION_SET_ID, ACTION_TYPE, TRANSFER_ACTION_NAME, ORDER_BY_NO, WHERE_CLAUSE_01, ACTION_CONTENTS_01, ENABLED_FLAG, CONNECTION_ID, WHERE_CLAUSE_02, WHERE_CLAUSE_03, ACTION_CONTENTS_02, ACTION_CONTENTS_03) values ('((3))', '15164', '2990', '94', 'EBSUpdate', 'Update', '0', 'typeName=EBSUpdate', '', 'Y', '0', '', '', '', '');
insert into Meta_transfer_actions (VERSION_NUMBER, TRANSFER_ACTION_ID, COLLECTION_ID, COLLECTION_SET_ID, ACTION_TYPE, TRANSFER_ACTION_NAME, ORDER_BY_NO, DESCRIPTION, WHERE_CLAUSE_01, ACTION_CONTENTS_01, ENABLED_FLAG, CONNECTION_ID, WHERE_CLAUSE_02, WHERE_CLAUSE_03, ACTION_CONTENTS_02, ACTION_CONTENTS_03) values ('((101))', '15219', '3003', '95', 'Parse', 'ebs', '1', '', '', '#
#Fri Aug 14 13:52:05 EEST 2009
outDir=${ETLDATA_DIR}/adapter_tmp/eba_ebsg/
maxFilesPerRun=5000
dublicateCheck=true
thresholdMethod=more
x3GPPParser.FillEmptyMOID=false
inDir=${PMDATA_DIR}/eniq_oss_1/eba_ebsg/
interfaceName=INTF_PM_E_EBSG
ProcessedFiles.fileNameFormat=A(.{13}).+
x3GPPParser.FillEmptyMOIDValue=0
minFileAge=1
x3GPPParser.FillEmptyMOIDStyle=inc
baseDir=${ARCHIVE_DIR}/eniq_oss_1/eba_ebsg/
useZip=gzip
archivePeriod=168
loaderDir=${ETLDATA_DIR}
parserType=ebs
doubleCheckAction=delete
x3GPPParser.vendorIDMask=.+,(.+)\=.+
ProcessedFiles.processedDir=${ARCHIVE_DIR}/eniq_oss_1/eba_ebsg/processed/
failedAction=move
x3GPPParser.readVendorIDFrom=data
dirThreshold=0
workers=3
afterParseAction=delete
', 'N', '2', '', '', '', '');
insert into Meta_transfer_actions (VERSION_NUMBER, TRANSFER_ACTION_ID, COLLECTION_ID, COLLECTION_SET_ID, ACTION_TYPE, TRANSFER_ACTION_NAME, ORDER_BY_NO, WHERE_CLAUSE_01, ACTION_CONTENTS_01, ENABLED_FLAG, CONNECTION_ID, WHERE_CLAUSE_02, WHERE_CLAUSE_03, ACTION_CONTENTS_02, ACTION_CONTENTS_03) values ('((101))', '15383', '3032', '97', 'Parse', 'ebs', '1', '', '#
#Tue Mar 03 10:48:37 EET 2009
outDir=${ETLDATA_DIR}/adapter_tmp/eba_ebss/
maxFilesPerRun=5000
dublicateCheck=true
thresholdMethod=more
x3GPPParser.FillEmptyMOID=true
inDir=${PMDATA_DIR}/eniq_oss_1/eba_ebss/
interfaceName=INTF_PM_E_EBSS
ProcessedFiles.fileNameFormat=A(.{13}).+
x3GPPParser.FillEmptyMOIDValue=SGSN\=X
minFileAge=1
x3GPPParser.FillEmptyMOIDStyle=static
baseDir=${ARCHIVE_DIR}/eniq_oss_1/eba_ebss/
useZip=gzip
archivePeriod=168
loaderDir=${ETLDATA_DIR}
parserType=ebs
doubleCheckAction=delete
x3GPPParser.vendorIDMask=(.+)\=.+
ProcessedFiles.processedDir=${ARCHIVE_DIR}/eniq_oss_1/eba_ebss/processed/
failedAction=move
x3GPPParser.readVendorIDFrom=data
dirThreshold=0
workers=3
afterParseAction=delete
', 'N', '2', '', '', '', '');
insert into Meta_transfer_actions (VERSION_NUMBER, TRANSFER_ACTION_ID, COLLECTION_ID, COLLECTION_SET_ID, ACTION_TYPE, TRANSFER_ACTION_NAME, ORDER_BY_NO, WHERE_CLAUSE_01, ACTION_CONTENTS_01, ENABLED_FLAG, CONNECTION_ID, WHERE_CLAUSE_02, WHERE_CLAUSE_03, ACTION_CONTENTS_02, ACTION_CONTENTS_03) values ('((2))', '15406', '3037', '98', 'EBSUpdate', 'Update', '0', 'typeName=EBSUpdate', '', 'Y', '0', '', '', '', '');
insert into Meta_transfer_actions (VERSION_NUMBER, TRANSFER_ACTION_ID, COLLECTION_ID, COLLECTION_SET_ID, ACTION_TYPE, TRANSFER_ACTION_NAME, ORDER_BY_NO, WHERE_CLAUSE_01, ACTION_CONTENTS_01, ENABLED_FLAG, CONNECTION_ID, WHERE_CLAUSE_02, WHERE_CLAUSE_03, ACTION_CONTENTS_02, ACTION_CONTENTS_03) values ('((101))', '15444', '3048', '99', 'Parse', 'ebs', '1', '', '#
#Fri Aug 14 13:56:06 EEST 2009
outDir=${ETLDATA_DIR}/adapter_tmp/eba_ebsw/
maxFilesPerRun=5000
dublicateCheck=true
thresholdMethod=more
x3GPPParser.FillEmptyMOID=false
inDir=${PMDATA_DIR}/eniq_oss_1/eba_ebsw/
interfaceName=INTF_PM_E_EBSW
ProcessedFiles.fileNameFormat=A(.{13}).+
x3GPPParser.FillEmptyMOIDValue=0
minFileAge=1
x3GPPParser.FillEmptyMOIDStyle=inc
baseDir=${ARCHIVE_DIR}/eniq_oss_1/eba_ebsw/
useZip=gzip
archivePeriod=168
loaderDir=${ETLDATA_DIR}
parserType=ebs
doubleCheckAction=delete
x3GPPParser.vendorIDMask=.+,(.+)\=.+
ProcessedFiles.processedDir=${ARCHIVE_DIR}/eniq_oss_1/eba_ebsw/processed/
failedAction=move
x3GPPParser.readVendorIDFrom=data
dirThreshold=0
workers=3
afterParseAction=delete
', 'N', '2', '', '', '', '');
insert into Meta_transfer_actions (VERSION_NUMBER, TRANSFER_ACTION_ID, COLLECTION_ID, COLLECTION_SET_ID, ACTION_TYPE, TRANSFER_ACTION_NAME, ORDER_BY_NO, WHERE_CLAUSE_01, ACTION_CONTENTS_01, ENABLED_FLAG, CONNECTION_ID, WHERE_CLAUSE_02, WHERE_CLAUSE_03, ACTION_CONTENTS_02, ACTION_CONTENTS_03) values ('((101))', '21159', '4104', '179', 'Parse', 'ebs', '1', '', '#
#Fri Aug 14 13:56:06 EEST 2009
outDir=${ETLDATA_DIR}/adapter_tmp/eba_ebsw/
maxFilesPerRun=5000
dublicateCheck=true
thresholdMethod=more
x3GPPParser.FillEmptyMOID=false
inDir=${PMDATA_DIR}/eniq_oss_1/eba_ebsw/
interfaceName=INTF_PM_E_EBSW
ProcessedFiles.fileNameFormat=A(.{13}).+
x3GPPParser.FillEmptyMOIDValue=0
minFileAge=1
x3GPPParser.FillEmptyMOIDStyle=inc
baseDir=${ARCHIVE_DIR}/eniq_oss_1/eba_ebsw/
useZip=gzip
archivePeriod=168
loaderDir=${ETLDATA_DIR}
parserType=ebs
doubleCheckAction=delete
x3GPPParser.vendorIDMask=.+,(.+)\=.+
ProcessedFiles.processedDir=${ARCHIVE_DIR}/eniq_oss_1/eba_ebsw/processed/
failedAction=move
x3GPPParser.readVendorIDFrom=data
dirThreshold=0
workers=3
afterParseAction=delete
', 'Y', '2', '', '', '', '');
insert into Meta_transfer_actions (VERSION_NUMBER, TRANSFER_ACTION_ID, COLLECTION_ID, COLLECTION_SET_ID, ACTION_TYPE, TRANSFER_ACTION_NAME, ORDER_BY_NO, DESCRIPTION, WHERE_CLAUSE_01, ACTION_CONTENTS_01, ENABLED_FLAG, CONNECTION_ID, WHERE_CLAUSE_02, WHERE_CLAUSE_03, ACTION_CONTENTS_02, ACTION_CONTENTS_03) values ('((101))', '21520', '4175', '202', 'Parse', 'ebs', '1', '', '', '#
#Fri Aug 14 13:52:05 EEST 2009
outDir=${ETLDATA_DIR}/adapter_tmp/eba_ebsg/
maxFilesPerRun=5000
dublicateCheck=true
thresholdMethod=more
x3GPPParser.FillEmptyMOID=false
inDir=${PMDATA_DIR}/eniq_oss_1/eba_ebsg/
interfaceName=INTF_PM_E_EBSG
ProcessedFiles.fileNameFormat=A(.{13}).+
x3GPPParser.FillEmptyMOIDValue=0
minFileAge=1
x3GPPParser.FillEmptyMOIDStyle=inc
baseDir=${ARCHIVE_DIR}/eniq_oss_1/eba_ebsg/
useZip=gzip
archivePeriod=168
loaderDir=${ETLDATA_DIR}
parserType=ebs
doubleCheckAction=delete
x3GPPParser.vendorIDMask=.+,(.+)\=.+
ProcessedFiles.processedDir=${ARCHIVE_DIR}/eniq_oss_1/eba_ebsg/processed/
failedAction=move
x3GPPParser.readVendorIDFrom=data
dirThreshold=0
workers=3
afterParseAction=delete
', 'Y', '2', '', '', '', '');
insert into Meta_transfer_actions (VERSION_NUMBER, TRANSFER_ACTION_ID, COLLECTION_ID, COLLECTION_SET_ID, ACTION_TYPE, TRANSFER_ACTION_NAME, ORDER_BY_NO, WHERE_CLAUSE_01, ACTION_CONTENTS_01, ENABLED_FLAG, CONNECTION_ID, WHERE_CLAUSE_02, WHERE_CLAUSE_03, ACTION_CONTENTS_02, ACTION_CONTENTS_03) values ('((101))', '22113', '4296', '242', 'Parse', 'ebs', '1', '', '#
#Tue Mar 03 10:48:37 EET 2009
outDir=${ETLDATA_DIR}/adapter_tmp/eba_ebss/
maxFilesPerRun=5000
dublicateCheck=true
thresholdMethod=more
x3GPPParser.FillEmptyMOID=true
inDir=${PMDATA_DIR}/eniq_oss_1/eba_ebss/
interfaceName=INTF_PM_E_EBSS
ProcessedFiles.fileNameFormat=A(.{13}).+
x3GPPParser.FillEmptyMOIDValue=SGSN\=X
minFileAge=1
x3GPPParser.FillEmptyMOIDStyle=static
baseDir=${ARCHIVE_DIR}/eniq_oss_1/eba_ebss/
useZip=gzip
archivePeriod=168
loaderDir=${ETLDATA_DIR}
parserType=ebs
doubleCheckAction=delete
x3GPPParser.vendorIDMask=(.+)\=.+
ProcessedFiles.processedDir=${ARCHIVE_DIR}/eniq_oss_1/eba_ebss/processed/
failedAction=move
x3GPPParser.readVendorIDFrom=data
dirThreshold=0
workers=3
afterParseAction=delete
', 'Y', '2', '', '', '', '');
