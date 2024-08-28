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
# Name    : glassfish_customResource_config.sh
# Date    : 30/04/2012
# Revision: A01
# Purpose : Script to update the domain.xml file with specific original
#			custom resource property values.
#
# Usage   : Copy Custom Resources: glassfish_customResource_config.sh
#

PARSER_CLASS="com.ericsson.eniq.glassfish.DomainParserDOM"
# Update the domains domian.xml file VM properties sized to fit current machine.
copy-custom-resources()
{
	domainXmlBackup=$(filename)
	${JAVA} -classpath ${CLASSPATH} ${PARSER_CLASS} \
		--d ${DEFAULT_DOMAIN} --b "${GLASSFISH_DOMAIN_HOME}" \
		--p ${domainXmlBackup}
}