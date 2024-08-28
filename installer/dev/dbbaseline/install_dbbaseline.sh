#!/bin/bash
# ----------------------------------------------------------------------
# Ericsson Network IQ database baseline installer script
#
# Usage: install_dbbaseline.sh [-v] [-nodwh] [-norep]
#
# Commandline arguments:
# -nodwh   do not install DWH Database
# -norep   do not install Repository Database
#
# ----------------------------------------------------------------------
# Copyright (c) 1999 - 2007 AB Ericsson Oy  All rights reserved.
# ----------------------------------------------------------------------

if [ -z "$CONF_DIR" ] ; then
  echo "ERROR: CONF_DIR is not set"
  exit 1
fi

. ${CONF_DIR}/niq.rc

TIMESTAMP=`date +%d.%m.%y_%H:%M:%S`

LOGFILE=${LOG_DIR}/platform_installer/dbbaseline_${TIMESTAMP}.log

IQLOGDIR=${LOG_DIR}/iq/

if [ ! -d ${LOG_DIR}/platform_installer ] ; then
  mkdir -p ${LOG_DIR}/platform_installer
fi

chmod u+x admin/iniget
IQ_USER=`admin/iniget DB -v IQUserName -f ${CONF_DIR}/niq.ini`

if [ ${LOGNAME} != "${IQ_USER}" ] ; then
  echo "This script has to be executed by ${IQ_USER}"
  exit 32
fi

VERBOSE=0
DWH=1
REP=1

for arg in "$@"
do
  if [ $arg = "-v" ] ; then
    VERBOSE=1
  elif [ $arg = "-nodwh" ] ; then
    DWH=0
  elif [ $arg = "-norep" ] ; then
    REP=0
  fi
done

chmod u+x dbinit/* | tee -a ${LOGFILE}

# ----------------------------------------------------------------------
# Deploy administration scripts
# ----------------------------------------------------------------------

deployScript() {

  if [ -f ${BIN_DIR}/$1 ] ; then
    rm -f ${BIN_DIR}/$1 | tee -a ${LOGFILE}
  fi

  cp admin/$1 ${BIN_DIR}/ | tee -a ${LOGFILE}
  chmod 540 ${BIN_DIR}/$1 | tee -a ${LOGFILE}

  if [ ${VERBOSE} = 1 ] ; then
    echo "$1 deployed" | tee -a ${LOGFILE}
  fi

}

deploySMFScript() {

  if [ -f ${ADMIN_BIN}/$1 ] ; then
    rm -f ${ADMIN_BIN}/$1 | tee -a ${LOGFILE}
  fi

  cp smf/$1 ${ADMIN_BIN}/ | tee -a ${LOGFILE}
  chmod 540 ${ADMIN_BIN}/$1 | tee -a ${LOGFILE}

  if [ ${VERBOSE} = 1 ] ; then
    echo "$1 deployed to smf" | tee -a ${LOGFILE}
  fi

}


if [ ${VERBOSE} = 1 ] ; then
  echo "Deploying admin scripts..." | tee -a ${LOGFILE}
fi

deployScript iniget
deployScript propertiesget
deployScript utildb
deployScript resizedb

if [ ${DWH} = 1 ] ; then
  deployScript dwhdb
  deploySMFScript dwhdb
  deployScript backup_dwhdb
fi

if [ ${REP} = 1 ] ; then
  deployScript repdb
  deploySMFScript repdb
  deployScript backup_repdb
fi

# ----------------------------------------------------------------------
# Deploy configuration files
# ----------------------------------------------------------------------

if [ ${VERBOSE} = 1 ] ; then
  echo "Deploying configuration files..." | tee -a ${LOGFILE}
fi

if [ ${DWH} = 1 ] ; then
  if [ -f ${DWH_DIR}/dwhdb.cfg ] ; then
    if [ ${VERBOSE} = 1 ] ; then
      echo "Backuping old configuration file" | tee -a ${LOGFILE}
    fi
    mv -f ${DWH_DIR}/dwhdb.cfg ${DWH_DIR}/dwhdb.cfg.`date '+%y%m%d'` | tee -a ${LOGFILE}
  fi
  
  sed "s|@@path_to_log@@|${IQLOGDIR}|" dbinit/conf/dwhdb.cfg > ${DWH_DIR}/dwhdb.cfg  
  chmod 440 ${DWH_DIR}/dwhdb.cfg | tee -a ${LOGFILE}
fi

if [ ${REP} = 1 ] ; then
  if [ -f ${REP_DIR}/repdb.cfg ] ; then
    if [ ${VERBOSE} = 1 ] ; then
      echo "Backuping old configuration file" | tee -a ${LOGFILE}
    fi
    mv -f ${REP_DIR}/repdb.cfg ${REP_DIR}/repdb.cfg.`date '+%y%m%d'` | tee -a ${LOGFILE}
  fi
  cp dbinit/conf/repdb.cfg ${REP_DIR}/repdb.cfg | tee -a ${LOGFILE}
  chmod 440 ${REP_DIR}/repdb.cfg | tee -a ${LOGFILE}
fi

# ----------------------------------------------------------------------
# Starting Utility Server
# ----------------------------------------------------------------------

start_utildb () {
  ${BIN_DIR}/utildb status
  if [ $? != 0 ] ; then
    ${BIN_DIR}/utildb start
    if [ $? != 0 ] ; then
       echo "Util server startup failed"
       exit 10
    fi
  else
   if [ ${VERBOSE} = 1 ] ; then
     echo "Util server already running"
   fi
  fi
}

start_utildb | tee -a ${LOGFILE}

if [ ${PIPESTATUS[0]} -ne 0 ] ; then
    abortInstallation
fi

abortInstallation() {
  echo "Installation failed" | tee -a ${LOGFILE}
  ${BIN_DIR}/utildb stop | tee -a ${LOGFILE}
  exit 32
}

## MS1: Starting DB creations

# ----------------------------------------------------------------------
# Create Repository Database
# ----------------------------------------------------------------------

create_rep () {
  dbinit/create_repdb -v
  if [ $? != 0 ] ; then
    echo "Creating Repository Database failed"
    exit 1
  fi
}

dbopts_rep () {
  dbinit/dboptions_repdb -v
  if [ $? != 0 ] ; then
    echo "Repository Database option setting failed"
    exit 1
  fi
}

if [ ${REP} = 1 ] ; then

  if [ -f ${REP_DIR}/install.complete ] ; then
    echo "Repository Database already created." | tee -a ${LOGFILE}
  else

    REPLS=`ls -l ${REP_DIR}/*.db 2> /dev/null`

    if [ ! -z "${REPLS}" ] ; then
      echo "Repository Database installation was not completed before. Cleaning up..." | tee -a ${LOGFILE}
      ${BIN_DIR}/repdb stop > /dev/null 2>&1
      mv ${REP_DIR}/repdb.cfg /tmp/ | tee -a ${LOGFILE}
      rm -rf ${REP_DIR}/* | tee -a ${LOGFILE}
      mv /tmp/repdb.cfg ${REP_DIR} | tee -a ${LOGFILE}
      echo "Cleanup complete." | tee -a ${LOGFILE}
    fi

    if [ ${VERBOSE} = 1 ] ; then
      echo "Starting Repository Database creation..." | tee -a ${LOGFILE}
    fi

    create_rep | tee -a ${LOGFILE}
	    
    if [ ${PIPESTATUS[0]} -ne 0 ] ; then
    	abortInstallation
    fi
  fi

  if [ ${VERBOSE} = 1 ] ; then
    echo "Setting Repository Database db options..." | tee -a ${LOGFILE}
  fi

  dbopts_rep | tee -a ${LOGFILE}
      
  if [ ${PIPESTATUS[0]} -ne 0 ] ; then
    	abortInstallation
  fi
  if [ ! -f ${REP_DIR}/install.complete ] ; then
    REPDATE=`date '+%y%m%d_%H%M%S'`
    echo ${REPDATE} > ${REP_DIR}/install.complete | tee -a ${LOGFILE}
    chmod 440 ${REP_DIR}/install.complete | tee -a ${LOGFILE}
  fi

fi

## MS2: Repository Database created

# ----------------------------------------------------------------------
# Create Datawarehouse
# ----------------------------------------------------------------------

create_dwh () {
  dbinit/create_dwhdb -v
  if [ $? != 0 ] ; then
    echo "Creating DWH Database failed"
    exit 1
  fi
}

dbopts_dwh () {
  dbinit/dboptions_dwhdb -v
  if [ $? != 0 ] ; then
    echo "DWH Database option setting failed"
    exit 1
  fi
}

if [ ${DWH} = 1 ] ; then

  if [ -f ${DWH_DIR}/install.complete ] ; then
    echo "DWH Database already created." | tee -a ${LOGFILE}
  else

    DWHLS=`ls -l ${DWH_DIR}/*.db 2> /dev/null`

    if [ ! -z "${DWHLS}" ] ; then
      echo "DWH Database installation was not completed before. Cleaning up..." | tee -a ${LOGFILE}
      ${BIN_DIR}/dwhdb stop > /dev/null 2>&1
      mv ${DWH_DIR}/dwhdb.cfg /tmp/ | tee -a ${LOGFILE}
      rm -rf ${DWH_DIR}/* | tee -a ${LOGFILE}
      mv /tmp/dwhdb.cfg ${DWH_DIR} | tee -a ${LOGFILE}
      echo "Cleanup complete." | tee -a ${LOGFILE}
    fi

    if [ ${VERBOSE} = 1 ] ; then
      echo "Starting DWH Database creation..." | tee -a ${LOGFILE}
    fi

    create_dwh | tee -a ${LOGFILE}
    
    if [ ${PIPESTATUS[0]} -ne 0 ] ; then
    	abortInstallation
    fi

  fi

  if [ ${VERBOSE} = 1 ] ; then
    echo "Setting DWH Database db options..." | tee -a ${LOGFILE}
  fi

  dbopts_dwh | tee -a ${LOGFILE}
  
  if [ ${PIPESTATUS[0]} -ne 0 ] ; then
    	abortInstallation
  fi
  
  if [ ! -f ${DWH_DIR}/install.complete ] ; then
    DWHDATE=`date '+%y%m%d_%H%M%S'`
    echo ${DWHDATE} > ${DWH_DIR}/install.complete | tee -a ${LOGFILE}
    chmod 440 ${DWH_DIR}/install.complete | tee -a ${LOGFILE}
  fi

fi

## MS3: DWH Database created

# ---------------------------------------------------------------------
# Shutdown util server
# ---------------------------------------------------------------------

${BIN_DIR}/utildb stop | tee -a ${LOGFILE}

# ---------------------------------------------------------------------
# Update or create versiondb.properties
# ---------------------------------------------------------------------

if [ ${VERBOSE} = 1 ] ; then
  echo "Updating version database..." | tee -a ${LOGFILE}
fi

TVER=`cat install/version.properties | grep module.version`
TBLD=`cat install/version.properties | grep module.build`

VER=${TVER##*=}
BLD=${TBLD##*=}

VTAG="module.dbbaseline=${VER}b${BLD}"

if [ ! -f ${INSTALLER_DIR}/versiondb.properties ] ; then

  echo "${VTAG}" > ${INSTALLER_DIR}/versiondb.properties
  chmod 640 ${INSTALLER_DIR}/versiondb.properties

else

  OLD=`cat ${INSTALLER_DIR}/versiondb.properties | grep module.dbbaseline`

  if [ -z "${OLD}" ] ; then
    echo "${VTAG}" >> ${INSTALLER_DIR}/versiondb.properties
  else
    cp ${INSTALLER_DIR}/versiondb.properties ${INSTALLER_DIR}/versiondb.properties.tmp
    sed -e "/${OLD}/s//${VTAG}/g" ${INSTALLER_DIR}/versiondb.properties.tmp > ${INSTALLER_DIR}/versiondb.properties
    rm ${INSTALLER_DIR}/versiondb.properties.tmp
  fi

fi

if [ ${VERBOSE} = 1 ] ; then
  echo "DBBaseline installed" | tee -a ${LOGFILE}
fi

exit 0
