var C14=
"<div id=\"content\" class=\"body-content\">\n"+
"<h1><span class=\"CHAPNUMBER\">14 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_tah4\"></a><a name=\"CHAPTER14\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">ENIQ Rolling Snapshots</a></span></h1>\n" +
"\n"+
"</div>\n";

var C14_1=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">14.1 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_gutr\"></a><a name=\"CHAPTER14.1\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Rolling Snapshot Mechanism</a></span></h2>\n" +
"\n" +
"<p>\n" +
"The rolling snapshot is designed to be a self-maintaining mechanism.\n" +
"It will create and delete snapshots of the ENIQ server when required.\n" +
"This mechanism is achieved using certain scripts/functions described\n" +
"below. </p>\n" +
"\n" +
"<p>\n" +
" <strong class=\"MEDEMPH\">NOTE</strong> : These scripts/functions are\n" +
"designed to be run in very specific ways and great care should be\n" +
"taken if these are run manually outside the intended mechanism. Misuse\n" +
"of the scripts may cause unexpected results. </p>\n" +
"\n" +
"\n"+
"</div>\n";

var C14_1_1=
"<div id=\"content\" class=\"body-content\">\n"+
"<h3><span class=\"CHAPNUMBER\">14.1.1 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_t6at\"></a><a name=\"CHAPTER14.1.1\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Script Locations</a></span></h3>\n" +
"\n" +
"<p>\n" +
"The snapshot scripts are located in <strong class=\"MEDEMPH\"> /eniq/bkup_sw/bin/</strong></p>\n" +
"\n" +
"\n"+
"</div>\n";

var C14_1_2=
"<div id=\"content\" class=\"body-content\">\n"+
"<h3><span class=\"CHAPNUMBER\">14.1.2 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_8mmv\"></a><a name=\"CHAPTER14.1.2\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Rolling Snapshot Logging</a></span></h3>\n" +
"\n" +
"<p>\n" +
"All the logs for the rolling snapshots are held in the directory <strong class=\"MEDEMPH\">/eniq/local_logs/rolling_snapshot_logs/</strong>. Each\n" +
"of the logs will be automatically rotated when the log reaches a certain\n" +
"size. By default, the number of log backup files kept is 10 and each\n" +
"log is rotated when it reaches 2MB in size. </p>\n" +
"\n" +
"\n"+
"</div>\n";

var C14_1_2_1=
"<div id=\"content\" class=\"body-content\">\n"+
"<h4><span class=\"CHAPNUMBER\">14.1.2.1 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_tezv\"></a><a name=\"CHAPTER14.1.2.1\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">eniq_rollsnapd.bsh</a></span></h4>\n" +
"\n" +
"<p>\n" +
"When this script runs, it will create a log file <strong class=\"MEDEMPH\">eniq_rollsnapd.log</strong> in the log directory.</p>\n" +
"\n" +
"\n"+
"</div>\n";

var C14_1_2_2=
"<div id=\"content\" class=\"body-content\">\n"+
"<h4><span class=\"CHAPNUMBER\">14.1.2.2 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_mqr2\"></a><a name=\"CHAPTER14.1.2.2\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">prepare_eniq_bkup.bsh</a></span></h4>\n" +
"\n" +
"<p>\n" +
"When this script runs, it will create a log file <strong class=\"MEDEMPH\">prep_roll_snap.log </strong>in the log directory.</p>\n" +
"\n" +
"\n"+
"</div>\n";

var C14_1_2_3=
"<div id=\"content\" class=\"body-content\">\n"+
"<h4><span class=\"CHAPNUMBER\">14.1.2.3 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_mcbz\"></a><a name=\"CHAPTER14.1.2.3\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">cleanup_eniq_backup.bsh</a></span></h4>\n" +
"\n" +
"<p>\n" +
"When this script runs, it will create a log file <strong class=\"MEDEMPH\">clean_roll_snap.log </strong>in the log directory.</p>\n" +
"\n" +
"\n" +
"\n"+
"</div>\n";

var C14_1_3=
"<div id=\"content\" class=\"body-content\">\n"+
"<h3><a name=\"RollingSnapshotCreation\"></a><span class=\"CHAPNUMBER\">14.1.3 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_pzau\"></a><a name=\"CHAPTER14.1.3\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Rolling Snapshot Creation</a></span></h3>\n" +
"\n" +
"<p>\n" +
"The automatic creation of the rolling snapshot(s) is controlled\n" +
"primarily by the SMF managed rolling snapshot daemon and the root\n" +
"crontab. </p>\n" +
"\n" +
"\n"+
"</div>\n";

var C14_1_3_1=
"<div id=\"content\" class=\"body-content\">\n"+
"<h4><span class=\"CHAPNUMBER\">14.1.3.1 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_7g1u\"></a><a name=\"CHAPTER14.1.3.1\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Root crontab entry</a></span></h4>\n" +
"\n" +
"<p>\n" +
"The root crontab periodically calls the <strong class=\"MEDEMPH\">trigger_roll_snap.bsh</strong> script that will trigger the creation of a rolling snapshot. </p>\n" +
"\n" +
"<p>\n" +
" If the rolling snapshot SMF service is online, this script will\n" +
"create the file that will cause the daemon to create the snapshot.\n" +
"This file is created by default every 4 hours on a rack mounted installation\n" +
"and every 12 hours on a blade installation.</p>\n" +
"\n" +
"\n"+
"</div>\n";

var C14_1_3_2=
"<div id=\"content\" class=\"body-content\">\n"+
"<h4><span class=\"CHAPNUMBER\">14.1.3.2 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_x1rn\"></a><a name=\"CHAPTER14.1.3.2\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Rolling Snapshot daemon</a></span></h4>\n" +
"\n" +
"<p>\n" +
"On startup, SMF will load the rolling snapshot service described\n" +
"in the file<strong class=\"MEDEMPH\"> /var/svc/manifest/eniq/runtime/roll_snap.xml</strong>.  </p>\n" +
"\n" +
"<p>\n" +
"SMF will in turn run the <strong class=\"MEDEMPH\">eniq_rollsnapd.bsh</strong> script to act as a daemon. This daemon periodically checks (default\n" +
"5 minutes) for the existence of a particular file which prompts for\n" +
"the creation of a rolling snapshot.</p>\n" +
"\n" +
"\n"+
"</div>\n";

var C14_1_3_3=
"<div id=\"content\" class=\"body-content\">\n"+
"<h4><span class=\"CHAPNUMBER\">14.1.3.3 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_olha\"></a><a name=\"CHAPTER14.1.3.3\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Creating the Rolling Snapshot</a></span></h4>\n" +
"\n" +
"<p>\n" +
"The rolling snapshot is created by passing certain parameters to\n" +
"the script <strong class=\"MEDEMPH\">prepare_eniq_bkup.bsh </strong>. There\n" +
"are certain criteria that must be met before the rolling snapshot\n" +
"is created.</p>\n" +
"\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\"><tt class=\"file-path\"></tt>All the\n" +
"IQ databases must be online at the time and the script must be able\n" +
"to login to each of them.</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\"><tt class=\"file-path\"></tt>The IQ dbspace\n" +
"files returned using the IQ command <strong class=\"MEDEMPH\">sp_iqdbspace</strong> must exist and have a file size greater than zero.</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">For a Multi Blade deployment to manually take a rolling\n" +
"snapshot of the system, run the following commands as user <tt class=\"file-path\">root</tt> on each of the four blades.</li></ul>\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd><br /><strong class=\"MEDEMPH\">cd /eniq/bkup_sw/bin</strong><br /><strong class=\"MEDEMPH\">/usr/bin/bash ./prepare_eniq_bkup.bsh -R</strong></dd></dl><br />\n" +
"<p>\n" +
"If these criteria are met, then the script will take a snapshot\n" +
"of each filesystem in a pre-determined order. For the filesystems\n" +
"that are associated with the IQ databases, the script will use the\n" +
"SAP IQ virtual backup facility to take the snapshot.</p>\n" +
"\n" +
"\n" +
"\n"+
"</div>\n";

var C14_1_4=
"<div id=\"content\" class=\"body-content\">\n"+
"<h3><span class=\"CHAPNUMBER\">14.1.4 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_r6su\"></a><a name=\"CHAPTER14.1.4\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Rolling Snapshot Deletion</a></span></h3>\n" +
"\n" +
"<p>\n" +
"Old rolling snapshots are deleted automatically at the end of the\n" +
"successful creation of a new rolling snapshot.</p>\n" +
"\n" +
"<p>\n" +
"The script <strong class=\"MEDEMPH\">cleanup_eniq_backup.bsh</strong> is\n" +
"used in conjunction with certain parameters to delete required rolling\n" +
"snapshots</p>\n" +
"\n" +
"<p>\n" +
"If for any reason, there is an error during the creation of the\n" +
"rolling snapshot, the script will normally clean up and remove any\n" +
"partially created snapshots.</p>\n" +
"\n" +
"<p>\n" +
"At the end of a successfully created rolling snapshot, any and\n" +
"all previous rolling snapshots are deleted.</p>\n" +
"\n" +
"<p>\n" +
"<strong class=\"MEDEMPH\">Note</strong> : It is possible for the script\n" +
"to delete any user created snapshots whose name contains the string\n" +
"above.</p>\n" +
"\n" +
"\n"+
"</div>\n";

var C14_1_5=
"<div id=\"content\" class=\"body-content\">\n"+
"<h3><span class=\"CHAPNUMBER\">14.1.5 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_gmw8\"></a><a name=\"CHAPTER14.1.5\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Enabling/Disabling Rolling Snapshot</a></span></h3>\n" +
"\n" +
"<p>\n" +
"When the ENIQ rolling snapshot SMF service is installed, its default\n" +
"state is disabled.</p>\n" +
"\n" +
"<p>\n" +
"The commands must be run on all server types in the following order</p>\n" +
"\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">ENIQ (Standalone) Server</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Coordinator Server</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Engine Server</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Reader Server(s)</li></ul>\n" +
"<p>\n" +
"As user root on each blade, do the following: </p>\n" +
"\n" +
"\n"+
"</div>\n";

var C14_1_5_1=
"<div id=\"content\" class=\"body-content\">\n"+
"<h4><span class=\"CHAPNUMBER\">14.1.5.1 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_65oy\"></a><a name=\"CHAPTER14.1.5.1\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Enable rolling snapshot SMF service</a></span></h4>\n" +
"\n" +
"<p>\n" +
"Enable the rolling snapshot service by issuing the following command\n" +
"as user <tt class=\"file-path\">root</tt></p>\n" +
"\n" +
"<p><tt class=\"input\"><b># cd /eniq/admin/bin</b></tt></p>\n" +
"<p><tt class=\"input\"><b># bash ./manage_deployment_services.bsh -a start -s roll-snap</b></tt></p>\n" +
"\n"+
"</div>\n";

var C14_1_5_2=
"<div id=\"content\" class=\"body-content\">\n"+
"<h4><span class=\"CHAPNUMBER\">14.1.5.2 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_djca\"></a><a name=\"CHAPTER14.1.5.2\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Disable rolling snapshot SMF service</a></span></h4>\n" +
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

var C14_2=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><a name=\"RollingSnapshotRecovery\"></a><span class=\"CHAPNUMBER\">14.2 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_trfl\"></a><a name=\"CHAPTER14.2\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Recover Using Rolling Snapshots</a></span></h2>\n" +
"\n" +
"<p>\n" +
"In the event of a certain types of system failures e.g. database\n" +
"corruption or accidental file deletion, then the rolling snapshots\n" +
"can be used as a very quick way of recovering the system to working\n" +
"state.</p>\n" +
"\n" +
"<p>\n" +
" When the decision has been taken to use the rolling snapshots\n" +
"to recover from some fault scenario, the very first thing that should\n" +
"be performed is to disable the creation of anymore rolling snapshots.\n" +
" Disabling the snapshot SMF service (described previously in this\n" +
"guide) will ensure no more snapshots are taken. </p>\n" +
"\n" +
"<p>\n" +
"The reason for this is that you do not want the rolling snapshot\n" +
"to snapshot the fault scenario e.g. if a user deletes a file and the\n" +
"automatic snapshot mechanism snapshots again before the file has been\n" +
"recovered, then the new snapshot will not contain the deleted file.</p>\n" +
"\n" +
"\n"+
"</div>\n";

var C14_2_1=
"<div id=\"content\" class=\"body-content\">\n"+
"<h3><span class=\"CHAPNUMBER\">14.2.1 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_0pom\"></a><a name=\"CHAPTER14.2.1\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Recovering the whole system</a></span></h3>\n" +
"\n" +
"<p>\n" +
"If the fault is so serious that the recovery of all the filesystems\n" +
"is required, then the following procedure should ensure the recovery\n" +
"of the ENIQ server to a last known good state. </p>\n" +
"\n" +
"\n"+
"</div>\n";

var C14_2_1_1=
"<div id=\"content\" class=\"body-content\">\n"+
"<h4><span class=\"CHAPNUMBER\">14.2.1.1 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_18a2\"></a><a name=\"CHAPTER14.2.1.1\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Disable rolling snapshot SMF service</a></span></h4>\n" +
"\n" +
"<p>\n" +
"Issue the following command as user <tt class=\"file-path\">root</tt> <br /><tt class=\"input\"><b># cd /eniq/admin/bin</b></tt></p>\n" +
"\n" +
"<p><tt class=\"input\"><b># bash ./manage_deployment_services.bsh -a stop -s roll-snap</b></tt></p>\n" +
"\n"+
"</div>\n";

var C14_2_1_2=
"<div id=\"content\" class=\"body-content\">\n"+
"<h4><span class=\"CHAPNUMBER\">14.2.1.2 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_yc7u\"></a><a name=\"CHAPTER14.2.1.2\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Stop all ENIQ services</a></span></h4>\n" +
"\n" +
"<p>\n" +
"As user root do the following: <br /><tt class=\"input\"><b># bash /eniq/admin/bin/manage_deployment_services.bsh\n" +
"-a stop -s ALL [-l &lt;path_to_logfile&gt;]</b></tt>  </p>\n" +
"\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>In the case of the Multi Blade, the following should be done\n" +
"as user root.<p>\n" +
"To stop the ENIQ Services the steps below have to be followed on\n" +
"all the four blades in sequence: [ ENIQ Statistics (Standalone) Server,ENIQ\n" +
"Statistics Reader_2 Server,ENIQ Statistics Reader_1,ENIQ Statistics\n" +
"Engine Server,ENIQ Statistics Coordinator Server.]</p>\n" +
"<p>\n" +
"<tt class=\"input\"><b># cd /eniq/admin/bin </b></tt></p>\n" +
"<p>\n" +
"<tt class=\"input\"><b># /usr/bin/bash ./manage_deployment_services.bsh -a stop\n" +
"-s ALL \\ [-l &lt;path_to_logfile]</b></tt></p>\n" +
"</dd></dl><br />\n" +
"<p>\n" +
"Check that all services are stopped (disabled) by using the following\n" +
"command <br /><tt class=\"input\"><b># /usr/bin/svcs &ndash;a | grep eniq</b></tt></p>\n" +
"\n" +
"\n"+
"</div>\n";

var C14_2_1_3=
"<div id=\"content\" class=\"body-content\">\n"+
"<h4><span class=\"CHAPNUMBER\">14.2.1.3 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_em69\"></a><a name=\"CHAPTER14.2.1.3\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Stop all ENIQ System User process(s)</a></span></h4>\n" +
"\n" +
"<p>\n" +
"As user root do the following: <br /><tt class=\"input\"><b># /usr/bin/pkill &ndash;u\n" +
"dcuser</b></tt></p>\n" +
"\n" +
"\n"+
"</div>\n";

var C14_2_1_4=
"<div id=\"content\" class=\"body-content\">\n"+
"<h4><span class=\"CHAPNUMBER\">14.2.1.4 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_56dw\"></a><a name=\"CHAPTER14.2.1.4\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Rollback ZFS Rolling Snapshot(s)</a></span></h4>\n" +
"\n" +
"<p>\n" +
"As root do the following</p>\n" +
"\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">List ZFS rolling snapshots to determine the <em class=\"LOWEMPH\">&lt;snap_label&gt;</em><p>\n" +
"<tt class=\"input\"><b># /eniq/bkup_sw/bin/manage_zfs_snapshots.bsh -a list -f\n" +
"ALL</b></tt></p>\n" +
"</li></ul>\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">Rollback rolling snapshot<p>\n" +
"<tt class=\"input\"><b># cd /</b></tt><br /><tt class=\"input\"><b># bash /eniq/bkup_sw/bin/manage_zfs_snapshots.bsh\n" +
"&ndash;a rollback &ndash;f  ALL=\"&lt;snap_label&gt;\" -F [-l &lt;path_to_logfile&gt;]</b></tt></p>\n" +
"</li></ul>\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">Restart ZFS service<p>\n" +
"<tt class=\"input\"><b># /usr/sbin/svcadm restart svc:/network/shares/group:zfs</b></tt>  </p>\n" +
"</li></ul>\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">Check that the ZFS service has restarted (enabled) <br /><tt class=\"input\"><b># /usr/bin/svcs &ndash;a | grep -i zfs</b></tt><br /><tt class=\"output\">online         21:19:54 svc:/network/shares/group:zfs</tt></li></ul>\n" +
"\n"+
"</div>\n";

var C14_2_1_5=
"<div id=\"content\" class=\"body-content\">\n"+
"<h4><span class=\"CHAPNUMBER\">14.2.1.5 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_e8r7\"></a><a name=\"CHAPTER14.2.1.5\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Rollback NAS Rolling Snapshot</a></span></h4>\n" +
"\n" +
"<p>\n" +
"As root do the following</p>\n" +
"\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">List NAS rolling snapshots to determine the <em class=\"LOWEMPH\">&lt;snap_label&gt;</em> <p>\n" +
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
"-a rollback -f ALL -n snap_label [-l &lt;path_to_logfile&gt;]</b></tt></p>\n" +
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

var C14_2_1_6=
"<div id=\"content\" class=\"body-content\">\n"+
"<h4><span class=\"CHAPNUMBER\">14.2.1.6 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_aex5\"></a><a name=\"CHAPTER14.2.1.6\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Rollback SAN Rolling Snapshot</a></span></h4>\n" +
"\n" +
"<p>\n" +
"As root do the following</p>\n" +
"\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">List SAN Rolling Snapshots to determine the <em class=\"LOWEMPH\">&lt;snap_label&gt;</em><p>\n" +
"<tt class=\"input\"><b># /usr/bin/bash /eniq/bkup_sw/bin/manage_san_snapshots.bsh\n" +
"-a list -f ALL</b></tt></p>\n" +
"</li></ul>\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">Rollback SAN rolling Snapshot<p>\n" +
"<tt class=\"input\"><b># cd /</b></tt><br /><tt class=\"input\"><b># bash /eniq/bkup_sw/bin/manage_san_snapshots.bsh\n" +
"-a rollback -f ALL -n snap_label [-l &lt;path_to_logfile&gt;]</b></tt></p>\n" +
"</li></ul>\n" +
"<p>\n" +
"<strong class=\"MEDEMPH\"></strong></p>\n" +
"\n" +
"\n"+
"</div>\n";

var C14_2_1_7=
"<div id=\"content\" class=\"body-content\">\n"+
"<h4><span class=\"CHAPNUMBER\">14.2.1.7 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_j3f5\"></a><a name=\"CHAPTER14.2.1.7\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Recovering SQL Anywhere (repdb)\n" +
"database</a></span></h4>\n" +
"\n" +
"<p>\n" +
"This must be run on below server types</p>\n" +
"\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">ENIQ Statistics (Standalone) Server</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Statistics Coordinator Server</li></ul>\n" +
"<p><tt class=\"input\"><b>(root)# su - dcuser</b></tt></p>\n" +
"<p><tt class=\"input\"><b>(dcuser)# /usr/bin/bash /eniq/bkup_sw/bin/repdb_restore.bsh\n" +
"-c /eniq/sw/conf<br /><br />Exit from dcuser</b></tt></p>\n" +
"<p><tt class=\"input\"><b>(dcuser)# exit</b></tt></p>\n" +
"\n"+
"</div>\n";

var C14_2_1_8=
"<div id=\"content\" class=\"body-content\">\n"+
"<h4><span class=\"CHAPNUMBER\">14.2.1.8 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_02v3\"></a><a name=\"CHAPTER14.2.1.8\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Recovering Sybase IQ database</a></span></h4>\n" +
"\n" +
"<p>\n" +
"This must be run on below server types</p>\n" +
"\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">ENIQ Statistics (Standalone) Server</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Statistics Coordinator Server</li></ul>\n" +
"<p>\n" +
"Check that the NASd service, and the NAS milestone are online by\n" +
"using the following command</p>\n" +
"\n" +
"<p>\n" +
" <tt class=\"input\"><b>(root)# /usr/bin/svcs -a | grep NAS</b></tt> <br /><br />Start\n" +
"the repdb database <br /><tt class=\"input\"><b>(root)# bash /eniq/admin/bin/manage_eniq_services.bsh\n" +
"-a start -s repdb</b></tt></p>\n" +
"\n" +
"<p>\n" +
"As the IQ databases were live when the snapshot was taken, it is\n" +
"necessary to recover the Sybase IQ virtual backup database dump files.\n" +
"As root do the following <br /><tt class=\"input\"><b># bash /eniq/bkup_sw/bin/recover_iq.bsh</b></tt></p>\n" +
"\n" +
"<p>\n" +
"During execution of the above, the following error may be seen: <br /><tt class=\"output\">-------------------------------------------------------</tt> <br /><tt class=\"output\">Stopping&nbsp;the utility db</tt>  <br /><tt class=\"output\">Utility db not online. No need to stop it.</tt> <br /><tt class=\"output\">Starting&nbsp;utility db  </tt> <br /><tt class=\"output\">Sun Microsystems\n" +
"Inc.   SunOS 5.10      Generic January 2005</tt> <br /><tt class=\"output\">Restoring SYBASE IQ database dwhdb</tt> <br /><tt class=\"output\">Sun Microsystems\n" +
"Inc.   SunOS 5.10      Generic January 2005</tt> <br /><tt class=\"output\">Error! The connection to the database was closed by the server.</tt> <br /><tt class=\"output\">Connection was terminated</tt> <br /><tt class=\"output\">SQLCODE=-308,\n" +
"ODBC 3 State=\"HY000\"</tt> <br /><tt class=\"output\">File: \"restore_iq.sql\"\n" +
"on line 1, column 1</tt> <br /><tt class=\"output\">RESTORE DATABASE '/eniq/database/dwh_main/dwhdb.db'\n" +
"FROM '/eniq/backup/iq_virtual_bkup/dwhdb_FULL_ENCAP_DUMP'</tt> <br /><tt class=\"output\">You can continue executing or stop.</tt> <br /><tt class=\"output\">1. Stop</tt> <br /><tt class=\"output\">2. Continue</tt> <br /><tt class=\"output\">Select\n" +
"an option:</tt> <br /><tt class=\"output\">--------------------------------------------------------</tt></p>\n" +
"\n" +
"<p>\n" +
"If the error above is seen, then select Option \"2\" to continue;\n" +
"otherwise, continue to the next step on <a href='javascript:parent.parent.parent.showAnchor(\"section-on-multi-blade-only\")' class=\"xref\">Section  14.2.1.9</a>.</p>\n" +
"\n" +
"<p><tt class=\"output\">--------------------------------------------------------</tt></p>\n" +
"<p>\n" +
"<tt class=\"output\">Select an option: 2</tt> <br /><tt class=\"output\">(Continuing after\n" +
"error)</tt> <br /><tt class=\"output\">Execution time: 23.101 seconds</tt> <br /><tt class=\"output\">Successfully Restored SYBASE IQ database dwhdb</tt> <br /><tt class=\"output\">Stopping the utility db</tt> <br /><tt class=\"output\">Utility\n" +
"db not online. No need to stop it.</tt> <br /><tt class=\"output\">(root) #:</tt> <br /><tt class=\"output\">--------------------------------------------------------</tt> <br /></p>\n" +
"\n" +
"<p>\n" +
"And then execute the below workaround:</p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Restart the dwhdb server in order to allow some initial\n" +
"database checks and to allow the server to reset an identity cookie\n" +
"before the multiplex can be used, if needed.<p>\n" +
"As root, run the following commands: <br />(root) <tt class=\"input\"><b>#: su -\n" +
"dcuser</b></tt> <br />{dcuser} <tt class=\"input\"><b>#: start_iq @/eniq/database/dwh_main/dwhdb.cfg\n" +
"-n dwhdb -iqmc 10028 -iqtc 4297 -x tcpip{port=2640} -dt /eniq/database/dwh_temp_dbspace\n" +
"/eniq/database/dwh_main/dwhdb.db -iqmpx_sn 1</b></tt></p>\n" +
"</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Execute the following script. On a multi blade deployment,\n" +
"this should be executed on the Coordinator node: <br />{dcuser} <tt class=\"input\"><b>#: stop_iq</b></tt></li><li class=\"STEPLIST\" id=\"THSSTYLE2\">During execution of the above, a prompt to stop the server\n" +
"will appear. Type <b>y</b> and press <b>Enter</b>.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">{dcuser} <tt class=\"input\"><b>#: exit</b></tt>.</li></ol>\n" +
"\n"+
"</div>\n";

var C14_2_1_9=
"<div id=\"content\" class=\"body-content\">\n"+
"<h4><a name=\"section-on-multi-blade-only\"></a><span class=\"CHAPNUMBER\">14.2.1.9 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_1y0z\"></a><a name=\"CHAPTER14.2.1.9\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">On a Multi Blade Deployment Only</a></span></h4>\n" +
"\n" +
"<p>\n" +
"Verify that all four blades have successfully rolled back before\n" +
"continuing.</p>\n" +
"\n" +
"\n"+
"</div>\n";

var C14_2_1_10=
"<div id=\"content\" class=\"body-content\">\n"+
"<h4><span class=\"CHAPNUMBER\">14.2.1.10 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_fj17\"></a><a name=\"CHAPTER14.2.1.10\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Post rollback steps for ENIQ</a></span></h4>\n" +
"\n" +
"<p>\n" +
"Start services as 'dcuser'<br /><strong class=\"MEDEMPH\"># su - dcuser</strong><br /><strong class=\"MEDEMPH\"># cd /eniq/sw/bin</strong><br /><strong class=\"MEDEMPH\"># dwhdb start</strong><br /><strong class=\"MEDEMPH\"># repdb start</strong><br /><strong class=\"MEDEMPH\"># webserver start</strong><br /><strong class=\"MEDEMPH\"># licmgr start</strong><br /><strong class=\"MEDEMPH\"># engine start</strong></p>\n" +
"\n" +
"<p>\n" +
"</p>\n" +
"\n" +
"<p>\n" +
"Start the ENIQ scheduler service<br /><strong class=\"MEDEMPH\"># scheduler start</strong></p>\n" +
"\n" +
"<p>\n" +
"</p>\n" +
"\n" +
"<p>\n" +
"Log into the adminUI for the ENIQ server<br /><strong class=\"MEDEMPH\">http://&lt;server_name&gt;:8080/adminui</strong><br /><strong class=\"MEDEMPH\">User: eniq</strong><br /><strong class=\"MEDEMPH\">Pass: eniq</strong></p>\n" +
"\n" +
"<p>\n" +
"</p>\n" +
"\n" +
"<p>\n" +
"1) Click the \"<strong class=\"MEDEMPH\">ETLC set scheduling</strong>\" link on the left\n" +
"hand menu</p>\n" +
"\n" +
"<p>\n" +
"</p>\n" +
"\n" +
"<p>\n" +
"Choose \"<strong class=\"MEDEMPH\">Maintenance</strong>\" from \"<strong class=\"MEDEMPH\">Set type</strong>\"\n" +
"select box</p>\n" +
"\n" +
"<p>\n" +
"</p>\n" +
"\n" +
"<p>\n" +
"Choose \"<strong class=\"MEDEMPH\">DWH_BASE</strong>\" from \"<strong class=\"MEDEMPH\">package</strong>\" select\n" +
"box</p>\n" +
"\n" +
"<p>\n" +
"</p>\n" +
"\n" +
"<p>\n" +
"Start the following sets by clicking \"<strong class=\"MEDEMPH\">Start</strong>\" from\n" +
"the table<br /><strong class=\"MEDEMPH\">Update_Dates</strong><br /><strong class=\"MEDEMPH\">Cleanup_logdir</strong><br /><strong class=\"MEDEMPH\">Cleanup_transfer_batches</strong><br />The browser will be\n" +
"automatically redirected to the \"<strong class=\"MEDEMPH\">ETLC monitoring</strong>\" page\n" +
"after each set is started so it is necessary to click the \"<strong class=\"MEDEMPH\">ETLC\n" +
"set scheduling</strong>\" link on the left hand menu before starting\n" +
"the next set.<br />Wait until all sets have executed before continuing.\n" +
"The page can be refreshed by clicking the \"<strong class=\"MEDEMPH\">ETLC monitoring</strong>\" link on the left hand menu</p>\n" +
"\n" +
"<p>\n" +
"<br />2) Click the \"<strong class=\"MEDEMPH\">ETLC set scheduling</strong>\" link on the\n" +
"left hand menu</p>\n" +
"\n" +
"<p>\n" +
"</p>\n" +
"\n" +
"<p>\n" +
"Choose \"<strong class=\"MEDEMPH\">Maintenance</strong>\" from \"<strong class=\"MEDEMPH\">Set type</strong>\"\n" +
"select box</p>\n" +
"\n" +
"<p>\n" +
"</p>\n" +
"\n" +
"<p>\n" +
"Choose \"<strong class=\"MEDEMPH\">DWH_BASE</strong>\" from \"<strong class=\"MEDEMPH\">package</strong>\" select\n" +
"box</p>\n" +
"\n" +
"<p>\n" +
"</p>\n" +
"\n" +
"<p>\n" +
"Find the <strong class=\"MEDEMPH\">Trigger_Partitioning</strong> set in the table and\n" +
"click \"<strong class=\"MEDEMPH\">Start</strong>\" to execute the set<br />Wait until all sets\n" +
"have executed before continuing.</p>\n" +
"\n" +
"<p>\n" +
"</p>\n" +
"\n" +
"<p>\n" +
"3) Click the \"<strong class=\"MEDEMPH\">ETLC set scheduling</strong>\" link on the left\n" +
"hand menu</p>\n" +
"\n" +
"<p>\n" +
"</p>\n" +
"\n" +
"<p>\n" +
"Choose \"<strong class=\"MEDEMPH\">Maintenance</strong>\" from \"<strong class=\"MEDEMPH\">Set type</strong>\"\n" +
"select box</p>\n" +
"\n" +
"<p>\n" +
"</p>\n" +
"\n" +
"<p>\n" +
"Choose \"<strong class=\"MEDEMPH\">DWH_MONITOR</strong>\" from \"<strong class=\"MEDEMPH\">package</strong>\" select\n" +
"box</p>\n" +
"\n" +
"<p>\n" +
"</p>\n" +
"\n" +
"<p>\n" +
"Find the <strong class=\"MEDEMPH\">UpdateFirstLoadings</strong> set in the table and\n" +
"click \"<strong class=\"MEDEMPH\">Start</strong>\" to execute the set<br />Wait until all sets\n" +
"have executed before continuing.</p>\n" +
"\n" +
"<p>\n" +
"</p>\n" +
"\n" +
"<p>\n" +
"4) Click the \"<strong class=\"MEDEMPH\">ETLC set scheduling</strong>\" link on the left\n" +
"hand menu</p>\n" +
"\n" +
"<p>\n" +
"</p>\n" +
"\n" +
"<p>\n" +
"Choose \"<strong class=\"MEDEMPH\">Maintenance</strong>\" from \"<strong class=\"MEDEMPH\">Set type</strong>\"\n" +
"select box</p>\n" +
"\n" +
"<p>\n" +
"</p>\n" +
"\n" +
"<p>\n" +
"Choose \"<strong class=\"MEDEMPH\">DWH_MONITOR</strong>\" from \"<strong class=\"MEDEMPH\">package</strong>\" select\n" +
"box</p>\n" +
"\n" +
"<p>\n" +
"</p>\n" +
"\n" +
"<p>\n" +
"Find the <strong class=\"MEDEMPH\">AggregationRuleCopy</strong> set in the table and\n" +
"click \"<strong class=\"MEDEMPH\">Start</strong>\" to execute the set<br />Wait until all sets\n" +
"have executed before continuing.</p>\n" +
"\n" +
"<div><p>\n" +
"Ensure the ENIQ engine is in 'normal' mode<br /><strong class=\"MEDEMPH\"># engine\n" +
"-e changeProfile Normal</strong></p>\n" +
"</div>\n" +
"<p>\n" +
"</p>\n" +
"\n" +
"<p>\n" +
"5) Swith user back to 'root'<br /><strong class=\"MEDEMPH\"># exit</strong></p>\n" +
"\n" +
"\n"+
"</div>\n";

var C14_2_1_11=
"<div id=\"content\" class=\"body-content\">\n"+
"<h4><span class=\"CHAPNUMBER\">14.2.1.11 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_4x0j\"></a><a name=\"CHAPTER14.2.1.11\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Running  post rollback sets from\n" +
"command line</a></span></h4>\n" +
"\n" +
"<div><p>\n" +
"this procedure needs to be followed only if adminUI is unavailable</p>\n" +
"</div>\n" +
"<div><p>\n" +
"Go  to /eniq/sw/installer dir  and run post_rollback.bsh\n" +
"script 	</p>\n" +
"<p>\n" +
"<strong class=\"MEDEMPH\"># cd /eniq/sw/installer</strong> </p>\n" +
"<p>\n" +
"# <strong class=\"MEDEMPH\">bash ./post_rollback.bsh</strong></p>\n" +
"</div>\n" +
"<div><p>\n" +
"Ensure the ENIQ engine is in 'normal' mode<br /><strong class=\"MEDEMPH\"># engine\n" +
"-e changeProfile Normal</strong></p>\n" +
"</div>\n" +
"\n"+
"</div>\n";

var C14_2_1_12=
"<div id=\"content\" class=\"body-content\">\n"+
"<h4><span class=\"CHAPNUMBER\">14.2.1.12 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_pa8j\"></a><a name=\"CHAPTER14.2.1.12\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Start all ENIQ services</a></span></h4>\n" +
"\n" +
"<p>\n" +
"As user root do the following: <br /><strong class=\"MEDEMPH\"># bash\n" +
"/eniq/admin/bin/manage_deployment_services.bsh -a start -s ALL [-l\n" +
"&lt;path_to_logfile&gt;]</strong>  </p>\n" +
"\n" +
"<p>\n" +
"Check that all services are started (enabled) by using the following\n" +
"command <br /><strong class=\"MEDEMPH\"># /usr/bin/svcs &ndash;a | grep eniq</strong>	 </p>\n" +
"\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>For Multi Blade deployment, the commands should be executed\n" +
"successfully on the Coordinator blade first, then the Engine blade,\n" +
"followed by Reader_1 blade and finally Reader_2 blade.</dd></dl><br />\n" +
"\n"+
"</div>\n";

var C14_2_1_13=
"<div id=\"content\" class=\"body-content\">\n"+
"<h4><span class=\"CHAPNUMBER\">14.2.1.13 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_tqnz\"></a><a name=\"CHAPTER14.2.1.13\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Enable rolling snapshot SMF service</a></span></h4>\n" +
"\n" +
"<p>\n" +
"Enable the rolling snapshot SMF service using the procedure described\n" +
"previously in this guide.</p>\n" +
"\n" +
"\n" +
"\n" +
"\n" +
"\n"+
"</div>\n";

