var C2=
"<div id=\"content\" class=\"body-content\">\n"+
"<h1><span class=\"CHAPNUMBER\">2 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_8z9p\"></a><a name=\"CHAPTER2\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">System Overview</a></span></h1>\n" +
"\n" +
"<p>\n" +
"The operation of Ericsson Network OSS is based around a series\n" +
"of event-driven data.  Events occur in the network, and these are\n" +
"captured by network elements, probes, or software systems. These are\n" +
"event producers, and they place these events in files, a database,\n" +
"or in a stream. The mediation layer within Ericsson Network OSS collects\n" +
"and processes this data from these event producers. Once collected\n" +
"the events are parsed, reformatted, and binary load files are created\n" +
"for loading into the data warehouse.  The data warehouse is partitioned\n" +
"such that the data can be made available to higher layers within a\n" +
"very short time interval.  Where multiple data sources are used, correlation\n" +
"between those data sources is performed, for example, events from\n" +
"RNC and SGSN that contain IMSI references would be correlated to get\n" +
"a view of the IMSI across the radio and packet core interfaces. </p>\n" +
"\n" +
"<p>\n" +
"User interface applications, built upon the data warehouse, run\n" +
"queries and the visual results facilitate monitoring and troubleshooting\n" +
"use cases.</p>\n" +
"\n" +
"<p>\n" +
"A more detailed technical overview is available in the <em class=\"LOWEMPH\">ENIQ Events System Description</em>, <a href='javascript:parent.parent.parent.showAnchor(\"rf-ProdDesc\")' class=\"xref\">Reference [3]</a>. </p>\n" +
"\n" +
"\n"+
"</div>\n";

var C2_1=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">2.1 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_7kzh\"></a><a name=\"CHAPTER2.1\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Ericsson Network OSS Directory Structure</a></span></h2>\n" +
"\n" +
"<p>\n" +
"Ericsson Network OSS has a standard directory structure. The common\n" +
"directories of Ericsson Network OSS and their content are outlined\n" +
"in <a name=\"TableforDirectoryStructure\" href='javascript:parent.parent.parent.showAnchor(\"TableforDirectoryStructure\")' class=\"xref\"> Table 1</a>:</p>\n" +
"\n" +
"<a name=\"TABLE1\"></a>\n" +
"<table class=\"tblcnt\" width=\"100%\">\n" +
"<caption>Table 1 &nbsp;&nbsp; Directory Structure</caption>\n" +
"<tr valign=\"top\">\n" +
"<th align=\"left\" valign=\"top\">\n" +
"\n" +
"<p>Directory</p></th>\n" +
"<th align=\"left\" valign=\"top\">\n" +
"\n" +
"<p>Contents</p></th></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\">\n" +
"<p><tt class=\"file-path\">/eniq/log/sw_log</tt></p>\n" +
"</td>\n" +
"<td align=\"left\">\n" +
"<p>Ericsson Network OSS log files</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\">\n" +
"<p><tt class=\"file-path\">/eniq/local_logs</tt></p>\n" +
"</td>\n" +
"<td align=\"left\">\n" +
"<p>Log files for local file system</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\">\n" +
"<p><tt class=\"file-path\">/eniq/sw/conf</tt></p>\n" +
"</td>\n" +
"<td align=\"left\">\n" +
"<p>Ericsson Network OSS configuration files</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\">\n" +
"<p><tt class=\"file-path\">/eniq/sw/bin</tt></p>\n" +
"</td>\n" +
"<td align=\"left\">\n" +
"<p>Scripts to start and stop Ericsson Network OSS services</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\">\n" +
"<p><tt class=\"file-path\">/eniq/database</tt></p>\n" +
"</td>\n" +
"<td align=\"left\">\n" +
"<p>Ericsson Network OSS database files</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\">\n" +
"<p><tt class=\"file-path\">/eniq/archive</tt></p>\n" +
"</td>\n" +
"<td align=\"left\">\n" +
"<p>Processed data from parser</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\">\n" +
"<p><tt class=\"file-path\">/eniq/data/etldata/<i class=\"var\">techpack_name_counter_name</i>/failed</tt></p>\n" +
"</td>\n" +
"<td align=\"left\">\n" +
"<p>Failed data from loader</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\">\n" +
"<p><tt class=\"file-path\">/eniq/local_logs/rolling_snapshot_logs</tt></p>\n" +
"</td>\n" +
"<td align=\"left\">\n" +
"<p>Contains logs regarding the roll back service and manual\n" +
"snapshots</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\">\n" +
"<p><tt class=\"file-path\">/eniq/log/sw_log</tt></p>\n" +
"</td>\n" +
"<td align=\"left\">\n" +
"<p>This location collects diverse logs for engine, scheduler,\n" +
"tech packs, interfaces, adminui, alarms</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\">\n" +
"<p><tt class=\"file-path\">/eniq/local_logs/iq/dwhdb.iqmsg</tt></p>\n" +
"</td>\n" +
"<td align=\"left\">\n" +
"<p>Master database log file</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\">\n" +
"<p><tt class=\"file-path\">/eniq/log/sw_log/glassfish</tt></p>\n" +
"</td>\n" +
"<td align=\"left\">\n" +
"<p>Contains GlassFish logs</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\">\n" +
"<p><tt class=\"file-path\">/eniq/log/sw_log/mediation_gw/M_E_LTEES/events_oss_&lt;n&gt;/topology</tt></p>\n" +
"</td>\n" +
"<td align=\"left\">\n" +
"<p>Details of LTE ES processed topology files </p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\">\n" +
"<p><tt class=\"file-path\">/eniq/log/sw_log/mediation_gw/M_E_LTEES/events_oss_&lt;n&gt;/celltrace</tt></p>\n" +
"</td>\n" +
"<td align=\"left\">\n" +
"<p>Details of LTE ES processed celltrace files </p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\">\n" +
"<p><tt class=\"file-path\">/eniq/log/sw_log/mediation_gw</tt></p>\n" +
"</td>\n" +
"<td align=\"left\">\n" +
"<p>Contains Controlzone, EC stop/start and install logs</p>\n" +
"</td></tr></table>\n" +
"\n" +
"\n"+
"</div>\n";

