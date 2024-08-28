#!/bin/bash

NAWK=/usr/bin/nawk
SVCS="/usr/bin/svcs -a"
directory="/eniq/data/pmdata_sim"
output_file="/eniq/sw/conf/sim/enginestatus.txt"
service="eniq/engine"
while true
        do
ENGSTATUS=`$SVCS | grep $service | $NAWK '{print $1}'`
DSA=`du -sk $directory | $NAWK '{print $1}' | sed 's/[.+].*//g'`
MEMSTATUS="MEMORYNOTFULL"
	if [ "$ENGSTATUS" != "online" ]; then
			engine status > /dev/null 2>&1
			if [ $? -eq 0 ] ; then
			   ENGSTATUS="online"
			fi
	fi
	#MEMSTATUS="MEMORYNOTFULL"
	if [ $DSA -gt 32000000 ]; then
			MEMSTATUS="MEMORYFULL"
	fi
	echo -e "EniqEngineService=$ENGSTATUS\nMemUsage=$MEMSTATUS" > $output_file
    sleep 300
    done;
