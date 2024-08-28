#!/bin/bash
# ----------------------------------------------------------------------
# Ericsson Network IQ Network Analytics parser file creation Script
#
# Usage: ./NetAnFileHandler.sh
#         
#
# Author : XKUMDEY 
#
# ----------------------------------------------------------------------
# Copyright (c) 1999 - 2016 AB LM Ericsson Oy  All rights reserved.
# ----------------------------------------------------------------------

. $HOME/.profile

. ${CONF_DIR}/niq.rc

for OSS_ID in `ls ${PMDATA_DIR} | grep -i eniq_oss_`
do
        echo ${OSS_ID}
	dir=/eniq/data/pmdata/${OSS_ID}/NetworkAnalytics
	find "$dir" -depth -type d 2>/dev/null |
		while read sub; do
		# case "$sub" in   */*) ;;   *) continue ;;   esac  # sub-dir only
		[ "`cd "$sub"; echo .* * ?`" = ". .. * ?" ] || continue
		now="$(date +'%d_%m_%Y-%T')"
		echo ${now} > "$sub/${now}.txt"
	done
	
	##Create input files for parsing 
	dir=/eniq/data/pmdata/${OSS_ID}/InformationStore 
	find "$dir" -depth -type d 2>/dev/null |
		while read sub; do
		# case "$sub" in   */*) ;;   *) continue ;;   esac  # sub-dir only
		[ "`cd "$sub"; echo .* * ?`" = ". .. * ?" ] || continue
		now="$(date +'%d_%m_%Y-%T')"
		echo ${now} > "$sub/${now}.txt"
	done
	
done