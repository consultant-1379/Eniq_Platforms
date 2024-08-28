var C8=
"<div id=\"content\" class=\"body-content\">\n"+
"<h1><a name=\"EniqServiceCommands\"></a><span class=\"CHAPNUMBER\">8 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_vzh9\"></a><a name=\"CHAPTER8\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">ENIQ Service\n" +
"Commands</a></span></h1>\n" +
"\n" +
"<p>\n" +
"</p>\n" +
"\n" +
"\n"+
"</div>\n";

var C8_1=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">8.1 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_4p7l\"></a><a name=\"CHAPTER8.1\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Changing dcuser Unix Password</a></span></h2>\n" +
"\n" +
"<p>\n" +
"<b>To change dcuser password</b></p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Log on to server as <tt class=\"file-path\">dcuser</tt> using\n" +
"SSH.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">In command prompt, type<br /><tt class=\"input\"><b>passwd <i class=\"var\">&lt;username&gt;</i></b></tt><br />where the <tt class=\"file-path\">&lt;username&gt;</tt> is <tt class=\"file-path\">dcuser</tt></li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Type the new password. <br /><tt class=\"LITERALMONO\">New Password:</tt></li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Type the new password again:<br /> <tt class=\"LITERALMONO\">Re-enter new Password:</tt><p>\n" +
"The following message is displayed:<br /> <tt class=\"LITERALMONO\">passwd: password successfully changed for dcuser</tt></p>\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>In case of a multi-blade server, dcuser UNIX password should\n" +
"be same on all blades.</dd></dl><br /></li><li class=\"STEPLIST\" id=\"THSSTYLE2\"><tt class=\"input\"><b>cd /eniq/sw/platform/repository-<i class=\"var\">&lt;Rstate&gt;</i>/bin</b></tt><p>\n" +
"<tt class=\"input\"><b>chmod +x ChangeUserPasswordsInRepdb</b></tt></p>\n" +
"</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Then run the below script: <br /><tt class=\"input\"><b>./ChangeUserPasswordsInRepdb <i class=\"var\">&lt;User name&gt;</i> <i class=\"var\">&lt;old password&gt;</i> <i class=\"var\">&lt;new password\n" +
"which you have given&gt;</i></b></tt><br /><br />It shows the result as\n" +
"below <br />Updating Password...<br />n Rows Affacted<br /><br />where\n" +
"'n' is number of <tt class=\"file-path\">dcuser</tt> services for which\n" +
"password has been changed.<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>In case of multi-blade run the above script on coordinator\n" +
"blade.</dd></dl><br /></li></ol>\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>If the password contains special characters such as <tt class=\"file-path\">$</tt>, <tt class=\"file-path\"> &amp;</tt>, <tt class=\"file-path\">^</tt> or <tt class=\"file-path\">..</tt>, enclose the <tt class=\"file-path\">&lt;new\n" +
"password which you have given&gt;</tt> with single quotes:<p>\n" +
"<tt class=\"input\"><b>./ChangeUserPasswordsInRepdb <i class=\"var\">&lt;User name&gt;</i> <i class=\"var\">&lt;old password&gt;</i> '<i class=\"var\">&lt;new password which you have given&gt;</i>'</b></tt></p>\n" +
"<p>\n" +
"Once the password is changed from the original <tt class=\"file-path\">dcuser</tt> password, it is not possible to reuse this password or to use a\n" +
"password containing any combination of the user id characters.</p>\n" +
"</dd></dl><br />\n" +
"\n"+
"</div>\n";

var C8_2=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">8.2 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_o0ja\"></a><a name=\"CHAPTER8.2\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Changing storadm and storobs Default\n" +
"Password</a></span></h2>\n" +
"\n" +
"<p>\n" +
"The default password for both storadm and storobs is &lsquo;change1me&rsquo;.\n" +
"This needs to be changed to a user defined password:</p>\n" +
"\n" +
"<p>\n" +
"<b>To change storadm password execute below command.</b></p>\n" +
"\n" +
"<p><tt class=\"input\"><b># passwd -r files storadm</b></tt></p>\n" +
"<p>\n" +
"<tt class=\"LITERALMONO\">New Password:</tt></p>\n" +
"\n" +
"<p>\n" +
"<tt class=\"LITERALMONO\">Re-enter new Password:</tt></p>\n" +
"\n" +
"<p>\n" +
"<b>To change storobs password  execute below command.</b></p>\n" +
"\n" +
"<p><tt class=\"input\"><b># passwd -r files storobs</b></tt></p>\n" +
"<p>\n" +
"<tt class=\"LITERALMONO\">New Password:</tt></p>\n" +
"\n" +
"<p>\n" +
"<tt class=\"LITERALMONO\">Re-enter new Password:</tt></p>\n" +
"\n" +
"\n"+
"</div>\n";

var C8_3=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">8.3 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_vpx8\"></a><a name=\"CHAPTER8.3\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Shutting Down DWH Database</a></span></h2>\n" +
"\n" +
"<p>\n" +
"<b>To shut down DWH database</b></p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Log on to server as <tt class=\"file-path\">dcuser</tt> using\n" +
"SSH.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Execute <br /><tt class=\"input\"><b>/eniq/sw/bin/dwhdb stop</b></tt><br /> If successful, the message <tt class=\"output\">SMF disabling svc:/eniq/dwhdb</tt> is displayed. </li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Verify shutdown with <br /><tt class=\"input\"><b>/eniq/sw/bin/dwhdb status</b></tt></li></ol>\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>When shutting down DWH database, SMF also shuts down the\n" +
"following ENIQ components: \n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">ENIQ engine</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ scheduler</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">On the multi-blade deployment the dwh_reader service\n" +
"on the dwh_reader_1 and dwh_reader_2 blades.</li></ul></dd></dl><br />\n" +
"\n"+
"</div>\n";

var C8_4=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">8.4 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_i5p8\"></a><a name=\"CHAPTER8.4\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Starting Up DWH Database</a></span></h2>\n" +
"\n" +
"<p>\n" +
"<b>To start up DWH database</b></p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Log on to server as <tt class=\"file-path\">dcuser</tt> using\n" +
"SSH.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Execute<br /> <tt class=\"input\"><b>/eniq/sw/bin/dwhdb start</b></tt><br /> If successful, the message <tt class=\"output\">SMF enabling svc:/eniq/dwhdb</tt> is displayed.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Verify startup with<br /><tt class=\"input\"><b>/eniq/sw/bin/dwhdb status</b></tt></li></ol>\n" +
"\n"+
"</div>\n";

var C8_5=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">8.5 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_96vv\"></a><a name=\"CHAPTER8.5\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Shutting Down REP Database</a></span></h2>\n" +
"\n" +
"<p>\n" +
"<b>To shut down REP database</b></p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Log on to server as <tt class=\"file-path\">dcuser</tt> using\n" +
"SSH.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Execute <br /><tt class=\"input\"><b>/eniq/sw/bin/repdb stop</b></tt><br /> If successful, the message <tt class=\"output\">SMF disabling svc:/eniq/repdb</tt> is displayed.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Verify shutdown with <br /><tt class=\"input\"><b>/eniq/sw/bin/repdb status</b></tt></li></ol>\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>When shutting down REP database, SMF also shuts down the\n" +
"following ENIQ components:\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">ENIQ engine</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ scheduler</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">AdminUI</li></ul></dd></dl><br />\n" +
"\n"+
"</div>\n";

var C8_6=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">8.6 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_zl8y\"></a><a name=\"CHAPTER8.6\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Starting Up REP Database</a></span></h2>\n" +
"\n" +
"<p>\n" +
"<b>To start up REP database</b></p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Log on to server as <tt class=\"file-path\">dcuser</tt> using\n" +
"SSH.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Execute <br /><tt class=\"input\"><b>/eniq/sw/bin/repdb start</b></tt><br /> If successful, the message <tt class=\"output\">SMF enabling svc:/eniq/repdb</tt> is displayed.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Verify startup with <br /><tt class=\"input\"><b>/eniq/sw/bin/repdb status</b></tt></li></ol>\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>When starting up REP database, SMF also starts the following\n" +
"ENIQ components:\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">ENIQ engine</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ scheduler</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">AdminUI</li></ul></dd></dl><br />\n" +
"\n"+
"</div>\n";

var C8_7=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><a name=\"shutdown\"></a><span class=\"CHAPNUMBER\">8.7 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_nci4\"></a><a name=\"CHAPTER8.7\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Shutting Down\n" +
"AdminUI</a></span></h2>\n" +
"\n" +
"<p>\n" +
"<b>To shut down AdminUI</b></p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Log on to server as <tt class=\"file-path\">dcuser</tt> using\n" +
"SSH.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Execute <br /><tt class=\"input\"><b>/eniq/sw/bin/webserver stop</b></tt><br /> If successful, the message <tt class=\"output\">SMF disabling svc:/eniq/webserver</tt> is displayed.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Verify shutdown with<br /><tt class=\"input\"><b>/eniq/sw/bin/webserver\n" +
"status</b></tt></li></ol>\n" +
"\n"+
"</div>\n";

var C8_8=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><a name=\"startup\"></a><span class=\"CHAPNUMBER\">8.8 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_drcr\"></a><a name=\"CHAPTER8.8\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Starting Up AdminUI</a></span></h2>\n" +
"\n" +
"<p>\n" +
"<b>To start up AdminUI</b></p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Log on to server as <tt class=\"file-path\">dcuser</tt> using\n" +
"SSH.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Execute <br /><tt class=\"input\"><b>/eniq/sw/bin/webserver start</b></tt><br /> If successful, the message <tt class=\"output\">SMF enabling svc:/eniq/webserver</tt> is displayed.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Verify startup with <br /><tt class=\"input\"><b>/eniq/sw/bin/webserver\n" +
"status</b></tt></li></ol>\n" +
"\n"+
"</div>\n";

var C8_9=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">8.9 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_4s2e\"></a><a name=\"CHAPTER8.9\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Shutting down ENIQ Engine</a></span></h2>\n" +
"\n" +
"<p>\n" +
"<b>To shut down ENIQ engine</b></p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Log on to server as <tt class=\"file-path\">dcuser</tt> using\n" +
"SSH. In a multi-blade deployment execute the command on the engine\n" +
"blade.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Execute <br /><tt class=\"input\"><b>/eniq/sw/bin/engine stop</b></tt><br /> If successful, the message <tt class=\"output\">SMF disabling svc:/eniq/engine</tt> is displayed.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Verify shutdown with<br /><tt class=\"input\"><b>/eniq/sw/bin/engine status</b></tt></li></ol>\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>When shutting down ENIQ engine, SMF also shuts down the following\n" +
"ENIQ component:\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">ENIQ scheduler</li></ul></dd></dl><br />\n" +
"\n"+
"</div>\n";

var C8_10=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">8.10 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_rq0h\"></a><a name=\"CHAPTER8.10\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Starting Up ENIQ Engine</a></span></h2>\n" +
"\n" +
"<p>\n" +
"<b>To start up ENIQ engine</b></p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Log on to server as <tt class=\"file-path\">dcuser</tt> using\n" +
"SSH. In a multi-blade deployment execute the command on the engine\n" +
"blade.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Execute <br /><tt class=\"input\"><b>/eniq/sw/bin/engine start</b></tt><br /> If successful, the message <tt class=\"output\">SMF enabling svc:/eniq/engine</tt> is displayed.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Verify startup with <br /><tt class=\"input\"><b>/eniq/sw/bin/engine status</b></tt></li></ol>\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>When starting up ENIQ engine, SMF also starts up the following\n" +
"ENIQ component, if it is offline:\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">ENIQ scheduler</li></ul></dd></dl><br />\n" +
"\n"+
"</div>\n";

var C8_11=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">8.11 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_irq4\"></a><a name=\"CHAPTER8.11\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Shutting Down ENIQ Scheduler</a></span></h2>\n" +
"\n" +
"<p>\n" +
"<b>To shut down ENIQ scheduler</b></p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Log on to server as <tt class=\"file-path\">dcuser</tt> using\n" +
"SSH.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Execute <br /><tt class=\"input\"><b>/eniq/sw/bin/scheduler stop</b></tt><br /> If successful, the message <tt class=\"output\">SMF disabling svc:/eniq/scheduler</tt> is displayed.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Verify shutdown with<br /><tt class=\"input\"><b>/eniq/sw/bin/scheduler\n" +
"status</b></tt></li></ol>\n" +
"\n"+
"</div>\n";

var C8_12=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">8.12 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_863r\"></a><a name=\"CHAPTER8.12\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Starting ENIQ Scheduler</a></span></h2>\n" +
"\n" +
"<p>\n" +
"<b>To start up ENIQ scheduler</b></p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Log on to server as <tt class=\"file-path\">dcuser</tt> using\n" +
"SSH.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Execute <br /><tt class=\"input\"><b>/eniq/sw/bin/scheduler start</b></tt><br /> If successful, the message <tt class=\"output\">SMF enabling svc:/eniq/scheduler</tt> is displayed.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Verify startup with <br /><tt class=\"input\"><b>/eniq/sw/bin/scheduler\n" +
"status</b></tt></li></ol>\n" +
"\n"+
"</div>\n";

var C8_13=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><a name=\"maintenance\"></a><span class=\"CHAPNUMBER\">8.13 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_sut8\"></a><a name=\"CHAPTER8.13\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Setting ENIQ\n" +
"to Maintenance Mode</a></span></h2>\n" +
"\n" +
"<p>\n" +
"<strong class=\"MEDEMPH\">To set maintenance mode:</strong></p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Log on to server as <tt class=\"file-path\">dcuser</tt> using\n" +
"SSH. In a multi-blade deployment execute the command on the engine\n" +
"blade.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Type on the command line: <br /><tt class=\"input\"><b>/eniq/sw/bin/engine\n" +
"-e changeProfile NoLoads</b></tt></li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Verify maintenance mode with <br /><tt class=\"input\"><b>/eniq/sw/bin/scheduler\n" +
"status</b></tt><dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd><b>Current Profile:</b> indicates the current state.</dd></dl><br /></li></ol>\n" +
"\n"+
"</div>\n";

var C8_14=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><a name=\"normal\"></a><span class=\"CHAPNUMBER\">8.14 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_j97v\"></a><a name=\"CHAPTER8.14\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Setting ENIQ to\n" +
"Normal Mode</a></span></h2>\n" +
"\n" +
"<p>\n" +
"<strong class=\"MEDEMPH\">To set normal mode:</strong></p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Log on to server as <tt class=\"file-path\">dcuser</tt> using\n" +
"SSH. In a multi-blade deployment execute the command on the engine\n" +
"blade.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Type on the command line: <br /><tt class=\"input\"><b>/eniq/sw/bin/engine\n" +
"-e changeProfile Normal</b></tt></li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Verify normal mode with <br /><tt class=\"input\"><b>/eniq/sw/bin/scheduler\n" +
"status</b></tt><dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd><b>Current Profile:</b> indicates the current state.</dd></dl><br /></li></ol>\n" +
"\n"+
"</div>\n";

var C8_15=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><a name=\"lock\"></a><span class=\"CHAPNUMBER\">8.15 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_0dqc\"></a><a name=\"CHAPTER8.15\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Locking IQ Users</a></span></h2>\n" +
"\n" +
"<p>\n" +
"<strong class=\"MEDEMPH\">To lock IQ users:</strong></p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Log in ENIQ database server. Or for a multi-blade deployment\n" +
"to <tt class=\"file-path\">dwh_reader_2</tt> using SQL client software.\n" +
"You need database administrator rights to lock users.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Create a login policy with the option locked=ON, with\n" +
"the command:<br /><tt class=\"input\"><b>CREATE LOGIN POLICY locked_users locked=ON</b></tt></li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Assign the user <tt class=\"file-path\">dcbo</tt> to the <tt class=\"file-path\">locked_users</tt> login policy:<br /><tt class=\"input\"><b>ALTER USER dcbo\n" +
"LOGIN POLICY locked_users</b></tt></li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Assign the user <tt class=\"file-path\">dcpublic</tt> to\n" +
"the <tt class=\"file-path\">locked_users</tt> login policy:<br /><tt class=\"input\"><b>ALTER USER dcpublic LOGIN POLICY locked_users</b></tt></li></ol>\n" +
"\n"+
"</div>\n";

var C8_16=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><a name=\"unlock\"></a><span class=\"CHAPNUMBER\">8.16 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_rs4z\"></a><a name=\"CHAPTER8.16\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Unlocking IQ Users</a></span></h2>\n" +
"\n" +
"<p>\n" +
"<strong class=\"MEDEMPH\">To unlock IQ users:</strong></p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Log on to ENIQ database server. Or, in a multi-blade deployment\n" +
"to the <tt class=\"file-path\">dwh_reader_2</tt> using SQL client software.\n" +
"You need database administrator rights to unlock users.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Create a login policy with the option locked=OFF, with\n" +
"the command:<br /><tt class=\"input\"><b>CREATE LOGIN POLICY unlocked_users locked=OFF</b></tt></li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Assign the user <tt class=\"file-path\">dcbo</tt> to the <tt class=\"file-path\">unlocked_users</tt> login policy:<br /> <tt class=\"input\"><b>ALTER USER\n" +
"dcbo LOGIN POLICY unlocked_users</b></tt></li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Assign the user <tt class=\"file-path\">dcpublic</tt> to\n" +
"the <tt class=\"file-path\">unlocked_users</tt> login policy:<br /><tt class=\"input\"><b>ALTER USER dcpublic LOGIN POLICY unlocked_users</b></tt></li></ol>\n" +
"\n"+
"</div>\n";

var C8_17=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">8.17 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_54ps\"></a><a name=\"CHAPTER8.17\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Changing Alarm Properties</a></span></h2>\n" +
"\n" +
"<p>\n" +
"<strong class=\"MEDEMPH\">To Change Alarm Properties</strong></p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Log on to server as <tt class=\"file-path\">dcuser</tt> using\n" +
"SSH.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">In the following directory<br /><tt class=\"input\"><b>/eniq/sw/bin/</b></tt><p>\n" +
"Execute the following command<br /><tt class=\"input\"><b>./change_alarm_property.bsh\n" +
"showAlarmConnProperties</b></tt></p>\n" +
"<p>\n" +
"to list all properties which can be edited.</p>\n" +
"<p>\n" +
"The following is an example of list of properties with sample values.</p><pre class=\"prencd\">authmethod=secEnterprise\n" +
"outputPath=${PMDATA_DIR}/AlarmInterface_15min/in\n" +
"username=eniq_alarm\n" +
"cms=webportal\\:6400\n" +
"interfaceId=AlarmInterface_15min\n" +
"hostname=localhost\\:8080\n" +
"password=eniq_alarm\n" +
"protocol=http\n" +
"outputFilePrefix=alarm_</pre><p></p>\n" +
"<p>\n" +
"Use command <br /><tt class=\"input\"><b>./change_alarm_property.bsh -alarmconn\n" +
" &lt;PROPERTY_NAME&gt;&lt;PROPERTY_VALUE&gt;</b></tt></p>\n" +
"<p>\n" +
"to change parameters which are used to make connection to Webportal\n" +
"from ENIQ server, where &lt;PROPERTY_NAME&gt; is the name of the parameter\n" +
"and &lt;PROPERTY_VALUE&gt; is the value of the parameter.</p>\n" +
"</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">In the following directory<br /><tt class=\"input\"><b>/eniq/sw/bin/</b></tt><p>\n" +
"Execute the following command<br /><tt class=\"input\"><b>./change_alarm_property.bsh\n" +
"showAlarmParserProperties</b></tt></p>\n" +
"<p>\n" +
"This command lists all properties which can be edited by using\n" +
"command mentioned later in this section. </p>\n" +
"<p>\n" +
"The following is an example of the list of properties with sample\n" +
"values : </p><pre class=\"prencd\">outDir=${ETLDATA_DIR}/adapter_tmp/alarm\n" +
"maxFilesPerRun=0\n" +
"dublicateCheck=false\n" +
"thresholdMethod=more\n" +
"inDir=${PMDATA_DIR}/AlarmInterface_5min/in\n" +
"ProcessedFiles.fileNameFormat=\n" +
"AlarmTemplate=ericsson_template.vm\n" +
"minFileAge=0\n" +
"periodDuration=5\n" +
"baseDir=${PMDATA_DIR}\n" +
"useZip=false\n" +
"archivePeriod=\n" +
"loaderDir=${ETLDATA_DIR}\n" +
"tag_id=alarm\n" +
"doubleCheckAction=move\n" +
"dateformat=yyyy-MM-dd HH\\:mm\\:ss\n" +
"ProcessedFiles.processedDir=\n" +
"failedAction=move\n" +
"dirThreshold=0\n" +
"workers=1\n" +
"afterParseAction=delete\n" +
"</pre><p></p>\n" +
"<p>\n" +
"Use command <br /><tt class=\"input\"><b>./change_alarm_property.bsh -alarmparser\n" +
"&lt;INTERFACE_NAME&gt;  &lt;PROPERTY_NAME&gt;&lt;PROPERTY_VALUE&gt;</b></tt></p>\n" +
"<p>\n" +
"to change parameters which are used to run Alarm Interfaces with\n" +
"certain parameter values, where <tt class=\"file-path\">&lt;INTERFACE_NAME&gt;</tt> is name of interface to be run, <tt class=\"file-path\">&lt;PROPERTY_NAME&gt;</tt> is the name of the parameter and <tt class=\"file-path\">&lt;PROPERTY_VALUE&gt;</tt> is the value of the parameter.</p>\n" +
"</li></ol>\n" +
"\n"+
"</div>\n";

var C8_18=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">8.18 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_ov7v\"></a><a name=\"CHAPTER8.18\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Deactivating SAEGW Interface</a></span></h2>\n" +
"\n" +
"<p>\n" +
"The network element name SAEGW has been changed to CPG. As a result\n" +
"the <tt class=\"file-path\">INTF_DC_E_REDB_SAEGW</tt> interface needs\n" +
"to be removed and new interface <tt class=\"file-path\">INTF_DC_E_REDB_CPG</tt> must be created. As removing of the interface is not supported the\n" +
"following command needs to be executed to deactivate the SAEGW interface.</p>\n" +
"\n" +
"<p>\n" +
"To deactivate the SAEGW interface:</p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Log in to ENIQ server as <tt class=\"file-path\">dcuser</tt>.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\"><tt class=\"input\"><b>cd /eniq/sw/installer</b></tt></li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Execute the command <p>\n" +
"<tt class=\"input\"><b>./deactivate_interface &ndash;o <i class=\"var\">&lt;OSS&gt;</i> -i INTF_DC_E_REDB_SAEGW</b></tt>.                                                               \n" +
"                                                                 \n" +
"                                  </p>\n" +
"</li></ol>\n" +
"<p>\n" +
"This deactivates the SAEGW interface from the particular OSS.</p>\n" +
"\n" +
"\n" +
"\n"+
"</div>\n";

