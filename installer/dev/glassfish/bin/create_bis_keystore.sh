#!/usr/bin/bash
# -------------------------------------------------------------------
# Ericsson Network IQ ETLC engine control script
#
# Usage : create_bis_keystore.sh bisname
#
# -------------------------------------------------------------------
# Copyright (c) 1999 - 2016 AB Ericsson Oy All rights reserved.
# --------------------------------------------------------------------

AWK=/usr/bin/awk
CP=/usr/bin/cp
ECHO=/usr/bin/echo
EXPR=/usr/bin/expr
GREP=/usr/bin/grep
HOST=/usr/sbin/host
MKDIR=/usr/bin/mkdir
RM=/usr/bin/rm
SED=/usr/bin/sed
TR=/usr/bin/tr
WC=/usr/bin/wc

#######################
# SUPPORTING FUNCTIONS
#######################

check_file_presence(){
	IS_FILE_PRESENT=0
	fileName=$1
	if [ -r $fileName ]
	then
		IS_FILE_PRESENT=`$EXPR $IS_FILE_PRESENT + 1`
	else
		IS_FILE_PRESENT=`$EXPR $IS_FILE_PRESENT + 0`
	fi
}

remove_file()
{
	if [ -f $1 ]
	then
		$RM -f $1 2>&1 > /dev/null
	fi
}


remove_lock_file(){
	remove_file $LOCK_FILE
}

log(){
	log_msg -s "$1" -l $GF_LOG_FILE -t
}

error_exit(){
	log_msg -s "Error: $1" -l $GF_LOG_FILE -t

	remove_lock_file
	exit 2
}

copy_and_del_temp_file(){
	$CP $1 $2
	if [ -f $1 ]
	then
		$RM -rf $1 2>&1 > /dev/null
	fi
	log "Updated $2"
}

take_backup(){
	$CP $1 $1.backup 2>&1 > /dev/null
	status_02=`$ECHO $?`
	if [ $status_02 -ne 0 ]
	then
		log "Error: : Failed to take the backup. $1 ==> $1.backup"
		error_exit "Failed to take the backup.   $1 ==> $1.backup"
	fi
}

revert_back(){
	if [ -f $1.backup ]
	then
		$CP $1.backup $1 2>&1 >> $GF_LOG_FILE
		status_02=`$ECHO $?`
	fi
	if [ $status_02 -eq 0 ]
    then
		log "Reverting back original files. $1.backup ==> $1"
	else
		log "Error : Failed to revert back the original files. $1.backup ==> $1"
	fi
}

#######################
# MAIN WORKER FUNCTIONS
#######################

#Get Password from User
get_password(){
	read -sp "Enter the KEYSTORE password: `$ECHO $'\n> '`" password
	if [ "$password" == "" ]
	then
		$ECHO "\nPassword cannot be empty. Please re-enter the password."
		get_password
	fi		
	read -sp "`$ECHO $'\n> '`Confirm the password: `$ECHO $'\n> '`" confirm_password
	if [ "$password" != "$confirm_password" ]
	then
		$ECHO "\nPassword does not match. Please re-enter the password."
		get_password
	fi
	read -p "`$ECHO $'\n> '`Are you sure you want to continue(Yy/Nn): `$ECHO $'\n> '`" input
	if [ "$input" == "y" -o "$input" == "Y" ]
    	then
		KEYSTORE_PWD="$password"
	elif [ "$input" == "n" -o "$input" == "N" ]
        then
		$ECHO Exiting from the script.
		exit 0
	else
		$ECHO Invalid option.
		$ECHO Execute the script again.
		exit 0
	fi
}

get_bis_details() {
	log "NAME: ${BIS_NAME}"

	BIS_COMMON_NAME=`${ECHO} ${BIS_NAME} | ${TR} '[a-z]' '[A-Z]'`
	status=`$ECHO $?`
	if [ $status -ne 0 -o "${BIS_COMMON_NAME}" = "" ]
	then
		error_exit "Problem building BIS common name"
	fi
	
	BIS_HOST_DETAILS=`${HOST} ${BIS_NAME}`
	status=`$ECHO $?`
	if [ $status -ne 0 -o "${BIS_HOST_DETAILS}" = "" ]
	then
		error_exit "Problem getting BIS Host details."
	fi
	
	BIS_FQDN=`${ECHO} ${BIS_HOST_DETAILS} | ${AWK} '{print  $1}'`
	status=`$ECHO $?`
	if [ $status -ne 0 -o "${BIS_FQDN}" = "" ]
	then
		error_exit "Problem getting BIS FQDN"
	fi

	BIS_IP_ADDRESS=`${ECHO} ${BIS_HOST_DETAILS} | ${AWK} '{print  $4}'`
	status=`$ECHO $?`
	if [ $status -ne 0 -o "${BIS_IP_ADDRESS}" = "" ]
	then
		error_exit "Problem getting BIS IP ADDRESS"
	fi

	log "COMMON NAME: ${BIS_COMMON_NAME}"
	log "FQDN: ${BIS_FQDN}"
	log "IP: ${BIS_IP_ADDRESS}"
}

# check working directory exists and clean up any existing files
prepare_directory() {
	if [ ! -d "${BIS_SSL_DIR}" ]; then 
		log "${BIS_SSL_DIR} does not exist. Creating..."
		${MKDIR} ${BIS_SSL_DIR}
		if [ ! -d "${BIS_SSL_DIR}" ]; then
			error_exit "Couldn't create directory ${BIS_SSL_DIR}"
		fi
	fi

	remove_file ${BIS_OPENSSL_CONF}
	remove_file ${BIS_KEY}
	remove_file ${BIS_CSR}
	remove_file ${BIS_CRT}
	remove_file ${BIS_P12}
	remove_file ${BIS_KEYSTORE}
	remove_file ${BIS_FINAL_CERT}
}

create_config_file() {
	log "Creating config file: ${BIS_OPENSSL_CONF}"
	${CP} ${BIS_OPENSSL_CONF_TEMPLATE} ${BIS_OPENSSL_CONF}
	iniset req_distinguished_name -f ${BIS_OPENSSL_CONF} commonName="${BIS_COMMON_NAME}"
	status=`$ECHO $?`
	if [ $status -ne 0 ]
	then
		error_exit "Problem setting commonName in ${BIS_OPENSSL_CONF}"
	fi

	iniset alt_names -f ${BIS_OPENSSL_CONF} DNS.1="${BIS_FQDN}" DNS.2="${BIS_IP_ADDRESS}" IP.1="${BIS_IP_ADDRESS}"
	status=`$ECHO $?`
	if [ $status -ne 0 ]
	then
		error_exit "Problem setting alt_names in ${BIS_OPENSSL_CONF}"
	fi
}

create_bis_keystore() {
	prepare_directory
	create_config_file
	
	log "Creating cert & keystore"

	
	# generate private key
	${OPEN_SSL} genrsa -out ${BIS_KEY} 2048 -config ${BIS_OPENSSL_CONF}
	status=`$ECHO $?`
	if [ $status -ne 0 -o ! -r "${BIS_KEY}" ]
	then
		error_exit "Problem creating key file: ${BIS_KEY}"
	else
		log "Created key file: ${BIS_KEY}"
	fi

	# generate csr
	${OPEN_SSL} req -new -key ${BIS_KEY} -out ${BIS_CSR} -config ${BIS_OPENSSL_CONF}
	status=`$ECHO $?`
	if [ $status -ne 0 -o ! -r "${BIS_CSR}" ]
	then
		error_exit "Problem creating csr file: ${BIS_CSR}"
	else
		log "Created csr file: ${BIS_CSR}"
	fi

	# self-sign and create cert
	${OPEN_SSL} x509 -req -days 1825 -sha1 -in ${BIS_CSR} -CA ${ROOT_CERT} -CAkey ${ROOT_KEY} -out ${BIS_CRT} -extensions v3_req -extfile ${BIS_OPENSSL_CONF}
	status=`$ECHO $?`
	if [ $status -ne 0 -o ! -r "${BIS_CRT}" ]
	then
		error_exit "Problem creating self-signed cert file: ${BIS_CRT}"
	else
		log "Created self-signed cert file: ${BIS_CRT}"
	fi

	# export in pkcs12 format
	${OPEN_SSL} pkcs12 -export -in ${BIS_CRT} -inkey ${BIS_KEY} -out ${BIS_P12} -passout pass:${KEYSTORE_PWD}
	status=`$ECHO $?`
	if [ $status -ne 0 -o ! -r "${BIS_P12}" ]
	then
		error_exit "Problem exporting pkcs12 file: ${BIS_P12}"
	else
		log "Exported pkcs12 file: ${BIS_P12}"
	fi

	# import into tomcatssl.bin keystore
	${KEYTOOL} -importkeystore -srckeystore ${BIS_P12} -srcstoretype PKCS12 -srcstorepass ${KEYSTORE_PWD} -deststoretype JKS -deststorepass ${KEYSTORE_PWD} -destkeystore ${BIS_KEYSTORE}
	status=`$ECHO $?`
	if [ $status -ne 0 -o ! -r "${BIS_KEYSTORE}" ]
	then
		error_exit "Problem creating BIS keystore: ${BIS_KEYSTORE}"
	else
		log "Created BIS keystore: ${BIS_KEYSTORE}"
	fi

	# export cert from tomcatssl.bin keystore
	${KEYTOOL} -export -alias 1 -keystore ${BIS_KEYSTORE} -storepass ${KEYSTORE_PWD} -keypass ${KEYSTORE_PWD} -file ${BIS_FINAL_CERT}
	status=`$ECHO $?`
	if [ $status -ne 0 -o ! -r "${BIS_FINAL_CERT}" ]
	then
		error_exit "Problem exporting final self-signed cert: ${BIS_FINAL_CERT}"
	else
		log "Exported final self-signed cert: ${BIS_FINAL_CERT}"
	fi

	# check if cert already exists in GF trust store
	CERT_EXISTS=`${KEYTOOL} -list -alias ${GF_BIS_CERT_ALIAS} -keystore ${GF_TRUSTSTORE} -storepass ${GF_STOREPASS} -keypass ${GF_KEYPASS} | ${GREP} "Certificate fingerprint" | ${WC} -l`
	
	if [ ${CERT_EXISTS} -ne 0 ]
	then
		log "Alias ${GF_BIS_CERT_ALIAS} already exists in GlassFish truststore"
		${KEYTOOL} -delete -alias ${GF_BIS_CERT_ALIAS} -keystore ${GF_TRUSTSTORE} -storepass ${GF_STOREPASS} -keypass ${GF_KEYPASS}
		status=`$ECHO $?`
		if [ $status -ne 0 ]
		then
			error_exit "Problem deleting existing alias"
		else
			log "Removed existing alias ${GF_BIS_CERT_ALIAS}"
		fi
	fi

	# import cert into glassfish trust store
	${KEYTOOL} -import -alias ${GF_BIS_CERT_ALIAS} -keystore ${GF_TRUSTSTORE} -storepass ${GF_STOREPASS} -keypass ${GF_KEYPASS} -file ${BIS_FINAL_CERT} -noprompt
	status=`$ECHO $?`
	if [ $status -ne 0 ]
	then
		error_exit "Problem importing cert into GlassFish trust store"
	else
		log "Imported cert into GlassFish trust store"
	fi

	# Restart glassfish to pick up new cert
	# glassfish restart
	
}


#######################
# Processing starts here
#######################

GF_LOG_FILE='/eniq/log/sw_log/glassfish/security.log'

COMMON_FUNCTIONS="/eniq/installation/core_install/lib/common_functions.lib"

# Source the common functions
if [ -s ${COMMON_FUNCTIONS} ]
then
	. ${COMMON_FUNCTIONS}
else
	${ECHO} "File ${COMMON_FUNCTIONS} not found"
	exit 2
fi

log_msg -s "Creating Keystore For BIS" -l $GF_LOG_FILE -h -t

ENIQ_SW_DIR='/eniq/sw'
LOCK_FILE="${ENIQ_SW_DIR}/installer/.cbklock.tmp"

IS_FILE_PRESENT=0

GF_DIR='/eniq/glassfish/glassfish'
GF_BIN_DIR="${GF_DIR}/glassfish/bin"
GF_SSL_DIR="${GF_DIR}/glassfish/domains/domain1/config/ssl"
GF_TRUSTSTORE="${GF_DIR}/glassfish/domains/domain1/config/cacerts.jks"
GF_STOREPASS='changeit'
GF_KEYPASS='changeit'
GF_BIS_CERT_ALIAS="bis_$1"

ROOT_KEY="${GF_SSL_DIR}/eniq-root.key"
ROOT_CERT="${GF_SSL_DIR}/eniq-root.crt"

BIS_SSL_DIR='/eniq/home/dcuser/bis_ssl'	
BIS_OPENSSL_CONF_TEMPLATE="${GF_BIN_DIR}/openssl-bis.cnf"
BIS_OPENSSL_CONF="${GF_SSL_DIR}/openssl-bis.cnf"
BIS_KEY="${GF_SSL_DIR}/ia-bis.key"
BIS_CSR="${GF_SSL_DIR}/ia-bis.csr"
BIS_CRT="${GF_SSL_DIR}/ia-bis.crt"
BIS_P12="${GF_SSL_DIR}/ia-bis.p12"
BIS_KEYSTORE="${BIS_SSL_DIR}/tomcatssl.bin"
BIS_FINAL_CERT="${GF_SSL_DIR}/bisserver1.crt"

SSL_ALIAS='eniq'
KEYSTORE_PWD=""

BIS_NAME=$1
BIS_COMMON_NAME=""
BIS_FQDN=""
BIS_IP_ADDRESS=""

KEYTOOL="${RT_DIR}/java/bin/keytool"
OPEN_SSL="/usr/sfw/bin/openssl"

#####
# Check Arguments
#####
if [ $# -ne 1 ]
then
        $ECHO "Usage: create_bis_keystore.sh bisname"
        exit 4
fi

#####
# Check if already running
#####
check_file_presence $LOCK_FILE
if [ $IS_FILE_PRESENT -eq 1 ]
then
	log "One instance of this process is already running. Can not continue..." 
	exit 3
else
	touch $LOCK_FILE
fi

get_password

get_bis_details

create_bis_keystore

remove_lock_file

log "Succesfully created the BIS keystore. Log file: {$GF_LOG_FILE}"
log "New keystore: ${BIS_KEYSTORE}"
log "See the Security Admin Guide for detailed instructions on how to complete the BIS configuration."
