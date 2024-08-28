#!/bin/bash
#----------------------------------------------------------------------
# Ericsson Network IQ Runtime installation script
#
# Usage: Configure_newkeystore.sh [-v]
#
# ---------------------------------------------------------------------
# Copyright (c) 1999 - 2007 AB LM Ericsson Oy  All rights reserved.
# ---------------------------------------------------------------------

############## THE SCRIPT BEGINS HERE ##############

VERBOSE=0
FORCE=0
CONFIGURED=0
NAWK=/usr/bin/nawk
ECHO=/usr/bin/echo
LOG_DIR="/eniq/log/sw_log"
RT_DIR="/eniq/sw/runtime"
TIMESTAMP=`date +%d.%m.%y_%H:%M:%S`
ENIQ_BASE_DIR="/eniq"
CONF_DIR="/eniq/sw/conf"
SU=/usr/bin/su
HOST=/usr/sbin/host 	
HOSTNAME=`/usr/bin/hostname`
FULLNAME=`echo \`$HOST $HOSTNAME\` | $NAWK '{print $1;}'`
PRIVATEKEY=${RT_DIR}/tomcat/ssl/private/$HOSTNAME-private.pem
PUBLICKEY=${RT_DIR}/tomcat/ssl/${HOSTNAME}_public.key
CERTFILE=${RT_DIR}/tomcat/ssl/$HOSTNAME.cer
CSRFILE=${RT_DIR}/tomcat/ssl/$HOSTNAME.csr
P12KEYSTORE=${RT_DIR}/tomcat/ssl/keystore.pkcs12
JKEYSTORE=${RT_DIR}/tomcat/ssl/keystore.jks
OPENSSL=/usr/sfw/bin/openssl
KEYTOOL=${RT_DIR}/java/bin/keytool
HOSTOUTPUT=`echo \`$HOST $HOSTNAME\` | grep "has address"`
TRUSTSTORE=${RT_DIR}/jdk/jre/lib/security/truststore.ts
NIQ_INI=${CONF_DIR}/niq.ini
LIB=/eniq/admin/lib

if [ ${LOGNAME} != "root"  ] ; then
  $ECHO "This script must be executed as root"
  exit 1
fi
if [ -s /eniq/admin/lib/common_functions.lib ]; then
    . /eniq/admin/lib/common_functions.lib
else
        echo "Could not find /eniq/admin/lib/common_functions.lib"
        exit 1
fi
if [ -s ${CONF_DIR}/niq.rc ]; then
    . ${CONF_DIR}/niq.rc
else
        echo "Could not find ${CONF_DIR}/niq.rc"
        exit 1
fi
while getopts  "vfl:" flag ; do
	case $flag in
	v)
		VERBOSE=1
		;;
	f)
		FORCE=1
		;;
	l)  getLogFileName="$OPTARG"
		;;
	esac
done

if [ -z "${CONF_DIR}" ] ; then
  $ECHO "ERROR: CONF_DIR is not set"
  exit 1
fi

if [ ! -r "${CONF_DIR}/niq.rc" ] ; then
  $ECHO "ERROR: Source file is not readable at ${CONF_DIR}/niq.rc"
  exit 2
fi





if [ ! -d "${LOG_DIR}/SSL" ] ; then
  $SU - dcuser -c "mkdir ${LOG_DIR}/SSL"
fi

LOG_FILE=${LOG_DIR}/SSL/keystore_${TIMESTAMP}.log
function _debug(){
	if [ $VERBOSE = 1 ] ; then
		_echo ${*}
	fi
}
IP_ADDRESS=""
Keystore_password=""

function _echo(){
	$ECHO ${*} | tee -a ${LOG_FILE}
}
#Configure_SSL
while :; do
	unset Keystore_password_old
	unset Keystore_password_old_niq
	$ECHO "Enter Old Keystore Password:"
    read -s Keystore_password_old
	Keystore_password_old_niq=`inigetpassword KEYSTOREPASS -v keyStorePassValue -f $NIQ_INI`
	if [ "$Keystore_password_old_niq" != "$Keystore_password_old" ] ; then
		$ECHO "Old keystore password is wrong..please refer /eniq/sw/conf/niq.ini file for old keystore password"
		continue
	fi
	break
done
while :; do
	unset Keystore_password
	unset Keystore_password_reenter
	$ECHO "Enter New Keystore Password:"
    read -s Keystore_password
	$ECHO "Re-enter New Keystore Password:"
	read -s Keystore_password_reenter
	
    if [ "$Keystore_password" != "$Keystore_password_reenter" ] ; then
		$ECHO "Mismatch in new password and re-entered new password..please enter correctly "
		continue
	fi
	break
done
	_echo "Stopping Tomcat ..." 
	if [ -x /eniq/sw/bin/webserver ] ; then
     $SU - dcuser -c "/eniq/sw/bin/webserver stop"
	 _echo "Tomcat is stopped successfully"
    else
	_echo "Tomcat is not stopped"
	fi
	_echo "Configuring Tomcat for SSL ..."
	
	if [ -d ${RT_DIR}/tomcat/ssl/ssl_backup/ ] ; then 
	$SU - dcuser -c "rm -rf ${RT_DIR}/tomcat/ssl/ssl_backup"
	if [ $? -ne 0 ] ; then
	$ECHO "Error in removing ssl_backup directory "
	exit 0
	fi
	fi
	
$SU - dcuser -c "mkdir -p ${RT_DIR}/tomcat/ssl/ssl_backup"
if [ $? -ne 0 ] ; then
$ECHO "Error in creating folder under ssl folder.Exiting.."
exit 0
else
$ECHO "ssl_backup directory created.."
fi	
	
	
	shopt -s extglob; 
	cp -rf ${RT_DIR}/tomcat/ssl/!(ssl_backup)  ${RT_DIR}/tomcat/ssl/ssl_backup
	chown -R dcuser:dc5000 ${RT_DIR}/tomcat/ssl/ssl_backup

if [ $? -eq 0 ] ; then
$ECHO "All files under ssl folder is copied to ssl_backup"
else
$ECHO "Error in copying files under ssl folder.Exiting.."
exit 0
fi	
	
	
shopt -s extglob
rm -rf /eniq/sw/runtime/tomcat/ssl/!(*.jks|ssl|ssl_backup)


if [ $? -eq 0 ] ; then
$ECHO "All files under ssl folder is removed except keystore.jks"
else
$ECHO "Error in removing files under ssl folder.Exiting.."
exit 0
fi	
	
	
	if [ ! -d ${RT_DIR}/tomcat/ssl/private ]; then
		$SU - dcuser -c "mkdir -p ${RT_DIR}/tomcat/ssl/private"
	fi
	$SU - dcuser -c "chmod og-rwx ${RT_DIR}/tomcat/ssl/private"

	
	
	if [ $CONFIGURED = 0 ] ; then
		if [ ! "${HOSTOUTPUT}" ]; then
			_echo "FULL name was not found in DNS lookup,using IP address " 
			ip_address
			FULLNAME=$IP_ADDRESS
		fi

		if [ -f  $JKEYSTORE  ] ; then
			$SU - dcuser -c "keytool -storepasswd -new $Keystore_password -keystore $JKEYSTORE -storepass $Keystore_password_old"
			$SU - dcuser -c "keytool -keypasswd -keypass $Keystore_password_old -new $Keystore_password -keystore $JKEYSTORE -alias eniq -storepass $Keystore_password"
			if [ $? -ne 0 ] ; then
			$ECHO "Error in changing keystore password for JKS keystore.Exiting..."
			exit 0
			else
			$ECHO "keystore password for keystore.jks updated successfully"
			_echo "Generating PKCS12 Keystore" 
			$SU - dcuser -c "$KEYTOOL -genkeypair -keystore $P12KEYSTORE -storetype pkcs12 -storepass ${Keystore_password} -alias eniq -keypass ${Keystore_password} -keysize 2048 -keyalg RSA -sigalg SHA256withRSA -dname "CN=$FULLNAME" -validity 3650"
			_echo "Exporting Self_signed Certificate" 
			$SU - dcuser -c "$KEYTOOL -exportcert -keystore $JKEYSTORE -storepass ${Keystore_password} -alias eniq -keypass ${Keystore_password} -file $CERTFILE"
			$SU - dcuser -c "chmod 0400 $CERTFILE"
			_echo "Generating Certificate Signing Request" 
			$SU - dcuser -c "$KEYTOOL -certreq -keystore $JKEYSTORE -storepass ${Keystore_password} -alias eniq -keypass ${Keystore_password} -file $CSRFILE"		
			_echo "Generating Private key" 
			$SU - dcuser -c "$OPENSSL pkcs12 -in $P12KEYSTORE -out $PRIVATEKEY -passin pass:${Keystore_password} -passout pass:${Keystore_password}"
			$SU - dcuser -c "chmod 0400 $PRIVATEKEY"
			fi
		fi
			
		_echo "Tomcat is configured with new Keystore password."		
	else
		_echo "Tomcat is not configured with new Keystore password" 
	fi		
if [ -f  $TRUSTSTORE  ] ; then
	 $SU - dcuser -c "keytool -storepasswd -new $Keystore_password -keystore $TRUSTSTORE -storepass $Keystore_password_old"
	 if [ $? -ne 0 ] ; then
	$ECHO "Error in changing keystore password for truststore.ts"
	exit 0
else
	$ECHO "keystore password for truststore.ts updated successfully"
fi
fi
	

	
iniset KEYSTOREPASS -f $NIQ_INI keyStorePassValue=$Keystore_password
if [ $? -ne 0 ] ; then
	$ECHO "Error in updating keystore password value in niq.ini file"
	exit 0
else
	$ECHO "keystore password in niq.ini file updated successfully"
fi

$SU - dcuser -c "/eniq/sw/bin/engine restart"

if [ $? -ne 0 ] ; then
	$ECHO "Error in restarting the engine service"
	exit 0
	else
	$ECHO "Engine restarted successfully"
fi
