var C4=
"<div id=\"content\" class=\"body-content\">\n"+
"<h1><span class=\"CHAPNUMBER\">4 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_9m4i\"></a><a name=\"CHAPTER4\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Corrective Maintenance</a></span></h1>\n" +
"\n" +
"<p><a name=\"tbl-error\"></a>\n" +
"The following are actions to take when a system error message is\n" +
"displayed.</p>\n" +
"\n" +
"\n" +
"<a name=\"TABLE7\"></a>\n" +
"<table class=\"tblcnt\" width=\"100%\">\n" +
"<caption>Table 7 &nbsp;&nbsp; Error Messages</caption>\n" +
"<tr valign=\"top\">\n" +
"<th align=\"left\" valign=\"top\" width=\"31%\">\n" +
"\n" +
"<p><strong class=\"MEDEMPH\">Error</strong></p></th>\n" +
"<th align=\"left\" valign=\"top\" width=\"31%\">\n" +
"\n" +
"<p><strong class=\"MEDEMPH\">Cause</strong> </p></th>\n" +
"<th align=\"left\" valign=\"top\" width=\"38%\">\n" +
"\n" +
"<p><strong class=\"MEDEMPH\">Action</strong></p></th></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"31%\">\n" +
"<p>Engine does not start: &quot;ERROR : Fault starting svc:/eniq/engine&quot;</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"31%\">\n" +
"<p>Engine is in maintenance state, because:</p><br />\n" +
"<p>Engine script fails to work.</p><br />\n" +
"<p><strong class=\"MEDEMPH\">or</strong></p><br />\n" +
"<p>Database is full.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"38%\">\n" +
"\n" +
"<ul>\n" +
"<li><p>Log in Ericsson Network OSS server with <tt class=\"file-path\">SSH</tt>.</p>\n" +
"</li>\n" +
"<li><p>To check maintenance state, enter the following command: <tt class=\"input\"><b>svcs -a | grep eniq</b></tt></p>\n" +
"</li>\n" +
"<li><p>To check what has happened during the start of the Engine,\n" +
"see the latest <tt class=\"file-path\">start_engine</tt> logs from <tt class=\"file-path\">/eniq/log/sw_log/engine</tt>.</p>\n" +
"</li>\n" +
"<li><p>When in maintenance state, check if the database is\n" +
"full. View the dwhdb-usage log file, <tt class=\"file-path\">/eniq/log/sw_log/iq/dwhdb_usage.log</tt>, to see if one of the most recent messages is: &quot;SEVERE: DWHDB\n" +
"is full!&quot;. If it is not full, the problem may be with the engine\n" +
"script. Contact local Ericsson support.</p>\n" +
"</li>\n" +
"<li><p>To reset Engine state, run command: <tt class=\"input\"><b>svcadm clear\n" +
"svc:/eniq/engine:default</b></tt> </p>\n" +
"</li>\n" +
"</ul><br />\n" +
"<p><strong class=\"MEDEMPH\">Note:</strong> The filling ratio log file is created the first\n" +
"time the ratio exceeds 85%. If there is no <tt class=\"file-path\">dwhdb_usage.log</tt>, the ratio has never exceeded 85%. </p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"31%\">\n" +
"<p>Parsing errors &quot;org.xml.sax.SAXParseException: XML\n" +
"document structures must start and end within the same entity.&quot; </p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"31%\">\n" +
"<p>PM data is corrupted or invalid.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"38%\">\n" +
"<p>Ensure the data is valid.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"31%\">\n" +
"<p>Engine error log: &quot;Can't find table for DC_E_XXX_YYYY:RAW\n" +
"@ YYYY-MM-DD&quot;, but dateString is between current date (YYYY-MM-DD)\n" +
"and last partition endtime (YYYY-MM-DD)<br /></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"31%\">\n" +
"<p>There is no partition for the date selected.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"38%\">\n" +
"<p>Wait until the Data Warehouse database is updated and a\n" +
"new partition has been created.</p>\n" +
"</td></tr></table>\n" +
"\n"+
"</div>\n";

