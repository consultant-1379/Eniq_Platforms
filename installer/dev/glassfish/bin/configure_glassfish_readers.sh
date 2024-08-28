#!/usr/bin/bash
# -------------------------------------------------------------------
# Ericsson Network IQ ETLC engine control script
#
# Usage : configure_glassfish_readers [status|force]
# 
# force option will re-configure data sources if they already exist
#
# -------------------------------------------------------------------
# Copyright (c) 1999 - 2013 AB Ericsson Oy All rights reserved.
# --------------------------------------------------------------------

AWK=/usr/bin/awk
CP=/usr/bin/cp
CUT=/usr/bin/cut
DATE=/usr/bin/date
ECHO=/usr/bin/echo
EGREP=/usr/bin/egrep
RM=/usr/bin/rm
SED=/usr/bin/sed
WHO=/usr/bin/who

#######################
# SUPPORTING FUNCTIONS
#######################

check_file_presence()
{
	IS_FILE_PRESENT=0
	fileName=$1
	if [ -r $fileName ]
	then
		IS_FILE_PRESENT=`$EXPR $IS_FILE_PRESENT + 1`
	else
		IS_FILE_PRESENT=`$EXPR $IS_FILE_PRESENT + 0`
	fi
}

remove_lock_file()
{
	if [ -f $LOCK_FILE ]
        then
			$RM -rf $LOCK_FILE 2>&1 > /dev/null
        fi
}

log()
{
	mess=$1
	dTime=`${DATE} +'%m/%d/%Y %H:%M:%S'`
	term=`${WHO} am i | $AWK  -F' ' '{print $2}'`
	$ECHO "$dTime :: $term :: $mess " >> $GF_LOG_FILE
	$ECHO $mess
}

error_exit()
{
	errStr=$1
	dTime=`${DATE} +'%m/%d/%Y %H:%M:%S'`
	term=`${WHO} am i | $AWK  -F' ' '{print $2}'`
	$ECHO "$dTime :: $term :: Error: $errStr " >> $GF_LOG_FILE
	$ECHO "Error: $errStr !!!!Exiting script...."
	$ECHO "Log file=${GF_LOG_FILE}"

	revert_domain_backup

	if [ ! -z ${GF_ADMIN_PASSWORD_FILE} ]
	then
		if [ -f ${GF_ADMIN_PASSWORD_FILE} ]
		then
			$RM ${GF_ADMIN_PASSWORD_FILE}
		fi
	fi
	
	if [ ! -z ${DB_MPX_SQL_FILE} ]
	then
		if [ -f ${DB_MPX_SQL_FILE} ]
		then
			$RM ${DB_MPX_SQL_FILE}
		fi
	fi
	
	remove_lock_file
	exit 2
}

backup_domain_xml()
{
	timestamp=`${DATE} +%d.%m.%y_%H:%M:%S`
	GF_DOMAIN_FILE_BACKUP="${GF_DOMAIN_FILE}.${timestamp}"
	
	$CP ${GF_DOMAIN_FILE} ${GF_DOMAIN_FILE_BACKUP}
	backup_status=`$ECHO $?`
	if [ ${backup_status} -ne 0 ]
	then
		error_exit "Problem backing up domain.xml to file: ${GF_DOMAIN_FILE_BACKUP}" 
	else
		log "Backed up domain.xml to file: ${GF_DOMAIN_FILE_BACKUP}"
	fi
}

revert_domain_backup()
{
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

call_asadmin()
{
	if [ -z "$1" ]
	then
		error_exit "No parameter specified for call_asadmin" 
	else
	
		if [ -z "${GF_ADMIN_PASSWORD_FILE}" ]
		then
			error_exit "GF_ADMIN_PASSWORD_FILE is undefined" 
		else
			$ECHO "AS_ADMIN_PASSWORD=admin" > ${GF_ADMIN_PASSWORD_FILE}
		
			${GF_ASADMIN_COMMAND} --port ${GF_ADMIN_PORT} -u ${GF_ADMIN_USER} --passwordfile ${GF_ADMIN_PASSWORD_FILE} $1
			asadmin_call_status=`$ECHO $?`
			if [ ${asadmin_call_status} -ne 0 ]
			then
				error_exit "Problem running asadmin command: $1 " 
			fi
		
			rm ${GF_ADMIN_PASSWORD_FILE}
		fi
	fi
}


check_jdbc_connection_exists()
{
	CONNECTION_EXISTS=0

	for jdbc_conn in ${JDBC_CONNECTIONS[*]}
	do
		if [ "${jdbc_conn}" = "$1"   ] 
		then
			CONNECTION_EXISTS=1
			break
		fi
	done
}

check_reader_is_useable()
{
	READER_IS_USEABLE=0

	for chk_reader in ${USEABLE_READER_NODES[*]}
	do
		if [ "${chk_reader}" = "$1"   ] 
		then
			READER_IS_USEABLE=1
			break
		fi
	done
}

source_functions()
{
	COMMON_FUNCTIONS="/eniq/installation/core_install/lib/common_functions.lib"
	SYBASE_FUNCTIONS="${IQ_DIR}/IQ.sh"

	# Source the common functions
	if [ -s ${COMMON_FUNCTIONS} ]
	then
		. ${COMMON_FUNCTIONS}
	else
		error_exit "File ${COMMON_FUNCTIONS} not found"
	fi

	# Source the sybase functions
	if [ -s ${SYBASE_FUNCTIONS} ]
	then
		. ${SYBASE_FUNCTIONS}
	else
		error_exit "File ${SYBASE_FUNCTIONS} not found"
	fi
}

init_server_type_details()
{
	# Set up vars and get server type
	if [ ! -r "${ENIQ_CONF_DIR}/installed_server_type" ] ; then
	  error_exit "ERROR: Server type not readable at ${ENIQ_CONF_DIR}/installed_server_type"
	fi

	if [ ! -r "${ENIQ_CONF_DIR}/extra_params/deployment" ] ; then
	  error_exit "ERROR: Deployment type not readable at ${ENIQ_CONF_DIR}/extra_params/deployment"
	fi

	SERVER_TYPE=`$CAT ${ENIQ_CONF_DIR}/installed_server_type`
	DEPLOYMENT_TYPE=`$CAT ${ENIQ_CONF_DIR}/extra_params/deployment`

	log "SERVER TYPE: ${SERVER_TYPE}"

	ENIQ_SW_DIR='/eniq/sw'
	LOCK_FILE="${ENIQ_SW_DIR}/installer/.cgrlock.tmp"

	if [ ${SERVER_TYPE} = "eniq_events" ]
	then
		DB_PORT_NUMBER=`iniget DWH -f ${ENIQ_INI} -v PortNumber`
	else
		DB_PORT_NUMBER=`iniget DWH_READER_SETTINGS -f ${ENIQ_INI} -v PortNumber`
	fi

	# connection pools for medium and large deployments are configured the same
	case ${DEPLOYMENT_TYPE} in
	ft)
		DB_MAX_POOL_SIZE=30
			;;
	small)
		DB_MAX_POOL_SIZE=50
			;;
	*)
		DB_MAX_POOL_SIZE=75
	esac
	log "DEPLOYMENT TYPE: ${DEPLOYMENT_TYPE} => MAX_POOL_SIZE: ${DB_MAX_POOL_SIZE}"
}

#######################
# MAIN WORKER FUNCTIONS
#######################

get_current_jdbc_reader_connections()
{
	log "CHECKING EXISTING JDBC CONNECTIONS"
	
	$ECHO "AS_ADMIN_PASSWORD=admin" > ${GF_ADMIN_PASSWORD_FILE}

	if [ -z "${GF_ADMIN_PASSWORD_FILE}" ]
	then
		error_exit "GF_ADMIN_PASSWORD_FILE is undefined" 
	else
		JDBC_CONNECTIONS=(`${GF_ASADMIN_COMMAND} --port ${GF_ADMIN_PORT} -u ${GF_ADMIN_USER} --passwordfile ${GF_ADMIN_PASSWORD_FILE} list-jdbc-connection-pools | ${EGREP} "dwh_reader_[0-9]"`)
		asadmin_call_status=`$ECHO $?`
		if [ ${asadmin_call_status} -gt 1 ]
		then
			error_exit "Problem running asadmin command: $1 " 
		fi

		$RM ${GF_ADMIN_PASSWORD_FILE}

		log "Number of defined dwh_reader jdbc connections: ${#JDBC_CONNECTIONS[@]}"
	fi
}

get_available_readers()
{
	log "CHECKING AVAILABLE READERS"
	
	if [ ${SERVER_TYPE} = "eniq_events" ]
	then
		READER_NODES[0]='dwh_reader_1'
		log "Single Blade install - active dwh_reader nodes: ${#READER_NODES[@]}"
	else
		$ECHO "set temporary option ON_ERROR='EXIT';" > ${DB_MPX_SQL_FILE}
		$ECHO "select server_name from sp_iqmpxinfo() where inc_state = 'active'" >> ${DB_MPX_SQL_FILE}

		READER_NODES=( `${DB_IQISQL_COMMAND} -c ${DB_CONNECTION} -nogui ${DB_MPX_SQL_FILE} | ${EGREP} "dwh_reader_[0-9]"` )
		status=`$ECHO $?`
		if [ $status -eq 0 ]
		then
			log "Got reader info from SybaseIQ "
		else
			error_exit "Problem getting reader info from SybaseIQ"
		fi

		$RM ${DB_MPX_SQL_FILE}

		if [ ${#READER_NODES[@]} -eq 0 ]
		then
			error_exit "No active dwh_reader nodes found"
		else
			log "Number of active dwh_reader nodes: ${#READER_NODES[@]}"
		fi
	fi
}

gf_reader_status()
{
	get_available_readers
	get_current_jdbc_reader_connections

	log "GLASSFISH READER STATUS"
	
	log "Currently Defined JDBC Connections to Readers"
	for jdbc_conn in ${JDBC_CONNECTIONS[*]}
	do
		log "  ${jdbc_conn}"
	done

	log "Currently Available Readers"
	for reader_node in ${READER_NODES[*]}
	do
		log "  ${reader_node}"
	done
}

delete_connection_pool_and_resource()
{
	log "Deleting connection pool and resource for $1"
	check_jdbc_connection_exists $1
	
	if [ ${CONNECTION_EXISTS} -eq 0 ] 
	then
		log "Connection doesn't exist"
	else
		call_asadmin "delete-jdbc-resource jdbc/$1"
		call_asadmin "delete-jdbc-connection-pool --cascade true $1"

		log "Sucessfully deleted connection pool and resource for $1"
	fi
	
}
	
create_connection_pool_and_resource()
{
	log "CREATING CONNECTION POOL AND RESOURCE: $1"
	check_jdbc_connection_exists $1

	if [ ${CONNECTION_EXISTS} -eq 1 -a ${GF_FORCE_CONFIG} -eq 1 ]
	then
		log "Connection $1 already exists - forcing re-create"

		call_asadmin "delete-jdbc-resource jdbc/$1"
		call_asadmin "delete-jdbc-connection-pool --cascade true $1"

		log "Sucessfully deleted connection pool and resource for $1"
		
		CONNECTION_EXISTS=0
	fi
		
	if [ ${CONNECTION_EXISTS} -eq 1 ] 
	then
		log "Connection $1 already exists - no need to create"
	else
		call_asadmin "create-jdbc-connection-pool --datasourceclassname ${DB_DATASOURCE} --restype javax.sql.DataSource --maxwait 60000 --idletimeout 300 --steadypoolsize 0 --maxpoolsize ${DB_MAX_POOL_SIZE} --poolresize 8 --leaktimeout 3600 --leakreclaim true --maxconnectionusagecount 120 --property portNumber=${DB_PORT_NUMBER}:serverName=$1:${DC_CONNECTION}:URL=\"jdbc\\:sybase\\:Tds\\:$1\\:${DB_PORT_NUMBER}\" $1"
		call_asadmin "create-jdbc-resource --connectionpoolid $1 --enabled true jdbc/$1"
		log "Sucessfully created connection: $1"
		RESTART_DOMAIN=1
	fi

	call_asadmin "set resources.jdbc-connection-pool.$1.datasource-classname=${DB_DATASOURCE}"
	call_asadmin "set resources.jdbc-connection-pool.$1.statement-timeout-in-seconds=3300"
	call_asadmin "set resources.jdbc-connection-pool.$1.statement-leak-timeout-in-seconds=3420"
	call_asadmin "set resources.jdbc-connection-pool.$1.statement-leak-reclaim=true"
}

set_dwh_reader_export_csv()
{
	log "SETTING DWH_READER_EXPORT_CSV CONNECTION POOL: $1"
	
	call_asadmin "set server.resources.jdbc-connection-pool.dwh_reader_export_csv.property.URL=jdbc:sybase:Tds:$1:${DB_PORT_NUMBER}"
	call_asadmin "set resources.jdbc-connection-pool.dwh_reader_export_csv.datasource-classname=${DB_DATASOURCE}"
	call_asadmin "set resources.jdbc-connection-pool.dwh_reader_export_csv.statement-timeout-in-seconds=3300"
	call_asadmin "set resources.jdbc-connection-pool.dwh_reader_export_csv.statement-leak-timeout-in-seconds=3420"
	call_asadmin "set resources.jdbc-connection-pool.dwh_reader_export_csv.statement-leak-reclaim=true"
    call_asadmin "set server.resources.jdbc-connection-pool.dwh_reader_export_csv.property.serverName=$1"
	
	log "Sucessfully set connection pool for dwh_reader_export_csv"
}

set_eniq_event_data_sources()
{
	log "SETTING DATA SOURCE(S)"
	log "Default data source: $1"

	# $1 = default data source name

	additional_ds=""
	
	if [ ${#ADDITIONAL_DATA_SOURCES[@]} -eq 0 ]
	then
		log "No additional data sources"
	else
		# build additional data source text, excluding default ds if included
		for i_ds in ${ADDITIONAL_DATA_SOURCES[*]}
		do
			if [ "${i_ds}" != "$1" ]  
			then
				if [ "${additional_ds}" != "" ]
				then
					additional_ds+=","
				fi
				additional_ds+="jdbc/${i_ds}"
			fi
		done
		additional_ds_msg=`$ECHO $additional_ds | ${SED} -e 's/jdbc\///g'`
		log "Additional data sources: ${additional_ds_msg}"
	fi

	if [ $domainName == "domain1" ]
	then
		call_asadmin "set server.resources.custom-resource.Eniq_Event_Properties.property.ENIQ_EVENTS_DEFAULT_DATA_SOURCE=jdbc/$1"
		call_asadmin "set server.resources.custom-resource.Eniq_Event_Properties.property.ENIQ_EVENTS_ADDITIONAL_DATA_SOURCES=${additional_ds}"
	fi
}

build_useable_reader_list()
{
	log "BUILDING USEABLE READER LIST"

	reader_2_exists=0
	USEABLE_READER_NODES=()
	
	for i_reader in ${READER_NODES[*]}
	do
		if [ "${i_reader}" != "dwh_reader_1" ]
		then
			USEABLE_READER_NODES+=(${i_reader})
			if [ "${i_reader}" == "dwh_reader_2" ]
			then
				reader_2_exists=1
			fi
		fi
	done

	if [ ${reader_2_exists} -eq 1 ]
	then
		DEFAULT_READER="dwh_reader_2"
	else
		DEFAULT_READER=${USEABLE_READER_NODES[0]}
	fi
	
	log "Useable readers: ${#USEABLE_READER_NODES[@]}"
	log "Default reader: ${DEFAULT_READER}"
}

# remove any unwanted existing connections
# existing connection is unwanted if the reader is not in the list of useable readers
remove_unwanted_connections()
{
	log "REMOVING UNWANTED CONNECTIONS"

	for jdbc_conn in ${JDBC_CONNECTIONS[*]}
	do
		check_reader_is_useable ${jdbc_conn}
		if [ ${READER_IS_USEABLE} -eq 0 ]
		then
			call_asadmin "delete-jdbc-resource jdbc/${jdbc_conn}"
			call_asadmin "delete-jdbc-connection-pool --cascade true ${jdbc_conn}"
			log "Removed connection: ${jdbc_conn}"
			RESTART_DOMAIN=1
		fi
	done
}

build_additional_ds_list()
{
	log "BUILDING ADDITIONAL DATA SOURCE LIST"
	
	ADDITIONAL_DATA_SOURCES=()
	
	for reader_node in ${USEABLE_READER_NODES[*]}
	do
		if [ "${reader_node}" != "dwh_reader_1" -a "${reader_node}" != "${DEFAULT_READER}" ]
		then
			ADDITIONAL_DATA_SOURCES+=(${reader_node})
		fi
	done

	log "Built ${#ADDITIONAL_DATA_SOURCES[@]} additional data source(s)"
}

restart_domain()
{
	if [ ${RESTART_DOMAIN} -eq 0 ]
	then
		log "No change in connection pool. No need to restart $domainName."
	else
		log "Change in connection pool, restarting $domainName."

		call_asadmin "stop-domain $domainName"

		call_asadmin "start-domain $domainName"

		log "Domains restarted successfully."

		RESTART_DOMAIN=0
	fi
}

configure_readers()
{
	backup_domain_xml

	get_available_readers
	get_current_jdbc_reader_connections
	
	# if only one reader exists, use this reader for everything
	if [ ${#READER_NODES[@]} -eq 1 ]
	then
		USEABLE_READER_NODES=(${READER_NODES[0]})
		DEFAULT_READER=${READER_NODES[0]}
	else
		build_useable_reader_list
		build_additional_ds_list
	fi

	remove_unwanted_connections

	# create connection pool/resource for all useable readers
	for reader_node in ${USEABLE_READER_NODES[*]}
	do
		create_connection_pool_and_resource ${reader_node}
	done

	set_dwh_reader_export_csv ${DEFAULT_READER}
	set_eniq_event_data_sources ${DEFAULT_READER}
	restart_domain
}


#######################
# Processing starts here
#######################

GF_LOG_FILE='/eniq/log/sw_log/glassfish/config.log'

log "========================================"
log "CONFIGURING GLASSFISH READER CONNECTIONS"
log "========================================"

if [ -z "${CONF_DIR}" ] ; then
  error_exit "ERROR: CONF_DIR is not set"
fi

if [ ! -r "${CONF_DIR}/niq.rc" ] ; then
  error_exit "ERROR: Source file is not readable at ${CONF_DIR}/niq.rc"
fi

. ${CONF_DIR}/niq.rc
	
source_functions

ENIQ_SW_DIR='/eniq/sw'
ENIQ_INI="${ENIQ_SW_DIR}/conf/niq.ini"
GF_DIR='/eniq/glassfish/glassfish'

DOMAINS=`ls ${GF_DIR}/glassfish/domains/`

GF_FORCE_CONFIG=0
GF_ASADMIN_COMMAND="${GF_DIR}/bin/asadmin"
GF_ADMIN_USER=`iniget GLASSFISH -f ${ENIQ_INI} -v GF_HostUser`
GF_ADMIN_PASSWORD_FILE="/tmp/gfpwdfile.txt"
GF_DOMAIN_FILE_BACKUP=""
TEMP_DOMAIN_FILE="/tmp/domain.xml.tmp"

init_server_type_details

DB_IQISQL_COMMAND="dbisql"
DB_DBA_PASSWORD=`iniget DB -f ${ENIQ_INI} -v DBAPassword`
DB_DC_PASSWORD=`iniget GLASSFISH_DB -f ${ENIQ_INI} -v Password`

DWH_PORT=`iniget DWH -f ${ENIQ_INI} -v PortNumber`
DWH_ENG=`iniget DWH -f ${ENIQ_INI} -v ServerName`
DB_DATASOURCE=`iniget GLASSFISH_DB -f ${ENIQ_INI} -v DataSourceClass`

DB_CONNECTION="eng=dwhdb;links=tcpip{host=${DWH_ENG};port=${DWH_PORT}};uid=dba;pwd=${DB_DBA_PASSWORD}"
DC_CONNECTION="User=dc:Password=${DB_DC_PASSWORD}:databaseName=dwhdb"
DB_MPX_SQL_FILE="/tmp/get_mpx_info_sql" 

declare -a JDBC_CONNECTIONS # will hold a list of the jdbc connections currently defined in glassfish
declare -a READER_NODES # will hold a list of available reader nodes
declare -a USEABLE_READER_NODES # will hold a list of the reader nodes that can be used by glassfish
declare -a ADDITIONAL_DATA_SOURCES # will hold a list of additional data sources
DEFAULT_READER=""

READER_IS_USEABLE=0
CONNECTION_EXISTS=0
RESTART_DOMAIN=0

# Check Arguments
if [ $# -ne 0 -a $# -ne 1 ]
then
	$ECHO "Usage: configure_glassfish_readers [status|force]"
	exit 4
fi

# Check if already running
check_file_presence $LOCK_FILE
if [ $IS_FILE_PRESENT -eq 1 ]
then
	$ECHO "One instance of this process is already running. Can not continue..." 
	exit 3
else
	touch $LOCK_FILE
fi

for domainName in $DOMAINS;
do
	if [ $domainName == "domain1" ]
	then
		GF_ADMIN_PORT=`iniget GLASSFISH -f ${ENIQ_INI} -v GF_HostAdminPort` 
	elif [ $domainName == "domain2" ]
	then
		GF_ADMIN_PORT="15050"
	fi
	
	GF_DOMAIN_FILE="${GF_DIR}/glassfish/domains/$domainName/config/domain.xml"
	if [ $# -eq 1 ]
	then
		# Run requested option
		case "$1" in 
		status) 
			gf_reader_status
				;;
		force)
			GF_FORCE_CONFIG=1
			configure_readers
				;; 
		*) 
			$ECHO "Usage : configure_glassfish_readers [status|force]"
			remove_lock_file
			exit 10
				;; 
		esac
	else
		configure_readers
	fi
done
log "Successfully configured GlassFish reader connections. Log file=${GF_LOG_FILE}"

remove_lock_file
