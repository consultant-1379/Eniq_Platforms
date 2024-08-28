var C11=
"<div id=\"content\" class=\"body-content\">\n"+
"<h1><span class=\"CHAPNUMBER\">11 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_rt18\"></a><a name=\"CHAPTER11\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Change the NAS VIPs</a></span></h1>\n" +
"\n" +
"<p>\n" +
"It is possible to update ENIQ so that the number of NAS VIPs can\n" +
"be changed. The procedure below will allow administrators to change\n" +
"the NAS VIPs. </p>\n" +
"\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>This procedure is applicable for blade installation only.</dd></dl><br />\n" +
"\n"+
"</div>\n";

var C11_1=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">11.1 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_t70x\"></a><a name=\"CHAPTER11.1\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Run Change NAS VIPs</a></span></h2>\n" +
"\n" +
"<p>\n" +
"To change NAS VIPs across the deployment, run the following command\n" +
"on the server types below .</p>\n" +
"\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">ENIQ Statistics (standalone)</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Statistics Coordinator</li></ul>\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">As user root do the following:  <p>\n" +
"<tt class=\"input\"><b># cd /eniq/installation/core_install/bin </b></tt></p>\n" +
"<p>\n" +
"<tt class=\"input\"><b># bash ./change_nas_vips.bsh [-l &lt;path_to_logfile&gt;]</b></tt></p>\n" +
"</li></ol>\n" +
"<p>\n" +
"You will be asked to select the number of virtual IPs available\n" +
"in SFS . Once entered then you will be asked to enter the virtual\n" +
"IP addresses of the NAS servers. This question will loop until all\n" +
"required IPs are entered. In case log file is not entered then default\n" +
"log will be in /eniq/local_logs/change_nas_vips/change_nas_vips.log	</p>\n" +
"\n" +
"\n"+
"</div>\n";

var C11_2=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">11.2 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_eekv\"></a><a name=\"CHAPTER11.2\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Restart ENIQ services and NASd</a></span></h2>\n" +
"\n" +
"\n"+
"</div>\n";

var C11_2_1=
"<div id=\"content\" class=\"body-content\">\n"+
"<h3><span class=\"CHAPNUMBER\">11.2.1 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_lje3\"></a><a name=\"CHAPTER11.2.1\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Disable Eniq Services</a></span></h3>\n" +
"\n" +
"<p>\n" +
"Stop ENIQ  Services Ensure all services are offline. This must\n" +
"be run on all server types in the following order</p>\n" +
"\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">ENIQ Statistics (Standalone) Server</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Statistics Reader_2 Server</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Statistics Reader_1 Server</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Statistics Engine Server</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Statistics Coordinator Server</li></ul>\n" +
"<p>\n" +
"As user root on each blade, do the following: </p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\"><tt class=\"input\"><b># cd /eniq/admin/bin</b></tt></li><li class=\"STEPLIST\" id=\"THSSTYLE2\"><tt class=\"input\"><b># bash ./manage_eniq_services.bsh -a stop -s ALL\n" +
"\\ [-l &lt;path_to_logfile&gt;]</b></tt></li></ol>\n" +
"\n"+
"</div>\n";

var C11_2_2=
"<div id=\"content\" class=\"body-content\">\n"+
"<h3><a name=\"NasdRestart\"></a><span class=\"CHAPNUMBER\">11.2.2 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_xec3\"></a><a name=\"CHAPTER11.2.2\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Restart NASd</a></span></h3>\n" +
"\n" +
"<p>\n" +
"To restart NASd across the deployment, as user root run the following\n" +
"commands on each blade in the deployment.</p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\"><tt class=\"input\"><b># /usr/sbin/svcadm restart svc:/storage/NASd </b></tt><p>\n" +
"Wait for NASd service and NAS milestone to come online and verify\n" +
"they are started by using the following command</p>\n" +
"</li><li class=\"STEPLIST\" id=\"THSSTYLE2\"><tt class=\"input\"><b># /usr/bin/svcs -a | grep NAS</b></tt></li></ol>\n" +
"\n"+
"</div>\n";

var C11_2_3=
"<div id=\"content\" class=\"body-content\">\n"+
"<h3><span class=\"CHAPNUMBER\">11.2.3 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_codr\"></a><a name=\"CHAPTER11.2.3\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Enable Eniq Services</a></span></h3>\n" +
"\n" +
"<p>\n" +
"Start ENIQ  Services ensures that all services are online. This\n" +
"must be run on all server types in the following order</p>\n" +
"\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">ENIQ (Standalone) Server</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Coordinator Server</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Engine Server</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Reader Server(s)</li></ul>\n" +
"<p>\n" +
"As user root on each blade, do the following: </p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\"><tt class=\"input\"><b># cd /eniq/admin/bin</b></tt></li><li class=\"STEPLIST\" id=\"THSSTYLE2\"><tt class=\"input\"><b># bash ./manage_eniq_services.bsh -a start -s ALL\n" +
"\\ [-l &lt;path_to_logfile&gt;]</b></tt></li></ol>\n" +
"\n" +
"\n" +
"\n"+
"</div>\n";

