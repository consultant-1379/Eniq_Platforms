var C13=
"<div id=\"content\" class=\"body-content\">\n"+
"<h1><span class=\"CHAPNUMBER\">13 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_jtjr\"></a><a name=\"CHAPTER13\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Flexible data backup configuration</a></span></h1>\n" +
"\n" +
"<p>\n" +
"OMBS supports 3 types of backups i.e. FullData, NoData and FlexibleData\n" +
"backup. The Flexible data-backup module provides a platform for user\n" +
"to backup the data as per the user configuration.</p>\n" +
"\n" +
"<p>\n" +
"Below mentioned section provide the action which needs to be executed\n" +
"on ENIQ Statistics server while selecting the OMBS type as \"FlexibleData\".</p>\n" +
"\n" +
"<p>\n" +
"Below are the operations included in the module:</p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Enabling flexible data backup.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Disabling flexible data backup.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Restoring the data from the backup.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Re-configuration of flexible data backup FS size.</li></ol>\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>If it is a multi-blade server, all the commands which is\n" +
"mentioned in below sections should be executed in coordinator blade\n" +
"until it is mentioned explicitly.</dd></dl><br />\n" +
"\n"+
"</div>\n";

var C13_1=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">13.1 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_sen0\"></a><a name=\"CHAPTER13.1\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Enabling flexible data backup</a></span></h2>\n" +
"\n" +
"<p>\n" +
"It will create a new file-system on NAS and start loading the data\n" +
"on created file-system.</p>\n" +
"\n" +
"<p>\n" +
"For executing flexible data backup it is a mandatory step which\n" +
"needs to be executed 2-week before the Flexible OMBS configuration</p>\n" +
"\n" +
"<p>\n" +
"To enable the flexible data backup below step needs to be executed\n" +
":</p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Take the snapshot of server. <p>\n" +
" For creating the snapshot refer to chapter \"Create Snapshots\"\n" +
"of <em class=\"LOWEMPH\">ENIQ Statistics X86 Blade Upgrade Procedure</em>, <a href='javascript:parent.parent.parent.showAnchor(\"EniqUpgradeProc\")' class=\"xref\">Reference [8]</a></p>\n" +
"</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Logon to the server as root user and run the following\n" +
"script:<p>\n" +
"<tt class=\"input\"><b># cd /eniq/bkup_sw/bin/</b></tt></p>\n" +
"<p>\n" +
"<tt class=\"input\"><b># bash ./flexible_data_bkup.bsh -a enable</b></tt></p>\n" +
"<p>\n" +
"A list of backup type (2-week, 3-week etc.) will be prompted to\n" +
"user for selecting the number of week of data to be backed up.</p>\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">Currently only 2-week data backup option is available\n" +
"for user. </li></ul>\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">Enabling of flexible data backup will take 15-20 min.\n" +
"for single-blade and 25-30 min. for multi-blade.</li></ul>\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">Flexible data backup is not supported for RACK.</li></ul></dd></dl><br /></li><li class=\"STEPLIST\" id=\"THSSTYLE2\">If any FRH server is present in the deployment then only\n" +
"we need to execute this step.<p>\n" +
"Below are the action which needs to be performed to make sure that\n" +
"\"flexible data backup\" FS is shared and mounted on all the FRH blade\n" +
"also.</p>\n" +
"<p>\n" +
"Log on to all the FRH server as a root user and execute the below\n" +
"step on all the servers one by one.</p>\n" +
"<p>\n" +
"<tt class=\"input\"><b># cd /ericsson/frh/installation/core_install/bin</b></tt></p>\n" +
"<p>\n" +
"<tt class=\"input\"><b># bash ./fs_configure.bsh -a add [-l logfile] </b></tt></p>\n" +
"</li></ol>\n" +
"\n"+
"</div>\n";

var C13_2=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">13.2 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_ukbj\"></a><a name=\"CHAPTER13.2\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Disabling flexible data backup</a></span></h2>\n" +
"\n" +
"<p>\n" +
"This procedure will stop the running backup of 2 week data and\n" +
"remove the NAS file system. </p>\n" +
"\n" +
"<p>\n" +
"This step needs to be executed after changing the OMBS type from\n" +
"Flexible data backup to any other.</p>\n" +
"\n" +
"<p>\n" +
"To disable the flexible data backup below step needs to be executed\n" +
":</p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Take the snapshot of server. <p>\n" +
" For creating the snapshot refer to chapter \"Create Snapshots\"\n" +
"of For creating the snapshot refer to chapter \"Create Snapshots\" of <em class=\"LOWEMPH\">ENIQ Statistics X86 Blade Upgrade Procedure</em>, <a href='javascript:parent.parent.parent.showAnchor(\"EniqUpgradeProc\")' class=\"xref\">Reference [8]</a></p>\n" +
"</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">	Logon to the server as <strong class=\"MEDEMPH\">root</strong> user and run\n" +
"the following script :<p>\n" +
"<tt class=\"input\"><b># cd /eniq/bkup_sw/bin/</b></tt></p>\n" +
"<p>\n" +
"<tt class=\"input\"><b># bash ./flexible_data_bkup.bsh -a disable</b></tt></p>\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>Disabling of flexible data backup will take 15-20 min. for\n" +
"single-blade and 25-30 min. for multi-blade.</dd></dl><br /></li><li class=\"STEPLIST\" id=\"THSSTYLE2\">If any FRH server is present in the deployment then only\n" +
"we need to execute this step.<p>\n" +
"Below are the action which needs to be performed to make sure that\n" +
"\"flexible data backup\" FS sharing is removed from all the FRH blade. </p>\n" +
"<p>\n" +
"Log on to all the FRH server as a root user and execute the below\n" +
"step on all the servers one by one.</p>\n" +
"<p>\n" +
"<tt class=\"input\"><b># cd /ericsson/frh/installation/core_install/bin</b></tt></p>\n" +
"<p>\n" +
"<tt class=\"input\"><b># bash ./fs_configure.bsh -a remove [-l logfile] </b></tt></p>\n" +
"</li></ol>\n" +
"\n"+
"</div>\n";

var C13_3=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">13.3 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_hdyk\"></a><a name=\"CHAPTER13.3\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Restoring the data from the backup</a></span></h2>\n" +
"\n" +
"<p>\n" +
"It will restore the data which is backed-up as a part of flexible\n" +
"data backup.</p>\n" +
"\n" +
"\n"+
"</div>\n";

var C13_4=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">13.4 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_g1hn\"></a><a name=\"CHAPTER13.4\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Re-configuration of flexible data\n" +
"backup FS size</a></span></h2>\n" +
"\n" +
"<p>\n" +
" If user wants to add the additional week of data backup against\n" +
"the existing one, backup file-system size and other backup parameters\n" +
"needs to be re-configured.</p>\n" +
"\n" +
"<p>\n" +
"To re-configure the file-system size below step needs to be executed\n" +
":</p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Take the snapshot of server. <p>\n" +
" For creating the snapshot refer to chapter \"Create Snapshots\"\n" +
"of For creating the snapshot refer to chapter \"Create Snapshots\" of <em class=\"LOWEMPH\">ENIQ Statistics X86 Blade Upgrade Procedure</em>, <a href='javascript:parent.parent.parent.showAnchor(\"EniqUpgradeProc\")' class=\"xref\">Reference [8]</a></p>\n" +
"</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Logon to the server as root user and run the following\n" +
"script :<p>\n" +
"<tt class=\"input\"><b># cd /eniq/bkup_sw/bin/</b></tt></p>\n" +
"<p>\n" +
"<tt class=\"input\"><b># bash ./flexible_data_bkup.bsh -a reconfigure</b></tt></p>\n" +
"</li></ol>\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>Once the FS size increased, it cannot be decrease as it will\n" +
"lead to data loss.</dd></dl><br />\n" +
"\n" +
"\n"+
"</div>\n";

