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
"<h3><span class=\"CHAPNUMBER\">4.1.1 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_125l\"></a><a name=\"CHAPTER4.1.1\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Monitoring the services using SMF</a></span></h3>\n" +
"\n" +
"<p>\n" +
"The ENIQ Admin UI provides monitoring both of the ENIQ components\n" +
"installed on the ENIQ server(s) and of SAP IQ. Please refer to <a href='javascript:parent.parent.parent.showAnchor(\"AdminUI\")' class=\"xref\">Section  5</a> for monitoring using the Admin UI. No monitoring\n" +
"from Solaris side is required, unless the Admin UI is down. In case\n" +
"the Admin UI is not accessible, the Solaris Service Management Facility\n" +
"shall be used to check the services status. Login as <tt class=\"file-path\">root</tt> user and run the following command:</p>\n" +
"\n" +
"<p>\n" +
"<tt class=\"input\"><b>/usr/bin/svcs | grep eniq</b></tt></p>\n" +
"\n" +
"<p>\n" +
"Please check all the required services are online. </p>\n" +
"\n" +
"<p>\n" +
"Example output of eniq services:</p>\n" +
"\n" +
"<p>\n" +
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
"\n" +
"<p>\n" +
"If any of the required services are not online and need to be restarted,\n" +
"please proceed according to the SMF Service Management chapter. See <a href='javascript:parent.parent.parent.showAnchor(\"EniqServiceCommands\")' class=\"xref\">Section  8</a> for more details of SMF service management\n" +
"if needed</p>\n" +
"\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>The services above are examples, depending on your installation\n" +
"rack/blade you may have different eniq services</dd></dl><br />\n" +
"\n"+
"</div>\n";

var C4_1_2=
"<div id=\"content\" class=\"body-content\">\n"+
"<h3><a name=\"smf.manage\"></a><span class=\"CHAPNUMBER\">4.1.2 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_figd\"></a><a name=\"CHAPTER4.1.2\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">SMF Service\n" +
"Management</a></span></h3>\n" +
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
"The ENIQ Admin UI provides monitoring of SAP IQ and ASA.. Please\n" +
"refer to <a href='javascript:parent.parent.parent.showAnchor(\"AdminUI\")' class=\"xref\">Section  5</a> for information on monitoring using\n" +
"the Admin UI. No monitoring from SAP IQ and ASA side is required,\n" +
"unless the Admin UI is down. In case the Admin UI is not accessible,\n" +
"the status of SAP IQ and ASA can be checked from the command line.\n" +
"Login as user <tt class=\"file-path\">dcuser</tt> and\n" +
"run the following command:</p>\n" +
"\n" +
"<p>\n" +
"<tt class=\"input\"><b>dbisql -c \"UID=DBA;PWD=<i class=\"var\">&lt;DBA password, default SQL&gt;</i>\" -host localhost -port <i class=\"var\">&lt;database port&gt;</i> -nogui</b></tt></p>\n" +
"\n" +
"<p>\n" +
"At the prompt, run</p>\n" +
"\n" +
"<p>\n" +
"For SAP IQ (DWHDB) </p>\n" +
"\n" +
"<p>\n" +
"<tt class=\"input\"><b>sp_iqstatus</b></tt></p>\n" +
"\n" +
"<p>\n" +
"For SAP ASA (REPDB) </p>\n" +
"\n" +
"<p>\n" +
"<tt class=\"input\"><b>sa_db_properties</b></tt></p>\n" +
"\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>The default port number for DWHDB is <strong class=\"MEDEMPH\">2640</strong>, the\n" +
"port number for REPDB is <strong class=\"MEDEMPH\">2641</strong></dd></dl><br />\n" +
"<p>\n" +
"Check for usage of main space (Main IQ Blocks used). If the main\n" +
"space usage is over 95% of the main database space, the database might\n" +
"need to be expanded. Please contact Ericsson support for assistance. </p>\n" +
"\n" +
"<p>\n" +
"Temp space (Temp IQ Blocks used) is used by IQ mainly to handle\n" +
"large queries, and it's allocated and released continuously. For the\n" +
"above reason sporadic monitoring of temp space is not useful. Filling\n" +
"up temp space can lead to failure of one or more queries running (not\n" +
"mentioning a substantial performance impact). If a query or ENIQ task\n" +
"is failing because of temp space filling up please contact Ericsson\n" +
"support for assistance. </p>\n" +
"\n" +
"\n"+
"</div>\n";

var C4_1_4=
"<div id=\"content\" class=\"body-content\">\n"+
"<h3><span class=\"CHAPNUMBER\">4.1.4 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_bwdb\"></a><a name=\"CHAPTER4.1.4\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Monitoring file system usage</a></span></h3>\n" +
"\n" +
"<p>\n" +
"The ENIQ Admin UI provides a basic monitoring of the file system\n" +
"usage. Please refer to Reference [2] for information on monitoring\n" +
"using the Admin UI. No monitoring from Solaris side is required, unless\n" +
"the Admin UI is down.  In case the Admin UI is not accessible, login\n" +
"as user root user and run the following command:</p>\n" +
"\n" +
"<p>\n" +
"<strong class=\"MEDEMPH\">ZFS File systems</strong></p>\n" +
"\n" +
"<p><tt class=\"input\"><b># /usr/sbin/zfs list</b></tt></p>\n" +
"<p>\n" +
"Please check there is space available on the file system. A sample\n" +
"output:</p>\n" +
"\n" +
"<p><tt class=\"output\">NAME USED AVAIL REFER MOUNTPOINT </tt></p>\n" +
"<p><tt class=\"output\">... </tt></p>\n" +
"<p><tt class=\"output\">eniq_sp_1/dwh_main 1.09G 1.12T 1.09G /eniq/database/dwh_main </tt></p>\n" +
"<p><tt class=\"output\">eniq_sp_1/dwh_main_dbspace 1.43T 1.12T 1.43T /eniq/database/dwh_m </tt></p>\n" +
"<p><tt class=\"output\">eniq_sp_1/dwh_temp_dbspace 489G 1.12T 489G /eniq/database/dwh_t </tt></p>\n" +
"<p><tt class=\"output\">eniq_sp_1/rep_main 2.27G 1.12T 2.27G /eniq/database/rep_main </tt></p>\n" +
"<p><tt class=\"output\">eniq_sp_1/rep_temp 1.00G 1.12T 1.00G /eniq/database/rep_temp </tt></p>\n" +
"<p><tt class=\"output\">...</tt></p>\n" +
"<p>\n" +
"If one of the file systems is over 90%, please identify it and\n" +
"contact Ericsson support.</p>\n" +
"\n" +
"<p>\n" +
"<strong class=\"MEDEMPH\">NAS File systems</strong></p>\n" +
"\n" +
"<p>\n" +
"In the case of ENIQ installed on a Blade you must also check the\n" +
"NAS file systems.</p>\n" +
"\n" +
"<p>\n" +
"Login to the NAS console as user <tt class=\"file-path\">master</tt> and run the following command:</p>\n" +
"\n" +
"<p><tt class=\"input\"><b>storage fs list</b></tt></p>\n" +
"<p>\n" +
"Please check there is space available on the file systems. A sample\n" +
"output:</p>\n" +
"\n" +
"<p><tt class=\"output\">FS                        STATUS       SIZE    LAYOUT	MIRRORS\n" +
"  COLUMNS   USE%  NFS SHARED  CIFS SHARED  SECONDARY TIER  POOL LIST</tt></p>\n" +
"<p><tt class=\"output\">========================= ======       ====    ======   =======\n" +
"  =======   ====  ==========  ===========  ==============  =========</tt></p>\n" +
"<p><tt class=\"output\">stats1-admin              online      2.00G    simple   -\n" +
"        -           3%    yes          no           no           stats1</tt></p>\n" +
"<p><tt class=\"output\">stats1-archive            online      8.00G    simple   -\n" +
"        -           0%    yes          no           no           stats1</tt></p>\n" +
"<p><tt class=\"output\">stats1-backup             online     10.00G    simple   -\n" +
"        -           1%    yes          no           no           stats1</tt></p>\n" +
"<p><tt class=\"output\">stats1-etldata            online     50.00G    simple   -\n" +
"        -           0%    yes          no           no           stats1</tt></p>\n" +
"<p><tt class=\"output\">stats1-fmdata             online      1.00G    simple   -\n" +
"        -           6%    yes          no           no           stats1</tt></p>\n" +
"<p><tt class=\"output\">stats1-home               online     10.00G    simple   -\n" +
"        -           1%    yes          no           no           stats1</tt></p>\n" +
"<p><tt class=\"output\">.......................</tt></p>\n" +
"<p><tt class=\"output\">.......................</tt></p>\n" +
"<p>\n" +
"If one of the file systems is over 90%, please identify it and\n" +
"contact Ericsson support.</p>\n" +
"\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>The above list is an example, depending on your installation\n" +
"you may have additional filesystems listed</dd></dl><br />\n" +
"\n"+
"</div>\n";

var C4_1_5=
"<div id=\"content\" class=\"body-content\">\n"+
"<h3><span class=\"CHAPNUMBER\">4.1.5 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_g5m3\"></a><a name=\"CHAPTER4.1.5\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Monitoring the ZFS pool</a></span></h3>\n" +
"\n" +
"<p>\n" +
" Login as <tt class=\"file-path\">root</tt> user\n" +
"and run the following command:</p>\n" +
"\n" +
"<p>\n" +
"<tt class=\"input\"><b>/usr/sbin/zpool status</b></tt></p>\n" +
"\n" +
"<p>\n" +
"Check that <strong class=\"MEDEMPH\">the pool is online and no data errors\n" +
"are detected</strong>, and all the mirrors are on line and healthy.\n" +
" In case ZFS reports that a device is faulty, <strong class=\"MEDEMPH\">it must be physically replaced. </strong>Please contact your HW support\n" +
"immediately if this occurs. No action is required at application level\n" +
"unless ZFS detects that the pool is offline or data corruption occurred.\n" +
"In case ZFS detects that the pool is offline or data corruption occurred,\n" +
"please take no action on ZFS, shutdown any application component (if\n" +
"any still running) according to the ENIQ SAG and contact immediately\n" +
"the Ericsson support.</p>\n" +
"\n" +
"\n"+
"</div>\n";

var C4_1_6=
"<div id=\"content\" class=\"body-content\">\n"+
"<h3><span class=\"CHAPNUMBER\">4.1.6 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_4q8t\"></a><a name=\"CHAPTER4.1.6\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Monitoring the NAS pool (Blade\n" +
"only)</a></span></h3>\n" +
"\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd><strong class=\"MEDEMPH\">This is only applicable where ENIQ is installed on\n" +
"a blade</strong></dd></dl><br />\n" +
"<p>\n" +
"Login to the NAS console as user master and run the following command:</p>\n" +
"\n" +
"<p><tt class=\"input\"><b>storage pool free</b></tt></p>\n" +
"\n"+
"</div>\n";

var C4_1_7=
"<div id=\"content\" class=\"body-content\">\n"+
"<h3><span class=\"CHAPNUMBER\">4.1.7 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_5ozx\"></a><a name=\"CHAPTER4.1.7\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Monitoring the NAS filesystems\n" +
"(Blade only)</a></span></h3>\n" +
"\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd><strong class=\"MEDEMPH\">This is only applicable where ENIQ is installed on\n" +
"a blade</strong></dd></dl><br />\n" +
"<p>\n" +
"For the NAS filesystems to mount, the NASd service and the NAS-online\n" +
"milestone must  be online.</p>\n" +
"\n" +
"<p>\n" +
" To check this run the following command:</p>\n" +
"\n" +
"<p><tt class=\"input\"><b># /usr/bin/svcs | grep NAS</b></tt></p>\n" +
"<p>\n" +
"Please ensure both the service and milestone are online</p>\n" +
"\n" +
"<p><tt class=\"output\">online         Jul_04   svc:/storage/NASd:default</tt></p>\n" +
"<p><tt class=\"output\">online         Jul_04   svc:/milestone/NAS-online:default</tt></p>\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd> See section <a href='javascript:parent.parent.parent.showAnchor(\"smf.manage\")' class=\"xref\">Section  4.1.2</a> to manage SMF services</dd></dl><br />\n" +
"<p>\n" +
"The milestone should come online once the NASd service is online\n" +
"(unless it was manually disabled). </p>\n" +
"\n" +
"<p>\n" +
"The following log file can be referenced for troubleshooting errors:</p>\n" +
"\n" +
"<p><tt class=\"output\">/eniq/local_logs/NASd/NASd.log</tt></p>\n" +
"\n" +
"\n"+
"</div>\n";

var C4_2=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">4.2 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_qmux\"></a><a name=\"CHAPTER4.2\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Managing ENIQ System</a></span></h2>\n" +
"\n" +
"<p>\n" +
" It is not recommended to take snapshots out of planned system\n" +
"maintenance activity. It might be requested by upgrade or backup procedure\n" +
"that snapshots shall be taken or released. Snapshots should be left\n" +
"on the ENIQ server only for the time required to perform the maintenance\n" +
"activity, since they freeze an image of the file system and use disk\n" +
"space. Leaving a snapshot on the file system for an indefinite length\n" +
"of time will lead to a file system full scenario. </p>\n" +
"\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>If using a Blade or Multi Blade deployment, only <strong class=\"MEDEMPH\">ONE</strong> Snapshot can exist before attempting a rollback\n" +
", having more than one snapshot existing WILL cause the rollback to\n" +
"fail.<p>\n" +
"If snapshots are taken by the recommended and supported way ( i.e.\n" +
"using the script<tt class=\"input\"><b> prepare_eniq_bkup.bsh</b></tt>  ) then at a\n" +
"time only one active rolling snapshot will exist as rolling snapshot\n" +
"and manual (upgrade , EU snapshot) both use the same framework (i.e.\n" +
"the script <tt class=\"input\"><b>prepare_eniq_bkup.bsh</b></tt>) and it will take\n" +
"care by itself to keep one active rolling snapshot at a time. </p>\n" +
"</dd></dl><br />\n" +
"\n" +
"\n"+
"</div>\n";

