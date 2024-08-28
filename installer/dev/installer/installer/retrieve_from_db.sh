#!/bin/bash
# ********************************************************************
# Ericsson Radio Systems AB                                     SCRIPT
# ********************************************************************
#
# (c) Ericsson Radio Systems AB 2016 - All rights reserved.
#
# The copyright to the computer program(s) herein is the property
# of Ericsson Radio Systems AB, Sweden. The programs may be used
# and/or copied only with the written permission from Ericsson Radio
# Systems AB or in accordance with the terms and conditions stipulated
# in the agreement/contract under which the program(s) have been
# supplied.
#
# ********************************************************************
# Name    : retrieve_from_db.sh
# Date    : 18/05/2016
# Purpose : Script to retrieve the user password for alarm from database
#           
# Usage   : retrieve_from_db.sh
#
# ********************************************************************
#
# Command Section
#
# ********************************************************************


GREP=/usr/bin/grep
NAWK=/usr/bin/nawk
CD=/usr/bin/cd
DBISQL="$(ls /eniq/sybase_iq/IQ-*/bin64/dbisql)"
ECHO=/usr/bin/echo
GREP=/usr/bin/grep
MKDIR=/usr/bin/mkdir
RM=/usr/bin/rm
INSTALLER_DIR=/eniq/sw/installer
CONF_DIR=/eniq/sw/conf
ENIQ_CONFIG_DIR=/eniq/installation/config

ETLREPUsername="$(cat $CONF_DIR/niq.ini | grep ETLREPUsername | $NAWK -F'=' '{print $2} ')"
ETLREPPassword="$(cat $CONF_DIR/niq.ini | grep ETLREPPassword | $NAWK -F'=' '{print $2} ')"

CURR_SERVER_TYPE=`cat $ENIQ_CONFIG_DIR/installed_server_type | grep -v  '^[[:blank:]]*#' | sed -e 's/ //g'`
 if [  "${CURR_SERVER_TYPE}" == "eniq_stats" ] ; then
	
	if [ -s $INSTALLER_DIR/temp_db_result/tmp.txt ]; then
	$ECHO "Alarm password backup already taken"
	exit 0
else
	$MKDIR $INSTALLER_DIR/temp_db_result
	TMP=${INSTALLER_DIR}/temp_db_result
	$DBISQL -nogui -c "eng=repdb;links=tcpip{host=repdb;port=2641};uid=${ETLREPUsername};pwd=${ETLREPPassword}" "select ACTION_CONTENTS_01 from META_TRANSFER_ACTIONS WHERE ACTION_TYPE = 'alarmhandler'"| $GREP password | $NAWK -F'=' 'NR==1 { print $2 } '> $TMP/tmp.txt
	if [ ! -s $INSTALLER_DIR/temp_db_result/tmp.txt ]; then
		$ECHO "No alarm password found in the database..exiting script"
		$RM -rf $INSTALLER_DIR/temp_db_result
		exit 0
	fi
fi
	
else
	$ECHO "Not stats server exiting retrieve_from_db.sh..."
	exit 0
fi
	




