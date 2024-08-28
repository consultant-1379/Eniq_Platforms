var C34=
"<div id=\"content\" class=\"body-content\">\n"+
"<h1><a name=\"chl-ES_troubleshooting\"></a><span class=\"CHAPNUMBER\">34 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_pavy\"></a><a name=\"CHAPTER34\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Troubleshooting</a></span></h1>\n" +
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

var C34_1=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">34.1 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_ox6r\"></a><a name=\"CHAPTER34.1\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Log Collector Tool</a></span></h2>\n" +
"\n" +
"<p>\n" +
"Log Collector tool is used to consolidate different log files of\n" +
"ENIQ Statistics belonging to a particular date into a zip file. This\n" +
"zip file will help in troubleshooting issues.</p>\n" +
"\n" +
"<p>\n" +
"Log Collector tool can be invoked manually by using following steps:</p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Log onto Coordinator server as root</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Enter the following command:<p>\n" +
"<tt class=\"input\"><b>{root} #:bash /eniq/installation/core_install/eniq_log_collector/bin/log_collector.bsh</b></tt></p>\n" +
"</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Enter the date in ddmmyyyy format, for which logs need\n" +
"to be collected.<p>\n" +
"Example: 09112016. This indicates 9th November 2016 </p>\n" +
"</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">The script may take some time to execute, depending on\n" +
"size of the logs. At the end of the execution of the script, a zip\n" +
"file log_collector_&lt;TimeStamp&gt; is created at path <tt class=\"input\"><b>/eniq/log/log_collector/</b></tt> <dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>If Log Collector Tool is not able to create zip file with\n" +
"below message, then execute following steps:<br /><br /><strong class=\"MEDEMPH\">Message:</strong><br />Cannot create zip file as the size of zip file log_collector_&lt;TimeStamp&gt;.zip\n" +
"is &lt;n&gt; MB.<br />Maximum Zip file size mentioned in the configuration\n" +
"file is 50MB.<br />Please modify MAX_ZIP_SIZE in the configuration\n" +
"file.<br /><br /><strong class=\"MEDEMPH\">Steps:</strong> </dd></dl><br />\n" +
"<ol type=\"a\">\n" +
"\n" +
"<li class=\"substep\">Modify the parameter \"MAX_ZIP_SIZE\" in the following configuration\n" +
"file to have the required memory size. The required memory size has\n" +
"to be a number more than the memory &lt;n&gt; MB displayed in the above\n" +
"message.<br /><tt class=\"input\"><b>/eniq/installation/core_install/eniq_log_collector/config/log_collector.cfg</b></tt>\n" +
"</li>\n" +
"\n" +
"<li class=\"substep\">Save the configuration file.\n" +
"</li>\n" +
"</ol></li></ol>\n" +
"<p>\n" +
"Following table shows the list of logs and their locations :</p>\n" +
"\n" +
"\n" +
"<a name=\"TABLE22\"></a>\n" +
"<table class=\"tblcnt\" width=\"100%\">\n" +
"<caption>Table 22 &nbsp;&nbsp; Log Collector Logs</caption>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"7%\">\n" +
"<p><strong class=\"MEDEMPH\">S NO.</strong></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"32%\">\n" +
"<p><strong class=\"MEDEMPH\">LOG NAME</strong></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"45%\">\n" +
"<p><strong class=\"MEDEMPH\">LOG LOCATION</strong></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"15%\">\n" +
"<p><strong class=\"MEDEMPH\">SERVER</strong></p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"7%\">\n" +
"<p>1</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"32%\">\n" +
"<p>adminui.log</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"45%\">\n" +
"<p> <tt class=\"input\"><b>/eniq/log/sw_log/adminui/</b></tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"15%\">\n" +
"<p>Coordinator</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"7%\">\n" +
"<p>2</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"32%\">\n" +
"<p>catalina.out</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"45%\">\n" +
"<p><tt class=\"input\"><b>/eniq/log/sw_log/adminui/</b></tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"15%\">\n" +
"<p>Coordinator</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"7%\">\n" +
"<p>3</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"32%\">\n" +
"<p>auto_lu.log</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"45%\">\n" +
"<p><tt class=\"input\"><b>/eniq/local_logs/auto_lu/</b></tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"15%\">\n" +
"<p>All Blades</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"7%\">\n" +
"<p>4</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"32%\">\n" +
"<p>connectd.log</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"45%\">\n" +
"<p><tt class=\"input\"><b>/eniq/local_logs/connectd/</b></tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"15%\">\n" +
"<p>All Blades</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"7%\">\n" +
"<p>5</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"32%\">\n" +
"<p>engine&lt;DATE&gt;.log</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"45%\">\n" +
"<p><tt class=\"input\"><b>/eniq/log/sw_log/engine/</b></tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"15%\">\n" +
"<p>Coordinator</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"7%\">\n" +
"<p>6</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"32%\">\n" +
"<p>engine-PriorityQueue&lt;DATE&gt;.log</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"45%\">\n" +
"<p><tt class=\"input\"><b>/eniq/log/sw_log/engine/</b></tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"15%\">\n" +
"<p>Coordinator</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"7%\">\n" +
"<p>7</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"32%\">\n" +
"<p>error.log-&lt;DATE&gt;.log</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"45%\">\n" +
"<p><tt class=\"input\"><b>/eniq/log/sw_log/engine/</b></tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"15%\">\n" +
"<p>Coordinator</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"7%\">\n" +
"<p>8</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"32%\">\n" +
"<p>start_engine_&lt;DATE&gt;_&lt;TIME&gt;.log</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"45%\">\n" +
"<p><tt class=\"input\"><b>/eniq/log/sw_log/engine/</b></tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"15%\">\n" +
"<p>Coordinator</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"7%\">\n" +
"<p>9</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"32%\">\n" +
"<p>stop_engine_&lt;DATE&gt;_&lt;TIME&gt;.log</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"45%\">\n" +
"<p><tt class=\"input\"><b>/eniq/log/sw_log/engine/</b></tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"15%\">\n" +
"<p>Coordinator</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"7%\">\n" +
"<p>10</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"32%\">\n" +
"<p>start_webserver_&lt;DATE&gt;_&lt;TIME&gt;.log</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"45%\">\n" +
"<p><tt class=\"input\"><b>/eniq/log/sw_log/engine/</b></tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"15%\">\n" +
"<p>Coordinator</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"7%\">\n" +
"<p>11</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"32%\">\n" +
"<p>stop_webserver_&lt;DATE&gt;_&lt;TIME&gt;.log</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"45%\">\n" +
"<p><tt class=\"input\"><b>/eniq/log/sw_log/engine/</b></tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"15%\">\n" +
"<p>Coordinator</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"7%\">\n" +
"<p>12</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"32%\">\n" +
"<p>sqlerror-&lt;DATE&gt;.log</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"45%\">\n" +
"<p><tt class=\"input\"><b>/eniq/log/sw_log/engine/</b></tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"15%\">\n" +
"<p>Coordinator</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"7%\">\n" +
"<p>13</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"32%\">\n" +
"<p>FileSystemCheck-&lt;DATE&gt;.log</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"45%\">\n" +
"<p><tt class=\"input\"><b>/eniq/log/sw_log/engine/</b></tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"15%\">\n" +
"<p>Coordinator</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"7%\">\n" +
"<p>14</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"32%\">\n" +
"<p>file-&lt;DATE&gt;.log</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"45%\">\n" +
"<p><tt class=\"input\"><b>/eniq/log/sw_log/engine/</b></tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"15%\">\n" +
"<p>Coordinator</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"7%\">\n" +
"<p>15</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"32%\">\n" +
"<p>failed_loader_file&lt;DATE&gt;.log</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"45%\">\n" +
"<p><tt class=\"input\"><b>/eniq/log/sw_log/engine/</b></tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"15%\">\n" +
"<p>Coordinator</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"7%\">\n" +
"<p>16</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"32%\">\n" +
"<p>start_repdb_&lt;DATE&gt;_&lt;TIME&gt;.log</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"45%\">\n" +
"<p><tt class=\"input\"><b>/eniq/log/sw_log/asa/</b></tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"15%\">\n" +
"<p>Coordinator</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"7%\">\n" +
"<p>17</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"32%\">\n" +
"<p>stopt_repdb_&lt;DATE&gt;_&lt;TIME&gt;.log</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"45%\">\n" +
"<p><tt class=\"input\"><b>/eniq/log/sw_log/asa/</b></tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"15%\">\n" +
"<p>Coordinator</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"7%\">\n" +
"<p>18</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"32%\">\n" +
"<p>start_rmiregistry_&lt;DATE&gt;_&lt;Time&gt;.log</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"45%\">\n" +
"<p><tt class=\"input\"><b>/eniq/log/sw_log/rmiregistry/</b></tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"15%\">\n" +
"<p>Coordinator</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"7%\">\n" +
"<p>19</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"32%\">\n" +
"<p>stop_rmiregistry_&lt;DATE&gt;_&lt;Time&gt;.log</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"45%\">\n" +
"<p><tt class=\"input\"><b>/eniq/log/sw_log/rmiregistry/</b></tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"15%\">\n" +
"<p>Coordinator</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"7%\">\n" +
"<p>20</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"32%\">\n" +
"<p>start_scheduler_&lt;DATE&gt;_&lt;Time&gt;.log</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"45%\">\n" +
"<p><tt class=\"input\"><b>/eniq/log/sw_log/scheduler/</b></tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"15%\">\n" +
"<p>Coordinator</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"7%\">\n" +
"<p>21</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"32%\">\n" +
"<p>stop_scheduler_&lt;DATE&gt;_&lt;Time&gt;.log</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"45%\">\n" +
"<p><tt class=\"input\"><b>/eniq/log/sw_log/scheduler/</b></tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"15%\">\n" +
"<p>Coordinator</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"7%\">\n" +
"<p>22</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"32%\">\n" +
"<p>start_dwhdb_&lt;DATE&gt;_&lt;Time&gt;.log</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"45%\">\n" +
"<p><tt class=\"input\"><b>/eniq/log/sw_log/iq/dwhdb/</b></tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"15%\">\n" +
"<p>Coordinator</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"7%\">\n" +
"<p>23</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"32%\">\n" +
"<p>stop_dwhdb_&lt;DATE&gt;_&lt;Time&gt;.log</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"45%\">\n" +
"<p><tt class=\"input\"><b>/eniq/log/sw_log/iq/dwhdb/</b></tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"15%\">\n" +
"<p>Coordinator</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"7%\">\n" +
"<p>24</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"32%\">\n" +
"<p>eniq-repdb:default.log</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"45%\">\n" +
"<p><tt class=\"input\"><b>/var/svc/log/</b></tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"15%\">\n" +
"<p>All Blades</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"7%\">\n" +
"<p>25</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"32%\">\n" +
"<p>ericsson-eric_monitor-ddc:default.log</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"45%\">\n" +
"<p><tt class=\"input\"><b>/var/svc/log/</b></tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"15%\">\n" +
"<p>All Blades</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"7%\">\n" +
"<p>26</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"32%\">\n" +
"<p>eniq-engine:default.log</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"45%\">\n" +
"<p><tt class=\"input\"><b>/var/svc/log/</b></tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"15%\">\n" +
"<p>All Blades</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"7%\">\n" +
"<p>27</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"32%\">\n" +
"<p>eniq-esm:default.log</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"45%\">\n" +
"<p><tt class=\"input\"><b>/var/svc/log/</b></tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"15%\">\n" +
"<p>All Blades</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"7%\">\n" +
"<p>28</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"32%\">\n" +
"<p>eniq-licmgr:default.log</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"45%\">\n" +
"<p><tt class=\"input\"><b>/var/svc/log/</b></tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"15%\">\n" +
"<p>All Blades</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"7%\">\n" +
"<p>29</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"32%\">\n" +
"<p>milestone-NAS-online:default.log</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"45%\">\n" +
"<p><tt class=\"input\"><b>/var/svc/log/</b></tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"15%\">\n" +
"<p>All Blades</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"7%\">\n" +
"<p>30</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"32%\">\n" +
"<p>eniq-rmiregistry:default.log</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"45%\">\n" +
"<p><tt class=\"input\"><b>/var/svc/log/</b></tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"15%\">\n" +
"<p>All Blades</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"7%\">\n" +
"<p>31</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"32%\">\n" +
"<p>eniq-roll-snap:default.log</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"45%\">\n" +
"<p><tt class=\"input\"><b>/var/svc/log/</b></tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"15%\">\n" +
"<p>All Blades</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"7%\">\n" +
"<p>32</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"32%\">\n" +
"<p>eniq-scheduler:default.log</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"45%\">\n" +
"<p><tt class=\"input\"><b>/var/svc/log/</b></tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"15%\">\n" +
"<p>All Blades</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"7%\">\n" +
"<p>33</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"32%\">\n" +
"<p>licensing-sentinel:default.log</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"45%\">\n" +
"<p><tt class=\"input\"><b>/var/svc/log/</b></tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"15%\">\n" +
"<p>All Blades</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"7%\">\n" +
"<p>34</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"32%\">\n" +
"<p>eniq-webserver:default.log</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"45%\">\n" +
"<p><tt class=\"input\"><b>/var/svc/log/</b></tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"15%\">\n" +
"<p>All Blades</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"7%\">\n" +
"<p>35</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"32%\">\n" +
"<p>storage-NASd:default.log</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"45%\">\n" +
"<p><tt class=\"input\"><b>/var/svc/log/</b></tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"15%\">\n" +
"<p>All Blades</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"7%\">\n" +
"<p>36</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"32%\">\n" +
"<p>dwhdb.iqmsg</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"45%\">\n" +
"<p><tt class=\"input\"><b>/eniq/local_logs/iq/</b></tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"15%\">\n" +
"<p>All Blades</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"7%\">\n" +
"<p>37</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"32%\">\n" +
"<p>backup_repdb_&lt;DATE&gt;_&lt;TIME&gt;.log</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"45%\">\n" +
"<p><tt class=\"input\"><b>/eniq/log/sw_log/asa/</b></tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"15%\">\n" +
"<p>Coordinator</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"7%\">\n" +
"<p>38</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"32%\">\n" +
"<p>restore_repdb_&lt;DATE&gt;_&lt;TIME&gt;.log</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"45%\">\n" +
"<p><tt class=\"input\"><b>/eniq/log/sw_log/asa/</b></tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"15%\">\n" +
"<p>Coordinator</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"7%\">\n" +
"<p>39</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"32%\">\n" +
"<p>EngineAdmin-&lt;DATE&gt;.log</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"45%\">\n" +
"<p><tt class=\"input\"><b>/eniq/log/sw_log/EngineAdmin/</b></tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"15%\">\n" +
"<p>Coordinator</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"7%\">\n" +
"<p>40</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"32%\">\n" +
"<p>manage_features.log</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"45%\">\n" +
"<p><tt class=\"input\"><b>/eniq/log/feature_management_log/</b></tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"15%\">\n" +
"<p>Coordinator</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"7%\">\n" +
"<p>41</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"32%\">\n" +
"<p>dwhdb.*.srvlog</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"45%\">\n" +
"<p><tt class=\"input\"><b>/eniq/local_logs/iq/</b></tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"15%\">\n" +
"<p>Coordinator</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"7%\">\n" +
"<p>42</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"32%\">\n" +
"<p>dwhdb.*.stderr</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"45%\">\n" +
"<p><tt class=\"input\"><b>/eniq/local_logs/iq/</b></tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"15%\">\n" +
"<p>Coordinator</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"7%\">\n" +
"<p>43</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"32%\">\n" +
"<p>dwh_reader_*.iqmsg</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"45%\">\n" +
"<p><tt class=\"input\"><b>/eniq/local_logs/iq/</b></tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"15%\">\n" +
"<p>Reader</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"7%\">\n" +
"<p>44</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"32%\">\n" +
"<p>dwh_reader_*.*.srvlog</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"45%\">\n" +
"<p><tt class=\"input\"><b>/eniq/local_logs/iq/</b></tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"15%\">\n" +
"<p>Reader</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"7%\">\n" +
"<p>45</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"32%\">\n" +
"<p>dwh_reader_*.*.stderr</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"45%\">\n" +
"<p><tt class=\"input\"><b>/eniq/local_logs/iq/</b></tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"15%\">\n" +
"<p>Reader</p>\n" +
"</td></tr></table>\n" +
"\n"+
"</div>\n";

var C34_2=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><a name=\"troublechapter\"></a><span class=\"CHAPNUMBER\">34.2 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"troubleshooting\"></a><a name=\"CHAPTER34.2\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Troubleshooting Ericsson Network IQ</a></span></h2>\n" +
"\n" +
"<p><a name=\"DailyAggregation\"></a>\n" +
"The table below lists potential issues and actions to perform to\n" +
"solve them.</p>\n" +
"\n" +
"\n" +
"<a name=\"TABLE23\"></a>\n" +
"<table class=\"tblgrp\" width=\"100%\">\n" +
"<caption>Table 23 &nbsp;&nbsp; Troubleshooting ENIQ</caption>\n" +
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
"<p> In a multi-blade deployment on the coordinator blade</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p>ENIQ Web Server process is not running.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p>Ensure that the ENIQ Web Server process is running. <br /><tt class=\"input\"><b>/eniq/sw/bin/webserver status</b></tt></p><br />\n" +
"<p>In a multi-blade deployment execute the command on the coordinator\n" +
"blade.</p><br />\n" +
"<p>If the process is not running, start it by executing <br /><tt class=\"input\"><b>/eniq/sw/bin/webserver start</b></tt>.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"<p>No data is loaded.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p>This can occur because of the following:</p><br />\n" +
"<ul>\n" +
"<li><p>There are no files in the mounted OSS directories, or\n" +
"in the  <tt class=\"LITERALMONO\">/eniq/data/pmdata</tt> directory on ENIQ Server</p>\n" +
"</li>\n" +
"<li><p>An ENIQ process is down</p>\n" +
"</li>\n" +
"<li><p>System is in maintenance mode (execution profile is\n" +
"in NoLoads)</p>\n" +
"</li>\n" +
"</ul>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"\n" +
"<ul>\n" +
"<li><p>If using OSS-RC, check that PM mediators collect data\n" +
"files from the network elements, and that symbolic links are created\n" +
"under the following directories on OSS: <tt class=\"LITERALMONO\">/var/opt/ericsson/pmData/</tt> x86 servers. <br />The same links should appear under the <tt class=\"LITERALMONO\">/eniq/data/pmdata</tt> directory on ENIQ Server.<br />Check the\n" +
"status of <tt class=\"file-path\">scheduler</tt>, <tt class=\"file-path\">engine</tt>, <tt class=\"file-path\">dwhdb</tt>, and <tt class=\"file-path\">repdb</tt>.</p>\n" +
"</li>\n" +
"<li><p>If using OSS-RC, check that PM mediators are collecting\n" +
"data, NFS/SSH mount is working.</p>\n" +
"</li>\n" +
"<li><p>Check the DWHDB filling ratio (this information is stored\n" +
"in <tt class=\"file-path\">/eniq/log/sw_log/iq/dwhdb_usage.log</tt><br />The filling ratio log file is created the first time the ratio exceeds\n" +
"85%. If there is no <tt class=\"file-path\">dwhdb_usage.log</tt>, the\n" +
"ratio has never exceeded 85%.<br />If the ratio is over 90%, the database\n" +
"is considered full and ENIQ is switched to maintenance mode.<br />If\n" +
"the DWHDBs filling ratio has been over 90% but no recent log entries\n" +
"are found (filling ratio has dropped below 90%), switch ENIQ back\n" +
"to normal mode. Type on the command line: <br /><tt class=\"input\"><b>engine -e changeProfile\n" +
"Normal</b></tt></p>\n" +
"</li>\n" +
"</ul>\n" +
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
"<p>See <a href='javascript:parent.parent.parent.showAnchor(\"correctiveMain\")' class=\"link\"> Corrective Maintenance</a> Section</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p>See <a href='javascript:parent.parent.parent.showAnchor(\"correctiveMain\")' class=\"link\"> Corrective Maintenance</a> Section</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"<p>Server has no more free disk space.</p>\n" +
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
"<p>Delete unnecessary faulty data and check the data mediation.</p><br />\n" +
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
"<p>The default value can be changed by changing the UpdateMonitoring\n" +
"property of the DWH_MONITOR tech pack. The <tt class=\"file-path\">lookahead</tt> value shows the number of days from today (the default is 2).  To\n" +
"change the value, contact  Ericsson Customer Support.</p><br />\n" +
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
"<p>Ensure that the service with the red light is running by\n" +
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
"<li><p>Log in ENIQ server. Or in a multi-blade deployment on\n" +
"the engine blade as <tt class=\"file-path\">dcuser</tt>.</p>\n" +
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
"<p>After the services restart <tt class=\"file-path\">repdb</tt> and <tt class=\"file-path\">engine</tt> remain in a maintenance state.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p>This is because of a known issue where <tt class=\"file-path\">stop_asiq</tt> does not always stop all running processes and leaves the services\n" +
"in a state which cannot be cleared, even manually.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p>Check the log files for <tt class=\"file-path\">repdb</tt> and <tt class=\"file-path\">engine</tt> to identify which processes\n" +
"are hanging after restart and use <tt class=\"file-path\">kill -6</tt> on the hanging process</p><br />\n" +
"<p>The location of the logfiles depend on the system version. </p><br />\n" +
"<ul>\n" +
"<li><p>If the System Version is earlier than <tt class=\"file-path\">AOM\n" +
"901 063 R4G</tt>, the logfiles are in <tt class=\"file-path\">/eniq/sw/log/iq</tt></p>\n" +
"</li>\n" +
"<li><p>If the System Version is later than <tt class=\"file-path\">AOM\n" +
"901 063 R4G</tt>, the logfiles are in: <tt class=\"file-path\">/eniq/log/sw_log/iq</tt></p>\n" +
"</li>\n" +
"</ul>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"<p>Could not find a valid license for the busyhour configuration\n" +
"module! Contact the system administrator.</p>\n" +
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
"<p>Run below commands as <tt class=\"file-path\">dcuser</tt> on Co-ordinator Blade to delete the idle connections.</p><br />\n" +
"<p><tt class=\"input\"><b>dbisql -nogui -c \"con=dba;eng=dwhdb;links=tcpip{host=dwhdb;port=2640;verify=no};uid=dba;pwd=sql\"\n" +
"\"call drop_idle_users_exceeding_fourHR_fortyGB_Connections()\"</b></tt></p><br />\n" +
"<p><tt class=\"input\"><b>dbisql -nogui -c \"con=dba;eng=dwhdb;links=tcpip{host=dwhdb;port=2640;verify=no};uid=dba;pwd=sql\"\n" +
"\"call dropIdle_dcpublic_dcbo_Connections()\"</b></tt></p><br />\n" +
"<p>If issue still persists after executing above commands, contact\n" +
"Ericsson Customer Support.</p>\n" +
"</td></tr></table></td></tr>\n" +
"<tr>\n" +
"<td>\n" +
"<table class=\"tblcnt tblcnt_margin\" width=\"100%\">\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"<p>Daily aggregation is happening late for particular techpack\n" +
"because of data coming late and continues.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p>See <a href='javascript:parent.parent.parent.showAnchor(\"DailyAggregationNot\")' class=\"xref\">Section  34.2.1</a></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p>See <a href='javascript:parent.parent.parent.showAnchor(\"DailyAggregationNot\")' class=\"xref\">Section  34.2.1</a></p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"<p>Historical data prior to upgrade is not fetched in the\n" +
"reports as the Universe points to Count tables instead of Raw tables\n" +
"after ENIQ upgrade from 2.x or 11.x to 12.x track. However, there\n" +
"is data in COUNT table only since the upgrade.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p>New count tables were created to support delta calculation\n" +
"for new Node releases. Pre-upgrade raw data was not populated in COUNT\n" +
"tables because newly implemented  table get the data from the day\n" +
"it got added.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p>Follow these steps to migrate the data from RAW to COUNT\n" +
"table when an upgrade is performed from ENIQ 2.x or ENIQ 11.x to ENIQ\n" +
"12.x track.</p><br />\n" +
"<p> The script is placed in <tt class=\"file-path\">/eniq/sw/installer</tt> directory and is executed with <tt class=\"file-path\">dcuser</tt> permission.</p><br />\n" +
"<p> <tt class=\"input\"><b>cd /eniq/sw/installer</b></tt></p><br />\n" +
"<p><tt class=\"input\"><b> # ./data_migration.sh -t <i class=\"var\">&lt;name_of_the_techpack&gt;</i></b></tt></p><br />\n" +
"<p> Example:</p><br />\n" +
"<p><tt class=\"input\"><b> ./data_migration.sh -t DC_E_GGSN</b></tt></p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"<p>DIM tables are empty for all corresponding Vector Measurement\n" +
"counters (for example, <tt class=\"file-path\">DC.DIM_E_RBS_HSDSCHRES_V_pmRemainingResourceCheck</tt>)</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p>The <tt class=\"file-path\">DWHM_StorageTimeUpdate</tt> set\n" +
"which updated these tables has not run correctly for the TP while\n" +
"installation/upgrade of the TP.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p>Run the DWHM_StorageTimeUpdate Set for the TP from the\n" +
"Adminui. </p><br />\n" +
"<p>Go to \"ETLC Set Sheduling\" in Adminui. </p><br />\n" +
"<p>Select Set Type as Techpack and Package as the TP for which you\n" +
"want to run the set. </p><br />\n" +
"<p>Then Start the DWHM_StorageTimeUpdate set for the TP.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"<p>Upgrade fails if <tt class=\"file-path\">dcuser</tt> is not\n" +
"able to ssh to localhost without password </p><br />\n" +
"<p> <strong class=\"MEDEMPH\">or</strong></p><br />\n" +
"<p> ExecutionProfiler set fails on running the ENIQ Expansion procedure</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p>In the <tt class=\"file-path\">/eniq/home/dcuser/.ssh/directory</tt>, the files <tt class=\"file-path\">id_rsa.pub</tt> and <tt class=\"file-path\">authorized_keys</tt> have different contents.  </p><br />\n" +
"<p>To log in through SSH, both files must have the same contents.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p>Before the upgrade check the permissions of the <tt class=\"file-path\">authorized_keys</tt> file. If permissions are set to <tt class=\"file-path\">root</tt> change the permissions to <tt class=\"file-path\">dcuser</tt> then continue the upgrade.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"<p>DC_RELEASE not populated for DIM table related the individual\n" +
"vector counters. (Ex: DIM_E_RBS_NODEBFUNCTION_V_pmCapacityNodeBDlCe) </p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p>Whenever a TP is upgraded or initially installed, there\n" +
"is a set called StorageTimeAction as part of dwhmanager, which runs\n" +
"and updates the data in the DIM tables for the individual vector counters.\n" +
"If this set had not run properly ,then this issue is caused.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p>To avoid this issue</p><br />\n" +
"<ul>\n" +
"<li><p>Go to ETLC Set Scheduling in Adminui.</p>\n" +
"</li>\n" +
"<li><p>Select set type as Techpack and Package as DC_E_RBS\n" +
"(or the TP having the problem)</p>\n" +
"</li>\n" +
"<li><p>Click Start for DWHM_StorageTimeUpdate_DC_ E_RBS and\n" +
"the set starts. This populates the data for DC_RELEASE column in the\n" +
"respective DIM tables. </p>\n" +
"</li>\n" +
"</ul>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"<p>Root partition has less than 2 GB of free space</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p>User-specific data on the root partition Or Obsolete files\n" +
"in <tt class=\"file-path\">/var/sadm/</tt>pkg</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p>Delete user-specific data from root (/) partition ( that\n" +
"is, <tt class=\"file-path\">/export/home</tt> directories and so on).\n" +
"To store user-specific data, use other partitions. </p><br />\n" +
"<p>Check if there are any \"obsolete\" files in <tt class=\"file-path\">/var/sadm/pkg</tt> : </p><br />\n" +
"<p><tt class=\"input\"><b># find /var/sadm/pkg/*/save/*-*/ -name obsolete.Z -exec\n" +
"ls {} \\;</b></tt></p><br />\n" +
"<p>If \"obsolete\" files are found, delete them: </p><br />\n" +
"<p><tt class=\"input\"><b># find /var/sadm/pkg/*/save/*-*/ -name obsolete.Z -exec\n" +
"rm -rf {} \\;</b></tt></p><br />\n" +
"<p>Check the root file system: </p><br />\n" +
"<p><tt class=\"input\"><b># df -h /</b></tt></p><br />\n" +
"<p><tt class=\"input\"><b>Note: </b></tt>Removing the \"undo\" files associated with\n" +
"a patch makes it impossible to uninstall the patch.</p><br />\n" +
"<p> If less than 2GB of free space is available, then delete \"undo\"\n" +
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
"<p>Delete unnecessary data from <tt class=\"file-path\">/eniq/home</tt> FS as extension of <tt class=\"file-path\">/eniq/home</tt> is not\n" +
"supported and it is already optimized.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"<p>The following Parsing errors are seen</p><br />\n" +
"<p><tt class=\"output\">...is not supported in binary output mode</tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p>The parsing is because the JVM cache is not updated properly\n" +
"after upgrade. </p><br />\n" +
"<p>The JVM cache is holding some data related to the process for\n" +
"a complete session. So if the cache is not refreshed when the JVM\n" +
"is restarted/not restarted, then this error occurs</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p>Restart of ENIQ services is required to get parsing working\n" +
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
"<p>This issue is caused by the following:</p><br />\n" +
"<ul>\n" +
"<li><p> The Monitored Type may be active at that moment, but\n" +
"in <tt class=\"file-path\">Log_AggregationRules</tt> monitored type\n" +
"was not present. As a result when UpdateMonitoring is triggered, the\n" +
"set not updated the Log_AggregationStatus table. </p>\n" +
"</li>\n" +
"<li><p> During AutomaticAggregation we are checking the Status\n" +
"column of Log_AggregationStatus table, which should be either LOADED\n" +
"or BLOCKED, if the aggregation need to run.</p>\n" +
"</li>\n" +
"</ul>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p>Do the following to reaggregate data: </p><br />\n" +
"<p>In AdminUI, click Reaggregation. </p><br />\n" +
"<p>Select the time level, start time, end time, and tech pack. </p><br />\n" +
"<p>Click List to get a list of measurement types to be reaggregated.\n" +
"Select the measurement types to be reaggregated and click Aggregate.</p><br />\n" +
"<p>  To run AutomaticREAggregation: In Adminui, click ETLC Set Scheduling.\n" +
"Select set type as 'Maintenance' and Package as 'DWH_MONITOR'. Click\n" +
"start AutomaticREAggregation.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"<p>After init 6, File parsing and Loading stopped.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p>ENIQ services did not start in proper order after init\n" +
"6.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p>Restart the ENIQ services. See <a href='javascript:parent.parent.parent.showAnchor(\"restartEniqServices\")' class=\"xref\">Section  12.7</a>.</p>\n" +
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
"<p>If the LTE Ran Load Expert (LLE) feature is not installed\n" +
"in the ENIQ server, then the following aggregators have no data to\n" +
"calculate. As a result, the aggregators go to Failed Dependency.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p>To avoid this issue the LLE license needs to be <strong class=\"MEDEMPH\">ACTIVE</strong> and the LLE techpack must be installed. </p><br />\n" +
"<p>The feature should be configured using the files <tt class=\"file-path\">AcceptableRates</tt>, <tt class=\"file-path\">backOffCell</tt>, <tt class=\"file-path\">backOffErbs</tt>. For details of configuring\n" +
"these files refer to <a href='javascript:parent.parent.parent.showAnchor(\"LTERANLoadExpert\")' class=\"xref\">Reference [25]</a> </p><br />\n" +
"<p><strong class=\"MEDEMPH\">Note : </strong> The busy hour configuration\n" +
"is only required for LTE Ran Load Expert feature. It is an optional\n" +
"feature. It has no impact on other functionality. If not interested\n" +
"ignore the aggregation status of the following aggregators.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"<p>DC_E_ERBSG2_CELLFDDBH_RANKBH_CELLFDD_PP1...PP3,PP10 &amp;\n" +
"PP11</p><br />\n" +
"<p>DC_E_ERBSG2_EUTRANCELLFDD_V_DAYBH_CELLFDD</p><br />\n" +
"<p>DC_E_ERBSG2_EUTRANCELLFDD_DAYBH_CELLFDD</p><br />\n" +
"<p>DC_E_ERBSG2_DUBH_RANKBH_DU_PP0.. to ..PP8</p><br />\n" +
"<p>DC_E_ERBSG2_EUTRANCELLFDD_V_DAYBH_DU</p><br />\n" +
"<p>DC_E_ERBSG2_BBPROCESSINGRESOURCE_V_DAYBH_DU</p><br />\n" +
"<p>DC_E_ERBSG2_ENODEBFUNCTION_DAYBH_DU</p><br />\n" +
"<p>DC_E_ ERBSG2_ERBSBH_RANKBH_ERBS_PP0</p><br />\n" +
"<p>DC_E_ ERBSG2_ENODEBFUNCTION_V_DAYBH_ERBS</p><br />\n" +
"<p>DC_E_ERBSG2_PROCESSORLOADBH_RANKBH_PROCESSORLOAD_PP0</p><br />\n" +
"<p>DC_E_ERBSG2_MPPROCESSINGRESOURCE_V_DAYBH_PROCESSORLOAD</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p>If the LTE Ran Load Expert (LLE) feature is not installed\n" +
"in the ENIQ server, then the following aggregators have no data to\n" +
"calculate. As a result, the aggregators go to Failed Dependency.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p>To avoid this issue the LLE license needs to be <strong class=\"MEDEMPH\">ACTIVE</strong> and the LLE techpack must be installed. </p><br />\n" +
"<p>The feature should be configured using the files <tt class=\"file-path\">AcceptableRates</tt>, <tt class=\"file-path\">backOffCell</tt>, <tt class=\"file-path\">backOffErbs</tt>. For details of configuring\n" +
"these files refer to <a href='javascript:parent.parent.parent.showAnchor(\"LTERANLoadExpert\")' class=\"xref\">Reference [25]</a> </p><br />\n" +
"<p><strong class=\"MEDEMPH\">Note : </strong> The busy hour configuration\n" +
"is only required for LTE Ran Load Expert feature. It is an optional\n" +
"feature. It has no impact on other functionality. If not interested\n" +
"ignore the aggregation status of the following aggregators.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"<p>Discrepancy in <tt class=\"file-path\">PERIOD_DURATION</tt> value between DAY and RAW tables</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p>The <tt class=\"file-path\">PERIOD_DURATION</tt> values for\n" +
"DAY tables are fetched from <tt class=\"file-path\">DIM_TIMELEVEL</tt>. </p><br />\n" +
"<p>In <tt class=\"file-path\">DIM_TIMELEVEL</tt>, for column <tt class=\"file-path\">TABLELEVEL=DAY</tt> , the <tt class=\"file-path\">DURATIONMIN</tt> value is set to default value as 1440.</p>\n" +
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
"<p>Example of a DIM table for a VECTOR measurement: DIM_E_RBS_HSDSCHRES_V_pmRemainingResourceCheck</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p>When upgrading or installing a TP DWHManager creates some <tt class=\"file-path\">.sql</tt> files in the <tt class=\"file-path\">eniq/upgrade/install</tt> directory of the Coordinator Blade which in turn is loaded into\n" +
"the DB to load the data for the Vector DIM tables. </p><br />\n" +
"<p>If the <tt class=\"file-path\">.sql</tt> files are not created properly\n" +
"the Vector DIM tables remain empty. </p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p>If a VECTOR DIM table is empty, then there is a set &ndash;\n" +
"DWHMStorageTimeUpdate, which when run, updates the tables correctly.\n" +
"Go to ADMINUI -&gt; ETLC Set Scheduling. </p><br />\n" +
"<p>Select the &ldquo;Set type&rdquo; as &ldquo;Techpack&rdquo; and\n" +
"&ldquo;Package&rdquo; as the TP for which the VECTOR DIM table data\n" +
"is missing. Then run the corresponding &ldquo;DWHM_StorageTimeUpdate&rdquo;\n" +
"set for the TP.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"<p>ENIQ Reports are showing wrong value for the no of Counters\n" +
"in case of Partial Parsing and Loading.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p>The formula used to calculate the No of Counter in Partial\n" +
"Loading and Parsing is giving wrong value.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p>In the reports, the formula used to calculate the number\n" +
"of counters for partial parsing and partial loading does not provide\n" +
"the correct result.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"<p>As a reference to <b class=\"object\">HS50857</b> issue we found\n" +
"that few Loaders stuck in priority queue and not moving to execution\n" +
"slot for around 3 ROPs.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p>Alarm interface was also scheduled with 168 reports then\n" +
"which is a possible reason for the issue. Alarm reports are disabled\n" +
"and issue not seen again .</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p>Disable the reports or reduced the no of reports to be\n" +
"fetched at a time to 5-10.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"<p>System degradation because of NULLs coming in the topology\n" +
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
"<p> 3)      Check the test.txt file to see how many tables have NULL\n" +
"values.</p><br />\n" +
"<p><strong class=\"MEDEMPH\">Usage of the script to delete the NULLs (from dcuser/dcuser)</strong> -:</p><br />\n" +
"<p> 1)      chmod 775 Topo_Null_Check.bsh </p><br />\n" +
"<p> 2)      bash ./Topo_Null_Check.bsh &ndash;fix</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"<p>Engine loading stops frequently because of the engine stoppage\n" +
"happens frequently. </p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p>This is because the JVM cache is not updated properly after\n" +
"upgrade which results in loading error. The JVM cache is holding some\n" +
"data related to the process for a complete session. So if the cache\n" +
"is not refreshed when the JVM is restarted/not restarted, then the\n" +
"said error occurs</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p>Restart of ENIQ services is required to get Loading working\n" +
"again.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"<p>While defining the Monitoring rules, if MINSOURCE value\n" +
"is greater than the sourcecount , it will labels the data of the respective\n" +
"ROP as CHECK_FAILED.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p>ENIQ expects <tt class=\"file-path\">Minsource</tt> to be\n" +
"less than <tt class=\"file-path\">Sourcecount</tt> to have incoming\n" +
"data marked as loaded. </p><br />\n" +
"<p>If the mentioned condition FALSE, ENIQ marks the data as CHECK_FAILED\n" +
". And as according to the Normal ENIQ behavior the data is picked\n" +
"up during the Re-Aggregation time, which runs around at 19:00 hrs\n" +
"daily.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p>Before applying any monitoring rule, check the sourcecount\n" +
"value from ShowLoadings in Adminui first then define the monitoring\n" +
"rule on respective Techpack. MinSource value should be configured\n" +
"less than SourceCount value.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"<p>Normally, during NAS fail over or SFS/VA Reboot, the ENIQ\n" +
"services are automatically restarted and do not require manual intervention.\n" +
"In rare cases the ENIQ services could be in a continuous maintenance\n" +
"state</p><br />\n" +
"<p>See <a href='javascript:parent.parent.parent.showAnchor(\"EniqServices\")' class=\"xref\">Section  34.2.2</a>.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p>Symantec Fault</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p>Ensure that the following services are online:</p><br />\n" +
"<p><tt class=\"input\"><b>svc:/storage/NASd:default</b></tt></p><br />\n" +
"<p><tt class=\"input\"><b>svc:/milestone/NAS-online:default</b></tt> </p><br />\n" +
"<p><tt class=\"input\"><b>svc:/licensing/sentinel:default</b></tt> </p><br />\n" +
"<p>Once these are online, then services should recover themselves.</p><br />\n" +
"<p> If services remain offline/maintenance, reboot the server by\n" +
"running <tt class=\"input\"><b>init 6</b></tt> in the ENIQ Statistics (standalone)\n" +
"server. </p><br />\n" +
"<p>For a Multi-blade Run <tt class=\"input\"><b>init 6</b></tt> in the below order.</p><br />\n" +
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
"<p>Then restart the services as mentioned in the section <a href='javascript:parent.parent.parent.showAnchor(\"restartEniqServices\")' class=\"xref\">Section  12.7</a></p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"<p>ENIQ FS is in read-only mode</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p> If any ENIQ FS on SFS/VA reaches the defined threshold\n" +
"limit (depends on the configuration), then the FS is in read-only\n" +
"mode and ENIQ functionality is lost.</p><br />\n" +
"<p> All ENIQ services are down until FS can be cleaned up. After\n" +
"cleaned up the FS, the services can be restarted and functionality\n" +
"will be recovered.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p>To clear unwanted files, follow the below steps</p><br />\n" +
"<p>1) logon the NAS console and cleanup the unwanted files </p><br />\n" +
"<p><tt class=\"input\"><b>Note</b></tt>  : You need to logon to NAS directly as support\n" +
"user and clean up the problem directory on the NAS server itself.</p><br />\n" +
"<p>2) Restart NAS service. See section <a href='javascript:parent.parent.parent.showAnchor(\"NasdRestart\")' class=\"xref\">Section  11.2.2</a></p><br />\n" +
"<p><tt class=\"input\"><b>Note</b></tt> : NAS service has dependency with Sentinel\n" +
"service, which will also needs to be in operational state.</p><br />\n" +
"<p>3)Restart all the ENIQ services. See section <a href='javascript:parent.parent.parent.showAnchor(\"restartall\")' class=\"xref\">Section  34.6.3</a></p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"<p>Symbolic links are not deleted even though the file parsing\n" +
"has happened correctly. While loading, the following warning message\n" +
"gets printed in the Loader Engine log of the particular TP <tt class=\"output\">Move with memory copy failed, deleting the temporary file</tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p>While loading, the loader picks up the temporary ASCII\n" +
"or binary file from the output file path as defined in the TP interface.\n" +
"But if the TP cache is not refreshed correctly, then the temporary\n" +
"file does not get created correctly in the correct path leading to\n" +
"the warning message.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p>Refresh the TP cache by reactivating the interface by following\n" +
"the below process. For the MB server, run the below steps from the\n" +
"coordinator blade as <tt class=\"input\"><b>dcuser</b></tt>.</p><br />\n" +
"<p><tt class=\"input\"><b>cd /eniq/sw/installer</b></tt></p><br />\n" +
"<p>Run the command as <tt class=\"input\"><b>./activate_interface -o <i class=\"var\">&lt;OSS_NAME&gt;</i> -i <i class=\"var\">&lt;interface name&gt;</i></b></tt></p><br />\n" +
"<p> Example: <tt class=\"input\"><b>./activate_interface -o eniq_oss_1 -i INTF_DC_E_BULK_CM</b></tt></p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"<p>NAS-online service is disabled and tries to enable automatically,\n" +
"then all services get restarted. But engine behaves abnormally. As\n" +
"a result no data loaded in dwhdb until ENGINE was restarted manually.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p>After the NAS comes online, when engine is started prior\n" +
"to repdb, then it waits until repdb comes online. Once repdb is online,\n" +
"Engine starts loading all the transformations before continuing with\n" +
"its other tasks. But in this case the transformations did not get\n" +
"refreshed because of which loading problems started to occur.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p>If transformations do not get updated when engine restarts\n" +
"after NAS comes online, then engine needs to be restarted again to\n" +
"update the transformations correctly.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"<p><b class=\"object\">::java.lang.ExceptionInInitializationError</b> error while launching techpackide/universeupdateinstaller</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p>There is jar signing issue from 16B as all the packages\n" +
"are unsigned. </p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p><b class=\"object\">java.policy</b> file in JAVA installed path\n" +
"should be updated with the below lines.<br /> To launch universeupdateinstaller/techpackide\n" +
"package when delivered as unsigned packages<br /><b class=\"object\"> grant { permission\n" +
"java.security.AllPermission;  };</b> <br />For example : C:\\Program\n" +
"Files\\Java\\jre7\\lib\\security\\java.policy </p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"<p>If the security settings have blocked an application from\n" +
"running with an out-of-date or expired version of java</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p>security settings</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p>Change the java security level in control panel from high\n" +
"to medium.<br /> Go to control panel --&gt;Java --&gt; security table --&gt;\n" +
"select Medium --&gt; apply.It is applicable for both EBSMANAGER and Techpackide</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"<p> IE9 32 bit is not supported for ebs manager and techpackide</p><br />\n" +
"<p>Error:<br />java.lang.Unsupported VersionError:<br />com/ericsson/eniq/techpachsdk/TechPackIDE:<br />unsupported major.minor version 51.0</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p>IE9 32 bit is using only java 32 bit</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p>For IE there are two browsers. One using 32 bit and the\n" +
"other one is using 64 bit<br />Use only 64-bit IE browsers so that\n" +
"it use only 64 bit JRE</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"<p>While refreshing Downlink Code tree Load(Busy hour) Report\n" +
"query getting the Max_join_enumeration error</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p>The query is too complex and volume of data is too large.\n" +
"This query contains 24 joins which exceed the Sybase limitation. Because\n" +
"of that the MaX_Join_Enumaration_Error has been thrown while refreshing\n" +
"the query</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p>To avoid Max_join_Enumeration ERROR there is a WorkAround\n" +
"available which is to increase the sybase limit to 25 by using below\n" +
"command.</p><br />\n" ;

var C34_2P1=
"<p> <tt class=\"input\"><b> # iqisql Udba Psql Sdwhdb</b></tt></p><br />\n" +
"<p> 1&gt; set option public.Max_Join_Enumeration =25 </p><br />\n" +
"<p> 2&gt; go </p><br />\n" +
"<p> ENIQ User (<tt class=\"file-path\">dcuser</tt>) have to follow\n" +
"the above step by logging in to the DB. No server restart required\n" +
"for the above activity.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"<p>During initial install below warning can be seen in engine\n" +
"logs-</p><br />\n" +
"<p><tt class=\"file-path\">WARNING org.apache.velocity : Could not create file\n" +
"appender 'velocity.log'    java.io.FileNotFoundException: velocity.log\n" +
"(No such file or directory)</tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p>During Initial Install <strong class=\"MEDEMPH\">DWHMonitor</strong> is throwing\n" +
"the <strong class=\"MEDEMPH\">FileNotFoundException</strong>.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p>This does not have any impact, so can be ignored.</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"<p>Data loading into the database tables stops with the following\n" +
"information message in the log file, &lt;Blade_Host_Name&gt;_status_log[_#&lt;number&gt;@&lt;Timestamp&gt;]\n" +
"present at /eniq/log/cleanup_log:<br /><br />INFO:: Partition size limit\n" +
"exceeded for below filesystems:<br />&lt;FileSystemName&gt;<br /><br /> Setting\n" +
"engine to no loads.<br /><br />where, <br />[_#&lt;number&gt;@&lt;Timestamp&gt;]\n" +
"is optional <br />&lt;FileSystemName&gt; is the name of the filesystem\n" +
"whose utilization has exceeded the threshold limit.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p>If the utilization of ZFS / NAS file system reaches more\n" +
"than 90% or utilization of Root file system reaches more than 95%\n" +
"on any of the blades of the ENIQ Stats server, engine profile changes\n" +
"to NoLoads at 1 AM.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p>Clean up the directory for which the file system utilization\n" +
"is more than 90% (for Root file system, threshold is 95%). Once the\n" +
"ZFS / NAS file system utilization is less than 90% and Root file system\n" +
"utilization is less than 95%, perform the following steps: <br /><br />Log on to ENIQ Stats Coordinator server as dcuser . <br />Use the\n" +
"following command:<br /> # engine -e getCurrentProfile <br />It should\n" +
"appear as NoLoads . <br />To set engine profile to Normal , use the\n" +
"command: <br /># engine -e changeProfile Normal <br />Verify the current\n" +
"profile using the following command: <br /># engine -e getCurrentProfile <br />It should appear as Normal .</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"<p>\"<strong class=\"MEDEMPH\">04:00:40 SEVERE 1.Aggregator Error while initializing\n" +
"aggregation due to an SQL exception JZ006: Caught IOException: java.net.SocketTimeoutException:\n" +
"Read timed out use getCause() to see the error chain</strong>\"Error\n" +
"seen during aggregation.</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p>In eniq/sw/conf, static.properties <em><strong class=\"HIGHEMPH\">statsdefaultQueryTimeOutInMins</strong></em> value is set to <strong class=\"MEDEMPH\">300minutes,</strong>which means that any query\n" +
"can run up to a max of 300min (5 hrs) for completing its execution</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p>The value of <em><strong class=\"HIGHEMPH\">statsdefaultQueryTimeOutInMins</strong></em> can be increased to higher value in multiples of <strong class=\"MEDEMPH\">60 minutes</strong> to avoid the aggregators from failing with timeout exception. After\n" +
"updating the value restart all the services.</p><br />\n" +
"<p> Login to the server as dcuser  : <tt class=\"file-path\"># su &ndash; dcuser</tt><br />1&gt;Increase the value of the parameter <em><strong class=\"HIGHEMPH\">statsdefaultQueryTimeOutInMins</strong></em> in static.properties under /eniq/sw/conf<br />2&gt;Restart all the services <br /><tt class=\"file-path\"># cd /eniq/admin/bin</tt><br /><tt class=\"file-path\"># bash ./manage_deployment_services.bsh -a restart -s ALL</tt></p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"25%\">\n" +
"<p>\"ERROR:All IQ large memory has been used, allocation cancelled\"\n" +
"seen in /eniq/local_logs/iq/dwhdb.iqmsg</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"20%\">\n" +
"<p>This error message can be thrown if the customized queries\n" +
"are trying to fetch huge amount of data, which in-turn may consume\n" +
"significant IQ memory. </p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"55%\">\n" +
"<p>Customized queries should be used optimally to avoid out\n" +
"of memory issue. </p>\n" +
"</td></tr></table></td></tr></table>\n" +
"\n"+
"</div>\n";

var C34_2_1=
"<div id=\"content\" class=\"body-content\">\n"+
"<h3><a name=\"DailyAggregationNot\"></a><span class=\"CHAPNUMBER\">34.2.1 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_tm1d\"></a><a name=\"CHAPTER34.2.1\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Daily Aggregation\n" +
"is Happening Very Late</a></span></h3>\n" +
"\n" +
"<p class=\"titled-block\">Cause</p><p>\n" +
"ENIQ triggers the aggregation for incomplete data only if last\n" +
"loading of data happened 1.5 hours before</p>\n" +
"<p>\n" +
"If the data loading pattern is late and continuous without having\n" +
"a 1.5 hour gap between two consecutive loadings, then ENIQ does not\n" +
"trigger initial aggregation.</p>\n" +
"\n" +
"<p class=\"titled-block\">Action</p><p>\n" +
"Do the following to trigger an initial aggregation at a fixed time\n" +
"with the available data (the data loaded by that time) for a particular\n" +
"techpack:</p>\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Log in ENIQ server or in a multi-blade deployment on the\n" +
"engine blade as <tt class=\"file-path\">dcuser</tt>.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Edit the below file <br /><tt class=\"input\"><b>/eniq/sw/conf/static.properties</b></tt></li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Add below entries to the static.properties and save it. <br /><tt class=\"input\"><b>AggregationStartTP=<i class=\"var\">&lt;tp_name&gt;</i></b></tt><br /><tt class=\"input\"><b>aggStartTime=<i class=\"var\">&lt;HH:mm:ss&gt;</i></b></tt></li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Change profile to NoLoads:<br /> <tt class=\"input\"><b>engine -e changeProfile\n" +
"NoLoads</b></tt></li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Reload engine configurations:<br /> <tt class=\"input\"><b>engine -e reloadConfig</b></tt></li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Change the profile back to normal:<br /> <tt class=\"input\"><b>engine\n" +
"-e changeProfile Normal</b></tt></li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Example:<p>\n" +
" If static.properties has below entries</p>\n" +
"<p>\n" +
"<tt class=\"input\"><b>AggregationStartTP=DC_E_ERBS</b></tt> <br /> <tt class=\"input\"><b>aggStartTime=04:00:00</b></tt></p>\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">The parameters must not contain spaces(DC_E_ERBS, 04:00:00).\n" +
"ENIQ triggers the initial aggregation for DC_E_ERBS techpack around\n" +
"04:00:00 am with the available data (if aggregation is not started\n" +
"by that time).</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">This feature works only with the techpack defined in\n" +
"static.properties. For others, aggregation works in the normal way</li></ul></dd></dl><br /></li></ol>\n" +
"\n"+
"</div>\n";

var C34_2_2=
"<div id=\"content\" class=\"body-content\">\n"+
"<h3><a name=\"EniqServices\"></a><span class=\"CHAPNUMBER\">34.2.2 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_9kz6\"></a><a name=\"CHAPTER34.2.2\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">ENIQ Services\n" +
"in a Continuous Maintenance State during NAS Failover or SFS/VA Reboot</a></span></h3>\n" +
"\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>Normally, during a NAS fail over or an SFS/VA reboot, the\n" +
"ENIQ services are automatically restarted and do not require manual\n" +
"intervention. <p>\n" +
"In rare circumstances the ENIQ services may have continuous maintenance\n" +
"state.</p>\n" +
"</dd></dl><br />\n" +
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

var C34_3=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">34.3 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_akax\"></a><a name=\"CHAPTER34.3\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Troubleshooting Licensing</a></span></h2>\n" +
"\n" +
"<p>\n" +
"If you cannot use the license manager to view ENIQ licenses or\n" +
"features, check the following:</p>\n" +
"\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">A valid license exists for the feature.</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">For a multi-blade-deployment ensure that License manager\n" +
"is running on the coordinator blade.</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\"><tt class=\"LITERALMONO\">/eniq/sentinel/etc/sentinel.env</tt> host\n" +
"has the correct IP address for the <tt class=\"file-path\">LSHOST</tt> variable.</li></ul>\n" +
"<p>\n" +
"If licensing still does not work as planned, inspect the licensing\n" +
"log files for more information.</p>\n" +
"\n" +
"<p>\n" +
"If licenses cannot be retrieved from the license server, the license\n" +
"manager appends an error code to the error message. The error codes\n" +
"are explained in <a name=\"tbl-licerrors\" href='javascript:parent.parent.parent.showAnchor(\"errorcodetable\")' class=\"xref\"> Table 24</a><a name=\"errorcodetable\"></a>.</p>\n" +
"\n" +
"\n" +
"<a name=\"TABLE24\"></a>\n" +
"<table class=\"tblcnt\" width=\"100%\">\n" +
"<caption>Table 24 &nbsp;&nbsp; License Manager Error Codes</caption>\n" +
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
"<p>LSUpdate failed; license expired because of a time-out.</p>\n" +
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
"<p>License server not responding because of unknown condition.</p>\n" +
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
"and it is not clear from the operation which licenses the requestor\n" +
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
"<p>The user is not authorized to do this operation.</p>\n" +
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
"<p>Most of the license servers in the redundant license server\n" +
"pool are not running.</p>\n" +
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

var C34_4=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><a name=\"troublepartition\"></a><span class=\"CHAPNUMBER\">34.4 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_dhed\"></a><a name=\"CHAPTER34.4\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Troubleshooting\n" +
"Partition Issues</a></span></h2>\n" +
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

var C34_4_1=
"<div id=\"content\" class=\"body-content\">\n"+
"<h3><span class=\"CHAPNUMBER\">34.4.1 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_d3qb\"></a><a name=\"CHAPTER34.4.1\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Solving Insane Partition Issues</a></span></h3>\n" +
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
"definition i.e. there is a column name mismatch between SYSCOLUMNS\n" +
"and DWHCOLUMN<p>\n" +
"Following steps can be executed as part of workaround -</p>\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE3\">Login as dcuser</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE3\">Login to repdb as dwhrep (Coordinator in case of Multi\n" +
"Blade)<p>\n" +
"<tt class=\"input\"><b>#iqisql -P<i class=\"var\">&lt;dwhrep_password&gt;</i> -Udwhrep -Srepdb\n" +
"-w200000</b></tt></p>\n" +
"<p>\n" +
"<tt class=\"input\"><b>1&gt;select * from DWHPARTITION where status='INSANE_AC'</b></tt></p>\n" +
"<p>\n" +
"<tt class=\"input\"><b>2&gt;go</b></tt></p>\n" +
"</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE3\">Capture the output in some file for future reference</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE3\">Follow the steps to run the script<p>\n" +
"<tt class=\"input\"><b># cd /eniq/sw/installer</b></tt></p>\n" +
"<p>\n" +
"<tt class=\"input\"><b># ./insane_WA.bsh</b></tt></p>\n" +
"</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE3\">Run DWHM_INSTALL set from Adminui for the corresponding\n" +
"TP</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE3\">Login to repdb as dwhrep<p>\n" +
"<tt class=\"input\"><b>#iqisql -P<i class=\"var\">&lt;dwhrep_password&gt;</i> -Udwhrep -Srepdb\n" +
"-w200000</b></tt></p>\n" +
"<p>\n" +
"<tt class=\"input\"><b>1&gt; select * from DWHPARTITION where status='INSANE_AC'</b></tt></p>\n" +
"<p>\n" +
"<tt class=\"input\"><b>2&gt;go</b></tt></p>\n" +
"</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE3\">Compare the result with previous one and there should\n" +
"not be any INSANE partition</li></ul></li></ul>\n" +
"\n" +
"\n"+
"</div>\n";

var C34_5=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">34.5 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_rnc0\"></a><a name=\"CHAPTER34.5\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">General Single Blade Information</a></span></h2>\n" +
"\n" +
"<p>\n" +
"To list ENIQ services that should be running on a single blade\n" +
"execute the following command as user <tt class=\"file-path\">root</tt></p>\n" +
"\n" +
"\n" +
"<p>\n" +
"For the single blade</p><ul>\n" +
"\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\"><tt class=\"input\"><b>grep eniq_stats /eniq/admin/etc/smf_contract_config\n" +
"|grep ENIQ |grep Y</b></tt></li></ul>\n" +
"<p>\n" +
" The following is the expected output of the command executed as\n" +
"user <tt class=\"file-path\">root</tt>.</p>\n" +
"\n" +
"<p>\n" +
" <tt class=\"input\"><b>svcs -a | grep eniq</b></tt></p>\n" +
"\n" +
"\n" +
"<table frame=\"void\" width=\"100%\" class=\"tblcnt\" rules=\"none\">\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\">\n" +
"<p>online/ disabled depending on current setup </p>\n" +
"</td>\n" +
"<td align=\"left\">\n" +
"<p>&lt;start date or time of service&gt;</p>\n" +
"</td>\n" +
"<td align=\"left\">\n" +
"<p>svc:/eniq/roll-snap:default</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\">\n" +
"<p>disabled</p>\n" +
"</td>\n" +
"<td align=\"left\">\n" +
"<p>&lt;start date or time of service&gt;</p>\n" +
"</td>\n" +
"<td align=\"left\">\n" +
"<p>svc:/eniq/dwh_reader:default</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\">\n" +
"<p>online</p>\n" +
"</td>\n" +
"<td align=\"left\">\n" +
"<p>&lt;start date or time of service&gt;</p>\n" +
"</td>\n" +
"<td align=\"left\">\n" +
"<p>svc:/eniq/esm:default</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\">\n" +
"<p>online</p>\n" +
"</td>\n" +
"<td align=\"left\">\n" +
"<p>&lt;start date or time of service&gt;</p>\n" +
"</td>\n" +
"<td align=\"left\">\n" +
"<p>svc:/eniq/rmiregistry:default</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\">\n" +
"<p>online</p>\n" +
"</td>\n" +
"<td align=\"left\">\n" +
"<p>&lt;start date or time of service&gt;</p>\n" +
"</td>\n" +
"<td align=\"left\">\n" +
"<p>svc:/eniq/licmgr:default</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\">\n" +
"<p>online</p>\n" +
"</td>\n" +
"<td align=\"left\">\n" +
"<p>&lt;start date or time of service&gt;</p>\n" +
"</td>\n" +
"<td align=\"left\">\n" +
"<p>svc:/eniq/connectd:default</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\">\n" +
"<p>online</p>\n" +
"</td>\n" +
"<td align=\"left\">\n" +
"<p>&lt;start date or time of service&gt;</p>\n" +
"</td>\n" +
"<td align=\"left\">\n" +
"<p> svc:/eniq/dwhdb:default</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\">\n" +
"<p>online</p>\n" +
"</td>\n" +
"<td align=\"left\">\n" +
"<p>&lt;start date or time of service&gt;</p>\n" +
"</td>\n" +
"<td align=\"left\">\n" +
"<p>svc:/eniq/scheduler:default</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\">\n" +
"<p>online</p>\n" +
"</td>\n" +
"<td align=\"left\">\n" +
"<p>&lt;start date or time of service&gt;</p>\n" +
"</td>\n" +
"<td align=\"left\">\n" +
"<p>svc:/eniq/sim:default</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\">\n" +
"<p>online</p>\n" +
"</td>\n" +
"<td align=\"left\">\n" +
"<p>&lt;start date or time of service&gt;</p>\n" +
"</td>\n" +
"<td align=\"left\">\n" +
"<p>svc:/eniq/repdb:default</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\">\n" +
"<p>online</p>\n" +
"</td>\n" +
"<td align=\"left\">\n" +
"<p>&lt;start date or time of service&gt;</p>\n" +
"</td>\n" +
"<td align=\"left\">\n" +
"<p>svc:/eniq/engine:default</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\">\n" +
"<p>online</p>\n" +
"</td>\n" +
"<td align=\"left\">\n" +
"<p>&lt;start date or time of service&gt;</p>\n" +
"</td>\n" +
"<td align=\"left\">\n" +
"<p>svc:/eniq/webserver:default</p>\n" +
"</td></tr></table>\n" +
"\n"+
"</div>\n";

var C34_6=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">34.6 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_24am\"></a><a name=\"CHAPTER34.6\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">General Multi-Blade Information</a></span></h2>\n" +
"\n" +
"<p>\n" +
"To list ENIQ services that should be running on any particular\n" +
"blade execute the following command as user <tt class=\"file-path\">root</tt>.</p>\n" +
"\n" +
"\n" +
"<p>\n" +
"For the coordinator blade</p><ul>\n" +
"\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\"><tt class=\"input\"><b>grep stats_coordinator /eniq/admin/etc/smf_contract_config\n" +
"|grep ENIQ |grep Y</b></tt></li></ul>\n" +
"\n" +
"<p>\n" +
"For the engine blade</p><ul>\n" +
"\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\"><tt class=\"input\"><b>grep stats_engine /eniq/admin/etc/smf_contract_config\n" +
"|grep ENIQ |grep Y</b></tt></li></ul>\n" +
"\n" +
"<p>\n" +
"For the reader blades</p><ul>\n" +
"\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\"><tt class=\"input\"><b>grep stats_iqr /eniq/admin/etc/smf_contract_config\n" +
"|grep ENIQ |grep Y</b></tt></li></ul>\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd><tt class=\"file-path\">dwh_reader_1</tt> is used by engine\n" +
"as a writer. Thus, ENIQ Statistics multi-blade server has two reader\n" +
"nodes (<tt class=\"file-path\">stats_iqr</tt>) , the first of which\n" +
"is referred to as the writer.</dd></dl><br />\n" +
"<p>\n" +
"Also while on the various blades the user sees varying output of\n" +
"the command because of different services executing on a particular\n" +
"blade. The following is the expected output of the command executed\n" +
"as user <tt class=\"file-path\">root</tt>.</p>\n" +
"\n" +
"<p><tt class=\"input\"><b>svcs -a | grep eniq</b></tt></p>\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">On the coordinator blade \n" +
"<table frame=\"void\" style=\"margin-top:4pt\" class=\"grd\" rules=\"none\" width=\"100%\">\n" +
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
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"38%\">\n" +
"<p>online</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"29%\">\n" +
"<p>&lt;start date or time of service&gt;</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"33%\">\n" +
"<p> svc:/eniq/sim:default</p>\n" +
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
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"38%\">\n" +
"<p>disabled</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"29%\">\n" +
"<p>&lt;start date or time of service&gt;</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"33%\">\n" +
"<p> svc:/eniq/sim:default</p>\n" +
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
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"38%\">\n" +
"<p>disabled</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"29%\">\n" +
"<p>&lt;start date or time of service&gt;</p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"33%\">\n" +
"<p> svc:/eniq/sim:default</p>\n" +
"</td></tr></table></li></ul>\n" +
"\n"+
"</div>\n";

var C34_6_1=
"<div id=\"content\" class=\"body-content\">\n"+
"<h3><a name=\"Stopall\"></a><span class=\"CHAPNUMBER\">34.6.1 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"stopall\"></a><a name=\"CHAPTER34.6.1\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Stopping All Services\n" +
"on All Blades</a></span></h3>\n" +
"\n" +
"<p>\n" +
"To stop all services on all the four blades, run the commands in\n" +
"the following order. Login as a <tt class=\"file-path\">root</tt>.</p>\n" +
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

var C34_6_2=
"<div id=\"content\" class=\"body-content\">\n"+
"<h3><a name=\"startall\"></a><span class=\"CHAPNUMBER\">34.6.2 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_hzlo\"></a><a name=\"CHAPTER34.6.2\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\"> Starting All\n" +
"Services on All Blades</a></span></h3>\n" +
"\n" +
"<p>\n" +
"To start all services on all the four blades, run the commands\n" +
"in the following order. Login as a <tt class=\"file-path\">root</tt>.</p>\n" +
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

var C34_6_3=
"<div id=\"content\" class=\"body-content\">\n"+
"<h3><a name=\"restartall\"></a><span class=\"CHAPNUMBER\">34.6.3 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_j3el\"></a><a name=\"CHAPTER34.6.3\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Restarting\n" +
"All Services on All Blades</a></span></h3>\n" +
"\n" +
"<p>\n" +
"To restart all services on all the four blades, run the commands\n" +
"in the following order. Login as a <tt class=\"file-path\">root</tt>.</p>\n" +
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
"in below sequence to start services. Login as a <tt class=\"file-path\">root</tt> .</p>\n" +
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

var C34_7=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><a name=\"dwhdbclear\"></a><span class=\"CHAPNUMBER\">34.7 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_gnm0\"></a><a name=\"CHAPTER34.7\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Clearing dwhdb\n" +
"Reader Services in Maintenance</a></span></h2>\n" +
"\n" +
"<p>\n" +
"In a multi-blade deployment if a service is offline for an extended\n" +
"period, SAP IQ excludes that service from the multiplex, which results\n" +
"in the excluded services having state <tt class=\"file-path\">Maintenance</tt>.</p>\n" +
"\n" +
"<p>\n" +
"Log on to the <tt class=\"file-path\">dwhdb</tt> database and check\n" +
"for nodes in the multiplex with status <tt class=\"file-path\">excluded</tt>: <br /><tt class=\"input\"><b>eniq_srv{dcuser} # <br />iqisql -U<i class=\"var\">&lt;database_username&gt;</i><br /> -P <i class=\"var\">&lt;database_password&gt;</i>-Sdwhdb  <br />1&gt; sp_iqmpxinfo <br />2&gt; go  </b></tt></p>\n" +
"\n" +
"<p>\n" +
"Then change their status to <tt class=\"LITERALMONO\">included</tt> :<br /><tt class=\"input\"><b>1&gt; ALTER MULTIPLEX SERVER  <i class=\"var\">&lt;server name&gt;</i>STATUS INCLUDED <br />2&gt; go <br />Example: <br />1&gt; ALTER MULTIPLEX SERVER  dwh_reader_1\n" +
"STATUS INCLUDED <br />2&gt; go </b></tt></p>\n" +
"\n" +
"<p>\n" +
"Then verify that all nodes in the multiplex have a status <tt class=\"file-path\">included</tt>:<br /><tt class=\"input\"><b>eniq_srv{dcuser} #<br /> iqisql\n" +
"-U <i class=\"var\">&lt;database_username&gt;</i> <br />-P <i class=\"var\">&lt;database_password&gt;</i>-Sdwhdb  <br />1&gt; sp_iqmpxinfo <br />2&gt; go  </b></tt> </p>\n" +
"\n" +
"\n"+
"</div>\n";

var C34_8=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">34.8 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_25rx\"></a><a name=\"CHAPTER34.8\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Monitoring ENIQ Statistics Services\n" +
"using SMF</a></span></h2>\n" +
"\n" +
"<p>\n" +
"Log in as <tt class=\"file-path\">root</tt> user and run the following\n" +
"command: <br /><tt class=\"input\"><b># /usr/bin/svcs | grep eniq</b></tt></p>\n" +
"\n" +
"<p>\n" +
"If any of the services are not online and need to be restarted,\n" +
"proceed with the following steps:</p>\n" +
"\n" +
"<p>\n" +
"To enable any ENIQ service, use the following command: <br /><tt class=\"input\"><b># /usr/sbin/svcadm enable <i class=\"var\">&lt;service_name&gt;</i></b></tt></p>\n" +
"\n" +
"<p>\n" +
"To disable any ENIQ service, use the following command: <br /><tt class=\"input\"><b># /usr/sbin/svcadm disable <i class=\"var\">&lt;service_name&gt;</i></b></tt></p>\n" +
"\n" +
"<p>\n" +
"If a service is in a maintenance state, it is necessary to clear\n" +
"the service:<br /> <tt class=\"input\"><b># /usr/sbin/svcadm clear <i class=\"var\">&lt;service_name&gt;</i></b></tt></p>\n" +
"\n" +
"\n"+
"</div>\n";

var C34_9=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">34.9 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_taow\"></a><a name=\"CHAPTER34.9\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Engine Startup Hanging</a></span></h2>\n" +
"\n" +
"<p>\n" +
"In a multi-blade deployment, if the <tt class=\"file-path\">engine</tt> startup is hanging and services are running properly on other nodes,\n" +
"run the following command on the <tt class=\"file-path\">engine</tt> node as user <tt class=\"file-path\">root</tt>:<br /><tt class=\"input\"><b> manage_eniq_services.bsh\n" +
"-a restart -s ALL</b></tt></p>\n" +
"\n" +
"<p>\n" +
"If above command hangs, then manually disable the RMI registry\n" +
"service and try again by executing below steps:<br /><tt class=\"input\"><b>1&gt; svcadm\n" +
"disable svc:/eniq/rmiregistry:default<br />2&gt; manage_eniq_services.bsh\n" +
"-a restart -s ALL</b></tt></p>\n" +
"\n" +
"<p>\n" +
"If the services on the other nodes have problems, then refer <tt class=\"input\"><b>Section 19.4.3</b></tt> to restart all services on the Multi-blade\n" +
"servers.</p>\n" +
"\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>This is an option for troubleshooting the case of Engine\n" +
"hanging (with no logging) when it is restarted.</dd></dl><br />\n" +
"\n"+
"</div>\n";

var C34_10=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">34.10 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_wv90\"></a><a name=\"CHAPTER34.10\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Rolling Snapshot Logs</a></span></h2>\n" +
"\n" +
"<p>\n" +
"There is an option to receive additional information to the rolling\n" +
"snapshot logfile, this information records that the Rolling Snapshot\n" +
"file indicator is <strong class=\"MEDEMPH\">not</strong> present on the system. To view\n" +
"this information enter the following command:</p>\n" +
"\n" +
"<p>\n" +
"<tt class=\"input\"><b># touch /eniq/sw/conf/.debug_rollsnap</b></tt></p>\n" +
"\n" +
"\n"+
"</div>\n";

var C34_11=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">34.11 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_n369\"></a><a name=\"CHAPTER34.11\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Hostsync Logs</a></span></h2>\n" +
"\n" +
"<p>\n" +
"There is an option to receive additional information to the <tt class=\"file-path\">hostsync</tt> log file. It reports an error when the EWM\n" +
"configuration files are <strong class=\"MEDEMPH\">not</strong> present on the system. To\n" +
"view this information enter the following command:</p>\n" +
"\n" +
"<p><tt class=\"input\"><b># touch /eniq/sw/conf/.debug_hostsync</b></tt></p>\n" +
"\n"+
"</div>\n";

var C34_12=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">34.12 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_tgof\"></a><a name=\"CHAPTER34.12\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Troubleshooting Snapshot Failures</a></span></h2>\n" +
"\n" +
"<p>\n" +
"If your snapshot fails and if either of the below error is displayed,\n" +
"then follow steps 1&ndash;3 before retrying the snapshot again. </p>\n" +
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
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>In Multi-blade deployment run all commands on the <tt class=\"file-path\">coordinator</tt> blade.</dd></dl><br />\n" +
"<p>\n" +
" Log in as <tt class=\"file-path\">root</tt> user and run the following\n" +
"commands:</p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Check the available diskspace on the backup partition.<p>\n" +
"<tt class=\"input\"><b>{root} #: df -k /eniq/backup/</b></tt></p>\n" +
"<p>\n" +
"Example output:</p>\n" +
"<p>\n" +
"<tt class=\"output\">Filesystem 									kbytes   	used   	avail   	 capacity\n" +
"  Mounted on</tt></p>\n" +
"<p>\n" +
"<tt class=\"output\">nas1:/vx/lte2979-backup  157286400  10832456 145310288\n" +
" 7%  		/eniq/backup</tt></p>\n" +
"<p>\n" +
"Note the available diskspace (avail) of backup partition = _____\n" +
"KB</p>\n" +
"</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Calculate the size of <tt class=\"file-path\">repdb</tt> database.<p>\n" +
"<tt class=\"input\"><b>(root) #: cd /eniq/database/rep_main</b></tt></p>\n" +
"<p>\n" +
"<tt class=\"input\"><b>{root} #: echo `ls -l repdb.db repdb.log| awk '{sum = sum\n" +
"+ $5} END {print sum}' ` / 1024 | bc</b></tt></p>\n" +
"<p>\n" +
"Example output:</p>\n" +
"<p>\n" +
"<tt class=\"file-path\">1669764</tt></p>\n" +
"<p>\n" +
"Note the size of the <tt class=\"file-path\">repdb</tt> database\n" +
"size = ______ KB</p>\n" +
"</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Compare the size of <tt class=\"file-path\">repdb</tt> database\n" +
"and amount of available diskspace on the backup partition.<p>\n" +
"  If the size of <tt class=\"file-path\">repdb</tt> database is greater\n" +
"than the available diskspace of backup partition then perform step\n" +
"3.1 otherwise there might be some other issue and need to contact\n" +
"the next level of support.</p>\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd><strong class=\"MEDEMPH\">Only perform the following steps during a Maintenance\n" +
"Window. This procedure involves database downtime.</strong></dd></dl><br /><p>\n" +
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
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>After successful completion of the above steps there is free\n" +
"space available for snapshot to proceed.</dd></dl><br /></li></ol>\n" +
"\n"+
"</div>\n";

var C34_13=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">34.13 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_2lkh\"></a><a name=\"CHAPTER34.13\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Deletion of Techpack</a></span></h2>\n" +
"\n" +
"<p>\n" +
"For Techpack installed in ENIQ which do not have an active node\n" +
"configured in the server, we provide the following feature to remove\n" +
"them, so that upgrade times are not affected.</p>\n" +
"\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">It is assumed that the deleted techpack will not be\n" +
"selected during subsequent upgrades (if the node remains not configured\n" +
"in the network)</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">Since solutions for ERBS nodes (DC_E_ERBS and DC_E_ERBSG2)\n" +
"and RBS nodes (DC_E_RBS and DC_E_RBSG2) are based on a combined view/universe\n" +
"solution and if user is deleting node of either ERBS or RBS, then\n" +
"the other node should also be deleted for the same.<p>\n" +
"<strong class=\"MEDEMPH\">Example -</strong> if user is deleting DC_E_ERBS then DC_E_ERBSG2\n" +
"should also be deleted </p>\n" +
"</li></ul></dd></dl><br />\n" +
"<p>\n" +
"To perform techpack deletion, run below commands as user <tt class=\"file-path\">dcuser</tt> on the following server types:</p>\n" +
"\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">ENIQ Statistics (standalone)</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Statistics Coordinator</li></ul>\n" +
"<p>\n" +
"To delete a techpack from ENIQ follow the below steps -</p>\n" +
"\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">Login to server as <tt class=\"file-path\">dcuser</tt> with SSH</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">Go to the following path -<p>\n" +
"<tt class=\"input\"><b># cd /eniq/sw/installer/</b></tt></p>\n" +
"</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">Run the below script <p>\n" +
"<tt class=\"input\"><b># ./remove_techpack.bsh <i class=\"var\">&lt;Techpack_Name&gt;</i></b></tt></p>\n" +
"<p>\n" +
"<strong class=\"MEDEMPH\">Example -</strong></p>\n" +
"<p>\n" +
"<tt class=\"input\"><b># ./remove_techpack.bsh DC_E_ERBS</b></tt></p>\n" +
"</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">If the techpack has some dependent techpacks a confirmation\n" +
"message like the following example will be shown to user -<p>\n" +
"<strong class=\"MEDEMPH\">Example -</strong></p>\n" +
"<p>\n" +
"<tt class=\"output\">Below Techpacks are dependent on DC_E_ERBS -</tt></p>\n" +
"<p>\n" +
"<tt class=\"output\">'DC_E_FFAX:((27))'</tt></p>\n" +
"<p>\n" +
"<tt class=\"output\">'DC_E_ERBSG2:((43))'</tt></p>\n" +
"<p>\n" +
"<tt class=\"output\">Are you sure you want to continue? (Y/N)</tt></p>\n" +
"</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">According to user's response script will continue further\n" +
"or exit.</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">Before deleting any data user will be asked for a final\n" +
"confirmation and the information which is going to be deleted will\n" +
"be displayed -<p>\n" +
"<strong class=\"MEDEMPH\">Example -</strong></p>\n" +
"<p>\n" +
"The following data will be deleted-</p>\n" +
"<p>\n" +
"<tt class=\"output\">1.	All DC/DIM/VECTOR tables and views created as part of\n" +
"Techpack</tt></p>\n" +
"<p>\n" +
"<tt class=\"output\">2.	All the metadata from REPDB related to Techpack</tt></p>\n" +
"<p>\n" +
"<tt class=\"output\">3.	Universe folder from the server</tt></p>\n" +
"<p>\n" +
"<tt class=\"output\">For more details about which all tables will be dropped\n" +
"see the below files-</tt></p>\n" +
"<p>\n" +
"<tt class=\"file-path\">/eniq/sw/installer/DIM_E_ERBS.csv</tt></p>\n" +
"<p>\n" +
"<tt class=\"file-path\">/eniq/sw/installer/DC_E_ERBS_VIEW.csv</tt></p>\n" +
"<p>\n" +
"<tt class=\"file-path\">/eniq/sw/installer/DC_E_ERBS.csv</tt></p>\n" +
"<p>\n" +
"<tt class=\"file-path\">/eniq/sw/installer/DIM_E_ERBS_V.csv</tt></p>\n" +
"<p>\n" +
"<tt class=\"file-path\">/eniq/sw/installer/DC_E_ERBS_VIEW_EXT.csv</tt></p>\n" +
"<p>\n" +
"<tt class=\"output\">Changes are permanent and cannot be undone.</tt></p>\n" +
"<p>\n" +
"<tt class=\"output\">Please confirm removal of Techpack DC_E_ERBS from ENIQ</tt></p>\n" +
"<p>\n" +
"<tt class=\"output\">Are you sure you want to continue? (Y/N)</tt></p>\n" +
"</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">According to user's response script will continue further\n" +
"or exit.</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">Log file path : <tt class=\"file-path\">/eniq/log/sw_log/tp_installer/remove_techpack_&lt;Techpach_Name&gt;.log</tt><p>\n" +
"<strong class=\"MEDEMPH\">Example -</strong> <tt class=\"file-path\">/eniq/log/sw_log/tp_installer/remove_techpack_DC_E_ERBS.log</tt></p>\n" +
"</li></ul>\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">This script is only for deleting the PM techpacks which\n" +
"are installed unintentionally in the server.</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">The techpack will not be deleted if it has any data\n" +
"in <tt class=\"file-path\">dwhdb</tt> tables.</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">If a user has deleted any Techpack then the report and\n" +
"universe, if present in the BO server for that particular Techpack,\n" +
"will not work.</li></ul></dd></dl><br />\n" +
"\n" +
"\n"+
"</div>\n";

