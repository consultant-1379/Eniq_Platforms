var C18=
"<div id=\"content\" class=\"body-content\">\n"+
"<h1><span class=\"CHAPNUMBER\">18 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_1hc3\"></a><a name=\"CHAPTER18\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">ENIQ Root only Restore</a></span></h1>\n" +
"\n" +
"<p>\n" +
"The Root only restore feature has been introduced in Solaris 11,\n" +
"to recover only the root partition in case of a root corruption. This\n" +
"will ensure easy root data recovery and less time for restoring root.</p>\n" +
"\n" +
"<p>\n" +
"In Solaris 11, Root will be a part of ZFS filesystem, referred\n" +
"to as rpool.</p>\n" +
"\n" +
"\n"+
"</div>\n";

var C18_1=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">18.1 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_mhp3\"></a><a name=\"CHAPTER18.1\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Root Backup Mechanism</a></span></h2>\n" +
"\n" +
"<p>\n" +
"From solaris 11 onwards, root backup will be taken by means of\n" +
"creating a boot environment, and root restore can be done by reverting\n" +
"to a sane copy of Boot environment in case of corruption.</p>\n" +
"\n" +
"<p>\n" +
"The script checks whether the root partition is not already corrupted\n" +
"and in such cases it won't take a backup of root partition and the\n" +
"last successful backup will be retained for rollback purpose.</p>\n" +
"\n" +
"<p>\n" +
" There are two ways of creating a root backup -</p>\n" +
"\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">Integrating root backup with roll-snap</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">Manually taking root backup with script (By default\n" +
"this setting will be enabled)</li></ul>\n" +
"<p>\n" +
"To check which root backup mechanism is currently configured please\n" +
"follow below steps -	</p>\n" +
"\n" +
"<p>\n" +
"The commands must be run on all the following server types</p>\n" +
"\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">ENIQ (Standalone) Server</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Coordinator Server</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Engine Server</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Reader Server(s)</li></ul>\n" +
"<p>\n" +
"As user <tt class=\"file-path\">root</tt> on each blade, run the\n" +
"following commands -</p>\n" +
"\n" +
"<div></div>\n" +
"<p><tt class=\"input\"><b>cd /eniq/bkup_sw/etc</b></tt></p>\n" +
"<p><tt class=\"input\"><b>grep -w \"ROLL_SNAP_ROR\" eniq_backup.conf</b></tt></p>\n" +
"<p>\n" +
"If value returned from above command is -</p>\n" +
"\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">YES - Root backup is automatically integrated with roll-snap\n" +
"service and backup will be taken twice a day or as scheduled in the\n" +
"cron.</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">NO - Root backup should be triggered by user as per\n" +
"there requirement. <p>\n" +
"The procedure to backup root partition manually has been specified\n" +
"in <a href='javascript:parent.parent.parent.showAnchor(\"create_rootbkup\")' class=\"link\"> Section 18.3</a></p>\n" +
"</li></ul>\n" +
"\n"+
"</div>\n";

var C18_1_1=
"<div id=\"content\" class=\"body-content\">\n"+
"<h3><span class=\"CHAPNUMBER\">18.1.1 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_jg39\"></a><a name=\"CHAPTER18.1.1\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Integrating Root backup with roll-snap</a></span></h3>\n" +
"\n" +
"<p>\n" +
"If the root backup is not integrated with roll-snap and it needs\n" +
"to be integrated, then execute below commands on the above mentioned\n" +
"server types- </p>\n" +
"\n" +
"<p><tt class=\"input\"><b>cd /eniq/bkup_sw/etc</b></tt></p>\n" +
"<p><tt class=\"input\"><b>sed 's/ROLL_SNAP_ROR=NO/ROLL_SNAP_ROR=YES/' eniq_backup.conf\n" +
"| tee eniq_backup.conf</b></tt></p>\n" +
"<p>\n" +
"The above command will enable the root backup mechanism to be integrated\n" +
"with roll-snap service. This will ensure a sane root partition copy\n" +
"to be always present on the server.</p>\n" +
"\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>In case root partition is already corrupted before this option\n" +
"is enabled, no backup will be created. Please take a backup of eniq_backup.conf\n" +
"before proceeding with changes to avoid loss of existing file in case\n" +
"of errors.</dd></dl><br />\n" +
"\n"+
"</div>\n";

var C18_1_2=
"<div id=\"content\" class=\"body-content\">\n"+
"<h3><span class=\"CHAPNUMBER\">18.1.2 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_813t\"></a><a name=\"CHAPTER18.1.2\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Manual root backup approach</a></span></h3>\n" +
"\n" +
"<p>\n" +
"If the root backup is already integrated with roll-snap and automatic\n" +
"root backup mechanism needs to be disabled, then execute below commands\n" +
"on the above mentioned server types -</p>\n" +
"\n" +
"<p><tt class=\"input\"><b>cd /eniq/bkup_sw/etc</b></tt></p>\n" +
"<p><tt class=\"input\"><b>sed 's/ROLL_SNAP_ROR=YES/ROLL_SNAP_ROR=NO/' eniq_backup.conf\n" +
" | tee eniq_backup.conf</b></tt></p>\n" +
"<p>\n" +
"The above command will ensure that no root backup is taken unless\n" +
"user has triggered root back up manually.</p>\n" +
"\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>Please take a backup of eniq_backup.conf before proceeding\n" +
"with changes to avoid loss of existing file in case of errors</dd></dl><br />\n" +
"\n" +
"\n"+
"</div>\n";

var C18_2=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">18.2 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"rootbkup_log\"></a><a name=\"CHAPTER18.2\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Root Backup Logging</a></span></h2>\n" +
"\n" +
"<p>\n" +
"After identifying the approach of Root backup, Logs for Root backup\n" +
"would be created at different paths for different approach</p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">For Root backup integrated with roll-snap, logs are located\n" +
"in -<tt class=\"input\"><b>/eniq/local_logs/rolling_snapshot_logs/</b></tt> </li><li class=\"STEPLIST\" id=\"THSSTYLE2\">For Manual Root backup/rollback, logs are located in -<tt class=\"input\"><b>/eniq/local_logs/root_restore_logs</b></tt> in the format <tt class=\"file-path\">root_restore_&lt;date_of_backup_taken&gt;.log</tt><p>\n" +
"For example : <tt class=\"input\"><b>/eniq/local_logs/root_restore_logs/root_restore_28_Dec.log</b></tt></p>\n" +
"</li></ol>\n" +
"\n"+
"</div>\n";

var C18_3=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">18.3 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"create_rootbkup\"></a><a name=\"CHAPTER18.3\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Creating Root Backup</a></span></h2>\n" +
"\n" +
"<p>\n" +
"A Boot Environment is created as part of Root backup activity.\n" +
"In case where the root is corrupted we need to switch to a sane boot\n" +
"environment which was created during root backup activity.</p>\n" +
"\n" +
"<p>\n" +
"Below are the steps to be followed depending upon what kind of\n" +
"root backup activity is being performed -</p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Rolling Snapshot Root Backup<p>\n" +
"If root backup is integrated with roll-snap, then the backup is\n" +
"run through a daily scheduled <tt class=\"file-path\">roll-snap</tt> cron. </p>\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd><tt class=\"file-path\">roll-snap</tt> Service should be enabled\n" +
"to create the root partition backup</dd></dl><br /></li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Manual Root Backup<p>\n" +
"If manual backup of root filesystem is required then execute below\n" +
"command on the server to take Root backup. </p>\n" +
"<p>\n" +
"The commands can be run on following server types -</p>\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ (Standalone) Server</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Coordinator Server</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Engine Server</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Reader Server(s)</li></ul><dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>Root filesystem is specific to server, hence backup needs\n" +
"to be run on all servers individually</dd></dl><br /><p>\n" +
"<tt class=\"input\"><b>cd /eniq/bkup_sw/bin</b></tt></p>\n" +
"<p>\n" +
"<tt class=\"input\"><b>bash manage_root_fs.bsh -a create -n &lt;root_backup_name&gt;\n" +
"[-l &lt;path_to_logfile&gt;]</b></tt></p>\n" +
"<p>\n" +
"In the above command -</p>\n" +
"<p>\n" +
"&lt;root_backup_name&gt; parameter is the name with which Boot environment/Root\n" +
"backup will be created</p>\n" +
"<p>\n" +
"&lt;path_to_logfile&gt; parameter is optional and takes a logfile\n" +
"name along with the complete path, If not specified it creates log\n" +
"in default location as mentioned in <a href='javascript:parent.parent.parent.showAnchor(\"rootbkup_log\")' class=\"link\"> section\n" +
"18.2</a></p>\n" +
"</li></ol>\n" +
"\n"+
"</div>\n";

var C18_4=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">18.4 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_q051\"></a><a name=\"CHAPTER18.4\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Listing Root Filesystem Backup</a></span></h2>\n" +
"\n" +
"<p>\n" +
"There can be one or more root backups present on the server depending\n" +
"upon the mechanism used for root backup activity. To view the root\n" +
"backups present on the server, execute below commands -</p>\n" +
"\n" +
"<p>\n" +
"The commands can be run on following server types -</p>\n" +
"\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">ENIQ (Standalone) Server</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Coordinator Server</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Engine Server</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Reader Server(s)</li></ul>\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>Root filesystem is specific to server, hence below command\n" +
"needs to be run on all servers individually</dd></dl><br />\n" +
"<p><tt class=\"input\"><b>cd /eniq/bkup_sw/bin</b></tt></p>\n" +
"<p><tt class=\"input\"><b>bash ./manage_root_fs.bsh -a list -n ALL</b></tt></p>\n" +
"\n"+
"</div>\n";

var C18_5=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">18.5 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_a45r\"></a><a name=\"CHAPTER18.5\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Recover Root Filesystem</a></span></h2>\n" +
"\n" +
"<p>\n" +
"In case a root file system is found to be corrupted, we need to\n" +
"rollback to a previous stable root backup. We can restore the root\n" +
"partition to a previous Boot environment created as root backup</p>\n" +
"\n" +
"<p>\n" +
"To recover a root file system execute below command on the server\n" +
"where root corruption has occurred.</p>\n" +
"\n" +
"<p>\n" +
"The commands can be run on following server types -</p>\n" +
"\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">ENIQ (Standalone) Server</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Coordinator Server</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Engine Server</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Reader Server(s)</li></ul>\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>Root partition is specific to individual server/blade. Hence,\n" +
"a corruption on a particular server requires script execution to be\n" +
"done only for that server.</dd></dl><br />\n" +
"<div></div>\n" +
"<p><tt class=\"input\"><b>cd /eniq/bkup_sw/bin</b></tt></p>\n" +
"<p><tt class=\"input\"><b>bash ./manage_root_fs.bsh -a rollback</b></tt></p>\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>If there exists a single inactive Boot environment, then\n" +
"rollback will proceed without user intervention. If there are multiple\n" +
"inactive Boot Environments present on a single server/blade. In Such\n" +
"cases, script would prompt for Boot Environment name to which it should\n" +
"rollback to<p>\n" +
"Restoring root partition would require <strong class=\"MEDEMPH\">server\n" +
"reboot</strong> .</p>\n" +
"<p>\n" +
"For example - </p>\n" +
"</dd></dl><br />\n" +
"<p><tt class=\"input\"><b>bash ./manage_root_fs.bsh -a rollback</b></tt></p>\n" +
"<div></div>\n" +
"<p><tt class=\"input\"><b> -------------------------------------------------------</b></tt></p>\n" +
"<p><tt class=\"input\"><b>2017-Jan-02_12.14.54 - Starting to rollback Boot Environment</b></tt></p>\n" +
"<p><tt class=\"input\"><b>------------------------------------------------------- </b></tt></p>\n" +
"<div></div>\n" +
"<p><tt class=\"input\"><b>Are you sure you wish to rollback to previous BE </b></tt></p>\n" +
"<p><tt class=\"input\"><b>WARNING: This would require server reboot. </b></tt></p>\n" +
"<p><tt class=\"input\"><b>Enter [Yes | No] (case sensitive) : Yes </b></tt></p>\n" +
"<p><tt class=\"input\"><b>Multiple inactive BE exist, Select a BE from below list to\n" +
"Rollback to -  </b></tt></p>\n" +
"<div></div>\n" +
"<p><tt class=\"input\"><b>ROOT_BKUP1|2017-01-02|12:01</b></tt></p>\n" +
"<p><tt class=\"input\"><b>ROOT_BKUP3|2016-12-27|13:21</b></tt></p>\n" +
"<p><tt class=\"input\"><b>Enter BE name : ROOT_BKUP3</b></tt></p>\n" +
"<div></div>\n" +
"<p>\n" +
"After Successful Rollback, Current Boot Environment name changes\n" +
"and root partition is restored.</p>\n" +
"\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>d10 and d70 are reserved names for Boot Environment. Hence,\n" +
"after rollback either d10 is active or d70.</dd></dl><br />\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>After successful rollback, Server reboot is being performed,\n" +
"ensure that all the required ENIQ services are up and running post\n" +
"reboot. These should be online. In case, services are offline/disable,\n" +
"please refer to <a href='javascript:parent.parent.parent.showAnchor(\"restartEniqServices\")' class=\"xref\">Section  12.7</a> restarting eniq\n" +
"services.</dd></dl><br />\n" +
"\n" +
"\n"+
"</div>\n";

