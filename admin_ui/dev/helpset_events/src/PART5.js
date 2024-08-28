var C5=
"<div id=\"content\" class=\"body-content\">\n"+
"<h1><span class=\"CHAPNUMBER\">5 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_pavy\"></a><a name=\"CHAPTER5\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Troubleshooting</a></span></h1>\n" +
"\n" +
"<p>\n" +
"This chapter describes some possible problems that can occur in\n" +
"ENIQ Events and presents possible solutions.</p>\n" +
"\n" +
"<p><a name=\"trblshooting\"></a>\n" +
"Contact local Ericsson support for additional\n" +
"assistance.</p>\n" +
"\n" +
"\n"+
"</div>\n";

var C5_1=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><a name=\"troublechapter\"></a><span class=\"CHAPNUMBER\">5.1 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"troubleshooting\"></a><a name=\"CHAPTER5.1\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Troubleshooting\n" +
"Ericsson Network OSS</a></span></h2>\n" +
"\n" +
"<p><a name=\"tbl-troubleshooting\"></a>\n" +
"The table below lists possible troubles and actions to perform\n" +
"to solve them.</p>\n" +
"\n" +
"\n" +
"<a name=\"TABLE8\"></a>\n" +
"<table class=\"tblcnt\" width=\"100%\">\n" +
"<caption>Table 8 &nbsp;&nbsp; Troubleshooting Ericsson Network OSS</caption>\n" +
"<tr valign=\"top\">\n" +
"<th align=\"left\" valign=\"top\" width=\"31%\">\n" +
"\n" +
"<p><strong class=\"MEDEMPH\">Description</strong></p></th>\n" +
"<th align=\"left\" valign=\"top\" width=\"29%\">\n" +
"\n" +
"<p><strong class=\"MEDEMPH\">Cause</strong> </p></th>\n" +
"<th align=\"left\" valign=\"top\" width=\"40%\">\n" +
"\n" +
"<p><strong class=\"MEDEMPH\">Action</strong></p></th></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"31%\">\n" +
"<p>AdminUI is not available at<br /><tt class=\"file-path\">http://<i class=\"var\">CoordinatorServerIP</i>:8080/adminui/</tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"29%\">\n" +
"<p>ENIQ Events Web Server process is not running.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"40%\">\n" +
"<p>Ensure ENIQ Events Web Server process is running by executing <tt class=\"input\"><b>/eniq/sw/bin/webserver status</b></tt> on the coordinator blade.</p><br />\n" +
"<p>If the process is not running, start it by executing<tt class=\"input\"><b>/eniq/sw/bin/webserver\n" +
"start</b></tt> .</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"31%\">\n" +
"<p>No data is loaded.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"29%\">\n" +
"<p>There are no files in the mounted OSS directories, or in <tt class=\"LITERALMONO\">/eniq/data/pmdata</tt> directory on Ericsson Network OSS Server</p><br />\n" +
"<p><strong class=\"MEDEMPH\">or</strong></p><br />\n" +
"<p>An Ericsson Network OSS process is down</p><br />\n" +
"<p><strong class=\"MEDEMPH\">or</strong></p><br />\n" +
"<p>System is in maintenance mode (execution profile is in NoLoads)</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"40%\">\n" +
"<p>If using OSS-RC, check that PM mediators collect data files\n" +
"from network element, and that symbolic links are created under <tt class=\"LITERALMONO\">/ossrc/data/pmMediation/pmData</tt> directory on OSS.</p><br />\n" +
"<p>The same links should appear under the <tt class=\"LITERALMONO\">/eniq/data/pmdata</tt> directory on ENIQ Server.</p><br />\n" +
"<p>Check status of scheduler, engine, dwhdb, and repdb.</p><br />\n" +
"<p><strong class=\"MEDEMPH\">or</strong></p><br />\n" +
"<p>If using OSS-RC, check that PM mediators are collecting data,\n" +
"NFS/SSH mount is working.</p><br />\n" +
"<p><strong class=\"MEDEMPH\">or</strong></p><br />\n" +
"<p>Check the DWHDB filling ratio (this information is stored in <tt class=\"file-path\">/eniq/log/sw_log/iq/dwhdb_usage.log</tt></p><br />\n" +
"<p><strong class=\"MEDEMPH\">Note:</strong> The filling ratio log file is created the first\n" +
"time the ratio exceeds 85%. If there is no <tt class=\"file-path\">dwhdb_usage.log</tt>, the ratio has never exceeded 85%.</p><br />\n" +
"<p>If the ratio is over 90%, the database is considered full and\n" +
"ENIQ is switched to maintenance mode.</p><br />\n" +
"<p>If the DWHDBs filling ratio has been over 90% but no recent log\n" +
"entries are found (filling ratio has dropped below 90%), switch ENIQ\n" +
"back to normal mode. Type on the command line:<br /><tt class=\"input\"><b>engine -e\n" +
"changeProfile Normal</b></tt></p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"31%\">\n" +
"<p>Database is full.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"29%\">\n" +
"<p>The database is too small for the amount of data.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"40%\">\n" +
"<p>Contact local Ericsson support.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"31%\">\n" +
"<p>Engine does not start.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"29%\">\n" +
"<p>See <a href='javascript:parent.parent.parent.showAnchor(\"tbl-error\")' class=\"xref\"> Table 7</a>.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"40%\">\n" +
"<p>See <a href='javascript:parent.parent.parent.showAnchor(\"tbl-error\")' class=\"xref\"> Table 7</a>.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"31%\">\n" +
"<p>Server has ran out of disc space.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"29%\">\n" +
"<p>Old snapshot is consuming unnecessary disc space.</p><br />\n" +
"<p><strong class=\"MEDEMPH\">or</strong> </p><br />\n" +
"<p>Ericsson Network OSS logging has been left on detailed level </p><br />\n" +
"<p><strong class=\"MEDEMPH\">or </strong></p><br />\n" +
"<p>ENIQ has been receiving faulty data which is not cleared out from\n" +
"/ <tt class=\"file-path\">eniq/data/rejected/ /eniq/data/etldata/*/failed</tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"40%\">\n" +
"<p>Remove unnecessary snapshots if any exist.</p><br />\n" +
"<p>Remove unnecessary log files and decrease the logging level.</p><br />\n" +
"<p>Remove unnecessary faulty data and check the data mediation.</p><br />\n" +
"<p>After the removals, stop all Ericsson Network OSS processes and\n" +
"start them again.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"31%\">\n" +
"<p>After installation, system loads data, but monitoring does\n" +
"not show measurement types</p><br />\n" +
"<p><strong class=\"MEDEMPH\">or</strong></p><br />\n" +
"<p>A new measurement type is activated in the system, but monitoring\n" +
"is not showing the new measurement type.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"29%\">\n" +
"<p>Monitored types are not updated.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"40%\">\n" +
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
"<td align=\"left\" width=\"31%\">\n" +
"<p>Monitoring is showing deactivated measurement types.</p><br />\n" +
"<p><strong class=\"MEDEMPH\">or</strong></p><br />\n" +
"<p>Monitoring is showing deleted measurement types.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"29%\">\n" +
"<p>Monitored types are not updated.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"40%\">\n" +
"<p>When you set a type as inactive using AdminUI, by default,\n" +
"the type is no longer monitored after three days.</p><br />\n" +
"<p>The default value can be changed by changing the <tt class=\"LITERALMONO\">lookahead</tt> UpdateMonitoring property of the DWH_MONITOR tech pack. The <tt class=\"LITERALMONO\">lookahead</tt> value shows the number of days from today (the\n" +
"default is 2).  To change the value, contact local Ericsson support.</p><br />\n" +
"<p><strong class=\"MEDEMPH\">or</strong></p><br />\n" +
"<p>The System gets measurement types from data stream dynamically\n" +
"once a day and monitoring is build upon these measurement types. If\n" +
"you delete a measurement type, but the data stream includes measurements\n" +
"of the deleted type, the type you deleted continues to be displayed\n" +
"in monitoring.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"31%\">\n" +
"<p>After loading late data, AdminUI does not show data that\n" +
"is older than three days.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"29%\">\n" +
"<p>By default, loaded late data that is older than three days\n" +
"is not updated to AdminUI.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"40%\">\n" +
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
"<p><strong class=\"MEDEMPH\">Note:</strong> This operation may take a long time if there\n" +
"is a large amount of data.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"31%\">\n" +
"<p>Engine, Scheduler, DWH database, or ETL database is showing\n" +
"a red light in AdminUI Loader status view.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"29%\">\n" +
"<p>The service is not running.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"40%\">\n" +
"<p>Make sure the service with the red light is running by\n" +
"following the instructions in this guide.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"31%\">\n" +
"<p>Sets are dropped from queue.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"29%\">\n" +
"<p>Each set enters the queue with a priority from 0-15. Priority\n" +
"is defined in the set. Each set is given a queue time limit. When\n" +
"the set has been in the queue for this time limit, the priority is\n" +
"increased. When a set reaches priority 15, and has not been entered\n" +
"into the execution slot, it waits for one more queue time limit and\n" +
"then it is removed from the priority queue. This can happen in a backlog\n" +
"situation.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"40%\">\n" +
"<p>Usually no action is required.</p><br />\n" +
"<p>If this problem occurs frequently, contact local Ericsson Support.</p>\n" +
"</td></tr></table>\n" +
"\n" +
"\n" +
"\n" +
"<br />\n"+
"</div>\n";

