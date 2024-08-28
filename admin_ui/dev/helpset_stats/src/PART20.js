var C20=
"<div id=\"content\" class=\"body-content\">\n"+
"<h1><span class=\"CHAPNUMBER\">20 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_ha54\"></a><a name=\"CHAPTER20\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">FRH Snapshot/Backup and Restore</a></span></h1>\n" +
"\n" +
"\n"+
"</div>\n";

var C20_1=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">20.1 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_qf81\"></a><a name=\"CHAPTER20.1\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Backup</a></span></h2>\n" +
"\n" +
"<p>\n" +
"This FRH backup contains the necessary configuration files which\n" +
"keep on changing at run time.</p>\n" +
"\n" +
"<p>\n" +
"This backup script will keep two days backup. Older backups are\n" +
"deleted by the backup script when it executes.</p>\n" +
"\n" +
"<p>\n" +
"After Initial Install of FRH server, we need to create an entry\n" +
"in the root crontab to run a backup at every midnight. Below steps\n" +
"needs to be executed.</p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Execute the command :<p>\n" +
"<tt class=\"input\"><b># crontab -e</b></tt></p>\n" +
"</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Above command will open the crontab file, add below entry\n" +
"in the file and save it.<p>\n" +
"<tt class=\"input\"><b>0 0 * * * /ericsson/frh/installation/core_install/templates/bkup_sw/bin/frh_backup.bsh\n" +
"&gt;&gt; /dev/null 2&gt;&amp;1</b></tt></p>\n" +
"</li></ol>\n" +
"<p>\n" +
"The backup can also be run manually by running the following script\n" +
"from the command line:</p>\n" +
"\n" +
"<p>\n" +
"<tt class=\"input\"><b># /ericsson/frh/bkup_sw/bin/frh_backup.bsh</b></tt></p>\n" +
"\n" +
"<p>\n" +
"Below is the list of data which is backed up:</p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">FRH services directories :<p>\n" +
"<tt class=\"input\"><b># /ericsson/frh/controller/conf/ </b></tt></p>\n" +
"<p>\n" +
"<tt class=\"input\"><b># /ericsson/frh/flow/conf/</b></tt></p>\n" +
"</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Connectd service configuration files directory : <p>\n" +
"<tt class=\"input\"><b># /ericsson/frh/connectd/mount_info </b></tt></p>\n" +
"</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Log files directory :<p>\n" +
"<tt class=\"input\"><b># /ericsson/frh/local_logs </b></tt></p>\n" +
"</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">SSH key's configuration directory :<p>\n" +
"<tt class=\"input\"><b># /root/.ssh </b></tt></p>\n" +
"</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">FRH configuration directory :<p>\n" +
"<tt class=\"input\"><b># /ericsson/frh/installation/core_install/etc</b></tt></p>\n" +
"</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">FS list mounted on NAS :<p>\n" +
"<tt class=\"input\"><b># /etc/auto.nas </b></tt></p>\n" +
"</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Hosts file :<p>\n" +
"<tt class=\"input\"><b># /etc/hosts</b></tt></p>\n" +
"</li></ol>\n" +
"<p>\n" +
"A backup file for each of the above is created and stored in :</p>\n" +
"\n" +
"<p>\n" +
"<tt class=\"input\"><b># /ericsson/frh/backup/eniq_frh/&lt;frh-hostname&gt; </b></tt></p>\n" +
"\n" +
"\n"+
"</div>\n";

var C20_2=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">20.2 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_jlx9\"></a><a name=\"CHAPTER20.2\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Script Locations</a></span></h2>\n" +
"\n" +
"<p>\n" +
"The backup and restore scripts are located in:</p>\n" +
"\n" +
"<p><tt class=\"input\"><b># /ericsson/frh/bkup_sw/bin/</b></tt></p>\n" +
"\n"+
"</div>\n";

var C20_3=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">20.3 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_khbl\"></a><a name=\"CHAPTER20.3\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Backup Location</a></span></h2>\n" +
"\n" +
"<p>\n" +
"The backups are stored in :</p>\n" +
"\n" +
"<p><tt class=\"input\"><b># /ericsson/frh/backup/eniq_frh</b></tt></p>\n" +
"\n"+
"</div>\n";

var C20_4=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">20.4 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_t1xv\"></a><a name=\"CHAPTER20.4\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Backup and Restore Logs</a></span></h2>\n" +
"\n" +
"<p>\n" +
"The backup logs are located in:</p>\n" +
"\n" +
"<p><tt class=\"input\"><b># /ericsson/frh/log/frh_backup </b></tt></p>\n" +
"<p><tt class=\"input\"><b># /ericsson/frh/log/frh_restore </b></tt></p>\n" +
"\n"+
"</div>\n";

var C20_5=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">20.5 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_gf4s\"></a><a name=\"CHAPTER20.5\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Full Restore of FRH Server</a></span></h2>\n" +
"\n" +
"<p>\n" +
"To roll back the FRH server (for example In the event of a serious\n" +
"system failure) perform the following procedures to recover all file\n" +
"systems and restore the FRH server to the last known good state.</p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Reinstall FRH :<p>\n" +
"Follow the FRH Installation Instructions, <a href='javascript:parent.parent.parent.showAnchor(\"FRHInstallInstruction\")' class=\"xref\">Reference [29]</a>, to reinstall the operating system\n" +
"and FRH software.</p>\n" +
"</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Full Restore from a Backup.<p>\n" +
"Log on as root and execute the following:</p>\n" +
"<p>\n" +
"<tt class=\"input\"><b># /ericsson/frh/bkup_sw/bin/frh_restore.bsh</b></tt></p>\n" +
"</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Reboot the FRH server.</li></ol>\n" +
"\n" +
"\n"+
"</div>\n";

