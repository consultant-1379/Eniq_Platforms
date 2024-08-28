#!/bin/bash
#----------------------------------------------------------------------
# Ericsson Network IQ simcfg installation script
#
# Usage: install_simcfg.sh [-v]
#
# ---------------------------------------------------------------------
# Copyright (c) 1999 - 2015 AB LM Ericsson Oy  All rights reserved.
# ---------------------------------------------------------------------

############## THE SCRIPT BEGINS HERE ##############

GREP=/usr/bin/grep
RM=/usr/bin/rm
ECHO=/usr/bin/echo
CAT=/usr/bin/cat
TEE=/usr/bin/tee
MKDIR=/usr/bin/mkdir
CHMOD=/usr/bin/chmod
CP=/usr/bin/cp

VERBOSE=0

if [ "$1" = "-v" ] ; then
  VERBOSE=1
fi

if [ -z "${CONF_DIR}" ] ; then
  $ECHO "ERROR: CONF_DIR is not set"
  exit 1
fi

. ${CONF_DIR}/niq.rc

TIMESTAMP=`date +%d.%m.%y_%H:%M:%S`

LOGFILE=${LOG_DIR}/platform_installer/simcfg_${TIMESTAMP}.log

ENIQ_ROOT_DIR=/eniq
ENIQ_BASE_DIR=${ENIQ_ROOT_DIR}

TEM_DIR=/tmp/install_feature.$$.$$

$MKDIR -p ${TEM_DIR}

. /eniq/admin/lib/common_functions.lib

if [ -f /eniq/sw/conf/sim/ ] ; then
  $RM -r /eniq/sw/conf/sim/* | $TEE -a ${LOGFILE}
fi
$CP -Rf conf/* /eniq/sw/conf/sim/ | $TEE -a ${LOGFILE}
$CHMOD -Rf 770 /eniq/sw/conf/sim/* | $TEE -a ${LOGFILE}

$ECHO "sim config module installed correctly" | $TEE -a ${LOGFILE}

# ---------------------------------------------------------------------
# Update or create versiondb.properties
# ---------------------------------------------------------------------

TVER=`$CAT install/version.properties | $GREP module.version`
TBLD=`$CAT install/version.properties | $GREP module.build`

VER=${TVER##*=}
BLD=${TBLD##*=}

VTAG="module.simcfg=${VER}b${BLD}"

if [ ! -f ${INSTALLER_DIR}/versiondb.properties ] ; then
  $ECHO "${VTAG}" > ${INSTALLER_DIR}/versiondb.properties
  $CHMOD 640 ${INSTALLER_DIR}/versiondb.properties
else
  OLD=`$CAT ${INSTALLER_DIR}/versiondb.properties | $GREP module.simcfg`
  if [ -z "${OLD}" ] ; then
    $ECHO "${VTAG}" >> ${INSTALLER_DIR}/versiondb.properties
  else
    $CP ${INSTALLER_DIR}/versiondb.properties ${INSTALLER_DIR}/versiondb.properties.tmp
    sed -e "/${OLD}/s//${VTAG}/g" ${INSTALLER_DIR}/versiondb.properties.tmp > ${INSTALLER_DIR}/versiondb.properties
    $RM ${INSTALLER_DIR}/versiondb.properties.tmp
  fi
fi

$ECHO "versiondb.properties file updated" | $TEE -a ${LOGFILE}

if [ $VERBOSE = 1 ] ; then
  $ECHO "simcfg installed" | $TEE -a ${LOGFILE}
fi

exit 0

### Function: abort_script ###
#
# This will is called if the script is aborted thru an error
# error signal sent by the kernel such as CTRL-C or if a serious
# error is encountered during runtime
#
# Arguments:
#       $1 - Error message from part of program (Not always used)
# Return Values:
#       none
abort_script()
{
_err_time_=`$DATE '+%Y-%b-%d_%H.%M.%S'`

if [ "$1" ]; then
    _err_msg_="${_err_time_} - $1"
else
    _err_msg_="${_err_time_} - ERROR : Script aborted.......\n"
fi

if [ "${LOGFILE}" ]; then
    $ECHO "\nERROR : $_err_msg_\n"|$TEE -a ${LOGFILE}
else
    $ECHO "\nERROR : $_err_msg_\n"
fi

$RM -rf ${TEM_DIR}

if [ "$2" ]; then
    ${2}
else
   exit 1
fi
}