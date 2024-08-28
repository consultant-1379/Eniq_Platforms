#!/bin/bash
# ----------------------------------------------------------------------
# Ericsson Network IQ ETLC
#
# AdminUI uses this script to show installed ENIQ platform modules
#
# ----------------------------------------------------------------------
# Copyright (c) 1999 - 2007 AB Ericsson Oy  All rights reserved.
# ----------------------------------------------------------------------

. ${CONF_DIR}/niq.rc

echo "Installed platform modules:"
cat ${INSTALLER_DIR}/versiondb.properties | grep module | sort
