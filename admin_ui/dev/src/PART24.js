var C24=
"<div id=\"content\" class=\"body-content\">\n"+
"<h1><span class=\"CHAPNUMBER\">24 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_ig8r\"></a><a name=\"CHAPTER24\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Automatic Housekeeping</a></span></h1>\n" +
"\n" +
"<p>\n" +
"ENIQ has a number of automatic housekeeping activities that are\n" +
"described in <a href='javascript:parent.parent.parent.showAnchor(\"tbl-house\")' class=\"xref\"> Table 19</a>.</p>\n" +
"\n" +
"<p><a name=\"tbl-house\"></a>\n" +
"Contact Ericsson Customer Support, if you require changes to the\n" +
"default intervals.</p>\n" +
"\n" +
"\n" +
"<a name=\"TABLE19\"></a>\n" +
"<table class=\"tblgrp\" width=\"100%\">\n" +
"<caption>Table 19 &nbsp;&nbsp; Automatic Housekeeping Activities</caption>\n" +
"<tr>\n" +
"<td>\n" +
"<table class=\"tblcnt\" width=\"100%\">\n" +
"<tr valign=\"top\">\n" +
"<th align=\"left\" valign=\"top\" width=\"23%\">\n" +
"\n" +
"<p>Action Name</p></th>\n" +
"<th align=\"left\" valign=\"top\" width=\"17%\">\n" +
"\n" +
"<p>Interval</p></th>\n" +
"<th align=\"left\" valign=\"top\" width=\"28%\">\n" +
"\n" +
"<p>Target Directory or Database</p></th>\n" +
"<th align=\"left\" valign=\"top\" width=\"32%\">\n" +
"\n" +
"<p>Description</p></th></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"23%\" rowspan=\"2\">\n" +
"<p>Cleanup_logdir</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"17%\" rowspan=\"2\">\n" +
"<p>Every Monday at 4 am.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"28%\">\n" +
"<p><tt class=\"file-path\">/eniq/log/sw_log</tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"32%\">\n" +
"<p>Deletes any file older than 65 days, except upgrade related\n" +
"files under <tt class=\"file-path\">/eniq/log/sw_log/upgrade</tt> directory.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"28%\">\n" +
"<p><tt class=\"file-path\">/eniq/data/rejected</tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"32%\">\n" +
"<p>Deletes any file older than 65 days, except upgrade related\n" +
"files under <tt class=\"file-path\">/eniq/log/sw_log/upgrade</tt> directory.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"23%\">\n" +
"<p>Diskmanager_DWH_BASE</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"17%\">\n" +
"<p>Every day at 9 pm.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"28%\">\n" +
"<p><tt class=\"file-path\">/eniq/log/sw_log/engine/DWH_BASE/</tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"32%\">\n" +
"<p>Creates an archive zip file of files older than 7 days. </p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"23%\">\n" +
"<p>Diskmanager_/eniq/data/pmdata/eniq_oss_1/alarmData</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"17%\">\n" +
"<p>Every 15 minutes.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"28%\">\n" +
"<p><tt class=\"file-path\">/eniq/data/pmdata/eniq_oss_1/alarmData</tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"32%\">\n" +
"<p>If <tt class=\"file-path\">alarmData</tt> directory exists,\n" +
"removes any files older than 15 minutes. </p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"23%\" rowspan=\"4\">\n" +
"<p>Diskmanager_[INTERFACE_NAME]</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"17%\" rowspan=\"4\">\n" +
"<p>Every day at 5 am.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"28%\">\n" +
"<p><tt class=\"file-path\">/eniq/archive/<i class=\"var\">[OSS_NAME]</i>/<i class=\"var\">[TYPE]</i>/archive/</tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"32%\">\n" +
"<p>Deletes any file older than 15 days.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"28%\">\n" +
"<p><tt class=\"file-path\">/eniq/archive/<i class=\"var\">[OSS_NAME]</i>/<i class=\"var\">[TYPE]</i>/processed/	</tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"32%\">\n" +
"<p>Deletes any file older than 15 days. </p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"28%\">\n" +
"<p><tt class=\"file-path\">/eniq/archive/<i class=\"var\">[OSS_NAME]</i>/<i class=\"var\">[TYPE]</i>/double/</tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"32%\">\n" +
"<p>Deletes any file older than 15 days.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"28%\">\n" +
"<p><tt class=\"file-path\">/eniq/archive/<i class=\"var\">[OSS_NAME]</i>/<i class=\"var\">[TYPE]</i>/failed/</tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"32%\">\n" +
"<p>Deletes any file older than 15 days.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"23%\" rowspan=\"3\">\n" +
"<p>Diskmanager_[TECHPACK_NAME]</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"17%\" rowspan=\"3\">\n" +
"<p>Every day at 8 pm.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"28%\">\n" +
"<p><tt class=\"file-path\">/eniq/log/sw_log/iqloader/<i class=\"var\">[TECHPACK_NAME]</i>/</tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"32%\">\n" +
"<p>Creates an archive zip file of files older than 2 days.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"28%\">\n" +
"<p><tt class=\"file-path\">/eniq/data/rejected/<i class=\"var\">[TECHPACK_NAME]</i>/	</tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"32%\">\n" +
"<p>Creates an archive zip file of files older than 2 days.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"28%\">\n" +
"<p><tt class=\"file-path\">/eniq/log/sw_log/engine/<i class=\"var\">[TECHPACK_NAME]</i></tt>	</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"32%\">\n" +
"<p>Creates an archive zip file of files older than 2 days.</p>\n" +
"</td></tr></table></td></tr>\n" +
"<tr>\n" +
"<td>\n" +
"<table class=\"tblcnt tblcnt_margin\" width=\"100%\">\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"23%\" rowspan=\"2\">\n" +
"<p>Diskmanager_DWH_MONITOR</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"17%\" rowspan=\"2\">\n" +
"<p>Every day at 8 pm. </p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"28%\" rowspan=\"2\">\n" +
"<p><tt class=\"file-path\">/eniq/data/etldata/<i class=\"var\">[TECHPACK_NAME]_[MEASUREMENT_TYPE_NAME]/failed</i></tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"32%\">\n" +
"<p>Creates an archive zip file of failed files older than\n" +
"7 days. </p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"32%\">\n" +
"<p>Deletes archive.zip files older than 60 Days will be deleted.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"23%\">\n" +
"<p>svc:/eniq/esm:default</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"17%\">\n" +
"<p>Always</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"28%\">\n" +
"<p>Local Eniq SMF services</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"32%\">\n" +
"<p>Monitors local ENIQ SMF services and clear's and starts\n" +
"any service that have gone into Maintenance</p>\n" +
"</td></tr></table></td></tr></table>\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>Due to an internal error in tech pack development tools the\n" +
"Diskmanager set is not executed automatically. Execute the set from\n" +
"AdminUI <b>Set Scheduling</b> by selecting the specific\n" +
"tech pack and executing the set <tt class=\"file-path\">Diskmanager_[TECHPACK_NAME]</tt>. Development tools have been fixed for further development.</dd></dl><br />\n" +
"\n"+
"</div>\n";

