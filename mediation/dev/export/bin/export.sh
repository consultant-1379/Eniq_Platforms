#!/usr/sunos/bin/sh

####################################
#
# This script is used to export
# data from ETLC databases
#
####################################

if [ ${LOGNAME} != "dcuser" ]
then
  echo "This script has to be executed by dcuser"
  exit 1
fi

if [ -z "$CONF_DIR" ]
then
  CONF_DIR=/dc/dc5000/conf
  export CONF_DIR
fi

. ${CONF_DIR}/customer.rc
. ${CONF_DIR}/dc5000.rc

${ANT_HOME}/bin/ant -f ${CONF_DIR}/export.xml export -Dconf_dir=${CONF_DIR} -Ddc.deploy.dir=$DEPLOY_DIR -Da1=$1 -Da2=$2 -Da3=$3 -Da4=$4 -Da5=$5 -Da6=$6 -Da7=$7 -Da8=$8 -Da9=$9 &
