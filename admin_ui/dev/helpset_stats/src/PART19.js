var C19=
"<div id=\"content\" class=\"body-content\">\n"+
"<h1><a name=\"chl1-CEPMediationConfigurationandManagement\"></a><span class=\"CHAPNUMBER\">19 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_dmv1\"></a><a name=\"CHAPTER19\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">FRH Configuration and Management</a></span></h1>\n" +
"\n"+
"</div>\n";

var C19_1=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">19.1 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_67jz\"></a><a name=\"CHAPTER19.1\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Monitor the NAS Filesystems</a></span></h2>\n" +
"\n" +
"<p>\n" +
"There is a <em class=\"LOWEMPH\">nasd</em> daemon which monitors all\n" +
"the NFS filesystems mounted from the SFS/VA (NAS filesystems). This\n" +
"daemon is enabled by default and it continuously monitors the NAS\n" +
"filesystems. If it detects that any one of the NAS filesystems is\n" +
"down or unavailable it restarts the automounter daemon (<em class=\"LOWEMPH\">autofs</em>).</p>\n" +
"\n" +
"<p>\n" +
"The daemon can be managed with the following command:</p>\n" +
"\n" +
"<p><tt class=\"input\"><b># service nasd {start|stop|restart|reload|status}</b></tt></p>\n" +
"<p>\n" +
"The following log file can be referenced for troubleshooting errors:</p>\n" +
"\n" +
"<p>\n" +
"<tt class=\"file-path\">/ericsson/frh/local_logs/nasd/nasd.log</tt></p>\n" +
"\n" +
"\n" +
"\n"+
"</div>\n";

