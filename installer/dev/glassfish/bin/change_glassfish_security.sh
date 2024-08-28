#!/usr/bin/bash
# -------------------------------------------------------------------
# Ericsson Network IQ ETLC engine control script
#
# Usage : change_glassfish_security.sh enable|disable|ssl|https|status [norestart]
#
# -------------------------------------------------------------------
# Copyright (c) 1999 - 2013 AB Ericsson Oy All rights reserved.
# --------------------------------------------------------------------

AWK=/usr/bin/awk
CP=/usr/bin/cp
ECHO=/usr/bin/echo
ED=/usr/bin/ed
EXPR=/usr/bin/expr
GREP=/usr/bin/grep
HOST=/usr/sbin/host
MKDIR=/usr/bin/mkdir
PS=/usr/bin/ps
RM=/usr/bin/rm
SED=/usr/bin/sed

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

remove_lock_file(){
	if [ -f $LOCK_FILE ]
        then
			$RM -rf $LOCK_FILE 2>&1 > /dev/null
        fi
}

log(){
	mess=$1
	dTime=`date +'%m/%d/%Y %H:%M:%S'`
	term=`who am i | $AWK  -F' ' '{print $2}'`
	$ECHO "$dTime :: $term :: $mess " >> $GF_LOG_FILE
	$ECHO $mess
}

error_exit(){
	errStr=$1
	dTime=`date +'%m/%d/%Y %H:%M:%S'`
	term=`who am i | $AWK  -F' ' '{print $2}'`
	$ECHO "$dTime :: $term :: Error: $errStr " >> $GF_LOG_FILE
	$ECHO "Error: $errStr !!!!Exiting script...."
	revert_domain_backup
	if [ -z ${GF_ADMIN_PASSWORD_FILE} ]
	then
		$ECHO "GF_ADMIN_PASSWORD_FILE is undefined" 
	else
		if [ -f ${GF_ADMIN_PASSWORD_FILE} ]
		then
			rm ${GF_ADMIN_PASSWORD_FILE}
		fi
	fi
	remove_lock_file
	exit 2
}

take_backup(){
	$CP $1 $1.backup 2>&1 > /dev/null
	status_02=`$ECHO $?`
	if [ $status_02 -eq 0 ]
	then
		log "Backup of original files has been taken. $1 ==> $1.backup"
	else
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

copy_and_del_temp_file(){
	$CP $1 $2
	if [ -f $1 ]
	then
		$RM -rf $1 2>&1 > /dev/null
	fi
	log "Updated $2"
}

replace_in_file(){

	take_backup $2
	
	# When asadmin updates domain.xml, it leaves the file with no final newline character
	# If this file is run through sed, the final line (closing </domain> tag) is lost
	#  and GlassFish will not start.
	# So... use ed to make sure there is a final newline character before doing the replace 
	# (ed opens and then saves the file, adding a final newline if required)
	$ED -s $2 <<< w

	# do the replace. rollback if there is a problem.
	$SED "$1" < $2 > $3
	status=`$ECHO $?`
	if [ $status -eq 0 ]
	then
		copy_and_del_temp_file $3 $2
	else
		revert_back $2
		error_exit "Error occur while enabling security in file : $2 " 
	fi
}	

call_asadmin(){
	if [ -z "$1" ]
	then
		error_exit "No parameter specified for call_asadmin" 
	else
	
		if [ -z "${GF_ADMIN_PASSWORD_FILE}" ]
		then
			error_exit "GF_ADMIN_PASSWORD_FILE is undefined" 
		else
			$ECHO "AS_ADMIN_PASSWORD=admin" > ${GF_ADMIN_PASSWORD_FILE}
		
			log "Calling asadmin: $1"
			${GF_ASADMIN_COMMAND} --port ${GF_ADMIN_PORT} -u ${GF_ADMIN_USER} --passwordfile ${GF_ADMIN_PASSWORD_FILE} $1 >> $GF_LOG_FILE 2>&1
			asadmin_call_status=`$ECHO $?`
			if [ ${asadmin_call_status} -ne 0 ]
			then
				error_exit "Problem running asadmin command: $1 " 
			fi
		
			rm ${GF_ADMIN_PASSWORD_FILE}
		fi
	fi
}

restart_glassfish(){
	if [ $NO_RESTART -eq 0 ]
	then
		${GF_COMMAND_FILE} restart >> $GF_LOG_FILE 2>&1
		glassfish_status=`$ECHO $?`
		if [ $glassfish_status -eq 0 ]
		then
			log "GlassFish restarted successfully."
		else
			error_exit "Problem restarting GlassFish. " 
		fi
	else
		log "Script called with norestart option. Restart GlassFish to apply changes."
	fi
}

stop_domain() {

	for domainToStop in $DOMAINS;
	do
		${GF_ASADMIN_COMMAND} stop-domain $domainToStop >> $GF_LOG_FILE 2>&1
		
		asadmin_call_status=`$ECHO $?`
		if [ ${asadmin_call_status} -ne 0 ]
		then
			log "Problem stopping domain(s) using asadmin. An attempt to kill the process will be done."
		fi
	done

	# problem stopping domain using asadmin so kill the process
	xPID=`$PS -ef | $GREP java | $GREP glassfish | $AWK '{print $2}'`
	if [ ! -z ${xPID} ] ; then
		kill -9 ${xPID}
		log "GlassFish domain(s) process killed"
	else 
		log "GlassFish domain(s) stopped"
	fi
	
	# double check GlassFish is down
	xPID=`$PS -ef | $GREP java | $GREP glassfish | $AWK '{print $2}'`
	if [ ! -z ${xPID} ] ; then
		error_exit "Problem stopping domain(s)." 
	fi
	
}

backup_domain_xml(){
	TIMESTAMP=`date +%d.%m.%y_%H:%M:%S`
	GF_DOMAIN_FILE_BACKUP="${GF_DOMAIN_FILE}.${TIMESTAMP}"
	
	$CP ${GF_DOMAIN_FILE} ${GF_DOMAIN_FILE_BACKUP}
	backup_status=`$ECHO $?`
	if [ ${backup_status} -ne 0 ]
	then
		error_exit "Problem backing up domain.xml to file: ${GF_DOMAIN_FILE_BACKUP} " 
	else
		log "Backed up domain.xml to file: ${GF_DOMAIN_FILE_BACKUP}"
	fi
}

revert_domain_backup(){

	if [ -z ${GF_DOMAIN_FILE_BACKUP} ]
	then
		log "No domain.xml backup file defined." 
	else
		if [ -f ${GF_DOMAIN_FILE_BACKUP} ]
		then
			$CP ${GF_DOMAIN_FILE_BACKUP} ${GF_DOMAIN_FILE} 2>&1 > /dev/null
		fi
		rollback_status=`$ECHO $?`
		if [ ${rollback_status} -eq 0 ]
		then
			log "Reverting domain.xml back to previous version. ${GF_DOMAIN_FILE_BACKUP} ==> ${GF_DOMAIN_FILE}"
		else
			log "Error : Failed to revert domain.xml back to previous version. ${GF_DOMAIN_FILE_BACKUP} ==> ${GF_DOMAIN_FILE}"
		fi
	fi

}

# Get IP Address of GlassFish server
get_gf_ip_address() {
	gf_host_details=`${HOST} ${GF_HOSTNAME}`
	status=`$ECHO $?`
	if [ $status -ne 0 -o "${gf_host_details}" = "" ]
	then
		error_exit "Problem getting GF Host details."
	fi

	GF_IP_ADDRESS=`${ECHO} ${gf_host_details} | ${AWK} '{print  $4}'`
	status=`$ECHO $?`
	if [ $status -ne 0 -o "${GF_IP_ADDRESS}" = "" ]
	then
		error_exit "Problem getting GF IP ADDRESS"
	fi
}



#######################
# MAIN WORKER FUNCTIONS
#######################

gf_is_glassfish_server(){
	IS_GLASSFISH_SERVER=`cat $CONF_DIR/service_names | grep "glassfish" | grep \`/usr/bin/hostname\` | wc -l`
}


#####
# check if ssl has been configured for GlassFish. Check for root cert first 
#####
gf_check_ssl(){

	IS_SSL_ROOT_SETUP=`$KEYTOOL -list -alias $SSL_ROOT_ALIAS -keystore $KEYSTORE -storepass $STOREPASS | $GREP "Certificate fingerprint" | wc -l`
	if [ ${IS_SSL_ROOT_SETUP} -eq 1 ]
	then
		IS_SSL_SETUP=`$KEYTOOL -list -alias $SSL_ALIAS -keystore $KEYSTORE -storepass $STOREPASS | $GREP "Certificate fingerprint" | wc -l`
	else
		IS_SSL_SETUP=0
	fi
}

# Create GF ssl directory & public cert directories if needed
prepare_directories() {
	if [ ! -d "${GF_SSL_DIR}" ]; then 
		log "${GF_SSL_DIR} does not exist. Creating..."
		${MKDIR} -p ${GF_SSL_DIR}
		if [ ! -d "${GF_SSL_DIR}" ]; then
			error_exit "Couldn't create directory ${GF_SSL_DIR}"
		fi
	fi
	
	
	if [ ! -d "${ROOT_PUBLIC_CERT_DIR}" ]; then 
		log "${ROOT_PUBLIC_CERT_DIR} does not exist. Creating..."
		${MKDIR} -p ${ROOT_PUBLIC_CERT_DIR}
		if [ ! -d "${ROOT_PUBLIC_CERT_DIR}" ]; then
			error_exit "Couldn't create directory ${ROOT_PUBLIC_CERT_DIR}"
		fi
	fi
}

# creating openssl config files
create_config_files() {
	log "Creating openssl config files in ${GF_SSL_DIR}"
	
	${CP} ${ROOT_OPENSSL_CONF_TEMPLATE} ${ROOT_OPENSSL_CONF}
	status=`$ECHO $?`
	if [ $status -ne 0 ]
	then
		error_exit "Problem copying openssl config file ${ROOT_OPENSSL_CONF_TEMPLATE} to ${ROOT_OPENSSL_CONF}"
	fi
	
	${CP} ${GF_OPENSSL_CONF_TEMPLATE} ${GF_OPENSSL_CONF}
	status=`$ECHO $?`
	if [ $status -ne 0 ]
	then
		error_exit "Problem copying openssl config file ${GF_OPENSSL_CONF_TEMPLATE} to ${GF_OPENSSL_CONF}"
	fi

	iniset req_distinguished_name -f ${ROOT_OPENSSL_CONF} commonName="${ROOT_COMMON_NAME}"
	status=`$ECHO $?`
	if [ $status -ne 0 ]
	then
		error_exit "Problem setting commonName in ${ROOT_OPENSSL_CONF}"
	else
		log "Set common name for root cert to ${ROOT_COMMON_NAME}"
	fi
	
	iniset req_distinguished_name -f ${GF_OPENSSL_CONF} commonName="${GF_FULLHOST}"
	status=`$ECHO $?`
	if [ $status -ne 0 ]
	then
		error_exit "Problem setting commonName in ${GF_OPENSSL_CONF}"
	else
		log "Set common name for GlassFish cert to ${GF_FULLHOST}"
	fi
	
	iniset alt_names -f ${GF_OPENSSL_CONF} DNS.1="${GF_FULLHOST}" IP.1="${GF_IP_ADDRESS}"
	status=`$ECHO $?`
	if [ $status -ne 0 ]
	then
		error_exit "Problem setting alt_names in ${GF_OPENSSL_CONF}"
	else
		log "Set alt names for GlassFish cert. DNS.1=${GF_FULLHOST}; IP.1=${GF_IP_ADDRESS}"
	fi
}

#####
# Set up ssl for GlassFish
#####
gf_setup_ssl(){
	gf_check_ssl
	if [ ${IS_SSL_SETUP} -eq 1 ]
	then
		error_exit "SSL is already set up."
	else
		log "Setting up SSL"

		prepare_directories
		create_config_files
		
		# create root cert
		${OPEN_SSL} genrsa -out ${ROOT_KEY} 2048 -config ${ROOT_OPENSSL_CONF}
		${OPEN_SSL} req -new -x509 -days 1825 -key ${ROOT_KEY} -out ${ROOT_CERT} -config ${ROOT_OPENSSL_CONF}

		# create serial number file for root cert
		${ECHO} "01" > ${ROOT_SERIAL_NO}

		# cert for GlassFish
		${OPEN_SSL} genrsa -out ${GF_KEY} 2048 -config ${GF_OPENSSL_CONF}
		${OPEN_SSL} req -new -key ${GF_KEY} -out ${GF_CERT_REQ} -config ${GF_OPENSSL_CONF}
		${OPEN_SSL} x509 -req -days 1825 -sha1 -in ${GF_CERT_REQ} -CA ${ROOT_CERT} -CAkey ${ROOT_KEY} -out ${GF_CERT}

		# convert certs to pkcs12 
		${OPEN_SSL} pkcs12 -export -in ${ROOT_CERT} -inkey ${ROOT_KEY} -out ${ROOT_CERT_P12} -name ${SSL_ROOT_ALIAS} -passout pass:${STOREPASS}
		${OPEN_SSL} pkcs12 -export -in ${GF_CERT} -inkey ${GF_KEY} -out ${GF_CERT_P12} -name ${SSL_ALIAS} -passout pass:${STOREPASS}

		SSL_ROOT_IN_KEYSTORE=`$KEYTOOL -list -alias $SSL_ROOT_ALIAS -keystore $KEYSTORE -storepass $STOREPASS | $GREP "Certificate fingerprint" | wc -l`
		SSL_ROOT_IN_TRUSTSTORE=`$KEYTOOL -list -alias $SSL_ROOT_ALIAS -keystore $TRUSTSTORE -storepass $STOREPASS | $GREP "Certificate fingerprint" | wc -l`
		SSL_SETUP_IN_KEYSTORE=`$KEYTOOL -list -alias $SSL_ALIAS -keystore $KEYSTORE -storepass $STOREPASS | $GREP "Certificate fingerprint" | wc -l`
		SSL_SETUP_IN_TRUSTSTORE=`$KEYTOOL -list -alias $SSL_ALIAS -keystore $TRUSTSTORE -storepass $STOREPASS | $GREP "Certificate fingerprint" | wc -l`

		# remove any existing certs
		if [ ${SSL_ROOT_IN_KEYSTORE} -ne 0 ]
		then
			${KEYTOOL} -delete -alias ${SSL_ROOT_ALIAS} -keystore ${KEYSTORE} -storepass ${STOREPASS} -keypass ${KEYPASS}
			log "Deleted existing cert \"${SSL_ROOT_ALIAS}\" from ${KEYSTORE}"
		fi

		if [ ${SSL_ROOT_IN_TRUSTSTORE} -ne 0 ]
		then
			${KEYTOOL} -delete -alias ${SSL_ROOT_ALIAS} -keystore ${TRUSTSTORE} -storepass ${STOREPASS} -keypass ${KEYPASS}
			log "Deleted existing cert \"${SSL_ROOT_ALIAS}\" from ${TRUSTSTORE}"
		fi

		if [ ${SSL_SETUP_IN_KEYSTORE} -ne 0 ]
		then
			${KEYTOOL} -delete -alias ${SSL_ALIAS} -keystore ${KEYSTORE} -storepass ${STOREPASS} -keypass ${KEYPASS}
			log "Deleted existing cert \"${SSL_ALIAS}\" from ${KEYSTORE}"
		fi

		if [ ${SSL_SETUP_IN_TRUSTSTORE} -ne 0 ]
		then
			${KEYTOOL} -delete -alias ${SSL_ALIAS} -keystore ${TRUSTSTORE} -storepass ${STOREPASS} -keypass ${KEYPASS}
			log "Deleted existing cert \"${SSL_ALIAS}\" from ${TRUSTSTORE}"
		fi
		
		
		# import certs into GF key store & trust store
		${KEYTOOL} -importkeystore \
        -deststorepass ${STOREPASS} -destkeypass ${KEYPASS} -destkeystore ${KEYSTORE} \
        -srckeystore ${ROOT_CERT_P12} -srcstoretype PKCS12 -srcstorepass ${STOREPASS} \
        -alias ${SSL_ROOT_ALIAS}

		${KEYTOOL} -importkeystore \
        -deststorepass ${STOREPASS} -destkeypass ${KEYPASS} -destkeystore ${KEYSTORE} \
        -srckeystore ${GF_CERT_P12} -srcstoretype PKCS12 -srcstorepass ${STOREPASS} \
        -alias ${SSL_ALIAS}

		${KEYTOOL} -importkeystore \
        -deststorepass ${STOREPASS} -destkeypass ${KEYPASS} -destkeystore ${TRUSTSTORE} \
        -srckeystore ${ROOT_CERT_P12} -srcstoretype PKCS12 -srcstorepass ${STOREPASS} \
        -alias ${SSL_ROOT_ALIAS}

		${KEYTOOL} -importkeystore \
        -deststorepass ${STOREPASS} -destkeypass ${KEYPASS} -destkeystore ${TRUSTSTORE} \
        -srckeystore ${GF_CERT_P12} -srcstoretype PKCS12 -srcstorepass ${STOREPASS} \
        -alias ${SSL_ALIAS}

		#exporting public key of eniqroot - required for import into browsers as trusted authority
		${KEYTOOL} -export -keystore ${KEYSTORE} -storepass ${STOREPASS} -alias ${SSL_ROOT_ALIAS} -file ${ROOT_PUBLIC_CERT}

		log "Done - SSL Setup Complete"
	fi
}

#####
# check if https has been configured for GlassFish
#####

gf_check_https_setup() {
	IS_HTTPS_SETUP=`cat $GF_DOMAIN_FILE | grep "protocol=\"https-redirect\"" | wc -l`
}

#####
# Set up https for GlassFish BUT DOES NOT ENABLE HTTPS.
#####
gf_setup_https(){
	if [ $IS_ALREADY_RUNNING -eq 1 ]
        then
			$ECHO "One instance of this process is already running. Can not continue..."
			exit 3
    fi

	gf_check_https_setup
	if [ $IS_HTTPS_SETUP -eq 1 ]
	then
		error_exit "HTTPS is already set up."
	else
	
		backup_domain_xml
		
		# set up ssl cert on listener 2 and enable https
		log "Setting up SSL & HTTPS on http-listener-2."
		call_asadmin 'set server.network-config.protocols.protocol.http-listener-2.security-enabled=false'
		call_asadmin 'delete-ssl --type http-listener http-listener-2'
		call_asadmin "create-ssl --type http-listener --certname ${SSL_ALIAS} http-listener-2"
		call_asadmin 'set server.network-config.protocols.protocol.http-listener-2.security-enabled=true'

		# set up port unification
		log "Setting up port unification - http to https."
		call_asadmin 'create-protocol --securityenabled=false http-redirect'
		call_asadmin "create-http-redirect --redirect-port ${GF_HTTPS_PORT} --secure-redirect true http-redirect"
		call_asadmin 'create-protocol --securityenabled=false pu-protocol-HTTP-HTTPS'
		call_asadmin 'create-protocol-finder --protocol pu-protocol-HTTP-HTTPS --targetprotocol http-listener-2 --classname com.sun.grizzly.config.HttpProtocolFinder http-finder'
		call_asadmin 'create-protocol-finder --protocol pu-protocol-HTTP-HTTPS --targetprotocol http-redirect --classname com.sun.grizzly.config.HttpProtocolFinder http-redirect'

		log "Setting up port unification - https to http."
		call_asadmin 'create-protocol --securityenabled=true https-redirect'
		call_asadmin "create-http-redirect --redirect-port ${GF_HTTP_PORT} --secure-redirect false https-redirect"
		call_asadmin "create-ssl --certname ${SSL_ALIAS} --type network-listener --ssl2enabled=false --ssl3enabled=false --tlsenabled=true --tlsrollbackenabled=true --clientauthenabled=false https-redirect"
		call_asadmin 'create-protocol --securityenabled=false pu-protocol-HTTPS-HTTP'
		call_asadmin 'create-protocol-finder --protocol pu-protocol-HTTPS-HTTP --targetprotocol http-listener-1 --classname com.sun.grizzly.config.HttpProtocolFinder http-finder'
		call_asadmin 'create-protocol-finder --protocol pu-protocol-HTTPS-HTTP --targetprotocol https-redirect --classname com.sun.grizzly.config.HttpProtocolFinder https-redirect'

		# initially set glassfish to http
		call_asadmin 'set configs.config.server-config.network-config.network-listeners.network-listener.http-listener-1.protocol=http-listener-1'
		call_asadmin 'set configs.config.server-config.network-config.network-listeners.network-listener.http-listener-2.protocol=pu-protocol-HTTPS-HTTP'
		
		CHANGED_HTTPS_CONFIG=1
		log "Done - SSL & HTTPS is now set up (but is not yet enabled)."
	fi
}

#####
# Get https status for GlassFish
#####
gf_check_http_status() {
	IS_HTTPS_ENABLED=0
	is_enabled=`cat $GF_DOMAIN_FILE | grep "pu-protocol-HTTP-HTTPS" | grep "http-listener-1" | wc -l`
	if [ $is_enabled -ge 1 ]
	then
		IS_HTTPS_ENABLED=`$EXPR $IS_HTTPS_ENABLED + 1`
	else
		IS_HTTPS_ENABLED=`$EXPR $IS_HTTPS_ENABLED + 0`
	fi
}

#####
# Report https status 
#####
gf_http_status(){
	gf_check_http_status
	if [ $IS_HTTPS_ENABLED -ge 1 ]
	then
		log "HTTPS is enabled. HTTP connections will be redirected to HTTPS."
	else
		log "HTTPS is disabled. HTTPS connections will be redirected to HTTP."
	fi
}

#####
# Check that both ssl and https are set up. If not, run setup
#####
check_ssl_http_setup() {
	gf_check_ssl
	if [ ${IS_SSL_SETUP} -eq 0 ]
	then
		log "SSL has not been set up. Running setup now."
		gf_setup_ssl
	fi

	gf_check_https_setup
	if [ $IS_HTTPS_SETUP -eq 0 ]
	then
		log "HTTPS has not been set up. Running setup now."
		gf_setup_https
	fi
}

#####
# Enable https 
#####
https_enable(){
	if [ $IS_ALREADY_RUNNING -eq 1 ]
	then
		$ECHO "One instance of this process is already running. Can not continue..."
		exit 3
    fi

	# make sure ssl and https are set up before attempting to change the configuration
	check_ssl_http_setup
	
	gf_check_http_status
	if [ $IS_HTTPS_ENABLED -eq 1 ]
	then
		error_exit "HTTPS is already enabled."
	else
	
		backup_domain_xml
		
		# switch glassfish to https
		call_asadmin 'set configs.config.server-config.network-config.network-listeners.network-listener.http-listener-1.protocol=pu-protocol-HTTP-HTTPS'
		call_asadmin 'set configs.config.server-config.network-config.network-listeners.network-listener.http-listener-2.protocol=http-listener-2'

		# enable secure admin - force https for GlassFish Admin Console
		#call_asadmin 'enable-secure-admin'
		
		log "Stop domain(s) before updating domain.xml"
		stop_domain

		# change URLs in domain.xml
		log "Replacing HTTP URLs in domain.xml"
		replace_in_file "s/${GF_HTTP_PORT}\/geoserver/${GF_HTTPS_PORT}\/geoserver/g" $GF_DOMAIN_FILE $TEMP_DOMAIN_FILE
		replace_in_file 's/GEO_SERVER_URI\" value=\"http\:/GEO_SERVER_URI\" value=\"https\:/g' $GF_DOMAIN_FILE $TEMP_DOMAIN_FILE
		replace_in_file 's/BIS_SOAP_SERVICE_URL\" value=\"http\:/BIS_SOAP_SERVICE_URL\" value=\"https\:/g' $GF_DOMAIN_FILE $TEMP_DOMAIN_FILE
		replace_in_file "s/\:${BIS_HTTP_PORT}\//\:${BIS_HTTPS_PORT}\//g" $GF_DOMAIN_FILE $TEMP_DOMAIN_FILE
		replace_in_file "s/${GF_HTTP_PORT}\/EniqEventsServices/${GF_HTTPS_PORT}\/EniqEventsServices/g" $GF_DOMAIN_FILE $TEMP_DOMAIN_FILE
		replace_in_file 's/ENIQ_EVENTS_SERVICES_URI\" value=\"http\:/ENIQ_EVENTS_SERVICES_URI\" value=\"https\:/g' $GF_DOMAIN_FILE $TEMP_DOMAIN_FILE
		
		restart_glassfish

		log "Done - HTTPS is now enabled. HTTP connections will be redirected to HTTPS."
	fi
}

#####
# Disable https 
#####
https_disable() {
	if [ $IS_ALREADY_RUNNING -eq 1 ]
	then
		$ECHO "One instance of this process is already running. Can not continue..."
		exit 3
    fi

	# make sure ssl and https are set up before attempting to change the configuration
	check_ssl_http_setup
	
	gf_check_http_status
	if [ $IS_HTTPS_ENABLED -eq 0 ]
	then
		if [ $CHANGED_HTTPS_CONFIG -eq 1 ]
		then
			# HTTPS configuration has been updated (initial set up), so restart
			restart_glassfish
		else 
			error_exit "HTTPS is already disabled."
		fi
	else
	
		backup_domain_xml
		
		# switch glassfish to http
		call_asadmin 'set configs.config.server-config.network-config.network-listeners.network-listener.http-listener-1.protocol=http-listener-1'
		call_asadmin 'set configs.config.server-config.network-config.network-listeners.network-listener.http-listener-2.protocol=pu-protocol-HTTPS-HTTP'
		
		# NOTE: For GlassFish 3.1.2, access to the GlassFish Admin console must be secure and use HTTPS - so don't disable it...

		log "Stop domain(s) before updating domain.xml"
		stop_domain

		# change URLs in domain.xml
		log "Replacing HTTPS URLs in domain.xml"
		replace_in_file "s/${GF_HTTPS_PORT}\/geoserver/${GF_HTTP_PORT}\/geoserver/g" $GF_DOMAIN_FILE $TEMP_DOMAIN_FILE
		replace_in_file 's/GEO_SERVER_URI\" value=\"https\:/GEO_SERVER_URI\" value=\"http\:/g' $GF_DOMAIN_FILE $TEMP_DOMAIN_FILE
		replace_in_file 's/BIS_SOAP_SERVICE_URL\" value=\"https\:/BIS_SOAP_SERVICE_URL\" value=\"http\:/g' $GF_DOMAIN_FILE $TEMP_DOMAIN_FILE
		replace_in_file "s/\:${BIS_HTTPS_PORT}\//\:${BIS_HTTP_PORT}\//g" $GF_DOMAIN_FILE $TEMP_DOMAIN_FILE
		replace_in_file "s/${GF_HTTPS_PORT}\/EniqEventsServices/${GF_HTTP_PORT}\/EniqEventsServices/g" $GF_DOMAIN_FILE $TEMP_DOMAIN_FILE
		replace_in_file 's/ENIQ_EVENTS_SERVICES_URI\" value=\"https\:/ENIQ_EVENTS_SERVICES_URI\" value=\"http\:/g' $GF_DOMAIN_FILE $TEMP_DOMAIN_FILE
		
		restart_glassfish
		
		log "Done - HTTPS is now disabled. HTTPS connections will be redirected to HTTP."
	fi

}

#######################
# Processing starts here
#######################

#####
# Global Variables
#####

COMMON_FUNCTIONS="/eniq/installation/core_install/lib/common_functions.lib"

# Source the common functions
if [ -s ${COMMON_FUNCTIONS} ]
then
    . ${COMMON_FUNCTIONS}
else
    error_exit "File ${COMMON_FUNCTIONS} not found"
fi

#####
# Check User. Only dcuser should be allowed to run the script
#####
isDCUSER=`id | $AWK -F' ' '{print $1}' | grep -i dcuser | wc -l`
isDCUSER=`$EXPR $isDCUSER + 0`
if [ $isDCUSER -ne 1 ]
then
	$ECHO "This script can be run only as dcuser."
	exit 5
fi

#####
# Check Arguments
#####
if [ $# -ne 1 -a $# -ne 2 -a $# -ne 3 ]
then
        $ECHO "Usage: change_glassfish_security.sh enable|disable|ssl|https|status [norestart]"
        exit 4
fi

GF_DIR='/eniq/glassfish/glassfish3'

if [ $# -eq 3 ]
then
	DOMAINS="$3"
else
	DOMAINS=`ls ${GF_DIR}/glassfish/domains/`
fi

for domainName in $DOMAINS;
do
	ENIQ_SW_DIR='/eniq/sw'

	LOCK_FILE="${ENIQ_SW_DIR}/installer/.gflock.tmp"
	ENIQ_INI="${ENIQ_SW_DIR}/conf/niq.ini"
	GF_FULLHOST=`iniget GLASSFISH -f ${ENIQ_INI} -v GF_FullHost`
	GF_HOSTNAME=`echo $GF_FULLHOST | cut -d . -f 1`

	get_gf_ip_address

	GF_COMMAND_FILE="${ENIQ_SW_DIR}/bin/glassfish"
	GF_SERVER='glassfish'
	GF_SERVER_USER='dcuser'

	GF_LOG_FILE='/eniq/log/sw_log/glassfish/security.log'
	GF_BIN_DIR="${GF_DIR}/glassfish/bin"

	GF_ASADMIN_COMMAND="${GF_DIR}/bin/asadmin"
	GF_ADMIN_USER="admin"
	GF_ADMIN_PASSWORD_FILE="/tmp/gfpwdfile.txt"

	ROOT_PUBLIC_CERT_DIR="/eniq/home/dcuser/bis_ssl"

	if [ $domainName == "domain1" ]
	then
		GF_ADMIN_PORT="14848"
		GF_HTTP_PORT='18080'
		GF_HTTPS_PORT='8181'
		BIS_HTTP_PORT='8080'
		BIS_HTTPS_PORT='8443'
		ROOT_COMMON_NAME="ENIQ_ROOT_${GF_HOSTNAME}"
		ROOT_PUBLIC_CERT="${ROOT_PUBLIC_CERT_DIR}/eniqroot-${GF_HOSTNAME}.cer"
		SSL_ALIAS="eniq"
	elif [ $domainName == "domain2" ]
	then
		GF_ADMIN_PORT="15050"
		ROOT_COMMON_NAME="ENIQ_ROOT_${GF_HOSTNAME}$domainName"
		ROOT_PUBLIC_CERT="${ROOT_PUBLIC_CERT_DIR}/eniqroot-${GF_HOSTNAME}$domainName.cer"
		SSL_ALIAS="eniq$domainName"
	fi
	
	GF_DOMAIN_FILE="${GF_DIR}/glassfish/domains/$domainName/config/domain.xml"
	GF_DOMAIN_FILE_BACKUP=""
	TEMP_DOMAIN_FILE="/tmp/domain.xml.tmp"

	OPEN_SSL="/usr/sfw/bin/openssl"
	KEYTOOL="${RT_DIR}/java/bin/keytool"

	GF_SSL_DIR="${GF_DIR}/glassfish/domains/$domainName/config/ssl"
	ROOT_OPENSSL_CONF_TEMPLATE="${GF_BIN_DIR}/openssl-root.cnf"
	ROOT_OPENSSL_CONF="${GF_SSL_DIR}/openssl-root.cnf"
	GF_OPENSSL_CONF_TEMPLATE="${GF_BIN_DIR}/openssl-gf.cnf"
	GF_OPENSSL_CONF="${GF_SSL_DIR}/openssl-gf.cnf"

	ROOT_KEY="${GF_SSL_DIR}/eniq-root.key"
	ROOT_CERT="${GF_SSL_DIR}/eniq-root.crt"
	ROOT_CERT_P12="${GF_SSL_DIR}/eniq-root.p12"
	ROOT_SERIAL_NO="${GF_SSL_DIR}/eniq-root.srl"

				
	GF_KEY="${GF_SSL_DIR}/ia-gf.key"
	GF_CERT_REQ="${GF_SSL_DIR}/ia-gf.csr"
	GF_CERT="${GF_SSL_DIR}/ia-gf.crt"
	GF_CERT_P12="${GF_SSL_DIR}/ia-gf.p12"

	KEYSTORE="${GF_DIR}/glassfish/domains/$domainName/config/keystore.jks"
	TRUSTSTORE="${GF_DIR}/glassfish/domains/$domainName/config/cacerts.jks"
	STOREPASS='changeit'
	KEYPASS='changeit'
	SSL_ROOT_ALIAS="eniq_root"

	IS_SSL_SETUP=1
	IS_SSL_ROOT_SETUP=1
	IS_HTTPS_SETUP=1
	IS_FILE_PRESENT=0
	IS_HTTPS_ENABLED=0
	CHANGED_HTTPS_CONFIG=0
	IS_ALREADY_RUNNING=0
	IS_GLASSFISH_SERVER=0
	NO_RESTART=0

	$ECHO "Logging to: ${GF_LOG_FILE}"

	#####
	# Check if already running
	#####
	check_file_presence $LOCK_FILE
	if [ $IS_FILE_PRESENT -eq 1 ]
	then
		IS_ALREADY_RUNNING=`$EXPR $IS_ALREADY_RUNNING + 1`
	else
			touch $LOCK_FILE
	fi

	#####
	# Check for norestart option
	#####
	if [ "$2" == "norestart" ]
	then
		NO_RESTART=1
	fi

	#####
	# Run requested option
	#####
	case "$1" in 
	enable)
		if [ $domainName == "domain1" ]
		then
			https_enable
		fi
			;;
	disable)
		if [ $domainName == "domain1" ]
		then
			https_disable
		fi
			;;
	ssl)
		gf_check_ssl
		if [ ${IS_SSL_SETUP} -eq 0 ]
		then
			gf_setup_ssl
		else
			log "SSL is already set up for: $domainName"
		fi
			;;
	https)
		if [ $domainName == "domain1" ]
		then
			gf_check_https_setup
			if [ $IS_HTTPS_SETUP -eq 0 ]
			then
				gf_setup_https
			else
				error_exit "HTTPS is already set up."
			fi
		fi
			;;
	status) 
		if [ $domainName == "domain1" ]
		then
			gf_http_status
		fi
			;;
	*) 
		$ECHO "Usage : change_glassfish_security.sh enable|disable|ssl|https|status [norestart]"
		remove_lock_file
		exit 10
			;; 
	esac

done
remove_lock_file
