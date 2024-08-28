var C35=
"<div id=\"content\" class=\"body-content\">\n"+
"<h1><span class=\"CHAPNUMBER\">35 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_ubpc\"></a><a name=\"CHAPTER35\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">ENIQ Services</a></span></h1>\n" +
"\n" +
"<p>\n" +
"In <a name=\"tbl-EniqServices\" href='javascript:parent.parent.parent.showAnchor(\"tbl-EniqServices\")' class=\"xref\"> Table 25</a>, each ENIQ service and other\n" +
"important services are described.</p>\n" +
"\n" +
"\n" +
"<a name=\"TABLE25\"></a>\n" +
"<table class=\"tblcnt\" width=\"100%\">\n" +
"<caption>Table 25 &nbsp;&nbsp; ENIQ and other important services</caption>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"24%\">\n" +
"<p>Service</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"51%\">\n" +
"<p>Description</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"26%\">\n" +
"<p>Runs on which server in Multi-blade setup</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"24%\">\n" +
"<p>svc:/eniq/rmiregistry</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"51%\">\n" +
"<p>Responsible for communication between 2 services running\n" +
"on different servers	Coordinator Engine</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"26%\">\n" +
"<p>Coordinator </p><br />\n" +
"<p>Engine</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"24%\">\n" +
"<p>svc:/eniq/licmgr</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"51%\">\n" +
"<p>Fetches license related information from Sentinel</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"26%\">\n" +
"<p>Coordinator</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"24%\">\n" +
"<p>svc:/eniq/repdb</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"51%\">\n" +
"<p>A database service for REPDB (SQL Anywhere) operations</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"26%\">\n" +
"<p>Coordinator</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"24%\">\n" +
"<p>svc:/eniq/dwhdb</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"51%\">\n" +
"<p>Service responsible for dwhdb IQ server operations</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"26%\">\n" +
"<p>Coordinator</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"24%\">\n" +
"<p>svc:/eniq/webserver</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"51%\">\n" +
"<p>Responsible for running all web applications</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"26%\">\n" +
"<p>Coordinator</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"24%\">\n" +
"<p>svc:/eniq/engine</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"51%\">\n" +
"<p>Engine is main service of the platform which is used to\n" +
"run all actions scheduled by scheduler</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"26%\">\n" +
"<p>Engine</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"24%\">\n" +
"<p>svc:/eniq/scheduler</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"51%\">\n" +
"<p>Scheduler is clock of platform which runs every second\n" +
"and check which sets are supposed to be executed</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"26%\">\n" +
"<p>Coordinator</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"24%\">\n" +
"<p>svc:/eniq/dwh_reader</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"51%\">\n" +
"<p>Responsible for all database read and write operatons.\n" +
"Same service is responsible for read operations on reader server and\n" +
"for write operations on writer server</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"26%\">\n" +
"<p>Dwh_reader_1</p><br />\n" +
"<p>Dwh_reader_2</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"24%\">\n" +
"<p>svc:/eniq/esm</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"51%\">\n" +
"<p>Monitors local ENIQ SMF services. Clears and starts any\n" +
"services that have gone into Maintenance</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"26%\">\n" +
"<p>All</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"24%\">\n" +
"<p>svc:/storage/NASd</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"51%\">\n" +
"<p>Service to monitor and mount NAS.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"26%\">\n" +
"<p>All</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"24%\">\n" +
"<p>svc:/milestone/NAS-online</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"51%\">\n" +
"<p>Milestone to indicate if NAS FileSystems are Available.\n" +
"Restarts all ENIQ services if NAS filesystems are not available. </p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"26%\">\n" +
"<p>All</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"24%\">\n" +
"<p>svc:/application/management/hostsync</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"51%\">\n" +
"<p>A service introduced for Blades Service to Monitor NAS\n" +
"for updates to hosts master file and update local hosts file if necessary</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"26%\">\n" +
"<p>All</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"24%\">\n" +
"<p>svc:/eniq/roll-snap</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"51%\">\n" +
"<p>The rolling snapshot is a self-maintaining mechanism. It\n" +
"creates and deletes snapshots of the ENIQ Events server when required</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"26%\">\n" +
"<p>All</p>\n" +
"</td></tr></table>\n" +
"<p>\n" +
"Further details of each of these and other services, including\n" +
"dependencies and location of log files, can be found by executing\n" +
"the following command:</p>\n" +
"\n" +
"<p><tt class=\"input\"><b>svcs -l <i class=\"var\">&lt;service_name&gt;</i></b></tt></p>\n" +
"\n"+
"</div>\n";

