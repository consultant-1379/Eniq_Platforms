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
# Usage   : backup_glassfish_ssl_https.sh [-c <glassfish-config-dir>] -b <backup-dir>
#

AWK=/usr/bin/awk
CP=/usr/bin/cp
ECHO=/usr/bin/echo
EXPR=/usr/bin/expr
GREP=/usr/bin/grep
JAVA="${RT_DIR}/jdk/bin/java"
MKDIR=/usr/bin/mkdir
RM=/usr/bin/rm


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

USAGE_MSG="Usage: backup_glassfish_ssl_https.sh [-c <glassfish-config-dir>] -b <backup-dir>"
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
		error_exit "\"Backup directory\" ${BACKUP_DIR} is not writable."
	else
		# remove trailing slash if included
		BACKUP_DIR=${BACKUP_DIR%/}
	fi
fi


# create backup ssl subdirectory if it doesn't exist
BACKUP_SSL_DIR="${BACKUP_DIR}/ssl"
if [ ! -d "${BACKUP_SSL_DIR}" ]
then
	log "${BACKUP_SSL_DIR} does not exist. Creating..."
	${MKDIR} ${BACKUP_SSL_DIR}
	if [ ! -d "${BACKUP_SSL_DIR}" ]; then
		error_exit "Couldn't create directory ${BACKUP_SSL_DIR}"
	fi
fi

log "Backing up GlassFish SSL/HTTPS config. From (Config Directory): ${GF_CONFIG_DIR}; To (Backup Directory): ${BACKUP_DIR}"

#####
# Backup keystores and cert files
#####

$CP ${GF_CONFIG_DIR}/*.jks ${BACKUP_DIR}

GF_SSL_DIR="${GF_CONFIG_DIR}/ssl"
GF_SSL_DIR_EXISTS=0
if [ ! -d "${GF_SSL_DIR}" ]
then
	log "${GF_SSL_DIR} does not exist - nothing to back up..."
else
	GF_SSL_DIR_EXISTS=1
	$CP ${GF_CONFIG_DIR}/ssl/* ${BACKUP_SSL_DIR}
fi

log "Copied files. Checking current https status..."

#####
# Check current SSL/HTTPS configuration
#####

${JAVA} -classpath ${CLASSPATH} ${PARSER_CLASS} ${GF_CONFIG_DIR}/domain.xml ${BACKUP_DIR} 2>> ${GF_LOG_FILE} > /dev/null
CHECK_STATUS=`$ECHO $?`

if [ "${CHECK_STATUS}" -ne 0 ]
then
	error_exit "Problem checking GlassFish https/ssl status"
fi

if [ -f "${BACKUP_DIR}/https_setup" ] 
then
	# if GF_SSL_DIR does not exists, then must be using older ssl config
	# create ssl_setup flag file to force new setup
	if [ "$GF_SSL_DIR_EXISTS" -eq 0 ]
	then
		${ECHO} "reset ssl" > ${BACKUP_DIR}/setup_ssl
	else
		# remove ssl_setup file if it exists
		if [ -f "${BACKUP_DIR}/setup_ssl" ] 
		then
			${RM} ${BACKUP_DIR}/setup_ssl
		fi
	fi
	
	log "HTTPS is set up for current install."
	if [ -f "${BACKUP_DIR}/https_enabled" ] 
	then
		log "HTTPS is enabled for current install."
	else
		log "HTTPS is not enabled for current install."
	fi
else
	log "HTTPS is not set up for current install."
fi

log "DONE"