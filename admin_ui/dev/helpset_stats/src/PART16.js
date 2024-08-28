var C16=
"<div id=\"content\" class=\"body-content\">\n"+
"<h1><span class=\"CHAPNUMBER\">16 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_tah4\"></a><a name=\"CHAPTER16\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">ENIQ Rolling Snapshots</a></span></h1>\n" +
"\n"+
"</div>\n";

var C16_1=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">16.1 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_gutr\"></a><a name=\"CHAPTER16.1\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Rolling Snapshot Mechanism</a></span></h2>\n" +
"\n" +
"<p>\n" +
"The rolling snapshot is a self-maintaining mechanism. It creates\n" +
"and deletes snapshots of the ENIQ server when required. This mechanism\n" +
"is achieved using certain scripts/functions described below. </p>\n" +
"\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>These scripts/functions are designed to be run in very specific\n" +
"ways and great care should be taken if these are run manually outside\n" +
"the intended mechanism. Misuse of the scripts may cause unexpected\n" +
"results. </dd></dl><br />\n" +
"\n"+
"</div>\n";

var C16_1_1=
"<div id=\"content\" class=\"body-content\">\n"+
"<h3><span class=\"CHAPNUMBER\">16.1.1 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_t6at\"></a><a name=\"CHAPTER16.1.1\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Script Locations</a></span></h3>\n" +
"\n" +
"<p>\n" +
"The snapshot scripts are located in <tt class=\"file-path\"> /eniq/bkup_sw/bin/</tt></p>\n" +
"\n" +
"\n"+
"</div>\n";

var C16_1_2=
"<div id=\"content\" class=\"body-content\">\n"+
"<h3><span class=\"CHAPNUMBER\">16.1.2 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_8mmv\"></a><a name=\"CHAPTER16.1.2\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Rolling Snapshot Logging</a></span></h3>\n" +
"\n" +
"<p>\n" +
"The Rolling Snapshot logs are located in <tt class=\"file-path\">/eniq/local_logs/rolling_snapshot_logs/</tt>. Each of the logs is automatically rotated when the log reaches\n" +
"a certain size. By default, the number of log backup files kept is\n" +
"10 and each log is rotated when it reaches 2 MB in size. </p>\n" +
"\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">When the <tt class=\"file-path\">eniq_rollsnapd.bsh</tt> script runs, it creates the <tt class=\"file-path\">eniq_rollsnapd.log</tt> log file in the log directory.</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">When the <tt class=\"file-path\"> prepare_eniq_bkup.bsh </tt>script runs, it creates the <tt class=\"file-path\">prep_roll_snap.log</tt> log file in the log directory.</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">When the <tt class=\"file-path\">cleanup_eniq_backup.bsh</tt> script runs, it creates the <tt class=\"file-path\">clean_roll_snap.log</tt> log file in the log directory.</li></ul>\n" +
"\n"+
"</div>\n";

var C16_1_3=
"<div id=\"content\" class=\"body-content\">\n"+
"<h3><a name=\"RollingSnapshotCreation\"></a><span class=\"CHAPNUMBER\">16.1.3 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_pzau\"></a><a name=\"CHAPTER16.1.3\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Creating Rolling Snapshots</a></span></h3>\n" +
"\n" +
"<p>\n" +
"The automatic creation of the rolling snapshot(s) is controlled\n" +
"primarily by the SMF managed rolling snapshot daemon and the root\n" +
"crontab. </p>\n" +
"\n" +
"\n"+
"</div>\n";

var C16_1_3_1=
"<div id=\"content\" class=\"body-content\">\n"+
"<h4><span class=\"CHAPNUMBER\">16.1.3.1 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_7g1u\"></a><a name=\"CHAPTER16.1.3.1\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Root Crontab Entry</a></span></h4>\n" +
"\n" +
"<p>\n" +
"The root crontab periodically calls the <tt class=\"file-path\">trigger_roll_snap.bsh</tt> script that triggers the creation of a rolling snapshot. </p>\n" +
"\n" +
"<p>\n" +
" If the rolling snapshot SMF service is online, this script creates\n" +
"the file that causes the daemon to create the snapshot. This file\n" +
"is created by default every 4 hours on a rack-mounted installation\n" +
"and every 12 hours on a blade installation.</p>\n" +
"\n" +
"\n"+
"</div>\n";

var C16_1_3_2=
"<div id=\"content\" class=\"body-content\">\n"+
"<h4><span class=\"CHAPNUMBER\">16.1.3.2 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_x1rn\"></a><a name=\"CHAPTER16.1.3.2\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Rolling Snapshot Daemon</a></span></h4>\n" +
"\n" +
"<p>\n" +
"On startup, SMF loads the rolling snapshot service described in\n" +
"the file <tt class=\"file-path\"> /var/svc/manifest/eniq/runtime/roll_snap.xml</tt>.  </p>\n" +
"\n" +
"<p>\n" +
"SMF then runs the <tt class=\"file-path\">eniq_rollsnapd.bsh</tt> script to act as a daemon. This daemon periodically checks (default\n" +
"5 minutes) for the existence of a particular file which prompts for\n" +
"the creation of a rolling snapshot.</p>\n" +
"\n" +
"\n"+
"</div>\n";

var C16_1_3_3=
"<div id=\"content\" class=\"body-content\">\n"+
"<h4><span class=\"CHAPNUMBER\">16.1.3.3 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_olha\"></a><a name=\"CHAPTER16.1.3.3\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Creating Rolling Snapshots</a></span></h4>\n" +
"\n" +
"<p>\n" +
" Below are certain criteria that must be met before the rolling\n" +
"snapshot is created.</p>\n" +
"\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">All the IQ databases must be online at the time and\n" +
"the script must be able to log in to each of them.</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">The IQ dbspace files returned using the IQ command <tt class=\"input\"><b>sp_iqdbspace</b></tt> must exist and have a file size greater than\n" +
"zero.</li></ul>\n" +
"<p>\n" +
"To manually take a rolling snapshot of the system, run the following\n" +
"commands on  the server types mentioned below as user <tt class=\"file-path\">root</tt></p>\n" +
"\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">ENIQ Statistics (Standalone) Server</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Statistics Coordinator Server</li></ul>\n" +
"<p>\n" +
"<tt class=\"input\"><b># cd /eniq/bkup_sw/bin</b></tt></p>\n" +
"\n" +
"<p>\n" +
"<tt class=\"input\"><b># /usr/bin/bash ./prep_eniq_snapshots.bsh</b></tt></p>\n" +
"\n" +
"<p>\n" +
"If these criteria are met, the script takes a snapshot of each\n" +
"filesystem in a pre-determined order. For the filesystems that are\n" +
"associated with the IQ databases, the script uses the SAP IQ virtual\n" +
"backup facility to take the snapshot.</p>\n" +
"\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>To prevent the manually created snapshot from getting overwritten,\n" +
"execution of the above script will disable the roll-snap service on\n" +
"each of the server in the deployment.<p>\n" +
"To enable the roll-snap service on all the servers in the deployment,\n" +
"follow section <a href='javascript:parent.parent.parent.showAnchor(\"enabling-rolling-snapshot\")' class=\"xref\">Section  16.1.5.1</a></p>\n" +
"</dd></dl><br />\n" +
"\n" +
"\n"+
"</div>\n";

var C16_1_4=
"<div id=\"content\" class=\"body-content\">\n"+
"<h3><span class=\"CHAPNUMBER\">16.1.4 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_r6su\"></a><a name=\"CHAPTER16.1.4\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Deleting Rolling Snapshots</a></span></h3>\n" +
"\n" +
"<p>\n" +
"Old rolling snapshots are deleted automatically at the end of the\n" +
"successful creation of a new rolling snapshot.</p>\n" +
"\n" +
"<p>\n" +
"The script <tt class=\"file-path\">cleanup_eniq_backup.bsh</tt> is\n" +
"used with certain parameters to delete required rolling snapshots</p>\n" +
"\n" +
"<p>\n" +
"If an error occurs during the creation of the rolling snapshot,\n" +
"the script normally cleans up and removes any partially created snapshots.</p>\n" +
"\n" +
"<p>\n" +
"At the end of a successfully created rolling snapshot, any previous\n" +
"rolling snapshots are deleted.</p>\n" +
"\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd> It is possible for the script to delete any user created\n" +
"snapshots whose name contains the string above.</dd></dl><br />\n" +
"\n"+
"</div>\n";

var C16_1_5=
"<div id=\"content\" class=\"body-content\">\n"+
"<h3><span class=\"CHAPNUMBER\">16.1.5 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_gmw8\"></a><a name=\"CHAPTER16.1.5\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Enabling and Disabling Rolling\n" +
"Snapshots</a></span></h3>\n" +
"\n" +
"<p>\n" +
"To enable or disable rolling snapshot, run the below steps as user <tt class=\"file-path\">root</tt> on following servers:</p>\n" +
"\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">ENIQ (Standalone) Server</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Coordinator Server</li></ul>\n" +
"\n"+
"</div>\n";

var C16_1_5_1=
"<div id=\"content\" class=\"body-content\">\n"+
"<h4><a name=\"enabling-rolling-snapshot\"></a><span class=\"CHAPNUMBER\">16.1.5.1 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_65oy\"></a><a name=\"CHAPTER16.1.5.1\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Enabling\n" +
"Rolling Snapshot SMF Service</a></span></h4>\n" +
"\n" +
"<p>\n" +
"Enable the rolling snapshot service by issuing the following command\n" +
"as user <tt class=\"file-path\">root</tt></p>\n" +
"\n" +
"<p><tt class=\"input\"><b># cd /eniq/admin/bin</b></tt></p>\n" +
"<p><tt class=\"input\"><b># bash ./manage_deployment_services.bsh -a start -s roll-snap</b></tt></p>\n" +
"\n"+
"</div>\n";

var C16_1_5_2=
"<div id=\"content\" class=\"body-content\">\n"+
"<h4><span class=\"CHAPNUMBER\">16.1.5.2 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_djca\"></a><a name=\"CHAPTER16.1.5.2\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Disabling Rolling Snapshot SMF\n" +
"Service</a></span></h4>\n" +
"\n" +
"<p>\n" +
"Disable the rolling snapshot service by issuing the following command\n" +
"as user <tt class=\"file-path\">root</tt></p>\n" +
"\n" +
"<p><tt class=\"input\"><b># cd /eniq/admin/bin</b></tt></p>\n" +
"<p><tt class=\"input\"><b># bash ./manage_deployment_services.bsh -a stop -s roll-snap</b></tt></p>\n" +
"\n" +
"\n" +
"\n"+
"</div>\n";

var C16_2=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><a name=\"RollingSnapshotRecovery\"></a><span class=\"CHAPNUMBER\">16.2 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_trfl\"></a><a name=\"CHAPTER16.2\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Recover Using Rolling Snapshots</a></span></h2>\n" +
"\n" +
"<p>\n" +
"If certain types of system failure occur, for example, database\n" +
"corruption or accidental file deletion, rolling snapshots can be used\n" +
"to restore the system to a working state.</p>\n" +
"\n" +
"<p>\n" +
" When using the rolling snapshots to recover from a fault scenario,\n" +
"first, disable the creation of any more rolling snapshots.  Disabling\n" +
"the snapshot SMF service (described previously in this guide) ensures\n" +
"that no more snapshots are taken. </p>\n" +
"\n" +
"<p>\n" +
"The reason for this is that you do not want the rolling snapshot\n" +
"to snapshot the fault scenario for example, if a user deletes a file\n" +
"and the automatic snapshot mechanism snapshots again before the file\n" +
"has been recovered, then the new snapshot will not contain the deleted\n" +
"file.</p>\n" +
"\n" +
"\n"+
"</div>\n";

var C16_2_1=
"<div id=\"content\" class=\"body-content\">\n"+
"<h3><span class=\"CHAPNUMBER\">16.2.1 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_0pom\"></a><a name=\"CHAPTER16.2.1\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Recovering the Entire System</a></span></h3>\n" +
"\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>If ENIQ is installed on a rack server then, you only have\n" +
"ZFS snapshots to rollback</dd></dl><br />\n" +
"<p>\n" +
"If the fault is so serious that the recovery of all the filesystems\n" +
"is required, then the following procedure should ensure the recovery\n" +
"of the ENIQ server to a last known good state. </p>\n" +
"\n" +
"\n"+
"</div>\n";

var C16_2_1_1=
"<div id=\"content\" class=\"body-content\">\n"+
"<h4><span class=\"CHAPNUMBER\">16.2.1.1 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_cuj5\"></a><a name=\"CHAPTER16.2.1.1\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Disabling Mounts</a></span></h4>\n" +
"\n" +
"<p>\n" +
"This command must be run for each managed OSS. The following steps\n" +
"must be run on all server types.</p>\n" +
"\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">ENIQ Statistics (Standalone) Server</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Statistics Engine Server</li></ul>\n" +
"<p><tt class=\"input\"><b># cd /eniq/connectd/mount_info/</b></tt></p>\n" +
"<p><tt class=\"input\"><b># /usr/bin/touch &lt;eniq_alias&gt;/disable_OSS</b></tt></p>\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd><tt class=\"file-path\">eniq_alias</tt>refers to the given\n" +
"name of the OSS server by ENIQ Statistics. This is in the form <tt class=\"file-path\">eniq_oss_&lt;n&gt;</tt></dd></dl><br />\n" +
"\n"+
"</div>\n";

var C16_2_1_2=
"<div id=\"content\" class=\"body-content\">\n"+
"<h4><span class=\"CHAPNUMBER\">16.2.1.2 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_18a2\"></a><a name=\"CHAPTER16.2.1.2\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Disabling Rolling Snapshot SMF\n" +
"Service</a></span></h4>\n" +
"\n" +
"<p>\n" +
"The commands must be run on following server types</p>\n" +
"\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">ENIQ (Standalone) Server</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Coordinator Server</li></ul>\n" +
"<p>\n" +
"Issue the following command as user <tt class=\"file-path\">root</tt> <br /><tt class=\"input\"><b># cd /</b></tt></p>\n" +
"\n" +
"<p><tt class=\"input\"><b># bash /eniq/admin/bin/manage_deployment_services.bsh -a stop\n" +
"-s roll-snap</b></tt></p>\n" +
"\n"+
"</div>\n";

var C16_2_1_3=
"<div id=\"content\" class=\"body-content\">\n"+
"<h4><span class=\"CHAPNUMBER\">16.2.1.3 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_mgah\"></a><a name=\"CHAPTER16.2.1.3\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Disabling All ENIQ Services</a></span></h4>\n" +
"\n" +
"<p>\n" +
"The commands must be run on following server types</p>\n" +
"\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">ENIQ (Standalone) Server</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Coordinator Server</li></ul>\n" +
"<p>\n" +
"Issue the following command as user <tt class=\"file-path\">root</tt> <br /><tt class=\"input\"><b># cd /</b></tt></p>\n" +
"\n" +
"<p><tt class=\"input\"><b># bash /eniq/admin/bin/manage_deployment_services.bsh -a stop\n" +
"-s ALL [-l &lt;path_to_logfile&gt;]</b></tt></p>\n" +
"<p>\n" +
"Check that all services are stopped (disabled) by using the following\n" +
"command <br /><tt class=\"input\"><b># /usr/bin/svcs &ndash;a | grep eniq</b></tt></p>\n" +
"\n" +
"\n"+
"</div>\n";

var C16_2_1_4=
"<div id=\"content\" class=\"body-content\">\n"+
"<h4><span class=\"CHAPNUMBER\">16.2.1.4 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_56dw\"></a><a name=\"CHAPTER16.2.1.4\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Rolling Back ZFS Rolling Snapshot(s)</a></span></h4>\n" +
"\n" +
"<p>\n" +
"The commands must be run on following server types</p>\n" +
"\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">ENIQ (Standalone) Server</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Coordinator Server</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Engine Server</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Reader Server(s)</li></ul>\n" +
"<p>\n" +
"Issue the following command as user <tt class=\"file-path\">root</tt><br /></p>\n" +
"\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">List ZFS rolling snapshots to determine the <tt class=\"file-path\">&lt;snap_label&gt;</tt><p>\n" +
"<tt class=\"input\"><b># cd /</b></tt></p>\n" +
"<p>\n" +
"<tt class=\"input\"><b># /eniq/bkup_sw/bin/manage_zfs_snapshots.bsh -a list -f\n" +
"ALL</b></tt></p>\n" +
"</li></ul>\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">Rollback rolling snapshot<p>\n" +
"<tt class=\"input\"><b># cd /</b></tt><br /><tt class=\"input\"><b># bash /eniq/bkup_sw/bin/manage_zfs_snapshots.bsh\n" +
"&ndash;a rollback &ndash;f  ALL=\"<i class=\"var\">&lt;snap_label&gt;</i>\" [-l <i class=\"var\">&lt;path_to_logfile&gt;</i>]</b></tt></p>\n" +
"</li></ul>\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">Remount ZFS filesystems<p>\n" +
"<tt class=\"input\"><b># /usr/sbin/zfs mount -a</b></tt>  </p>\n" +
"</li></ul>\n" +
"\n"+
"</div>\n";

var C16_2_1_5=
"<div id=\"content\" class=\"body-content\">\n"+
"<h4><span class=\"CHAPNUMBER\">16.2.1.5 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_e8r7\"></a><a name=\"CHAPTER16.2.1.5\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Rolling Back NAS Rolling Snapshot</a></span></h4>\n" +
"\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>This step is applicable only for ENIQ Statistics Blade/Multi-Blade\n" +
"servers.</dd></dl><br />\n" +
"<p>\n" +
"The commands must be run on following server types</p>\n" +
"\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">ENIQ (Standalone) Server</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Coordinator Server</li></ul>\n" +
"<p>\n" +
"Issue the following command as user <tt class=\"file-path\">root</tt> <br /><tt class=\"input\"><b># cd /</b></tt></p>\n" +
"\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">List NAS rolling snapshots to determine the <tt class=\"file-path\">&lt;snap_label&gt;</tt><p>\n" +
"<tt class=\"input\"><b># cd /</b></tt><br /></p>\n" +
"<p>\n" +
"<tt class=\"input\"><b># /usr/bin/bash /eniq/bkup_sw/bin/manage_nas_snapshots.bsh\n" +
"-a list -f ALL</b></tt></p>\n" +
"</li></ul>\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">Stop the NAS service and milestone<p>\n" +
"<tt class=\"input\"><b># /usr/sbin/svcadm disable svc:/storage/NASd:default</b></tt>  </p>\n" +
"</li></ul>\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">Check that the NAS service and milestone are stopped\n" +
"(disabled) <p>\n" +
"<br /><tt class=\"input\"><b># /usr/bin/svcs &ndash;a | grep NAS</b></tt></p>\n" +
"<p>\n" +
"<tt class=\"output\">disabled       16:43:42 svc:/milestone/NAS-online:default </tt></p>\n" +
"<p>\n" +
"<tt class=\"output\">disabled       16:43:30 svc:/storage/NASd:default</tt>	 </p>\n" +
"<p>\n" +
"<strong class=\"MEDEMPH\">Note</strong>: It can be a few minutes before\n" +
"all the services are disabled.</p>\n" +
"</li></ul>\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">Rollback Snapshot<p>\n" +
"<tt class=\"input\"><b># cd /</b></tt><br /><tt class=\"input\"><b># bash /eniq/bkup_sw/bin/manage_nas_snapshots.bsh\n" +
"-a rollback -f ALL -n <i class=\"var\">&lt;snap_label&gt;</i> [-l <i class=\"var\">&lt;path_to_logfile&gt;</i>]</b></tt></p>\n" +
"</li></ul>\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">Start the NAS service and milestone<p>\n" +
"<tt class=\"input\"><b># /usr/sbin/svcadm enable svc:/storage/NASd:default</b></tt>  </p>\n" +
"</li></ul>\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">Check that NAS service and milestone are started (enabled)<p>\n" +
"<tt class=\"input\"><b># /usr/bin/svcs &ndash;a | grep NAS</b></tt></p>\n" +
"<p>\n" +
"<tt class=\"output\">online         21:20:08 svc:/storage/NASd:default</tt></p>\n" +
"<p>\n" +
"<tt class=\"output\">online         21:21:21 svc:/milestone/NAS-online:default</tt>	 </p>\n" +
"<p>\n" +
"<strong class=\"MEDEMPH\">Note</strong>: It can be a few minutes before\n" +
"the services are started.</p>\n" +
"</li></ul>\n" +
"\n"+
"</div>\n";

var C16_2_1_6=
"<div id=\"content\" class=\"body-content\">\n"+
"<h4><span class=\"CHAPNUMBER\">16.2.1.6 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_aex5\"></a><a name=\"CHAPTER16.2.1.6\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Rolling Back SAN Rolling Snapshot</a></span></h4>\n" +
"\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>These steps are only applicable for ENIQ Statistics Blade/Multi-Blade\n" +
"servers.</dd></dl><br />\n" +
"<p>\n" +
"The commands must be run on following server types</p>\n" +
"\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">ENIQ (Standalone) Server</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Coordinator Server</li></ul>\n" +
"<p>\n" +
"Issue the following command as user <tt class=\"file-path\">root</tt><br /><tt class=\"input\"><b># cd /</b></tt></p>\n" +
"\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">List SAN Rolling Snapshots to determine the <tt class=\"file-path\">&lt;snap_label&gt;</tt><p>\n" +
"<tt class=\"input\"><b># /usr/bin/bash /eniq/bkup_sw/bin/manage_san_snapshots.bsh\n" +
"-a list -f ALL</b></tt></p>\n" +
"</li></ul>\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">Rollback SAN rolling Snapshot<p>\n" +
"<tt class=\"input\"><b># cd /</b></tt><br /><tt class=\"input\"><b># bash /eniq/bkup_sw/bin/manage_san_snapshots.bsh\n" +
"-a rollback -f ALL -n <i class=\"var\">&lt;snap_label&gt;</i> [-l <i class=\"var\">&lt;path_to_logfile&gt;</i>]</b></tt></p>\n" +
"</li></ul>\n" +
"<p>\n" +
"<strong class=\"MEDEMPH\"></strong></p>\n" +
"\n" +
"\n"+
"</div>\n";

var C16_2_1_7=
"<div id=\"content\" class=\"body-content\">\n"+
"<h4><span class=\"CHAPNUMBER\">16.2.1.7 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_610u\"></a><a name=\"CHAPTER16.2.1.7\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Restoring Repository Database</a></span></h4>\n" +
"\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>These steps are only applicable for ENIQ Statistics Blade\n" +
"or Multi-Blade servers.</dd></dl><br />\n" +
"<p>\n" +
"It is necessary to recover the <tt class=\"file-path\">repdb</tt>database following a rollback. Run the commands below on the following\n" +
"server types:</p>\n" +
"\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">ENIQ (Standalone) Server</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Coordinator Server</li></ul>\n" +
"<p><tt class=\"input\"><b># su - dcuser</b></tt></p>\n" +
"<p><tt class=\"input\"><b># cd /eniq/bkup_sw/bin</b></tt></p>\n" +
"<p><tt class=\"input\"><b># /usr/bin/bash ./repdb_restore.bsh -c /eniq/sw/conf</b></tt></p>\n" +
"<p><tt class=\"input\"><b># exit</b></tt></p>\n" +
"\n"+
"</div>\n";

var C16_2_1_8=
"<div id=\"content\" class=\"body-content\">\n"+
"<h4><span class=\"CHAPNUMBER\">16.2.1.8 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_jbht\"></a><a name=\"CHAPTER16.2.1.8\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Restoring the dwhdb Database</a></span></h4>\n" +
"\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>These steps are only applicable ENIQ Statistics Blade or\n" +
"Multi-Blade servers.</dd></dl><br />\n" +
"<p>\n" +
"It is necessary to recover the <tt class=\"file-path\">dwhdb</tt> database following rollback. These steps are only applicable on\n" +
"the following server types:</p>\n" +
"\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">ENIQ (Standalone) Server</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Coordinator Server</li></ul>\n" +
"<p>\n" +
"Enable the NAS daemon</p>\n" +
"\n" +
"<p><tt class=\"input\"><b># /usr/sbin/svcadm enable svc:/storage/NASd</b></tt></p>\n" +
"<p>\n" +
"Check that the <tt class=\"file-path\">NASd</tt>service, and the <tt class=\"file-path\">NAS milestone</tt>are online by using the following command</p>\n" +
"\n" +
"<p><tt class=\"input\"><b># /usr/bin/svcs &ndash;a | grep NAS</b></tt></p>\n" +
"<p>\n" +
"Recover the <tt class=\"file-path\">dwhdb</tt>database by completing\n" +
"the following steps</p>\n" +
"\n" +
"<p><tt class=\"input\"><b># cd /eniq/bkup_sw/bin</b></tt></p>\n" +
"<p><tt class=\"input\"><b># bash ./recover_iq.bsh [-l <i class=\"var\">&lt;path_to_logfile&gt;</i>]</b></tt></p>\n" +
"\n"+
"</div>\n";

var C16_2_1_9=
"<div id=\"content\" class=\"body-content\">\n"+
"<h4><span class=\"CHAPNUMBER\">16.2.1.9 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_e0e2\"></a><a name=\"CHAPTER16.2.1.9\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Sybase IQ Recovery Deviation</a></span></h4>\n" +
"\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>These steps are only applicable for ENIQ Statistics Blade\n" +
"or Multi-Blade servers.</dd></dl><br />\n" +
"<p>\n" +
"During execution of the above, the following may be seen:</p>\n" +
"\n" +
"<p><tt class=\"output\">-------------------------------------------------------<br />Stopping the utility db<br />Utility db not online. No need to stop\n" +
"it.<br />Starting utility db <br />Sun Microsystems Inc. SunOS 5.10\n" +
"Generic January 2005 <br />Restoring SYBASE IQ database dwhdb <br />Sun\n" +
"Microsystems Inc. SunOS 5.10 Generic January 2005 <br />Error! The\n" +
"connection to the database was closed by the server. <br />Connection\n" +
"was terminated <br />SQLCODE=-308, ODBC 3 State=\"HY000\"<br />File: \"restore_iq.sql\"\n" +
"on line 1, column 1 RESTORE DATABASE '/eniq/database/dwh_main/dwhdb.db'\n" +
"FROM <br />'/eniq/backup/iq_virtual_bkup/dwhdb_FULL_ENCAP_DUMP' <br />You can continue executing or stop. <br />1. Stop <br />2. Continue <br />Select an option: <br />--------------------------------------------------------</tt></p>\n" +
"<p>\n" +
"If you are prompted for input, then select Option &lsquo;2&rsquo;\n" +
"to continue.</p>\n" +
"\n" +
"<p><tt class=\"output\">-------------------------------------------------------- <br />Select an option: 2 <br />(Continuing after error) <br />Execution\n" +
"time: 23.101 seconds <br />Successfully Restored SYBASE IQ database\n" +
"dwhdb <br />Stopping the utility db <br />Utility db not online. No\n" +
"need to stop it. <br />(root) #: <br />--------------------------------------------------------</tt></p>\n" +
"<p>\n" +
"Restart the <tt class=\"file-path\">dwhdb</tt>server to allow some\n" +
"initial database checks and to allow the server to reset an identity\n" +
"cookie before the multiplex can be used, if needed.</p>\n" +
"\n" +
"<p>\n" +
"As root, run the following commands:</p>\n" +
"\n" +
"<p><tt class=\"input\"><b># su &ndash; dcuser</b></tt></p>\n" +
"<p><tt class=\"input\"><b># start_iq @/eniq/database/dwh_main/dwhdb.cfg -n dwhdb -iqmc\n" +
"10028 -iqtc 4297 -x tcpip{port=2640} &ndash;dt /eniq/database/dwh_temp_dbspace\n" +
"/eniq/database/dwh_main/dwhdb.db -iqmpx_sn 1/</b></tt></p>\n" +
"<p><tt class=\"input\"><b># exit</b></tt></p>\n" +
"<p>\n" +
"If the recovery is being run on a multiblade deployment, execute\n" +
"the following step on the ENIQ Statistics Coordinator Server:</p>\n" +
"\n" +
"<p><tt class=\"input\"><b># su &ndash; dcuser</b></tt></p>\n" +
"<p><tt class=\"input\"><b># stop_iq</b></tt></p>\n" +
"<p>\n" +
"During execution of the above, a prompt to stop the server appears.\n" +
"Type &lsquo;y&rsquo; and press Enter.</p>\n" +
"\n" +
"<p><tt class=\"input\"><b># exit</b></tt></p>\n" +
"\n"+
"</div>\n";

var C16_2_1_10=
"<div id=\"content\" class=\"body-content\">\n"+
"<h4><span class=\"CHAPNUMBER\">16.2.1.10 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_p65j\"></a><a name=\"CHAPTER16.2.1.10\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Enabling all ENIQ Services</a></span></h4>\n" +
"\n" +
"<p>\n" +
"The commands must be run on following server types</p>\n" +
"\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">ENIQ (Standalone) Server</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Coordinator Server</li></ul>\n" +
"<p>\n" +
"Issue the following command as user <tt class=\"file-path\">root</tt><br /><tt class=\"input\"><b># cd /</b></tt></p>\n" +
"\n" +
"<p><tt class=\"input\"><b># bash /eniq/admin/bin/manage_deployment_services.bsh -a start\n" +
"-s ALL [-l <i class=\"var\">&lt;path_to_logfile&gt;</i>]</b></tt></p>\n" +
"<p>\n" +
"Check that all services are started (enabled) by using the following\n" +
"command <br /><tt class=\"input\"><b># /usr/bin/svcs &ndash;a | grep eniq</b></tt></p>\n" +
"\n" +
"\n"+
"</div>\n";

var C16_2_1_11=
"<div id=\"content\" class=\"body-content\">\n"+
"<h4><span class=\"CHAPNUMBER\">16.2.1.11 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_9i9g\"></a><a name=\"CHAPTER16.2.1.11\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Enabling Rolling Snapshot SMF Service</a></span></h4>\n" +
"\n" +
"<p>\n" +
"The commands must be run on all server types in the following order</p>\n" +
"\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">ENIQ (Standalone) Server</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Coordinator Server</li></ul>\n" +
"<p>\n" +
"Issue the following command as user <tt class=\"file-path\">root</tt><br /><tt class=\"input\"><b># cd /</b></tt></p>\n" +
"\n" +
"<p><tt class=\"input\"><b># bash /eniq/admin/bin/manage_deployment_services.bsh -a start\n" +
"-s roll-snap</b></tt></p>\n" +
"\n"+
"</div>\n";

var C16_2_1_12=
"<div id=\"content\" class=\"body-content\">\n"+
"<h4><span class=\"CHAPNUMBER\">16.2.1.12 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_fj17\"></a><a name=\"CHAPTER16.2.1.12\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Post Rollback ENIQ Steps</a></span></h4>\n" +
"\n" +
"<p>\n" +
"Because of the nature of the rollback procedure, once it has completed,\n" +
"you may not be able to run commands in the current directory. </p>\n" +
"\n" +
"<p>\n" +
"The commands must be run on following server types</p>\n" +
"\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">ENIQ (Standalone) Server</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Coordinator Server</li></ul>\n" +
"<p><tt class=\"input\"><b># su - dcuser </b></tt></p>\n" +
"<p><tt class=\"input\"><b># bash /eniq/sw/installer/post_rollback.bsh </b></tt></p>\n" +
"\n" +
"\n" +
"\n" +
"\n"+
"</div>\n";

