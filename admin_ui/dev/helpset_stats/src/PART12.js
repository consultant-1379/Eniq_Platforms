var C12=
"<div id=\"content\" class=\"body-content\">\n"+
"<h1><span class=\"CHAPNUMBER\">12 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_gssy\"></a><a name=\"CHAPTER12\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Re-sizing NAS File System</a></span></h1>\n" +
"\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>Before running this script make sure, you have your data\n" +
"backed up as snapshots get deleted during the script execution.</dd></dl><br />\n" +
"\n"+
"</div>\n";

var C12_1=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">12.1 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_idf9\"></a><a name=\"CHAPTER12.1\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Disabling ENIQ Statistics Services</a></span></h2>\n" +
"\n" +
"<p>\n" +
"To disable the ENIQ services across the deployment, run the following\n" +
"command on the server types mentioned below: </p>\n" +
"\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">ENIQ Statistics (Standalone) Server</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Statistics Coordinator Server</li></ul>\n" +
"<p>\n" +
"As user <tt class=\"file-path\">root</tt> do the following: </p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\"><tt class=\"input\"><b># cd /eniq/admin/bin</b></tt></li><li class=\"STEPLIST\" id=\"THSSTYLE2\"><tt class=\"input\"><b># bash ./manage_deployment_services.bsh -a stop\n" +
"-s ALL</b></tt></li></ol>\n" +
"\n"+
"</div>\n";

var C12_2=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">12.2 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_rhx9\"></a><a name=\"CHAPTER12.2\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Stopping ENIQ Statistics Rolling\n" +
"Snapshot Service</a></span></h2>\n" +
"\n" +
"<p>\n" +
"Disable the rolling snapshot service. This must be done on each\n" +
"blade in the deployment in the following order.</p>\n" +
"\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">ENIQ Statistics (Standalone) Server</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Statistics Coordinator Server</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Statistics Engine Server</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Statistics Reader Server(s)</li></ul>\n" +
"<p>\n" +
"As user <tt class=\"file-path\">root</tt> do the following: </p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\"><tt class=\"input\"><b># /usr/sbin/svcadm disable svc:/eniq/roll-snap</b></tt></li><li class=\"STEPLIST\" id=\"THSSTYLE2\">If the service exists, check that the service is stopped\n" +
"(disabled/offline) by using the following command<p>\n" +
"<tt class=\"input\"><b># /usr/bin/svcs &ndash;a | grep roll-snap</b></tt></p>\n" +
"</li></ol>\n" +
"\n"+
"</div>\n";

var C12_3=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">12.3 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_74mx\"></a><a name=\"CHAPTER12.3\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Listing Snapshots</a></span></h2>\n" +
"\n" +
"<p>\n" +
"Get the list of snapshots using the following commands: </p>\n" +
"\n" +
"<p><tt class=\"input\"><b># cd /eniq/bkup_sw/bin</b></tt></p>\n" +
"<p>\n" +
"Run the commands below on the following server types:</p>\n" +
"\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">ENIQ (standalone)</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Coordinator</li></ul>\n" +
"<p>\n" +
"As user <tt class=\"file-path\">root</tt> do the following: </p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\"><tt class=\"input\"><b># bash ./manage_nas_snapshots.bsh -a list -f ALL</b></tt></li><li class=\"STEPLIST\" id=\"THSSTYLE2\"><tt class=\"input\"><b># bash ./manage_san_snapshots.bsh -a list -f ALL</b></tt><p>\n" +
"Run the command below on each blade in the deployment:</p>\n" +
"</li><li class=\"STEPLIST\" id=\"THSSTYLE2\"><tt class=\"input\"><b># bash ./manage_zfs_snapshots.bsh -a list -f ALL</b></tt></li></ol>\n" +
"<p>\n" +
"This gives an output similar to the following syntax:</p>\n" +
"\n" +
"<p><tt class=\"output\">&lt;pool_id&gt;/&lt;fs_name&gt;/&lt;snapshot_name&gt;</tt></p>\n" +
"\n"+
"</div>\n";

var C12_4=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">12.4 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_s4y9\"></a><a name=\"CHAPTER12.4\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Deleting Snapshots</a></span></h2>\n" +
"\n" +
"<p>\n" +
"Delete the snapshots listed in the previous step using the following\n" +
"commands:</p>\n" +
"\n" +
"<p>\n" +
"<tt class=\"input\"><b># cd /eniq/bkup_sw/bin</b></tt></p>\n" +
"\n" +
"<p>\n" +
"Run the commands below on the following server types:</p>\n" +
"\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">ENIQ (standalone)</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Coordinator</li></ul>\n" +
"<p>\n" +
"As user <tt class=\"file-path\">root</tt> do the following: </p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\"><tt class=\"input\"><b># bash ./manage_nas_snapshots.bsh -a delete -f\n" +
"ALL -n <i class=\"var\">snapshot-label</i></b></tt></li><li class=\"STEPLIST\" id=\"THSSTYLE2\"><tt class=\"input\"><b># bash ./manage_san_snapshots.bsh -a delete -f\n" +
"ALL -n <i class=\"var\">&lt;snapshot-label&gt;</i></b></tt><p>\n" +
"Run the commands below on each blade in the deployment:</p>\n" +
"</li><li class=\"STEPLIST\" id=\"THSSTYLE2\"><tt class=\"input\"><b># bash ./manage_zfs_snapshots.bsh -a delete -f\n" +
"ALL=<i class=\"var\">&lt;snapshot-label&gt;</i></b></tt></li></ol>\n" +
"\n"+
"</div>\n";

var C12_5=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">12.5 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_dpmi\"></a><a name=\"CHAPTER12.5\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Re-sizing the NAS File System</a></span></h2>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Verify that the ENIQ services are offline. This must be\n" +
"run on all server types in the following order.\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Statistics (Standalone) Server</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Statistics Coordinator Server</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Statistics Engine Server</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Statistics Reader Server(s)</li></ul><p>\n" +
"<tt class=\"input\"><b># /usr/bin/svcs &ndash;a | grep eniq</b></tt></p>\n" +
"</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Re-size the NAS FS by running the below command on following\n" +
"server types :\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ (standalone)</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Coordinator</li></ul><p>\n" +
"As user <tt class=\"file-path\">root</tt> do the following: </p>\n" +
"<p>\n" +
"<tt class=\"input\"><b># cd /eniq/admin/bin </b></tt></p>\n" +
"<p>\n" +
"<tt class=\"input\"><b># bash ./resize_nas_fs.bsh -a [auto|manual] [-l <i class=\"var\">&lt;path_to_logfile&gt;</i>]</b></tt></p>\n" +
"</li></ol>\n" +
"<p>\n" +
"When <strong class=\"MEDEMPH\">automatically</strong> re-sizing the NAS,\n" +
"the script checks the deployment type of the system and re-sizes the\n" +
"File Systems only if they are smaller than the recommended size.</p>\n" +
"\n" +
"<p>\n" +
"After a <strong class=\"MEDEMPH\">manual</strong> re-size you are prompted\n" +
"with a list of all File Systems on the system </p>\n" +
"\n" +
"<p><tt class=\"output\">List of all NAS FS, with current FS size.</tt></p>\n" +
"<p><tt class=\"output\">----------------------------------------</tt></p>\n" +
"<p><tt class=\"output\">FS Name                 FS Size</tt></p>\n" +
"<p><tt class=\"output\">-------------------------------</tt></p>\n" +
"<p><tt class=\"output\">1) admin                     2g</tt></p>\n" +
"<p><tt class=\"output\">2) archive                   8g</tt></p>\n" +
"<p><tt class=\"output\">3) backup                   10g</tt></p>\n" +
"<p><tt class=\"output\">4) etldata                  10g</tt></p>\n" +
"<p><tt class=\"output\">5) etldata_/00              10g</tt></p>\n" +
"<p><tt class=\"output\">6) etldata_/01              10g</tt></p>\n" +
"<p><tt class=\"output\">7) etldata_/02              10g</tt></p>\n" +
"<p><tt class=\"output\">...</tt></p>\n" +
"<p>\n" +
"And then you are prompted with a question to resize the NAS FS.</p>\n" +
"\n" +
"<p><tt class=\"output\">Please enter a FS to re-size using the following format [n,n,n-n,n...n],\n" +
"eg. 1,3,7-11)</tt></p>\n" +
"<p>\n" +
"Enter the File System that you wish to increase accordingly. Once\n" +
"entered you are prompted to enter the size that you wish to increase\n" +
"the File System to. Ex : </p>\n" +
"\n" +
"<p><tt class=\"output\">Enter new filessytem size for admin, the current size is 2g</tt></p>\n" +
"<p><tt class=\"output\">(Recommended size for deployment is 20g ('g = gb' 'use m for\n" +
"mb' and 'use k for kb'))</tt></p>\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>Enter a value followed by a single character either g(gigabyte)\n" +
"/ m(megabyte) / k(kilobyte)</dd></dl><br />\n" +
"<p>\n" +
"Once entered the script checks available space on the NAS and compare\n" +
"it with your entry, if there is enough space the selected NAS FS is\n" +
"re-sized.</p>\n" +
"\n" +
"\n"+
"</div>\n";

var C12_6=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">12.6 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_0v29\"></a><a name=\"CHAPTER12.6\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Starting ENIQ Statistics Rolling\n" +
"Snapshot Service</a></span></h2>\n" +
"\n" +
"<p>\n" +
"Enable the rolling snapshot service. This must be done on each\n" +
"blade in the deployment in the following order.</p>\n" +
"\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">ENIQ Statistics (Standalone) Server</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Statistics Coordinator Server</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Statistics Engine Server</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Statistics Reader Server(s)</li></ul>\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\"><tt class=\"input\"><b># /usr/sbin/svcadm enable svc:/eniq/roll-snap</b></tt></li><li class=\"STEPLIST\" id=\"THSSTYLE2\">If the service exists, check that the service has started(enabled/online)\n" +
"by using the following command<p>\n" +
"<tt class=\"input\"><b># /usr/bin/svcs &ndash;a | grep roll-snap</b></tt></p>\n" +
"</li></ol>\n" +
"\n"+
"</div>\n";

var C12_7=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><a name=\"restartEniqServices\"></a><span class=\"CHAPNUMBER\">12.7 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_c6wx\"></a><a name=\"CHAPTER12.7\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Restarting\n" +
"ENIQ Services</a></span></h2>\n" +
"\n" +
"<p>\n" +
"To restart ENIQ services across the deployment, run the following\n" +
"command on the server types mentioned below: </p>\n" +
"\n" +
"\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">ENIQ Statistics (Standalone) Server</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ Statistics Coordinator Server</li></ul>\n" +
"<p>\n" +
"As user <tt class=\"file-path\">root</tt>, do the following: </p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\"><tt class=\"input\"><b># cd /eniq/admin/bin</b></tt></li><li class=\"STEPLIST\" id=\"THSSTYLE2\"><tt class=\"input\"><b># bash ./manage_deployment_services.bsh -a restart\n" +
"-s ALL</b></tt></li></ol>\n" +
"\n" +
"\n"+
"</div>\n";

