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
# supplied.
#
#
# ********************************************************************
# Name    : hosts_file_update.sh
# Date    : 05/07/2016
# Purpose : Script to disable alarm reports pre-upgrade and enable alarm reports post-upgrade
#           
# Usage   : hosts_file_update.sh hostip|hostname
#
# ********************************************************************
#
# Command Section
#
# ********************************************************************

rx='([1-9]?[0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])'

CAT=/usr/bin/cat
ECHO=/usr/bin/echo
NAWK=/usr/bin/nawk
GREP=/usr/bin/grep

check_duplicate_and_enter(){

	result="$($CAT /etc/hosts | $GREP $2 |$GREP webportal )" 
	if [[ $result != '' ]];then
		$ECHO $result " : Already exists"
	else
		$ECHO $1  $2  webportal >> /etc/hosts
		$ECHO "hosts file updated successfully"
	fi
}
if [[ $1 = '' ]];then
	$ECHO "Enter server name or IP address"
elif [[ $1 =~ ^$rx\.$rx\.$rx\.$rx$ ]]; then
	ip=$1
	host="$( nslookup $ip | $NAWK -F'= ' 'NR==4 { print $2 } ' | $NAWK -F'.' 'NR==1 { print $1 } ')"
	if [[ $host = '' ]];then
		$ECHO "Host not found. Check the ip address"
	else
		$ECHO "ip: " $ip
		$ECHO "host: " $host
		check_duplicate_and_enter $ip $host
	fi

elif [[ $1 =~ ^[A-Za-z0-9.]*$ ]]; then
	host=$1
	ip="$(nslookup $host | $NAWK -F': ' 'NR==5 { print $2 } ')"
	if [[ $ip = '' ]];then
		$ECHO "Host name could not be resolved"
	else
		host="$( $ECHO $host  | $NAWK -F'.' 'NR==1 { print $1 } ')"
		$ECHO "ip: " $ip
		$ECHO "host : " $host
		check_duplicate_and_enter $ip $host
	fi
fi



