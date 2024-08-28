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
"<h2><span class=\"CHAPNUMBER\">8.1 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_4p7l\"></a><a name=\"CHAPTER8.1\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Changing dcuser Unix password</a></span></h2>\n" +
"\n" +
"<p>\n" +
"<b>To change dcuser password</b></p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Log in to server as dcuser with SSH.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">In command prompt, type<br /><tt class=\"LITERALMONO\">passwd &lt;username&gt;</tt><br />where the &lt;username&gt; is dcuser</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Type dcuser's existing password. For security reasons,\n" +
"the user interface does not show the password you are typing. <br /><tt class=\"LITERALMONO\">Enter existing login password:</tt></li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Type the new password. <br /><tt class=\"LITERALMONO\">New Password:</tt></li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Type the new password again:<br /> <tt class=\"LITERALMONO\">Reenter new Password:</tt><p>\n" +
"The following message is displayed:<br /> <tt class=\"LITERALMONO\">passwd: password successfully changed for dcuser</tt></p>\n" +
"</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Please go to below path:<br /><tt class=\"LITERALMONO\">cd /eniq/sw/platform/repository-&lt;Rstate&gt;/bin</tt><br />Run the following command <tt class=\"LITERALMONO\">chmod +x ChangeUserPasswordsInRepdb</tt></li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Then run the below script: <br /><tt class=\"LITERALMONO\">./ChangeUserPasswordsInRepdb &lt;User name&gt; &lt;service name&gt; &lt;old password&gt; &lt;new password which you have given&gt;</tt><br />It will show the result as below <br />Updating Password...<br />1 Rows Affected</li></ol>\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>The script does not set the password properly when the password\n" +
"is with special character such as '$, &amp;, ^..'. So in that case\n" +
"we need to execute the command as below:<tt class=\"LITERALMONO\">&nbsp;./ChangeUserPasswordsInRepdb &lt;User name&gt; &lt;service name&gt; &lt;old password&gt;'&lt;new password which you have given&gt;'</tt></dd></dl><br />\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>It should be noted that once the password is changed from\n" +
"the original 'dcuser' password, it will not be possible to reuse this\n" +
"password or to use any password containing any combination of the\n" +
"user id characters</dd></dl><br />\n" +
"\n"+
"</div>\n";

var C8_2=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">8.2 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_vpx8\"></a><a name=\"CHAPTER8.2\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Shutting down DWH database</a></span></h2>\n" +
"\n" +
"<p>\n" +
"<b>To shut down DWH database</b></p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Log in to server as dcuser with SSH.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Execute <br /><tt class=\"LITERALMONO\">/eniq/sw/bin/dwhdb stop</tt><br /> If successful, the message <tt class=\"output\">SMF disabling svc:/eniq/dwhdb</tt> is displayed. </li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Verify shutdown with <br /><tt class=\"LITERALMONO\">/eniq/sw/bin/dwhdb status</tt></li></ol>\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>When shutting down DWH database, SMF also shuts down the\n" +
"following ENIQ components: \n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">ENIQ engine</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ scheduler</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">On the multi blade deployment the dwh_reader service\n" +
"on the dwh_reader_1 and dwh_reader_2 blades.</li></ul></dd></dl><br />\n" +
"\n"+
"</div>\n";

var C8_3=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">8.3 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_i5p8\"></a><a name=\"CHAPTER8.3\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Starting up DWH database</a></span></h2>\n" +
"\n" +
"<p>\n" +
"<b>To start up DWH database</b></p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Log in to server as dcuser with SSH.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Execute<br /> <tt class=\"LITERALMONO\">/eniq/sw/bin/dwhdb start</tt><br /> If successful, the message <tt class=\"output\">SMF enabling svc:/eniq/dwhdb</tt> is displayed.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Verify startup with<br /><tt class=\"LITERALMONO\">/eniq/sw/bin/dwhdb status</tt></li></ol>\n" +
"\n"+
"</div>\n";

var C8_4=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">8.4 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_96vv\"></a><a name=\"CHAPTER8.4\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Shutting down REP database</a></span></h2>\n" +
"\n" +
"<p>\n" +
"<b>To shut down REP database</b></p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Log in to server as dcuser with SSH.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Execute <br /><tt class=\"LITERALMONO\">/eniq/sw/bin/repdb stop</tt><br /> If successful, the message <tt class=\"output\">SMF disabling svc:/eniq/repdb</tt> is displayed.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Verify shutdown with <br /><tt class=\"LITERALMONO\">/eniq/sw/bin/repdb status</tt></li></ol>\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>When shutting down REP database, SMF also shuts down the\n" +
"following ENIQ components:\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">ENIQ engine</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ scheduler</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">AdminUI</li></ul></dd></dl><br />\n" +
"\n"+
"</div>\n";

var C8_5=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">8.5 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_zl8y\"></a><a name=\"CHAPTER8.5\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Starting up REP database</a></span></h2>\n" +
"\n" +
"<p>\n" +
"<b>To start up REP database</b></p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Log in to server as dcuser with SSH.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Execute <br /><tt class=\"LITERALMONO\">/eniq/sw/bin/repdb start</tt><br /> If successful, the message <tt class=\"output\">SMF enabling svc:/eniq/repdb</tt> is displayed.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Verify startup with <br /><tt class=\"LITERALMONO\">/eniq/sw/bin/repdb status</tt></li></ol>\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>When starting up REP database, SMF also starts the following\n" +
"ENIQ components:\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">ENIQ engine</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">ENIQ scheduler</li>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE1\">AdminUI</li></ul></dd></dl><br />\n" +
"\n"+
"</div>\n";

var C8_6=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><a name=\"shutdown\"></a><span class=\"CHAPNUMBER\">8.6 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_nci4\"></a><a name=\"CHAPTER8.6\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Shutting down\n" +
"AdminUI</a></span></h2>\n" +
"\n" +
"<p>\n" +
"<b>To shut down AdminUI</b></p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Log in to server as dcuser with SSH.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Execute <br /><tt class=\"LITERALMONO\">/eniq/sw/bin/webserver stop</tt><br /> If successful, the message <tt class=\"output\">SMF disabling svc:/eniq/webserver</tt> is displayed.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Verify shutdown with<br /><tt class=\"LITERALMONO\">/eniq/sw/bin/webserver status</tt></li></ol>\n" +
"\n"+
"</div>\n";

var C8_7=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><a name=\"startup\"></a><span class=\"CHAPNUMBER\">8.7 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_drcr\"></a><a name=\"CHAPTER8.7\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Starting up AdminUI</a></span></h2>\n" +
"\n" +
"<p>\n" +
"<b>To start up AdminUI</b></p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Log in to server as dcuser with SSH.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Execute <br /><tt class=\"LITERALMONO\">/eniq/sw/bin/webserver start</tt><br /> If successful, the message <tt class=\"output\">SMF enabling svc:/eniq/webserver</tt> is displayed.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Verify startup with <br /><tt class=\"LITERALMONO\">/eniq/sw/bin/webserver status</tt></li></ol>\n" +
"\n"+
"</div>\n";

var C8_8=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">8.8 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_4s2e\"></a><a name=\"CHAPTER8.8\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Shutting down ENIQ engine</a></span></h2>\n" +
"\n" +
"<p>\n" +
"<b>To shut down ENIQ engine</b></p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Log in to server as dcuser with SSH or in the case of\n" +
"a multi blade deployment execute the command on the engine blade. </li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Execute <br /><tt class=\"LITERALMONO\">/eniq/sw/bin/engine stop</tt><br /> If successful, the message <tt class=\"output\">SMF disabling svc:/eniq/engine</tt> is displayed.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Verify shutdown with<br /><tt class=\"LITERALMONO\">/eniq/sw/bin/engine status</tt></li></ol>\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>When shutting down ENIQ engine, SMF also shuts down the following\n" +
"ENIQ component:\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">ENIQ scheduler</li></ul></dd></dl><br />\n" +
"\n"+
"</div>\n";

var C8_9=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">8.9 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_rq0h\"></a><a name=\"CHAPTER8.9\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Starting up ENIQ engine</a></span></h2>\n" +
"\n" +
"<p>\n" +
"<b>To start up ENIQ engine</b></p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Log in to server as dcuser with SSH or in the case of\n" +
"a multi blade deployment execute the command on the engine blade..</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Execute <br /><tt class=\"LITERALMONO\">/eniq/sw/bin/engine start</tt><br /> If successful, the message <tt class=\"output\">SMF enabling svc:/eniq/engine</tt> is displayed.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Verify startup with <br /><tt class=\"LITERALMONO\">/eniq/sw/bin/engine status</tt></li></ol>\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>When starting up ENIQ engine, SMF also starts up the following\n" +
"ENIQ component, if it is offline:\n" +
"<ul>\n" +
"<li class=\"UNORD\" id=\"THSSTYLE0\">ENIQ scheduler</li></ul></dd></dl><br />\n" +
"\n"+
"</div>\n";

var C8_10=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">8.10 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_irq4\"></a><a name=\"CHAPTER8.10\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Shutting down ENIQ scheduler</a></span></h2>\n" +
"\n" +
"<p>\n" +
"<b>To shut down ENIQ scheduler</b></p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Log in to server as dcuser with SSH.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Execute <br /><tt class=\"LITERALMONO\">/eniq/sw/bin/scheduler stop</tt><br /> If successful, the message <tt class=\"output\">SMF disabling svc:/eniq/scheduler</tt> is displayed.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Verify shutdown with<br /><tt class=\"LITERALMONO\">/eniq/sw/bin/scheduler status</tt></li></ol>\n" +
"\n"+
"</div>\n";

var C8_11=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">8.11 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_863r\"></a><a name=\"CHAPTER8.11\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Starting ENIQ scheduler</a></span></h2>\n" +
"\n" +
"<p>\n" +
"<b>To start up ENIQ scheduler</b></p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Log in to server as dcuser with SSH.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Execute <br /><tt class=\"LITERALMONO\">/eniq/sw/bin/scheduler start</tt><br /> If successful, the message <tt class=\"output\">SMF enabling svc:/eniq/scheduler</tt> is displayed.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Verify startup with <br /><tt class=\"LITERALMONO\">/eniq/sw/bin/scheduler status</tt></li></ol>\n" +
"\n"+
"</div>\n";

var C8_12=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><a name=\"maintenance\"></a><span class=\"CHAPNUMBER\">8.12 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_sut8\"></a><a name=\"CHAPTER8.12\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Setting ENIQ\n" +
"to maintenance mode</a></span></h2>\n" +
"\n" +
"<p>\n" +
"<strong class=\"MEDEMPH\">To set maintenance mode:</strong></p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Log in to server as dcuser with SSH or in the case of\n" +
"a multi blade deployment execute the command on the engine blade. </li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Type on the command line: <br /><tt class=\"LITERALMONO\">/eniq/sw/bin/engine -e changeProfile NoLoads</tt></li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Verify maintenance mode with <br /><tt class=\"LITERALMONO\">/eniq/sw/bin/scheduler status</tt><dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd><b>Current Profile:</b> will indicate current state.</dd></dl><br /></li></ol>\n" +
"\n"+
"</div>\n";

var C8_13=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><a name=\"normal\"></a><span class=\"CHAPNUMBER\">8.13 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_j97v\"></a><a name=\"CHAPTER8.13\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Setting ENIQ to\n" +
"normal mode</a></span></h2>\n" +
"\n" +
"<p>\n" +
"<strong class=\"MEDEMPH\">To set normal mode:</strong></p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Log in to server as dcuser with SSH or in the case of\n" +
"a multi blade deployment execute the command on the engine blade..</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Type on the command line: <br /><tt class=\"LITERALMONO\">/eniq/sw/bin/engine -e changeProfile Normal</tt></li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Verify normal mode with <br /><tt class=\"LITERALMONO\">/eniq/sw/bin/scheduler status</tt><dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd><b>Current Profile:</b> will indicate current state.</dd></dl><br /></li></ol>\n" +
"\n"+
"</div>\n";

var C8_14=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><a name=\"lock\"></a><span class=\"CHAPNUMBER\">8.14 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_0dqc\"></a><a name=\"CHAPTER8.14\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Locking IQ users</a></span></h2>\n" +
"\n" +
"<p>\n" +
"<strong class=\"MEDEMPH\">To lock IQ users:</strong></p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Log in ENIQ database server or in the case of a multi\n" +
"blade deployment to dwh_reader_2 with your SQL client software. You\n" +
"need database administrator rights to lock users.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Create a login policy with the option locked=ON, with\n" +
"the command:<br /><tt class=\"LITERALMONO\">CREATE LOGIN POLICY locked_users locked=ON</tt></li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Assign the user 'dcbo' to the locked_users login policy:<br /><tt class=\"LITERALMONO\">ALTER USER dcbo LOGIN POLICY locked_users</tt></li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Assign the user 'dcpublic' to the locked_users login policy:<br /><tt class=\"LITERALMONO\">ALTER USER dcpublic LOGIN POLICY locked_users</tt></li></ol>\n" +
"\n"+
"</div>\n";

var C8_15=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><a name=\"unlock\"></a><span class=\"CHAPNUMBER\">8.15 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_rs4z\"></a><a name=\"CHAPTER8.15\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Unlocking IQ users</a></span></h2>\n" +
"\n" +
"<p>\n" +
"<strong class=\"MEDEMPH\">To unlock IQ users:</strong></p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Log in to ENIQ database server or in the case of a multi\n" +
"blade deployment the dwh_reader_2 with your SQL client software. You\n" +
"need database administrator rights to unlock users.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Create a login policy with the option locked=OFF, with\n" +
"the command:<br /><tt class=\"LITERALMONO\">CREATE LOGIN POLICY unlocked_users locked=OFF</tt></li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Assign the user 'dcbo' to the unlocked_users login policy:<br /> <tt class=\"LITERALMONO\">ALTER USER dcbo LOGIN POLICY unlocked_users</tt></li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Assign the user 'dcpublic' to the unlocked_users login\n" +
"policy:<br /><tt class=\"LITERALMONO\">ALTER USER dcpublic LOGIN POLICY unlocked_users</tt></li></ol>\n" +
"\n"+
"</div>\n";

var C8_16=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">8.16 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_54ps\"></a><a name=\"CHAPTER8.16\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Changing Alarm Properties</a></span></h2>\n" +
"\n" +
"<p>\n" +
"<strong class=\"MEDEMPH\">To Change Alarm Properties</strong></p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Log in to server as dcuser with SSH.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">In the following directory<br /><tt class=\"LITERALMONO\">/eniq/sw/bin/</tt><p>\n" +
"Execute the following command<br /><tt class=\"LITERALMONO\">./change_alarm_property.bsh showAlarmConnProperties</tt></p>\n" +
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
"Use command <br /><tt class=\"LITERALMONO\">./change_alarm_property.bsh -alarmconn </tt><br />\n" +
"<tt class=\"LITERALMONO\">&lt;PROPERTY_NAME&gt;&lt;PROPERTY_VALUE&gt;</tt></p>\n" +
"<p>\n" +
"to change parameters which are used to make connection to Webportal\n" +
"from ENIQ server, where &lt;PROPERTY_NAME&gt; is the name of the parameter\n" +
"and &lt;PROPERTY_VALUE&gt; is the value of the parameter.</p>\n" +
"</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">In the following directory<br /><tt class=\"LITERALMONO\">/eniq/sw/bin/</tt><p>\n" +
"Execute the following command<br /><tt class=\"LITERALMONO\">./change_alarm_property.bsh showAlarmParserProperties</tt></p>\n" +
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
"Use command <br /><tt class=\"LITERALMONO\">./change_alarm_property.bsh -alarmparser &lt;INTERFACE_NAME&gt; </tt><br />\n" +
"<tt class=\"LITERALMONO\">&lt;PROPERTY_NAME&gt;&lt;PROPERTY_VALUE&gt;</tt></p>\n" +
"<p>\n" +
"to change parameters which are used to run Alarm Interfaces with\n" +
"certain parameter values, where &lt;INTERFACE_NAME&gt; is name of interface\n" +
"to be run, &lt;PROPERTY_NAME&gt; is the name of the parameter and &lt;PROPERTY_VALUE&gt;\n" +
"is the value of the parameter.</p>\n" +
"</li></ol>\n" +
"\n"+
"</div>\n";

var C8_17=
"<div id=\"content\" class=\"body-content\">\n"+
"<h2><span class=\"CHAPNUMBER\">8.17 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_ov7v\"></a><a name=\"CHAPTER8.17\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Deactivation of Interface</a></span></h2>\n" +
"\n" +
"<p>\n" +
"The network element name SAEGW has been changed to CPG. As a result\n" +
"the INTF_DC_E_REDB_SAEGW interface needs to be removed and new interface\n" +
"INTF_DC_E_REDB_CPG should be created. As removing of the interface\n" +
"is not supported the following command needs to be executed to deactivate\n" +
"the SAEGW interface.</p>\n" +
"\n" +
"<p>\n" +
"To deactivate the SAEGW interface:</p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Login to eniq server with dcuser</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Goto to <tt class=\"LITERALMONO\">/eniq/sw/installer</tt></li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Execute the command ./deactivate_interface &ndash;o &lt;OSS&gt;\n" +
"-i INTF_DC_E_REDB_SAEGW.                                         \n" +
"                                                                 \n" +
"                                                        </li></ol>\n" +
"<p>\n" +
"This will deactivate the SAEGW interface from the particular OSS.</p>\n" +
"\n" +
"\n" +
"\n"+
"</div>\n";

