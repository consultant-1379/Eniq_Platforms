#!/bin/bash
# ********************************************************************
# Ericsson Radio Systems AB                                     SCRIPT
# ********************************************************************
#
#
# (c) Ericsson Radio Systems AB 2012 - All rights reserved.
#
# The copyright to the computer program(s) herein is the property
# of Ericsson Radio Systems AB, Sweden. The programs may be used
# and/or copied only with the written permission from Ericsson Radio
# Systems AB or in accordance with the terms and conditions stipulated
# in the agreement/contract under which the program(s) have been
# supplied.
#
# ********************************************************************
# Name    : glassfish_jvm_config.sh
# Date    : 05/011/2012
# Revision: A01
# Purpose : Script to update the domain.xml file with specific JVM memory settings
#			read from ini files.
#
# Usage   : Update JVM settings: glassfish_jvm_config.sh
#		  : List JVM settings: glassfish_jvm_config.sh -l
#


AWK=/usr/bin/awk
ECHO=/usr/bin/echo
CAT=/usr/bin/cat
SED=/usr/bin/sed
EGREP=/usr/bin/egrep
GREP=/usr/bin/grep
PS=/usr/bin/ps
PRTCONF=/usr/sbin/prtconf
NAWK=/usr/bin/nawk
EXPR=/usr/bin/expr

ERROR_GENERAL=1
ERROR_LIBS=3

#
# Default Glassfish JVM Settings
#
## Default -Xmx size
DEFAULT_HEAP_MIN="8192m"
## Default -Xms size
DEFAULT_HEAP_MAX="8192m"
## Default value for -XX:MaxPermSize
DEFAULT_MAX_PERM_SIZE="4096m"
## Default value for -XX:ParallelGCThreads
DEFAULT_PGC_THREADS="8"
## Default value for -XX:LargePageSizeInBytes
DEFAULT_PSB="256m"
## Default value for -XX:SurvivorRatio
DEFAULT_SURVIVORRATIO="4"
## Default value for -XX:NewRatio
DEFAULT_NEWRATIO="4"
## Default value for java.awt.headless
DEFAULT_HEADLESS="true"

NIQ_INI="$CONF_DIR/niq.ini"

GF_LOG_FILE='/eniq/log/sw_log/glassfish/security.log'
GF_DIR='/eniq/glassfish/glassfish3'
GF_ASADMIN_COMMAND="${GF_DIR}/bin/asadmin"

if [ -z ${CONF_DIR} ] ; then
	$ECHO "CONF_DIR is not set!"
	exit ${ERROR_GENERAL}
fi
. $CONF_DIR/niq.rc

GLASSFISH_DIR=/eniq/glassfish
GLASSFISH_DOMAIN_HOME="${GLASSFISH_DIR}/glassfish3/glassfish"

COMMON_FUNCTIONS="${ENIQ_CONF_DIR}/../core_install/lib/common_functions.lib"
if [ -s "$COMMON_FUNCTIONS" ] ; then
	. "$COMMON_FUNCTIONS"
else
	$ECHO "Can't source $COMMON_FUNCTIONS !" 
	exit ${ERROR_LIBS}
fi

if [ $# -ne 1 ]
then
	DEFAULT_DOMAIN="domain1"
else
	DEFAULT_DOMAIN=$1
fi

JAVA="${RT_DIR}/jdk/bin/java"
CLASSPATH="${GLASSFISH_DOMAIN_HOME}/bin/glassfish_config.jar"
CURR_SERVER_TYPE=`$CAT $ENIQ_CONF_DIR/installed_server_type | $EGREP -v '^[ 	]*#' | $SED -e 's/ //g'`

log(){
	mess=$1
	dTime=`date +'%m/%d/%Y %H:%M:%S'`
	term=`who am i | $AWK  -F' ' '{print $2}'`
	$ECHO "$dTime :: $term :: $mess " >> $GF_LOG_FILE
	$ECHO $mess
}

# Sets some of the parameter based on server type, e.g. DEFAULT_HEAP_MIN
set-params-for-server-type()
{
	if [ "${CURR_SERVER_TYPE}" == "eniq_coordinator" -o "${CURR_SERVER_TYPE}" == "eniq_ui" ]; then
		if [ $DEFAULT_DOMAIN == "domain2" ]
		then
			# Get the total memory of the server in MBytes
			_total_mem_=`$PRTCONF | $EGREP '^Memory size' | $NAWK '{print $3}'`
			
			# If memory is less than 128G ( 128 * 1024 = 131072M ) then do not set domain2 to 80G
			if [ ${_total_mem_} -lt 131072 ]; then
				# In this case, only give domain2 50% of the memory.
				REDUCED_MEM=`$EXPR ${_total_mem_} \* 50 / 100`
				DEFAULT_HEAP_MIN="${REDUCED_MEM}m"
				DEFAULT_HEAP_MAX="${REDUCED_MEM}m"				
			else
				DEFAULT_HEAP_MIN="80g"
				DEFAULT_HEAP_MAX="80g"
			fi
			DEFAULT_NEWRATIO="2"
		fi
		
		if [ $DEFAULT_DOMAIN == "domain1" ]
		then
			DEFAULT_HEAP_MIN="8g"
			DEFAULT_HEAP_MAX="8g"
		fi
	fi
}

# 
# Get a value from the file denoted by ${NIQ_INI}
#
# If no value found, return a default (if specified).
#
# Arguements:
# 	$1 - block name
#	$2 - parameter name
#	$3 - default value if nothing found in ini file.
#
ini-get-with-default()
{
	_value_=$(iniget $1 -f ${NIQ_INI} -v $2)
	if [ -z $_value_ ] ; then
		$ECHO $3
	fi
	$ECHO $_value_
}

# Generates a space seperated String with the updated VM properties
generate-jvm-options()
{
	declare -a jvm_args=('-d64' '-server' '-XX:+UseCompressedOops' '-XX:+UseParallelGC' '-XX:+UseAdaptiveSizePolicy' '-XX:+UseParallelOldGC');
	jvm_args=( "${jvm_args[@]}" "-Dcom.sun.enterprise.tools.admingui.NO_NETWORK=true");
	
	if [ "${CURR_SERVER_TYPE}" == "eniq_coordinator" -o "${CURR_SERVER_TYPE}" == "eniq_ui" ]; then
		jvm_args=( "${jvm_args[@]}" "-Xms"${DEFAULT_HEAP_MIN});
		jvm_args=( "${jvm_args[@]}" "-Xmx"${DEFAULT_HEAP_MAX});
		jvm_args=( "${jvm_args[@]}" "-XX:MaxPermSize="${DEFAULT_MAX_PERM_SIZE});
	else
		jvm_args=( "${jvm_args[@]}" "-Xms"$(ini-get-with-default GLASSFISH MinHeap ${DEFAULT_HEAP_MIN}));
		jvm_args=( "${jvm_args[@]}" "-Xmx"$(ini-get-with-default GLASSFISH MaxHeap ${DEFAULT_HEAP_MAX}));
		jvm_args=( "${jvm_args[@]}" "-XX:MaxPermSize="$(ini-get-with-default GLASSFISH MaxPermSize ${DEFAULT_MAX_PERM_SIZE}));
	fi
	
	jvm_args=( "${jvm_args[@]}" "-XX:ParallelGCThreads="$(ini-get-with-default GLASSFISH ParallelGCThreads ${DEFAULT_PGC_THREADS}));
	jvm_args=( "${jvm_args[@]}" "-XX:LargePageSizeInBytes="$(ini-get-with-default GLASSFISH LargePageSizeInBytes ${DEFAULT_PSB}));
	jvm_args=( "${jvm_args[@]}" "-XX:SurvivorRatio="$(ini-get-with-default GLASSFISH SurvivorRatio ${DEFAULT_SURVIVORRATIO}));
	jvm_args=( "${jvm_args[@]}" "-XX:NewRatio="$(ini-get-with-default GLASSFISH NewRatio ${DEFAULT_NEWRATIO}));
	jvm_args=( "${jvm_args[@]}" "-Djava.awt.headless="$(ini-get-with-default GLASSFISH headless ${DEFAULT_HEADLESS}));
	$ECHO ${jvm_args[*]}
}

generate-jvm-options-domain2()
{
	declare -a jvm_args=('-d64' '-server' '-XX:+UseParNewGC' '-XX:+DisableExplicitGC' '-XX:+UseConcMarkSweepGC' '-XX:-DontCompileHugeMethods' '-XX:+CMSClassUnloadingEnabled' '-XX:CMSInitiatingOccupancyFraction=70' '-XX:+UseCMSInitiatingOccupancyOnly');
	jvm_args=( "${jvm_args[@]}" "-Dcom.sun.enterprise.tools.admingui.NO_NETWORK=true");
	
	if [ "${CURR_SERVER_TYPE}" == "eniq_coordinator" -o "${CURR_SERVER_TYPE}" == "eniq_ui" ]; then
		jvm_args=( "${jvm_args[@]}" "-Xms"${DEFAULT_HEAP_MIN});
		jvm_args=( "${jvm_args[@]}" "-Xmx"${DEFAULT_HEAP_MAX});
		jvm_args=( "${jvm_args[@]}" "-XX:MaxPermSize="${DEFAULT_MAX_PERM_SIZE});
	else
		jvm_args=( "${jvm_args[@]}" "-Xms"$(ini-get-with-default GLASSFISH MinHeap ${DEFAULT_HEAP_MIN}));
		jvm_args=( "${jvm_args[@]}" "-Xmx"$(ini-get-with-default GLASSFISH MaxHeap ${DEFAULT_HEAP_MAX}));
		jvm_args=( "${jvm_args[@]}" "-XX:MaxPermSize="$(ini-get-with-default GLASSFISH MaxPermSize ${DEFAULT_MAX_PERM_SIZE}));
	fi
	
	jvm_args=( "${jvm_args[@]}" "-XX:ParallelGCThreads="$(ini-get-with-default GLASSFISH ParallelGCThreads ${DEFAULT_PGC_THREADS}));
	jvm_args=( "${jvm_args[@]}" "-XX:SurvivorRatio="$(ini-get-with-default GLASSFISH SurvivorRatio ${DEFAULT_SURVIVORRATIO}));
	jvm_args=( "${jvm_args[@]}" "-XX:NewRatio="$(ini-get-with-default GLASSFISH NewRatio ${DEFAULT_NEWRATIO}));
	jvm_args=( "${jvm_args[@]}" "-Djava.awt.headless="$(ini-get-with-default GLASSFISH headless ${DEFAULT_HEADLESS}));
	$ECHO ${jvm_args[*]}
}

PARSER_CLASS="com.ericsson.eniq.glassfish.DomainParserDOM"
# Update the domains domian.xml file VM properties sized to fit current machine.
update-jvm-options()
{
	if [ $DEFAULT_DOMAIN == "domain2" ]
	then
		jvm_options=$(generate-jvm-options-domain2)
		${JAVA} -classpath ${CLASSPATH} ${PARSER_CLASS} \
			--d ${DEFAULT_DOMAIN} --b "${GLASSFISH_DOMAIN_HOME}" \
			--o ${jvm_options}
	else
		jvm_options=$(generate-jvm-options)
		${JAVA} -classpath ${CLASSPATH} ${PARSER_CLASS} \
			--d ${DEFAULT_DOMAIN} --b "${GLASSFISH_DOMAIN_HOME}" \
			--o ${jvm_options}
	fi
}

# List current domains VM properties
list-jvm-options()
{
	${JAVA} -classpath ${CLASSPATH} ${PARSER_CLASS} \
		--l --d ${DEFAULT_DOMAIN} --b "${GLASSFISH_DOMAIN_HOME}"
}

# stop the glassfish domain - updates to domain.xml won't work if the domain is running 
stop_domain() {
	log "Stopping domain before updating JVM settings"
	${GF_ASADMIN_COMMAND} stop-domain ${DEFAULT_DOMAIN} >> $GF_LOG_FILE 2>&1
	asadmin_call_status=`$ECHO $?`
	if [ ${asadmin_call_status} -ne 0 ]
	then
		# problem stopping domain using asadmin so kill the process
		xPID=`$PS -ef | $GREP java | $GREP glassfish | $AWK '{print $2}'`
		if [ ! -z ${xPID} ] ; then
			kill -9 ${xPID}
			log "GlassFish ${DEFAULT_DOMAIN} process killed"
		else 
			log "GlassFish ${DEFAULT_DOMAIN} stopped"
		fi
	fi

	# double check GlassFish is down
	xPID=`$PS -ef | $GREP java | $GREP glassfish | $AWK '{print $2}'`
	if [ ! -z ${xPID} ] ; then
		log "Problem stopping ${DEFAULT_DOMAIN}."
		exit ${ERROR_GENERAL}
	fi
	
}



# Main
args=("$@")
if [ ${#args[@]} -eq 0 ] ; then
	stop_domain
	set-params-for-server-type
	update-jvm-options
elif [ ${#args[@]} -eq 1 ] ; then
	stop_domain
	set-params-for-server-type
	update-jvm-options
elif [ "${args[0]}" == "-l" ] ; then
	list-jvm-options
fi

