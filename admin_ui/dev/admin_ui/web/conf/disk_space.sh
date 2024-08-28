#/bin/bash
# ----------------------------------------------------------------------
# Ericsson Network IQ ETLC
#
# AdminUI uses this script to show disk space usage of the system
#
# ----------------------------------------------------------------------
# Copyright (c) 1999 - 2007 AB Ericsson Oy  All rights reserved.
# ----------------------------------------------------------------------

OSTYPE=`uname -s`
if [ ${OSTYPE} = "HP-UX" ] ; then
  df -Pk
elif [ ${OSTYPE} = "SunOS" ]; then
  df -h
else
  echo "Unknown O/S ${OSTYPE}"
fi
