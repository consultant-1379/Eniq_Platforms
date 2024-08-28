#!/bin/bash
# ********************************************************************
# Ericsson Radio Systems AB                                     SCRIPT
# ********************************************************************
#
#
# (c) Ericsson Radio Systems AB 2013 - All rights reserved.
#
# The copyright to the computer program(s) herein is the property
# of Ericsson Radio Systems AB, Sweden. The programs may be used
# and/or copied only with the written permission from Ericsson Radio
# Systems AB or in accordance with the terms and conditions stipulated
# in the agreement/contract under which the program(s) have been
# supplied.
#
# ********************************************************************
# Name    : backup_glassfish_ssl_https.sh
# Date    : 15/02/2013
# Revision: A01
# Purpose : Script to backup GlassFish's ssl & https configuration
#
# Usage   : restore_glassfish_ssl_https.sh [-c <glassfish-config-dir>] -b <backup-dir>
#

AWK=/usr/bin/awk
CP=/usr/bin/cp
ECHO=/usr/bin/echo
EXPR=/usr/bin/expr
GREP=/usr/bin/grep
JAVA="${RT_DIR}/jdk/bin/java"
LS=/usr/bin/ls
MKDIR=/usr/bin/mkdir

#######################
# SUPPORTING FUNCTIONS
#######################

log(){
	mess=$1
	dTime=`date +'%m/%d/%Y %H:%M:%S'`
	term=`who am i | $AWK  -F' ' '{print $2}'`
	$ECHO "$dTime :: $term :: $mess " >> ${GF_LOG_FILE}
	$ECHO "$mess"
}

error_exit(){
	errStr=$1
	dTime=`date +'%m/%d/%Y %H:%M:%S'`
	term=`who am i | $AWK  -F' ' '{print $2}'`
	$ECHO "$dTime :: $term :: Error: $errStr " >> ${GF_LOG_FILE}
	$ECHO "Error: $errStr !!!!Exiting script.... "
	exit 2
}

#######################
# Processing starts here
#######################

#####
# Global Variables
#####

USAGE_MSG="Usage: restore_glassfish_ssl_https.sh [-c <glassfish-config-dir>] -b <backup-dir>"
GF_LOG_FILE='/eniq/log/sw_log/glassfish/security.log'
GF_DOMAIN_HOME="/eniq/glassfish/glassfish3/glassfish"
CLASSPATH="${GF_DOMAIN_HOME}/bin/glassfish_config.jar"
PARSER_CLASS="com.ericsson.eniq.glassfish.DomainParserHTTPS"
GF_CONFIG_DIR="${GF_DOMAIN_HOME}/domains/domain1/config"
BACKUP_DIR=""



#####
# Check User. Only dcuser should be allowed to run the script
#####
isDCUSER=`id | $AWK -F' ' '{print $1}' | $GREP -i dcuser | wc -l`
isDCUSER=`$EXPR $isDCUSER + 0`
if [ $isDCUSER -ne 1 ]
then
	$ECHO " This script can be run only as dcuser. "
	exit 5
fi

#####
# Check Arguments
#####
if [ $# -ne 2 -a $# -ne 4 ]
then
        $ECHO ${USAGE_MSG}
        exit 4
fi

while getopts ":c:b:" OPTION
do
  case ${OPTION} in
    c) GF_CONFIG_DIR="$OPTARG"
       ;;
    b) BACKUP_DIR="$OPTARG"
       ;;
    c) DOMAIN_NAME="$OPTARG"
       ;;
   \?) $ECHO ${USAGE_MSG}
       exit 29
       ;;
  esac
done

$ECHO "Logging to: ${GF_LOG_FILE}"

#####
# Check Directories
#####

if [ ! -d "${GF_CONFIG_DIR}" ]
then
	error_exit "\"GlassFish config directory\" ${GF_CONFIG_DIR} does not exist."
else
	# remove trailing slash if included
	GF_CONFIG_DIR=${GF_CONFIG_DIR%/}
fi

if [ ! -d "${BACKUP_DIR}" ]
then
	error_exit "\"Backup directory\" ${BACKUP_DIR} does not exist."
else
	if [ ! -w "${BACKUP_DIR}" ]
	then
		# remove trailing slash if included
		error_exit "\"Backup directory\" ${BACKUP_DIR} is not writable."
	else
		BACKUP_DIR=${BACKUP_DIR%/}
	fi
fi

BACKUP_SSL_DIR="${BACKUP_DIR}/ssl"
if [ ! -d "${BACKUP_SSL_DIR}" ]
then
	error_exit "\"Backup ssl directory\" ${BACKUP_SSL_DIR} does not exist."
else
	if [ ! -w "${BACKUP_SSL_DIR}" ]
	then
		# remove trailing slash if included
		error_exit "\"Backup ssl directory\" ${BACKUP_SSL_DIR} is not writable."
	else
		BACKUP_SSL_DIR=${BACKUP_SSL_DIR%/}
	fi
fi

# create ssl subdirectory if it doesn't exist
GF_SSL_DIR="${GF_CONFIG_DIR}/ssl"
if [ ! -d "${GF_SSL_DIR}" ]
then
	log "${GF_SSL_DIR} does not exist. Creating..."
	${MKDIR} ${GF_SSL_DIR}
	if [ ! -d "${GF_SSL_DIR}" ]; then
		error_exit "Couldn't create directory ${GF_SSL_DIR}"
	fi
fi


if $LS ${BACKUP_DIR}/*.jks &> /dev/null
then
	log "Keystores exist in backup directory ${BACKUP_DIR}. Restoring now."
else
	error_exit "No keystores (*.jks) found in backup directory ${BACKUP_DIR}"
fi

log "Restoring GlassFish SSL/HTTPS config. From (Backup Directory): ${BACKUP_DIR}; To (Config Directory): ${GF_CONFIG_DIR}"

#####
# Restore keystores and cert files
#####

if $CP ${BACKUP_DIR}/*.jks ${GF_CONFIG_DIR}
then
	log "Restored keystore (*.jks) files"
else
	error_exit "Problem restoring keystores (*.jks) files."
fi

if $CP ${BACKUP_SSL_DIR}/* ${GF_SSL_DIR}
then
	log "Restored ssl (${GF_SSL_DIR}/*) files"
fi

log "DONE"