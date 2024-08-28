#!/usr/bin/bash
# -------------------------------------------------------------------
# Ericsson Network IQ ETLC engine control script
#
# Usage : change_eniq_security.sh <web_module_name> disable|enable|status"
#
# -------------------------------------------------------------------
# Copyright (c) 1999 - 2012 AB Ericsson Oy All rights reserved.
# --------------------------------------------------------------------

checkFilePresence(){
	IS_FILE_PRESENT=0
	fileName=$1
	if [ -r $fileName ]
	then
		IS_FILE_PRESENT=`expr $IS_FILE_PRESENT + 1`
	else
		IS_FILE_PRESENT=`expr $IS_FILE_PRESENT + 0`
	fi
}


remove_lock_file(){
	if [ -f $LOCK_FILE ]
        then
                log "Removing Log File before exiting: $LOCK_FILE "
                rm -rf $LOCK_FILE 2>&1 > /dev/null
        fi
}


error_exit(){
	errStr=$1
	dTime=`date +'%m/%d/%Y %H:%M:%S'`
	term=`who am i | nawk  -F' ' '{print $2}'`
	echo " $dTime :: $term :: Error: $errStr " >> $HTTP_LOG_FILE
	echo " Error: $errStr !!!!Exiting script.... "
	remove_lock_file
	rm -rf $TEMP_FILE* 2>&1 > /dev/null
	rm -rf $WEBXML_FILE.backup 2>&1 > /dev/null
	rm -rf $INDEX_HTML.backup 2>&1 > /dev/null
	rm -rf $APP_JNLP.backup 2>&1 > /dev/null
	rm -rf $APP_JNLP_TEMPLATE.backup 2>&1 > /dev/null
	exit 2
}

log(){
	mess=$1
	dTime=`date +'%m/%d/%Y %H:%M:%S'`
	term=`who am i | nawk  -F' ' '{print $2}'`
	echo " $dTime :: $term :: $mess " >> $HTTP_LOG_FILE
	#echo " $mess "
}


checkDirPresence(){
	IS_DIR_PRESENT=0
	dir=$1
	if [ -d $dir ]
	then
		IS_DIR_PRESENT=`expr $IS_DIR_PRESENT + 1`
	else
		IS_DIR_PRESENT=`expr $IS_DIR_PRESENT + 0`
	fi
}

copyAndDelTempFile(){
log " Replacement was successfull. Created the TMP_FILE: $1 with the changes. "
log " Copying the TEMP_FILE: $1 to ORIG FILE: $2"
cp $1 $2
log " Copied successfully. "
if [ -f $1 ]
then
	log " Removing the TEMP_FILE: $1 "
	rm -rf $1 2>&1 > /dev/null
	log " Removed successfully."
fi
}

execCommand(){
sed $1 < $2 > $3
status=`echo $?`
if [ $status -eq 0 ]
then
	copyAndDelTempFile $3 $2
else
revert_back $2
error_exit "Error occur while enabling security in file : $2" 
fi
}	

http_enable(){
	log " Going to enable the security from HTTP to HTTPS. "
	IS_STATE_CHANGED=0
	if [ $IS_ALREADY_RUNNING -eq 1 ]
        then
                echo "One instance of this process is already running. Can not continue..."
                exit 3
    fi
	http_status_inner
	if [ $IS_HTTPS_ENABLED -eq 0 ]
	then
		take_backup $WEBXML_FILE
		execCommand 's/NONE/CONFIDENTIAL/g' $WEBXML_FILE $TEMP_FILE
		if [ -f $INDEX_HTML ]
		then
			take_backup $INDEX_HTML
			execCommand 's/http/https/g' $INDEX_HTML $TEMP_FILE_INDEX
			execCommand 's/8080/8443/g' $INDEX_HTML $TEMP_FILE_INDEX 
		fi
		if [ -f $APP_JNLP ]
		then
			take_backup $APP_JNLP
			execCommand 's/http/https/g' $APP_JNLP $TEMP_FILE_JNLP
			execCommand 's/8080/8443/g' $APP_JNLP $TEMP_FILE_JNLP
		fi
		if [ -f $APP_JNLP_TEMPLATE ]
        then
            take_backup $APP_JNLP_TEMPLATE
            execCommand 's/http/https/g' $APP_JNLP_TEMPLATE $TEMP_FILE_JNLP_TEMPLATE
            execCommand 's/8080/8443/g' $APP_JNLP_TEMPLATE $TEMP_FILE_JNLP_TEMPLATE
        fi
		IS_STATE_CHANGED=`expr $IS_STATE_CHANGED + 1`
	else
		echo " Security is already enabled. "
		IS_STATE_CHANGED=`expr $IS_STATE_CHANGED + 0`
	fi
}

http_disable() {
	IS_STATE_CHANGED=0
	log " Going to disable the security from HTTPS to HTTP. "
	if [ $IS_ALREADY_RUNNING -eq 1 ]
	then
		echo "One instance of this process is already running. Can not continue..."
        	exit 3
	fi
	http_status_inner
	if [ $IS_HTTPS_ENABLED -eq 1 ]
	then
		take_backup $WEBXML_FILE
     	execCommand 's/CONFIDENTIAL/NONE/g' $WEBXML_FILE $TEMP_FILE
		if [ -f $INDEX_HTML ]
		then
			take_backup $INDEX_HTML
            execCommand 's/https/http/g' $INDEX_HTML $TEMP_FILE_INDEX
            execCommand 's/8443/8080/g' $INDEX_HTML $TEMP_FILE_INDEX
        fi
        if [ -f $APP_JNLP ]
        then
			take_backup $APP_JNLP
            execCommand 's/https/http/g' $APP_JNLP $TEMP_FILE_JNLP
            execCommand 's/8443/8080/g' $APP_JNLP $TEMP_FILE_JNLP
        fi
        if [ -f $APP_JNLP_TEMPLATE ]
        then
            take_backup $APP_JNLP_TEMPLATE
            execCommand 's/https/http/g' $APP_JNLP_TEMPLATE $TEMP_FILE_JNLP_TEMPLATE
            execCommand 's/8443/8080/g' $APP_JNLP_TEMPLATE $TEMP_FILE_JNLP_TEMPLATE
        fi
        IS_STATE_CHANGED=`expr $IS_STATE_CHANGED + 1`
	else
		echo " Security is already disabled. "
		IS_STATE_CHANGED=`expr $IS_STATE_CHANGED + 0`
	fi
}


take_backup(){
	log "Taking backup"
	cp $1 $1.backup 2>&1 > /dev/null
	status_02=`echo $?`
	if [ $status_02 -eq 0 ]
	then
		log " Backup of original files has been taken. $1 ==> $1.backup "
		echo " Backup of oiginal files has been taken. "
	else
		echo " Error : Failed to take the backup of original files. "
		log " Error: : Failed to take the backup. $1 ==> $1.backup "
		error_exit " Failed to take the backup.   $1 ==> $1.backup "
	fi
}


revert_back(){
	if [ -f $1.backup ]
	then
		cp $1.backup $1 2>&1 > /dev/null
	fi
	status_02=`echo $?`
	if [ $status_02 -eq 0 ]
    then
			log " Reverting back original files. $1.backup ==> $1 "
			echo " Reverting back original files. "
	else
		echo " Error : Failed to revert back the original files. "
		log " Error : Failed to revert back the original files. $1.backup ==> $1 "
        fi
}


http_status_inner(){
	log " Going to check Status of Security. "
        IS_HTTPS_ENABLED=0
        isEnable=`cat $WEBXML_FILE | grep CONFIDENTIAL | wc -l`
        if [ $isEnable -ge 1 ]
        then
                IS_HTTPS_ENABLED=`expr $IS_HTTPS_ENABLED + 1`
                log " Security is enabled. HTTPS will be used by default. "
        else
                IS_HTTPS_ENABLED=`expr $IS_HTTPS_ENABLED + 0`
                log " Security is disabled. HTTP will be used by default. "
        fi
}

http_status(){
	log " Going to check Status of Security. "
	IS_HTTPS_ENABLED=0
	isEnable=`cat $WEBXML_FILE | grep CONFIDENTIAL | wc -l`
	if [ $isEnable -ge 1 ]
	then
		IS_HTTPS_ENABLED=`expr $IS_HTTPS_ENABLED + 1`
		log " Security is enabled. HTTPS will be used by default. "
		echo " Security is enabled. HTTPS will be used by default. "
	else
		IS_HTTPS_ENABLED=`expr $IS_HTTPS_ENABLED + 0`
		log " Security is disabled. HTTP will be used by default. "
		echo  " Security is disabled. HTTP will be used by default. "
	fi
}

start_webserver() {

   echo " Starting webserver "
   log "  Starting webserver "
   $WEBSERVER_COMMAND_FILE start
   output=`echo $?`
   if [ $output -eq 0 ]
   then 
         echo " Successfully started webserver "
         log " Successfully started webserver " 
   else 
         echo " Can not start webserver....Check webserver logs for more details or contact SYSTEM ADMIN...."
         log " Error : Can not start webserver....Check webserver logs for more details or contact SYSTEM ADMIN...."
   fi
}

stop_webserver() {

   echo " Stopping webserver "
   log " Stopping webserver "
   $WEBSERVER_COMMAND_FILE stop
   output=`echo $?`
   if [ $output -eq 0 ]
   then 
         echo " Successfully stopped webserver "
         log " Successfully stopped webserver "
   else 
         echo " Can not stop webserver....Check webserver logs for more details or contact  SYSTEM ADMIN...." 
         log " Error : Can not stop webserver....Check webserver logs for more details or contact SYSTEM ADMIN...."
   fi
}


handleLogFiles(){
	log " Handling Log Files. "
	if [ $IS_ALREADY_RUNNING -eq 1 ]
        then
                echo "One instance of this process is already running. Can not continue..."
                exit 3
        fi
	if [ -f $HTTP_LOG_FILE ]
	then
		dTime=`date +'%m_%d_%Y_%H::%M::%S'`
		getSize=`ls -lrt $HTTP_LOG_FILE | nawk -F' ' '{print $5}'`
		getSize=`expr $getSize + 0`
		if [ $getSize -gt 1048576 ] 
		then
			zip -9 ${HTTP_LOG_FILE}.${dTime}.zip ${HTTP_LOG_FILE} 2>&1 > /dev/null
			status=`echo $?`
			if [ $status -eq 0 ]
			then
				log " Took backup of log file : $HTTP_LOG_FILE to file: ${HTTP_LOG_FILE}.${dTime}.zip "
				echo " " > $HTTP_LOG_FILE
			else
				log " Error : Error comes while taking backup of file : $HTTP_LOG_FILE "
			fi	
		fi
	fi

	#Delete older zip  files
	checkList=`ls $HTTP_LOG_DIR | grep -i zip |  wc -l`
	checkList=`expr $checkList + 0`
	if [ $checkList -gt 0 ]
	then
		find $HTTP_LOG_FILE*.zip -mtime +3 -exec rm -rf {} \; 2>&1 > /dev/null
	fi
}


############################
###Main work starts here
############################

###
# Global Variables
###
WEBXML_FILE="/eniq/sw/runtime/tomcat/webapps/$1/WEB-INF/web.xml"
INDEX_HTML="/eniq/sw/runtime/tomcat/webapps/$1/index.html"
APP_JNLP="/eniq/sw/runtime/tomcat/webapps/$1/$1.jnlp"
APP_JNLP_TEMPLATE="/eniq/sw/runtime/tomcat/webapps/$1/$1.jnlp.template"
TEMP_FILE='/eniq/sw/installer/.httptemp.xml'
TEMP_FILE_INDEX='/eniq/sw/installer/.httpindex.html'
TEMP_FILE_JNLP='/eniq/sw/installer/.httpjnlp.html'
TEMP_FILE_JNLP_TEMPLATE='/eniq/sw/installer/.httpjnlptemplate.html'
LOCK_FILE='/eniq/sw/installer/.httplock.tmp'
HTTP_LOG_FILE='/eniq/log/sw_log/eniq_http/http.log'
HTTP_LOG_DIR='/eniq/log/sw_log/eniq_http'
WEBSERVER_COMMAND_FILE='/eniq/sw/bin/webserver'
IS_FILE_PRESENT=0
IS_DIR_PRESENT=0
IS_HTTPS_ENABLED=0
IS_STATE_CHANGED=0
IS_ALREADY_RUNNING=0

###
#Starting main functionality
###

#####
##Delete temp files, if any
####
rm -rf $TEMP_FILE* 2>&1 > /dev/null

######
## Check User. Only dcuser should be allowed to run the script
#####
isDCUSER=`id | nawk -F' ' '{print $1}' | grep -i dcuser | wc -l`
isDCUSER=`expr $isDCUSER + 0`
if [ $isDCUSER -ne 1 ]
then
	echo " This script can be run only as dcuser. "
	exit 5
fi

#####
##Check Arguments
#####
if [ $# -ne 2 ]
then
        echo "Usage: change_eniq_security.sh <web_module_name> disable|enable|status "
        exit 4
fi


####
## Check if LOG_DIR exist otherwise create it
####
checkDirPresence $HTTP_LOG_DIR
if [ $IS_DIR_PRESENT -eq 0 ]
then
	mkdir -m 777 -p $HTTP_LOG_DIR
fi

#####
## Check if already running
#####
checkFilePresence $LOCK_FILE
if [ -f $LOCK_FILE ]
then
	IS_ALREADY_RUNNING=`expr $IS_ALREADY_RUNNING + 1`
else
        touch $LOCK_FILE
        log "Created log file: $LOCK_FILE to know the running instance of this script."
fi


######
##Handling Log files
######
handleLogFiles

###
## Check if webserver file present otherwise exit
###
checkFilePresence $WEBSERVER_COMMAND_FILE
if [ $IS_FILE_PRESENT -eq 0 ]
then
	error_exit "File: $WEBSERVER_COMMAND_FILE is not present. Can not continue..."
fi


#####
## Check if aplicaion WEB_XML present otherwise exit
#####
checkFilePresence $WEBXML_FILE
if [ $IS_FILE_PRESENT -eq 0 ]
then
	error_exit "File $WEBXML_FILE is not present. Can not continue..."
fi

#if [ $IS_STATE_CHANGED -eq 0 ]  
#then
#        stop_webserver
#fi 

######
##Check the arguments
#####
case "$2" in 
enable)
	IS_STATE_CHANGED=0
	http_status_inner
        if [ $IS_HTTPS_ENABLED -eq 1 ]
        then
		IS_STATE_CHANGED=`expr $IS_STATE_CHANGED + 0`
		echo " Security is already enabled. "
	else
		IS_STATE_CHANGED=`expr $IS_STATE_CHANGED + 1`
		stop_webserver
		http_enable
        fi
     	;;
disable)
	IS_STATE_CHANGED=0
	http_status_inner
	if [ $IS_HTTPS_ENABLED -eq 0 ]
	then
		IS_STATE_CHANGED=`expr $IS_STATE_CHANGED + 0`
		echo " Security is already disabled. "
	else
		IS_STATE_CHANGED=`expr $IS_STATE_CHANGED + 1`
		stop_webserver
		http_disable
	fi
     	;;
status) 
      	http_status
     	;;
*) 
	echo " Usage :| change_eniq_security.sh <web_module_name> disable|enable|status "
   	remove_lock_file
   	exit 10
   	;; 
esac

if [ $IS_STATE_CHANGED -eq 1 ]
then
        start_webserver
fi

remove_lock_file
rm -rf $TEMP_FILE* 2>&1 > /dev/null
rm -rf $WEBXML_FILE.backup 2>&1 > /dev/null
rm -rf $INDEX_HTML.backup 2>&1 > /dev/null
rm -rf $APP_JNLP.backup 2>&1 > /dev/null
rm -rf $APP_JNLP_TEMPLATE.backup 2>&1 > /dev/null
