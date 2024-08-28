var C4=
"<div id=\"content\" class=\"body-content\">\n"+
"<h1><span class=\"CHAPNUMBER\">4 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_xpkn\"></a><a name=\"CHAPTER4\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Administering ENIQ System</a></span></h1>\n" +
"\n"+
"</div>\n";

var C4_1=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">4.1 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_oik7\"></a><a name=\"CHAPTER4.1\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">\n" +
"Monitoring ENIQ</a></span></h2>\n" +
"\n"+
"</div>\n";

var C4_1_1=
"<div id=\"content\" class=\"body-content\">\n"+
"<h3><span class=\"CHAPNUMBER\">4.1.1 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_125l\"></a><a name=\"CHAPTER4.1.1\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Monitoring Services Using SMF</a></span></h3>\n" +
"\n" +
"<p>\n" +
"The ENIQ Admin UI provides monitoring for the ENIQ components installed\n" +
"on the ENIQ servers and for SAP IQ. See <a href='javascript:parent.parent.parent.showAnchor(\"AdminUI\")' class=\"xref\">Section  5</a> for\n" +
"more information monitoring using the Admin UI. </p>\n" +
"\n" +
"<p>\n" +
"If the Admin UI is not accessible, use the Solaris Service Management\n" +
"Facility (SMF) to check the status of the services. </p>\n" +
"\n" +
"<p>\n" +
"To check all required services are online:</p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\"> Log in as <tt class=\"file-path\">root</tt> user</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Run the command<br /><tt class=\"input\"><b>/usr/bin/svcs | grep eniq</b></tt><p>\n" +
"<tt class=\"LITERALMONO\">.................</tt><br />\n" +
"<tt class=\"LITERALMONO\">.................</tt><br />\n" +
"<tt class=\"LITERALMONO\">online &nbsp; &nbsp; &nbsp; &nbsp; Dec_06 &nbsp; svc:/eniq/repdb:default</tt><br />\n" +
"<tt class=\"LITERALMONO\">online &nbsp; &nbsp; &nbsp; &nbsp; Dec_06 &nbsp; svc:/eniq/webserver:default</tt><br />\n" +
"<tt class=\"LITERALMONO\">online &nbsp; &nbsp; &nbsp; &nbsp; Dec_07 &nbsp; svc:/eniq/dwhdb:default</tt><br />\n" +
"<tt class=\"LITERALMONO\">online &nbsp; &nbsp; &nbsp; &nbsp; Dec_07 &nbsp; svc:/eniq/engine:default</tt><br />\n" +
"<tt class=\"LITERALMONO\">online &nbsp; &nbsp; &nbsp; &nbsp; Dec_07 &nbsp; svc:/eniq/scheduler:default</tt><br />\n" +
"<tt class=\"LITERALMONO\">online &nbsp; &nbsp; &nbsp; &nbsp; Dec_07 &nbsp; svc:/eniq/licmgr:default</tt><br />\n" +
"<tt class=\"LITERALMONO\">.................</tt><br />\n" +
"<tt class=\"LITERALMONO\">.................</tt></p>\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>This shows the ENIQ services on your system. The ENIQ services\n" +
"displayed depend on your installation (rack or blade).</dd></dl><br /></li></ol>\n" +
"\n" +
"<p>\n" +
"If necessary ENIQ services are not <tt class=\"file-path\">online</tt> see the commands in <a href='javascript:parent.parent.parent.showAnchor(\"smf.manage\")' class=\"xref\">Section  4.1.2</a> for information\n" +
"on restarting these services. Further information on SMF service management\n" +
"is available in <a href='javascript:parent.parent.parent.showAnchor(\"EniqServiceCommands\")' class=\"xref\">Section  8</a> .</p>\n" +
"\n" +
"\n"+
"</div>\n";

var C4_1_2=
"<div id=\"content\" class=\"body-content\">\n"+
"<h3><a name=\"smf.manage\"></a><span class=\"CHAPNUMBER\">4.1.2 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_figd\"></a><a name=\"CHAPTER4.1.2\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Managing SMF\n" +
"Services</a></span></h3>\n" +
"\n" +
"<p>\n" +
"To enable any ENIQ service, use the following command:</p>\n" +
"\n" +
"<p>\n" +
"<tt class=\"input\"><b># /usr/sbin/svcadm enable <i class=\"var\">&lt;service_name&gt;</i></b></tt></p>\n" +
"\n" +
"<p>\n" +
"To disable any ENIQ service, use the following command:</p>\n" +
"\n" +
"<p>\n" +
"<tt class=\"input\"><b># /usr/sbin/svcadm disable <i class=\"var\">&lt;service_name&gt;</i></b></tt></p>\n" +
"\n" +
"<p>\n" +
"If a service is in a maintenance state, it is necessary to clear\n" +
"the service.</p>\n" +
"\n" +
"<p>\n" +
"<tt class=\"input\"><b># /usr/sbin/svcadm clear <i class=\"var\">&lt;service_name&gt;</i></b></tt></p>\n" +
"\n" +
"\n"+
"</div>\n";

var C4_1_3=
"<div id=\"content\" class=\"body-content\">\n"+
"<h3><span class=\"CHAPNUMBER\">4.1.3 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_rhzo\"></a><a name=\"CHAPTER4.1.3\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Monitoring SAP IQ and ASA</a></span></h3>\n" +
"\n" +
"<p>\n" +
"The ENIQ Admin UI monitors SAP IQ and ASA, see <a href='javascript:parent.parent.parent.showAnchor(\"AdminUI\")' class=\"xref\">Section  5</a> for more information.</p>\n" +
"\n" +
"<p>\n" +
"If the Admin UI is not accessible, check the status of SAP IQ and\n" +
"ASA from the command line: </p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\"> Log in as user <tt class=\"file-path\">dcuser</tt>.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Run the command<p>\n" +
"<tt class=\"input\"><b>dbisql -c \"UID=DBA;PWD=<i class=\"var\">&lt;DBA password, default SQL&gt;</i>\" -host localhost -port <i class=\"var\">&lt;database port&gt;</i> -nogui</b></tt></p>\n" +
"</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">At the prompt:<p>\n" +
"For SAP IQ (DWHDB) run the command <tt class=\"input\"><b>sp_iqstatus</b></tt></p>\n" +
"<p>\n" +
"For SAP ASA (REPDB) run the command <tt class=\"input\"><b>sa_db_properties</b></tt></p>\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>The default port number for DWHDB is <tt class=\"input\"><b>2640</b></tt>.\n" +
"The port number for REPDB is <tt class=\"input\"><b>2641</b></tt></dd></dl><br /></li></ol>\n" +
"\n" +
"<p>\n" +
"Check for usage of main space (Main IQ Blocks used). If the main\n" +
"space usage is over 95% of the main database space, the database might\n" +
"need to be expanded. Contact Ericsson support for assistance. </p>\n" +
"\n" +
"<p>\n" +
"Temp space (Temp IQ Blocks used) is used by IQ to handle large\n" +
"queries, and it is allocated and released continuously. For the above\n" +
"reason sporadic monitoring of temp space is not useful. Filling up\n" +
"temp space can lead to failure of one or more queries running (not\n" +
"mentioning a substantial performance impact). If a query or ENIQ task\n" +
"is failing because of temp space filling up contact Ericsson support\n" +
"for assistance. </p>\n" +
"\n" +
"\n"+
"</div>\n";

var C4_1_4=
"<div id=\"content\" class=\"body-content\">\n"+
"<h3><span class=\"CHAPNUMBER\">4.1.4 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_bwdb\"></a><a name=\"CHAPTER4.1.4\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Monitoring File System Usage</a></span></h3>\n" +
"\n" +
"<p>\n" +
"The ENIQ Admin UI provides basic file system usage monitoring,\n" +
"see <a href='javascript:parent.parent.parent.showAnchor(\"AdminUI\")' class=\"xref\">Section  5</a> for more information.</p>\n" +
"\n" +
"<p>\n" +
"If the Admin UI is not accessible, check the file system usage\n" +
"from the command line.</p>\n" +
"\n" +
"<p>\n" +
"<strong class=\"MEDEMPH\">ZFS File Systems</strong></p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Log on as user <tt class=\"file-path\">root</tt>.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">For ZFS file systems run the command <br /><tt class=\"input\"><b># /usr/sbin/zfs\n" +
"list</b></tt></li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Check there is space available on the file system. If\n" +
"any of the file systems are over 90% contact Ericsson support.</li></ol>\n" +
"<p>\n" +
"The following is an example of the output produced by the <tt class=\"file-path\"># /usr/sbin/zfs list</tt>command.</p><pre class=\"prencd\">NAME USED AVAIL REFER MOUNTPOINT\n" +
"... \n" +
"\n" +
"eniq_sp_1/dwh_main 1.09G 1.12T 1.09G /eniq/database/dwh_main \n" +
"\n" +
"eniq_sp_1/dwh_main_dbspace 1.43T 1.12T 1.43T /eniq/database/dwh_m \n" +
"\n" +
"eniq_sp_1/dwh_temp_dbspace 489G 1.12T 489G /eniq/database/dwh_t \n" +
"\n" +
"eniq_sp_1/rep_main 2.27G 1.12T 2.27G /eniq/database/rep_main \n" +
"\n" +
"eniq_sp_1/rep_temp 1.00G 1.12T 1.00G /eniq/database/rep_temp \n" +
"\n" +
"...\n" +
"</pre><p></p>\n" +
"\n" +
"<p>\n" +
"<strong class=\"MEDEMPH\">NAS File Systems</strong></p>\n" +
"\n" +
"<p>\n" +
"If ENIQ is installed on a blade, you must also check the NAS file\n" +
"systems:</p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Log on to the NAS console as user <tt class=\"file-path\">master</tt>. </li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Run the command <br /><tt class=\"input\"><b>storage fs list</b></tt></li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Check there is space available on the file systems. If\n" +
"any of the file systems are over 90%, contact Ericsson support.</li></ol>\n" +
"<p>\n" +
"The following is an example of the output produced by the <tt class=\"file-path\">storage fs list</tt> command.</p><pre class=\"prencd\">FS STATUS SIZE LAYOUT MIRRORS COLUMNS USE% NFS SHARED CIFS \n" +
"SHARED SECONDARY TIER POOL LIST\n" +
"\n" +
"========================= ====== ==== ====== ======= \n" +
"======= ==== ========== =========== ============== \n" +
"=========\n" +
"\n" +
"stats1-admin online 2.00G simple - - 3% yes no \n" +
"no stats1\n" +
"\n" +
"stats1-archive online 8.00G simple - - 0% yes no \n" +
"no stats1\n" +
"\n" +
"stats1-backup online 10.00G simple - - 1% yes no \n" +
"no stats1\n" +
"\n" +
"stats1-etldata online 50.00G simple - - 0% yes no \n" +
"no stats1\n" +
"\n" +
"stats1-fmdata online 1.00G simple - - 6% yes no \n" +
"no stats1\n" +
"\n" +
"stats1-home online 10.00G simple - - 1% yes no \n" +
"no stats1\n" +
"\n" +
".......................\n" +
"\n" +
".......................\n" +
"</pre><p></p>\n" +
"\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>The above list is an example, depending on your installation\n" +
"you may have additional filesystems listed.</dd></dl><br />\n" +
"\n"+
"</div>\n";

var C4_1_5=
"<div id=\"content\" class=\"body-content\">\n"+
"<h3><span class=\"CHAPNUMBER\">4.1.5 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_g5m3\"></a><a name=\"CHAPTER4.1.5\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Monitoring the ZFS Pool</a></span></h3>\n" +
"\n" +
"<p>\n" +
" Log in as <tt class=\"file-path\">root</tt> user and run the following\n" +
"command:</p>\n" +
"\n" +
"<p>\n" +
"<tt class=\"input\"><b>/usr/sbin/zpool status</b></tt></p>\n" +
"\n" +
"<p>\n" +
"Check that <strong class=\"MEDEMPH\">the pool is online and no data errors\n" +
"are detected</strong>, and all the mirrors are online and healthy. \n" +
"If ZFS reports that a device is faulty, <strong class=\"MEDEMPH\">it must\n" +
"be physically replaced. </strong>If this occurs contact your HW support\n" +
"immediately. No action is required at application level unless ZFS\n" +
"detects that the pool is offline or data corruption occurred. </p>\n" +
"\n" +
"<p>\n" +
"If ZFS detects that the pool is offline or that data corruption\n" +
"occurred, take no action on ZFS, shutdown any application component\n" +
"(if any still running) according to the ENIQ SAG and contact immediately\n" +
"the Ericsson support.</p>\n" +
"\n" +
"\n"+
"</div>\n";

var C4_1_6=
"<div id=\"content\" class=\"body-content\">\n"+
"<h3><span class=\"CHAPNUMBER\">4.1.6 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_4q8t\"></a><a name=\"CHAPTER4.1.6\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Monitoring the NAS Pool (Blade\n" +
"Only)</a></span></h3>\n" +
"\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd><strong class=\"MEDEMPH\">This is only applicable where ENIQ is installed on\n" +
"a blade</strong></dd></dl><br />\n" +
"<p>\n" +
"Log on to the NAS console as user master and run the following\n" +
"command:</p>\n" +
"\n" +
"<p><tt class=\"input\"><b>storage pool free</b></tt></p>\n" +
"\n"+
"</div>\n";

var C4_1_7=
"<div id=\"content\" class=\"body-content\">\n"+
"<h3><span class=\"CHAPNUMBER\">4.1.7 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_5ozx\"></a><a name=\"CHAPTER4.1.7\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Monitoring the NAS Filesystems\n" +
"(Blade Only)</a></span></h3>\n" +
"\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd><strong class=\"MEDEMPH\">This is only applicable where ENIQ is installed on\n" +
"a blade</strong></dd></dl><br />\n" +
"<p>\n" +
"For the NAS filesystems to mount, the <tt class=\"file-path\">NASd</tt> service and the <tt class=\"file-path\">NAS-online</tt> milestone\n" +
"must both be <tt class=\"file-path\">online</tt>.</p>\n" +
"\n" +
"<p>\n" +
" To check this run the following command:</p>\n" +
"\n" +
"<p><tt class=\"input\"><b># /usr/bin/svcs | grep NAS</b></tt></p>\n" +
"<p><tt class=\"output\">online         Jul_04   svc:/storage/NASd:default</tt></p>\n" +
"<p><tt class=\"output\">online         Jul_04   svc:/milestone/NAS-online:default</tt></p>\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd> See section <a href='javascript:parent.parent.parent.showAnchor(\"smf.manage\")' class=\"xref\">Section  4.1.2</a> to manage SMF services</dd></dl><br />\n" +
"<p>\n" +
"The milestone should come online once the <tt class=\"file-path\">NASd</tt> service is online (unless it was manually disabled). </p>\n" +
"\n" +
"<p>\n" +
"The following log file can be referenced for troubleshooting errors:</p>\n" +
"\n" +
"<p><tt class=\"output\">/eniq/local_logs/NASd/NASd.log</tt></p>\n" +
"\n"+
"</div>\n";

var C4_1_8=
"<div id=\"content\" class=\"body-content\">\n"+
"<h3><span class=\"CHAPNUMBER\">4.1.8 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_kwql\"></a><a name=\"CHAPTER4.1.8\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\"> Database Consistency Check</a></span></h3>\n" +
"\n" +
"<p>\n" +
"The Database Consistency Check (DBCC) verifies the validity and\n" +
"consistency of the current database. It detects allocation problems\n" +
"and index inconsistencies. It also checks the available <tt class=\"file-path\">iqmsg</tt> files on the IQ nodes and reports if damaged\n" +
"index messages are found.</p>\n" +
"\n" +
"<p>\n" +
"The Database Consistency Check script performs the following tasks:</p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Database allocation (<tt class=\"file-path\">db_allocation</tt>)</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Table verification (<tt class=\"file-path\">verify_tables</tt>)</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">iqmsg log check (<tt class=\"file-path\">iqmsg_check</tt>)</li></ol>\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>Only one action can be performed at a time on a server. <tt class=\"file-path\">iqmsg</tt> log check is executed by default while performing\n" +
"the other two tasks, database allocation and table verification. Each\n" +
"task can be run independently if necessary.</dd></dl><br />\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>Table verification and <tt class=\"file-path\">iqmsg</tt> log\n" +
"check are automated through <tt class=\"file-path\">cron</tt> which\n" +
"is triggered daily (at 9:00 AM).</dd></dl><br />\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Database allocation:<p>\n" +
"Database allocation checks allocation with blockmap information\n" +
"for the entire database, that is, it checks for metadata consistency\n" +
"and reports any errors. At the end the <tt class=\"file-path\">iqmsg</tt> log check checks for damaged index msgs present in the available <tt class=\"file-path\">iqmsg</tt> files on IQ nodes.</p>\n" +
"<p>\n" +
"Run the DB allocation on a monthly basis.</p>\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>DB allocation requires downtime that is, all ENIQ services\n" +
"on all blades are disabled while <tt class=\"file-path\">db_allocation</tt> runs.<p>\n" +
"For example, for ~75K tables, around 2 hours of downtime on all\n" +
"blades is required.</p>\n" +
"</dd></dl><br /><p>\n" +
"To perform database allocation, run below command as user <tt class=\"file-path\">root</tt>on the following server types:</p>\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Statistics (standalone)</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Statistics Coordinator</li></ul><p>\n" +
"<tt class=\"input\"><b># bash /eniq/admin/bin/DbCheck.bsh -a db_allocation [-l <i class=\"var\">&lt;path_to_logfile&gt;</i>]</b></tt></p>\n" +
"</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Table Verification:<p>\n" +
"Table verification performs two levels of checks.</p>\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">First level of check is done on all tables in database\n" +
"using the <tt class=\"file-path\">dbisql</tt> utility, in which it scans\n" +
"complete table and reports erroneous tables.</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">Second level of check is performed on those erroneous\n" +
"tables by running <tt class=\"file-path\">sp_iqcheckdb</tt> procedure\n" +
"with <tt class=\"file-path\">verify</tt> mode where detailed index check\n" +
"is done.</li></ul><p>\n" +
" Contact Ericsson support if any errors are reported.</p>\n" +
"<p>\n" +
"There are two types of table verifications:</p>\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\"><strong class=\"MEDEMPH\">Full Run:</strong> <br />By default,\n" +
"the first run of the table verification is a full run and it checks\n" +
"all the tables in database.</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\"><strong class=\"MEDEMPH\">Delta Run:</strong> <br /> After first\n" +
"run is completed, all consecutive runs are delta runs. These only\n" +
"check the tables which have been updated since the previous run. </li></ul><p>\n" +
"The following commands show how to perform the Full Run and Delta\n" +
"Run table verifications.</p>\n" +
"\n" +
"<ol type=\"a\">\n" +
"\n" +
"<li class=\"substep\">Full Run:\n" +
"<p>\n" +
"A Full run affects data loading on ENIQ Statistics server as it\n" +
"requires a restart of the ENIQ (<tt class=\"file-path\">engine</tt> and <tt class=\"file-path\">dwhdb</tt>) services at both the start and the end of the\n" +
"Table verification check. However, there is no downtime when the run\n" +
"is actually happening.</p>\n" +
"\n" +
"<p>\n" +
"Full run takes more time to complete than delta runs</p>\n" +
"\n" +
"<p>\n" +
"Perform the full run when there is minimal load on ENIQ Statistics\n" +
"server.</p>\n" +
"\n" +
"\n" +
"<p>\n" +
"For example, a Full run for ~75K tables takes around 12 hours</p>\n" +
"\n" +
"<p>\n" +
"To perform full run table verification, run below command as user <tt class=\"file-path\">root</tt> on the following server types:</p>\n" +
"\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE3\">ENIQ Statistics (standalone)</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE3\">ENIQ Statistics Coordinator</li></ul>\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>Delete the files <tt class=\"file-path\">/eniq/admin/etc/dbcc_restart_service_stop_indicator</tt>and <tt class=\"file-path\">/eniq/admin/etc/dbcc_full_run_indicator</tt>if they exist using the below commands.<p>\n" +
"<tt class=\"input\"><b># rm -f /eniq/admin/etc/dbcc_full_run_indicator</b></tt></p>\n" +
"<p>\n" +
"<tt class=\"input\"><b># rm -f /eniq/admin/etc/dbcc_restart_service_stop_indicator</b></tt></p>\n" +
"</dd></dl><br />\n" +
"<p>\n" +
"<tt class=\"input\"><b># bash /eniq/admin/bin/DbCheck.bsh -a verify_tables -f \n" +
"[-l <i class=\"var\">&lt;path_to_logfile&gt;</i>]</b></tt></p>\n" +
"\n" +
"</li>\n" +
"\n" +
"<li class=\"substep\">Delta Run:\n" +
"<p>\n" +
"Only tables which were modified after the last run of table verification\n" +
"are checked.</p>\n" +
"\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>No downtime is required while performing a delta run.</dd></dl><br />\n" +
"<p>\n" +
"To perform a delta run, execute the following command as user <tt class=\"file-path\">root</tt> on the following server types:</p>\n" +
"\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE3\">ENIQ Statistics (standalone)</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE3\">ENIQ Statistics Coordinator</li></ul>\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>By default table verification when run for the first time\n" +
"performs full run irrespective of passing <tt class=\"file-path\">-f</tt> option or not.</dd></dl><br />\n" +
"<p>\n" +
"<tt class=\"input\"><b># bash /eniq/admin/bin/DbCheck.bsh -a verify_tables [-l <i class=\"var\">&lt;path_to_logfile&gt;</i>]</b></tt></p>\n" +
"\n" +
"</li>\n" +
"</ol></li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Iqmsg log checking:<p>\n" +
"The <tt class=\"file-path\">iqmsg_check</tt> checks the damaged index\n" +
"messages present in all the available <tt class=\"file-path\">iqmsg</tt> log files on IQ nodes.</p>\n" +
"<p>\n" +
"When first run it checks from starting of the available iqmsg files\n" +
"on IQ nodes, after that it will check for the damaged index messages\n" +
"only after last iqmsg check was performed.</p>\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>By default iqmsg log checking is done while performing other\n" +
"two actions (db_allocation and verify_tables). And it can be run separately\n" +
"as well, as given below.</dd></dl><br /><p>\n" +
"To perform <tt class=\"file-path\">iqmsg</tt> checking, run below\n" +
"command as <tt class=\"file-path\">root</tt> user on the following server\n" +
"types:</p>\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Statistics (standalone)</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Statistics Coordinator</li></ul><p>\n" +
"<tt class=\"input\"><b># bash /eniq/admin/bin/DbCheck.bsh -a iqmsg_check [-l <i class=\"var\">&lt;path_to_logfile&gt;</i>]</b></tt></p>\n" +
"</li></ol>\n" +
"\n"+
"</div>\n";

var C4_1_9=
"<div id=\"content\" class=\"body-content\">\n"+
"<h3><span class=\"CHAPNUMBER\">4.1.9 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_g605\"></a><a name=\"CHAPTER4.1.9\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Truncating DWHDB Transaction Log</a></span></h3>\n" +
"\n" +
"<p>\n" +
" The <tt class=\"file-path\">dwhdb</tt> transaction logs do not have\n" +
"a limit, but the <tt class=\"file-path\">dwhdb</tt> partition that contains\n" +
"the logs has defined limit.  To avoid filling up of <tt class=\"file-path\">dwhdb</tt> partition, the transaction logs should be rolled over. Check the\n" +
"current size of <tt class=\"file-path\">dwhdb.log</tt>. If the size\n" +
"is greater than 10 GB perform below step</p>\n" +
"\n" +
"<div class=\"ADMON\"><span class=\"ADMONMSG2\">Attention!</span><p class=\"ADMONDESC\">\n" +
"Only do the following during a Maintenance Window, as this procedure\n" +
"involves Database downtime.</p>\n" +
"</div>\n" +
"<p>\n" +
"To truncate <tt class=\"file-path\">dwhdb</tt> transaction log, run\n" +
"the command below on following server types</p>\n" +
"\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">ENIQ Statistics (standalone)</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Statistics Coordinator</li></ul>\n" +
"<p><tt class=\"input\"><b>{root} #: bash /eniq/admin/bin/transaction_log_admin.bsh -t\n" +
"dwhdb -R -l /eniq/local_logs/transaction_log_admin.txt -N</b></tt></p>\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Verify the below logs to ensure successful of step 4.1.9<p>\n" +
"Example output: </p>\n" +
"<p>\n" +
"<tt class=\"file-path\">Checking for correct user and server type to exectue\n" +
"script. <br />=&lt; 2016-03-02_09.18.20 - Starting /eniq/admin/bin/transaction_log_admin.bsh\n" +
"&gt;= <br />Getting an ordered list of server(s).<br />Setting engine to\n" +
"noloads and wating for execution slots to empty<br />Running engine\n" +
"command for NoLoads locally<br />Oracle Corporation      SunOS 5.10\n" +
"     Generic Patch   January 2005<br />Changing profile to: NoLoads<br />Change profile requested successfully<br />Backing up original /eniq/database/dwh_main/dwhdb.cfg\n" +
"file<br />Oracle Corporation      SunOS 5.10      Generic Patch   January\n" +
"2005<br />Starting the process to truncate the database transaction\n" +
"log.<br />Running sql in the database.<br />Oracle Corporation     \n" +
"SunOS 5.10      Generic Patch   January 2005<br />Execution time: 0.005\n" +
"seconds<br />Execution time: 0.054 seconds<br />Execution time: 0.051\n" +
"seconds<br />Execution time: 0.053 seconds<br />Stopping the database.<br />==============&lt; 2016-03-02_09.18.27 - Stopping ENIQ services\n" +
"&gt;============<br />2016-03-02_09.18.27 - Stopping ENIQ service svc:/eniq/dwhdb<br /> ENIQ services stopped correctly on atrcxb2332<br />Starting the\n" +
"database with -m flag to truncate transaction log.<br />=======&lt;\n" +
"2016-03-02_09.18.33 - Starting ENIQ services on atrcxb2332 &gt;=====<br />2016-03-02_09.18.33 - Starting ENIQ service svc:/eniq/dwhdb<br />ENIQ\n" +
"services started correctly on atrcxb2332<br />Restoring original /eniq/database/dwh_main/dwhdb.cfg\n" +
"file<br />Oracle Corporation      SunOS 5.10      Generic Patch   January\n" +
"2005<br />Restarting the database.<br />=============&lt; 2016-03-02_09.18.59\n" +
"- Restarting ENIQ services &gt;===========<br /> 2016-03-02_09.18.59 -\n" +
"Stopping ENIQ service svc:/eniq/dwhdb<br />2016-03-02_09.19.00 - Starting\n" +
"ENIQ service svc:/eniq/dwhdb<br />Renaming the database transaction\n" +
"log from dwhdb.log to dwhdb.tran.<br />==============&lt; 2016-03-02_09.19.25\n" +
"- Stopping ENIQ services &gt;============<br />2016-03-02_09.19.25 - Stopping\n" +
"ENIQ service svc:/eniq/dwhdb<br />ENIQ services stopped correctly on\n" +
"atrcxb2332<br />Oracle Corporation      SunOS 5.10      Generic Patch\n" +
"  January 2005<br />SQL Anywhere Transaction Log Utility Version 16.0.0.809<br />\"/eniq/database/dwh_main/dwhdb.db\" was using log file \"dwhdb.log\"<br />\"/eniq/database/dwh_main/dwhdb.db\" is using no log mirror file<br />\"/eniq/database/dwh_main/dwhdb.db\" is now using log file \"/eniq/database/dwh_main/dwhdb.tran\"<br />Transaction log starting offset is 0184019024<br />Transaction log\n" +
"current relative offset is 0000004300<br />=======&lt; 2016-03-02_09.19.27\n" +
"- Starting ENIQ services on atrcxb2332 &gt;=====<br />2016-03-02_09.19.27\n" +
"- Starting ENIQ service svc:/eniq/dwhdb<br />ENIQ services started\n" +
"correctly on atrcxb2332<br />Setting engine to original profile.<br />Running engine command for Normal locally<br />Oracle Corporation\n" +
"     SunOS 5.10      Generic Patch   January 2005<br />Changing profile\n" +
"to: Normal<br />Change profile requested successfully<br />=&lt; 2016-03-02_09.19.53\n" +
"- Finishing /eniq/admin/bin/transaction_log_admin.bsh &gt;=</tt></p>\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>If the above steps are not successfully completed contact\n" +
"the Ericsson support team.</dd></dl><br /></li></ol>\n" +
"\n" +
"\n"+
"</div>\n";

var C4_2=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">4.2 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_qmux\"></a><a name=\"CHAPTER4.2\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Managing ENIQ System</a></span></h2>\n" +
"\n" +
"<p>\n" +
" It is only recommended to take snapshots during a planned system\n" +
"maintenance activity. </p>\n" +
"\n" +
"<p>\n" +
"During an upgrade or backup procedure you may be requested to take\n" +
"or release a snapshot. </p>\n" +
"\n" +
"<p>\n" +
"Snapshots should only be left on the ENIQ server for the period\n" +
"required to perform the maintenance activity. Snapshots use significant\n" +
"disk space. </p>\n" +
"\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">Before performing a rollback in a Blade or Multi-Blade\n" +
"deployment ensure that only one Snapshot exists on the system. If\n" +
"more than one Snapshot exists the rollback fails.</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">If snapshots are taken using the <tt class=\"file-path\">prepare_eniq_bkup.bsh</tt> script, then only one active rolling snapshot will exist. Since\n" +
"rolling snapshot and manual snapshot (upgrade, EU snapshot) both use\n" +
"the same <tt class=\"file-path\">prepare_eniq_bkup.bsh</tt> script and\n" +
"this ensures only one active rolling snapshot exists at a time. </li></ul></dd></dl><br />\n" +
"\n" +
"\n"+
"</div>\n";

