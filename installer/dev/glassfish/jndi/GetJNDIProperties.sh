#!/bin/bash
   
declare -a JNDI_PROP_ARRAY  
index=0


if [ "$#" -ne 1 ]; then
echo "1 argument required, the path to the JNDI properties file and name. You provided $# arguments!! Command failed."
exit 1
fi


FILE_PATH_AND_NAME=$1
OUT=$(awk '{ print $1 }' $FILE_PATH_AND_NAME)
for jndi_prop in $OUT
do
	case $jndi_prop in
	*#);;              # Disregard as there is a has at the end
	#*);; 			   # Disregard as there is a has at the start 
	*#*);;             # Disregard as there is a has at the middle
	*) 
	if [ $index -eq 0 ]; then
	JNDI_PROP_ARRAY[$index]=$jndi_prop
	else
	JNDI_PROP_ARRAY[$index]=":"$jndi_prop
	fi
	index=$(( index + 1 ));; # increase number by 1
	esac
done
      	temp=${JNDI_PROP_ARRAY[*]}
		TEMP_SPACES=${temp//[[:space:]]}
		echo $TEMP_SPACES | sed -e "s/\\\_/ /g"