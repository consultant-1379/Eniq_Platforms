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
# Name    : restore_jndi_config.sh
# Date    : 30/04/2012
# Revision: A01
# Purpose : Script to update the domain.xml file with customised jndi properties
#
# Usage   : Restore JNDI Config: restore_jndi_config.sh
#

GLASSFISH_DOMAIN_HOME="${GLASSFISH_DIR}/glassfish3/glassfish"
ECHO=/usr/bin/echo
JAVA="${RT_DIR}/jdk/bin/java"
CLASSPATH="${GLASSFISH_DOMAIN_HOME}/bin/glassfish_config.jar"
GLASSFISH_DIR=/eniq/glassfish

PARSER_CLASS="com.ericsson.eniq.glassfish.DomainParserDOM"
# Update the domains domain.xml file with saved jndi properties.
restore-jndi-config()
{
	local domainXmlBackup="$1"
	local defaultDomain="$2"
	echo "$domainXmlBackup"	
	echo "${domainXmlBackup}"
	echo "$1"
	${JAVA} -classpath ${CLASSPATH} ${PARSER_CLASS} \
		--d "${defaultDomain}" --b "${GLASSFISH_DOMAIN_HOME}" \
		--p "${domainXmlBackup}"
}

echo "$1"
echo $1
restore-jndi-config "$1" "$2"