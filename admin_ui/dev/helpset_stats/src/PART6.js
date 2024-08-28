var C6=
"<div id=\"content\" class=\"body-content\">\n"+
"<h1><span class=\"CHAPNUMBER\">6 &nbsp; </span><span class=\"CHAPTITLE\"><a name=\"id_q298\"></a><a name=\"CHAPTER6\" href='javascript:parent.parent.sC2(\"TOP\")' class=\"CHAPLINK\">Start Day of the Week Configuration</a></span></h1>\n" +
"\n" +
"<p>\n" +
"ENIQ system supports any day of the week, which is set by operator\n" +
"to be the first day of the week. This means that week aggregations\n" +
"will be calculated according to new week calculation after updating\n" +
"first day of the week.</p>\n" +
"\n" +
"<p>\n" +
"Parameter value for first day of the week is set and in static\n" +
"property file and is read and used in the platform code.</p>\n" +
"\n" +
"<p>\n" +
"Do the following to change first day of the week and update ENIQ\n" +
"platform:</p>\n" +
"\n" +
"<p>\n" +
"Prerequisite:</p>\n" +
"\n" +
"<p>\n" +
"ZFS snapshots should be taken of ENIQ filesystems prior to any\n" +
"upgrade. All ENIQ services should be disabled so as to have the system\n" +
"in a consistent state prior to taking the snapshot. These snapshots\n" +
"can then be used to perform a rollback in the event of an error.</p>\n" +
"\n" +
"<p>\n" +
"For more details see <a href='javascript:parent.parent.parent.showAnchor(\"EniqUpgradeProc\")' class=\"xref\">Reference [8]</a>.</p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Log on to server as <tt class=\"file-path\">dcuser</tt> using\n" +
"SSH</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Type on the command-line <tt class=\"input\"><b>/eniq/sw/bin/startDayOfTheWeek.bsh</b></tt><dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>Wait until command executed successfully.</dd></dl><br /></li></ol>\n" +
"<p>\n" +
"This command asks you to enter first day of the week as following:\n" +
" </p>\n" +
"\n" +
"<p>\n" +
"<tt class=\"output\">Enter start day of the week 	(Monday, Tuesday, Wednesday,\n" +
"Thursday, Friday, Saturday, Sunday): </tt> Enter the day that\n" +
"you want to be first day of the week and hit return.</p>\n" +
"\n" +
"\n" +
"<ol class=\"STEPLIST\" start=\"3\"><li class=\"STEPLIST\" id=\"THSSTYLE2\">Log on to the adminUI for the ENIQ server.<p>\n" +
"<tt class=\"file-path\">http://<i class=\"var\">&lt;server_name&gt;</i>:8080/adminui</tt><br />User:	<tt class=\"file-path\">eniq</tt>  	<br />Pass:	<tt class=\"file-path\">eniq</tt> </p>\n" +
"</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Click <b>ETLC set scheduling</b> link on the\n" +
"left-hand menu</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Chose <b>Maintenance</b> from <b>Set type</b> select box</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">Chose <b>DWH_BASE</b> from <b>package select</b> box.</li><li class=\"STEPLIST\" id=\"THSSTYLE2\">To run set <b>Update_Dates</b>, click start\n" +
"of a set.</li></ol>\n" +
"<div class=\"ADMON\"><span class=\"ADMONMSG2\">Caution!</span><p class=\"ADMONDESC\">\n" +
"Be careful while giving first day of the week. This feature can\n" +
"be used only once.  If a customized value exists, it is necessary\n" +
"to rollback to a snapshot before the customization. If no snapshot\n" +
"exists the value cannot be changed.</p>\n" +
"</div>\n" +
"<p>\n" +
"To rollback to the point before start day of the week configuration\n" +
"see <a href='javascript:parent.parent.parent.showAnchor(\"EniqUpgradeProc\")' class=\"xref\">Reference [8]</a>.</p>\n" +
"\n" +
"<dl class=\"note\"><dt><b>Note: &nbsp;</b></dt><dd>To have the previous weeks based on the new start day of\n" +
"the week it is required to run manual reaggregation. It is recommended\n" +
"to run the reaggregation in batches to reduce performance degradation.</dd></dl><br />\n" +
"\n"+
"</div>\n";

