var C21=
"<div id=\"content\" class=\"body-content\">\n"+
"<h1><span class=\"CHAPNUMBER\">21 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_r1ng\"></a><a name=\"CHAPTER21\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Manage DDC</a></span></h1>\n" +
"\n" +
"<p>\n" +
"Diagnostics Data Collection (DDC) is a collection of scripts and\n" +
"utilities which perform periodic system analysis and dimensioning\n" +
"of the running ENIQ system. </p>\n" +
"\n" +
"<p>\n" +
"DDC consists of a single SMF service and three &ldquo;cron&rdquo;\n" +
"jobs. These are responsible for collecting data throughout the day,\n" +
"and collating the data into a single gzipped tar file. </p>\n" +
"\n" +
"<p>\n" +
"Historical data for the previous 28 days is maintained, and data\n" +
"older than this is removed from the system.  The data collected by\n" +
"DDC is intended to act as an aid for Ericsson engineers in the event\n" +
"of a fault in the ENIQ software or the network.  DDC is installed\n" +
"as part of ENIQ installation, and is enabled by default. No ongoing\n" +
"maintenance or user interaction is required. </p>\n" +
"\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>DDC performs a vital role in the diagnosis of faults and\n" +
"performance bottlenecks in an ENIQ installation. DDC is not known\n" +
"to have any impact on the performance of the ENIQ server. Disabling\n" +
"this service may reduce the effectiveness of third-line support fault\n" +
"analysis and require more effort to troubleshoot faults.</dd></dl><br />\n" +
"\n"+
"</div>\n";

var C21_1=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">21.1 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_uab8\"></a><a name=\"CHAPTER21.1\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Managing the DDC Service</a></span></h2>\n" +
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
"\n"+
"</div>\n";

var C21_2=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">21.2 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_hyq4\"></a><a name=\"CHAPTER21.2\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Managing DDC Collection of SAN/NAS\n" +
"Metrics</a></span></h2>\n" +
"\n" +
"<p>\n" +
"Diagnostic Data Collection for SAN and SFS/VA is enabled by default. </p>\n" +
"\n" +
"<p>\n" +
"The presence of <b>/eniq/log/ddc_data/config/MONITOR_CLARIION</b> file and <b>/eniq/log/ddc_data/config/MONITOR_SFS </b> suggests that the metric collection is enabled for SAN and SFS/VA\n" +
"respectively. </p>\n" +
"\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>These steps are only applicable for ENIQ Statistics Blade\n" +
"and Multi-Blade servers. \n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">ENIQ (Standalone) Server </li></ul>\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">ENIQ Coordinator Server </li></ul></dd></dl><br />\n" +
"<p>\n" +
"As <tt class=\"file-path\">root </tt>user, run the following commands: </p>\n" +
"\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">To disable or enable the metric collection for SAN use\n" +
"the below command. \n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE3\"><tt class=\"input\"><b># cd /eniq/admin/bin </b></tt></li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE3\"><tt class=\"input\"><b># ./manageSanDataCollection.bsh -m <i class=\"var\">&lt;enable/disable&gt;</i></b></tt><p>\n" +
"The log location for the command execution is <tt class=\"file-path\"> /eniq/log/assureddc</tt> and the log name is <tt class=\"file-path\">manageSanDataCollection.bsh.log</tt> </p>\n" +
"</li></ul></li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">To disable or enable the metric collection for SFS/VA\n" +
"use the below command. \n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE3\"><tt class=\"input\"><b># cd /eniq/admin/bin</b></tt> </li></ul><br />\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE3\"><tt class=\"input\"><b># ./manageSfsDataCollection.bsh -m <i class=\"var\">&lt;enable/disable&gt;</i></b></tt><p>\n" +
"The log location for the command execution is /eniq/log/assureddc\n" +
"and the log name is manageSfsDataCollection.bsh.log </p>\n" +
"</li></ul></li></ul>\n" +
"\n" +
"\n"+
"</div>\n";

