#!/bin/bash
# ********************************************************************
# Ericsson Radio Systems AB                                     SCRIPT
# ********************************************************************
#
# (c) Ericsson Radio Systems AB 2016 - All rights reserved.
#
# The copyright to the computer program(s) herein is the property
# of Ericsson Radio Systems AB, Sweden. The programs may be used
# and/or copied only with the written permission from Ericsson Radio
# Systems AB or in accordance with the terms and conditions stipulated
# in the agreement/contract under which the program(s) have been
# supplied .
#
# ********************************************************************
# Name    : logloadcheck.bsh
# Purpose : Main script to change the log level to DEFAULT.
#
# Usage   : bash logloadcheck.bsh
#
# ********************************************************************
#
#   Command Section
#
# ********************************************************************
NAWK=/usr/bin/nawk
BASH=/usr/bin/bash
CAT=/usr/bin/cat
CHMOD=/usr/bin/chmod
CP=/usr/bin/cp
CUT=/usr/bin/cut
DATE=/usr/bin/date
ECHO=/usr/bin/echo
GREP=/usr/bin/grep
INIT=/usr/sbin/init
LS=/usr/bin/ls
MV=/usr/bin/mv
SED=/usr/bin/sed

CONF_DIR=/eniq/sw/conf
export CONF_DIR
LOG_DIR=/eniq/log/sw_log
LOG_FILE=$LOG_DIR/engine/logmanagement-`$DATE +%Y_%m_%d`.log
ENGINELOGFILE=$CONF_DIR/engineLogging.properties
ENGINELOGFILEBKUP=$CONF_DIR/engineLoggingproperties_bkup
SCHEDULERLOGFILE=$CONF_DIR/schedulerLogging.properties
SCHEDULERLOGFILEBKUP=$CONF_DIR/schedulerLoggingproperties_bkup
LICENSINGLOGFILE=$CONF_DIR/licensingLogging.properties
LICENSINGLOGFILEBKUP=$CONF_DIR/licensingLoggingproperties_bkup
FILE=$CONF_DIR/static.properties

abort_script()
{
_err_time_=`$DATE '+%Y-%b-%d_%H.%M.%S'`
    
if [ "$1" ]; then
    _err_msg_="${_err_time_} - $1" >> $LOG_FILE
else
    _err_msg_="${_err_time_} - ERROR : Script aborted.......\n"  >> $LOG_FILE
fi


}
change_schedulerlicensing_log()
{
if line=$($CAT $LICENSINGLOGFILE  |$GREP -i 'FINEST')
then
    $ECHO "Logging  to be changed to Default are  $line"  >> $LOG_FILE
else
    $ECHO "No logs in  FINEST level in licensingLogging.properties " >> $LOG_FILE 
fi
$SED "s/licensing.level=.*/licensing.level=FINE/" $LICENSINGLOGFILE >> licensinglog.properties
if [ $? -ne 0 ] ; then
    _err_msg_="Could not change log level to Default  in licensingLogging.properties" >> $LOG_FILE
    abort_script "${_err_msg_}"
fi;
$MV licensinglog.properties $LICENSINGLOGFILE
if line=$($CAT $SCHEDULERLOGFILE  |$GREP -i 'FINE')
then
    $ECHO "Logging  to be changed to Default are  $line"  >> $LOG_FILE
else
    $ECHO "No logs in  FINE level in schedulerLogging.properties  ">> $LOG_FILE 
fi
$SED "s/sun.rmi.level=.*/sun.rmi.level=WARNING/;s/ .level=.*/.level=CONFIG/" $SCHEDULERLOGFILE >> schedulerlog.properties
if [ $? -ne 0 ] ; then
    _err_msg_="Could not change log level to Default in schedulerLogging.properties  " >> $LOG_FILE
    abort_script "${_err_msg_}"
fi;
$MV schedulerlog.properties $SCHEDULERLOGFILE 
$CHMOD 640 $ENGINELOGFILE $LICENSINGLOGFILE $SCHEDULERLOGFILE
}
permission_check()
{
$LS -lrt |$GREP -i $1|$NAWK '{ print $1 }'  |while read output ;
do
if [ "$output" != "-rwxr-xr-x" ] ; then
  $CHMOD 640 $1
  $ECHO "File permission changed from $output to 640" >> $LOG_FILE
if [ $? -ne 0 ] ; then
    _err_msg_="Could not change permission" >> $LOG_FILE
    abort_script "${_err_msg_}"
fi;
fi;
done
}

change_all_values(){

if line=$($CAT $ENGINELOGFILE | $GREP  -i 'FINE')
then
    $ECHO "Logging  to be changed to INFO are  $line"  >> $LOG_FILE
else
    $ECHO "No logs in  FINE level in enginelogging.properties ">> $LOG_FILE 
fi
$SED "s/FINEST/INFO/;s/FINER/INFO/;s/FINE/INFO/"  $ENGINELOGFILE >> enginelog.properties
if [ $? -ne 0 ] ; then
    _err_msg_="Could not change log level in engineLogging.properties " >> $LOG_FILE
    abort_script "${_err_msg_}"
fi;
$MV enginelog.properties $ENGINELOGFILE
change_schedulerlicensing_log
if [ $? -ne 0 ] ; then
    _err_msg_="Could not change permission " >> $LOG_FILE
    abort_script "${_err_msg_}"
fi;
}

backup_files()
{
cp $ENGINELOGFILE $ENGINELOGFILEBKUP
cp $SCHEDULERLOGFILE $SCHEDULERLOGFILEBKUP
cp $LICENSINGLOGFILE $LICENSINGLOGFILEBKUP
}

# ********************************************************************
#
#       Main body of program
#
# ********************************************************************
#
backup_files 
permission_check $ENGINELOGFILE
permission_check $SCHEDULERLOGFILE
permission_check $LICENSINGLOGFILE
usep=$(df -h | $GREP -i '/eniq/log'|$NAWK '{ print $5}' |  $CUT -d'%' -f1  )
maxlimit=$($GREP -i 'Logging.MaxLimit' $FILE  | $CUT -f2 -d'=')
finest=$($GREP -i 'FINEST.Limit' $FILE  | $CUT -f2 -d'=')
finer=$($GREP -i 'FINER.Limit' $FILE  | $CUT -f2 -d'=')
fine=$($GREP -i 'FINE.Limit' $FILE  | $CUT -f2 -d'=')
if [ $usep -gt 100 ] || [ $finest -gt $finer ] || [ $finer -gt $fine ] || [ $fine -gt $maxlimit ] || [ $finest -gt $finer ] ;  then
        $ECHO $($DATE) " Properties values are not valid,set proper values" >> $LOG_FILE
else
if [ $usep -gt $maxlimit ]; then
  $ECHO $($DATE) "Log filesystem [/eniq/log] utilization is greater than $maxlimit ,Changing  Logging level Default" >> $LOG_FILE 
  change_all_values     
fi;
fi;
