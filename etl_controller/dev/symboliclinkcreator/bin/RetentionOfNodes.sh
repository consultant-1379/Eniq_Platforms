#!/bin/bash
# ********************************************************************
# Ericsson Radio Systems AB                                     SCRIPT
# ********************************************************************
#
# (c) Ericsson Radio Systems AB 2015 - All rights reserved.
#
# The copyright to the computer program(s) herein is the property
# of Ericsson Radio Systems AB, Sweden. The programs may be used
# and/or copied only with the written permission from Ericsson Radio
# Systems AB or in accordance with the terms and conditions stipulated
# in the agreement/contract under which the program(s) have been
# supplied.
#
# ********************************************************************
# Name    : RetentionOfNodes.sh
# Date    : 10/11/2016
# Purpose : Adding the fdns present in the ENIQ-S before mounting  
# Usage   : RetentionOfNodes.sh
#
# ********************************************************************
#
# Command Section
#
# ********************************************************************

GREP=/usr/bin/grep
AWK=/usr/bin/awk
NAWK=/usr/bin/nawk
DBISQL="$(ls /eniq/sybase_iq/IQ-*/bin64/dbisql)"
ECHO=/usr/bin/echo
MKDIR=/usr/bin/mkdir
RM=/usr/bin/rm
CAT=/usr/bin/cat
CHMOD=/usr/bin/chmod
CHOWN=/usr/bin/chown
DATE=/usr/bin/date
SED=/usr/bin/sed
SU=/usr/bin/su
MV=/usr/bin/mv
TEE=/usr/bin/tee

STARTTIMESTAMP=`$DATE '+%y%m%d_%H%M%S'`
LOG_FILE=/eniq/log/sw_log/symboliclinkcreator/RetentionOfNodes_${STARTTIMESTAMP}.log
touch ${LOG_FILE}
INSTALLER_DIR=/eniq/sw/installer
TEMP=${INSTALLER_DIR}/temp_nat

if [ ! -d ${TEMP} ] ; then
  $MKDIR -p ${TEMP}
  $CHMOD 777 ${TEMP}
else
  $RM -rf ${TEMP}
  $MKDIR -p ${TEMP}
  $CHMOD 777 ${TEMP}
fi 

if [ -s /eniq/admin/lib/common_functions.lib ]; then
    . /eniq/admin/lib/common_functions.lib
else
        $ECHO "Could not find /eniq/admin/lib/common_functions.lib"
        exit 1
fi

DWHDBPASSWORD=`inigetpassword DWH -v DCPassword -f ${CONF_DIR}/niq.ini`
DWHDB_PORT=`inigetpassword DWH -v PortNumber -f ${CONF_DIR}/niq.ini`
DWH_SERVER_NAME=`inigetpassword DWH -v ServerName -f ${CONF_DIR}/niq.ini`

DWHREPUSER=`inigetpassword REP -v DWHREPUsername -f ${CONF_DIR}/niq.ini`
DWHREPPASSWORD=`inigetpassword REP -v DWHREPPassword -f ${CONF_DIR}/niq.ini`
REP_PORT=`inigetpassword REP -v PortNumber -f ${CONF_DIR}/niq.ini`
REP_SERVER_NAME=`inigetpassword REP -v ServerName -f ${CONF_DIR}/niq.ini`
DBAPASSWORD=`inigetpassword DB -v UtilDBAPASSWORD -f ${CONF_DIR}/niq.ini`

if [ ! -x "$DBISQL" ]; then
    _err_msg_="$DBISQL commands not found or not executable."
    abort_script "$_err_msg_"
fi

### Function: retaining_ExistingNodes ###
#
#   Retain the existing nodes from the DIM tables
#
# Arguments:
#      $1- Topology Table
#      $2- Column which contains the FDN 
#      $3- Column which contains the NETYPE 
#      $4- Eniq_identifier
# Return Values:
#       none
#*************** Retain the existing nodes from the DIM tables **************************************
retaining_ExistingNodes(){
	topologyTable=$1
	fdnColumn=$2
	neType=$3
	eniqIdentifier=$4
	$ECHO "Retaining the existing nodes for the node $1  $2  $3" | $TEE -a ${LOG_FILE}
	$DBISQL -nogui -onerror exit -c "eng=${DWH_SERVER_NAME};links=tcpip{host=${DWH_SERVER_NAME};port=${DWHDB_PORT}};uid=dc;pwd=${DWHDBPASSWORD}" "select distinct $2,$3 from $1;OUTPUT TO ${TEMP}/tmp.txt DELIMITED BY '|'"   > /dev/null 2>&1
	$DBISQL -nogui -onerror exit -c "eng=${DWH_SERVER_NAME};links=tcpip{host=${DWH_SERVER_NAME};port=${DWHDB_PORT}};uid=dc;pwd=${DWHDBPASSWORD}" "select distinct $2,$3 from $1;OUTPUT TO ${TEMP}/NodeFDN.txt DELIMITED BY '|' APPEND "   > /dev/null 2>&1
	$SED "s/'//g" ${TEMP}/NodeFDN.txt >> ${TEMP}/NodeFDNtemp.txt
	$MV ${TEMP}/NodeFDNtemp.txt ${TEMP}/NodeFDN.txt
	
    if [[ $? -eq 0 ]]; then
	if [[ -s ${TEMP}/tmp.txt ]]; then
		$ECHO "Successfully Retrieved nodes from the topology table $1!!!"  | $TEE -a ${LOG_FILE}
	else
		$ECHO "Topology table $1 is empty"| $TEE -a ${LOG_FILE}
	fi
	else
		$ECHO "Failed while retrieving nodes from the topology table $1!!!"   | $TEE -a ${LOG_FILE}
	fi

}
### Function: topology_active ###
#
#   Check whether the topology table is active
#
# Arguments:
#      $1- Topology Name
# Return Values:
#       Active or Inactive
topology_active(){
# Is parameter #1 zero length?
if [ -z "$1" ]                           
then
	$ECHO "There is no topology to check" >> ${LOG_FILE}
else															 # Or no parameter passed.
	$ECHO "Topology  \"$1\" is checked for active or not..." >> ${LOG_FILE}
	$DBISQL -nogui -c "eng=${REP_SERVER_NAME};links=tcpip{host=${REP_SERVER_NAME};port=${REP_PORT}};uid=$DWHREPUSER;pwd=$DWHREPPASSWORD" "select status from tpActivation where TYPE='topology' and TECHPACK_NAME='$1';OUTPUT TO ${TEMP}/tempText.txt" > /dev/null 2>&1
	resultSet=$(head -n 1 ${TEMP}/tempText.txt)
	$ECHO "$resultSet" 
fi
}

### Function: insert_NAT ###
#
#   copy the fdn and ne_type to the NAT table   
#
# Arguments:
#      none
# Return Values:
#       none
insert_NAT(){
textfile=${TEMP}/RetentionOfNodes.txt
if [[ -f ${textfile}  && -s ${textfile} ]] ; then
	$ECHO "Input file is present with some data to be added in NAT" >> ${LOG_FILE}
	$DBISQL -nogui -onerror exit -c "eng=${REP_SERVER_NAME};links=tcpip{host=${REP_SERVER_NAME};port=${REP_PORT}};uid=$DWHREPUSER;pwd=$DWHREPPASSWORD" "LOAD TABLE ENIQS_Node_Assignment (ENIQ_IDENTIFIER , FDN, NETYPE) FROM '$textfile' QUOTES OFF DELIMITED BY '|';"  > /dev/null 2>&1
	if [[ $? -eq 0 ]]; then
		$ECHO "Successfully inserted into NAT table!!!"  | $TEE -a ${LOG_FILE}
	else
		$ECHO "Failed while inserting into NAT table!!!" | $TEE -a ${LOG_FILE}
		$ECHO "May be Duplicate Fdn's present in FdnList" | $TEE -a ${LOG_FILE}
	fi
else
	$ECHO "Input file for inserting the node_fdn and ne_type is missing or data is not present"| $TEE -a ${LOG_FILE}
fi
}

### Function: check_fdn ###
#
#   Check if both ManagedElement and MeContext is present in the fdn   
#
# Arguments:
#       $1 - FDN which contains ManagedElement
# Return Values:
#       Correct FDN with either Mecontext or ManangedElement
check_fdn(){
	$ECHO "FDN read from the Input file $1" >> ${LOG_FILE}
	if [[ ! -z $1 ]] ; then
		first_matching_fdn=`expr match "$1"  '\(.*,MeContext=[^,]*\).*' `
		if [ "$first_matching_fdn" = "" ] ; then
			second_matching_fdn=`expr match "$1"  '\(.*,ManagedElement=[^,]*\).*' `
			if [ "$second_matching_fdn" = "" ] ;  then
				$ECHO "fdn is $1" >> ${LOG_FILE}
				modified_fdn=$1
				$ECHO $modified_fdn
			else
				$ECHO "fdn is $second_matching_fdn" >> ${LOG_FILE}
				modified_fdn=$second_matching_fdn
				$ECHO $modified_fdn
			fi
		else
			$ECHO "fdn is $first_matching_fdn" >> ${LOG_FILE}
			modified_fdn=$first_matching_fdn
			$ECHO $modified_fdn
		fi
	else 
			$ECHO  $1
	fi
}

#****************************************** Main Body of the Script *********************************************
$ECHO "Execution started at " $STARTTIMESTAMP >> ${LOG_FILE}
conf_File="/eniq/sw/conf/Topologytables.txt"
#Get the Eniq_identifire from the servicenames:
eniqIdentifier=`$CAT /eniq/sw/conf/service_names | $GREP engine | $NAWK -F'::' '{print $3}'`
$ECHO "eniqIdentifier is identified as  $eniqIdentifier" | $TEE -a ${LOG_FILE}
while IFS= read -r line
do
	$ECHO "For $line" >> ${LOG_FILE}
    arr=($line)
	topology=${arr[0]}
	topologyTable=${arr[1]}
	fdnColumn=${arr[2]}
	neType=${arr[3]}
	resultSet=$(topology_active $topology) 
	if [ "${resultSet}" == "'ACTIVE'" ]; then
		$ECHO "$topology is active in the server. Hence retaining the nodes."  | $TEE -a ${LOG_FILE}
		retaining_ExistingNodes $topologyTable $fdnColumn $neType $eniqIdentifier
	else
		$ECHO "$topology is not active in the server.Hence retention of nodes is skipped for this $topology" | $TEE -a ${LOG_FILE}		
	fi
done < $conf_File
$SED "s/^/$eniqIdentifier|/g" ${TEMP}/NodeFDN.txt >> ${TEMP}/NodeFDNtemp.txt
manipulate_fdn="${TEMP}/NodeFDNtemp.txt"
while IFS= read -r line
do
	temp_identifire=`$ECHO $line | $NAWK -F'|' '{print $1}'` 
	temp_neTtpe=`$ECHO $line |  $NAWK -F'|' '{print $3}'`
	temp_tempFDN=`$ECHO $line |  $NAWK -F'|' '{print $2}'`
	temp_fdn=`$ECHO $line |  $NAWK -F'|' '{print $2}' | grep -i "ManagedElement"`
	if [[ ! -z ${temp_fdn} ]] ; then
		actual_fdn=$(check_fdn $temp_fdn)
		if [[ ! -z ${actual_fdn} ]] ; then
			$ECHO "$temp_identifire|$actual_fdn|$temp_neTtpe" >> ${TEMP}/RetentionOfNodes.txt
		else 
			$ECHO "fdn $temp_tempFDN is not Proper... Please check it " | $TEE -a ${LOG_FILE}
		fi
	else
		$ECHO "$temp_identifire|$temp_tempFDN|$temp_neTtpe" >> ${TEMP}/RetentionOfNodes.txt
		$ECHO "FDN read from the Input file $temp_tempFDN" >> ${LOG_FILE}
		$ECHO "fdn is $temp_tempFDN" >> ${LOG_FILE}
	fi
done < $manipulate_fdn


#insert into NAT table:
insert_NAT 

ENDTIMESTAMP=`$DATE '+%y%m%d_%H%M%S'`
$ECHO "Execution ended at " $ENDTIMESTAMP >> ${LOG_FILE}

##cleanup
rm -rf ${TEMP}