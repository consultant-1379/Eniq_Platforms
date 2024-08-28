#!/bin/bash
#----------------------------------------------------------------------
# Ericsson Network IQ Runtime installation script
#
# Usage: install_runtime.sh [-v]
#
# ---------------------------------------------------------------------
# Copyright (c) 1999 - 2007 AB LM Ericsson Oy  All rights reserved.
# ---------------------------------------------------------------------

############## THE SCRIPT BEGINS HERE ##############


VERBOSE=0
FORCE=0
CONFIGURED=0

###For JIRA EQEV-40496
TRUSTSTORE=/eniq/sw/runtime/jdk/jre/lib/security/
BACKUP=/var/tmp/truststore/
SU=/usr/bin/su
CHOWN=/usr/bin/chown
CHMOD=/usr/bin/chmod

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
  echo "ERROR: CONF_DIR is not set"
  exit 1
fi

if [ ! -r "${CONF_DIR}/niq.rc" ] ; then
  echo "ERROR: Source file is not readable at ${CONF_DIR}/niq.rc"
  exit 2
fi

. ${CONF_DIR}/niq.rc

if [ ! -d "${LOG_DIR}/platform_installer" ] ; then
  mkdir ${LOG_DIR}/platform_installer
fi

TIMESTAMP=`date +%d.%m.%y_%H:%M:%S`

LOG_FILE=${LOG_DIR}/platform_installer/runtime_${TIMESTAMP}.log

currdir=`pwd`
OSTYPE=$(uname -s)


UG_14A="false"
#Check to see if this install of tomcat contains the switchable renegotiation configs, if so then it is 14A+ upgrade
# If not then it is either an II or a pre 14A upgrade
if [ -a ${RT_DIR}/tomcat/conf/enable_server.xml ] ; then
	UG_14A="true"
	var_dir=/var/tmp
	#Save the current tls settings
	cp ${RT_DIR}/tomcat/conf/server.xml ${var_dir}/server.xml.save
fi

function _echo(){
	echo ${*} | tee -a ${LOG_FILE}
}
function _debug(){
	if [ $VERBOSE = 1 ] ; then
		_echo ${*}
	fi
}

IP_ADDRESS=""

# ---------------------------------------------------------------------
# JAVA installation
# ---------------------------------------------------------------------
function install_java(){

if [ -x /eniq/sw/bin/webserver ] ; then
      /eniq/sw/bin/webserver stop
fi

find ${RT_DIR}/ -type f -name .nfs\* -exec rm -f {} \;

cd $currdir
	_echo "Installing JDK"
if [[ ${OSTYPE} == CYGWIN* ]] ; then
	PROSTYPE="i386"
else
	PROSTYPE=`uname -p`
fi

	if [ -h ${RT_DIR}/jdk ] ; then
		oldversion=$(basename $(ls -l ${RT_DIR}/jdk | nawk '{print $NF}'))
		_debug "Current JDK Version is $oldversion"
		# Get the new version being installed
		jdk_pkgfile=$(cd jdk/${PROSTYPE}/; ls jdk*)
		newversion=$(basename $jdk_pkgfile .tar.gz)
		_debug "New JDK Version is $newversion"
		if [ $oldversion == $newversion -a $FORCE -eq 0 ] ; then
			_echo "JDK Version ${newversion} already installed."
			return
		elif [ -h ${RT_DIR}/jdk ] ; then
			_echo "Reinstalling JDK Version $newversion"
		fi
	fi
	
	
_echo "Installing Java"
JAVA_TMP=

if [ -h ${RT_DIR}/java ] ; then
	_debug "removing old java..."

  rm -f ${RT_DIR}/java >> ${LOG_FILE} > /dev/null 2>&1
  rm -f ${RT_DIR}/jdk >> ${LOG_FILE} > /dev/null 2>&1
  rm -rf ${RT_DIR}/jre* >> ${LOG_FILE} > /dev/null 2>&1
  rm -rf ${RT_DIR}/jdk* >> ${LOG_FILE} > /dev/null 2>&1
   
    _debug "old java removed..."


fi


  if [ ${PROSTYPE} = "i386" ]
  then
	_echo "Server Type is : ${PROSTYPE}, installing JDK on this."
	cd jdk
    cd i386
    JAVA_TMP=`ls jdk*`
  fi

cp ${JAVA_TMP} ${RT_DIR} >> ${LOG_FILE}

cd ${RT_DIR}

_debug "Extracting java..."

gunzip ${JAVA_TMP} >> ${LOG_FILE} 2>&1
JAVA_TMP=`basename ${JAVA_TMP} .gz`

_flags="xf"
if [ $VERBOSE = 1 ] ; then
	_flags="xvf"
fi

tar ${_flags} ${JAVA_TMP} >> ${LOG_FILE}

rm ${JAVA_TMP} >> ${LOG_FILE}

JAVA_DIR=`basename ${JAVA_TMP} .tar`

_debug "Linking java..."

  if [ ${PROSTYPE} = "i386" ]
  then
	_echo "Server Type is : ${PROSTYPE}, Linking Java..."
	ln -s ${JAVA_DIR} java >> ${LOG_FILE}
	ln -s ${JAVA_DIR} jdk >> ${LOG_FILE}
  fi

_debug "Java is installed."
_debug "Setting exec permissions to Java."

chmod 750 ${RT_DIR}/java/bin/*

if [ ${PROSTYPE} = "i386" ]
  	then
	chmod 750 ${RT_DIR}/jdk/bin/*
fi

_debug "Exec permissions to Java set."

JAVA_HOME=${RT_DIR}/java
export JAVA_HOME
cd $currdir
}

function install_ant(){
                                           
# ---------------------------------------------------------------------
# ANT installation
# ---------------------------------------------------------------------
cd $currdir
_echo "Installing Ant"
if [ -h ${RT_DIR}/ant ] ; then
	oldversion=$(basename $(ls -l ${RT_DIR}/ant | nawk '{print $NF}'))
	_debug "Current Ant Version is $oldversion"
	# Get the new version being installed
	ant_pkgfile=$(cd ant; ls apache-ant*)
	newversion=$(basename $ant_pkgfile -bin.tar.gz)
	_debug "New Ant Version is $newversion"
	if [ $oldversion == $newversion -a $FORCE -eq 0 ] ; then
		_echo "Ant Version ${newversion} already installed."
		return
	elif [ -h ${RT_DIR}/jdk ] ; then
		_echo "Reinstalling Ant Version $newversion"
	fi
  _debug "removing old ant..."

  rm -f ${RT_DIR}/ant | tee -a ${LOG_FILE}
  rm -rf ${RT_DIR}/apache-ant* | tee -a ${LOG_FILE}

  _debug "old ant removed..."

fi


cd ant

ANT_TMP=`ls apache-ant*`

cp ${ANT_TMP} ${RT_DIR} | tee -a ${LOG_FILE}

cd ${RT_DIR}

_debug "Extracting ant..."

gunzip ${ANT_TMP} >> ${LOG_FILE} 2>&1
ANT_TMP=`basename ${ANT_TMP} .gz`

if [ $VERBOSE = 1 ] ; then
  tar xvf ${ANT_TMP} >> ${LOG_FILE} 2>&1
else
  tar xf ${ANT_TMP} >> ${LOG_FILE} 2>&1
fi

rm ${ANT_TMP}

# List ant, remove tar file, replace all spaces with one space and then get the last column i.e. the dir name
ANT_DIR=`ls -dl apache-ant* | grep -v \.tar | tr -s ' ' ' ' | nawk -F' ' '{print $NF}'`

if [ -e ant ] ; then
  rm ant | tee -a ${LOG_FILE}
fi

_debug "Linking ant..."

ln -s ${ANT_DIR} ant | tee -a ${LOG_FILE}

_echo "Ant is installed."

cd $currdir
}

# ---------------------------------------------------------------------
# Tomcat installation
# ---------------------------------------------------------------------
function install_tomcat(){

    cd $currdir	

	_echo "Installing Tomcat ..."

	# Get the current tomcat version installed
	if [ -h ${RT_DIR}/tomcat ] ; then
		oldversion=$(basename $(ls -l ${RT_DIR}/tomcat | nawk '{print $NF}'))
	fi

	cd tomcat
	# Get the new version being installed
	tomcat_pkgfile=$(ls apache-tomcat*)

	newversion=$(basename $tomcat_pkgfile .zip)
	if [ "${oldversion}" = "${newversion}" ] ; then
		_echo "Tomcat version ${oldversion} already installed"	
		 if [ -d ${RT_DIR}/tomcat/ssl -a -f ${RT_DIR}/tomcat/ssl/keystore.jks ] ; then
				# Make a backup of the existing ssl certificates and private keys
				_debug "Backing up ssl directory"				
				cp -rf ${RT_DIR}/tomcat/ssl ${RT_DIR}/
				CONFIGURED=1				
		fi	
	else
		_echo "Installing new version ${newversion}"
		# Copy the zip file to /eniq/sw/runtime
		cp ${tomcat_pkgfile} ${RT_DIR}/
		_echo "Copying ${tomcat_pkgfile} to ${RT_DIR}"
		
		# unzip it
		cd ${RT_DIR}
		_echo "Extracting ${tomcat_pkgfile} in ${RT_DIR}"
		unzip -qq ${tomcat_pkgfile} >> ${LOG_FILE} 2>&1
		chmod 755 ${newversion}
		rm -rf ${RT_DIR}/${tomcat_pkgfile}		
			
		if [ -h ${RT_DIR}/tomcat -a -d ${RT_DIR}/tomcat/webapps ] ; then
			for webapp in $(ls ${RT_DIR}/tomcat/webapps) ; do
				if [[ ${webapp} != ROOT ]] ; then
					_debug "Copying old webapps (${webapp}) to tomcat ${newversion}"
					cp -r ${RT_DIR}/tomcat/webapps/${webapp} ${RT_DIR}/${newversion}/webapps/
				fi
			done
		fi
		if [ -h ${RT_DIR}/tomcat ] ; then
			if [ -f ${RT_DIR}/tomcat/conf/tomcat-users.xml ] ; then
				_debug "Moving up original users file"
				mv ${RT_DIR}/tomcat/conf/tomcat-users.xml ${RT_DIR}/${newversion}/conf/
			fi
			if [ -f ${RT_DIR}/tomcat/conf/Catalina/localhost/adminui.xml ] ; then
				# Copying existing user database conf file, it will get moved to the new install
				_debug "Copying original adminui.xml file"
				mkdir -p ${RT_DIR}/${newversion}/conf/Catalina/localhost/
				mv ${RT_DIR}/tomcat/conf/Catalina/localhost/adminui.xml ${RT_DIR}/${newversion}/conf/Catalina/localhost/
			fi			
			if [ -d ${RT_DIR}/tomcat/ssl -a -f ${RT_DIR}/tomcat/ssl/keystore.jks ] ; then
				# Copying existing ssl certificates and private keys
				_debug "Copying up ssl directory"				
				cp -rf ${RT_DIR}/tomcat/ssl ${RT_DIR}/${newversion}/ssl
				CONFIGURED=1				
			fi
			# This is required for AdminUI Automatic redirect
			if [ -f ${RT_DIR}/tomcat/webapps/ROOT/index.jsp ] ; then
				_debug "Copying original index.jsp file"
				cp -f ${RT_DIR}/tomcat/webapps/ROOT/index.jsp ${RT_DIR}/${newversion}/webapps/ROOT/
			fi
			# Backup web.xml file
			if [ -f ${RT_DIR}/tomcat/webapps/adminui/WEB-INF/web.xml ]; then
				_debug "Copying original web.xml file"
				cp -f ${RT_DIR}/tomcat/webapps/adminui/WEB-INF/web.xml ${RT_DIR}/${newversion}/webapps/adminui/WEB-INF/
			fi
			
			_echo "Removing the .nfs files under /eniq/sw/runtime/"
			remove_hidden_files
			find ${RT_DIR}/ -type f -name .nfs\* -exec rm -f {} \;
			
			_echo "Removing old tomcat link."
			rm -rf ${RT_DIR}/tomcat
			_echo "Removing old version tomcat"
		    rm -rf ${RT_DIR}/${oldversion}
			
			_echo "Removing the .nfs files [in any] under /eniq/sw/runtime/"
			remove_hidden_files
			
			COUNT=`find ${RT_DIR} -type f -name .nfs\* | wc -l`
				if [ $COUNT -ne 0 ]; then
					echo ".nfs files are still there. Please follow the WA available in FDD to remove them later"
				fi
			
	     	if [ -d ${RT_DIR}/${oldversion} ]; then
            	    _echo "Old version ${oldversion} was not properly removed. Please follow the WA available in FDD to remove them."
			fi

   		    if [ -d ${RT_DIR}/tomcat -o -h ${RT_DIR}/tomcat ]; then
			_echo "tomcat directory/link still available. So deleting it again"
			sleep 5
			rm -rf ${RT_DIR}/tomcat
			fi
		fi
		
		_echo "Linking to new version ${newversion}"
		ln -s ${RT_DIR}/${newversion} ${RT_DIR}/tomcat
		if [ ! -h ${RT_DIR}/tomcat ]; then
            _echo "New version tommcat link is not available. Retrying..."
			rm -rf ${RT_DIR}/tomcat
            ln -s ${RT_DIR}/${newversion} ${RT_DIR}/tomcat
        fi
		
	fi
	cd $currdir
	_debug "Copying user database ..."
	cd adminui_userdb
    cp user-database.jar ${RT_DIR}/tomcat/lib/
	cd ..
	configure_ssl
	_debug "Copying Scripts ..."
	# Copy https_security script
    cp bin/change_eniq_security.sh ${BIN_DIR}/
    chmod ug+x ${BIN_DIR}/change_eniq_security.sh
	# Copy renegotiation switch script
	cp bin/tls_switch.bsh ${RT_DIR}/tomcat/bin
    chmod ugo+x ${RT_DIR}/tomcat/bin/tls_switch.bsh
	# Copy start/stop scripts
	cp bin/webserver ${BIN_DIR}/
	chmod ug+x ${BIN_DIR}/webserver
	cp bin/before_webserver_start.xml ${BIN_DIR}/ 
	cp smf/webserver ${ADMIN_BIN}/
	chmod ug+x ${ADMIN_BIN}/webserver
	cp ${RT_DIR}/tomcat/conf/server.xml ${RT_DIR}/tomcat/conf/server_orig.xml 2>&1 | tee -a  ${getLogFileName}
    cp ${RT_DIR}/tomcat/conf/web.xml ${RT_DIR}/tomcat/conf/web_orig.xml  2>&1 | tee -a  ${getLogFileName}
    cp conf/* ${RT_DIR}/tomcat/conf/ 2>&1 | tee -a  ${getLogFileName}
	chmod 664 ${RT_DIR}/tomcat/conf/server.xml
	chmod 664 ${RT_DIR}/tomcat/conf/enable_server.xml
	chmod 664 ${RT_DIR}/tomcat/conf/disable_server.xml
	if [ -f ${RT_DIR}/tomcat/conf/server.xml -a -f ${RT_DIR}/tomcat/conf/web.xml ] ; then
	 _debug "Server and web files copied"
	else
		exit 100
	fi
	# If it is a post 14A upgrade then use the backed up file
	if [ ${UG_14A} == "true" ] ; then
		cp ${var_dir}/server.xml.save ${RT_DIR}/tomcat/conf/server.xml
		rm ${var_dir}/server.xml.save
	fi

	webserver status > /dev/null 2>&1
	if [ $? -eq 1 ] ; then
	_echo "Starting Tomcat ..."
		result=$(webserver start)
	else
		_echo "Restarting Tomcat ..."
	result=$(webserver restart)
	fi
	webserver status > /dev/null 2>&1
	res=$?
	if [ ${res} -ne 0 ] ; then
		_echo "Tomcat failed to start:"
		_echo "Reason : ${result}"
		exit 101
	else
	cd $currdir
	_echo "Tomcat is installed."
	fi
}

remove_hidden_files() {

	find ${RT_DIR} -type f -name .nfs\* -exec rm -f {} \;

	touch /tmp/nfs_list
	find ${RT_DIR} -type f -name .nfs\* >> /tmp/nfs_list

# Remove all the hidden files from above directories

	while read first_line
	do
        if [ -f "$first_line"  ]; then
			remove_file $first_line
		fi
	done < "/tmp/nfs_list"
}

remove_file(){

	local _hidden_file_=$1
		
		/usr/local/bin/lsof $_hidden_file_ | grep $_hidden_file_ | nawk -F" " '{print $2}' | sort | uniq > /tmp/nfs_list_pid
		while read PID 
		do
			kill -9 $PID > /dev/null 2>&1
			done < "/tmp/nfs_list_pid"
			
		rm -f $_hidden_file_
}

# ---------------------------------------------------------------------
# Configure Tomcat for SSL
# ---------------------------------------------------------------------
#Fix for TR HQ94390
function ip_address(){
	SERVICE_NAME=/eniq/sw/conf/service_names
	HOST_FILE=/etc/hosts
	if [ -f $SERVICE_NAME ]
	then
        echo "Reading IP from serivce_name file"
        IP_ADDRESS=`cat /eniq/sw/conf/service_names | grep webserver | nawk -F"::" '{print $1}'`
        echo "$IP_ADDRESS"
	else
        echo "Reading IP from Hosts file"
        IP_ADDRESS=`cat /etc/hosts | grep webserver | nawk -F" " '{print $1}'`
        echo "$IP_ADDRESS"
	fi
}
function configure_ssl(){
	_echo "Configuring Tomcat for SSL ..."	
	if [ ! -d ${RT_DIR}/tomcat/ssl ]; then
		mkdir ${RT_DIR}/tomcat/ssl
	fi
	if [ ! -d ${RT_DIR}/tomcat/ssl/private ]; then
		mkdir -p ${RT_DIR}/tomcat/ssl/private
	fi
	chmod og-rwx ${RT_DIR}/tomcat/ssl/private
	ENIQ_INI="niq.ini"
	ENIQ_BASE_DIR="/eniq"
	ENIQ_CONF_DIR="/eniq/sw/conf"
	COMMON_FUNCTIONS=${ENIQ_BASE_DIR}/installation/core_install/lib/common_functions.lib
	if [ -f ${COMMON_FUNCTIONS} ] ; then
        . ${COMMON_FUNCTIONS}
    else
        echo "Cant not find file ${COMMON_FUNCTIONS}"
        exit 53
    fi
	KEYSTOREPASSWORD=`iniget KEYSTOREPASS -f ${ENIQ_CONF_DIR}/${ENIQ_INI} -v keyStorePassValue`
	
	#OpenSSL Warning supress
	export OPENSSL_CONF=/etc/openssl/openssl.cnf         
	
	HOST=/usr/sbin/host 	
	HOSTNAME=`/usr/bin/hostname`
	FULLNAME=`echo \`$HOST $HOSTNAME\` | nawk '{print $1;}'`
	PRIVATEKEY=${RT_DIR}/tomcat/ssl/private/$HOSTNAME-private.pem
	PUBLICKEY=${RT_DIR}/tomcat/ssl/${HOSTNAME}_public.key
	CERTFILE=${RT_DIR}/tomcat/ssl/$HOSTNAME.cer
	CSRFILE=${RT_DIR}/tomcat/ssl/$HOSTNAME.csr
	P12KEYSTORE=${RT_DIR}/tomcat/ssl/keystore.pkcs12
	JKEYSTORE=${RT_DIR}/tomcat/ssl/keystore.jks
	OPENSSL=/usr/sfw/bin/openssl
	KEYTOOL=${RT_DIR}/java/bin/keytool
	HOSTOUTPUT=`echo \`$HOST $HOSTNAME\` | grep "has address"`
	
	if [ $CONFIGURED = 0 ] ; then
		if [ ! "${HOSTOUTPUT}" ]; then
			_echo "FULL name was not found in DNS lookup,using IP address "
			ip_address
			FULLNAME=$IP_ADDRESS
		fi

		_echo "Generating JKS Keystore"	
		$KEYTOOL -genkeypair -keystore $JKEYSTORE -storepass ${KEYSTOREPASSWORD} -alias eniq -keypass ${KEYSTOREPASSWORD} -keysize 2048 -keyalg RSA -sigalg SHA256withRSA -dname "CN=$FULLNAME" -validity 3650
		if [ $? -ne 0 ] ; then
			_echo "Failed to generate JKS Keystore. Exiting...."
			exit 0
		else
			_echo "Generating PKCS12 Keystore"
			$KEYTOOL -genkeypair -keystore $P12KEYSTORE -storetype pkcs12 -storepass ${KEYSTOREPASSWORD} -alias eniq -keypass ${KEYSTOREPASSWORD} -keysize 2048 -keyalg RSA -sigalg SHA256withRSA -dname "CN=$FULLNAME" -validity 3650
			_echo "Exporting Self_signed Certificate"
			$KEYTOOL -exportcert -keystore $JKEYSTORE -storepass ${KEYSTOREPASSWORD} -alias eniq -keypass ${KEYSTOREPASSWORD} -file $CERTFILE
			chmod 0400 $CERTFILE
			_echo "Generating Certificate Signing Request"
			$KEYTOOL -certreq -keystore $JKEYSTORE -storepass ${KEYSTOREPASSWORD} -alias eniq -keypass ${KEYSTOREPASSWORD} -file $CSRFILE		
			_echo "Generating Private key"
			$OPENSSL pkcs12 -in $P12KEYSTORE -out $PRIVATEKEY -passin pass:${KEYSTOREPASSWORD} -passout pass:${KEYSTOREPASSWORD}
			chmod 0400 $PRIVATEKEY
		fi		
		_echo "Tomcat is configured for SSL."		
	else
		_echo "Tomcat is already configured for SSL"
	fi	
	
}
function is_ssl_configured(){
	ssl_dir=${RT_DIR}/tomcat/ssl
	if [ -d ${RT_DIR}/tomcat/ssl -a -f ${RT_DIR}/tomcat/ssl/keystore.jks ] ; then	
		return 0
	else
		return 1
	fi

}

function get_version_tag(){
	if [ $# -eq 0 ] ; then
		echo "No Arguements to can_install()"
		echo "Need version file"
		return 3
	fi
	version_properties=${1}
	if [ ! -f ${version_properties} ] ; then
		echo "File ${version_properties} not found"
		return 4
	fi
	version=$(grep module.version ${version_properties} | cut -d= -f2)
	build=$(grep module.build ${version_properties} | cut -d= -f2)
	echo "${version}b${build}"
	return 0
}

#$1 the version to check e.g. R1A01b01
# Return 0 if version ${1} already installed
# Return 1 if version ${1} not installed
function is_installed(){
	if [ $# -eq 0 ] ; then
		echo "No Arguements to is_installed()"
		echo "Need version info"
		return 3
	fi
	version=${1}
	if [ -f ${INSTALLER_DIR}/versiondb.properties ] ; then
		grep "module.runtime=${version}" ${INSTALLER_DIR}/versiondb.properties > /dev/null
		return $?
	else
		return 2
	fi
	
}

# ---------------------------------------------------------------------
# Update or create versiondb.properties
# ---------------------------------------------------------------------
function update_versiondb(){


_debug "Updating version database..."
VTAG="module.runtime="$(get_version_tag install/version.properties)

if [ ! -f ${INSTALLER_DIR}/versiondb.properties ] ; then

  echo "${VTAG}" > ${INSTALLER_DIR}/versiondb.properties
  chmod 640 ${INSTALLER_DIR}/versiondb.properties

else

  OLD=$(grep module.runtime ${INSTALLER_DIR}/versiondb.properties)

  if [ -z "${OLD}" ] ; then
    echo "${VTAG}" >> ${INSTALLER_DIR}/versiondb.properties
  else
    cp ${INSTALLER_DIR}/versiondb.properties ${INSTALLER_DIR}/versiondb.properties.tmp
    sed -e "/${OLD}/s//${VTAG}/g" ${INSTALLER_DIR}/versiondb.properties.tmp > ${INSTALLER_DIR}/versiondb.properties
    rm ${INSTALLER_DIR}/versiondb.properties.tmp
  fi

fi
}

# ---------------------------------------------------------------------
# Pre java install
# ---------------------------------------------------------------------
function pre_javainstall(){





if [ -f ${TRUSTSTORE}/truststore.ts ] ; then

_echo "Taking backup of truststore.ts file before java upgrade..."
if [ ! -d ${BACKUP} ] ; then
mkdir ${BACKUP}


$CHOWN dcuser:dc5000 ${BACKUP}
$CHMOD 755 ${BACKUP}

fi

cp ${TRUSTSTORE}/truststore.ts ${BACKUP}
if [ $? == 0 ] ; then
_echo "truststore.ts file is successfully copied in the path ${BACKUP}"

else

_echo "truststore.ts file is not copied in the path ${BACKUP}"
fi
if [ -f ${BACKUP}/truststore.ts ] ; then
$CHOWN dcuser:dc5000 ${BACKUP}/truststore.ts
$CHMOD 755 ${BACKUP}/truststore.ts
fi

fi


}



# ---------------------------------------------------------------------
# Post java install
# ---------------------------------------------------------------------
function post_javainstall(){



if [ -f ${BACKUP}/truststore.ts ] ; then

_echo "Restoring of truststore.ts file after java upgrade..."
cp ${BACKUP}/truststore.ts ${TRUSTSTORE}
if [ $? == 0 ] ; then
_echo "truststore.ts file successfully restored in the path ${TRUSTSTORE}"
rm -rf ${BACKUP}
if [ $? == 0 ] ; then
_echo "${BACKUP} is successfully deleted as part of cleanup"
else
_echo "${BACKUP} is not deleted as part of cleanup"
fi
else
_echo "truststore.ts file is not restored in the path ${TRUSTSTORE}"
fi
if [ -f ${TRUSTSTORE}/truststore.ts ] ; then
$CHOWN dcuser:dc5000 ${TRUSTSTORE}/truststore.ts
$CHMOD 755 ${TRUSTSTORE}/truststore.ts
fi
fi
}


# ---------------------------------------------------------------------
# Sets tls renegotiation limit appropriately
# ---------------------------------------------------------------------
function set_tls_renegotiation(){
	echo "Disabling the TLS/SSL renegotiation"
	bash ${RT_DIR}/tomcat/bin/tls_switch.bsh disable
	if [ $? -ne 0 ] ; then
		echo "Failed to disable TLS/SSL renegotiation."
	fi
}

new_version=$(get_version_tag install/version.properties)

ok=$?

if [ ${ok} -ne 0 ] ; then
	echo ${ci}
	exit ${ok}
fi
_debug "Checking ${new_version}"
is_installed ${new_version}
is_installed_check=$?
if [ ${is_installed_check} -eq 0 -a ${FORCE} -eq 0 ] ; then
	_echo "Runtime version ${new_version} already installed."
	is_ssl_configured	
	is_configured=$?
	if [ ${is_configured} -ne 0 ] ; then				
		configure_ssl
		cp ${RT_DIR}/tomcat/conf/server.xml ${RT_DIR}/tomcat/conf/server_orig.xml
		cp conf/* ${RT_DIR}/tomcat/conf/
		webserver status > /dev/null 2>&1
		if [ $? -eq 1 ] ; then
			_echo "Starting Tomcat ..."
			result=$(webserver start)
		else
			_echo "Restarting Tomcat ..."
			result=$(webserver restart)
		fi
		webserver status > /dev/null 2>&1
		res=$?
		if [ ${res} -ne 0 ] ; then
			_echo "Tomcat failed to start:"
			_echo "Reason : ${result}" 
		fi
	else
		_echo "Tomcat is already configured for SSL"
	fi
	
	exit 0
elif [ ${is_installed_check} -eq 0 -a ${FORCE} -eq 1 ] ; then
	_echo "Runtime version ${new_version} already installed, forcing reinstall of same version."
else
	_echo "Installing/Upgrading Runtime to ${new_version}."
fi

pre_javainstall
install_java
post_javainstall
install_ant
install_tomcat
update_versiondb

# If it is just an initial install then disable tls renegotiation
if [ ${UG_14A} == "false" ] ; then
	set_tls_renegotiation
fi

_echo "Runtime now installed."
exit 0

