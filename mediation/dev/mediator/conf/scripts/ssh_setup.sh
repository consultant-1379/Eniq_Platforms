#!/usr/sunos/bin/ksh
# *******************************************************************
# * COPYRIGHT (c)      Ericsson AB, Sweden                          *
# *                   All rights reserved                           *
# *                                                                 *
# * The copyright to the computer program(s) herein is the property *
# * of Ericsson Radio Systems AB, Sweden.                           *
# * The programs may be used or copied only with the written        *
# * permission of Ericsson Radio Systems AB, Sweden.                *
# *                                                                 *
# *******************************************************************
#
# ssh_node_setup.sh
#
# 2010-02-19 QFREEVE
# 2010-05-20 ESEAMOR
#
# Assists in configuring the SSH connection to a node

#. /nav_base.ini

#logfile=${BASEDIR}/nav/var/esm/log/ssh_node_setup.log

#[ -f $logfile ] && rm $logfile
#touch $logfile
#chmod 666 $logfile

user=`/usr/xpg4/bin/id -u -n`
HOMEDIR=`/usr/bin/echo ~$user`
if [ "$HOMEDIR" = "/" ]; then
	HOMEDIR=""
fi
 
usage() {
  echo "Usage: $0 <user>@<host> [password]"
  exit 1
}
# Check args
  [ -z "$1" ] && usage
  [ -n "$3" ] && usage
  if [ $1 = "-help" -o $1 = "-h" -o  $1 = "help" ]; then
	usage
 fi
if [ ! -f $HOMEDIR/.ssh/id_rsa ]; then
	echo "-> Generating RSA key"
	mkdir -p $HOMEDIR/.ssh
	ssh-keygen -t rsa -P "" -f $HOMEDIR/.ssh/id_rsa >/dev/null 2>&1
fi
success=0
# Already configured?
touch /tmp/foo.$$.$user

scpSuccess=0;

	scp -qB /tmp/foo.$$.$user $1:/tmp >/dev/null 2>&1
	if [ $? != 0 ]; then
		scpSuccess=1
	fi
	
if [ $scpSuccess = 0 ]; then
	echo "----------------------"
	echo "No action: connection for $1 already setup for local user $user"
	ssh $1 "rm /tmp/foo.$$.$user" >/dev/null 2>&1
	ssh $1 "rm /tmp/foo.$$.$user" >/dev/null 2>&1
else
	echo "----------------------"
	echo "Setting up $1"
	key=`cat $HOMEDIR/.ssh/id_rsa.pub | tr -d '\n'`
	if [ ${#key} -gt 20 ]; then
				 ssh -o 'StrictHostKeyChecking no' -o 'PreferredAuthentications keyboard-interactive' $1 "mkdir -p .ssh;cat .ssh/authorized_keys | grep \"$key\" || echo \"$key\" | tee -a .ssh/authorized_keys >/dev/null; chmod 600 .ssh/authorized_keys;" >/dev/null 2>&1
	else
		echo "ERROR:Could not read SSH key from $HOMEDIR/.ssh/id_rsa.pub"
	fi

	scpSuccess=0

		scp -qB /tmp/foo.$$.$user $1:/tmp >/dev/null 2>&1
		if [ $? != 0 ]; then
			scpSuccess=1
		fi
	if [ $scpSuccess = 0 ]; then
		echo "SSH setup for $1 successful for local user $user"
		ssh $1 "rm /tmp/foo.$$.$user" >/dev/null 2>&1
		ssh $1 "rm /tmp/foo.$$.$user" >/dev/null 2>&1
	else
		echo "SSH setup for $1 failed"
		success=1
	fi
fi
[ -f /tmp/foo.$$.$user ] && rm /tmp/foo.$$.$user
exit $success