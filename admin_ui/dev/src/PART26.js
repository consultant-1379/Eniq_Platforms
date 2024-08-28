var C26=
"<div id=\"content\" class=\"body-content\">\n"+
"<h1><span class=\"CHAPNUMBER\">26 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_pavy\"></a><a name=\"CHAPTER26\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Troubleshooting</a></span></h1>\n" +
"\n" +
"<p>\n" +
"This chapter describes some possible problems that can occur in\n" +
"ENIQ and presents possible solutions.</p>\n" +
"\n" +
"<p><a name=\"trblshooting\"></a>\n" +
"Contact Ericsson Customer Support for additional\n" +
"assistance.</p>\n" +
"\n" +
"\n"+
"</div>\n";

var C26_1=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><a name=\"troublechapter\"></a><span class=\"CHAPNUMBER\">26.1 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"troubleshooting\"></a><a name=\"CHAPTER26.1\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Troubleshooting\n" +
"Ericsson Network IQ</a></span></h2>\n" +
"\n" +
"<p><a name=\"DailyAggregation\"></a>\n" +
"The table below lists possible troubles and actions to perform\n" +
"to solve them.</p>\n" +
"\n" +
"\n" +
"<a name=\"TABLE21\"></a>\n" +
"<table class=\"tblgrp\" width=\"100%\">\n" +
"<caption>Table 21 &nbsp;&nbsp; Troubleshooting ENIQ</caption>\n" +
"<tr>\n" +
"<td>\n" +
"<table class=\"tblcnt\" width=\"100%\">\n" +
"<tr valign=\"top\">\n" +
"<th align=\"left\" valign=\"top\" width=\"25%\">\n" +
"\n" +
"<p><strong class=\"MEDEMPH\">Description</strong></p></th>\n" +
"<th align=\"left\" valign=\"top\" width=\"20%\">\n" +
"\n" +
"<p><strong class=\"MEDEMPH\">Cause</strong> </p></th>\n" +
"<th align=\"left\" valign=\"top\" width=\"55%\">\n" +
"\n" +
"<p><strong class=\"MEDEMPH\">Action</strong></p></th></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"<p>AdminUI is not available at<br /><tt class=\"file-path\">http://<i class=\"var\">YourLoaderServerIP</i>:8080/adminui/</tt></p><br />\n" +
"<p> in the case of a multi blade deployment on the coordinator blade</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p>ENIQ Web Server process is not running.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p>Make sure ENIQ Web Server process is running by executing <tt class=\"LITERALMONO\">/eniq/sw/bin/webserver status</tt> or in the case of a multi\n" +
"blade deployment execute the command on the coordinator blade.</p><br />\n" +
"<p>If the process is not running, start it by executing <tt class=\"LITERALMONO\">/eniq/sw/bin/webserver start</tt>.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"<p>No data is loaded.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p>There are no files in the mounted OSS directories, or in <tt class=\"LITERALMONO\">/eniq/data/pmdata</tt> directory on ENIQ Server</p><br />\n" +
"<p><strong class=\"MEDEMPH\">or</strong></p><br />\n" +
"<p>An ENIQ process is down</p><br />\n" +
"<p><strong class=\"MEDEMPH\">or</strong></p><br />\n" +
"<p>System is in maintenance mode (execution profile is in NoLoads)</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p>If using OSS-RC, check that PM mediators collect data files\n" +
"from the network elements, and that symbolic links are created under\n" +
"the following directories on OSS: <tt class=\"LITERALMONO\">/var/opt/ericsson/pmData/</tt> x86 servers.</p><br />\n" +
"<p>The same links should appear under the <tt class=\"LITERALMONO\">/eniq/data/pmdata</tt> directory on ENIQ Server.</p><br />\n" +
"<p>Check status of scheduler, engine, dwhdb, and repdb.</p><br />\n" +
"<p><strong class=\"MEDEMPH\">or</strong></p><br />\n" +
"<p>If using OSS-RC, check that PM mediators are collecting data,\n" +
"NFS/SSH mount is working.</p><br />\n" +
"<p><strong class=\"MEDEMPH\">or</strong></p><br />\n" +
"<p>Check the DWHDB filling ratio (this information is stored in <tt class=\"file-path\">/eniq/log/sw_log/iq/dwhdb_usage.log</tt></p><br />\n" +
"<p>The filling ratio log file is created the first time the ratio\n" +
"exceeds 85%. If there is no <tt class=\"file-path\">dwhdb_usage.log</tt>, the ratio has never exceeded 85%.</p><br />\n" +
"<p>If the ratio is over 90%, the database is considered full and\n" +
"ENIQ is switched to maintenance mode.</p><br />\n" +
"<p>If the DWHDBs filling ratio has been over 90% but no recent log\n" +
"entries are found (filling ratio has dropped below 90%), switch ENIQ\n" +
"back to normal mode. Type on the command line: <br /><tt class=\"input\"><b>engine\n" +
"-e changeProfile Normal</b></tt></p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"<p>Database is full.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p>The database is too small for the amount of data.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p>Contact Ericsson Customer Support.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"<p>Engine does not start.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p>See <a href='javascript:parent.parent.parent.showAnchor(\"tbl-error\")' class=\"xref\"> Table 20</a>.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p>See <a href='javascript:parent.parent.parent.showAnchor(\"tbl-error\")' class=\"xref\"> Table 20</a>.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"<p>Server has ran out of disc space.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p>Old snapshot is consuming unnecessary disc space.</p><br />\n" +
"<p><strong class=\"MEDEMPH\">or</strong> </p><br />\n" +
"<p>ENIQ logging has been left on detailed level </p><br />\n" +
"<p><strong class=\"MEDEMPH\">or </strong></p><br />\n" +
"<p>ENIQ has been receiving faulty data which is not cleared out from <tt class=\"file-path\">/eniq/data/rejected/ /eniq/data/etldata/*/failed</tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p>Remove unnecessary snapshots if such exist.</p><br />\n" +
"<p>Remove unnecessary log files and decrease the logging level.</p><br />\n" +
"<p>Remove unnecessary faulty data and check the data mediation.</p><br />\n" +
"<p>After the removals, stop all ENIQ processes and start them again.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"<p>After installation, system loads data, but monitoring does\n" +
"not show measurement types</p><br />\n" +
"<p><strong class=\"MEDEMPH\">or</strong></p><br />\n" +
"<p>A new measurement type is activated in the system, but monitoring\n" +
"is not showing the new measurement type.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p>Monitored types are not updated.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p>The System gets measurement types from data stream dynamically\n" +
"once a day and monitoring is build upon these measurement types. It\n" +
"may, therefore, take a day for the system to start monitoring the\n" +
"new measurement type(s). To start monitoring the new measurement types\n" +
"immediately:</p><br />\n" +
"<ul>\n" +
"<li><p>In AdminUI, click <b class=\"object\">ETLC Set Scheduling</b></p>\n" +
"</li>\n" +
"<li><p>From <b class=\"object\">Package</b>, select <tt class=\"input\"><b><i class=\"var\">DWH_MONITOR</i></b></tt>.</p>\n" +
"</li>\n" +
"<li><p>Select <b class=\"object\">UpdateMonitoredTypes</b> and click <b class=\"object\">Start</b>.</p>\n" +
"</li>\n" +
"<li><p>In <b class=\"object\">ETLC Set Scheduling</b>, select <tt class=\"input\"><b><i class=\"var\">DWH_MONITOR</i></b></tt>.</p>\n" +
"</li>\n" +
"<li><p>Select <b class=\"object\">UpdateMonitoring</b> and click <b class=\"object\">Start</b> </p>\n" +
"</li>\n" +
"</ul>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"<p>Monitoring is showing deactivated measurement types.</p><br />\n" +
"<p><strong class=\"MEDEMPH\">or</strong></p><br />\n" +
"<p>Monitoring is showing deleted measurement types.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p>Monitored types are not updated.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p>When you set a type as inactive using AdminUI, by default,\n" +
"the type is no longer monitored after three days.</p><br />\n" +
"<p>The default value can be changed by changing the <tt class=\"LITERALMONO\">lookahead</tt> UpdateMonitoring property of the DWH_MONITOR tech pack. The <tt class=\"LITERALMONO\">lookahead</tt> value shows the number of days from today (the\n" +
"default is 2).  To change the value, contact  Ericsson Customer Support.</p><br />\n" +
"<p><strong class=\"MEDEMPH\">or</strong></p><br />\n" +
"<p>The System gets measurement types from data stream dynamically\n" +
"once a day and monitoring is build upon these measurement types. If\n" +
"you delete a measurement type, but the data stream includes measurements\n" +
"of the deleted type, the type you deleted continues to be displayed\n" +
"in monitoring.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"<p>After loading late data, AdminUI does not show data that\n" +
"is older than three days.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p>By default, loaded late data that is older than three days\n" +
"is not updated to AdminUI.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"\n" +
"<ul>\n" +
"<li><p>After loading late data, in AdminUI, click <b class=\"object\">ETLC\n" +
"Set Scheduling</b>.</p>\n" +
"</li>\n" +
"<li><p>From <b class=\"object\">Set type</b>, select <tt class=\"input\"><b><i class=\"var\">Maintenance</i></b></tt> and from <b class=\"object\">Package</b>, select <tt class=\"input\"><b><i class=\"var\">DWH_MONITOR</i></b></tt>.</p>\n" +
"</li>\n" +
"<li><p>Select <b class=\"object\">UpdateFirstLoadings</b> and click<b class=\"object\">Start.</b></p>\n" +
"</li>\n" +
"</ul><br />\n" +
"<p>Running this set updates the data loadings for AdminUI from the\n" +
"previous 30 days.</p><br />\n" +
"<p>This operation may take a long time if there is a large amount\n" +
"of data.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"<p>Engine, Scheduler, DWH database, or ETL database is showing\n" +
"a red light in AdminUI Loader status view.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p>The service is not running.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p>Make sure the service with the red light is running by\n" +
"following the instructions in this guide.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"<p>Sets are dropped from queue.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p>Each set enters the queue with a priority from 0-15. Priority\n" +
"is defined in the set. Each set is given a queue time limit. When\n" +
"the set has been in the queue for this time limit, the priority is\n" +
"increased. When a set reaches priority 15, and has not been entered\n" +
"into the execution slot, it waits for one more queue time limit and\n" +
"then it is removed from the priority queue. This can happen in a backlog\n" +
"situation.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p>Usually no action is required.</p><br />\n" +
"<p>If this problem occurs frequently, contact Ericsson Customer Support </p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"<p>Performance problems in RNC Tech Pack with DC_E_RAN_UCELL_V_PMRES\n" +
"measurement</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p>Data storage method.</p><br />\n" +
"<p>If there is a large amount of data to be stored, 15 minute time\n" +
"window is exceeded causing system backlog.</p><br />\n" +
"<p>Previously disabled counters are enabled after tech pack upgrade.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p>Disable the following UCELL_V_PMRES counters that are enabled\n" +
"by default: pmRes7, pmRes8, pmRes9, pmRes10, pmRes11, pmRes12 </p><br />\n" +
"<ul>\n" +
"<li><p>Log in ENIQ server or in the case of a multi blade deployment\n" +
"on the engine blade as dcuser.</p>\n" +
"</li>\n" +
"<li><p>Change profile to NoLoads:<br /> <tt class=\"input\"><b>engine -e changeProfile\n" +
"NoLoads</b></tt></p>\n" +
"</li>\n" +
"<li><p>Navigate to ENIQ admin directory: <br /><tt class=\"input\"><b>cd /eniq/admin/bin</b></tt></p>\n" +
"</li>\n" +
"<li><p>Run the following script to disable counters:<br /> <tt class=\"input\"><b>./disableCounters.bsh</b></tt></p>\n" +
"</li>\n" +
"<li><p>Navigate to root directory<br /> <tt class=\"input\"><b>cd /</b></tt></p>\n" +
"</li>\n" +
"<li><p>Reload engine configurations:<br /> <tt class=\"input\"><b>engine -e\n" +
"reloadConfig</b></tt></p>\n" +
"</li>\n" +
"<li><p>Change the profile back to normal:<br /> <tt class=\"input\"><b>engine\n" +
"-e changeProfile Normal</b></tt></p>\n" +
"</li>\n" +
"</ul><br />\n" +
"<p>If  not seeing better performance do engine restart:<br />  <tt class=\"input\"><b>engine restart</b></tt></p><br />\n" +
"<p>To enable the counters, perform the steps above, but instead of\n" +
"the disable script use <tt class=\"input\"><b>./enableCounters.bsh</b></tt></p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"<p>After services restart., <tt class=\"file-path\">repdb</tt> and <tt class=\"file-path\">engine</tt> will not leave maintenance\n" +
"state.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p>This is due to a known issue where <tt class=\"file-path\">stop_asiq</tt> does not always stop all running processes thereby leaving the services\n" +
"in state which cannot be cleared, even manually.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p>Check the log files for <tt class=\"file-path\">repdb</tt> and <tt class=\"file-path\">engine</tt> to identify which processes\n" +
"are hanging after restart and use <tt class=\"file-path\">kill -6</tt> on the hanging process</p><br />\n" +
"<p>The location of the logfile will depend on the system version.\n" +
"If System Version is prior to AOM 901 063 R4G the logfiles are available\n" +
"at: <tt class=\"file-path\">/eniq/sw/log/iq</tt>, post  AOM 901 063\n" +
"R4G, the logfiles are available at: <tt class=\"file-path\">/eniq/log/sw_log/iq</tt></p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"<p>Could not find a valid license for the busyhour configuration\n" +
"module! Please contact the system administrator.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p>No license exists for Busy Hour Configuration</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p>Order a license to use Busy Hour Configuration.</p><br />\n" +
"<p>Use <b class=\"object\">Show Installed Licenses</b> to verify that license\n" +
"installed.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"<p>Ebs service is unavailable because of technical problems\n" +
"(ex. engineRMI-connection...). See logs for more detailed information</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p>There is a problem with  java RMI</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p>Restart the engine, if this fails, it may be necessary\n" +
"to kill the old engine process</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"<p>Loader sets are failing with following error message: \n" +
"   <tt class=\"input\"><b>WARNING   1.UnPartitionedLoader General loader failure:\n" +
"com.sybase.jdbc3.jdbc.SybSQLException: SQL Anywhere Error -1009170:\n" +
"You have run out of space in IQ_SYSTEM_MAIN DBSpace.</b></tt><br /></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p>There are many open database connections or old transactions\n" +
"are ongoing in the server.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p>The amount of connections running to the database should\n" +
"be monitored and verify there are none remaining open. <br />Alternatively,\n" +
"contact Ericsson Customer Support to increase the IQ_SYSTEM_MAIN size\n" +
"to handle the amount of connections.</p>\n" +
"</td></tr></table></td></tr>\n" +
"<tr>\n" +
"<td>\n" +
"<table class=\"tblcnt tblcnt_margin\" width=\"100%\">\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"<p>Daily aggregation is happening very late for particular\n" +
"techpack due to data coming late and continues.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p>Please follow <a href='javascript:parent.parent.parent.showAnchor(\"DailyAggregationNot\")' class=\"xref\">Section  26.1.1</a></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p>Please follow <a href='javascript:parent.parent.parent.showAnchor(\"DailyAggregationNot\")' class=\"xref\">Section  26.1.1</a></p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"<p>Historical data prior to upgrade is not fetched in the\n" +
"reports as the Universe points to Count tables instead of Raw tables\n" +
"after ENIQ upgrade from 2.x or 11.x to 12.x track.. However, there\n" +
"is data in COUNT table only since the upgrade.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p>New count tables were created to support delta calculation\n" +
"for new Node releases, so pre-upgrade raw data was not populated in\n" +
"COUNT tables because newly implemented  table will get the data from\n" +
"the day it got added.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p>These steps should be followed when upgrade is performed\n" +
"from ENIQ 2.x or ENIQ 11.x to ENIQ 12.x track to migrate the data\n" +
"from RAW to COUNT table.</p><br />\n" +
"<p> The script is placed in /eniq/sw/installer directory and it shall\n" +
"be executed with dcuser permission.</p><br />\n" +
"<p> cd /eniq/sw/installer</p><br />\n" +
"<p> # ./data_migration.sh -t &lt;name_of_the_techpack&gt;</p><br />\n" +
"<p> Example:</p><br />\n" +
"<p> ./data_migration.sh -t DC_E_GGSN</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"<p>Connection lost to repdb database\" followed by irregular\n" +
"Engine issues <br />&bull; Engine throwing OutOfMemoryError</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p>Issue occurred due to engine memory running out of heap\n" +
"space. This is happening because of large size of RNC files </p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p>Please follow <a href='javascript:parent.parent.parent.showAnchor(\"ConnectionLost\")' class=\"xref\">Section  26.1.2</a></p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"<p>DIM tables are empty for all corresponding Vector Measurment\n" +
"counters (example : DC.DIM_E_RBS_HSDSCHRES_V_pmRemainingResourceCheck)</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p>The DWHM_StorageTimeUpdate set which updated these tables\n" +
"has not run correctly for the TP while installation/upgrade of the\n" +
"TP.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p>Run the DWHM_StorageTimeUpdate Set for the TP from the\n" +
"Adminui. Go to \"ETLC Set Sheduling\" in Adminui. Select Set Type as\n" +
"Techpack and Package as the TP for which you want to run the set.\n" +
"Then Start the DWHM_StorageTimeUpdate set for the TP.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"<p>Upgrade will fail if dcuser is not able to ssh to localhost\n" +
"without password </p><br />\n" +
"<p> <strong class=\"MEDEMPH\">or</strong></p><br />\n" +
"<p> ExecutionProfiler set fails on running the ENIQ Expansion procedure</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p>In the /eniq/home/dcuser/.ssh/directory,files id_rsa.pub\n" +
"and authorized_keys had different contents.  </p><br />\n" +
"<p>To login through ssh,these 2 files should have the same contents.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p>Before the  upgarde check the permission of authorized_keys\n" +
"file ,if permission are in root then  please change  the permission\n" +
"to dcuser then continue the upgrade.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"<p>DC_RELEASE not populated for DIM table related the individual\n" +
"vector counters. (Ex: DIM_E_RBS_NODEBFUNCTION_V_pmCapacityNodeBDlCe) </p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p>Whenever a TP is upgraded or initially installed, there\n" +
"is a set called StorageTimeAction as part of dwhmanager, which will\n" +
"run and update the data in the DIM tables for the individual vector\n" +
"counters. If this set had not run properly ,then this issue is caused.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p>To avoid this issue</p><br />\n" +
"<ul>\n" +
"<li><p>Go to ETLC Set Scheduling in Adminui.</p>\n" +
"</li>\n" +
"<li><p>Select set type as Techpack and Package as DC_E_RBS\n" +
"(or the TP having the problem)</p>\n" +
"</li>\n" +
"<li><p>Click on Start for DWHM_StorageTimeUpdate_DC_ E_RBS\n" +
"and the set will start. This will populate the data for DC_RELEASE\n" +
"column in the respective DIM tables. </p>\n" +
"</li>\n" +
"</ul>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"<p>Root partition has less than 2GB of free space</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p>User specific data on the root partition Or Obsolete files\n" +
"in <tt class=\"file-path\">/var/sadm/</tt>pkg</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p>Delete user specific data from root (/) partition ( i.e. <tt class=\"file-path\">/export/home</tt> directories etc). To store user specific\n" +
"data, use other partitions. </p><br />\n" +
"<p>Check if there are any \"obsolete\" files in <tt class=\"file-path\">/var/sadm/pkg</tt> : </p><br />\n" +
"<p><tt class=\"input\"><b># find /var/sadm/pkg/*/save/*-*/ -name obsolete.Z -exec\n" +
"ls {} \\;</b></tt></p><br />\n" +
"<p>If \"obsolete\" files found then delete using below command : </p><br />\n" +
"<p><tt class=\"input\"><b># find /var/sadm/pkg/*/save/*-*/ -name obsolete.Z -exec\n" +
"rm -rf {} \\;</b></tt></p><br />\n" +
"<p>Check the root file system: </p><br />\n" +
"<p><tt class=\"input\"><b># df -h /</b></tt></p><br />\n" +
"<p><tt class=\"input\"><b>Note: </b></tt>Removing the \"undo\" files associated with\n" +
"a patch means that it will no longer be possible to uninstall the\n" +
"patch.</p><br />\n" +
"<p> If less than 2GB of free space is available then delete \"undo\"\n" +
"files, except those associated with live upgrade, from <tt class=\"file-path\">/var/sadm/pkg</tt> :</p><br />\n" +
"<p><tt class=\"input\"><b> # find /var/sadm/pkg/*/save/*-*/ -name undo.Z | grep -v\n" +
"SUNWlu | xargs rm -rf </b></tt></p><br />\n" +
"<p><tt class=\"input\"><b># find /var/sadm/pkg/*/save/pspool/*/save/ *-* -name undo.Z\n" +
"| grep -v SUNWlu | xargs rm -rf</b></tt></p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"<p><tt class=\"file-path\">/eniq/home</tt> FS is full</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p>Unnecessary data in <tt class=\"file-path\">/eniq/home</tt> FS</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p>Remove unnecessary data from <tt class=\"file-path\">/eniq/home</tt> FS as extension of <tt class=\"file-path\">/eniq/home</tt> is not\n" +
"supported and it is already optimized.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"<p>Parsing errors seen as mentioned below</p><br />\n" +
"<p><tt class=\"output\">...is not supported in binary output mode</tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p>This is because the JVM cache is not updated properly after\n" +
"upgrade which results in parsing error. The JVM cache will be holding\n" +
"some data related to the process for a complete session. So if the\n" +
"cache is not refreshed when the JVM is restarted/not restarted, then\n" +
"the said error will occur</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p>Restart of Eniq services is required to get parsing working\n" +
"again.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"<p>DWHDB goes into maintenance state frequently</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p>The database size is configured less in the server than\n" +
"what is recommended for ENIQ, because of which database is full. The\n" +
"connection to the database is getting lost frequently and hence the\n" +
"database is going into maintenance state.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p>Dimension the database according to the hardware dimension\n" +
"tools provided</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"<p>Automatic aggregation not happening for TP after upgrade. </p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p>The root cause suspected as - </p><br />\n" +
"<p> 1. The Monitored Type may be active at that moment, but in Log_AggregationRules\n" +
"monitored type was not present. As a result when UpdateMonitoring\n" +
"is triggered, the set not updated the Log_AggregationStatus table. </p><br />\n" +
"<p> 2. During AutomaticAggregation we are checking the Status column\n" +
"of Log_AggregationStatus table, which should be either LOADED or BLOCKED,\n" +
"if the aggregation need to run.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p>To reaggregate data: In AdminUI, click Reaggregation. Select\n" +
"the time level, start time, end time, and tech pack. Click List to\n" +
"get a list of measurement types to be reaggregated. Select the measurement\n" +
"types to be reaggregated and click Aggregate.</p><br />\n" +
"<p>  To run AutomaticREAggregation: In Adminui, click ETLC Set Scheduling.\n" +
"Select set type as 'Maintenance' and Package as 'DWH_MONITOR'. Click\n" +
"start AutomaticREAggregation.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"<p>After init 6,File parsing and Loading stopped.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p>ENIQ services did not start in proper order after init\n" +
"6.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p>Restart the ENIQ services.Refer <a href='javascript:parent.parent.parent.showAnchor(\"restartEniqServices\")' class=\"xref\">Section  12.7</a>.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"\n" +
"<ul>\n" +
"<li><p>DC_E_ERBS_CELLFDDBH_RANKBH_CELLFDD_PP0</p>\n" +
"</li>\n" +
"<li><p>DC_E_ERBS_CELLFDDBH_RANKBH_CELLFDD_PP1</p>\n" +
"</li>\n" +
"<li><p>DC_E_ERBS_ERBSBH_RANKBH_ERBS_PP0</p>\n" +
"</li>\n" +
"<li><p>DC_E_ERBS_EUTRANCELLFDD_DAYBH_CELLFDD</p>\n" +
"</li>\n" +
"<li><p>DC_E_ERBS_EUTRANCELLFDD_V_DAYBH_CELLFDD</p>\n" +
"</li>\n" +
"<li><p>DC_E_ERBS_CAPACITYCONNECTEDUSERS_V_DAYBH_ERBS</p>\n" +
"</li>\n" +
"<li><p>DC_E_CPP_PROCESSORLOADBH_RANKBH_PROCESSORLOAD_PP0</p>\n" +
"</li>\n" +
"<li><p>DC_E_CPP_PROCESSORLOAD_V_DAYBH_PROCESSORLOAD</p>\n" +
"</li>\n" +
"</ul>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p>If LTE Ran Load Expert (LLE) feature is not installed in\n" +
"the ENIQ server, then the following aggregators will not have data\n" +
"to calculate. Hence, the aggregators will go to Failed Dependency.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p>To avoid this issue the LLE license needs to be <strong class=\"MEDEMPH\">ACTIVE</strong> and the LLE techpack should be installed.\n" +
"The feature should be configured using the files AcceptableRates,\n" +
"backOffCell, backOffErbs. For details of configuring these files please\n" +
"refer <a href='javascript:parent.parent.parent.showAnchor(\"LTERANLoadExpert\")' class=\"xref\">Reference [24]</a> </p><br />\n" +
"<p><strong class=\"MEDEMPH\">Note : </strong> The busy hour configuration\n" +
"is only required for LTE Ran Load Expert feature. It is totally an\n" +
"optional feature. It has no impact on other functionality. If not\n" +
"interested please ignore the aggregation status of the following aggregators.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"<p>Discrepancy in PERIOD_DURATION value between DAY and RAW\n" +
"tables</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p>PERIOD_DURATION value for DAY tables are fetched from DIM_TIMELEVEL.\n" +
"In DIM_TIMELEVEL , for column TABLELEVEL=DAY ,DURATIONMIN value is\n" +
"set to default value as 1440.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p>To get the accurate values for PERIOD_DURATION use DATACOVERAGE\n" +
"column from DAY table as it calculates (sum of (PERIOD_DURATION )from\n" +
"RAW), when a comparison of PERIOD_DURATION needs to be done between\n" +
"RAW and DAY tables. </p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"<p>DIM_E tables are empty for VECTOR measurements, while measurements\n" +
"present in tables.</p><br />\n" +
"<p>Example of a DIM table for a VECTOR measurements: DIM_E_RBS_HSDSCHRES_V_pmRemainingResourceCheck</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p>When a TP upgrade/installs,DWHManager creates some .sql\n" +
"files in the eniq/upgrade/install directory of the Coordinator Blade\n" +
"which in turn will be loaded into the DB to load the data for the\n" +
"Vector DIM tables. </p><br />\n" +
"<p>Sometimes these .sql files doesn&rsquo;t get created properly\n" +
"because of which the Vector DIM tables remain empty. </p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p>If a VECOTR DIM table is empty, then there is a set &ndash;\n" +
"DWHMStorageTimeUpdate, which when run, will update the tables correctly.\n" +
"Go to ADMINUI -&gt; ETLC Set Scheduling. </p><br />\n" +
"<p>Select the &ldquo;Set type&rdquo; as &ldquo;Techpack&rdquo; and\n" +
"&ldquo;Package&rdquo; as the TP for which the VECTOR DIM table data\n" +
"is missing.Then run the corresponding &ldquo;DWHM_StorageTimeUpdate&rdquo;\n" +
"set for the TP.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"<p>ENIQ Reports is showing wrong value for the no of Counters\n" +
"in case of Partial Parsing and Loading.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p>The formula used to calculate the No of Counter in Partial\n" +
"Loading and Parsing is giving wrong value.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p>In the reports, the formula used to calculate the no. of\n" +
"counters for partial parsing and partial loading will not give the\n" +
"correct result.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"<p>As a reference to <b class=\"object\">HS50857</b> issue we found\n" +
"that few Loaders stuck in priority queue and not moving to execution\n" +
"slot for around 3 rops.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p>Alarm interface was also scheduled with 168 reports at\n" +
"that time which is a possible reason for the issue. Alarm reports\n" +
"are disabled and issue not seen again .</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p>Disable the reports or reduced the no of reports to be\n" +
"fetched at a time to 5-10.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"<p>System degradation due to NULLs coming in the topology\n" +
"tables.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p>The transformation cache has not updated correctly because\n" +
"of which the columns which get data from the transformations are getting\n" +
"values as NULLs.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p>Run the script - Topo_Null_Check.bsh present in the path\n" +
"-<tt class=\"input\"><b>/eniq/sw/bin</b></tt> to check and delete the NULLs. <strong class=\"MEDEMPH\">Usage of the script to check for tables having the NULLs (from dcuser/dcuser)\n" +
"-:</strong></p><br />\n" +
"<p>1)      chmod 775 Topo_Null_Check.bsh  </p><br />\n" +
"<p> 2)      bash ./Topo_Null_Check.bsh &gt; test.txt </p><br />\n" +
"<p> 3)      Check the test.txt file to see how many table have NULL\n" +
"values.</p><br />\n" +
"<p><strong class=\"MEDEMPH\">Usage of the script to delete the NULLs (from dcuser/dcuser)</strong> -:</p><br />\n" +
"<p> 1)      chmod 775 Topo_Null_Check.bsh </p><br />\n" +
"<p> 2)      bash ./Topo_Null_Check.bsh &ndash;fix</p><br />\n" +
"<p><tt class=\"input\"><b>Note</b></tt>-: If we get the following error - ./Topo_Null_Check.bsh:\n" +
"line 23: [: Msg 207, Level 16, State 0:SQL Anywhere Error -143: Column\n" +
"'oss_id' not found: integer expression expected , ignore the above\n" +
"message some topology tables might not contain the columns we are\n" +
"checking for nulls</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"<p>Engine loading stops frequently because of the engine stoppage\n" +
"happens frequently. </p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p>This is because the JVM cache is not updated properly after\n" +
"upgrade which results in loading error. The JVM cache will be holding\n" +
"some data related to the process for a complete session. So if the\n" +
"cache is not refreshed when the JVM is restarted/not restarted, then\n" +
"the said error will occur</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p>Restart of Eniq services is required to get Loading working\n" +
"again.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"<p>While defining the Monitoring rules , If MINSOURCE value\n" +
"is greater than the sourcecount , it will labeled the data of respective\n" +
"ROP as CHECK_FAILED .</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p>ENIQ will expect Minsource should be less than Sourcecount\n" +
"to have incoming data marked as loaded , If the mentioned condition\n" +
"FALSE, It will mark the data as CHECK_FAILED . And as according to\n" +
"the Normal eniq behavior the data is picked up during the Re-Aggregation\n" +
"time , which runs around at 1900 hrs daily.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p>Before applying any monitoring rule, check the sourcecount\n" +
"value from ShowLoadings in Adminui first then define the monitoring\n" +
"rule on respective Techpack. MinSource value should be configured\n" +
"less than SourceCount value.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"<p>In normal cases, during NAS fail over or SFS Reboot, the\n" +
"ENIQ services will be restarted automatically and do not need manual\n" +
"intervention. In rare cases the ENIQ services could be in a continuous\n" +
"maintenance state</p><br />\n" +
"<p>Please follow <a href='javascript:parent.parent.parent.showAnchor(\"EniqServices\")' class=\"xref\">Section  26.1.3</a>.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p>Symantec Fault</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p>Ensure the following services are online:</p><br />\n" +
"<p><tt class=\"input\"><b>svc:/storage/NASd:default</b></tt></p><br />\n" +
"<p><tt class=\"input\"><b>svc:/milestone/NAS-online:default</b></tt> </p><br />\n" +
"<p><tt class=\"input\"><b>svc:/licensing/sentinel:default</b></tt> </p><br />\n" +
"<p>Once these are online, then services should recover themselves.\n" +
"If services are still offline/maintenance then reboot the server by\n" +
"running init 6 in the ENIQ Statistics (standalone) server. In case\n" +
"of Multi blade Run init 6 in the below order.</p><br />\n" +
"<ul>\n" +
"<li><p>ENIQ Statistics Coordinator Server</p>\n" +
"</li>\n" +
"<li><p>ENIQ Statistics Engine Server</p>\n" +
"</li>\n" +
"<li><p>ENIQ Statistics Reader_1 Server</p>\n" +
"</li>\n" +
"<li><p>ENIQ Statistics Reader_2 Server</p>\n" +
"</li>\n" +
"</ul><br />\n" +
"<p>Then restart the services as mentioned in the section Refer <a href='javascript:parent.parent.parent.showAnchor(\"restartEniqServices\")' class=\"xref\">Section  12.7</a></p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"<p>ENIQ FS will be in read-only mode</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p> If any ENIQ FS on SFS reach defined threshold limit (depends\n" +
"on the configuration) then the FS will be in read only mode and ENIQ\n" +
"functionality will be lost. All ENIQ services will be down until FS\n" +
"can be cleaned up. After cleaned up the FS, the services can be restarted\n" +
"and functionality will be recovered.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p>To clear unwanted files, follow the below steps</p><br />\n" +
"<p>1) Login into NAS console and clean up the unwanted files </p><br />\n" +
"<p><tt class=\"input\"><b>Note</b></tt>  : You need to login to NAS directly as support\n" +
"user and clean up the problem directory on the NAS server itself.</p><br />\n" +
"<p>2) Restart NAS service. Refer Section <a href='javascript:parent.parent.parent.showAnchor(\"NasdRestart\")' class=\"xref\">Section  11.2.2</a></p><br />\n" +
"<p><tt class=\"input\"><b>Note</b></tt> : NAS service has dependency with Sentinel\n" +
"service, which will also needs to be in up and running state.</p><br />\n" +
"<p>3)Restart all the ENIQ services. Refer Section <a href='javascript:parent.parent.parent.showAnchor(\"restartall\")' class=\"xref\">Section  26.4.3</a></p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"<p>Symbolic links doesn't get deleted even though the file\n" +
"parsing has happened correctly. While loading, the following warning\n" +
"message gets printed in the Loader Engine log of the particular TP <tt class=\"output\">Move with memory copy failed, deleting the temporary file</tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p>While loading, the loader will pick up the temporary ascii\n" +
"or binary file from the output file path as defined in the TP interface.\n" +
"But if the TP cache is not refreshed correctly, then the temporary\n" +
"file does not get created correctly in the correct path leading to\n" +
"the warning message.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p>Refresh the TP cache by reactivating the interface by following\n" +
"the below process. In case of MB server, run the below steps from\n" +
"the coordinator blade as <tt class=\"input\"><b>dcuser</b></tt>.</p><br />\n" +
"<p><tt class=\"input\"><b>cd /eniq/sw/installer</b></tt></p><br />\n" +
"<p>Run the command as <tt class=\"input\"><b>./activate_interface -o &lt;OSS_NAME&gt;\n" +
"-i &lt;interface name&gt;</b></tt></p><br />\n" +
"<p> Example: <tt class=\"input\"><b>./activate_interface -o eniq_oss_1 -i INTF_DC_E_BULK_CM</b></tt></p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"<p>NAS-online service disabled and tries to enable automatically,\n" +
"at that time all services get restarted. But engine behaves abnormally.\n" +
"As a result no data loaded in dwhdb until ENGINE was restarted manually.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p>After NAS comes online, when engine is started prior to\n" +
"repdb, then it will wait till repdb comes online. Once repdb is online,\n" +
"Engine will start loading all the transformations before going ahead\n" +
"with its other tasks. But in this case the transformations did not\n" +
"get refreshed because of which loading problems started to occur.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p>If transformations does not get updated when engine restarts\n" +
"after NAS comes online, then engine needs to be restarted again to\n" +
"update the transformations correctly.</p>\n" +
"</td></tr></table></td></tr></table>\n" +
"\n"+
"</div>\n";

var C26_1_1=
"<div id=\"content\" class=\"body-content\">\n"+
"<h3><a name=\"DailyAggregationNot\"></a><span class=\"CHAPNUMBER\">26.1.1 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_tm1d\"></a><a name=\"CHAPTER26.1.1\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Daily aggregation\n" +
"is happening very late</a></span></h3>\n" +
"\n" +
"<p class=\"titled-block\">Cause</p><p>\n" +
"ENIQ triggers the aggregation for incomplete data only if last\n" +
"loading of data happened 1.5 hours before</p>\n" +
"<p>\n" +
"If the data loading pattern is late and continuous without having\n" +
"1.5 hour gap between two consecutive loading then ENIQ would not trigger\n" +
"initial aggregation.</p>\n" +
"\n" +
"<p class=\"titled-block\">Action</p><p>\n" +
"If customer wants to trigger initial aggregation at a fixed time\n" +
"with the available data (whatever data loaded by that time) for a\n" +
"particular techpack, following needs to be done.</p>\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Log in ENIQ server or in the case of a multi blade deployment\n" +
"on the engine blade as  <tt class=\"input\"><b>dcuser</b></tt></li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Edit the below file <br /><tt class=\"input\"><b>/eniq/sw/conf/static.properties</b></tt></li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Add below entries to the static.properties and save it. <br /><tt class=\"input\"><b>AggregationStartTP=&lt;tp_name&gt;</b></tt><br /><tt class=\"input\"><b>aggStartTime=&lt;HH:mm:ss&gt;</b></tt></li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Change profile to NoLoads:<br /> <tt class=\"input\"><b>engine -e changeProfile\n" +
"NoLoads</b></tt></li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Reload engine configurations:<br /> <tt class=\"input\"><b>engine -e reloadConfig</b></tt></li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Change the profile back to normal:<br /> <tt class=\"input\"><b>engine\n" +
"-e changeProfile Normal</b></tt></li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Example:<p>\n" +
" If static.properties has below entries</p>\n" +
"<p>\n" +
"<tt class=\"input\"><b>AggregationStartTP=DC_E_ERBS</b></tt> <br /> <tt class=\"input\"><b>aggStartTime=04:00:00</b></tt></p>\n" +
"<p>\n" +
"<tt class=\"input\"><b>Note:</b></tt> The above two parameters(DC_E_ERBS,04:00:00)\n" +
"should not contain any spaces.</p>\n" +
"<p>\n" +
"ENIQ will trigger the initial aggregation for DC_E_ERBS techpack\n" +
"around 04:00:00 am with the available data ( if aggregation is not\n" +
"started by that time).</p>\n" +
"<p>\n" +
"<tt class=\"input\"><b>Note:</b></tt> This feature works only with the techpack\n" +
"defined in static.properties. For others, aggregation should work\n" +
"in the normal way.</p>\n" +
"</li></ol>\n" +
"\n"+
"</div>\n";

var C26_1_2=
"<div id=\"content\" class=\"body-content\">\n"+
"<h3><a name=\"ConnectionLost\"></a><span class=\"CHAPNUMBER\">26.1.2 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_7gwy\"></a><a name=\"CHAPTER26.1.2\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Connection\n" +
"lost to repdb database</a></span></h3>\n" +
"\n" +
"<p>\n" +
"To avoid this issue we need to reduce the capacity of Max files\n" +
"per run</p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Log in ENIQ server as  <tt class=\"input\"><b>dcuser</b></tt></li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Stop the engine as <tt class=\"input\"><b>engine stop</b></tt></li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Go to  /eniq/sw/bin for the Script and Jar file which\n" +
"will be available there(set_maxper_run_value.bsh and MaxFilePerRunReduced.jar\n" +
") and run the command <tt class=\"input\"><b>chmod 777 &lt;script&gt;</b></tt></li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Run the following script to reduce the maxFilesPerRun\n" +
"value for RNC or RBS or RXI -<br /> <tt class=\"input\"><b>./set_maxper_run_value.bsh\n" +
"&ndash;change</b></tt></li><li class=\"STEPLIST\" id=\"THSSTYLE2\"><tt class=\"input\"><b>Note:</b></tt> 1.	OSS NAME (Ex :: eniq_oss_1) <br />2.	Node Name ( RNC or RBS or RXI ). The workaround currently runs\n" +
"for only these 3 Nodes.<br />3.	New MAXPER RUN value ( The MAXPER RUN\n" +
"value should be minimum twice the number of nodes being loaded ).<br />4. For every run, a backup of the input NODE info will be created\n" +
"as a separate table in the DB.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Now start the engine<br /> <tt class=\"input\"><b>engine start</b></tt></li><li class=\"STEPLIST\" id=\"THSSTYLE2\"><tt class=\"input\"><b> Note:</b></tt>After the engine is restarted, please\n" +
"check if the maxFilesPerRun is updated by running the RNC adapter\n" +
"in the log by changing the logging configuration of the adapter to\n" +
"FINEST.<br /><br /></li><li class=\"STEPLIST\" id=\"THSSTYLE2\">If user wants to set the previous maxFilesPerRun for changed\n" +
"NODE or any issue after the action is performed,  they can run the\n" +
"same script with option &ndash;rollback.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Stop the engine as <tt class=\"input\"><b>engine stop</b></tt></li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Go to  /eniq/sw/bin for the Script and Jar file which\n" +
"will be available there(set_maxper_run_value.bsh and MaxFilePerRunReduced.jar\n" +
") and run the command <tt class=\"input\"><b>chmod 777 &lt;script&gt;</b></tt></li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Run the following script to reduce the maxFilesPerRun\n" +
"value for RNC or RBS or RXI -<br /> <tt class=\"input\"><b>./set_maxper_run_value.bsh\n" +
"&ndash;rollback</b></tt></li><li class=\"STEPLIST\" id=\"THSSTYLE2\"><tt class=\"input\"><b>Note:</b></tt> 1.	OSS NAME (Ex :: eniq_oss_1) <br />2.	Node Name ( RNC or RBS or RXI ). The workaround currently runs\n" +
"for only these 3 Nodes.<br /></li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Now start the engine <tt class=\"input\"><b> engine start</b></tt></li><li class=\"STEPLIST\" id=\"THSSTYLE2\"><tt class=\"input\"><b> Note:</b></tt>After the engine is restarted, please\n" +
"check if the maxFilesPerRun is updated by running the RNC adapter\n" +
"in the log by changing the logging configuration of the adapter to\n" +
"FINEST</li></ol>\n" +
"\n"+
"</div>\n";

var C26_1_3=
"<div id=\"content\" class=\"body-content\">\n"+
"<h3><a name=\"EniqServices\"></a><span class=\"CHAPNUMBER\">26.1.3 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_9kz6\"></a><a name=\"CHAPTER26.1.3\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">In normal\n" +
"cases, during NAS fail over or SFS Reboot, the ENIQ services will\n" +
"be restarted automatically and do not need manual intervention. In\n" +
"rare cases the ENIQ services could be in a continuous maintenance\n" +
"state</a></span></h3>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Manual clearing the service status from maintenance is\n" +
"also not moving the service to ONLINE or OFFLINE.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Check the service SMF logs for below warning message are\n" +
"seen repeatedly<p>\n" +
"<b>Stopping because dependency activity requires stop.</b></p>\n" +
"<p>\n" +
"<b> Method or service exit timed out. Killing contract &lt;\n" +
"contract id &gt;.</b></p>\n" +
"<p>\n" +
"<b>Method \"&lt;stop&gt; \" or \"&lt;start&gt;\" failed due to signal\n" +
"KILL</b></p>\n" +
"<p>\n" +
"<b>Leaving maintenance because clear requested.</b></p>\n" +
"</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Check /eniq/local_logs/NASd/NASd.log  for below warnings<p>\n" +
"<b>2014-Aug-11_21.16.33:: Warning:: NAS filesystems failed\n" +
"to respond within 20 seconds.</b></p>\n" +
"<p>\n" +
"<b>Waiting 10 seconds before retry number 3.This will allow\n" +
"the NAS to failover and recover before disabling services.</b></p>\n" +
"<p>\n" +
"<b>2014-Aug-11_21.17.06:: Warning:: NAS filesystems failed\n" +
"to respond within 20 seconds.</b></p>\n" +
"<p>\n" +
"<b>Waiting 10 seconds before retry number 4This will allow\n" +
"the NAS to failover and recover before disabling services.</b></p>\n" +
"<p>\n" +
"<b>2014-Aug-11_21.17.16:: INFO:: disable called for svc:/milestone/NAS-online.</b></p>\n" +
"<p>\n" +
"<b>2014-Aug-11_21.32.27:: WARNING:: NAS filesystems unavailable;\n" +
"NAS milestone Disabled.</b></p>\n" +
"</li></ol>\n" +
"\n" +
"\n"+
"</div>\n";

var C26_2=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">26.2 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_akax\"></a><a name=\"CHAPTER26.2\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Troubleshooting licensing</a></span></h2>\n" +
"\n" +
"<p>\n" +
"If you cannot use the license manager to view ENIQ licenses or\n" +
"features, check the following:</p>\n" +
"\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">A valid license exists for the feature.</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">License manager is running in the case of a multi blade\n" +
"deployment on the coordinator blade.</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\"><tt class=\"LITERALMONO\">/eniq/sentinel/etc/sentinel.env</tt> host\n" +
"has the correct IP-address for the LSHOST variable.</li></ul>\n" +
"<p>\n" +
"If licensing still does not work as planned, inspect the licensing\n" +
"log files for more information.</p>\n" +
"\n" +
"<p>\n" +
"If licenses can not be retrieved from the license server, the license\n" +
"manager appends an error code to the error message. The error codes\n" +
"are explained in <a name=\"tbl-licerrors\" href='javascript:parent.parent.parent.showAnchor(\"errorcodetable\")' class=\"xref\"> Table 22</a><a name=\"errorcodetable\"></a>.</p>\n" +
"\n" +
"\n" +
"<a name=\"TABLE22\"></a>\n" +
"<table class=\"tblcnt\" width=\"100%\">\n" +
"<caption>Table 22 &nbsp;&nbsp; License Manager Error Codes</caption>\n" +
"<tr valign=\"top\">\n" +
"<th align=\"left\" valign=\"top\" width=\"36%\">\n" +
"\n" +
"<p>Error</p></th>\n" +
"<th align=\"center\" valign=\"top\" width=\"11%\">\n" +
"\n" +
"<p>Value</p></th>\n" +
"<th align=\"left\" valign=\"top\" width=\"53%\">\n" +
"\n" +
"<p>Description</p></th></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"36%\">\n" +
"<p>LS_SUCCESS</p>\n" +
"</td>\n" +
"<td align=\"center\" width=\"11%\">\n" +
"<p>0</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"53%\">\n" +
"<p>The license server operation was successful.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"36%\">\n" +
"<p>LS_BADHANDLE</p>\n" +
"</td>\n" +
"<td align=\"center\" width=\"11%\">\n" +
"<p>1</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"53%\">\n" +
"<p>Handle given to method represents a bad context.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"36%\">\n" +
"<p>LS_INSUFFICIENTUNITS 2</p>\n" +
"</td>\n" +
"<td align=\"center\" width=\"11%\">\n" +
"<p>2</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"53%\">\n" +
"<p>Not enough resources to satisfy LSRequest.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"36%\">\n" +
"<p>LS_LICENSESYSNOTAVAILABLE</p>\n" +
"</td>\n" +
"<td align=\"center\" width=\"11%\">\n" +
"<p>3</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"53%\">\n" +
"<p>License system itself is unavailable.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"36%\">\n" +
"<p>LS_LICENSETERMINATED</p>\n" +
"</td>\n" +
"<td align=\"center\" width=\"11%\">\n" +
"<p>4</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"53%\">\n" +
"<p>LSUpdate failed; license expired due to timeout.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"36%\">\n" +
"<p>LS_NOAUTHORIZATIONAVAILABLE</p>\n" +
"</td>\n" +
"<td align=\"center\" width=\"11%\">\n" +
"<p>5</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"53%\">\n" +
"<p>License server does not recognize this feature name.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"36%\">\n" +
"<p>LS_NOLICENSESAVAILABLE</p>\n" +
"</td>\n" +
"<td align=\"center\" width=\"11%\">\n" +
"<p>6</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"53%\">\n" +
"<p>License server has no more license keys for this feature.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"36%\">\n" +
"<p>LS_NORESOURCES</p>\n" +
"</td>\n" +
"<td align=\"center\" width=\"11%\">\n" +
"<p>7</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"53%\">\n" +
"<p>Insufficient resources (such as memory).</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"36%\">\n" +
"<p>LS_NO_NETWORK</p>\n" +
"</td>\n" +
"<td align=\"center\" width=\"11%\">\n" +
"<p>8</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"53%\">\n" +
"<p>Network communication problems encountered.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"36%\">\n" +
"<p>LS_NO_MSG_TEXT</p>\n" +
"</td>\n" +
"<td align=\"center\" width=\"11%\">\n" +
"<p>9</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"53%\">\n" +
"<p>Unable to retrieve message text.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"36%\">\n" +
"<p>LS_UNKNOWN_STATUS</p>\n" +
"</td>\n" +
"<td align=\"center\" width=\"11%\">\n" +
"<p>10</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"53%\">\n" +
"<p>Unknown code passed.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"36%\">\n" +
"<p>LS_BAD_INDEX</p>\n" +
"</td>\n" +
"<td align=\"center\" width=\"11%\">\n" +
"<p>11</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"53%\">\n" +
"<p>Invalid index - internal use only.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"36%\">\n" +
"<p>LS_NO_MORE_UNITS</p>\n" +
"</td>\n" +
"<td align=\"center\" width=\"11%\">\n" +
"<p>12</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"53%\">\n" +
"<p>Additional license keys/units requested are unavailable.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"36%\">\n" +
"<p>LS_LICENSE_EXPIRED</p>\n" +
"</td>\n" +
"<td align=\"center\" width=\"11%\">\n" +
"<p>13</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"53%\">\n" +
"<p>Licensing agreement for this feature has expired.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"36%\">\n" +
"<p>LS_BUFFER_TOO_SMALL</p>\n" +
"</td>\n" +
"<td align=\"center\" width=\"11%\">\n" +
"<p>14</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"53%\">\n" +
"<p>Buffer provided to method is too small.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"36%\">\n" +
"<p>VLS_NO_LICENSE_GIVEN</p>\n" +
"</td>\n" +
"<td align=\"center\" width=\"11%\">\n" +
"<p>15</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"53%\">\n" +
"<p>Other internal error not listed in this table.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"36%\">\n" +
"<p>VLS_APP_UNAMED</p>\n" +
"</td>\n" +
"<td align=\"center\" width=\"11%\">\n" +
"<p>16</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"53%\">\n" +
"<p>No feature name provided with method.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"36%\">\n" +
"<p>VLS_HOST_UNKNOWN</p>\n" +
"</td>\n" +
"<td align=\"center\" width=\"11%\">\n" +
"<p>17</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"53%\">\n" +
"<p>License server host does not seem to be on the network.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"36%\">\n" +
"<p>VLS_NO_SERVER_FILE</p>\n" +
"</td>\n" +
"<td align=\"center\" width=\"11%\">\n" +
"<p>18</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"53%\">\n" +
"<p>Client not initialized with name of license server host.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"36%\">\n" +
"<p>VLS_NO_SERVER_RUNNING</p>\n" +
"</td>\n" +
"<td align=\"center\" width=\"11%\">\n" +
"<p>19</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"53%\">\n" +
"<p>No license server seems to be running on remote host.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"36%\">\n" +
"<p>VLS_APP_NODE_LOCKED</p>\n" +
"</td>\n" +
"<td align=\"center\" width=\"11%\">\n" +
"<p>20</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"53%\">\n" +
"<p>Node locked feature cannot be issued float license key.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"36%\">\n" +
"<p>VLS_NO_KEY_TO_RETURN</p>\n" +
"</td>\n" +
"<td align=\"center\" width=\"11%\">\n" +
"<p>21</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"53%\">\n" +
"<p>LSrelease called before license key was issued.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"36%\">\n" +
"<p>VLS_RETURN_FAILED</p>\n" +
"</td>\n" +
"<td align=\"center\" width=\"11%\">\n" +
"<p>22</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"53%\">\n" +
"<p>LSrelease failed to return the issued license key.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"36%\">\n" +
"<p>VLS_NO_MORE_CLIENTS</p>\n" +
"</td>\n" +
"<td align=\"center\" width=\"11%\">\n" +
"<p>23</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"53%\">\n" +
"<p>LSgetClientInfo has no more clients to report.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"36%\">\n" +
"<p>VLS_NO_MORE_FEATURES</p>\n" +
"</td>\n" +
"<td align=\"center\" width=\"11%\">\n" +
"<p>24</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"53%\">\n" +
"<p>LSgetFeatureInfo has no more features to report.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"36%\">\n" +
"<p>VLS_CALLING_ERROR</p>\n" +
"</td>\n" +
"<td align=\"center\" width=\"11%\">\n" +
"<p>25</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"53%\">\n" +
"<p>Error in calling a LicenseServ function.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"36%\">\n" +
"<p>VLS_INTERNAL_ERROR</p>\n" +
"</td>\n" +
"<td align=\"center\" width=\"11%\">\n" +
"<p>26</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"53%\">\n" +
"<p>LicenseServ internal error.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"36%\">\n" +
"<p>VLS_SEVERE_INTERNAL_ERROR</p>\n" +
"</td>\n" +
"<td align=\"center\" width=\"11%\">\n" +
"<p>27</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"53%\">\n" +
"<p>LicenseServ severe internal error.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"36%\">\n" +
"<p>VLS_NO_SERVER_RESPONSE</p>\n" +
"</td>\n" +
"<td align=\"center\" width=\"11%\">\n" +
"<p>28</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"53%\">\n" +
"<p>License server not responding due to unknown condition.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"36%\">\n" +
"<p>VLS_USER_EXCLUDED</p>\n" +
"</td>\n" +
"<td align=\"center\" width=\"11%\">\n" +
"<p>29</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"53%\">\n" +
"<p>User/machine is excluded by group reservations.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"36%\">\n" +
"<p>VLS_UNKNOWN_SHARED_ID</p>\n" +
"</td>\n" +
"<td align=\"center\" width=\"11%\">\n" +
"<p>30</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"53%\">\n" +
"<p>Supplied sharing criteria is unknown.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"36%\">\n" +
"<p>VLS_NO_RESPONSE_TO_BROADCAST</p>\n" +
"</td>\n" +
"<td align=\"center\" width=\"11%\">\n" +
"<p>31</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"53%\">\n" +
"<p>No license servers responded to VLSwhere.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"36%\">\n" +
"<p>VLS_NO_SUCH_FEATURE</p>\n" +
"</td>\n" +
"<td align=\"center\" width=\"11%\">\n" +
"<p>32</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"53%\">\n" +
"<p>License server does not recognize the given feature.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"36%\">\n" +
"<p>VLS_ADD_LIC_FAILED</p>\n" +
"</td>\n" +
"<td align=\"center\" width=\"11%\">\n" +
"<p>33</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"53%\">\n" +
"<p>Dynamic license addition failed.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"36%\">\n" +
"<p>VLS_DELETE_LIC_FAILED</p>\n" +
"</td>\n" +
"<td align=\"center\" width=\"11%\">\n" +
"<p>34</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"53%\">\n" +
"<p>Dynamic license deletion failed.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"36%\">\n" +
"<p>VLS_LOCAL_UPDATE</p>\n" +
"</td>\n" +
"<td align=\"center\" width=\"11%\">\n" +
"<p>35</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"53%\">\n" +
"<p>Last update was done locally.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"36%\">\n" +
"<p>VLS_REMOTE_UPDATE</p>\n" +
"</td>\n" +
"<td align=\"center\" width=\"11%\">\n" +
"<p>36</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"53%\">\n" +
"<p>Last update was done by the LicenseServ license server.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"36%\">\n" +
"<p>VLS_VENDORIDMISMATCH</p>\n" +
"</td>\n" +
"<td align=\"center\" width=\"11%\">\n" +
"<p>37</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"53%\">\n" +
"<p>The license system has resources that could satisfy the\n" +
"request, but the vendor code of the requesting application does not\n" +
"match with that of the application licensed by lserv.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"36%\">\n" +
"<p>VLS_MULTIPLE_VENDORID_FOUND</p>\n" +
"</td>\n" +
"<td align=\"center\" width=\"11%\">\n" +
"<p>38</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"53%\">\n" +
"<p>The license system has multiple licenses for the same feature/version\n" +
"and it is not clear from the operation which license the requestor\n" +
"is interested in.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"36%\">\n" +
"<p>VLS_BAD_SERVER_MESSAGE</p>\n" +
"</td>\n" +
"<td align=\"center\" width=\"11%\">\n" +
"<p>39</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"53%\">\n" +
"<p>An error has occurred in decrypting (or decoding) a network\n" +
"message at the client end. Probably an incompatible or unknown server\n" +
"or a version mismatch.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"36%\">\n" +
"<p>VLS_CLK_TAMP_FOUND</p>\n" +
"</td>\n" +
"<td align=\"center\" width=\"11%\">\n" +
"<p>40</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"53%\">\n" +
"<p>The license server has found evidence of tampering of the\n" +
"system clock and it cannot service the request since the license for\n" +
"this feature has been set to be time tamper proof.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"36%\">\n" +
"<p>VLS_LEADER_NOT_PRESENT</p>\n" +
"</td>\n" +
"<td align=\"center\" width=\"11%\">\n" +
"<p>41</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"53%\">\n" +
"<p>The leader of the redundant license server pool is not\n" +
"known.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"36%\">\n" +
"<p>VLS_NON_REDUNDANT_SRVR</p>\n" +
"</td>\n" +
"<td align=\"center\" width=\"11%\">\n" +
"<p>42</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"53%\">\n" +
"<p>The contacted license server is not a redundant license\n" +
"server.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"36%\">\n" +
"<p>VLS_SERVER_SYNC_IN_PROGRESS</p>\n" +
"</td>\n" +
"<td align=\"center\" width=\"11%\">\n" +
"<p>43</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"53%\">\n" +
"<p>When contacted, redundant license servers were busy synchronizing\n" +
"information.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"36%\">\n" +
"<p>VLS_NON_REDUNDANT_FEATURE</p>\n" +
"</td>\n" +
"<td align=\"center\" width=\"11%\">\n" +
"<p>44</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"53%\">\n" +
"<p>This feature does not support license server redundancy.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"36%\">\n" +
"<p>VLS_DIFF_LIB_VER</p>\n" +
"</td>\n" +
"<td align=\"center\" width=\"11%\">\n" +
"<p>45</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"53%\">\n" +
"<p>The client and license server libraries do not match.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"36%\">\n" +
"<p>VLS_SERVER_ALREADY_PRESENT</p>\n" +
"</td>\n" +
"<td align=\"center\" width=\"11%\">\n" +
"<p>46</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"53%\">\n" +
"<p>The license server is already in the redundant license\n" +
"server pool.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"36%\">\n" +
"<p>VLS_SERVER_NOT_PRESENT</p>\n" +
"</td>\n" +
"<td align=\"center\" width=\"11%\">\n" +
"<p>47</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"53%\">\n" +
"<p>The license server contacted is not in the redundant license\n" +
"server pool.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"36%\">\n" +
"<p>VLS_BAD_HOSTNAME</p>\n" +
"</td>\n" +
"<td align=\"center\" width=\"11%\">\n" +
"<p>48</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"53%\">\n" +
"<p>Could not resolve the license server name.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"36%\">\n" +
"<p>VLS_NOT_AUTHORIZED</p>\n" +
"</td>\n" +
"<td align=\"center\" width=\"11%\">\n" +
"<p>49</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"53%\">\n" +
"<p>The user is not authorized to carry out this operation.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"36%\">\n" +
"<p>VLS_SERVER_FILE_SYNC</p>\n" +
"</td>\n" +
"<td align=\"center\" width=\"11%\">\n" +
"<p>50</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"53%\">\n" +
"<p>Redundant license server synchronization of information\n" +
"is in progress.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"36%\">\n" +
"<p>VLS_CONF_FILE_ERROR</p>\n" +
"</td>\n" +
"<td align=\"center\" width=\"11%\">\n" +
"<p>51</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"53%\">\n" +
"<p>There is an error in the configuration file.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"36%\">\n" +
"<p>VLS_POOL_FULL</p>\n" +
"</td>\n" +
"<td align=\"center\" width=\"11%\">\n" +
"<p>52</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"53%\">\n" +
"<p>The redundant server pool already has the maximum number\n" +
"of license servers it can handle.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"36%\">\n" +
"<p>VLS_ONLY_SERVER</p>\n" +
"</td>\n" +
"<td align=\"center\" width=\"11%\">\n" +
"<p>53</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"53%\">\n" +
"<p>The license server to be removed is the only one in the\n" +
"redundant license server pool.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"36%\">\n" +
"<p>VLS_MAJORITY_RULE_FAILURE</p>\n" +
"</td>\n" +
"<td align=\"center\" width=\"11%\">\n" +
"<p>54</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"53%\">\n" +
"<p>The majority of the license servers in the redundant license\n" +
"server pool are not running.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"36%\">\n" +
"<p>VLS_PLACED_IN_QUEUE</p>\n" +
"</td>\n" +
"<td align=\"center\" width=\"11%\">\n" +
"<p>55</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"53%\">\n" +
"<p>The user was placed into the license queue.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"36%\">\n" +
"<p>VLS_CLIENT_NOT_AUTHORIZED</p>\n" +
"</td>\n" +
"<td align=\"center\" width=\"11%\">\n" +
"<p>56</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"53%\">\n" +
"<p>The client is not authorized to perform this operation.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"36%\">\n" +
"<p>VLS_NO_SUCH_CLIENT</p>\n" +
"</td>\n" +
"<td align=\"center\" width=\"11%\">\n" +
"<p>57</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"53%\">\n" +
"<p>The requested client is not present in the license queue.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"36%\">\n" +
"<p>VLS_MSG_TO_LEADER</p>\n" +
"</td>\n" +
"<td align=\"center\" width=\"11%\">\n" +
"<p>58</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"53%\">\n" +
"<p>The message has been forwarded to the leader; this is not\n" +
"an error.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"36%\">\n" +
"<p>VLS_NOT_SUPPORTED</p>\n" +
"</td>\n" +
"<td align=\"center\" width=\"11%\">\n" +
"<p>59</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"53%\">\n" +
"<p>The license server does not support this API. The license\n" +
"server is probably an old version.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"36%\">\n" +
"<p>VLS_BAD_DISTRIBUTION_CRIT</p>\n" +
"</td>\n" +
"<td align=\"center\" width=\"11%\">\n" +
"<p>60</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"53%\">\n" +
"<p>The specified distribution criteria is not valid</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"36%\">\n" +
"<p>VLS_TRIAL_LIC_EXHAUSTED</p>\n" +
"</td>\n" +
"<td align=\"center\" width=\"11%\">\n" +
"<p>61</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"53%\">\n" +
"<p>The trial license period has expired.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"36%\">\n" +
"<p>VLS_NO_TRIAL_INFO</p>\n" +
"</td>\n" +
"<td align=\"center\" width=\"11%\">\n" +
"<p>62</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"53%\">\n" +
"<p>No Trial usage info exists on the server</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"36%\">\n" +
"<p>VLS_TRIAL_INFO_FAILED</p>\n" +
"</td>\n" +
"<td align=\"center\" width=\"11%\">\n" +
"<p>63</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"53%\">\n" +
"<p>Trial usage query failed</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"36%\">\n" +
"<p>VLS_NOMORE_QUEUE_RESOURCES</p>\n" +
"</td>\n" +
"<td align=\"center\" width=\"11%\">\n" +
"<p>64</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"53%\">\n" +
"<p>Could not queue the client because the queue is full.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"36%\">\n" +
"<p>VLS_INVALID_LICENSE</p>\n" +
"</td>\n" +
"<td align=\"center\" width=\"11%\">\n" +
"<p>65</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"53%\">\n" +
"<p>Invalid license string.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"36%\">\n" +
"<p>VLS_DUPLICATE_LICENSE 66</p>\n" +
"</td>\n" +
"<td align=\"center\" width=\"11%\">\n" +
"<p>66</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"53%\">\n" +
"<p>Cannot add license as it is already added.</p>\n" +
"</td></tr></table>\n" +
"\n"+
"</div>\n";

var C26_3=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><a name=\"troublepartition\"></a><span class=\"CHAPNUMBER\">26.3 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_dhed\"></a><a name=\"CHAPTER26.3\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Troubleshooting\n" +
"partition issues</a></span></h2>\n" +
"\n" +
"<p>\n" +
"Problematic partitions are labeled INSANE. There is no automatic\n" +
"notification of INSANE partitions. You must check the logs, AdminUI,\n" +
"or the database manually. </p>\n" +
"\n" +
"<p>\n" +
"INSANE data is not part of Measurement Type database view, therefore\n" +
"data in an INSANE partition is not displayed in the AdminUI.</p>\n" +
"\n" +
"<p>\n" +
"You can suspect INSANE data, if:</p>\n" +
"\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">Data is loading and aggregating for all other measurement\n" +
"types for the node.</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">Data is loaded into the table but is not displayed in\n" +
"AdminUI, for example, when you select <b>Show Aggregations</b>.</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">The data in the partition is not displayed in the Measurement\n" +
"Type database view.</li></ul>\n" +
"<p>\n" +
"There are various causes for insane data, such as:</p>\n" +
"\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">The partition contains data that does not meet the time\n" +
"constraints set for that partition in the DWHPartition table.</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">The column structure in the existing partition does\n" +
"not equal the partition definition in the DWH Repository &ndash; DWHPartition,\n" +
"DWHColumn, tables. There are various reasons why this can happen:\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE3\">Invalid data type</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE3\">Invalid column name</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE3\">Data size does not match</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE3\">Missing column</li></ul></li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">The partition does not have any limits (start and end\n" +
"time) defined in the DWHPartition table</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">A new partition already contains data.</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">SIMPLE partition type has an end date defined. The value\n" +
"should be null.</li></ul>\n" +
"\n"+
"</div>\n";

var C26_3_1=
"<div id=\"content\" class=\"body-content\">\n"+
"<h3><span class=\"CHAPNUMBER\">26.3.1 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_d3qb\"></a><a name=\"CHAPTER26.3.1\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Solving insane partition issues</a></span></h3>\n" +
"\n" +
"<p>\n" +
"There is no automatic recovery from an INSANE partition. The fix\n" +
"for an INSANE partition depends on the cause of it.</p>\n" +
"\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">If the partition has data in it that is older than the\n" +
"start time or newer than the end time, move the data that is outside\n" +
"the limits to the correct partition, if it is available.</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">If the column structure does not match the partition\n" +
"definition, reinstall the Tech Pack.</li></ul>\n" +
"\n" +
"\n"+
"</div>\n";

var C26_4=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">26.4 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_24am\"></a><a name=\"CHAPTER26.4\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">General Multi Blade info</a></span></h2>\n" +
"\n" +
"<p>\n" +
"To list ENIQ services that should be running on any particular\n" +
"blade execute the following command as user root.</p>\n" +
"\n" +
"\n" +
"<p>\n" +
"For the coordinator blade</p><ul>\n" +
"\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">grep stats_coordinator /eniq/admin/etc/smf_contract_config\n" +
"|grep ENIQ |grep Y</li></ul>\n" +
"\n" +
"<p>\n" +
"For the engine blade</p><ul>\n" +
"\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">grep stats_engine /eniq/admin/etc/smf_contract_config\n" +
"|grep ENIQ |grep Y</li></ul>\n" +
"\n" +
"<p>\n" +
"For the reader blades</p><ul>\n" +
"\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">grep stats_iqr /eniq/admin/etc/smf_contract_config |grep\n" +
"ENIQ |grep Y</li></ul>\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>dwh_reader_1 is used by engine as a writer. Thus, ENIQ stats\n" +
"multi blade server has two reader nodes (stats_iqr) , the first of\n" +
"which is referred to as the writer.</dd></dl><br />\n" +
"<p>\n" +
"Also while on the various blades the user will see varying output\n" +
"of the command due to different services executing on a particular\n" +
"blade. The following is the expected output of the command executed\n" +
"as user root.</p>\n" +
"\n" +
"<p>\n" +
" <tt class=\"LITERALMONO\">svcs -a | grep eniq</tt></p>\n" +
"\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">On the coordinator blade \n" +
"<table frame=\"void\" style=\"margin-top:4pt\" class=\"tblcnt\" rules=\"none\" width=\"100%\">\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"38%\">\n" +
"<p>disabled</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"29%\">\n" +
"<p>&lt;start date or time of service&gt;</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"33%\">\n" +
"<p>svc:/eniq/dwh_reader:default</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"38%\">\n" +
"<p>disabled</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"29%\">\n" +
"<p>&lt;start date or time of service&gt;</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"33%\">\n" +
"<p>svc:/eniq/engine:default</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"38%\">\n" +
"<p>online/ disabled depending on current setup </p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"29%\">\n" +
"<p>&lt;start date or time of service&gt;</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"33%\">\n" +
"<p>svc:/eniq/roll-snap:default</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"38%\">\n" +
"<p>online</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"29%\">\n" +
"<p>&lt;start date or time of service&gt;</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"33%\">\n" +
"<p>svc:/eniq/esm:default</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"38%\">\n" +
"<p>online</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"29%\">\n" +
"<p>&lt;start date or time of service&gt;</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"33%\">\n" +
"<p>svc:/eniq/rmiregistry:default</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"38%\">\n" +
"<p>online</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"29%\">\n" +
"<p>&lt;start date or time of service&gt;</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"33%\">\n" +
"<p>svc:/eniq/licmgr:default</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"38%\">\n" +
"<p>online</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"29%\">\n" +
"<p>&lt;start date or time of service&gt;</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"33%\">\n" +
"<p>svc:/eniq/connectd:default</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"38%\">\n" +
"<p>online </p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"29%\">\n" +
"<p>&lt;start date or time of service&gt;</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"33%\">\n" +
"<p>svc:/eniq/repdb:default</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"38%\">\n" +
"<p>online </p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"29%\">\n" +
"<p>&lt;start date or time of service&gt;</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"33%\">\n" +
"<p>svc:/eniq/dwhdb:default</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"38%\">\n" +
"<p>online </p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"29%\">\n" +
"<p>&lt;start date or time of service&gt;</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"33%\">\n" +
"<p>svc:/eniq/webserver:default</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"38%\">\n" +
"<p>online </p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"29%\">\n" +
"<p>&lt;start date or time of service&gt;</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"33%\">\n" +
"<p> svc:/eniq/scheduler:default</p>\n" +
"</td></tr></table></li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">On the engine blade \n" +
"<table frame=\"void\" style=\"margin-top:4pt\" class=\"grd\" rules=\"none\" width=\"100%\">\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"38%\">\n" +
"<p>online/ disabled depending on current setup </p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"29%\">\n" +
"<p>&lt;start date or time of service&gt;</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"33%\">\n" +
"<p>svc:/eniq/roll-snap:default</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"38%\">\n" +
"<p>disabled</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"29%\">\n" +
"<p>&lt;start date or time of service&gt;</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"33%\">\n" +
"<p>svc:/eniq/repdb:default</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"38%\">\n" +
"<p>disabled </p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"29%\">\n" +
"<p>&lt;start date or time of service&gt;</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"33%\">\n" +
"<p>svc:/eniq/dwhdb:default</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"38%\">\n" +
"<p>disabled</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"29%\">\n" +
"<p>&lt;start date or time of service&gt;</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"33%\">\n" +
"<p>svc:/eniq/licmgr:default</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"38%\">\n" +
"<p>disabled</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"29%\">\n" +
"<p>&lt;start date or time of service&gt;</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"33%\">\n" +
"<p>svc:/eniq/dwh_reader:default</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"38%\">\n" +
"<p>disabled</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"29%\">\n" +
"<p>&lt;start date or time of service&gt;</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"33%\">\n" +
"<p>svc:/eniq/webserver:default</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"38%\">\n" +
"<p>disabled</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"29%\">\n" +
"<p>&lt;start date or time of service&gt;</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"33%\">\n" +
"<p>svc:/eniq/scheduler:default</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"38%\">\n" +
"<p>online </p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"29%\">\n" +
"<p>&lt;start date or time of service&gt;</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"33%\">\n" +
"<p>svc:/eniq/esm:default</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"38%\">\n" +
"<p>online </p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"29%\">\n" +
"<p>&lt;start date or time of service&gt;</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"33%\">\n" +
"<p>svc:/eniq/connectd:default</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"38%\">\n" +
"<p>online </p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"29%\">\n" +
"<p>&lt;start date or time of service&gt;</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"33%\">\n" +
"<p>svc:/eniq/rmiregistry:default</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"38%\">\n" +
"<p>online </p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"29%\">\n" +
"<p>&lt;start date or time of service&gt;</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"33%\">\n" +
"<p> svc:/eniq/engine:default</p>\n" +
"</td></tr></table></li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">On the reader blades \n" +
"<table frame=\"void\" style=\"margin-top:4pt\" class=\"grd\" rules=\"none\" width=\"100%\">\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"38%\">\n" +
"<p>online/ disabled depending on current setup </p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"29%\">\n" +
"<p>&lt;start date or time of service&gt;</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"33%\">\n" +
"<p>svc:/eniq/roll-snap:default</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"38%\">\n" +
"<p>disabled</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"29%\">\n" +
"<p>&lt;start date or time of service&gt;</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"33%\">\n" +
"<p>svc:/eniq/repdb:default</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"38%\">\n" +
"<p>disabled </p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"29%\">\n" +
"<p>&lt;start date or time of service&gt;</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"33%\">\n" +
"<p>svc:/eniq/dwhdb:default</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"38%\">\n" +
"<p>disabled</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"29%\">\n" +
"<p>&lt;start date or time of service&gt;</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"33%\">\n" +
"<p>svc:/eniq/licmgr:default</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"38%\">\n" +
"<p>disabled</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"29%\">\n" +
"<p>&lt;start date or time of service&gt;</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"33%\">\n" +
"<p>svc:/eniq/connectd:default</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"38%\">\n" +
"<p>disabled</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"29%\">\n" +
"<p>&lt;start date or time of service&gt;</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"33%\">\n" +
"<p>svc:/eniq/webserver:default</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"38%\">\n" +
"<p>disabled</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"29%\">\n" +
"<p>&lt;start date or time of service&gt;</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"33%\">\n" +
"<p>svc:/eniq/scheduler:default</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"38%\">\n" +
"<p>online</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"29%\">\n" +
"<p>&lt;start date or time of service&gt;</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"33%\">\n" +
"<p>svc:/eniq/esm:default</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"38%\">\n" +
"<p>online</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"29%\">\n" +
"<p>&lt;start date or time of service&gt;</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"33%\">\n" +
"<p>svc:/eniq/dwh_reader:default</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"38%\">\n" +
"<p>disabled</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"29%\">\n" +
"<p>&lt;start date or time of service&gt;</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"33%\">\n" +
"<p>svc:/eniq/rmiregistry:default</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"38%\">\n" +
"<p>disabled</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"29%\">\n" +
"<p>&lt;start date or time of service&gt;</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"33%\">\n" +
"<p> svc:/eniq/engine:default</p>\n" +
"</td></tr></table></li></ul>\n" +
"\n"+
"</div>\n";

var C26_4_1=
"<div id=\"content\" class=\"body-content\">\n"+
"<h3><a name=\"Stopall\"></a><span class=\"CHAPNUMBER\">26.4.1 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"stopall\"></a><a name=\"CHAPTER26.4.1\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">To stop all the services\n" +
"on all blades</a></span></h3>\n" +
"\n" +
"<p>\n" +
"Steps mentioned below has to be followed on all the four blades\n" +
"in below sequence . Login as a root.</p>\n" +
"\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">ENIQ Reader_2</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Reader_1</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Engine Server</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Coordinator Server</li></ul>\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\"><tt class=\"input\"><b>cd /eniq/admin/bin </b></tt></li><li class=\"STEPLIST\" id=\"THSSTYLE2\"><tt class=\"input\"><b>/usr/bin/bash ./manage_eniq_services.bsh -a stop\n" +
"-s ALL</b></tt></li></ol>\n" +
"\n"+
"</div>\n";

var C26_4_2=
"<div id=\"content\" class=\"body-content\">\n"+
"<h3><a name=\"startall\"></a><span class=\"CHAPNUMBER\">26.4.2 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_hzlo\"></a><a name=\"CHAPTER26.4.2\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\"> To start all\n" +
"the services on all blades</a></span></h3>\n" +
"\n" +
"<p>\n" +
"Steps mentioned below has to be followed on all the four blades\n" +
"in below sequence. Login as a root.</p>\n" +
"\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">ENIQ Coordinator Server</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Engine Server</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Reader_1</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Reader_2</li></ul>\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\"><tt class=\"input\"><b>cd /eniq/admin/bin</b></tt></li><li class=\"STEPLIST\" id=\"THSSTYLE2\"><tt class=\"input\"><b>/usr/bin/bash ./manage_eniq_services.bsh -a start\n" +
"-s ALL</b></tt></li></ol>\n" +
"\n"+
"</div>\n";

var C26_4_3=
"<div id=\"content\" class=\"body-content\">\n"+
"<h3><a name=\"restartall\"></a><span class=\"CHAPNUMBER\">26.4.3 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_j3el\"></a><a name=\"CHAPTER26.4.3\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">To restart\n" +
"all the services on all blades</a></span></h3>\n" +
"\n" +
"<p>\n" +
"Steps mentioned below has to be followed on all the four blades\n" +
"in below sequence to stop services. Login as a root .</p>\n" +
"\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">ENIQ Reader_2 Server</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Reader_1 Server</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Engine Server</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Coordinator Server</li></ul>\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\"><tt class=\"input\"><b>cd /eniq/admin/bin </b></tt></li><li class=\"STEPLIST\" id=\"THSSTYLE2\"><tt class=\"input\"><b>/usr/bin/bash ./manage_eniq_services.bsh -a stop\n" +
"-s ALL</b></tt><p>\n" +
"Once all the services have been disabled on all 4 blades we can\n" +
"start it as : </p>\n" +
"<p>\n" +
"Steps mentioned below has to be followed on all the four blades\n" +
"in below sequence to start services. Login as a root .</p>\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Coordinator Server</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Engine Server</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Reader_1 Server</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Reader_2 Server</li></ul></li><li class=\"STEPLIST\" id=\"THSSTYLE2\"><tt class=\"input\"><b>cd /eniq/admin/bin</b></tt></li><li class=\"STEPLIST\" id=\"THSSTYLE2\"><tt class=\"input\"><b>/usr/bin/bash ./manage_eniq_services.bsh -a start\n" +
"-s ALL</b></tt></li></ol>\n" +
"\n" +
"\n"+
"</div>\n";

var C26_5=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><a name=\"dwhdbclear\"></a><span class=\"CHAPNUMBER\">26.5 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_gnm0\"></a><a name=\"CHAPTER26.5\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Clearing dwhdb\n" +
"reader Services in Maintenance</a></span></h2>\n" +
"\n" +
"<p>\n" +
"In the case of a multi blade deployment if a service is offline\n" +
"for an extended period, SAP IQ will exclude that service from the\n" +
"multiplex, which results in the excluded services having state <tt class=\"LITERALMONO\">Maintenance</tt>.</p>\n" +
"\n" +
"<p>\n" +
"Log into the dwhdb database and check for nodes in the multiplex\n" +
"with an <tt class=\"LITERALMONO\">excluded</tt> status: <br /><tt class=\"input\"><b>eniq_srv{dcuser}\n" +
"# <br />iqisql -U&lt;<i class=\"var\">database_username</i>&gt; <br /> -P&lt;<i class=\"var\">database_password</i>&gt; -Sdwhdb  <br />1&gt; sp_iqmpxinfo <br />2&gt; go\n" +
" </b></tt></p>\n" +
"\n" +
"<p>\n" +
"Then change their status to <tt class=\"LITERALMONO\">included</tt> :<br /><tt class=\"input\"><b>1&gt; ALTER MULTIPLEX SERVER  &lt;<i class=\"var\">server name</i>&gt; STATUS INCLUDED <br />2&gt; go <br />Example: <br />1&gt; ALTER MULTIPLEX SERVER  dwh_reader_1\n" +
"STATUS INCLUDED <br />2&gt; go </b></tt></p>\n" +
"\n" +
"<p>\n" +
"Then verify that all nodes in the multiplex have a status <tt class=\"LITERALMONO\">included</tt>:<br /><tt class=\"input\"><b>eniq_srv{dcuser} #<br /> iqisql -U&lt;<i class=\"var\">database_username</i>&gt;  <br />-P&lt;<i class=\"var\">database_password</i>&gt; -Sdwhdb  <br />1&gt; sp_iqmpxinfo <br />2&gt; go  </b></tt> </p>\n" +
"\n" +
"\n"+
"</div>\n";

var C26_6=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">26.6 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_25rx\"></a><a name=\"CHAPTER26.6\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Monitoring ENIQ Statistics Services\n" +
"using SMF</a></span></h2>\n" +
"\n" +
"<p>\n" +
"Log in as <tt class=\"file-path\">root</tt> user and run the following\n" +
"command: <br /><tt class=\"input\"><b># /usr/bin/svcs | grep eniq</b></tt></p>\n" +
"\n" +
"<p>\n" +
"If any of the services is not online and needs to be restarted,\n" +
"please proceed with the following steps:</p>\n" +
"\n" +
"<p>\n" +
"To enable any ENIQ service, use the following command: <br /><tt class=\"input\"><b># /usr/sbin/svcadm enable &lt;<i class=\"var\">service_name</i>></b></tt></p>\n" +
"\n" +
"<p>\n" +
"To disable any ENIQ service, use the following command: <br /><tt class=\"input\"><b># /usr/sbin/svcadm disable &lt;<i class=\"var\">service_name</i>></b></tt></p>\n" +
"\n" +
"<p>\n" +
"If a service is in a maintenance state, it is necessary to clear\n" +
"the service:<br /> <tt class=\"input\"><b># /usr/sbin/svcadm clear &lt;<i class=\"var\">service_name</i>></b></tt></p>\n" +
"\n" +
"\n"+
"</div>\n";

var C26_7=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">26.7 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_taow\"></a><a name=\"CHAPTER26.7\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Engine Start up Hanging</a></span></h2>\n" +
"\n" +
"<p>\n" +
"In the case of a multi blade deployment ,if engine start up is\n" +
"hanging and services are running properly on other nodes, below command\n" +
"should be used as root user on engine node:<br /><tt class=\"input\"><b> manage_eniq_services.bsh\n" +
"-a restart -s ALL</b></tt></p>\n" +
"\n" +
"<p>\n" +
"If above command hangs, then manually disable the RMI registry\n" +
"service and try again by executing below steps:<br /><tt class=\"input\"><b>1&gt; svcadm\n" +
"disable svc:/eniq/rmiregistry:default<br />2&gt; manage_eniq_services.bsh\n" +
"-a restart -s ALL</b></tt></p>\n" +
"\n" +
"<p>\n" +
"If the services on the other nodes have problems, then refer <tt class=\"input\"><b>Section 19.4.3</b></tt> to restart all services on the Multi blade\n" +
"servers.</p>\n" +
"\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>This is an option for trouble-shooting the case of Engine\n" +
"hanging (with no logging) when it is restarted.</dd></dl><br />\n" +
"\n"+
"</div>\n";

var C26_8=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">26.8 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_wv90\"></a><a name=\"CHAPTER26.8\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Rolling Snapshot Logs</a></span></h2>\n" +
"\n" +
"<p>\n" +
"There is an option to receive additional information to the rolling\n" +
"snapshot logfile, this information will record that the Rolling Snapshot\n" +
"file indicator is <strong class=\"MEDEMPH\">not</strong> present on the system. To view\n" +
"this information enter the following command:</p>\n" +
"\n" +
"<p>\n" +
"<tt class=\"input\"><b># touch /eniq/sw/conf/.debug_rollsnap</b></tt></p>\n" +
"\n" +
"\n"+
"</div>\n";

var C26_9=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">26.9 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_n369\"></a><a name=\"CHAPTER26.9\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Hostsync Logs</a></span></h2>\n" +
"\n" +
"<p>\n" +
"There is an option to receive additional information to the hostsync\n" +
"log file. It will report and error when the EWM configuration files\n" +
"are <strong class=\"MEDEMPH\">not</strong> present on the system. To view this information\n" +
"enter the following command:</p>\n" +
"\n" +
"<p><tt class=\"input\"><b># touch /eniq/sw/conf/.debug_hostsync</b></tt></p>\n" +
"\n"+
"</div>\n";

var C26_10=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">26.10 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_tgof\"></a><a name=\"CHAPTER26.10\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Troubleshooting Guide for Snapshot\n" +
"Failure</a></span></h2>\n" +
"\n" +
"<p>\n" +
"If your snapshot fails and if either of the below error is displayed\n" +
"then follow steps 1 to 3 before retrying the snapshot again. </p>\n" +
"\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\"><tt class=\"output\">ERROR : Could not backup repdb database</tt></li></ul>\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\"><tt class=\"output\">ERROR : Could not copy backup file /eniq/database/rep_main/repdb.log\n" +
"to /eniq/backup/repdb_bkup/repdb1.log </tt></li></ul>\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\"><tt class=\"output\">ERROR : Could not copy backup file /eniq/database/rep_main/repdb.db\n" +
"to /eniq/backup/repdb_bkup/repdb1.db</tt></li></ul>\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>In case of Multiblade all the below steps to be run on co-ordinator\n" +
"blade only.</dd></dl><br />\n" +
"<p>\n" +
" Login as <tt class=\"file-path\">root</tt> user\n" +
"and run the following commands:</p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Check the available diskspace of backup partition.<p>\n" +
"<tt class=\"input\"><b>{root} #: df -k /eniq/backup/</b></tt></p>\n" +
"<p>\n" +
"Example output :</p>\n" +
"<p>\n" +
"<tt class=\"output\">Filesystem 									kbytes   	used   	avail   	 capacity\n" +
"  Mounted on</tt></p>\n" +
"<p>\n" +
"<tt class=\"output\">nas1:/vx/lte2979-backup  157286400  10832456 145310288\n" +
" 7%  		/eniq/backup</tt></p>\n" +
"<p>\n" +
"Note down the available diskspace(avail) of backup partition =\n" +
"_____ KB</p>\n" +
"</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Calculate the size of repdb database.<p>\n" +
"<tt class=\"input\"><b>(root) #: cd /eniq/database/rep_main</b></tt></p>\n" +
"<p>\n" +
"<tt class=\"input\"><b>{root} #: echo `ls -l repdb.db repdb.log| awk '{sum = sum\n" +
"+ $5} END {print sum}' ` / 1024 | bc</b></tt></p>\n" +
"<p>\n" +
"Example output :</p>\n" +
"<p>\n" +
"<tt class=\"file-path\">1669764</tt></p>\n" +
"<p>\n" +
"Note down the repdb database size = ______ KB</p>\n" +
"</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Compare the size of repdb database and available diskspace\n" +
"of backup partition.<p>\n" +
"  If the size of repdb database is greater as compared to the available\n" +
"diskspace of backup partition then perform step 3.1 otherwise there\n" +
"might be some other issue and need to contact the next level of support.</p>\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd><strong class=\"MEDEMPH\">The below step to be performed only during Maintenance\n" +
"Window, as it involves downtime of Database.</strong></dd></dl><br /><p>\n" +
"3.1 Truncate the database transaction log.</p>\n" +
"<p>\n" +
"<tt class=\"input\"><b>{root} #: bash /eniq/admin/bin/transaction_log_admin.bsh\n" +
"-t repdb -R -l /eniq/local_logs/transaction_log_admin.txt -N</b></tt></p>\n" +
"</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Verify the logs as below to ensure successful completion\n" +
"of step 3.1. <p>\n" +
"Example output :</p>\n" +
"<p>\n" +
"<tt class=\"file-path\"> Checking for correct user and server type to exectue\n" +
"script.<br />   =======&lt; 2014-02-18_08.39.29 - Starting ./transaction_log_admin.bsh\n" +
"&gt;=====<br />  Getting an ordered list of server(s).<br /> Setting engine\n" +
"to noloads and wating for execution slots to empty <br />Running engine\n" +
"command for NoLoads locally<br /> 			 . <br /> 			 . 		<br />	 .  <br />		=======&lt; 2014-02-18_08.39.53 - Starting ENIQ services on atrcxb2332\n" +
"&gt;=====<br />  2014-02-18_08.39.53 - Starting ENIQ service svc:/eniq/repdb <br />  ENIQ services started correctly on atrcxb2332<br />  Setting engine\n" +
"to original profile.<br /> Running engine command for Normal locally <br />Oracle Corporation      SunOS 5.10      Generic Patch   January\n" +
"2005 <br />Changing profile to: Normal <br />Change profile requested\n" +
"successfully<br /> Tidying up old logs.<br /><br />   ======&lt; 2014-02-18_08.40.00\n" +
"- Finishing ./transaction_log_admin.bsh &gt;=====</tt></p>\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>After successful completion of the above steps there will\n" +
"be free space available for snapshot to proceed.</dd></dl><br /></li></ol>\n" +
"\n" +
"\n"+
"</div>\n";

