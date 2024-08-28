#!/bin/bash
# ----------------------------------------------------------------------
# Ericsson Network IQ ETLC
#
# AdminUI uses this script to show installed ENIQ software version
#
# ----------------------------------------------------------------------
# Copyright (c) 1999 - 2007 AB Ericsson Oy  All rights reserved.
# ----------------------------------------------------------------------

. ${CONF_DIR}/niq.rc

echo "Installed ENIQ version:"
if test -f /eniq/admin/version/eniq_status 
then
	cat /eniq/admin/version/eniq_status
else
	echo "No version information available"
fi