#!/usr/sunos/bin/ksh

	DU="du -hs"

        output_file="/eniq/mediator/runtime/memUsage.txt"
        directory="/eniq/mediator"

        while true
        do
		STATUS=`$DU $directory | nawk '{print $1}'`
		echo "MediatorMemUsage="$STATUS > $output_file
                sleep 50
        done