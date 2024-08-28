#!/usr/sunos/bin/sh
# ----------------------------------------------------------------------
# Ericsson Network IQ Data deletion script
#
# Usage: Migrate Data from the Raw to newly implemented Count table for any Techpack
#
# Author: ENIQ-Statistic Design Team
#
# ----------------------------------------------------------------------
# Copyright (c) 2015 AB LM Ericsson Oy  All rights reserved.
# ----------------------------------------------------------------------

### Function: usage_msg ###
#
#   Print out the usage message
#
# Arguments:
#	none
# Return Values:
#	none

usage_msg() 
{
echo ""
echo "Usage: data_delete -t <TP_NAME>"
echo "options:"
echo "-t  : Option needed for TP Name"
}

####Global_Variables used########

DC_USER=dc
DWHREP_USER=dwhrep
DC_PASSWORD=dc
DWHREP_PASSWORD=dwhrep
DWH_NAME_dwhdb=dwhdb
DWH_NAME_repdb=repdb
SYBASE_IQ_PATH=/eniq/sybase_iq/OCS-15_0/bin/iqisql
log_path=/eniq/sw/installer
file_creation=/eniq/sw/installer
NAWK=/usr/bin/nawk


######## Main Execution ##########

if [ $# != 2 ]
then
	usage_msg
        exit
fi

if [ $1 != "-t" ]
then
	usage_msg
        exit
fi
tpName=$2

${SYBASE_IQ_PATH} -U${DWHREP_USER} -P${DWHREP_USER} -S${DWH_NAME_repdb} -b << EOISQL > output.txt
select * into temp from DWHPARTITION where tablename like '${tpName}%'
go
unload table temp to '${file_creation}/truncate_techpack.txt' QUOTES OFF
go
drop table temp
go
EOISQL

cd ${file_creation}

$NAWK -F,  '{print "truncate table " $2}' ${file_creation}/truncate_techpack.txt > ${file_creation}/result.sql

echo "go" >> ${file_creation}/result.sql

echo "Please wait sql queries are running..."
${SYBASE_IQ_PATH}  -P${DC_USER} -U${DC_USER} -S${DWH_NAME_dwhdb} -i ${file_creation}/result.sql -o ${file_creation}/query.output

echo "SQL Statements executed successfully"
#rm ${file_creation}/dwhpartition_data.txt
#rm ${file_creation}/result.sql
rm ${file_creation}/query.sql > /dev/null 2>&1
rm ${file_creation}/vivek.txt > /dev/null 2>&1
#rm ${file_creation}/query.output
