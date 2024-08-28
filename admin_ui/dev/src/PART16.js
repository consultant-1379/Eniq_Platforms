var C16=
"<div id=\"content\" class=\"body-content\">\n"+
"<h1><span class=\"CHAPNUMBER\">16 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_r1ng\"></a><a name=\"CHAPTER16\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Manage DDC</a></span></h1>\n" +
"\n" +
"<p>\n" +
"Diagnostics Data Collection (DDC) is a collection of scripts and\n" +
"utilities which perform periodic system analysis and dimensioning\n" +
"of the running ENIQ system. DDC consists of a single SMF service and\n" +
"three &ldquo;cron&rdquo; jobs. These are responsible for collecting\n" +
"data throughout the day, and collating the data into a single gzipped\n" +
"tar file at the end of the day. Historical data for the previous 28\n" +
"days is maintained, and data older than this is removed from the system.\n" +
" The data collected by DDC is intended to act as an aid for Ericsson\n" +
"engineers in the event of a fault in the ENIQ software or the network.\n" +
" DDC is installed as part of ENIQ installation, and is enabled by\n" +
"default. No ongoing maintenance or user interaction is required. </p>\n" +
"\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>DDC performs a vital role in the diagnosis of faults and\n" +
"performance bottlenecks in an ENIQ installation. DDC is not known\n" +
"to have any impact on the performance of the ENIQ server. Disabling\n" +
"this service may reduce the effectiveness of third-line support fault\n" +
"analysis and require more effort to troubleshoot faults.</dd></dl><br />\n" +
"\n"+
"</div>\n";

var C16_1=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">16.1 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_uab8\"></a><a name=\"CHAPTER16.1\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Managing the DDC service</a></span></h2>\n" +
"\n" +
"<p>\n" +
"The commands must be run on all the following server types</p>\n" +
"\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">ENIQ (Standalone) Server</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Coordinator Server</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Engine Server</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Reader Server(s)</li></ul>\n" +
"<p>\n" +
"Enter the following command to check the status of the DDC service:<br /><tt class=\"input\"><b># svcs -a | grep ddc</b></tt> </p>\n" +
"\n" +
"<p>\n" +
"To enable DDC, use the following command:</p>\n" +
"\n" +
"<p>\n" +
"<tt class=\"input\"><b># /usr/sbin/svcadm enable ddc</b></tt></p>\n" +
"\n" +
"<p>\n" +
"To disable DDC, use the following command:</p>\n" +
"\n" +
"<p>\n" +
"<tt class=\"input\"><b># /usr/sbin/svcadm disable ddc</b></tt></p>\n" +
"\n" +
"<p>\n" +
"If the DDC service is in a maintenance state, it is necessary to\n" +
"clear the service.</p>\n" +
"\n" +
"<p>\n" +
"<tt class=\"input\"><b># /usr/sbin/svcadm clear ddc</b></tt></p>\n" +
"\n" +
"\n" +
"\n"+
"</div>\n";

