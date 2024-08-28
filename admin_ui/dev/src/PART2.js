var C2=
"<div id=\"content\" class=\"body-content\">\n"+
"<h1><span class=\"CHAPNUMBER\">2 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_8z9p\"></a><a name=\"CHAPTER2\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">System Overview</a></span></h1>\n" +
"\n" +
"<p>\n" +
"The reference architecture for Ericsson Network IQ is shown in <a href='javascript:parent.parent.parent.showAnchor(\"RefArchitecture\")' class=\"xref\"> Figure 1</a>.</p>\n" +
"\n" +
"<div style=\"margin-top: 12pt\"><a name=\"RefArchitecture\"></a><p align=\"left\" class=\"image\"><a name=\"FIGURE1\"><img src=\"4_1543-CSA11363_1Uen.X5-ENIQ_Reference_Architecture.jpg\" title=\"\" border=\"0\" width=\"528\" height=\"373\" class=\"tab0\" /></a></p><p align=\"left\"><i>Figure 1 &nbsp; Ericsson Network IQ Reference System Architecture</i></p></div>\n" +
"\n"+
"</div>\n";

var C2_1=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">2.1 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_lxc7\"></a><a name=\"CHAPTER2.1\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">ENIQ Statistics Deployments</a></span></h2>\n" +
"\n" +
"<p>\n" +
"ENIQ Statistics has the following supported deployments:</p>\n" +
"\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">x86 Rack</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">x86 blade</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">x86 multi blade</li></ul>\n" +
"\n"+
"</div>\n";

var C2_1_1=
"<div id=\"content\" class=\"body-content\">\n"+
"<h3><span class=\"CHAPNUMBER\">2.1.1 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_ilxa\"></a><a name=\"CHAPTER2.1.1\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">ENIQ Statistics blade Deployment</a></span></h3>\n" +
"\n" +
"<p>\n" +
"The Network Analytics Server (NetAnServer) is an optional server\n" +
"in ENIQ Statistics blade deployment. For more information on NetAnServer\n" +
"deployment, see <a href='javascript:parent.parent.parent.showAnchor(\"NetworkAnalyticserver\")' class=\"xref\">Reference [3]</a> </p>\n" +
"\n" +
"\n"+
"</div>\n";

var C2_1_2=
"<div id=\"content\" class=\"body-content\">\n"+
"<h3><span class=\"CHAPNUMBER\">2.1.2 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_rfnv\"></a><a name=\"CHAPTER2.1.2\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">ENIQ Statistics multi blade deployment</a></span></h3>\n" +
"\n" +
"<p>\n" +
"The ENIQ Statistics multi blade deployment has 4 blades (blade\n" +
"name in brackets):</p>\n" +
"\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">ENIQ Engine (<tt class=\"file-path\">engine</tt>): executes\n" +
"parsing and loading, mounts directories from OSS</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">SAP IQ Coordinator (<tt class=\"file-path\">coordinator</tt>): coordinates database activity</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">SAP IQ Writer (<tt class=\"file-path\">dwh_reader_1</tt>): writes the data to the database</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">SAP IQ Reader (<tt class=\"file-path\">dwh_reader_2</tt>): used to handle database queries; should be used for any ODBC connection\n" +
"that will query the database (through BusinessObjects /Network Analytics\n" +
"Server / the SQL Interface)<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd> The Network Analytics Server is an Optional Server .</dd></dl><br /></li></ul>\n" +
"\n" +
"\n"+
"</div>\n";

var C2_2=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">2.2 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_g0tu\"></a><a name=\"CHAPTER2.2\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Data flow on Ericsson Network IQ</a></span></h2>\n" +
"\n" +
"<p>\n" +
"Raw measurement data is produced by OSS-RC and is delivered to\n" +
"the <b>pmdata</b> directories on Ericsson Network IQ Server\n" +
"or in the case of a multi blade deployment on the engine blade. This\n" +
"delivery is handled by mediators, OSS-RC, or Ericsson Network IQ (ENIQ)\n" +
"mediation actions.</p>\n" +
"\n" +
"<p>\n" +
"ENIQ Server or in the case of a multi blade deployment on the engine\n" +
"blade performs ETL (Extract, Transform, and Load) on raw data. After\n" +
"ETL processing, the data is available on the DWH database in the case\n" +
"of a multi blade deployment on the dwh_reader_2 blade. ENIQ Server\n" +
"also performs aggregations and busy hour ranking for data. Administrative\n" +
"client, AdminUI, is hosted by ENIQ Server or in the case of a multi\n" +
"blade deployment on the coordinator blade.</p>\n" +
"\n" +
"<p>\n" +
"ENIQ Business Intelligence Server provides the end-user interface\n" +
"Ericsson Network IQ Web Portal, for accessing data using reports.</p>\n" +
"\n" +
"<p>\n" +
"See <a href='javascript:parent.parent.parent.showAnchor(\"DataFlow\")' class=\"xref\"> Figure 2</a>.</p>\n" +
"\n" +
"<div style=\"margin-top: 12pt\"><a name=\"DataFlow\"></a><p align=\"left\" class=\"image\"><a name=\"FIGURE2\"><img src=\"4_1543-CSA11363_1Uen.X5-dataflow.gif\" title=\"\" border=\"0\" width=\"550\" height=\"338\" class=\"tab0\" /></a></p><p align=\"left\"><i>Figure 2 &nbsp; Data Flow</i></p></div>\n" +
"\n"+
"</div>\n";

var C2_3=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">2.3 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_7kzh\"></a><a name=\"CHAPTER2.3\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Ericsson Network IQ directory structure</a></span></h2>\n" +
"\n" +
"<p>\n" +
"Ericsson Network IQ has a standard directory structure. The common\n" +
"directories of Ericsson Network IQ and their content are outlined\n" +
"in <a name=\"TableforDirectoryStructure\" href='javascript:parent.parent.parent.showAnchor(\"TableforDirectoryStructure\")' class=\"xref\"> Table 1</a>.</p>\n" +
"\n" +
"<a name=\"TABLE1\"></a>\n" +
"<table class=\"tblcnt\" width=\"100%\">\n" +
"<caption>Table 1 &nbsp;&nbsp; Directory Structure</caption>\n" +
"<tr valign=\"top\">\n" +
"<th align=\"left\" valign=\"top\" width=\"50%\">\n" +
"\n" +
"<p><b class=\"object\">Directory</b></p></th>\n" +
"<th align=\"left\" valign=\"top\" width=\"50%\">\n" +
"\n" +
"<p><b class=\"object\">Contains</b></p></th></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"50%\">\n" +
"<p><tt class=\"file-path\">/eniq/data/pmdata</tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"50%\">\n" +
"<p>Links to PM files</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"50%\">\n" +
"<p><tt class=\"file-path\">/eniq/data/pmdata_wifi</tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"50%\">\n" +
"<p>Links to PM files from WIFI node</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"50%\">\n" +
"<p><tt class=\"file-path\">/eniq/data/etldata_XX</tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"50%\">\n" +
"<p>On a raw device Data processed by Ericsson Network IQ</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"50%\">\n" +
"<p><tt class=\"file-path\">/eniq/data/etldata</tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"50%\">\n" +
"<p>On a rack device Data processed by Ericsson Network IQ</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"50%\">\n" +
"<p><tt class=\"file-path\">/eniq/log/sw_log</tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"50%\">\n" +
"<p>Ericsson Network IQ log files</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"50%\">\n" +
"<p><tt class=\"file-path\">/eniq/sw/conf</tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"50%\">\n" +
"<p>Ericsson Network IQ configuration files</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"50%\">\n" +
"<p><tt class=\"file-path\">/eniq/sw/bin</tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"50%\">\n" +
"<p>Scripts to start and stop Ericsson Network IQ services</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"50%\">\n" +
"<p><tt class=\"file-path\">/eniq/database</tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"50%\">\n" +
"<p>Ericsson Network IQ database files</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"50%\">\n" +
"<p><tt class=\"file-path\">/eniq/archive</tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"50%\">\n" +
"<p>Processed data from parser</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"50%\">\n" +
"<p><tt class=\"file-path\">/eniq/archive/<i class=\"var\">OSS_ID/interface_name</i>/failed</tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"50%\">\n" +
"<p>Failed data from parser</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"50%\">\n" +
"<p><tt class=\"file-path\">/eniq/data/etldata/<i class=\"var\">techpack_name_counter_name</i>/failed</tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"50%\">\n" +
"<p>Failed data from loader</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"50%\">\n" +
"<p><tt class=\"file-path\">/eniq/sentinel/lic</tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"50%\">\n" +
"<p>ENIQ licenses</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"50%\">\n" +
"<p>	<tt class=\"file-path\">/eniq/local_logs/installation</tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"50%\">\n" +
"<p>Contains initial installation log files</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"50%\">\n" +
"<p><tt class=\"file-path\">/eniq/log/sentinel_logs</tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"50%\">\n" +
"<p>Contains license logs</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"50%\">\n" +
"<p><tt class=\"file-path\">/eniq/log/tunnel_logs</tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"50%\">\n" +
"<p>Contains logs regarding the tunnel connection</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"50%\">\n" +
"<p><tt class=\"file-path\">/eniq/log/rolling_snapshot_logs</tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"50%\">\n" +
"<p>Contains logs regarding the roll back service and manual\n" +
"snapshots</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"50%\">\n" +
"<p><tt class=\"file-path\">/eniq/log/sw_log/busyhourcfg</tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"50%\">\n" +
"<p>Contains logs regarding busy hour configuration</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"50%\">\n" +
"<p><tt class=\"file-path\">/eniq/log/sw_log/iq/dwhdb.iqmsg</tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"50%\">\n" +
"<p>Master database log file (Rack)</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"50%\">\n" +
"<p><tt class=\"file-path\">/eniq/local_logs/iq/dwhdb.iqmsg</tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"50%\">\n" +
"<p>Master database log file (Blade)</p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"50%\">\n" +
"<p><tt class=\"file-path\">&lt;ENIQ_VOLUME_MT_POINT&gt;/redback/&lt;nodetype&gt;</tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"50%\">\n" +
"<p>The location where symbolic links to the PM counter files\n" +
"from Redback nodes will be available. <sup><a href='javascript:parent.parent.parent.showAnchor(\"tnote1-1\")'>(1)</a></sup> </p>\n" +
"</td></tr>\n" +
"<tr valign=\"top\">\n" +
"<td align=\"left\" width=\"50%\">\n" +
"<p>	<tt class=\"file-path\">/eniq/data/pmdata_soem</tt></p>\n" +
"</td>\n" +
"<td align=\"left\" width=\"50%\">\n" +
"<p>Links to PM files from SO-EM node</p>\n" +
"</td></tr></table><p class=\"tnote\"><a name=\"tnote1-1\">(1) &nbsp;</a>Where ENIQ_VOLUME_MT_POINT\n" +
"is <tt class=\"file-path\">/ossrc/data/pmMediation/pmData</tt> which\n" +
"is set in <tt class=\"file-path\">/ericsson/eniq/etc/eniq.env</tt> and\n" +
"the node type can be edge, cpg or mlppp.<br /></p><br />\n" +
"\n" +
"\n"+
"</div>\n";

