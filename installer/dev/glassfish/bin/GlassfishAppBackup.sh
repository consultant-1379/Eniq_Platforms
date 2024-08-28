#!/bin/bash

if [ "$#" -ne 2 ]; then
echo "1 argument required, the path to the glassfish application directory. You provided $# arguments!! Command failed."
exit 1
fi

temp_dir=~/glassfish_application_backup/$2

if [ ! -d "$temp_dir" ]; then
# Directory does not exist so create it
    mkdir -p $temp_dir
fi




declare -a APP_ARRAY_LIST  

APP_ARRAY_LIST=`ls $1 | grep -w 'war'`

for file_name in $APP_ARRAY_LIST
do
	cp $1/$file_name $temp_dir
done

